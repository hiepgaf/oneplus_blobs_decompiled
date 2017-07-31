package com.oneplus.camera;

import android.hardware.camera2.CameraDevice;

public class CameraDeviceEventArgs
  extends CameraIdEventArgs
{
  private final CameraDevice m_CameraDevice;
  
  public CameraDeviceEventArgs(CameraDevice paramCameraDevice)
  {
    super(paramCameraDevice.getId());
    this.m_CameraDevice = paramCameraDevice;
  }
  
  public final CameraDevice getCameraDevice()
  {
    return this.m_CameraDevice;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraDeviceEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */