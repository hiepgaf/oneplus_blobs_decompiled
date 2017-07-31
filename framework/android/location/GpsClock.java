package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GpsClock
  implements Parcelable
{
  public static final Parcelable.Creator<GpsClock> CREATOR = new Parcelable.Creator()
  {
    public GpsClock createFromParcel(Parcel paramAnonymousParcel)
    {
      GpsClock localGpsClock = new GpsClock();
      GpsClock.-set4(localGpsClock, (short)paramAnonymousParcel.readInt());
      GpsClock.-set6(localGpsClock, (short)paramAnonymousParcel.readInt());
      GpsClock.-set9(localGpsClock, paramAnonymousParcel.readByte());
      GpsClock.-set7(localGpsClock, paramAnonymousParcel.readLong());
      GpsClock.-set8(localGpsClock, paramAnonymousParcel.readDouble());
      GpsClock.-set5(localGpsClock, paramAnonymousParcel.readLong());
      GpsClock.-set0(localGpsClock, paramAnonymousParcel.readDouble());
      GpsClock.-set1(localGpsClock, paramAnonymousParcel.readDouble());
      GpsClock.-set2(localGpsClock, paramAnonymousParcel.readDouble());
      GpsClock.-set3(localGpsClock, paramAnonymousParcel.readDouble());
      return localGpsClock;
    }
    
    public GpsClock[] newArray(int paramAnonymousInt)
    {
      return new GpsClock[paramAnonymousInt];
    }
  };
  private static final short HAS_BIAS = 8;
  private static final short HAS_BIAS_UNCERTAINTY = 16;
  private static final short HAS_DRIFT = 32;
  private static final short HAS_DRIFT_UNCERTAINTY = 64;
  private static final short HAS_FULL_BIAS = 4;
  private static final short HAS_LEAP_SECOND = 1;
  private static final short HAS_NO_FLAGS = 0;
  private static final short HAS_TIME_UNCERTAINTY = 2;
  public static final byte TYPE_GPS_TIME = 2;
  public static final byte TYPE_LOCAL_HW_TIME = 1;
  public static final byte TYPE_UNKNOWN = 0;
  private double mBiasInNs;
  private double mBiasUncertaintyInNs;
  private double mDriftInNsPerSec;
  private double mDriftUncertaintyInNsPerSec;
  private short mFlags;
  private long mFullBiasInNs;
  private short mLeapSecond;
  private long mTimeInNs;
  private double mTimeUncertaintyInNs;
  private byte mType;
  
  GpsClock()
  {
    initialize();
  }
  
  private String getTypeString()
  {
    switch (this.mType)
    {
    default: 
      return "<Invalid:" + this.mType + ">";
    case 0: 
      return "Unknown";
    case 2: 
      return "GpsTime";
    }
    return "LocalHwClock";
  }
  
  private void initialize()
  {
    this.mFlags = 0;
    resetLeapSecond();
    setType((byte)0);
    setTimeInNs(Long.MIN_VALUE);
    resetTimeUncertaintyInNs();
    resetFullBiasInNs();
    resetBiasInNs();
    resetBiasUncertaintyInNs();
    resetDriftInNsPerSec();
    resetDriftUncertaintyInNsPerSec();
  }
  
  private boolean isFlagSet(short paramShort)
  {
    return (this.mFlags & paramShort) == paramShort;
  }
  
  private void resetFlag(short paramShort)
  {
    this.mFlags = ((short)(this.mFlags & paramShort));
  }
  
  private void setFlag(short paramShort)
  {
    this.mFlags = ((short)(this.mFlags | paramShort));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public double getBiasInNs()
  {
    return this.mBiasInNs;
  }
  
  public double getBiasUncertaintyInNs()
  {
    return this.mBiasUncertaintyInNs;
  }
  
  public double getDriftInNsPerSec()
  {
    return this.mDriftInNsPerSec;
  }
  
  public double getDriftUncertaintyInNsPerSec()
  {
    return this.mDriftUncertaintyInNsPerSec;
  }
  
  public long getFullBiasInNs()
  {
    return this.mFullBiasInNs;
  }
  
  public short getLeapSecond()
  {
    return this.mLeapSecond;
  }
  
  public long getTimeInNs()
  {
    return this.mTimeInNs;
  }
  
  public double getTimeUncertaintyInNs()
  {
    return this.mTimeUncertaintyInNs;
  }
  
  public byte getType()
  {
    return this.mType;
  }
  
  public boolean hasBiasInNs()
  {
    return isFlagSet((short)8);
  }
  
  public boolean hasBiasUncertaintyInNs()
  {
    return isFlagSet((short)16);
  }
  
  public boolean hasDriftInNsPerSec()
  {
    return isFlagSet((short)32);
  }
  
  public boolean hasDriftUncertaintyInNsPerSec()
  {
    return isFlagSet((short)64);
  }
  
  public boolean hasFullBiasInNs()
  {
    return isFlagSet((short)4);
  }
  
  public boolean hasLeapSecond()
  {
    return isFlagSet((short)1);
  }
  
  public boolean hasTimeUncertaintyInNs()
  {
    return isFlagSet((short)2);
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void resetBiasInNs()
  {
    resetFlag((short)8);
    this.mBiasInNs = NaN.0D;
  }
  
  public void resetBiasUncertaintyInNs()
  {
    resetFlag((short)16);
    this.mBiasUncertaintyInNs = NaN.0D;
  }
  
  public void resetDriftInNsPerSec()
  {
    resetFlag((short)32);
    this.mDriftInNsPerSec = NaN.0D;
  }
  
  public void resetDriftUncertaintyInNsPerSec()
  {
    resetFlag((short)64);
    this.mDriftUncertaintyInNsPerSec = NaN.0D;
  }
  
  public void resetFullBiasInNs()
  {
    resetFlag((short)4);
    this.mFullBiasInNs = Long.MIN_VALUE;
  }
  
  public void resetLeapSecond()
  {
    resetFlag((short)1);
    this.mLeapSecond = Short.MIN_VALUE;
  }
  
  public void resetTimeUncertaintyInNs()
  {
    resetFlag((short)2);
    this.mTimeUncertaintyInNs = NaN.0D;
  }
  
  public void set(GpsClock paramGpsClock)
  {
    this.mFlags = paramGpsClock.mFlags;
    this.mLeapSecond = paramGpsClock.mLeapSecond;
    this.mType = paramGpsClock.mType;
    this.mTimeInNs = paramGpsClock.mTimeInNs;
    this.mTimeUncertaintyInNs = paramGpsClock.mTimeUncertaintyInNs;
    this.mFullBiasInNs = paramGpsClock.mFullBiasInNs;
    this.mBiasInNs = paramGpsClock.mBiasInNs;
    this.mBiasUncertaintyInNs = paramGpsClock.mBiasUncertaintyInNs;
    this.mDriftInNsPerSec = paramGpsClock.mDriftInNsPerSec;
    this.mDriftUncertaintyInNsPerSec = paramGpsClock.mDriftUncertaintyInNsPerSec;
  }
  
  public void setBiasInNs(double paramDouble)
  {
    setFlag((short)8);
    this.mBiasInNs = paramDouble;
  }
  
  public void setBiasUncertaintyInNs(double paramDouble)
  {
    setFlag((short)16);
    this.mBiasUncertaintyInNs = paramDouble;
  }
  
  public void setDriftInNsPerSec(double paramDouble)
  {
    setFlag((short)32);
    this.mDriftInNsPerSec = paramDouble;
  }
  
  public void setDriftUncertaintyInNsPerSec(double paramDouble)
  {
    setFlag((short)64);
    this.mDriftUncertaintyInNsPerSec = paramDouble;
  }
  
  public void setFullBiasInNs(long paramLong)
  {
    setFlag((short)4);
    this.mFullBiasInNs = paramLong;
  }
  
  public void setLeapSecond(short paramShort)
  {
    setFlag((short)1);
    this.mLeapSecond = paramShort;
  }
  
  public void setTimeInNs(long paramLong)
  {
    this.mTimeInNs = paramLong;
  }
  
  public void setTimeUncertaintyInNs(double paramDouble)
  {
    setFlag((short)2);
    this.mTimeUncertaintyInNs = paramDouble;
  }
  
  public void setType(byte paramByte)
  {
    this.mType = paramByte;
  }
  
  public String toString()
  {
    Object localObject3 = null;
    StringBuilder localStringBuilder = new StringBuilder("GpsClock:\n");
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Type", getTypeString() }));
    label100:
    label151:
    label190:
    Object localObject2;
    if (hasLeapSecond())
    {
      localObject1 = Short.valueOf(this.mLeapSecond);
      localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "LeapSecond", localObject1 }));
      long l = this.mTimeInNs;
      if (!hasTimeUncertaintyInNs()) {
        break label322;
      }
      localObject1 = Double.valueOf(this.mTimeUncertaintyInNs);
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "TimeInNs", Long.valueOf(l), "TimeUncertaintyInNs", localObject1 }));
      if (!hasFullBiasInNs()) {
        break label327;
      }
      localObject1 = Long.valueOf(this.mFullBiasInNs);
      localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "FullBiasInNs", localObject1 }));
      if (!hasBiasInNs()) {
        break label332;
      }
      localObject1 = Double.valueOf(this.mBiasInNs);
      if (!hasBiasUncertaintyInNs()) {
        break label337;
      }
      localObject2 = Double.valueOf(this.mBiasUncertaintyInNs);
      label206:
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "BiasInNs", localObject1, "BiasUncertaintyInNs", localObject2 }));
      if (!hasDriftInNsPerSec()) {
        break label343;
      }
    }
    label322:
    label327:
    label332:
    label337:
    label343:
    for (Object localObject1 = Double.valueOf(this.mDriftInNsPerSec);; localObject1 = null)
    {
      localObject2 = localObject3;
      if (hasDriftUncertaintyInNsPerSec()) {
        localObject2 = Double.valueOf(this.mDriftUncertaintyInNsPerSec);
      }
      localStringBuilder.append(String.format("   %-15s = %-25s   %-26s = %s\n", new Object[] { "DriftInNsPerSec", localObject1, "DriftUncertaintyInNsPerSec", localObject2 }));
      return localStringBuilder.toString();
      localObject1 = null;
      break;
      localObject1 = null;
      break label100;
      localObject1 = null;
      break label151;
      localObject1 = null;
      break label190;
      localObject2 = null;
      break label206;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeInt(this.mLeapSecond);
    paramParcel.writeByte(this.mType);
    paramParcel.writeLong(this.mTimeInNs);
    paramParcel.writeDouble(this.mTimeUncertaintyInNs);
    paramParcel.writeLong(this.mFullBiasInNs);
    paramParcel.writeDouble(this.mBiasInNs);
    paramParcel.writeDouble(this.mBiasUncertaintyInNs);
    paramParcel.writeDouble(this.mDriftInNsPerSec);
    paramParcel.writeDouble(this.mDriftUncertaintyInNsPerSec);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GpsClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */