package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class GnssClock
  implements Parcelable
{
  public static final Parcelable.Creator<GnssClock> CREATOR = new Parcelable.Creator()
  {
    public GnssClock createFromParcel(Parcel paramAnonymousParcel)
    {
      GnssClock localGnssClock = new GnssClock();
      GnssClock.-set4(localGnssClock, paramAnonymousParcel.readInt());
      GnssClock.-set7(localGnssClock, paramAnonymousParcel.readInt());
      GnssClock.-set8(localGnssClock, paramAnonymousParcel.readLong());
      GnssClock.-set9(localGnssClock, paramAnonymousParcel.readDouble());
      GnssClock.-set5(localGnssClock, paramAnonymousParcel.readLong());
      GnssClock.-set0(localGnssClock, paramAnonymousParcel.readDouble());
      GnssClock.-set1(localGnssClock, paramAnonymousParcel.readDouble());
      GnssClock.-set2(localGnssClock, paramAnonymousParcel.readDouble());
      GnssClock.-set3(localGnssClock, paramAnonymousParcel.readDouble());
      GnssClock.-set6(localGnssClock, paramAnonymousParcel.readInt());
      return localGnssClock;
    }
    
    public GnssClock[] newArray(int paramAnonymousInt)
    {
      return new GnssClock[paramAnonymousInt];
    }
  };
  private static final int HAS_BIAS = 8;
  private static final int HAS_BIAS_UNCERTAINTY = 16;
  private static final int HAS_DRIFT = 32;
  private static final int HAS_DRIFT_UNCERTAINTY = 64;
  private static final int HAS_FULL_BIAS = 4;
  private static final int HAS_LEAP_SECOND = 1;
  private static final int HAS_NO_FLAGS = 0;
  private static final int HAS_TIME_UNCERTAINTY = 2;
  private double mBiasNanos;
  private double mBiasUncertaintyNanos;
  private double mDriftNanosPerSecond;
  private double mDriftUncertaintyNanosPerSecond;
  private int mFlags;
  private long mFullBiasNanos;
  private int mHardwareClockDiscontinuityCount;
  private int mLeapSecond;
  private long mTimeNanos;
  private double mTimeUncertaintyNanos;
  
  public GnssClock()
  {
    initialize();
  }
  
  private void initialize()
  {
    this.mFlags = 0;
    resetLeapSecond();
    setTimeNanos(Long.MIN_VALUE);
    resetTimeUncertaintyNanos();
    resetFullBiasNanos();
    resetBiasNanos();
    resetBiasUncertaintyNanos();
    resetDriftNanosPerSecond();
    resetDriftUncertaintyNanosPerSecond();
    setHardwareClockDiscontinuityCount(Integer.MIN_VALUE);
  }
  
  private boolean isFlagSet(int paramInt)
  {
    return (this.mFlags & paramInt) == paramInt;
  }
  
  private void resetFlag(int paramInt)
  {
    this.mFlags &= paramInt;
  }
  
  private void setFlag(int paramInt)
  {
    this.mFlags |= paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public double getBiasNanos()
  {
    return this.mBiasNanos;
  }
  
  public double getBiasUncertaintyNanos()
  {
    return this.mBiasUncertaintyNanos;
  }
  
  public double getDriftNanosPerSecond()
  {
    return this.mDriftNanosPerSecond;
  }
  
  public double getDriftUncertaintyNanosPerSecond()
  {
    return this.mDriftUncertaintyNanosPerSecond;
  }
  
  public long getFullBiasNanos()
  {
    return this.mFullBiasNanos;
  }
  
  public int getHardwareClockDiscontinuityCount()
  {
    return this.mHardwareClockDiscontinuityCount;
  }
  
  public int getLeapSecond()
  {
    return this.mLeapSecond;
  }
  
  public long getTimeNanos()
  {
    return this.mTimeNanos;
  }
  
  public double getTimeUncertaintyNanos()
  {
    return this.mTimeUncertaintyNanos;
  }
  
  public boolean hasBiasNanos()
  {
    return isFlagSet(8);
  }
  
  public boolean hasBiasUncertaintyNanos()
  {
    return isFlagSet(16);
  }
  
  public boolean hasDriftNanosPerSecond()
  {
    return isFlagSet(32);
  }
  
  public boolean hasDriftUncertaintyNanosPerSecond()
  {
    return isFlagSet(64);
  }
  
  public boolean hasFullBiasNanos()
  {
    return isFlagSet(4);
  }
  
  public boolean hasLeapSecond()
  {
    return isFlagSet(1);
  }
  
  public boolean hasTimeUncertaintyNanos()
  {
    return isFlagSet(2);
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void resetBiasNanos()
  {
    resetFlag(8);
    this.mBiasNanos = NaN.0D;
  }
  
  public void resetBiasUncertaintyNanos()
  {
    resetFlag(16);
    this.mBiasUncertaintyNanos = NaN.0D;
  }
  
  public void resetDriftNanosPerSecond()
  {
    resetFlag(32);
    this.mDriftNanosPerSecond = NaN.0D;
  }
  
  public void resetDriftUncertaintyNanosPerSecond()
  {
    resetFlag(64);
    this.mDriftUncertaintyNanosPerSecond = NaN.0D;
  }
  
  public void resetFullBiasNanos()
  {
    resetFlag(4);
    this.mFullBiasNanos = Long.MIN_VALUE;
  }
  
  public void resetLeapSecond()
  {
    resetFlag(1);
    this.mLeapSecond = Integer.MIN_VALUE;
  }
  
  public void resetTimeUncertaintyNanos()
  {
    resetFlag(2);
    this.mTimeUncertaintyNanos = NaN.0D;
  }
  
  public void set(GnssClock paramGnssClock)
  {
    this.mFlags = paramGnssClock.mFlags;
    this.mLeapSecond = paramGnssClock.mLeapSecond;
    this.mTimeNanos = paramGnssClock.mTimeNanos;
    this.mTimeUncertaintyNanos = paramGnssClock.mTimeUncertaintyNanos;
    this.mFullBiasNanos = paramGnssClock.mFullBiasNanos;
    this.mBiasNanos = paramGnssClock.mBiasNanos;
    this.mBiasUncertaintyNanos = paramGnssClock.mBiasUncertaintyNanos;
    this.mDriftNanosPerSecond = paramGnssClock.mDriftNanosPerSecond;
    this.mDriftUncertaintyNanosPerSecond = paramGnssClock.mDriftUncertaintyNanosPerSecond;
    this.mHardwareClockDiscontinuityCount = paramGnssClock.mHardwareClockDiscontinuityCount;
  }
  
  public void setBiasNanos(double paramDouble)
  {
    setFlag(8);
    this.mBiasNanos = paramDouble;
  }
  
  public void setBiasUncertaintyNanos(double paramDouble)
  {
    setFlag(16);
    this.mBiasUncertaintyNanos = paramDouble;
  }
  
  public void setDriftNanosPerSecond(double paramDouble)
  {
    setFlag(32);
    this.mDriftNanosPerSecond = paramDouble;
  }
  
  public void setDriftUncertaintyNanosPerSecond(double paramDouble)
  {
    setFlag(64);
    this.mDriftUncertaintyNanosPerSecond = paramDouble;
  }
  
  public void setFullBiasNanos(long paramLong)
  {
    setFlag(4);
    this.mFullBiasNanos = paramLong;
  }
  
  public void setHardwareClockDiscontinuityCount(int paramInt)
  {
    this.mHardwareClockDiscontinuityCount = paramInt;
  }
  
  public void setLeapSecond(int paramInt)
  {
    setFlag(1);
    this.mLeapSecond = paramInt;
  }
  
  public void setTimeNanos(long paramLong)
  {
    this.mTimeNanos = paramLong;
  }
  
  public void setTimeUncertaintyNanos(double paramDouble)
  {
    setFlag(2);
    this.mTimeUncertaintyNanos = paramDouble;
  }
  
  public String toString()
  {
    Object localObject3 = null;
    StringBuilder localStringBuilder = new StringBuilder("GnssClock:\n");
    label73:
    label124:
    label163:
    Object localObject2;
    if (hasLeapSecond())
    {
      localObject1 = Integer.valueOf(this.mLeapSecond);
      localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "LeapSecond", localObject1 }));
      long l = this.mTimeNanos;
      if (!hasTimeUncertaintyNanos()) {
        break label323;
      }
      localObject1 = Double.valueOf(this.mTimeUncertaintyNanos);
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "TimeNanos", Long.valueOf(l), "TimeUncertaintyNanos", localObject1 }));
      if (!hasFullBiasNanos()) {
        break label328;
      }
      localObject1 = Long.valueOf(this.mFullBiasNanos);
      localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "FullBiasNanos", localObject1 }));
      if (!hasBiasNanos()) {
        break label333;
      }
      localObject1 = Double.valueOf(this.mBiasNanos);
      if (!hasBiasUncertaintyNanos()) {
        break label338;
      }
      localObject2 = Double.valueOf(this.mBiasUncertaintyNanos);
      label179:
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "BiasNanos", localObject1, "BiasUncertaintyNanos", localObject2 }));
      if (!hasDriftNanosPerSecond()) {
        break label344;
      }
    }
    label323:
    label328:
    label333:
    label338:
    label344:
    for (Object localObject1 = Double.valueOf(this.mDriftNanosPerSecond);; localObject1 = null)
    {
      localObject2 = localObject3;
      if (hasDriftUncertaintyNanosPerSecond()) {
        localObject2 = Double.valueOf(this.mDriftUncertaintyNanosPerSecond);
      }
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "DriftNanosPerSecond", localObject1, "DriftUncertaintyNanosPerSecond", localObject2 }));
      localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "HardwareClockDiscontinuityCount", Integer.valueOf(this.mHardwareClockDiscontinuityCount) }));
      return localStringBuilder.toString();
      localObject1 = null;
      break;
      localObject1 = null;
      break label73;
      localObject1 = null;
      break label124;
      localObject1 = null;
      break label163;
      localObject2 = null;
      break label179;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeInt(this.mLeapSecond);
    paramParcel.writeLong(this.mTimeNanos);
    paramParcel.writeDouble(this.mTimeUncertaintyNanos);
    paramParcel.writeLong(this.mFullBiasNanos);
    paramParcel.writeDouble(this.mBiasNanos);
    paramParcel.writeDouble(this.mBiasUncertaintyNanos);
    paramParcel.writeDouble(this.mDriftNanosPerSecond);
    paramParcel.writeDouble(this.mDriftUncertaintyNanosPerSecond);
    paramParcel.writeInt(this.mHardwareClockDiscontinuityCount);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */