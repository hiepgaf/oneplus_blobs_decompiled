package com.android.server.wm;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.TokenWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManagerPolicy;

public class KeyguardDisableHandler
  extends Handler
{
  private static final int ALLOW_DISABLE_NO = 0;
  private static final int ALLOW_DISABLE_UNKNOWN = -1;
  private static final int ALLOW_DISABLE_YES = 1;
  static final int KEYGUARD_DISABLE = 1;
  static final int KEYGUARD_POLICY_CHANGED = 3;
  static final int KEYGUARD_REENABLE = 2;
  private static final String TAG = "WindowManager";
  private int mAllowDisableKeyguard = -1;
  final Context mContext;
  KeyguardTokenWatcher mKeyguardTokenWatcher;
  final WindowManagerPolicy mPolicy;
  
  public KeyguardDisableHandler(Context paramContext, WindowManagerPolicy paramWindowManagerPolicy)
  {
    this.mContext = paramContext;
    this.mPolicy = paramWindowManagerPolicy;
  }
  
  public void handleMessage(Message paramMessage)
  {
    if (this.mKeyguardTokenWatcher == null) {
      this.mKeyguardTokenWatcher = new KeyguardTokenWatcher(this);
    }
    switch (paramMessage.what)
    {
    }
    do
    {
      return;
      paramMessage = (Pair)paramMessage.obj;
      this.mKeyguardTokenWatcher.acquire((IBinder)paramMessage.first, (String)paramMessage.second);
      return;
      this.mKeyguardTokenWatcher.release((IBinder)paramMessage.obj);
      return;
      this.mAllowDisableKeyguard = -1;
      if (!this.mKeyguardTokenWatcher.isAcquired()) {
        break;
      }
      this.mKeyguardTokenWatcher.updateAllowState();
    } while (this.mAllowDisableKeyguard == 1);
    this.mPolicy.enableKeyguard(true);
    return;
    this.mPolicy.enableKeyguard(true);
  }
  
  class KeyguardTokenWatcher
    extends TokenWatcher
  {
    public KeyguardTokenWatcher(Handler paramHandler)
    {
      super(KeyguardDisableHandler.-get0());
    }
    
    public void acquired()
    {
      if (KeyguardDisableHandler.-get1(KeyguardDisableHandler.this) == -1) {
        updateAllowState();
      }
      if (KeyguardDisableHandler.-get1(KeyguardDisableHandler.this) == 1)
      {
        KeyguardDisableHandler.this.mPolicy.enableKeyguard(false);
        return;
      }
      Log.v(KeyguardDisableHandler.-get0(), "Not disabling keyguard since device policy is enforced");
    }
    
    public void released()
    {
      KeyguardDisableHandler.this.mPolicy.enableKeyguard(true);
    }
    
    public void updateAllowState()
    {
      int i = 0;
      DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)KeyguardDisableHandler.this.mContext.getSystemService("device_policy");
      if (localDevicePolicyManager != null) {}
      try
      {
        KeyguardDisableHandler localKeyguardDisableHandler = KeyguardDisableHandler.this;
        if (localDevicePolicyManager.getPasswordQuality(null, ActivityManagerNative.getDefault().getCurrentUser().id) == 0) {
          i = 1;
        }
        KeyguardDisableHandler.-set0(localKeyguardDisableHandler, i);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/KeyguardDisableHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */