package android.graphics;

public class DashPathEffect
  extends PathEffect
{
  public DashPathEffect(float[] paramArrayOfFloat, float paramFloat)
  {
    if (paramArrayOfFloat.length < 2) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.native_instance = nativeCreate(paramArrayOfFloat, paramFloat);
  }
  
  private static native long nativeCreate(float[] paramArrayOfFloat, float paramFloat);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/DashPathEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */