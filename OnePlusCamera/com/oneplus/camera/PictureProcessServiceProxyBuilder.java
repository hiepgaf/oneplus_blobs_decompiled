package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public class PictureProcessServiceProxyBuilder
  extends UIComponentBuilder
{
  public PictureProcessServiceProxyBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, PictureProcessServiceProxy.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new PictureProcessServiceProxy(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/PictureProcessServiceProxyBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */