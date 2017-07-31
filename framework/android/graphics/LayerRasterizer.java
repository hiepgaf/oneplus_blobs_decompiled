package android.graphics;

@Deprecated
public class LayerRasterizer
  extends Rasterizer
{
  public LayerRasterizer()
  {
    this.native_instance = nativeConstructor();
  }
  
  private static native void nativeAddLayer(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2);
  
  private static native long nativeConstructor();
  
  public void addLayer(Paint paramPaint)
  {
    nativeAddLayer(this.native_instance, paramPaint.getNativeInstance(), 0.0F, 0.0F);
  }
  
  public void addLayer(Paint paramPaint, float paramFloat1, float paramFloat2)
  {
    nativeAddLayer(this.native_instance, paramPaint.getNativeInstance(), paramFloat1, paramFloat2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/LayerRasterizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */