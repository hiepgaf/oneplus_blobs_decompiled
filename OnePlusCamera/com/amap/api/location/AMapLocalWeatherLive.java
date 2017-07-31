package com.amap.api.location;

import com.amap.api.location.core.AMapLocException;

public class AMapLocalWeatherLive
{
  private String a;
  private String b;
  private String c;
  private String d;
  private String e;
  private String f;
  private AMapLocException g;
  private String h;
  private String i;
  private String j;
  
  void a(AMapLocException paramAMapLocException)
  {
    this.g = paramAMapLocException;
  }
  
  void a(String paramString)
  {
    this.a = paramString;
  }
  
  void b(String paramString)
  {
    this.b = paramString;
  }
  
  void c(String paramString)
  {
    this.c = paramString;
  }
  
  void d(String paramString)
  {
    this.d = paramString;
  }
  
  void e(String paramString)
  {
    this.e = paramString;
  }
  
  void f(String paramString)
  {
    this.f = paramString;
  }
  
  public AMapLocException getAMapException()
  {
    return this.g;
  }
  
  public String getCity()
  {
    return this.h;
  }
  
  public String getCityCode()
  {
    return this.j;
  }
  
  public String getHumidity()
  {
    return this.e;
  }
  
  public String getProvince()
  {
    return this.i;
  }
  
  public String getReportTime()
  {
    return this.f;
  }
  
  public String getTemperature()
  {
    return this.b;
  }
  
  public String getWeather()
  {
    return this.a;
  }
  
  public String getWindDir()
  {
    return this.c;
  }
  
  public String getWindPower()
  {
    return this.d;
  }
  
  public void setCity(String paramString)
  {
    this.h = paramString;
  }
  
  public void setCityCode(String paramString)
  {
    this.j = paramString;
  }
  
  public void setProvince(String paramString)
  {
    this.i = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/AMapLocalWeatherLive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */