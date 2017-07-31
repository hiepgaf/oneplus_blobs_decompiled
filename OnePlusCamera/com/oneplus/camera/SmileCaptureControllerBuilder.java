package com.oneplus.camera;

public final class SmileCaptureControllerBuilder
  extends UIComponentBuilder
{
  public SmileCaptureControllerBuilder()
  {
    super(SmileCaptureControllerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new SmileCaptureControllerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/SmileCaptureControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */