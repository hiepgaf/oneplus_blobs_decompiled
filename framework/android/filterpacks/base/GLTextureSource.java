package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.format.ImageFormat;

public class GLTextureSource
  extends Filter
{
  private Frame mFrame;
  @GenerateFieldPort(name="height")
  private int mHeight;
  @GenerateFieldPort(hasDefault=true, name="repeatFrame")
  private boolean mRepeatFrame = false;
  @GenerateFieldPort(name="texId")
  private int mTexId;
  @GenerateFieldPort(hasDefault=true, name="timestamp")
  private long mTimestamp = -1L;
  @GenerateFieldPort(name="width")
  private int mWidth;
  
  public GLTextureSource(String paramString)
  {
    super(paramString);
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mFrame != null)
    {
      this.mFrame.release();
      this.mFrame = null;
    }
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mFrame == null)
    {
      MutableFrameFormat localMutableFrameFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
      this.mFrame = paramFilterContext.getFrameManager().newBoundFrame(localMutableFrameFormat, 100, this.mTexId);
      this.mFrame.setTimestamp(this.mTimestamp);
    }
    pushOutput("frame", this.mFrame);
    if (!this.mRepeatFrame) {
      closeOutputPort("frame");
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("frame", ImageFormat.create(3, 3));
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mFrame != null) {
      this.mFrame.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/GLTextureSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */