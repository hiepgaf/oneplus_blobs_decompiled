package android.graphics;

public class Xfermode
{
  long native_instance;
  
  private static native void finalizer(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      finalizer(this.native_instance);
      this.native_instance = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Xfermode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */