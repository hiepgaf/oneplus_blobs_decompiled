package com.android.server.policy.keyguard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.policy.IKeyguardService;
import com.android.internal.policy.IKeyguardStateCallback.Stub;
import com.android.internal.widget.LockPatternUtils;
import java.io.PrintWriter;

public class KeyguardStateMonitor
  extends IKeyguardStateCallback.Stub
{
  private static final String TAG = "KeyguardStateMonitor";
  private int mCurrentUserId;
  private volatile boolean mFingerprintAuthenticating = false;
  private volatile boolean mHasLockscreenWallpaper = false;
  private volatile boolean mInputRestricted = true;
  private volatile boolean mIsShowing = true;
  private Runnable mKeyguardFingerprintChangeCallback;
  private final LockPatternUtils mLockPatternUtils;
  private final OnShowingStateChangedCallback mOnShowingStateChangedCallback;
  private volatile boolean mSimSecure = true;
  private volatile boolean mTrusted = false;
  
  public KeyguardStateMonitor(Context paramContext, IKeyguardService paramIKeyguardService, OnShowingStateChangedCallback paramOnShowingStateChangedCallback, Runnable paramRunnable)
  {
    this.mKeyguardFingerprintChangeCallback = paramRunnable;
    this.mLockPatternUtils = new LockPatternUtils(paramContext);
    this.mCurrentUserId = ActivityManager.getCurrentUser();
    this.mOnShowingStateChangedCallback = paramOnShowingStateChangedCallback;
    try
    {
      paramIKeyguardService.addStateMonitorCallback(this);
      return;
    }
    catch (RemoteException paramContext)
    {
      Slog.w("KeyguardStateMonitor", "Remote Exception", paramContext);
    }
  }
  
  private int getCurrentUser()
  {
    try
    {
      int i = this.mCurrentUserId;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "KeyguardStateMonitor");
    paramString = paramString + "  ";
    paramPrintWriter.println(paramString + "mIsShowing=" + this.mIsShowing);
    paramPrintWriter.println(paramString + "mSimSecure=" + this.mSimSecure);
    paramPrintWriter.println(paramString + "mInputRestricted=" + this.mInputRestricted);
    paramPrintWriter.println(paramString + "mTrusted=" + this.mTrusted);
    paramPrintWriter.println(paramString + "mCurrentUserId=" + this.mCurrentUserId);
    paramPrintWriter.println(paramString + "mFingerprintAuthenticating=" + this.mFingerprintAuthenticating);
    paramPrintWriter.println(paramString + "mCurrentUserId=" + this.mCurrentUserId);
  }
  
  public boolean hasLockscreenWallpaper()
  {
    return this.mHasLockscreenWallpaper;
  }
  
  public boolean isFingerprintAuthenticating()
  {
    return this.mFingerprintAuthenticating;
  }
  
  public boolean isInputRestricted()
  {
    return this.mInputRestricted;
  }
  
  public boolean isSecure(int paramInt)
  {
    if (!this.mLockPatternUtils.isSecure(paramInt)) {
      return this.mSimSecure;
    }
    return true;
  }
  
  public boolean isShowing()
  {
    return this.mIsShowing;
  }
  
  public boolean isTrusted()
  {
    return this.mTrusted;
  }
  
  public void onFingerprintStateChange(boolean paramBoolean)
  {
    Slog.d("KeyguardStateMonitor", "onFingerprintStateChange , " + paramBoolean);
    if ((this.mFingerprintAuthenticating == paramBoolean) || (paramBoolean)) {}
    for (;;)
    {
      this.mFingerprintAuthenticating = paramBoolean;
      return;
      if (this.mKeyguardFingerprintChangeCallback != null) {
        this.mKeyguardFingerprintChangeCallback.run();
      }
    }
  }
  
  public void onHasLockscreenWallpaperChanged(boolean paramBoolean)
  {
    this.mHasLockscreenWallpaper = paramBoolean;
  }
  
  public void onInputRestrictedStateChanged(boolean paramBoolean)
  {
    this.mInputRestricted = paramBoolean;
  }
  
  public void onShowingStateChanged(boolean paramBoolean)
  {
    this.mIsShowing = paramBoolean;
    this.mOnShowingStateChangedCallback.onShowingStateChanged(paramBoolean);
  }
  
  public void onSimSecureStateChanged(boolean paramBoolean)
  {
    this.mSimSecure = paramBoolean;
  }
  
  public void onTrustedChanged(boolean paramBoolean)
  {
    this.mTrusted = paramBoolean;
  }
  
  public void setCurrentUser(int paramInt)
  {
    try
    {
      this.mCurrentUserId = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static abstract interface OnShowingStateChangedCallback
  {
    public abstract void onShowingStateChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/keyguard/KeyguardStateMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */