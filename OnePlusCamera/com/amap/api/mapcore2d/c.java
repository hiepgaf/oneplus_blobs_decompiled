package com.amap.api.mapcore2d;

import android.location.Location;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;

class c
  implements LocationSource.OnLocationChangedListener
{
  Location a;
  private w b;
  
  c(w paramw)
  {
    this.b = paramw;
  }
  
  public void onLocationChanged(Location paramLocation)
  {
    this.a = paramLocation;
    try
    {
      if (!this.b.n()) {
        return;
      }
      this.b.a(paramLocation);
      return;
    }
    catch (Throwable paramLocation)
    {
      cj.a(paramLocation, "AMapOnLocationChangedListener", "onLocationChanged");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/c.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */