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

public class ImageSlicer
  extends Filter
{
  private int mInputHeight;
  private int mInputWidth;
  private Frame mOriginalFrame;
  private int mOutputHeight;
  private int mOutputWidth;
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
  
  public ImageSlicer(String paramString)
  {
    super(paramString);
  }
  
  private void calcOutputFormatForInput(Frame paramFrame)
  {
    this.mInputWidth = paramFrame.getFormat().getWidth();
    this.mInputHeight = paramFrame.getFormat().getHeight();
    this.mSliceWidth = ((this.mInputWidth + this.mXSlices - 1) / this.mXSlices);
    this.mSliceHeight = ((this.mInputHeight + this.mYSlices - 1) / this.mYSlices);
    this.mOutputWidth = (this.mSliceWidth + this.mPadSize * 2);
    this.mOutputHeight = (this.mSliceHeight + this.mPadSize * 2);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mSliceIndex == 0)
    {
      this.mOriginalFrame = pullInput("image");
      calcOutputFormatForInput(this.mOriginalFrame);
    }
    Object localObject = this.mOriginalFrame.getFormat().mutableCopy();
    ((MutableFrameFormat)localObject).setDimensions(this.mOutputWidth, this.mOutputHeight);
    localObject = paramFilterContext.getFrameManager().newFrame((FrameFormat)localObject);
    if (this.mProgram == null) {
      this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
    }
    int i = this.mSliceIndex;
    int j = this.mXSlices;
    int k = this.mSliceIndex / this.mXSlices;
    float f1 = (this.mSliceWidth * (i % j) - this.mPadSize) / this.mInputWidth;
    float f2 = (this.mSliceHeight * k - this.mPadSize) / this.mInputHeight;
    ((ShaderProgram)this.mProgram).setSourceRect(f1, f2, this.mOutputWidth / this.mInputWidth, this.mOutputHeight / this.mInputHeight);
    this.mProgram.process(this.mOriginalFrame, (Frame)localObject);
    this.mSliceIndex += 1;
    if (this.mSliceIndex == this.mXSlices * this.mYSlices)
    {
      this.mSliceIndex = 0;
      this.mOriginalFrame.release();
      setWaitsOnInputPort("image", true);
    }
    for (;;)
    {
      pushOutput("image", (Frame)localObject);
      ((Frame)localObject).release();
      return;
      this.mOriginalFrame.retain();
      setWaitsOnInputPort("image", false);
    }
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ImageSlicer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */