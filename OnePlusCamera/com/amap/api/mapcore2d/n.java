package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.RemoteException;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

class n
  implements x
{
  private LatLng a = null;
  private double b = 0.0D;
  private float c = 10.0F;
  private int d = -16777216;
  private int e = 0;
  private float f = 0.0F;
  private boolean g = true;
  private String h;
  private b i;
  
  public n(b paramb)
  {
    this.i = paramb;
    try
    {
      this.h = c();
      return;
    }
    catch (RemoteException paramb)
    {
      cj.a(paramb, "CircleDelegateImp", "CircleDelegateIme");
    }
  }
  
  public void a(double paramDouble)
    throws RemoteException
  {
    this.b = paramDouble;
  }
  
  public void a(float paramFloat)
    throws RemoteException
  {
    this.f = paramFloat;
    this.i.invalidate();
  }
  
  public void a(int paramInt)
    throws RemoteException
  {
    this.d = paramInt;
  }
  
  public void a(Canvas paramCanvas)
    throws RemoteException
  {
    int j = 0;
    if (g() == null) {}
    do
    {
      return;
      if (this.b <= 0.0D) {
        j = 1;
      }
    } while ((j != 0) || (!e()));
    float f1 = this.i.b().b.a((float)h());
    Object localObject = new u((int)(this.a.latitude * 1000000.0D), (int)(this.a.longitude * 1000000.0D));
    Point localPoint = new Point();
    this.i.s().a((u)localObject, localPoint);
    localObject = new Paint();
    ((Paint)localObject).setColor(k());
    ((Paint)localObject).setAntiAlias(true);
    ((Paint)localObject).setStyle(Paint.Style.FILL);
    paramCanvas.drawCircle(localPoint.x, localPoint.y, f1, (Paint)localObject);
    ((Paint)localObject).setColor(j());
    ((Paint)localObject).setStyle(Paint.Style.STROKE);
    ((Paint)localObject).setStrokeWidth(i());
    paramCanvas.drawCircle(localPoint.x, localPoint.y, f1, (Paint)localObject);
  }
  
  public void a(LatLng paramLatLng)
    throws RemoteException
  {
    this.a = paramLatLng;
  }
  
  public void a(boolean paramBoolean)
    throws RemoteException
  {
    this.g = paramBoolean;
    this.i.postInvalidate();
  }
  
  public boolean a()
  {
    return true;
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
    this.i.a(c());
    this.i.postInvalidate();
  }
  
  public void b(float paramFloat)
    throws RemoteException
  {
    this.c = paramFloat;
  }
  
  public void b(int paramInt)
    throws RemoteException
  {
    this.e = paramInt;
  }
  
  public boolean b(LatLng paramLatLng)
    throws RemoteException
  {
    return this.b >= AMapUtils.calculateLineDistance(this.a, paramLatLng);
  }
  
  public String c()
    throws RemoteException
  {
    if (this.h != null) {}
    for (;;)
    {
      return this.h;
      this.h = t.a("Circle");
    }
  }
  
  public float d()
    throws RemoteException
  {
    return this.f;
  }
  
  public boolean e()
    throws RemoteException
  {
    return this.g;
  }
  
  public int f()
    throws RemoteException
  {
    return 0;
  }
  
  public LatLng g()
    throws RemoteException
  {
    return this.a;
  }
  
  public double h()
    throws RemoteException
  {
    return this.b;
  }
  
  public float i()
    throws RemoteException
  {
    return this.c;
  }
  
  public int j()
    throws RemoteException
  {
    return this.d;
  }
  
  public int k()
    throws RemoteException
  {
    return this.e;
  }
  
  public void l()
  {
    this.a = null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/n.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */