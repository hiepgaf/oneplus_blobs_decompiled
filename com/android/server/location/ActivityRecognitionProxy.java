package com.android.server.location;

import android.content.Context;
import android.hardware.location.ActivityRecognitionHardware;
import android.hardware.location.IActivityRecognitionHardwareClient;
import android.hardware.location.IActivityRecognitionHardwareClient.Stub;
import android.hardware.location.IActivityRecognitionHardwareWatcher;
import android.hardware.location.IActivityRecognitionHardwareWatcher.Stub;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.ServiceWatcher;

public class ActivityRecognitionProxy
{
  private static final String TAG = "ActivityRecognitionProxy";
  private final ActivityRecognitionHardware mInstance;
  private final boolean mIsSupported;
  private final ServiceWatcher mServiceWatcher;
  
  private ActivityRecognitionProxy(Context paramContext, Handler paramHandler, boolean paramBoolean, ActivityRecognitionHardware paramActivityRecognitionHardware, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mIsSupported = paramBoolean;
    this.mInstance = paramActivityRecognitionHardware;
    this.mServiceWatcher = new ServiceWatcher(paramContext, "ActivityRecognitionProxy", "com.android.location.service.ActivityRecognitionProvider", paramInt1, paramInt2, paramInt3, new Runnable()
    {
      public void run()
      {
        ActivityRecognitionProxy.-wrap0(ActivityRecognitionProxy.this);
      }
    }, paramHandler);
  }
  
  private void bindProvider()
  {
    Object localObject = this.mServiceWatcher.getBinder();
    if (localObject == null)
    {
      Log.e("ActivityRecognitionProxy", "Null binder found on connection.");
      return;
    }
    String str;
    try
    {
      str = ((IBinder)localObject).getInterfaceDescriptor();
      if (!IActivityRecognitionHardwareWatcher.class.getCanonicalName().equals(str)) {
        break label107;
      }
      localObject = IActivityRecognitionHardwareWatcher.Stub.asInterface((IBinder)localObject);
      if (localObject == null)
      {
        Log.e("ActivityRecognitionProxy", "No watcher found on connection.");
        return;
      }
    }
    catch (RemoteException localRemoteException1)
    {
      Log.e("ActivityRecognitionProxy", "Unable to get interface descriptor.", localRemoteException1);
      return;
    }
    if (this.mInstance == null)
    {
      Log.d("ActivityRecognitionProxy", "AR HW instance not available, binding will be a no-op.");
      return;
    }
    try
    {
      localRemoteException1.onInstanceChanged(this.mInstance);
      return;
    }
    catch (RemoteException localRemoteException2)
    {
      Log.e("ActivityRecognitionProxy", "Error delivering hardware interface to watcher.", localRemoteException2);
      return;
    }
    label107:
    if (IActivityRecognitionHardwareClient.class.getCanonicalName().equals(str))
    {
      IActivityRecognitionHardwareClient localIActivityRecognitionHardwareClient = IActivityRecognitionHardwareClient.Stub.asInterface(localRemoteException2);
      if (localIActivityRecognitionHardwareClient == null)
      {
        Log.e("ActivityRecognitionProxy", "No client found on connection.");
        return;
      }
      try
      {
        localIActivityRecognitionHardwareClient.onAvailabilityChanged(this.mIsSupported, this.mInstance);
        return;
      }
      catch (RemoteException localRemoteException3)
      {
        Log.e("ActivityRecognitionProxy", "Error delivering hardware interface to client.", localRemoteException3);
        return;
      }
    }
    Log.e("ActivityRecognitionProxy", "Invalid descriptor found on connection: " + str);
  }
  
  public static ActivityRecognitionProxy createAndBind(Context paramContext, Handler paramHandler, boolean paramBoolean, ActivityRecognitionHardware paramActivityRecognitionHardware, int paramInt1, int paramInt2, int paramInt3)
  {
    paramContext = new ActivityRecognitionProxy(paramContext, paramHandler, paramBoolean, paramActivityRecognitionHardware, paramInt1, paramInt2, paramInt3);
    if (!paramContext.mServiceWatcher.start())
    {
      Log.e("ActivityRecognitionProxy", "ServiceWatcher could not start.");
      return null;
    }
    return paramContext;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/ActivityRecognitionProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */