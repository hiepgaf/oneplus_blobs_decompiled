package android.graphics;

public class DiscretePathEffect
  extends PathEffect
{
  public DiscretePathEffect(float paramFloat1, float paramFloat2)
  {
    this.native_instance = nativeCreate(paramFloat1, paramFloat2);
  }
  
  private static native long nativeCreate(float paramFloat1, float paramFloat2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/DiscretePathEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */