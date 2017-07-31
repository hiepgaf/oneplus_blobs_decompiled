package com.oneplus.camera;

public final class UnprocessedPictureControllerBuilder
  extends UIComponentBuilder
{
  public UnprocessedPictureControllerBuilder()
  {
    super(UnprocessedPictureControllerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new UnprocessedPictureControllerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UnprocessedPictureControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */