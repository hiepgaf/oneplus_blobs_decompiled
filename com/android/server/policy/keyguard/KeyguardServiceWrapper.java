package com.android.server.policy.keyguard;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardService;
import com.android.internal.policy.IKeyguardStateCallback;
import java.io.PrintWriter;

public class KeyguardServiceWrapper
  implements IKeyguardService
{
  private String TAG = "KeyguardServiceWrapper";
  private Runnable mKeyguardFingerprintChangeCallback;
  private KeyguardStateMonitor mKeyguardStateMonitor;
  private IKeyguardService mService;
  
  public KeyguardServiceWrapper(Context paramContext, IKeyguardService paramIKeyguardService, KeyguardStateMonitor.OnShowingStateChangedCallback paramOnShowingStateChangedCallback, Runnable paramRunnable)
  {
    this.mService = paramIKeyguardService;
    this.mKeyguardStateMonitor = new KeyguardStateMonitor(paramContext, paramIKeyguardService, paramOnShowingStateChangedCallback, paramRunnable);
  }
  
  public void addStateMonitorCallback(IKeyguardStateCallback paramIKeyguardStateCallback)
  {
    try
    {
      this.mService.addStateMonitorCallback(paramIKeyguardStateCallback);
      return;
    }
    catch (RemoteException paramIKeyguardStateCallback)
    {
      Slog.w(this.TAG, "Remote Exception", paramIKeyguardStateCallback);
    }
  }
  
  public IBinder asBinder()
  {
    return this.mService.asBinder();
  }
  
  public void dismiss(boolean paramBoolean)
  {
    try
    {
      this.mService.dismiss(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void doKeyguardTimeout(Bundle paramBundle)
  {
    try
    {
      this.mService.doKeyguardTimeout(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      Slog.w(this.TAG, "Remote Exception", paramBundle);
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    this.mKeyguardStateMonitor.dump(paramString, paramPrintWriter);
  }
  
  public void forceDismiss(boolean paramBoolean)
  {
    try
    {
      this.mService.forceDismiss(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public boolean hasLockscreenWallpaper()
  {
    return this.mKeyguardStateMonitor.hasLockscreenWallpaper();
  }
  
  public boolean isFingerprintAuthenticating()
  {
    return this.mKeyguardStateMonitor.isFingerprintAuthenticating();
  }
  
  public boolean isInputRestricted()
  {
    return this.mKeyguardStateMonitor.isInputRestricted();
  }
  
  public boolean isSecure(int paramInt)
  {
    return this.mKeyguardStateMonitor.isSecure(paramInt);
  }
  
  public boolean isShowing()
  {
    return this.mKeyguardStateMonitor.isShowing();
  }
  
  public boolean isTrusted()
  {
    return this.mKeyguardStateMonitor.isTrusted();
  }
  
  public void keyguardDone(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mService.keyguardDone(paramBoolean1, paramBoolean2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onActivityDrawn()
  {
    try
    {
      this.mService.onActivityDrawn();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onBootCompleted()
  {
    try
    {
      this.mService.onBootCompleted();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onDreamingStarted()
  {
    try
    {
      this.mService.onDreamingStarted();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onDreamingStopped()
  {
    try
    {
      this.mService.onDreamingStopped();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onFinishedGoingToSleep(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.onFinishedGoingToSleep(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onScreenTurnedOff()
  {
    try
    {
      this.mService.onScreenTurnedOff();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onScreenTurnedOn()
  {
    try
    {
      this.mService.onScreenTurnedOn();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onScreenTurningOn(IKeyguardDrawnCallback paramIKeyguardDrawnCallback)
  {
    try
    {
      this.mService.onScreenTurningOn(paramIKeyguardDrawnCallback);
      return;
    }
    catch (RemoteException paramIKeyguardDrawnCallback)
    {
      Slog.w(this.TAG, "Remote Exception", paramIKeyguardDrawnCallback);
    }
  }
  
  public void onStartedGoingToSleep(int paramInt)
  {
    try
    {
      this.mService.onStartedGoingToSleep(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onStartedWakingUp()
  {
    try
    {
      this.mService.onStartedWakingUp();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void onSystemReady()
  {
    try
    {
      this.mService.onSystemReady();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void setCurrentUser(int paramInt)
  {
    this.mKeyguardStateMonitor.setCurrentUser(paramInt);
    try
    {
      this.mService.setCurrentUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void setKeyguardEnabled(boolean paramBoolean)
  {
    try
    {
      this.mService.setKeyguardEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void setOccluded(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mService.setOccluded(paramBoolean1, paramBoolean2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void startKeyguardExitAnimation(long paramLong1, long paramLong2)
  {
    try
    {
      this.mService.startKeyguardExitAnimation(paramLong1, paramLong2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(this.TAG, "Remote Exception", localRemoteException);
    }
  }
  
  public void verifyUnlock(IKeyguardExitCallback paramIKeyguardExitCallback)
  {
    try
    {
      this.mService.verifyUnlock(paramIKeyguardExitCallback);
      return;
    }
    catch (RemoteException paramIKeyguardExitCallback)
    {
      Slog.w(this.TAG, "Remote Exception", paramIKeyguardExitCallback);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/keyguard/KeyguardServiceWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */