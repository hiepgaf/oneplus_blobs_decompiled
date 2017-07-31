package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class NetworkQuotaInfo
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkQuotaInfo> CREATOR = new Parcelable.Creator()
  {
    public NetworkQuotaInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkQuotaInfo(paramAnonymousParcel);
    }
    
    public NetworkQuotaInfo[] newArray(int paramAnonymousInt)
    {
      return new NetworkQuotaInfo[paramAnonymousInt];
    }
  };
  public static final long NO_LIMIT = -1L;
  private final long mEstimatedBytes;
  private final long mHardLimitBytes;
  private final long mSoftLimitBytes;
  
  public NetworkQuotaInfo(long paramLong1, long paramLong2, long paramLong3)
  {
    this.mEstimatedBytes = paramLong1;
    this.mSoftLimitBytes = paramLong2;
    this.mHardLimitBytes = paramLong3;
  }
  
  public NetworkQuotaInfo(Parcel paramParcel)
  {
    this.mEstimatedBytes = paramParcel.readLong();
    this.mSoftLimitBytes = paramParcel.readLong();
    this.mHardLimitBytes = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getEstimatedBytes()
  {
    return this.mEstimatedBytes;
  }
  
  public long getHardLimitBytes()
  {
    return this.mHardLimitBytes;
  }
  
  public long getSoftLimitBytes()
  {
    return this.mSoftLimitBytes;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mEstimatedBytes);
    paramParcel.writeLong(this.mSoftLimitBytes);
    paramParcel.writeLong(this.mHardLimitBytes);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkQuotaInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */