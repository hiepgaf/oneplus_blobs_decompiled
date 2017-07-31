package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public final class FlashControllerBuilder
  extends UIComponentBuilder
{
  public FlashControllerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, FlashControllerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new FlashControllerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FlashControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */