package com.amap.api.maps2d.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.mapcore2d.cj;

public final class BitmapDescriptor
  implements Parcelable, Cloneable
{
  static final a a = new a();
  int b = 0;
  int c = 0;
  Bitmap d;
  
  BitmapDescriptor(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return;
    }
    this.b = paramBitmap.getWidth();
    this.c = paramBitmap.getHeight();
    this.d = paramBitmap;
  }
  
  private BitmapDescriptor(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    this.b = paramInt1;
    this.c = paramInt2;
    this.d = paramBitmap;
  }
  
  public BitmapDescriptor clone()
  {
    try
    {
      BitmapDescriptor localBitmapDescriptor = new BitmapDescriptor(Bitmap.createBitmap(this.d), this.b, this.c);
      return localBitmapDescriptor;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "BitmapDescriptor", "clone");
    }
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bitmap getBitmap()
  {
    return this.d;
  }
  
  public int getHeight()
  {
    return this.c;
  }
  
  public int getWidth()
  {
    return this.b;
  }
  
  public void recycle()
  {
    if (this.d == null) {}
    while (this.d.isRecycled()) {
      return;
    }
    this.d.recycle();
    this.d = null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.d, paramInt);
    paramParcel.writeInt(this.b);
    paramParcel.writeInt(this.c);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/BitmapDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */