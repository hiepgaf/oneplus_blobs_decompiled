package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public class CameraServiceProxyBuilder
  extends UIComponentBuilder
{
  public CameraServiceProxyBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, CameraServiceProxy.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new CameraServiceProxy(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraServiceProxyBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */