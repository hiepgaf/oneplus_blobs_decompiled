package android.app;

import android.app.trust.ITrustManager;
import android.app.trust.ITrustManager.Stub;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.IUserManager.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.view.IOnKeyguardExitResult.Stub;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;

public class KeyguardManager
{
  public static final String ACTION_CONFIRM_DEVICE_CREDENTIAL = "android.app.action.CONFIRM_DEVICE_CREDENTIAL";
  public static final String ACTION_CONFIRM_DEVICE_CREDENTIAL_WITH_USER = "android.app.action.CONFIRM_DEVICE_CREDENTIAL_WITH_USER";
  public static final String EXTRA_DESCRIPTION = "android.app.extra.DESCRIPTION";
  public static final String EXTRA_TITLE = "android.app.extra.TITLE";
  private ITrustManager mTrustManager = ITrustManager.Stub.asInterface(ServiceManager.getService("trust"));
  private IUserManager mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
  private IWindowManager mWM = WindowManagerGlobal.getWindowManagerService();
  
  private ITrustManager getTrustManager()
  {
    try
    {
      if (this.mTrustManager == null) {
        this.mTrustManager = ITrustManager.Stub.asInterface(ServiceManager.getService("trust"));
      }
      ITrustManager localITrustManager = this.mTrustManager;
      return localITrustManager;
    }
    finally {}
  }
  
  public Intent createConfirmDeviceCredentialIntent(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (!isDeviceSecure()) {
      return null;
    }
    Intent localIntent = new Intent("android.app.action.CONFIRM_DEVICE_CREDENTIAL");
    localIntent.putExtra("android.app.extra.TITLE", paramCharSequence1);
    localIntent.putExtra("android.app.extra.DESCRIPTION", paramCharSequence2);
    localIntent.setPackage("com.android.settings");
    return localIntent;
  }
  
  public Intent createConfirmDeviceCredentialIntent(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if (!isDeviceSecure(paramInt)) {
      return null;
    }
    Intent localIntent = new Intent("android.app.action.CONFIRM_DEVICE_CREDENTIAL_WITH_USER");
    localIntent.putExtra("android.app.extra.TITLE", paramCharSequence1);
    localIntent.putExtra("android.app.extra.DESCRIPTION", paramCharSequence2);
    localIntent.putExtra("android.intent.extra.USER_ID", paramInt);
    localIntent.setPackage("com.android.settings");
    return localIntent;
  }
  
  @Deprecated
  public void exitKeyguardSecurely(final OnKeyguardExitResult paramOnKeyguardExitResult)
  {
    try
    {
      this.mWM.exitKeyguardSecurely(new IOnKeyguardExitResult.Stub()
      {
        public void onKeyguardExitResult(boolean paramAnonymousBoolean)
          throws RemoteException
        {
          if (paramOnKeyguardExitResult != null) {
            paramOnKeyguardExitResult.onKeyguardExitResult(paramAnonymousBoolean);
          }
        }
      });
      return;
    }
    catch (RemoteException paramOnKeyguardExitResult) {}
  }
  
  public boolean inKeyguardRestrictedInputMode()
  {
    try
    {
      boolean bool = this.mWM.inKeyguardRestrictedInputMode();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isDeviceLocked()
  {
    return isDeviceLocked(UserHandle.getCallingUserId());
  }
  
  public boolean isDeviceLocked(int paramInt)
  {
    ITrustManager localITrustManager = getTrustManager();
    try
    {
      boolean bool = localITrustManager.isDeviceLocked(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isDeviceSecure()
  {
    return isDeviceSecure(UserHandle.getCallingUserId());
  }
  
  public boolean isDeviceSecure(int paramInt)
  {
    ITrustManager localITrustManager = getTrustManager();
    try
    {
      boolean bool = localITrustManager.isDeviceSecure(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isKeyguardLocked()
  {
    try
    {
      boolean bool = this.mWM.isKeyguardLocked();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isKeyguardSecure()
  {
    try
    {
      boolean bool = this.mWM.isKeyguardSecure();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  @Deprecated
  public KeyguardLock newKeyguardLock(String paramString)
  {
    return new KeyguardLock(paramString);
  }
  
  public class KeyguardLock
  {
    private final String mTag;
    private final IBinder mToken = new Binder();
    
    KeyguardLock(String paramString)
    {
      this.mTag = paramString;
    }
    
    public void disableKeyguard()
    {
      try
      {
        KeyguardManager.-get0(KeyguardManager.this).disableKeyguard(this.mToken, this.mTag);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void reenableKeyguard()
    {
      try
      {
        KeyguardManager.-get0(KeyguardManager.this).reenableKeyguard(this.mToken);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  public static abstract interface OnKeyguardExitResult
  {
    public abstract void onKeyguardExitResult(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/KeyguardManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */