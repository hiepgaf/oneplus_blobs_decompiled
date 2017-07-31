package android.app.usage;

import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConfigurationStats
  implements Parcelable
{
  public static final Parcelable.Creator<ConfigurationStats> CREATOR = new Parcelable.Creator()
  {
    public ConfigurationStats createFromParcel(Parcel paramAnonymousParcel)
    {
      ConfigurationStats localConfigurationStats = new ConfigurationStats();
      if (paramAnonymousParcel.readInt() != 0) {
        localConfigurationStats.mConfiguration = ((Configuration)Configuration.CREATOR.createFromParcel(paramAnonymousParcel));
      }
      localConfigurationStats.mBeginTimeStamp = paramAnonymousParcel.readLong();
      localConfigurationStats.mEndTimeStamp = paramAnonymousParcel.readLong();
      localConfigurationStats.mLastTimeActive = paramAnonymousParcel.readLong();
      localConfigurationStats.mTotalTimeActive = paramAnonymousParcel.readLong();
      localConfigurationStats.mActivationCount = paramAnonymousParcel.readInt();
      return localConfigurationStats;
    }
    
    public ConfigurationStats[] newArray(int paramAnonymousInt)
    {
      return new ConfigurationStats[paramAnonymousInt];
    }
  };
  public int mActivationCount;
  public long mBeginTimeStamp;
  public Configuration mConfiguration;
  public long mEndTimeStamp;
  public long mLastTimeActive;
  public long mTotalTimeActive;
  
  public ConfigurationStats() {}
  
  public ConfigurationStats(ConfigurationStats paramConfigurationStats)
  {
    this.mConfiguration = paramConfigurationStats.mConfiguration;
    this.mBeginTimeStamp = paramConfigurationStats.mBeginTimeStamp;
    this.mEndTimeStamp = paramConfigurationStats.mEndTimeStamp;
    this.mLastTimeActive = paramConfigurationStats.mLastTimeActive;
    this.mTotalTimeActive = paramConfigurationStats.mTotalTimeActive;
    this.mActivationCount = paramConfigurationStats.mActivationCount;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getActivationCount()
  {
    return this.mActivationCount;
  }
  
  public Configuration getConfiguration()
  {
    return this.mConfiguration;
  }
  
  public long getFirstTimeStamp()
  {
    return this.mBeginTimeStamp;
  }
  
  public long getLastTimeActive()
  {
    return this.mLastTimeActive;
  }
  
  public long getLastTimeStamp()
  {
    return this.mEndTimeStamp;
  }
  
  public long getTotalTimeActive()
  {
    return this.mTotalTimeActive;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mConfiguration != null)
    {
      paramParcel.writeInt(1);
      this.mConfiguration.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeLong(this.mBeginTimeStamp);
      paramParcel.writeLong(this.mEndTimeStamp);
      paramParcel.writeLong(this.mLastTimeActive);
      paramParcel.writeLong(this.mTotalTimeActive);
      paramParcel.writeInt(this.mActivationCount);
      return;
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/ConfigurationStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */