package android.security.net.config;

import android.util.ArrayMap;
import android.util.ArraySet;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NetworkSecurityConfig
{
  public static final boolean DEFAULT_CLEARTEXT_TRAFFIC_PERMITTED = true;
  public static final boolean DEFAULT_HSTS_ENFORCED = false;
  private Set<TrustAnchor> mAnchors;
  private final Object mAnchorsLock = new Object();
  private final List<CertificatesEntryRef> mCertificatesEntryRefs;
  private final boolean mCleartextTrafficPermitted;
  private final boolean mHstsEnforced;
  private final PinSet mPins;
  private NetworkSecurityTrustManager mTrustManager;
  private final Object mTrustManagerLock = new Object();
  
  private NetworkSecurityConfig(boolean paramBoolean1, boolean paramBoolean2, PinSet paramPinSet, List<CertificatesEntryRef> paramList)
  {
    this.mCleartextTrafficPermitted = paramBoolean1;
    this.mHstsEnforced = paramBoolean2;
    this.mPins = paramPinSet;
    this.mCertificatesEntryRefs = paramList;
    Collections.sort(this.mCertificatesEntryRefs, new Comparator()
    {
      public int compare(CertificatesEntryRef paramAnonymousCertificatesEntryRef1, CertificatesEntryRef paramAnonymousCertificatesEntryRef2)
      {
        int i = 0;
        if (paramAnonymousCertificatesEntryRef1.overridesPins())
        {
          if (paramAnonymousCertificatesEntryRef2.overridesPins()) {
            return 0;
          }
          return -1;
        }
        if (paramAnonymousCertificatesEntryRef2.overridesPins()) {
          i = 1;
        }
        return i;
      }
    });
  }
  
  public static final Builder getDefaultBuilder(int paramInt)
  {
    Builder localBuilder = new Builder().setCleartextTrafficPermitted(true).setHstsEnforced(false).addCertificatesEntryRef(new CertificatesEntryRef(SystemCertificateSource.getInstance(), false));
    if (paramInt <= 23) {
      localBuilder.addCertificatesEntryRef(new CertificatesEntryRef(UserCertificateSource.getInstance(), false));
    }
    return localBuilder;
  }
  
  public Set<X509Certificate> findAllCertificatesByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    ArraySet localArraySet = new ArraySet();
    Iterator localIterator = this.mCertificatesEntryRefs.iterator();
    while (localIterator.hasNext()) {
      localArraySet.addAll(((CertificatesEntryRef)localIterator.next()).findAllCertificatesByIssuerAndSignature(paramX509Certificate));
    }
    return localArraySet;
  }
  
  public TrustAnchor findTrustAnchorByIssuerAndSignature(X509Certificate paramX509Certificate)
  {
    Iterator localIterator = this.mCertificatesEntryRefs.iterator();
    while (localIterator.hasNext())
    {
      TrustAnchor localTrustAnchor = ((CertificatesEntryRef)localIterator.next()).findByIssuerAndSignature(paramX509Certificate);
      if (localTrustAnchor != null) {
        return localTrustAnchor;
      }
    }
    return null;
  }
  
  public TrustAnchor findTrustAnchorBySubjectAndPublicKey(X509Certificate paramX509Certificate)
  {
    Iterator localIterator = this.mCertificatesEntryRefs.iterator();
    while (localIterator.hasNext())
    {
      TrustAnchor localTrustAnchor = ((CertificatesEntryRef)localIterator.next()).findBySubjectAndPublicKey(paramX509Certificate);
      if (localTrustAnchor != null) {
        return localTrustAnchor;
      }
    }
    return null;
  }
  
  public PinSet getPins()
  {
    return this.mPins;
  }
  
  public Set<TrustAnchor> getTrustAnchors()
  {
    synchronized (this.mAnchorsLock)
    {
      if (this.mAnchors != null)
      {
        localObject2 = this.mAnchors;
        return (Set<TrustAnchor>)localObject2;
      }
      Object localObject2 = new ArrayMap();
      TrustAnchor localTrustAnchor;
      X509Certificate localX509Certificate;
      do
      {
        localObject4 = this.mCertificatesEntryRefs.iterator();
        Iterator localIterator;
        while (!localIterator.hasNext())
        {
          if (!((Iterator)localObject4).hasNext()) {
            break;
          }
          localIterator = ((CertificatesEntryRef)((Iterator)localObject4).next()).getTrustAnchors().iterator();
        }
        localTrustAnchor = (TrustAnchor)localIterator.next();
        localX509Certificate = localTrustAnchor.certificate;
      } while (((Map)localObject2).containsKey(localX509Certificate));
      ((Map)localObject2).put(localX509Certificate, localTrustAnchor);
    }
    Object localObject4 = new ArraySet(((Map)localObject3).size());
    ((ArraySet)localObject4).addAll(((Map)localObject3).values());
    this.mAnchors = ((Set)localObject4);
    Set localSet = this.mAnchors;
    return localSet;
  }
  
  public NetworkSecurityTrustManager getTrustManager()
  {
    synchronized (this.mTrustManagerLock)
    {
      if (this.mTrustManager == null) {
        this.mTrustManager = new NetworkSecurityTrustManager(this);
      }
      NetworkSecurityTrustManager localNetworkSecurityTrustManager = this.mTrustManager;
      return localNetworkSecurityTrustManager;
    }
  }
  
  public void handleTrustStorageUpdate()
  {
    synchronized (this.mAnchorsLock)
    {
      this.mAnchors = null;
      Iterator localIterator = this.mCertificatesEntryRefs.iterator();
      if (localIterator.hasNext()) {
        ((CertificatesEntryRef)localIterator.next()).handleTrustStorageUpdate();
      }
    }
    getTrustManager().handleTrustStorageUpdate();
  }
  
  public boolean isCleartextTrafficPermitted()
  {
    return this.mCleartextTrafficPermitted;
  }
  
  public boolean isHstsEnforced()
  {
    return this.mHstsEnforced;
  }
  
  public static final class Builder
  {
    private List<CertificatesEntryRef> mCertificatesEntryRefs;
    private boolean mCleartextTrafficPermitted = true;
    private boolean mCleartextTrafficPermittedSet = false;
    private boolean mHstsEnforced = false;
    private boolean mHstsEnforcedSet = false;
    private Builder mParentBuilder;
    private PinSet mPinSet;
    
    private List<CertificatesEntryRef> getEffectiveCertificatesEntryRefs()
    {
      if (this.mCertificatesEntryRefs != null) {
        return this.mCertificatesEntryRefs;
      }
      if (this.mParentBuilder != null) {
        return this.mParentBuilder.getEffectiveCertificatesEntryRefs();
      }
      return Collections.emptyList();
    }
    
    private boolean getEffectiveCleartextTrafficPermitted()
    {
      if (this.mCleartextTrafficPermittedSet) {
        return this.mCleartextTrafficPermitted;
      }
      if (this.mParentBuilder != null) {
        return this.mParentBuilder.getEffectiveCleartextTrafficPermitted();
      }
      return true;
    }
    
    private boolean getEffectiveHstsEnforced()
    {
      if (this.mHstsEnforcedSet) {
        return this.mHstsEnforced;
      }
      if (this.mParentBuilder != null) {
        return this.mParentBuilder.getEffectiveHstsEnforced();
      }
      return false;
    }
    
    private PinSet getEffectivePinSet()
    {
      if (this.mPinSet != null) {
        return this.mPinSet;
      }
      if (this.mParentBuilder != null) {
        return this.mParentBuilder.getEffectivePinSet();
      }
      return PinSet.EMPTY_PINSET;
    }
    
    public Builder addCertificatesEntryRef(CertificatesEntryRef paramCertificatesEntryRef)
    {
      if (this.mCertificatesEntryRefs == null) {
        this.mCertificatesEntryRefs = new ArrayList();
      }
      this.mCertificatesEntryRefs.add(paramCertificatesEntryRef);
      return this;
    }
    
    public Builder addCertificatesEntryRefs(Collection<? extends CertificatesEntryRef> paramCollection)
    {
      if (this.mCertificatesEntryRefs == null) {
        this.mCertificatesEntryRefs = new ArrayList();
      }
      this.mCertificatesEntryRefs.addAll(paramCollection);
      return this;
    }
    
    public NetworkSecurityConfig build()
    {
      return new NetworkSecurityConfig(getEffectiveCleartextTrafficPermitted(), getEffectiveHstsEnforced(), getEffectivePinSet(), getEffectiveCertificatesEntryRefs(), null);
    }
    
    List<CertificatesEntryRef> getCertificatesEntryRefs()
    {
      return this.mCertificatesEntryRefs;
    }
    
    public Builder getParent()
    {
      return this.mParentBuilder;
    }
    
    public boolean hasCertificatesEntryRefs()
    {
      return this.mCertificatesEntryRefs != null;
    }
    
    public Builder setCleartextTrafficPermitted(boolean paramBoolean)
    {
      this.mCleartextTrafficPermitted = paramBoolean;
      this.mCleartextTrafficPermittedSet = true;
      return this;
    }
    
    public Builder setHstsEnforced(boolean paramBoolean)
    {
      this.mHstsEnforced = paramBoolean;
      this.mHstsEnforcedSet = true;
      return this;
    }
    
    public Builder setParent(Builder paramBuilder)
    {
      for (Builder localBuilder = paramBuilder; localBuilder != null; localBuilder = localBuilder.getParent()) {
        if (localBuilder == this) {
          throw new IllegalArgumentException("Loops are not allowed in Builder parents");
        }
      }
      this.mParentBuilder = paramBuilder;
      return this;
    }
    
    public Builder setPinSet(PinSet paramPinSet)
    {
      this.mPinSet = paramPinSet;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/NetworkSecurityConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */