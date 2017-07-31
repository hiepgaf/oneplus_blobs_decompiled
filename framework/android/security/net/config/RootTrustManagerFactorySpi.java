package android.security.net.config;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

public class RootTrustManagerFactorySpi
  extends TrustManagerFactorySpi
{
  private ApplicationConfig mApplicationConfig;
  private NetworkSecurityConfig mConfig;
  
  public TrustManager[] engineGetTrustManagers()
  {
    if (this.mApplicationConfig == null) {
      throw new IllegalStateException("TrustManagerFactory not initialized");
    }
    return new TrustManager[] { this.mApplicationConfig.getTrustManager() };
  }
  
  public void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException
  {
    if (paramKeyStore != null)
    {
      this.mApplicationConfig = new ApplicationConfig(new KeyStoreConfigSource(paramKeyStore));
      return;
    }
    this.mApplicationConfig = ApplicationConfig.getDefaultInstance();
  }
  
  public void engineInit(ManagerFactoryParameters paramManagerFactoryParameters)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramManagerFactoryParameters instanceof ApplicationConfigParameters)) {
      throw new InvalidAlgorithmParameterException("Unsupported spec: " + paramManagerFactoryParameters + ". Only " + ApplicationConfigParameters.class.getName() + " supported");
    }
    this.mApplicationConfig = ((ApplicationConfigParameters)paramManagerFactoryParameters).config;
  }
  
  public static final class ApplicationConfigParameters
    implements ManagerFactoryParameters
  {
    public final ApplicationConfig config;
    
    public ApplicationConfigParameters(ApplicationConfig paramApplicationConfig)
    {
      this.config = paramApplicationConfig;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/RootTrustManagerFactorySpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */