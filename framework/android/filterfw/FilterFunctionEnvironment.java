package android.filterfw;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterFunction;
import android.filterfw.core.FrameManager;

public class FilterFunctionEnvironment
  extends MffEnvironment
{
  public FilterFunctionEnvironment()
  {
    super(null);
  }
  
  public FilterFunctionEnvironment(FrameManager paramFrameManager)
  {
    super(paramFrameManager);
  }
  
  public FilterFunction createFunction(Class paramClass, Object... paramVarArgs)
  {
    String str = "FilterFunction(" + paramClass.getSimpleName() + ")";
    paramClass = FilterFactory.sharedFactory().createFilterByClass(paramClass, str);
    paramClass.initWithAssignmentList(paramVarArgs);
    return new FilterFunction(getContext(), paramClass);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/FilterFunctionEnvironment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */