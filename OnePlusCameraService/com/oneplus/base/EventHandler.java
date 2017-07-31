package com.oneplus.base;

public abstract interface EventHandler<TArgs extends EventArgs>
{
  public abstract void onEventReceived(EventSource paramEventSource, EventKey<TArgs> paramEventKey, TArgs paramTArgs);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/EventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */