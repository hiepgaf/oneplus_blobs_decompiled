package com.amap.api.mapcore2d;

import com.amap.api.maps2d.model.LatLng;
import java.math.BigDecimal;

public class cd
{
  private static double a(double paramDouble)
  {
    return Math.sin(3000.0D * paramDouble * 0.017453292519943295D) * 2.0E-5D;
  }
  
  private static double a(double paramDouble, int paramInt)
  {
    return new BigDecimal(paramDouble).setScale(paramInt, 4).doubleValue();
  }
  
  private static ce a(double paramDouble1, double paramDouble2)
  {
    ce localce = new ce();
    double d1 = Math.cos(b(paramDouble1) + Math.atan2(paramDouble2, paramDouble1));
    double d2 = a(paramDouble2);
    double d3 = Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
    double d4 = Math.sin(b(paramDouble1) + Math.atan2(paramDouble2, paramDouble1));
    double d5 = a(paramDouble2);
    paramDouble1 = Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
    localce.a = a(d1 * (d2 + d3) + 0.0065D, 8);
    localce.b = a(d4 * (d5 + paramDouble1) + 0.006D, 8);
    return localce;
  }
  
  private static LatLng a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    ce localce1 = new ce();
    paramDouble3 = paramDouble1 - paramDouble3;
    paramDouble4 = paramDouble2 - paramDouble4;
    ce localce2 = a(paramDouble3, paramDouble4);
    localce1.a = a(paramDouble3 + paramDouble1 - localce2.a, 8);
    localce1.b = a(paramDouble2 + paramDouble4 - localce2.b, 8);
    return new LatLng(localce1.b, localce1.a);
  }
  
  public static LatLng a(LatLng paramLatLng)
  {
    if (paramLatLng == null) {
      return null;
    }
    return b(paramLatLng);
  }
  
  private static LatLng a(LatLng paramLatLng, int paramInt)
  {
    double d1 = 0.006401062D;
    double d2 = 0.0060424805D;
    int i = 0;
    LatLng localLatLng = null;
    for (;;)
    {
      if (i >= paramInt) {
        return localLatLng;
      }
      localLatLng = a(paramLatLng.longitude, paramLatLng.latitude, d1, d2);
      d1 = paramLatLng.longitude - localLatLng.longitude;
      d2 = paramLatLng.latitude - localLatLng.latitude;
      i += 1;
    }
  }
  
  private static double b(double paramDouble)
  {
    return Math.cos(3000.0D * paramDouble * 0.017453292519943295D) * 3.0E-6D;
  }
  
  private static LatLng b(LatLng paramLatLng)
  {
    return a(paramLatLng, 2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */