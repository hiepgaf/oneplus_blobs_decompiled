package com.oneplus.camera.panorama;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class PanoramaUIBuilder
  extends UIComponentBuilder
{
  public PanoramaUIBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, PanoramaUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new PanoramaUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/panorama/PanoramaUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */