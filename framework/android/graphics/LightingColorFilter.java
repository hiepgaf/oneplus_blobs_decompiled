package android.graphics;

public class LightingColorFilter
  extends ColorFilter
{
  private int mAdd;
  private int mMul;
  
  public LightingColorFilter(int paramInt1, int paramInt2)
  {
    this.mMul = paramInt1;
    this.mAdd = paramInt2;
    update();
  }
  
  private static native long native_CreateLightingFilter(int paramInt1, int paramInt2);
  
  private void update()
  {
    destroyFilter(this.native_instance);
    this.native_instance = native_CreateLightingFilter(this.mMul, this.mAdd);
  }
  
  public int getColorAdd()
  {
    return this.mAdd;
  }
  
  public int getColorMultiply()
  {
    return this.mMul;
  }
  
  public void setColorAdd(int paramInt)
  {
    this.mAdd = paramInt;
    update();
  }
  
  public void setColorMultiply(int paramInt)
  {
    this.mMul = paramInt;
    update();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/LightingColorFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */