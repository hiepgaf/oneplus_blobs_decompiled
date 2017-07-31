package com.oneplus.camera.panorama;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponentBuilder;

public final class PanoramaControllerBuilder
  extends CameraThreadComponentBuilder
{
  public PanoramaControllerBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, PanoramaController.class);
  }
  
  protected CameraComponent create(CameraThread paramCameraThread)
  {
    return new PanoramaController(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/panorama/PanoramaControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */