package com.android.server.am;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileUtils;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.util.FastXmlSerializer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class TaskPersister
{
  static final boolean DEBUG = false;
  private static final long FLUSH_QUEUE = -1L;
  private static final String IMAGES_DIRNAME = "recent_images";
  static final String IMAGE_EXTENSION = ".png";
  private static final long INTER_WRITE_DELAY_MS = 500L;
  private static final int MAX_WRITE_QUEUE_LENGTH = 6;
  private static final String PERSISTED_TASK_IDS_FILENAME = "persisted_taskIds.txt";
  private static final long PRE_TASK_DELAY_MS = 3000L;
  private static final String RECENTS_FILENAME = "_task";
  static final String TAG = "TaskPersister";
  private static final String TAG_TASK = "task";
  private static final String TASKS_DIRNAME = "recent_tasks";
  private static final String TASK_EXTENSION = ".xml";
  private final Object mIoLock = new Object();
  private final LazyTaskWriterThread mLazyTaskWriterThread;
  private long mNextWriteTime = 0L;
  private final RecentTasks mRecentTasks;
  private final ActivityManagerService mService;
  private final ActivityStackSupervisor mStackSupervisor;
  private final File mTaskIdsDir;
  private final SparseArray<SparseBooleanArray> mTaskIdsInFile = new SparseArray();
  ArrayList<WriteQueueItem> mWriteQueue = new ArrayList();
  
  TaskPersister(File paramFile)
  {
    this.mTaskIdsDir = paramFile;
    this.mStackSupervisor = null;
    this.mService = null;
    this.mRecentTasks = null;
    this.mLazyTaskWriterThread = new LazyTaskWriterThread("LazyTaskWriterThreadTest");
  }
  
  TaskPersister(File paramFile, ActivityStackSupervisor paramActivityStackSupervisor, ActivityManagerService paramActivityManagerService, RecentTasks paramRecentTasks)
  {
    File localFile = new File(paramFile, "recent_images");
    if ((!localFile.exists()) || ((FileUtils.deleteContents(localFile)) && (localFile.delete())))
    {
      paramFile = new File(paramFile, "recent_tasks");
      if ((paramFile.exists()) && ((!FileUtils.deleteContents(paramFile)) || (!paramFile.delete()))) {
        break label186;
      }
    }
    for (;;)
    {
      this.mTaskIdsDir = new File(Environment.getDataDirectory(), "system_de");
      this.mStackSupervisor = paramActivityStackSupervisor;
      this.mService = paramActivityManagerService;
      this.mRecentTasks = paramRecentTasks;
      this.mLazyTaskWriterThread = new LazyTaskWriterThread("LazyTaskWriterThread");
      return;
      Slog.i("TaskPersister", "Failure deleting legacy images directory: " + localFile);
      break;
      label186:
      Slog.i("TaskPersister", "Failure deleting legacy tasks directory: " + paramFile);
    }
  }
  
  private static boolean createParentDirectory(String paramString)
  {
    paramString = new File(paramString).getParentFile();
    if (!paramString.exists()) {
      return paramString.mkdirs();
    }
    return true;
  }
  
  private String fileToString(File paramFile)
  {
    String str1 = System.lineSeparator();
    StringBuffer localStringBuffer;
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramFile));
      localStringBuffer = new StringBuffer((int)paramFile.length() * 2);
      for (;;)
      {
        String str3 = localBufferedReader.readLine();
        if (str3 == null) {
          break;
        }
        localStringBuffer.append(str3 + str1);
      }
      localBufferedReader.close();
    }
    catch (IOException localIOException)
    {
      Slog.e("TaskPersister", "Couldn't read file " + paramFile.getName());
      return null;
    }
    String str2 = localStringBuffer.toString();
    return str2;
  }
  
  static File getUserImagesDir(int paramInt)
  {
    return new File(Environment.getDataSystemCeDirectory(paramInt), "recent_images");
  }
  
  private File getUserPersistedTaskIdsFile(int paramInt)
  {
    File localFile = new File(this.mTaskIdsDir, String.valueOf(paramInt));
    if ((localFile.exists()) || (localFile.mkdirs())) {}
    for (;;)
    {
      return new File(localFile, "persisted_taskIds.txt");
      Slog.e("TaskPersister", "Error while creating user directory: " + localFile);
    }
  }
  
  static File getUserTasksDir(int paramInt)
  {
    File localFile = new File(Environment.getDataSystemCeDirectory(paramInt), "recent_tasks");
    if ((!localFile.exists()) && (!localFile.mkdir())) {
      Slog.e("TaskPersister", "Failure creating tasks directory for user " + paramInt + ": " + localFile);
    }
    return localFile;
  }
  
  private void removeObsoleteFiles(ArraySet<Integer> paramArraySet)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      int[] arrayOfInt = this.mRecentTasks.usersWithRecentsLoadedLocked();
      ActivityManagerService.resetPriorityAfterLockedSection();
      int i = 0;
      int j = arrayOfInt.length;
      if (i < j)
      {
        int k = arrayOfInt[i];
        removeObsoleteFiles(paramArraySet, getUserImagesDir(k).listFiles());
        removeObsoleteFiles(paramArraySet, getUserTasksDir(k).listFiles());
        i += 1;
      }
    }
  }
  
  private static void removeObsoleteFiles(ArraySet<Integer> paramArraySet, File[] paramArrayOfFile)
  {
    if (paramArrayOfFile == null)
    {
      Slog.e("TaskPersister", "File error accessing recents directory (directory doesn't exist?).");
      return;
    }
    int i = 0;
    File localFile;
    while (i < paramArrayOfFile.length)
    {
      localFile = paramArrayOfFile[i];
      String str = localFile.getName();
      int j = str.indexOf('_');
      if (j > 0) {}
      try
      {
        j = Integer.parseInt(str.substring(0, j));
        if (!paramArraySet.contains(Integer.valueOf(j))) {
          localFile.delete();
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Slog.wtf("TaskPersister", "removeObsoleteFiles: Can't parse file=" + localFile.getName());
          localFile.delete();
        }
      }
      i += 1;
    }
  }
  
  private void removeThumbnails(TaskRecord paramTaskRecord)
  {
    paramTaskRecord = Integer.toString(paramTaskRecord.taskId);
    int i = this.mWriteQueue.size() - 1;
    while (i >= 0)
    {
      WriteQueueItem localWriteQueueItem = (WriteQueueItem)this.mWriteQueue.get(i);
      if (((localWriteQueueItem instanceof ImageWriteQueueItem)) && (new File(((ImageWriteQueueItem)localWriteQueueItem).mFilePath).getName().startsWith(paramTaskRecord))) {
        this.mWriteQueue.remove(i);
      }
      i -= 1;
    }
  }
  
  static Bitmap restoreImage(String paramString)
  {
    return BitmapFactory.decodeFile(paramString);
  }
  
  private StringWriter saveToXml(TaskRecord paramTaskRecord)
    throws IOException, XmlPullParserException
  {
    FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
    StringWriter localStringWriter = new StringWriter();
    localFastXmlSerializer.setOutput(localStringWriter);
    localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
    localFastXmlSerializer.startTag(null, "task");
    paramTaskRecord.saveToXml(localFastXmlSerializer);
    localFastXmlSerializer.endTag(null, "task");
    localFastXmlSerializer.endDocument();
    localFastXmlSerializer.flush();
    return localStringWriter;
  }
  
  private TaskRecord taskIdToTask(int paramInt, ArrayList<TaskRecord> paramArrayList)
  {
    if (paramInt < 0) {
      return null;
    }
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      TaskRecord localTaskRecord = (TaskRecord)paramArrayList.get(i);
      if (localTaskRecord.taskId == paramInt) {
        return localTaskRecord;
      }
      i -= 1;
    }
    Slog.e("TaskPersister", "Restore affiliation error looking for taskId=" + paramInt);
    return null;
  }
  
  private void writeTaskIdsFiles()
  {
    SparseArray localSparseArray = new SparseArray();
    for (;;)
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int[] arrayOfInt = this.mRecentTasks.usersWithRecentsLoadedLocked();
        i = 0;
        int j = arrayOfInt.length;
        if (i < j)
        {
          int k = arrayOfInt[i];
          SparseBooleanArray localSparseBooleanArray1 = (SparseBooleanArray)this.mRecentTasks.mPersistedTaskIds.get(k);
          SparseBooleanArray localSparseBooleanArray2 = (SparseBooleanArray)this.mTaskIdsInFile.get(k);
          if ((localSparseBooleanArray2 != null) && (localSparseBooleanArray2.equals(localSparseBooleanArray1))) {
            break label176;
          }
          localSparseBooleanArray1 = localSparseBooleanArray1.clone();
          this.mTaskIdsInFile.put(k, localSparseBooleanArray1);
          localSparseArray.put(k, localSparseBooleanArray1);
        }
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      int i = 0;
      while (i < ((SparseArray)localObject).size())
      {
        writePersistedTaskIdsForUser((SparseBooleanArray)((SparseArray)localObject).valueAt(i), ((SparseArray)localObject).keyAt(i));
        i += 1;
      }
      return;
      label176:
      i += 1;
    }
  }
  
  private void yieldIfQueueTooDeep()
  {
    int i = 0;
    try
    {
      long l = this.mNextWriteTime;
      if (l == -1L) {
        i = 1;
      }
      if (i != 0) {
        Thread.yield();
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  void flush()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: ldc2_w 24
    //   6: putfield 82	com/android/server/am/TaskPersister:mNextWriteTime	J
    //   9: aload_0
    //   10: invokevirtual 451	com/android/server/am/TaskPersister:notifyAll	()V
    //   13: aload_0
    //   14: invokevirtual 454	com/android/server/am/TaskPersister:wait	()V
    //   17: aload_0
    //   18: getfield 82	com/android/server/am/TaskPersister:mNextWriteTime	J
    //   21: lstore_1
    //   22: lload_1
    //   23: ldc2_w 24
    //   26: lcmp
    //   27: ifeq -14 -> 13
    //   30: aload_0
    //   31: monitorexit
    //   32: return
    //   33: astore_3
    //   34: goto -17 -> 17
    //   37: astore_3
    //   38: aload_0
    //   39: monitorexit
    //   40: aload_3
    //   41: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	TaskPersister
    //   21	2	1	l	long
    //   33	1	3	localInterruptedException	InterruptedException
    //   37	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	33	java/lang/InterruptedException
    //   2	13	37	finally
    //   13	17	37	finally
    //   17	22	37	finally
  }
  
  Bitmap getImageFromWriteQueue(String paramString)
  {
    try
    {
      int i = this.mWriteQueue.size() - 1;
      while (i >= 0)
      {
        Object localObject = (WriteQueueItem)this.mWriteQueue.get(i);
        if ((localObject instanceof ImageWriteQueueItem))
        {
          localObject = (ImageWriteQueueItem)localObject;
          if (((ImageWriteQueueItem)localObject).mFilePath.equals(paramString))
          {
            paramString = ((ImageWriteQueueItem)localObject).mImage;
            return paramString;
          }
        }
        i -= 1;
      }
      return null;
    }
    finally {}
  }
  
  Bitmap getTaskDescriptionIcon(String paramString)
  {
    Bitmap localBitmap = getImageFromWriteQueue(paramString);
    if (localBitmap != null) {
      return localBitmap;
    }
    return restoreImage(paramString);
  }
  
  /* Error */
  SparseBooleanArray loadPersistedTaskIdsForUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 125	com/android/server/am/TaskPersister:mTaskIdsInFile	Landroid/util/SparseArray;
    //   4: iload_1
    //   5: invokevirtual 416	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   8: ifnull +18 -> 26
    //   11: aload_0
    //   12: getfield 125	com/android/server/am/TaskPersister:mTaskIdsInFile	Landroid/util/SparseArray;
    //   15: iload_1
    //   16: invokevirtual 416	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   19: checkcast 418	android/util/SparseBooleanArray
    //   22: invokevirtual 425	android/util/SparseBooleanArray:clone	()Landroid/util/SparseBooleanArray;
    //   25: areturn
    //   26: new 418	android/util/SparseBooleanArray
    //   29: dup
    //   30: invokespecial 470	android/util/SparseBooleanArray:<init>	()V
    //   33: astore 9
    //   35: aload_0
    //   36: getfield 127	com/android/server/am/TaskPersister:mIoLock	Ljava/lang/Object;
    //   39: astore 8
    //   41: aload 8
    //   43: monitorenter
    //   44: aconst_null
    //   45: astore 7
    //   47: aconst_null
    //   48: astore 5
    //   50: aconst_null
    //   51: astore 6
    //   53: new 215	java/io/BufferedReader
    //   56: dup
    //   57: new 217	java/io/FileReader
    //   60: dup
    //   61: aload_0
    //   62: iload_1
    //   63: invokespecial 472	com/android/server/am/TaskPersister:getUserPersistedTaskIdsFile	(I)Ljava/io/File;
    //   66: invokespecial 219	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   69: invokespecial 222	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   72: astore 4
    //   74: aload 4
    //   76: invokevirtual 234	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   79: astore 5
    //   81: aload 5
    //   83: ifnull +44 -> 127
    //   86: aload 5
    //   88: ldc_w 474
    //   91: invokevirtual 478	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   94: astore 5
    //   96: iconst_0
    //   97: istore_2
    //   98: aload 5
    //   100: arraylength
    //   101: istore_3
    //   102: iload_2
    //   103: iload_3
    //   104: if_icmpge -30 -> 74
    //   107: aload 9
    //   109: aload 5
    //   111: iload_2
    //   112: aaload
    //   113: invokestatic 319	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   116: iconst_1
    //   117: invokevirtual 481	android/util/SparseBooleanArray:put	(IZ)V
    //   120: iload_2
    //   121: iconst_1
    //   122: iadd
    //   123: istore_2
    //   124: goto -22 -> 102
    //   127: aload 4
    //   129: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   132: aload 8
    //   134: monitorexit
    //   135: aload_0
    //   136: getfield 125	com/android/server/am/TaskPersister:mTaskIdsInFile	Landroid/util/SparseArray;
    //   139: iload_1
    //   140: aload 9
    //   142: invokevirtual 429	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   145: aload 9
    //   147: invokevirtual 425	android/util/SparseBooleanArray:clone	()Landroid/util/SparseBooleanArray;
    //   150: areturn
    //   151: astore 5
    //   153: aload 6
    //   155: astore 4
    //   157: aload 5
    //   159: astore 6
    //   161: aload 4
    //   163: astore 5
    //   165: ldc 50
    //   167: new 173	java/lang/StringBuilder
    //   170: dup
    //   171: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   174: ldc_w 489
    //   177: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: iload_1
    //   181: invokevirtual 272	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   184: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: aload 6
    //   189: invokestatic 492	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   192: pop
    //   193: aload 4
    //   195: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   198: goto -66 -> 132
    //   201: astore 4
    //   203: aload 8
    //   205: monitorexit
    //   206: aload 4
    //   208: athrow
    //   209: astore 4
    //   211: aload 7
    //   213: astore 4
    //   215: aload 4
    //   217: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   220: goto -88 -> 132
    //   223: aload 5
    //   225: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   228: aload 4
    //   230: athrow
    //   231: astore 4
    //   233: goto -30 -> 203
    //   236: astore 6
    //   238: aload 4
    //   240: astore 5
    //   242: aload 6
    //   244: astore 4
    //   246: goto -23 -> 223
    //   249: astore 5
    //   251: goto -36 -> 215
    //   254: astore 6
    //   256: goto -95 -> 161
    //   259: astore 4
    //   261: goto -38 -> 223
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	264	0	this	TaskPersister
    //   0	264	1	paramInt	int
    //   97	27	2	i	int
    //   101	4	3	j	int
    //   72	122	4	localObject1	Object
    //   201	6	4	localObject2	Object
    //   209	1	4	localFileNotFoundException1	java.io.FileNotFoundException
    //   213	16	4	localObject3	Object
    //   231	8	4	localObject4	Object
    //   244	1	4	localObject5	Object
    //   259	1	4	localObject6	Object
    //   48	62	5	localObject7	Object
    //   151	7	5	localException1	Exception
    //   163	78	5	localObject8	Object
    //   249	1	5	localFileNotFoundException2	java.io.FileNotFoundException
    //   51	137	6	localException2	Exception
    //   236	7	6	localObject9	Object
    //   254	1	6	localException3	Exception
    //   45	167	7	localObject10	Object
    //   39	165	8	localObject11	Object
    //   33	113	9	localSparseBooleanArray	SparseBooleanArray
    // Exception table:
    //   from	to	target	type
    //   53	74	151	java/lang/Exception
    //   193	198	201	finally
    //   215	220	201	finally
    //   223	231	201	finally
    //   53	74	209	java/io/FileNotFoundException
    //   127	132	231	finally
    //   74	81	236	finally
    //   86	96	236	finally
    //   98	102	236	finally
    //   107	120	236	finally
    //   74	81	249	java/io/FileNotFoundException
    //   86	96	249	java/io/FileNotFoundException
    //   98	102	249	java/io/FileNotFoundException
    //   107	120	249	java/io/FileNotFoundException
    //   74	81	254	java/lang/Exception
    //   86	96	254	java/lang/Exception
    //   98	102	254	java/lang/Exception
    //   107	120	254	java/lang/Exception
    //   53	74	259	finally
    //   165	193	259	finally
  }
  
  /* Error */
  java.util.List<TaskRecord> restoreTasksForUserLocked(int paramInt)
  {
    // Byte code:
    //   0: new 129	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 130	java/util/ArrayList:<init>	()V
    //   7: astore 9
    //   9: new 324	android/util/ArraySet
    //   12: dup
    //   13: invokespecial 495	android/util/ArraySet:<init>	()V
    //   16: astore 10
    //   18: iload_1
    //   19: invokestatic 299	com/android/server/am/TaskPersister:getUserTasksDir	(I)Ljava/io/File;
    //   22: astore 11
    //   24: aload 11
    //   26: invokevirtual 294	java/io/File:listFiles	()[Ljava/io/File;
    //   29: astore 12
    //   31: aload 12
    //   33: ifnonnull +33 -> 66
    //   36: ldc 50
    //   38: new 173	java/lang/StringBuilder
    //   41: dup
    //   42: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   45: ldc_w 497
    //   48: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload 11
    //   53: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   56: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   59: invokestatic 245	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   62: pop
    //   63: aload 9
    //   65: areturn
    //   66: iconst_0
    //   67: istore_2
    //   68: iload_2
    //   69: aload 12
    //   71: arraylength
    //   72: if_icmpge +481 -> 553
    //   75: aload 12
    //   77: iload_2
    //   78: aaload
    //   79: astore 8
    //   81: aconst_null
    //   82: astore 4
    //   84: aconst_null
    //   85: astore 7
    //   87: new 215	java/io/BufferedReader
    //   90: dup
    //   91: new 217	java/io/FileReader
    //   94: dup
    //   95: aload 8
    //   97: invokespecial 219	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   100: invokespecial 222	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   103: astore 5
    //   105: invokestatic 503	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   108: astore 4
    //   110: aload 4
    //   112: aload 5
    //   114: invokeinterface 508 2 0
    //   119: aload 4
    //   121: invokeinterface 511 1 0
    //   126: istore_3
    //   127: iload_3
    //   128: iconst_1
    //   129: if_icmpeq +406 -> 535
    //   132: iload_3
    //   133: iconst_3
    //   134: if_icmpeq +401 -> 535
    //   137: aload 4
    //   139: invokeinterface 512 1 0
    //   144: astore 6
    //   146: iload_3
    //   147: iconst_2
    //   148: if_icmpne +80 -> 228
    //   151: ldc 53
    //   153: aload 6
    //   155: invokevirtual 456	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   158: ifeq +337 -> 495
    //   161: aload 4
    //   163: aload_0
    //   164: getfield 136	com/android/server/am/TaskPersister:mStackSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   167: invokestatic 516	com/android/server/am/TaskRecord:restoreFromXml	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/am/ActivityStackSupervisor;)Lcom/android/server/am/TaskRecord;
    //   170: astore 6
    //   172: aload 6
    //   174: ifnull +276 -> 450
    //   177: aload 6
    //   179: getfield 341	com/android/server/am/TaskRecord:taskId	I
    //   182: istore_3
    //   183: aload_0
    //   184: getfield 136	com/android/server/am/TaskPersister:mStackSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   187: iload_3
    //   188: iconst_0
    //   189: iconst_0
    //   190: invokevirtual 522	com/android/server/am/ActivityStackSupervisor:anyTaskForIdLocked	(IZI)Lcom/android/server/am/TaskRecord;
    //   193: ifnull +141 -> 334
    //   196: ldc 50
    //   198: new 173	java/lang/StringBuilder
    //   201: dup
    //   202: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   205: ldc_w 524
    //   208: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: iload_3
    //   212: invokevirtual 272	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   215: ldc_w 526
    //   218: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   224: invokestatic 333	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   227: pop
    //   228: aload 4
    //   230: invokestatic 532	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   233: goto -114 -> 119
    //   236: astore 6
    //   238: aload 5
    //   240: astore 4
    //   242: ldc 50
    //   244: new 173	java/lang/StringBuilder
    //   247: dup
    //   248: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   251: ldc_w 534
    //   254: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   257: aload 8
    //   259: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   262: ldc_w 536
    //   265: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   268: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   271: aload 6
    //   273: invokestatic 538	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   276: pop
    //   277: aload 5
    //   279: astore 4
    //   281: ldc 50
    //   283: new 173	java/lang/StringBuilder
    //   286: dup
    //   287: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   290: ldc_w 540
    //   293: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: aload_0
    //   297: aload 8
    //   299: invokespecial 542	com/android/server/am/TaskPersister:fileToString	(Ljava/io/File;)Ljava/lang/String;
    //   302: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   308: invokestatic 245	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   311: pop
    //   312: aload 5
    //   314: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   317: iconst_1
    //   318: ifeq +9 -> 327
    //   321: aload 8
    //   323: invokevirtual 162	java/io/File:delete	()Z
    //   326: pop
    //   327: iload_2
    //   328: iconst_1
    //   329: iadd
    //   330: istore_2
    //   331: goto -263 -> 68
    //   334: iload_1
    //   335: aload 6
    //   337: getfield 545	com/android/server/am/TaskRecord:userId	I
    //   340: if_icmpeq +74 -> 414
    //   343: ldc 50
    //   345: new 173	java/lang/StringBuilder
    //   348: dup
    //   349: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   352: ldc_w 547
    //   355: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: aload 6
    //   360: getfield 545	com/android/server/am/TaskRecord:userId	I
    //   363: invokevirtual 272	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   366: ldc_w 549
    //   369: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   372: aload 11
    //   374: invokevirtual 552	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   377: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   380: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   383: invokestatic 333	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   386: pop
    //   387: goto -159 -> 228
    //   390: astore 6
    //   392: aload 5
    //   394: astore 4
    //   396: aload 4
    //   398: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   401: iconst_0
    //   402: ifeq +9 -> 411
    //   405: aload 8
    //   407: invokevirtual 162	java/io/File:delete	()Z
    //   410: pop
    //   411: aload 6
    //   413: athrow
    //   414: aload_0
    //   415: getfield 136	com/android/server/am/TaskPersister:mStackSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   418: iload_3
    //   419: iload_1
    //   420: invokevirtual 556	com/android/server/am/ActivityStackSupervisor:setNextTaskIdForUserLocked	(II)V
    //   423: aload 6
    //   425: iconst_1
    //   426: putfield 559	com/android/server/am/TaskRecord:isPersistable	Z
    //   429: aload 9
    //   431: aload 6
    //   433: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   436: pop
    //   437: aload 10
    //   439: iload_3
    //   440: invokestatic 322	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   443: invokevirtual 563	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   446: pop
    //   447: goto -219 -> 228
    //   450: ldc 50
    //   452: new 173	java/lang/StringBuilder
    //   455: dup
    //   456: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   459: ldc_w 565
    //   462: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   465: aload 8
    //   467: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   470: ldc_w 274
    //   473: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   476: aload_0
    //   477: aload 8
    //   479: invokespecial 542	com/android/server/am/TaskPersister:fileToString	(Ljava/io/File;)Ljava/lang/String;
    //   482: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   485: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   488: invokestatic 245	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   491: pop
    //   492: goto -264 -> 228
    //   495: ldc 50
    //   497: new 173	java/lang/StringBuilder
    //   500: dup
    //   501: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   504: ldc_w 567
    //   507: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   510: iload_3
    //   511: invokevirtual 272	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   514: ldc_w 569
    //   517: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   520: aload 6
    //   522: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   525: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   528: invokestatic 333	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   531: pop
    //   532: goto -304 -> 228
    //   535: aload 5
    //   537: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   540: iconst_0
    //   541: ifeq +9 -> 550
    //   544: aload 8
    //   546: invokevirtual 162	java/io/File:delete	()Z
    //   549: pop
    //   550: goto -223 -> 327
    //   553: aload 10
    //   555: aload 11
    //   557: invokevirtual 294	java/io/File:listFiles	()[Ljava/io/File;
    //   560: invokestatic 297	com/android/server/am/TaskPersister:removeObsoleteFiles	(Landroid/util/ArraySet;[Ljava/io/File;)V
    //   563: aload 9
    //   565: invokevirtual 347	java/util/ArrayList:size	()I
    //   568: iconst_1
    //   569: isub
    //   570: istore_1
    //   571: iload_1
    //   572: iflt +53 -> 625
    //   575: aload 9
    //   577: iload_1
    //   578: invokevirtual 351	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   581: checkcast 338	com/android/server/am/TaskRecord
    //   584: astore 4
    //   586: aload 4
    //   588: aload_0
    //   589: aload 4
    //   591: getfield 572	com/android/server/am/TaskRecord:mPrevAffiliateTaskId	I
    //   594: aload 9
    //   596: invokespecial 574	com/android/server/am/TaskPersister:taskIdToTask	(ILjava/util/ArrayList;)Lcom/android/server/am/TaskRecord;
    //   599: invokevirtual 577	com/android/server/am/TaskRecord:setPrevAffiliate	(Lcom/android/server/am/TaskRecord;)V
    //   602: aload 4
    //   604: aload_0
    //   605: aload 4
    //   607: getfield 580	com/android/server/am/TaskRecord:mNextAffiliateTaskId	I
    //   610: aload 9
    //   612: invokespecial 574	com/android/server/am/TaskPersister:taskIdToTask	(ILjava/util/ArrayList;)Lcom/android/server/am/TaskRecord;
    //   615: invokevirtual 583	com/android/server/am/TaskRecord:setNextAffiliate	(Lcom/android/server/am/TaskRecord;)V
    //   618: iload_1
    //   619: iconst_1
    //   620: isub
    //   621: istore_1
    //   622: goto -51 -> 571
    //   625: aload 9
    //   627: new 6	com/android/server/am/TaskPersister$1
    //   630: dup
    //   631: aload_0
    //   632: invokespecial 585	com/android/server/am/TaskPersister$1:<init>	(Lcom/android/server/am/TaskPersister;)V
    //   635: invokestatic 591	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   638: aload 9
    //   640: areturn
    //   641: astore 6
    //   643: goto -247 -> 396
    //   646: astore 6
    //   648: aload 7
    //   650: astore 5
    //   652: goto -414 -> 238
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	655	0	this	TaskPersister
    //   0	655	1	paramInt	int
    //   67	264	2	i	int
    //   126	385	3	j	int
    //   82	524	4	localObject1	Object
    //   103	548	5	localObject2	Object
    //   144	34	6	localObject3	Object
    //   236	123	6	localException1	Exception
    //   390	131	6	localObject4	Object
    //   641	1	6	localObject5	Object
    //   646	1	6	localException2	Exception
    //   85	564	7	localObject6	Object
    //   79	466	8	localFile1	File
    //   7	632	9	localArrayList	ArrayList
    //   16	538	10	localArraySet	ArraySet
    //   22	534	11	localFile2	File
    //   29	47	12	arrayOfFile	File[]
    // Exception table:
    //   from	to	target	type
    //   105	119	236	java/lang/Exception
    //   119	127	236	java/lang/Exception
    //   137	146	236	java/lang/Exception
    //   151	172	236	java/lang/Exception
    //   177	228	236	java/lang/Exception
    //   228	233	236	java/lang/Exception
    //   334	387	236	java/lang/Exception
    //   414	447	236	java/lang/Exception
    //   450	492	236	java/lang/Exception
    //   495	532	236	java/lang/Exception
    //   105	119	390	finally
    //   119	127	390	finally
    //   137	146	390	finally
    //   151	172	390	finally
    //   177	228	390	finally
    //   228	233	390	finally
    //   334	387	390	finally
    //   414	447	390	finally
    //   450	492	390	finally
    //   495	532	390	finally
    //   87	105	641	finally
    //   242	277	641	finally
    //   281	312	641	finally
    //   87	105	646	java/lang/Exception
  }
  
  /* Error */
  void saveImage(Bitmap paramBitmap, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 132	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
    //   6: invokevirtual 347	java/util/ArrayList:size	()I
    //   9: iconst_1
    //   10: isub
    //   11: istore_3
    //   12: iload_3
    //   13: iflt +49 -> 62
    //   16: aload_0
    //   17: getfield 132	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
    //   20: iload_3
    //   21: invokevirtual 351	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   24: checkcast 17	com/android/server/am/TaskPersister$WriteQueueItem
    //   27: astore 4
    //   29: aload 4
    //   31: instanceof 8
    //   34: ifeq +79 -> 113
    //   37: aload 4
    //   39: checkcast 8	com/android/server/am/TaskPersister$ImageWriteQueueItem
    //   42: astore 4
    //   44: aload 4
    //   46: getfield 354	com/android/server/am/TaskPersister$ImageWriteQueueItem:mFilePath	Ljava/lang/String;
    //   49: aload_2
    //   50: invokevirtual 456	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifeq +60 -> 113
    //   56: aload 4
    //   58: aload_1
    //   59: putfield 460	com/android/server/am/TaskPersister$ImageWriteQueueItem:mImage	Landroid/graphics/Bitmap;
    //   62: iload_3
    //   63: ifge +20 -> 83
    //   66: aload_0
    //   67: getfield 132	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
    //   70: new 8	com/android/server/am/TaskPersister$ImageWriteQueueItem
    //   73: dup
    //   74: aload_2
    //   75: aload_1
    //   76: invokespecial 597	com/android/server/am/TaskPersister$ImageWriteQueueItem:<init>	(Ljava/lang/String;Landroid/graphics/Bitmap;)V
    //   79: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   82: pop
    //   83: aload_0
    //   84: getfield 132	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
    //   87: invokevirtual 347	java/util/ArrayList:size	()I
    //   90: bipush 6
    //   92: if_icmple +28 -> 120
    //   95: aload_0
    //   96: ldc2_w 24
    //   99: putfield 82	com/android/server/am/TaskPersister:mNextWriteTime	J
    //   102: aload_0
    //   103: invokevirtual 451	com/android/server/am/TaskPersister:notifyAll	()V
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_0
    //   109: invokespecial 599	com/android/server/am/TaskPersister:yieldIfQueueTooDeep	()V
    //   112: return
    //   113: iload_3
    //   114: iconst_1
    //   115: isub
    //   116: istore_3
    //   117: goto -105 -> 12
    //   120: aload_0
    //   121: getfield 82	com/android/server/am/TaskPersister:mNextWriteTime	J
    //   124: lconst_0
    //   125: lcmp
    //   126: ifne -24 -> 102
    //   129: aload_0
    //   130: invokestatic 604	android/os/SystemClock:uptimeMillis	()J
    //   133: ldc2_w 43
    //   136: ladd
    //   137: putfield 82	com/android/server/am/TaskPersister:mNextWriteTime	J
    //   140: goto -38 -> 102
    //   143: astore_1
    //   144: aload_0
    //   145: monitorexit
    //   146: aload_1
    //   147: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	TaskPersister
    //   0	148	1	paramBitmap	Bitmap
    //   0	148	2	paramString	String
    //   11	106	3	i	int
    //   27	30	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	12	143	finally
    //   16	62	143	finally
    //   66	83	143	finally
    //   83	102	143	finally
    //   102	106	143	finally
    //   120	140	143	finally
  }
  
  void startPersisting()
  {
    if (!this.mLazyTaskWriterThread.isAlive()) {
      this.mLazyTaskWriterThread.start();
    }
  }
  
  void unloadUserDataFromMemory(int paramInt)
  {
    this.mTaskIdsInFile.delete(paramInt);
  }
  
  void wakeup(TaskRecord paramTaskRecord, boolean paramBoolean)
  {
    if (paramTaskRecord != null) {}
    for (;;)
    {
      try
      {
        int i = this.mWriteQueue.size() - 1;
        if (i >= 0)
        {
          WriteQueueItem localWriteQueueItem = (WriteQueueItem)this.mWriteQueue.get(i);
          if ((!(localWriteQueueItem instanceof TaskWriteQueueItem)) || (((TaskWriteQueueItem)localWriteQueueItem).mTask != paramTaskRecord)) {
            continue;
          }
          if (!paramTaskRecord.inRecents) {
            removeThumbnails(paramTaskRecord);
          }
        }
        if ((i < 0) && (paramTaskRecord.isPersistable)) {
          this.mWriteQueue.add(new TaskWriteQueueItem(paramTaskRecord));
        }
        if ((paramBoolean) || (this.mWriteQueue.size() > 6))
        {
          this.mNextWriteTime = -1L;
          notifyAll();
          yieldIfQueueTooDeep();
          return;
          i -= 1;
          continue;
          this.mWriteQueue.add(new WriteQueueItem(null));
          continue;
        }
        if (this.mNextWriteTime != 0L) {
          continue;
        }
      }
      finally {}
      this.mNextWriteTime = (SystemClock.uptimeMillis() + 3000L);
    }
  }
  
  /* Error */
  void writePersistedTaskIdsForUser(SparseBooleanArray paramSparseBooleanArray, int paramInt)
  {
    // Byte code:
    //   0: iload_2
    //   1: ifge +4 -> 5
    //   4: return
    //   5: aload_0
    //   6: iload_2
    //   7: invokespecial 472	com/android/server/am/TaskPersister:getUserPersistedTaskIdsFile	(I)Ljava/io/File;
    //   10: astore 5
    //   12: aload_0
    //   13: getfield 127	com/android/server/am/TaskPersister:mIoLock	Ljava/lang/Object;
    //   16: astore 7
    //   18: aload 7
    //   20: monitorenter
    //   21: aconst_null
    //   22: astore 4
    //   24: aconst_null
    //   25: astore 6
    //   27: new 632	java/io/BufferedWriter
    //   30: dup
    //   31: new 634	java/io/FileWriter
    //   34: dup
    //   35: aload 5
    //   37: invokespecial 635	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   40: invokespecial 637	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   43: astore 5
    //   45: iconst_0
    //   46: istore_3
    //   47: iload_3
    //   48: aload_1
    //   49: invokevirtual 638	android/util/SparseBooleanArray:size	()I
    //   52: if_icmpge +36 -> 88
    //   55: aload_1
    //   56: iload_3
    //   57: invokevirtual 641	android/util/SparseBooleanArray:valueAt	(I)Z
    //   60: ifeq +21 -> 81
    //   63: aload 5
    //   65: aload_1
    //   66: iload_3
    //   67: invokevirtual 642	android/util/SparseBooleanArray:keyAt	(I)I
    //   70: invokestatic 261	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   73: invokevirtual 645	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   76: aload 5
    //   78: invokevirtual 648	java/io/BufferedWriter:newLine	()V
    //   81: iload_3
    //   82: iconst_1
    //   83: iadd
    //   84: istore_3
    //   85: goto -38 -> 47
    //   88: aload 5
    //   90: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   93: aload 7
    //   95: monitorexit
    //   96: return
    //   97: astore 5
    //   99: aload 6
    //   101: astore_1
    //   102: aload_1
    //   103: astore 4
    //   105: ldc 50
    //   107: new 173	java/lang/StringBuilder
    //   110: dup
    //   111: invokespecial 174	java/lang/StringBuilder:<init>	()V
    //   114: ldc_w 650
    //   117: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: iload_2
    //   121: invokevirtual 272	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   124: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   127: aload 5
    //   129: invokestatic 492	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   132: pop
    //   133: aload_1
    //   134: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   137: goto -44 -> 93
    //   140: astore_1
    //   141: aload 7
    //   143: monitorexit
    //   144: aload_1
    //   145: athrow
    //   146: astore_1
    //   147: aload 4
    //   149: invokestatic 487	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   152: aload_1
    //   153: athrow
    //   154: astore_1
    //   155: goto -14 -> 141
    //   158: astore_1
    //   159: aload 5
    //   161: astore 4
    //   163: goto -16 -> 147
    //   166: astore 4
    //   168: aload 5
    //   170: astore_1
    //   171: aload 4
    //   173: astore 5
    //   175: goto -73 -> 102
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	this	TaskPersister
    //   0	178	1	paramSparseBooleanArray	SparseBooleanArray
    //   0	178	2	paramInt	int
    //   46	39	3	i	int
    //   22	140	4	localObject1	Object
    //   166	6	4	localException1	Exception
    //   10	79	5	localObject2	Object
    //   97	72	5	localException2	Exception
    //   173	1	5	localObject3	Object
    //   25	75	6	localObject4	Object
    //   16	126	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   27	45	97	java/lang/Exception
    //   133	137	140	finally
    //   147	154	140	finally
    //   27	45	146	finally
    //   105	133	146	finally
    //   88	93	154	finally
    //   47	81	158	finally
    //   47	81	166	java/lang/Exception
  }
  
  private static class ImageWriteQueueItem
    extends TaskPersister.WriteQueueItem
  {
    final String mFilePath;
    Bitmap mImage;
    
    ImageWriteQueueItem(String paramString, Bitmap paramBitmap)
    {
      super();
      this.mFilePath = paramString;
      this.mImage = paramBitmap;
    }
  }
  
  private class LazyTaskWriterThread
    extends Thread
  {
    LazyTaskWriterThread(String paramString)
    {
      super();
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: bipush 10
      //   2: invokestatic 33	android/os/Process:setThreadPriority	(I)V
      //   5: new 35	android/util/ArraySet
      //   8: dup
      //   9: invokespecial 37	android/util/ArraySet:<init>	()V
      //   12: astore 12
      //   14: aload_0
      //   15: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   18: astore 7
      //   20: aload 7
      //   22: monitorenter
      //   23: aload_0
      //   24: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   27: getfield 41	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
      //   30: invokevirtual 47	java/util/ArrayList:isEmpty	()Z
      //   33: istore_2
      //   34: aload 7
      //   36: monitorexit
      //   37: iload_2
      //   38: ifeq +151 -> 189
      //   41: aload 12
      //   43: invokevirtual 50	android/util/ArraySet:clear	()V
      //   46: aload_0
      //   47: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   50: invokestatic 54	com/android/server/am/TaskPersister:-get2	(Lcom/android/server/am/TaskPersister;)Lcom/android/server/am/ActivityManagerService;
      //   53: astore 7
      //   55: aload 7
      //   57: monitorenter
      //   58: invokestatic 59	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
      //   61: aload_0
      //   62: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   65: invokestatic 63	com/android/server/am/TaskPersister:-get1	(Lcom/android/server/am/TaskPersister;)Lcom/android/server/am/RecentTasks;
      //   68: invokevirtual 69	com/android/server/am/RecentTasks:size	()I
      //   71: iconst_1
      //   72: isub
      //   73: istore_1
      //   74: iload_1
      //   75: iflt +99 -> 174
      //   78: aload_0
      //   79: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   82: invokestatic 63	com/android/server/am/TaskPersister:-get1	(Lcom/android/server/am/TaskPersister;)Lcom/android/server/am/RecentTasks;
      //   85: iload_1
      //   86: invokevirtual 73	com/android/server/am/RecentTasks:get	(I)Ljava/lang/Object;
      //   89: checkcast 75	com/android/server/am/TaskRecord
      //   92: astore 8
      //   94: aload 8
      //   96: getfield 79	com/android/server/am/TaskRecord:isPersistable	Z
      //   99: ifne +11 -> 110
      //   102: aload 8
      //   104: getfield 82	com/android/server/am/TaskRecord:inRecents	Z
      //   107: ifeq +24 -> 131
      //   110: aload 8
      //   112: getfield 86	com/android/server/am/TaskRecord:stack	Lcom/android/server/am/ActivityStack;
      //   115: ifnull +31 -> 146
      //   118: aload 8
      //   120: getfield 86	com/android/server/am/TaskRecord:stack	Lcom/android/server/am/ActivityStack;
      //   123: invokevirtual 91	com/android/server/am/ActivityStack:isHomeStack	()Z
      //   126: istore_2
      //   127: iload_2
      //   128: ifeq +18 -> 146
      //   131: iload_1
      //   132: iconst_1
      //   133: isub
      //   134: istore_1
      //   135: goto -61 -> 74
      //   138: astore 8
      //   140: aload 7
      //   142: monitorexit
      //   143: aload 8
      //   145: athrow
      //   146: aload 12
      //   148: aload 8
      //   150: getfield 95	com/android/server/am/TaskRecord:taskId	I
      //   153: invokestatic 101	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   156: invokevirtual 105	android/util/ArraySet:add	(Ljava/lang/Object;)Z
      //   159: pop
      //   160: goto -29 -> 131
      //   163: astore 8
      //   165: aload 7
      //   167: monitorexit
      //   168: invokestatic 108	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   171: aload 8
      //   173: athrow
      //   174: aload 7
      //   176: monitorexit
      //   177: invokestatic 108	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   180: aload_0
      //   181: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   184: aload 12
      //   186: invokestatic 112	com/android/server/am/TaskPersister:-wrap2	(Lcom/android/server/am/TaskPersister;Landroid/util/ArraySet;)V
      //   189: aload_0
      //   190: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   193: invokestatic 116	com/android/server/am/TaskPersister:-wrap3	(Lcom/android/server/am/TaskPersister;)V
      //   196: aload_0
      //   197: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   200: astore 7
      //   202: aload 7
      //   204: monitorenter
      //   205: aload_0
      //   206: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   209: invokestatic 120	com/android/server/am/TaskPersister:-get0	(Lcom/android/server/am/TaskPersister;)J
      //   212: ldc2_w 121
      //   215: lcmp
      //   216: ifeq +18 -> 234
      //   219: aload_0
      //   220: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   223: invokestatic 128	android/os/SystemClock:uptimeMillis	()J
      //   226: ldc2_w 129
      //   229: ladd
      //   230: invokestatic 134	com/android/server/am/TaskPersister:-set0	(Lcom/android/server/am/TaskPersister;J)J
      //   233: pop2
      //   234: aload_0
      //   235: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   238: getfield 41	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
      //   241: invokevirtual 47	java/util/ArrayList:isEmpty	()Z
      //   244: ifeq +46 -> 290
      //   247: aload_0
      //   248: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   251: invokestatic 120	com/android/server/am/TaskPersister:-get0	(Lcom/android/server/am/TaskPersister;)J
      //   254: lconst_0
      //   255: lcmp
      //   256: ifeq +19 -> 275
      //   259: aload_0
      //   260: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   263: lconst_0
      //   264: invokestatic 134	com/android/server/am/TaskPersister:-set0	(Lcom/android/server/am/TaskPersister;J)J
      //   267: pop2
      //   268: aload_0
      //   269: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   272: invokevirtual 137	com/android/server/am/TaskPersister:notifyAll	()V
      //   275: aload_0
      //   276: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   279: invokevirtual 140	com/android/server/am/TaskPersister:wait	()V
      //   282: goto -48 -> 234
      //   285: astore 8
      //   287: goto -53 -> 234
      //   290: aload_0
      //   291: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   294: getfield 41	com/android/server/am/TaskPersister:mWriteQueue	Ljava/util/ArrayList;
      //   297: iconst_0
      //   298: invokevirtual 143	java/util/ArrayList:remove	(I)Ljava/lang/Object;
      //   301: checkcast 145	com/android/server/am/TaskPersister$WriteQueueItem
      //   304: astore 8
      //   306: invokestatic 128	android/os/SystemClock:uptimeMillis	()J
      //   309: lstore_3
      //   310: aload_0
      //   311: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   314: invokestatic 120	com/android/server/am/TaskPersister:-get0	(Lcom/android/server/am/TaskPersister;)J
      //   317: lstore 5
      //   319: lload_3
      //   320: lload 5
      //   322: lcmp
      //   323: ifge +26 -> 349
      //   326: aload_0
      //   327: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   330: aload_0
      //   331: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   334: invokestatic 120	com/android/server/am/TaskPersister:-get0	(Lcom/android/server/am/TaskPersister;)J
      //   337: lload_3
      //   338: lsub
      //   339: invokevirtual 148	com/android/server/am/TaskPersister:wait	(J)V
      //   342: invokestatic 128	android/os/SystemClock:uptimeMillis	()J
      //   345: lstore_3
      //   346: goto -36 -> 310
      //   349: aload 7
      //   351: monitorexit
      //   352: aload 8
      //   354: instanceof 150
      //   357: ifeq +174 -> 531
      //   360: aload 8
      //   362: checkcast 150	com/android/server/am/TaskPersister$ImageWriteQueueItem
      //   365: astore 7
      //   367: aload 7
      //   369: getfield 154	com/android/server/am/TaskPersister$ImageWriteQueueItem:mFilePath	Ljava/lang/String;
      //   372: astore 10
      //   374: aload 10
      //   376: invokestatic 158	com/android/server/am/TaskPersister:-wrap0	(Ljava/lang/String;)Z
      //   379: ifne +40 -> 419
      //   382: ldc -96
      //   384: new 162	java/lang/StringBuilder
      //   387: dup
      //   388: invokespecial 163	java/lang/StringBuilder:<init>	()V
      //   391: ldc -91
      //   393: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   396: aload 10
      //   398: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   401: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   404: invokestatic 179	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   407: pop
      //   408: goto -394 -> 14
      //   411: astore 8
      //   413: aload 7
      //   415: monitorexit
      //   416: aload 8
      //   418: athrow
      //   419: aload 7
      //   421: getfield 183	com/android/server/am/TaskPersister$ImageWriteQueueItem:mImage	Landroid/graphics/Bitmap;
      //   424: astore 11
      //   426: aconst_null
      //   427: astore 7
      //   429: aconst_null
      //   430: astore 9
      //   432: new 185	java/io/FileOutputStream
      //   435: dup
      //   436: new 187	java/io/File
      //   439: dup
      //   440: aload 10
      //   442: invokespecial 188	java/io/File:<init>	(Ljava/lang/String;)V
      //   445: invokespecial 191	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   448: astore 8
      //   450: aload 11
      //   452: getstatic 197	android/graphics/Bitmap$CompressFormat:PNG	Landroid/graphics/Bitmap$CompressFormat;
      //   455: bipush 100
      //   457: aload 8
      //   459: invokevirtual 203	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
      //   462: pop
      //   463: aload 8
      //   465: invokestatic 209	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   468: goto -454 -> 14
      //   471: astore 7
      //   473: aload 9
      //   475: astore 8
      //   477: aload 7
      //   479: astore 9
      //   481: aload 8
      //   483: astore 7
      //   485: ldc -96
      //   487: new 162	java/lang/StringBuilder
      //   490: dup
      //   491: invokespecial 163	java/lang/StringBuilder:<init>	()V
      //   494: ldc -45
      //   496: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   499: aload 10
      //   501: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   504: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   507: aload 9
      //   509: invokestatic 214	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   512: pop
      //   513: aload 8
      //   515: invokestatic 209	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   518: goto -504 -> 14
      //   521: astore 8
      //   523: aload 7
      //   525: invokestatic 209	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   528: aload 8
      //   530: athrow
      //   531: aload 8
      //   533: instanceof 216
      //   536: ifeq -522 -> 14
      //   539: aconst_null
      //   540: astore 7
      //   542: aload 8
      //   544: checkcast 216	com/android/server/am/TaskPersister$TaskWriteQueueItem
      //   547: getfield 220	com/android/server/am/TaskPersister$TaskWriteQueueItem:mTask	Lcom/android/server/am/TaskRecord;
      //   550: astore 10
      //   552: aload_0
      //   553: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   556: invokestatic 54	com/android/server/am/TaskPersister:-get2	(Lcom/android/server/am/TaskPersister;)Lcom/android/server/am/ActivityManagerService;
      //   559: astore 9
      //   561: aload 9
      //   563: monitorenter
      //   564: invokestatic 59	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
      //   567: aload 10
      //   569: getfield 82	com/android/server/am/TaskRecord:inRecents	Z
      //   572: istore_2
      //   573: aload 7
      //   575: astore 8
      //   577: iload_2
      //   578: ifeq +14 -> 592
      //   581: aload_0
      //   582: getfield 13	com/android/server/am/TaskPersister$LazyTaskWriterThread:this$0	Lcom/android/server/am/TaskPersister;
      //   585: aload 10
      //   587: invokestatic 224	com/android/server/am/TaskPersister:-wrap1	(Lcom/android/server/am/TaskPersister;Lcom/android/server/am/TaskRecord;)Ljava/io/StringWriter;
      //   590: astore 8
      //   592: aload 9
      //   594: monitorexit
      //   595: invokestatic 108	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   598: aload 8
      //   600: ifnull -586 -> 14
      //   603: aconst_null
      //   604: astore 9
      //   606: aconst_null
      //   607: astore 11
      //   609: aconst_null
      //   610: astore 7
      //   612: new 226	android/util/AtomicFile
      //   615: dup
      //   616: new 187	java/io/File
      //   619: dup
      //   620: aload 10
      //   622: getfield 229	com/android/server/am/TaskRecord:userId	I
      //   625: invokestatic 233	com/android/server/am/TaskPersister:getUserTasksDir	(I)Ljava/io/File;
      //   628: new 162	java/lang/StringBuilder
      //   631: dup
      //   632: invokespecial 163	java/lang/StringBuilder:<init>	()V
      //   635: aload 10
      //   637: getfield 95	com/android/server/am/TaskRecord:taskId	I
      //   640: invokestatic 238	java/lang/String:valueOf	(I)Ljava/lang/String;
      //   643: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   646: ldc -16
      //   648: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   651: ldc -14
      //   653: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   656: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   659: invokespecial 245	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   662: invokespecial 246	android/util/AtomicFile:<init>	(Ljava/io/File;)V
      //   665: astore 10
      //   667: aload 11
      //   669: astore 7
      //   671: aload 10
      //   673: invokevirtual 250	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
      //   676: astore 9
      //   678: aload 9
      //   680: astore 7
      //   682: aload 9
      //   684: aload 8
      //   686: invokevirtual 253	java/io/StringWriter:toString	()Ljava/lang/String;
      //   689: invokevirtual 257	java/lang/String:getBytes	()[B
      //   692: invokevirtual 261	java/io/FileOutputStream:write	([B)V
      //   695: aload 9
      //   697: astore 7
      //   699: aload 9
      //   701: bipush 10
      //   703: invokevirtual 263	java/io/FileOutputStream:write	(I)V
      //   706: aload 9
      //   708: astore 7
      //   710: aload 10
      //   712: aload 9
      //   714: invokevirtual 267	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
      //   717: goto -703 -> 14
      //   720: astore 11
      //   722: aload 10
      //   724: astore 8
      //   726: aload 7
      //   728: astore 9
      //   730: aload 11
      //   732: astore 7
      //   734: aload 9
      //   736: ifnull +10 -> 746
      //   739: aload 8
      //   741: aload 9
      //   743: invokevirtual 270	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
      //   746: ldc -96
      //   748: new 162	java/lang/StringBuilder
      //   751: dup
      //   752: invokespecial 163	java/lang/StringBuilder:<init>	()V
      //   755: ldc_w 272
      //   758: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   761: aload 8
      //   763: invokevirtual 275	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   766: ldc_w 277
      //   769: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   772: aload 7
      //   774: invokevirtual 275	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   777: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   780: invokestatic 179	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   783: pop
      //   784: goto -770 -> 14
      //   787: astore 7
      //   789: aload 9
      //   791: monitorexit
      //   792: invokestatic 108	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   795: aload 7
      //   797: athrow
      //   798: astore 10
      //   800: aload 7
      //   802: astore 8
      //   804: aload 10
      //   806: astore 7
      //   808: goto -74 -> 734
      //   811: astore 8
      //   813: aload 7
      //   815: astore 8
      //   817: goto -225 -> 592
      //   820: astore 8
      //   822: aload 7
      //   824: astore 8
      //   826: goto -234 -> 592
      //   829: astore 9
      //   831: aload 8
      //   833: astore 7
      //   835: aload 9
      //   837: astore 8
      //   839: goto -316 -> 523
      //   842: astore 9
      //   844: goto -363 -> 481
      //   847: astore 9
      //   849: goto -507 -> 342
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	852	0	this	LazyTaskWriterThread
      //   73	62	1	i	int
      //   33	545	2	bool	boolean
      //   309	37	3	l1	long
      //   317	4	5	l2	long
      //   18	410	7	localObject1	Object
      //   471	7	7	localException1	Exception
      //   483	290	7	localObject2	Object
      //   787	14	7	localObject3	Object
      //   806	28	7	localObject4	Object
      //   92	27	8	localTaskRecord	TaskRecord
      //   138	11	8	localObject5	Object
      //   163	9	8	localObject6	Object
      //   285	1	8	localInterruptedException1	InterruptedException
      //   304	57	8	localWriteQueueItem	TaskPersister.WriteQueueItem
      //   411	6	8	localObject7	Object
      //   448	66	8	localObject8	Object
      //   521	22	8	localObject9	Object
      //   575	228	8	localObject10	Object
      //   811	1	8	localIOException1	IOException
      //   815	1	8	localObject11	Object
      //   820	1	8	localXmlPullParserException	XmlPullParserException
      //   824	14	8	localObject12	Object
      //   829	7	9	localObject14	Object
      //   842	1	9	localException2	Exception
      //   847	1	9	localInterruptedException2	InterruptedException
      //   372	351	10	localObject15	Object
      //   798	7	10	localIOException2	IOException
      //   424	244	11	localBitmap	Bitmap
      //   720	11	11	localIOException3	IOException
      //   12	173	12	localArraySet	ArraySet
      // Exception table:
      //   from	to	target	type
      //   23	34	138	finally
      //   58	74	163	finally
      //   78	110	163	finally
      //   110	127	163	finally
      //   146	160	163	finally
      //   275	282	285	java/lang/InterruptedException
      //   205	234	411	finally
      //   234	275	411	finally
      //   275	282	411	finally
      //   290	310	411	finally
      //   310	319	411	finally
      //   326	342	411	finally
      //   342	346	411	finally
      //   432	450	471	java/lang/Exception
      //   432	450	521	finally
      //   485	513	521	finally
      //   671	678	720	java/io/IOException
      //   682	695	720	java/io/IOException
      //   699	706	720	java/io/IOException
      //   710	717	720	java/io/IOException
      //   564	573	787	finally
      //   581	592	787	finally
      //   612	667	798	java/io/IOException
      //   581	592	811	java/io/IOException
      //   581	592	820	org/xmlpull/v1/XmlPullParserException
      //   450	463	829	finally
      //   450	463	842	java/lang/Exception
      //   326	342	847	java/lang/InterruptedException
    }
  }
  
  private static class TaskWriteQueueItem
    extends TaskPersister.WriteQueueItem
  {
    final TaskRecord mTask;
    
    TaskWriteQueueItem(TaskRecord paramTaskRecord)
    {
      super();
      this.mTask = paramTaskRecord;
    }
  }
  
  private static class WriteQueueItem {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/TaskPersister.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */