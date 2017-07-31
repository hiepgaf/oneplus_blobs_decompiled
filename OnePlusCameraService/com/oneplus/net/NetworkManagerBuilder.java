package com.oneplus.net;

import com.oneplus.base.BaseAppComponentBuilder;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentCreationPriority;

public final class NetworkManagerBuilder
  extends BaseAppComponentBuilder
{
  public NetworkManagerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, NetworkManagerImpl.class);
  }
  
  protected Component create(BaseApplication paramBaseApplication)
  {
    return new NetworkManagerImpl(paramBaseApplication);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/net/NetworkManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */