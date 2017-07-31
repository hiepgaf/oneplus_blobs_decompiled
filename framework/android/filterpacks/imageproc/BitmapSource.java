package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;

public class BitmapSource
  extends Filter
{
  @GenerateFieldPort(name="bitmap")
  private Bitmap mBitmap;
  private Frame mImageFrame;
  @GenerateFieldPort(hasDefault=true, name="recycleBitmap")
  private boolean mRecycleBitmap = true;
  @GenerateFieldPort(hasDefault=true, name="repeatFrame")
  boolean mRepeatFrame = false;
  private int mTarget;
  @GenerateFieldPort(name="target")
  String mTargetString;
  
  public BitmapSource(String paramString)
  {
    super(paramString);
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (((paramString.equals("bitmap")) || (paramString.equals("target"))) && (this.mImageFrame != null))
    {
      this.mImageFrame.release();
      this.mImageFrame = null;
    }
  }
  
  public void loadImage(FilterContext paramFilterContext)
  {
    this.mTarget = FrameFormat.readTargetString(this.mTargetString);
    MutableFrameFormat localMutableFrameFormat = ImageFormat.create(this.mBitmap.getWidth(), this.mBitmap.getHeight(), 3, this.mTarget);
    this.mImageFrame = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
    this.mImageFrame.setBitmap(this.mBitmap);
    this.mImageFrame.setTimestamp(-1L);
    if (this.mRecycleBitmap) {
      this.mBitmap.recycle();
    }
    this.mBitmap = null;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mImageFrame == null) {
      loadImage(paramFilterContext);
    }
    pushOutput("image", this.mImageFrame);
    if (!this.mRepeatFrame) {
      closeOutputPort("image");
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("image", ImageFormat.create(3, 0));
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mImageFrame != null)
    {
      this.mImageFrame.release();
      this.mImageFrame = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/BitmapSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */