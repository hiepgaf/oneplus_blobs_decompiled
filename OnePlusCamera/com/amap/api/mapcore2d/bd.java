package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.RemoteException;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class bd
  implements ae
{
  private b a;
  private float b = 0.0F;
  private boolean c = true;
  private String d;
  private float e;
  private int f;
  private int g;
  private List<LatLng> h;
  private List<ad> i = new ArrayList();
  private LatLngBounds j = null;
  
  public bd(b paramb)
  {
    this.a = paramb;
    try
    {
      this.d = c();
      return;
    }
    catch (RemoteException paramb)
    {
      cj.a(paramb, "PolygonDelegateImp", "PolygonDelegateImp");
    }
  }
  
  public void a(float paramFloat)
    throws RemoteException
  {
    this.b = paramFloat;
    this.a.invalidate();
  }
  
  public void a(int paramInt)
    throws RemoteException
  {
    this.f = paramInt;
  }
  
  public void a(Canvas paramCanvas)
    throws RemoteException
  {
    if (this.i == null) {}
    while (this.i.size() == 0) {
      return;
    }
    Path localPath = new Path();
    Object localObject = new u(((ad)this.i.get(0)).b, ((ad)this.i.get(0)).a);
    Point localPoint = new Point();
    localObject = this.a.s().a((u)localObject, localPoint);
    localPath.moveTo(((Point)localObject).x, ((Point)localObject).y);
    int k = 1;
    for (;;)
    {
      if (k >= this.i.size())
      {
        localObject = new Paint();
        ((Paint)localObject).setColor(h());
        ((Paint)localObject).setAntiAlias(true);
        localPath.close();
        ((Paint)localObject).setStyle(Paint.Style.FILL);
        paramCanvas.drawPath(localPath, (Paint)localObject);
        ((Paint)localObject).setStyle(Paint.Style.STROKE);
        ((Paint)localObject).setColor(j());
        ((Paint)localObject).setStrokeWidth(g());
        paramCanvas.drawPath(localPath, (Paint)localObject);
        return;
      }
      localObject = new u(((ad)this.i.get(k)).b, ((ad)this.i.get(k)).a);
      localPoint = new Point();
      localObject = this.a.s().a((u)localObject, localPoint);
      localPath.lineTo(((Point)localObject).x, ((Point)localObject).y);
      k += 1;
    }
  }
  
  public void a(List<LatLng> paramList)
    throws RemoteException
  {
    this.h = paramList;
    b(paramList);
  }
  
  public void a(boolean paramBoolean)
    throws RemoteException
  {
    this.c = paramBoolean;
  }
  
  public boolean a()
  {
    LatLngBounds localLatLngBounds;
    if (this.j != null)
    {
      localLatLngBounds = this.a.x();
      if (localLatLngBounds == null) {
        break label34;
      }
      if (!this.j.contains(localLatLngBounds)) {
        break label36;
      }
    }
    label34:
    label36:
    while (this.j.intersects(localLatLngBounds))
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
  
  public boolean a(LatLng paramLatLng)
    throws RemoteException
  {
    return cj.a(paramLatLng, i());
  }
  
  public void b()
    throws RemoteException
  {
    this.a.a(c());
  }
  
  public void b(float paramFloat)
    throws RemoteException
  {
    this.e = paramFloat;
  }
  
  public void b(int paramInt)
    throws RemoteException
  {
    this.g = paramInt;
  }
  
  void b(List<LatLng> paramList)
    throws RemoteException
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    this.i.clear();
    if (paramList == null)
    {
      this.j = localBuilder.build();
      return;
    }
    Iterator localIterator = paramList.iterator();
    paramList = null;
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        int k = this.i.size();
        if (k <= 1) {
          break;
        }
        paramList = (ad)this.i.get(0);
        localObject = (ad)this.i.get(k - 1);
        if ((paramList.a != ((ad)localObject).a) || (paramList.b != ((ad)localObject).b)) {
          break;
        }
        this.i.remove(k - 1);
        break;
      }
      Object localObject = (LatLng)localIterator.next();
      if (!((LatLng)localObject).equals(paramList))
      {
        paramList = new ad();
        this.a.a(((LatLng)localObject).latitude, ((LatLng)localObject).longitude, paramList);
        this.i.add(paramList);
        localBuilder.include((LatLng)localObject);
        paramList = (List<LatLng>)localObject;
      }
    }
  }
  
  public String c()
    throws RemoteException
  {
    if (this.d != null) {}
    for (;;)
    {
      return this.d;
      this.d = t.a("Polygon");
    }
  }
  
  public float d()
    throws RemoteException
  {
    return this.b;
  }
  
  public boolean e()
    throws RemoteException
  {
    return this.c;
  }
  
  public int f()
    throws RemoteException
  {
    return super.hashCode();
  }
  
  public float g()
    throws RemoteException
  {
    return this.e;
  }
  
  public int h()
    throws RemoteException
  {
    return this.f;
  }
  
  public List<LatLng> i()
    throws RemoteException
  {
    return k();
  }
  
  public int j()
    throws RemoteException
  {
    return this.g;
  }
  
  List<LatLng> k()
    throws RemoteException
  {
    if (this.i == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.i.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      ad localad = (ad)localIterator.next();
      if (localad != null)
      {
        r localr = new r();
        this.a.b(localad.a, localad.b, localr);
        localArrayList.add(new LatLng(localr.b, localr.a));
      }
    }
  }
  
  public void l() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */