package com.oneplus.camera.watermark;

import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public class OnlineWatermarkControllerBuilder
  extends CameraThreadComponentBuilder
{
  public OnlineWatermarkControllerBuilder()
  {
    super(OnlineWatermarkControllerImpl.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new OnlineWatermarkControllerImpl(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/OnlineWatermarkControllerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */