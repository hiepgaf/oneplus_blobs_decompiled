package android.filterfw.core;

import java.util.Iterator;

public class SimpleScheduler
  extends Scheduler
{
  public SimpleScheduler(FilterGraph paramFilterGraph)
  {
    super(paramFilterGraph);
  }
  
  public void reset() {}
  
  public Filter scheduleNextNode()
  {
    Iterator localIterator = getGraph().getFilters().iterator();
    while (localIterator.hasNext())
    {
      Filter localFilter = (Filter)localIterator.next();
      if (localFilter.canProcess()) {
        return localFilter;
      }
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/SimpleScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */