package android.graphics;

public class CornerPathEffect
  extends PathEffect
{
  public CornerPathEffect(float paramFloat)
  {
    this.native_instance = nativeCreate(paramFloat);
  }
  
  private static native long nativeCreate(float paramFloat);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/CornerPathEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */