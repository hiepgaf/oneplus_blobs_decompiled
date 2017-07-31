package com.oneplus.net;

import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Message;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.component.BasicComponent;
import java.util.HashSet;
import java.util.Set;

final class NetworkManagerImpl
  extends BasicComponent
  implements NetworkManager
{
  private static final int MSG_NETWORK_AVAILABLE = 10000;
  private static final int MSG_NETWORK_LOST = 10001;
  private final Set<Network> m_AvailableNetworks = new HashSet();
  private ConnectivityManager m_ConnectivityManager;
  private final ConnectivityManager.NetworkCallback m_NetworkCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onAvailable(Network paramAnonymousNetwork)
    {
      if (NetworkManagerImpl.this.isDependencyThread())
      {
        NetworkManagerImpl.-wrap0(NetworkManagerImpl.this, paramAnonymousNetwork);
        return;
      }
      HandlerUtils.sendMessage(NetworkManagerImpl.this, 10000, paramAnonymousNetwork);
    }
    
    public void onLost(Network paramAnonymousNetwork)
    {
      if (NetworkManagerImpl.this.isDependencyThread())
      {
        NetworkManagerImpl.-wrap1(NetworkManagerImpl.this, paramAnonymousNetwork);
        return;
      }
      HandlerUtils.sendMessage(NetworkManagerImpl.this, 10001, paramAnonymousNetwork);
    }
  };
  
  NetworkManagerImpl(BaseApplication paramBaseApplication)
  {
    super("Network manager", paramBaseApplication, true);
    enablePropertyLogs(PROP_IS_NETWORK_CONNECTED, 1);
  }
  
  private void onNetworkAvailable(Network paramNetwork)
  {
    this.m_AvailableNetworks.add(paramNetwork);
    Log.d(this.TAG, "onNetworkAvailable() - ", paramNetwork, ", network count : ", Integer.valueOf(this.m_AvailableNetworks.size()));
    if (this.m_AvailableNetworks.size() == 1) {
      setReadOnly(PROP_IS_NETWORK_CONNECTED, Boolean.valueOf(true));
    }
  }
  
  private void onNetworkLost(Network paramNetwork)
  {
    if (!this.m_AvailableNetworks.remove(paramNetwork)) {
      return;
    }
    Log.d(this.TAG, "onNetworkLost() - ", paramNetwork, ", network count : ", Integer.valueOf(this.m_AvailableNetworks.size()));
    if (this.m_AvailableNetworks.isEmpty()) {
      setReadOnly(PROP_IS_NETWORK_CONNECTED, Boolean.valueOf(false));
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10000: 
      onNetworkAvailable((Network)paramMessage.obj);
      return;
    }
    onNetworkLost((Network)paramMessage.obj);
  }
  
  protected void onDeinitialize()
  {
    if (this.m_ConnectivityManager != null)
    {
      this.m_ConnectivityManager.unregisterNetworkCallback(this.m_NetworkCallback);
      this.m_ConnectivityManager = null;
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_ConnectivityManager = ((ConnectivityManager)BaseApplication.current().getSystemService("connectivity"));
    if (this.m_ConnectivityManager != null)
    {
      NetworkRequest localNetworkRequest = new NetworkRequest.Builder().addCapability(12).build();
      this.m_ConnectivityManager.registerNetworkCallback(localNetworkRequest, this.m_NetworkCallback);
      return;
    }
    Log.e(this.TAG, "onInitialize() - No ConnectivityManager");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/net/NetworkManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */