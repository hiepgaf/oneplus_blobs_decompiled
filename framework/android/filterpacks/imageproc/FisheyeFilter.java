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

public class FisheyeFilter
  extends Filter
{
  private static final String TAG = "FisheyeFilter";
  private static final String mFisheyeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec2 scale;\nuniform float alpha;\nuniform float radius2;\nuniform float factor;\nvarying vec2 v_texcoord;\nvoid main() {\n  const float m_pi_2 = 1.570963;\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float radian = m_pi_2 - atan(alpha * sqrt(radius2 - dist * dist), dist);\n  float scalar = radian * factor / dist;\n  vec2 new_coord = coord * scalar + vec2(0.5, 0.5);\n  gl_FragColor = texture2D(tex_sampler_0, new_coord);\n}\n";
  private int mHeight = 0;
  private Program mProgram;
  @GenerateFieldPort(hasDefault=true, name="scale")
  private float mScale = 0.0F;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private int mWidth = 0;
  
  public FisheyeFilter(String paramString)
  {
    super(paramString);
  }
  
  private void updateFrameSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    updateProgramParams();
  }
  
  private void updateProgramParams()
  {
    float[] arrayOfFloat = new float[2];
    if (this.mWidth > this.mHeight)
    {
      arrayOfFloat[0] = 1.0F;
      arrayOfFloat[1] = (this.mHeight / this.mWidth);
    }
    for (;;)
    {
      float f1 = this.mScale * 2.0F + 0.75F;
      float f2 = 0.25F * (arrayOfFloat[0] * arrayOfFloat[0] + arrayOfFloat[1] * arrayOfFloat[1]);
      float f3 = (float)Math.sqrt(f2);
      float f4 = 1.15F * f3;
      f4 *= f4;
      f2 = f3 / (1.5707964F - (float)Math.atan(f1 / f3 * (float)Math.sqrt(f4 - f2)));
      this.mProgram.setHostValue("scale", arrayOfFloat);
      this.mProgram.setHostValue("radius2", Float.valueOf(f4));
      this.mProgram.setHostValue("factor", Float.valueOf(f2));
      this.mProgram.setHostValue("alpha", Float.valueOf(f1));
      return;
      arrayOfFloat[0] = (this.mWidth / this.mHeight);
      arrayOfFloat[1] = 1.0F;
    }
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mProgram != null) {
      updateProgramParams();
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
      throw new RuntimeException("Filter FisheyeFilter does not support frames of target " + paramInt + "!");
    }
    paramFilterContext = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec2 scale;\nuniform float alpha;\nuniform float radius2;\nuniform float factor;\nvarying vec2 v_texcoord;\nvoid main() {\n  const float m_pi_2 = 1.570963;\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float radian = m_pi_2 - atan(alpha * sqrt(radius2 - dist * dist), dist);\n  float scalar = radian * factor / dist;\n  vec2 new_coord = coord * scalar + vec2(0.5, 0.5);\n  gl_FragColor = texture2D(tex_sampler_0, new_coord);\n}\n");
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame1 = pullInput("image");
    FrameFormat localFrameFormat = localFrame1.getFormat();
    Frame localFrame2 = paramFilterContext.getFrameManager().newFrame(localFrameFormat);
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
    }
    if ((localFrameFormat.getWidth() != this.mWidth) || (localFrameFormat.getHeight() != this.mHeight)) {
      updateFrameSize(localFrameFormat.getWidth(), localFrameFormat.getHeight());
    }
    this.mProgram.process(localFrame1, localFrame2);
    pushOutput("image", localFrame2);
    localFrame2.release();
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/FisheyeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */