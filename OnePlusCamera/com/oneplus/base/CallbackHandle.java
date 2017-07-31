package com.oneplus.base;

import android.os.Handler;

public abstract class CallbackHandle<TCallback>
  extends Handle
{
  private final TCallback m_Callback;
  private final Handler m_Handler;
  
  public CallbackHandle(String paramString, TCallback paramTCallback, Handler paramHandler)
  {
    super(paramString);
    this.m_Callback = paramTCallback;
    this.m_Handler = paramHandler;
  }
  
  public final TCallback getCallback()
  {
    return (TCallback)this.m_Callback;
  }
  
  public final Handler getHandler()
  {
    return this.m_Handler;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/CallbackHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */