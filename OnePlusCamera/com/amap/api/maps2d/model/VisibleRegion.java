package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.mapcore2d.cj;

public final class VisibleRegion
  implements Parcelable
{
  public static final l CREATOR = new l();
  private final int a;
  public final LatLng farLeft;
  public final LatLng farRight;
  public final LatLngBounds latLngBounds;
  public final LatLng nearLeft;
  public final LatLng nearRight;
  
  VisibleRegion(int paramInt, LatLng paramLatLng1, LatLng paramLatLng2, LatLng paramLatLng3, LatLng paramLatLng4, LatLngBounds paramLatLngBounds)
  {
    this.a = paramInt;
    this.nearLeft = paramLatLng1;
    this.nearRight = paramLatLng2;
    this.farLeft = paramLatLng3;
    this.farRight = paramLatLng4;
    this.latLngBounds = paramLatLngBounds;
  }
  
  public VisibleRegion(LatLng paramLatLng1, LatLng paramLatLng2, LatLng paramLatLng3, LatLng paramLatLng4, LatLngBounds paramLatLngBounds)
  {
    this(1, paramLatLng1, paramLatLng2, paramLatLng3, paramLatLng4, paramLatLngBounds);
  }
  
  int a()
  {
    return this.a;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this != paramObject)
    {
      if (!(paramObject instanceof VisibleRegion)) {
        break label39;
      }
      paramObject = (VisibleRegion)paramObject;
      if (this.nearLeft.equals(((VisibleRegion)paramObject).nearLeft)) {
        break label41;
      }
    }
    for (;;)
    {
      bool = false;
      label39:
      label41:
      do
      {
        return bool;
        return true;
        return false;
        if ((!this.nearRight.equals(((VisibleRegion)paramObject).nearRight)) || (!this.farLeft.equals(((VisibleRegion)paramObject).farLeft)) || (!this.farRight.equals(((VisibleRegion)paramObject).farRight))) {
          break;
        }
      } while (this.latLngBounds.equals(((VisibleRegion)paramObject).latLngBounds));
    }
  }
  
  public int hashCode()
  {
    return cj.a(new Object[] { this.nearLeft, this.nearRight, this.farLeft, this.farRight, this.latLngBounds });
  }
  
  public String toString()
  {
    return cj.a(new String[] { cj.a("nearLeft", this.nearLeft), cj.a("nearRight", this.nearRight), cj.a("farLeft", this.farLeft), cj.a("farRight", this.farRight), cj.a("latLngBounds", this.latLngBounds) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    l.a(this, paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/VisibleRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */