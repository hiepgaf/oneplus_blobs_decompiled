package android.animation;

public class ArgbEvaluator
  implements TypeEvaluator
{
  private static final ArgbEvaluator sInstance = new ArgbEvaluator();
  
  public static ArgbEvaluator getInstance()
  {
    return sInstance;
  }
  
  public Object evaluate(float paramFloat, Object paramObject1, Object paramObject2)
  {
    int m = ((Integer)paramObject1).intValue();
    int i = m >> 24 & 0xFF;
    int j = m >> 16 & 0xFF;
    int k = m >> 8 & 0xFF;
    m &= 0xFF;
    int n = ((Integer)paramObject2).intValue();
    return Integer.valueOf((int)(((n >> 24 & 0xFF) - i) * paramFloat) + i << 24 | (int)(((n >> 16 & 0xFF) - j) * paramFloat) + j << 16 | (int)(((n >> 8 & 0xFF) - k) * paramFloat) + k << 8 | (int)(((n & 0xFF) - m) * paramFloat) + m);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/ArgbEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */