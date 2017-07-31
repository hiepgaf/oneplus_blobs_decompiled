package com.amap.api.location.core;

import com.aps.u;

public class CoordinateConvert
{
  public static GeoPoint fromGpsToAMap(double paramDouble1, double paramDouble2)
  {
    try
    {
      Object localObject = u.a(paramDouble2, paramDouble1);
      localObject = new GeoPoint((int)(localObject[1] * 1000000.0D), (int)(localObject[0] * 1000000.0D));
      return (GeoPoint)localObject;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public static double[] fromSeveralGpsToAMap(String paramString)
  {
    int i = 0;
    try
    {
      paramString = paramString.split(",");
      int j = paramString.length;
      double[] arrayOfDouble1 = new double[j];
      for (;;)
      {
        if (i >= j / 2) {
          return arrayOfDouble1;
        }
        double[] arrayOfDouble2 = u.a(Double.parseDouble(paramString[(i * 2)]), Double.parseDouble(paramString[(i * 2 + 1)]));
        arrayOfDouble1[(i * 2)] = arrayOfDouble2[0];
        arrayOfDouble1[(i * 2 + 1)] = arrayOfDouble2[1];
        i += 1;
      }
      return null;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public static double[] fromSeveralGpsToAMap(double[] paramArrayOfDouble)
  {
    int i = 0;
    try
    {
      int j = paramArrayOfDouble.length;
      double[] arrayOfDouble1 = new double[j];
      for (;;)
      {
        if (i >= j / 2) {
          return arrayOfDouble1;
        }
        double[] arrayOfDouble2 = u.a(paramArrayOfDouble[(i * 2)], paramArrayOfDouble[(i * 2 + 1)]);
        arrayOfDouble1[(i * 2)] = arrayOfDouble2[0];
        arrayOfDouble1[(i * 2 + 1)] = arrayOfDouble2[1];
        i += 1;
      }
      return null;
    }
    catch (Throwable paramArrayOfDouble)
    {
      paramArrayOfDouble.printStackTrace();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/CoordinateConvert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */