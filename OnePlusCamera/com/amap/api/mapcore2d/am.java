package com.amap.api.mapcore2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import com.amap.api.maps2d.model.TileProvider;
import java.util.Iterator;

class am
  extends an
{
  au a;
  public String b = "";
  public int c = 18;
  public int d = 3;
  public boolean e = true;
  public boolean f = false;
  public boolean g = false;
  public boolean h = true;
  public long i = 0L;
  public by j = null;
  TileProvider k = null;
  public int l = -1;
  public String m = "";
  az n = null;
  s o = null;
  bk<bp> p = null;
  private String r = "LayerPropertys";
  private boolean s = false;
  
  public am(au paramau)
  {
    this.a = paramau;
  }
  
  protected void a(Canvas paramCanvas)
  {
    for (;;)
    {
      try
      {
        if (this.p == null) {
          break;
        }
        Iterator localIterator = this.p.iterator();
        if (!localIterator.hasNext()) {
          return;
        }
        Object localObject = (bp)localIterator.next();
        if (((bp)localObject).h >= 0)
        {
          Bitmap localBitmap = this.n.a(((bp)localObject).h);
          localObject = this.a.a(((bp)localObject).b, ((bp)localObject).c);
          if ((localBitmap == null) || (localObject == null)) {
            continue;
          }
          float f1 = ((PointF)localObject).x;
          float f2 = this.a.a;
          float f3 = ((PointF)localObject).y;
          float f4 = this.a.a;
          paramCanvas.drawBitmap(localBitmap, null, new RectF(((PointF)localObject).x, ((PointF)localObject).y, f1 + f2, f3 + f4), null);
          continue;
        }
        bool = this.e;
      }
      catch (Throwable paramCanvas)
      {
        cj.a(paramCanvas, this.r, "drawLayer");
        return;
      }
      boolean bool;
      if (!bool) {}
    }
  }
  
  void a(boolean paramBoolean)
  {
    this.s = paramBoolean;
    if (!paramBoolean)
    {
      this.n.c();
      this.q.c();
      return;
    }
    this.q.d();
  }
  
  boolean a()
  {
    return this.s;
  }
  
  protected void b()
  {
    this.q.e();
    this.o.a(null);
    this.n.c();
    this.p.clear();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this != paramObject)
    {
      if ((paramObject instanceof am))
      {
        paramObject = (am)paramObject;
        return this.b.equals(((am)paramObject).b);
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.l;
  }
  
  public String toString()
  {
    return this.b;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/am.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */