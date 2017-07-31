package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class j
  implements Parcelable.Creator<Tile>
{
  public Tile a(Parcel paramParcel)
  {
    return new Tile(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.createByteArray());
  }
  
  public Tile[] a(int paramInt)
  {
    return new Tile[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/j.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */