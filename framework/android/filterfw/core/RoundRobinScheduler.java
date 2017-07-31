package android.filterfw.core;

import java.util.Iterator;
import java.util.Set;

public class RoundRobinScheduler
  extends Scheduler
{
  private int mLastPos = -1;
  
  public RoundRobinScheduler(FilterGraph paramFilterGraph)
  {
    super(paramFilterGraph);
  }
  
  public void reset()
  {
    this.mLastPos = -1;
  }
  
  public Filter scheduleNextNode()
  {
    Object localObject2 = getGraph().getFilters();
    if (this.mLastPos >= ((Set)localObject2).size()) {
      this.mLastPos = -1;
    }
    int i = 0;
    Object localObject1 = null;
    int j = -1;
    Iterator localIterator = ((Iterable)localObject2).iterator();
    while (localIterator.hasNext())
    {
      Filter localFilter = (Filter)localIterator.next();
      localObject2 = localObject1;
      int k = j;
      if (localFilter.canProcess())
      {
        if (i > this.mLastPos) {
          break label118;
        }
        localObject2 = localObject1;
        k = j;
        if (localObject1 == null)
        {
          localObject2 = localFilter;
          k = i;
        }
      }
      i += 1;
      localObject1 = localObject2;
      j = k;
      continue;
      label118:
      this.mLastPos = i;
      return localFilter;
    }
    if (localObject1 != null)
    {
      this.mLastPos = j;
      return (Filter)localObject1;
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/RoundRobinScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */