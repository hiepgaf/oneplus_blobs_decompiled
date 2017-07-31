package android.security.net.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import java.util.Set;

public class ManifestConfigSource
  implements ConfigSource
{
  private static final boolean DBG = true;
  private static final String LOG_TAG = "NetworkSecurityConfig";
  private final int mApplicationInfoFlags;
  private final int mConfigResourceId;
  private ConfigSource mConfigSource;
  private final Context mContext;
  private final Object mLock = new Object();
  private final int mTargetSdkVersion;
  
  public ManifestConfigSource(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = paramContext.getApplicationInfo();
    this.mApplicationInfoFlags = paramContext.flags;
    this.mTargetSdkVersion = paramContext.targetSdkVersion;
    this.mConfigResourceId = paramContext.networkSecurityConfigRes;
  }
  
  private ConfigSource getConfigSource()
  {
    synchronized (this.mLock)
    {
      if (this.mConfigSource != null)
      {
        localObject1 = this.mConfigSource;
        return (ConfigSource)localObject1;
      }
      if (this.mConfigResourceId != 0) {
        if ((this.mApplicationInfoFlags & 0x2) != 0)
        {
          bool = true;
          Log.d("NetworkSecurityConfig", "Using Network Security Config from resource " + this.mContext.getResources().getResourceEntryName(this.mConfigResourceId) + " debugBuild: " + bool);
        }
      }
      for (Object localObject1 = new XmlConfigSource(this.mContext, this.mConfigResourceId, bool, this.mTargetSdkVersion);; localObject1 = new DefaultConfigSource(bool, this.mTargetSdkVersion))
      {
        this.mConfigSource = ((ConfigSource)localObject1);
        localObject1 = this.mConfigSource;
        return (ConfigSource)localObject1;
        bool = false;
        break;
        Log.d("NetworkSecurityConfig", "No Network Security Config specified, using platform default");
        if ((this.mApplicationInfoFlags & 0x8000000) == 0) {
          break label164;
        }
        bool = true;
      }
      label164:
      boolean bool = false;
    }
  }
  
  public NetworkSecurityConfig getDefaultConfig()
  {
    return getConfigSource().getDefaultConfig();
  }
  
  public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs()
  {
    return getConfigSource().getPerDomainConfigs();
  }
  
  private static final class DefaultConfigSource
    implements ConfigSource
  {
    private final NetworkSecurityConfig mDefaultConfig;
    
    public DefaultConfigSource(boolean paramBoolean, int paramInt)
    {
      this.mDefaultConfig = NetworkSecurityConfig.getDefaultBuilder(paramInt).setCleartextTrafficPermitted(paramBoolean).build();
    }
    
    public NetworkSecurityConfig getDefaultConfig()
    {
      return this.mDefaultConfig;
    }
    
    public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs()
    {
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/ManifestConfigSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */