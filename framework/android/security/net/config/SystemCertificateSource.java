package android.security.net.config;

import android.os.Environment;
import android.os.UserHandle;
import java.io.File;

public final class SystemCertificateSource
  extends DirectoryCertificateSource
{
  private final File mUserRemovedCaDir = new File(Environment.getUserConfigDirectory(UserHandle.myUserId()), "cacerts-removed");
  
  private SystemCertificateSource()
  {
    super(new File(System.getenv("ANDROID_ROOT") + "/etc/security/cacerts"));
  }
  
  public static SystemCertificateSource getInstance()
  {
    return NoPreloadHolder.-get0();
  }
  
  protected boolean isCertMarkedAsRemoved(String paramString)
  {
    return new File(this.mUserRemovedCaDir, paramString).exists();
  }
  
  private static class NoPreloadHolder
  {
    private static final SystemCertificateSource INSTANCE = new SystemCertificateSource(null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/SystemCertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */