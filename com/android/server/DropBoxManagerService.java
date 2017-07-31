package com.android.server;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.Log;
import android.util.Slog;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.os.IDropBoxManagerService.Stub;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import net.oneplus.odm.insight.tracker.OSTracker;

public final class DropBoxManagerService
  extends SystemService
{
  private static final String BUNDLE_LOG = "log";
  private static final int DEFAULT_AGE_SECONDS = 259200;
  private static final int DEFAULT_MAX_FILES = 1000;
  private static final int DEFAULT_QUOTA_KB = 5120;
  private static final int DEFAULT_QUOTA_PERCENT = 10;
  private static final int DEFAULT_RESERVE_PERCENT = 10;
  private static final String MDM_ERROR_TAG = "MDM_DropBox";
  private static final int MSG_EARLY_LOG_RECORDING = 2;
  private static final int MSG_LOG_RECORDING = 1;
  private static final int MSG_SEND_BROADCAST = 1;
  private static final boolean PROFILE_DUMP = false;
  private static final int QUOTA_RESCAN_MILLIS = 5000;
  private static final String TAG = "DropBoxManagerService";
  private static final String[] TAG_VALUES = { "system_server_crash", "system_server_anr", "system_app_anr", "system_server_watchdog" };
  private FileList mAllFiles = null;
  private int mBlockSize = 0;
  private volatile boolean mBooted = false;
  private int mCachedQuotaBlocks = 0;
  private long mCachedQuotaUptimeMillis = 0L;
  private final ContentResolver mContentResolver;
  private final File mDropBoxDir;
  private HashMap<String, FileList> mFilesByTag = null;
  private final Handler mHandler;
  private final HashSet<String> mNeedRecordLogTagSet = new HashSet(Arrays.asList(TAG_VALUES));
  private OSTracker mOSTracker;
  private Object mObjEpitaphLock = new Object();
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      DropBoxManagerService.-set0(DropBoxManagerService.this, 0L);
      new Thread()
      {
        public void run()
        {
          try
          {
            DropBoxManagerService.-wrap1(DropBoxManagerService.this);
            DropBoxManagerService.-wrap0(DropBoxManagerService.this);
            return;
          }
          catch (IOException localIOException)
          {
            Slog.e("DropBoxManagerService", "Can't init", localIOException);
          }
        }
      }.start();
    }
  };
  private StatFs mStatFs = null;
  private final IDropBoxManagerService.Stub mStub = new IDropBoxManagerService.Stub()
  {
    public void add(DropBoxManager.Entry paramAnonymousEntry)
    {
      DropBoxManagerService.this.add(paramAnonymousEntry);
    }
    
    public void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      DropBoxManagerService.this.dump(paramAnonymousFileDescriptor, paramAnonymousPrintWriter, paramAnonymousArrayOfString);
    }
    
    public DropBoxManager.Entry getNextEntry(String paramAnonymousString, long paramAnonymousLong)
    {
      return DropBoxManagerService.this.getNextEntry(paramAnonymousString, paramAnonymousLong);
    }
    
    public boolean isTagEnabled(String paramAnonymousString)
    {
      return DropBoxManagerService.this.isTagEnabled(paramAnonymousString);
    }
  };
  private Handler mTrackerHandler;
  private HandlerThread mTrackerHandlerThread;
  
  public DropBoxManagerService(Context paramContext)
  {
    this(paramContext, new File("/data/system/dropbox"));
  }
  
  public DropBoxManagerService(Context paramContext, File paramFile)
  {
    super(paramContext);
    this.mDropBoxDir = paramFile;
    this.mContentResolver = getContext().getContentResolver();
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (paramAnonymousMessage.what == 1) {
          DropBoxManagerService.this.getContext().sendBroadcastAsUser((Intent)paramAnonymousMessage.obj, UserHandle.SYSTEM, "android.permission.READ_LOGS");
        }
      }
    };
    this.mTrackerHandlerThread = new HandlerThread("TrackerThread");
    this.mTrackerHandlerThread.start();
    this.mOSTracker = new OSTracker(paramContext);
  }
  
  private boolean checkRecordError(String paramString)
  {
    if (this.mNeedRecordLogTagSet.contains(paramString))
    {
      Log.v("MDM_DropBox", "record:" + paramString);
      return true;
    }
    return false;
  }
  
  private long createEntry(File paramFile, String paramString, int paramInt)
    throws IOException
  {
    label369:
    for (;;)
    {
      try
      {
        long l2 = System.currentTimeMillis();
        SortedSet localSortedSet = this.mAllFiles.contents.tailSet(new EntryFile(10000L + l2));
        EntryFile[] arrayOfEntryFile = null;
        if (!localSortedSet.isEmpty())
        {
          arrayOfEntryFile = (EntryFile[])localSortedSet.toArray(new EntryFile[localSortedSet.size()]);
          localSortedSet.clear();
        }
        long l1 = l2;
        if (!this.mAllFiles.contents.isEmpty()) {
          l1 = Math.max(l2, ((EntryFile)this.mAllFiles.contents.last()).timestampMillis + 1L);
        }
        l2 = l1;
        int i;
        if (arrayOfEntryFile != null)
        {
          int j = arrayOfEntryFile.length;
          i = 0;
          l2 = l1;
          if (i < j)
          {
            localSortedSet = arrayOfEntryFile[i];
            FileList localFileList = this.mAllFiles;
            localFileList.blocks -= localSortedSet.blocks;
            localFileList = (FileList)this.mFilesByTag.get(localSortedSet.tag);
            if ((localFileList != null) && (localFileList.contents.remove(localSortedSet))) {
              localFileList.blocks -= localSortedSet.blocks;
            }
            if ((localSortedSet.flags & 0x1) == 0)
            {
              enrollEntry(new EntryFile(localSortedSet.file, this.mDropBoxDir, localSortedSet.tag, l1, localSortedSet.flags, this.mBlockSize));
              l1 += 1L;
              break label369;
            }
            enrollEntry(new EntryFile(this.mDropBoxDir, localSortedSet.tag, l1));
            l1 += 1L;
            break label369;
          }
        }
        if (paramFile == null)
        {
          enrollEntry(new EntryFile(this.mDropBoxDir, paramString, l2));
          return l2;
        }
        enrollEntry(new EntryFile(paramFile, this.mDropBoxDir, paramString, l2, paramInt, this.mBlockSize));
        continue;
        i += 1;
      }
      finally {}
    }
  }
  
  private void enrollEntry(EntryFile paramEntryFile)
  {
    try
    {
      this.mAllFiles.contents.add(paramEntryFile);
      Object localObject = this.mAllFiles;
      ((FileList)localObject).blocks += paramEntryFile.blocks;
      if ((paramEntryFile.tag != null) && (paramEntryFile.file != null) && (paramEntryFile.blocks > 0))
      {
        FileList localFileList = (FileList)this.mFilesByTag.get(paramEntryFile.tag);
        localObject = localFileList;
        if (localFileList == null)
        {
          localObject = new FileList(null);
          this.mFilesByTag.put(paramEntryFile.tag, localObject);
        }
        ((FileList)localObject).contents.add(paramEntryFile);
        ((FileList)localObject).blocks += paramEntryFile.blocks;
      }
      return;
    }
    finally {}
  }
  
  private void init()
    throws IOException
  {
    try
    {
      if (this.mStatFs == null) {
        if (!this.mDropBoxDir.isDirectory())
        {
          boolean bool = this.mDropBoxDir.mkdirs();
          if (!bool) {
            break label119;
          }
        }
      }
      File[] arrayOfFile;
      throw new IOException("Can't mkdir: " + this.mDropBoxDir);
    }
    finally
    {
      try
      {
        this.mStatFs = new StatFs(this.mDropBoxDir.getPath());
        this.mBlockSize = this.mStatFs.getBlockSize();
        if (this.mAllFiles != null) {
          break label381;
        }
        arrayOfFile = this.mDropBoxDir.listFiles();
        if (arrayOfFile != null) {
          break label183;
        }
        throw new IOException("Can't list files: " + this.mDropBoxDir);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new IOException("Can't statfs: " + this.mDropBoxDir);
      }
      localObject1 = finally;
    }
    label119:
    label183:
    this.mAllFiles = new FileList(null);
    this.mFilesByTag = new HashMap();
    int i = 0;
    int j = localIllegalArgumentException.length;
    for (;;)
    {
      if (i < j)
      {
        Object localObject2 = localIllegalArgumentException[i];
        if (((File)localObject2).getName().endsWith(".tmp"))
        {
          Slog.i("DropBoxManagerService", "Cleaning temp file: " + localObject2);
          ((File)localObject2).delete();
        }
        else
        {
          EntryFile localEntryFile = new EntryFile((File)localObject2, this.mBlockSize);
          if (localEntryFile.tag == null)
          {
            Slog.w("DropBoxManagerService", "Unrecognized file: " + localObject2);
          }
          else if (localEntryFile.timestampMillis == 0L)
          {
            Slog.w("DropBoxManagerService", "Invalid filename: " + localObject2);
            ((File)localObject2).delete();
          }
          else
          {
            enrollEntry(localEntryFile);
          }
        }
      }
      else
      {
        label381:
        return;
      }
      i += 1;
    }
  }
  
  private void recordError(String paramString)
  {
    if (paramString.equals("system_server_watchdog"))
    {
      localMessage = this.mTrackerHandler.obtainMessage(2);
      localMessage.obj = paramString;
      localMessage.setData(new Bundle());
      this.mTrackerHandler.sendMessage(localMessage);
      return;
    }
    Message localMessage = this.mTrackerHandler.obtainMessage(1);
    localMessage.obj = paramString;
    localMessage.setData(new Bundle());
    this.mTrackerHandler.sendMessage(localMessage);
  }
  
  private long trimToFit()
    throws IOException
  {
    for (;;)
    {
      int i;
      int j;
      long l1;
      Object localObject1;
      int m;
      int k;
      Object localObject4;
      try
      {
        i = Settings.Global.getInt(this.mContentResolver, "dropbox_age_seconds", 259200);
        j = Settings.Global.getInt(this.mContentResolver, "dropbox_max_files", 1000);
        l1 = System.currentTimeMillis();
        long l2 = i * 1000;
        if (!this.mAllFiles.contents.isEmpty())
        {
          localObject1 = (EntryFile)this.mAllFiles.contents.first();
          if ((((EntryFile)localObject1).timestampMillis <= l1 - l2) || (this.mAllFiles.contents.size() >= j)) {}
        }
        else
        {
          l1 = SystemClock.uptimeMillis();
          if (l1 > this.mCachedQuotaUptimeMillis + 5000L)
          {
            i = Settings.Global.getInt(this.mContentResolver, "dropbox_quota_percent", 10);
            m = Settings.Global.getInt(this.mContentResolver, "dropbox_reserve_percent", 10);
            j = Settings.Global.getInt(this.mContentResolver, "dropbox_quota_kb", 5120);
            localObject1 = this.mDropBoxDir.getPath();
          }
        }
      }
      finally {}
      try
      {
        this.mStatFs.restat((String)localObject1);
        k = this.mStatFs.getAvailableBlocks();
        m = this.mStatFs.getBlockCount() * m / 100;
        this.mCachedQuotaBlocks = Math.min(j * 1024 / this.mBlockSize, Math.max(0, (k - m) * i / 100));
        this.mCachedQuotaUptimeMillis = l1;
        if (this.mAllFiles.blocks > this.mCachedQuotaBlocks)
        {
          j = this.mAllFiles.blocks;
          i = 0;
          localObject1 = new TreeSet(this.mFilesByTag.values());
          localObject3 = ((Iterable)localObject1).iterator();
          if (((Iterator)localObject3).hasNext())
          {
            localObject4 = (FileList)((Iterator)localObject3).next();
            if ((i <= 0) || (((FileList)localObject4).blocks > (this.mCachedQuotaBlocks - j) / i)) {
              break label549;
            }
          }
          i = (this.mCachedQuotaBlocks - j) / i;
          localObject1 = ((Iterable)localObject1).iterator();
          if (((Iterator)localObject1).hasNext())
          {
            localObject3 = (FileList)((Iterator)localObject1).next();
            if (this.mAllFiles.blocks >= this.mCachedQuotaBlocks) {
              break label685;
            }
          }
        }
        i = this.mCachedQuotaBlocks;
        j = this.mBlockSize;
        l1 = i * j;
        return l1;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new IOException("Can't restat: " + this.mDropBoxDir);
      }
      Object localObject3 = (FileList)this.mFilesByTag.get(((EntryFile)localObject1).tag);
      if ((localObject3 != null) && (((FileList)localObject3).contents.remove(localObject1))) {
        ((FileList)localObject3).blocks -= ((EntryFile)localObject1).blocks;
      }
      if (this.mAllFiles.contents.remove(localObject1))
      {
        localObject3 = this.mAllFiles;
        ((FileList)localObject3).blocks -= ((EntryFile)localObject1).blocks;
      }
      if (((EntryFile)localObject1).file != null)
      {
        ((EntryFile)localObject1).file.delete();
        continue;
        label549:
        j -= ((FileList)localObject4).blocks;
        i += 1;
        continue;
        label685:
        do
        {
          localObject4 = (EntryFile)((FileList)localObject3).contents.first();
          if (((FileList)localObject3).contents.remove(localObject4)) {
            ((FileList)localObject3).blocks -= ((EntryFile)localObject4).blocks;
          }
          if (this.mAllFiles.contents.remove(localObject4))
          {
            FileList localFileList = this.mAllFiles;
            localFileList.blocks -= ((EntryFile)localObject4).blocks;
          }
          try
          {
            if (((EntryFile)localObject4).file != null) {
              ((EntryFile)localObject4).file.delete();
            }
            enrollEntry(new EntryFile(this.mDropBoxDir, ((EntryFile)localObject4).tag, ((EntryFile)localObject4).timestampMillis));
          }
          catch (IOException localIOException)
          {
            for (;;)
            {
              Slog.e("DropBoxManagerService", "Can't write tombstone file", localIOException);
            }
          }
          if (((FileList)localObject3).blocks <= i) {
            break;
          }
        } while (!((FileList)localObject3).contents.isEmpty());
      }
    }
  }
  
  /* Error */
  public void add(DropBoxManager.Entry paramEntry)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 27
    //   3: aconst_null
    //   4: astore 18
    //   6: aconst_null
    //   7: astore 29
    //   9: aconst_null
    //   10: astore 20
    //   12: aconst_null
    //   13: astore 16
    //   15: aconst_null
    //   16: astore 28
    //   18: aconst_null
    //   19: astore 25
    //   21: aconst_null
    //   22: astore 26
    //   24: aload_1
    //   25: invokevirtual 492	android/os/DropBoxManager$Entry:getTag	()Ljava/lang/String;
    //   28: astore 30
    //   30: ldc_w 494
    //   33: iconst_0
    //   34: invokestatic 500	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   37: istore 6
    //   39: aload 20
    //   41: astore 23
    //   43: aload 26
    //   45: astore 24
    //   47: aload 18
    //   49: astore 22
    //   51: aload 29
    //   53: astore 19
    //   55: aload 28
    //   57: astore 21
    //   59: aload 27
    //   61: astore 17
    //   63: aload_1
    //   64: invokevirtual 503	android/os/DropBoxManager$Entry:getFlags	()I
    //   67: istore 5
    //   69: iload 5
    //   71: iconst_1
    //   72: iand
    //   73: ifeq +112 -> 185
    //   76: aload 20
    //   78: astore 23
    //   80: aload 26
    //   82: astore 24
    //   84: aload 18
    //   86: astore 22
    //   88: aload 29
    //   90: astore 19
    //   92: aload 28
    //   94: astore 21
    //   96: aload 27
    //   98: astore 17
    //   100: new 331	java/lang/IllegalArgumentException
    //   103: dup
    //   104: invokespecial 504	java/lang/IllegalArgumentException:<init>	()V
    //   107: athrow
    //   108: astore 18
    //   110: aload 24
    //   112: astore 16
    //   114: aload 23
    //   116: astore 20
    //   118: aload 20
    //   120: astore 19
    //   122: aload 16
    //   124: astore 21
    //   126: aload 22
    //   128: astore 17
    //   130: ldc 52
    //   132: new 212	java/lang/StringBuilder
    //   135: dup
    //   136: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   139: ldc_w 506
    //   142: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: aload 30
    //   147: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: aload 18
    //   155: invokestatic 486	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   158: pop
    //   159: aload 16
    //   161: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   164: aload 20
    //   166: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   169: aload_1
    //   170: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   173: aload 22
    //   175: ifnull +9 -> 184
    //   178: aload 22
    //   180: invokevirtual 379	java/io/File:delete	()Z
    //   183: pop
    //   184: return
    //   185: aload 20
    //   187: astore 23
    //   189: aload 26
    //   191: astore 24
    //   193: aload 18
    //   195: astore 22
    //   197: aload 29
    //   199: astore 19
    //   201: aload 28
    //   203: astore 21
    //   205: aload 27
    //   207: astore 17
    //   209: aload_0
    //   210: invokespecial 111	com/android/server/DropBoxManagerService:init	()V
    //   213: aload 20
    //   215: astore 23
    //   217: aload 26
    //   219: astore 24
    //   221: aload 18
    //   223: astore 22
    //   225: aload 29
    //   227: astore 19
    //   229: aload 28
    //   231: astore 21
    //   233: aload 27
    //   235: astore 17
    //   237: aload_0
    //   238: aload 30
    //   240: invokevirtual 518	com/android/server/DropBoxManagerService:isTagEnabled	(Ljava/lang/String;)Z
    //   243: istore 7
    //   245: iload 7
    //   247: ifne +16 -> 263
    //   250: aconst_null
    //   251: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   254: aconst_null
    //   255: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   258: aload_1
    //   259: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   262: return
    //   263: aload 20
    //   265: astore 23
    //   267: aload 26
    //   269: astore 24
    //   271: aload 18
    //   273: astore 22
    //   275: aload 29
    //   277: astore 19
    //   279: aload 28
    //   281: astore 21
    //   283: aload 27
    //   285: astore 17
    //   287: aload_0
    //   288: invokespecial 105	com/android/server/DropBoxManagerService:trimToFit	()J
    //   291: lstore 10
    //   293: aload 20
    //   295: astore 23
    //   297: aload 26
    //   299: astore 24
    //   301: aload 18
    //   303: astore 22
    //   305: aload 29
    //   307: astore 19
    //   309: aload 28
    //   311: astore 21
    //   313: aload 27
    //   315: astore 17
    //   317: invokestatic 238	java/lang/System:currentTimeMillis	()J
    //   320: lstore 8
    //   322: aload 20
    //   324: astore 23
    //   326: aload 26
    //   328: astore 24
    //   330: aload 18
    //   332: astore 22
    //   334: aload 29
    //   336: astore 19
    //   338: aload 28
    //   340: astore 21
    //   342: aload 27
    //   344: astore 17
    //   346: aload_0
    //   347: getfield 146	com/android/server/DropBoxManagerService:mBlockSize	I
    //   350: newarray <illegal type>
    //   352: astore 31
    //   354: aload 20
    //   356: astore 23
    //   358: aload 26
    //   360: astore 24
    //   362: aload 18
    //   364: astore 22
    //   366: aload 29
    //   368: astore 19
    //   370: aload 28
    //   372: astore 21
    //   374: aload 27
    //   376: astore 17
    //   378: aload_1
    //   379: invokevirtual 522	android/os/DropBoxManager$Entry:getInputStream	()Ljava/io/InputStream;
    //   382: astore 20
    //   384: iconst_0
    //   385: istore_2
    //   386: aload 20
    //   388: astore 23
    //   390: aload 26
    //   392: astore 24
    //   394: aload 18
    //   396: astore 22
    //   398: aload 20
    //   400: astore 19
    //   402: aload 28
    //   404: astore 21
    //   406: aload 27
    //   408: astore 17
    //   410: iload_2
    //   411: aload 31
    //   413: arraylength
    //   414: if_icmpge +45 -> 459
    //   417: aload 20
    //   419: astore 23
    //   421: aload 26
    //   423: astore 24
    //   425: aload 18
    //   427: astore 22
    //   429: aload 20
    //   431: astore 19
    //   433: aload 28
    //   435: astore 21
    //   437: aload 27
    //   439: astore 17
    //   441: aload 20
    //   443: aload 31
    //   445: iload_2
    //   446: aload 31
    //   448: arraylength
    //   449: iload_2
    //   450: isub
    //   451: invokevirtual 528	java/io/InputStream:read	([BII)I
    //   454: istore_3
    //   455: iload_3
    //   456: ifgt +1831 -> 2287
    //   459: aload 20
    //   461: astore 23
    //   463: aload 26
    //   465: astore 24
    //   467: aload 18
    //   469: astore 22
    //   471: aload 20
    //   473: astore 19
    //   475: aload 28
    //   477: astore 21
    //   479: aload 27
    //   481: astore 17
    //   483: new 128	java/io/File
    //   486: dup
    //   487: aload_0
    //   488: getfield 176	com/android/server/DropBoxManagerService:mDropBoxDir	Ljava/io/File;
    //   491: new 212	java/lang/StringBuilder
    //   494: dup
    //   495: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   498: ldc_w 530
    //   501: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   504: invokestatic 536	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   507: invokevirtual 539	java/lang/Thread:getId	()J
    //   510: invokevirtual 542	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   513: ldc_w 366
    //   516: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   519: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   522: invokespecial 545	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   525: astore 18
    //   527: aload 16
    //   529: astore 17
    //   531: aload 25
    //   533: astore 19
    //   535: aload_0
    //   536: getfield 146	com/android/server/DropBoxManagerService:mBlockSize	I
    //   539: istore 4
    //   541: iload 4
    //   543: istore_3
    //   544: iload 4
    //   546: sipush 4096
    //   549: if_icmple +2113 -> 2662
    //   552: sipush 4096
    //   555: istore_3
    //   556: goto +2106 -> 2662
    //   559: aload 16
    //   561: astore 17
    //   563: aload 25
    //   565: astore 19
    //   567: new 547	java/io/FileOutputStream
    //   570: dup
    //   571: aload 18
    //   573: invokespecial 550	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   576: astore 22
    //   578: aload 16
    //   580: astore 17
    //   582: aload 25
    //   584: astore 19
    //   586: new 552	java/io/BufferedOutputStream
    //   589: dup
    //   590: aload 22
    //   592: iload 4
    //   594: invokespecial 555	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   597: astore 16
    //   599: iload_2
    //   600: aload 31
    //   602: arraylength
    //   603: if_icmpne +1691 -> 2294
    //   606: iload 5
    //   608: iconst_4
    //   609: iand
    //   610: ifne +1684 -> 2294
    //   613: new 557	java/util/zip/GZIPOutputStream
    //   616: dup
    //   617: aload 16
    //   619: invokespecial 560	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   622: astore 17
    //   624: iload 5
    //   626: iconst_4
    //   627: ior
    //   628: istore_3
    //   629: aload 17
    //   631: astore 16
    //   633: iload 6
    //   635: ifne +73 -> 708
    //   638: aload 16
    //   640: astore 17
    //   642: aload 16
    //   644: astore 19
    //   646: aload 30
    //   648: ldc_w 562
    //   651: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   654: ifeq +54 -> 708
    //   657: aload 16
    //   659: astore 17
    //   661: aload 16
    //   663: astore 19
    //   665: ldc_w 564
    //   668: invokestatic 570	com/oem/debug/OemManager:writeRawPartition	(Ljava/lang/String;)I
    //   671: istore 4
    //   673: aload 16
    //   675: astore 17
    //   677: aload 16
    //   679: astore 19
    //   681: ldc 52
    //   683: new 212	java/lang/StringBuilder
    //   686: dup
    //   687: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   690: ldc_w 572
    //   693: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   696: iload 4
    //   698: invokevirtual 575	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   701: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   704: invokestatic 578	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   707: pop
    //   708: aload 16
    //   710: astore 17
    //   712: aload 16
    //   714: astore 19
    //   716: aload_0
    //   717: aload 30
    //   719: invokespecial 580	com/android/server/DropBoxManagerService:checkRecordError	(Ljava/lang/String;)Z
    //   722: istore 7
    //   724: aload 16
    //   726: astore 21
    //   728: aload 21
    //   730: astore 17
    //   732: aload 21
    //   734: astore 19
    //   736: aload 21
    //   738: aload 31
    //   740: iconst_0
    //   741: iload_2
    //   742: invokevirtual 586	java/io/OutputStream:write	([BII)V
    //   745: iload 6
    //   747: ifne +78 -> 825
    //   750: aload 21
    //   752: astore 17
    //   754: aload 21
    //   756: astore 19
    //   758: aload 30
    //   760: ldc_w 562
    //   763: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   766: ifeq +59 -> 825
    //   769: aload 21
    //   771: astore 17
    //   773: aload 21
    //   775: astore 19
    //   777: aload 31
    //   779: iconst_0
    //   780: iload_2
    //   781: ldc_w 588
    //   784: invokestatic 594	org/apache/http/util/EncodingUtils:getString	([BIILjava/lang/String;)Ljava/lang/String;
    //   787: invokestatic 570	com/oem/debug/OemManager:writeRawPartition	(Ljava/lang/String;)I
    //   790: istore_2
    //   791: aload 21
    //   793: astore 17
    //   795: aload 21
    //   797: astore 19
    //   799: ldc 52
    //   801: new 212	java/lang/StringBuilder
    //   804: dup
    //   805: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   808: ldc_w 596
    //   811: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   814: iload_2
    //   815: invokevirtual 575	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   818: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   821: invokestatic 578	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   824: pop
    //   825: aload 21
    //   827: astore 17
    //   829: aload 21
    //   831: astore 19
    //   833: invokestatic 238	java/lang/System:currentTimeMillis	()J
    //   836: lstore 14
    //   838: lload 8
    //   840: lstore 12
    //   842: lload 14
    //   844: lload 8
    //   846: lsub
    //   847: ldc2_w 597
    //   850: lcmp
    //   851: ifle +21 -> 872
    //   854: aload 21
    //   856: astore 17
    //   858: aload 21
    //   860: astore 19
    //   862: aload_0
    //   863: invokespecial 105	com/android/server/DropBoxManagerService:trimToFit	()J
    //   866: lstore 10
    //   868: lload 14
    //   870: lstore 12
    //   872: aload 21
    //   874: astore 17
    //   876: aload 21
    //   878: astore 19
    //   880: aload 20
    //   882: aload 31
    //   884: invokevirtual 601	java/io/InputStream:read	([B)I
    //   887: istore 4
    //   889: iload 4
    //   891: ifgt +1409 -> 2300
    //   894: aload 21
    //   896: astore 17
    //   898: aload 21
    //   900: astore 19
    //   902: aload 22
    //   904: invokestatic 607	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   907: pop
    //   908: aload 21
    //   910: astore 17
    //   912: aload 21
    //   914: astore 19
    //   916: aload 21
    //   918: invokevirtual 608	java/io/OutputStream:close	()V
    //   921: aconst_null
    //   922: astore 16
    //   924: aload 16
    //   926: astore 17
    //   928: aload 16
    //   930: astore 19
    //   932: aload 18
    //   934: invokevirtual 611	java/io/File:length	()J
    //   937: lload 10
    //   939: lcmp
    //   940: ifle +1397 -> 2337
    //   943: aload 16
    //   945: astore 17
    //   947: aload 16
    //   949: astore 19
    //   951: ldc 52
    //   953: new 212	java/lang/StringBuilder
    //   956: dup
    //   957: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   960: ldc_w 613
    //   963: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   966: aload 30
    //   968: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   971: ldc_w 615
    //   974: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   977: aload 18
    //   979: invokevirtual 611	java/io/File:length	()J
    //   982: invokevirtual 542	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   985: ldc_w 617
    //   988: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   991: lload 10
    //   993: invokevirtual 542	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   996: ldc_w 619
    //   999: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1002: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1005: invokestatic 387	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1008: pop
    //   1009: aload 16
    //   1011: astore 17
    //   1013: aload 16
    //   1015: astore 19
    //   1017: aload 18
    //   1019: invokevirtual 379	java/io/File:delete	()Z
    //   1022: pop
    //   1023: aconst_null
    //   1024: astore 18
    //   1026: iload 7
    //   1028: ifeq +33 -> 1061
    //   1031: aload 20
    //   1033: astore 23
    //   1035: aload 16
    //   1037: astore 24
    //   1039: aload 18
    //   1041: astore 22
    //   1043: aload 20
    //   1045: astore 19
    //   1047: aload 16
    //   1049: astore 21
    //   1051: aload 18
    //   1053: astore 17
    //   1055: aload_0
    //   1056: aload 30
    //   1058: invokespecial 621	com/android/server/DropBoxManagerService:recordError	(Ljava/lang/String;)V
    //   1061: iload 6
    //   1063: ifne +119 -> 1182
    //   1066: aload 20
    //   1068: astore 23
    //   1070: aload 16
    //   1072: astore 24
    //   1074: aload 18
    //   1076: astore 22
    //   1078: aload 20
    //   1080: astore 19
    //   1082: aload 16
    //   1084: astore 21
    //   1086: aload 18
    //   1088: astore 17
    //   1090: aload 30
    //   1092: ldc_w 562
    //   1095: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1098: ifeq +84 -> 1182
    //   1101: aload 20
    //   1103: astore 23
    //   1105: aload 16
    //   1107: astore 24
    //   1109: aload 18
    //   1111: astore 22
    //   1113: aload 20
    //   1115: astore 19
    //   1117: aload 16
    //   1119: astore 21
    //   1121: aload 18
    //   1123: astore 17
    //   1125: ldc_w 623
    //   1128: invokestatic 570	com/oem/debug/OemManager:writeRawPartition	(Ljava/lang/String;)I
    //   1131: istore_2
    //   1132: aload 20
    //   1134: astore 23
    //   1136: aload 16
    //   1138: astore 24
    //   1140: aload 18
    //   1142: astore 22
    //   1144: aload 20
    //   1146: astore 19
    //   1148: aload 16
    //   1150: astore 21
    //   1152: aload 18
    //   1154: astore 17
    //   1156: ldc 52
    //   1158: new 212	java/lang/StringBuilder
    //   1161: dup
    //   1162: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   1165: ldc_w 625
    //   1168: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1171: iload_2
    //   1172: invokevirtual 575	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1175: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1178: invokestatic 578	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1181: pop
    //   1182: aload 20
    //   1184: astore 23
    //   1186: aload 16
    //   1188: astore 24
    //   1190: aload 18
    //   1192: astore 22
    //   1194: aload 20
    //   1196: astore 19
    //   1198: aload 16
    //   1200: astore 21
    //   1202: aload 18
    //   1204: astore 17
    //   1206: aload_0
    //   1207: aload 18
    //   1209: aload 30
    //   1211: iload_3
    //   1212: invokespecial 627	com/android/server/DropBoxManagerService:createEntry	(Ljava/io/File;Ljava/lang/String;I)J
    //   1215: lstore 8
    //   1217: aconst_null
    //   1218: astore 27
    //   1220: aconst_null
    //   1221: astore 26
    //   1223: aload 20
    //   1225: astore 23
    //   1227: aload 16
    //   1229: astore 24
    //   1231: aload 26
    //   1233: astore 22
    //   1235: aload 20
    //   1237: astore 19
    //   1239: aload 16
    //   1241: astore 21
    //   1243: aload 27
    //   1245: astore 17
    //   1247: new 629	android/content/Intent
    //   1250: dup
    //   1251: ldc_w 631
    //   1254: invokespecial 632	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   1257: astore 18
    //   1259: aload 20
    //   1261: astore 23
    //   1263: aload 16
    //   1265: astore 24
    //   1267: aload 26
    //   1269: astore 22
    //   1271: aload 20
    //   1273: astore 19
    //   1275: aload 16
    //   1277: astore 21
    //   1279: aload 27
    //   1281: astore 17
    //   1283: aload 18
    //   1285: ldc_w 633
    //   1288: aload 30
    //   1290: invokevirtual 637	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   1293: pop
    //   1294: aload 20
    //   1296: astore 23
    //   1298: aload 16
    //   1300: astore 24
    //   1302: aload 26
    //   1304: astore 22
    //   1306: aload 20
    //   1308: astore 19
    //   1310: aload 16
    //   1312: astore 21
    //   1314: aload 27
    //   1316: astore 17
    //   1318: aload 18
    //   1320: ldc_w 639
    //   1323: lload 8
    //   1325: invokevirtual 642	android/content/Intent:putExtra	(Ljava/lang/String;J)Landroid/content/Intent;
    //   1328: pop
    //   1329: aload 20
    //   1331: astore 23
    //   1333: aload 16
    //   1335: astore 24
    //   1337: aload 26
    //   1339: astore 22
    //   1341: aload 20
    //   1343: astore 19
    //   1345: aload 16
    //   1347: astore 21
    //   1349: aload 27
    //   1351: astore 17
    //   1353: aload_0
    //   1354: getfield 150	com/android/server/DropBoxManagerService:mBooted	Z
    //   1357: ifne +36 -> 1393
    //   1360: aload 20
    //   1362: astore 23
    //   1364: aload 16
    //   1366: astore 24
    //   1368: aload 26
    //   1370: astore 22
    //   1372: aload 20
    //   1374: astore 19
    //   1376: aload 16
    //   1378: astore 21
    //   1380: aload 27
    //   1382: astore 17
    //   1384: aload 18
    //   1386: ldc_w 643
    //   1389: invokevirtual 647	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   1392: pop
    //   1393: aload 20
    //   1395: astore 23
    //   1397: aload 16
    //   1399: astore 24
    //   1401: aload 26
    //   1403: astore 22
    //   1405: aload 20
    //   1407: astore 19
    //   1409: aload 16
    //   1411: astore 21
    //   1413: aload 27
    //   1415: astore 17
    //   1417: aload_0
    //   1418: getfield 191	com/android/server/DropBoxManagerService:mHandler	Landroid/os/Handler;
    //   1421: aload_0
    //   1422: getfield 191	com/android/server/DropBoxManagerService:mHandler	Landroid/os/Handler;
    //   1425: iconst_1
    //   1426: aload 18
    //   1428: invokevirtual 650	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   1431: invokevirtual 417	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
    //   1434: pop
    //   1435: aload 20
    //   1437: astore 23
    //   1439: aload 16
    //   1441: astore 24
    //   1443: aload 26
    //   1445: astore 22
    //   1447: aload 20
    //   1449: astore 19
    //   1451: aload 16
    //   1453: astore 21
    //   1455: aload 27
    //   1457: astore 17
    //   1459: new 128	java/io/File
    //   1462: dup
    //   1463: ldc -126
    //   1465: invokespecial 133	java/io/File:<init>	(Ljava/lang/String;)V
    //   1468: invokevirtual 350	java/io/File:listFiles	()[Ljava/io/File;
    //   1471: astore 28
    //   1473: iconst_0
    //   1474: istore_2
    //   1475: aload 28
    //   1477: ifnull +1127 -> 2604
    //   1480: aload 20
    //   1482: astore 23
    //   1484: aload 16
    //   1486: astore 24
    //   1488: aload 26
    //   1490: astore 22
    //   1492: aload 20
    //   1494: astore 19
    //   1496: aload 16
    //   1498: astore 21
    //   1500: aload 27
    //   1502: astore 17
    //   1504: iload_2
    //   1505: aload 28
    //   1507: arraylength
    //   1508: if_icmpge +1096 -> 2604
    //   1511: aload 20
    //   1513: astore 23
    //   1515: aload 16
    //   1517: astore 24
    //   1519: aload 26
    //   1521: astore 22
    //   1523: aload 20
    //   1525: astore 19
    //   1527: aload 16
    //   1529: astore 21
    //   1531: aload 27
    //   1533: astore 17
    //   1535: aload 28
    //   1537: iload_2
    //   1538: aaload
    //   1539: invokevirtual 364	java/io/File:getName	()Ljava/lang/String;
    //   1542: astore 18
    //   1544: aload 20
    //   1546: astore 23
    //   1548: aload 16
    //   1550: astore 24
    //   1552: aload 26
    //   1554: astore 22
    //   1556: aload 18
    //   1558: astore 25
    //   1560: aload 20
    //   1562: astore 19
    //   1564: aload 16
    //   1566: astore 21
    //   1568: aload 27
    //   1570: astore 17
    //   1572: aload 18
    //   1574: ldc_w 652
    //   1577: invokevirtual 369	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   1580: ifeq +42 -> 1622
    //   1583: aload 20
    //   1585: astore 23
    //   1587: aload 16
    //   1589: astore 24
    //   1591: aload 26
    //   1593: astore 22
    //   1595: aload 20
    //   1597: astore 19
    //   1599: aload 16
    //   1601: astore 21
    //   1603: aload 27
    //   1605: astore 17
    //   1607: aload 18
    //   1609: iconst_0
    //   1610: aload 18
    //   1612: invokevirtual 654	java/lang/String:length	()I
    //   1615: iconst_3
    //   1616: isub
    //   1617: invokevirtual 658	java/lang/String:substring	(II)Ljava/lang/String;
    //   1620: astore 25
    //   1622: aload 20
    //   1624: astore 23
    //   1626: aload 16
    //   1628: astore 24
    //   1630: aload 26
    //   1632: astore 22
    //   1634: aload 20
    //   1636: astore 19
    //   1638: aload 16
    //   1640: astore 21
    //   1642: aload 27
    //   1644: astore 17
    //   1646: aload 25
    //   1648: ldc_w 660
    //   1651: invokevirtual 369	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   1654: ifeq +702 -> 2356
    //   1657: aload 20
    //   1659: astore 23
    //   1661: aload 16
    //   1663: astore 24
    //   1665: aload 26
    //   1667: astore 22
    //   1669: aload 20
    //   1671: astore 19
    //   1673: aload 16
    //   1675: astore 21
    //   1677: aload 27
    //   1679: astore 17
    //   1681: aload 25
    //   1683: iconst_0
    //   1684: aload 25
    //   1686: invokevirtual 654	java/lang/String:length	()I
    //   1689: iconst_5
    //   1690: isub
    //   1691: invokevirtual 658	java/lang/String:substring	(II)Ljava/lang/String;
    //   1694: astore 18
    //   1696: aload 20
    //   1698: astore 23
    //   1700: aload 16
    //   1702: astore 24
    //   1704: aload 26
    //   1706: astore 22
    //   1708: aload 20
    //   1710: astore 19
    //   1712: aload 16
    //   1714: astore 21
    //   1716: aload 27
    //   1718: astore 17
    //   1720: new 662	java/text/SimpleDateFormat
    //   1723: dup
    //   1724: ldc_w 664
    //   1727: invokespecial 665	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
    //   1730: new 667	java/util/Date
    //   1733: dup
    //   1734: lload 8
    //   1736: invokespecial 668	java/util/Date:<init>	(J)V
    //   1739: invokevirtual 672	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   1742: astore 25
    //   1744: aload 20
    //   1746: astore 23
    //   1748: aload 16
    //   1750: astore 24
    //   1752: aload 26
    //   1754: astore 22
    //   1756: aload 20
    //   1758: astore 19
    //   1760: aload 16
    //   1762: astore 21
    //   1764: aload 27
    //   1766: astore 17
    //   1768: aload 18
    //   1770: new 212	java/lang/StringBuilder
    //   1773: dup
    //   1774: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   1777: aload 30
    //   1779: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1782: ldc_w 674
    //   1785: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1788: aload 25
    //   1790: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1793: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1796: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1799: ifeq +481 -> 2280
    //   1802: aload 20
    //   1804: astore 23
    //   1806: aload 16
    //   1808: astore 24
    //   1810: aload 26
    //   1812: astore 22
    //   1814: aload 20
    //   1816: astore 19
    //   1818: aload 16
    //   1820: astore 21
    //   1822: aload 27
    //   1824: astore 17
    //   1826: new 676	java/util/ArrayList
    //   1829: dup
    //   1830: invokespecial 677	java/util/ArrayList:<init>	()V
    //   1833: new 212	java/lang/StringBuilder
    //   1836: dup
    //   1837: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   1840: ldc_w 679
    //   1843: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1846: aload 28
    //   1848: iload_2
    //   1849: aaload
    //   1850: invokevirtual 364	java/io/File:getName	()Ljava/lang/String;
    //   1853: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1856: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1859: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1862: pop
    //   1863: aload 20
    //   1865: astore 23
    //   1867: aload 16
    //   1869: astore 24
    //   1871: aload 26
    //   1873: astore 22
    //   1875: aload 20
    //   1877: astore 19
    //   1879: aload 16
    //   1881: astore 21
    //   1883: aload 27
    //   1885: astore 17
    //   1887: ldc 52
    //   1889: new 212	java/lang/StringBuilder
    //   1892: dup
    //   1893: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   1896: ldc_w 682
    //   1899: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1902: aload 28
    //   1904: iload_2
    //   1905: aaload
    //   1906: invokevirtual 364	java/io/File:getName	()Ljava/lang/String;
    //   1909: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1912: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1915: invokestatic 578	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1918: pop
    //   1919: iload 6
    //   1921: ifeq +359 -> 2280
    //   1924: aload 20
    //   1926: astore 23
    //   1928: aload 16
    //   1930: astore 24
    //   1932: aload 26
    //   1934: astore 22
    //   1936: aload 20
    //   1938: astore 19
    //   1940: aload 16
    //   1942: astore 21
    //   1944: aload 27
    //   1946: astore 17
    //   1948: aload 30
    //   1950: ldc_w 684
    //   1953: invokevirtual 687	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   1956: ifne +39 -> 1995
    //   1959: aload 20
    //   1961: astore 23
    //   1963: aload 16
    //   1965: astore 24
    //   1967: aload 26
    //   1969: astore 22
    //   1971: aload 20
    //   1973: astore 19
    //   1975: aload 16
    //   1977: astore 21
    //   1979: aload 27
    //   1981: astore 17
    //   1983: aload 30
    //   1985: ldc_w 689
    //   1988: iconst_0
    //   1989: invokevirtual 693	java/lang/String:startsWith	(Ljava/lang/String;I)Z
    //   1992: ifne +142 -> 2134
    //   1995: aload 20
    //   1997: astore 23
    //   1999: aload 16
    //   2001: astore 24
    //   2003: aload 26
    //   2005: astore 22
    //   2007: aload 20
    //   2009: astore 19
    //   2011: aload 16
    //   2013: astore 21
    //   2015: aload 27
    //   2017: astore 17
    //   2019: aload 30
    //   2021: ldc_w 695
    //   2024: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2027: ifne +107 -> 2134
    //   2030: aload 20
    //   2032: astore 23
    //   2034: aload 16
    //   2036: astore 24
    //   2038: aload 26
    //   2040: astore 22
    //   2042: aload 20
    //   2044: astore 19
    //   2046: aload 16
    //   2048: astore 21
    //   2050: aload 27
    //   2052: astore 17
    //   2054: aload 30
    //   2056: ldc 120
    //   2058: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2061: ifne +73 -> 2134
    //   2064: aload 20
    //   2066: astore 23
    //   2068: aload 16
    //   2070: astore 24
    //   2072: aload 26
    //   2074: astore 22
    //   2076: aload 20
    //   2078: astore 19
    //   2080: aload 16
    //   2082: astore 21
    //   2084: aload 27
    //   2086: astore 17
    //   2088: aload 30
    //   2090: ldc_w 697
    //   2093: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2096: ifne +38 -> 2134
    //   2099: aload 20
    //   2101: astore 23
    //   2103: aload 16
    //   2105: astore 24
    //   2107: aload 26
    //   2109: astore 22
    //   2111: aload 20
    //   2113: astore 19
    //   2115: aload 16
    //   2117: astore 21
    //   2119: aload 27
    //   2121: astore 17
    //   2123: aload 30
    //   2125: ldc_w 699
    //   2128: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2131: ifeq +98 -> 2229
    //   2134: aload 20
    //   2136: astore 23
    //   2138: aload 16
    //   2140: astore 24
    //   2142: aload 26
    //   2144: astore 22
    //   2146: aload 20
    //   2148: astore 19
    //   2150: aload 16
    //   2152: astore 21
    //   2154: aload 27
    //   2156: astore 17
    //   2158: aload_0
    //   2159: getfield 169	com/android/server/DropBoxManagerService:mObjEpitaphLock	Ljava/lang/Object;
    //   2162: astore 18
    //   2164: aload 20
    //   2166: astore 23
    //   2168: aload 16
    //   2170: astore 24
    //   2172: aload 26
    //   2174: astore 22
    //   2176: aload 20
    //   2178: astore 19
    //   2180: aload 16
    //   2182: astore 21
    //   2184: aload 27
    //   2186: astore 17
    //   2188: aload 18
    //   2190: monitorenter
    //   2191: aload 28
    //   2193: iload_2
    //   2194: aaload
    //   2195: aload 30
    //   2197: iload_3
    //   2198: invokestatic 705	com/oem/debug/ASSERT:epitaph	(Ljava/io/File;Ljava/lang/String;I)Z
    //   2201: pop
    //   2202: aload 20
    //   2204: astore 23
    //   2206: aload 16
    //   2208: astore 24
    //   2210: aload 26
    //   2212: astore 22
    //   2214: aload 20
    //   2216: astore 19
    //   2218: aload 16
    //   2220: astore 21
    //   2222: aload 27
    //   2224: astore 17
    //   2226: aload 18
    //   2228: monitorexit
    //   2229: aload 20
    //   2231: astore 23
    //   2233: aload 16
    //   2235: astore 24
    //   2237: aload 26
    //   2239: astore 22
    //   2241: aload 20
    //   2243: astore 19
    //   2245: aload 16
    //   2247: astore 21
    //   2249: aload 27
    //   2251: astore 17
    //   2253: ldc 52
    //   2255: new 212	java/lang/StringBuilder
    //   2258: dup
    //   2259: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   2262: ldc_w 707
    //   2265: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2268: iload 6
    //   2270: invokevirtual 710	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   2273: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2276: invokestatic 578	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   2279: pop
    //   2280: iload_2
    //   2281: iconst_1
    //   2282: iadd
    //   2283: istore_2
    //   2284: goto -809 -> 1475
    //   2287: iload_2
    //   2288: iload_3
    //   2289: iadd
    //   2290: istore_2
    //   2291: goto -1905 -> 386
    //   2294: iload 5
    //   2296: istore_3
    //   2297: goto -1664 -> 633
    //   2300: aload 21
    //   2302: astore 17
    //   2304: aload 21
    //   2306: astore 19
    //   2308: aload 21
    //   2310: invokevirtual 713	java/io/OutputStream:flush	()V
    //   2313: aload 21
    //   2315: astore 16
    //   2317: goto -1393 -> 924
    //   2320: astore 16
    //   2322: aload 18
    //   2324: astore 22
    //   2326: aload 16
    //   2328: astore 18
    //   2330: aload 17
    //   2332: astore 16
    //   2334: goto -2216 -> 118
    //   2337: lload 12
    //   2339: lstore 8
    //   2341: aload 16
    //   2343: astore 21
    //   2345: iload 4
    //   2347: istore_2
    //   2348: iload 4
    //   2350: ifgt -1622 -> 728
    //   2353: goto -1327 -> 1026
    //   2356: aload 20
    //   2358: astore 23
    //   2360: aload 16
    //   2362: astore 24
    //   2364: aload 26
    //   2366: astore 22
    //   2368: aload 20
    //   2370: astore 19
    //   2372: aload 16
    //   2374: astore 21
    //   2376: aload 27
    //   2378: astore 17
    //   2380: aload 25
    //   2382: ldc_w 715
    //   2385: invokevirtual 369	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   2388: ifeq +45 -> 2433
    //   2391: aload 20
    //   2393: astore 23
    //   2395: aload 16
    //   2397: astore 24
    //   2399: aload 26
    //   2401: astore 22
    //   2403: aload 20
    //   2405: astore 19
    //   2407: aload 16
    //   2409: astore 21
    //   2411: aload 27
    //   2413: astore 17
    //   2415: aload 25
    //   2417: iconst_0
    //   2418: aload 25
    //   2420: invokevirtual 654	java/lang/String:length	()I
    //   2423: iconst_4
    //   2424: isub
    //   2425: invokevirtual 658	java/lang/String:substring	(II)Ljava/lang/String;
    //   2428: astore 18
    //   2430: goto -734 -> 1696
    //   2433: aload 20
    //   2435: astore 23
    //   2437: aload 16
    //   2439: astore 24
    //   2441: aload 26
    //   2443: astore 22
    //   2445: aload 25
    //   2447: astore 18
    //   2449: aload 20
    //   2451: astore 19
    //   2453: aload 16
    //   2455: astore 21
    //   2457: aload 27
    //   2459: astore 17
    //   2461: aload 25
    //   2463: ldc_w 717
    //   2466: invokevirtual 369	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   2469: ifeq -773 -> 1696
    //   2472: aload 20
    //   2474: astore 23
    //   2476: aload 16
    //   2478: astore 24
    //   2480: aload 26
    //   2482: astore 22
    //   2484: aload 20
    //   2486: astore 19
    //   2488: aload 16
    //   2490: astore 21
    //   2492: aload 27
    //   2494: astore 17
    //   2496: aload 25
    //   2498: iconst_0
    //   2499: aload 25
    //   2501: invokevirtual 654	java/lang/String:length	()I
    //   2504: iconst_4
    //   2505: isub
    //   2506: invokevirtual 658	java/lang/String:substring	(II)Ljava/lang/String;
    //   2509: astore 18
    //   2511: goto -815 -> 1696
    //   2514: astore 25
    //   2516: aload 20
    //   2518: astore 23
    //   2520: aload 16
    //   2522: astore 24
    //   2524: aload 26
    //   2526: astore 22
    //   2528: aload 20
    //   2530: astore 19
    //   2532: aload 16
    //   2534: astore 21
    //   2536: aload 27
    //   2538: astore 17
    //   2540: aload 18
    //   2542: monitorexit
    //   2543: aload 20
    //   2545: astore 23
    //   2547: aload 16
    //   2549: astore 24
    //   2551: aload 26
    //   2553: astore 22
    //   2555: aload 20
    //   2557: astore 19
    //   2559: aload 16
    //   2561: astore 21
    //   2563: aload 27
    //   2565: astore 17
    //   2567: aload 25
    //   2569: athrow
    //   2570: astore 16
    //   2572: aload 19
    //   2574: astore 20
    //   2576: aload 21
    //   2578: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   2581: aload 20
    //   2583: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   2586: aload_1
    //   2587: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   2590: aload 17
    //   2592: ifnull +9 -> 2601
    //   2595: aload 17
    //   2597: invokevirtual 379	java/io/File:delete	()Z
    //   2600: pop
    //   2601: aload 16
    //   2603: athrow
    //   2604: aload 16
    //   2606: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   2609: aload 20
    //   2611: invokestatic 512	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   2614: aload_1
    //   2615: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   2618: return
    //   2619: astore 16
    //   2621: aload 18
    //   2623: astore 17
    //   2625: aload 19
    //   2627: astore 21
    //   2629: goto -53 -> 2576
    //   2632: astore 19
    //   2634: aload 18
    //   2636: astore 17
    //   2638: aload 16
    //   2640: astore 21
    //   2642: aload 19
    //   2644: astore 16
    //   2646: goto -70 -> 2576
    //   2649: astore 17
    //   2651: aload 18
    //   2653: astore 22
    //   2655: aload 17
    //   2657: astore 18
    //   2659: goto -2541 -> 118
    //   2662: iload_3
    //   2663: istore 4
    //   2665: iload_3
    //   2666: sipush 512
    //   2669: if_icmpge -2110 -> 559
    //   2672: sipush 512
    //   2675: istore 4
    //   2677: goto -2118 -> 559
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2680	0	this	DropBoxManagerService
    //   0	2680	1	paramEntry	DropBoxManager.Entry
    //   385	1963	2	i	int
    //   454	2216	3	j	int
    //   539	2137	4	k	int
    //   67	2228	5	m	int
    //   37	2232	6	bool1	boolean
    //   243	784	7	bool2	boolean
    //   320	2020	8	l1	long
    //   291	701	10	l2	long
    //   840	1498	12	l3	long
    //   836	33	14	l4	long
    //   13	2303	16	localObject1	Object
    //   2320	7	16	localIOException1	IOException
    //   2332	228	16	localObject2	Object
    //   2570	35	16	localAutoCloseable	AutoCloseable
    //   2619	20	16	localObject3	Object
    //   2644	1	16	localObject4	Object
    //   61	2576	17	localObject5	Object
    //   2649	7	17	localIOException2	IOException
    //   4	81	18	localObject6	Object
    //   108	360	18	localIOException3	IOException
    //   525	2133	18	localObject7	Object
    //   53	2573	19	localObject8	Object
    //   2632	11	19	localObject9	Object
    //   10	2600	20	localObject10	Object
    //   57	2584	21	localObject11	Object
    //   49	2605	22	localObject12	Object
    //   41	2505	23	localObject13	Object
    //   45	2505	24	localObject14	Object
    //   19	2481	25	localObject15	Object
    //   2514	54	25	localObject16	Object
    //   22	2530	26	localObject17	Object
    //   1	2563	27	localObject18	Object
    //   16	2176	28	arrayOfFile	File[]
    //   7	360	29	localObject19	Object
    //   28	2168	30	str	String
    //   352	531	31	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   63	69	108	java/io/IOException
    //   100	108	108	java/io/IOException
    //   209	213	108	java/io/IOException
    //   237	245	108	java/io/IOException
    //   287	293	108	java/io/IOException
    //   317	322	108	java/io/IOException
    //   346	354	108	java/io/IOException
    //   378	384	108	java/io/IOException
    //   410	417	108	java/io/IOException
    //   441	455	108	java/io/IOException
    //   483	527	108	java/io/IOException
    //   1055	1061	108	java/io/IOException
    //   1090	1101	108	java/io/IOException
    //   1125	1132	108	java/io/IOException
    //   1156	1182	108	java/io/IOException
    //   1206	1217	108	java/io/IOException
    //   1247	1259	108	java/io/IOException
    //   1283	1294	108	java/io/IOException
    //   1318	1329	108	java/io/IOException
    //   1353	1360	108	java/io/IOException
    //   1384	1393	108	java/io/IOException
    //   1417	1435	108	java/io/IOException
    //   1459	1473	108	java/io/IOException
    //   1504	1511	108	java/io/IOException
    //   1535	1544	108	java/io/IOException
    //   1572	1583	108	java/io/IOException
    //   1607	1622	108	java/io/IOException
    //   1646	1657	108	java/io/IOException
    //   1681	1696	108	java/io/IOException
    //   1720	1744	108	java/io/IOException
    //   1768	1802	108	java/io/IOException
    //   1826	1863	108	java/io/IOException
    //   1887	1919	108	java/io/IOException
    //   1948	1959	108	java/io/IOException
    //   1983	1995	108	java/io/IOException
    //   2019	2030	108	java/io/IOException
    //   2054	2064	108	java/io/IOException
    //   2088	2099	108	java/io/IOException
    //   2123	2134	108	java/io/IOException
    //   2158	2164	108	java/io/IOException
    //   2188	2191	108	java/io/IOException
    //   2226	2229	108	java/io/IOException
    //   2253	2280	108	java/io/IOException
    //   2380	2391	108	java/io/IOException
    //   2415	2430	108	java/io/IOException
    //   2461	2472	108	java/io/IOException
    //   2496	2511	108	java/io/IOException
    //   2540	2543	108	java/io/IOException
    //   2567	2570	108	java/io/IOException
    //   535	541	2320	java/io/IOException
    //   567	578	2320	java/io/IOException
    //   586	599	2320	java/io/IOException
    //   646	657	2320	java/io/IOException
    //   665	673	2320	java/io/IOException
    //   681	708	2320	java/io/IOException
    //   716	724	2320	java/io/IOException
    //   736	745	2320	java/io/IOException
    //   758	769	2320	java/io/IOException
    //   777	791	2320	java/io/IOException
    //   799	825	2320	java/io/IOException
    //   833	838	2320	java/io/IOException
    //   862	868	2320	java/io/IOException
    //   880	889	2320	java/io/IOException
    //   902	908	2320	java/io/IOException
    //   916	921	2320	java/io/IOException
    //   932	943	2320	java/io/IOException
    //   951	1009	2320	java/io/IOException
    //   1017	1023	2320	java/io/IOException
    //   2308	2313	2320	java/io/IOException
    //   2191	2202	2514	finally
    //   63	69	2570	finally
    //   100	108	2570	finally
    //   130	159	2570	finally
    //   209	213	2570	finally
    //   237	245	2570	finally
    //   287	293	2570	finally
    //   317	322	2570	finally
    //   346	354	2570	finally
    //   378	384	2570	finally
    //   410	417	2570	finally
    //   441	455	2570	finally
    //   483	527	2570	finally
    //   1055	1061	2570	finally
    //   1090	1101	2570	finally
    //   1125	1132	2570	finally
    //   1156	1182	2570	finally
    //   1206	1217	2570	finally
    //   1247	1259	2570	finally
    //   1283	1294	2570	finally
    //   1318	1329	2570	finally
    //   1353	1360	2570	finally
    //   1384	1393	2570	finally
    //   1417	1435	2570	finally
    //   1459	1473	2570	finally
    //   1504	1511	2570	finally
    //   1535	1544	2570	finally
    //   1572	1583	2570	finally
    //   1607	1622	2570	finally
    //   1646	1657	2570	finally
    //   1681	1696	2570	finally
    //   1720	1744	2570	finally
    //   1768	1802	2570	finally
    //   1826	1863	2570	finally
    //   1887	1919	2570	finally
    //   1948	1959	2570	finally
    //   1983	1995	2570	finally
    //   2019	2030	2570	finally
    //   2054	2064	2570	finally
    //   2088	2099	2570	finally
    //   2123	2134	2570	finally
    //   2158	2164	2570	finally
    //   2188	2191	2570	finally
    //   2226	2229	2570	finally
    //   2253	2280	2570	finally
    //   2380	2391	2570	finally
    //   2415	2430	2570	finally
    //   2461	2472	2570	finally
    //   2496	2511	2570	finally
    //   2540	2543	2570	finally
    //   2567	2570	2570	finally
    //   535	541	2619	finally
    //   567	578	2619	finally
    //   586	599	2619	finally
    //   646	657	2619	finally
    //   665	673	2619	finally
    //   681	708	2619	finally
    //   716	724	2619	finally
    //   736	745	2619	finally
    //   758	769	2619	finally
    //   777	791	2619	finally
    //   799	825	2619	finally
    //   833	838	2619	finally
    //   862	868	2619	finally
    //   880	889	2619	finally
    //   902	908	2619	finally
    //   916	921	2619	finally
    //   932	943	2619	finally
    //   951	1009	2619	finally
    //   1017	1023	2619	finally
    //   2308	2313	2619	finally
    //   599	606	2632	finally
    //   613	624	2632	finally
    //   599	606	2649	java/io/IOException
    //   613	624	2649	java/io/IOException
  }
  
  /* Error */
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 180	com/android/server/DropBoxManagerService:getContext	()Landroid/content/Context;
    //   6: ldc_w 721
    //   9: invokevirtual 724	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   12: ifeq +13 -> 25
    //   15: aload_2
    //   16: ldc_w 726
    //   19: invokevirtual 731	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    //   25: aload_0
    //   26: invokespecial 111	com/android/server/DropBoxManagerService:init	()V
    //   29: new 212	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   36: astore 16
    //   38: iconst_0
    //   39: istore 5
    //   41: iconst_0
    //   42: istore 6
    //   44: new 676	java/util/ArrayList
    //   47: dup
    //   48: invokespecial 677	java/util/ArrayList:<init>	()V
    //   51: astore 17
    //   53: iconst_0
    //   54: istore 4
    //   56: aload_3
    //   57: ifnull +162 -> 219
    //   60: iload 4
    //   62: aload_3
    //   63: arraylength
    //   64: if_icmpge +155 -> 219
    //   67: aload_3
    //   68: iload 4
    //   70: aaload
    //   71: ldc_w 733
    //   74: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   77: ifne +1236 -> 1313
    //   80: aload_3
    //   81: iload 4
    //   83: aaload
    //   84: ldc_w 735
    //   87: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   90: ifeq +44 -> 134
    //   93: goto +1220 -> 1313
    //   96: astore_1
    //   97: aload_2
    //   98: new 212	java/lang/StringBuilder
    //   101: dup
    //   102: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   105: ldc_w 737
    //   108: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_1
    //   112: invokevirtual 355	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokevirtual 731	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   121: ldc 52
    //   123: ldc_w 739
    //   126: aload_1
    //   127: invokestatic 486	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   130: pop
    //   131: aload_0
    //   132: monitorexit
    //   133: return
    //   134: aload_3
    //   135: iload 4
    //   137: aaload
    //   138: ldc_w 741
    //   141: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   144: ifne +1181 -> 1325
    //   147: aload_3
    //   148: iload 4
    //   150: aaload
    //   151: ldc_w 743
    //   154: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   157: ifeq +6 -> 163
    //   160: goto +1165 -> 1325
    //   163: aload_3
    //   164: iload 4
    //   166: aaload
    //   167: ldc_w 745
    //   170: invokevirtual 747	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   173: ifeq +33 -> 206
    //   176: aload 16
    //   178: ldc_w 749
    //   181: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: aload_3
    //   185: iload 4
    //   187: aaload
    //   188: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: ldc_w 751
    //   194: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: pop
    //   198: goto +1118 -> 1316
    //   201: astore_1
    //   202: aload_0
    //   203: monitorexit
    //   204: aload_1
    //   205: athrow
    //   206: aload 17
    //   208: aload_3
    //   209: iload 4
    //   211: aaload
    //   212: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   215: pop
    //   216: goto +1100 -> 1316
    //   219: aload 16
    //   221: ldc_w 753
    //   224: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   227: aload_0
    //   228: getfield 140	com/android/server/DropBoxManagerService:mAllFiles	Lcom/android/server/DropBoxManagerService$FileList;
    //   231: getfield 242	com/android/server/DropBoxManagerService$FileList:contents	Ljava/util/TreeSet;
    //   234: invokevirtual 431	java/util/TreeSet:size	()I
    //   237: invokevirtual 575	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   240: ldc_w 755
    //   243: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: pop
    //   247: aload 17
    //   249: invokevirtual 756	java/util/ArrayList:isEmpty	()Z
    //   252: ifne +66 -> 318
    //   255: aload 16
    //   257: ldc_w 758
    //   260: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: pop
    //   264: aload 17
    //   266: invokeinterface 470 1 0
    //   271: astore_1
    //   272: aload_1
    //   273: invokeinterface 475 1 0
    //   278: ifeq +31 -> 309
    //   281: aload_1
    //   282: invokeinterface 478 1 0
    //   287: checkcast 114	java/lang/String
    //   290: astore 11
    //   292: aload 16
    //   294: ldc_w 760
    //   297: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: aload 11
    //   302: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: pop
    //   306: goto -34 -> 272
    //   309: aload 16
    //   311: ldc_w 751
    //   314: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: pop
    //   318: iconst_0
    //   319: istore 4
    //   321: aload 17
    //   323: invokevirtual 761	java/util/ArrayList:size	()I
    //   326: istore 9
    //   328: new 763	android/text/format/Time
    //   331: dup
    //   332: invokespecial 764	android/text/format/Time:<init>	()V
    //   335: astore 18
    //   337: aload 16
    //   339: ldc_w 751
    //   342: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   345: pop
    //   346: aload_0
    //   347: getfield 140	com/android/server/DropBoxManagerService:mAllFiles	Lcom/android/server/DropBoxManagerService$FileList;
    //   350: getfield 242	com/android/server/DropBoxManagerService$FileList:contents	Ljava/util/TreeSet;
    //   353: invokeinterface 470 1 0
    //   358: astore 19
    //   360: aload 19
    //   362: invokeinterface 475 1 0
    //   367: ifeq +862 -> 1229
    //   370: aload 19
    //   372: invokeinterface 478 1 0
    //   377: checkcast 18	com/android/server/DropBoxManagerService$EntryFile
    //   380: astore 20
    //   382: aload 18
    //   384: aload 20
    //   386: getfield 280	com/android/server/DropBoxManagerService$EntryFile:timestampMillis	J
    //   389: invokevirtual 767	android/text/format/Time:set	(J)V
    //   392: aload 18
    //   394: ldc_w 769
    //   397: invokevirtual 772	android/text/format/Time:format	(Ljava/lang/String;)Ljava/lang/String;
    //   400: astore_1
    //   401: iconst_1
    //   402: istore 10
    //   404: iconst_0
    //   405: istore 7
    //   407: iload 7
    //   409: iload 9
    //   411: if_icmpge +44 -> 455
    //   414: iload 10
    //   416: ifeq +39 -> 455
    //   419: aload 17
    //   421: iload 7
    //   423: invokevirtual 775	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   426: checkcast 114	java/lang/String
    //   429: astore 11
    //   431: aload_1
    //   432: aload 11
    //   434: invokevirtual 687	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   437: ifne +903 -> 1340
    //   440: aload 11
    //   442: aload 20
    //   444: getfield 293	com/android/server/DropBoxManagerService$EntryFile:tag	Ljava/lang/String;
    //   447: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   450: istore 10
    //   452: goto +879 -> 1331
    //   455: iload 10
    //   457: ifeq -97 -> 360
    //   460: iload 4
    //   462: iconst_1
    //   463: iadd
    //   464: istore 8
    //   466: iload 5
    //   468: ifeq +12 -> 480
    //   471: aload 16
    //   473: ldc_w 777
    //   476: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   479: pop
    //   480: aload 16
    //   482: aload_1
    //   483: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: ldc_w 760
    //   489: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   492: astore 11
    //   494: aload 20
    //   496: getfield 293	com/android/server/DropBoxManagerService$EntryFile:tag	Ljava/lang/String;
    //   499: ifnonnull +38 -> 537
    //   502: ldc_w 779
    //   505: astore_1
    //   506: aload 11
    //   508: aload_1
    //   509: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   512: pop
    //   513: aload 20
    //   515: getfield 308	com/android/server/DropBoxManagerService$EntryFile:file	Ljava/io/File;
    //   518: ifnonnull +28 -> 546
    //   521: aload 16
    //   523: ldc_w 781
    //   526: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   529: pop
    //   530: iload 8
    //   532: istore 4
    //   534: goto -174 -> 360
    //   537: aload 20
    //   539: getfield 293	com/android/server/DropBoxManagerService$EntryFile:tag	Ljava/lang/String;
    //   542: astore_1
    //   543: goto -37 -> 506
    //   546: aload 20
    //   548: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   551: iconst_1
    //   552: iand
    //   553: ifeq +19 -> 572
    //   556: aload 16
    //   558: ldc_w 783
    //   561: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   564: pop
    //   565: iload 8
    //   567: istore 4
    //   569: goto -209 -> 360
    //   572: aload 16
    //   574: ldc_w 615
    //   577: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   580: pop
    //   581: aload 20
    //   583: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   586: iconst_4
    //   587: iand
    //   588: ifeq +12 -> 600
    //   591: aload 16
    //   593: ldc_w 785
    //   596: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   599: pop
    //   600: aload 20
    //   602: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   605: iconst_2
    //   606: iand
    //   607: ifeq +139 -> 746
    //   610: ldc_w 787
    //   613: astore_1
    //   614: aload 16
    //   616: aload_1
    //   617: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   620: pop
    //   621: aload 16
    //   623: ldc_w 789
    //   626: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   629: aload 20
    //   631: getfield 308	com/android/server/DropBoxManagerService$EntryFile:file	Ljava/io/File;
    //   634: invokevirtual 611	java/io/File:length	()J
    //   637: invokevirtual 542	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   640: ldc_w 791
    //   643: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   646: pop
    //   647: iload 6
    //   649: ifne +18 -> 667
    //   652: iload 5
    //   654: ifeq +47 -> 701
    //   657: aload 20
    //   659: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   662: iconst_2
    //   663: iand
    //   664: ifne +37 -> 701
    //   667: iload 5
    //   669: ifne +12 -> 681
    //   672: aload 16
    //   674: ldc_w 793
    //   677: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   680: pop
    //   681: aload 16
    //   683: aload 20
    //   685: getfield 308	com/android/server/DropBoxManagerService$EntryFile:file	Ljava/io/File;
    //   688: invokevirtual 342	java/io/File:getPath	()Ljava/lang/String;
    //   691: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   694: ldc_w 751
    //   697: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   700: pop
    //   701: aload 20
    //   703: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   706: iconst_2
    //   707: iand
    //   708: ifeq +13 -> 721
    //   711: iload 5
    //   713: ifne +40 -> 753
    //   716: iload 6
    //   718: ifeq +35 -> 753
    //   721: iload 8
    //   723: istore 4
    //   725: iload 5
    //   727: ifeq -367 -> 360
    //   730: aload 16
    //   732: ldc_w 751
    //   735: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   738: pop
    //   739: iload 8
    //   741: istore 4
    //   743: goto -383 -> 360
    //   746: ldc_w 795
    //   749: astore_1
    //   750: goto -136 -> 614
    //   753: aconst_null
    //   754: astore 14
    //   756: aconst_null
    //   757: astore 15
    //   759: aconst_null
    //   760: astore 13
    //   762: aconst_null
    //   763: astore_1
    //   764: new 489	android/os/DropBoxManager$Entry
    //   767: dup
    //   768: aload 20
    //   770: getfield 293	com/android/server/DropBoxManagerService$EntryFile:tag	Ljava/lang/String;
    //   773: aload 20
    //   775: getfield 280	com/android/server/DropBoxManagerService$EntryFile:timestampMillis	J
    //   778: aload 20
    //   780: getfield 308	com/android/server/DropBoxManagerService$EntryFile:file	Ljava/io/File;
    //   783: aload 20
    //   785: getfield 305	com/android/server/DropBoxManagerService$EntryFile:flags	I
    //   788: invokespecial 798	android/os/DropBoxManager$Entry:<init>	(Ljava/lang/String;JLjava/io/File;I)V
    //   791: astore 11
    //   793: iload 5
    //   795: ifeq +259 -> 1054
    //   798: aload 11
    //   800: astore 12
    //   802: aload 15
    //   804: astore 13
    //   806: new 800	java/io/InputStreamReader
    //   809: dup
    //   810: aload 11
    //   812: invokevirtual 522	android/os/DropBoxManager$Entry:getInputStream	()Ljava/io/InputStream;
    //   815: invokespecial 803	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   818: astore_1
    //   819: sipush 4096
    //   822: newarray <illegal type>
    //   824: astore 12
    //   826: iconst_0
    //   827: istore 7
    //   829: aload_1
    //   830: aload 12
    //   832: invokevirtual 806	java/io/InputStreamReader:read	([C)I
    //   835: istore 4
    //   837: iload 4
    //   839: ifgt +42 -> 881
    //   842: iload 7
    //   844: ifne +466 -> 1310
    //   847: aload 16
    //   849: ldc_w 751
    //   852: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   855: pop
    //   856: aload 11
    //   858: ifnull +8 -> 866
    //   861: aload 11
    //   863: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   866: aload_1
    //   867: ifnull -146 -> 721
    //   870: aload_1
    //   871: invokevirtual 807	java/io/InputStreamReader:close	()V
    //   874: goto -153 -> 721
    //   877: astore_1
    //   878: goto -157 -> 721
    //   881: aload 16
    //   883: aload 12
    //   885: iconst_0
    //   886: iload 4
    //   888: invokevirtual 810	java/lang/StringBuilder:append	([CII)Ljava/lang/StringBuilder;
    //   891: pop
    //   892: aload 12
    //   894: iload 4
    //   896: iconst_1
    //   897: isub
    //   898: caload
    //   899: bipush 10
    //   901: if_icmpne +147 -> 1048
    //   904: iconst_1
    //   905: istore 4
    //   907: iload 4
    //   909: istore 7
    //   911: aload 16
    //   913: invokevirtual 811	java/lang/StringBuilder:length	()I
    //   916: ldc_w 812
    //   919: if_icmple -90 -> 829
    //   922: aload_2
    //   923: aload 16
    //   925: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   928: invokevirtual 814	java/io/PrintWriter:write	(Ljava/lang/String;)V
    //   931: aload 16
    //   933: iconst_0
    //   934: invokevirtual 818	java/lang/StringBuilder:setLength	(I)V
    //   937: iload 4
    //   939: istore 7
    //   941: goto -112 -> 829
    //   944: astore 12
    //   946: aload_1
    //   947: astore 14
    //   949: aload 12
    //   951: astore_1
    //   952: aload 11
    //   954: astore 12
    //   956: aload 14
    //   958: astore 13
    //   960: aload 16
    //   962: ldc_w 820
    //   965: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   968: aload_1
    //   969: invokevirtual 821	java/io/IOException:toString	()Ljava/lang/String;
    //   972: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   975: ldc_w 751
    //   978: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   981: pop
    //   982: aload 11
    //   984: astore 12
    //   986: aload 14
    //   988: astore 13
    //   990: ldc 52
    //   992: new 212	java/lang/StringBuilder
    //   995: dup
    //   996: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   999: ldc_w 823
    //   1002: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1005: aload 20
    //   1007: getfield 308	com/android/server/DropBoxManagerService$EntryFile:file	Ljava/io/File;
    //   1010: invokevirtual 355	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1013: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1016: aload_1
    //   1017: invokestatic 486	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1020: pop
    //   1021: aload 11
    //   1023: ifnull +8 -> 1031
    //   1026: aload 11
    //   1028: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   1031: aload 14
    //   1033: ifnull -312 -> 721
    //   1036: aload 14
    //   1038: invokevirtual 807	java/io/InputStreamReader:close	()V
    //   1041: goto -320 -> 721
    //   1044: astore_1
    //   1045: goto -324 -> 721
    //   1048: iconst_0
    //   1049: istore 4
    //   1051: goto -144 -> 907
    //   1054: aload 11
    //   1056: astore 12
    //   1058: aload 15
    //   1060: astore 13
    //   1062: aload 11
    //   1064: bipush 70
    //   1066: invokevirtual 827	android/os/DropBoxManager$Entry:getText	(I)Ljava/lang/String;
    //   1069: astore 21
    //   1071: aload 11
    //   1073: astore 12
    //   1075: aload 15
    //   1077: astore 13
    //   1079: aload 16
    //   1081: ldc_w 793
    //   1084: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1087: pop
    //   1088: aload 21
    //   1090: ifnonnull +40 -> 1130
    //   1093: aload 11
    //   1095: astore 12
    //   1097: aload 15
    //   1099: astore 13
    //   1101: aload 16
    //   1103: ldc_w 829
    //   1106: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1109: pop
    //   1110: aload 11
    //   1112: astore 12
    //   1114: aload 15
    //   1116: astore 13
    //   1118: aload 16
    //   1120: ldc_w 751
    //   1123: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1126: pop
    //   1127: goto -271 -> 856
    //   1130: aload 11
    //   1132: astore 12
    //   1134: aload 15
    //   1136: astore 13
    //   1138: aload 21
    //   1140: invokevirtual 654	java/lang/String:length	()I
    //   1143: bipush 70
    //   1145: if_icmpne +205 -> 1350
    //   1148: iconst_1
    //   1149: istore 4
    //   1151: aload 11
    //   1153: astore 12
    //   1155: aload 15
    //   1157: astore 13
    //   1159: aload 16
    //   1161: aload 21
    //   1163: invokevirtual 832	java/lang/String:trim	()Ljava/lang/String;
    //   1166: bipush 10
    //   1168: bipush 47
    //   1170: invokevirtual 836	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1173: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1176: pop
    //   1177: iload 4
    //   1179: ifeq -69 -> 1110
    //   1182: aload 11
    //   1184: astore 12
    //   1186: aload 15
    //   1188: astore 13
    //   1190: aload 16
    //   1192: ldc_w 838
    //   1195: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1198: pop
    //   1199: goto -89 -> 1110
    //   1202: astore_1
    //   1203: aload 12
    //   1205: astore 11
    //   1207: aload 11
    //   1209: ifnull +8 -> 1217
    //   1212: aload 11
    //   1214: invokevirtual 515	android/os/DropBoxManager$Entry:close	()V
    //   1217: aload 13
    //   1219: ifnull +8 -> 1227
    //   1222: aload 13
    //   1224: invokevirtual 807	java/io/InputStreamReader:close	()V
    //   1227: aload_1
    //   1228: athrow
    //   1229: iload 4
    //   1231: ifne +12 -> 1243
    //   1234: aload 16
    //   1236: ldc_w 840
    //   1239: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1242: pop
    //   1243: aload_3
    //   1244: ifnull +8 -> 1252
    //   1247: aload_3
    //   1248: arraylength
    //   1249: ifne +26 -> 1275
    //   1252: iload 5
    //   1254: ifne +12 -> 1266
    //   1257: aload 16
    //   1259: ldc_w 751
    //   1262: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1265: pop
    //   1266: aload 16
    //   1268: ldc_w 842
    //   1271: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1274: pop
    //   1275: aload_2
    //   1276: aload 16
    //   1278: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1281: invokevirtual 814	java/io/PrintWriter:write	(Ljava/lang/String;)V
    //   1284: aload_0
    //   1285: monitorexit
    //   1286: return
    //   1287: astore_1
    //   1288: aconst_null
    //   1289: astore 11
    //   1291: goto -84 -> 1207
    //   1294: astore_2
    //   1295: aload_1
    //   1296: astore 13
    //   1298: aload_2
    //   1299: astore_1
    //   1300: goto -93 -> 1207
    //   1303: astore_1
    //   1304: aconst_null
    //   1305: astore 11
    //   1307: goto -355 -> 952
    //   1310: goto -454 -> 856
    //   1313: iconst_1
    //   1314: istore 5
    //   1316: iload 4
    //   1318: iconst_1
    //   1319: iadd
    //   1320: istore 4
    //   1322: goto -1266 -> 56
    //   1325: iconst_1
    //   1326: istore 6
    //   1328: goto -12 -> 1316
    //   1331: iload 7
    //   1333: iconst_1
    //   1334: iadd
    //   1335: istore 7
    //   1337: goto -930 -> 407
    //   1340: iconst_1
    //   1341: istore 10
    //   1343: goto -12 -> 1331
    //   1346: astore_1
    //   1347: goto -395 -> 952
    //   1350: iconst_0
    //   1351: istore 4
    //   1353: goto -202 -> 1151
    //   1356: astore_2
    //   1357: goto -130 -> 1227
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1360	0	this	DropBoxManagerService
    //   0	1360	1	paramFileDescriptor	FileDescriptor
    //   0	1360	2	paramPrintWriter	PrintWriter
    //   0	1360	3	paramArrayOfString	String[]
    //   54	1298	4	i	int
    //   39	1276	5	j	int
    //   42	1285	6	k	int
    //   405	931	7	m	int
    //   464	276	8	n	int
    //   326	86	9	i1	int
    //   402	940	10	bool	boolean
    //   290	1016	11	localObject1	Object
    //   800	93	12	localObject2	Object
    //   944	6	12	localIOException	IOException
    //   954	250	12	localObject3	Object
    //   760	537	13	localObject4	Object
    //   754	283	14	localFileDescriptor	FileDescriptor
    //   757	430	15	localObject5	Object
    //   36	1241	16	localStringBuilder	StringBuilder
    //   51	369	17	localArrayList	java.util.ArrayList
    //   335	58	18	localTime	android.text.format.Time
    //   358	13	19	localIterator	Iterator
    //   380	626	20	localEntryFile	EntryFile
    //   1069	93	21	str	String
    // Exception table:
    //   from	to	target	type
    //   25	29	96	java/io/IOException
    //   2	22	201	finally
    //   25	29	201	finally
    //   29	38	201	finally
    //   44	53	201	finally
    //   60	93	201	finally
    //   97	131	201	finally
    //   134	160	201	finally
    //   163	198	201	finally
    //   206	216	201	finally
    //   219	272	201	finally
    //   272	306	201	finally
    //   309	318	201	finally
    //   321	360	201	finally
    //   360	401	201	finally
    //   419	452	201	finally
    //   471	480	201	finally
    //   480	502	201	finally
    //   506	530	201	finally
    //   537	543	201	finally
    //   546	565	201	finally
    //   572	600	201	finally
    //   600	610	201	finally
    //   614	647	201	finally
    //   657	667	201	finally
    //   672	681	201	finally
    //   681	701	201	finally
    //   701	711	201	finally
    //   730	739	201	finally
    //   861	866	201	finally
    //   870	874	201	finally
    //   1026	1031	201	finally
    //   1036	1041	201	finally
    //   1212	1217	201	finally
    //   1222	1227	201	finally
    //   1227	1229	201	finally
    //   1234	1243	201	finally
    //   1247	1252	201	finally
    //   1257	1266	201	finally
    //   1266	1275	201	finally
    //   1275	1284	201	finally
    //   870	874	877	java/io/IOException
    //   819	826	944	java/io/IOException
    //   829	837	944	java/io/IOException
    //   847	856	944	java/io/IOException
    //   881	892	944	java/io/IOException
    //   911	937	944	java/io/IOException
    //   1036	1041	1044	java/io/IOException
    //   806	819	1202	finally
    //   960	982	1202	finally
    //   990	1021	1202	finally
    //   1062	1071	1202	finally
    //   1079	1088	1202	finally
    //   1101	1110	1202	finally
    //   1118	1127	1202	finally
    //   1138	1148	1202	finally
    //   1159	1177	1202	finally
    //   1190	1199	1202	finally
    //   764	793	1287	finally
    //   819	826	1294	finally
    //   829	837	1294	finally
    //   847	856	1294	finally
    //   881	892	1294	finally
    //   911	937	1294	finally
    //   764	793	1303	java/io/IOException
    //   806	819	1346	java/io/IOException
    //   1062	1071	1346	java/io/IOException
    //   1079	1088	1346	java/io/IOException
    //   1101	1110	1346	java/io/IOException
    //   1118	1127	1346	java/io/IOException
    //   1138	1148	1346	java/io/IOException
    //   1159	1177	1346	java/io/IOException
    //   1190	1199	1346	java/io/IOException
    //   1222	1227	1356	java/io/IOException
  }
  
  public DropBoxManager.Entry getNextEntry(String paramString, long paramLong)
  {
    try
    {
      if (getContext().checkCallingOrSelfPermission("android.permission.READ_LOGS") != 0) {
        throw new SecurityException("READ_LOGS permission required");
      }
    }
    finally {}
    for (;;)
    {
      try
      {
        init();
        if (paramString == null)
        {
          paramString = this.mAllFiles;
          if (paramString != null) {
            break;
          }
          return null;
        }
      }
      catch (IOException paramString)
      {
        Slog.e("DropBoxManagerService", "Can't init", paramString);
        return null;
      }
      paramString = (FileList)this.mFilesByTag.get(paramString);
    }
    paramString = paramString.contents.tailSet(new EntryFile(1L + paramLong)).iterator();
    while (paramString.hasNext())
    {
      EntryFile localEntryFile = (EntryFile)paramString.next();
      if (localEntryFile.tag != null)
      {
        if ((localEntryFile.flags & 0x1) != 0)
        {
          paramString = new DropBoxManager.Entry(localEntryFile.tag, localEntryFile.timestampMillis);
          return paramString;
        }
        try
        {
          DropBoxManager.Entry localEntry = new DropBoxManager.Entry(localEntryFile.tag, localEntryFile.timestampMillis, localEntryFile.file, localEntryFile.flags);
          return localEntry;
        }
        catch (IOException localIOException)
        {
          Slog.e("DropBoxManagerService", "Can't read: " + localEntryFile.file, localIOException);
        }
      }
    }
    return null;
  }
  
  public IDropBoxManagerService getServiceStub()
  {
    return this.mStub;
  }
  
  /* Error */
  public boolean isTagEnabled(String paramString)
  {
    // Byte code:
    //   0: invokestatic 861	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: ldc_w 863
    //   7: aload_0
    //   8: getfield 188	com/android/server/DropBoxManagerService:mContentResolver	Landroid/content/ContentResolver;
    //   11: new 212	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   18: ldc_w 865
    //   21: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_1
    //   25: invokevirtual 219	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 223	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 868	android/provider/Settings$Global:getString	(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;
    //   34: invokevirtual 393	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   37: istore 4
    //   39: iload 4
    //   41: ifeq +13 -> 54
    //   44: iconst_0
    //   45: istore 4
    //   47: lload_2
    //   48: invokestatic 871	android/os/Binder:restoreCallingIdentity	(J)V
    //   51: iload 4
    //   53: ireturn
    //   54: iconst_1
    //   55: istore 4
    //   57: goto -10 -> 47
    //   60: astore_1
    //   61: lload_2
    //   62: invokestatic 871	android/os/Binder:restoreCallingIdentity	(J)V
    //   65: aload_1
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	DropBoxManagerService
    //   0	67	1	paramString	String
    //   3	59	2	l	long
    //   37	19	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   4	39	60	finally
  }
  
  public void onBootPhase(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    }
    this.mBooted = true;
  }
  
  public void onStart()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.DEVICE_STORAGE_LOW");
    getContext().registerReceiver(this.mReceiver, localIntentFilter);
    this.mContentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, new ContentObserver(new Handler())
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        DropBoxManagerService.-get1(DropBoxManagerService.this).onReceive(DropBoxManagerService.this.getContext(), (Intent)null);
      }
    });
    publishBinderService("dropbox", this.mStub);
    this.mTrackerHandler = new Handler(this.mTrackerHandlerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        super.handleMessage(paramAnonymousMessage);
        String str = (String)paramAnonymousMessage.obj;
        switch (paramAnonymousMessage.what)
        {
        default: 
          return;
        case 1: 
          DropBoxManagerService.-get0(DropBoxManagerService.this).onEvent(str, null);
          return;
        }
        paramAnonymousMessage = new File("/data/mdm/" + str);
        try
        {
          if (!paramAnonymousMessage.exists()) {
            paramAnonymousMessage.createNewFile();
          }
          Log.v("MDM_DropBox", "Recording log which need to generated as file");
          return;
        }
        catch (IOException paramAnonymousMessage)
        {
          for (;;)
          {
            Log.e("DropBoxManagerService", paramAnonymousMessage.getMessage());
          }
        }
      }
    };
  }
  
  private static final class EntryFile
    implements Comparable<EntryFile>
  {
    public final int blocks;
    public final File file;
    public final int flags;
    public final String tag;
    public final long timestampMillis;
    
    public EntryFile(long paramLong)
    {
      this.tag = null;
      this.timestampMillis = paramLong;
      this.flags = 1;
      this.file = null;
      this.blocks = 0;
    }
    
    public EntryFile(File paramFile, int paramInt)
    {
      this.file = paramFile;
      this.blocks = ((int)((this.file.length() + paramInt - 1L) / paramInt));
      String str = paramFile.getName();
      int i = str.lastIndexOf('@');
      if (i < 0)
      {
        this.tag = null;
        this.timestampMillis = 0L;
        this.flags = 1;
        return;
      }
      paramInt = 0;
      this.tag = Uri.decode(str.substring(0, i));
      paramFile = str;
      if (str.endsWith(".gz"))
      {
        paramInt = 4;
        paramFile = str.substring(0, str.length() - 3);
      }
      if (paramFile.endsWith(".lost"))
      {
        paramInt |= 0x1;
        paramFile = paramFile.substring(i + 1, paramFile.length() - 5);
      }
      for (;;)
      {
        this.flags = paramInt;
        try
        {
          l = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss_SSS").parse(paramFile).getTime();
          this.timestampMillis = l;
          return;
          if (paramFile.endsWith(".txt"))
          {
            paramInt |= 0x2;
            paramFile = paramFile.substring(i + 1, paramFile.length() - 4);
            continue;
          }
          if (paramFile.endsWith(".dat"))
          {
            paramFile = paramFile.substring(i + 1, paramFile.length() - 4);
            continue;
          }
          this.flags = 1;
          this.timestampMillis = 0L;
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.w("DropBoxManagerService", "file name:" + paramFile + ", Exception:" + localException.getMessage());
            long l = 0L;
          }
        }
      }
    }
    
    public EntryFile(File paramFile1, File paramFile2, String paramString, long paramLong, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((paramInt1 & 0x1) != 0) {
        throw new IllegalArgumentException();
      }
      this.tag = paramString;
      this.timestampMillis = paramLong;
      this.flags = paramInt1;
      Object localObject = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss_SSS").format(new Date(paramLong));
      localObject = new StringBuilder().append(Uri.encode(paramString)).append("@").append((String)localObject);
      if ((paramInt1 & 0x2) != 0)
      {
        paramString = ".txt";
        localObject = ((StringBuilder)localObject).append(paramString);
        if ((paramInt1 & 0x4) == 0) {
          break label190;
        }
      }
      label190:
      for (paramString = ".gz";; paramString = "")
      {
        this.file = new File(paramFile2, paramString);
        if (paramFile1.renameTo(this.file)) {
          break label196;
        }
        throw new IOException("Can't rename " + paramFile1 + " to " + this.file);
        paramString = ".dat";
        break;
      }
      label196:
      this.blocks = ((int)((this.file.length() + paramInt2 - 1L) / paramInt2));
    }
    
    public EntryFile(File paramFile, String paramString, long paramLong)
      throws IOException
    {
      this.tag = paramString;
      this.timestampMillis = paramLong;
      this.flags = 1;
      this.file = new File(paramFile, Uri.encode(paramString) + "@" + paramLong + ".lost");
      this.blocks = 0;
      new FileOutputStream(this.file).close();
    }
    
    public final int compareTo(EntryFile paramEntryFile)
    {
      if (this.timestampMillis < paramEntryFile.timestampMillis) {
        return -1;
      }
      if (this.timestampMillis > paramEntryFile.timestampMillis) {
        return 1;
      }
      if ((this.file != null) && (paramEntryFile.file != null)) {
        return this.file.compareTo(paramEntryFile.file);
      }
      if (paramEntryFile.file != null) {
        return -1;
      }
      if (this.file != null) {
        return 1;
      }
      if (this == paramEntryFile) {
        return 0;
      }
      if (hashCode() < paramEntryFile.hashCode()) {
        return -1;
      }
      if (hashCode() > paramEntryFile.hashCode()) {
        return 1;
      }
      return 0;
    }
  }
  
  private static final class FileList
    implements Comparable<FileList>
  {
    public int blocks = 0;
    public final TreeSet<DropBoxManagerService.EntryFile> contents = new TreeSet();
    
    public final int compareTo(FileList paramFileList)
    {
      if (this.blocks != paramFileList.blocks) {
        return paramFileList.blocks - this.blocks;
      }
      if (this == paramFileList) {
        return 0;
      }
      if (hashCode() < paramFileList.hashCode()) {
        return -1;
      }
      if (hashCode() > paramFileList.hashCode()) {
        return 1;
      }
      return 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/DropBoxManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */