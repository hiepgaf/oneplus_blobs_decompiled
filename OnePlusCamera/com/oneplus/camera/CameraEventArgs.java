package com.oneplus.camera;

import com.oneplus.base.EventArgs;

public class CameraEventArgs
  extends EventArgs
{
  private final Camera m_Camera;
  
  public CameraEventArgs(Camera paramCamera)
  {
    this.m_Camera = paramCamera;
  }
  
  public final Camera getCamera()
  {
    return this.m_Camera;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */