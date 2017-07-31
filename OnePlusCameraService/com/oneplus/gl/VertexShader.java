package com.oneplus.gl;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

public class VertexShader
  extends Shader
{
  public static final String VAR_MVP_MATRIX = "uMVPMatrix";
  public static final String VAR_OPACITY_MASK_TEXTURE_COORD = "vOpacityMaskTexCoord";
  public static final String VAR_SHARED_OPACITY_MASK_TEXTURE_COORD = "vSharedOpacityMaskTexCoord";
  public static final String VAR_SHARED_TEXTURE_COORD = "vSharedTexCoord";
  public static final String VAR_TEXTURE_COORD = "vTexCoord";
  public static final String VAR_TRANSFORM_MATRIX = "uTransformMatrix";
  public static final String VAR_VERTEX_POSITION = "vPosition";
  
  public VertexShader(String paramString)
  {
    super(35633, paramString);
  }
  
  protected boolean setMvpMatrix(Program paramProgram, float[] paramArrayOfFloat)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "uMVPMatrix");
    if (i >= 0)
    {
      GLES20.glUniformMatrix4fv(i, 1, false, paramArrayOfFloat, 0);
      return true;
    }
    return false;
  }
  
  protected boolean setOpacityMaskTexCoordBuffer(Program paramProgram, FloatBuffer paramFloatBuffer)
  {
    int i = GLES20.glGetAttribLocation(paramProgram.getObjectId(), "vOpacityMaskTexCoord");
    if (i >= 0)
    {
      if (paramFloatBuffer != null)
      {
        GLES20.glEnableVertexAttribArray(i);
        GLES20.glVertexAttribPointer(i, 2, 5126, false, 0, paramFloatBuffer);
      }
    }
    else {
      return false;
    }
    GLES20.glDisableVertexAttribArray(i);
    return false;
  }
  
  protected boolean setTexCoordBuffer(Program paramProgram, FloatBuffer paramFloatBuffer)
  {
    int i = GLES20.glGetAttribLocation(paramProgram.getObjectId(), "vTexCoord");
    if (i >= 0)
    {
      if (paramFloatBuffer != null)
      {
        GLES20.glEnableVertexAttribArray(i);
        GLES20.glVertexAttribPointer(i, 2, 5126, false, 0, paramFloatBuffer);
      }
    }
    else {
      return false;
    }
    GLES20.glDisableVertexAttribArray(i);
    return false;
  }
  
  protected boolean setTransformMatrix(Program paramProgram, float[] paramArrayOfFloat)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "uTransformMatrix");
    if (i >= 0)
    {
      GLES20.glUniformMatrix4fv(i, 1, false, paramArrayOfFloat, 0);
      return true;
    }
    return false;
  }
  
  protected boolean setVertexPositionBuffer(Program paramProgram, FloatBuffer paramFloatBuffer)
  {
    int i = GLES20.glGetAttribLocation(paramProgram.getObjectId(), "vPosition");
    if (i >= 0)
    {
      if (paramFloatBuffer != null)
      {
        GLES20.glEnableVertexAttribArray(i);
        GLES20.glVertexAttribPointer(i, 3, 5126, false, 0, paramFloatBuffer);
      }
    }
    else {
      return false;
    }
    GLES20.glDisableVertexAttribArray(i);
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/VertexShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */