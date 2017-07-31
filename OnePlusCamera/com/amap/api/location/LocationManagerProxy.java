package com.amap.api.location;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import com.amap.api.location.core.d;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class LocationManagerProxy
{
  public static final String GPS_PROVIDER = "gps";
  public static final String KEY_LOCATION_CHANGED = "location";
  public static final String KEY_PROVIDER_ENABLED = "providerEnabled";
  public static final String KEY_PROXIMITY_ENTERING = "entering";
  public static final String KEY_STATUS_CHANGED = "status";
  public static final String NETWORK_PROVIDER = "network";
  public static final int WEATHER_TYPE_FORECAST = 2;
  public static final int WEATHER_TYPE_LIVE = 1;
  static Object a = new Object();
  private static LocationManagerProxy c = null;
  private LocationManager b = null;
  private a d = null;
  private Context e;
  private f f;
  private b g;
  private ArrayList<PendingIntent> h = new ArrayList();
  private Hashtable<String, LocationProviderProxy> i = new Hashtable();
  private Vector<g> j = new Vector();
  private Vector<g> k = new Vector();
  private a l = new a();
  
  private LocationManagerProxy(Activity paramActivity)
  {
    a(paramActivity.getApplicationContext());
  }
  
  private LocationManagerProxy(Context paramContext)
  {
    a(paramContext);
  }
  
  private static void a()
  {
    c = null;
  }
  
  private void a(Context paramContext)
  {
    try
    {
      this.e = paramContext;
      this.b = ((LocationManager)paramContext.getSystemService("location"));
      this.d = a.a(paramContext.getApplicationContext(), this.b);
      return;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private void a(String paramString, long paramLong, float paramFloat, AMapLocationListener paramAMapLocationListener, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.d == null) {
          continue;
        }
      }
      catch (Throwable paramString)
      {
        Looper localLooper;
        label45:
        label88:
        paramString.printStackTrace();
        continue;
      }
      finally {}
      if (!"lbs".equals(paramString))
      {
        if ("gps".equals(paramString)) {
          break label157;
        }
        localLooper = this.e.getMainLooper();
        if (Looper.myLooper() == null) {
          break label183;
        }
        paramAMapLocationListener = new g(paramLong, paramFloat, paramAMapLocationListener, paramString, false);
        this.j.add(paramAMapLocationListener);
        this.b.requestLocationUpdates(paramString, paramLong, paramFloat, this.l, localLooper);
        return;
        this.d = a.a(this.e.getApplicationContext(), this.b);
      }
      label157:
      label183:
      while (paramString == null)
      {
        paramString = "lbs";
        break;
        if (this.d == null) {
          break label88;
        }
        this.d.a(paramLong, paramFloat, paramAMapLocationListener, "lbs", paramBoolean);
        break label88;
        if (this.d == null) {
          break label88;
        }
        this.d.a(paramLong, paramFloat, paramAMapLocationListener, "gps", paramBoolean);
        break label88;
        Looper.prepare();
        break label45;
      }
    }
  }
  
  public static LocationManagerProxy getInstance(Activity paramActivity)
  {
    try
    {
      synchronized (a)
      {
        if (c != null)
        {
          paramActivity = c;
          return paramActivity;
        }
        c = new LocationManagerProxy(paramActivity);
      }
      return null;
    }
    catch (Throwable paramActivity)
    {
      paramActivity.printStackTrace();
    }
  }
  
  /* Error */
  public static LocationManagerProxy getInstance(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 62	com/amap/api/location/LocationManagerProxy:c	Lcom/amap/api/location/LocationManagerProxy;
    //   6: ifnull +12 -> 18
    //   9: getstatic 62	com/amap/api/location/LocationManagerProxy:c	Lcom/amap/api/location/LocationManagerProxy;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 2	com/amap/api/location/LocationManagerProxy
    //   21: dup
    //   22: aload_0
    //   23: invokespecial 172	com/amap/api/location/LocationManagerProxy:<init>	(Landroid/content/Context;)V
    //   26: putstatic 62	com/amap/api/location/LocationManagerProxy:c	Lcom/amap/api/location/LocationManagerProxy;
    //   29: goto -20 -> 9
    //   32: astore_0
    //   33: aload_0
    //   34: invokevirtual 126	java/lang/Throwable:printStackTrace	()V
    //   37: ldc 2
    //   39: monitorexit
    //   40: aconst_null
    //   41: areturn
    //   42: astore_0
    //   43: ldc 2
    //   45: monitorexit
    //   46: aload_0
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	32	java/lang/Throwable
    //   9	13	32	java/lang/Throwable
    //   18	29	32	java/lang/Throwable
    //   3	9	42	finally
    //   9	13	42	finally
    //   18	29	42	finally
    //   33	37	42	finally
  }
  
  public static String getVersion()
  {
    return "V1.3.1";
  }
  
  public void addGeoFenceAlert(double paramDouble1, double paramDouble2, float paramFloat, long paramLong, PendingIntent paramPendingIntent)
  {
    try
    {
      if (this.d == null) {
        return;
      }
      this.d.b(paramDouble1, paramDouble2, paramFloat, paramLong, paramPendingIntent);
      return;
    }
    catch (Throwable paramPendingIntent)
    {
      paramPendingIntent.printStackTrace();
    }
  }
  
  public boolean addGpsStatusListener(GpsStatus.Listener paramListener)
  {
    try
    {
      if (this.b != null)
      {
        boolean bool = this.b.addGpsStatusListener(paramListener);
        return bool;
      }
    }
    catch (Throwable paramListener)
    {
      paramListener.printStackTrace();
    }
    return false;
  }
  
  public void addProximityAlert(double paramDouble1, double paramDouble2, float paramFloat, long paramLong, PendingIntent paramPendingIntent)
  {
    try
    {
      if (!this.d.f) {}
      for (;;)
      {
        this.d.a(paramDouble1, paramDouble2, paramFloat, paramLong, paramPendingIntent);
        return;
        this.b.addProximityAlert(paramDouble1, paramDouble2, paramFloat, paramLong, paramPendingIntent);
      }
      return;
    }
    catch (Throwable paramPendingIntent)
    {
      paramPendingIntent.printStackTrace();
    }
  }
  
  public void addTestProvider(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, int paramInt1, int paramInt2)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.addTestProvider(paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6, paramBoolean7, paramInt1, paramInt2);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void clearTestProviderEnabled(String paramString)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.clearTestProviderEnabled(paramString);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void clearTestProviderLocation(String paramString)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.clearTestProviderLocation(paramString);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void clearTestProviderStatus(String paramString)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.clearTestProviderStatus(paramString);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  @Deprecated
  public void destory()
  {
    try
    {
      destroy();
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public void destroy()
  {
    label37:
    label91:
    do
    {
      for (;;)
      {
        try
        {
          synchronized (a)
          {
            a.c();
            if (this.i == null)
            {
              if (this.j == null)
              {
                if (this.b != null) {
                  break;
                }
                if (this.h != null) {
                  break label155;
                }
                this.d = null;
                a();
                this.l = null;
              }
            }
            else {
              this.i.clear();
            }
          }
          this.j.clear();
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
          return;
        }
      }
      if (this.l != null) {
        break;
      }
    } while (this.h == null);
    int m = 0;
    while (m < this.h.size())
    {
      PendingIntent localPendingIntent = (PendingIntent)this.h.get(m);
      if (localPendingIntent == null)
      {
        break label165;
        this.b.removeUpdates(this.l);
        break label91;
      }
      this.b.removeUpdates(localPendingIntent);
      break label165;
      label155:
      this.h.clear();
      break label37;
      label165:
      m += 1;
    }
  }
  
  public List<String> getAllProviders()
  {
    try
    {
      Object localObject = this.b.getAllProviders();
      if (localObject == null)
      {
        localObject = new ArrayList();
        ((List)localObject).add("lbs");
        ((List)localObject).addAll(this.b.getAllProviders());
        return (List<String>)localObject;
      }
      if (!((List)localObject).contains("lbs"))
      {
        ((List)localObject).add("lbs");
        return (List<String>)localObject;
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return null;
    }
    return localThrowable;
  }
  
  public String getBestProvider(Criteria paramCriteria, boolean paramBoolean)
  {
    String str = "lbs";
    if (paramCriteria != null)
    {
      do
      {
        try
        {
          if (getProvider("lbs").meetsCriteria(paramCriteria)) {
            continue;
          }
          str = this.b.getBestProvider(paramCriteria, paramBoolean);
        }
        catch (Throwable paramCriteria)
        {
          paramCriteria.printStackTrace();
          return "gps";
        }
        if (d.a(this.e)) {
          break;
        }
        paramCriteria = this.b.getBestProvider(paramCriteria, paramBoolean);
        return paramCriteria;
      } while (paramBoolean);
      return str;
    }
    return "lbs";
  }
  
  public GpsStatus getGpsStatus(GpsStatus paramGpsStatus)
  {
    try
    {
      if (this.b == null) {
        return null;
      }
      paramGpsStatus = this.b.getGpsStatus(paramGpsStatus);
      return paramGpsStatus;
    }
    catch (Throwable paramGpsStatus)
    {
      paramGpsStatus.printStackTrace();
    }
    return null;
  }
  
  public AMapLocation getLastKnownLocation(String paramString)
  {
    try
    {
      if (this.d != null)
      {
        if (!"lbs".equals(paramString))
        {
          if (this.b == null) {
            return null;
          }
        }
        else {
          return this.d.a();
        }
        paramString = this.b.getLastKnownLocation(paramString);
        if (paramString == null) {
          return null;
        }
        paramString = new AMapLocation(paramString);
        return paramString;
      }
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
      return null;
    }
    return null;
  }
  
  public LocationProviderProxy getProvider(String paramString)
  {
    if (paramString != null) {}
    try
    {
      if (this.i.containsKey(paramString)) {
        break label54;
      }
      LocationProviderProxy localLocationProviderProxy = LocationProviderProxy.a(this.b, paramString);
      this.i.put(paramString, localLocationProviderProxy);
      return localLocationProviderProxy;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
      return null;
    }
    throw new IllegalArgumentException("name不能为空！");
    label54:
    paramString = (LocationProviderProxy)this.i.get(paramString);
    return paramString;
  }
  
  public List<String> getProviders(Criteria paramCriteria, boolean paramBoolean)
  {
    try
    {
      Object localObject = this.b.getProviders(paramCriteria, paramBoolean);
      if (localObject == null) {
        localObject = new ArrayList();
      }
      for (;;)
      {
        if ("lbs".equals(getBestProvider(paramCriteria, paramBoolean))) {
          break label50;
        }
        return (List<String>)localObject;
        if (((List)localObject).size() == 0) {
          break;
        }
      }
      label50:
      ((List)localObject).add("lbs");
      return (List<String>)localObject;
    }
    catch (Throwable paramCriteria)
    {
      paramCriteria.printStackTrace();
    }
    return null;
  }
  
  public List<String> getProviders(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        localObject = this.b.getProviders(paramBoolean);
        if (isProviderEnabled("lbs")) {
          break label60;
        }
        return (List<String>)localObject;
      }
      catch (Throwable localThrowable)
      {
        Object localObject;
        label28:
        localThrowable.printStackTrace();
        return null;
      }
      localObject = new ArrayList();
      ((List)localObject).add("lbs");
      return (List<String>)localObject;
      label60:
      while (localThrowable != null)
      {
        int m = localThrowable.size();
        if (m == 0) {
          break;
        }
        break label28;
      }
    }
  }
  
  public boolean isProviderEnabled(String paramString)
  {
    try
    {
      if (!"lbs".equals(paramString)) {
        return this.b.isProviderEnabled(paramString);
      }
      boolean bool = d.a(this.e);
      return bool;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
    return false;
  }
  
  public void removeGeoFenceAlert(PendingIntent paramPendingIntent)
  {
    try
    {
      if (this.d == null) {
        return;
      }
      this.d.b(paramPendingIntent);
      return;
    }
    catch (Throwable paramPendingIntent)
    {
      paramPendingIntent.printStackTrace();
    }
  }
  
  public void removeGpsStatusListener(GpsStatus.Listener paramListener)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.removeGpsStatusListener(paramListener);
      return;
    }
    catch (Throwable paramListener)
    {
      paramListener.printStackTrace();
    }
  }
  
  public void removeProximityAlert(PendingIntent paramPendingIntent)
  {
    try
    {
      if (this.d == null) {}
      while (this.d == null)
      {
        return;
        if ((this.d.f) && (this.b != null)) {
          this.b.removeProximityAlert(paramPendingIntent);
        }
      }
      this.d.a(paramPendingIntent);
    }
    catch (Throwable paramPendingIntent)
    {
      paramPendingIntent.printStackTrace();
      return;
    }
  }
  
  public void removeUpdates(PendingIntent paramPendingIntent)
  {
    try
    {
      if (this.f == null) {}
      for (;;)
      {
        this.f = null;
        this.b.removeUpdates(paramPendingIntent);
        return;
        this.h.remove(paramPendingIntent);
        if (this.h.size() == 0) {
          this.f.a();
        }
      }
      return;
    }
    catch (Throwable paramPendingIntent)
    {
      paramPendingIntent.printStackTrace();
    }
  }
  
  public void removeUpdates(AMapLocationListener paramAMapLocationListener)
  {
    int n = 0;
    if (paramAMapLocationListener == null) {}
    Object localObject;
    do
    {
      try
      {
        localObject = this.j;
        if (localObject != null) {
          continue;
        }
      }
      catch (Throwable paramAMapLocationListener)
      {
        for (;;)
        {
          paramAMapLocationListener.printStackTrace();
        }
      }
      finally {}
      return;
      if (this.d == null) {}
      for (;;)
      {
        this.b.removeUpdates(paramAMapLocationListener);
        break;
        this.d.a(paramAMapLocationListener);
      }
    } while (this.j.size() <= 0);
    int m = this.j.size();
    for (;;)
    {
      if (n >= m)
      {
        if ((this.j.size() != 0) || (this.l == null)) {
          break;
        }
        this.b.removeUpdates(this.l);
        break;
      }
      localObject = (g)this.j.get(n);
      if (paramAMapLocationListener.equals(((g)localObject).b))
      {
        this.j.remove(localObject);
        n -= 1;
        m -= 1;
      }
      n += 1;
    }
  }
  
  public void requestLocationData(String paramString, long paramLong, float paramFloat, AMapLocationListener paramAMapLocationListener)
  {
    try
    {
      a(paramString, paramLong, paramFloat, paramAMapLocationListener, true);
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void requestLocationUpdates(String paramString, long paramLong, float paramFloat, PendingIntent paramPendingIntent)
  {
    for (;;)
    {
      try
      {
        if (!"lbs".equals(paramString))
        {
          this.h.add(paramPendingIntent);
          this.b.requestLocationUpdates(paramString, paramLong, paramFloat, paramPendingIntent);
          return;
        }
        if (this.f != null)
        {
          if (this.g == null) {
            break label95;
          }
          this.f.a(this.g, paramLong, paramFloat, paramString);
          this.h.add(paramPendingIntent);
          return;
        }
      }
      catch (Throwable paramString)
      {
        paramString.printStackTrace();
        return;
      }
      this.f = new f(this);
      continue;
      label95:
      this.g = new b();
    }
  }
  
  @Deprecated
  public void requestLocationUpdates(String paramString, long paramLong, float paramFloat, AMapLocationListener paramAMapLocationListener)
  {
    try
    {
      a(paramString, paramLong, paramFloat, paramAMapLocationListener, false);
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void requestWeatherUpdates(int paramInt, AMapLocalWeatherListener paramAMapLocalWeatherListener)
  {
    try
    {
      this.d.a(paramInt, paramAMapLocalWeatherListener);
      return;
    }
    catch (Throwable paramAMapLocalWeatherListener)
    {
      paramAMapLocalWeatherListener.printStackTrace();
    }
  }
  
  public void setGpsEnable(boolean paramBoolean)
  {
    try
    {
      if (this.d == null) {
        return;
      }
      this.d.a(paramBoolean);
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public void setTestProviderEnabled(String paramString, boolean paramBoolean)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.setTestProviderEnabled(paramString, paramBoolean);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void setTestProviderLocation(String paramString, Location paramLocation)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.setTestProviderLocation(paramString, paramLocation);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void setTestProviderStatus(String paramString, int paramInt, Bundle paramBundle, long paramLong)
  {
    try
    {
      if (this.b == null) {
        return;
      }
      this.b.setTestProviderStatus(paramString, paramInt, paramBundle, paramLong);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  class a
    implements AMapLocationListener
  {
    a() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      int j = 0;
      int i;
      if (paramLocation == null) {
        i = 0;
      }
      for (;;)
      {
        try
        {
          if (LocationManagerProxy.c(LocationManagerProxy.this) != null) {
            break label293;
          }
          if (LocationManagerProxy.d(LocationManagerProxy.this) != null) {
            break label365;
          }
          return;
        }
        catch (Throwable paramLocation)
        {
          label85:
          paramLocation.printStackTrace();
          return;
        }
        paramLocation = new AMapLocation(paramLocation);
        i = 0;
        g localg;
        if (LocationManagerProxy.c(LocationManagerProxy.this) == null)
        {
          if ((LocationManagerProxy.d(LocationManagerProxy.this) == null) || (LocationManagerProxy.d(LocationManagerProxy.this).size() <= 0) || (LocationManagerProxy.c(LocationManagerProxy.this) == null)) {
            break label494;
          }
          i = j;
          if (i >= LocationManagerProxy.d(LocationManagerProxy.this).size())
          {
            LocationManagerProxy.d(LocationManagerProxy.this).clear();
            if ((LocationManagerProxy.c(LocationManagerProxy.this).size() != 0) || (LocationManagerProxy.e(LocationManagerProxy.this) == null) || (LocationManagerProxy.f(LocationManagerProxy.this) == null)) {
              break label494;
            }
            LocationManagerProxy.e(LocationManagerProxy.this).removeUpdates(LocationManagerProxy.f(LocationManagerProxy.this));
          }
        }
        else
        {
          if (i >= LocationManagerProxy.c(LocationManagerProxy.this).size()) {
            continue;
          }
          localg = (g)LocationManagerProxy.c(LocationManagerProxy.this).get(i);
          if ((localg == null) || (localg.b == null)) {
            break label495;
          }
          localg.b.onLocationChanged(paramLocation);
        }
        label293:
        label365:
        label494:
        label495:
        while (localg != null)
        {
          if ((localg.a != -1L) || (LocationManagerProxy.d(LocationManagerProxy.this) == null)) {
            break label500;
          }
          LocationManagerProxy.d(LocationManagerProxy.this).add(localg);
          break label500;
          LocationManagerProxy.c(LocationManagerProxy.this).remove(LocationManagerProxy.d(LocationManagerProxy.this).get(i));
          i += 1;
          break label85;
          if (i >= LocationManagerProxy.c(LocationManagerProxy.this).size()) {
            break;
          }
          paramLocation = (g)LocationManagerProxy.c(LocationManagerProxy.this).get(i);
          if ((paramLocation == null) || (paramLocation.a != -1L) || (LocationManagerProxy.d(LocationManagerProxy.this) == null)) {
            break label507;
          }
          LocationManagerProxy.d(LocationManagerProxy.this).add(paramLocation);
          break label507;
          if ((LocationManagerProxy.d(LocationManagerProxy.this).size() > 0) && (LocationManagerProxy.c(LocationManagerProxy.this) != null))
          {
            i = 0;
            for (;;)
            {
              if (i >= LocationManagerProxy.d(LocationManagerProxy.this).size())
              {
                LocationManagerProxy.d(LocationManagerProxy.this).clear();
                if ((LocationManagerProxy.c(LocationManagerProxy.this).size() != 0) || (LocationManagerProxy.e(LocationManagerProxy.this) == null) || (LocationManagerProxy.f(LocationManagerProxy.this) == null)) {
                  break;
                }
                LocationManagerProxy.e(LocationManagerProxy.this).removeUpdates(LocationManagerProxy.f(LocationManagerProxy.this));
                return;
              }
              LocationManagerProxy.c(LocationManagerProxy.this).remove(LocationManagerProxy.d(LocationManagerProxy.this).get(i));
              i += 1;
            }
          }
          return;
        }
        label500:
        i += 1;
        continue;
        label507:
        i += 1;
      }
    }
    
    public void onLocationChanged(AMapLocation paramAMapLocation) {}
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
  
  class b
    implements AMapLocationListener
  {
    b() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      try
      {
        if (LocationManagerProxy.a(LocationManagerProxy.this) == null) {
          return;
        }
        if (LocationManagerProxy.a(LocationManagerProxy.this).size() > 0)
        {
          Iterator localIterator = LocationManagerProxy.a(LocationManagerProxy.this).iterator();
          while (localIterator.hasNext())
          {
            PendingIntent localPendingIntent = (PendingIntent)localIterator.next();
            Intent localIntent = new Intent();
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("location", paramLocation);
            localIntent.putExtras(localBundle);
            try
            {
              localPendingIntent.send(LocationManagerProxy.b(LocationManagerProxy.this), 0, localIntent);
            }
            catch (PendingIntent.CanceledException localCanceledException)
            {
              localCanceledException.printStackTrace();
            }
          }
        }
        return;
      }
      catch (Throwable paramLocation)
      {
        paramLocation.printStackTrace();
      }
    }
    
    public void onLocationChanged(AMapLocation paramAMapLocation)
    {
      try
      {
        if (LocationManagerProxy.a(LocationManagerProxy.this) == null) {
          return;
        }
        if (LocationManagerProxy.a(LocationManagerProxy.this).size() > 0)
        {
          Iterator localIterator = LocationManagerProxy.a(LocationManagerProxy.this).iterator();
          while (localIterator.hasNext())
          {
            PendingIntent localPendingIntent = (PendingIntent)localIterator.next();
            Intent localIntent = new Intent();
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("location", paramAMapLocation);
            localIntent.putExtras(localBundle);
            try
            {
              localPendingIntent.send(LocationManagerProxy.b(LocationManagerProxy.this), 0, localIntent);
            }
            catch (PendingIntent.CanceledException localCanceledException)
            {
              localCanceledException.printStackTrace();
            }
          }
        }
        return;
      }
      catch (Throwable paramAMapLocation)
      {
        paramAMapLocation.printStackTrace();
      }
    }
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/LocationManagerProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */