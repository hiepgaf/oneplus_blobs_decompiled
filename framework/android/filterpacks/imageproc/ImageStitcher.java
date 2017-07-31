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

public class ImageStitcher
  extends Filter
{
  private int mImageHeight;
  private int mImageWidth;
  private int mInputHeight;
  private int mInputWidth;
  private Frame mOutputFrame;
  @GenerateFieldPort(name="padSize")
  private int mPadSize;
  private Program mProgram;
  private int mSliceHeight;
  private int mSliceIndex = 0;
  private int mSliceWidth;
  @GenerateFieldPort(name="xSlices")
  private int mXSlices;
  @GenerateFieldPort(name="ySlices")
  private int mYSlices;
  
  public ImageStitcher(String paramString)
  {
    super(paramString);
  }
  
  private FrameFormat calcOutputFormatForInput(FrameFormat paramFrameFormat)
  {
    MutableFrameFormat localMutableFrameFormat = paramFrameFormat.mutableCopy();
    this.mInputWidth = paramFrameFormat.getWidth();
    this.mInputHeight = paramFrameFormat.getHeight();
    this.mSliceWidth = (this.mInputWidth - this.mPadSize * 2);
    this.mSliceHeight = (this.mInputHeight - this.mPadSize * 2);
    this.mImageWidth = (this.mSliceWidth * this.mXSlices);
    this.mImageHeight = (this.mSliceHeight * this.mYSlices);
    localMutableFrameFormat.setDimensions(this.mImageWidth, this.mImageHeight);
    return localMutableFrameFormat;
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    FrameFormat localFrameFormat = localFrame.getFormat();
    if (this.mSliceIndex == 0) {
      this.mOutputFrame = paramFilterContext.getFrameManager().newFrame(calcOutputFormatForInput(localFrameFormat));
    }
    while ((localFrameFormat.getWidth() == this.mInputWidth) && (localFrameFormat.getHeight() == this.mInputHeight))
    {
      if (this.mProgram == null) {
        this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
      }
      float f1 = this.mPadSize / this.mInputWidth;
      float f2 = this.mPadSize / this.mInputHeight;
      int i = this.mSliceIndex % this.mXSlices * this.mSliceWidth;
      int j = this.mSliceIndex / this.mXSlices * this.mSliceHeight;
      float f3 = Math.min(this.mSliceWidth, this.mImageWidth - i);
      float f4 = Math.min(this.mSliceHeight, this.mImageHeight - j);
      ((ShaderProgram)this.mProgram).setSourceRect(f1, f2, f3 / this.mInputWidth, f4 / this.mInputHeight);
      ((ShaderProgram)this.mProgram).setTargetRect(i / this.mImageWidth, j / this.mImageHeight, f3 / this.mImageWidth, f4 / this.mImageHeight);
      this.mProgram.process(localFrame, this.mOutputFrame);
      this.mSliceIndex += 1;
      if (this.mSliceIndex == this.mXSlices * this.mYSlices)
      {
        pushOutput("image", this.mOutputFrame);
        this.mOutputFrame.release();
        this.mSliceIndex = 0;
      }
      return;
    }
    throw new RuntimeException("Image size should not change.");
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ImageStitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */