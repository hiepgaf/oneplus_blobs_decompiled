package com.amap.api.mapcore2d;

import android.os.RemoteException;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;

class a
{
  private b a;
  private int b;
  
  public a(b paramb)
  {
    this.a = paramb;
  }
  
  protected void a(l paraml)
    throws RemoteException
  {
    for (;;)
    {
      float f1;
      try
      {
        if (this.a == null) {
          return;
        }
        if (this.a.D() == null) {
          break label437;
        }
        f1 = this.a.f();
        if (paraml.a != l.a.h)
        {
          if (paraml.a != l.a.b)
          {
            if (paraml.a == l.a.e) {
              break label198;
            }
            if (paraml.a == l.a.f) {
              break label212;
            }
            if (paraml.a == l.a.g) {
              break label232;
            }
            if (paraml.a == l.a.i) {
              break label287;
            }
            if (paraml.a == l.a.c) {
              break label348;
            }
            if (paraml.a != l.a.j) {
              break label411;
            }
            this.a.a(paraml, false, -1L);
            if (f1 == this.b) {
              return;
            }
            if (this.a.q().a()) {
              break;
            }
          }
        }
        else
        {
          this.a.b.b((int)paraml.b, (int)paraml.c);
          this.a.postInvalidate();
          continue;
        }
        this.a.D().c();
      }
      catch (Exception paraml)
      {
        cj.a(paraml, "AMapCallback", "runCameraUpdate");
        return;
      }
      continue;
      label198:
      this.a.D().d();
      continue;
      label212:
      float f2 = paraml.d;
      this.a.D().c(f2);
      continue;
      label232:
      f2 = paraml.e;
      f2 = this.a.a(f2 + f1);
      paraml = paraml.k;
      if (paraml == null)
      {
        this.a.D().c(f2);
      }
      else
      {
        this.a.a(f2 - f1, paraml, false);
        continue;
        label287:
        paraml = paraml.f;
        int i = (int)(paraml.target.latitude * 1000000.0D);
        int j = (int)(paraml.target.longitude * 1000000.0D);
        this.a.D().a(new u(i, j), paraml.zoom);
        continue;
        label348:
        paraml = paraml.f;
        i = (int)(paraml.target.latitude * 1000000.0D);
        j = (int)(paraml.target.longitude * 1000000.0D);
        this.a.D().a(new u(i, j));
        k.a().b();
        continue;
        label411:
        if (paraml.a != l.a.k) {
          paraml.l = true;
        }
      }
    }
    this.a.N();
    return;
    label437:
    return;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */