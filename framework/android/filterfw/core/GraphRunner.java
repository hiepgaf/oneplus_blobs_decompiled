package android.filterfw.core;

public abstract class GraphRunner
{
  public static final int RESULT_BLOCKED = 4;
  public static final int RESULT_ERROR = 6;
  public static final int RESULT_FINISHED = 2;
  public static final int RESULT_RUNNING = 1;
  public static final int RESULT_SLEEPING = 3;
  public static final int RESULT_STOPPED = 5;
  public static final int RESULT_UNKNOWN = 0;
  protected FilterContext mFilterContext = null;
  
  public GraphRunner(FilterContext paramFilterContext)
  {
    this.mFilterContext = paramFilterContext;
  }
  
  protected boolean activateGlContext()
  {
    GLEnvironment localGLEnvironment = this.mFilterContext.getGLEnvironment();
    if ((localGLEnvironment == null) || (localGLEnvironment.isActive())) {
      return false;
    }
    localGLEnvironment.activate();
    return true;
  }
  
  public abstract void close();
  
  protected void deactivateGlContext()
  {
    GLEnvironment localGLEnvironment = this.mFilterContext.getGLEnvironment();
    if (localGLEnvironment != null) {
      localGLEnvironment.deactivate();
    }
  }
  
  public FilterContext getContext()
  {
    return this.mFilterContext;
  }
  
  public abstract Exception getError();
  
  public abstract FilterGraph getGraph();
  
  public abstract boolean isRunning();
  
  public abstract void run();
  
  public abstract void setDoneCallback(OnRunnerDoneListener paramOnRunnerDoneListener);
  
  public abstract void stop();
  
  public static abstract interface OnRunnerDoneListener
  {
    public abstract void onRunnerDone(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/GraphRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */