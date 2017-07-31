package com.aps;

import com.amap.api.location.core.AMapLocException;
import org.json.JSONException;
import org.json.JSONObject;

public class c
{
  private String a = "";
  private double b = 0.0D;
  private double c = 0.0D;
  private float d = 0.0F;
  private float e = 0.0F;
  private float f = 0.0F;
  private long g = 0L;
  private AMapLocException h = new AMapLocException();
  private String i = "new";
  private String j = "";
  private String k = "";
  private String l = "";
  private String m = "";
  private String n = "";
  private String o = "";
  private String p = "";
  private String q = "";
  private String r = "";
  private String s = "";
  private String t = "";
  private String u = "";
  private String v = "";
  private String w = "";
  private String x = "";
  private JSONObject y = null;
  
  public c() {}
  
  public c(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null) {
      return;
    }
    try
    {
      this.a = paramJSONObject.getString("provider");
      this.b = paramJSONObject.getDouble("lon");
      this.c = paramJSONObject.getDouble("lat");
      this.d = ((float)paramJSONObject.getLong("accuracy"));
      this.e = ((float)paramJSONObject.getLong("speed"));
      this.f = ((float)paramJSONObject.getLong("bearing"));
      this.g = paramJSONObject.getLong("time");
      this.i = paramJSONObject.getString("type");
      this.j = paramJSONObject.getString("retype");
      this.k = paramJSONObject.getString("citycode");
      this.l = paramJSONObject.getString("desc");
      this.m = paramJSONObject.getString("adcode");
      this.n = paramJSONObject.getString("country");
      this.o = paramJSONObject.getString("province");
      this.p = paramJSONObject.getString("city");
      this.q = paramJSONObject.getString("road");
      this.r = paramJSONObject.getString("street");
      this.s = paramJSONObject.getString("poiname");
      this.u = paramJSONObject.getString("floor");
      this.t = paramJSONObject.getString("poiid");
      this.v = paramJSONObject.getString("coord");
      this.w = paramJSONObject.getString("mcell");
      this.x = paramJSONObject.getString("district");
      return;
    }
    catch (Throwable paramJSONObject)
    {
      paramJSONObject.printStackTrace();
      t.a(paramJSONObject);
    }
  }
  
  public AMapLocException a()
  {
    return this.h;
  }
  
  public void a(double paramDouble)
  {
    this.b = paramDouble;
  }
  
  public void a(float paramFloat)
  {
    this.d = paramFloat;
  }
  
  public void a(long paramLong)
  {
    this.g = paramLong;
  }
  
  public void a(AMapLocException paramAMapLocException)
  {
    this.h = paramAMapLocException;
  }
  
  public void a(String paramString)
  {
    this.t = paramString;
  }
  
  public void a(JSONObject paramJSONObject)
  {
    this.y = paramJSONObject;
  }
  
  public String b()
  {
    return this.t;
  }
  
  public void b(double paramDouble)
  {
    this.c = paramDouble;
  }
  
  public void b(String paramString)
  {
    this.u = paramString;
  }
  
  public String c()
  {
    return this.u;
  }
  
  public void c(String paramString)
  {
    this.x = paramString;
  }
  
  public String d()
  {
    return this.x;
  }
  
  public void d(String paramString)
  {
    this.v = paramString;
  }
  
  public double e()
  {
    return this.b;
  }
  
  public void e(String paramString)
  {
    this.w = paramString;
  }
  
  public double f()
  {
    return this.c;
  }
  
  public void f(String paramString)
  {
    this.a = paramString;
  }
  
  public float g()
  {
    return this.d;
  }
  
  public void g(String paramString)
  {
    this.i = paramString;
  }
  
  public long h()
  {
    return this.g;
  }
  
  public void h(String paramString)
  {
    this.j = paramString;
  }
  
  public String i()
  {
    return this.i;
  }
  
  public void i(String paramString)
  {
    this.k = paramString;
  }
  
  public String j()
  {
    return this.j;
  }
  
  public void j(String paramString)
  {
    this.l = paramString;
  }
  
  public String k()
  {
    return this.k;
  }
  
  public void k(String paramString)
  {
    this.m = paramString;
  }
  
  public String l()
  {
    return this.l;
  }
  
  public void l(String paramString)
  {
    this.n = paramString;
  }
  
  public String m()
  {
    return this.m;
  }
  
  public void m(String paramString)
  {
    this.o = paramString;
  }
  
  public String n()
  {
    return this.n;
  }
  
  public void n(String paramString)
  {
    this.p = paramString;
  }
  
  public String o()
  {
    return this.o;
  }
  
  public void o(String paramString)
  {
    this.q = paramString;
  }
  
  public String p()
  {
    return this.p;
  }
  
  public void p(String paramString)
  {
    this.r = paramString;
  }
  
  public String q()
  {
    return this.q;
  }
  
  public void q(String paramString)
  {
    this.s = paramString;
  }
  
  public String r()
  {
    return this.r;
  }
  
  public String s()
  {
    return this.s;
  }
  
  public JSONObject t()
  {
    return this.y;
  }
  
  public String u()
  {
    try
    {
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("provider", this.a);
      localJSONObject.put("lon", this.b);
      localJSONObject.put("lat", this.c);
      localJSONObject.put("accuracy", this.d);
      localJSONObject.put("speed", this.e);
      localJSONObject.put("bearing", this.f);
      localJSONObject.put("time", this.g);
      localJSONObject.put("type", this.i);
      localJSONObject.put("retype", this.j);
      localJSONObject.put("citycode", this.k);
      localJSONObject.put("desc", this.l);
      localJSONObject.put("adcode", this.m);
      localJSONObject.put("country", this.n);
      localJSONObject.put("province", this.o);
      localJSONObject.put("city", this.p);
      localJSONObject.put("road", this.q);
      localJSONObject.put("street", this.r);
      localJSONObject.put("poiname", this.s);
      localJSONObject.put("poiid", this.t);
      localJSONObject.put("floor", this.u);
      localJSONObject.put("coord", this.v);
      localJSONObject.put("mcell", this.w);
      localJSONObject.put("district", this.x);
      if (localJSONObject != null) {
        return localJSONObject.toString();
      }
    }
    catch (JSONException localJSONException)
    {
      for (;;)
      {
        t.a(localJSONException);
        Object localObject = null;
      }
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/c.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */