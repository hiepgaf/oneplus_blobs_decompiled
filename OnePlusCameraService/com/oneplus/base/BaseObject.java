package com.oneplus.base;

public abstract interface BaseObject
  extends ThreadDependentObject, PropertySource, EventSource
{
  public static final PropertyKey<Boolean> PROP_IS_RELEASED = new PropertyKey("IsReleased", Boolean.class, BaseObject.class, Boolean.valueOf(false));
  
  public abstract void release();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */