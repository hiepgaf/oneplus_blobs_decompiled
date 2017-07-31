package android.app.usage;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class UsageStatsManager
{
  public static final int INTERVAL_BEST = 4;
  public static final int INTERVAL_COUNT = 4;
  public static final int INTERVAL_DAILY = 0;
  public static final int INTERVAL_MONTHLY = 2;
  public static final int INTERVAL_WEEKLY = 1;
  public static final int INTERVAL_YEARLY = 3;
  private static final UsageEvents sEmptyResults = new UsageEvents();
  private final Context mContext;
  private final IUsageStatsManager mService;
  
  public UsageStatsManager(Context paramContext, IUsageStatsManager paramIUsageStatsManager)
  {
    this.mContext = paramContext;
    this.mService = paramIUsageStatsManager;
  }
  
  public boolean isAppInactive(String paramString)
  {
    try
    {
      boolean bool = this.mService.isAppInactive(paramString, UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException paramString) {}
    return false;
  }
  
  public void onCarrierPrivilegedAppsChanged()
  {
    try
    {
      this.mService.onCarrierPrivilegedAppsChanged();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public Map<String, UsageStats> queryAndAggregateUsageStats(long paramLong1, long paramLong2)
  {
    List localList = queryUsageStats(4, paramLong1, paramLong2);
    if (localList.isEmpty()) {
      return Collections.emptyMap();
    }
    ArrayMap localArrayMap = new ArrayMap();
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      UsageStats localUsageStats1 = (UsageStats)localList.get(i);
      UsageStats localUsageStats2 = (UsageStats)localArrayMap.get(localUsageStats1.getPackageName());
      if (localUsageStats2 == null) {
        localArrayMap.put(localUsageStats1.mPackageName, localUsageStats1);
      }
      for (;;)
      {
        i += 1;
        break;
        localUsageStats2.add(localUsageStats1);
      }
    }
    return localArrayMap;
  }
  
  public List<ConfigurationStats> queryConfigurations(int paramInt, long paramLong1, long paramLong2)
  {
    try
    {
      Object localObject = this.mService.queryConfigurationStats(paramInt, paramLong1, paramLong2, this.mContext.getOpPackageName());
      if (localObject != null)
      {
        localObject = ((ParceledListSlice)localObject).getList();
        return (List<ConfigurationStats>)localObject;
      }
    }
    catch (RemoteException localRemoteException) {}
    return Collections.emptyList();
  }
  
  public UsageEvents queryEvents(long paramLong1, long paramLong2)
  {
    try
    {
      UsageEvents localUsageEvents = this.mService.queryEvents(paramLong1, paramLong2, this.mContext.getOpPackageName());
      if (localUsageEvents != null) {
        return localUsageEvents;
      }
    }
    catch (RemoteException localRemoteException) {}
    return sEmptyResults;
  }
  
  public List<UsageStats> queryUsageStats(int paramInt, long paramLong1, long paramLong2)
  {
    try
    {
      Object localObject = this.mService.queryUsageStats(paramInt, paramLong1, paramLong2, this.mContext.getOpPackageName());
      if (localObject != null)
      {
        localObject = ((ParceledListSlice)localObject).getList();
        return (List<UsageStats>)localObject;
      }
    }
    catch (RemoteException localRemoteException) {}
    return Collections.emptyList();
  }
  
  public void setAppInactive(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setAppInactive(paramString, paramBoolean, UserHandle.myUserId());
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void whitelistAppTemporarily(String paramString, long paramLong, UserHandle paramUserHandle)
  {
    try
    {
      this.mService.whitelistAppTemporarily(paramString, paramLong, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/UsageStatsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */