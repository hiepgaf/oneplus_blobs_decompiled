package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public final class FaceTrackerBuilder
  extends UIComponentBuilder
{
  public FaceTrackerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, FaceTrackerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new FaceTrackerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FaceTrackerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */