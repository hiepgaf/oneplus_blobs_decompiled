package android.net;

import com.android.org.conscrypt.PSKKeyManager;
import java.net.Socket;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLEngine;

public abstract class PskKeyManager
  implements PSKKeyManager
{
  public static final int MAX_IDENTITY_HINT_LENGTH_BYTES = 128;
  public static final int MAX_IDENTITY_LENGTH_BYTES = 128;
  public static final int MAX_KEY_LENGTH_BYTES = 256;
  
  public String chooseClientKeyIdentity(String paramString, Socket paramSocket)
  {
    return "";
  }
  
  public String chooseClientKeyIdentity(String paramString, SSLEngine paramSSLEngine)
  {
    return "";
  }
  
  public String chooseServerKeyIdentityHint(Socket paramSocket)
  {
    return null;
  }
  
  public String chooseServerKeyIdentityHint(SSLEngine paramSSLEngine)
  {
    return null;
  }
  
  public SecretKey getKey(String paramString1, String paramString2, Socket paramSocket)
  {
    return null;
  }
  
  public SecretKey getKey(String paramString1, String paramString2, SSLEngine paramSSLEngine)
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/PskKeyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */