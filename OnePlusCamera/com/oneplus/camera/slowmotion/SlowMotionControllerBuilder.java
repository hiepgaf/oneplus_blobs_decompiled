package com.oneplus.camera.slowmotion;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public final class SlowMotionControllerBuilder
  extends CameraThreadComponentBuilder
{
  public SlowMotionControllerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, SlowMotionController.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new SlowMotionController(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */