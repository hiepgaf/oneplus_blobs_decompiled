package com.android.server.policy;

import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal.AppTransitionListener;
import android.view.WindowManagerPolicy.WindowState;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import com.android.server.LocalServices;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.util.List;

public class StatusBarController
  extends BarController
{
  private static final long TRANSITION_DURATION = 120L;
  private final WindowManagerInternal.AppTransitionListener mAppTransitionListener = new WindowManagerInternal.AppTransitionListener()
  {
    public void onAppTransitionCancelledLocked()
    {
      StatusBarController.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerInternal localStatusBarManagerInternal = StatusBarController.this.getStatusBarInternal();
          if (localStatusBarManagerInternal != null) {
            localStatusBarManagerInternal.appTransitionCancelled();
          }
        }
      });
    }
    
    public void onAppTransitionFinishedLocked(IBinder paramAnonymousIBinder)
    {
      StatusBarController.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerInternal localStatusBarManagerInternal = (StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class);
          if (localStatusBarManagerInternal != null) {
            localStatusBarManagerInternal.appTransitionFinished();
          }
        }
      });
    }
    
    public void onAppTransitionPendingLocked()
    {
      StatusBarController.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerInternal localStatusBarManagerInternal = StatusBarController.this.getStatusBarInternal();
          if (localStatusBarManagerInternal != null) {
            localStatusBarManagerInternal.appTransitionPending();
          }
        }
      });
    }
    
    public void onAppTransitionStartingLocked(IBinder paramAnonymousIBinder1, IBinder paramAnonymousIBinder2, final Animation paramAnonymousAnimation1, final Animation paramAnonymousAnimation2)
    {
      StatusBarController.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerInternal localStatusBarManagerInternal = StatusBarController.this.getStatusBarInternal();
          long l2;
          if (localStatusBarManagerInternal != null)
          {
            l2 = StatusBarController.-wrap0(paramAnonymousAnimation1, paramAnonymousAnimation2);
            if ((paramAnonymousAnimation2 == null) && (paramAnonymousAnimation1 == null)) {
              break label57;
            }
          }
          label57:
          for (long l1 = 120L;; l1 = 0L)
          {
            localStatusBarManagerInternal.appTransitionStarting(l2, l1);
            return;
          }
        }
      });
    }
  };
  
  public StatusBarController()
  {
    super("StatusBar", 67108864, 268435456, 1073741824, 1, 67108864, 8);
  }
  
  private static long calculateStatusBarTransitionStartTime(Animation paramAnimation1, Animation paramAnimation2)
  {
    if ((paramAnimation1 != null) && (paramAnimation2 != null))
    {
      paramAnimation1 = findTranslateAnimation(paramAnimation1);
      paramAnimation2 = findTranslateAnimation(paramAnimation2);
      if (paramAnimation1 != null)
      {
        float f = findAlmostThereFraction(paramAnimation1.getInterpolator());
        return SystemClock.uptimeMillis() + paramAnimation1.getStartOffset() + ((float)paramAnimation1.getDuration() * f) - 120L;
      }
      if (paramAnimation2 != null) {
        return SystemClock.uptimeMillis();
      }
      return SystemClock.uptimeMillis();
    }
    return SystemClock.uptimeMillis();
  }
  
  private static float findAlmostThereFraction(Interpolator paramInterpolator)
  {
    float f1 = 0.5F;
    float f2 = 0.25F;
    if (f2 >= 0.01F)
    {
      if (paramInterpolator.getInterpolation(f1) < 0.99F) {}
      for (f1 += f2;; f1 -= f2)
      {
        f2 /= 2.0F;
        break;
      }
    }
    return f1;
  }
  
  private static TranslateAnimation findTranslateAnimation(Animation paramAnimation)
  {
    if ((paramAnimation instanceof TranslateAnimation)) {
      return (TranslateAnimation)paramAnimation;
    }
    if ((paramAnimation instanceof AnimationSet))
    {
      paramAnimation = (AnimationSet)paramAnimation;
      int i = 0;
      while (i < paramAnimation.getAnimations().size())
      {
        Animation localAnimation = (Animation)paramAnimation.getAnimations().get(i);
        if ((localAnimation instanceof TranslateAnimation)) {
          return (TranslateAnimation)localAnimation;
        }
        i += 1;
      }
    }
    return null;
  }
  
  public WindowManagerInternal.AppTransitionListener getAppTransitionListener()
  {
    return this.mAppTransitionListener;
  }
  
  protected boolean skipAnimation()
  {
    return this.mWin.getAttrs().height == -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/StatusBarController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */