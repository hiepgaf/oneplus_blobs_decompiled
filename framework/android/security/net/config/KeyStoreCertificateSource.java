package android.security.net.config;

import android.util.ArraySet;
import com.android.org.conscrypt.TrustedCertificateIndex;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

class KeyStoreCertificateSource
  implements CertificateSource
{
  private Set<X509Certificate> mCertificates;
  private TrustedCertificateIndex mIndex;
  private final KeyStore mKeyStore;
  private final Object mLock = new Object();
  
  public KeyStoreCertificateSource(KeyStore paramKeyStore)
  {
    this.mKeyStore = paramKeyStore;
  }
  
  private void ensureInitialized()
  {
    ArraySet localArraySet;
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCertificates;
      if (localObject2 != null) {
        return;
      }
      try
      {
        localObject2 = new TrustedCertificateIndex();
        localArraySet = new ArraySet(this.mKeyStore.size());
        Enumeration localEnumeration = this.mKeyStore.aliases();
        while (localEnumeration.hasMoreElements())
        {
          Object localObject3 = (String)localEnumeration.nextElement();
          localObject3 = (X509Certificate)this.mKeyStore.getCertificate((String)localObject3);
          if (localObject3 != null)
          {
            localArraySet.add(localObject3);
            ((TrustedCertificateIndex)localObject2).index((X509Certificate)localObject3);
            continue;
            localTrustedCertificateIndex = finally;
          }
        }
      }
      catch (KeyStoreException localKeyStoreException)
      {
        throw new RuntimeException("Failed to load certificates from KeyStore", localKeyStoreException);
      }
    }
    this.mIndex = localTrustedCertificateIndex;
    this.mCertificates = localArraySet;
  }
  
  public Set<X509Certificate> findAllByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    ensureInitialized();
    Object localObject = this.mIndex.findAllByIssuerAndSignature(paramX509Certificate);
    if (((Set)localObject).isEmpty()) {
      return Collections.emptySet();
    }
    paramX509Certificate = new ArraySet(((Set)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      paramX509Certificate.add(((TrustAnchor)((Iterator)localObject).next()).getTrustedCert());
    }
    return paramX509Certificate;
  }
  
  public X509Certificate findByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    ensureInitialized();
    paramX509Certificate = this.mIndex.findByIssuerAndSignature(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return paramX509Certificate.getTrustedCert();
  }
  
  public X509Certificate findBySubjectAndPublicKey(X509Certificate paramX509Certificate)
  {
    ensureInitialized();
    paramX509Certificate = this.mIndex.findBySubjectAndPublicKey(paramX509Certificate);
    if (paramX509Certificate == null) {
      return null;
    }
    return paramX509Certificate.getTrustedCert();
  }
  
  public Set<X509Certificate> getCertificates()
  {
    ensureInitialized();
    return this.mCertificates;
  }
  
  public void handleTrustStorageUpdate() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/KeyStoreCertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */