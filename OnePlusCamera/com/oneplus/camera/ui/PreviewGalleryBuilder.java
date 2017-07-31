package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class PreviewGalleryBuilder
  extends UIComponentBuilder
{
  public PreviewGalleryBuilder()
  {
    super(PreviewGallery.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (!paramCameraActivity.isServiceMode()) {
      return new PreviewGallery(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/PreviewGalleryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */