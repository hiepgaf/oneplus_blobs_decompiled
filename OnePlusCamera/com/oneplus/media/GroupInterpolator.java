package com.oneplus.media;

public class GroupInterpolator
  implements ColorInterpolator
{
  private ColorInterpolator[] m_Interpolators;
  
  public GroupInterpolator(ColorInterpolator[] paramArrayOfColorInterpolator)
  {
    this.m_Interpolators = paramArrayOfColorInterpolator;
  }
  
  public float getInterpolation(float paramFloat)
  {
    if ((this.m_Interpolators == null) || (this.m_Interpolators.length == 0)) {
      return paramFloat;
    }
    int i = 0;
    while (i < this.m_Interpolators.length)
    {
      paramFloat = this.m_Interpolators[i].getInterpolation(paramFloat);
      i += 1;
    }
    return paramFloat;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/GroupInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */