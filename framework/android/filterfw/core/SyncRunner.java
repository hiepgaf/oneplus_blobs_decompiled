package android.filterfw.core;

import android.os.ConditionVariable;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SyncRunner
  extends GraphRunner
{
  private static final String TAG = "SyncRunner";
  private GraphRunner.OnRunnerDoneListener mDoneListener = null;
  private final boolean mLogVerbose = Log.isLoggable("SyncRunner", 2);
  private Scheduler mScheduler = null;
  private StopWatchMap mTimer = null;
  private ConditionVariable mWakeCondition = new ConditionVariable();
  private ScheduledThreadPoolExecutor mWakeExecutor = new ScheduledThreadPoolExecutor(1);
  
  public SyncRunner(FilterContext paramFilterContext, FilterGraph paramFilterGraph, Class paramClass)
  {
    super(paramFilterContext);
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Initializing SyncRunner");
    }
    if (Scheduler.class.isAssignableFrom(paramClass)) {
      try
      {
        this.mScheduler = ((Scheduler)paramClass.getConstructor(new Class[] { FilterGraph.class }).newInstance(new Object[] { paramFilterGraph }));
        this.mFilterContext = paramFilterContext;
        this.mFilterContext.addGraph(paramFilterGraph);
        this.mTimer = new StopWatchMap();
        if (this.mLogVerbose) {
          Log.v("SyncRunner", "Setting up filters");
        }
        paramFilterGraph.setupFilters();
        return;
      }
      catch (Exception paramFilterContext)
      {
        throw new RuntimeException("Could not instantiate Scheduler", paramFilterContext);
      }
      catch (InvocationTargetException paramFilterContext)
      {
        throw new RuntimeException("Scheduler constructor threw an exception", paramFilterContext);
      }
      catch (IllegalAccessException paramFilterContext)
      {
        throw new RuntimeException("Cannot access Scheduler constructor!", paramFilterContext);
      }
      catch (InstantiationException paramFilterContext)
      {
        throw new RuntimeException("Could not instantiate the Scheduler instance!", paramFilterContext);
      }
      catch (NoSuchMethodException paramFilterContext)
      {
        throw new RuntimeException("Scheduler does not have constructor <init>(FilterGraph)!", paramFilterContext);
      }
    }
    throw new IllegalArgumentException("Class provided is not a Scheduler subclass!");
  }
  
  void assertReadyToStep()
  {
    if (this.mScheduler == null) {
      throw new RuntimeException("Attempting to run schedule with no scheduler in place!");
    }
    if (getGraph() == null) {
      throw new RuntimeException("Calling step on scheduler with no graph in place!");
    }
  }
  
  public void beginProcessing()
  {
    this.mScheduler.reset();
    getGraph().beginProcessing();
  }
  
  public void close()
  {
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Closing graph.");
    }
    getGraph().closeFilters(this.mFilterContext);
    this.mScheduler.reset();
  }
  
  protected int determinePostRunState()
  {
    Iterator localIterator = this.mScheduler.getGraph().getFilters().iterator();
    while (localIterator.hasNext())
    {
      Filter localFilter = (Filter)localIterator.next();
      if (localFilter.isOpen())
      {
        if (localFilter.getStatus() == 4) {
          return 3;
        }
        return 4;
      }
    }
    return 2;
  }
  
  public Exception getError()
  {
    return null;
  }
  
  public FilterGraph getGraph()
  {
    FilterGraph localFilterGraph = null;
    if (this.mScheduler != null) {
      localFilterGraph = this.mScheduler.getGraph();
    }
    return localFilterGraph;
  }
  
  public boolean isRunning()
  {
    return false;
  }
  
  boolean performStep()
  {
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Performing one step.");
    }
    Filter localFilter = this.mScheduler.scheduleNextNode();
    if (localFilter != null)
    {
      this.mTimer.start(localFilter.getName());
      processFilterNode(localFilter);
      this.mTimer.stop(localFilter.getName());
      return true;
    }
    return false;
  }
  
  protected void processFilterNode(Filter paramFilter)
  {
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Processing filter node");
    }
    paramFilter.performProcess(this.mFilterContext);
    if (paramFilter.getStatus() == 6) {
      throw new RuntimeException("There was an error executing " + paramFilter + "!");
    }
    if (paramFilter.getStatus() == 4)
    {
      if (this.mLogVerbose) {
        Log.v("SyncRunner", "Scheduling filter wakeup");
      }
      scheduleFilterWake(paramFilter, paramFilter.getSleepDelay());
    }
  }
  
  public void run()
  {
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Beginning run.");
    }
    assertReadyToStep();
    beginProcessing();
    boolean bool2 = activateGlContext();
    for (boolean bool1 = true; bool1; bool1 = performStep()) {}
    if (bool2) {
      deactivateGlContext();
    }
    if (this.mDoneListener != null)
    {
      if (this.mLogVerbose) {
        Log.v("SyncRunner", "Calling completion listener.");
      }
      this.mDoneListener.onRunnerDone(determinePostRunState());
    }
    if (this.mLogVerbose) {
      Log.v("SyncRunner", "Run complete");
    }
  }
  
  protected void scheduleFilterWake(final Filter paramFilter, int paramInt)
  {
    this.mWakeCondition.close();
    final ConditionVariable localConditionVariable = this.mWakeCondition;
    this.mWakeExecutor.schedule(new Runnable()
    {
      public void run()
      {
        paramFilter.unsetStatus(4);
        localConditionVariable.open();
      }
    }, paramInt, TimeUnit.MILLISECONDS);
  }
  
  public void setDoneCallback(GraphRunner.OnRunnerDoneListener paramOnRunnerDoneListener)
  {
    this.mDoneListener = paramOnRunnerDoneListener;
  }
  
  public int step()
  {
    assertReadyToStep();
    if (!getGraph().isReady()) {
      throw new RuntimeException("Trying to process graph that is not open!");
    }
    if (performStep()) {
      return 1;
    }
    return determinePostRunState();
  }
  
  public void stop()
  {
    throw new RuntimeException("SyncRunner does not support stopping a graph!");
  }
  
  protected void waitUntilWake()
  {
    this.mWakeCondition.block();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/SyncRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */