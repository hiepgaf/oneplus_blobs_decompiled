package com.amap.api.location;

import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;

public class LocationProviderProxy
{
  public static final String AMapNetwork = "lbs";
  public static final int AVAILABLE = 2;
  public static final int OUT_OF_SERVICE = 0;
  public static final int TEMPORARILY_UNAVAILABLE = 1;
  private LocationManager a;
  private String b;
  
  protected LocationProviderProxy(LocationManager paramLocationManager, String paramString)
  {
    this.a = paramLocationManager;
    this.b = paramString;
  }
  
  private LocationProvider a()
  {
    try
    {
      if (this.a == null) {
        return null;
      }
      LocationProvider localLocationProvider = this.a.getProvider(this.b);
      return localLocationProvider;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  static LocationProviderProxy a(LocationManager paramLocationManager, String paramString)
  {
    return new LocationProviderProxy(paramLocationManager, paramString);
  }
  
  public int getAccuracy()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        break label44;
        if ("lbs".equals(this.b)) {
          return 2;
        }
      }
      int i = a().getAccuracy();
      return i;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    label44:
    return -1;
  }
  
  public String getName()
  {
    if ("lbs" == null) {}
    for (;;)
    {
      try
      {
        localObject = a();
        if (localObject != null) {
          continue;
        }
      }
      catch (Throwable localThrowable)
      {
        Object localObject;
        localThrowable.printStackTrace();
        continue;
      }
      return "null";
      if ("lbs".equals(this.b)) {
        return "lbs";
      }
    }
    localObject = a().getName();
    return (String)localObject;
  }
  
  public int getPowerRequirement()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        break label44;
        if ("lbs".equals(this.b)) {
          return 2;
        }
      }
      int i = a().getPowerRequirement();
      return i;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    label44:
    return -1;
  }
  
  public boolean hasMonetaryCost()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return false;
        if ("lbs".equals(this.b)) {
          return false;
        }
      }
      boolean bool = a().hasMonetaryCost();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return false;
  }
  
  public boolean meetsCriteria(Criteria paramCriteria)
  {
    boolean bool = true;
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return false;
        if ("lbs".equals(this.b))
        {
          if (paramCriteria == null) {
            break label89;
          }
          if ((paramCriteria.isAltitudeRequired()) || (paramCriteria.isBearingRequired()) || (paramCriteria.isSpeedRequired())) {
            break label85;
          }
          if (paramCriteria.getAccuracy() != 1) {
            break label87;
          }
          break label85;
        }
      }
      bool = a().meetsCriteria(paramCriteria);
      return bool;
    }
    catch (Throwable paramCriteria)
    {
      paramCriteria.printStackTrace();
      return false;
    }
    label85:
    bool = false;
    label87:
    return bool;
    label89:
    return true;
  }
  
  public boolean requiresCell()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return true;
        if ("lbs".equals(this.b)) {
          return true;
        }
      }
      boolean bool = a().requiresCell();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return true;
  }
  
  public boolean requiresNetwork()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return true;
        if ("lbs".equals(this.b)) {
          return true;
        }
      }
      boolean bool = a().requiresNetwork();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return true;
  }
  
  public boolean requiresSatellite()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        break label44;
        if ("lbs".equals(this.b)) {
          return false;
        }
      }
      boolean bool = a().requiresNetwork();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    label44:
    return true;
  }
  
  public boolean supportsAltitude()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return false;
        if ("lbs".equals(this.b)) {
          return false;
        }
      }
      boolean bool = a().supportsAltitude();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return false;
  }
  
  public boolean supportsBearing()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return false;
        if ("lbs".equals(this.b)) {
          return false;
        }
      }
      boolean bool = a().supportsBearing();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return false;
  }
  
  public boolean supportsSpeed()
  {
    if ("lbs" == null) {}
    try
    {
      while (a() == null)
      {
        return false;
        if ("lbs".equals(this.b)) {
          return false;
        }
      }
      boolean bool = a().supportsSpeed();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/LocationProviderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */