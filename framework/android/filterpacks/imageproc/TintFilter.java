package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Color;

public class TintFilter
  extends Filter
{
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  @GenerateFieldPort(hasDefault=true, name="tint")
  private int mTint = -16776961;
  private final String mTintShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 tint;\nuniform vec3 color_ratio;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float avg_color = dot(color_ratio, color.rgb);\n  vec3 new_color = min(0.8 * avg_color + 0.2 * tint, 1.0);\n  gl_FragColor = vec4(new_color.rgb, color.a);\n}\n";
  
  public TintFilter(String paramString)
  {
    super(paramString);
  }
  
  private void initParameters()
  {
    this.mProgram.setHostValue("color_ratio", new float[] { 0.21F, 0.71F, 0.07F });
    updateParameters();
  }
  
  private void updateParameters()
  {
    float f1 = Color.red(this.mTint) / 255.0F;
    float f2 = Color.green(this.mTint) / 255.0F;
    float f3 = Color.blue(this.mTint) / 255.0F;
    this.mProgram.setHostValue("tint", new float[] { f1, f2, f3 });
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mProgram != null) {
      updateParameters();
    }
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void initProgram(FilterContext paramFilterContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new RuntimeException("Filter Sharpen does not support frames of target " + paramInt + "!");
    }
    paramFilterContext = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 tint;\nuniform vec3 color_ratio;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float avg_color = dot(color_ratio, color.rgb);\n  vec3 new_color = min(0.8 * avg_color + 0.2 * tint, 1.0);\n  gl_FragColor = vec4(new_color.rgb, color.a);\n}\n");
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    FrameFormat localFrameFormat = localFrame.getFormat();
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget))
    {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
      initParameters();
    }
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(localFrameFormat);
    this.mProgram.process(localFrame, paramFilterContext);
    pushOutput("image", paramFilterContext);
    paramFilterContext.release();
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/TintFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */