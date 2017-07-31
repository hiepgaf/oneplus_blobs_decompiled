package com.amap.api.mapcore2d;

public class u
{
  private long a = Long.MIN_VALUE;
  private long b = Long.MIN_VALUE;
  private double c = Double.MIN_VALUE;
  private double d = Double.MIN_VALUE;
  
  public u()
  {
    this.a = 0L;
    this.b = 0L;
  }
  
  private u(double paramDouble1, double paramDouble2, long paramLong1, long paramLong2)
  {
    this.c = paramDouble1;
    this.d = paramDouble2;
    this.a = paramLong1;
    this.b = paramLong2;
  }
  
  u(double paramDouble1, double paramDouble2, boolean paramBoolean)
  {
    if (paramBoolean != true)
    {
      this.c = paramDouble1;
      this.d = paramDouble2;
      return;
    }
    this.a = ((paramDouble1 * 1000000.0D));
    this.b = ((paramDouble2 * 1000000.0D));
  }
  
  public u(int paramInt1, int paramInt2)
  {
    this.a = paramInt1;
    this.b = paramInt2;
  }
  
  public int a()
  {
    return (int)this.b;
  }
  
  public void a(double paramDouble)
  {
    this.d = paramDouble;
  }
  
  public int b()
  {
    return (int)this.a;
  }
  
  public void b(double paramDouble)
  {
    this.c = paramDouble;
  }
  
  public long c()
  {
    return this.b;
  }
  
  public long d()
  {
    return this.a;
  }
  
  public double e()
  {
    if (Double.doubleToLongBits(this.d) == Double.doubleToLongBits(Double.MIN_VALUE)) {
      this.d = (q.a(this.b) * 2.003750834E7D / 180.0D);
    }
    return this.d;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this != paramObject)
    {
      if (paramObject != null)
      {
        if (getClass() != paramObject.getClass()) {
          break label43;
        }
        paramObject = (u)paramObject;
        if (this.a == ((u)paramObject).a) {
          break label45;
        }
        return false;
      }
    }
    else {
      return true;
    }
    return false;
    label43:
    return false;
    label45:
    if (this.b != ((u)paramObject).b) {
      return false;
    }
    if (Double.doubleToLongBits(this.c) != Double.doubleToLongBits(((u)paramObject).c)) {
      return false;
    }
    return Double.doubleToLongBits(this.d) == Double.doubleToLongBits(((u)paramObject).d);
  }
  
  public double f()
  {
    if (Double.doubleToLongBits(this.c) == Double.doubleToLongBits(Double.MIN_VALUE)) {
      this.c = (Math.log(Math.tan((q.a(this.a) + 90.0D) * 3.141592653589793D / 360.0D)) / 0.017453292519943295D * 2.003750834E7D / 180.0D);
    }
    return this.c;
  }
  
  public u g()
  {
    return new u(this.c, this.d, this.a, this.b);
  }
  
  public int hashCode()
  {
    int i = (int)(this.a ^ this.a >>> 32);
    int j = (int)(this.b ^ this.b >>> 32);
    long l = Double.doubleToLongBits(this.c);
    int k = (int)(l ^ l >>> 32);
    l = Double.doubleToLongBits(this.d);
    return (((i + 31) * 31 + j) * 31 + k) * 31 + (int)(l ^ l >>> 32);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/u.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */