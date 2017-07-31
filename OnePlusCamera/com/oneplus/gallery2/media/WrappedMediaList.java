package com.oneplus.gallery2.media;

import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;

public abstract class WrappedMediaList
  extends BaseMediaList
{
  private final EventHandler<ListChangeEventArgs> m_MediaAddedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      WrappedMediaList.this.onMediaAddedToInternalMediaList(paramAnonymousListChangeEventArgs);
    }
  };
  private final MediaList m_MediaList;
  private final EventHandler<ListMoveEventArgs> m_MediaMovedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListMoveEventArgs> paramAnonymousEventKey, ListMoveEventArgs paramAnonymousListMoveEventArgs)
    {
      WrappedMediaList.this.onMediaMovedInInternalMediaList(paramAnonymousListMoveEventArgs);
    }
  };
  private final EventHandler<ListChangeEventArgs> m_MediaRemovingHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      WrappedMediaList.this.onMediaRemovingFromInternalMediaList(paramAnonymousListChangeEventArgs);
    }
  };
  private final boolean m_OwnsMediaList;
  
  public WrappedMediaList(MediaList paramMediaList, MediaComparator paramMediaComparator, int paramInt, boolean paramBoolean)
  {
    super(paramMediaComparator, paramInt);
    this.m_OwnsMediaList = paramBoolean;
    this.m_MediaList = paramMediaList;
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_ADDED, this.m_MediaAddedHandler);
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_MOVED, this.m_MediaMovedHandler);
    this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_MediaRemovingHandler);
  }
  
  protected final MediaList getInternalMediaList()
  {
    return this.m_MediaList;
  }
  
  protected void onMediaAddedToInternalMediaList(ListChangeEventArgs paramListChangeEventArgs) {}
  
  protected void onMediaMovedInInternalMediaList(ListMoveEventArgs paramListMoveEventArgs)
  {
    int i = paramListMoveEventArgs.getStartIndex();
    int j = paramListMoveEventArgs.getEndIndex();
    while (i <= j)
    {
      checkMediaIndex((Media)this.m_MediaList.get(i));
      i += 1;
    }
  }
  
  protected void onMediaRemovingFromInternalMediaList(ListChangeEventArgs paramListChangeEventArgs)
  {
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      removeMedia((Media)this.m_MediaList.get(i));
      i += 1;
    }
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/WrappedMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */