package com.oneplus.gallery2.media;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.gallery2.MediaContentThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlbumManager
  extends MediaSourceComponent<MediaStoreMediaSource>
{
  public static EventKey<AlbumEventArgs> EVENT_ALBUM_CREATED = new EventKey("AlbumCreated", AlbumEventArgs.class, AlbumManager.class);
  public static final int FLAG_NO_EMPTY_ALBUMS = 1;
  private static final int MSG_ALBUMS_READY = 10001;
  private static final int MSG_ALBUM_CREATED = 10010;
  private static final int MSG_ALBUM_RENAMED = 10015;
  private static final int MSG_MEDIA_ADDED_TO_ALBUM = 10020;
  private static final int MSG_SETUP_ALBUM = 10000;
  private final Map<Long, Album> m_Albums = new HashMap();
  private final Map<Media, Set<Album>> m_AlbumsByMedia = new HashMap();
  private boolean m_AlbumsReady;
  private final PropertyChangedCallback<Integer> m_MediaCountChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      AlbumManager.this.onMediaSetMediaCountChanged((AlbumMediaSet)paramAnonymousPropertySource, (Integer)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Integer)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final List<AlbumMediaSetList> m_OpenedMediaSetLists = new ArrayList();
  
  AlbumManager(BaseApplication paramBaseApplication)
  {
    super("Album media set manager", paramBaseApplication, MediaStoreMediaSource.class);
  }
  
  private void createDefaultMediaSets(AlbumMediaSetList paramAlbumMediaSetList, Album paramAlbum)
  {
    Object localObject;
    if (paramAlbumMediaSetList != null)
    {
      localObject = (AlbumMediaSet)paramAlbum.defaultMediaSets.get(paramAlbumMediaSetList.targetMediaType);
      if (localObject == null) {
        break label184;
      }
      paramAlbum = (Album)localObject;
      if ((paramAlbumMediaSetList.flags & 0x1) != 0) {
        break label243;
      }
    }
    label104:
    label184:
    label243:
    while (!MediaSets.isEmpty(paramAlbum))
    {
      paramAlbumMediaSetList.addMediaSet(paramAlbum, false);
      for (;;)
      {
        return;
        int i = this.m_OpenedMediaSetLists.size() - 1;
        while (i >= 0)
        {
          paramAlbumMediaSetList = (AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i);
          localObject = paramAlbumMediaSetList.targetMediaType;
          if (!paramAlbum.defaultMediaSets.containsKey(localObject)) {
            break label104;
          }
          i -= 1;
        }
      }
      AlbumMediaSet localAlbumMediaSet = new AlbumMediaSet((MediaStoreMediaSource)getMediaSource(), this, paramAlbum.info, (MediaType)localObject);
      localAlbumMediaSet.addCallback(MediaSet.PROP_MEDIA_COUNT, this.m_MediaCountChangedCB);
      paramAlbum.defaultMediaSets.put(localObject, localAlbumMediaSet);
      if ((paramAlbumMediaSetList.flags & 0x1) == 0) {}
      for (;;)
      {
        paramAlbumMediaSetList.addMediaSet(localAlbumMediaSet, true);
        break;
        if (MediaSets.isEmpty(localAlbumMediaSet)) {
          break;
        }
      }
      localObject = new AlbumMediaSet((MediaStoreMediaSource)getMediaSource(), this, paramAlbum.info, paramAlbumMediaSetList.targetMediaType);
      ((AlbumMediaSet)localObject).addCallback(MediaSet.PROP_MEDIA_COUNT, this.m_MediaCountChangedCB);
      paramAlbum.defaultMediaSets.put(paramAlbumMediaSetList.targetMediaType, localObject);
      paramAlbum = (Album)localObject;
      break;
    }
  }
  
  private void onAlbumCreationCompleted(CallbackHandle<AlbumCallback> paramCallbackHandle, String paramString, GalleryDatabase.AlbumInfo paramAlbumInfo)
  {
    if (isRunningOrInitializing(true))
    {
      if (paramAlbumInfo == null) {
        break label146;
      }
      if (this.m_Albums.containsKey(Long.valueOf(paramAlbumInfo.albumId))) {
        break label201;
      }
      Log.v(this.TAG, "onAlbumCreationCompleted() - Album '", paramString, "' (", Long.valueOf(paramAlbumInfo.albumId), ") created");
      paramString = new Album(paramAlbumInfo);
      this.m_Albums.put(Long.valueOf(paramString.id), paramString);
      createDefaultMediaSets(null, paramString);
      paramAlbumInfo = (AlbumMediaSet[])paramString.defaultMediaSets.values().toArray(new AlbumMediaSet[paramString.defaultMediaSets.size()]);
      if (paramCallbackHandle.getCallback() != null) {
        break label253;
      }
    }
    for (;;)
    {
      raise(EVENT_ALBUM_CREATED, new AlbumEventArgs(paramString.id, Arrays.asList(paramAlbumInfo)));
      return;
      return;
      label146:
      Log.e(this.TAG, "onAlbumCreationCompleted() - Fail to create album '" + paramString + "'");
      if (paramCallbackHandle.getCallback() == null) {
        return;
      }
      ((AlbumCallback)paramCallbackHandle.getCallback()).onAlbumCreationCompleted(-1L, false, null, 0);
      return;
      label201:
      Log.e(this.TAG, "onAlbumCreationCompleted() - Duplicate album ID : " + paramAlbumInfo.albumId);
      if (paramCallbackHandle.getCallback() == null) {
        return;
      }
      ((AlbumCallback)paramCallbackHandle.getCallback()).onAlbumCreationCompleted(-1L, false, null, 0);
      return;
      label253:
      ((AlbumCallback)paramCallbackHandle.getCallback()).onAlbumCreationCompleted(paramString.id, true, paramAlbumInfo, 0);
    }
  }
  
  private void onAlbumMediaSetListReleased(AlbumMediaSetList paramAlbumMediaSetList)
  {
    verifyAccess();
    if (this.m_OpenedMediaSetLists.remove(paramAlbumMediaSetList))
    {
      if (this.m_OpenedMediaSetLists.isEmpty())
      {
        Log.d(this.TAG, "onAlbumMediaSetListReleased() - All media set lists are released");
        paramAlbumMediaSetList = this.m_Albums.values().iterator();
        if (!paramAlbumMediaSetList.hasNext()) {
          return;
        }
        Album localAlbum = (Album)paramAlbumMediaSetList.next();
        AlbumMediaSet[] arrayOfAlbumMediaSet = (AlbumMediaSet[])localAlbum.defaultMediaSets.values().toArray(new AlbumMediaSet[localAlbum.defaultMediaSets.size()]);
        localAlbum.defaultMediaSets.clear();
        int i = arrayOfAlbumMediaSet.length;
        for (;;)
        {
          i -= 1;
          if (i < 0) {
            break;
          }
          arrayOfAlbumMediaSet[i].release();
        }
      }
    }
    else {
      return;
    }
    Log.d(this.TAG, "onAlbumMediaSetListReleased() - " + this.m_OpenedMediaSetLists.size() + " media set list(s) opened");
    return;
  }
  
  private void onAlbumRenamingCompleted(long paramLong, String paramString1, String paramString2, boolean paramBoolean)
  {
    if (isRunningOrInitializing())
    {
      Log.d(this.TAG, "onAlbumRenamingCompleted() - Album ID : " + paramLong + ", result : " + paramBoolean);
      if (paramBoolean) {
        break label49;
      }
    }
    for (;;)
    {
      return;
      return;
      label49:
      Object localObject = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localObject == null) {
        break;
      }
      localObject = ((Album)localObject).defaultMediaSets.values().iterator();
      while (((Iterator)localObject).hasNext()) {
        ((AlbumMediaSet)((Iterator)localObject).next()).onRenamed(paramString1, paramString2);
      }
    }
    Log.e(this.TAG, "onAlbumRenamingCompleted() - No data for album " + paramLong);
  }
  
  private void onAlbumsReady()
  {
    Log.v(this.TAG, "onAlbumsReady()");
    this.m_AlbumsReady = true;
    int i = this.m_OpenedMediaSetLists.size() - 1;
    while (i >= 0)
    {
      ((AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i)).ready();
      i -= 1;
    }
  }
  
  private void onMediaAddedToAlbum(long paramLong, Media paramMedia, boolean paramBoolean)
  {
    Album localAlbum;
    Object localObject;
    if (isRunningOrInitializing(true))
    {
      if (!paramBoolean) {
        break label96;
      }
      localAlbum = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localAlbum == null) {
        break label131;
      }
      localAlbum.recycledMedia.remove(paramMedia);
      if (!((MediaStoreMediaSource)getMediaSource()).isSubMedia(paramMedia)) {
        break label156;
      }
      localObject = (Set)this.m_AlbumsByMedia.get(paramMedia);
      if (localObject == null) {
        break label270;
      }
    }
    for (paramMedia = (Media)localObject;; paramMedia = (Media)localObject)
    {
      paramMedia.add(localAlbum);
      label96:
      label131:
      label156:
      do
      {
        return;
        return;
        Log.e(this.TAG, "onMediaAddedToAlbum() - Fail to add " + paramMedia + " to album " + paramLong);
        return;
        Log.e(this.TAG, "onMediaAddedToAlbum() - No data for album " + paramLong);
        return;
      } while (!localAlbum.media.add(paramMedia));
      localObject = (Set)this.m_AlbumsByMedia.get(paramMedia);
      if (localObject != null) {}
      for (;;)
      {
        ((Set)localObject).add(localAlbum);
        localObject = localAlbum.defaultMediaSets.values().iterator();
        while (((Iterator)localObject).hasNext()) {
          ((AlbumMediaSet)((Iterator)localObject).next()).onMediaCreated(paramMedia, 0);
        }
        break;
        localObject = new HashSet();
        this.m_AlbumsByMedia.put(paramMedia, localObject);
      }
      label270:
      localObject = new HashSet();
      this.m_AlbumsByMedia.put(paramMedia, localObject);
    }
  }
  
  private void onMediaSetMediaCountChanged(AlbumMediaSet paramAlbumMediaSet, Integer paramInteger1, Integer paramInteger2)
  {
    boolean bool1 = MediaSets.isEmpty(paramInteger1);
    boolean bool2 = MediaSets.isEmpty(paramInteger2);
    if (bool1 != bool2)
    {
      if (!bool2)
      {
        i = this.m_OpenedMediaSetLists.size() - 1;
        while (i >= 0)
        {
          paramInteger1 = (AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i);
          if ((paramInteger1.flags & 0x1) != 0) {
            paramInteger1.addMediaSet(paramAlbumMediaSet, true);
          }
          i -= 1;
        }
      }
    }
    else {
      return;
    }
    int i = this.m_OpenedMediaSetLists.size() - 1;
    while (i >= 0)
    {
      paramInteger1 = (AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i);
      if ((paramInteger1.flags & 0x1) != 0) {
        paramInteger1.removeMediaSet(paramAlbumMediaSet, true);
      }
      i -= 1;
    }
  }
  
  private boolean removeMediaFromAlbum(long paramLong, Media paramMedia, int paramInt)
  {
    verifyAccess();
    Object localObject1;
    boolean bool;
    if (isRunningOrInitializing(true))
    {
      if (paramMedia == null) {
        break label130;
      }
      localObject1 = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localObject1 == null) {
        break label132;
      }
      bool = ((Album)localObject1).media.remove(paramMedia);
      if ((Media.FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break label164;
      }
      paramInt = 0;
      if (paramInt == 0) {
        break label170;
      }
      if (!((Album)localObject1).recycledMedia.add(paramMedia)) {
        break label352;
      }
    }
    label130:
    label132:
    label164:
    label170:
    label182:
    label236:
    label316:
    label350:
    for (;;)
    {
      localObject1 = ((Album)localObject1).defaultMediaSets.values().iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((AlbumMediaSet)((Iterator)localObject1).next()).onMediaDeleted(paramMedia, 0);
      }
      return false;
      return false;
      Log.e(this.TAG, "removeMediaFromAlbum() - Album " + paramLong + " not found");
      return false;
      paramInt = 1;
      break;
      final Object localObject2;
      if (bool)
      {
        if ((paramMedia instanceof MediaStoreMedia)) {
          break label236;
        }
        localObject2 = (Set)this.m_AlbumsByMedia.get(paramMedia);
        if (localObject2 != null) {
          break label316;
        }
      }
      for (;;)
      {
        if ((!(paramMedia instanceof MediaStoreMedia)) || (!((MediaStoreMedia)paramMedia).isSubMedia())) {
          break label350;
        }
        return true;
        ((Album)localObject1).recycledMedia.remove(paramMedia);
        break;
        localObject2 = new GalleryDatabase.AlbumMediaRelation(((Album)localObject1).id, ((MediaStoreMedia)paramMedia).getMediaId());
        if (HandlerUtils.post(MediaContentThread.current(), new Runnable()
        {
          public void run()
          {
            GalleryDatabase.deleteAlbumMediaRelation(localObject2);
          }
        })) {
          break label182;
        }
        Log.e(this.TAG, "removeMediaFromAlbum() - Fail to post to media content thread to remove media " + paramMedia + " from album " + ((Album)localObject1).id);
        return false;
        if ((((Set)localObject2).remove(localObject1)) && (((Set)localObject2).isEmpty())) {
          this.m_AlbumsByMedia.remove(paramMedia);
        }
      }
    }
    label352:
    return false;
    return true;
  }
  
  private void setupAlbum(final GalleryDatabase.AlbumInfo paramAlbumInfo, List<GalleryDatabase.AlbumMediaRelation> paramList)
  {
    Album localAlbum;
    final Object localObject1;
    long l1;
    int i;
    int j;
    HashSet localHashSet;
    Object localObject2;
    if (isRunningOrInitializing(false))
    {
      localAlbum = new Album(paramAlbumInfo);
      localObject1 = (Album)this.m_Albums.put(Long.valueOf(localAlbum.id), localAlbum);
      if (localObject1 != null) {
        break label247;
      }
      l1 = SystemClock.elapsedRealtime();
      i = 0;
      j = 0;
      paramAlbumInfo = null;
      localObject1 = null;
      localHashSet = new HashSet();
      localObject2 = new HashSet();
      if (paramList != null) {
        break label295;
      }
      localObject2 = ((Set)localObject2).iterator();
      paramAlbumInfo = null;
    }
    for (;;)
    {
      label151:
      label177:
      long l2;
      if (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (BaseGroupedMedia)((Iterator)localObject2).next();
        if (localAlbum.media.add(localObject3))
        {
          paramList = (Set)this.m_AlbumsByMedia.get(localObject3);
          if (paramList != null)
          {
            paramList.add(localAlbum);
            j += 1;
            paramList = ((BaseGroupedMedia)localObject3).getSubMedia().iterator();
            do
            {
              if (!paramList.hasNext()) {
                break;
              }
              l2 = ((MediaStoreMedia)paramList.next()).getMediaId();
            } while (localHashSet.remove(Long.valueOf(l2)));
            if (paramAlbumInfo == null) {
              break label611;
            }
          }
          for (;;)
          {
            paramAlbumInfo.add(new GalleryDatabase.AlbumMediaRelation(localAlbum.id, l2));
            break label177;
            return;
            label247:
            Log.e(this.TAG, "setupAlbum() - Duplicate album : " + paramAlbumInfo.albumId);
            this.m_Albums.put(Long.valueOf(((Album)localObject1).id), localObject1);
            return;
            label295:
            localObject3 = (MediaStoreMediaSource)getMediaSource();
            int k = paramList.size() - 1;
            localObject1 = paramAlbumInfo;
            j = i;
            if (k < 0) {
              break;
            }
            localObject1 = (GalleryDatabase.AlbumMediaRelation)paramList.get(k);
            MediaStoreMedia localMediaStoreMedia = ((MediaStoreMediaSource)localObject3).getMedia(((GalleryDatabase.AlbumMediaRelation)localObject1).mediaId);
            if (localMediaStoreMedia != null)
            {
              localHashSet.add(Long.valueOf(localMediaStoreMedia.getMediaId()));
              if (!localMediaStoreMedia.isSubMedia()) {
                break label461;
              }
              localObject1 = (Set)this.m_AlbumsByMedia.get(localMediaStoreMedia);
              if (localObject1 == null) {
                break label543;
              }
              label400:
              ((Set)localObject1).add(localAlbum);
              localObject1 = ((MediaStoreMediaSource)localObject3).getGroupedMedia(localMediaStoreMedia, 0);
              if (localObject1 != null) {
                break label569;
              }
            }
            for (;;)
            {
              k -= 1;
              break;
              if (paramAlbumInfo != null) {}
              for (;;)
              {
                paramAlbumInfo.add(localObject1);
                break;
                paramAlbumInfo = new ArrayList();
              }
              label461:
              if (localAlbum.media.add(localMediaStoreMedia))
              {
                localObject1 = (Set)this.m_AlbumsByMedia.get(localMediaStoreMedia);
                if (localObject1 != null) {}
                for (;;)
                {
                  ((Set)localObject1).add(localAlbum);
                  i += 1;
                  break;
                  localObject1 = new HashSet();
                  this.m_AlbumsByMedia.put(localMediaStoreMedia, localObject1);
                }
                label543:
                localObject1 = new HashSet();
                this.m_AlbumsByMedia.put(localMediaStoreMedia, localObject1);
                break label400;
                label569:
                ((Set)localObject2).add((BaseGroupedMedia)localObject1[0]);
              }
            }
            paramList = new HashSet();
            this.m_AlbumsByMedia.put(localObject3, paramList);
            break label151;
            label611:
            paramAlbumInfo = new ArrayList();
          }
        }
      }
      else
      {
        l2 = SystemClock.elapsedRealtime();
        Log.d(this.TAG, "setupAlbum() - Take " + (l2 - l1) + " ms to create album " + localAlbum.id + " with " + j + " media");
        if (paramAlbumInfo == null) {
          if (localObject1 != null) {
            break label765;
          }
        }
        for (;;)
        {
          createDefaultMediaSets(null, localAlbum);
          return;
          Log.w(this.TAG, "setupAlbum() - Add " + paramAlbumInfo.size() + " album-media relations for album " + localAlbum.id);
          HandlerUtils.post(MediaContentThread.current(), new Runnable()
          {
            public void run()
            {
              int i = paramAlbumInfo.size() - 1;
              while (i >= 0)
              {
                GalleryDatabase.addAlbumMediaRelation((GalleryDatabase.AlbumMediaRelation)paramAlbumInfo.get(i));
                i -= 1;
              }
            }
          });
          break;
          label765:
          Log.w(this.TAG, "setupAlbum() - Remove " + ((List)localObject1).size() + " album-media relations for album " + localAlbum.id);
          HandlerUtils.post(MediaContentThread.current(), new Runnable()
          {
            public void run()
            {
              int i = localObject1.size() - 1;
              while (i >= 0)
              {
                GalleryDatabase.deleteAlbumMediaRelation((GalleryDatabase.AlbumMediaRelation)localObject1.get(i));
                i -= 1;
              }
            }
          });
        }
      }
    }
  }
  
  private void setupAlbums()
  {
    HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        Log.v(AlbumManager.this.TAG, "setupAlbums()");
        long l1 = SystemClock.elapsedRealtime();
        List localList1 = GalleryDatabase.getAlbumInfos();
        long l2 = SystemClock.elapsedRealtime();
        Log.d(AlbumManager.this.TAG, "setupAlbums() - Take " + (l2 - l1) + " ms to get " + localList1.size() + " album info(s)");
        l1 = SystemClock.elapsedRealtime();
        int j = localList1.size() - 1;
        int i = 0;
        if (j >= 0)
        {
          GalleryDatabase.AlbumInfo localAlbumInfo = (GalleryDatabase.AlbumInfo)localList1.get(j);
          List localList2 = GalleryDatabase.getAlbumMediaRelationsByAlbumId(localAlbumInfo.albumId);
          HandlerUtils.sendMessage(AlbumManager.this, 10000, new Object[] { localAlbumInfo, localList2 });
          if (localList2 == null) {}
          for (;;)
          {
            j -= 1;
            break;
            i = localList2.size() + i;
          }
        }
        HandlerUtils.sendMessage(AlbumManager.this, 10001);
        l2 = SystemClock.elapsedRealtime();
        Log.d(AlbumManager.this.TAG, "setupAlbums() - Take " + (l2 - l1) + " ms to get " + i + " album-media relation(s)");
      }
    });
  }
  
  boolean addMediaToAlbum(long paramLong, final Media paramMedia)
  {
    verifyAccess();
    Album localAlbum;
    if (isRunningOrInitializing(true))
    {
      if (paramMedia == null) {
        break label75;
      }
      localAlbum = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localAlbum == null) {
        break label87;
      }
      if (localAlbum.media.contains(paramMedia)) {
        break label113;
      }
      if ((paramMedia instanceof MediaStoreMedia)) {
        break label115;
      }
      onMediaAddedToAlbum(localAlbum.id, paramMedia, true);
    }
    label75:
    label87:
    label113:
    label115:
    final GalleryDatabase.AlbumMediaRelation localAlbumMediaRelation;
    do
    {
      return true;
      return false;
      Log.e(this.TAG, "addMediaToAlbum() - No media to add");
      return false;
      Log.e(this.TAG, "addMediaToAlbum() - No data for album " + paramLong);
      return false;
      return true;
      localAlbumMediaRelation = new GalleryDatabase.AlbumMediaRelation(localAlbum.id, ((MediaStoreMedia)paramMedia).getMediaId());
    } while (HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        boolean bool = GalleryDatabase.addAlbumMediaRelation(localAlbumMediaRelation);
        AlbumManager localAlbumManager = AlbumManager.this;
        if (!bool) {}
        for (int i = 0;; i = 1)
        {
          HandlerUtils.sendMessage(localAlbumManager, 10020, i, 0, new Object[] { Long.valueOf(localAlbumMediaRelation.albumId), paramMedia });
          return;
        }
      }
    }));
    Log.e(this.TAG, "addMediaToAlbum() - Fail to post to media content thread to add media " + paramMedia + " to album " + localAlbum.id);
    return false;
  }
  
  public Handle createAlbum(final String paramString, final AlbumCallback paramAlbumCallback)
  {
    verifyAccess();
    if (isRunningOrInitializing(true))
    {
      paramAlbumCallback = new CallbackHandle("CreateAlbum", paramAlbumCallback, null)
      {
        protected void onClose(int paramAnonymousInt) {}
      };
      if (HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.AlbumInfo localAlbumInfo;
          if (Handle.isValid(paramAlbumCallback))
          {
            localAlbumInfo = GalleryDatabase.addAlbumInfo(paramString);
            if (localAlbumInfo != null) {
              break label69;
            }
            Log.e(AlbumManager.this.TAG, "createAlbum() - Fail to create new album info");
          }
          for (;;)
          {
            HandlerUtils.sendMessage(AlbumManager.this, 10010, new Object[] { paramAlbumCallback, paramString, localAlbumInfo });
            return;
            return;
            label69:
            Log.d(AlbumManager.this.TAG, "createAlbum() - Album info " + localAlbumInfo.albumId + " created, name : " + paramString);
          }
        }
      })) {
        return paramAlbumCallback;
      }
    }
    else
    {
      return null;
    }
    Log.e(this.TAG, "createAlbum() - Fail to post to media content thread");
    return null;
  }
  
  boolean deleteAlbum(final long paramLong)
  {
    verifyAccess();
    Album localAlbum;
    Iterator localIterator;
    if (isRunningOrInitializing(true))
    {
      localAlbum = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localAlbum == null) {
        break label145;
      }
      if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.deleteAlbumInfo(paramLong);
          GalleryDatabase.deleteAlbumMediaRelationsByAlbumId(paramLong);
        }
      })) {
        break label147;
      }
      localIterator = localAlbum.defaultMediaSets.values().iterator();
    }
    while (localIterator.hasNext())
    {
      AlbumMediaSet localAlbumMediaSet = (AlbumMediaSet)localIterator.next();
      int i = this.m_OpenedMediaSetLists.size() - 1;
      if (i >= 0)
      {
        AlbumMediaSetList localAlbumMediaSetList = (AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i);
        if (localAlbumMediaSetList.targetMediaType != localAlbumMediaSet.getTargetMediaType()) {}
        for (;;)
        {
          i -= 1;
          break;
          return false;
          label145:
          return false;
          label147:
          Log.e(this.TAG, "deleteAlbum() - Fail to post to media content thread to delete album " + paramLong);
          return false;
          localAlbumMediaSetList.removeMediaSet(localAlbumMediaSet, true);
        }
      }
      localAlbumMediaSet.removeCallback(MediaSet.PROP_MEDIA_COUNT, this.m_MediaCountChangedCB);
      localAlbumMediaSet.release();
    }
    localAlbum.defaultMediaSets.clear();
    localAlbum.media.clear();
    this.m_Albums.remove(Long.valueOf(paramLong));
    Log.v(this.TAG, "deleteAlbum() - Delete album ", Long.valueOf(paramLong));
    return true;
  }
  
  Iterable<Media> getMedia(long paramLong, MediaType paramMediaType)
  {
    Album localAlbum = (Album)this.m_Albums.get(Long.valueOf(paramLong));
    if (localAlbum == null) {
      return Collections.EMPTY_LIST;
    }
    return new MediaIterable(paramMediaType, localAlbum.media);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    long l;
    Object localObject1;
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10010: 
      paramMessage = (Object[])paramMessage.obj;
      onAlbumCreationCompleted((CallbackHandle)paramMessage[0], (String)paramMessage[1], (GalleryDatabase.AlbumInfo)paramMessage[2]);
      return;
    case 10015: 
      Object localObject2 = (Object[])paramMessage.obj;
      l = ((Long)localObject2[0]).longValue();
      localObject1 = (String)localObject2[1];
      localObject2 = (String)localObject2[2];
      if (paramMessage.arg1 == 0) {}
      for (;;)
      {
        onAlbumRenamingCompleted(l, (String)localObject1, (String)localObject2, bool1);
        return;
        bool1 = true;
      }
    case 10001: 
      onAlbumsReady();
      return;
    case 10020: 
      localObject1 = (Object[])paramMessage.obj;
      l = ((Long)localObject1[0]).longValue();
      localObject1 = (Media)localObject1[1];
      if (paramMessage.arg1 == 0) {}
      for (bool1 = bool2;; bool1 = true)
      {
        onMediaAddedToAlbum(l, (Media)localObject1, bool1);
        return;
      }
    }
    paramMessage = (Object[])paramMessage.obj;
    setupAlbum((GalleryDatabase.AlbumInfo)paramMessage[0], (List)paramMessage[1]);
  }
  
  protected void onDeinitialize()
  {
    int i = this.m_OpenedMediaSetLists.size() - 1;
    while (i >= 0)
    {
      ((AlbumMediaSetList)this.m_OpenedMediaSetLists.get(i)).clearMediaSetLists(true);
      i -= 1;
    }
    this.m_OpenedMediaSetLists.clear();
    Iterator localIterator1 = this.m_Albums.values().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((Album)localIterator1.next()).defaultMediaSets.values().iterator();
      while (localIterator2.hasNext()) {
        ((AlbumMediaSet)localIterator2.next()).release();
      }
    }
    this.m_Albums.clear();
    this.m_AlbumsByMedia.clear();
    super.onDeinitialize();
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt)
  {
    Object localObject2 = (Set)this.m_AlbumsByMedia.get(paramMedia);
    Object localObject1;
    int i;
    Object localObject3;
    if (localObject2 != null)
    {
      localObject1 = (MediaStoreMediaSource)getMediaSource();
      localObject2 = (Album[])((Set)localObject2).toArray(new Album[((Set)localObject2).size()]);
      i = localObject2.length;
      paramInt = 0;
      if (paramInt >= i) {
        break label102;
      }
      localObject3 = localObject2[paramInt];
      if (((MediaStoreMediaSource)localObject1).isSubMedia(paramMedia)) {
        break label213;
      }
      onMediaAddedToAlbum(((Album)localObject3).id, paramMedia, true);
    }
    for (;;)
    {
      paramInt += 1;
      break;
      if (!(paramMedia instanceof BaseGroupedMedia)) {
        return;
      }
      label102:
      label212:
      for (;;)
      {
        localObject1 = ((BaseGroupedMedia)paramMedia).getSubMedia().iterator();
        for (;;)
        {
          if (!((Iterator)localObject1).hasNext()) {
            break label212;
          }
          localObject2 = (Media)((Iterator)localObject1).next();
          localObject2 = (Set)this.m_AlbumsByMedia.get(localObject2);
          if (localObject2 == null) {
            break;
          }
          localObject2 = (Album[])((Set)localObject2).toArray(new Album[((Set)localObject2).size()]);
          i = localObject2.length;
          paramInt = 0;
          while (paramInt < i)
          {
            onMediaAddedToAlbum(localObject2[paramInt].id, paramMedia, true);
            paramInt += 1;
          }
        }
      }
      label213:
      GroupedMedia[] arrayOfGroupedMedia = ((MediaStoreMediaSource)localObject1).getGroupedMedia(paramMedia, 0);
      if ((arrayOfGroupedMedia != null) && (arrayOfGroupedMedia.length > 0)) {
        onMediaAddedToAlbum(((Album)localObject3).id, arrayOfGroupedMedia[0], true);
      }
    }
  }
  
  protected void onMediaDeleted(Media paramMedia, int paramInt)
  {
    int i = 0;
    Object localObject;
    if ((paramMedia instanceof MediaStoreItem))
    {
      localObject = (Set)this.m_AlbumsByMedia.get(paramMedia);
      if (localObject != null) {
        break label31;
      }
    }
    for (;;)
    {
      return;
      return;
      label31:
      localObject = (Album[])((Set)localObject).toArray(new Album[((Set)localObject).size()]);
      int j = localObject.length;
      while (i < j)
      {
        removeMediaFromAlbum(localObject[i].id, paramMedia, paramInt);
        i += 1;
      }
    }
  }
  
  protected void onMediaTableReady()
  {
    super.onMediaTableReady();
    setupAlbums();
    enableMediaChangeCallback();
  }
  
  protected void onMediaUpdated(Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_SUB_MEDIA & paramInt) == 0)
    {
      localObject = (Set)this.m_AlbumsByMedia.get(paramMedia);
      if (localObject != null) {}
    }
    else
    {
      return;
    }
    Object localObject = ((Set)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Iterator localIterator = ((Album)((Iterator)localObject).next()).defaultMediaSets.values().iterator();
      while (localIterator.hasNext()) {
        ((AlbumMediaSet)localIterator.next()).onMediaUpdated(paramMedia, paramInt);
      }
    }
  }
  
  public MediaSetList openAlbumMediaSetList(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType, int paramInt)
  {
    verifyAccess();
    if (isRunningOrInitializing(true))
    {
      paramMediaSetComparator = new AlbumMediaSetList(paramMediaSetComparator, paramMediaType, paramInt);
      this.m_OpenedMediaSetLists.add(paramMediaSetComparator);
      paramMediaType = this.m_Albums.values().iterator();
      while (paramMediaType.hasNext()) {
        createDefaultMediaSets(paramMediaSetComparator, (Album)paramMediaType.next());
      }
    }
    return MediaSetList.EMPTY;
    if (!this.m_AlbumsReady) {
      return paramMediaSetComparator;
    }
    paramMediaSetComparator.ready();
    return paramMediaSetComparator;
  }
  
  public MediaSetList openAlbumMediaSetList(MediaType paramMediaType, int paramInt)
  {
    return openAlbumMediaSetList(MediaSetComparator.DEFAULT, paramMediaType, paramInt);
  }
  
  boolean removeMediaFromAlbum(long paramLong, Media paramMedia)
  {
    return removeMediaFromAlbum(paramLong, paramMedia, 0);
  }
  
  boolean renameAlbum(final long paramLong, final String paramString)
  {
    verifyAccess();
    Album localAlbum;
    if (isRunningOrInitializing(true))
    {
      localAlbum = (Album)this.m_Albums.get(Long.valueOf(paramLong));
      if (localAlbum == null) {
        break label54;
      }
      if (!TextUtils.equals(localAlbum.info.name, paramString)) {
        break label80;
      }
    }
    label54:
    label80:
    String str;
    do
    {
      return true;
      return false;
      Log.e(this.TAG, "renameAlbum() - No data for album " + paramLong);
      return false;
      str = localAlbum.info.name;
      localAlbum.info.name = paramString;
      localAlbum.info.lastModifiedTime = System.currentTimeMillis();
      paramString = localAlbum.info.clone();
    } while (HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        boolean bool = GalleryDatabase.updateAlbumInfo(paramString);
        AlbumManager localAlbumManager = AlbumManager.this;
        if (!bool) {}
        for (int i = 0;; i = 1)
        {
          HandlerUtils.sendMessage(localAlbumManager, 10015, i, 0, new Object[] { Long.valueOf(paramLong), this.val$oldName, paramString.name });
          return;
        }
      }
    }));
    Log.e(this.TAG, "renameAlbum() - Fail to post to media content thread to rename");
    return false;
  }
  
  void updateLastMediaAddedTime(long paramLong1, long paramLong2)
  {
    final Object localObject = (Album)this.m_Albums.get(Long.valueOf(paramLong1));
    if (localObject != null)
    {
      ((Album)localObject).info.lastMediaAddedTime = paramLong2;
      ((Album)localObject).info.lastModifiedTime = Math.max(((Album)localObject).info.lastModifiedTime, paramLong2);
      localObject = ((Album)localObject).info.clone();
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.updateAlbumInfo(localObject);
        }
      });
      return;
    }
    Log.e(this.TAG, "updateLastMediaAddedTime() - No data for album " + localObject);
  }
  
  private static final class Album
  {
    public final Map<MediaType, AlbumMediaSet> defaultMediaSets = new HashMap();
    public final long id;
    public final GalleryDatabase.AlbumInfo info;
    public final Set<Media> media = new HashSet();
    public final Set<Media> recycledMedia = new HashSet();
    
    public Album(GalleryDatabase.AlbumInfo paramAlbumInfo)
    {
      this.id = paramAlbumInfo.albumId;
      this.info = paramAlbumInfo;
    }
  }
  
  public static abstract class AlbumCallback
  {
    public void onAlbumCreationCompleted(long paramLong, boolean paramBoolean, AlbumMediaSet[] paramArrayOfAlbumMediaSet, int paramInt) {}
  }
  
  private final class AlbumMediaSetList
    extends BaseMediaSetList
  {
    public final int flags;
    public final MediaType targetMediaType;
    
    public AlbumMediaSetList(MediaSetComparator paramMediaSetComparator, MediaType paramMediaType, int paramInt)
    {
      super(true);
      this.targetMediaType = paramMediaType;
      this.flags = paramInt;
    }
    
    public void ready()
    {
      setReadOnly(PROP_IS_READY, Boolean.valueOf(true));
    }
    
    public void release()
    {
      super.release();
      AlbumManager.this.onAlbumMediaSetListReleased(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/AlbumManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */