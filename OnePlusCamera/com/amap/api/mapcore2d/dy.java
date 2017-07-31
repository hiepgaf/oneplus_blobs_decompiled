package com.amap.api.mapcore2d;

import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class dy
{
  private static dy a;
  
  public static dy a()
  {
    if (a != null) {}
    for (;;)
    {
      return a;
      a = new dy();
    }
  }
  
  public eg a(ee paramee, boolean paramBoolean)
    throws ck
  {
    Proxy localProxy = null;
    try
    {
      b(paramee);
      if (paramee.e != null) {
        localProxy = paramee.e;
      }
      paramee = new eb(paramee.c, paramee.d, localProxy, paramBoolean).a(paramee.j(), paramee.e(), paramee.k());
      return paramee;
    }
    catch (ck paramee)
    {
      throw paramee;
    }
    catch (Throwable paramee)
    {
      paramee.printStackTrace();
      throw new ck("未知的错误");
    }
  }
  
  public byte[] a(ee paramee)
    throws ck
  {
    try
    {
      paramee = a(paramee, false);
      if (paramee == null) {
        return null;
      }
    }
    catch (ck paramee)
    {
      throw paramee;
    }
    catch (Throwable paramee)
    {
      cy.a(paramee, "BaseNetManager", "makeSyncPostRequest");
      throw new ck("未知的错误");
    }
    return paramee.a;
  }
  
  protected void b(ee paramee)
    throws ck
  {
    if (paramee != null) {
      if (paramee.g() != null) {
        break label31;
      }
    }
    label31:
    while ("".equals(paramee.g()))
    {
      throw new ck("request url is empty");
      throw new ck("requeust is null");
    }
  }
  
  public static abstract interface a
  {
    public abstract URLConnection a(Proxy paramProxy, URL paramURL);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */