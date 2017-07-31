package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;

public class NullFilter
  extends Filter
{
  public NullFilter(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    pullInput("frame");
  }
  
  public void setupPorts()
  {
    addInputPort("frame");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/NullFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */