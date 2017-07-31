package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentEventArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MediaSources
{
  private static final String TAG = "MediaSources";
  private static final List<ActivationHandle> m_ActivationHandles = new ArrayList();
  private static final EventHandler<ComponentEventArgs<Component>> m_ComponentReadyHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
    {
      if (!(paramAnonymousComponentEventArgs.getComponent() instanceof MediaSource)) {
        return;
      }
      MediaSources.onMediaSourceReady((MediaSource)paramAnonymousComponentEventArgs.getComponent());
    }
  };
  private static final EventHandler<ComponentEventArgs<Component>> m_ComponentRemovedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
    {
      if (!(paramAnonymousComponentEventArgs.getComponent() instanceof MediaSource)) {
        return;
      }
      MediaSources.onMediaSourceRemoved((MediaSource)paramAnonymousComponentEventArgs.getComponent());
    }
  };
  
  public static Handle activate(int paramInt)
  {
    int i = 0;
    BaseApplication localBaseApplication = BaseApplication.current();
    ActivationHandle localActivationHandle;
    MediaSource localMediaSource;
    Handle localHandle;
    if (localBaseApplication.isDependencyThread())
    {
      localActivationHandle = new ActivationHandle(paramInt);
      m_ActivationHandles.add(localActivationHandle);
      Log.v("MediaSources", "activate() - Hancle count : ", Integer.valueOf(m_ActivationHandles.size()));
      MediaSource[] arrayOfMediaSource = (MediaSource[])localBaseApplication.findComponents(MediaSource.class);
      int j = arrayOfMediaSource.length;
      if (i >= j) {
        break label131;
      }
      localMediaSource = arrayOfMediaSource[i];
      localHandle = localMediaSource.activate(paramInt);
      if (Handle.isValid(localHandle)) {
        break label113;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      throw new RuntimeException("Access outside main thread");
      label113:
      localActivationHandle.subActivationHandles.put(localMediaSource, localHandle);
    }
    label131:
    if (m_ActivationHandles.size() != 1) {
      return localActivationHandle;
    }
    localBaseApplication.addHandler(BaseApplication.EVENT_COMPONENT_ADDED, m_ComponentReadyHandler);
    localBaseApplication.addHandler(BaseApplication.EVENT_COMPONENT_REMOVED, m_ComponentRemovedHandler);
    return localActivationHandle;
  }
  
  private static void deactivate(ActivationHandle paramActivationHandle)
  {
    BaseApplication localBaseApplication = BaseApplication.current();
    if (localBaseApplication.isDependencyThread())
    {
      if (m_ActivationHandles.remove(paramActivationHandle))
      {
        paramActivationHandle = paramActivationHandle.subActivationHandles.values().iterator();
        while (paramActivationHandle.hasNext()) {
          Handle.close((Handle)paramActivationHandle.next());
        }
      }
    }
    else {
      throw new RuntimeException("Access outside main thread");
    }
    return;
    Log.v("MediaSources", "deactivate() - Hancle count : ", Integer.valueOf(m_ActivationHandles.size()));
    if (!m_ActivationHandles.isEmpty()) {
      return;
    }
    localBaseApplication.removeHandler(BaseApplication.EVENT_COMPONENT_ADDED, m_ComponentReadyHandler);
    localBaseApplication.removeHandler(BaseApplication.EVENT_COMPONENT_REMOVED, m_ComponentRemovedHandler);
  }
  
  private static void onMediaSourceReady(MediaSource paramMediaSource)
  {
    int i = m_ActivationHandles.size() - 1;
    if (i >= 0)
    {
      ActivationHandle localActivationHandle = (ActivationHandle)m_ActivationHandles.get(i);
      Handle localHandle = paramMediaSource.activate(localActivationHandle.flags);
      if (!Handle.isValid(localHandle)) {}
      for (;;)
      {
        i -= 1;
        break;
        localActivationHandle.subActivationHandles.put(paramMediaSource, localHandle);
      }
    }
  }
  
  private static void onMediaSourceRemoved(MediaSource paramMediaSource)
  {
    int i = m_ActivationHandles.size() - 1;
    while (i >= 0)
    {
      ((ActivationHandle)m_ActivationHandles.get(i)).subActivationHandles.remove(paramMediaSource);
      i -= 1;
    }
  }
  
  private static final class ActivationHandle
    extends Handle
  {
    public final int flags;
    public final Map<MediaSource, Handle> subActivationHandles = new HashMap();
    
    public ActivationHandle(int paramInt)
    {
      super();
      this.flags = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      MediaSources.deactivate(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */