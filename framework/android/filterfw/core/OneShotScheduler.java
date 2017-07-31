package android.filterfw.core;

import android.util.Log;
import java.util.HashMap;

public class OneShotScheduler
  extends RoundRobinScheduler
{
  private static final String TAG = "OneShotScheduler";
  private final boolean mLogVerbose = Log.isLoggable("OneShotScheduler", 2);
  private HashMap<String, Integer> scheduled = new HashMap();
  
  public OneShotScheduler(FilterGraph paramFilterGraph)
  {
    super(paramFilterGraph);
  }
  
  public void reset()
  {
    super.reset();
    this.scheduled.clear();
  }
  
  public Filter scheduleNextNode()
  {
    Object localObject = null;
    for (;;)
    {
      Filter localFilter = super.scheduleNextNode();
      if (localFilter == null)
      {
        if (this.mLogVerbose) {
          Log.v("OneShotScheduler", "No filters available to run.");
        }
        return null;
      }
      if (!this.scheduled.containsKey(localFilter.getName()))
      {
        if (localFilter.getNumberOfConnectedInputs() == 0) {
          this.scheduled.put(localFilter.getName(), Integer.valueOf(1));
        }
        if (this.mLogVerbose) {
          Log.v("OneShotScheduler", "Scheduling filter \"" + localFilter.getName() + "\" of type " + localFilter.getFilterClassName());
        }
        return localFilter;
      }
      if (localObject == localFilter)
      {
        if (this.mLogVerbose) {
          Log.v("OneShotScheduler", "One pass through graph completed.");
        }
        return null;
      }
      if (localObject == null) {
        localObject = localFilter;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/OneShotScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */