package com.amap.api.mapcore2d;

import java.util.HashMap;
import java.util.Map;

@Deprecated
class cw
  extends ee
{
  private Map<String, String> a = new HashMap();
  private String b;
  private Map<String, String> f = new HashMap();
  
  void a(String paramString)
  {
    this.b = paramString;
  }
  
  void a(Map<String, String> paramMap)
  {
    this.a.clear();
    this.a.putAll(paramMap);
  }
  
  void b(Map<String, String> paramMap)
  {
    this.f.clear();
    this.f.putAll(paramMap);
  }
  
  public Map<String, String> e()
  {
    return this.a;
  }
  
  public Map<String, String> f()
  {
    return this.f;
  }
  
  public String g()
  {
    return this.b;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */