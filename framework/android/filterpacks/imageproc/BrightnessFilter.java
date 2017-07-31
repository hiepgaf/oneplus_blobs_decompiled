package android.filterpacks.imageproc;

import android.filterfw.core.FilterContext;
import android.filterfw.core.NativeProgram;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;

public class BrightnessFilter
  extends SimpleImageFilter
{
  private static final String mBrightnessShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float brightness;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = brightness * color;\n}\n";
  
  public BrightnessFilter(String paramString)
  {
    super(paramString, "brightness");
  }
  
  protected Program getNativeProgram(FilterContext paramFilterContext)
  {
    return new NativeProgram("filterpack_imageproc", "brightness");
  }
  
  protected Program getShaderProgram(FilterContext paramFilterContext)
  {
    return new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float brightness;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = brightness * color;\n}\n");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/BrightnessFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */