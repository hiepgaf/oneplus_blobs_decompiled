package android.location;

public abstract class GnssStatusCallback
{
  public void onFirstFix(int paramInt) {}
  
  public void onSatelliteStatusChanged(GnssStatus paramGnssStatus) {}
  
  public void onStarted() {}
  
  public void onStopped() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssStatusCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */