package com.oneplus.camera;

import android.net.Uri;
import android.util.Log;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.camera.media.MediaEventArgs;
import java.util.Hashtable;

final class UnprocessedPictureControllerImpl
  extends CameraComponent
  implements UnprocessedPictureController
{
  private PictureProcessService m_PictureProcessService;
  private Hashtable<CaptureHandle, String> m_UnprocessedPicutreIdTable = new Hashtable();
  
  UnprocessedPictureControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Unprocessed picture controller", paramCameraActivity, true);
  }
  
  private void onMediaSaved(CaptureHandle paramCaptureHandle, String paramString, Uri paramUri)
  {
    if (paramCaptureHandle == null) {
      return;
    }
    if (this.m_PictureProcessService == null)
    {
      Log.e(this.TAG, "onMediaSaved() - m_CameraSystemService is null");
      return;
    }
    Log.v(this.TAG, "onMediaSaved() - captureHandle: " + paramCaptureHandle);
    String str;
    PictureProcessService localPictureProcessService;
    if (this.m_UnprocessedPicutreIdTable.containsKey(paramCaptureHandle))
    {
      str = (String)this.m_UnprocessedPicutreIdTable.get(paramCaptureHandle);
      Log.v(this.TAG, "onMediaSaved() - pictureId: " + str);
      localPictureProcessService = this.m_PictureProcessService;
      if (paramUri == null) {
        break label133;
      }
    }
    for (;;)
    {
      localPictureProcessService.onUnprocessedPictureSaved(str, paramString, paramUri);
      this.m_UnprocessedPicutreIdTable.remove(paramCaptureHandle);
      return;
      label133:
      paramUri = Uri.EMPTY;
    }
  }
  
  private void onUnprocessedPictureReceived(UnprocessedPictureEventArgs paramUnprocessedPictureEventArgs)
  {
    if (paramUnprocessedPictureEventArgs == null)
    {
      Log.e(this.TAG, "onUnprocessedPictureReceived() - args is null");
      return;
    }
    if (this.m_PictureProcessService == null)
    {
      Log.e(this.TAG, "onUnprocessedPictureReceived() - m_CameraSystemService is null");
      return;
    }
    Log.v(this.TAG, "onUnprocessedPictureReceived() - arg.getCaptureHandle(): " + paramUnprocessedPictureEventArgs.getCaptureHandle() + " , arg.getPictureId(): " + paramUnprocessedPictureEventArgs.getPictureId());
    this.m_UnprocessedPicutreIdTable.put(paramUnprocessedPictureEventArgs.getCaptureHandle(), paramUnprocessedPictureEventArgs.getPictureId());
    this.m_PictureProcessService.onUnprocessedPictureReceived(paramUnprocessedPictureEventArgs.getPictureId(), paramUnprocessedPictureEventArgs.getHALPictureId());
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_PictureProcessService = ((PictureProcessService)findComponent(PictureProcessService.class));
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        UnprocessedPictureControllerImpl.-wrap0(UnprocessedPictureControllerImpl.this, paramAnonymousMediaEventArgs.getCaptureHandle(), paramAnonymousMediaEventArgs.getFilePath(), paramAnonymousMediaEventArgs.getContentUri());
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVE_FAILED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        UnprocessedPictureControllerImpl.-wrap0(UnprocessedPictureControllerImpl.this, paramAnonymousMediaEventArgs.getCaptureHandle(), null, null);
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_UNPROCESSED_PHOTO_RECEIVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<UnprocessedPictureEventArgs> paramAnonymousEventKey, UnprocessedPictureEventArgs paramAnonymousUnprocessedPictureEventArgs)
      {
        UnprocessedPictureControllerImpl.-wrap1(UnprocessedPictureControllerImpl.this, paramAnonymousUnprocessedPictureEventArgs);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UnprocessedPictureControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */