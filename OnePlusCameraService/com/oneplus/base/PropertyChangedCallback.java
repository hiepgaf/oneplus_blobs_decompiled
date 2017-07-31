package com.oneplus.base;

public abstract interface PropertyChangedCallback<TValue>
{
  public abstract void onPropertyChanged(PropertySource paramPropertySource, PropertyKey<TValue> paramPropertyKey, PropertyChangeEventArgs<TValue> paramPropertyChangeEventArgs);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PropertyChangedCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */