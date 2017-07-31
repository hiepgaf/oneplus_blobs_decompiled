package android.graphics;

public class LinearGradient
  extends Shader
{
  private static final int TYPE_COLORS_AND_POSITIONS = 1;
  private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
  private int mColor0;
  private int mColor1;
  private int[] mColors;
  private float[] mPositions;
  private Shader.TileMode mTileMode;
  private int mType;
  private float mX0;
  private float mX1;
  private float mY0;
  private float mY1;
  
  public LinearGradient(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, Shader.TileMode paramTileMode)
  {
    this.mType = 2;
    this.mX0 = paramFloat1;
    this.mY0 = paramFloat2;
    this.mX1 = paramFloat3;
    this.mY1 = paramFloat4;
    this.mColor0 = paramInt1;
    this.mColor1 = paramInt2;
    this.mTileMode = paramTileMode;
    init(nativeCreate2(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1, paramInt2, paramTileMode.nativeInt));
  }
  
  public LinearGradient(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int[] paramArrayOfInt, float[] paramArrayOfFloat, Shader.TileMode paramTileMode)
  {
    if (paramArrayOfInt.length < 2) {
      throw new IllegalArgumentException("needs >= 2 number of colors");
    }
    if ((paramArrayOfFloat != null) && (paramArrayOfInt.length != paramArrayOfFloat.length)) {
      throw new IllegalArgumentException("color and position arrays must be of equal length");
    }
    this.mType = 1;
    this.mX0 = paramFloat1;
    this.mY0 = paramFloat2;
    this.mX1 = paramFloat3;
    this.mY1 = paramFloat4;
    this.mColors = paramArrayOfInt;
    this.mPositions = paramArrayOfFloat;
    this.mTileMode = paramTileMode;
    init(nativeCreate1(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramArrayOfInt, paramArrayOfFloat, paramTileMode.nativeInt));
  }
  
  private native long nativeCreate1(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int[] paramArrayOfInt, float[] paramArrayOfFloat, int paramInt);
  
  private native long nativeCreate2(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3);
  
  protected Shader copy()
  {
    Object localObject = null;
    float f1;
    float f2;
    float f3;
    float f4;
    int[] arrayOfInt;
    switch (this.mType)
    {
    default: 
      throw new IllegalArgumentException("LinearGradient should be created with either colors and positions or start color and end color");
    case 1: 
      f1 = this.mX0;
      f2 = this.mY0;
      f3 = this.mX1;
      f4 = this.mY1;
      arrayOfInt = (int[])this.mColors.clone();
      if (this.mPositions != null) {
        localObject = (float[])this.mPositions.clone();
      }
      break;
    }
    for (localObject = new LinearGradient(f1, f2, f3, f4, arrayOfInt, (float[])localObject, this.mTileMode);; localObject = new LinearGradient(this.mX0, this.mY0, this.mX1, this.mY1, this.mColor0, this.mColor1, this.mTileMode))
    {
      copyLocalMatrix((Shader)localObject);
      return (Shader)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/LinearGradient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */