package android.filterfw.core;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncRunner
  extends GraphRunner
{
  private static final String TAG = "AsyncRunner";
  private boolean isProcessing;
  private GraphRunner.OnRunnerDoneListener mDoneListener;
  private Exception mException;
  private boolean mLogVerbose;
  private AsyncRunnerTask mRunTask;
  private SyncRunner mRunner;
  private Class mSchedulerClass;
  
  public AsyncRunner(FilterContext paramFilterContext)
  {
    super(paramFilterContext);
    this.mSchedulerClass = SimpleScheduler.class;
    this.mLogVerbose = Log.isLoggable("AsyncRunner", 2);
  }
  
  public AsyncRunner(FilterContext paramFilterContext, Class paramClass)
  {
    super(paramFilterContext);
    this.mSchedulerClass = paramClass;
    this.mLogVerbose = Log.isLoggable("AsyncRunner", 2);
  }
  
  private void setException(Exception paramException)
  {
    try
    {
      this.mException = paramException;
      return;
    }
    finally
    {
      paramException = finally;
      throw paramException;
    }
  }
  
  private void setRunning(boolean paramBoolean)
  {
    try
    {
      this.isProcessing = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void close()
  {
    try
    {
      if (isRunning()) {
        throw new RuntimeException("Cannot close graph while it is running!");
      }
    }
    finally {}
    if (this.mLogVerbose) {
      Log.v("AsyncRunner", "Closing filters.");
    }
    this.mRunner.close();
  }
  
  public Exception getError()
  {
    try
    {
      Exception localException = this.mException;
      return localException;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public FilterGraph getGraph()
  {
    FilterGraph localFilterGraph = null;
    if (this.mRunner != null) {
      localFilterGraph = this.mRunner.getGraph();
    }
    return localFilterGraph;
  }
  
  public boolean isRunning()
  {
    try
    {
      boolean bool = this.isProcessing;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void run()
  {
    try
    {
      if (this.mLogVerbose) {
        Log.v("AsyncRunner", "Running graph.");
      }
      setException(null);
      if (isRunning()) {
        throw new RuntimeException("Graph is already running!");
      }
    }
    finally {}
    if (this.mRunner == null) {
      throw new RuntimeException("Cannot run before a graph is set!");
    }
    getClass();
    this.mRunTask = new AsyncRunnerTask(null);
    setRunning(true);
    this.mRunTask.execute(new SyncRunner[] { this.mRunner });
  }
  
  public void setDoneCallback(GraphRunner.OnRunnerDoneListener paramOnRunnerDoneListener)
  {
    this.mDoneListener = paramOnRunnerDoneListener;
  }
  
  public void setGraph(FilterGraph paramFilterGraph)
  {
    try
    {
      if (isRunning()) {
        throw new RuntimeException("Graph is already running!");
      }
    }
    finally {}
    this.mRunner = new SyncRunner(this.mFilterContext, paramFilterGraph, this.mSchedulerClass);
  }
  
  /* Error */
  public void stop()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 118	android/filterfw/core/AsyncRunner:mRunTask	Landroid/filterfw/core/AsyncRunner$AsyncRunnerTask;
    //   6: ifnull +15 -> 21
    //   9: aload_0
    //   10: getfield 118	android/filterfw/core/AsyncRunner:mRunTask	Landroid/filterfw/core/AsyncRunner$AsyncRunnerTask;
    //   13: invokevirtual 139	android/os/AsyncTask:isCancelled	()Z
    //   16: istore_1
    //   17: iload_1
    //   18: ifeq +6 -> 24
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    //   24: aload_0
    //   25: getfield 36	android/filterfw/core/AsyncRunner:mLogVerbose	Z
    //   28: ifeq +11 -> 39
    //   31: ldc 14
    //   33: ldc -115
    //   35: invokestatic 90	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   38: pop
    //   39: aload_0
    //   40: getfield 118	android/filterfw/core/AsyncRunner:mRunTask	Landroid/filterfw/core/AsyncRunner$AsyncRunnerTask;
    //   43: iconst_0
    //   44: invokevirtual 145	android/os/AsyncTask:cancel	(Z)Z
    //   47: pop
    //   48: goto -27 -> 21
    //   51: astore_2
    //   52: aload_0
    //   53: monitorexit
    //   54: aload_2
    //   55: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	AsyncRunner
    //   16	2	1	bool	boolean
    //   51	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	17	51	finally
    //   24	39	51	finally
    //   39	48	51	finally
  }
  
  private class AsyncRunnerTask
    extends AsyncTask<SyncRunner, Void, AsyncRunner.RunnerResult>
  {
    private static final String TAG = "AsyncRunnerTask";
    
    private AsyncRunnerTask() {}
    
    protected AsyncRunner.RunnerResult doInBackground(SyncRunner... paramVarArgs)
    {
      AsyncRunner.RunnerResult localRunnerResult = new AsyncRunner.RunnerResult(AsyncRunner.this, null);
      try
      {
        if (paramVarArgs.length > 1) {
          throw new RuntimeException("More than one runner received!");
        }
      }
      catch (Exception paramVarArgs)
      {
        localRunnerResult.exception = paramVarArgs;
        localRunnerResult.status = 6;
      }
      try
      {
        for (;;)
        {
          AsyncRunner.this.deactivateGlContext();
          if (AsyncRunner.-get1(AsyncRunner.this)) {
            Log.v("AsyncRunnerTask", "Done with background graph processing.");
          }
          return localRunnerResult;
          paramVarArgs[0].assertReadyToStep();
          if (AsyncRunner.-get1(AsyncRunner.this)) {
            Log.v("AsyncRunnerTask", "Starting background graph processing.");
          }
          AsyncRunner.this.activateGlContext();
          if (AsyncRunner.-get1(AsyncRunner.this)) {
            Log.v("AsyncRunnerTask", "Preparing filter graph for processing.");
          }
          paramVarArgs[0].beginProcessing();
          if (AsyncRunner.-get1(AsyncRunner.this)) {
            Log.v("AsyncRunnerTask", "Running graph.");
          }
          localRunnerResult.status = 1;
          while ((!isCancelled()) && (localRunnerResult.status == 1)) {
            if (!paramVarArgs[0].performStep())
            {
              localRunnerResult.status = paramVarArgs[0].determinePostRunState();
              if (localRunnerResult.status == 3)
              {
                paramVarArgs[0].waitUntilWake();
                localRunnerResult.status = 1;
              }
            }
          }
          if (isCancelled()) {
            localRunnerResult.status = 5;
          }
        }
      }
      catch (Exception paramVarArgs)
      {
        for (;;)
        {
          localRunnerResult.exception = paramVarArgs;
          localRunnerResult.status = 6;
        }
      }
    }
    
    protected void onCancelled(AsyncRunner.RunnerResult paramRunnerResult)
    {
      onPostExecute(paramRunnerResult);
    }
    
    protected void onPostExecute(AsyncRunner.RunnerResult paramRunnerResult)
    {
      if (AsyncRunner.-get1(AsyncRunner.this)) {
        Log.v("AsyncRunnerTask", "Starting post-execute.");
      }
      AsyncRunner.-wrap1(AsyncRunner.this, false);
      AsyncRunner.RunnerResult localRunnerResult = paramRunnerResult;
      if (paramRunnerResult == null)
      {
        localRunnerResult = new AsyncRunner.RunnerResult(AsyncRunner.this, null);
        localRunnerResult.status = 5;
      }
      AsyncRunner.-wrap0(AsyncRunner.this, localRunnerResult.exception);
      if ((localRunnerResult.status == 5) || (localRunnerResult.status == 6)) {
        if (AsyncRunner.-get1(AsyncRunner.this)) {
          Log.v("AsyncRunnerTask", "Closing filters.");
        }
      }
      try
      {
        AsyncRunner.-get2(AsyncRunner.this).close();
        if (AsyncRunner.-get0(AsyncRunner.this) != null)
        {
          if (AsyncRunner.-get1(AsyncRunner.this)) {
            Log.v("AsyncRunnerTask", "Calling graph done callback.");
          }
          AsyncRunner.-get0(AsyncRunner.this).onRunnerDone(localRunnerResult.status);
        }
        if (AsyncRunner.-get1(AsyncRunner.this)) {
          Log.v("AsyncRunnerTask", "Completed post-execute.");
        }
        return;
      }
      catch (Exception paramRunnerResult)
      {
        for (;;)
        {
          localRunnerResult.status = 6;
          AsyncRunner.-wrap0(AsyncRunner.this, paramRunnerResult);
        }
      }
    }
  }
  
  private class RunnerResult
  {
    public Exception exception;
    public int status = 0;
    
    private RunnerResult() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/AsyncRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */