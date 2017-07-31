package com.oneplus.gallery2.media;

import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.util.Size;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;

final class VideoMtpMedia
  extends MtpMedia
  implements VideoMedia
{
  private Size m_Size;
  
  VideoMtpMedia(MtpMediaSource paramMtpMediaSource, MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    super(paramMtpMediaSource, MediaType.VIDEO, paramMtpDevice, paramMtpObjectInfo);
  }
  
  public Handle getDetails(Media.DetailsCallback paramDetailsCallback)
  {
    return null;
  }
  
  public Handle getDuration(VideoMedia.DurationCallback paramDurationCallback)
  {
    return null;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    int j = 0;
    if (paramSizeCallback == null) {
      return new EmptyHandle("GetMtpPhotoSize");
    }
    int i;
    if (this.m_Size == null)
    {
      i = 0;
      label25:
      if (this.m_Size != null) {
        break label55;
      }
    }
    for (;;)
    {
      paramSizeCallback.onSizeObtained(this, i, j);
      break;
      i = this.m_Size.getWidth();
      break label25;
      label55:
      j = this.m_Size.getHeight();
    }
  }
  
  public boolean isSlowMotion()
  {
    return false;
  }
  
  public boolean isTimeLapse()
  {
    return false;
  }
  
  protected int onUpdate(MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo, boolean paramBoolean)
  {
    int i = super.onUpdate(paramMtpDevice, paramMtpObjectInfo, paramBoolean);
    int j = paramMtpObjectInfo.getImagePixWidth();
    int k = paramMtpObjectInfo.getImagePixHeight();
    if (this.m_Size == null) {}
    while ((this.m_Size.getWidth() != j) || (this.m_Size.getHeight() != k))
    {
      this.m_Size = new Size(j, k);
      return i | FLAG_SIZE_CHANGED;
    }
    return i;
  }
  
  public Long peekDuration()
  {
    return null;
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/VideoMtpMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */