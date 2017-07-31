package com.android.server.job.controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.SystemClock;
import android.os.UserHandle;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class IdleController
  extends StateController
{
  private static final String TAG = "IdleController";
  private static volatile IdleController sController;
  private static Object sCreationLock = new Object();
  IdlenessTracker mIdleTracker;
  private long mIdleWindowSlop;
  private long mInactivityIdleThreshold;
  final ArrayList<JobStatus> mTrackedTasks = new ArrayList();
  
  private IdleController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    super(paramStateChangedListener, paramContext, paramObject);
    initIdleStateTracking();
  }
  
  public static IdleController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (sController == null) {
        sController = new IdleController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      paramJobSchedulerService = sController;
      return paramJobSchedulerService;
    }
  }
  
  private void initIdleStateTracking()
  {
    this.mInactivityIdleThreshold = this.mContext.getResources().getInteger(17694887);
    this.mIdleWindowSlop = this.mContext.getResources().getInteger(17694888);
    this.mIdleTracker = new IdlenessTracker();
    this.mIdleTracker.startTracking();
  }
  
  public void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt)
  {
    paramPrintWriter.print("Idle: ");
    Object localObject;
    int i;
    if (this.mIdleTracker.isIdle())
    {
      localObject = "true";
      paramPrintWriter.println((String)localObject);
      paramPrintWriter.print("Tracking ");
      paramPrintWriter.print(this.mTrackedTasks.size());
      paramPrintWriter.println(":");
      i = 0;
      label51:
      if (i >= this.mTrackedTasks.size()) {
        return;
      }
      localObject = (JobStatus)this.mTrackedTasks.get(i);
      if (((JobStatus)localObject).shouldDump(paramInt)) {
        break label98;
      }
    }
    for (;;)
    {
      i += 1;
      break label51;
      localObject = "false";
      break;
      label98:
      paramPrintWriter.print("  #");
      ((JobStatus)localObject).printUniqueId(paramPrintWriter);
      paramPrintWriter.print(" from ");
      UserHandle.formatUid(paramPrintWriter, ((JobStatus)localObject).getSourceUid());
      paramPrintWriter.println();
    }
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    if (paramJobStatus1.hasIdleConstraint())
    {
      this.mTrackedTasks.add(paramJobStatus1);
      paramJobStatus1.setIdleConstraintSatisfied(this.mIdleTracker.isIdle());
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    this.mTrackedTasks.remove(paramJobStatus1);
  }
  
  void reportNewIdleState(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mTrackedTasks.iterator();
      if (localIterator.hasNext()) {
        ((JobStatus)localIterator.next()).setIdleConstraintSatisfied(paramBoolean);
      }
    }
    this.mStateChangedListener.onControllerStateChanged();
  }
  
  class IdlenessTracker
    extends BroadcastReceiver
  {
    private AlarmManager mAlarm = (AlarmManager)IdleController.this.mContext.getSystemService("alarm");
    boolean mIdle;
    private PendingIntent mIdleTriggerIntent;
    boolean mScreenOn;
    
    public IdlenessTracker()
    {
      Intent localIntent = new Intent("com.android.server.ACTION_TRIGGER_IDLE").setPackage("android").setFlags(1073741824);
      this.mIdleTriggerIntent = PendingIntent.getBroadcast(IdleController.this.mContext, 0, localIntent, 0);
      this.mIdle = false;
      this.mScreenOn = true;
    }
    
    public boolean isIdle()
    {
      return this.mIdle;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ((paramContext.equals("android.intent.action.SCREEN_ON")) || (paramContext.equals("android.intent.action.DREAMING_STOPPED")))
      {
        this.mScreenOn = true;
        this.mAlarm.cancel(this.mIdleTriggerIntent);
        if (this.mIdle)
        {
          this.mIdle = false;
          IdleController.this.reportNewIdleState(this.mIdle);
        }
      }
      do
      {
        return;
        if ((paramContext.equals("android.intent.action.SCREEN_OFF")) || (paramContext.equals("android.intent.action.DREAMING_STARTED")))
        {
          long l1 = SystemClock.elapsedRealtime();
          long l2 = IdleController.-get1(IdleController.this);
          this.mScreenOn = false;
          this.mAlarm.setWindow(2, l1 + l2, IdleController.-get0(IdleController.this), this.mIdleTriggerIntent);
          return;
        }
      } while ((!paramContext.equals("com.android.server.ACTION_TRIGGER_IDLE")) || (this.mIdle) || (this.mScreenOn));
      this.mIdle = true;
      IdleController.this.reportNewIdleState(this.mIdle);
    }
    
    public void startTracking()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.SCREEN_ON");
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.intent.action.DREAMING_STARTED");
      localIntentFilter.addAction("android.intent.action.DREAMING_STOPPED");
      localIntentFilter.addAction("com.android.server.ACTION_TRIGGER_IDLE");
      IdleController.this.mContext.registerReceiver(this, localIntentFilter);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/IdleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */