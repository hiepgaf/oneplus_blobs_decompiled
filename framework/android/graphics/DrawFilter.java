package android.graphics;

public class DrawFilter
{
  public long mNativeInt;
  
  private static native void nativeDestructor(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDestructor(this.mNativeInt);
      this.mNativeInt = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/DrawFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */