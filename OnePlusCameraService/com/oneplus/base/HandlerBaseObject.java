package com.oneplus.base;

import android.os.Handler;
import android.os.Message;

public abstract class HandlerBaseObject
  extends BasicBaseObject
  implements HandlerObject
{
  private final InternalHandler m_Handler;
  
  protected HandlerBaseObject(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (InternalHandler localInternalHandler = new InternalHandler(this);; localInternalHandler = null)
    {
      this.m_Handler = localInternalHandler;
      return;
    }
  }
  
  protected HandlerBaseObject(boolean paramBoolean, String paramString)
  {
    super(paramString);
    if (paramBoolean) {}
    for (paramString = new InternalHandler(this);; paramString = null)
    {
      this.m_Handler = paramString;
      return;
    }
  }
  
  public final Handler getHandler()
  {
    return this.m_Handler;
  }
  
  protected void handleMessage(Message paramMessage) {}
  
  protected void onRelease()
  {
    if (this.m_Handler != null) {
      this.m_Handler.release();
    }
    super.onRelease();
  }
  
  private static final class InternalHandler
    extends Handler
  {
    private volatile HandlerBaseObject m_Owner;
    private final String m_Tag;
    
    public InternalHandler(HandlerBaseObject paramHandlerBaseObject)
    {
      this.m_Owner = paramHandlerBaseObject;
      this.m_Tag = paramHandlerBaseObject.TAG;
    }
    
    public void handleMessage(Message paramMessage)
    {
      HandlerBaseObject localHandlerBaseObject = this.m_Owner;
      if (localHandlerBaseObject != null)
      {
        localHandlerBaseObject.handleMessage(paramMessage);
        return;
      }
      Log.e(this.m_Tag, "Owner released, drop message " + paramMessage.what);
    }
    
    public void release()
    {
      this.m_Owner = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/HandlerBaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */