package com.android.server.backup;

import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Slog;

public class FullBackupJob
  extends JobService
{
  private static final boolean DEBUG = true;
  private static final int JOB_ID = 20536;
  private static final String TAG = "FullBackupJob";
  private static ComponentName sIdleService = new ComponentName("android", FullBackupJob.class.getName());
  JobParameters mParams;
  
  public static void schedule(Context paramContext, long paramLong)
  {
    paramContext = (JobScheduler)paramContext.getSystemService("jobscheduler");
    JobInfo.Builder localBuilder = new JobInfo.Builder(20536, sIdleService).setRequiresDeviceIdle(true).setRequiredNetworkType(2).setRequiresCharging(true);
    if (paramLong > 0L) {
      localBuilder.setMinimumLatency(paramLong);
    }
    try
    {
      paramContext.schedule(localBuilder.build());
      return;
    }
    catch (IllegalArgumentException paramContext)
    {
      Slog.e("FullBackupJob", "Error while scheduling FullBackupJob:", paramContext.fillInStackTrace());
    }
  }
  
  public void finishBackupPass()
  {
    if (this.mParams != null)
    {
      jobFinished(this.mParams, false);
      this.mParams = null;
    }
  }
  
  public boolean onStartJob(JobParameters paramJobParameters)
  {
    this.mParams = paramJobParameters;
    return BackupManagerService.getInstance().beginFullBackup(this);
  }
  
  public boolean onStopJob(JobParameters paramJobParameters)
  {
    if (this.mParams != null)
    {
      this.mParams = null;
      BackupManagerService.getInstance().endFullBackup();
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/backup/FullBackupJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */