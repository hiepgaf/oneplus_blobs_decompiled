package com.android.server.display;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.util.EventLog;
import android.util.MathUtils;
import android.util.Slog;
import android.util.Spline;
import android.util.TimeUtils;
import com.android.server.LocalServices;
import com.android.server.power.PowerManagerService;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

class AutomaticBrightnessController
{
  private static final long AMBIENT_LIGHT_PREDICTION_TIME_MILLIS = 100L;
  private static final float BRIGHTENING_FAST_THRESHOLD = 3000.0F;
  private static final float BRIGHTENING_LIGHT_HYSTERESIS = 0.0F;
  private static final int BRIGHTNESS_ADJUSTMENT_SAMPLE_DEBOUNCE_MILLIS = 10000;
  private static final float DARKENING_LIGHT_HYSTERESIS = 0.0F;
  static boolean DEBUG = false;
  private static boolean DEBUG_BACKLIGHT = false;
  private static final boolean DEBUG_PRETEND_LIGHT_SENSOR_ABSENT = false;
  private static final int MSG_BRIGHTNESS_ADJUSTMENT_SAMPLE = 2;
  private static final int MSG_UPDATE_AMBIENT_LUX = 1;
  private static final String TAG = "AutomaticBrightnessController";
  private static final float TWILIGHT_ADJUSTMENT_MAX_GAMMA = 1.0F;
  private static final boolean USE_SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT = true;
  private static BrightnessControllerUtility sBrightnessControllerUtility;
  private final int mAmbientLightHorizon;
  private AmbientLightRingBuffer mAmbientLightRingBuffer;
  private int mAmbientLightZone = -1;
  private float mAmbientLux;
  private float mAmbientLuxMax = 0.0F;
  private float mAmbientLuxMin = 0.0F;
  private boolean mAmbientLuxValid;
  private int mAmbientState = 0;
  private final long mBrighteningLightDebounceConfig;
  private final long mBrighteningLightFastDebounceConfig;
  private float mBrighteningLuxThreshold;
  private float mBrightnessAdjustmentSampleOldAdjustment;
  private int mBrightnessAdjustmentSampleOldBrightness;
  private float mBrightnessAdjustmentSampleOldGamma;
  private float mBrightnessAdjustmentSampleOldLux;
  private boolean mBrightnessAdjustmentSamplePending;
  private final Callbacks mCallbacks;
  private Context mContext;
  private final long mDarkeningLightDebounceConfig;
  private float mDarkeningLuxThreshold;
  private final float mDozeScaleFactor;
  private boolean mDozing;
  private boolean mFirst_lux = true;
  private AutomaticBrightnessHandler mHandler;
  private AmbientLightRingBuffer mInitialHorizonAmbientLightRingBuffer;
  private int mLastAmbientLightZone = -1;
  private float mLastAmbientLuxMax = 0.0F;
  private float mLastAmbientLuxMin = 0.0F;
  private float mLastObservedLux;
  private long mLastObservedLuxTime;
  private float mLastScreenAutoBrightnessGamma = 1.0F;
  private final Sensor mLightSensor;
  private long mLightSensorEnableTime;
  private boolean mLightSensorEnabled;
  private final SensorEventListener mLightSensorListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      long l;
      float f2;
      float f1;
      label384:
      int j;
      int i;
      if (AutomaticBrightnessController.-get12(AutomaticBrightnessController.this))
      {
        l = SystemClock.uptimeMillis();
        f2 = paramAnonymousSensorEvent.values[0];
        f1 = f2;
        if (f2 == 1.0F) {
          f1 = 0.0F;
        }
        Settings.System.putInt(AutomaticBrightnessController.-get4(AutomaticBrightnessController.this).getContentResolver(), "lux-display", (int)f1);
        if (PowerManagerService.sBrightnessBoost == 1)
        {
          PowerManagerService.sBrightnessBoost = 2;
          Slog.d("RampAnimator", "sBrightnessBoost == 2");
        }
        if (AutomaticBrightnessController.-get15(AutomaticBrightnessController.this))
        {
          if (AutomaticBrightnessController.DEBUG) {
            Slog.d("AutomaticBrightnessController", "P-Sensor Changed ");
          }
          return;
        }
        if (AutomaticBrightnessController.DEBUG) {
          Slog.d("AutomaticBrightnessController", "Light-Sensor Changed lux: " + f1 + "mAmbientState:" + AutomaticBrightnessController.-get3(AutomaticBrightnessController.this) + " mAmbientLuxMin: " + AutomaticBrightnessController.-get2(AutomaticBrightnessController.this) + " mAmbientLuxMax: " + AutomaticBrightnessController.-get1(AutomaticBrightnessController.this));
        }
        if ((f1 == 0.0F) && (AutomaticBrightnessController.-get5(AutomaticBrightnessController.this))) {
          AutomaticBrightnessController.-set4(AutomaticBrightnessController.this, false);
        }
        while ((f1 != 0.0F) || (AutomaticBrightnessController.-get2(AutomaticBrightnessController.this) == 0.0F) || (PowerManagerService.sBrightnessBoost == 2) || (AutomaticBrightnessController.-get21(AutomaticBrightnessController.this)))
        {
          if (AutomaticBrightnessController.-get21(AutomaticBrightnessController.this))
          {
            if (f1 == 0.0F) {
              break;
            }
            AutomaticBrightnessController.-wrap3(AutomaticBrightnessController.this);
            if (AutomaticBrightnessController.DEBUG) {
              Slog.d("AutomaticBrightnessController", "received 0lux at" + AutomaticBrightnessController.-get20(AutomaticBrightnessController.this) + "now received lux=" + f1);
            }
          }
          if ((AutomaticBrightnessController.-get3(AutomaticBrightnessController.this) != 0) || (f1 < AutomaticBrightnessController.-get2(AutomaticBrightnessController.this)) || (f1 >= AutomaticBrightnessController.-get1(AutomaticBrightnessController.this))) {
            break label384;
          }
          return;
        }
        AutomaticBrightnessController.-set8(AutomaticBrightnessController.this, l);
        AutomaticBrightnessController.-set9(AutomaticBrightnessController.this, true);
        AutomaticBrightnessController.-wrap2(AutomaticBrightnessController.this);
        if (AutomaticBrightnessController.DEBUG) {
          Slog.d("AutomaticBrightnessController", "onSensorChanged: first received lux = 0");
        }
        return;
        Slog.d("AutomaticBrightnessController", "it will not go here");
        return;
        if (AutomaticBrightnessController.-get3(AutomaticBrightnessController.this) == 1)
        {
          if ((f1 >= AutomaticBrightnessController.-get9(AutomaticBrightnessController.this)) && (f1 < AutomaticBrightnessController.-get8(AutomaticBrightnessController.this)))
          {
            AutomaticBrightnessController.-set3(AutomaticBrightnessController.this, 2);
            paramAnonymousSensorEvent = AutomaticBrightnessController.this;
            AutomaticBrightnessController.-get22();
            AutomaticBrightnessController.-wrap1(paramAnonymousSensorEvent, l, BrightnessControllerUtility.mAmbientLuxConfig[AutomaticBrightnessController.-get7(AutomaticBrightnessController.this)]);
            AutomaticBrightnessController.-set3(AutomaticBrightnessController.this, 0);
            AutomaticBrightnessController.-set2(AutomaticBrightnessController.this, AutomaticBrightnessController.-get9(AutomaticBrightnessController.this));
            AutomaticBrightnessController.-set1(AutomaticBrightnessController.this, AutomaticBrightnessController.-get8(AutomaticBrightnessController.this));
            AutomaticBrightnessController.-set0(AutomaticBrightnessController.this, AutomaticBrightnessController.-get7(AutomaticBrightnessController.this));
            return;
          }
          if ((f1 >= AutomaticBrightnessController.-get2(AutomaticBrightnessController.this)) && (f1 < AutomaticBrightnessController.-get1(AutomaticBrightnessController.this))) {
            return;
          }
        }
        AutomaticBrightnessController.-set7(AutomaticBrightnessController.this, AutomaticBrightnessController.-get22().calculateRate(f1, AutomaticBrightnessController.-get10(AutomaticBrightnessController.this)));
        if ((AutomaticBrightnessController.DEBUG) || (AutomaticBrightnessController.-get0())) {
          Slog.d("AutomaticBrightnessController", "lux=" + f1 + " ,mLastObservedLux=" + AutomaticBrightnessController.-get10(AutomaticBrightnessController.this) + " ,mScreenAutoRate=" + AutomaticBrightnessController.-get18(AutomaticBrightnessController.this));
        }
        AutomaticBrightnessController.-set5(AutomaticBrightnessController.this, f1);
        AutomaticBrightnessController.-get22();
        j = BrightnessControllerUtility.BRIGHTNESS_LEVELS;
        i = 0;
      }
      for (;;)
      {
        f2 = f1;
        if (i < j)
        {
          AutomaticBrightnessController.-get22();
          if (f1 > BrightnessControllerUtility.mAmbientLuxConfig[i]) {
            break label801;
          }
          AutomaticBrightnessController.-get22();
          f2 = BrightnessControllerUtility.mAmbientLuxConfig[i];
          paramAnonymousSensorEvent = AutomaticBrightnessController.this;
          AutomaticBrightnessController.-get22();
          AutomaticBrightnessController.-set2(paramAnonymousSensorEvent, BrightnessControllerUtility.mAmbientLuxMinConfig[i]);
          paramAnonymousSensorEvent = AutomaticBrightnessController.this;
          AutomaticBrightnessController.-get22();
          AutomaticBrightnessController.-set1(paramAnonymousSensorEvent, BrightnessControllerUtility.mAmbientLuxMaxConfig[i]);
          AutomaticBrightnessController.-set0(AutomaticBrightnessController.this, i);
        }
        for (;;)
        {
          AutomaticBrightnessController.-set3(AutomaticBrightnessController.this, 1);
          if ((AutomaticBrightnessController.DEBUG) || (AutomaticBrightnessController.-get0())) {
            Slog.d("AutomaticBrightnessController", "Light-Sensor Changed new lux: " + f2 + "mAmbientState:" + AutomaticBrightnessController.-get3(AutomaticBrightnessController.this));
          }
          AutomaticBrightnessController.-wrap1(AutomaticBrightnessController.this, l, f2);
          return;
          label801:
          AutomaticBrightnessController.-get22();
          if (f1 <= BrightnessControllerUtility.mAmbientLuxConfig[(j - 1)]) {
            break;
          }
          AutomaticBrightnessController.-get22();
          f2 = BrightnessControllerUtility.mAmbientLuxConfig[(j - 1)];
          paramAnonymousSensorEvent = AutomaticBrightnessController.this;
          AutomaticBrightnessController.-get22();
          AutomaticBrightnessController.-set2(paramAnonymousSensorEvent, BrightnessControllerUtility.mAmbientLuxMinConfig[(j - 1)]);
          paramAnonymousSensorEvent = AutomaticBrightnessController.this;
          AutomaticBrightnessController.-get22();
          AutomaticBrightnessController.-set1(paramAnonymousSensorEvent, BrightnessControllerUtility.mAmbientLuxMaxConfig[(j - 1)]);
          AutomaticBrightnessController.-set0(AutomaticBrightnessController.this, j - 1);
        }
        i += 1;
      }
    }
  };
  private final int mLightSensorRate;
  private int mLightSensorWarmUpTimeConfig;
  private boolean mManulBrightnessSlide = false;
  private boolean mProximityPositive;
  private final Sensor mProximitySensor;
  private final SensorEventListener mProximitySensorListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      SystemClock.uptimeMillis();
      if (paramAnonymousSensorEvent.values[0] == 0.0F) {
        AutomaticBrightnessController.-set6(AutomaticBrightnessController.this, true);
      }
      for (;;)
      {
        if (AutomaticBrightnessController.-get0()) {
          Slog.d("AutomaticBrightnessController", "mProximityPositive : " + AutomaticBrightnessController.-get15(AutomaticBrightnessController.this));
        }
        return;
        AutomaticBrightnessController.-set6(AutomaticBrightnessController.this, false);
      }
    }
  };
  private int mRecentLightSamples;
  private final boolean mResetAmbientLuxAfterWarmUpConfig;
  public int mScreenAutoBrightness = -1;
  private float mScreenAutoBrightnessAdjustment = 0.0F;
  private float mScreenAutoBrightnessAdjustmentMaxGamma;
  private final Spline mScreenAutoBrightnessSpline;
  private int mScreenAutoRate = 0;
  private final int mScreenBrightnessRangeMaximum;
  private final int mScreenBrightnessRangeMinimum;
  private final SensorManager mSensorManager;
  private boolean mStartManual = false;
  private final TwilightManager mTwilight;
  private final TwilightListener mTwilightListener = new TwilightListener()
  {
    public void onTwilightStateChanged(TwilightState paramAnonymousTwilightState)
    {
      AutomaticBrightnessController.-wrap5(AutomaticBrightnessController.this, true);
    }
  };
  private boolean mUseTwilight;
  private final int mWeightingIntercept;
  private long mZeroStartTime = 0L;
  private TimerTask mZeroTask;
  private Timer mZeroTimer;
  private boolean mbStartTimer = false;
  private Handler zeroHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      if (paramAnonymousMessage.what == 1)
      {
        long l = SystemClock.uptimeMillis();
        paramAnonymousMessage = AutomaticBrightnessController.this;
        AutomaticBrightnessController.-get22();
        AutomaticBrightnessController.-set2(paramAnonymousMessage, BrightnessControllerUtility.mAmbientLuxMinConfig[0]);
        paramAnonymousMessage = AutomaticBrightnessController.this;
        AutomaticBrightnessController.-get22();
        AutomaticBrightnessController.-set1(paramAnonymousMessage, BrightnessControllerUtility.mAmbientLuxMaxConfig[0]);
        AutomaticBrightnessController.-set7(AutomaticBrightnessController.this, AutomaticBrightnessController.-get22().calculateRate(0.0F, AutomaticBrightnessController.-get10(AutomaticBrightnessController.this)));
        AutomaticBrightnessController.-wrap1(AutomaticBrightnessController.this, l, 0.0F);
      }
      if (AutomaticBrightnessController.-get21(AutomaticBrightnessController.this)) {
        AutomaticBrightnessController.-wrap3(AutomaticBrightnessController.this);
      }
    }
  };
  
  public AutomaticBrightnessController(Callbacks paramCallbacks, Looper paramLooper, Context paramContext, SensorManager paramSensorManager, Spline paramSpline, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, int paramInt4, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, int paramInt5, float paramFloat2)
  {
    this.mCallbacks = paramCallbacks;
    this.mTwilight = ((TwilightManager)LocalServices.getService(TwilightManager.class));
    this.mSensorManager = paramSensorManager;
    this.mScreenAutoBrightnessSpline = paramSpline;
    this.mScreenBrightnessRangeMinimum = paramInt2;
    this.mScreenBrightnessRangeMaximum = paramInt3;
    this.mLightSensorWarmUpTimeConfig = paramInt1;
    this.mDozeScaleFactor = paramFloat1;
    this.mLightSensorRate = paramInt4;
    this.mBrighteningLightDebounceConfig = paramLong1;
    this.mDarkeningLightDebounceConfig = paramLong3;
    this.mResetAmbientLuxAfterWarmUpConfig = paramBoolean;
    this.mAmbientLightHorizon = paramInt5;
    this.mWeightingIntercept = paramInt5;
    this.mScreenAutoBrightnessAdjustmentMaxGamma = paramFloat2;
    this.mHandler = new AutomaticBrightnessHandler(paramLooper);
    this.mAmbientLightRingBuffer = new AmbientLightRingBuffer(this.mLightSensorRate, this.mAmbientLightHorizon);
    this.mInitialHorizonAmbientLightRingBuffer = new AmbientLightRingBuffer(this.mLightSensorRate, this.mAmbientLightHorizon);
    this.mLightSensor = this.mSensorManager.getDefaultSensor(5);
    this.mContext = paramContext;
    this.mBrighteningLightFastDebounceConfig = paramLong2;
    this.mProximitySensor = this.mSensorManager.getDefaultSensor(8);
    this.mContext.getResources();
    sBrightnessControllerUtility = BrightnessControllerUtility.getInstance();
    sBrightnessControllerUtility.init(this.mContext);
    sBrightnessControllerUtility.readAutoBrightnessLuxConfig();
  }
  
  private void applyLightSensorMeasurement(long paramLong, float paramFloat)
  {
    this.mRecentLightSamples += 1;
    if (paramLong <= this.mLightSensorEnableTime + this.mAmbientLightHorizon) {
      this.mInitialHorizonAmbientLightRingBuffer.push(paramLong, paramFloat);
    }
    this.mAmbientLightRingBuffer.prune(paramLong - this.mAmbientLightHorizon);
    this.mAmbientLightRingBuffer.push(paramLong, paramFloat);
  }
  
  private float calculateAmbientLux(long paramLong)
  {
    int i = this.mAmbientLightRingBuffer.size();
    if (i == 0)
    {
      Slog.e("AutomaticBrightnessController", "calculateAmbientLux: No ambient light readings available");
      return -1.0F;
    }
    float f1 = 0.0F;
    float f2 = 0.0F;
    long l2 = 100L;
    i -= 1;
    while (i >= 0)
    {
      long l3 = this.mAmbientLightRingBuffer.getTime(i) - paramLong;
      long l1 = l3;
      if (l3 < 0L) {
        l1 = 0L;
      }
      float f3 = calculateWeight(l1, l2);
      float f4 = this.mAmbientLightRingBuffer.getLux(i);
      if (DEBUG) {
        Slog.d("AutomaticBrightnessController", "calculateAmbientLux: [" + l1 + ", " + l2 + "]: lux=" + f4 + ", weight=" + f3);
      }
      f2 += f3;
      f1 += this.mAmbientLightRingBuffer.getLux(i) * f3;
      i -= 1;
      l2 = l1;
    }
    if (DEBUG) {
      Slog.d("AutomaticBrightnessController", "calculateAmbientLux: totalWeight=" + f2 + ", newAmbientLux=" + f1 / f2);
    }
    return f1 / f2;
  }
  
  private float calculateWeight(long paramLong1, long paramLong2)
  {
    return weightIntegral(paramLong2) - weightIntegral(paramLong1);
  }
  
  private void cancelBrightnessAdjustmentSample()
  {
    if (this.mBrightnessAdjustmentSamplePending)
    {
      this.mBrightnessAdjustmentSamplePending = false;
      this.mHandler.removeMessages(2);
    }
  }
  
  private int clampScreenBrightness(int paramInt)
  {
    return MathUtils.constrain(paramInt, this.mScreenBrightnessRangeMinimum, this.mScreenBrightnessRangeMaximum);
  }
  
  private void collectBrightnessAdjustmentSample()
  {
    if (this.mBrightnessAdjustmentSamplePending)
    {
      this.mBrightnessAdjustmentSamplePending = false;
      if ((this.mAmbientLuxValid) && (this.mScreenAutoBrightness >= 0))
      {
        if (DEBUG) {
          Slog.d("AutomaticBrightnessController", "Auto-brightness adjustment changed by user: adj=" + this.mScreenAutoBrightnessAdjustment + ", lux=" + this.mAmbientLux + ", brightness=" + this.mScreenAutoBrightness + ", gamma=" + this.mLastScreenAutoBrightnessGamma + ", ring=" + this.mAmbientLightRingBuffer);
        }
        EventLog.writeEvent(35000, new Object[] { Float.valueOf(this.mBrightnessAdjustmentSampleOldAdjustment), Float.valueOf(this.mBrightnessAdjustmentSampleOldLux), Integer.valueOf(this.mBrightnessAdjustmentSampleOldBrightness), Float.valueOf(this.mBrightnessAdjustmentSampleOldGamma), Float.valueOf(this.mScreenAutoBrightnessAdjustment), Float.valueOf(this.mAmbientLux), Integer.valueOf(this.mScreenAutoBrightness), Float.valueOf(this.mLastScreenAutoBrightnessGamma) });
      }
    }
  }
  
  private void handleLightSensorEvent(long paramLong, float paramFloat)
  {
    this.mHandler.removeMessages(1);
    applyLightSensorMeasurement(paramLong, paramFloat);
    updateAmbientLux(paramLong);
  }
  
  private long nextAmbientLightBrighteningTransition(long paramLong, float paramFloat)
  {
    int j = this.mAmbientLightRingBuffer.size();
    int i = j - 1;
    if ((i < 0) || (this.mAmbientLightRingBuffer.getLux(i) <= this.mBrighteningLuxThreshold)) {
      if (this.mLastObservedLux - paramFloat <= 3000.0F) {
        break label112;
      }
    }
    label112:
    for (long l = this.mBrighteningLightFastDebounceConfig;; l = this.mBrighteningLightDebounceConfig)
    {
      if ((j != 2) || (this.mAmbientLightRingBuffer.getTime(j - 1) - this.mAmbientLightRingBuffer.getTime(j - 2) >= 100L)) {
        break label121;
      }
      return paramLong;
      paramLong = this.mAmbientLightRingBuffer.getTime(i);
      i -= 1;
      break;
    }
    label121:
    return paramLong + l;
  }
  
  private long nextAmbientLightDarkeningTransition(long paramLong, float paramFloat)
  {
    int j = this.mAmbientLightRingBuffer.size();
    int i = j - 1;
    for (;;)
    {
      if ((i < 0) || (this.mAmbientLightRingBuffer.getLux(i) >= this.mDarkeningLuxThreshold))
      {
        if ((j != 2) || (this.mAmbientLightRingBuffer.getTime(j - 1) - this.mAmbientLightRingBuffer.getTime(j - 2) >= 100L)) {
          break;
        }
        return paramLong;
      }
      paramLong = this.mAmbientLightRingBuffer.getTime(i);
      i -= 1;
    }
    return this.mDarkeningLightDebounceConfig + paramLong;
  }
  
  private void prepareBrightnessAdjustmentSample()
  {
    float f;
    if (!this.mBrightnessAdjustmentSamplePending)
    {
      this.mBrightnessAdjustmentSamplePending = true;
      this.mBrightnessAdjustmentSampleOldAdjustment = this.mScreenAutoBrightnessAdjustment;
      if (this.mAmbientLuxValid)
      {
        f = this.mAmbientLux;
        this.mBrightnessAdjustmentSampleOldLux = f;
        this.mBrightnessAdjustmentSampleOldBrightness = this.mScreenAutoBrightness;
        this.mBrightnessAdjustmentSampleOldGamma = this.mLastScreenAutoBrightnessGamma;
      }
    }
    for (;;)
    {
      this.mHandler.sendEmptyMessageDelayed(2, 10000L);
      return;
      f = -1.0F;
      break;
      this.mHandler.removeMessages(2);
    }
  }
  
  private void resetAutoBrightness(float paramFloat1, float paramFloat2, int paramInt)
  {
    float f = 0.0F;
    int i = 0;
    int j = sBrightnessControllerUtility.resetAmbientLux(paramFloat1);
    int k = clampScreenBrightness(Math.round(255.0F * this.mScreenAutoBrightnessSpline.interpolate(paramFloat2)));
    int m = sBrightnessControllerUtility.resetAmbientLux(paramFloat2);
    paramFloat1 = f;
    if (k != 0)
    {
      paramFloat2 = Math.abs(paramInt - k) / k;
      paramFloat1 = paramFloat2;
      if (paramFloat2 >= 0.3F) {
        paramFloat1 = 0.3F;
      }
    }
    if (Math.abs(j - m) <= 3) {
      i = Math.round(this.mScreenAutoBrightness * paramFloat1 * (1.0F - Math.abs(j - m) * 0.25F));
    }
    if (paramInt > k) {
      this.mScreenAutoBrightness += i;
    }
    for (;;)
    {
      this.mScreenAutoBrightness = clampScreenBrightness(this.mScreenAutoBrightness);
      if (DEBUG) {
        Slog.d("AutomaticBrightnessController", "brightness = " + k + " manulBrihgtness = " + paramInt + " manullux = " + m + " nowlux = " + j + " mScreenAutoBrightness = " + this.mScreenAutoBrightness + " step = " + i + " scal = " + paramFloat1);
      }
      return;
      if (paramInt < k) {
        this.mScreenAutoBrightness -= i;
      }
    }
  }
  
  private void setAmbientLux(float paramFloat)
  {
    this.mAmbientLux = paramFloat;
    this.mBrighteningLuxThreshold = (this.mAmbientLux * 1.0F);
    this.mDarkeningLuxThreshold = (this.mAmbientLux * 1.0F);
  }
  
  private boolean setLightSensorEnabled(boolean paramBoolean)
  {
    boolean bool = false;
    if (paramBoolean)
    {
      if (!this.mLightSensorEnabled)
      {
        paramBoolean = bool;
        if (DisplayPowerController.DEBUG_ONEPLUS) {
          paramBoolean = true;
        }
        DEBUG_BACKLIGHT = paramBoolean;
        this.mLightSensorEnabled = true;
        this.mLightSensorEnableTime = SystemClock.uptimeMillis();
        PowerManagerService.mUseAutoBrightness = true;
        if (PowerManagerService.sBrightnessBoost == 1)
        {
          Thread localThread = new Thread(new Runnable()
          {
            public void run()
            {
              AutomaticBrightnessController.-get19(AutomaticBrightnessController.this).registerListener(AutomaticBrightnessController.-get13(AutomaticBrightnessController.this), AutomaticBrightnessController.-get11(AutomaticBrightnessController.this), AutomaticBrightnessController.-get14(AutomaticBrightnessController.this) * 1000, AutomaticBrightnessController.-get6(AutomaticBrightnessController.this));
              AutomaticBrightnessController.-get19(AutomaticBrightnessController.this).registerListener(AutomaticBrightnessController.-get17(AutomaticBrightnessController.this), AutomaticBrightnessController.-get16(AutomaticBrightnessController.this), AutomaticBrightnessController.-get14(AutomaticBrightnessController.this) * 1000, AutomaticBrightnessController.-get6(AutomaticBrightnessController.this));
            }
          }, "LightSensorEnableThread");
          localThread.setPriority(10);
          localThread.start();
        }
        for (;;)
        {
          this.mAmbientLuxMin = 0.0F;
          this.mAmbientLuxMax = 0.0F;
          this.mLastAmbientLuxMin = 0.0F;
          this.mLastAmbientLuxMax = 0.0F;
          this.mFirst_lux = true;
          return true;
          this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, this.mLightSensorRate * 1000, this.mHandler);
          this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, this.mLightSensorRate * 1000, this.mHandler);
        }
      }
    }
    else if (this.mLightSensorEnabled)
    {
      DEBUG_BACKLIGHT = false;
      this.mLightSensorEnabled = false;
      if (!this.mResetAmbientLuxAfterWarmUpConfig) {
        break label279;
      }
    }
    label279:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mAmbientLuxValid = paramBoolean;
      this.mRecentLightSamples = 0;
      this.mAmbientLightRingBuffer.clear();
      this.mInitialHorizonAmbientLightRingBuffer.clear();
      this.mHandler.removeMessages(1);
      this.mSensorManager.unregisterListener(this.mLightSensorListener);
      this.mManulBrightnessSlide = false;
      PowerManagerService.mManualBrightness = 0;
      this.mStartManual = false;
      PowerManagerService.mUseAutoBrightness = false;
      this.mSensorManager.unregisterListener(this.mProximitySensorListener);
      this.mAmbientState = 0;
      this.mProximityPositive = false;
      this.mFirst_lux = false;
      return false;
    }
  }
  
  private boolean setScreenAutoBrightnessAdjustment(float paramFloat)
  {
    if (paramFloat != this.mScreenAutoBrightnessAdjustment)
    {
      this.mScreenAutoBrightnessAdjustment = paramFloat;
      if ((this.mLightSensorEnabled) && (this.mScreenAutoBrightnessAdjustment != 300.0F) && (this.mScreenAutoBrightnessAdjustment != 500.0F))
      {
        if (this.mScreenAutoBrightnessAdjustment != 0.0F) {
          break label71;
        }
        this.mManulBrightnessSlide = false;
        this.mStartManual = false;
      }
      for (;;)
      {
        PowerManagerService.mManualBrightness = Math.round(paramFloat);
        return true;
        label71:
        this.mManulBrightnessSlide = true;
      }
    }
    return false;
  }
  
  private boolean setUseTwilight(boolean paramBoolean)
  {
    if (this.mUseTwilight == paramBoolean) {
      return false;
    }
    if (paramBoolean) {
      this.mTwilight.registerListener(this.mTwilightListener, this.mHandler);
    }
    for (;;)
    {
      this.mUseTwilight = paramBoolean;
      return true;
      this.mTwilight.unregisterListener(this.mTwilightListener);
    }
  }
  
  private void startZeroTimer()
  {
    try
    {
      if (this.mZeroTimer == null) {
        this.mZeroTimer = new Timer();
      }
      if (this.mZeroTask == null) {
        this.mZeroTask = new TimerTask()
        {
          public void run()
          {
            Message localMessage = new Message();
            localMessage.what = 1;
            AutomaticBrightnessController.-get23(AutomaticBrightnessController.this).sendMessage(localMessage);
          }
        };
      }
      if ((this.mZeroTimer != null) && (this.mZeroTask != null)) {
        this.mZeroTimer.schedule(this.mZeroTask, 5000L, this.mDarkeningLightDebounceConfig);
      }
      return;
    }
    finally {}
  }
  
  private void stopZeroTimer()
  {
    if (!this.mbStartTimer) {
      return;
    }
    try
    {
      this.mbStartTimer = false;
      if (this.mZeroTimer != null)
      {
        this.mZeroTimer.cancel();
        this.mZeroTimer = null;
      }
      if (this.mZeroTask != null)
      {
        this.mZeroTask.cancel();
        this.mZeroTask = null;
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        Slog.i("AutomaticBrightnessController", "stopZeroTimer null pointer" + localNullPointerException);
      }
    }
    finally {}
  }
  
  private void updateAmbientLux()
  {
    long l = SystemClock.uptimeMillis();
    this.mAmbientLightRingBuffer.prune(l - this.mAmbientLightHorizon);
    updateAmbientLux(l);
  }
  
  private void updateAmbientLux(long paramLong)
  {
    long l1;
    if (!this.mAmbientLuxValid)
    {
      l1 = this.mLightSensorWarmUpTimeConfig + this.mLightSensorEnableTime;
      if (paramLong < l1)
      {
        if (DEBUG) {
          Slog.d("AutomaticBrightnessController", "updateAmbientLux: Sensor not  ready yet: time=" + paramLong + ", timeWhenSensorWarmedUp=" + l1);
        }
        this.mHandler.sendEmptyMessageAtTime(1, l1);
        return;
      }
      setAmbientLux(calculateAmbientLux(paramLong));
      this.mAmbientLuxValid = true;
      if (DEBUG) {
        Slog.d("AutomaticBrightnessController", "updateAmbientLux: Initializing: mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer + ", mAmbientLux=" + this.mAmbientLux);
      }
      updateAutoBrightness(true);
    }
    float f = calculateAmbientLux(paramLong);
    int i = 0;
    Object localObject = sBrightnessControllerUtility;
    label276:
    long l2;
    if (i < BrightnessControllerUtility.BRIGHTNESS_LEVELS)
    {
      double d = Math.rint(f);
      localObject = sBrightnessControllerUtility;
      if (d != BrightnessControllerUtility.mAmbientLuxConfig[i]) {}
    }
    else
    {
      localObject = sBrightnessControllerUtility;
      if (i >= BrightnessControllerUtility.BRIGHTNESS_LEVELS) {
        break label672;
      }
      localObject = sBrightnessControllerUtility;
      if (BrightnessControllerUtility.mAmbientLuxMinConfig[i] != this.mAmbientLuxMin)
      {
        localObject = sBrightnessControllerUtility;
        if (BrightnessControllerUtility.mAmbientLuxMinConfig[i] != this.mAmbientLuxMax)
        {
          localObject = sBrightnessControllerUtility;
          this.mAmbientLuxMin = BrightnessControllerUtility.mAmbientLuxMinConfig[i];
          localObject = sBrightnessControllerUtility;
          this.mAmbientLuxMax = BrightnessControllerUtility.mAmbientLuxMinConfig[i];
        }
      }
      l1 = nextAmbientLightBrighteningTransition(paramLong, f);
      l2 = nextAmbientLightDarkeningTransition(paramLong, f);
      if (this.mAmbientState == 2) {
        paramLong = 0L;
      }
      StringBuilder localStringBuilder;
      if (DEBUG)
      {
        localStringBuilder = new StringBuilder().append("Pre-updateAmbientLux: ");
        if (f <= this.mAmbientLux) {
          break label719;
        }
        localObject = "Brightened";
        label340:
        Slog.d("AutomaticBrightnessController", (String)localObject + ": " + "mBrighteningLuxThreshold=" + this.mBrighteningLuxThreshold + ", mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer + ", mAmbientLux=" + this.mAmbientLux + ", mAmbientState=" + this.mAmbientState);
      }
      if ((f < this.mBrighteningLuxThreshold) || (l1 > paramLong) || (this.mAmbientState != 1)) {
        break label727;
      }
      label439:
      setAmbientLux(f);
      if (DEBUG)
      {
        localStringBuilder = new StringBuilder().append("updateAmbientLux: ");
        if (f <= this.mAmbientLux) {
          break label762;
        }
        localObject = "Brightened";
        label481:
        Slog.d("AutomaticBrightnessController", (String)localObject + ": " + "mBrighteningLuxThreshold=" + this.mBrighteningLuxThreshold + ", mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer + ", mAmbientLux=" + this.mAmbientLux);
      }
      this.mAmbientState = 0;
      this.mLastAmbientLuxMin = this.mAmbientLuxMin;
      this.mLastAmbientLuxMax = this.mAmbientLuxMax;
      this.mLastAmbientLightZone = this.mAmbientLightZone;
      updateAutoBrightness(true);
      l1 = nextAmbientLightBrighteningTransition(paramLong, f);
      l2 = nextAmbientLightDarkeningTransition(paramLong, f);
      label594:
      l1 = Math.min(l2, l1);
      if (l1 <= paramLong) {
        break label770;
      }
    }
    label672:
    label719:
    label727:
    label762:
    label770:
    for (paramLong = l1;; paramLong += this.mLightSensorRate)
    {
      if (DEBUG) {
        Slog.d("AutomaticBrightnessController", "updateAmbientLux: Scheduling ambient lux update for " + paramLong + TimeUtils.formatUptime(paramLong));
      }
      this.mHandler.sendEmptyMessageAtTime(1, paramLong);
      return;
      i += 1;
      break;
      if (!DEBUG) {
        break label276;
      }
      Slog.d("AutomaticBrightnessController", "The lux_index is illegal ambientLux = " + f + " lux_index=" + i);
      break label276;
      localObject = "Darkened";
      break label340;
      if ((f <= this.mDarkeningLuxThreshold) && (l2 <= paramLong) && (this.mAmbientState == 1)) {
        break label439;
      }
      if (f != 0.0F) {
        break label594;
      }
      break label439;
      localObject = "Darkened";
      break label481;
    }
  }
  
  private void updateAutoBrightness(boolean paramBoolean)
  {
    if ((this.mAmbientLuxValid) || (this.mManulBrightnessSlide)) {}
    float f;
    while (!this.mLightSensorEnabled)
    {
      f = this.mScreenAutoBrightnessSpline.interpolate(this.mAmbientLux);
      if (!this.mManulBrightnessSlide) {
        break;
      }
      if ((DEBUG) || (DEBUG_BACKLIGHT)) {
        Slog.d("AutomaticBrightnessController", "PowerManagerService.mManualBrightness = " + PowerManagerService.mManualBrightness + " mAmbientLux = " + this.mAmbientLux);
      }
      PowerManagerService.mManulAtAmbientLux = this.mAmbientLux;
      this.mStartManual = true;
      this.mManulBrightnessSlide = false;
      PowerManagerService.mManualSetAutoBrightness = true;
      this.mScreenAutoBrightness = PowerManagerService.mManualBrightness;
      this.mCallbacks.updateBrightness();
      return;
    }
    return;
    PowerManagerService.mManualSetAutoBrightness = false;
    int i = clampScreenBrightness(Math.round(255.0F * f));
    i = sBrightnessControllerUtility.getNightBrightness(i);
    if (this.mScreenAutoBrightness != i)
    {
      if ((DEBUG) || (DEBUG_BACKLIGHT)) {
        Slog.d("AutomaticBrightnessController", "mScreenAutoBrightness = " + this.mScreenAutoBrightness + " newScreenAutoBrightness = " + i + " PowerManagerService.mManualBrightness = " + PowerManagerService.mManualBrightness + " mStartManual = " + this.mStartManual + " PowerManagerService.mManualBrightnessBackup = " + PowerManagerService.mManualBrightnessBackup + " PowerManagerService.mDisplayStateOn = " + PowerManagerService.mDisplayStateOn + " mBrightnessOverride = " + PowerManagerService.mBrightnessOverride);
      }
      int j = this.mScreenAutoBrightness;
      this.mScreenAutoBrightness = i;
      this.mLastScreenAutoBrightnessGamma = 1.0F;
      if ((PowerManagerService.mDisplayStateOn) && (PowerManagerService.mManualBrightnessBackup != 0))
      {
        this.mStartManual = true;
        PowerManagerService.mManualBrightness = PowerManagerService.mManualBrightnessBackup;
        PowerManagerService.mManulAtAmbientLux = PowerManagerService.mManualAmbientLuxBackup;
        PowerManagerService.mManualBrightnessBackup = 0;
        PowerManagerService.mManualAmbientLuxBackup = 0.0F;
      }
      if ((PowerManagerService.mBrightnessOverride == 2) && (PowerManagerService.mBrightnessOverrideAdj != 0) && (this.mLightSensorEnabled))
      {
        this.mStartManual = true;
        PowerManagerService.mManualBrightness = PowerManagerService.mBrightnessOverrideAdj;
        PowerManagerService.mBrightnessOverride = 0;
        PowerManagerService.mBrightnessOverrideAdj = 0;
      }
      if ((this.mStartManual) && (this.mLightSensorEnabled))
      {
        if (this.mAmbientLux != PowerManagerService.mManulAtAmbientLux) {
          break label453;
        }
        this.mScreenAutoBrightness = PowerManagerService.mManualBrightness;
      }
    }
    for (;;)
    {
      if (paramBoolean) {
        this.mCallbacks.updateBrightness();
      }
      if ((PowerManagerService.mDisplayStateOn) && (this.mLightSensorEnabled) && (PowerManagerService.mManualBrightnessBackup != 0))
      {
        this.mStartManual = true;
        PowerManagerService.mManualBrightness = PowerManagerService.mManualBrightnessBackup;
        PowerManagerService.mManualBrightnessBackup = 0;
      }
      PowerManagerService.mDisplayStateOn = false;
      return;
      label453:
      resetAutoBrightness(this.mAmbientLux, PowerManagerService.mManulAtAmbientLux, PowerManagerService.mManualBrightness);
      if (this.mAmbientLux > PowerManagerService.mManulAtAmbientLux) {
        this.mScreenAutoBrightness = Math.max(this.mScreenAutoBrightness, PowerManagerService.mManualBrightness);
      } else if (this.mAmbientLux < PowerManagerService.mManulAtAmbientLux) {
        this.mScreenAutoBrightness = Math.min(this.mScreenAutoBrightness, PowerManagerService.mManualBrightness);
      }
    }
  }
  
  private float weightIntegral(long paramLong)
  {
    return (float)paramLong * ((float)paramLong * 0.5F + this.mWeightingIntercept);
  }
  
  public void configure(boolean paramBoolean1, float paramFloat, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.mDozing = paramBoolean2;
    boolean bool;
    if ((!paramBoolean1) || (paramBoolean2))
    {
      bool = false;
      if ((setLightSensorEnabled(bool) | setScreenAutoBrightnessAdjustment(paramFloat) | setUseTwilight(paramBoolean4))) {
        updateAutoBrightness(false);
      }
      if ((paramBoolean1) && (!paramBoolean2)) {
        break label58;
      }
    }
    label58:
    while (!paramBoolean3)
    {
      return;
      bool = true;
      break;
    }
    prepareBrightnessAdjustmentSample();
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println();
    paramPrintWriter.println("Automatic Brightness Controller Configuration:");
    paramPrintWriter.println("  mScreenAutoBrightnessSpline=" + this.mScreenAutoBrightnessSpline);
    paramPrintWriter.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
    paramPrintWriter.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
    paramPrintWriter.println("  mLightSensorWarmUpTimeConfig=" + this.mLightSensorWarmUpTimeConfig);
    paramPrintWriter.println("  mBrighteningLightDebounceConfig=" + this.mBrighteningLightDebounceConfig);
    paramPrintWriter.println("  mBrighteningLightFastDebounceConfig=" + this.mBrighteningLightFastDebounceConfig);
    paramPrintWriter.println("  mDarkeningLightDebounceConfig=" + this.mDarkeningLightDebounceConfig);
    paramPrintWriter.println("  mResetAmbientLuxAfterWarmUpConfig=" + this.mResetAmbientLuxAfterWarmUpConfig);
    paramPrintWriter.println();
    paramPrintWriter.println("Automatic Brightness Controller State:");
    paramPrintWriter.println("  mLightSensor=" + this.mLightSensor);
    paramPrintWriter.println("  mTwilight.getLastTwilightState()=" + this.mTwilight.getLastTwilightState());
    paramPrintWriter.println("  mLightSensorEnabled=" + this.mLightSensorEnabled);
    paramPrintWriter.println("  mLightSensorEnableTime=" + TimeUtils.formatUptime(this.mLightSensorEnableTime));
    paramPrintWriter.println("  mAmbientLux=" + this.mAmbientLux);
    paramPrintWriter.println("  mAmbientLightHorizon=" + this.mAmbientLightHorizon);
    paramPrintWriter.println("  mBrighteningLuxThreshold=" + this.mBrighteningLuxThreshold);
    paramPrintWriter.println("  mDarkeningLuxThreshold=" + this.mDarkeningLuxThreshold);
    paramPrintWriter.println("  mLastObservedLux=" + this.mLastObservedLux);
    paramPrintWriter.println("  mLastObservedLuxTime=" + TimeUtils.formatUptime(this.mLastObservedLuxTime));
    paramPrintWriter.println("  mRecentLightSamples=" + this.mRecentLightSamples);
    paramPrintWriter.println("  mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer);
    paramPrintWriter.println("  mInitialHorizonAmbientLightRingBuffer=" + this.mInitialHorizonAmbientLightRingBuffer);
    paramPrintWriter.println("  mScreenAutoBrightness=" + this.mScreenAutoBrightness);
    paramPrintWriter.println("  mScreenAutoBrightnessAdjustment=" + this.mScreenAutoBrightnessAdjustment);
    paramPrintWriter.println("  mScreenAutoBrightnessAdjustmentMaxGamma=" + this.mScreenAutoBrightnessAdjustmentMaxGamma);
    paramPrintWriter.println("  mLastScreenAutoBrightnessGamma=" + this.mLastScreenAutoBrightnessGamma);
    paramPrintWriter.println("  mDozing=" + this.mDozing);
  }
  
  public int getAutomaticScreenBrightness()
  {
    if (this.mDozing) {
      return (int)(this.mScreenAutoBrightness * this.mDozeScaleFactor);
    }
    return this.mScreenAutoBrightness;
  }
  
  public int getAutomaticScreenRate()
  {
    return this.mScreenAutoRate;
  }
  
  private static final class AmbientLightRingBuffer
  {
    private static final float BUFFER_SLACK = 1.5F;
    private int mCapacity;
    private int mCount;
    private int mEnd;
    private float[] mRingLux;
    private long[] mRingTime;
    private int mStart;
    
    public AmbientLightRingBuffer(long paramLong, int paramInt)
    {
      this.mCapacity = ((int)Math.ceil(paramInt * 1.5F / (float)paramLong));
      this.mRingLux = new float[this.mCapacity];
      this.mRingTime = new long[this.mCapacity];
    }
    
    private int offsetOf(int paramInt)
    {
      if ((paramInt >= this.mCount) || (paramInt < 0)) {
        throw new ArrayIndexOutOfBoundsException(paramInt);
      }
      int i = paramInt + this.mStart;
      paramInt = i;
      if (i >= this.mCapacity) {
        paramInt = i - this.mCapacity;
      }
      return paramInt;
    }
    
    public void clear()
    {
      this.mStart = 0;
      this.mEnd = 0;
      this.mCount = 0;
    }
    
    public float getLux(int paramInt)
    {
      return this.mRingLux[offsetOf(paramInt)];
    }
    
    public long getTime(int paramInt)
    {
      return this.mRingTime[offsetOf(paramInt)];
    }
    
    public void prune(long paramLong)
    {
      if (this.mCount == 0) {
        return;
      }
      int i;
      do
      {
        this.mStart = i;
        this.mCount -= 1;
        if (this.mCount <= 1) {
          break;
        }
        int j = this.mStart + 1;
        i = j;
        if (j >= this.mCapacity) {
          i = j - this.mCapacity;
        }
      } while (this.mRingTime[i] <= paramLong);
      if (this.mRingTime[this.mStart] < paramLong) {
        this.mRingTime[this.mStart] = paramLong;
      }
    }
    
    public void push(long paramLong, float paramFloat)
    {
      int i = this.mEnd;
      if (this.mCount == this.mCapacity)
      {
        int j = this.mCapacity * 2;
        float[] arrayOfFloat = new float[j];
        long[] arrayOfLong = new long[j];
        i = this.mCapacity - this.mStart;
        System.arraycopy(this.mRingLux, this.mStart, arrayOfFloat, 0, i);
        System.arraycopy(this.mRingTime, this.mStart, arrayOfLong, 0, i);
        if (this.mStart != 0)
        {
          System.arraycopy(this.mRingLux, 0, arrayOfFloat, i, this.mStart);
          System.arraycopy(this.mRingTime, 0, arrayOfLong, i, this.mStart);
        }
        this.mRingLux = arrayOfFloat;
        this.mRingTime = arrayOfLong;
        i = this.mCapacity;
        this.mCapacity = j;
        this.mStart = 0;
      }
      this.mRingTime[i] = paramLong;
      this.mRingLux[i] = paramFloat;
      this.mEnd = (i + 1);
      if (this.mEnd == this.mCapacity) {
        this.mEnd = 0;
      }
      this.mCount += 1;
    }
    
    public int size()
    {
      return this.mCount;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append('[');
      int i = 0;
      if (i < this.mCount)
      {
        if (i + 1 < this.mCount) {}
        for (long l = getTime(i + 1);; l = SystemClock.uptimeMillis())
        {
          if (i != 0) {
            localStringBuffer.append(", ");
          }
          localStringBuffer.append(getLux(i));
          localStringBuffer.append(" / ");
          localStringBuffer.append(l - getTime(i));
          localStringBuffer.append("ms");
          i += 1;
          break;
        }
      }
      localStringBuffer.append(']');
      return localStringBuffer.toString();
    }
  }
  
  private final class AutomaticBrightnessHandler
    extends Handler
  {
    public AutomaticBrightnessHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        AutomaticBrightnessController.-wrap4(AutomaticBrightnessController.this);
        return;
      }
      AutomaticBrightnessController.-wrap0(AutomaticBrightnessController.this);
    }
  }
  
  static abstract interface Callbacks
  {
    public abstract void updateBrightness();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/AutomaticBrightnessController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */