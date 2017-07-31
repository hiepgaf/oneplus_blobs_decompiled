package android.animation;

public class IntArrayEvaluator
  implements TypeEvaluator<int[]>
{
  private int[] mArray;
  
  public IntArrayEvaluator() {}
  
  public IntArrayEvaluator(int[] paramArrayOfInt)
  {
    this.mArray = paramArrayOfInt;
  }
  
  public int[] evaluate(float paramFloat, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int[] arrayOfInt2 = this.mArray;
    int[] arrayOfInt1 = arrayOfInt2;
    if (arrayOfInt2 == null) {
      arrayOfInt1 = new int[paramArrayOfInt1.length];
    }
    int i = 0;
    while (i < arrayOfInt1.length)
    {
      int j = paramArrayOfInt1[i];
      int k = paramArrayOfInt2[i];
      arrayOfInt1[i] = ((int)(j + (k - j) * paramFloat));
      i += 1;
    }
    return arrayOfInt1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/IntArrayEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */