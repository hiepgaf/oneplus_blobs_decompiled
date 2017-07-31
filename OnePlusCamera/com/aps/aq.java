package com.aps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import java.text.SimpleDateFormat;

final class aq
  implements LocationListener
{
  aq(y paramy) {}
  
  private static boolean a(Location paramLocation)
  {
    if (paramLocation == null) {}
    while ((!"gps".equalsIgnoreCase(paramLocation.getProvider())) || (paramLocation.getLatitude() <= -90.0D) || (paramLocation.getLatitude() >= 90.0D) || (paramLocation.getLongitude() <= -180.0D) || (paramLocation.getLongitude() >= 180.0D)) {
      return false;
    }
    return true;
  }
  
  public final void onLocationChanged(Location paramLocation)
  {
    int i = 0;
    try
    {
      long l1 = paramLocation.getTime();
      long l2 = System.currentTimeMillis();
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      localSimpleDateFormat.format(Long.valueOf(l1));
      localSimpleDateFormat.format(Long.valueOf(l2));
      if (l1 > 0L) {
        i = 1;
      }
      for (;;)
      {
        if (a(paramLocation))
        {
          if (paramLocation.getSpeed() > y.g())
          {
            ay.a(y.h());
            ay.b(y.h() * 10);
          }
          for (;;)
          {
            y.b(this.a).a();
            a(paramLocation);
            if (y.b(this.a).a()) {
              break;
            }
            return;
            if (paramLocation.getSpeed() > y.i())
            {
              ay.a(y.j());
              ay.b(y.j() * 10);
            }
            else
            {
              ay.a(y.k());
              ay.b(y.k() * 10);
            }
          }
          if (!a(paramLocation)) {
            break;
          }
          paramLocation.setTime(System.currentTimeMillis());
          y.a(this.a, System.currentTimeMillis());
          y.a(this.a, paramLocation);
          if (y.c(this.a) == true)
          {
            y.a(this.a, "new location in indoor collect");
            return;
          }
          y.a(this.a, paramLocation, 0, l1);
          return;
          if (i != 0) {
            break label245;
          }
          l1 = l2;
        }
        label245:
        while (paramLocation == null) {
          return;
        }
      }
      return;
    }
    catch (Exception paramLocation) {}
  }
  
  public final void onProviderDisabled(String paramString) {}
  
  public final void onProviderEnabled(String paramString) {}
  
  public final void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/aq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */