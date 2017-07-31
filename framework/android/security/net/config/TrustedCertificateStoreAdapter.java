package android.security.net.config;

import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

public class TrustedCertificateStoreAdapter
  extends TrustedCertificateStore
{
  private final NetworkSecurityConfig mConfig;
  
  public TrustedCertificateStoreAdapter(NetworkSecurityConfig paramNetworkSecurityConfig)
  {
    this.mConfig = paramNetworkSecurityConfig;
  }
  
  public Set<String> aliases()
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<String> allSystemAliases()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAlias(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<X509Certificate> findAllIssuers(X509Certificate paramX509Certificate)
  {
    return this.mConfig.findAllCertificatesByIssuerAndSignature(paramX509Certificate);
  }
  
  public X509Certificate findIssuer(X509Certificate paramX509Certificate)
  {
    paramX509Certificate = this.mConfig.findTrustAnchorByIssuerAndSignature(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return paramX509Certificate.certificate;
  }
  
  public Certificate getCertificate(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public Certificate getCertificate(String paramString, boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getCertificateAlias(Certificate paramCertificate)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getCertificateAlias(Certificate paramCertificate, boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public File getCertificateFile(File paramFile, X509Certificate paramX509Certificate)
  {
    throw new UnsupportedOperationException();
  }
  
  public Date getCreationDate(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public X509Certificate getTrustAnchor(X509Certificate paramX509Certificate)
  {
    paramX509Certificate = this.mConfig.findTrustAnchorBySubjectAndPublicKey(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return paramX509Certificate.certificate;
  }
  
  public boolean isUserAddedCertificate(X509Certificate paramX509Certificate)
  {
    paramX509Certificate = this.mConfig.findTrustAnchorBySubjectAndPublicKey(paramX509Certificate);
    if (paramX509Certificate == null) {
      return false;
    }
    return paramX509Certificate.overridesPins;
  }
  
  public Set<String> userAliases()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/TrustedCertificateStoreAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */