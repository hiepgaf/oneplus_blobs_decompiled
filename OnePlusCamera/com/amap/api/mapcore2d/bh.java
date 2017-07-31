package com.amap.api.mapcore2d;

import com.amap.api.maps2d.AMapException;

abstract class bh<T, V>
  extends ee
{
  protected T a;
  private int b = 1;
  private int f = 0;
  
  public bh() {}
  
  public bh(T paramT)
  {
    this();
    this.a = paramT;
  }
  
  private V b(byte[] paramArrayOfByte)
    throws AMapException
  {
    return (V)a(paramArrayOfByte);
  }
  
  private V d()
    throws AMapException
  {
    try
    {
      Object localObject = b(b());
      return (V)localObject;
    }
    catch (AMapException localAMapException)
    {
      c();
      throw new AMapException(localAMapException.getErrorMessage());
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "ProtocalHandler", "GetDataMayThrow");
    }
    return null;
  }
  
  public V a()
    throws AMapException
  {
    if (this.a == null) {
      return null;
    }
    return (V)d();
  }
  
  protected abstract V a(byte[] paramArrayOfByte)
    throws AMapException;
  
  protected byte[] b()
    throws AMapException
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.b) {
        return null;
      }
      try
      {
        byte[] arrayOfByte = ed.a(false).c(this);
        return arrayOfByte;
      }
      catch (ck localck)
      {
        i += 1;
        if (i >= this.b) {
          throw new AMapException(localck.a());
        }
        try
        {
          Thread.sleep(this.f * 1000);
          cj.a(localck, "ProtocalHandler", "getData");
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new AMapException(localck.getMessage());
        }
      }
    }
  }
  
  protected V c()
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */