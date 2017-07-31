package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.ListHandlerBaseObject;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;

public class MirroredMediaList
  extends ListHandlerBaseObject<Media>
  implements MediaList
{
  private final EventHandler m_EventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      MirroredMediaList.this.raise(paramAnonymousEventKey, paramAnonymousEventArgs);
    }
  };
  private final PropertyChangedCallback<Boolean> m_IsReleasedChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      MirroredMediaList.this.release();
    }
  };
  private final MediaList m_MediaList;
  
  public MirroredMediaList(MediaList paramMediaList)
  {
    if (!((Boolean)paramMediaList.get(MediaList.PROP_IS_RELEASED)).booleanValue())
    {
      this.m_MediaList = paramMediaList;
      this.m_MediaList.addCallback(MediaList.PROP_IS_RELEASED, this.m_IsReleasedChangedCB);
      this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_ADDED, this.m_EventHandler);
      this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_MOVED, this.m_EventHandler);
      this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_MOVING, this.m_EventHandler);
      this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_REMOVED, this.m_EventHandler);
      this.m_MediaList.addHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_EventHandler);
      return;
    }
    throw new IllegalArgumentException("Source media list has been released");
  }
  
  public Media get(int paramInt)
  {
    return (Media)this.m_MediaList.get(paramInt);
  }
  
  public void release()
  {
    this.m_MediaList.removeCallback(MediaList.PROP_IS_RELEASED, this.m_IsReleasedChangedCB);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_ADDED, this.m_EventHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_MOVED, this.m_EventHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_MOVING, this.m_EventHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_REMOVED, this.m_EventHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_EventHandler);
    super.release();
  }
  
  public int size()
  {
    return this.m_MediaList.size();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MirroredMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */