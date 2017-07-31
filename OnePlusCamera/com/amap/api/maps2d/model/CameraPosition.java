package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.amap.api.mapcore2d.ci;
import com.amap.api.mapcore2d.cj;

public final class CameraPosition
  implements Parcelable
{
  public static final b CREATOR = new b();
  public final float bearing;
  public final boolean isAbroad;
  public final LatLng target;
  public final float tilt;
  public final float zoom;
  
  public CameraPosition(LatLng paramLatLng, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramLatLng != null) {}
    for (;;)
    {
      this.target = paramLatLng;
      this.zoom = cj.b(paramFloat1);
      this.tilt = cj.a(paramFloat2);
      paramFloat1 = paramFloat3;
      if (paramFloat3 <= 0.0D) {
        paramFloat1 = paramFloat3 % 360.0F + 360.0F;
      }
      this.bearing = (paramFloat1 % 360.0F);
      if (paramLatLng != null) {
        break;
      }
      this.isAbroad = false;
      return;
      Log.w("CameraPosition", "构建CameraPosition时,位置(target)不能为null");
    }
    if (ci.a(paramLatLng.latitude, paramLatLng.longitude)) {}
    for (;;)
    {
      this.isAbroad = bool;
      return;
      bool = true;
    }
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static Builder builder(CameraPosition paramCameraPosition)
  {
    return new Builder(paramCameraPosition);
  }
  
  public static final CameraPosition fromLatLngZoom(LatLng paramLatLng, float paramFloat)
  {
    return new CameraPosition(paramLatLng, paramFloat, 0.0F, 0.0F);
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
      if (!(paramObject instanceof CameraPosition)) {
        break label39;
      }
      paramObject = (CameraPosition)paramObject;
      if (this.target.equals(((CameraPosition)paramObject).target)) {
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
        if ((Float.floatToIntBits(this.zoom) != Float.floatToIntBits(((CameraPosition)paramObject).zoom)) || (Float.floatToIntBits(this.tilt) != Float.floatToIntBits(((CameraPosition)paramObject).tilt))) {
          break;
        }
      } while (Float.floatToIntBits(this.bearing) == Float.floatToIntBits(((CameraPosition)paramObject).bearing));
    }
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return cj.a(new String[] { cj.a("target", this.target), cj.a("zoom", Float.valueOf(this.zoom)), cj.a("tilt", Float.valueOf(this.tilt)), cj.a("bearing", Float.valueOf(this.bearing)) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.bearing);
    paramParcel.writeFloat((float)this.target.latitude);
    paramParcel.writeFloat((float)this.target.longitude);
    paramParcel.writeFloat(this.tilt);
    paramParcel.writeFloat(this.zoom);
  }
  
  public static final class Builder
  {
    private LatLng a;
    private float b;
    private float c;
    private float d;
    
    public Builder() {}
    
    public Builder(CameraPosition paramCameraPosition)
    {
      target(paramCameraPosition.target).bearing(paramCameraPosition.bearing).tilt(paramCameraPosition.tilt).zoom(paramCameraPosition.zoom);
    }
    
    public Builder bearing(float paramFloat)
    {
      this.d = paramFloat;
      return this;
    }
    
    public CameraPosition build()
    {
      try
      {
        if (this.a != null) {
          return new CameraPosition(this.a, this.b, this.c, this.d);
        }
        Log.w("CameraPosition", "target is null");
        return null;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "CameraPosition", "build");
      }
      return null;
    }
    
    public Builder target(LatLng paramLatLng)
    {
      this.a = paramLatLng;
      return this;
    }
    
    public Builder tilt(float paramFloat)
    {
      this.c = paramFloat;
      return this;
    }
    
    public Builder zoom(float paramFloat)
    {
      this.b = paramFloat;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/CameraPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */