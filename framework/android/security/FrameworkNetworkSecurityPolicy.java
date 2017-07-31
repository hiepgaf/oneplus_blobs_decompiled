package android.security;

import libcore.net.NetworkSecurityPolicy;

public class FrameworkNetworkSecurityPolicy
  extends NetworkSecurityPolicy
{
  private final boolean mCleartextTrafficPermitted;
  
  public FrameworkNetworkSecurityPolicy(boolean paramBoolean)
  {
    this.mCleartextTrafficPermitted = paramBoolean;
  }
  
  public boolean isCleartextTrafficPermitted()
  {
    return this.mCleartextTrafficPermitted;
  }
  
  public boolean isCleartextTrafficPermitted(String paramString)
  {
    return isCleartextTrafficPermitted();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/FrameworkNetworkSecurityPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */