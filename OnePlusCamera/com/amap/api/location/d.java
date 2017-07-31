package com.amap.api.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

public class d
{
  public LocationManager a = null;
  LocationListener b = new e(this);
  private a.a c;
  private a d;
  private Context e;
  
  d(Context paramContext, LocationManager paramLocationManager, a.a parama, a parama1)
  {
    this.e = paramContext;
    this.a = paramLocationManager;
    this.d = parama1;
    this.c = parama;
  }
  
  void a() {}
  
  void a(long paramLong, float paramFloat)
  {
    try
    {
      Looper localLooper = this.e.getMainLooper();
      if (Looper.myLooper() != null) {}
      for (;;)
      {
        this.a.requestLocationUpdates("gps", paramLong, paramFloat, this.b, localLooper);
        return;
        Looper.prepare();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  void b()
  {
    if (this.b == null) {
      return;
    }
    this.a.removeUpdates(this.b);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/d.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */