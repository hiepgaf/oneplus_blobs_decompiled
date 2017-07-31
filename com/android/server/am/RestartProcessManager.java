package com.android.server.am;

import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class RestartProcessManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = "RestartProcessManager";
  public static int sMaxNumber = 10;
  private HashMap<String, Long> mPackageDuration = new HashMap();
  private ArrayList<PackageRankInfo> mPackagesInfo = new ArrayList();
  private HashMap<String, Long> mProcessLaunchTime = new HashMap();
  
  public boolean canRestart(String paramString)
  {
    Object localObject2;
    try
    {
      this.mPackagesInfo.clear();
      localObject1 = this.mPackageDuration.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        long l2 = ((Long)this.mPackageDuration.get(localObject2)).longValue();
        long l1 = 0L;
        if (this.mProcessLaunchTime.containsKey(localObject2)) {
          l1 = ((Long)this.mProcessLaunchTime.get(localObject2)).longValue();
        }
        this.mPackagesInfo.add(new PackageRankInfo((String)localObject2, l2, l1));
      }
      Collections.sort(this.mPackagesInfo, new DurationComparator());
    }
    finally {}
    Object localObject1 = this.mPackagesInfo.iterator();
    int i = 1;
    int j;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PackageRankInfo)((Iterator)localObject1).next();
      if (PackageRankInfo.-get0((PackageRankInfo)localObject2) != 0L)
      {
        j = i + 1;
        ((PackageRankInfo)localObject2).increaseRank(i);
        i = j;
      }
    }
    else
    {
      Collections.sort(this.mPackagesInfo, new LRUComparator());
      localObject1 = this.mPackagesInfo.iterator();
      i = 1;
    }
    label429:
    for (;;)
    {
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (PackageRankInfo)((Iterator)localObject1).next();
        if (PackageRankInfo.-get1((PackageRankInfo)localObject2) != 0L)
        {
          j = i + 1;
          ((PackageRankInfo)localObject2).increaseRank(i);
          i = j;
          break label429;
        }
      }
      else
      {
        Collections.sort(this.mPackagesInfo, new RankComparator());
        i = 0;
        while (i < sMaxNumber)
        {
          localObject1 = (PackageRankInfo)this.mPackagesInfo.get(i);
          if ((localObject1 != null) && (PackageRankInfo.-get2((PackageRankInfo)localObject1).equals(paramString)))
          {
            Log.d("RestartProcessManager", "Process package can restart : " + paramString);
            return true;
          }
          if (localObject1 == null)
          {
            Log.d("RestartProcessManager", "Process package can restart : " + paramString);
            return true;
          }
          i += 1;
        }
        Log.d("RestartProcessManager", "Process package can't restart : " + paramString);
        return false;
      }
      break label429;
      break;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, ArrayList<String> paramArrayList)
  {
    try
    {
      paramPrintWriter.println("Current Restart Whitelist : ");
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext()) {
        paramPrintWriter.println((String)paramArrayList.next());
      }
      paramPrintWriter.println("Current Restart Rank : ");
    }
    finally {}
    paramPrintWriter.println("Package Name        Last LaunchTime     Foreground Time");
    paramArrayList = this.mPackagesInfo.iterator();
    while (paramArrayList.hasNext())
    {
      PackageRankInfo localPackageRankInfo = (PackageRankInfo)paramArrayList.next();
      paramPrintWriter.println(PackageRankInfo.-get2(localPackageRankInfo) + "           " + PackageRankInfo.-get1(localPackageRankInfo) + "     " + PackageRankInfo.-get0(localPackageRankInfo));
    }
  }
  
  public void updateDuration(String paramString, long paramLong)
  {
    synchronized (this.mPackageDuration)
    {
      this.mPackageDuration.put(paramString, Long.valueOf(paramLong));
      return;
    }
  }
  
  public void updateLaunchTime(String paramString, long paramLong)
  {
    synchronized (this.mProcessLaunchTime)
    {
      this.mProcessLaunchTime.put(paramString, Long.valueOf(paramLong));
      return;
    }
  }
  
  public static final class DurationComparator
    implements Comparator<RestartProcessManager.PackageRankInfo>
  {
    public int compare(RestartProcessManager.PackageRankInfo paramPackageRankInfo1, RestartProcessManager.PackageRankInfo paramPackageRankInfo2)
    {
      return Long.valueOf(RestartProcessManager.PackageRankInfo.-get0(paramPackageRankInfo1)).compareTo(Long.valueOf(RestartProcessManager.PackageRankInfo.-get0(paramPackageRankInfo2)));
    }
  }
  
  public static final class LRUComparator
    implements Comparator<RestartProcessManager.PackageRankInfo>
  {
    public int compare(RestartProcessManager.PackageRankInfo paramPackageRankInfo1, RestartProcessManager.PackageRankInfo paramPackageRankInfo2)
    {
      return Long.valueOf(RestartProcessManager.PackageRankInfo.-get1(paramPackageRankInfo1)).compareTo(Long.valueOf(RestartProcessManager.PackageRankInfo.-get1(paramPackageRankInfo2)));
    }
  }
  
  private static class PackageRankInfo
  {
    private long mForegroundTime;
    private long mLastLaunchTime;
    private String mPackageName;
    private int mRank = 0;
    
    public PackageRankInfo(String paramString, long paramLong1, long paramLong2)
    {
      this.mPackageName = paramString;
      this.mForegroundTime = paramLong1;
      this.mLastLaunchTime = paramLong2;
    }
    
    public void increaseRank(int paramInt)
    {
      this.mRank += paramInt;
    }
  }
  
  public static final class RankComparator
    implements Comparator<RestartProcessManager.PackageRankInfo>
  {
    public int compare(RestartProcessManager.PackageRankInfo paramPackageRankInfo1, RestartProcessManager.PackageRankInfo paramPackageRankInfo2)
    {
      int i = RestartProcessManager.PackageRankInfo.-get3(paramPackageRankInfo1);
      return Integer.valueOf(RestartProcessManager.PackageRankInfo.-get3(paramPackageRankInfo2)).compareTo(Integer.valueOf(i));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/RestartProcessManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */