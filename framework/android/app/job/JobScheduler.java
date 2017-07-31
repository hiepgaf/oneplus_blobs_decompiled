package android.app.job;

import java.util.List;

public abstract class JobScheduler
{
  public static final int RESULT_FAILURE = 0;
  public static final int RESULT_SUCCESS = 1;
  
  public abstract void cancel(int paramInt);
  
  public abstract void cancelAll();
  
  public abstract List<JobInfo> getAllPendingJobs();
  
  public abstract JobInfo getPendingJob(int paramInt);
  
  public abstract int schedule(JobInfo paramJobInfo);
  
  public abstract int scheduleAsPackage(JobInfo paramJobInfo, String paramString1, int paramInt, String paramString2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/JobScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */