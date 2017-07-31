package com.oneplus.gl;

import java.nio.FloatBuffer;

public class SimpleVertexShader
  extends VertexShader
{
  private static final String SHADER_SCRIPT = "attribute vec4 vPosition;uniform mat4 uMVPMatrix;uniform mat4 uTransformMatrix;attribute vec2 vTexCoord;attribute vec2 vOpacityMaskTexCoord;varying vec2 vSharedTexCoord;varying vec2 vSharedOpacityMaskTexCoord;void main(){  vSharedTexCoord = vTexCoord;  vSharedOpacityMaskTexCoord = vOpacityMaskTexCoord;  gl_Position = uMVPMatrix * uTransformMatrix * vPosition;}";
  
  public SimpleVertexShader()
  {
    super("attribute vec4 vPosition;uniform mat4 uMVPMatrix;uniform mat4 uTransformMatrix;attribute vec2 vTexCoord;attribute vec2 vOpacityMaskTexCoord;varying vec2 vSharedTexCoord;varying vec2 vSharedOpacityMaskTexCoord;void main(){  vSharedTexCoord = vTexCoord;  vSharedOpacityMaskTexCoord = vOpacityMaskTexCoord;  gl_Position = uMVPMatrix * uTransformMatrix * vPosition;}");
  }
  
  protected void onComplete(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase)
  {
    setVertexPositionBuffer(paramProgram, null);
    setTexCoordBuffer(paramProgram, null);
    setOpacityMaskTexCoordBuffer(paramProgram, null);
    super.onComplete(paramDrawingContext, paramProgram, paramModelBase);
  }
  
  protected void onPrepare(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase)
  {
    super.onPrepare(paramDrawingContext, paramProgram, paramModelBase);
    Object localObject = paramModelBase.getVertexBuffer(paramDrawingContext);
    if (localObject != null) {
      setVertexPositionBuffer(paramProgram, (FloatBuffer)localObject);
    }
    setMvpMatrix(paramProgram, paramDrawingContext.getMvpMatrix());
    setTransformMatrix(paramProgram, paramModelBase.getTransformMatrix());
    localObject = paramModelBase.getFragmentShader();
    if ((localObject != null) && (((FragmentShader)localObject).isTextureCoordinateNeeded())) {
      setTexCoordBuffer(paramProgram, paramModelBase.getTexCoordBuffer(paramDrawingContext));
    }
    if (paramModelBase.getOpacityMask() != null) {
      setOpacityMaskTexCoordBuffer(paramProgram, paramModelBase.getOpacityMaskTexCoordBuffer(paramDrawingContext));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/SimpleVertexShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */