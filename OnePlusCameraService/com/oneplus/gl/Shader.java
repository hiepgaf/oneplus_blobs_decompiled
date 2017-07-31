package com.oneplus.gl;

import android.opengl.Matrix;

public abstract class Shader
  extends EglObject
{
  protected static final float[] IDENTITY_MATRIX = new float[16];
  public static final int TYPE_FRAGMENT_SHADER = 35632;
  public static final int TYPE_VERTEX_SHADER = 35633;
  private int m_Id;
  private String m_Source;
  private final int m_Type;
  
  static
  {
    Matrix.setIdentityM(IDENTITY_MATRIX, 0);
  }
  
  Shader(int paramInt, String paramString)
  {
    this.m_Source = paramString;
    this.m_Type = paramInt;
  }
  
  public int getObjectId()
  {
    throwIfNotAccessible();
    if (this.m_Id <= 0)
    {
      this.m_Id = ShaderHolder.createShader(this.m_Type, this.m_Source);
      if (this.m_Id <= 0) {
        EglContextManager.throwEglError("Fail to create shader");
      }
    }
    return this.m_Id;
  }
  
  public final int getType()
  {
    return this.m_Type;
  }
  
  protected void onComplete(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase) {}
  
  protected void onEglContextDestroying()
  {
    this.m_Id = 0;
    super.onEglContextDestroying();
  }
  
  protected void onPrepare(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase) {}
  
  protected void onRelease()
  {
    if (this.m_Id > 0)
    {
      ShaderHolder.deleteShader(this.m_Id);
      this.m_Id = 0;
    }
    this.m_Source = null;
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Shader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */