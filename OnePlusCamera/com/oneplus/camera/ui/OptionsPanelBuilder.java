package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class OptionsPanelBuilder
  extends UIComponentBuilder
{
  public OptionsPanelBuilder()
  {
    super(OptionsPanelImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isBusinessCardMode()) {
      return null;
    }
    return new OptionsPanelImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/OptionsPanelBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */