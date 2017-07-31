package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentEventArgs;
import com.oneplus.base.component.ComponentOwner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MultiMediaSourcesComponent
  extends BasicComponent
{
  private EventHandler<ComponentEventArgs<Component>> m_ComponentAddedHandler;
  private EventHandler<ComponentEventArgs<Component>> m_ComponentRemovedHandler;
  private final MediaChangeCallback m_MediaChangedCB = new MediaChangeCallback()
  {
    public void onMediaCreated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiMediaSourcesComponent.this.onMediaCreated(paramAnonymousMediaSource, paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaDeleted(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiMediaSourcesComponent.this.onMediaDeleted(paramAnonymousMediaSource, paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaUpdated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MultiMediaSourcesComponent.this.onMediaUpdated(paramAnonymousMediaSource, paramAnonymousMedia, paramAnonymousInt);
    }
  };
  private final Map<MediaSource, Handle> m_MediaChangedCBHandles = new HashMap();
  private final List<MediaSource> m_MediaSources = new ArrayList();
  private final PropertyChangedCallback<Boolean> m_MediaTableInitChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
      MultiMediaSourcesComponent.this.onMediaTableReady((MediaSource)paramAnonymousPropertySource);
    }
  };
  
  protected MultiMediaSourcesComponent(String paramString, ComponentOwner paramComponentOwner)
  {
    super(paramString, paramComponentOwner, true);
  }
  
  protected boolean addCallbacksBeforeMediaTableReady()
  {
    return false;
  }
  
  protected void onDeinitialize()
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
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    int i = 0;
    super.onInitialize();
    BaseApplication localBaseApplication = BaseApplication.current();
    MediaSource[] arrayOfMediaSource = (MediaSource[])localBaseApplication.findComponents(MediaSource.class);
    int j = arrayOfMediaSource.length;
    if (i < j)
    {
      MediaSource localMediaSource = arrayOfMediaSource[i];
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
        MultiMediaSourcesComponent.this.onMediaSourceReady((MediaSource)paramAnonymousComponentEventArgs.getComponent());
      }
    };
    this.m_ComponentRemovedHandler = new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        if (!(paramAnonymousComponentEventArgs.getComponent() instanceof MediaSource)) {
          return;
        }
        MultiMediaSourcesComponent.this.onMediaSourceRemoved((MediaSource)paramAnonymousComponentEventArgs.getComponent());
      }
    };
    localBaseApplication.addHandler(BaseApplication.EVENT_COMPONENT_ADDED, this.m_ComponentAddedHandler);
    localBaseApplication.addHandler(BaseApplication.EVENT_COMPONENT_REMOVED, this.m_ComponentRemovedHandler);
  }
  
  protected void onMediaCreated(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  protected void onMediaDeleted(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  protected void onMediaSourceReady(MediaSource paramMediaSource)
  {
    this.m_MediaSources.add(paramMediaSource);
    if (!addCallbacksBeforeMediaTableReady()) {}
    while (((Boolean)paramMediaSource.get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      onMediaTableReady(paramMediaSource);
      return;
      if (!Handle.isValid((Handle)this.m_MediaChangedCBHandles.get(paramMediaSource))) {
        this.m_MediaChangedCBHandles.put(paramMediaSource, paramMediaSource.addMediaChangedCallback(this.m_MediaChangedCB));
      }
    }
    paramMediaSource.addCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableInitChangedCB);
  }
  
  protected void onMediaSourceRemoved(MediaSource paramMediaSource)
  {
    if (this.m_MediaSources.remove(paramMediaSource))
    {
      paramMediaSource.removeCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableInitChangedCB);
      Handle.close((Handle)this.m_MediaChangedCBHandles.remove(paramMediaSource));
      return;
    }
  }
  
  protected void onMediaTableReady(MediaSource paramMediaSource)
  {
    if (Handle.isValid((Handle)this.m_MediaChangedCBHandles.get(paramMediaSource))) {
      return;
    }
    this.m_MediaChangedCBHandles.put(paramMediaSource, paramMediaSource.addMediaChangedCallback(this.m_MediaChangedCB));
  }
  
  protected void onMediaUpdated(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MultiMediaSourcesComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */