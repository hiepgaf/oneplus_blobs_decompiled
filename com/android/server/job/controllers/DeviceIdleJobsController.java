package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.UserHandle;
import com.android.internal.util.ArrayUtils;
import com.android.server.DeviceIdleController.LocalService;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.JobStore;
import com.android.server.job.JobStore.JobStatusFunctor;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;

public class DeviceIdleJobsController
  extends StateController
{
  private static final boolean LOG_DEBUG = false;
  private static final String LOG_TAG = "DeviceIdleJobsController";
  private static DeviceIdleJobsController sController;
  private static Object sCreationLock = new Object();
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED".equals(paramAnonymousContext)) || ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(paramAnonymousContext)))
      {
        paramAnonymousContext = DeviceIdleJobsController.this;
        if (DeviceIdleJobsController.-get0(DeviceIdleJobsController.this) != null) {
          if (!DeviceIdleJobsController.-get0(DeviceIdleJobsController.this).isDeviceIdleMode())
          {
            bool = DeviceIdleJobsController.-get0(DeviceIdleJobsController.this).isLightDeviceIdleMode();
            paramAnonymousContext.updateIdleMode(bool);
          }
        }
      }
      while (!"android.os.action.POWER_SAVE_WHITELIST_CHANGED".equals(paramAnonymousContext)) {
        for (;;)
        {
          return;
          boolean bool = true;
          continue;
          bool = false;
        }
      }
      DeviceIdleJobsController.this.updateWhitelist();
    }
  };
  private boolean mDeviceIdleMode;
  private int[] mDeviceIdleWhitelistAppIds;
  private final JobSchedulerService mJobSchedulerService;
  private final DeviceIdleController.LocalService mLocalDeviceIdleController;
  private final PowerManager mPowerManager;
  final JobStore.JobStatusFunctor mUpdateFunctor = new JobStore.JobStatusFunctor()
  {
    public void process(JobStatus paramAnonymousJobStatus)
    {
      DeviceIdleJobsController.-wrap0(DeviceIdleJobsController.this, paramAnonymousJobStatus);
    }
  };
  
  private DeviceIdleJobsController(JobSchedulerService paramJobSchedulerService, Context paramContext, Object paramObject)
  {
    super(paramJobSchedulerService, paramContext, paramObject);
    this.mJobSchedulerService = paramJobSchedulerService;
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mLocalDeviceIdleController = ((DeviceIdleController.LocalService)LocalServices.getService(DeviceIdleController.LocalService.class));
    paramJobSchedulerService = new IntentFilter();
    paramJobSchedulerService.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
    paramJobSchedulerService.addAction("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
    paramJobSchedulerService.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, paramJobSchedulerService, null, null);
  }
  
  public static DeviceIdleJobsController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (sController == null) {
        sController = new DeviceIdleJobsController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      paramJobSchedulerService = sController;
      return paramJobSchedulerService;
    }
  }
  
  private void updateTaskStateLocked(JobStatus paramJobStatus)
  {
    boolean bool2 = isWhitelistedLocked(paramJobStatus);
    if (this.mDeviceIdleMode) {}
    for (boolean bool1 = bool2;; bool1 = true)
    {
      paramJobStatus.setDeviceNotDozingConstraintSatisfied(bool1, bool2);
      return;
    }
  }
  
  public void dumpControllerStateLocked(final PrintWriter paramPrintWriter, final int paramInt)
  {
    paramPrintWriter.println("DeviceIdleJobsController");
    this.mJobSchedulerService.getJobStore().forEachJob(new JobStore.JobStatusFunctor()
    {
      public void process(JobStatus paramAnonymousJobStatus)
      {
        if (!paramAnonymousJobStatus.shouldDump(paramInt)) {
          return;
        }
        paramPrintWriter.print("  #");
        paramAnonymousJobStatus.printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, paramAnonymousJobStatus.getSourceUid());
        paramPrintWriter.print(": ");
        paramPrintWriter.print(paramAnonymousJobStatus.getSourcePackageName());
        PrintWriter localPrintWriter = paramPrintWriter;
        if ((paramAnonymousJobStatus.satisfiedConstraints & 0x100) != 0) {}
        for (String str = " RUNNABLE";; str = " WAITING")
        {
          localPrintWriter.print(str);
          if (paramAnonymousJobStatus.dozeWhitelisted) {
            paramPrintWriter.print(" WHITELISTED");
          }
          paramPrintWriter.println();
          return;
        }
      }
    });
  }
  
  boolean isWhitelistedLocked(JobStatus paramJobStatus)
  {
    return (this.mDeviceIdleWhitelistAppIds != null) && (ArrayUtils.contains(this.mDeviceIdleWhitelistAppIds, UserHandle.getAppId(paramJobStatus.getSourceUid())));
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus arg2)
  {
    synchronized (this.mLock)
    {
      updateTaskStateLocked(paramJobStatus1);
      return;
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean) {}
  
  void updateIdleMode(boolean paramBoolean)
  {
    int i = 0;
    if (this.mDeviceIdleWhitelistAppIds == null) {
      updateWhitelist();
    }
    synchronized (this.mLock)
    {
      if (this.mDeviceIdleMode != paramBoolean) {
        i = 1;
      }
      this.mDeviceIdleMode = paramBoolean;
      this.mJobSchedulerService.getJobStore().forEachJob(this.mUpdateFunctor);
      if (i != 0) {
        this.mStateChangedListener.onDeviceIdleStateChanged(paramBoolean);
      }
      return;
    }
  }
  
  void updateWhitelist()
  {
    synchronized (this.mLock)
    {
      if (this.mLocalDeviceIdleController != null) {
        this.mDeviceIdleWhitelistAppIds = this.mLocalDeviceIdleController.getPowerSaveWhitelistUserAppIds();
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/DeviceIdleJobsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */