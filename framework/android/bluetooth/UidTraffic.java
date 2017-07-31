package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UidTraffic
  implements Cloneable, Parcelable
{
  public static final Parcelable.Creator<UidTraffic> CREATOR = new Parcelable.Creator()
  {
    public UidTraffic createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UidTraffic(paramAnonymousParcel);
    }
    
    public UidTraffic[] newArray(int paramAnonymousInt)
    {
      return new UidTraffic[paramAnonymousInt];
    }
  };
  private final int mAppUid;
  private long mRxBytes;
  private long mTxBytes;
  
  public UidTraffic(int paramInt)
  {
    this.mAppUid = paramInt;
  }
  
  public UidTraffic(int paramInt, long paramLong1, long paramLong2)
  {
    this.mAppUid = paramInt;
    this.mRxBytes = paramLong1;
    this.mTxBytes = paramLong2;
  }
  
  UidTraffic(Parcel paramParcel)
  {
    this.mAppUid = paramParcel.readInt();
    this.mRxBytes = paramParcel.readLong();
    this.mTxBytes = paramParcel.readLong();
  }
  
  public void addRxBytes(long paramLong)
  {
    this.mRxBytes += paramLong;
  }
  
  public void addTxBytes(long paramLong)
  {
    this.mTxBytes += paramLong;
  }
  
  public UidTraffic clone()
  {
    return new UidTraffic(this.mAppUid, this.mRxBytes, this.mTxBytes);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getRxBytes()
  {
    return this.mRxBytes;
  }
  
  public long getTxBytes()
  {
    return this.mTxBytes;
  }
  
  public int getUid()
  {
    return this.mAppUid;
  }
  
  public void setRxBytes(long paramLong)
  {
    this.mRxBytes = paramLong;
  }
  
  public void setTxBytes(long paramLong)
  {
    this.mTxBytes = paramLong;
  }
  
  public String toString()
  {
    return "UidTraffic{mAppUid=" + this.mAppUid + ", mRxBytes=" + this.mRxBytes + ", mTxBytes=" + this.mTxBytes + '}';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAppUid);
    paramParcel.writeLong(this.mRxBytes);
    paramParcel.writeLong(this.mTxBytes);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/UidTraffic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */