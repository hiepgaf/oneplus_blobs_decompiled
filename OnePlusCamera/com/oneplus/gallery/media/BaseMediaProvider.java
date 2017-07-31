package com.oneplus.gallery.media;

import com.oneplus.base.BasicThreadDependentObject;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseMediaProvider
  extends BasicThreadDependentObject
  implements MediaProvider
{
  protected final String TAG = getClass().getSimpleName();
  private final List<GroupMediaChangeCallbackHandle> m_GroupMediaChangeCallbackHandles = new ArrayList();
  private boolean m_IsReleased;
  private final List<MediaChangeCallbackHandle> m_MediaChangeCallbackHandles = new ArrayList();
  private final Map<MediaId, Media> m_MediaTable = new HashMap();
  private volatile Set<Media> m_RecycledMediaSet = new HashSet();
  
  private void removeGroupMediaChangeCallback(GroupMediaChangeCallbackHandle paramGroupMediaChangeCallbackHandle)
  {
    verifyAccess();
    this.m_GroupMediaChangeCallbackHandles.remove(paramGroupMediaChangeCallbackHandle);
  }
  
  private void removeMediaChangeCallback(MediaChangeCallbackHandle paramMediaChangeCallbackHandle)
  {
    verifyAccess();
    this.m_MediaChangeCallbackHandles.remove(paramMediaChangeCallbackHandle);
  }
  
  public Handle addGroupMediaChangedCallback(GroupMedia.GroupMediaChangeCallback paramGroupMediaChangeCallback)
  {
    if (paramGroupMediaChangeCallback != null)
    {
      verifyAccess();
      paramGroupMediaChangeCallback = new GroupMediaChangeCallbackHandle(paramGroupMediaChangeCallback);
      this.m_GroupMediaChangeCallbackHandles.add(paramGroupMediaChangeCallback);
      return paramGroupMediaChangeCallback;
    }
    Log.e(this.TAG, "addGroupMediaChangedCallback() - No call-back");
    return null;
  }
  
  public final Handle addMediaChangedCallback(MediaChangeCallback paramMediaChangeCallback)
  {
    if (paramMediaChangeCallback != null)
    {
      verifyAccess();
      paramMediaChangeCallback = new MediaChangeCallbackHandle(paramMediaChangeCallback);
      this.m_MediaChangeCallbackHandles.add(paramMediaChangeCallback);
      return paramMediaChangeCallback;
    }
    Log.e(this.TAG, "addMediaChangedCallback() - No call-back");
    return null;
  }
  
  protected final boolean addToMediaTable(Media paramMedia, boolean paramBoolean)
  {
    for (;;)
    {
      Media localMedia;
      synchronized (this.m_MediaTable)
      {
        localMedia = (Media)this.m_MediaTable.put(paramMedia.getId(), paramMedia);
        if (localMedia != null) {
          break label116;
        }
        if (!paramBoolean)
        {
          break label114;
          return false;
          Log.w(this.TAG, "addToMediaTable() - Duplicate media");
          this.m_MediaTable.put(paramMedia.getId(), localMedia);
        }
      }
      if (!isSubMedia(paramMedia)) {}
      for (int i = 0;; i = MediaChangeCallback.FLAG_SUB_MEDIA | 0x0)
      {
        callOnMediaCreated(paramMedia, i);
        break;
      }
      label114:
      return true;
      label116:
      if (localMedia != paramMedia) {}
    }
  }
  
  protected final boolean addToRecycledMedia(Media paramMedia, boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.m_RecycledMediaSet)
    {
      if (this.m_RecycledMediaSet.add(paramMedia))
      {
        if (!paramBoolean) {
          break label94;
        }
      }
      else
      {
        Log.w(this.TAG, "addToRecycledMedia() - Already recycled media: " + paramMedia);
        return false;
      }
    }
    if (!isSubMedia(paramMedia)) {}
    for (;;)
    {
      callOnMediaRecycled(paramMedia, i);
      break;
      i = MediaChangeCallback.FLAG_SUB_MEDIA | 0x0;
    }
    label94:
    return true;
  }
  
  protected final void callOnGroupMediaCoverChanged(GroupMedia paramGroupMedia, int paramInt)
  {
    int i = this.m_GroupMediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((GroupMediaChangeCallbackHandle)this.m_GroupMediaChangeCallbackHandles.get(i)).callback.onCoverChanged(paramGroupMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnGroupMediaSubMediaSizeChanged(GroupMedia paramGroupMedia, int paramInt)
  {
    int i = this.m_GroupMediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((GroupMediaChangeCallbackHandle)this.m_GroupMediaChangeCallbackHandles.get(i)).callback.onSubMediaSizeChanged(paramGroupMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnMediaCreated(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCallbackHandles.get(i)).callback.onMediaCreated(paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnMediaDeleted(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCallbackHandles.get(i)).callback.onMediaDeleted(paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnMediaRecycled(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCallbackHandles.get(i)).callback.onMediaRecycled(paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnMediaRestored(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCallbackHandles.get(i)).callback.onMediaRestored(paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected final void callOnMediaUpdated(Media paramMedia, int paramInt)
  {
    int i = this.m_MediaChangeCallbackHandles.size() - 1;
    while (i >= 0)
    {
      ((MediaChangeCallbackHandle)this.m_MediaChangeCallbackHandles.get(i)).callback.onMediaUpdated(paramMedia, paramInt);
      i -= 1;
    }
  }
  
  protected abstract MediaIterator createMediaIterator(int paramInt);
  
  protected final <TMedia extends Media> TMedia getFromMediaTable(MediaId paramMediaId)
  {
    synchronized (this.m_MediaTable)
    {
      paramMediaId = (Media)this.m_MediaTable.get(paramMediaId);
      return paramMediaId;
    }
  }
  
  protected Set<MediaId> getMediaIds()
  {
    synchronized (this.m_MediaTable)
    {
      Set localSet = Collections.unmodifiableSet(this.m_MediaTable.keySet());
      return localSet;
    }
  }
  
  protected int getMediaTableSize()
  {
    synchronized (this.m_MediaTable)
    {
      int i = this.m_MediaTable.size();
      return i;
    }
  }
  
  public boolean isMediaRecycled(Media paramMedia)
  {
    synchronized (this.m_RecycledMediaSet)
    {
      return this.m_RecycledMediaSet.contains(paramMedia);
    }
  }
  
  public boolean isOwnedMedia(Media paramMedia)
  {
    if (paramMedia == null) {
      return false;
    }
    synchronized (this.m_MediaTable)
    {
      boolean bool = this.m_MediaTable.containsKey(paramMedia.getId());
      return bool;
    }
  }
  
  protected abstract boolean isSubMedia(Media paramMedia);
  
  public Iterator<Media> iterateMedia()
  {
    return iterateMedia(FLAG_GROUP_MEDIA | FLAG_NORMAL_MEDIA);
  }
  
  public Iterator<Media> iterateMedia(int paramInt)
  {
    verifyAccess();
    return createMediaIterator(paramInt);
  }
  
  public void notifyMediaDeleted(Media paramMedia, int paramInt)
  {
    removeFromMediaTable(paramMedia, false);
    if (!isSubMedia(paramMedia)) {}
    for (;;)
    {
      callOnMediaDeleted(paramMedia, paramInt);
      return;
      paramInt |= MediaChangeCallback.FLAG_SUB_MEDIA;
    }
  }
  
  public void notifyMediaUpdated(Media paramMedia, int paramInt)
  {
    if (!isSubMedia(paramMedia)) {}
    for (;;)
    {
      callOnMediaUpdated(paramMedia, paramInt);
      return;
      paramInt |= MediaChangeCallback.FLAG_SUB_MEDIA;
    }
  }
  
  protected abstract void onRelease();
  
  public void release()
  {
    this.m_IsReleased = true;
    this.m_MediaTable.clear();
    this.m_RecycledMediaSet.clear();
    onRelease();
  }
  
  protected final boolean removeFromMediaTable(Media paramMedia, boolean paramBoolean)
  {
    boolean bool = isSubMedia(paramMedia);
    synchronized (this.m_MediaTable)
    {
      Media localMedia = (Media)this.m_MediaTable.remove(paramMedia.getId());
      if (localMedia != null)
      {
        if (localMedia == paramMedia)
        {
          if (paramBoolean) {
            break label115;
          }
          break label140;
        }
      }
      else {
        return false;
      }
      this.m_MediaTable.put(paramMedia.getId(), localMedia);
      Log.e(this.TAG, "removeFromMediaTable() - Invalid media : " + paramMedia);
      return false;
    }
    label115:
    if (!bool) {}
    for (int i = 0;; i = MediaChangeCallback.FLAG_SUB_MEDIA | 0x0)
    {
      callOnMediaDeleted(paramMedia, i);
      break;
    }
    label140:
    return true;
  }
  
  protected final boolean removeFromRecycledMedia(Media paramMedia, boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.m_RecycledMediaSet)
    {
      if (this.m_RecycledMediaSet.remove(paramMedia))
      {
        if (!paramBoolean) {
          break label81;
        }
      }
      else
      {
        Log.w(this.TAG, "removeFromRecycledMedia() - No need to restore");
        return false;
      }
    }
    if (!isSubMedia(paramMedia)) {}
    for (;;)
    {
      callOnMediaRestored(paramMedia, i);
      break;
      i = MediaChangeCallback.FLAG_SUB_MEDIA | 0x0;
    }
    label81:
    return true;
  }
  
  protected final void verifyReleaseState()
  {
    if (!this.m_IsReleased) {
      return;
    }
    throw new RuntimeException("Object has been released.");
  }
  
  private final class GroupMediaChangeCallbackHandle
    extends Handle
  {
    public final GroupMedia.GroupMediaChangeCallback callback;
    
    public GroupMediaChangeCallbackHandle(GroupMedia.GroupMediaChangeCallback paramGroupMediaChangeCallback)
    {
      super();
      this.callback = paramGroupMediaChangeCallback;
    }
    
    protected void onClose(int paramInt)
    {
      BaseMediaProvider.this.removeGroupMediaChangeCallback(this);
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
      BaseMediaProvider.this.removeMediaChangeCallback(this);
    }
  }
  
  protected abstract class MediaIterator
    implements Iterator<Media>
  {
    private final Iterator<Media> m_BaseIterator;
    private final int m_Flags;
    private Media m_Next;
    
    protected MediaIterator(int paramInt)
    {
      this.m_Flags = paramInt;
      this.m_BaseIterator = BaseMediaProvider.this.m_MediaTable.values().iterator();
    }
    
    protected abstract boolean canIterate(Media paramMedia);
    
    public boolean hasNext()
    {
      while (this.m_BaseIterator.hasNext())
      {
        this.m_Next = ((Media)this.m_BaseIterator.next());
        if (!canIterate(this.m_Next)) {
          this.m_Next = null;
        } else {
          return true;
        }
      }
      return false;
    }
    
    protected final boolean isGroupMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_GROUP_MEDIA) != 0;
    }
    
    protected final boolean isNormalMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_NORMAL_MEDIA) != 0;
    }
    
    protected final boolean isPhotoMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_PHOTO_ONLY) != 0;
    }
    
    protected final boolean isRecycledMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_RECYCLED_MEDIA) != 0;
    }
    
    protected final boolean isSubMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_SUB_MEDIA) != 0;
    }
    
    protected final boolean isVideoMediaIterated()
    {
      return (this.m_Flags & BaseMediaProvider.FLAG_VIDEO_ONLY) != 0;
    }
    
    public Media next()
    {
      if (this.m_Next == null) {
        throw new IllegalStateException();
      }
      Media localMedia = this.m_Next;
      this.m_Next = null;
      return localMedia;
    }
    
    public void remove()
    {
      throw new RuntimeException("Cannot remove media");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/BaseMediaProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */