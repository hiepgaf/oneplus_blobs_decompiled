package com.oneplus.camera.manual;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public class ManualModeControllerBuilder
  extends CameraThreadComponentBuilder
{
  public ManualModeControllerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, ManualModeController.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new ManualModeController(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ManualModeControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */