package com.amap.api.mapcore2d;

import android.graphics.PointF;

class bp
  implements Cloneable
{
  public int a = 0;
  public final int b;
  public final int c;
  public final int d;
  public final int e;
  public final boolean f;
  public PointF g;
  public int h = -1;
  public boolean i = false;
  private String j;
  
  public bp(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.b = paramInt1;
    this.c = paramInt2;
    this.d = paramInt3;
    this.e = paramInt4;
    if (ci.a(this.b, this.c, this.d)) {}
    for (;;)
    {
      this.f = bool;
      b();
      return;
      bool = true;
    }
  }
  
  public bp(bp parambp)
  {
    this.b = parambp.b;
    this.c = parambp.c;
    this.d = parambp.d;
    this.e = parambp.e;
    this.g = parambp.g;
    this.a = parambp.a;
    if (ci.a(this.b, this.c, this.d)) {}
    for (;;)
    {
      this.f = bool;
      b();
      return;
      bool = true;
    }
  }
  
  public bp a()
  {
    return new bp(this);
  }
  
  public void b()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.b);
    localStringBuilder.append("-");
    localStringBuilder.append(this.c);
    localStringBuilder.append("-");
    localStringBuilder.append(this.d);
    if (!this.f) {}
    for (;;)
    {
      this.j = localStringBuilder.toString();
      return;
      if (p.i == 1) {
        localStringBuilder.append("-").append(1);
      }
    }
  }
  
  public String c()
  {
    return this.j;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this != paramObject)
    {
      if (!(paramObject instanceof bp)) {
        break label36;
      }
      paramObject = (bp)paramObject;
      if (this.b == ((bp)paramObject).b) {
        break label38;
      }
    }
    for (;;)
    {
      bool = false;
      label36:
      label38:
      do
      {
        return bool;
        return true;
        return false;
        if ((this.c != ((bp)paramObject).c) || (this.d != ((bp)paramObject).d)) {
          break;
        }
      } while (this.e == ((bp)paramObject).e);
    }
  }
  
  public int hashCode()
  {
    return this.b * 7 + this.c * 11 + this.d * 13 + this.e;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.b);
    localStringBuilder.append("-");
    localStringBuilder.append(this.c);
    localStringBuilder.append("-");
    localStringBuilder.append(this.d);
    localStringBuilder.append("-");
    localStringBuilder.append(this.e);
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */