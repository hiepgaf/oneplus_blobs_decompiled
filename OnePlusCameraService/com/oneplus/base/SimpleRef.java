package com.oneplus.base;

public final class SimpleRef<T>
  implements Ref<T>
{
  private volatile T m_Value;
  
  public SimpleRef() {}
  
  public SimpleRef(T paramT)
  {
    this.m_Value = paramT;
  }
  
  public T get()
  {
    return (T)this.m_Value;
  }
  
  public void set(T paramT)
  {
    this.m_Value = paramT;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/SimpleRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */