package com.android.server.net;

import android.net.DataUsageRequest;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.net.VpnInfo;
import com.android.internal.util.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;

class NetworkStatsObservers
{
  private static final boolean LOGV = false;
  private static final long MIN_THRESHOLD_BYTES = 2097152L;
  private static final int MSG_REGISTER = 1;
  private static final int MSG_UNREGISTER = 2;
  private static final int MSG_UPDATE_STATS = 3;
  private static final String TAG = "NetworkStatsObservers";
  private final SparseArray<RequestInfo> mDataUsageRequests = new SparseArray();
  private Handler mHandler;
  private Handler.Callback mHandlerCallback = new Handler.Callback()
  {
    public boolean handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return false;
      case 1: 
        NetworkStatsObservers.-wrap0(NetworkStatsObservers.this, (NetworkStatsObservers.RequestInfo)paramAnonymousMessage.obj);
        return true;
      case 2: 
        NetworkStatsObservers.-wrap1(NetworkStatsObservers.this, (DataUsageRequest)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
        return true;
      }
      NetworkStatsObservers.-wrap2(NetworkStatsObservers.this, (NetworkStatsObservers.StatsContext)paramAnonymousMessage.obj);
      return true;
    }
  };
  private final AtomicInteger mNextDataUsageRequestId = new AtomicInteger();
  
  private DataUsageRequest buildRequest(DataUsageRequest paramDataUsageRequest)
  {
    long l = Math.max(2097152L, paramDataUsageRequest.thresholdInBytes);
    if (l < paramDataUsageRequest.thresholdInBytes) {
      Slog.w("NetworkStatsObservers", "Threshold was too low for " + paramDataUsageRequest + ". Overriding to a safer default of " + l + " bytes");
    }
    return new DataUsageRequest(this.mNextDataUsageRequestId.incrementAndGet(), paramDataUsageRequest.template, l);
  }
  
  private RequestInfo buildRequestInfo(DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (paramInt2 <= 1) {
      return new UserUsageRequestInfo(this, paramDataUsageRequest, paramMessenger, paramIBinder, paramInt1, paramInt2);
    }
    if (paramInt2 >= 2) {}
    for (;;)
    {
      Preconditions.checkArgument(bool);
      return new NetworkUsageRequestInfo(this, paramDataUsageRequest, paramMessenger, paramIBinder, paramInt1, paramInt2);
      bool = false;
    }
  }
  
  private Handler getHandler()
  {
    if (this.mHandler == null) {}
    try
    {
      if (this.mHandler == null) {
        this.mHandler = new Handler(getHandlerLooperLocked(), this.mHandlerCallback);
      }
      return this.mHandler;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void handleRegister(RequestInfo paramRequestInfo)
  {
    this.mDataUsageRequests.put(paramRequestInfo.mRequest.requestId, paramRequestInfo);
  }
  
  private void handleUnregister(DataUsageRequest paramDataUsageRequest, int paramInt)
  {
    RequestInfo localRequestInfo = (RequestInfo)this.mDataUsageRequests.get(paramDataUsageRequest.requestId);
    if (localRequestInfo == null) {
      return;
    }
    if ((1000 != paramInt) && (localRequestInfo.mCallingUid != paramInt))
    {
      Slog.w("NetworkStatsObservers", "Caller uid " + paramInt + " is not owner of " + paramDataUsageRequest);
      return;
    }
    this.mDataUsageRequests.remove(paramDataUsageRequest.requestId);
    RequestInfo.-wrap1(localRequestInfo);
    RequestInfo.-wrap0(localRequestInfo, 1);
  }
  
  private void handleUpdateStats(StatsContext paramStatsContext)
  {
    if (this.mDataUsageRequests.size() == 0) {
      return;
    }
    int i = 0;
    while (i < this.mDataUsageRequests.size())
    {
      RequestInfo.-wrap2((RequestInfo)this.mDataUsageRequests.valueAt(i), paramStatsContext);
      i += 1;
    }
  }
  
  protected Looper getHandlerLooperLocked()
  {
    HandlerThread localHandlerThread = new HandlerThread("NetworkStatsObservers");
    localHandlerThread.start();
    return localHandlerThread.getLooper();
  }
  
  public DataUsageRequest register(DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    paramDataUsageRequest = buildRequest(paramDataUsageRequest);
    paramMessenger = buildRequestInfo(paramDataUsageRequest, paramMessenger, paramIBinder, paramInt1, paramInt2);
    getHandler().sendMessage(this.mHandler.obtainMessage(1, paramMessenger));
    return paramDataUsageRequest;
  }
  
  public void unregister(DataUsageRequest paramDataUsageRequest, int paramInt)
  {
    getHandler().sendMessage(this.mHandler.obtainMessage(2, paramInt, 0, paramDataUsageRequest));
  }
  
  public void updateStats(NetworkStats paramNetworkStats1, NetworkStats paramNetworkStats2, ArrayMap<String, NetworkIdentitySet> paramArrayMap1, ArrayMap<String, NetworkIdentitySet> paramArrayMap2, VpnInfo[] paramArrayOfVpnInfo, long paramLong)
  {
    paramNetworkStats1 = new StatsContext(paramNetworkStats1, paramNetworkStats2, paramArrayMap1, paramArrayMap2, paramArrayOfVpnInfo, paramLong);
    getHandler().sendMessage(this.mHandler.obtainMessage(3, paramNetworkStats1));
  }
  
  private static class NetworkUsageRequestInfo
    extends NetworkStatsObservers.RequestInfo
  {
    NetworkUsageRequestInfo(NetworkStatsObservers paramNetworkStatsObservers, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder, int paramInt1, int paramInt2)
    {
      super(paramDataUsageRequest, paramMessenger, paramIBinder, paramInt1, paramInt2);
    }
    
    private long getTotalBytesForNetwork(NetworkTemplate paramNetworkTemplate)
    {
      return this.mCollection.getSummary(paramNetworkTemplate, Long.MIN_VALUE, Long.MAX_VALUE, this.mAccessLevel, this.mCallingUid).getTotalBytes();
    }
    
    protected boolean checkStats()
    {
      return getTotalBytesForNetwork(this.mRequest.template) > this.mRequest.thresholdInBytes;
    }
    
    protected void recordSample(NetworkStatsObservers.StatsContext paramStatsContext)
    {
      this.mRecorder.recordSnapshotLocked(paramStatsContext.mXtSnapshot, paramStatsContext.mActiveIfaces, null, paramStatsContext.mCurrentTime);
    }
  }
  
  private static abstract class RequestInfo
    implements IBinder.DeathRecipient
  {
    protected final int mAccessLevel;
    private final IBinder mBinder;
    protected final int mCallingUid;
    protected NetworkStatsCollection mCollection;
    private final Messenger mMessenger;
    protected NetworkStatsRecorder mRecorder;
    protected final DataUsageRequest mRequest;
    private final NetworkStatsObservers mStatsObserver;
    
    RequestInfo(NetworkStatsObservers paramNetworkStatsObservers, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder, int paramInt1, int paramInt2)
    {
      this.mStatsObserver = paramNetworkStatsObservers;
      this.mRequest = paramDataUsageRequest;
      this.mMessenger = paramMessenger;
      this.mBinder = paramIBinder;
      this.mCallingUid = paramInt1;
      this.mAccessLevel = paramInt2;
      try
      {
        this.mBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException paramNetworkStatsObservers)
      {
        binderDied();
      }
    }
    
    private void callCallback(int paramInt)
    {
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("DataUsageRequest", this.mRequest);
      Message localMessage = Message.obtain();
      localMessage.what = paramInt;
      localMessage.setData(localBundle);
      try
      {
        this.mMessenger.send(localMessage);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("NetworkStatsObservers", "RemoteException caught trying to send a callback msg for " + this.mRequest);
      }
    }
    
    private String callbackTypeToName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN";
      case 0: 
        return "LIMIT_REACHED";
      }
      return "RELEASED";
    }
    
    private void resetRecorder()
    {
      this.mRecorder = new NetworkStatsRecorder();
      this.mCollection = this.mRecorder.getSinceBoot();
    }
    
    private void unlinkDeathRecipient()
    {
      if (this.mBinder != null) {
        this.mBinder.unlinkToDeath(this, 0);
      }
    }
    
    private void updateStats(NetworkStatsObservers.StatsContext paramStatsContext)
    {
      if (this.mRecorder == null)
      {
        resetRecorder();
        recordSample(paramStatsContext);
        return;
      }
      recordSample(paramStatsContext);
      if (checkStats())
      {
        resetRecorder();
        callCallback(0);
      }
    }
    
    public void binderDied()
    {
      this.mStatsObserver.unregister(this.mRequest, 1000);
      callCallback(1);
    }
    
    protected abstract boolean checkStats();
    
    protected abstract void recordSample(NetworkStatsObservers.StatsContext paramStatsContext);
    
    public String toString()
    {
      return "RequestInfo from uid:" + this.mCallingUid + " for " + this.mRequest + " accessLevel:" + this.mAccessLevel;
    }
  }
  
  private static class StatsContext
  {
    ArrayMap<String, NetworkIdentitySet> mActiveIfaces;
    ArrayMap<String, NetworkIdentitySet> mActiveUidIfaces;
    long mCurrentTime;
    NetworkStats mUidSnapshot;
    VpnInfo[] mVpnArray;
    NetworkStats mXtSnapshot;
    
    StatsContext(NetworkStats paramNetworkStats1, NetworkStats paramNetworkStats2, ArrayMap<String, NetworkIdentitySet> paramArrayMap1, ArrayMap<String, NetworkIdentitySet> paramArrayMap2, VpnInfo[] paramArrayOfVpnInfo, long paramLong)
    {
      this.mXtSnapshot = paramNetworkStats1;
      this.mUidSnapshot = paramNetworkStats2;
      this.mActiveIfaces = paramArrayMap1;
      this.mActiveUidIfaces = paramArrayMap2;
      this.mVpnArray = paramArrayOfVpnInfo;
      this.mCurrentTime = paramLong;
    }
  }
  
  private static class UserUsageRequestInfo
    extends NetworkStatsObservers.RequestInfo
  {
    UserUsageRequestInfo(NetworkStatsObservers paramNetworkStatsObservers, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder, int paramInt1, int paramInt2)
    {
      super(paramDataUsageRequest, paramMessenger, paramIBinder, paramInt1, paramInt2);
    }
    
    private long getTotalBytesForNetworkUid(NetworkTemplate paramNetworkTemplate, int paramInt)
    {
      try
      {
        long l = this.mCollection.getHistory(paramNetworkTemplate, paramInt, -1, 0, -1, Long.MIN_VALUE, Long.MAX_VALUE, this.mAccessLevel, this.mCallingUid).getTotalBytes();
        return l;
      }
      catch (SecurityException paramNetworkTemplate) {}
      return 0L;
    }
    
    protected boolean checkStats()
    {
      int[] arrayOfInt = this.mCollection.getRelevantUids(this.mAccessLevel, this.mCallingUid);
      int i = 0;
      while (i < arrayOfInt.length)
      {
        if (getTotalBytesForNetworkUid(this.mRequest.template, arrayOfInt[i]) > this.mRequest.thresholdInBytes) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    protected void recordSample(NetworkStatsObservers.StatsContext paramStatsContext)
    {
      this.mRecorder.recordSnapshotLocked(paramStatsContext.mUidSnapshot, paramStatsContext.mActiveUidIfaces, paramStatsContext.mVpnArray, paramStatsContext.mCurrentTime);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkStatsObservers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */