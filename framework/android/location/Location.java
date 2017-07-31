package android.location;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.util.Printer;
import android.util.TimeUtils;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Location
  implements Parcelable
{
  public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator()
  {
    public Location createFromParcel(Parcel paramAnonymousParcel)
    {
      Location localLocation = new Location(paramAnonymousParcel.readString());
      Location.-set9(localLocation, paramAnonymousParcel.readLong());
      Location.-set3(localLocation, paramAnonymousParcel.readLong());
      Location.-set5(localLocation, paramAnonymousParcel.readByte());
      Location.-set6(localLocation, paramAnonymousParcel.readDouble());
      Location.-set7(localLocation, paramAnonymousParcel.readDouble());
      Location.-set1(localLocation, paramAnonymousParcel.readDouble());
      Location.-set8(localLocation, paramAnonymousParcel.readFloat());
      Location.-set2(localLocation, paramAnonymousParcel.readFloat());
      Location.-set0(localLocation, paramAnonymousParcel.readFloat());
      Location.-set4(localLocation, Bundle.setDefusable(paramAnonymousParcel.readBundle(), true));
      return localLocation;
    }
    
    public Location[] newArray(int paramAnonymousInt)
    {
      return new Location[paramAnonymousInt];
    }
  };
  public static final String EXTRA_COARSE_LOCATION = "coarseLocation";
  public static final String EXTRA_NO_GPS_LOCATION = "noGPSLocation";
  public static final int FORMAT_DEGREES = 0;
  public static final int FORMAT_MINUTES = 1;
  public static final int FORMAT_SECONDS = 2;
  private static final byte HAS_ACCURACY_MASK = 8;
  private static final byte HAS_ALTITUDE_MASK = 1;
  private static final byte HAS_BEARING_MASK = 4;
  private static final byte HAS_MOCK_PROVIDER_MASK = 16;
  private static final byte HAS_SPEED_MASK = 2;
  private static ThreadLocal<BearingDistanceCache> sBearingDistanceCache = new ThreadLocal()
  {
    protected Location.BearingDistanceCache initialValue()
    {
      return new Location.BearingDistanceCache(null);
    }
  };
  private float mAccuracy = 0.0F;
  private double mAltitude = 0.0D;
  private float mBearing = 0.0F;
  private long mElapsedRealtimeNanos = 0L;
  private Bundle mExtras = null;
  private byte mFieldsMask = 0;
  private double mLatitude = 0.0D;
  private double mLongitude = 0.0D;
  private String mProvider;
  private float mSpeed = 0.0F;
  private long mTime = 0L;
  
  public Location(Location paramLocation)
  {
    set(paramLocation);
  }
  
  public Location(String paramString)
  {
    this.mProvider = paramString;
  }
  
  private static void computeDistanceAndBearing(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, BearingDistanceCache paramBearingDistanceCache)
  {
    double d6 = paramDouble1 * 0.017453292519943295D;
    double d7 = paramDouble3 * 0.017453292519943295D;
    double d8 = paramDouble2 * 0.017453292519943295D;
    double d9 = paramDouble4 * 0.017453292519943295D;
    double d10 = 21384.685800000094D / 6378137.0D;
    double d11 = (4.0680631590769E13D - 4.0408299984087055E13D) / 4.0408299984087055E13D;
    double d4 = d9 - d8;
    double d1 = 0.0D;
    paramDouble2 = Math.atan((1.0D - d10) * Math.tan(d6));
    paramDouble1 = Math.atan((1.0D - d10) * Math.tan(d7));
    double d12 = Math.cos(paramDouble2);
    double d13 = Math.cos(paramDouble1);
    double d14 = Math.sin(paramDouble2);
    double d15 = Math.sin(paramDouble1);
    double d16 = d12 * d13;
    double d17 = d14 * d15;
    paramDouble2 = 0.0D;
    paramDouble3 = 0.0D;
    paramDouble4 = 0.0D;
    paramDouble1 = 0.0D;
    double d2 = d4;
    int i = 0;
    for (;;)
    {
      double d5 = d2;
      double d18;
      double d19;
      if (i < 20)
      {
        paramDouble4 = Math.cos(d5);
        paramDouble1 = Math.sin(d5);
        paramDouble2 = d13 * paramDouble1;
        paramDouble3 = d12 * d15 - d14 * d13 * paramDouble4;
        d18 = Math.sqrt(paramDouble2 * paramDouble2 + paramDouble3 * paramDouble3);
        d19 = d17 + d16 * paramDouble4;
        paramDouble2 = Math.atan2(d18, d19);
        if (d18 != 0.0D) {
          break label612;
        }
        paramDouble3 = 0.0D;
        d2 = 1.0D - paramDouble3 * paramDouble3;
        if (d2 != 0.0D) {
          break label624;
        }
      }
      label612:
      label624:
      for (double d3 = 0.0D;; d3 = d19 - 2.0D * d17 / d2)
      {
        double d20 = d2 * d11;
        d1 = 1.0D + d20 / 16384.0D * (((320.0D - 175.0D * d20) * d20 - 768.0D) * d20 + 4096.0D);
        d20 = d20 / 1024.0D * (((74.0D - 47.0D * d20) * d20 - 128.0D) * d20 + 256.0D);
        double d21 = d10 / 16.0D * d2 * ((4.0D - 3.0D * d2) * d10 + 4.0D);
        d2 = d3 * d3;
        d2 = d20 * d18 * (d20 / 4.0D * ((2.0D * d2 - 1.0D) * d19 - d20 / 6.0D * d3 * (4.0D * d18 * d18 - 3.0D) * (4.0D * d2 - 3.0D)) + d3);
        d3 = d4 + (1.0D - d21) * d10 * paramDouble3 * (d21 * d18 * (d21 * d19 * (2.0D * d3 * d3 - 1.0D) + d3) + paramDouble2);
        if (Math.abs((d3 - d5) / d3) >= 1.0E-12D) {
          break label641;
        }
        paramDouble3 = d2;
        BearingDistanceCache.-set0(paramBearingDistanceCache, (float)(6356752.3142D * d1 * (paramDouble2 - paramDouble3)));
        BearingDistanceCache.-set2(paramBearingDistanceCache, (float)((float)Math.atan2(d13 * paramDouble1, d12 * d15 - d14 * d13 * paramDouble4) * 57.29577951308232D));
        BearingDistanceCache.-set1(paramBearingDistanceCache, (float)((float)Math.atan2(d12 * paramDouble1, -d14 * d13 + d12 * d15 * paramDouble4) * 57.29577951308232D));
        BearingDistanceCache.-set3(paramBearingDistanceCache, d6);
        BearingDistanceCache.-set4(paramBearingDistanceCache, d7);
        BearingDistanceCache.-set5(paramBearingDistanceCache, d8);
        BearingDistanceCache.-set6(paramBearingDistanceCache, d9);
        return;
        paramDouble3 = d16 * paramDouble1 / d18;
        break;
      }
      label641:
      i += 1;
      paramDouble3 = d2;
      d2 = d3;
    }
  }
  
  public static double convert(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("coordinate");
    }
    int j = 0;
    String str1 = paramString;
    if (paramString.charAt(0) == '-')
    {
      str1 = paramString.substring(1);
      j = 1;
    }
    paramString = new StringTokenizer(str1, ":");
    int i = paramString.countTokens();
    if (i < 1) {
      throw new IllegalArgumentException("coordinate=" + str1);
    }
    String str3;
    int m;
    double d2;
    int k;
    try
    {
      String str2 = paramString.nextToken();
      if (i == 1)
      {
        d1 = Double.parseDouble(str2);
        if (j == 0) {
          break label318;
        }
        return -d1;
      }
      str3 = paramString.nextToken();
      m = Integer.parseInt(str2);
      d2 = 0.0D;
      k = 0;
      if (!paramString.hasMoreTokens()) {
        break label221;
      }
      d1 = Integer.parseInt(str3);
      d2 = Double.parseDouble(paramString.nextToken());
      k = 1;
    }
    catch (NumberFormatException paramString)
    {
      throw new IllegalArgumentException("coordinate=" + str1);
    }
    throw new IllegalArgumentException("coordinate=" + str1);
    label221:
    double d1 = Double.parseDouble(str3);
    break label320;
    label230:
    throw new IllegalArgumentException("coordinate=" + str1);
    for (;;)
    {
      label258:
      throw new IllegalArgumentException("coordinate=" + str1);
      label318:
      label320:
      label396:
      label398:
      do
      {
        d2 = (m * 3600.0D + 60.0D * d1 + d2) / 3600.0D;
        d1 = d2;
        if (j != 0) {
          d1 = -d2;
        }
        return d1;
        return d1;
        if ((j != 0) && (m == 180) && (d1 == 0.0D)) {
          if (d2 == 0.0D) {
            i = 1;
          }
        }
        for (;;)
        {
          if ((m < 0.0D) || ((m > 179) && (i == 0))) {
            break label396;
          }
          if (d1 < 0.0D) {
            break;
          }
          if (d1 < 60.0D) {
            break label398;
          }
          break;
          i = 0;
          continue;
          i = 0;
        }
        break label230;
        if ((k != 0) && (d1 > 59.0D)) {
          break;
        }
        if (d2 < 0.0D) {
          break label258;
        }
      } while (d2 < 60.0D);
    }
  }
  
  public static String convert(double paramDouble, int paramInt)
  {
    if ((paramDouble < -180.0D) || (paramDouble > 180.0D)) {}
    while (Double.isNaN(paramDouble)) {
      throw new IllegalArgumentException("coordinate=" + paramDouble);
    }
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("outputType=" + paramInt);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    double d = paramDouble;
    if (paramDouble < 0.0D)
    {
      localStringBuilder.append('-');
      d = -paramDouble;
    }
    DecimalFormat localDecimalFormat = new DecimalFormat("###.#####");
    if (paramInt != 1)
    {
      paramDouble = d;
      if (paramInt != 2) {}
    }
    else
    {
      int i = (int)Math.floor(d);
      localStringBuilder.append(i);
      localStringBuilder.append(':');
      d = (d - i) * 60.0D;
      paramDouble = d;
      if (paramInt == 2)
      {
        paramInt = (int)Math.floor(d);
        localStringBuilder.append(paramInt);
        localStringBuilder.append(':');
        paramDouble = (d - paramInt) * 60.0D;
      }
    }
    localStringBuilder.append(localDecimalFormat.format(paramDouble));
    return localStringBuilder.toString();
  }
  
  public static void distanceBetween(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat == null) || (paramArrayOfFloat.length < 1)) {
      throw new IllegalArgumentException("results is null or has length < 1");
    }
    BearingDistanceCache localBearingDistanceCache = (BearingDistanceCache)sBearingDistanceCache.get();
    computeDistanceAndBearing(paramDouble1, paramDouble2, paramDouble3, paramDouble4, localBearingDistanceCache);
    paramArrayOfFloat[0] = BearingDistanceCache.-get0(localBearingDistanceCache);
    if (paramArrayOfFloat.length > 1)
    {
      paramArrayOfFloat[1] = BearingDistanceCache.-get2(localBearingDistanceCache);
      if (paramArrayOfFloat.length > 2) {
        paramArrayOfFloat[2] = BearingDistanceCache.-get1(localBearingDistanceCache);
      }
    }
  }
  
  public float bearingTo(Location paramLocation)
  {
    BearingDistanceCache localBearingDistanceCache = (BearingDistanceCache)sBearingDistanceCache.get();
    if ((this.mLatitude != BearingDistanceCache.-get3(localBearingDistanceCache)) || (this.mLongitude != BearingDistanceCache.-get5(localBearingDistanceCache))) {}
    for (;;)
    {
      computeDistanceAndBearing(this.mLatitude, this.mLongitude, paramLocation.mLatitude, paramLocation.mLongitude, localBearingDistanceCache);
      do
      {
        return BearingDistanceCache.-get2(localBearingDistanceCache);
        if (paramLocation.mLatitude != BearingDistanceCache.-get4(localBearingDistanceCache)) {
          break;
        }
      } while (paramLocation.mLongitude == BearingDistanceCache.-get6(localBearingDistanceCache));
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public float distanceTo(Location paramLocation)
  {
    BearingDistanceCache localBearingDistanceCache = (BearingDistanceCache)sBearingDistanceCache.get();
    if ((this.mLatitude != BearingDistanceCache.-get3(localBearingDistanceCache)) || (this.mLongitude != BearingDistanceCache.-get5(localBearingDistanceCache))) {}
    for (;;)
    {
      computeDistanceAndBearing(this.mLatitude, this.mLongitude, paramLocation.mLatitude, paramLocation.mLongitude, localBearingDistanceCache);
      do
      {
        return BearingDistanceCache.-get0(localBearingDistanceCache);
        if (paramLocation.mLatitude != BearingDistanceCache.-get4(localBearingDistanceCache)) {
          break;
        }
      } while (paramLocation.mLongitude == BearingDistanceCache.-get6(localBearingDistanceCache));
    }
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + toString());
  }
  
  public float getAccuracy()
  {
    return this.mAccuracy;
  }
  
  public double getAltitude()
  {
    return this.mAltitude;
  }
  
  public float getBearing()
  {
    return this.mBearing;
  }
  
  public long getElapsedRealtimeNanos()
  {
    return this.mElapsedRealtimeNanos;
  }
  
  public Location getExtraLocation(String paramString)
  {
    if (this.mExtras != null)
    {
      paramString = this.mExtras.getParcelable(paramString);
      if ((paramString instanceof Location)) {
        return (Location)paramString;
      }
    }
    return null;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public double getLatitude()
  {
    return this.mLatitude;
  }
  
  public double getLongitude()
  {
    return this.mLongitude;
  }
  
  public String getProvider()
  {
    return this.mProvider;
  }
  
  public float getSpeed()
  {
    return this.mSpeed;
  }
  
  public long getTime()
  {
    return this.mTime;
  }
  
  public boolean hasAccuracy()
  {
    boolean bool = false;
    if ((this.mFieldsMask & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasAltitude()
  {
    boolean bool = false;
    if ((this.mFieldsMask & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasBearing()
  {
    boolean bool = false;
    if ((this.mFieldsMask & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasSpeed()
  {
    boolean bool = false;
    if ((this.mFieldsMask & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isComplete()
  {
    if (this.mProvider == null) {
      return false;
    }
    if (!hasAccuracy()) {
      return false;
    }
    if (this.mTime == 0L) {
      return false;
    }
    return this.mElapsedRealtimeNanos != 0L;
  }
  
  public boolean isFromMockProvider()
  {
    boolean bool = false;
    if ((this.mFieldsMask & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void makeComplete()
  {
    if (this.mProvider == null) {
      this.mProvider = "?";
    }
    if (!hasAccuracy())
    {
      this.mFieldsMask = ((byte)(this.mFieldsMask | 0x8));
      this.mAccuracy = 100.0F;
    }
    if (this.mTime == 0L) {
      this.mTime = System.currentTimeMillis();
    }
    if (this.mElapsedRealtimeNanos == 0L) {
      this.mElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
    }
  }
  
  public void removeAccuracy()
  {
    this.mAccuracy = 0.0F;
    this.mFieldsMask = ((byte)(this.mFieldsMask & 0xFFFFFFF7));
  }
  
  public void removeAltitude()
  {
    this.mAltitude = 0.0D;
    this.mFieldsMask = ((byte)(this.mFieldsMask & 0xFFFFFFFE));
  }
  
  public void removeBearing()
  {
    this.mBearing = 0.0F;
    this.mFieldsMask = ((byte)(this.mFieldsMask & 0xFFFFFFFB));
  }
  
  public void removeSpeed()
  {
    this.mSpeed = 0.0F;
    this.mFieldsMask = ((byte)(this.mFieldsMask & 0xFFFFFFFD));
  }
  
  public void reset()
  {
    this.mProvider = null;
    this.mTime = 0L;
    this.mElapsedRealtimeNanos = 0L;
    this.mFieldsMask = 0;
    this.mLatitude = 0.0D;
    this.mLongitude = 0.0D;
    this.mAltitude = 0.0D;
    this.mSpeed = 0.0F;
    this.mBearing = 0.0F;
    this.mAccuracy = 0.0F;
    this.mExtras = null;
  }
  
  public void set(Location paramLocation)
  {
    Object localObject = null;
    this.mProvider = paramLocation.mProvider;
    this.mTime = paramLocation.mTime;
    this.mElapsedRealtimeNanos = paramLocation.mElapsedRealtimeNanos;
    this.mFieldsMask = paramLocation.mFieldsMask;
    this.mLatitude = paramLocation.mLatitude;
    this.mLongitude = paramLocation.mLongitude;
    this.mAltitude = paramLocation.mAltitude;
    this.mSpeed = paramLocation.mSpeed;
    this.mBearing = paramLocation.mBearing;
    this.mAccuracy = paramLocation.mAccuracy;
    if (paramLocation.mExtras == null) {}
    for (paramLocation = (Location)localObject;; paramLocation = new Bundle(paramLocation.mExtras))
    {
      this.mExtras = paramLocation;
      return;
    }
  }
  
  public void setAccuracy(float paramFloat)
  {
    this.mAccuracy = paramFloat;
    this.mFieldsMask = ((byte)(this.mFieldsMask | 0x8));
  }
  
  public void setAltitude(double paramDouble)
  {
    this.mAltitude = paramDouble;
    this.mFieldsMask = ((byte)(this.mFieldsMask | 0x1));
  }
  
  public void setBearing(float paramFloat)
  {
    float f;
    for (;;)
    {
      f = paramFloat;
      if (paramFloat >= 0.0F) {
        break;
      }
      paramFloat += 360.0F;
    }
    while (f >= 360.0F) {
      f -= 360.0F;
    }
    this.mBearing = f;
    this.mFieldsMask = ((byte)(this.mFieldsMask | 0x4));
  }
  
  public void setElapsedRealtimeNanos(long paramLong)
  {
    this.mElapsedRealtimeNanos = paramLong;
  }
  
  public void setExtraLocation(String paramString, Location paramLocation)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putParcelable(paramString, paramLocation);
  }
  
  public void setExtras(Bundle paramBundle)
  {
    Object localObject = null;
    if (paramBundle == null) {}
    for (paramBundle = (Bundle)localObject;; paramBundle = new Bundle(paramBundle))
    {
      this.mExtras = paramBundle;
      return;
    }
  }
  
  public void setIsFromMockProvider(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mFieldsMask = ((byte)(this.mFieldsMask | 0x10));
      return;
    }
    this.mFieldsMask = ((byte)(this.mFieldsMask & 0xFFFFFFEF));
  }
  
  public void setLatitude(double paramDouble)
  {
    this.mLatitude = paramDouble;
  }
  
  public void setLongitude(double paramDouble)
  {
    this.mLongitude = paramDouble;
  }
  
  public void setProvider(String paramString)
  {
    this.mProvider = paramString;
  }
  
  public void setSpeed(float paramFloat)
  {
    this.mSpeed = paramFloat;
    this.mFieldsMask = ((byte)(this.mFieldsMask | 0x2));
  }
  
  public void setTime(long paramLong)
  {
    this.mTime = paramLong;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Location[");
    localStringBuilder.append(this.mProvider);
    localStringBuilder.append(String.format(" %.6f,%.6f", new Object[] { Double.valueOf(this.mLatitude), Double.valueOf(this.mLongitude) }));
    if (hasAccuracy())
    {
      localStringBuilder.append(String.format(" acc=%.0f", new Object[] { Float.valueOf(this.mAccuracy) }));
      if (this.mTime == 0L) {
        localStringBuilder.append(" t=?!?");
      }
      if (this.mElapsedRealtimeNanos != 0L) {
        break label257;
      }
      localStringBuilder.append(" et=?!?");
    }
    for (;;)
    {
      if (hasAltitude()) {
        localStringBuilder.append(" alt=").append(this.mAltitude);
      }
      if (hasSpeed()) {
        localStringBuilder.append(" vel=").append(this.mSpeed);
      }
      if (hasBearing()) {
        localStringBuilder.append(" bear=").append(this.mBearing);
      }
      if (isFromMockProvider()) {
        localStringBuilder.append(" mock");
      }
      if (this.mExtras != null) {
        localStringBuilder.append(" {").append(this.mExtras).append('}');
      }
      localStringBuilder.append(']');
      return localStringBuilder.toString();
      localStringBuilder.append(" acc=???");
      break;
      label257:
      localStringBuilder.append(" et=");
      TimeUtils.formatDuration(this.mElapsedRealtimeNanos / 1000000L, localStringBuilder);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mProvider);
    paramParcel.writeLong(this.mTime);
    paramParcel.writeLong(this.mElapsedRealtimeNanos);
    paramParcel.writeByte(this.mFieldsMask);
    paramParcel.writeDouble(this.mLatitude);
    paramParcel.writeDouble(this.mLongitude);
    paramParcel.writeDouble(this.mAltitude);
    paramParcel.writeFloat(this.mSpeed);
    paramParcel.writeFloat(this.mBearing);
    paramParcel.writeFloat(this.mAccuracy);
    paramParcel.writeBundle(this.mExtras);
  }
  
  private static class BearingDistanceCache
  {
    private float mDistance = 0.0F;
    private float mFinalBearing = 0.0F;
    private float mInitialBearing = 0.0F;
    private double mLat1 = 0.0D;
    private double mLat2 = 0.0D;
    private double mLon1 = 0.0D;
    private double mLon2 = 0.0D;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Location.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */