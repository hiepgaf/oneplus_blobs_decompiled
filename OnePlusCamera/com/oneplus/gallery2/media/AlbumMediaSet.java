package com.oneplus.gallery2.media;

import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyChangedCallback;
import java.util.Iterator;
import java.util.Locale;

public class AlbumMediaSet
  extends VirtualMediaSet
{
  private final long m_AlbumId;
  private final AlbumManager m_AlbumManager;
  private final String m_Id;
  
  AlbumMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource, AlbumManager paramAlbumManager, GalleryDatabase.AlbumInfo paramAlbumInfo, MediaType paramMediaType)
  {
    super(paramMediaStoreMediaSource, paramMediaType);
    this.m_AlbumId = paramAlbumInfo.albumId;
    this.m_Id = ("Album/" + this.m_AlbumId);
    this.m_AlbumManager = paramAlbumManager;
    setReadOnly(PROP_NAME, paramAlbumInfo.name);
    setReadOnly(PROP_LAST_MEDIA_ADDED_TIME, Long.valueOf(paramAlbumInfo.lastMediaAddedTime));
    onMediaIterationStarted();
    paramMediaStoreMediaSource = paramAlbumManager.getMedia(paramAlbumInfo.albumId, paramMediaType).iterator();
    while (paramMediaStoreMediaSource.hasNext()) {
      onIterateMedia((Media)paramMediaStoreMediaSource.next());
    }
    onMediaIterationEnded();
  }
  
  public long getAlbumId()
  {
    return this.m_AlbumId;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.USER;
  }
  
  public boolean isVirtual()
  {
    return true;
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt)
  {
    super.onMediaCreated(paramMedia, paramInt);
    long l;
    if (!((Boolean)getSource().get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue())
    {
      if ((paramMedia instanceof MediaStoreItem)) {
        break label80;
      }
      l = 0L;
      if (l > 0L) {
        break label124;
      }
    }
    label80:
    label122:
    label124:
    for (paramInt = 1;; paramInt = 0)
    {
      if (paramInt == 0)
      {
        setReadOnly(PROP_LAST_MEDIA_ADDED_TIME, Long.valueOf(l));
        this.m_AlbumManager.updateLastMediaAddedTime(this.m_AlbumId, l);
      }
      return;
      l = System.currentTimeMillis();
      break;
      l = ((MediaStoreItem)paramMedia).getAddedTime();
      if (l > ((Long)get(PROP_LAST_MEDIA_ADDED_TIME)).longValue()) {}
      for (paramInt = 1;; paramInt = 0)
      {
        if (paramInt != 0) {
          break label122;
        }
        l = 0L;
        break;
      }
      break;
    }
  }
  
  protected void onMediaIterationEnded()
  {
    if (!MediaSets.isEmpty(this)) {}
    for (;;)
    {
      super.onMediaIterationEnded();
      return;
      if (((Integer)get(PROP_HIDDEN_MEDIA_COUNT)).intValue() > 0) {
        updateCoverHashCode();
      }
    }
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
  
  void onRenamed(String paramString1, String paramString2)
  {
    setReadOnly(PROP_NAME, paramString2);
  }
  
  protected boolean removeMediaFromSet(Media paramMedia)
  {
    return paramMedia.removeFromAlbum(getAlbumId(), 0);
  }
  
  public Handle rename(String paramString, RenameCallback paramRenameCallback)
  {
    verifyAccess();
    String str = (String)get(PROP_NAME);
    if (!this.m_AlbumManager.renameAlbum(this.m_AlbumId, paramString)) {
      return null;
    }
    if (paramRenameCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("RenameAlbum");
      paramRenameCallback.onRenameCompleted(this, true, str, paramString, 0);
    }
  }
  
  protected void startDeletion(Handle paramHandle, int paramInt)
  {
    completeDeletion(paramHandle, this.m_AlbumManager.deleteAlbum(this.m_AlbumId), paramInt);
  }
  
  public static abstract class RenameCallback
  {
    public void onRenameCompleted(AlbumMediaSet paramAlbumMediaSet, boolean paramBoolean, String paramString1, String paramString2, int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/AlbumMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */