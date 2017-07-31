package com.oneplus.gallery.media;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventKey;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.PropertyKey;
import com.oneplus.gallery.ListChangeEventArgs;
import com.oneplus.gallery.ListMoveEventArgs;
import java.util.List;

public abstract interface MediaSetList
  extends List<MediaSet>, BaseObject, HandlerObject
{
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_ADDED = new EventKey("MediaSetAdded", ListChangeEventArgs.class, MediaSetList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_SET_MOVED = new EventKey("MediaSetMoved", ListMoveEventArgs.class, MediaSetList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_SET_MOVING = new EventKey("MediaSetMoving", ListMoveEventArgs.class, MediaSetList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_REMOVED = new EventKey("MediaSetRemoved", ListChangeEventArgs.class, MediaSetList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_REMOVING = new EventKey("MediaSetRemoving", ListChangeEventArgs.class, MediaSetList.class);
  public static final PropertyKey<Comparator> PROP_COMPARATOR;
  public static final PropertyKey<Boolean> PROP_CONTAINS_CAMERA_ROLL = new PropertyKey("ContainsCameraRoll", Boolean.class, MediaSetList.class, 1, null);
  public static final PropertyKey<Boolean> PROP_IS_READY;
  
  static
  {
    PROP_COMPARATOR = new PropertyKey("Comparator", Comparator.class, MediaSetList.class, 2, Comparator.SYSTEM_ALBUM_DIRECTORY);
    PROP_IS_READY = new PropertyKey("IsReady", Boolean.class, MediaSetList.class, Boolean.valueOf(false));
  }
  
  public static enum Comparator
  {
    SYSTEM_ALBUM_DIRECTORY,  SYSTEM_DATE,  SYSTEM_NAME;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaSetList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */