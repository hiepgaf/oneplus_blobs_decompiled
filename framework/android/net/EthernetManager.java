package android.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class EthernetManager
{
  private static final int MSG_AVAILABILITY_CHANGED = 1000;
  private static final String TAG = "EthernetManager";
  private final Context mContext;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (paramAnonymousMessage.what == 1000)
      {
        if (paramAnonymousMessage.arg1 == 1) {}
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousMessage = EthernetManager.-get1(EthernetManager.this).iterator();
          while (paramAnonymousMessage.hasNext()) {
            ((EthernetManager.Listener)paramAnonymousMessage.next()).onAvailabilityChanged(bool);
          }
        }
      }
    }
  };
  private final ArrayList<Listener> mListeners = new ArrayList();
  private final IEthernetManager mService;
  private final IEthernetServiceListener.Stub mServiceListener = new IEthernetServiceListener.Stub()
  {
    public void onAvailabilityChanged(boolean paramAnonymousBoolean)
    {
      Handler localHandler = EthernetManager.-get0(EthernetManager.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(1000, i, 0, null).sendToTarget();
        return;
      }
    }
  };
  
  public EthernetManager(Context paramContext, IEthernetManager paramIEthernetManager)
  {
    this.mContext = paramContext;
    this.mService = paramIEthernetManager;
  }
  
  public void addListener(Listener paramListener)
  {
    if (paramListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    this.mListeners.add(paramListener);
    if (this.mListeners.size() == 1) {}
    try
    {
      this.mService.addListener(this.mServiceListener);
      return;
    }
    catch (RemoteException paramListener)
    {
      throw paramListener.rethrowFromSystemServer();
    }
  }
  
  public IpConfiguration getConfiguration()
  {
    try
    {
      IpConfiguration localIpConfiguration = this.mService.getConfiguration();
      return localIpConfiguration;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isAvailable()
  {
    try
    {
      boolean bool = this.mService.isAvailable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void removeListener(Listener paramListener)
  {
    if (paramListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    this.mListeners.remove(paramListener);
    if (this.mListeners.isEmpty()) {}
    try
    {
      this.mService.removeListener(this.mServiceListener);
      return;
    }
    catch (RemoteException paramListener)
    {
      throw paramListener.rethrowFromSystemServer();
    }
  }
  
  public void setConfiguration(IpConfiguration paramIpConfiguration)
  {
    try
    {
      this.mService.setConfiguration(paramIpConfiguration);
      return;
    }
    catch (RemoteException paramIpConfiguration)
    {
      throw paramIpConfiguration.rethrowFromSystemServer();
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onAvailabilityChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/EthernetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */