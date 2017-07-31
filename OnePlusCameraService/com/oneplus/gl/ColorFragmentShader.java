package com.oneplus.gl;

import android.graphics.Color;
import android.opengl.GLES20;

public class ColorFragmentShader
  extends FragmentShader
{
  private static final String SHADER_SCRIPT = "precision mediump float;uniform vec4 vColor;uniform int bHasOpacityMask;uniform sampler2D sOpacityMask;varying vec2 vSharedOpacityMaskTexCoord;void main(){  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor = vec4 (vColor.xyz, vColor.w * a);  }  else  {    gl_FragColor = vColor;  }}";
  public static final String VAR_COLOR = "vColor";
  private int m_Color;
  private final float[] m_ColorVector = new float[4];
  
  public ColorFragmentShader()
  {
    this(0);
  }
  
  public ColorFragmentShader(int paramInt)
  {
    super("precision mediump float;uniform vec4 vColor;uniform int bHasOpacityMask;uniform sampler2D sOpacityMask;varying vec2 vSharedOpacityMaskTexCoord;void main(){  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor = vec4 (vColor.xyz, vColor.w * a);  }  else  {    gl_FragColor = vColor;  }}");
    setColor(paramInt);
  }
  
  public final int getColor()
  {
    return this.m_Color;
  }
  
  public boolean hasAlphaBlending()
  {
    return this.m_ColorVector[3] < 1.0F;
  }
  
  public boolean isTextureCoordinateNeeded()
  {
    return false;
  }
  
  protected void onPrepare(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase)
  {
    super.onPrepare(paramDrawingContext, paramProgram, paramModelBase);
    paramDrawingContext = this.m_ColorVector;
    float f2 = Color.alpha(this.m_Color) / 255.0F;
    if (paramModelBase != null) {}
    for (float f1 = paramModelBase.getOpacity();; f1 = 1.0F)
    {
      paramDrawingContext[3] = (f1 * f2);
      setColor(paramProgram, this.m_ColorVector);
      paramDrawingContext = paramModelBase.getOpacityMask();
      if (paramDrawingContext != null) {
        break;
      }
      disableOpacityMaskTexture(paramProgram);
      return;
    }
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(3553, paramDrawingContext.getObjectId());
    enableOpacityMaskTexture(paramProgram);
    setOpacityMaskTextureUnit(paramProgram, 0);
  }
  
  public ColorFragmentShader setColor(int paramInt)
  {
    throwIfNotAccessible();
    this.m_Color = paramInt;
    this.m_ColorVector[0] = (Color.red(paramInt) / 255.0F);
    this.m_ColorVector[1] = (Color.green(paramInt) / 255.0F);
    this.m_ColorVector[2] = (Color.blue(paramInt) / 255.0F);
    return this;
  }
  
  protected boolean setColor(Program paramProgram, float[] paramArrayOfFloat)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "vColor");
    if (i >= 0)
    {
      GLES20.glUniform4fv(i, 1, paramArrayOfFloat, 0);
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/ColorFragmentShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */