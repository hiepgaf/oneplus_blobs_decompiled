package android.app.usage;

import android.content.Context;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.NetworkStats.Entry;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.IntArray;
import android.util.Log;
import dalvik.system.CloseGuard;

public final class NetworkStats
  implements AutoCloseable
{
  private static final String TAG = "NetworkStats";
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final long mEndTimeStamp;
  private int mEnumerationIndex = 0;
  private NetworkStatsHistory mHistory = null;
  private NetworkStatsHistory.Entry mRecycledHistoryEntry = null;
  private NetworkStats.Entry mRecycledSummaryEntry = null;
  private INetworkStatsSession mSession;
  private final long mStartTimeStamp;
  private android.net.NetworkStats mSummary = null;
  private int mTag = 0;
  private NetworkTemplate mTemplate;
  private int mUidOrUidIndex;
  private int[] mUids;
  
  NetworkStats(Context paramContext, NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
    throws RemoteException, SecurityException
  {
    this.mSession = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats")).openSessionForUsageStats(paramContext.getOpPackageName());
    this.mCloseGuard.open("close");
    this.mTemplate = paramNetworkTemplate;
    this.mStartTimeStamp = paramLong1;
    this.mEndTimeStamp = paramLong2;
  }
  
  private void fillBucketFromSummaryEntry(Bucket paramBucket)
  {
    Bucket.-set9(paramBucket, Bucket.-wrap3(this.mRecycledSummaryEntry.uid));
    Bucket.-set6(paramBucket, Bucket.-wrap2(this.mRecycledSummaryEntry.tag));
    Bucket.-set5(paramBucket, Bucket.-wrap1(this.mRecycledSummaryEntry.set));
    Bucket.-set2(paramBucket, Bucket.-wrap0(this.mRecycledSummaryEntry.roaming));
    Bucket.-set0(paramBucket, this.mStartTimeStamp);
    Bucket.-set1(paramBucket, this.mEndTimeStamp);
    Bucket.-set3(paramBucket, this.mRecycledSummaryEntry.rxBytes);
    Bucket.-set4(paramBucket, this.mRecycledSummaryEntry.rxPackets);
    Bucket.-set7(paramBucket, this.mRecycledSummaryEntry.txBytes);
    Bucket.-set8(paramBucket, this.mRecycledSummaryEntry.txPackets);
  }
  
  private boolean getNextHistoryBucket(Bucket paramBucket)
  {
    if ((paramBucket != null) && (this.mHistory != null))
    {
      if (this.mEnumerationIndex < this.mHistory.size())
      {
        NetworkStatsHistory localNetworkStatsHistory = this.mHistory;
        int i = this.mEnumerationIndex;
        this.mEnumerationIndex = (i + 1);
        this.mRecycledHistoryEntry = localNetworkStatsHistory.getValues(i, this.mRecycledHistoryEntry);
        Bucket.-set9(paramBucket, Bucket.-wrap3(getUid()));
        Bucket.-set6(paramBucket, Bucket.-wrap2(this.mTag));
        Bucket.-set5(paramBucket, -1);
        Bucket.-set2(paramBucket, -1);
        Bucket.-set0(paramBucket, this.mRecycledHistoryEntry.bucketStart);
        Bucket.-set1(paramBucket, this.mRecycledHistoryEntry.bucketStart + this.mRecycledHistoryEntry.bucketDuration);
        Bucket.-set3(paramBucket, this.mRecycledHistoryEntry.rxBytes);
        Bucket.-set4(paramBucket, this.mRecycledHistoryEntry.rxPackets);
        Bucket.-set7(paramBucket, this.mRecycledHistoryEntry.txBytes);
        Bucket.-set8(paramBucket, this.mRecycledHistoryEntry.txPackets);
        return true;
      }
      if (hasNextUid())
      {
        stepHistory();
        return getNextHistoryBucket(paramBucket);
      }
    }
    return false;
  }
  
  private boolean getNextSummaryBucket(Bucket paramBucket)
  {
    if ((paramBucket != null) && (this.mEnumerationIndex < this.mSummary.size()))
    {
      android.net.NetworkStats localNetworkStats = this.mSummary;
      int i = this.mEnumerationIndex;
      this.mEnumerationIndex = (i + 1);
      this.mRecycledSummaryEntry = localNetworkStats.getValues(i, this.mRecycledSummaryEntry);
      fillBucketFromSummaryEntry(paramBucket);
      return true;
    }
    return false;
  }
  
  private int getUid()
  {
    if (isUidEnumeration())
    {
      if ((this.mUidOrUidIndex < 0) || (this.mUidOrUidIndex >= this.mUids.length)) {
        throw new IndexOutOfBoundsException("Index=" + this.mUidOrUidIndex + " mUids.length=" + this.mUids.length);
      }
      return this.mUids[this.mUidOrUidIndex];
    }
    return this.mUidOrUidIndex;
  }
  
  private boolean hasNextUid()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isUidEnumeration())
    {
      bool1 = bool2;
      if (this.mUidOrUidIndex + 1 < this.mUids.length) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isUidEnumeration()
  {
    return this.mUids != null;
  }
  
  private void setSingleUidTag(int paramInt1, int paramInt2)
  {
    this.mUidOrUidIndex = paramInt1;
    this.mTag = paramInt2;
  }
  
  private void stepHistory()
  {
    if (hasNextUid())
    {
      stepUid();
      this.mHistory = null;
    }
    try
    {
      this.mHistory = this.mSession.getHistoryIntervalForUid(this.mTemplate, getUid(), -1, 0, -1, this.mStartTimeStamp, this.mEndTimeStamp);
      this.mEnumerationIndex = 0;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("NetworkStats", localRemoteException);
      }
    }
  }
  
  private void stepUid()
  {
    if (this.mUids != null) {
      this.mUidOrUidIndex += 1;
    }
  }
  
  public void close()
  {
    if (this.mSession != null) {}
    try
    {
      this.mSession.close();
      this.mSession = null;
      if (this.mCloseGuard != null) {
        this.mCloseGuard.close();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("NetworkStats", localRemoteException);
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mCloseGuard != null) {
        this.mCloseGuard.warnIfOpen();
      }
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  Bucket getDeviceSummaryForNetwork()
    throws RemoteException
  {
    this.mSummary = this.mSession.getDeviceSummaryForNetwork(this.mTemplate, this.mStartTimeStamp, this.mEndTimeStamp);
    this.mEnumerationIndex = this.mSummary.size();
    return getSummaryAggregate();
  }
  
  public boolean getNextBucket(Bucket paramBucket)
  {
    if (this.mSummary != null) {
      return getNextSummaryBucket(paramBucket);
    }
    return getNextHistoryBucket(paramBucket);
  }
  
  Bucket getSummaryAggregate()
  {
    if (this.mSummary == null) {
      return null;
    }
    Bucket localBucket = new Bucket();
    if (this.mRecycledSummaryEntry == null) {
      this.mRecycledSummaryEntry = new NetworkStats.Entry();
    }
    this.mSummary.getTotal(this.mRecycledSummaryEntry);
    fillBucketFromSummaryEntry(localBucket);
    return localBucket;
  }
  
  public boolean hasNextBucket()
  {
    boolean bool = true;
    if (this.mSummary != null) {
      return this.mEnumerationIndex < this.mSummary.size();
    }
    if (this.mHistory != null)
    {
      if (this.mEnumerationIndex >= this.mHistory.size()) {
        bool = hasNextUid();
      }
      return bool;
    }
    return false;
  }
  
  void startHistoryEnumeration(int paramInt)
  {
    startHistoryEnumeration(paramInt, 0);
  }
  
  void startHistoryEnumeration(int paramInt1, int paramInt2)
  {
    this.mHistory = null;
    try
    {
      this.mHistory = this.mSession.getHistoryIntervalForUid(this.mTemplate, paramInt1, -1, paramInt2, -1, this.mStartTimeStamp, this.mEndTimeStamp);
      setSingleUidTag(paramInt1, paramInt2);
      this.mEnumerationIndex = 0;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("NetworkStats", localRemoteException);
      }
    }
  }
  
  void startSummaryEnumeration()
    throws RemoteException
  {
    this.mSummary = this.mSession.getSummaryForAllUid(this.mTemplate, this.mStartTimeStamp, this.mEndTimeStamp, false);
    this.mEnumerationIndex = 0;
  }
  
  void startUserUidEnumeration()
    throws RemoteException
  {
    int[] arrayOfInt = this.mSession.getRelevantUids();
    IntArray localIntArray = new IntArray(arrayOfInt.length);
    int j = arrayOfInt.length;
    int i = 0;
    int k;
    while (i < j)
    {
      k = arrayOfInt[i];
      try
      {
        NetworkStatsHistory localNetworkStatsHistory = this.mSession.getHistoryIntervalForUid(this.mTemplate, k, -1, 0, -1, this.mStartTimeStamp, this.mEndTimeStamp);
        if ((localNetworkStatsHistory != null) && (localNetworkStatsHistory.size() > 0)) {
          localIntArray.add(k);
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("NetworkStats", "Error while getting history of uid " + k, localRemoteException);
        }
      }
      i += 1;
    }
    this.mUids = localIntArray.toArray();
    this.mUidOrUidIndex = -1;
    stepHistory();
  }
  
  public static class Bucket
  {
    public static final int ROAMING_ALL = -1;
    public static final int ROAMING_NO = 1;
    public static final int ROAMING_YES = 2;
    public static final int STATE_ALL = -1;
    public static final int STATE_DEFAULT = 1;
    public static final int STATE_FOREGROUND = 2;
    public static final int TAG_NONE = 0;
    public static final int UID_ALL = -1;
    public static final int UID_REMOVED = -4;
    public static final int UID_TETHERING = -5;
    private long mBeginTimeStamp;
    private long mEndTimeStamp;
    private int mRoaming;
    private long mRxBytes;
    private long mRxPackets;
    private int mState;
    private int mTag;
    private long mTxBytes;
    private long mTxPackets;
    private int mUid;
    
    private static int convertRoaming(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 0;
      case -1: 
        return -1;
      case 0: 
        return 1;
      }
      return 2;
    }
    
    private static int convertState(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 0;
      case -1: 
        return -1;
      case 0: 
        return 1;
      }
      return 2;
    }
    
    private static int convertTag(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return paramInt;
      }
      return 0;
    }
    
    private static int convertUid(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return paramInt;
      case -4: 
        return -4;
      }
      return -5;
    }
    
    public long getEndTimeStamp()
    {
      return this.mEndTimeStamp;
    }
    
    public int getRoaming()
    {
      return this.mRoaming;
    }
    
    public long getRxBytes()
    {
      return this.mRxBytes;
    }
    
    public long getRxPackets()
    {
      return this.mRxPackets;
    }
    
    public long getStartTimeStamp()
    {
      return this.mBeginTimeStamp;
    }
    
    public int getState()
    {
      return this.mState;
    }
    
    public int getTag()
    {
      return this.mTag;
    }
    
    public long getTxBytes()
    {
      return this.mTxBytes;
    }
    
    public long getTxPackets()
    {
      return this.mTxPackets;
    }
    
    public int getUid()
    {
      return this.mUid;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/NetworkStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */