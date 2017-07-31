package com.oneplus.camera.media;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventKey;
import java.util.List;

public abstract interface MediaList
  extends List<MediaInfo>, BaseObject
{
  public static final EventKey<MediaListChangeEventArgs> EVENT_MEDIA_ADDED = new EventKey("MediaAdded", MediaListChangeEventArgs.class, MediaList.class);
  public static final EventKey<MediaListChangeEventArgs> EVENT_MEDIA_REMOVED = new EventKey("MediaRemoved", MediaListChangeEventArgs.class, MediaList.class);
  public static final EventKey<MediaListChangeEventArgs> EVENT_MEDIA_REMOVING = new EventKey("MediaRemoving", MediaListChangeEventArgs.class, MediaList.class);
  public static final EventKey<MediaListChangeEventArgs> EVENT_MEDIA_REPLACED = new EventKey("MediaReplaced", MediaListChangeEventArgs.class, MediaList.class);
  public static final EventKey<MediaListChangeEventArgs> EVENT_MEDIA_REPLACING = new EventKey("MediaReplacing", MediaListChangeEventArgs.class, MediaList.class);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/MediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */