package com.oneplus.camera;

import com.oneplus.base.EventArgs;

public class UnprocessedPictureEventArgs
  extends EventArgs
{
  private final CaptureHandle m_CaptureHandle;
  private final String m_HALPictureId;
  private final String m_PictureId;
  
  public UnprocessedPictureEventArgs(CaptureHandle paramCaptureHandle, String paramString1, String paramString2)
  {
    this.m_CaptureHandle = paramCaptureHandle;
    this.m_PictureId = paramString1;
    this.m_HALPictureId = paramString2;
  }
  
  public final CaptureHandle getCaptureHandle()
  {
    return this.m_CaptureHandle;
  }
  
  public final String getHALPictureId()
  {
    return this.m_HALPictureId;
  }
  
  public final String getPictureId()
  {
    return this.m_PictureId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UnprocessedPictureEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */