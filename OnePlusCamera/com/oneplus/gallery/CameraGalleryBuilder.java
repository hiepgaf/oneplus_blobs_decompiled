package com.oneplus.gallery;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class CameraGalleryBuilder
  extends UIComponentBuilder
{
  public CameraGalleryBuilder()
  {
    super(CameraGalleryImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isServiceMode()) {
      return null;
    }
    return new CameraGalleryImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/CameraGalleryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */