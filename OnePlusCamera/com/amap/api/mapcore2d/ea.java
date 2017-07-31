package com.amap.api.mapcore2d;

import java.net.Proxy;

public class ea
{
  private eb a;
  private ee b;
  
  public ea(ee paramee)
  {
    this(paramee, 0L, -1L);
  }
  
  public ea(ee paramee, long paramLong1, long paramLong2)
  {
    this.b = paramee;
    if (paramee.e != null) {
      localProxy = paramee.e;
    }
    this.a = new eb(this.b.c, this.b.d, localProxy);
    this.a.b(paramLong2);
    this.a.a(paramLong1);
  }
  
  public void a(a parama)
  {
    this.a.a(this.b.g(), this.b.e(), this.b.f(), parama);
  }
  
  public static abstract interface a
  {
    public abstract void a(Throwable paramThrowable);
    
    public abstract void a(byte[] paramArrayOfByte, long paramLong);
    
    public abstract void b();
    
    public abstract void c();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */