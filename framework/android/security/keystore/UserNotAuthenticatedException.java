package android.security.keystore;

import java.security.InvalidKeyException;

public class UserNotAuthenticatedException
  extends InvalidKeyException
{
  public UserNotAuthenticatedException()
  {
    super("User not authenticated");
  }
  
  public UserNotAuthenticatedException(String paramString)
  {
    super(paramString);
  }
  
  public UserNotAuthenticatedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/UserNotAuthenticatedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */