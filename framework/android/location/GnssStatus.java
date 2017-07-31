package android.location;

public final class GnssStatus
{
  public static final int CONSTELLATION_BEIDOU = 5;
  public static final int CONSTELLATION_GALILEO = 6;
  public static final int CONSTELLATION_GLONASS = 3;
  public static final int CONSTELLATION_GPS = 1;
  public static final int CONSTELLATION_QZSS = 4;
  public static final int CONSTELLATION_SBAS = 2;
  public static final int CONSTELLATION_TYPE_MASK = 15;
  public static final int CONSTELLATION_TYPE_SHIFT_WIDTH = 3;
  public static final int CONSTELLATION_UNKNOWN = 0;
  public static final int GNSS_SV_FLAGS_HAS_ALMANAC_DATA = 2;
  public static final int GNSS_SV_FLAGS_HAS_EPHEMERIS_DATA = 1;
  public static final int GNSS_SV_FLAGS_NONE = 0;
  public static final int GNSS_SV_FLAGS_USED_IN_FIX = 4;
  public static final int SVID_SHIFT_WIDTH = 7;
  float[] mAzimuths;
  float[] mCn0DbHz;
  float[] mElevations;
  int mSvCount;
  int[] mSvidWithFlags;
  
  GnssStatus(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
  {
    this.mSvCount = paramInt;
    this.mSvidWithFlags = paramArrayOfInt;
    this.mCn0DbHz = paramArrayOfFloat1;
    this.mElevations = paramArrayOfFloat2;
    this.mAzimuths = paramArrayOfFloat3;
  }
  
  public float getAzimuthDegrees(int paramInt)
  {
    return this.mAzimuths[paramInt];
  }
  
  public float getCn0DbHz(int paramInt)
  {
    return this.mCn0DbHz[paramInt];
  }
  
  public int getConstellationType(int paramInt)
  {
    return this.mSvidWithFlags[paramInt] >> 3 & 0xF;
  }
  
  public float getElevationDegrees(int paramInt)
  {
    return this.mElevations[paramInt];
  }
  
  public int getNumSatellites()
  {
    return getSatelliteCount();
  }
  
  public int getSatelliteCount()
  {
    return this.mSvCount;
  }
  
  public int getSvid(int paramInt)
  {
    return this.mSvidWithFlags[paramInt] >> 7;
  }
  
  public boolean hasAlmanac(int paramInt)
  {
    return hasAlmanacData(paramInt);
  }
  
  public boolean hasAlmanacData(int paramInt)
  {
    boolean bool = false;
    if ((this.mSvidWithFlags[paramInt] & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasEphemeris(int paramInt)
  {
    return hasEphemerisData(paramInt);
  }
  
  public boolean hasEphemerisData(int paramInt)
  {
    boolean bool = false;
    if ((this.mSvidWithFlags[paramInt] & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean usedInFix(int paramInt)
  {
    boolean bool = false;
    if ((this.mSvidWithFlags[paramInt] & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static abstract class Callback
  {
    public void onFirstFix(int paramInt) {}
    
    public void onSatelliteStatusChanged(GnssStatus paramGnssStatus) {}
    
    public void onStarted() {}
    
    public void onStopped() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */