package com.oneplus.base.component;

public abstract interface ComponentBuilder
{
  public abstract Component create(Object... paramVarArgs);
  
  public abstract ComponentCreationPriority getPriority();
  
  public abstract boolean isComponentTypeSupported(Class<?> paramClass);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */