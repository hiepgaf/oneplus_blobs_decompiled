package com.oneplus.camera.manual;

public class KnobInfo
{
  public final int angleMax;
  public final int angleMin;
  public final int autoAngle;
  public final int tickMax;
  public final int tickMin;
  
  public KnobInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, 0);
  }
  
  public KnobInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.angleMax = paramInt2;
    this.angleMin = paramInt1;
    this.tickMax = paramInt4;
    this.tickMin = paramInt3;
    this.autoAngle = paramInt5;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/KnobInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */