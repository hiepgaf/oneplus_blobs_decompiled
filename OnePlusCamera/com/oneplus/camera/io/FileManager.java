package com.oneplus.camera.io;

import android.graphics.Bitmap;
import android.net.Uri;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.camera.media.MediaEventArgs;
import java.io.File;
import java.util.List;

public abstract interface FileManager
  extends Component
{
  public static final EventKey<EventArgs> EVENT_MEDIA_FILES_RESET;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_FILE_ADDED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_FILE_DELETED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_FILE_SAVED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVED = new EventKey("MediaSaved", MediaEventArgs.class, FileManager.class);
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_CANCELLED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_FAILED;
  public static final PropertyKey<Boolean> PROP_IS_SAVING_QUEUE_FULL = new PropertyKey("IsSavingQueueFull", Boolean.class, FileManager.class, Boolean.valueOf(false));
  public static final PropertyKey<Long> PROP_SAVING_QUEUE_SIZE = new PropertyKey("SavingQueueSize", Long.class, FileManager.class, Long.valueOf(0L));
  
  static
  {
    EVENT_MEDIA_FILES_RESET = new EventKey("MediaFileUpdated", EventArgs.class, FileManager.class);
    EVENT_MEDIA_FILE_ADDED = new EventKey("MediaFileUpdated", MediaEventArgs.class, FileManager.class);
    EVENT_MEDIA_FILE_SAVED = new EventKey("MediaFileSaved", MediaEventArgs.class, FileManager.class);
    EVENT_MEDIA_FILE_DELETED = new EventKey("MediaFileSaved", MediaEventArgs.class, FileManager.class);
    EVENT_MEDIA_SAVE_CANCELLED = new EventKey("MediaSaveCancelled", MediaEventArgs.class, FileManager.class);
    EVENT_MEDIA_SAVE_FAILED = new EventKey("MediaSaveFailed", MediaEventArgs.class, FileManager.class);
  }
  
  public abstract void deleteFile(String paramString);
  
  public abstract void getBitmap(String paramString, int paramInt1, int paramInt2, PhotoCallback paramPhotoCallback, int paramInt3);
  
  public abstract Uri getFileUri(String paramString);
  
  public abstract List<File> getMediaFiles();
  
  public abstract boolean isBusy();
  
  public abstract boolean isFileSaving();
  
  public abstract void pauseInsert();
  
  public abstract void resumeInsert();
  
  public abstract Handle saveMedia(MediaSaveTask paramMediaSaveTask, int paramInt);
  
  public abstract void scanFiles();
  
  public abstract void setCurrent(int paramInt);
  
  public static abstract interface PhotoCallback
  {
    public abstract void onBitmapLoad(Bitmap paramBitmap, boolean paramBoolean1, boolean paramBoolean2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/FileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */