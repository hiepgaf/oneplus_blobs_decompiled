package android.graphics;

public class ColorFilter
{
  public long native_instance;
  
  static native void destroyFilter(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      super.finalize();
      return;
    }
    finally
    {
      destroyFilter(this.native_instance);
      this.native_instance = 0L;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/ColorFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */