package com.oneplus.gallery;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class BurstViewerBuilder
  extends UIComponentBuilder
{
  public BurstViewerBuilder()
  {
    super(BurstViewer.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new BurstViewer(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/BurstViewerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */