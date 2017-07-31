package android.graphics;

public class SweepGradient
  extends Shader
{
  private static final int TYPE_COLORS_AND_POSITIONS = 1;
  private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
  private int mColor0;
  private int mColor1;
  private int[] mColors;
  private float mCx;
  private float mCy;
  private float[] mPositions;
  private int mType;
  
  public SweepGradient(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    this.mType = 2;
    this.mCx = paramFloat1;
    this.mCy = paramFloat2;
    this.mColor0 = paramInt1;
    this.mColor1 = paramInt2;
    init(nativeCreate2(paramFloat1, paramFloat2, paramInt1, paramInt2));
  }
  
  public SweepGradient(float paramFloat1, float paramFloat2, int[] paramArrayOfInt, float[] paramArrayOfFloat)
  {
    if (paramArrayOfInt.length < 2) {
      throw new IllegalArgumentException("needs >= 2 number of colors");
    }
    if ((paramArrayOfFloat != null) && (paramArrayOfInt.length != paramArrayOfFloat.length)) {
      throw new IllegalArgumentException("color and position arrays must be of equal length");
    }
    this.mType = 1;
    this.mCx = paramFloat1;
    this.mCy = paramFloat2;
    this.mColors = paramArrayOfInt;
    this.mPositions = paramArrayOfFloat;
    init(nativeCreate1(paramFloat1, paramFloat2, paramArrayOfInt, paramArrayOfFloat));
  }
  
  private static native long nativeCreate1(float paramFloat1, float paramFloat2, int[] paramArrayOfInt, float[] paramArrayOfFloat);
  
  private static native long nativeCreate2(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2);
  
  protected Shader copy()
  {
    Object localObject = null;
    float f1;
    float f2;
    int[] arrayOfInt;
    switch (this.mType)
    {
    default: 
      throw new IllegalArgumentException("SweepGradient should be created with either colors and positions or start color and end color");
    case 1: 
      f1 = this.mCx;
      f2 = this.mCy;
      arrayOfInt = (int[])this.mColors.clone();
      if (this.mPositions != null) {
        localObject = (float[])this.mPositions.clone();
      }
      break;
    }
    for (localObject = new SweepGradient(f1, f2, arrayOfInt, (float[])localObject);; localObject = new SweepGradient(this.mCx, this.mCy, this.mColor0, this.mColor1))
    {
      copyLocalMatrix((Shader)localObject);
      return (Shader)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/SweepGradient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */