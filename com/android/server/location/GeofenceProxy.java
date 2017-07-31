package com.android.server.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.location.GeofenceHardwareService;
import android.hardware.location.IGeofenceHardware;
import android.hardware.location.IGeofenceHardware.Stub;
import android.location.IFusedGeofenceHardware;
import android.location.IGeofenceProvider;
import android.location.IGeofenceProvider.Stub;
import android.location.IGpsGeofenceHardware;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.server.ServiceWatcher;

public final class GeofenceProxy
{
  private static final int GEOFENCE_GPS_HARDWARE_CONNECTED = 4;
  private static final int GEOFENCE_GPS_HARDWARE_DISCONNECTED = 5;
  private static final int GEOFENCE_HARDWARE_CONNECTED = 2;
  private static final int GEOFENCE_HARDWARE_DISCONNECTED = 3;
  private static final int GEOFENCE_PROVIDER_CONNECTED = 1;
  private static final String SERVICE_ACTION = "com.android.location.service.GeofenceProvider";
  private static final String TAG = "GeofenceProxy";
  private final Context mContext;
  private final IFusedGeofenceHardware mFusedGeofenceHardware;
  private IGeofenceHardware mGeofenceHardware;
  private final IGpsGeofenceHardware mGpsGeofenceHardware;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Object localObject;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        localObject = GeofenceProxy.-get2(GeofenceProxy.this);
        paramAnonymousMessage = (Message)localObject;
      }
      for (;;)
      {
        try
        {
          if (GeofenceProxy.-get0(GeofenceProxy.this) != null)
          {
            GeofenceProxy.-wrap1(GeofenceProxy.this);
            paramAnonymousMessage = (Message)localObject;
          }
          return;
        }
        finally
        {
          paramAnonymousMessage = finally;
          throw paramAnonymousMessage;
        }
        localObject = GeofenceProxy.-get2(GeofenceProxy.this);
        paramAnonymousMessage = (Message)localObject;
        try
        {
          if (GeofenceProxy.-get0(GeofenceProxy.this) == null) {
            continue;
          }
          GeofenceProxy.-wrap2(GeofenceProxy.this);
          GeofenceProxy.-wrap0(GeofenceProxy.this);
          GeofenceProxy.-wrap1(GeofenceProxy.this);
          paramAnonymousMessage = (Message)localObject;
        }
        finally {}
        paramAnonymousMessage = (Message)localObject;
        try
        {
          if (GeofenceProxy.-get0(GeofenceProxy.this) != null) {
            continue;
          }
          GeofenceProxy.-wrap1(GeofenceProxy.this);
          paramAnonymousMessage = (Message)localObject;
        }
        finally {}
      }
    }
  };
  private final Object mLock = new Object();
  private Runnable mRunnable = new Runnable()
  {
    public void run()
    {
      GeofenceProxy.-get1(GeofenceProxy.this).sendEmptyMessage(1);
    }
  };
  private ServiceConnection mServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName arg1, IBinder paramAnonymousIBinder)
    {
      synchronized (GeofenceProxy.-get2(GeofenceProxy.this))
      {
        GeofenceProxy.-set0(GeofenceProxy.this, IGeofenceHardware.Stub.asInterface(paramAnonymousIBinder));
        GeofenceProxy.-get1(GeofenceProxy.this).sendEmptyMessage(2);
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (GeofenceProxy.-get2(GeofenceProxy.this))
      {
        GeofenceProxy.-set0(GeofenceProxy.this, null);
        GeofenceProxy.-get1(GeofenceProxy.this).sendEmptyMessage(3);
        return;
      }
    }
  };
  private final ServiceWatcher mServiceWatcher;
  
  private GeofenceProxy(Context paramContext, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler, IGpsGeofenceHardware paramIGpsGeofenceHardware, IFusedGeofenceHardware paramIFusedGeofenceHardware)
  {
    this.mContext = paramContext;
    this.mServiceWatcher = new ServiceWatcher(paramContext, "GeofenceProxy", "com.android.location.service.GeofenceProvider", paramInt1, paramInt2, paramInt3, this.mRunnable, paramHandler);
    this.mGpsGeofenceHardware = paramIGpsGeofenceHardware;
    this.mFusedGeofenceHardware = paramIFusedGeofenceHardware;
    bindHardwareGeofence();
  }
  
  private boolean bindGeofenceProvider()
  {
    return this.mServiceWatcher.start();
  }
  
  private void bindHardwareGeofence()
  {
    this.mContext.bindServiceAsUser(new Intent(this.mContext, GeofenceHardwareService.class), this.mServiceConnection, 1, UserHandle.SYSTEM);
  }
  
  public static GeofenceProxy createAndBind(Context paramContext, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler, IGpsGeofenceHardware paramIGpsGeofenceHardware, IFusedGeofenceHardware paramIFusedGeofenceHardware)
  {
    paramContext = new GeofenceProxy(paramContext, paramInt1, paramInt2, paramInt3, paramHandler, paramIGpsGeofenceHardware, paramIFusedGeofenceHardware);
    if (paramContext.bindGeofenceProvider()) {
      return paramContext;
    }
    return null;
  }
  
  private void setFusedGeofenceLocked()
  {
    try
    {
      this.mGeofenceHardware.setFusedGeofenceHardware(this.mFusedGeofenceHardware);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("GeofenceProxy", "Error while connecting to GeofenceHardwareService");
    }
  }
  
  private void setGeofenceHardwareInProviderLocked()
  {
    try
    {
      IGeofenceProvider localIGeofenceProvider = IGeofenceProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
      if (localIGeofenceProvider != null) {
        localIGeofenceProvider.setGeofenceHardware(this.mGeofenceHardware);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("GeofenceProxy", "Remote Exception: setGeofenceHardwareInProviderLocked: " + localRemoteException);
    }
  }
  
  private void setGpsGeofenceLocked()
  {
    try
    {
      if (this.mGpsGeofenceHardware != null) {
        this.mGeofenceHardware.setGpsGeofenceHardware(this.mGpsGeofenceHardware);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("GeofenceProxy", "Error while connecting to GeofenceHardwareService");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GeofenceProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */