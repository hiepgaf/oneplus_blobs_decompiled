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

public class VignetteFilter
  extends Filter
{
  private int mHeight = 0;
  private Program mProgram;
  @GenerateFieldPort(hasDefault=true, name="scale")
  private float mScale = 0.0F;
  private final float mShade = 0.85F;
  private final float mSlope = 20.0F;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private final String mVignetteShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float range;\nuniform float inv_max_dist;\nuniform float shade;\nuniform vec2 scale;\nvarying vec2 v_texcoord;\nvoid main() {\n  const float slope = 20.0;\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = vec4(color.rgb * lumen, color.a);\n}\n";
  private int mWidth = 0;
  
  public VignetteFilter(String paramString)
  {
    super(paramString);
  }
  
  private void initParameters()
  {
    float[] arrayOfFloat;
    if (this.mProgram != null)
    {
      arrayOfFloat = new float[2];
      if (this.mWidth <= this.mHeight) {
        break label108;
      }
      arrayOfFloat[0] = 1.0F;
      arrayOfFloat[1] = (this.mHeight / this.mWidth);
    }
    for (;;)
    {
      float f = (float)Math.sqrt(arrayOfFloat[0] * arrayOfFloat[0] + arrayOfFloat[1] * arrayOfFloat[1]);
      this.mProgram.setHostValue("scale", arrayOfFloat);
      this.mProgram.setHostValue("inv_max_dist", Float.valueOf(1.0F / (f * 0.5F)));
      this.mProgram.setHostValue("shade", Float.valueOf(0.85F));
      updateParameters();
      return;
      label108:
      arrayOfFloat[0] = (this.mWidth / this.mHeight);
      arrayOfFloat[1] = 1.0F;
    }
  }
  
  private void updateParameters()
  {
    this.mProgram.setHostValue("range", Float.valueOf(1.3F - (float)Math.sqrt(this.mScale) * 0.7F));
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
    paramFilterContext = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float range;\nuniform float inv_max_dist;\nuniform float shade;\nuniform vec2 scale;\nvarying vec2 v_texcoord;\nvoid main() {\n  const float slope = 20.0;\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = vec4(color.rgb * lumen, color.a);\n}\n");
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    FrameFormat localFrameFormat = localFrame.getFormat();
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
    }
    if ((localFrameFormat.getWidth() != this.mWidth) || (localFrameFormat.getHeight() != this.mHeight))
    {
      this.mWidth = localFrameFormat.getWidth();
      this.mHeight = localFrameFormat.getHeight();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/VignetteFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */