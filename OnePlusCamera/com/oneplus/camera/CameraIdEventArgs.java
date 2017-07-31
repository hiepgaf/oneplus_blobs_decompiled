package com.oneplus.camera;

import com.oneplus.base.EventArgs;

public class CameraIdEventArgs
  extends EventArgs
{
  private final String m_CameraId;
  
  public CameraIdEventArgs(String paramString)
  {
    this.m_CameraId = paramString;
  }
  
  public final String getCameraId()
  {
    return this.m_CameraId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraIdEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */