package com.android.server.pm;

import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Environment;
import android.os.ServiceManager;
import android.os.storage.StorageManager;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackgroundDexOptService
  extends JobService
{
  static final long DAYS_IN_MILLISECONDS = 86400000L;
  static final int JOB_IDLE_OPTIMIZE = 800;
  static final int JOB_POST_BOOT_UPDATE = 801;
  static final long MONTH_IN_MILLISECONDS = 2592000000L;
  static final long RETRY_LATENCY = 14400000L;
  static final String TAG = "BackgroundDexOptService";
  private static ComponentName sDexoptServiceName = new ComponentName("android", BackgroundDexOptService.class.getName());
  static final ArraySet<String> sFailedPackageNames = new ArraySet();
  private final File dataDir = Environment.getDataDirectory();
  final AtomicBoolean mAbortIdleOptimization = new AtomicBoolean(false);
  final AtomicBoolean mAbortPostBootUpdate = new AtomicBoolean(false);
  final AtomicBoolean mExitPostBootUpdate = new AtomicBoolean(false);
  
  private int getBatteryLevel()
  {
    Intent localIntent = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    int i = localIntent.getIntExtra("level", -1);
    int j = localIntent.getIntExtra("scale", -1);
    if ((i < 0) || (j <= 0)) {
      return 0;
    }
    return i * 100 / j;
  }
  
  private long getLowStorageThreshold()
  {
    long l = StorageManager.from(this).getStorageLowBytes(this.dataDir);
    if (l == 0L) {
      Log.e("BackgroundDexOptService", "Invalid low storage threshold");
    }
    return l;
  }
  
  public static void notifyPackageChanged(String paramString)
  {
    synchronized (sFailedPackageNames)
    {
      sFailedPackageNames.remove(paramString);
      return;
    }
  }
  
  private boolean runIdleOptimization(final JobParameters paramJobParameters, PackageManagerService paramPackageManagerService, final ArraySet<String> paramArraySet)
  {
    this.mExitPostBootUpdate.set(true);
    this.mAbortIdleOptimization.set(false);
    final long l = getLowStorageThreshold();
    new Thread("BackgroundDexOptService_IdleOptimization")
    {
      public void run()
      {
        Iterator localIterator = paramArraySet.iterator();
        String str;
        int i;
        int j;
        long l1;
        for (;;)
        {
          int k;
          if (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            if (BackgroundDexOptService.this.mAbortIdleOptimization.get()) {
              return;
            }
            k = 0;
            i = 0;
          }
          for (;;)
          {
            j = k;
            if (i < this.val$IgnoreList.length)
            {
              if (str.equals(this.val$IgnoreList[i]))
              {
                Log.d("BackgroundDexOptService", "ignore: " + str);
                j = 1;
              }
            }
            else
            {
              if ((BackgroundDexOptService.sFailedPackageNames.contains(str)) || (j != 0)) {
                break;
              }
              l1 = BackgroundDexOptService.-get0(BackgroundDexOptService.this).getUsableSpace();
              if (l1 >= l) {
                break label187;
              }
              Log.w("BackgroundDexOptService", "Aborting background dex opt job due to low storage: " + l1);
              BackgroundDexOptService.this.jobFinished(this.val$jobParams, false);
              return;
            }
            i += 1;
          }
          synchronized (BackgroundDexOptService.sFailedPackageNames)
          {
            label187:
            BackgroundDexOptService.sFailedPackageNames.add(str);
            ??? = paramJobParameters.getOatFileCompilerFilter(str);
            Log.d("BackgroundDexOptService", "IdleOptimization run: pkg = " + str + ", compilerFilter = " + (String)???);
            if ((??? == null) || (((String)???).equals("interpret-only")) || (((String)???).equals("verify-profile")))
            {
              Log.d("BackgroundDexOptService", "compilerFilter is " + (String)??? + ", do full oat");
              if (!paramJobParameters.performDexOpt(str, false, 7, false)) {
                continue;
              }
            }
          }
        }
        for (;;)
        {
          synchronized (BackgroundDexOptService.sFailedPackageNames)
          {
            BackgroundDexOptService.sFailedPackageNames.remove(str);
            break;
            localObject2 = finally;
            throw ((Throwable)localObject2);
          }
          Log.d("BackgroundDexOptService", "compilerFilter is profiled or full");
          j = 0;
          l1 = System.currentTimeMillis();
          long l2 = paramJobParameters.getLastUsedTime(str);
          Log.e("BackgroundDexOptService", "now = " + l1 + ", lastUsed = " + l2 + ", DAYS = " + 86400000L);
          i = j;
          if (l2 > 0L)
          {
            i = j;
            if (l2 < l1 - 2592000000L)
            {
              i = 1;
              Log.d("BackgroundDexOptService", "rarely used: " + str + ", lastUsed = " + l2);
            }
          }
          if (((String)???).equals("speed")) {
            if (i != 0)
            {
              Log.d("BackgroundDexOptService", "speed but rarelyUsed, back to use profile-based");
              if (!paramJobParameters.performDexOpt(str, true, 3, true)) {
                break;
              }
            }
          }
          synchronized (BackgroundDexOptService.sFailedPackageNames)
          {
            BackgroundDexOptService.sFailedPackageNames.remove(str);
          }
        }
      }
    }.start();
    return true;
  }
  
  private boolean runIdleStorageLowOptimization(final JobParameters paramJobParameters, final PackageManagerService paramPackageManagerService, final ArraySet<String> paramArraySet)
  {
    this.mExitPostBootUpdate.set(true);
    this.mAbortIdleOptimization.set(false);
    new Thread("BackgroundDexOptService_IdleOptimization")
    {
      public void run()
      {
        Iterator localIterator = paramArraySet.iterator();
        String str;
        for (;;)
        {
          if (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            if (BackgroundDexOptService.this.mAbortIdleOptimization.get()) {
              return;
            }
            if (BackgroundDexOptService.sFailedPackageNames.contains(str)) {
              continue;
            }
            synchronized (BackgroundDexOptService.sFailedPackageNames)
            {
              BackgroundDexOptService.sFailedPackageNames.add(str);
              ??? = paramPackageManagerService.getOatFileCompilerFilter(str);
              if (??? != null)
              {
                long l1 = System.currentTimeMillis();
                long l2 = paramPackageManagerService.getLastUsedTime(str);
                int i = 0;
                if (l2 < l1 - 2592000000L)
                {
                  i = 1;
                  Log.d("BackgroundDexOptService", "rarely used: " + str + "lastUsed = " + l2);
                }
                if (((i != 0) && (((String)???).equals("speed"))) || (((String)???).equals("speed-profile"))) {
                  if (!paramPackageManagerService.performDexOpt(str, false, 2, true)) {
                    continue;
                  }
                }
              }
            }
          }
        }
        for (;;)
        {
          synchronized (BackgroundDexOptService.sFailedPackageNames)
          {
            BackgroundDexOptService.sFailedPackageNames.remove(str);
            break;
            localObject2 = finally;
            throw ((Throwable)localObject2);
          }
          if ((!((String)???).equals("speed")) || (!paramPackageManagerService.performDexOpt(str, true, 3, true))) {
            break;
          }
          synchronized (BackgroundDexOptService.sFailedPackageNames)
          {
            BackgroundDexOptService.sFailedPackageNames.remove(str);
          }
        }
      }
    }.start();
    return true;
  }
  
  private boolean runPostBootUpdate(final JobParameters paramJobParameters, PackageManagerService paramPackageManagerService, final ArraySet<String> paramArraySet)
  {
    if (this.mExitPostBootUpdate.get()) {
      return false;
    }
    final int i = getResources().getInteger(17694808);
    final long l = getLowStorageThreshold();
    this.mAbortPostBootUpdate.set(false);
    new Thread("BackgroundDexOptService_PostBootUpdate")
    {
      public void run()
      {
        Iterator localIterator = paramArraySet.iterator();
        for (;;)
        {
          String str;
          if (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            if (BackgroundDexOptService.this.mAbortPostBootUpdate.get()) {
              return;
            }
            if (!BackgroundDexOptService.this.mExitPostBootUpdate.get()) {
              break label70;
            }
          }
          for (;;)
          {
            BackgroundDexOptService.this.jobFinished(this.val$jobParams, false);
            return;
            label70:
            if (BackgroundDexOptService.-wrap0(BackgroundDexOptService.this) >= i)
            {
              long l = BackgroundDexOptService.-get0(BackgroundDexOptService.this).getUsableSpace();
              if (l >= l) {
                break;
              }
              Log.w("BackgroundDexOptService", "Aborting background dex opt job due to low storage: " + l);
            }
          }
          if (PackageManagerService.DEBUG_DEXOPT) {
            Log.i("BackgroundDexOptService", "Updating package " + str);
          }
          paramJobParameters.performDexOpt(str, false, 1, false);
        }
      }
    }.start();
    return true;
  }
  
  public static void schedule(Context paramContext)
  {
    paramContext = (JobScheduler)paramContext.getSystemService("jobscheduler");
    paramContext.schedule(new JobInfo.Builder(801, sDexoptServiceName).setMinimumLatency(TimeUnit.MINUTES.toMillis(1L)).setOverrideDeadline(TimeUnit.MINUTES.toMillis(1L)).build());
    paramContext.schedule(new JobInfo.Builder(800, sDexoptServiceName).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(TimeUnit.DAYS.toMillis(1L)).build());
    if (PackageManagerService.DEBUG_DEXOPT) {
      Log.i("BackgroundDexOptService", "Jobs scheduled");
    }
  }
  
  public boolean onStartJob(JobParameters paramJobParameters)
  {
    if (PackageManagerService.DEBUG_DEXOPT) {
      Log.i("BackgroundDexOptService", "onStartJob");
    }
    PackageManagerService localPackageManagerService = (PackageManagerService)ServiceManager.getService("package");
    if (localPackageManagerService.isStorageLow())
    {
      if (PackageManagerService.DEBUG_DEXOPT) {
        Log.i("BackgroundDexOptService", "Low storage, skipping this run");
      }
      return false;
    }
    ArraySet localArraySet = localPackageManagerService.getOptimizablePackages();
    if ((localArraySet == null) || (localArraySet.isEmpty()))
    {
      if (PackageManagerService.DEBUG_DEXOPT) {
        Log.i("BackgroundDexOptService", "No packages to optimize");
      }
      return false;
    }
    long l = getLowStorageThreshold();
    if (this.dataDir.getUsableSpace() < l)
    {
      if (PackageManagerService.DEBUG_DEXOPT) {
        Log.i("BackgroundDexOptService", "Low storage, runIdleStorageLowOptimization");
      }
      return runIdleStorageLowOptimization(paramJobParameters, localPackageManagerService, localArraySet);
    }
    if (paramJobParameters.getJobId() == 801) {
      return runPostBootUpdate(paramJobParameters, localPackageManagerService, localArraySet);
    }
    return runIdleOptimization(paramJobParameters, localPackageManagerService, localArraySet);
  }
  
  public boolean onStopJob(JobParameters paramJobParameters)
  {
    if (PackageManagerService.DEBUG_DEXOPT) {
      Log.i("BackgroundDexOptService", "onStopJob");
    }
    if (paramJobParameters.getJobId() == 801) {
      this.mAbortPostBootUpdate.set(true);
    }
    for (;;)
    {
      return false;
      this.mAbortIdleOptimization.set(true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/BackgroundDexOptService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */