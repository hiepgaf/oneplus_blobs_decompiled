package android.os;

import android.util.AndroidRuntimeException;

public class BadParcelableException
  extends AndroidRuntimeException
{
  public BadParcelableException(Exception paramException)
  {
    super(paramException);
  }
  
  public BadParcelableException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BadParcelableException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */