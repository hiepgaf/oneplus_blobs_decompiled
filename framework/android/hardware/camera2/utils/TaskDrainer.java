package android.hardware.camera2.utils;

import android.os.Handler;
import com.android.internal.util.Preconditions;
import java.util.HashSet;
import java.util.Set;

public class TaskDrainer<T>
{
  private static final String TAG = "TaskDrainer";
  private final boolean DEBUG = false;
  private boolean mDrainFinished = false;
  private boolean mDraining = false;
  private final Set<T> mEarlyFinishedTaskSet = new HashSet();
  private final Handler mHandler;
  private final DrainListener mListener;
  private final Object mLock = new Object();
  private final String mName;
  private final Set<T> mTaskSet = new HashSet();
  
  public TaskDrainer(Handler paramHandler, DrainListener paramDrainListener)
  {
    this.mHandler = ((Handler)Preconditions.checkNotNull(paramHandler, "handler must not be null"));
    this.mListener = ((DrainListener)Preconditions.checkNotNull(paramDrainListener, "listener must not be null"));
    this.mName = null;
  }
  
  public TaskDrainer(Handler paramHandler, DrainListener paramDrainListener, String paramString)
  {
    this.mHandler = ((Handler)Preconditions.checkNotNull(paramHandler, "handler must not be null"));
    this.mListener = ((DrainListener)Preconditions.checkNotNull(paramDrainListener, "listener must not be null"));
    this.mName = paramString;
  }
  
  private void checkIfDrainFinished()
  {
    if ((!this.mTaskSet.isEmpty()) || (!this.mDraining) || (this.mDrainFinished)) {
      return;
    }
    this.mDrainFinished = true;
    postDrained();
  }
  
  private void postDrained()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        TaskDrainer.-get0(TaskDrainer.this).onDrained();
      }
    });
  }
  
  public void beginDrain()
  {
    synchronized (this.mLock)
    {
      if (!this.mDraining)
      {
        this.mDraining = true;
        checkIfDrainFinished();
      }
      return;
    }
  }
  
  public void taskFinished(T paramT)
  {
    synchronized (this.mLock)
    {
      if ((!this.mTaskSet.remove(paramT)) && (!this.mEarlyFinishedTaskSet.add(paramT))) {
        throw new IllegalStateException("Task " + paramT + " was already finished");
      }
    }
    checkIfDrainFinished();
  }
  
  public void taskStarted(T paramT)
  {
    synchronized (this.mLock)
    {
      if (this.mDraining) {
        throw new IllegalStateException("Can't start more tasks after draining has begun");
      }
    }
    if ((!this.mEarlyFinishedTaskSet.remove(paramT)) && (!this.mTaskSet.add(paramT))) {
      throw new IllegalStateException("Task " + paramT + " was already started");
    }
  }
  
  public static abstract interface DrainListener
  {
    public abstract void onDrained();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/TaskDrainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */