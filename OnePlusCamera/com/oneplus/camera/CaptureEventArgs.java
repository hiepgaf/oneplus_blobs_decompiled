package com.oneplus.camera;

import com.oneplus.base.EventArgs;
import com.oneplus.camera.media.MediaType;

public class CaptureEventArgs
  extends EventArgs
{
  private final CaptureHandle m_CaptureHandle;
  private final CaptureTrigger m_CaptureTrigger;
  private final int m_FrameIndex;
  
  public CaptureEventArgs(CaptureHandle paramCaptureHandle)
  {
    this(paramCaptureHandle, 0, CaptureTrigger.SW_BUTTON);
  }
  
  public CaptureEventArgs(CaptureHandle paramCaptureHandle, int paramInt)
  {
    this(paramCaptureHandle, paramInt, CaptureTrigger.SW_BUTTON);
  }
  
  public CaptureEventArgs(CaptureHandle paramCaptureHandle, int paramInt, CaptureTrigger paramCaptureTrigger)
  {
    this.m_CaptureHandle = paramCaptureHandle;
    this.m_CaptureTrigger = paramCaptureTrigger;
    this.m_FrameIndex = paramInt;
  }
  
  public CaptureEventArgs(CaptureHandle paramCaptureHandle, CaptureTrigger paramCaptureTrigger)
  {
    this(paramCaptureHandle, 0, paramCaptureTrigger);
  }
  
  public final CaptureHandle getCaptureHandle()
  {
    return this.m_CaptureHandle;
  }
  
  public final CaptureTrigger getCaptureTrigger()
  {
    return this.m_CaptureTrigger;
  }
  
  public final int getFrameIndex()
  {
    return this.m_FrameIndex;
  }
  
  public final MediaType getMediaType()
  {
    MediaType localMediaType = null;
    if (this.m_CaptureHandle != null) {
      localMediaType = this.m_CaptureHandle.getMediaType();
    }
    return localMediaType;
  }
  
  public final boolean isBurstPhotoCapture()
  {
    if (this.m_CaptureHandle != null) {
      return this.m_CaptureHandle.isBurstPhotoCapture();
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CaptureEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */