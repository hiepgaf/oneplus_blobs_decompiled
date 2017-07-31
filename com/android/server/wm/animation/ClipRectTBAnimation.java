package com.android.server.wm.animation;

import android.graphics.Rect;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class ClipRectTBAnimation
  extends ClipRectAnimation
{
  private final int mFromTranslateY;
  private float mNormalizedTime;
  private final int mToTranslateY;
  private final Interpolator mTranslateInterpolator;
  
  public ClipRectTBAnimation(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Interpolator paramInterpolator)
  {
    super(0, paramInt1, 0, paramInt2, 0, paramInt3, 0, paramInt4);
    this.mFromTranslateY = paramInt5;
    this.mToTranslateY = paramInt6;
    this.mTranslateInterpolator = paramInterpolator;
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    float f = this.mTranslateInterpolator.getInterpolation(this.mNormalizedTime);
    int i = (int)(this.mFromTranslateY + (this.mToTranslateY - this.mFromTranslateY) * f);
    Rect localRect = paramTransformation.getClipRect();
    paramTransformation.setClipRect(localRect.left, this.mFromRect.top - i + (int)((this.mToRect.top - this.mFromRect.top) * paramFloat), localRect.right, this.mFromRect.bottom - i + (int)((this.mToRect.bottom - this.mFromRect.bottom) * paramFloat));
  }
  
  public boolean getTransformation(long paramLong, Transformation paramTransformation)
  {
    long l1 = getStartOffset();
    long l2 = getDuration();
    float f;
    if (l2 != 0L) {
      f = (float)(paramLong - (getStartTime() + l1)) / (float)l2;
    }
    for (;;)
    {
      this.mNormalizedTime = f;
      return super.getTransformation(paramLong, paramTransformation);
      if (paramLong < getStartTime()) {
        f = 0.0F;
      } else {
        f = 1.0F;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/animation/ClipRectTBAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */