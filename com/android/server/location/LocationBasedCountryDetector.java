package com.android.server.location;

import android.content.Context;
import android.location.Address;
import android.location.Country;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.util.Slog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationBasedCountryDetector
  extends CountryDetectorBase
{
  private static final long QUERY_LOCATION_TIMEOUT = 300000L;
  private static final String TAG = "LocationBasedCountryDetector";
  private List<String> mEnabledProviders;
  protected List<LocationListener> mLocationListeners;
  private LocationManager mLocationManager;
  protected Thread mQueryThread;
  protected Timer mTimer;
  
  public LocationBasedCountryDetector(Context paramContext)
  {
    super(paramContext);
    this.mLocationManager = ((LocationManager)paramContext.getSystemService("location"));
  }
  
  private void queryCountryCode(final Location paramLocation)
  {
    if (paramLocation == null) {}
    try
    {
      notifyListener(null);
      return;
    }
    finally {}
    Thread localThread = this.mQueryThread;
    if (localThread != null) {
      return;
    }
    this.mQueryThread = new Thread(new Runnable()
    {
      public void run()
      {
        String str = null;
        if (paramLocation != null) {
          str = LocationBasedCountryDetector.this.getCountryFromLocation(paramLocation);
        }
        if (str != null) {}
        for (LocationBasedCountryDetector.this.mDetectedCountry = new Country(str, 1);; LocationBasedCountryDetector.this.mDetectedCountry = null)
        {
          LocationBasedCountryDetector.this.notifyListener(LocationBasedCountryDetector.this.mDetectedCountry);
          LocationBasedCountryDetector.this.mQueryThread = null;
          return;
        }
      }
    });
    this.mQueryThread.start();
  }
  
  public Country detectCountry()
  {
    try
    {
      if (this.mLocationListeners != null) {
        throw new IllegalStateException();
      }
    }
    finally {}
    Object localObject2 = getEnabledProviders();
    int j = ((List)localObject2).size();
    int i;
    if (j > 0)
    {
      this.mLocationListeners = new ArrayList(j);
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        String str = (String)((List)localObject2).get(i);
        if (isAcceptableProvider(str))
        {
          LocationListener local1 = new LocationListener()
          {
            public void onLocationChanged(Location paramAnonymousLocation)
            {
              if (paramAnonymousLocation != null)
              {
                LocationBasedCountryDetector.this.stop();
                LocationBasedCountryDetector.-wrap0(LocationBasedCountryDetector.this, paramAnonymousLocation);
              }
            }
            
            public void onProviderDisabled(String paramAnonymousString) {}
            
            public void onProviderEnabled(String paramAnonymousString) {}
            
            public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
          };
          this.mLocationListeners.add(local1);
          registerListener(str, local1);
        }
      }
      else
      {
        this.mTimer = new Timer();
        this.mTimer.schedule(new TimerTask()
        {
          public void run()
          {
            LocationBasedCountryDetector.this.mTimer = null;
            LocationBasedCountryDetector.this.stop();
            LocationBasedCountryDetector.-wrap0(LocationBasedCountryDetector.this, LocationBasedCountryDetector.this.getLastKnownLocation());
          }
        }, getQueryLocationTimeout());
        for (;;)
        {
          localObject2 = this.mDetectedCountry;
          return (Country)localObject2;
          queryCountryCode(getLastKnownLocation());
        }
      }
      i += 1;
    }
  }
  
  protected String getCountryFromLocation(Location paramLocation)
  {
    Object localObject1 = null;
    Object localObject2 = new Geocoder(this.mContext);
    try
    {
      localObject2 = ((Geocoder)localObject2).getFromLocation(paramLocation.getLatitude(), paramLocation.getLongitude(), 1);
      paramLocation = (Location)localObject1;
      if (localObject2 != null)
      {
        paramLocation = (Location)localObject1;
        if (((List)localObject2).size() > 0) {
          paramLocation = ((Address)((List)localObject2).get(0)).getCountryCode();
        }
      }
      return paramLocation;
    }
    catch (IOException paramLocation)
    {
      Slog.w("LocationBasedCountryDetector", "Exception occurs when getting country from location");
    }
    return null;
  }
  
  protected List<String> getEnabledProviders()
  {
    if (this.mEnabledProviders == null) {
      this.mEnabledProviders = this.mLocationManager.getProviders(true);
    }
    return this.mEnabledProviders;
  }
  
  protected Location getLastKnownLocation()
  {
    long l1 = Binder.clearCallingIdentity();
    try
    {
      Object localObject3 = this.mLocationManager.getAllProviders();
      Object localObject1 = null;
      Iterator localIterator = ((Iterable)localObject3).iterator();
      while (localIterator.hasNext())
      {
        localObject3 = (String)localIterator.next();
        localObject3 = this.mLocationManager.getLastKnownLocation((String)localObject3);
        if (localObject3 != null) {
          if (localObject1 != null)
          {
            long l2 = ((Location)localObject1).getElapsedRealtimeNanos();
            long l3 = ((Location)localObject3).getElapsedRealtimeNanos();
            if (l2 >= l3) {}
          }
          else
          {
            localObject1 = localObject3;
          }
        }
      }
      return (Location)localObject1;
    }
    finally
    {
      Binder.restoreCallingIdentity(l1);
    }
  }
  
  protected long getQueryLocationTimeout()
  {
    return 300000L;
  }
  
  protected boolean isAcceptableProvider(String paramString)
  {
    return "passive".equals(paramString);
  }
  
  protected void registerListener(String paramString, LocationListener paramLocationListener)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mLocationManager.requestLocationUpdates(paramString, 0L, 0.0F, paramLocationListener);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void stop()
  {
    try
    {
      if (this.mLocationListeners != null)
      {
        Iterator localIterator = this.mLocationListeners.iterator();
        while (localIterator.hasNext()) {
          unregisterListener((LocationListener)localIterator.next());
        }
        this.mLocationListeners = null;
      }
    }
    finally {}
    if (this.mTimer != null)
    {
      this.mTimer.cancel();
      this.mTimer = null;
    }
  }
  
  protected void unregisterListener(LocationListener paramLocationListener)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mLocationManager.removeUpdates(paramLocationListener);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationBasedCountryDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */