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
  
    public static double[] fromSeveralGpsToAMap(final String s) {
        int i = 0;
        final String s2 = ",";
        try {
            final String[] split = s.split(s2);
            final int length = split.length;
            final double[] array = new double[length];
            while (i < length / 2) {
                final double[] a = u.a(Double.parseDouble(split[i * 2]), Double.parseDouble(split[i * 2 + 1]));
                array[i * 2] = a[0];
                array[i * 2 + 1] = a[1];
                ++i;
            }
            return array;
        }
        finally {
            loadexception(java.lang.Throwable.class).printStackTrace();
            return null;
        }
    }
    
    public static double[] fromSeveralGpsToAMap(final double[] array) {
        int i = 0;
        try {
            final int length = array.length;
            final double[] array2 = new double[length];
            while (i < length / 2) {
                final double[] a = u.a(array[i * 2], array[i * 2 + 1]);
                array2[i * 2] = a[0];
                array2[i * 2 + 1] = a[1];
                ++i;
            }
            return array2;
        }
        finally {
            loadexception(java.lang.Throwable.class).printStackTrace();
            return null;
        }
    }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/CoordinateConvert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */