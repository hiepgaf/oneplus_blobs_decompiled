package android.hardware.camera2.utils;

public class UncheckedThrow
{
  public static void throwAnyException(Exception paramException)
  {
    throwAnyImpl(paramException);
  }
  
  public static void throwAnyException(Throwable paramThrowable)
  {
    throwAnyImpl(paramThrowable);
  }
  
  private static <T extends Throwable> void throwAnyImpl(Throwable paramThrowable)
    throws Throwable
  {
    throw paramThrowable;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/UncheckedThrow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */