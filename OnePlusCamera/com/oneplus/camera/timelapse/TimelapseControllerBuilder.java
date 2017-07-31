package com.oneplus.camera.timelapse;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public final class TimelapseControllerBuilder
  extends CameraThreadComponentBuilder
{
  public TimelapseControllerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, TimelapseController.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new TimelapseController(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/timelapse/TimelapseControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */