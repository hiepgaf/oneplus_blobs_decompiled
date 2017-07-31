package com.oneplus.gallery2.media;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SystemMediaSetManager
  extends MediaSourceComponent<MediaStoreMediaSource>
{
  public static final int FLAG_INCLUDE_ALL_MEDIA = 1;
  public static final int FLAG_INCLUDE_RECENT = 2;
  private final Map<MediaType, AllMediaMediaSet> m_AllMediaMediaSets = new HashMap();
  private final Map<MediaType, CameraRollMediaSet> m_CameraRollMediaSets = new HashMap();
  private final Map<MediaType, FavoriteMediaSet> m_FavoriteMediaSets = new HashMap();
  private final Map<MediaType, List<MediaSetListImpl>> m_OpenedMediaSetLists = new HashMap();
  private final Map<MediaType, RecentMediaSet> m_RecentMediaSets = new HashMap();
  private final Map<MediaType, ScreenshotMediaSet> m_ScreenshotMediaSets = new HashMap();
  private final Map<MediaType, SelfieMediaSet> m_SelfieMediaSets = new HashMap();
  
  SystemMediaSetManager(BaseApplication paramBaseApplication)
  {
    super("System media set manager", paramBaseApplication, MediaStoreMediaSource.class);
  }
  
  private void onSystemMediaSetListReleased(MediaSetListImpl paramMediaSetListImpl)
  {
    Object localObject1 = paramMediaSetListImpl.targetMediaType;
    Object localObject2 = (List)this.m_OpenedMediaSetLists.get(localObject1);
    if (localObject2 == null) {}
    while (!((List)localObject2).remove(paramMediaSetListImpl)) {
      return;
    }
    if (((List)localObject2).isEmpty())
    {
      localObject2 = this.TAG;
      if (localObject1 == null) {
        break label236;
      }
      paramMediaSetListImpl = (MediaSetListImpl)localObject1;
      Log.v((String)localObject2, "onSystemMediaSetListReleased() - All lists are released for media type ", paramMediaSetListImpl);
      this.m_OpenedMediaSetLists.remove(localObject1);
      paramMediaSetListImpl = (AllMediaMediaSet)this.m_AllMediaMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label242;
      }
      label96:
      paramMediaSetListImpl = (CameraRollMediaSet)this.m_CameraRollMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label249;
      }
      label114:
      paramMediaSetListImpl = (SelfieMediaSet)this.m_SelfieMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label256;
      }
      label132:
      paramMediaSetListImpl = (FavoriteMediaSet)this.m_FavoriteMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label263;
      }
      label150:
      paramMediaSetListImpl = (ScreenshotMediaSet)this.m_ScreenshotMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label270;
      }
      label168:
      paramMediaSetListImpl = (RecentMediaSet)this.m_RecentMediaSets.remove(localObject1);
      if (paramMediaSetListImpl != null) {
        break label277;
      }
    }
    for (;;)
    {
      if (this.m_OpenedMediaSetLists.isEmpty()) {
        break label284;
      }
      return;
      paramMediaSetListImpl = this.TAG;
      int i = ((List)localObject2).size();
      if (localObject1 != null) {}
      for (;;)
      {
        Log.v(paramMediaSetListImpl, "onSystemMediaSetListReleased() - ", Integer.valueOf(i), " list(s) opened for media type ", localObject1);
        return;
        localObject1 = "ALL";
      }
      label236:
      paramMediaSetListImpl = "ALL";
      break;
      label242:
      paramMediaSetListImpl.release();
      break label96;
      label249:
      paramMediaSetListImpl.release();
      break label114;
      label256:
      paramMediaSetListImpl.release();
      break label132;
      label263:
      paramMediaSetListImpl.release();
      break label150;
      label270:
      paramMediaSetListImpl.release();
      break label168;
      label277:
      paramMediaSetListImpl.release();
    }
    label284:
    Log.v(this.TAG, "onSystemMediaSetListReleased() - All lists are released");
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
  }
  
  protected void onMediaTableReady()
  {
    super.onMediaTableReady();
    Iterator localIterator = this.m_OpenedMediaSetLists.values().iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)localIterator.next();
      int i = localList.size() - 1;
      while (i >= 0)
      {
        ((MediaSetListImpl)localList.get(i)).ready();
        i -= 1;
      }
    }
  }
  
  public MediaSetList openSystemMediaSetList(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType, int paramInt)
  {
    verifyAccess();
    MediaSetListImpl localMediaSetListImpl;
    if (isRunningOrInitializing(true))
    {
      localMediaSetListImpl = new MediaSetListImpl(paramMediaSetComparator, paramMediaType);
      paramMediaSetComparator = (List)this.m_OpenedMediaSetLists.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label248;
      }
      paramMediaSetComparator.add(localMediaSetListImpl);
      String str1 = this.TAG;
      String str2 = "openSystemMediaSetList() - " + paramMediaSetComparator.size() + " list(s) opened for media type ";
      if (paramMediaType == null) {
        break label271;
      }
      paramMediaSetComparator = paramMediaType;
      label91:
      Log.v(str1, str2, paramMediaSetComparator);
      if ((paramInt & 0x1) != 0) {
        break label277;
      }
      paramMediaSetComparator = (CameraRollMediaSet)this.m_CameraRollMediaSets.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label330;
      }
      label123:
      localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
      paramMediaSetComparator = (SelfieMediaSet)this.m_SelfieMediaSets.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label361;
      }
      label149:
      localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
      paramMediaSetComparator = (FavoriteMediaSet)this.m_FavoriteMediaSets.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label392;
      }
      label175:
      localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
      if (paramMediaType != null) {
        break label416;
      }
      label187:
      paramMediaSetComparator = (ScreenshotMediaSet)this.m_ScreenshotMediaSets.get(paramMediaType);
      if (paramMediaSetComparator == null) {
        break label426;
      }
    }
    for (;;)
    {
      localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
      for (;;)
      {
        if ((paramInt & 0x2) != 0) {
          break label456;
        }
        if (((Boolean)((MediaStoreMediaSource)getMediaSource()).get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {
          break label514;
        }
        return localMediaSetListImpl;
        return MediaSetList.EMPTY;
        label248:
        paramMediaSetComparator = new ArrayList();
        this.m_OpenedMediaSetLists.put(paramMediaType, paramMediaSetComparator);
        break;
        label271:
        paramMediaSetComparator = "ALL";
        break label91;
        label277:
        paramMediaSetComparator = (AllMediaMediaSet)this.m_AllMediaMediaSets.get(paramMediaType);
        if (paramMediaSetComparator != null) {}
        for (;;)
        {
          localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
          break;
          paramMediaSetComparator = new AllMediaMediaSet(paramMediaType);
          this.m_AllMediaMediaSets.put(paramMediaType, paramMediaSetComparator);
        }
        label330:
        paramMediaSetComparator = new CameraRollMediaSet((MediaStoreMediaSource)getMediaSource(), paramMediaType);
        this.m_CameraRollMediaSets.put(paramMediaType, paramMediaSetComparator);
        break label123;
        label361:
        paramMediaSetComparator = new SelfieMediaSet((MediaStoreMediaSource)getMediaSource(), paramMediaType);
        this.m_SelfieMediaSets.put(paramMediaType, paramMediaSetComparator);
        break label149;
        label392:
        paramMediaSetComparator = new FavoriteMediaSet(paramMediaType);
        this.m_FavoriteMediaSets.put(paramMediaType, paramMediaSetComparator);
        break label175;
        label416:
        if (paramMediaType == MediaType.PHOTO) {
          break label187;
        }
      }
      label426:
      paramMediaSetComparator = new ScreenshotMediaSet((MediaStoreMediaSource)getMediaSource());
      this.m_ScreenshotMediaSets.put(paramMediaType, paramMediaSetComparator);
    }
    label456:
    paramMediaSetComparator = (RecentMediaSet)this.m_RecentMediaSets.get(paramMediaType);
    if (paramMediaSetComparator != null) {}
    for (;;)
    {
      localMediaSetListImpl.addMediaSet(paramMediaSetComparator, false);
      break;
      paramMediaSetComparator = new RecentMediaSet(getMediaSource(), paramMediaType, false);
      this.m_RecentMediaSets.put(paramMediaType, paramMediaSetComparator);
    }
    label514:
    localMediaSetListImpl.ready();
    return localMediaSetListImpl;
  }
  
  public MediaSetList openSystemMediaSetList(MediaType paramMediaType, int paramInt)
  {
    return openSystemMediaSetList(MediaSetComparator.DEFAULT, paramMediaType, paramInt);
  }
  
  private final class MediaSetListImpl
    extends BaseMediaSetList
  {
    public final MediaType targetMediaType;
    
    public MediaSetListImpl(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType)
    {
      super();
      this.targetMediaType = paramMediaType;
    }
    
    public void ready()
    {
      setReadOnly(PROP_IS_READY, Boolean.valueOf(true));
    }
    
    public void release()
    {
      super.release();
      SystemMediaSetManager.this.onSystemMediaSetListReleased(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/SystemMediaSetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */