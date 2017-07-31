package android.security;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.security.net.config.ApplicationConfig;
import android.security.net.config.ManifestConfigSource;

public class NetworkSecurityPolicy
{
  private static final NetworkSecurityPolicy INSTANCE = new NetworkSecurityPolicy();
  
  public static ApplicationConfig getApplicationConfigForPackage(Context paramContext, String paramString)
    throws PackageManager.NameNotFoundException
  {
    return new ApplicationConfig(new ManifestConfigSource(paramContext.createPackageContext(paramString, 0)));
  }
  
  public static NetworkSecurityPolicy getInstance()
  {
    return INSTANCE;
  }
  
  public void handleTrustStorageUpdate()
  {
    ApplicationConfig.getDefaultInstance().handleTrustStorageUpdate();
  }
  
  public boolean isCleartextTrafficPermitted()
  {
    return libcore.net.NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted();
  }
  
  public boolean isCleartextTrafficPermitted(String paramString)
  {
    return libcore.net.NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(paramString);
  }
  
  public void setCleartextTrafficPermitted(boolean paramBoolean)
  {
    libcore.net.NetworkSecurityPolicy.setInstance(new FrameworkNetworkSecurityPolicy(paramBoolean));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/NetworkSecurityPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */