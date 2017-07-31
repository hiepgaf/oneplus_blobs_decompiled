package com.oneplus.gallery2.media;

import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;

public abstract class FilteredMediaList
  extends BaseMediaList
{
  private final EventHandler<ListChangeEventArgs> m_MediaAddedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      FilteredMediaList.this.onMediaAdded(paramAnonymousListChangeEventArgs);
    }
  };
  private final MediaList m_MediaList;
  private final EventHandler<ListMoveEventArgs> m_MediaMovedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListMoveEventArgs> paramAnonymousEventKey, ListMoveEventArgs paramAnonymousListMoveEventArgs)
    {
      FilteredMediaList.this.onMediaMoved(paramAnonymousListMoveEventArgs);
    }
  };
  private final EventHandler<ListChangeEventArgs> m_MediaRemovingHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      FilteredMediaList.this.onMediaRemoving(paramAnonymousListChangeEventArgs);
    }
  };
  private final boolean m_OwnsMediaList;
  
  protected FilteredMediaList(MediaComparator paramMediaComparator, int paramInt, MediaList paramMediaList, boolean paramBoolean)
  {
    super(paramMediaComparator, paramInt);
    this.m_OwnsMediaList = paramBoolean;
    this.m_MediaList = paramMediaList;
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_ADDED, this.m_MediaAddedHandler);
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_MOVED, this.m_MediaMovedHandler);
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_MediaRemovingHandler);
    paramInt = paramMediaList.size() - 1;
    if (paramInt >= 0)
    {
      paramMediaComparator = (Media)paramMediaList.get(paramInt);
      if (!filterMedia(paramMediaComparator)) {}
      for (;;)
      {
        paramInt -= 1;
        break;
        addMedia(paramMediaComparator);
      }
    }
  }
  
  private void onMediaAdded(ListChangeEventArgs paramListChangeEventArgs)
  {
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    if (i <= j)
    {
      paramListChangeEventArgs = (Media)this.m_MediaList.get(i);
      if (!filterMedia(paramListChangeEventArgs)) {}
      for (;;)
      {
        i += 1;
        break;
        addMedia(paramListChangeEventArgs);
      }
    }
  }
  
  private void onMediaMoved(ListMoveEventArgs paramListMoveEventArgs)
  {
    int i = paramListMoveEventArgs.getStartIndex();
    int j = paramListMoveEventArgs.getEndIndex();
    while (i <= j)
    {
      checkMediaIndex((Media)this.m_MediaList.get(i));
      i += 1;
    }
  }
  
  private void onMediaRemoving(ListChangeEventArgs paramListChangeEventArgs)
  {
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      removeMedia((Media)this.m_MediaList.get(i));
      i += 1;
    }
  }
  
  public boolean checkMedia(Media paramMedia)
  {
    if (paramMedia != null)
    {
      verifyAccess();
      if (this.m_MediaList.contains(paramMedia)) {
        break label29;
      }
    }
    label29:
    while (!filterMedia(paramMedia))
    {
      return removeMedia(paramMedia);
      return false;
    }
    return addMedia(paramMedia) >= 0;
  }
  
  protected abstract boolean filterMedia(Media paramMedia);
  
  public final MediaList getWrappedMediaList()
  {
    return this.m_MediaList;
  }
  
  public void release()
  {
    super.release();
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_ADDED, this.m_MediaAddedHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_MOVED, this.m_MediaMovedHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_MediaRemovingHandler);
    if (!this.m_OwnsMediaList) {
      return;
    }
    this.m_MediaList.release();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/FilteredMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */