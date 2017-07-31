package android.graphics;

public class ComposePathEffect
  extends PathEffect
{
  public ComposePathEffect(PathEffect paramPathEffect1, PathEffect paramPathEffect2)
  {
    this.native_instance = nativeCreate(paramPathEffect1.native_instance, paramPathEffect2.native_instance);
  }
  
  private static native long nativeCreate(long paramLong1, long paramLong2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/ComposePathEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */