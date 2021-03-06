package android.animation;

public class FloatArrayEvaluator
  implements TypeEvaluator<float[]>
{
  private float[] mArray;
  
  public FloatArrayEvaluator() {}
  
  public FloatArrayEvaluator(float[] paramArrayOfFloat)
  {
    this.mArray = paramArrayOfFloat;
  }
  
  public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float[] arrayOfFloat2 = this.mArray;
    float[] arrayOfFloat1 = arrayOfFloat2;
    if (arrayOfFloat2 == null) {
      arrayOfFloat1 = new float[paramArrayOfFloat1.length];
    }
    int i = 0;
    while (i < arrayOfFloat1.length)
    {
      float f = paramArrayOfFloat1[i];
      arrayOfFloat1[i] = ((paramArrayOfFloat2[i] - f) * paramFloat + f);
      i += 1;
    }
    return arrayOfFloat1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/FloatArrayEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */