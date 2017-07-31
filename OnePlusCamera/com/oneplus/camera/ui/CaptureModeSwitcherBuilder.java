package com.oneplus.camera.ui;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class CaptureModeSwitcherBuilder
  extends UIComponentBuilder
{
  public CaptureModeSwitcherBuilder()
  {
    super(ComponentCreationPriority.NORMAL, CaptureModeSwitcher.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isBusinessCardMode()) {
      return null;
    }
    return new CaptureModeSwitcher(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CaptureModeSwitcherBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */