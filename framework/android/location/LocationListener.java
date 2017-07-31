package android.location;

import android.os.Bundle;

public abstract interface LocationListener
{
  public abstract void onLocationChanged(Location paramLocation);
  
  public abstract void onProviderDisabled(String paramString);
  
  public abstract void onProviderEnabled(String paramString);
  
  public abstract void onStatusChanged(String paramString, int paramInt, Bundle paramBundle);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/LocationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */