package android.hardware.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.os.Binder;
import android.os.IBinder;

public class GeofenceHardwareService
  extends Service
{
  private IBinder mBinder = new IGeofenceHardware.Stub()
  {
    public boolean addCircularFence(int paramAnonymousInt, GeofenceHardwareRequestParcelable paramAnonymousGeofenceHardwareRequestParcelable, IGeofenceHardwareCallback paramAnonymousIGeofenceHardwareCallback)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).addCircularFence(paramAnonymousInt, paramAnonymousGeofenceHardwareRequestParcelable, paramAnonymousIGeofenceHardwareCallback);
    }
    
    public int[] getMonitoringTypes()
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).getMonitoringTypes();
    }
    
    public int getStatusOfMonitoringType(int paramAnonymousInt)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).getStatusOfMonitoringType(paramAnonymousInt);
    }
    
    public boolean pauseGeofence(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt2);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).pauseGeofence(paramAnonymousInt1, paramAnonymousInt2);
    }
    
    public boolean registerForMonitorStateChangeCallback(int paramAnonymousInt, IGeofenceHardwareMonitorCallback paramAnonymousIGeofenceHardwareMonitorCallback)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).registerForMonitorStateChangeCallback(paramAnonymousInt, paramAnonymousIGeofenceHardwareMonitorCallback);
    }
    
    public boolean removeGeofence(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt2);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).removeGeofence(paramAnonymousInt1, paramAnonymousInt2);
    }
    
    public boolean resumeGeofence(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt2);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).resumeGeofence(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
    }
    
    public void setFusedGeofenceHardware(IFusedGeofenceHardware paramAnonymousIFusedGeofenceHardware)
    {
      GeofenceHardwareService.-get1(GeofenceHardwareService.this).setFusedGeofenceHardware(paramAnonymousIFusedGeofenceHardware);
    }
    
    public void setGpsGeofenceHardware(IGpsGeofenceHardware paramAnonymousIGpsGeofenceHardware)
    {
      GeofenceHardwareService.-get1(GeofenceHardwareService.this).setGpsHardwareGeofence(paramAnonymousIGpsGeofenceHardware);
    }
    
    public boolean unregisterForMonitorStateChangeCallback(int paramAnonymousInt, IGeofenceHardwareMonitorCallback paramAnonymousIGeofenceHardwareMonitorCallback)
    {
      GeofenceHardwareService.-get0(GeofenceHardwareService.this).enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware geofence");
      GeofenceHardwareService.-wrap0(GeofenceHardwareService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt);
      return GeofenceHardwareService.-get1(GeofenceHardwareService.this).unregisterForMonitorStateChangeCallback(paramAnonymousInt, paramAnonymousIGeofenceHardwareMonitorCallback);
    }
  };
  private Context mContext;
  private GeofenceHardwareImpl mGeofenceHardwareImpl;
  
  private void checkPermission(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mGeofenceHardwareImpl.getAllowedResolutionLevel(paramInt1, paramInt2) < this.mGeofenceHardwareImpl.getMonitoringResolutionLevel(paramInt3)) {
      throw new SecurityException("Insufficient permissions to access hardware geofence for type: " + paramInt3);
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  public void onCreate()
  {
    this.mContext = this;
    this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
  }
  
  public void onDestroy()
  {
    this.mGeofenceHardwareImpl = null;
  }
  
  public boolean onUnbind(Intent paramIntent)
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */