package android.security.net.config;

import android.os.Environment;
import android.os.UserHandle;
import java.io.File;

public final class UserCertificateSource
  extends DirectoryCertificateSource
{
  private UserCertificateSource()
  {
    super(new File(Environment.getUserConfigDirectory(UserHandle.myUserId()), "cacerts-added"));
  }
  
  public static UserCertificateSource getInstance()
  {
    return NoPreloadHolder.-get0();
  }
  
  protected boolean isCertMarkedAsRemoved(String paramString)
  {
    return false;
  }
  
  private static class NoPreloadHolder
  {
    private static final UserCertificateSource INSTANCE = new UserCertificateSource(null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/UserCertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */