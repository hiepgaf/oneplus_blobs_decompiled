package com.oneplus.gl;

import android.opengl.GLES20;

public class TextureFragmentShader
  extends FragmentShader
{
  private static final String SHADER_SCRIPT_2D = "precision mediump float;\nuniform sampler2D aaaTexture;\nuniform int bHasOpacityMask;\nuniform sampler2D sOpacityMask;\nuniform mat4 uTransform;\nuniform float fOpacity;\nvarying vec2 vSharedTexCoord;\nvarying vec2 vSharedOpacityMaskTexCoord;\nvoid main(){  vec4 coord4 = uTransform * vec4 (vSharedTexCoord.xy, 0, 1);  gl_FragColor = texture2D(aaaTexture, coord4.xy);  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor.w = gl_FragColor.w * a * fOpacity;  }  else  {    gl_FragColor.w = gl_FragColor.w * fOpacity;  }}";
  private static final String SHADER_SCRIPT_EXTERNAL_OES = "#extension GL_OES_EGL_image_external : require \nprecision highp float;uniform samplerExternalOES aaaTexture;uniform int bHasOpacityMask;uniform sampler2D sOpacityMask;uniform mat4 uTransform;uniform float fOpacity;\nvarying vec2 vSharedTexCoord;varying vec2 vSharedOpacityMaskTexCoord;\nvoid main(){  vec4 coord4 = uTransform * vec4 (vSharedTexCoord.xy, 0, 1);  gl_FragColor = texture2D(aaaTexture, vec2 (coord4.x, 1.0 - coord4.y));  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor.w = gl_FragColor.w * a * fOpacity;  }  else  {    gl_FragColor.w = gl_FragColor.w * fOpacity;  }}";
  public static final String VAR_OPACITY = "fOpacity";
  public static final String VAR_TEXTURE_COORD_TRANSFORM_MATRIX = "uTransform";
  public static final String VAR_TEXTURE_UNIT = "aaaTexture";
  private final boolean m_OwnsTexture;
  private Texture m_Texture;
  private final float[] m_TransformMatrix = new float[16];
  
  public TextureFragmentShader(Texture paramTexture)
  {
    this(paramTexture, false);
  }
  
  public TextureFragmentShader(Texture paramTexture, boolean paramBoolean)
  {
    super(selectShaderSource(paramTexture.getType()));
    this.m_Texture = paramTexture;
    this.m_OwnsTexture = paramBoolean;
  }
  
  private static String selectShaderSource(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new RuntimeException("Unsupported texture type : " + paramInt);
    case 3553: 
      return "precision mediump float;\nuniform sampler2D aaaTexture;\nuniform int bHasOpacityMask;\nuniform sampler2D sOpacityMask;\nuniform mat4 uTransform;\nuniform float fOpacity;\nvarying vec2 vSharedTexCoord;\nvarying vec2 vSharedOpacityMaskTexCoord;\nvoid main(){  vec4 coord4 = uTransform * vec4 (vSharedTexCoord.xy, 0, 1);  gl_FragColor = texture2D(aaaTexture, coord4.xy);  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor.w = gl_FragColor.w * a * fOpacity;  }  else  {    gl_FragColor.w = gl_FragColor.w * fOpacity;  }}";
    }
    return "#extension GL_OES_EGL_image_external : require \nprecision highp float;uniform samplerExternalOES aaaTexture;uniform int bHasOpacityMask;uniform sampler2D sOpacityMask;uniform mat4 uTransform;uniform float fOpacity;\nvarying vec2 vSharedTexCoord;varying vec2 vSharedOpacityMaskTexCoord;\nvoid main(){  vec4 coord4 = uTransform * vec4 (vSharedTexCoord.xy, 0, 1);  gl_FragColor = texture2D(aaaTexture, vec2 (coord4.x, 1.0 - coord4.y));  if(bHasOpacityMask != 0)  {    float a = texture2D(sOpacityMask, vSharedOpacityMaskTexCoord).w;    gl_FragColor.w = gl_FragColor.w * a * fOpacity;  }  else  {    gl_FragColor.w = gl_FragColor.w * fOpacity;  }}";
  }
  
  public Texture getTexture()
  {
    return this.m_Texture;
  }
  
  public boolean hasAlphaBlending()
  {
    if ((this.m_Texture instanceof Texture2D)) {
      return ((Texture2D)this.m_Texture).getFormat() != Texture2D.Format.RGB_565;
    }
    return true;
  }
  
  protected void onPrepare(DrawingContext paramDrawingContext, Program paramProgram, ModelBase paramModelBase)
  {
    super.onPrepare(paramDrawingContext, paramProgram, paramModelBase);
    int i = this.m_Texture.getType();
    GLES20.glActiveTexture(33985);
    GLES20.glBindTexture(i, this.m_Texture.getObjectId());
    setTextureUnit(paramProgram, 1);
    if ((this.m_Texture instanceof Texture2D))
    {
      setTexCoordTransformMatrix(paramProgram, IDENTITY_MATRIX);
      paramDrawingContext = paramModelBase.getOpacityMask();
      if (paramDrawingContext != null) {
        break label121;
      }
      disableOpacityMaskTexture(paramProgram);
    }
    for (;;)
    {
      setOpacity(paramProgram, paramModelBase.getOpacity());
      return;
      if (!(this.m_Texture instanceof ExternalOESTexture)) {
        break;
      }
      ((ExternalOESTexture)this.m_Texture).getTransformMatrix(this.m_TransformMatrix);
      setTexCoordTransformMatrix(paramProgram, this.m_TransformMatrix);
      break;
      label121:
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, paramDrawingContext.getObjectId());
      enableOpacityMaskTexture(paramProgram);
      setOpacityMaskTextureUnit(paramProgram, 0);
    }
  }
  
  protected void onRelease()
  {
    if (this.m_Texture != null)
    {
      if (this.m_OwnsTexture) {
        EglObject.release(this.m_Texture);
      }
      this.m_Texture = null;
    }
    super.onRelease();
  }
  
  protected boolean setOpacity(Program paramProgram, float paramFloat)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "fOpacity");
    if (i >= 0)
    {
      GLES20.glUniform1f(i, paramFloat);
      return true;
    }
    return false;
  }
  
  protected boolean setTexCoordTransformMatrix(Program paramProgram, float[] paramArrayOfFloat)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "uTransform");
    if (i >= 0)
    {
      GLES20.glUniformMatrix4fv(i, 1, false, paramArrayOfFloat, 0);
      return true;
    }
    return false;
  }
  
  protected boolean setTextureUnit(Program paramProgram, int paramInt)
  {
    int i = GLES20.glGetUniformLocation(paramProgram.getObjectId(), "aaaTexture");
    if (i >= 0)
    {
      GLES20.glUniform1i(i, paramInt);
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/TextureFragmentShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */