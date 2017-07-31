package com.oneplus.base;

import android.os.Handler;
import android.os.Looper;

public class OperationCallbackHelper<TCanceled, TCompleted, TStarted>
{
  private OperationCallback<TCanceled, TCompleted, TStarted> m_Callback;
  private Handler m_Handler;
  
  public OperationCallbackHelper(OperationCallback<TCanceled, TCompleted, TStarted> paramOperationCallback)
  {
    this(paramOperationCallback, null);
  }
  
  public OperationCallbackHelper(OperationCallback<TCanceled, TCompleted, TStarted> paramOperationCallback, Handler paramHandler)
  {
    this.m_Callback = paramOperationCallback;
    if (this.m_Callback == null) {
      throw new IllegalArgumentException("Operation callback is null");
    }
    this.m_Handler = paramHandler;
  }
  
  private boolean isDependencyThread()
  {
    if (this.m_Handler == null) {
      return true;
    }
    return this.m_Handler.getLooper().getThread() == Thread.currentThread();
  }
  
  public void callOnCanceled(final TCanceled paramTCanceled)
  {
    if (isDependencyThread())
    {
      this.m_Callback.onCanceled(paramTCanceled);
      return;
    }
    this.m_Handler.post(new Runnable()
    {
      public void run()
      {
        OperationCallbackHelper.-get0(OperationCallbackHelper.this).onCanceled(paramTCanceled);
      }
    });
  }
  
  public void callOnCompleted(final TCompleted paramTCompleted)
  {
    if (isDependencyThread())
    {
      this.m_Callback.onCompleted(paramTCompleted);
      return;
    }
    this.m_Handler.post(new Runnable()
    {
      public void run()
      {
        OperationCallbackHelper.-get0(OperationCallbackHelper.this).onCompleted(paramTCompleted);
      }
    });
  }
  
  public void callOnStarted(final TStarted paramTStarted)
  {
    if (isDependencyThread())
    {
      this.m_Callback.onStarted(paramTStarted);
      return;
    }
    this.m_Handler.post(new Runnable()
    {
      public void run()
      {
        OperationCallbackHelper.-get0(OperationCallbackHelper.this).onStarted(paramTStarted);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/OperationCallbackHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */