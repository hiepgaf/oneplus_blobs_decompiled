package com.android.server;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.MutableBoolean;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.internal.logging.MetricsLogger;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.util.Arrays;

public class GestureLauncherService
  extends SystemService
{
  private static final long CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS = 300L;
  private static final long CAMERA_POWER_DOUBLE_TAP_MIN_TIME_MS = 120L;
  private static final boolean DBG = false;
  private static final long EMERGENCY_CALL_POWER_KEY_TAP_INTERVAL = 400L;
  private static final String TAG = "GestureLauncherService";
  private boolean mCameraDoubleTapPowerEnabled;
  private long mCameraGestureLastEventTime = 0L;
  private long mCameraGestureOnTimeMs = 0L;
  private long mCameraGestureSensor1LastOnTimeMs = 0L;
  private long mCameraGestureSensor2LastOnTimeMs = 0L;
  private int mCameraLaunchLastEventExtra = 0;
  private Sensor mCameraLaunchSensor;
  private Context mContext;
  private long mDuration;
  private String mEmergencyNumber;
  private final GestureEventListener mGestureListener = new GestureEventListener(null);
  private long[] mHits;
  private boolean mIsEmergencyOnPowerKeyTapEnabled;
  private long mLastPowerDown;
  private boolean mRegistered;
  private final ContentObserver mSettingObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri, int paramAnonymousInt)
    {
      if (paramAnonymousInt == GestureLauncherService.-get9(GestureLauncherService.this))
      {
        GestureLauncherService.-wrap3(GestureLauncherService.this);
        GestureLauncherService.-wrap2(GestureLauncherService.this);
      }
    }
  };
  private int mUserId;
  private final BroadcastReceiver mUserReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousIntent.getAction()))
      {
        GestureLauncherService.-set4(GestureLauncherService.this, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0));
        GestureLauncherService.-get6(GestureLauncherService.this).getContentResolver().unregisterContentObserver(GestureLauncherService.-get8(GestureLauncherService.this));
        GestureLauncherService.-wrap1(GestureLauncherService.this);
        GestureLauncherService.-wrap3(GestureLauncherService.this);
        GestureLauncherService.-wrap2(GestureLauncherService.this);
      }
    }
  };
  private PowerManager.WakeLock mWakeLock;
  
  public GestureLauncherService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  private boolean handleCameraLaunchGesture(boolean paramBoolean, int paramInt)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0) {}
    for (int i = 1; i == 0; i = 0) {
      return false;
    }
    if (paramBoolean) {
      this.mWakeLock.acquire(500L);
    }
    ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class)).onCameraLaunchGestureDetected(paramInt);
    return true;
  }
  
  public static boolean isCameraDoubleTapPowerEnabled(Resources paramResources)
  {
    return paramResources.getBoolean(17957038);
  }
  
  public static boolean isCameraDoubleTapPowerSettingEnabled(Context paramContext, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isCameraDoubleTapPowerEnabled(paramContext.getResources()))
    {
      bool1 = bool2;
      if (Settings.Secure.getIntForUser(paramContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0, paramInt) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isCameraLaunchEnabled(Resources paramResources)
  {
    if (paramResources.getInteger(17694884) != -1) {}
    for (int i = 1; (i == 0) || (SystemProperties.getBoolean("gesture.disable_camera_launch", false)); i = 0) {
      return false;
    }
    return true;
  }
  
  public static boolean isCameraLaunchSettingEnabled(Context paramContext, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isCameraLaunchEnabled(paramContext.getResources()))
    {
      bool1 = bool2;
      if (Settings.Secure.getIntForUser(paramContext.getContentResolver(), "camera_gesture_disabled", 0, paramInt) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isEmergencyAffordanceNeeded()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "emergency_affordance_needed", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmergencyOnpowerButtonTapEnabled(Resources paramResources)
  {
    if (!SystemProperties.getBoolean("persist.sys.ecall_pwr_key_press", false)) {
      return paramResources.getBoolean(17957073);
    }
    return true;
  }
  
  public static boolean isGestureLauncherEnabled(Resources paramResources)
  {
    if ((!isCameraLaunchEnabled(paramResources)) && (!isCameraDoubleTapPowerEnabled(paramResources))) {
      return isEmergencyOnpowerButtonTapEnabled(paramResources);
    }
    return true;
  }
  
  private void registerCameraLaunchGesture(Resources paramResources)
  {
    if (this.mRegistered) {
      return;
    }
    this.mCameraGestureOnTimeMs = SystemClock.elapsedRealtime();
    this.mCameraGestureLastEventTime = this.mCameraGestureOnTimeMs;
    SensorManager localSensorManager = (SensorManager)this.mContext.getSystemService("sensor");
    int i = paramResources.getInteger(17694884);
    if (i != -1)
    {
      this.mRegistered = false;
      paramResources = paramResources.getString(17039469);
      this.mCameraLaunchSensor = localSensorManager.getDefaultSensor(i, true);
      if (this.mCameraLaunchSensor != null)
      {
        if (!paramResources.equals(this.mCameraLaunchSensor.getStringType())) {
          break label109;
        }
        this.mRegistered = localSensorManager.registerListener(this.mGestureListener, this.mCameraLaunchSensor, 0);
      }
    }
    return;
    label109:
    throw new RuntimeException(String.format("Wrong configuration. Sensor type and sensor string type don't match: %s in resources, %s in the sensor.", new Object[] { paramResources, this.mCameraLaunchSensor.getStringType() }));
  }
  
  private void registerContentObservers()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("camera_gesture_disabled"), false, this.mSettingObserver, this.mUserId);
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("camera_double_tap_power_gesture_disabled"), false, this.mSettingObserver, this.mUserId);
  }
  
  private void unregisterCameraLaunchGesture()
  {
    if (this.mRegistered)
    {
      this.mRegistered = false;
      this.mCameraGestureOnTimeMs = 0L;
      this.mCameraGestureLastEventTime = 0L;
      this.mCameraGestureSensor1LastOnTimeMs = 0L;
      this.mCameraGestureSensor2LastOnTimeMs = 0L;
      this.mCameraLaunchLastEventExtra = 0;
      ((SensorManager)this.mContext.getSystemService("sensor")).unregisterListener(this.mGestureListener);
    }
  }
  
  private void updateCameraDoubleTapPowerEnabled()
  {
    boolean bool = isCameraDoubleTapPowerSettingEnabled(this.mContext, this.mUserId);
    try
    {
      this.mCameraDoubleTapPowerEnabled = bool;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void updateCameraRegistered()
  {
    Resources localResources = this.mContext.getResources();
    if (isCameraLaunchSettingEnabled(this.mContext, this.mUserId))
    {
      registerCameraLaunchGesture(localResources);
      return;
    }
    unregisterCameraLaunchGesture();
  }
  
  private void updateEmergencyCallTapPowerEnabled()
  {
    Resources localResources = this.mContext.getResources();
    this.mIsEmergencyOnPowerKeyTapEnabled = isEmergencyOnpowerButtonTapEnabled(localResources);
    this.mEmergencyNumber = localResources.getString(17039483);
    int i = localResources.getInteger(17694900);
    this.mDuration = (i * 400L);
    this.mHits = new long[i];
    Slog.d("GestureLauncherService", "Gesture launcher mEmergencyNumber = " + this.mEmergencyNumber + " hits = " + i);
  }
  
  public boolean interceptPowerKeyDown(KeyEvent paramKeyEvent, boolean paramBoolean, MutableBoolean paramMutableBoolean)
  {
    boolean bool3 = false;
    boolean bool4 = false;
    for (;;)
    {
      boolean bool1;
      try
      {
        long l = paramKeyEvent.getEventTime() - this.mLastPowerDown;
        this.mIsEmergencyOnPowerKeyTapEnabled = isEmergencyAffordanceNeeded();
        boolean bool2;
        if (this.mIsEmergencyOnPowerKeyTapEnabled)
        {
          System.arraycopy(this.mHits, 1, this.mHits, 0, this.mHits.length - 1);
          this.mHits[(this.mHits.length - 1)] = SystemClock.uptimeMillis();
          int i = 0;
          if (i < this.mHits.length)
          {
            Slog.i("GestureLauncherService", "mHits[" + i + "] = " + this.mHits[i] + "ms");
            i += 1;
            continue;
          }
          bool2 = bool4;
          bool1 = bool3;
          if (this.mHits[0] >= SystemClock.uptimeMillis() - this.mDuration)
          {
            bool1 = true;
            Arrays.fill(this.mHits, 0L);
            bool2 = paramBoolean;
          }
          this.mLastPowerDown = paramKeyEvent.getEventTime();
          paramBoolean = bool1;
          if (bool1)
          {
            if ((this.mIsEmergencyOnPowerKeyTapEnabled) && (!TextUtils.isEmpty(this.mEmergencyNumber))) {
              break label372;
            }
            Slog.i("GestureLauncherService", "Power button double tap gesture detected, launching camera. Interval=" + l + "ms");
            bool1 = handleCameraLaunchGesture(false, 1);
            paramBoolean = bool1;
            if (bool1)
            {
              MetricsLogger.action(this.mContext, 255, (int)l);
              paramBoolean = bool1;
            }
          }
          MetricsLogger.histogram(this.mContext, "power_double_tap_interval", (int)l);
          paramMutableBoolean.value = paramBoolean;
          if (!bool2) {
            break;
          }
          return paramBoolean;
        }
        else
        {
          boolean bool5 = this.mCameraDoubleTapPowerEnabled;
          bool2 = bool4;
          bool1 = bool3;
          if (!bool5) {
            continue;
          }
          bool2 = bool4;
          bool1 = bool3;
          if (l >= 300L) {
            continue;
          }
          bool2 = bool4;
          bool1 = bool3;
          if (l <= 120L) {
            continue;
          }
          bool1 = true;
          bool2 = paramBoolean;
          continue;
        }
        Slog.i("GestureLauncherService", "Power button Triple tap gesture detected, launching Emergency Call");
      }
      finally {}
      label372:
      paramKeyEvent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.fromParts("tel", this.mEmergencyNumber, null));
      paramKeyEvent.setFlags(268435456);
      getContext().startActivityAsUser(paramKeyEvent, UserHandle.CURRENT);
      paramBoolean = bool1;
    }
    return false;
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 600)
    {
      if (!isGestureLauncherEnabled(this.mContext.getResources())) {
        return;
      }
      this.mWakeLock = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, "GestureLauncherService");
      updateCameraRegistered();
      updateCameraDoubleTapPowerEnabled();
      updateEmergencyCallTapPowerEnabled();
      this.mUserId = ActivityManager.getCurrentUser();
      this.mContext.registerReceiver(this.mUserReceiver, new IntentFilter("android.intent.action.USER_SWITCHED"));
      registerContentObservers();
    }
  }
  
  public void onStart()
  {
    LocalServices.addService(GestureLauncherService.class, this);
  }
  
  private final class GestureEventListener
    implements SensorEventListener
  {
    private GestureEventListener() {}
    
    private void trackCameraLaunchEvent(SensorEvent paramSensorEvent)
    {
      long l1 = SystemClock.elapsedRealtime();
      long l3 = l1 - GestureLauncherService.-get1(GestureLauncherService.this);
      paramSensorEvent = paramSensorEvent.values;
      long l2 = (l3 * paramSensorEvent[0]);
      l3 = (l3 * paramSensorEvent[1]);
      int i = (int)paramSensorEvent[2];
      long l4 = l1 - GestureLauncherService.-get0(GestureLauncherService.this);
      long l5 = l2 - GestureLauncherService.-get2(GestureLauncherService.this);
      long l6 = l3 - GestureLauncherService.-get3(GestureLauncherService.this);
      int j = GestureLauncherService.-get4(GestureLauncherService.this);
      if ((l4 < 0L) || (l5 < 0L)) {}
      while (l6 < 0L) {
        return;
      }
      EventLogTags.writeCameraGestureTriggered(l4, l5, l6, i - j);
      GestureLauncherService.-set0(GestureLauncherService.this, l1);
      GestureLauncherService.-set1(GestureLauncherService.this, l2);
      GestureLauncherService.-set2(GestureLauncherService.this, l3);
      GestureLauncherService.-set3(GestureLauncherService.this, i);
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      if (!GestureLauncherService.-get7(GestureLauncherService.this)) {
        return;
      }
      if (paramSensorEvent.sensor == GestureLauncherService.-get5(GestureLauncherService.this))
      {
        if (GestureLauncherService.-wrap0(GestureLauncherService.this, true, 0))
        {
          MetricsLogger.action(GestureLauncherService.-get6(GestureLauncherService.this), 256);
          trackCameraLaunchEvent(paramSensorEvent);
        }
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/GestureLauncherService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */