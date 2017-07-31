package android.os;

import java.lang.ref.WeakReference;

public class Registrant
{
  WeakReference refH;
  Object userObj;
  int what;
  
  public Registrant(Handler paramHandler, int paramInt, Object paramObject)
  {
    this.refH = new WeakReference(paramHandler);
    this.what = paramInt;
    this.userObj = paramObject;
  }
  
  public void clear()
  {
    this.refH = null;
    this.userObj = null;
  }
  
  public Handler getHandler()
  {
    if (this.refH == null) {
      return null;
    }
    return (Handler)this.refH.get();
  }
  
  void internalNotifyRegistrant(Object paramObject, Throwable paramThrowable)
  {
    Handler localHandler = getHandler();
    if (localHandler == null)
    {
      clear();
      return;
    }
    Message localMessage = Message.obtain();
    localMessage.what = this.what;
    localMessage.obj = new AsyncResult(this.userObj, paramObject, paramThrowable);
    localHandler.sendMessage(localMessage);
  }
  
  public Message messageForRegistrant()
  {
    Object localObject = getHandler();
    if (localObject == null)
    {
      clear();
      return null;
    }
    localObject = ((Handler)localObject).obtainMessage();
    ((Message)localObject).what = this.what;
    ((Message)localObject).obj = this.userObj;
    return (Message)localObject;
  }
  
  public void notifyException(Throwable paramThrowable)
  {
    internalNotifyRegistrant(null, paramThrowable);
  }
  
  public void notifyRegistrant()
  {
    internalNotifyRegistrant(null, null);
  }
  
  public void notifyRegistrant(AsyncResult paramAsyncResult)
  {
    internalNotifyRegistrant(paramAsyncResult.result, paramAsyncResult.exception);
  }
  
  public void notifyResult(Object paramObject)
  {
    internalNotifyRegistrant(paramObject, null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Registrant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */