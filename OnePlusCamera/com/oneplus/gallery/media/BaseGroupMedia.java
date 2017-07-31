package com.oneplus.gallery.media;

import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.oneplus.base.ConcurrencyObject;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class BaseGroupMedia
  implements GroupMedia
{
  private static final boolean PRINT_LOGS = false;
  private static final String TAG = BaseGroupMedia.class.getSimpleName();
  private final List<WeakReference<GroupMediaListImpl>> m_ActiveGroupMediaLists = new ArrayList();
  private int m_CoverIndex = -1;
  private ConcurrencyObject<Media> m_CoverMedia;
  private final String m_GroupId;
  private final List<GroupMedia.GroupMediaChangeCallback> m_GroupMediaChangeCallbacks = new ArrayList();
  private final Handler m_Handler;
  private boolean m_IsFavorite;
  private boolean m_IsUserCoverFound;
  private final MediaId m_MediaId;
  private MediaProvider m_MediaProvider;
  private Long m_ParentId;
  private Set<Media> m_SubMedia = new HashSet();
  
  BaseGroupMedia(String paramString, Media paramMedia, MediaProvider paramMediaProvider, Handler paramHandler)
  {
    if (paramMediaProvider != null) {
      if (paramHandler != null) {
        break label111;
      }
    }
    label111:
    for (this.m_Handler = new Handler(Looper.getMainLooper());; this.m_Handler = paramHandler)
    {
      this.m_GroupId = paramString;
      this.m_MediaId = new MediaId(paramString);
      this.m_CoverMedia = new ConcurrencyObject(paramHandler);
      this.m_MediaProvider = paramMediaProvider;
      return;
      throw new IllegalArgumentException("Media provider is null");
    }
  }
  
  private void releaseGroupMediaList(GroupMediaListImpl paramGroupMediaListImpl)
  {
    int i = this.m_ActiveGroupMediaLists.size() - 1;
    if (i >= 0)
    {
      GroupMediaListImpl localGroupMediaListImpl = (GroupMediaListImpl)((WeakReference)this.m_ActiveGroupMediaLists.get(i)).get();
      if (localGroupMediaListImpl != paramGroupMediaListImpl) {
        if (localGroupMediaListImpl == null) {
          break label79;
        }
      }
    }
    for (;;)
    {
      i -= 1;
      break;
      this.m_ActiveGroupMediaLists.remove(i);
      Log.v(TAG, "releaseGroupMediaList() - Group id: ", getId(), ", released media list: ", paramGroupMediaListImpl);
      return;
      label79:
      this.m_ActiveGroupMediaLists.remove(i);
    }
  }
  
  private void updateFavoriteState()
  {
    for (;;)
    {
      synchronized (this.m_SubMedia)
      {
        Iterator localIterator1 = this.m_SubMedia.iterator();
        if (!localIterator1.hasNext()) {
          break label134;
        }
        if (!((Media)localIterator1.next()).isFavorite()) {
          continue;
        }
        bool = true;
        if (!(this.m_IsFavorite ^ bool)) {
          return;
        }
      }
      this.m_IsFavorite = bool;
      synchronized (this.m_SubMedia)
      {
        Iterator localIterator2 = this.m_SubMedia.iterator();
        if (localIterator2.hasNext()) {
          ((Media)localIterator2.next()).setFavorite(this.m_IsFavorite);
        }
      }
      this.m_MediaProvider.notifyMediaUpdated(this, 0);
      return;
      label134:
      boolean bool = false;
    }
  }
  
  public void addGroupMediaChangeCallback(GroupMedia.GroupMediaChangeCallback paramGroupMediaChangeCallback)
  {
    if (this.m_GroupMediaChangeCallbacks.contains(paramGroupMediaChangeCallback)) {
      return;
    }
    this.m_GroupMediaChangeCallbacks.add(paramGroupMediaChangeCallback);
  }
  
  public boolean addSubMedia(Media paramMedia)
  {
    synchronized (this.m_SubMedia)
    {
      boolean bool = this.m_SubMedia.add(paramMedia);
      if (!bool) {
        return false;
      }
    }
    if (!refreshCover(paramMedia, null)) {}
    for (;;)
    {
      updateFavoriteState();
      int i = this.m_ActiveGroupMediaLists.size() - 1;
      while (i >= 0)
      {
        ((GroupMediaListImpl)((WeakReference)this.m_ActiveGroupMediaLists.get(i)).get()).addMedia(paramMedia);
        i -= 1;
      }
      i = this.m_GroupMediaChangeCallbacks.size() - 1;
      while (i >= 0)
      {
        ((GroupMedia.GroupMediaChangeCallback)this.m_GroupMediaChangeCallbacks.get(i)).onCoverChanged(this, 0);
        i -= 1;
      }
    }
    return true;
  }
  
  public boolean addToAlbum(long paramLong)
  {
    synchronized (this.m_SubMedia)
    {
      Iterator localIterator = this.m_SubMedia.iterator();
      for (boolean bool = true; localIterator.hasNext(); bool = ((Media)localIterator.next()).addToAlbum(paramLong) & bool) {}
      return bool;
    }
  }
  
  protected abstract int evaluateGroupIndex(Media paramMedia);
  
  public Uri getContentUri()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getContentUri();
  }
  
  public int getCoverIndex()
  {
    return this.m_CoverIndex;
  }
  
  public Media getCoverMedia()
  {
    return (Media)this.m_CoverMedia.get();
  }
  
  public Handle getDetails(Media.MediaDetailsCallback paramMediaDetailsCallback, Handler paramHandler)
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getDetails(paramMediaDetailsCallback, paramHandler);
  }
  
  public String getFilePath()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getFilePath();
  }
  
  public long getFileSize()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return 0L;
    }
    return localMedia.getFileSize();
  }
  
  public String getGroupId()
  {
    return this.m_GroupId;
  }
  
  public Handler getHandler()
  {
    return this.m_Handler;
  }
  
  public int getHeight()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return 0;
    }
    return localMedia.getHeight();
  }
  
  public MediaId getId()
  {
    return this.m_MediaId;
  }
  
  public long getLastModifiedTime()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return 0L;
    }
    return localMedia.getLastModifiedTime();
  }
  
  public Location getLocation()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getLocation();
  }
  
  public String getMimeType()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getMimeType();
  }
  
  public long getParentId()
  {
    if (this.m_ParentId != null) {}
    for (;;)
    {
      return this.m_ParentId.longValue();
      int i = this.m_GroupId.indexOf("_");
      this.m_ParentId = Long.valueOf(Long.parseLong(this.m_GroupId.substring(0, i)));
    }
  }
  
  public Set<Media> getSubMedia()
  {
    synchronized (this.m_SubMedia)
    {
      Set localSet2 = this.m_SubMedia;
      return localSet2;
    }
  }
  
  public long getTakenTime()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return 0L;
    }
    return localMedia.getTakenTime();
  }
  
  public MediaType getType()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return null;
    }
    return localMedia.getType();
  }
  
  public int getWidth()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return 0;
    }
    return localMedia.getWidth();
  }
  
  public boolean isCapturedByFrontCamera()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return false;
    }
    return localMedia.isCapturedByFrontCamera();
  }
  
  public boolean isDependencyThread()
  {
    return this.m_Handler.getLooper().getThread() == Thread.currentThread();
  }
  
  public boolean isDocumentUri()
  {
    Media localMedia = getCoverMedia();
    if (localMedia == null) {
      return false;
    }
    return localMedia.isDocumentUri();
  }
  
  public boolean isFavorite()
  {
    return this.m_IsFavorite;
  }
  
  public boolean isFavoriteSupported()
  {
    return true;
  }
  
  public boolean isUserCoverFound()
  {
    return this.m_IsUserCoverFound;
  }
  
  public MediaList openGroupMediaList(MediaComparator paramMediaComparator, int paramInt)
  {
    if (paramMediaComparator != null)
    {
      Log.v(TAG, "openGroupMediaList()");
      paramMediaComparator = new GroupMediaListImpl(paramMediaComparator);
      paramMediaComparator.addMedia(getSubMedia());
      this.m_ActiveGroupMediaLists.add(new WeakReference(paramMediaComparator));
      return paramMediaComparator;
    }
    throw new IllegalArgumentException("No comparator");
  }
  
  protected abstract boolean refreshCover(Media paramMedia1, Media paramMedia2);
  
  public boolean removeFromAlbum(long paramLong)
  {
    synchronized (this.m_SubMedia)
    {
      Iterator localIterator = this.m_SubMedia.iterator();
      for (boolean bool = true; localIterator.hasNext(); bool = ((Media)localIterator.next()).removeFromAlbum(paramLong) & bool) {}
      return bool;
    }
  }
  
  public void removeGroupMediaChangeCallback(GroupMedia.GroupMediaChangeCallback paramGroupMediaChangeCallback)
  {
    this.m_GroupMediaChangeCallbacks.remove(paramGroupMediaChangeCallback);
  }
  
  public boolean removeSubMedia(Media paramMedia)
  {
    synchronized (this.m_SubMedia)
    {
      boolean bool = this.m_SubMedia.remove(paramMedia);
      if (!bool) {
        return false;
      }
    }
    if (!refreshCover(null, paramMedia)) {}
    for (;;)
    {
      updateFavoriteState();
      int i = this.m_ActiveGroupMediaLists.size() - 1;
      while (i >= 0)
      {
        ((GroupMediaListImpl)((WeakReference)this.m_ActiveGroupMediaLists.get(i)).get()).removeMedia(paramMedia);
        i -= 1;
      }
      i = this.m_GroupMediaChangeCallbacks.size() - 1;
      while (i >= 0)
      {
        ((GroupMedia.GroupMediaChangeCallback)this.m_GroupMediaChangeCallbacks.get(i)).onCoverChanged(this, 0);
        i -= 1;
      }
    }
    return true;
  }
  
  public boolean setCoverMedia(Media paramMedia, int paramInt)
  {
    if (paramMedia == null)
    {
      paramInt = -1;
      paramMedia = null;
      label8:
      if (this.m_CoverIndex != paramInt) {
        break label35;
      }
    }
    for (boolean bool = false;; bool = true)
    {
      if (getCoverMedia() != paramMedia) {
        break label45;
      }
      return bool;
      if (paramInt < 0) {
        break;
      }
      break label8;
      label35:
      this.m_CoverIndex = paramInt;
    }
    label45:
    this.m_CoverMedia.set(paramMedia);
    return true;
  }
  
  public boolean setFavorite(boolean paramBoolean)
  {
    if (this.m_IsFavorite != paramBoolean)
    {
      this.m_IsFavorite = paramBoolean;
      synchronized (this.m_SubMedia)
      {
        Iterator localIterator = this.m_SubMedia.iterator();
        for (boolean bool = true; localIterator.hasNext(); bool = ((Media)localIterator.next()).setFavorite(paramBoolean) & bool) {}
        this.m_MediaProvider.notifyMediaUpdated(this, 0);
        return bool;
      }
    }
    return true;
  }
  
  public boolean setIsUserCoverFound(boolean paramBoolean)
  {
    if (this.m_IsUserCoverFound == paramBoolean) {
      return false;
    }
    this.m_IsUserCoverFound = paramBoolean;
    return true;
  }
  
  public String toString()
  {
    Object localObject = getCoverMedia();
    StringBuilder localStringBuilder = new StringBuilder("[Group id: ").append(this.m_GroupId).append(", Cover path: ");
    if (localObject == null) {}
    for (localObject = "No cover";; localObject = ((Media)localObject).getFilePath()) {
      return (String)localObject + ", Cover index: " + this.m_CoverIndex + ", User cover found: " + this.m_IsUserCoverFound + ", Favorite: " + this.m_IsFavorite + ", Sub media size: " + this.m_SubMedia.size() + "]";
    }
  }
  
  private final class GroupMediaListImpl
    extends BasicMediaList
  {
    public GroupMediaListImpl(MediaComparator paramMediaComparator)
    {
      super(-1);
    }
    
    public void release()
    {
      super.release();
      clearMedia();
      BaseGroupMedia.this.releaseGroupMediaList(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/BaseGroupMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */