package android.animation;

import android.view.animation.AnimationUtils;

public class TimeAnimator
  extends ValueAnimator
{
  private TimeListener mListener;
  private long mPreviousTime = -1L;
  
  boolean animateBasedOnTime(long paramLong)
  {
    long l2;
    if (this.mListener != null)
    {
      l2 = this.mStartTime;
      if (this.mPreviousTime >= 0L) {
        break label46;
      }
    }
    label46:
    for (long l1 = 0L;; l1 = paramLong - this.mPreviousTime)
    {
      this.mPreviousTime = paramLong;
      this.mListener.onTimeUpdate(this, paramLong - l2, l1);
      return false;
    }
  }
  
  void animateValue(float paramFloat) {}
  
  void initAnimation() {}
  
  public void setCurrentPlayTime(long paramLong)
  {
    long l = AnimationUtils.currentAnimationTimeMillis();
    this.mStartTime = Math.max(this.mStartTime, l - paramLong);
    this.mStartTimeCommitted = true;
    animateBasedOnTime(l);
  }
  
  public void setTimeListener(TimeListener paramTimeListener)
  {
    this.mListener = paramTimeListener;
  }
  
  public void start()
  {
    this.mPreviousTime = -1L;
    super.start();
  }
  
  public static abstract interface TimeListener
  {
    public abstract void onTimeUpdate(TimeAnimator paramTimeAnimator, long paramLong1, long paramLong2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/TimeAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */