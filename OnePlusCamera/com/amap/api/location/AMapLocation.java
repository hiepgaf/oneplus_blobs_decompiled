package com.amap.api.location;

import android.location.Location;
import com.amap.api.location.core.AMapLocException;

public class AMapLocation
  extends Location
{
  private String a;
  private String b;
  private String c;
  private String d;
  private String e;
  private String f;
  private String g;
  private String h;
  private String i;
  private String j;
  private String k;
  private String l;
  private AMapLocException m = new AMapLocException();
  
  public AMapLocation(Location paramLocation)
  {
    super(paramLocation);
  }
  
  public AMapLocation(String paramString)
  {
    super(paramString);
  }
  
  void a(String paramString)
  {
    this.h = paramString;
  }
  
  void b(String paramString)
  {
    this.i = paramString;
  }
  
  public AMapLocException getAMapException()
  {
    return this.m;
  }
  
  public String getAdCode()
  {
    return this.e;
  }
  
  public String getAddress()
  {
    return this.i;
  }
  
  public String getCity()
  {
    return this.b;
  }
  
  public String getCityCode()
  {
    return this.d;
  }
  
  public String getCountry()
  {
    return this.j;
  }
  
  public String getDistrict()
  {
    return this.c;
  }
  
  public String getFloor()
  {
    return this.g;
  }
  
  public String getPoiId()
  {
    return this.f;
  }
  
  public String getPoiName()
  {
    return this.l;
  }
  
  public String getProvince()
  {
    return this.a;
  }
  
  public String getRoad()
  {
    return this.k;
  }
  
  public String getStreet()
  {
    return this.h;
  }
  
  public void setAMapException(AMapLocException paramAMapLocException)
  {
    this.m = paramAMapLocException;
  }
  
  public void setAdCode(String paramString)
  {
    this.e = paramString;
  }
  
  public void setCity(String paramString)
  {
    this.b = paramString;
  }
  
  public void setCityCode(String paramString)
  {
    this.d = paramString;
  }
  
  public void setCountry(String paramString)
  {
    this.j = paramString;
  }
  
  public void setDistrict(String paramString)
  {
    this.c = paramString;
  }
  
  public void setFloor(String paramString)
  {
    this.g = paramString;
  }
  
  public void setPoiId(String paramString)
  {
    this.f = paramString;
  }
  
  public void setPoiName(String paramString)
  {
    this.l = paramString;
  }
  
  public void setProvince(String paramString)
  {
    this.a = paramString;
  }
  
  public void setRoad(String paramString)
  {
    this.k = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/AMapLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */