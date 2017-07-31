package android.security.keystore;

import java.security.ProviderException;

public class KeyStoreConnectException
  extends ProviderException
{
  public KeyStoreConnectException()
  {
    super("Failed to communicate with keystore service");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyStoreConnectException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */