package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;

public class StringSource
  extends Filter
{
  private FrameFormat mOutputFormat;
  @GenerateFieldPort(name="stringValue")
  private String mString;
  
  public StringSource(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
    paramFilterContext.setObjectValue(this.mString);
    paramFilterContext.setTimestamp(-1L);
    pushOutput("string", paramFilterContext);
    closeOutputPort("string");
  }
  
  public void setupPorts()
  {
    this.mOutputFormat = ObjectFormat.fromClass(String.class, 1);
    addOutputPort("string", this.mOutputFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/text/StringSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */