package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFinalPort;

public class FrameBranch
  extends Filter
{
  @GenerateFinalPort(hasDefault=true, name="outputs")
  private int mNumberOfOutputs = 2;
  
  public FrameBranch(String paramString)
  {
    super(paramString);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = pullInput("in");
    int i = 0;
    while (i < this.mNumberOfOutputs)
    {
      pushOutput("out" + i, paramFilterContext);
      i += 1;
    }
  }
  
  public void setupPorts()
  {
    addInputPort("in");
    int i = 0;
    while (i < this.mNumberOfOutputs)
    {
      addOutputBasedOnInput("out" + i, "in");
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/FrameBranch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */