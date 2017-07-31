package com.oneplus.camera;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;

public abstract class CameraThreadComponentBuilder
  implements ComponentBuilder
{
  private final Class<? extends CameraThreadComponent> m_ComponentType;
  private final ComponentCreationPriority m_Priority;
  
  protected CameraThreadComponentBuilder(ComponentCreationPriority paramComponentCreationPriority, Class<? extends CameraThreadComponent> paramClass)
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
  
  protected CameraThreadComponentBuilder(Class<? extends CameraThreadComponent> paramClass)
  {
    this(ComponentCreationPriority.NORMAL, paramClass);
  }
  
  public Component create(Object... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return null;
    }
    if (!(paramVarArgs[0] instanceof CameraThread)) {
      return null;
    }
    return create((CameraThread)paramVarArgs[0]);
  }
  
  protected abstract CameraThreadComponent create(CameraThread paramCameraThread);
  
  public final ComponentCreationPriority getPriority()
  {
    return this.m_Priority;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    return paramClass.isAssignableFrom(this.m_ComponentType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraThreadComponentBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */