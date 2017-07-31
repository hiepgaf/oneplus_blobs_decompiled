package android.net;

import android.os.SystemProperties;
import android.util.Log;
import com.android.org.conscrypt.ClientSessionContext;
import com.android.org.conscrypt.OpenSSLContextImpl;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLCertificateSocketFactory
  extends javax.net.ssl.SSLSocketFactory
{
  private static final TrustManager[] INSECURE_TRUST_MANAGER = { new X509TrustManager()
  {
    public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString) {}
    
    public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString) {}
    
    public X509Certificate[] getAcceptedIssuers()
    {
      return null;
    }
  } };
  private static final String TAG = "SSLCertificateSocketFactory";
  private byte[] mAlpnProtocols = null;
  private PrivateKey mChannelIdPrivateKey = null;
  private final int mHandshakeTimeoutMillis;
  private javax.net.ssl.SSLSocketFactory mInsecureFactory = null;
  private KeyManager[] mKeyManagers = null;
  private byte[] mNpnProtocols = null;
  private final boolean mSecure;
  private javax.net.ssl.SSLSocketFactory mSecureFactory = null;
  private final SSLClientSessionCache mSessionCache;
  private TrustManager[] mTrustManagers = null;
  
  @Deprecated
  public SSLCertificateSocketFactory(int paramInt)
  {
    this(paramInt, null, true);
  }
  
  private SSLCertificateSocketFactory(int paramInt, SSLSessionCache paramSSLSessionCache, boolean paramBoolean)
  {
    this.mHandshakeTimeoutMillis = paramInt;
    if (paramSSLSessionCache == null) {}
    for (paramSSLSessionCache = (SSLSessionCache)localObject;; paramSSLSessionCache = paramSSLSessionCache.mSessionCache)
    {
      this.mSessionCache = paramSSLSessionCache;
      this.mSecure = paramBoolean;
      return;
    }
  }
  
  private static OpenSSLSocketImpl castToOpenSSLSocket(Socket paramSocket)
  {
    if (!(paramSocket instanceof OpenSSLSocketImpl)) {
      throw new IllegalArgumentException("Socket not created by this factory: " + paramSocket);
    }
    return (OpenSSLSocketImpl)paramSocket;
  }
  
  public static SocketFactory getDefault(int paramInt)
  {
    return new SSLCertificateSocketFactory(paramInt, null, true);
  }
  
  public static javax.net.ssl.SSLSocketFactory getDefault(int paramInt, SSLSessionCache paramSSLSessionCache)
  {
    return new SSLCertificateSocketFactory(paramInt, paramSSLSessionCache, true);
  }
  
  private javax.net.ssl.SSLSocketFactory getDelegate()
  {
    try
    {
      if ((!this.mSecure) || (isSslCheckRelaxed()))
      {
        if (this.mInsecureFactory == null)
        {
          if (!this.mSecure) {
            break label61;
          }
          Log.w("SSLCertificateSocketFactory", "*** BYPASSING SSL SECURITY CHECKS (socket.relaxsslcheck=yes) ***");
        }
        for (;;)
        {
          this.mInsecureFactory = makeSocketFactory(this.mKeyManagers, INSECURE_TRUST_MANAGER);
          javax.net.ssl.SSLSocketFactory localSSLSocketFactory1 = this.mInsecureFactory;
          return localSSLSocketFactory1;
          label61:
          Log.w("SSLCertificateSocketFactory", "Bypassing SSL security checks at caller's request");
        }
      }
      if (this.mSecureFactory != null) {
        break label100;
      }
    }
    finally {}
    this.mSecureFactory = makeSocketFactory(this.mKeyManagers, this.mTrustManagers);
    label100:
    javax.net.ssl.SSLSocketFactory localSSLSocketFactory2 = this.mSecureFactory;
    return localSSLSocketFactory2;
  }
  
  @Deprecated
  public static org.apache.http.conn.ssl.SSLSocketFactory getHttpSocketFactory(int paramInt, SSLSessionCache paramSSLSessionCache)
  {
    return new org.apache.http.conn.ssl.SSLSocketFactory(new SSLCertificateSocketFactory(paramInt, paramSSLSessionCache, true));
  }
  
  public static javax.net.ssl.SSLSocketFactory getInsecure(int paramInt, SSLSessionCache paramSSLSessionCache)
  {
    return new SSLCertificateSocketFactory(paramInt, paramSSLSessionCache, false);
  }
  
  private static boolean isSslCheckRelaxed()
  {
    if ("1".equals(SystemProperties.get("ro.debuggable"))) {
      return "yes".equals(SystemProperties.get("socket.relaxsslcheck"));
    }
    return false;
  }
  
  private javax.net.ssl.SSLSocketFactory makeSocketFactory(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager)
  {
    try
    {
      OpenSSLContextImpl localOpenSSLContextImpl = OpenSSLContextImpl.getPreferred();
      localOpenSSLContextImpl.engineInit(paramArrayOfKeyManager, paramArrayOfTrustManager, null);
      localOpenSSLContextImpl.engineGetClientSessionContext().setPersistentCache(this.mSessionCache);
      paramArrayOfKeyManager = localOpenSSLContextImpl.engineGetSocketFactory();
      return paramArrayOfKeyManager;
    }
    catch (KeyManagementException paramArrayOfKeyManager)
    {
      Log.wtf("SSLCertificateSocketFactory", paramArrayOfKeyManager);
    }
    return (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault();
  }
  
  static byte[] toLengthPrefixedList(byte[]... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      throw new IllegalArgumentException("items.length == 0");
    }
    int j = 0;
    int k = paramVarArgs.length;
    int i = 0;
    while (i < k)
    {
      arrayOfByte1 = paramVarArgs[i];
      if ((arrayOfByte1.length == 0) || (arrayOfByte1.length > 255)) {
        throw new IllegalArgumentException("s.length == 0 || s.length > 255: " + arrayOfByte1.length);
      }
      j += arrayOfByte1.length + 1;
      i += 1;
    }
    byte[] arrayOfByte1 = new byte[j];
    int m = paramVarArgs.length;
    j = 0;
    i = 0;
    while (j < m)
    {
      byte[] arrayOfByte2 = paramVarArgs[j];
      arrayOfByte1[i] = ((byte)arrayOfByte2.length);
      int n = arrayOfByte2.length;
      k = 0;
      i += 1;
      while (k < n)
      {
        arrayOfByte1[i] = arrayOfByte2[k];
        k += 1;
        i += 1;
      }
      j += 1;
    }
    return arrayOfByte1;
  }
  
  public static void verifyHostname(Socket paramSocket, String paramString)
    throws IOException
  {
    if (!(paramSocket instanceof SSLSocket)) {
      throw new IllegalArgumentException("Attempt to verify non-SSL socket");
    }
    if (!isSslCheckRelaxed())
    {
      paramSocket = (SSLSocket)paramSocket;
      paramSocket.startHandshake();
      paramSocket = paramSocket.getSession();
      if (paramSocket == null) {
        throw new SSLException("Cannot verify SSL socket without session");
      }
      if (!HttpsURLConnection.getDefaultHostnameVerifier().verify(paramString, paramSocket)) {
        throw new SSLPeerUnverifiedException("Cannot verify hostname: " + paramString);
      }
    }
  }
  
  public Socket createSocket()
    throws IOException
  {
    OpenSSLSocketImpl localOpenSSLSocketImpl = (OpenSSLSocketImpl)getDelegate().createSocket();
    localOpenSSLSocketImpl.setNpnProtocols(this.mNpnProtocols);
    localOpenSSLSocketImpl.setAlpnProtocols(this.mAlpnProtocols);
    localOpenSSLSocketImpl.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    localOpenSSLSocketImpl.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    return localOpenSSLSocketImpl;
  }
  
  public Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    OpenSSLSocketImpl localOpenSSLSocketImpl = (OpenSSLSocketImpl)getDelegate().createSocket(paramString, paramInt);
    localOpenSSLSocketImpl.setNpnProtocols(this.mNpnProtocols);
    localOpenSSLSocketImpl.setAlpnProtocols(this.mAlpnProtocols);
    localOpenSSLSocketImpl.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    localOpenSSLSocketImpl.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    if (this.mSecure) {
      verifyHostname(localOpenSSLSocketImpl, paramString);
    }
    return localOpenSSLSocketImpl;
  }
  
  public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException
  {
    paramInetAddress = (OpenSSLSocketImpl)getDelegate().createSocket(paramString, paramInt1, paramInetAddress, paramInt2);
    paramInetAddress.setNpnProtocols(this.mNpnProtocols);
    paramInetAddress.setAlpnProtocols(this.mAlpnProtocols);
    paramInetAddress.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    paramInetAddress.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    if (this.mSecure) {
      verifyHostname(paramInetAddress, paramString);
    }
    return paramInetAddress;
  }
  
  public Socket createSocket(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    paramInetAddress = (OpenSSLSocketImpl)getDelegate().createSocket(paramInetAddress, paramInt);
    paramInetAddress.setNpnProtocols(this.mNpnProtocols);
    paramInetAddress.setAlpnProtocols(this.mAlpnProtocols);
    paramInetAddress.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    paramInetAddress.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    return paramInetAddress;
  }
  
  public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws IOException
  {
    paramInetAddress1 = (OpenSSLSocketImpl)getDelegate().createSocket(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2);
    paramInetAddress1.setNpnProtocols(this.mNpnProtocols);
    paramInetAddress1.setAlpnProtocols(this.mAlpnProtocols);
    paramInetAddress1.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    paramInetAddress1.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    return paramInetAddress1;
  }
  
  public Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    paramSocket = (OpenSSLSocketImpl)getDelegate().createSocket(paramSocket, paramString, paramInt, paramBoolean);
    paramSocket.setNpnProtocols(this.mNpnProtocols);
    paramSocket.setAlpnProtocols(this.mAlpnProtocols);
    paramSocket.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
    paramSocket.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
    if (this.mSecure) {
      verifyHostname(paramSocket, paramString);
    }
    return paramSocket;
  }
  
  public byte[] getAlpnSelectedProtocol(Socket paramSocket)
  {
    return castToOpenSSLSocket(paramSocket).getAlpnSelectedProtocol();
  }
  
  public String[] getDefaultCipherSuites()
  {
    return getDelegate().getDefaultCipherSuites();
  }
  
  public byte[] getNpnSelectedProtocol(Socket paramSocket)
  {
    return castToOpenSSLSocket(paramSocket).getNpnSelectedProtocol();
  }
  
  public String[] getSupportedCipherSuites()
  {
    return getDelegate().getSupportedCipherSuites();
  }
  
  public void setAlpnProtocols(byte[][] paramArrayOfByte)
  {
    this.mAlpnProtocols = toLengthPrefixedList(paramArrayOfByte);
  }
  
  public void setChannelIdPrivateKey(PrivateKey paramPrivateKey)
  {
    this.mChannelIdPrivateKey = paramPrivateKey;
  }
  
  public void setHostname(Socket paramSocket, String paramString)
  {
    castToOpenSSLSocket(paramSocket).setHostname(paramString);
  }
  
  public void setKeyManagers(KeyManager[] paramArrayOfKeyManager)
  {
    this.mKeyManagers = paramArrayOfKeyManager;
    this.mSecureFactory = null;
    this.mInsecureFactory = null;
  }
  
  public void setNpnProtocols(byte[][] paramArrayOfByte)
  {
    this.mNpnProtocols = toLengthPrefixedList(paramArrayOfByte);
  }
  
  public void setSoWriteTimeout(Socket paramSocket, int paramInt)
    throws SocketException
  {
    castToOpenSSLSocket(paramSocket).setSoWriteTimeout(paramInt);
  }
  
  public void setTrustManagers(TrustManager[] paramArrayOfTrustManager)
  {
    this.mTrustManagers = paramArrayOfTrustManager;
    this.mSecureFactory = null;
  }
  
  public void setUseSessionTickets(Socket paramSocket, boolean paramBoolean)
  {
    castToOpenSSLSocket(paramSocket).setUseSessionTickets(paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/SSLCertificateSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */