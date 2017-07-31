package android.hardware.location;

import android.location.Location;

public abstract class GeofenceHardwareCallback
{
  public void onGeofenceAdd(int paramInt1, int paramInt2) {}
  
  public void onGeofencePause(int paramInt1, int paramInt2) {}
  
  public void onGeofenceRemove(int paramInt1, int paramInt2) {}
  
  public void onGeofenceResume(int paramInt1, int paramInt2) {}
  
  public void onGeofenceTransition(int paramInt1, int paramInt2, Location paramLocation, long paramLong, int paramInt3) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */