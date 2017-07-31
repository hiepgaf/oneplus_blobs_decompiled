package com.android.server.location;

import android.os.SystemClock;
import android.util.Log;
import java.util.HashMap;

public class LocationRequestStatistics
{
  private static final String TAG = "LocationStats";
  public final HashMap<PackageProviderKey, PackageStatistics> statistics = new HashMap();
  
  public void startRequesting(String paramString1, String paramString2, long paramLong)
  {
    PackageProviderKey localPackageProviderKey = new PackageProviderKey(paramString1, paramString2);
    paramString2 = (PackageStatistics)this.statistics.get(localPackageProviderKey);
    paramString1 = paramString2;
    if (paramString2 == null)
    {
      paramString1 = new PackageStatistics(null);
      this.statistics.put(localPackageProviderKey, paramString1);
    }
    PackageStatistics.-wrap0(paramString1, paramLong);
  }
  
  public void stopRequesting(String paramString1, String paramString2)
  {
    paramString1 = new PackageProviderKey(paramString1, paramString2);
    paramString1 = (PackageStatistics)this.statistics.get(paramString1);
    if (paramString1 != null)
    {
      PackageStatistics.-wrap1(paramString1);
      return;
    }
    Log.e("LocationStats", "Couldn't find package statistics when removing location request.");
  }
  
  public static class PackageProviderKey
  {
    public final String packageName;
    public final String providerName;
    
    public PackageProviderKey(String paramString1, String paramString2)
    {
      this.packageName = paramString1;
      this.providerName = paramString2;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (!(paramObject instanceof PackageProviderKey)) {
        return false;
      }
      paramObject = (PackageProviderKey)paramObject;
      if (this.packageName.equals(((PackageProviderKey)paramObject).packageName)) {
        bool = this.providerName.equals(((PackageProviderKey)paramObject).providerName);
      }
      return bool;
    }
    
    public int hashCode()
    {
      return this.packageName.hashCode() + this.providerName.hashCode() * 31;
    }
  }
  
  public static class PackageStatistics
  {
    private long mFastestIntervalMs = Long.MAX_VALUE;
    private final long mInitialElapsedTimeMs = SystemClock.elapsedRealtime();
    private long mLastActivitationElapsedTimeMs;
    private int mNumActiveRequests = 0;
    private long mSlowestIntervalMs = 0L;
    private long mTotalDurationMs = 0L;
    
    private void startRequesting(long paramLong)
    {
      if (this.mNumActiveRequests == 0) {
        this.mLastActivitationElapsedTimeMs = SystemClock.elapsedRealtime();
      }
      if (paramLong < this.mFastestIntervalMs) {
        this.mFastestIntervalMs = paramLong;
      }
      if (paramLong > this.mSlowestIntervalMs) {
        this.mSlowestIntervalMs = paramLong;
      }
      this.mNumActiveRequests += 1;
    }
    
    private void stopRequesting()
    {
      if (this.mNumActiveRequests <= 0)
      {
        Log.e("LocationStats", "Reference counting corrupted in usage statistics.");
        return;
      }
      this.mNumActiveRequests -= 1;
      if (this.mNumActiveRequests == 0)
      {
        long l1 = SystemClock.elapsedRealtime();
        long l2 = this.mLastActivitationElapsedTimeMs;
        this.mTotalDurationMs += l1 - l2;
      }
    }
    
    public long getDurationMs()
    {
      long l2 = this.mTotalDurationMs;
      long l1 = l2;
      if (this.mNumActiveRequests > 0) {
        l1 = l2 + (SystemClock.elapsedRealtime() - this.mLastActivitationElapsedTimeMs);
      }
      return l1;
    }
    
    public long getFastestIntervalMs()
    {
      return this.mFastestIntervalMs;
    }
    
    public long getSlowestIntervalMs()
    {
      return this.mSlowestIntervalMs;
    }
    
    public long getTimeSinceFirstRequestMs()
    {
      return SystemClock.elapsedRealtime() - this.mInitialElapsedTimeMs;
    }
    
    public boolean isActive()
    {
      boolean bool = false;
      if (this.mNumActiveRequests > 0) {
        bool = true;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.mFastestIntervalMs == this.mSlowestIntervalMs) {
        localStringBuilder.append("Interval ").append(this.mFastestIntervalMs / 1000L).append(" seconds");
      }
      for (;;)
      {
        localStringBuilder.append(": Duration requested ").append(getDurationMs() / 1000L / 60L).append(" out of the last ").append(getTimeSinceFirstRequestMs() / 1000L / 60L).append(" minutes");
        if (isActive()) {
          localStringBuilder.append(": Currently active");
        }
        return localStringBuilder.toString();
        localStringBuilder.append("Min interval ").append(this.mFastestIntervalMs / 1000L).append(" seconds");
        localStringBuilder.append(": Max interval ").append(this.mSlowestIntervalMs / 1000L).append(" seconds");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationRequestStatistics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */