package android.location;

@Deprecated
public final class GpsSatellite
{
  float mAzimuth;
  float mElevation;
  boolean mHasAlmanac;
  boolean mHasEphemeris;
  int mPrn;
  float mSnr;
  boolean mUsedInFix;
  boolean mValid;
  
  GpsSatellite(int paramInt)
  {
    this.mPrn = paramInt;
  }
  
  public float getAzimuth()
  {
    return this.mAzimuth;
  }
  
  public float getElevation()
  {
    return this.mElevation;
  }
  
  public int getPrn()
  {
    return this.mPrn;
  }
  
  public float getSnr()
  {
    return this.mSnr;
  }
  
  public boolean hasAlmanac()
  {
    return this.mHasAlmanac;
  }
  
  public boolean hasEphemeris()
  {
    return this.mHasEphemeris;
  }
  
  void setStatus(GpsSatellite paramGpsSatellite)
  {
    if (paramGpsSatellite == null)
    {
      this.mValid = false;
      return;
    }
    this.mValid = paramGpsSatellite.mValid;
    this.mHasEphemeris = paramGpsSatellite.mHasEphemeris;
    this.mHasAlmanac = paramGpsSatellite.mHasAlmanac;
    this.mUsedInFix = paramGpsSatellite.mUsedInFix;
    this.mSnr = paramGpsSatellite.mSnr;
    this.mElevation = paramGpsSatellite.mElevation;
    this.mAzimuth = paramGpsSatellite.mAzimuth;
  }
  
  public boolean usedInFix()
  {
    return this.mUsedInFix;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GpsSatellite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */