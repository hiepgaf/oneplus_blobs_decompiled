package com.oneplus.base;

import java.util.AbstractList;

public abstract class ListBaseObject<T>
  extends AbstractList<T>
  implements BaseObject
{
  protected final String TAG = getClass().getSimpleName();
  private final BaseObjectAdapter m_BaseObjectAdapter = new BaseObjectAdapter(this, this.TAG);
  private volatile boolean m_IsReleased;
  
  public <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.addCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    this.m_BaseObjectAdapter.addHandler(paramEventKey, paramEventHandler);
  }
  
  protected final void disableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    this.m_BaseObjectAdapter.disableEventLogs(paramEventKey, paramInt);
  }
  
  protected final void disablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    this.m_BaseObjectAdapter.disablePropertyLogs(paramPropertyKey, paramInt);
  }
  
  protected final void enableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    this.m_BaseObjectAdapter.enableEventLogs(paramEventKey, paramInt);
  }
  
  protected final void enablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    this.m_BaseObjectAdapter.enablePropertyLogs(paramPropertyKey, paramInt);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_RELEASED) {
      return Boolean.valueOf(this.m_IsReleased);
    }
    return (TValue)this.m_BaseObjectAdapter.get(paramPropertyKey);
  }
  
  public boolean isDependencyThread()
  {
    return this.m_BaseObjectAdapter.isDependencyThread();
  }
  
  protected <TValue> boolean notifyPropertyChanged(PropertyKey<TValue> paramPropertyKey, TValue paramTValue1, TValue paramTValue2)
  {
    return this.m_BaseObjectAdapter.notifyPropertyChanged(paramPropertyKey, paramTValue1, paramTValue2);
  }
  
  protected <TArgs extends EventArgs> void raise(EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    this.m_BaseObjectAdapter.raise(paramEventKey, paramTArgs);
  }
  
  public void release()
  {
    verifyAccess();
    this.m_IsReleased = true;
  }
  
  public <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.removeCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    this.m_BaseObjectAdapter.removeHandler(paramEventKey, paramEventHandler);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.set(paramPropertyKey, paramTValue);
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  public final void verifyAccess()
  {
    if (!this.m_BaseObjectAdapter.isDependencyThread()) {
      throw new RuntimeException("Cross-thread access.");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ListBaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */