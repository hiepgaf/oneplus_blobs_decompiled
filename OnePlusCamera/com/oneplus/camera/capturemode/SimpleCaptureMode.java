package com.oneplus.camera.capturemode;

import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.media.MediaType;

public abstract class SimpleCaptureMode
  extends BasicCaptureMode
{
  private final MediaType m_MediaType;
  
  protected SimpleCaptureMode(CameraActivity paramCameraActivity, String paramString1, MediaType paramMediaType, String paramString2)
  {
    super(paramCameraActivity, paramString1, paramString2, paramMediaType);
    if (paramMediaType == null) {
      throw new IllegalArgumentException("No target media type.");
    }
    this.m_MediaType = paramMediaType;
  }
  
  public boolean isSimpleCaptureMode()
  {
    return true;
  }
  
  protected boolean onEnter(CaptureMode paramCaptureMode, int paramInt)
  {
    paramCaptureMode = getCameraActivity();
    if ((paramInt & 0x1) == 0) {
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((com.oneplus.camera.OperationState)paramCaptureMode.get(CameraActivity.PROP_CAMERA_PREVIEW_STATE)).ordinal()])
      {
      default: 
        paramInt = 0;
      }
    }
    try
    {
      while (!paramCaptureMode.setMediaType(this.m_MediaType))
      {
        Log.e(this.TAG, "onEnter() - Fail to change nedia type to " + this.m_MediaType);
        return false;
        Log.v(this.TAG, "onEnter() - Stop preview");
        paramInt = 1;
        paramCaptureMode.stopCameraPreview();
        continue;
        paramInt = 0;
      }
      return true;
    }
    finally
    {
      if (paramInt != 0)
      {
        Log.v(this.TAG, "onEnter() - Restart preview");
        paramCaptureMode.startCameraPreview();
      }
    }
  }
  
  protected void onExit(CaptureMode paramCaptureMode, int paramInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/SimpleCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */