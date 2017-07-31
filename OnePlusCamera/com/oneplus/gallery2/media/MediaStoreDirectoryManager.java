package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore.Files;
import android.text.TextUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.gallery2.MediaContentThread;
import com.oneplus.io.Path;
import java.io.File;
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

public final class MediaStoreDirectoryManager
  extends MediaSourceComponent<MediaStoreMediaSource>
{
  private static final Uri CONTENT_URI_FILE = MediaStore.Files.getContentUri("external");
  private static final int MSG_EXTRA_DIR_INFO_GET = 10000;
  private boolean m_DirectoriesReady;
  private final Map<Long, DirectoryInfo> m_DirectoryInfoById = new HashMap();
  private final Map<String, DirectoryInfo> m_DirectoryInfoByPath = new HashMap();
  private int m_NumOfDirQueryingExtraInfo;
  private final List<DirectoryMediaSetList> m_OpenedMediaSetLists = new ArrayList();
  private final List<String> m_SystemDirPathPrefixList = new ArrayList();
  private final HashMap<SystemDirectoryType, List<String>> m_SystemDirectoryTable = new HashMap();
  
  MediaStoreDirectoryManager(BaseApplication paramBaseApplication)
  {
    super("Media store directory manager", paramBaseApplication, MediaStoreMediaSource.class);
  }
  
  private void addToDirectory(MediaStoreItem paramMediaStoreItem, boolean paramBoolean, int paramInt)
  {
    final long l = paramMediaStoreItem.getParentId();
    if (l >= 0L) {}
    for (int i = 1; i == 0; i = 0) {
      return;
    }
    Object localObject2 = paramMediaStoreItem.getFilePath();
    Object localObject1;
    if (!isPathInSystemDirectory((String)localObject2))
    {
      localObject1 = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(l));
      if (localObject1 != null) {
        break label176;
      }
      localObject1 = Path.getDirectoryPath((String)localObject2);
      if (localObject1 == null) {
        break label270;
      }
      localObject2 = new DirectoryInfo(l, (String)localObject1);
      ((DirectoryInfo)localObject2).media.add((Media)paramMediaStoreItem);
      this.m_DirectoryInfoById.put(Long.valueOf(l), localObject2);
      this.m_DirectoryInfoByPath.put(localObject1, localObject2);
      if (!paramBoolean) {
        break label280;
      }
    }
    while (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        GalleryDatabase.ExtraDirectoryInfo localExtraDirectoryInfo = GalleryDatabase.getExtraDirectoryInfo(l);
        HandlerUtils.sendMessage(MediaStoreDirectoryManager.this, 10000, new Object[] { Long.valueOf(l), localExtraDirectoryInfo });
      }
    }))
    {
      Log.e(this.TAG, "addToDirectory() - Fail to post to media content thread to query extra info");
      return;
      return;
      label176:
      if (!((DirectoryInfo)localObject1).media.add((Media)paramMediaStoreItem))
      {
        ((DirectoryInfo)localObject1).recycledMedia.remove(paramMediaStoreItem);
        return;
      }
      localObject2 = ((DirectoryInfo)localObject1).extraInfo;
      if (localObject2 == null) {}
      for (;;)
      {
        notifyMediaCreated((DirectoryInfo)localObject1, (Media)paramMediaStoreItem, paramInt);
        if ((Media.FLAG_RESTORE_FROM_RECYCLE_BIN & paramInt) == 0) {
          break;
        }
        createDefaultMediaSet((DirectoryInfo)localObject1);
        break;
        if ((((GalleryDatabase.ExtraDirectoryInfo)localObject2).oneplusFlags & 0x20) != 0L) {
          paramMediaStoreItem.notifyParentVisibilityChanged(false);
        }
      }
      label270:
      Log.e(this.TAG, "addToDirectory() - No directory path");
      return;
      label280:
      Log.d(this.TAG, "addToDirectory() - New directory found : (" + l + ") " + (String)localObject1);
    }
    this.m_NumOfDirQueryingExtraInfo += 1;
  }
  
  private void createDefaultMediaSet(DirectoryInfo paramDirectoryInfo)
  {
    int i = this.m_OpenedMediaSetLists.size() - 1;
    if (i >= 0)
    {
      DirectoryMediaSetList localDirectoryMediaSetList = (DirectoryMediaSetList)this.m_OpenedMediaSetLists.get(i);
      MediaType localMediaType = localDirectoryMediaSetList.targetMediaType;
      DirectoryMediaSet localDirectoryMediaSet = (DirectoryMediaSet)paramDirectoryInfo.defaultMediaSets.get(localMediaType);
      if (localDirectoryMediaSet != null) {}
      for (;;)
      {
        localDirectoryMediaSetList.addMediaSet(localDirectoryMediaSet, true);
        i -= 1;
        break;
        localDirectoryMediaSet = new DirectoryMediaSet((MediaStoreMediaSource)getMediaSource(), this, true, paramDirectoryInfo.id, paramDirectoryInfo.path, paramDirectoryInfo.extraInfo, localMediaType);
        paramDirectoryInfo.defaultMediaSets.put(localMediaType, localDirectoryMediaSet);
      }
    }
  }
  
  private void deleteDefaultMediaSet(DirectoryInfo paramDirectoryInfo)
  {
    int i = this.m_OpenedMediaSetLists.size() - 1;
    if (i >= 0)
    {
      localObject1 = (DirectoryMediaSetList)this.m_OpenedMediaSetLists.get(i);
      Object localObject2 = ((DirectoryMediaSetList)localObject1).targetMediaType;
      localObject2 = (DirectoryMediaSet)paramDirectoryInfo.defaultMediaSets.get(localObject2);
      if (localObject2 == null) {}
      for (;;)
      {
        i -= 1;
        break;
        ((DirectoryMediaSetList)localObject1).removeMediaSet((MediaSet)localObject2, true);
      }
    }
    Object localObject1 = (DirectoryMediaSet[])paramDirectoryInfo.defaultMediaSets.values().toArray(new DirectoryMediaSet[paramDirectoryInfo.defaultMediaSets.size()]);
    int j = localObject1.length;
    i = 0;
    while (i < j)
    {
      localObject1[i].release();
      i += 1;
    }
    paramDirectoryInfo.defaultMediaSets.clear();
  }
  
  /* Error */
  private boolean isDirectoryIdExistInTheDB(DirectoryInfo paramDirectoryInfo)
  {
    // Byte code:
    //   0: invokestatic 301	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   3: invokevirtual 305	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   6: getstatic 55	com/oneplus/gallery2/media/MediaStoreDirectoryManager:CONTENT_URI_FILE	Landroid/net/Uri;
    //   9: invokevirtual 311	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   12: astore 5
    //   14: aload 5
    //   16: ifnull +110 -> 126
    //   19: getstatic 55	com/oneplus/gallery2/media/MediaStoreDirectoryManager:CONTENT_URI_FILE	Landroid/net/Uri;
    //   22: astore 6
    //   24: aload_1
    //   25: getfield 264	com/oneplus/gallery2/media/MediaStoreDirectoryManager$DirectoryInfo:id	J
    //   28: lstore_2
    //   29: aload 5
    //   31: aload 6
    //   33: iconst_1
    //   34: anewarray 313	java/lang/String
    //   37: dup
    //   38: iconst_0
    //   39: ldc_w 315
    //   42: aastore
    //   43: ldc_w 317
    //   46: iconst_1
    //   47: anewarray 313	java/lang/String
    //   50: dup
    //   51: iconst_0
    //   52: lload_2
    //   53: invokestatic 320	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   56: aastore
    //   57: aconst_null
    //   58: invokevirtual 326	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   61: astore 5
    //   63: aload 5
    //   65: invokeinterface 332 1 0
    //   70: istore 4
    //   72: iload 4
    //   74: ifne +64 -> 138
    //   77: iconst_0
    //   78: istore 4
    //   80: aload 5
    //   82: ifnonnull +62 -> 144
    //   85: aload_0
    //   86: getfield 100	com/oneplus/gallery2/media/MediaStoreDirectoryManager:TAG	Ljava/lang/String;
    //   89: new 213	java/lang/StringBuilder
    //   92: dup
    //   93: ldc_w 334
    //   96: invokespecial 218	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   99: aload_1
    //   100: getfield 264	com/oneplus/gallery2/media/MediaStoreDirectoryManager$DirectoryInfo:id	J
    //   103: invokevirtual 222	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   106: ldc_w 336
    //   109: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: iload 4
    //   114: invokevirtual 339	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   117: invokevirtual 230	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: invokestatic 233	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   123: iload 4
    //   125: ireturn
    //   126: aload_0
    //   127: getfield 100	com/oneplus/gallery2/media/MediaStoreDirectoryManager:TAG	Ljava/lang/String;
    //   130: ldc_w 341
    //   133: invokestatic 344	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   136: iconst_0
    //   137: ireturn
    //   138: iconst_1
    //   139: istore 4
    //   141: goto -61 -> 80
    //   144: aload 5
    //   146: invokeinterface 347 1 0
    //   151: goto -66 -> 85
    //   154: astore 5
    //   156: aconst_null
    //   157: astore 6
    //   159: aload 6
    //   161: ifnull +50 -> 211
    //   164: aload 6
    //   166: aload 5
    //   168: if_acmpne +50 -> 218
    //   171: aload 6
    //   173: athrow
    //   174: astore 5
    //   176: aload_0
    //   177: getfield 100	com/oneplus/gallery2/media/MediaStoreDirectoryManager:TAG	Ljava/lang/String;
    //   180: ldc_w 349
    //   183: aload 5
    //   185: invokestatic 352	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   188: goto -103 -> 85
    //   191: astore 6
    //   193: aload 5
    //   195: ifnonnull +6 -> 201
    //   198: aload 6
    //   200: athrow
    //   201: aload 5
    //   203: invokeinterface 347 1 0
    //   208: goto -10 -> 198
    //   211: aload 5
    //   213: astore 6
    //   215: goto -44 -> 171
    //   218: aload 6
    //   220: aload 5
    //   222: invokevirtual 356	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   225: goto -54 -> 171
    //   228: astore 5
    //   230: aconst_null
    //   231: astore 6
    //   233: iconst_0
    //   234: istore 4
    //   236: goto -77 -> 159
    //   239: astore 5
    //   241: iconst_0
    //   242: istore 4
    //   244: goto -85 -> 159
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	247	0	this	MediaStoreDirectoryManager
    //   0	247	1	paramDirectoryInfo	DirectoryInfo
    //   28	25	2	l	long
    //   70	173	4	bool	boolean
    //   12	133	5	localObject1	Object
    //   154	13	5	localObject2	Object
    //   174	47	5	localThrowable1	Throwable
    //   228	1	5	localObject3	Object
    //   239	1	5	localObject4	Object
    //   22	150	6	localUri	Uri
    //   191	8	6	localObject5	Object
    //   213	19	6	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   144	151	154	finally
    //   171	174	174	java/lang/Throwable
    //   218	225	174	java/lang/Throwable
    //   63	72	191	finally
    //   19	63	228	finally
    //   198	201	239	finally
    //   201	208	239	finally
  }
  
  private void notifyDirectoryRenamed(DirectoryInfo paramDirectoryInfo, String paramString1, String paramString2)
  {
    Object localObject = paramDirectoryInfo.defaultMediaSets.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((DirectoryMediaSet)((Iterator)localObject).next()).onDirectoryRenamed(paramString1, paramString2);
    }
    paramDirectoryInfo = paramDirectoryInfo.mediaSets.values().iterator();
    while (paramDirectoryInfo.hasNext())
    {
      localObject = (List)paramDirectoryInfo.next();
      int i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((DirectoryMediaSet)((List)localObject).get(i)).onDirectoryRenamed(paramString1, paramString2);
        i -= 1;
      }
    }
  }
  
  private void notifyExtraDirectoryInfoUpdated(DirectoryInfo paramDirectoryInfo, GalleryDatabase.ExtraDirectoryInfo paramExtraDirectoryInfo)
  {
    Object localObject = paramDirectoryInfo.defaultMediaSets.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((DirectoryMediaSet)((Iterator)localObject).next()).onExtraDirectoryInfoUpdated(paramExtraDirectoryInfo);
    }
    paramDirectoryInfo = paramDirectoryInfo.mediaSets.values().iterator();
    while (paramDirectoryInfo.hasNext())
    {
      localObject = (List)paramDirectoryInfo.next();
      int i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((DirectoryMediaSet)((List)localObject).get(i)).onExtraDirectoryInfoUpdated(paramExtraDirectoryInfo);
        i -= 1;
      }
    }
  }
  
  private void notifyMediaCreated(DirectoryInfo paramDirectoryInfo, Media paramMedia, int paramInt)
  {
    Object localObject = paramDirectoryInfo.defaultMediaSets.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((DirectoryMediaSet)((Iterator)localObject).next()).onMediaCreated(paramMedia, paramInt);
    }
    paramDirectoryInfo = paramDirectoryInfo.mediaSets.values().iterator();
    while (paramDirectoryInfo.hasNext())
    {
      localObject = (List)paramDirectoryInfo.next();
      int i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((DirectoryMediaSet)((List)localObject).get(i)).onMediaCreated(paramMedia, paramInt);
        i -= 1;
      }
    }
  }
  
  private void notifyMediaDeleted(DirectoryInfo paramDirectoryInfo, Media paramMedia, int paramInt)
  {
    Object localObject = paramDirectoryInfo.defaultMediaSets.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((DirectoryMediaSet)((Iterator)localObject).next()).onMediaDeleted(paramMedia, paramInt);
    }
    paramDirectoryInfo = paramDirectoryInfo.mediaSets.values().iterator();
    while (paramDirectoryInfo.hasNext())
    {
      localObject = (List)paramDirectoryInfo.next();
      int i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((DirectoryMediaSet)((List)localObject).get(i)).onMediaDeleted(paramMedia, paramInt);
        i -= 1;
      }
    }
  }
  
  private void notifyMediaUpdated(DirectoryInfo paramDirectoryInfo, Media paramMedia, int paramInt)
  {
    Object localObject = paramDirectoryInfo.defaultMediaSets.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((DirectoryMediaSet)((Iterator)localObject).next()).onMediaUpdated(paramMedia, paramInt);
    }
    paramDirectoryInfo = paramDirectoryInfo.mediaSets.values().iterator();
    while (paramDirectoryInfo.hasNext())
    {
      localObject = (List)paramDirectoryInfo.next();
      int i = ((List)localObject).size() - 1;
      while (i >= 0)
      {
        ((DirectoryMediaSet)((List)localObject).get(i)).onMediaUpdated(paramMedia, paramInt);
        i -= 1;
      }
    }
  }
  
  private void onDirectoriesReady()
  {
    Log.v(this.TAG, "onDirectoriesReady()");
    this.m_DirectoriesReady = true;
    int i = this.m_OpenedMediaSetLists.size() - 1;
    while (i >= 0)
    {
      ((DirectoryMediaSetList)this.m_OpenedMediaSetLists.get(i)).ready();
      i -= 1;
    }
  }
  
  private void onDirectoryInfoReady(DirectoryInfo paramDirectoryInfo)
  {
    createDefaultMediaSet(paramDirectoryInfo);
  }
  
  private void onDirectoryMediaSetListReleased(DirectoryMediaSetList paramDirectoryMediaSetList)
  {
    if (this.m_OpenedMediaSetLists.remove(paramDirectoryMediaSetList))
    {
      if (this.m_OpenedMediaSetLists.isEmpty())
      {
        Log.v(this.TAG, "onDirectoryMediaSetListReleased() - All lists released");
        paramDirectoryMediaSetList = this.m_DirectoryInfoById.values().iterator();
        if (!paramDirectoryMediaSetList.hasNext()) {
          return;
        }
        DirectoryInfo localDirectoryInfo = (DirectoryInfo)paramDirectoryMediaSetList.next();
        DirectoryMediaSet[] arrayOfDirectoryMediaSet = (DirectoryMediaSet[])localDirectoryInfo.defaultMediaSets.values().toArray(new DirectoryMediaSet[localDirectoryInfo.defaultMediaSets.size()]);
        localDirectoryInfo.defaultMediaSets.clear();
        int i = arrayOfDirectoryMediaSet.length;
        for (;;)
        {
          i -= 1;
          if (i < 0) {
            break;
          }
          arrayOfDirectoryMediaSet[i].release();
        }
      }
    }
    else {
      return;
    }
    Log.v(this.TAG, "onDirectoryMediaSetListReleased() - ", Integer.valueOf(this.m_OpenedMediaSetLists.size()), " list(s) opened");
    return;
  }
  
  private void onExtraDirectoryInfoGet(long paramLong, GalleryDatabase.ExtraDirectoryInfo paramExtraDirectoryInfo)
  {
    int i = 0;
    this.m_NumOfDirQueryingExtraInfo -= 1;
    DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo != null)
    {
      if (localDirectoryInfo.extraInfo != null) {
        break label74;
      }
      localDirectoryInfo.extraInfo = paramExtraDirectoryInfo;
      notifyExtraDirectoryInfoUpdated(localDirectoryInfo, paramExtraDirectoryInfo);
      if (!localDirectoryInfo.isReady) {
        break label99;
      }
      label65:
      if (!this.m_DirectoriesReady) {
        break label114;
      }
    }
    label74:
    label99:
    label114:
    while (this.m_NumOfDirQueryingExtraInfo > 0)
    {
      return;
      return;
      if (localDirectoryInfo.extraInfo.mediaAddedTime <= paramExtraDirectoryInfo.mediaAddedTime) {
        i = 1;
      }
      if (i != 0) {
        break;
      }
      return;
      localDirectoryInfo.isReady = true;
      onDirectoryInfoReady(localDirectoryInfo);
      break label65;
    }
    onDirectoriesReady();
  }
  
  private void onMediaFileMoved(MediaStoreItem paramMediaStoreItem, int paramInt)
  {
    removeFromDirectory(paramMediaStoreItem, paramMediaStoreItem.getPreviousParentId(), 0);
    addToDirectory(paramMediaStoreItem, false, 0);
  }
  
  private void removeFromDirectory(MediaStoreItem paramMediaStoreItem, int paramInt)
  {
    removeFromDirectory(paramMediaStoreItem, paramMediaStoreItem.getParentId(), paramInt);
  }
  
  private void removeFromDirectory(MediaStoreItem paramMediaStoreItem, final long paramLong, int paramInt)
  {
    int j = 0;
    if (paramLong >= 0L) {}
    for (int i = 1; i == 0; i = 0) {
      return;
    }
    final DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo == null) {}
    boolean bool;
    label96:
    do
    {
      return;
      bool = localDirectoryInfo.media.remove(paramMediaStoreItem);
      if ((Media.FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break;
      }
      i = j;
      if (i == 0) {
        break label194;
      }
      if (!localDirectoryInfo.recycledMedia.add((Media)paramMediaStoreItem)) {
        break label214;
      }
      notifyMediaDeleted(localDirectoryInfo, (Media)paramMediaStoreItem, paramInt);
    } while (!localDirectoryInfo.media.isEmpty());
    if (!localDirectoryInfo.recycledMedia.isEmpty()) {
      Log.d(this.TAG, "removeFromDirectory() - All media in directory (" + localDirectoryInfo.id + ") " + localDirectoryInfo.path + " has been recycled");
    }
    for (;;)
    {
      deleteDefaultMediaSet(localDirectoryInfo);
      return;
      i = 1;
      break;
      label194:
      if ((bool) || (localDirectoryInfo.recycledMedia.remove(paramMediaStoreItem))) {
        break label96;
      }
      return;
      label214:
      return;
      Log.d(this.TAG, "removeFromDirectory() - Directory (" + localDirectoryInfo.id + ") " + localDirectoryInfo.path + " becomes empty");
      this.m_DirectoryInfoById.remove(Long.valueOf(localDirectoryInfo.id));
      this.m_DirectoryInfoByPath.remove(localDirectoryInfo.path);
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          if (MediaStoreDirectoryManager.this.isDirectoryIdExistInTheDB(localDirectoryInfo)) {}
          while (!GalleryDatabase.deleteExtraDirectoryInfo(paramLong)) {
            return;
          }
          Log.d(MediaStoreDirectoryManager.this.TAG, "removeFromDirectory() - Extra info of " + paramLong + " deleted");
        }
      });
    }
  }
  
  private void setupSystemDirTable()
  {
    Object localObject = new ArrayList();
    ((List)localObject).add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
    this.m_SystemDirectoryTable.put(SystemDirectoryType.CAMERA, Collections.unmodifiableList((List)localObject));
    localObject = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    this.m_SystemDirectoryTable.put(SystemDirectoryType.SCREENSHOTS, Arrays.asList(new String[] { Path.combine(new String[] { localObject, "Screenshots" }) }));
    localObject = this.m_SystemDirectoryTable.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      List localList = (List)((Iterator)localObject).next();
      int i = localList.size() - 1;
      while (i >= 0)
      {
        this.m_SystemDirPathPrefixList.add((String)localList.get(i) + "/");
        i -= 1;
      }
    }
  }
  
  public String getDirectoryPath(long paramLong)
  {
    DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo == null) {
      return null;
    }
    return localDirectoryInfo.path;
  }
  
  GalleryDatabase.ExtraDirectoryInfo getExtraDirectoryInfo(long paramLong)
  {
    DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo == null) {
      return null;
    }
    return localDirectoryInfo.extraInfo;
  }
  
  public Iterable<Media> getMedia(long paramLong, MediaType paramMediaType)
  {
    verifyAccess();
    DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo == null) {
      return Collections.EMPTY_LIST;
    }
    return new MediaIterable(paramMediaType, localDirectoryInfo.media);
  }
  
  public List<String> getSystemDirectoryPaths(SystemDirectoryType paramSystemDirectoryType)
  {
    return (List)this.m_SystemDirectoryTable.get(paramSystemDirectoryType);
  }
  
  public void getSystemDirectoryPaths(SystemDirectoryType paramSystemDirectoryType, List<String> paramList)
  {
    paramSystemDirectoryType = (List)this.m_SystemDirectoryTable.get(paramSystemDirectoryType);
    if (paramSystemDirectoryType == null) {
      return;
    }
    paramList.addAll(paramSystemDirectoryType);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    onExtraDirectoryInfoGet(((Long)paramMessage[0]).longValue(), (GalleryDatabase.ExtraDirectoryInfo)paramMessage[1]);
  }
  
  public boolean isPathInSystemDirectory(String paramString)
  {
    if (paramString != null)
    {
      int i = this.m_SystemDirPathPrefixList.size() - 1;
      for (;;)
      {
        if (i < 0) {
          break label51;
        }
        if (paramString.startsWith((String)this.m_SystemDirPathPrefixList.get(i))) {
          break;
        }
        i -= 1;
      }
    }
    return false;
    return true;
    label51:
    return false;
  }
  
  protected void onBindToMediaSource(MediaStoreMediaSource paramMediaStoreMediaSource)
  {
    super.onBindToMediaSource(paramMediaStoreMediaSource);
  }
  
  void onDirectoryMediaSetCreated(long paramLong, DirectoryMediaSet paramDirectoryMediaSet)
  {
    if (paramDirectoryMediaSet.isDefault()) {
      return;
    }
    DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localDirectoryInfo == null)
    {
      Log.e(this.TAG, "onDirectoryMediaSetCreated() - No directory info for " + paramLong);
      return;
    }
    Object localObject = (List)localDirectoryInfo.mediaSets.get(paramDirectoryMediaSet.getTargetMediaType());
    if (localObject != null) {}
    for (;;)
    {
      ((List)localObject).add(paramDirectoryMediaSet);
      return;
      localObject = new ArrayList();
      localDirectoryInfo.mediaSets.put(paramDirectoryMediaSet.getTargetMediaType(), localObject);
    }
  }
  
  void onDirectoryMediaSetReleased(long paramLong, DirectoryMediaSet paramDirectoryMediaSet)
  {
    Object localObject1 = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(paramLong));
    if (localObject1 == null) {}
    Object localObject2;
    do
    {
      return;
      localObject2 = paramDirectoryMediaSet.getTargetMediaType();
      if (paramDirectoryMediaSet.isDefault()) {
        break;
      }
      localObject2 = (List)((DirectoryInfo)localObject1).mediaSets.get(localObject2);
    } while ((localObject2 == null) || (!((List)localObject2).remove(paramDirectoryMediaSet)) || (!((List)localObject2).isEmpty()));
    ((DirectoryInfo)localObject1).mediaSets.remove(paramDirectoryMediaSet.getTargetMediaType());
    return;
    ((DirectoryInfo)localObject1).defaultMediaSets.remove(localObject2);
    int i = this.m_OpenedMediaSetLists.size() - 1;
    label122:
    if (i >= 0)
    {
      localObject1 = (DirectoryMediaSetList)this.m_OpenedMediaSetLists.get(i);
      if (((DirectoryMediaSetList)localObject1).targetMediaType == localObject2) {
        break label162;
      }
    }
    for (;;)
    {
      i -= 1;
      break label122;
      break;
      label162:
      ((DirectoryMediaSetList)localObject1).removeMediaSet(paramDirectoryMediaSet, true);
    }
  }
  
  protected void onInitialize()
  {
    long l1;
    if (MediaContentThread.current() != null)
    {
      setupSystemDirTable();
      super.onInitialize();
      enableMediaChangeCallback();
      l1 = SystemClock.elapsedRealtime();
      Iterator localIterator = ((MediaStoreMediaSource)getMediaSource()).getMedia(null, 0).iterator();
      while (localIterator.hasNext())
      {
        Media localMedia = (Media)localIterator.next();
        if ((localMedia instanceof MediaStoreMedia)) {
          addToDirectory((MediaStoreMedia)localMedia, true, 0);
        }
      }
    }
    throw new RuntimeException("No media content thread");
    long l2 = SystemClock.elapsedRealtime();
    Log.d(this.TAG, "onInitialize() - Take " + (l2 - l1) + " ms to find " + this.m_DirectoryInfoById.size() + " directories");
  }
  
  protected void onMediaCreated(Media paramMedia, int paramInt)
  {
    if (!(paramMedia instanceof MediaStoreItem)) {}
    while ((Media.FLAG_SUB_MEDIA & paramInt) != 0) {
      return;
    }
    addToDirectory((MediaStoreItem)paramMedia, false, paramInt);
  }
  
  protected void onMediaDeleted(Media paramMedia, int paramInt)
  {
    if (!(paramMedia instanceof MediaStoreItem)) {}
    while ((Media.FLAG_SUB_MEDIA & paramInt) != 0) {
      return;
    }
    removeFromDirectory((MediaStoreItem)paramMedia, paramInt);
  }
  
  protected void onMediaTableReady()
  {
    super.onMediaTableReady();
    if (this.m_DirectoriesReady) {}
    while (this.m_NumOfDirQueryingExtraInfo > 0) {
      return;
    }
    onDirectoriesReady();
  }
  
  protected void onMediaUpdated(Media paramMedia, int paramInt)
  {
    int k = 0;
    int i;
    int j;
    if ((Media.FLAG_VISIBILITY_CHANGED & paramInt) == 0)
    {
      i = 0;
      if ((Media.FLAG_FILE_PATH_CHANGED & paramInt) != 0) {
        break label256;
      }
      j = 0;
      label24:
      if ((Media.FLAG_SUB_MEDIA & paramInt) != 0) {
        break label262;
      }
      label32:
      if (k != 0) {
        break label268;
      }
      if (j == 0) {
        break label269;
      }
    }
    for (;;)
    {
      if ((paramMedia instanceof MediaStoreItem))
      {
        if ((MediaStoreMedia.FLAG_PARENT_ID_CHANGED & paramInt) != 0) {
          break label275;
        }
        long l = ((MediaStoreItem)paramMedia).getParentId();
        DirectoryInfo localDirectoryInfo = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(l));
        if (localDirectoryInfo == null) {
          break label285;
        }
        if (isPathInSystemDirectory(paramMedia.getPreviousFilePath())) {
          break label306;
        }
        notifyMediaUpdated(localDirectoryInfo, paramMedia, paramInt);
        String str = Path.getDirectoryPath(paramMedia.getFilePath());
        if (TextUtils.equals(str, localDirectoryInfo.path)) {
          break label313;
        }
        if (isPathInSystemDirectory(paramMedia.getFilePath())) {
          break label314;
        }
        paramMedia = localDirectoryInfo.path;
        Log.d(this.TAG, "onMediaUpdated() - Directory (" + localDirectoryInfo.id + ") renamed from " + localDirectoryInfo.path + " to " + str);
        this.m_DirectoryInfoByPath.remove(paramMedia);
        this.m_DirectoryInfoByPath.put(str, localDirectoryInfo);
        localDirectoryInfo.path = str;
        notifyDirectoryRenamed(localDirectoryInfo, paramMedia, str);
        return;
        i = 1;
        break;
        label256:
        j = 1;
        break label24;
        label262:
        k = 1;
        break label32;
        label268:
        return;
        label269:
        if (i == 0) {
          return;
        }
      }
    }
    return;
    label275:
    onMediaFileMoved((MediaStoreItem)paramMedia, paramInt);
    return;
    label285:
    if (isPathInSystemDirectory(paramMedia.getFilePath())) {
      return;
    }
    onMediaCreated(paramMedia, paramInt);
    return;
    label306:
    onMediaCreated(paramMedia, paramInt);
    return;
    label313:
    return;
    label314:
    onMediaDeleted(paramMedia, paramInt);
  }
  
  public MediaSetList openDirectoryMediaSetList(MediaType paramMediaType)
  {
    verifyAccess();
    DirectoryMediaSetList localDirectoryMediaSetList;
    DirectoryInfo localDirectoryInfo;
    DirectoryMediaSet localDirectoryMediaSet;
    if (isRunningOrInitializing(true))
    {
      localDirectoryMediaSetList = new DirectoryMediaSetList(paramMediaType);
      this.m_OpenedMediaSetLists.add(localDirectoryMediaSetList);
      Log.v(this.TAG, "openDirectoryMediaSetList() - ", this.m_OpenedMediaSetLists.size() + " list(s) opened");
      Iterator localIterator = this.m_DirectoryInfoById.values().iterator();
      if (!localIterator.hasNext()) {
        break label199;
      }
      localDirectoryInfo = (DirectoryInfo)localIterator.next();
      localDirectoryMediaSet = (DirectoryMediaSet)localDirectoryInfo.defaultMediaSets.get(paramMediaType);
      if (localDirectoryMediaSet == null) {
        break label142;
      }
    }
    for (;;)
    {
      localDirectoryMediaSetList.addMediaSet(localDirectoryMediaSet, false);
      break;
      return MediaSetList.EMPTY;
      label142:
      if (!localDirectoryInfo.isReady) {
        break;
      }
      localDirectoryMediaSet = new DirectoryMediaSet((MediaStoreMediaSource)getMediaSource(), this, true, localDirectoryInfo.id, localDirectoryInfo.path, localDirectoryInfo.extraInfo, paramMediaType);
      localDirectoryInfo.defaultMediaSets.put(paramMediaType, localDirectoryMediaSet);
    }
    label199:
    if (!this.m_DirectoriesReady) {
      return localDirectoryMediaSetList;
    }
    localDirectoryMediaSetList.ready();
    return localDirectoryMediaSetList;
  }
  
  void updateLastMediaAddedTime(final DirectoryMediaSet paramDirectoryMediaSet, long paramLong)
  {
    long l = paramDirectoryMediaSet.getDirectoryId();
    paramDirectoryMediaSet = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(l));
    if (paramDirectoryMediaSet != null) {
      if (paramDirectoryMediaSet.extraInfo == null) {
        break label94;
      }
    }
    for (;;)
    {
      paramDirectoryMediaSet.extraInfo.mediaAddedTime = paramLong;
      paramDirectoryMediaSet = paramDirectoryMediaSet.extraInfo.clone();
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.updateExtraDirectoryInfo(paramDirectoryMediaSet);
        }
      });
      return;
      Log.e(this.TAG, "updateLastMediaAddedTime() - No directory info for " + l);
      return;
      label94:
      paramDirectoryMediaSet.extraInfo = new GalleryDatabase.ExtraDirectoryInfo(l);
    }
  }
  
  void updateVisibility(DirectoryMediaSet paramDirectoryMediaSet, boolean paramBoolean)
  {
    long l = paramDirectoryMediaSet.getDirectoryId();
    paramDirectoryMediaSet = (DirectoryInfo)this.m_DirectoryInfoById.get(Long.valueOf(l));
    final Object localObject;
    if (paramDirectoryMediaSet != null)
    {
      if (paramDirectoryMediaSet.extraInfo == null) {
        break label163;
      }
      if (!paramBoolean) {
        break label178;
      }
      localObject = paramDirectoryMediaSet.extraInfo;
    }
    for (((GalleryDatabase.ExtraDirectoryInfo)localObject).oneplusFlags &= 0xFFFFFFFFFFFFFFDF;; ((GalleryDatabase.ExtraDirectoryInfo)localObject).oneplusFlags |= 0x20)
    {
      localObject = paramDirectoryMediaSet.extraInfo.clone();
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.updateExtraDirectoryInfo(localObject);
        }
      });
      localObject = paramDirectoryMediaSet.media.iterator();
      while (((Iterator)localObject).hasNext())
      {
        Media localMedia = (Media)((Iterator)localObject).next();
        if ((localMedia instanceof MediaStoreItem)) {
          ((MediaStoreItem)localMedia).notifyParentVisibilityChanged(paramBoolean);
        }
      }
      Log.e(this.TAG, "updateVisibility() - No directory info for " + l);
      return;
      label163:
      paramDirectoryMediaSet.extraInfo = new GalleryDatabase.ExtraDirectoryInfo(l);
      break;
      label178:
      localObject = paramDirectoryMediaSet.extraInfo;
    }
    paramDirectoryMediaSet = paramDirectoryMediaSet.recycledMedia.iterator();
    while (paramDirectoryMediaSet.hasNext())
    {
      localObject = (Media)paramDirectoryMediaSet.next();
      if ((localObject instanceof MediaStoreItem)) {
        ((MediaStoreItem)localObject).notifyParentVisibilityChanged(paramBoolean);
      }
    }
  }
  
  private static final class DirectoryInfo
  {
    public final Map<MediaType, DirectoryMediaSet> defaultMediaSets = new HashMap();
    public GalleryDatabase.ExtraDirectoryInfo extraInfo;
    public final long id;
    public boolean isReady;
    public final Set<Media> media = new HashSet();
    public final Map<MediaType, List<DirectoryMediaSet>> mediaSets = new HashMap();
    public String path;
    public final Set<Media> recycledMedia = new HashSet();
    
    public DirectoryInfo(long paramLong, String paramString)
    {
      this.id = paramLong;
      this.path = paramString;
    }
  }
  
  private final class DirectoryMediaSetList
    extends BaseMediaSetList
  {
    public final MediaType targetMediaType;
    
    public DirectoryMediaSetList(MediaType paramMediaType)
    {
      super();
      this.targetMediaType = paramMediaType;
    }
    
    public void release()
    {
      super.release();
      MediaStoreDirectoryManager.this.onDirectoryMediaSetListReleased(this);
    }
  }
  
  public static enum SystemDirectoryType
  {
    CAMERA,  SCREENSHOTS;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaStoreDirectoryManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */