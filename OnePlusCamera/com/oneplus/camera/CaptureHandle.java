package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.Rotation;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.media.MediaType;

public abstract class CaptureHandle
  extends Handle
{
  private final Camera m_Camera;
  private final CaptureMode m_CaptureMode;
  private final Rotation m_CaptureRotation;
  private boolean m_IsMirrored;
  private final MediaType m_MediaType;
  
  protected CaptureHandle(Camera paramCamera, CaptureMode paramCaptureMode, Rotation paramRotation, MediaType paramMediaType)
  {
    super("Capture");
    if (paramMediaType == null) {
      throw new IllegalArgumentException("No media type specified.");
    }
    this.m_Camera = paramCamera;
    this.m_CaptureMode = paramCaptureMode;
    this.m_CaptureRotation = paramRotation;
    this.m_MediaType = paramMediaType;
  }
  
  public final Camera getCamera()
  {
    return this.m_Camera;
  }
  
  public final CaptureMode getCaptureMode()
  {
    return this.m_CaptureMode;
  }
  
  public abstract long getCaptureRealTime();
  
  public final Rotation getCaptureRotation()
  {
    return this.m_CaptureRotation;
  }
  
  public CaptureHandle getInternalCaptureHandle()
  {
    return null;
  }
  
  public final MediaType getMediaType()
  {
    return this.m_MediaType;
  }
  
  public boolean isBurstPhotoCapture()
  {
    return false;
  }
  
  public boolean isMirrored()
  {
    return this.m_IsMirrored;
  }
  
  public boolean isVideoSnapshot()
  {
    return false;
  }
  
  public void setIsMirrored(boolean paramBoolean)
  {
    this.m_IsMirrored = paramBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CaptureHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */