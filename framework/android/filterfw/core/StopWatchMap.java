package android.filterfw.core;

import java.util.HashMap;

public class StopWatchMap
{
  public boolean LOG_MFF_RUNNING_TIMES = false;
  private HashMap<String, StopWatch> mStopWatches = null;
  
  public void start(String paramString)
  {
    if (!this.LOG_MFF_RUNNING_TIMES) {
      return;
    }
    if (!this.mStopWatches.containsKey(paramString)) {
      this.mStopWatches.put(paramString, new StopWatch(paramString));
    }
    ((StopWatch)this.mStopWatches.get(paramString)).start();
  }
  
  public void stop(String paramString)
  {
    if (!this.LOG_MFF_RUNNING_TIMES) {
      return;
    }
    if (!this.mStopWatches.containsKey(paramString)) {
      throw new RuntimeException("Calling stop with unknown stopWatchName: " + paramString);
    }
    ((StopWatch)this.mStopWatches.get(paramString)).stop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/StopWatchMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */