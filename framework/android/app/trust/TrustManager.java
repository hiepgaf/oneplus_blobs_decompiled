package android.app.trust;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;

public class TrustManager
{
  private static final String DATA_FLAGS = "initiatedByUser";
  private static final int MSG_TRUST_CHANGED = 1;
  private static final int MSG_TRUST_MANAGED_CHANGED = 2;
  private static final String TAG = "TrustManager";
  private final Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        int i;
        if (paramAnonymousMessage.peekData() != null)
        {
          i = paramAnonymousMessage.peekData().getInt("initiatedByUser");
          localTrustListener = (TrustManager.TrustListener)paramAnonymousMessage.obj;
          if (paramAnonymousMessage.arg1 == 0) {
            break label85;
          }
        }
        for (;;)
        {
          localTrustListener.onTrustChanged(bool1, paramAnonymousMessage.arg2, i);
          return;
          i = 0;
          break;
          label85:
          bool1 = false;
        }
      }
      TrustManager.TrustListener localTrustListener = (TrustManager.TrustListener)paramAnonymousMessage.obj;
      if (paramAnonymousMessage.arg1 != 0) {}
      for (bool1 = bool2;; bool1 = false)
      {
        localTrustListener.onTrustManagedChanged(bool1, paramAnonymousMessage.arg2);
        return;
      }
    }
  };
  private final ITrustManager mService;
  private final ArrayMap<TrustListener, ITrustListener> mTrustListeners;
  
  public TrustManager(IBinder paramIBinder)
  {
    this.mService = ITrustManager.Stub.asInterface(paramIBinder);
    this.mTrustListeners = new ArrayMap();
  }
  
  public boolean isTrustUsuallyManaged(int paramInt)
  {
    try
    {
      boolean bool = this.mService.isTrustUsuallyManaged(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void registerTrustListener(final TrustListener paramTrustListener)
  {
    try
    {
      ITrustListener.Stub local2 = new ITrustListener.Stub()
      {
        public void onTrustChanged(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = 0;
          Object localObject = TrustManager.-get0(TrustManager.this);
          if (paramAnonymousBoolean) {
            i = 1;
          }
          localObject = ((Handler)localObject).obtainMessage(1, i, paramAnonymousInt1, paramTrustListener);
          if (paramAnonymousInt2 != 0) {
            ((Message)localObject).getData().putInt("initiatedByUser", paramAnonymousInt2);
          }
          ((Message)localObject).sendToTarget();
        }
        
        public void onTrustManagedChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
        {
          Handler localHandler = TrustManager.-get0(TrustManager.this);
          if (paramAnonymousBoolean) {}
          for (int i = 1;; i = 0)
          {
            localHandler.obtainMessage(2, i, paramAnonymousInt, paramTrustListener).sendToTarget();
            return;
          }
        }
      };
      this.mService.registerTrustListener(local2);
      this.mTrustListeners.put(paramTrustListener, local2);
      return;
    }
    catch (RemoteException paramTrustListener)
    {
      throw paramTrustListener.rethrowFromSystemServer();
    }
  }
  
  public void reportEnabledTrustAgentsChanged(int paramInt)
  {
    try
    {
      this.mService.reportEnabledTrustAgentsChanged(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportKeyguardShowingChanged()
  {
    try
    {
      this.mService.reportKeyguardShowingChanged();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportUnlockAttempt(boolean paramBoolean, int paramInt)
  {
    try
    {
      this.mService.reportUnlockAttempt(paramBoolean, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setDeviceLockedForUser(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setDeviceLockedForUser(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unregisterTrustListener(TrustListener paramTrustListener)
  {
    paramTrustListener = (ITrustListener)this.mTrustListeners.remove(paramTrustListener);
    if (paramTrustListener != null) {}
    try
    {
      this.mService.unregisterTrustListener(paramTrustListener);
      return;
    }
    catch (RemoteException paramTrustListener)
    {
      throw paramTrustListener.rethrowFromSystemServer();
    }
  }
  
  public static abstract interface TrustListener
  {
    public abstract void onTrustChanged(boolean paramBoolean, int paramInt1, int paramInt2);
    
    public abstract void onTrustManagedChanged(boolean paramBoolean, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/trust/TrustManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */