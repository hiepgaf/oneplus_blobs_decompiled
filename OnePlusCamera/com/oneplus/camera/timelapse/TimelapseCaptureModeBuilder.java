package com.oneplus.camera.timelapse;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeBuilder;

public class TimelapseCaptureModeBuilder
  implements CaptureModeBuilder
{
  public CaptureMode createCaptureMode(CameraActivity paramCameraActivity)
  {
    if (!paramCameraActivity.isServiceMode()) {
      return new TimelapseCaptureMode(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/timelapse/TimelapseCaptureModeBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */