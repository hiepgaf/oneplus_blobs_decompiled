package com.oneplus.camera.capturemode;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.StartMode;

public class VideoCaptureModeBuilder
  implements CaptureModeBuilder
{
  public CaptureMode createCaptureMode(CameraActivity paramCameraActivity)
  {
    if ((!paramCameraActivity.isServiceMode()) || (paramCameraActivity.getStartMode() == StartMode.SERVICE_VIDEO)) {
      return new VideoCaptureMode(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/VideoCaptureModeBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */