package com.oneplus.camera;

public class CameraOpenFailedEventArgs
  extends CameraEventArgs
{
  public static final int DISCONNECTED = -2;
  public static final int UNKNOWN_ERROR = -4;
  private int m_ErrorCode;
  
  public CameraOpenFailedEventArgs(Camera paramCamera, int paramInt)
  {
    super(paramCamera);
    this.m_ErrorCode = paramInt;
  }
  
  public int getErrorCode()
  {
    return this.m_ErrorCode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraOpenFailedEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */