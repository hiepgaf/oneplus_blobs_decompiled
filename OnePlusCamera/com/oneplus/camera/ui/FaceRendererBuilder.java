package com.oneplus.camera.ui;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class FaceRendererBuilder
  extends UIComponentBuilder
{
  public FaceRendererBuilder()
  {
    super(ComponentCreationPriority.NORMAL, FaceRenderer.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new FaceRenderer(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/FaceRendererBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */