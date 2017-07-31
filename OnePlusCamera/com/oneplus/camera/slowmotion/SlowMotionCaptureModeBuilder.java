package com.oneplus.camera.slowmotion;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeBuilder;

public class SlowMotionCaptureModeBuilder
  implements CaptureModeBuilder
{
  public CaptureMode createCaptureMode(CameraActivity paramCameraActivity)
  {
    if (!paramCameraActivity.isServiceMode()) {
      return new SlowMotionCaptureMode(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionCaptureModeBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */