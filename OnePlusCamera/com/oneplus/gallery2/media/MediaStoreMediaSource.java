package com.oneplus.gallery2.media;

import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.HandleSet;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.IntentEventArgs;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.gallery2.GalleryLib;
import com.oneplus.gallery2.MediaContentThread;
import com.oneplus.gallery2.location.LocationManager;
import com.oneplus.gallery2.location.LocationManager.AddressCallback;
import com.oneplus.io.FileUtils;
import com.oneplus.io.Path;
import com.oneplus.net.NetworkManager;
import com.oneplus.util.CollectionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MediaStoreMediaSource
  extends BaseMediaSource
{
  private static final String ACTION_CLEAR_IMAGE_CACHE = "com.oneplus.camera.service.CLEAR_IMAGE_CACHE";
  private static final String ACTION_MEDIA_DELETED = "com.oneplus.gallery.MEDIA_STORE_MEDIA_DELETED";
  private static final Uri CONTENT_URI_FILE;
  private static final Uri CONTENT_URI_IMAGE;
  private static final Uri CONTENT_URI_OBJECT = Uri.parse("content://media/external/object");
  private static final String CONTENT_URI_STRING_FILE = CONTENT_URI_FILE.toString();
  private static final Uri CONTENT_URI_VIDEO;
  private static final long DURATION_GET_ADDRESSES_DELAY = 500L;
  private static final long DURATION_SYNC_MEDIA_WITH_MEDIA_STORE_DELAY = 1000L;
  private static final long DURATION_SYNC_SINGLE_MEDIA_WITH_MEDIA_STORE_DELAY = 50L;
  public static final EventKey<IntentEventArgs> EVENT_NEW_MEDIA_INTENT_RECEIVED;
  public static final int FLAG_CAMERA_ROLL_ONLY = FLAGS_GROUP.nextIntFlag();
  private static final String INTENT_NEW_PICTURE = "com.oneplus.camera.intent.action.NEW_PICTURE";
  private static final String INTENT_NEW_VIDEO = "com.oneplus.camera.intent.action.NEW_VIDEO";
  private static final int MAX_PENDING_ADDRESS_OBTAINING_MEDIA = 64;
  private static final int MAX_PENDING_MEDIA_SYNC_COUNT = 16;
  private static final int MEDIA_STORE_QUERY_CHUNK_SIZE = 512;
  private static final String MEDIA_STORE_QUERY_COND = "media_type=1 OR media_type=3";
  private static final String MEDIA_STORE_QUERY_COND_CAMERA_ROLL = "(media_type=1 OR media_type=3) AND _data LIKE '%/DCIM/%'";
  private static final String MEDIA_STORE_QUERY_SORT_ORDER = "datetaken DESC, _id DESC";
  private static final int MODE_CAMERA_ROLL = 1;
  private static final int MODE_NORMAL = 0;
  private static final int MSG_MEDIA_CONTENT_URI_QUERIED = 10021;
  private static final int MSG_MEDIA_DELETED = 10010;
  private static final int MSG_MEDIA_STORE_ACCESS_COMPLETED = 10020;
  private static final int MSG_START_SYNC_MEDIA_WITH_MEDIA_STORE = 10001;
  private static final int MSG_START_SYNC_SINGLE_MEDIA_WITH_MEDIA_STORE = 10004;
  private static final int MSG_SYNC_MEDIA_WITH_MEDIA_STORE = 10000;
  private static final int MSG_SYNC_SINGLE_MEDIA_WITH_MEDIA_STORE = 10002;
  private static final String RAW_FILE_DELETION_COND = "_data=?";
  private static final boolean SIMULATE_SLOW_MEDIA_STORE_QUERY = false;
  private int m_CameraRollMediaCount;
  private int m_CameraRollOnlyFlagCount;
  private final Set<Media> m_CandidateMediaToRemove = new HashSet();
  private final ContentObserver.ContentChangeCallback m_ContentChangeCallback = new ContentObserver.ContentChangeCallback()
  {
    public void onContentChanged(Uri paramAnonymousUri)
    {
      MediaStoreMediaSource.this.onMediaStoreContentChanged(paramAnonymousUri);
    }
  };
  private final HandleSet m_ContentChangeCallbackHandles = new HandleSet(new Handle[0]);
  private boolean m_HasPendingMediaSyncWithMediaStore;
  private boolean m_HasPendingSingleMediaSyncWithMediaStore;
  private final List<String> m_HiddenPathPrefixList = new ArrayList();
  private boolean m_IsMediaAddressesObtainingScheduled;
  private boolean m_IsMediaTableReady;
  private final LocationManager.AddressCallback m_MediaAddressCallback = new LocationManager.AddressCallback()
  {
    public void onAddressesObtained(Handle paramAnonymousHandle, Locale paramAnonymousLocale, Map<?, Address> paramAnonymousMap, int paramAnonymousInt)
    {
      MediaStoreMediaSource.this.onMediaAddressesObtained(paramAnonymousHandle, paramAnonymousLocale, paramAnonymousMap, paramAnonymousInt);
    }
  };
  private final BroadcastReceiver m_MediaIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      MediaStoreMediaSource.this.onMediaIntentReceived(paramAnonymousIntent);
    }
  };
  private final Map<Long, List<CallbackHandle<MediaSource.MediaObtainCallback>>> m_MediaObtainCallbackHandles = new HashMap();
  private final BroadcastReceiver m_MediaSetIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      MediaStoreMediaSource.this.onMediaSetIntentReceived(paramAnonymousIntent);
    }
  };
  private Object m_MediaStoreSyncToken;
  private int m_Mode = 0;
  private boolean m_NeedToSyncMediaWhenPermsReady = true;
  private final Runnable m_ObtainMediaAddressesRunnable = new Runnable()
  {
    public void run()
    {
      MediaStoreMediaSource.this.obtainMediaAddresses();
    }
  };
  private final Set<MediaStoreMedia> m_PendingAddressObtainingMedia = new HashSet();
  private final Set<Long> m_PendingMediaIdToSync = new HashSet();
  private int m_PendingMediaSyncCount;
  private boolean m_PermissionsReady;
  private final Set<Media> m_RecycledMedia = new HashSet();
  private final List<MediaStoreMedia.DbValues> m_TempDbValueList = new ArrayList();
  private final List<MediaStoreMedia.FileInfo> m_TempFileInfoList = new ArrayList();
  
  static
  {
    EVENT_NEW_MEDIA_INTENT_RECEIVED = new EventKey("NewMediaIntentReceived", IntentEventArgs.class, MediaStoreMediaSource.class);
    CONTENT_URI_FILE = MediaStore.Files.getContentUri("external");
    CONTENT_URI_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    CONTENT_URI_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
  }
  
  MediaStoreMediaSource(BaseApplication paramBaseApplication)
  {
    super("Media store media source", paramBaseApplication);
  }
  
  private boolean addToGroupedMedia(MediaStoreMedia paramMediaStoreMedia)
  {
    if (paramMediaStoreMedia == null) {}
    BaseGroupedMedia localBaseGroupedMedia;
    label55:
    label90:
    for (;;)
    {
      return false;
      if (paramMediaStoreMedia.isSubMedia())
      {
        localBaseGroupedMedia = (BaseGroupedMedia)getMedia(paramMediaStoreMedia.getGroupId(), 0);
        int i;
        if (localBaseGroupedMedia != null)
        {
          i = 0;
          if (i != 0) {
            break label55;
          }
        }
        for (;;)
        {
          if (!localBaseGroupedMedia.addSubMedia(paramMediaStoreMedia)) {
            break label90;
          }
          if (i != 0) {
            break label94;
          }
          return true;
          i = 1;
          break;
          localBaseGroupedMedia = createGroupedMedia(paramMediaStoreMedia);
          if (localBaseGroupedMedia == null) {
            break label92;
          }
          Log.d(this.TAG, "addToGroupedMedia() - Create " + localBaseGroupedMedia);
        }
      }
    }
    label92:
    return false;
    label94:
    addMedia(localBaseGroupedMedia, true, prepareMediaFlagsForCallback(localBaseGroupedMedia));
    return true;
  }
  
  private void callOnMediaObtained(long paramLong, Media paramMedia)
  {
    int i = 0;
    List localList = (List)this.m_MediaObtainCallbackHandles.remove(Long.valueOf(paramLong));
    if (localList == null) {
      return;
    }
    label31:
    Uri localUri;
    label66:
    String str;
    label76:
    int j;
    label87:
    MediaSource.MediaObtainCallback localMediaObtainCallback;
    if (paramMedia == null)
    {
      if (paramMedia != null) {
        break label136;
      }
      localUri = Uri.parse(CONTENT_URI_STRING_FILE + "/" + paramLong);
      if (paramMedia != null) {
        break label147;
      }
      str = MediaStoreMedia.getId(paramLong);
      j = localList.size() - 1;
      if (j >= 0)
      {
        localMediaObtainCallback = (MediaSource.MediaObtainCallback)((CallbackHandle)localList.get(j)).getCallback();
        if (localMediaObtainCallback != null) {
          break label158;
        }
      }
    }
    for (;;)
    {
      j -= 1;
      break label87;
      break;
      i = prepareMediaFlagsForCallback(paramMedia);
      break label31;
      label136:
      localUri = paramMedia.getContentUri();
      break label66;
      label147:
      str = paramMedia.getId();
      break label76;
      label158:
      localMediaObtainCallback.onMediaObtained(this, localUri, str, paramMedia, i);
    }
  }
  
  private void checkTakenTime(MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo)
  {
    if (paramDbValues == null) {}
    while (paramFileInfo == null) {
      return;
    }
    if (paramDbValues.takenTime > 0L) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {}
      switch (paramDbValues.mediaType)
      {
      case 2: 
      default: 
        return;
      }
    }
    paramFileInfo.takenTime = PhotoMediaStoreMedia.getTakenTimeFromFile(PhotoMediaStoreMedia.createContentUri(paramDbValues.id));
    return;
    paramFileInfo.takenTime = paramFileInfo.lastModifiedTime;
  }
  
  private BaseGroupedMedia createGroupedMedia(MediaStoreMedia paramMediaStoreMedia)
  {
    String str = paramMediaStoreMedia.getGroupId();
    if (str == null) {}
    while (!paramMediaStoreMedia.isBurstSubMedia()) {
      return null;
    }
    return new BurstMediaStoreMedia(this, str);
  }
  
  private void deleteMedia(MediaDeletionHandle paramMediaDeletionHandle)
  {
    int j = 1;
    if (Handle.isValid(paramMediaDeletionHandle)) {}
    for (;;)
    {
      Media localMedia;
      ContentProviderClient localContentProviderClient;
      Object localObject1;
      try
      {
        if (!paramMediaDeletionHandle.isCancelled)
        {
          paramMediaDeletionHandle.isCancellable = false;
          localMedia = paramMediaDeletionHandle.getMedia();
          if (!(localMedia instanceof MediaStoreMedia)) {
            break label191;
          }
          localContentProviderClient = BaseApplication.current().getContentResolver().acquireUnstableContentProviderClient(CONTENT_URI_FILE);
          if (localContentProviderClient == null) {
            break label232;
          }
        }
      }
      finally {}
      try
      {
        if ((paramMediaDeletionHandle.getFlags() & Media.FLAG_INCLUDE_RAW_PHOTO) != 0) {
          break label252;
        }
        localObject1 = null;
        if (localContentProviderClient.delete(CONTENT_URI_FILE, "_id=" + ((MediaStoreMedia)localMedia).getMediaId(), null) > 0) {
          break label330;
        }
        Log.w(this.TAG, "deleteMedia() - " + localMedia + " is not found");
      }
      catch (Throwable localThrowable)
      {
        String str;
        Log.e(this.TAG, "deleteMedia() - Fail to delete " + localMedia, localThrowable);
        localContentProviderClient.release();
        i = 0;
        continue;
        localFile = new File(localThrowable);
        if (localFile.exists()) {
          break label460;
        }
        i = 0;
        localContentProviderClient.delete(CONTENT_URI_FILE, "_data=?", new String[] { localThrowable });
        if (i == 0) {
          continue;
        }
        Log.v(this.TAG, "deleteMedia() - RAW file ", localThrowable, " deleted");
        continue;
      }
      finally
      {
        localContentProviderClient.release();
      }
      GalleryDatabase.deleteExtraMediaInfo(((MediaStoreMedia)localMedia).getMediaId());
      localContentProviderClient.release();
      int i = 1;
      if (i == 0) {
        j = 0;
      }
      HandlerUtils.sendMessage(this, 10010, j, 0, paramMediaDeletionHandle);
      return;
      return;
      return;
      label191:
      Log.e(this.TAG, "deleteMedia() - " + localMedia + " is not a media store media");
      HandlerUtils.sendMessage(this, 10010, paramMediaDeletionHandle);
      return;
      label232:
      Log.e(this.TAG, "deleteMedia() - Fail to acquire content provider client");
      HandlerUtils.sendMessage(this, 10010, paramMediaDeletionHandle);
      return;
      label252:
      str = localMedia.getFilePath();
      i = str.lastIndexOf('.');
      if (i < 0)
      {
        str = str + ".dng";
      }
      else
      {
        str = str.substring(0, i) + ".dng";
        continue;
        label330:
        Log.v(this.TAG, "deleteMedia() - ", localMedia, " deleted");
        label460:
        while (localThrowable != null) {
          for (;;)
          {
            File localFile;
            if (!localFile.delete())
            {
              Log.e(this.TAG, "deleteMedia() - Fail to delete RAW file " + localThrowable);
              i = 0;
            }
            else
            {
              i = 1;
            }
          }
        }
      }
    }
  }
  
  private boolean handleQueriedDataInMediaStore(Cursor paramCursor, List<MediaStoreMedia.DbValues> paramList, List<MediaStoreMedia.FileInfo> paramList1)
  {
    paramCursor = MediaStoreMedia.DbValues.read(paramCursor);
    if (paramCursor == null) {}
    while ((paramCursor.data == null) || (isPathInHiddenDirectory(paramCursor.data)) || (FileUtils.isRawFilePath(paramCursor.data)) || (isTiffFilePath(paramCursor.data))) {
      return false;
    }
    paramCursor.extraInfo = GalleryDatabase.getExtraMediaInfo(paramCursor.id);
    MediaStoreMedia.FileInfo localFileInfo = MediaStoreMedia.FileInfo.read(paramCursor.data);
    checkTakenTime(paramCursor, localFileInfo);
    paramList.add(paramCursor);
    paramList1.add(localFileInfo);
    return true;
  }
  
  private boolean isTiffFilePath(String paramString)
  {
    if (paramString != null) {
      paramString = Path.getExtension(paramString).toLowerCase(Locale.US);
    }
    switch (paramString.hashCode())
    {
    default: 
    case 1485219: 
      do
      {
        return false;
        return false;
      } while (!paramString.equals(".tif"));
    }
    for (;;)
    {
      return true;
      if (!paramString.equals(".tiff")) {
        break;
      }
    }
  }
  
  private void obtainMediaAddresses()
  {
    LocationManager localLocationManager;
    HashMap localHashMap;
    if (!GalleryLib.isClient())
    {
      this.m_IsMediaAddressesObtainingScheduled = false;
      if (!this.m_PendingAddressObtainingMedia.isEmpty())
      {
        localLocationManager = (LocationManager)BaseApplication.current().findComponent(LocationManager.class);
        if (localLocationManager == null) {
          break label106;
        }
        localHashMap = new HashMap();
        Iterator localIterator = this.m_PendingAddressObtainingMedia.iterator();
        while (localIterator.hasNext())
        {
          MediaStoreMedia localMediaStoreMedia = (MediaStoreMedia)localIterator.next();
          Location localLocation = localMediaStoreMedia.getLocation();
          if (localLocation != null) {
            localHashMap.put(localMediaStoreMedia, localLocation);
          }
        }
      }
    }
    else
    {
      return;
    }
    return;
    label106:
    this.m_PendingAddressObtainingMedia.clear();
    return;
    this.m_PendingAddressObtainingMedia.clear();
    if (localHashMap.isEmpty()) {}
    do
    {
      return;
      Log.d(this.TAG, "obtainMediaAddresses() - Start obtaining addresses for ", Integer.valueOf(localHashMap.size()), " media");
    } while (Handle.isValid(localLocationManager.getAddresses(localHashMap, null, this.m_MediaAddressCallback, 0)));
    Log.e(this.TAG, "obtainMediaAddresses() - Fail to start obtaining addresses");
  }
  
  private void onLocaleChanged()
  {
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext())
    {
      Media localMedia = (Media)localIterator.next();
      if ((localMedia instanceof MediaStoreMedia)) {
        ((MediaStoreMedia)localMedia).onLocaleChanged();
      }
    }
  }
  
  private void onMediaAddressesObtained(Handle paramHandle, Locale paramLocale, Map<?, Address> paramMap, int paramInt)
  {
    paramHandle = paramMap.entrySet().iterator();
    while (paramHandle.hasNext())
    {
      paramLocale = (Map.Entry)paramHandle.next();
      ((MediaStoreMedia)paramLocale.getKey()).onAddressObtained((Address)paramLocale.getValue());
    }
  }
  
  private void onMediaContentUriQueried(CallbackHandle<MediaStoreAccessCallback> paramCallbackHandle, Uri paramUri)
  {
    if (!Handle.isValid(paramCallbackHandle)) {}
    while (paramCallbackHandle.getCallback() == null) {
      return;
    }
    MediaStoreAccessCallback localMediaStoreAccessCallback = (MediaStoreAccessCallback)paramCallbackHandle.getCallback();
    if (paramUri == null) {}
    for (int i = 0;; i = 1)
    {
      localMediaStoreAccessCallback.onCompleted(paramCallbackHandle, paramUri, i, 0);
      return;
    }
  }
  
  private void onMediaDeleted(MediaDeletionHandle paramMediaDeletionHandle, boolean paramBoolean)
  {
    Media localMedia = paramMediaDeletionHandle.getMedia();
    int i;
    if (!paramBoolean)
    {
      if (!isRunningOrInitializing(true)) {
        break label90;
      }
      i = prepareMediaFlagsForCallback(localMedia);
      if (!paramBoolean) {
        break label91;
      }
      if (removeMedia(localMedia, true, i)) {
        break label136;
      }
    }
    for (;;)
    {
      if (paramMediaDeletionHandle.callback != null) {
        break label175;
      }
      return;
      Intent localIntent = new Intent("com.oneplus.gallery.MEDIA_STORE_MEDIA_DELETED");
      localIntent.setDataAndType(localMedia.getContentUri(), localMedia.getMimeType());
      BaseApplication.current().sendBroadcast(localIntent);
      break;
      label90:
      return;
      label91:
      Log.e(this.TAG, "onMediaDeleted() - Fail to delete " + localMedia);
      if (paramMediaDeletionHandle.callback == null) {
        return;
      }
      paramMediaDeletionHandle.callback.onDeletionCompleted(localMedia, false, i);
      return;
      label136:
      if ((localMedia instanceof MediaStoreMedia))
      {
        removeFromGroupedMedia((MediaStoreMedia)localMedia);
        releaseMedia(localMedia);
        this.m_PendingAddressObtainingMedia.remove(localMedia);
      }
    }
    label175:
    paramMediaDeletionHandle.callback.onDeletionCompleted(localMedia, true, i);
  }
  
  private void onMediaIntentReceived(Intent paramIntent)
  {
    int i = 0;
    String str = paramIntent.getAction();
    Uri localUri = paramIntent.getData();
    if (str == null) {}
    while (localUri == null) {
      return;
    }
    switch (str.hashCode())
    {
    default: 
    case -2098526293: 
      do
      {
        return;
      } while (!str.equals("android.hardware.action.NEW_VIDEO"));
      label95:
      Log.v(this.TAG, "onMediaIntentReceived() - New video : ", localUri);
      i = 1;
    }
    for (;;)
    {
      label109:
      if (!this.m_PermissionsReady) {
        break label226;
      }
      syncMediaWithMediaStore(localUri);
      if (i != 0) {
        break label242;
      }
      return;
      if (!str.equals("com.oneplus.camera.intent.action.NEW_VIDEO")) {
        break;
      }
      break label95;
      if (!str.equals("com.oneplus.camera.intent.action.NEW_PICTURE")) {
        break;
      }
      for (;;)
      {
        Log.v(this.TAG, "onMediaIntentReceived() - New photo : ", localUri);
        i = 1;
        break label109;
        if (!str.equals("com.oneplus.camera.service.CLEAR_IMAGE_CACHE")) {
          break;
        }
        Log.v(this.TAG, "onMediaIntentReceived() - Clear cache : ", localUri);
        break label109;
        if (!str.equals("android.hardware.action.NEW_PICTURE")) {
          break;
        }
      }
      if (!str.equals("com.oneplus.gallery.MEDIA_STORE_MEDIA_DELETED")) {
        break;
      }
      Log.v(this.TAG, "onMediaIntentReceived() - Media deleted : ", localUri);
    }
    label226:
    Log.w(this.TAG, "onMediaIntentReceived() - Start full media sync when permissions ready");
    this.m_NeedToSyncMediaWhenPermsReady = true;
    return;
    label242:
    raise(EVENT_NEW_MEDIA_INTENT_RECEIVED, new IntentEventArgs(paramIntent));
  }
  
  private void onMediaSetIntentReceived(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    paramIntent = paramIntent.getStringExtra("MediaSetId");
    if (str == null) {}
    while (paramIntent == null) {
      return;
    }
    switch (str.hashCode())
    {
    }
    do
    {
      return;
    } while (!str.equals("com.oneplus.gallery2.media.action.MEDIA_SET_DELETED"));
    Log.v(this.TAG, "onMediaSetIntentReceived() - Media set deleted : ", paramIntent);
    if (this.m_PermissionsReady)
    {
      if ("CameraRoll".equals(paramIntent)) {}
    }
    else
    {
      Log.w(this.TAG, "onMediaSetIntentReceived() - Start full media sync when permissions ready");
      this.m_NeedToSyncMediaWhenPermsReady = true;
      return;
    }
    syncMediaWithMediaStore();
  }
  
  private void onMediaStoreAccessCompleted(CallbackHandle<MediaStoreAccessCallback> paramCallbackHandle, Uri paramUri, int paramInt1, int paramInt2)
  {
    if (!Handle.isValid(paramCallbackHandle)) {}
    while (paramCallbackHandle.getCallback() == null) {
      return;
    }
    ((MediaStoreAccessCallback)paramCallbackHandle.getCallback()).onCompleted(paramCallbackHandle, paramUri, paramInt1, paramInt2);
  }
  
  private void onMediaStoreContentChanged(Uri paramUri)
  {
    long l2;
    long l1;
    if (isRunningOrInitializing())
    {
      Log.d(this.TAG, "onMediaStoreContentChanged() - Content URI : " + paramUri);
      if (!this.m_PermissionsReady) {
        break label80;
      }
      l2 = -1L;
      if (paramUri != null) {
        break label91;
      }
      l1 = l2;
    }
    for (;;)
    {
      int i;
      if (l1 <= 0L)
      {
        i = 1;
        label58:
        if (i != 0) {
          break label184;
        }
        if (this.m_PendingMediaIdToSync.add(Long.valueOf(l1))) {
          break label150;
        }
      }
      label80:
      label91:
      label150:
      while (this.m_HasPendingSingleMediaSyncWithMediaStore)
      {
        return;
        return;
        Log.w(this.TAG, "onMediaStoreContentChanged() - Permissions not ready, ignore");
        return;
        try
        {
          String str = paramUri.getPath();
          l1 = l2;
          if (str == null) {
            break;
          }
          l1 = l2;
          if (str.length() <= 0) {
            break;
          }
          l1 = l2;
          if (!Character.isDigit(str.charAt(str.length() - 1))) {
            break;
          }
          l1 = ContentUris.parseId(paramUri);
        }
        catch (Throwable paramUri)
        {
          l1 = l2;
        }
        i = 0;
        break label58;
      }
      Log.d(this.TAG, "onMediaStoreContentChanged() - Schedule single media sync");
      this.m_HasPendingSingleMediaSyncWithMediaStore = true;
      HandlerUtils.sendMessage(this, 10004, 50L);
      return;
      label184:
      syncMediaWithMediaStoreDelayed(false);
      return;
    }
  }
  
  private void onNetworkConnectionStateChanged(boolean paramBoolean)
  {
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext())
    {
      Media localMedia = (Media)localIterator.next();
      if ((localMedia instanceof MediaStoreMedia)) {
        ((MediaStoreMedia)localMedia).onNetworkConnectionStateChanged(paramBoolean);
      }
    }
  }
  
  private void onPermissionsReady()
  {
    Log.v(this.TAG, "onPermissionsReady()");
    this.m_PermissionsReady = true;
    if (!this.m_NeedToSyncMediaWhenPermsReady) {
      return;
    }
    this.m_NeedToSyncMediaWhenPermsReady = false;
    syncMediaWithMediaStore();
  }
  
  /* Error */
  private void queryAllMediaStoreContent(Object paramObject, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   4: ldc_w 956
    //   7: iload_2
    //   8: invokestatic 771	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   11: ldc_w 958
    //   14: iload_3
    //   15: invokestatic 771	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   18: invokestatic 961	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   21: invokestatic 966	android/os/SystemClock:elapsedRealtime	()J
    //   24: lstore 6
    //   26: new 286	java/util/ArrayList
    //   29: dup
    //   30: invokespecial 287	java/util/ArrayList:<init>	()V
    //   33: astore 14
    //   35: new 286	java/util/ArrayList
    //   38: dup
    //   39: invokespecial 287	java/util/ArrayList:<init>	()V
    //   42: astore 15
    //   44: aconst_null
    //   45: astore 12
    //   47: aconst_null
    //   48: astore 11
    //   50: iconst_0
    //   51: istore 4
    //   53: iconst_0
    //   54: istore 5
    //   56: invokestatic 559	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   59: invokevirtual 563	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   62: getstatic 231	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_FILE	Landroid/net/Uri;
    //   65: invokevirtual 569	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   68: astore 13
    //   70: aload 13
    //   72: ifnonnull +109 -> 181
    //   75: aload_0
    //   76: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   79: ldc_w 968
    //   82: invokestatic 614	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   85: iload 5
    //   87: istore_2
    //   88: aload 13
    //   90: ifnonnull +742 -> 832
    //   93: iload_2
    //   94: istore 4
    //   96: invokestatic 966	android/os/SystemClock:elapsedRealtime	()J
    //   99: lstore 8
    //   101: aload_0
    //   102: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   105: new 419	java/lang/StringBuilder
    //   108: dup
    //   109: ldc_w 970
    //   112: invokespecial 424	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   115: lload 8
    //   117: lload 6
    //   119: lsub
    //   120: invokevirtual 472	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   123: ldc_w 972
    //   126: invokevirtual 469	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload 14
    //   131: invokeinterface 479 1 0
    //   136: invokevirtual 975	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   139: ldc_w 977
    //   142: invokevirtual 469	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: invokevirtual 429	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokestatic 954	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   151: aload_0
    //   152: sipush 10000
    //   155: iload_3
    //   156: iload 4
    //   158: iconst_3
    //   159: anewarray 979	java/lang/Object
    //   162: dup
    //   163: iconst_0
    //   164: aload_1
    //   165: aastore
    //   166: dup
    //   167: iconst_1
    //   168: aload 14
    //   170: aastore
    //   171: dup
    //   172: iconst_2
    //   173: aload 15
    //   175: aastore
    //   176: invokestatic 609	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;IIILjava/lang/Object;)Z
    //   179: pop
    //   180: return
    //   181: iload_2
    //   182: ifeq +700 -> 882
    //   185: iconst_0
    //   186: istore 5
    //   188: iload_3
    //   189: ifne +20 -> 209
    //   192: iload_3
    //   193: ifeq +31 -> 224
    //   196: iload_3
    //   197: aload_0
    //   198: getfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   201: if_icmplt +249 -> 450
    //   204: iconst_0
    //   205: istore_2
    //   206: goto +668 -> 874
    //   209: aload_0
    //   210: getfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   213: istore_2
    //   214: iload_3
    //   215: iload_2
    //   216: if_icmplt -24 -> 192
    //   219: iconst_0
    //   220: istore_2
    //   221: goto +653 -> 874
    //   224: aload 13
    //   226: getstatic 231	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_FILE	Landroid/net/Uri;
    //   229: iconst_1
    //   230: anewarray 461	java/lang/String
    //   233: dup
    //   234: iconst_0
    //   235: ldc_w 983
    //   238: aastore
    //   239: ldc 103
    //   241: aconst_null
    //   242: aconst_null
    //   243: invokevirtual 987	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   246: astore 11
    //   248: aload 11
    //   250: invokeinterface 992 1 0
    //   255: ifne +131 -> 386
    //   258: aload_0
    //   259: iconst_0
    //   260: putfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   263: aload_0
    //   264: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   267: ldc_w 994
    //   270: aload_0
    //   271: getfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   274: invokestatic 771	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   277: invokestatic 870	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   280: aload 11
    //   282: ifnull -86 -> 196
    //   285: aload 11
    //   287: invokeinterface 997 1 0
    //   292: goto -96 -> 196
    //   295: astore 11
    //   297: aconst_null
    //   298: astore 12
    //   300: aload 12
    //   302: ifnull +131 -> 433
    //   305: aload 12
    //   307: aload 11
    //   309: if_acmpne +131 -> 440
    //   312: aload 12
    //   314: athrow
    //   315: astore 11
    //   317: aload_0
    //   318: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   321: ldc_w 999
    //   324: aload 11
    //   326: invokestatic 643	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   329: aload_0
    //   330: iconst_0
    //   331: putfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   334: goto -138 -> 196
    //   337: astore 12
    //   339: aload 13
    //   341: astore 11
    //   343: aload 12
    //   345: astore 13
    //   347: iload 4
    //   349: istore_2
    //   350: aload 11
    //   352: astore 12
    //   354: aload_0
    //   355: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   358: ldc_w 1001
    //   361: aload 13
    //   363: invokestatic 643	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   366: iload_2
    //   367: istore 4
    //   369: aload 11
    //   371: ifnull -275 -> 96
    //   374: aload 11
    //   376: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   379: pop
    //   380: iload_2
    //   381: istore 4
    //   383: goto -287 -> 96
    //   386: aload_0
    //   387: aload 11
    //   389: iconst_0
    //   390: invokeinterface 1004 2 0
    //   395: putfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   398: goto -135 -> 263
    //   401: astore 12
    //   403: aload 11
    //   405: ifnonnull +6 -> 411
    //   408: aload 12
    //   410: athrow
    //   411: aload 11
    //   413: invokeinterface 997 1 0
    //   418: goto -10 -> 408
    //   421: astore_1
    //   422: aload 13
    //   424: astore 12
    //   426: aload 12
    //   428: ifnonnull +395 -> 823
    //   431: aload_1
    //   432: athrow
    //   433: aload 11
    //   435: astore 12
    //   437: goto -125 -> 312
    //   440: aload 12
    //   442: aload 11
    //   444: invokevirtual 1008	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   447: goto -135 -> 312
    //   450: aload_0
    //   451: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   454: ldc_w 1010
    //   457: invokestatic 954	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   460: aload 13
    //   462: getstatic 231	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_FILE	Landroid/net/Uri;
    //   465: getstatic 1014	com/oneplus/gallery2/media/MediaStoreMedia:MEDIA_STORE_COLUMNS	[Ljava/lang/String;
    //   468: ldc 103
    //   470: aconst_null
    //   471: new 419	java/lang/StringBuilder
    //   474: dup
    //   475: ldc_w 1016
    //   478: invokespecial 424	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   481: iload_3
    //   482: invokevirtual 975	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   485: invokevirtual 429	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   488: invokevirtual 987	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   491: astore 11
    //   493: iconst_0
    //   494: istore_2
    //   495: aload 11
    //   497: invokeinterface 992 1 0
    //   502: ifeq +27 -> 529
    //   505: aload_0
    //   506: aload 11
    //   508: aload 14
    //   510: aload 15
    //   512: invokespecial 1018	com/oneplus/gallery2/media/MediaStoreMediaSource:handleQueriedDataInMediaStore	(Landroid/database/Cursor;Ljava/util/List;Ljava/util/List;)Z
    //   515: istore 10
    //   517: iload 10
    //   519: ifne -24 -> 495
    //   522: iload_2
    //   523: iconst_1
    //   524: iadd
    //   525: istore_2
    //   526: goto -31 -> 495
    //   529: aload 11
    //   531: ifnonnull +13 -> 544
    //   534: iload_2
    //   535: ifeq +98 -> 633
    //   538: iconst_0
    //   539: istore 5
    //   541: goto +333 -> 874
    //   544: aload 11
    //   546: invokeinterface 997 1 0
    //   551: goto -17 -> 534
    //   554: astore 11
    //   556: aconst_null
    //   557: astore 12
    //   559: aload 12
    //   561: ifnull +52 -> 613
    //   564: aload 12
    //   566: aload 11
    //   568: if_acmpne +52 -> 620
    //   571: iload_2
    //   572: istore 4
    //   574: aload 12
    //   576: athrow
    //   577: astore 12
    //   579: iload 4
    //   581: istore_2
    //   582: aload 13
    //   584: astore 11
    //   586: aload 12
    //   588: astore 13
    //   590: goto -240 -> 350
    //   593: astore 12
    //   595: aload 11
    //   597: ifnonnull +6 -> 603
    //   600: aload 12
    //   602: athrow
    //   603: aload 11
    //   605: invokeinterface 997 1 0
    //   610: goto -10 -> 600
    //   613: aload 11
    //   615: astore 12
    //   617: goto -46 -> 571
    //   620: iload_2
    //   621: istore 4
    //   623: aload 12
    //   625: aload 11
    //   627: invokevirtual 1008	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   630: goto -59 -> 571
    //   633: iload_2
    //   634: istore 4
    //   636: aload 14
    //   638: invokeinterface 1019 1 0
    //   643: ifeq -105 -> 538
    //   646: iload_2
    //   647: istore 4
    //   649: aload_0
    //   650: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   653: ldc_w 1021
    //   656: invokestatic 954	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   659: iload_2
    //   660: istore 4
    //   662: aload_0
    //   663: iload_3
    //   664: putfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   667: goto +207 -> 874
    //   670: aload 13
    //   672: getstatic 231	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_FILE	Landroid/net/Uri;
    //   675: getstatic 1014	com/oneplus/gallery2/media/MediaStoreMedia:MEDIA_STORE_COLUMNS	[Ljava/lang/String;
    //   678: ldc 100
    //   680: aconst_null
    //   681: new 419	java/lang/StringBuilder
    //   684: dup
    //   685: ldc_w 1016
    //   688: invokespecial 424	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   691: iload_3
    //   692: aload_0
    //   693: getfield 981	com/oneplus/gallery2/media/MediaStoreMediaSource:m_CameraRollMediaCount	I
    //   696: isub
    //   697: invokevirtual 975	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   700: invokevirtual 429	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   703: invokevirtual 987	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   706: astore 11
    //   708: aload 11
    //   710: invokeinterface 992 1 0
    //   715: ifeq +27 -> 742
    //   718: aload_0
    //   719: aload 11
    //   721: aload 14
    //   723: aload 15
    //   725: invokespecial 1018	com/oneplus/gallery2/media/MediaStoreMediaSource:handleQueriedDataInMediaStore	(Landroid/database/Cursor;Ljava/util/List;Ljava/util/List;)Z
    //   728: istore 10
    //   730: iload 10
    //   732: ifne -24 -> 708
    //   735: iload_2
    //   736: iconst_1
    //   737: iadd
    //   738: istore_2
    //   739: goto -31 -> 708
    //   742: aload 11
    //   744: ifnonnull +6 -> 750
    //   747: goto -659 -> 88
    //   750: aload 11
    //   752: invokeinterface 997 1 0
    //   757: goto -10 -> 747
    //   760: astore 11
    //   762: aconst_null
    //   763: astore 12
    //   765: aload 12
    //   767: ifnull +36 -> 803
    //   770: aload 12
    //   772: aload 11
    //   774: if_acmpne +36 -> 810
    //   777: iload_2
    //   778: istore 4
    //   780: aload 12
    //   782: athrow
    //   783: astore 12
    //   785: aload 11
    //   787: ifnonnull +6 -> 793
    //   790: aload 12
    //   792: athrow
    //   793: aload 11
    //   795: invokeinterface 997 1 0
    //   800: goto -10 -> 790
    //   803: aload 11
    //   805: astore 12
    //   807: goto -30 -> 777
    //   810: iload_2
    //   811: istore 4
    //   813: aload 12
    //   815: aload 11
    //   817: invokevirtual 1008	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   820: goto -43 -> 777
    //   823: aload 12
    //   825: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   828: pop
    //   829: goto -398 -> 431
    //   832: aload 13
    //   834: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   837: pop
    //   838: iload_2
    //   839: istore 4
    //   841: goto -745 -> 96
    //   844: astore_1
    //   845: goto -419 -> 426
    //   848: astore 13
    //   850: iload 4
    //   852: istore_2
    //   853: goto -503 -> 350
    //   856: astore 11
    //   858: aconst_null
    //   859: astore 12
    //   861: goto -96 -> 765
    //   864: astore 11
    //   866: aconst_null
    //   867: astore 12
    //   869: iconst_0
    //   870: istore_2
    //   871: goto -312 -> 559
    //   874: iload 5
    //   876: ifne -206 -> 670
    //   879: goto -791 -> 88
    //   882: iconst_1
    //   883: istore 5
    //   885: goto -697 -> 188
    //   888: astore 11
    //   890: goto -590 -> 300
    //   893: astore 11
    //   895: goto -336 -> 559
    //   898: astore 11
    //   900: goto -135 -> 765
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	903	0	this	MediaStoreMediaSource
    //   0	903	1	paramObject	Object
    //   0	903	2	paramInt1	int
    //   0	903	3	paramInt2	int
    //   51	800	4	i	int
    //   54	830	5	j	int
    //   24	94	6	l1	long
    //   99	17	8	l2	long
    //   515	216	10	bool	boolean
    //   48	238	11	localCursor	Cursor
    //   295	13	11	localObject1	Object
    //   315	10	11	localThrowable1	Throwable
    //   341	204	11	localObject2	Object
    //   554	13	11	localObject3	Object
    //   584	167	11	localObject4	Object
    //   760	56	11	localThrowable2	Throwable
    //   856	1	11	localObject5	Object
    //   864	1	11	localObject6	Object
    //   888	1	11	localObject7	Object
    //   893	1	11	localObject8	Object
    //   898	1	11	localObject9	Object
    //   45	268	12	localObject10	Object
    //   337	7	12	localThrowable3	Throwable
    //   352	1	12	localObject11	Object
    //   401	8	12	localObject12	Object
    //   424	151	12	localObject13	Object
    //   577	10	12	localThrowable4	Throwable
    //   593	8	12	localObject14	Object
    //   615	166	12	localObject15	Object
    //   783	8	12	localObject16	Object
    //   805	63	12	localObject17	Object
    //   68	765	13	localObject18	Object
    //   848	1	13	localThrowable5	Throwable
    //   33	689	14	localArrayList1	ArrayList
    //   42	682	15	localArrayList2	ArrayList
    // Exception table:
    //   from	to	target	type
    //   224	248	295	finally
    //   285	292	295	finally
    //   312	315	315	java/lang/Throwable
    //   440	447	315	java/lang/Throwable
    //   75	85	337	java/lang/Throwable
    //   196	204	337	java/lang/Throwable
    //   209	214	337	java/lang/Throwable
    //   317	334	337	java/lang/Throwable
    //   450	460	337	java/lang/Throwable
    //   248	263	401	finally
    //   263	280	401	finally
    //   386	398	401	finally
    //   75	85	421	finally
    //   196	204	421	finally
    //   209	214	421	finally
    //   312	315	421	finally
    //   317	334	421	finally
    //   440	447	421	finally
    //   450	460	421	finally
    //   574	577	421	finally
    //   623	630	421	finally
    //   636	646	421	finally
    //   649	659	421	finally
    //   662	667	421	finally
    //   780	783	421	finally
    //   813	820	421	finally
    //   544	551	554	finally
    //   574	577	577	java/lang/Throwable
    //   623	630	577	java/lang/Throwable
    //   636	646	577	java/lang/Throwable
    //   649	659	577	java/lang/Throwable
    //   662	667	577	java/lang/Throwable
    //   780	783	577	java/lang/Throwable
    //   813	820	577	java/lang/Throwable
    //   495	517	593	finally
    //   750	757	760	finally
    //   708	730	783	finally
    //   56	70	844	finally
    //   354	366	844	finally
    //   56	70	848	java/lang/Throwable
    //   670	708	856	finally
    //   460	493	864	finally
    //   408	411	888	finally
    //   411	418	888	finally
    //   600	603	893	finally
    //   603	610	893	finally
    //   790	793	898	finally
    //   793	800	898	finally
  }
  
  /* Error */
  private void queryMediaStoreContent(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 13
    //   3: aconst_null
    //   4: astore 4
    //   6: aconst_null
    //   7: astore 9
    //   9: aconst_null
    //   10: astore 12
    //   12: aconst_null
    //   13: astore 7
    //   15: aconst_null
    //   16: astore 15
    //   18: aconst_null
    //   19: astore 11
    //   21: aconst_null
    //   22: astore 8
    //   24: aconst_null
    //   25: astore 10
    //   27: aconst_null
    //   28: astore 5
    //   30: aconst_null
    //   31: astore 14
    //   33: invokestatic 559	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   36: invokevirtual 563	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   39: getstatic 231	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_FILE	Landroid/net/Uri;
    //   42: invokevirtual 569	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   45: astore 6
    //   47: aload 6
    //   49: ifnonnull +55 -> 104
    //   52: aload_0
    //   53: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   56: ldc_w 1023
    //   59: invokestatic 614	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   62: aconst_null
    //   63: astore 8
    //   65: aload 15
    //   67: astore 7
    //   69: aload 6
    //   71: ifnonnull +518 -> 589
    //   74: aload_0
    //   75: sipush 10002
    //   78: iconst_3
    //   79: anewarray 979	java/lang/Object
    //   82: dup
    //   83: iconst_0
    //   84: lload_1
    //   85: invokestatic 451	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   88: aastore
    //   89: dup
    //   90: iconst_1
    //   91: aload 7
    //   93: aastore
    //   94: dup
    //   95: iconst_2
    //   96: aload 8
    //   98: aastore
    //   99: invokestatic 617	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   102: pop
    //   103: return
    //   104: new 419	java/lang/StringBuilder
    //   107: dup
    //   108: getstatic 258	com/oneplus/gallery2/media/MediaStoreMediaSource:CONTENT_URI_STRING_FILE	Ljava/lang/String;
    //   111: invokestatic 464	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   114: invokespecial 424	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   117: ldc_w 466
    //   120: invokevirtual 469	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: lload_1
    //   124: invokevirtual 472	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   127: invokevirtual 429	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   130: invokestatic 250	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   133: astore 5
    //   135: aload 6
    //   137: aload 5
    //   139: getstatic 1014	com/oneplus/gallery2/media/MediaStoreMedia:MEDIA_STORE_COLUMNS	[Ljava/lang/String;
    //   142: aconst_null
    //   143: aconst_null
    //   144: aconst_null
    //   145: invokevirtual 987	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   148: astore 9
    //   150: aload 9
    //   152: invokeinterface 992 1 0
    //   157: istore_3
    //   158: iload_3
    //   159: ifne +156 -> 315
    //   162: aconst_null
    //   163: astore 5
    //   165: aload 13
    //   167: astore 4
    //   169: aload 5
    //   171: astore 8
    //   173: aload 4
    //   175: astore 7
    //   177: aload 9
    //   179: ifnull -110 -> 69
    //   182: aload 9
    //   184: invokeinterface 997 1 0
    //   189: aload 5
    //   191: astore 8
    //   193: aload 4
    //   195: astore 7
    //   197: goto -128 -> 69
    //   200: astore 7
    //   202: aconst_null
    //   203: astore 8
    //   205: aload 8
    //   207: ifnull +292 -> 499
    //   210: aload 8
    //   212: aload 7
    //   214: if_acmpne +292 -> 506
    //   217: aload 8
    //   219: athrow
    //   220: astore 7
    //   222: aload 5
    //   224: astore 10
    //   226: aload 7
    //   228: astore 5
    //   230: aload 4
    //   232: astore 9
    //   234: aload 6
    //   236: astore 4
    //   238: aload 5
    //   240: astore 6
    //   242: aload 4
    //   244: astore 5
    //   246: aload 9
    //   248: astore 7
    //   250: aload 10
    //   252: astore 8
    //   254: aload_0
    //   255: getfield 388	com/oneplus/gallery2/media/MediaStoreMediaSource:TAG	Ljava/lang/String;
    //   258: new 419	java/lang/StringBuilder
    //   261: dup
    //   262: ldc_w 1025
    //   265: invokespecial 424	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   268: lload_1
    //   269: invokevirtual 472	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   272: invokevirtual 429	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   275: aload 6
    //   277: invokestatic 643	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   280: aload 4
    //   282: ifnonnull +289 -> 571
    //   285: aload_0
    //   286: sipush 10002
    //   289: iconst_3
    //   290: anewarray 979	java/lang/Object
    //   293: dup
    //   294: iconst_0
    //   295: lload_1
    //   296: invokestatic 451	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   299: aastore
    //   300: dup
    //   301: iconst_1
    //   302: aload 9
    //   304: aastore
    //   305: dup
    //   306: iconst_2
    //   307: aload 10
    //   309: aastore
    //   310: invokestatic 617	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   313: pop
    //   314: return
    //   315: aload_0
    //   316: getfield 312	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempDbValueList	Ljava/util/List;
    //   319: invokeinterface 1026 1 0
    //   324: aload_0
    //   325: getfield 314	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempFileInfoList	Ljava/util/List;
    //   328: invokeinterface 1026 1 0
    //   333: aload_0
    //   334: aload 9
    //   336: aload_0
    //   337: getfield 312	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempDbValueList	Ljava/util/List;
    //   340: aload_0
    //   341: getfield 314	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempFileInfoList	Ljava/util/List;
    //   344: invokespecial 1018	com/oneplus/gallery2/media/MediaStoreMediaSource:handleQueriedDataInMediaStore	(Landroid/database/Cursor;Ljava/util/List;Ljava/util/List;)Z
    //   347: ifne +34 -> 381
    //   350: goto +297 -> 647
    //   353: aload_0
    //   354: sipush 10002
    //   357: iconst_3
    //   358: anewarray 979	java/lang/Object
    //   361: dup
    //   362: iconst_0
    //   363: lload_1
    //   364: invokestatic 451	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   367: aastore
    //   368: dup
    //   369: iconst_1
    //   370: aconst_null
    //   371: aastore
    //   372: dup
    //   373: iconst_2
    //   374: aconst_null
    //   375: aastore
    //   376: invokestatic 617	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   379: pop
    //   380: return
    //   381: aload_0
    //   382: getfield 312	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempDbValueList	Ljava/util/List;
    //   385: invokeinterface 1019 1 0
    //   390: ifne +257 -> 647
    //   393: aload_0
    //   394: getfield 314	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempFileInfoList	Ljava/util/List;
    //   397: invokeinterface 1019 1 0
    //   402: ifne +245 -> 647
    //   405: aload_0
    //   406: getfield 312	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempDbValueList	Ljava/util/List;
    //   409: iconst_0
    //   410: invokeinterface 483 2 0
    //   415: checkcast 505	com/oneplus/gallery2/media/MediaStoreMedia$DbValues
    //   418: astore 5
    //   420: aload_0
    //   421: getfield 314	com/oneplus/gallery2/media/MediaStoreMediaSource:m_TempFileInfoList	Ljava/util/List;
    //   424: iconst_0
    //   425: invokeinterface 483 2 0
    //   430: checkcast 526	com/oneplus/gallery2/media/MediaStoreMedia$FileInfo
    //   433: astore 7
    //   435: aload 5
    //   437: astore 4
    //   439: aload 7
    //   441: astore 5
    //   443: goto -274 -> 169
    //   446: aload 9
    //   448: invokeinterface 997 1 0
    //   453: goto +199 -> 652
    //   456: astore 7
    //   458: aconst_null
    //   459: astore 8
    //   461: aconst_null
    //   462: astore 5
    //   464: goto -259 -> 205
    //   467: aload 6
    //   469: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   472: pop
    //   473: goto -120 -> 353
    //   476: astore 4
    //   478: aconst_null
    //   479: astore 5
    //   481: aload 9
    //   483: ifnonnull +6 -> 489
    //   486: aload 4
    //   488: athrow
    //   489: aload 9
    //   491: invokeinterface 997 1 0
    //   496: goto -10 -> 486
    //   499: aload 7
    //   501: astore 8
    //   503: goto -286 -> 217
    //   506: aload 8
    //   508: aload 7
    //   510: invokevirtual 1008	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   513: goto -296 -> 217
    //   516: astore 9
    //   518: aload 5
    //   520: astore 8
    //   522: aload 4
    //   524: astore 7
    //   526: aload 6
    //   528: astore 5
    //   530: aload 9
    //   532: astore 4
    //   534: aload 5
    //   536: ifnonnull +44 -> 580
    //   539: aload_0
    //   540: sipush 10002
    //   543: iconst_3
    //   544: anewarray 979	java/lang/Object
    //   547: dup
    //   548: iconst_0
    //   549: lload_1
    //   550: invokestatic 451	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   553: aastore
    //   554: dup
    //   555: iconst_1
    //   556: aload 7
    //   558: aastore
    //   559: dup
    //   560: iconst_2
    //   561: aload 8
    //   563: aastore
    //   564: invokestatic 617	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   567: pop
    //   568: aload 4
    //   570: athrow
    //   571: aload 4
    //   573: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   576: pop
    //   577: goto -292 -> 285
    //   580: aload 5
    //   582: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   585: pop
    //   586: goto -47 -> 539
    //   589: aload 6
    //   591: invokevirtual 603	android/content/ContentProviderClient:release	()Z
    //   594: pop
    //   595: goto -521 -> 74
    //   598: astore 4
    //   600: goto -66 -> 534
    //   603: astore 4
    //   605: aload 6
    //   607: astore 5
    //   609: aload 12
    //   611: astore 7
    //   613: aload 11
    //   615: astore 8
    //   617: goto -83 -> 534
    //   620: astore 6
    //   622: aload 14
    //   624: astore 4
    //   626: goto -384 -> 242
    //   629: astore 5
    //   631: aload 6
    //   633: astore 4
    //   635: aload 5
    //   637: astore 6
    //   639: goto -397 -> 242
    //   642: astore 4
    //   644: goto -163 -> 481
    //   647: aload 9
    //   649: ifnonnull -203 -> 446
    //   652: aload 6
    //   654: ifnonnull -187 -> 467
    //   657: goto -304 -> 353
    //   660: astore 7
    //   662: aload 5
    //   664: astore 9
    //   666: aconst_null
    //   667: astore 5
    //   669: aload 4
    //   671: astore 8
    //   673: aload 9
    //   675: astore 4
    //   677: goto -472 -> 205
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	680	0	this	MediaStoreMediaSource
    //   0	680	1	paramLong	long
    //   157	2	3	bool	boolean
    //   4	434	4	localObject1	Object
    //   476	47	4	localObject2	Object
    //   532	40	4	localObject3	Object
    //   598	1	4	localObject4	Object
    //   603	1	4	localObject5	Object
    //   624	10	4	localObject6	Object
    //   642	28	4	localObject7	Object
    //   675	1	4	localObject8	Object
    //   28	580	5	localObject9	Object
    //   629	34	5	localThrowable1	Throwable
    //   667	1	5	localObject10	Object
    //   45	561	6	localObject11	Object
    //   620	12	6	localThrowable2	Throwable
    //   637	16	6	localThrowable3	Throwable
    //   13	183	7	localObject12	Object
    //   200	13	7	localObject13	Object
    //   220	7	7	localThrowable4	Throwable
    //   248	192	7	localObject14	Object
    //   456	53	7	localThrowable5	Throwable
    //   524	88	7	localObject15	Object
    //   660	1	7	localObject16	Object
    //   22	650	8	localObject17	Object
    //   7	483	9	localObject18	Object
    //   516	132	9	localObject19	Object
    //   664	10	9	localObject20	Object
    //   25	283	10	localObject21	Object
    //   19	595	11	localObject22	Object
    //   10	600	12	localObject23	Object
    //   1	165	13	localObject24	Object
    //   31	592	14	localObject25	Object
    //   16	50	15	localObject26	Object
    // Exception table:
    //   from	to	target	type
    //   182	189	200	finally
    //   217	220	220	java/lang/Throwable
    //   506	513	220	java/lang/Throwable
    //   135	150	456	finally
    //   446	453	456	finally
    //   150	158	476	finally
    //   315	350	476	finally
    //   353	380	476	finally
    //   381	420	476	finally
    //   467	473	476	finally
    //   217	220	516	finally
    //   506	513	516	finally
    //   33	47	598	finally
    //   254	280	598	finally
    //   52	62	603	finally
    //   104	135	603	finally
    //   33	47	620	java/lang/Throwable
    //   52	62	629	java/lang/Throwable
    //   104	135	629	java/lang/Throwable
    //   420	435	642	finally
    //   486	489	660	finally
    //   489	496	660	finally
  }
  
  private void releaseMedia(Media paramMedia)
  {
    if (paramMedia != null)
    {
      if (!(paramMedia instanceof MediaStoreMedia)) {
        if ((paramMedia instanceof BurstMediaStoreMedia)) {
          break label28;
        }
      }
    }
    else {
      return;
    }
    ((MediaStoreMedia)paramMedia).release();
    return;
    label28:
    ((BurstMediaStoreMedia)paramMedia).release();
  }
  
  private boolean removeFromGroupedMedia(MediaStoreMedia paramMediaStoreMedia)
  {
    if (paramMediaStoreMedia == null) {}
    while (!paramMediaStoreMedia.isSubMedia()) {
      return false;
    }
    return removeFromGroupedMedia(paramMediaStoreMedia.getGroupId(), paramMediaStoreMedia);
  }
  
  private boolean removeFromGroupedMedia(String paramString, MediaStoreMedia paramMediaStoreMedia)
  {
    paramString = (BaseGroupedMedia)getMedia(paramString, 0);
    if (paramString == null) {
      return false;
    }
    if (paramString.getSubMediaCount() != 1) {}
    for (;;)
    {
      boolean bool = paramString.removeSubMedia(paramMediaStoreMedia);
      paramMediaStoreMedia.release();
      return bool;
      if (paramString.contains(paramMediaStoreMedia))
      {
        Log.d(this.TAG, "removeFromGroupedMedia() - Remove " + paramString);
        removeMedia(paramString, true, prepareMediaFlagsForCallback(paramString));
        releaseMedia(paramString);
      }
    }
  }
  
  private void restoreMedia(MediaDeletionHandle paramMediaDeletionHandle)
  {
    verifyAccess();
    if (isRunningOrInitializing(true))
    {
      Media localMedia = paramMediaDeletionHandle.getMedia();
      if (this.m_RecycledMedia.remove(localMedia)) {
        notifyMediaCreated(localMedia, paramMediaDeletionHandle.getFlags() & (Media.FLAG_MOVE_TO_RECYCE_BIN ^ 0xFFFFFFFF) | Media.FLAG_RESTORE_FROM_RECYCLE_BIN);
      }
    }
    else
    {
      return;
    }
    Log.e(this.TAG, "restoreMedia() - Media is not contained in recycle bin");
  }
  
  private void syncMediaWithMediaStore()
  {
    if (isRunningOrInitializing())
    {
      if (!this.m_PermissionsReady) {
        break label118;
      }
      if (!((Boolean)get(PROP_IS_ACTIVE)).booleanValue()) {
        break label134;
      }
      Collection localCollection = getMedia();
      Log.v(this.TAG, "syncMediaWithMediaStore() - Media table size : ", Integer.valueOf(localCollection.size()));
      this.m_CandidateMediaToRemove.clear();
      this.m_CandidateMediaToRemove.addAll(localCollection);
      this.m_MediaStoreSyncToken = new Object();
      if (this.m_HasPendingMediaSyncWithMediaStore) {
        break label199;
      }
    }
    for (;;)
    {
      HandlerUtils.post(MediaContentThread.current(), new QueryAllMediaStoreContentRunnable(this.m_MediaStoreSyncToken, this.m_Mode, 0));
      return;
      return;
      label118:
      Log.w(this.TAG, "syncMediaWithMediaStore() - Start full media sync when permissions ready");
      this.m_NeedToSyncMediaWhenPermsReady = true;
      return;
      label134:
      this.m_PendingMediaSyncCount += 1;
      if (this.m_PendingMediaSyncCount > 16)
      {
        Log.w(this.TAG, "syncMediaWithMediaStore() - Too many pending full media sync");
        this.m_PendingMediaSyncCount = 0;
        break;
      }
      Log.w(this.TAG, "syncMediaWithMediaStore() - Start full media sync when activated, pending sync count : " + this.m_PendingMediaSyncCount);
      return;
      label199:
      this.m_HasPendingMediaSyncWithMediaStore = false;
      HandlerUtils.removeMessages(this, 10001);
    }
  }
  
  private void syncMediaWithMediaStore(long paramLong, MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo)
  {
    MediaStoreMedia localMediaStoreMedia;
    String str;
    int i;
    if (isRunningOrInitializing())
    {
      localMediaStoreMedia = (MediaStoreMedia)getMedia(MediaStoreMedia.getId(paramLong), 0);
      if (paramDbValues != null)
      {
        if (localMediaStoreMedia == null) {
          break label140;
        }
        str = localMediaStoreMedia.getGroupId();
        i = localMediaStoreMedia.update(paramDbValues, paramFileInfo);
        if (i != 0) {
          break label204;
        }
        callOnMediaObtained(paramLong, localMediaStoreMedia);
      }
    }
    else
    {
      return;
    }
    if (localMediaStoreMedia == null) {}
    for (;;)
    {
      callOnMediaObtained(paramLong, null);
      return;
      Log.d(this.TAG, "syncMediaWithMediaStore() - Remove " + localMediaStoreMedia);
      removeFromGroupedMedia(localMediaStoreMedia);
      removeMedia(localMediaStoreMedia, true, prepareMediaFlagsForCallback(localMediaStoreMedia));
      releaseMedia(localMediaStoreMedia);
      this.m_PendingAddressObtainingMedia.remove(localMediaStoreMedia);
    }
    label140:
    paramDbValues = MediaStoreMedia.create(this, paramDbValues, paramFileInfo);
    if (paramDbValues == null) {}
    for (;;)
    {
      callOnMediaObtained(paramLong, paramDbValues);
      return;
      Log.d(this.TAG, "syncMediaWithMediaStore() - Add " + paramDbValues);
      addMedia(paramDbValues, true, prepareMediaFlagsForCallback(paramDbValues));
      addToGroupedMedia(paramDbValues);
    }
    label204:
    Log.d(this.TAG, "syncMediaWithMediaStore() - Update " + localMediaStoreMedia);
    if ((MediaStoreMedia.FLAG_GROUP_CHANGED & i) != 0)
    {
      removeFromGroupedMedia(str, localMediaStoreMedia);
      addToGroupedMedia(localMediaStoreMedia);
    }
    for (;;)
    {
      notifyMediaUpdated(localMediaStoreMedia, prepareMediaFlagsForCallback(localMediaStoreMedia) | i);
      break;
      if (localMediaStoreMedia.isSubMedia())
      {
        paramDbValues = (BaseGroupedMedia)getMedia(localMediaStoreMedia.getGroupId(), 0);
        if (paramDbValues != null) {
          paramDbValues.notifySubMediaUpdated(localMediaStoreMedia, i);
        }
      }
    }
  }
  
  private void syncMediaWithMediaStore(Uri paramUri)
  {
    if (isRunningOrInitializing()) {}
    try
    {
      final long l = ContentUris.parseId(paramUri);
      MediaContentThread.current().getHandler().postAtFrontOfQueue(new Runnable()
      {
        public void run()
        {
          MediaStoreMediaSource.this.queryMediaStoreContent(l);
        }
      });
      return;
    }
    catch (Throwable paramUri) {}
    return;
  }
  
  private void syncMediaWithMediaStore(Object paramObject, int paramInt1, int paramInt2, List<MediaStoreMedia.DbValues> paramList, List<MediaStoreMedia.FileInfo> paramList1)
  {
    long l1;
    Object localObject2;
    Object localObject1;
    Object localObject4;
    HashMap localHashMap;
    HashSet localHashSet;
    int i;
    Object localObject6;
    Object localObject5;
    Object localObject3;
    int j;
    if (isRunningOrInitializing())
    {
      if (this.m_MediaStoreSyncToken != paramObject) {
        break label208;
      }
      Log.v(this.TAG, "syncMediaWithMediaStore() - Offset : ", Integer.valueOf(paramInt1));
      l1 = SystemClock.elapsedRealtime();
      localObject2 = null;
      localObject1 = null;
      localObject4 = null;
      localHashMap = new HashMap();
      localHashSet = new HashSet();
      i = paramList.size() - 1;
      if (i < 0) {
        break label933;
      }
      paramObject = (MediaStoreMedia.DbValues)paramList.get(i);
      localObject6 = (MediaStoreMedia.FileInfo)paramList1.get(i);
      localObject5 = (MediaStoreMedia)getMedia(MediaStoreMedia.getId(((MediaStoreMedia.DbValues)paramObject).id), 0);
      if (localObject5 == null) {
        break label219;
      }
      this.m_CandidateMediaToRemove.remove(localObject5);
      localObject3 = ((MediaStoreMedia)localObject5).getGroupId();
      j = ((MediaStoreMedia)localObject5).update((MediaStoreMedia.DbValues)paramObject, (MediaStoreMedia.FileInfo)localObject6);
      if (j != 0) {
        break label490;
      }
      if (((MediaStoreMedia)localObject5).isSubMedia()) {
        break label889;
      }
      localObject3 = null;
      paramObject = localObject4;
      label174:
      if (localObject3 != null) {
        break label910;
      }
      localObject5 = localObject1;
      localObject3 = localObject2;
    }
    for (;;)
    {
      label187:
      i -= 1;
      localObject2 = localObject3;
      localObject4 = paramObject;
      localObject1 = localObject5;
      break;
      return;
      label208:
      Log.w(this.TAG, "syncMediaWithMediaStore() - Different token, ignore");
      return;
      label219:
      localObject6 = MediaStoreMedia.create(this, (MediaStoreMedia.DbValues)paramObject, (MediaStoreMedia.FileInfo)localObject6);
      localObject3 = localObject2;
      paramObject = localObject4;
      localObject5 = localObject1;
      if (localObject6 != null)
      {
        localObject3 = localObject2;
        paramObject = localObject4;
        localObject5 = localObject1;
        if (addMedia((Media)localObject6, false, 0))
        {
          if (localObject2 != null) {}
          for (paramObject = localObject2;; paramObject = new ArrayList())
          {
            ((List)paramObject).add(localObject6);
            if (((MediaStoreMedia)localObject6).isSubMedia()) {
              break label315;
            }
            localObject3 = paramObject;
            paramObject = localObject4;
            localObject5 = localObject1;
            break;
          }
          label315:
          localObject2 = (BaseGroupedMedia)getMedia(((MediaStoreMedia)localObject6).getGroupId(), 0);
          if (localObject2 != null)
          {
            label335:
            if (localObject2 == null) {
              break label405;
            }
            this.m_CandidateMediaToRemove.remove(localObject2);
            label352:
            if (localHashSet.add(localObject2)) {
              break label482;
            }
          }
          for (;;)
          {
            ((BaseGroupedMedia)localObject2).addSubMedia((Media)localObject6);
            localObject3 = paramObject;
            paramObject = localObject4;
            localObject5 = localObject1;
            break;
            localObject2 = (BaseGroupedMedia)localHashMap.get(((MediaStoreMedia)localObject6).getGroupId());
            break label335;
            label405:
            localObject2 = createGroupedMedia((MediaStoreMedia)localObject6);
            if (localObject2 == null)
            {
              localObject3 = paramObject;
              paramObject = localObject4;
              localObject5 = localObject1;
              break;
            }
            if (addMedia((Media)localObject2, false, 0))
            {
              localHashMap.put(((BaseGroupedMedia)localObject2).getId(), localObject2);
              ((List)paramObject).add(localObject2);
              break label352;
            }
            localObject3 = paramObject;
            paramObject = localObject4;
            localObject5 = localObject1;
            break;
            label482:
            ((BaseGroupedMedia)localObject2).startSubMediaUpdate();
          }
          label490:
          if (localObject1 != null)
          {
            paramObject = localObject4;
            label498:
            ((List)localObject1).add(localObject5);
            ((List)paramObject).add(Integer.valueOf(j));
            Log.d(this.TAG, "syncMediaWithMediaStore() - Update " + localObject5);
            if ((MediaStoreMedia.FLAG_GROUP_CHANGED & j) == 0) {
              break label593;
            }
            if (localObject3 != null) {
              break label662;
            }
          }
          for (;;)
          {
            if (!((MediaStoreMedia)localObject5).isSubMedia())
            {
              localObject3 = null;
              break;
              localObject1 = new ArrayList();
              paramObject = new ArrayList();
              break label498;
              label593:
              if (!((MediaStoreMedia)localObject5).isSubMedia())
              {
                localObject3 = null;
                break;
              }
              localObject3 = (BaseGroupedMedia)getMedia(((MediaStoreMedia)localObject5).getGroupId(), 0);
              if (localObject3 == null) {
                break;
              }
              if (!localHashSet.add(localObject3)) {}
              for (;;)
              {
                ((BaseGroupedMedia)localObject3).notifySubMediaUpdated((Media)localObject5, j);
                break;
                ((BaseGroupedMedia)localObject3).startSubMediaUpdate();
              }
              label662:
              localObject3 = (BaseGroupedMedia)getMedia((String)localObject3, 0);
              if (localObject3 != null)
              {
                if (!localHashSet.add(localObject3)) {}
                for (;;)
                {
                  ((BaseGroupedMedia)localObject3).removeSubMedia((Media)localObject5);
                  break;
                  ((BaseGroupedMedia)localObject3).startSubMediaUpdate();
                }
              }
            }
          }
          localObject3 = (BaseGroupedMedia)getMedia(((MediaStoreMedia)localObject5).getGroupId(), 0);
          if (localObject3 != null)
          {
            label730:
            if (localObject3 == null) {
              break label790;
            }
            this.m_CandidateMediaToRemove.remove(localObject3);
            label747:
            if (localHashSet.add(localObject3)) {
              break label881;
            }
          }
          for (;;)
          {
            ((BaseGroupedMedia)localObject3).addSubMedia((Media)localObject5);
            break;
            localObject3 = (BaseGroupedMedia)localHashMap.get(((MediaStoreMedia)localObject5).getGroupId());
            break label730;
            label790:
            localObject3 = createGroupedMedia((MediaStoreMedia)localObject5);
            if (localObject3 == null)
            {
              localObject3 = localObject2;
              localObject5 = localObject1;
              break label187;
            }
            if (addMedia((Media)localObject3, false, 0))
            {
              localHashMap.put(((BaseGroupedMedia)localObject3).getId(), localObject3);
              if (localObject2 == null) {
                break label869;
              }
            }
            for (;;)
            {
              ((List)localObject2).add(localObject3);
              break label747;
              localObject3 = localObject2;
              localObject5 = localObject1;
              break;
              label869:
              localObject2 = new ArrayList();
            }
            label881:
            ((BaseGroupedMedia)localObject3).startSubMediaUpdate();
          }
          label889:
          localObject3 = (BaseGroupedMedia)getMedia(((MediaStoreMedia)localObject5).getGroupId(), 0);
          paramObject = localObject4;
          break label174;
          label910:
          this.m_CandidateMediaToRemove.remove(localObject3);
          localObject3 = localObject2;
          localObject5 = localObject1;
        }
      }
    }
    label933:
    if (!paramList.isEmpty())
    {
      HandlerUtils.post(MediaContentThread.current(), new QueryAllMediaStoreContentRunnable(this.m_MediaStoreSyncToken, this.m_Mode, paramInt1 + paramInt2 + paramList.size()));
      label977:
      if (localObject2 != null) {
        break label1293;
      }
      label982:
      if (localObject1 != null) {
        break label1359;
      }
      label987:
      Log.v(this.TAG, "syncMediaWithMediaStore() - Media table size : ", Integer.valueOf(getMedia().size()));
      if (!localHashSet.isEmpty()) {
        break label1442;
      }
    }
    for (;;)
    {
      if (!paramList.isEmpty()) {
        break label1542;
      }
      if (!this.m_IsMediaTableReady) {
        break label1582;
      }
      return;
      if (paramInt2 > 0) {
        break;
      }
      Log.v(this.TAG, "syncMediaWithMediaStore() - This is last chunk");
      if (this.m_CandidateMediaToRemove.isEmpty()) {
        break label977;
      }
      Log.v(this.TAG, "syncMediaWithMediaStore() - Remove ", Integer.valueOf(this.m_CandidateMediaToRemove.size()), " media");
      paramObject = (Media[])this.m_CandidateMediaToRemove.toArray(new Media[this.m_CandidateMediaToRemove.size()]);
      this.m_CandidateMediaToRemove.clear();
      paramInt1 = paramObject.length - 1;
      label1127:
      if (paramInt1 >= 0)
      {
        paramList1 = paramObject[paramInt1];
        Log.d(this.TAG, "syncMediaWithMediaStore() - Remove " + paramList1);
        removeMedia(paramList1, true, prepareMediaFlagsForCallback(paramList1));
        releaseMedia(paramList1);
        this.m_PendingAddressObtainingMedia.remove(paramList1);
        boolean bool = paramList1 instanceof MediaStoreMedia;
        if (bool) {
          break label1217;
        }
        label1205:
        if (bool) {
          break label1282;
        }
      }
      for (;;)
      {
        paramInt1 -= 1;
        break label1127;
        break;
        label1217:
        if (!((MediaStoreMedia)paramList1).isSubMedia()) {
          break label1205;
        }
        localObject3 = (BaseGroupedMedia)getMedia(((MediaStoreMedia)paramList1).getGroupId(), 0);
        if (localObject3 == null) {
          break label1205;
        }
        if (!localHashSet.add(localObject3)) {}
        for (;;)
        {
          ((BaseGroupedMedia)localObject3).removeSubMedia(paramList1);
          break;
          ((BaseGroupedMedia)localObject3).startSubMediaUpdate();
        }
        label1282:
        ((MediaStoreMedia)paramList1).release();
      }
      label1293:
      Log.v(this.TAG, "syncMediaWithMediaStore() - Add ", Integer.valueOf(((List)localObject2).size()), " media");
      paramInt1 = ((List)localObject2).size() - 1;
      while (paramInt1 >= 0)
      {
        paramObject = (Media)((List)localObject2).get(paramInt1);
        notifyMediaCreated((Media)paramObject, prepareMediaFlagsForCallback((Media)paramObject));
        paramInt1 -= 1;
      }
      break label982;
      label1359:
      Log.v(this.TAG, "syncMediaWithMediaStore() - Update ", Integer.valueOf(((List)localObject1).size()), " media");
      paramInt1 = ((List)localObject1).size() - 1;
      while (paramInt1 >= 0)
      {
        paramObject = (Media)((List)localObject1).get(paramInt1);
        paramInt2 = prepareMediaFlagsForCallback((Media)paramObject);
        notifyMediaUpdated((Media)paramObject, ((Integer)((List)localObject4).get(paramInt1)).intValue() | paramInt2);
        paramInt1 -= 1;
      }
      break label987;
      label1442:
      Log.d(this.TAG, "syncMediaWithMediaStore() - Complete updating " + localHashSet.size() + " grouped media");
      paramObject = localHashSet.iterator();
      while (((Iterator)paramObject).hasNext())
      {
        paramList1 = (BaseGroupedMedia)((Iterator)paramObject).next();
        paramList1.completeSubMediaUpdate();
        if (paramList1.getSubMediaCount() == 0)
        {
          removeMedia(paramList1, true, prepareMediaFlagsForCallback(paramList1));
          releaseMedia(paramList1);
        }
      }
    }
    label1542:
    long l2 = SystemClock.elapsedRealtime();
    Log.v(this.TAG, "syncMediaWithMediaStore() - Take ", Long.valueOf(l2 - l1), " ms to handle ", Integer.valueOf(paramList.size()), " entries");
    return;
    label1582:
    this.m_IsMediaTableReady = true;
    notifyPropertyChanged(PROP_IS_MEDIA_TABLE_READY, Boolean.valueOf(false), Boolean.valueOf(true));
  }
  
  private void syncMediaWithMediaStoreDelayed(boolean paramBoolean)
  {
    if (this.m_HasPendingMediaSyncWithMediaStore) {
      return;
    }
    if (((Boolean)get(PROP_IS_ACTIVE)).booleanValue()) {}
    while (paramBoolean)
    {
      Log.d(this.TAG, "onMediaStoreContentChanged() - Schedule full media sync");
      this.m_HasPendingMediaSyncWithMediaStore = true;
      this.m_PendingMediaSyncCount = 0;
      HandlerUtils.sendMessage(this, 10001, 1000L);
      return;
    }
    this.m_PendingMediaSyncCount += 1;
    if (this.m_PendingMediaSyncCount > 16)
    {
      Log.w(this.TAG, "syncMediaWithMediaStoreDelayed() - Too many pending full media sync");
      syncMediaWithMediaStore();
      return;
    }
    Log.v(this.TAG, "syncMediaWithMediaStoreDelayed() - Start full media sync when activated, pending sync count : ", Integer.valueOf(this.m_PendingMediaSyncCount));
  }
  
  private void updateMode()
  {
    int i = 0;
    int j = this.m_Mode;
    int k = getActivationHandleCount();
    if (k != 0) {
      if (this.m_CameraRollOnlyFlagCount >= k) {
        break label39;
      }
    }
    for (;;)
    {
      this.m_Mode = i;
      if (j != this.m_Mode) {
        break;
      }
      return;
      return;
      label39:
      i = 1;
    }
    Log.v(this.TAG, "updateMode() - Change mode from ", Integer.valueOf(j), " to ", Integer.valueOf(this.m_Mode));
    switch (this.m_Mode)
    {
    default: 
      return;
    }
    syncMediaWithMediaStore();
  }
  
  public Handle deleteFromMediaStore(final CharSequence paramCharSequence, final String[] paramArrayOfString, final MediaStoreAccessCallback paramMediaStoreAccessCallback)
  {
    if (paramCharSequence != null)
    {
      paramMediaStoreAccessCallback = new CallbackHandle("DeleteFromMediaStore", paramMediaStoreAccessCallback, null)
      {
        protected void onClose(int paramAnonymousInt) {}
      };
      if (HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          if (Handle.isValid(paramMediaStoreAccessCallback))
          {
            localContentProviderClient = BaseApplication.current().getContentResolver().acquireContentProviderClient(MediaStoreMediaSource.CONTENT_URI_FILE);
            if (localContentProviderClient == null) {
              break label85;
            }
          }
          try
          {
            int i = localContentProviderClient.delete(MediaStoreMediaSource.CONTENT_URI_FILE, paramCharSequence.toString(), paramArrayOfString);
            HandlerUtils.sendMessage(MediaStoreMediaSource.this, 10020, i, 0, new Object[] { paramMediaStoreAccessCallback, MediaStoreMediaSource.CONTENT_URI_FILE });
            return;
          }
          catch (Throwable localThrowable)
          {
            Log.e(MediaStoreMediaSource.this.TAG, "deleteFromMediaStore() - Fail to delete", localThrowable);
            HandlerUtils.sendMessage(MediaStoreMediaSource.this, 10020, 0, 0, new Object[] { paramMediaStoreAccessCallback, MediaStoreMediaSource.CONTENT_URI_FILE });
            return;
          }
          finally
          {
            localContentProviderClient.release();
          }
          return;
          label85:
          Log.e(MediaStoreMediaSource.this.TAG, "deleteFromMediaStore() - Fail to get content provider client");
          HandlerUtils.sendMessage(MediaStoreMediaSource.this, 10020, 0, 0, new Object[] { paramMediaStoreAccessCallback, MediaStoreMediaSource.CONTENT_URI_FILE });
        }
      })) {
        return paramMediaStoreAccessCallback;
      }
    }
    else
    {
      Log.e(this.TAG, "deleteFromMediaStore() - No selection");
      return null;
    }
    Log.e(this.TAG, "deleteFromMediaStore() - Fail to post to media content thread");
    return null;
  }
  
  final Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    final MediaDeletionHandle localMediaDeletionHandle;
    if (isRunningOrInitializing(true))
    {
      if (paramMedia == null) {
        break label87;
      }
      if (!containsMedia(paramMedia)) {
        break label99;
      }
      paramInt = (Media.FLAG_MOVE_TO_RECYCE_BIN ^ 0xFFFFFFFF) & paramInt | prepareMediaFlagsForCallback(paramMedia);
      localMediaDeletionHandle = new MediaDeletionHandle(paramMedia, paramDeletionCallback, paramInt);
      if (paramDeletionCallback != null) {
        break label131;
      }
      if (this.m_RecycledMedia.remove(paramMedia)) {
        break label140;
      }
      label68:
      if ((paramMedia instanceof MediaStoreMedia)) {
        break label173;
      }
      onMediaDeleted(localMediaDeletionHandle, true);
    }
    label87:
    label99:
    label131:
    label140:
    label173:
    while (HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        MediaStoreMediaSource.this.deleteMedia(localMediaDeletionHandle);
      }
    }))
    {
      return localMediaDeletionHandle;
      return null;
      Log.e(this.TAG, "deleteMedia() - No media to delete");
      return null;
      Log.e(this.TAG, "deleteMedia() - " + paramMedia + " is not contained in media table");
      return null;
      paramDeletionCallback.onDeletionStarted(paramMedia, paramInt);
      break;
      Log.v(this.TAG, "deleteMedia() - Remove " + paramMedia + " from recycle bin");
      break label68;
    }
    Log.e(this.TAG, "deleteMedia() - Fail to post to media content thread");
    onMediaDeleted(localMediaDeletionHandle, false);
    return localMediaDeletionHandle;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_IS_MEDIA_TABLE_READY) {
      return (TValue)super.get(paramPropertyKey);
    }
    return Boolean.valueOf(this.m_IsMediaTableReady);
  }
  
  public GroupedMedia[] getGroupedMedia(Media paramMedia, int paramInt)
  {
    if (!(paramMedia instanceof MediaStoreMedia)) {}
    do
    {
      do
      {
        return null;
        paramMedia = (MediaStoreMedia)paramMedia;
      } while (!paramMedia.isSubMedia());
      paramMedia = (GroupedMedia)getMedia(paramMedia.getGroupId(), 0);
    } while (paramMedia == null);
    return new GroupedMedia[] { paramMedia };
  }
  
  public Handle getMedia(String paramString, MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    verifyAccess();
    Object localObject;
    final long l;
    if (isRunningOrInitializing(false))
    {
      if (paramString == null) {
        break label145;
      }
      if ((FLAG_ALWAYS_REFRESH & paramInt) == 0) {
        break label157;
      }
      localObject = new SimpleRef();
      if (!MediaStoreMedia.isValidId(paramString, (Ref)localObject)) {
        break label211;
      }
      l = ((Long)((Ref)localObject).get()).longValue();
      localObject = Uri.parse(CONTENT_URI_STRING_FILE + "/" + l);
      paramMediaObtainCallback = new CallbackHandle("GetMedia", paramMediaObtainCallback, null)
      {
        protected void onClose(int paramAnonymousInt)
        {
          List localList = (List)MediaStoreMediaSource.this.m_MediaObtainCallbackHandles.get(Long.valueOf(l));
          if (localList == null) {}
          while ((!localList.remove(this)) || (!localList.isEmpty())) {
            return;
          }
          MediaStoreMediaSource.this.m_MediaObtainCallbackHandles.remove(Long.valueOf(l));
        }
      };
      paramString = (List)this.m_MediaObtainCallbackHandles.get(Long.valueOf(l));
      if (paramString == null) {
        break label237;
      }
    }
    for (;;)
    {
      paramString.add(paramMediaObtainCallback);
      syncMediaWithMediaStore((Uri)localObject);
      return paramMediaObtainCallback;
      return null;
      label145:
      Log.e(this.TAG, "getMedia() - No media ID");
      return null;
      label157:
      localObject = getMedia(paramString, 0);
      if (localObject == null) {
        break;
      }
      if (paramMediaObtainCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetMedia");
        paramMediaObtainCallback.onMediaObtained(this, ((Media)localObject).getContentUri(), paramString, (Media)localObject, prepareMediaFlagsForCallback((Media)localObject));
      }
      label211:
      Log.e(this.TAG, "getMedia() - Invalid ID : " + paramString);
      return null;
      label237:
      paramString = new ArrayList();
      this.m_MediaObtainCallbackHandles.put(Long.valueOf(l), paramString);
    }
  }
  
  public MediaStoreMedia getMedia(long paramLong)
  {
    return (MediaStoreMedia)getMedia(MediaStoreMedia.getId(paramLong), 0);
  }
  
  public Handle getMediaContentUri(final String paramString, final MediaStoreAccessCallback paramMediaStoreAccessCallback)
  {
    if (paramString != null)
    {
      if (paramMediaStoreAccessCallback != null)
      {
        if (!this.m_PermissionsReady) {
          break label74;
        }
        paramMediaStoreAccessCallback = new CallbackHandle("GetMediaContentURI", paramMediaStoreAccessCallback, null)
        {
          protected void onClose(int paramAnonymousInt) {}
        };
        if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
        {
          /* Error */
          public void run()
          {
            // Byte code:
            //   0: aconst_null
            //   1: astore 4
            //   3: aconst_null
            //   4: astore_2
            //   5: aconst_null
            //   6: astore_3
            //   7: aconst_null
            //   8: astore_1
            //   9: aload_0
            //   10: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$11:val$handle	Lcom/oneplus/base/CallbackHandle;
            //   13: invokestatic 38	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
            //   16: ifeq +70 -> 86
            //   19: invokestatic 44	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
            //   22: invokevirtual 48	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
            //   25: invokestatic 52	com/oneplus/gallery2/media/MediaStoreMediaSource:access$7	()Landroid/net/Uri;
            //   28: invokevirtual 58	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
            //   31: astore 5
            //   33: aload 5
            //   35: ifnonnull +52 -> 87
            //   38: aload_2
            //   39: astore_3
            //   40: aload_0
            //   41: getfield 21	com/oneplus/gallery2/media/MediaStoreMediaSource$11:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
            //   44: invokestatic 62	com/oneplus/gallery2/media/MediaStoreMediaSource:access$8	(Lcom/oneplus/gallery2/media/MediaStoreMediaSource;)Ljava/lang/String;
            //   47: ldc 64
            //   49: invokestatic 70	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
            //   52: aload 5
            //   54: ifnonnull +242 -> 296
            //   57: aload_1
            //   58: astore_2
            //   59: aload_0
            //   60: getfield 21	com/oneplus/gallery2/media/MediaStoreMediaSource$11:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
            //   63: sipush 10021
            //   66: iconst_2
            //   67: anewarray 4	java/lang/Object
            //   70: dup
            //   71: iconst_0
            //   72: aload_0
            //   73: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$11:val$handle	Lcom/oneplus/base/CallbackHandle;
            //   76: aastore
            //   77: dup
            //   78: iconst_1
            //   79: aload_2
            //   80: aastore
            //   81: invokestatic 76	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
            //   84: pop
            //   85: return
            //   86: return
            //   87: aload 5
            //   89: invokestatic 52	com/oneplus/gallery2/media/MediaStoreMediaSource:access$7	()Landroid/net/Uri;
            //   92: getstatic 82	com/oneplus/gallery2/media/MediaStoreMedia:MEDIA_STORE_COLUMNS_ID	[Ljava/lang/String;
            //   95: ldc 84
            //   97: iconst_1
            //   98: anewarray 86	java/lang/String
            //   101: dup
            //   102: iconst_0
            //   103: aload_0
            //   104: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$11:val$filePath	Ljava/lang/String;
            //   107: aastore
            //   108: aconst_null
            //   109: invokevirtual 92	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
            //   112: astore_3
            //   113: aload_3
            //   114: invokeinterface 98 1 0
            //   119: ifne +8 -> 127
            //   122: aconst_null
            //   123: astore_1
            //   124: goto +205 -> 329
            //   127: new 100	java/lang/StringBuilder
            //   130: dup
            //   131: invokestatic 104	com/oneplus/gallery2/media/MediaStoreMediaSource:access$11	()Ljava/lang/String;
            //   134: invokestatic 108	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
            //   137: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
            //   140: ldc 113
            //   142: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   145: aload_3
            //   146: iconst_0
            //   147: invokeinterface 121 2 0
            //   152: invokevirtual 124	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
            //   155: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   158: invokestatic 133	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
            //   161: astore_1
            //   162: goto +167 -> 329
            //   165: aload_3
            //   166: invokeinterface 136 1 0
            //   171: goto +162 -> 333
            //   174: astore_3
            //   175: aconst_null
            //   176: astore_2
            //   177: aload_1
            //   178: astore 4
            //   180: aload_3
            //   181: astore_1
            //   182: aload_2
            //   183: ifnull +89 -> 272
            //   186: aload_2
            //   187: aload_1
            //   188: if_acmpne +89 -> 277
            //   191: aload 4
            //   193: astore_3
            //   194: aload_2
            //   195: athrow
            //   196: astore_2
            //   197: aload 5
            //   199: astore_1
            //   200: aload_0
            //   201: getfield 21	com/oneplus/gallery2/media/MediaStoreMediaSource$11:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
            //   204: invokestatic 62	com/oneplus/gallery2/media/MediaStoreMediaSource:access$8	(Lcom/oneplus/gallery2/media/MediaStoreMediaSource;)Ljava/lang/String;
            //   207: new 100	java/lang/StringBuilder
            //   210: dup
            //   211: ldc -118
            //   213: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
            //   216: aload_0
            //   217: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$11:val$filePath	Ljava/lang/String;
            //   220: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   223: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   226: aload_2
            //   227: invokestatic 141	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
            //   230: aload_3
            //   231: astore_2
            //   232: aload_1
            //   233: ifnull -174 -> 59
            //   236: aload_1
            //   237: invokevirtual 144	android/content/ContentProviderClient:release	()Z
            //   240: pop
            //   241: aload_3
            //   242: astore_2
            //   243: goto -184 -> 59
            //   246: astore_2
            //   247: aload_3
            //   248: ifnonnull +5 -> 253
            //   251: aload_2
            //   252: athrow
            //   253: aload_3
            //   254: invokeinterface 136 1 0
            //   259: goto -8 -> 251
            //   262: astore_2
            //   263: aload 5
            //   265: astore_1
            //   266: aload_1
            //   267: ifnonnull +21 -> 288
            //   270: aload_2
            //   271: athrow
            //   272: aload_1
            //   273: astore_2
            //   274: goto -83 -> 191
            //   277: aload 4
            //   279: astore_3
            //   280: aload_2
            //   281: aload_1
            //   282: invokevirtual 148	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
            //   285: goto -94 -> 191
            //   288: aload_1
            //   289: invokevirtual 144	android/content/ContentProviderClient:release	()Z
            //   292: pop
            //   293: goto -23 -> 270
            //   296: aload 5
            //   298: invokevirtual 144	android/content/ContentProviderClient:release	()Z
            //   301: pop
            //   302: aload_1
            //   303: astore_2
            //   304: goto -245 -> 59
            //   307: astore_2
            //   308: aconst_null
            //   309: astore_1
            //   310: goto -44 -> 266
            //   313: astore_2
            //   314: goto -48 -> 266
            //   317: astore_2
            //   318: aconst_null
            //   319: astore_1
            //   320: goto -120 -> 200
            //   323: astore_1
            //   324: aconst_null
            //   325: astore_2
            //   326: goto -144 -> 182
            //   329: aload_3
            //   330: ifnonnull -165 -> 165
            //   333: goto -281 -> 52
            //   336: astore_1
            //   337: goto -155 -> 182
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	340	0	this	11
            //   8	312	1	localObject1	Object
            //   323	1	1	localObject2	Object
            //   336	1	1	localObject3	Object
            //   4	191	2	localObject4	Object
            //   196	31	2	localThrowable1	Throwable
            //   231	12	2	localObject5	Object
            //   246	6	2	localObject6	Object
            //   262	9	2	localObject7	Object
            //   273	31	2	localObject8	Object
            //   307	1	2	localObject9	Object
            //   313	1	2	localObject10	Object
            //   317	1	2	localThrowable2	Throwable
            //   325	1	2	localObject11	Object
            //   6	160	3	localObject12	Object
            //   174	7	3	localObject13	Object
            //   193	137	3	localObject14	Object
            //   1	277	4	localObject15	Object
            //   31	266	5	localContentProviderClient	ContentProviderClient
            // Exception table:
            //   from	to	target	type
            //   165	171	174	finally
            //   40	52	196	java/lang/Throwable
            //   194	196	196	java/lang/Throwable
            //   280	285	196	java/lang/Throwable
            //   113	122	246	finally
            //   127	162	246	finally
            //   40	52	262	finally
            //   194	196	262	finally
            //   280	285	262	finally
            //   19	33	307	finally
            //   200	230	313	finally
            //   19	33	317	java/lang/Throwable
            //   87	113	323	finally
            //   251	253	336	finally
            //   253	259	336	finally
          }
        })) {
          break label86;
        }
        return paramMediaStoreAccessCallback;
      }
    }
    else
    {
      Log.e(this.TAG, "getMediaContentUri() - No file path");
      return null;
    }
    Log.e(this.TAG, "getMediaContentUri() - No call-back to receive result");
    return null;
    label74:
    Log.w(this.TAG, "getMediaContentUri() - Permissions not ready");
    return null;
    label86:
    Log.e(this.TAG, "getMediaContentUri() - Fail to post to media content thread");
    return null;
  }
  
  public String getMediaId(Uri paramUri, String paramString)
  {
    paramString = new SimpleRef();
    if (PhotoMediaStoreMedia.parsePhotoContentUri(paramUri, paramString)) {}
    while ((VideoMediaStoreMedia.parseVideoContentUri(paramUri, paramString)) || (MediaStoreMedia.parseFileContentUri(paramUri, paramString))) {
      return MediaStoreMedia.getId(((Long)paramString.get()).longValue());
    }
    return null;
  }
  
  protected Iterable<Media> getRecycledMedia(MediaType paramMediaType, int paramInt)
  {
    if ((FLAG_EXPAND_GROUPED_MEDIA & paramInt) != 0) {
      return new BaseMediaSource.ExpandedMediaIterable(this, paramMediaType, this.m_RecycledMedia);
    }
    return new BaseMediaSource.NormalMediaIterable(this, paramMediaType, this.m_RecycledMedia);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool = false;
    Object localObject;
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    case 10021: 
    case 10010: 
    case 10020: 
    case 10001: 
    case 10004: 
      do
      {
        return;
        paramMessage = (Object[])paramMessage.obj;
        onMediaContentUriQueried((CallbackHandle)paramMessage[0], (Uri)paramMessage[1]);
        return;
        localObject = (MediaDeletionHandle)paramMessage.obj;
        if (paramMessage.arg1 == 0) {}
        for (;;)
        {
          onMediaDeleted((MediaDeletionHandle)localObject, bool);
          return;
          bool = true;
        }
        localObject = (Object[])paramMessage.obj;
        onMediaStoreAccessCompleted((CallbackHandle)localObject[0], (Uri)localObject[1], paramMessage.arg1, paramMessage.arg2);
        return;
        this.m_HasPendingMediaSyncWithMediaStore = false;
        syncMediaWithMediaStore();
        return;
        this.m_HasPendingSingleMediaSyncWithMediaStore = false;
        if (!this.m_PermissionsReady) {
          break;
        }
      } while (this.m_PendingMediaIdToSync.isEmpty());
      paramMessage = this.m_PendingMediaIdToSync.iterator();
      while (paramMessage.hasNext())
      {
        localObject = (Long)paramMessage.next();
        syncMediaWithMediaStore(Uri.parse(CONTENT_URI_STRING_FILE + "/" + localObject));
      }
      Log.w(this.TAG, "handleMessage() - Start full media sync when permissions ready");
      this.m_NeedToSyncMediaWhenPermsReady = true;
      return;
      this.m_PendingMediaIdToSync.clear();
      return;
    case 10000: 
      localObject = (Object[])paramMessage.obj;
      syncMediaWithMediaStore(localObject[0], paramMessage.arg1, paramMessage.arg2, (List)localObject[1], (List)localObject[2]);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    syncMediaWithMediaStore(((Long)paramMessage[0]).longValue(), (MediaStoreMedia.DbValues)paramMessage[1], (MediaStoreMedia.FileInfo)paramMessage[2]);
  }
  
  public Handle insertIntoMediaStore(final MediaType paramMediaType, final ContentValues paramContentValues, final MediaStoreAccessCallback paramMediaStoreAccessCallback)
  {
    if (paramMediaType != null) {
      if (paramContentValues == null) {
        break label78;
      }
    }
    switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[paramMediaType.ordinal()])
    {
    default: 
      Log.e(this.TAG, "insertIntoMediaStore() - Invalid media type : " + paramMediaType);
      return null;
      Log.e(this.TAG, "insertIntoMediaStore() - No media type");
      return null;
      label78:
      Log.e(this.TAG, "insertIntoMediaStore() - No values to insert");
      return null;
    }
    for (paramMediaType = CONTENT_URI_IMAGE;; paramMediaType = CONTENT_URI_VIDEO)
    {
      paramMediaStoreAccessCallback = new CallbackHandle("InsertIntoMediaStore", paramMediaStoreAccessCallback, null)
      {
        protected void onClose(int paramAnonymousInt) {}
      };
      if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   4: invokestatic 42	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
          //   7: ifeq +118 -> 125
          //   10: invokestatic 48	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
          //   13: invokevirtual 52	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
          //   16: aload_0
          //   17: getfield 27	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$contentUri	Landroid/net/Uri;
          //   20: invokevirtual 58	android/content/ContentResolver:acquireContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
          //   23: astore 6
          //   25: aload 6
          //   27: ifnull +99 -> 126
          //   30: aload 6
          //   32: aload_0
          //   33: getfield 27	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$contentUri	Landroid/net/Uri;
          //   36: aload_0
          //   37: getfield 29	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$values	Landroid/content/ContentValues;
          //   40: invokevirtual 64	android/content/ContentProviderClient:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
          //   43: astore 4
          //   45: aload 4
          //   47: ifnull +123 -> 170
          //   50: aload 4
          //   52: invokestatic 70	android/content/ContentUris:parseId	(Landroid/net/Uri;)J
          //   55: lstore_2
          //   56: invokestatic 74	com/oneplus/gallery2/media/MediaStoreMediaSource:access$7	()Landroid/net/Uri;
          //   59: invokevirtual 80	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
          //   62: lload_2
          //   63: invokestatic 86	java/lang/Long:toString	(J)Ljava/lang/String;
          //   66: invokevirtual 92	android/net/Uri$Builder:appendPath	(Ljava/lang/String;)Landroid/net/Uri$Builder;
          //   69: invokevirtual 95	android/net/Uri$Builder:build	()Landroid/net/Uri;
          //   72: astore 5
          //   74: aload 5
          //   76: astore 4
          //   78: aload_0
          //   79: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   82: astore 5
          //   84: aload 4
          //   86: ifnonnull +366 -> 452
          //   89: iconst_0
          //   90: istore_1
          //   91: aload 5
          //   93: sipush 10020
          //   96: iload_1
          //   97: iconst_0
          //   98: iconst_2
          //   99: anewarray 4	java/lang/Object
          //   102: dup
          //   103: iconst_0
          //   104: aload_0
          //   105: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   108: aastore
          //   109: dup
          //   110: iconst_1
          //   111: aload 4
          //   113: aastore
          //   114: invokestatic 101	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;IIILjava/lang/Object;)Z
          //   117: pop
          //   118: aload 6
          //   120: invokevirtual 105	android/content/ContentProviderClient:release	()Z
          //   123: pop
          //   124: return
          //   125: return
          //   126: aload_0
          //   127: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   130: invokestatic 109	com/oneplus/gallery2/media/MediaStoreMediaSource:access$8	(Lcom/oneplus/gallery2/media/MediaStoreMediaSource;)Ljava/lang/String;
          //   133: ldc 111
          //   135: invokestatic 117	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
          //   138: aload_0
          //   139: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   142: sipush 10020
          //   145: iconst_0
          //   146: iconst_0
          //   147: iconst_2
          //   148: anewarray 4	java/lang/Object
          //   151: dup
          //   152: iconst_0
          //   153: aload_0
          //   154: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   157: aastore
          //   158: dup
          //   159: iconst_1
          //   160: aload_0
          //   161: getfield 27	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$contentUri	Landroid/net/Uri;
          //   164: aastore
          //   165: invokestatic 101	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;IIILjava/lang/Object;)Z
          //   168: pop
          //   169: return
          //   170: aload_0
          //   171: getfield 29	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$values	Landroid/content/ContentValues;
          //   174: ldc 119
          //   176: invokevirtual 125	android/content/ContentValues:containsKey	(Ljava/lang/String;)Z
          //   179: ifne +6 -> 185
          //   182: goto -104 -> 78
          //   185: iconst_1
          //   186: anewarray 127	java/lang/String
          //   189: astore 5
          //   191: aload 5
          //   193: iconst_0
          //   194: aload_0
          //   195: getfield 29	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$values	Landroid/content/ContentValues;
          //   198: ldc 119
          //   200: invokevirtual 131	android/content/ContentValues:getAsString	(Ljava/lang/String;)Ljava/lang/String;
          //   203: aastore
          //   204: aload 6
          //   206: aload_0
          //   207: getfield 27	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$contentUri	Landroid/net/Uri;
          //   210: aload_0
          //   211: getfield 29	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$values	Landroid/content/ContentValues;
          //   214: ldc -123
          //   216: aload 5
          //   218: invokevirtual 137	android/content/ContentProviderClient:update	(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
          //   221: istore_1
          //   222: iload_1
          //   223: iconst_1
          //   224: if_icmpeq +6 -> 230
          //   227: goto -149 -> 78
          //   230: aload 6
          //   232: invokestatic 74	com/oneplus/gallery2/media/MediaStoreMediaSource:access$7	()Landroid/net/Uri;
          //   235: getstatic 143	com/oneplus/gallery2/media/MediaStoreMedia:MEDIA_STORE_COLUMNS_ID	[Ljava/lang/String;
          //   238: ldc -123
          //   240: aload 5
          //   242: aconst_null
          //   243: invokevirtual 147	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
          //   246: astore 7
          //   248: aload 7
          //   250: invokeinterface 152 1 0
          //   255: ifne +6 -> 261
          //   258: goto +199 -> 457
          //   261: new 154	java/lang/StringBuilder
          //   264: dup
          //   265: invokestatic 158	com/oneplus/gallery2/media/MediaStoreMediaSource:access$11	()Ljava/lang/String;
          //   268: invokestatic 162	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
          //   271: invokespecial 165	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   274: ldc -89
          //   276: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   279: aload 7
          //   281: iconst_0
          //   282: invokeinterface 175 2 0
          //   287: invokevirtual 178	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   290: invokevirtual 180	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   293: invokestatic 184	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
          //   296: astore 4
          //   298: goto +159 -> 457
          //   301: aload 7
          //   303: invokeinterface 187 1 0
          //   308: goto +154 -> 462
          //   311: astore 4
          //   313: aconst_null
          //   314: astore 5
          //   316: aload 5
          //   318: ifnull +98 -> 416
          //   321: aload 5
          //   323: aload 4
          //   325: if_acmpne +98 -> 423
          //   328: aload 5
          //   330: athrow
          //   331: astore 4
          //   333: aload_0
          //   334: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   337: invokestatic 109	com/oneplus/gallery2/media/MediaStoreMediaSource:access$8	(Lcom/oneplus/gallery2/media/MediaStoreMediaSource;)Ljava/lang/String;
          //   340: ldc -67
          //   342: aload 4
          //   344: invokestatic 192	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   347: aload_0
          //   348: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   351: sipush 10020
          //   354: iconst_0
          //   355: iconst_0
          //   356: iconst_2
          //   357: anewarray 4	java/lang/Object
          //   360: dup
          //   361: iconst_0
          //   362: aload_0
          //   363: getfield 25	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   366: aastore
          //   367: dup
          //   368: iconst_1
          //   369: aload_0
          //   370: getfield 27	com/oneplus/gallery2/media/MediaStoreMediaSource$13:val$contentUri	Landroid/net/Uri;
          //   373: aastore
          //   374: invokestatic 101	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;IIILjava/lang/Object;)Z
          //   377: pop
          //   378: aload 6
          //   380: invokevirtual 105	android/content/ContentProviderClient:release	()Z
          //   383: pop
          //   384: return
          //   385: astore 5
          //   387: aload 7
          //   389: ifnonnull +6 -> 395
          //   392: aload 5
          //   394: athrow
          //   395: aload 7
          //   397: invokeinterface 187 1 0
          //   402: goto -10 -> 392
          //   405: astore 4
          //   407: aload 6
          //   409: invokevirtual 105	android/content/ContentProviderClient:release	()Z
          //   412: pop
          //   413: aload 4
          //   415: athrow
          //   416: aload 4
          //   418: astore 5
          //   420: goto -92 -> 328
          //   423: aload 5
          //   425: aload 4
          //   427: invokevirtual 196	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
          //   430: goto -102 -> 328
          //   433: astore 5
          //   435: aload_0
          //   436: getfield 23	com/oneplus/gallery2/media/MediaStoreMediaSource$13:this$0	Lcom/oneplus/gallery2/media/MediaStoreMediaSource;
          //   439: invokestatic 109	com/oneplus/gallery2/media/MediaStoreMediaSource:access$8	(Lcom/oneplus/gallery2/media/MediaStoreMediaSource;)Ljava/lang/String;
          //   442: ldc -58
          //   444: aload 5
          //   446: invokestatic 192	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   449: goto -371 -> 78
          //   452: iconst_1
          //   453: istore_1
          //   454: goto -363 -> 91
          //   457: aload 7
          //   459: ifnonnull -158 -> 301
          //   462: goto -384 -> 78
          //   465: astore 4
          //   467: goto -151 -> 316
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	470	0	this	13
          //   90	364	1	i	int
          //   55	8	2	l	long
          //   43	254	4	localObject1	Object
          //   311	13	4	localObject2	Object
          //   331	12	4	localThrowable1	Throwable
          //   405	21	4	localThrowable2	Throwable
          //   465	1	4	localObject3	Object
          //   72	257	5	localObject4	Object
          //   385	8	5	localObject5	Object
          //   418	6	5	localThrowable3	Throwable
          //   433	12	5	localThrowable4	Throwable
          //   23	385	6	localContentProviderClient	ContentProviderClient
          //   246	212	7	localCursor	Cursor
          // Exception table:
          //   from	to	target	type
          //   230	248	311	finally
          //   301	308	311	finally
          //   30	45	331	java/lang/Throwable
          //   78	84	331	java/lang/Throwable
          //   91	118	331	java/lang/Throwable
          //   170	182	331	java/lang/Throwable
          //   185	222	331	java/lang/Throwable
          //   328	331	331	java/lang/Throwable
          //   423	430	331	java/lang/Throwable
          //   435	449	331	java/lang/Throwable
          //   248	258	385	finally
          //   261	298	385	finally
          //   30	45	405	finally
          //   50	74	405	finally
          //   78	84	405	finally
          //   91	118	405	finally
          //   170	182	405	finally
          //   185	222	405	finally
          //   328	331	405	finally
          //   333	378	405	finally
          //   423	430	405	finally
          //   435	449	405	finally
          //   50	74	433	java/lang/Throwable
          //   392	395	465	finally
          //   395	402	465	finally
        }
      })) {
        break;
      }
      return paramMediaStoreAccessCallback;
    }
    Log.e(this.TAG, "insertIntoMediaStore() - Fail to post to media content thread");
    return null;
  }
  
  public boolean isMediaIdSupported(String paramString)
  {
    if (MediaStoreMedia.isValidId(paramString, null)) {}
    while (BurstMediaStoreMedia.isValidId(paramString)) {
      return true;
    }
    return false;
  }
  
  public boolean isPathInHiddenDirectory(String paramString)
  {
    if (paramString != null)
    {
      int i = this.m_HiddenPathPrefixList.size() - 1;
      for (;;)
      {
        if (i < 0) {
          break label51;
        }
        if (paramString.startsWith((String)this.m_HiddenPathPrefixList.get(i))) {
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
  
  public boolean isRecycledMedia(Media paramMedia)
  {
    return this.m_RecycledMedia.contains(paramMedia);
  }
  
  public boolean isSubMedia(Media paramMedia)
  {
    if (!(paramMedia instanceof MediaStoreMedia)) {
      return false;
    }
    return ((MediaStoreMedia)paramMedia).isSubMedia();
  }
  
  final void notifyMediaSetDeleted(MediaSet paramMediaSet, Media[] paramArrayOfMedia)
  {
    verifyAccess();
    if (isRunningOrInitializing(true)) {
      if (paramMediaSet != null) {
        break label18;
      }
    }
    label18:
    while ((paramMediaSet.isVirtual()) || (paramArrayOfMedia == null) || (paramArrayOfMedia.length == 0))
    {
      return;
      return;
    }
    Log.v(this.TAG, "notifyMediaSetDeleted() - Media set : ", paramMediaSet, ", ", Integer.valueOf(paramArrayOfMedia.length), " media in this set");
    int i = paramArrayOfMedia.length - 1;
    if (i >= 0)
    {
      paramMediaSet = paramArrayOfMedia[i];
      if (paramMediaSet != null)
      {
        if ((paramMediaSet instanceof BaseGroupedMedia)) {
          break label117;
        }
        removeMedia(paramMediaSet, true, prepareMediaFlagsForCallback(paramMediaSet));
        releaseMedia(paramMediaSet);
        this.m_PendingAddressObtainingMedia.remove(paramMediaSet);
      }
      for (;;)
      {
        i -= 1;
        break;
        label117:
        Media[] arrayOfMedia = (Media[])CollectionUtils.toArray(((BaseGroupedMedia)paramMediaSet).getSubMedia(), Media.class);
        int j = arrayOfMedia.length - 1;
        if (j >= 0)
        {
          Media localMedia = arrayOfMedia[j];
          if (!(localMedia instanceof MediaStoreMedia)) {}
          for (;;)
          {
            removeMedia(localMedia, true, prepareMediaFlagsForCallback(localMedia));
            j -= 1;
            break;
            removeFromGroupedMedia((MediaStoreMedia)localMedia);
            releaseMedia(localMedia);
          }
        }
        releaseMedia(paramMediaSet);
      }
    }
    syncMediaWithMediaStoreDelayed(true);
  }
  
  protected void notifyMediaUpdated(Media paramMedia, int paramInt)
  {
    super.notifyMediaUpdated(paramMedia, paramInt);
    if (!(paramMedia instanceof MediaStoreMedia)) {}
    Object localObject;
    do
    {
      do
      {
        return;
        localObject = (MediaStoreMedia)paramMedia;
      } while (!((MediaStoreMedia)localObject).isSubMedia());
      localObject = (BaseGroupedMedia)getMedia(((MediaStoreMedia)localObject).getGroupId(), 0);
    } while (localObject == null);
    ((BaseGroupedMedia)localObject).notifySubMediaUpdated(paramMedia, paramInt);
  }
  
  protected void onActivated()
  {
    super.onActivated();
    if (this.m_PendingMediaSyncCount <= 0) {
      return;
    }
    this.m_PendingMediaSyncCount = 0;
    syncMediaWithMediaStore();
  }
  
  protected void onActivationHandleClosed(BaseMediaSource.ActivationHandle paramActivationHandle)
  {
    if ((paramActivationHandle.getFlags() & FLAG_CAMERA_ROLL_ONLY) == 0) {}
    for (;;)
    {
      updateMode();
      super.onActivationHandleClosed(paramActivationHandle);
      return;
      this.m_CameraRollOnlyFlagCount -= 1;
    }
  }
  
  protected boolean onActivationHandleCreated(BaseMediaSource.ActivationHandle paramActivationHandle)
  {
    super.onActivationHandleCreated(paramActivationHandle);
    if ((paramActivationHandle.getFlags() & FLAG_CAMERA_ROLL_ONLY) == 0) {}
    for (;;)
    {
      updateMode();
      return true;
      this.m_CameraRollOnlyFlagCount += 1;
    }
  }
  
  protected void onDeactivated()
  {
    if (!this.m_HasPendingMediaSyncWithMediaStore) {}
    for (;;)
    {
      super.onDeactivated();
      return;
      Log.w(this.TAG, "onDeactivated() - Cancel scheduled full media sync");
      this.m_PendingMediaSyncCount += 1;
      this.m_HasPendingMediaSyncWithMediaStore = false;
      HandlerUtils.removeMessages(this, 10001);
    }
  }
  
  protected void onDeinitialize()
  {
    Handle.close(this.m_ContentChangeCallbackHandles);
    try
    {
      BaseApplication.current().unregisterReceiver(this.m_MediaIntentReceiver);
      BaseApplication.current().unregisterReceiver(this.m_MediaSetIntentReceiver);
      super.onDeinitialize();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onDeinitialize() - Fail to unregister receiver", localThrowable);
      }
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    BaseApplication localBaseApplication;
    Object localObject1;
    NetworkManager localNetworkManager;
    Object localObject2;
    if (MediaContentThread.current() != null)
    {
      localBaseApplication = BaseApplication.current();
      localObject1 = (ContentObserver)localBaseApplication.findComponent(ContentObserver.class);
      localNetworkManager = (NetworkManager)localBaseApplication.findComponent(NetworkManager.class);
      localObject2 = Environment.getExternalStorageDirectory().getAbsolutePath();
      this.m_HiddenPathPrefixList.add(localObject2 + "/Android/");
      this.m_HiddenPathPrefixList.add(localObject2 + "/oem_log/");
      this.m_HiddenPathPrefixList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/");
      if (((Boolean)localBaseApplication.get(BaseApplication.PROP_IS_READ_STORAGE_PERM_GRANTED)).booleanValue()) {
        break label326;
      }
      localBaseApplication.addCallback(BaseApplication.PROP_IS_READ_STORAGE_PERM_GRANTED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
            return;
          }
          MediaStoreMediaSource.this.onPermissionsReady();
        }
      });
    }
    for (;;)
    {
      if (localObject1 == null)
      {
        Log.e(this.TAG, "onInitialize() - No ContentObserver");
        label189:
        localObject1 = new IntentFilter();
        if (Build.VERSION.SDK_INT < 24) {
          break label411;
        }
        ((IntentFilter)localObject1).addAction("com.oneplus.camera.intent.action.NEW_PICTURE");
        ((IntentFilter)localObject1).addAction("com.oneplus.camera.intent.action.NEW_VIDEO");
        ((IntentFilter)localObject1).addAction("com.oneplus.camera.service.CLEAR_IMAGE_CACHE");
        ((IntentFilter)localObject1).addAction("com.oneplus.gallery.MEDIA_STORE_MEDIA_DELETED");
        ((IntentFilter)localObject1).addAction("com.oneplus.gallery2.media.action.MEDIA_SET_DELETED");
      }
      try
      {
        ((IntentFilter)localObject1).addDataType("image/*");
        ((IntentFilter)localObject1).addDataType("video/*");
        localBaseApplication.registerReceiver(this.m_MediaIntentReceiver, (IntentFilter)localObject1);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("com.oneplus.gallery2.media.action.MEDIA_SET_DELETED");
        localBaseApplication.registerReceiver(this.m_MediaSetIntentReceiver, (IntentFilter)localObject1);
        localBaseApplication.addCallback(BaseApplication.PROP_LOCALE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Locale> paramAnonymousPropertyKey, PropertyChangeEventArgs<Locale> paramAnonymousPropertyChangeEventArgs)
          {
            MediaStoreMediaSource.this.onLocaleChanged();
          }
        });
        if (localNetworkManager == null)
        {
          Log.w(this.TAG, "onInitialize() - No NetworkManager");
          return;
          throw new RuntimeException("No media content thread");
          label326:
          onPermissionsReady();
          continue;
          localObject2 = getHandler();
          this.m_ContentChangeCallbackHandles.addHandle(((ContentObserver)localObject1).registerContentChangedCallback(CONTENT_URI_IMAGE, this.m_ContentChangeCallback, (Handler)localObject2));
          this.m_ContentChangeCallbackHandles.addHandle(((ContentObserver)localObject1).registerContentChangedCallback(CONTENT_URI_VIDEO, this.m_ContentChangeCallback, (Handler)localObject2));
          this.m_ContentChangeCallbackHandles.addHandle(((ContentObserver)localObject1).registerContentChangedCallback(CONTENT_URI_OBJECT, this.m_ContentChangeCallback, (Handler)localObject2));
          break label189;
          label411:
          ((IntentFilter)localObject1).addAction("android.hardware.action.NEW_PICTURE");
          ((IntentFilter)localObject1).addAction("android.hardware.action.NEW_VIDEO");
        }
      }
      catch (IntentFilter.MalformedMimeTypeException localMalformedMimeTypeException)
      {
        for (;;)
        {
          Log.e(this.TAG, "onInitialize() - Unknown error while preparing intent filter", localMalformedMimeTypeException);
        }
        localNetworkManager.addCallback(NetworkManager.PROP_IS_NETWORK_CONNECTED, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
          {
            MediaStoreMediaSource.this.onNetworkConnectionStateChanged(((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
          }
        });
      }
    }
  }
  
  final Handle recycleMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    verifyAccess();
    if (isRunningOrInitializing(true))
    {
      if (paramMedia == null) {
        break label77;
      }
      if (!containsMedia(paramMedia)) {
        break label89;
      }
      if (!this.m_RecycledMedia.add(paramMedia)) {
        break label121;
      }
      paramInt = prepareMediaFlagsForCallback(paramMedia) | Media.FLAG_MOVE_TO_RECYCE_BIN | paramInt;
      if (paramDeletionCallback != null) {
        break label153;
      }
      notifyMediaDeleted(paramMedia, paramInt);
      if (paramDeletionCallback != null) {
        break label162;
      }
    }
    for (;;)
    {
      return new MediaDeletionHandle(paramMedia, paramDeletionCallback, paramInt);
      return null;
      label77:
      Log.e(this.TAG, "recycleMedia() - No media to delete");
      return null;
      label89:
      Log.e(this.TAG, "recycleMedia() - " + paramMedia + " is not contained in media table");
      return null;
      label121:
      Log.e(this.TAG, "recycleMedia() - " + paramMedia + " is already recycled");
      return null;
      label153:
      paramDeletionCallback.onDeletionStarted(paramMedia, paramInt);
      break;
      label162:
      paramDeletionCallback.onDeletionCompleted(paramMedia, true, paramInt);
    }
  }
  
  final void scheduleAddressObtaining(MediaStoreMedia paramMediaStoreMedia)
  {
    if (GalleryLib.isClient()) {}
    while (!this.m_PendingAddressObtainingMedia.add(paramMediaStoreMedia)) {
      return;
    }
    if (this.m_IsMediaAddressesObtainingScheduled)
    {
      if (this.m_PendingAddressObtainingMedia.size() >= 64) {}
    }
    else
    {
      this.m_IsMediaAddressesObtainingScheduled = true;
      HandlerUtils.post(BaseApplication.current(), this.m_ObtainMediaAddressesRunnable, 500L);
      return;
    }
    this.m_ObtainMediaAddressesRunnable.run();
  }
  
  void updateAddress(final MediaStoreMedia paramMediaStoreMedia, final Address paramAddress)
  {
    final double d1;
    double d2;
    final String str1;
    String str2;
    final String str3;
    final String str4;
    final String str5;
    final String str6;
    final String str7;
    final String str8;
    if (paramAddress == null)
    {
      paramAddress = null;
      d1 = NaN.0D;
      d2 = NaN.0D;
      str1 = null;
      str2 = null;
      str3 = null;
      str4 = null;
      str5 = null;
      str6 = null;
      str7 = null;
      str8 = null;
    }
    for (;;)
    {
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          GalleryDatabase.ExtraMediaInfo localExtraMediaInfo;
          if (!paramMediaStoreMedia.isReleased())
          {
            localExtraMediaInfo = GalleryDatabase.getExtraMediaInfo(paramMediaStoreMedia.getMediaId());
            if (localExtraMediaInfo == null) {
              break label122;
            }
          }
          for (;;)
          {
            localExtraMediaInfo.addressLocale = paramAddress;
            localExtraMediaInfo.addressLatitude = d1;
            localExtraMediaInfo.addressLongitude = str1;
            localExtraMediaInfo.addressCountry = str3;
            localExtraMediaInfo.addressAdminArea = str4;
            localExtraMediaInfo.addressSubAdminArea = str5;
            localExtraMediaInfo.addressLocality = str6;
            localExtraMediaInfo.addressSubLocality = str7;
            localExtraMediaInfo.addressFeature = str8;
            localExtraMediaInfo.addressAddressLine0 = this.val$addrLine0;
            localExtraMediaInfo.addressAddressLine1 = this.val$addrLine1;
            if (!GalleryDatabase.updateExtraMediaInfo(localExtraMediaInfo)) {
              break;
            }
            return;
            return;
            label122:
            localExtraMediaInfo = new GalleryDatabase.ExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          }
          Log.e(MediaStoreMediaSource.this.TAG, "updateAddress() - Fail to update address for " + paramMediaStoreMedia);
        }
      });
      return;
      String str9 = paramAddress.getLocale().toString();
      d1 = paramAddress.getLatitude();
      d2 = paramAddress.getLongitude();
      str1 = paramAddress.getCountryName();
      str2 = paramAddress.getAdminArea();
      str3 = paramAddress.getSubAdminArea();
      str4 = paramAddress.getLocality();
      str5 = paramAddress.getSubLocality();
      str6 = paramAddress.getFeatureName();
      str7 = paramAddress.getAddressLine(0);
      str8 = paramAddress.getAddressLine(1);
      paramAddress = str9;
    }
  }
  
  void updateObjectDetectionResult(final MediaStoreMedia paramMediaStoreMedia, final long paramLong1, long paramLong2, final List<GalleryDatabase.ObjectDetectionResult> paramList)
  {
    HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        GalleryDatabase.ExtraMediaInfo localExtraMediaInfo;
        if (!paramMediaStoreMedia.isReleased())
        {
          localExtraMediaInfo = GalleryDatabase.getExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          if (localExtraMediaInfo == null) {
            break label62;
          }
          localExtraMediaInfo.objectDetectionVersion = paramLong1;
          localExtraMediaInfo.objectDetectionFileTime = paramList;
          if (this.val$result != null) {
            break label80;
          }
          label48:
          localExtraMediaInfo.objectDetectionResult = null;
        }
        for (;;)
        {
          if (!GalleryDatabase.updateExtraMediaInfo(localExtraMediaInfo)) {
            break label143;
          }
          return;
          return;
          label62:
          localExtraMediaInfo = new GalleryDatabase.ExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          break;
          label80:
          if (this.val$result.isEmpty()) {
            break label48;
          }
          if (localExtraMediaInfo.objectDetectionResult != null)
          {
            localExtraMediaInfo.objectDetectionResult.clear();
            localExtraMediaInfo.objectDetectionResult.addAll(this.val$result);
          }
          else
          {
            localExtraMediaInfo.objectDetectionResult = new ArrayList(this.val$result);
          }
        }
        label143:
        Log.e(MediaStoreMediaSource.this.TAG, "updateObjectDetectionResult() - Fail to update object detection result for " + paramMediaStoreMedia);
      }
    });
  }
  
  void updateOnePlusFlags(final MediaStoreMedia paramMediaStoreMedia, final int paramInt1, final int paramInt2)
  {
    HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        GalleryDatabase.ExtraMediaInfo localExtraMediaInfo;
        if (!paramMediaStoreMedia.isReleased())
        {
          localExtraMediaInfo = GalleryDatabase.getExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          if (localExtraMediaInfo == null) {
            break label64;
          }
        }
        for (;;)
        {
          localExtraMediaInfo.oneplusFlags |= paramInt1;
          localExtraMediaInfo.oneplusFlags &= (paramInt2 ^ 0xFFFFFFFF);
          if (!GalleryDatabase.updateExtraMediaInfo(localExtraMediaInfo)) {
            break;
          }
          return;
          return;
          label64:
          localExtraMediaInfo = new GalleryDatabase.ExtraMediaInfo(paramMediaStoreMedia.getMediaId());
        }
        Log.e(MediaStoreMediaSource.this.TAG, "updateOnePlusFlags() - Fail to update OnePlus flags for " + paramMediaStoreMedia);
      }
    });
  }
  
  void updateSceneDetectionResult(final MediaStoreMedia paramMediaStoreMedia, final long paramLong1, long paramLong2, final List<GalleryDatabase.SceneDetectionResult> paramList)
  {
    HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        GalleryDatabase.ExtraMediaInfo localExtraMediaInfo;
        if (!paramMediaStoreMedia.isReleased())
        {
          localExtraMediaInfo = GalleryDatabase.getExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          if (localExtraMediaInfo == null) {
            break label62;
          }
          localExtraMediaInfo.sceneDetectionVersion = paramLong1;
          localExtraMediaInfo.sceneDetectionFileTime = paramList;
          if (this.val$result != null) {
            break label80;
          }
          label48:
          localExtraMediaInfo.sceneDetectionResult = null;
        }
        for (;;)
        {
          if (!GalleryDatabase.updateExtraMediaInfo(localExtraMediaInfo)) {
            break label143;
          }
          return;
          return;
          label62:
          localExtraMediaInfo = new GalleryDatabase.ExtraMediaInfo(paramMediaStoreMedia.getMediaId());
          break;
          label80:
          if (this.val$result.isEmpty()) {
            break label48;
          }
          if (localExtraMediaInfo.sceneDetectionResult != null)
          {
            localExtraMediaInfo.sceneDetectionResult.clear();
            localExtraMediaInfo.sceneDetectionResult.addAll(this.val$result);
          }
          else
          {
            localExtraMediaInfo.sceneDetectionResult = new ArrayList(this.val$result);
          }
        }
        label143:
        Log.e(MediaStoreMediaSource.this.TAG, "updateSceneDetectionResult() - Fail to update scene detection result for " + paramMediaStoreMedia);
      }
    });
  }
  
  private final class MediaDeletionHandle
    extends MediaHandle
  {
    public final Media.DeletionCallback callback;
    public volatile boolean isCancellable = true;
    public volatile boolean isCancelled;
    
    public MediaDeletionHandle(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
    {
      super(paramMedia, paramInt);
      this.callback = paramDeletionCallback;
    }
    
    protected void onClose(int paramInt)
    {
      if ((getFlags() | Media.FLAG_MOVE_TO_RECYCE_BIN) == 0) {}
      try
      {
        if (!this.isCancellable)
        {
          return;
          MediaStoreMediaSource.this.restoreMedia(this);
          return;
        }
        this.isCancelled = true;
        if (this.callback == null) {
          return;
        }
      }
      finally {}
      this.callback.onDeletionCancelled(getMedia(), getFlags() | MediaStoreMediaSource.this.prepareMediaFlagsForCallback(getMedia()));
    }
  }
  
  public static abstract class MediaStoreAccessCallback
  {
    public void onCompleted(Handle paramHandle, Uri paramUri, int paramInt1, int paramInt2) {}
  }
  
  private final class QueryAllMediaStoreContentRunnable
    implements Runnable
  {
    private final int contentStartOffset;
    private final int mode;
    private final Object token;
    
    public QueryAllMediaStoreContentRunnable(Object paramObject, int paramInt1, int paramInt2)
    {
      this.token = paramObject;
      this.mode = paramInt1;
      this.contentStartOffset = paramInt2;
    }
    
    public void run()
    {
      MediaStoreMediaSource.this.queryAllMediaStoreContent(this.token, this.mode, this.contentStartOffset);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaStoreMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */