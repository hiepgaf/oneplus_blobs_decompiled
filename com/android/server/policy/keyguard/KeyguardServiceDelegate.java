package com.android.server.policy.keyguard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy.OnKeyguardExitResult;
import com.android.internal.policy.IKeyguardDrawnCallback.Stub;
import com.android.internal.policy.IKeyguardExitCallback.Stub;
import com.android.internal.policy.IKeyguardService.Stub;
import com.android.server.UiThread;
import java.io.PrintWriter;

public class KeyguardServiceDelegate
{
  private static final boolean DEBUG = true;
  private static final int INTERACTIVE_STATE_AWAKE = 1;
  private static final int INTERACTIVE_STATE_GOING_TO_SLEEP = 2;
  private static final int INTERACTIVE_STATE_SLEEP = 0;
  private static final int SCREEN_STATE_OFF = 0;
  private static final int SCREEN_STATE_ON = 2;
  private static final int SCREEN_STATE_TURNING_ON = 1;
  private static final String TAG = "KeyguardServiceDelegate";
  private final Context mContext;
  private DrawnListener mDrawnListenerWhenConnect;
  private final ServiceConnection mKeyguardConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.v("KeyguardServiceDelegate", "*** Keyguard connected (yay!)");
      KeyguardServiceDelegate.this.mKeyguardService = new KeyguardServiceWrapper(KeyguardServiceDelegate.-get0(KeyguardServiceDelegate.this), IKeyguardService.Stub.asInterface(paramAnonymousIBinder), KeyguardServiceDelegate.-get5(KeyguardServiceDelegate.this), KeyguardServiceDelegate.-get2(KeyguardServiceDelegate.this));
      if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).systemIsReady)
      {
        KeyguardServiceDelegate.this.mKeyguardService.onSystemReady();
        if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).currentUser != 55536) {
          KeyguardServiceDelegate.this.mKeyguardService.setCurrentUser(KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).currentUser);
        }
        if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).interactiveState == 1) {
          KeyguardServiceDelegate.this.mKeyguardService.onStartedWakingUp();
        }
        if ((KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).screenState == 2) || (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).screenState == 1)) {
          KeyguardServiceDelegate.this.mKeyguardService.onScreenTurningOn(new KeyguardServiceDelegate.KeyguardShowDelegate(KeyguardServiceDelegate.this, KeyguardServiceDelegate.-get1(KeyguardServiceDelegate.this)));
        }
        if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).screenState == 2) {
          KeyguardServiceDelegate.this.mKeyguardService.onScreenTurnedOn();
        }
        KeyguardServiceDelegate.-set0(KeyguardServiceDelegate.this, null);
      }
      if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).bootCompleted) {
        KeyguardServiceDelegate.this.mKeyguardService.onBootCompleted();
      }
      if (KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).occluded) {
        KeyguardServiceDelegate.this.mKeyguardService.setOccluded(KeyguardServiceDelegate.-get3(KeyguardServiceDelegate.this).occluded, false);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.v("KeyguardServiceDelegate", "*** Keyguard disconnected (boo!)");
      KeyguardServiceDelegate.this.mKeyguardService = null;
    }
  };
  private Runnable mKeyguardFingerprintChanageCallback;
  protected KeyguardServiceWrapper mKeyguardService;
  private final KeyguardState mKeyguardState = new KeyguardState();
  private final View mScrim;
  private final Handler mScrimHandler;
  private final KeyguardStateMonitor.OnShowingStateChangedCallback mShowingStateChangedCallback;
  
  public KeyguardServiceDelegate(Context paramContext, KeyguardStateMonitor.OnShowingStateChangedCallback paramOnShowingStateChangedCallback, Runnable paramRunnable)
  {
    this.mContext = paramContext;
    this.mScrimHandler = UiThread.getHandler();
    this.mShowingStateChangedCallback = paramOnShowingStateChangedCallback;
    this.mScrim = createScrim(paramContext, this.mScrimHandler);
    this.mKeyguardFingerprintChanageCallback = paramRunnable;
  }
  
  private static View createScrim(Context paramContext, Handler paramHandler)
  {
    final View localView = new View(paramContext);
    final WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1, 2029, 67840, -3);
    localLayoutParams.softInputMode = 16;
    localLayoutParams.screenOrientation = 5;
    localLayoutParams.privateFlags |= 0x1;
    localLayoutParams.setTitle("KeyguardScrim");
    paramContext = (WindowManager)paramContext.getSystemService("window");
    localView.setSystemUiVisibility(56688640);
    paramHandler.post(new Runnable()
    {
      public void run()
      {
        this.val$wm.addView(localView, localLayoutParams);
      }
    });
    return localView;
  }
  
  public void bindService(Context arg1)
  {
    Intent localIntent = new Intent();
    ComponentName localComponentName = ComponentName.unflattenFromString(???.getApplicationContext().getResources().getString(17039466));
    localIntent.addFlags(256);
    localIntent.setComponent(localComponentName);
    if (!???.bindServiceAsUser(localIntent, this.mKeyguardConnection, 1, this.mScrimHandler, UserHandle.SYSTEM))
    {
      Log.v("KeyguardServiceDelegate", "*** Keyguard: can't bind to " + localComponentName);
      this.mKeyguardState.showing = false;
      this.mKeyguardState.showingAndNotOccluded = false;
      this.mKeyguardState.secure = false;
      synchronized (this.mKeyguardState)
      {
        this.mKeyguardState.deviceHasKeyguard = false;
        hideScrim();
        return;
      }
    }
    Log.v("KeyguardServiceDelegate", "*** Keyguard started");
  }
  
  public void dismiss(boolean paramBoolean)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.dismiss(paramBoolean);
    }
  }
  
  public void doKeyguardTimeout(Bundle paramBundle)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.doKeyguardTimeout(paramBundle);
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "KeyguardServiceDelegate");
    paramString = paramString + "  ";
    paramPrintWriter.println(paramString + "showing=" + this.mKeyguardState.showing);
    paramPrintWriter.println(paramString + "showingAndNotOccluded=" + this.mKeyguardState.showingAndNotOccluded);
    paramPrintWriter.println(paramString + "inputRestricted=" + this.mKeyguardState.inputRestricted);
    paramPrintWriter.println(paramString + "occluded=" + this.mKeyguardState.occluded);
    paramPrintWriter.println(paramString + "secure=" + this.mKeyguardState.secure);
    paramPrintWriter.println(paramString + "dreaming=" + this.mKeyguardState.dreaming);
    paramPrintWriter.println(paramString + "systemIsReady=" + this.mKeyguardState.systemIsReady);
    paramPrintWriter.println(paramString + "deviceHasKeyguard=" + this.mKeyguardState.deviceHasKeyguard);
    paramPrintWriter.println(paramString + "enabled=" + this.mKeyguardState.enabled);
    paramPrintWriter.println(paramString + "offReason=" + this.mKeyguardState.offReason);
    paramPrintWriter.println(paramString + "currentUser=" + this.mKeyguardState.currentUser);
    paramPrintWriter.println(paramString + "bootCompleted=" + this.mKeyguardState.bootCompleted);
    paramPrintWriter.println(paramString + "screenState=" + this.mKeyguardState.screenState);
    paramPrintWriter.println(paramString + "interactiveState=" + this.mKeyguardState.interactiveState);
    if (this.mKeyguardService != null) {
      this.mKeyguardService.dump(paramString, paramPrintWriter);
    }
  }
  
  public void forceDismiss(boolean paramBoolean)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.forceDismiss(paramBoolean);
    }
  }
  
  public boolean hasLockscreenWallpaper()
  {
    if (this.mKeyguardService != null) {
      return this.mKeyguardService.hasLockscreenWallpaper();
    }
    return false;
  }
  
  public void hideScrim()
  {
    this.mScrimHandler.post(new Runnable()
    {
      public void run()
      {
        KeyguardServiceDelegate.-get4(KeyguardServiceDelegate.this).setVisibility(8);
      }
    });
  }
  
  public boolean isFingerprintAuthenticating()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardState.isFingerprintAuthenticating = this.mKeyguardService.isFingerprintAuthenticating();
    }
    return this.mKeyguardState.isFingerprintAuthenticating;
  }
  
  public boolean isInputRestricted()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardState.inputRestricted = this.mKeyguardService.isInputRestricted();
    }
    return this.mKeyguardState.inputRestricted;
  }
  
  public boolean isSecure(int paramInt)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardState.secure = this.mKeyguardService.isSecure(paramInt);
    }
    return this.mKeyguardState.secure;
  }
  
  public boolean isShowing()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardState.showing = this.mKeyguardService.isShowing();
    }
    return this.mKeyguardState.showing;
  }
  
  public boolean isTrusted()
  {
    if (this.mKeyguardService != null) {
      return this.mKeyguardService.isTrusted();
    }
    return false;
  }
  
  public void keyguardDone(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.keyguardDone(paramBoolean1, paramBoolean2);
    }
  }
  
  public void onActivityDrawn()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onActivityDrawn();
    }
  }
  
  public void onBootCompleted()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onBootCompleted();
    }
    this.mKeyguardState.bootCompleted = true;
  }
  
  public void onDreamingStarted()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onDreamingStarted();
    }
    this.mKeyguardState.dreaming = true;
  }
  
  public void onDreamingStopped()
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onDreamingStopped();
    }
    this.mKeyguardState.dreaming = false;
  }
  
  public void onFinishedGoingToSleep(int paramInt, boolean paramBoolean)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onFinishedGoingToSleep(paramInt, paramBoolean);
    }
    this.mKeyguardState.interactiveState = 0;
  }
  
  public void onScreenTurnedOff()
  {
    if (this.mKeyguardService != null)
    {
      Log.v("KeyguardServiceDelegate", "onScreenTurnedOff()");
      this.mKeyguardService.onScreenTurnedOff();
    }
    this.mKeyguardState.screenState = 0;
  }
  
  public void onScreenTurnedOn()
  {
    if (this.mKeyguardService != null)
    {
      Log.v("KeyguardServiceDelegate", "onScreenTurnedOn()");
      this.mKeyguardService.onScreenTurnedOn();
    }
    this.mKeyguardState.screenState = 2;
  }
  
  public void onScreenTurningOn(DrawnListener paramDrawnListener)
  {
    if (this.mKeyguardService != null)
    {
      Log.v("KeyguardServiceDelegate", "onScreenTurnedOn(showListener = " + paramDrawnListener + ")");
      this.mKeyguardService.onScreenTurningOn(new KeyguardShowDelegate(paramDrawnListener));
    }
    for (;;)
    {
      this.mKeyguardState.screenState = 1;
      return;
      Slog.w("KeyguardServiceDelegate", "onScreenTurningOn(): no keyguard service!");
      this.mDrawnListenerWhenConnect = paramDrawnListener;
      showScrim();
    }
  }
  
  public void onStartedGoingToSleep(int paramInt)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.onStartedGoingToSleep(paramInt);
    }
    this.mKeyguardState.offReason = paramInt;
    this.mKeyguardState.interactiveState = 2;
  }
  
  public void onStartedWakingUp()
  {
    if (this.mKeyguardService != null)
    {
      Log.v("KeyguardServiceDelegate", "onStartedWakingUp()");
      this.mKeyguardService.onStartedWakingUp();
    }
    this.mKeyguardState.interactiveState = 1;
  }
  
  public void onSystemReady()
  {
    if (this.mKeyguardService != null)
    {
      this.mKeyguardService.onSystemReady();
      return;
    }
    this.mKeyguardState.systemIsReady = true;
  }
  
  public void setCurrentUser(int paramInt)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.setCurrentUser(paramInt);
    }
    this.mKeyguardState.currentUser = paramInt;
  }
  
  public void setKeyguardEnabled(boolean paramBoolean)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.setKeyguardEnabled(paramBoolean);
    }
    this.mKeyguardState.enabled = paramBoolean;
  }
  
  public void setOccluded(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mKeyguardService != null)
    {
      Log.v("KeyguardServiceDelegate", "setOccluded(" + paramBoolean1 + ") animate=" + paramBoolean2);
      this.mKeyguardService.setOccluded(paramBoolean1, paramBoolean2);
    }
    this.mKeyguardState.occluded = paramBoolean1;
  }
  
  public void showScrim()
  {
    synchronized (this.mKeyguardState)
    {
      boolean bool = this.mKeyguardState.deviceHasKeyguard;
      if (!bool) {
        return;
      }
      this.mScrimHandler.post(new Runnable()
      {
        public void run()
        {
          KeyguardServiceDelegate.-get4(KeyguardServiceDelegate.this).setVisibility(0);
        }
      });
      return;
    }
  }
  
  public void startKeyguardExitAnimation(long paramLong1, long paramLong2)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.startKeyguardExitAnimation(paramLong1, paramLong2);
    }
  }
  
  public void verifyUnlock(WindowManagerPolicy.OnKeyguardExitResult paramOnKeyguardExitResult)
  {
    if (this.mKeyguardService != null) {
      this.mKeyguardService.verifyUnlock(new KeyguardExitDelegate(paramOnKeyguardExitResult));
    }
  }
  
  public static abstract interface DrawnListener
  {
    public abstract void onDrawn();
  }
  
  private final class KeyguardExitDelegate
    extends IKeyguardExitCallback.Stub
  {
    private WindowManagerPolicy.OnKeyguardExitResult mOnKeyguardExitResult;
    
    KeyguardExitDelegate(WindowManagerPolicy.OnKeyguardExitResult paramOnKeyguardExitResult)
    {
      this.mOnKeyguardExitResult = paramOnKeyguardExitResult;
    }
    
    public void onKeyguardExitResult(boolean paramBoolean)
      throws RemoteException
    {
      Log.v("KeyguardServiceDelegate", "**** onKeyguardExitResult(" + paramBoolean + ") CALLED ****");
      if (this.mOnKeyguardExitResult != null) {
        this.mOnKeyguardExitResult.onKeyguardExitResult(paramBoolean);
      }
    }
  }
  
  private final class KeyguardShowDelegate
    extends IKeyguardDrawnCallback.Stub
  {
    private KeyguardServiceDelegate.DrawnListener mDrawnListener;
    
    KeyguardShowDelegate(KeyguardServiceDelegate.DrawnListener paramDrawnListener)
    {
      this.mDrawnListener = paramDrawnListener;
    }
    
    public void onDrawn()
      throws RemoteException
    {
      Log.v("KeyguardServiceDelegate", "**** SHOWN CALLED ****");
      if (this.mDrawnListener != null) {
        this.mDrawnListener.onDrawn();
      }
      KeyguardServiceDelegate.this.hideScrim();
    }
  }
  
  private static final class KeyguardState
  {
    public boolean bootCompleted;
    public int currentUser = 55536;
    boolean deviceHasKeyguard = true;
    boolean dreaming;
    public boolean enabled;
    boolean inputRestricted;
    public int interactiveState;
    boolean isFingerprintAuthenticating;
    boolean occluded;
    public int offReason;
    public int screenState;
    boolean secure = true;
    boolean showing = true;
    boolean showingAndNotOccluded = true;
    boolean systemIsReady;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/keyguard/KeyguardServiceDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */