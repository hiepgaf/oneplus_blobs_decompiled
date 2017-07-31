package com.oneplus.gallery.media;

import android.os.Handler;
import android.os.Looper;

public final class MediaDeletionCallbackUtils
{
  public static void callOnDeletionCompleted(MediaDeletionCallback paramMediaDeletionCallback, final Media paramMedia, final boolean paramBoolean, Handler paramHandler)
  {
    if (paramMediaDeletionCallback != null) {
      if (paramHandler != null) {
        break label18;
      }
    }
    label18:
    while (paramHandler.getLooper().getThread() == Thread.currentThread())
    {
      paramMediaDeletionCallback.onDeletionCompleted(paramMedia, paramBoolean);
      return;
      return;
    }
    paramHandler.post(new Runnable()
    {
      public void run()
      {
        MediaDeletionCallbackUtils.this.onDeletionCompleted(paramMedia, paramBoolean);
      }
    });
  }
  
  public static void callOnDeletionStarted(MediaDeletionCallback paramMediaDeletionCallback, final Media paramMedia, Handler paramHandler)
  {
    if (paramMediaDeletionCallback != null) {
      if (paramHandler != null) {
        break label17;
      }
    }
    label17:
    while (paramHandler.getLooper().getThread() == Thread.currentThread())
    {
      paramMediaDeletionCallback.onDeletionStarted(paramMedia);
      return;
      return;
    }
    paramHandler.post(new Runnable()
    {
      public void run()
      {
        MediaDeletionCallbackUtils.this.onDeletionStarted(paramMedia);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaDeletionCallbackUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */