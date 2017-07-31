package com.oneplus.camera;

import android.graphics.Rect;
import android.net.Uri;
import com.oneplus.base.EventKey;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.watermark.Watermark;

public abstract interface PictureProcessService
  extends Component
{
  public static final EventKey<MediaEventArgs> EVENT_PICTURE_PROCESSED = new EventKey("PictureProcessed", MediaEventArgs.class, PictureProcessService.class);
  public static final EventKey<MediaEventArgs> EVENT_WATERMARK_PROCESSED = new EventKey("WatermarkProcessed", MediaEventArgs.class, PictureProcessService.class);
  public static final EventKey<MediaEventArgs> EVENT_WATERMARK_PROCESSING = new EventKey("WatermarkProcessing", MediaEventArgs.class, PictureProcessService.class);
  public static final String EXTRA_CONTENT_URI = "contentUri";
  public static final String EXTRA_FILE_PATH = "filePath";
  public static final String EXTRA_HAL_PICTURE_ID = "halPictureId";
  public static final String EXTRA_PICTURE_ID = "pictureId";
  public static final String EXTRA_PROCESS_TYPES = "processTypes";
  public static final String EXTRA_WATERMARK = "watermark";
  public static final String EXTRA_WATERMARK_BOUNDS = "watermarkBounds";
  public static final String EXTRA_WATERMARK_TEXT = "watermarkText";
  public static final String INTENT_CLEAR_IMAGE_CACHE = "com.oneplus.camera.service.CLEAR_IMAGE_CACHE";
  public static final int MSG_SCHEDULE_PROCESS_WATERMARK = -130005;
  public static final int MSG_UNPROCESSED_PICTURE_RECEIVED = -130001;
  public static final int MSG_UNPROCESSED_PICTURE_SAVED = -130002;
  public static final PropertyKey<Boolean> PROP_IS_CONNECTED = new PropertyKey("IsConnected", Boolean.class, PictureProcessService.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_PROCESSING = new PropertyKey("IsProcessing", Boolean.class, PictureProcessService.class, Boolean.valueOf(false));
  
  public abstract boolean isPictureProcessing(String paramString);
  
  public abstract boolean isWatermarkProcessing(String paramString);
  
  public abstract void onUnprocessedPictureReceived(String paramString1, String paramString2);
  
  public abstract void onUnprocessedPictureSaved(String paramString1, String paramString2, Uri paramUri);
  
  public abstract void scheduleProcessWatermark(String paramString1, String paramString2, Watermark paramWatermark, Rect paramRect, String paramString3);
  
  public static enum ProcessType
  {
    OFFLINE,  WATERMARK;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/PictureProcessService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */