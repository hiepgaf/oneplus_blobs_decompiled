package com.amap.api.location;

import android.app.PendingIntent;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.amap.api.location.core.AMapLocException;
import com.aps.j;
import java.util.Iterator;
import java.util.Vector;

public class a
{
  static a h = null;
  d a = null;
  c b = null;
  boolean c = false;
  long d;
  boolean e = true;
  boolean f = true;
  b g;
  private Context i;
  private Vector<g> j = null;
  private a k = null;
  private Vector<g> l = new Vector();
  private AMapLocation m;
  private AMapLocation n;
  private volatile Thread o;
  private long p = 2000L;
  private float q = 10.0F;
  
  private a(Context paramContext, LocationManager paramLocationManager)
  {
    this.i = paramContext;
    e();
    if (Looper.myLooper() != null) {}
    for (this.k = new a();; this.k = new a(paramContext.getMainLooper()))
    {
      this.a = new d(paramContext, paramLocationManager, this.k, this);
      this.b = new c(paramContext, this.k, this);
      b(false);
      this.e = true;
      this.f = true;
      this.g = new b(this, paramContext);
      return;
    }
  }
  
  /* Error */
  public static a a(Context paramContext, LocationManager paramLocationManager)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   6: ifnull +12 -> 18
    //   9: getstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 2	com/amap/api/location/a
    //   21: dup
    //   22: aload_0
    //   23: aload_1
    //   24: invokespecial 120	com/amap/api/location/a:<init>	(Landroid/content/Context;Landroid/location/LocationManager;)V
    //   27: putstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   30: goto -21 -> 9
    //   33: astore_0
    //   34: ldc 2
    //   36: monitorexit
    //   37: aload_0
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	paramContext	Context
    //   0	39	1	paramLocationManager	LocationManager
    // Exception table:
    //   from	to	target	type
    //   3	9	33	finally
    //   9	13	33	finally
    //   18	30	33	finally
  }
  
  /* Error */
  static void c()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   6: ifnonnull +11 -> 17
    //   9: aconst_null
    //   10: putstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   13: ldc 2
    //   15: monitorexit
    //   16: return
    //   17: getstatic 42	com/amap/api/location/a:h	Lcom/amap/api/location/a;
    //   20: invokevirtual 126	com/amap/api/location/a:d	()V
    //   23: goto -14 -> 9
    //   26: astore_0
    //   27: ldc 2
    //   29: monitorexit
    //   30: aload_0
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   26	5	0	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	9	26	finally
    //   9	13	26	finally
    //   17	23	26	finally
  }
  
  private void c(boolean paramBoolean)
  {
    this.e = paramBoolean;
  }
  
  private void d(boolean paramBoolean)
  {
    this.f = paramBoolean;
  }
  
  private void e()
  {
    this.j = new Vector();
  }
  
  AMapLocation a()
  {
    if (this.m == null) {
      return com.amap.api.location.core.d.b(this.i);
    }
    return this.m;
  }
  
  void a(double paramDouble1, double paramDouble2, float paramFloat, long paramLong, PendingIntent paramPendingIntent)
  {
    j localj = new j();
    localj.b = paramDouble1;
    localj.a = paramDouble2;
    localj.c = paramFloat;
    localj.a(paramLong);
    this.b.a(localj, paramPendingIntent);
  }
  
  void a(final int paramInt, final AMapLocalWeatherListener paramAMapLocalWeatherListener)
  {
    try
    {
      new Thread()
      {
        public void run()
        {
          a.this.g.a(paramInt, paramAMapLocalWeatherListener, a.e(a.this));
        }
      }.start();
      return;
    }
    catch (Throwable paramAMapLocalWeatherListener)
    {
      paramAMapLocalWeatherListener.printStackTrace();
    }
  }
  
  void a(long paramLong, float paramFloat, AMapLocationListener paramAMapLocationListener, String paramString, boolean paramBoolean)
  {
    this.p = paramLong;
    this.q = paramFloat;
    if (paramAMapLocationListener == null) {
      return;
    }
    paramAMapLocationListener = new g(paramLong, paramFloat, paramAMapLocationListener, paramString, paramBoolean);
    this.j.add(paramAMapLocationListener);
    if (!"gps".equals(paramString))
    {
      if ("lbs".equals(paramString)) {}
    }
    else
    {
      this.a.a(paramLong, paramFloat);
      return;
    }
    if (!this.f) {}
    for (;;)
    {
      this.b.a(paramLong);
      c(true);
      if (this.o != null) {
        break;
      }
      this.b.b(true);
      this.o = new Thread(this.b);
      this.o.start();
      return;
      this.a.a(paramLong, paramFloat);
    }
  }
  
  void a(PendingIntent paramPendingIntent)
  {
    this.b.a(paramPendingIntent);
  }
  
  void a(AMapLocationListener paramAMapLocationListener)
  {
    int i1;
    int i2;
    if (this.j == null)
    {
      i1 = 0;
      i2 = 0;
      if (i2 < i1) {
        break label56;
      }
      if (this.j != null) {
        break label142;
      }
    }
    label56:
    label82:
    label127:
    label142:
    while (this.j.size() == 0)
    {
      b(false);
      c(false);
      b();
      if (this.a != null) {
        break label153;
      }
      return;
      i1 = this.j.size();
      break;
      g localg = (g)this.j.get(i2);
      if (localg != null)
      {
        if (localg.b != null) {
          break label127;
        }
        this.j.remove(localg);
        i2 -= 1;
        i1 -= 1;
      }
      for (;;)
      {
        i2 += 1;
        break;
        this.j.remove(i2);
        i2 -= 1;
        i1 -= 1;
        continue;
        if (paramAMapLocationListener.equals(localg.b)) {
          break label82;
        }
      }
    }
    return;
    label153:
    this.a.b();
  }
  
  void a(boolean paramBoolean)
  {
    d(paramBoolean);
    if (this.j == null) {}
    while (this.j.size() <= 0) {
      return;
    }
    if (!paramBoolean)
    {
      this.a.b();
      return;
    }
    this.a.b();
    this.a.a(this.p, this.q);
  }
  
  void b()
  {
    if (this.b == null) {}
    while (this.o == null)
    {
      return;
      this.b.b(false);
    }
    this.o.interrupt();
    this.o = null;
  }
  
  void b(double paramDouble1, double paramDouble2, float paramFloat, long paramLong, PendingIntent paramPendingIntent)
  {
    j localj = new j();
    localj.b = paramDouble1;
    localj.a = paramDouble2;
    localj.c = paramFloat;
    localj.a(paramLong);
    this.b.b(localj, paramPendingIntent);
  }
  
  void b(PendingIntent paramPendingIntent)
  {
    this.b.b(paramPendingIntent);
  }
  
  void b(boolean paramBoolean)
  {
    this.c = paramBoolean;
  }
  
  void d()
  {
    if (this.a == null)
    {
      if (this.b != null) {
        break label49;
      }
      label14:
      if (this.j != null) {
        break label59;
      }
    }
    for (;;)
    {
      b(false);
      return;
      this.a.b();
      this.a.a();
      this.a = null;
      break;
      label49:
      this.b.b();
      break label14;
      label59:
      this.j.clear();
    }
  }
  
  class a
    extends Handler
  {
    public a() {}
    
    public a(Looper paramLooper)
    {
      super();
      Looper.prepare();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage != null)
      {
        for (;;)
        {
          try
          {
            if (paramMessage.what != 100) {
              return;
            }
            localObject = a.a(a.this);
            if (localObject == null) {
              break label355;
            }
          }
          catch (Throwable paramMessage)
          {
            Object localObject;
            return;
          }
          try
          {
            a.a(a.this, (AMapLocation)paramMessage.obj);
            localObject = a.b(a.this);
            if (localObject != null) {
              break label113;
            }
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
            continue;
            g localg = (g)localException.next();
            if (localg.b == null) {
              continue;
            }
            AMapLocation localAMapLocation = (AMapLocation)paramMessage.obj;
            if (!localg.c.booleanValue()) {
              break label268;
            }
            for (;;)
            {
              localg.b.onLocationChanged(localAMapLocation);
              if ((!localg.c.booleanValue()) || (localg.a != -1L) || (a.c(a.this) == null)) {
                break;
              }
              a.c(a.this).add(localg);
              break;
              if (localAMapLocation.getAMapException().getErrorCode() != 0) {
                break;
              }
            }
            if (a.c(a.this).size() <= 0) {
              continue;
            }
            int i = 0;
            for (;;)
            {
              if (i >= a.c(a.this).size())
              {
                a.c(a.this).clear();
                break;
              }
              a.this.a(((g)a.c(a.this).get(i)).b);
              i += 1;
            }
          }
          localObject = a.a(a.this).iterator();
          if (((Iterator)localObject).hasNext()) {
            break;
          }
          if (a.c(a.this) != null) {
            break label282;
          }
          if (a.b(a.this) == null) {
            break label355;
          }
          com.amap.api.location.core.d.a(a.d(a.this), a.b(a.this));
          return;
          label113:
          if ((a.b(a.this).getAdCode() != null) && (a.b(a.this).getAdCode().length() > 0)) {
            a.b(a.this, a.b(a.this));
          }
        }
        label268:
        label282:
        label355:
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */