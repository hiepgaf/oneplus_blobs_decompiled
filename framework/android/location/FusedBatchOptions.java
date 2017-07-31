package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class FusedBatchOptions
  implements Parcelable
{
  public static final Parcelable.Creator<FusedBatchOptions> CREATOR = new Parcelable.Creator()
  {
    public FusedBatchOptions createFromParcel(Parcel paramAnonymousParcel)
    {
      FusedBatchOptions localFusedBatchOptions = new FusedBatchOptions();
      localFusedBatchOptions.setMaxPowerAllocationInMW(paramAnonymousParcel.readDouble());
      localFusedBatchOptions.setPeriodInNS(paramAnonymousParcel.readLong());
      localFusedBatchOptions.setSourceToUse(paramAnonymousParcel.readInt());
      localFusedBatchOptions.setFlag(paramAnonymousParcel.readInt());
      localFusedBatchOptions.setSmallestDisplacementMeters(paramAnonymousParcel.readFloat());
      return localFusedBatchOptions;
    }
    
    public FusedBatchOptions[] newArray(int paramAnonymousInt)
    {
      return new FusedBatchOptions[paramAnonymousInt];
    }
  };
  private volatile int mFlags = 0;
  private volatile double mMaxPowerAllocationInMW = 0.0D;
  private volatile long mPeriodInNS = 0L;
  private volatile float mSmallestDisplacementMeters = 0.0F;
  private volatile int mSourcesToUse = 0;
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public double getMaxPowerAllocationInMW()
  {
    return this.mMaxPowerAllocationInMW;
  }
  
  public long getPeriodInNS()
  {
    return this.mPeriodInNS;
  }
  
  public float getSmallestDisplacementMeters()
  {
    return this.mSmallestDisplacementMeters;
  }
  
  public int getSourcesToUse()
  {
    return this.mSourcesToUse;
  }
  
  public boolean isFlagSet(int paramInt)
  {
    boolean bool = false;
    if ((this.mFlags & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSourceToUseSet(int paramInt)
  {
    boolean bool = false;
    if ((this.mSourcesToUse & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void resetFlag(int paramInt)
  {
    this.mFlags &= paramInt;
  }
  
  public void resetSourceToUse(int paramInt)
  {
    this.mSourcesToUse &= paramInt;
  }
  
  public void setFlag(int paramInt)
  {
    this.mFlags |= paramInt;
  }
  
  public void setMaxPowerAllocationInMW(double paramDouble)
  {
    this.mMaxPowerAllocationInMW = paramDouble;
  }
  
  public void setPeriodInNS(long paramLong)
  {
    this.mPeriodInNS = paramLong;
  }
  
  public void setSmallestDisplacementMeters(float paramFloat)
  {
    this.mSmallestDisplacementMeters = paramFloat;
  }
  
  public void setSourceToUse(int paramInt)
  {
    this.mSourcesToUse |= paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeDouble(this.mMaxPowerAllocationInMW);
    paramParcel.writeLong(this.mPeriodInNS);
    paramParcel.writeInt(this.mSourcesToUse);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeFloat(this.mSmallestDisplacementMeters);
  }
  
  public static final class BatchFlags
  {
    public static int CALLBACK_ON_LOCATION_FIX = 2;
    public static int WAKEUP_ON_FIFO_FULL = 1;
  }
  
  public static final class SourceTechnologies
  {
    public static int BLUETOOTH = 16;
    public static int CELL;
    public static int GNSS = 1;
    public static int SENSORS;
    public static int WIFI = 2;
    
    static
    {
      SENSORS = 4;
      CELL = 8;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/FusedBatchOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */