package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.amap.api.mapcore2d.cj;

public final class LatLngBounds
  implements Parcelable
{
  public static final e CREATOR = new e();
  private final int a;
  private boolean b = true;
  public final LatLng northeast;
  public final LatLng southwest;
  
  LatLngBounds(int paramInt, LatLng paramLatLng1, LatLng paramLatLng2)
  {
    if ((paramLatLng1 == null) || (paramLatLng2 != null)) {}
    for (;;)
    {
      try
      {
        if (paramLatLng2.latitude <= paramLatLng1.latitude)
        {
          Log.w("LatLngBounds", "southern latitude exceeds northern latitude (" + paramLatLng1.latitude + " > " + paramLatLng2.latitude + ")");
          this.b = false;
        }
        this.a = paramInt;
        this.southwest = paramLatLng1;
        this.northeast = paramLatLng2;
        return;
        Log.w("LatLngBounds", "null southwest");
        this.b = false;
      }
      catch (Exception localException)
      {
        cj.a(localException, "LatLngBounds", "LatLngBounds");
        continue;
      }
      Log.w("LatLngBounds", "null northeast");
      this.b = false;
    }
  }
  
  public LatLngBounds(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    this(1, paramLatLng1, paramLatLng2);
  }
  
  private boolean a(double paramDouble)
  {
    return (this.southwest.latitude <= paramDouble) && (paramDouble <= this.northeast.latitude);
  }
  
  private boolean a(LatLngBounds paramLatLngBounds)
  {
    boolean bool2 = false;
    if (paramLatLngBounds == null) {}
    while ((paramLatLngBounds.northeast == null) || (paramLatLngBounds.southwest == null) || (this.northeast == null) || (this.southwest == null)) {
      return false;
    }
    double d1 = paramLatLngBounds.northeast.longitude;
    double d2 = paramLatLngBounds.southwest.longitude;
    double d3 = this.northeast.longitude;
    double d4 = this.southwest.longitude;
    double d5 = this.northeast.longitude;
    double d6 = this.southwest.longitude;
    double d7 = paramLatLngBounds.northeast.longitude;
    double d8 = this.southwest.longitude;
    double d9 = paramLatLngBounds.northeast.latitude;
    double d10 = paramLatLngBounds.southwest.latitude;
    double d11 = this.northeast.latitude;
    double d12 = this.southwest.latitude;
    double d13 = this.northeast.latitude;
    double d14 = this.southwest.latitude;
    double d15 = paramLatLngBounds.northeast.latitude;
    double d16 = paramLatLngBounds.southwest.latitude;
    boolean bool1 = bool2;
    if (Math.abs(d1 + d2 - d3 - d4) < d5 - d6 + d7 - d8)
    {
      bool1 = bool2;
      if (Math.abs(d9 + d10 - d11 - d12) < d13 - d14 + d15 - d16) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean b(double paramDouble)
  {
    boolean bool = false;
    if (this.southwest.longitude <= this.northeast.longitude) {
      return (this.southwest.longitude <= paramDouble) && (paramDouble <= this.northeast.longitude);
    }
    if (this.southwest.longitude <= paramDouble) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (paramDouble <= this.northeast.longitude)) {
        bool = true;
      }
      return bool;
    }
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  private static double c(double paramDouble1, double paramDouble2)
  {
    return (paramDouble1 - paramDouble2 + 360.0D) % 360.0D;
  }
  
  private static double d(double paramDouble1, double paramDouble2)
  {
    return (paramDouble2 - paramDouble1 + 360.0D) % 360.0D;
  }
  
  int a()
  {
    return this.a;
  }
  
  public boolean contains(LatLng paramLatLng)
  {
    if (paramLatLng != null)
    {
      if (!this.b) {
        break label26;
      }
      if (a(paramLatLng.latitude)) {
        break label36;
      }
    }
    label26:
    label36:
    while (!b(paramLatLng.longitude))
    {
      return false;
      return false;
      Log.w("LatLngBounds", "this LatLngBounds is invalid!");
      return false;
    }
    return true;
  }
  
  public boolean contains(LatLngBounds paramLatLngBounds)
  {
    if (paramLatLngBounds != null)
    {
      if (!this.b) {
        break label26;
      }
      if (contains(paramLatLngBounds.southwest)) {
        break label36;
      }
    }
    label26:
    label36:
    while (!contains(paramLatLngBounds.northeast))
    {
      return false;
      return false;
      Log.w("LatLngBounds", "this LatLngBounds is invalid!");
      return false;
    }
    return true;
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
      if (!(paramObject instanceof LatLngBounds)) {
        break label39;
      }
      paramObject = (LatLngBounds)paramObject;
      if (this.southwest.equals(((LatLngBounds)paramObject).southwest)) {
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
      } while (this.northeast.equals(((LatLngBounds)paramObject).northeast));
    }
  }
  
  public int hashCode()
  {
    return cj.a(new Object[] { this.southwest, this.northeast });
  }
  
  public LatLngBounds including(LatLng paramLatLng)
  {
    double d1;
    double d2;
    double d3;
    double d4;
    double d5;
    if (paramLatLng != null)
    {
      if (!this.b) {
        break label106;
      }
      d1 = Math.min(this.southwest.latitude, paramLatLng.latitude);
      d2 = Math.max(this.northeast.latitude, paramLatLng.latitude);
      d3 = this.northeast.longitude;
      d4 = this.southwest.longitude;
      d5 = paramLatLng.longitude;
      if (!b(d5)) {
        break label116;
      }
    }
    for (;;)
    {
      return new LatLngBounds(new LatLng(d1, d5), new LatLng(d2, d5));
      return this;
      label106:
      Log.w("LatLngBounds", "this LatLngBounds is invalid!");
      return null;
      label116:
      if (c(d4, d5) >= d(d3, d5)) {}
    }
  }
  
  public boolean intersects(LatLngBounds paramLatLngBounds)
  {
    if (paramLatLngBounds != null)
    {
      if (!this.b) {
        break label23;
      }
      if (!a(paramLatLngBounds)) {
        break label33;
      }
    }
    label23:
    label33:
    while (paramLatLngBounds.a(this))
    {
      return true;
      return false;
      Log.w("LatLngBounds", "this LatLngBounds is invalid!");
      return false;
    }
    return false;
  }
  
  public String toString()
  {
    return cj.a(new String[] { cj.a("southwest", this.southwest), cj.a("northeast", this.northeast) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    e.a(this, paramParcel, paramInt);
  }
  
  public static final class Builder
  {
    private double a = Double.POSITIVE_INFINITY;
    private double b = Double.NEGATIVE_INFINITY;
    private double c = NaN.0D;
    private double d = NaN.0D;
    
    private boolean a(double paramDouble)
    {
      boolean bool = false;
      if (this.c <= this.d) {
        return (this.c <= paramDouble) && (paramDouble <= this.d);
      }
      if (this.c <= paramDouble) {}
      for (int i = 1;; i = 0)
      {
        if ((i != 0) || (paramDouble <= this.d)) {
          bool = true;
        }
        return bool;
      }
    }
    
    public LatLngBounds build()
    {
      try
      {
        if (!Double.isNaN(this.c)) {
          return new LatLngBounds(new LatLng(this.a, this.c), new LatLng(this.b, this.d));
        }
        Log.w("LatLngBounds", "no included points");
        return null;
      }
      catch (Exception localException)
      {
        cj.a(localException, "LatLngBounds", "build");
      }
      return null;
    }
    
    public Builder include(LatLng paramLatLng)
    {
      this.a = Math.min(this.a, paramLatLng.latitude);
      this.b = Math.max(this.b, paramLatLng.latitude);
      double d1 = paramLatLng.longitude;
      if (!Double.isNaN(this.c))
      {
        if (a(d1)) {
          return this;
        }
      }
      else
      {
        this.c = d1;
        this.d = d1;
        return this;
      }
      if (LatLngBounds.a(this.c, d1) < LatLngBounds.b(this.d, d1))
      {
        this.c = d1;
        return this;
      }
      this.d = d1;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/LatLngBounds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */