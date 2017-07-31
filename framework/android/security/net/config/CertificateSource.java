package android.security.net.config;

import java.security.cert.X509Certificate;
import java.util.Set;

public abstract interface CertificateSource
{
  public abstract Set<X509Certificate> findAllByIssuerAndSignature(X509Certificate paramX509Certificate);
  
  public abstract X509Certificate findByIssuerAndSignature(X509Certificate paramX509Certificate);
  
  public abstract X509Certificate findBySubjectAndPublicKey(X509Certificate paramX509Certificate);
  
  public abstract Set<X509Certificate> getCertificates();
  
  public abstract void handleTrustStorageUpdate();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/CertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */