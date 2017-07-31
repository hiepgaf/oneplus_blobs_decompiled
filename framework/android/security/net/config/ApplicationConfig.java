package android.security.net.config;

import android.util.Pair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.net.ssl.X509TrustManager;

public final class ApplicationConfig
{
  private static ApplicationConfig sInstance;
  private static Object sLock = new Object();
  private ConfigSource mConfigSource;
  private Set<Pair<Domain, NetworkSecurityConfig>> mConfigs;
  private NetworkSecurityConfig mDefaultConfig;
  private boolean mInitialized;
  private final Object mLock = new Object();
  private X509TrustManager mTrustManager;
  
  public ApplicationConfig(ConfigSource paramConfigSource)
  {
    this.mConfigSource = paramConfigSource;
    this.mInitialized = false;
  }
  
  private void ensureInitialized()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mInitialized;
      if (bool) {
        return;
      }
      this.mConfigs = this.mConfigSource.getPerDomainConfigs();
      this.mDefaultConfig = this.mConfigSource.getDefaultConfig();
      this.mConfigSource = null;
      this.mTrustManager = new RootTrustManager(this);
      this.mInitialized = true;
      return;
    }
  }
  
  public static ApplicationConfig getDefaultInstance()
  {
    synchronized (sLock)
    {
      ApplicationConfig localApplicationConfig = sInstance;
      return localApplicationConfig;
    }
  }
  
  public static void setDefaultInstance(ApplicationConfig paramApplicationConfig)
  {
    synchronized (sLock)
    {
      sInstance = paramApplicationConfig;
      return;
    }
  }
  
  public NetworkSecurityConfig getConfigForHostname(String paramString)
  {
    ensureInitialized();
    if ((paramString == null) || (paramString.isEmpty()) || (this.mConfigs == null)) {
      return this.mDefaultConfig;
    }
    if (paramString.charAt(0) == '.') {
      throw new IllegalArgumentException("hostname must not begin with a .");
    }
    paramString = paramString.toLowerCase(Locale.US);
    String str = paramString;
    if (paramString.charAt(paramString.length() - 1) == '.') {
      str = paramString.substring(0, paramString.length() - 1);
    }
    paramString = null;
    Iterator localIterator = this.mConfigs.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      Domain localDomain = (Domain)localPair.first;
      NetworkSecurityConfig localNetworkSecurityConfig = (NetworkSecurityConfig)localPair.second;
      if (localDomain.hostname.equals(str)) {
        return localNetworkSecurityConfig;
      }
      if ((localDomain.subdomainsIncluded) && (str.endsWith(localDomain.hostname)) && (str.charAt(str.length() - localDomain.hostname.length() - 1) == '.')) {
        if (paramString == null) {
          paramString = localPair;
        } else if (localDomain.hostname.length() > ((Domain)paramString.first).hostname.length()) {
          paramString = localPair;
        }
      }
    }
    if (paramString != null) {
      return (NetworkSecurityConfig)paramString.second;
    }
    return this.mDefaultConfig;
  }
  
  public X509TrustManager getTrustManager()
  {
    ensureInitialized();
    return this.mTrustManager;
  }
  
  public void handleTrustStorageUpdate()
  {
    ensureInitialized();
    this.mDefaultConfig.handleTrustStorageUpdate();
    if (this.mConfigs != null)
    {
      HashSet localHashSet = new HashSet(this.mConfigs.size());
      Iterator localIterator = this.mConfigs.iterator();
      while (localIterator.hasNext())
      {
        Pair localPair = (Pair)localIterator.next();
        if (localHashSet.add((NetworkSecurityConfig)localPair.second)) {
          ((NetworkSecurityConfig)localPair.second).handleTrustStorageUpdate();
        }
      }
    }
  }
  
  public boolean hasPerDomainConfigs()
  {
    ensureInitialized();
    return (this.mConfigs != null) && (!this.mConfigs.isEmpty());
  }
  
  public boolean isCleartextTrafficPermitted()
  {
    ensureInitialized();
    if (this.mConfigs != null)
    {
      Iterator localIterator = this.mConfigs.iterator();
      while (localIterator.hasNext()) {
        if (!((NetworkSecurityConfig)((Pair)localIterator.next()).second).isCleartextTrafficPermitted()) {
          return false;
        }
      }
    }
    return this.mDefaultConfig.isCleartextTrafficPermitted();
  }
  
  public boolean isCleartextTrafficPermitted(String paramString)
  {
    return getConfigForHostname(paramString).isCleartextTrafficPermitted();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/ApplicationConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */