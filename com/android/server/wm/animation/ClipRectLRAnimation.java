package com.android.server.wm.animation;

import android.graphics.Rect;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Transformation;

public class ClipRectLRAnimation
  extends ClipRectAnimation
{
  public ClipRectLRAnimation(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramInt1, 0, paramInt2, 0, paramInt3, 0, paramInt4, 0);
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    Rect localRect = paramTransformation.getClipRect();
    paramTransformation.setClipRect(this.mFromRect.left + (int)((this.mToRect.left - this.mFromRect.left) * paramFloat), localRect.top, this.mFromRect.right + (int)((this.mToRect.right - this.mFromRect.right) * paramFloat), localRect.bottom);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/animation/ClipRectLRAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */