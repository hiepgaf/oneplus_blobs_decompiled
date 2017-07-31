package android.telecom;

import android.os.Handler;

class CallbackRecord<T>
{
  private final T mCallback;
  private final Handler mHandler;
  
  public CallbackRecord(T paramT, Handler paramHandler)
  {
    this.mCallback = paramT;
    this.mHandler = paramHandler;
  }
  
  public T getCallback()
  {
    return (T)this.mCallback;
  }
  
  public Handler getHandler()
  {
    return this.mHandler;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/CallbackRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */