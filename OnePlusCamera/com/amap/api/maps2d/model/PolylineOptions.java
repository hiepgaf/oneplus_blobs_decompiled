package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolylineOptions
  implements Parcelable
{
  public static final i CREATOR = new i();
  String a;
  private final List<LatLng> b = new ArrayList();
  private float c = 10.0F;
  private int d = -16777216;
  private float e = 0.0F;
  private boolean f = true;
  private boolean g = false;
  private boolean h = false;
  
  public PolylineOptions add(LatLng paramLatLng)
  {
    this.b.add(paramLatLng);
    return this;
  }
  
  public PolylineOptions add(LatLng... paramVarArgs)
  {
    this.b.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public PolylineOptions addAll(Iterable<LatLng> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    for (;;)
    {
      if (!paramIterable.hasNext()) {
        return this;
      }
      LatLng localLatLng = (LatLng)paramIterable.next();
      this.b.add(localLatLng);
    }
  }
  
  public PolylineOptions color(int paramInt)
  {
    this.d = paramInt;
    return this;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public PolylineOptions geodesic(boolean paramBoolean)
  {
    this.g = paramBoolean;
    return this;
  }
  
  public int getColor()
  {
    return this.d;
  }
  
  public List<LatLng> getPoints()
  {
    return this.b;
  }
  
  public float getWidth()
  {
    return this.c;
  }
  
  public float getZIndex()
  {
    return this.e;
  }
  
  public boolean isDottedLine()
  {
    return this.h;
  }
  
  public boolean isGeodesic()
  {
    return this.g;
  }
  
  public boolean isVisible()
  {
    return this.f;
  }
  
  public PolylineOptions setDottedLine(boolean paramBoolean)
  {
    this.h = paramBoolean;
    return this;
  }
  
  public PolylineOptions visible(boolean paramBoolean)
  {
    this.f = paramBoolean;
    return this;
  }
  
  public PolylineOptions width(float paramFloat)
  {
    this.c = paramFloat;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 0;
    paramParcel.writeTypedList(getPoints());
    paramParcel.writeFloat(getWidth());
    paramParcel.writeInt(getColor());
    paramParcel.writeFloat(getZIndex());
    if (!isVisible())
    {
      paramInt = 0;
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeString(this.a);
      if (isGeodesic()) {
        break label93;
      }
      paramInt = 0;
      label66:
      paramParcel.writeByte((byte)paramInt);
      if (isDottedLine()) {
        break label98;
      }
    }
    label93:
    label98:
    for (paramInt = i;; paramInt = 1)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
      paramInt = 1;
      break;
      paramInt = 1;
      break label66;
    }
  }
  
  public PolylineOptions zIndex(float paramFloat)
  {
    this.e = paramFloat;
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/PolylineOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */