package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class PreviewCoverBuilder
  extends UIComponentBuilder
{
  public PreviewCoverBuilder()
  {
    super(PreviewCoverImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new PreviewCoverImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/PreviewCoverBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */