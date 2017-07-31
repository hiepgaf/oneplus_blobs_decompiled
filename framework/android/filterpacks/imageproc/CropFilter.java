package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.format.ObjectFormat;
import android.filterfw.geometry.Quad;

public class CropFilter
  extends Filter
{
  @GenerateFieldPort(name="fillblack")
  private boolean mFillBlack = false;
  private final String mFragShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  const vec2 lo = vec2(0.0, 0.0);\n  const vec2 hi = vec2(1.0, 1.0);\n  const vec4 black = vec4(0.0, 0.0, 0.0, 1.0);\n  bool out_of_bounds =\n    any(lessThan(v_texcoord, lo)) ||\n    any(greaterThan(v_texcoord, hi));\n  if (out_of_bounds) {\n    gl_FragColor = black;\n  } else {\n    gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n  }\n}\n";
  private FrameFormat mLastFormat = null;
  @GenerateFieldPort(name="oheight")
  private int mOutputHeight = -1;
  @GenerateFieldPort(name="owidth")
  private int mOutputWidth = -1;
  private Program mProgram;
  
  public CropFilter(String paramString)
  {
    super(paramString);
  }
  
  protected void createProgram(FilterContext paramFilterContext, FrameFormat paramFrameFormat)
  {
    if ((this.mLastFormat != null) && (this.mLastFormat.getTarget() == paramFrameFormat.getTarget())) {
      return;
    }
    this.mLastFormat = paramFrameFormat;
    this.mProgram = null;
    switch (paramFrameFormat.getTarget())
    {
    }
    while (this.mProgram == null)
    {
      throw new RuntimeException("Could not create a program for crop filter " + this + "!");
      if (this.mFillBlack) {
        this.mProgram = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  const vec2 lo = vec2(0.0, 0.0);\n  const vec2 hi = vec2(1.0, 1.0);\n  const vec4 black = vec4(0.0, 0.0, 0.0, 1.0);\n  bool out_of_bounds =\n    any(lessThan(v_texcoord, lo)) ||\n    any(greaterThan(v_texcoord, hi));\n  if (out_of_bounds) {\n    gl_FragColor = black;\n  } else {\n    gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n  }\n}\n");
      } else {
        this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
      }
    }
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    paramString = paramFrameFormat.mutableCopy();
    paramString.setDimensions(0, 0);
    return paramString;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    Object localObject = pullInput("box");
    createProgram(paramFilterContext, localFrame.getFormat());
    localObject = (Quad)((Frame)localObject).getObjectValue();
    MutableFrameFormat localMutableFrameFormat = localFrame.getFormat().mutableCopy();
    int i;
    if (this.mOutputWidth == -1)
    {
      i = localMutableFrameFormat.getWidth();
      if (this.mOutputHeight != -1) {
        break label144;
      }
    }
    label144:
    for (int j = localMutableFrameFormat.getHeight();; j = this.mOutputHeight)
    {
      localMutableFrameFormat.setDimensions(i, j);
      paramFilterContext = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
      if ((this.mProgram instanceof ShaderProgram)) {
        ((ShaderProgram)this.mProgram).setSourceRegion((Quad)localObject);
      }
      this.mProgram.process(localFrame, paramFilterContext);
      pushOutput("image", paramFilterContext);
      paramFilterContext.release();
      return;
      i = this.mOutputWidth;
      break;
    }
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addMaskedInputPort("box", ObjectFormat.fromClass(Quad.class, 1));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/CropFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */