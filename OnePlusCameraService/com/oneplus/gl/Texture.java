package com.oneplus.gl;

import android.opengl.GLES20;

public abstract class Texture
  extends EglObject
{
  public static final int TYPE_2D = 3553;
  public static final int TYPE_EXTERNAL_OES = 36197;
  private final int m_Type;
  
  protected Texture(int paramInt)
  {
    this.m_Type = paramInt;
  }
  
  static int createNativeTexture()
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    if (arrayOfInt[0] != 0) {
      return arrayOfInt[0];
    }
    EglContextManager.throwEglError("Fail to generate texture");
    return 0;
  }
  
  static void destroyNativeTexture(int paramInt)
  {
    if (paramInt != 0) {
      GLES20.glDeleteTextures(1, new int[] { paramInt }, 0);
    }
  }
  
  public final int getType()
  {
    return this.m_Type;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Texture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */