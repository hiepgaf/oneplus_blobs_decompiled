package com.amap.api.mapcore2d;

import com.amap.api.maps2d.model.LatLng;

public class cg
{
  public static LatLng a(LatLng paramLatLng)
  {
    paramLatLng = en.a(paramLatLng.longitude, paramLatLng.latitude);
    return new LatLng(paramLatLng[1], paramLatLng[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */