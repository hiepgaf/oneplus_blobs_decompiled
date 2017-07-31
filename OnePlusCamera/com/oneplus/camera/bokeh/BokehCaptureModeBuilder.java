package com.oneplus.camera.bokeh;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeBuilder;

public class BokehCaptureModeBuilder
  implements CaptureModeBuilder
{
  public CaptureMode createCaptureMode(CameraActivity paramCameraActivity)
  {
    if (!paramCameraActivity.isServiceMode()) {
      return new BokehCaptureMode(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehCaptureModeBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */