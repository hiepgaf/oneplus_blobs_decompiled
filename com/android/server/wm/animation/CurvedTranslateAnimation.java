package com.android.server.wm.animation;

import android.animation.KeyframeSet;
import android.animation.PathKeyframes;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CurvedTranslateAnimation
  extends Animation
{
  private final PathKeyframes mKeyframes;
  
  public CurvedTranslateAnimation(Path paramPath)
  {
    this.mKeyframes = KeyframeSet.ofPath(paramPath);
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    PointF localPointF = (PointF)this.mKeyframes.getValue(paramFloat);
    paramTransformation.getMatrix().setTranslate(localPointF.x, localPointF.y);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/animation/CurvedTranslateAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */