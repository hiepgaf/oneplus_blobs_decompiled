package android.security;

public class KeyStoreException
  extends Exception
{
  private final int mErrorCode;
  
  public KeyStoreException(int paramInt, String paramString)
  {
    super(paramString);
    this.mErrorCode = paramInt;
  }
  
  public int getErrorCode()
  {
    return this.mErrorCode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/KeyStoreException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */