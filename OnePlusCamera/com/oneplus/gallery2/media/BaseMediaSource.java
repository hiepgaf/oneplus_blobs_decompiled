package com.oneplus.gallery2.media;

import android.os.Handler;
import android.os.Message;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.BasicComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseMediaSource
  extends BasicComponent
  implements MediaSource
{
  private static final int MSG_ITERATE_MEDIA = -10000;
  private final List<ActivationHandle> m_ActivationHandles = new ArrayList();
  private final List<MediaChangeCallbackHandle> m_MediaChangeCBHandles = new ArrayList();
  private final List<MediaIterationClientHandle> m_MediaIterationClientHandles = new ArrayList();
  private final Map<String, Media> m_MediaTable = new HashMap();
  private final Set<Integer> m_PendingMediaIterationFlags = new HashSet();
  
  protected BaseMediaSource(String paramString, BaseApplication paramBaseApplication)
  {
    super(paramString, paramBaseApplication, true);
    enablePropertyLogs(PROP_IS_ACTIVE, 1);
    enablePropertyLogs(PROP_IS_MEDIA_TABLE_READY, 1);
  }
  
  private void deactivate(ActivationHandle paramActivationHandle)
  {
    verifyAccess();
    if (this.m_ActivationHandles.remove(paramActivationHandle))
    {
      Log.v(this.TAG, "deactivate() - Handle count : ", Integer.valueOf(this.m_ActivationHandles.size()));
      onActivationHandleClosed(paramActivationHandle);
      if (this.m_ActivationHandles.isEmpty()) {}
    }
    else
    {
      return;
    }
    onDeactivated();
    notifyPropertyChanged(PROP_IS_ACTIVE, Boolean.valueOf(true), Boolean.valueOf(false));
  }
  
  private void iterateMedia(int paramInt)
  {
    int j = this.m_MediaIterationClientHandles.size();
    MediaIterationClientHandle[] arrayOfMediaIterationClientHandle;
    if (j != 0)
    {
      arrayOfMediaIterationClientHandle = (MediaIterationClientHandle[])this.m_MediaIterationClientHandles.toArray(new MediaIterationClientHandle[j]);
      int i = j - 1;
      while (i >= 0)
      {
        arrayOfMediaIterationClientHandle[i].client.onIterationStarted();
        i -= 1;
      }
    }
    return;
    Iterator localIterator;
    Media localMedia;
    label116:
    MediaIterationClientHandle localMediaIterationClientHandle;
    if ((FLAG_EXPAND_GROUPED_MEDIA & paramInt) != 0)
    {
      localIterator = this.m_MediaTable.values().iterator();
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localMedia = (Media)localIterator.next();
      } while ((localMedia instanceof GroupedMedia));
      paramInt = j - 1;
      if (paramInt >= 0)
      {
        localMediaIterationClientHandle = arrayOfMediaIterationClientHandle[paramInt];
        if (localMediaIterationClientHandle.targetMediaType != null) {
          break label255;
        }
        label134:
        localMediaIterationClientHandle.client.onIterate(localMedia);
      }
    }
    for (;;)
    {
      paramInt -= 1;
      break label116;
      break;
      localIterator = this.m_MediaTable.values().iterator();
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localMedia = (Media)localIterator.next();
      } while (isSubMedia(localMedia));
      paramInt = j - 1;
      label202:
      if (paramInt >= 0)
      {
        localMediaIterationClientHandle = arrayOfMediaIterationClientHandle[paramInt];
        if (localMediaIterationClientHandle.targetMediaType != null) {
          break label237;
        }
        label220:
        localMediaIterationClientHandle.client.onIterate(localMedia);
      }
      for (;;)
      {
        paramInt -= 1;
        break label202;
        break;
        label237:
        if (localMedia.getType() == localMediaIterationClientHandle.targetMediaType) {
          break label220;
        }
      }
      label255:
      if (localMedia.getType() == localMediaIterationClientHandle.targetMediaType) {
        break label134;
      }
    }
    paramInt = j - 1;
    while (paramInt >= 0)
    {
      arrayOfMediaIterationClientHandle[paramInt].client.onIterationEnded();
      paramInt -= 1;
    }
  }
  
  private void removeMediaChangedCallback(MediaChangeCallbackHandle paramMediaChangeCallbackHandle)
  {
    verifyAccess();
    this.m_MediaChangeCBHandles.remove(paramMediaChangeCallbackHandle);
  }
  
  private void removeMediaIterationClient(MediaIterationClientHandle paramMediaIterationClientHandle)
  {
    verifyAccess();
    this.m_MediaIterationClientHandles.remove(paramMediaIterationClientHandle);
  }
  
  public Handle activate(int paramInt)
  {
    verifyAccess();
    ActivationHandle localActivationHandle;
    if (isRunningOrInitializing(true))
    {
      localActivationHandle = new ActivationHandle(paramInt);
      this.m_ActivationHandles.add(localActivationHandle);
      if (onActivationHandleCreated(localActivationHandle))
      {
        Log.v(this.TAG, "activate() - Handle count : ", Integer.valueOf(this.m_ActivationHandles.size()));
        if (this.m_ActivationHandles.size() == 1) {
          break label92;
        }
        return localActivationHandle;
      }
    }
    else
    {
      return null;
    }
    this.m_ActivationHandles.remove(localActivationHandle);
    return null;
    label92:
    onActivated();
    notifyPropertyChanged(PROP_IS_ACTIVE, Boolean.valueOf(false), Boolean.valueOf(true));
    return localActivationHandle;
  }
  
  protected final boolean addMedia(Media paramMedia, boolean paramBoolean, int paramInt)
  {
    verifyAccess();
    Media localMedia;
    if (paramMedia != null)
    {
      localMedia = (Media)this.m_MediaTable.put(paramMedia.getId(), paramMedia);
      if (localMedia == null)
      {
        if (paramBoolean) {
          break label107;
        }
        return true;
      }
    }
    else
    {
      Log.e(this.TAG, "addMedia() - No media to add");
      return false;
    }
    if (localMedia == paramMedia) {
      return true;
    }
    Log.w(this.TAG, "addMedia() - Duplicate media : " + paramMedia.getId());
    this.m_MediaTable.put(paramMedia.getId(), localMedia);
    return false;
    label107:
    notifyMediaCreated(paramMedia, paramInt);
    return true;
  }
  
  public Handle addMediaChangedCallback(MediaChangeCallback paramMediaChangeCallback)
  {
    verifyAccess();
    if (paramMediaChangeCallback != null)
    {
      paramMediaChangeCallback = new MediaChangeCallbackHandle(paramMediaChangeCallback);
      this.m_MediaChangeCBHandles.add(paramMediaChangeCallback);
      return paramMediaChangeCallback;
    }
    Log.e(this.TAG, "addMediaChangedCallback() - No call-back to add");
    return null;
  }
  
  public Handle addMediaIterationClient(MediaIterationClient paramMediaIterationClient, MediaType paramMediaType)
  {
    verifyAccess();
    if (paramMediaIterationClient != null)
    {
      paramMediaIterationClient = new MediaIterationClientHandle(paramMediaIterationClient, paramMediaType);
      this.m_MediaIterationClientHandles.add(paramMediaIterationClient);
      return paramMediaIterationClient;
    }
    Log.e(this.TAG, "addMediaChangedCallback() - No client to add");
    return null;
  }
  
  protected final boolean containsMedia(Media paramMedia)
  {
    if (paramMedia != null)
    {
      if (this.m_MediaTable.get(paramMedia.getId()) != paramMedia) {
        return false;
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    boolean bool = false;
    if (paramPropertyKey != PROP_IS_ACTIVE) {
      return (TValue)super.get(paramPropertyKey);
    }
    if (!this.m_ActivationHandles.isEmpty()) {
      bool = true;
    }
    return Boolean.valueOf(bool);
  }
  
  protected final int getActivationHandleCount()
  {
    return this.m_ActivationHandles.size();
  }
  
  public <T extends Media> T getMedia(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    return (Media)this.m_MediaTable.get(paramString);
  }
  
  public Iterable<Media> getMedia(MediaType paramMediaType, int paramInt)
  {
    verifyAccess();
    if ((FLAG_RECYCLED_MEDIA_ONLY & paramInt) == 0)
    {
      if ((FLAG_EXPAND_GROUPED_MEDIA & paramInt) != 0) {
        return new ExpandedMediaIterable(paramMediaType, this.m_MediaTable.values());
      }
    }
    else {
      return getRecycledMedia(paramMediaType, paramInt);
    }
    return new NormalMediaIterable(paramMediaType, this.m_MediaTable.values());
  }
  
  protected final Collection<Media> getMedia()
  {
    return this.m_MediaTable.values();
  }
  
  protected abstract Iterable<Media> getRecycledMedia(MediaType paramMediaType, int paramInt);
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    paramMessage = (Integer[])this.m_PendingMediaIterationFlags.toArray(new Integer[this.m_PendingMediaIterationFlags.size()]);
    this.m_PendingMediaIterationFlags.clear();
    int i = paramMessage.length;
    for (;;)
    {
      i -= 1;
      if (i < 0) {
        break;
      }
      iterateMedia(paramMessage[i].intValue());
    }
  }
  
  protected void notifyMediaCreated(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCBHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCBHandles.get(i)).callback.onMediaCreated(this, paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected void notifyMediaDeleted(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCBHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCBHandles.get(i)).callback.onMediaDeleted(this, paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected void notifyMediaUpdated(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCBHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCBHandles.get(i)).callback.onMediaUpdated(this, paramMedia, paramInt);
      i -= 1;
    }
  }
  
  final void notifyMediaUpdatedByItself(Media paramMedia, int paramInt)
  {
    if (paramInt != 0)
    {
      if (containsMedia(paramMedia)) {
        notifyMediaUpdated(paramMedia, prepareMediaFlagsForCallback(paramMedia) | paramInt);
      }
    }
    else {}
  }
  
  protected void onActivated() {}
  
  protected void onActivationHandleClosed(ActivationHandle paramActivationHandle) {}
  
  protected boolean onActivationHandleCreated(ActivationHandle paramActivationHandle)
  {
    return true;
  }
  
  protected void onDeactivated() {}
  
  protected void onDeinitialize()
  {
    this.m_MediaTable.clear();
    this.m_ActivationHandles.clear();
    super.onDeinitialize();
  }
  
  protected int prepareMediaFlagsForCallback(Media paramMedia)
  {
    if (!isSubMedia(paramMedia)) {
      return 0;
    }
    return Media.FLAG_SUB_MEDIA | 0x0;
  }
  
  protected final boolean removeMedia(Media paramMedia, boolean paramBoolean, int paramInt)
  {
    verifyAccess();
    Media localMedia;
    if (paramMedia != null)
    {
      localMedia = (Media)this.m_MediaTable.remove(paramMedia.getId());
      if (localMedia == null) {
        break label47;
      }
      if (localMedia != paramMedia) {
        break label49;
      }
      if (paramBoolean) {
        break label69;
      }
    }
    for (;;)
    {
      return true;
      return false;
      label47:
      return false;
      label49:
      this.m_MediaTable.put(paramMedia.getId(), localMedia);
      return false;
      label69:
      notifyMediaDeleted(paramMedia, paramInt);
    }
  }
  
  public boolean scheduleMediaIteration(int paramInt)
  {
    verifyAccess();
    if (isRunningOrInitializing(true))
    {
      boolean bool = this.m_PendingMediaIterationFlags.isEmpty();
      this.m_PendingMediaIterationFlags.add(Integer.valueOf(paramInt));
      if (!bool) {
        return true;
      }
    }
    else
    {
      return false;
    }
    getHandler().sendEmptyMessage(55536);
    return true;
  }
  
  protected class ActivationHandle
    extends Handle
  {
    private final int m_Flags;
    
    public ActivationHandle(int paramInt)
    {
      super();
      this.m_Flags = paramInt;
    }
    
    public final int getFlags()
    {
      return this.m_Flags;
    }
    
    protected void onClose(int paramInt)
    {
      BaseMediaSource.this.deactivate(this);
    }
  }
  
  protected class ExpandedMediaIterable
    extends MediaIterable
  {
    public ExpandedMediaIterable(Iterable<Media> paramIterable)
    {
      super(localIterable);
    }
    
    protected boolean filterMedia(Media paramMedia)
    {
      if (!super.filterMedia(paramMedia)) {}
      while ((paramMedia instanceof GroupedMedia)) {
        return false;
      }
      return true;
    }
  }
  
  private final class MediaChangeCallbackHandle
    extends Handle
  {
    public final MediaChangeCallback callback;
    
    public MediaChangeCallbackHandle(MediaChangeCallback paramMediaChangeCallback)
    {
      super();
      this.callback = paramMediaChangeCallback;
    }
    
    protected void onClose(int paramInt)
    {
      BaseMediaSource.this.removeMediaChangedCallback(this);
    }
  }
  
  private final class MediaIterationClientHandle
    extends Handle
  {
    public final MediaIterationClient client;
    public final MediaType targetMediaType;
    
    public MediaIterationClientHandle(MediaIterationClient paramMediaIterationClient, MediaType paramMediaType)
    {
      super();
      this.client = paramMediaIterationClient;
      this.targetMediaType = paramMediaType;
    }
    
    protected void onClose(int paramInt)
    {
      BaseMediaSource.this.removeMediaIterationClient(this);
    }
  }
  
  protected class NormalMediaIterable
    extends MediaIterable
  {
    public NormalMediaIterable(Iterable<Media> paramIterable)
    {
      super(localIterable);
    }
    
    protected boolean filterMedia(Media paramMedia)
    {
      if (!super.filterMedia(paramMedia)) {}
      while (BaseMediaSource.this.isSubMedia(paramMedia)) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */