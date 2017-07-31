package android.security.net.config;

import android.util.Pair;
import java.util.Set;

public abstract interface ConfigSource
{
  public abstract NetworkSecurityConfig getDefaultConfig();
  
  public abstract Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/ConfigSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */