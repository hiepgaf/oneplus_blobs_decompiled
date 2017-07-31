package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
import java.util.Calendar;

public class MountServiceIdler
  extends JobService
{
  private static int MOUNT_JOB_ID = 808;
  private static final String TAG = "MountServiceIdler";
  private static ComponentName sIdleService = new ComponentName("android", MountServiceIdler.class.getName());
  private Runnable mFinishCallback = new Runnable()
  {
    public void run()
    {
      Slog.i("MountServiceIdler", "Got mount service completion callback");
      synchronized (MountServiceIdler.-get0(MountServiceIdler.this))
      {
        if (MountServiceIdler.-get2(MountServiceIdler.this))
        {
          MountServiceIdler.this.jobFinished(MountServiceIdler.-get1(MountServiceIdler.this), false);
          MountServiceIdler.-set0(MountServiceIdler.this, false);
        }
        MountServiceIdler.scheduleIdlePass(MountServiceIdler.this);
        return;
      }
    }
  };
  private JobParameters mJobParams;
  private boolean mStarted;
  
  public static void scheduleIdlePass(Context paramContext)
  {
    paramContext = (JobScheduler)paramContext.getSystemService("jobscheduler");
    long l1 = tomorrowMidnight().getTimeInMillis();
    long l2 = System.currentTimeMillis();
    JobInfo.Builder localBuilder = new JobInfo.Builder(MOUNT_JOB_ID, sIdleService);
    localBuilder.setRequiresDeviceIdle(true);
    localBuilder.setRequiresCharging(true);
    localBuilder.setMinimumLatency(l1 - l2);
    paramContext.schedule(localBuilder.build());
  }
  
  private static Calendar tomorrowMidnight()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(System.currentTimeMillis());
    localCalendar.set(11, 3);
    localCalendar.set(12, 0);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    localCalendar.add(5, 1);
    return localCalendar;
  }
  
  public boolean onStartJob(JobParameters arg1)
  {
    try
    {
      ActivityManagerNative.getDefault().performIdleMaintenance();
      this.mJobParams = ???;
      MountService localMountService = MountService.sSelf;
      if (localMountService != null) {}
      synchronized (this.mFinishCallback)
      {
        this.mStarted = true;
        localMountService.runIdleMaintenance(this.mFinishCallback);
        if (localMountService != null) {
          return true;
        }
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public boolean onStopJob(JobParameters arg1)
  {
    synchronized (this.mFinishCallback)
    {
      this.mStarted = false;
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/MountServiceIdler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */