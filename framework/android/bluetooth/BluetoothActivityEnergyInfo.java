package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class BluetoothActivityEnergyInfo
  implements Parcelable
{
  public static final int BT_STACK_STATE_INVALID = 0;
  public static final int BT_STACK_STATE_STATE_ACTIVE = 1;
  public static final int BT_STACK_STATE_STATE_IDLE = 3;
  public static final int BT_STACK_STATE_STATE_SCANNING = 2;
  public static final Parcelable.Creator<BluetoothActivityEnergyInfo> CREATOR = new Parcelable.Creator()
  {
    public BluetoothActivityEnergyInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothActivityEnergyInfo(paramAnonymousParcel);
    }
    
    public BluetoothActivityEnergyInfo[] newArray(int paramAnonymousInt)
    {
      return new BluetoothActivityEnergyInfo[paramAnonymousInt];
    }
  };
  private int mBluetoothStackState;
  private long mControllerEnergyUsed;
  private long mControllerIdleTimeMs;
  private long mControllerRxTimeMs;
  private long mControllerTxTimeMs;
  private final long mTimestamp;
  private UidTraffic[] mUidTraffic;
  
  public BluetoothActivityEnergyInfo(long paramLong1, int paramInt, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    this.mTimestamp = paramLong1;
    this.mBluetoothStackState = paramInt;
    this.mControllerTxTimeMs = paramLong2;
    this.mControllerRxTimeMs = paramLong3;
    this.mControllerIdleTimeMs = paramLong4;
    this.mControllerEnergyUsed = paramLong5;
  }
  
  BluetoothActivityEnergyInfo(Parcel paramParcel)
  {
    this.mTimestamp = paramParcel.readLong();
    this.mBluetoothStackState = paramParcel.readInt();
    this.mControllerTxTimeMs = paramParcel.readLong();
    this.mControllerRxTimeMs = paramParcel.readLong();
    this.mControllerIdleTimeMs = paramParcel.readLong();
    this.mControllerEnergyUsed = paramParcel.readLong();
    this.mUidTraffic = ((UidTraffic[])paramParcel.createTypedArray(UidTraffic.CREATOR));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getBluetoothStackState()
  {
    return this.mBluetoothStackState;
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
  
  public long getTimeStamp()
  {
    return this.mTimestamp;
  }
  
  public UidTraffic[] getUidTraffic()
  {
    return this.mUidTraffic;
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
  
  public void setUidTraffic(UidTraffic[] paramArrayOfUidTraffic)
  {
    this.mUidTraffic = paramArrayOfUidTraffic;
  }
  
  public String toString()
  {
    return "BluetoothActivityEnergyInfo{ mTimestamp=" + this.mTimestamp + " mBluetoothStackState=" + this.mBluetoothStackState + " mControllerTxTimeMs=" + this.mControllerTxTimeMs + " mControllerRxTimeMs=" + this.mControllerRxTimeMs + " mControllerIdleTimeMs=" + this.mControllerIdleTimeMs + " mControllerEnergyUsed=" + this.mControllerEnergyUsed + " mUidTraffic=" + Arrays.toString(this.mUidTraffic) + " }";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mTimestamp);
    paramParcel.writeInt(this.mBluetoothStackState);
    paramParcel.writeLong(this.mControllerTxTimeMs);
    paramParcel.writeLong(this.mControllerRxTimeMs);
    paramParcel.writeLong(this.mControllerIdleTimeMs);
    paramParcel.writeLong(this.mControllerEnergyUsed);
    paramParcel.writeTypedArray(this.mUidTraffic, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothActivityEnergyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */