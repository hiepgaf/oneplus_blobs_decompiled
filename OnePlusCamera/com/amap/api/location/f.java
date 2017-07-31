package com.amap.api.location;

import android.location.Location;
import android.os.Bundle;

public class f
  implements AMapLocationListener
{
  private LocationManagerProxy a;
  private AMapLocationListener b = null;
  
  public f(LocationManagerProxy paramLocationManagerProxy)
  {
    this.a = paramLocationManagerProxy;
  }
  
  public void a()
  {
    if (this.a == null) {}
    for (;;)
    {
      this.b = null;
      return;
      this.a.removeUpdates(this);
    }
  }
  
  public boolean a(AMapLocationListener paramAMapLocationListener, long paramLong, float paramFloat, String paramString)
  {
    this.b = paramAMapLocationListener;
    if (!"lbs".equals(paramString)) {
      return false;
    }
    this.a.requestLocationUpdates(paramString, paramLong, paramFloat, this);
    return true;
  }
  
  public void onLocationChanged(Location paramLocation)
  {
    if (this.b == null) {
      return;
    }
    this.b.onLocationChanged(paramLocation);
  }
  
  public void onLocationChanged(AMapLocation paramAMapLocation)
  {
    if (this.b == null) {
      return;
    }
    this.b.onLocationChanged(paramAMapLocation);
  }
  
  public void onProviderDisabled(String paramString)
  {
    if (this.b == null) {
      return;
    }
    this.b.onProviderDisabled(paramString);
  }
  
  public void onProviderEnabled(String paramString)
  {
    if (this.b == null) {
      return;
    }
    this.b.onProviderEnabled(paramString);
  }
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
  {
    if (this.b == null) {
      return;
    }
    this.b.onStatusChanged(paramString, paramInt, paramBundle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/f.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */