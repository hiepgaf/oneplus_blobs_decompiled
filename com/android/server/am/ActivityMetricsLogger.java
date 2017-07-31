package com.android.server.am;

import android.app.ActivityManager.StackId;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.app.ProcessMap;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;

class ActivityMetricsLogger
{
  private static final long INVALID_START_TIME = -1L;
  private static final String TAG = "ActivityManager";
  private static final String[] TRON_WINDOW_STATE_VARZ_STRINGS = { "window_time_0", "window_time_1", "window_time_2" };
  private static final int WINDOW_STATE_FREEFORM = 2;
  private static final int WINDOW_STATE_INVALID = -1;
  private static final int WINDOW_STATE_SIDE_BY_SIDE = 1;
  private static final int WINDOW_STATE_STANDARD = 0;
  private final Context mContext;
  private long mCurrentTransitionStartTime = -1L;
  private long mLastLogTimeSecs = SystemClock.elapsedRealtime() / 1000L;
  private boolean mLoggedStartingWindowDrawn;
  private boolean mLoggedTransitionStarting;
  private boolean mLoggedWindowsDrawn;
  private final ActivityStackSupervisor mSupervisor;
  private int mWindowState = 0;
  
  ActivityMetricsLogger(ActivityStackSupervisor paramActivityStackSupervisor, Context paramContext)
  {
    this.mSupervisor = paramActivityStackSupervisor;
    this.mContext = paramContext;
  }
  
  private int calculateCurrentDelay()
  {
    return (int)(System.currentTimeMillis() - this.mCurrentTransitionStartTime);
  }
  
  private boolean hasStartedActivity(ProcessRecord paramProcessRecord, ActivityRecord paramActivityRecord)
  {
    paramProcessRecord = paramProcessRecord.activities;
    int i = paramProcessRecord.size() - 1;
    if (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)paramProcessRecord.get(i);
      if (paramActivityRecord == localActivityRecord) {}
      while (localActivityRecord.stopped)
      {
        i -= 1;
        break;
      }
      return true;
    }
    return false;
  }
  
  private boolean isTransitionActive()
  {
    return this.mCurrentTransitionStartTime != -1L;
  }
  
  private void notifyActivityLaunched(int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramInt < 0) || (paramString == null)) {}
    while (!paramBoolean2)
    {
      reset();
      return;
    }
    MetricsLogger.action(this.mContext, 323, paramString);
    MetricsLogger.action(this.mContext, 324, paramBoolean1);
    MetricsLogger.action(this.mContext, 325, (int)(SystemClock.uptimeMillis() / 1000L));
  }
  
  private void reset()
  {
    this.mCurrentTransitionStartTime = -1L;
    this.mLoggedWindowsDrawn = false;
    this.mLoggedTransitionStarting = false;
    this.mLoggedStartingWindowDrawn = false;
  }
  
  void logWindowState()
  {
    long l = SystemClock.elapsedRealtime() / 1000L;
    if (this.mWindowState != -1) {
      MetricsLogger.count(this.mContext, TRON_WINDOW_STATE_VARZ_STRINGS[this.mWindowState], (int)(l - this.mLastLogTimeSecs));
    }
    this.mLastLogTimeSecs = l;
    Object localObject = this.mSupervisor.getStack(3);
    if ((localObject != null) && (((ActivityStack)localObject).getStackVisibilityLocked(null) != 0))
    {
      this.mWindowState = 1;
      return;
    }
    this.mWindowState = -1;
    ActivityStack localActivityStack = this.mSupervisor.getFocusedStack();
    localObject = localActivityStack;
    if (localActivityStack.mStackId == 4) {
      localObject = this.mSupervisor.findStackBehind(localActivityStack);
    }
    if ((((ActivityStack)localObject).mStackId == 0) || (((ActivityStack)localObject).mStackId == 1)) {
      this.mWindowState = 0;
    }
    do
    {
      return;
      if (((ActivityStack)localObject).mStackId == 3)
      {
        Slog.wtf(TAG, "Docked stack shouldn't be the focused stack, because it reported not being visible.");
        this.mWindowState = -1;
        return;
      }
      if (((ActivityStack)localObject).mStackId == 2)
      {
        this.mWindowState = 2;
        return;
      }
    } while (!ActivityManager.StackId.isStaticStack(((ActivityStack)localObject).mStackId));
    throw new IllegalStateException("Unknown stack=" + localObject);
  }
  
  void notifyActivityLaunched(int paramInt, ActivityRecord paramActivityRecord)
  {
    boolean bool3 = true;
    ProcessRecord localProcessRecord = null;
    if (paramActivityRecord != null) {
      localProcessRecord = (ProcessRecord)this.mSupervisor.mService.mProcessNames.get(paramActivityRecord.processName, paramActivityRecord.appInfo.uid);
    }
    boolean bool1;
    if (localProcessRecord != null)
    {
      bool1 = true;
      if (paramActivityRecord == null) {
        break label98;
      }
    }
    label98:
    for (String str = paramActivityRecord.shortComponentName;; str = null)
    {
      boolean bool2 = bool3;
      if (localProcessRecord != null)
      {
        bool2 = bool3;
        if (hasStartedActivity(localProcessRecord, paramActivityRecord)) {
          bool2 = false;
        }
      }
      notifyActivityLaunched(paramInt, str, bool1, bool2);
      return;
      bool1 = false;
      break;
    }
  }
  
  void notifyActivityLaunching()
  {
    this.mCurrentTransitionStartTime = System.currentTimeMillis();
  }
  
  void notifyStartingWindowDrawn()
  {
    if ((!isTransitionActive()) || (this.mLoggedStartingWindowDrawn)) {
      return;
    }
    this.mLoggedStartingWindowDrawn = true;
    MetricsLogger.action(this.mContext, 321, calculateCurrentDelay());
  }
  
  void notifyTransitionStarting(int paramInt)
  {
    if ((!isTransitionActive()) || (this.mLoggedTransitionStarting)) {
      return;
    }
    MetricsLogger.action(this.mContext, 320, paramInt);
    MetricsLogger.action(this.mContext, 319, calculateCurrentDelay());
    this.mLoggedTransitionStarting = true;
    if (this.mLoggedWindowsDrawn) {
      reset();
    }
  }
  
  void notifyWindowsDrawn()
  {
    if ((!isTransitionActive()) || (this.mLoggedWindowsDrawn)) {
      return;
    }
    MetricsLogger.action(this.mContext, 322, calculateCurrentDelay());
    this.mLoggedWindowsDrawn = true;
    if (this.mLoggedTransitionStarting) {
      reset();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityMetricsLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */