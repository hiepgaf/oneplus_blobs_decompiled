package com.android.server.display;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityThread;
import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.IColorBalanceManager.Stub;
import android.hardware.display.SDManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.MathUtils;
import android.util.Slog;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.app.NightDisplayController.LocalTime;
import com.android.server.SystemService;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import com.oem.os.IOemExService;
import com.oem.os.IOemExService.Stub;
import com.qti.snapdragon.sdk.display.ColorManager;
import com.qti.snapdragon.sdk.display.ColorManager.ColorManagerListener;
import com.qti.snapdragon.sdk.display.ColorManager.DCM_DISPLAY_TYPE;
import com.qti.snapdragon.sdk.display.ColorManager.MODE_TYPE;
import com.qti.snapdragon.sdk.display.ModeInfo;
import com.qti.snapdragon.sdk.display.PictureAdjustmentConfig;
import com.qti.snapdragon.sdk.display.PictureAdjustmentConfig.PICTURE_ADJUSTMENT_PARAMS;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ColorBalanceService
  extends SystemService
  implements NightDisplayController.Callback
{
  private static final int AUTO_ADJUSTMENT_SHADING_TIME = 0;
  private static final int COLOR_DELTA = 90;
  private static final ColorMatrixEvaluator COLOR_MATRIX_EVALUATOR = new ColorMatrixEvaluator(null);
  private static final float COLOR_MATRIX_FACTOR_DELTA = 0.0125F;
  private static final int COLOR_MATRIX_FACTOR_DELTA_SHADING_DELAY = 15;
  private static final boolean DEBUG = false;
  private static final int DEFAULT_COLORBALANCE = 43;
  private static final boolean IS_QCOM_SAVE_MODE_USED = false;
  private static final float[] MATRIX_IDENTITY = new float[16];
  private static final int MODE_CREATE_ING = 1;
  private static final int MODE_CREATE_NONE = 0;
  private static final int MODE_NIGHT = 1;
  private static final int MODE_NIGHT_OFF = 3;
  private static final int MODE_NULL = 0;
  private static final int MODE_READING = 2;
  private static final int MODE_READING_OFF = 4;
  private static final int MODE_READING_OFF_AUTO = 5;
  private static final int MSG_ACTIVE_MODE = 5;
  private static final int MSG_CHECK_LIGHT = 16;
  private static final int MSG_CHECK_SRGBSEN = 15;
  private static final int MSG_DEFAULT_MODE = 6;
  private static final int MSG_NIGHT2READING = 13;
  private static final int MSG_NIGHT_ENVIRONMENT_CHANGED = 8;
  private static final int MSG_NIGHT_SWITCH = 10;
  private static final int MSG_READING2NIGHT = 14;
  private static final int MSG_READING_ENVIRONMENT_CHANGED = 9;
  private static final int MSG_READING_SWITCH = 11;
  private static final int MSG_REVERT_STATUS = 12;
  private static final int MSG_SAVE_MODE = 4;
  private static final int MSG_SCREEN_AFTER_ON = 7;
  private static final int MSG_SCREEN_OFF = 2;
  private static final int MSG_SCREEN_ON = 1;
  private static final int MSG_SET_COLORBALANCE = 3;
  private static final int NIGHT_MODE_SEEKBAR_DEFAULT = 103;
  private static final int NIGHT_MODE_SEEKBAR_MAX = 132;
  private static final int NIGHT_STAGE_CLOSED = 4;
  private static final int NIGHT_STAGE_CLOSING = 3;
  private static final int NIGHT_STAGE_OPENED = 2;
  private static final int NIGHT_STAGE_OPENING = 1;
  private static final String OP_SYS_DCIP3_PROPERTY = "sys.dci3p";
  private static final String OP_SYS_NIGHT_MODE = "sys.night_mode";
  private static final String OP_SYS_SRGB_PROPERTY = "sys.srgb";
  private static final int READING_INIT_COLOR = -20;
  private static final int READING_STAGE_CLOSED = 8;
  private static final int READING_STAGE_CLOSING = 7;
  private static final int READING_STAGE_OPENED = 6;
  private static final int READING_STAGE_OPENING = 5;
  private static final int SENSOR_TYPE_RGB = 33171020;
  private static final int STAGE_NULL = 0;
  private static final String TAG = "ColorBalanceService";
  private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0F;
  private static int[] config_colorbalance_reading_mode = { 35, 42, 58, 71, 75, 78 };
  private static int[] config_colorbalance_reading_mode_SRGB_and_P3 = { 54, 61, 78, 89, 93, 98 };
  int[] colortemprature = { 2979, 2981, 3104, 3104, 3172, 3172, 3171, 3297, 3296, 3406, 3407, 3405, 3536, 3533, 3641, 3641, 3640, 3790, 3789, 3860, 3863, 3862, 4008, 4009, 4052, 4052, 4052, 4173, 4173, 4271, 4271, 4271, 4369, 4368, 4461, 4460, 4460, 4553, 4553, 4637, 4636, 4635, 4704, 4705, 4794, 4794, 4792, 4903, 4901, 4975, 4976, 4976, 5079, 5079, 5175, 5175, 5175, 5276, 5277, 5355, 5354, 5353, 5416, 5416, 5554, 5554, 5555, 5629, 5629, 5719, 5718, 5718, 5772, 5771, 5871, 5872, 5981, 5981, 6057, 6057, 6056, 6097, 6084, 6161, 6160, 6160, 6213, 6214, 6315, 6315, 6314, 6362, 6362, 6442, 6441, 6440, 6506, 6505, 6563, 6563, 6562, 6649, 6650, 6737, 6738, 6737, 6824, 6824, 6870, 6868, 6868, 6975, 6976, 7006, 7007, 7006, 7072, 7072, 7106, 7103, 7104, 7168, 7168, 7218, 7215, 7214, 7297, 7297, 7395, 7398, 7395, 7432 };
  int[] config_autocolortemp = { 2979, 3104, 3172, 3296, 3407, 3536, 3641, 3790, 3863, 4008, 4052, 4173, 4271, 4369, 4461, 4553, 4636, 4704, 4794, 4903, 4975, 5079, 5175, 5276, 5355, 5416, 5554, 5629, 5719, 5772, 5871, 5981, 6057, 6097, 6161, 6213, 6315, 6362, 6442, 6506, 6563, 6649, 6737, 6824, 6870, 6975, 7006, 7072, 7106, 7168, 7218, 7297, 7398, 7432 };
  int[] config_autoseekbar = { 1, 3, 5, 8, 10, 13, 16, 18, 21, 23, 25, 28, 30, 33, 35, 38, 40, 43, 45, 48, 50, 53, 55, 58, 60, 63, 65, 68, 70, 73, 75, 78, 80, 83, 85, 88, 90, 93, 95, 98, 100, 103, 105, 108, 110, 113, 115, 118, 120, 123, 125, 128, 130, 132 };
  int[][] config_colorbalance;
  private DisplayTransformManager dtm;
  private AutoMode mAutoMode;
  private int mAverageColor = 0;
  private boolean mBootCompleted;
  private int mBootPhase;
  private final CMH mCMHHandler;
  private ColorManager mCmgr;
  private HandlerThread mColorBalanceThread;
  private float[] mColorMatrix = { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.754F, 0.0F, 0.0F, 0.0F, 0.0F, 0.516F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
  private ValueAnimator mColorMatrixAnimator;
  private Context mContext;
  private NightDisplayController mController;
  private int mCreateModeState = 0;
  private int mCurrentColorBalance = 46;
  private int mCurrentNightColorbalance = 0;
  private int mCurrentReadingColorbalance = 0;
  private int mCurrentSrgbSensorAverageColor = 0;
  private int mCurrentUser = 55536;
  private float mFactor = 1.0F;
  private int mGeneration = 0;
  private final Handler mHandler;
  private final AtomicBoolean mIgnoreAllColorMatrixChanges = new AtomicBoolean();
  private Boolean mIsColorManagerConnected = Boolean.valueOf(false);
  private Boolean mIsDisableByApp = Boolean.valueOf(false);
  private Boolean mIsNightModeActivated;
  private Boolean mIsNightModeSettingFromeUser = Boolean.valueOf(true);
  private Boolean mIsReadingModeActivated = Boolean.valueOf(false);
  private Boolean mIsReadingModeActivatedAuto = Boolean.valueOf(false);
  private Boolean mIsReadingModeSettingFromeUser = Boolean.valueOf(true);
  private Boolean mIsReadingOrNightModeOpendLastSesson;
  private Boolean mIsScreenOn = Boolean.valueOf(false);
  private Boolean mIsTimeActivated = Boolean.valueOf(false);
  private int mLightBrightness = 0;
  private int mLightGeneration = 0;
  private SensorEventListener mLightSensorListener;
  private final Object mLock;
  private int mLowLightCount = 0;
  private int mMode = 0;
  private int mModeEnable = 0;
  private int mModeId = -1;
  private ArrayList<ModeInfoWrapper> mModeList;
  private int mModeStage = 0;
  private int mNight2ReadingModeStage = 0;
  private int mNightDisplayMoede = 0;
  private int mNightModeAutoStatus = 0;
  private int mNightModeClosingStage = 0;
  private int mNightModeOpingStage = 0;
  private Boolean mNightModeStatus = Boolean.valueOf(false);
  private IOemExService mOemExSvc;
  private PowerManager mPowerManager;
  private int mPreColorTemp = 0;
  private int mPretNightColorbalance = 0;
  private int mReading2NightModeStage = 0;
  private int mReadingModeClosingStage = 0;
  private int mReadingModeOpingStage = 0;
  private Boolean mReadingModeStatus = Boolean.valueOf(false);
  private SDManager mSDM;
  private boolean mSRGBSensorEnabled;
  private SensorEventListener mSRGBSensorListener;
  private int mSensorColortemperature;
  private SensorManager mSensorManager;
  private int mSrgbSensorGeneration = 0;
  private int mStableColor = 0;
  private int mStableCount = 0;
  private int mStartSetCount = 0;
  private int mStopSetCount = 0;
  private ContentObserver mUserSetupObserver;
  
  static
  {
    Matrix.setIdentityM(MATRIX_IDENTITY, 0);
  }
  
  public ColorBalanceService(Context paramContext)
  {
    super(paramContext);
    int[] arrayOfInt1 = { 1, 3, 5, 5, 10, 13, 16 };
    int[] arrayOfInt2 = { 3, 5, 8, 10, 16, 21 };
    int[] arrayOfInt3 = { 5, 8, 10, 13, 18, 23 };
    int[] arrayOfInt4 = { 13, 25, 30, 33, 40, 45 };
    int[] arrayOfInt5 = { 16, 20, 28, 38, 48, 53 };
    int[] arrayOfInt6 = { 18, 25, 35, 45, 53, 58 };
    int[] arrayOfInt7 = { 23, 33, 43, 53, 60, 65 };
    int[] arrayOfInt8 = { 30, 40, 50, 60, 68, 73 };
    int[] arrayOfInt9 = { 33, 43, 53, 63, 70, 75 };
    int[] arrayOfInt10 = { 43, 53, 63, 73, 80, 85 };
    int[] arrayOfInt11 = { 48, 58, 68, 78, 85, 90 };
    int[] arrayOfInt12 = { 53, 63, 73, 83, 90, 95 };
    int[] arrayOfInt13 = { 45, 63, 73, 85, 93, 98 };
    int[] arrayOfInt14 = { 55, 73, 83, 95, 103, 108 };
    int[] arrayOfInt15 = { 63, 80, 90, 103, 110, 115 };
    int[] arrayOfInt16 = { 65, 83, 93, 105, 113, 118 };
    int[] arrayOfInt17 = { 70, 88, 98, 110, 118, 123 };
    int[] arrayOfInt18 = { 75, 93, 103, 115, 123, 128 };
    int[] arrayOfInt19 = { 78, 95, 105, 118, 125, 130 };
    int[] arrayOfInt20 = { 80, 95, 108, 120, 128, 132 };
    this.config_colorbalance = new int[][] { { 1, 1, 1, 1, 3, 10, 13 }, { 1, 3, 3, 3, 8, 13, 16 }, arrayOfInt1, { 3, 5, 8, 8, 13, 18 }, arrayOfInt2, arrayOfInt3, { 5, 8, 13, 16, 21, 25 }, { 8, 10, 16, 18, 23, 28 }, { 10, 13, 18, 21, 25, 30 }, { 10, 13, 18, 23, 28, 33 }, { 13, 16, 21, 25, 30, 35 }, { 13, 18, 23, 28, 33, 38 }, { 13, 21, 25, 30, 35, 40 }, { 13, 23, 28, 33, 38, 43 }, arrayOfInt4, { 13, 23, 30, 33, 43, 48 }, { 13, 20, 25, 35, 45, 50 }, arrayOfInt5, { 18, 23, 30, 40, 50, 55 }, arrayOfInt6, { 18, 28, 38, 48, 55, 60 }, { 21, 30, 40, 50, 58, 63 }, arrayOfInt7, { 25, 35, 45, 55, 63, 68 }, { 28, 38, 48, 58, 65, 70 }, arrayOfInt8, arrayOfInt9, { 35, 45, 55, 65, 73, 78 }, { 38, 48, 58, 68, 75, 80 }, { 40, 50, 60, 70, 78, 83 }, arrayOfInt10, { 45, 55, 65, 75, 83, 88 }, arrayOfInt11, { 50, 60, 70, 80, 88, 93 }, arrayOfInt12, arrayOfInt13, { 48, 65, 75, 88, 95, 100 }, { 50, 68, 78, 90, 98, 103 }, { 53, 70, 80, 93, 100, 105 }, arrayOfInt14, { 58, 75, 85, 98, 105, 110 }, { 60, 78, 88, 100, 108, 113 }, arrayOfInt15, arrayOfInt16, { 68, 85, 95, 108, 115, 120 }, arrayOfInt17, { 73, 90, 100, 113, 120, 125 }, arrayOfInt18, arrayOfInt19, arrayOfInt20, { 83, 98, 110, 123, 128, 132 }, { 85, 100, 113, 125, 130, 132 }, { 88, 103, 115, 130, 132, 132 }, { 90, 105, 118, 132, 132, 132 } };
    this.mSensorManager = null;
    this.mSRGBSensorListener = new SensorEventListener()
    {
      public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
      
      public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
      {
        ColorBalanceService.-set13(ColorBalanceService.this, (int)paramAnonymousSensorEvent.values[0]);
        if (ColorBalanceService.-get4(ColorBalanceService.this) == 0) {
          ColorBalanceService.-set0(ColorBalanceService.this, ColorBalanceService.-get20(ColorBalanceService.this));
        }
        for (;;)
        {
          if ((ColorBalanceService.-get19(ColorBalanceService.this)) && (ColorBalanceService.-get11(ColorBalanceService.this) > 35) && (ColorBalanceService.-get16(ColorBalanceService.this) % 2 != 1)) {
            ColorBalanceService.-wrap5(ColorBalanceService.this, ColorBalanceService.-get20(ColorBalanceService.this));
          }
          return;
          ColorBalanceService.-set0(ColorBalanceService.this, (ColorBalanceService.-get4(ColorBalanceService.this) + ColorBalanceService.-get20(ColorBalanceService.this)) / 2);
        }
      }
    };
    this.mLightSensorListener = new SensorEventListener()
    {
      public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
      
      public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
      {
        ColorBalanceService localColorBalanceService;
        if (ColorBalanceService.-get19(ColorBalanceService.this))
        {
          if (((ColorBalanceService.-get11(ColorBalanceService.this) >= 5) && (ColorBalanceService.-get14(ColorBalanceService.this) != 0)) || ((int)paramAnonymousSensorEvent.values[0] >= 5)) {
            break label163;
          }
          if (ColorBalanceService.-get14(ColorBalanceService.this) < 1)
          {
            localColorBalanceService = ColorBalanceService.this;
            ColorBalanceService.-set8(localColorBalanceService, ColorBalanceService.-get14(localColorBalanceService) + 1);
            Slog.i("ColorBalanceService", "mLowLightCount:" + ColorBalanceService.-get14(ColorBalanceService.this));
            if (ColorBalanceService.-get14(ColorBalanceService.this) == 1)
            {
              localColorBalanceService = ColorBalanceService.this;
              ColorBalanceService.-set7(localColorBalanceService, ColorBalanceService.-get12(localColorBalanceService) + 1);
              ColorBalanceService.this.sendMsgWithValueDelayed(16, ColorBalanceService.-get12(ColorBalanceService.this), 0, 1500);
            }
          }
        }
        for (;;)
        {
          ColorBalanceService.-set6(ColorBalanceService.this, (int)paramAnonymousSensorEvent.values[0]);
          return;
          label163:
          ColorBalanceService.-set8(ColorBalanceService.this, 0);
          localColorBalanceService = ColorBalanceService.this;
          ColorBalanceService.-set7(localColorBalanceService, ColorBalanceService.-get12(localColorBalanceService) + 1);
        }
      }
    };
    this.mLock = new Object();
    this.mContext = paramContext;
    this.mHandler = new Handler(Looper.getMainLooper());
    this.mColorBalanceThread = new HandlerThread("ColorBalanceThread");
    this.mColorBalanceThread.start();
    this.mCMHHandler = new CMH(this.mColorBalanceThread.getLooper());
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.ACTION_SHUTDOWN");
    paramContext.setPriority(1000);
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (("android.intent.action.ACTION_SHUTDOWN".equals(paramAnonymousIntent.getAction())) && (ColorBalanceService.-get10(ColorBalanceService.this).booleanValue()))
        {
          if (ColorBalanceService.-get17(ColorBalanceService.this).booleanValue())
          {
            ColorBalanceService.-set2(ColorBalanceService.this, Boolean.valueOf(false));
            ColorBalanceService.-get3(ColorBalanceService.this).setActivated(true);
          }
          Slog.i("ColorBalanceService", "POWER down...");
        }
      }
    }, paramContext);
  }
  
  private void changeNight2ReadingMode(int paramInt1, int paramInt2)
  {
    int j = 1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.mGeneration == paramInt1)
        {
          if (!this.mIsScreenOn.booleanValue())
          {
            setNightModeProp(Boolean.valueOf(true));
            this.mCmgr.setActiveMode(3);
            this.mStableColor = 6800;
            opSetColorBalance(-20, 0);
            Slog.i("ColorBalanceService", "Screen off,readingModeSwitch ON done!");
            this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
            this.mModeStage = 6;
            this.mFactor = 1.0F;
            closeMatrix();
            setSRGBSensorEnabled(false);
            this.mGeneration += 1;
          }
        }
        else {
          return;
        }
        if (this.mNight2ReadingModeStage != 0) {
          break label417;
        }
        setSRGBSensorEnabled(true);
        Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor should be 1 :" + this.mFactor);
        if (this.mFactor > 0.9999D)
        {
          this.mFactor = 1.0F;
          this.mCmgr.setActiveMode(4);
          this.mCurrentReadingColorbalance = this.mCurrentNightColorbalance;
          Slog.i("ColorBalanceService", "changeNight2ReadingMode mCurrentNightColorbalance:" + this.mCurrentNightColorbalance);
          if ((this.mStableColor <= 2000) || (this.mStableColor >= 8000)) {
            break label385;
          }
          paramInt2 = getBalanceByTemprature(this.mStableColor);
          Slog.i("ColorBalanceService", "mStableColor:" + this.mStableColor + " mCurrentSrgbSensorAverageColor:" + this.mCurrentSrgbSensorAverageColor);
          this.mNight2ReadingModeStage = 10000;
          Slog.i("ColorBalanceService", "changeNight2ReadingMode 0 --> 10000,init");
          sendMsgWithValueDelayed(13, paramInt1, paramInt2, 15);
        }
      }
      if (this.mFactor < 0.001D)
      {
        this.mCmgr.setActiveMode(3);
        this.mFactor = 0.0F;
        Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor:" + this.mFactor);
        continue;
        label385:
        if ((this.mCurrentSrgbSensorAverageColor > 2000) && (this.mCurrentSrgbSensorAverageColor < 8000))
        {
          paramInt2 = getBalanceByTemprature(this.mCurrentSrgbSensorAverageColor);
          continue;
          label417:
          if (this.mNight2ReadingModeStage == 10000)
          {
            int i = 0;
            if (this.mCurrentReadingColorbalance != paramInt2)
            {
              int k = this.mCurrentReadingColorbalance;
              i = j;
              if (this.mCurrentReadingColorbalance > paramInt2) {
                i = -1;
              }
              this.mCurrentReadingColorbalance = (i + k);
              this.mCurrentReadingColorbalance = this.mCurrentReadingColorbalance;
              opSetColorBalance(this.mCurrentReadingColorbalance, 0);
              i = 1;
            }
            j = i;
            if (this.mFactor > 0.0115F)
            {
              this.mFactor -= 0.0125F;
              if ((this.mFactor <= 0.7249F) || (this.mFactor >= 0.7251F)) {
                break label609;
              }
              this.mCmgr.setActiveMode(5);
              this.mCmgr.setActiveMode(6);
              Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor:" + this.mFactor);
            }
            for (;;)
            {
              setColorMatrixNight2Reading(this.mFactor);
              j = i + 2;
              if (j <= 0) {
                break label805;
              }
              sendMsgWithValueDelayed(13, paramInt1, paramInt2, 15);
              break;
              label609:
              if ((this.mFactor > 0.49F) && (this.mFactor < 0.51F))
              {
                this.mCmgr.setActiveMode(7);
                Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor:" + this.mFactor);
              }
              else if ((this.mFactor > 0.249F) && (this.mFactor < 0.251F))
              {
                this.mCmgr.setActiveMode(8);
                this.mCmgr.setActiveMode(9);
                Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor:" + this.mFactor);
              }
              else if (this.mFactor < 0.001D)
              {
                this.mCmgr.setActiveMode(3);
                this.mFactor = 0.0F;
                Slog.i("ColorBalanceService", "changeNight2ReadingMode mFactor:" + this.mFactor);
              }
            }
            label805:
            this.mNight2ReadingModeStage = 15000;
            Slog.i("ColorBalanceService", "changeNight2ReadingMode mCurrentReadingColorbalance:" + this.mCurrentReadingColorbalance + " target:" + paramInt2 + "  mFactor:" + this.mFactor);
            Slog.i("ColorBalanceService", "changeNight2ReadingMode 10000 --> 15000,be B & W,and set colorbalance");
            sendMsgWithValueDelayed(13, paramInt1, paramInt2, 0);
          }
          else if (this.mNight2ReadingModeStage == 15000)
          {
            if (this.mFactor > 0.9999D)
            {
              this.mFactor = 1.0F;
              closeMatrix();
              this.mNight2ReadingModeStage = 20000;
              Slog.i("ColorBalanceService", "changeNight2ReadingMode 15000 --> 20000,turn off Matrix(shading)");
              sendMsgWithValueDelayed(13, paramInt1, 0, 0);
            }
            else
            {
              this.mFactor += 0.025F;
              setColorMartix(this.mFactor);
              setDTMColorMatrix();
              sendMsgWithValueDelayed(13, paramInt1, 0, 15);
            }
          }
          else if (this.mNight2ReadingModeStage == 20000)
          {
            this.mModeStage = 6;
            saveColorModeInternal();
            Slog.i("ColorBalanceService", "changeNight2ReadingMode done!");
          }
        }
        else
        {
          paramInt2 = -20;
        }
      }
    }
  }
  
  private void changeReading2NightMode(int paramInt1, int paramInt2)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.mGeneration == paramInt1)
        {
          if (!this.mIsScreenOn.booleanValue())
          {
            this.mFactor = 1.0F;
            closeMatrix();
            Slog.i("ColorBalanceService", "Screen off,changeReading2NightMode done!");
            this.mCmgr.setActiveMode(0);
            setNightModeProp(Boolean.valueOf(true));
            opSetColorBalance(132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90, 0);
            this.mModeStage = 2;
            saveColorModeInternal();
            setSRGBSensorEnabled(false);
            this.mGeneration += 1;
          }
        }
        else {
          return;
        }
        if (this.mReading2NightModeStage != 0) {
          break label233;
        }
        if (this.mFactor < 1.0E-4D)
        {
          this.mFactor = 0.0F;
          setSRGBSensorEnabled(true);
          setNightModeProp(Boolean.valueOf(true));
          this.mReading2NightModeStage = 5000;
          sendMsgWithValueDelayed(14, paramInt1, 0, 0);
          Slog.i("ColorBalanceService", "changeReading2NightMode: 0 --> 5000,be B & W");
        }
      }
      this.mFactor -= 0.025F;
      setColorMartix(this.mFactor);
      setDTMColorMatrix();
      sendMsgWithValueDelayed(14, paramInt1, 0, 15);
      continue;
      label233:
      if (this.mReading2NightModeStage != 5000) {
        break;
      }
      paramInt2 = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2);
      this.mCurrentNightColorbalance = this.mCurrentReadingColorbalance;
      Slog.i("ColorBalanceService", "changeReading2NightMode:" + this.mCurrentNightColorbalance);
      setColorMatrixNight2Reading(this.mFactor);
      this.mCmgr.setActiveMode(1);
      if (this.mFactor < 1.0E-4D)
      {
        this.mCmgr.setActiveMode(9);
        this.mCmgr.setActiveMode(8);
      }
      this.mReading2NightModeStage = 10000;
      sendMsgWithValueDelayed(14, paramInt1, 132 - paramInt2 - 90, 0);
      Slog.i("ColorBalanceService", "changeReading2NightMode:stage 5000 --> 10000,init target colorbalance");
    }
    int j;
    int i;
    if (this.mReading2NightModeStage == 10000)
    {
      j = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
      if (this.mFactor <= 0.999F) {
        break label872;
      }
      if (this.mCurrentNightColorbalance == j) {
        break label867;
      }
      i = 1;
      int k = this.mCurrentNightColorbalance;
      if (this.mCurrentNightColorbalance <= j) {
        break label862;
      }
      paramInt2 = -1;
      label457:
      this.mCurrentNightColorbalance = (paramInt2 + k);
      this.mCurrentColorBalance = this.mCurrentNightColorbalance;
      opSetColorBalance(this.mCurrentNightColorbalance, 0);
      paramInt2 = i;
    }
    for (;;)
    {
      i = paramInt2;
      if (this.mFactor < 0.999F)
      {
        if ((this.mFactor <= 0.249F) || (this.mFactor >= 0.251F)) {
          break label600;
        }
        this.mCmgr.setActiveMode(7);
        Slog.i("ColorBalanceService", "changeReading2NightMode mFactor:" + this.mFactor);
      }
      for (;;)
      {
        i = paramInt2 + 2;
        this.mFactor += 0.0125F;
        setColorMatrixNight2Reading(this.mFactor);
        if (i < 2) {
          break label745;
        }
        sendMsgWithValueDelayed(14, paramInt1, j, 15);
        break;
        label600:
        if ((this.mFactor > 0.49F) && (this.mFactor < 0.51F))
        {
          this.mCmgr.setActiveMode(6);
          this.mCmgr.setActiveMode(5);
          Slog.i("ColorBalanceService", "changeReading2NightMod mFactor:" + this.mFactor);
        }
        else if ((this.mFactor > 0.749F) && (this.mFactor < 0.751F))
        {
          this.mCmgr.setActiveMode(4);
          this.mCmgr.setActiveMode(2);
          Slog.i("ColorBalanceService", "changeReading2NightMode mFactor:" + this.mFactor);
        }
      }
      label745:
      if (i == 1)
      {
        sendMsgWithValueDelayed(14, paramInt1, j, 15);
        break;
      }
      this.mReading2NightModeStage = 20000;
      Slog.i("ColorBalanceService", "changeReading2NightMode mCurrentNightColorbalance:" + this.mCurrentNightColorbalance);
      Slog.i("ColorBalanceService", "changeReading2NightMode:stage 10000 --> 20000,to be colors and set colorbalance");
      sendMsgWithValueDelayed(14, paramInt1, j, 0);
      break;
      if (this.mReading2NightModeStage != 20000) {
        break;
      }
      this.mFactor = 1.0F;
      closeMatrix();
      this.mModeStage = 2;
      saveColorModeInternal();
      Slog.i("ColorBalanceService", "changeReading2NightMode done(20000)!");
      break;
      label862:
      paramInt2 = 1;
      break label457;
      label867:
      paramInt2 = 0;
      continue;
      label872:
      paramInt2 = 1;
    }
  }
  
  private boolean checkColorManageEnable()
  {
    boolean bool = false;
    int i = 5000;
    for (;;)
    {
      if ((i > 0) && (this.mCmgr == null))
      {
        if (!this.mIsColorManagerConnected.booleanValue()) {
          this.mIsColorManagerConnected = Boolean.valueOf(colorManagerInit());
        }
        if (this.mCmgr == null) {
          if (i % 50 == 0) {
            Slog.i("ColorBalanceService", "init error,wait 10ms and reinit,retryCount:" + i);
          }
        }
        try
        {
          Thread.sleep(10L);
          i -= 1;
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            localInterruptedException.printStackTrace();
          }
        }
      }
    }
    if (this.mCmgr != null) {
      bool = true;
    }
    return bool;
  }
  
  private void closeMatrix()
  {
    int i = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_daltonizer_enabled", 0, -2);
    int j = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, -2);
    if ((i == 1) || (j == 1)) {
      return;
    }
    if (this.dtm != null)
    {
      this.dtm.setColorMatrix(0, null);
      Slog.i("ColorBalanceService", "close matrix!");
    }
  }
  
  private boolean colorManagerInit()
  {
    if (this.mCmgr == null)
    {
      ColorManager.ColorManagerListener local5 = new ColorManager.ColorManagerListener()
      {
        public void onConnected()
        {
          ColorBalanceService.-wrap17(ColorBalanceService.this);
        }
      };
      if (ColorManager.connect(this.mContext, local5) != 0)
      {
        Slog.e("ColorBalanceService", "Connection failed");
        return false;
      }
      Slog.i("ColorBalanceService", "ColorManager Connected!");
    }
    return true;
  }
  
  private void createModeList(ModeInfo[] paramArrayOfModeInfo)
  {
    this.mModeList = new ArrayList();
    int i = 0;
    int j = paramArrayOfModeInfo.length;
    while (i < j)
    {
      ModeInfo localModeInfo = paramArrayOfModeInfo[i];
      this.mModeList.add(new ModeInfoWrapper(localModeInfo));
      i += 1;
    }
  }
  
  private int getBalanceByTemprature(int paramInt)
  {
    if (this.mIsReadingModeActivated.booleanValue())
    {
      Slog.d("ColorBalanceService", " when open the reading mode the enviroment color temperature         == " + paramInt);
      if (paramInt < 0)
      {
        Slog.d("ColorBalanceService", " The RGB sensor output negative data this is error !  ");
        return 0;
      }
      paramInt /= 100;
      if (paramInt < 26)
      {
        paramInt = 0;
        Slog.d("ColorBalanceService", " when in reading mode the enviroment_step = " + paramInt);
        i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
        if ((i != 1) && (i != 3)) {
          break label229;
        }
      }
      label229:
      for (paramInt = config_colorbalance_reading_mode[paramInt];; paramInt = config_colorbalance_reading_mode_SRGB_and_P3[paramInt])
      {
        paramInt -= 90;
        Slog.i("ColorBalanceService", "getBalanceByTemprature:" + paramInt);
        return paramInt;
        if ((paramInt >= 26) && (paramInt < 36))
        {
          paramInt = 1;
          break;
        }
        if ((paramInt >= 36) && (paramInt < 47))
        {
          paramInt = 2;
          break;
        }
        if ((paramInt >= 47) && (paramInt < 57))
        {
          paramInt = 3;
          break;
        }
        if ((paramInt >= 57) && (paramInt < 66))
        {
          paramInt = 4;
          break;
        }
        paramInt = 5;
        break;
      }
    }
    int k = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2);
    int i = 0;
    int j;
    for (;;)
    {
      j = i;
      if (i < 54) {
        if (k > this.config_autoseekbar[i]) {
          break label322;
        }
      }
      for (j = i;; j = 53)
      {
        if ((j >= 0) && (j <= 53)) {
          break label347;
        }
        Slog.d("ColorBalanceService", "Out of range elemIndex: " + j);
        return 0;
        label322:
        if (k < this.config_autoseekbar[53]) {
          break;
        }
      }
      i += 1;
    }
    label347:
    Slog.d("ColorBalanceService", " when open the night mode the enviroment color temperature         == " + paramInt);
    if (paramInt < 0)
    {
      Slog.d("ColorBalanceService", " The RGB sensor output negative data this is error !  ");
      return 0;
    }
    paramInt /= 100;
    if (paramInt < 26) {
      paramInt = 0;
    }
    for (;;)
    {
      Slog.d("ColorBalanceService", " when in night mode the enviroment_step = " + paramInt);
      paramInt = this.config_colorbalance[j][paramInt] - 90;
      break;
      if ((paramInt >= 26) && (paramInt < 36)) {
        paramInt = 1;
      } else if ((paramInt >= 36) && (paramInt < 47)) {
        paramInt = 2;
      } else if ((paramInt >= 47) && (paramInt < 57)) {
        paramInt = 3;
      } else if ((paramInt >= 57) && (paramInt < 66)) {
        paramInt = 4;
      } else {
        paramInt = 5;
      }
    }
  }
  
  private void handleSRGBSensorEvent(int paramInt)
  {
    if ((paramInt > 2000) && (paramInt < 8000))
    {
      if (this.mAverageColor == 0)
      {
        this.mAverageColor = paramInt;
        this.mStableCount = 0;
        return;
      }
      if (this.mStableCount >= 8) {
        break label243;
      }
      if (Math.abs(this.mAverageColor - paramInt) >= 200) {
        break label232;
      }
      this.mStableCount += 1;
      this.mAverageColor = ((this.mAverageColor + paramInt) / 2);
      if (this.mStableCount == 8)
      {
        if (this.mStableColor != 0) {
          break label132;
        }
        this.mStableColor = this.mAverageColor;
        Slog.i("ColorBalanceService", "Stable color is " + this.mStableColor);
      }
    }
    return;
    label132:
    if (Math.abs(this.mStableColor - this.mAverageColor) < 800)
    {
      Slog.i("ColorBalanceService", "ignored,Stable color is " + this.mStableColor);
      return;
    }
    this.mStableColor = this.mAverageColor;
    processEnvironmentChange();
    Slog.i("ColorBalanceService", "adjusted,Stable color is " + this.mStableColor);
    this.mStableCount = 0;
    this.mAverageColor = 0;
    return;
    label232:
    this.mStableCount = 0;
    this.mAverageColor = 0;
    return;
    label243:
    if (Math.abs(this.mStableColor - paramInt) < 100) {
      return;
    }
    this.mStableCount = 0;
    this.mAverageColor = 0;
  }
  
  private boolean isFactoryMode(ModeInfoWrapper paramModeInfoWrapper)
  {
    return paramModeInfoWrapper.mode.getModeType() == ColorManager.MODE_TYPE.MODE_SYSTEM;
  }
  
  private boolean isScreenOn()
  {
    if (this.mPowerManager != null) {
      return this.mPowerManager.isScreenOn();
    }
    Slog.w("ColorBalanceService", "mPowerManager is null!");
    return false;
  }
  
  private static boolean isUserSetupCompleted(ContentResolver paramContentResolver, int paramInt)
  {
    return Settings.Secure.getIntForUser(paramContentResolver, "user_setup_complete", 0, paramInt) == 1;
  }
  
  private ModeInfoWrapper modeExists(String paramString)
  {
    Object localObject = this.mCmgr.getModes(ColorManager.MODE_TYPE.MODE_USER);
    if (localObject != null) {
      createModeList((ModeInfo[])localObject);
    }
    if (this.mModeList == null) {
      return null;
    }
    localObject = this.mModeList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ModeInfoWrapper localModeInfoWrapper = (ModeInfoWrapper)((Iterator)localObject).next();
      if (localModeInfoWrapper.modename.equals(paramString)) {
        return localModeInfoWrapper;
      }
    }
    return null;
  }
  
  private void nightModeSwitch(boolean paramBoolean, int paramInt)
  {
    int i = 1;
    int j = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (!this.mIsScreenOn.booleanValue())
        {
          if (this.mGeneration == paramInt)
          {
            this.mGeneration += 1;
            setSRGBSensorEnabled(false);
            this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
            if (this.mIsNightModeActivated.booleanValue())
            {
              this.mFactor = 1.0F;
              closeMatrix();
              setNightModeProp(Boolean.valueOf(true));
              this.mCmgr.setActiveMode(0);
              opSetColorBalance(j, 0);
              this.mNightModeOpingStage = 4;
              this.mModeStage = 2;
              Slog.i("ColorBalanceService", "Screen off,nightModeSwitch ON done!");
              saveColorModeInternal();
            }
          }
          else
          {
            return;
          }
          revertStatus();
          this.mNightModeClosingStage = 3;
          this.mModeStage = 4;
          Slog.i("ColorBalanceService", "Screen off,nightModeSwitch OFF done!");
        }
      }
      if (this.mGeneration == paramInt) {
        if (this.mIsNightModeActivated.booleanValue())
        {
          if (this.mModeStage != 2) {
            if (!this.mIsReadingOrNightModeOpendLastSesson.booleanValue())
            {
              if (this.mNightModeOpingStage == 0)
              {
                if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2) != 3)
                {
                  this.mCmgr.setActiveMode(0);
                  setNightModeProp(Boolean.valueOf(true));
                  this.mCurrentNightColorbalance = 43;
                  this.mNightModeOpingStage = 3;
                  Slog.d("ColorBalanceService", "nightModeSwitch ON: 0 --> 3,default!");
                }
                for (;;)
                {
                  sendMsgWithValue(10, paramInt);
                  break;
                  this.mCurrentNightColorbalance = (100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 57, -2));
                  this.mNightModeOpingStage = 2;
                  Slog.d("ColorBalanceService", "nightModeSwitch ON: 0 --> 2,status 3,setting customer to default");
                }
              }
              if (this.mNightModeOpingStage == 2)
              {
                opSetColorBalance(this.mCurrentNightColorbalance, 0);
                if (this.mCurrentNightColorbalance == 43)
                {
                  this.mNightModeOpingStage = 3;
                  Slog.d("ColorBalanceService", "nightModeSwitch ON: 2 --> 3,default!");
                  this.mCmgr.setActiveMode(0);
                  setNightModeProp(Boolean.valueOf(true));
                }
                for (this.mCurrentNightColorbalance = 43;; this.mCurrentNightColorbalance = (i + j))
                {
                  sendMsgWithValueDelayed(10, paramInt, 0, 15);
                  break;
                  j = this.mCurrentNightColorbalance;
                  if (this.mCurrentNightColorbalance > 43) {
                    i = -1;
                  }
                }
              }
              if (this.mNightModeOpingStage == 3)
              {
                if (this.mCurrentNightColorbalance == j)
                {
                  this.mNightModeOpingStage = 4;
                  Slog.d("ColorBalanceService", "nightModeSwitch ON: 3 --> 4, SET Balance:" + this.mCurrentNightColorbalance);
                }
                for (;;)
                {
                  opSetColorBalance(this.mCurrentNightColorbalance, 0);
                  sendMsgWithValueDelayed(10, paramInt, 0, 15);
                  break;
                  if (this.mCurrentNightColorbalance <= j) {
                    break label1015;
                  }
                  i = -1;
                  label525:
                  this.mCurrentNightColorbalance += i;
                }
              }
              if (this.mNightModeOpingStage == 4)
              {
                this.mCurrentColorBalance = j;
                saveColorModeInternal();
                this.mModeStage = 2;
                setSRGBSensorEnabled(true);
                Slog.i("ColorBalanceService", "nightModeSwitch ON done!");
              }
            }
            else
            {
              this.mFactor = 1.0F;
              closeMatrix();
              this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
              Slog.i("ColorBalanceService", "#2 night mode had been opened!");
              this.mCmgr.setActiveMode(0);
              setProp(Boolean.valueOf(true));
              this.mCurrentNightColorbalance = j;
              this.mCurrentColorBalance = j;
              opSetColorBalance(j, 0);
              this.mModeStage = 2;
              saveColorModeInternal();
              setSRGBSensorEnabled(true);
            }
          }
        }
        else if ((this.mModeStage != 4) && (this.mModeStage != 0)) {
          if (this.mNightModeClosingStage == 0)
          {
            if (43 == this.mCurrentNightColorbalance)
            {
              this.mNightModeClosingStage = 1;
              sendMsgWithValueDelayed(10, paramInt, 0, 0);
              Slog.d("ColorBalanceService", "nightModeSwitch OFF 0 --> 1,colorBalance default!");
            }
            else
            {
              if (this.mCurrentNightColorbalance <= 43) {
                break label1020;
              }
              i = -1;
              label728:
              this.mCurrentNightColorbalance += i;
              opSetColorBalance(this.mCurrentNightColorbalance, 0);
              sendMsgWithValueDelayed(10, paramInt, 0, 15);
            }
          }
          else
          {
            if (this.mNightModeClosingStage == 1)
            {
              if (setNightModeProp(Boolean.valueOf(false)) != 3)
              {
                revertStatus();
                this.mNightModeClosingStage = 3;
                Slog.d("ColorBalanceService", "nightModeSwitch OFF:1 --> 3, revertStatus");
              }
              for (;;)
              {
                sendMsgWithValueDelayed(10, paramInt, 0, 0);
                break;
                this.mFactor = 1.0F;
                closeMatrix();
                this.mCmgr.setActiveMode(0);
                this.mCurrentNightColorbalance = 43;
                this.mNightModeClosingStage = 2;
                Slog.d("ColorBalanceService", "nightModeSwitch OFF:1 --> 2,be status 3");
              }
            }
            if (this.mNightModeClosingStage != 2) {
              break label956;
            }
            i = 100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 57, -2);
            if (this.mCurrentNightColorbalance != i) {
              break;
            }
            this.mNightModeClosingStage = 3;
            sendMsgWithValueDelayed(10, paramInt, 0, 0);
            Slog.d("ColorBalanceService", "nightModeSwitch OFF:2 --> 3,status 3 done!");
          }
        }
      }
    }
    if (this.mCurrentNightColorbalance > i) {}
    for (i = -1;; i = 1)
    {
      this.mCurrentNightColorbalance += i;
      opSetColorBalance(this.mCurrentNightColorbalance, 0);
      sendMsgWithValueDelayed(10, paramInt, 0, 15);
      break;
      label956:
      if (this.mNightModeClosingStage != 3) {
        break;
      }
      setSRGBSensorEnabled(false);
      Slog.i("ColorBalanceService", "nightModeSwitch OFF done!");
      this.mModeStage = 4;
      saveColorModeInternal();
      this.mCurrentColorBalance = (100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 57, -2));
      break;
      label1015:
      i = 1;
      break label525;
      label1020:
      i = 1;
      break label728;
    }
  }
  
  private void onUserChanged(int paramInt)
  {
    final ContentResolver localContentResolver = getContext().getContentResolver();
    if (this.mCurrentUser != 55536)
    {
      if (this.mUserSetupObserver == null) {
        break label101;
      }
      localContentResolver.unregisterContentObserver(this.mUserSetupObserver);
      this.mUserSetupObserver = null;
    }
    label101:
    do
    {
      for (;;)
      {
        this.mCurrentUser = paramInt;
        if (this.mCurrentUser != 55536)
        {
          if (isUserSetupCompleted(localContentResolver, this.mCurrentUser)) {
            break;
          }
          this.mUserSetupObserver = new ContentObserver(this.mHandler)
          {
            public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
            {
              if (ColorBalanceService.-wrap1(localContentResolver, ColorBalanceService.-get5(ColorBalanceService.this)))
              {
                localContentResolver.unregisterContentObserver(this);
                ColorBalanceService.-set16(ColorBalanceService.this, null);
                if (ColorBalanceService.-get0(ColorBalanceService.this)) {
                  ColorBalanceService.-wrap16(ColorBalanceService.this);
                }
              }
            }
          };
          localContentResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this.mUserSetupObserver, this.mCurrentUser);
        }
        return;
        if (this.mBootCompleted) {
          tearDown();
        }
      }
    } while (!this.mBootCompleted);
    setUp();
  }
  
  private void oneplusSetColorBalance(int paramInt1, int paramInt2)
  {
    double d3 = paramInt1;
    double d2 = paramInt2;
    double d1 = 1.0D;
    if ((paramInt1 > 100) && (paramInt1 < -100))
    {
      Slog.e("ColorBalanceService", "oneplusSetColorBalance ERROR:" + paramInt1);
      return;
    }
    if ((d3 < 43.0D) || (d3 == 43.0D)) {
      if (paramInt2 != 0) {}
    }
    for (;;)
    {
      try
      {
        if (this.mSDM != null) {
          this.mSDM.SetUsrColorBalanceConfig(1.0D, 1.028D * d3 * d3 * d3 * 1.0000000116860974E-7D + -1.679D * d3 * d3 * 9.999999747378752E-6D + 0.002807D * d3 + 0.8973D, -1.394D * d3 * d3 * d3 * 1.0000000116860974E-7D + -2.048D * d3 * d3 * 9.999999747378752E-6D + 0.006439D * d3 + 0.7532D);
        }
        return;
      }
      catch (NullPointerException localNullPointerException1)
      {
        Slog.e("ColorBalanceService", "mSDM.SetUsrColorBalanceConfig error!");
        return;
      }
      paramInt2 = 0;
      while (paramInt2 < 5)
      {
        d1 -= 0.2D;
        d3 = paramInt1 - d2 * d1;
        try
        {
          if (this.mSDM != null) {
            this.mSDM.SetUsrColorBalanceConfig(1.0D, 1.028D * d3 * d3 * d3 * 1.0000000116860974E-7D + -1.679D * d3 * d3 * 9.999999747378752E-6D + 0.002807D * d3 + 0.8973D, -1.394D * d3 * d3 * d3 * 1.0000000116860974E-7D + -2.048D * d3 * d3 * 9.999999747378752E-6D + 0.006439D * d3 + 0.7532D);
          }
        }
        catch (NullPointerException localNullPointerException2)
        {
          try
          {
            for (;;)
            {
              Thread.sleep(15L);
              paramInt2 += 1;
              break;
              localNullPointerException2 = localNullPointerException2;
              Slog.e("ColorBalanceService", "mSDM.SetUsrColorBalanceConfig error!");
            }
          }
          catch (InterruptedException localInterruptedException1)
          {
            for (;;)
            {
              localInterruptedException1.printStackTrace();
            }
          }
        }
      }
      if (paramInt2 == 0) {
        try
        {
          if (this.mSDM != null)
          {
            this.mSDM.SetUsrColorBalanceConfig(-3.176D * d3 * d3 * d3 * 1.0000000116860974E-7D + 1.011D * d3 * d3 * 9.999999747378752E-5D + -0.01282D * d3 + 1.387D, 1.333D * d3 * d3 * d3 * 9.999999974752427E-7D + -2.01D * d3 * d3 * 9.999999747378752E-5D + 0.006843D * d3 + 0.9677D, 1.0D);
            return;
          }
        }
        catch (NullPointerException localNullPointerException3)
        {
          Slog.e("ColorBalanceService", "mSDM.SetUsrColorBalanceConfig error!");
          return;
        }
      }
    }
    paramInt2 = 0;
    while (paramInt2 < 5)
    {
      d1 -= 0.2D;
      d3 = paramInt1 - d2 * d1;
      try
      {
        if (this.mSDM != null) {
          this.mSDM.SetUsrColorBalanceConfig(-3.176D * d3 * d3 * d3 * 1.0000000116860974E-7D + 1.011D * d3 * d3 * 9.999999747378752E-5D + -0.01282D * d3 + 1.387D, 1.333D * d3 * d3 * d3 * 9.999999974752427E-7D + -2.01D * d3 * d3 * 9.999999747378752E-5D + 0.006843D * d3 + 0.9677D, 1.0D);
        }
      }
      catch (NullPointerException localNullPointerException4)
      {
        try
        {
          for (;;)
          {
            Thread.sleep(15L);
            paramInt2 += 1;
            break;
            localNullPointerException4 = localNullPointerException4;
            Slog.e("ColorBalanceService", "mSDM.SetUsrColorBalanceConfig error!");
          }
        }
        catch (InterruptedException localInterruptedException2)
        {
          for (;;)
          {
            localInterruptedException2.printStackTrace();
          }
        }
      }
    }
  }
  
  private void opSetColorBalance(int paramInt1, int paramInt2)
  {
    oneplusSetColorBalance(paramInt1, paramInt2);
  }
  
  private void processEnvironmentChange()
  {
    Message localMessage;
    long l;
    Bundle localBundle;
    if ((this.mIsNightModeActivated != null) && (this.mIsNightModeActivated.booleanValue()))
    {
      this.mGeneration += 1;
      localMessage = Message.obtain();
      localMessage.what = 8;
      localMessage.arg1 = getBalanceByTemprature(this.mStableColor);
      localMessage.arg2 = this.mGeneration;
      Slog.i("ColorBalanceService", "mCurrentNightColorbalance:" + this.mCurrentNightColorbalance + " target:" + localMessage.arg1);
      if (this.mCurrentNightColorbalance != localMessage.arg1)
      {
        l = 0 / Math.abs(this.mCurrentNightColorbalance - localMessage.arg1);
        localBundle = new Bundle();
        localBundle.putLong("delay", l);
        localMessage.setData(localBundle);
        if (this.mCMHHandler != null) {
          this.mCMHHandler.sendMessageDelayed(localMessage, l);
        }
      }
    }
    do
    {
      do
      {
        Slog.i("ColorBalanceService", "mCurrentNightColorbalance:" + this.mCurrentNightColorbalance + " target:" + localMessage.arg1);
        do
        {
          return;
        } while (!this.mIsReadingModeActivated.booleanValue());
        this.mGeneration += 1;
        localMessage = Message.obtain();
        localMessage.what = 9;
        localMessage.arg1 = getBalanceByTemprature(this.mStableColor);
        localMessage.arg2 = this.mGeneration;
        Slog.i("ColorBalanceService", "mCurrentReadingColorbalance:" + this.mCurrentReadingColorbalance + " target:" + localMessage.arg1);
      } while (this.mCurrentReadingColorbalance == localMessage.arg1);
      l = 0 / Math.abs(this.mCurrentReadingColorbalance - localMessage.arg1);
      localBundle = new Bundle();
      localBundle.putLong("delay", l);
      localMessage.setData(localBundle);
    } while (this.mCMHHandler == null);
    this.mCMHHandler.sendMessageDelayed(localMessage, l);
    Slog.i("ColorBalanceService", "mCurrentReadingColorbalance:" + this.mCurrentReadingColorbalance + " target:" + localMessage.arg1 + " delay:" + l);
  }
  
  private void processEnvironmentColorChangeAtNightMode(Message paramMessage)
  {
    long l = paramMessage.getData().getLong("delay");
    int i = paramMessage.arg2;
    int k = paramMessage.arg1;
    int j = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
    if ((i == this.mGeneration) && (j == this.mPretNightColorbalance))
    {
      if (this.mCurrentNightColorbalance <= k) {
        break label198;
      }
      i = -1;
      k = this.mCurrentNightColorbalance + i;
      this.mCurrentNightColorbalance = k;
      this.mCurrentNightColorbalance = k;
      opSetColorBalance(this.mCurrentNightColorbalance, i);
      if (this.mCurrentNightColorbalance == paramMessage.arg1) {
        break label203;
      }
      if (this.mCMHHandler != null)
      {
        Message localMessage = Message.obtain();
        localMessage.what = paramMessage.what;
        Bundle localBundle = new Bundle();
        localMessage.arg1 = paramMessage.arg1;
        localMessage.arg2 = paramMessage.arg2;
        localBundle.putLong("delay", l);
        localMessage.setData(localBundle);
        this.mCMHHandler.sendMessageDelayed(localMessage, l);
      }
    }
    for (;;)
    {
      this.mPretNightColorbalance = j;
      return;
      label198:
      i = 1;
      break;
      label203:
      Slog.i("ColorBalanceService", "shading done,mCurrentNightColorbalance:" + this.mCurrentNightColorbalance + " target:" + paramMessage.arg1);
    }
  }
  
  private void processEnvironmentColorChangeAtReadingMode(Message paramMessage)
  {
    long l = paramMessage.getData().getLong("delay");
    int i = paramMessage.arg2;
    int j = paramMessage.arg1;
    if (i == this.mGeneration) {
      if (this.mCurrentReadingColorbalance <= j) {
        break label145;
      }
    }
    label145:
    for (i = -1;; i = 1)
    {
      this.mCurrentReadingColorbalance += i;
      opSetColorBalance(this.mCurrentReadingColorbalance, i);
      if (this.mCurrentReadingColorbalance == j) {
        break;
      }
      if (this.mCMHHandler != null)
      {
        Message localMessage = Message.obtain();
        localMessage.what = paramMessage.what;
        Bundle localBundle = new Bundle();
        localMessage.arg1 = paramMessage.arg1;
        localMessage.arg2 = paramMessage.arg2;
        localBundle.putLong("delay", l);
        localMessage.setData(localBundle);
        this.mCMHHandler.sendMessageDelayed(localMessage, l);
      }
      return;
    }
    Slog.i("ColorBalanceService", "shading done,mCurrentReadingColorbalance:" + this.mCurrentReadingColorbalance + " target:" + paramMessage.arg1);
  }
  
  private void processScreenOn(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      Slog.i("ColorBalanceService", "processScreenOn:" + paramBoolean);
      if (paramBoolean)
      {
        if (((this.mIsNightModeActivated != null) && (this.mIsNightModeActivated.booleanValue())) || (this.mIsReadingModeActivated.booleanValue())) {
          setSRGBSensorEnabled(true);
        }
        return;
      }
      setSRGBSensorEnabled(false);
    }
  }
  
  private void processSetColorBalance(int paramInt)
  {
    this.mStartSetCount -= 1;
    Slog.i("ColorBalanceService", "MSG_SET_COLORBALANCE:" + this.mCurrentColorBalance + " --> " + paramInt + " mStartSetCount:" + this.mStartSetCount);
    if ((this.mStartSetCount == 0) && (this.mModeStage != 1))
    {
      paramInt = shading(this.mCurrentColorBalance, paramInt);
      this.mCurrentColorBalance = paramInt;
      this.mCurrentNightColorbalance = paramInt;
    }
  }
  
  private void readingModeSwitch(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int m = 1;
    int j = 1;
    int i = 1;
    int k = 1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (!this.mIsScreenOn.booleanValue())
        {
          if (this.mGeneration == paramInt1)
          {
            this.mGeneration += 1;
            setSRGBSensorEnabled(false);
            this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
            this.mFactor = 1.0F;
            closeMatrix();
            if (this.mIsReadingModeActivated.booleanValue())
            {
              setNightModeProp(Boolean.valueOf(true));
              this.mCmgr.setActiveMode(3);
              this.mStableColor = 6800;
              opSetColorBalance(-20, 0);
              Slog.i("ColorBalanceService", "Screen off,readingModeSwitch ON done!");
              this.mReadingModeOpingStage = 40000;
              this.mModeStage = 6;
            }
          }
          else
          {
            return;
          }
          revertStatus();
          this.mReadingModeClosingStage = 20000;
          this.mModeStage = 8;
          Slog.i("ColorBalanceService", "Screen off,readingModeSwitch OFF done!");
        }
      }
      if (this.mGeneration == paramInt1) {
        if (this.mIsReadingModeActivated.booleanValue())
        {
          if (this.mReadingModeClosingStage == 500)
          {
            this.mReadingModeClosingStage = 20000;
            this.mFactor = 1.0F;
            closeMatrix();
            this.mReadingModeOpingStage = 40000;
            this.mModeStage = 6;
            Slog.i("ColorBalanceService", "readingModeSwitch ON, mReadingModeClosingStage 500,to be on done!");
            return;
          }
          if (this.mModeStage != 6) {
            if (!this.mIsReadingOrNightModeOpendLastSesson.booleanValue())
            {
              if (this.mReadingModeOpingStage == 0)
              {
                setSRGBSensorEnabled(true);
                if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2) != 3)
                {
                  this.mReadingModeOpingStage = 15000;
                  sendMsgWithValueDelayed(11, paramInt1, 0, 0);
                  Slog.i("ColorBalanceService", "readingModeSwitch ON:stage 0 --> 15000,colorBalance default");
                }
                else
                {
                  this.mCurrentReadingColorbalance = (100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 57, -2));
                  opSetColorBalance(this.mCurrentReadingColorbalance, 0);
                  this.mReadingModeOpingStage = 10000;
                  sendMsgWithValueDelayed(11, paramInt1, 0, 5);
                  Slog.i("ColorBalanceService", "readingModeSwitch ON:stage 0 --> 10000,status 3,to be default");
                }
              }
              else if (this.mReadingModeOpingStage == 10000)
              {
                if (this.mCurrentReadingColorbalance == 43)
                {
                  this.mReadingModeOpingStage = 15000;
                  sendMsgWithValueDelayed(11, paramInt1, 0, 0);
                  Slog.i("ColorBalanceService", "readingModeSwitch ON:,10000 --> 15000, default now");
                }
                else
                {
                  i = this.mCurrentReadingColorbalance;
                  paramInt2 = k;
                  if (this.mCurrentReadingColorbalance > 43) {
                    paramInt2 = -1;
                  }
                  this.mCurrentReadingColorbalance = (paramInt2 + i);
                  opSetColorBalance(this.mCurrentReadingColorbalance, 0);
                  sendMsgWithValueDelayed(11, paramInt1, 0, 7);
                }
              }
              else
              {
                if (this.mReadingModeOpingStage == 15000)
                {
                  if (this.mFactor < 0.001F)
                  {
                    this.mReadingModeOpingStage = 20000;
                    setNightModeProp(Boolean.valueOf(true));
                    this.mCmgr.setActiveMode(3);
                    this.mCurrentReadingColorbalance = 43;
                    if ((this.mStableColor > 2000) && (this.mStableColor < 8000)) {}
                    for (paramInt2 = getBalanceByTemprature(this.mStableColor);; paramInt2 = getBalanceByTemprature(this.mCurrentSrgbSensorAverageColor))
                    {
                      label566:
                      Slog.i("ColorBalanceService", "mStableColor:" + this.mStableColor + " mCurrentSrgbSensorAverageColor:" + this.mCurrentSrgbSensorAverageColor);
                      sendMsgWithValueDelayed(11, paramInt1, paramInt2, 0);
                      Slog.i("ColorBalanceService", "readingModeSwitch ON:,15000 --> 20000, B & W now!");
                      break;
                      if ((this.mCurrentSrgbSensorAverageColor <= 2000) || (this.mCurrentSrgbSensorAverageColor >= 8000)) {
                        break label2301;
                      }
                    }
                  }
                  if (this.mFactor > 0.9999D)
                  {
                    Slog.i("ColorBalanceService", "readingModeSwitch ON:mFactor:" + this.mFactor);
                    this.mCmgr.setActiveMode(4);
                  }
                  for (;;)
                  {
                    this.mFactor -= 0.0125F;
                    if (this.mFactor < 0.03F) {
                      this.mFactor = 0.0F;
                    }
                    setColorMartix(this.mFactor);
                    setDTMColorMatrix();
                    sendMsgWithValueDelayed(11, paramInt1, 0, 16);
                    break;
                    if ((this.mFactor > 0.7249F) && (this.mFactor < 0.7251F))
                    {
                      Slog.i("ColorBalanceService", "readingModeSwitch ON:mFactor:" + this.mFactor);
                      this.mCmgr.setActiveMode(5);
                      this.mCmgr.setActiveMode(6);
                    }
                    else if ((this.mFactor > 0.49F) && (this.mFactor < 0.51F))
                    {
                      this.mCmgr.setActiveMode(7);
                      Slog.i("ColorBalanceService", "readingModeSwitch ON:mFactor:" + this.mFactor);
                    }
                    else if ((this.mFactor > 0.249F) && (this.mFactor < 0.251F))
                    {
                      this.mCmgr.setActiveMode(8);
                      this.mCmgr.setActiveMode(9);
                      Slog.i("ColorBalanceService", "readingModeSwitch ON:mFactor:" + this.mFactor);
                    }
                  }
                }
                if (this.mReadingModeOpingStage == 20000)
                {
                  if (this.mCurrentReadingColorbalance == paramInt2)
                  {
                    saveColorModeInternal();
                    this.mReadingModeOpingStage = 30000;
                    this.mFactor = 0.0F;
                    this.mCurrentReadingColorbalance = paramInt2;
                    sendMsgWithValueDelayed(11, paramInt1, paramInt2, 0);
                    Slog.i("ColorBalanceService", "readingModeSwitch ON:,2000 --> 30000  reading colorBalance:" + this.mCurrentReadingColorbalance);
                  }
                  else
                  {
                    j = this.mCurrentReadingColorbalance;
                    i = m;
                    if (this.mCurrentReadingColorbalance > paramInt2) {
                      i = -1;
                    }
                    this.mCurrentReadingColorbalance = (i + j);
                    opSetColorBalance(this.mCurrentReadingColorbalance, 0);
                    sendMsgWithValueDelayed(11, paramInt1, paramInt2, 7);
                  }
                }
                else if (this.mReadingModeOpingStage == 30000) {
                  if (this.mFactor > 0.9999D)
                  {
                    this.mFactor = 1.0F;
                    closeMatrix();
                    this.mReadingModeOpingStage = 40000;
                    this.mModeStage = 6;
                    Slog.i("ColorBalanceService", "readingModeSwitch ON Done(30000 -> 40000)");
                  }
                  else
                  {
                    this.mFactor += 0.025F;
                    setColorMartix(this.mFactor);
                    setDTMColorMatrix();
                    sendMsgWithValueDelayed(11, paramInt1, 0, 16);
                  }
                }
              }
            }
            else
            {
              this.mFactor = 1.0F;
              closeMatrix();
              setProp(Boolean.valueOf(true));
              this.mCmgr.setActiveMode(3);
              this.mStableColor = 6800;
              opSetColorBalance(-20, 0);
              Slog.i("ColorBalanceService", "readingModeSwitch ON done!");
              this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
              this.mModeStage = 6;
              this.mReadingModeOpingStage = 40000;
              setSRGBSensorEnabled(true);
            }
          }
        }
        else if ((this.mModeStage != 8) && (this.mModeStage != 0))
        {
          if ((this.mReadingModeClosingStage == 0) || (this.mReadingModeClosingStage == 500))
          {
            if (this.mFactor < 1.0E-4D)
            {
              this.mCmgr.setActiveMode(1);
              this.mFactor = 0.0F;
              this.mReadingModeClosingStage = 1000;
              paramInt2 = 43;
              i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
              if ((i == 2) || (i == 4)) {
                break label2307;
              }
              label1381:
              sendMsgWithValueDelayed(11, paramInt1, paramInt2, 0);
              Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 0 --> 1000,Matrix be B & W");
            }
            else
            {
              this.mReadingModeClosingStage = 500;
              this.mFactor -= 0.025F;
              setColorMartix(this.mFactor);
              setDTMColorMatrix();
              sendMsgWithValueDelayed(11, paramInt1, 0, 8);
              Slog.i("ColorBalanceService", "readingModeSwitch OFF: mReadingModeClosingStage:-->500,mFactor:" + this.mFactor);
            }
          }
          else if (this.mReadingModeClosingStage == 1000)
          {
            if (this.mCurrentReadingColorbalance == paramInt2)
            {
              setNightModeProp(Boolean.valueOf(false));
              this.mReadingModeClosingStage = 2500;
              Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 1000 --> 2500,colorBalance default:" + this.mCurrentReadingColorbalance);
              sendMsgWithValueDelayed(11, paramInt1, 0, 0);
            }
            else
            {
              k = this.mCurrentReadingColorbalance;
              if (this.mCurrentReadingColorbalance <= paramInt2) {
                break label2313;
              }
              i = -1;
              label1567:
              this.mCurrentReadingColorbalance = (i + k);
              if (this.mCurrentReadingColorbalance != paramInt2)
              {
                k = this.mCurrentReadingColorbalance;
                i = j;
                if (this.mCurrentReadingColorbalance > paramInt2) {
                  i = -1;
                }
                this.mCurrentReadingColorbalance = (i + k);
              }
              opSetColorBalance(this.mCurrentReadingColorbalance, 0);
              sendMsgWithValueDelayed(11, paramInt1, paramInt2, 5);
            }
          }
          else if (this.mReadingModeClosingStage == 2500)
          {
            if (this.mFactor > 0.999D)
            {
              this.mFactor = 1.0F;
              this.mReadingModeClosingStage = 5000;
              Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 2500 --> 5000,be Colors");
              sendMsgWithValueDelayed(11, paramInt1, 0, 0);
            }
            else
            {
              if ((this.mFactor > 0.049F) && (this.mFactor < 0.051F))
              {
                Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 2500,mFactor:" + this.mFactor);
                this.mCmgr.setActiveMode(9);
              }
              for (;;)
              {
                this.mFactor += 0.0125F;
                setColorMartix(this.mFactor);
                setDTMColorMatrix();
                sendMsgWithValueDelayed(11, paramInt1, 0, 15);
                break;
                if ((this.mFactor > 0.249F) && (this.mFactor < 0.251F))
                {
                  this.mCmgr.setActiveMode(8);
                  this.mCmgr.setActiveMode(7);
                  Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 2500,mFactor:" + this.mFactor);
                }
                else if ((this.mFactor > 0.49F) && (this.mFactor < 0.51F))
                {
                  this.mCmgr.setActiveMode(6);
                  this.mCmgr.setActiveMode(5);
                  Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 2500,mFactor:" + this.mFactor);
                }
                else if ((this.mFactor > 0.749F) && (this.mFactor < 0.751F))
                {
                  this.mCmgr.setActiveMode(4);
                  this.mCmgr.setActiveMode(2);
                  Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage 2500,mFactor:" + this.mFactor);
                }
              }
            }
          }
          else
          {
            if (this.mReadingModeClosingStage != 5000) {
              break;
            }
            paramInt2 = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
            if (paramInt2 != 1) {
              break label2319;
            }
            revertStatus();
            this.mReadingModeClosingStage = 20000;
            setSRGBSensorEnabled(false);
            Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage:5000 --> 20000,reverstatus");
            label2064:
            sendMsgWithValue(11, paramInt1);
          }
        }
      }
    }
    for (;;)
    {
      label2074:
      this.mReadingModeClosingStage = 20000;
      Slog.i("ColorBalanceService", "readingModeSwitch OFF: srgb stage:5000 --> 20000,status:" + paramInt2);
      break label2064;
      label2301:
      label2307:
      label2313:
      label2319:
      do
      {
        this.mFactor = 1.0F;
        closeMatrix();
        this.mCmgr.setActiveMode(0);
        this.mCurrentReadingColorbalance = 43;
        this.mReadingModeClosingStage = 10000;
        Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage:5000 --> 10000,status 3");
        break label2064;
        if (this.mReadingModeClosingStage == 10000)
        {
          k = 100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 43, -2);
          if (this.mCurrentReadingColorbalance == k)
          {
            this.mReadingModeClosingStage = 20000;
            Slog.i("ColorBalanceService", "readingModeSwitch OFF:stage:10000 -> 20000,status 3,be customer colorbalance");
          }
          for (;;)
          {
            sendMsgWithValueDelayed(11, paramInt1, 0, 10);
            break;
            j = this.mCurrentReadingColorbalance;
            paramInt2 = i;
            if (this.mCurrentReadingColorbalance > k) {
              paramInt2 = -1;
            }
            this.mCurrentReadingColorbalance = (paramInt2 + j);
            opSetColorBalance(this.mCurrentReadingColorbalance, 0);
          }
        }
        if (this.mReadingModeClosingStage != 20000) {
          break;
        }
        saveColorModeInternal();
        setSRGBSensorEnabled(false);
        this.mModeStage = 8;
        Slog.i("ColorBalanceService", "readingModeSwitch OFF done(-->2000)!");
        break;
        paramInt2 = -20;
        break label566;
        paramInt2 = 45;
        break label1381;
        i = 1;
        break label1567;
        if (paramInt2 == 2) {
          break label2074;
        }
      } while (paramInt2 != 4);
    }
  }
  
  private void revertStatus()
  {
    this.mFactor = 1.0F;
    closeMatrix();
    if (this.mCmgr == null)
    {
      Slog.w("ColorBalanceService", "revertStatus:mCmgr is null!");
      return;
    }
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
    Slog.d("ColorBalanceService", "revertStatus:" + i);
    switch (i)
    {
    }
    for (;;)
    {
      saveColorModeInternal();
      return;
      this.mCurrentColorBalance = 43;
      setSRGB(false);
      setDciP3(false);
      setNightMode(false);
      this.mCmgr.setActiveMode(0);
      continue;
      this.mCurrentColorBalance = 43;
      setSRGB(true);
      this.mCmgr.setActiveMode(0);
      continue;
      setSRGB(false);
      setDciP3(false);
      setNightMode(false);
      i = 100 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 57, -2);
      this.mCmgr.setActiveMode(0);
      opSetColorBalance(i, 0);
      this.mCurrentColorBalance = i;
      Slog.i("ColorBalanceService", "customer value:" + i);
      continue;
      setDciP3(true);
      this.mCurrentColorBalance = 43;
      this.mCmgr.setActiveMode(0);
    }
  }
  
  private void saveColorModeInternal() {}
  
  private void sendMsgInternal(int paramInt)
  {
    sendMsg(paramInt);
  }
  
  private void setColorMartix(float paramFloat)
  {
    this.mColorMatrix[0] = ((1.0F - paramFloat) * 0.3086F + paramFloat);
    this.mColorMatrix[1] = ((1.0F - paramFloat) * 0.3086F);
    this.mColorMatrix[2] = ((1.0F - paramFloat) * 0.3086F);
    this.mColorMatrix[3] = 0.0F;
    this.mColorMatrix[4] = ((1.0F - paramFloat) * 0.6094F);
    this.mColorMatrix[5] = ((1.0F - paramFloat) * 0.6094F + paramFloat);
    this.mColorMatrix[6] = ((1.0F - paramFloat) * 0.6094F);
    this.mColorMatrix[7] = 0.0F;
    this.mColorMatrix[8] = ((1.0F - paramFloat) * 0.082F);
    this.mColorMatrix[9] = ((1.0F - paramFloat) * 0.082F);
    this.mColorMatrix[10] = ((1.0F - paramFloat) * 0.082F + paramFloat);
    this.mColorMatrix[11] = 0.0F;
    this.mColorMatrix[12] = 0.0F;
    this.mColorMatrix[13] = 0.0F;
    this.mColorMatrix[14] = 0.0F;
    this.mColorMatrix[15] = 1.0F;
  }
  
  private void setColorMartixNight2ReadingBW(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    this.mColorMatrix[0] = ((paramFloat1 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.3086F * (1.0F - paramFloat5) + paramFloat5);
    this.mColorMatrix[1] = ((paramFloat1 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.3086F * (1.0F - paramFloat5));
    this.mColorMatrix[2] = ((paramFloat1 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.3086F * (1.0F - paramFloat5));
    this.mColorMatrix[3] = 0.0F;
    this.mColorMatrix[4] = ((paramFloat2 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.6094F * (1.0F - paramFloat5));
    this.mColorMatrix[5] = ((paramFloat2 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.6094F * (1.0F - paramFloat5) + paramFloat5);
    this.mColorMatrix[6] = ((paramFloat2 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.6094F * (1.0F - paramFloat5));
    this.mColorMatrix[7] = 0.0F;
    this.mColorMatrix[8] = ((paramFloat3 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.082F * (1.0F - paramFloat5));
    this.mColorMatrix[9] = ((paramFloat3 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.082F * (1.0F - paramFloat5));
    this.mColorMatrix[10] = ((paramFloat3 / 255.0F * (1.0F - paramFloat4) + paramFloat4) * 0.082F * (1.0F - paramFloat5) + paramFloat5);
    this.mColorMatrix[11] = 0.0F;
    this.mColorMatrix[12] = 0.0F;
    this.mColorMatrix[13] = 0.0F;
    this.mColorMatrix[14] = 0.0F;
    this.mColorMatrix[15] = 1.0F;
  }
  
  private void setColorMatrixNight2Reading(float paramFloat)
  {
    int i = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_daltonizer_enabled", 0, -2);
    int j = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, -2);
    if ((i == 1) || (j == 1)) {
      return;
    }
    setColorMartix(paramFloat);
    if (this.dtm != null) {
      this.dtm.setColorMatrix(0, this.mColorMatrix);
    }
  }
  
  private void setDTMColorMatrix()
  {
    int i = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_daltonizer_enabled", 0, -2);
    int j = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, -2);
    if ((i == 1) || (j == 1)) {
      return;
    }
    if (this.dtm != null) {
      this.dtm.setColorMatrix(0, this.mColorMatrix);
    }
  }
  
  private void setDciP3(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Slog.d("ColorBalanceService", "DCIP3,turn on!");
      SystemProperties.set("sys.dci3p", "1");
      return;
    }
    Slog.d("ColorBalanceService", "DCIP3,turn off!");
    SystemProperties.set("sys.dci3p", "0");
  }
  
  private void setNightMode(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Slog.d("ColorBalanceService", "night mode node,turn on!");
      SystemProperties.set("sys.night_mode", "1");
      return;
    }
    Slog.d("ColorBalanceService", "night mode node,turn off!");
    SystemProperties.set("sys.night_mode", "0");
  }
  
  private int setNightModeProp(Boolean paramBoolean)
  {
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
    if ((i == 0) || (i == 1)) {}
    for (;;)
    {
      setNightMode(paramBoolean.booleanValue());
      do
      {
        return i;
      } while (i != 3);
    }
  }
  
  private void setPa(int paramInt)
  {
    if (this.mCmgr != null)
    {
      PictureAdjustmentConfig localPictureAdjustmentConfig = this.mCmgr.getPictureAdjustmentParams();
      localPictureAdjustmentConfig = new PictureAdjustmentConfig(EnumSet.allOf(PictureAdjustmentConfig.PICTURE_ADJUSTMENT_PARAMS.class), localPictureAdjustmentConfig.getHue(), paramInt, localPictureAdjustmentConfig.getIntensity(), localPictureAdjustmentConfig.getContrast(), localPictureAdjustmentConfig.getSaturationThreshold());
      this.mCmgr.setPictureAdjustmentParams(localPictureAdjustmentConfig);
    }
  }
  
  private int setProp(Boolean paramBoolean)
  {
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
    if ((i == 0) || (i == 1)) {}
    while (i == 3)
    {
      setNightMode(paramBoolean.booleanValue());
      return i;
    }
    if (i == 2)
    {
      setSRGB(paramBoolean.booleanValue());
      return i;
    }
    setDciP3(paramBoolean.booleanValue());
    return i;
  }
  
  private void setSRGB(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Slog.d("ColorBalanceService", "SRGB,turn on!");
      SystemProperties.set("sys.srgb", "1");
      return;
    }
    Slog.d("ColorBalanceService", "SRGB,turn off!");
    SystemProperties.set("sys.srgb", "0");
  }
  
  private boolean setSRGBSensorEnabled(boolean paramBoolean)
  {
    Slog.i("ColorBalanceService", "setSRGBSensorEnabled:" + paramBoolean);
    if ((paramBoolean) && (this.mCmgr != null))
    {
      if (!this.mSRGBSensorEnabled)
      {
        this.mSRGBSensorEnabled = true;
        if (this.mSensorManager == null) {
          this.mSensorManager = ((SensorManager)this.mContext.getSystemService("sensor"));
        }
        if (this.mSensorManager != null)
        {
          this.mSensorManager.registerListener(this.mSRGBSensorListener, this.mSensorManager.getDefaultSensor(33171020), 3);
          this.mSensorManager.registerListener(this.mLightSensorListener, this.mSensorManager.getDefaultSensor(5), 3);
          Slog.d("ColorBalanceService", "SRGB & LIGHT SENSOR ENABLE!");
          return true;
        }
      }
    }
    else if (this.mSRGBSensorEnabled)
    {
      this.mSRGBSensorEnabled = false;
      if (this.mSensorManager != null)
      {
        this.mSensorManager.unregisterListener(this.mSRGBSensorListener);
        this.mSensorManager.unregisterListener(this.mLightSensorListener);
        Slog.d("ColorBalanceService", "SRGB & LIGHT SENSOR DISABLE!");
        return true;
      }
    }
    return false;
  }
  
  private void setUp()
  {
    this.mFactor = 1.0F;
    closeMatrix();
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "colorbalanceservice-night-reading-mode", 0, -2);
    boolean bool;
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "reading_mode_status_manual", 0, -2) == 1)
    {
      bool = true;
      if ((Boolean.valueOf(bool).booleanValue()) && ((i & 0x2) == 2)) {
        break label306;
      }
      if (1 == Settings.System.getIntForUser(this.mContext.getContentResolver(), "reading_mode_status", 0, -2))
      {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "reading_mode_status", 0, -2);
        Slog.i("ColorBalanceService", "INIT Settings.System.READING_MODE_STATUS TO BE ZERO!");
      }
      label111:
      this.mController = new NightDisplayController(getContext(), this.mCurrentUser);
      this.mController.setListener(this);
      Slog.d("ColorBalanceService", "setUp: currentUser=" + this.mCurrentUser);
      bool = this.mController.isActivated();
      onAutoModeChanged(this.mController.getAutoMode());
      if (!this.mController.isReadingModeActivated()) {
        break label329;
      }
      if (this.mIsReadingOrNightModeOpendLastSesson != null) {
        break label318;
      }
      Slog.i("ColorBalanceService", "#1 reading mode had been opened!");
      this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(true);
      label220:
      onReadingModeActivatedManual(true);
    }
    for (;;)
    {
      Slog.i("ColorBalanceService", "SetUp mode:" + i);
      if ((i == 3) && (this.mMode < 3))
      {
        this.mReadingModeStatus = Boolean.valueOf(true);
        this.mNightModeStatus = Boolean.valueOf(true);
        this.mMode = 3;
      }
      if ((i == 1) && (!Boolean.valueOf(bool).booleanValue())) {
        break label422;
      }
      return;
      bool = false;
      break;
      label306:
      Slog.i("ColorBalanceService", "INIT Settings.System.READING_MODE_STATUS_MANUAL 1!");
      break label111;
      label318:
      this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
      break label220;
      label329:
      if ((this.mController.isActivated()) && (this.mAutoMode == null))
      {
        if (this.mIsReadingOrNightModeOpendLastSesson == null) {
          Slog.i("ColorBalanceService", "#1 night mode had been opened!");
        }
        for (this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(true);; this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false))
        {
          setNightModeProp(Boolean.valueOf(true));
          onNightModeActivated(true);
          break;
        }
      }
      this.mIsReadingOrNightModeOpendLastSesson = Boolean.valueOf(false);
      if (this.mAutoMode == null) {
        sendMsg(12);
      }
    }
    label422:
    Slog.i("ColorBalanceService", "setup: turn on night Mode!");
    this.mController.setActivated(true);
  }
  
  private void setupApplication()
  {
    Slog.d("ColorBalanceService", "Display ColorManager registered..");
    if (this.mCmgr == null)
    {
      this.mCmgr = ColorManager.getInstance(ActivityThread.currentActivityThread().getApplication(), ActivityThread.currentActivityThread().getSystemContext(), ColorManager.DCM_DISPLAY_TYPE.DISP_PRIMARY);
      if (this.mCmgr == null) {
        Slog.e("ColorBalanceService", "Failed to get ColorManager instance.");
      }
    }
  }
  
  private int shading(int paramInt1, int paramInt2)
  {
    int j = paramInt1;
    int i = j;
    if (paramInt2 > paramInt1) {
      for (;;)
      {
        paramInt1 = j;
        if (j >= paramInt2) {
          break;
        }
        paramInt1 = j;
        if (this.mStartSetCount != 0) {
          break;
        }
        j += 1;
        opSetColorBalance(j, 0);
        Slog.i("ColorBalanceService", "setColorBalance:" + j);
        try
        {
          Thread.sleep(8L);
        }
        catch (InterruptedException localInterruptedException1)
        {
          localInterruptedException1.printStackTrace();
        }
      }
    }
    for (;;)
    {
      paramInt1 = i;
      if (i <= paramInt2) {
        break;
      }
      paramInt1 = i;
      if (this.mStartSetCount != 0) {
        break;
      }
      i -= 1;
      opSetColorBalance(i, 0);
      Slog.i("ColorBalanceService", "setColorBalance:" + i);
      try
      {
        Thread.sleep(8L);
      }
      catch (InterruptedException localInterruptedException2)
      {
        localInterruptedException2.printStackTrace();
      }
    }
    return paramInt1;
  }
  
  private int shadingSeekBar(int paramInt1, int paramInt2)
  {
    int j = paramInt1;
    int i = j;
    if (paramInt2 > paramInt1)
    {
      i = j;
      for (;;)
      {
        if ((i <= paramInt2) && (this.mStartSetCount == 0) && (this.mStopSetCount == 0))
        {
          opSetColorBalance(i, 1);
          Slog.i("ColorBalanceService", "SeekBar setColorBalance:" + i);
          try
          {
            Thread.sleep(7L);
            i += 1;
          }
          catch (InterruptedException localInterruptedException1)
          {
            for (;;)
            {
              localInterruptedException1.printStackTrace();
            }
          }
        }
      }
      paramInt1 = i;
      if (this.mStopSetCount == 1)
      {
        paramInt1 = i;
        while ((paramInt1 <= paramInt2) && (this.mStopSetCount == 1))
        {
          opSetColorBalance(paramInt1, 0);
          Slog.i("ColorBalanceService", "SeekBar next,setColorBalance:" + paramInt1);
          paramInt1 += 3;
        }
      }
    }
    for (;;)
    {
      try
      {
        Thread.sleep(100L);
        Slog.i("ColorBalanceService", "mStartSetCount:" + this.mStartSetCount + " mStopSetCount:" + this.mStopSetCount);
        return paramInt1;
      }
      catch (InterruptedException localInterruptedException2)
      {
        localInterruptedException2.printStackTrace();
        continue;
      }
      for (;;)
      {
        if ((i >= paramInt2) && (this.mStartSetCount == 0) && (this.mStopSetCount == 0))
        {
          opSetColorBalance(i, 0);
          Slog.i("ColorBalanceService", "SeekBar setColorBalance:" + i);
          try
          {
            Thread.sleep(7L);
            i -= 1;
          }
          catch (InterruptedException localInterruptedException3)
          {
            for (;;)
            {
              localInterruptedException3.printStackTrace();
            }
          }
        }
      }
      paramInt1 = i;
      if (this.mStopSetCount == 1)
      {
        paramInt1 = i;
        while ((paramInt1 >= paramInt2) && (this.mStopSetCount == 1))
        {
          opSetColorBalance(paramInt1, 0);
          Slog.i("ColorBalanceService", "SeekBar next,setColorBalance:" + paramInt1);
          paramInt1 -= 3;
        }
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException4)
        {
          localInterruptedException4.printStackTrace();
        }
      }
    }
  }
  
  private void tearDown()
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        Slog.d("ColorBalanceService", "tearDown: currentUser=" + this.mCurrentUser);
        if (this.mController != null)
        {
          this.mController.setListener(null);
          this.mController = null;
        }
        if (this.mAutoMode != null)
        {
          this.mAutoMode.onStop();
          this.mAutoMode = null;
        }
        this.mMode = 0;
        this.mIsTimeActivated = Boolean.valueOf(false);
        this.mNightModeAutoStatus = 0;
        this.mNightModeStatus = Boolean.valueOf(false);
        this.mReadingModeStatus = Boolean.valueOf(false);
        this.mIsDisableByApp = Boolean.valueOf(false);
        this.mIsReadingModeActivatedAuto = Boolean.valueOf(false);
        this.mModeEnable = 0;
        if (this.mIsReadingModeActivated.booleanValue())
        {
          Slog.i("ColorBalanceService", "TearDown:Turning off reading mode");
          this.mGeneration += 1;
          this.mIsReadingModeActivated = Boolean.valueOf(false);
          this.mReadingModeClosingStage = 0;
          readingModeSwitch(false, this.mGeneration, 0);
          return;
        }
        if ((this.mIsNightModeActivated != null) && (this.mIsNightModeActivated.booleanValue()))
        {
          Slog.i("ColorBalanceService", "TearDown:Turning off night  mode");
          this.mGeneration += 1;
          this.mIsNightModeActivated = Boolean.valueOf(false);
          this.mNightModeClosingStage = 0;
          nightModeSwitch(false, this.mGeneration);
        }
      }
      this.mFactor = 1.0F;
      closeMatrix();
      Slog.i("ColorBalanceService", "TearDown:Turn off B & W!");
      revertStatus();
    }
  }
  
  private void triggerNightModeButton(Boolean paramBoolean1, Boolean paramBoolean2)
  {
    if (this.mController != null)
    {
      if (!paramBoolean1.booleanValue()) {
        break label68;
      }
      if (!this.mController.isActivated())
      {
        this.mIsNightModeSettingFromeUser = paramBoolean2;
        this.mController.setActivated(true);
        Slog.i("ColorBalanceService", "night mdoe trigger button is on,Is from user:" + this.mIsNightModeSettingFromeUser);
      }
    }
    label68:
    while (!this.mController.isActivated()) {
      return;
    }
    this.mIsNightModeSettingFromeUser = paramBoolean2;
    this.mController.setActivated(false);
    Slog.i("ColorBalanceService", "night mdoe trigger button is off,Is from user:" + this.mIsNightModeSettingFromeUser);
  }
  
  private void triggerReadingButton(Boolean paramBoolean)
  {
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "reading_mode_status_manual", 0, -2);
    if ((paramBoolean.booleanValue()) && (i == 0))
    {
      this.mIsReadingModeSettingFromeUser = Boolean.valueOf(false);
      Settings.System.putStringForUser(this.mContext.getContentResolver(), "reading_mode_status_manual", "force-on", -2);
    }
    while ((paramBoolean.booleanValue()) || (i != 1)) {
      return;
    }
    this.mIsReadingModeSettingFromeUser = Boolean.valueOf(false);
    Settings.System.putStringForUser(this.mContext.getContentResolver(), "reading_mode_status_manual", "force-off", -2);
  }
  
  public void onAutoModeChanged(int paramInt)
  {
    this.mNightDisplayMoede = paramInt;
    if (this.mAutoMode != null)
    {
      this.mAutoMode.onStop();
      this.mAutoMode = null;
    }
    if (paramInt == 1) {
      this.mAutoMode = new CustomAutoMode();
    }
    for (;;)
    {
      if (this.mAutoMode != null) {
        this.mAutoMode.onStart();
      }
      return;
      if (paramInt == 2)
      {
        this.mAutoMode = new TwilightAutoMode();
      }
      else
      {
        this.mIsTimeActivated = Boolean.valueOf(false);
        this.mNightModeAutoStatus = 0;
      }
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    this.mBootPhase = paramInt;
    if (paramInt == 600)
    {
      this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
      if (this.mPowerManager != null) {
        this.mIsScreenOn = Boolean.valueOf(this.mPowerManager.isScreenOn());
      }
      if (this.dtm == null) {
        this.dtm = ((DisplayTransformManager)getLocalService(DisplayTransformManager.class));
      }
      this.mBootCompleted = true;
      if ((this.mCurrentUser != 55536) && (this.mUserSetupObserver == null))
      {
        Slog.d("ColorBalanceService", "onBootPhase Call the function setUp ");
        colorManagerInit();
        setUp();
      }
      this.mSDM = new SDManager(this.mContext);
    }
  }
  
  public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    Slog.d("ColorBalanceService", "onCustomEndTimeChanged: endTime=" + paramLocalTime);
    if (this.mAutoMode != null) {
      this.mAutoMode.onCustomEndTimeChanged(paramLocalTime);
    }
  }
  
  public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    Slog.d("ColorBalanceService", "onCustomStartTimeChanged: startTime=" + paramLocalTime);
    if (this.mAutoMode != null) {
      this.mAutoMode.onCustomStartTimeChanged(paramLocalTime);
    }
  }
  
  public void onModeSettingChange()
  {
    revertStatus();
  }
  
  public void onNightModeActivated(boolean paramBoolean)
  {
    boolean bool = true;
    Slog.i("ColorBalanceService", "onNightModeActivated:" + paramBoolean);
    Object localObject3;
    int i;
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (!this.mIsNightModeSettingFromeUser.booleanValue())
        {
          Slog.i("ColorBalanceService", "onNightModeActivated ignore being invoked ");
          this.mIsNightModeSettingFromeUser = Boolean.valueOf(true);
          return;
        }
        this.mNightModeStatus = Boolean.valueOf(paramBoolean);
        if (paramBoolean)
        {
          this.mMode |= 0x1;
          Slog.i("ColorBalanceService", "onNightModeActivated,save mode:" + this.mMode);
          Settings.System.putIntForUser(this.mContext.getContentResolver(), "colorbalanceservice-night-reading-mode", this.mMode, -2);
          if (this.mIsDisableByApp.booleanValue())
          {
            Slog.i("ColorBalanceService", "onNightModeActivated: reading mode disable App!");
            if ((paramBoolean) && (this.mReadingModeStatus.booleanValue())) {
              triggerReadingButton(Boolean.valueOf(false));
            }
          }
          else
          {
            Slog.i("ColorBalanceService", "onNightModeActivated:" + paramBoolean + " current-status:" + this.mIsNightModeActivated);
            if ((this.mIsNightModeActivated == null) || (this.mIsNightModeActivated.booleanValue() != paramBoolean))
            {
              localObject3 = new StringBuilder().append("onNightModeActivated:").append(this.mNightDisplayMoede);
              if (!paramBoolean) {
                break label974;
              }
              String str = " Turning on night display";
              Slog.i("ColorBalanceService", str);
              this.mGeneration += 1;
              this.mCurrentSrgbSensorAverageColor = 0;
              if (this.mAutoMode != null) {
                this.mAutoMode.onActivated(paramBoolean);
              }
              this.mIsNightModeActivated = Boolean.valueOf(paramBoolean);
              if (!this.mIsNightModeActivated.booleanValue()) {
                break;
              }
              this.mStableCount = 0;
              this.mAverageColor = 0;
              this.mPreColorTemp = 0;
              i = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
              this.mPretNightColorbalance = i;
              this.mCurrentNightColorbalance = i;
              if ((!this.mIsReadingModeActivated.booleanValue()) && (!this.mIsReadingModeActivatedAuto.booleanValue())) {
                break label550;
              }
              if (this.mIsReadingModeActivatedAuto.booleanValue())
              {
                Slog.i("ColorBalanceService", "turn off reading mode button!");
                Settings.System.putStringForUser(this.mContext.getContentResolver(), "reading_mode_status_manual", "force-off", -2);
              }
              this.mIsReadingModeActivated = Boolean.valueOf(false);
              this.mIsReadingModeActivatedAuto = Boolean.valueOf(false);
              triggerReadingButton(Boolean.valueOf(false));
              this.mMode |= 0x1;
              this.mModeEnable = 1;
              this.mReading2NightModeStage = 0;
              this.mModeStage = 1;
              sendMsgWithValue(14, this.mGeneration);
            }
          }
        }
        else
        {
          label499:
          this.mMode &= 0xFFFFFFFE;
        }
      }
      if ((!paramBoolean) && (this.mReadingModeStatus.booleanValue()))
      {
        triggerReadingButton(Boolean.valueOf(true));
        continue;
        label550:
        this.mMode |= 0x1;
        this.mModeEnable = 1;
        this.mNightModeOpingStage = 0;
        this.mModeStage = 1;
        this.mStableColor = 0;
        sendMsgWithValue(10, this.mGeneration);
      }
    }
    this.mMode &= 0xFFFFFFFE;
    this.mIsTimeActivated = Boolean.valueOf(false);
    this.mNightModeAutoStatus = 0;
    Slog.d("ColorBalanceService", "onNightModeActivated:Night mode is off,set mIsTimeActivated false!");
    this.mModeEnable = 3;
    Boolean localBoolean;
    Object localObject2;
    if ((this.mMode == 0) || (this.mIsDisableByApp.booleanValue()))
    {
      localBoolean = Boolean.valueOf(false);
      localObject2 = localBoolean;
    }
    for (;;)
    {
      try
      {
        if (this.mOemExSvc == null)
        {
          localObject2 = localBoolean;
          this.mOemExSvc = IOemExService.Stub.asInterface(ServiceManager.getService("OEMExService"));
        }
        localObject3 = localBoolean;
        localObject2 = localBoolean;
        if (this.mOemExSvc != null)
        {
          localObject2 = localBoolean;
          localObject3 = Boolean.valueOf(this.mOemExSvc.preEvaluateModeStatus(0, 1));
          localObject2 = localObject3;
          i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "reading_mode_status", 0, -2);
          localObject2 = localObject3;
          Slog.i("ColorBalanceService", "isAutoReadingModeOn:" + localObject3 + " READING_MODE_STATUS:" + i);
          localObject2 = localObject3;
          if ((!((Boolean)localObject3).booleanValue()) || (i != 1)) {
            break label982;
          }
          paramBoolean = bool;
          localObject3 = Boolean.valueOf(paramBoolean);
        }
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("ColorBalanceService", "error while operating the scene mode controller: " + localRemoteException);
        Object localObject4 = localObject2;
        continue;
        this.mNightModeClosingStage = 0;
        this.mModeStage = 3;
        this.mStableColor = 0;
        sendMsgWithValue(10, this.mGeneration);
      }
      if (((Boolean)localObject3).booleanValue())
      {
        this.mIsReadingModeActivated = Boolean.valueOf(true);
        this.mModeEnable = 2;
        Slog.i("ColorBalanceService", "Auto Reading mode is on,turn on Reading mode");
        this.mNight2ReadingModeStage = 0;
        this.mModeStage = 5;
        sendMsgWithValue(13, this.mGeneration);
        break label499;
        this.mIsReadingModeActivated = Boolean.valueOf(true);
        this.mModeEnable = 2;
        Slog.i("ColorBalanceService", "onNightModeActivated:Night mode is off, revert to reading mode!");
        this.mNight2ReadingModeStage = 0;
        this.mModeStage = 5;
        sendMsgWithValue(13, this.mGeneration);
        triggerReadingButton(Boolean.valueOf(true));
        break label499;
      }
      break label499;
      label974:
      localObject2 = " Turning off night display";
      break;
      label982:
      paramBoolean = false;
    }
  }
  
  public void onNightOrReadingModeDisableByApp(boolean paramBoolean)
  {
    boolean bool = false;
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mIsDisableByApp = Boolean.valueOf(paramBoolean);
        this.mCurrentSrgbSensorAverageColor = 0;
        Slog.i("ColorBalanceService", "onNightOrReadingModeChangeByApp,on:" + paramBoolean + " mModeEnable:" + this.mModeEnable + " mReadingModeStatus:" + this.mReadingModeStatus);
        if (this.mModeEnable == 2) {
          break label279;
        }
        if (this.mModeEnable == 5)
        {
          break label279;
          this.mIsReadingModeActivated = Boolean.valueOf(paramBoolean);
          this.mReadingModeOpingStage = 0;
          this.mReadingModeClosingStage = 0;
          if (this.mIsReadingModeActivated.booleanValue())
          {
            this.mModeEnable = 2;
            this.mCurrentReadingColorbalance = -20;
            StringBuilder localStringBuilder = new StringBuilder().append("onNightOrReadingModeChangeByApp,");
            if (!this.mIsReadingModeActivated.booleanValue()) {
              break label263;
            }
            String str1 = "turn on reading mode!";
            Slog.i("ColorBalanceService", str1);
            if (!this.mIsReadingModeActivated.booleanValue()) {
              break label270;
            }
            this.mModeStage = 5;
            this.mGeneration += 1;
            sendMsgWithValue(11, this.mGeneration);
          }
        }
        else
        {
          if ((!this.mReadingModeStatus.booleanValue()) || (this.mModeEnable == 1)) {
            continue;
          }
          break label279;
          paramBoolean = this.mReadingModeStatus.booleanValue();
          continue;
        }
        this.mModeEnable = 5;
      }
      label263:
      String str2 = "turn off reading mode!";
      continue;
      label270:
      this.mModeStage = 7;
      continue;
      label279:
      if (paramBoolean) {
        paramBoolean = bool;
      }
    }
  }
  
  public void onReadingModeActivatedAuto(boolean paramBoolean)
  {
    Slog.i("ColorBalanceService", "onReadingModeActivatedAuto:" + paramBoolean + " bootPhase: " + this.mBootPhase);
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        i = this.mBootPhase;
        if (i < 1000) {
          return;
        }
        Slog.i("ColorBalanceService", "onReadingModeActivatedAuto:" + this.mIsReadingModeActivatedAuto);
        if (this.mIsReadingModeActivatedAuto.booleanValue() == paramBoolean)
        {
          Slog.i("ColorBalanceService", "onReadingModeActivatedAuto ignore same status!");
          return;
        }
        this.mCurrentSrgbSensorAverageColor = 0;
        this.mIsReadingModeActivatedAuto = Boolean.valueOf(paramBoolean);
        if (this.mIsReadingModeActivatedAuto.booleanValue())
        {
          this.mStableCount = 0;
          this.mAverageColor = 0;
          this.mPreColorTemp = 0;
          if ((this.mIsNightModeActivated != null) && (this.mIsNightModeActivated.booleanValue()))
          {
            this.mIsReadingModeActivated = Boolean.valueOf(true);
            this.mIsNightModeActivated = Boolean.valueOf(false);
            this.mModeEnable = 2;
            Slog.i("ColorBalanceService", "onReadingModeActivatedAuto:switch night into reading mode!");
            this.mNight2ReadingModeStage = 0;
            this.mModeStage = 5;
            this.mGeneration += 1;
            sendMsgWithValue(13, this.mGeneration);
            triggerNightModeButton(Boolean.valueOf(false), Boolean.valueOf(false));
            return;
          }
          this.mIsReadingModeActivated = Boolean.valueOf(true);
          this.mIsNightModeActivated = Boolean.valueOf(false);
          this.mModeEnable = 2;
          this.mReadingModeOpingStage = 0;
          this.mCurrentReadingColorbalance = -20;
          Slog.i("ColorBalanceService", "onReadingModeActivatedAuto:turning reading mode!");
          this.mModeStage = 5;
          this.mStableColor = 0;
          this.mGeneration += 1;
          sendMsgWithValue(11, this.mGeneration);
        }
      }
      if ((this.mNightModeStatus.booleanValue()) || (this.mNightModeAutoStatus == 1))
      {
        this.mIsNightModeActivated = Boolean.valueOf(true);
        this.mIsReadingModeActivated = Boolean.valueOf(false);
        this.mModeEnable = 1;
        this.mReading2NightModeStage = 0;
        triggerReadingButton(Boolean.valueOf(false));
        triggerNightModeButton(Boolean.valueOf(true), Boolean.valueOf(false));
        i = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
        this.mPretNightColorbalance = i;
        this.mCurrentNightColorbalance = i;
        this.mModeStage = 1;
        this.mGeneration += 1;
        sendMsgWithValue(14, this.mGeneration);
      }
      else
      {
        if (!this.mReadingModeStatus.booleanValue())
        {
          this.mIsReadingModeActivated = Boolean.valueOf(false);
          this.mModeEnable = 4;
          Slog.i("ColorBalanceService", "onReadingModeActivatedAuto Turning off reading mode");
          this.mReadingModeClosingStage = 0;
          this.mModeStage = 7;
          this.mGeneration += 1;
          sendMsgWithValue(11, this.mGeneration);
        }
        if (this.mNightModeAutoStatus == -1)
        {
          Slog.i("ColorBalanceService", "onReadingModeActivatedAuto: night mode should be off status!");
          this.mNightModeAutoStatus = 0;
          this.mMode &= 0xFFFFFFFE;
          this.mIsNightModeActivated = Boolean.valueOf(false);
          this.mNightModeStatus = Boolean.valueOf(false);
          triggerNightModeButton(Boolean.valueOf(false), Boolean.valueOf(false));
        }
      }
    }
  }
  
  public void onReadingModeActivatedManual(boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (!this.mIsReadingModeSettingFromeUser.booleanValue())
        {
          this.mIsReadingModeSettingFromeUser = Boolean.valueOf(true);
          Slog.i("ColorBalanceService", "onReadingModeActivatedManual ignore being invoked!");
          return;
        }
        this.mReadingModeStatus = Boolean.valueOf(paramBoolean);
        if (paramBoolean)
        {
          this.mMode |= 0x2;
          Slog.i("ColorBalanceService", "onReadingModeActivatedManual,save mode:" + this.mMode);
          Settings.System.putIntForUser(this.mContext.getContentResolver(), "colorbalanceservice-night-reading-mode", this.mMode, -2);
          if (!this.mIsDisableByApp.booleanValue()) {
            break;
          }
          Slog.i("ColorBalanceService", "reaing disable app,ignore!");
          if ((paramBoolean) && (this.mNightModeStatus.booleanValue()))
          {
            triggerNightModeButton(Boolean.valueOf(false), Boolean.valueOf(false));
            this.mNightModeClosingStage = 0;
            this.mModeStage = 3;
            this.mIsNightModeActivated = Boolean.valueOf(false);
            sendMsgWithValue(10, this.mGeneration);
          }
        }
        else
        {
          this.mMode &= 0xFFFFFFFD;
        }
      }
      if ((!paramBoolean) && (this.mNightModeStatus.booleanValue())) {
        triggerNightModeButton(Boolean.valueOf(true), Boolean.valueOf(true));
      }
    }
    Slog.i("ColorBalanceService", "onReadingModeActivatedManual:" + paramBoolean + " current-status:" + this.mIsReadingModeActivated);
    if (this.mIsReadingModeActivated.booleanValue() != paramBoolean)
    {
      this.mIsReadingModeActivated = Boolean.valueOf(paramBoolean);
      if (!this.mIsReadingModeActivated.booleanValue()) {
        break label474;
      }
      this.mStableCount = 0;
      this.mAverageColor = 0;
      this.mPreColorTemp = 0;
      this.mCurrentReadingColorbalance = -20;
      this.mMode |= 0x2;
      if ((this.mIsNightModeActivated == null) || (!this.mIsNightModeActivated.booleanValue())) {
        break label422;
      }
      this.mIsNightModeActivated = Boolean.valueOf(false);
      triggerNightModeButton(Boolean.valueOf(false), Boolean.valueOf(false));
      this.mNightModeClosingStage = 0;
      this.mModeEnable = 2;
      Slog.i("ColorBalanceService", "onReadingModeActivatedManual changing night to reading mode");
      this.mNight2ReadingModeStage = 0;
      this.mModeStage = 5;
      this.mGeneration += 1;
      sendMsgWithValue(13, this.mGeneration);
    }
    for (;;)
    {
      return;
      label422:
      this.mModeEnable = 2;
      Slog.i("ColorBalanceService", "onReadingModeActivatedManual Turning on reading mode");
      this.mReadingModeOpingStage = 0;
      this.mModeStage = 5;
      this.mStableColor = 0;
      this.mGeneration += 1;
      sendMsgWithValue(11, this.mGeneration);
      continue;
      label474:
      this.mMode &= 0xFFFFFFFD;
      Slog.i("ColorBalanceService", "onReadingModeActivatedManual Turning off reading mode");
      if ((this.mNightModeAutoStatus == 1) || (this.mMode != 0))
      {
        this.mMode |= 0x1;
        this.mIsNightModeActivated = Boolean.valueOf(true);
        this.mIsReadingModeActivated = Boolean.valueOf(false);
        this.mModeEnable = 1;
        this.mReading2NightModeStage = 0;
        int i = 132 - Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 103, -2) - 90;
        this.mPretNightColorbalance = i;
        this.mCurrentNightColorbalance = i;
        this.mModeStage = 1;
        this.mGeneration += 1;
        sendMsgWithValue(14, this.mGeneration);
        triggerNightModeButton(Boolean.valueOf(true), Boolean.valueOf(false));
        Slog.i("ColorBalanceService", "onReadingModeActivatedManual Revert to night mode!");
      }
      else
      {
        this.mModeStage = 7;
        this.mStableColor = 0;
        this.mGeneration += 1;
        this.mModeEnable = 4;
        this.mReadingModeClosingStage = 0;
        sendMsgWithValue(11, this.mGeneration);
      }
    }
  }
  
  public void onStart()
  {
    publishBinderService("nightdisplay", new BinderService(null), true);
  }
  
  public void onStartUser(int paramInt)
  {
    super.onStartUser(paramInt);
    if (this.mCurrentUser == 55536) {
      onUserChanged(paramInt);
    }
  }
  
  public void onStopUser(int paramInt)
  {
    super.onStopUser(paramInt);
    if (this.mCurrentUser == paramInt) {
      onUserChanged(55536);
    }
  }
  
  public void onSwitchUser(int paramInt)
  {
    super.onSwitchUser(paramInt);
    onUserChanged(paramInt);
  }
  
  public void sendMsg(int paramInt)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt;
    if (this.mCMHHandler != null)
    {
      this.mCMHHandler.removeMessages(localMessage.what);
      this.mCMHHandler.sendMessage(localMessage);
    }
  }
  
  public void sendMsgWithValue(int paramInt1, int paramInt2)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    if (this.mCMHHandler != null)
    {
      if (localMessage.what != 3) {
        this.mCMHHandler.removeMessages(localMessage.what);
      }
      this.mCMHHandler.sendMessage(localMessage);
    }
  }
  
  public void sendMsgWithValueDelayed(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    if (this.mCMHHandler != null) {
      this.mCMHHandler.sendMessageDelayed(localMessage, paramInt4);
    }
  }
  
  private abstract class AutoMode
    implements NightDisplayController.Callback
  {
    private AutoMode() {}
    
    public abstract void onStart();
    
    public abstract void onStop();
  }
  
  private final class BinderService
    extends IColorBalanceManager.Stub
  {
    private BinderService() {}
    
    public void sendMsg(int paramInt)
    {
      Slog.i("ColorBalanceService", "MSG:" + paramInt);
      ColorBalanceService.-wrap15(ColorBalanceService.this, paramInt);
    }
    
    public void setActiveMode(int paramInt)
    {
      ColorBalanceService.this.sendMsgWithValue(5, paramInt);
    }
    
    public void setColorBalance(int paramInt)
    {
      if (paramInt == 65024) {
        return;
      }
      ColorBalanceService localColorBalanceService = ColorBalanceService.this;
      ColorBalanceService.-set15(localColorBalanceService, ColorBalanceService.-get23(localColorBalanceService) + 1);
      ColorBalanceService.this.sendMsgWithValue(3, paramInt);
    }
    
    public void setDefaultMode(int paramInt)
    {
      ColorBalanceService.this.sendMsgWithValue(6, paramInt);
    }
  }
  
  private final class CMH
    extends Handler
  {
    public CMH(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (!ColorBalanceService.-wrap0(ColorBalanceService.this))
      {
        ColorBalanceService.-set3(ColorBalanceService.this, Boolean.valueOf(false));
        Slog.e("ColorBalanceService", "mCmgr is null!");
        return;
      }
      if (ColorBalanceService.-get18(ColorBalanceService.this) == null) {
        ColorBalanceService.-set12(ColorBalanceService.this, new SDManager(ColorBalanceService.-get2(ColorBalanceService.this)));
      }
      switch (paramMessage.what)
      {
      case 7: 
      default: 
        Slog.i("ColorBalanceService", "msg:" + paramMessage.what);
      }
      int i;
      do
      {
        do
        {
          do
          {
            return;
            Slog.d("ColorBalanceService", "MSG_SCREEN_ON");
            ColorBalanceService.-set4(ColorBalanceService.this, Boolean.valueOf(true));
            ColorBalanceService.-wrap10(ColorBalanceService.this, true);
            return;
            Slog.d("ColorBalanceService", "MSG_SCREEN_OFF");
            ColorBalanceService.-set4(ColorBalanceService.this, Boolean.valueOf(false));
            if (ColorBalanceService.-get7(ColorBalanceService.this).booleanValue())
            {
              Slog.i("ColorBalanceService", "MSG_SCREEN_OFF: mIsDisableByApp");
              return;
            }
            ColorBalanceService.-wrap10(ColorBalanceService.this, false);
            return;
            ColorBalanceService.-wrap6(ColorBalanceService.this, ColorBalanceService.-get8(ColorBalanceService.this).booleanValue(), paramMessage.arg1);
            return;
            ColorBalanceService.-wrap12(ColorBalanceService.this, ColorBalanceService.-get9(ColorBalanceService.this).booleanValue(), paramMessage.arg1, paramMessage.arg2);
            return;
            ColorBalanceService.-wrap4(ColorBalanceService.this, paramMessage.arg1, paramMessage.arg2);
            return;
            ColorBalanceService.-wrap3(ColorBalanceService.this, paramMessage.arg1, paramMessage.arg2);
            return;
            ColorBalanceService.-wrap11(ColorBalanceService.this, paramMessage.arg1);
            return;
            ColorBalanceService.-wrap14(ColorBalanceService.this);
            return;
            i = paramMessage.arg1;
            ColorBalanceService.-get1(ColorBalanceService.this).setActiveMode(i);
            Slog.i("ColorBalanceService", "AMODE:" + i);
            return;
            i = paramMessage.arg1;
            ColorBalanceService.-get1(ColorBalanceService.this).setDefaultMode(i);
            Slog.i("ColorBalanceService", "AMODE:" + i);
            return;
            ColorBalanceService.-wrap8(ColorBalanceService.this, paramMessage);
            return;
            ColorBalanceService.-wrap9(ColorBalanceService.this, paramMessage);
            return;
            ColorBalanceService.-wrap13(ColorBalanceService.this);
            return;
          } while ((ColorBalanceService.-get12(ColorBalanceService.this) != paramMessage.arg1) || (ColorBalanceService.-get22(ColorBalanceService.this) == 4500));
          ColorBalanceService.-set14(ColorBalanceService.this, 4500);
          ColorBalanceService.-set8(ColorBalanceService.this, 0);
          ColorBalanceService.-wrap7(ColorBalanceService.this);
          return;
          Slog.i("ColorBalanceService", "MSG_CHECK_SRGBSEN,generation:" + ColorBalanceService.-get21(ColorBalanceService.this) + " =? " + paramMessage.arg1 + " color:" + ColorBalanceService.-get20(ColorBalanceService.this));
        } while (ColorBalanceService.-get21(ColorBalanceService.this) != paramMessage.arg1);
        i = 0;
        if (ColorBalanceService.-get22(ColorBalanceService.this) != 0) {
          i = Math.abs(ColorBalanceService.-get22(ColorBalanceService.this) - ColorBalanceService.-get20(ColorBalanceService.this));
        }
        Slog.i("ColorBalanceService", "mStableColor:" + ColorBalanceService.-get22(ColorBalanceService.this) + " delat:" + i);
        ColorBalanceService.-set14(ColorBalanceService.this, ColorBalanceService.-get20(ColorBalanceService.this));
      } while (i <= 500);
      ColorBalanceService.-wrap7(ColorBalanceService.this);
    }
  }
  
  private static class ColorMatrixEvaluator
    implements TypeEvaluator<float[]>
  {
    private final float[] mResultMatrix = new float[16];
    
    public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = 0;
      while (i < this.mResultMatrix.length)
      {
        this.mResultMatrix[i] = MathUtils.lerp(paramArrayOfFloat1[i], paramArrayOfFloat2[i], paramFloat);
        i += 1;
      }
      return this.mResultMatrix;
    }
  }
  
  private class CustomAutoMode
    extends ColorBalanceService.AutoMode
    implements AlarmManager.OnAlarmListener
  {
    private final AlarmManager mAlarmManager = (AlarmManager)ColorBalanceService.this.getContext().getSystemService("alarm");
    private NightDisplayController.LocalTime mEndTime;
    private Calendar mLastActivatedTime;
    private NightDisplayController.LocalTime mStartTime;
    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        ColorBalanceService.CustomAutoMode.-wrap0(ColorBalanceService.CustomAutoMode.this);
      }
    };
    
    public CustomAutoMode()
    {
      super(null);
    }
    
    private void updateActivated()
    {
      Slog.i("ColorBalanceService", "CustomAutoMode updateActivated!");
      boolean bool3;
      label320:
      label430:
      label617:
      Object localObject3;
      for (;;)
      {
        synchronized (ColorBalanceService.-get13(ColorBalanceService.this))
        {
          Calendar localCalendar = Calendar.getInstance();
          Object localObject1 = this.mStartTime.getDateTimeBefore(localCalendar);
          bool3 = localCalendar.before(this.mEndTime.getDateTimeAfter((Calendar)localObject1));
          boolean bool2;
          if (this.mLastActivatedTime == null)
          {
            bool2 = true;
            bool1 = bool2;
            if (!bool2) {
              if (ColorBalanceService.-get8(ColorBalanceService.this) != null)
              {
                bool1 = bool2;
                if (ColorBalanceService.-get8(ColorBalanceService.this).booleanValue() == bool3) {}
              }
              else
              {
                localObject1 = localCalendar.getTimeZone();
                if (!((TimeZone)localObject1).equals(this.mLastActivatedTime.getTimeZone()))
                {
                  int i = this.mLastActivatedTime.get(1);
                  int j = this.mLastActivatedTime.get(6);
                  int k = this.mLastActivatedTime.get(11);
                  int m = this.mLastActivatedTime.get(12);
                  this.mLastActivatedTime.setTimeZone((TimeZone)localObject1);
                  this.mLastActivatedTime.set(1, i);
                  this.mLastActivatedTime.set(6, j);
                  this.mLastActivatedTime.set(11, k);
                  this.mLastActivatedTime.set(12, m);
                }
                if ((ColorBalanceService.-get8(ColorBalanceService.this) == null) || (!ColorBalanceService.-get8(ColorBalanceService.this).booleanValue())) {
                  continue;
                }
                if (localCalendar.before(this.mStartTime.getDateTimeBefore(this.mLastActivatedTime))) {
                  continue;
                }
                bool1 = localCalendar.after(this.mEndTime.getDateTimeAfter(this.mLastActivatedTime));
              }
            }
            StringBuilder localStringBuilder;
            if ((ColorBalanceService.-get9(ColorBalanceService.this).booleanValue()) || (ColorBalanceService.-get3(ColorBalanceService.this).isReadingModeActivated()))
            {
              localStringBuilder = new StringBuilder().append("CustomAutoMode Reading mode is on,dont turn ");
              if (!bool3) {
                break label854;
              }
              localObject1 = "on";
              Slog.i("ColorBalanceService", (String)localObject1 + " night mode automatically for time up.");
              if (!bool3) {
                continue;
              }
              ColorBalanceService.-set10(ColorBalanceService.this, 1);
            }
            if (bool1)
            {
              if ((!ColorBalanceService.-get9(ColorBalanceService.this).booleanValue()) && (!ColorBalanceService.-get3(ColorBalanceService.this).isReadingModeActivated())) {
                break label624;
              }
              localStringBuilder = new StringBuilder().append("CustomAutoMode Reading mode is on,dont turn ");
              if (!bool3) {
                break label617;
              }
              localObject1 = "on";
              Slog.i("ColorBalanceService", (String)localObject1 + " night mode automatically for time up!");
              Slog.i("ColorBalanceService", "time up:" + bool3);
            }
            ColorBalanceService.-set3(ColorBalanceService.this, Boolean.valueOf(false));
            ColorBalanceService.-set5(ColorBalanceService.this, Boolean.valueOf(bool3));
            updateNextAlarm(ColorBalanceService.-get8(ColorBalanceService.this), localCalendar);
          }
          else
          {
            bool2 = false;
            continue;
          }
          bool1 = true;
          continue;
          if (localCalendar.before(this.mEndTime.getDateTimeBefore(this.mLastActivatedTime))) {
            break label848;
          }
          bool1 = localCalendar.after(this.mStartTime.getDateTimeAfter(this.mLastActivatedTime));
          continue;
          ColorBalanceService.-set10(ColorBalanceService.this, -1);
          localObject1 = ColorBalanceService.this;
          ColorBalanceService.-set9((ColorBalanceService)localObject1, ColorBalanceService.-get15((ColorBalanceService)localObject1) & 0xFFFFFFFE);
          ColorBalanceService.-set11(ColorBalanceService.this, Boolean.valueOf(false));
          ColorBalanceService.-wrap18(ColorBalanceService.this, Boolean.valueOf(false), Boolean.valueOf(false));
        }
        localObject3 = "off";
        continue;
        label624:
        ColorBalanceService.-set10(ColorBalanceService.this, 0);
        if (ColorBalanceService.-get3(ColorBalanceService.this) != null)
        {
          if (ColorBalanceService.-get8(ColorBalanceService.this) != null) {
            break label832;
          }
          if (ColorBalanceService.-get3(ColorBalanceService.this).isActivated() == bool3) {
            break;
          }
          Slog.i("ColorBalanceService", "JUST BOOT 1:" + bool3);
          ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool3);
        }
      }
      if (bool3)
      {
        localObject3 = ColorBalanceService.-get3(ColorBalanceService.this);
        if (!bool3) {
          break label861;
        }
      }
      label832:
      label848:
      label854:
      label861:
      for (boolean bool1 = false;; bool1 = true)
      {
        ((NightDisplayController)localObject3).setActivated(bool1);
        ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool3);
        Slog.i("ColorBalanceService", "JUST BOOT 2:" + bool3);
        break label430;
        ColorBalanceService.this.sendMsg(12);
        ColorBalanceService.-set1(ColorBalanceService.this, Boolean.valueOf(false));
        Slog.i("ColorBalanceService", "JUST BOOT 2:" + bool3);
        break label430;
        ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool3);
        break label430;
        bool1 = true;
        break;
        localObject3 = "off";
        break label320;
      }
    }
    
    private void updateActivatedOnCustomStartTimeChanged()
    {
      updateNextAlarm(Boolean.valueOf(true), Calendar.getInstance());
    }
    
    private void updateNextAlarm(Boolean paramBoolean, Calendar paramCalendar)
    {
      paramBoolean = this.mStartTime.getDateTimeAfter(paramCalendar);
      paramCalendar = this.mEndTime.getDateTimeAfter(paramCalendar);
      if (paramBoolean.before(paramCalendar))
      {
        Slog.i("ColorBalanceService", "updateNextAlarm nextStart setExact:" + paramBoolean);
        this.mAlarmManager.setExact(1, paramBoolean.getTimeInMillis(), "ColorBalanceService", this, null);
        return;
      }
      Slog.i("ColorBalanceService", "updateNextAlarm nextEnd setExact:" + paramCalendar);
      this.mAlarmManager.setExact(1, paramCalendar.getTimeInMillis(), "ColorBalanceService", this, null);
    }
    
    public void onActivated(boolean paramBoolean)
    {
      Calendar localCalendar = Calendar.getInstance();
      this.mLastActivatedTime = localCalendar;
      updateNextAlarm(Boolean.valueOf(paramBoolean), localCalendar);
    }
    
    public void onAlarm()
    {
      Slog.d("ColorBalanceService", "onAlarm");
      updateActivated();
    }
    
    public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
    {
      this.mEndTime = paramLocalTime;
      this.mLastActivatedTime = null;
      updateActivated();
    }
    
    public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
    {
      this.mStartTime = paramLocalTime;
      this.mLastActivatedTime = null;
      updateActivated();
    }
    
    public void onStart()
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      ColorBalanceService.this.getContext().registerReceiver(this.mTimeChangedReceiver, localIntentFilter);
      this.mStartTime = ColorBalanceService.-get3(ColorBalanceService.this).getCustomStartTime();
      this.mEndTime = ColorBalanceService.-get3(ColorBalanceService.this).getCustomEndTime();
      updateActivated();
    }
    
    public void onStop()
    {
      ColorBalanceService.this.getContext().unregisterReceiver(this.mTimeChangedReceiver);
      this.mAlarmManager.cancel(this);
      this.mLastActivatedTime = null;
      if (ColorBalanceService.-get8(ColorBalanceService.this).booleanValue())
      {
        Slog.i("ColorBalanceService", "CustomAutoMode stop,disable night display mode!");
        if (ColorBalanceService.-get3(ColorBalanceService.this) != null) {
          ColorBalanceService.-get3(ColorBalanceService.this).setActivated(false);
        }
      }
    }
  }
  
  public class ModeInfoWrapper
  {
    public ModeInfo mode;
    public int modeID;
    public String modename;
    
    ModeInfoWrapper(ModeInfo paramModeInfo)
    {
      this.mode = paramModeInfo;
      this.modename = paramModeInfo.getName();
      this.modeID = paramModeInfo.getId();
    }
    
    public void resetName()
    {
      this.modename = this.mode.getName();
    }
    
    public String toString()
    {
      return this.modename;
    }
  }
  
  private class TwilightAutoMode
    extends ColorBalanceService.AutoMode
    implements TwilightListener
  {
    private Calendar mLastActivatedTime;
    private final TwilightManager mTwilightManager = (TwilightManager)ColorBalanceService.-wrap2(ColorBalanceService.this, TwilightManager.class);
    
    public TwilightAutoMode()
    {
      super(null);
    }
    
    private void updateActivated(TwilightState paramTwilightState)
    {
      boolean bool2 = false;
      Object localObject2 = ColorBalanceService.-get13(ColorBalanceService.this);
      Object localObject1 = paramTwilightState;
      boolean bool1;
      for (;;)
      {
        try
        {
          if (Math.abs(SystemClock.uptimeMillis() - paramTwilightState.sunriseTimeMillis()) > 172800000L)
          {
            Slog.i("ColorBalanceService", "1 updateActivated:" + paramTwilightState);
            localObject1 = this.mTwilightManager.getLastTwilightState();
            Slog.i("ColorBalanceService", "2 updateActivated:" + localObject1);
          }
          if (localObject1 != null)
          {
            bool1 = ((TwilightState)localObject1).isNight();
            if (localObject1 != null) {
              Slog.i("ColorBalanceService", "updateActivated:" + localObject1 + " isNight:" + bool1 + " setActivated:" + true);
            }
            if ((ColorBalanceService.-get9(ColorBalanceService.this).booleanValue()) || (ColorBalanceService.-get3(ColorBalanceService.this).isReadingModeActivated()))
            {
              localObject1 = new StringBuilder().append("TwilightAutoMode Reading mode is on,dont turn ");
              if (bool1)
              {
                paramTwilightState = "on";
                Slog.i("ColorBalanceService", paramTwilightState + " night mode automatically for time up.");
                if (!bool1) {
                  continue;
                }
                ColorBalanceService.-set10(ColorBalanceService.this, 1);
              }
            }
            else if (1 != 0)
            {
              if ((!ColorBalanceService.-get9(ColorBalanceService.this).booleanValue()) && (!ColorBalanceService.-get3(ColorBalanceService.this).isReadingModeActivated())) {
                break label415;
              }
              localObject1 = new StringBuilder().append("TwilightAutoMode Reading mode is on,dont turn ");
              if (!bool1) {
                break label409;
              }
              paramTwilightState = "on";
              Slog.i("ColorBalanceService", paramTwilightState + " night mode automatically for time up!");
              ColorBalanceService.-set3(ColorBalanceService.this, Boolean.valueOf(false));
              ColorBalanceService.-set5(ColorBalanceService.this, Boolean.valueOf(bool1));
            }
          }
          else
          {
            bool1 = false;
            continue;
          }
          paramTwilightState = "off";
          continue;
          ColorBalanceService.-set10(ColorBalanceService.this, -1);
          paramTwilightState = ColorBalanceService.this;
          ColorBalanceService.-set9(paramTwilightState, ColorBalanceService.-get15(paramTwilightState) & 0xFFFFFFFE);
          ColorBalanceService.-set11(ColorBalanceService.this, Boolean.valueOf(false));
          ColorBalanceService.-wrap18(ColorBalanceService.this, Boolean.valueOf(false), Boolean.valueOf(false));
          continue;
          paramTwilightState = "off";
        }
        finally {}
        label409:
        continue;
        label415:
        ColorBalanceService.-set10(ColorBalanceService.this, 0);
        if (ColorBalanceService.-get3(ColorBalanceService.this) != null)
        {
          if (ColorBalanceService.-get8(ColorBalanceService.this) != null) {
            break label609;
          }
          if (ColorBalanceService.-get3(ColorBalanceService.this).isActivated() == bool1) {
            break;
          }
          Slog.i("ColorBalanceService", "JUST BOOT 1:" + bool1);
          ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool1);
        }
      }
      if (bool1)
      {
        paramTwilightState = ColorBalanceService.-get3(ColorBalanceService.this);
        if (!bool1) {
          break label624;
        }
      }
      for (;;)
      {
        paramTwilightState.setActivated(bool2);
        ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool1);
        Slog.i("ColorBalanceService", "JUST BOOT 2:" + bool1);
        break;
        ColorBalanceService.this.sendMsg(12);
        ColorBalanceService.-set1(ColorBalanceService.this, Boolean.valueOf(false));
        Slog.i("ColorBalanceService", "JUST BOOT 2:" + bool1);
        break;
        label609:
        ColorBalanceService.-get3(ColorBalanceService.this).setActivated(bool1);
        break;
        label624:
        bool2 = true;
      }
    }
    
    public void onActivated(boolean paramBoolean)
    {
      this.mLastActivatedTime = Calendar.getInstance();
    }
    
    public void onStart()
    {
      this.mTwilightManager.registerListener(this, ColorBalanceService.-get6(ColorBalanceService.this));
      updateActivated(this.mTwilightManager.getLastTwilightState());
    }
    
    public void onStop()
    {
      this.mTwilightManager.unregisterListener(this);
      this.mLastActivatedTime = null;
      if ((ColorBalanceService.-get8(ColorBalanceService.this) != null) && (ColorBalanceService.-get8(ColorBalanceService.this).booleanValue()))
      {
        Slog.i("ColorBalanceService", "CustomAutoMode stop,disable night display mode!");
        if (ColorBalanceService.-get3(ColorBalanceService.this) != null) {
          ColorBalanceService.-get3(ColorBalanceService.this).setActivated(false);
        }
      }
    }
    
    public void onTwilightStateChanged(TwilightState paramTwilightState)
    {
      Object localObject = null;
      StringBuilder localStringBuilder = new StringBuilder().append("onTwilightStateChanged: isNight=");
      if (paramTwilightState == null) {}
      for (;;)
      {
        Slog.d("ColorBalanceService", localObject);
        updateActivated(paramTwilightState);
        return;
        localObject = Boolean.valueOf(paramTwilightState.isNight());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/ColorBalanceService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */