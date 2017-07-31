package android.security.keystore;

import java.security.InvalidKeyException;

public class KeyPermanentlyInvalidatedException
  extends InvalidKeyException
{
  public KeyPermanentlyInvalidatedException()
  {
    super("Key permanently invalidated");
  }
  
  public KeyPermanentlyInvalidatedException(String paramString)
  {
    super(paramString);
  }
  
  public KeyPermanentlyInvalidatedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyPermanentlyInvalidatedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */