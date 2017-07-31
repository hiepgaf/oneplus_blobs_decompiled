package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.format.ObjectFormat;
import java.util.Locale;

public class ToUpperCase
  extends Filter
{
  private FrameFormat mOutputFormat;
  
  public ToUpperCase(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    String str = (String)pullInput("mixedcase").getObjectValue();
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
    paramFilterContext.setObjectValue(str.toUpperCase(Locale.getDefault()));
    pushOutput("uppercase", paramFilterContext);
  }
  
  public void setupPorts()
  {
    this.mOutputFormat = ObjectFormat.fromClass(String.class, 1);
    addMaskedInputPort("mixedcase", this.mOutputFormat);
    addOutputPort("uppercase", this.mOutputFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/text/ToUpperCase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */