package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

public class ResizeFilter
  extends Filter
{
  @GenerateFieldPort(hasDefault=true, name="generateMipMap")
  private boolean mGenerateMipMap = false;
  private int mInputChannels;
  @GenerateFieldPort(hasDefault=true, name="keepAspectRatio")
  private boolean mKeepAspectRatio = false;
  private FrameFormat mLastFormat = null;
  @GenerateFieldPort(name="oheight")
  private int mOHeight;
  @GenerateFieldPort(name="owidth")
  private int mOWidth;
  private MutableFrameFormat mOutputFormat;
  private Program mProgram;
  
  public ResizeFilter(String paramString)
  {
    super(paramString);
  }
  
  protected void createProgram(FilterContext paramFilterContext, FrameFormat paramFrameFormat)
  {
    if ((this.mLastFormat != null) && (this.mLastFormat.getTarget() == paramFrameFormat.getTarget())) {
      return;
    }
    this.mLastFormat = paramFrameFormat;
    switch (paramFrameFormat.getTarget())
    {
    default: 
      throw new RuntimeException("ResizeFilter could not create suitable program!");
    case 2: 
      throw new RuntimeException("Native ResizeFilter not implemented yet!");
    }
    this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    createProgram(paramFilterContext, localFrame.getFormat());
    Object localObject = localFrame.getFormat().mutableCopy();
    if (this.mKeepAspectRatio)
    {
      FrameFormat localFrameFormat = localFrame.getFormat();
      this.mOHeight = (this.mOWidth * localFrameFormat.getHeight() / localFrameFormat.getWidth());
    }
    ((MutableFrameFormat)localObject).setDimensions(this.mOWidth, this.mOHeight);
    localObject = paramFilterContext.getFrameManager().newFrame((FrameFormat)localObject);
    if (this.mGenerateMipMap)
    {
      paramFilterContext = (GLFrame)paramFilterContext.getFrameManager().newFrame(localFrame.getFormat());
      paramFilterContext.setTextureParameter(10241, 9985);
      paramFilterContext.setDataFromFrame(localFrame);
      paramFilterContext.generateMipMap();
      this.mProgram.process(paramFilterContext, (Frame)localObject);
      paramFilterContext.release();
    }
    for (;;)
    {
      pushOutput("image", (Frame)localObject);
      ((Frame)localObject).release();
      return;
      this.mProgram.process(localFrame, (Frame)localObject);
    }
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ResizeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */