package com.android.server.policy;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.ShortcutQuery;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.TorchCallback;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.android.server.LocalServices;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class DeviceKeyHandler
{
  private static final String ACTION_FRONT_CAMERA = "FrontCamera";
  private static final String ACTION_OPEN_APP = "OpenApp";
  private static final String ACTION_OPEN_CAMERA = "OpenCamera";
  private static final String ACTION_OPEN_SHELF = "OpenShelf";
  private static final String ACTION_OPEN_SHORTCUT = "OpenShortcut";
  private static final String ACTION_OPEN_TORCH = "OpenTorch";
  private static final String ACTION_TAKE_VIDEO = "TakeVideo";
  private static final String BLACK_ENBALE_PATH = "/proc/touchpanel/gesture_enable";
  private static final String BLACK_VALUE_PATH = "/proc/touchpanel/coordinate";
  private static final String CAMERA_ID = "0";
  private static final boolean DEBUG = SystemProperties.getBoolean("persist.sys.assert.panic", false);
  private static final String GESTURE_DOUBLE_TAP = "1";
  private static final String GESTURE_GTR_SCANCODE = "4";
  private static final String GESTURE_LTR_SCANCODE = "5";
  private static final String GESTURE_NEW_M_SCANCODE = "12";
  private static final String GESTURE_NEW_O_SCANCODE = "6";
  private static final String GESTURE_NEW_S_SCANCODE = "14";
  private static final String GESTURE_NEW_V_SCANCODE = "2";
  private static final String GESTURE_NEW_W_SCANCODE = "13";
  private static final String GESTURE_SWIPE_DOWN_SCANCODE = "7";
  private static final String GESTURE_SWITCH = "/proc/touchpanel/gesture_switch";
  private static final int GESTURE_WAKELOCK_DURATION = 3000;
  private static final int MAX_WAIT_TIME = 1000;
  private static final float PROXIMITY_THRESHOLD = 0.1F;
  private static final String TAG = "DeviceKeyHandler";
  private static final long VIBRATE_DURATION_LONG = 150L;
  private static final long VIBRATE_DURATION_SHORT = 75L;
  private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
  private boolean gesture_switch_exist = false;
  PowerManager.WakeLock mAcquireCauseWakeUpGestureWakeLock;
  private ActionInfo mActionInfo;
  private ActivityManager mActivityManager;
  private boolean mBlackEnableState = false;
  private int mBlackKeySettingState = 0;
  private CameraManager mCameraManager;
  private final Context mContext;
  private boolean mDoubleScreenOn = true;
  private EventHandler mEventHandler;
  private boolean mFlashlightEnabled;
  final HashMap<String, ActionInfo> mGestureMap = new HashMap();
  private Handler mHandler;
  private HandlerThread mHandlerThread;
  KeyguardServiceDelegate mKeyguardDelegate;
  LauncherApps mLauncherApps;
  private boolean mListenKeyguard = false;
  private boolean mMusic_control = true;
  private boolean mMusic_next = true;
  private boolean mMusic_pause = true;
  private boolean mMusic_play = true;
  private boolean mMusic_prev = true;
  private final Object mObject = new Object();
  private SettingsObserver mObserver;
  private final PackageManager mPackageManager;
  PowerManager.WakeLock mPartialGestureWakeLock;
  SensorEventListener mPocketListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      if (paramAnonymousSensorEvent.values[0] > 0.1F) {}
      for (final boolean bool = true;; bool = false)
      {
        DeviceKeyHandler.-get4(DeviceKeyHandler.this).postDelayed(new Runnable()
        {
          public void run()
          {
            if (bool)
            {
              Log.e("DeviceKeyHandler", "p-sensor near, disable gesture");
              FileUtils.writeIntLine("/proc/touchpanel/gesture_switch", 50);
              return;
            }
            Log.e("DeviceKeyHandler", "p-sensor far, enable gesture");
            FileUtils.writeIntLine("/proc/touchpanel/gesture_switch", 49);
          }
        }, 40L);
        return;
      }
    }
  };
  private final PowerManager mPowerManager;
  SensorEventListener mProximityListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      boolean bool = false;
      if (DeviceKeyHandler.-get0()) {
        Log.d("DeviceKeyHandler", "mProximityListener.onSensorChanged(): values[0]: " + paramAnonymousSensorEvent.values[0]);
      }
      synchronized (DeviceKeyHandler.-get5(DeviceKeyHandler.this))
      {
        float f = paramAnonymousSensorEvent.values[0];
        paramAnonymousSensorEvent = DeviceKeyHandler.this;
        if (f > 0.1F) {
          bool = true;
        }
        DeviceKeyHandler.-set1(paramAnonymousSensorEvent, bool);
        if (DeviceKeyHandler.-get0()) {
          Log.d("DeviceKeyHandler", "mProximityListener.onSensorChanged(): active = " + DeviceKeyHandler.-get6(DeviceKeyHandler.this));
        }
        DeviceKeyHandler.-get5(DeviceKeyHandler.this).notifyAll();
        return;
      }
    }
  };
  private Sensor mProximitySensor;
  private boolean mProximitySensorActive = true;
  private boolean mProximitySensorEnabled = false;
  PowerManager.WakeLock mProximityWakeLock;
  private boolean mSensorEnabled = false;
  private SensorManager mSensorManager;
  private boolean mSystemReady;
  private final CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback()
  {
    public void onTorchModeChanged(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      if (DeviceKeyHandler.-get0()) {
        Log.d("DeviceKeyHandler", "onTorchModeChanged(): enabled = " + paramAnonymousBoolean);
      }
      if (DeviceKeyHandler.-get3(DeviceKeyHandler.this) != paramAnonymousBoolean) {
        DeviceKeyHandler.-set0(DeviceKeyHandler.this, paramAnonymousBoolean);
      }
    }
    
    public void onTorchModeUnavailable(String paramAnonymousString)
    {
      DeviceKeyHandler.-set0(DeviceKeyHandler.this, false);
    }
  };
  private Vibrator mVibrator;
  
  public DeviceKeyHandler(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mEventHandler = new EventHandler(null);
    this.mSensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.mProximitySensor = this.mSensorManager.getDefaultSensor(33171025);
    this.mVibrator = ((Vibrator)paramContext.getSystemService("vibrator"));
    this.mActivityManager = ((ActivityManager)paramContext.getSystemService("activity"));
    this.mPackageManager = paramContext.getPackageManager();
    this.mProximityWakeLock = this.mPowerManager.newWakeLock(1, "ProximityWakeLock");
    this.mPartialGestureWakeLock = this.mPowerManager.newWakeLock(1, "PartialGestureWakeLock");
    this.mAcquireCauseWakeUpGestureWakeLock = this.mPowerManager.newWakeLock(268435457, "AcquireCauseWakeUpGestureWakeLock");
    this.mObserver = new SettingsObserver(this.mEventHandler);
    this.mCameraManager = ((CameraManager)paramContext.getSystemService("camera"));
    this.mHandlerThread = new HandlerThread("DeviceKeyHandler", 10);
    this.mHandlerThread.start();
    this.mHandler = new Handler(this.mHandlerThread.getLooper());
    registerCameraManagerCallbacks();
    this.gesture_switch_exist = new File("/proc/touchpanel/gesture_switch").exists();
  }
  
  private void SensorProcessMessage()
  {
    this.mProximityWakeLock.acquire();
    for (;;)
    {
      Message localMessage;
      synchronized (this.mObject)
      {
        if (this.gesture_switch_exist)
        {
          localMessage = this.mEventHandler.obtainMessage(1);
          this.mEventHandler.sendMessage(localMessage);
          this.mProximityWakeLock.release();
          return;
        }
        enableProximitySensor();
      }
      try
      {
        this.mObject.wait(1000L);
        if (!this.mProximitySensorActive)
        {
          if (DEBUG) {
            Log.e("DeviceKeyHandler", "SensorProcessMessage(): sensor value change.");
          }
          localMessage = this.mEventHandler.obtainMessage(1);
          this.mEventHandler.sendMessage(localMessage);
        }
        disableProximitySensor();
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
  }
  
  private void acquireGestureWakeLock(String paramString)
  {
    if ((this.mAcquireCauseWakeUpGestureWakeLock != null) && (this.mAcquireCauseWakeUpGestureWakeLock.isHeld())) {
      this.mAcquireCauseWakeUpGestureWakeLock.release();
    }
    if ((this.mPartialGestureWakeLock != null) && (this.mPartialGestureWakeLock.isHeld())) {
      this.mPartialGestureWakeLock.release();
    }
    if (isAWakeUpGesture(paramString)) {
      if (this.mAcquireCauseWakeUpGestureWakeLock != null) {
        this.mAcquireCauseWakeUpGestureWakeLock.acquire(3000L);
      }
    }
    while (this.mPartialGestureWakeLock == null) {
      return;
    }
    this.mPartialGestureWakeLock.acquire(3000L);
  }
  
  private void disableProximitySensor()
  {
    if (DEBUG) {
      Log.d("DeviceKeyHandler", "disableProximitySensor() called.");
    }
    long l;
    if (this.mProximitySensorEnabled) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.mSensorManager.unregisterListener(this.mProximityListener);
      this.mProximitySensorEnabled = false;
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void dispatchMediaKeyWithWakeLockToAudioService(int paramInt)
  {
    if (this.mSystemReady)
    {
      MediaSessionLegacyHelper localMediaSessionLegacyHelper = MediaSessionLegacyHelper.getHelper(this.mContext);
      if (localMediaSessionLegacyHelper == null) {
        break label53;
      }
      localKeyEvent = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, paramInt, 0);
      localMediaSessionLegacyHelper.sendMediaButtonEvent(localKeyEvent, true);
      localMediaSessionLegacyHelper.sendMediaButtonEvent(KeyEvent.changeAction(localKeyEvent, 1), true);
    }
    label53:
    while (!DEBUG)
    {
      KeyEvent localKeyEvent;
      return;
    }
    Log.w("DeviceKeyHandler", "MediaSessionLegacyHelper instance is null.");
  }
  
  private void enableProximitySensor()
  {
    if (DEBUG) {
      Log.d("DeviceKeyHandler", "enableProximitySensor() called.");
    }
    long l;
    if (!this.mProximitySensorEnabled) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.mSensorManager.registerListener(this.mProximityListener, this.mProximitySensor, 0);
      this.mProximitySensorEnabled = true;
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private String getCameraId()
    throws CameraAccessException
  {
    String[] arrayOfString = this.mCameraManager.getCameraIdList();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      try
      {
        Object localObject = this.mCameraManager.getCameraCharacteristics(str);
        Boolean localBoolean = (Boolean)((CameraCharacteristics)localObject).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        localObject = (Integer)((CameraCharacteristics)localObject).get(CameraCharacteristics.LENS_FACING);
        if ((localBoolean != null) && (localBoolean.booleanValue()) && (localObject != null))
        {
          int k = ((Integer)localObject).intValue();
          if (k == 1) {
            return str;
          }
        }
      }
      catch (NullPointerException localNullPointerException)
      {
        Log.e("DeviceKeyHandler", "Couldn't get torch mode characteristics.", localNullPointerException);
        return null;
      }
      i += 1;
    }
    return null;
  }
  
  private int getCameraType(String paramString)
  {
    if (paramString.equals("OpenCamera")) {
      return 268435712;
    }
    if (paramString.equals("FrontCamera")) {
      return 268435968;
    }
    if (paramString.equals("TakeVideo")) {
      return 268436480;
    }
    return 268435712;
  }
  
  private String getDefaultHomePackageName(Context paramContext)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
    paramContext = paramContext.getPackageManager();
    if (paramContext == null)
    {
      Log.e("DeviceKeyHandler", "getDefaultHomePackageName: could not get package manager");
      return "";
    }
    paramContext = paramContext.resolveActivity(localIntent, 128);
    if (paramContext == null)
    {
      Log.e("DeviceKeyHandler", "getDefaultHomePackageName: could not get ResolveInfo");
      return "";
    }
    Log.d("DeviceKeyHandler", "[isDefaultHome] default home: " + paramContext.activityInfo);
    if (paramContext.activityInfo != null) {
      return paramContext.activityInfo.packageName;
    }
    return "";
  }
  
  public static int getOffset(int paramInt1, int paramInt2)
  {
    return (1 << paramInt2 & paramInt1) >> paramInt2;
  }
  
  private boolean isAWakeUpGesture(String paramString)
  {
    if (paramString.equals("1")) {}
    while ((paramString.equals("OpenCamera")) || (paramString.equals("FrontCamera")) || (paramString.equals("TakeVideo")) || (paramString.equals("OpenShelf")) || (paramString.equals("OpenApp")) || (paramString.equals("OpenShortcut"))) {
      return true;
    }
    return false;
  }
  
  private void performVibration()
  {
    performVibration(true);
  }
  
  private void performVibration(boolean paramBoolean)
  {
    Vibrator localVibrator;
    if (this.mVibrator != null)
    {
      localVibrator = this.mVibrator;
      if (!paramBoolean) {
        break label31;
      }
    }
    label31:
    for (long l = 75L;; l = 150L)
    {
      localVibrator.vibrate(l, VIBRATION_ATTRIBUTES);
      return;
    }
  }
  
  private void processKeyEvent()
  {
    boolean bool1 = false;
    Object localObject = FileUtils.readOneLine("/proc/touchpanel/coordinate");
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return;
    }
    localObject = ((String)localObject).substring(0, ((String)localObject).indexOf(',', 0));
    Log.e("DeviceKeyHandler", "Receive gesture " + (String)localObject);
    String str;
    if (((String)localObject).equals("7"))
    {
      if (this.mMusic_pause)
      {
        acquireGestureWakeLock((String)localObject);
        performVibration();
        dispatchMediaKeyWithWakeLockToAudioService(85);
      }
      localObject = (ActionInfo)this.mGestureMap.get(localObject);
      if (localObject != null)
      {
        Log.e("DeviceKeyHandler", "Corresponding action is " + ((ActionInfo)localObject).toString());
        str = ((ActionInfo)localObject).getAction();
        if (!str.equals("OpenTorch")) {
          break label276;
        }
        acquireGestureWakeLock(str);
        if (!this.mFlashlightEnabled) {
          break label448;
        }
        performVibration(setFlashlight(bool1));
      }
    }
    label169:
    label276:
    label347:
    label448:
    label453:
    label461:
    for (;;)
    {
      return;
      if (((String)localObject).equals("5"))
      {
        if (!this.mMusic_prev) {
          break;
        }
        acquireGestureWakeLock((String)localObject);
        performVibration();
        dispatchMediaKeyWithWakeLockToAudioService(88);
        break;
      }
      if (((String)localObject).equals("4"))
      {
        if (!this.mMusic_next) {
          break;
        }
        acquireGestureWakeLock((String)localObject);
        performVibration();
        dispatchMediaKeyWithWakeLockToAudioService(87);
        break;
      }
      if ((!((String)localObject).equals("1")) || (!this.mDoubleScreenOn)) {
        break;
      }
      acquireGestureWakeLock((String)localObject);
      performVibration();
      this.mPowerManager.wakeUp(SystemClock.uptimeMillis());
      break;
      if (str.equals("OpenCamera")) {}
      while ((str.equals("FrontCamera")) || (str.equals("TakeVideo")))
      {
        acquireGestureWakeLock(str);
        performVibration();
        ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class)).onCameraLaunchGestureDetected(getCameraType(str));
        return;
      }
      boolean bool2;
      if (str.equals("OpenShelf"))
      {
        acquireGestureWakeLock(str);
        if (this.mKeyguardDelegate == null) {
          continue;
        }
        if (!this.mKeyguardDelegate.isSecure(0)) {
          break label453;
        }
        bool2 = startWithKeyguardUnlocked((ActionInfo)localObject, true);
        bool1 = bool2;
        if (bool2) {
          this.mActionInfo = ((ActionInfo)localObject);
        }
      }
      for (bool1 = bool2;; bool1 = startWithKeyguardUnlocked((ActionInfo)localObject, false))
      {
        if (!bool1) {
          break label461;
        }
        performVibration();
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis());
        if (this.mKeyguardDelegate == null) {
          break label169;
        }
        this.mKeyguardDelegate.forceDismiss(true);
        return;
        if (str.equals("OpenApp")) {
          break label347;
        }
        if (!str.equals("OpenShortcut")) {
          break label169;
        }
        break label347;
        bool1 = true;
        break;
      }
    }
  }
  
  private boolean startApp(String paramString, boolean paramBoolean)
  {
    Intent localIntent = this.mPackageManager.getLaunchIntentForPackage(paramString);
    if (localIntent != null)
    {
      if (paramBoolean) {
        return true;
      }
      this.mContext.startActivityAsUser(localIntent, UserHandle.OWNER);
      return true;
    }
    Log.e("DeviceKeyHandler", "start app " + paramString + " failed because intent is null");
    return false;
  }
  
  private boolean startShelf(boolean paramBoolean)
  {
    if (paramBoolean) {
      return true;
    }
    if ("net.oneplus.h2launcher".equals(getDefaultHomePackageName(this.mContext))) {}
    for (Intent localIntent = new Intent("net.oneplus.h2launcher.action.OPEN_QUICK_PAGE");; localIntent = new Intent("net.oneplus.launcher.action.OPEN_QUICK_PAGE"))
    {
      localIntent.addFlags(268435456);
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.OWNER);
      return true;
    }
  }
  
  private boolean startShortcut(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.mLauncherApps = ((LauncherApps)this.mContext.getSystemService("launcherapps"));
    if (this.mLauncherApps != null) {
      if (paramBoolean)
      {
        LauncherApps.ShortcutQuery localShortcutQuery = new LauncherApps.ShortcutQuery();
        localShortcutQuery.setPackage(paramString1);
        localShortcutQuery.setShortcutIds(Arrays.asList(new String[] { paramString2 }));
        try
        {
          paramString1 = this.mLauncherApps.getShortcuts(localShortcutQuery, UserHandle.OWNER);
          if (paramString1 == null) {
            break label120;
          }
          return true;
        }
        catch (IllegalStateException paramString1)
        {
          Log.e("DeviceKeyHandler", "get shortcuts failed");
          return false;
        }
      }
      else
      {
        try
        {
          this.mLauncherApps.startShortcut(paramString1, paramString2, null, null, UserHandle.OWNER);
          return true;
        }
        catch (ActivityNotFoundException paramString1)
        {
          Log.e("DeviceKeyHandler", "start shortcut failed");
          return false;
        }
      }
    }
    label120:
    Log.e("DeviceKeyHandler", "shortcut service is null");
    return false;
  }
  
  private void updateOemSettings()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    this.mBlackKeySettingState = Settings.System.getIntForUser(localContentResolver, "oem_acc_blackscreen_gestrue_enable", 0, -2);
    if (DEBUG) {
      Log.d("DeviceKeyHandler", "updateH2OemSettings(): mBlackKeySettingState = " + Integer.toHexString(this.mBlackKeySettingState));
    }
    if (getOffset(this.mBlackKeySettingState, 7) == 1)
    {
      bool = true;
      this.mDoubleScreenOn = bool;
      if (getOffset(this.mBlackKeySettingState, 5) != 1) {
        break label234;
      }
      bool = true;
      label94:
      this.mMusic_control = bool;
      if (getOffset(this.mBlackKeySettingState, 4) != 1) {
        break label239;
      }
      bool = true;
      label113:
      this.mMusic_prev = bool;
      if (getOffset(this.mBlackKeySettingState, 3) != 1) {
        break label244;
      }
      bool = true;
      label132:
      this.mMusic_next = bool;
      if (getOffset(this.mBlackKeySettingState, 2) != 1) {
        break label249;
      }
      bool = true;
      label151:
      this.mMusic_pause = bool;
      if (getOffset(this.mBlackKeySettingState, 1) != 1) {
        break label254;
      }
    }
    label234:
    label239:
    label244:
    label249:
    label254:
    for (boolean bool = true;; bool = false)
    {
      this.mMusic_play = bool;
      if (this.mMusic_play) {
        this.mMusic_pause = true;
      }
      makeGestureMap(localContentResolver);
      FileUtils.writeByteArray("/proc/touchpanel/gesture_enable", new byte[] { (byte)(this.mBlackKeySettingState & 0xFF), (byte)(this.mBlackKeySettingState >> 8 & 0xFF) });
      return;
      bool = false;
      break;
      bool = false;
      break label94;
      bool = false;
      break label113;
      bool = false;
      break label132;
      bool = false;
      break label151;
    }
  }
  
  public boolean handleKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    int j = paramKeyEvent.getRepeatCount();
    if (paramKeyEvent.getAction() == 1) {}
    boolean bool1;
    for (int i = 1;; i = 0)
    {
      bool1 = bool2;
      if (i != 0)
      {
        bool1 = bool2;
        if (j == 0) {
          bool1 = true;
        }
      }
      if (bool1)
      {
        paramKeyEvent = this.mEventHandler.obtainMessage(1);
        if (this.mProximitySensor == null) {
          break;
        }
        SensorProcessMessage();
      }
      return bool1;
    }
    this.mEventHandler.sendMessage(paramKeyEvent);
    return bool1;
  }
  
  void makeGestureMap(ContentResolver paramContentResolver)
  {
    String str1 = Settings.System.getStringForUser(paramContentResolver, "oem_acc_blackscreen_gesture_o", -2);
    String str2 = Settings.System.getStringForUser(paramContentResolver, "oem_acc_blackscreen_gesture_v", -2);
    String str3 = Settings.System.getStringForUser(paramContentResolver, "oem_acc_blackscreen_gesture_s", -2);
    String str4 = Settings.System.getStringForUser(paramContentResolver, "oem_acc_blackscreen_gesture_w", -2);
    paramContentResolver = Settings.System.getStringForUser(paramContentResolver, "oem_acc_blackscreen_gesture_m", -2);
    parseSettingData("6", str1);
    parseSettingData("2", str2);
    parseSettingData("14", str3);
    parseSettingData("13", str4);
    parseSettingData("12", paramContentResolver);
  }
  
  public void onKeyguardDone()
  {
    Log.e("DeviceKeyHandler", "receive keyguard done, process gesture action");
    if (this.mActionInfo != null)
    {
      startWithKeyguardUnlocked(this.mActionInfo, false);
      this.mActionInfo = null;
    }
  }
  
  void onScreenTurnedOff()
  {
    if ((!this.gesture_switch_exist) || (this.mBlackKeySettingState == 0) || (this.mSensorEnabled)) {}
    for (;;)
    {
      this.mActionInfo = null;
      return;
      this.mSensorEnabled = true;
      this.mSensorManager.registerListener(this.mPocketListener, this.mProximitySensor, 0);
    }
  }
  
  void onScreenTurnedOn()
  {
    if (this.mSensorEnabled)
    {
      this.mSensorEnabled = false;
      this.mSensorManager.unregisterListener(this.mPocketListener);
    }
  }
  
  void parseSettingData(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    String[] arrayOfString = new String[3];
    paramString2 = paramString2.split(":|;");
    System.arraycopy(paramString2, 0, arrayOfString, 0, paramString2.length);
    paramString2 = new ActionInfo(null);
    paramString2.setActionName(arrayOfString[0]);
    paramString2.setPackage(arrayOfString[1]);
    paramString2.setShortcutId(arrayOfString[2]);
    this.mGestureMap.put(paramString1, paramString2);
  }
  
  public void registerCameraManagerCallbacks()
  {
    if (DEBUG) {
      Log.d("DeviceKeyHandler", "registerCameraManagerCallbacks() called.");
    }
    this.mCameraManager.registerTorchCallback(this.mTorchCallback, this.mHandler);
  }
  
  public boolean setFlashlight(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("DeviceKeyHandler", "setTorchMode() called: " + paramBoolean);
    }
    try
    {
      if (this.mFlashlightEnabled != paramBoolean)
      {
        this.mFlashlightEnabled = paramBoolean;
        try
        {
          String str = getCameraId();
          CameraManager localCameraManager = this.mCameraManager;
          if (str != null) {}
          for (;;)
          {
            localCameraManager.setTorchMode(str, paramBoolean);
            return true;
            str = "0";
          }
        }
        catch (CameraAccessException localCameraAccessException)
        {
          Log.e("DeviceKeyHandler", "CameraAccessException: Couldn't set torch mode.", localCameraAccessException);
          this.mFlashlightEnabled = false;
          return false;
        }
      }
      return false;
    }
    finally {}
  }
  
  void setKeyguardDelegate(KeyguardServiceDelegate paramKeyguardServiceDelegate)
  {
    this.mKeyguardDelegate = paramKeyguardServiceDelegate;
  }
  
  boolean startWithKeyguardUnlocked(ActionInfo paramActionInfo, boolean paramBoolean)
  {
    String str = paramActionInfo.getAction();
    boolean bool = false;
    if (str.equals("OpenShelf")) {
      bool = startShelf(paramBoolean);
    }
    do
    {
      return bool;
      if (str.equals("OpenApp")) {
        return startApp(paramActionInfo.getPackage(), paramBoolean);
      }
    } while (!str.equals("OpenShortcut"));
    return startShortcut(paramActionInfo.getPackage(), paramActionInfo.getShortcutId(), paramBoolean);
  }
  
  public void systemReady()
  {
    this.mSystemReady = true;
    this.mObserver.observe();
    try
    {
      localPackageInfo = this.mContext.getPackageManager().getPackageInfo("com.netease.cloudmusic", 0);
      if (localPackageInfo == null) {}
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        try
        {
          AppGlobals.getPackageManager().setPackageStoppedState("com.netease.cloudmusic", false, 0);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          PackageInfo localPackageInfo;
          localRemoteException.printStackTrace();
        }
        localNameNotFoundException = localNameNotFoundException;
        localPackageInfo = null;
        localNameNotFoundException.printStackTrace();
      }
    }
  }
  
  private class ActionInfo
  {
    String mActionName;
    String mPackageName;
    String mShortcutId;
    
    private ActionInfo() {}
    
    public String getAction()
    {
      return this.mActionName;
    }
    
    public String getPackage()
    {
      return this.mPackageName;
    }
    
    public String getShortcutId()
    {
      return this.mShortcutId;
    }
    
    public void setActionName(String paramString)
    {
      this.mActionName = paramString;
    }
    
    public void setPackage(String paramString)
    {
      this.mPackageName = paramString;
    }
    
    public void setShortcutId(String paramString)
    {
      this.mShortcutId = paramString;
    }
    
    public String toString()
    {
      return "Name:" + this.mActionName + " Package:" + this.mPackageName + " ShortcutId:" + this.mShortcutId;
    }
  }
  
  private class EventHandler
    extends Handler
  {
    static final int MSG_KEY_EVENT = 1;
    
    private EventHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      DeviceKeyHandler.-get1(DeviceKeyHandler.this);
      if (ActivityManager.getCurrentUser() != 0) {
        return;
      }
      DeviceKeyHandler.-wrap0(DeviceKeyHandler.this);
    }
  }
  
  class SettingsObserver
    extends ContentObserver
  {
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    void observe()
    {
      ContentResolver localContentResolver = DeviceKeyHandler.-get2(DeviceKeyHandler.this).getContentResolver();
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gestrue_enable"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gesture_o"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gesture_v"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gesture_s"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gesture_w"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("oem_acc_blackscreen_gesture_m"), false, this, -1);
      DeviceKeyHandler.-wrap1(DeviceKeyHandler.this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      DeviceKeyHandler.-wrap1(DeviceKeyHandler.this);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
    {
      super.onChange(paramBoolean, paramUri, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/DeviceKeyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */