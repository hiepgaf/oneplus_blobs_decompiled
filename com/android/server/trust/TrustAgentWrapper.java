package com.android.server.trust;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.trust.ITrustAgentService;
import android.service.trust.ITrustAgentService.Stub;
import android.service.trust.ITrustAgentServiceCallback;
import android.service.trust.ITrustAgentServiceCallback.Stub;
import android.util.Log;
import android.util.Slog;
import java.util.Collections;
import java.util.List;

public class TrustAgentWrapper
{
  private static final String DATA_DURATION = "duration";
  private static final boolean DEBUG = false;
  private static final String EXTRA_COMPONENT_NAME = "componentName";
  private static final int MSG_GRANT_TRUST = 1;
  private static final int MSG_MANAGING_TRUST = 6;
  private static final int MSG_RESTART_TIMEOUT = 4;
  private static final int MSG_REVOKE_TRUST = 2;
  private static final int MSG_SET_TRUST_AGENT_FEATURES_COMPLETED = 5;
  private static final int MSG_TRUST_TIMEOUT = 3;
  private static final String PERMISSION = "android.permission.PROVIDE_TRUST_AGENT";
  private static final long RESTART_TIMEOUT_MILLIS = 300000L;
  private static final String TAG = "TrustAgentWrapper";
  private static final String TRUST_EXPIRED_ACTION = "android.server.trust.TRUST_EXPIRED_ACTION";
  private final Intent mAlarmIntent;
  private AlarmManager mAlarmManager;
  private PendingIntent mAlarmPendingIntent;
  private boolean mBound;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = (ComponentName)paramAnonymousIntent.getParcelableExtra("componentName");
      if (("android.server.trust.TRUST_EXPIRED_ACTION".equals(paramAnonymousIntent.getAction())) && (TrustAgentWrapper.-get10(TrustAgentWrapper.this).equals(paramAnonymousContext)))
      {
        TrustAgentWrapper.-get6(TrustAgentWrapper.this).removeMessages(3);
        TrustAgentWrapper.-get6(TrustAgentWrapper.this).sendEmptyMessage(3);
      }
    }
  };
  private ITrustAgentServiceCallback mCallback = new ITrustAgentServiceCallback.Stub()
  {
    public void grantTrust(CharSequence paramAnonymousCharSequence, long paramAnonymousLong, int paramAnonymousInt)
    {
      paramAnonymousCharSequence = TrustAgentWrapper.-get6(TrustAgentWrapper.this).obtainMessage(1, paramAnonymousInt, 0, paramAnonymousCharSequence);
      paramAnonymousCharSequence.getData().putLong("duration", paramAnonymousLong);
      paramAnonymousCharSequence.sendToTarget();
    }
    
    public void onConfigureCompleted(boolean paramAnonymousBoolean, IBinder paramAnonymousIBinder)
    {
      Handler localHandler = TrustAgentWrapper.-get6(TrustAgentWrapper.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(5, i, 0, paramAnonymousIBinder).sendToTarget();
        return;
      }
    }
    
    public void revokeTrust()
    {
      TrustAgentWrapper.-get6(TrustAgentWrapper.this).sendEmptyMessage(2);
    }
    
    public void setManagingTrust(boolean paramAnonymousBoolean)
    {
      Handler localHandler = TrustAgentWrapper.-get6(TrustAgentWrapper.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(6, i, 0).sendToTarget();
        return;
      }
    }
  };
  private final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      TrustAgentWrapper.-get6(TrustAgentWrapper.this).removeMessages(4);
      TrustAgentWrapper.-set5(TrustAgentWrapper.this, ITrustAgentService.Stub.asInterface(paramAnonymousIBinder));
      TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive.logAgentConnected(TrustAgentWrapper.-get15(TrustAgentWrapper.this), paramAnonymousComponentName);
      TrustAgentWrapper.-wrap2(TrustAgentWrapper.this, TrustAgentWrapper.-get4(TrustAgentWrapper.this));
      TrustAgentWrapper.this.updateDevicePolicyFeatures();
      if (TrustAgentWrapper.-get11(TrustAgentWrapper.this))
      {
        TrustAgentWrapper.this.onUnlockAttempt(true);
        TrustAgentWrapper.-set3(TrustAgentWrapper.this, false);
      }
      if (TrustAgentWrapper.-get14(TrustAgentWrapper.this).isDeviceLockedInner(TrustAgentWrapper.-get15(TrustAgentWrapper.this)))
      {
        TrustAgentWrapper.this.onDeviceLocked();
        return;
      }
      TrustAgentWrapper.this.onDeviceUnlocked();
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      TrustAgentWrapper.-set5(TrustAgentWrapper.this, null);
      TrustAgentWrapper.-set1(TrustAgentWrapper.this, false);
      TrustAgentWrapper.-set4(TrustAgentWrapper.this, null);
      TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive.logAgentDied(TrustAgentWrapper.-get15(TrustAgentWrapper.this), paramAnonymousComponentName);
      TrustAgentWrapper.-get6(TrustAgentWrapper.this).sendEmptyMessage(2);
      if (TrustAgentWrapper.-get3(TrustAgentWrapper.this)) {
        TrustAgentWrapper.-wrap1(TrustAgentWrapper.this);
      }
    }
  };
  private final Context mContext;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        if (!TrustAgentWrapper.this.isConnected())
        {
          Log.w("TrustAgentWrapper", "Agent is not connected, cannot grant trust: " + TrustAgentWrapper.-get10(TrustAgentWrapper.this).flattenToShortString());
          return;
        }
        TrustAgentWrapper.-set7(TrustAgentWrapper.this, true);
        TrustAgentWrapper.-set2(TrustAgentWrapper.this, (CharSequence)paramAnonymousMessage.obj);
        i = paramAnonymousMessage.arg1;
        long l2 = paramAnonymousMessage.getData().getLong("duration");
        long l1;
        int j;
        ComponentName localComponentName;
        if (l2 > 0L)
        {
          if (TrustAgentWrapper.-get8(TrustAgentWrapper.this) != 0L)
          {
            l1 = Math.min(l2, TrustAgentWrapper.-get8(TrustAgentWrapper.this));
            long l3 = SystemClock.elapsedRealtime();
            TrustAgentWrapper.-set0(TrustAgentWrapper.this, PendingIntent.getBroadcast(TrustAgentWrapper.-get5(TrustAgentWrapper.this), 0, TrustAgentWrapper.-get0(TrustAgentWrapper.this), 268435456));
            TrustAgentWrapper.-get1(TrustAgentWrapper.this).set(2, l3 + l1, TrustAgentWrapper.-get2(TrustAgentWrapper.this));
          }
        }
        else
        {
          localObject = TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive;
          j = TrustAgentWrapper.-get15(TrustAgentWrapper.this);
          localComponentName = TrustAgentWrapper.-get10(TrustAgentWrapper.this);
          if (TrustAgentWrapper.-get9(TrustAgentWrapper.this) == null) {
            break label309;
          }
        }
        for (paramAnonymousMessage = TrustAgentWrapper.-get9(TrustAgentWrapper.this).toString();; paramAnonymousMessage = null)
        {
          ((TrustArchive)localObject).logGrantTrust(j, localComponentName, paramAnonymousMessage, l2, i);
          TrustAgentWrapper.-get14(TrustAgentWrapper.this).updateTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), i);
          return;
          l1 = l2;
          break;
        }
      case 3: 
        TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive.logTrustTimeout(TrustAgentWrapper.-get15(TrustAgentWrapper.this), TrustAgentWrapper.-get10(TrustAgentWrapper.this));
        TrustAgentWrapper.-wrap0(TrustAgentWrapper.this);
      case 2: 
        TrustAgentWrapper.-set7(TrustAgentWrapper.this, false);
        TrustAgentWrapper.-set2(TrustAgentWrapper.this, null);
        TrustAgentWrapper.-get6(TrustAgentWrapper.this).removeMessages(3);
        if (paramAnonymousMessage.what == 2) {
          TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive.logRevokeTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), TrustAgentWrapper.-get10(TrustAgentWrapper.this));
        }
        TrustAgentWrapper.-get14(TrustAgentWrapper.this).updateTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), 0);
        return;
      case 4: 
        TrustAgentWrapper.this.destroy();
        TrustAgentWrapper.-get14(TrustAgentWrapper.this).resetAgent(TrustAgentWrapper.-get10(TrustAgentWrapper.this), TrustAgentWrapper.-get15(TrustAgentWrapper.this));
        return;
      case 5: 
        label309:
        localObject = (IBinder)paramAnonymousMessage.obj;
        if (paramAnonymousMessage.arg1 != 0) {}
        for (i = 1; TrustAgentWrapper.-get12(TrustAgentWrapper.this) == localObject; i = 0)
        {
          TrustAgentWrapper.-set4(TrustAgentWrapper.this, null);
          if ((!TrustAgentWrapper.-get13(TrustAgentWrapper.this)) || (i == 0)) {
            break;
          }
          TrustAgentWrapper.-set6(TrustAgentWrapper.this, false);
          TrustAgentWrapper.-get14(TrustAgentWrapper.this).updateTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), 0);
          return;
        }
      }
      Object localObject = TrustAgentWrapper.this;
      if (paramAnonymousMessage.arg1 != 0) {}
      for (boolean bool = true;; bool = false)
      {
        TrustAgentWrapper.-set1((TrustAgentWrapper)localObject, bool);
        if (!TrustAgentWrapper.-get7(TrustAgentWrapper.this))
        {
          TrustAgentWrapper.-set7(TrustAgentWrapper.this, false);
          TrustAgentWrapper.-set2(TrustAgentWrapper.this, null);
        }
        TrustAgentWrapper.-get14(TrustAgentWrapper.this).mArchive.logManagingTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), TrustAgentWrapper.-get10(TrustAgentWrapper.this), TrustAgentWrapper.-get7(TrustAgentWrapper.this));
        TrustAgentWrapper.-get14(TrustAgentWrapper.this).updateTrust(TrustAgentWrapper.-get15(TrustAgentWrapper.this), 0);
        return;
      }
    }
  };
  private boolean mManagingTrust;
  private long mMaximumTimeToLock;
  private CharSequence mMessage;
  private final ComponentName mName;
  private boolean mPendingSuccessfulUnlock = false;
  private long mScheduledRestartUptimeMillis;
  private IBinder mSetTrustAgentFeaturesToken;
  private ITrustAgentService mTrustAgentService;
  private boolean mTrustDisabledByDpm;
  private final TrustManagerService mTrustManagerService;
  private boolean mTrusted;
  private final int mUserId;
  
  public TrustAgentWrapper(Context paramContext, TrustManagerService paramTrustManagerService, Intent paramIntent, UserHandle paramUserHandle)
  {
    this.mContext = paramContext;
    this.mTrustManagerService = paramTrustManagerService;
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    this.mUserId = paramUserHandle.getIdentifier();
    this.mName = paramIntent.getComponent();
    this.mAlarmIntent = new Intent("android.server.trust.TRUST_EXPIRED_ACTION").putExtra("componentName", this.mName);
    this.mAlarmIntent.setData(Uri.parse(this.mAlarmIntent.toUri(1)));
    this.mAlarmIntent.setPackage(paramContext.getPackageName());
    paramTrustManagerService = new IntentFilter("android.server.trust.TRUST_EXPIRED_ACTION");
    paramTrustManagerService.addDataScheme(this.mAlarmIntent.getScheme());
    paramTrustManagerService.addDataPath(this.mAlarmIntent.toUri(1), 0);
    scheduleRestart();
    this.mBound = paramContext.bindServiceAsUser(paramIntent, this.mConnection, 67108865, paramUserHandle);
    if (this.mBound)
    {
      this.mContext.registerReceiver(this.mBroadcastReceiver, paramTrustManagerService, "android.permission.PROVIDE_TRUST_AGENT", null);
      return;
    }
    Log.e("TrustAgentWrapper", "Can't bind to TrustAgent " + this.mName.flattenToShortString());
  }
  
  private void onError(Exception paramException)
  {
    Slog.w("TrustAgentWrapper", "Remote Exception", paramException);
  }
  
  private void onTrustTimeout()
  {
    try
    {
      if (this.mTrustAgentService != null) {
        this.mTrustAgentService.onTrustTimeout();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      onError(localRemoteException);
    }
  }
  
  private void scheduleRestart()
  {
    this.mHandler.removeMessages(4);
    this.mScheduledRestartUptimeMillis = (SystemClock.uptimeMillis() + 300000L);
    this.mHandler.sendEmptyMessageAtTime(4, this.mScheduledRestartUptimeMillis);
  }
  
  private void setCallback(ITrustAgentServiceCallback paramITrustAgentServiceCallback)
  {
    try
    {
      if (this.mTrustAgentService != null) {
        this.mTrustAgentService.setCallback(paramITrustAgentServiceCallback);
      }
      return;
    }
    catch (RemoteException paramITrustAgentServiceCallback)
    {
      onError(paramITrustAgentServiceCallback);
    }
  }
  
  public void destroy()
  {
    this.mHandler.removeMessages(4);
    if (!this.mBound) {
      return;
    }
    this.mTrustManagerService.mArchive.logAgentStopped(this.mUserId, this.mName);
    this.mContext.unbindService(this.mConnection);
    this.mBound = false;
    this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    this.mTrustAgentService = null;
    this.mSetTrustAgentFeaturesToken = null;
    this.mHandler.sendEmptyMessage(2);
  }
  
  public CharSequence getMessage()
  {
    return this.mMessage;
  }
  
  public long getScheduledRestartUptimeMillis()
  {
    return this.mScheduledRestartUptimeMillis;
  }
  
  public boolean isBound()
  {
    return this.mBound;
  }
  
  public boolean isConnected()
  {
    return this.mTrustAgentService != null;
  }
  
  public boolean isManagingTrust()
  {
    return (this.mManagingTrust) && (!this.mTrustDisabledByDpm);
  }
  
  public boolean isTrusted()
  {
    return (this.mTrusted) && (this.mManagingTrust) && (!this.mTrustDisabledByDpm);
  }
  
  public void onDeviceLocked()
  {
    try
    {
      if (this.mTrustAgentService != null) {
        this.mTrustAgentService.onDeviceLocked();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      onError(localRemoteException);
    }
  }
  
  public void onDeviceUnlocked()
  {
    try
    {
      if (this.mTrustAgentService != null) {
        this.mTrustAgentService.onDeviceUnlocked();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      onError(localRemoteException);
    }
  }
  
  public void onUnlockAttempt(boolean paramBoolean)
  {
    try
    {
      if (this.mTrustAgentService != null)
      {
        this.mTrustAgentService.onUnlockAttempt(paramBoolean);
        return;
      }
      this.mPendingSuccessfulUnlock = paramBoolean;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      onError(localRemoteException);
    }
  }
  
  boolean updateDevicePolicyFeatures()
  {
    bool3 = false;
    boolean bool4 = false;
    boolean bool2 = false;
    bool1 = bool4;
    for (;;)
    {
      try
      {
        if (this.mTrustAgentService != null)
        {
          bool1 = bool4;
          DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService("device_policy");
          bool1 = bool4;
          if ((localDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mUserId) & 0x10) == 0) {
            continue;
          }
          bool1 = bool4;
          List localList = localDevicePolicyManager.getTrustAgentConfiguration(null, this.mName, this.mUserId);
          bool4 = true;
          bool3 = true;
          bool2 = bool3;
          if (localList != null)
          {
            bool2 = bool3;
            bool1 = bool4;
            if (localList.size() > 0)
            {
              bool1 = bool4;
              this.mSetTrustAgentFeaturesToken = new Binder();
              bool1 = bool4;
              this.mTrustAgentService.onConfigure(localList, this.mSetTrustAgentFeaturesToken);
              bool2 = bool3;
            }
          }
          bool1 = bool2;
          long l = localDevicePolicyManager.getMaximumTimeToLockForUserAndProfiles(this.mUserId);
          bool3 = bool2;
          bool1 = bool2;
          if (l != this.mMaximumTimeToLock)
          {
            bool1 = bool2;
            this.mMaximumTimeToLock = l;
            bool3 = bool2;
            bool1 = bool2;
            if (this.mAlarmPendingIntent != null)
            {
              bool1 = bool2;
              this.mAlarmManager.cancel(this.mAlarmPendingIntent);
              bool1 = bool2;
              this.mAlarmPendingIntent = null;
              bool1 = bool2;
              this.mHandler.sendEmptyMessage(3);
              bool3 = bool2;
            }
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        onError(localRemoteException);
        bool3 = bool1;
        continue;
      }
      if (this.mTrustDisabledByDpm != bool3)
      {
        this.mTrustDisabledByDpm = bool3;
        this.mTrustManagerService.updateTrust(this.mUserId, 0);
      }
      return bool3;
      bool1 = bool4;
      this.mTrustAgentService.onConfigure(Collections.EMPTY_LIST, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/trust/TrustAgentWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */