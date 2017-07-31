package com.android.server.job;

import android.app.ActivityManager;
import android.app.job.IJobCallback.Stub;
import android.app.job.IJobService;
import android.app.job.IJobService.Stub;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.server.job.controllers.JobStatus;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobServiceContext
  extends IJobCallback.Stub
  implements ServiceConnection
{
  private static final boolean DEBUG = false;
  private static final long EXECUTING_TIMESLICE_MILLIS = 600000L;
  private static final int MSG_CALLBACK = 1;
  private static final int MSG_CANCEL = 3;
  private static final int MSG_SERVICE_BOUND = 2;
  private static final int MSG_SHUTDOWN_EXECUTION = 4;
  private static final int MSG_TIMEOUT = 0;
  public static final int NO_PREFERRED_UID = -1;
  private static final long OP_TIMEOUT_MILLIS = 8000L;
  private static final String TAG = "JobServiceContext";
  static final int VERB_BINDING = 0;
  static final int VERB_EXECUTING = 2;
  static final int VERB_FINISHED = 4;
  static final int VERB_STARTING = 1;
  static final int VERB_STOPPING = 3;
  private static final String[] VERB_STRINGS;
  private static final int defaultMaxActiveJobsPerService;
  @GuardedBy("mLock")
  private boolean mAvailable;
  private final IBatteryStats mBatteryStats;
  private final Handler mCallbackHandler;
  private AtomicBoolean mCancelled = new AtomicBoolean();
  private final JobCompletedListener mCompletedListener;
  private final Context mContext;
  private long mExecutionStartTimeElapsed;
  private final JobPackageTracker mJobPackageTracker;
  private final Object mLock;
  private JobParameters mParams;
  private int mPreferredUid;
  private JobStatus mRunningJob;
  private long mTimeoutElapsed;
  int mVerb;
  private PowerManager.WakeLock mWakeLock;
  IJobService service;
  
  static
  {
    if (ActivityManager.isLowRamDeviceStatic()) {}
    for (int i = 1;; i = 3)
    {
      defaultMaxActiveJobsPerService = i;
      VERB_STRINGS = new String[] { "VERB_BINDING", "VERB_STARTING", "VERB_EXECUTING", "VERB_STOPPING", "VERB_FINISHED" };
      return;
    }
  }
  
  JobServiceContext(Context paramContext, Object paramObject, IBatteryStats paramIBatteryStats, JobPackageTracker paramJobPackageTracker, JobCompletedListener paramJobCompletedListener, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mLock = paramObject;
    this.mBatteryStats = paramIBatteryStats;
    this.mJobPackageTracker = paramJobPackageTracker;
    this.mCallbackHandler = new JobServiceHandler(paramLooper);
    this.mCompletedListener = paramJobCompletedListener;
    this.mAvailable = true;
    this.mVerb = 4;
    this.mPreferredUid = -1;
  }
  
  JobServiceContext(JobSchedulerService paramJobSchedulerService, IBatteryStats paramIBatteryStats, JobPackageTracker paramJobPackageTracker, Looper paramLooper)
  {
    this(paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock(), paramIBatteryStats, paramJobPackageTracker, paramJobSchedulerService, paramLooper);
  }
  
  private void removeOpTimeOut()
  {
    this.mCallbackHandler.removeMessages(0);
  }
  
  private void scheduleOpTimeOut()
  {
    removeOpTimeOut();
    if (this.mVerb == 2) {}
    for (long l = 600000L;; l = 8000L)
    {
      Message localMessage = this.mCallbackHandler.obtainMessage(0);
      this.mCallbackHandler.sendMessageDelayed(localMessage, l);
      this.mTimeoutElapsed = (SystemClock.elapsedRealtime() + l);
      return;
    }
  }
  
  private boolean verifyCallingUid()
  {
    synchronized (this.mLock)
    {
      int i;
      int j;
      if (this.mRunningJob != null)
      {
        i = Binder.getCallingUid();
        j = this.mRunningJob.getUid();
      }
      return i == j;
    }
  }
  
  public void acknowledgeStartMessage(int paramInt, boolean paramBoolean)
  {
    if (!verifyCallingUid()) {
      return;
    }
    Handler localHandler = this.mCallbackHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(1, paramInt, i).sendToTarget();
      return;
    }
  }
  
  public void acknowledgeStopMessage(int paramInt, boolean paramBoolean)
  {
    if (!verifyCallingUid()) {
      return;
    }
    Handler localHandler = this.mCallbackHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(1, paramInt, i).sendToTarget();
      return;
    }
  }
  
  void cancelExecutingJob(int paramInt)
  {
    this.mCallbackHandler.obtainMessage(3, paramInt, 0).sendToTarget();
  }
  
  void clearPreferredUid()
  {
    this.mPreferredUid = -1;
  }
  
  boolean executeRunnableJob(JobStatus paramJobStatus)
  {
    synchronized (this.mLock)
    {
      if (!this.mAvailable)
      {
        Slog.e("JobServiceContext", "Starting new runnable but context is unavailable > Error.");
        return false;
      }
      this.mPreferredUid = -1;
      this.mRunningJob = paramJobStatus;
      if (paramJobStatus.hasDeadlineConstraint())
      {
        if (paramJobStatus.getLatestRunTimeElapsed() < SystemClock.elapsedRealtime()) {}
        for (bool = true;; bool = false)
        {
          Object localObject1 = null;
          if (paramJobStatus.changedUris != null)
          {
            localObject1 = new Uri[paramJobStatus.changedUris.size()];
            paramJobStatus.changedUris.toArray((Object[])localObject1);
          }
          String[] arrayOfString = null;
          if (paramJobStatus.changedAuthorities != null)
          {
            arrayOfString = new String[paramJobStatus.changedAuthorities.size()];
            paramJobStatus.changedAuthorities.toArray(arrayOfString);
          }
          this.mParams = new JobParameters(this, paramJobStatus.getJobId(), paramJobStatus.getExtras(), bool, (Uri[])localObject1, arrayOfString);
          this.mExecutionStartTimeElapsed = SystemClock.elapsedRealtime();
          this.mVerb = 0;
          scheduleOpTimeOut();
          localObject1 = new Intent().setComponent(paramJobStatus.getServiceComponent());
          if (this.mContext.bindServiceAsUser((Intent)localObject1, this, 5, new UserHandle(paramJobStatus.getUserId()))) {
            break;
          }
          this.mRunningJob = null;
          this.mParams = null;
          this.mExecutionStartTimeElapsed = 0L;
          this.mVerb = 4;
          removeOpTimeOut();
          return false;
        }
      }
      boolean bool = false;
    }
    try
    {
      this.mBatteryStats.noteJobStart(paramJobStatus.getBatteryName(), paramJobStatus.getSourceUid());
      this.mJobPackageTracker.noteActive(paramJobStatus);
      this.mAvailable = false;
      return true;
      paramJobStatus = finally;
      throw paramJobStatus;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  long getExecutionStartTimeElapsed()
  {
    return this.mExecutionStartTimeElapsed;
  }
  
  int getPreferredUid()
  {
    return this.mPreferredUid;
  }
  
  JobStatus getRunningJob()
  {
    synchronized (this.mLock)
    {
      JobStatus localJobStatus1 = this.mRunningJob;
      if (localJobStatus1 == null) {
        return null;
      }
    }
    return new JobStatus(localJobStatus2);
  }
  
  JobStatus getRunningJobUnsafeLocked()
  {
    return this.mRunningJob;
  }
  
  long getTimeoutElapsed()
  {
    return this.mTimeoutElapsed;
  }
  
  public void jobFinished(int paramInt, boolean paramBoolean)
  {
    if (!verifyCallingUid()) {
      return;
    }
    Handler localHandler = this.mCallbackHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(1, paramInt, i).sendToTarget();
      return;
    }
  }
  
  public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
  {
    JobStatus localJobStatus;
    synchronized (this.mLock)
    {
      localJobStatus = this.mRunningJob;
      if ((localJobStatus != null) && (???.equals(localJobStatus.getServiceComponent())))
      {
        this.service = IJobService.Stub.asInterface(paramIBinder);
        paramIBinder = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, localJobStatus.getTag());
        paramIBinder.setWorkSource(new WorkSource(localJobStatus.getSourceUid()));
        paramIBinder.setReferenceCounted(false);
        paramIBinder.acquire();
      }
    }
    synchronized (this.mLock)
    {
      if (this.mWakeLock != null)
      {
        Slog.w("JobServiceContext", "Bound new job " + localJobStatus + " but live wakelock " + this.mWakeLock + " tag=" + this.mWakeLock.getTag());
        this.mWakeLock.release();
      }
      this.mWakeLock = paramIBinder;
      this.mCallbackHandler.obtainMessage(2).sendToTarget();
      return;
      ??? = finally;
      throw ???;
      this.mCallbackHandler.obtainMessage(4).sendToTarget();
      return;
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    this.mCallbackHandler.obtainMessage(4).sendToTarget();
  }
  
  void preemptExecutingJob()
  {
    Message localMessage = this.mCallbackHandler.obtainMessage(3);
    localMessage.arg1 = 2;
    localMessage.sendToTarget();
  }
  
  private class JobServiceHandler
    extends Handler
  {
    JobServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    private void closeAndCleanupJobH(boolean paramBoolean)
    {
      JobStatus localJobStatus;
      synchronized (JobServiceContext.-get6(JobServiceContext.this))
      {
        int i = JobServiceContext.this.mVerb;
        if (i == 4) {
          return;
        }
        localJobStatus = JobServiceContext.-get8(JobServiceContext.this);
        JobServiceContext.-get5(JobServiceContext.this).noteInactive(localJobStatus);
      }
      try
      {
        JobServiceContext.-get1(JobServiceContext.this).noteJobFinish(JobServiceContext.-get8(JobServiceContext.this).getBatteryName(), JobServiceContext.-get8(JobServiceContext.this).getSourceUid());
        if (JobServiceContext.-get9(JobServiceContext.this) != null) {
          JobServiceContext.-get9(JobServiceContext.this).release();
        }
        try
        {
          JobServiceContext.-get4(JobServiceContext.this).unbindService(JobServiceContext.this);
          JobServiceContext.-set4(JobServiceContext.this, null);
          JobServiceContext.-set3(JobServiceContext.this, null);
          JobServiceContext.-set1(JobServiceContext.this, null);
          JobServiceContext.this.mVerb = 4;
          JobServiceContext.-get2(JobServiceContext.this).set(false);
          JobServiceContext.this.service = null;
          JobServiceContext.-set0(JobServiceContext.this, true);
          JobServiceContext.-wrap0(JobServiceContext.this);
          removeMessages(1);
          removeMessages(2);
          removeMessages(3);
          removeMessages(4);
          JobServiceContext.-get3(JobServiceContext.this).onJobCompleted(localJobStatus, paramBoolean);
          return;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            localIllegalArgumentException.printStackTrace();
          }
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
    
    private void handleCancelH()
    {
      switch (JobServiceContext.this.mVerb)
      {
      default: 
        Slog.e("JobServiceContext", "Cancelling a job without a valid verb: " + JobServiceContext.this.mVerb);
      case 3: 
        return;
      case 0: 
      case 1: 
        JobServiceContext.-get2(JobServiceContext.this).set(true);
        return;
      }
      if (hasMessages(1)) {
        return;
      }
      sendStopMessageH();
    }
    
    private void handleFinishedH(boolean paramBoolean)
    {
      switch (JobServiceContext.this.mVerb)
      {
      default: 
        Slog.e("JobServiceContext", "Got an execution complete message for a job that wasn't beingexecuted. Was " + JobServiceContext.-get0()[JobServiceContext.this.mVerb] + ".");
        return;
      }
      closeAndCleanupJobH(paramBoolean);
    }
    
    private void handleOpTimeoutH()
    {
      switch (JobServiceContext.this.mVerb)
      {
      default: 
        Slog.e("JobServiceContext", "Handling timeout for an invalid job state: " + JobServiceContext.-get8(JobServiceContext.this).toShortString() + ", dropping.");
        closeAndCleanupJobH(false);
        return;
      case 0: 
        Slog.e("JobServiceContext", "Time-out while trying to bind " + JobServiceContext.-get8(JobServiceContext.this).toShortString() + ", dropping.");
        closeAndCleanupJobH(false);
        return;
      case 1: 
        Slog.e("JobServiceContext", "No response from client for onStartJob '" + JobServiceContext.-get8(JobServiceContext.this).toShortString());
        closeAndCleanupJobH(false);
        return;
      case 3: 
        Slog.e("JobServiceContext", "No response from client for onStopJob, '" + JobServiceContext.-get8(JobServiceContext.this).toShortString());
        closeAndCleanupJobH(true);
        return;
      }
      Slog.i("JobServiceContext", "Client timed out while executing (no jobFinished received). sending onStop. " + JobServiceContext.-get8(JobServiceContext.this).toShortString());
      JobServiceContext.-get7(JobServiceContext.this).setStopReason(3);
      sendStopMessageH();
    }
    
    private void handleServiceBoundH()
    {
      if (JobServiceContext.this.mVerb != 0)
      {
        Slog.e("JobServiceContext", "Sending onStartJob for a job that isn't pending. " + JobServiceContext.-get0()[JobServiceContext.this.mVerb]);
        closeAndCleanupJobH(false);
        return;
      }
      if (JobServiceContext.-get2(JobServiceContext.this).get())
      {
        closeAndCleanupJobH(true);
        return;
      }
      try
      {
        JobServiceContext.this.mVerb = 1;
        JobServiceContext.-wrap1(JobServiceContext.this);
        JobServiceContext.this.service.startJob(JobServiceContext.-get7(JobServiceContext.this));
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("JobServiceContext", "Error sending onStart message to '" + JobServiceContext.-get8(JobServiceContext.this).getServiceComponent().getShortClassName() + "' ", localRemoteException);
      }
    }
    
    private void handleStartedH(boolean paramBoolean)
    {
      switch (JobServiceContext.this.mVerb)
      {
      default: 
        Slog.e("JobServiceContext", "Handling started job but job wasn't starting! Was " + JobServiceContext.-get0()[JobServiceContext.this.mVerb] + ".");
        return;
      }
      JobServiceContext.this.mVerb = 2;
      if (!paramBoolean)
      {
        handleFinishedH(false);
        return;
      }
      if (JobServiceContext.-get2(JobServiceContext.this).get())
      {
        handleCancelH();
        return;
      }
      JobServiceContext.-wrap1(JobServiceContext.this);
    }
    
    private void sendStopMessageH()
    {
      JobServiceContext.-wrap0(JobServiceContext.this);
      if (JobServiceContext.this.mVerb != 2)
      {
        Slog.e("JobServiceContext", "Sending onStopJob for a job that isn't started. " + JobServiceContext.-get8(JobServiceContext.this));
        closeAndCleanupJobH(false);
        return;
      }
      try
      {
        JobServiceContext.this.mVerb = 3;
        JobServiceContext.-wrap1(JobServiceContext.this);
        JobServiceContext.this.service.stopJob(JobServiceContext.-get7(JobServiceContext.this));
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("JobServiceContext", "Error sending onStopJob to client.", localRemoteException);
        closeAndCleanupJobH(false);
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      switch (paramMessage.what)
      {
      default: 
        Slog.e("JobServiceContext", "Unrecognised message: " + paramMessage);
      case 2: 
      case 1: 
        do
        {
          return;
          JobServiceContext.-wrap0(JobServiceContext.this);
          handleServiceBoundH();
          return;
          JobServiceContext.-wrap0(JobServiceContext.this);
          if (JobServiceContext.this.mVerb == 1)
          {
            if (paramMessage.arg2 == 1) {}
            for (;;)
            {
              handleStartedH(bool);
              return;
              bool = false;
            }
          }
        } while ((JobServiceContext.this.mVerb != 2) && (JobServiceContext.this.mVerb != 3));
        if (paramMessage.arg2 == 1) {}
        for (bool = true;; bool = false)
        {
          handleFinishedH(bool);
          return;
        }
      case 3: 
        if (JobServiceContext.this.mVerb == 4) {
          return;
        }
        JobServiceContext.-get7(JobServiceContext.this).setStopReason(paramMessage.arg1);
        if (paramMessage.arg1 == 2)
        {
          paramMessage = JobServiceContext.this;
          if (JobServiceContext.-get8(JobServiceContext.this) == null) {
            break label230;
          }
        }
        for (int i = JobServiceContext.-get8(JobServiceContext.this).getUid();; i = -1)
        {
          JobServiceContext.-set2(paramMessage, i);
          handleCancelH();
          return;
        }
      case 0: 
        label230:
        handleOpTimeoutH();
        return;
      }
      closeAndCleanupJobH(true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobServiceContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */