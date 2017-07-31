package com.oneplus.gallery2.media;

import android.net.Uri;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.gallery2.MediaContentThread;
import com.oneplus.io.Path;
import com.oneplus.util.CollectionUtils;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;

public class DirectoryMediaSet
  extends BaseMediaSet
{
  private final long m_DirectoryId;
  private MediaStoreDirectoryManager m_DirectoryManager;
  private String m_DirectoryPath;
  private final String m_Id;
  private final boolean m_IsDefault;
  private boolean m_IsVisible = true;
  private long m_LastAddedTimeInMediaStore;
  
  public DirectoryMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource, MediaStoreDirectoryManager paramMediaStoreDirectoryManager, long paramLong, MediaType paramMediaType)
  {
    this(paramMediaStoreMediaSource, paramMediaStoreDirectoryManager, false, paramLong, null, null, paramMediaType);
  }
  
  DirectoryMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource, MediaStoreDirectoryManager paramMediaStoreDirectoryManager, boolean paramBoolean, long paramLong, String paramString, GalleryDatabase.ExtraDirectoryInfo paramExtraDirectoryInfo, MediaType paramMediaType)
  {
    super(paramMediaStoreMediaSource, paramMediaType);
    if (paramMediaStoreDirectoryManager != null) {
      if (paramLong < 0L) {
        break label66;
      }
    }
    label66:
    for (int i = 1; i == 0; i = 0)
    {
      throw new IllegalArgumentException("Invalid ID : " + paramLong);
      throw new IllegalArgumentException("No directory manager");
    }
    this.m_DirectoryManager = paramMediaStoreDirectoryManager;
    this.m_DirectoryId = paramLong;
    this.m_Id = ("Directory:" + paramLong);
    this.m_IsDefault = paramBoolean;
    onMediaIterationStarted();
    paramMediaStoreMediaSource = paramMediaStoreDirectoryManager.getMedia(paramLong, paramMediaType).iterator();
    while (paramMediaStoreMediaSource.hasNext()) {
      onIterateMedia((Media)paramMediaStoreMediaSource.next());
    }
    onMediaIterationEnded();
    label166:
    label177:
    long l;
    if (paramString != null)
    {
      if (paramString != null) {
        break label225;
      }
      this.m_DirectoryPath = paramString;
      if (paramExtraDirectoryInfo == null) {
        break label241;
      }
      l = this.m_LastAddedTimeInMediaStore;
      if (paramExtraDirectoryInfo != null) {
        break label252;
      }
    }
    for (;;)
    {
      setReadOnly(PROP_LAST_MEDIA_ADDED_TIME, Long.valueOf(l));
      if (paramExtraDirectoryInfo != null) {
        break label267;
      }
      paramMediaStoreDirectoryManager.onDirectoryMediaSetCreated(paramLong, this);
      return;
      paramString = paramMediaStoreDirectoryManager.getDirectoryPath(paramLong);
      break;
      label225:
      setReadOnly(PROP_NAME, Path.getFileName(paramString));
      break label166;
      label241:
      paramExtraDirectoryInfo = paramMediaStoreDirectoryManager.getExtraDirectoryInfo(paramLong);
      break label177;
      label252:
      l = Math.max(l, paramExtraDirectoryInfo.mediaAddedTime);
    }
    label267:
    paramMediaStoreMediaSource = PROP_IS_VISIBLE;
    if ((paramExtraDirectoryInfo.oneplusFlags & 0x20) == 0L) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setReadOnly(paramMediaStoreMediaSource, Boolean.valueOf(paramBoolean));
      break;
    }
  }
  
  private boolean setIsVisibleProp(boolean paramBoolean)
  {
    boolean bool = this.m_IsVisible;
    if (bool != paramBoolean)
    {
      verifyAccess();
      if (isVisibilityChangeSupported())
      {
        this.m_IsVisible = paramBoolean;
        this.m_DirectoryManager.updateVisibility(this, paramBoolean);
        return notifyPropertyChanged(PROP_IS_VISIBLE, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      }
    }
    else
    {
      return false;
    }
    return false;
  }
  
  protected void completeDeletion(Handle paramHandle, boolean paramBoolean, int paramInt)
  {
    if (!paramBoolean) {}
    for (;;)
    {
      super.completeDeletion(paramHandle, paramBoolean, paramInt);
      if (paramBoolean) {
        break;
      }
      return;
      if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          String str = DirectoryMediaSet.this.m_DirectoryPath;
          try
          {
            File localFile = new File(str);
            if (!localFile.exists()) {
              return;
            }
            if ((localFile.isDirectory()) && (localFile.delete()))
            {
              Log.v(DirectoryMediaSet.this.TAG, "completeDeletion() -  Directory ", str, " deleted");
              ((MediaStoreMediaSource)DirectoryMediaSet.this.getSource()).deleteFromMediaStore("_id=" + DirectoryMediaSet.this.m_DirectoryId, null, null);
              if (GalleryDatabase.deleteExtraDirectoryInfo(DirectoryMediaSet.this.m_DirectoryId))
              {
                Log.d(DirectoryMediaSet.this.TAG, "completeDeletion() - Extra info of " + DirectoryMediaSet.this.m_DirectoryId + " deleted");
                return;
              }
            }
          }
          catch (Throwable localThrowable)
          {
            Log.e(DirectoryMediaSet.this.TAG, "completeDeletion() - Fail to delete directory " + str, localThrowable);
          }
        }
      })) {
        Log.w(this.TAG, "completeDeletion() - Fail to post to media content thread");
      }
    }
    release();
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    int i = 0;
    if (paramMedia != null) {
      if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break label38;
      }
    }
    label38:
    for (paramInt = i;; paramInt = Media.FLAG_MOVE_TO_RECYCE_BIN | 0x0)
    {
      return paramMedia.delete(paramDeletionCallback, paramInt);
      Log.e(this.TAG, "delete() - No media to delete");
      return null;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return (TValue)super.get(paramPropertyKey);
    }
    return Boolean.valueOf(this.m_IsVisible);
  }
  
  public final long getDirectoryId()
  {
    return this.m_DirectoryId;
  }
  
  public final String getDirectoryPath()
  {
    return this.m_DirectoryPath;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.APPLICATION;
  }
  
  final boolean isDefault()
  {
    return this.m_IsDefault;
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  protected void onDeletionCompleted(boolean paramBoolean, int paramInt)
  {
    ((MediaStoreMediaSource)getSource()).notifyMediaSetDeleted(this, (Media[])CollectionUtils.toArray(getMedia(), Media.class));
    super.onDeletionCompleted(paramBoolean, paramInt);
  }
  
  protected void onDirectoryRenamed(String paramString1, String paramString2)
  {
    this.m_DirectoryPath = paramString2;
    setReadOnly(PROP_NAME, Path.getFileName(paramString2));
  }
  
  final void onExtraDirectoryInfoUpdated(GalleryDatabase.ExtraDirectoryInfo paramExtraDirectoryInfo)
  {
    if (paramExtraDirectoryInfo == null) {
      return;
    }
    setReadOnly(PROP_LAST_MEDIA_ADDED_TIME, Long.valueOf(Math.max(this.m_LastAddedTimeInMediaStore, paramExtraDirectoryInfo.mediaAddedTime)));
    PropertyKey localPropertyKey = PROP_IS_VISIBLE;
    if ((paramExtraDirectoryInfo.oneplusFlags & 0x20) == 0L) {}
    for (boolean bool = true;; bool = false)
    {
      setReadOnly(localPropertyKey, Boolean.valueOf(bool));
      return;
    }
  }
  
  protected void onIterateMedia(Media paramMedia)
  {
    int i = 0;
    super.onIterateMedia(paramMedia);
    if (!(paramMedia instanceof MediaStoreItem)) {}
    long l;
    do
    {
      return;
      l = ((MediaStoreItem)paramMedia).getAddedTime();
      if (l <= this.m_LastAddedTimeInMediaStore) {
        i = 1;
      }
    } while (i != 0);
    this.m_LastAddedTimeInMediaStore = l;
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt)
  {
    super.onMediaCreated(paramMedia, paramInt);
    long l;
    if (!((Boolean)getSource().get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      if ((paramMedia instanceof MediaStoreItem)) {
        break label77;
      }
      l = 0L;
      if (l > 0L) {
        break label121;
      }
    }
    label77:
    label119:
    label121:
    for (paramInt = 1;; paramInt = 0)
    {
      if (paramInt == 0)
      {
        setReadOnly(PROP_LAST_MEDIA_ADDED_TIME, Long.valueOf(l));
        this.m_DirectoryManager.updateLastMediaAddedTime(this, l);
      }
      return;
      l = System.currentTimeMillis();
      break;
      l = ((MediaStoreItem)paramMedia).getAddedTime();
      if (l > ((Long)get(PROP_LAST_MEDIA_ADDED_TIME)).longValue()) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if (paramInt != 0) {
          break label119;
        }
        l = 0L;
        break;
      }
      break;
    }
  }
  
  protected void onMediaIterationStarted()
  {
    super.onMediaIterationStarted();
    this.m_LastAddedTimeInMediaStore = 0L;
  }
  
  protected PropertyChangedCallback<Locale> onPrepareLocaleChangedCallback()
  {
    return null;
  }
  
  protected Handle onPrepareMediaChangeCallback()
  {
    return null;
  }
  
  protected Handle onPrepareMediaIterationClient()
  {
    return null;
  }
  
  protected void onRelease()
  {
    verifyAccess();
    this.m_DirectoryManager.onDirectoryMediaSetReleased(this.m_DirectoryId, this);
    super.onRelease();
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return super.set(paramPropertyKey, paramTValue);
    }
    return setIsVisibleProp(((Boolean)paramTValue).booleanValue());
  }
  
  protected boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_SUB_MEDIA & paramInt) == 0)
    {
      if ((paramMedia instanceof MediaStoreItem))
      {
        if (((MediaStoreItem)paramMedia).getParentId() != this.m_DirectoryId) {
          break label38;
        }
        return true;
      }
    }
    else {
      return false;
    }
    return false;
    label38:
    return false;
  }
  
  protected void startDeletion(final Handle paramHandle, int paramInt)
  {
    String str = "(media_type=1 OR media_type=3) AND parent=" + this.m_DirectoryId;
    if (Handle.isValid(((MediaStoreMediaSource)this.m_DirectoryManager.getMediaSource()).deleteFromMediaStore(str, null, new MediaStoreMediaSource.MediaStoreAccessCallback()
    {
      public void onCompleted(Handle paramAnonymousHandle, Uri paramAnonymousUri, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        DirectoryMediaSet.this.completeDeletion(paramHandle, true, paramAnonymousInt2);
      }
    }))) {
      return;
    }
    Log.e(this.TAG, "startDeletion() - Fail to delete data from media store");
    completeDeletion(paramHandle, false, paramInt);
  }
  
  public String toString()
  {
    return "[" + this.m_DirectoryId + ", " + this.m_DirectoryPath + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/DirectoryMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */