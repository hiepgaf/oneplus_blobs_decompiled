package com.oneplus.camera.media;

import android.graphics.Bitmap;
import android.net.Uri;
import com.oneplus.base.EventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.io.MediaSaveTask;

public class MediaEventArgs
  extends EventArgs
{
  private final CaptureHandle m_CaptureHandle;
  private final Uri m_ContentUri;
  private final String m_FilePath;
  private final int m_FrameIndex;
  private final String m_PictureId;
  private final Bitmap m_Thumbnail;
  
  public MediaEventArgs(CaptureHandle paramCaptureHandle, String paramString1, int paramInt, String paramString2, Uri paramUri, Bitmap paramBitmap)
  {
    this.m_CaptureHandle = paramCaptureHandle;
    this.m_FrameIndex = paramInt;
    this.m_FilePath = paramString2;
    this.m_ContentUri = paramUri;
    this.m_PictureId = paramString1;
    this.m_Thumbnail = paramBitmap;
  }
  
  public MediaEventArgs(MediaSaveTask paramMediaSaveTask, boolean paramBoolean)
  {
    this.m_CaptureHandle = paramMediaSaveTask.getCaptureHandle();
    this.m_FrameIndex = paramMediaSaveTask.getFrameIndex();
    this.m_FilePath = paramMediaSaveTask.getFilePath();
    this.m_ContentUri = paramMediaSaveTask.getContentUri();
    this.m_PictureId = paramMediaSaveTask.getPictureId();
    if (paramBoolean)
    {
      this.m_Thumbnail = paramMediaSaveTask.getThumbnail();
      return;
    }
    this.m_Thumbnail = null;
  }
  
  public final CaptureHandle getCaptureHandle()
  {
    return this.m_CaptureHandle;
  }
  
  public final Uri getContentUri()
  {
    return this.m_ContentUri;
  }
  
  public final String getFilePath()
  {
    return this.m_FilePath;
  }
  
  public final int getFrameIndex()
  {
    return this.m_FrameIndex;
  }
  
  public final String getPictureId()
  {
    return this.m_PictureId;
  }
  
  public Bitmap getThumbnail()
  {
    return this.m_Thumbnail;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/MediaEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */