package com.android.server.backup;

import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
import java.util.Random;

public class KeyValueBackupJob
  extends JobService
{
  static final long BATCH_INTERVAL = 14400000L;
  private static final int FUZZ_MILLIS = 600000;
  private static final int JOB_ID = 20537;
  private static final long MAX_DEFERRAL = 86400000L;
  private static final String TAG = "KeyValueBackupJob";
  private static ComponentName sKeyValueJobService = new ComponentName("android", KeyValueBackupJob.class.getName());
  private static long sNextScheduled = 0L;
  private static boolean sScheduled = false;
  
  public static void cancel(Context paramContext)
  {
    try
    {
      ((JobScheduler)paramContext.getSystemService("jobscheduler")).cancel(20537);
      sNextScheduled = 0L;
      sScheduled = false;
      return;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  public static long nextScheduled()
  {
    try
    {
      long l = sNextScheduled;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static void schedule(Context paramContext)
  {
    schedule(paramContext, 0L);
  }
  
  public static void schedule(Context paramContext, long paramLong)
  {
    try
    {
      if (!sScheduled)
      {
        long l = paramLong;
        if (paramLong <= 0L) {
          l = 14400000L + new Random().nextInt(600000);
        }
        Slog.v("KeyValueBackupJob", "Scheduling k/v pass in " + l / 1000L / 60L + " minutes");
        ((JobScheduler)paramContext.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(20537, sKeyValueJobService).setMinimumLatency(l).setRequiredNetworkType(1).setRequiresCharging(true).setOverrideDeadline(86400000L).build());
        sNextScheduled = System.currentTimeMillis() + l;
        sScheduled = true;
      }
      return;
    }
    finally {}
  }
  
  public boolean onStartJob(JobParameters paramJobParameters)
  {
    try
    {
      sNextScheduled = 0L;
      sScheduled = false;
      paramJobParameters = BackupManagerService.getInstance();
      return false;
    }
    finally
    {
      try
      {
        paramJobParameters.backupNow();
        return false;
      }
      catch (RemoteException paramJobParameters) {}
      paramJobParameters = finally;
    }
  }
  
  public boolean onStopJob(JobParameters paramJobParameters)
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/backup/KeyValueBackupJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */