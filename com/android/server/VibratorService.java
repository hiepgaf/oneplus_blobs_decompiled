package com.android.server;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IVibratorService.Stub;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.WorkSource;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Slog;
import android.view.InputDevice;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class VibratorService
  extends IVibratorService.Stub
  implements InputManager.InputDeviceListener
{
  private static final boolean DEBUG = false;
  private static final String SYSTEM_UI_PACKAGE = "com.android.systemui";
  private static final String TAG = "VibratorService";
  private final int INTENSITY_MIDDLE = -2;
  private final int INTENSITY_STRONG = -3;
  private final int INTENSITY_WEAK = -1;
  private final String VIBRATOR_INTENSITY_PATH = "/sys/class/timed_output/vibrator/vmax";
  private final int intensityMiddle = 1508;
  private final int intensityStrong = 3596;
  private final int intensityWeak = 812;
  private final IAppOpsService mAppOpsService;
  private final IBatteryStats mBatteryStatsService;
  private final Context mContext;
  private int mCurVibUid = -1;
  private Vibration mCurrentVibration;
  private final Handler mH = new Handler();
  private InputManager mIm;
  private boolean mInputDeviceListenerRegistered;
  private final ArrayList<Vibrator> mInputDeviceVibrators = new ArrayList();
  BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.SCREEN_OFF")) {
        for (;;)
        {
          synchronized (VibratorService.-get3(VibratorService.this))
          {
            if (VibratorService.-get1(VibratorService.this) == 0)
            {
              Slog.e("VibratorService", "vibrate service don't cancel when lidState is close ");
              return;
            }
            if ((VibratorService.-get0(VibratorService.this) == null) || (VibratorService.-get0(VibratorService.this).isSystemHapticFeedback()))
            {
              paramAnonymousIntent = VibratorService.-get3(VibratorService.this).iterator();
              if (!paramAnonymousIntent.hasNext()) {
                break;
              }
              localVibration = (VibratorService.Vibration)paramAnonymousIntent.next();
              if (localVibration == VibratorService.-get0(VibratorService.this)) {
                continue;
              }
              VibratorService.-wrap3(VibratorService.this, localVibration);
              paramAnonymousIntent.remove();
            }
          }
          VibratorService.-wrap0(VibratorService.this);
        }
      }
      while (!paramAnonymousIntent.getAction().equals("android.intent.action.LID_SWITCH"))
      {
        VibratorService.Vibration localVibration;
        return;
      }
      VibratorService.-set0(VibratorService.this, paramAnonymousIntent.getIntExtra("lidOpen", 1));
    }
  };
  private int mLidState = 1;
  private boolean mLowPowerMode;
  private PowerManagerInternal mPowerManagerInternal;
  private final LinkedList<VibrationInfo> mPreviousVibrations;
  private final int mPreviousVibrationsLimit;
  private SettingsObserver mSettingObserver;
  volatile VibrateThread mThread;
  private final WorkSource mTmpWorkSource = new WorkSource();
  private boolean mVibrateInputDevicesSetting;
  private final Runnable mVibrationRunnable = new Runnable()
  {
    public void run()
    {
      synchronized (VibratorService.-get3(VibratorService.this))
      {
        VibratorService.-wrap0(VibratorService.this);
        VibratorService.-wrap2(VibratorService.this);
        return;
      }
    }
  };
  private final LinkedList<Vibration> mVibrations;
  private final PowerManager.WakeLock mWakeLock;
  
  VibratorService(Context paramContext)
  {
    vibratorInit();
    vibratorOff();
    this.mContext = paramContext;
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "*vibrator*");
    this.mWakeLock.setReferenceCounted(true);
    this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    this.mBatteryStatsService = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    this.mPreviousVibrationsLimit = this.mContext.getResources().getInteger(17694882);
    this.mVibrations = new LinkedList();
    this.mPreviousVibrations = new LinkedList();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.LID_SWITCH");
    paramContext.registerReceiver(this.mIntentReceiver, localIntentFilter);
  }
  
  private void WriteNodeValue(int paramInt)
  {
    try
    {
      FileWriter localFileWriter = new FileWriter(new File("/sys/class/timed_output/vibrator/vmax"));
      localFileWriter.write(String.valueOf(paramInt));
      localFileWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  private void addToPreviousVibrationsLocked(Vibration paramVibration)
  {
    if (this.mPreviousVibrations.size() > this.mPreviousVibrationsLimit) {
      this.mPreviousVibrations.removeFirst();
    }
    paramVibration = new VibrationInfo(Vibration.-get4(paramVibration), Vibration.-get3(paramVibration), Vibration.-get1(paramVibration), Vibration.-get8(paramVibration), Vibration.-get2(paramVibration), Vibration.-get7(paramVibration), Vibration.-get6(paramVibration), Vibration.-get0(paramVibration));
    Slog.d("VibratorService", paramVibration.toString());
    this.mPreviousVibrations.addLast(paramVibration);
  }
  
  private void doCancelVibrateLocked()
  {
    if (this.mThread != null) {}
    synchronized (this.mThread)
    {
      this.mThread.mDone = true;
      this.mThread.notify();
      this.mThread = null;
      doVibratorOff();
      this.mH.removeCallbacks(this.mVibrationRunnable);
      reportFinishVibrationLocked();
      return;
    }
  }
  
  private boolean doVibratorExists()
  {
    return vibratorExists();
  }
  
  private void doVibratorOff()
  {
    int i;
    synchronized (this.mInputDeviceVibrators)
    {
      i = this.mCurVibUid;
      if (i < 0) {}
    }
    try
    {
      this.mBatteryStatsService.noteVibratorOff(this.mCurVibUid);
      this.mCurVibUid = -1;
      int j = this.mInputDeviceVibrators.size();
      if (j != 0)
      {
        i = 0;
        while (i < j)
        {
          ((Vibrator)this.mInputDeviceVibrators.get(i)).cancel();
          i += 1;
        }
      }
      vibratorOff();
      return;
      localObject = finally;
      throw ((Throwable)localObject);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private void doVibratorOn(long paramLong, int paramInt1, int paramInt2)
  {
    try
    {
      synchronized (this.mInputDeviceVibrators)
      {
        this.mBatteryStatsService.noteVibratorOn(paramInt1, paramLong);
        this.mCurVibUid = paramInt1;
        int i = this.mInputDeviceVibrators.size();
        if (i != 0)
        {
          AudioAttributes localAudioAttributes = new AudioAttributes.Builder().setUsage(paramInt2).build();
          paramInt1 = 0;
          while (paramInt1 < i)
          {
            ((Vibrator)this.mInputDeviceVibrators.get(paramInt1)).vibrate(paramLong, localAudioAttributes);
            paramInt1 += 1;
          }
        }
        vibratorOn(paramLong);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private boolean isAll0(long[] paramArrayOfLong)
  {
    int j = paramArrayOfLong.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfLong[i] != 0L) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private Vibration removeVibrationLocked(IBinder paramIBinder)
  {
    ListIterator localListIterator = this.mVibrations.listIterator(0);
    while (localListIterator.hasNext())
    {
      Vibration localVibration = (Vibration)localListIterator.next();
      if (Vibration.-get5(localVibration) == paramIBinder)
      {
        localListIterator.remove();
        unlinkVibration(localVibration);
        return localVibration;
      }
    }
    if ((this.mCurrentVibration != null) && (Vibration.-get5(this.mCurrentVibration) == paramIBinder))
    {
      unlinkVibration(this.mCurrentVibration);
      return this.mCurrentVibration;
    }
    return null;
  }
  
  private void reportFinishVibrationLocked()
  {
    if (this.mCurrentVibration != null) {}
    try
    {
      this.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAppOpsService), 3, Vibration.-get6(this.mCurrentVibration), Vibration.-get0(this.mCurrentVibration));
      this.mCurrentVibration = null;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private boolean shouldVibrateForRingtone()
  {
    int i = ((AudioManager)this.mContext.getSystemService("audio")).getRingerModeInternal();
    if (Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_when_ringing", 0) != 0) {
      return i != 0;
    }
    return i == 1;
  }
  
  private void startNextVibrationLocked()
  {
    if (this.mVibrations.size() <= 0)
    {
      reportFinishVibrationLocked();
      this.mCurrentVibration = null;
      return;
    }
    startVibrationLocked((Vibration)this.mVibrations.getFirst());
  }
  
  private void startVibrationLocked(Vibration paramVibration)
  {
    try
    {
      if ((this.mLowPowerMode) && (Vibration.-get7(paramVibration) != 6)) {
        return;
      }
      if ((Vibration.-get7(paramVibration) == 6) && (!shouldVibrateForRingtone())) {
        break label139;
      }
      int j = this.mAppOpsService.checkAudioOperation(3, Vibration.-get7(paramVibration), Vibration.-get6(paramVibration), Vibration.-get0(paramVibration));
      i = j;
      if (j == 0) {
        i = this.mAppOpsService.startOperation(AppOpsManager.getToken(this.mAppOpsService), 3, Vibration.-get6(paramVibration), Vibration.-get0(paramVibration));
      }
      if (i != 0) {
        break label140;
      }
      this.mCurrentVibration = paramVibration;
    }
    catch (RemoteException localRemoteException)
    {
      int i;
      label139:
      label140:
      for (;;) {}
    }
    if (Vibration.-get4(paramVibration) != 0L)
    {
      doVibratorOn(Vibration.-get4(paramVibration), Vibration.-get6(paramVibration), Vibration.-get7(paramVibration));
      this.mH.postDelayed(this.mVibrationRunnable, Vibration.-get4(paramVibration));
      return;
      return;
      if (i == 2) {
        Slog.w("VibratorService", "Would be an error: vibrate from uid " + Vibration.-get6(paramVibration));
      }
      this.mH.post(this.mVibrationRunnable);
      return;
    }
    this.mThread = new VibrateThread(paramVibration);
    this.mThread.start();
  }
  
  private void unlinkVibration(Vibration paramVibration)
  {
    if (Vibration.-get1(paramVibration) != null) {
      Vibration.-get5(paramVibration).unlinkToDeath(paramVibration, 0);
    }
  }
  
  private void updateInputDeviceVibrators()
  {
    boolean bool = true;
    synchronized (this.mVibrations)
    {
      doCancelVibrateLocked();
      synchronized (this.mInputDeviceVibrators)
      {
        this.mVibrateInputDevicesSetting = false;
      }
    }
    try
    {
      if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "vibrate_input_devices", -2) > 0)
      {
        this.mVibrateInputDevicesSetting = bool;
        this.mLowPowerMode = this.mPowerManagerInternal.getLowPowerModeEnabled();
        if (this.mVibrateInputDevicesSetting) {
          if (!this.mInputDeviceListenerRegistered)
          {
            this.mInputDeviceListenerRegistered = true;
            this.mIm.registerInputDeviceListener(this, this.mH);
          }
        }
        for (;;)
        {
          this.mInputDeviceVibrators.clear();
          if (!this.mVibrateInputDevicesSetting) {
            break;
          }
          int[] arrayOfInt = this.mIm.getInputDeviceIds();
          i = 0;
          if (i >= arrayOfInt.length) {
            break;
          }
          Vibrator localVibrator = this.mIm.getInputDevice(arrayOfInt[i]).getVibrator();
          if (!localVibrator.hasVibrator()) {
            break label214;
          }
          this.mInputDeviceVibrators.add(localVibrator);
          break label214;
          if (this.mInputDeviceListenerRegistered)
          {
            this.mInputDeviceListenerRegistered = false;
            this.mIm.unregisterInputDeviceListener(this);
          }
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
        localObject1 = finally;
        throw ((Throwable)localObject1);
        startNextVibrationLocked();
        return;
      }
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      for (;;)
      {
        int i;
        continue;
        label214:
        i += 1;
        continue;
        bool = false;
      }
    }
  }
  
  private void verifyIncomingUid(int paramInt)
  {
    if (paramInt == Binder.getCallingUid()) {
      return;
    }
    if (Binder.getCallingPid() == Process.myPid()) {
      return;
    }
    this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
  }
  
  static native boolean vibratorExists();
  
  static native void vibratorInit();
  
  static native void vibratorOff();
  
  static native void vibratorOn(long paramLong);
  
  /* Error */
  public void cancelVibrate(IBinder paramIBinder)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 183	com/android/server/VibratorService:mContext	Landroid/content/Context;
    //   4: ldc_w 589
    //   7: ldc_w 590
    //   10: invokevirtual 594	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   13: invokestatic 598	android/os/Binder:clearCallingIdentity	()J
    //   16: lstore_2
    //   17: aload_0
    //   18: getfield 101	com/android/server/VibratorService:mVibrations	Ljava/util/LinkedList;
    //   21: astore 4
    //   23: aload 4
    //   25: monitorenter
    //   26: aload_0
    //   27: aload_1
    //   28: invokespecial 600	com/android/server/VibratorService:removeVibrationLocked	(Landroid/os/IBinder;)Lcom/android/server/VibratorService$Vibration;
    //   31: aload_0
    //   32: getfield 88	com/android/server/VibratorService:mCurrentVibration	Lcom/android/server/VibratorService$Vibration;
    //   35: if_acmpne +11 -> 46
    //   38: aload_0
    //   39: invokespecial 113	com/android/server/VibratorService:doCancelVibrateLocked	()V
    //   42: aload_0
    //   43: invokespecial 123	com/android/server/VibratorService:startNextVibrationLocked	()V
    //   46: aload 4
    //   48: monitorexit
    //   49: lload_2
    //   50: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   53: return
    //   54: astore_1
    //   55: aload 4
    //   57: monitorexit
    //   58: aload_1
    //   59: athrow
    //   60: astore_1
    //   61: lload_2
    //   62: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   65: aload_1
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	VibratorService
    //   0	67	1	paramIBinder	IBinder
    //   16	46	2	l	long
    // Exception table:
    //   from	to	target	type
    //   26	46	54	finally
    //   17	26	60	finally
    //   46	49	60	finally
    //   55	60	60	finally
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump vibrator service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    paramPrintWriter.println("Previous vibrations:");
    synchronized (this.mVibrations)
    {
      paramArrayOfString = this.mPreviousVibrations.iterator();
      if (paramArrayOfString.hasNext())
      {
        VibrationInfo localVibrationInfo = (VibrationInfo)paramArrayOfString.next();
        paramPrintWriter.print("  ");
        paramPrintWriter.println(localVibrationInfo.toString());
      }
    }
  }
  
  public boolean hasVibrator()
  {
    return doVibratorExists();
  }
  
  public void onInputDeviceAdded(int paramInt)
  {
    updateInputDeviceVibrators();
  }
  
  public void onInputDeviceChanged(int paramInt)
  {
    updateInputDeviceVibrators();
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    updateInputDeviceVibrators();
  }
  
  public void systemReady()
  {
    this.mIm = ((InputManager)this.mContext.getSystemService(InputManager.class));
    this.mSettingObserver = new SettingsObserver(this.mH);
    this.mPowerManagerInternal = ((PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class));
    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener()
    {
      public void onLowPowerModeChanged(boolean paramAnonymousBoolean)
      {
        VibratorService.-wrap4(VibratorService.this);
      }
    });
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("vibrate_input_devices"), true, this.mSettingObserver, -1);
    this.mContext.registerReceiver(new BroadcastReceiver()new IntentFilter
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        VibratorService.-wrap4(VibratorService.this);
      }
    }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mH);
    updateInputDeviceVibrators();
  }
  
  /* Error */
  public void vibrate(int paramInt1, String arg2, long paramLong, int paramInt2, IBinder paramIBinder)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 183	com/android/server/VibratorService:mContext	Landroid/content/Context;
    //   4: ldc_w 589
    //   7: invokevirtual 611	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 680	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 682
    //   20: invokespecial 683	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_0
    //   25: iload_1
    //   26: invokespecial 685	com/android/server/VibratorService:verifyIncomingUid	(I)V
    //   29: lload_3
    //   30: lconst_0
    //   31: lcmp
    //   32: ifle +21 -> 53
    //   35: aload_0
    //   36: getfield 88	com/android/server/VibratorService:mCurrentVibration	Lcom/android/server/VibratorService$Vibration;
    //   39: ifnull +15 -> 54
    //   42: aload_0
    //   43: getfield 88	com/android/server/VibratorService:mCurrentVibration	Lcom/android/server/VibratorService$Vibration;
    //   46: lload_3
    //   47: invokevirtual 689	com/android/server/VibratorService$Vibration:hasLongerTimeout	(J)Z
    //   50: ifeq +4 -> 54
    //   53: return
    //   54: aload_0
    //   55: sipush 1276
    //   58: invokespecial 691	com/android/server/VibratorService:WriteNodeValue	(I)V
    //   61: new 22	com/android/server/VibratorService$Vibration
    //   64: dup
    //   65: aload_0
    //   66: aload 6
    //   68: lload_3
    //   69: ldc_w 693
    //   72: iload 5
    //   74: iload_1
    //   75: aload_2
    //   76: invokespecial 696	com/android/server/VibratorService$Vibration:<init>	(Lcom/android/server/VibratorService;Landroid/os/IBinder;JLjava/lang/String;IILjava/lang/String;)V
    //   79: astore 7
    //   81: invokestatic 598	android/os/Binder:clearCallingIdentity	()J
    //   84: lstore_3
    //   85: aload_0
    //   86: getfield 101	com/android/server/VibratorService:mVibrations	Ljava/util/LinkedList;
    //   89: astore_2
    //   90: aload_2
    //   91: monitorenter
    //   92: aload_0
    //   93: aload 6
    //   95: invokespecial 600	com/android/server/VibratorService:removeVibrationLocked	(Landroid/os/IBinder;)Lcom/android/server/VibratorService$Vibration;
    //   98: pop
    //   99: aload_0
    //   100: invokespecial 113	com/android/server/VibratorService:doCancelVibrateLocked	()V
    //   103: aload_0
    //   104: aload 7
    //   106: invokespecial 698	com/android/server/VibratorService:addToPreviousVibrationsLocked	(Lcom/android/server/VibratorService$Vibration;)V
    //   109: aload_0
    //   110: aload 7
    //   112: invokespecial 464	com/android/server/VibratorService:startVibrationLocked	(Lcom/android/server/VibratorService$Vibration;)V
    //   115: aload_2
    //   116: monitorexit
    //   117: lload_3
    //   118: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   121: return
    //   122: astore 6
    //   124: aload_2
    //   125: monitorexit
    //   126: aload 6
    //   128: athrow
    //   129: astore_2
    //   130: lload_3
    //   131: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   134: aload_2
    //   135: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	VibratorService
    //   0	136	1	paramInt1	int
    //   0	136	3	paramLong	long
    //   0	136	5	paramInt2	int
    //   0	136	6	paramIBinder	IBinder
    //   79	32	7	localVibration	Vibration
    // Exception table:
    //   from	to	target	type
    //   92	115	122	finally
    //   85	92	129	finally
    //   115	117	129	finally
    //   124	129	129	finally
  }
  
  /* Error */
  public void vibratePattern(int paramInt1, String arg2, long[] paramArrayOfLong, int paramInt2, int paramInt3, IBinder paramIBinder)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 183	com/android/server/VibratorService:mContext	Landroid/content/Context;
    //   4: ldc_w 589
    //   7: invokevirtual 611	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 680	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 682
    //   20: invokespecial 683	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_0
    //   25: iload_1
    //   26: invokespecial 685	com/android/server/VibratorService:verifyIncomingUid	(I)V
    //   29: invokestatic 598	android/os/Binder:clearCallingIdentity	()J
    //   32: lstore 8
    //   34: aload_3
    //   35: ifnull +12 -> 47
    //   38: aload_3
    //   39: arraylength
    //   40: istore 7
    //   42: iload 7
    //   44: ifne +9 -> 53
    //   47: lload 8
    //   49: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: return
    //   53: aload_0
    //   54: aload_3
    //   55: invokespecial 702	com/android/server/VibratorService:isAll0	([J)Z
    //   58: ifne -11 -> 47
    //   61: iload 4
    //   63: aload_3
    //   64: arraylength
    //   65: if_icmpge -18 -> 47
    //   68: aload 6
    //   70: ifnull -23 -> 47
    //   73: ldc_w 693
    //   76: astore 10
    //   78: aload_3
    //   79: iconst_0
    //   80: laload
    //   81: ldc2_w 703
    //   84: lcmp
    //   85: ifne +100 -> 185
    //   88: ldc_w 706
    //   91: astore 10
    //   93: aload_0
    //   94: sipush 812
    //   97: invokespecial 691	com/android/server/VibratorService:WriteNodeValue	(I)V
    //   100: aload_3
    //   101: aload_3
    //   102: iconst_0
    //   103: laload
    //   104: invokestatic 712	com/android/internal/util/ArrayUtils:removeLong	([JJ)[J
    //   107: astore_3
    //   108: new 22	com/android/server/VibratorService$Vibration
    //   111: dup
    //   112: aload_0
    //   113: aload 6
    //   115: aload_3
    //   116: aload 10
    //   118: iload 4
    //   120: iload 5
    //   122: iload_1
    //   123: aload_2
    //   124: invokespecial 715	com/android/server/VibratorService$Vibration:<init>	(Lcom/android/server/VibratorService;Landroid/os/IBinder;[JLjava/lang/String;IIILjava/lang/String;)V
    //   127: astore_3
    //   128: aload 6
    //   130: aload_3
    //   131: iconst_0
    //   132: invokeinterface 719 3 0
    //   137: aload_0
    //   138: getfield 101	com/android/server/VibratorService:mVibrations	Ljava/util/LinkedList;
    //   141: astore_2
    //   142: aload_2
    //   143: monitorenter
    //   144: aload_0
    //   145: aload 6
    //   147: invokespecial 600	com/android/server/VibratorService:removeVibrationLocked	(Landroid/os/IBinder;)Lcom/android/server/VibratorService$Vibration;
    //   150: pop
    //   151: aload_0
    //   152: invokespecial 113	com/android/server/VibratorService:doCancelVibrateLocked	()V
    //   155: iload 4
    //   157: iflt +114 -> 271
    //   160: aload_0
    //   161: getfield 101	com/android/server/VibratorService:mVibrations	Ljava/util/LinkedList;
    //   164: aload_3
    //   165: invokevirtual 722	java/util/LinkedList:addFirst	(Ljava/lang/Object;)V
    //   168: aload_0
    //   169: invokespecial 123	com/android/server/VibratorService:startNextVibrationLocked	()V
    //   172: aload_0
    //   173: aload_3
    //   174: invokespecial 698	com/android/server/VibratorService:addToPreviousVibrationsLocked	(Lcom/android/server/VibratorService$Vibration;)V
    //   177: aload_2
    //   178: monitorexit
    //   179: lload 8
    //   181: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   184: return
    //   185: aload_3
    //   186: iconst_0
    //   187: laload
    //   188: ldc2_w 723
    //   191: lcmp
    //   192: ifne +21 -> 213
    //   195: aload_0
    //   196: sipush 1508
    //   199: invokespecial 691	com/android/server/VibratorService:WriteNodeValue	(I)V
    //   202: aload_3
    //   203: aload_3
    //   204: iconst_0
    //   205: laload
    //   206: invokestatic 712	com/android/internal/util/ArrayUtils:removeLong	([JJ)[J
    //   209: astore_3
    //   210: goto -102 -> 108
    //   213: aload_3
    //   214: iconst_0
    //   215: laload
    //   216: ldc2_w 725
    //   219: lcmp
    //   220: ifne +26 -> 246
    //   223: ldc_w 728
    //   226: astore 10
    //   228: aload_0
    //   229: sipush 3596
    //   232: invokespecial 691	com/android/server/VibratorService:WriteNodeValue	(I)V
    //   235: aload_3
    //   236: aload_3
    //   237: iconst_0
    //   238: laload
    //   239: invokestatic 712	com/android/internal/util/ArrayUtils:removeLong	([JJ)[J
    //   242: astore_3
    //   243: goto -135 -> 108
    //   246: aload_0
    //   247: sipush 1276
    //   250: invokespecial 691	com/android/server/VibratorService:WriteNodeValue	(I)V
    //   253: goto -145 -> 108
    //   256: astore_2
    //   257: lload 8
    //   259: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   262: aload_2
    //   263: athrow
    //   264: astore_2
    //   265: lload 8
    //   267: invokestatic 603	android/os/Binder:restoreCallingIdentity	(J)V
    //   270: return
    //   271: aload_0
    //   272: aload_3
    //   273: invokespecial 464	com/android/server/VibratorService:startVibrationLocked	(Lcom/android/server/VibratorService$Vibration;)V
    //   276: goto -104 -> 172
    //   279: astore_3
    //   280: aload_2
    //   281: monitorexit
    //   282: aload_3
    //   283: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	VibratorService
    //   0	284	1	paramInt1	int
    //   0	284	3	paramArrayOfLong	long[]
    //   0	284	4	paramInt2	int
    //   0	284	5	paramInt3	int
    //   0	284	6	paramIBinder	IBinder
    //   40	3	7	i	int
    //   32	234	8	l	long
    //   76	151	10	str	String
    // Exception table:
    //   from	to	target	type
    //   38	42	256	finally
    //   53	68	256	finally
    //   93	108	256	finally
    //   108	128	256	finally
    //   128	137	256	finally
    //   137	144	256	finally
    //   177	179	256	finally
    //   195	210	256	finally
    //   228	243	256	finally
    //   246	253	256	finally
    //   280	284	256	finally
    //   128	137	264	android/os/RemoteException
    //   144	155	279	finally
    //   160	172	279	finally
    //   172	177	279	finally
    //   271	276	279	finally
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      VibratorService.-wrap4(VibratorService.this);
    }
  }
  
  private class VibrateThread
    extends Thread
  {
    boolean mDone;
    final VibratorService.Vibration mVibration;
    
    VibrateThread(VibratorService.Vibration paramVibration)
    {
      this.mVibration = paramVibration;
      VibratorService.-get2(VibratorService.this).set(VibratorService.Vibration.-get6(paramVibration));
      VibratorService.-get4(VibratorService.this).setWorkSource(VibratorService.-get2(VibratorService.this));
      VibratorService.-get4(VibratorService.this).acquire();
    }
    
    private void delay(long paramLong)
    {
      long l2;
      long l1;
      if (paramLong > 0L)
      {
        l2 = SystemClock.uptimeMillis();
        l1 = paramLong;
      }
      for (;;)
      {
        try
        {
          wait(l1);
          if (this.mDone) {
            return;
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          continue;
          l1 = paramLong + l2 - SystemClock.uptimeMillis();
          if (l1 <= 0L) {}
        }
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: bipush -8
      //   2: invokestatic 73	android/os/Process:setThreadPriority	(I)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 22	com/android/server/VibratorService$VibrateThread:mVibration	Lcom/android/server/VibratorService$Vibration;
      //   11: invokestatic 77	com/android/server/VibratorService$Vibration:-get1	(Lcom/android/server/VibratorService$Vibration;)[J
      //   14: astore 12
      //   16: aload 12
      //   18: arraylength
      //   19: istore 5
      //   21: aload_0
      //   22: getfield 22	com/android/server/VibratorService$VibrateThread:mVibration	Lcom/android/server/VibratorService$Vibration;
      //   25: invokestatic 79	com/android/server/VibratorService$Vibration:-get2	(Lcom/android/server/VibratorService$Vibration;)I
      //   28: istore_3
      //   29: aload_0
      //   30: getfield 22	com/android/server/VibratorService$VibrateThread:mVibration	Lcom/android/server/VibratorService$Vibration;
      //   33: invokestatic 32	com/android/server/VibratorService$Vibration:-get6	(Lcom/android/server/VibratorService$Vibration;)I
      //   36: istore 6
      //   38: aload_0
      //   39: getfield 22	com/android/server/VibratorService$VibrateThread:mVibration	Lcom/android/server/VibratorService$Vibration;
      //   42: invokestatic 82	com/android/server/VibratorService$Vibration:-get7	(Lcom/android/server/VibratorService$Vibration;)I
      //   45: istore 7
      //   47: lconst_0
      //   48: lstore 8
      //   50: iconst_0
      //   51: istore_1
      //   52: aload_0
      //   53: getfield 67	com/android/server/VibratorService$VibrateThread:mDone	Z
      //   56: ifne +197 -> 253
      //   59: lload 8
      //   61: lstore 10
      //   63: iload_1
      //   64: istore_2
      //   65: iload_1
      //   66: iload 5
      //   68: if_icmpge +16 -> 84
      //   71: lload 8
      //   73: aload 12
      //   75: iload_1
      //   76: laload
      //   77: ladd
      //   78: lstore 10
      //   80: iload_1
      //   81: iconst_1
      //   82: iadd
      //   83: istore_2
      //   84: aload_0
      //   85: lload 10
      //   87: invokespecial 84	com/android/server/VibratorService$VibrateThread:delay	(J)V
      //   90: aload_0
      //   91: getfield 67	com/android/server/VibratorService$VibrateThread:mDone	Z
      //   94: ifeq +75 -> 169
      //   97: aload_0
      //   98: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   101: invokestatic 42	com/android/server/VibratorService:-get4	(Lcom/android/server/VibratorService;)Landroid/os/PowerManager$WakeLock;
      //   104: invokevirtual 87	android/os/PowerManager$WakeLock:release	()V
      //   107: aload_0
      //   108: monitorexit
      //   109: aload_0
      //   110: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   113: invokestatic 91	com/android/server/VibratorService:-get3	(Lcom/android/server/VibratorService;)Ljava/util/LinkedList;
      //   116: astore 12
      //   118: aload 12
      //   120: monitorenter
      //   121: aload_0
      //   122: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   125: getfield 95	com/android/server/VibratorService:mThread	Lcom/android/server/VibratorService$VibrateThread;
      //   128: aload_0
      //   129: if_acmpne +11 -> 140
      //   132: aload_0
      //   133: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   136: aconst_null
      //   137: putfield 95	com/android/server/VibratorService:mThread	Lcom/android/server/VibratorService$VibrateThread;
      //   140: aload_0
      //   141: getfield 67	com/android/server/VibratorService$VibrateThread:mDone	Z
      //   144: ifne +21 -> 165
      //   147: aload_0
      //   148: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   151: aload_0
      //   152: getfield 22	com/android/server/VibratorService$VibrateThread:mVibration	Lcom/android/server/VibratorService$Vibration;
      //   155: invokestatic 98	com/android/server/VibratorService:-wrap3	(Lcom/android/server/VibratorService;Lcom/android/server/VibratorService$Vibration;)V
      //   158: aload_0
      //   159: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   162: invokestatic 102	com/android/server/VibratorService:-wrap2	(Lcom/android/server/VibratorService;)V
      //   165: aload 12
      //   167: monitorexit
      //   168: return
      //   169: iload_2
      //   170: iload 5
      //   172: if_icmpge +51 -> 223
      //   175: iload_2
      //   176: iconst_1
      //   177: iadd
      //   178: istore 4
      //   180: aload 12
      //   182: iload_2
      //   183: laload
      //   184: lstore 10
      //   186: lload 10
      //   188: lstore 8
      //   190: iload 4
      //   192: istore_1
      //   193: lload 10
      //   195: lconst_0
      //   196: lcmp
      //   197: ifle +23 -> 220
      //   200: aload_0
      //   201: getfield 17	com/android/server/VibratorService$VibrateThread:this$0	Lcom/android/server/VibratorService;
      //   204: lload 10
      //   206: iload 6
      //   208: iload 7
      //   210: invokestatic 106	com/android/server/VibratorService:-wrap1	(Lcom/android/server/VibratorService;JII)V
      //   213: iload 4
      //   215: istore_1
      //   216: lload 10
      //   218: lstore 8
      //   220: goto -168 -> 52
      //   223: iload_3
      //   224: ifge +6 -> 230
      //   227: goto -130 -> 97
      //   230: iload_3
      //   231: istore_1
      //   232: lconst_0
      //   233: lstore 8
      //   235: goto -15 -> 220
      //   238: astore 12
      //   240: aload_0
      //   241: monitorexit
      //   242: aload 12
      //   244: athrow
      //   245: astore 13
      //   247: aload 12
      //   249: monitorexit
      //   250: aload 13
      //   252: athrow
      //   253: goto -156 -> 97
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	256	0	this	VibrateThread
      //   51	181	1	i	int
      //   64	119	2	j	int
      //   28	203	3	k	int
      //   178	36	4	m	int
      //   19	154	5	n	int
      //   36	171	6	i1	int
      //   45	164	7	i2	int
      //   48	186	8	l1	long
      //   61	156	10	l2	long
      //   238	10	12	localObject2	Object
      //   245	6	13	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   7	47	238	finally
      //   52	59	238	finally
      //   84	97	238	finally
      //   97	107	238	finally
      //   200	213	238	finally
      //   121	140	245	finally
      //   140	165	245	finally
    }
  }
  
  private class Vibration
    implements IBinder.DeathRecipient
  {
    private final String mOpPkg;
    private final long[] mPattern;
    private final int mRepeat;
    private final long mStartTime;
    private final long mTimeout;
    private final IBinder mToken;
    private final int mUid;
    private final int mUsageHint;
    private String mVibrateIntensity = "Middle";
    
    Vibration(IBinder paramIBinder, long paramLong, int paramInt1, int paramInt2, String paramString)
    {
      this(paramIBinder, paramLong, null, 0, paramInt1, paramInt2, paramString);
    }
    
    Vibration(IBinder paramIBinder, long paramLong, String paramString1, int paramInt1, int paramInt2, String paramString2)
    {
      this(paramIBinder, paramLong, null, paramString1, 0, paramInt1, paramInt2, paramString2);
    }
    
    private Vibration(IBinder paramIBinder, long paramLong, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, String paramString)
    {
      this.mToken = paramIBinder;
      this.mTimeout = paramLong;
      this.mStartTime = SystemClock.uptimeMillis();
      this.mPattern = paramArrayOfLong;
      this.mRepeat = paramInt1;
      this.mUsageHint = paramInt2;
      this.mUid = paramInt3;
      this.mOpPkg = paramString;
    }
    
    private Vibration(IBinder paramIBinder, long paramLong, long[] paramArrayOfLong, String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
    {
      this.mToken = paramIBinder;
      this.mTimeout = paramLong;
      this.mStartTime = SystemClock.uptimeMillis();
      this.mPattern = paramArrayOfLong;
      this.mVibrateIntensity = paramString1;
      this.mRepeat = paramInt1;
      this.mUsageHint = paramInt2;
      this.mUid = paramInt3;
      this.mOpPkg = paramString2;
    }
    
    Vibration(IBinder paramIBinder, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, String paramString)
    {
      this(paramIBinder, 0L, paramArrayOfLong, paramInt1, paramInt2, paramInt3, paramString);
    }
    
    Vibration(IBinder paramIBinder, long[] paramArrayOfLong, String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
    {
      this(paramIBinder, 0L, paramArrayOfLong, paramString1, paramInt1, paramInt2, paramInt3, paramString2);
    }
    
    public void binderDied()
    {
      synchronized (VibratorService.-get3(VibratorService.this))
      {
        VibratorService.-get3(VibratorService.this).remove(this);
        if (this == VibratorService.-get0(VibratorService.this))
        {
          VibratorService.-wrap0(VibratorService.this);
          VibratorService.-wrap2(VibratorService.this);
        }
        return;
      }
    }
    
    public boolean hasLongerTimeout(long paramLong)
    {
      if (this.mTimeout == 0L) {
        return false;
      }
      return this.mStartTime + this.mTimeout >= SystemClock.uptimeMillis() + paramLong;
    }
    
    public boolean isSystemHapticFeedback()
    {
      boolean bool2 = false;
      if ((this.mUid == 1000) || (this.mUid == 0)) {}
      for (;;)
      {
        boolean bool1 = bool2;
        if (this.mRepeat < 0) {
          bool1 = true;
        }
        do
        {
          return bool1;
          bool1 = bool2;
        } while (!"com.android.systemui".equals(this.mOpPkg));
      }
    }
  }
  
  private static class VibrationInfo
  {
    String opPkg;
    long[] pattern;
    int repeat;
    long startTime;
    long timeout;
    int uid;
    int usageHint;
    String vibrateIntensity = "Middle";
    
    public VibrationInfo(long paramLong1, long paramLong2, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, String paramString)
    {
      this.timeout = paramLong1;
      this.startTime = paramLong2;
      this.pattern = paramArrayOfLong;
      this.repeat = paramInt1;
      this.usageHint = paramInt2;
      this.uid = paramInt3;
      this.opPkg = paramString;
    }
    
    public VibrationInfo(long paramLong1, long paramLong2, long[] paramArrayOfLong, String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
    {
      this.vibrateIntensity = paramString1;
      this.timeout = paramLong1;
      this.startTime = paramLong2;
      this.pattern = paramArrayOfLong;
      this.repeat = paramInt1;
      this.usageHint = paramInt2;
      this.uid = paramInt3;
      this.opPkg = paramString2;
    }
    
    public String toString()
    {
      return "timeout: " + this.timeout + ", startTime: " + this.startTime + ", pattern: " + Arrays.toString(this.pattern) + ", vibrateIntensity: " + this.vibrateIntensity + ", repeat: " + this.repeat + ", usageHint: " + this.usageHint + ", uid: " + this.uid + ", opPkg: " + this.opPkg;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/VibratorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */