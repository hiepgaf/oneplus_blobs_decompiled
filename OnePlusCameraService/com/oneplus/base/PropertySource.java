package com.oneplus.base;

public abstract interface PropertySource
  extends ThreadDependentObject
{
  public abstract <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback);
  
  public abstract <TValue> TValue get(PropertyKey<TValue> paramPropertyKey);
  
  public abstract <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback);
  
  public abstract <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PropertySource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */