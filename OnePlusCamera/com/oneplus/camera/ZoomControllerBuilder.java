package com.oneplus.camera;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;

public final class ZoomControllerBuilder
  implements ComponentBuilder
{
  public Component create(Object... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length > 0))
    {
      if ((paramVarArgs[0] instanceof CameraActivity)) {
        return new UIZoomControllerImpl((CameraActivity)paramVarArgs[0]);
      }
      if ((paramVarArgs[0] instanceof CameraThread)) {
        return new ZoomControlImpl((CameraThread)paramVarArgs[0]);
      }
    }
    return null;
  }
  
  public ComponentCreationPriority getPriority()
  {
    return ComponentCreationPriority.ON_DEMAND;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    return ZoomController.class.isAssignableFrom(paramClass);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ZoomControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */