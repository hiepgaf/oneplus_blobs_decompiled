package android.location;

import android.util.SparseArray;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Deprecated
public final class GpsStatus
{
  private static final int BEIDOU_SVID_OFFSET = 200;
  private static final int GALILEO_SVID_OFFSET = 300;
  private static final int GLONASS_SVID_OFFSET = 64;
  public static final int GPS_EVENT_FIRST_FIX = 3;
  public static final int GPS_EVENT_SATELLITE_STATUS = 4;
  public static final int GPS_EVENT_STARTED = 1;
  public static final int GPS_EVENT_STOPPED = 2;
  private static final int NUM_SATELLITES = 340;
  private static final int SBAS_SVID_OFFSET = -87;
  private Iterable<GpsSatellite> mSatelliteList = new Iterable()
  {
    public Iterator<GpsSatellite> iterator()
    {
      return new GpsStatus.SatelliteIterator(GpsStatus.this);
    }
  };
  private final SparseArray<GpsSatellite> mSatellites = new SparseArray();
  private int mTimeToFirstFix;
  
  private void clearSatellites()
  {
    int j = this.mSatellites.size();
    int i = 0;
    while (i < j)
    {
      ((GpsSatellite)this.mSatellites.valueAt(i)).mValid = false;
      i += 1;
    }
  }
  
  private void setStatus(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
  {
    clearSatellites();
    int j = 0;
    if (j < paramInt)
    {
      int m = paramArrayOfInt[j] >> 3 & 0xF;
      int k = paramArrayOfInt[j] >> 7;
      int i;
      label46:
      GpsSatellite localGpsSatellite1;
      if (m == 3)
      {
        i = k + 64;
        if ((i > 0) && (i <= 340))
        {
          GpsSatellite localGpsSatellite2 = (GpsSatellite)this.mSatellites.get(i);
          localGpsSatellite1 = localGpsSatellite2;
          if (localGpsSatellite2 == null)
          {
            localGpsSatellite1 = new GpsSatellite(i);
            this.mSatellites.put(i, localGpsSatellite1);
          }
          localGpsSatellite1.mValid = true;
          localGpsSatellite1.mSnr = paramArrayOfFloat1[j];
          localGpsSatellite1.mElevation = paramArrayOfFloat2[j];
          localGpsSatellite1.mAzimuth = paramArrayOfFloat3[j];
          if ((paramArrayOfInt[j] & 0x1) == 0) {
            break label279;
          }
          bool = true;
          label151:
          localGpsSatellite1.mHasEphemeris = bool;
          if ((paramArrayOfInt[j] & 0x2) == 0) {
            break label285;
          }
          bool = true;
          label170:
          localGpsSatellite1.mHasAlmanac = bool;
          if ((paramArrayOfInt[j] & 0x4) == 0) {
            break label291;
          }
        }
      }
      label279:
      label285:
      label291:
      for (boolean bool = true;; bool = false)
      {
        localGpsSatellite1.mUsedInFix = bool;
        for (;;)
        {
          j += 1;
          break;
          if (m == 5)
          {
            i = k + 200;
            break label46;
          }
          if (m == 2)
          {
            i = k - 87;
            break label46;
          }
          if (m == 6)
          {
            i = k + 300;
            break label46;
          }
          i = k;
          if (m == 1) {
            break label46;
          }
          i = k;
          if (m == 4) {
            break label46;
          }
        }
        bool = false;
        break label151;
        bool = false;
        break label170;
      }
    }
  }
  
  public int getMaxSatellites()
  {
    return 340;
  }
  
  public Iterable<GpsSatellite> getSatellites()
  {
    return this.mSatelliteList;
  }
  
  public int getTimeToFirstFix()
  {
    return this.mTimeToFirstFix;
  }
  
  void setStatus(GnssStatus paramGnssStatus, int paramInt)
  {
    this.mTimeToFirstFix = paramInt;
    setStatus(paramGnssStatus.mSvCount, paramGnssStatus.mSvidWithFlags, paramGnssStatus.mCn0DbHz, paramGnssStatus.mElevations, paramGnssStatus.mAzimuths);
  }
  
  void setTimeToFirstFix(int paramInt)
  {
    this.mTimeToFirstFix = paramInt;
  }
  
  @Deprecated
  public static abstract interface Listener
  {
    public abstract void onGpsStatusChanged(int paramInt);
  }
  
  @Deprecated
  public static abstract interface NmeaListener
  {
    public abstract void onNmeaReceived(long paramLong, String paramString);
  }
  
  private final class SatelliteIterator
    implements Iterator<GpsSatellite>
  {
    private int mIndex = 0;
    private final int mSatellitesCount = GpsStatus.-get0(GpsStatus.this).size();
    
    SatelliteIterator() {}
    
    public boolean hasNext()
    {
      while (this.mIndex < this.mSatellitesCount)
      {
        if (((GpsSatellite)GpsStatus.-get0(GpsStatus.this).valueAt(this.mIndex)).mValid) {
          return true;
        }
        this.mIndex += 1;
      }
      return false;
    }
    
    public GpsSatellite next()
    {
      while (this.mIndex < this.mSatellitesCount)
      {
        GpsSatellite localGpsSatellite = (GpsSatellite)GpsStatus.-get0(GpsStatus.this).valueAt(this.mIndex);
        this.mIndex += 1;
        if (localGpsSatellite.mValid) {
          return localGpsSatellite;
        }
      }
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GpsStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */