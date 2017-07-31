package android.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.android.collect.Maps;
import dalvik.system.BlockGuard;
import dalvik.system.BlockGuard.Policy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;

final class SharedPreferencesImpl
  implements SharedPreferences
{
  private static final boolean DEBUG = false;
  private static final String TAG = "SharedPreferencesImpl";
  private static final Object mContent = new Object();
  private final File mBackupFile;
  private int mDiskWritesInFlight = 0;
  private final File mFile;
  private final WeakHashMap<SharedPreferences.OnSharedPreferenceChangeListener, Object> mListeners = new WeakHashMap();
  private boolean mLoaded = false;
  private Map<String, Object> mMap;
  private final int mMode;
  private long mStatSize;
  private long mStatTimestamp;
  private final Object mWritingToDiskLock = new Object();
  
  SharedPreferencesImpl(File paramFile, int paramInt)
  {
    this.mFile = paramFile;
    this.mBackupFile = makeBackupFile(paramFile);
    this.mMode = paramInt;
    this.mLoaded = false;
    this.mMap = null;
    startLoadFromDisk();
  }
  
  private void awaitLoadedLocked()
  {
    if (!this.mLoaded) {
      BlockGuard.getThreadPolicy().onReadFromDisk();
    }
    while (!this.mLoaded) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  private static FileOutputStream createFileOutputStream(File paramFile)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
      return localFileOutputStream;
    }
    catch (FileNotFoundException localFileNotFoundException1)
    {
      Object localObject = paramFile.getParentFile();
      if (!((File)localObject).mkdir())
      {
        Log.e("SharedPreferencesImpl", "Couldn't create directory for SharedPreferences file " + paramFile);
        return null;
      }
      FileUtils.setPermissions(((File)localObject).getPath(), 505, -1, -1);
      try
      {
        localObject = new FileOutputStream(paramFile);
        return (FileOutputStream)localObject;
      }
      catch (FileNotFoundException localFileNotFoundException2)
      {
        Log.e("SharedPreferencesImpl", "Couldn't create SharedPreferences file " + paramFile, localFileNotFoundException2);
      }
    }
    return null;
  }
  
  private void enqueueDiskWrite(final MemoryCommitResult paramMemoryCommitResult, final Runnable paramRunnable)
  {
    paramMemoryCommitResult = new Runnable()
    {
      public void run()
      {
        synchronized (SharedPreferencesImpl.-get3(SharedPreferencesImpl.this))
        {
          SharedPreferencesImpl.-wrap2(SharedPreferencesImpl.this, paramMemoryCommitResult);
        }
        synchronized (SharedPreferencesImpl.this)
        {
          SharedPreferencesImpl localSharedPreferencesImpl = SharedPreferencesImpl.this;
          SharedPreferencesImpl.-set0(localSharedPreferencesImpl, SharedPreferencesImpl.-get0(localSharedPreferencesImpl) - 1);
          if (paramRunnable != null) {
            paramRunnable.run();
          }
          return;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
      }
    };
    int i;
    if (paramRunnable == null) {
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {}
      try
      {
        i = this.mDiskWritesInFlight;
        if (i == 1)
        {
          i = 1;
          if (i == 0) {
            break label58;
          }
          return;
          i = 0;
          continue;
        }
        i = 0;
        label58:
        return;
      }
      finally {}
    }
  }
  
  /* Error */
  private boolean hasFileChangedUnexpectedly()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 52	android/app/SharedPreferencesImpl:mDiskWritesInFlight	I
    //   8: istore_1
    //   9: iload_1
    //   10: ifle +7 -> 17
    //   13: aload_0
    //   14: monitorexit
    //   15: iconst_0
    //   16: ireturn
    //   17: aload_0
    //   18: monitorexit
    //   19: invokestatic 121	dalvik/system/BlockGuard:getThreadPolicy	()Ldalvik/system/BlockGuard$Policy;
    //   22: invokeinterface 126 1 0
    //   27: aload_0
    //   28: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   31: invokevirtual 173	java/io/File:getPath	()Ljava/lang/String;
    //   34: invokestatic 212	android/system/Os:stat	(Ljava/lang/String;)Landroid/system/StructStat;
    //   37: astore 8
    //   39: aload_0
    //   40: monitorenter
    //   41: iload_3
    //   42: istore_2
    //   43: aload_0
    //   44: getfield 214	android/app/SharedPreferencesImpl:mStatTimestamp	J
    //   47: aload 8
    //   49: getfield 219	android/system/StructStat:st_mtime	J
    //   52: lcmp
    //   53: ifne +26 -> 79
    //   56: aload_0
    //   57: getfield 221	android/app/SharedPreferencesImpl:mStatSize	J
    //   60: lstore 4
    //   62: aload 8
    //   64: getfield 224	android/system/StructStat:st_size	J
    //   67: lstore 6
    //   69: lload 4
    //   71: lload 6
    //   73: lcmp
    //   74: ifeq +20 -> 94
    //   77: iload_3
    //   78: istore_2
    //   79: aload_0
    //   80: monitorexit
    //   81: iload_2
    //   82: ireturn
    //   83: astore 8
    //   85: aload_0
    //   86: monitorexit
    //   87: aload 8
    //   89: athrow
    //   90: astore 8
    //   92: iconst_1
    //   93: ireturn
    //   94: iconst_0
    //   95: istore_2
    //   96: goto -17 -> 79
    //   99: astore 8
    //   101: aload_0
    //   102: monitorexit
    //   103: aload 8
    //   105: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	SharedPreferencesImpl
    //   8	2	1	i	int
    //   42	54	2	bool1	boolean
    //   1	77	3	bool2	boolean
    //   60	10	4	l1	long
    //   67	5	6	l2	long
    //   37	26	8	localStructStat	android.system.StructStat
    //   83	5	8	localObject1	Object
    //   90	1	8	localErrnoException	android.system.ErrnoException
    //   99	5	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	9	83	finally
    //   19	39	90	android/system/ErrnoException
    //   43	69	99	finally
  }
  
  /* Error */
  private void loadFromDisk()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 96	android/app/SharedPreferencesImpl:mLoaded	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   18: invokevirtual 231	java/io/File:exists	()Z
    //   21: ifeq +23 -> 44
    //   24: aload_0
    //   25: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   28: invokevirtual 234	java/io/File:delete	()Z
    //   31: pop
    //   32: aload_0
    //   33: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   36: aload_0
    //   37: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   40: invokevirtual 238	java/io/File:renameTo	(Ljava/io/File;)Z
    //   43: pop
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_0
    //   47: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   50: invokevirtual 231	java/io/File:exists	()Z
    //   53: ifeq +13 -> 66
    //   56: aload_0
    //   57: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   60: invokevirtual 241	java/io/File:canRead	()Z
    //   63: ifeq +146 -> 209
    //   66: aconst_null
    //   67: astore 7
    //   69: aconst_null
    //   70: astore 8
    //   72: aconst_null
    //   73: astore_3
    //   74: aload 7
    //   76: astore_2
    //   77: aload_0
    //   78: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   81: invokevirtual 173	java/io/File:getPath	()Ljava/lang/String;
    //   84: invokestatic 212	android/system/Os:stat	(Ljava/lang/String;)Landroid/system/StructStat;
    //   87: astore 5
    //   89: aload 7
    //   91: astore_2
    //   92: aload 5
    //   94: astore_3
    //   95: aload_0
    //   96: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   99: invokevirtual 241	java/io/File:canRead	()Z
    //   102: istore_1
    //   103: aload 8
    //   105: astore_2
    //   106: aload 5
    //   108: astore_3
    //   109: iload_1
    //   110: ifeq +55 -> 165
    //   113: aconst_null
    //   114: astore_2
    //   115: aconst_null
    //   116: astore 4
    //   118: new 243	java/io/BufferedInputStream
    //   121: dup
    //   122: new 245	java/io/FileInputStream
    //   125: dup
    //   126: aload_0
    //   127: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   130: invokespecial 246	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   133: sipush 16384
    //   136: invokespecial 249	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;I)V
    //   139: astore 6
    //   141: aload 6
    //   143: invokestatic 255	com/android/internal/util/XmlUtils:readMapXml	(Ljava/io/InputStream;)Ljava/util/HashMap;
    //   146: astore 4
    //   148: aload 4
    //   150: astore_2
    //   151: aload 5
    //   153: astore_3
    //   154: aload 6
    //   156: invokestatic 261	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   159: aload 5
    //   161: astore_3
    //   162: aload 4
    //   164: astore_2
    //   165: aload_0
    //   166: monitorenter
    //   167: aload_0
    //   168: iconst_1
    //   169: putfield 96	android/app/SharedPreferencesImpl:mLoaded	Z
    //   172: aload_2
    //   173: ifnull +128 -> 301
    //   176: aload_0
    //   177: aload_2
    //   178: putfield 61	android/app/SharedPreferencesImpl:mMap	Ljava/util/Map;
    //   181: aload_0
    //   182: aload_3
    //   183: getfield 219	android/system/StructStat:st_mtime	J
    //   186: putfield 214	android/app/SharedPreferencesImpl:mStatTimestamp	J
    //   189: aload_0
    //   190: aload_3
    //   191: getfield 224	android/system/StructStat:st_size	J
    //   194: putfield 221	android/app/SharedPreferencesImpl:mStatSize	J
    //   197: aload_0
    //   198: invokevirtual 264	android/app/SharedPreferencesImpl:notifyAll	()V
    //   201: aload_0
    //   202: monitorexit
    //   203: return
    //   204: astore_2
    //   205: aload_0
    //   206: monitorexit
    //   207: aload_2
    //   208: athrow
    //   209: ldc 29
    //   211: new 150	java/lang/StringBuilder
    //   214: dup
    //   215: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   218: ldc_w 266
    //   221: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: aload_0
    //   225: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   228: invokevirtual 160	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   231: ldc_w 268
    //   234: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   240: invokestatic 271	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   243: pop
    //   244: goto -178 -> 66
    //   247: astore_3
    //   248: aload 4
    //   250: astore_2
    //   251: ldc 29
    //   253: ldc_w 273
    //   256: aload_3
    //   257: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   260: pop
    //   261: aload 7
    //   263: astore_2
    //   264: aload 5
    //   266: astore_3
    //   267: aload 4
    //   269: invokestatic 261	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   272: aload 8
    //   274: astore_2
    //   275: aload 5
    //   277: astore_3
    //   278: goto -113 -> 165
    //   281: aload 7
    //   283: astore_2
    //   284: aload 5
    //   286: astore_3
    //   287: aload 4
    //   289: invokestatic 261	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   292: aload 7
    //   294: astore_2
    //   295: aload 5
    //   297: astore_3
    //   298: aload 6
    //   300: athrow
    //   301: aload_0
    //   302: new 277	java/util/HashMap
    //   305: dup
    //   306: invokespecial 278	java/util/HashMap:<init>	()V
    //   309: putfield 61	android/app/SharedPreferencesImpl:mMap	Ljava/util/Map;
    //   312: goto -115 -> 197
    //   315: astore_2
    //   316: aload_0
    //   317: monitorexit
    //   318: aload_2
    //   319: athrow
    //   320: astore_2
    //   321: aload 6
    //   323: astore 4
    //   325: aload_2
    //   326: astore 6
    //   328: goto -47 -> 281
    //   331: astore_3
    //   332: aload 6
    //   334: astore 4
    //   336: goto -88 -> 248
    //   339: astore 4
    //   341: goto -176 -> 165
    //   344: astore 6
    //   346: aload_2
    //   347: astore 4
    //   349: goto -68 -> 281
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	this	SharedPreferencesImpl
    //   6	104	1	bool	boolean
    //   76	102	2	localObject1	Object
    //   204	4	2	localObject2	Object
    //   250	45	2	localObject3	Object
    //   315	4	2	localObject4	Object
    //   320	27	2	localObject5	Object
    //   73	118	3	localObject6	Object
    //   247	10	3	localXmlPullParserException1	org.xmlpull.v1.XmlPullParserException
    //   266	32	3	localObject7	Object
    //   331	1	3	localXmlPullParserException2	org.xmlpull.v1.XmlPullParserException
    //   116	219	4	localObject8	Object
    //   339	1	4	localErrnoException	android.system.ErrnoException
    //   347	1	4	localObject9	Object
    //   87	209	5	localStructStat	android.system.StructStat
    //   139	194	6	localObject10	Object
    //   344	1	6	localObject11	Object
    //   67	226	7	localObject12	Object
    //   70	203	8	localObject13	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	204	finally
    //   14	44	204	finally
    //   118	141	247	org/xmlpull/v1/XmlPullParserException
    //   118	141	247	java/io/IOException
    //   167	172	315	finally
    //   176	197	315	finally
    //   197	201	315	finally
    //   301	312	315	finally
    //   141	148	320	finally
    //   141	148	331	org/xmlpull/v1/XmlPullParserException
    //   141	148	331	java/io/IOException
    //   77	89	339	android/system/ErrnoException
    //   95	103	339	android/system/ErrnoException
    //   154	159	339	android/system/ErrnoException
    //   267	272	339	android/system/ErrnoException
    //   287	292	339	android/system/ErrnoException
    //   298	301	339	android/system/ErrnoException
    //   118	141	344	finally
    //   251	261	344	finally
  }
  
  static File makeBackupFile(File paramFile)
  {
    return new File(paramFile.getPath() + ".bak");
  }
  
  private void startLoadFromDisk()
  {
    try
    {
      this.mLoaded = false;
      new Thread("SharedPreferencesImpl-load")
      {
        public void run()
        {
          SharedPreferencesImpl.-wrap1(SharedPreferencesImpl.this);
        }
      }.start();
      return;
    }
    finally {}
  }
  
  /* Error */
  private void writeToFile(MemoryCommitResult paramMemoryCommitResult)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   4: invokevirtual 231	java/io/File:exists	()Z
    //   7: ifeq +96 -> 103
    //   10: aload_1
    //   11: getfield 294	android/app/SharedPreferencesImpl$MemoryCommitResult:changesMade	Z
    //   14: ifne +9 -> 23
    //   17: aload_1
    //   18: iconst_1
    //   19: invokevirtual 298	android/app/SharedPreferencesImpl$MemoryCommitResult:setDiskWriteResult	(Z)V
    //   22: return
    //   23: aload_0
    //   24: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   27: invokevirtual 231	java/io/File:exists	()Z
    //   30: ifne +65 -> 95
    //   33: aload_0
    //   34: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   37: aload_0
    //   38: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   41: invokevirtual 238	java/io/File:renameTo	(Ljava/io/File;)Z
    //   44: ifne +59 -> 103
    //   47: ldc 29
    //   49: new 150	java/lang/StringBuilder
    //   52: dup
    //   53: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   56: ldc_w 300
    //   59: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: aload_0
    //   63: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   66: invokevirtual 160	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   69: ldc_w 302
    //   72: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: aload_0
    //   76: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   79: invokevirtual 160	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   88: pop
    //   89: aload_1
    //   90: iconst_0
    //   91: invokevirtual 298	android/app/SharedPreferencesImpl$MemoryCommitResult:setDiskWriteResult	(Z)V
    //   94: return
    //   95: aload_0
    //   96: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   99: invokevirtual 234	java/io/File:delete	()Z
    //   102: pop
    //   103: aload_0
    //   104: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   107: invokestatic 304	android/app/SharedPreferencesImpl:createFileOutputStream	(Ljava/io/File;)Ljava/io/FileOutputStream;
    //   110: astore_2
    //   111: aload_2
    //   112: ifnonnull +9 -> 121
    //   115: aload_1
    //   116: iconst_0
    //   117: invokevirtual 298	android/app/SharedPreferencesImpl$MemoryCommitResult:setDiskWriteResult	(Z)V
    //   120: return
    //   121: aload_1
    //   122: getfield 307	android/app/SharedPreferencesImpl$MemoryCommitResult:mapToWriteToDisk	Ljava/util/Map;
    //   125: aload_2
    //   126: invokestatic 311	com/android/internal/util/XmlUtils:writeMapXml	(Ljava/util/Map;Ljava/io/OutputStream;)V
    //   129: aload_2
    //   130: invokestatic 315	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   133: pop
    //   134: aload_2
    //   135: invokevirtual 318	java/io/FileOutputStream:close	()V
    //   138: aload_0
    //   139: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   142: invokevirtual 173	java/io/File:getPath	()Ljava/lang/String;
    //   145: aload_0
    //   146: getfield 109	android/app/SharedPreferencesImpl:mMode	I
    //   149: iconst_0
    //   150: invokestatic 324	android/app/ContextImpl:setFilePermissionsFromMode	(Ljava/lang/String;II)V
    //   153: aload_0
    //   154: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   157: invokevirtual 173	java/io/File:getPath	()Ljava/lang/String;
    //   160: invokestatic 212	android/system/Os:stat	(Ljava/lang/String;)Landroid/system/StructStat;
    //   163: astore_2
    //   164: aload_0
    //   165: monitorenter
    //   166: aload_0
    //   167: aload_2
    //   168: getfield 219	android/system/StructStat:st_mtime	J
    //   171: putfield 214	android/app/SharedPreferencesImpl:mStatTimestamp	J
    //   174: aload_0
    //   175: aload_2
    //   176: getfield 224	android/system/StructStat:st_size	J
    //   179: putfield 221	android/app/SharedPreferencesImpl:mStatSize	J
    //   182: aload_0
    //   183: monitorexit
    //   184: aload_0
    //   185: getfield 107	android/app/SharedPreferencesImpl:mBackupFile	Ljava/io/File;
    //   188: invokevirtual 234	java/io/File:delete	()Z
    //   191: pop
    //   192: aload_1
    //   193: iconst_1
    //   194: invokevirtual 298	android/app/SharedPreferencesImpl$MemoryCommitResult:setDiskWriteResult	(Z)V
    //   197: return
    //   198: astore_2
    //   199: aload_0
    //   200: monitorexit
    //   201: aload_2
    //   202: athrow
    //   203: astore_2
    //   204: goto -20 -> 184
    //   207: astore_2
    //   208: ldc 29
    //   210: ldc_w 326
    //   213: aload_2
    //   214: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   217: pop
    //   218: aload_0
    //   219: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   222: invokevirtual 231	java/io/File:exists	()Z
    //   225: ifeq +42 -> 267
    //   228: aload_0
    //   229: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   232: invokevirtual 234	java/io/File:delete	()Z
    //   235: ifne +32 -> 267
    //   238: ldc 29
    //   240: new 150	java/lang/StringBuilder
    //   243: dup
    //   244: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   247: ldc_w 328
    //   250: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: aload_0
    //   254: getfield 101	android/app/SharedPreferencesImpl:mFile	Ljava/io/File;
    //   257: invokevirtual 160	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   260: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   263: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   266: pop
    //   267: aload_1
    //   268: iconst_0
    //   269: invokevirtual 298	android/app/SharedPreferencesImpl$MemoryCommitResult:setDiskWriteResult	(Z)V
    //   272: return
    //   273: astore_2
    //   274: ldc 29
    //   276: ldc_w 326
    //   279: aload_2
    //   280: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   283: pop
    //   284: goto -66 -> 218
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	287	0	this	SharedPreferencesImpl
    //   0	287	1	paramMemoryCommitResult	MemoryCommitResult
    //   110	66	2	localObject1	Object
    //   198	4	2	localObject2	Object
    //   203	1	2	localErrnoException	android.system.ErrnoException
    //   207	7	2	localIOException	java.io.IOException
    //   273	7	2	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
    // Exception table:
    //   from	to	target	type
    //   166	182	198	finally
    //   153	166	203	android/system/ErrnoException
    //   182	184	203	android/system/ErrnoException
    //   199	203	203	android/system/ErrnoException
    //   103	111	207	java/io/IOException
    //   115	120	207	java/io/IOException
    //   121	153	207	java/io/IOException
    //   153	166	207	java/io/IOException
    //   182	184	207	java/io/IOException
    //   184	197	207	java/io/IOException
    //   199	203	207	java/io/IOException
    //   103	111	273	org/xmlpull/v1/XmlPullParserException
    //   115	120	273	org/xmlpull/v1/XmlPullParserException
    //   121	153	273	org/xmlpull/v1/XmlPullParserException
    //   153	166	273	org/xmlpull/v1/XmlPullParserException
    //   182	184	273	org/xmlpull/v1/XmlPullParserException
    //   184	197	273	org/xmlpull/v1/XmlPullParserException
    //   199	203	273	org/xmlpull/v1/XmlPullParserException
  }
  
  public boolean contains(String paramString)
  {
    try
    {
      awaitLoadedLocked();
      boolean bool = this.mMap.containsKey(paramString);
      return bool;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public SharedPreferences.Editor edit()
  {
    try
    {
      awaitLoadedLocked();
      return new EditorImpl();
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Map<String, ?> getAll()
  {
    try
    {
      awaitLoadedLocked();
      HashMap localHashMap = new HashMap(this.mMap);
      return localHashMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    try
    {
      awaitLoadedLocked();
      paramString = (Boolean)this.mMap.get(paramString);
      if (paramString != null) {
        paramBoolean = paramString.booleanValue();
      }
      return paramBoolean;
    }
    finally {}
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    try
    {
      awaitLoadedLocked();
      paramString = (Float)this.mMap.get(paramString);
      if (paramString != null) {
        paramFloat = paramString.floatValue();
      }
      return paramFloat;
    }
    finally {}
  }
  
  public int getInt(String paramString, int paramInt)
  {
    try
    {
      awaitLoadedLocked();
      paramString = (Integer)this.mMap.get(paramString);
      if (paramString != null) {
        paramInt = paramString.intValue();
      }
      return paramInt;
    }
    finally {}
  }
  
  public long getLong(String paramString, long paramLong)
  {
    try
    {
      awaitLoadedLocked();
      paramString = (Long)this.mMap.get(paramString);
      if (paramString != null) {
        paramLong = paramString.longValue();
      }
      return paramLong;
    }
    finally {}
  }
  
  /* Error */
  public String getString(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 332	android/app/SharedPreferencesImpl:awaitLoadedLocked	()V
    //   6: aload_0
    //   7: getfield 61	android/app/SharedPreferencesImpl:mMap	Ljava/util/Map;
    //   10: aload_1
    //   11: invokeinterface 355 2 0
    //   16: checkcast 388	java/lang/String
    //   19: astore_1
    //   20: aload_1
    //   21: ifnull +7 -> 28
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: areturn
    //   28: aload_2
    //   29: astore_1
    //   30: goto -6 -> 24
    //   33: astore_1
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_1
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	SharedPreferencesImpl
    //   0	38	1	paramString1	String
    //   0	38	2	paramString2	String
    // Exception table:
    //   from	to	target	type
    //   2	20	33	finally
  }
  
  /* Error */
  public Set<String> getStringSet(String paramString, Set<String> paramSet)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 332	android/app/SharedPreferencesImpl:awaitLoadedLocked	()V
    //   6: aload_0
    //   7: getfield 61	android/app/SharedPreferencesImpl:mMap	Ljava/util/Map;
    //   10: aload_1
    //   11: invokeinterface 355 2 0
    //   16: checkcast 392	java/util/Set
    //   19: astore_1
    //   20: aload_1
    //   21: ifnull +7 -> 28
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: areturn
    //   28: aload_2
    //   29: astore_1
    //   30: goto -6 -> 24
    //   33: astore_1
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_1
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	SharedPreferencesImpl
    //   0	38	1	paramString	String
    //   0	38	2	paramSet	Set<String>
    // Exception table:
    //   from	to	target	type
    //   2	20	33	finally
  }
  
  public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    try
    {
      this.mListeners.put(paramOnSharedPreferenceChangeListener, mContent);
      return;
    }
    finally
    {
      paramOnSharedPreferenceChangeListener = finally;
      throw paramOnSharedPreferenceChangeListener;
    }
  }
  
  void startReloadIfChangedUnexpectedly()
  {
    try
    {
      boolean bool = hasFileChangedUnexpectedly();
      if (!bool) {
        return;
      }
      startLoadFromDisk();
      return;
    }
    finally {}
  }
  
  public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    try
    {
      this.mListeners.remove(paramOnSharedPreferenceChangeListener);
      return;
    }
    finally
    {
      paramOnSharedPreferenceChangeListener = finally;
      throw paramOnSharedPreferenceChangeListener;
    }
  }
  
  public final class EditorImpl
    implements SharedPreferences.Editor
  {
    private boolean mClear = false;
    private final Map<String, Object> mModified = Maps.newHashMap();
    
    public EditorImpl() {}
    
    private SharedPreferencesImpl.MemoryCommitResult commitToMemory()
    {
      int i = 1;
      SharedPreferencesImpl.MemoryCommitResult localMemoryCommitResult1 = new SharedPreferencesImpl.MemoryCommitResult(null);
      for (;;)
      {
        Object localObject3;
        String str;
        synchronized (SharedPreferencesImpl.this)
        {
          if (SharedPreferencesImpl.-get0(SharedPreferencesImpl.this) > 0) {
            SharedPreferencesImpl.-set1(SharedPreferencesImpl.this, new HashMap(SharedPreferencesImpl.-get2(SharedPreferencesImpl.this)));
          }
          localMemoryCommitResult1.mapToWriteToDisk = SharedPreferencesImpl.-get2(SharedPreferencesImpl.this);
          Object localObject2 = SharedPreferencesImpl.this;
          SharedPreferencesImpl.-set0((SharedPreferencesImpl)localObject2, SharedPreferencesImpl.-get0((SharedPreferencesImpl)localObject2) + 1);
          if (SharedPreferencesImpl.-get1(SharedPreferencesImpl.this).size() > 0)
          {
            if (i != 0)
            {
              localMemoryCommitResult1.keysModified = new ArrayList();
              localMemoryCommitResult1.listeners = new HashSet(SharedPreferencesImpl.-get1(SharedPreferencesImpl.this).keySet());
            }
            try
            {
              if (this.mClear)
              {
                if (!SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).isEmpty())
                {
                  localMemoryCommitResult1.changesMade = true;
                  SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).clear();
                }
                this.mClear = false;
              }
              localObject2 = this.mModified.entrySet().iterator();
              if (!((Iterator)localObject2).hasNext()) {
                break;
              }
              localObject3 = (Map.Entry)((Iterator)localObject2).next();
              str = (String)((Map.Entry)localObject3).getKey();
              localObject3 = ((Map.Entry)localObject3).getValue();
              if ((localObject3 != this) && (localObject3 != null)) {
                break label316;
              }
              if (!SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).containsKey(str)) {
                continue;
              }
              SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).remove(str);
              localMemoryCommitResult1.changesMade = true;
              if (i == 0) {
                continue;
              }
              localMemoryCommitResult1.keysModified.add(str);
              continue;
              localMemoryCommitResult2 = finally;
            }
            finally {}
          }
        }
        i = 0;
        continue;
        label316:
        if (SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).containsKey(str))
        {
          Object localObject4 = SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).get(str);
          if ((localObject4 != null) && (localObject4.equals(localObject3))) {}
        }
        else
        {
          SharedPreferencesImpl.-get2(SharedPreferencesImpl.this).put(str, localObject3);
        }
      }
      this.mModified.clear();
      return localMemoryCommitResult2;
    }
    
    private void notifyListeners(final SharedPreferencesImpl.MemoryCommitResult paramMemoryCommitResult)
    {
      if ((paramMemoryCommitResult.listeners == null) || (paramMemoryCommitResult.keysModified == null)) {}
      while (paramMemoryCommitResult.keysModified.size() == 0) {
        return;
      }
      if (Looper.myLooper() == Looper.getMainLooper())
      {
        int i = paramMemoryCommitResult.keysModified.size() - 1;
        while (i >= 0)
        {
          String str = (String)paramMemoryCommitResult.keysModified.get(i);
          Iterator localIterator = paramMemoryCommitResult.listeners.iterator();
          while (localIterator.hasNext())
          {
            SharedPreferences.OnSharedPreferenceChangeListener localOnSharedPreferenceChangeListener = (SharedPreferences.OnSharedPreferenceChangeListener)localIterator.next();
            if (localOnSharedPreferenceChangeListener != null) {
              localOnSharedPreferenceChangeListener.onSharedPreferenceChanged(SharedPreferencesImpl.this, str);
            }
          }
          i -= 1;
        }
      }
      ActivityThread.sMainThreadHandler.post(new Runnable()
      {
        public void run()
        {
          SharedPreferencesImpl.EditorImpl.-wrap0(SharedPreferencesImpl.EditorImpl.this, paramMemoryCommitResult);
        }
      });
    }
    
    public void apply()
    {
      final SharedPreferencesImpl.MemoryCommitResult localMemoryCommitResult = commitToMemory();
      final Object localObject = new Runnable()
      {
        public void run()
        {
          try
          {
            localMemoryCommitResult.writtenToDiskLatch.await();
            return;
          }
          catch (InterruptedException localInterruptedException) {}
        }
      };
      QueuedWork.add((Runnable)localObject);
      localObject = new Runnable()
      {
        public void run()
        {
          localObject.run();
          QueuedWork.remove(localObject);
        }
      };
      SharedPreferencesImpl.-wrap0(SharedPreferencesImpl.this, localMemoryCommitResult, (Runnable)localObject);
      notifyListeners(localMemoryCommitResult);
    }
    
    public SharedPreferences.Editor clear()
    {
      try
      {
        this.mClear = true;
        return this;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public boolean commit()
    {
      SharedPreferencesImpl.MemoryCommitResult localMemoryCommitResult = commitToMemory();
      SharedPreferencesImpl.-wrap0(SharedPreferencesImpl.this, localMemoryCommitResult, null);
      try
      {
        localMemoryCommitResult.writtenToDiskLatch.await();
        notifyListeners(localMemoryCommitResult);
        return localMemoryCommitResult.writeToDiskResult;
      }
      catch (InterruptedException localInterruptedException) {}
      return false;
    }
    
    public SharedPreferences.Editor putBoolean(String paramString, boolean paramBoolean)
    {
      try
      {
        this.mModified.put(paramString, Boolean.valueOf(paramBoolean));
        return this;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public SharedPreferences.Editor putFloat(String paramString, float paramFloat)
    {
      try
      {
        this.mModified.put(paramString, Float.valueOf(paramFloat));
        return this;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public SharedPreferences.Editor putInt(String paramString, int paramInt)
    {
      try
      {
        this.mModified.put(paramString, Integer.valueOf(paramInt));
        return this;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public SharedPreferences.Editor putLong(String paramString, long paramLong)
    {
      try
      {
        this.mModified.put(paramString, Long.valueOf(paramLong));
        return this;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public SharedPreferences.Editor putString(String paramString1, String paramString2)
    {
      try
      {
        this.mModified.put(paramString1, paramString2);
        return this;
      }
      finally
      {
        paramString1 = finally;
        throw paramString1;
      }
    }
    
    /* Error */
    public SharedPreferences.Editor putStringSet(String paramString, Set<String> paramSet)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: aload_0
      //   3: monitorenter
      //   4: aload_0
      //   5: getfield 44	android/app/SharedPreferencesImpl$EditorImpl:mModified	Ljava/util/Map;
      //   8: astore 4
      //   10: aload_2
      //   11: ifnonnull +19 -> 30
      //   14: aload_3
      //   15: astore_2
      //   16: aload 4
      //   18: aload_1
      //   19: aload_2
      //   20: invokeinterface 169 3 0
      //   25: pop
      //   26: aload_0
      //   27: monitorexit
      //   28: aload_0
      //   29: areturn
      //   30: new 95	java/util/HashSet
      //   33: dup
      //   34: aload_2
      //   35: invokespecial 102	java/util/HashSet:<init>	(Ljava/util/Collection;)V
      //   38: astore_2
      //   39: goto -23 -> 16
      //   42: astore_1
      //   43: aload_0
      //   44: monitorexit
      //   45: aload_1
      //   46: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	47	0	this	EditorImpl
      //   0	47	1	paramString	String
      //   0	47	2	paramSet	Set<String>
      //   1	14	3	localObject	Object
      //   8	9	4	localMap	Map
      // Exception table:
      //   from	to	target	type
      //   4	10	42	finally
      //   16	26	42	finally
      //   30	39	42	finally
    }
    
    public SharedPreferences.Editor remove(String paramString)
    {
      try
      {
        this.mModified.put(paramString, this);
        return this;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
  }
  
  private static class MemoryCommitResult
  {
    public boolean changesMade;
    public List<String> keysModified;
    public Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
    public Map<?, ?> mapToWriteToDisk;
    public volatile boolean writeToDiskResult = false;
    public final CountDownLatch writtenToDiskLatch = new CountDownLatch(1);
    
    public void setDiskWriteResult(boolean paramBoolean)
    {
      this.writeToDiskResult = paramBoolean;
      this.writtenToDiskLatch.countDown();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SharedPreferencesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */