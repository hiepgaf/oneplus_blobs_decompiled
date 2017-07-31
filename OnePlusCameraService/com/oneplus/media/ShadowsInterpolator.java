package com.oneplus.media;

public class ShadowsInterpolator
  implements ColorInterpolator
{
  private static final float[] NEGATIVE_LEVEL_COEFFICIENT = { -5.2885F, 17.242F, -20.672F, 10.773F, -1.0567F, 0.0035F };
  private static final float[] POSITIVE_LEVEL_COEFFICIENT = { 3.2051F, -11.509F, 15.009F, -8.4164F, 2.7146F, -0.0045F };
  private float m_Level;
  
  private double evaluateMaxValue(double paramDouble)
  {
    double d = 0.0D;
    int i = 0;
    while (i <= 5)
    {
      d += POSITIVE_LEVEL_COEFFICIENT[i] * Math.pow(paramDouble, 5 - i);
      i += 1;
    }
    paramDouble = d;
    if (d > 1.0D) {
      paramDouble = 1.0D;
    }
    return paramDouble;
  }
  
  private double evaluateMinValue(double paramDouble)
  {
    double d = 0.0D;
    int i = 0;
    while (i <= 5)
    {
      d += NEGATIVE_LEVEL_COEFFICIENT[i] * Math.pow(paramDouble, 5 - i);
      i += 1;
    }
    paramDouble = d;
    if (d < 0.0D) {
      paramDouble = 0.0D;
    }
    return paramDouble;
  }
  
  public float getInterpolation(float paramFloat)
  {
    double d1 = 0.998D;
    if (Math.abs(this.m_Level) <= 0.001F) {
      return paramFloat;
    }
    if (this.m_Level < 0.0F)
    {
      d2 = evaluateMinValue(paramFloat);
      if (paramFloat < 0.998D) {
        d1 = paramFloat;
      }
      d1 = d1 * (this.m_Level + 1.0F) - this.m_Level * d2;
      return (float)d1;
    }
    double d2 = evaluateMaxValue(paramFloat);
    if (paramFloat > 0.00196D) {}
    for (d1 = paramFloat;; d1 = 0.00196D)
    {
      d1 = d1 * (1.0F - this.m_Level) + this.m_Level * d2;
      break;
    }
  }
  
  public void setLevel(float paramFloat)
  {
    this.m_Level = paramFloat;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ShadowsInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */