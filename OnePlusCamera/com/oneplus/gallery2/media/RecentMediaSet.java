package com.oneplus.gallery2.media;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.gallery2.GalleryApplication;

public class RecentMediaSet
  extends BaseMediaSet
{
  private static final long DAY_IN_MILLISEC = 86400000L;
  private static final String PREF_RECENT_MEDIA_SET_BASE_TIME = "RecentMediaSetBaseTime";
  private static final String TAG = "RecentMediaSet";
  private long m_BaseTimeInMillis = 0L;
  private final boolean m_ExcludeTodayTakenMedia;
  private MediaStoreDirectoryManager m_MediaStoreDirectoryManager;
  
  public RecentMediaSet(MediaSource paramMediaSource, MediaType paramMediaType, boolean paramBoolean)
  {
    super(paramMediaSource, paramMediaType);
    this.m_ExcludeTodayTakenMedia = paramBoolean;
    paramMediaSource = PreferenceManager.getDefaultSharedPreferences(GalleryApplication.current());
    long l1 = System.currentTimeMillis();
    l1 -= l1 % 86400000L;
    long l2 = paramMediaSource.getLong("RecentMediaSetBaseTime", 0L);
    int i;
    if (l2 > 0L)
    {
      i = 1;
      if (i != 0) {
        break label117;
      }
    }
    label117:
    for (this.m_BaseTimeInMillis = l1;; this.m_BaseTimeInMillis = Math.max(l2, l1))
    {
      this.m_MediaStoreDirectoryManager = ((MediaStoreDirectoryManager)BaseApplication.current().findComponent(MediaStoreDirectoryManager.class));
      Log.d("RecentMediaSet", "RecentMediaSet() - created, m_BaseTimeInMillis:" + this.m_BaseTimeInMillis);
      return;
      i = 0;
      break;
    }
  }
  
  public static void resetBaseTime()
  {
    Object localObject = PreferenceManager.getDefaultSharedPreferences(GalleryApplication.current());
    long l1 = ((SharedPreferences)localObject).getLong("RecentMediaSetBaseTime", 0L);
    long l2 = System.currentTimeMillis();
    localObject = ((SharedPreferences)localObject).edit();
    ((SharedPreferences.Editor)localObject).putLong("RecentMediaSetBaseTime", l2);
    ((SharedPreferences.Editor)localObject).apply();
    Log.d("RecentMediaSet", "resetBaseTime() - save current time to preference. last:" + l1 + ",current:" + l2);
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    int i = 0;
    if (paramMedia != null) {
      if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break label36;
      }
    }
    label36:
    for (paramInt = i;; paramInt = Media.FLAG_MOVE_TO_RECYCE_BIN | 0x0)
    {
      return paramMedia.delete(paramDeletionCallback, paramInt);
      Log.w("RecentMediaSet", "deleteMedia() - No media to delete");
      return null;
    }
  }
  
  public String getId()
  {
    return "Recent";
  }
  
  protected int getNameResourceId()
  {
    return BaseApplication.current().getResources().getIdentifier("media_set_name_recent", "string", "com.oneplus.gallery");
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.SYSTEM;
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  protected boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    String str;
    if ((Media.FLAG_SUB_MEDIA & paramInt) == 0)
    {
      str = paramMedia.getFilePath();
      if (str != null) {
        break label32;
      }
      if ((paramMedia instanceof MediaStoreItem)) {
        break label53;
      }
    }
    for (;;)
    {
      return false;
      return false;
      label32:
      if ((this.m_MediaStoreDirectoryManager == null) || (!this.m_MediaStoreDirectoryManager.isPathInSystemDirectory(str))) {
        break;
      }
      return false;
      label53:
      if (((MediaStoreItem)paramMedia).getAddedTime() < this.m_BaseTimeInMillis) {}
      for (paramInt = 1; paramInt == 0; paramInt = 0)
      {
        if (this.m_ExcludeTodayTakenMedia) {
          break label90;
        }
        return true;
      }
      continue;
      label90:
      long l = System.currentTimeMillis();
      if (paramMedia.getTakenTime() >= l - l % 86400000L) {}
      for (paramInt = 1; paramInt == 0; paramInt = 0) {
        return true;
      }
    }
  }
  
  protected void startDeletion(Handle paramHandle, int paramInt)
  {
    completeDeletion(paramHandle, false, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/RecentMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */