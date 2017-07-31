package com.android.server.location;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.SecureRandom;

public class LocationFudger
{
  private static final int APPROXIMATE_METERS_PER_DEGREE_AT_EQUATOR = 111000;
  private static final long CHANGE_INTERVAL_MS = 3600000L;
  private static final double CHANGE_PER_INTERVAL = 0.03D;
  private static final String COARSE_ACCURACY_CONFIG_NAME = "locationCoarseAccuracy";
  private static final boolean D = false;
  private static final float DEFAULT_ACCURACY_IN_METERS = 2000.0F;
  public static final long FASTEST_INTERVAL_MS = 600000L;
  private static final double MAX_LATITUDE = 89.999990990991D;
  private static final float MINIMUM_ACCURACY_IN_METERS = 200.0F;
  private static final double NEW_WEIGHT = 0.03D;
  private static final double PREVIOUS_WEIGHT = Math.sqrt(0.9991D);
  private static final String TAG = "LocationFudge";
  private float mAccuracyInMeters;
  private final Context mContext;
  private double mGridSizeInMeters;
  private final Object mLock = new Object();
  private long mNextInterval;
  private double mOffsetLatitudeMeters;
  private double mOffsetLongitudeMeters;
  private final SecureRandom mRandom = new SecureRandom();
  private final ContentObserver mSettingsObserver;
  private double mStandardDeviationInMeters;
  
  public LocationFudger(Context arg1, Handler paramHandler)
  {
    this.mContext = ???;
    this.mSettingsObserver = new ContentObserver(paramHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        LocationFudger.-wrap1(LocationFudger.this, LocationFudger.-wrap0(LocationFudger.this));
      }
    };
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("locationCoarseAccuracy"), false, this.mSettingsObserver);
    float f = loadCoarseAccuracy();
    synchronized (this.mLock)
    {
      setAccuracyInMetersLocked(f);
      this.mOffsetLatitudeMeters = nextOffsetLocked();
      this.mOffsetLongitudeMeters = nextOffsetLocked();
      this.mNextInterval = (SystemClock.elapsedRealtime() + 3600000L);
      return;
    }
  }
  
  private Location addCoarseLocationExtraLocked(Location paramLocation)
  {
    Location localLocation = createCoarseLocked(paramLocation);
    paramLocation.setExtraLocation("coarseLocation", localLocation);
    return localLocation;
  }
  
  private Location createCoarseLocked(Location paramLocation)
  {
    paramLocation = new Location(paramLocation);
    paramLocation.removeBearing();
    paramLocation.removeSpeed();
    paramLocation.removeAltitude();
    paramLocation.setExtras(null);
    double d1 = paramLocation.getLatitude();
    double d2 = paramLocation.getLongitude();
    d1 = wrapLatitude(d1);
    d2 = wrapLongitude(d2);
    updateRandomOffsetLocked();
    double d3 = metersToDegreesLongitude(this.mOffsetLongitudeMeters, d1);
    d1 = wrapLatitude(d1 + metersToDegreesLatitude(this.mOffsetLatitudeMeters));
    d2 = wrapLongitude(d2 + d3);
    d3 = metersToDegreesLatitude(this.mGridSizeInMeters);
    d3 = Math.round(d1 / d3) * d3;
    d1 = metersToDegreesLongitude(this.mGridSizeInMeters, d3);
    d2 = Math.round(d2 / d1);
    d3 = wrapLatitude(d3);
    d1 = wrapLongitude(d2 * d1);
    paramLocation.setLatitude(d3);
    paramLocation.setLongitude(d1);
    paramLocation.setAccuracy(Math.max(this.mAccuracyInMeters, paramLocation.getAccuracy()));
    return paramLocation;
  }
  
  private float loadCoarseAccuracy()
  {
    String str = Settings.Secure.getString(this.mContext.getContentResolver(), "locationCoarseAccuracy");
    if (str == null) {
      return 2000.0F;
    }
    try
    {
      float f = Float.parseFloat(str);
      return f;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 2000.0F;
  }
  
  private static double metersToDegreesLatitude(double paramDouble)
  {
    return paramDouble / 111000.0D;
  }
  
  private static double metersToDegreesLongitude(double paramDouble1, double paramDouble2)
  {
    return paramDouble1 / 111000.0D / Math.cos(Math.toRadians(paramDouble2));
  }
  
  private double nextOffsetLocked()
  {
    return this.mRandom.nextGaussian() * this.mStandardDeviationInMeters;
  }
  
  private void setAccuracyInMeters(float paramFloat)
  {
    synchronized (this.mLock)
    {
      setAccuracyInMetersLocked(paramFloat);
      return;
    }
  }
  
  private void setAccuracyInMetersLocked(float paramFloat)
  {
    this.mAccuracyInMeters = Math.max(paramFloat, 200.0F);
    this.mGridSizeInMeters = this.mAccuracyInMeters;
    this.mStandardDeviationInMeters = (this.mGridSizeInMeters / 4.0D);
  }
  
  private void updateRandomOffsetLocked()
  {
    long l = SystemClock.elapsedRealtime();
    if (l < this.mNextInterval) {
      return;
    }
    this.mNextInterval = (3600000L + l);
    this.mOffsetLatitudeMeters *= PREVIOUS_WEIGHT;
    this.mOffsetLatitudeMeters += nextOffsetLocked() * 0.03D;
    this.mOffsetLongitudeMeters *= PREVIOUS_WEIGHT;
    this.mOffsetLongitudeMeters += nextOffsetLocked() * 0.03D;
  }
  
  private static double wrapLatitude(double paramDouble)
  {
    double d = paramDouble;
    if (paramDouble > 89.999990990991D) {
      d = 89.999990990991D;
    }
    paramDouble = d;
    if (d < -89.999990990991D) {
      paramDouble = -89.999990990991D;
    }
    return paramDouble;
  }
  
  private static double wrapLongitude(double paramDouble)
  {
    double d = paramDouble % 360.0D;
    paramDouble = d;
    if (d >= 180.0D) {
      paramDouble = d - 360.0D;
    }
    d = paramDouble;
    if (paramDouble < -180.0D) {
      d = paramDouble + 360.0D;
    }
    return d;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println(String.format("offset: %.0f, %.0f (meters)", new Object[] { Double.valueOf(this.mOffsetLongitudeMeters), Double.valueOf(this.mOffsetLatitudeMeters) }));
  }
  
  public Location getOrCreate(Location paramLocation)
  {
    synchronized (this.mLock)
    {
      Location localLocation = paramLocation.getExtraLocation("coarseLocation");
      if (localLocation == null)
      {
        paramLocation = addCoarseLocationExtraLocked(paramLocation);
        return paramLocation;
      }
      if (localLocation.getAccuracy() < this.mAccuracyInMeters)
      {
        paramLocation = addCoarseLocationExtraLocked(paramLocation);
        return paramLocation;
      }
      return localLocation;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationFudger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */