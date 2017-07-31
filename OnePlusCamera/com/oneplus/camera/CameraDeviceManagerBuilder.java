package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

class CameraDeviceManagerBuilder
  extends CameraThreadComponentBuilder
{
  public CameraDeviceManagerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, CameraDeviceManagerImpl.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new CameraDeviceManagerImpl(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraDeviceManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */