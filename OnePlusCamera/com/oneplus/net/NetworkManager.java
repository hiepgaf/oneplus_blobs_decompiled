package com.oneplus.net;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface NetworkManager
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_NETWORK_CONNECTED = new PropertyKey("IsNetworkConnected", Boolean.class, NetworkManager.class, Boolean.valueOf(false));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/net/NetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */