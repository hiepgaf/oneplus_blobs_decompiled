package com.android.server.retaildemo;

import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RetailDemoModeServiceInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.CallLog.Calls;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.KeyValueListParser;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.BackgroundThread;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.am.ActivityManagerService;
import java.io.File;
import java.util.ArrayList;

public class RetailDemoModeService
  extends SystemService
{
  private static final String ACTION_RESET_DEMO = "com.android.server.retaildemo.ACTION_RESET_DEMO";
  private static final boolean DEBUG = false;
  private static final String DEMO_SESSION_COUNT = "retail_demo_session_count";
  private static final String DEMO_SESSION_DURATION = "retail_demo_session_duration";
  private static final String DEMO_USER_NAME = "Demo";
  private static final long MILLIS_PER_SECOND = 1000L;
  private static final int MSG_INACTIVITY_TIME_OUT = 1;
  private static final int MSG_START_NEW_SESSION = 2;
  private static final int MSG_TURN_SCREEN_ON = 0;
  private static final long SCREEN_WAKEUP_DELAY = 2500L;
  private static final String SYSTEM_PROPERTY_RETAIL_DEMO_ENABLED = "sys.retaildemo.enabled";
  private static final String TAG = RetailDemoModeService.class.getSimpleName();
  private static final long USER_INACTIVITY_TIMEOUT_DEFAULT = 90000L;
  private static final long USER_INACTIVITY_TIMEOUT_MIN = 10000L;
  private static final int[] VOLUME_STREAMS_TO_MUTE = { 2, 3 };
  private static final long WARNING_DIALOG_TIMEOUT_DEFAULT = 0L;
  final Object mActivityLock = new Object();
  private ActivityManagerInternal mAmi;
  private ActivityManagerService mAms;
  private AudioManager mAudioManager;
  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!RetailDemoModeService.this.mDeviceInDemoMode) {
        return;
      }
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext.equals("android.intent.action.SCREEN_OFF"))
      {
        RetailDemoModeService.this.mHandler.removeMessages(0);
        RetailDemoModeService.this.mHandler.sendEmptyMessageDelayed(0, 2500L);
      }
      while (!paramAnonymousContext.equals("com.android.server.retaildemo.ACTION_RESET_DEMO")) {
        return;
      }
      RetailDemoModeService.this.mHandler.sendEmptyMessage(2);
    }
  };
  private String[] mCameraIdsWithFlash;
  private CameraManager mCameraManager;
  int mCurrentUserId = 0;
  boolean mDeviceInDemoMode = false;
  @GuardedBy("mActivityLock")
  long mFirstUserActivityTime;
  Handler mHandler;
  private ServiceThread mHandlerThread;
  @GuardedBy("mActivityLock")
  long mLastUserActivityTime;
  private RetailDemoModeServiceInternal mLocalService = new RetailDemoModeServiceInternal()
  {
    private static final long USER_ACTIVITY_DEBOUNCE_TIME = 2000L;
    
    public void onUserActivity()
    {
      if (!RetailDemoModeService.this.mDeviceInDemoMode) {
        return;
      }
      long l1 = SystemClock.uptimeMillis();
      synchronized (RetailDemoModeService.this.mActivityLock)
      {
        long l2 = RetailDemoModeService.this.mLastUserActivityTime;
        if (l1 < l2 + 2000L) {
          return;
        }
        RetailDemoModeService.this.mLastUserActivityTime = l1;
        if ((RetailDemoModeService.this.mUserUntouched) && (RetailDemoModeService.this.isDemoLauncherDisabled()))
        {
          Slog.d(RetailDemoModeService.-get0(), "retail_demo first touch");
          RetailDemoModeService.this.mUserUntouched = false;
          RetailDemoModeService.this.mFirstUserActivityTime = l1;
        }
        RetailDemoModeService.this.mHandler.removeMessages(1);
        RetailDemoModeService.this.mHandler.sendEmptyMessageDelayed(1, RetailDemoModeService.this.mUserInactivityTimeout);
        return;
      }
    }
  };
  private NotificationManager mNm;
  private PowerManager mPm;
  private PreloadAppsInstaller mPreloadAppsInstaller;
  private PendingIntent mResetDemoPendingIntent;
  private Configuration mSystemUserConfiguration;
  private UserManager mUm;
  long mUserInactivityTimeout;
  @GuardedBy("mActivityLock")
  boolean mUserUntouched;
  private PowerManager.WakeLock mWakeLock;
  long mWarningDialogTimeout;
  private WifiManager mWifiManager;
  
  public RetailDemoModeService(Context arg1)
  {
    super(???);
    synchronized (this.mActivityLock)
    {
      long l = SystemClock.uptimeMillis();
      this.mLastUserActivityTime = l;
      this.mFirstUserActivityTime = l;
      return;
    }
  }
  
  private void clearPrimaryCallLog()
  {
    ContentResolver localContentResolver = getContext().getContentResolver();
    Uri localUri = CallLog.Calls.CONTENT_URI;
    try
    {
      localContentResolver.delete(localUri, null, null);
      return;
    }
    catch (Exception localException)
    {
      Slog.w(TAG, "Deleting call log failed: " + localException);
    }
  }
  
  private Notification createResetNotification()
  {
    return new Notification.Builder(getContext()).setContentTitle(getContext().getString(17040908)).setContentText(getContext().getString(17040909)).setOngoing(true).setSmallIcon(17302877).setShowWhen(false).setVisibility(1).setContentIntent(getResetDemoPendingIntent()).setColor(getContext().getColor(17170523)).build();
  }
  
  private boolean deletePreloadsFolderContents()
  {
    File localFile = Environment.getDataPreloadsDirectory();
    Slog.i(TAG, "Deleting contents of " + localFile);
    return FileUtils.deleteContents(localFile);
  }
  
  private ActivityManagerService getActivityManager()
  {
    if (this.mAms == null) {
      this.mAms = ((ActivityManagerService)ActivityManagerNative.getDefault());
    }
    return this.mAms;
  }
  
  private AudioManager getAudioManager()
  {
    if (this.mAudioManager == null) {
      this.mAudioManager = ((AudioManager)getContext().getSystemService(AudioManager.class));
    }
    return this.mAudioManager;
  }
  
  private String[] getCameraIdsWithFlash()
  {
    localArrayList = new ArrayList();
    try
    {
      String[] arrayOfString = this.mCameraManager.getCameraIdList();
      int i = 0;
      int j = arrayOfString.length;
      while (i < j)
      {
        String str = arrayOfString[i];
        CameraCharacteristics localCameraCharacteristics = this.mCameraManager.getCameraCharacteristics(str);
        if (Boolean.TRUE.equals(localCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) {
          localArrayList.add(str);
        }
        i += 1;
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    catch (CameraAccessException localCameraAccessException)
    {
      Slog.e(TAG, "Unable to access camera while getting camera id list", localCameraAccessException);
    }
  }
  
  private PendingIntent getResetDemoPendingIntent()
  {
    if (this.mResetDemoPendingIntent == null)
    {
      Intent localIntent = new Intent("com.android.server.retaildemo.ACTION_RESET_DEMO");
      this.mResetDemoPendingIntent = PendingIntent.getBroadcast(getContext(), 0, localIntent, 0);
    }
    return this.mResetDemoPendingIntent;
  }
  
  private Configuration getSystemUsersConfiguration()
  {
    if (this.mSystemUserConfiguration == null)
    {
      ContentResolver localContentResolver = getContext().getContentResolver();
      Configuration localConfiguration = new Configuration();
      this.mSystemUserConfiguration = localConfiguration;
      Settings.System.getConfiguration(localContentResolver, localConfiguration);
    }
    return this.mSystemUserConfiguration;
  }
  
  private UserManager getUserManager()
  {
    if (this.mUm == null) {
      this.mUm = ((UserManager)getContext().getSystemService(UserManager.class));
    }
    return this.mUm;
  }
  
  private void grantRuntimePermissionToCamera(UserHandle paramUserHandle)
  {
    Object localObject = new Intent("android.media.action.IMAGE_CAPTURE");
    PackageManager localPackageManager = getContext().getPackageManager();
    localObject = localPackageManager.resolveActivityAsUser((Intent)localObject, 786432, paramUserHandle.getIdentifier());
    if ((localObject == null) || (((ResolveInfo)localObject).activityInfo == null)) {
      return;
    }
    try
    {
      localPackageManager.grantRuntimePermission(((ResolveInfo)localObject).activityInfo.packageName, "android.permission.ACCESS_FINE_LOCATION", paramUserHandle);
      return;
    }
    catch (Exception paramUserHandle) {}
  }
  
  private boolean isDeviceProvisioned()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getContext().getContentResolver(), "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void muteVolumeStreams()
  {
    int[] arrayOfInt = VOLUME_STREAMS_TO_MUTE;
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      int k = arrayOfInt[i];
      getAudioManager().setStreamVolume(k, getAudioManager().getStreamMinVolume(k), 0);
      i += 1;
    }
  }
  
  private void putDeviceInDemoMode()
  {
    SystemProperties.set("sys.retaildemo.enabled", "1");
    this.mHandler.sendEmptyMessage(2);
  }
  
  private void registerBroadcastReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("com.android.server.retaildemo.ACTION_RESET_DEMO");
    getContext().registerReceiver(this.mBroadcastReceiver, localIntentFilter);
  }
  
  private void setupDemoUser(UserInfo paramUserInfo)
  {
    UserManager localUserManager = getUserManager();
    UserHandle localUserHandle = UserHandle.of(paramUserInfo.id);
    localUserManager.setUserRestriction("no_config_wifi", true, localUserHandle);
    localUserManager.setUserRestriction("no_install_unknown_sources", true, localUserHandle);
    localUserManager.setUserRestriction("no_config_mobile_networks", true, localUserHandle);
    localUserManager.setUserRestriction("no_usb_file_transfer", true, localUserHandle);
    localUserManager.setUserRestriction("no_modify_accounts", true, localUserHandle);
    localUserManager.setUserRestriction("no_config_bluetooth", true, localUserHandle);
    localUserManager.setUserRestriction("no_outgoing_calls", false, localUserHandle);
    getUserManager().setUserRestriction("no_safe_boot", true, UserHandle.SYSTEM);
    Settings.Secure.putIntForUser(getContext().getContentResolver(), "skip_first_use_hints", 1, paramUserInfo.id);
    Settings.Global.putInt(getContext().getContentResolver(), "package_verifier_enable", 0);
    grantRuntimePermissionToCamera(localUserHandle);
    clearPrimaryCallLog();
  }
  
  private void showInactivityCountdownDialog()
  {
    UserInactivityCountdownDialog localUserInactivityCountdownDialog = new UserInactivityCountdownDialog(getContext(), this.mWarningDialogTimeout, 1000L);
    localUserInactivityCountdownDialog.setNegativeButtonClickListener(null);
    localUserInactivityCountdownDialog.setPositiveButtonClickListener(new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        RetailDemoModeService.this.mHandler.sendEmptyMessage(2);
      }
    });
    localUserInactivityCountdownDialog.setOnCountDownExpiredListener(new UserInactivityCountdownDialog.OnCountDownExpiredListener()
    {
      public void onCountDownExpired()
      {
        RetailDemoModeService.this.mHandler.sendEmptyMessage(2);
      }
    });
    localUserInactivityCountdownDialog.show();
  }
  
  private void turnOffAllFlashLights()
  {
    int i = 0;
    String[] arrayOfString = this.mCameraIdsWithFlash;
    int j = arrayOfString.length;
    for (;;)
    {
      if (i < j)
      {
        String str = arrayOfString[i];
        try
        {
          this.mCameraManager.setTorchMode(str, false);
          i += 1;
        }
        catch (CameraAccessException localCameraAccessException)
        {
          for (;;)
          {
            Slog.e(TAG, "Unable to access camera " + str + " while turning off flash", localCameraAccessException);
          }
        }
      }
    }
  }
  
  boolean isDemoLauncherDisabled()
  {
    IPackageManager localIPackageManager = AppGlobals.getPackageManager();
    int i = 0;
    String str = getContext().getResources().getString(17039484);
    try
    {
      int j = localIPackageManager.getComponentEnabledSetting(ComponentName.unflattenFromString(str), this.mCurrentUserId);
      i = j;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e(TAG, "Unable to talk to Package Manager", localRemoteException);
      }
    }
    return i == 2;
  }
  
  void logSessionDuration()
  {
    synchronized (this.mActivityLock)
    {
      long l = (this.mLastUserActivityTime - this.mFirstUserActivityTime) / 1000L;
      int i = (int)l;
      MetricsLogger.histogram(getContext(), "retail_demo_session_duration", i);
      return;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    switch (paramInt)
    {
    }
    do
    {
      return;
      this.mPreloadAppsInstaller = new PreloadAppsInstaller(getContext());
      this.mPm = ((PowerManager)getContext().getSystemService("power"));
      this.mAmi = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class));
      this.mWakeLock = this.mPm.newWakeLock(268435482, TAG);
      this.mNm = NotificationManager.from(getContext());
      this.mWifiManager = ((WifiManager)getContext().getSystemService("wifi"));
      this.mCameraManager = ((CameraManager)getContext().getSystemService("camera"));
      this.mCameraIdsWithFlash = getCameraIdsWithFlash();
      SettingsObserver localSettingsObserver = new SettingsObserver(this.mHandler);
      localSettingsObserver.register();
      SettingsObserver.-wrap0(localSettingsObserver);
      registerBroadcastReceiver();
      return;
    } while (!UserManager.isDeviceInDemoMode(getContext()));
    this.mDeviceInDemoMode = true;
    putDeviceInDemoMode();
  }
  
  public void onStart()
  {
    this.mHandlerThread = new ServiceThread(TAG, -2, false);
    this.mHandlerThread.start();
    this.mHandler = new MainHandler(this.mHandlerThread.getLooper());
    publishLocalService(RetailDemoModeServiceInternal.class, this.mLocalService);
  }
  
  public void onSwitchUser(final int paramInt)
  {
    if (!this.mDeviceInDemoMode) {
      return;
    }
    if (!getUserManager().getUserInfo(paramInt).isDemo())
    {
      Slog.wtf(TAG, "Should not allow switch to non-demo user in demo mode");
      return;
    }
    if (!this.mWakeLock.isHeld()) {
      this.mWakeLock.acquire();
    }
    this.mCurrentUserId = paramInt;
    this.mAmi.updatePersistentConfigurationForUser(getSystemUsersConfiguration(), paramInt);
    turnOffAllFlashLights();
    muteVolumeStreams();
    if (!this.mWifiManager.isWifiEnabled()) {
      this.mWifiManager.setWifiEnabled(true);
    }
    new LockPatternUtils(getContext()).setLockScreenDisabled(true, paramInt);
    this.mNm.notifyAsUser(TAG, 1, createResetNotification(), UserHandle.of(paramInt));
    synchronized (this.mActivityLock)
    {
      this.mUserUntouched = true;
      MetricsLogger.count(getContext(), "retail_demo_session_count", 1);
      this.mHandler.removeMessages(1);
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          RetailDemoModeService.-get1(RetailDemoModeService.this).installApps(paramInt);
        }
      });
      return;
    }
  }
  
  final class MainHandler
    extends Handler
  {
    MainHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      do
      {
        do
        {
          return;
          if (RetailDemoModeService.-get2(RetailDemoModeService.this).isHeld()) {
            RetailDemoModeService.-get2(RetailDemoModeService.this).release();
          }
          RetailDemoModeService.-get2(RetailDemoModeService.this).acquire();
          return;
        } while (!RetailDemoModeService.this.isDemoLauncherDisabled());
        Slog.i(RetailDemoModeService.-get0(), "User inactivity timeout reached");
        RetailDemoModeService.-wrap6(RetailDemoModeService.this);
        return;
        removeMessages(2);
        removeMessages(1);
        if (RetailDemoModeService.this.mCurrentUserId != 0) {
          RetailDemoModeService.this.logSessionDuration();
        }
        paramMessage = RetailDemoModeService.-wrap0(RetailDemoModeService.this).createUser("Demo", 768);
      } while (paramMessage == null);
      RetailDemoModeService.-wrap5(RetailDemoModeService.this, paramMessage);
      RetailDemoModeService.-wrap3(RetailDemoModeService.this).switchUser(paramMessage.id);
    }
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    private static final String KEY_USER_INACTIVITY_TIMEOUT = "user_inactivity_timeout_ms";
    private static final String KEY_WARNING_DIALOG_TIMEOUT = "warning_dialog_timeout_ms";
    private final Uri mDeviceDemoModeUri = Settings.Global.getUriFor("device_demo_mode");
    private final Uri mDeviceProvisionedUri = Settings.Global.getUriFor("device_provisioned");
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private final Uri mRetailDemoConstantsUri = Settings.Global.getUriFor("retail_demo_mode_constants");
    
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    private void refreshTimeoutConstants()
    {
      try
      {
        this.mParser.setString(Settings.Global.getString(RetailDemoModeService.this.getContext().getContentResolver(), "retail_demo_mode_constants"));
        RetailDemoModeService.this.mWarningDialogTimeout = this.mParser.getLong("warning_dialog_timeout_ms", 0L);
        RetailDemoModeService.this.mUserInactivityTimeout = this.mParser.getLong("user_inactivity_timeout_ms", 90000L);
        RetailDemoModeService.this.mUserInactivityTimeout = Math.max(RetailDemoModeService.this.mUserInactivityTimeout, 10000L);
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          Slog.e(RetailDemoModeService.-get0(), "Invalid string passed to KeyValueListParser");
        }
      }
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (this.mRetailDemoConstantsUri.equals(paramUri))
      {
        refreshTimeoutConstants();
        return;
      }
      if (this.mDeviceDemoModeUri.equals(paramUri))
      {
        RetailDemoModeService.this.mDeviceInDemoMode = UserManager.isDeviceInDemoMode(RetailDemoModeService.this.getContext());
        if (!RetailDemoModeService.this.mDeviceInDemoMode) {
          break label97;
        }
        RetailDemoModeService.-wrap4(RetailDemoModeService.this);
      }
      for (;;)
      {
        if ((!RetailDemoModeService.this.mDeviceInDemoMode) && (RetailDemoModeService.-wrap2(RetailDemoModeService.this))) {
          BackgroundThread.getHandler().post(new Runnable()
          {
            public void run()
            {
              if (!RetailDemoModeService.-wrap1(RetailDemoModeService.this)) {
                Slog.w(RetailDemoModeService.-get0(), "Failed to delete preloads folder contents");
              }
            }
          });
        }
        return;
        label97:
        SystemProperties.set("sys.retaildemo.enabled", "0");
        if (RetailDemoModeService.-get2(RetailDemoModeService.this).isHeld()) {
          RetailDemoModeService.-get2(RetailDemoModeService.this).release();
        }
      }
    }
    
    public void register()
    {
      ContentResolver localContentResolver = RetailDemoModeService.this.getContext().getContentResolver();
      localContentResolver.registerContentObserver(this.mDeviceDemoModeUri, false, this, 0);
      localContentResolver.registerContentObserver(this.mDeviceProvisionedUri, false, this, 0);
      localContentResolver.registerContentObserver(this.mRetailDemoConstantsUri, false, this, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/retaildemo/RetailDemoModeService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */