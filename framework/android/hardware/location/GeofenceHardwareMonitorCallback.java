package android.hardware.location;

import android.location.Location;

public abstract class GeofenceHardwareMonitorCallback
{
  @Deprecated
  public void onMonitoringSystemChange(int paramInt, boolean paramBoolean, Location paramLocation) {}
  
  public void onMonitoringSystemChange(GeofenceHardwareMonitorEvent paramGeofenceHardwareMonitorEvent) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareMonitorCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */