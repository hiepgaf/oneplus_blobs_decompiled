package com.oneplus.gallery2.media;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import java.util.ArrayList;
import java.util.List;

public final class AllMediaMediaSet
  extends MultiSourcesMediaSet
{
  private final List<String> m_CameraDirectoryPaths = new ArrayList();
  private boolean m_IgnoreMediaCount;
  
  public AllMediaMediaSet(MediaType paramMediaType)
  {
    super(null, paramMediaType);
    if (!areAllMediaTablesReady()) {
      bool = true;
    }
    this.m_IgnoreMediaCount = bool;
  }
  
  protected boolean canSyncMediaBeforeMediaTableReady()
  {
    return true;
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    Object localObject;
    int i;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramMedia == null) {
        break label72;
      }
      if (this.m_CameraDirectoryPaths.isEmpty()) {
        break label83;
      }
      localObject = paramMedia.getFilePath();
      if (localObject != null) {
        break label116;
      }
      i = 0;
    }
    for (;;)
    {
      if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) == 0) {}
      for (;;)
      {
        return paramMedia.delete(paramDeletionCallback, i);
        return null;
        label72:
        Log.e(this.TAG, "delete() - No media to delete");
        return null;
        label83:
        localObject = (MediaStoreDirectoryManager)BaseApplication.current().findComponent(MediaStoreDirectoryManager.class);
        if (localObject == null) {
          break;
        }
        ((MediaStoreDirectoryManager)localObject).getSystemDirectoryPaths(MediaStoreDirectoryManager.SystemDirectoryType.CAMERA, this.m_CameraDirectoryPaths);
        break;
        label116:
        int j = ((String)localObject).length();
        i = this.m_CameraDirectoryPaths.size() - 1;
        label136:
        if (i < 0) {
          break label226;
        }
        String str = (String)this.m_CameraDirectoryPaths.get(i);
        int k = str.length();
        if (!((String)localObject).startsWith(str)) {}
        for (;;)
        {
          i -= 1;
          break label136;
          if (j == k) {}
          while (((String)localObject).charAt(k) == '/')
          {
            i = Media.FLAG_INCLUDE_RAW_PHOTO | 0x0;
            break;
          }
        }
        i |= Media.FLAG_MOVE_TO_RECYCE_BIN;
      }
      label226:
      i = 0;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_MEDIA_COUNT) {}
    while (!this.m_IgnoreMediaCount) {
      return (TValue)super.get(paramPropertyKey);
    }
    return null;
  }
  
  public String getId()
  {
    return "AllMedia";
  }
  
  protected int getNameResourceId()
  {
    Resources localResources = BaseApplication.current().getResources();
    String str;
    if (getTargetMediaType() != null) {
      switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[getTargetMediaType().ordinal()])
      {
      default: 
        str = "media_set_name_all";
      }
    }
    for (;;)
    {
      return localResources.getIdentifier(str, "string", "com.oneplus.gallery");
      str = "media_set_name_all";
      continue;
      str = "media_set_name_all_photos";
      continue;
      str = "media_set_name_all_videos";
    }
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.SYSTEM;
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  protected void onAllMediaTablesReady()
  {
    this.m_IgnoreMediaCount = false;
    super.onAllMediaTablesReady();
    commitMediaSync();
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_MEDIA_COUNT) {
      return super.setReadOnly(paramPropertyKey, paramTValue);
    }
    if (paramTValue == null) {}
    while ((((Integer)paramTValue).intValue() != 0) || (areAllMediaTablesReady()))
    {
      this.m_IgnoreMediaCount = false;
      break;
    }
    return false;
  }
  
  public boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    return (Media.FLAG_SUB_MEDIA & paramInt) == 0;
  }
  
  protected void startDeletion(Handle paramHandle, int paramInt)
  {
    completeDeletion(paramHandle, false, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/AllMediaMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */