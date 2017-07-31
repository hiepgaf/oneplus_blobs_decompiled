package android.security.keystore;

import java.security.InvalidKeyException;

public class KeyNotYetValidException
  extends InvalidKeyException
{
  public KeyNotYetValidException()
  {
    super("Key not yet valid");
  }
  
  public KeyNotYetValidException(String paramString)
  {
    super(paramString);
  }
  
  public KeyNotYetValidException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyNotYetValidException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */