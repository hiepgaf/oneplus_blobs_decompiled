package com.android.server.display;

import android.animation.ValueAnimator;
import android.util.IntProperty;
import android.view.Choreographer;
import com.android.server.power.PowerManagerService;

final class RampAnimator<T>
{
  private float mAnimatedValue;
  private boolean mAnimating;
  private final Runnable mAnimationCallback = new Runnable()
  {
    public void run()
    {
      long l = RampAnimator.-get1(RampAnimator.this).getFrameTimeNanos();
      float f2 = (float)(l - RampAnimator.-get3(RampAnimator.this));
      RampAnimator.-set3(RampAnimator.this, l);
      float f1;
      if (PowerManagerService.sBrightnessNoAnimation)
      {
        f1 = 0.0F;
        if (f1 != 0.0F) {
          break label157;
        }
        RampAnimator.-set0(RampAnimator.this, RampAnimator.-get8(RampAnimator.this));
        label64:
        int i = RampAnimator.-get2(RampAnimator.this);
        RampAnimator.-set2(RampAnimator.this, Math.round(RampAnimator.-get0(RampAnimator.this)));
        if (i != RampAnimator.-get2(RampAnimator.this)) {
          RampAnimator.-get6(RampAnimator.this).setValue(RampAnimator.-get5(RampAnimator.this), RampAnimator.-get2(RampAnimator.this));
        }
        if (RampAnimator.-get8(RampAnimator.this) == RampAnimator.-get2(RampAnimator.this)) {
          break label277;
        }
        RampAnimator.-wrap0(RampAnimator.this);
      }
      label157:
      label277:
      do
      {
        return;
        f1 = ValueAnimator.getDurationScale();
        break;
        f2 = RampAnimator.-get7(RampAnimator.this) * (f2 * 1.0E-9F) / f1;
        if (RampAnimator.-get8(RampAnimator.this) > RampAnimator.-get2(RampAnimator.this))
        {
          RampAnimator.-set0(RampAnimator.this, Math.min(RampAnimator.-get0(RampAnimator.this) + f2, RampAnimator.-get8(RampAnimator.this)));
          break label64;
        }
        f1 = f2;
        if (RampAnimator.-get0(RampAnimator.this) <= 30.0F)
        {
          f1 = f2;
          if (f2 < 1.0F) {
            f1 = 1.0F;
          }
        }
        RampAnimator.-set0(RampAnimator.this, Math.max(RampAnimator.-get0(RampAnimator.this) - f1, RampAnimator.-get8(RampAnimator.this)));
        break label64;
        RampAnimator.-set1(RampAnimator.this, false);
      } while (RampAnimator.-get4(RampAnimator.this) == null);
      RampAnimator.-get4(RampAnimator.this).onAnimationEnd();
    }
  };
  private final Choreographer mChoreographer;
  private int mCurrentValue;
  private boolean mFirstTime = true;
  private long mLastFrameTimeNanos;
  private Listener mListener;
  private final T mObject;
  private final IntProperty<T> mProperty;
  private int mRate;
  private int mTargetValue;
  
  public RampAnimator(T paramT, IntProperty<T> paramIntProperty)
  {
    this.mObject = paramT;
    this.mProperty = paramIntProperty;
    this.mChoreographer = Choreographer.getInstance();
  }
  
  private void cancelAnimationCallback()
  {
    this.mChoreographer.removeCallbacks(1, this.mAnimationCallback, null);
  }
  
  private void postAnimationCallback()
  {
    this.mChoreographer.postCallback(1, this.mAnimationCallback, null);
  }
  
  public boolean animateTo(int paramInt1, int paramInt2)
  {
    if ((this.mFirstTime) || (paramInt2 <= 0))
    {
      if ((this.mFirstTime) || (paramInt1 != this.mCurrentValue))
      {
        this.mFirstTime = false;
        this.mRate = 0;
        this.mTargetValue = paramInt1;
        this.mCurrentValue = paramInt1;
        this.mProperty.setValue(this.mObject, paramInt1);
        if (this.mAnimating)
        {
          this.mAnimating = false;
          cancelAnimationCallback();
        }
        if (this.mListener != null) {
          this.mListener.onAnimationEnd();
        }
        return true;
      }
      return false;
    }
    if ((!this.mAnimating) || (paramInt2 > this.mRate))
    {
      this.mRate = paramInt2;
      label114:
      if ((this.mRate == 6) && (Math.abs(paramInt1 - this.mCurrentValue) > 30)) {
        this.mRate = 30;
      }
      if (this.mTargetValue == paramInt1) {
        break label241;
      }
    }
    label241:
    for (boolean bool = true;; bool = false)
    {
      this.mTargetValue = paramInt1;
      if ((!this.mAnimating) && (paramInt1 != this.mCurrentValue))
      {
        this.mAnimating = true;
        this.mAnimatedValue = this.mCurrentValue;
        this.mLastFrameTimeNanos = System.nanoTime();
        postAnimationCallback();
      }
      return bool;
      if ((paramInt1 <= this.mCurrentValue) && (this.mCurrentValue <= this.mTargetValue)) {
        break;
      }
      if ((this.mTargetValue > this.mCurrentValue) || (this.mCurrentValue > paramInt1)) {
        break label114;
      }
      break;
    }
  }
  
  public boolean isAnimating()
  {
    return this.mAnimating;
  }
  
  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public static abstract interface Listener
  {
    public abstract void onAnimationEnd();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/RampAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */