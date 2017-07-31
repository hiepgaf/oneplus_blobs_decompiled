package com.android.server.am;

import android.util.EventLog;

public class EventLogTags
{
  public static final int AM_ACTIVITY_FULLY_DRAWN_TIME = 30042;
  public static final int AM_ACTIVITY_LAUNCH_TIME = 30009;
  public static final int AM_ANR = 30008;
  public static final int AM_BROADCAST_DISCARD_APP = 30025;
  public static final int AM_BROADCAST_DISCARD_FILTER = 30024;
  public static final int AM_CRASH = 30039;
  public static final int AM_CREATE_ACTIVITY = 30005;
  public static final int AM_CREATE_SERVICE = 30030;
  public static final int AM_CREATE_TASK = 30004;
  public static final int AM_DESTROY_ACTIVITY = 30018;
  public static final int AM_DESTROY_SERVICE = 30031;
  public static final int AM_DROP_PROCESS = 30033;
  public static final int AM_FAILED_TO_PAUSE = 30012;
  public static final int AM_FINISH_ACTIVITY = 30001;
  public static final int AM_FOCUSED_ACTIVITY = 30043;
  public static final int AM_FOCUSED_STACK = 30044;
  public static final int AM_KILL = 30023;
  public static final int AM_LOW_MEMORY = 30017;
  public static final int AM_MEMINFO = 30046;
  public static final int AM_MEM_FACTOR = 30050;
  public static final int AM_NEW_INTENT = 30003;
  public static final int AM_ON_PAUSED_CALLED = 30021;
  public static final int AM_ON_RESUME_CALLED = 30022;
  public static final int AM_ON_STOP_CALLED = 30049;
  public static final int AM_PAUSE_ACTIVITY = 30013;
  public static final int AM_PRE_BOOT = 30045;
  public static final int AM_PROCESS_CRASHED_TOO_MUCH = 30032;
  public static final int AM_PROCESS_START_TIMEOUT = 30037;
  public static final int AM_PROC_BAD = 30015;
  public static final int AM_PROC_BOUND = 30010;
  public static final int AM_PROC_DIED = 30011;
  public static final int AM_PROC_GOOD = 30016;
  public static final int AM_PROC_START = 30014;
  public static final int AM_PROVIDER_LOST_PROCESS = 30036;
  public static final int AM_PSS = 30047;
  public static final int AM_RELAUNCH_ACTIVITY = 30020;
  public static final int AM_RELAUNCH_RESUME_ACTIVITY = 30019;
  public static final int AM_RESTART_ACTIVITY = 30006;
  public static final int AM_RESUME_ACTIVITY = 30007;
  public static final int AM_SCHEDULE_SERVICE_RESTART = 30035;
  public static final int AM_SERVICE_CRASHED_TOO_MUCH = 30034;
  public static final int AM_STOP_ACTIVITY = 30048;
  public static final int AM_SWITCH_USER = 30041;
  public static final int AM_TASK_TO_FRONT = 30002;
  public static final int AM_WTF = 30040;
  public static final int BOOT_PROGRESS_AMS_READY = 3040;
  public static final int BOOT_PROGRESS_ENABLE_SCREEN = 3050;
  public static final int CONFIGURATION_CHANGED = 2719;
  public static final int CPU = 2721;
  
  public static void writeAmActivityFullyDrawnTime(int paramInt1, int paramInt2, String paramString, long paramLong)
  {
    EventLog.writeEvent(30042, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Long.valueOf(paramLong) });
  }
  
  public static void writeAmActivityLaunchTime(int paramInt1, int paramInt2, String paramString, long paramLong)
  {
    EventLog.writeEvent(30009, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Long.valueOf(paramLong) });
  }
  
  public static void writeAmAnr(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2)
  {
    EventLog.writeEvent(30008, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2 });
  }
  
  public static void writeAmBroadcastDiscardApp(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2)
  {
    EventLog.writeEvent(30025, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2 });
  }
  
  public static void writeAmBroadcastDiscardFilter(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4)
  {
    EventLog.writeEvent(30024, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
  }
  
  public static void writeAmCrash(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, String paramString3, String paramString4, int paramInt4)
  {
    EventLog.writeEvent(30039, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2, paramString3, paramString4, Integer.valueOf(paramInt4) });
  }
  
  public static void writeAmCreateActivity(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt4)
  {
    EventLog.writeEvent(30005, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString1, paramString2, paramString3, paramString4, Integer.valueOf(paramInt4) });
  }
  
  public static void writeAmCreateService(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4)
  {
    EventLog.writeEvent(30030, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
  }
  
  public static void writeAmCreateTask(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(30004, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeAmDestroyActivity(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30018, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString1, paramString2 });
  }
  
  public static void writeAmDestroyService(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(30031, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeAmDropProcess(int paramInt)
  {
    EventLog.writeEvent(30033, paramInt);
  }
  
  public static void writeAmFailedToPause(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30012, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, paramString2 });
  }
  
  public static void writeAmFinishActivity(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30001, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString1, paramString2 });
  }
  
  public static void writeAmFocusedActivity(int paramInt, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30043, new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 });
  }
  
  public static void writeAmFocusedStack(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30044, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmKill(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2)
  {
    EventLog.writeEvent(30023, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2 });
  }
  
  public static void writeAmLowMemory(int paramInt)
  {
    EventLog.writeEvent(30017, paramInt);
  }
  
  public static void writeAmMemFactor(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(30050, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeAmMeminfo(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    EventLog.writeEvent(30046, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5) });
  }
  
  public static void writeAmNewIntent(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt4)
  {
    EventLog.writeEvent(30003, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString1, paramString2, paramString3, paramString4, Integer.valueOf(paramInt4) });
  }
  
  public static void writeAmOnPausedCalled(int paramInt, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30021, new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 });
  }
  
  public static void writeAmOnResumeCalled(int paramInt, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30022, new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 });
  }
  
  public static void writeAmOnStopCalled(int paramInt, String paramString1, String paramString2)
  {
    EventLog.writeEvent(30049, new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 });
  }
  
  public static void writeAmPauseActivity(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30013, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmPreBoot(int paramInt, String paramString)
  {
    EventLog.writeEvent(30045, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writeAmProcBad(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30015, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmProcBound(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30010, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmProcDied(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30011, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmProcGood(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30016, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmProcStart(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3)
  {
    EventLog.writeEvent(30014, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString1, paramString2, paramString3 });
  }
  
  public static void writeAmProcessCrashedTooMuch(int paramInt1, String paramString, int paramInt2)
  {
    EventLog.writeEvent(30032, new Object[] { Integer.valueOf(paramInt1), paramString, Integer.valueOf(paramInt2) });
  }
  
  public static void writeAmProcessStartTimeout(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30037, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmProviderLostProcess(int paramInt1, String paramString1, int paramInt2, String paramString2)
  {
    EventLog.writeEvent(30036, new Object[] { Integer.valueOf(paramInt1), paramString1, Integer.valueOf(paramInt2), paramString2 });
  }
  
  public static void writeAmPss(int paramInt1, int paramInt2, String paramString, long paramLong1, long paramLong2, long paramLong3)
  {
    EventLog.writeEvent(30047, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3) });
  }
  
  public static void writeAmRelaunchActivity(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30020, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmRelaunchResumeActivity(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30019, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmRestartActivity(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30006, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmResumeActivity(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(30007, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeAmScheduleServiceRestart(int paramInt, String paramString, long paramLong)
  {
    EventLog.writeEvent(30035, new Object[] { Integer.valueOf(paramInt), paramString, Long.valueOf(paramLong) });
  }
  
  public static void writeAmServiceCrashedTooMuch(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    EventLog.writeEvent(30034, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString, Integer.valueOf(paramInt3) });
  }
  
  public static void writeAmStopActivity(int paramInt1, int paramInt2, String paramString)
  {
    EventLog.writeEvent(30048, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
  }
  
  public static void writeAmSwitchUser(int paramInt)
  {
    EventLog.writeEvent(30041, paramInt);
  }
  
  public static void writeAmTaskToFront(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(30002, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeAmWtf(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, String paramString3)
  {
    EventLog.writeEvent(30040, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2, paramString3 });
  }
  
  public static void writeBootProgressAmsReady(long paramLong)
  {
    EventLog.writeEvent(3040, paramLong);
  }
  
  public static void writeBootProgressEnableScreen(long paramLong)
  {
    EventLog.writeEvent(3050, paramLong);
  }
  
  public static void writeConfigurationChanged(int paramInt)
  {
    EventLog.writeEvent(2719, paramInt);
  }
  
  public static void writeCpu(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    EventLog.writeEvent(2721, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */