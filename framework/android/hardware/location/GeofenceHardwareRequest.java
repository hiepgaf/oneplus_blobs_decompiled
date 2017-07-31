package android.hardware.location;

public final class GeofenceHardwareRequest
{
  static final int GEOFENCE_TYPE_CIRCLE = 0;
  private int mLastTransition = 4;
  private double mLatitude;
  private double mLongitude;
  private int mMonitorTransitions = 7;
  private int mNotificationResponsiveness = 5000;
  private double mRadius;
  private int mSourceTechnologies = 1;
  private int mType;
  private int mUnknownTimer = 30000;
  
  public static GeofenceHardwareRequest createCircularGeofence(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    GeofenceHardwareRequest localGeofenceHardwareRequest = new GeofenceHardwareRequest();
    localGeofenceHardwareRequest.setCircularGeofence(paramDouble1, paramDouble2, paramDouble3);
    return localGeofenceHardwareRequest;
  }
  
  private void setCircularGeofence(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.mLatitude = paramDouble1;
    this.mLongitude = paramDouble2;
    this.mRadius = paramDouble3;
    this.mType = 0;
  }
  
  public int getLastTransition()
  {
    return this.mLastTransition;
  }
  
  public double getLatitude()
  {
    return this.mLatitude;
  }
  
  public double getLongitude()
  {
    return this.mLongitude;
  }
  
  public int getMonitorTransitions()
  {
    return this.mMonitorTransitions;
  }
  
  public int getNotificationResponsiveness()
  {
    return this.mNotificationResponsiveness;
  }
  
  public double getRadius()
  {
    return this.mRadius;
  }
  
  public int getSourceTechnologies()
  {
    return this.mSourceTechnologies;
  }
  
  int getType()
  {
    return this.mType;
  }
  
  public int getUnknownTimer()
  {
    return this.mUnknownTimer;
  }
  
  public void setLastTransition(int paramInt)
  {
    this.mLastTransition = paramInt;
  }
  
  public void setMonitorTransitions(int paramInt)
  {
    this.mMonitorTransitions = paramInt;
  }
  
  public void setNotificationResponsiveness(int paramInt)
  {
    this.mNotificationResponsiveness = paramInt;
  }
  
  public void setSourceTechnologies(int paramInt)
  {
    paramInt &= 0x1F;
    if (paramInt == 0) {
      throw new IllegalArgumentException("At least one valid source technology must be set.");
    }
    this.mSourceTechnologies = paramInt;
  }
  
  public void setUnknownTimer(int paramInt)
  {
    this.mUnknownTimer = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */