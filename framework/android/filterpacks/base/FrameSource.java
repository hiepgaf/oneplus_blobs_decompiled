package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;

public class FrameSource
  extends Filter
{
  @GenerateFinalPort(name="format")
  private FrameFormat mFormat;
  @GenerateFieldPort(hasDefault=true, name="frame")
  private Frame mFrame = null;
  @GenerateFieldPort(hasDefault=true, name="repeatFrame")
  private boolean mRepeatFrame = false;
  
  public FrameSource(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mFrame != null) {
      pushOutput("frame", this.mFrame);
    }
    if (!this.mRepeatFrame) {
      closeOutputPort("frame");
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("frame", this.mFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/FrameSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */