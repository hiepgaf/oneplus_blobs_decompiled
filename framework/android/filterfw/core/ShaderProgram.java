package android.filterfw.core;

import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.opengl.GLES20;

public class ShaderProgram
  extends Program
{
  private GLEnvironment mGLEnvironment;
  private int mMaxTileSize = 0;
  private StopWatchMap mTimer = null;
  private int shaderProgramId;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  private ShaderProgram() {}
  
  public ShaderProgram(FilterContext paramFilterContext, String paramString)
  {
    this.mGLEnvironment = getGLEnvironment(paramFilterContext);
    allocate(this.mGLEnvironment, null, paramString);
    if (!compileAndLink()) {
      throw new RuntimeException("Could not compile and link shader!");
    }
    setTimer();
  }
  
  public ShaderProgram(FilterContext paramFilterContext, String paramString1, String paramString2)
  {
    this.mGLEnvironment = getGLEnvironment(paramFilterContext);
    allocate(this.mGLEnvironment, paramString1, paramString2);
    if (!compileAndLink()) {
      throw new RuntimeException("Could not compile and link shader!");
    }
    setTimer();
  }
  
  private ShaderProgram(NativeAllocatorTag paramNativeAllocatorTag) {}
  
  private native boolean allocate(GLEnvironment paramGLEnvironment, String paramString1, String paramString2);
  
  private native boolean beginShaderDrawing();
  
  private native boolean compileAndLink();
  
  public static ShaderProgram createIdentity(FilterContext paramFilterContext)
  {
    paramFilterContext = nativeCreateIdentity(getGLEnvironment(paramFilterContext));
    paramFilterContext.setTimer();
    return paramFilterContext;
  }
  
  private native boolean deallocate();
  
  private static GLEnvironment getGLEnvironment(FilterContext paramFilterContext)
  {
    GLEnvironment localGLEnvironment = null;
    if (paramFilterContext != null) {
      localGLEnvironment = paramFilterContext.getGLEnvironment();
    }
    if (localGLEnvironment == null) {
      throw new NullPointerException("Attempting to create ShaderProgram with no GL environment in place!");
    }
    return localGLEnvironment;
  }
  
  private native Object getUniformValue(String paramString);
  
  private static native ShaderProgram nativeCreateIdentity(GLEnvironment paramGLEnvironment);
  
  private native boolean setShaderAttributeValues(String paramString, float[] paramArrayOfFloat, int paramInt);
  
  private native boolean setShaderAttributeVertexFrame(String paramString, VertexFrame paramVertexFrame, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);
  
  private native boolean setShaderBlendEnabled(boolean paramBoolean);
  
  private native boolean setShaderBlendFunc(int paramInt1, int paramInt2);
  
  private native boolean setShaderClearColor(float paramFloat1, float paramFloat2, float paramFloat3);
  
  private native boolean setShaderClearsOutput(boolean paramBoolean);
  
  private native boolean setShaderDrawMode(int paramInt);
  
  private native boolean setShaderTileCounts(int paramInt1, int paramInt2);
  
  private native boolean setShaderVertexCount(int paramInt);
  
  private native boolean setTargetRegion(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8);
  
  private void setTimer()
  {
    this.mTimer = new StopWatchMap();
  }
  
  private native boolean setUniformValue(String paramString, Object paramObject);
  
  private native boolean shaderProcess(GLFrame[] paramArrayOfGLFrame, GLFrame paramGLFrame);
  
  public void beginDrawing()
  {
    if (!beginShaderDrawing()) {
      throw new RuntimeException("Could not prepare shader-program for drawing!");
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    deallocate();
  }
  
  public GLEnvironment getGLEnvironment()
  {
    return this.mGLEnvironment;
  }
  
  public Object getHostValue(String paramString)
  {
    return getUniformValue(paramString);
  }
  
  public void process(Frame[] paramArrayOfFrame, Frame paramFrame)
  {
    if (this.mTimer.LOG_MFF_RUNNING_TIMES)
    {
      this.mTimer.start("glFinish");
      GLES20.glFinish();
      this.mTimer.stop("glFinish");
    }
    GLFrame[] arrayOfGLFrame = new GLFrame[paramArrayOfFrame.length];
    int i = 0;
    while (i < paramArrayOfFrame.length) {
      if ((paramArrayOfFrame[i] instanceof GLFrame))
      {
        arrayOfGLFrame[i] = ((GLFrame)paramArrayOfFrame[i]);
        i += 1;
      }
      else
      {
        throw new RuntimeException("ShaderProgram got non-GL frame as input " + i + "!");
      }
    }
    if ((paramFrame instanceof GLFrame))
    {
      if (this.mMaxTileSize > 0) {
        setShaderTileCounts((paramFrame.getFormat().getWidth() + this.mMaxTileSize - 1) / this.mMaxTileSize, (paramFrame.getFormat().getHeight() + this.mMaxTileSize - 1) / this.mMaxTileSize);
      }
      if (!shaderProcess(arrayOfGLFrame, paramFrame)) {
        throw new RuntimeException("Error executing ShaderProgram!");
      }
    }
    else
    {
      throw new RuntimeException("ShaderProgram got non-GL output frame!");
    }
    if (this.mTimer.LOG_MFF_RUNNING_TIMES) {
      GLES20.glFinish();
    }
  }
  
  public void setAttributeValues(String paramString, VertexFrame paramVertexFrame, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (!setShaderAttributeVertexFrame(paramString, paramVertexFrame, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean)) {
      throw new RuntimeException("Error setting attribute value for attribute '" + paramString + "'!");
    }
  }
  
  public void setAttributeValues(String paramString, float[] paramArrayOfFloat, int paramInt)
  {
    if (!setShaderAttributeValues(paramString, paramArrayOfFloat, paramInt)) {
      throw new RuntimeException("Error setting attribute value for attribute '" + paramString + "'!");
    }
  }
  
  public void setBlendEnabled(boolean paramBoolean)
  {
    if (!setShaderBlendEnabled(paramBoolean)) {
      throw new RuntimeException("Could not set Blending " + paramBoolean + "!");
    }
  }
  
  public void setBlendFunc(int paramInt1, int paramInt2)
  {
    if (!setShaderBlendFunc(paramInt1, paramInt2)) {
      throw new RuntimeException("Could not set BlendFunc " + paramInt1 + "," + paramInt2 + "!");
    }
  }
  
  public void setClearColor(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (!setShaderClearColor(paramFloat1, paramFloat2, paramFloat3)) {
      throw new RuntimeException("Could not set clear color to " + paramFloat1 + "," + paramFloat2 + "," + paramFloat3 + "!");
    }
  }
  
  public void setClearsOutput(boolean paramBoolean)
  {
    if (!setShaderClearsOutput(paramBoolean)) {
      throw new RuntimeException("Could not set clears-output flag to " + paramBoolean + "!");
    }
  }
  
  public void setDrawMode(int paramInt)
  {
    if (!setShaderDrawMode(paramInt)) {
      throw new RuntimeException("Could not set GL draw-mode to " + paramInt + "!");
    }
  }
  
  public void setHostValue(String paramString, Object paramObject)
  {
    if (!setUniformValue(paramString, paramObject)) {
      throw new RuntimeException("Error setting uniform value for variable '" + paramString + "'!");
    }
  }
  
  public void setMaximumTileSize(int paramInt)
  {
    this.mMaxTileSize = paramInt;
  }
  
  public void setSourceRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    setSourceRegion(paramFloat1, paramFloat2, paramFloat1 + paramFloat3, paramFloat2, paramFloat1, paramFloat2 + paramFloat4, paramFloat1 + paramFloat3, paramFloat2 + paramFloat4);
  }
  
  public void setSourceRegion(Quad paramQuad)
  {
    setSourceRegion(paramQuad.p0.x, paramQuad.p0.y, paramQuad.p1.x, paramQuad.p1.y, paramQuad.p2.x, paramQuad.p2.y, paramQuad.p3.x, paramQuad.p3.y);
  }
  
  public native boolean setSourceRegion(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8);
  
  public void setTargetRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    setTargetRegion(paramFloat1, paramFloat2, paramFloat1 + paramFloat3, paramFloat2, paramFloat1, paramFloat2 + paramFloat4, paramFloat1 + paramFloat3, paramFloat2 + paramFloat4);
  }
  
  public void setTargetRegion(Quad paramQuad)
  {
    setTargetRegion(paramQuad.p0.x, paramQuad.p0.y, paramQuad.p1.x, paramQuad.p1.y, paramQuad.p2.x, paramQuad.p2.y, paramQuad.p3.x, paramQuad.p3.y);
  }
  
  public void setVertexCount(int paramInt)
  {
    if (!setShaderVertexCount(paramInt)) {
      throw new RuntimeException("Could not set GL vertex count to " + paramInt + "!");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/ShaderProgram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */