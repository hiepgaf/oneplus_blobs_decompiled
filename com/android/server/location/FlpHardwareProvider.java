package com.android.server.location;

import android.content.Context;
import android.hardware.location.GeofenceHardwareImpl;
import android.hardware.location.GeofenceHardwareRequestParcelable;
import android.hardware.location.IFusedLocationHardware;
import android.hardware.location.IFusedLocationHardware.Stub;
import android.hardware.location.IFusedLocationHardwareSink;
import android.location.FusedBatchOptions;
import android.location.IFusedGeofenceHardware;
import android.location.IFusedGeofenceHardware.Stub;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

public class FlpHardwareProvider
{
  private static final int FIRST_VERSION_WITH_FLUSH_LOCATIONS = 2;
  private static final int FLP_GEOFENCE_MONITOR_STATUS_AVAILABLE = 2;
  private static final int FLP_GEOFENCE_MONITOR_STATUS_UNAVAILABLE = 1;
  private static final int FLP_RESULT_ERROR = -1;
  private static final int FLP_RESULT_ID_EXISTS = -4;
  private static final int FLP_RESULT_ID_UNKNOWN = -5;
  private static final int FLP_RESULT_INSUFFICIENT_MEMORY = -2;
  private static final int FLP_RESULT_INVALID_GEOFENCE_TRANSITION = -6;
  private static final int FLP_RESULT_SUCCESS = 0;
  private static final int FLP_RESULT_TOO_MANY_GEOFENCES = -3;
  public static final String GEOFENCING = "Geofencing";
  public static final String LOCATION = "Location";
  private static final String TAG = "FlpHardwareProvider";
  private static FlpHardwareProvider sSingletonInstance = null;
  private int mBatchingCapabilities;
  private final Context mContext;
  private final IFusedGeofenceHardware mGeofenceHardwareService = new IFusedGeofenceHardware.Stub()
  {
    public void addGeofences(GeofenceHardwareRequestParcelable[] paramAnonymousArrayOfGeofenceHardwareRequestParcelable)
    {
      FlpHardwareProvider.-wrap6(FlpHardwareProvider.this, paramAnonymousArrayOfGeofenceHardwareRequestParcelable);
    }
    
    public boolean isSupported()
    {
      return FlpHardwareProvider.-wrap2(FlpHardwareProvider.this);
    }
    
    public void modifyGeofenceOptions(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6)
    {
      FlpHardwareProvider.-wrap11(FlpHardwareProvider.this, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousInt5, paramAnonymousInt6);
    }
    
    public void pauseMonitoringGeofence(int paramAnonymousInt)
    {
      FlpHardwareProvider.-wrap12(FlpHardwareProvider.this, paramAnonymousInt);
    }
    
    public void removeGeofences(int[] paramAnonymousArrayOfInt)
    {
      FlpHardwareProvider.-wrap13(FlpHardwareProvider.this, paramAnonymousArrayOfInt);
    }
    
    public void resumeMonitoringGeofence(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      FlpHardwareProvider.-wrap15(FlpHardwareProvider.this, paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  private GeofenceHardwareImpl mGeofenceHardwareSink = null;
  private boolean mHaveBatchingCapabilities;
  private final IFusedLocationHardware mLocationHardware = new IFusedLocationHardware.Stub()
  {
    public void flushBatchedLocations()
    {
      if (getVersion() >= 2)
      {
        FlpHardwareProvider.-wrap7(FlpHardwareProvider.this);
        return;
      }
      Log.wtf("FlpHardwareProvider", "Tried to call flushBatchedLocations on an unsupported implementation");
    }
    
    public int getSupportedBatchSize()
    {
      return FlpHardwareProvider.-wrap4(FlpHardwareProvider.this);
    }
    
    public int getVersion()
    {
      return FlpHardwareProvider.-wrap3(FlpHardwareProvider.this);
    }
    
    public void injectDeviceContext(int paramAnonymousInt)
    {
      FlpHardwareProvider.-wrap8(FlpHardwareProvider.this, paramAnonymousInt);
    }
    
    public void injectDiagnosticData(String paramAnonymousString)
    {
      FlpHardwareProvider.-wrap9(FlpHardwareProvider.this, paramAnonymousString);
    }
    
    public void registerSink(IFusedLocationHardwareSink paramAnonymousIFusedLocationHardwareSink)
    {
      synchronized (FlpHardwareProvider.-get1(FlpHardwareProvider.this))
      {
        if (FlpHardwareProvider.-get0(FlpHardwareProvider.this) != null) {
          Log.e("FlpHardwareProvider", "Replacing an existing IFusedLocationHardware sink");
        }
        FlpHardwareProvider.-set0(FlpHardwareProvider.this, paramAnonymousIFusedLocationHardwareSink);
        FlpHardwareProvider.-wrap5(FlpHardwareProvider.this);
        return;
      }
    }
    
    public void requestBatchOfLocations(int paramAnonymousInt)
    {
      FlpHardwareProvider.-wrap14(FlpHardwareProvider.this, paramAnonymousInt);
    }
    
    public void startBatching(int paramAnonymousInt, FusedBatchOptions paramAnonymousFusedBatchOptions)
    {
      FlpHardwareProvider.-wrap16(FlpHardwareProvider.this, paramAnonymousInt, paramAnonymousFusedBatchOptions);
    }
    
    public void stopBatching(int paramAnonymousInt)
    {
      FlpHardwareProvider.-wrap17(FlpHardwareProvider.this, paramAnonymousInt);
    }
    
    public boolean supportsDeviceContextInjection()
    {
      return FlpHardwareProvider.-wrap0(FlpHardwareProvider.this);
    }
    
    public boolean supportsDiagnosticDataInjection()
    {
      return FlpHardwareProvider.-wrap1(FlpHardwareProvider.this);
    }
    
    public void unregisterSink(IFusedLocationHardwareSink paramAnonymousIFusedLocationHardwareSink)
    {
      synchronized (FlpHardwareProvider.-get1(FlpHardwareProvider.this))
      {
        if (FlpHardwareProvider.-get0(FlpHardwareProvider.this) == paramAnonymousIFusedLocationHardwareSink) {
          FlpHardwareProvider.-set0(FlpHardwareProvider.this, null);
        }
        return;
      }
    }
    
    public void updateBatchingOptions(int paramAnonymousInt, FusedBatchOptions paramAnonymousFusedBatchOptions)
    {
      FlpHardwareProvider.-wrap18(FlpHardwareProvider.this, paramAnonymousInt, paramAnonymousFusedBatchOptions);
    }
  };
  private IFusedLocationHardwareSink mLocationSink = null;
  private final Object mLocationSinkLock = new Object();
  private int mVersion = 1;
  
  static
  {
    nativeClassInit();
  }
  
  private FlpHardwareProvider(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = (LocationManager)this.mContext.getSystemService("location");
    LocationRequest localLocationRequest = LocationRequest.createFromDeprecatedProvider("passive", 0L, 0.0F, false);
    localLocationRequest.setHideFromAppOps(true);
    paramContext.requestLocationUpdates(localLocationRequest, new NetworkLocationListener(null), Looper.myLooper());
  }
  
  private GeofenceHardwareImpl getGeofenceHardwareSink()
  {
    if (this.mGeofenceHardwareSink == null)
    {
      this.mGeofenceHardwareSink = GeofenceHardwareImpl.getInstance(this.mContext);
      this.mGeofenceHardwareSink.setVersion(getVersion());
    }
    return this.mGeofenceHardwareSink;
  }
  
  public static FlpHardwareProvider getInstance(Context paramContext)
  {
    if (sSingletonInstance == null)
    {
      sSingletonInstance = new FlpHardwareProvider(paramContext);
      sSingletonInstance.nativeInit();
    }
    return sSingletonInstance;
  }
  
  private int getVersion()
  {
    synchronized (this.mLocationSinkLock)
    {
      if (this.mHaveBatchingCapabilities)
      {
        int i = this.mVersion;
        return i;
      }
      return 1;
    }
  }
  
  public static boolean isSupported()
  {
    return nativeIsSupported();
  }
  
  private void maybeSendCapabilities()
  {
    IFusedLocationHardwareSink localIFusedLocationHardwareSink;
    int i;
    synchronized (this.mLocationSinkLock)
    {
      localIFusedLocationHardwareSink = this.mLocationSink;
      boolean bool = this.mHaveBatchingCapabilities;
      i = this.mBatchingCapabilities;
      if ((localIFusedLocationHardwareSink == null) || (!bool)) {}
    }
  }
  
  private native void nativeAddGeofences(GeofenceHardwareRequestParcelable[] paramArrayOfGeofenceHardwareRequestParcelable);
  
  private static native void nativeClassInit();
  
  private native void nativeCleanup();
  
  private native void nativeFlushBatchedLocations();
  
  private native int nativeGetBatchSize();
  
  private native void nativeInit();
  
  private native void nativeInjectDeviceContext(int paramInt);
  
  private native void nativeInjectDiagnosticData(String paramString);
  
  private native void nativeInjectLocation(Location paramLocation);
  
  private native boolean nativeIsDeviceContextSupported();
  
  private native boolean nativeIsDiagnosticSupported();
  
  private native boolean nativeIsGeofencingSupported();
  
  private static native boolean nativeIsSupported();
  
  private native void nativeModifyGeofenceOption(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private native void nativePauseGeofence(int paramInt);
  
  private native void nativeRemoveGeofences(int[] paramArrayOfInt);
  
  private native void nativeRequestBatchedLocation(int paramInt);
  
  private native void nativeResumeGeofence(int paramInt1, int paramInt2);
  
  private native void nativeStartBatching(int paramInt, FusedBatchOptions paramFusedBatchOptions);
  
  private native void nativeStopBatching(int paramInt);
  
  private native void nativeUpdateBatchingOptions(int paramInt, FusedBatchOptions paramFusedBatchOptions);
  
  private void onBatchingCapabilities(int paramInt)
  {
    synchronized (this.mLocationSinkLock)
    {
      this.mHaveBatchingCapabilities = true;
      this.mBatchingCapabilities = paramInt;
      maybeSendCapabilities();
      if (this.mGeofenceHardwareSink != null) {
        this.mGeofenceHardwareSink.setVersion(getVersion());
      }
      return;
    }
  }
  
  private void onBatchingStatus(int paramInt)
  {
    IFusedLocationHardwareSink localIFusedLocationHardwareSink;
    synchronized (this.mLocationSinkLock)
    {
      localIFusedLocationHardwareSink = this.mLocationSink;
      if (localIFusedLocationHardwareSink == null) {}
    }
  }
  
  private void onDataReport(String paramString)
  {
    IFusedLocationHardwareSink localIFusedLocationHardwareSink;
    synchronized (this.mLocationSinkLock)
    {
      localIFusedLocationHardwareSink = this.mLocationSink;
    }
    try
    {
      if (this.mLocationSink != null) {
        localIFusedLocationHardwareSink.onDiagnosticDataAvailable(paramString);
      }
      return;
    }
    catch (RemoteException paramString)
    {
      Log.e("FlpHardwareProvider", "RemoteException calling onDiagnosticDataAvailable");
    }
    paramString = finally;
    throw paramString;
  }
  
  private void onGeofenceAdd(int paramInt1, int paramInt2)
  {
    getGeofenceHardwareSink().reportGeofenceAddStatus(paramInt1, translateToGeofenceHardwareStatus(paramInt2));
  }
  
  private void onGeofenceMonitorStatus(int paramInt1, int paramInt2, Location paramLocation)
  {
    Location localLocation = null;
    if (paramLocation != null) {
      localLocation = updateLocationInformation(paramLocation);
    }
    switch (paramInt1)
    {
    default: 
      Log.e("FlpHardwareProvider", "Invalid FlpHal Geofence monitor status: " + paramInt1);
      paramInt1 = 1;
    }
    for (;;)
    {
      getGeofenceHardwareSink().reportGeofenceMonitorStatus(1, paramInt1, localLocation, paramInt2);
      return;
      paramInt1 = 1;
      continue;
      paramInt1 = 0;
    }
  }
  
  private void onGeofencePause(int paramInt1, int paramInt2)
  {
    getGeofenceHardwareSink().reportGeofencePauseStatus(paramInt1, translateToGeofenceHardwareStatus(paramInt2));
  }
  
  private void onGeofenceRemove(int paramInt1, int paramInt2)
  {
    getGeofenceHardwareSink().reportGeofenceRemoveStatus(paramInt1, translateToGeofenceHardwareStatus(paramInt2));
  }
  
  private void onGeofenceResume(int paramInt1, int paramInt2)
  {
    getGeofenceHardwareSink().reportGeofenceResumeStatus(paramInt1, translateToGeofenceHardwareStatus(paramInt2));
  }
  
  private void onGeofenceTransition(int paramInt1, Location paramLocation, int paramInt2, long paramLong, int paramInt3)
  {
    getGeofenceHardwareSink().reportGeofenceTransition(paramInt1, updateLocationInformation(paramLocation), paramInt2, paramLong, 1, paramInt3);
  }
  
  private void onGeofencingCapabilities(int paramInt)
  {
    getGeofenceHardwareSink().onCapabilities(paramInt);
  }
  
  private void onLocationReport(Location[] paramArrayOfLocation)
  {
    int i = 0;
    int j = paramArrayOfLocation.length;
    while (i < j)
    {
      ??? = paramArrayOfLocation[i];
      ((Location)???).setProvider("fused");
      ((Location)???).setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
      i += 1;
    }
    IFusedLocationHardwareSink localIFusedLocationHardwareSink;
    synchronized (this.mLocationSinkLock)
    {
      localIFusedLocationHardwareSink = this.mLocationSink;
      if (localIFusedLocationHardwareSink == null) {}
    }
    try
    {
      localIFusedLocationHardwareSink.onLocationAvailable(paramArrayOfLocation);
      return;
    }
    catch (RemoteException paramArrayOfLocation)
    {
      Log.e("FlpHardwareProvider", "RemoteException calling onLocationAvailable");
    }
    paramArrayOfLocation = finally;
    throw paramArrayOfLocation;
  }
  
  private void setVersion(int paramInt)
  {
    this.mVersion = paramInt;
    if (this.mGeofenceHardwareSink != null) {
      this.mGeofenceHardwareSink.setVersion(getVersion());
    }
  }
  
  private static int translateToGeofenceHardwareStatus(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.e("FlpHardwareProvider", String.format("Invalid FlpHal result code: %d", new Object[] { Integer.valueOf(paramInt) }));
      return 5;
    case 0: 
      return 0;
    case -1: 
      return 5;
    case -2: 
      return 6;
    case -3: 
      return 1;
    case -4: 
      return 2;
    case -5: 
      return 3;
    }
    return 4;
  }
  
  private Location updateLocationInformation(Location paramLocation)
  {
    paramLocation.setProvider("fused");
    paramLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
    return paramLocation;
  }
  
  public void cleanup()
  {
    Log.i("FlpHardwareProvider", "Calling nativeCleanup()");
    nativeCleanup();
  }
  
  public IFusedGeofenceHardware getGeofenceHardware()
  {
    return this.mGeofenceHardwareService;
  }
  
  public IFusedLocationHardware getLocationHardware()
  {
    return this.mLocationHardware;
  }
  
  private final class NetworkLocationListener
    implements LocationListener
  {
    private NetworkLocationListener() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      if (("network".equals(paramLocation.getProvider())) && (paramLocation.hasAccuracy()))
      {
        FlpHardwareProvider.-wrap10(FlpHardwareProvider.this, paramLocation);
        return;
      }
    }
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/FlpHardwareProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */