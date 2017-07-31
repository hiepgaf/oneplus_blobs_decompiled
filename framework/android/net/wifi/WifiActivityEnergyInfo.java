package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class WifiActivityEnergyInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WifiActivityEnergyInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiActivityEnergyInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WifiActivityEnergyInfo(paramAnonymousParcel.readLong(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readLong(), paramAnonymousParcel.createLongArray(), paramAnonymousParcel.readLong(), paramAnonymousParcel.readLong(), paramAnonymousParcel.readLong());
    }
    
    public WifiActivityEnergyInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiActivityEnergyInfo[paramAnonymousInt];
    }
  };
  public static final int STACK_STATE_INVALID = 0;
  public static final int STACK_STATE_STATE_ACTIVE = 1;
  public static final int STACK_STATE_STATE_IDLE = 3;
  public static final int STACK_STATE_STATE_SCANNING = 2;
  public long mControllerEnergyUsed;
  public long mControllerIdleTimeMs;
  public long mControllerRxTimeMs;
  public long mControllerTxTimeMs;
  public long[] mControllerTxTimePerLevelMs;
  public int mStackState;
  public long mTimestamp;
  
  public WifiActivityEnergyInfo(long paramLong1, int paramInt, long paramLong2, long[] paramArrayOfLong, long paramLong3, long paramLong4, long paramLong5)
  {
    this.mTimestamp = paramLong1;
    this.mStackState = paramInt;
    this.mControllerTxTimeMs = paramLong2;
    this.mControllerTxTimePerLevelMs = paramArrayOfLong;
    this.mControllerRxTimeMs = paramLong3;
    this.mControllerIdleTimeMs = paramLong4;
    this.mControllerEnergyUsed = paramLong5;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getControllerEnergyUsed()
  {
    return this.mControllerEnergyUsed;
  }
  
  public long getControllerIdleTimeMillis()
  {
    return this.mControllerIdleTimeMs;
  }
  
  public long getControllerRxTimeMillis()
  {
    return this.mControllerRxTimeMs;
  }
  
  public long getControllerTxTimeMillis()
  {
    return this.mControllerTxTimeMs;
  }
  
  public long getControllerTxTimeMillisAtLevel(int paramInt)
  {
    if (paramInt < this.mControllerTxTimePerLevelMs.length) {
      return this.mControllerTxTimePerLevelMs[paramInt];
    }
    return 0L;
  }
  
  public int getStackState()
  {
    return this.mStackState;
  }
  
  public long getTimeStamp()
  {
    return this.mTimestamp;
  }
  
  public boolean isValid()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mControllerTxTimeMs >= 0L)
    {
      bool1 = bool2;
      if (this.mControllerRxTimeMs >= 0L)
      {
        bool1 = bool2;
        if (this.mControllerIdleTimeMs >= 0L) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    return "WifiActivityEnergyInfo{ timestamp=" + this.mTimestamp + " mStackState=" + this.mStackState + " mControllerTxTimeMs=" + this.mControllerTxTimeMs + " mControllerTxTimePerLevelMs=" + Arrays.toString(this.mControllerTxTimePerLevelMs) + " mControllerRxTimeMs=" + this.mControllerRxTimeMs + " mControllerIdleTimeMs=" + this.mControllerIdleTimeMs + " mControllerEnergyUsed=" + this.mControllerEnergyUsed + " }";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mTimestamp);
    paramParcel.writeInt(this.mStackState);
    paramParcel.writeLong(this.mControllerTxTimeMs);
    paramParcel.writeLongArray(this.mControllerTxTimePerLevelMs);
    paramParcel.writeLong(this.mControllerRxTimeMs);
    paramParcel.writeLong(this.mControllerIdleTimeMs);
    paramParcel.writeLong(this.mControllerEnergyUsed);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiActivityEnergyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */