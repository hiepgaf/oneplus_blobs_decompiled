package com.oneplus.gallery2.media;

import android.util.Log;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class MultiSourcesVirtualMediaSet
  extends MultiSourcesMediaSet
{
  private final Set<Media> m_RecycledMedia = new HashSet();
  
  protected MultiSourcesVirtualMediaSet(Collection<MediaSource> paramCollection, MediaType paramMediaType)
  {
    super(paramCollection, paramMediaType);
  }
  
  private void restoreFromRecycleBin(MediaRecyclingHandle paramMediaRecyclingHandle)
  {
    verifyAccess();
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (this.m_RecycledMedia.remove(paramMediaRecyclingHandle.getMedia()))
      {
        onMediaRestoringFromRecycleBin(paramMediaRecyclingHandle.getMedia(), 0);
        addMedia(paramMediaRecyclingHandle.getMedia(), true);
      }
    }
    else {
      return;
    }
    Log.e(this.TAG, "restoreFromRecycleBin() - " + paramMediaRecyclingHandle.getMedia() + " is not in recycle bin");
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    int i;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramMedia == null) {
        break label94;
      }
      if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break label96;
      }
      i = 0;
      if (!contains(paramMedia)) {
        break label102;
      }
      label43:
      if (paramDeletionCallback != null) {
        break label125;
      }
      label47:
      if (i != 0) {
        break label134;
      }
      if (!removeMediaFromSet(paramMedia)) {
        break label186;
      }
      this.m_RecycledMedia.remove(paramMedia);
      removeMedia(paramMedia, true);
      if (paramDeletionCallback != null) {
        break label211;
      }
    }
    for (;;)
    {
      return new EmptyHandle("DeleteMediaFromVirtualMediaSet");
      return null;
      label94:
      return null;
      label96:
      i = 1;
      break;
      label102:
      if (i != 0) {}
      while (!this.m_RecycledMedia.contains(paramMedia)) {
        return null;
      }
      break label43;
      label125:
      paramDeletionCallback.onDeletionStarted(paramMedia, paramInt);
      break label47;
      label134:
      MediaRecyclingHandle localMediaRecyclingHandle = new MediaRecyclingHandle(paramMedia);
      this.m_RecycledMedia.add(paramMedia);
      removeMedia(paramMedia, true);
      onMediaMovedToRecycleBin(paramMedia, 0);
      if (paramDeletionCallback == null) {
        return localMediaRecyclingHandle;
      }
      paramDeletionCallback.onDeletionCompleted(paramMedia, true, paramInt);
      return localMediaRecyclingHandle;
      label186:
      Log.e(this.TAG, "deleteMedia() - Fail to remove media from this set");
      if (paramDeletionCallback == null) {
        return null;
      }
      paramDeletionCallback.onDeletionCompleted(paramMedia, false, paramInt);
      return null;
      label211:
      paramDeletionCallback.onDeletionCompleted(paramMedia, true, paramInt);
    }
  }
  
  public boolean isVirtual()
  {
    return true;
  }
  
  protected void onMediaMovedToRecycleBin(Media paramMedia, int paramInt) {}
  
  protected void onMediaRestoringFromRecycleBin(Media paramMedia, int paramInt) {}
  
  protected abstract boolean removeMediaFromSet(Media paramMedia);
  
  protected boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    boolean bool = false;
    if (!this.m_RecycledMedia.contains(paramMedia)) {
      bool = true;
    }
    return bool;
  }
  
  private final class MediaRecyclingHandle
    extends MediaHandle
  {
    public MediaRecyclingHandle(Media paramMedia)
    {
      super(paramMedia, 0);
    }
    
    protected void onClose(int paramInt)
    {
      MultiSourcesVirtualMediaSet.this.restoreFromRecycleBin(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MultiSourcesVirtualMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */