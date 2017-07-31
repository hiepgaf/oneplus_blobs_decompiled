package com.oneplus.camera.timelapse;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class TimelapseUIBuilder
  extends UIComponentBuilder
{
  public TimelapseUIBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, TimelapseUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new TimelapseUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/timelapse/TimelapseUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */