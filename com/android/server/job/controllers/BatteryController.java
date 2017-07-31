package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManagerInternal;
import android.os.UserHandle;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatteryController
  extends StateController
{
  private static final String TAG = "JobScheduler.Batt";
  private static volatile BatteryController sController;
  private static final Object sCreationLock = new Object();
  private ChargingTracker mChargeTracker = new ChargingTracker();
  private List<JobStatus> mTrackedTasks = new ArrayList();
  
  private BatteryController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    super(paramStateChangedListener, paramContext, paramObject);
    this.mChargeTracker.startTracking();
  }
  
  public static BatteryController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (sController == null) {
        sController = new BatteryController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      return sController;
    }
  }
  
  public static BatteryController getForTesting(StateChangedListener paramStateChangedListener, Context paramContext)
  {
    return new BatteryController(paramStateChangedListener, paramContext, new Object());
  }
  
  private void maybeReportNewChargingState()
  {
    boolean bool1 = this.mChargeTracker.isOnStablePower();
    int i = 0;
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mTrackedTasks.iterator();
      while (localIterator.hasNext())
      {
        boolean bool2 = ((JobStatus)localIterator.next()).setChargingConstraintSatisfied(bool1);
        if (bool2 != bool1) {
          i = 1;
        }
      }
      if (i != 0) {
        this.mStateChangedListener.onControllerStateChanged();
      }
      if (bool1) {
        this.mStateChangedListener.onRunJobNow(null);
      }
      return;
    }
  }
  
  public void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt)
  {
    paramPrintWriter.print("Battery: stable power = ");
    paramPrintWriter.println(this.mChargeTracker.isOnStablePower());
    paramPrintWriter.print("Tracking ");
    paramPrintWriter.print(this.mTrackedTasks.size());
    paramPrintWriter.println(":");
    int i = 0;
    if (i < this.mTrackedTasks.size())
    {
      JobStatus localJobStatus = (JobStatus)this.mTrackedTasks.get(i);
      if (!localJobStatus.shouldDump(paramInt)) {}
      for (;;)
      {
        i += 1;
        break;
        paramPrintWriter.print("  #");
        localJobStatus.printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, localJobStatus.getSourceUid());
        paramPrintWriter.println();
      }
    }
  }
  
  public ChargingTracker getTracker()
  {
    return this.mChargeTracker;
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    boolean bool = this.mChargeTracker.isOnStablePower();
    if (paramJobStatus1.hasChargingConstraint())
    {
      this.mTrackedTasks.add(paramJobStatus1);
      paramJobStatus1.setChargingConstraintSatisfied(bool);
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    if (paramJobStatus1.hasChargingConstraint()) {
      this.mTrackedTasks.remove(paramJobStatus1);
    }
  }
  
  public class ChargingTracker
    extends BroadcastReceiver
  {
    private boolean mBatteryHealthy;
    private boolean mCharging;
    
    public ChargingTracker() {}
    
    boolean isOnStablePower()
    {
      if (this.mCharging) {
        return this.mBatteryHealthy;
      }
      return false;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      onReceiveInternal(paramIntent);
    }
    
    public void onReceiveInternal(Intent paramIntent)
    {
      paramIntent = paramIntent.getAction();
      if ("android.intent.action.BATTERY_LOW".equals(paramIntent)) {
        this.mBatteryHealthy = false;
      }
      do
      {
        return;
        if ("android.intent.action.BATTERY_OKAY".equals(paramIntent))
        {
          this.mBatteryHealthy = true;
          BatteryController.-wrap0(BatteryController.this);
          return;
        }
        if ("android.os.action.CHARGING".equals(paramIntent))
        {
          this.mCharging = true;
          BatteryController.-wrap0(BatteryController.this);
          return;
        }
      } while (!"android.os.action.DISCHARGING".equals(paramIntent));
      this.mCharging = false;
      BatteryController.-wrap0(BatteryController.this);
    }
    
    public void startTracking()
    {
      Object localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.BATTERY_LOW");
      ((IntentFilter)localObject).addAction("android.intent.action.BATTERY_OKAY");
      ((IntentFilter)localObject).addAction("android.os.action.CHARGING");
      ((IntentFilter)localObject).addAction("android.os.action.DISCHARGING");
      BatteryController.this.mContext.registerReceiver(this, (IntentFilter)localObject);
      localObject = (BatteryManagerInternal)LocalServices.getService(BatteryManagerInternal.class);
      if (((BatteryManagerInternal)localObject).getBatteryLevelLow()) {}
      for (boolean bool = false;; bool = true)
      {
        this.mBatteryHealthy = bool;
        this.mCharging = ((BatteryManagerInternal)localObject).isPowered(7);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/BatteryController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */