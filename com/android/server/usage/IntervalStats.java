package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.TimeSparseArray;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.util.ArrayMap;
import android.util.ArraySet;

class IntervalStats
{
  public Configuration activeConfiguration;
  public long beginTime;
  public final ArrayMap<Configuration, ConfigurationStats> configurations = new ArrayMap();
  public long endTime;
  public TimeSparseArray<UsageEvents.Event> events;
  public long lastTimeSaved;
  private final ArraySet<String> mStringCache = new ArraySet();
  public final ArrayMap<String, UsageStats> packageStats = new ArrayMap();
  
  private String getCachedStringRef(String paramString)
  {
    int i = this.mStringCache.indexOf(paramString);
    if (i < 0)
    {
      this.mStringCache.add(paramString);
      return paramString;
    }
    return (String)this.mStringCache.valueAt(i);
  }
  
  private boolean isStatefulEvent(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  UsageEvents.Event buildEvent(String paramString1, String paramString2)
  {
    UsageEvents.Event localEvent = new UsageEvents.Event();
    localEvent.mPackage = getCachedStringRef(paramString1);
    if (paramString2 != null) {
      localEvent.mClass = getCachedStringRef(paramString2);
    }
    return localEvent;
  }
  
  ConfigurationStats getOrCreateConfigurationStats(Configuration paramConfiguration)
  {
    ConfigurationStats localConfigurationStats2 = (ConfigurationStats)this.configurations.get(paramConfiguration);
    ConfigurationStats localConfigurationStats1 = localConfigurationStats2;
    if (localConfigurationStats2 == null)
    {
      localConfigurationStats1 = new ConfigurationStats();
      localConfigurationStats1.mBeginTimeStamp = this.beginTime;
      localConfigurationStats1.mEndTimeStamp = this.endTime;
      localConfigurationStats1.mConfiguration = paramConfiguration;
      this.configurations.put(paramConfiguration, localConfigurationStats1);
    }
    return localConfigurationStats1;
  }
  
  UsageStats getOrCreateUsageStats(String paramString)
  {
    UsageStats localUsageStats2 = (UsageStats)this.packageStats.get(paramString);
    UsageStats localUsageStats1 = localUsageStats2;
    if (localUsageStats2 == null)
    {
      localUsageStats1 = new UsageStats();
      localUsageStats1.mPackageName = getCachedStringRef(paramString);
      localUsageStats1.mBeginTimeStamp = this.beginTime;
      localUsageStats1.mEndTimeStamp = this.endTime;
      this.packageStats.put(localUsageStats1.mPackageName, localUsageStats1);
    }
    return localUsageStats1;
  }
  
  void update(String paramString, long paramLong, int paramInt)
  {
    paramString = getOrCreateUsageStats(paramString);
    if (((paramInt == 2) || (paramInt == 3)) && ((paramString.mLastEvent == 1) || (paramString.mLastEvent == 4))) {
      paramString.mTotalTimeInForeground += paramLong - paramString.mLastTimeUsed;
    }
    if (isStatefulEvent(paramInt)) {
      paramString.mLastEvent = paramInt;
    }
    if (paramInt != 6) {
      paramString.mLastTimeUsed = paramLong;
    }
    paramString.mEndTimeStamp = paramLong;
    if (paramInt == 1) {
      paramString.mLaunchCount += 1;
    }
    this.endTime = paramLong;
  }
  
  void updateConfigurationStats(Configuration paramConfiguration, long paramLong)
  {
    if (this.activeConfiguration != null)
    {
      ConfigurationStats localConfigurationStats = (ConfigurationStats)this.configurations.get(this.activeConfiguration);
      localConfigurationStats.mTotalTimeActive += paramLong - localConfigurationStats.mLastTimeActive;
      localConfigurationStats.mLastTimeActive = (paramLong - 1L);
    }
    if (paramConfiguration != null)
    {
      paramConfiguration = getOrCreateConfigurationStats(paramConfiguration);
      paramConfiguration.mLastTimeActive = paramLong;
      paramConfiguration.mActivationCount += 1;
      this.activeConfiguration = paramConfiguration.mConfiguration;
    }
    this.endTime = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/IntervalStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */