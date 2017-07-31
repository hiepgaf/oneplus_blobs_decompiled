package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class ShutterEffectBuilder
  extends UIComponentBuilder
{
  public ShutterEffectBuilder()
  {
    super(ShutterEffect.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new ShutterEffect(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ShutterEffectBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */