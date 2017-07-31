package android.os;

public class ServiceSpecificException
  extends RuntimeException
{
  public final int errorCode;
  
  public ServiceSpecificException(int paramInt)
  {
    this.errorCode = paramInt;
  }
  
  public ServiceSpecificException(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ServiceSpecificException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */