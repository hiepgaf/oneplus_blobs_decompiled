package com.oneplus.gallery2.media;

import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;

final class BurstMediaStoreMedia
  extends BaseGroupedMedia
  implements MediaStoreItem, PhotoMedia
{
  private static final String ID_PREFIX = "MediaStore:Burst/";
  private final String m_Id;
  private boolean m_IsAvailable = true;
  private boolean m_IsParentVisible = true;
  private long m_ParentId = -1L;
  private long m_PreviousParentId = -1L;
  
  public BurstMediaStoreMedia(MediaStoreMediaSource paramMediaStoreMediaSource, String paramString)
  {
    super(paramMediaStoreMediaSource, MediaType.PHOTO);
    this.m_Id = paramString;
  }
  
  static String getId(String paramString)
  {
    int i;
    if (paramString != null)
    {
      i = paramString.lastIndexOf('/');
      if (i >= 0) {
        break label85;
      }
      i = 0;
    }
    while (i + 27 <= paramString.length())
    {
      if (paramString.charAt(i + 19) != '_') {
        break label94;
      }
      if (paramString.charAt(i + 23) != '.') {
        break label96;
      }
      j = 20;
      for (;;)
      {
        if (j >= 23) {
          break label100;
        }
        if (!Character.isDigit(paramString.charAt(i + j))) {
          break;
        }
        j += 1;
      }
      return null;
      label85:
      i += 1;
    }
    return null;
    label94:
    return null;
    label96:
    return null;
    return null;
    label100:
    if (paramString.charAt(i) != 'I') {}
    while ((paramString.charAt(i + 1) != 'M') || (paramString.charAt(i + 2) != 'G') || (paramString.charAt(i + 3) != '_')) {
      return null;
    }
    if (paramString.charAt(i + 12) == '_')
    {
      j = 4;
      for (;;)
      {
        if (j >= 12) {
          break label193;
        }
        if (!Character.isDigit(paramString.charAt(i + j))) {
          break;
        }
        j += 1;
      }
    }
    return null;
    return null;
    label193:
    int j = 13;
    while (j < 19) {
      if (Character.isDigit(paramString.charAt(i + j))) {
        j += 1;
      } else {
        return null;
      }
    }
    return "MediaStore:Burst/" + paramString.substring(0, i + 19);
  }
  
  static boolean isValidId(String paramString)
  {
    if (paramString == null) {}
    while (!paramString.startsWith("MediaStore:Burst/")) {
      return false;
    }
    return true;
  }
  
  private Media selectCoverMedia()
  {
    int i = getSubMediaCount();
    long l;
    Object localObject;
    Media localMedia;
    if (i > 0)
    {
      l = 0L;
      i -= 1;
      localObject = null;
      if (i < 0) {
        break label81;
      }
      localMedia = getSubMedia(i);
      if (localObject != null) {
        break label55;
      }
      l = localMedia.getFileSize();
      localObject = localMedia;
    }
    label55:
    label79:
    for (;;)
    {
      i -= 1;
      break;
      return null;
      if (localMedia.getFileSize() <= l) {}
      for (int j = 1;; j = 0)
      {
        if (j != 0) {
          break label79;
        }
        break;
      }
    }
    label81:
    if (localObject == null) {
      return getSubMedia(0);
    }
    return (Media)localObject;
  }
  
  public Handle checkAnimatable(PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
  {
    if (paramCheckAnimatableCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("CheckAnimatable");
      paramCheckAnimatableCallback.onChecked(this, false);
    }
  }
  
  protected Handle deleteGroupedMediaItself(Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
      return ((MediaStoreMediaSource)getSource()).recycleMedia(this, paramDeletionCallback, paramInt);
    }
    return ((MediaStoreMediaSource)getSource()).deleteMedia(this, paramDeletionCallback, paramInt);
  }
  
  public long getAddedTime()
  {
    MediaStoreMedia localMediaStoreMedia = (MediaStoreMedia)getCover();
    if (localMediaStoreMedia == null) {
      return 0L;
    }
    return localMediaStoreMedia.getAddedTime();
  }
  
  public PhotoMedia getEncodedMedia()
  {
    return null;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  public long getParentId()
  {
    return this.m_ParentId;
  }
  
  public long getPreviousParentId()
  {
    return this.m_PreviousParentId;
  }
  
  public PhotoMedia getRawMedia()
  {
    return null;
  }
  
  public boolean isAvailable()
  {
    return this.m_IsAvailable;
  }
  
  public boolean isBokeh()
  {
    return false;
  }
  
  public boolean isBurstGroup()
  {
    return true;
  }
  
  public boolean isPanorama()
  {
    return false;
  }
  
  public boolean isParentVisible()
  {
    return this.m_IsParentVisible;
  }
  
  public boolean isRaw()
  {
    return false;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  public boolean isVisible()
  {
    if (!super.isVisible()) {}
    while (!this.m_IsParentVisible) {
      return false;
    }
    return true;
  }
  
  public void notifyParentVisibilityChanged(boolean paramBoolean)
  {
    verifyAccess();
    if (this.m_IsParentVisible != paramBoolean)
    {
      this.m_IsParentVisible = paramBoolean;
      if (super.isVisible()) {}
    }
    else
    {
      return;
    }
    ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, FLAG_VISIBILITY_CHANGED);
  }
  
  protected void onSubMediaChanged()
  {
    setCover(selectCoverMedia());
    super.onSubMediaChanged();
  }
  
  protected int onUpdate(Media paramMedia)
  {
    int j = super.onUpdate(paramMedia);
    long l;
    if (!(paramMedia instanceof MediaStoreItem))
    {
      l = -1L;
      if (this.m_ParentId == l) {
        break label77;
      }
      this.m_PreviousParentId = this.m_ParentId;
      this.m_ParentId = l;
      if (this.m_PreviousParentId >= 0L) {
        break label79;
      }
    }
    label77:
    label79:
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        return j;
      }
      return MediaStoreMedia.FLAG_PARENT_ID_CHANGED | j;
      l = ((MediaStoreItem)paramMedia).getParentId();
      break;
      return j;
    }
    return j;
  }
  
  public Boolean peekIsAnimatable()
  {
    return Boolean.valueOf(false);
  }
  
  void release()
  {
    this.m_IsAvailable = false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BurstMediaStoreMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */