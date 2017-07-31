package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class ZoomBarBuilder
  extends UIComponentBuilder
{
  public ZoomBarBuilder()
  {
    super(ZoomBarImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new ZoomBarImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ZoomBarBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */