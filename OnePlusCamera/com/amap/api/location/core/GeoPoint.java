package com.amap.api.location.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GeoPoint
  implements Parcelable
{
  public static final Parcelable.Creator<GeoPoint> CREATOR = new f();
  private long a = Long.MIN_VALUE;
  private long b = Long.MIN_VALUE;
  private double c = Double.MIN_VALUE;
  private double d = Double.MIN_VALUE;
  
  public GeoPoint()
  {
    this.a = 0L;
    this.b = 0L;
  }
  
  public GeoPoint(int paramInt1, int paramInt2)
  {
    this.a = paramInt1;
    this.b = paramInt2;
  }
  
  public GeoPoint(long paramLong1, long paramLong2)
  {
    this.a = paramLong1;
    this.b = paramLong2;
  }
  
  private GeoPoint(Parcel paramParcel)
  {
    this.a = paramParcel.readLong();
    this.b = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject != null)
    {
      if (paramObject.getClass() == getClass())
      {
        paramObject = (GeoPoint)paramObject;
        boolean bool1 = bool2;
        if (this.c == ((GeoPoint)paramObject).c)
        {
          bool1 = bool2;
          if (this.d == ((GeoPoint)paramObject).d)
          {
            bool1 = bool2;
            if (this.a == ((GeoPoint)paramObject).a)
            {
              bool1 = bool2;
              if (this.b == ((GeoPoint)paramObject).b) {
                bool1 = true;
              }
            }
          }
        }
        return bool1;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public int getLatitudeE6()
  {
    return (int)this.a;
  }
  
  public int getLongitudeE6()
  {
    return (int)this.b;
  }
  
  public int hashCode()
  {
    return (int)(this.d * 7.0D + this.c * 11.0D);
  }
  
  public String toString()
  {
    return "" + this.a + "," + this.b;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.a);
    paramParcel.writeLong(this.b);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/GeoPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */