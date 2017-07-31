package com.oneplus.base;

public final class BaseObjectAdapter
  extends BasicBaseObject
{
  private final EventSource m_OwnerEventSource;
  private final PropertySource m_OwnerPropertySource;
  public final Class<?> ownerType;
  
  public BaseObjectAdapter(Object paramObject, String paramString)
  {
    super(paramString);
    this.ownerType = paramObject.getClass();
    if ((paramObject instanceof EventSource))
    {
      paramString = (EventSource)paramObject;
      this.m_OwnerEventSource = paramString;
      if (!(paramObject instanceof PropertySource)) {
        break label53;
      }
    }
    label53:
    for (paramObject = (PropertySource)paramObject;; paramObject = null)
    {
      this.m_OwnerPropertySource = ((PropertySource)paramObject);
      return;
      paramString = null;
      break;
    }
  }
  
  protected <TArgs extends EventArgs> void callEventHandler(EventSource paramEventSource, EventHandler<TArgs> paramEventHandler, EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    super.callEventHandler(this.m_OwnerEventSource, paramEventHandler, paramEventKey, paramTArgs);
  }
  
  protected <TValue> void callPropertyChangedCallback(PropertySource paramPropertySource, PropertyChangedCallback<TValue> paramPropertyChangedCallback, PropertyKey<TValue> paramPropertyKey, PropertyChangeEventArgs<TValue> paramPropertyChangeEventArgs)
  {
    super.callPropertyChangedCallback(this.m_OwnerPropertySource, paramPropertyChangedCallback, paramPropertyKey, paramPropertyChangeEventArgs);
  }
  
  public boolean hasCallbacks(PropertyKey<?> paramPropertyKey)
  {
    return super.hasCallbacks(paramPropertyKey);
  }
  
  public boolean hasHandlers(EventKey<?> paramEventKey)
  {
    return super.hasHandlers(paramEventKey);
  }
  
  public <TValue> boolean notifyPropertyChanged(PropertyKey<TValue> paramPropertyKey, TValue paramTValue1, TValue paramTValue2)
  {
    return super.notifyPropertyChanged(paramPropertyKey, paramTValue1, paramTValue2);
  }
  
  public <TArgs extends EventArgs> void raise(EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    super.raise(paramEventKey, paramTArgs);
  }
  
  public <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return super.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  protected void verifyEvent(EventKey<?> paramEventKey)
  {
    if (!paramEventKey.ownerType.isAssignableFrom(this.ownerType)) {
      throw new IllegalArgumentException("Event " + paramEventKey + " is not owned by type " + this.ownerType + ".");
    }
  }
  
  protected void verifyProperty(PropertyKey<?> paramPropertyKey)
  {
    if (!paramPropertyKey.ownerType.isAssignableFrom(this.ownerType)) {
      throw new IllegalArgumentException("Property " + paramPropertyKey + " is not owned by type " + this.ownerType + ".");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BaseObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */