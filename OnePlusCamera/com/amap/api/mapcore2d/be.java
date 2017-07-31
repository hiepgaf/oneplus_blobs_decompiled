package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Join;
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

class be
  implements af
{
  private b a;
  private float b = 10.0F;
  private int c = -16777216;
  private float d = 0.0F;
  private boolean e = true;
  private boolean f = false;
  private boolean g = false;
  private String h;
  private List<ad> i = new ArrayList();
  private List<LatLng> j = new ArrayList();
  private LatLngBounds k = null;
  
  public be(b paramb)
  {
    this.a = paramb;
    try
    {
      this.h = c();
      return;
    }
    catch (RemoteException paramb)
    {
      cj.a(paramb, "PolylineDelegateImp", "PolylineDelegateImp");
    }
  }
  
  private List<LatLng> m()
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
  
  ad a(ad paramad1, ad paramad2, ad paramad3, double paramDouble, int paramInt)
  {
    ad localad = new ad();
    double d1 = paramad2.a - paramad1.a;
    double d2 = paramad2.b - paramad1.b;
    double d3 = d2 * d2 / (d1 * d1);
    localad.b = ((int)(paramInt * paramDouble / Math.sqrt(d3 + 1.0D) + paramad3.b));
    localad.a = ((int)(d2 * (paramad3.b - localad.b) / d1 + paramad3.a));
    return localad;
  }
  
  public void a(float paramFloat)
    throws RemoteException
  {
    this.d = paramFloat;
    this.a.invalidate();
  }
  
  public void a(int paramInt)
    throws RemoteException
  {
    this.c = paramInt;
  }
  
  public void a(Canvas paramCanvas)
    throws RemoteException
  {
    if (this.i == null) {}
    while ((this.i.size() == 0) || (this.b <= 0.0F)) {
      return;
    }
    Path localPath = new Path();
    Object localObject = new u(((ad)this.i.get(0)).b, ((ad)this.i.get(0)).a);
    Point localPoint = new Point();
    localObject = this.a.s().a((u)localObject, localPoint);
    localPath.moveTo(((Point)localObject).x, ((Point)localObject).y);
    int m = 1;
    if (m >= this.i.size())
    {
      localObject = new Paint();
      ((Paint)localObject).setColor(h());
      ((Paint)localObject).setAntiAlias(true);
      ((Paint)localObject).setStrokeWidth(g());
      ((Paint)localObject).setStyle(Paint.Style.STROKE);
      ((Paint)localObject).setStrokeJoin(Paint.Join.ROUND);
      if (this.f) {
        break label291;
      }
    }
    for (;;)
    {
      paramCanvas.drawPath(localPath, (Paint)localObject);
      return;
      localObject = new u(((ad)this.i.get(m)).b, ((ad)this.i.get(m)).a);
      localPoint = new Point();
      localObject = this.a.s().a((u)localObject, localPoint);
      localPath.lineTo(((Point)localObject).x, ((Point)localObject).y);
      m += 1;
      break;
      label291:
      m = (int)g();
      ((Paint)localObject).setPathEffect(new DashPathEffect(new float[] { m * 3, m, m * 3, m }, 1.0F));
    }
  }
  
  void a(LatLng paramLatLng1, LatLng paramLatLng2, List<ad> paramList, LatLngBounds.Builder paramBuilder)
  {
    double d1 = Math.abs(paramLatLng1.longitude - paramLatLng2.longitude) * 3.141592653589793D / 180.0D;
    Object localObject = new LatLng((paramLatLng2.latitude + paramLatLng1.latitude) / 2.0D, (paramLatLng2.longitude + paramLatLng1.longitude) / 2.0D);
    paramBuilder.include(paramLatLng1).include((LatLng)localObject).include(paramLatLng2);
    if (((LatLng)localObject).latitude > 0.0D) {}
    for (int m = 1;; m = -1)
    {
      paramBuilder = new ad();
      this.a.a(paramLatLng1.latitude, paramLatLng1.longitude, paramBuilder);
      paramLatLng1 = new ad();
      this.a.a(paramLatLng2.latitude, paramLatLng2.longitude, paramLatLng1);
      paramLatLng2 = new ad();
      this.a.a(((LatLng)localObject).latitude, ((LatLng)localObject).longitude, paramLatLng2);
      double d2 = Math.cos(0.5D * d1);
      paramLatLng2 = a(paramBuilder, paramLatLng1, paramLatLng2, Math.hypot(paramBuilder.a - paramLatLng1.a, paramBuilder.b - paramLatLng1.b) * 0.5D * Math.tan(0.5D * d1), m);
      localObject = new ArrayList();
      ((List)localObject).add(paramBuilder);
      ((List)localObject).add(paramLatLng2);
      ((List)localObject).add(paramLatLng1);
      a((List)localObject, paramList, d2);
      return;
    }
  }
  
  public void a(List<LatLng> paramList)
    throws RemoteException
  {
    if (this.g) {
      this.j = paramList;
    }
    for (;;)
    {
      b(paramList);
      return;
      if (this.f) {
        break;
      }
    }
  }
  
  void a(List<ad> paramList1, List<ad> paramList2, double paramDouble)
  {
    if (paramList1.size() == 3) {}
    for (int m = 0;; m = (int)(m + 1.0F))
    {
      if (m > 10)
      {
        return;
        return;
      }
      float f1 = m / 10.0F;
      ad localad = new ad();
      double d1 = f1;
      double d2 = f1;
      double d3 = ((ad)paramList1.get(0)).a;
      double d4 = 2.0F * f1;
      double d5 = f1;
      double d6 = ((ad)paramList1.get(1)).a;
      double d7 = ((ad)paramList1.get(2)).a * (f1 * f1);
      double d8 = f1;
      double d9 = f1;
      double d10 = ((ad)paramList1.get(0)).b;
      double d11 = 2.0F * f1;
      double d12 = f1;
      double d13 = ((ad)paramList1.get(1)).b;
      double d14 = ((ad)paramList1.get(2)).b * (f1 * f1);
      double d15 = f1;
      double d16 = f1;
      double d17 = 2.0F * f1;
      double d18 = f1;
      double d19 = f1 * f1;
      double d20 = f1;
      double d21 = f1;
      double d22 = 2.0F * f1;
      double d23 = f1;
      double d24 = f1 * f1;
      localad.a = ((int)(((1.0D - d1) * (1.0D - d2) * d3 + d4 * (1.0D - d5) * d6 * paramDouble + d7) / ((1.0D - d15) * (1.0D - d16) + d17 * (1.0D - d18) * paramDouble + d19)));
      localad.b = ((int)(((1.0D - d8) * (1.0D - d9) * d10 + d11 * (1.0D - d12) * d13 * paramDouble + d14) / ((1.0D - d20) * (1.0D - d21) + d22 * (1.0D - d23) * paramDouble + d24)));
      paramList2.add(localad);
    }
  }
  
  public void a(boolean paramBoolean)
    throws RemoteException
  {
    this.e = paramBoolean;
  }
  
  public boolean a()
  {
    LatLngBounds localLatLngBounds;
    if (this.k != null)
    {
      localLatLngBounds = this.a.x();
      if (localLatLngBounds == null) {
        break label34;
      }
      if (!localLatLngBounds.contains(this.k)) {
        break label36;
      }
    }
    label34:
    label36:
    while (this.k.intersects(localLatLngBounds))
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
    this.a.a(c());
  }
  
  public void b(float paramFloat)
    throws RemoteException
  {
    this.b = paramFloat;
  }
  
  void b(List<LatLng> paramList)
    throws RemoteException
  {
    if (paramList == null) {}
    while (paramList.size() == 0) {
      return;
    }
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    this.i.clear();
    if (paramList == null)
    {
      if (this.i.size() > 0) {}
    }
    else
    {
      Iterator localIterator = paramList.iterator();
      paramList = null;
      label54:
      LatLng localLatLng;
      while (localIterator.hasNext())
      {
        localLatLng = (LatLng)localIterator.next();
        if ((localLatLng != null) && (!localLatLng.equals(paramList)))
        {
          if (!this.g) {
            break label103;
          }
          if (paramList != null) {
            break label147;
          }
        }
      }
      for (;;)
      {
        paramList = localLatLng;
        break label54;
        break;
        label103:
        paramList = new ad();
        this.a.a(localLatLng.latitude, localLatLng.longitude, paramList);
        this.i.add(paramList);
        localBuilder.include(localLatLng);
        continue;
        label147:
        if (Math.abs(localLatLng.longitude - paramList.longitude) < 0.01D)
        {
          ad localad = new ad();
          this.a.a(paramList.latitude, paramList.longitude, localad);
          this.i.add(localad);
          localBuilder.include(paramList);
          paramList = new ad();
          this.a.a(localLatLng.latitude, localLatLng.longitude, paramList);
          this.i.add(paramList);
          localBuilder.include(localLatLng);
        }
        else
        {
          a(paramList, localLatLng, this.i, localBuilder);
        }
      }
    }
    this.k = localBuilder.build();
  }
  
  public void b(boolean paramBoolean)
  {
    this.f = paramBoolean;
  }
  
  public String c()
    throws RemoteException
  {
    if (this.h != null) {}
    for (;;)
    {
      return this.h;
      this.h = t.a("Polyline");
    }
  }
  
  public void c(boolean paramBoolean)
    throws RemoteException
  {
    if (this.g == paramBoolean) {
      return;
    }
    this.g = paramBoolean;
  }
  
  public float d()
    throws RemoteException
  {
    return this.d;
  }
  
  public boolean e()
    throws RemoteException
  {
    return this.e;
  }
  
  public int f()
    throws RemoteException
  {
    return super.hashCode();
  }
  
  public float g()
    throws RemoteException
  {
    return this.b;
  }
  
  public int h()
    throws RemoteException
  {
    return this.c;
  }
  
  public List<LatLng> i()
    throws RemoteException
  {
    if (this.g) {}
    while (this.f) {
      return this.j;
    }
    return m();
  }
  
  public boolean j()
  {
    return this.f;
  }
  
  public boolean k()
  {
    return this.g;
  }
  
  public void l() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/be.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */