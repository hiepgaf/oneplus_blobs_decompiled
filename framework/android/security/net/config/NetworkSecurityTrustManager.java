package android.security.net.config;

import android.util.ArrayMap;
import com.android.org.conscrypt.TrustManagerImpl;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

public class NetworkSecurityTrustManager
  extends X509ExtendedTrustManager
{
  private final TrustManagerImpl mDelegate;
  private X509Certificate[] mIssuers;
  private final Object mIssuersLock = new Object();
  private final NetworkSecurityConfig mNetworkSecurityConfig;
  
  public NetworkSecurityTrustManager(NetworkSecurityConfig paramNetworkSecurityConfig)
  {
    if (paramNetworkSecurityConfig == null) {
      throw new NullPointerException("config must not be null");
    }
    this.mNetworkSecurityConfig = paramNetworkSecurityConfig;
    try
    {
      paramNetworkSecurityConfig = new TrustedCertificateStoreAdapter(paramNetworkSecurityConfig);
      KeyStore localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      localKeyStore.load(null);
      this.mDelegate = new TrustManagerImpl(localKeyStore, null, paramNetworkSecurityConfig);
      return;
    }
    catch (GeneralSecurityException|IOException paramNetworkSecurityConfig)
    {
      throw new RuntimeException(paramNetworkSecurityConfig);
    }
  }
  
  private void checkPins(List<X509Certificate> paramList)
    throws CertificateException
  {
    PinSet localPinSet = this.mNetworkSecurityConfig.getPins();
    if ((localPinSet.pins.isEmpty()) || (System.currentTimeMillis() > localPinSet.expirationTime)) {}
    while (!isPinningEnforced(paramList)) {
      return;
    }
    Set localSet = localPinSet.getPinAlgorithms();
    ArrayMap localArrayMap = new ArrayMap(localSet.size());
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      byte[] arrayOfByte = ((X509Certificate)paramList.get(i)).getPublicKey().getEncoded();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        MessageDigest localMessageDigest2 = (MessageDigest)localArrayMap.get(str);
        MessageDigest localMessageDigest1 = localMessageDigest2;
        if (localMessageDigest2 == null) {}
        try
        {
          localMessageDigest1 = MessageDigest.getInstance(str);
          localArrayMap.put(str, localMessageDigest1);
          if (localPinSet.pins.contains(new Pin(str, localMessageDigest1.digest(arrayOfByte)))) {
            return;
          }
        }
        catch (GeneralSecurityException paramList)
        {
          throw new RuntimeException(paramList);
        }
      }
      i -= 1;
    }
    throw new CertificateException("Pin verification failed");
  }
  
  private boolean isPinningEnforced(List<X509Certificate> paramList)
    throws CertificateException
  {
    if (paramList.isEmpty()) {
      return false;
    }
    paramList = (X509Certificate)paramList.get(paramList.size() - 1);
    paramList = this.mNetworkSecurityConfig.findTrustAnchorBySubjectAndPublicKey(paramList);
    if (paramList == null) {
      throw new CertificateException("Trusted chain does not end in a TrustAnchor");
    }
    return !paramList.overridesPins;
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    this.mDelegate.checkClientTrusted(paramArrayOfX509Certificate, paramString);
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException
  {
    this.mDelegate.checkClientTrusted(paramArrayOfX509Certificate, paramString, paramSocket);
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException
  {
    this.mDelegate.checkClientTrusted(paramArrayOfX509Certificate, paramString, paramSSLEngine);
  }
  
  public List<X509Certificate> checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString1, String paramString2)
    throws CertificateException
  {
    paramArrayOfX509Certificate = this.mDelegate.checkServerTrusted(paramArrayOfX509Certificate, paramString1, paramString2);
    checkPins(paramArrayOfX509Certificate);
    return paramArrayOfX509Certificate;
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    checkServerTrusted(paramArrayOfX509Certificate, paramString, (String)null);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException
  {
    checkPins(this.mDelegate.getTrustedChainForServer(paramArrayOfX509Certificate, paramString, paramSocket));
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException
  {
    checkPins(this.mDelegate.getTrustedChainForServer(paramArrayOfX509Certificate, paramString, paramSSLEngine));
  }
  
  public X509Certificate[] getAcceptedIssuers()
  {
    synchronized (this.mIssuersLock)
    {
      if (this.mIssuers == null)
      {
        Object localObject3 = this.mNetworkSecurityConfig.getTrustAnchors();
        arrayOfX509Certificate = new X509Certificate[((Set)localObject3).size()];
        localObject3 = ((Iterable)localObject3).iterator();
        int i = 0;
        while (((Iterator)localObject3).hasNext())
        {
          arrayOfX509Certificate[i] = ((TrustAnchor)((Iterator)localObject3).next()).certificate;
          i += 1;
        }
        this.mIssuers = arrayOfX509Certificate;
      }
      X509Certificate[] arrayOfX509Certificate = (X509Certificate[])this.mIssuers.clone();
      return arrayOfX509Certificate;
    }
  }
  
  public void handleTrustStorageUpdate()
  {
    synchronized (this.mIssuersLock)
    {
      this.mIssuers = null;
      this.mDelegate.handleTrustStorageUpdate();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/NetworkSecurityTrustManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */