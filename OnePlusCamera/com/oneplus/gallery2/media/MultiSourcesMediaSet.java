package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentEventArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class MultiSourcesMediaSet
  extends BaseMediaSet
{
  private boolean m_AllMediaTablesReady = false;
  private EventHandler<ComponentEventArgs<Component>> m_ComponentAddedHandler;
  private EventHandler<ComponentEventArgs<Component>> m_ComponentRemovedHandler;
  private final MediaChangeCallback m_MediaChangedCB = new MediaChangeCallback()
  {
    public void onMediaCreated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiSourcesMediaSet.this.onMediaCreated(paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaDeleted(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiSourcesMediaSet.this.onMediaDeleted(paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaUpdated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiSourcesMediaSet.this.onMediaUpdated(paramAnonymousMedia, paramAnonymousInt);
    }
  };
  private final Map<MediaSource, Handle> m_MediaChangedCBHandles = new HashMap();
  private final MediaIterationClient m_MediaIterationClient = new MediaIterationClient()
  {
    public void onIterate(Media paramAnonymousMedia)
    {
      MultiSourcesMediaSet.this.onIterateMedia(paramAnonymousMedia);
    }
    
    public void onIterationEnded()
    {
      MultiSourcesMediaSet.this.onMediaIterationEnded();
    }
    
    public void onIterationStarted()
    {
      MultiSourcesMediaSet.this.onMediaIterationStarted();
    }
  };
  private final Map<MediaSource, Handle> m_MediaIterationClientHandles = new HashMap();
  private final List<MediaSource> m_MediaSources = new ArrayList();
  private final PropertyChangedCallback<Boolean> m_MediaTableInitChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
      MultiSourcesMediaSet.this.onMediaTableReady((MediaSource)paramAnonymousPropertySource);
    }
  };
  
  protected MultiSourcesMediaSet(Collection<MediaSource> paramCollection, MediaType paramMediaType)
  {
    super((MediaSource)BaseApplication.current().findComponent(TempMediaSource.class), paramMediaType);
    if (paramCollection != null)
    {
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext()) {
        onMediaSourceReady((MediaSource)paramCollection.next());
      }
    }
    paramCollection = BaseApplication.current();
    paramMediaType = (MediaSource[])paramCollection.findComponents(MediaSource.class);
    int j = paramMediaType.length;
    if (i < j)
    {
      MediaSource localMediaSource = paramMediaType[i];
      if ((localMediaSource instanceof TempMediaSource)) {}
      for (;;)
      {
        i += 1;
        break;
        onMediaSourceReady(localMediaSource);
      }
    }
    this.m_ComponentAddedHandler = new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        if (!(paramAnonymousComponentEventArgs.getComponent() instanceof MediaSource)) {
          return;
        }
        MultiSourcesMediaSet.this.onMediaSourceReady((MediaSource)paramAnonymousComponentEventArgs.getComponent());
      }
    };
    this.m_ComponentRemovedHandler = new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        if (!(paramAnonymousComponentEventArgs.getComponent() instanceof MediaSource)) {
          return;
        }
        MultiSourcesMediaSet.this.onMediaSourceRemoved((MediaSource)paramAnonymousComponentEventArgs.getComponent());
      }
    };
    paramCollection.addHandler(BaseApplication.EVENT_COMPONENT_ADDED, this.m_ComponentAddedHandler);
    paramCollection.addHandler(BaseApplication.EVENT_COMPONENT_REMOVED, this.m_ComponentRemovedHandler);
  }
  
  private void setupMedia(MediaSource paramMediaSource)
  {
    if (this.m_MediaChangedCBHandles.containsKey(paramMediaSource)) {
      if (!this.m_MediaIterationClientHandles.containsKey(paramMediaSource)) {
        break label59;
      }
    }
    for (;;)
    {
      paramMediaSource.scheduleMediaIteration(0);
      return;
      this.m_MediaChangedCBHandles.put(paramMediaSource, paramMediaSource.addMediaChangedCallback(this.m_MediaChangedCB));
      break;
      label59:
      this.m_MediaIterationClientHandles.put(paramMediaSource, paramMediaSource.addMediaIterationClient(this.m_MediaIterationClient, getTargetMediaType()));
    }
  }
  
  protected boolean areAllMediaTablesReady()
  {
    return this.m_AllMediaTablesReady;
  }
  
  protected void onAllMediaTablesReady()
  {
    Log.v(this.TAG, "onAllMediaTablesReady()");
  }
  
  protected void onMediaSourceReady(MediaSource paramMediaSource)
  {
    this.m_MediaSources.add(paramMediaSource);
    if (((Boolean)paramMediaSource.get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {
      onMediaTableReady(paramMediaSource);
    }
    while (!canSyncMediaBeforeMediaTableReady())
    {
      return;
      this.m_AllMediaTablesReady = false;
      paramMediaSource.addCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableInitChangedCB);
    }
    setupMedia(paramMediaSource);
  }
  
  protected void onMediaSourceRemoved(MediaSource paramMediaSource)
  {
    if (this.m_MediaSources.remove(paramMediaSource))
    {
      paramMediaSource.removeCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableInitChangedCB);
      Handle.close((Handle)this.m_MediaChangedCBHandles.remove(paramMediaSource));
      Handle.close((Handle)this.m_MediaIterationClientHandles.remove(paramMediaSource));
      boolean bool = this.m_AllMediaTablesReady;
      this.m_AllMediaTablesReady = true;
      paramMediaSource = this.m_MediaSources.iterator();
      while (paramMediaSource.hasNext()) {
        if (!((Boolean)((MediaSource)paramMediaSource.next()).get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {
          this.m_AllMediaTablesReady = false;
        }
      }
      if (!bool) {
        break label126;
      }
    }
    label126:
    while ((!this.m_AllMediaTablesReady) || (this.m_MediaSources.isEmpty()))
    {
      return;
      return;
    }
    onAllMediaTablesReady();
  }
  
  protected void onMediaTableReady(MediaSource paramMediaSource)
  {
    if (canSyncMediaBeforeMediaTableReady()) {}
    for (;;)
    {
      this.m_AllMediaTablesReady = true;
      paramMediaSource = this.m_MediaSources.iterator();
      while (paramMediaSource.hasNext()) {
        if (!((Boolean)((MediaSource)paramMediaSource.next()).get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {
          this.m_AllMediaTablesReady = false;
        }
      }
      if (this.m_AllMediaTablesReady) {
        break;
      }
      return;
      setupMedia(paramMediaSource);
    }
    onAllMediaTablesReady();
  }
  
  protected final Handle onPrepareMediaChangeCallback()
  {
    return null;
  }
  
  protected final Handle onPrepareMediaIterationClient()
  {
    return null;
  }
  
  protected void onRelease()
  {
    Object localObject = BaseApplication.current();
    if (this.m_ComponentAddedHandler == null) {
      if (this.m_ComponentRemovedHandler != null) {
        break label78;
      }
    }
    for (;;)
    {
      localObject = (MediaSource[])this.m_MediaSources.toArray(new MediaSource[this.m_MediaSources.size()]);
      int i = localObject.length;
      for (;;)
      {
        i -= 1;
        if (i < 0) {
          break;
        }
        onMediaSourceRemoved(localObject[i]);
      }
      ((BaseApplication)localObject).removeHandler(BaseApplication.EVENT_COMPONENT_ADDED, this.m_ComponentAddedHandler);
      break;
      label78:
      ((BaseApplication)localObject).removeHandler(BaseApplication.EVENT_COMPONENT_REMOVED, this.m_ComponentRemovedHandler);
    }
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MultiSourcesMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */