package android.security.net.config;

import libcore.net.NetworkSecurityPolicy;

public class ConfigNetworkSecurityPolicy
  extends NetworkSecurityPolicy
{
  private final ApplicationConfig mConfig;
  
  public ConfigNetworkSecurityPolicy(ApplicationConfig paramApplicationConfig)
  {
    this.mConfig = paramApplicationConfig;
  }
  
  public boolean isCleartextTrafficPermitted()
  {
    return this.mConfig.isCleartextTrafficPermitted();
  }
  
  public boolean isCleartextTrafficPermitted(String paramString)
  {
    return this.mConfig.isCleartextTrafficPermitted(paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/ConfigNetworkSecurityPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */