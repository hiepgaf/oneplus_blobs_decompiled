package com.android.server.power;

import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.RetailDemoModeServiceInternal;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.input.InputManagerInternal;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings.Global;
import android.util.EventLog;
import android.util.Slog;
import android.view.WindowManagerPolicy;
import android.view.inputmethod.InputMethodManagerInternal;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.server.LocalServices;

final class Notifier
{
  static boolean DEBUG = false;
  private static final int INTERACTIVE_STATE_ASLEEP = 2;
  private static final int INTERACTIVE_STATE_AWAKE = 1;
  private static final int INTERACTIVE_STATE_UNKNOWN = 0;
  private static final int MSG_BROADCAST = 2;
  private static final int MSG_SCREEN_BRIGHTNESS_BOOST_CHANGED = 4;
  private static final int MSG_USER_ACTIVITY = 1;
  private static final int MSG_WIRELESS_CHARGING_STARTED = 3;
  private static final String TAG = "PowerManagerNotifier";
  private final ActivityManagerInternal mActivityManagerInternal;
  private final IAppOpsService mAppOps;
  private final IBatteryStats mBatteryStats;
  private boolean mBroadcastInProgress;
  private long mBroadcastStartTime;
  private int mBroadcastedInteractiveState;
  private final Context mContext;
  private final BroadcastReceiver mGoToSleepBroadcastDone = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (Notifier.DEBUG) {
        Slog.d("PowerManagerNotifier", "mGoToSleepBroadcastDone - sendNextBroadcast");
      }
      EventLog.writeEvent(2726, new Object[] { Integer.valueOf(0), Long.valueOf(SystemClock.uptimeMillis() - Notifier.-get1(Notifier.this)), Integer.valueOf(1) });
      Notifier.-wrap2(Notifier.this);
    }
  };
  private int mGoToSleepReason;
  private final NotifierHandler mHandler;
  private final InputManagerInternal mInputManagerInternal;
  private final InputMethodManagerInternal mInputMethodManagerInternal;
  private boolean mInteractive = true;
  private int mInteractiveChangeReason;
  private boolean mInteractiveChanging;
  private final Object mLock = new Object();
  private boolean mPendingGoToSleepBroadcast;
  private int mPendingInteractiveState;
  private boolean mPendingWakeUpBroadcast;
  private final WindowManagerPolicy mPolicy;
  private final RetailDemoModeServiceInternal mRetailDemoModeServiceInternal;
  private final BroadcastReceiver mScreeBrightnessBoostChangedDone = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Notifier.-get3(Notifier.this).release();
    }
  };
  private final Intent mScreenBrightnessBoostIntent;
  private final Intent mScreenOffIntent;
  private final Intent mScreenOnIntent;
  private final SuspendBlocker mSuspendBlocker;
  private final boolean mSuspendWhenScreenOffDueToProximityConfig;
  private boolean mUserActivityPending;
  private final BroadcastReceiver mWakeUpBroadcastDone = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (Notifier.DEBUG) {
        Slog.d("PowerManagerNotifier", "mWakeUpBroadcastDone - sendNextBroadcast");
      }
      EventLog.writeEvent(2726, new Object[] { Integer.valueOf(1), Long.valueOf(SystemClock.uptimeMillis() - Notifier.-get1(Notifier.this)), Integer.valueOf(1) });
      Notifier.-wrap2(Notifier.this);
    }
  };
  
  public Notifier(Looper paramLooper, Context paramContext, IBatteryStats paramIBatteryStats, IAppOpsService paramIAppOpsService, SuspendBlocker paramSuspendBlocker, WindowManagerPolicy paramWindowManagerPolicy)
  {
    this.mContext = paramContext;
    this.mBatteryStats = paramIBatteryStats;
    this.mAppOps = paramIAppOpsService;
    this.mSuspendBlocker = paramSuspendBlocker;
    this.mPolicy = paramWindowManagerPolicy;
    this.mActivityManagerInternal = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class));
    this.mInputManagerInternal = ((InputManagerInternal)LocalServices.getService(InputManagerInternal.class));
    this.mInputMethodManagerInternal = ((InputMethodManagerInternal)LocalServices.getService(InputMethodManagerInternal.class));
    this.mRetailDemoModeServiceInternal = ((RetailDemoModeServiceInternal)LocalServices.getService(RetailDemoModeServiceInternal.class));
    this.mHandler = new NotifierHandler(paramLooper);
    this.mScreenOnIntent = new Intent("android.intent.action.SCREEN_ON");
    this.mScreenOnIntent.addFlags(1342177280);
    this.mScreenOffIntent = new Intent("android.intent.action.SCREEN_OFF");
    this.mScreenOffIntent.addFlags(1342177280);
    this.mScreenBrightnessBoostIntent = new Intent("android.os.action.SCREEN_BRIGHTNESS_BOOST_CHANGED");
    this.mScreenBrightnessBoostIntent.addFlags(1342177280);
    this.mSuspendWhenScreenOffDueToProximityConfig = paramContext.getResources().getBoolean(17956930);
    try
    {
      this.mBatteryStats.noteInteractive(true);
      return;
    }
    catch (RemoteException paramLooper) {}
  }
  
  private void finishPendingBroadcastLocked()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "finishPendingBroadcastLocked");
    }
    this.mBroadcastInProgress = false;
    this.mSuspendBlocker.release();
  }
  
  private int getBatteryStatsWakeLockMonitorType(int paramInt)
  {
    switch (0xFFFF & paramInt)
    {
    default: 
      return -1;
    case 1: 
      return 0;
    case 6: 
    case 10: 
      return 1;
    case 32: 
      if (this.mSuspendWhenScreenOffDueToProximityConfig) {
        return -1;
      }
      return 0;
    case 128: 
      return 18;
    }
    return -1;
  }
  
  private void handleEarlyInteractiveChange()
  {
    synchronized (this.mLock)
    {
      if (this.mInteractive)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            EventLog.writeEvent(2728, new Object[] { Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
            if (Notifier.DEBUG) {
              Slog.d("PowerManagerNotifier", "handleEarlyInteractiveChange: mPolicy.startedWakingUp");
            }
            Notifier.-get2(Notifier.this).startedWakingUp();
          }
        });
        this.mPendingInteractiveState = 1;
        this.mPendingWakeUpBroadcast = true;
        updatePendingBroadcastLocked();
        return;
      }
      final int i = translateOffReason(this.mInteractiveChangeReason);
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          Notifier.-get2(Notifier.this).startedGoingToSleep(i);
        }
      });
    }
  }
  
  private void handleLateInteractiveChange()
  {
    synchronized (this.mLock)
    {
      if (this.mInteractive)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            Notifier.-get2(Notifier.this).finishedWakingUp();
          }
        });
        return;
      }
      if (this.mUserActivityPending)
      {
        this.mUserActivityPending = false;
        this.mHandler.removeMessages(1);
      }
      final int i = translateOffReason(this.mInteractiveChangeReason);
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          EventLog.writeEvent(2728, new Object[] { Integer.valueOf(0), Integer.valueOf(i), Integer.valueOf(0), Integer.valueOf(0) });
          Notifier.-get2(Notifier.this).finishedGoingToSleep(i);
        }
      });
      this.mPendingInteractiveState = 2;
      this.mPendingGoToSleepBroadcast = true;
      updatePendingBroadcastLocked();
    }
  }
  
  private void playWirelessChargingStartedSound()
  {
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "charging_sounds_enabled", 1) != 0) {}
    for (int i = 1;; i = 0)
    {
      Object localObject = Settings.Global.getString(this.mContext.getContentResolver(), "wireless_charging_started_sound");
      if ((i != 0) && (localObject != null))
      {
        localObject = Uri.parse("file://" + (String)localObject);
        if (localObject != null)
        {
          localObject = RingtoneManager.getRingtone(this.mContext, (Uri)localObject);
          if (localObject != null)
          {
            ((Ringtone)localObject).setStreamType(1);
            ((Ringtone)localObject).play();
          }
        }
      }
      this.mSuspendBlocker.release();
      return;
    }
  }
  
  private void sendBrightnessBoostChangedBroadcast()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "Sending brightness boost changed broadcast.");
    }
    this.mContext.sendOrderedBroadcastAsUser(this.mScreenBrightnessBoostIntent, UserHandle.ALL, null, this.mScreeBrightnessBoostChangedDone, this.mHandler, 0, null, null);
  }
  
  private void sendGoToSleepBroadcast()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "Sending go to sleep broadcast.");
    }
    if (ActivityManagerNative.isSystemReady())
    {
      if (DEBUG) {
        Slog.d("PowerManagerNotifier", "sendGoToSleepBroadcast - sendOrderedBroadcastAsUser");
      }
      this.mScreenOffIntent.putExtra("screenoff_reason", this.mGoToSleepReason);
      this.mContext.sendOrderedBroadcastAsUser(this.mScreenOffIntent, UserHandle.ALL, null, this.mGoToSleepBroadcastDone, this.mHandler, 0, null, null);
      return;
    }
    EventLog.writeEvent(2727, new Object[] { Integer.valueOf(3), Integer.valueOf(1) });
    sendNextBroadcast();
  }
  
  private void sendNextBroadcast()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "sendNextBroadcast mBroadcastedInteractiveState = " + this.mBroadcastedInteractiveState + ", mPendingInteractiveState = " + this.mPendingInteractiveState + ", mPendingWakeUpBroadcast = " + this.mPendingWakeUpBroadcast + ", mPendingGoToSleepBroadcast = " + this.mPendingGoToSleepBroadcast);
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.mBroadcastedInteractiveState == 0)
        {
          this.mPendingWakeUpBroadcast = false;
          this.mBroadcastedInteractiveState = 1;
          this.mBroadcastStartTime = SystemClock.uptimeMillis();
          int i = this.mBroadcastedInteractiveState;
          EventLog.writeEvent(2725, 1);
          if (i != 1) {
            break label227;
          }
          sendWakeUpBroadcast();
          return;
        }
        if (this.mBroadcastedInteractiveState != 1) {
          break label185;
        }
        if ((this.mPendingWakeUpBroadcast) || (this.mPendingGoToSleepBroadcast) || (this.mPendingInteractiveState == 2))
        {
          this.mPendingGoToSleepBroadcast = false;
          this.mBroadcastedInteractiveState = 2;
        }
      }
      finishPendingBroadcastLocked();
      return;
      label185:
      if ((!this.mPendingWakeUpBroadcast) && (!this.mPendingGoToSleepBroadcast) && (this.mPendingInteractiveState != 1)) {
        break;
      }
      this.mPendingWakeUpBroadcast = false;
      this.mBroadcastedInteractiveState = 1;
    }
    finishPendingBroadcastLocked();
    return;
    label227:
    sendGoToSleepBroadcast();
  }
  
  private void sendUserActivity()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mUserActivityPending;
      if (!bool) {
        return;
      }
      this.mUserActivityPending = false;
      if (this.mRetailDemoModeServiceInternal != null) {
        this.mRetailDemoModeServiceInternal.onUserActivity();
      }
      this.mPolicy.userActivity();
      return;
    }
  }
  
  private void sendWakeUpBroadcast()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "Sending wake up broadcast.");
    }
    if (ActivityManagerNative.isSystemReady())
    {
      if (DEBUG) {
        Slog.d("PowerManagerNotifier", "sendWakeUpBroadcast - sendOrderedBroadcastAsUser");
      }
      this.mContext.sendOrderedBroadcastAsUser(this.mScreenOnIntent, UserHandle.ALL, null, this.mWakeUpBroadcastDone, this.mHandler, 0, null, null);
      return;
    }
    EventLog.writeEvent(2727, new Object[] { Integer.valueOf(2), Integer.valueOf(1) });
    sendNextBroadcast();
  }
  
  private static int translateOffReason(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 2;
    case 1: 
      return 1;
    case 2: 
      return 3;
    case 7: 
      return 4;
    }
    return 11;
  }
  
  private void updatePendingBroadcastLocked()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "updatePendingBroadcastLocked mBroadcastInProgress = " + this.mBroadcastInProgress + ", mPendingInteractiveState = " + this.mPendingInteractiveState + ", mPendingGoToSleepBroadcast = " + this.mPendingGoToSleepBroadcast + ", mBroadcastedInteractiveState = " + this.mBroadcastedInteractiveState);
    }
    if ((!this.mBroadcastInProgress) && (this.mPendingInteractiveState != 0) && ((this.mPendingWakeUpBroadcast) || (this.mPendingGoToSleepBroadcast) || (this.mPendingInteractiveState != this.mBroadcastedInteractiveState)))
    {
      this.mBroadcastInProgress = true;
      this.mSuspendBlocker.acquire();
      Message localMessage = this.mHandler.obtainMessage(2);
      localMessage.setAsynchronous(true);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  public void onLongPartialWakeLockFinish(String paramString1, int paramInt, WorkSource paramWorkSource, String paramString2)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onLongPartialWakeLockFinish: ownerUid=" + paramInt + ", workSource=" + paramWorkSource);
    }
    if (paramWorkSource != null) {}
    try
    {
      int i = paramWorkSource.size();
      paramInt = 0;
      while (paramInt < i)
      {
        this.mBatteryStats.noteLongPartialWakelockFinish(paramString1, paramString2, paramWorkSource.get(paramInt));
        paramInt += 1;
        continue;
        this.mBatteryStats.noteLongPartialWakelockFinish(paramString1, paramString2, paramInt);
      }
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void onLongPartialWakeLockStart(String paramString1, int paramInt, WorkSource paramWorkSource, String paramString2)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onLongPartialWakeLockStart: ownerUid=" + paramInt + ", workSource=" + paramWorkSource);
    }
    if (paramWorkSource != null) {}
    try
    {
      int i = paramWorkSource.size();
      paramInt = 0;
      while (paramInt < i)
      {
        this.mBatteryStats.noteLongPartialWakelockStart(paramString1, paramString2, paramWorkSource.get(paramInt));
        paramInt += 1;
        continue;
        this.mBatteryStats.noteLongPartialWakelockStart(paramString1, paramString2, paramInt);
      }
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void onScreenBrightnessBoostChanged()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onScreenBrightnessBoostChanged");
    }
    this.mSuspendBlocker.acquire();
    Message localMessage = this.mHandler.obtainMessage(4);
    localMessage.setAsynchronous(true);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void onUserActivity(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onUserActivity: event=" + paramInt1 + ", uid=" + paramInt2);
    }
    try
    {
      this.mBatteryStats.noteUserActivity(paramInt2, paramInt1);
      synchronized (this.mLock)
      {
        if (!this.mUserActivityPending)
        {
          this.mUserActivityPending = true;
          Message localMessage = this.mHandler.obtainMessage(1);
          localMessage.setAsynchronous(true);
          this.mHandler.sendMessage(localMessage);
        }
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void onWakeLockAcquired(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, WorkSource paramWorkSource, String paramString3)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWakeLockAcquired: flags=" + paramInt1 + ", tag=\"" + paramString1 + "\", packageName=" + paramString2 + ", ownerUid=" + paramInt2 + ", ownerPid=" + paramInt3 + ", workSource=" + paramWorkSource);
    }
    int i = getBatteryStatsWakeLockMonitorType(paramInt1);
    boolean bool;
    if (i >= 0)
    {
      if (paramInt2 != 1000) {
        break label193;
      }
      if ((0x40000000 & paramInt1) == 0) {
        break label187;
      }
      bool = true;
    }
    for (;;)
    {
      if (paramWorkSource != null) {}
      try
      {
        this.mBatteryStats.noteStartWakelockFromSource(paramWorkSource, paramInt3, paramString1, paramString3, i, bool);
        return;
      }
      catch (RemoteException paramString1) {}
      this.mBatteryStats.noteStartWakelock(paramInt2, paramInt3, paramString1, paramString3, i, bool);
      this.mAppOps.startOperation(AppOpsManager.getToken(this.mAppOps), 40, paramInt2, paramString2);
      return;
      return;
      label187:
      bool = false;
      continue;
      label193:
      bool = false;
    }
  }
  
  public void onWakeLockChanging(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, WorkSource paramWorkSource1, String paramString3, int paramInt4, String paramString4, String paramString5, int paramInt5, int paramInt6, WorkSource paramWorkSource2, String paramString6)
  {
    int i = getBatteryStatsWakeLockMonitorType(paramInt1);
    int j = getBatteryStatsWakeLockMonitorType(paramInt4);
    boolean bool;
    if ((paramWorkSource1 != null) && (paramWorkSource2 != null) && (i >= 0) && (j >= 0))
    {
      if (DEBUG) {
        Slog.d("PowerManagerNotifier", "onWakeLockChanging: flags=" + paramInt4 + ", tag=\"" + paramString4 + "\", packageName=" + paramString5 + ", ownerUid=" + paramInt5 + ", ownerPid=" + paramInt6 + ", workSource=" + paramWorkSource2);
      }
      if (paramInt5 == 1000) {
        if ((0x40000000 & paramInt4) != 0) {
          bool = true;
        }
      }
    }
    for (;;)
    {
      try
      {
        this.mBatteryStats.noteChangeWakelockFromSource(paramWorkSource1, paramInt3, paramString1, paramString3, i, paramWorkSource2, paramInt6, paramString4, paramString6, j, bool);
        return;
      }
      catch (RemoteException paramString1) {}
      bool = false;
      continue;
      bool = false;
      continue;
      onWakeLockReleased(paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramWorkSource1, paramString3);
      onWakeLockAcquired(paramInt4, paramString4, paramString5, paramInt5, paramInt6, paramWorkSource2, paramString6);
      return;
    }
  }
  
  public void onWakeLockReleased(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, WorkSource paramWorkSource, String paramString3)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWakeLockReleased: flags=" + paramInt1 + ", tag=\"" + paramString1 + "\", packageName=" + paramString2 + ", ownerUid=" + paramInt2 + ", ownerPid=" + paramInt3 + ", workSource=" + paramWorkSource);
    }
    paramInt1 = getBatteryStatsWakeLockMonitorType(paramInt1);
    if (paramInt1 >= 0)
    {
      if (paramWorkSource != null) {}
      try
      {
        this.mBatteryStats.noteStopWakelockFromSource(paramWorkSource, paramInt3, paramString1, paramString3, paramInt1);
        return;
      }
      catch (RemoteException paramString1) {}
      this.mBatteryStats.noteStopWakelock(paramInt2, paramInt3, paramString1, paramString3, paramInt1);
      this.mAppOps.finishOperation(AppOpsManager.getToken(this.mAppOps), 40, paramInt2, paramString2);
      return;
    }
  }
  
  public void onWakeUp(String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWakeUp: event=" + paramString1 + ", reasonUid=" + paramInt1 + " opPackageName=" + paramString2 + " opUid=" + paramInt2);
    }
    try
    {
      this.mBatteryStats.noteWakeUp(paramString1, paramInt1);
      if (paramString2 != null) {
        this.mAppOps.noteOperation(61, paramInt2, paramString2);
      }
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void onWakefulnessChangeFinished()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWakefulnessChangeFinished");
    }
    if (this.mInteractiveChanging)
    {
      this.mInteractiveChanging = false;
      handleLateInteractiveChange();
    }
  }
  
  public void onWakefulnessChangeStarted(final int paramInt1, int paramInt2)
  {
    boolean bool = PowerManagerInternal.isInteractive(paramInt1);
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWakefulnessChangeStarted: wakefulness=" + paramInt1 + ", reason=" + paramInt2 + ", interactive=" + bool);
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Notifier.-get0(Notifier.this).onWakefulnessChanged(paramInt1);
      }
    });
    if (this.mInteractive != bool)
    {
      if (this.mInteractiveChanging) {
        handleLateInteractiveChange();
      }
      this.mInputManagerInternal.setInteractive(bool);
      this.mInputMethodManagerInternal.setInteractive(bool);
    }
    try
    {
      this.mBatteryStats.noteInteractive(bool);
      this.mInteractive = bool;
      this.mInteractiveChangeReason = paramInt2;
      if (!bool) {
        this.mGoToSleepReason = paramInt2;
      }
      this.mInteractiveChanging = true;
      handleEarlyInteractiveChange();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void onWirelessChargingStarted()
  {
    if (DEBUG) {
      Slog.d("PowerManagerNotifier", "onWirelessChargingStarted");
    }
    this.mSuspendBlocker.acquire();
    Message localMessage = this.mHandler.obtainMessage(3);
    localMessage.setAsynchronous(true);
    this.mHandler.sendMessage(localMessage);
  }
  
  private final class NotifierHandler
    extends Handler
  {
    public NotifierHandler(Looper paramLooper)
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
        Notifier.-wrap3(Notifier.this);
        return;
      case 2: 
        Notifier.-wrap2(Notifier.this);
        return;
      case 3: 
        Notifier.-wrap0(Notifier.this);
        return;
      }
      Notifier.-wrap1(Notifier.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/Notifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */