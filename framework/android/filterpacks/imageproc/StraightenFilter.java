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

public class StraightenFilter
  extends Filter
{
  private static final float DEGREE_TO_RADIAN = 0.017453292F;
  @GenerateFieldPort(hasDefault=true, name="angle")
  private float mAngle = 0.0F;
  private int mHeight = 0;
  @GenerateFieldPort(hasDefault=true, name="maxAngle")
  private float mMaxAngle = 45.0F;
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private int mWidth = 0;
  
  public StraightenFilter(String paramString)
  {
    super(paramString);
  }
  
  private void updateParameters()
  {
    float f2 = (float)Math.cos(this.mAngle * 0.017453292F);
    float f3 = (float)Math.sin(this.mAngle * 0.017453292F);
    if (this.mMaxAngle <= 0.0F) {
      throw new RuntimeException("Max angle is out of range (0-180).");
    }
    if (this.mMaxAngle > 90.0F) {}
    for (float f1 = 90.0F;; f1 = this.mMaxAngle)
    {
      this.mMaxAngle = f1;
      Object localObject = new Point(-f2 * this.mWidth + this.mHeight * f3, -f3 * this.mWidth - this.mHeight * f2);
      Point localPoint1 = new Point(this.mWidth * f2 + this.mHeight * f3, this.mWidth * f3 - this.mHeight * f2);
      Point localPoint2 = new Point(-f2 * this.mWidth - this.mHeight * f3, -f3 * this.mWidth + this.mHeight * f2);
      Point localPoint3 = new Point(this.mWidth * f2 - this.mHeight * f3, this.mWidth * f3 + this.mHeight * f2);
      f1 = Math.max(Math.abs(((Point)localObject).x), Math.abs(localPoint1.x));
      f2 = Math.max(Math.abs(((Point)localObject).y), Math.abs(localPoint1.y));
      f1 = 0.5F * Math.min(this.mWidth / f1, this.mHeight / f2);
      ((Point)localObject).set(((Point)localObject).x * f1 / this.mWidth + 0.5F, ((Point)localObject).y * f1 / this.mHeight + 0.5F);
      localPoint1.set(localPoint1.x * f1 / this.mWidth + 0.5F, localPoint1.y * f1 / this.mHeight + 0.5F);
      localPoint2.set(localPoint2.x * f1 / this.mWidth + 0.5F, localPoint2.y * f1 / this.mHeight + 0.5F);
      localPoint3.set(localPoint3.x * f1 / this.mWidth + 0.5F, localPoint3.y * f1 / this.mHeight + 0.5F);
      localObject = new Quad((Point)localObject, localPoint1, localPoint2, localPoint3);
      ((ShaderProgram)this.mProgram).setSourceRegion((Quad)localObject);
      return;
    }
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
      updateParameters();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/StraightenFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */