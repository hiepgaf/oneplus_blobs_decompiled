package com.oneplus.camera.capturemode;

import com.oneplus.base.EventArgs;

public class CaptureModeEventArgs
  extends EventArgs
{
  private final CaptureMode m_CaptureMode;
  
  public CaptureModeEventArgs(CaptureMode paramCaptureMode)
  {
    this.m_CaptureMode = paramCaptureMode;
  }
  
  public CaptureMode getCaptureMode()
  {
    return this.m_CaptureMode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/CaptureModeEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */