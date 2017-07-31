package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.format.ObjectFormat;

public class ObjectSource
  extends Filter
{
  private Frame mFrame;
  @GenerateFieldPort(name="object")
  private Object mObject;
  @GenerateFinalPort(hasDefault=true, name="format")
  private FrameFormat mOutputFormat = FrameFormat.unspecified();
  @GenerateFieldPort(hasDefault=true, name="repeatFrame")
  boolean mRepeatFrame = false;
  
  public ObjectSource(String paramString)
  {
    super(paramString);
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if ((paramString.equals("object")) && (this.mFrame != null))
    {
      this.mFrame.release();
      this.mFrame = null;
    }
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mFrame == null)
    {
      if (this.mObject == null) {
        throw new NullPointerException("ObjectSource producing frame with no object set!");
      }
      MutableFrameFormat localMutableFrameFormat = ObjectFormat.fromObject(this.mObject, 1);
      this.mFrame = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
      this.mFrame.setObjectValue(this.mObject);
      this.mFrame.setTimestamp(-1L);
    }
    pushOutput("frame", this.mFrame);
    if (!this.mRepeatFrame) {
      closeOutputPort("frame");
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("frame", this.mOutputFormat);
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    this.mFrame.release();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/ObjectSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */