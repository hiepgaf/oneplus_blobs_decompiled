package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public class AppTrackerBuilder
  extends UIComponentBuilder
{
  public AppTrackerBuilder()
  {
    super(ComponentCreationPriority.HIGH, AppTrackerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new AppTrackerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/AppTrackerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */