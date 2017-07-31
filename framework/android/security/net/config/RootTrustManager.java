package android.security.net.config;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedTrustManager;

public class RootTrustManager
  extends X509ExtendedTrustManager
{
  private final ApplicationConfig mConfig;
  
  public RootTrustManager(ApplicationConfig paramApplicationConfig)
  {
    if (paramApplicationConfig == null) {
      throw new NullPointerException("config must not be null");
    }
    this.mConfig = paramApplicationConfig;
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    this.mConfig.getConfigForHostname("").getTrustManager().checkClientTrusted(paramArrayOfX509Certificate, paramString);
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException
  {
    this.mConfig.getConfigForHostname("").getTrustManager().checkClientTrusted(paramArrayOfX509Certificate, paramString, paramSocket);
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException
  {
    this.mConfig.getConfigForHostname("").getTrustManager().checkClientTrusted(paramArrayOfX509Certificate, paramString, paramSSLEngine);
  }
  
  public List<X509Certificate> checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString1, String paramString2)
    throws CertificateException
  {
    if ((paramString2 == null) && (this.mConfig.hasPerDomainConfigs())) {
      throw new CertificateException("Domain specific configurations require that the hostname be provided");
    }
    return this.mConfig.getConfigForHostname(paramString2).getTrustManager().checkServerTrusted(paramArrayOfX509Certificate, paramString1, paramString2);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    if (this.mConfig.hasPerDomainConfigs()) {
      throw new CertificateException("Domain specific configurations require that hostname aware checkServerTrusted(X509Certificate[], String, String) is used");
    }
    this.mConfig.getConfigForHostname("").getTrustManager().checkServerTrusted(paramArrayOfX509Certificate, paramString);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException
  {
    if ((paramSocket instanceof SSLSocket))
    {
      Object localObject = ((SSLSocket)paramSocket).getHandshakeSession();
      if (localObject == null) {
        throw new CertificateException("Not in handshake; no session available");
      }
      localObject = ((SSLSession)localObject).getPeerHost();
      this.mConfig.getConfigForHostname((String)localObject).getTrustManager().checkServerTrusted(paramArrayOfX509Certificate, paramString, paramSocket);
      return;
    }
    checkServerTrusted(paramArrayOfX509Certificate, paramString);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException
  {
    Object localObject = paramSSLEngine.getHandshakeSession();
    if (localObject == null) {
      throw new CertificateException("Not in handshake; no session available");
    }
    localObject = ((SSLSession)localObject).getPeerHost();
    this.mConfig.getConfigForHostname((String)localObject).getTrustManager().checkServerTrusted(paramArrayOfX509Certificate, paramString, paramSSLEngine);
  }
  
  public X509Certificate[] getAcceptedIssuers()
  {
    return this.mConfig.getConfigForHostname("").getTrustManager().getAcceptedIssuers();
  }
  
  public boolean isSameTrustConfiguration(String paramString1, String paramString2)
  {
    return this.mConfig.getConfigForHostname(paramString1).equals(this.mConfig.getConfigForHostname(paramString2));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/RootTrustManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */