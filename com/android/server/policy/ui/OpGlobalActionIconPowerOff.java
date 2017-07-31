package com.android.server.policy.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;

public class OpGlobalActionIconPowerOff
  extends OpGlobalActionIcon
{
  private static final String TAG = "OpGlobalActionIcon";
  private int mLineAnimProgress = 0;
  
  public OpGlobalActionIconPowerOff(int paramInt, Context paramContext)
  {
    super(paramInt, paramContext);
    this.mIsAnimCircleDelayedByIcon = false;
    this.mIsArcFadeIn = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    RectF localRectF = new RectF(localRect.left + 75, localRect.top + 75, localRect.right - 75, localRect.bottom - 75);
    if (!this.mIsAnimationStarted)
    {
      paramCanvas.drawArc(localRectF, 315.0F, 270.0F, false, this.mPaint);
      paramCanvas.drawLine(localRect.centerX(), 72.0F, localRect.centerX(), localRect.centerY(), this.mPaint);
      return;
    }
    if ((this.mLineAnimProgress < 500) && (this.mLineAnimProgress / 500.0F * 27.0F < 26.0F)) {
      paramCanvas.drawLine(localRect.centerX(), 81.0F + this.mLineAnimProgress / 500.0F * 27.0F, localRect.centerX(), localRect.centerY(), this.mPaint);
    }
    drawCircle(paramCanvas);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void startAnimateConfirmed()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { 0, 500 });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIconPowerOff.this.mIsAnimationStarted = true;
        OpGlobalActionIconPowerOff.-set0(OpGlobalActionIconPowerOff.this, ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      }
    });
    localValueAnimator.setDuration((ANIM_DURATION_SCALE * 500.0F));
    localValueAnimator.setInterpolator(new DecelerateInterpolator());
    localValueAnimator.setStartDelay(400L);
    localValueAnimator.start();
    startCircleAnimation();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionIconPowerOff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */