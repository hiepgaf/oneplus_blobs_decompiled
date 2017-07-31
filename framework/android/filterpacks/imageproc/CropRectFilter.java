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

public class CropRectFilter
  extends Filter
{
  private int mHeight = 0;
  @GenerateFieldPort(name="height")
  private int mOutputHeight;
  @GenerateFieldPort(name="width")
  private int mOutputWidth;
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private int mWidth = 0;
  @GenerateFieldPort(name="xorigin")
  private int mXorigin;
  @GenerateFieldPort(name="yorigin")
  private int mYorigin;
  
  public CropRectFilter(String paramString)
  {
    super(paramString);
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mProgram != null) {
      updateSourceRect(this.mWidth, this.mHeight);
    }
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
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    FrameFormat localFrameFormat = localFrame.getFormat();
    Object localObject = ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3);
    localObject = paramFilterContext.getFrameManager().newFrame((FrameFormat)localObject);
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
    }
    if ((localFrameFormat.getWidth() != this.mWidth) || (localFrameFormat.getHeight() != this.mHeight)) {
      updateSourceRect(localFrameFormat.getWidth(), localFrameFormat.getHeight());
    }
    this.mProgram.process(localFrame, (Frame)localObject);
    pushOutput("image", (Frame)localObject);
    ((Frame)localObject).release();
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
  
  void updateSourceRect(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    ((ShaderProgram)this.mProgram).setSourceRect(this.mXorigin / this.mWidth, this.mYorigin / this.mHeight, this.mOutputWidth / this.mWidth, this.mOutputHeight / this.mHeight);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/CropRectFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */