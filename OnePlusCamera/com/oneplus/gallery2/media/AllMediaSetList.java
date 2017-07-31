package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.Log;
import com.oneplus.gallery2.web.WebMediaSetManager;

public final class AllMediaSetList
  extends CompoundMediaSetList
{
  public static final int FLAG_INCLUDE_ALL_MEDIA = 1;
  public static final int FLAG_INCLUDE_RECENT = 4;
  public static final int FLAG_NO_EMPTY_ALBUMS = 2;
  public static final int FLAG_NO_WEB_MEDIA = 8;
  private static final String TAG = AllMediaSetList.class.getSimpleName();
  private static volatile int m_InstanceCount;
  
  private AllMediaSetList(MediaSetComparator paramMediaSetComparator, MediaSetList... paramVarArgs)
  {
    super(paramMediaSetComparator, true, paramVarArgs);
  }
  
  public static AllMediaSetList open(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType, int paramInt)
  {
    paramMediaSetComparator = new AllMediaSetList(paramMediaSetComparator, new MediaSetList[0]);
    Object localObject1 = BaseApplication.current();
    Object localObject2 = (SystemMediaSetManager)((BaseApplication)localObject1).findComponent(SystemMediaSetManager.class);
    if (localObject2 == null)
    {
      Log.w(TAG, "open() - No SystemMediaSetManager");
      localObject2 = (MediaStoreDirectoryManager)((BaseApplication)localObject1).findComponent(MediaStoreDirectoryManager.class);
      if (localObject2 != null) {
        break label192;
      }
      Log.w(TAG, "open() - No MediaStoreDirectoryManager");
      label68:
      localObject2 = (AlbumManager)((BaseApplication)localObject1).findComponent(AlbumManager.class);
      if (localObject2 != null) {
        break label206;
      }
      Log.w(TAG, "open() - No AlbumManager");
      localObject2 = (MtpMediaSetManager)((BaseApplication)localObject1).findComponent(MtpMediaSetManager.class);
      if (localObject2 != null) {
        break label234;
      }
      label110:
      if ((paramInt & 0x8) == 0) {
        break label249;
      }
    }
    for (;;)
    {
      m_InstanceCount += 1;
      Log.d(TAG, "open() - Instance count : " + m_InstanceCount);
      return paramMediaSetComparator;
      if ((paramInt & 0x1) == 0)
      {
        i = 0;
        label159:
        if ((paramInt & 0x4) != 0) {
          break label185;
        }
      }
      for (;;)
      {
        paramMediaSetComparator.addMediaSetList(((SystemMediaSetManager)localObject2).openSystemMediaSetList(paramMediaType, i));
        break;
        i = 1;
        break label159;
        label185:
        i |= 0x2;
      }
      label192:
      paramMediaSetComparator.addMediaSetList(((MediaStoreDirectoryManager)localObject2).openDirectoryMediaSetList(paramMediaType));
      break label68;
      label206:
      if ((paramInt & 0x2) == 0) {}
      for (int i = 0;; i = 1)
      {
        paramMediaSetComparator.addMediaSetList(((AlbumManager)localObject2).openAlbumMediaSetList(paramMediaType, i));
        break;
      }
      label234:
      paramMediaSetComparator.addMediaSetList(((MtpMediaSetManager)localObject2).openMtpMediaSetList(paramMediaType, 0));
      break label110;
      label249:
      localObject1 = (WebMediaSetManager[])((BaseApplication)localObject1).findComponents(WebMediaSetManager.class);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < localObject1.length)
        {
          paramMediaSetComparator.addMediaSetList(localObject1[paramInt].openMediaSetList(null, paramMediaType, 0));
          paramInt += 1;
        }
      }
    }
  }
  
  public static AllMediaSetList open(MediaType paramMediaType, int paramInt)
  {
    return open(MediaSetComparator.DEFAULT, paramMediaType, paramInt);
  }
  
  public void release()
  {
    super.release();
    m_InstanceCount -= 1;
    Log.d(TAG, "release() - Instance count : " + m_InstanceCount);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/AllMediaSetList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */