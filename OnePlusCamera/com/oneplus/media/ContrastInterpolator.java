package com.oneplus.media;

public class ContrastInterpolator
  implements ColorInterpolator
{
  private static final double[] NEGATIVE_LEVEL_COEFFICIENT = { 0.0D, 0.0D, 0.0D, 0.0D, 0.6D, 0.2D };
  private static final double[] POSITIVE_LEVEL_COEFFICIENT = { 6.5705D, -16.514D, 11.488D, -0.6717D, 0.1278D, -0.0012D };
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ContrastInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */