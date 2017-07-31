package com.oneplus.base;

public abstract interface EventSource
  extends ThreadDependentObject
{
  public abstract <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler);
  
  public abstract <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/EventSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */