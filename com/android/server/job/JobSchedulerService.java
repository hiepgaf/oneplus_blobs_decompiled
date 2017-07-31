package com.android.server.job;

import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.app.IUidObserver.Stub;
import android.app.job.IJobScheduler.Stub;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.util.ArrayUtils;
import com.android.server.DeviceIdleController.LocalService;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.job.controllers.AppIdleController;
import com.android.server.job.controllers.BatteryController;
import com.android.server.job.controllers.ConnectivityController;
import com.android.server.job.controllers.ContentObserverController;
import com.android.server.job.controllers.DeviceIdleJobsController;
import com.android.server.job.controllers.IdleController;
import com.android.server.job.controllers.JobStatus;
import com.android.server.job.controllers.StateController;
import com.android.server.job.controllers.TimeController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import libcore.util.EmptyArray;

public final class JobSchedulerService
  extends SystemService
  implements StateChangedListener, JobCompletedListener
{
  public static final boolean DEBUG = false;
  private static final boolean ENFORCE_MAX_JOBS = true;
  private static final int MAX_JOBS_PER_APP = 100;
  private static final int MAX_JOB_CONTEXTS_COUNT = 16;
  static final int MSG_CHECK_JOB = 1;
  static final int MSG_CHECK_JOB_GREEDY = 3;
  static final int MSG_JOB_EXPIRED = 0;
  static final int MSG_STOP_JOB = 2;
  static final String TAG = "JobSchedulerService";
  final List<JobServiceContext> mActiveServices = new ArrayList();
  IBatteryStats mBatteryStats;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      ??? = paramAnonymousIntent.getAction();
      int j;
      int i;
      if ("android.intent.action.PACKAGE_CHANGED".equals(???))
      {
        ??? = JobSchedulerService.-wrap2(JobSchedulerService.this, paramAnonymousIntent);
        j = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
        if ((??? != null) && (j != -1))
        {
          paramAnonymousIntent = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
          if (paramAnonymousIntent != null)
          {
            i = 0;
            int k = paramAnonymousIntent.length;
            if ((i < k) && (!paramAnonymousIntent[i].equals(???))) {
              break label123;
            }
          }
        }
      }
      label123:
      label203:
      label337:
      do
      {
        do
        {
          do
          {
            do
            {
              try
              {
                i = UserHandle.getUserId(j);
                i = AppGlobals.getPackageManager().getApplicationEnabledSetting(???, i);
                if ((i == 2) || (i == 3)) {
                  JobSchedulerService.this.cancelJobsForUid(j, true);
                }
                return;
              }
              catch (RemoteException|IllegalArgumentException ???)
              {
                Slog.e("JobSchedulerService", "got IllegalArgumentException, since package might be already removed");
                return;
              }
              i += 1;
              break;
              Slog.w("JobSchedulerService", "PACKAGE_CHANGED for " + ??? + " / uid " + j);
              return;
              if (!"android.intent.action.PACKAGE_REMOVED".equals(???)) {
                break label203;
              }
            } while (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false));
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
            JobSchedulerService.this.cancelJobsForUid(i, true);
            return;
            if ("android.intent.action.USER_REMOVED".equals(???))
            {
              i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
              JobSchedulerService.this.cancelJobsForUser(i);
              return;
            }
            if (!"android.intent.action.QUERY_PACKAGE_RESTART".equals(???)) {
              break label337;
            }
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
            paramAnonymousIntent = paramAnonymousIntent.getData().getSchemeSpecificPart();
          } while (i == -1);
          for (;;)
          {
            synchronized (JobSchedulerService.this.mLock)
            {
              List localList = JobSchedulerService.this.mJobs.getJobsByUid(i);
              i = localList.size() - 1;
              if (i < 0) {
                break;
              }
              if (((JobStatus)localList.get(i)).getSourcePackageName().equals(paramAnonymousIntent))
              {
                setResultCode(-1);
                return;
              }
            }
            i -= 1;
          }
        } while (!"android.intent.action.PACKAGE_RESTARTED".equals(???));
        i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
        ??? = paramAnonymousIntent.getData().getSchemeSpecificPart();
      } while (i == -1);
      JobSchedulerService.this.cancelJobsForPackageAndUid(???, i);
    }
  };
  final Constants mConstants;
  List<StateController> mControllers;
  final JobHandler mHandler;
  final JobPackageTracker mJobPackageTracker = new JobPackageTracker();
  final JobSchedulerStub mJobSchedulerStub;
  final JobStore mJobs;
  DeviceIdleController.LocalService mLocalDeviceIdleController;
  final Object mLock = new Object();
  int mMaxActiveJobs = 1;
  final ArrayList<JobStatus> mPendingJobs = new ArrayList();
  PowerManager mPowerManager;
  boolean mReadyToRock;
  boolean mReportedActive;
  int[] mStartedUsers = EmptyArray.INT;
  boolean[] mTmpAssignAct = new boolean[16];
  JobStatus[] mTmpAssignContextIdToJobMap = new JobStatus[16];
  int[] mTmpAssignPreferredUidForContext = new int[16];
  private final IUidObserver mUidObserver = new IUidObserver.Stub()
  {
    public void onUidActive(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidGone(int paramAnonymousInt)
      throws RemoteException
    {
      JobSchedulerService.this.updateUidState(paramAnonymousInt, 16);
    }
    
    public void onUidIdle(int paramAnonymousInt)
      throws RemoteException
    {
      JobSchedulerService.this.cancelJobsForUid(paramAnonymousInt, false);
    }
    
    public void onUidStateChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {
      JobSchedulerService.this.updateUidState(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  final SparseIntArray mUidPriorityOverride = new SparseIntArray();
  
  public JobSchedulerService(Context paramContext)
  {
    super(paramContext);
    this.mHandler = new JobHandler(paramContext.getMainLooper());
    this.mConstants = new Constants(this.mHandler);
    this.mJobSchedulerStub = new JobSchedulerStub();
    this.mJobs = JobStore.initAndGet(this);
    this.mControllers = new ArrayList();
    this.mControllers.add(ConnectivityController.get(this));
    this.mControllers.add(TimeController.get(this));
    this.mControllers.add(IdleController.get(this));
    this.mControllers.add(BatteryController.get(this));
    this.mControllers.add(AppIdleController.get(this));
    this.mControllers.add(ContentObserverController.get(this));
    this.mControllers.add(DeviceIdleJobsController.get(this));
  }
  
  private int adjustJobPriority(int paramInt, JobStatus paramJobStatus)
  {
    int i = paramInt;
    float f;
    if (paramInt < 40)
    {
      f = this.mJobPackageTracker.getLoadFactor(paramJobStatus);
      if (f < this.mConstants.HEAVY_USE_FACTOR) {
        break label39;
      }
      i = paramInt - 80;
    }
    label39:
    do
    {
      return i;
      i = paramInt;
    } while (f < this.mConstants.MODERATE_USE_FACTOR);
    return paramInt - 40;
  }
  
  private void assignJobsToContextsLocked()
  {
    try
    {
      i = ActivityManagerNative.getDefault().getMemoryTrimLevel();
      switch (i)
      {
      default: 
        this.mMaxActiveJobs = this.mConstants.BG_NORMAL_JOB_COUNT;
        JobStatus[] arrayOfJobStatus = this.mTmpAssignContextIdToJobMap;
        arrayOfBoolean = this.mTmpAssignAct;
        arrayOfInt = this.mTmpAssignPreferredUidForContext;
        j = 0;
        i = 0;
        k = 0;
        while (k < 16)
        {
          localObject = (JobServiceContext)this.mActiveServices.get(k);
          localJobStatus = ((JobServiceContext)localObject).getRunningJob();
          arrayOfJobStatus[k] = localJobStatus;
          m = j;
          n = i;
          if (localJobStatus != null)
          {
            j += 1;
            m = j;
            n = i;
            if (localJobStatus.lastEvaluatedPriority >= 40)
            {
              n = i + 1;
              m = j;
            }
          }
          arrayOfBoolean[k] = false;
          arrayOfInt[k] = ((JobServiceContext)localObject).getPreferredUid();
          k += 1;
          j = m;
          i = n;
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      boolean[] arrayOfBoolean;
      int[] arrayOfInt;
      int j;
      Object localObject;
      JobStatus localJobStatus;
      for (;;)
      {
        i = 0;
        continue;
        this.mMaxActiveJobs = this.mConstants.BG_MODERATE_JOB_COUNT;
        continue;
        this.mMaxActiveJobs = this.mConstants.BG_LOW_JOB_COUNT;
        continue;
        this.mMaxActiveJobs = this.mConstants.BG_CRITICAL_JOB_COUNT;
      }
      int m = 0;
      int k = i;
      int n = j;
      if (m < this.mPendingJobs.size())
      {
        localObject = (JobStatus)this.mPendingJobs.get(m);
        if (findJobContextIdFromMap((JobStatus)localObject, localRemoteException) != -1)
        {
          j = k;
          i = n;
        }
        int i1;
        int i2;
        int i3;
        for (;;)
        {
          m += 1;
          n = i;
          k = j;
          break;
          int i4 = evaluateJobPriorityLocked((JobStatus)localObject);
          ((JobStatus)localObject).lastEvaluatedPriority = i4;
          i1 = Integer.MAX_VALUE;
          j = -1;
          i = 0;
          i2 = j;
          if (i < 16)
          {
            localJobStatus = localRemoteException[i];
            int i5 = arrayOfInt[i];
            if (localJobStatus != null) {
              break label454;
            }
            if (n >= this.mMaxActiveJobs)
            {
              i2 = i1;
              i3 = j;
              if (i4 < 40) {
                break label474;
              }
              i2 = i1;
              i3 = j;
              if (k >= this.mConstants.FG_JOB_COUNT) {
                break label474;
              }
            }
            if (i5 != ((JobStatus)localObject).getUid())
            {
              i2 = i1;
              i3 = j;
              if (i5 != -1) {
                break label474;
              }
            }
            i2 = i;
          }
          i = n;
          j = k;
          if (i2 != -1)
          {
            localRemoteException[i2] = localObject;
            arrayOfBoolean[i2] = true;
            n += 1;
            i = n;
            j = k;
            if (i4 >= 40)
            {
              j = k + 1;
              i = n;
            }
          }
        }
        label454:
        if (localJobStatus.getUid() != ((JobStatus)localObject).getUid())
        {
          i3 = j;
          i2 = i1;
        }
        for (;;)
        {
          label474:
          i += 1;
          i1 = i2;
          j = i3;
          break;
          i2 = i1;
          i3 = j;
          if (evaluateJobPriorityLocked(localJobStatus) < ((JobStatus)localObject).lastEvaluatedPriority)
          {
            i2 = i1;
            i3 = j;
            if (i1 > ((JobStatus)localObject).lastEvaluatedPriority)
            {
              i2 = ((JobStatus)localObject).lastEvaluatedPriority;
              i3 = i;
            }
          }
        }
      }
      this.mJobPackageTracker.noteConcurrency(n, k);
      int i = 0;
      if (i < 16)
      {
        k = 0;
        j = k;
        if (arrayOfBoolean[i] != 0)
        {
          if (((JobServiceContext)this.mActiveServices.get(i)).getRunningJob() == null) {
            break label632;
          }
          ((JobServiceContext)this.mActiveServices.get(i)).preemptExecutingJob();
          j = 1;
        }
        for (;;)
        {
          if (j == 0) {
            ((JobServiceContext)this.mActiveServices.get(i)).clearPreferredUid();
          }
          i += 1;
          break;
          label632:
          arrayOfInt = localRemoteException[i];
          j = 0;
          while (j < this.mControllers.size())
          {
            ((StateController)this.mControllers.get(j)).prepareForExecutionLocked(arrayOfInt);
            j += 1;
          }
          if (!((JobServiceContext)this.mActiveServices.get(i)).executeRunnableJob(arrayOfInt)) {
            Slog.d("JobSchedulerService", "Error executing " + arrayOfInt);
          }
          j = k;
          if (this.mPendingJobs.remove(arrayOfInt))
          {
            this.mJobPackageTracker.noteNonpending(arrayOfInt);
            j = k;
          }
        }
      }
    }
  }
  
  private void cancelJobImpl(JobStatus paramJobStatus1, JobStatus arg2)
  {
    stopTrackingJob(paramJobStatus1, ???, true);
    synchronized (this.mLock)
    {
      if (this.mPendingJobs.remove(paramJobStatus1)) {
        this.mJobPackageTracker.noteNonpending(paramJobStatus1);
      }
      stopJobOnServiceContextLocked(paramJobStatus1, 0);
      reportActive();
      return;
    }
  }
  
  static void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Job Scheduler (jobscheduler) dump options:");
    paramPrintWriter.println("  [-h] [package] ...");
    paramPrintWriter.println("    -h: print this help");
    paramPrintWriter.println("  [package] is an optional package name to limit the output to.");
  }
  
  private int evaluateJobPriorityLocked(JobStatus paramJobStatus)
  {
    int i = paramJobStatus.getPriority();
    if (i >= 30) {
      return adjustJobPriority(i, paramJobStatus);
    }
    int j = this.mUidPriorityOverride.get(paramJobStatus.getSourceUid(), 0);
    if (j != 0) {
      return adjustJobPriority(j, paramJobStatus);
    }
    return adjustJobPriority(i, paramJobStatus);
  }
  
  private String getPackageName(Intent paramIntent)
  {
    Object localObject = null;
    Uri localUri = paramIntent.getData();
    paramIntent = (Intent)localObject;
    if (localUri != null) {
      paramIntent = localUri.getSchemeSpecificPart();
    }
    return paramIntent;
  }
  
  private JobStatus getRescheduleJobForFailure(JobStatus paramJobStatus)
  {
    long l2 = SystemClock.elapsedRealtime();
    Object localObject = paramJobStatus.getJob();
    long l1 = ((JobInfo)localObject).getInitialBackoffMillis();
    int i = paramJobStatus.getNumFailures() + 1;
    switch (((JobInfo)localObject).getBackoffPolicy())
    {
    }
    for (l1 = Math.scalb((float)l1, i - 1);; l1 *= i)
    {
      localObject = new JobStatus(paramJobStatus, l2 + Math.min(l1, 18000000L), Long.MAX_VALUE, i);
      i = 0;
      while (i < this.mControllers.size())
      {
        ((StateController)this.mControllers.get(i)).rescheduleForFailure((JobStatus)localObject, paramJobStatus);
        i += 1;
      }
    }
    return (JobStatus)localObject;
  }
  
  private JobStatus getRescheduleJobForPeriodic(JobStatus paramJobStatus)
  {
    long l2 = SystemClock.elapsedRealtime();
    long l1 = 0L;
    if (paramJobStatus.hasDeadlineConstraint()) {
      l1 = Math.max(paramJobStatus.getLatestRunTimeElapsed() - l2, 0L);
    }
    long l3 = paramJobStatus.getJob().getFlexMillis();
    l1 = l2 + l1 + paramJobStatus.getJob().getIntervalMillis();
    return new JobStatus(paramJobStatus, l1 - l3, l1, 0);
  }
  
  private boolean isCurrentlyActiveLocked(JobStatus paramJobStatus)
  {
    int i = 0;
    while (i < this.mActiveServices.size())
    {
      JobStatus localJobStatus = ((JobServiceContext)this.mActiveServices.get(i)).getRunningJobUnsafeLocked();
      if ((localJobStatus != null) && (localJobStatus.matches(paramJobStatus.getUid(), paramJobStatus.getJobId()))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private String printContextIdToJobMap(JobStatus[] paramArrayOfJobStatus, String paramString)
  {
    paramString = new StringBuilder(paramString + ": ");
    int i = 0;
    if (i < paramArrayOfJobStatus.length)
    {
      StringBuilder localStringBuilder = paramString.append("(");
      if (paramArrayOfJobStatus[i] == null)
      {
        j = -1;
        label54:
        localStringBuilder = localStringBuilder.append(j);
        if (paramArrayOfJobStatus[i] != null) {
          break label104;
        }
      }
      label104:
      for (int j = -1;; j = paramArrayOfJobStatus[i].getUid())
      {
        localStringBuilder.append(j).append(")");
        i += 1;
        break;
        j = paramArrayOfJobStatus[i].getJobId();
        break label54;
      }
    }
    return paramString.toString();
  }
  
  private String printPendingQueue()
  {
    StringBuilder localStringBuilder = new StringBuilder("Pending queue: ");
    Iterator localIterator = this.mPendingJobs.iterator();
    while (localIterator.hasNext())
    {
      JobStatus localJobStatus = (JobStatus)localIterator.next();
      localStringBuilder.append("(").append(localJobStatus.getJob().getId()).append(", ").append(localJobStatus.getUid()).append(") ");
    }
    return localStringBuilder.toString();
  }
  
  private void startTrackingJob(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mJobs.add(paramJobStatus1);
      if (this.mReadyToRock)
      {
        int i = 0;
        while (i < this.mControllers.size())
        {
          StateController localStateController = (StateController)this.mControllers.get(i);
          if (bool) {
            localStateController.maybeStopTrackingJobLocked(paramJobStatus1, null, true);
          }
          localStateController.maybeStartTrackingJobLocked(paramJobStatus1, paramJobStatus2);
          i += 1;
        }
      }
      return;
    }
  }
  
  private boolean stopJobOnServiceContextLocked(JobStatus paramJobStatus, int paramInt)
  {
    int i = 0;
    while (i < this.mActiveServices.size())
    {
      JobServiceContext localJobServiceContext = (JobServiceContext)this.mActiveServices.get(i);
      JobStatus localJobStatus = localJobServiceContext.getRunningJob();
      if ((localJobStatus != null) && (localJobStatus.matches(paramJobStatus.getUid(), paramJobStatus.getJobId())))
      {
        localJobServiceContext.cancelExecutingJob(paramInt);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean stopTrackingJob(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      paramBoolean = this.mJobs.remove(paramJobStatus1, paramBoolean);
      if ((paramBoolean) && (this.mReadyToRock))
      {
        int i = 0;
        while (i < this.mControllers.size())
        {
          ((StateController)this.mControllers.get(i)).maybeStopTrackingJobLocked(paramJobStatus1, paramJobStatus2, false);
          i += 1;
        }
      }
      return paramBoolean;
    }
  }
  
  public void cancelJob(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      JobStatus localJobStatus = this.mJobs.getJobByUidAndJobId(paramInt1, paramInt2);
      if (localJobStatus != null) {
        cancelJobImpl(localJobStatus, null);
      }
      return;
    }
  }
  
  void cancelJobsForPackageAndUid(String paramString, int paramInt)
  {
    synchronized (this.mLock)
    {
      List localList = this.mJobs.getJobsByUid(paramInt);
      paramInt = localList.size() - 1;
      if (paramInt >= 0)
      {
        ??? = (JobStatus)localList.get(paramInt);
        if (((JobStatus)???).getSourcePackageName().equals(paramString)) {
          cancelJobImpl((JobStatus)???, null);
        }
        paramInt -= 1;
      }
    }
  }
  
  public void cancelJobsForUid(int paramInt, boolean paramBoolean)
  {
    int i;
    String str;
    int j;
    synchronized (this.mLock)
    {
      List localList = this.mJobs.getJobsByUid(paramInt);
      i = 0;
      if (i < localList.size())
      {
        ??? = (JobStatus)localList.get(i);
        if (!paramBoolean) {
          str = ((JobStatus)???).getServiceComponent().getPackageName();
        }
      }
    }
  }
  
  void cancelJobsForUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      List localList = this.mJobs.getJobsByUser(paramInt);
      paramInt = 0;
      if (paramInt < localList.size())
      {
        cancelJobImpl((JobStatus)localList.get(paramInt), null);
        paramInt += 1;
      }
    }
  }
  
  void dumpInternal(PrintWriter paramPrintWriter, String[] arg2)
  {
    int k = -1;
    int i = k;
    Object localObject1;
    if (!ArrayUtils.isEmpty(???))
    {
      j = 0;
      while (j < ???.length)
      {
        localObject1 = ???[j];
        if ("-h".equals(localObject1))
        {
          dumpHelp(paramPrintWriter);
          return;
        }
        if ("-a".equals(localObject1))
        {
          j += 1;
        }
        else if ((((String)localObject1).length() > 0) && (((String)localObject1).charAt(0) == '-'))
        {
          paramPrintWriter.println("Unknown option: " + (String)localObject1);
          return;
        }
      }
      i = k;
      if (j < ???.length) {
        ??? = ???[j];
      }
    }
    long l;
    try
    {
      i = getContext().getPackageManager().getPackageUid(???, 8192);
      k = UserHandle.getAppId(i);
      l = SystemClock.elapsedRealtime();
      synchronized (this.mLock)
      {
        this.mConstants.dump(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.println("Started users: " + Arrays.toString(this.mStartedUsers));
        paramPrintWriter.print("Registered ");
        paramPrintWriter.print(this.mJobs.size());
        paramPrintWriter.println(" jobs:");
        if (this.mJobs.size() > 0)
        {
          localObject1 = this.mJobs.mJobSet.getAllJobs();
          Collections.sort((List)localObject1, new Comparator()
          {
            public int compare(JobStatus paramAnonymousJobStatus1, JobStatus paramAnonymousJobStatus2)
            {
              int i = paramAnonymousJobStatus1.getUid();
              int j = paramAnonymousJobStatus2.getUid();
              int k = paramAnonymousJobStatus1.getJobId();
              int m = paramAnonymousJobStatus2.getJobId();
              if (i != j)
              {
                if (i < j) {
                  return -1;
                }
                return 1;
              }
              if (k < m) {
                return -1;
              }
              if (k > m) {
                return 1;
              }
              return 0;
            }
          });
          localObject1 = ((Iterable)localObject1).iterator();
          JobStatus localJobStatus;
          do
          {
            if (!((Iterator)localObject1).hasNext()) {
              break;
            }
            localJobStatus = (JobStatus)((Iterator)localObject1).next();
            paramPrintWriter.print("  JOB #");
            localJobStatus.printUniqueId(paramPrintWriter);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localJobStatus.toShortStringExceptUniqueId());
          } while (!localJobStatus.shouldDump(k));
          localJobStatus.dump(paramPrintWriter, "    ", true);
          paramPrintWriter.print("    Ready: ");
          paramPrintWriter.print(JobHandler.-wrap1(this.mHandler, localJobStatus));
          paramPrintWriter.print(" (job=");
          paramPrintWriter.print(localJobStatus.isReady());
          paramPrintWriter.print(" pending=");
          paramPrintWriter.print(this.mPendingJobs.contains(localJobStatus));
          paramPrintWriter.print(" active=");
          paramPrintWriter.print(isCurrentlyActiveLocked(localJobStatus));
          paramPrintWriter.print(" user=");
          paramPrintWriter.print(ArrayUtils.contains(this.mStartedUsers, localJobStatus.getUserId()));
          paramPrintWriter.println(")");
        }
      }
      paramPrintWriter.println("  None.");
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      paramPrintWriter.println("Invalid package: " + ???);
      return;
    }
    break label1036;
    while (j < this.mControllers.size())
    {
      paramPrintWriter.println();
      ((StateController)this.mControllers.get(j)).dumpControllerStateLocked(paramPrintWriter, k);
      j += 1;
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Uid priority overrides:");
    int j = 0;
    label549:
    Object localObject2;
    if (j < this.mUidPriorityOverride.size())
    {
      int m = this.mUidPriorityOverride.keyAt(j);
      if ((k == -1) || (k == UserHandle.getAppId(m)))
      {
        paramPrintWriter.print("  ");
        paramPrintWriter.print(UserHandle.formatUid(m));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(this.mUidPriorityOverride.valueAt(j));
      }
    }
    else
    {
      paramPrintWriter.println();
      this.mJobPackageTracker.dump(paramPrintWriter, "", k);
      paramPrintWriter.println();
      if (this.mJobPackageTracker.dumpHistory(paramPrintWriter, "", k)) {
        paramPrintWriter.println();
      }
      paramPrintWriter.println("Pending queue:");
      j = 0;
      while (j < this.mPendingJobs.size())
      {
        localObject2 = (JobStatus)this.mPendingJobs.get(j);
        paramPrintWriter.print("  Pending #");
        paramPrintWriter.print(j);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(((JobStatus)localObject2).toShortString());
        ((JobStatus)localObject2).dump(paramPrintWriter, "    ", false);
        k = evaluateJobPriorityLocked((JobStatus)localObject2);
        if (k != 0)
        {
          paramPrintWriter.print("    Evaluated priority: ");
          paramPrintWriter.println(k);
        }
        paramPrintWriter.print("    Tag: ");
        paramPrintWriter.println(((JobStatus)localObject2).getTag());
        j += 1;
      }
      paramPrintWriter.println();
      paramPrintWriter.println("Active jobs:");
      j = 0;
    }
    for (;;)
    {
      if (j < this.mActiveServices.size())
      {
        localObject2 = (JobServiceContext)this.mActiveServices.get(j);
        paramPrintWriter.print("  Slot #");
        paramPrintWriter.print(j);
        paramPrintWriter.print(": ");
        if (((JobServiceContext)localObject2).getRunningJob() == null)
        {
          paramPrintWriter.println("inactive");
        }
        else
        {
          paramPrintWriter.println(((JobServiceContext)localObject2).getRunningJob().toShortString());
          paramPrintWriter.print("    Running for: ");
          TimeUtils.formatDuration(l - ((JobServiceContext)localObject2).getExecutionStartTimeElapsed(), paramPrintWriter);
          paramPrintWriter.print(", timeout at: ");
          TimeUtils.formatDuration(((JobServiceContext)localObject2).getTimeoutElapsed() - l, paramPrintWriter);
          paramPrintWriter.println();
          ((JobServiceContext)localObject2).getRunningJob().dump(paramPrintWriter, "    ", false);
          k = evaluateJobPriorityLocked(((JobServiceContext)localObject2).getRunningJob());
          if (k != 0)
          {
            paramPrintWriter.print("    Evaluated priority: ");
            paramPrintWriter.println(k);
          }
        }
      }
      else
      {
        if (i == -1)
        {
          paramPrintWriter.println();
          paramPrintWriter.print("mReadyToRock=");
          paramPrintWriter.println(this.mReadyToRock);
          paramPrintWriter.print("mReportedActive=");
          paramPrintWriter.println(this.mReportedActive);
          paramPrintWriter.print("mMaxActiveJobs=");
          paramPrintWriter.println(this.mMaxActiveJobs);
        }
        paramPrintWriter.println();
        return;
        label1036:
        j = 0;
        break;
        j += 1;
        break label549;
      }
      j += 1;
    }
  }
  
  int executeRunCommand(String arg1, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    try
    {
      paramInt1 = AppGlobals.getPackageManager().getPackageUid(???, 0, paramInt1);
      if (paramInt1 < 0) {
        return 64536;
      }
      synchronized (this.mLock)
      {
        JobStatus localJobStatus = this.mJobs.getJobByUidAndJobId(paramInt1, paramInt2);
        if (localJobStatus == null) {
          return 64535;
        }
        if (paramBoolean) {}
        for (paramInt1 = 2;; paramInt1 = 1)
        {
          localJobStatus.overrideState = paramInt1;
          if (localJobStatus.isConstraintsSatisfied()) {
            break;
          }
          localJobStatus.overrideState = 0;
          return 64534;
        }
        this.mHandler.obtainMessage(3).sendToTarget();
        return 0;
      }
      return 0;
    }
    catch (RemoteException ???) {}
  }
  
  int findJobContextIdFromMap(JobStatus paramJobStatus, JobStatus[] paramArrayOfJobStatus)
  {
    int i = 0;
    while (i < paramArrayOfJobStatus.length)
    {
      if ((paramArrayOfJobStatus[i] != null) && (paramArrayOfJobStatus[i].matches(paramJobStatus.getUid(), paramJobStatus.getJobId()))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public JobStore getJobStore()
  {
    return this.mJobs;
  }
  
  public Object getLock()
  {
    return this.mLock;
  }
  
  public JobInfo getPendingJob(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mJobs.getJobsByUid(paramInt1);
      paramInt1 = ((List)localObject2).size() - 1;
      while (paramInt1 >= 0)
      {
        JobStatus localJobStatus = (JobStatus)((List)localObject2).get(paramInt1);
        if (localJobStatus.getJobId() == paramInt2)
        {
          localObject2 = localJobStatus.getJob();
          return (JobInfo)localObject2;
        }
        paramInt1 -= 1;
      }
      return null;
    }
  }
  
  public List<JobInfo> getPendingJobs(int paramInt)
  {
    synchronized (this.mLock)
    {
      List localList = this.mJobs.getJobsByUid(paramInt);
      ArrayList localArrayList = new ArrayList(localList.size());
      paramInt = localList.size() - 1;
      while (paramInt >= 0)
      {
        localArrayList.add(((JobStatus)localList.get(paramInt)).getJob());
        paramInt -= 1;
      }
      return localArrayList;
    }
  }
  
  void noteJobsNonpending(List<JobStatus> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      JobStatus localJobStatus = (JobStatus)paramList.get(i);
      this.mJobPackageTracker.noteNonpending(localJobStatus);
      i -= 1;
    }
  }
  
  void noteJobsPending(List<JobStatus> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      JobStatus localJobStatus = (JobStatus)paramList.get(i);
      this.mJobPackageTracker.notePending(localJobStatus);
      i -= 1;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (500 == paramInt)
    {
      this.mConstants.start(getContext().getContentResolver());
      ??? = new IntentFilter();
      ((IntentFilter)???).addAction("android.intent.action.PACKAGE_REMOVED");
      ((IntentFilter)???).addAction("android.intent.action.PACKAGE_CHANGED");
      ((IntentFilter)???).addAction("android.intent.action.PACKAGE_RESTARTED");
      ((IntentFilter)???).addAction("android.intent.action.QUERY_PACKAGE_RESTART");
      ((IntentFilter)???).addDataScheme("package");
      getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, (IntentFilter)???, null, null);
      ??? = new IntentFilter("android.intent.action.USER_REMOVED");
      getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, (IntentFilter)???, null, null);
      this.mPowerManager = ((PowerManager)getContext().getSystemService("power"));
    }
    for (;;)
    {
      try
      {
        ActivityManagerNative.getDefault().registerUidObserver(this.mUidObserver, 7);
        return;
      }
      catch (RemoteException localRemoteException) {}
      if (paramInt == 600) {
        synchronized (this.mLock)
        {
          this.mReadyToRock = true;
          this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
          this.mLocalDeviceIdleController = ((DeviceIdleController.LocalService)LocalServices.getService(DeviceIdleController.LocalService.class));
          paramInt = 0;
          if (paramInt < 16)
          {
            this.mActiveServices.add(new JobServiceContext(this, this.mBatteryStats, this.mJobPackageTracker, getContext().getMainLooper()));
            paramInt += 1;
          }
          else
          {
            this.mJobs.forEachJob(new JobStore.JobStatusFunctor()
            {
              public void process(JobStatus paramAnonymousJobStatus)
              {
                int i = 0;
                while (i < JobSchedulerService.this.mControllers.size())
                {
                  ((StateController)JobSchedulerService.this.mControllers.get(i)).maybeStartTrackingJobLocked(paramAnonymousJobStatus, null);
                  i += 1;
                }
              }
            });
            this.mHandler.obtainMessage(1).sendToTarget();
            return;
          }
        }
      }
    }
  }
  
  public void onControllerStateChanged()
  {
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  public void onDeviceIdleStateChanged(boolean paramBoolean)
  {
    Object localObject1 = this.mLock;
    int i;
    if (paramBoolean) {
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i < this.mActiveServices.size())
        {
          JobServiceContext localJobServiceContext = (JobServiceContext)this.mActiveServices.get(i);
          JobStatus localJobStatus = localJobServiceContext.getRunningJob();
          if ((localJobStatus != null) && ((localJobStatus.getFlags() & 0x1) == 0))
          {
            localJobServiceContext.cancelExecutingJob(4);
            break label127;
            if ((this.mReadyToRock) && (this.mLocalDeviceIdleController != null) && (!this.mReportedActive))
            {
              this.mReportedActive = true;
              this.mLocalDeviceIdleController.setJobsActive(true);
            }
            this.mHandler.obtainMessage(1).sendToTarget();
          }
        }
        else
        {
          return;
        }
      }
      finally {}
      label127:
      i += 1;
    }
  }
  
  public void onJobCompleted(JobStatus paramJobStatus, boolean paramBoolean)
  {
    if (paramJobStatus.getJob().isPeriodic()) {}
    for (boolean bool = false; !stopTrackingJob(paramJobStatus, null, bool); bool = true)
    {
      this.mHandler.obtainMessage(3).sendToTarget();
      return;
    }
    if (paramBoolean) {
      startTrackingJob(getRescheduleJobForFailure(paramJobStatus), paramJobStatus);
    }
    for (;;)
    {
      reportActive();
      this.mHandler.obtainMessage(3).sendToTarget();
      return;
      if (paramJobStatus.getJob().isPeriodic()) {
        startTrackingJob(getRescheduleJobForPeriodic(paramJobStatus), paramJobStatus);
      }
    }
  }
  
  public void onRunJobNow(JobStatus paramJobStatus)
  {
    this.mHandler.obtainMessage(0, paramJobStatus).sendToTarget();
  }
  
  public void onStart()
  {
    publishLocalService(JobSchedulerInternal.class, new LocalService());
    publishBinderService("jobscheduler", this.mJobSchedulerStub);
  }
  
  public void onStartUser(int paramInt)
  {
    this.mStartedUsers = ArrayUtils.appendInt(this.mStartedUsers, paramInt);
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  public void onStopUser(int paramInt)
  {
    this.mStartedUsers = ArrayUtils.removeInt(this.mStartedUsers, paramInt);
  }
  
  public void onUnlockUser(int paramInt)
  {
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  void reportActive()
  {
    if (this.mPendingJobs.size() > 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      bool2 = bool1;
      if (this.mPendingJobs.size() > 0) {
        break label99;
      }
      int i = 0;
      for (;;)
      {
        bool2 = bool1;
        if (i >= this.mActiveServices.size()) {
          break label99;
        }
        JobStatus localJobStatus = ((JobServiceContext)this.mActiveServices.get(i)).getRunningJob();
        if ((localJobStatus != null) && ((localJobStatus.getJob().getFlags() & 0x1) == 0) && (!localJobStatus.dozeWhitelisted)) {
          break;
        }
        i += 1;
      }
    }
    boolean bool2 = true;
    label99:
    if (this.mReportedActive != bool2)
    {
      this.mReportedActive = bool2;
      if (this.mLocalDeviceIdleController != null) {
        this.mLocalDeviceIdleController.setJobsActive(bool2);
      }
    }
  }
  
  public int schedule(JobInfo paramJobInfo, int paramInt)
  {
    return scheduleAsPackage(paramJobInfo, paramInt, null, -1, null);
  }
  
  public int scheduleAsPackage(JobInfo paramJobInfo, int paramInt1, String paramString1, int paramInt2, String paramString2)
  {
    paramString2 = JobStatus.createFromJobInfo(paramJobInfo, paramInt1, paramString1, paramInt2, paramString2);
    try
    {
      if (ActivityManagerNative.getDefault().getAppStartMode(paramInt1, paramJobInfo.getService().getPackageName()) == 2)
      {
        Slog.w("JobSchedulerService", "Not scheduling job " + paramInt1 + ":" + paramJobInfo.toString() + " -- package not allowed to start");
        return 0;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Object localObject = this.mLock;
      if (paramString1 == null) {
        try
        {
          if (this.mJobs.countJobsForUid(paramInt1) > 100)
          {
            Slog.w("JobSchedulerService", "Too many jobs for uid " + paramInt1);
            throw new IllegalStateException("Apps may not schedule more than 100 distinct jobs");
          }
        }
        finally {}
      }
      paramJobInfo = this.mJobs.getJobByUidAndJobId(paramInt1, paramJobInfo.getId());
      if (paramJobInfo != null) {
        cancelJobImpl(paramJobInfo, paramString2);
      }
      startTrackingJob(paramString2, paramJobInfo);
      this.mHandler.obtainMessage(1).sendToTarget();
    }
    return 1;
  }
  
  void updateUidState(int paramInt1, int paramInt2)
  {
    Object localObject1 = this.mLock;
    if (paramInt2 == 2) {}
    for (;;)
    {
      try
      {
        this.mUidPriorityOverride.put(paramInt1, 40);
        return;
      }
      finally {}
      if (paramInt2 <= 4) {
        this.mUidPriorityOverride.put(paramInt1, 30);
      } else {
        this.mUidPriorityOverride.delete(paramInt1);
      }
    }
  }
  
  private final class Constants
    extends ContentObserver
  {
    private static final int DEFAULT_BG_CRITICAL_JOB_COUNT = 1;
    private static final int DEFAULT_BG_LOW_JOB_COUNT = 1;
    private static final int DEFAULT_BG_MODERATE_JOB_COUNT = 4;
    private static final int DEFAULT_BG_NORMAL_JOB_COUNT = 6;
    private static final int DEFAULT_FG_JOB_COUNT = 4;
    private static final float DEFAULT_HEAVY_USE_FACTOR = 0.9F;
    private static final int DEFAULT_MIN_CHARGING_COUNT = 1;
    private static final int DEFAULT_MIN_CONNECTIVITY_COUNT = 1;
    private static final int DEFAULT_MIN_CONTENT_COUNT = 1;
    private static final int DEFAULT_MIN_IDLE_COUNT = 1;
    private static final int DEFAULT_MIN_READY_JOBS_COUNT = 1;
    private static final float DEFAULT_MODERATE_USE_FACTOR = 0.5F;
    private static final String KEY_BG_CRITICAL_JOB_COUNT = "bg_critical_job_count";
    private static final String KEY_BG_LOW_JOB_COUNT = "bg_low_job_count";
    private static final String KEY_BG_MODERATE_JOB_COUNT = "bg_moderate_job_count";
    private static final String KEY_BG_NORMAL_JOB_COUNT = "bg_normal_job_count";
    private static final String KEY_FG_JOB_COUNT = "fg_job_count";
    private static final String KEY_HEAVY_USE_FACTOR = "heavy_use_factor";
    private static final String KEY_MIN_CHARGING_COUNT = "min_charging_count";
    private static final String KEY_MIN_CONNECTIVITY_COUNT = "min_connectivity_count";
    private static final String KEY_MIN_CONTENT_COUNT = "min_content_count";
    private static final String KEY_MIN_IDLE_COUNT = "min_idle_count";
    private static final String KEY_MIN_READY_JOBS_COUNT = "min_ready_jobs_count";
    private static final String KEY_MODERATE_USE_FACTOR = "moderate_use_factor";
    int BG_CRITICAL_JOB_COUNT = 1;
    int BG_LOW_JOB_COUNT = 1;
    int BG_MODERATE_JOB_COUNT = 4;
    int BG_NORMAL_JOB_COUNT = 6;
    int FG_JOB_COUNT = 4;
    float HEAVY_USE_FACTOR = 0.9F;
    int MIN_CHARGING_COUNT = 1;
    int MIN_CONNECTIVITY_COUNT = 1;
    int MIN_CONTENT_COUNT = 1;
    int MIN_IDLE_COUNT = 1;
    int MIN_READY_JOBS_COUNT = 1;
    float MODERATE_USE_FACTOR = 0.5F;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private ContentResolver mResolver;
    
    public Constants(Handler paramHandler)
    {
      super();
    }
    
    private void updateConstants()
    {
      synchronized (JobSchedulerService.this.mLock)
      {
        try
        {
          this.mParser.setString(Settings.Global.getString(this.mResolver, "alarm_manager_constants"));
          this.MIN_IDLE_COUNT = this.mParser.getInt("min_idle_count", 1);
          this.MIN_CHARGING_COUNT = this.mParser.getInt("min_charging_count", 1);
          this.MIN_CONNECTIVITY_COUNT = this.mParser.getInt("min_connectivity_count", 1);
          this.MIN_CONTENT_COUNT = this.mParser.getInt("min_content_count", 1);
          this.MIN_READY_JOBS_COUNT = this.mParser.getInt("min_ready_jobs_count", 1);
          this.HEAVY_USE_FACTOR = this.mParser.getFloat("heavy_use_factor", 0.9F);
          this.MODERATE_USE_FACTOR = this.mParser.getFloat("moderate_use_factor", 0.5F);
          this.FG_JOB_COUNT = this.mParser.getInt("fg_job_count", 4);
          this.BG_NORMAL_JOB_COUNT = this.mParser.getInt("bg_normal_job_count", 6);
          if (this.FG_JOB_COUNT + this.BG_NORMAL_JOB_COUNT > 16) {
            this.BG_NORMAL_JOB_COUNT = (16 - this.FG_JOB_COUNT);
          }
          this.BG_MODERATE_JOB_COUNT = this.mParser.getInt("bg_moderate_job_count", 4);
          if (this.FG_JOB_COUNT + this.BG_MODERATE_JOB_COUNT > 16) {
            this.BG_MODERATE_JOB_COUNT = (16 - this.FG_JOB_COUNT);
          }
          this.BG_LOW_JOB_COUNT = this.mParser.getInt("bg_low_job_count", 1);
          if (this.FG_JOB_COUNT + this.BG_LOW_JOB_COUNT > 16) {
            this.BG_LOW_JOB_COUNT = (16 - this.FG_JOB_COUNT);
          }
          this.BG_CRITICAL_JOB_COUNT = this.mParser.getInt("bg_critical_job_count", 1);
          if (this.FG_JOB_COUNT + this.BG_CRITICAL_JOB_COUNT > 16) {
            this.BG_CRITICAL_JOB_COUNT = (16 - this.FG_JOB_COUNT);
          }
          return;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            Slog.e("JobSchedulerService", "Bad device idle settings", localIllegalArgumentException);
          }
        }
      }
    }
    
    void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("  Settings:");
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_idle_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MIN_IDLE_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_charging_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MIN_CHARGING_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_connectivity_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MIN_CONNECTIVITY_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_content_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MIN_CONTENT_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_ready_jobs_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MIN_READY_JOBS_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("heavy_use_factor");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.HEAVY_USE_FACTOR);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("moderate_use_factor");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.MODERATE_USE_FACTOR);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("fg_job_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.FG_JOB_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("bg_normal_job_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.BG_NORMAL_JOB_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("bg_moderate_job_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.BG_MODERATE_JOB_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("bg_low_job_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.BG_LOW_JOB_COUNT);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("bg_critical_job_count");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.BG_CRITICAL_JOB_COUNT);
      paramPrintWriter.println();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      updateConstants();
    }
    
    public void start(ContentResolver paramContentResolver)
    {
      this.mResolver = paramContentResolver;
      this.mResolver.registerContentObserver(Settings.Global.getUriFor("job_scheduler_constants"), false, this);
      updateConstants();
    }
  }
  
  private class JobHandler
    extends Handler
  {
    private final MaybeReadyJobQueueFunctor mMaybeQueueFunctor = new MaybeReadyJobQueueFunctor();
    private final ReadyJobQueueFunctor mReadyQueueFunctor = new ReadyJobQueueFunctor();
    
    public JobHandler(Looper paramLooper)
    {
      super();
    }
    
    private boolean areJobConstraintsNotSatisfiedLocked(JobStatus paramJobStatus)
    {
      if (!paramJobStatus.isReady()) {
        return JobSchedulerService.-wrap0(JobSchedulerService.this, paramJobStatus);
      }
      return false;
    }
    
    private boolean isReadyToBeExecutedLocked(JobStatus paramJobStatus)
    {
      boolean bool1 = paramJobStatus.isReady();
      boolean bool2 = JobSchedulerService.this.mPendingJobs.contains(paramJobStatus);
      boolean bool3 = JobSchedulerService.-wrap0(JobSchedulerService.this, paramJobStatus);
      int i = paramJobStatus.getUserId();
      boolean bool4 = ArrayUtils.contains(JobSchedulerService.this.mStartedUsers, i);
      try
      {
        paramJobStatus = AppGlobals.getPackageManager().getServiceInfo(paramJobStatus.getServiceComponent(), 268435456, i);
        if (paramJobStatus != null)
        {
          i = 1;
          if ((bool4) && (i != 0) && (bool1) && (!bool2)) {
            break label99;
          }
        }
        while (bool3)
        {
          return false;
          i = 0;
          break;
        }
      }
      catch (RemoteException paramJobStatus)
      {
        throw paramJobStatus.rethrowAsRuntimeException();
      }
      label99:
      return true;
    }
    
    private void maybeQueueReadyJobsForExecutionLockedH()
    {
      JobSchedulerService.this.noteJobsNonpending(JobSchedulerService.this.mPendingJobs);
      JobSchedulerService.this.mPendingJobs.clear();
      JobSchedulerService.this.mJobs.forEachJob(this.mMaybeQueueFunctor);
      this.mMaybeQueueFunctor.postProcess();
    }
    
    private void maybeRunPendingJobsH()
    {
      synchronized (JobSchedulerService.this.mLock)
      {
        JobSchedulerService.-wrap3(JobSchedulerService.this);
        JobSchedulerService.this.reportActive();
        return;
      }
    }
    
    private void queueReadyJobsForExecutionLockedH()
    {
      JobSchedulerService.this.noteJobsNonpending(JobSchedulerService.this.mPendingJobs);
      JobSchedulerService.this.mPendingJobs.clear();
      JobSchedulerService.this.mJobs.forEachJob(this.mReadyQueueFunctor);
      this.mReadyQueueFunctor.postProcess();
    }
    
    public void handleMessage(Message arg1)
    {
      for (;;)
      {
        synchronized (JobSchedulerService.this.mLock)
        {
          boolean bool = JobSchedulerService.this.mReadyToRock;
          if (!bool) {
            return;
          }
          switch (???.what)
          {
          default: 
            maybeRunPendingJobsH();
            removeMessages(1);
            return;
          }
        }
        for (;;)
        {
          synchronized (JobSchedulerService.this.mLock)
          {
            ??? = (JobStatus)???.obj;
            if ((??? == null) || (JobSchedulerService.this.mPendingJobs.contains(???)))
            {
              queueReadyJobsForExecutionLockedH();
              ??? = (Message)???;
              break;
            }
            if (!JobSchedulerService.this.mJobs.containsJob(???)) {
              continue;
            }
            JobSchedulerService.this.mJobPackageTracker.notePending(???);
            JobSchedulerService.this.mPendingJobs.add(???);
          }
          synchronized (JobSchedulerService.this.mLock)
          {
            if (JobSchedulerService.this.mReportedActive) {
              queueReadyJobsForExecutionLockedH();
            }
          }
          maybeQueueReadyJobsForExecutionLockedH();
        }
        synchronized (JobSchedulerService.this.mLock)
        {
          queueReadyJobsForExecutionLockedH();
        }
        JobSchedulerService.-wrap4(JobSchedulerService.this, (JobStatus)???.obj, null);
      }
    }
    
    class MaybeReadyJobQueueFunctor
      implements JobStore.JobStatusFunctor
    {
      int backoffCount;
      int chargingCount;
      int connectivityCount;
      int contentCount;
      int idleCount;
      List<JobStatus> runnableJobs;
      
      public MaybeReadyJobQueueFunctor()
      {
        reset();
      }
      
      private void reset()
      {
        this.chargingCount = 0;
        this.idleCount = 0;
        this.backoffCount = 0;
        this.connectivityCount = 0;
        this.contentCount = 0;
        this.runnableJobs = null;
      }
      
      public void postProcess()
      {
        if ((this.backoffCount > 0) || (this.idleCount >= JobSchedulerService.this.mConstants.MIN_IDLE_COUNT)) {}
        for (;;)
        {
          JobSchedulerService.this.noteJobsPending(this.runnableJobs);
          JobSchedulerService.this.mPendingJobs.addAll(this.runnableJobs);
          do
          {
            reset();
            return;
            if ((this.connectivityCount >= JobSchedulerService.this.mConstants.MIN_CONNECTIVITY_COUNT) || (this.chargingCount >= JobSchedulerService.this.mConstants.MIN_CHARGING_COUNT) || (this.contentCount >= JobSchedulerService.this.mConstants.MIN_CONTENT_COUNT)) {
              break;
            }
          } while ((this.runnableJobs == null) || (this.runnableJobs.size() < JobSchedulerService.this.mConstants.MIN_READY_JOBS_COUNT));
        }
      }
      
      public void process(JobStatus paramJobStatus)
      {
        if (JobSchedulerService.JobHandler.-wrap1(JobSchedulerService.JobHandler.this, paramJobStatus)) {
          try
          {
            if (ActivityManagerNative.getDefault().getAppStartMode(paramJobStatus.getUid(), paramJobStatus.getJob().getService().getPackageName()) == 2)
            {
              Slog.w("JobSchedulerService", "Aborting job " + paramJobStatus.getUid() + ":" + paramJobStatus.getJob().toString() + " -- package not allowed to start");
              JobSchedulerService.this.mHandler.obtainMessage(2, paramJobStatus).sendToTarget();
              return;
            }
          }
          catch (RemoteException localRemoteException)
          {
            if (paramJobStatus.getNumFailures() > 0) {
              this.backoffCount += 1;
            }
            if (paramJobStatus.hasIdleConstraint()) {
              this.idleCount += 1;
            }
            if ((paramJobStatus.hasConnectivityConstraint()) || (paramJobStatus.hasUnmeteredConstraint()) || (paramJobStatus.hasNotRoamingConstraint())) {
              this.connectivityCount += 1;
            }
            if (paramJobStatus.hasChargingConstraint()) {
              this.chargingCount += 1;
            }
            if (paramJobStatus.hasContentTriggerConstraint()) {
              this.contentCount += 1;
            }
            if (this.runnableJobs == null) {
              this.runnableJobs = new ArrayList();
            }
            this.runnableJobs.add(paramJobStatus);
          }
        }
        while (!JobSchedulerService.JobHandler.-wrap0(JobSchedulerService.JobHandler.this, paramJobStatus)) {
          return;
        }
        JobSchedulerService.-wrap1(JobSchedulerService.this, paramJobStatus, 1);
      }
    }
    
    class ReadyJobQueueFunctor
      implements JobStore.JobStatusFunctor
    {
      ArrayList<JobStatus> newReadyJobs;
      
      ReadyJobQueueFunctor() {}
      
      public void postProcess()
      {
        if (this.newReadyJobs != null)
        {
          JobSchedulerService.this.noteJobsPending(this.newReadyJobs);
          JobSchedulerService.this.mPendingJobs.addAll(this.newReadyJobs);
        }
        this.newReadyJobs = null;
      }
      
      public void process(JobStatus paramJobStatus)
      {
        if (JobSchedulerService.JobHandler.-wrap1(JobSchedulerService.JobHandler.this, paramJobStatus))
        {
          if (this.newReadyJobs == null) {
            this.newReadyJobs = new ArrayList();
          }
          this.newReadyJobs.add(paramJobStatus);
        }
        while (!JobSchedulerService.JobHandler.-wrap0(JobSchedulerService.JobHandler.this, paramJobStatus)) {
          return;
        }
        JobSchedulerService.-wrap1(JobSchedulerService.this, paramJobStatus, 1);
      }
    }
  }
  
  final class JobSchedulerStub
    extends IJobScheduler.Stub
  {
    private final SparseArray<Boolean> mPersistCache = new SparseArray();
    
    JobSchedulerStub() {}
    
    private boolean canPersistJobs(int paramInt1, int paramInt2)
    {
      for (;;)
      {
        synchronized (this.mPersistCache)
        {
          Boolean localBoolean = (Boolean)this.mPersistCache.get(paramInt2);
          if (localBoolean != null)
          {
            bool = localBoolean.booleanValue();
            return bool;
          }
          if (JobSchedulerService.this.getContext().checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", paramInt1, paramInt2) == 0)
          {
            bool = true;
            this.mPersistCache.put(paramInt2, Boolean.valueOf(bool));
          }
        }
        boolean bool = false;
      }
    }
    
    private void enforceValidJobRequest(int paramInt, JobInfo paramJobInfo)
    {
      Object localObject = AppGlobals.getPackageManager();
      paramJobInfo = paramJobInfo.getService();
      try
      {
        localObject = ((IPackageManager)localObject).getServiceInfo(paramJobInfo, 786432, UserHandle.getUserId(paramInt));
        if (localObject == null) {
          throw new IllegalArgumentException("No such service " + paramJobInfo);
        }
        if (((ServiceInfo)localObject).applicationInfo.uid != paramInt) {
          throw new IllegalArgumentException("uid " + paramInt + " cannot schedule job in " + paramJobInfo.getPackageName());
        }
        if (!"android.permission.BIND_JOB_SERVICE".equals(((ServiceInfo)localObject).permission)) {
          throw new IllegalArgumentException("Scheduled service " + paramJobInfo + " does not require android.permission.BIND_JOB_SERVICE permission");
        }
      }
      catch (RemoteException paramJobInfo) {}
    }
    
    public void cancel(int paramInt)
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        JobSchedulerService.this.cancelJob(i, paramInt);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void cancelAll()
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        JobSchedulerService.this.cancelJobsForUid(i, true);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      JobSchedulerService.this.getContext().enforceCallingOrSelfPermission("android.permission.DUMP", "JobSchedulerService");
      long l = Binder.clearCallingIdentity();
      try
      {
        JobSchedulerService.this.dumpInternal(paramPrintWriter, paramArrayOfString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public List<JobInfo> getAllPendingJobs()
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        List localList = JobSchedulerService.this.getPendingJobs(i);
        return localList;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public JobInfo getPendingJob(int paramInt)
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        JobInfo localJobInfo = JobSchedulerService.this.getPendingJob(i, paramInt);
        return localJobInfo;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
      throws RemoteException
    {
      new JobSchedulerShellCommand(JobSchedulerService.this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
    }
    
    public int schedule(JobInfo paramJobInfo)
      throws RemoteException
    {
      int i = Binder.getCallingPid();
      int j = Binder.getCallingUid();
      enforceValidJobRequest(j, paramJobInfo);
      if ((paramJobInfo.isPersisted()) && (!canPersistJobs(i, j))) {
        throw new IllegalArgumentException("Error: requested job be persisted without holding RECEIVE_BOOT_COMPLETED permission.");
      }
      if ((paramJobInfo.getFlags() & 0x1) != 0) {
        JobSchedulerService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "JobSchedulerService");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        i = JobSchedulerService.this.schedule(paramJobInfo, j);
        return i;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int scheduleAsPackage(JobInfo paramJobInfo, String paramString1, int paramInt, String paramString2)
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      if (paramString1 == null) {
        throw new NullPointerException("Must specify a package for scheduleAsPackage()");
      }
      if (JobSchedulerService.this.getContext().checkCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS") != 0) {
        throw new SecurityException("Caller uid " + i + " not permitted to schedule jobs for other apps");
      }
      if ((paramJobInfo.getFlags() & 0x1) != 0) {
        JobSchedulerService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "JobSchedulerService");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramInt = JobSchedulerService.this.scheduleAsPackage(paramJobInfo, i, paramString1, paramInt, paramString2);
        return paramInt;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  final class LocalService
    implements JobSchedulerInternal
  {
    LocalService() {}
    
    public List<JobInfo> getSystemScheduledPendingJobs()
    {
      synchronized (JobSchedulerService.this.mLock)
      {
        final ArrayList localArrayList = new ArrayList();
        JobSchedulerService.this.mJobs.forEachJob(1000, new JobStore.JobStatusFunctor()
        {
          public void process(JobStatus paramAnonymousJobStatus)
          {
            if ((!paramAnonymousJobStatus.getJob().isPeriodic()) && (JobSchedulerService.-wrap0(JobSchedulerService.this, paramAnonymousJobStatus))) {
              return;
            }
            localArrayList.add(paramAnonymousJobStatus.getJob());
          }
        });
        return localArrayList;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobSchedulerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */