package com.oneplus.camera.manual;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class ManualModeUIBuilder
  extends UIComponentBuilder
{
  public ManualModeUIBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, ManualModeUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new ManualModeUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ManualModeUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */