package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.oem.os.IOemExInputCallBack;
import com.oem.os.IOemExService.Stub;
import com.oem.os.IOemUeventCallback;
import com.oem.os.IThreeKeyPolicy;
import com.oneplus.threekey.ThreeKey;
import com.oneplus.threekey.ThreeKeyAudioPolicy;
import com.oneplus.threekey.ThreeKeyHw;
import com.oneplus.threekey.ThreeKeyHw.ThreeKeyUnsupportException;
import com.oneplus.threekey.ThreeKeyVibratorPolicy;
import java.io.File;

public final class OemExService
  extends IOemExService.Stub
{
  private static final String ACTION_BACK_COVER = "com.oem.intent.action.THREE_BACK_COVER";
  private static final String ACTION_BLACK_MODE_INIT = "android.settings.OEM_THEME_MODE.init";
  private static final String ACTION_OXYGEN_DARK_MODE_INIT = "com.oneplus.oxygen.changetheme.init";
  static final boolean DEBUG = true;
  static final boolean DEBUG_OEM_OBSERVER = true;
  public static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  private static final int MSG_DELAY_COVER = 2;
  private static final int MSG_INSTALL_COMPLETE = 3;
  private static final int MSG_SYSTEM_READY = 1;
  private static final String TAG = "OemExService";
  private static final String UDEV_NAME_BACKCOVER = "switch-theme";
  private static final String VENDOR_APP_INSTALLED = "vendor_app_installed";
  private static int mPackageInstallState = 0;
  private static int mPackageVerifierEnable;
  private static int sBackcoverState = 0;
  private Context mContext;
  private final Handler mHandler = new Handler(Looper.myLooper(), null, true)
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i = paramAnonymousMessage.arg1;
      int j = paramAnonymousMessage.arg2;
      switch (paramAnonymousMessage.what)
      {
      default: 
      case 1: 
      case 2: 
        do
        {
          do
          {
            return;
            OemExService.-wrap1(OemExService.this);
          } while (!OemExService.-get4(OemExService.this).isHeld());
          OemExService.-get4(OemExService.this).release();
          return;
          if (i != j)
          {
            OemExService.-set1(i);
            OemExService.-wrap2(OemExService.this, i);
          }
        } while (!OemExService.-get4(OemExService.this).isHeld());
        OemExService.-get4(OemExService.this).release();
        return;
      }
      String str;
      if ((paramAnonymousMessage.arg1 == 1) && (paramAnonymousMessage.obj != null))
      {
        str = paramAnonymousMessage.obj.toString();
        paramAnonymousMessage = Settings.Secure.getString(OemExService.-get0(OemExService.this).getContentResolver(), "vendor_app_installed");
        if (paramAnonymousMessage != null) {
          break label346;
        }
      }
      label346:
      for (paramAnonymousMessage = str + ", ";; paramAnonymousMessage = paramAnonymousMessage + str + ", ")
      {
        Settings.Secure.putString(OemExService.-get0(OemExService.this).getContentResolver(), "vendor_app_installed", paramAnonymousMessage);
        if (OemExService.DEBUG_ONEPLUS) {
          Slog.d("OemExService", "[" + str + "] has been installed.");
        }
        OemExService.-set0(OemExService.-get2() - 1);
        if (OemExService.DEBUG_ONEPLUS) {
          Slog.d("OemExService", "done: mPackageInstallState = " + OemExService.-get2());
        }
        if (OemExService.-get2() != 0) {
          break;
        }
        Settings.Global.putInt(OemExService.-get0(OemExService.this).getContentResolver(), "package_verifier_enable", OemExService.-get3());
        if (!OemExService.DEBUG_ONEPLUS) {
          break;
        }
        Slog.d("OemExService", "All Done : " + Settings.Secure.getString(OemExService.-get0(OemExService.this).getContentResolver(), "vendor_app_installed"));
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private OemSceneModeController mSceneModeController;
  private volatile boolean mSystemReady = false;
  private IThreeKeyPolicy mThreeKeyAudioPolicy;
  private IThreeKeyPolicy mThreeKeyVibratorPolicy;
  private final PowerManager.WakeLock mWakeLock;
  private ThreeKey threekey;
  private ThreeKeyHw threekeyhw;
  
  static
  {
    mPackageVerifierEnable = 0;
  }
  
  public OemExService(Context paramContext)
  {
    PowerManager localPowerManager = (PowerManager)paramContext.getSystemService("power");
    this.mContext = paramContext;
    this.mWakeLock = localPowerManager.newWakeLock(1, "OemExService");
  }
  
  private void installAPKs(String paramString)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    String str = Settings.Secure.getString(this.mContext.getContentResolver(), "vendor_app_installed");
    if (DEBUG_ONEPLUS) {
      Slog.v("OemExService", "installAPKs: Settings[IN_APP_INSTALLED] = " + str);
    }
    paramString = new File("/system/vendor/etc/apps/" + paramString);
    if (paramString.isDirectory())
    {
      paramString = paramString.listFiles();
      mPackageVerifierEnable = Settings.Global.getInt(this.mContext.getContentResolver(), "package_verifier_enable", 1);
      int k = paramString.length;
      int i = 0;
      if (i < k)
      {
        Object localObject = paramString[i];
        PackageInfo localPackageInfo;
        int j;
        if ((((File)localObject).exists()) && (((File)localObject).isFile()))
        {
          localPackageInfo = localPackageManager.getPackageArchiveInfo(((File)localObject).getAbsolutePath(), 0);
          if (DEBUG_ONEPLUS)
          {
            StringBuilder localStringBuilder = new StringBuilder().append("[").append(localPackageInfo.packageName).append("] = ");
            if (!localPackageManager.isPackageAvailable(localPackageInfo.packageName)) {
              break label244;
            }
            j = 1;
            label204:
            Slog.d("OemExService", j);
          }
          if ((str == null) || (!str.contains(localPackageInfo.packageName))) {
            break label249;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          label244:
          j = 0;
          break label204;
          label249:
          if (!localPackageManager.isPackageAvailable(localPackageInfo.packageName))
          {
            Settings.Global.putInt(this.mContext.getContentResolver(), "package_verifier_enable", 0);
            mPackageInstallState += 1;
            if (DEBUG_ONEPLUS) {
              Slog.d("OemExService", "start install: mPackageInstallState = " + mPackageInstallState);
            }
            localPackageManager.installPackage(Uri.parse("file://" + ((File)localObject).getAbsolutePath()), new PackageInstallObserver(), 258, localPackageInfo.packageName);
          }
        }
      }
    }
  }
  
  private void onSystemReady()
  {
    Slog.d("OemExService", "systemReady");
    this.mSystemReady = true;
    sendBroadcastForChangeTheme();
    this.threekeyhw = new ThreeKeyHw(this.mContext);
    if (!this.threekeyhw.isSupportThreeKey()) {
      return;
    }
    this.threekeyhw.init();
    this.mThreeKeyAudioPolicy = new ThreeKeyAudioPolicy(this.mContext);
    this.mThreeKeyVibratorPolicy = new ThreeKeyVibratorPolicy(this.mContext);
    try
    {
      this.threekey = new ThreeKey(this.mContext);
      this.threekey.addThreeKeyPolicy(this.mThreeKeyAudioPolicy);
      this.threekey.addThreeKeyPolicy(this.mThreeKeyVibratorPolicy);
      this.threekey.init(this.threekeyhw.getState());
      if (OpFeatures.isSupport(new int[] { 25 }))
      {
        if (DEBUG_ONEPLUS) {
          Slog.d("OemExService", "[scene] satrtMonitorSceneChanging");
        }
        if (this.mSceneModeController == null) {
          this.mSceneModeController = new OemSceneModeController(this.mContext);
        }
        this.mSceneModeController.startMonitor();
        return;
      }
    }
    catch (ThreeKeyHw.ThreeKeyUnsupportException localThreeKeyUnsupportException)
    {
      do
      {
        for (;;)
        {
          Slog.e("OemExService", "device is not support threekey");
          this.threekey = null;
        }
      } while (!DEBUG_ONEPLUS);
      Slog.d("OemExService", "[scene] Scene mode not supported");
    }
  }
  
  private void sendBroadcastForChangeTheme()
  {
    Intent localIntent = new Intent("android.settings.OEM_THEME_MODE.init");
    localIntent.addFlags(268435456);
    this.mContext.sendBroadcast(localIntent);
  }
  
  private void sendBroadcastForChangeTheme(int paramInt)
  {
    Intent localIntent = new Intent("com.oem.intent.action.THREE_BACK_COVER");
    localIntent.putExtra("switch_state", String.valueOf(paramInt));
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private native void setLCDGammaData(int paramInt);
  
  private native void setLaserCrossTalk(int paramInt);
  
  private native void setLaserOffset(int paramInt);
  
  public void addThreeKeyPolicy(IThreeKeyPolicy paramIThreeKeyPolicy)
  {
    Slog.d("OemExService", "[setThreeKeyPolicy]");
    this.threekey.addThreeKeyPolicy(paramIThreeKeyPolicy);
  }
  
  public void disableDefaultThreeKey()
  {
    this.threekey.removeThreeKeyPolicy(this.mThreeKeyAudioPolicy);
    Slog.d("OemExService", "[disableDefaultThreeKey]");
  }
  
  public void enalbeDefaultThreeKey()
  {
    this.threekey.addThreeKeyPolicy(this.mThreeKeyAudioPolicy);
    Slog.d("OemExService", "[enableDefaultThreeKey]");
  }
  
  public int getThreeKeyStatus()
  {
    try
    {
      int i = this.threekeyhw.getState();
      return i;
    }
    catch (ThreeKeyHw.ThreeKeyUnsupportException localThreeKeyUnsupportException)
    {
      Slog.e("OemExService", "system unsupport for threekey");
    }
    return 0;
  }
  
  public void monitorSceneChanging(boolean paramBoolean)
  {
    if (OpFeatures.isSupport(new int[] { 25 }))
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("OemExService", "[scene] monitorSceneChanging: " + paramBoolean);
      }
      if (paramBoolean)
      {
        if (this.mSceneModeController == null) {
          this.mSceneModeController = new OemSceneModeController(this.mContext);
        }
        this.mSceneModeController.startMonitorPassive();
      }
    }
    while (!DEBUG_ONEPLUS)
    {
      do
      {
        return;
      } while (this.mSceneModeController == null);
      this.mSceneModeController.stopMonitorPassive();
      return;
    }
    Slog.d("OemExService", "[scene] Scene mode not supported");
  }
  
  public void pauseExInputEvent()
    throws RemoteException
  {}
  
  public boolean preEvaluateModeStatus(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if (OpFeatures.isSupport(new int[] { 25 }))
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("OemExService", "[scene] preEvaluateModeStatus: modeType: " + paramInt1 + " swithcer switcherType: " + paramInt2);
      }
      if (this.mSceneModeController != null) {
        bool = this.mSceneModeController.preEvaluateModeStatus(paramInt1, paramInt2);
      }
    }
    while (!DEBUG_ONEPLUS) {
      return bool;
    }
    Slog.d("OemExService", "[scene] Scene mode not supported");
    return false;
  }
  
  public boolean registerInputEvent(IOemExInputCallBack paramIOemExInputCallBack, int paramInt)
  {
    return true;
  }
  
  public void removeThreeKeyPolicy(IThreeKeyPolicy paramIThreeKeyPolicy)
  {
    Slog.d("OemExService", "[removeThreeKeyPolicy]");
    this.threekey.removeThreeKeyPolicy(paramIThreeKeyPolicy);
  }
  
  public void resetThreeKey()
  {
    Slog.d("OemExService", "[resetThreeKey]");
    this.threekey.reset();
  }
  
  public void resumeExInputEvent()
    throws RemoteException
  {}
  
  public void setGammaData(int paramInt)
  {
    setLCDGammaData(paramInt);
  }
  
  public boolean setHomeUpLock()
  {
    Log.d("OemExService", "[setHomeUpLock]");
    return true;
  }
  
  public boolean setInteractive(boolean paramBoolean, long paramLong)
  {
    return true;
  }
  
  public boolean setKeyMode(int paramInt)
  {
    return true;
  }
  
  public void setLaserSensorCrossTalk(int paramInt)
  {
    setLaserCrossTalk(paramInt);
  }
  
  public void setLaserSensorOffset(int paramInt)
  {
    setLaserOffset(paramInt);
  }
  
  public boolean setSystemProperties(String paramString1, String paramString2)
  {
    return true;
  }
  
  public void startApkInstall(final String paramString)
  {
    if (mPackageInstallState == 0) {
      BackgroundThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          try
          {
            OemExService.-wrap0(OemExService.this, paramString);
            return;
          }
          catch (Exception localException)
          {
            Slog.w("OemExService", "installAPKs error.", localException);
          }
        }
      });
    }
  }
  
  public boolean startUevent(String paramString, IOemUeventCallback paramIOemUeventCallback)
    throws RemoteException
  {
    return true;
  }
  
  public boolean stopUevent(IOemUeventCallback paramIOemUeventCallback)
    throws RemoteException
  {
    return true;
  }
  
  public void systemRunning()
  {
    synchronized (this.mLock)
    {
      if (!this.mWakeLock.isHeld()) {
        this.mWakeLock.acquire();
      }
      Message localMessage = this.mHandler.obtainMessage(1, 0, 0, null);
      this.mHandler.sendMessage(localMessage);
      return;
    }
  }
  
  public void unregisterInputEvent(IOemExInputCallBack paramIOemExInputCallBack) {}
  
  class PackageInstallObserver
    extends IPackageInstallObserver.Stub
  {
    PackageInstallObserver() {}
    
    public void packageInstalled(String paramString, int paramInt)
    {
      Message localMessage = OemExService.-get1(OemExService.this).obtainMessage(3);
      localMessage.arg1 = paramInt;
      localMessage.obj = paramString;
      OemExService.-get1(OemExService.this).sendMessage(localMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/OemExService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */