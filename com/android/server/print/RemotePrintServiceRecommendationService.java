package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.printservice.recommendation.IRecommendationService;
import android.printservice.recommendation.IRecommendationService.Stub;
import android.printservice.recommendation.IRecommendationServiceCallbacks.Stub;
import android.printservice.recommendation.RecommendationInfo;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import java.util.List;

class RemotePrintServiceRecommendationService
{
  private static final String LOG_TAG = "RemotePrintServiceRecS";
  @GuardedBy("mLock")
  private final Connection mConnection;
  private final Context mContext;
  @GuardedBy("mLock")
  private boolean mIsBound;
  private final Object mLock = new Object();
  @GuardedBy("mLock")
  private IRecommendationService mService;
  
  RemotePrintServiceRecommendationService(Context arg1, UserHandle paramUserHandle, RemotePrintServiceRecommendationServiceCallbacks paramRemotePrintServiceRecommendationServiceCallbacks)
  {
    this.mContext = ???;
    this.mConnection = new Connection(paramRemotePrintServiceRecommendationServiceCallbacks);
    try
    {
      paramRemotePrintServiceRecommendationServiceCallbacks = getServiceIntent(paramUserHandle);
      synchronized (this.mLock)
      {
        this.mIsBound = this.mContext.bindServiceAsUser(paramRemotePrintServiceRecommendationServiceCallbacks, this.mConnection, 67108865, paramUserHandle);
        if (!this.mIsBound) {
          throw new Exception("Failed to bind to service " + paramRemotePrintServiceRecommendationServiceCallbacks);
        }
      }
    }
    catch (Exception ???)
    {
      Log.e("RemotePrintServiceRecS", "Could not connect to print service recommendation service", ???);
      return;
    }
  }
  
  private Intent getServiceIntent(UserHandle paramUserHandle)
    throws Exception
  {
    paramUserHandle = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.printservice.recommendation.RecommendationService"), 268435588, paramUserHandle.getIdentifier());
    if (paramUserHandle.size() != 1) {
      throw new Exception(paramUserHandle.size() + " instead of exactly one service found");
    }
    Object localObject = (ResolveInfo)paramUserHandle.get(0);
    paramUserHandle = new ComponentName(((ResolveInfo)localObject).serviceInfo.packageName, ((ResolveInfo)localObject).serviceInfo.name);
    ApplicationInfo localApplicationInfo = this.mContext.getPackageManager().getApplicationInfo(((ResolveInfo)localObject).serviceInfo.packageName, 0);
    if (localApplicationInfo == null) {
      throw new Exception("Cannot read appInfo for service");
    }
    if ((localApplicationInfo.flags & 0x1) == 0) {
      throw new Exception("Service is not part of the system");
    }
    if (!"android.permission.BIND_PRINT_RECOMMENDATION_SERVICE".equals(((ResolveInfo)localObject).serviceInfo.permission)) {
      throw new Exception("Service " + paramUserHandle.flattenToShortString() + " does not require permission " + "android.permission.BIND_PRINT_RECOMMENDATION_SERVICE");
    }
    localObject = new Intent();
    ((Intent)localObject).setComponent(paramUserHandle);
    return (Intent)localObject;
  }
  
  void close()
  {
    synchronized (this.mLock)
    {
      IRecommendationService localIRecommendationService = this.mService;
      if (localIRecommendationService != null) {}
      try
      {
        this.mService.registerCallbacks(null);
        this.mService = null;
        if (this.mIsBound)
        {
          this.mContext.unbindService(this.mConnection);
          this.mIsBound = false;
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("RemotePrintServiceRecS", "Could not unregister callbacks", localRemoteException);
        }
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    if ((this.mIsBound) || (this.mService != null))
    {
      Log.w("RemotePrintServiceRecS", "Service still connected on finalize()");
      close();
    }
    super.finalize();
  }
  
  private class Connection
    implements ServiceConnection
  {
    private final RemotePrintServiceRecommendationService.RemotePrintServiceRecommendationServiceCallbacks mCallbacks;
    
    public Connection(RemotePrintServiceRecommendationService.RemotePrintServiceRecommendationServiceCallbacks paramRemotePrintServiceRecommendationServiceCallbacks)
    {
      this.mCallbacks = paramRemotePrintServiceRecommendationServiceCallbacks;
    }
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      synchronized (RemotePrintServiceRecommendationService.-get1(RemotePrintServiceRecommendationService.this))
      {
        RemotePrintServiceRecommendationService.-set0(RemotePrintServiceRecommendationService.this, IRecommendationService.Stub.asInterface(paramIBinder));
        try
        {
          RemotePrintServiceRecommendationService.-get2(RemotePrintServiceRecommendationService.this).registerCallbacks(new IRecommendationServiceCallbacks.Stub()
          {
            public void onRecommendationsUpdated(List<RecommendationInfo> paramAnonymousList)
            {
              synchronized (RemotePrintServiceRecommendationService.-get1(RemotePrintServiceRecommendationService.this))
              {
                if ((RemotePrintServiceRecommendationService.-get0(RemotePrintServiceRecommendationService.this)) && (RemotePrintServiceRecommendationService.-get2(RemotePrintServiceRecommendationService.this) != null))
                {
                  if (paramAnonymousList != null) {
                    Preconditions.checkCollectionElementsNotNull(paramAnonymousList, "recommendation");
                  }
                  RemotePrintServiceRecommendationService.Connection.-get0(RemotePrintServiceRecommendationService.Connection.this).onPrintServiceRecommendationsUpdated(paramAnonymousList);
                }
                return;
              }
            }
          });
          return;
        }
        catch (RemoteException paramIBinder)
        {
          for (;;)
          {
            Log.e("RemotePrintServiceRecS", "Could not register callbacks", paramIBinder);
          }
        }
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      Log.w("RemotePrintServiceRecS", "Unexpected termination of connection");
      synchronized (RemotePrintServiceRecommendationService.-get1(RemotePrintServiceRecommendationService.this))
      {
        RemotePrintServiceRecommendationService.-set0(RemotePrintServiceRecommendationService.this, null);
        return;
      }
    }
  }
  
  public static abstract interface RemotePrintServiceRecommendationServiceCallbacks
  {
    public abstract void onPrintServiceRecommendationsUpdated(List<RecommendationInfo> paramList);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/print/RemotePrintServiceRecommendationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */