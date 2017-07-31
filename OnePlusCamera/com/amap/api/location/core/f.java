package com.amap.api.location.core;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class f
  implements Parcelable.Creator<GeoPoint>
{
  public GeoPoint a(Parcel paramParcel)
  {
    return new GeoPoint(paramParcel, null);
  }
  
  public GeoPoint[] a(int paramInt)
  {
    return new GeoPoint[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/f.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */