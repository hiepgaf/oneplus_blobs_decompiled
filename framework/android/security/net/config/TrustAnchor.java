package android.security.net.config;

import java.security.cert.X509Certificate;

public final class TrustAnchor
{
  public final X509Certificate certificate;
  public final boolean overridesPins;
  
  public TrustAnchor(X509Certificate paramX509Certificate, boolean paramBoolean)
  {
    if (paramX509Certificate == null) {
      throw new NullPointerException("certificate");
    }
    this.certificate = paramX509Certificate;
    this.overridesPins = paramBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/TrustAnchor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */