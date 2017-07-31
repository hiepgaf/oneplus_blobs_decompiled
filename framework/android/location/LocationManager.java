package android.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.util.SeempLog;
import com.android.internal.location.ProviderProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationManager
{
  public static final String EXTRA_GPS_ENABLED = "enabled";
  public static final String FUSED_PROVIDER = "fused";
  public static final String GPS_ENABLED_CHANGE_ACTION = "android.location.GPS_ENABLED_CHANGE";
  public static final String GPS_FIX_CHANGE_ACTION = "android.location.GPS_FIX_CHANGE";
  public static final String GPS_PROVIDER = "gps";
  public static final String HIGH_POWER_REQUEST_CHANGE_ACTION = "android.location.HIGH_POWER_REQUEST_CHANGE";
  public static final String KEY_LOCATION_CHANGED = "location";
  public static final String KEY_PROVIDER_ENABLED = "providerEnabled";
  public static final String KEY_PROXIMITY_ENTERING = "entering";
  public static final String KEY_STATUS_CHANGED = "status";
  public static final String MODE_CHANGED_ACTION = "android.location.MODE_CHANGED";
  public static final String NETWORK_PROVIDER = "network";
  public static final String PASSIVE_PROVIDER = "passive";
  public static final String PROVIDERS_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED";
  private static final String TAG = "LocationManager";
  private final Context mContext;
  private final GnssMeasurementCallbackTransport mGnssMeasurementCallbackTransport;
  private final GnssNavigationMessageCallbackTransport mGnssNavigationMessageCallbackTransport;
  private final HashMap<OnNmeaMessageListener, GnssStatusListenerTransport> mGnssNmeaListeners = new HashMap();
  private GnssStatus mGnssStatus;
  private final HashMap<GnssStatus.Callback, GnssStatusListenerTransport> mGnssStatusListeners = new HashMap();
  private final HashMap<GpsStatus.NmeaListener, GnssStatusListenerTransport> mGpsNmeaListeners = new HashMap();
  private final HashMap<GpsStatus.Listener, GnssStatusListenerTransport> mGpsStatusListeners = new HashMap();
  private HashMap<LocationListener, ListenerTransport> mListeners = new HashMap();
  private final HashMap<GnssNavigationMessageEvent.Callback, GnssNavigationMessage.Callback> mNavigationMessageBridge = new HashMap();
  private final HashMap<GnssNmeaListener, GnssStatusListenerTransport> mOldGnssNmeaListeners = new HashMap();
  private final HashMap<GnssStatusCallback, GnssStatusListenerTransport> mOldGnssStatusListeners = new HashMap();
  private final ILocationManager mService;
  private int mTimeToFirstFix;
  
  public LocationManager(Context paramContext, ILocationManager paramILocationManager)
  {
    this.mService = paramILocationManager;
    this.mContext = paramContext;
    this.mGnssMeasurementCallbackTransport = new GnssMeasurementCallbackTransport(this.mContext, this.mService);
    this.mGnssNavigationMessageCallbackTransport = new GnssNavigationMessageCallbackTransport(this.mContext, this.mService);
  }
  
  private static void checkCriteria(Criteria paramCriteria)
  {
    if (paramCriteria == null) {
      throw new IllegalArgumentException("invalid criteria: " + paramCriteria);
    }
  }
  
  private static void checkGeofence(Geofence paramGeofence)
  {
    if (paramGeofence == null) {
      throw new IllegalArgumentException("invalid geofence: " + paramGeofence);
    }
  }
  
  private static void checkListener(LocationListener paramLocationListener)
  {
    if (paramLocationListener == null) {
      throw new IllegalArgumentException("invalid listener: " + paramLocationListener);
    }
  }
  
  private void checkPendingIntent(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      throw new IllegalArgumentException("invalid pending intent: " + paramPendingIntent);
    }
    if (!paramPendingIntent.isTargetedToPackage())
    {
      paramPendingIntent = new IllegalArgumentException("pending intent must be targeted to package");
      if (this.mContext.getApplicationInfo().targetSdkVersion > 16) {
        throw paramPendingIntent;
      }
      Log.w("LocationManager", paramPendingIntent);
    }
  }
  
  private static void checkProvider(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("invalid provider: " + paramString);
    }
  }
  
  private LocationProvider createProvider(String paramString, ProviderProperties paramProviderProperties)
  {
    return new LocationProvider(paramString, paramProviderProperties);
  }
  
  private void requestLocationUpdates(LocationRequest paramLocationRequest, LocationListener paramLocationListener, Looper paramLooper, PendingIntent paramPendingIntent)
  {
    SeempLog.record(47);
    String str = this.mContext.getPackageName();
    Log.d("LocationManager", "requestLocationUpdates listener=" + paramLocationListener + " packageName= " + str);
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(this.mContext).requestPermissionAuto("android.permission.ACCESS_FINE_LOCATION"))) {
      return;
    }
    paramLocationListener = wrapListener(paramLocationListener, paramLooper);
    try
    {
      this.mService.requestLocationUpdates(paramLocationRequest, paramLocationListener, paramPendingIntent, str);
      return;
    }
    catch (RemoteException paramLocationRequest)
    {
      throw paramLocationRequest.rethrowFromSystemServer();
    }
  }
  
  private ListenerTransport wrapListener(LocationListener paramLocationListener, Looper paramLooper)
  {
    if (paramLocationListener == null) {
      return null;
    }
    synchronized (this.mListeners)
    {
      ListenerTransport localListenerTransport2 = (ListenerTransport)this.mListeners.get(paramLocationListener);
      ListenerTransport localListenerTransport1 = localListenerTransport2;
      if (localListenerTransport2 == null) {
        localListenerTransport1 = new ListenerTransport(paramLocationListener, paramLooper);
      }
      this.mListeners.put(paramLocationListener, localListenerTransport1);
      return localListenerTransport1;
    }
  }
  
  public void addGeofence(LocationRequest paramLocationRequest, Geofence paramGeofence, PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    checkGeofence(paramGeofence);
    try
    {
      this.mService.requestGeofence(paramLocationRequest, paramGeofence, paramPendingIntent, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException paramLocationRequest)
    {
      throw paramLocationRequest.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean addGpsMeasurementListener(GpsMeasurementsEvent.Listener paramListener)
  {
    return false;
  }
  
  @Deprecated
  public boolean addGpsNavigationMessageListener(GpsNavigationMessageEvent.Listener paramListener)
  {
    return false;
  }
  
  @Deprecated
  public boolean addGpsStatusListener(GpsStatus.Listener paramListener)
  {
    SeempLog.record(43);
    if (this.mGpsStatusListeners.get(paramListener) != null) {
      return true;
    }
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(this.mContext).requestPermissionAuto("android.permission.ACCESS_FINE_LOCATION"))) {
      return false;
    }
    try
    {
      GnssStatusListenerTransport localGnssStatusListenerTransport = new GnssStatusListenerTransport(paramListener);
      boolean bool = this.mService.registerGnssStatusCallback(localGnssStatusListenerTransport, this.mContext.getPackageName());
      if (bool) {
        this.mGpsStatusListeners.put(paramListener, localGnssStatusListenerTransport);
      }
      return bool;
    }
    catch (RemoteException paramListener)
    {
      throw paramListener.rethrowFromSystemServer();
    }
  }
  
  public boolean addNmeaListener(GnssNmeaListener paramGnssNmeaListener)
  {
    return addNmeaListener(paramGnssNmeaListener, null);
  }
  
  public boolean addNmeaListener(GnssNmeaListener paramGnssNmeaListener, Handler paramHandler)
  {
    if (this.mGpsNmeaListeners.get(paramGnssNmeaListener) != null) {
      return true;
    }
    try
    {
      paramHandler = new GnssStatusListenerTransport(paramGnssNmeaListener, paramHandler);
      boolean bool = this.mService.registerGnssStatusCallback(paramHandler, this.mContext.getPackageName());
      if (bool) {
        this.mOldGnssNmeaListeners.put(paramGnssNmeaListener, paramHandler);
      }
      return bool;
    }
    catch (RemoteException paramGnssNmeaListener)
    {
      throw paramGnssNmeaListener.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean addNmeaListener(GpsStatus.NmeaListener paramNmeaListener)
  {
    SeempLog.record(44);
    if (this.mGpsNmeaListeners.get(paramNmeaListener) != null) {
      return true;
    }
    try
    {
      GnssStatusListenerTransport localGnssStatusListenerTransport = new GnssStatusListenerTransport(paramNmeaListener);
      boolean bool = this.mService.registerGnssStatusCallback(localGnssStatusListenerTransport, this.mContext.getPackageName());
      if (bool) {
        this.mGpsNmeaListeners.put(paramNmeaListener, localGnssStatusListenerTransport);
      }
      return bool;
    }
    catch (RemoteException paramNmeaListener)
    {
      throw paramNmeaListener.rethrowFromSystemServer();
    }
  }
  
  public boolean addNmeaListener(OnNmeaMessageListener paramOnNmeaMessageListener)
  {
    return addNmeaListener(paramOnNmeaMessageListener, null);
  }
  
  public boolean addNmeaListener(OnNmeaMessageListener paramOnNmeaMessageListener, Handler paramHandler)
  {
    if (this.mGpsNmeaListeners.get(paramOnNmeaMessageListener) != null) {
      return true;
    }
    try
    {
      paramHandler = new GnssStatusListenerTransport(paramOnNmeaMessageListener, paramHandler);
      boolean bool = this.mService.registerGnssStatusCallback(paramHandler, this.mContext.getPackageName());
      if (bool) {
        this.mGnssNmeaListeners.put(paramOnNmeaMessageListener, paramHandler);
      }
      return bool;
    }
    catch (RemoteException paramOnNmeaMessageListener)
    {
      throw paramOnNmeaMessageListener.rethrowFromSystemServer();
    }
  }
  
  public void addProximityAlert(double paramDouble1, double paramDouble2, float paramFloat, long paramLong, PendingIntent paramPendingIntent)
  {
    SeempLog.record(45);
    checkPendingIntent(paramPendingIntent);
    long l = paramLong;
    if (paramLong < 0L) {
      l = Long.MAX_VALUE;
    }
    Geofence localGeofence = Geofence.createCircle(paramDouble1, paramDouble2, paramFloat);
    LocationRequest localLocationRequest = new LocationRequest().setExpireIn(l);
    try
    {
      this.mService.requestGeofence(localLocationRequest, localGeofence, paramPendingIntent, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public void addTestProvider(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, int paramInt1, int paramInt2)
  {
    ProviderProperties localProviderProperties = new ProviderProperties(paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6, paramBoolean7, paramInt1, paramInt2);
    if (paramString.matches("[^a-zA-Z0-9]")) {
      throw new IllegalArgumentException("provider name contains illegal character: " + paramString);
    }
    try
    {
      this.mService.addTestProvider(paramString, localProviderProperties, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearAllPendingBroadcastsLocked()
  {
    try
    {
      this.mService.clearAllPendingBroadcastsLocked();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("LocationManager", "RemoteException", localRemoteException);
    }
  }
  
  public void clearTestProviderEnabled(String paramString)
  {
    try
    {
      this.mService.clearTestProviderEnabled(paramString, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearTestProviderLocation(String paramString)
  {
    try
    {
      this.mService.clearTestProviderLocation(paramString, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearTestProviderStatus(String paramString)
  {
    try
    {
      this.mService.clearTestProviderStatus(paramString, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<String> getAllProviders()
  {
    try
    {
      List localList = this.mService.getAllProviders();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getBestProvider(Criteria paramCriteria, boolean paramBoolean)
  {
    checkCriteria(paramCriteria);
    try
    {
      paramCriteria = this.mService.getBestProvider(paramCriteria, paramBoolean);
      return paramCriteria;
    }
    catch (RemoteException paramCriteria)
    {
      throw paramCriteria.rethrowFromSystemServer();
    }
  }
  
  public List<String> getCurrentProviderPackageList(String paramString)
  {
    try
    {
      paramString = this.mService.getCurrentProviderPackageList(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getGnssYearOfHardware()
  {
    try
    {
      int i = this.mService.getGnssYearOfHardware();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public GpsStatus getGpsStatus(GpsStatus paramGpsStatus)
  {
    GpsStatus localGpsStatus = paramGpsStatus;
    if (paramGpsStatus == null) {
      localGpsStatus = new GpsStatus();
    }
    if (this.mGnssStatus != null) {
      localGpsStatus.setStatus(this.mGnssStatus, this.mTimeToFirstFix);
    }
    return localGpsStatus;
  }
  
  public Location getLastKnownLocation()
  {
    try
    {
      Location localLocation = this.mService.getLastKnownLocation();
      return localLocation;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("LocationManager", "RemoteException", localRemoteException);
    }
    return null;
  }
  
  public Location getLastKnownLocation(String paramString)
  {
    SeempLog.record(46);
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(this.mContext).requestPermissionAuto("android.permission.ACCESS_FINE_LOCATION"))) {
      return null;
    }
    checkProvider(paramString);
    String str = this.mContext.getPackageName();
    paramString = LocationRequest.createFromDeprecatedProvider(paramString, 0L, 0.0F, true);
    try
    {
      paramString = this.mService.getLastLocation(paramString, str);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Location getLastLocation()
  {
    Object localObject = this.mContext.getPackageName();
    try
    {
      localObject = this.mService.getLastLocation(null, (String)localObject);
      return (Location)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public LocationProvider getProvider(String paramString)
  {
    checkProvider(paramString);
    try
    {
      ProviderProperties localProviderProperties = this.mService.getProviderProperties(paramString);
      if (localProviderProperties == null) {
        return null;
      }
      paramString = createProvider(paramString, localProviderProperties);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<String> getProviders(Criteria paramCriteria, boolean paramBoolean)
  {
    checkCriteria(paramCriteria);
    try
    {
      paramCriteria = this.mService.getProviders(paramCriteria, paramBoolean);
      return paramCriteria;
    }
    catch (RemoteException paramCriteria)
    {
      throw paramCriteria.rethrowFromSystemServer();
    }
  }
  
  public List<String> getProviders(boolean paramBoolean)
  {
    try
    {
      List localList = this.mService.getProviders(null, paramBoolean);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isProviderEnabled(String paramString)
  {
    checkProvider(paramString);
    try
    {
      boolean bool = this.mService.isProviderEnabled(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean registerGnssMeasurementsCallback(GnssMeasurementsEvent.Callback paramCallback)
  {
    return registerGnssMeasurementsCallback(paramCallback, null);
  }
  
  public boolean registerGnssMeasurementsCallback(GnssMeasurementsEvent.Callback paramCallback, Handler paramHandler)
  {
    return this.mGnssMeasurementCallbackTransport.add(paramCallback, paramHandler);
  }
  
  public boolean registerGnssNavigationMessageCallback(GnssNavigationMessage.Callback paramCallback)
  {
    return registerGnssNavigationMessageCallback(paramCallback, null);
  }
  
  public boolean registerGnssNavigationMessageCallback(GnssNavigationMessage.Callback paramCallback, Handler paramHandler)
  {
    return this.mGnssNavigationMessageCallbackTransport.add(paramCallback, paramHandler);
  }
  
  public boolean registerGnssNavigationMessageCallback(GnssNavigationMessageEvent.Callback paramCallback)
  {
    return registerGnssNavigationMessageCallback(paramCallback, null);
  }
  
  public boolean registerGnssNavigationMessageCallback(final GnssNavigationMessageEvent.Callback paramCallback, Handler paramHandler)
  {
    GnssNavigationMessage.Callback local1 = new GnssNavigationMessage.Callback()
    {
      public void onGnssNavigationMessageReceived(GnssNavigationMessage paramAnonymousGnssNavigationMessage)
      {
        paramAnonymousGnssNavigationMessage = new GnssNavigationMessageEvent(paramAnonymousGnssNavigationMessage);
        paramCallback.onGnssNavigationMessageReceived(paramAnonymousGnssNavigationMessage);
      }
      
      public void onStatusChanged(int paramAnonymousInt)
      {
        paramCallback.onStatusChanged(paramAnonymousInt);
      }
    };
    this.mNavigationMessageBridge.put(paramCallback, local1);
    return this.mGnssNavigationMessageCallbackTransport.add(local1, paramHandler);
  }
  
  public boolean registerGnssStatusCallback(GnssStatus.Callback paramCallback)
  {
    return registerGnssStatusCallback(paramCallback, null);
  }
  
  public boolean registerGnssStatusCallback(GnssStatus.Callback paramCallback, Handler paramHandler)
  {
    if (this.mGnssStatusListeners.get(paramCallback) != null) {
      return true;
    }
    try
    {
      paramHandler = new GnssStatusListenerTransport(paramCallback, paramHandler);
      boolean bool = this.mService.registerGnssStatusCallback(paramHandler, this.mContext.getPackageName());
      if (bool) {
        this.mGnssStatusListeners.put(paramCallback, paramHandler);
      }
      return bool;
    }
    catch (RemoteException paramCallback)
    {
      throw paramCallback.rethrowFromSystemServer();
    }
  }
  
  public boolean registerGnssStatusCallback(GnssStatusCallback paramGnssStatusCallback)
  {
    return registerGnssStatusCallback(paramGnssStatusCallback, null);
  }
  
  public boolean registerGnssStatusCallback(GnssStatusCallback paramGnssStatusCallback, Handler paramHandler)
  {
    if (this.mOldGnssStatusListeners.get(paramGnssStatusCallback) != null) {
      return true;
    }
    try
    {
      paramHandler = new GnssStatusListenerTransport(paramGnssStatusCallback, paramHandler);
      boolean bool = this.mService.registerGnssStatusCallback(paramHandler, this.mContext.getPackageName());
      if (bool) {
        this.mOldGnssStatusListeners.put(paramGnssStatusCallback, paramHandler);
      }
      return bool;
    }
    catch (RemoteException paramGnssStatusCallback)
    {
      throw paramGnssStatusCallback.rethrowFromSystemServer();
    }
  }
  
  public void removeAllGeofences(PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    String str = this.mContext.getPackageName();
    try
    {
      this.mService.removeGeofence(null, paramPendingIntent, str);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public void removeGeofence(Geofence paramGeofence, PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    checkGeofence(paramGeofence);
    String str = this.mContext.getPackageName();
    try
    {
      this.mService.removeGeofence(paramGeofence, paramPendingIntent, str);
      return;
    }
    catch (RemoteException paramGeofence)
    {
      throw paramGeofence.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void removeGpsMeasurementListener(GpsMeasurementsEvent.Listener paramListener) {}
  
  @Deprecated
  public void removeGpsNavigationMessageListener(GpsNavigationMessageEvent.Listener paramListener) {}
  
  @Deprecated
  public void removeGpsStatusListener(GpsStatus.Listener paramListener)
  {
    try
    {
      paramListener = (GnssStatusListenerTransport)this.mGpsStatusListeners.remove(paramListener);
      if (paramListener != null) {
        this.mService.unregisterGnssStatusCallback(paramListener);
      }
      return;
    }
    catch (RemoteException paramListener)
    {
      throw paramListener.rethrowFromSystemServer();
    }
  }
  
  public void removeNmeaListener(GnssNmeaListener paramGnssNmeaListener)
  {
    try
    {
      paramGnssNmeaListener = (GnssStatusListenerTransport)this.mOldGnssNmeaListeners.remove(paramGnssNmeaListener);
      if (paramGnssNmeaListener != null) {
        this.mService.unregisterGnssStatusCallback(paramGnssNmeaListener);
      }
      return;
    }
    catch (RemoteException paramGnssNmeaListener)
    {
      throw paramGnssNmeaListener.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void removeNmeaListener(GpsStatus.NmeaListener paramNmeaListener)
  {
    try
    {
      paramNmeaListener = (GnssStatusListenerTransport)this.mGpsNmeaListeners.remove(paramNmeaListener);
      if (paramNmeaListener != null) {
        this.mService.unregisterGnssStatusCallback(paramNmeaListener);
      }
      return;
    }
    catch (RemoteException paramNmeaListener)
    {
      throw paramNmeaListener.rethrowFromSystemServer();
    }
  }
  
  public void removeNmeaListener(OnNmeaMessageListener paramOnNmeaMessageListener)
  {
    try
    {
      paramOnNmeaMessageListener = (GnssStatusListenerTransport)this.mGnssNmeaListeners.remove(paramOnNmeaMessageListener);
      if (paramOnNmeaMessageListener != null) {
        this.mService.unregisterGnssStatusCallback(paramOnNmeaMessageListener);
      }
      return;
    }
    catch (RemoteException paramOnNmeaMessageListener)
    {
      throw paramOnNmeaMessageListener.rethrowFromSystemServer();
    }
  }
  
  public void removeProximityAlert(PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    String str = this.mContext.getPackageName();
    try
    {
      this.mService.removeGeofence(null, paramPendingIntent, str);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public void removeTestProvider(String paramString)
  {
    try
    {
      this.mService.removeTestProvider(paramString, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void removeUpdates(PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    String str = this.mContext.getPackageName();
    try
    {
      this.mService.removeUpdates(null, paramPendingIntent, str);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public void removeUpdates(LocationListener paramLocationListener)
  {
    checkListener(paramLocationListener);
    String str = this.mContext.getPackageName();
    Log.d("LocationManager", "removeUpdates listener=" + paramLocationListener + " packageName=  " + str);
    synchronized (this.mListeners)
    {
      paramLocationListener = (ListenerTransport)this.mListeners.remove(paramLocationListener);
      if (paramLocationListener == null) {
        return;
      }
    }
    try
    {
      this.mService.removeUpdates(paramLocationListener, null, str);
      return;
    }
    catch (RemoteException paramLocationListener)
    {
      throw paramLocationListener.rethrowFromSystemServer();
    }
  }
  
  public void requestLocationUpdates(long paramLong, float paramFloat, Criteria paramCriteria, PendingIntent paramPendingIntent)
  {
    SeempLog.record(47);
    checkCriteria(paramCriteria);
    checkPendingIntent(paramPendingIntent);
    requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(paramCriteria, paramLong, paramFloat, false), null, null, paramPendingIntent);
  }
  
  public void requestLocationUpdates(long paramLong, float paramFloat, Criteria paramCriteria, LocationListener paramLocationListener, Looper paramLooper)
  {
    SeempLog.record(47);
    checkCriteria(paramCriteria);
    checkListener(paramLocationListener);
    requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(paramCriteria, paramLong, paramFloat, false), paramLocationListener, paramLooper, null);
  }
  
  public void requestLocationUpdates(LocationRequest paramLocationRequest, PendingIntent paramPendingIntent)
  {
    SeempLog.record(47);
    checkPendingIntent(paramPendingIntent);
    requestLocationUpdates(paramLocationRequest, null, null, paramPendingIntent);
  }
  
  public void requestLocationUpdates(LocationRequest paramLocationRequest, LocationListener paramLocationListener, Looper paramLooper)
  {
    SeempLog.record(47);
    checkListener(paramLocationListener);
    requestLocationUpdates(paramLocationRequest, paramLocationListener, paramLooper, null);
  }
  
  public void requestLocationUpdates(String paramString, long paramLong, float paramFloat, PendingIntent paramPendingIntent)
  {
    SeempLog.record(47);
    checkProvider(paramString);
    checkPendingIntent(paramPendingIntent);
    requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(paramString, paramLong, paramFloat, false), null, null, paramPendingIntent);
  }
  
  public void requestLocationUpdates(String paramString, long paramLong, float paramFloat, LocationListener paramLocationListener)
  {
    SeempLog.record(47);
    checkProvider(paramString);
    checkListener(paramLocationListener);
    requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(paramString, paramLong, paramFloat, false), paramLocationListener, null, null);
  }
  
  public void requestLocationUpdates(String paramString, long paramLong, float paramFloat, LocationListener paramLocationListener, Looper paramLooper)
  {
    SeempLog.record(47);
    checkProvider(paramString);
    checkListener(paramLocationListener);
    requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(paramString, paramLong, paramFloat, false), paramLocationListener, paramLooper, null);
  }
  
  public void requestSingleUpdate(Criteria paramCriteria, PendingIntent paramPendingIntent)
  {
    SeempLog.record(64);
    checkCriteria(paramCriteria);
    checkPendingIntent(paramPendingIntent);
    requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(paramCriteria, 0L, 0.0F, true), null, null, paramPendingIntent);
  }
  
  public void requestSingleUpdate(Criteria paramCriteria, LocationListener paramLocationListener, Looper paramLooper)
  {
    SeempLog.record(64);
    checkCriteria(paramCriteria);
    checkListener(paramLocationListener);
    requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(paramCriteria, 0L, 0.0F, true), paramLocationListener, paramLooper, null);
  }
  
  public void requestSingleUpdate(String paramString, PendingIntent paramPendingIntent)
  {
    SeempLog.record(64);
    checkProvider(paramString);
    checkPendingIntent(paramPendingIntent);
    requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(paramString, 0L, 0.0F, true), null, null, paramPendingIntent);
  }
  
  public void requestSingleUpdate(String paramString, LocationListener paramLocationListener, Looper paramLooper)
  {
    SeempLog.record(64);
    checkProvider(paramString);
    checkListener(paramLocationListener);
    requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(paramString, 0L, 0.0F, true), paramLocationListener, paramLooper, null);
  }
  
  public boolean sendExtraCommand(String paramString1, String paramString2, Bundle paramBundle)
  {
    SeempLog.record(48);
    try
    {
      boolean bool = this.mService.sendExtraCommand(paramString1, paramString2, paramBundle);
      return bool;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public boolean sendNiResponse(int paramInt1, int paramInt2)
  {
    try
    {
      boolean bool = this.mService.sendNiResponse(paramInt1, paramInt2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setTestProviderEnabled(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setTestProviderEnabled(paramString, paramBoolean, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setTestProviderLocation(String paramString, Location paramLocation)
  {
    IllegalArgumentException localIllegalArgumentException;
    if (!paramLocation.isComplete())
    {
      localIllegalArgumentException = new IllegalArgumentException("Incomplete location object, missing timestamp or accuracy? " + paramLocation);
      if (this.mContext.getApplicationInfo().targetSdkVersion > 16) {
        break label80;
      }
      Log.w("LocationManager", localIllegalArgumentException);
      paramLocation.makeComplete();
    }
    try
    {
      this.mService.setTestProviderLocation(paramString, paramLocation, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      label80:
      throw paramString.rethrowFromSystemServer();
    }
    throw localIllegalArgumentException;
  }
  
  public void setTestProviderStatus(String paramString, int paramInt, Bundle paramBundle, long paramLong)
  {
    try
    {
      this.mService.setTestProviderStatus(paramString, paramInt, paramBundle, paramLong, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void unregisterGnssMeasurementsCallback(GnssMeasurementsEvent.Callback paramCallback)
  {
    this.mGnssMeasurementCallbackTransport.remove(paramCallback);
  }
  
  public void unregisterGnssNavigationMessageCallback(GnssNavigationMessage.Callback paramCallback)
  {
    this.mGnssNavigationMessageCallbackTransport.remove(paramCallback);
  }
  
  public void unregisterGnssNavigationMessageCallback(GnssNavigationMessageEvent.Callback paramCallback)
  {
    this.mGnssNavigationMessageCallbackTransport.remove((GnssNavigationMessage.Callback)this.mNavigationMessageBridge.remove(paramCallback));
  }
  
  public void unregisterGnssStatusCallback(GnssStatus.Callback paramCallback)
  {
    try
    {
      paramCallback = (GnssStatusListenerTransport)this.mGnssStatusListeners.remove(paramCallback);
      if (paramCallback != null) {
        this.mService.unregisterGnssStatusCallback(paramCallback);
      }
      return;
    }
    catch (RemoteException paramCallback)
    {
      throw paramCallback.rethrowFromSystemServer();
    }
  }
  
  public void unregisterGnssStatusCallback(GnssStatusCallback paramGnssStatusCallback)
  {
    try
    {
      paramGnssStatusCallback = (GnssStatusListenerTransport)this.mOldGnssStatusListeners.remove(paramGnssStatusCallback);
      if (paramGnssStatusCallback != null) {
        this.mService.unregisterGnssStatusCallback(paramGnssStatusCallback);
      }
      return;
    }
    catch (RemoteException paramGnssStatusCallback)
    {
      throw paramGnssStatusCallback.rethrowFromSystemServer();
    }
  }
  
  private class GnssStatusListenerTransport
    extends IGnssStatusListener.Stub
  {
    private static final int NMEA_RECEIVED = 1000;
    private final GnssStatus.Callback mGnssCallback;
    private final Handler mGnssHandler;
    private final OnNmeaMessageListener mGnssNmeaListener;
    private final GpsStatus.Listener mGpsListener;
    private final GpsStatus.NmeaListener mGpsNmeaListener;
    private final ArrayList<Nmea> mNmeaBuffer;
    private final GnssStatusCallback mOldGnssCallback;
    private final GnssNmeaListener mOldGnssNmeaListener;
    
    GnssStatusListenerTransport(GnssNmeaListener paramGnssNmeaListener)
    {
      this(paramGnssNmeaListener, null);
    }
    
    GnssStatusListenerTransport(GnssNmeaListener paramGnssNmeaListener, Handler paramHandler)
    {
      this.mGnssCallback = null;
      this.mOldGnssCallback = null;
      this.mGnssHandler = new GnssHandler(paramHandler);
      this.mOldGnssNmeaListener = paramGnssNmeaListener;
      if (this.mOldGnssNmeaListener != null) {}
      for (this$1 = new OnNmeaMessageListener()
          {
            public void onNmeaMessage(String paramAnonymousString, long paramAnonymousLong)
            {
              LocationManager.GnssStatusListenerTransport.-get6(LocationManager.GnssStatusListenerTransport.this).onNmeaReceived(paramAnonymousLong, paramAnonymousString);
            }
          };; this$1 = null)
      {
        this.mGnssNmeaListener = LocationManager.this;
        this.mGpsListener = null;
        this.mGpsNmeaListener = null;
        this.mNmeaBuffer = new ArrayList();
        return;
      }
    }
    
    GnssStatusListenerTransport(GnssStatus.Callback paramCallback)
    {
      this(paramCallback, null);
    }
    
    GnssStatusListenerTransport(GnssStatus.Callback paramCallback, Handler paramHandler)
    {
      this.mOldGnssCallback = null;
      this.mGnssCallback = paramCallback;
      this.mGnssHandler = new GnssHandler(paramHandler);
      this.mOldGnssNmeaListener = null;
      this.mGnssNmeaListener = null;
      this.mNmeaBuffer = null;
      this.mGpsListener = null;
      this.mGpsNmeaListener = null;
    }
    
    GnssStatusListenerTransport(GnssStatusCallback paramGnssStatusCallback)
    {
      this(paramGnssStatusCallback, null);
    }
    
    GnssStatusListenerTransport(GnssStatusCallback paramGnssStatusCallback, Handler paramHandler)
    {
      this.mOldGnssCallback = paramGnssStatusCallback;
      if (this.mOldGnssCallback != null) {}
      for (this$1 = new GnssStatus.Callback()
          {
            public void onFirstFix(int paramAnonymousInt)
            {
              LocationManager.GnssStatusListenerTransport.-get5(LocationManager.GnssStatusListenerTransport.this).onFirstFix(paramAnonymousInt);
            }
            
            public void onSatelliteStatusChanged(GnssStatus paramAnonymousGnssStatus)
            {
              LocationManager.GnssStatusListenerTransport.-get5(LocationManager.GnssStatusListenerTransport.this).onSatelliteStatusChanged(paramAnonymousGnssStatus);
            }
            
            public void onStarted()
            {
              LocationManager.GnssStatusListenerTransport.-get5(LocationManager.GnssStatusListenerTransport.this).onStarted();
            }
            
            public void onStopped()
            {
              LocationManager.GnssStatusListenerTransport.-get5(LocationManager.GnssStatusListenerTransport.this).onStopped();
            }
          };; this$1 = null)
      {
        this.mGnssCallback = LocationManager.this;
        this.mGnssHandler = new GnssHandler(paramHandler);
        this.mOldGnssNmeaListener = null;
        this.mGnssNmeaListener = null;
        this.mNmeaBuffer = null;
        this.mGpsListener = null;
        this.mGpsNmeaListener = null;
        return;
      }
    }
    
    GnssStatusListenerTransport(GpsStatus.Listener paramListener)
    {
      this(paramListener, null);
    }
    
    GnssStatusListenerTransport(GpsStatus.Listener paramListener, Handler paramHandler)
    {
      this.mGpsListener = paramListener;
      this.mGnssHandler = new GnssHandler(paramHandler);
      this.mGpsNmeaListener = null;
      this.mNmeaBuffer = null;
      this.mOldGnssCallback = null;
      if (this.mGpsListener != null) {}
      for (this$1 = new GnssStatus.Callback()
          {
            public void onFirstFix(int paramAnonymousInt)
            {
              LocationManager.GnssStatusListenerTransport.-get2(LocationManager.GnssStatusListenerTransport.this).onGpsStatusChanged(3);
            }
            
            public void onSatelliteStatusChanged(GnssStatus paramAnonymousGnssStatus)
            {
              LocationManager.GnssStatusListenerTransport.-get2(LocationManager.GnssStatusListenerTransport.this).onGpsStatusChanged(4);
            }
            
            public void onStarted()
            {
              LocationManager.GnssStatusListenerTransport.-get2(LocationManager.GnssStatusListenerTransport.this).onGpsStatusChanged(1);
            }
            
            public void onStopped()
            {
              LocationManager.GnssStatusListenerTransport.-get2(LocationManager.GnssStatusListenerTransport.this).onGpsStatusChanged(2);
            }
          };; this$1 = null)
      {
        this.mGnssCallback = LocationManager.this;
        this.mOldGnssNmeaListener = null;
        this.mGnssNmeaListener = null;
        return;
      }
    }
    
    GnssStatusListenerTransport(GpsStatus.NmeaListener paramNmeaListener)
    {
      this(paramNmeaListener, null);
    }
    
    GnssStatusListenerTransport(GpsStatus.NmeaListener paramNmeaListener, Handler paramHandler)
    {
      this.mGpsListener = null;
      this.mGnssHandler = new GnssHandler(paramHandler);
      this.mGpsNmeaListener = paramNmeaListener;
      this.mNmeaBuffer = new ArrayList();
      this.mOldGnssCallback = null;
      this.mGnssCallback = null;
      this.mOldGnssNmeaListener = null;
      this$1 = (LocationManager)localObject;
      if (this.mGpsNmeaListener != null) {
        this$1 = new OnNmeaMessageListener()
        {
          public void onNmeaMessage(String paramAnonymousString, long paramAnonymousLong)
          {
            LocationManager.GnssStatusListenerTransport.-get3(LocationManager.GnssStatusListenerTransport.this).onNmeaReceived(paramAnonymousLong, paramAnonymousString);
          }
        };
      }
      this.mGnssNmeaListener = LocationManager.this;
    }
    
    GnssStatusListenerTransport(OnNmeaMessageListener paramOnNmeaMessageListener)
    {
      this(paramOnNmeaMessageListener, null);
    }
    
    GnssStatusListenerTransport(OnNmeaMessageListener paramOnNmeaMessageListener, Handler paramHandler)
    {
      this.mOldGnssCallback = null;
      this.mGnssCallback = null;
      this.mGnssHandler = new GnssHandler(paramHandler);
      this.mOldGnssNmeaListener = null;
      this.mGnssNmeaListener = paramOnNmeaMessageListener;
      this.mGpsListener = null;
      this.mGpsNmeaListener = null;
      this.mNmeaBuffer = new ArrayList();
    }
    
    public void onFirstFix(int paramInt)
    {
      if (this.mGnssCallback != null)
      {
        LocationManager.-set1(LocationManager.this, paramInt);
        Message localMessage = Message.obtain();
        localMessage.what = 3;
        this.mGnssHandler.sendMessage(localMessage);
      }
    }
    
    public void onGnssStarted()
    {
      if (this.mGnssCallback != null)
      {
        Message localMessage = Message.obtain();
        localMessage.what = 1;
        this.mGnssHandler.sendMessage(localMessage);
      }
    }
    
    public void onGnssStopped()
    {
      if (this.mGnssCallback != null)
      {
        Message localMessage = Message.obtain();
        localMessage.what = 2;
        this.mGnssHandler.sendMessage(localMessage);
      }
    }
    
    public void onNmeaReceived(long paramLong, String paramString)
    {
      if (this.mGnssNmeaListener != null) {}
      synchronized (this.mNmeaBuffer)
      {
        this.mNmeaBuffer.add(new Nmea(paramLong, paramString));
        paramString = Message.obtain();
        paramString.what = 1000;
        this.mGnssHandler.removeMessages(1000);
        this.mGnssHandler.sendMessage(paramString);
        return;
      }
    }
    
    public void onSvStatusChanged(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
    {
      if (this.mGnssCallback != null)
      {
        LocationManager.-set0(LocationManager.this, new GnssStatus(paramInt, paramArrayOfInt, paramArrayOfFloat1, paramArrayOfFloat2, paramArrayOfFloat3));
        paramArrayOfInt = Message.obtain();
        paramArrayOfInt.what = 4;
        this.mGnssHandler.removeMessages(4);
        this.mGnssHandler.sendMessage(paramArrayOfInt);
      }
    }
    
    private class GnssHandler
      extends Handler
    {
      public GnssHandler(Handler paramHandler) {}
      
      public void handleMessage(Message arg1)
      {
        switch (???.what)
        {
        default: 
          return;
        case 1000: 
          synchronized (LocationManager.GnssStatusListenerTransport.-get4(LocationManager.GnssStatusListenerTransport.this))
          {
            int j = LocationManager.GnssStatusListenerTransport.-get4(LocationManager.GnssStatusListenerTransport.this).size();
            int i = 0;
            while (i < j)
            {
              LocationManager.GnssStatusListenerTransport.Nmea localNmea = (LocationManager.GnssStatusListenerTransport.Nmea)LocationManager.GnssStatusListenerTransport.-get4(LocationManager.GnssStatusListenerTransport.this).get(i);
              LocationManager.GnssStatusListenerTransport.-get1(LocationManager.GnssStatusListenerTransport.this).onNmeaMessage(localNmea.mNmea, localNmea.mTimestamp);
              i += 1;
            }
            LocationManager.GnssStatusListenerTransport.-get4(LocationManager.GnssStatusListenerTransport.this).clear();
            return;
          }
        case 1: 
          LocationManager.GnssStatusListenerTransport.-get0(LocationManager.GnssStatusListenerTransport.this).onStarted();
          return;
        case 2: 
          LocationManager.GnssStatusListenerTransport.-get0(LocationManager.GnssStatusListenerTransport.this).onStopped();
          return;
        case 3: 
          LocationManager.GnssStatusListenerTransport.-get0(LocationManager.GnssStatusListenerTransport.this).onFirstFix(LocationManager.-get2(LocationManager.this));
          return;
        }
        LocationManager.GnssStatusListenerTransport.-get0(LocationManager.GnssStatusListenerTransport.this).onSatelliteStatusChanged(LocationManager.-get0(LocationManager.this));
      }
    }
    
    private class Nmea
    {
      String mNmea;
      long mTimestamp;
      
      Nmea(long paramLong, String paramString)
      {
        this.mTimestamp = paramLong;
        this.mNmea = paramString;
      }
    }
  }
  
  private class ListenerTransport
    extends ILocationListener.Stub
  {
    private static final int TYPE_LOCATION_CHANGED = 1;
    private static final int TYPE_PROVIDER_DISABLED = 4;
    private static final int TYPE_PROVIDER_ENABLED = 3;
    private static final int TYPE_STATUS_CHANGED = 2;
    private LocationListener mListener;
    private final Handler mListenerHandler;
    
    ListenerTransport(LocationListener paramLocationListener, Looper paramLooper)
    {
      this.mListener = paramLocationListener;
      if (paramLooper == null)
      {
        this.mListenerHandler = new Handler()
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            LocationManager.ListenerTransport.-wrap0(LocationManager.ListenerTransport.this, paramAnonymousMessage);
          }
        };
        return;
      }
      this.mListenerHandler = new Handler(paramLooper)
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          LocationManager.ListenerTransport.-wrap0(LocationManager.ListenerTransport.this, paramAnonymousMessage);
        }
      };
    }
    
    private void _handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        try
        {
          LocationManager.-get1(LocationManager.this).locationCallbackFinished(this);
          return;
        }
        catch (RemoteException paramMessage)
        {
          Bundle localBundle;
          int i;
          throw paramMessage.rethrowFromSystemServer();
        }
        paramMessage = new Location((Location)paramMessage.obj);
        this.mListener.onLocationChanged(paramMessage);
        continue;
        localBundle = (Bundle)paramMessage.obj;
        paramMessage = localBundle.getString("provider");
        i = localBundle.getInt("status");
        localBundle = localBundle.getBundle("extras");
        this.mListener.onStatusChanged(paramMessage, i, localBundle);
        continue;
        this.mListener.onProviderEnabled((String)paramMessage.obj);
        continue;
        this.mListener.onProviderDisabled((String)paramMessage.obj);
      }
    }
    
    public void onLocationChanged(Location paramLocation)
    {
      Message localMessage = Message.obtain();
      localMessage.what = 1;
      localMessage.obj = paramLocation;
      this.mListenerHandler.sendMessage(localMessage);
    }
    
    public void onProviderDisabled(String paramString)
    {
      Message localMessage = Message.obtain();
      localMessage.what = 4;
      localMessage.obj = paramString;
      this.mListenerHandler.sendMessage(localMessage);
    }
    
    public void onProviderEnabled(String paramString)
    {
      Message localMessage = Message.obtain();
      localMessage.what = 3;
      localMessage.obj = paramString;
      this.mListenerHandler.sendMessage(localMessage);
    }
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
    {
      Message localMessage = Message.obtain();
      localMessage.what = 2;
      Bundle localBundle = new Bundle();
      localBundle.putString("provider", paramString);
      localBundle.putInt("status", paramInt);
      if (paramBundle != null) {
        localBundle.putBundle("extras", paramBundle);
      }
      localMessage.obj = localBundle;
      this.mListenerHandler.sendMessage(localMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/LocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */