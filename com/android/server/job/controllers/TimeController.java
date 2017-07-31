package com.android.server.job.controllers;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.content.Context;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.TimeUtils;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TimeController
  extends StateController
{
  private static final String TAG = "JobScheduler.Time";
  private static TimeController mSingleton;
  private final String DEADLINE_TAG = "*job.deadline*";
  private final String DELAY_TAG = "*job.delay*";
  private AlarmManager mAlarmService = null;
  private final AlarmManager.OnAlarmListener mDeadlineExpiredListener = new AlarmManager.OnAlarmListener()
  {
    public void onAlarm()
    {
      TimeController.-wrap0(TimeController.this);
    }
  };
  private long mNextDelayExpiredElapsedMillis = Long.MAX_VALUE;
  private final AlarmManager.OnAlarmListener mNextDelayExpiredListener = new AlarmManager.OnAlarmListener()
  {
    public void onAlarm()
    {
      TimeController.-wrap1(TimeController.this);
    }
  };
  private long mNextJobExpiredElapsedMillis = Long.MAX_VALUE;
  private final List<JobStatus> mTrackedJobs = new LinkedList();
  
  private TimeController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    super(paramStateChangedListener, paramContext, paramObject);
  }
  
  private boolean canStopTrackingJobLocked(JobStatus paramJobStatus)
  {
    boolean bool2 = true;
    boolean bool1 = false;
    if ((!paramJobStatus.hasTimingDelayConstraint()) || ((paramJobStatus.satisfiedConstraints & 0x2) != 0))
    {
      bool1 = bool2;
      if (paramJobStatus.hasDeadlineConstraint()) {
        if ((paramJobStatus.satisfiedConstraints & 0x4) == 0) {
          break label42;
        }
      }
    }
    label42:
    for (bool1 = bool2;; bool1 = false) {
      return bool1;
    }
  }
  
  private void checkExpiredDeadlinesAndResetAlarm()
  {
    Object localObject1 = this.mLock;
    long l2 = Long.MAX_VALUE;
    int j = 0;
    long l1;
    int i;
    try
    {
      long l3 = SystemClock.elapsedRealtime();
      Iterator localIterator = this.mTrackedJobs.iterator();
      JobStatus localJobStatus;
      for (;;)
      {
        l1 = l2;
        i = j;
        if (!localIterator.hasNext()) {
          break label137;
        }
        localJobStatus = (JobStatus)localIterator.next();
        if (localJobStatus.hasDeadlineConstraint())
        {
          l1 = localJobStatus.getLatestRunTimeElapsed();
          if (l1 > l3) {
            break;
          }
          if (localJobStatus.hasTimingDelayConstraint()) {
            localJobStatus.setTimingDelayConstraintSatisfied(true);
          }
          localJobStatus.setDeadlineConstraintSatisfied(true);
          this.mStateChangedListener.onRunJobNow(localJobStatus);
          localIterator.remove();
        }
      }
      i = localJobStatus.getSourceUid();
    }
    finally {}
    label137:
    setDeadlineExpiredAlarmLocked(l1, i);
  }
  
  private void checkExpiredDelaysAndResetAlarm()
  {
    synchronized (this.mLock)
    {
      long l3 = SystemClock.elapsedRealtime();
      long l1 = Long.MAX_VALUE;
      int j = 0;
      int i = 0;
      Iterator localIterator = this.mTrackedJobs.iterator();
      while (localIterator.hasNext())
      {
        JobStatus localJobStatus = (JobStatus)localIterator.next();
        if (localJobStatus.hasTimingDelayConstraint())
        {
          long l2 = localJobStatus.getEarliestRunTime();
          if (l2 <= l3)
          {
            localJobStatus.setTimingDelayConstraintSatisfied(true);
            if (canStopTrackingJobLocked(localJobStatus)) {
              localIterator.remove();
            }
            if (localJobStatus.isReady()) {
              i = 1;
            }
          }
          else if ((!localJobStatus.isConstraintSatisfied(2)) && (l1 > l2))
          {
            l1 = l2;
            j = localJobStatus.getSourceUid();
          }
        }
      }
      if (i != 0) {
        this.mStateChangedListener.onControllerStateChanged();
      }
      setDelayExpiredAlarmLocked(l1, j);
      return;
    }
  }
  
  private void ensureAlarmServiceLocked()
  {
    if (this.mAlarmService == null) {
      this.mAlarmService = ((AlarmManager)this.mContext.getSystemService("alarm"));
    }
  }
  
  public static TimeController get(JobSchedulerService paramJobSchedulerService)
  {
    try
    {
      if (mSingleton == null) {
        mSingleton = new TimeController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      paramJobSchedulerService = mSingleton;
      return paramJobSchedulerService;
    }
    finally {}
  }
  
  private long maybeAdjustAlarmTime(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    if (paramLong < l) {
      return l;
    }
    return paramLong;
  }
  
  private void maybeUpdateAlarmsLocked(long paramLong1, long paramLong2, int paramInt)
  {
    if (paramLong1 < this.mNextDelayExpiredElapsedMillis) {
      setDelayExpiredAlarmLocked(paramLong1, paramInt);
    }
    if (paramLong2 < this.mNextJobExpiredElapsedMillis) {
      setDeadlineExpiredAlarmLocked(paramLong2, paramInt);
    }
  }
  
  private void setDeadlineExpiredAlarmLocked(long paramLong, int paramInt)
  {
    this.mNextJobExpiredElapsedMillis = maybeAdjustAlarmTime(paramLong);
    updateAlarmWithListenerLocked("*job.deadline*", this.mDeadlineExpiredListener, this.mNextJobExpiredElapsedMillis, paramInt);
  }
  
  private void setDelayExpiredAlarmLocked(long paramLong, int paramInt)
  {
    this.mNextDelayExpiredElapsedMillis = maybeAdjustAlarmTime(paramLong);
    updateAlarmWithListenerLocked("*job.delay*", this.mNextDelayExpiredListener, this.mNextDelayExpiredElapsedMillis, paramInt);
  }
  
  private void updateAlarmWithListenerLocked(String paramString, AlarmManager.OnAlarmListener paramOnAlarmListener, long paramLong, int paramInt)
  {
    ensureAlarmServiceLocked();
    if (paramLong == Long.MAX_VALUE)
    {
      this.mAlarmService.cancel(paramOnAlarmListener);
      return;
    }
    this.mAlarmService.set(2, paramLong, -1L, 0L, paramString, paramOnAlarmListener, null, new WorkSource(paramInt));
  }
  
  public void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt)
  {
    long l = SystemClock.elapsedRealtime();
    paramPrintWriter.print("Alarms: now=");
    paramPrintWriter.print(SystemClock.elapsedRealtime());
    paramPrintWriter.println();
    paramPrintWriter.print("Next delay alarm in ");
    TimeUtils.formatDuration(this.mNextDelayExpiredElapsedMillis, l, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("Next deadline alarm in ");
    TimeUtils.formatDuration(this.mNextJobExpiredElapsedMillis, l, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("Tracking ");
    paramPrintWriter.print(this.mTrackedJobs.size());
    paramPrintWriter.println(":");
    Iterator localIterator = this.mTrackedJobs.iterator();
    while (localIterator.hasNext())
    {
      JobStatus localJobStatus = (JobStatus)localIterator.next();
      if (localJobStatus.shouldDump(paramInt))
      {
        paramPrintWriter.print("  #");
        localJobStatus.printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, localJobStatus.getSourceUid());
        paramPrintWriter.print(": Delay=");
        if (localJobStatus.hasTimingDelayConstraint())
        {
          TimeUtils.formatDuration(localJobStatus.getEarliestRunTime(), l, paramPrintWriter);
          label180:
          paramPrintWriter.print(", Deadline=");
          if (!localJobStatus.hasDeadlineConstraint()) {
            break label222;
          }
          TimeUtils.formatDuration(localJobStatus.getLatestRunTimeElapsed(), l, paramPrintWriter);
        }
        for (;;)
        {
          paramPrintWriter.println();
          break;
          paramPrintWriter.print("N/A");
          break label180;
          label222:
          paramPrintWriter.print("N/A");
        }
      }
    }
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    long l2 = Long.MAX_VALUE;
    if ((paramJobStatus1.hasTimingDelayConstraint()) || (paramJobStatus1.hasDeadlineConstraint()))
    {
      maybeStopTrackingJobLocked(paramJobStatus1, null, false);
      int j = 0;
      paramJobStatus2 = this.mTrackedJobs.listIterator(this.mTrackedJobs.size());
      do
      {
        i = j;
        if (!paramJobStatus2.hasPrevious()) {
          break;
        }
      } while (((JobStatus)paramJobStatus2.previous()).getLatestRunTimeElapsed() >= paramJobStatus1.getLatestRunTimeElapsed());
      int i = 1;
      if (i != 0) {
        paramJobStatus2.next();
      }
      paramJobStatus2.add(paramJobStatus1);
      if (!paramJobStatus1.hasTimingDelayConstraint()) {
        break label139;
      }
    }
    label139:
    for (long l1 = paramJobStatus1.getEarliestRunTime();; l1 = Long.MAX_VALUE)
    {
      if (paramJobStatus1.hasDeadlineConstraint()) {
        l2 = paramJobStatus1.getLatestRunTimeElapsed();
      }
      maybeUpdateAlarmsLocked(l1, l2, paramJobStatus1.getSourceUid());
      return;
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    if (this.mTrackedJobs.remove(paramJobStatus1))
    {
      checkExpiredDelaysAndResetAlarm();
      checkExpiredDeadlinesAndResetAlarm();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/TimeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */