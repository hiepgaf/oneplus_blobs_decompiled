package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class br
  extends View
{
  CopyOnWriteArrayList<Integer> a = new CopyOnWriteArrayList();
  private w b;
  private CopyOnWriteArrayList<aj> c = new CopyOnWriteArrayList();
  private a d = new a(null);
  
  public br(Context paramContext, w paramw)
  {
    super(paramContext);
    this.b = paramw;
  }
  
  protected void a(Canvas paramCanvas)
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aj localaj = (aj)localIterator.next();
      if (localaj.e()) {
        localaj.a(paramCanvas);
      }
    }
  }
  
  public void a(aj paramaj)
  {
    b(paramaj);
    this.c.add(paramaj);
    c();
  }
  
  public void a(boolean paramBoolean)
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aj localaj = (aj)localIterator.next();
      if ((localaj != null) && (localaj.e())) {
        localaj.b(paramBoolean);
      }
    }
  }
  
  protected boolean a()
  {
    return this.c.size() > 0;
  }
  
  public void b()
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        this.c.clear();
        return;
      }
      aj localaj = (aj)localIterator.next();
      if (localaj != null) {
        localaj.a();
      }
    }
  }
  
  public boolean b(aj paramaj)
  {
    return this.c.remove(paramaj);
  }
  
  void c()
  {
    Object[] arrayOfObject = this.c.toArray();
    Arrays.sort(arrayOfObject, this.d);
    this.c.clear();
    int j = arrayOfObject.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      Object localObject = arrayOfObject[i];
      this.c.add((aj)localObject);
      i += 1;
    }
  }
  
  public void d()
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aj localaj = (aj)localIterator.next();
      if (localaj != null) {
        localaj.g();
      }
    }
  }
  
  public void e()
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aj localaj = (aj)localIterator.next();
      if (localaj != null) {
        localaj.h();
      }
    }
  }
  
  public void f()
  {
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aj localaj = (aj)localIterator.next();
      if (localaj != null) {
        localaj.i();
      }
    }
  }
  
  private class a
    implements Comparator<Object>
  {
    private a() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      paramObject1 = (aj)paramObject1;
      paramObject2 = (aj)paramObject2;
      if (paramObject1 == null) {}
      for (;;)
      {
        return 0;
        if (paramObject2 != null) {
          try
          {
            if (((aj)paramObject1).d() > ((aj)paramObject2).d()) {
              return 1;
            }
            float f1 = ((aj)paramObject1).d();
            float f2 = ((aj)paramObject2).d();
            if (f1 < f2) {
              return -1;
            }
          }
          catch (Exception paramObject1)
          {
            cj.a((Throwable)paramObject1, "TileOverlayView", "compare");
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/br.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */