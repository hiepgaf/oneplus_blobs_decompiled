package com.oneplus.base;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;

public abstract class BaseAppComponentBuilder
  implements ComponentBuilder
{
  private final Class<?> m_ComponentType;
  private final ComponentCreationPriority m_Priority;
  
  protected BaseAppComponentBuilder(ComponentCreationPriority paramComponentCreationPriority, Class<?> paramClass)
  {
    if (paramComponentCreationPriority == null) {
      throw new IllegalArgumentException("No creation priority.");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("No component type.");
    }
    this.m_Priority = paramComponentCreationPriority;
    this.m_ComponentType = paramClass;
  }
  
  protected BaseAppComponentBuilder(Class<?> paramClass)
  {
    this(ComponentCreationPriority.NORMAL, paramClass);
  }
  
  protected abstract Component create(BaseApplication paramBaseApplication);
  
  public Component create(Object... paramVarArgs)
  {
    if ((paramVarArgs.length == 1) && ((paramVarArgs[0] instanceof BaseApplication))) {
      return create((BaseApplication)paramVarArgs[0]);
    }
    return null;
  }
  
  public ComponentCreationPriority getPriority()
  {
    return this.m_Priority;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    if (paramClass != null) {
      return paramClass.isAssignableFrom(this.m_ComponentType);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BaseAppComponentBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */