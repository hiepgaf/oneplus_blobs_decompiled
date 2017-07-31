package android.security.net.config;

import android.util.Pair;
import java.security.KeyStore;
import java.util.Set;

class KeyStoreConfigSource
  implements ConfigSource
{
  private final NetworkSecurityConfig mConfig;
  
  public KeyStoreConfigSource(KeyStore paramKeyStore)
  {
    this.mConfig = new NetworkSecurityConfig.Builder().addCertificatesEntryRef(new CertificatesEntryRef(new KeyStoreCertificateSource(paramKeyStore), false)).build();
  }
  
  public NetworkSecurityConfig getDefaultConfig()
  {
    return this.mConfig;
  }
  
  public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs()
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/KeyStoreConfigSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */