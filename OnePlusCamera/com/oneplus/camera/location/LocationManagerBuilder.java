package com.oneplus.camera.location;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraThread;

public final class LocationManagerBuilder
  implements ComponentBuilder
{
  public Component create(Object... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length > 0))
    {
      if ((paramVarArgs[0] instanceof CameraActivity)) {
        return new UILocationManagerImpl((CameraActivity)paramVarArgs[0]);
      }
      if ((paramVarArgs[0] instanceof CameraThread)) {
        return new LocationManagerImpl((CameraThread)paramVarArgs[0]);
      }
    }
    return null;
  }
  
  public ComponentCreationPriority getPriority()
  {
    return ComponentCreationPriority.NORMAL;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    return LocationManager.class.isAssignableFrom(paramClass);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/location/LocationManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */