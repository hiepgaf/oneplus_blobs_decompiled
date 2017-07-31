package com.android.server.policy;

import android.view.animation.Interpolator;

public class LogDecelerateInterpolator
  implements Interpolator
{
  private int mBase;
  private int mDrift;
  private final float mLogScale;
  
  public LogDecelerateInterpolator(int paramInt1, int paramInt2)
  {
    this.mBase = paramInt1;
    this.mDrift = paramInt2;
    this.mLogScale = (1.0F / computeLog(1.0F, this.mBase, this.mDrift));
  }
  
  private static float computeLog(float paramFloat, int paramInt1, int paramInt2)
  {
    return (float)-Math.pow(paramInt1, -paramFloat) + 1.0F + paramInt2 * paramFloat;
  }
  
  public float getInterpolation(float paramFloat)
  {
    return computeLog(paramFloat, this.mBase, this.mDrift) * this.mLogScale;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/LogDecelerateInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */