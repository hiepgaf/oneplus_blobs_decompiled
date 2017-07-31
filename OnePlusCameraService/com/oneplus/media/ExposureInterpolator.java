package com.oneplus.media;

public class ExposureInterpolator
  implements ColorInterpolator
{
  private ContrastInterpolator m_ContrastInterpolator = new ContrastInterpolator();
  private float m_EV;
  
  public float getInterpolation(float paramFloat)
  {
    double d1 = paramFloat;
    if (Math.abs(this.m_EV) <= 0.001F) {
      return paramFloat;
    }
    if (this.m_EV > 0.0F)
    {
      d2 = 1.0D / Math.pow(2.0D, this.m_EV);
      if (d1 > 0.00196D) {}
      for (;;)
      {
        d1 = Math.pow(d1, d2);
        d1 = this.m_ContrastInterpolator.getInterpolation((float)d1);
        return (float)d1;
        d1 = 0.00196D;
      }
    }
    double d2 = this.m_EV * 0.15F + 1.0F;
    double d3 = d2 / Math.pow(2.0D, this.m_EV);
    if (paramFloat < 0.998D) {}
    for (d1 = paramFloat;; d1 = 0.998D)
    {
      d1 = Math.pow(d1, d3) * d2;
      break;
    }
  }
  
  public void setEV(float paramFloat)
  {
    this.m_EV = paramFloat;
    if (paramFloat > 0.0F)
    {
      this.m_ContrastInterpolator.setLevel((float)(Math.tanh(paramFloat) / 1.1D));
      return;
    }
    this.m_ContrastInterpolator.setLevel(0.0F);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ExposureInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */