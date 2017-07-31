package android.filterfw.core;

public abstract class Scheduler
{
  private FilterGraph mGraph;
  
  Scheduler(FilterGraph paramFilterGraph)
  {
    this.mGraph = paramFilterGraph;
  }
  
  boolean finished()
  {
    return true;
  }
  
  FilterGraph getGraph()
  {
    return this.mGraph;
  }
  
  abstract void reset();
  
  abstract Filter scheduleNextNode();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/Scheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */