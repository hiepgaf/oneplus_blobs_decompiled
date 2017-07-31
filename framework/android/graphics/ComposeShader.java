package android.graphics;

public class ComposeShader
  extends Shader
{
  private static final int TYPE_PORTERDUFFMODE = 2;
  private static final int TYPE_XFERMODE = 1;
  private PorterDuff.Mode mPorterDuffMode;
  private final Shader mShaderA;
  private final Shader mShaderB;
  private int mType;
  private Xfermode mXferMode;
  
  public ComposeShader(Shader paramShader1, Shader paramShader2, PorterDuff.Mode paramMode)
  {
    this.mType = 2;
    this.mShaderA = paramShader1;
    this.mShaderB = paramShader2;
    this.mPorterDuffMode = paramMode;
    init(nativeCreate2(paramShader1.getNativeInstance(), paramShader2.getNativeInstance(), paramMode.nativeInt));
  }
  
  public ComposeShader(Shader paramShader1, Shader paramShader2, Xfermode paramXfermode)
  {
    this.mType = 1;
    this.mShaderA = paramShader1;
    this.mShaderB = paramShader2;
    this.mXferMode = paramXfermode;
    long l2 = paramShader1.getNativeInstance();
    long l3 = paramShader2.getNativeInstance();
    if (paramXfermode != null) {}
    for (long l1 = paramXfermode.native_instance;; l1 = 0L)
    {
      init(nativeCreate1(l2, l3, l1));
      return;
    }
  }
  
  private static native long nativeCreate1(long paramLong1, long paramLong2, long paramLong3);
  
  private static native long nativeCreate2(long paramLong1, long paramLong2, int paramInt);
  
  protected Shader copy()
  {
    switch (this.mType)
    {
    default: 
      throw new IllegalArgumentException("ComposeShader should be created with either Xfermode or PorterDuffMode");
    }
    for (ComposeShader localComposeShader = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mXferMode);; localComposeShader = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mPorterDuffMode))
    {
      copyLocalMatrix(localComposeShader);
      return localComposeShader;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/ComposeShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */