package com.android.server.power;

import android.annotation.IntDef;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayManagerInternal.DisplayPowerCallbacks;
import android.hardware.display.DisplayManagerInternal.DisplayPowerRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManagerInternal;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IPowerManager.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.service.dreams.DreamManagerInternal;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.EventLog;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.view.Display;
import android.view.WindowManagerPolicy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.server.EventLogTags;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import com.android.server.am.BatteryStatsService;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.lights.LightsService;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import libcore.util.Objects;
import net.oneplus.odm.insight.tracker.OSTracker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class PowerManagerService
  extends SystemService
  implements Watchdog.Monitor
{
  private static final int BUTTON_OFF_TIMEOUT = 1000;
  private static boolean DEBUG = false;
  private static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  private static boolean DEBUG_SPEW = false;
  private static final int DEFAULT_DOUBLE_TAP_TO_WAKE = 0;
  private static final int DEFAULT_SCREEN_OFF_TIMEOUT = 15000;
  private static final int DEFAULT_SLEEP_TIMEOUT = -1;
  private static final int DIRTY_ACTUAL_DISPLAY_POWER_STATE_UPDATED = 8;
  private static final int DIRTY_BATTERY_STATE = 256;
  private static final int DIRTY_BOOT_COMPLETED = 16;
  private static final int DIRTY_DOCK_STATE = 1024;
  private static final int DIRTY_IS_POWERED = 64;
  private static final int DIRTY_PROXIMITY_POSITIVE = 512;
  private static final int DIRTY_SCREEN_BRIGHTNESS_BOOST = 2048;
  private static final int DIRTY_SETTINGS = 32;
  private static final int DIRTY_STAY_ON = 128;
  private static final int DIRTY_USER_ACTIVITY = 4;
  private static final int DIRTY_WAKEFULNESS = 2;
  protected static final int DIRTY_WAKE_LOCKS = 1;
  static final int GET_ONLINECONFIG = 7;
  private static final int HALT_MODE_REBOOT = 1;
  private static final int HALT_MODE_REBOOT_SAFE_MODE = 2;
  private static final int HALT_MODE_SHUTDOWN = 0;
  static final long MIN_LONG_WAKE_CHECK_INTERVAL = 60000L;
  private static final int MSG_CHECK_FOR_LONG_WAKELOCKS = 4;
  private static final int MSG_FORCESTOP = 5;
  private static final int MSG_SANDMAN = 2;
  private static final int MSG_SCREEN_BRIGHTNESS_BOOST_TIMEOUT = 3;
  private static final int MSG_USER_ACTIVITY_TIMEOUT = 1;
  private static String PACKAGEMANAGERMENT_CONFIG_NAME;
  private static final int POWER_FEATURE_DOUBLE_TAP_TO_WAKE = 1;
  private static final int POWER_HINT_LOW_POWER = 5;
  private static final int POWER_HINT_VR_MODE = 7;
  private static final int SCREEN_BRIGHTNESS_BOOST_TIMEOUT = 5000;
  private static final int STATE_ACTIVE = 0;
  private static final int STATE_IDLE = 5;
  private static final int STATE_IDLE_MAINTENANCE = 6;
  private static final int STATE_INACTIVE = 1;
  private static final String TAG = "PowerManagerService";
  private static final int USER_ACTIVITY_BUTTON_BRIGHT = 8;
  private static final int USER_ACTIVITY_SCREEN_BRIGHT = 1;
  private static final int USER_ACTIVITY_SCREEN_DIM = 2;
  private static final int USER_ACTIVITY_SCREEN_DREAM = 4;
  private static final int WAKE_LOCK_BUTTON_BRIGHT = 8;
  private static final int WAKE_LOCK_CPU = 1;
  private static final int WAKE_LOCK_DOZE = 64;
  private static final int WAKE_LOCK_DRAW = 128;
  private static final int WAKE_LOCK_PROXIMITY_SCREEN_OFF = 16;
  private static final int WAKE_LOCK_SCREEN_BRIGHT = 2;
  private static final int WAKE_LOCK_SCREEN_DIM = 4;
  private static final int WAKE_LOCK_STAY_AWAKE = 32;
  public static int mBrightnessOverride;
  public static int mBrightnessOverrideAdj;
  public static boolean mDisplayStateOn;
  public static boolean mFirstSetScreenState;
  private static boolean mFirstSetWindowBrightness;
  public static float mManualAmbientLuxBackup;
  public static int mManualBrightness;
  public static int mManualBrightnessBackup;
  public static boolean mManualSetAutoBrightness;
  public static float mManulAtAmbientLux;
  private static int mMaximumPartialWakelockDurationConfig;
  private static PackageManager mPackageManager;
  private static int mScreenBrightnessSettingMaximum;
  private static int mScreenBrightnessSettingMinimum;
  private static boolean mSetAdj;
  public static boolean mUseAutoBrightness;
  public static int sBrightnessBoost;
  public static boolean sBrightnessNoAnimation;
  private ActivityManager mActivityManager;
  private IAppOpsService mAppOps;
  private Light mAttentionLight;
  private AudioManager mAudioManager;
  private boolean mAutoLowPowerModeConfigured;
  private boolean mAutoLowPowerModeSnoozing;
  private int mBatteryLevel;
  private boolean mBatteryLevelLow;
  private int mBatteryLevelWhenDreamStarted;
  private BatteryManagerInternal mBatteryManagerInternal;
  private IBatteryStats mBatteryStats;
  private boolean mBlockFingerprintSleep = false;
  private boolean mBootCompleted;
  private Runnable[] mBootCompletedRunnables;
  private boolean mBrightnessUseTwilight;
  private int mButtonBrightness = -1;
  private int mButtonBrightnessSettingDefault;
  private Light mButtonLight;
  private final Context mContext;
  private int mCriticalBatteryLevel = 0;
  private boolean mDecoupleHalAutoSuspendModeFromDisplayConfig;
  private boolean mDecoupleHalInteractiveModeFromDisplayConfig;
  private boolean mDeviceIdleAggressive;
  private boolean mDeviceIdleMode;
  private int mDeviceIdleState;
  int[] mDeviceIdleTempWhitelist = new int[0];
  int[] mDeviceIdleWhitelist = new int[0];
  protected int mDirty;
  private DisplayManagerInternal mDisplayManagerInternal;
  private final DisplayManagerInternal.DisplayPowerCallbacks mDisplayPowerCallbacks = new DisplayManagerInternal.DisplayPowerCallbacks()
  {
    private int mDisplayState = 0;
    
    public void acquireSuspendBlocker()
    {
      PowerManagerService.-get6(PowerManagerService.this).acquire();
    }
    
    public void onDisplayStateChange(int paramAnonymousInt)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        if (this.mDisplayState != paramAnonymousInt)
        {
          this.mDisplayState = paramAnonymousInt;
          if (paramAnonymousInt != 1) {
            break label67;
          }
          if (!PowerManagerService.-get5(PowerManagerService.this)) {
            PowerManagerService.-wrap28(PowerManagerService.this, false);
          }
          if (!PowerManagerService.-get4(PowerManagerService.this)) {
            PowerManagerService.-wrap27(PowerManagerService.this, true);
          }
        }
        label67:
        do
        {
          return;
          if (!PowerManagerService.-get4(PowerManagerService.this)) {
            PowerManagerService.-wrap27(PowerManagerService.this, false);
          }
        } while (PowerManagerService.-get5(PowerManagerService.this));
        PowerManagerService.-wrap28(PowerManagerService.this, true);
      }
    }
    
    public void onProximityNegative()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        Slog.i("PowerManagerService", "onProximityNegative");
        PowerManagerService.-set4(PowerManagerService.this, false);
        PowerManagerService localPowerManagerService = PowerManagerService.this;
        localPowerManagerService.mDirty |= 0x200;
        PowerManagerService.-wrap6(PowerManagerService.this, SystemClock.uptimeMillis(), 0, 0, 1000);
        PowerManagerService.this.updatePowerStateLocked();
        return;
      }
    }
    
    public void onProximityNegativeForceSuspend()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        Slog.i("PowerManagerService", "onProximityNegativeForceSuspend");
        PowerManagerService.-set4(PowerManagerService.this, false);
        PowerManagerService.-wrap37(PowerManagerService.this, SystemClock.uptimeMillis(), "wakeUp", 1000, "com.android.incallui", 1000);
        return;
      }
    }
    
    public void onProximityPositive()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        Slog.i("PowerManagerService", "onProximityPositive");
        PowerManagerService.-set4(PowerManagerService.this, true);
        PowerManagerService localPowerManagerService = PowerManagerService.this;
        localPowerManagerService.mDirty |= 0x200;
        PowerManagerService.this.updatePowerStateLocked();
        return;
      }
    }
    
    public void onProximityPositiveForceSuspend()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        Slog.i("PowerManagerService", "onProximityPositiveForceSuspend");
        PowerManagerService.-set4(PowerManagerService.this, true);
        PowerManagerService.-wrap11(PowerManagerService.this, SystemClock.uptimeMillis(), 7, 0, 1000);
        return;
      }
    }
    
    public void onStateChanged()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService localPowerManagerService = PowerManagerService.this;
        localPowerManagerService.mDirty |= 0x8;
        PowerManagerService.this.updatePowerStateLocked();
        return;
      }
    }
    
    public void releaseSuspendBlocker()
    {
      PowerManagerService.-get6(PowerManagerService.this).release();
    }
    
    public String toString()
    {
      try
      {
        String str = "state=" + Display.stateToString(this.mDisplayState);
        return str;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void unblockScreenOn()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-set0(PowerManagerService.this, false);
        return;
      }
    }
  };
  private final DisplayManagerInternal.DisplayPowerRequest mDisplayPowerRequest = new DisplayManagerInternal.DisplayPowerRequest();
  private boolean mDisplayReady;
  private final SuspendBlocker mDisplaySuspendBlocker;
  private int mDockState = 0;
  private boolean mDoubleTapWakeEnabled;
  private boolean mDozeAfterScreenOffConfig;
  private ConfigObserver mDozeBlackForAudioConfigObserver;
  private List<String> mDozeBlackForAudioList;
  private int mDozeScreenBrightnessOverrideFromDreamManager = -1;
  private int mDozeScreenStateOverrideFromDreamManager = 0;
  private DreamManagerInternal mDreamManager;
  private boolean mDreamsActivateOnDockSetting;
  private boolean mDreamsActivateOnSleepSetting;
  private boolean mDreamsActivatedOnDockByDefaultConfig;
  private boolean mDreamsActivatedOnSleepByDefaultConfig;
  private int mDreamsBatteryLevelDrainCutoffConfig;
  private int mDreamsBatteryLevelMinimumWhenNotPoweredConfig;
  private int mDreamsBatteryLevelMinimumWhenPoweredConfig;
  private boolean mDreamsEnabledByDefaultConfig;
  private boolean mDreamsEnabledOnBatteryConfig;
  private boolean mDreamsEnabledSetting;
  private boolean mDreamsSupportedConfig;
  private boolean mHalAutoSuspendModeEnabled;
  private boolean mHalInteractiveModeEnabled;
  private final PowerManagerHandler mHandler;
  private final ServiceThread mHandlerThread;
  private boolean mHoldingDisplaySuspendBlocker;
  private boolean mHoldingWakeLockSuspendBlocker;
  private boolean mIsPowered;
  private long mLastInteractivePowerHintTime;
  private long mLastScreenBrightnessBoostTime;
  private long mLastSleepTime;
  private long mLastUserActivityButtonTime;
  private long mLastUserActivityTime;
  private long mLastUserActivityTimeNoChangeLights;
  private long mLastWakeTime;
  private long mLastWarningAboutUserActivityPermission = Long.MIN_VALUE;
  private boolean mLightDeviceIdleMode;
  private List<String> mLightIdleBlackList = Arrays.asList(new String[] { "mobi.andrutil.cm.MnMsgInstIdService" });
  private LightsManager mLightsManager;
  private final Object mLock = new Object();
  private boolean mLowPowerModeEnabled;
  private final ArrayList<PowerManagerInternal.LowPowerModeListener> mLowPowerModeListeners = new ArrayList();
  private boolean mLowPowerModeSetting;
  private int mMaximumScreenDimDurationConfig;
  private float mMaximumScreenDimRatioConfig;
  private int mMaximumScreenOffTimeoutFromDeviceAdmin = Integer.MAX_VALUE;
  private int mMinimumScreenOffTimeoutConfig;
  private Notifier mNotifier;
  private long mNotifyLongDispatched;
  private long mNotifyLongNextCheck;
  private long mNotifyLongScheduled;
  private long mOverriddenTimeout = -1L;
  private int mPlugType;
  private WindowManagerPolicy mPolicy;
  private boolean mProximityLockFromInCallUi = false;
  private boolean mProximityPositive;
  private boolean mRequestWaitForNegativeProximity;
  private boolean mSandmanScheduled;
  private boolean mSandmanSummoned;
  private float mScreenAutoBrightnessAdjustmentSetting;
  private boolean mScreenBrightnessBoostInProgress;
  private int mScreenBrightnessModeSetting;
  private int mScreenBrightnessOverrideFromWindowManager = -1;
  private int mScreenBrightnessSetting;
  private int mScreenBrightnessSettingDefault;
  private int mScreenOffTimeoutSetting;
  private SettingsObserver mSettingsObserver;
  private int mSleepTimeoutSetting;
  private boolean mStayOn;
  private int mStayOnWhilePluggedInSetting;
  private boolean mSupportsDoubleTapWakeConfig;
  private final ArrayList<SuspendBlocker> mSuspendBlockers = new ArrayList();
  private boolean mSuspendWhenScreenOffDueToProximityConfig;
  private boolean mSystemReady;
  private float mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = NaN.0F;
  private int mTemporaryScreenBrightnessSettingOverride = -1;
  private boolean mTheaterModeEnabled;
  private final SparseIntArray mUidState = new SparseIntArray();
  private int mUserActivitySummary;
  private long mUserActivityTimeoutOverrideFromWindowManager = -1L;
  private boolean mUserInactiveOverrideFromWindowManager;
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(boolean paramAnonymousBoolean)
    {
      PowerManagerService localPowerManagerService = PowerManagerService.this;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        PowerManagerService.-wrap21(localPowerManagerService, 7, i);
        return;
      }
    }
  };
  private int mWakeLockSummary;
  private final SuspendBlocker mWakeLockSuspendBlocker;
  protected final ArrayList<WakeLock> mWakeLocks = new ArrayList();
  private boolean mWakeUpWhenPluggedOrUnpluggedConfig;
  private boolean mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig;
  private int mWakefulness;
  private boolean mWakefulnessChanging;
  private WirelessChargerDetector mWirelessChargerDetector;
  private QCNsrmPowerExtension qcNsrmPowExt;
  private boolean useProximityForceSuspend = false;
  
  static
  {
    DEBUG = false;
    if (DEBUG) {}
    for (boolean bool = true;; bool = false)
    {
      DEBUG_SPEW = bool;
      sBrightnessBoost = 0;
      sBrightnessNoAnimation = false;
      mUseAutoBrightness = false;
      mManualSetAutoBrightness = false;
      mManualBrightnessBackup = 0;
      mManualAmbientLuxBackup = 0.0F;
      mManualBrightness = 0;
      mManulAtAmbientLux = 0.0F;
      mDisplayStateOn = false;
      mFirstSetScreenState = true;
      mSetAdj = true;
      mBrightnessOverride = 0;
      mBrightnessOverrideAdj = 0;
      mFirstSetWindowBrightness = true;
      PACKAGEMANAGERMENT_CONFIG_NAME = "ProcessManagement";
      mPackageManager = null;
      return;
    }
  }
  
  public PowerManagerService(Context arg1)
  {
    super(???);
    this.mContext = ???;
    this.mDeviceIdleState = 0;
    this.mHandlerThread = new ServiceThread("PowerManagerService", -4, false);
    this.mHandlerThread.start();
    this.mHandler = new PowerManagerHandler(this.mHandlerThread.getLooper());
    this.qcNsrmPowExt = new QCNsrmPowerExtension(this);
    synchronized (this.mLock)
    {
      this.mWakeLockSuspendBlocker = createSuspendBlockerLocked("PowerManagerService.WakeLocks");
      this.mDisplaySuspendBlocker = createSuspendBlockerLocked("PowerManagerService.Display");
      this.mDisplaySuspendBlocker.acquire();
      this.mHoldingDisplaySuspendBlocker = true;
      this.mHalAutoSuspendModeEnabled = false;
      this.mHalInteractiveModeEnabled = true;
      this.mWakefulness = 1;
      nativeInit();
      nativeSetAutoSuspend(false);
      nativeSetInteractive(true);
      nativeSetFeature(1, 0);
      return;
    }
  }
  
  private void acquireWakeLockInternal(IBinder paramIBinder, int paramInt1, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3, int paramInt2, int paramInt3)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if ((DEBUG_SPEW) || (DEBUG_ONEPLUS)) {
          Slog.d("PowerManagerService", "acquireWakeLockInternal: lock=" + Objects.hashCode(paramIBinder) + ", flags=0x" + Integer.toHexString(paramInt1) + ", tag=\"" + paramString1 + "\", ws=" + paramWorkSource + ", uid=" + paramInt2 + ", pid=" + paramInt3);
        }
        int i = findWakeLockIndexLocked(paramIBinder);
        if (i >= 0)
        {
          paramIBinder = (WakeLock)this.mWakeLocks.get(i);
          if (!paramIBinder.hasSameProperties(paramInt1, paramString1, paramWorkSource, paramInt2, paramInt3))
          {
            notifyWakeLockChangingLocked(paramIBinder, paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramWorkSource, paramString3);
            paramIBinder.updateProperties(paramInt1, paramString1, paramString2, paramWorkSource, paramString3, paramInt2, paramInt3);
            break label306;
            applyWakeLockFlagsOnAcquireLocked(paramIBinder, paramInt2);
            this.mDirty |= 0x1;
            updatePowerStateLocked();
            if (paramInt1 != 0) {
              notifyWakeLockAcquiredLocked(paramIBinder);
            }
          }
        }
        else
        {
          paramString2 = new WakeLock(paramIBinder, paramInt1, paramString1, paramString2, paramWorkSource, paramString3, paramInt2, paramInt3);
          try
          {
            paramIBinder.linkToDeath(paramString2, 0);
            this.mWakeLocks.add(paramString2);
            setWakeLockDisabledStateLocked(paramString2);
            this.qcNsrmPowExt.checkPmsBlockedWakelocks(paramInt2, paramInt3, paramInt1, paramString1, paramString2);
            paramInt1 = 1;
            paramIBinder = paramString2;
          }
          catch (RemoteException paramIBinder)
          {
            throw new IllegalArgumentException("Wake lock is already dead.");
          }
        }
      }
      label306:
      paramInt1 = 0;
    }
  }
  
  private boolean allowAcquireWakeLock(WakeLock paramWakeLock)
  {
    String[] arrayOfString = getActiveAudioUids();
    if (DEBUG) {
      Slog.d("PowerManagerService", "ActiveAudioUid = " + Arrays.toString(arrayOfString));
    }
    if (arrayOfString != null)
    {
      int i = 0;
      if (i < arrayOfString.length)
      {
        if (arrayOfString[i].isEmpty()) {}
        for (;;)
        {
          i += 1;
          break;
          int k = Integer.valueOf(arrayOfString[i]).intValue();
          if (k == 0) {
            return false;
          }
          if (DEBUG) {
            Slog.d("PowerManagerService", "WakeLock owner uid: " + paramWakeLock.mOwnerUid);
          }
          if (k == paramWakeLock.mOwnerUid) {
            return true;
          }
          if (paramWakeLock.mWorkSource != null)
          {
            int m = paramWakeLock.mWorkSource.size();
            int j = 0;
            while (j < m)
            {
              if (paramWakeLock.mWorkSource.get(j) == k) {
                return true;
              }
              j += 1;
            }
          }
        }
      }
    }
    return false;
  }
  
  private void applyWakeLockFlagsOnAcquireLocked(WakeLock paramWakeLock, int paramInt)
  {
    String str;
    if (((paramWakeLock.mFlags & 0x10000000) != 0) && (isScreenLock(paramWakeLock)))
    {
      if ((this.useProximityForceSuspend) && (this.mProximityPositive))
      {
        Slog.i("PowerManagerService", "wakeLock : " + paramWakeLock.mTag + ", lock = " + Objects.hashCode(paramWakeLock.mLock) + " try to wakeup device while proximity positive");
        userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 1, paramInt);
        return;
      }
      if ((paramWakeLock.mWorkSource == null) || (paramWakeLock.mWorkSource.getName(0) == null)) {
        break label147;
      }
      str = paramWakeLock.mWorkSource.getName(0);
      paramInt = paramWakeLock.mWorkSource.get(0);
    }
    for (;;)
    {
      wakeUpNoUpdateLocked(SystemClock.uptimeMillis(), paramWakeLock.mTag, paramInt, str, paramInt);
      return;
      label147:
      str = paramWakeLock.mPackageName;
      if (paramWakeLock.mWorkSource != null) {
        paramInt = paramWakeLock.mWorkSource.get(0);
      } else {
        paramInt = paramWakeLock.mOwnerUid;
      }
    }
  }
  
  private void applyWakeLockFlagsOnReleaseLocked(WakeLock paramWakeLock)
  {
    if (((paramWakeLock.mFlags & 0x20000000) != 0) && (isScreenLock(paramWakeLock))) {
      userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 1, paramWakeLock.mOwnerUid);
    }
  }
  
  private boolean blackPackageForAudio(int paramInt)
  {
    String str = getPackageNameForUid(paramInt);
    if (this.mDozeBlackForAudioList.contains(str))
    {
      Log.e("PowerManagerService", " blackPackageForAudio  packageName = " + str);
      return true;
    }
    return false;
  }
  
  private void boostScreenBrightnessInternal(long paramLong, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mSystemReady)
      {
        int i = this.mWakefulness;
        if (i != 0) {
          break label31;
        }
      }
      label31:
      while (paramLong < this.mLastScreenBrightnessBoostTime) {
        return;
      }
      Slog.i("PowerManagerService", "Brightness boost activated (uid " + paramInt + ")...");
      this.mLastScreenBrightnessBoostTime = paramLong;
      if (!this.mScreenBrightnessBoostInProgress)
      {
        this.mScreenBrightnessBoostInProgress = true;
        this.mNotifier.onScreenBrightnessBoostChanged();
      }
      this.mDirty |= 0x800;
      userActivityNoUpdateLocked(paramLong, 0, 0, paramInt);
      updatePowerStateLocked();
      return;
    }
  }
  
  private boolean canDozeLocked()
  {
    return this.mWakefulness == 3;
  }
  
  private boolean canDreamLocked()
  {
    if (DEBUG_SPEW) {
      Slog.i("PowerManagerService", "canDreamLocked mWakefulness = " + this.mWakefulness + ", mDreamsSupportedConfig = " + this.mDreamsSupportedConfig + ", mDreamsEnabledSetting = " + this.mDreamsEnabledSetting + ", mDisplayPowerRequest.isBrightOrDim() = " + this.mDisplayPowerRequest.isBrightOrDim() + ", mUserActivitySummary = " + this.mUserActivitySummary + ", mBootCompleted = " + this.mBootCompleted);
    }
    if ((this.mWakefulness != 2) || (!this.mDreamsSupportedConfig) || (!this.mDreamsEnabledSetting) || (!this.mDisplayPowerRequest.isBrightOrDim()) || ((this.mUserActivitySummary & 0x7) == 0)) {}
    while (!this.mBootCompleted) {
      return false;
    }
    if (!isBeingKeptAwakeLocked())
    {
      if ((this.mIsPowered) || (this.mDreamsEnabledOnBatteryConfig))
      {
        if ((!this.mIsPowered) && (this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig >= 0) && (this.mBatteryLevel < this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig)) {
          return false;
        }
      }
      else {
        return false;
      }
      if ((this.mIsPowered) && (this.mDreamsBatteryLevelMinimumWhenPoweredConfig >= 0) && (this.mBatteryLevel < this.mDreamsBatteryLevelMinimumWhenPoweredConfig)) {
        return false;
      }
    }
    return true;
  }
  
  private static WorkSource copyWorkSource(WorkSource paramWorkSource)
  {
    WorkSource localWorkSource = null;
    if (paramWorkSource != null) {
      localWorkSource = new WorkSource(paramWorkSource);
    }
    return localWorkSource;
  }
  
  private void crashInternal(final String paramString)
  {
    paramString = new Thread("PowerManagerService.crash()")
    {
      public void run()
      {
        throw new RuntimeException(paramString);
      }
    };
    try
    {
      paramString.start();
      paramString.join();
      return;
    }
    catch (InterruptedException paramString)
    {
      Slog.wtf("PowerManagerService", paramString);
    }
  }
  
  private SuspendBlocker createSuspendBlockerLocked(String paramString)
  {
    paramString = new SuspendBlockerImpl(paramString);
    this.mSuspendBlockers.add(paramString);
    return paramString;
  }
  
  private void dumpInternal(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("POWER MANAGER (dumpsys power)\n");
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println("Power Manager State:");
        paramPrintWriter.println("  mDirty=0x" + Integer.toHexString(this.mDirty));
        paramPrintWriter.println("  mWakefulness=" + PowerManagerInternal.wakefulnessToString(this.mWakefulness));
        paramPrintWriter.println("  mWakefulnessChanging=" + this.mWakefulnessChanging);
        paramPrintWriter.println("  mIsPowered=" + this.mIsPowered);
        paramPrintWriter.println("  mPlugType=" + this.mPlugType);
        paramPrintWriter.println("  mBatteryLevel=" + this.mBatteryLevel);
        paramPrintWriter.println("  mBatteryLevelWhenDreamStarted=" + this.mBatteryLevelWhenDreamStarted);
        paramPrintWriter.println("  mDockState=" + this.mDockState);
        paramPrintWriter.println("  mStayOn=" + this.mStayOn);
        paramPrintWriter.println("  mProximityPositive=" + this.mProximityPositive);
        paramPrintWriter.println("  mBootCompleted=" + this.mBootCompleted);
        paramPrintWriter.println("  mSystemReady=" + this.mSystemReady);
        paramPrintWriter.println("  mHalAutoSuspendModeEnabled=" + this.mHalAutoSuspendModeEnabled);
        paramPrintWriter.println("  mHalInteractiveModeEnabled=" + this.mHalInteractiveModeEnabled);
        paramPrintWriter.println("  mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary));
        paramPrintWriter.print("  mNotifyLongScheduled=");
        if (this.mNotifyLongScheduled == 0L)
        {
          paramPrintWriter.print("(none)");
          paramPrintWriter.println();
          paramPrintWriter.print("  mNotifyLongDispatched=");
          if (this.mNotifyLongDispatched == 0L)
          {
            paramPrintWriter.print("(none)");
            paramPrintWriter.println();
            paramPrintWriter.print("  mNotifyLongNextCheck=");
            if (this.mNotifyLongNextCheck != 0L) {
              break label2463;
            }
            paramPrintWriter.print("(none)");
            paramPrintWriter.println();
            paramPrintWriter.println("  mUserActivitySummary=0x" + Integer.toHexString(this.mUserActivitySummary));
            paramPrintWriter.println("  mRequestWaitForNegativeProximity=" + this.mRequestWaitForNegativeProximity);
            paramPrintWriter.println("  mSandmanScheduled=" + this.mSandmanScheduled);
            paramPrintWriter.println("  mSandmanSummoned=" + this.mSandmanSummoned);
            paramPrintWriter.println("  mLowPowerModeEnabled=" + this.mLowPowerModeEnabled);
            paramPrintWriter.println("  mBatteryLevelLow=" + this.mBatteryLevelLow);
            paramPrintWriter.println("  mLightDeviceIdleMode=" + this.mLightDeviceIdleMode);
            paramPrintWriter.println("  mDeviceIdleMode=" + this.mDeviceIdleMode);
            paramPrintWriter.println("  mDeviceIdleWhitelist=" + Arrays.toString(this.mDeviceIdleWhitelist));
            paramPrintWriter.println("  mDeviceIdleTempWhitelist=" + Arrays.toString(this.mDeviceIdleTempWhitelist));
            paramPrintWriter.println("  mLastWakeTime=" + TimeUtils.formatUptime(this.mLastWakeTime));
            paramPrintWriter.println("  mLastSleepTime=" + TimeUtils.formatUptime(this.mLastSleepTime));
            paramPrintWriter.println("  mLastUserActivityTime=" + TimeUtils.formatUptime(this.mLastUserActivityTime));
            paramPrintWriter.println("  mLastUserActivityTimeNoChangeLights=" + TimeUtils.formatUptime(this.mLastUserActivityTimeNoChangeLights));
            paramPrintWriter.println("  mLastInteractivePowerHintTime=" + TimeUtils.formatUptime(this.mLastInteractivePowerHintTime));
            paramPrintWriter.println("  mLastScreenBrightnessBoostTime=" + TimeUtils.formatUptime(this.mLastScreenBrightnessBoostTime));
            paramPrintWriter.println("  mScreenBrightnessBoostInProgress=" + this.mScreenBrightnessBoostInProgress);
            paramPrintWriter.println("  mDisplayReady=" + this.mDisplayReady);
            paramPrintWriter.println("  mHoldingWakeLockSuspendBlocker=" + this.mHoldingWakeLockSuspendBlocker);
            paramPrintWriter.println("  mHoldingDisplaySuspendBlocker=" + this.mHoldingDisplaySuspendBlocker);
            paramPrintWriter.println();
            paramPrintWriter.println("Settings and Configuration:");
            paramPrintWriter.println("  mDecoupleHalAutoSuspendModeFromDisplayConfig=" + this.mDecoupleHalAutoSuspendModeFromDisplayConfig);
            paramPrintWriter.println("  mDecoupleHalInteractiveModeFromDisplayConfig=" + this.mDecoupleHalInteractiveModeFromDisplayConfig);
            paramPrintWriter.println("  mWakeUpWhenPluggedOrUnpluggedConfig=" + this.mWakeUpWhenPluggedOrUnpluggedConfig);
            paramPrintWriter.println("  mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig=" + this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
            paramPrintWriter.println("  mTheaterModeEnabled=" + this.mTheaterModeEnabled);
            paramPrintWriter.println("  mSuspendWhenScreenOffDueToProximityConfig=" + this.mSuspendWhenScreenOffDueToProximityConfig);
            paramPrintWriter.println("  mDreamsSupportedConfig=" + this.mDreamsSupportedConfig);
            paramPrintWriter.println("  mDreamsEnabledByDefaultConfig=" + this.mDreamsEnabledByDefaultConfig);
            paramPrintWriter.println("  mDreamsActivatedOnSleepByDefaultConfig=" + this.mDreamsActivatedOnSleepByDefaultConfig);
            paramPrintWriter.println("  mDreamsActivatedOnDockByDefaultConfig=" + this.mDreamsActivatedOnDockByDefaultConfig);
            paramPrintWriter.println("  mDreamsEnabledOnBatteryConfig=" + this.mDreamsEnabledOnBatteryConfig);
            paramPrintWriter.println("  mDreamsBatteryLevelMinimumWhenPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenPoweredConfig);
            paramPrintWriter.println("  mDreamsBatteryLevelMinimumWhenNotPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig);
            paramPrintWriter.println("  mDreamsBatteryLevelDrainCutoffConfig=" + this.mDreamsBatteryLevelDrainCutoffConfig);
            paramPrintWriter.println("  mDreamsEnabledSetting=" + this.mDreamsEnabledSetting);
            paramPrintWriter.println("  mDreamsActivateOnSleepSetting=" + this.mDreamsActivateOnSleepSetting);
            paramPrintWriter.println("  mDreamsActivateOnDockSetting=" + this.mDreamsActivateOnDockSetting);
            paramPrintWriter.println("  mDozeAfterScreenOffConfig=" + this.mDozeAfterScreenOffConfig);
            paramPrintWriter.println("  mLowPowerModeSetting=" + this.mLowPowerModeSetting);
            paramPrintWriter.println("  mAutoLowPowerModeConfigured=" + this.mAutoLowPowerModeConfigured);
            paramPrintWriter.println("  mAutoLowPowerModeSnoozing=" + this.mAutoLowPowerModeSnoozing);
            paramPrintWriter.println("  mMinimumScreenOffTimeoutConfig=" + this.mMinimumScreenOffTimeoutConfig);
            paramPrintWriter.println("  mMaximumScreenDimDurationConfig=" + this.mMaximumScreenDimDurationConfig);
            paramPrintWriter.println("  mMaximumScreenDimRatioConfig=" + this.mMaximumScreenDimRatioConfig);
            paramPrintWriter.println("  mScreenOffTimeoutSetting=" + this.mScreenOffTimeoutSetting);
            paramPrintWriter.println("  mSleepTimeoutSetting=" + this.mSleepTimeoutSetting);
            paramPrintWriter.println("  mMaximumScreenOffTimeoutFromDeviceAdmin=" + this.mMaximumScreenOffTimeoutFromDeviceAdmin + " (enforced=" + isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() + ")");
            paramPrintWriter.println("  mStayOnWhilePluggedInSetting=" + this.mStayOnWhilePluggedInSetting);
            paramPrintWriter.println("  mScreenBrightnessSetting=" + this.mScreenBrightnessSetting);
            paramPrintWriter.println("  mScreenAutoBrightnessAdjustmentSetting=" + this.mScreenAutoBrightnessAdjustmentSetting);
            paramPrintWriter.println("  mScreenBrightnessModeSetting=" + this.mScreenBrightnessModeSetting);
            paramPrintWriter.println("  mScreenBrightnessOverrideFromWindowManager=" + this.mScreenBrightnessOverrideFromWindowManager);
            paramPrintWriter.println("  mUserActivityTimeoutOverrideFromWindowManager=" + this.mUserActivityTimeoutOverrideFromWindowManager);
            paramPrintWriter.println("  mUserInactiveOverrideFromWindowManager=" + this.mUserInactiveOverrideFromWindowManager);
            paramPrintWriter.println("  mTemporaryScreenBrightnessSettingOverride=" + this.mTemporaryScreenBrightnessSettingOverride);
            paramPrintWriter.println("  mTemporaryScreenAutoBrightnessAdjustmentSettingOverride=" + this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride);
            paramPrintWriter.println("  mDozeScreenStateOverrideFromDreamManager=" + this.mDozeScreenStateOverrideFromDreamManager);
            paramPrintWriter.println("  mDozeScreenBrightnessOverrideFromDreamManager=" + this.mDozeScreenBrightnessOverrideFromDreamManager);
            paramPrintWriter.println("  mScreenBrightnessSettingMinimum=" + mScreenBrightnessSettingMinimum);
            paramPrintWriter.println("  mScreenBrightnessSettingMaximum=" + mScreenBrightnessSettingMaximum);
            paramPrintWriter.println("  mScreenBrightnessSettingDefault=" + this.mScreenBrightnessSettingDefault);
            paramPrintWriter.println("  mDoubleTapWakeEnabled=" + this.mDoubleTapWakeEnabled);
            int i = getSleepTimeoutLocked();
            int j = getScreenOffTimeoutLocked(i);
            int k = getScreenDimDurationLocked(j);
            paramPrintWriter.println();
            paramPrintWriter.println("Sleep timeout: " + i + " ms");
            paramPrintWriter.println("Screen off timeout: " + j + " ms");
            paramPrintWriter.println("Screen dim duration: " + k + " ms");
            paramPrintWriter.println();
            paramPrintWriter.println("UID states:");
            i = 0;
            if (i >= this.mUidState.size()) {
              break;
            }
            paramPrintWriter.print("  UID ");
            UserHandle.formatUid(paramPrintWriter, this.mUidState.keyAt(i));
            paramPrintWriter.print(": ");
            paramPrintWriter.println(this.mUidState.valueAt(i));
            i += 1;
            continue;
          }
        }
        else
        {
          TimeUtils.formatDuration(this.mNotifyLongScheduled, SystemClock.uptimeMillis(), paramPrintWriter);
        }
      }
      TimeUtils.formatDuration(this.mNotifyLongDispatched, SystemClock.uptimeMillis(), paramPrintWriter);
      continue;
      label2463:
      TimeUtils.formatDuration(this.mNotifyLongNextCheck, SystemClock.uptimeMillis(), paramPrintWriter);
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Looper state:");
    this.mHandler.getLooper().dump(new PrintWriterPrinter(paramPrintWriter), "  ");
    paramPrintWriter.println();
    paramPrintWriter.println("Wake Locks: size=" + this.mWakeLocks.size());
    Object localObject2 = this.mWakeLocks.iterator();
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (WakeLock)((Iterator)localObject2).next();
      paramPrintWriter.println("  " + localObject3);
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Suspend Blockers: size=" + this.mSuspendBlockers.size());
    localObject2 = this.mSuspendBlockers.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (SuspendBlocker)((Iterator)localObject2).next();
      paramPrintWriter.println("  " + localObject3);
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Display Power: " + this.mDisplayPowerCallbacks);
    localObject2 = this.mWirelessChargerDetector;
    if (localObject2 != null) {
      ((WirelessChargerDetector)localObject2).dump(paramPrintWriter);
    }
  }
  
  private void enqueueNotifyLongMsgLocked(long paramLong)
  {
    this.mNotifyLongScheduled = paramLong;
    Message localMessage = this.mHandler.obtainMessage(4);
    localMessage.setAsynchronous(true);
    this.mHandler.sendMessageAtTime(localMessage, paramLong);
  }
  
  private int findWakeLockIndexLocked(IBinder paramIBinder)
  {
    int j = this.mWakeLocks.size();
    int i = 0;
    while (i < j)
    {
      if (((WakeLock)this.mWakeLocks.get(i)).mLock == paramIBinder) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private void finishWakefulnessChangeIfNeededLocked()
  {
    if ((this.mWakefulnessChanging) && (this.mDisplayReady))
    {
      if ((this.mWakefulness == 3) && ((this.mWakeLockSummary & 0x40) == 0)) {
        return;
      }
      if ((this.mWakefulness == 3) || (this.mWakefulness == 0)) {
        logSleepTimeoutRecapturedLocked();
      }
      this.mWakefulnessChanging = false;
      this.mNotifier.onWakefulnessChangeFinished();
    }
  }
  
  private boolean forceStopPackageName(int paramInt)
  {
    if (this.mActivityManager == null) {
      this.mActivityManager = ((ActivityManager)this.mContext.getSystemService("activity"));
    }
    String str = getPackageNameForUid(paramInt);
    if ((this.mActivityManager == null) || (str.equals(""))) {}
    for (;;)
    {
      return false;
      if (!str.equals("com.gaana"))
      {
        Log.e("PowerManagerService", " forceStopPackageName  packageName = " + str + " for audio");
        Message localMessage = this.mHandler.obtainMessage(5);
        localMessage.obj = str;
        localMessage.setAsynchronous(true);
        this.mHandler.sendMessageDelayed(localMessage, 0L);
      }
    }
  }
  
  private String[] getActiveAudioUids()
  {
    if (this.mAudioManager == null) {
      this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
    }
    if (this.mAudioManager != null) {}
    for (String str = this.mAudioManager.getParameters("get_uid");; str = ":0") {
      return parseActiveAudioUidsStr(str);
    }
  }
  
  private int getDesiredScreenPolicyLocked()
  {
    if (this.mWakefulness == 0) {
      return 0;
    }
    if (this.mWakefulness == 3)
    {
      if ((this.mWakeLockSummary & 0x40) != 0) {
        return 1;
      }
      if (this.mDozeAfterScreenOffConfig) {
        return 0;
      }
    }
    if (((this.mWakeLockSummary & 0x2) != 0) || ((this.mUserActivitySummary & 0x1) != 0)) {}
    while ((!this.mBootCompleted) || (this.mScreenBrightnessBoostInProgress)) {
      return 3;
    }
    return 2;
  }
  
  private String getPackageNameForUid(int paramInt)
  {
    if (mPackageManager == null) {
      mPackageManager = this.mContext.getPackageManager();
    }
    if (mPackageManager != null)
    {
      Object localObject = mPackageManager.getPackagesForUid(paramInt);
      if (localObject != null)
      {
        paramInt = 0;
        while (paramInt < localObject.length)
        {
          try
          {
            ApplicationInfo localApplicationInfo = mPackageManager.getApplicationInfo(localObject[paramInt], 0);
            if ((localApplicationInfo != null) && ((localApplicationInfo.flags & 0x1) == 0))
            {
              localObject = localObject[paramInt];
              return (String)localObject;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            return "";
          }
          paramInt += 1;
        }
      }
    }
    return "";
  }
  
  private int getScreenDimDurationLocked(int paramInt)
  {
    return Math.min(this.mMaximumScreenDimDurationConfig, (int)(paramInt * this.mMaximumScreenDimRatioConfig));
  }
  
  private int getScreenOffTimeoutLocked(int paramInt)
  {
    int j = this.mScreenOffTimeoutSetting;
    int i = j;
    if (isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
      i = Math.min(j, this.mMaximumScreenOffTimeoutFromDeviceAdmin);
    }
    j = i;
    if (this.mUserActivityTimeoutOverrideFromWindowManager >= 0L) {
      j = (int)Math.min(i, this.mUserActivityTimeoutOverrideFromWindowManager);
    }
    i = j;
    if (paramInt >= 0) {
      i = Math.min(j, paramInt);
    }
    return Math.max(i, this.mMinimumScreenOffTimeoutConfig);
  }
  
  private int getSleepTimeoutLocked()
  {
    int i = this.mSleepTimeoutSetting;
    if (i <= 0) {
      return -1;
    }
    return Math.max(i, this.mMinimumScreenOffTimeoutConfig);
  }
  
  private void goToSleepInternal(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mLock)
    {
      this.mDisplayManagerInternal.setWakingupReason("");
      if (goToSleepNoUpdateLocked(paramLong, paramInt1, paramInt2, paramInt3)) {
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private boolean goToSleepNoUpdateLocked(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    int j;
    if (DEBUG)
    {
      StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
      i = 0;
      j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Slog.d("PowerManagerService", " \t|----" + localStackTraceElement.toString());
        i += 1;
      }
    }
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "goToSleepNoUpdateLocked: eventTime=" + paramLong + ", reason=" + paramInt1 + ", flags=" + paramInt2 + ", uid=" + paramInt3);
    }
    if ((this.mBlockFingerprintSleep) && (paramInt1 == 11))
    {
      if (!DEBUG_SPEW) {}
      Slog.d("PowerManagerService", "drop fingerprint's sleep");
      return false;
    }
    if ((paramLong < this.mLastWakeTime) || (this.mWakefulness == 0)) {}
    while ((this.mWakefulness == 3) || (!this.mBootCompleted) || (!this.mSystemReady)) {
      return false;
    }
    this.mBlockFingerprintSleep = false;
    Trace.traceBegin(131072L, "goToSleep");
    switch (paramInt1)
    {
    }
    for (;;)
    {
      try
      {
        Slog.i("PowerManagerService", "Going to sleep by application request (uid " + paramInt3 + ")...");
        paramInt1 = 0;
        this.mLastSleepTime = paramLong;
        this.mSandmanSummoned = true;
        setWakefulnessLocked(3, paramInt1);
        i = 0;
        j = this.mWakeLocks.size();
        paramInt1 = 0;
        if (paramInt1 >= j) {
          break;
        }
        switch (((WakeLock)this.mWakeLocks.get(paramInt1)).mFlags & 0xFFFF)
        {
        default: 
          Slog.i("PowerManagerService", "Going to sleep due to device administration policy (uid " + paramInt3 + ")...");
          continue;
          Slog.i("PowerManagerService", "Going to sleep due to screen timeout (uid " + paramInt3 + ")...");
        }
      }
      finally
      {
        Trace.traceEnd(131072L);
      }
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to lid switch (uid " + paramInt3 + ")...");
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to power button (uid " + paramInt3 + ")...");
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to sleep button (uid " + paramInt3 + ")...");
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to HDMI standby (uid " + paramInt3 + ")...");
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to proximity (uid " + paramInt3 + ")...");
      continue;
      Slog.i("PowerManagerService", "Going to sleep due to fingerprint (uid " + paramInt3 + ")...");
    }
    EventLog.writeEvent(2724, i);
    if ((paramInt2 & 0x1) != 0) {
      reallyGoToSleepNoUpdateLocked(paramLong, paramInt3);
    }
    Trace.traceEnd(131072L);
    return true;
    for (;;)
    {
      paramInt1 += 1;
      break;
      i += 1;
    }
  }
  
  private void handleBatteryStateChangedLocked()
  {
    this.mDirty |= 0x100;
    updatePowerStateLocked();
  }
  
  private void handleSandman()
  {
    boolean bool2 = true;
    int i;
    boolean bool1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mSandmanScheduled = false;
        i = this.mWakefulness;
        if ((this.mSandmanSummoned) && (this.mDisplayReady)) {
          if (!canDreamLocked())
          {
            bool1 = canDozeLocked();
            if (DEBUG_SPEW) {
              Slog.i("PowerManagerService", "handleSandman startDreaming = " + bool1);
            }
            this.mSandmanSummoned = false;
            label85:
            if (this.mDreamManager == null) {
              break label219;
            }
            if (bool1)
            {
              this.mDreamManager.stopDream(true);
              ??? = this.mDreamManager;
              if (i != 3) {
                break label213;
              }
              label118:
              ((DreamManagerInternal)???).startDream(bool2);
            }
            bool2 = this.mDreamManager.isDreaming();
            label134:
            ??? = this.mLock;
            if ((!bool1) || (!bool2)) {}
          }
        }
      }
      try
      {
        this.mBatteryLevelWhenDreamStarted = this.mBatteryLevel;
        if (i == 3) {
          Slog.i("PowerManagerService", "Dozing...");
        }
        for (;;)
        {
          if (!this.mSandmanSummoned)
          {
            int j = this.mWakefulness;
            if (j == i) {
              break label245;
            }
          }
          return;
          bool1 = true;
          break;
          bool1 = false;
          break label85;
          localObject2 = finally;
          throw ((Throwable)localObject2);
          label213:
          bool2 = false;
          break label118;
          label219:
          bool2 = false;
          break label134;
          Slog.i("PowerManagerService", "Dreaming...");
        }
        if (i != 2) {
          break label457;
        }
      }
      finally {}
    }
    label245:
    if ((bool2) && (canDreamLocked()))
    {
      if ((this.mDreamsBatteryLevelDrainCutoffConfig >= 0) && (this.mBatteryLevel < this.mBatteryLevelWhenDreamStarted - this.mDreamsBatteryLevelDrainCutoffConfig))
      {
        bool1 = isBeingKeptAwakeLocked();
        if (!bool1) {}
      }
      else
      {
        return;
      }
      Slog.i("PowerManagerService", "Stopping dream because the battery appears to be draining faster than it is charging.  Battery level when dream started: " + this.mBatteryLevelWhenDreamStarted + "%.  " + "Battery level now: " + this.mBatteryLevel + "%.");
    }
    if (isItBedTimeYetLocked())
    {
      Slog.i("PowerManagerService", "handleSandman: Bed time and goToSleepNoUpdateLocked");
      goToSleepNoUpdateLocked(SystemClock.uptimeMillis(), 2, 0, 1000);
      updatePowerStateLocked();
    }
    for (;;)
    {
      if (bool2)
      {
        if (DEBUG_SPEW) {
          Slog.i("PowerManagerService", "handleSandman stopDream(false)");
        }
        this.mDreamManager.stopDream(false);
      }
      return;
      Slog.i("PowerManagerService", "handleSandman: time to wakeUpNoUpdateLocked");
      wakeUpNoUpdateLocked(SystemClock.uptimeMillis(), "android.server.power:DREAM", 1000, this.mContext.getOpPackageName(), 1000);
      updatePowerStateLocked();
      continue;
      label457:
      if (i == 3)
      {
        if (bool2) {
          return;
        }
        reallyGoToSleepNoUpdateLocked(SystemClock.uptimeMillis(), 1000);
        updatePowerStateLocked();
      }
    }
  }
  
  private void handleScreenBrightnessBoostTimeout()
  {
    synchronized (this.mLock)
    {
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "handleScreenBrightnessBoostTimeout");
      }
      this.mDirty |= 0x800;
      updatePowerStateLocked();
      return;
    }
  }
  
  private void handleSettingsChangedLocked()
  {
    updateSettingsLocked();
    updatePowerStateLocked();
  }
  
  private void handleUserActivityTimeout()
  {
    synchronized (this.mLock)
    {
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "handleUserActivityTimeout");
      }
      this.mDirty |= 0x4;
      updatePowerStateLocked();
      return;
    }
  }
  
  private void handleWakeLockDeath(WakeLock paramWakeLock)
  {
    synchronized (this.mLock)
    {
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "handleWakeLockDeath: lock=" + Objects.hashCode(paramWakeLock.mLock) + " [" + paramWakeLock.mTag + "]");
      }
      int i = this.mWakeLocks.indexOf(paramWakeLock);
      if (i < 0) {
        return;
      }
      removeWakeLockLocked(paramWakeLock, i);
      return;
    }
  }
  
  private void incrementBootCount()
  {
    synchronized (this.mLock)
    {
      try
      {
        i = Settings.Global.getInt(getContext().getContentResolver(), "boot_count");
        Settings.Global.putInt(getContext().getContentResolver(), "boot_count", i + 1);
        return;
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        for (;;)
        {
          int i = 0;
        }
      }
    }
  }
  
  private boolean isBeingKeptAwakeLocked()
  {
    if ((this.mStayOn) || ((this.mProximityPositive) && (!this.useProximityForceSuspend)) || ((this.mWakeLockSummary & 0x20) != 0)) {}
    while ((this.mUserActivitySummary & 0x3) != 0) {
      return true;
    }
    return this.mScreenBrightnessBoostInProgress;
  }
  
  private boolean isInteractiveInternal()
  {
    synchronized (this.mLock)
    {
      boolean bool = PowerManagerInternal.isInteractive(this.mWakefulness);
      return bool;
    }
  }
  
  private boolean isItBedTimeYetLocked()
  {
    return (this.mBootCompleted) && (!isBeingKeptAwakeLocked());
  }
  
  private boolean isLowPowerModeInternal()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mLowPowerModeEnabled;
      return bool;
    }
  }
  
  private boolean isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mMaximumScreenOffTimeoutFromDeviceAdmin >= 0)
    {
      bool1 = bool2;
      if (this.mMaximumScreenOffTimeoutFromDeviceAdmin < Integer.MAX_VALUE) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isScreenBrightnessBoostedInternal()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mScreenBrightnessBoostInProgress;
      return bool;
    }
  }
  
  private static boolean isScreenLock(WakeLock paramWakeLock)
  {
    switch (paramWakeLock.mFlags & 0xFFFF)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isValidAutoBrightnessAdjustment(float paramFloat)
  {
    if ((paramFloat >= mScreenBrightnessSettingMinimum) && (paramFloat <= mScreenBrightnessSettingMaximum)) {}
    while ((paramFloat == 500.0F) || (paramFloat == 300.0F)) {
      return true;
    }
    return false;
  }
  
  private static boolean isValidBrightness(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 255) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isWakeLockLevelSupportedInternal(int paramInt)
  {
    boolean bool = false;
    Object localObject1 = this.mLock;
    switch (paramInt)
    {
    default: 
      return false;
    case 1: 
    case 6: 
    case 10: 
    case 26: 
    case 64: 
    case 128: 
      return true;
    }
    try
    {
      if (this.mSystemReady) {
        bool = this.mDisplayManagerInternal.isProximitySensorAvailable();
      }
      return bool;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  private void logSleepTimeoutRecapturedLocked()
  {
    long l = SystemClock.uptimeMillis();
    l = this.mOverriddenTimeout - l;
    if (l >= 0L)
    {
      EventLog.writeEvent(2731, l);
      this.mOverriddenTimeout = -1L;
    }
  }
  
  public static void lowLevelReboot(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    if ((str.equals("recovery")) || (str.equals("recovery-update"))) {
      SystemProperties.set("sys.powerctl", "reboot,recovery");
    }
    try
    {
      for (;;)
      {
        Thread.sleep(20000L);
        Slog.wtf("PowerManagerService", "Unexpected return from lowLevelReboot!");
        return;
        SystemProperties.set("sys.powerctl", "reboot," + str);
      }
    }
    catch (InterruptedException paramString)
    {
      for (;;)
      {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public static void lowLevelShutdown(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    SystemProperties.set("sys.powerctl", "shutdown," + str);
  }
  
  private void napInternal(long paramLong, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (napNoUpdateLocked(paramLong, paramInt)) {
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private boolean napNoUpdateLocked(long paramLong, int paramInt)
  {
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "napNoUpdateLocked: eventTime=" + paramLong + ", uid=" + paramInt);
    }
    if ((paramLong < this.mLastWakeTime) || (this.mWakefulness != 1)) {}
    while ((!this.mBootCompleted) || (!this.mSystemReady)) {
      return false;
    }
    Trace.traceBegin(131072L, "nap");
    try
    {
      Slog.i("PowerManagerService", "Nap time (uid " + paramInt + ")...");
      this.mSandmanSummoned = true;
      setWakefulnessLocked(2, 0);
      return true;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  private static native void nativeAcquireSuspendBlocker(String paramString);
  
  private native void nativeInit();
  
  private static native void nativeReleaseSuspendBlocker(String paramString);
  
  private static native void nativeSendPowerHint(int paramInt1, int paramInt2);
  
  private static native void nativeSetAutoSuspend(boolean paramBoolean);
  
  private static native void nativeSetFeature(int paramInt1, int paramInt2);
  
  private static native void nativeSetInteractive(boolean paramBoolean);
  
  private boolean needDisplaySuspendBlockerLocked()
  {
    if (!this.mDisplayReady) {
      return true;
    }
    if ((!this.mDisplayPowerRequest.isBrightOrDim()) || ((this.mDisplayPowerRequest.useProximitySensor) && (this.mProximityPositive) && (this.mSuspendWhenScreenOffDueToProximityConfig)))
    {
      if (this.mScreenBrightnessBoostInProgress) {
        return true;
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  private void notifyWakeLockChangingLocked(WakeLock paramWakeLock, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, WorkSource paramWorkSource, String paramString3)
  {
    if ((this.mSystemReady) && (paramWakeLock.mNotifiedAcquired))
    {
      this.mNotifier.onWakeLockChanging(paramWakeLock.mFlags, paramWakeLock.mTag, paramWakeLock.mPackageName, paramWakeLock.mOwnerUid, paramWakeLock.mOwnerPid, paramWakeLock.mWorkSource, paramWakeLock.mHistoryTag, paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramWorkSource, paramString3);
      notifyWakeLockLongFinishedLocked(paramWakeLock);
      restartNofifyLongTimerLocked(paramWakeLock);
    }
  }
  
  private void notifyWakeLockLongFinishedLocked(WakeLock paramWakeLock)
  {
    if (paramWakeLock.mNotifiedLong)
    {
      paramWakeLock.mNotifiedLong = false;
      this.mNotifier.onLongPartialWakeLockFinish(paramWakeLock.mTag, paramWakeLock.mOwnerUid, paramWakeLock.mWorkSource, paramWakeLock.mHistoryTag);
    }
  }
  
  private void notifyWakeLockLongStartedLocked(WakeLock paramWakeLock)
  {
    if ((!this.mSystemReady) || (paramWakeLock.mDisabled)) {
      return;
    }
    paramWakeLock.mNotifiedLong = true;
    this.mNotifier.onLongPartialWakeLockStart(paramWakeLock.mTag, paramWakeLock.mOwnerUid, paramWakeLock.mWorkSource, paramWakeLock.mHistoryTag);
  }
  
  private String[] parseActiveAudioUidsStr(String paramString)
  {
    if (DEBUG) {
      Slog.d("PowerManagerService", "parseActiveAudioUidsStr():uids=" + paramString);
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    if (!paramString.contains(":")) {
      return null;
    }
    return paramString.split(":");
  }
  
  private void postAfterBootCompleted(Runnable paramRunnable)
  {
    if (this.mBootCompleted)
    {
      BackgroundThread.getHandler().post(paramRunnable);
      return;
    }
    Slog.d("PowerManagerService", "Delaying runnable until system is booted");
    this.mBootCompletedRunnables = ((Runnable[])ArrayUtils.appendElement(Runnable.class, this.mBootCompletedRunnables, paramRunnable));
  }
  
  private void powerHintInternal(int paramInt1, int paramInt2)
  {
    nativeSendPowerHint(paramInt1, paramInt2);
  }
  
  private void readConfigurationLocked()
  {
    Resources localResources = this.mContext.getResources();
    this.mDecoupleHalAutoSuspendModeFromDisplayConfig = localResources.getBoolean(17956981);
    this.mDecoupleHalInteractiveModeFromDisplayConfig = localResources.getBoolean(17956982);
    this.mWakeUpWhenPluggedOrUnpluggedConfig = localResources.getBoolean(17956902);
    this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig = localResources.getBoolean(17956907);
    this.mSuspendWhenScreenOffDueToProximityConfig = localResources.getBoolean(17956930);
    this.mDreamsSupportedConfig = localResources.getBoolean(17956974);
    this.mDreamsEnabledByDefaultConfig = localResources.getBoolean(17956975);
    this.mDreamsActivatedOnSleepByDefaultConfig = localResources.getBoolean(17956977);
    this.mDreamsActivatedOnDockByDefaultConfig = localResources.getBoolean(17956976);
    this.mDreamsEnabledOnBatteryConfig = localResources.getBoolean(17956978);
    this.mDreamsBatteryLevelMinimumWhenPoweredConfig = localResources.getInteger(17694856);
    this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig = localResources.getInteger(17694857);
    this.mDreamsBatteryLevelDrainCutoffConfig = localResources.getInteger(17694858);
    this.mDozeAfterScreenOffConfig = localResources.getBoolean(17956979);
    this.mMinimumScreenOffTimeoutConfig = localResources.getInteger(17694859);
    this.mMaximumScreenDimDurationConfig = localResources.getInteger(17694860);
    this.mMaximumScreenDimRatioConfig = localResources.getFraction(18022403, 1, 1);
    this.mSupportsDoubleTapWakeConfig = localResources.getBoolean(17957032);
    this.mCriticalBatteryLevel = this.mContext.getResources().getInteger(17694806);
    mMaximumPartialWakelockDurationConfig = localResources.getInteger(84475916);
  }
  
  private boolean reallyGoToSleepNoUpdateLocked(long paramLong, int paramInt)
  {
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "reallyGoToSleepNoUpdateLocked: eventTime=" + paramLong + ", uid=" + paramInt);
    }
    if ((paramLong < this.mLastWakeTime) || (this.mWakefulness == 0)) {}
    while ((!this.mBootCompleted) || (!this.mSystemReady)) {
      return false;
    }
    Trace.traceBegin(131072L, "reallyGoToSleep");
    try
    {
      Slog.i("PowerManagerService", "Sleeping (uid " + paramInt + ")...");
      setWakefulnessLocked(0, 2);
      return true;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  private void releaseWakeLockInternal(IBinder paramIBinder, int paramInt)
  {
    synchronized (this.mLock)
    {
      int i = findWakeLockIndexLocked(paramIBinder);
      if (i < 0)
      {
        if (DEBUG_SPEW) {
          Slog.d("PowerManagerService", "releaseWakeLockInternal: lock=" + Objects.hashCode(paramIBinder) + " [not found], flags=0x" + Integer.toHexString(paramInt));
        }
        return;
      }
      WakeLock localWakeLock = (WakeLock)this.mWakeLocks.get(i);
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "releaseWakeLockInternal: lock=" + Objects.hashCode(paramIBinder) + " [" + localWakeLock.mTag + "], flags=0x" + Integer.toHexString(paramInt));
      }
      if ((paramInt & 0x1) != 0) {
        this.mRequestWaitForNegativeProximity = true;
      }
      localWakeLock.mLock.unlinkToDeath(localWakeLock, 0);
      removeWakeLockLocked(localWakeLock, i);
      return;
    }
  }
  
  private void removeWakeLockLocked(WakeLock paramWakeLock, int paramInt)
  {
    this.mWakeLocks.remove(paramInt);
    notifyWakeLockReleasedLocked(paramWakeLock);
    applyWakeLockFlagsOnReleaseLocked(paramWakeLock);
    this.mDirty |= 0x1;
    updatePowerStateLocked();
  }
  
  private void resolveBlackForAudioConfigFromJSON(JSONArray paramJSONArray)
  {
    if (paramJSONArray == null)
    {
      Slog.v("PowerManagerService", "[OnlineConfig] DozeBlackForAudioConfigUpdater jsonArray ==null mDozeBlackForAudioList = " + this.mDozeBlackForAudioList);
      return;
    }
    int i = 0;
    for (;;)
    {
      try
      {
        if (i < paramJSONArray.length())
        {
          ??? = paramJSONArray.getJSONObject(i);
          if (!((JSONObject)???).getString("name").equals("config_oemBlackPackageForAudio")) {
            break label203;
          }
          JSONArray localJSONArray = ((JSONObject)???).getJSONArray("value");
          synchronized (this.mDozeBlackForAudioList)
          {
            this.mDozeBlackForAudioList.clear();
            int j = 0;
            if (j < localJSONArray.length())
            {
              this.mDozeBlackForAudioList.add(localJSONArray.getString(j));
              j += 1;
              continue;
            }
          }
        }
        Slog.v("PowerManagerService", "[OnlineConfig] DozeBlackForAudioConfigUpdater updated complete mDozeBlackForAudioList = " + this.mDozeBlackForAudioList);
      }
      catch (JSONException paramJSONArray)
      {
        Slog.e("PowerManagerService", "[OnlineConfig] resolveDozeBlackForAudioConfigFromJSON, error message:" + paramJSONArray.getMessage());
        return;
      }
      return;
      label203:
      i += 1;
    }
  }
  
  private void restartNofifyLongTimerLocked(WakeLock paramWakeLock)
  {
    paramWakeLock.mAcquireTime = SystemClock.uptimeMillis();
    if (((paramWakeLock.mFlags & 0xFFFF) == 1) && (this.mNotifyLongScheduled == 0L)) {
      enqueueNotifyLongMsgLocked(paramWakeLock.mAcquireTime + 60000L);
    }
  }
  
  private void scheduleSandmanLocked()
  {
    if (!this.mSandmanScheduled)
    {
      this.mSandmanScheduled = true;
      Message localMessage = this.mHandler.obtainMessage(2);
      localMessage.setAsynchronous(true);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  private void sendPowerSaverModeChangeTracker(String paramString, boolean paramBoolean)
  {
    OSTracker localOSTracker = new OSTracker(this.mContext);
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramString, Boolean.toString(paramBoolean));
    if ((localHashMap != null) && (localHashMap.size() > 0)) {
      localOSTracker.onEvent("PowerSaverModeTag", localHashMap);
    }
  }
  
  private void setAttentionLightInternal(boolean paramBoolean, int paramInt)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        boolean bool = this.mSystemReady;
        if (!bool) {
          return;
        }
        Light localLight = this.mAttentionLight;
        if (paramBoolean)
        {
          i = 3;
          localLight.setFlashing(paramInt, 2, i, 0);
          return;
        }
      }
      int i = 0;
    }
  }
  
  private void setDozeOverrideFromDreamManagerInternal(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      if ((this.mDozeScreenStateOverrideFromDreamManager != paramInt1) || (this.mDozeScreenBrightnessOverrideFromDreamManager != paramInt2))
      {
        this.mDozeScreenStateOverrideFromDreamManager = paramInt1;
        this.mDozeScreenBrightnessOverrideFromDreamManager = paramInt2;
        this.mDirty |= 0x20;
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private void setHalAutoSuspendModeLocked(boolean paramBoolean)
  {
    if (paramBoolean != this.mHalAutoSuspendModeEnabled)
    {
      if (DEBUG) {
        Slog.d("PowerManagerService", "Setting HAL auto-suspend mode to " + paramBoolean);
      }
      this.mHalAutoSuspendModeEnabled = paramBoolean;
      Trace.traceBegin(131072L, "setHalAutoSuspend(" + paramBoolean + ")");
    }
    try
    {
      nativeSetAutoSuspend(paramBoolean);
      return;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  private void setHalInteractiveModeLocked(boolean paramBoolean)
  {
    if (paramBoolean != this.mHalInteractiveModeEnabled)
    {
      if (DEBUG) {
        Slog.d("PowerManagerService", "Setting HAL interactive mode to " + paramBoolean);
      }
      this.mHalInteractiveModeEnabled = paramBoolean;
      Trace.traceBegin(131072L, "setHalInteractive(" + paramBoolean + ")");
    }
    try
    {
      nativeSetInteractive(paramBoolean);
      return;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  private boolean setLowPowerModeInternal(boolean paramBoolean)
  {
    int i = 0;
    sendPowerSaverModeChangeTracker("setpowersavemode", paramBoolean);
    synchronized (this.mLock)
    {
      Slog.i("PowerManagerService", "setLowPowerModeInternal " + paramBoolean + " mIsPowered=" + this.mIsPowered);
      boolean bool = this.mIsPowered;
      if (bool) {
        return false;
      }
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      if (paramBoolean) {
        i = 1;
      }
      Settings.Global.putInt(localContentResolver, "low_power", i);
      this.mLowPowerModeSetting = paramBoolean;
      if ((this.mAutoLowPowerModeConfigured) && ((this.mBatteryLevelLow) || (this.mBatteryLevel <= this.mCriticalBatteryLevel)))
      {
        if ((!paramBoolean) || (!this.mAutoLowPowerModeSnoozing)) {
          break label167;
        }
        if (DEBUG_SPEW) {
          Slog.d("PowerManagerService", "setLowPowerModeInternal: clearing low power mode snooze");
        }
        this.mAutoLowPowerModeSnoozing = false;
      }
      label167:
      while ((paramBoolean) || (this.mAutoLowPowerModeSnoozing))
      {
        updateLowPowerModeLocked();
        return true;
      }
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "setLowPowerModeInternal: snoozing low power mode");
      }
      this.mAutoLowPowerModeSnoozing = true;
    }
  }
  
  private void setScreenBrightnessOverrideFromWindowManagerInternal(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.mScreenBrightnessOverrideFromWindowManager != paramInt)
        {
          if (this.mScreenBrightnessModeSetting != 1) {
            break label144;
          }
          if (paramInt == -1)
          {
            mBrightnessOverride = 2;
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness", LightsService.mScreenBrightness, -2);
            mFirstSetWindowBrightness = true;
            this.mScreenBrightnessOverrideFromWindowManager = paramInt;
            if ((DEBUG) || (DEBUG_ONEPLUS)) {
              Slog.d("PowerManagerService", "mScreenBrightnessOverrideFromWindowManager = " + paramInt);
            }
            this.mDirty |= 0x20;
            updatePowerStateLocked();
          }
        }
        else
        {
          return;
        }
        mBrightnessOverride = 1;
        if (!mFirstSetWindowBrightness) {
          continue;
        }
        mBrightnessOverrideAdj = mManualBrightness;
        mFirstSetWindowBrightness = false;
      }
      label144:
      mBrightnessOverride = 0;
      mBrightnessOverrideAdj = 0;
    }
  }
  
  private void setTemporaryScreenAutoBrightnessAdjustmentSettingOverrideInternal(float paramFloat)
  {
    synchronized (this.mLock)
    {
      if (this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride != paramFloat)
      {
        this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = paramFloat;
        this.mDirty |= 0x20;
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private void setTemporaryScreenBrightnessSettingOverrideInternal(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mTemporaryScreenBrightnessSettingOverride != paramInt)
      {
        this.mTemporaryScreenBrightnessSettingOverride = paramInt;
        if (DEBUG) {
          Slog.d("PowerManagerService", "mTemporaryScreenBrightnessSettingOverride = " + paramInt);
        }
        this.mDirty |= 0x20;
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private void setUserActivityTimeoutOverrideFromWindowManagerInternal(long paramLong)
  {
    synchronized (this.mLock)
    {
      if (this.mUserActivityTimeoutOverrideFromWindowManager != paramLong)
      {
        if (DEBUG) {
          Slog.d("PowerManagerService", "UA TimeoutOverrideFromWindowManagerInternal = " + paramLong);
        }
        this.mUserActivityTimeoutOverrideFromWindowManager = paramLong;
        this.mDirty |= 0x20;
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private void setUserInactiveOverrideFromWindowManagerInternal()
  {
    synchronized (this.mLock)
    {
      this.mUserInactiveOverrideFromWindowManager = true;
      this.mDirty |= 0x4;
      updatePowerStateLocked();
      return;
    }
  }
  
  private boolean setWakeLockDisabledStateLocked(WakeLock paramWakeLock)
  {
    if ((paramWakeLock.mFlags & 0xFFFF) == 1)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      int i;
      int j;
      if (this.mLightDeviceIdleMode)
      {
        bool1 = bool2;
        if (paramWakeLock.mTag != null)
        {
          i = 0;
          bool1 = bool2;
          if (i < this.mLightIdleBlackList.size())
          {
            if (!paramWakeLock.mTag.contains((CharSequence)this.mLightIdleBlackList.get(i))) {
              break label402;
            }
            j = UserHandle.getAppId(paramWakeLock.mOwnerUid);
            if ((j < 10000) || (Arrays.binarySearch(this.mDeviceIdleWhitelist, j) >= 0) || (Arrays.binarySearch(this.mDeviceIdleTempWhitelist, j) >= 0) || (this.mUidState.get(paramWakeLock.mOwnerUid, 16) <= 4)) {
              break label402;
            }
            bool1 = true;
          }
        }
      }
      boolean bool3 = bool1;
      label261:
      int k;
      if (this.mDeviceIdleMode)
      {
        j = UserHandle.getAppId(paramWakeLock.mOwnerUid);
        if (paramWakeLock.mNotifiedLong) {
          paramWakeLock.mLongBeforeIdle = true;
        }
        if (!this.mDeviceIdleAggressive) {
          break label409;
        }
        bool2 = bool1;
        if (j >= 10000)
        {
          bool2 = bool1;
          if (Arrays.binarySearch(this.mDeviceIdleWhitelist, j) < 0)
          {
            bool2 = bool1;
            if (Arrays.binarySearch(this.mDeviceIdleTempWhitelist, j) < 0)
            {
              i = this.mUidState.get(paramWakeLock.mOwnerUid, 16);
              if (i > 4) {
                bool1 = true;
              }
              bool2 = bool1;
              if (i == 4)
              {
                bool2 = bool1;
                if (!allowAcquireWakeLock(paramWakeLock)) {
                  bool2 = true;
                }
              }
            }
          }
        }
        bool3 = bool2;
        if (!bool2) {
          if (paramWakeLock.mWorkSource != null)
          {
            k = paramWakeLock.mWorkSource.size();
            i = 0;
          }
        }
      }
      for (;;)
      {
        if (i < k)
        {
          int m = paramWakeLock.mWorkSource.get(i);
          if ((blackPackageForAudio(m)) && (allowAcquireWakeLock(paramWakeLock))) {
            forceStopPackageName(m);
          }
        }
        else
        {
          bool3 = bool2;
          if (paramWakeLock.mLongBeforeIdle)
          {
            bool3 = bool2;
            if (j >= 10000)
            {
              bool3 = bool2;
              if (!allowAcquireWakeLock(paramWakeLock)) {
                bool3 = true;
              }
            }
          }
          if ((this.mDeviceIdleState == 0) || (this.mDeviceIdleState == 1)) {
            paramWakeLock.mLongBeforeIdle = false;
          }
          if (paramWakeLock.mDisabled == bool3) {
            break label484;
          }
          paramWakeLock.mDisabled = bool3;
          return true;
          label402:
          i += 1;
          break;
          label409:
          bool2 = bool1;
          if (j < 10000) {
            break label261;
          }
          bool2 = bool1;
          if (Arrays.binarySearch(this.mDeviceIdleWhitelist, j) >= 0) {
            break label261;
          }
          bool2 = bool1;
          if (Arrays.binarySearch(this.mDeviceIdleTempWhitelist, j) >= 0) {
            break label261;
          }
          bool2 = bool1;
          if (this.mUidState.get(paramWakeLock.mOwnerUid, 16) <= 4) {
            break label261;
          }
          bool2 = true;
          break label261;
        }
        i += 1;
      }
    }
    label484:
    return false;
  }
  
  private void setWakefulnessLocked(int paramInt1, int paramInt2)
  {
    if (this.mWakefulness != paramInt1)
    {
      this.mWakefulness = paramInt1;
      this.mWakefulnessChanging = true;
      this.mDirty |= 0x2;
      this.mNotifier.onWakefulnessChangeStarted(paramInt1, paramInt2);
    }
  }
  
  private boolean shouldNapAtBedTimeLocked()
  {
    if (!this.mDreamsActivateOnSleepSetting)
    {
      if (!this.mDreamsActivateOnDockSetting) {}
    }
    else {
      return this.mDockState != 0;
    }
    return false;
  }
  
  private boolean shouldUseProximitySensorLocked()
  {
    boolean bool = false;
    if ((this.mWakeLockSummary & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean shouldWakeUpWhenPluggedOrUnpluggedLocked(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    if (!this.mWakeUpWhenPluggedOrUnpluggedConfig) {
      return false;
    }
    if ((!paramBoolean1) || (this.mIsPowered)) {}
    while ((paramBoolean1) || (!this.mIsPowered) || (this.mPlugType != 4) || (paramBoolean2))
    {
      if ((!this.mIsPowered) || (this.mWakefulness != 2)) {
        break label69;
      }
      return false;
      if (paramInt == 4) {
        return false;
      }
    }
    return false;
    label69:
    return (!this.mTheaterModeEnabled) || (this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
  }
  
  /* Error */
  private void shutdownOrRebootInternal(final int paramInt, final boolean paramBoolean1, final String paramString, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 708	com/android/server/power/PowerManagerService:mHandler	Lcom/android/server/power/PowerManagerService$PowerManagerHandler;
    //   4: ifnull +67 -> 71
    //   7: aload_0
    //   8: getfield 327	com/android/server/power/PowerManagerService:mSystemReady	Z
    //   11: ifeq +60 -> 71
    //   14: new 14	com/android/server/power/PowerManagerService$4
    //   17: dup
    //   18: aload_0
    //   19: iload_1
    //   20: iload_2
    //   21: aload_3
    //   22: invokespecial 2040	com/android/server/power/PowerManagerService$4:<init>	(Lcom/android/server/power/PowerManagerService;IZLjava/lang/String;)V
    //   25: astore_3
    //   26: aload_0
    //   27: getfield 708	com/android/server/power/PowerManagerService:mHandler	Lcom/android/server/power/PowerManagerService$PowerManagerHandler;
    //   30: aload_3
    //   31: invokestatic 2044	android/os/Message:obtain	(Landroid/os/Handler;Ljava/lang/Runnable;)Landroid/os/Message;
    //   34: astore 5
    //   36: aload 5
    //   38: iconst_1
    //   39: invokevirtual 1424	android/os/Message:setAsynchronous	(Z)V
    //   42: aload_0
    //   43: getfield 708	com/android/server/power/PowerManagerService:mHandler	Lcom/android/server/power/PowerManagerService$PowerManagerHandler;
    //   46: aload 5
    //   48: invokevirtual 1930	com/android/server/power/PowerManagerService$PowerManagerHandler:sendMessage	(Landroid/os/Message;)Z
    //   51: pop
    //   52: iload 4
    //   54: ifeq +35 -> 89
    //   57: aload_3
    //   58: monitorenter
    //   59: aload_3
    //   60: invokevirtual 2047	java/lang/Object:wait	()V
    //   63: goto -4 -> 59
    //   66: astore 5
    //   68: goto -9 -> 59
    //   71: new 2049	java/lang/IllegalStateException
    //   74: dup
    //   75: ldc_w 2051
    //   78: invokespecial 2052	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   81: athrow
    //   82: astore 5
    //   84: aload_3
    //   85: monitorexit
    //   86: aload 5
    //   88: athrow
    //   89: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	PowerManagerService
    //   0	90	1	paramInt	int
    //   0	90	2	paramBoolean1	boolean
    //   0	90	3	paramString	String
    //   0	90	4	paramBoolean2	boolean
    //   34	13	5	localMessage	Message
    //   66	1	5	localInterruptedException	InterruptedException
    //   82	5	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   59	63	66	java/lang/InterruptedException
    //   59	63	82	finally
  }
  
  private boolean updateDisplayPowerStateLocked(int paramInt)
  {
    boolean bool5 = this.mDisplayReady;
    boolean bool4;
    float f;
    boolean bool3;
    boolean bool1;
    boolean bool2;
    if ((paramInt & 0x83F) != 0)
    {
      this.mDisplayPowerRequest.policy = getDesiredScreenPolicyLocked();
      if (DEBUG) {
        Slog.d("PowerManagerService", "mDisplayPowerRequest.policy = " + this.mDisplayPowerRequest.policy);
      }
      this.useProximityForceSuspend = false;
      bool4 = true;
      paramInt = this.mScreenBrightnessSettingDefault;
      f = 0.0F;
      if (this.mScreenBrightnessModeSetting != 1) {
        break label556;
      }
      bool3 = true;
      if (this.mBootCompleted) {
        break label562;
      }
      bool1 = false;
      bool2 = false;
      label101:
      if (bool1) {
        paramInt = this.mScreenBrightnessSettingDefault;
      }
      paramInt = Math.max(Math.min(paramInt, mScreenBrightnessSettingMaximum), mScreenBrightnessSettingMinimum);
      this.mDisplayPowerRequest.screenBrightness = paramInt;
      if (!isValidAutoBrightnessAdjustment(this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride)) {
        break label642;
      }
      f = this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride;
      label147:
      if (f <= 0.0F) {
        break label660;
      }
      f = Math.max(Math.min(f, mScreenBrightnessSettingMaximum), mScreenBrightnessSettingMinimum);
      this.mDisplayPowerRequest.screenAutoBrightnessAdjustment = f;
      label177:
      this.mDisplayPowerRequest.brightnessSetByUser = bool2;
      this.mDisplayPowerRequest.useAutoBrightness = bool1;
      this.mDisplayPowerRequest.useProximitySensor = shouldUseProximitySensorLocked();
      this.mDisplayPowerRequest.lowPowerMode = this.mLowPowerModeEnabled;
      this.mDisplayPowerRequest.boostScreenBrightness = this.mScreenBrightnessBoostInProgress;
      this.mDisplayPowerRequest.useTwilight = this.mBrightnessUseTwilight;
      if (this.mDisplayPowerRequest.policy != 1) {
        break label671;
      }
      this.mDisplayPowerRequest.dozeScreenState = this.mDozeScreenStateOverrideFromDreamManager;
      if ((this.mDisplayPowerRequest.dozeScreenState == 4) && ((this.mWakeLockSummary & 0x80) != 0)) {
        this.mDisplayPowerRequest.dozeScreenState = 3;
      }
      this.mDisplayPowerRequest.dozeScreenBrightness = this.mDozeScreenBrightnessOverrideFromDreamManager;
      label301:
      if ((this.mProximityLockFromInCallUi) || (this.mWakefulness != 1)) {
        break label690;
      }
      this.mDisplayManagerInternal.setUseProximityForceSuspend(false);
      label324:
      this.mDisplayReady = this.mDisplayManagerInternal.requestPowerState(this.mDisplayPowerRequest, this.mRequestWaitForNegativeProximity);
      this.mRequestWaitForNegativeProximity = false;
      if ((this.mDisplayPowerRequest.policy != 3) || (this.mWakefulness != 1)) {
        break label739;
      }
      if ((this.mUserActivitySummary & 0x8) == 0) {
        break label713;
      }
      this.mButtonLight.setBrightness(this.mButtonBrightness);
      if (DEBUG_SPEW) {
        Slog.i("PowerManagerService", "setBrightness mButtonLight, screenBrightness=" + paramInt);
      }
    }
    for (;;)
    {
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateDisplayPowerStateLocked: mDisplayReady=" + this.mDisplayReady + ", policy=" + this.mDisplayPowerRequest.policy + ", mWakefulness=" + this.mWakefulness + ", mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary) + ", mUserActivitySummary=0x" + Integer.toHexString(this.mUserActivitySummary) + ", mBootCompleted=" + this.mBootCompleted + ", mScreenBrightnessBoostInProgress=" + this.mScreenBrightnessBoostInProgress);
      }
      if ((this.mDisplayReady) && (!bool5)) {
        break label765;
      }
      return false;
      label556:
      bool3 = false;
      break;
      label562:
      if (isValidBrightness(this.mScreenBrightnessOverrideFromWindowManager))
      {
        paramInt = this.mScreenBrightnessOverrideFromWindowManager;
        bool1 = false;
        bool2 = false;
        break label101;
      }
      if (isValidBrightness(this.mTemporaryScreenBrightnessSettingOverride))
      {
        paramInt = this.mTemporaryScreenBrightnessSettingOverride;
        bool1 = bool3;
        bool2 = bool4;
        break label101;
      }
      bool1 = bool3;
      bool2 = bool4;
      if (!isValidBrightness(this.mScreenBrightnessSetting)) {
        break label101;
      }
      paramInt = this.mScreenBrightnessSetting;
      bool1 = bool3;
      bool2 = bool4;
      break label101;
      label642:
      if (!isValidAutoBrightnessAdjustment(this.mScreenAutoBrightnessAdjustmentSetting)) {
        break label147;
      }
      f = this.mScreenAutoBrightnessAdjustmentSetting;
      break label147;
      label660:
      this.mDisplayPowerRequest.screenAutoBrightnessAdjustment = 0.0F;
      break label177;
      label671:
      this.mDisplayPowerRequest.dozeScreenState = 0;
      this.mDisplayPowerRequest.dozeScreenBrightness = -1;
      break label301;
      label690:
      if (!this.mProximityLockFromInCallUi) {
        break label324;
      }
      this.mDisplayManagerInternal.setUseProximityForceSuspend(true);
      this.useProximityForceSuspend = true;
      break label324;
      label713:
      this.mButtonLight.setBrightness(0);
      if (DEBUG_SPEW)
      {
        Slog.i("PowerManagerService", "setBrightness mButtonLight 0.");
        continue;
        label739:
        this.mButtonLight.setBrightness(0);
        if (DEBUG_SPEW) {
          Slog.i("PowerManagerService", "setBrightness mButtonLight 0.");
        }
      }
    }
    label765:
    return true;
  }
  
  private void updateDreamLocked(int paramInt, boolean paramBoolean)
  {
    if ((((paramInt & 0x3F7) != 0) || (paramBoolean)) && (this.mDisplayReady)) {
      scheduleSandmanLocked();
    }
  }
  
  private void updateIsPoweredLocked(int paramInt)
  {
    if ((paramInt & 0x100) != 0)
    {
      boolean bool1 = this.mIsPowered;
      paramInt = this.mPlugType;
      boolean bool2 = this.mBatteryLevelLow;
      this.mIsPowered = this.mBatteryManagerInternal.isPowered(7);
      this.mPlugType = this.mBatteryManagerInternal.getPlugType();
      this.mBatteryLevel = this.mBatteryManagerInternal.getBatteryLevel();
      this.mBatteryLevelLow = this.mBatteryManagerInternal.getBatteryLevelLow();
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateIsPoweredLocked: wasPowered=" + bool1 + ", mIsPowered=" + this.mIsPowered + ", oldPlugType=" + paramInt + ", mPlugType=" + this.mPlugType + ", mBatteryLevelLow=" + this.mBatteryLevelLow + ", mBatteryLevel=" + this.mBatteryLevel);
      }
      if ((bool1 != this.mIsPowered) || (paramInt != this.mPlugType))
      {
        this.mDirty |= 0x40;
        boolean bool3 = this.mWirelessChargerDetector.update(this.mIsPowered, this.mPlugType, this.mBatteryLevel);
        long l = SystemClock.uptimeMillis();
        if (shouldWakeUpWhenPluggedOrUnpluggedLocked(bool1, paramInt, bool3)) {
          wakeUpNoUpdateLocked(l, "android.server.power:POWER", 1000, this.mContext.getOpPackageName(), 1000);
        }
        userActivityNoUpdateLocked(l, 0, 0, 1000);
        if (bool3) {
          this.mNotifier.onWirelessChargingStarted();
        }
      }
      if ((bool1 != this.mIsPowered) || (bool2 != this.mBatteryLevelLow)) {
        if ((bool2 != this.mBatteryLevelLow) && (!this.mBatteryLevelLow)) {
          break label310;
        }
      }
    }
    for (;;)
    {
      updateLowPowerModeLocked();
      return;
      label310:
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateIsPoweredLocked: resetting low power snooze");
      }
      this.mAutoLowPowerModeSnoozing = false;
    }
  }
  
  private void updateLowPowerModeLocked()
  {
    int j = 0;
    int k = 0;
    int i;
    boolean bool1;
    if (!this.mIsPowered)
    {
      i = k;
      if (!this.mBatteryLevelLow)
      {
        if (this.mBootCompleted) {
          i = k;
        }
      }
      else
      {
        if (DEBUG) {
          Slog.d("PowerManagerService", "POWERMODE # updateLowPowerModeLocked:mIsPowered=" + this.mIsPowered + ",mAutoLowPowerModeConfigured=" + this.mAutoLowPowerModeConfigured + ",mAutoLowPowerModeSnoozing=" + this.mAutoLowPowerModeSnoozing + ",mBatteryLevelLow=" + this.mBatteryLevelLow + ",mLowPowerModeSetting=" + this.mLowPowerModeSetting + ",mLowPowerModeEnabled=" + this.mLowPowerModeEnabled + ",mBatteryLevel=" + this.mBatteryLevel + ",mCriticalBatteryLevel=" + this.mCriticalBatteryLevel);
        }
        if ((!this.mIsPowered) && (this.mAutoLowPowerModeConfigured) && (!this.mAutoLowPowerModeSnoozing)) {
          break label372;
        }
        bool1 = false;
        label179:
        if (this.mLowPowerModeSetting) {
          break label402;
        }
      }
    }
    label372:
    label402:
    for (final boolean bool2 = bool1;; bool2 = true)
    {
      if (DEBUG) {
        Slog.d("PowerManagerService", "POWERMODE # updateLowPowerModeLocked:autoLowPowerModeEnabled=" + bool1 + ",lowPowerModeEnabled=" + bool2 + ",mLowPowerModeEnabled=" + this.mLowPowerModeEnabled);
      }
      if ((this.mLowPowerModeEnabled != bool2) || (i != 0))
      {
        this.mLowPowerModeEnabled = bool2;
        i = j;
        if (bool2) {
          i = 1;
        }
        powerHintInternal(5, i);
        postAfterBootCompleted(new Runnable()
        {
          public void run()
          {
            Slog.i("PowerManagerService", "POWERMODE # updateLowPowerModeLocked: post runnable # mLowPowerModeEnabled=" + PowerManagerService.-get10(PowerManagerService.this));
            ??? = new Intent("android.os.action.POWER_SAVE_MODE_CHANGING").putExtra("mode", PowerManagerService.-get10(PowerManagerService.this)).addFlags(1073741824);
            PowerManagerService.-get3(PowerManagerService.this).sendBroadcast((Intent)???);
            synchronized (PowerManagerService.-get9(PowerManagerService.this))
            {
              ArrayList localArrayList = new ArrayList(PowerManagerService.-get11(PowerManagerService.this));
              int i = 0;
              if (i < localArrayList.size())
              {
                ((PowerManagerInternal.LowPowerModeListener)localArrayList.get(i)).onLowPowerModeChanged(bool2);
                i += 1;
              }
            }
          }
        });
      }
      return;
      i = k;
      if (!this.mLowPowerModeSetting) {
        break;
      }
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateLowPowerModeLocked: powered or booting with sufficient battery, turning setting off");
      }
      Slog.d("PowerManagerService", "POWERMODE # updateLowPowerModeLocked: powered, turning setting off");
      Settings.Global.putInt(this.mContext.getContentResolver(), "low_power", 0);
      this.mLowPowerModeSetting = false;
      i = k;
      if (this.mBatteryLevelLow) {
        break;
      }
      i = k;
      if (this.mBootCompleted) {
        break;
      }
      i = 1;
      break;
      if ((this.mBatteryLevelLow) || (this.mBatteryLevel <= this.mCriticalBatteryLevel))
      {
        bool1 = true;
        break label179;
      }
      bool1 = false;
      break label179;
    }
  }
  
  private void updateScreenBrightnessBoostLocked(int paramInt)
  {
    if (((paramInt & 0x800) != 0) && (this.mScreenBrightnessBoostInProgress))
    {
      long l1 = SystemClock.uptimeMillis();
      this.mHandler.removeMessages(3);
      if (this.mLastScreenBrightnessBoostTime > this.mLastSleepTime)
      {
        long l2 = this.mLastScreenBrightnessBoostTime + 5000L;
        if (l2 > l1)
        {
          Message localMessage = this.mHandler.obtainMessage(3);
          localMessage.setAsynchronous(true);
          this.mHandler.sendMessageAtTime(localMessage, l2);
          return;
        }
      }
      this.mScreenBrightnessBoostInProgress = false;
      this.mNotifier.onScreenBrightnessBoostChanged();
      userActivityNoUpdateLocked(l1, 0, 0, 1000);
    }
  }
  
  private void updateSettingsLocked()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int i;
    boolean bool1;
    label34:
    label48:
    label64:
    label78:
    label94:
    label164:
    label192:
    label214:
    label357:
    label418:
    boolean bool2;
    label435:
    boolean bool3;
    label515:
    boolean bool4;
    if (this.mDreamsEnabledByDefaultConfig)
    {
      i = 1;
      if (Settings.Secure.getIntForUser(localContentResolver, "screensaver_enabled", i, -2) == 0) {
        break label628;
      }
      bool1 = true;
      this.mDreamsEnabledSetting = bool1;
      if (!this.mDreamsActivatedOnSleepByDefaultConfig) {
        break label633;
      }
      i = 1;
      if (Settings.Secure.getIntForUser(localContentResolver, "screensaver_activate_on_sleep", i, -2) == 0) {
        break label638;
      }
      bool1 = true;
      this.mDreamsActivateOnSleepSetting = bool1;
      if (!this.mDreamsActivatedOnDockByDefaultConfig) {
        break label643;
      }
      i = 1;
      if (Settings.Secure.getIntForUser(localContentResolver, "screensaver_activate_on_dock", i, -2) == 0) {
        break label648;
      }
      bool1 = true;
      this.mDreamsActivateOnDockSetting = bool1;
      this.mScreenOffTimeoutSetting = Settings.System.getIntForUser(localContentResolver, "screen_off_timeout", 15000, -2);
      this.mSleepTimeoutSetting = Settings.Secure.getIntForUser(localContentResolver, "sleep_timeout", -1, -2);
      this.mStayOnWhilePluggedInSetting = Settings.Global.getInt(localContentResolver, "stay_on_while_plugged_in", 1);
      if (Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) != 1) {
        break label653;
      }
      bool1 = true;
      this.mTheaterModeEnabled = bool1;
      if (this.mSupportsDoubleTapWakeConfig)
      {
        if (Settings.Secure.getIntForUser(localContentResolver, "double_tap_to_wake", 0, -2) == 0) {
          break label658;
        }
        bool1 = true;
        if (bool1 != this.mDoubleTapWakeEnabled)
        {
          this.mDoubleTapWakeEnabled = bool1;
          if (!this.mDoubleTapWakeEnabled) {
            break label663;
          }
          i = 1;
          nativeSetFeature(1, i);
        }
      }
      i = this.mScreenBrightnessSetting;
      this.mScreenBrightnessSetting = Settings.System.getIntForUser(localContentResolver, "screen_brightness", this.mScreenBrightnessSettingDefault, -2);
      if (i != this.mScreenBrightnessSetting) {
        this.mTemporaryScreenBrightnessSettingOverride = -1;
      }
      float f = this.mScreenAutoBrightnessAdjustmentSetting;
      this.mScreenAutoBrightnessAdjustmentSetting = Settings.System.getFloatForUser(localContentResolver, "screen_auto_brightness_adj", 0.0F, -2);
      if (f != this.mScreenAutoBrightnessAdjustmentSetting) {
        this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = NaN.0F;
      }
      this.mScreenBrightnessModeSetting = Settings.System.getIntForUser(localContentResolver, "screen_brightness_mode", 0, -2);
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateSettingsLocked: mScreenBrightnessModeSetting=" + this.mScreenBrightnessModeSetting);
      }
      if (Settings.Secure.getIntForUser(localContentResolver, "brightness_use_twilight", 0, -2) == 0) {
        break label668;
      }
      bool1 = true;
      this.mBrightnessUseTwilight = bool1;
      i = this.mButtonBrightness;
      this.mButtonBrightness = Settings.System.getInt(localContentResolver, "buttons_brightness", this.mButtonBrightnessSettingDefault);
      if (i != this.mButtonBrightness) {
        userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 1, 0, 1000);
      }
      if (Settings.Global.getInt(localContentResolver, "low_power", 0) == 0) {
        break label673;
      }
      bool1 = true;
      i = Settings.Global.getInt(localContentResolver, "low_power_trigger_level", 0);
      if (i == 0) {
        break label678;
      }
      bool2 = true;
      if (DEBUG) {
        Slog.d("PowerManagerService", "POWERMODE # updateSettingsLocked:lowPowerModeEnabled=" + bool1 + ",mLowPowerModeSetting=" + this.mLowPowerModeSetting + ",autoLowPowerModeConfigured=" + bool2 + ",mAutoLowPowerModeConfigured=" + this.mAutoLowPowerModeConfigured);
      }
      if (bool1 == this.mLowPowerModeSetting) {
        break label684;
      }
      bool3 = true;
      if (bool2 == this.mAutoLowPowerModeConfigured) {
        break label690;
      }
      bool4 = true;
      label527:
      if (DEBUG) {
        Slog.d("PowerManagerService", "POWERMODE # updateSettingsLocked:triggerLevel=" + i + ",enableChanged=" + bool3 + ",configChanged=" + bool4);
      }
      if (bool3) {
        this.mLowPowerModeSetting = bool1;
      }
      if (bool4) {
        this.mAutoLowPowerModeConfigured = bool2;
      }
      if (!bool3) {
        break label696;
      }
      updateLowPowerModeLocked();
    }
    for (;;)
    {
      this.mDirty |= 0x20;
      return;
      i = 0;
      break;
      label628:
      bool1 = false;
      break label34;
      label633:
      i = 0;
      break label48;
      label638:
      bool1 = false;
      break label64;
      label643:
      i = 0;
      break label78;
      label648:
      bool1 = false;
      break label94;
      label653:
      bool1 = false;
      break label164;
      label658:
      bool1 = false;
      break label192;
      label663:
      i = 0;
      break label214;
      label668:
      bool1 = false;
      break label357;
      label673:
      bool1 = false;
      break label418;
      label678:
      bool2 = false;
      break label435;
      label684:
      bool3 = false;
      break label515;
      label690:
      bool4 = false;
      break label527;
      label696:
      if ((bool4) && (this.mBatteryLevel <= i)) {
        updateLowPowerModeLocked();
      }
    }
  }
  
  private void updateStayOnLocked(int paramInt)
  {
    boolean bool;
    if ((paramInt & 0x120) != 0)
    {
      bool = this.mStayOn;
      if ((this.mStayOnWhilePluggedInSetting != 0) && (!isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked())) {
        break label53;
      }
    }
    label53:
    for (this.mStayOn = false;; this.mStayOn = this.mBatteryManagerInternal.isPowered(this.mStayOnWhilePluggedInSetting))
    {
      if (this.mStayOn != bool) {
        this.mDirty |= 0x80;
      }
      return;
    }
  }
  
  private void updateSuspendBlockerLocked()
  {
    int i;
    boolean bool1;
    int j;
    label22:
    boolean bool2;
    if ((this.mWakeLockSummary & 0x1) != 0)
    {
      i = 1;
      bool1 = needDisplaySuspendBlockerLocked();
      if (!bool1) {
        break label166;
      }
      j = 0;
      bool2 = this.mDisplayPowerRequest.isBrightOrDim();
      if ((j == 0) && (this.mDecoupleHalAutoSuspendModeFromDisplayConfig)) {
        setHalAutoSuspendModeLocked(false);
      }
      if ((i != 0) && (!this.mHoldingWakeLockSuspendBlocker)) {
        break label171;
      }
      label58:
      if ((bool1) && (!this.mHoldingDisplaySuspendBlocker)) {
        break label188;
      }
    }
    for (;;)
    {
      if ((this.mDecoupleHalInteractiveModeFromDisplayConfig) && ((bool2) || (this.mDisplayReady))) {
        setHalInteractiveModeLocked(bool2);
      }
      if ((i == 0) && (this.mHoldingWakeLockSuspendBlocker))
      {
        this.mWakeLockSuspendBlocker.release();
        this.mHoldingWakeLockSuspendBlocker = false;
      }
      if ((!bool1) && (this.mHoldingDisplaySuspendBlocker))
      {
        this.mDisplaySuspendBlocker.release();
        this.mHoldingDisplaySuspendBlocker = false;
      }
      if ((j != 0) && (this.mDecoupleHalAutoSuspendModeFromDisplayConfig)) {
        setHalAutoSuspendModeLocked(true);
      }
      return;
      i = 0;
      break;
      label166:
      j = 1;
      break label22;
      label171:
      this.mWakeLockSuspendBlocker.acquire();
      this.mHoldingWakeLockSuspendBlocker = true;
      break label58;
      label188:
      this.mDisplaySuspendBlocker.acquire();
      this.mHoldingDisplaySuspendBlocker = true;
    }
  }
  
  private void updateUserActivitySummaryLocked(long paramLong, int paramInt)
  {
    int i;
    int j;
    boolean bool;
    label133:
    long l2;
    if ((paramInt & 0x27) != 0)
    {
      this.mHandler.removeMessages(1);
      l1 = 0L;
      if ((this.mWakefulness != 1) && (this.mWakefulness != 2)) {
        break label457;
      }
      paramInt = getSleepTimeoutLocked();
      i = getScreenOffTimeoutLocked(paramInt);
      j = getScreenDimDurationLocked(i);
      bool = this.mUserInactiveOverrideFromWindowManager;
      this.mUserActivitySummary = 0;
      if (this.mLastUserActivityTime >= this.mLastWakeTime)
      {
        if ((this.mLastUserActivityButtonTime < this.mLastWakeTime) || (paramLong >= this.mLastUserActivityButtonTime + 1000L)) {
          break label473;
        }
        this.mUserActivitySummary |= 0x8;
        this.mUserActivitySummary |= 0x1;
        l1 = this.mLastUserActivityButtonTime + 1000L;
      }
      l2 = l1;
      if (this.mUserActivitySummary == 0)
      {
        l2 = l1;
        if (this.mLastUserActivityTimeNoChangeLights >= this.mLastWakeTime)
        {
          l1 = this.mLastUserActivityTimeNoChangeLights + i;
          l2 = l1;
          if (paramLong < l1)
          {
            if (this.mDisplayPowerRequest.policy != 3) {
              break label545;
            }
            this.mUserActivitySummary = 1;
            l2 = l1;
          }
        }
      }
      label201:
      l1 = l2;
      if (this.mUserActivitySummary == 0)
      {
        if (paramInt < 0) {
          break label572;
        }
        long l3 = Math.max(this.mLastUserActivityTime, this.mLastUserActivityTimeNoChangeLights);
        l1 = l2;
        if (l3 >= this.mLastWakeTime)
        {
          l2 = l3 + paramInt;
          l1 = l2;
          if (paramLong < l2) {
            this.mUserActivitySummary = 4;
          }
        }
      }
    }
    for (long l1 = l2;; l1 = -1L)
    {
      l2 = l1;
      if (this.mUserActivitySummary != 4)
      {
        l2 = l1;
        if (bool)
        {
          if (((this.mUserActivitySummary & 0x3) != 0) && (l1 >= paramLong) && (this.mOverriddenTimeout == -1L)) {
            this.mOverriddenTimeout = l1;
          }
          this.mUserActivitySummary = 4;
          l2 = -1L;
        }
      }
      l1 = l2;
      if (this.mUserActivitySummary != 0)
      {
        l1 = l2;
        if (l2 >= 0L)
        {
          Message localMessage = this.mHandler.obtainMessage(1);
          localMessage.setAsynchronous(true);
          this.mHandler.sendMessageAtTime(localMessage, l2);
          l1 = l2;
        }
      }
      for (;;)
      {
        if (DEBUG_SPEW) {
          Slog.d("PowerManagerService", "updateUserActivitySummaryLocked: mWakefulness=" + PowerManagerInternal.wakefulnessToString(this.mWakefulness) + ", mUserActivitySummary=0x" + Integer.toHexString(this.mUserActivitySummary) + ", nextTimeout=" + TimeUtils.formatUptime(l1));
        }
        return;
        label457:
        if (this.mWakefulness == 3) {
          break;
        }
        this.mUserActivitySummary = 0;
      }
      label473:
      if (paramLong < this.mLastUserActivityTime + i - j)
      {
        l1 = this.mLastUserActivityTime + i - j;
        this.mUserActivitySummary = 1;
        break label133;
      }
      l2 = this.mLastUserActivityTime + i;
      l1 = l2;
      if (paramLong >= l2) {
        break label133;
      }
      this.mUserActivitySummary = 2;
      l1 = l2;
      break label133;
      label545:
      l2 = l1;
      if (this.mDisplayPowerRequest.policy != 2) {
        break label201;
      }
      this.mUserActivitySummary = 2;
      l2 = l1;
      break label201;
      label572:
      this.mUserActivitySummary = 4;
    }
  }
  
  private void updateWakeLockDisabledStatesLocked()
  {
    int j = 0;
    int m = this.mWakeLocks.size();
    int i = 0;
    if (i < m)
    {
      WakeLock localWakeLock = (WakeLock)this.mWakeLocks.get(i);
      int k = j;
      if ((localWakeLock.mFlags & 0xFFFF) == 1)
      {
        k = j;
        if (setWakeLockDisabledStateLocked(localWakeLock))
        {
          k = 1;
          if (!localWakeLock.mDisabled) {
            break label83;
          }
          notifyWakeLockReleasedLocked(localWakeLock);
        }
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        label83:
        notifyWakeLockAcquiredLocked(localWakeLock);
      }
    }
    if (j != 0)
    {
      this.mDirty |= 0x1;
      updatePowerStateLocked();
    }
  }
  
  private void updateWakeLockSummaryLocked(int paramInt)
  {
    if ((paramInt & 0x3) != 0)
    {
      this.mWakeLockSummary = 0;
      this.mProximityLockFromInCallUi = false;
      int i = this.mWakeLocks.size();
      paramInt = 0;
      if (paramInt < i)
      {
        WakeLock localWakeLock = (WakeLock)this.mWakeLocks.get(paramInt);
        switch (localWakeLock.mFlags & 0xFFFF)
        {
        }
        for (;;)
        {
          paramInt += 1;
          break;
          if (!localWakeLock.mDisabled)
          {
            this.mWakeLockSummary |= 0x1;
            continue;
            if (checkForDisableWakeLocks(localWakeLock))
            {
              this.mWakeLockSummary |= 0xA;
              continue;
              this.mWakeLockSummary |= 0x2;
              continue;
              this.mWakeLockSummary |= 0x4;
              continue;
              this.mWakeLockSummary |= 0x10;
              if (localWakeLock.mPackageName.equals("com.android.incallui"))
              {
                this.mProximityLockFromInCallUi = true;
                continue;
                this.mWakeLockSummary |= 0x40;
                continue;
                this.mWakeLockSummary |= 0x80;
              }
            }
          }
        }
      }
      if (this.mWakefulness != 3) {
        this.mWakeLockSummary &= 0xFF3F;
      }
      if ((this.mWakefulness == 0) || ((this.mWakeLockSummary & 0x40) != 0))
      {
        this.mWakeLockSummary &= 0xFFFFFFF1;
        if (this.mWakefulness == 0) {
          this.mWakeLockSummary &= 0xFFFFFFEF;
        }
      }
      if (this.mProximityLockFromInCallUi) {
        this.mWakeLockSummary |= 0x10;
      }
      if ((this.mWakeLockSummary & 0x6) != 0)
      {
        if (this.mWakefulness != 1) {
          break label456;
        }
        this.mWakeLockSummary |= 0x21;
      }
    }
    for (;;)
    {
      if ((this.mWakeLockSummary & 0x80) != 0) {
        this.mWakeLockSummary |= 0x1;
      }
      if ((this.mWakeLockSummary & 0x10) == 0) {
        this.mProximityPositive = false;
      }
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "updateWakeLockSummaryLocked: mWakefulness=" + PowerManagerInternal.wakefulnessToString(this.mWakefulness) + ", mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary));
      }
      return;
      label456:
      if (this.mWakefulness == 2) {
        this.mWakeLockSummary |= 0x1;
      }
    }
  }
  
  private void updateWakeLockWorkSourceInternal(IBinder paramIBinder, WorkSource paramWorkSource, String paramString, int paramInt)
  {
    int i;
    synchronized (this.mLock)
    {
      i = findWakeLockIndexLocked(paramIBinder);
      if (i < 0)
      {
        if (DEBUG_SPEW) {
          Slog.d("PowerManagerService", "updateWakeLockWorkSourceInternal: lock=" + Objects.hashCode(paramIBinder) + " [not found], ws=" + paramWorkSource);
        }
        throw new IllegalArgumentException("Wake lock not active: " + paramIBinder + " from uid " + paramInt);
      }
    }
    WakeLock localWakeLock = (WakeLock)this.mWakeLocks.get(i);
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "updateWakeLockWorkSourceInternal: lock=" + Objects.hashCode(paramIBinder) + " [" + localWakeLock.mTag + "], ws=" + paramWorkSource);
    }
    if (!localWakeLock.hasSameWorkSource(paramWorkSource))
    {
      notifyWakeLockChangingLocked(localWakeLock, localWakeLock.mFlags, localWakeLock.mTag, localWakeLock.mPackageName, localWakeLock.mOwnerUid, localWakeLock.mOwnerPid, paramWorkSource, paramString);
      localWakeLock.mHistoryTag = paramString;
      localWakeLock.updateWorkSource(paramWorkSource);
    }
  }
  
  private boolean updateWakefulnessLocked(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    long l;
    if ((paramInt & 0x697) != 0)
    {
      bool1 = bool2;
      if (this.mWakefulness == 1)
      {
        bool1 = bool2;
        if (isItBedTimeYetLocked())
        {
          if (DEBUG_SPEW) {
            Slog.d("PowerManagerService", "updateWakefulnessLocked: Bed time...");
          }
          l = SystemClock.uptimeMillis();
          if (!shouldNapAtBedTimeLocked()) {
            break label70;
          }
          bool1 = napNoUpdateLocked(l, 1000);
        }
      }
    }
    return bool1;
    label70:
    return goToSleepNoUpdateLocked(l, 2, 0, 1000);
  }
  
  private void userActivityFromNative(long paramLong, int paramInt1, int paramInt2)
  {
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "userActivityFromNative");
    }
    userActivityInternal(paramLong, paramInt1, paramInt2, 1000);
  }
  
  private void userActivityInternal(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mLock)
    {
      if (userActivityNoUpdateLocked(paramLong, paramInt1, paramInt2, paramInt3)) {
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private boolean userActivityNoUpdateLocked(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "userActivityNoUpdateLocked: eventTime=" + paramLong + ", event=" + paramInt1 + ", flags=0x" + Integer.toHexString(paramInt2) + ", uid=" + paramInt3);
    }
    if ((paramLong < this.mLastSleepTime) || (paramLong < this.mLastWakeTime)) {}
    while ((!this.mBootCompleted) || (!this.mSystemReady)) {
      return false;
    }
    Trace.traceBegin(131072L, "userActivity");
    try
    {
      if (paramLong > this.mLastInteractivePowerHintTime)
      {
        powerHintInternal(2, 0);
        this.mLastInteractivePowerHintTime = paramLong;
      }
      this.mNotifier.onUserActivity(paramInt1, paramInt3);
      if (this.mUserInactiveOverrideFromWindowManager)
      {
        this.mUserInactiveOverrideFromWindowManager = false;
        this.mOverriddenTimeout = -1L;
      }
      if (this.mWakefulness != 0)
      {
        paramInt3 = this.mWakefulness;
        if (paramInt3 != 3) {
          break label186;
        }
      }
      label186:
      while ((paramInt2 & 0x2) != 0) {
        return false;
      }
      if ((paramInt2 & 0x1) != 0)
      {
        if ((paramLong > this.mLastUserActivityTimeNoChangeLights) && (paramLong > this.mLastUserActivityTime))
        {
          this.mLastUserActivityTimeNoChangeLights = paramLong;
          this.mDirty |= 0x4;
          return true;
        }
      }
      else if (paramLong > this.mLastUserActivityTime)
      {
        this.mLastUserActivityTime = paramLong;
        this.mDirty |= 0x4;
        if (paramInt1 == 1) {
          this.mLastUserActivityButtonTime = paramLong;
        }
        return true;
      }
      return false;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  private void wakeUpInternal(long paramLong, String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mDisplayManagerInternal.setWakingupReason(paramString1);
      if (wakeUpNoUpdateLocked(paramLong, paramString1, paramInt1, paramString2, paramInt2)) {
        updatePowerStateLocked();
      }
      return;
    }
  }
  
  private boolean wakeUpNoUpdateLocked(long paramLong, String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    if (DEBUG)
    {
      StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[i];
        Slog.d("PowerManagerService", "   |----" + localStackTraceElement.toString());
        i += 1;
      }
    }
    if (DEBUG_SPEW) {
      Slog.d("PowerManagerService", "wakeUpNoUpdateLocked: eventTime=" + paramLong + ", reason=" + paramString1 + ", uid=" + paramInt1);
    }
    if (paramInt1 == 1000) {
      this.mBlockFingerprintSleep = true;
    }
    if ((paramLong < this.mLastSleepTime) || (this.mWakefulness == 1)) {}
    while ((!this.mBootCompleted) || (!this.mSystemReady)) {
      return false;
    }
    if ((this.mDeviceIdleState == 5) || (this.mDeviceIdleState == 6))
    {
      if ((paramString1 != null) && (paramString1.contains("com.appboy.push")))
      {
        Log.w("PowerManagerService", "Not allow to hold any wakelock during doze mode for: " + paramString1);
        return false;
      }
      if ((paramString2 != null) && (paramString2.equals("com.moblie.bestapps.inlike")))
      {
        Log.w("PowerManagerService", "Not allow to hold any wakelock during doze mode for: " + paramString2);
        return false;
      }
    }
    if (this.mScreenBrightnessModeSetting == 1) {
      sBrightnessBoost = 1;
    }
    Trace.traceBegin(131072L, "wakeUp");
    for (;;)
    {
      try
      {
        switch (this.mWakefulness)
        {
        case 1: 
          this.mLastWakeTime = paramLong;
          setWakefulnessLocked(1, 0);
          this.mNotifier.onWakeUp(paramString1, paramInt1, paramString2, paramInt2);
          userActivityNoUpdateLocked(paramLong, 0, 0, paramInt1);
          return true;
        }
      }
      finally
      {
        Trace.traceEnd(131072L);
      }
      Slog.i("PowerManagerService", "Waking up from sleep (uid " + paramInt1 + ", " + paramString1 + ")...");
      continue;
      Slog.i("PowerManagerService", "Waking up from dream (uid " + paramInt1 + ", " + paramString1 + ")...");
      continue;
      Slog.i("PowerManagerService", "Waking up from dozing (uid " + paramInt1 + ", " + paramString1 + ")...");
    }
  }
  
  boolean checkForDisableWakeLocks(WakeLock paramWakeLock)
  {
    return (!UserHandle.isApp(paramWakeLock.mOwnerUid)) || (paramWakeLock.mTag == null) || (!paramWakeLock.mTag.equals("Unity-StartupWakeLock")) || (this.mUidState.get(paramWakeLock.mOwnerUid, 16) <= 4);
  }
  
  void checkForLongWakeLocks()
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        long l4 = SystemClock.uptimeMillis();
        this.mNotifyLongDispatched = l4;
        l1 = Long.MAX_VALUE;
        int j = this.mWakeLocks.size();
        i = 0;
        if (i >= j) {
          break label156;
        }
        WakeLock localWakeLock = (WakeLock)this.mWakeLocks.get(i);
        l2 = l1;
        if ((localWakeLock.mFlags & 0xFFFF) != 1) {
          break label199;
        }
        l2 = l1;
        if (!localWakeLock.mNotifiedAcquired) {
          break label199;
        }
        if (localWakeLock.mNotifiedLong)
        {
          l2 = l1;
        }
        else if (localWakeLock.mAcquireTime < l4 - 60000L)
        {
          notifyWakeLockLongStartedLocked(localWakeLock);
          l2 = l1;
        }
      }
      long l3 = ((WakeLock)localObject2).mAcquireTime + 60000L;
      long l2 = l1;
      if (l3 < l1)
      {
        l2 = l3;
        break label199;
        label156:
        this.mNotifyLongScheduled = 0L;
        this.mHandler.removeMessages(4);
        if (l1 != Long.MAX_VALUE)
        {
          this.mNotifyLongNextCheck = l1;
          enqueueNotifyLongMsgLocked(l1);
        }
        for (;;)
        {
          return;
          this.mNotifyLongNextCheck = 0L;
        }
      }
      label199:
      i += 1;
      long l1 = l2;
    }
  }
  
  protected boolean dynamicallyConfigPowerManagerServiceLogTag(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length >= 1)
    {
      if (!"log".equals(paramArrayOfString[0])) {
        return false;
      }
      if (paramArrayOfString.length != 3)
      {
        paramPrintWriter.println("Invalid argument! Get detail help as bellow:");
        logOutPowerManagerServiceLogTagHelp(paramPrintWriter);
        return true;
      }
    }
    else
    {
      return false;
    }
    paramPrintWriter.println("dynamicallyConfigPowerManagerServiceLogTag, args.length:" + paramArrayOfString.length);
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      paramPrintWriter.println("dynamicallyConfigPowerManagerServiceLogTag, args[" + i + "]:" + paramArrayOfString[i]);
      i += 1;
    }
    paramFileDescriptor = paramArrayOfString[1];
    if ("1".equals(paramArrayOfString[2])) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println("dynamicallyConfigPowerManagerServiceLogTag, logCategoryTag:" + paramFileDescriptor + ", on:" + bool);
      if (!"all".equals(paramFileDescriptor)) {
        break;
      }
      DEBUG = bool;
      DEBUG_SPEW = bool;
      Notifier.DEBUG = bool;
      LightsService.DEBUG = bool;
      return true;
    }
    if ("switch".equals(paramFileDescriptor))
    {
      LightsService.DEBUG = bool;
      com.android.server.OemExService.DEBUG_ONEPLUS = bool;
      com.android.server.policy.OemPhoneWindowManager.DEBUG = bool;
      com.android.server.policy.OemPhoneWindowManager.DEBUG_INPUT = bool;
      com.android.server.policy.OemPhoneWindowManager.DEBUG_KEYLOCK = bool;
      com.android.server.policy.OemPhoneWindowManager.DEBUG_KEYSWAP = bool;
      return true;
    }
    paramPrintWriter.println("Invalid log tag argument! Get detail help as bellow:");
    logOutPowerManagerServiceLogTagHelp(paramPrintWriter);
    return true;
  }
  
  boolean isDeviceIdleModeInternal()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mDeviceIdleMode;
      return bool;
    }
  }
  
  boolean isLightDeviceIdleModeInternal()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mLightDeviceIdleMode;
      return bool;
    }
  }
  
  protected void logOutPowerManagerServiceLogTagHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("********************** Help begin:**********************");
    paramPrintWriter.println("1 All PowerManagerService log");
    paramPrintWriter.println("cmd: dumpsys power log all 0/1");
    paramPrintWriter.println("2 All needed log when oem log is on");
    paramPrintWriter.println("cmd: dumpsys power log switch 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("********************** Help end.  **********************");
  }
  
  public void monitor()
  {
    Object localObject = this.mLock;
  }
  
  protected void notifyWakeLockAcquiredLocked(WakeLock paramWakeLock)
  {
    if ((!this.mSystemReady) || (paramWakeLock.mDisabled)) {
      return;
    }
    paramWakeLock.mNotifiedAcquired = true;
    this.mNotifier.onWakeLockAcquired(paramWakeLock.mFlags, paramWakeLock.mTag, paramWakeLock.mPackageName, paramWakeLock.mOwnerUid, paramWakeLock.mOwnerPid, paramWakeLock.mWorkSource, paramWakeLock.mHistoryTag);
    restartNofifyLongTimerLocked(paramWakeLock);
  }
  
  protected void notifyWakeLockReleasedLocked(WakeLock paramWakeLock)
  {
    if ((this.mSystemReady) && (paramWakeLock.mNotifiedAcquired))
    {
      paramWakeLock.mNotifiedAcquired = false;
      paramWakeLock.mAcquireTime = 0L;
      this.mNotifier.onWakeLockReleased(paramWakeLock.mFlags, paramWakeLock.mTag, paramWakeLock.mPackageName, paramWakeLock.mOwnerUid, paramWakeLock.mOwnerPid, paramWakeLock.mWorkSource, paramWakeLock.mHistoryTag);
      notifyWakeLockLongFinishedLocked(paramWakeLock);
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    localObject1 = this.mLock;
    if (paramInt == 600) {}
    for (;;)
    {
      try
      {
        incrementBootCount();
        return;
      }
      finally {}
      if (paramInt == 1000)
      {
        long l = SystemClock.uptimeMillis();
        this.mBootCompleted = true;
        this.mDirty |= 0x10;
        userActivityNoUpdateLocked(l, 0, 0, 1000);
        updatePowerStateLocked();
        if (!ArrayUtils.isEmpty(this.mBootCompletedRunnables))
        {
          Slog.d("PowerManagerService", "Posting " + this.mBootCompletedRunnables.length + " delayed runnables");
          Runnable[] arrayOfRunnable = this.mBootCompletedRunnables;
          int i = arrayOfRunnable.length;
          paramInt = 0;
          if (paramInt < i)
          {
            Runnable localRunnable = arrayOfRunnable[paramInt];
            BackgroundThread.getHandler().post(localRunnable);
            paramInt += 1;
            continue;
          }
        }
        this.mBootCompletedRunnables = null;
      }
    }
  }
  
  public void onStart()
  {
    publishBinderService("power", new BinderService(null));
    publishLocalService(PowerManagerInternal.class, new LocalService(null));
    Watchdog.getInstance().addMonitor(this);
    Watchdog.getInstance().addThread(this.mHandler);
  }
  
  boolean setDeviceIdleModeInternal(boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        boolean bool = this.mDeviceIdleMode;
        if (bool == paramBoolean) {
          return false;
        }
        this.mDeviceIdleMode = paramBoolean;
        updateWakeLockDisabledStatesLocked();
        if (paramBoolean)
        {
          EventLogTags.writeDeviceIdleOnPhase("power");
          return true;
        }
      }
      EventLogTags.writeDeviceIdleOffPhase("power");
    }
  }
  
  void setDeviceIdleStateInternal(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mDeviceIdleState = paramInt;
      return;
    }
  }
  
  void setDeviceIdleTempWhitelistInternal(int[] paramArrayOfInt)
  {
    synchronized (this.mLock)
    {
      this.mDeviceIdleTempWhitelist = paramArrayOfInt;
      if (this.mDeviceIdleMode) {
        updateWakeLockDisabledStatesLocked();
      }
      return;
    }
  }
  
  void setDeviceIdleWhitelistInternal(int[] paramArrayOfInt)
  {
    synchronized (this.mLock)
    {
      this.mDeviceIdleWhitelist = paramArrayOfInt;
      if (this.mDeviceIdleMode) {
        updateWakeLockDisabledStatesLocked();
      }
      return;
    }
  }
  
  boolean setLightDeviceIdleModeInternal(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mLightDeviceIdleMode != paramBoolean)
      {
        this.mLightDeviceIdleMode = paramBoolean;
        updateWakeLockDisabledStatesLocked();
        return true;
      }
      return false;
    }
  }
  
  void setMaximumScreenOffTimeoutFromDeviceAdminInternal(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mMaximumScreenOffTimeoutFromDeviceAdmin = paramInt;
      this.mDirty |= 0x20;
      updatePowerStateLocked();
      return;
    }
  }
  
  void setStayOnSettingInternal(int paramInt)
  {
    Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", paramInt);
  }
  
  public void systemReady(IAppOpsService paramIAppOpsService)
  {
    synchronized (this.mLock)
    {
      this.mSystemReady = true;
      this.mAppOps = paramIAppOpsService;
      this.mDreamManager = ((DreamManagerInternal)getLocalService(DreamManagerInternal.class));
      this.mDisplayManagerInternal = ((DisplayManagerInternal)getLocalService(DisplayManagerInternal.class));
      this.mPolicy = ((WindowManagerPolicy)getLocalService(WindowManagerPolicy.class));
      this.mBatteryManagerInternal = ((BatteryManagerInternal)getLocalService(BatteryManagerInternal.class));
      this.mBatteryLevel = this.mBatteryManagerInternal.getBatteryLevel();
      this.mBatteryLevelLow = this.mBatteryManagerInternal.getBatteryLevelLow();
      paramIAppOpsService = (PowerManager)this.mContext.getSystemService("power");
      mScreenBrightnessSettingMinimum = paramIAppOpsService.getMinimumScreenBrightnessSetting();
      mScreenBrightnessSettingMaximum = paramIAppOpsService.getMaximumScreenBrightnessSetting();
      this.mScreenBrightnessSettingDefault = paramIAppOpsService.getDefaultScreenBrightnessSetting();
      if (DEBUG_SPEW) {
        Slog.d("PowerManagerService", "mScreenBrightnessSettingMinimum = " + mScreenBrightnessSettingMinimum + " mScreenBrightnessSettingMinimum = " + mScreenBrightnessSettingMaximum + " mScreenBrightnessSettingDefault = " + this.mScreenBrightnessSettingDefault);
      }
      Object localObject2 = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
      this.mBatteryStats = BatteryStatsService.getService();
      this.mNotifier = new Notifier(Looper.getMainLooper(), this.mContext, this.mBatteryStats, this.mAppOps, createSuspendBlockerLocked("PowerManagerService.Broadcasts"), this.mPolicy);
      this.mWirelessChargerDetector = new WirelessChargerDetector((SensorManager)localObject2, createSuspendBlockerLocked("PowerManagerService.WirelessChargerDetector"), this.mHandler);
      this.mSettingsObserver = new SettingsObserver(this.mHandler);
      this.mLightsManager = ((LightsManager)getLocalService(LightsManager.class));
      this.mAttentionLight = this.mLightsManager.getLight(5);
      this.mDisplayManagerInternal.initPowerManagement(this.mDisplayPowerCallbacks, this.mHandler, (SensorManager)localObject2);
      localObject2 = new IntentFilter();
      ((IntentFilter)localObject2).addAction("android.intent.action.BATTERY_CHANGED");
      ((IntentFilter)localObject2).setPriority(1000);
      this.mContext.registerReceiver(new BatteryReceiver(null), (IntentFilter)localObject2, null, this.mHandler);
      localObject2 = new IntentFilter();
      ((IntentFilter)localObject2).addAction("android.intent.action.DREAMING_STARTED");
      ((IntentFilter)localObject2).addAction("android.intent.action.DREAMING_STOPPED");
      this.mContext.registerReceiver(new DreamReceiver(null), (IntentFilter)localObject2, null, this.mHandler);
      localObject2 = new IntentFilter();
      ((IntentFilter)localObject2).addAction("android.intent.action.USER_SWITCHED");
      this.mContext.registerReceiver(new UserSwitchedReceiver(null), (IntentFilter)localObject2, null, this.mHandler);
      localObject2 = new IntentFilter();
      ((IntentFilter)localObject2).addAction("android.intent.action.DOCK_EVENT");
      this.mContext.registerReceiver(new DockReceiver(null), (IntentFilter)localObject2, null, this.mHandler);
      localObject2 = this.mContext.getContentResolver();
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("screensaver_enabled"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_sleep"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_dock"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("sleep_timeout"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Global.getUriFor("stay_on_while_plugged_in"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.System.getUriFor("screen_brightness"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Global.getUriFor("low_power"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Global.getUriFor("theater_mode_on"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("double_tap_to_wake"), false, this.mSettingsObserver, -1);
      ((ContentResolver)localObject2).registerContentObserver(Settings.Secure.getUriFor("brightness_use_twilight"), false, this.mSettingsObserver, -1);
      IVrManager localIVrManager = (IVrManager)getBinderService("vrmanager");
      try
      {
        localIVrManager.registerListener(this.mVrStateCallbacks);
        this.mButtonBrightnessSettingDefault = paramIAppOpsService.getDefaultButtonBrightness();
        this.mButtonLight = this.mLightsManager.getLight(2);
        ((ContentResolver)localObject2).registerContentObserver(Settings.System.getUriFor("buttons_brightness"), false, this.mSettingsObserver, -1);
        if (DEBUG) {
          Slog.d("PowerManagerService", "system ready!");
        }
        readConfigurationLocked();
        updateSettingsLocked();
        this.mDirty |= 0x100;
        updatePowerStateLocked();
        this.mDozeBlackForAudioList = new ArrayList(Arrays.asList(getContext().getResources().getStringArray(84344842)));
        Slog.v("PowerManagerService", "mDozeBlackForAudioList = " + this.mDozeBlackForAudioList);
        this.mDozeBlackForAudioConfigObserver = new ConfigObserver(this.mContext, this.mHandler, new DozeBlackForAudioConfigUpdater(), PACKAGEMANAGERMENT_CONFIG_NAME);
        this.mDozeBlackForAudioConfigObserver.register();
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(7), 6000L);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.e("PowerManagerService", "Failed to register VR mode state listener: " + localRemoteException);
        }
      }
    }
  }
  
  void uidGoneInternal(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mUidState.delete(paramInt);
      if (this.mDeviceIdleMode) {
        updateWakeLockDisabledStatesLocked();
      }
      return;
    }
  }
  
  protected void updatePowerStateLocked()
  {
    if ((!this.mSystemReady) || (this.mDirty == 0)) {
      return;
    }
    if (!Thread.holdsLock(this.mLock)) {
      Slog.wtf("PowerManagerService", "Power manager lock was not held when calling updatePowerStateLocked");
    }
    Trace.traceBegin(131072L, "updatePowerState");
    try
    {
      updateIsPoweredLocked(this.mDirty);
      updateStayOnLocked(this.mDirty);
      updateScreenBrightnessBoostLocked(this.mDirty);
      long l = SystemClock.uptimeMillis();
      int i = 0;
      int k;
      int j;
      do
      {
        k = this.mDirty;
        j = i | k;
        this.mDirty = 0;
        updateWakeLockSummaryLocked(k);
        updateUserActivitySummaryLocked(l, k);
        i = j;
      } while (updateWakefulnessLocked(k));
      updateDreamLocked(j, updateDisplayPowerStateLocked(j));
      finishWakefulnessChangeIfNeededLocked();
      updateSuspendBlockerLocked();
      return;
    }
    finally
    {
      Trace.traceEnd(131072L);
    }
  }
  
  void updateUidProcStateInternal(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mUidState.put(paramInt1, paramInt2);
      if (this.mDeviceIdleMode) {
        updateWakeLockDisabledStatesLocked();
      }
      return;
    }
  }
  
  private final class BatteryReceiver
    extends BroadcastReceiver
  {
    private BatteryReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-wrap12(PowerManagerService.this);
        return;
      }
    }
  }
  
  private final class BinderService
    extends IPowerManager.Stub
  {
    private BinderService() {}
    
    public void acquireWakeLock(IBinder paramIBinder, int paramInt, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("lock must not be null");
      }
      if (paramString2 == null) {
        throw new IllegalArgumentException("packageName must not be null");
      }
      PowerManager.validateWakeLockParameters(paramInt, paramString1);
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
      if ((paramInt & 0x40) != 0) {
        PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      }
      if ((paramWorkSource != null) && (paramWorkSource.size() != 0)) {
        PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
      }
      for (;;)
      {
        int i = Binder.getCallingUid();
        int j = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        try
        {
          PowerManagerService.-wrap7(PowerManagerService.this, paramIBinder, paramInt, paramString1, paramString2, paramWorkSource, paramString3, i, j);
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        paramWorkSource = null;
      }
    }
    
    public void acquireWakeLockWithUid(IBinder paramIBinder, int paramInt1, String paramString1, String paramString2, int paramInt2)
    {
      int i = paramInt2;
      if (paramInt2 < 0) {
        i = Binder.getCallingUid();
      }
      acquireWakeLock(paramIBinder, paramInt1, paramString1, paramString2, new WorkSource(i), null);
    }
    
    public void boostScreenBrightness(long paramLong)
    {
      if (paramLong > SystemClock.uptimeMillis()) {
        throw new IllegalArgumentException("event time must not be in the future");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap8(PowerManagerService.this, paramLong, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void crash(String paramString)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.REBOOT", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap9(PowerManagerService.this, paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (PowerManagerService.-get3(PowerManagerService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump PowerManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      if (PowerManagerService.this.dynamicallyConfigPowerManagerServiceLogTag(paramFileDescriptor, paramPrintWriter, paramArrayOfString)) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap10(PowerManagerService.this, paramPrintWriter);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void goToSleep(long paramLong, int paramInt1, int paramInt2)
    {
      if (paramLong > SystemClock.uptimeMillis()) {
        throw new IllegalArgumentException("event time must not be in the future");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap11(PowerManagerService.this, paramLong, paramInt1, paramInt2, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isDeviceIdleMode()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.this.isDeviceIdleModeInternal();
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isInteractive()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.-wrap1(PowerManagerService.this);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isLightDeviceIdleMode()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.this.isLightDeviceIdleModeInternal();
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isPowerSaveMode()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.-wrap2(PowerManagerService.this);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isScreenBrightnessBoosted()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.-wrap3(PowerManagerService.this);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isWakeLockLevelSupported(int paramInt)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = PowerManagerService.-wrap4(PowerManagerService.this, paramInt);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void nap(long paramLong)
    {
      if (paramLong > SystemClock.uptimeMillis()) {
        throw new IllegalArgumentException("event time must not be in the future");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap18(PowerManagerService.this, paramLong, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void powerHint(int paramInt1, int paramInt2)
    {
      if (!PowerManagerService.-get12(PowerManagerService.this)) {
        return;
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      PowerManagerService.-wrap21(PowerManagerService.this, paramInt1, paramInt2);
    }
    
    public void reboot(boolean paramBoolean1, String paramString, boolean paramBoolean2)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.REBOOT", null);
      if (("recovery".equals(paramString)) || ("recovery-update".equals(paramString))) {
        PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.RECOVERY", null);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap34(PowerManagerService.this, 1, paramBoolean1, paramString, paramBoolean2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void rebootSafeMode(boolean paramBoolean1, boolean paramBoolean2)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.REBOOT", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap34(PowerManagerService.this, 2, paramBoolean1, "safemode", paramBoolean2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void releaseWakeLock(IBinder paramIBinder, int paramInt)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("lock must not be null");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap22(PowerManagerService.this, paramIBinder, paramInt);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setAttentionLight(boolean paramBoolean, int paramInt)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap25(PowerManagerService.this, paramBoolean, paramInt);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean setPowerSaveMode(boolean paramBoolean)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        paramBoolean = PowerManagerService.-wrap5(PowerManagerService.this, paramBoolean);
        return paramBoolean;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setStayOnSetting(int paramInt)
    {
      int i = Binder.getCallingUid();
      if ((i != 0) && (!Settings.checkAndNoteWriteSettingsOperation(PowerManagerService.-get3(PowerManagerService.this), i, Settings.getPackageNameForUid(PowerManagerService.-get3(PowerManagerService.this), i), true))) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.this.setStayOnSettingInternal(paramInt);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float paramFloat)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap30(PowerManagerService.this, paramFloat);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setTemporaryScreenBrightnessSettingOverride(int paramInt)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap31(PowerManagerService.this, paramInt);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void shutdown(boolean paramBoolean1, String paramString, boolean paramBoolean2)
    {
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.REBOOT", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap34(PowerManagerService.this, 0, paramBoolean1, paramString, paramBoolean2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void updateBlockedUids(int paramInt, boolean paramBoolean)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-get13(PowerManagerService.this).processPmsBlockedUid(paramInt, paramBoolean, PowerManagerService.this.mWakeLocks);
        return;
      }
    }
    
    public void updateWakeLockUids(IBinder paramIBinder, int[] paramArrayOfInt)
    {
      Object localObject = null;
      if (paramArrayOfInt != null)
      {
        WorkSource localWorkSource = new WorkSource();
        int i = 0;
        for (;;)
        {
          localObject = localWorkSource;
          if (i >= paramArrayOfInt.length) {
            break;
          }
          localWorkSource.add(paramArrayOfInt[i]);
          i += 1;
        }
      }
      updateWakeLockWorkSource(paramIBinder, (WorkSource)localObject, null);
    }
    
    public void updateWakeLockWorkSource(IBinder paramIBinder, WorkSource paramWorkSource, String paramString)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("lock must not be null");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
      if ((paramWorkSource != null) && (paramWorkSource.size() != 0)) {
        PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
      }
      for (;;)
      {
        int i = Binder.getCallingUid();
        l = Binder.clearCallingIdentity();
        try
        {
          PowerManagerService.-wrap35(PowerManagerService.this, paramIBinder, paramWorkSource, paramString, i);
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        paramWorkSource = null;
      }
    }
    
    public void userActivity(long paramLong, int paramInt1, int paramInt2)
    {
      long l = SystemClock.uptimeMillis();
      if ((PowerManagerService.-get3(PowerManagerService.this).checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) && (PowerManagerService.-get3(PowerManagerService.this).checkCallingOrSelfPermission("android.permission.USER_ACTIVITY") != 0)) {
        synchronized (PowerManagerService.-get9(PowerManagerService.this))
        {
          if (l >= PowerManagerService.-get8(PowerManagerService.this) + 300000L)
          {
            PowerManagerService.-set3(PowerManagerService.this, l);
            Slog.w("PowerManagerService", "Ignoring call to PowerManager.userActivity() because the caller does not have DEVICE_POWER or USER_ACTIVITY permission.  Please fix your app!   pid=" + Binder.getCallingPid() + " uid=" + Binder.getCallingUid());
          }
          return;
        }
      }
      if (paramLong > l) {
        throw new IllegalArgumentException("event time must not be in the future");
      }
      int i = Binder.getCallingUid();
      l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap36(PowerManagerService.this, paramLong, paramInt1, paramInt2, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void wakeUp(long paramLong, String paramString1, String paramString2)
    {
      if (paramLong > SystemClock.uptimeMillis()) {
        throw new IllegalArgumentException("event time must not be in the future");
      }
      PowerManagerService.-get3(PowerManagerService.this).enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        PowerManagerService.-wrap37(PowerManagerService.this, paramLong, paramString1, i, paramString2, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private final class DockReceiver
    extends BroadcastReceiver
  {
    private DockReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        int i = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
        if (PowerManagerService.-get7(PowerManagerService.this) != i)
        {
          PowerManagerService.-set2(PowerManagerService.this, i);
          paramIntent = PowerManagerService.this;
          paramIntent.mDirty |= 0x400;
          PowerManagerService.this.updatePowerStateLocked();
        }
        return;
      }
    }
  }
  
  class DozeBlackForAudioConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    DozeBlackForAudioConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      PowerManagerService.-wrap23(PowerManagerService.this, paramJSONArray);
    }
  }
  
  private final class DreamReceiver
    extends BroadcastReceiver
  {
    private DreamReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-wrap24(PowerManagerService.this);
        return;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  public static @interface HaltMode {}
  
  private final class LocalService
    extends PowerManagerInternal
  {
    private LocalService() {}
    
    public boolean getLowPowerModeEnabled()
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        boolean bool = PowerManagerService.-get10(PowerManagerService.this);
        return bool;
      }
    }
    
    public void powerHint(int paramInt1, int paramInt2)
    {
      PowerManagerService.-wrap21(PowerManagerService.this, paramInt1, paramInt2);
    }
    
    public void registerLowPowerModeObserver(PowerManagerInternal.LowPowerModeListener paramLowPowerModeListener)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-get11(PowerManagerService.this).add(paramLowPowerModeListener);
        return;
      }
    }
    
    public void setButtonBrightnessOverrideFromWindowManager(int paramInt) {}
    
    public void setDeviceIdleAggressive(boolean paramBoolean)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-set1(PowerManagerService.this, paramBoolean);
        return;
      }
    }
    
    public boolean setDeviceIdleMode(boolean paramBoolean)
    {
      return PowerManagerService.this.setDeviceIdleModeInternal(paramBoolean);
    }
    
    public void setDeviceIdleState(int paramInt)
    {
      PowerManagerService.this.setDeviceIdleStateInternal(paramInt);
    }
    
    public void setDeviceIdleTempWhitelist(int[] paramArrayOfInt)
    {
      PowerManagerService.this.setDeviceIdleTempWhitelistInternal(paramArrayOfInt);
    }
    
    public void setDeviceIdleWhitelist(int[] paramArrayOfInt)
    {
      PowerManagerService.this.setDeviceIdleWhitelistInternal(paramArrayOfInt);
    }
    
    public void setDozeOverrideFromDreamManager(int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      switch (paramInt1)
      {
      default: 
        i = 0;
      }
      if (paramInt2 >= -1)
      {
        paramInt1 = paramInt2;
        if (paramInt2 <= 255) {}
      }
      else
      {
        paramInt1 = -1;
      }
      PowerManagerService.-wrap26(PowerManagerService.this, i, paramInt1);
    }
    
    public boolean setLightDeviceIdleMode(boolean paramBoolean)
    {
      return PowerManagerService.this.setLightDeviceIdleModeInternal(paramBoolean);
    }
    
    public void setMaximumScreenOffTimeoutFromDeviceAdmin(int paramInt)
    {
      PowerManagerService.this.setMaximumScreenOffTimeoutFromDeviceAdminInternal(paramInt);
    }
    
    public void setScreenBrightnessOverrideFromWindowManager(int paramInt)
    {
      int i;
      if (paramInt >= -1)
      {
        i = paramInt;
        if (paramInt <= 255) {}
      }
      else
      {
        i = -1;
      }
      PowerManagerService.-wrap29(PowerManagerService.this, i);
    }
    
    public void setUserActivityTimeoutOverrideFromWindowManager(long paramLong)
    {
      PowerManagerService.-wrap32(PowerManagerService.this, paramLong);
    }
    
    public void setUserInactiveOverrideFromWindowManager()
    {
      PowerManagerService.-wrap33(PowerManagerService.this);
    }
    
    public void uidGone(int paramInt)
    {
      PowerManagerService.this.uidGoneInternal(paramInt);
    }
    
    public void updateUidProcState(int paramInt1, int paramInt2)
    {
      PowerManagerService.this.updateUidProcStateInternal(paramInt1, paramInt2);
    }
  }
  
  private final class PowerManagerHandler
    extends Handler
  {
    public PowerManagerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 6: 
      default: 
        return;
      case 1: 
        PowerManagerService.-wrap16(PowerManagerService.this);
        return;
      case 2: 
        PowerManagerService.-wrap13(PowerManagerService.this);
        return;
      case 3: 
        PowerManagerService.-wrap14(PowerManagerService.this);
        return;
      case 4: 
        PowerManagerService.this.checkForLongWakeLocks();
        return;
      case 5: 
        paramMessage = (String)paramMessage.obj;
        PowerManagerService.-get2(PowerManagerService.this).forceStopPackageAsUser(paramMessage, 0);
        return;
      }
      paramMessage = new ConfigGrabber(PowerManagerService.-get3(PowerManagerService.this), PowerManagerService.-get1());
      PowerManagerService.-wrap23(PowerManagerService.this, paramMessage.grabConfig());
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri arg2)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        PowerManagerService.-wrap15(PowerManagerService.this);
        return;
      }
    }
  }
  
  private final class SuspendBlockerImpl
    implements SuspendBlocker
  {
    private final String mName;
    private int mReferenceCount;
    private final String mTraceName;
    
    public SuspendBlockerImpl(String paramString)
    {
      this.mName = paramString;
      this.mTraceName = ("SuspendBlocker (" + paramString + ")");
    }
    
    public void acquire()
    {
      try
      {
        this.mReferenceCount += 1;
        if (this.mReferenceCount == 1)
        {
          if (PowerManagerService.-get0()) {
            Slog.d("PowerManagerService", "Acquiring suspend blocker \"" + this.mName + "\".");
          }
          Trace.asyncTraceBegin(131072L, this.mTraceName, 0);
          PowerManagerService.-wrap19(this.mName);
        }
        return;
      }
      finally {}
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.mReferenceCount != 0)
        {
          Slog.wtf("PowerManagerService", "Suspend blocker \"" + this.mName + "\" was finalized without being released!");
          this.mReferenceCount = 0;
          PowerManagerService.-wrap20(this.mName);
          Trace.asyncTraceEnd(131072L, this.mTraceName, 0);
        }
        return;
      }
      finally
      {
        super.finalize();
      }
    }
    
    /* Error */
    public void release()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: aload_0
      //   4: getfield 46	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mReferenceCount	I
      //   7: iconst_1
      //   8: isub
      //   9: putfield 46	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mReferenceCount	I
      //   12: aload_0
      //   13: getfield 46	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mReferenceCount	I
      //   16: ifne +63 -> 79
      //   19: invokestatic 50	com/android/server/power/PowerManagerService:-get0	()Z
      //   22: ifeq +36 -> 58
      //   25: ldc 52
      //   27: new 27	java/lang/StringBuilder
      //   30: dup
      //   31: invokespecial 28	java/lang/StringBuilder:<init>	()V
      //   34: ldc 96
      //   36: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   39: aload_0
      //   40: getfield 25	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mName	Ljava/lang/String;
      //   43: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   46: ldc 56
      //   48: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   51: invokevirtual 40	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   54: invokestatic 62	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   57: pop
      //   58: aload_0
      //   59: getfield 25	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mName	Ljava/lang/String;
      //   62: invokestatic 87	com/android/server/power/PowerManagerService:-wrap20	(Ljava/lang/String;)V
      //   65: ldc2_w 63
      //   68: aload_0
      //   69: getfield 42	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mTraceName	Ljava/lang/String;
      //   72: iconst_0
      //   73: invokestatic 90	android/os/Trace:asyncTraceEnd	(JLjava/lang/String;I)V
      //   76: aload_0
      //   77: monitorexit
      //   78: return
      //   79: aload_0
      //   80: getfield 46	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mReferenceCount	I
      //   83: ifge -7 -> 76
      //   86: ldc 52
      //   88: new 27	java/lang/StringBuilder
      //   91: dup
      //   92: invokespecial 28	java/lang/StringBuilder:<init>	()V
      //   95: ldc 79
      //   97: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   100: aload_0
      //   101: getfield 25	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mName	Ljava/lang/String;
      //   104: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   107: ldc 98
      //   109: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   112: invokevirtual 40	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   115: new 77	java/lang/Throwable
      //   118: dup
      //   119: invokespecial 99	java/lang/Throwable:<init>	()V
      //   122: invokestatic 102	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   125: pop
      //   126: aload_0
      //   127: iconst_0
      //   128: putfield 46	com/android/server/power/PowerManagerService$SuspendBlockerImpl:mReferenceCount	I
      //   131: goto -55 -> 76
      //   134: astore_1
      //   135: aload_0
      //   136: monitorexit
      //   137: aload_1
      //   138: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	139	0	this	SuspendBlockerImpl
      //   134	4	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	58	134	finally
      //   58	76	134	finally
      //   79	131	134	finally
    }
    
    public String toString()
    {
      try
      {
        String str = this.mName + ": ref count=" + this.mReferenceCount;
        return str;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
  
  private final class UserSwitchedReceiver
    extends BroadcastReceiver
  {
    private UserSwitchedReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (PowerManagerService.-get9(PowerManagerService.this))
      {
        Slog.d("PowerManagerService", "UserSwitchedReceiver On PowerManagerService");
        PowerManagerService.-wrap15(PowerManagerService.this);
        return;
      }
    }
  }
  
  protected final class WakeLock
    implements IBinder.DeathRecipient
  {
    public long mAcquireTime;
    public boolean mDisabled;
    public int mFlags;
    public String mHistoryTag;
    public final IBinder mLock;
    public boolean mLongBeforeIdle;
    public boolean mNotifiedAcquired;
    public boolean mNotifiedLong;
    public final int mOwnerPid;
    public final int mOwnerUid;
    public final String mPackageName;
    public String mTag;
    public WorkSource mWorkSource;
    
    public WakeLock(IBinder paramIBinder, int paramInt1, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3, int paramInt2, int paramInt3)
    {
      this.mLock = paramIBinder;
      this.mFlags = paramInt1;
      this.mTag = paramString1;
      this.mPackageName = paramString2;
      this.mWorkSource = PowerManagerService.-wrap0(paramWorkSource);
      this.mHistoryTag = paramString3;
      this.mOwnerUid = paramInt2;
      this.mOwnerPid = paramInt3;
      this.mLongBeforeIdle = false;
    }
    
    private String getLockFlagsString()
    {
      String str1 = "";
      if ((this.mFlags & 0x10000000) != 0) {
        str1 = "" + " ACQUIRE_CAUSES_WAKEUP";
      }
      String str2 = str1;
      if ((this.mFlags & 0x20000000) != 0) {
        str2 = str1 + " ON_AFTER_RELEASE";
      }
      return str2;
    }
    
    private String getLockLevelString()
    {
      switch (this.mFlags & 0xFFFF)
      {
      default: 
        return "???                           ";
      case 26: 
        return "FULL_WAKE_LOCK                ";
      case 10: 
        return "SCREEN_BRIGHT_WAKE_LOCK       ";
      case 6: 
        return "SCREEN_DIM_WAKE_LOCK          ";
      case 1: 
        return "PARTIAL_WAKE_LOCK             ";
      case 32: 
        return "PROXIMITY_SCREEN_OFF_WAKE_LOCK";
      case 64: 
        return "DOZE_WAKE_LOCK                ";
      }
      return "DRAW_WAKE_LOCK                ";
    }
    
    public void binderDied()
    {
      PowerManagerService.-wrap17(PowerManagerService.this, this);
    }
    
    public boolean hasSameProperties(int paramInt1, String paramString, WorkSource paramWorkSource, int paramInt2, int paramInt3)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mFlags == paramInt1)
      {
        bool1 = bool2;
        if (this.mTag.equals(paramString))
        {
          bool1 = bool2;
          if (hasSameWorkSource(paramWorkSource))
          {
            bool1 = bool2;
            if (this.mOwnerUid == paramInt2)
            {
              bool1 = bool2;
              if (this.mOwnerPid == paramInt3) {
                bool1 = true;
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public boolean hasSameWorkSource(WorkSource paramWorkSource)
    {
      return Objects.equal(this.mWorkSource, paramWorkSource);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getLockLevelString());
      localStringBuilder.append(" '");
      localStringBuilder.append(this.mTag);
      localStringBuilder.append("'");
      localStringBuilder.append(getLockFlagsString());
      if (this.mDisabled) {
        localStringBuilder.append(" DISABLED");
      }
      if (this.mNotifiedAcquired)
      {
        localStringBuilder.append(" ACQ=");
        TimeUtils.formatDuration(this.mAcquireTime - SystemClock.uptimeMillis(), localStringBuilder);
      }
      if (this.mNotifiedLong) {
        localStringBuilder.append(" LONG");
      }
      localStringBuilder.append(" (uid=");
      localStringBuilder.append(this.mOwnerUid);
      if (this.mOwnerPid != 0)
      {
        localStringBuilder.append(" pid=");
        localStringBuilder.append(this.mOwnerPid);
      }
      if (this.mWorkSource != null)
      {
        localStringBuilder.append(" ws=");
        localStringBuilder.append(this.mWorkSource);
      }
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
    
    public void updateProperties(int paramInt1, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3, int paramInt2, int paramInt3)
    {
      if (!this.mPackageName.equals(paramString2)) {
        throw new IllegalStateException("Existing wake lock package name changed: " + this.mPackageName + " to " + paramString2);
      }
      if (this.mOwnerUid != paramInt2) {
        throw new IllegalStateException("Existing wake lock uid changed: " + this.mOwnerUid + " to " + paramInt2);
      }
      if (this.mOwnerPid != paramInt3) {
        throw new IllegalStateException("Existing wake lock pid changed: " + this.mOwnerPid + " to " + paramInt3);
      }
      this.mFlags = paramInt1;
      this.mTag = paramString1;
      updateWorkSource(paramWorkSource);
      this.mHistoryTag = paramString3;
    }
    
    public void updateWorkSource(WorkSource paramWorkSource)
    {
      this.mWorkSource = PowerManagerService.-wrap0(paramWorkSource);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/PowerManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */