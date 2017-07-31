package android.graphics;

public class PaintFlagsDrawFilter
  extends DrawFilter
{
  public PaintFlagsDrawFilter(int paramInt1, int paramInt2)
  {
    this.mNativeInt = nativeConstructor(paramInt1, paramInt2);
  }
  
  private static native long nativeConstructor(int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PaintFlagsDrawFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */