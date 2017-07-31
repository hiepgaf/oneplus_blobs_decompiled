package com.oneplus.camera.bokeh;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public final class BokehControllerBuilder
  extends CameraThreadComponentBuilder
{
  public BokehControllerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, BokehController.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new BokehController(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */