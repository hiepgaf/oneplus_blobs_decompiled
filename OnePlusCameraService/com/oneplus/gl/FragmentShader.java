package com.oneplus.gl;

import android.opengl.GLES20;

public class FragmentShader
  extends Shader
{
  public static final String VAR_HAS_OPACITY_MASK = "bHasOpacityMask";
  public static final String VAR_OPACITY_MASK_TEXTURE_UNIT = "sOpacityMask";
  
  public FragmentShader(String paramString)
  {
    super(35632, paramString);
  }
  
  protected void disableOpacityMaskTexture(Program paramProgram)
  {
    GLES20.glUniform1i(GLES20.glGetUniformLocation(paramProgram.getObjectId(), "bHasOpacityMask"), 0);
  }
  
  protected boolean enableOpacityMaskTexture(Program paramProgram)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "bHasOpacityMask");
    if (i >= 0)
    {
      GLES20.glUniform1i(i, 1);
      return true;
    }
    return false;
  }
  
  public boolean hasAlphaBlending()
  {
    return false;
  }
  
  public boolean isTextureCoordinateNeeded()
  {
    return true;
  }
  
  protected boolean setOpacityMaskTextureUnit(Program paramProgram, int paramInt)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "sOpacityMask");
    if (i >= 0)
    {
      GLES20.glUniform1i(i, paramInt);
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/FragmentShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */