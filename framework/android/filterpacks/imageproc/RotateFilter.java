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
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;

public class RotateFilter
  extends Filter
{
  @GenerateFieldPort(name="angle")
  private int mAngle;
  private int mHeight = 0;
  private int mOutputHeight;
  private int mOutputWidth;
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private int mWidth = 0;
  
  public RotateFilter(String paramString)
  {
    super(paramString);
  }
  
  private void updateParameters()
  {
    if (this.mAngle % 90 == 0)
    {
      if (this.mAngle % 180 == 0)
      {
        f2 = 0.0F;
        if (this.mAngle % 360 == 0) {}
        for (f1 = 1.0F;; f1 = -1.0F)
        {
          Quad localQuad = new Quad(new Point((-f1 + f2 + 1.0F) * 0.5F, (-f2 - f1 + 1.0F) * 0.5F), new Point((f1 + f2 + 1.0F) * 0.5F, (f2 - f1 + 1.0F) * 0.5F), new Point((-f1 - f2 + 1.0F) * 0.5F, (-f2 + f1 + 1.0F) * 0.5F), new Point((f1 - f2 + 1.0F) * 0.5F, (f2 + f1 + 1.0F) * 0.5F));
          ((ShaderProgram)this.mProgram).setTargetRegion(localQuad);
          return;
        }
      }
      float f1 = 0.0F;
      if ((this.mAngle + 90) % 360 == 0) {}
      for (float f2 = -1.0F;; f2 = 1.0F)
      {
        this.mOutputWidth = this.mHeight;
        this.mOutputHeight = this.mWidth;
        break;
      }
    }
    throw new RuntimeException("degree has to be multiply of 90.");
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mProgram != null) {
      updateParameters();
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
    paramFilterContext.setClearsOutput(true);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    Object localObject = localFrame.getFormat();
    if ((this.mProgram == null) || (((FrameFormat)localObject).getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, ((FrameFormat)localObject).getTarget());
    }
    if ((((FrameFormat)localObject).getWidth() != this.mWidth) || (((FrameFormat)localObject).getHeight() != this.mHeight))
    {
      this.mWidth = ((FrameFormat)localObject).getWidth();
      this.mHeight = ((FrameFormat)localObject).getHeight();
      this.mOutputWidth = this.mWidth;
      this.mOutputHeight = this.mHeight;
      updateParameters();
    }
    localObject = ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3);
    paramFilterContext = paramFilterContext.getFrameManager().newFrame((FrameFormat)localObject);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/RotateFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */