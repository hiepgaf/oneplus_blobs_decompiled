package com.amap.api.maps2d.model;

public class PoiPara
{
  private LatLng a;
  private String b;
  
  public LatLng getCenter()
  {
    return this.a;
  }
  
  public String getKeywords()
  {
    return this.b;
  }
  
  public void setCenter(LatLng paramLatLng)
  {
    this.a = paramLatLng;
  }
  
  public void setKeywords(String paramString)
  {
    this.b = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/PoiPara.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */