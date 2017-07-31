package com.android.server.location;

import android.app.PendingIntent;
import android.location.Geofence;
import android.location.Location;

public class GeofenceState
{
  public static final int FLAG_ENTER = 1;
  public static final int FLAG_EXIT = 2;
  private static final int STATE_INSIDE = 1;
  private static final int STATE_OUTSIDE = 2;
  private static final int STATE_UNKNOWN = 0;
  public final int mAllowedResolutionLevel;
  double mDistanceToCenter = Double.MAX_VALUE;
  public final long mExpireAt;
  public final Geofence mFence;
  public final PendingIntent mIntent;
  private final Location mLocation;
  public final String mPackageName;
  int mState = 0;
  public final int mUid;
  
  public GeofenceState(Geofence paramGeofence, long paramLong, int paramInt1, int paramInt2, String paramString, PendingIntent paramPendingIntent)
  {
    this.mFence = paramGeofence;
    this.mExpireAt = paramLong;
    this.mAllowedResolutionLevel = paramInt1;
    this.mUid = paramInt2;
    this.mPackageName = paramString;
    this.mIntent = paramPendingIntent;
    this.mLocation = new Location("");
    this.mLocation.setLatitude(paramGeofence.getLatitude());
    this.mLocation.setLongitude(paramGeofence.getLongitude());
  }
  
  public double getDistanceToBoundary()
  {
    if (Double.compare(this.mDistanceToCenter, Double.MAX_VALUE) == 0) {
      return Double.MAX_VALUE;
    }
    return Math.abs(this.mFence.getRadius() - this.mDistanceToCenter);
  }
  
  public int processLocation(Location paramLocation)
  {
    this.mDistanceToCenter = this.mLocation.distanceTo(paramLocation);
    int j = this.mState;
    if (this.mDistanceToCenter <= Math.max(this.mFence.getRadius(), paramLocation.getAccuracy())) {}
    for (int i = 1; i != 0; i = 0)
    {
      this.mState = 1;
      if (j == 1) {
        break label76;
      }
      return 1;
    }
    this.mState = 2;
    if (j == 1) {
      return 2;
    }
    label76:
    return 0;
  }
  
  public String toString()
  {
    String str;
    switch (this.mState)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return String.format("%s d=%.0f %s", new Object[] { this.mFence.toString(), Double.valueOf(this.mDistanceToCenter), str });
      str = "IN";
      continue;
      str = "OUT";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GeofenceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */