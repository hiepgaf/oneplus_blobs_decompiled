package android.filterpacks.numeric;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;

public class SinWaveFilter
  extends Filter
{
  private FrameFormat mOutputFormat;
  @GenerateFieldPort(hasDefault=true, name="stepSize")
  private float mStepSize = 0.05F;
  private float mValue = 0.0F;
  
  public SinWaveFilter(String paramString)
  {
    super(paramString);
  }
  
  public void open(FilterContext paramFilterContext)
  {
    this.mValue = 0.0F;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
    paramFilterContext.setObjectValue(Float.valueOf(((float)Math.sin(this.mValue) + 1.0F) / 2.0F));
    pushOutput("value", paramFilterContext);
    this.mValue += this.mStepSize;
    paramFilterContext.release();
  }
  
  public void setupPorts()
  {
    this.mOutputFormat = ObjectFormat.fromClass(Float.class, 1);
    addOutputPort("value", this.mOutputFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/numeric/SinWaveFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */