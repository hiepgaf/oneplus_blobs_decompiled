package com.oneplus.camera.bokeh;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class BokehUIBuilder
  extends UIComponentBuilder
{
  public BokehUIBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, BokehUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new BokehUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */