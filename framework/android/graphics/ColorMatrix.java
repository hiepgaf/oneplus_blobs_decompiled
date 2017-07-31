package android.graphics;

import java.util.Arrays;

public class ColorMatrix
{
  private final float[] mArray = new float[20];
  
  public ColorMatrix()
  {
    reset();
  }
  
  public ColorMatrix(ColorMatrix paramColorMatrix)
  {
    System.arraycopy(paramColorMatrix.mArray, 0, this.mArray, 0, 20);
  }
  
  public ColorMatrix(float[] paramArrayOfFloat)
  {
    System.arraycopy(paramArrayOfFloat, 0, this.mArray, 0, 20);
  }
  
  public final float[] getArray()
  {
    return this.mArray;
  }
  
  public void postConcat(ColorMatrix paramColorMatrix)
  {
    setConcat(paramColorMatrix, this);
  }
  
  public void preConcat(ColorMatrix paramColorMatrix)
  {
    setConcat(this, paramColorMatrix);
  }
  
  public void reset()
  {
    float[] arrayOfFloat = this.mArray;
    Arrays.fill(arrayOfFloat, 0.0F);
    arrayOfFloat[18] = 1.0F;
    arrayOfFloat[12] = 1.0F;
    arrayOfFloat[6] = 1.0F;
    arrayOfFloat[0] = 1.0F;
  }
  
  public void set(ColorMatrix paramColorMatrix)
  {
    System.arraycopy(paramColorMatrix.mArray, 0, this.mArray, 0, 20);
  }
  
  public void set(float[] paramArrayOfFloat)
  {
    System.arraycopy(paramArrayOfFloat, 0, this.mArray, 0, 20);
  }
  
  public void setConcat(ColorMatrix paramColorMatrix1, ColorMatrix paramColorMatrix2)
  {
    float[] arrayOfFloat;
    int i;
    int j;
    if ((paramColorMatrix1 == this) || (paramColorMatrix2 == this))
    {
      arrayOfFloat = new float[20];
      paramColorMatrix1 = paramColorMatrix1.mArray;
      paramColorMatrix2 = paramColorMatrix2.mArray;
      i = 0;
      j = 0;
    }
    for (;;)
    {
      if (j >= 20) {
        break label204;
      }
      int k = 0;
      for (;;)
      {
        if (k < 4)
        {
          arrayOfFloat[i] = (paramColorMatrix1[(j + 0)] * paramColorMatrix2[(k + 0)] + paramColorMatrix1[(j + 1)] * paramColorMatrix2[(k + 5)] + paramColorMatrix1[(j + 2)] * paramColorMatrix2[(k + 10)] + paramColorMatrix1[(j + 3)] * paramColorMatrix2[(k + 15)]);
          k += 1;
          i += 1;
          continue;
          arrayOfFloat = this.mArray;
          break;
        }
      }
      k = i + 1;
      arrayOfFloat[i] = (paramColorMatrix1[(j + 0)] * paramColorMatrix2[4] + paramColorMatrix1[(j + 1)] * paramColorMatrix2[9] + paramColorMatrix1[(j + 2)] * paramColorMatrix2[14] + paramColorMatrix1[(j + 3)] * paramColorMatrix2[19] + paramColorMatrix1[(j + 4)]);
      j += 5;
      i = k;
    }
    label204:
    if (arrayOfFloat != this.mArray) {
      System.arraycopy(arrayOfFloat, 0, this.mArray, 0, 20);
    }
  }
  
  public void setRGB2YUV()
  {
    reset();
    float[] arrayOfFloat = this.mArray;
    arrayOfFloat[0] = 0.299F;
    arrayOfFloat[1] = 0.587F;
    arrayOfFloat[2] = 0.114F;
    arrayOfFloat[5] = -0.16874F;
    arrayOfFloat[6] = -0.33126F;
    arrayOfFloat[7] = 0.5F;
    arrayOfFloat[10] = 0.5F;
    arrayOfFloat[11] = -0.41869F;
    arrayOfFloat[12] = -0.08131F;
  }
  
  public void setRotate(int paramInt, float paramFloat)
  {
    reset();
    double d = paramFloat * 3.141592653589793D / 180.0D;
    paramFloat = (float)Math.cos(d);
    float f = (float)Math.sin(d);
    switch (paramInt)
    {
    default: 
      throw new RuntimeException();
    case 0: 
      arrayOfFloat = this.mArray;
      this.mArray[12] = paramFloat;
      arrayOfFloat[6] = paramFloat;
      this.mArray[7] = f;
      this.mArray[11] = (-f);
      return;
    case 1: 
      arrayOfFloat = this.mArray;
      this.mArray[12] = paramFloat;
      arrayOfFloat[0] = paramFloat;
      this.mArray[2] = (-f);
      this.mArray[10] = f;
      return;
    }
    float[] arrayOfFloat = this.mArray;
    this.mArray[6] = paramFloat;
    arrayOfFloat[0] = paramFloat;
    this.mArray[1] = f;
    this.mArray[5] = (-f);
  }
  
  public void setSaturation(float paramFloat)
  {
    reset();
    float[] arrayOfFloat = this.mArray;
    float f3 = 1.0F - paramFloat;
    float f1 = 0.213F * f3;
    float f2 = 0.715F * f3;
    f3 = 0.072F * f3;
    arrayOfFloat[0] = (f1 + paramFloat);
    arrayOfFloat[1] = f2;
    arrayOfFloat[2] = f3;
    arrayOfFloat[5] = f1;
    arrayOfFloat[6] = (f2 + paramFloat);
    arrayOfFloat[7] = f3;
    arrayOfFloat[10] = f1;
    arrayOfFloat[11] = f2;
    arrayOfFloat[12] = (f3 + paramFloat);
  }
  
  public void setScale(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float[] arrayOfFloat = this.mArray;
    int i = 19;
    while (i > 0)
    {
      arrayOfFloat[i] = 0.0F;
      i -= 1;
    }
    arrayOfFloat[0] = paramFloat1;
    arrayOfFloat[6] = paramFloat2;
    arrayOfFloat[12] = paramFloat3;
    arrayOfFloat[18] = paramFloat4;
  }
  
  public void setYUV2RGB()
  {
    reset();
    float[] arrayOfFloat = this.mArray;
    arrayOfFloat[2] = 1.402F;
    arrayOfFloat[5] = 1.0F;
    arrayOfFloat[6] = -0.34414F;
    arrayOfFloat[7] = -0.71414F;
    arrayOfFloat[10] = 1.0F;
    arrayOfFloat[11] = 1.772F;
    arrayOfFloat[12] = 0.0F;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/ColorMatrix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */