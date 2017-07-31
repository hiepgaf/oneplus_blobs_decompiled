package com.amap.api.mapcore2d;

import android.content.Context;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.MapsInitializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

abstract class e<T, V>
  extends av
{
  protected bl<T> a;
  private volatile boolean d = true;
  private Vector<Thread> e = null;
  private Runnable f = new Runnable()
  {
    public void run()
    {
      Object localObject1 = null;
      Object localObject3 = Thread.currentThread();
      if (localObject3 == null) {}
      for (;;)
      {
        boolean bool;
        try
        {
          if (e.a(e.this) == null)
          {
            break label376;
            bool = e.b(e.this);
            if (!bool)
            {
              return;
              ((Thread)localObject3).setName("TaskRunDownLoad");
            }
          }
          else
          {
            e.a(e.this).add(localObject3);
          }
        }
        catch (Throwable localThrowable)
        {
          cj.a(localThrowable, "AsyncServer", "run");
          return;
        }
        if (!Thread.interrupted())
        {
          Object localObject6;
          if (e.this.b != null)
          {
            if (!MapsInitializer.getNetworkEnable()) {
              break label149;
            }
            if (e.this.a != null) {
              break label195;
            }
            localObject6 = localObject3;
            break label381;
          }
          label108:
          label123:
          label149:
          label195:
          ArrayList localArrayList;
          for (;;)
          {
            if (!e.b(e.this)) {
              break label395;
            }
            if (localObject6 == null)
            {
              if (e.b(e.this) == true) {
                break label334;
              }
              break label389;
              e.a(e.this, false);
              break;
              try
              {
                if (Thread.interrupted()) {
                  break;
                }
                Thread.sleep(200L);
              }
              catch (InterruptedException localInterruptedException2)
              {
                Thread.currentThread().interrupt();
              }
              catch (Exception localException2)
              {
                cj.a(localException2, "AsyncServer", "run");
                Thread.currentThread().interrupt();
              }
              break;
              localArrayList = e.this.a.a(e.this.g(), false);
            }
            label334:
            label376:
            label381:
            while (localArrayList != null)
            {
              localObject3 = localArrayList;
              if (localArrayList.size() == 0) {
                break;
              }
              break label108;
              if (!e.b(e.this)) {
                return;
              }
              localObject3 = localArrayList;
              if (e.this.b == null) {
                break;
              }
              ay.c localc = e.this.b.f;
              localObject3 = localArrayList;
              if (localc == null) {
                break;
              }
              Object localObject4;
              try
              {
                localObject3 = e.this.a(localArrayList);
                localObject2 = localObject3;
                if (localObject3 == null) {
                  break label123;
                }
                localObject2 = localObject3;
                if (e.this.a == null) {
                  break label123;
                }
                e.this.a.a((List)localObject3, false);
                localObject2 = localObject3;
              }
              catch (AMapException localAMapException)
              {
                for (;;)
                {
                  Object localObject2;
                  cj.a(localAMapException, "AsyncServer", "run");
                  localObject4 = localObject2;
                }
              }
              bool = Thread.interrupted();
              if (bool) {
                break label389;
              }
              try
              {
                Thread.sleep(50L);
                localObject4 = localArrayList;
              }
              catch (InterruptedException localInterruptedException1)
              {
                for (;;)
                {
                  Thread.currentThread().interrupt();
                }
              }
              catch (Exception localException1)
              {
                for (;;)
                {
                  cj.a(localException1, "AsyncServer", "run");
                }
              }
              localObject5 = null;
              break;
            }
          }
          label389:
          Object localObject5 = localArrayList;
        }
      }
      label395:
      return;
    }
  };
  private Runnable g = new Runnable()
  {
    public void run()
    {
      Object localObject1 = null;
      Object localObject2 = Thread.currentThread();
      if (localObject2 == null) {}
      label98:
      label154:
      label176:
      label238:
      label280:
      label285:
      label292:
      for (;;)
      {
        boolean bool;
        try
        {
          if (e.a(e.this) == null)
          {
            break label280;
            bool = e.b(e.this);
            if (!bool)
            {
              return;
              ((Thread)localObject2).setName("TaskRunCach");
            }
          }
          else
          {
            e.a(e.this).add(localObject2);
          }
        }
        catch (Throwable localThrowable1)
        {
          cj.a(localThrowable1, "AsyncServer", "run");
          return;
        }
        if (!Thread.interrupted())
        {
          if (e.this.b != null)
          {
            if (e.this.a != null) {
              break label154;
            }
            break label285;
            break label176;
          }
          for (;;)
          {
            bool = e.b(e.this);
            if (!bool) {
              return;
            }
            ArrayList localArrayList1;
            try
            {
              ArrayList localArrayList2 = e.this.b(localThrowable1);
              localObject2 = localArrayList2;
            }
            catch (Throwable localThrowable2)
            {
              for (;;)
              {
                cj.a(localThrowable2, "AsyncServer", "run");
                continue;
                if ((e.this.a != null) && (cj.a(e.this.c))) {
                  e.this.a.a((List)localObject2, false);
                }
              }
              bool = Thread.interrupted();
              if (bool) {
                break label292;
              }
            }
            if (localObject2 == null)
            {
              if (e.b(e.this) == true) {
                break label238;
              }
              break label292;
              e.a(e.this, false);
              break;
              localArrayList1 = e.this.a.a(e.this.g(), true);
              break label285;
              if (localArrayList1.size() != 0) {
                continue;
              }
              break;
            }
            try
            {
              Thread.sleep(50L);
            }
            catch (InterruptedException localInterruptedException)
            {
              for (;;)
              {
                Thread.currentThread().interrupt();
              }
            }
            catch (Throwable localThrowable3)
            {
              for (;;)
              {
                cj.a(localThrowable3, "AsyncServer", "run");
              }
            }
            localObject2 = null;
            break;
            if (localArrayList1 != null) {
              break label98;
            }
          }
        }
      }
    }
  };
  private bn h;
  
  public e(ay paramay, Context paramContext)
  {
    super(paramay, paramContext);
  }
  
  protected abstract ArrayList<T> a(ArrayList<T> paramArrayList)
    throws AMapException;
  
  protected void a()
  {
    if (this.e != null) {}
    for (;;)
    {
      this.h = new bn(f(), this.g, this.f);
      this.h.a();
      return;
      this.e = new Vector();
    }
  }
  
  protected abstract ArrayList<T> b(ArrayList<T> paramArrayList)
    throws AMapException;
  
  public void b()
  {
    if (this.a == null)
    {
      e();
      if (this.a != null) {
        break label54;
      }
    }
    for (;;)
    {
      this.a = null;
      this.g = null;
      this.f = null;
      this.b = null;
      this.c = null;
      return;
      this.a.a();
      break;
      label54:
      this.a.c();
    }
  }
  
  public void c()
  {
    super.c();
    e();
  }
  
  public void d()
  {
    if (this.d != true)
    {
      this.d = true;
      if (this.e == null) {
        break label29;
      }
    }
    while (this.h != null)
    {
      return;
      return;
      label29:
      this.e = new Vector();
    }
    this.h = new bn(f(), this.g, this.f);
    this.h.a();
  }
  
  public void e()
  {
    this.d = false;
    if (this.e == null) {}
    while (this.h == null)
    {
      return;
      int j = this.e.size();
      int i = 0;
      if (i >= j)
      {
        this.e = null;
      }
      else
      {
        Thread localThread = (Thread)this.e.get(0);
        if (localThread == null) {}
        for (;;)
        {
          i += 1;
          break;
          localThread.interrupt();
          this.e.remove(0);
        }
      }
    }
    this.h.b();
    this.h = null;
  }
  
  protected abstract int f();
  
  protected abstract int g();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/e.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */