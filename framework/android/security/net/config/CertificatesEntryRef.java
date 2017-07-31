package android.security.net.config;

import android.util.ArraySet;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;

public final class CertificatesEntryRef
{
  private final boolean mOverridesPins;
  private final CertificateSource mSource;
  
  public CertificatesEntryRef(CertificateSource paramCertificateSource, boolean paramBoolean)
  {
    this.mSource = paramCertificateSource;
    this.mOverridesPins = paramBoolean;
  }
  
  public Set<X509Certificate> findAllCertificatesByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    return this.mSource.findAllByIssuerAndSignature(paramX509Certificate);
  }
  
  public TrustAnchor findByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    paramX509Certificate = this.mSource.findByIssuerAndSignature(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return new TrustAnchor(paramX509Certificate, this.mOverridesPins);
  }
  
  public TrustAnchor findBySubjectAndPublicKey(X509Certificate paramX509Certificate)
  {
    paramX509Certificate = this.mSource.findBySubjectAndPublicKey(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return new TrustAnchor(paramX509Certificate, this.mOverridesPins);
  }
  
  public Set<TrustAnchor> getTrustAnchors()
  {
    ArraySet localArraySet = new ArraySet();
    Iterator localIterator = this.mSource.getCertificates().iterator();
    while (localIterator.hasNext()) {
      localArraySet.add(new TrustAnchor((X509Certificate)localIterator.next(), this.mOverridesPins));
    }
    return localArraySet;
  }
  
  public void handleTrustStorageUpdate()
  {
    this.mSource.handleTrustStorageUpdate();
  }
  
  boolean overridesPins()
  {
    return this.mOverridesPins;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/CertificatesEntryRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */