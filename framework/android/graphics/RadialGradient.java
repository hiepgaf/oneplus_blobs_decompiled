package android.graphics;

public class RadialGradient
  extends Shader
{
  private static final int TYPE_COLORS_AND_POSITIONS = 1;
  private static final int TYPE_COLOR_CENTER_AND_COLOR_EDGE = 2;
  private int mCenterColor;
  private int[] mColors;
  private int mEdgeColor;
  private float[] mPositions;
  private float mRadius;
  private Shader.TileMode mTileMode;
  private int mType;
  private float mX;
  private float mY;
  
  public RadialGradient(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2, Shader.TileMode paramTileMode)
  {
    if (paramFloat3 <= 0.0F) {
      throw new IllegalArgumentException("radius must be > 0");
    }
    this.mType = 2;
    this.mX = paramFloat1;
    this.mY = paramFloat2;
    this.mRadius = paramFloat3;
    this.mCenterColor = paramInt1;
    this.mEdgeColor = paramInt2;
    this.mTileMode = paramTileMode;
    init(nativeCreate2(paramFloat1, paramFloat2, paramFloat3, paramInt1, paramInt2, paramTileMode.nativeInt));
  }
  
  public RadialGradient(float paramFloat1, float paramFloat2, float paramFloat3, int[] paramArrayOfInt, float[] paramArrayOfFloat, Shader.TileMode paramTileMode)
  {
    if (paramFloat3 <= 0.0F) {
      throw new IllegalArgumentException("radius must be > 0");
    }
    if (paramArrayOfInt.length < 2) {
      throw new IllegalArgumentException("needs >= 2 number of colors");
    }
    if ((paramArrayOfFloat != null) && (paramArrayOfInt.length != paramArrayOfFloat.length)) {
      throw new IllegalArgumentException("color and position arrays must be of equal length");
    }
    this.mType = 1;
    this.mX = paramFloat1;
    this.mY = paramFloat2;
    this.mRadius = paramFloat3;
    this.mColors = paramArrayOfInt;
    this.mPositions = paramArrayOfFloat;
    this.mTileMode = paramTileMode;
    init(nativeCreate1(paramFloat1, paramFloat2, paramFloat3, paramArrayOfInt, paramArrayOfFloat, paramTileMode.nativeInt));
  }
  
  private static native long nativeCreate1(float paramFloat1, float paramFloat2, float paramFloat3, int[] paramArrayOfInt, float[] paramArrayOfFloat, int paramInt);
  
  private static native long nativeCreate2(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2, int paramInt3);
  
  protected Shader copy()
  {
    Object localObject = null;
    float f1;
    float f2;
    float f3;
    int[] arrayOfInt;
    switch (this.mType)
    {
    default: 
      throw new IllegalArgumentException("RadialGradient should be created with either colors and positions or center color and edge color");
    case 1: 
      f1 = this.mX;
      f2 = this.mY;
      f3 = this.mRadius;
      arrayOfInt = (int[])this.mColors.clone();
      if (this.mPositions != null) {
        localObject = (float[])this.mPositions.clone();
      }
      break;
    }
    for (localObject = new RadialGradient(f1, f2, f3, arrayOfInt, (float[])localObject, this.mTileMode);; localObject = new RadialGradient(this.mX, this.mY, this.mRadius, this.mCenterColor, this.mEdgeColor, this.mTileMode))
    {
      copyLocalMatrix((Shader)localObject);
      return (Shader)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/RadialGradient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */