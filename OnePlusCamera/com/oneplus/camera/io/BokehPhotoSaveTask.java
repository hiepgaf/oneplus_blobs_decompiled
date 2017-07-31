package com.oneplus.camera.io;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.media.EncodedImage;
import com.oneplus.media.OnePlusXMP;
import com.oneplus.media.XMPContainer;
import com.oneplus.media.XMPPropertyKey;

public class BokehPhotoSaveTask
  extends PhotoSaveTask
{
  private static final String FILE_PATH_SUFFIX = "Bokeh";
  private final boolean m_IsOriginalPicture;
  
  public BokehPhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    super(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    if ((paramCameraCaptureEventArgs.getFlags() & Camera.FLAG_ORIGINAL_PICTURE) != 0) {
      bool = true;
    }
    this.m_IsOriginalPicture = bool;
  }
  
  protected String getFilePathSuffix()
  {
    if (!this.m_IsOriginalPicture) {
      return "Bokeh";
    }
    return null;
  }
  
  protected void onImageEncoded(EncodedImage paramEncodedImage)
  {
    XMPPropertyKey localXMPPropertyKey;
    if ((paramEncodedImage instanceof XMPContainer))
    {
      paramEncodedImage = (XMPContainer)paramEncodedImage;
      localXMPPropertyKey = OnePlusXMP.KEY_IS_BOKEH_ACTIVE;
      if (!this.m_IsOriginalPicture) {
        break label37;
      }
    }
    label37:
    for (boolean bool = false;; bool = true)
    {
      paramEncodedImage.setXMPProperty(localXMPPropertyKey, Boolean.valueOf(bool));
      return;
    }
  }
  
  protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    long l2 = 0L;
    long l1 = l2;
    if (super.onPrepareGalleryDatabaseValues(paramString, paramUri, paramContentValues))
    {
      paramString = paramContentValues.getAsLong("oneplus_flags");
      l1 = l2;
      if (paramString != null) {
        l1 = paramString.longValue();
      }
    }
    if (!this.m_IsOriginalPicture) {
      paramContentValues.put("oneplus_flags", Long.valueOf(0x40 | l1));
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/BokehPhotoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */