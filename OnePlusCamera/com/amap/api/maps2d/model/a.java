package com.amap.api.maps2d.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable.Creator;

class a
  implements Parcelable.Creator<BitmapDescriptor>
{
  public BitmapDescriptor a(Parcel paramParcel)
  {
    BitmapDescriptor localBitmapDescriptor = new BitmapDescriptor(null);
    localBitmapDescriptor.d = ((Bitmap)paramParcel.readParcelable(BitmapDescriptor.class.getClassLoader()));
    localBitmapDescriptor.b = paramParcel.readInt();
    localBitmapDescriptor.c = paramParcel.readInt();
    return localBitmapDescriptor;
  }
  
  public BitmapDescriptor[] a(int paramInt)
  {
    return new BitmapDescriptor[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */