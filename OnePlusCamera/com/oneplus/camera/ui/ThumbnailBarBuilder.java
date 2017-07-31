package com.oneplus.camera.ui;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class ThumbnailBarBuilder
  extends UIComponentBuilder
{
  public ThumbnailBarBuilder()
  {
    super(ComponentCreationPriority.NORMAL, ThumbnailBarImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isServiceMode()) {
      return null;
    }
    return new ThumbnailBarImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ThumbnailBarBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */