package android.security.net.config;

import android.content.Context;
import java.security.Provider;
import java.security.Security;
import libcore.net.NetworkSecurityPolicy;

public final class NetworkSecurityConfigProvider
  extends Provider
{
  private static final String PREFIX = NetworkSecurityConfigProvider.class.getPackage().getName() + ".";
  
  public NetworkSecurityConfigProvider()
  {
    super("AndroidNSSP", 1.0D, "Android Network Security Policy Provider");
    put("TrustManagerFactory.PKIX", PREFIX + "RootTrustManagerFactorySpi");
    put("Alg.Alias.TrustManagerFactory.X509", "PKIX");
  }
  
  public static void install(Context paramContext)
  {
    paramContext = new ApplicationConfig(new ManifestConfigSource(paramContext));
    ApplicationConfig.setDefaultInstance(paramContext);
    int i = Security.insertProviderAt(new NetworkSecurityConfigProvider(), 1);
    if (i != 1) {
      throw new RuntimeException("Failed to install provider as highest priority provider. Provider was installed at position " + i);
    }
    NetworkSecurityPolicy.setInstance(new ConfigNetworkSecurityPolicy(paramContext));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/NetworkSecurityConfigProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */