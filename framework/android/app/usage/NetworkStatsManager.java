package android.app.usage;

import android.content.Context;
import android.net.DataUsageRequest;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.NetworkIdentity;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.util.Preconditions;

public class NetworkStatsManager
{
  public static final int CALLBACK_LIMIT_REACHED = 0;
  public static final int CALLBACK_RELEASED = 1;
  private static final boolean DBG = false;
  private static final String TAG = "NetworkStatsManager";
  private final Context mContext;
  private final INetworkStatsService mService;
  
  public NetworkStatsManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
  }
  
  private static NetworkTemplate createTemplate(int paramInt, String paramString)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Cannot create template for network type " + paramInt + ", subscriberId '" + NetworkIdentity.scrubSubscriberId(paramString) + "'.");
    case 0: 
      return NetworkTemplate.buildTemplateMobileAll(paramString);
    }
    return NetworkTemplate.buildTemplateWifiWildcard();
  }
  
  public NetworkStats queryDetails(int paramInt, String paramString, long paramLong1, long paramLong2)
    throws SecurityException, RemoteException
  {
    try
    {
      paramString = createTemplate(paramInt, paramString);
      paramString = new NetworkStats(this.mContext, paramString, paramLong1, paramLong2);
      paramString.startUserUidEnumeration();
      return paramString;
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  public NetworkStats queryDetailsForUid(int paramInt1, String paramString, long paramLong1, long paramLong2, int paramInt2)
    throws SecurityException, RemoteException
  {
    return queryDetailsForUidTag(paramInt1, paramString, paramLong1, paramLong2, paramInt2, 0);
  }
  
  public NetworkStats queryDetailsForUidTag(int paramInt1, String paramString, long paramLong1, long paramLong2, int paramInt2, int paramInt3)
    throws SecurityException
  {
    paramString = createTemplate(paramInt1, paramString);
    try
    {
      paramString = new NetworkStats(this.mContext, paramString, paramLong1, paramLong2);
      paramString.startHistoryEnumeration(paramInt2, paramInt3);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      Log.e("NetworkStatsManager", "Error while querying stats for uid=" + paramInt2 + " tag=" + paramInt3, paramString);
    }
    return null;
  }
  
  public NetworkStats querySummary(int paramInt, String paramString, long paramLong1, long paramLong2)
    throws SecurityException, RemoteException
  {
    try
    {
      paramString = createTemplate(paramInt, paramString);
      paramString = new NetworkStats(this.mContext, paramString, paramLong1, paramLong2);
      paramString.startSummaryEnumeration();
      return paramString;
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  public NetworkStats.Bucket querySummaryForDevice(int paramInt, String paramString, long paramLong1, long paramLong2)
    throws SecurityException, RemoteException
  {
    try
    {
      paramString = createTemplate(paramInt, paramString);
      paramString = new NetworkStats(this.mContext, paramString, paramLong1, paramLong2);
      NetworkStats.Bucket localBucket = paramString.getDeviceSummaryForNetwork();
      paramString.close();
      return localBucket;
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  public NetworkStats.Bucket querySummaryForUser(int paramInt, String paramString, long paramLong1, long paramLong2)
    throws SecurityException, RemoteException
  {
    try
    {
      paramString = createTemplate(paramInt, paramString);
      paramString = new NetworkStats(this.mContext, paramString, paramLong1, paramLong2);
      paramString.startSummaryEnumeration();
      paramString.close();
      return paramString.getSummaryAggregate();
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  public void registerUsageCallback(int paramInt, String paramString, long paramLong, UsageCallback paramUsageCallback)
  {
    registerUsageCallback(paramInt, paramString, paramLong, paramUsageCallback, null);
  }
  
  public void registerUsageCallback(int paramInt, String paramString, long paramLong, UsageCallback paramUsageCallback, Handler paramHandler)
  {
    Preconditions.checkNotNull(paramUsageCallback, "UsageCallback cannot be null");
    if (paramHandler == null) {}
    for (paramHandler = Looper.myLooper();; paramHandler = paramHandler.getLooper())
    {
      DataUsageRequest localDataUsageRequest = new DataUsageRequest(0, createTemplate(paramInt, paramString), paramLong);
      try
      {
        paramString = new CallbackHandler(paramHandler, paramInt, paramString, paramUsageCallback);
        UsageCallback.-set0(paramUsageCallback, this.mService.registerUsageCallback(this.mContext.getOpPackageName(), localDataUsageRequest, new Messenger(paramString), new Binder()));
        if (UsageCallback.-get0(paramUsageCallback) == null) {
          Log.e("NetworkStatsManager", "Request from callback is null; should not happen");
        }
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
  }
  
  public void unregisterUsageCallback(UsageCallback paramUsageCallback)
  {
    if ((paramUsageCallback == null) || (UsageCallback.-get0(paramUsageCallback) == null)) {}
    while (UsageCallback.-get0(paramUsageCallback).requestId == 0) {
      throw new IllegalArgumentException("Invalid UsageCallback");
    }
    try
    {
      this.mService.unregisterUsageRequest(UsageCallback.-get0(paramUsageCallback));
      return;
    }
    catch (RemoteException paramUsageCallback)
    {
      throw paramUsageCallback.rethrowFromSystemServer();
    }
  }
  
  private static class CallbackHandler
    extends Handler
  {
    private NetworkStatsManager.UsageCallback mCallback;
    private final int mNetworkType;
    private final String mSubscriberId;
    
    CallbackHandler(Looper paramLooper, int paramInt, String paramString, NetworkStatsManager.UsageCallback paramUsageCallback)
    {
      super();
      this.mNetworkType = paramInt;
      this.mSubscriberId = paramString;
      this.mCallback = paramUsageCallback;
    }
    
    private static Object getObject(Message paramMessage, String paramString)
    {
      return paramMessage.getData().getParcelable(paramString);
    }
    
    public void handleMessage(Message paramMessage)
    {
      DataUsageRequest localDataUsageRequest = (DataUsageRequest)getObject(paramMessage, "DataUsageRequest");
      switch (paramMessage.what)
      {
      default: 
        return;
      case 0: 
        if (this.mCallback != null)
        {
          this.mCallback.onThresholdReached(this.mNetworkType, this.mSubscriberId);
          return;
        }
        Log.e("NetworkStatsManager", "limit reached with released callback for " + localDataUsageRequest);
        return;
      }
      this.mCallback = null;
    }
  }
  
  public static abstract class UsageCallback
  {
    private DataUsageRequest request;
    
    public abstract void onThresholdReached(int paramInt, String paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/NetworkStatsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */