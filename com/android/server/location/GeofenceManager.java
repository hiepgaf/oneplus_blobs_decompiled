package com.android.server.location;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.content.Intent;
import android.location.Geofence;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Slog;
import com.android.server.LocationManagerService;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GeofenceManager
  implements LocationListener, PendingIntent.OnFinished
{
  private static final boolean D = LocationManagerService.D;
  private static final long MAX_AGE_NANOS = 300000000000L;
  private static final long MAX_INTERVAL_MS = 7200000L;
  private static final int MAX_SPEED_M_S = 100;
  private static final long MIN_INTERVAL_MS = 60000L;
  private static final int MSG_UPDATE_FENCES = 1;
  private static final String TAG = "GeofenceManager";
  private final AppOpsManager mAppOps;
  private final LocationBlacklist mBlacklist;
  private final Context mContext;
  private List<GeofenceState> mFences = new LinkedList();
  private final GeofenceHandler mHandler;
  private Location mLastLocationUpdate;
  private final LocationManager mLocationManager;
  private long mLocationUpdateInterval;
  private Object mLock = new Object();
  private boolean mPendingUpdate;
  private boolean mReceivingLocationUpdates;
  private final PowerManager.WakeLock mWakeLock;
  
  public GeofenceManager(Context paramContext, LocationBlacklist paramLocationBlacklist)
  {
    this.mContext = paramContext;
    this.mLocationManager = ((LocationManager)this.mContext.getSystemService("location"));
    this.mAppOps = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mWakeLock = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, "GeofenceManager");
    this.mHandler = new GeofenceHandler();
    this.mBlacklist = paramLocationBlacklist;
  }
  
  private Location getFreshLocationLocked()
  {
    Location localLocation1;
    if (this.mReceivingLocationUpdates)
    {
      localLocation1 = this.mLastLocationUpdate;
      localLocation2 = localLocation1;
      if (localLocation1 == null) {
        if (!this.mFences.isEmpty()) {
          break label43;
        }
      }
    }
    label43:
    for (Location localLocation2 = localLocation1;; localLocation2 = this.mLocationManager.getLastLocation())
    {
      if (localLocation2 != null) {
        break label54;
      }
      return null;
      localLocation1 = null;
      break;
    }
    label54:
    if (SystemClock.elapsedRealtimeNanos() - localLocation2.getElapsedRealtimeNanos() > 300000000000L) {
      return null;
    }
    return localLocation2;
  }
  
  private void removeExpiredFencesLocked()
  {
    long l = SystemClock.elapsedRealtime();
    Iterator localIterator = this.mFences.iterator();
    while (localIterator.hasNext()) {
      if (((GeofenceState)localIterator.next()).mExpireAt < l) {
        localIterator.remove();
      }
    }
  }
  
  private void scheduleUpdateFencesLocked()
  {
    if (!this.mPendingUpdate)
    {
      this.mPendingUpdate = true;
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  private void sendIntent(PendingIntent paramPendingIntent, Intent paramIntent)
  {
    this.mWakeLock.acquire();
    try
    {
      paramPendingIntent.send(this.mContext, 0, paramIntent, this, null, "android.permission.ACCESS_FINE_LOCATION");
      return;
    }
    catch (PendingIntent.CanceledException paramIntent)
    {
      removeFence(null, paramPendingIntent);
      this.mWakeLock.release();
    }
  }
  
  private void sendIntentEnter(PendingIntent paramPendingIntent)
  {
    if (D) {
      Slog.d("GeofenceManager", "sendIntentEnter: pendingIntent=" + paramPendingIntent);
    }
    Intent localIntent = new Intent();
    localIntent.putExtra("entering", true);
    sendIntent(paramPendingIntent, localIntent);
  }
  
  private void sendIntentExit(PendingIntent paramPendingIntent)
  {
    if (D) {
      Slog.d("GeofenceManager", "sendIntentExit: pendingIntent=" + paramPendingIntent);
    }
    Intent localIntent = new Intent();
    localIntent.putExtra("entering", false);
    sendIntent(paramPendingIntent, localIntent);
  }
  
  private void updateFences()
  {
    LinkedList localLinkedList1 = new LinkedList();
    LinkedList localLinkedList2 = new LinkedList();
    Location localLocation;
    double d1;
    int i;
    Object localObject3;
    for (;;)
    {
      GeofenceState localGeofenceState;
      synchronized (this.mLock)
      {
        this.mPendingUpdate = false;
        removeExpiredFencesLocked();
        localLocation = getFreshLocationLocked();
        d1 = Double.MAX_VALUE;
        i = 0;
        localObject3 = this.mFences.iterator();
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
        localGeofenceState = (GeofenceState)((Iterator)localObject3).next();
        if (this.mBlacklist.isBlacklisted(localGeofenceState.mPackageName))
        {
          if (!D) {
            continue;
          }
          Slog.d("GeofenceManager", "skipping geofence processing for blacklisted app: " + localGeofenceState.mPackageName);
        }
      }
      if ((LocationManagerService.resolutionLevelToOp(localGeofenceState.mAllowedResolutionLevel) >= 0) && (this.mAppOps.noteOpNoThrow(1, localGeofenceState.mUid, localGeofenceState.mPackageName) != 0))
      {
        if (D) {
          Slog.d("GeofenceManager", "skipping geofence processing for no op app: " + localGeofenceState.mPackageName);
        }
      }
      else
      {
        int j = 1;
        i = j;
        if (localLocation != null)
        {
          i = localGeofenceState.processLocation(localLocation);
          if ((i & 0x1) != 0) {
            ((List)localObject1).add(localGeofenceState.mIntent);
          }
          if ((i & 0x2) != 0) {
            localLinkedList2.add(localGeofenceState.mIntent);
          }
          double d2 = localGeofenceState.getDistanceToBoundary();
          i = j;
          if (d2 < d1)
          {
            d1 = d2;
            i = j;
          }
        }
      }
    }
    long l;
    if (i != 0) {
      if ((localLocation != null) && (Double.compare(d1, Double.MAX_VALUE) != 0))
      {
        l = Math.min(7200000.0D, Math.max(60000.0D, 1000.0D * d1 / 100.0D));
        if ((!this.mReceivingLocationUpdates) || (this.mLocationUpdateInterval != l))
        {
          this.mReceivingLocationUpdates = true;
          this.mLocationUpdateInterval = l;
          this.mLastLocationUpdate = localLocation;
          localObject3 = new LocationRequest();
          ((LocationRequest)localObject3).setInterval(l).setFastestInterval(0L);
          this.mLocationManager.requestLocationUpdates((LocationRequest)localObject3, this, this.mHandler.getLooper());
        }
      }
    }
    for (;;)
    {
      if (D) {
        Slog.d("GeofenceManager", "updateFences: location=" + localLocation + ", mFences.size()=" + this.mFences.size() + ", mReceivingLocationUpdates=" + this.mReceivingLocationUpdates + ", mLocationUpdateInterval=" + this.mLocationUpdateInterval + ", mLastLocationUpdate=" + this.mLastLocationUpdate);
      }
      ??? = localLinkedList2.iterator();
      while (((Iterator)???).hasNext()) {
        sendIntentExit((PendingIntent)((Iterator)???).next());
      }
      l = 60000L;
      break;
      if (this.mReceivingLocationUpdates)
      {
        this.mReceivingLocationUpdates = false;
        this.mLocationUpdateInterval = 0L;
        this.mLastLocationUpdate = null;
        this.mLocationManager.removeUpdates(this);
      }
    }
    Iterator localIterator = ((Iterable)localObject1).iterator();
    while (localIterator.hasNext()) {
      sendIntentEnter((PendingIntent)localIterator.next());
    }
  }
  
  public void addFence(LocationRequest arg1, Geofence paramGeofence, PendingIntent paramPendingIntent, int paramInt1, int paramInt2, String paramString)
  {
    if (D) {
      Slog.d("GeofenceManager", "addFence: request=" + ??? + ", geofence=" + paramGeofence + ", intent=" + paramPendingIntent + ", uid=" + paramInt2 + ", packageName=" + paramString);
    }
    paramString = new GeofenceState(paramGeofence, ???.getExpireAt(), paramInt1, paramInt2, paramString, paramPendingIntent);
    synchronized (this.mLock)
    {
      paramInt1 = this.mFences.size() - 1;
      if (paramInt1 >= 0)
      {
        GeofenceState localGeofenceState = (GeofenceState)this.mFences.get(paramInt1);
        if ((paramGeofence.equals(localGeofenceState.mFence)) && (paramPendingIntent.equals(localGeofenceState.mIntent))) {
          this.mFences.remove(paramInt1);
        }
      }
      else
      {
        this.mFences.add(paramString);
        scheduleUpdateFencesLocked();
        return;
      }
      paramInt1 -= 1;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("  Geofences:");
    Iterator localIterator = this.mFences.iterator();
    while (localIterator.hasNext())
    {
      GeofenceState localGeofenceState = (GeofenceState)localIterator.next();
      paramPrintWriter.append("    ");
      paramPrintWriter.append(localGeofenceState.mPackageName);
      paramPrintWriter.append(" ");
      paramPrintWriter.append(localGeofenceState.mFence.toString());
      paramPrintWriter.append("\n");
    }
  }
  
  public void onLocationChanged(Location paramLocation)
  {
    synchronized (this.mLock)
    {
      if (this.mReceivingLocationUpdates) {
        this.mLastLocationUpdate = paramLocation;
      }
      if (this.mPendingUpdate)
      {
        this.mHandler.removeMessages(1);
        updateFences();
        return;
      }
      this.mPendingUpdate = true;
    }
  }
  
  public void onProviderDisabled(String paramString) {}
  
  public void onProviderEnabled(String paramString) {}
  
  public void onSendFinished(PendingIntent paramPendingIntent, Intent paramIntent, int paramInt, String paramString, Bundle paramBundle)
  {
    this.mWakeLock.release();
  }
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  
  public void removeFence(Geofence paramGeofence, PendingIntent paramPendingIntent)
  {
    if (D) {
      Slog.d("GeofenceManager", "removeFence: fence=" + paramGeofence + ", intent=" + paramPendingIntent);
    }
    for (;;)
    {
      Iterator localIterator;
      GeofenceState localGeofenceState;
      synchronized (this.mLock)
      {
        localIterator = this.mFences.iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        localGeofenceState = (GeofenceState)localIterator.next();
        if (!localGeofenceState.mIntent.equals(paramPendingIntent)) {
          continue;
        }
        if (paramGeofence == null) {
          localIterator.remove();
        }
      }
      if (paramGeofence.equals(localGeofenceState.mFence)) {
        localIterator.remove();
      }
    }
    scheduleUpdateFencesLocked();
  }
  
  public void removeFence(String paramString)
  {
    if (D) {
      Slog.d("GeofenceManager", "removeFence: packageName=" + paramString);
    }
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mFences.iterator();
      while (localIterator.hasNext()) {
        if (((GeofenceState)localIterator.next()).mPackageName.equals(paramString)) {
          localIterator.remove();
        }
      }
    }
    scheduleUpdateFencesLocked();
  }
  
  private final class GeofenceHandler
    extends Handler
  {
    public GeofenceHandler()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      GeofenceManager.-wrap0(GeofenceManager.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GeofenceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */