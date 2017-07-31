package com.oneplus.camera;

public final class SensorFocusControllerBuilder
  extends UIComponentBuilder
{
  public SensorFocusControllerBuilder()
  {
    super(SensorFocusControllerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new SensorFocusControllerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/SensorFocusControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */