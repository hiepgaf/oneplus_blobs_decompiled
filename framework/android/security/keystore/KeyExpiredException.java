package android.security.keystore;

import java.security.InvalidKeyException;

public class KeyExpiredException
  extends InvalidKeyException
{
  public KeyExpiredException()
  {
    super("Key expired");
  }
  
  public KeyExpiredException(String paramString)
  {
    super(paramString);
  }
  
  public KeyExpiredException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyExpiredException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */