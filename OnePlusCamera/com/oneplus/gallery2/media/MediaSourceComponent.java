package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;

public abstract class MediaSourceComponent<TSource extends MediaSource>
  extends BasicComponent
{
  private boolean m_IsMediaChangeCBEnabled;
  private final MediaChangeCallback m_MediaChangeCB = new MediaChangeCallback()
  {
    public void onMediaCreated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MediaSourceComponent.this.onMediaCreated(paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaDeleted(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MediaSourceComponent.this.onMediaDeleted(paramAnonymousMedia, paramAnonymousInt);
    }
    
    public void onMediaUpdated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MediaSourceComponent.this.onMediaUpdated(paramAnonymousMedia, paramAnonymousInt);
    }
  };
  private Handle m_MediaChangeCBHandle;
  private TSource m_MediaSource;
  private final Class<? extends TSource> m_MediaSourceClass;
  private final PropertyChangedCallback<Boolean> m_MediaTableStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
      MediaSourceComponent.this.onMediaTableReady();
    }
  };
  
  protected MediaSourceComponent(String paramString, ComponentOwner paramComponentOwner, Class<? extends TSource> paramClass)
  {
    super(paramString, paramComponentOwner, true);
    if (paramClass != null)
    {
      this.m_MediaSourceClass = paramClass;
      return;
    }
    throw new IllegalArgumentException("No type of media source");
  }
  
  protected void disableMediaChangeCallback()
  {
    this.m_MediaChangeCBHandle = Handle.close(this.m_MediaChangeCBHandle);
    this.m_IsMediaChangeCBEnabled = false;
  }
  
  protected void enableMediaChangeCallback()
  {
    if (this.m_MediaSource == null) {
      this.m_IsMediaChangeCBEnabled = true;
    }
    while (Handle.isValid(this.m_MediaChangeCBHandle)) {
      return;
    }
    this.m_MediaChangeCBHandle = this.m_MediaSource.addMediaChangedCallback(this.m_MediaChangeCB);
    this.m_IsMediaChangeCBEnabled = Handle.isValid(this.m_MediaChangeCBHandle);
  }
  
  public final TSource getMediaSource()
  {
    return this.m_MediaSource;
  }
  
  public final boolean isMediaSourceBound()
  {
    return this.m_MediaSource != null;
  }
  
  protected void onBindToMediaSource(TSource paramTSource)
  {
    if (((Boolean)paramTSource.get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      onMediaTableReady();
      return;
    }
    if (!this.m_IsMediaChangeCBEnabled) {}
    for (;;)
    {
      Log.v(this.TAG, "onBindToMediaSource() - Waiting for media table ready");
      paramTSource.addCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableStateChangedCB);
      return;
      enableMediaChangeCallback();
    }
  }
  
  protected void onDeinitialize()
  {
    if (this.m_MediaSource == null) {}
    for (;;)
    {
      super.onDeinitialize();
      return;
      onUnbindFromMediaSource(this.m_MediaSource);
      this.m_MediaSource = null;
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    ComponentUtils.findComponent(BaseApplication.current(), this.m_MediaSourceClass, this, new ComponentSearchCallback()
    {
      public void onComponentFound(TSource paramAnonymousTSource)
      {
        MediaSourceComponent.this.m_MediaSource = paramAnonymousTSource;
        MediaSourceComponent.this.onBindToMediaSource(paramAnonymousTSource);
      }
    });
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt) {}
  
  protected void onMediaDeleted(Media paramMedia, int paramInt) {}
  
  protected void onMediaTableReady()
  {
    Log.v(this.TAG, "onMediaTableReady()");
  }
  
  protected void onMediaUpdated(Media paramMedia, int paramInt) {}
  
  protected void onUnbindFromMediaSource(TSource paramTSource)
  {
    disableMediaChangeCallback();
    paramTSource.removeCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableStateChangedCB);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSourceComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */