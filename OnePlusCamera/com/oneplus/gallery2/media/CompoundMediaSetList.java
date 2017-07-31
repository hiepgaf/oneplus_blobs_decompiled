package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundMediaSetList
  extends BaseMediaSetList
{
  private int m_HiddenMediaSetCount;
  private final PropertyChangedCallback<Integer> m_HiddenMediaSetCountChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      CompoundMediaSetList.this.updateHiddenMediaSetCount();
    }
  };
  private final List<MediaSetList> m_InternalLists = new ArrayList();
  private final EventHandler<ListChangeEventArgs> m_MediaSetAddedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      CompoundMediaSetList.this.onMediaSetAdded((MediaSetList)paramAnonymousEventSource, paramAnonymousListChangeEventArgs);
    }
  };
  private final EventHandler<EventArgs> m_MediaSetListResetHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      CompoundMediaSetList.this.onMediaSetListReset((MediaSetList)paramAnonymousEventSource);
    }
  };
  private final EventHandler<ListMoveEventArgs> m_MediaSetMovedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListMoveEventArgs> paramAnonymousEventKey, ListMoveEventArgs paramAnonymousListMoveEventArgs)
    {
      CompoundMediaSetList.this.onMediaSetMoved((MediaSetList)paramAnonymousEventSource, paramAnonymousListMoveEventArgs);
    }
  };
  private final EventHandler<ListChangeEventArgs> m_MediaSetRemovingHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ListChangeEventArgs> paramAnonymousEventKey, ListChangeEventArgs paramAnonymousListChangeEventArgs)
    {
      CompoundMediaSetList.this.onMediaSetRemoving((MediaSetList)paramAnonymousEventSource, paramAnonymousListChangeEventArgs);
    }
  };
  private final boolean m_OwnsInternalLists;
  private final PropertyChangedCallback<Boolean> m_ReadyStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      CompoundMediaSetList.this.onMediaSetListReadyStateChanged((MediaSetList)paramAnonymousPropertySource, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  
  public CompoundMediaSetList(MediaSetComparator paramMediaSetComparator, boolean paramBoolean, MediaSetList... paramVarArgs)
  {
    super(paramMediaSetComparator, true);
    this.m_OwnsInternalLists = paramBoolean;
    int i = paramVarArgs.length;
    for (;;)
    {
      int j = i - 1;
      if (j < 0) {
        break;
      }
      paramMediaSetComparator = paramVarArgs[j];
      i = j;
      if (this.m_InternalLists.add(paramMediaSetComparator))
      {
        attachToMediaSetList(paramMediaSetComparator);
        i = j;
      }
    }
    if (this.m_InternalLists.isEmpty()) {}
    for (;;)
    {
      checkReadyState();
      return;
      reset();
    }
  }
  
  private void attachToMediaSetList(MediaSetList paramMediaSetList)
  {
    paramMediaSetList.addCallback(MediaSetList.PROP_IS_READY, this.m_ReadyStateChangedCallback);
    paramMediaSetList.addHandler(MediaSetList.EVENT_MEDIA_SET_ADDED, this.m_MediaSetAddedHandler);
    paramMediaSetList.addHandler(MediaSetList.EVENT_MEDIA_SET_MOVED, this.m_MediaSetMovedHandler);
    paramMediaSetList.addHandler(MediaSetList.EVENT_MEDIA_SET_REMOVING, this.m_MediaSetRemovingHandler);
    paramMediaSetList.addHandler(MediaSetList.EVENT_RESET, this.m_MediaSetListResetHandler);
    paramMediaSetList.addCallback(MediaSetList.PROP_HIDDEN_MEDIA_SET_COUNT, this.m_HiddenMediaSetCountChangedCallback);
  }
  
  private void checkReadyState()
  {
    int i = this.m_InternalLists.size() - 1;
    for (;;)
    {
      if (i < 0) {
        break label68;
      }
      if (!((Boolean)((MediaSetList)this.m_InternalLists.get(i)).get(MediaSetList.PROP_IS_READY)).booleanValue()) {
        break;
      }
      i -= 1;
    }
    label68:
    for (boolean bool = false;; bool = true)
    {
      setReadOnly(PROP_IS_READY, Boolean.valueOf(bool));
      return;
    }
  }
  
  private void detachFromMediaSetList(MediaSetList paramMediaSetList)
  {
    paramMediaSetList.removeCallback(MediaSetList.PROP_IS_READY, this.m_ReadyStateChangedCallback);
    paramMediaSetList.removeHandler(MediaSetList.EVENT_MEDIA_SET_ADDED, this.m_MediaSetAddedHandler);
    paramMediaSetList.removeHandler(MediaSetList.EVENT_MEDIA_SET_MOVED, this.m_MediaSetMovedHandler);
    paramMediaSetList.removeHandler(MediaSetList.EVENT_MEDIA_SET_REMOVING, this.m_MediaSetRemovingHandler);
    paramMediaSetList.removeHandler(MediaSetList.EVENT_RESET, this.m_MediaSetListResetHandler);
    paramMediaSetList.removeCallback(MediaSetList.PROP_HIDDEN_MEDIA_SET_COUNT, this.m_HiddenMediaSetCountChangedCallback);
  }
  
  private void onMediaSetAdded(MediaSetList paramMediaSetList, ListChangeEventArgs paramListChangeEventArgs)
  {
    int i = paramListChangeEventArgs.getItemCount();
    ArrayList localArrayList;
    if (i > 4)
    {
      localArrayList = new ArrayList(i);
      i = paramListChangeEventArgs.getStartIndex();
      j = paramListChangeEventArgs.getEndIndex();
      while (i <= j)
      {
        localArrayList.add((MediaSet)paramMediaSetList.get(i));
        i += 1;
      }
    }
    i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      addMediaSet((MediaSet)paramMediaSetList.get(i), true);
      i += 1;
    }
    addMediaSets(localArrayList, true);
  }
  
  private void onMediaSetListReadyStateChanged(MediaSetList paramMediaSetList, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      setReadOnly(PROP_IS_READY, Boolean.valueOf(false));
      return;
    }
    checkReadyState();
  }
  
  private void onMediaSetListReset(MediaSetList paramMediaSetList)
  {
    reset();
  }
  
  private void onMediaSetMoved(MediaSetList paramMediaSetList, ListMoveEventArgs paramListMoveEventArgs)
  {
    int i = paramListMoveEventArgs.getStartIndex();
    int j = paramListMoveEventArgs.getEndIndex();
    while (i <= j)
    {
      checkMediaSetIndex((MediaSet)paramMediaSetList.get(i));
      i += 1;
    }
  }
  
  private void onMediaSetRemoving(MediaSetList paramMediaSetList, ListChangeEventArgs paramListChangeEventArgs)
  {
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      removeMediaSet((MediaSet)paramMediaSetList.get(i), true);
      i += 1;
    }
  }
  
  private void reset()
  {
    clearMediaSetLists(false);
    int i = this.m_InternalLists.size() - 1;
    while (i >= 0)
    {
      addMediaSets((MediaSetList)this.m_InternalLists.get(i), false);
      i -= 1;
    }
    raise(EVENT_RESET, EventArgs.EMPTY);
  }
  
  private void updateHiddenMediaSetCount()
  {
    int j = this.m_HiddenMediaSetCount;
    this.m_HiddenMediaSetCount = 0;
    int i = this.m_InternalLists.size() - 1;
    while (i >= 0)
    {
      int k = this.m_HiddenMediaSetCount;
      this.m_HiddenMediaSetCount = (((Integer)((MediaSetList)this.m_InternalLists.get(i)).get(MediaSetList.PROP_HIDDEN_MEDIA_SET_COUNT)).intValue() + k);
      i -= 1;
    }
    if (j == this.m_HiddenMediaSetCount) {
      return;
    }
    notifyPropertyChanged(PROP_HIDDEN_MEDIA_SET_COUNT, Integer.valueOf(j), Integer.valueOf(this.m_HiddenMediaSetCount));
  }
  
  public boolean addMediaSetList(MediaSetList paramMediaSetList)
  {
    verifyAccess();
    if (paramMediaSetList != null)
    {
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break label90;
      }
      this.m_InternalLists.add(paramMediaSetList);
      attachToMediaSetList(paramMediaSetList);
      addMediaSets(paramMediaSetList, true);
      if (((Boolean)get(PROP_IS_READY)).booleanValue()) {
        break label92;
      }
    }
    for (;;)
    {
      paramMediaSetList.set(PROP_SHOW_HIDDEN_MEDIA_SETS, (Boolean)get(PROP_SHOW_HIDDEN_MEDIA_SETS));
      updateHiddenMediaSetCount();
      return true;
      return false;
      label90:
      return false;
      label92:
      if (!((Boolean)paramMediaSetList.get(MediaSetList.PROP_IS_READY)).booleanValue()) {
        setReadOnly(PROP_IS_READY, Boolean.valueOf(false));
      }
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_HIDDEN_MEDIA_SET_COUNT) {
      return (TValue)super.get(paramPropertyKey);
    }
    return Integer.valueOf(this.m_HiddenMediaSetCount);
  }
  
  public void release()
  {
    int i = this.m_InternalLists.size() - 1;
    if (i >= 0)
    {
      MediaSetList localMediaSetList = (MediaSetList)this.m_InternalLists.get(i);
      detachFromMediaSetList(localMediaSetList);
      if (!this.m_OwnsInternalLists) {}
      for (;;)
      {
        i -= 1;
        break;
        localMediaSetList.release();
      }
    }
    this.m_InternalLists.clear();
    super.release();
  }
  
  public boolean removeMediaSetList(MediaSetList paramMediaSetList)
  {
    verifyAccess();
    if (this.m_InternalLists.remove(paramMediaSetList))
    {
      detachFromMediaSetList(paramMediaSetList);
      paramMediaSetList = paramMediaSetList.iterator();
      while (paramMediaSetList.hasNext()) {
        removeMediaSet((MediaSet)paramMediaSetList.next(), true);
      }
    }
    return false;
    if (((Boolean)get(PROP_IS_READY)).booleanValue()) {}
    for (;;)
    {
      updateHiddenMediaSetCount();
      return true;
      checkReadyState();
    }
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_SHOW_HIDDEN_MEDIA_SETS) {
      return super.set(paramPropertyKey, paramTValue);
    }
    if (super.set(paramPropertyKey, paramTValue))
    {
      boolean bool = ((Boolean)get(PROP_SHOW_HIDDEN_MEDIA_SETS)).booleanValue();
      paramPropertyKey = this.m_InternalLists.iterator();
      while (paramPropertyKey.hasNext()) {
        ((MediaSetList)paramPropertyKey.next()).set(PROP_SHOW_HIDDEN_MEDIA_SETS, Boolean.valueOf(bool));
      }
    }
    return false;
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/CompoundMediaSetList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */