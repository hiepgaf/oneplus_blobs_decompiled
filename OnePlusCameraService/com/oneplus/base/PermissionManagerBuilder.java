package com.oneplus.base;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.base.component.ComponentOwner;

public class PermissionManagerBuilder
  implements ComponentBuilder
{
  public Component create(Object... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length > 0) && ((paramVarArgs[0] instanceof BaseApplication))) {
      return new PermissionManagerImpl((ComponentOwner)paramVarArgs[0]);
    }
    return null;
  }
  
  public ComponentCreationPriority getPriority()
  {
    return ComponentCreationPriority.ON_DEMAND;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    return PermissionManager.class.isAssignableFrom(paramClass);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PermissionManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */