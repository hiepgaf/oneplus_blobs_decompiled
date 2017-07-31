package android.os;

public final class CpuUsageInfo
  implements Parcelable
{
  public static final Parcelable.Creator<CpuUsageInfo> CREATOR = new Parcelable.Creator()
  {
    public CpuUsageInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CpuUsageInfo(paramAnonymousParcel, null);
    }
    
    public CpuUsageInfo[] newArray(int paramAnonymousInt)
    {
      return new CpuUsageInfo[paramAnonymousInt];
    }
  };
  private long mActive;
  private long mTotal;
  
  public CpuUsageInfo(long paramLong1, long paramLong2)
  {
    this.mActive = paramLong1;
    this.mTotal = paramLong2;
  }
  
  private CpuUsageInfo(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    this.mActive = paramParcel.readLong();
    this.mTotal = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getActive()
  {
    return this.mActive;
  }
  
  public long getTotal()
  {
    return this.mTotal;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mActive);
    paramParcel.writeLong(this.mTotal);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CpuUsageInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */