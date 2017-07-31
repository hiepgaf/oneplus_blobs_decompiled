package com.android.server.power;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManager.Stub;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.ThemeController;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.nfc.INfcAdapter;
import android.nfc.INfcAdapter.Stub;
import android.os.FileUtils;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemVibrator;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.IMountShutdownObserver.Stub;
import android.util.Log;
import android.view.Window;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public final class ShutdownThread
  extends Thread
{
  private static final int ACTIVITY_MANAGER_STOP_PERCENT = 4;
  public static final String AUDIT_SAFEMODE_PROPERTY = "persist.sys.audit_safemode";
  private static final int BROADCAST_STOP_PERCENT = 2;
  private static final int MAX_BROADCAST_TIME = 10000;
  private static final int MAX_RADIO_WAIT_TIME = 12000;
  private static final int MAX_SHUTDOWN_WAIT_TIME = 20000;
  private static final int MAX_UNCRYPT_WAIT_TIME = 900000;
  private static final int MOUNT_SERVICE_STOP_PERCENT = 20;
  private static final int PACKAGE_MANAGER_STOP_PERCENT = 6;
  private static final int PHONE_STATE_POLL_SLEEP_MSEC = 500;
  private static final int RADIO_STOP_PERCENT = 18;
  public static final String REBOOT_SAFEMODE_PROPERTY = "persist.sys.safemode";
  public static final String RO_SAFEMODE_PROPERTY = "ro.sys.safemode";
  public static final String SHUTDOWN_ACTION_PROPERTY = "sys.shutdown.requested";
  private static final int SHUTDOWN_VIBRATE_MS = 500;
  private static final String TAG = "ShutdownThread";
  private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
  private static boolean mNeedShutdownDialog = false;
  private static String mReason;
  private static boolean mReboot = false;
  private static boolean mRebootHasProgressBar = false;
  private static boolean mRebootSafeMode = false;
  private static final boolean mSpew = true;
  private static AlertDialog sConfirmDialog;
  private static final ShutdownThread sInstance;
  private static boolean sIsStarted;
  private static Object sIsStartedGuard = new Object();
  private boolean mActionDone;
  private final Object mActionDoneSync = new Object();
  private Context mContext;
  private PowerManager.WakeLock mCpuWakeLock;
  private Handler mHandler;
  private PowerManager mPowerManager;
  private ProgressDialog mProgressDialog;
  private PowerManager.WakeLock mScreenWakeLock;
  
  static
  {
    sIsStarted = false;
    mNeedShutdownDialog = true;
    sInstance = new ShutdownThread();
  }
  
  private static void beginShutdownSequence(Context paramContext)
  {
    for (;;)
    {
      boolean bool;
      synchronized (sIsStartedGuard)
      {
        if (sIsStarted)
        {
          Log.d("ShutdownThread", "Shutdown sequence already running, returning.");
          return;
        }
        sIsStarted = true;
        if ("true".equals(SystemProperties.get("sys.update", "false"))) {
          mNeedShutdownDialog = false;
        }
        ??? = new ProgressDialog(paramContext, ThemeController.getInstance(paramContext).getCorrectThemeResource(new int[] { 16974546, 16974545 }));
        if ("recovery-update".equals(mReason)) {
          if (RecoverySystem.UNCRYPT_PACKAGE_FILE.exists()) {
            if (RecoverySystem.BLOCK_MAP_FILE.exists())
            {
              bool = false;
              mRebootHasProgressBar = bool;
              ((ProgressDialog)???).setTitle(paramContext.getText(17039659));
              if (!mRebootHasProgressBar) {
                break label366;
              }
              ((ProgressDialog)???).setMax(100);
              ((ProgressDialog)???).setProgress(0);
              ((ProgressDialog)???).setIndeterminate(false);
              ((ProgressDialog)???).setProgressNumberFormat(null);
              ((ProgressDialog)???).setProgressStyle(1);
              ((ProgressDialog)???).setMessage(paramContext.getText(17039660));
              ((ProgressDialog)???).setCancelable(false);
              ((ProgressDialog)???).getWindow().setType(2009);
              if (mNeedShutdownDialog) {
                ((ProgressDialog)???).show();
              }
              sInstance.mProgressDialog = ((ProgressDialog)???);
              sInstance.mContext = paramContext;
              sInstance.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
              sInstance.mCpuWakeLock = null;
            }
          }
        }
      }
      try
      {
        sInstance.mCpuWakeLock = sInstance.mPowerManager.newWakeLock(1, "ShutdownThread-cpu");
        sInstance.mCpuWakeLock.setReferenceCounted(false);
        sInstance.mCpuWakeLock.acquire();
        Log.d("ShutdownThread", "shutdown acquire partial WakeLock: cpu");
        sInstance.mScreenWakeLock = null;
        if (!sInstance.mPowerManager.isScreenOn()) {}
      }
      catch (SecurityException paramContext)
      {
        try
        {
          sInstance.mScreenWakeLock = sInstance.mPowerManager.newWakeLock(26, "ShutdownThread-screen");
          sInstance.mScreenWakeLock.setReferenceCounted(false);
          sInstance.mScreenWakeLock.acquire();
          sInstance.mHandler = new Handler() {};
          sInstance.start();
          return;
          paramContext = finally;
          throw paramContext;
          bool = true;
          continue;
          bool = false;
          continue;
          label366:
          ((ProgressDialog)???).setIndeterminate(true);
          ((ProgressDialog)???).setMessage(paramContext.getText(17039662));
          continue;
          if ("recovery".equals(mReason))
          {
            ((ProgressDialog)???).setTitle(paramContext.getText(17039663));
            ((ProgressDialog)???).setMessage(paramContext.getText(17039664));
            ((ProgressDialog)???).setIndeterminate(true);
            continue;
          }
          ((ProgressDialog)???).setTitle(paramContext.getText(17039655));
          ((ProgressDialog)???).setMessage(paramContext.getText(17039665));
          ((ProgressDialog)???).setIndeterminate(true);
          continue;
          paramContext = paramContext;
          Log.w("ShutdownThread", "No permission to acquire wake lock", paramContext);
          sInstance.mCpuWakeLock = null;
        }
        catch (SecurityException paramContext)
        {
          for (;;)
          {
            Log.w("ShutdownThread", "No permission to acquire wake lock", paramContext);
            sInstance.mScreenWakeLock = null;
          }
        }
      }
    }
  }
  
  private static void deviceRebootOrShutdown(boolean paramBoolean, String paramString)
  {
    try
    {
      Class localClass = Class.forName("com.qti.server.power.ShutdownOem");
      return;
    }
    catch (ClassNotFoundException paramString)
    {
      try
      {
        localClass.getMethod("rebootOrShutdown", new Class[] { Boolean.TYPE, String.class }).invoke(localClass.newInstance(), new Object[] { Boolean.valueOf(paramBoolean), paramString });
        return;
      }
      catch (Exception paramString)
      {
        Log.e("ShutdownThread", "Unknown exception while trying to invoke " + "rebootOrShutdown");
        return;
        paramString = paramString;
        Log.e("ShutdownThread", "Unable to find class " + "com.qti.server.power.ShutdownOem");
        return;
      }
      catch (NoSuchMethodException paramString)
      {
        Log.e("ShutdownThread", "Unable to find method " + "rebootOrShutdown" + " in class " + "com.qti.server.power.ShutdownOem");
        return;
      }
    }
    catch (Exception paramString)
    {
      Log.e("ShutdownThread", "Unknown exception while loading class " + "com.qti.server.power.ShutdownOem");
    }
  }
  
  public static void reboot(Context paramContext, String paramString, boolean paramBoolean)
  {
    int i = 0;
    mReboot = true;
    mRebootSafeMode = false;
    mRebootHasProgressBar = false;
    mReason = paramString;
    Log.d("ShutdownThread", "!!! Request to reboot !!!");
    paramString = new Throwable().getStackTrace();
    int j = paramString.length;
    while (i < j)
    {
      Object localObject = paramString[i];
      Log.d("ShutdownThread", " \t|----" + ((StackTraceElement)localObject).toString());
      i += 1;
    }
    shutdownInner(paramContext, paramBoolean);
  }
  
  public static void rebootOrShutdown(Context paramContext, boolean paramBoolean, String paramString)
  {
    deviceRebootOrShutdown(paramBoolean, paramString);
    String str;
    if (paramBoolean)
    {
      Log.i("ShutdownThread", "Rebooting, reason: " + paramString);
      PowerManagerService.lowLevelReboot(paramString);
      Log.e("ShutdownThread", "Reboot failed, will attempt shutdown instead");
      str = null;
    }
    for (;;)
    {
      Log.i("ShutdownThread", "Performing low-level shutdown...");
      PowerManagerService.lowLevelShutdown(str);
      return;
      str = paramString;
      if (paramContext == null) {
        continue;
      }
      paramContext = new SystemVibrator(paramContext);
      try
      {
        paramContext.vibrate(500L, VIBRATION_ATTRIBUTES);
        try
        {
          Thread.sleep(500L);
          str = paramString;
        }
        catch (InterruptedException paramContext)
        {
          str = paramString;
        }
      }
      catch (Exception paramContext)
      {
        for (;;)
        {
          Log.w("ShutdownThread", "Failed to vibrate during shutdown.", paramContext);
        }
      }
    }
  }
  
  public static void rebootSafeMode(Context paramContext, boolean paramBoolean)
  {
    if (((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_safe_boot")) {
      return;
    }
    mReboot = true;
    mRebootSafeMode = true;
    mRebootHasProgressBar = false;
    mReason = null;
    Log.d("ShutdownThread", "rebootSafeMode");
    shutdownInner(paramContext, paramBoolean);
  }
  
  private void setRebootProgress(final int paramInt, final CharSequence paramCharSequence)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (ShutdownThread.-get1(ShutdownThread.this) != null)
        {
          ShutdownThread.-get1(ShutdownThread.this).setProgress(paramInt);
          if (paramCharSequence != null) {
            ShutdownThread.-get1(ShutdownThread.this).setMessage(paramCharSequence);
          }
        }
      }
    });
  }
  
  public static void setShutdownDialogEnabled(boolean paramBoolean)
  {
    Log.d("ShutdownThread", "Need shutdown dialog: " + paramBoolean);
    mNeedShutdownDialog = paramBoolean;
  }
  
  public static void shutdown(Context paramContext, String paramString, boolean paramBoolean)
  {
    int i = 0;
    mReboot = false;
    mRebootSafeMode = false;
    mReason = paramString;
    Log.d("ShutdownThread", "!!! Request to shutdown !!!");
    paramString = new Throwable().getStackTrace();
    int j = paramString.length;
    while (i < j)
    {
      Object localObject = paramString[i];
      Log.d("ShutdownThread", " \t|----" + ((StackTraceElement)localObject).toString());
      i += 1;
    }
    shutdownInner(paramContext, paramBoolean);
  }
  
  static void shutdownInner(Context paramContext, boolean paramBoolean)
  {
    for (;;)
    {
      int k;
      int i;
      int j;
      int m;
      synchronized (sIsStartedGuard)
      {
        if (sIsStarted)
        {
          Log.d("ShutdownThread", "Request to shutdown already running, returning.");
          return;
        }
        k = 0;
        ??? = paramContext.getResources().getStringArray(17236031);
        i = 0;
        j = k;
        if (i < ???.length)
        {
          if (???[i].equals("reboot")) {
            j = 1;
          }
        }
        else
        {
          m = paramContext.getResources().getInteger(17694800);
          if (!mRebootSafeMode) {
            break label269;
          }
          i = 17039669;
          k = i;
          if (j != 0)
          {
            if (!mRebootSafeMode) {
              break label289;
            }
            k = i;
          }
          Log.d("ShutdownThread", "Notifying thread to start shutdown longPressBehavior=" + m);
          if (!paramBoolean) {
            break;
          }
          ??? = new CloseDialogReceiver(paramContext);
          if (sConfirmDialog != null) {
            sConfirmDialog.dismiss();
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
          if (!mRebootSafeMode) {
            break label297;
          }
          i = 17039668;
          sConfirmDialog = localBuilder.setTitle(i).setMessage(k).setPositiveButton(17039379, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              ShutdownThread.-wrap0(this.val$context);
            }
          }).setNegativeButton(17039369, null).create();
          ((CloseDialogReceiver)???).dialog = sConfirmDialog;
          sConfirmDialog.setOnDismissListener((DialogInterface.OnDismissListener)???);
          sConfirmDialog.getWindow().setType(2009);
          sConfirmDialog.show();
          return;
        }
      }
      i += 1;
      continue;
      label269:
      if (m == 2)
      {
        i = 17039667;
      }
      else
      {
        i = 17039666;
        continue;
        label289:
        k = 17039671;
        continue;
        label297:
        if (j != 0) {
          i = 17039670;
        } else {
          i = 17039655;
        }
      }
    }
    beginShutdownSequence(paramContext);
  }
  
  private void shutdownRadios(int paramInt)
  {
    long l1 = SystemClock.elapsedRealtime();
    long l2 = paramInt;
    final boolean[] arrayOfBoolean = new boolean[1];
    Thread local6 = new Thread()
    {
      public void run()
      {
        INfcAdapter localINfcAdapter = INfcAdapter.Stub.asInterface(ServiceManager.checkService("nfc"));
        ITelephony localITelephony = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
        IBluetoothManager localIBluetoothManager = IBluetoothManager.Stub.asInterface(ServiceManager.checkService("bluetooth_manager"));
        if (localINfcAdapter != null) {}
        for (;;)
        {
          try
          {
            if (localINfcAdapter.getState() != 1) {
              continue;
            }
            i = 1;
            j = i;
            if (i == 0)
            {
              Log.w("ShutdownThread", "Turning off NFC...");
              localINfcAdapter.disable(false);
              j = i;
            }
          }
          catch (RemoteException localRemoteException1)
          {
            Log.e("ShutdownThread", "RemoteException during NFC shutdown", localRemoteException1);
            j = 1;
            continue;
            i = 1;
            continue;
            i = 0;
            continue;
          }
          if (localIBluetoothManager == null) {
            continue;
          }
          try
          {
            if (localIBluetoothManager.getState() != 10) {
              continue;
            }
            i = 1;
            k = i;
            if (i == 0)
            {
              Log.w("ShutdownThread", "Disabling Bluetooth...");
              localIBluetoothManager.disable(false);
              k = i;
            }
          }
          catch (RemoteException localRemoteException2)
          {
            Log.e("ShutdownThread", "RemoteException during bluetooth shutdown", localRemoteException2);
            k = 1;
            continue;
            i = 1;
            continue;
          }
          if (localITelephony == null) {
            break label431;
          }
          try
          {
            if (!localITelephony.needMobileRadioShutdown()) {
              break label431;
            }
            i = 0;
            m = i;
            if (i == 0)
            {
              Log.w("ShutdownThread", "Turning off cellular radios...");
              localITelephony.shutdownMobileRadios();
              m = i;
            }
          }
          catch (RemoteException localRemoteException3)
          {
            long l;
            int n;
            Log.e("ShutdownThread", "RemoteException during radio shutdown", localRemoteException3);
            int m = 1;
            continue;
            i = 0;
            continue;
            continue;
          }
          Log.i("ShutdownThread", "Waiting for NFC, Bluetooth and Radio...");
          l = this.val$endTime - SystemClock.elapsedRealtime();
          i = k;
          if (l > 0L)
          {
            if (ShutdownThread.-get2())
            {
              k = (int)((arrayOfBoolean - l) * 1.0D * 12.0D / arrayOfBoolean);
              ShutdownThread.-wrap1(ShutdownThread.-get3(), k + 6, null);
            }
            k = i;
            if (i != 0) {}
          }
          try
          {
            i = localIBluetoothManager.getState();
            if (i != 10) {
              break label454;
            }
            i = 1;
          }
          catch (RemoteException localRemoteException4)
          {
            Log.e("ShutdownThread", "RemoteException during bluetooth shutdown", localRemoteException4);
            i = 1;
            continue;
            i = 1;
            continue;
          }
          k = i;
          if (i != 0)
          {
            Log.i("ShutdownThread", "Bluetooth turned off.");
            k = i;
          }
          n = m;
          if (m == 0) {}
          try
          {
            boolean bool = localITelephony.needMobileRadioShutdown();
            if (!bool) {
              break label476;
            }
            i = 0;
          }
          catch (RemoteException localRemoteException5)
          {
            Log.e("ShutdownThread", "RemoteException during radio shutdown", localRemoteException5);
            i = 1;
            continue;
            i = 0;
            continue;
          }
          n = i;
          if (i != 0)
          {
            Log.i("ShutdownThread", "Radio turned off.");
            n = i;
          }
          m = j;
          if (j == 0) {}
          try
          {
            i = localINfcAdapter.getState();
            if (i != 1) {
              break label498;
            }
            i = 1;
          }
          catch (RemoteException localRemoteException6)
          {
            Log.e("ShutdownThread", "RemoteException during NFC shutdown", localRemoteException6);
            i = 1;
            continue;
            SystemClock.sleep(500L);
            l = this.val$endTime - SystemClock.elapsedRealtime();
            i = k;
            j = m;
            m = n;
          }
          m = i;
          if (i != 0)
          {
            Log.i("ShutdownThread", "NFC turned off.");
            m = i;
          }
          if ((n == 0) || (k == 0) || (m == 0)) {
            break label520;
          }
          Log.i("ShutdownThread", "NFC, Radio and Bluetooth shutdown complete.");
          this.val$done[0] = true;
          return;
          i = 1;
          continue;
          i = 0;
        }
      }
    };
    local6.start();
    l1 = paramInt;
    try
    {
      local6.join(l1);
      if (arrayOfBoolean[0] == 0) {
        Log.w("ShutdownThread", "Timed out waiting for NFC, Radio and Bluetooth shutdown.");
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private void uncrypt()
  {
    Log.i("ShutdownThread", "Calling uncrypt and monitoring the progress...");
    final Object localObject2 = new RecoverySystem.ProgressListener()
    {
      public void onProgress(int paramAnonymousInt)
      {
        if ((paramAnonymousInt >= 0) && (paramAnonymousInt < 100))
        {
          paramAnonymousInt = (int)(paramAnonymousInt * 80.0D / 100.0D);
          localCharSequence = ShutdownThread.-get0(ShutdownThread.this).getText(17039661);
          ShutdownThread.-wrap1(ShutdownThread.-get3(), paramAnonymousInt + 20, localCharSequence);
        }
        while (paramAnonymousInt != 100) {
          return;
        }
        CharSequence localCharSequence = ShutdownThread.-get0(ShutdownThread.this).getText(17039662);
        ShutdownThread.-wrap1(ShutdownThread.-get3(), paramAnonymousInt, localCharSequence);
      }
    };
    final Object localObject1 = new boolean[1];
    localObject1[0] = 0;
    localObject2 = new Thread()
    {
      public void run()
      {
        Object localObject = (RecoverySystem)ShutdownThread.-get0(ShutdownThread.this).getSystemService("recovery");
        try
        {
          localObject = FileUtils.readTextFile(RecoverySystem.UNCRYPT_PACKAGE_FILE, 0, null);
          RecoverySystem.processPackage(ShutdownThread.-get0(ShutdownThread.this), new File((String)localObject), localObject2);
          localObject1[0] = true;
          return;
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            Log.e("ShutdownThread", "Error uncrypting file", localIOException);
          }
        }
      }
    };
    ((Thread)localObject2).start();
    try
    {
      ((Thread)localObject2).join(900000L);
      if (localObject1[0] == 0)
      {
        Log.w("ShutdownThread", "Timed out waiting for uncrypt.");
        localObject1 = String.format("uncrypt_time: %d\nuncrypt_error: %d\n", new Object[] { Integer.valueOf(900), Integer.valueOf(100) });
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        try
        {
          FileUtils.stringToFile(RecoverySystem.UNCRYPT_STATUS_FILE, (String)localObject1);
          return;
        }
        catch (IOException localIOException)
        {
          Log.e("ShutdownThread", "Failed to write timeout message to uncrypt status", localIOException);
        }
        localInterruptedException = localInterruptedException;
      }
    }
  }
  
  void actionDone()
  {
    synchronized (this.mActionDoneSync)
    {
      this.mActionDone = true;
      this.mActionDoneSync.notifyAll();
      return;
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: new 10	com/android/server/power/ShutdownThread$3
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 614	com/android/server/power/ShutdownThread$3:<init>	(Lcom/android/server/power/ShutdownThread;)V
    //   8: astore 8
    //   10: new 362	java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial 363	java/lang/StringBuilder:<init>	()V
    //   17: astore 9
    //   19: getstatic 388	com/android/server/power/ShutdownThread:mReboot	Z
    //   22: ifeq +669 -> 691
    //   25: ldc_w 616
    //   28: astore 7
    //   30: aload 9
    //   32: aload 7
    //   34: invokevirtual 369	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: astore 9
    //   39: getstatic 202	com/android/server/power/ShutdownThread:mReason	Ljava/lang/String;
    //   42: ifnull +657 -> 699
    //   45: getstatic 202	com/android/server/power/ShutdownThread:mReason	Ljava/lang/String;
    //   48: astore 7
    //   50: ldc 57
    //   52: aload 9
    //   54: aload 7
    //   56: invokevirtual 369	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: invokevirtual 373	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   62: invokestatic 620	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   65: getstatic 390	com/android/server/power/ShutdownThread:mRebootSafeMode	Z
    //   68: ifeq +11 -> 79
    //   71: ldc 51
    //   73: ldc_w 616
    //   76: invokestatic 620	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   79: iconst_1
    //   80: newarray <illegal type>
    //   82: dup
    //   83: iconst_0
    //   84: iconst_2
    //   85: iastore
    //   86: invokestatic 626	android/util/OpFeatures:isSupport	([I)Z
    //   89: ifeq +95 -> 184
    //   92: ldc 61
    //   94: ldc_w 628
    //   97: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: new 630	java/lang/ProcessBuilder
    //   104: dup
    //   105: bipush 10
    //   107: anewarray 177	java/lang/String
    //   110: dup
    //   111: iconst_0
    //   112: ldc_w 632
    //   115: aastore
    //   116: dup
    //   117: iconst_1
    //   118: ldc_w 634
    //   121: aastore
    //   122: dup
    //   123: iconst_2
    //   124: ldc_w 636
    //   127: aastore
    //   128: dup
    //   129: iconst_3
    //   130: ldc_w 638
    //   133: aastore
    //   134: dup
    //   135: iconst_4
    //   136: ldc_w 640
    //   139: aastore
    //   140: dup
    //   141: iconst_5
    //   142: ldc_w 642
    //   145: aastore
    //   146: dup
    //   147: bipush 6
    //   149: ldc_w 644
    //   152: aastore
    //   153: dup
    //   154: bipush 7
    //   156: ldc_w 646
    //   159: aastore
    //   160: dup
    //   161: bipush 8
    //   163: ldc_w 648
    //   166: aastore
    //   167: dup
    //   168: bipush 9
    //   170: ldc_w 650
    //   173: aastore
    //   174: invokespecial 653	java/lang/ProcessBuilder:<init>	([Ljava/lang/String;)V
    //   177: invokevirtual 656	java/lang/ProcessBuilder:start	()Ljava/lang/Process;
    //   180: invokevirtual 662	java/lang/Process:waitFor	()I
    //   183: pop
    //   184: ldc 61
    //   186: ldc_w 664
    //   189: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   192: pop
    //   193: aload_0
    //   194: iconst_0
    //   195: putfield 607	com/android/server/power/ShutdownThread:mActionDone	Z
    //   198: new 666	android/content/Intent
    //   201: dup
    //   202: ldc_w 668
    //   205: invokespecial 670	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   208: astore 7
    //   210: aload 7
    //   212: ldc_w 671
    //   215: invokevirtual 675	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   218: pop
    //   219: aload_0
    //   220: getfield 96	com/android/server/power/ShutdownThread:mContext	Landroid/content/Context;
    //   223: aload 7
    //   225: getstatic 681	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   228: aconst_null
    //   229: aload 8
    //   231: aload_0
    //   232: getfield 302	com/android/server/power/ShutdownThread:mHandler	Landroid/os/Handler;
    //   235: iconst_0
    //   236: aconst_null
    //   237: aconst_null
    //   238: invokevirtual 685	android/content/Context:sendOrderedBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;Ljava/lang/String;Landroid/content/BroadcastReceiver;Landroid/os/Handler;ILjava/lang/String;Landroid/os/Bundle;)V
    //   241: invokestatic 554	android/os/SystemClock:elapsedRealtime	()J
    //   244: lstore_2
    //   245: aload_0
    //   246: getfield 153	com/android/server/power/ShutdownThread:mActionDoneSync	Ljava/lang/Object;
    //   249: astore 7
    //   251: aload 7
    //   253: monitorenter
    //   254: aload_0
    //   255: getfield 607	com/android/server/power/ShutdownThread:mActionDone	Z
    //   258: ifne +30 -> 288
    //   261: lload_2
    //   262: ldc2_w 686
    //   265: ladd
    //   266: invokestatic 554	android/os/SystemClock:elapsedRealtime	()J
    //   269: lsub
    //   270: lstore 4
    //   272: lload 4
    //   274: lconst_0
    //   275: lcmp
    //   276: ifgt +447 -> 723
    //   279: ldc 61
    //   281: ldc_w 689
    //   284: invokestatic 565	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   287: pop
    //   288: aload 7
    //   290: monitorexit
    //   291: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   294: ifeq +11 -> 305
    //   297: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   300: iconst_2
    //   301: aconst_null
    //   302: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   305: ldc 61
    //   307: ldc_w 691
    //   310: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   313: pop
    //   314: ldc_w 693
    //   317: invokestatic 699	android/os/ServiceManager:checkService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   320: invokestatic 705	android/app/ActivityManagerNative:asInterface	(Landroid/os/IBinder;)Landroid/app/IActivityManager;
    //   323: astore 7
    //   325: aload 7
    //   327: ifnull +14 -> 341
    //   330: aload 7
    //   332: sipush 10000
    //   335: invokeinterface 710 2 0
    //   340: pop
    //   341: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   344: ifeq +11 -> 355
    //   347: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   350: iconst_4
    //   351: aconst_null
    //   352: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   355: ldc 61
    //   357: ldc_w 712
    //   360: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   363: pop
    //   364: ldc_w 714
    //   367: invokestatic 717	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   370: checkcast 719	com/android/server/pm/PackageManagerService
    //   373: astore 7
    //   375: aload 7
    //   377: ifnull +8 -> 385
    //   380: aload 7
    //   382: invokevirtual 721	com/android/server/pm/PackageManagerService:shutdown	()V
    //   385: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   388: ifeq +12 -> 400
    //   391: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   394: bipush 6
    //   396: aconst_null
    //   397: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   400: ldc 61
    //   402: ldc_w 723
    //   405: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   408: pop
    //   409: aload_0
    //   410: sipush 12000
    //   413: invokespecial 725	com/android/server/power/ShutdownThread:shutdownRadios	(I)V
    //   416: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   419: ifeq +12 -> 431
    //   422: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   425: bipush 18
    //   427: aconst_null
    //   428: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   431: new 12	com/android/server/power/ShutdownThread$4
    //   434: dup
    //   435: aload_0
    //   436: invokespecial 726	com/android/server/power/ShutdownThread$4:<init>	(Lcom/android/server/power/ShutdownThread;)V
    //   439: astore 8
    //   441: ldc 61
    //   443: ldc_w 728
    //   446: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   449: pop
    //   450: aload_0
    //   451: iconst_0
    //   452: putfield 607	com/android/server/power/ShutdownThread:mActionDone	Z
    //   455: invokestatic 554	android/os/SystemClock:elapsedRealtime	()J
    //   458: lstore_2
    //   459: aload_0
    //   460: getfield 153	com/android/server/power/ShutdownThread:mActionDoneSync	Ljava/lang/Object;
    //   463: astore 7
    //   465: aload 7
    //   467: monitorenter
    //   468: ldc_w 730
    //   471: invokestatic 699	android/os/ServiceManager:checkService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   474: invokestatic 735	android/os/storage/IMountService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/storage/IMountService;
    //   477: astore 9
    //   479: aload 9
    //   481: ifnull +311 -> 792
    //   484: aload 9
    //   486: aload 8
    //   488: invokeinterface 740 2 0
    //   493: aload_0
    //   494: getfield 607	com/android/server/power/ShutdownThread:mActionDone	Z
    //   497: ifne +30 -> 527
    //   500: lload_2
    //   501: ldc2_w 741
    //   504: ladd
    //   505: invokestatic 554	android/os/SystemClock:elapsedRealtime	()J
    //   508: lsub
    //   509: lstore 4
    //   511: lload 4
    //   513: lconst_0
    //   514: lcmp
    //   515: ifgt +313 -> 828
    //   518: ldc 61
    //   520: ldc_w 744
    //   523: invokestatic 565	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   526: pop
    //   527: aload 7
    //   529: monitorexit
    //   530: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   533: ifeq +16 -> 549
    //   536: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   539: bipush 20
    //   541: aconst_null
    //   542: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   545: aload_0
    //   546: invokespecial 746	com/android/server/power/ShutdownThread:uncrypt	()V
    //   549: ldc 61
    //   551: ldc_w 748
    //   554: invokestatic 418	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   557: pop
    //   558: aload_0
    //   559: getfield 96	com/android/server/power/ShutdownThread:mContext	Landroid/content/Context;
    //   562: invokevirtual 752	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   565: ldc_w 754
    //   568: getstatic 757	com/android/server/power/PowerManagerService:mManualBrightness	I
    //   571: i2f
    //   572: bipush -2
    //   574: invokestatic 763	android/provider/Settings$System:putFloatForUser	(Landroid/content/ContentResolver;Ljava/lang/String;FI)Z
    //   577: pop
    //   578: aload_0
    //   579: getfield 96	com/android/server/power/ShutdownThread:mContext	Landroid/content/Context;
    //   582: invokevirtual 752	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   585: ldc_w 765
    //   588: getstatic 769	com/android/server/power/PowerManagerService:mManulAtAmbientLux	F
    //   591: bipush -2
    //   593: invokestatic 763	android/provider/Settings$System:putFloatForUser	(Landroid/content/ContentResolver;Ljava/lang/String;FI)Z
    //   596: pop
    //   597: ldc_w 771
    //   600: iconst_0
    //   601: invokestatic 775	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   604: istore 6
    //   606: ldc_w 777
    //   609: invokestatic 780	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   612: astore 7
    //   614: iload 6
    //   616: ifeq +34 -> 650
    //   619: ldc_w 782
    //   622: aload 7
    //   624: invokevirtual 181	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   627: ifne +14 -> 641
    //   630: ldc_w 616
    //   633: aload 7
    //   635: invokevirtual 181	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   638: ifeq +12 -> 650
    //   641: ldc_w 784
    //   644: ldc_w 616
    //   647: invokestatic 789	android/app/AlarmManager:writePowerOffAlarmFile	(Ljava/lang/String;Ljava/lang/String;)V
    //   650: getstatic 388	com/android/server/power/ShutdownThread:mReboot	Z
    //   653: ifeq +234 -> 887
    //   656: ldc_w 308
    //   659: getstatic 202	com/android/server/power/ShutdownThread:mReason	Ljava/lang/String;
    //   662: invokevirtual 181	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   665: ifeq +222 -> 887
    //   668: ldc_w 791
    //   671: ldc_w 793
    //   674: invokestatic 789	android/app/AlarmManager:writePowerOffAlarmFile	(Ljava/lang/String;Ljava/lang/String;)V
    //   677: aload_0
    //   678: getfield 96	com/android/server/power/ShutdownThread:mContext	Landroid/content/Context;
    //   681: getstatic 388	com/android/server/power/ShutdownThread:mReboot	Z
    //   684: getstatic 202	com/android/server/power/ShutdownThread:mReason	Ljava/lang/String;
    //   687: invokestatic 795	com/android/server/power/ShutdownThread:rebootOrShutdown	(Landroid/content/Context;ZLjava/lang/String;)V
    //   690: return
    //   691: ldc_w 797
    //   694: astore 7
    //   696: goto -666 -> 30
    //   699: ldc_w 793
    //   702: astore 7
    //   704: goto -654 -> 50
    //   707: astore 7
    //   709: ldc 61
    //   711: ldc_w 799
    //   714: aload 7
    //   716: invokestatic 604	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   719: pop
    //   720: goto -536 -> 184
    //   723: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   726: ifeq +30 -> 756
    //   729: ldc2_w 686
    //   732: lload 4
    //   734: lsub
    //   735: l2d
    //   736: dconst_1
    //   737: dmul
    //   738: ldc2_w 800
    //   741: dmul
    //   742: ldc2_w 802
    //   745: ddiv
    //   746: d2i
    //   747: istore_1
    //   748: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   751: iload_1
    //   752: aconst_null
    //   753: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   756: aload_0
    //   757: getfield 153	com/android/server/power/ShutdownThread:mActionDoneSync	Ljava/lang/Object;
    //   760: lload 4
    //   762: ldc2_w 435
    //   765: invokestatic 809	java/lang/Math:min	(JJ)J
    //   768: invokevirtual 812	java/lang/Object:wait	(J)V
    //   771: goto -517 -> 254
    //   774: astore 8
    //   776: goto -522 -> 254
    //   779: astore 8
    //   781: aload 7
    //   783: monitorexit
    //   784: aload 8
    //   786: athrow
    //   787: astore 7
    //   789: goto -448 -> 341
    //   792: ldc 61
    //   794: ldc_w 814
    //   797: invokestatic 565	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   800: pop
    //   801: goto -308 -> 493
    //   804: astore 8
    //   806: ldc 61
    //   808: ldc_w 816
    //   811: aload 8
    //   813: invokestatic 604	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   816: pop
    //   817: goto -324 -> 493
    //   820: astore 8
    //   822: aload 7
    //   824: monitorexit
    //   825: aload 8
    //   827: athrow
    //   828: getstatic 105	com/android/server/power/ShutdownThread:mRebootHasProgressBar	Z
    //   831: ifeq +33 -> 864
    //   834: ldc2_w 741
    //   837: lload 4
    //   839: lsub
    //   840: l2d
    //   841: dconst_1
    //   842: dmul
    //   843: ldc2_w 800
    //   846: dmul
    //   847: ldc2_w 817
    //   850: ddiv
    //   851: d2i
    //   852: istore_1
    //   853: getstatic 109	com/android/server/power/ShutdownThread:sInstance	Lcom/android/server/power/ShutdownThread;
    //   856: iload_1
    //   857: bipush 18
    //   859: iadd
    //   860: aconst_null
    //   861: invokespecial 120	com/android/server/power/ShutdownThread:setRebootProgress	(ILjava/lang/CharSequence;)V
    //   864: aload_0
    //   865: getfield 153	com/android/server/power/ShutdownThread:mActionDoneSync	Ljava/lang/Object;
    //   868: lload 4
    //   870: ldc2_w 435
    //   873: invokestatic 809	java/lang/Math:min	(JJ)J
    //   876: invokevirtual 812	java/lang/Object:wait	(J)V
    //   879: goto -386 -> 493
    //   882: astore 8
    //   884: goto -391 -> 493
    //   887: ldc_w 791
    //   890: ldc_w 820
    //   893: invokestatic 780	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   896: invokestatic 789	android/app/AlarmManager:writePowerOffAlarmFile	(Ljava/lang/String;Ljava/lang/String;)V
    //   899: goto -222 -> 677
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	902	0	this	ShutdownThread
    //   747	113	1	i	int
    //   244	257	2	l1	long
    //   270	599	4	l2	long
    //   604	11	6	bool	boolean
    //   707	75	7	localException1	Exception
    //   787	36	7	localRemoteException	RemoteException
    //   8	479	8	localObject2	Object
    //   774	1	8	localInterruptedException1	InterruptedException
    //   779	6	8	localObject3	Object
    //   804	8	8	localException2	Exception
    //   820	6	8	localObject4	Object
    //   882	1	8	localInterruptedException2	InterruptedException
    //   17	468	9	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   101	184	707	java/lang/Exception
    //   756	771	774	java/lang/InterruptedException
    //   254	272	779	finally
    //   279	288	779	finally
    //   723	756	779	finally
    //   756	771	779	finally
    //   330	341	787	android/os/RemoteException
    //   468	479	804	java/lang/Exception
    //   484	493	804	java/lang/Exception
    //   792	801	804	java/lang/Exception
    //   468	479	820	finally
    //   484	493	820	finally
    //   493	511	820	finally
    //   518	527	820	finally
    //   792	801	820	finally
    //   806	817	820	finally
    //   828	864	820	finally
    //   864	879	820	finally
    //   864	879	882	java/lang/InterruptedException
  }
  
  private static class CloseDialogReceiver
    extends BroadcastReceiver
    implements DialogInterface.OnDismissListener
  {
    public Dialog dialog;
    private Context mContext;
    
    CloseDialogReceiver(Context paramContext)
    {
      this.mContext = paramContext;
      paramContext.registerReceiver(this, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      this.mContext.unregisterReceiver(this);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.dialog.cancel();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/ShutdownThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */