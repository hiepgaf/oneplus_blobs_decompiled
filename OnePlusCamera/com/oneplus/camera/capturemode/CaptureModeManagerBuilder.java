package com.oneplus.camera.capturemode;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class CaptureModeManagerBuilder
  extends UIComponentBuilder
{
  public CaptureModeManagerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, CaptureModeManagerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new CaptureModeManagerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/CaptureModeManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */