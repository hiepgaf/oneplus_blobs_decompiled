package com.amap.api.mapcore2d;

import android.graphics.Color;
import android.os.RemoteException;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

class bb
{
  private w a;
  private aa b;
  private x c;
  private MyLocationStyle d;
  private LatLng e;
  private double f;
  
  bb(w paramw)
  {
    this.a = paramw;
  }
  
  private void b()
  {
    if (this.d != null)
    {
      d();
      return;
    }
    c();
  }
  
  private void c()
  {
    try
    {
      this.c = this.a.a(new CircleOptions().strokeWidth(1.0F).fillColor(Color.argb(20, 0, 0, 180)).strokeColor(Color.argb(255, 0, 0, 220)).center(new LatLng(0.0D, 0.0D)));
      this.c.a(200.0D);
      this.b = this.a.b(new MarkerOptions().anchor(0.5F, 0.5F).icon(BitmapDescriptorFactory.fromAsset(ah.a.c.name() + ".png")).position(new LatLng(0.0D, 0.0D)));
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MyLocationOverlay", "defaultLocStyle");
    }
  }
  
  private void d()
  {
    if (this.d != null)
    {
      try
      {
        this.c = this.a.a(new CircleOptions().strokeWidth(this.d.getStrokeWidth()).fillColor(this.d.getRadiusFillColor()).strokeColor(this.d.getStrokeColor()).center(new LatLng(0.0D, 0.0D)));
        if (this.e == null) {}
        for (;;)
        {
          this.c.a(this.f);
          this.b = this.a.b(new MarkerOptions().anchor(this.d.getAnchorU(), this.d.getAnchorV()).icon(this.d.getMyLocationIcon()).position(new LatLng(0.0D, 0.0D)));
          if (this.e != null) {
            break;
          }
          return;
          this.c.a(this.e);
        }
        this.b.b(this.e);
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        return;
      }
      return;
    }
  }
  
  public void a()
    throws RemoteException
  {
    if (this.c == null) {}
    while (this.b == null)
    {
      return;
      this.a.a(this.c.c());
      this.c = null;
    }
    this.a.b(this.b.d());
    this.b = null;
  }
  
  public void a(float paramFloat)
  {
    if (this.b == null) {
      return;
    }
    try
    {
      this.b.a(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MyLocationOverlay", "setRotateAngle");
    }
  }
  
  public void a(LatLng paramLatLng, double paramDouble)
  {
    this.e = paramLatLng;
    this.f = paramDouble;
    if (this.b != null) {}
    while (this.b != null)
    {
      this.b.b(paramLatLng);
      try
      {
        this.c.a(paramLatLng);
        if (paramDouble != -1.0D) {
          this.c.a(paramDouble);
        }
        return;
      }
      catch (RemoteException paramLatLng)
      {
        cj.a(paramLatLng, "MyLocationOverlay", "setCentAndRadius");
      }
      if (this.c == null) {
        b();
      }
    }
    return;
  }
  
  public void a(MyLocationStyle paramMyLocationStyle)
  {
    this.d = paramMyLocationStyle;
    if (this.b != null) {}
    try
    {
      do
      {
        a();
        d();
        return;
      } while (this.c != null);
      return;
    }
    catch (RemoteException paramMyLocationStyle)
    {
      for (;;)
      {
        cj.a(paramMyLocationStyle, "MyLocationOverlay", "setMyLocationStyle");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */