package com.amap.api.mapcore2d;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class t
{
  private static int a = 0;
  private CopyOnWriteArrayList<ac> b = new CopyOnWriteArrayList();
  private a c = new a(null);
  private Handler d = new Handler();
  private Runnable e = new Runnable()
  {
    public void run()
    {
      for (;;)
      {
        try
        {
          arrayOfObject = t.a(t.this).toArray();
          Arrays.sort(arrayOfObject, t.b(t.this));
          t.a(t.this).clear();
          int j = arrayOfObject.length;
          i = 0;
          if (i < j) {
            continue;
          }
        }
        catch (Throwable localThrowable)
        {
          Object[] arrayOfObject;
          int i;
          Object localObject2;
          db.b(localThrowable, "MapOverlayImageView", "changeOverlayIndex");
          continue;
        }
        finally {}
        return;
        localObject2 = arrayOfObject[i];
        t.a(t.this).add((ac)localObject2);
        i += 1;
      }
    }
  };
  
  static String a(String paramString)
  {
    a += 1;
    return paramString + a;
  }
  
  private ac c(String paramString)
    throws RemoteException
  {
    Iterator localIterator = this.b.iterator();
    ac localac;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localac = (ac)localIterator.next();
    } while ((localac == null) || (!localac.c().equals(paramString)));
    return localac;
  }
  
  private void c()
  {
    this.d.removeCallbacks(this.e);
    this.d.postDelayed(this.e, 10L);
  }
  
  public void a()
  {
    Iterator localIterator = this.b.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {}
      try
      {
        localIterator = this.b.iterator();
        for (;;)
        {
          if (!localIterator.hasNext())
          {
            this.b.clear();
            return;
            ((ac)localIterator.next()).l();
            break;
          }
          ((ac)localIterator.next()).l();
        }
        return;
      }
      catch (Exception localException)
      {
        cj.a(localException, "GLOverlayLayer", "clear");
        Log.d("amapApi", "GLOverlayLayer clear erro" + localException.getMessage());
      }
    }
  }
  
  public void a(Canvas paramCanvas)
  {
    Object localObject1 = this.b.toArray();
    Arrays.sort((Object[])localObject1, this.c);
    this.b.clear();
    int j = localObject1.length;
    int i = 0;
    if (i >= j)
    {
      i = this.b.size();
      localObject1 = this.b.iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        return;
        Object localObject2 = localObject1[i];
        try
        {
          this.b.add((ac)localObject2);
          i += 1;
        }
        catch (Throwable localThrowable)
        {
          for (;;)
          {
            cj.a(localThrowable, "GLOverlayLayer", "draw");
          }
        }
      }
      ac localac = (ac)((Iterator)localObject1).next();
      try
      {
        if (!localac.e()) {
          continue;
        }
        if (i > 20) {
          break label157;
        }
        localac.a(paramCanvas);
      }
      catch (RemoteException localRemoteException)
      {
        cj.a(localRemoteException, "GLOverlayLayer", "draw");
      }
      continue;
      label157:
      if (localRemoteException.a()) {
        localRemoteException.a(paramCanvas);
      }
    }
  }
  
  public void a(ac paramac)
    throws RemoteException
  {
    b(paramac.c());
    this.b.add(paramac);
    c();
  }
  
  public void b()
  {
    try
    {
      Iterator localIterator = this.b.iterator();
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          a();
          return;
        }
        ((ac)localIterator.next()).l();
      }
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "GLOverlayLayer", "destory");
      Log.d("amapApi", "GLOverlayLayer destory erro" + localException.getMessage());
    }
  }
  
  public boolean b(String paramString)
    throws RemoteException
  {
    paramString = c(paramString);
    if (paramString == null) {
      return false;
    }
    return this.b.remove(paramString);
  }
  
  private class a
    implements Comparator<Object>
  {
    private a() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      paramObject1 = (ac)paramObject1;
      paramObject2 = (ac)paramObject2;
      if (paramObject1 == null) {}
      for (;;)
      {
        return 0;
        if (paramObject2 != null) {
          try
          {
            if (((ac)paramObject1).d() > ((ac)paramObject2).d()) {
              return 1;
            }
            float f1 = ((ac)paramObject1).d();
            float f2 = ((ac)paramObject2).d();
            if (f1 < f2) {
              return -1;
            }
          }
          catch (Exception paramObject1)
          {
            cj.a((Throwable)paramObject1, "GLOverlayLayer", "compare");
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/t.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */