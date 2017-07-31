package com.aps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class y
{
  private static float P = 1.1F;
  private static float Q = 2.2F;
  private static float R = 2.3F;
  private static float S = 3.8F;
  private static int T = 3;
  private static int U = 10;
  private static int V = 2;
  private static int W = 7;
  private static int X = 20;
  private static int Y = 70;
  private static int Z = 120;
  protected static boolean a = false;
  protected static boolean b = true;
  private static int c = 10;
  private static int d = 2;
  private static int e = 10;
  private static int f = 10;
  private static int g = 50;
  private static int h = 200;
  private static Object i = new Object();
  private static y j;
  private Thread A = null;
  private Looper B = null;
  private av C = null;
  private Location D = null;
  private au E = null;
  private Handler F = null;
  private aw G = new aw(this);
  private LocationListener H = new aq(this);
  private BroadcastReceiver I = new ar(this);
  private GpsStatus J = null;
  private int K = 0;
  private int L = 0;
  private HashMap M = null;
  private int N = 0;
  private int O = 0;
  private boolean k = false;
  private boolean l = false;
  private int m = -1;
  private int n = 0;
  private int o = 0;
  private int p = 10000;
  private long q = 0L;
  private Context r;
  private LocationManager s;
  private ak t;
  private ay u;
  private bf v;
  private ah w;
  private be x;
  private ax y;
  private ab z;
  
  private y(Context paramContext)
  {
    this.r = paramContext;
    this.t = ak.a(paramContext);
    this.z = new ab();
    this.u = new ay(this.t);
    this.w = new ah(paramContext);
    this.v = new bf(this.w);
    this.x = new be(this.w);
    this.s = ((LocationManager)this.r.getSystemService("location"));
    this.y = ax.a(this.r);
    this.y.a(this.G);
    n();
    List localList = this.s.getAllProviders();
    if (localList == null) {}
    for (boolean bool = false;; bool = true)
    {
      this.l = bool;
      bg.a(paramContext);
      return;
      if ((!localList.contains("gps")) || (!localList.contains("passive"))) {
        break;
      }
    }
  }
  
  private int a(HashMap paramHashMap)
  {
    if (this.K <= 4) {}
    Object localObject1;
    label76:
    label418:
    label437:
    label452:
    do
    {
      ArrayList localArrayList;
      do
      {
        return 3;
        localObject1 = new ArrayList();
        localArrayList = new ArrayList();
        i1 = 0;
        paramHashMap = paramHashMap.entrySet().iterator();
        if (paramHashMap.hasNext()) {
          break;
        }
      } while (((List)localObject1).isEmpty());
      paramHashMap = new double[2];
      int i2 = ((List)localObject1).size();
      int i1 = 0;
      double d2;
      double d3;
      double d1;
      if (i1 >= i2)
      {
        paramHashMap[0] /= i2;
        paramHashMap[1] /= i2;
        d2 = paramHashMap[0];
        d3 = paramHashMap[1];
        if (d3 != 0.0D) {
          break label437;
        }
        if (d2 <= 0.0D) {
          break label418;
        }
        d1 = 90.0D;
      }
      for (;;)
      {
        localObject1 = new double[2];
        localObject1[0] = Math.sqrt(d2 * d2 + d3 * d3);
        localObject1[1] = d1;
        String.format(Locale.CHINA, "%d,%d,%d,%d", new Object[] { Long.valueOf(Math.round(paramHashMap[0] * 100.0D)), Long.valueOf(Math.round(paramHashMap[1] * 100.0D)), Long.valueOf(Math.round(localObject1[0] * 100.0D)), Long.valueOf(Math.round(localObject1[1] * 100.0D)) });
        if (localObject1[0] > Y) {
          break label452;
        }
        return 1;
        Object localObject2 = (List)((Map.Entry)paramHashMap.next()).getValue();
        if (localObject2 == null) {
          break;
        }
        localObject2 = a((List)localObject2);
        if (localObject2 == null) {
          break;
        }
        ((List)localObject1).add(localObject2);
        i2 = i1 + 1;
        localArrayList.add(Integer.valueOf(i1));
        i1 = i2;
        break;
        localObject2 = (double[])((List)localObject1).get(i1);
        int i3 = ((Integer)localArrayList.get(i1)).intValue();
        localObject2[0] *= i3;
        localObject2[1] *= i3;
        paramHashMap[0] += localObject2[0];
        paramHashMap[1] += localObject2[1];
        i1 += 1;
        break label76;
        if (d2 < 0.0D)
        {
          d1 = 270.0D;
        }
        else
        {
          d1 = 0.0D;
          continue;
          d1 = Math.toDegrees(Math.atan(d2 / d3));
        }
      }
    } while (localObject1[0] < Z);
    return 4;
  }
  
  public static y a(Context paramContext)
  {
    if (j != null) {
      return j;
    }
    for (;;)
    {
      synchronized (i)
      {
        if (j == null) {}
      }
      j = new y(paramContext);
    }
  }
  
  public static String a(String paramString)
  {
    if (!paramString.equals("version")) {
      return null;
    }
    return "COL.14.1126r";
  }
  
  private double[] a(List paramList)
  {
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return null;
    }
    double[] arrayOfDouble = new double[2];
    Iterator localIterator = paramList.iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        int i1 = paramList.size();
        arrayOfDouble[0] /= i1;
        arrayOfDouble[1] /= i1;
        return arrayOfDouble;
      }
      Object localObject = (GpsSatellite)localIterator.next();
      if (localObject != null)
      {
        double d1 = 90.0F - ((GpsSatellite)localObject).getElevation();
        double d2 = ((GpsSatellite)localObject).getAzimuth();
        localObject = new double[2];
        localObject[0] = (Math.sin(Math.toRadians(d2)) * d1);
        localObject[1] = (d1 * Math.cos(Math.toRadians(d2)));
        arrayOfDouble[0] += localObject[0];
        arrayOfDouble[1] += localObject[1];
      }
    }
  }
  
  private void n()
  {
    this.n = (this.y.b() * 1000);
    this.o = this.y.c();
    ay localay = this.u;
    int i1 = this.n;
    i1 = this.o;
    ay.a();
  }
  
  public void a()
  {
    bd.a = true;
    if (!this.l) {}
    while (this.t == null) {
      return;
    }
    if (!a)
    {
      IntentFilter localIntentFilter = new IntentFilter("android.location.GPS_ENABLED_CHANGE");
      localIntentFilter.addAction("android.location.GPS_FIX_CHANGE");
      b = true;
      this.r.registerReceiver(this.I, localIntentFilter);
      this.s.removeUpdates(this.H);
      if (this.B != null) {
        break label120;
      }
      if (this.A != null) {
        break label135;
      }
    }
    for (;;)
    {
      this.A = new as(this, "");
      this.A.start();
      this.t.a();
      a = true;
      return;
      return;
      label120:
      this.B.quit();
      this.B = null;
      break;
      label135:
      this.A.interrupt();
      this.A = null;
    }
  }
  
  public void a(int paramInt)
  {
    if (paramInt == 256) {}
    while ((paramInt == 8736) || (paramInt == 768))
    {
      this.w.a(paramInt);
      return;
    }
    throw new RuntimeException("invalid Size! must be COLLECTOR_SMALL_SIZE or COLLECTOR_BIG_SIZE or COLLECTOR_MEDIUM_SIZE");
  }
  
  public void a(ag paramag, String paramString)
  {
    boolean bool = this.y.a(paramString);
    if (paramag == null) {
      return;
    }
    paramString = paramag.a();
    if (!bool) {}
    for (;;)
    {
      paramag.a(bool);
      this.x.a(paramag);
      return;
      if (paramString != null)
      {
        Object localObject = ((ConnectivityManager)this.r.getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localObject != null) && (((NetworkInfo)localObject).isConnected()))
        {
          int i1;
          if (((NetworkInfo)localObject).getType() != 1)
          {
            localObject = this.y;
            i1 = this.y.f();
            ((ax)localObject).b(paramString.length + i1);
          }
          else
          {
            localObject = this.y;
            i1 = this.y.e();
            ((ax)localObject).a(paramString.length + i1);
          }
        }
      }
    }
  }
  
  public void b()
  {
    bd.a = false;
    if (!this.l) {}
    while (this.t == null) {
      return;
    }
    if (a)
    {
      if (this.I != null) {
        break label111;
      }
      if (this.t != null) {
        break label129;
      }
      label39:
      this.s.removeGpsStatusListener(this.E);
      this.s.removeNmeaListener(this.E);
      this.E = null;
      this.s.removeUpdates(this.H);
      if (this.B != null) {
        break label139;
      }
      label84:
      if (this.A != null) {
        break label154;
      }
      label91:
      if (this.C != null) {
        break label169;
      }
    }
    for (;;)
    {
      this.t.b();
      a = false;
      return;
      return;
      try
      {
        label111:
        this.r.unregisterReceiver(this.I);
      }
      catch (Exception localException) {}
      break;
      label129:
      this.t.w();
      break label39;
      label139:
      this.B.quit();
      this.B = null;
      break label84;
      label154:
      this.A.interrupt();
      this.A = null;
      break label91;
      label169:
      this.k = false;
      this.C.interrupt();
      this.C = null;
    }
  }
  
  public void c()
  {
    if (this.l)
    {
      b();
      return;
    }
  }
  
  public ag d()
  {
    if (this.x != null)
    {
      e();
      if (this.y.a()) {
        return this.x.a(this.y.d());
      }
    }
    else
    {
      return null;
    }
    return null;
  }
  
  public boolean e()
  {
    if (this.t == null) {}
    List localList;
    do
    {
      return false;
      localList = this.t.n();
    } while ((localList == null) || (localList.size() <= 0));
    return this.w.b(((Long)localList.get(0)).longValue());
  }
  
  public int f()
  {
    if (this.x == null) {
      return 0;
    }
    return this.x.a();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/y.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */