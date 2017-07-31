package android.app.usage;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class UsageStats
  implements Parcelable
{
  public static final Parcelable.Creator<UsageStats> CREATOR = new Parcelable.Creator()
  {
    public UsageStats createFromParcel(Parcel paramAnonymousParcel)
    {
      UsageStats localUsageStats = new UsageStats();
      localUsageStats.mPackageName = paramAnonymousParcel.readString();
      localUsageStats.mBeginTimeStamp = paramAnonymousParcel.readLong();
      localUsageStats.mEndTimeStamp = paramAnonymousParcel.readLong();
      localUsageStats.mLastTimeUsed = paramAnonymousParcel.readLong();
      localUsageStats.mTotalTimeInForeground = paramAnonymousParcel.readLong();
      localUsageStats.mLaunchCount = paramAnonymousParcel.readInt();
      localUsageStats.mLastEvent = paramAnonymousParcel.readInt();
      return localUsageStats;
    }
    
    public UsageStats[] newArray(int paramAnonymousInt)
    {
      return new UsageStats[paramAnonymousInt];
    }
  };
  public long mBeginTimeStamp;
  public long mEndTimeStamp;
  public int mLastEvent;
  public long mLastTimeUsed;
  public int mLaunchCount;
  public String mPackageName;
  public long mTotalTimeInForeground;
  
  public UsageStats() {}
  
  public UsageStats(UsageStats paramUsageStats)
  {
    this.mPackageName = paramUsageStats.mPackageName;
    this.mBeginTimeStamp = paramUsageStats.mBeginTimeStamp;
    this.mEndTimeStamp = paramUsageStats.mEndTimeStamp;
    this.mLastTimeUsed = paramUsageStats.mLastTimeUsed;
    this.mTotalTimeInForeground = paramUsageStats.mTotalTimeInForeground;
    this.mLaunchCount = paramUsageStats.mLaunchCount;
    this.mLastEvent = paramUsageStats.mLastEvent;
  }
  
  public void add(UsageStats paramUsageStats)
  {
    if (!this.mPackageName.equals(paramUsageStats.mPackageName)) {
      throw new IllegalArgumentException("Can't merge UsageStats for package '" + this.mPackageName + "' with UsageStats for package '" + paramUsageStats.mPackageName + "'.");
    }
    if (paramUsageStats.mBeginTimeStamp > this.mBeginTimeStamp)
    {
      this.mLastEvent = paramUsageStats.mLastEvent;
      this.mLastTimeUsed = paramUsageStats.mLastTimeUsed;
    }
    this.mBeginTimeStamp = Math.min(this.mBeginTimeStamp, paramUsageStats.mBeginTimeStamp);
    this.mEndTimeStamp = Math.max(this.mEndTimeStamp, paramUsageStats.mEndTimeStamp);
    this.mTotalTimeInForeground += paramUsageStats.mTotalTimeInForeground;
    this.mLaunchCount += paramUsageStats.mLaunchCount;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getFirstTimeStamp()
  {
    return this.mBeginTimeStamp;
  }
  
  public long getLastTimeStamp()
  {
    return this.mEndTimeStamp;
  }
  
  public long getLastTimeUsed()
  {
    return this.mLastTimeUsed;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public long getTotalTimeInForeground()
  {
    return this.mTotalTimeInForeground;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeLong(this.mBeginTimeStamp);
    paramParcel.writeLong(this.mEndTimeStamp);
    paramParcel.writeLong(this.mLastTimeUsed);
    paramParcel.writeLong(this.mTotalTimeInForeground);
    paramParcel.writeInt(this.mLaunchCount);
    paramParcel.writeInt(this.mLastEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/UsageStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */