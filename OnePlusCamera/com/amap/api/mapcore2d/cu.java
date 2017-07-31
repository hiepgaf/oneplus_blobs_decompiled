package com.amap.api.mapcore2d;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

@di(a="a")
public class cu
{
  @dj(a="a1", b=6)
  private String a;
  @dj(a="a2", b=6)
  private String b;
  @dj(a="a6", b=2)
  private int c = 1;
  @dj(a="a3", b=6)
  private String d;
  @dj(a="a4", b=6)
  private String e;
  @dj(a="a5", b=6)
  private String f;
  private String g;
  private String h;
  private String i;
  private String j;
  private String k;
  private String[] l = null;
  
  private cu() {}
  
  private cu(a parama)
  {
    this.g = a.a(parama);
    this.h = a.b(parama);
    this.j = a.c(parama);
    this.i = a.d(parama);
    if (!a.e(parama)) {}
    for (;;)
    {
      this.c = m;
      this.k = a.f(parama);
      this.l = a.g(parama);
      this.b = cv.b(this.h);
      this.a = cv.b(this.j);
      this.d = cv.b(this.i);
      this.e = cv.b(a(this.l));
      this.f = cv.b(this.k);
      return;
      m = 1;
    }
  }
  
  public static String a(String paramString)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("a1", cv.b(paramString));
    return dh.a(localHashMap);
  }
  
  private String a(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder();
        int n = paramArrayOfString.length;
        int m = 0;
        for (;;)
        {
          if (m >= n) {
            return localStringBuilder.toString();
          }
          localStringBuilder.append(paramArrayOfString[m]).append(";");
          m += 1;
        }
        return null;
      }
      catch (Throwable paramArrayOfString)
      {
        paramArrayOfString.printStackTrace();
        return null;
      }
    }
  }
  
  private String[] b(String paramString)
  {
    try
    {
      paramString = paramString.split(";");
      return paramString;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
    return null;
  }
  
  public static String g()
  {
    return "a6=1";
  }
  
  public String a()
  {
    if (!TextUtils.isEmpty(this.j)) {}
    for (;;)
    {
      return this.j;
      if (!TextUtils.isEmpty(this.a)) {
        this.j = cv.c(this.a);
      }
    }
  }
  
  public void a(boolean paramBoolean)
  {
    int m = 0;
    if (!paramBoolean) {}
    for (;;)
    {
      this.c = m;
      return;
      m = 1;
    }
  }
  
  public String b()
  {
    return this.g;
  }
  
  public String c()
  {
    if (!TextUtils.isEmpty(this.h)) {}
    for (;;)
    {
      return this.h;
      if (!TextUtils.isEmpty(this.b)) {
        this.h = cv.c(this.b);
      }
    }
  }
  
  public String d()
  {
    if (!TextUtils.isEmpty(this.i)) {}
    for (;;)
    {
      return this.i;
      if (!TextUtils.isEmpty(this.d)) {
        this.i = cv.c(this.d);
      }
    }
  }
  
  public String e()
  {
    if (!TextUtils.isEmpty(this.k)) {
      if (TextUtils.isEmpty(this.k)) {
        break label49;
      }
    }
    for (;;)
    {
      return this.k;
      if (TextUtils.isEmpty(this.f)) {
        break;
      }
      this.k = cv.c(this.f);
      break;
      label49:
      this.k = "standard";
    }
  }
  
  public String[] f()
  {
    if (this.l == null) {
      if (!TextUtils.isEmpty(this.e)) {
        break label39;
      }
    }
    for (;;)
    {
      return (String[])this.l.clone();
      if (this.l.length == 0) {
        break;
      }
      continue;
      label39:
      this.l = b(cv.c(this.e));
    }
  }
  
  public static class a
  {
    private String a;
    private String b;
    private String c;
    private String d;
    private boolean e = true;
    private String f = "standard";
    private String[] g = null;
    
    public a(String paramString1, String paramString2, String paramString3)
    {
      this.a = paramString2;
      this.b = paramString2;
      this.d = paramString3;
      this.c = paramString1;
    }
    
    public a a(String[] paramArrayOfString)
    {
      this.g = ((String[])paramArrayOfString.clone());
      return this;
    }
    
    public cu a()
      throws ck
    {
      if (this.g != null) {
        return new cu(this, null);
      }
      throw new ck("sdk packages is null");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */