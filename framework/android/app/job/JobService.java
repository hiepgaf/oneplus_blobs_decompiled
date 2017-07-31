package android.app.job;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.lang.ref.WeakReference;

public abstract class JobService
  extends Service
{
  private static final int MSG_EXECUTE_JOB = 0;
  private static final int MSG_JOB_FINISHED = 2;
  private static final int MSG_STOP_JOB = 1;
  public static final String PERMISSION_BIND = "android.permission.BIND_JOB_SERVICE";
  private static final String TAG = "JobService";
  IJobService mBinder;
  @GuardedBy("mHandlerLock")
  JobHandler mHandler;
  private final Object mHandlerLock = new Object();
  
  void ensureHandler()
  {
    synchronized (this.mHandlerLock)
    {
      if (this.mHandler == null) {
        this.mHandler = new JobHandler(getMainLooper());
      }
      return;
    }
  }
  
  public final void jobFinished(JobParameters paramJobParameters, boolean paramBoolean)
  {
    ensureHandler();
    paramJobParameters = Message.obtain(this.mHandler, 2, paramJobParameters);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      paramJobParameters.arg2 = i;
      paramJobParameters.sendToTarget();
      return;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if (this.mBinder == null) {
      this.mBinder = new JobInterface(this);
    }
    return this.mBinder.asBinder();
  }
  
  public abstract boolean onStartJob(JobParameters paramJobParameters);
  
  public abstract boolean onStopJob(JobParameters paramJobParameters);
  
  class JobHandler
    extends Handler
  {
    JobHandler(Looper paramLooper)
    {
      super();
    }
    
    private void ackStartMessage(JobParameters paramJobParameters, boolean paramBoolean)
    {
      IJobCallback localIJobCallback = paramJobParameters.getCallback();
      int i = paramJobParameters.getJobId();
      if (localIJobCallback != null) {}
      while (!Log.isLoggable("JobService", 3)) {
        try
        {
          localIJobCallback.acknowledgeStartMessage(i, paramBoolean);
          return;
        }
        catch (RemoteException paramJobParameters)
        {
          Log.e("JobService", "System unreachable for starting job.");
          return;
        }
      }
      Log.d("JobService", "Attempting to ack a job that has already been processed.");
    }
    
    private void ackStopMessage(JobParameters paramJobParameters, boolean paramBoolean)
    {
      IJobCallback localIJobCallback = paramJobParameters.getCallback();
      int i = paramJobParameters.getJobId();
      if (localIJobCallback != null) {}
      while (!Log.isLoggable("JobService", 3)) {
        try
        {
          localIJobCallback.acknowledgeStopMessage(i, paramBoolean);
          return;
        }
        catch (RemoteException paramJobParameters)
        {
          Log.e("JobService", "System unreachable for stopping job.");
          return;
        }
      }
      Log.d("JobService", "Attempting to ack a job that has already been processed.");
    }
    
    public void handleMessage(Message paramMessage)
    {
      JobParameters localJobParameters = (JobParameters)paramMessage.obj;
      switch (paramMessage.what)
      {
      default: 
        Log.e("JobService", "Unrecognised message received.");
        return;
      case 0: 
        try
        {
          ackStartMessage(localJobParameters, JobService.this.onStartJob(localJobParameters));
          return;
        }
        catch (Exception paramMessage)
        {
          Log.e("JobService", "Error while executing job: " + localJobParameters.getJobId());
          throw new RuntimeException(paramMessage);
        }
      case 1: 
        try
        {
          ackStopMessage(localJobParameters, JobService.this.onStopJob(localJobParameters));
          return;
        }
        catch (Exception paramMessage)
        {
          Log.e("JobService", "Application unable to handle onStopJob.", paramMessage);
          throw new RuntimeException(paramMessage);
        }
      }
      if (paramMessage.arg2 == 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramMessage = localJobParameters.getCallback();
        if (paramMessage == null) {
          break;
        }
        try
        {
          paramMessage.jobFinished(localJobParameters.getJobId(), bool);
          return;
        }
        catch (RemoteException paramMessage)
        {
          Log.e("JobService", "Error reporting job finish to system: binder has goneaway.");
          return;
        }
      }
      Log.e("JobService", "finishJob() called for a nonexistent job id.");
    }
  }
  
  static final class JobInterface
    extends IJobService.Stub
  {
    final WeakReference<JobService> mService;
    
    JobInterface(JobService paramJobService)
    {
      this.mService = new WeakReference(paramJobService);
    }
    
    public void startJob(JobParameters paramJobParameters)
      throws RemoteException
    {
      JobService localJobService = (JobService)this.mService.get();
      if (localJobService != null)
      {
        localJobService.ensureHandler();
        Message.obtain(localJobService.mHandler, 0, paramJobParameters).sendToTarget();
      }
    }
    
    public void stopJob(JobParameters paramJobParameters)
      throws RemoteException
    {
      JobService localJobService = (JobService)this.mService.get();
      if (localJobService != null)
      {
        localJobService.ensureHandler();
        Message.obtain(localJobService.mHandler, 1, paramJobParameters).sendToTarget();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/JobService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */