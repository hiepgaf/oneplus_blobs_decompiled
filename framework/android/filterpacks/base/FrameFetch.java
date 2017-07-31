package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;

public class FrameFetch
  extends Filter
{
  @GenerateFinalPort(hasDefault=true, name="format")
  private FrameFormat mFormat;
  @GenerateFieldPort(name="key")
  private String mKey;
  @GenerateFieldPort(hasDefault=true, name="repeatFrame")
  private boolean mRepeatFrame = false;
  
  public FrameFetch(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = paramFilterContext.fetchFrame(this.mKey);
    if (paramFilterContext != null)
    {
      pushOutput("frame", paramFilterContext);
      if (!this.mRepeatFrame) {
        closeOutputPort("frame");
      }
      return;
    }
    delayNextProcess(250);
  }
  
  public void setupPorts()
  {
    if (this.mFormat == null) {}
    for (FrameFormat localFrameFormat = FrameFormat.unspecified();; localFrameFormat = this.mFormat)
    {
      addOutputPort("frame", localFrameFormat);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/FrameFetch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */