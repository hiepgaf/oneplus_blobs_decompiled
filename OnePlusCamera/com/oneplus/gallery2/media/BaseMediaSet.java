package com.oneplus.gallery2.media;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.LongSparseArray;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.gallery2.ExtraKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class BaseMediaSet
  extends BasicBaseObject
  implements MediaSet
{
  private static final int COVER_MEDIA_UPDATE_FLAGS_MASK = Media.FLAG_LAST_MODIFIED_TIME_CHANGED | GroupedMedia.FLAG_COVER_CHANGED;
  private static final long DURATION_COMMIT_MEDIA_SYNC_DELAY = 500L;
  private static final boolean PRINT_MEDIA_DEBUG_LOG = false;
  private boolean m_ContainsHiddenMedia;
  private final List<Media> m_CoverMediaList = new ArrayList();
  private LongSparseArray<Object> m_Extra;
  private final Set<Media> m_HiddenMedia = new HashSet();
  private boolean m_IsDelayedMediaSyncCommitScheduled;
  private PropertyChangedCallback<Locale> m_LocaleChangedCallback;
  private final Set<Media> m_Media = new HashSet();
  private Handle m_MediaChangeCBHandle;
  private Handle m_MediaIterationClientHandle;
  private long m_MediaIterationStartTime;
  private final PropertyChangedCallback<Boolean> m_MediaTableStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
      BaseMediaSet.this.onMediaTableReady();
    }
  };
  private final List<MediaListImpl> m_OpenedMediaLists = new ArrayList();
  private final Set<Media> m_PendingAddingMedia = new HashSet();
  private final Set<Media> m_PendingRemovingMedia = new HashSet();
  private int m_PhotoCount;
  private final MediaSource m_Source;
  private final MediaType m_TargetMediaType;
  private int m_VideoCount;
  
  protected BaseMediaSet(MediaSource paramMediaSource, MediaType paramMediaType)
  {
    label140:
    label147:
    int i;
    if (paramMediaSource != null)
    {
      if (paramMediaType == MediaType.UNKNOWN) {
        break label175;
      }
      this.m_Source = paramMediaSource;
      this.m_TargetMediaType = paramMediaType;
      this.m_LocaleChangedCallback = onPrepareLocaleChangedCallback();
      if (this.m_LocaleChangedCallback != null) {
        break label185;
      }
      if (!((Boolean)paramMediaSource.get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {
        break label201;
      }
      onMediaTableReady();
      if (canSyncMediaBeforeMediaTableReady()) {
        break label217;
      }
      i = getNameResourceId();
      if (i != 0) {
        break label224;
      }
    }
    for (;;)
    {
      enablePropertyLogs(PROP_COVER_HASH_CODE, 1);
      return;
      throw new IllegalArgumentException("No media source");
      label175:
      throw new IllegalArgumentException("No target media type");
      label185:
      BaseApplication.current().addCallback(BaseApplication.PROP_LOCALE, this.m_LocaleChangedCallback);
      break;
      label201:
      paramMediaSource.addCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableStateChangedCB);
      break label140;
      label217:
      setupMedia();
      break label147;
      label224:
      setReadOnly(PROP_NAME, BaseApplication.current().getString(i));
    }
  }
  
  private boolean addHiddenMedia(Media paramMedia)
  {
    if (paramMedia == null) {}
    while (paramMedia.isVisible()) {
      return false;
    }
    boolean bool = this.m_HiddenMedia.add(paramMedia);
    if (!bool) {
      return bool;
    }
    setReadOnly(MediaSet.PROP_HIDDEN_MEDIA_COUNT, Integer.valueOf(this.m_HiddenMedia.size()));
    return bool;
  }
  
  private void onMediaListReleased(MediaListImpl paramMediaListImpl)
  {
    if (this.m_OpenedMediaLists.remove(paramMediaListImpl))
    {
      Log.v(this.TAG, "[", getId(), "] onMediaListReleased() - Opened media list count : ", Integer.valueOf(this.m_OpenedMediaLists.size()));
      return;
    }
  }
  
  private boolean removeHiddenMedia(Media paramMedia)
  {
    boolean bool;
    if (paramMedia != null)
    {
      bool = this.m_HiddenMedia.remove(paramMedia);
      if (!bool) {
        return bool;
      }
    }
    else
    {
      return false;
    }
    setReadOnly(MediaSet.PROP_HIDDEN_MEDIA_COUNT, Integer.valueOf(this.m_HiddenMedia.size()));
    return bool;
  }
  
  private void scheduleCommitMediaSync()
  {
    if (this.m_IsDelayedMediaSyncCommitScheduled) {
      return;
    }
    this.m_IsDelayedMediaSyncCommitScheduled = HandlerUtils.post(this, new CommitMediaSyncRunnable(null), 500L);
  }
  
  private boolean setContainsHiddenMediaProp(boolean paramBoolean)
  {
    boolean bool = this.m_ContainsHiddenMedia;
    if (bool != paramBoolean)
    {
      this.m_ContainsHiddenMedia = paramBoolean;
      if (!paramBoolean)
      {
        localIterator = this.m_HiddenMedia.iterator();
        while (localIterator.hasNext()) {
          removeMedia((Media)localIterator.next(), false);
        }
      }
    }
    else
    {
      return false;
    }
    Iterator localIterator = this.m_HiddenMedia.iterator();
    while (localIterator.hasNext()) {
      addMedia((Media)localIterator.next(), false);
    }
    commitMediaSync();
    return notifyPropertyChanged(PROP_CONTAINS_HIDDEN_MEDIA, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
  }
  
  private void setupMedia()
  {
    if (Handle.isValid(this.m_MediaChangeCBHandle)) {
      if (!Handle.isValid(this.m_MediaIterationClientHandle)) {
        break label42;
      }
    }
    for (;;)
    {
      if (Handle.isValid(this.m_MediaIterationClientHandle)) {
        break label53;
      }
      return;
      this.m_MediaChangeCBHandle = onPrepareMediaChangeCallback();
      break;
      label42:
      this.m_MediaIterationClientHandle = onPrepareMediaIterationClient();
    }
    label53:
    this.m_Source.scheduleMediaIteration(0);
  }
  
  private boolean shouldContainsMediaInternal(Media paramMedia, int paramInt)
  {
    if (this.m_TargetMediaType == null) {}
    while (paramMedia.getType() == this.m_TargetMediaType) {
      return shouldContainsMedia(paramMedia, paramInt);
    }
    return false;
  }
  
  private void updateCoverMediaList()
  {
    this.m_CoverMediaList.clear();
    if (this.m_Media.size() <= 12)
    {
      this.m_CoverMediaList.addAll(this.m_Media);
      Collections.sort(this.m_CoverMediaList, COVER_MEDIA_COMPARATOR);
    }
    for (;;)
    {
      updateCoverHashCode();
      return;
      Iterator localIterator = this.m_Media.iterator();
      while (localIterator.hasNext())
      {
        Media localMedia = (Media)localIterator.next();
        int i = Collections.binarySearch(this.m_CoverMediaList, localMedia, COVER_MEDIA_COMPARATOR) ^ 0xFFFFFFFF;
        if ((i >= 0) && (i < 12)) {
          this.m_CoverMediaList.add(i, localMedia);
        }
      }
    }
  }
  
  protected boolean addMedia(Media paramMedia, boolean paramBoolean)
  {
    if (paramMedia != null)
    {
      if (!paramMedia.isVisible()) {
        break label47;
      }
      if (this.m_Media.contains(paramMedia)) {
        break label62;
      }
      if (!this.m_PendingRemovingMedia.remove(paramMedia)) {
        break label64;
      }
      label39:
      if (paramBoolean) {
        break label79;
      }
    }
    for (;;)
    {
      return true;
      return false;
      label47:
      addHiddenMedia(paramMedia);
      if (this.m_ContainsHiddenMedia) {
        break;
      }
      return false;
      label62:
      return false;
      label64:
      if (this.m_PendingAddingMedia.add(paramMedia)) {
        break label39;
      }
      return false;
      label79:
      commitMediaSync();
    }
  }
  
  protected void addMediaToMediaLists(Media paramMedia)
  {
    int i = this.m_OpenedMediaLists.size() - 1;
    while (i >= 0)
    {
      ((MediaListImpl)this.m_OpenedMediaLists.get(i)).addMedia(paramMedia);
      i -= 1;
    }
  }
  
  protected void addMediaToMediaLists(Collection<Media> paramCollection)
  {
    MediaListImpl localMediaListImpl = null;
    if (paramCollection == null) {}
    while (this.m_OpenedMediaLists.isEmpty()) {
      return;
    }
    ArrayList localArrayList = new ArrayList(paramCollection);
    int i = this.m_OpenedMediaLists.size() - 1;
    paramCollection = localMediaListImpl;
    if (i >= 0)
    {
      localMediaListImpl = (MediaListImpl)this.m_OpenedMediaLists.get(i);
      if (localMediaListImpl.getComparator() == paramCollection) {}
      for (;;)
      {
        localMediaListImpl.addMedia(localArrayList, true);
        i -= 1;
        break;
        paramCollection = localMediaListImpl.getComparator();
        Collections.sort(localArrayList, paramCollection);
      }
    }
  }
  
  protected boolean canSyncMediaBeforeMediaTableReady()
  {
    return true;
  }
  
  protected void checkMediaPositionInMediaLists(Media paramMedia, int paramInt)
  {
    int i = this.m_OpenedMediaLists.size() - 1;
    if (i >= 0)
    {
      MediaListImpl localMediaListImpl = (MediaListImpl)this.m_OpenedMediaLists.get(i);
      if ((localMediaListImpl.getComparator().getEffectiveMediaUpdateFlags() & paramInt) == 0) {}
      for (;;)
      {
        i -= 1;
        break;
        localMediaListImpl.checkMediaIndex(paramMedia);
      }
    }
  }
  
  protected void clearExtras()
  {
    this.m_Extra = null;
  }
  
  protected void clearMedia()
  {
    verifyAccess();
    if (!this.m_Media.isEmpty())
    {
      int i = this.m_PhotoCount;
      int j = this.m_VideoCount;
      this.m_PendingAddingMedia.clear();
      this.m_PendingRemovingMedia.clear();
      this.m_Media.clear();
      this.m_CoverMediaList.clear();
      this.m_PhotoCount = 0;
      this.m_VideoCount = 0;
      notifyPropertyChanged(PROP_PHOTO_COUNT, Integer.valueOf(i), Integer.valueOf(this.m_PhotoCount));
      notifyPropertyChanged(PROP_VIDEO_COUNT, Integer.valueOf(j), Integer.valueOf(this.m_VideoCount));
      setReadOnly(PROP_MEDIA_COUNT, null);
      updateCoverHashCode();
      return;
    }
  }
  
  protected void commitMediaSync()
  {
    int i1 = 0;
    int i2 = 0;
    verifyAccess();
    int n;
    int i;
    int j;
    label53:
    label57:
    label61:
    Media localMedia;
    if (this.m_PendingRemovingMedia.isEmpty())
    {
      n = 0;
      i = 0;
      j = 0;
      if (this.m_PendingAddingMedia.isEmpty())
      {
        k = i2;
        if (n != 0) {
          break label486;
        }
        if (k != 0) {
          break label493;
        }
        if (j != 0) {
          break label500;
        }
        if (i != 0) {
          break label537;
        }
        setReadOnly(PROP_MEDIA_COUNT, Integer.valueOf(this.m_Media.size()));
      }
    }
    else
    {
      localIterator = this.m_PendingRemovingMedia.iterator();
      i = 0;
      k = 0;
      m = 0;
      j = 0;
      while (localIterator.hasNext())
      {
        localMedia = (Media)localIterator.next();
        if (this.m_Media.remove(localMedia)) {
          switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[localMedia.getType().ordinal()])
          {
          default: 
            break;
          case 2: 
            j -= 1;
          case 3: 
            for (;;)
            {
              if (k == 0) {
                break label201;
              }
              i = 1;
              break;
              m -= 1;
            }
            label201:
            if (Collections.binarySearch(this.m_CoverMediaList, localMedia, COVER_MEDIA_COMPARATOR) < 0)
            {
              i = 1;
            }
            else
            {
              i = 1;
              k = 1;
            }
            break;
          }
        }
      }
      if (i == 0) {}
      for (;;)
      {
        this.m_PendingRemovingMedia.clear();
        i = m;
        n = k;
        break;
        removeMediaFromMediaLists(this.m_PendingRemovingMedia);
      }
    }
    Iterator localIterator = this.m_PendingAddingMedia.iterator();
    int m = 0;
    int k = i1;
    for (;;)
    {
      if (localIterator.hasNext())
      {
        localMedia = (Media)localIterator.next();
        if (this.m_Media.add(localMedia)) {
          switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[localMedia.getType().ordinal()])
          {
          default: 
            break;
          case 2: 
            j += 1;
          case 3: 
            for (;;)
            {
              if (n == 0) {
                break label376;
              }
              k = 1;
              break;
              i += 1;
            }
            label376:
            k = Collections.binarySearch(this.m_CoverMediaList, localMedia, COVER_MEDIA_COMPARATOR) ^ 0xFFFFFFFF;
            if (k < 0) {}
            while (k >= 12)
            {
              k = 1;
              break;
            }
            this.m_CoverMediaList.add(k, localMedia);
            while (this.m_CoverMediaList.size() > 12) {
              this.m_CoverMediaList.remove(this.m_CoverMediaList.size() - 1);
            }
          }
        }
      }
      else
      {
        if (k == 0) {}
        for (;;)
        {
          this.m_PendingAddingMedia.clear();
          k = m;
          break;
          addMediaToMediaLists(this.m_PendingAddingMedia);
        }
        label486:
        updateCoverMediaList();
        break label53;
        label493:
        updateCoverHashCode();
        break label53;
        label500:
        this.m_PhotoCount += j;
        notifyPropertyChanged(PROP_PHOTO_COUNT, Integer.valueOf(this.m_PhotoCount - j), Integer.valueOf(this.m_PhotoCount));
        break label57;
        label537:
        this.m_VideoCount += i;
        notifyPropertyChanged(PROP_VIDEO_COUNT, Integer.valueOf(this.m_VideoCount - i), Integer.valueOf(this.m_VideoCount));
        break label61;
        k = 1;
        m = 1;
      }
    }
  }
  
  protected void completeDeletion(Handle paramHandle, boolean paramBoolean, int paramInt)
  {
    verifyAccess();
    if (paramHandle == null) {}
    while (!(paramHandle instanceof CallbackHandle)) {
      return;
    }
    if (((Boolean)get(PROP_IS_DELETING)).booleanValue())
    {
      paramHandle = (CallbackHandle)paramHandle;
      if (paramHandle.getCallback() != null) {
        break label88;
      }
      setReadOnly(PROP_IS_DELETING, Boolean.valueOf(false));
      if (paramBoolean) {
        break label104;
      }
    }
    for (;;)
    {
      onDeletionCompleted(paramBoolean, paramInt);
      if (paramBoolean) {
        break label120;
      }
      if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break label165;
      }
      return;
      return;
      label88:
      ((MediaSet.DeletionCallback)paramHandle.getCallback()).onDeletionCompleted(this, paramBoolean, paramInt);
      break;
      label104:
      this.m_CoverMediaList.clear();
      updateCoverHashCode();
    }
    label120:
    clearMedia();
    raise(EVENT_DELETED, EventArgs.EMPTY);
    paramHandle = new Intent("com.oneplus.gallery2.media.action.MEDIA_SET_DELETED");
    paramHandle.putExtra("MediaSetId", getId());
    BaseApplication.current().sendBroadcast(paramHandle);
    return;
    label165:
    commitMediaSync();
  }
  
  public boolean contains(Media paramMedia)
  {
    if (paramMedia == null) {}
    while (!this.m_Media.contains(paramMedia)) {
      return false;
    }
    return true;
  }
  
  public Handle delete(MediaSet.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {}
    while (((Boolean)get(PROP_IS_DELETING)).booleanValue()) {
      return null;
    }
    setReadOnly(PROP_IS_DELETING, Boolean.valueOf(true));
    CallbackHandle local2 = new CallbackHandle("DeleteMediaSet", paramDeletionCallback, null)
    {
      protected void onClose(int paramAnonymousInt) {}
    };
    if (paramDeletionCallback == null) {}
    for (;;)
    {
      startDeletion(local2, paramInt);
      return local2;
      paramDeletionCallback.onDeletionStarted(this, paramInt);
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_CONTAINS_HIDDEN_MEDIA)
    {
      if (paramPropertyKey != PROP_PHOTO_COUNT)
      {
        if (paramPropertyKey == PROP_VIDEO_COUNT) {
          break label43;
        }
        return (TValue)super.get(paramPropertyKey);
      }
    }
    else {
      return Boolean.valueOf(this.m_ContainsHiddenMedia);
    }
    return Integer.valueOf(this.m_PhotoCount);
    label43:
    return Integer.valueOf(this.m_VideoCount);
  }
  
  public <T> T getExtra(ExtraKey<T> paramExtraKey, T paramT)
  {
    if (this.m_Extra == null) {}
    do
    {
      return paramT;
      paramExtraKey = this.m_Extra.get(paramExtraKey.getId());
    } while (paramExtraKey == null);
    return paramExtraKey;
  }
  
  public final Handler getHandler()
  {
    return this.m_Source.getHandler();
  }
  
  protected final Iterable<Media> getMedia()
  {
    return this.m_Media;
  }
  
  protected int getNameResourceId()
  {
    return 0;
  }
  
  public <T extends MediaSource> T getSource()
  {
    return this.m_Source;
  }
  
  public MediaType getTargetMediaType()
  {
    return this.m_TargetMediaType;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return false;
  }
  
  protected void onDeletionCompleted(boolean paramBoolean, int paramInt) {}
  
  protected void onIterateMedia(Media paramMedia)
  {
    if (!shouldContainsMediaInternal(paramMedia, 0)) {
      return;
    }
    addMedia(paramMedia, false);
  }
  
  protected void onLocaleChanged(Locale paramLocale1, Locale paramLocale2)
  {
    int i = getNameResourceId();
    if (i == 0) {
      return;
    }
    setReadOnly(PROP_NAME, BaseApplication.current().getString(i));
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt)
  {
    if (!shouldContainsMediaInternal(paramMedia, paramInt)) {
      return;
    }
    if (!((Boolean)paramMedia.getSource().get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      addMedia(paramMedia, false);
      scheduleCommitMediaSync();
      return;
    }
    addMedia(paramMedia, true);
  }
  
  protected void onMediaDeleted(Media paramMedia, int paramInt)
  {
    removeHiddenMedia(paramMedia);
    if (!((Boolean)paramMedia.getSource().get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      removeMedia(paramMedia, false);
      scheduleCommitMediaSync();
      return;
    }
    removeMedia(paramMedia, true);
  }
  
  protected void onMediaIterationEnded()
  {
    long l1 = SystemClock.elapsedRealtime();
    long l2 = this.m_MediaIterationStartTime;
    Log.d(this.TAG, "onMediaIterationEnded() - Take ", Long.valueOf(l1 - l2), " ms to iterate media");
    commitMediaSync();
  }
  
  protected void onMediaIterationStarted()
  {
    this.m_MediaIterationStartTime = SystemClock.elapsedRealtime();
  }
  
  protected void onMediaTableReady()
  {
    if (!canSyncMediaBeforeMediaTableReady())
    {
      setupMedia();
      return;
    }
    commitMediaSync();
  }
  
  protected void onMediaUpdated(Media paramMedia, int paramInt)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    if (!paramMedia.getSource().isRecycledMedia(paramMedia))
    {
      if (!shouldContainsMediaInternal(paramMedia, paramInt))
      {
        removeHiddenMedia(paramMedia);
        removeMedia(paramMedia, true);
      }
    }
    else {
      return;
    }
    int i;
    if ((Media.FLAG_VISIBILITY_CHANGED & paramInt) == 0)
    {
      i = 0;
      label55:
      if (i != 0) {
        break label132;
      }
      label59:
      if (!paramMedia.isVisible()) {
        break label159;
      }
    }
    for (;;)
    {
      if (i == 0)
      {
        label72:
        if (addMedia(paramMedia, bool1)) {
          break label203;
        }
        if (!this.m_Media.contains(paramMedia)) {
          break;
        }
        checkMediaPositionInMediaLists(paramMedia, paramInt);
        if ((!this.m_CoverMediaList.contains(paramMedia)) || ((COVER_MEDIA_UPDATE_FLAGS_MASK & paramInt) == 0)) {
          break;
        }
        updateCoverHashCode();
        return;
        i = 1;
        break label55;
        label132:
        if (!paramMedia.isVisible())
        {
          addHiddenMedia(paramMedia);
          break label59;
        }
        removeHiddenMedia(paramMedia);
        break label59;
        label159:
        if (!this.m_ContainsHiddenMedia)
        {
          addHiddenMedia(paramMedia);
          if (i != 0) {
            break label213;
          }
        }
      }
    }
    label203:
    label213:
    for (bool1 = bool2;; bool1 = false)
    {
      removeMedia(paramMedia, bool1);
      if (i == 0) {
        break;
      }
      scheduleCommitMediaSync();
      return;
      bool1 = false;
      break label72;
      if (i == 0) {
        return;
      }
      scheduleCommitMediaSync();
      return;
    }
  }
  
  protected PropertyChangedCallback<Locale> onPrepareLocaleChangedCallback()
  {
    new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Locale> paramAnonymousPropertyKey, PropertyChangeEventArgs<Locale> paramAnonymousPropertyChangeEventArgs)
      {
        BaseMediaSet.this.onLocaleChanged((Locale)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Locale)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    };
  }
  
  protected Handle onPrepareMediaChangeCallback()
  {
    this.m_Source.addMediaChangedCallback(new MediaChangeCallback()
    {
      public void onMediaCreated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
      {
        BaseMediaSet.this.onMediaCreated(paramAnonymousMedia, paramAnonymousInt);
      }
      
      public void onMediaDeleted(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
      {
        BaseMediaSet.this.onMediaDeleted(paramAnonymousMedia, paramAnonymousInt);
      }
      
      public void onMediaUpdated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
      {
        BaseMediaSet.this.onMediaUpdated(paramAnonymousMedia, paramAnonymousInt);
      }
    });
  }
  
  protected Iterable<Media> onPrepareMediaForMediaList()
  {
    return this.m_Media;
  }
  
  protected Handle onPrepareMediaIterationClient()
  {
    this.m_Source.addMediaIterationClient(new MediaIterationClient()
    {
      public void onIterate(Media paramAnonymousMedia)
      {
        BaseMediaSet.this.onIterateMedia(paramAnonymousMedia);
      }
      
      public void onIterationEnded()
      {
        BaseMediaSet.this.onMediaIterationEnded();
      }
      
      public void onIterationStarted()
      {
        BaseMediaSet.this.onMediaIterationStarted();
      }
    }, this.m_TargetMediaType);
  }
  
  protected void onRelease()
  {
    this.m_Source.removeCallback(MediaSource.PROP_IS_MEDIA_TABLE_READY, this.m_MediaTableStateChangedCB);
    if (this.m_LocaleChangedCallback == null) {}
    for (;;)
    {
      this.m_MediaChangeCBHandle = Handle.close(this.m_MediaChangeCBHandle);
      this.m_MediaIterationClientHandle = Handle.close(this.m_MediaIterationClientHandle);
      clearMedia();
      super.onRelease();
      return;
      BaseApplication.current().removeCallback(BaseApplication.PROP_LOCALE, this.m_LocaleChangedCallback);
    }
  }
  
  public MediaList openMediaList(MediaComparator paramMediaComparator, int paramInt1, int paramInt2)
  {
    Object localObject1 = null;
    verifyAccess();
    MediaListImpl localMediaListImpl;
    long l1;
    Object localObject3;
    Object localObject2;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramInt1 != 0)
      {
        localMediaListImpl = new MediaListImpl(paramMediaComparator, paramInt1);
        this.m_OpenedMediaLists.add(localMediaListImpl);
        Log.v(this.TAG, "[", getId(), "] openMediaList() - Opened media list count : ", Integer.valueOf(this.m_OpenedMediaLists.size()));
        l1 = SystemClock.elapsedRealtime();
        localObject3 = onPrepareMediaForMediaList();
        if (paramInt1 != 1) {
          break label142;
        }
        localObject3 = ((Iterable)localObject3).iterator();
        for (;;)
        {
          if (!((Iterator)localObject3).hasNext()) {
            break label423;
          }
          localObject2 = (Media)((Iterator)localObject3).next();
          if (localObject1 != null) {
            break;
          }
          label131:
          localObject1 = localObject2;
        }
      }
    }
    else {
      return null;
    }
    return null;
    label142:
    if (!(localObject3 instanceof Collection))
    {
      localObject2 = new ArrayList();
      localObject3 = ((Iterable)localObject3).iterator();
      for (;;)
      {
        localObject1 = localObject2;
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
        ((List)localObject2).add((Media)((Iterator)localObject3).next());
      }
    }
    localObject1 = new ArrayList((Collection)localObject3);
    Collections.sort((List)localObject1, paramMediaComparator);
    if (paramInt1 <= 0)
    {
      paramMediaComparator = (MediaComparator)localObject1;
      label230:
      localMediaListImpl.addMedia(paramMediaComparator, true);
    }
    for (;;)
    {
      long l2 = SystemClock.elapsedRealtime();
      Log.v(this.TAG, "[", new Object[] { getId(), "] openMediaList() - Take ", Long.valueOf(l2 - l1), " ms to select ", Integer.valueOf(localMediaListImpl.size()), " media" });
      return localMediaListImpl;
      paramMediaComparator = (MediaComparator)localObject1;
      if (((List)localObject1).size() <= paramInt1) {
        break label230;
      }
      if (((List)localObject1).size() < paramInt1 * 2)
      {
        paramInt2 = ((List)localObject1).size();
        for (;;)
        {
          paramInt2 -= 1;
          paramMediaComparator = (MediaComparator)localObject1;
          if (paramInt2 < paramInt1) {
            break;
          }
          ((List)localObject1).remove(paramInt2);
        }
      }
      paramMediaComparator = new ArrayList(paramInt1);
      paramInt2 = 0;
      while (paramInt2 < paramInt1)
      {
        paramMediaComparator.add((Media)((List)localObject1).get(paramInt2));
        paramInt2 += 1;
      }
      break label230;
      if (paramMediaComparator.compare(localObject2, localObject1) < 0) {
        break label131;
      }
      break;
      label423:
      localMediaListImpl.addMedia((Media)localObject1);
    }
  }
  
  public <T> void putExtra(ExtraKey<T> paramExtraKey, T paramT)
  {
    if (paramT == null)
    {
      if (this.m_Extra != null) {}
    }
    else
    {
      if (this.m_Extra != null) {}
      for (;;)
      {
        this.m_Extra.put(paramExtraKey.getId(), paramT);
        return;
        this.m_Extra = new LongSparseArray();
      }
    }
    this.m_Extra.delete(paramExtraKey.getId());
  }
  
  protected boolean removeMedia(Media paramMedia, boolean paramBoolean)
  {
    if (paramMedia != null)
    {
      if (this.m_PendingAddingMedia.remove(paramMedia)) {
        break label51;
      }
      if (!this.m_Media.contains(paramMedia)) {
        break label53;
      }
      if (!this.m_PendingRemovingMedia.add(paramMedia)) {
        break label55;
      }
      if (paramBoolean) {
        break label57;
      }
    }
    label51:
    label53:
    label55:
    label57:
    while (((Boolean)get(PROP_IS_DELETING)).booleanValue())
    {
      return true;
      return false;
      return true;
      return false;
      return false;
    }
    commitMediaSync();
    return true;
  }
  
  protected void removeMediaFromMediaLists(Media paramMedia)
  {
    int i = this.m_OpenedMediaLists.size() - 1;
    if (i >= 0)
    {
      MediaListImpl localMediaListImpl = (MediaListImpl)this.m_OpenedMediaLists.get(i);
      if (!localMediaListImpl.removeMedia(paramMedia)) {}
      for (;;)
      {
        i -= 1;
        break;
        if ((localMediaListImpl.getMaxMediaCount() > 0) && (localMediaListImpl.size() < localMediaListImpl.getMaxMediaCount()) && (localMediaListImpl.size() < this.m_Media.size())) {
          localMediaListImpl.addMedia(this.m_Media);
        }
      }
    }
  }
  
  protected void removeMediaFromMediaLists(Collection<Media> paramCollection)
  {
    if (paramCollection == null) {}
    while ((paramCollection.isEmpty()) || (this.m_OpenedMediaLists.isEmpty())) {
      return;
    }
    int i;
    MediaListImpl localMediaListImpl;
    if (!(paramCollection instanceof List))
    {
      i = this.m_OpenedMediaLists.size() - 1;
      if (i >= 0)
      {
        localMediaListImpl = (MediaListImpl)this.m_OpenedMediaLists.get(i);
        Iterator localIterator = paramCollection.iterator();
        for (boolean bool1 = false; localIterator.hasNext(); bool1 = localMediaListImpl.removeMedia((Media)localIterator.next()) | bool1) {}
      }
    }
    else
    {
      paramCollection = (List)paramCollection;
      int k = paramCollection.size();
      i = this.m_OpenedMediaLists.size() - 1;
      if (i >= 0)
      {
        localMediaListImpl = (MediaListImpl)this.m_OpenedMediaLists.get(i);
        int j = k - 1;
        boolean bool2 = false;
        while (j >= 0)
        {
          bool2 |= localMediaListImpl.removeMedia((Media)paramCollection.get(j));
          j -= 1;
        }
        if (!bool2) {}
        for (;;)
        {
          i -= 1;
          break;
          if ((localMediaListImpl.getMaxMediaCount() > 0) && (localMediaListImpl.size() < localMediaListImpl.getMaxMediaCount()) && (localMediaListImpl.size() < this.m_Media.size())) {
            localMediaListImpl.addMedia(this.m_Media);
          }
        }
        if (j == 0) {}
        for (;;)
        {
          i -= 1;
          break;
          if ((localMediaListImpl.getMaxMediaCount() > 0) && (localMediaListImpl.size() < localMediaListImpl.getMaxMediaCount()) && (localMediaListImpl.size() < this.m_Media.size())) {
            localMediaListImpl.addMedia(this.m_Media);
          }
        }
      }
    }
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_CONTAINS_HIDDEN_MEDIA) {
      return super.set(paramPropertyKey, paramTValue);
    }
    return setContainsHiddenMediaProp(((Boolean)paramTValue).booleanValue());
  }
  
  protected abstract boolean shouldContainsMedia(Media paramMedia, int paramInt);
  
  protected abstract void startDeletion(Handle paramHandle, int paramInt);
  
  public String toString()
  {
    return getId();
  }
  
  protected void updateCoverHashCode()
  {
    StringBuilder localStringBuilder;
    int i;
    Object localObject;
    if (!((Boolean)get(PROP_IS_DELETING)).booleanValue())
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append('[');
      localStringBuilder.append(getId());
      localStringBuilder.append(']');
      int j = this.m_CoverMediaList.size();
      i = 0;
      if (i >= j) {
        break label166;
      }
      localObject = (Media)this.m_CoverMediaList.get(i);
      if ((localObject instanceof GroupedMedia)) {
        break label133;
      }
      label89:
      if (i > 0) {
        break label155;
      }
    }
    for (;;)
    {
      localStringBuilder.append(((Media)localObject).getId());
      localStringBuilder.append(':');
      localStringBuilder.append(((Media)localObject).getLastModifiedTime());
      i += 1;
      break;
      return;
      label133:
      Media localMedia = ((GroupedMedia)localObject).getCover();
      if (localMedia == null) {
        break label89;
      }
      localObject = localMedia;
      break label89;
      label155:
      localStringBuilder.append(',');
    }
    label166:
    setReadOnly(PROP_COVER_HASH_CODE, localStringBuilder.toString());
  }
  
  private final class CommitMediaSyncRunnable
    implements Runnable
  {
    private CommitMediaSyncRunnable() {}
    
    public void run()
    {
      BaseMediaSet.this.m_IsDelayedMediaSyncCommitScheduled = false;
      BaseMediaSet.this.commitMediaSync();
    }
  }
  
  private final class MediaListImpl
    extends BaseMediaList
  {
    public MediaListImpl(MediaComparator paramMediaComparator, int paramInt)
    {
      super(paramInt);
    }
    
    public void release()
    {
      verifyAccess();
      BaseMediaSet.this.onMediaListReleased(this);
      super.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */