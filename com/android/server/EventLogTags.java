package com.android.server;

import android.util.EventLog;

public class EventLogTags
{
  public static final int AUTO_BRIGHTNESS_ADJ = 35000;
  public static final int BACKUP_AGENT_FAILURE = 2823;
  public static final int BACKUP_DATA_CHANGED = 2820;
  public static final int BACKUP_INITIALIZE = 2827;
  public static final int BACKUP_PACKAGE = 2824;
  public static final int BACKUP_QUOTA_EXCEEDED = 2829;
  public static final int BACKUP_REQUESTED = 2828;
  public static final int BACKUP_RESET = 2826;
  public static final int BACKUP_START = 2821;
  public static final int BACKUP_SUCCESS = 2825;
  public static final int BACKUP_TRANSPORT_FAILURE = 2822;
  public static final int BACKUP_TRANSPORT_LIFECYCLE = 2850;
  public static final int BATTERY_DISCHARGE = 2730;
  public static final int BATTERY_LEVEL = 2722;
  public static final int BATTERY_STATUS = 2723;
  public static final int BOOT_PROGRESS_PMS_DATA_SCAN_START = 3080;
  public static final int BOOT_PROGRESS_PMS_READY = 3100;
  public static final int BOOT_PROGRESS_PMS_SCAN_END = 3090;
  public static final int BOOT_PROGRESS_PMS_START = 3060;
  public static final int BOOT_PROGRESS_PMS_SYSTEM_SCAN_START = 3070;
  public static final int BOOT_PROGRESS_SYSTEM_RUN = 3010;
  public static final int CACHE_FILE_DELETED = 2748;
  public static final int CAMERA_GESTURE_TRIGGERED = 40100;
  public static final int CONFIG_INSTALL_FAILED = 51300;
  public static final int CONNECTIVITY_STATE_CHANGED = 50020;
  public static final int DEVICE_IDLE = 34000;
  public static final int DEVICE_IDLE_LIGHT = 34009;
  public static final int DEVICE_IDLE_LIGHT_STEP = 34010;
  public static final int DEVICE_IDLE_OFF_COMPLETE = 34008;
  public static final int DEVICE_IDLE_OFF_PHASE = 34007;
  public static final int DEVICE_IDLE_OFF_START = 34006;
  public static final int DEVICE_IDLE_ON_COMPLETE = 34005;
  public static final int DEVICE_IDLE_ON_PHASE = 34004;
  public static final int DEVICE_IDLE_ON_START = 34003;
  public static final int DEVICE_IDLE_STEP = 34001;
  public static final int DEVICE_IDLE_WAKE_FROM_IDLE = 34002;
  public static final int FREE_STORAGE_CHANGED = 2744;
  public static final int FREE_STORAGE_LEFT = 2746;
  public static final int FSTRIM_FINISH = 2756;
  public static final int FSTRIM_START = 2755;
  public static final int FULL_BACKUP_AGENT_FAILURE = 2841;
  public static final int FULL_BACKUP_PACKAGE = 2840;
  public static final int FULL_BACKUP_QUOTA_EXCEEDED = 2845;
  public static final int FULL_BACKUP_SUCCESS = 2843;
  public static final int FULL_BACKUP_TRANSPORT_FAILURE = 2842;
  public static final int FULL_RESTORE_PACKAGE = 2844;
  public static final int IDLE_MAINTENANCE_WINDOW_FINISH = 51501;
  public static final int IDLE_MAINTENANCE_WINDOW_START = 51500;
  public static final int IFW_INTENT_MATCHED = 51400;
  public static final int IMF_FORCE_RECONNECT_IME = 32000;
  public static final int LOCKDOWN_VPN_CONNECTED = 51201;
  public static final int LOCKDOWN_VPN_CONNECTING = 51200;
  public static final int LOCKDOWN_VPN_ERROR = 51202;
  public static final int LOW_STORAGE = 2745;
  public static final int NETSTATS_MOBILE_SAMPLE = 51100;
  public static final int NETSTATS_WIFI_SAMPLE = 51101;
  public static final int NOTIFICATION_ACTION_CLICKED = 27521;
  public static final int NOTIFICATION_ALERT = 27532;
  public static final int NOTIFICATION_AUTOGROUPED = 27533;
  public static final int NOTIFICATION_CANCEL = 2751;
  public static final int NOTIFICATION_CANCELED = 27530;
  public static final int NOTIFICATION_CANCEL_ALL = 2752;
  public static final int NOTIFICATION_CLICKED = 27520;
  public static final int NOTIFICATION_ENQUEUE = 2750;
  public static final int NOTIFICATION_EXPANSION = 27511;
  public static final int NOTIFICATION_PANEL_HIDDEN = 27501;
  public static final int NOTIFICATION_PANEL_REVEALED = 27500;
  public static final int NOTIFICATION_UNAUTOGROUPED = 275534;
  public static final int NOTIFICATION_VISIBILITY = 27531;
  public static final int NOTIFICATION_VISIBILITY_CHANGED = 27510;
  public static final int PM_CRITICAL_INFO = 3120;
  public static final int POWER_PARTIAL_WAKE_STATE = 2729;
  public static final int POWER_SCREEN_BROADCAST_DONE = 2726;
  public static final int POWER_SCREEN_BROADCAST_SEND = 2725;
  public static final int POWER_SCREEN_BROADCAST_STOP = 2727;
  public static final int POWER_SCREEN_STATE = 2728;
  public static final int POWER_SLEEP_REQUESTED = 2724;
  public static final int POWER_SOFT_SLEEP_REQUESTED = 2731;
  public static final int RESTORE_AGENT_FAILURE = 2832;
  public static final int RESTORE_PACKAGE = 2833;
  public static final int RESTORE_START = 2830;
  public static final int RESTORE_SUCCESS = 2834;
  public static final int RESTORE_TRANSPORT_FAILURE = 2831;
  public static final int STREAM_DEVICES_CHANGED = 40001;
  public static final int UNKNOWN_SOURCES_ENABLED = 3110;
  public static final int VOLUME_CHANGED = 40000;
  public static final int WATCHDOG = 2802;
  public static final int WATCHDOG_HARD_RESET = 2805;
  public static final int WATCHDOG_MEMINFO = 2809;
  public static final int WATCHDOG_PROC_PSS = 2803;
  public static final int WATCHDOG_PROC_STATS = 2807;
  public static final int WATCHDOG_PSS_STATS = 2806;
  public static final int WATCHDOG_REQUESTED_REBOOT = 2811;
  public static final int WATCHDOG_SCHEDULED_REBOOT = 2808;
  public static final int WATCHDOG_SOFT_RESET = 2804;
  public static final int WATCHDOG_VMSTAT = 2810;
  public static final int WM_BOOT_ANIMATION_DONE = 31007;
  public static final int WM_HOME_STACK_MOVED = 31005;
  public static final int WM_NO_SURFACE_MEMORY = 31000;
  public static final int WM_STACK_CREATED = 31004;
  public static final int WM_STACK_REMOVED = 31006;
  public static final int WM_TASK_CREATED = 31001;
  public static final int WM_TASK_MOVED = 31002;
  public static final int WM_TASK_REMOVED = 31003;
  public static final int WP_WALLPAPER_CRASHED = 33000;
  
  public static void writeAutoBrightnessAdj(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
  {
    EventLog.writeEvent(35000, new Object[] { Float.valueOf(paramFloat1), Float.valueOf(paramFloat2), Float.valueOf(paramFloat3), Float.valueOf(paramFloat4), Float.valueOf(paramFloat5), Float.valueOf(paramFloat6), Float.valueOf(paramFloat7), Float.valueOf(paramFloat8) });
  }
  
  public static void writeBackupAgentFailure(String paramString1, String paramString2)
  {
    EventLog.writeEvent(2823, new Object[] { paramString1, paramString2 });
  }
  
  public static void writeBackupDataChanged(String paramString)
  {
    EventLog.writeEvent(2820, paramString);
  }
  
  public static void writeBackupInitialize()
  {
    EventLog.writeEvent(2827, new Object[0]);
  }
  
  public static void writeBackupPackage(String paramString, int paramInt)
  {
    EventLog.writeEvent(2824, new Object[] { paramString, Integer.valueOf(paramInt) });
  }
  
  public static void writeBackupQuotaExceeded(String paramString)
  {
    EventLog.writeEvent(2829, paramString);
  }
  
  public static void writeBackupRequested(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(2828, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeBackupReset(String paramString)
  {
    EventLog.writeEvent(2826, paramString);
  }
  
  public static void writeBackupStart(String paramString)
  {
    EventLog.writeEvent(2821, paramString);
  }
  
  public static void writeBackupSuccess(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(2825, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeBackupTransportFailure(String paramString)
  {
    EventLog.writeEvent(2822, paramString);
  }
  
  public static void writeBackupTransportLifecycle(String paramString, int paramInt)
  {
    EventLog.writeEvent(2850, new Object[] { paramString, Integer.valueOf(paramInt) });
  }
  
  public static void writeBatteryDischarge(long paramLong, int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(2730, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeBatteryLevel(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(2722, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeBatteryStatus(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    EventLog.writeEvent(2723, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), paramString });
  }
  
  public static void writeBootProgressPmsDataScanStart(long paramLong)
  {
    EventLog.writeEvent(3080, paramLong);
  }
  
  public static void writeBootProgressPmsReady(long paramLong)
  {
    EventLog.writeEvent(3100, paramLong);
  }
  
  public static void writeBootProgressPmsScanEnd(long paramLong)
  {
    EventLog.writeEvent(3090, paramLong);
  }
  
  public static void writeBootProgressPmsStart(long paramLong)
  {
    EventLog.writeEvent(3060, paramLong);
  }
  
  public static void writeBootProgressPmsSystemScanStart(long paramLong)
  {
    EventLog.writeEvent(3070, paramLong);
  }
  
  public static void writeBootProgressSystemRun(long paramLong)
  {
    EventLog.writeEvent(3010, paramLong);
  }
  
  public static void writeCacheFileDeleted(String paramString)
  {
    EventLog.writeEvent(2748, paramString);
  }
  
  public static void writeCameraGestureTriggered(long paramLong1, long paramLong2, long paramLong3, int paramInt)
  {
    EventLog.writeEvent(40100, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Integer.valueOf(paramInt) });
  }
  
  public static void writeConfigInstallFailed(String paramString)
  {
    EventLog.writeEvent(51300, paramString);
  }
  
  public static void writeConnectivityStateChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(50020, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeDeviceIdle(int paramInt, String paramString)
  {
    EventLog.writeEvent(34000, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writeDeviceIdleLight(int paramInt, String paramString)
  {
    EventLog.writeEvent(34009, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writeDeviceIdleLightStep()
  {
    EventLog.writeEvent(34010, new Object[0]);
  }
  
  public static void writeDeviceIdleOffComplete()
  {
    EventLog.writeEvent(34008, new Object[0]);
  }
  
  public static void writeDeviceIdleOffPhase(String paramString)
  {
    EventLog.writeEvent(34007, paramString);
  }
  
  public static void writeDeviceIdleOffStart(String paramString)
  {
    EventLog.writeEvent(34006, paramString);
  }
  
  public static void writeDeviceIdleOnComplete()
  {
    EventLog.writeEvent(34005, new Object[0]);
  }
  
  public static void writeDeviceIdleOnPhase(String paramString)
  {
    EventLog.writeEvent(34004, paramString);
  }
  
  public static void writeDeviceIdleOnStart()
  {
    EventLog.writeEvent(34003, new Object[0]);
  }
  
  public static void writeDeviceIdleStep()
  {
    EventLog.writeEvent(34001, new Object[0]);
  }
  
  public static void writeDeviceIdleWakeFromIdle(int paramInt, String paramString)
  {
    EventLog.writeEvent(34002, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writeFreeStorageChanged(long paramLong)
  {
    EventLog.writeEvent(2744, paramLong);
  }
  
  public static void writeFreeStorageLeft(long paramLong1, long paramLong2, long paramLong3)
  {
    EventLog.writeEvent(2746, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3) });
  }
  
  public static void writeFstrimFinish(long paramLong)
  {
    EventLog.writeEvent(2756, paramLong);
  }
  
  public static void writeFstrimStart(long paramLong)
  {
    EventLog.writeEvent(2755, paramLong);
  }
  
  public static void writeFullBackupAgentFailure(String paramString1, String paramString2)
  {
    EventLog.writeEvent(2841, new Object[] { paramString1, paramString2 });
  }
  
  public static void writeFullBackupPackage(String paramString)
  {
    EventLog.writeEvent(2840, paramString);
  }
  
  public static void writeFullBackupQuotaExceeded(String paramString)
  {
    EventLog.writeEvent(2845, paramString);
  }
  
  public static void writeFullBackupSuccess(String paramString)
  {
    EventLog.writeEvent(2843, paramString);
  }
  
  public static void writeFullBackupTransportFailure()
  {
    EventLog.writeEvent(2842, new Object[0]);
  }
  
  public static void writeFullRestorePackage(String paramString)
  {
    EventLog.writeEvent(2844, paramString);
  }
  
  public static void writeIdleMaintenanceWindowFinish(long paramLong1, long paramLong2, int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(51501, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeIdleMaintenanceWindowStart(long paramLong1, long paramLong2, int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(51500, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeIfwIntentMatched(int paramInt1, String paramString1, int paramInt2, int paramInt3, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt4)
  {
    EventLog.writeEvent(51400, new Object[] { Integer.valueOf(paramInt1), paramString1, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString2, paramString3, paramString4, paramString5, Integer.valueOf(paramInt4) });
  }
  
  public static void writeImfForceReconnectIme(Object[] paramArrayOfObject, long paramLong, int paramInt)
  {
    EventLog.writeEvent(32000, new Object[] { paramArrayOfObject, Long.valueOf(paramLong), Integer.valueOf(paramInt) });
  }
  
  public static void writeLockdownVpnConnected(int paramInt)
  {
    EventLog.writeEvent(51201, paramInt);
  }
  
  public static void writeLockdownVpnConnecting(int paramInt)
  {
    EventLog.writeEvent(51200, paramInt);
  }
  
  public static void writeLockdownVpnError(int paramInt)
  {
    EventLog.writeEvent(51202, paramInt);
  }
  
  public static void writeLowStorage(long paramLong)
  {
    EventLog.writeEvent(2745, paramLong);
  }
  
  public static void writeNetstatsMobileSample(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10, long paramLong11, long paramLong12, long paramLong13)
  {
    EventLog.writeEvent(51100, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8), Long.valueOf(paramLong9), Long.valueOf(paramLong10), Long.valueOf(paramLong11), Long.valueOf(paramLong12), Long.valueOf(paramLong13) });
  }
  
  public static void writeNetstatsWifiSample(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10, long paramLong11, long paramLong12, long paramLong13)
  {
    EventLog.writeEvent(51101, new Object[] { Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8), Long.valueOf(paramLong9), Long.valueOf(paramLong10), Long.valueOf(paramLong11), Long.valueOf(paramLong12), Long.valueOf(paramLong13) });
  }
  
  public static void writeNotificationActionClicked(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    EventLog.writeEvent(27521, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
  }
  
  public static void writeNotificationAlert(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(27532, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeNotificationAutogrouped(String paramString)
  {
    EventLog.writeEvent(27533, paramString);
  }
  
  public static void writeNotificationCancel(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, int paramInt4, int paramInt5, int paramInt6, int paramInt7, String paramString3)
  {
    EventLog.writeEvent(2751, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2, Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), Integer.valueOf(paramInt7), paramString3 });
  }
  
  public static void writeNotificationCancelAll(int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString2)
  {
    EventLog.writeEvent(2752, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), paramString2 });
  }
  
  public static void writeNotificationCanceled(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    EventLog.writeEvent(27530, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
  }
  
  public static void writeNotificationClicked(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(27520, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeNotificationEnqueue(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, int paramInt4, String paramString3, int paramInt5)
  {
    EventLog.writeEvent(2750, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString1, Integer.valueOf(paramInt3), paramString2, Integer.valueOf(paramInt4), paramString3, Integer.valueOf(paramInt5) });
  }
  
  public static void writeNotificationExpansion(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    EventLog.writeEvent(27511, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
  }
  
  public static void writeNotificationPanelHidden()
  {
    EventLog.writeEvent(27501, new Object[0]);
  }
  
  public static void writeNotificationPanelRevealed(int paramInt)
  {
    EventLog.writeEvent(27500, paramInt);
  }
  
  public static void writeNotificationUnautogrouped(String paramString)
  {
    EventLog.writeEvent(275534, paramString);
  }
  
  public static void writeNotificationVisibility(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    EventLog.writeEvent(27531, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
  }
  
  public static void writeNotificationVisibilityChanged(String paramString1, String paramString2)
  {
    EventLog.writeEvent(27510, new Object[] { paramString1, paramString2 });
  }
  
  public static void writePmCriticalInfo(String paramString)
  {
    EventLog.writeEvent(3120, paramString);
  }
  
  public static void writePowerPartialWakeState(int paramInt, String paramString)
  {
    EventLog.writeEvent(2729, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writePowerScreenBroadcastDone(int paramInt1, long paramLong, int paramInt2)
  {
    EventLog.writeEvent(2726, new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(paramInt2) });
  }
  
  public static void writePowerScreenBroadcastSend(int paramInt)
  {
    EventLog.writeEvent(2725, paramInt);
  }
  
  public static void writePowerScreenBroadcastStop(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(2727, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writePowerScreenState(int paramInt1, int paramInt2, long paramLong, int paramInt3)
  {
    EventLog.writeEvent(2728, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(paramInt3) });
  }
  
  public static void writePowerSleepRequested(int paramInt)
  {
    EventLog.writeEvent(2724, paramInt);
  }
  
  public static void writePowerSoftSleepRequested(long paramLong)
  {
    EventLog.writeEvent(2731, paramLong);
  }
  
  public static void writeRestoreAgentFailure(String paramString1, String paramString2)
  {
    EventLog.writeEvent(2832, new Object[] { paramString1, paramString2 });
  }
  
  public static void writeRestorePackage(String paramString, int paramInt)
  {
    EventLog.writeEvent(2833, new Object[] { paramString, Integer.valueOf(paramInt) });
  }
  
  public static void writeRestoreStart(String paramString, long paramLong)
  {
    EventLog.writeEvent(2830, new Object[] { paramString, Long.valueOf(paramLong) });
  }
  
  public static void writeRestoreSuccess(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(2834, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeRestoreTransportFailure()
  {
    EventLog.writeEvent(2831, new Object[0]);
  }
  
  public static void writeStreamDevicesChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(40001, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeUnknownSourcesEnabled(int paramInt)
  {
    EventLog.writeEvent(3110, paramInt);
  }
  
  public static void writeVolumeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    EventLog.writeEvent(40000, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), paramString });
  }
  
  public static void writeWatchdog(String paramString)
  {
    EventLog.writeEvent(2802, paramString);
  }
  
  public static void writeWatchdogHardReset(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(2805, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeWatchdogMeminfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11)
  {
    EventLog.writeEvent(2809, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), Integer.valueOf(paramInt7), Integer.valueOf(paramInt8), Integer.valueOf(paramInt9), Integer.valueOf(paramInt10), Integer.valueOf(paramInt11) });
  }
  
  public static void writeWatchdogProcPss(String paramString, int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(2803, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeWatchdogProcStats(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    EventLog.writeEvent(2807, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
  }
  
  public static void writeWatchdogPssStats(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11)
  {
    EventLog.writeEvent(2806, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), Integer.valueOf(paramInt7), Integer.valueOf(paramInt8), Integer.valueOf(paramInt9), Integer.valueOf(paramInt10), Integer.valueOf(paramInt11) });
  }
  
  public static void writeWatchdogRequestedReboot(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    EventLog.writeEvent(2811, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), Integer.valueOf(paramInt7) });
  }
  
  public static void writeWatchdogScheduledReboot(long paramLong, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    EventLog.writeEvent(2808, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
  }
  
  public static void writeWatchdogSoftReset(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
  {
    EventLog.writeEvent(2804, new Object[] { paramString1, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString2 });
  }
  
  public static void writeWatchdogVmstat(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    EventLog.writeEvent(2810, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
  }
  
  public static void writeWmBootAnimationDone(long paramLong)
  {
    EventLog.writeEvent(31007, paramLong);
  }
  
  public static void writeWmHomeStackMoved(int paramInt)
  {
    EventLog.writeEvent(31005, paramInt);
  }
  
  public static void writeWmNoSurfaceMemory(String paramString1, int paramInt, String paramString2)
  {
    EventLog.writeEvent(31000, new Object[] { paramString1, Integer.valueOf(paramInt), paramString2 });
  }
  
  public static void writeWmStackCreated(int paramInt)
  {
    EventLog.writeEvent(31004, paramInt);
  }
  
  public static void writeWmStackRemoved(int paramInt)
  {
    EventLog.writeEvent(31006, paramInt);
  }
  
  public static void writeWmTaskCreated(int paramInt1, int paramInt2)
  {
    EventLog.writeEvent(31001, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public static void writeWmTaskMoved(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(31002, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeWmTaskRemoved(int paramInt, String paramString)
  {
    EventLog.writeEvent(31003, new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static void writeWpWallpaperCrashed(String paramString)
  {
    EventLog.writeEvent(33000, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */