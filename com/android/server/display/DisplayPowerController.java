package com.android.server.display;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.ColorBalanceManager;
import android.hardware.display.DisplayManagerInternal.DisplayPowerCallbacks;
import android.hardware.display.DisplayManagerInternal.DisplayPowerRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.Settings.System;
import android.util.MathUtils;
import android.util.Slog;
import android.util.Spline;
import android.util.TimeUtils;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.ScreenOnListener;
import com.android.internal.app.IBatteryStats;
import com.android.server.LocalServices;
import com.android.server.am.BatteryStatsService;
import com.android.server.power.PowerManagerService;
import java.io.PrintWriter;

final class DisplayPowerController
  implements AutomaticBrightnessController.Callbacks
{
  private static final int BRIGHTNESS_RAMP_RATE_BRIGHTEN = 120;
  private static final int BRIGHTNESS_RAMP_RATE_DARKEN = 10;
  private static final int BRIGHTNESS_RAMP_RATE_SCREENON = 120;
  private static final int BRIGHTNESS_RAMP_RATE_SLOW = 40;
  private static final int COLOR_FADE_OFF_ANIMATION_DURATION_MILLIS = 200;
  private static final int COLOR_FADE_ON_ANIMATION_DURATION_MILLIS = 250;
  static boolean DEBUG = false;
  public static boolean DEBUG_ONEPLUS = false;
  private static final boolean DEBUG_PRETEND_PROXIMITY_SENSOR_ABSENT = false;
  private static final int MSG_PROXIMITY_SENSOR_DEBOUNCED = 2;
  private static final int MSG_SCREEN_ON_BRIGHTNESS_BOOST = 4;
  private static final int MSG_SCREEN_ON_UNBLOCKED = 3;
  private static final int MSG_UPDATE_POWER_STATE = 1;
  private static final int PROXIMITY_NEGATIVE = 0;
  private static final int PROXIMITY_POSITIVE = 1;
  private static final int PROXIMITY_SENSOR_NEGATIVE_DEBOUNCE_DELAY = 10;
  private static final int PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY = 0;
  private static final int PROXIMITY_UNKNOWN = -1;
  private static final int REPORTED_TO_POLICY_SCREEN_OFF = 0;
  private static final int REPORTED_TO_POLICY_SCREEN_ON = 2;
  private static final int REPORTED_TO_POLICY_SCREEN_TURNING_ON = 1;
  private static final long SCREENON_BRIGHTNESS_BOOST_TIMEOUT = 5000L;
  private static final int SCREEN_DIM_MINIMUM_REDUCTION = 10;
  private static final String SCREEN_ON_BLOCKED_TRACE_NAME = "Screen on blocked";
  private static final String TAG = "DisplayPowerController";
  private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0F;
  private static final boolean USE_COLOR_FADE_ON_ANIMATION = false;
  private static ColorBalanceManager mCBM;
  public static boolean mQuickDarkToBright;
  private static boolean mQuicklyApplyDimming;
  private final boolean mAllowAutoBrightnessWhileDozingConfig;
  private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator) {}
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      DisplayPowerController.-wrap3(DisplayPowerController.this);
    }
    
    public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
    
    public void onAnimationStart(Animator paramAnonymousAnimator) {}
  };
  private boolean mAppliedAutoBrightness;
  private boolean mAppliedDimming;
  private boolean mAppliedLowPower;
  private AutomaticBrightnessController mAutomaticBrightnessController;
  private final IBatteryStats mBatteryStats;
  private final DisplayBlanker mBlanker;
  private final int mBrightnessRampRateFast;
  private final DisplayManagerInternal.DisplayPowerCallbacks mCallbacks;
  private final Runnable mCleanListener = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-wrap3(DisplayPowerController.this);
    }
  };
  private boolean mColorFadeFadesConfig;
  private ObjectAnimator mColorFadeOffAnimator;
  private ObjectAnimator mColorFadeOnAnimator;
  private final Context mContext;
  private boolean mDisplayReadyLocked;
  private final DisplayControllerHandler mHandler;
  private final Object mLock = new Object();
  private final Runnable mOnProximityNegativeRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-get0(DisplayPowerController.this).onProximityNegative();
      DisplayPowerController.-get0(DisplayPowerController.this).releaseSuspendBlocker();
    }
  };
  private final Runnable mOnProximityNegativeSuspendRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-get0(DisplayPowerController.this).onProximityNegativeForceSuspend();
      DisplayPowerController.-get0(DisplayPowerController.this).releaseSuspendBlocker();
    }
  };
  private final Runnable mOnProximityPositiveRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-get0(DisplayPowerController.this).onProximityPositive();
      DisplayPowerController.-get0(DisplayPowerController.this).releaseSuspendBlocker();
    }
  };
  private final Runnable mOnProximityPositiveSuspendRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-get0(DisplayPowerController.this).onProximityPositiveForceSuspend();
      DisplayPowerController.-get0(DisplayPowerController.this).releaseSuspendBlocker();
    }
  };
  private final Runnable mOnStateChangedRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerController.-get0(DisplayPowerController.this).onStateChanged();
      DisplayPowerController.-get0(DisplayPowerController.this).releaseSuspendBlocker();
    }
  };
  private int mPendingProximity = -1;
  private long mPendingProximityDebounceTime = -1L;
  private boolean mPendingRequestChangedLocked;
  private DisplayManagerInternal.DisplayPowerRequest mPendingRequestLocked;
  private boolean mPendingScreenOff;
  private ScreenOnUnblocker mPendingScreenOnUnblocker;
  private boolean mPendingUpdatePowerStateLocked;
  private boolean mPendingWaitForNegativeProximityLocked;
  private DisplayManagerInternal.DisplayPowerRequest mPowerRequest;
  private DisplayPowerState mPowerState;
  private Boolean mPreScreeeStatus;
  private int mProximity = -1;
  private boolean mProximityEventHandled = true;
  private Sensor mProximitySensor;
  private boolean mProximitySensorEnabled;
  private final SensorEventListener mProximitySensorListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      int i = 0;
      long l;
      float f;
      if (DisplayPowerController.-get4(DisplayPowerController.this))
      {
        l = SystemClock.uptimeMillis();
        f = paramAnonymousSensorEvent.values[0];
        if ((f < 0.0F) || (f >= DisplayPowerController.-get5(DisplayPowerController.this))) {
          break label124;
        }
      }
      label124:
      for (boolean bool = true;; bool = false)
      {
        if (DisplayPowerController.DEBUG_ONEPLUS) {
          Slog.d("DisplayPowerController", "P-Sensor Changed: distance = " + f + ", positive = " + bool);
        }
        DisplayPowerController.-wrap2(DisplayPowerController.this, l, bool);
        paramAnonymousSensorEvent = DisplayPowerController.-get1(DisplayPowerController.this).getContentResolver();
        if (bool) {
          i = 1;
        }
        Settings.System.putInt(paramAnonymousSensorEvent, "display_ctrl_psensor_positive", i);
        return;
      }
    }
  };
  private float mProximityThreshold;
  private final RampAnimator.Listener mRampAnimatorListener = new RampAnimator.Listener()
  {
    public void onAnimationEnd()
    {
      DisplayPowerController.-wrap3(DisplayPowerController.this);
    }
  };
  private int mReportedScreenStateToPolicy;
  private final int mScreenBrightnessDarkConfig;
  private final int mScreenBrightnessDimConfig;
  private final int mScreenBrightnessDozeConfig;
  private RampAnimator<DisplayPowerState> mScreenBrightnessRampAnimator;
  private final int mScreenBrightnessRangeMaximum;
  private final int mScreenBrightnessRangeMinimum;
  private boolean mScreenOffBecauseOfProximity;
  private long mScreenOnBlockStartRealTime;
  private int mScreenState;
  private final SensorManager mSensorManager;
  private boolean mUnfinishedBusiness;
  private boolean mUseSoftwareAutoBrightnessConfig;
  private boolean mWaitingForNegativeProximity;
  private String mWakingUpReason;
  private final WindowManagerPolicy mWindowManagerPolicy;
  private boolean useProximityForceSuspend = false;
  
  static
  {
    if (DisplayPowerController.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      DEBUG = false;
      DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
      mQuicklyApplyDimming = false;
      mQuickDarkToBright = false;
      return;
    }
  }
  
  public DisplayPowerController(Context paramContext, DisplayManagerInternal.DisplayPowerCallbacks paramDisplayPowerCallbacks, Handler paramHandler, SensorManager paramSensorManager, DisplayBlanker paramDisplayBlanker)
  {
    this.mHandler = new DisplayControllerHandler(paramHandler.getLooper());
    this.mCallbacks = paramDisplayPowerCallbacks;
    mCBM = new ColorBalanceManager(paramContext);
    this.mBatteryStats = BatteryStatsService.getService();
    this.mSensorManager = paramSensorManager;
    this.mWindowManagerPolicy = ((WindowManagerPolicy)LocalServices.getService(WindowManagerPolicy.class));
    this.mBlanker = paramDisplayBlanker;
    this.mContext = paramContext;
    mQuicklyApplyDimming = false;
    this.mScreenState = -1;
    mQuickDarkToBright = false;
    paramContext = paramContext.getResources();
    int i = clampAbsoluteBrightness(paramContext.getInteger(17694820));
    this.mScreenBrightnessDozeConfig = clampAbsoluteBrightness(paramContext.getInteger(17694823));
    this.mScreenBrightnessDimConfig = clampAbsoluteBrightness(paramContext.getInteger(17694828));
    this.mScreenBrightnessDarkConfig = clampAbsoluteBrightness(paramContext.getInteger(17694829));
    if (this.mScreenBrightnessDarkConfig > this.mScreenBrightnessDimConfig) {
      Slog.w("DisplayPowerController", "Expected config_screenBrightnessDark (" + this.mScreenBrightnessDarkConfig + ") to be less than or equal to " + "config_screenBrightnessDim (" + this.mScreenBrightnessDimConfig + ").");
    }
    if (this.mScreenBrightnessDarkConfig > this.mScreenBrightnessDimConfig) {
      Slog.w("DisplayPowerController", "Expected config_screenBrightnessDark (" + this.mScreenBrightnessDarkConfig + ") to be less than or equal to " + "config_screenBrightnessSettingMinimum (" + i + ").");
    }
    int j = Math.min(Math.min(i, this.mScreenBrightnessDimConfig), this.mScreenBrightnessDarkConfig);
    this.mScreenBrightnessRangeMaximum = 255;
    this.mUseSoftwareAutoBrightnessConfig = paramContext.getBoolean(17956900);
    this.mAllowAutoBrightnessWhileDozingConfig = paramContext.getBoolean(17956942);
    this.mBrightnessRampRateFast = paramContext.getInteger(17694786);
    int m = paramContext.getInteger(17694826);
    long l1 = paramContext.getInteger(17694824);
    long l2 = paramContext.getInteger(84475904);
    long l3 = paramContext.getInteger(17694825);
    boolean bool = paramContext.getBoolean(17956943);
    int n = paramContext.getInteger(17694827);
    float f1 = paramContext.getFraction(18022401, 1, 1);
    i = j;
    int i1;
    float f2;
    Spline localSpline;
    if (this.mUseSoftwareAutoBrightnessConfig)
    {
      paramDisplayPowerCallbacks = paramContext.getIntArray(17236010);
      paramDisplayBlanker = paramContext.getIntArray(17236011);
      i1 = paramContext.getInteger(17694830);
      f2 = paramContext.getFraction(18022402, 1, 1);
      localSpline = createAutoBrightnessSpline(paramDisplayPowerCallbacks, paramDisplayBlanker);
      if (localSpline != null) {
        break label738;
      }
      Slog.e("DisplayPowerController", "Error in config.xml.  config_autoBrightnessLcdBacklightValues (size " + paramDisplayBlanker.length + ") " + "must be monotic and have exactly one more entry than " + "config_autoBrightnessLevels (size " + paramDisplayPowerCallbacks.length + ") " + "which must be strictly increasing.  " + "Auto-brightness will be disabled.");
      this.mUseSoftwareAutoBrightnessConfig = false;
      i = j;
    }
    for (;;)
    {
      this.mScreenBrightnessRangeMinimum = i;
      this.mColorFadeFadesConfig = paramContext.getBoolean(17956905);
      this.mProximitySensor = this.mSensorManager.getDefaultSensor(8);
      if (this.mProximitySensor != null) {
        this.mProximityThreshold = Math.min(this.mProximitySensor.getMaximumRange(), 5.0F);
      }
      return;
      label738:
      int k = clampAbsoluteBrightness(paramDisplayBlanker[0]);
      if (this.mScreenBrightnessDarkConfig > k) {
        Slog.w("DisplayPowerController", "config_screenBrightnessDark (" + this.mScreenBrightnessDarkConfig + ") should be less than or equal to the first value of " + "config_autoBrightnessLcdBacklightValues (" + k + ").");
      }
      i = j;
      if (k < j) {
        i = k;
      }
      this.mAutomaticBrightnessController = new AutomaticBrightnessController(this, paramHandler.getLooper(), this.mContext, paramSensorManager, localSpline, i1, i, this.mScreenBrightnessRangeMaximum, f2, m, l1, l2, l3, bool, n, f1);
    }
  }
  
  private void animateScreenBrightness(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Slog.d("DisplayPowerController", "Animating brightness: target=" + paramInt1 + ", rate=" + paramInt2);
    }
    if (this.mScreenBrightnessRampAnimator.animateTo(paramInt1, paramInt2)) {}
    try
    {
      this.mBatteryStats.noteScreenBrightness(paramInt1);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void animateScreenStateChange(int paramInt, boolean paramBoolean)
  {
    int i = 2;
    if ((this.mColorFadeOnAnimator.isStarted()) || (this.mColorFadeOffAnimator.isStarted()))
    {
      if ("com.android.systemui:UnlockStart".equals(this.mWakingUpReason))
      {
        Slog.d("DisplayPowerController", "fingerprint unlock, do not stop color fade");
        return;
      }
      if (paramInt != 2) {
        return;
      }
      this.mPendingScreenOff = false;
    }
    if ((this.mPendingScreenOff) && (paramInt != 1))
    {
      setScreenState(1);
      this.mPendingScreenOff = false;
      this.mPowerState.dismissColorFadeResources();
    }
    if (paramInt == 2)
    {
      if (!setScreenState(2)) {
        return;
      }
      this.mPowerState.setColorFadeLevel(1.0F);
      this.mPowerState.dismissColorFade();
      return;
    }
    if (paramInt == 3)
    {
      if ((this.mScreenBrightnessRampAnimator.isAnimating()) && (this.mPowerState.getScreenState() == 2)) {
        return;
      }
      if (!setScreenState(3)) {
        return;
      }
      this.mPowerState.setColorFadeLevel(1.0F);
      this.mPowerState.dismissColorFade();
      return;
    }
    if (paramInt == 4)
    {
      if ((this.mScreenBrightnessRampAnimator.isAnimating()) && (this.mPowerState.getScreenState() != 4)) {
        return;
      }
      if (this.mPowerState.getScreenState() != 4)
      {
        if (!setScreenState(3)) {
          return;
        }
        setScreenState(4);
      }
      this.mPowerState.setColorFadeLevel(1.0F);
      this.mPowerState.dismissColorFade();
      return;
    }
    this.mPendingScreenOff = true;
    if (this.mPowerState.getColorFadeLevel() == 0.0F)
    {
      setScreenState(1);
      this.mPendingScreenOff = false;
      this.mPowerState.dismissColorFadeResources();
      return;
    }
    if (paramBoolean)
    {
      DisplayPowerState localDisplayPowerState = this.mPowerState;
      Context localContext = this.mContext;
      if (this.mColorFadeFadesConfig) {}
      for (paramInt = i; (localDisplayPowerState.prepareColorFade(localContext, paramInt)) && (this.mPowerState.getScreenState() != 1); paramInt = 1)
      {
        this.mColorFadeOffAnimator.start();
        return;
      }
    }
    this.mColorFadeOffAnimator.end();
  }
  
  private void blockScreenOn()
  {
    if (this.mPendingScreenOnUnblocker == null)
    {
      Trace.asyncTraceBegin(131072L, "Screen on blocked", 0);
      this.mPendingScreenOnUnblocker = new ScreenOnUnblocker(null);
      this.mScreenOnBlockStartRealTime = SystemClock.elapsedRealtime();
      Slog.i("DisplayPowerController", "Blocking screen on until initial contents have been drawn.");
    }
  }
  
  private static int clampAbsoluteBrightness(int paramInt)
  {
    return MathUtils.constrain(paramInt, 0, 255);
  }
  
  private int clampScreenBrightness(int paramInt)
  {
    return MathUtils.constrain(paramInt, this.mScreenBrightnessRangeMinimum, this.mScreenBrightnessRangeMaximum);
  }
  
  private void clearPendingProximityDebounceTime()
  {
    if (this.mPendingProximityDebounceTime >= 0L)
    {
      this.mPendingProximityDebounceTime = -1L;
      this.mCallbacks.releaseSuspendBlocker();
    }
  }
  
  private static Spline createAutoBrightnessSpline(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((paramArrayOfInt1 == null) || (paramArrayOfInt1.length == 0)) {}
    while ((paramArrayOfInt2 == null) || (paramArrayOfInt2.length == 0))
    {
      Slog.e("DisplayPowerController", "Could not create auto-brightness spline.");
      return null;
    }
    try
    {
      int j = paramArrayOfInt2.length;
      float[] arrayOfFloat1 = new float[j];
      float[] arrayOfFloat2 = new float[j];
      arrayOfFloat2[0] = normalizeAbsoluteBrightness(paramArrayOfInt2[0]);
      int i = 1;
      while (i < j)
      {
        arrayOfFloat1[i] = paramArrayOfInt1[(i - 1)];
        arrayOfFloat2[i] = normalizeAbsoluteBrightness(paramArrayOfInt2[i]);
        i += 1;
      }
      paramArrayOfInt2 = Spline.createSpline(arrayOfFloat1, arrayOfFloat2);
      if (DEBUG)
      {
        Slog.d("DisplayPowerController", "Auto-brightness spline: " + paramArrayOfInt2);
        for (float f = 1.0F; f < paramArrayOfInt1[(paramArrayOfInt1.length - 1)] * 1.25F; f *= 1.25F) {
          Slog.d("DisplayPowerController", String.format("  %7.1f: %7.1f", new Object[] { Float.valueOf(f), Float.valueOf(paramArrayOfInt2.interpolate(f)) }));
        }
      }
      return paramArrayOfInt2;
    }
    catch (IllegalArgumentException paramArrayOfInt1)
    {
      Slog.e("DisplayPowerController", "Could not create auto-brightness spline.", paramArrayOfInt1);
    }
    return null;
  }
  
  private void debounceProximitySensor()
  {
    if ((this.mProximitySensorEnabled) && (this.mPendingProximity != -1) && (this.mPendingProximityDebounceTime >= 0L))
    {
      long l = SystemClock.uptimeMillis();
      if (this.mPendingProximityDebounceTime <= l)
      {
        this.mProximity = this.mPendingProximity;
        this.mProximityEventHandled = false;
        updatePowerState();
        clearPendingProximityDebounceTime();
      }
    }
    else
    {
      return;
    }
    Message localMessage = this.mHandler.obtainMessage(2);
    localMessage.setAsynchronous(true);
    this.mHandler.sendMessageAtTime(localMessage, this.mPendingProximityDebounceTime);
  }
  
  private void dumpLocal(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println();
    paramPrintWriter.println("Display Power Controller Thread State:");
    paramPrintWriter.println("  mPowerRequest=" + this.mPowerRequest);
    paramPrintWriter.println("  mWaitingForNegativeProximity=" + this.mWaitingForNegativeProximity);
    paramPrintWriter.println("  mProximitySensor=" + this.mProximitySensor);
    paramPrintWriter.println("  mProximitySensorEnabled=" + this.mProximitySensorEnabled);
    paramPrintWriter.println("  mProximityThreshold=" + this.mProximityThreshold);
    paramPrintWriter.println("  mProximity=" + proximityToString(this.mProximity));
    paramPrintWriter.println("  mPendingProximity=" + proximityToString(this.mPendingProximity));
    paramPrintWriter.println("  mPendingProximityDebounceTime=" + TimeUtils.formatUptime(this.mPendingProximityDebounceTime));
    paramPrintWriter.println("  mScreenOffBecauseOfProximity=" + this.mScreenOffBecauseOfProximity);
    paramPrintWriter.println("  mAppliedAutoBrightness=" + this.mAppliedAutoBrightness);
    paramPrintWriter.println("  mAppliedDimming=" + this.mAppliedDimming);
    paramPrintWriter.println("  mAppliedLowPower=" + this.mAppliedLowPower);
    paramPrintWriter.println("  mPendingScreenOnUnblocker=" + this.mPendingScreenOnUnblocker);
    paramPrintWriter.println("  mPendingScreenOff=" + this.mPendingScreenOff);
    paramPrintWriter.println("  mReportedToPolicy=" + reportedToPolicyToString(this.mReportedScreenStateToPolicy));
    paramPrintWriter.println("  mScreenBrightnessRampAnimator.isAnimating()=" + this.mScreenBrightnessRampAnimator.isAnimating());
    if (this.mColorFadeOnAnimator != null) {
      paramPrintWriter.println("  mColorFadeOnAnimator.isStarted()=" + this.mColorFadeOnAnimator.isStarted());
    }
    if (this.mColorFadeOffAnimator != null) {
      paramPrintWriter.println("  mColorFadeOffAnimator.isStarted()=" + this.mColorFadeOffAnimator.isStarted());
    }
    if (this.mPowerState != null) {
      this.mPowerState.dump(paramPrintWriter);
    }
    if (this.mAutomaticBrightnessController != null) {
      this.mAutomaticBrightnessController.dump(paramPrintWriter);
    }
  }
  
  private void handleProximitySensorEvent(long paramLong, boolean paramBoolean)
  {
    if (this.mProximitySensorEnabled)
    {
      if ((this.mPendingProximity != 0) || (paramBoolean))
      {
        if ((this.mPendingProximity != 1) || (!paramBoolean)) {}
      }
      else {
        return;
      }
      this.mHandler.removeMessages(2);
      if (!paramBoolean) {
        break label61;
      }
      this.mPendingProximity = 1;
      setPendingProximityDebounceTime(0L + paramLong);
    }
    for (;;)
    {
      debounceProximitySensor();
      return;
      label61:
      this.mPendingProximity = 0;
      setPendingProximityDebounceTime(10L + paramLong);
    }
  }
  
  private void initialize()
  {
    this.mPowerState = new DisplayPowerState(this.mBlanker, new ColorFade(0));
    this.mColorFadeOnAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.COLOR_FADE_LEVEL, new float[] { 0.0F, 1.0F });
    this.mColorFadeOnAnimator.setDuration(250L);
    this.mColorFadeOnAnimator.addListener(this.mAnimatorListener);
    this.mColorFadeOffAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.COLOR_FADE_LEVEL, new float[] { 1.0F, 0.0F });
    this.mColorFadeOffAnimator.setDuration(200L);
    this.mColorFadeOffAnimator.addListener(this.mAnimatorListener);
    this.mScreenBrightnessRampAnimator = new RampAnimator(this.mPowerState, DisplayPowerState.SCREEN_BRIGHTNESS);
    this.mScreenBrightnessRampAnimator.setListener(this.mRampAnimatorListener);
    try
    {
      this.mBatteryStats.noteScreenState(this.mPowerState.getScreenState());
      this.mBatteryStats.noteScreenBrightness(this.mPowerState.getScreenBrightness());
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private static float normalizeAbsoluteBrightness(int paramInt)
  {
    return clampAbsoluteBrightness(paramInt) / 255.0F;
  }
  
  private static String proximityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case -1: 
      return "Unknown";
    case 0: 
      return "Negative";
    }
    return "Positive";
  }
  
  private static String reportedToPolicyToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "REPORTED_TO_POLICY_SCREEN_OFF";
    case 1: 
      return "REPORTED_TO_POLICY_SCREEN_TURNING_ON";
    }
    return "REPORTED_TO_POLICY_SCREEN_ON";
  }
  
  private void sendOnProximityNegativeSuspendWithWakelock()
  {
    this.mCallbacks.acquireSuspendBlocker();
    this.mHandler.post(this.mOnProximityNegativeSuspendRunnable);
  }
  
  private void sendOnProximityNegativeWithWakelock()
  {
    this.mCallbacks.acquireSuspendBlocker();
    this.mHandler.post(this.mOnProximityNegativeRunnable);
  }
  
  private void sendOnProximityPositiveSuspendWithWakelock()
  {
    this.mCallbacks.acquireSuspendBlocker();
    this.mHandler.post(this.mOnProximityPositiveSuspendRunnable);
  }
  
  private void sendOnProximityPositiveWithWakelock()
  {
    this.mCallbacks.acquireSuspendBlocker();
    this.mHandler.post(this.mOnProximityPositiveRunnable);
  }
  
  private void sendOnStateChangedWithWakelock()
  {
    this.mCallbacks.acquireSuspendBlocker();
    this.mHandler.post(this.mOnStateChangedRunnable);
  }
  
  private void sendUpdatePowerState()
  {
    synchronized (this.mLock)
    {
      sendUpdatePowerStateLocked();
      return;
    }
  }
  
  private void sendUpdatePowerStateLocked()
  {
    if (!this.mPendingUpdatePowerStateLocked)
    {
      this.mPendingUpdatePowerStateLocked = true;
      Message localMessage = this.mHandler.obtainMessage(1);
      localMessage.setAsynchronous(true);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  private void setPendingProximityDebounceTime(long paramLong)
  {
    if (this.mPendingProximityDebounceTime < 0L) {
      this.mCallbacks.acquireSuspendBlocker();
    }
    this.mPendingProximityDebounceTime = paramLong;
  }
  
  private void setProximitySensorEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (!this.mProximitySensorEnabled)
      {
        this.mProximitySensorEnabled = true;
        this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, 3, this.mHandler);
      }
    }
    while (!this.mProximitySensorEnabled) {
      return;
    }
    this.mProximitySensorEnabled = false;
    this.useProximityForceSuspend = false;
    this.mProximity = -1;
    this.mPendingProximity = -1;
    this.mHandler.removeMessages(2);
    this.mSensorManager.unregisterListener(this.mProximitySensorListener);
    clearPendingProximityDebounceTime();
    Settings.System.putInt(this.mContext.getContentResolver(), "display_ctrl_psensor_positive", 0);
  }
  
  private boolean setScreenState(int paramInt)
  {
    if ((paramInt == 2) && (PowerManagerService.mFirstSetScreenState))
    {
      if (!this.mPowerRequest.useAutoBrightness) {
        break label338;
      }
      PowerManagerService.mDisplayStateOn = true;
      PowerManagerService.mFirstSetScreenState = false;
      PowerManagerService.mManualBrightnessBackup = (int)Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0F, -2);
      PowerManagerService.mManualAmbientLuxBackup = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "autobrightness_manul_ambient", 0.0F, -2);
      if (DEBUG) {
        Slog.d("DisplayPowerController", " mManulAtAmbientLux = " + PowerManagerService.mManulAtAmbientLux);
      }
    }
    for (;;)
    {
      if (this.mPowerState.getScreenState() != paramInt)
      {
        if (paramInt == 1)
        {
          PowerManagerService.mManualBrightnessBackup = PowerManagerService.mManualBrightness;
          PowerManagerService.mManualAmbientLuxBackup = PowerManagerService.mManulAtAmbientLux;
          if (DEBUG) {
            Slog.d("DisplayPowerController", "Display.STATE_OFF PowerManagerService.mManualBrightness = " + PowerManagerService.mManualBrightness + " mManulAtAmbientLux = " + PowerManagerService.mManulAtAmbientLux);
          }
          PowerManagerService.mManualBrightness = 0;
          Settings.System.putFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", PowerManagerService.mManualBrightnessBackup, -2);
          Settings.System.putFloatForUser(this.mContext.getContentResolver(), "autobrightness_manul_ambient", PowerManagerService.mManulAtAmbientLux, -2);
        }
      }
      else {
        label219:
        if (this.mPowerState.getScreenState() != paramInt)
        {
          if (this.mPowerState.getScreenState() == 1) {
            break label423;
          }
          label241:
          this.mPowerState.setScreenState(paramInt);
        }
      }
      try
      {
        this.mBatteryStats.noteScreenState(paramInt);
        if (paramInt == 1)
        {
          paramInt = 1;
          label266:
          if ((paramInt != 0) && (this.mReportedScreenStateToPolicy != 0) && (!this.mScreenOffBecauseOfProximity)) {
            break label431;
          }
          if ((paramInt == 0) && (this.mReportedScreenStateToPolicy == 0))
          {
            this.mReportedScreenStateToPolicy = 1;
            if (this.mPowerState.getColorFadeLevel() != 0.0F) {
              break label452;
            }
            blockScreenOn();
          }
        }
        for (;;)
        {
          this.mWindowManagerPolicy.screenTurningOn(this.mPendingScreenOnUnblocker);
          for (;;)
          {
            if (this.mPendingScreenOnUnblocker != null) {
              break label459;
            }
            return true;
            label338:
            PowerManagerService.mFirstSetScreenState = false;
            break;
            if (paramInt != 2) {
              break label219;
            }
            PowerManagerService.mDisplayStateOn = true;
            PowerManagerService.mManualBrightnessBackup = (int)Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0F, -2);
            if (PowerManagerService.mManualBrightnessBackup != 0) {
              PowerManagerService.mManualBrightness = PowerManagerService.mManualBrightnessBackup;
            }
            if (!DEBUG) {
              break label219;
            }
            Slog.d("DisplayPowerController", "Display.STATE_ON PowerManagerService.mManualBrightness " + PowerManagerService.mManualBrightness);
            break label219;
            label423:
            break label241;
            paramInt = 0;
            break label266;
            label431:
            this.mReportedScreenStateToPolicy = 0;
            unblockScreenOn();
            this.mWindowManagerPolicy.screenTurnedOff();
          }
          label452:
          unblockScreenOn();
        }
        label459:
        return false;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private void setScreenStatus(Boolean paramBoolean)
  {
    if (((this.mPreScreeeStatus == null) || (this.mPreScreeeStatus != paramBoolean)) && (mCBM != null))
    {
      this.mPreScreeeStatus = paramBoolean;
      if (paramBoolean.booleanValue()) {
        mCBM.sendMsg(1);
      }
    }
    else
    {
      return;
    }
    mCBM.sendMsg(2);
  }
  
  private void unblockScreenOn()
  {
    if (this.mPendingScreenOnUnblocker != null)
    {
      this.mPendingScreenOnUnblocker = null;
      long l1 = SystemClock.elapsedRealtime();
      long l2 = this.mScreenOnBlockStartRealTime;
      this.mCallbacks.unblockScreenOn();
      Slog.i("DisplayPowerController", "Unblocked screen on after " + (l1 - l2) + " ms");
      Trace.asyncTraceEnd(131072L, "Screen on blocked", 0);
    }
  }
  
  private void updatePowerState()
  {
    int j = 0;
    int m = 0;
    int i;
    boolean bool1;
    int n;
    boolean bool2;
    int k;
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mPendingUpdatePowerStateLocked = false;
        DisplayManagerInternal.DisplayPowerRequest localDisplayPowerRequest = this.mPendingRequestLocked;
        if (localDisplayPowerRequest == null) {
          return;
        }
        if (this.mPowerRequest == null)
        {
          this.mPowerRequest = new DisplayManagerInternal.DisplayPowerRequest(this.mPendingRequestLocked);
          this.mWaitingForNegativeProximity = this.mPendingWaitForNegativeProximityLocked;
          this.mPendingWaitForNegativeProximityLocked = false;
          this.mPendingRequestChangedLocked = false;
          i = 1;
          bool1 = this.mDisplayReadyLocked;
          if (!bool1) {
            break label297;
          }
          n = 0;
          if (i != 0) {
            initialize();
          }
          if ((this.mScreenState == 2) && (this.mPowerRequest.policy == 3)) {
            mQuickDarkToBright = true;
          }
          this.mScreenState = this.mPowerRequest.policy;
          j = -1;
          bool2 = false;
        }
        switch (this.mPowerRequest.policy)
        {
        default: 
          setScreenStatus(Boolean.valueOf(true));
          i = 2;
          bool1 = bool2;
          if (-assertionsDisabled) {
            break label382;
          }
          if (i == 0) {
            break label376;
          }
          k = 1;
          if (k != 0) {
            break label382;
          }
          throw new AssertionError();
          i = j;
          if (!this.mPendingRequestChangedLocked) {
            continue;
          }
          if (this.mPowerRequest.screenAutoBrightnessAdjustment != this.mPendingRequestLocked.screenAutoBrightnessAdjustment)
          {
            i = 1;
            this.mPowerRequest.copyFrom(this.mPendingRequestLocked);
            this.mWaitingForNegativeProximity |= this.mPendingWaitForNegativeProximityLocked;
            this.mPendingWaitForNegativeProximityLocked = false;
            this.mPendingRequestChangedLocked = false;
            this.mDisplayReadyLocked = false;
            m = i;
            i = j;
          }
          break;
        }
      }
      i = 0;
      continue;
      label297:
      n = 1;
      continue;
      i = 1;
      setScreenStatus(Boolean.valueOf(false));
      bool1 = true;
      continue;
      if (this.mPowerRequest.dozeScreenState != 0) {}
      for (k = this.mPowerRequest.dozeScreenState;; k = 3)
      {
        bool1 = bool2;
        i = k;
        if (this.mAllowAutoBrightnessWhileDozingConfig) {
          break;
        }
        j = this.mPowerRequest.dozeScreenBrightness;
        bool1 = bool2;
        i = k;
        break;
      }
      label376:
      k = 0;
    }
    label382:
    if (this.mProximitySensor != null)
    {
      Slog.i("DisplayPowerController", "useProximityForceSuspend = " + this.useProximityForceSuspend);
      if (this.useProximityForceSuspend) {
        if (this.mPowerRequest.useProximitySensor)
        {
          setProximitySensorEnabled(true);
          if (!this.mProximityEventHandled)
          {
            Slog.i("DisplayPowerController", "mProximity = " + proximityToString(this.mProximity));
            if (this.mProximity == 1) {
              sendOnProximityPositiveSuspendWithWakelock();
            }
          }
        }
      }
    }
    for (;;)
    {
      this.mProximityEventHandled = true;
      label496:
      k = i;
      if (!this.useProximityForceSuspend)
      {
        k = i;
        if (this.mScreenOffBecauseOfProximity) {
          k = 1;
        }
      }
      animateScreenStateChange(k, bool1);
      int i1 = this.mPowerState.getScreenState();
      if (i1 == 1) {
        j = 0;
      }
      bool1 = false;
      label575:
      label602:
      label616:
      boolean bool3;
      if (this.mAutomaticBrightnessController != null)
      {
        if (!this.mAllowAutoBrightnessWhileDozingConfig) {
          break label1609;
        }
        if ((i1 == 3) || (i1 == 4))
        {
          i = 1;
          if ((!this.mPowerRequest.useAutoBrightness) || ((i1 != 2) && (i == 0))) {
            break label1620;
          }
          if (j >= 0) {
            break label1614;
          }
          bool1 = true;
          if (m == 0) {
            break label1626;
          }
          bool2 = this.mPowerRequest.brightnessSetByUser;
          ??? = this.mAutomaticBrightnessController;
          float f = this.mPowerRequest.screenAutoBrightnessAdjustment;
          if (i1 == 2) {
            break label1632;
          }
          bool3 = true;
          label639:
          ((AutomaticBrightnessController)???).configure(bool1, f, bool3, bool2, this.mPowerRequest.useTwilight);
        }
      }
      else
      {
        i = j;
        if (this.mPowerRequest.boostScreenBrightness)
        {
          i = j;
          if (j != 0) {
            i = 255;
          }
        }
        k = 0;
        j = 0;
        if (i >= 0) {
          break label1656;
        }
        if (bool1) {
          i = this.mAutomaticBrightnessController.getAutomaticScreenBrightness();
        }
        if (i < 0) {
          break label1643;
        }
        k = clampScreenBrightness(i);
        i = j;
        if (this.mAppliedAutoBrightness)
        {
          if (m == 0) {
            break label1638;
          }
          i = j;
        }
        label729:
        if (PowerManagerService.sBrightnessBoost == 2)
        {
          if (!this.mDisplayReadyLocked) {
            PowerManagerService.sBrightnessNoAnimation = true;
          }
          i = 0;
          PowerManagerService.sBrightnessBoost = 3;
          if (DEBUG_ONEPLUS) {
            Slog.d("DisplayPowerController", "sBrightnessBoost = 3");
          }
          this.mHandler.removeMessages(4);
          ??? = this.mHandler.obtainMessage(4);
          this.mHandler.sendMessageDelayed((Message)???, 5000L);
        }
        this.mAppliedAutoBrightness = true;
        j = k;
        label807:
        m = j;
        if (j < 0) {
          if (i1 != 3)
          {
            m = j;
            if (i1 != 4) {}
          }
          else
          {
            m = this.mScreenBrightnessDozeConfig;
          }
        }
        k = m;
        if (m < 0) {
          k = clampScreenBrightness(this.mPowerRequest.screenBrightness);
        }
        if (this.mPowerRequest.policy != 2) {
          break label1669;
        }
        j = k;
        if (k > this.mScreenBrightnessRangeMinimum) {
          j = Math.max(Math.min(k - 10, this.mScreenBrightnessDimConfig), this.mScreenBrightnessRangeMinimum);
        }
        if (!this.mAppliedDimming) {
          i = 0;
        }
        this.mAppliedDimming = true;
        mQuicklyApplyDimming = true;
        PowerManagerService.mManualSetAutoBrightness = false;
        label922:
        if (!this.mPowerRequest.lowPowerMode) {
          break label1692;
        }
        k = j;
        if (j > this.mScreenBrightnessRangeMinimum) {
          k = Math.max(j / 2, this.mScreenBrightnessRangeMinimum);
        }
        if (!this.mAppliedLowPower) {
          i = 0;
        }
        this.mAppliedLowPower = true;
        label969:
        if (!this.mPendingScreenOff)
        {
          if ((i1 != 2) && (i1 != 3)) {
            break label1733;
          }
          if (i == 0) {
            break label1715;
          }
          j = 40;
          label995:
          if (bool1) {
            j = this.mAutomaticBrightnessController.getAutomaticScreenRate();
          }
          if (PowerManagerService.sBrightnessBoost == 3)
          {
            if (DEBUG_ONEPLUS) {
              Slog.d("DisplayPowerController", "BRIGHTNESS_RAMP_RATE_SCREENON");
            }
            j = 120;
          }
          if (i == 0) {
            j = this.mBrightnessRampRateFast;
          }
          if (mQuicklyApplyDimming)
          {
            mQuicklyApplyDimming = false;
            j = 180;
          }
          if (mQuickDarkToBright)
          {
            mQuickDarkToBright = false;
            j = 120;
          }
          if (DEBUG_ONEPLUS) {
            Slog.d("DisplayPowerController", "brightness = " + k + ", rate = " + j);
          }
          if (!PowerManagerService.mManualSetAutoBrightness) {
            break label1723;
          }
          animateScreenBrightness(k, 0);
        }
        label1125:
        if ((this.mPendingScreenOnUnblocker == null) && (!this.mColorFadeOnAnimator.isStarted())) {
          break label1743;
        }
        label1142:
        bool1 = false;
        label1145:
        if (!bool1) {
          break label1774;
        }
        if (!this.mScreenBrightnessRampAnimator.isAnimating()) {
          break label1769;
        }
        i = 0;
        label1162:
        if ((bool1) && (i1 != 1) && (this.mReportedScreenStateToPolicy == 1))
        {
          this.mReportedScreenStateToPolicy = 2;
          this.mWindowManagerPolicy.screenTurnedOn();
        }
        if ((i == 0) && (!this.mUnfinishedBusiness)) {
          break label1779;
        }
        if ((bool1) && (n != 0)) {
          PowerManagerService.sBrightnessNoAnimation = false;
        }
      }
      synchronized (this.mLock)
      {
        if (!this.mPendingRequestChangedLocked)
        {
          this.mDisplayReadyLocked = true;
          if (DEBUG) {
            Slog.d("DisplayPowerController", "Display ready!");
          }
        }
        sendOnStateChangedWithWakelock();
        if ((i != 0) && (this.mUnfinishedBusiness))
        {
          if (DEBUG) {
            Slog.d("DisplayPowerController", "Finished business...");
          }
          this.mUnfinishedBusiness = false;
          this.mCallbacks.releaseSuspendBlocker();
        }
        return;
        if (this.mProximity != 0) {
          continue;
        }
        sendOnProximityNegativeSuspendWithWakelock();
        continue;
        if (!DEBUG) {
          break label496;
        }
        Slog.i("DisplayPowerController", "the last proximity event has been handled");
        break label496;
        if (!this.mProximitySensorEnabled) {
          break label496;
        }
        Slog.i("DisplayPowerController", "mPowerRequest.useProximitySensor = " + this.mPowerRequest.useProximitySensor + ", mWaitingForNegativeProximity = " + this.mWaitingForNegativeProximity + ", state = " + i);
        if ((this.mWaitingForNegativeProximity) && (this.mProximity == 1) && ((i == 1) || (i == 3)))
        {
          setProximitySensorEnabled(true);
          break label496;
        }
        setProximitySensorEnabled(false);
        if ((i == 1) || (i == 3))
        {
          Slog.i("DisplayPowerController", "turn on lcd light due to proximity released");
          sendOnProximityNegativeSuspendWithWakelock();
        }
        this.mScreenOffBecauseOfProximity = false;
        this.mWaitingForNegativeProximity = false;
        this.mProximityEventHandled = true;
        break label496;
        if ((this.mPowerRequest.useProximitySensor) && (i != 1))
        {
          setProximitySensorEnabled(true);
          if ((!this.mScreenOffBecauseOfProximity) && (this.mProximity == 1))
          {
            this.mScreenOffBecauseOfProximity = true;
            sendOnProximityPositiveWithWakelock();
          }
        }
        while ((this.mScreenOffBecauseOfProximity) && (this.mProximity != 1))
        {
          this.mScreenOffBecauseOfProximity = false;
          sendOnProximityNegativeWithWakelock();
          break;
          if ((this.mWaitingForNegativeProximity) && (this.mScreenOffBecauseOfProximity) && (this.mProximity == 1) && (i != 1))
          {
            setProximitySensorEnabled(true);
          }
          else
          {
            setProximitySensorEnabled(false);
            this.mWaitingForNegativeProximity = false;
          }
        }
        this.mWaitingForNegativeProximity = false;
        break label496;
        i = 0;
        break label575;
        label1609:
        i = 0;
        break label575;
        label1614:
        bool1 = false;
        break label602;
        label1620:
        bool1 = false;
        break label602;
        label1626:
        bool2 = false;
        break label616;
        label1632:
        bool3 = false;
        break label639;
        label1638:
        i = 1;
        break label729;
        label1643:
        this.mAppliedAutoBrightness = false;
        j = i;
        i = k;
        break label807;
        label1656:
        this.mAppliedAutoBrightness = false;
        j = i;
        i = k;
        break label807;
        label1669:
        j = k;
        if (!this.mAppliedDimming) {
          break label922;
        }
        i = 0;
        this.mAppliedDimming = false;
        j = k;
        break label922;
        label1692:
        k = j;
        if (!this.mAppliedLowPower) {
          break label969;
        }
        i = 0;
        this.mAppliedLowPower = false;
        k = j;
        break label969;
        label1715:
        j = this.mBrightnessRampRateFast;
        break label995;
        label1723:
        animateScreenBrightness(k, j);
        break label1125;
        label1733:
        animateScreenBrightness(k, 0);
        break label1125;
        label1743:
        if (this.mColorFadeOffAnimator.isStarted()) {
          break label1142;
        }
        bool1 = this.mPowerState.waitUntilClean(this.mCleanListener);
        break label1145;
        label1769:
        i = 1;
        break label1162;
        label1774:
        i = 0;
        break label1162;
        label1779:
        if (DEBUG) {
          Slog.d("DisplayPowerController", "Unfinished business...");
        }
        this.mCallbacks.acquireSuspendBlocker();
        this.mUnfinishedBusiness = true;
      }
    }
  }
  
  public void dump(final PrintWriter paramPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("Display Power Controller Locked State:");
      paramPrintWriter.println("  mDisplayReadyLocked=" + this.mDisplayReadyLocked);
      paramPrintWriter.println("  mPendingRequestLocked=" + this.mPendingRequestLocked);
      paramPrintWriter.println("  mPendingRequestChangedLocked=" + this.mPendingRequestChangedLocked);
      paramPrintWriter.println("  mPendingWaitForNegativeProximityLocked=" + this.mPendingWaitForNegativeProximityLocked);
      paramPrintWriter.println("  mPendingUpdatePowerStateLocked=" + this.mPendingUpdatePowerStateLocked);
      paramPrintWriter.println();
      paramPrintWriter.println("Display Power Controller Configuration:");
      paramPrintWriter.println("  mScreenBrightnessDozeConfig=" + this.mScreenBrightnessDozeConfig);
      paramPrintWriter.println("  mScreenBrightnessDimConfig=" + this.mScreenBrightnessDimConfig);
      paramPrintWriter.println("  mScreenBrightnessDarkConfig=" + this.mScreenBrightnessDarkConfig);
      paramPrintWriter.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
      paramPrintWriter.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
      paramPrintWriter.println("  mUseSoftwareAutoBrightnessConfig=" + this.mUseSoftwareAutoBrightnessConfig);
      paramPrintWriter.println("  mAllowAutoBrightnessWhileDozingConfig=" + this.mAllowAutoBrightnessWhileDozingConfig);
      paramPrintWriter.println("  mColorFadeFadesConfig=" + this.mColorFadeFadesConfig);
      this.mHandler.runWithScissors(new Runnable()
      {
        public void run()
        {
          DisplayPowerController.-wrap1(DisplayPowerController.this, paramPrintWriter);
        }
      }, 1000L);
      return;
    }
  }
  
  public boolean isProximitySensorAvailable()
  {
    return this.mProximitySensor != null;
  }
  
  /* Error */
  public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest paramDisplayPowerRequest, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 232	com/android/server/display/DisplayPowerController:DEBUG	Z
    //   3: ifeq +39 -> 42
    //   6: ldc 79
    //   8: new 353	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 1103
    //   18: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: aload_1
    //   22: invokevirtual 619	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   25: ldc_w 1105
    //   28: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: iload_2
    //   32: invokevirtual 679	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   35: invokevirtual 373	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   38: invokestatic 486	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   41: pop
    //   42: aload_0
    //   43: getfield 247	com/android/server/display/DisplayPowerController:mLock	Ljava/lang/Object;
    //   46: astore 5
    //   48: aload 5
    //   50: monitorenter
    //   51: iconst_0
    //   52: istore 4
    //   54: iload 4
    //   56: istore_3
    //   57: iload_2
    //   58: ifeq +13 -> 71
    //   61: aload_0
    //   62: getfield 935	com/android/server/display/DisplayPowerController:mPendingWaitForNegativeProximityLocked	Z
    //   65: ifeq +57 -> 122
    //   68: iload 4
    //   70: istore_3
    //   71: aload_0
    //   72: getfield 930	com/android/server/display/DisplayPowerController:mPendingRequestLocked	Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;
    //   75: ifnonnull +57 -> 132
    //   78: aload_0
    //   79: new 858	android/hardware/display/DisplayManagerInternal$DisplayPowerRequest
    //   82: dup
    //   83: aload_1
    //   84: invokespecial 933	android/hardware/display/DisplayManagerInternal$DisplayPowerRequest:<init>	(Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;)V
    //   87: putfield 930	com/android/server/display/DisplayPowerController:mPendingRequestLocked	Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;
    //   90: iconst_1
    //   91: istore_3
    //   92: iload_3
    //   93: ifeq +8 -> 101
    //   96: aload_0
    //   97: iconst_0
    //   98: putfield 939	com/android/server/display/DisplayPowerController:mDisplayReadyLocked	Z
    //   101: iload_3
    //   102: ifeq +10 -> 112
    //   105: aload_0
    //   106: getfield 937	com/android/server/display/DisplayPowerController:mPendingRequestChangedLocked	Z
    //   109: ifeq +47 -> 156
    //   112: aload_0
    //   113: getfield 939	com/android/server/display/DisplayPowerController:mDisplayReadyLocked	Z
    //   116: istore_2
    //   117: aload 5
    //   119: monitorexit
    //   120: iload_2
    //   121: ireturn
    //   122: aload_0
    //   123: iconst_1
    //   124: putfield 935	com/android/server/display/DisplayPowerController:mPendingWaitForNegativeProximityLocked	Z
    //   127: iconst_1
    //   128: istore_3
    //   129: goto -58 -> 71
    //   132: aload_0
    //   133: getfield 930	com/android/server/display/DisplayPowerController:mPendingRequestLocked	Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;
    //   136: aload_1
    //   137: invokevirtual 1108	android/hardware/display/DisplayManagerInternal$DisplayPowerRequest:equals	(Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;)Z
    //   140: ifne -48 -> 92
    //   143: aload_0
    //   144: getfield 930	com/android/server/display/DisplayPowerController:mPendingRequestLocked	Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;
    //   147: aload_1
    //   148: invokevirtual 958	android/hardware/display/DisplayManagerInternal$DisplayPowerRequest:copyFrom	(Landroid/hardware/display/DisplayManagerInternal$DisplayPowerRequest;)V
    //   151: iconst_1
    //   152: istore_3
    //   153: goto -61 -> 92
    //   156: aload_0
    //   157: iconst_1
    //   158: putfield 937	com/android/server/display/DisplayPowerController:mPendingRequestChangedLocked	Z
    //   161: aload_0
    //   162: invokespecial 824	com/android/server/display/DisplayPowerController:sendUpdatePowerStateLocked	()V
    //   165: goto -53 -> 112
    //   168: astore_1
    //   169: aload 5
    //   171: monitorexit
    //   172: aload_1
    //   173: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	174	0	this	DisplayPowerController
    //   0	174	1	paramDisplayPowerRequest	DisplayManagerInternal.DisplayPowerRequest
    //   0	174	2	paramBoolean	boolean
    //   56	97	3	i	int
    //   52	17	4	j	int
    //   46	124	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   61	68	168	finally
    //   71	90	168	finally
    //   96	101	168	finally
    //   105	112	168	finally
    //   112	117	168	finally
    //   122	127	168	finally
    //   132	151	168	finally
    //   156	165	168	finally
  }
  
  public void setUseProximityForceSuspend(boolean paramBoolean)
  {
    if (!this.useProximityForceSuspend) {
      this.useProximityForceSuspend = paramBoolean;
    }
  }
  
  public void setWakingupReason(String paramString)
  {
    this.mWakingUpReason = paramString;
  }
  
  public void updateBrightness()
  {
    sendUpdatePowerState();
  }
  
  private final class DisplayControllerHandler
    extends Handler
  {
    public DisplayControllerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
      case 1: 
      case 2: 
      case 3: 
        do
        {
          return;
          DisplayPowerController.-wrap5(DisplayPowerController.this);
          return;
          DisplayPowerController.-wrap0(DisplayPowerController.this);
          return;
        } while (DisplayPowerController.-get3(DisplayPowerController.this) != paramMessage.obj);
        DisplayPowerController.-wrap4(DisplayPowerController.this);
        DisplayPowerController.-wrap5(DisplayPowerController.this);
        return;
      }
      PowerManagerService.sBrightnessBoost = 4;
    }
  }
  
  private final class ScreenOnUnblocker
    implements WindowManagerPolicy.ScreenOnListener
  {
    private ScreenOnUnblocker() {}
    
    public void onScreenOn()
    {
      Message localMessage = DisplayPowerController.-get2(DisplayPowerController.this).obtainMessage(3, this);
      localMessage.setAsynchronous(true);
      DisplayPowerController.-get2(DisplayPowerController.this).sendMessage(localMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayPowerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */