package com.android.server.location;

import android.content.Context;
import android.hardware.location.IFusedLocationHardware;
import android.location.IFusedProvider;
import android.location.IFusedProvider.Stub;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.ServiceWatcher;

public final class FusedProxy
{
  private final String TAG = "FusedProxy";
  private final FusedLocationHardwareSecure mLocationHardware;
  private final ServiceWatcher mServiceWatcher;
  
  private FusedProxy(Context paramContext, Handler paramHandler, IFusedLocationHardware paramIFusedLocationHardware, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mLocationHardware = new FusedLocationHardwareSecure(paramIFusedLocationHardware, paramContext, "android.permission.LOCATION_HARDWARE");
    this.mServiceWatcher = new ServiceWatcher(paramContext, "FusedProxy", "com.android.location.service.FusedProvider", paramInt1, paramInt2, paramInt3, new Runnable()
    {
      public void run()
      {
        FusedProxy.-wrap0(FusedProxy.this, FusedProxy.-get0(FusedProxy.this));
      }
    }, paramHandler);
  }
  
  private void bindProvider(IFusedLocationHardware paramIFusedLocationHardware)
  {
    IFusedProvider localIFusedProvider = IFusedProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
    if (localIFusedProvider == null)
    {
      Log.e("FusedProxy", "No instance of FusedProvider found on FusedLocationHardware connected.");
      return;
    }
    try
    {
      localIFusedProvider.onFusedLocationHardwareChange(paramIFusedLocationHardware);
      return;
    }
    catch (RemoteException paramIFusedLocationHardware)
    {
      Log.e("FusedProxy", paramIFusedLocationHardware.toString());
    }
  }
  
  public static FusedProxy createAndBind(Context paramContext, Handler paramHandler, IFusedLocationHardware paramIFusedLocationHardware, int paramInt1, int paramInt2, int paramInt3)
  {
    paramContext = new FusedProxy(paramContext, paramHandler, paramIFusedLocationHardware, paramInt1, paramInt2, paramInt3);
    if (!paramContext.mServiceWatcher.start()) {
      return null;
    }
    return paramContext;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/FusedProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */