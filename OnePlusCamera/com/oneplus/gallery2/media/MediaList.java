package com.oneplus.gallery2.media;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventKey;
import com.oneplus.base.HandlerObject;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;
import java.util.List;

public abstract interface MediaList
  extends List<Media>, BaseObject, HandlerObject
{
  public static final MediaList EMPTY = new SimpleMediaList(new Media[0]);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_ADDED = new EventKey("MediaAdded", ListChangeEventArgs.class, MediaList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_MOVED = new EventKey("MediaMoved", ListMoveEventArgs.class, MediaList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_MOVING = new EventKey("MediaMoving", ListMoveEventArgs.class, MediaList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_REMOVED = new EventKey("MediaRemoved", ListChangeEventArgs.class, MediaList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_REMOVING = new EventKey("MediaRemoving", ListChangeEventArgs.class, MediaList.class);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */