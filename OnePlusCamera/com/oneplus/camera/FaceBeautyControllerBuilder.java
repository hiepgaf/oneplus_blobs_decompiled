package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public class FaceBeautyControllerBuilder
  extends UIComponentBuilder
{
  public FaceBeautyControllerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, FaceBeautyControllerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new FaceBeautyControllerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FaceBeautyControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */