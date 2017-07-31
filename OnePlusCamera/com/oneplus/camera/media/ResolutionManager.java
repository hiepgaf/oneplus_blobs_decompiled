package com.oneplus.camera.media;

import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface ResolutionManager
  extends Component
{
  public static final int FLAG_SYNC_RESOLUTION = 1;
  public static final PropertyKey<Long> PROP_MAX_VIDEO_DURATION_SECONDS = new PropertyKey("MaxVideoDurationSeconds", Long.class, ResolutionManager.class, Long.valueOf(-1L));
  public static final PropertyKey<Long> PROP_MAX_VIDEO_FILE_SIZE = new PropertyKey("MaxVideoFileSize", Long.class, ResolutionManager.class, Long.valueOf(-1L));
  public static final PropertyKey<Size> PROP_PHOTO_PREVIEW_SIZE = new PropertyKey("PhotoPreviewSize", Size.class, ResolutionManager.class, new Size(0, 0));
  public static final PropertyKey<Resolution> PROP_PHOTO_RESOLUTION = new PropertyKey("PhotoResolution", Resolution.class, ResolutionManager.class, 0, null);
  public static final PropertyKey<List<Resolution>> PROP_PHOTO_RESOLUTION_LIST = new PropertyKey("PhotoResolutionList", List.class, ResolutionManager.class, Collections.EMPTY_LIST);
  public static final PropertyKey<Size> PROP_VIDEO_PREVIEW_SIZE = new PropertyKey("VideoPreviewSize", Size.class, ResolutionManager.class, new Size(0, 0));
  public static final PropertyKey<Resolution> PROP_VIDEO_RESOLUTION = new PropertyKey("VideoResolution", Resolution.class, ResolutionManager.class, 0, null);
  public static final PropertyKey<List<Resolution>> PROP_VIDEO_RESOLUTION_LIST = new PropertyKey("VideoResolutionList", List.class, ResolutionManager.class, Collections.EMPTY_LIST);
  
  public abstract String getPhotoResolutionSettingsKey();
  
  public abstract Handle setResolutionSelector(ResolutionSelector paramResolutionSelector, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/ResolutionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */