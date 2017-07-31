package android.security.net.config;

import android.content.Context;
import android.content.res.Resources;
import android.util.ArraySet;
import com.android.org.conscrypt.TrustedCertificateIndex;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import libcore.io.IoUtils;

public class ResourceCertificateSource
  implements CertificateSource
{
  private Set<X509Certificate> mCertificates;
  private Context mContext;
  private TrustedCertificateIndex mIndex;
  private final Object mLock = new Object();
  private final int mResourceId;
  
  public ResourceCertificateSource(int paramInt, Context paramContext)
  {
    this.mResourceId = paramInt;
    this.mContext = paramContext;
  }
  
  private void ensureInitialized()
  {
    Object localObject1;
    ArraySet localArraySet;
    Collection localCollection;
    Object localObject6;
    Object localObject4;
    synchronized (this.mLock)
    {
      localObject1 = this.mCertificates;
      if (localObject1 != null) {
        return;
      }
      localArraySet = new ArraySet();
      localCollection = null;
      localObject6 = null;
      localObject4 = localObject6;
      localObject1 = localCollection;
    }
    Object localObject3;
    try
    {
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      localObject4 = localObject6;
      localObject1 = localCollection;
      localObject6 = this.mContext.getResources().openRawResource(this.mResourceId);
      localObject4 = localObject6;
      localObject1 = localObject6;
      localCollection = localCertificateFactory.generateCertificates((InputStream)localObject6);
      IoUtils.closeQuietly((AutoCloseable)localObject6);
      localObject1 = new TrustedCertificateIndex();
      localObject4 = localCollection.iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject6 = (Certificate)((Iterator)localObject4).next();
        localArraySet.add((X509Certificate)localObject6);
        ((TrustedCertificateIndex)localObject1).index((X509Certificate)localObject6);
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    catch (CertificateException localCertificateException)
    {
      localObject3 = localObject4;
      throw new RuntimeException("Failed to load trust anchors from id " + this.mResourceId, localCertificateException);
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject3);
    }
    this.mCertificates = localArraySet;
    this.mIndex = ((TrustedCertificateIndex)localObject3);
    this.mContext = null;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/ResourceCertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */