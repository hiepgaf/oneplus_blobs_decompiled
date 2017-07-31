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

public class FlipFilter
  extends Filter
{
  @GenerateFieldPort(hasDefault=true, name="horizontal")
  private boolean mHorizontal = false;
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  @GenerateFieldPort(hasDefault=true, name="vertical")
  private boolean mVertical = false;
  
  public FlipFilter(String paramString)
  {
    super(paramString);
  }
  
  private void updateParameters()
  {
    float f1;
    float f2;
    label18:
    float f3;
    if (this.mHorizontal)
    {
      f1 = 1.0F;
      if (!this.mVertical) {
        break label60;
      }
      f2 = 1.0F;
      if (!this.mHorizontal) {
        break label65;
      }
      f3 = -1.0F;
      label28:
      if (!this.mVertical) {
        break label70;
      }
    }
    label60:
    label65:
    label70:
    for (float f4 = -1.0F;; f4 = 1.0F)
    {
      ((ShaderProgram)this.mProgram).setSourceRect(f1, f2, f3, f4);
      return;
      f1 = 0.0F;
      break;
      f2 = 0.0F;
      break label18;
      f3 = 1.0F;
      break label28;
    }
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
    paramFilterContext = ShaderProgram.createIdentity(paramFilterContext);
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
    updateParameters();
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    FrameFormat localFrameFormat = localFrame.getFormat();
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/FlipFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */