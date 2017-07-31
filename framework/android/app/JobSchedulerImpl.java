package android.app;

import android.app.job.IJobScheduler;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.os.RemoteException;
import java.util.List;

public class JobSchedulerImpl
  extends JobScheduler
{
  IJobScheduler mBinder;
  
  JobSchedulerImpl(IJobScheduler paramIJobScheduler)
  {
    this.mBinder = paramIJobScheduler;
  }
  
  public void cancel(int paramInt)
  {
    try
    {
      this.mBinder.cancel(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void cancelAll()
  {
    try
    {
      this.mBinder.cancelAll();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public List<JobInfo> getAllPendingJobs()
  {
    try
    {
      List localList = this.mBinder.getAllPendingJobs();
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public JobInfo getPendingJob(int paramInt)
  {
    try
    {
      JobInfo localJobInfo = this.mBinder.getPendingJob(paramInt);
      return localJobInfo;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public int schedule(JobInfo paramJobInfo)
  {
    try
    {
      int i = this.mBinder.schedule(paramJobInfo);
      return i;
    }
    catch (RemoteException paramJobInfo) {}
    return 0;
  }
  
  public int scheduleAsPackage(JobInfo paramJobInfo, String paramString1, int paramInt, String paramString2)
  {
    try
    {
      paramInt = this.mBinder.scheduleAsPackage(paramJobInfo, paramString1, paramInt, paramString2);
      return paramInt;
    }
    catch (RemoteException paramJobInfo) {}
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/JobSchedulerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */