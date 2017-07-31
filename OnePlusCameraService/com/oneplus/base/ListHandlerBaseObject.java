package com.oneplus.base;

import android.os.Handler;
import android.os.Message;

public abstract class ListHandlerBaseObject<T>
  extends ListBaseObject<T>
  implements HandlerObject
{
  private final InternalHandler m_Handler = new InternalHandler(this);
  
  public Handler getHandler()
  {
    return this.m_Handler;
  }
  
  protected void handleMessage(Message paramMessage) {}
  
  public void release()
  {
    super.release();
    this.m_Handler.release();
  }
  
  private static final class InternalHandler
    extends Handler
  {
    private volatile ListHandlerBaseObject<?> m_Owner;
    
    public InternalHandler(ListHandlerBaseObject<?> paramListHandlerBaseObject)
    {
      this.m_Owner = paramListHandlerBaseObject;
    }
    
    public void handleMessage(Message paramMessage)
    {
      ListHandlerBaseObject localListHandlerBaseObject = this.m_Owner;
      if (localListHandlerBaseObject != null)
      {
        localListHandlerBaseObject.handleMessage(paramMessage);
        return;
      }
      Log.e("ListHandlerBaseObject", "Owner released, drop message " + paramMessage.what);
    }
    
    public void release()
    {
      this.m_Owner = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ListHandlerBaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */