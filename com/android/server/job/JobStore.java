package com.android.server.job;

import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.IoThread;
import com.android.server.job.controllers.JobStatus;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class JobStore
{
  private static final boolean DEBUG = false;
  private static final int JOBS_FILE_VERSION = 0;
  private static final int MAX_OPS_BEFORE_WRITE = 1;
  private static final String TAG = "JobStore";
  private static final String XML_TAG_EXTRAS = "extras";
  private static final String XML_TAG_ONEOFF = "one-off";
  private static final String XML_TAG_PARAMS_CONSTRAINTS = "constraints";
  private static final String XML_TAG_PERIODIC = "periodic";
  private static JobStore sSingleton;
  private static final Object sSingletonLock = new Object();
  final Context mContext;
  private int mDirtyOperations;
  private final Handler mIoHandler = IoThread.getHandler();
  final JobSet mJobSet;
  private final AtomicFile mJobsFile;
  final Object mLock;
  
  private JobStore(Context paramContext, Object paramObject, File paramFile)
  {
    this.mLock = paramObject;
    this.mContext = paramContext;
    this.mDirtyOperations = 0;
    paramContext = new File(new File(paramFile, "system"), "job");
    paramContext.mkdirs();
    this.mJobsFile = new AtomicFile(new File(paramContext, "jobs.xml"));
    this.mJobSet = new JobSet();
    readJobMapFromDisk(this.mJobSet);
  }
  
  static JobStore initAndGet(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sSingletonLock)
    {
      if (sSingleton == null) {
        sSingleton = new JobStore(paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock(), Environment.getDataDirectory());
      }
      paramJobSchedulerService = sSingleton;
      return paramJobSchedulerService;
    }
  }
  
  public static JobStore initAndGetForTesting(Context paramContext, File paramFile)
  {
    paramContext = new JobStore(paramContext, new Object(), paramFile);
    paramContext.clear();
    return paramContext;
  }
  
  private void maybeWriteStatusToDiskAsync()
  {
    this.mDirtyOperations += 1;
    if (this.mDirtyOperations >= 1) {
      this.mIoHandler.post(new WriteJobsMapToDiskRunnable(null));
    }
  }
  
  public boolean add(JobStatus paramJobStatus)
  {
    boolean bool = this.mJobSet.remove(paramJobStatus);
    this.mJobSet.add(paramJobStatus);
    if (paramJobStatus.isPersisted()) {
      maybeWriteStatusToDiskAsync();
    }
    return bool;
  }
  
  public void clear()
  {
    this.mJobSet.clear();
    maybeWriteStatusToDiskAsync();
  }
  
  boolean containsJob(JobStatus paramJobStatus)
  {
    return this.mJobSet.contains(paramJobStatus);
  }
  
  public int countJobsForUid(int paramInt)
  {
    return this.mJobSet.countJobsForUid(paramInt);
  }
  
  public void forEachJob(int paramInt, JobStatusFunctor paramJobStatusFunctor)
  {
    this.mJobSet.forEachJob(paramInt, paramJobStatusFunctor);
  }
  
  public void forEachJob(JobStatusFunctor paramJobStatusFunctor)
  {
    this.mJobSet.forEachJob(paramJobStatusFunctor);
  }
  
  public JobStatus getJobByUidAndJobId(int paramInt1, int paramInt2)
  {
    return this.mJobSet.get(paramInt1, paramInt2);
  }
  
  public List<JobStatus> getJobsByUid(int paramInt)
  {
    return this.mJobSet.getJobsByUid(paramInt);
  }
  
  public List<JobStatus> getJobsByUser(int paramInt)
  {
    return this.mJobSet.getJobsByUser(paramInt);
  }
  
  public void readJobMapFromDisk(JobSet paramJobSet)
  {
    new ReadJobMapFromDiskRunnable(paramJobSet).run();
  }
  
  public boolean remove(JobStatus paramJobStatus, boolean paramBoolean)
  {
    boolean bool = this.mJobSet.remove(paramJobStatus);
    if (!bool) {
      return false;
    }
    if ((paramBoolean) && (paramJobStatus.isPersisted())) {
      maybeWriteStatusToDiskAsync();
    }
    return bool;
  }
  
  public int size()
  {
    return this.mJobSet.size();
  }
  
  static class JobSet
  {
    private SparseArray<ArraySet<JobStatus>> mJobs = new SparseArray();
    
    public boolean add(JobStatus paramJobStatus)
    {
      int i = paramJobStatus.getUid();
      ArraySet localArraySet2 = (ArraySet)this.mJobs.get(i);
      ArraySet localArraySet1 = localArraySet2;
      if (localArraySet2 == null)
      {
        localArraySet1 = new ArraySet();
        this.mJobs.put(i, localArraySet1);
      }
      return localArraySet1.add(paramJobStatus);
    }
    
    public void clear()
    {
      this.mJobs.clear();
    }
    
    public boolean contains(JobStatus paramJobStatus)
    {
      int i = paramJobStatus.getUid();
      ArraySet localArraySet = (ArraySet)this.mJobs.get(i);
      if (localArraySet != null) {
        return localArraySet.contains(paramJobStatus);
      }
      return false;
    }
    
    public int countJobsForUid(int paramInt)
    {
      int j = 0;
      int i = 0;
      ArraySet localArraySet = (ArraySet)this.mJobs.get(paramInt);
      if (localArraySet != null)
      {
        j = localArraySet.size() - 1;
        paramInt = i;
        i = j;
        for (;;)
        {
          j = paramInt;
          if (i < 0) {
            break;
          }
          JobStatus localJobStatus = (JobStatus)localArraySet.valueAt(i);
          j = paramInt;
          if (localJobStatus.getUid() == localJobStatus.getSourceUid()) {
            j = paramInt + 1;
          }
          i -= 1;
          paramInt = j;
        }
      }
      return j;
    }
    
    public void forEachJob(int paramInt, JobStore.JobStatusFunctor paramJobStatusFunctor)
    {
      ArraySet localArraySet = (ArraySet)this.mJobs.get(paramInt);
      if (localArraySet != null)
      {
        paramInt = localArraySet.size() - 1;
        while (paramInt >= 0)
        {
          paramJobStatusFunctor.process((JobStatus)localArraySet.valueAt(paramInt));
          paramInt -= 1;
        }
      }
    }
    
    public void forEachJob(JobStore.JobStatusFunctor paramJobStatusFunctor)
    {
      int i = this.mJobs.size() - 1;
      while (i >= 0)
      {
        ArraySet localArraySet = (ArraySet)this.mJobs.valueAt(i);
        int j = localArraySet.size() - 1;
        while (j >= 0)
        {
          paramJobStatusFunctor.process((JobStatus)localArraySet.valueAt(j));
          j -= 1;
        }
        i -= 1;
      }
    }
    
    public JobStatus get(int paramInt1, int paramInt2)
    {
      ArraySet localArraySet = (ArraySet)this.mJobs.get(paramInt1);
      if (localArraySet != null)
      {
        paramInt1 = localArraySet.size() - 1;
        while (paramInt1 >= 0)
        {
          JobStatus localJobStatus = (JobStatus)localArraySet.valueAt(paramInt1);
          if (localJobStatus.getJobId() == paramInt2) {
            return localJobStatus;
          }
          paramInt1 -= 1;
        }
      }
      return null;
    }
    
    public List<JobStatus> getAllJobs()
    {
      ArrayList localArrayList = new ArrayList(size());
      int i = this.mJobs.size() - 1;
      while (i >= 0)
      {
        ArraySet localArraySet = (ArraySet)this.mJobs.valueAt(i);
        if (localArraySet != null)
        {
          int j = localArraySet.size() - 1;
          while (j >= 0)
          {
            localArrayList.add((JobStatus)localArraySet.valueAt(j));
            j -= 1;
          }
        }
        i -= 1;
      }
      return localArrayList;
    }
    
    public List<JobStatus> getJobsByUid(int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      ArraySet localArraySet = (ArraySet)this.mJobs.get(paramInt);
      if (localArraySet != null) {
        localArrayList.addAll(localArraySet);
      }
      return localArrayList;
    }
    
    public List<JobStatus> getJobsByUser(int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      int i = this.mJobs.size() - 1;
      while (i >= 0)
      {
        if (UserHandle.getUserId(this.mJobs.keyAt(i)) == paramInt)
        {
          ArraySet localArraySet = (ArraySet)this.mJobs.get(i);
          if (localArraySet != null) {
            localArrayList.addAll(localArraySet);
          }
        }
        i -= 1;
      }
      return localArrayList;
    }
    
    public boolean remove(JobStatus paramJobStatus)
    {
      boolean bool = false;
      int i = paramJobStatus.getUid();
      ArraySet localArraySet = (ArraySet)this.mJobs.get(i);
      if (localArraySet != null) {
        bool = localArraySet.remove(paramJobStatus);
      }
      if ((bool) && (localArraySet.size() == 0)) {
        this.mJobs.remove(i);
      }
      return bool;
    }
    
    public int size()
    {
      int j = 0;
      int i = this.mJobs.size() - 1;
      while (i >= 0)
      {
        j += ((ArraySet)this.mJobs.valueAt(i)).size();
        i -= 1;
      }
      return j;
    }
  }
  
  public static abstract interface JobStatusFunctor
  {
    public abstract void process(JobStatus paramJobStatus);
  }
  
  private class ReadJobMapFromDiskRunnable
    implements Runnable
  {
    private final JobStore.JobSet jobSet;
    
    ReadJobMapFromDiskRunnable(JobStore.JobSet paramJobSet)
    {
      this.jobSet = paramJobSet;
    }
    
    private JobInfo.Builder buildBuilderFromXml(XmlPullParser paramXmlPullParser)
      throws NumberFormatException
    {
      return new JobInfo.Builder(Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "jobid")), new ComponentName(paramXmlPullParser.getAttributeValue(null, "package"), paramXmlPullParser.getAttributeValue(null, "class")));
    }
    
    private void buildConstraintsFromXml(JobInfo.Builder paramBuilder, XmlPullParser paramXmlPullParser)
    {
      if (paramXmlPullParser.getAttributeValue(null, "connectivity") != null) {
        paramBuilder.setRequiredNetworkType(1);
      }
      if (paramXmlPullParser.getAttributeValue(null, "unmetered") != null) {
        paramBuilder.setRequiredNetworkType(2);
      }
      if (paramXmlPullParser.getAttributeValue(null, "not-roaming") != null) {
        paramBuilder.setRequiredNetworkType(3);
      }
      if (paramXmlPullParser.getAttributeValue(null, "idle") != null) {
        paramBuilder.setRequiresDeviceIdle(true);
      }
      if (paramXmlPullParser.getAttributeValue(null, "charging") != null) {
        paramBuilder.setRequiresCharging(true);
      }
    }
    
    private Pair<Long, Long> buildExecutionTimesFromXml(XmlPullParser paramXmlPullParser)
      throws NumberFormatException
    {
      long l3 = System.currentTimeMillis();
      long l4 = SystemClock.elapsedRealtime();
      long l2 = 0L;
      long l1 = Long.MAX_VALUE;
      String str = paramXmlPullParser.getAttributeValue(null, "deadline");
      if (str != null) {
        l1 = l4 + Math.max(Long.valueOf(str).longValue() - l3, 0L);
      }
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "delay");
      if (paramXmlPullParser != null) {
        l2 = l4 + Math.max(Long.valueOf(paramXmlPullParser).longValue() - l3, 0L);
      }
      return Pair.create(Long.valueOf(l2), Long.valueOf(l1));
    }
    
    private void maybeBuildBackoffPolicyFromXml(JobInfo.Builder paramBuilder, XmlPullParser paramXmlPullParser)
    {
      String str = paramXmlPullParser.getAttributeValue(null, "initial-backoff");
      if (str != null) {
        paramBuilder.setBackoffCriteria(Long.valueOf(str).longValue(), Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "backoff-policy")));
      }
    }
    
    private List<JobStatus> readJobMapImpl(FileInputStream paramFileInputStream)
      throws XmlPullParserException, IOException
    {
      XmlPullParser localXmlPullParser = Xml.newPullParser();
      localXmlPullParser.setInput(paramFileInputStream, StandardCharsets.UTF_8.name());
      int i = localXmlPullParser.getEventType();
      while ((i != 2) && (i != 1))
      {
        i = localXmlPullParser.next();
        Slog.d("JobStore", "Start tag: " + localXmlPullParser.getName());
      }
      if (i == 1) {
        return null;
      }
      if ("job-info".equals(localXmlPullParser.getName()))
      {
        paramFileInputStream = new ArrayList();
        try
        {
          if (Integer.parseInt(localXmlPullParser.getAttributeValue(null, "version")) != 0)
          {
            Slog.d("JobStore", "Invalid version number, aborting jobs file read.");
            return null;
          }
        }
        catch (NumberFormatException paramFileInputStream)
        {
          Slog.e("JobStore", "Invalid version number, aborting jobs file read.");
          return null;
        }
        i = localXmlPullParser.next();
        if ((i == 2) && ("job".equals(localXmlPullParser.getName())))
        {
          JobStatus localJobStatus = restoreJobFromXml(localXmlPullParser);
          if (localJobStatus == null) {
            break label213;
          }
          paramFileInputStream.add(localJobStatus);
        }
        for (;;)
        {
          int j = localXmlPullParser.next();
          i = j;
          if (j != 1) {
            break;
          }
          return paramFileInputStream;
          label213:
          Slog.d("JobStore", "Error reading job from file.");
        }
      }
      return null;
    }
    
    /* Error */
    private JobStatus restoreJobFromXml(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: invokespecial 225	com/android/server/job/JobStore$ReadJobMapFromDiskRunnable:buildBuilderFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/app/job/JobInfo$Builder;
      //   5: astore 15
      //   7: aload 15
      //   9: iconst_1
      //   10: invokevirtual 228	android/app/job/JobInfo$Builder:setPersisted	(Z)Landroid/app/job/JobInfo$Builder;
      //   13: pop
      //   14: aload_1
      //   15: aconst_null
      //   16: ldc -26
      //   18: invokeinterface 37 3 0
      //   23: invokestatic 43	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   26: istore_3
      //   27: aload_1
      //   28: aconst_null
      //   29: ldc -24
      //   31: invokeinterface 37 3 0
      //   36: astore 12
      //   38: aload 12
      //   40: ifnull +14 -> 54
      //   43: aload 15
      //   45: aload 12
      //   47: invokestatic 43	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   50: invokevirtual 235	android/app/job/JobInfo$Builder:setPriority	(I)Landroid/app/job/JobInfo$Builder;
      //   53: pop
      //   54: aload_1
      //   55: aconst_null
      //   56: ldc -19
      //   58: invokeinterface 37 3 0
      //   63: astore 12
      //   65: aload 12
      //   67: ifnull +14 -> 81
      //   70: aload 15
      //   72: aload 12
      //   74: invokestatic 43	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   77: invokevirtual 240	android/app/job/JobInfo$Builder:setFlags	(I)Landroid/app/job/JobInfo$Builder;
      //   80: pop
      //   81: aload_1
      //   82: aconst_null
      //   83: ldc -14
      //   85: invokeinterface 37 3 0
      //   90: astore 12
      //   92: aload 12
      //   94: ifnonnull +67 -> 161
      //   97: iconst_m1
      //   98: istore_2
      //   99: aload_1
      //   100: aconst_null
      //   101: ldc -12
      //   103: invokeinterface 37 3 0
      //   108: astore 14
      //   110: aload_1
      //   111: aconst_null
      //   112: ldc -10
      //   114: invokeinterface 37 3 0
      //   119: astore 16
      //   121: aload_1
      //   122: invokeinterface 168 1 0
      //   127: istore 4
      //   129: iload 4
      //   131: iconst_4
      //   132: if_icmpeq -11 -> 121
      //   135: iload 4
      //   137: iconst_2
      //   138: if_icmpne +43 -> 181
      //   141: ldc -8
      //   143: aload_1
      //   144: invokeinterface 182 1 0
      //   149: invokevirtual 199	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   152: istore 5
      //   154: iload 5
      //   156: ifne +31 -> 187
      //   159: aconst_null
      //   160: areturn
      //   161: aload 12
      //   163: invokestatic 43	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   166: istore_2
      //   167: goto -68 -> 99
      //   170: astore_1
      //   171: ldc -86
      //   173: ldc -6
      //   175: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   178: pop
      //   179: aconst_null
      //   180: areturn
      //   181: iconst_0
      //   182: istore 5
      //   184: goto -30 -> 154
      //   187: aload_0
      //   188: aload 15
      //   190: aload_1
      //   191: invokespecial 252	com/android/server/job/JobStore$ReadJobMapFromDiskRunnable:buildConstraintsFromXml	(Landroid/app/job/JobInfo$Builder;Lorg/xmlpull/v1/XmlPullParser;)V
      //   194: aload_1
      //   195: invokeinterface 168 1 0
      //   200: pop
      //   201: aload_1
      //   202: invokeinterface 168 1 0
      //   207: istore 4
      //   209: iload 4
      //   211: iconst_4
      //   212: if_icmpeq -11 -> 201
      //   215: iload 4
      //   217: iconst_2
      //   218: if_icmpeq +16 -> 234
      //   221: aconst_null
      //   222: areturn
      //   223: astore_1
      //   224: ldc -86
      //   226: ldc -2
      //   228: invokestatic 191	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   231: pop
      //   232: aconst_null
      //   233: areturn
      //   234: aload_0
      //   235: aload_1
      //   236: invokespecial 256	com/android/server/job/JobStore$ReadJobMapFromDiskRunnable:buildExecutionTimesFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/Pair;
      //   239: astore 13
      //   241: invokestatic 92	android/os/SystemClock:elapsedRealtime	()J
      //   244: lstore 10
      //   246: ldc_w 258
      //   249: aload_1
      //   250: invokeinterface 182 1 0
      //   255: invokevirtual 199	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   258: ifeq +283 -> 541
      //   261: aload_1
      //   262: aconst_null
      //   263: ldc_w 260
      //   266: invokeinterface 37 3 0
      //   271: invokestatic 102	java/lang/Long:valueOf	(Ljava/lang/String;)Ljava/lang/Long;
      //   274: invokevirtual 105	java/lang/Long:longValue	()J
      //   277: lstore 8
      //   279: aload_1
      //   280: aconst_null
      //   281: ldc_w 262
      //   284: invokeinterface 37 3 0
      //   289: astore 12
      //   291: aload 12
      //   293: ifnull +229 -> 522
      //   296: aload 12
      //   298: invokestatic 102	java/lang/Long:valueOf	(Ljava/lang/String;)Ljava/lang/Long;
      //   301: invokevirtual 105	java/lang/Long:longValue	()J
      //   304: lstore 6
      //   306: aload 15
      //   308: lload 8
      //   310: lload 6
      //   312: invokevirtual 266	android/app/job/JobInfo$Builder:setPeriodic	(JJ)Landroid/app/job/JobInfo$Builder;
      //   315: pop
      //   316: aload 13
      //   318: astore 12
      //   320: aload 13
      //   322: getfield 270	android/util/Pair:second	Ljava/lang/Object;
      //   325: checkcast 98	java/lang/Long
      //   328: invokevirtual 105	java/lang/Long:longValue	()J
      //   331: lload 10
      //   333: lload 8
      //   335: ladd
      //   336: lload 6
      //   338: ladd
      //   339: lcmp
      //   340: ifle +124 -> 464
      //   343: lload 10
      //   345: lload 6
      //   347: ladd
      //   348: lload 8
      //   350: ladd
      //   351: lstore 8
      //   353: lload 8
      //   355: lload 6
      //   357: lsub
      //   358: lstore 6
      //   360: ldc -86
      //   362: ldc_w 272
      //   365: iconst_5
      //   366: anewarray 4	java/lang/Object
      //   369: dup
      //   370: iconst_0
      //   371: iload_3
      //   372: invokestatic 275	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   375: aastore
      //   376: dup
      //   377: iconst_1
      //   378: aload 13
      //   380: getfield 278	android/util/Pair:first	Ljava/lang/Object;
      //   383: checkcast 98	java/lang/Long
      //   386: invokevirtual 105	java/lang/Long:longValue	()J
      //   389: ldc2_w 279
      //   392: ldiv
      //   393: invokestatic 286	android/text/format/DateUtils:formatElapsedTime	(J)Ljava/lang/String;
      //   396: aastore
      //   397: dup
      //   398: iconst_2
      //   399: aload 13
      //   401: getfield 270	android/util/Pair:second	Ljava/lang/Object;
      //   404: checkcast 98	java/lang/Long
      //   407: invokevirtual 105	java/lang/Long:longValue	()J
      //   410: ldc2_w 279
      //   413: ldiv
      //   414: invokestatic 286	android/text/format/DateUtils:formatElapsedTime	(J)Ljava/lang/String;
      //   417: aastore
      //   418: dup
      //   419: iconst_3
      //   420: lload 6
      //   422: ldc2_w 279
      //   425: ldiv
      //   426: invokestatic 286	android/text/format/DateUtils:formatElapsedTime	(J)Ljava/lang/String;
      //   429: aastore
      //   430: dup
      //   431: iconst_4
      //   432: lload 8
      //   434: ldc2_w 279
      //   437: ldiv
      //   438: invokestatic 286	android/text/format/DateUtils:formatElapsedTime	(J)Ljava/lang/String;
      //   441: aastore
      //   442: invokestatic 290	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   445: invokestatic 293	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   448: pop
      //   449: lload 6
      //   451: invokestatic 116	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   454: lload 8
      //   456: invokestatic 116	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   459: invokestatic 122	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
      //   462: astore 12
      //   464: aload_0
      //   465: aload 15
      //   467: aload_1
      //   468: invokespecial 295	com/android/server/job/JobStore$ReadJobMapFromDiskRunnable:maybeBuildBackoffPolicyFromXml	(Landroid/app/job/JobInfo$Builder;Lorg/xmlpull/v1/XmlPullParser;)V
      //   471: aload_1
      //   472: invokeinterface 298 1 0
      //   477: pop
      //   478: aload_1
      //   479: invokeinterface 168 1 0
      //   484: istore 4
      //   486: iload 4
      //   488: iconst_4
      //   489: if_icmpeq -11 -> 478
      //   492: iload 4
      //   494: iconst_2
      //   495: if_icmpne +160 -> 655
      //   498: ldc_w 300
      //   501: aload_1
      //   502: invokeinterface 182 1 0
      //   507: invokevirtual 199	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   510: istore 5
      //   512: iload 5
      //   514: ifne +147 -> 661
      //   517: aconst_null
      //   518: areturn
      //   519: astore_1
      //   520: aconst_null
      //   521: areturn
      //   522: lload 8
      //   524: lstore 6
      //   526: goto -220 -> 306
      //   529: astore_1
      //   530: ldc -86
      //   532: ldc_w 302
      //   535: invokestatic 191	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   538: pop
      //   539: aconst_null
      //   540: areturn
      //   541: ldc_w 304
      //   544: aload_1
      //   545: invokeinterface 182 1 0
      //   550: invokevirtual 199	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   553: ifeq +100 -> 653
      //   556: aload 13
      //   558: getfield 278	android/util/Pair:first	Ljava/lang/Object;
      //   561: checkcast 98	java/lang/Long
      //   564: invokevirtual 105	java/lang/Long:longValue	()J
      //   567: lconst_0
      //   568: lcmp
      //   569: ifeq +23 -> 592
      //   572: aload 15
      //   574: aload 13
      //   576: getfield 278	android/util/Pair:first	Ljava/lang/Object;
      //   579: checkcast 98	java/lang/Long
      //   582: invokevirtual 105	java/lang/Long:longValue	()J
      //   585: lload 10
      //   587: lsub
      //   588: invokevirtual 308	android/app/job/JobInfo$Builder:setMinimumLatency	(J)Landroid/app/job/JobInfo$Builder;
      //   591: pop
      //   592: aload 13
      //   594: astore 12
      //   596: aload 13
      //   598: getfield 270	android/util/Pair:second	Ljava/lang/Object;
      //   601: checkcast 98	java/lang/Long
      //   604: invokevirtual 105	java/lang/Long:longValue	()J
      //   607: ldc2_w 93
      //   610: lcmp
      //   611: ifeq -147 -> 464
      //   614: aload 15
      //   616: aload 13
      //   618: getfield 270	android/util/Pair:second	Ljava/lang/Object;
      //   621: checkcast 98	java/lang/Long
      //   624: invokevirtual 105	java/lang/Long:longValue	()J
      //   627: lload 10
      //   629: lsub
      //   630: invokevirtual 311	android/app/job/JobInfo$Builder:setOverrideDeadline	(J)Landroid/app/job/JobInfo$Builder;
      //   633: pop
      //   634: aload 13
      //   636: astore 12
      //   638: goto -174 -> 464
      //   641: astore_1
      //   642: ldc -86
      //   644: ldc_w 313
      //   647: invokestatic 191	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   650: pop
      //   651: aconst_null
      //   652: areturn
      //   653: aconst_null
      //   654: areturn
      //   655: iconst_0
      //   656: istore 5
      //   658: goto -146 -> 512
      //   661: aload_1
      //   662: invokestatic 319	android/os/PersistableBundle:restoreFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/os/PersistableBundle;
      //   665: astore 13
      //   667: aload 15
      //   669: aload 13
      //   671: invokevirtual 323	android/app/job/JobInfo$Builder:setExtras	(Landroid/os/PersistableBundle;)Landroid/app/job/JobInfo$Builder;
      //   674: pop
      //   675: aload_1
      //   676: invokeinterface 298 1 0
      //   681: pop
      //   682: aload 14
      //   684: astore_1
      //   685: ldc_w 325
      //   688: aload 14
      //   690: invokevirtual 199	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   693: ifeq +37 -> 730
      //   696: aload 14
      //   698: astore_1
      //   699: aload 13
      //   701: ifnull +29 -> 730
      //   704: aload 14
      //   706: astore_1
      //   707: aload 13
      //   709: ldc_w 327
      //   712: iconst_0
      //   713: invokevirtual 331	android/os/PersistableBundle:getBoolean	(Ljava/lang/String;Z)Z
      //   716: ifeq +14 -> 730
      //   719: aload 13
      //   721: ldc_w 333
      //   724: aload 14
      //   726: invokevirtual 336	android/os/PersistableBundle:getString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      //   729: astore_1
      //   730: new 338	com/android/server/job/controllers/JobStatus
      //   733: dup
      //   734: aload 15
      //   736: invokevirtual 342	android/app/job/JobInfo$Builder:build	()Landroid/app/job/JobInfo;
      //   739: iload_3
      //   740: aload_1
      //   741: iload_2
      //   742: aload 16
      //   744: aload 12
      //   746: getfield 278	android/util/Pair:first	Ljava/lang/Object;
      //   749: checkcast 98	java/lang/Long
      //   752: invokevirtual 105	java/lang/Long:longValue	()J
      //   755: aload 12
      //   757: getfield 270	android/util/Pair:second	Ljava/lang/Object;
      //   760: checkcast 98	java/lang/Long
      //   763: invokevirtual 105	java/lang/Long:longValue	()J
      //   766: invokespecial 345	com/android/server/job/controllers/JobStatus:<init>	(Landroid/app/job/JobInfo;ILjava/lang/String;ILjava/lang/String;JJ)V
      //   769: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	770	0	this	ReadJobMapFromDiskRunnable
      //   0	770	1	paramXmlPullParser	XmlPullParser
      //   98	644	2	i	int
      //   26	714	3	j	int
      //   127	369	4	k	int
      //   152	505	5	bool	boolean
      //   304	221	6	l1	long
      //   277	246	8	l2	long
      //   244	384	10	l3	long
      //   36	720	12	localObject1	Object
      //   239	481	13	localObject2	Object
      //   108	617	14	str1	String
      //   5	730	15	localBuilder	JobInfo.Builder
      //   119	624	16	str2	String
      // Exception table:
      //   from	to	target	type
      //   0	38	170	java/lang/NumberFormatException
      //   43	54	170	java/lang/NumberFormatException
      //   54	65	170	java/lang/NumberFormatException
      //   70	81	170	java/lang/NumberFormatException
      //   81	92	170	java/lang/NumberFormatException
      //   161	167	170	java/lang/NumberFormatException
      //   187	194	223	java/lang/NumberFormatException
      //   234	241	519	java/lang/NumberFormatException
      //   261	291	529	java/lang/NumberFormatException
      //   296	306	529	java/lang/NumberFormatException
      //   306	316	529	java/lang/NumberFormatException
      //   320	343	529	java/lang/NumberFormatException
      //   360	464	529	java/lang/NumberFormatException
      //   556	592	641	java/lang/NumberFormatException
      //   596	634	641	java/lang/NumberFormatException
    }
    
    public void run()
    {
      try
      {
        FileInputStream localFileInputStream = JobStore.-get0(JobStore.this).openRead();
        synchronized (JobStore.this.mLock)
        {
          List localList = readJobMapImpl(localFileInputStream);
          if (localList != null)
          {
            int i = 0;
            while (i < localList.size())
            {
              this.jobSet.add((JobStatus)localList.get(i));
              i += 1;
            }
          }
          localFileInputStream.close();
          return;
        }
        return;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        return;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        return;
      }
      catch (IOException localIOException) {}
    }
  }
  
  private class WriteJobsMapToDiskRunnable
    implements Runnable
  {
    private WriteJobsMapToDiskRunnable() {}
    
    private void addAttributesToJobTag(XmlSerializer paramXmlSerializer, JobStatus paramJobStatus)
      throws IOException
    {
      paramXmlSerializer.attribute(null, "jobid", Integer.toString(paramJobStatus.getJobId()));
      paramXmlSerializer.attribute(null, "package", paramJobStatus.getServiceComponent().getPackageName());
      paramXmlSerializer.attribute(null, "class", paramJobStatus.getServiceComponent().getClassName());
      if (paramJobStatus.getSourcePackageName() != null) {
        paramXmlSerializer.attribute(null, "sourcePackageName", paramJobStatus.getSourcePackageName());
      }
      if (paramJobStatus.getSourceTag() != null) {
        paramXmlSerializer.attribute(null, "sourceTag", paramJobStatus.getSourceTag());
      }
      paramXmlSerializer.attribute(null, "sourceUserId", String.valueOf(paramJobStatus.getSourceUserId()));
      paramXmlSerializer.attribute(null, "uid", Integer.toString(paramJobStatus.getUid()));
      paramXmlSerializer.attribute(null, "priority", String.valueOf(paramJobStatus.getPriority()));
      paramXmlSerializer.attribute(null, "flags", String.valueOf(paramJobStatus.getFlags()));
    }
    
    private PersistableBundle deepCopyBundle(PersistableBundle paramPersistableBundle, int paramInt)
    {
      if (paramInt <= 0) {
        return null;
      }
      PersistableBundle localPersistableBundle = (PersistableBundle)paramPersistableBundle.clone();
      paramPersistableBundle = paramPersistableBundle.keySet().iterator();
      while (paramPersistableBundle.hasNext())
      {
        String str = (String)paramPersistableBundle.next();
        Object localObject = localPersistableBundle.get(str);
        if ((localObject instanceof PersistableBundle)) {
          localPersistableBundle.putPersistableBundle(str, deepCopyBundle((PersistableBundle)localObject, paramInt - 1));
        }
      }
      return localPersistableBundle;
    }
    
    private void writeBundleToXml(PersistableBundle paramPersistableBundle, XmlSerializer paramXmlSerializer)
      throws IOException, XmlPullParserException
    {
      paramXmlSerializer.startTag(null, "extras");
      deepCopyBundle(paramPersistableBundle, 10).saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "extras");
    }
    
    private void writeConstraintsToXml(XmlSerializer paramXmlSerializer, JobStatus paramJobStatus)
      throws IOException
    {
      paramXmlSerializer.startTag(null, "constraints");
      if (paramJobStatus.hasConnectivityConstraint()) {
        paramXmlSerializer.attribute(null, "connectivity", Boolean.toString(true));
      }
      if (paramJobStatus.hasUnmeteredConstraint()) {
        paramXmlSerializer.attribute(null, "unmetered", Boolean.toString(true));
      }
      if (paramJobStatus.hasNotRoamingConstraint()) {
        paramXmlSerializer.attribute(null, "not-roaming", Boolean.toString(true));
      }
      if (paramJobStatus.hasIdleConstraint()) {
        paramXmlSerializer.attribute(null, "idle", Boolean.toString(true));
      }
      if (paramJobStatus.hasChargingConstraint()) {
        paramXmlSerializer.attribute(null, "charging", Boolean.toString(true));
      }
      paramXmlSerializer.endTag(null, "constraints");
    }
    
    private void writeExecutionCriteriaToXml(XmlSerializer paramXmlSerializer, JobStatus paramJobStatus)
      throws IOException
    {
      JobInfo localJobInfo = paramJobStatus.getJob();
      if (paramJobStatus.getJob().isPeriodic())
      {
        paramXmlSerializer.startTag(null, "periodic");
        paramXmlSerializer.attribute(null, "period", Long.toString(localJobInfo.getIntervalMillis()));
        paramXmlSerializer.attribute(null, "flex", Long.toString(localJobInfo.getFlexMillis()));
      }
      for (;;)
      {
        if (paramJobStatus.hasDeadlineConstraint()) {
          paramXmlSerializer.attribute(null, "deadline", Long.toString(System.currentTimeMillis() + (paramJobStatus.getLatestRunTimeElapsed() - SystemClock.elapsedRealtime())));
        }
        if (paramJobStatus.hasTimingDelayConstraint()) {
          paramXmlSerializer.attribute(null, "delay", Long.toString(System.currentTimeMillis() + (paramJobStatus.getEarliestRunTime() - SystemClock.elapsedRealtime())));
        }
        if ((paramJobStatus.getJob().getInitialBackoffMillis() != 30000L) || (paramJobStatus.getJob().getBackoffPolicy() != 1))
        {
          paramXmlSerializer.attribute(null, "backoff-policy", Integer.toString(localJobInfo.getBackoffPolicy()));
          paramXmlSerializer.attribute(null, "initial-backoff", Long.toString(localJobInfo.getInitialBackoffMillis()));
        }
        if (!localJobInfo.isPeriodic()) {
          break;
        }
        paramXmlSerializer.endTag(null, "periodic");
        return;
        paramXmlSerializer.startTag(null, "one-off");
      }
      paramXmlSerializer.endTag(null, "one-off");
    }
    
    private void writeJobsMapImpl(List<JobStatus> paramList)
    {
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
        localFastXmlSerializer.setOutput(localByteArrayOutputStream, StandardCharsets.UTF_8.name());
        localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
        localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        localFastXmlSerializer.startTag(null, "job-info");
        localFastXmlSerializer.attribute(null, "version", Integer.toString(0));
        int i = 0;
        while (i < paramList.size())
        {
          JobStatus localJobStatus = (JobStatus)paramList.get(i);
          localFastXmlSerializer.startTag(null, "job");
          addAttributesToJobTag(localFastXmlSerializer, localJobStatus);
          writeConstraintsToXml(localFastXmlSerializer, localJobStatus);
          writeExecutionCriteriaToXml(localFastXmlSerializer, localJobStatus);
          writeBundleToXml(localJobStatus.getExtras(), localFastXmlSerializer);
          localFastXmlSerializer.endTag(null, "job");
          i += 1;
        }
        localFastXmlSerializer.endTag(null, "job-info");
        localFastXmlSerializer.endDocument();
        paramList = JobStore.-get0(JobStore.this).startWrite();
        paramList.write(localByteArrayOutputStream.toByteArray());
        JobStore.-get0(JobStore.this).finishWrite(paramList);
        JobStore.-set0(JobStore.this, 0);
        return;
      }
      catch (IOException paramList) {}catch (XmlPullParserException paramList) {}
    }
    
    public void run()
    {
      SystemClock.elapsedRealtime();
      final ArrayList localArrayList = new ArrayList();
      synchronized (JobStore.this.mLock)
      {
        JobStore.this.mJobSet.forEachJob(new JobStore.JobStatusFunctor()
        {
          public void process(JobStatus paramAnonymousJobStatus)
          {
            if (paramAnonymousJobStatus.isPersisted()) {
              localArrayList.add(new JobStatus(paramAnonymousJobStatus));
            }
          }
        });
        writeJobsMapImpl(localArrayList);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */