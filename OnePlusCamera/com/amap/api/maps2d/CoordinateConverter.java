package com.amap.api.maps2d;

import com.amap.api.mapcore2d.cd;
import com.amap.api.mapcore2d.cf;
import com.amap.api.mapcore2d.cg;
import com.amap.api.maps2d.model.LatLng;

public class CoordinateConverter
{
  private CoordType a = null;
  private LatLng b = null;
  
  public LatLng convert()
  {
    if (this.a != null)
    {
      if (this.b != null)
      {
        try
        {
          switch (1.a[this.a.ordinal()])
          {
          case 1: 
            return cd.a(this.b);
          }
        }
        catch (Throwable localThrowable)
        {
          LatLng localLatLng;
          localThrowable.printStackTrace();
          return this.b;
        }
        return cf.a(this.b);
        return this.b;
        localLatLng = cg.a(this.b);
        return localLatLng;
        return null;
      }
    }
    else {
      return null;
    }
    return null;
  }
  
  public CoordinateConverter coord(LatLng paramLatLng)
  {
    this.b = paramLatLng;
    return this;
  }
  
  public CoordinateConverter from(CoordType paramCoordType)
  {
    this.a = paramCoordType;
    return this;
  }
  
  public static enum CoordType
  {
    static
    {
      MAPABC = new CoordType("MAPABC", 2);
      SOSOMAP = new CoordType("SOSOMAP", 3);
    }
    
    private CoordType() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/CoordinateConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */