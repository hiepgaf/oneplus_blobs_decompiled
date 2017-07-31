package android.graphics;

public class PathEffect
{
  long native_instance;
  
  private static native void nativeDestructor(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    nativeDestructor(this.native_instance);
    this.native_instance = 0L;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PathEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */