package android.graphics;

public class ColorMatrixColorFilter
  extends ColorFilter
{
  private final ColorMatrix mMatrix = new ColorMatrix();
  
  public ColorMatrixColorFilter(ColorMatrix paramColorMatrix)
  {
    this.mMatrix.set(paramColorMatrix);
    update();
  }
  
  public ColorMatrixColorFilter(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 20) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.mMatrix.set(paramArrayOfFloat);
    update();
  }
  
  private static native long nativeColorMatrixFilter(float[] paramArrayOfFloat);
  
  private void update()
  {
    float[] arrayOfFloat = this.mMatrix.getArray();
    destroyFilter(this.native_instance);
    this.native_instance = nativeColorMatrixFilter(arrayOfFloat);
  }
  
  public ColorMatrix getColorMatrix()
  {
    return this.mMatrix;
  }
  
  public void setColorMatrix(ColorMatrix paramColorMatrix)
  {
    if (paramColorMatrix == null) {
      this.mMatrix.reset();
    }
    for (;;)
    {
      update();
      return;
      if (paramColorMatrix != this.mMatrix) {
        this.mMatrix.set(paramColorMatrix);
      }
    }
  }
  
  public void setColorMatrix(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      this.mMatrix.reset();
    }
    for (;;)
    {
      update();
      return;
      if (paramArrayOfFloat.length < 20) {
        throw new ArrayIndexOutOfBoundsException();
      }
      this.mMatrix.set(paramArrayOfFloat);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/ColorMatrixColorFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */