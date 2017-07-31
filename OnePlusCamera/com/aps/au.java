package com.aps;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;
import android.location.LocationManager;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class au
  implements GpsStatus.Listener, GpsStatus.NmeaListener
{
  private long a = 0L;
  private long b = 0L;
  private boolean c = false;
  private List d = new ArrayList();
  private String e = null;
  private String f = null;
  private String g = null;
  
  protected au(y paramy) {}
  
  public final void a(String paramString)
  {
    int i;
    if (System.currentTimeMillis() - this.b <= 400L)
    {
      i = 1;
      if ((i == 0) && (this.c)) {
        break label68;
      }
      label28:
      if (paramString.startsWith("$GPGGA")) {
        break label251;
      }
      if (paramString.startsWith("$GPGSV")) {
        break label267;
      }
      if (paramString.startsWith("$GPGSA")) {
        break label284;
      }
    }
    for (;;)
    {
      this.b = System.currentTimeMillis();
      return;
      i = 0;
      break;
      label68:
      if (this.d.size() <= 0) {
        break label28;
      }
      try
      {
        w localw = new w(this.d, this.e, null, this.g);
        if (!localw.a()) {
          y.e(this.h, 0);
        }
        for (;;)
        {
          this.d.clear();
          this.g = null;
          this.f = null;
          this.e = null;
          this.c = false;
          break;
          y.e(this.h, y.a(this.h, localw, y.o(this.h)));
          if (y.p(this.h) > 0) {
            y.b(this.h, String.format(Locale.CHINA, "&nmea=%.1f|%.1f&g_tp=%d", new Object[] { Double.valueOf(localw.c()), Double.valueOf(localw.b()), Integer.valueOf(y.p(this.h)) }));
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          y.e(this.h, 0);
        }
      }
      label251:
      this.c = true;
      this.e = paramString.trim();
      continue;
      label267:
      this.d.add(paramString.trim());
      continue;
      label284:
      this.g = paramString.trim();
    }
  }
  
  public final void onGpsStatusChanged(int paramInt)
  {
    int i = 0;
    label456:
    label464:
    label472:
    label473:
    do
    {
      for (;;)
      {
        int j;
        try
        {
          if (y.e(this.h) != null) {
            switch (paramInt)
            {
            case 4: 
              Iterator localIterator;
              if (y.a)
              {
                if (y.i(this.h) != null)
                {
                  y.e(this.h).getGpsStatus(y.i(this.h));
                  localIterator = y.i(this.h).getSatellites().iterator();
                  y.a(this.h, 0);
                  y.b(this.h, 0);
                  y.a(this.h, new HashMap());
                  paramInt = 0;
                  j = 0;
                  if (localIterator.hasNext()) {
                    continue;
                  }
                  if (y.k(this.h) != -1) {
                    break label464;
                  }
                  y.c(this.h, j);
                  if (j < 4) {
                    continue;
                  }
                  if (y.l(this.h) != null) {
                    continue;
                  }
                  continue;
                  y.d(this.h, i);
                  y.b(this.h, y.m(this.h));
                  if (y.a) {
                    break label472;
                  }
                  if (j <= 3) {
                    break label473;
                  }
                  if (y.e(this.h).getLastKnownLocation("gps") != null) {
                    continue;
                  }
                }
              }
              else
              {
                if (System.currentTimeMillis() - this.a < 10000L) {
                  continue;
                }
                paramInt = 1;
                continue;
              }
              y.a(this.h, y.e(this.h).getGpsStatus(null));
              continue;
              GpsSatellite localGpsSatellite = (GpsSatellite)localIterator.next();
              int n = paramInt + 1;
              if (localGpsSatellite.usedInFix()) {
                break label456;
              }
              k = j;
              int m = i;
              if (localGpsSatellite.getSnr() > 0.0F) {
                m = i + 1;
              }
              i = m;
              j = k;
              paramInt = n;
              if (localGpsSatellite.getSnr() < y.m()) {
                continue;
              }
              y.j(this.h);
              i = m;
              j = k;
              paramInt = n;
              continue;
              if (j >= 4) {
                continue;
              }
              if (y.k(this.h) >= 4) {
                continue;
              }
              continue;
              if (y.k(this.h) >= 4) {
                continue;
              }
              continue;
              if (y.l(this.h) == null) {
                continue;
              }
              y.l(this.h).w();
              continue;
              y.l(this.h).v();
              continue;
              this.a = System.currentTimeMillis();
              return;
            case 2: 
              y.d(this.h, 0);
              return;
            case 3: 
            default: 
              return;
            }
          } else {
            return;
          }
          if (paramInt == 0)
          {
            return;
            paramInt = 0;
            continue;
          }
          continue;
          int k = j + 1;
        }
        catch (Exception localException)
        {
          return;
        }
        continue;
        if (j >= 4) {}
      }
      return;
    } while (paramInt > 15);
  }
  
  public final void onNmeaReceived(long paramLong, String paramString)
  {
    try
    {
      if (!y.a) {
        break label67;
      }
      if (paramString == null) {
        return;
      }
      if (!paramString.equals(""))
      {
        if (paramString.length() < 9) {
          return;
        }
        if (paramString.length() > 150) {
          return;
        }
        y.n(this.h).sendMessage(y.n(this.h).obtainMessage(1, paramString));
        return;
      }
    }
    catch (Exception paramString)
    {
      return;
    }
    return;
    label67:
    return;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/au.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */