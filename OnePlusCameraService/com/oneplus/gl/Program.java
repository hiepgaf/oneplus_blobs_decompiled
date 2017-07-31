package com.oneplus.gl;

import android.opengl.GLES20;
import java.util.ArrayList;

public class Program
  extends EglObject
{
  private int m_Id;
  private final ArrayList<Shader> m_OwnedShaders = new ArrayList(2);
  private final ArrayList<Shader> m_Shaders = new ArrayList(2);
  
  public final Program addShader(Shader paramShader)
  {
    return addShader(paramShader, false);
  }
  
  public final Program addShader(Shader paramShader, boolean paramBoolean)
  {
    throwIfNotAccessible();
    if (this.m_Id > 0) {
      throw new RuntimeException("Program has already be linked");
    }
    this.m_Shaders.add(paramShader);
    if (paramBoolean) {
      this.m_OwnedShaders.add(paramShader);
    }
    return this;
  }
  
  public int getObjectId()
  {
    throwIfNotAccessible();
    if (this.m_Id <= 0)
    {
      this.m_Id = GLES20.glCreateProgram();
      if (this.m_Id <= 0) {
        break label79;
      }
      int i = this.m_Shaders.size() - 1;
      while (i >= 0)
      {
        GLES20.glAttachShader(this.m_Id, ((Shader)this.m_Shaders.get(i)).getObjectId());
        i -= 1;
      }
      GLES20.glLinkProgram(this.m_Id);
    }
    for (;;)
    {
      return this.m_Id;
      label79:
      EglContextManager.throwEglError("Fail to create program");
    }
  }
  
  protected void onEglContextDestroying()
  {
    this.m_Id = 0;
    super.onEglContextDestroying();
  }
  
  protected void onRelease()
  {
    if (this.m_Id > 0)
    {
      GLES20.glDeleteProgram(this.m_Id);
      this.m_Id = 0;
    }
    int i = this.m_OwnedShaders.size() - 1;
    while (i >= 0)
    {
      EglObject.release((Shader)this.m_OwnedShaders.get(i));
      i -= 1;
    }
    this.m_OwnedShaders.clear();
    this.m_Shaders.clear();
    super.onRelease();
  }
  
  public final Program removeShader(Shader paramShader)
  {
    verifyAccess();
    if (this.m_Id > 0) {
      throw new RuntimeException("Program has already be linked");
    }
    this.m_Shaders.remove(paramShader);
    this.m_OwnedShaders.remove(paramShader);
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Program.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */