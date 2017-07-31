package android.graphics;

public class EmbossMaskFilter
  extends MaskFilter
{
  public EmbossMaskFilter(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramArrayOfFloat.length < 3) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.native_instance = nativeConstructor(paramArrayOfFloat, paramFloat1, paramFloat2, paramFloat3);
  }
  
  private static native long nativeConstructor(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/EmbossMaskFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */