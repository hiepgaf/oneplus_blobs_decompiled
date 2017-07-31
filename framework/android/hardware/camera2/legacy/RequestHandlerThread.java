package android.hardware.camera2.legacy;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

public class RequestHandlerThread
  extends HandlerThread
{
  public static final int MSG_POKE_IDLE_HANDLER = -1;
  private Handler.Callback mCallback;
  private volatile Handler mHandler;
  private final ConditionVariable mIdle = new ConditionVariable(true);
  private final MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler()
  {
    public boolean queueIdle()
    {
      RequestHandlerThread.-get0(RequestHandlerThread.this).open();
      return false;
    }
  };
  private final ConditionVariable mStarted = new ConditionVariable(false);
  
  public RequestHandlerThread(String paramString, Handler.Callback paramCallback)
  {
    super(paramString, 10);
    this.mCallback = paramCallback;
  }
  
  public Handler getHandler()
  {
    return this.mHandler;
  }
  
  public boolean hasAnyMessages(int[] paramArrayOfInt)
  {
    synchronized (this.mHandler.getLooper().getQueue())
    {
      int j = paramArrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        int k = paramArrayOfInt[i];
        boolean bool = this.mHandler.hasMessages(k);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
  }
  
  protected void onLooperPrepared()
  {
    this.mHandler = new Handler(getLooper(), this.mCallback);
    this.mStarted.open();
  }
  
  public void removeMessages(int[] paramArrayOfInt)
  {
    MessageQueue localMessageQueue = this.mHandler.getLooper().getQueue();
    int i = 0;
    try
    {
      int j = paramArrayOfInt.length;
      while (i < j)
      {
        int k = paramArrayOfInt[i];
        this.mHandler.removeMessages(k);
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  public Handler waitAndGetHandler()
  {
    waitUntilStarted();
    return getHandler();
  }
  
  public void waitUntilIdle()
  {
    Handler localHandler = waitAndGetHandler();
    MessageQueue localMessageQueue = localHandler.getLooper().getQueue();
    if (localMessageQueue.isIdle()) {
      return;
    }
    this.mIdle.close();
    localMessageQueue.addIdleHandler(this.mIdleHandler);
    localHandler.sendEmptyMessage(-1);
    if (localMessageQueue.isIdle()) {
      return;
    }
    this.mIdle.block();
  }
  
  public void waitUntilStarted()
  {
    this.mStarted.block();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/RequestHandlerThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */