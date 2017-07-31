package com.oneplus.gallery2.location;

import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.media.Media;
import com.oneplus.gallery2.media.MediaList;
import java.util.List;

public class AutoAddressClassifier
  extends BaseAddressClassifier
{
  private final AddressClassifier m_InternalClassifier;
  private boolean m_IsInternalClassifierUpdated;
  private final EventHandler<ListChangeEventArgs> m_MediaAddedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      AutoAddressClassifier.this.onMediaAdded(paramAnonymousListChangeEventArgs);
    }
  };
  private final MediaList m_MediaList;
  private final EventHandler<ListChangeEventArgs> m_MediaRemovingHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      AutoAddressClassifier.this.onMediaRemoving(paramAnonymousListChangeEventArgs);
    }
  };
  private int m_MediaUpdatingCounter;
  private final boolean m_OwnsInternalClassifier;
  private final EventHandler<EventArgs> m_UpdatedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      AutoAddressClassifier.this.onInternalClassifierUpdated();
    }
  };
  
  public AutoAddressClassifier(MediaList paramMediaList, AddressClassifier paramAddressClassifier, boolean paramBoolean)
  {
    this.m_MediaList = paramMediaList;
    this.m_InternalClassifier = paramAddressClassifier;
    this.m_OwnsInternalClassifier = paramBoolean;
    int i = paramMediaList.size() - 1;
    while (i >= 0)
    {
      paramAddressClassifier.addMedia((Media)paramMediaList.get(i), 0);
      i -= 1;
    }
    paramAddressClassifier.addHandler(EVENT_UPDATED, this.m_UpdatedHandler);
    paramMediaList.addHandler(MediaList.EVENT_MEDIA_ADDED, this.m_MediaAddedHandler);
    paramMediaList.addHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_MediaRemovingHandler);
  }
  
  private void onInternalClassifierUpdated()
  {
    if (this.m_MediaUpdatingCounter <= 0)
    {
      raise(EVENT_UPDATED, EventArgs.EMPTY);
      return;
    }
    this.m_IsInternalClassifierUpdated = true;
  }
  
  private void onMediaAdded(ListChangeEventArgs paramListChangeEventArgs)
  {
    this.m_MediaUpdatingCounter += 1;
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      this.m_InternalClassifier.addMedia((Media)this.m_MediaList.get(i), 0);
      i += 1;
    }
    this.m_MediaUpdatingCounter -= 1;
    if (this.m_MediaUpdatingCounter > 0) {}
    while (!this.m_IsInternalClassifierUpdated) {
      return;
    }
    onInternalClassifierUpdated();
  }
  
  private void onMediaRemoving(ListChangeEventArgs paramListChangeEventArgs)
  {
    this.m_MediaUpdatingCounter += 1;
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      this.m_InternalClassifier.removeMedia((Media)this.m_MediaList.get(i), 0);
      i += 1;
    }
    this.m_MediaUpdatingCounter -= 1;
    if (this.m_MediaUpdatingCounter > 0) {}
    while (!this.m_IsInternalClassifierUpdated) {
      return;
    }
    onInternalClassifierUpdated();
  }
  
  public boolean addMedia(Media paramMedia, int paramInt)
  {
    return false;
  }
  
  public List<String> getLocationNameList(AddressClassifier.LocationType paramLocationType, int paramInt)
  {
    return this.m_InternalClassifier.getLocationNameList(paramLocationType, paramInt);
  }
  
  protected void onRelease()
  {
    super.onRelease();
    this.m_InternalClassifier.removeHandler(EVENT_UPDATED, this.m_UpdatedHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_ADDED, this.m_MediaAddedHandler);
    this.m_MediaList.removeHandler(MediaList.EVENT_MEDIA_REMOVING, this.m_MediaRemovingHandler);
    if (!this.m_OwnsInternalClassifier) {
      return;
    }
    this.m_InternalClassifier.release();
  }
  
  public boolean removeMedia(Media paramMedia, int paramInt)
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/location/AutoAddressClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */