package android.os;

public class AsyncResult
{
  public Throwable exception;
  public Object result;
  public Object userObj;
  
  public AsyncResult(Object paramObject1, Object paramObject2, Throwable paramThrowable)
  {
    this.userObj = paramObject1;
    this.result = paramObject2;
    this.exception = paramThrowable;
  }
  
  public static AsyncResult forMessage(Message paramMessage)
  {
    AsyncResult localAsyncResult = new AsyncResult(paramMessage.obj, null, null);
    paramMessage.obj = localAsyncResult;
    return localAsyncResult;
  }
  
  public static AsyncResult forMessage(Message paramMessage, Object paramObject, Throwable paramThrowable)
  {
    paramObject = new AsyncResult(paramMessage.obj, paramObject, paramThrowable);
    paramMessage.obj = paramObject;
    return (AsyncResult)paramObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/AsyncResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */