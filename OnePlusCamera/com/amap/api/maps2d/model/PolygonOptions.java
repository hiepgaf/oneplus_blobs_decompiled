package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolygonOptions
  implements Parcelable
{
  public static final h CREATOR = new h();
  String a;
  private final List<LatLng> b = new ArrayList();
  private float c = 10.0F;
  private int d = -16777216;
  private int e = -16777216;
  private float f = 0.0F;
  private boolean g = true;
  
  public PolygonOptions add(LatLng paramLatLng)
  {
    this.b.add(paramLatLng);
    return this;
  }
  
  public PolygonOptions add(LatLng... paramVarArgs)
  {
    this.b.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public PolygonOptions addAll(Iterable<LatLng> paramIterable)
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
  
  public int describeContents()
  {
    return 0;
  }
  
  public PolygonOptions fillColor(int paramInt)
  {
    this.e = paramInt;
    return this;
  }
  
  public int getFillColor()
  {
    return this.e;
  }
  
  public List<LatLng> getPoints()
  {
    return this.b;
  }
  
  public int getStrokeColor()
  {
    return this.d;
  }
  
  public float getStrokeWidth()
  {
    return this.c;
  }
  
  public float getZIndex()
  {
    return this.f;
  }
  
  public boolean isVisible()
  {
    return this.g;
  }
  
  public PolygonOptions strokeColor(int paramInt)
  {
    this.d = paramInt;
    return this;
  }
  
  public PolygonOptions strokeWidth(float paramFloat)
  {
    this.c = paramFloat;
    return this;
  }
  
  public PolygonOptions visible(boolean paramBoolean)
  {
    this.g = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = 0;
    paramParcel.writeTypedList(this.b);
    paramParcel.writeFloat(this.c);
    paramParcel.writeInt(this.d);
    paramParcel.writeInt(this.e);
    paramParcel.writeFloat(this.f);
    if (!this.g) {
      paramInt = 1;
    }
    paramParcel.writeByte((byte)paramInt);
    paramParcel.writeString(this.a);
  }
  
  public PolygonOptions zIndex(float paramFloat)
  {
    this.f = paramFloat;
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/PolygonOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */