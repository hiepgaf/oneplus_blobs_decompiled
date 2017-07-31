package android.hardware.camera2.utils;

import android.os.Handler;

public class TaskSingleDrainer
{
  private final Object mSingleTask = new Object();
  private final TaskDrainer<Object> mTaskDrainer;
  
  public TaskSingleDrainer(Handler paramHandler, TaskDrainer.DrainListener paramDrainListener)
  {
    this.mTaskDrainer = new TaskDrainer(paramHandler, paramDrainListener);
  }
  
  public TaskSingleDrainer(Handler paramHandler, TaskDrainer.DrainListener paramDrainListener, String paramString)
  {
    this.mTaskDrainer = new TaskDrainer(paramHandler, paramDrainListener, paramString);
  }
  
  public void beginDrain()
  {
    this.mTaskDrainer.beginDrain();
  }
  
  public void taskFinished()
  {
    this.mTaskDrainer.taskFinished(this.mSingleTask);
  }
  
  public void taskStarted()
  {
    this.mTaskDrainer.taskStarted(this.mSingleTask);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/TaskSingleDrainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */