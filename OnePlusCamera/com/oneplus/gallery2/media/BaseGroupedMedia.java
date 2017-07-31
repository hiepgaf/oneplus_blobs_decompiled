package com.oneplus.gallery2.media;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.Ref;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class BaseGroupedMedia
  extends BaseMedia
  implements GroupedMedia
{
  private static final int INTERNAL_FLAG_CAPTURED_BY_FRONT_CAM = 256;
  private static final int INTERNAL_FLAG_DELETING = 1;
  private static final int INTERNAL_FLAG_FAVORITE = 16;
  private static final int INTERNAL_FLAG_FAVORITE_SUPPORTED = 32;
  private static final int INTERNAL_FLAG_HIDDEN = 4;
  private static final int INTERNAL_FLAG_RESTORING = 2;
  private static final int SUB_MEDIA_UPDATE_FLAGS_MASK = FLAG_FILE_PATH_CHANGED | FLAG_FILE_SIZE_CHANGED | FLAG_LAST_MODIFIED_TIME_CHANGED | FLAG_TAKEN_TIME_CHANGED | FLAG_LOCATION_CHANGED | FLAG_ADDRESS_CHANGED;
  private Address m_Address;
  private boolean m_CanAddToAlbum;
  private volatile Media m_Cover;
  private String m_FilePath;
  private long m_FileSize;
  private int m_InternalFlags;
  private long m_LastModifiedTime;
  private Location m_Location;
  private List<SubMediaList> m_OpenedSubMediaLists;
  private final Set<Media> m_PendingAddingSubMedia = new HashSet();
  private final Set<Media> m_PendingRemovingSubMedia = new HashSet();
  private int m_PendingUpdateFlags;
  private Address m_PrevAddress;
  private String m_PrevFilePath;
  private Location m_PrevLocation;
  private long m_PrevTakenTime;
  private List<Media> m_RecycledSubMediaList;
  private final MediaComparator m_SubMediaComparator;
  private final List<Media> m_SubMediaList = new ArrayList();
  private int m_SubMediaUpdateCounter;
  private long m_TakenTime;
  
  protected BaseGroupedMedia(MediaSource paramMediaSource, MediaType paramMediaType)
  {
    this(paramMediaSource, paramMediaType, MediaComparator.FILE_PATH_ASC);
  }
  
  protected BaseGroupedMedia(MediaSource paramMediaSource, MediaType paramMediaType, MediaComparator paramMediaComparator)
  {
    super(paramMediaSource, paramMediaType);
    if (paramMediaComparator != null)
    {
      this.m_SubMediaComparator = paramMediaComparator;
      return;
    }
    throw new IllegalArgumentException("No comparator for sub media");
  }
  
  private void commitSubMediaChanges()
  {
    int j;
    if (this.m_SubMediaUpdateCounter <= 0)
    {
      if (this.m_PendingRemovingSubMedia.isEmpty())
      {
        i = 0;
        if (!this.m_PendingAddingSubMedia.isEmpty()) {
          break label174;
        }
        j = this.m_PendingUpdateFlags;
        this.m_PendingUpdateFlags = 0;
        if (i != 0) {
          break label308;
        }
        i = j;
        label49:
        notifyUpdated(i);
      }
    }
    else {
      return;
    }
    Iterator localIterator = this.m_PendingRemovingSubMedia.iterator();
    int i = 0;
    for (;;)
    {
      Media localMedia;
      if (localIterator.hasNext())
      {
        localMedia = (Media)localIterator.next();
        if (this.m_SubMediaList.remove(localMedia))
        {
          onSubMediaRemoved(localMedia);
          if (this.m_OpenedSubMediaLists == null)
          {
            i = 1;
          }
          else
          {
            i = this.m_OpenedSubMediaLists.size() - 1;
            while (i >= 0)
            {
              ((SubMediaList)this.m_OpenedSubMediaLists.get(i)).removeMedia(localMedia);
              i -= 1;
            }
          }
        }
      }
      else
      {
        this.m_PendingRemovingSubMedia.clear();
        break;
        label174:
        localIterator = this.m_PendingAddingSubMedia.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            localMedia = (Media)localIterator.next();
            j = Collections.binarySearch(this.m_SubMediaList, localMedia, this.m_SubMediaComparator) ^ 0xFFFFFFFF;
            if (j >= 0)
            {
              this.m_SubMediaList.add(j, localMedia);
              onSubMediaAdded(localMedia);
              if (this.m_OpenedSubMediaLists == null)
              {
                i = 1;
              }
              else
              {
                i = this.m_OpenedSubMediaLists.size() - 1;
                while (i >= 0)
                {
                  ((SubMediaList)this.m_OpenedSubMediaLists.get(i)).addMedia(localMedia);
                  i -= 1;
                }
              }
            }
          }
          else
          {
            this.m_PendingAddingSubMedia.clear();
            break;
            label308:
            onSubMediaChanged();
            i = j | FLAG_SUB_MEDIA_COUNT_CHANGED;
            break label49;
            i = 1;
          }
        }
        i = 1;
      }
    }
  }
  
  private void onSubMediaListReleased(SubMediaList paramSubMediaList)
  {
    if (this.m_OpenedSubMediaLists == null) {}
    while ((!this.m_OpenedSubMediaLists.remove(paramSubMediaList)) || (!this.m_OpenedSubMediaLists.isEmpty())) {
      return;
    }
    this.m_OpenedSubMediaLists = null;
  }
  
  private void restoreFromRecycleBin(DeletionHandle paramDeletionHandle, int paramInt)
  {
    this.m_InternalFlags |= 0x2;
    paramDeletionHandle = paramDeletionHandle.subDeletionHandles.entrySet().iterator();
    while (paramDeletionHandle.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramDeletionHandle.next();
      Media localMedia = (Media)localEntry.getKey();
      Handle.close((Handle)localEntry.getValue());
      if ((this.m_RecycledSubMediaList != null) && (this.m_RecycledSubMediaList.remove(localMedia)) && (!this.m_PendingRemovingSubMedia.contains(localMedia))) {
        this.m_PendingAddingSubMedia.add(localMedia);
      }
    }
    if (this.m_RecycledSubMediaList == null) {}
    for (;;)
    {
      this.m_InternalFlags &= 0xFFFFFFFD;
      commitSubMediaChanges();
      return;
      if (this.m_RecycledSubMediaList.isEmpty()) {
        this.m_RecycledSubMediaList = null;
      }
    }
  }
  
  private void syncStateFromSubMedia()
  {
    this.m_CanAddToAlbum = true;
    int k = this.m_SubMediaList.size() - 1;
    int m = 1;
    int i = 0;
    int n = 0;
    int j = 0;
    Object localObject;
    if (k >= 0)
    {
      localObject = (Media)this.m_SubMediaList.get(k);
      if (!((Media)localObject).isFavoriteSupported())
      {
        label56:
        if (((Media)localObject).isCapturedByFrontCamera()) {
          break label116;
        }
        label66:
        if (!((Media)localObject).canAddToAlbum()) {
          break label121;
        }
        label76:
        if (!((Media)localObject).isVisible()) {
          break label129;
        }
      }
      for (;;)
      {
        k -= 1;
        break;
        if (!((Media)localObject).isFavorite())
        {
          i = 1;
          break label56;
        }
        i = 1;
        n = 1;
        break label56;
        label116:
        j = 1;
        break label66;
        label121:
        this.m_CanAddToAlbum = false;
        break label76;
        label129:
        m = 0;
      }
    }
    if (i == 0)
    {
      this.m_InternalFlags &= 0xFFFFFFDF;
      if (isFavorite() != n) {
        break label205;
      }
      i = 0;
      if (j != 0) {
        break label244;
      }
      this.m_InternalFlags &= 0xFEFF;
      label177:
      if (isVisible() != m) {
        break label259;
      }
      if (i != 0) {
        break label297;
      }
    }
    label205:
    label244:
    label259:
    label297:
    do
    {
      return;
      this.m_InternalFlags |= 0x20;
      break;
      if (n == 0) {}
      for (this.m_InternalFlags &= 0xFFFFFFEF;; this.m_InternalFlags |= 0x10)
      {
        i = FLAG_FAVORITE_CHANGED | 0x0;
        break;
      }
      this.m_InternalFlags |= 0x100;
      break label177;
      if (m != 0) {}
      for (this.m_InternalFlags &= 0xFFFFFFFB;; this.m_InternalFlags |= 0x4)
      {
        i |= FLAG_VISIBILITY_CHANGED;
        break;
      }
      localObject = getSource();
    } while (!(localObject instanceof MediaStoreMediaSource));
    ((MediaStoreMediaSource)localObject).notifyMediaUpdatedByItself(this, i);
  }
  
  protected final boolean addSubMedia(Media paramMedia)
  {
    verifyAccess();
    if (paramMedia != null)
    {
      if ((this.m_InternalFlags & 0x3) != 0) {
        break label38;
      }
      if (!this.m_PendingRemovingSubMedia.remove(paramMedia)) {
        break label40;
      }
    }
    label38:
    label40:
    while (this.m_PendingAddingSubMedia.add(paramMedia))
    {
      commitSubMediaChanges();
      return true;
      return false;
      return false;
    }
    return false;
  }
  
  public boolean addToAlbum(long paramLong, int paramInt)
  {
    AlbumManager localAlbumManager;
    Media localMedia;
    if (this.m_CanAddToAlbum)
    {
      localAlbumManager = (AlbumManager)BaseApplication.current().findComponent(AlbumManager.class);
      if (localAlbumManager != null)
      {
        int i = this.m_SubMediaList.size() - 1;
        for (;;)
        {
          if (i < 0) {
            break label248;
          }
          localMedia = (Media)this.m_SubMediaList.get(i);
          if (!localMedia.addToAlbum(paramLong, paramInt)) {
            break;
          }
          i -= 1;
        }
      }
    }
    else
    {
      return false;
    }
    Log.e(getClass().getSimpleName(), "addToAlbum() - No AlbumManager");
    return false;
    Log.e(getClass().getSimpleName(), "addToAlbum() - Fail to add sub media " + localMedia + " to album " + paramLong);
    label248:
    for (paramInt = 0;; paramInt = 1)
    {
      if (paramInt == 0) {}
      while (paramInt != 0)
      {
        return true;
        if (!localAlbumManager.addMediaToAlbum(paramLong, this))
        {
          Log.e(getClass().getSimpleName(), "addToAlbum() - Fail to add " + this + " to album " + paramLong);
          paramInt = 0;
        }
      }
      paramInt = this.m_SubMediaList.size() - 1;
      while (paramInt >= 0)
      {
        localAlbumManager.removeMediaFromAlbum(paramLong, (Media)this.m_SubMediaList.get(paramInt));
        paramInt -= 1;
      }
      return false;
    }
  }
  
  public boolean canAddToAlbum()
  {
    return this.m_CanAddToAlbum;
  }
  
  protected final void completeSubMediaUpdate()
  {
    verifyAccess();
    if (this.m_SubMediaUpdateCounter > 0)
    {
      this.m_SubMediaUpdateCounter -= 1;
      if (this.m_SubMediaUpdateCounter == 0) {}
    }
    else
    {
      return;
    }
    commitSubMediaChanges();
  }
  
  public boolean contains(Media paramMedia)
  {
    return this.m_SubMediaList.contains(paramMedia);
  }
  
  public Handle delete(Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    Object localObject1;
    DeletionHandle localDeletionHandle;
    Object localObject2;
    if ((this.m_InternalFlags & 0x1) == 0)
    {
      if ((this.m_InternalFlags & 0x2) == 0)
      {
        commitSubMediaChanges();
        this.m_InternalFlags |= 0x1;
        localObject1 = (Media[])this.m_SubMediaList.toArray(new Media[this.m_SubMediaList.size()]);
        this.m_SubMediaList.clear();
        localDeletionHandle = new DeletionHandle(paramDeletionCallback, paramInt);
        if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) == 0) {
          break label189;
        }
        if (this.m_RecycledSubMediaList == null) {
          break label373;
        }
        i = localObject1.length;
        for (;;)
        {
          i -= 1;
          if (i < 0) {
            break;
          }
          localObject2 = localObject1[i];
          this.m_RecycledSubMediaList.add(localObject2);
          localDeletionHandle.subDeletionHandles.put(localObject2, ((Media)localObject2).delete(localDeletionHandle.wrappedCallback, paramInt));
        }
      }
    }
    else
    {
      Log.e(getClass().getSimpleName(), "delete() - Already deleting");
      return null;
    }
    Log.e(getClass().getSimpleName(), "delete() - Restoring");
    return null;
    label189:
    int i = localObject1.length;
    for (;;)
    {
      i -= 1;
      if (i < 0) {
        break;
      }
      localObject2 = localObject1[i];
      localDeletionHandle.subDeletionHandles.put(localObject2, ((Media)localObject2).delete(localDeletionHandle.wrappedCallback, paramInt));
    }
    if (this.m_RecycledSubMediaList == null)
    {
      label243:
      localObject1 = deleteGroupedMediaItself(localDeletionHandle.wrappedCallback, paramInt);
      if (Handle.isValid((Handle)localObject1)) {
        break label387;
      }
      label263:
      this.m_InternalFlags &= 0xFFFFFFFE;
      if (localDeletionHandle.numberOfCompletedHandles + localDeletionHandle.numberOfCancelledHandles == localDeletionHandle.subDeletionHandles.size()) {
        break label404;
      }
    }
    label373:
    label387:
    label404:
    while (paramDeletionCallback == null)
    {
      return localDeletionHandle;
      i = this.m_RecycledSubMediaList.size() - 1;
      while (i >= 0)
      {
        localObject1 = (Media)this.m_RecycledSubMediaList.get(i);
        localDeletionHandle.subDeletionHandles.put(localObject1, ((Media)localObject1).delete(localDeletionHandle.wrappedCallback, paramInt));
        i -= 1;
      }
      this.m_RecycledSubMediaList = null;
      break label243;
      this.m_RecycledSubMediaList = new ArrayList();
      break;
      localDeletionHandle.subDeletionHandles.put(this, localObject1);
      break label263;
    }
    paramDeletionCallback.onDeletionCompleted(this, true, paramInt);
    return localDeletionHandle;
  }
  
  protected abstract Handle deleteGroupedMediaItself(Media.DeletionCallback paramDeletionCallback, int paramInt);
  
  public Address getAddress()
  {
    return this.m_Address;
  }
  
  public MediaCacheKey getCacheKey()
  {
    Media localMedia = this.m_Cover;
    if (localMedia == null) {
      return null;
    }
    return localMedia.getCacheKey();
  }
  
  public Uri getContentUri()
  {
    if (this.m_Cover == null) {
      return null;
    }
    return this.m_Cover.getContentUri();
  }
  
  public <T extends Media> T getCover()
  {
    return this.m_Cover;
  }
  
  public Handle getDetails(Media.DetailsCallback paramDetailsCallback)
  {
    verifyAccess();
    if (paramDetailsCallback == null) {}
    while (this.m_Cover == null) {
      return null;
    }
    return this.m_Cover.getDetails(paramDetailsCallback);
  }
  
  public String getFilePath()
  {
    return this.m_FilePath;
  }
  
  public long getFileSize()
  {
    return this.m_FileSize;
  }
  
  public long getLastModifiedTime()
  {
    return this.m_LastModifiedTime;
  }
  
  public Location getLocation()
  {
    return this.m_Location;
  }
  
  public String getMimeType()
  {
    Media localMedia = this.m_Cover;
    if (localMedia == null) {
      return null;
    }
    return localMedia.getMimeType();
  }
  
  public Address getPreviousAddress()
  {
    return this.m_PrevAddress;
  }
  
  public String getPreviousFilePath()
  {
    return this.m_PrevFilePath;
  }
  
  public Location getPreviousLocation()
  {
    return this.m_PrevLocation;
  }
  
  public long getPreviousTakenTime()
  {
    return this.m_PrevTakenTime;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    if (this.m_Cover == null) {
      return null;
    }
    return this.m_Cover.getSize(paramSizeCallback);
  }
  
  protected final <T extends Media> T getSubMedia(int paramInt)
  {
    return (Media)this.m_SubMediaList.get(paramInt);
  }
  
  protected final Iterable<Media> getSubMedia()
  {
    return this.m_SubMediaList;
  }
  
  public int getSubMediaCount()
  {
    return this.m_SubMediaList.size();
  }
  
  public long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public boolean isCapturedByFrontCamera()
  {
    return (this.m_InternalFlags & 0x100) != 0;
  }
  
  public boolean isFavorite()
  {
    return (this.m_InternalFlags & 0x10) != 0;
  }
  
  public boolean isFavoriteSupported()
  {
    return (this.m_InternalFlags & 0x20) != 0;
  }
  
  public boolean isVisible()
  {
    return (this.m_InternalFlags & 0x4) == 0;
  }
  
  protected void notifySubMediaUpdated(Media paramMedia, int paramInt)
  {
    if (paramMedia != null) {
      if (paramMedia == this.m_Cover) {
        break label25;
      }
    }
    for (;;)
    {
      syncStateFromSubMedia();
      if (this.m_OpenedSubMediaLists != null) {
        break;
      }
      return;
      return;
      label25:
      if ((SUB_MEDIA_UPDATE_FLAGS_MASK & paramInt) != 0) {
        notifyUpdated(onUpdate(this.m_Cover));
      }
    }
    int i = this.m_OpenedSubMediaLists.size() - 1;
    label60:
    SubMediaList localSubMediaList;
    if (i >= 0)
    {
      localSubMediaList = (SubMediaList)this.m_OpenedSubMediaLists.get(i);
      if ((localSubMediaList.getComparator().getEffectiveMediaUpdateFlags() & paramInt) != 0) {
        break label99;
      }
    }
    for (;;)
    {
      i -= 1;
      break label60;
      break;
      label99:
      localSubMediaList.checkMediaIndex(paramMedia);
    }
  }
  
  protected void notifyUpdated(int paramInt)
  {
    MediaSource localMediaSource;
    if (paramInt != 0)
    {
      localMediaSource = getSource();
      if (this.m_SubMediaUpdateCounter <= 0)
      {
        if ((localMediaSource instanceof BaseMediaSource)) {
          break label50;
        }
        Log.e(getClass().getSimpleName(), "notifyUpdated() - No implementation");
      }
    }
    else
    {
      return;
    }
    this.m_PendingUpdateFlags |= paramInt;
    return;
    label50:
    ((BaseMediaSource)localMediaSource).notifyMediaUpdatedByItself(this, paramInt);
  }
  
  protected void onCoverChanged(Media paramMedia) {}
  
  protected void onSubMediaAdded(Media paramMedia) {}
  
  protected void onSubMediaChanged()
  {
    syncStateFromSubMedia();
    if (!isFavorite()) {}
    for (;;)
    {
      return;
      int i = this.m_SubMediaList.size() - 1;
      while (i >= 0)
      {
        ((Media)this.m_SubMediaList.get(i)).setFavorite(true);
        i -= 1;
      }
    }
  }
  
  protected void onSubMediaRemoved(Media paramMedia) {}
  
  protected int onUpdate(Media paramMedia)
  {
    long l1 = 0L;
    long l3;
    Object localObject;
    Address localAddress;
    long l2;
    int j;
    label35:
    int i;
    if (paramMedia == null)
    {
      paramMedia = null;
      l3 = 0L;
      localObject = null;
      localAddress = null;
      l2 = 0L;
      if (!TextUtils.equals(this.m_FilePath, (CharSequence)localObject)) {
        break label191;
      }
      j = 0;
      i = j;
      if (this.m_FileSize != l3)
      {
        this.m_FileSize = l3;
        i = j | FLAG_FILE_SIZE_CHANGED;
      }
      j = i;
      if (this.m_LastModifiedTime != l2)
      {
        this.m_LastModifiedTime = l2;
        j = i | FLAG_LAST_MODIFIED_TIME_CHANGED;
      }
      i = j;
      if (this.m_TakenTime != l1)
      {
        this.m_PrevTakenTime = this.m_TakenTime;
        this.m_TakenTime = l1;
        i = j | FLAG_TAKEN_TIME_CHANGED;
      }
      if (this.m_Location != null) {
        break label214;
      }
      label122:
      if (paramMedia != null) {
        break label247;
      }
    }
    for (;;)
    {
      if (this.m_Address != localAddress) {
        break label261;
      }
      return i;
      localAddress = paramMedia.getAddress();
      localObject = paramMedia.getFilePath();
      l3 = paramMedia.getFileSize();
      Location localLocation = paramMedia.getLocation();
      l2 = paramMedia.getLastModifiedTime();
      l1 = paramMedia.getTakenTime();
      paramMedia = localLocation;
      break;
      label191:
      this.m_PrevFilePath = this.m_FilePath;
      this.m_FilePath = ((String)localObject);
      j = FLAG_FILE_PATH_CHANGED | 0x0;
      break label35;
      label214:
      if (this.m_Location.equals(paramMedia)) {
        break label122;
      }
      label247:
      do
      {
        this.m_PrevLocation = this.m_Location;
        this.m_Location = paramMedia;
        i |= FLAG_LOCATION_CHANGED;
        break;
      } while (!paramMedia.equals(this.m_Location));
    }
    label261:
    this.m_PrevAddress = this.m_Address;
    this.m_Address = localAddress;
    return i | FLAG_ADDRESS_CHANGED;
  }
  
  public InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException
  {
    Media localMedia = this.m_Cover;
    if (localMedia == null) {
      throw new RuntimeException("No cover media to open input stream");
    }
    return localMedia.openInputStream(paramRef, paramInt);
  }
  
  public MediaList openSubMediaList(MediaComparator paramMediaComparator, int paramInt)
  {
    verifyAccess();
    SubMediaList localSubMediaList;
    boolean bool;
    if (paramMediaComparator != null)
    {
      localSubMediaList = new SubMediaList(paramMediaComparator);
      List localList = this.m_SubMediaList;
      if (paramMediaComparator == this.m_SubMediaComparator) {
        break label72;
      }
      bool = false;
      label35:
      localSubMediaList.addMedia(localList, bool);
      if (this.m_OpenedSubMediaLists == null) {
        break label77;
      }
    }
    for (;;)
    {
      this.m_OpenedSubMediaLists.add(localSubMediaList);
      return localSubMediaList;
      paramMediaComparator = MediaComparator.FILE_PATH_ASC;
      break;
      label72:
      bool = true;
      break label35;
      label77:
      this.m_OpenedSubMediaLists = new ArrayList();
    }
  }
  
  public Size peekSize()
  {
    if (this.m_Cover == null) {
      return null;
    }
    return this.m_Cover.peekSize();
  }
  
  public boolean removeFromAlbum(long paramLong, int paramInt)
  {
    AlbumManager localAlbumManager;
    boolean bool;
    if (this.m_CanAddToAlbum)
    {
      localAlbumManager = (AlbumManager)BaseApplication.current().findComponent(AlbumManager.class);
      if (localAlbumManager != null)
      {
        int i = this.m_SubMediaList.size();
        bool = false;
        i -= 1;
        while (i >= 0)
        {
          bool |= ((Media)this.m_SubMediaList.get(i)).removeFromAlbum(paramLong, paramInt);
          i -= 1;
        }
      }
    }
    else
    {
      return false;
    }
    Log.e(getClass().getSimpleName(), "removeFromAlbum() - No AlbumManager");
    return false;
    return localAlbumManager.removeMediaFromAlbum(paramLong, this) | bool;
  }
  
  protected boolean removeSubMedia(Media paramMedia)
  {
    verifyAccess();
    if (paramMedia != null) {
      if (!this.m_PendingAddingSubMedia.remove(paramMedia)) {
        break label29;
      }
    }
    label29:
    while (this.m_PendingRemovingSubMedia.add(paramMedia))
    {
      commitSubMediaChanges();
      return true;
      return false;
    }
    return false;
  }
  
  protected void setCover(Media paramMedia)
  {
    verifyAccess();
    if (this.m_Cover != paramMedia)
    {
      this.m_Cover = paramMedia;
      int i = FLAG_COVER_CHANGED;
      onCoverChanged(paramMedia);
      notifyUpdated(i | onUpdate(paramMedia));
      return;
    }
  }
  
  public boolean setFavorite(boolean paramBoolean)
  {
    verifyAccess();
    Media localMedia;
    if (isFavoriteSupported())
    {
      i = this.m_SubMediaList.size() - 1;
      if (i < 0) {
        break label161;
      }
      localMedia = (Media)this.m_SubMediaList.get(i);
      if (localMedia.isFavoriteSupported()) {
        break label61;
      }
    }
    label61:
    while (localMedia.setFavorite(paramBoolean))
    {
      i -= 1;
      break;
      return false;
    }
    Log.e(getClass().getSimpleName(), "setFavorite() - Fail to update favorite state of " + localMedia);
    int i = this.m_SubMediaList.size() - 1;
    if (i >= 0)
    {
      localMedia = (Media)this.m_SubMediaList.get(i);
      if (!paramBoolean) {}
      for (boolean bool = true;; bool = false)
      {
        localMedia.setFavorite(bool);
        i -= 1;
        break;
      }
    }
    return false;
    label161:
    return true;
  }
  
  public boolean setVisible(boolean paramBoolean)
  {
    verifyAccess();
    Media localMedia;
    if (isVisibilityChangeSupported())
    {
      i = this.m_SubMediaList.size() - 1;
      if (i < 0) {
        break label161;
      }
      localMedia = (Media)this.m_SubMediaList.get(i);
      if (localMedia.isVisibilityChangeSupported()) {
        break label61;
      }
    }
    label61:
    while (localMedia.setVisible(paramBoolean))
    {
      i -= 1;
      break;
      return false;
    }
    Log.e(getClass().getSimpleName(), "setVisible() - Fail to update visibility state of " + localMedia);
    int i = this.m_SubMediaList.size() - 1;
    if (i >= 0)
    {
      localMedia = (Media)this.m_SubMediaList.get(i);
      if (!paramBoolean) {}
      for (boolean bool = true;; bool = false)
      {
        localMedia.setVisible(bool);
        i -= 1;
        break;
      }
    }
    return false;
    label161:
    return true;
  }
  
  protected final void startSubMediaUpdate()
  {
    verifyAccess();
    this.m_SubMediaUpdateCounter += 1;
  }
  
  public String toString()
  {
    return "[" + getId() + "]";
  }
  
  private final class DeletionHandle
    extends CallbackHandle<Media.DeletionCallback>
  {
    public final int flags;
    public int numberOfCancelledHandles;
    public int numberOfCompletedHandles;
    public final Map<Media, Handle> subDeletionHandles = new HashMap();
    public final Media.DeletionCallback wrappedCallback = new Media.DeletionCallback()
    {
      public void onDeletionCancelled(Media paramAnonymousMedia, int paramAnonymousInt)
      {
        paramAnonymousMedia = BaseGroupedMedia.DeletionHandle.this;
        paramAnonymousMedia.numberOfCancelledHandles += 1;
      }
      
      public void onDeletionCompleted(Media paramAnonymousMedia, boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        paramAnonymousMedia = BaseGroupedMedia.DeletionHandle.this;
        paramAnonymousMedia.numberOfCompletedHandles += 1;
        if (BaseGroupedMedia.DeletionHandle.this.numberOfCompletedHandles + BaseGroupedMedia.DeletionHandle.this.numberOfCancelledHandles != BaseGroupedMedia.DeletionHandle.this.subDeletionHandles.size()) {}
        while (BaseGroupedMedia.DeletionHandle.this.getCallback() == null) {
          return;
        }
        ((Media.DeletionCallback)BaseGroupedMedia.DeletionHandle.this.getCallback()).onDeletionCompleted(BaseGroupedMedia.this, true, paramAnonymousInt);
      }
    };
    
    public DeletionHandle(Media.DeletionCallback paramDeletionCallback, int paramInt)
    {
      super(paramDeletionCallback, null);
      this.flags = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      int i = 0;
      if ((this.flags & Media.FLAG_MOVE_TO_RECYCE_BIN) == 0) {}
      while (i == 0)
      {
        if (this.numberOfCompletedHandles > 0) {
          break label79;
        }
        Iterator localIterator = this.subDeletionHandles.values().iterator();
        while (localIterator.hasNext()) {
          Handle.close((Handle)localIterator.next());
        }
        i = 1;
      }
      BaseGroupedMedia.this.restoreFromRecycleBin(this, paramInt);
      label79:
      do
      {
        return;
        Log.w(getClass().getSimpleName(), "onClose() - Some of sub media has been deleted, cannot cancel");
        return;
        this.subDeletionHandles.clear();
      } while (getCallback() == null);
      ((Media.DeletionCallback)getCallback()).onDeletionCancelled(BaseGroupedMedia.this, this.flags);
    }
  }
  
  private final class SubMediaList
    extends BaseMediaList
  {
    public SubMediaList(MediaComparator paramMediaComparator)
    {
      super(-1);
    }
    
    public void release()
    {
      super.release();
      BaseGroupedMedia.this.onSubMediaListReleased(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseGroupedMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */