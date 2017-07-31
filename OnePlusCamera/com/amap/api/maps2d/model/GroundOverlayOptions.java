package com.amap.api.maps2d.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.amap.api.mapcore2d.cj;

public final class GroundOverlayOptions
  implements Parcelable
{
  public static final d CREATOR = new d();
  public static final float NO_DIMENSION = -1.0F;
  private final int a;
  private BitmapDescriptor b;
  private LatLng c;
  private float d;
  private float e;
  private LatLngBounds f;
  private float g;
  private float h;
  private boolean i = true;
  private float j = 0.0F;
  private float k = 0.5F;
  private float l = 0.5F;
  
  public GroundOverlayOptions()
  {
    this.a = 1;
  }
  
  GroundOverlayOptions(int paramInt, IBinder paramIBinder, LatLng paramLatLng, float paramFloat1, float paramFloat2, LatLngBounds paramLatLngBounds, float paramFloat3, float paramFloat4, boolean paramBoolean, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    this.a = paramInt;
    this.b = BitmapDescriptorFactory.fromBitmap(null);
    this.c = paramLatLng;
    this.d = paramFloat1;
    this.e = paramFloat2;
    this.f = paramLatLngBounds;
    this.g = paramFloat3;
    this.h = paramFloat4;
    this.i = paramBoolean;
    this.j = paramFloat5;
    this.k = paramFloat6;
    this.l = paramFloat7;
  }
  
  private GroundOverlayOptions a(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    this.c = paramLatLng;
    this.d = paramFloat1;
    this.e = paramFloat2;
    return this;
  }
  
  public GroundOverlayOptions anchor(float paramFloat1, float paramFloat2)
  {
    this.k = paramFloat1;
    this.l = paramFloat2;
    return this;
  }
  
  public GroundOverlayOptions bearing(float paramFloat)
  {
    this.g = paramFloat;
    return this;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public float getAnchorU()
  {
    return this.k;
  }
  
  public float getAnchorV()
  {
    return this.l;
  }
  
  public float getBearing()
  {
    return this.g;
  }
  
  public LatLngBounds getBounds()
  {
    return this.f;
  }
  
  public float getHeight()
  {
    return this.e;
  }
  
  public BitmapDescriptor getImage()
  {
    return this.b;
  }
  
  public LatLng getLocation()
  {
    return this.c;
  }
  
  public float getTransparency()
  {
    return this.j;
  }
  
  public float getWidth()
  {
    return this.d;
  }
  
  public float getZIndex()
  {
    return this.h;
  }
  
  public GroundOverlayOptions image(BitmapDescriptor paramBitmapDescriptor)
  {
    this.b = paramBitmapDescriptor;
    return this;
  }
  
  public boolean isVisible()
  {
    return this.i;
  }
  
  public GroundOverlayOptions position(LatLng paramLatLng, float paramFloat)
  {
    for (;;)
    {
      try
      {
        if (this.f == null)
        {
          break label65;
          if (paramFloat <= 0.0F) {
            Log.w("GroundOverlayOptions", "Width must be non-negative");
          }
          return a(paramLatLng, paramFloat, paramFloat);
        }
        else
        {
          Log.w("GroundOverlayOptions", "Position has already been set using positionFromBounds");
        }
      }
      catch (Exception paramLatLng)
      {
        cj.a(paramLatLng, "GroundOverlayOptions", "position");
        return null;
      }
      label65:
      do
      {
        Log.w("GroundOverlayOptions", "Location must be specified");
        break;
      } while (paramLatLng == null);
    }
  }
  
  public GroundOverlayOptions position(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    for (;;)
    {
      try
      {
        if (this.f == null)
        {
          break label65;
          Log.w("GroundOverlayOptions", "Width and Height must be non-negative");
          return a(paramLatLng, paramFloat1, paramFloat2);
        }
        else
        {
          Log.w("GroundOverlayOptions", "Position has already been set using positionFromBounds");
        }
      }
      catch (Exception paramLatLng)
      {
        cj.a(paramLatLng, "GroundOverlayOptions", "position");
        return null;
      }
      Log.w("GroundOverlayOptions", "Location must be specified");
      label65:
      while (paramFloat1 > 0.0F)
      {
        m = 0;
        break label78;
        if (paramLatLng == null) {
          break;
        }
      }
      int m = 1;
      label78:
      if (m == 0) {
        if (paramFloat2 > 0.0F) {}
      }
    }
  }
  
  public GroundOverlayOptions positionFromBounds(LatLngBounds paramLatLngBounds)
  {
    try
    {
      if (this.c == null) {}
      for (;;)
      {
        this.f = paramLatLngBounds;
        return this;
        Log.w("GroundOverlayOptions", "Position has already been set using position: " + this.c);
      }
      return null;
    }
    catch (Exception paramLatLngBounds)
    {
      cj.a(paramLatLngBounds, "GroundOverlayOptions", "positionFromBounds");
    }
  }
  
  public GroundOverlayOptions transparency(float paramFloat)
  {
    float f1 = paramFloat;
    if (paramFloat < 0.0F) {}
    try
    {
      Log.w("GroundOverlayOptions", "Transparency must be in the range [0..1]");
      f1 = 0.0F;
      this.j = f1;
      return this;
    }
    catch (Exception localException)
    {
      cj.a(localException, "GroundOverlayOptions", "transparency");
    }
    return null;
  }
  
  public GroundOverlayOptions visible(boolean paramBoolean)
  {
    this.i = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int m = 0;
    paramParcel.writeInt(this.a);
    paramParcel.writeParcelable(this.b, paramInt);
    paramParcel.writeParcelable(this.c, paramInt);
    paramParcel.writeFloat(this.d);
    paramParcel.writeFloat(this.e);
    paramParcel.writeParcelable(this.f, paramInt);
    paramParcel.writeFloat(this.g);
    paramParcel.writeFloat(this.h);
    if (!this.i) {}
    for (paramInt = m;; paramInt = 1)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeFloat(this.j);
      paramParcel.writeFloat(this.k);
      paramParcel.writeFloat(this.l);
      return;
    }
  }
  
  public GroundOverlayOptions zIndex(float paramFloat)
  {
    this.h = paramFloat;
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/GroundOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */