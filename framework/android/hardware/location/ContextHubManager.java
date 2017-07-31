package android.hardware.location;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public final class ContextHubManager
{
  private static final String TAG = "ContextHubManager";
  private Callback mCallback;
  private Handler mCallbackHandler;
  private IContextHubCallback.Stub mClientCallback = new IContextHubCallback.Stub()
  {
    public void onMessageReceipt(final int paramAnonymousInt1, final int paramAnonymousInt2, final ContextHubMessage paramAnonymousContextHubMessage)
    {
      if (ContextHubManager.-get0(ContextHubManager.this) != null) {}
      for (;;)
      {
        try
        {
          final ContextHubManager.Callback localCallback = ContextHubManager.-get0(ContextHubManager.this);
          if (ContextHubManager.-get1(ContextHubManager.this) == null)
          {
            localHandler = new Handler(ContextHubManager.-get3(ContextHubManager.this));
            localHandler.post(new Runnable()
            {
              public void run()
              {
                localCallback.onMessageReceipt(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousContextHubMessage);
              }
            });
            return;
          }
          Handler localHandler = ContextHubManager.-get1(ContextHubManager.this);
          continue;
          if (ContextHubManager.-get2(ContextHubManager.this) == null) {
            break label121;
          }
        }
        finally {}
        label121:
        try
        {
          ContextHubManager.-get2(ContextHubManager.this).onMessageReceipt(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousContextHubMessage);
        }
        finally {}
      }
    }
  };
  private IContextHubService mContextHubService;
  @Deprecated
  private ICallback mLocalCallback;
  private final Looper mMainLooper;
  
  public ContextHubManager(Context paramContext, Looper paramLooper)
  {
    this.mMainLooper = paramLooper;
    paramContext = ServiceManager.getService("contexthub_service");
    if (paramContext != null)
    {
      this.mContextHubService = IContextHubService.Stub.asInterface(paramContext);
      try
      {
        getBinder().registerCallback(this.mClientCallback);
        return;
      }
      catch (RemoteException paramContext)
      {
        Log.w("ContextHubManager", "Could not register callback:" + paramContext);
        return;
      }
    }
    Log.w("ContextHubManager", "failed to getService");
  }
  
  private IContextHubService getBinder()
    throws RemoteException
  {
    if (this.mContextHubService == null) {
      throw new RemoteException("Service not connected.");
    }
    return this.mContextHubService;
  }
  
  public int[] findNanoAppOnHub(int paramInt, NanoAppFilter paramNanoAppFilter)
  {
    try
    {
      paramNanoAppFilter = getBinder().findNanoAppOnHub(paramInt, paramNanoAppFilter);
      return paramNanoAppFilter;
    }
    catch (RemoteException paramNanoAppFilter)
    {
      Log.w("ContextHubManager", "Could not query nanoApp instance :" + paramNanoAppFilter);
    }
    return null;
  }
  
  public int[] getContextHubHandles()
  {
    try
    {
      int[] arrayOfInt = getBinder().getContextHubHandles();
      return arrayOfInt;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("ContextHubManager", "Could not fetch context hub handles : " + localRemoteException);
    }
    return null;
  }
  
  public ContextHubInfo getContextHubInfo(int paramInt)
  {
    try
    {
      ContextHubInfo localContextHubInfo = getBinder().getContextHubInfo(paramInt);
      return localContextHubInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("ContextHubManager", "Could not fetch context hub info :" + localRemoteException);
    }
    return null;
  }
  
  public NanoAppInstanceInfo getNanoAppInstanceInfo(int paramInt)
  {
    try
    {
      NanoAppInstanceInfo localNanoAppInstanceInfo = getBinder().getNanoAppInstanceInfo(paramInt);
      return localNanoAppInstanceInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("ContextHubManager", "Could not fetch nanoApp info :" + localRemoteException);
    }
    return null;
  }
  
  public int loadNanoApp(int paramInt, NanoApp paramNanoApp)
  {
    if (paramNanoApp == null) {
      return -1;
    }
    try
    {
      paramInt = getBinder().loadNanoApp(paramInt, paramNanoApp);
      return paramInt;
    }
    catch (RemoteException paramNanoApp)
    {
      Log.w("ContextHubManager", "Could not load nanoApp :" + paramNanoApp);
    }
    return -1;
  }
  
  public int registerCallback(Callback paramCallback)
  {
    return registerCallback(paramCallback, null);
  }
  
  public int registerCallback(Callback paramCallback, Handler paramHandler)
  {
    try
    {
      if (this.mCallback != null)
      {
        Log.w("ContextHubManager", "Max number of callbacks reached!");
        return -1;
      }
      this.mCallback = paramCallback;
      this.mCallbackHandler = paramHandler;
      return 0;
    }
    finally {}
  }
  
  @Deprecated
  public int registerCallback(ICallback paramICallback)
  {
    if (this.mLocalCallback != null)
    {
      Log.w("ContextHubManager", "Max number of local callbacks reached!");
      return -1;
    }
    this.mLocalCallback = paramICallback;
    return 0;
  }
  
  public int sendMessage(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage)
  {
    if ((paramContextHubMessage == null) || (paramContextHubMessage.getData() == null))
    {
      Log.w("ContextHubManager", "null ptr");
      return -1;
    }
    try
    {
      paramInt1 = getBinder().sendMessage(paramInt1, paramInt2, paramContextHubMessage);
      return paramInt1;
    }
    catch (RemoteException paramContextHubMessage)
    {
      Log.w("ContextHubManager", "Could not send message :" + paramContextHubMessage.toString());
    }
    return -1;
  }
  
  public int unloadNanoApp(int paramInt)
  {
    try
    {
      paramInt = getBinder().unloadNanoApp(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("ContextHubManager", "Could not fetch unload nanoApp :" + localRemoteException);
    }
    return -1;
  }
  
  public int unregisterCallback(Callback paramCallback)
  {
    try
    {
      if (paramCallback != this.mCallback)
      {
        Log.w("ContextHubManager", "Cannot recognize callback!");
        return -1;
      }
      this.mCallback = null;
      this.mCallbackHandler = null;
      return 0;
    }
    finally {}
  }
  
  public int unregisterCallback(ICallback paramICallback)
  {
    try
    {
      if (paramICallback != this.mLocalCallback)
      {
        Log.w("ContextHubManager", "Cannot recognize local callback!");
        return -1;
      }
      this.mLocalCallback = null;
      return 0;
    }
    finally {}
  }
  
  public static abstract class Callback
  {
    public abstract void onMessageReceipt(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage);
  }
  
  @Deprecated
  public static abstract interface ICallback
  {
    public abstract void onMessageReceipt(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ContextHubManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */