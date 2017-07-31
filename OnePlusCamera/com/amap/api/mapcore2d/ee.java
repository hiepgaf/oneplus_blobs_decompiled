package com.amap.api.mapcore2d;

import android.text.TextUtils;
import java.net.Proxy;
import java.util.Map;

public abstract class ee
{
  int c = 20000;
  int d = 20000;
  Proxy e = null;
  
  public final void a(int paramInt)
  {
    this.c = paramInt;
  }
  
  public final void a(Proxy paramProxy)
  {
    this.e = paramProxy;
  }
  
  public byte[] a_()
  {
    return null;
  }
  
  public final void b(int paramInt)
  {
    this.d = paramInt;
  }
  
  public abstract Map<String, String> e();
  
  public abstract Map<String, String> f();
  
  public abstract String g();
  
  String j()
  {
    Object localObject = a_();
    if (localObject == null) {}
    while (localObject.length == 0) {
      return g();
    }
    localObject = f();
    if (localObject != null)
    {
      localObject = eb.a((Map)localObject);
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(g()).append("?").append((String)localObject);
      return localStringBuffer.toString();
    }
    return g();
  }
  
  byte[] k()
  {
    byte[] arrayOfByte = a_();
    if (arrayOfByte == null) {}
    String str;
    while (arrayOfByte.length == 0)
    {
      str = eb.a(f());
      if (!TextUtils.isEmpty(str)) {
        break;
      }
      return arrayOfByte;
    }
    return arrayOfByte;
    return cv.a(str);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ee.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */