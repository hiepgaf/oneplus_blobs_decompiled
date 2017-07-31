package android.os;

class ZygoteStartFailedEx
  extends Exception
{
  ZygoteStartFailedEx(String paramString)
  {
    super(paramString);
  }
  
  ZygoteStartFailedEx(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  ZygoteStartFailedEx(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ZygoteStartFailedEx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */