package com.oneplus.camera.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.core.AMapLocException;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.CoordinateConverter.CoordType;
import com.amap.api.maps2d.model.LatLng;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Device;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Log;
import com.oneplus.base.PermissionEventArgs;
import com.oneplus.base.PermissionManager;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.base.SettingsValueChangedEventArgs;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraApplication;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.OperationState;
import com.oneplus.camera.VideoCaptureState;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class UILocationManagerImpl
  extends CameraComponent
  implements LocationManager
{
  private static final float MAX_ACCURACY_TOLERANCE = 3000.0F;
  private static final int MAX_TIME_TOLERANCE = 120000;
  protected static final long MIN_GPS_TIME_TOLERANCE = -1000L;
  private static final String[] PERMISSION_LIST = { "android.permission.ACCESS_FINE_LOCATION" };
  private static final boolean USE_AMAP = Device.isHydrogenOS();
  private CoordinateConverter m_CoordinateConvert;
  private boolean m_IsMonitoringSysLocationMode;
  private LocationListener m_LocationListener = new LocationListener()
  {
    public void onLocationChanged(Location paramAnonymousLocation)
    {
      Log.d(UILocationManagerImpl.-get0(UILocationManagerImpl.this), "onLocationChanged() - ", new Object[] { paramAnonymousLocation, ", time: ", Long.valueOf(paramAnonymousLocation.getTime()), " ms, elapsed: ", Long.valueOf(paramAnonymousLocation.getElapsedRealtimeNanos()), " ns" });
      if ((paramAnonymousLocation != null) && (paramAnonymousLocation.getAccuracy() <= 3000.0F) && (paramAnonymousLocation.getAccuracy() > 0.0F))
      {
        if (!"gps".equals(paramAnonymousLocation.getProvider()))
        {
          paramAnonymousLocation.setTime(System.currentTimeMillis());
          paramAnonymousLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        UILocationManagerImpl.-wrap3(UILocationManagerImpl.this, paramAnonymousLocation);
      }
    }
    
    public void onProviderDisabled(String paramAnonymousString) {}
    
    public void onProviderEnabled(String paramAnonymousString) {}
    
    public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
  };
  private LocationManagerProxy m_LocationManagerProxy;
  private boolean m_NeedToRequestPermissions = true;
  private AMapLocationListener m_OPAMapLocationListener = new AMapLocationListener()
  {
    public void onLocationChanged(Location paramAnonymousLocation) {}
    
    public void onLocationChanged(AMapLocation paramAnonymousAMapLocation)
    {
      if (paramAnonymousAMapLocation == null) {
        return;
      }
      if (paramAnonymousAMapLocation.getAMapException().getErrorCode() != 0)
      {
        Log.d(UILocationManagerImpl.-get0(UILocationManagerImpl.this), "[AMAP] onLocationChanged() - Error: " + paramAnonymousAMapLocation.getAMapException().getErrorCode() + ", " + paramAnonymousAMapLocation.getAMapException());
        return;
      }
      paramAnonymousAMapLocation.setTime(System.currentTimeMillis());
      paramAnonymousAMapLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
      Log.d(UILocationManagerImpl.-get0(UILocationManagerImpl.this), "[AMAP] onLocationChanged() - ", new Object[] { paramAnonymousAMapLocation, ", time: ", Long.valueOf(paramAnonymousAMapLocation.getTime()), " ms, elapsed: ", Long.valueOf(paramAnonymousAMapLocation.getElapsedRealtimeNanos()), " ns" });
      if ((paramAnonymousAMapLocation.getAccuracy() <= 3000.0F) && (paramAnonymousAMapLocation.getAccuracy() > 0.0F)) {
        UILocationManagerImpl.-wrap3(UILocationManagerImpl.this, paramAnonymousAMapLocation);
      }
    }
    
    public void onProviderDisabled(String paramAnonymousString) {}
    
    public void onProviderEnabled(String paramAnonymousString) {}
    
    public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
  };
  private final EventHandler<PermissionEventArgs> m_PermissionDeniedEventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<PermissionEventArgs> paramAnonymousEventKey, PermissionEventArgs paramAnonymousPermissionEventArgs)
    {
      UILocationManagerImpl.-wrap4(UILocationManagerImpl.this, paramAnonymousPermissionEventArgs.getPermission(), -1);
    }
  };
  private final EventHandler<PermissionEventArgs> m_PermissionGrantedEventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<PermissionEventArgs> paramAnonymousEventKey, PermissionEventArgs paramAnonymousPermissionEventArgs)
    {
      UILocationManagerImpl.-wrap4(UILocationManagerImpl.this, paramAnonymousPermissionEventArgs.getPermission(), 0);
    }
  };
  private PermissionManager m_PermissionManager;
  private boolean m_PermissionsGranted;
  private boolean m_PrevLocationSetting;
  private Hashtable<String, Integer> m_RequestPermissionResults = new Hashtable();
  private android.location.LocationManager m_SysLocationManager;
  private int m_SysLocationMode;
  private final BroadcastReceiver m_SysLocationModeChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      UILocationManagerImpl.-wrap5(UILocationManagerImpl.this, Settings.Secure.getInt(UILocationManagerImpl.this.getContext().getContentResolver(), "location_mode", 0));
    }
  };
  
  UILocationManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Location Manager", paramCameraActivity, false);
  }
  
  private void checkPermissions()
  {
    this.m_PermissionsGranted = true;
    CameraActivity localCameraActivity = getCameraActivity();
    int i = PERMISSION_LIST.length - 1;
    while (i >= 0)
    {
      if (!localCameraActivity.isPermissionGranted(PERMISSION_LIST[i]))
      {
        Log.w(this.TAG, "checkPermissions() - Permission " + PERMISSION_LIST[i] + " not granted");
        this.m_PermissionsGranted = false;
      }
      i -= 1;
    }
  }
  
  private void checkSystemLocationMode()
  {
    onSystemLocationModeChanged(Settings.Secure.getInt(getContext().getContentResolver(), "location_mode", 0));
    if (!this.m_IsMonitoringSysLocationMode)
    {
      IntentFilter localIntentFilter = new IntentFilter("android.location.MODE_CHANGED");
      getContext().registerReceiver(this.m_SysLocationModeChangedReceiver, localIntentFilter);
      this.m_IsMonitoringSysLocationMode = true;
    }
  }
  
  private boolean isBetterLocation(Location paramLocation1, Location paramLocation2)
  {
    if (paramLocation1 == null) {
      return false;
    }
    if ((paramLocation1 != null) && (paramLocation1.getAccuracy() > 3000.0F)) {
      return false;
    }
    if ((paramLocation1.getLatitude() == 0.0D) && (paramLocation1.getLongitude() == 0.0D)) {
      return false;
    }
    if (paramLocation2 == null) {
      return true;
    }
    if ((paramLocation1.getLatitude() == paramLocation2.getLatitude()) && (paramLocation1.getLongitude() == paramLocation2.getLongitude()) && (paramLocation1.getAccuracy() == paramLocation2.getAccuracy())) {
      return false;
    }
    long l = paramLocation1.getTime() - paramLocation2.getTime();
    if (l > 120000L)
    {
      j = 1;
      if (l >= -120000L) {
        break label143;
      }
      k = 1;
      label121:
      if (l <= 0L) {
        break label149;
      }
    }
    label143:
    label149:
    for (int i = 1;; i = 0)
    {
      if (j == 0) {
        break label154;
      }
      return true;
      j = 0;
      break;
      k = 0;
      break label121;
    }
    label154:
    if (k != 0) {
      return false;
    }
    int j = 0;
    if (paramLocation1.getProvider().equals(paramLocation2.getProvider())) {
      j = 1;
    }
    if ((int)(paramLocation1.getAccuracy() - paramLocation2.getAccuracy()) < 0) {}
    for (int k = 1; k != 0; k = 0) {
      return true;
    }
    if ((i == 0) || (k != 0))
    {
      if ((i != 0) && (j != 0)) {
        return true;
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  private void onLocationChanged(Location paramLocation)
  {
    if (this.m_SysLocationMode == 0)
    {
      Log.w(this.TAG, "onLocationChanged() - System location mode is OFF");
      return;
    }
    if (USE_AMAP) {
      if (!"lbs".equals(paramLocation.getProvider()))
      {
        if (this.m_CoordinateConvert == null) {
          this.m_CoordinateConvert = new CoordinateConverter();
        }
        this.m_CoordinateConvert.from(CoordinateConverter.CoordType.GPS);
        this.m_CoordinateConvert.coord(new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude()));
        localLatLng = this.m_CoordinateConvert.convert();
        paramLocation.setLatitude(localLatLng.latitude);
        paramLocation.setLongitude(localLatLng.longitude);
      }
    }
    while (!"lbs".equals(paramLocation.getProvider()))
    {
      LatLng localLatLng;
      if (isBetterLocation(paramLocation, (Location)get(PROP_LOCATION)))
      {
        Log.v(this.TAG, "onLocationChanged() - Use better, ", new Object[] { paramLocation, ", time: ", Long.valueOf(paramLocation.getTime()), " ms, elapsed: ", Long.valueOf(paramLocation.getElapsedRealtimeNanos()), " ns" });
        if (paramLocation.getElapsedRealtimeNanos() == 0L) {
          paramLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        setReadOnly(PROP_LOCATION, paramLocation);
      }
      return;
    }
    Log.v(this.TAG, "O2 don't use AMAP Location Service");
  }
  
  private void onPermissionResult(String paramString, int paramInt)
  {
    this.m_RequestPermissionResults.put(paramString, Integer.valueOf(paramInt));
    paramString = PERMISSION_LIST;
    int i = paramString.length;
    paramInt = 0;
    Object localObject;
    while (paramInt < i)
    {
      localObject = paramString[paramInt];
      if (!this.m_RequestPermissionResults.keySet().contains(localObject))
      {
        Log.v(this.TAG, "onPermissionResult() - Permission has not completed yet");
        return;
      }
      paramInt += 1;
    }
    this.m_PermissionsGranted = true;
    paramString = this.m_RequestPermissionResults.keySet().iterator();
    while (paramString.hasNext())
    {
      localObject = (String)paramString.next();
      if (((Integer)this.m_RequestPermissionResults.get(localObject)).intValue() == -1)
      {
        Log.w(this.TAG, "onPermissionResult() - Permission denied: " + (String)localObject);
        this.m_PermissionsGranted = false;
      }
    }
    if (this.m_PermissionsGranted) {
      startLocationListeners();
    }
    for (;;)
    {
      this.m_RequestPermissionResults.clear();
      this.m_PermissionManager.removeHandler(PermissionManager.EVENT_PERMISSION_GRANTED, this.m_PermissionGrantedEventHandler);
      this.m_PermissionManager.removeHandler(PermissionManager.EVENT_PERMISSION_DENIED, this.m_PermissionDeniedEventHandler);
      return;
      getSettings().set("Location.Save", Boolean.valueOf(false));
    }
  }
  
  private void onSystemLocationModeChanged(int paramInt)
  {
    Log.v(this.TAG, "onSystemLocationModeChanged() - Location mode : ", Integer.valueOf(paramInt));
    this.m_SysLocationMode = paramInt;
    if (paramInt == 0) {
      setReadOnly(PROP_LOCATION, null);
    }
  }
  
  private void requestPermissions()
  {
    if (getCameraActivity().isSecureMode())
    {
      Log.v(this.TAG, "requestPermissions() - Secure mode don't request location permission.");
      return;
    }
    if ((!this.m_PermissionsGranted) && (this.m_NeedToRequestPermissions))
    {
      if (this.m_PermissionManager == null)
      {
        this.m_PermissionManager = ((PermissionManager)CameraApplication.current().findComponent(PermissionManager.class));
        if (this.m_PermissionManager == null) {
          Log.w(this.TAG, "requestPermissions() - Cannot find permission manager");
        }
      }
    }
    else {
      return;
    }
    this.m_NeedToRequestPermissions = false;
    ArrayList localArrayList = new ArrayList();
    int i = PERMISSION_LIST.length - 1;
    while (i >= 0)
    {
      if (!getCameraActivity().isPermissionGranted(PERMISSION_LIST[i])) {
        localArrayList.add(PERMISSION_LIST[i]);
      }
      i -= 1;
    }
    if (localArrayList.isEmpty())
    {
      this.m_PermissionsGranted = true;
      return;
    }
    this.m_PermissionManager.addHandler(PermissionManager.EVENT_PERMISSION_GRANTED, this.m_PermissionGrantedEventHandler);
    this.m_PermissionManager.addHandler(PermissionManager.EVENT_PERMISSION_DENIED, this.m_PermissionDeniedEventHandler);
    this.m_PermissionManager.requestPermissions(getCameraActivity(), (String[])localArrayList.toArray(new String[0]), 0);
  }
  
  private void startLocationListeners()
  {
    if (((Boolean)get(PROP_IS_LOCATION_LISTENER_STARTED)).booleanValue()) {
      return;
    }
    if (!getSettings().getBoolean("Location.Save"))
    {
      setReadOnly(PROP_LOCATION, null);
      return;
    }
    if (!this.m_PermissionsGranted) {
      if (this.m_NeedToRequestPermissions)
      {
        requestPermissions();
        if (!this.m_PermissionsGranted) {
          Log.w(this.TAG, "startLocationListeners() - Waiting for permissions request");
        }
      }
      else
      {
        Log.w(this.TAG, "startLocationListeners() - Permissions denied");
        return;
      }
    }
    Object localObject3 = getCameraActivity();
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)localObject3.get(CameraActivity.PROP_STATE)).ordinal()])
    {
    default: 
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)localObject3.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      return;
    }
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (USE_AMAP)
    {
      if (this.m_LocationManagerProxy == null)
      {
        this.m_LocationManagerProxy = LocationManagerProxy.getInstance(getContext());
        this.m_LocationManagerProxy.setGpsEnable(false);
      }
      localObject1 = localObject2;
      if (this.m_LocationManagerProxy.isProviderEnabled("lbs"))
      {
        Log.v(this.TAG, "[AMAP] startLocationListeners() - Use provider: ", "lbs");
        this.m_LocationManagerProxy.requestLocationData("lbs", 10000L, 15.0F, this.m_OPAMapLocationListener);
        localObject2 = this.m_LocationManagerProxy.getLastKnownLocation("lbs");
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          ((Location)localObject2).setTime(System.currentTimeMillis());
          ((Location)localObject2).setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
          localObject1 = localObject2;
        }
      }
    }
    if (this.m_SysLocationManager == null) {
      this.m_SysLocationManager = ((android.location.LocationManager)((CameraActivity)localObject3).getSystemService("location"));
    }
    Iterator localIterator = this.m_SysLocationManager.getProviders(true).iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (String)localIterator.next();
      int i = 0;
      long l;
      if (((String)localObject2).equals("passive"))
      {
        i = 1;
        l = 600000L;
      }
      for (;;)
      {
        Log.v(this.TAG, "startLocationListeners() - Use provider: ", localObject2);
        this.m_SysLocationManager.requestLocationUpdates((String)localObject2, l, 0.0F, this.m_LocationListener);
        localObject3 = this.m_SysLocationManager.getLastKnownLocation((String)localObject2);
        if (localObject3 == null) {
          break;
        }
        if ((i == 0) || (((Location)localObject3).getAccuracy() <= 3000.0F)) {
          break label488;
        }
        Log.d(this.TAG, "startLocationListeners() - Passive location is out of acceptable accuracy");
        break;
        if (((String)localObject2).equals("network"))
        {
          l = 10000L;
        }
        else
        {
          if (!((String)localObject2).equals("gps")) {
            break;
          }
          l = 10000L;
        }
      }
      label488:
      if (isBetterLocation((Location)localObject3, (Location)localObject1))
      {
        localObject2 = localObject3;
        localObject1 = localObject2;
        if (!"gps".equals(((Location)localObject3).getProvider()))
        {
          ((Location)localObject3).setTime(System.currentTimeMillis());
          ((Location)localObject3).setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
          localObject1 = localObject2;
        }
      }
    }
    if (localObject1 != null)
    {
      Log.v(this.TAG, "startLocationListeners() - Use last known, ", new Object[] { localObject1, ", time: ", Long.valueOf(((Location)localObject1).getTime()), " ms, elapsed: ", Long.valueOf(((Location)localObject1).getElapsedRealtimeNanos()), " ns" });
      setReadOnly(PROP_LOCATION, localObject1);
    }
    for (;;)
    {
      setReadOnly(PROP_IS_LOCATION_LISTENER_STARTED, Boolean.valueOf(true));
      return;
      Log.v(this.TAG, "startLocationListeners() - No best location");
    }
  }
  
  private void stopLocationListeners()
  {
    if (this.m_SysLocationManager == null) {
      return;
    }
    if (!((Boolean)get(PROP_IS_LOCATION_LISTENER_STARTED)).booleanValue()) {
      return;
    }
    Log.v(this.TAG, "stopLocationListeners()");
    this.m_SysLocationManager.removeUpdates(this.m_LocationListener);
    if (this.m_LocationManagerProxy != null)
    {
      this.m_LocationManagerProxy.removeUpdates(this.m_OPAMapLocationListener);
      this.m_LocationManagerProxy.destroy();
      this.m_LocationManagerProxy = null;
    }
    setReadOnly(PROP_IS_LOCATION_LISTENER_STARTED, Boolean.valueOf(false));
  }
  
  protected void onDeinitialize()
  {
    stopLocationListeners();
    if (this.m_IsMonitoringSysLocationMode)
    {
      this.m_IsMonitoringSysLocationMode = false;
      getContext().unregisterReceiver(this.m_SysLocationModeChangedReceiver);
    }
    this.m_SysLocationManager = null;
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    checkPermissions();
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == OperationState.STARTED) {
          UILocationManagerImpl.-wrap6(UILocationManagerImpl.this);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        boolean bool = false;
        if (paramAnonymousPropertyChangeEventArgs.getOldValue() == BaseActivity.State.RUNNING)
        {
          UILocationManagerImpl.-wrap7(UILocationManagerImpl.this);
          if (UILocationManagerImpl.-get1(UILocationManagerImpl.this))
          {
            UILocationManagerImpl.-set0(UILocationManagerImpl.this, false);
            UILocationManagerImpl.this.getContext().unregisterReceiver(UILocationManagerImpl.-get3(UILocationManagerImpl.this));
          }
        }
        label135:
        do
        {
          return;
          if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING)
          {
            if (UILocationManagerImpl.-get2(UILocationManagerImpl.this) != UILocationManagerImpl.-wrap0(UILocationManagerImpl.this).getBoolean("Location.Save"))
            {
              paramAnonymousPropertySource = UILocationManagerImpl.this;
              if (!UILocationManagerImpl.-get2(UILocationManagerImpl.this)) {
                break label135;
              }
            }
            for (;;)
            {
              UILocationManagerImpl.-set2(paramAnonymousPropertySource, bool);
              UILocationManagerImpl.-set1(UILocationManagerImpl.this, true);
              UILocationManagerImpl.-wrap1(UILocationManagerImpl.this);
              UILocationManagerImpl.-wrap2(UILocationManagerImpl.this);
              return;
              bool = true;
            }
          }
        } while (paramAnonymousPropertyChangeEventArgs.getNewValue() != BaseActivity.State.NEW_INTENT);
        UILocationManagerImpl.-set1(UILocationManagerImpl.this, true);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        default: 
          UILocationManagerImpl.-wrap7(UILocationManagerImpl.this);
        }
      }
    });
    getSettings().addHandler(Settings.EVENT_VALUE_CHANGED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<SettingsValueChangedEventArgs> paramAnonymousEventKey, SettingsValueChangedEventArgs paramAnonymousSettingsValueChangedEventArgs)
      {
        if ("Location.Save".equals(paramAnonymousSettingsValueChangedEventArgs.getKey()))
        {
          if (UILocationManagerImpl.-wrap0(UILocationManagerImpl.this).getBoolean("Location.Save")) {
            UILocationManagerImpl.-wrap6(UILocationManagerImpl.this);
          }
        }
        else {
          return;
        }
        UILocationManagerImpl.-wrap7(UILocationManagerImpl.this);
      }
    });
    checkSystemLocationMode();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/location/UILocationManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */