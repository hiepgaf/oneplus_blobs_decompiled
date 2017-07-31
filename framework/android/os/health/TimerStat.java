package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class TimerStat
  implements Parcelable
{
  public static final Parcelable.Creator<TimerStat> CREATOR = new Parcelable.Creator()
  {
    public TimerStat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TimerStat(paramAnonymousParcel);
    }
    
    public TimerStat[] newArray(int paramAnonymousInt)
    {
      return new TimerStat[paramAnonymousInt];
    }
  };
  private int mCount;
  private long mTime;
  
  public TimerStat() {}
  
  public TimerStat(int paramInt, long paramLong)
  {
    this.mCount = paramInt;
    this.mTime = paramLong;
  }
  
  public TimerStat(Parcel paramParcel)
  {
    this.mCount = paramParcel.readInt();
    this.mTime = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCount()
  {
    return this.mCount;
  }
  
  public long getTime()
  {
    return this.mTime;
  }
  
  public void setCount(int paramInt)
  {
    this.mCount = paramInt;
  }
  
  public void setTime(long paramLong)
  {
    this.mTime = paramLong;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mCount);
    paramParcel.writeLong(this.mTime);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/TimerStat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */