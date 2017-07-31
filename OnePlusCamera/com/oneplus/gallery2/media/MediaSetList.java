package com.oneplus.gallery2.media;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.PropertyKey;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;
import java.util.List;

public abstract interface MediaSetList
  extends List<MediaSet>, BaseObject, HandlerObject
{
  public static final MediaSetList EMPTY = new BaseMediaSetList(MediaSetComparator.DEFAULT) {};
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_ADDED = new EventKey("MediaSetAdded", ListChangeEventArgs.class, MediaSetList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_SET_MOVED = new EventKey("MediaSetMoved", ListMoveEventArgs.class, MediaSetList.class);
  public static final EventKey<ListMoveEventArgs> EVENT_MEDIA_SET_MOVING = new EventKey("MediaSetMoving", ListMoveEventArgs.class, MediaSetList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_REMOVED = new EventKey("MediaSetRemoved", ListChangeEventArgs.class, MediaSetList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_MEDIA_SET_REMOVING = new EventKey("MediaSetRemoving", ListChangeEventArgs.class, MediaSetList.class);
  public static final EventKey<EventArgs> EVENT_RESET = new EventKey("Reset", EventArgs.class, MediaSetList.class);
  public static final PropertyKey<MediaSetComparator> PROP_COMPARATOR = new PropertyKey("Comparator", MediaSetComparator.class, MediaSetList.class, 2, MediaSetComparator.DEFAULT);
  public static final PropertyKey<Integer> PROP_HIDDEN_MEDIA_SET_COUNT = new PropertyKey("HiddenMediaSetsCount", Integer.class, MediaSetList.class, Integer.valueOf(0));
  public static final PropertyKey<Boolean> PROP_IS_READY = new PropertyKey("IsReady", Boolean.class, MediaSetList.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_SHOW_HIDDEN_MEDIA_SETS = new PropertyKey("ShowHiddenMediaSets", Boolean.class, MediaSetList.class, 2, Boolean.valueOf(false));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSetList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */