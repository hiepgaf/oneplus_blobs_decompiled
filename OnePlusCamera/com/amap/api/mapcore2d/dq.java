package com.amap.api.mapcore2d;

import java.util.HashMap;
import java.util.Map;

public abstract class dq
{
  @dj(a="b2", b=2)
  protected int a = -1;
  @dj(a="b1", b=6)
  protected String b;
  @dj(a="b3", b=2)
  protected int c = 1;
  @dj(a="a1", b=6)
  private String d;
  
  public static String c(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      localStringBuilder.append("b2").append("=").append(paramInt);
      return localStringBuilder.toString();
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        localThrowable.printStackTrace();
      }
    }
  }
  
  public static String c(String paramString)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("b1", paramString);
    return dh.a(localHashMap);
  }
  
  public int a()
  {
    return this.a;
  }
  
  public void a(int paramInt)
  {
    this.a = paramInt;
  }
  
  public void a(String paramString)
  {
    this.b = paramString;
  }
  
  public String b()
  {
    return this.b;
  }
  
  public void b(int paramInt)
  {
    this.c = paramInt;
  }
  
  public void b(String paramString)
  {
    this.d = cv.b(paramString);
  }
  
  public int c()
  {
    return this.c;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */