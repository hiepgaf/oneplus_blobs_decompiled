package com.amap.api.mapcore2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.RemoteException;
import android.util.Log;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;

class v
  implements y
{
  private final double a = 0.01745329251994329D;
  private final double b = 6371000.79D;
  private b c;
  private BitmapDescriptor d;
  private LatLng e;
  private float f;
  private float g;
  private LatLngBounds h;
  private float i;
  private float j;
  private boolean k = true;
  private float l = 0.0F;
  private float m = 0.5F;
  private float n = 0.5F;
  private String o;
  private Bitmap p;
  
  v(b paramb)
  {
    this.c = paramb;
    try
    {
      this.o = c();
      return;
    }
    catch (RemoteException paramb)
    {
      cj.a(paramb, "GroundOverlayDelegateImp", "GroundOverlayDelegateImp");
    }
  }
  
  private u b(LatLng paramLatLng)
  {
    if (paramLatLng != null) {
      return new u((int)(paramLatLng.latitude * 1000000.0D), (int)(paramLatLng.longitude * 1000000.0D));
    }
    return null;
  }
  
  private void o()
  {
    double d1 = this.f / (Math.cos(this.e.latitude * 0.01745329251994329D) * 6371000.79D * 0.01745329251994329D);
    double d2 = this.g / 111194.94043265979D;
    LatLng localLatLng = new LatLng(this.e.latitude - (1.0F - this.n) * d2, this.e.longitude - this.m * d1);
    double d3 = this.e.latitude;
    double d4 = this.n;
    double d5 = this.e.longitude;
    this.h = new LatLngBounds(localLatLng, new LatLng(d2 * d4 + d3, d1 * (1.0F - this.m) + d5));
  }
  
  private void p()
  {
    LatLng localLatLng1 = this.h.southwest;
    LatLng localLatLng2 = this.h.northeast;
    this.e = new LatLng(localLatLng1.latitude + (1.0F - this.n) * (localLatLng2.latitude - localLatLng1.latitude), localLatLng1.longitude + this.m * (localLatLng2.longitude - localLatLng1.longitude));
    this.f = ((float)(Math.cos(this.e.latitude * 0.01745329251994329D) * 6371000.79D * (localLatLng2.longitude - localLatLng1.longitude) * 0.01745329251994329D));
    this.g = ((float)((localLatLng2.latitude - localLatLng1.latitude) * 6371000.79D * 0.01745329251994329D));
  }
  
  public void a(float paramFloat)
    throws RemoteException
  {
    this.j = paramFloat;
    this.c.postInvalidate();
  }
  
  public void a(float paramFloat1, float paramFloat2)
    throws RemoteException
  {
    if (paramFloat1 <= 0.0F) {}
    for (int i1 = 1;; i1 = 0)
    {
      if ((i1 != 0) || (paramFloat2 <= 0.0F)) {
        Log.w("GroundOverlayDelegateImp", "Width and Height must be non-negative");
      }
      if ((this.f == paramFloat1) || (this.g == paramFloat2)) {
        break;
      }
      this.f = paramFloat1;
      this.g = paramFloat2;
      return;
    }
    this.f = paramFloat1;
    this.g = paramFloat2;
  }
  
  public void a(Canvas paramCanvas)
    throws RemoteException
  {
    if (!this.k) {}
    do
    {
      return;
      while (this.d == null) {
        if (this.e == null) {
          break;
        }
      }
      g();
      if ((this.f != 0.0F) || (this.g != 0.0F)) {
        break;
      }
      return;
    } while (this.h != null);
    return;
    this.p = this.d.getBitmap();
    if (this.p == null) {}
    while (this.p.isRecycled()) {
      return;
    }
    Object localObject1 = this.h.southwest;
    Object localObject3 = this.h.northeast;
    Object localObject2 = this.e;
    localObject1 = b((LatLng)localObject1);
    localObject3 = b((LatLng)localObject3);
    u localu = b((LatLng)localObject2);
    Point localPoint1 = new Point();
    Point localPoint2 = new Point();
    localObject2 = new Point();
    this.c.s().a((u)localObject1, localPoint1);
    this.c.s().a((u)localObject3, localPoint2);
    this.c.s().a(localu, (Point)localObject2);
    localObject1 = new Paint();
    localObject3 = new RectF(localPoint1.x, localPoint2.y, localPoint2.x, localPoint1.y);
    ((Paint)localObject1).setAlpha((int)(255.0F - this.l * 255.0F));
    ((Paint)localObject1).setFilterBitmap(true);
    paramCanvas.save();
    paramCanvas.rotate(this.i, ((Point)localObject2).x, ((Point)localObject2).y);
    paramCanvas.drawBitmap(this.p, null, (RectF)localObject3, (Paint)localObject1);
    paramCanvas.restore();
  }
  
  public void a(BitmapDescriptor paramBitmapDescriptor)
    throws RemoteException
  {
    this.d = paramBitmapDescriptor;
  }
  
  public void a(LatLng paramLatLng)
    throws RemoteException
  {
    if (this.e == null) {}
    while (this.e.equals(paramLatLng))
    {
      this.e = paramLatLng;
      return;
    }
    this.e = paramLatLng;
    o();
  }
  
  public void a(LatLngBounds paramLatLngBounds)
    throws RemoteException
  {
    if (this.h == null) {}
    while (this.h.equals(paramLatLngBounds))
    {
      this.h = paramLatLngBounds;
      return;
    }
    this.h = paramLatLngBounds;
    p();
  }
  
  public void a(boolean paramBoolean)
    throws RemoteException
  {
    this.k = paramBoolean;
    this.c.postInvalidate();
  }
  
  public boolean a()
  {
    LatLngBounds localLatLngBounds;
    if (this.h != null)
    {
      localLatLngBounds = this.c.x();
      if (localLatLngBounds == null) {
        break label34;
      }
      if (!localLatLngBounds.contains(this.h)) {
        break label36;
      }
    }
    label34:
    label36:
    while (this.h.intersects(localLatLngBounds))
    {
      return true;
      return false;
      return true;
    }
    return false;
  }
  
  public boolean a(ac paramac)
    throws RemoteException
  {
    if (equals(paramac)) {}
    while (paramac.c().equals(c())) {
      return true;
    }
    return false;
  }
  
  public void b()
    throws RemoteException
  {
    this.c.a(c());
  }
  
  public void b(float paramFloat)
    throws RemoteException
  {
    if (paramFloat <= 0.0F) {
      Log.w("GroundOverlayDelegateImp", "Width must be non-negative");
    }
    if (this.f != paramFloat)
    {
      this.f = paramFloat;
      this.g = paramFloat;
      return;
    }
    this.f = paramFloat;
    this.g = paramFloat;
  }
  
  public void b(float paramFloat1, float paramFloat2)
    throws RemoteException
  {
    this.m = paramFloat1;
    this.n = paramFloat2;
  }
  
  public String c()
    throws RemoteException
  {
    if (this.o != null) {}
    for (;;)
    {
      return this.o;
      this.o = t.a("GroundOverlay");
    }
  }
  
  public void c(float paramFloat)
    throws RemoteException
  {
    paramFloat = (-paramFloat % 360.0F + 360.0F) % 360.0F;
    if (Double.doubleToLongBits(this.i) != Double.doubleToLongBits(paramFloat))
    {
      this.i = paramFloat;
      return;
    }
    this.i = paramFloat;
  }
  
  public float d()
    throws RemoteException
  {
    return this.j;
  }
  
  public void d(float paramFloat)
    throws RemoteException
  {
    if (paramFloat < 0.0F) {
      Log.w("GroundOverlayDelegateImp", "Transparency must be in the range [0..1]");
    }
    this.l = paramFloat;
  }
  
  public boolean e()
    throws RemoteException
  {
    return this.k;
  }
  
  public int f()
    throws RemoteException
  {
    return super.hashCode();
  }
  
  public void g()
    throws RemoteException
  {
    if (this.e != null)
    {
      if (this.h == null) {}
    }
    else
    {
      p();
      return;
    }
    o();
  }
  
  public LatLng h()
    throws RemoteException
  {
    return this.e;
  }
  
  public float i()
    throws RemoteException
  {
    return this.f;
  }
  
  public float j()
    throws RemoteException
  {
    return this.g;
  }
  
  public LatLngBounds k()
    throws RemoteException
  {
    return this.h;
  }
  
  public void l()
  {
    try
    {
      b();
      if (this.d == null) {}
      for (;;)
      {
        this.e = null;
        this.h = null;
        return;
        Bitmap localBitmap = this.d.getBitmap();
        if (localBitmap != null)
        {
          localBitmap.recycle();
          this.d = null;
        }
      }
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "GroundOverlayDelegateImp", "destroy");
      Log.d("destroy erro", "GroundOverlayDelegateImp destroy");
    }
  }
  
  public float m()
    throws RemoteException
  {
    return this.i;
  }
  
  public float n()
    throws RemoteException
  {
    return this.l;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/v.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */