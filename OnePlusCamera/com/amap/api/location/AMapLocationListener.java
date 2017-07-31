package com.amap.api.location;

import android.location.LocationListener;

public abstract interface AMapLocationListener
  extends LocationListener
{
  public abstract void onLocationChanged(AMapLocation paramAMapLocation);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/AMapLocationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */