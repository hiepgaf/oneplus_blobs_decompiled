package com.oneplus.camera.watermark;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class WatermarkUIBuilder
  extends UIComponentBuilder
{
  public WatermarkUIBuilder()
  {
    super(WatermarkUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new WatermarkUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/WatermarkUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */