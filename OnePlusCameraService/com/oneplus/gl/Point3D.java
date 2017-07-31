package com.oneplus.gl;

import java.util.Locale;

public class Point3D
  implements Cloneable
{
  public float x;
  public float y;
  public float z;
  
  public Point3D() {}
  
  public Point3D(float paramFloat1, float paramFloat2)
  {
    this(paramFloat1, paramFloat2, 0.0F);
  }
  
  public Point3D(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }
  
  public Point3D(Point3D paramPoint3D)
  {
    if (paramPoint3D != null)
    {
      this.x = paramPoint3D.x;
      this.y = paramPoint3D.y;
      this.z = paramPoint3D.z;
    }
  }
  
  public Point3D clone()
  {
    try
    {
      Point3D localPoint3D = (Point3D)super.clone();
      return localPoint3D;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return new Point3D(this.x, this.y, this.z);
  }
  
  public boolean equals(Point3D paramPoint3D)
  {
    boolean bool2 = false;
    if (paramPoint3D != null)
    {
      boolean bool1 = bool2;
      if (this.x == paramPoint3D.x)
      {
        bool1 = bool2;
        if (this.y == paramPoint3D.y)
        {
          bool1 = bool2;
          if (this.z == paramPoint3D.z) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Point3D)) {
      return equals((Point3D)paramObject);
    }
    return false;
  }
  
  public final Point3D set(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
    return this;
  }
  
  public final Point3D set(Point3D paramPoint3D)
  {
    if (paramPoint3D != null)
    {
      this.x = paramPoint3D.x;
      this.y = paramPoint3D.y;
      this.z = paramPoint3D.z;
      return this;
    }
    this.x = 0.0F;
    this.y = 0.0F;
    this.z = 0.0F;
    return this;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "(%.4f, %.4f, %.4f)", new Object[] { Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Point3D.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */