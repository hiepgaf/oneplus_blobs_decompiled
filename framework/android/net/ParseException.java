package android.net;

public class ParseException
  extends RuntimeException
{
  public String response;
  
  ParseException(String paramString)
  {
    this.response = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ParseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */