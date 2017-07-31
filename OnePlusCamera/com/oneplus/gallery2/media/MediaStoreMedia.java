package com.oneplus.gallery2.media;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore.Files;
import android.text.TextUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Device;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.gallery2.contentdetection.ObjectType;
import com.oneplus.gallery2.media.content.MediaContentRecognitionScene;
import com.oneplus.gallery2.media.content.Scene;
import com.oneplus.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class MediaStoreMedia
  extends BaseMedia
  implements MediaStoreItem, ContentAwareMedia
{
  private static final Uri CONTENT_URI_FILE = MediaStore.Files.getContentUri("external");
  private static final String CONTENT_URI_STRING_FILE = CONTENT_URI_FILE.toString();
  public static final int FLAG_GROUP_CHANGED;
  public static final int FLAG_PARENT_ID_CHANGED;
  private static final String ID_PREFIX = "MediaStore/";
  private static final int INTERNAL_FLAG_BURST_SUB_MEDIA = 256;
  private static final int INTERNAL_FLAG_CAPTURED_BY_FRONT_CAM = 4;
  private static final int INTERNAL_FLAG_FAVORITE = 1;
  private static final int INTERNAL_FLAG_HIDDEN = 8;
  private static final int INTERNAL_FLAG_RAW = 22;
  private static final int INTERNAL_FLAG_RAW_SUB_MEDIA = 512;
  private static final int INTERNAL_FLAG_RELEASED = 1073741824;
  private static final int INTERNAL_FLAG_SUB_MEDIA = 256;
  private static final int INTERNAL_FLAG_TEMPORARY = 2;
  static final String[] MEDIA_STORE_COLUMNS = { "_id", "media_type", "_data", "_size", "mime_type", "date_modified", "datetaken", "date_added", "width", "height", "latitude", "longitude", "orientation", "duration", "parent", "_display_name", "title" };
  static final String[] MEDIA_STORE_COLUMNS_ID = { "_id" };
  private static final String TAG;
  private long m_AddedTime;
  private Address m_Address;
  private Set<ObjectType> m_ContentObjectTypes;
  private Set<Scene> m_ContentScenes;
  private final Uri m_ContentUri;
  private String m_DisplayName;
  private String m_FilePath;
  private long m_FileSize;
  private String m_GroupId;
  private final String m_Id;
  private volatile int m_InternalFlags;
  private boolean m_IsParentVisible = true;
  private long m_LastModifiedTime;
  private Location m_Location;
  private final long m_MediaId;
  private String m_MimeType;
  private long m_ObjectDetectionFileTime;
  private long m_ObjectDetectionVersion;
  private long m_ParentId = -1L;
  private Address m_PrevAddress;
  private ObjectType[] m_PrevContentObjectTypes;
  private Scene[] m_PrevContentScenes;
  private String m_PrevFilePath;
  private Location m_PrevLocation;
  private long m_PrevParentId = -1L;
  private long m_PrevTakenTime;
  private long m_SceneDetectionFileTime;
  private long m_SceneDetectionVersion;
  private long m_TakenTime;
  private String m_Title;
  
  static
  {
    FLAG_PARENT_ID_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_GROUP_CHANGED = FLAGS_GROUP.nextIntFlag();
    TAG = MediaStoreMedia.class.getSimpleName();
  }
  
  protected MediaStoreMedia(MediaStoreMediaSource paramMediaStoreMediaSource, DbValues paramDbValues, FileInfo paramFileInfo)
  {
    super(paramMediaStoreMediaSource, getTypeFromMediaStoreValue(paramDbValues.mediaType));
    this.m_Id = getId(paramDbValues.id);
    this.m_MediaId = paramDbValues.id;
    this.m_ContentUri = Uri.parse(CONTENT_URI_STRING_FILE + "/" + paramDbValues.id);
    onUpdate(paramDbValues, paramFileInfo, true);
  }
  
  static final MediaStoreMedia create(MediaStoreMediaSource paramMediaStoreMediaSource, DbValues paramDbValues, FileInfo paramFileInfo)
  {
    for (;;)
    {
      try
      {
        switch (paramDbValues.mediaType)
        {
        case 2: 
          String str = paramDbValues.data;
          if (!FileUtils.isImageFilePath(str))
          {
            if (FileUtils.isVideoFilePath(str)) {
              continue;
            }
            Log.e(TAG, "create() - Unknown media type : " + paramDbValues.mediaType);
            return null;
          }
        case 1: 
          return new PhotoMediaStoreMedia(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
        case 3: 
          return new VideoMediaStoreMedia(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
          paramDbValues.mediaType = 1;
          return new PhotoMediaStoreMedia(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
          paramDbValues.mediaType = 3;
          paramMediaStoreMediaSource = new VideoMediaStoreMedia(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
          return paramMediaStoreMediaSource;
        }
      }
      catch (Throwable paramMediaStoreMediaSource)
      {
        Log.e(TAG, "create() - Fail to create media for " + paramDbValues.data, paramMediaStoreMediaSource);
        return null;
      }
    }
  }
  
  public static String getId(long paramLong)
  {
    return "MediaStore/" + paramLong;
  }
  
  public static MediaType getTypeFromMediaStoreValue(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      return MediaType.UNKNOWN;
    case 1: 
      return MediaType.PHOTO;
    }
    return MediaType.VIDEO;
  }
  
  static final boolean isValidId(String paramString, Ref<Long> paramRef)
  {
    if (paramString != null) {
      if (paramString.startsWith("MediaStore/")) {
        break label17;
      }
    }
    label17:
    while (paramString.length() == "MediaStore/".length())
    {
      return false;
      return false;
    }
    if (paramRef == null)
    {
      int i = "MediaStore/".length();
      int j = paramString.length();
      while (i < j)
      {
        if (!Character.isDigit(paramString.charAt(i))) {
          break label95;
        }
        i += 1;
      }
    }
    try
    {
      paramRef.set(Long.valueOf(Long.parseLong(paramString.substring("MediaStore/".length()), 10)));
      return true;
    }
    catch (Throwable paramString)
    {
      return false;
    }
    label95:
    return false;
  }
  
  static final boolean parseContentUri(Uri paramUri1, Uri paramUri2, Ref<Long> paramRef)
  {
    String str;
    if (paramUri2 != null)
    {
      str = paramUri2.toString();
      paramUri1 = paramUri1.toString();
      if (str.startsWith(paramUri1)) {
        break label28;
      }
    }
    label28:
    while (str.length() == paramUri1.length())
    {
      return false;
      return false;
    }
    if (str.charAt(paramUri1.length()) == '/')
    {
      if (paramRef == null)
      {
        int i = paramUri1.length();
        int j = str.length();
        while (i < j)
        {
          if (!Character.isDigit(str.charAt(i))) {
            break label115;
          }
          i += 1;
        }
      }
    }
    else {
      return false;
    }
    try
    {
      paramRef.set(Long.valueOf(ContentUris.parseId(paramUri2)));
      return true;
    }
    catch (Throwable paramUri1)
    {
      return false;
    }
    label115:
    return false;
  }
  
  static final boolean parseFileContentUri(Uri paramUri, Ref<Long> paramRef)
  {
    return parseContentUri(CONTENT_URI_FILE, paramUri, paramRef);
  }
  
  private boolean updateObjectDetectionResult(List<GalleryDatabase.ObjectDetectionResult> paramList, boolean paramBoolean)
  {
    Object localObject = null;
    if (paramBoolean)
    {
      this.m_PrevContentObjectTypes = null;
      label12:
      if (paramList != null) {
        break label75;
      }
    }
    for (;;)
    {
      if (this.m_PrevContentObjectTypes == null)
      {
        return false;
        if ((this.m_ContentObjectTypes == null) || (!this.m_ContentObjectTypes.isEmpty())) {
          break;
        }
        this.m_PrevContentObjectTypes = ((ObjectType[])this.m_ContentObjectTypes.toArray(new ObjectType[this.m_ContentObjectTypes.size()]));
        break label12;
        label75:
        if (!paramList.isEmpty())
        {
          label108:
          int i;
          if (!paramBoolean)
          {
            if (this.m_ContentObjectTypes != null) {
              break label220;
            }
            this.m_ContentObjectTypes = new HashSet();
            paramBoolean = false;
            i = paramList.size() - 1;
            label117:
            if (i < 0) {
              break label365;
            }
            localObject = ObjectType.fromId(((GalleryDatabase.ObjectDetectionResult)paramList.get(i)).objectTypeId);
            if (localObject != null) {
              break label346;
            }
          }
          for (;;)
          {
            i -= 1;
            break label117;
            this.m_ContentObjectTypes = new HashSet();
            i = paramList.size() - 1;
            label171:
            if (i >= 0)
            {
              localObject = ObjectType.fromId(((GalleryDatabase.ObjectDetectionResult)paramList.get(i)).objectTypeId);
              if (localObject != null) {
                break label205;
              }
            }
            for (;;)
            {
              i -= 1;
              break label171;
              break;
              label205:
              this.m_ContentObjectTypes.add(localObject);
            }
            label220:
            Iterator localIterator = this.m_ContentObjectTypes.iterator();
            while (localIterator.hasNext())
            {
              ObjectType localObjectType = (ObjectType)localIterator.next();
              if (!paramList.contains(localObjectType))
              {
                if (localObject != null) {}
                for (;;)
                {
                  ((List)localObject).add(localObjectType);
                  break;
                  localObject = new ArrayList();
                }
              }
            }
            if (localObject == null)
            {
              paramBoolean = false;
              break label108;
            }
            i = ((List)localObject).size();
            for (;;)
            {
              i -= 1;
              if (i < 0) {
                break;
              }
              this.m_ContentObjectTypes.remove(((List)localObject).get(i));
            }
            paramBoolean = true;
            break label108;
            label346:
            if (this.m_ContentObjectTypes.add(localObject)) {
              paramBoolean = true;
            }
          }
          label365:
          return paramBoolean;
        }
      }
    }
    this.m_ContentObjectTypes = null;
    return true;
  }
  
  private boolean updateSceneDetectionResult(List<GalleryDatabase.SceneDetectionResult> paramList, boolean paramBoolean)
  {
    Object localObject = null;
    if (paramBoolean)
    {
      this.m_PrevContentScenes = null;
      label12:
      if (paramList != null) {
        break label75;
      }
    }
    for (;;)
    {
      if (this.m_PrevContentScenes == null)
      {
        return false;
        if ((this.m_ContentScenes == null) || (!this.m_ContentScenes.isEmpty())) {
          break;
        }
        this.m_PrevContentScenes = ((Scene[])this.m_ContentScenes.toArray(new Scene[this.m_ContentScenes.size()]));
        break label12;
        label75:
        if (!paramList.isEmpty())
        {
          label108:
          int i;
          if (!paramBoolean)
          {
            if (this.m_ContentScenes != null) {
              break label220;
            }
            this.m_ContentScenes = new HashSet();
            paramBoolean = false;
            i = paramList.size() - 1;
            label117:
            if (i < 0) {
              break label365;
            }
            localObject = MediaContentRecognitionScene.create(((GalleryDatabase.SceneDetectionResult)paramList.get(i)).sceneId);
            if (localObject != null) {
              break label346;
            }
          }
          for (;;)
          {
            i -= 1;
            break label117;
            this.m_ContentScenes = new HashSet();
            i = paramList.size() - 1;
            label171:
            if (i >= 0)
            {
              localObject = MediaContentRecognitionScene.create(((GalleryDatabase.SceneDetectionResult)paramList.get(i)).sceneId);
              if (localObject != null) {
                break label205;
              }
            }
            for (;;)
            {
              i -= 1;
              break label171;
              break;
              label205:
              this.m_ContentScenes.add(localObject);
            }
            label220:
            Iterator localIterator = this.m_ContentScenes.iterator();
            while (localIterator.hasNext())
            {
              Scene localScene = (Scene)localIterator.next();
              if (!paramList.contains(localScene))
              {
                if (localObject != null) {}
                for (;;)
                {
                  ((List)localObject).add(localScene);
                  break;
                  localObject = new ArrayList();
                }
              }
            }
            if (localObject == null)
            {
              paramBoolean = false;
              break label108;
            }
            i = ((List)localObject).size();
            for (;;)
            {
              i -= 1;
              if (i < 0) {
                break;
              }
              this.m_ContentScenes.remove(((List)localObject).get(i));
            }
            paramBoolean = true;
            break label108;
            label346:
            if (this.m_ContentScenes.add(localObject)) {
              paramBoolean = true;
            }
          }
          label365:
          return paramBoolean;
        }
      }
    }
    this.m_ContentScenes = null;
    return true;
  }
  
  public boolean addToAlbum(long paramLong, int paramInt)
  {
    if (canAddToAlbum())
    {
      AlbumManager localAlbumManager = (AlbumManager)BaseApplication.current().findComponent(AlbumManager.class);
      if (localAlbumManager != null) {
        return localAlbumManager.addMediaToAlbum(paramLong, this);
      }
    }
    else
    {
      return false;
    }
    Log.e(TAG, "addToAlbum() - No AlbumManager");
    return false;
  }
  
  public boolean canAddToAlbum()
  {
    boolean bool = false;
    if (!isTemporary()) {
      bool = true;
    }
    return bool;
  }
  
  public boolean containsObject(ObjectType paramObjectType)
  {
    if (paramObjectType == null) {}
    while ((this.m_ContentObjectTypes == null) || (!this.m_ContentObjectTypes.contains(paramObjectType))) {
      return false;
    }
    return true;
  }
  
  public boolean containsScene(Scene paramScene)
  {
    if (paramScene == null) {}
    while ((this.m_ContentScenes == null) || (!this.m_ContentScenes.contains(paramScene))) {
      return false;
    }
    return true;
  }
  
  protected abstract Uri createContentUri(DbValues paramDbValues);
  
  public Handle delete(Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
      return ((MediaStoreMediaSource)getSource()).recycleMedia(this, paramDeletionCallback, paramInt);
    }
    return ((MediaStoreMediaSource)getSource()).deleteMedia(this, paramDeletionCallback, paramInt);
  }
  
  public long getAddedTime()
  {
    return this.m_AddedTime;
  }
  
  public Address getAddress()
  {
    return this.m_Address;
  }
  
  public MediaCacheKey getCacheKey()
  {
    return new CacheKey(this);
  }
  
  public int getContentObjectTypes(List<ObjectType> paramList, int paramInt)
  {
    paramList.clear();
    if (this.m_ContentObjectTypes == null) {
      return 0;
    }
    Iterator localIterator = this.m_ContentObjectTypes.iterator();
    while (localIterator.hasNext()) {
      paramList.add((ObjectType)localIterator.next());
    }
    return paramList.size();
  }
  
  public int getContentSceneCount()
  {
    if (this.m_ContentScenes != null) {
      return this.m_ContentScenes.size();
    }
    return 0;
  }
  
  public int getContentScenes(List<Scene> paramList, int paramInt)
  {
    paramList.clear();
    if (this.m_ContentScenes == null) {
      return 0;
    }
    Iterator localIterator = this.m_ContentScenes.iterator();
    while (localIterator.hasNext()) {
      paramList.add((Scene)localIterator.next());
    }
    return paramList.size();
  }
  
  public Uri getContentUri()
  {
    return this.m_ContentUri;
  }
  
  public String getDisplayName()
  {
    return this.m_DisplayName;
  }
  
  public String getFilePath()
  {
    return this.m_FilePath;
  }
  
  public long getFileSize()
  {
    return this.m_FileSize;
  }
  
  final String getGroupId()
  {
    return this.m_GroupId;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  public long getLastModifiedTime()
  {
    return this.m_LastModifiedTime;
  }
  
  public Location getLocation()
  {
    return this.m_Location;
  }
  
  public final long getMediaId()
  {
    return this.m_MediaId;
  }
  
  public String getMimeType()
  {
    return this.m_MimeType;
  }
  
  public final long getObjectDetectionFileTime()
  {
    return this.m_ObjectDetectionFileTime;
  }
  
  public final long getObjectDetectionVersion()
  {
    return this.m_ObjectDetectionVersion;
  }
  
  public final long getParentId()
  {
    return this.m_ParentId;
  }
  
  public Address getPreviousAddress()
  {
    return this.m_PrevAddress;
  }
  
  public int getPreviousContentObjectTypes(List<ObjectType> paramList, int paramInt)
  {
    paramList.clear();
    if (this.m_PrevContentObjectTypes == null) {
      return 0;
    }
    paramInt = this.m_PrevContentObjectTypes.length;
    for (;;)
    {
      paramInt -= 1;
      if (paramInt < 0) {
        break;
      }
      paramList.add(this.m_PrevContentObjectTypes[paramInt]);
    }
    return paramList.size();
  }
  
  public int getPreviousContentScenes(List<Scene> paramList, int paramInt)
  {
    paramList.clear();
    if (this.m_PrevContentScenes == null) {
      return 0;
    }
    paramInt = this.m_PrevContentScenes.length;
    for (;;)
    {
      paramInt -= 1;
      if (paramInt < 0) {
        break;
      }
      paramList.add(this.m_PrevContentScenes[paramInt]);
    }
    return paramList.size();
  }
  
  public String getPreviousFilePath()
  {
    return this.m_PrevFilePath;
  }
  
  public Location getPreviousLocation()
  {
    return this.m_PrevLocation;
  }
  
  public final long getPreviousParentId()
  {
    return this.m_PrevParentId;
  }
  
  public long getPreviousTakenTime()
  {
    return this.m_PrevTakenTime;
  }
  
  public final long getSceneDetectionFileTime()
  {
    return this.m_SceneDetectionFileTime;
  }
  
  public final long getSceneDetectionVersion()
  {
    return this.m_SceneDetectionVersion;
  }
  
  public long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public String getTitle()
  {
    return this.m_Title;
  }
  
  public boolean isAvailable()
  {
    boolean bool = false;
    if (!isReleased()) {
      bool = true;
    }
    return bool;
  }
  
  final boolean isBurstSubMedia()
  {
    return (this.m_InternalFlags & 0x100) != 0;
  }
  
  public boolean isCapturedByFrontCamera()
  {
    return (this.m_InternalFlags & 0x4) != 0;
  }
  
  public boolean isFavorite()
  {
    return (this.m_InternalFlags & 0x1) != 0;
  }
  
  public boolean isFavoriteSupported()
  {
    return (this.m_InternalFlags & 0x2) == 0;
  }
  
  public boolean isParentVisible()
  {
    return this.m_IsParentVisible;
  }
  
  public boolean isRaw()
  {
    return (this.m_InternalFlags & 0x16) != 0;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public final boolean isReleased()
  {
    return (this.m_InternalFlags & 0x40000000) != 0;
  }
  
  public final boolean isSubMedia()
  {
    return (this.m_InternalFlags & INTERNAL_FLAG_SUB_MEDIA) != 0;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  public boolean isVisible()
  {
    if ((this.m_InternalFlags & 0x8) != 0) {}
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
      if ((this.m_InternalFlags & 0x8) == 0) {}
    }
    else
    {
      return;
    }
    ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, FLAG_VISIBILITY_CHANGED);
  }
  
  final void onAddressObtained(Address paramAddress)
  {
    if (paramAddress == null) {}
    while (isReleased()) {
      return;
    }
    Object localObject = ((Locale)BaseApplication.current().get(BaseApplication.PROP_LOCALE)).toString();
    String str = paramAddress.getLocale().toString();
    if (((String)localObject).equals(str)) {
      if (this.m_Location != null) {
        break label119;
      }
    }
    for (;;)
    {
      this.m_PrevAddress = this.m_Address;
      this.m_Address = paramAddress;
      localObject = (MediaStoreMediaSource)getSource();
      ((MediaStoreMediaSource)localObject).updateAddress(this, paramAddress);
      ((MediaStoreMediaSource)localObject).notifyMediaUpdatedByItself(this, FLAG_ADDRESS_CHANGED);
      return;
      if (!Device.isHydrogenOS()) {}
      while (!((String)localObject).contains(str))
      {
        ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
        return;
      }
      break;
      label119:
      if (Math.abs(paramAddress.getLatitude() - this.m_Location.getLatitude()) > 1.0E-4D) {}
      for (int i = 1; (i != 0) || (Math.abs(paramAddress.getLongitude() - this.m_Location.getLongitude()) > 1.0E-4D); i = 0)
      {
        ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
        return;
      }
    }
  }
  
  void onLocaleChanged()
  {
    if (this.m_Location == null) {
      return;
    }
    if (this.m_Address == null) {}
    for (;;)
    {
      ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
      return;
      this.m_PrevAddress = this.m_Address;
      this.m_Address = null;
      ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, FLAG_ADDRESS_CHANGED);
    }
  }
  
  void onNetworkConnectionStateChanged(boolean paramBoolean)
  {
    if (this.m_Location == null) {}
    while (this.m_Address != null) {
      return;
    }
    ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
  }
  
  protected int onUpdate(DbValues paramDbValues, FileInfo paramFileInfo, boolean paramBoolean)
  {
    int j = 0;
    int i = 0;
    GalleryDatabase.ExtraMediaInfo localExtraMediaInfo = paramDbValues.extraInfo;
    label29:
    label32:
    int k;
    label44:
    label51:
    label56:
    label59:
    label71:
    label99:
    label111:
    Object localObject;
    label119:
    label131:
    label197:
    label211:
    label227:
    long l1;
    if (paramFileInfo == null)
    {
      this.m_AddedTime = paramDbValues.addedTime;
      if (localExtraMediaInfo != null) {
        break label582;
      }
      j = 0;
      if ((this.m_InternalFlags & 0x4) != 0) {
        break label600;
      }
      k = 0;
      if (k != j) {
        break label606;
      }
      if (localExtraMediaInfo != null) {
        break label638;
      }
      j = 0;
      if ((this.m_InternalFlags & 0x1) != 0) {
        break label658;
      }
      k = 0;
      if (k != j) {
        break label664;
      }
      if ((paramDbValues.latitude == 0.0D) || (paramDbValues.longitude == 0.0D)) {
        break label704;
      }
      j = 1;
      if (j != 0) {
        break label710;
      }
      if (this.m_Location != null) {
        break label833;
      }
      if (localExtraMediaInfo != null) {
        break label857;
      }
      localObject = null;
      if (localObject != null) {
        break label1031;
      }
      if (this.m_Location != null) {
        break label1197;
      }
      this.m_MimeType = paramDbValues.mimeType;
      j = i;
      if (this.m_ParentId != paramDbValues.parentId)
      {
        this.m_PrevParentId = this.m_ParentId;
        this.m_ParentId = paramDbValues.parentId;
        j = i | FLAG_PARENT_ID_CHANGED;
      }
      if (!TextUtils.equals(this.m_DisplayName, paramDbValues.displayName)) {
        break label1211;
      }
      i = j;
      if (!TextUtils.equals(this.m_Title, paramDbValues.title)) {
        break label1230;
      }
      long l2 = paramDbValues.takenTime;
      if (l2 <= 0L) {
        break label1249;
      }
      j = 1;
      l1 = l2;
      if (j == 0)
      {
        if (paramFileInfo != null) {
          break label1255;
        }
        l1 = l2;
      }
      label244:
      l1 /= 1000L;
      if (paramBoolean) {
        break label1264;
      }
      label256:
      if (localExtraMediaInfo != null) {
        break label1277;
      }
      label261:
      j = 0;
      label264:
      if ((this.m_InternalFlags & 0x8) != 0) {
        break label1297;
      }
      k = 0;
      label277:
      if (k != j) {
        break label1303;
      }
      if (localExtraMediaInfo != null) {
        break label1344;
      }
      this.m_ObjectDetectionVersion = 0L;
      this.m_ObjectDetectionFileTime = 0L;
      if (updateObjectDetectionResult(null, paramBoolean)) {
        break label1390;
      }
      j = i;
      label312:
      if (localExtraMediaInfo != null) {
        break label1401;
      }
      this.m_SceneDetectionVersion = 0L;
      this.m_SceneDetectionFileTime = 0L;
      if (updateSceneDetectionResult(null, paramBoolean)) {
        break label1439;
      }
    }
    label544:
    label582:
    label600:
    label606:
    label638:
    label658:
    label664:
    label704:
    label710:
    label831:
    label833:
    label857:
    label1031:
    label1038:
    label1114:
    label1189:
    label1191:
    label1195:
    label1197:
    label1211:
    label1230:
    label1249:
    label1255:
    label1264:
    label1277:
    label1297:
    label1303:
    label1344:
    label1390:
    label1401:
    do
    {
      return j;
      localObject = this.m_GroupId;
      if (TextUtils.equals(this.m_FilePath, paramDbValues.data)) {
        i = j;
      }
      for (;;)
      {
        j = i;
        if (this.m_FileSize != paramFileInfo.size)
        {
          this.m_FileSize = paramFileInfo.size;
          j = i | FLAG_FILE_SIZE_CHANGED;
        }
        i = j;
        if (this.m_LastModifiedTime == paramFileInfo.lastModifiedTime) {
          break;
        }
        this.m_LastModifiedTime = paramFileInfo.lastModifiedTime;
        i = j | FLAG_LAST_MODIFIED_TIME_CHANGED;
        break;
        this.m_PrevFilePath = this.m_FilePath;
        this.m_FilePath = paramDbValues.data;
        j = FLAG_FILE_PATH_CHANGED | 0x0;
        this.m_GroupId = BurstMediaStoreMedia.getId(this.m_FilePath);
        if (this.m_GroupId == null) {}
        for (;;)
        {
          i = j;
          if (TextUtils.equals(this.m_GroupId, (CharSequence)localObject)) {
            break;
          }
          i = FLAG_GROUP_CHANGED | j;
          if (this.m_GroupId != null) {
            break label544;
          }
          this.m_InternalFlags &= 0xFEFF;
          break;
          if ((localExtraMediaInfo != null) && ((localExtraMediaInfo.oneplusFlags & 0x20000) == 0L)) {
            this.m_GroupId = null;
          }
        }
        this.m_InternalFlags |= 0x100;
        if (localExtraMediaInfo == null) {
          ((MediaStoreMediaSource)getSource()).updateOnePlusFlags(this, 131072, 0);
        }
      }
      if ((localExtraMediaInfo.oneplusFlags & 1L) == 0L) {
        break label29;
      }
      j = 1;
      break label32;
      k = 1;
      break label44;
      if (j == 0)
      {
        this.m_InternalFlags &= 0xFFFFFFFB;
        break label51;
      }
      this.m_InternalFlags |= 0x4;
      break label51;
      if ((localExtraMediaInfo.oneplusFlags & 0x10) == 0L) {
        break label56;
      }
      j = 1;
      break label59;
      k = 1;
      break label71;
      if (j == 0) {}
      for (this.m_InternalFlags &= 0xFFFFFFFE;; this.m_InternalFlags |= 0x1)
      {
        i |= FLAG_FAVORITE_CHANGED;
        break;
      }
      j = 0;
      break label99;
      if (this.m_Location == null) {}
      for (;;)
      {
        this.m_PrevLocation = this.m_Location;
        this.m_Location = new Location("");
        this.m_Location.setLatitude(paramDbValues.latitude);
        this.m_Location.setLongitude(paramDbValues.longitude);
        i |= FLAG_LOCATION_CHANGED;
        break;
        if (Math.abs(this.m_Location.getLatitude() - paramDbValues.latitude) > 1.0E-4D) {}
        for (j = 1;; j = 0)
        {
          if ((j != 0) || (Math.abs(this.m_Location.getLongitude() - paramDbValues.longitude) > 1.0E-4D)) {
            break label831;
          }
          break;
        }
      }
      this.m_PrevLocation = this.m_Location;
      this.m_Location = null;
      i |= FLAG_LOCATION_CHANGED;
      break label111;
      if (Double.isNaN(localExtraMediaInfo.addressLatitude))
      {
        localObject = null;
        break label119;
      }
      if (Double.isNaN(localExtraMediaInfo.addressLongitude))
      {
        localObject = null;
        break label119;
      }
      if (localExtraMediaInfo.addressLocale == null)
      {
        localObject = null;
        break label119;
      }
      localObject = new Address(new Locale(localExtraMediaInfo.addressLocale));
      ((Address)localObject).setLatitude(localExtraMediaInfo.addressLatitude);
      ((Address)localObject).setLongitude(localExtraMediaInfo.addressLongitude);
      ((Address)localObject).setCountryName(localExtraMediaInfo.addressCountry);
      ((Address)localObject).setAdminArea(localExtraMediaInfo.addressAdminArea);
      ((Address)localObject).setSubAdminArea(localExtraMediaInfo.addressSubAdminArea);
      ((Address)localObject).setLocality(localExtraMediaInfo.addressLocality);
      ((Address)localObject).setSubLocality(localExtraMediaInfo.addressSubLocality);
      ((Address)localObject).setFeatureName(localExtraMediaInfo.addressFeature);
      ((Address)localObject).setAddressLine(0, localExtraMediaInfo.addressAddressLine0);
      ((Address)localObject).setAddressLine(1, localExtraMediaInfo.addressAddressLine1);
      break label119;
      if (this.m_Location == null) {}
      for (;;)
      {
        this.m_PrevAddress = this.m_Address;
        this.m_Address = null;
        j = i | FLAG_ADDRESS_CHANGED;
        i = j;
        if (this.m_Location == null) {
          break;
        }
        ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
        i = j;
        break;
        if (Math.abs(((Address)localObject).getLatitude() - this.m_Location.getLatitude()) > 1.0E-4D)
        {
          j = 1;
          if (j != 0) {
            break label1189;
          }
          if (Math.abs(((Address)localObject).getLongitude() - this.m_Location.getLongitude()) <= 1.0E-4D) {
            break label1191;
          }
        }
        for (j = 1;; j = 0)
        {
          if ((j != 0) || (!((Locale)BaseApplication.current().get(BaseApplication.PROP_LOCALE)).toString().equals(localExtraMediaInfo.addressLocale))) {
            break label1195;
          }
          this.m_Address = ((Address)localObject);
          break;
          j = 0;
          break label1114;
          break label1038;
        }
      }
      ((MediaStoreMediaSource)getSource()).scheduleAddressObtaining(this);
      break label131;
      this.m_DisplayName = paramDbValues.displayName;
      i = j | FLAG_DISPLAY_NAME_CHANGED;
      break label197;
      this.m_Title = paramDbValues.title;
      i |= FLAG_TITLE_CHANGED;
      break label211;
      j = 0;
      break label227;
      l1 = paramFileInfo.takenTime;
      break label244;
      this.m_TakenTime = (l1 * 1000L);
      break label256;
      if ((localExtraMediaInfo.oneplusFlags & 0x20) == 0L) {
        break label261;
      }
      j = 1;
      break label264;
      k = 1;
      break label277;
      if (j == 0) {}
      for (this.m_InternalFlags &= 0xFFFFFFF7;; this.m_InternalFlags |= 0x8)
      {
        i = FLAG_VISIBILITY_CHANGED | i;
        break;
      }
      this.m_ObjectDetectionVersion = localExtraMediaInfo.objectDetectionVersion;
      this.m_ObjectDetectionFileTime = localExtraMediaInfo.objectDetectionFileTime;
      j = i;
      if (!updateObjectDetectionResult(localExtraMediaInfo.objectDetectionResult, paramBoolean)) {
        break label312;
      }
      j = i | FLAG_CONTENT_OBJECT_TYPES_CHANGED;
      break label312;
      j = i | FLAG_CONTENT_OBJECT_TYPES_CHANGED;
      break label312;
      this.m_SceneDetectionVersion = localExtraMediaInfo.sceneDetectionVersion;
      this.m_SceneDetectionFileTime = localExtraMediaInfo.sceneDetectionFileTime;
    } while (!updateSceneDetectionResult(localExtraMediaInfo.sceneDetectionResult, paramBoolean));
    return j | FLAG_CONTENT_SCENES_CHANGED;
    label1439:
    return j | FLAG_CONTENT_SCENES_CHANGED;
  }
  
  public InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException
  {
    if (this.m_ContentUri == null) {}
    while (this.m_FilePath == null)
    {
      throw new RuntimeException("No source to open input stream");
      try
      {
        paramRef = BaseApplication.current().getContentResolver().openInputStream(this.m_ContentUri);
        return paramRef;
      }
      catch (Throwable paramRef)
      {
        Log.e(TAG, "openInputStream() - fail", paramRef);
      }
    }
    return new FileInputStream(this.m_FilePath);
  }
  
  final void release()
  {
    this.m_InternalFlags |= 0x40000000;
  }
  
  public boolean removeFromAlbum(long paramLong, int paramInt)
  {
    if (canAddToAlbum())
    {
      AlbumManager localAlbumManager = (AlbumManager)BaseApplication.current().findComponent(AlbumManager.class);
      if (localAlbumManager != null) {
        return localAlbumManager.removeMediaFromAlbum(paramLong, this);
      }
    }
    else
    {
      return false;
    }
    Log.e(TAG, "removeFromAlbum() - No AlbumManager");
    return false;
  }
  
  public boolean setFavorite(boolean paramBoolean)
  {
    verifyAccess();
    MediaStoreMediaSource localMediaStoreMediaSource;
    if (isFavoriteSupported())
    {
      if (isFavorite() == paramBoolean) {
        break label62;
      }
      localMediaStoreMediaSource = (MediaStoreMediaSource)getSource();
      if (paramBoolean) {
        break label64;
      }
      this.m_InternalFlags &= 0xFFFFFFFE;
      localMediaStoreMediaSource.updateOnePlusFlags(this, 0, 16);
    }
    for (;;)
    {
      localMediaStoreMediaSource.notifyMediaUpdatedByItself(this, FLAG_FAVORITE_CHANGED);
      return true;
      return false;
      label62:
      return true;
      label64:
      this.m_InternalFlags |= 0x1;
      localMediaStoreMediaSource.updateOnePlusFlags(this, 16, 0);
    }
  }
  
  public boolean setVisible(boolean paramBoolean)
  {
    verifyAccess();
    MediaStoreMediaSource localMediaStoreMediaSource;
    if (isVisibilityChangeSupported())
    {
      if (isVisible() == paramBoolean) {
        break label62;
      }
      localMediaStoreMediaSource = (MediaStoreMediaSource)getSource();
      if (!paramBoolean) {
        break label64;
      }
      this.m_InternalFlags &= 0xFFFFFFF7;
      localMediaStoreMediaSource.updateOnePlusFlags(this, 0, 32);
    }
    for (;;)
    {
      localMediaStoreMediaSource.notifyMediaUpdatedByItself(this, FLAG_VISIBILITY_CHANGED);
      return true;
      return false;
      label62:
      return true;
      label64:
      this.m_InternalFlags |= 0x8;
      localMediaStoreMediaSource.updateOnePlusFlags(this, 32, 0);
    }
  }
  
  public String toString()
  {
    return "[" + this.m_Id + ", " + this.m_FilePath + "]";
  }
  
  final int update(DbValues paramDbValues, FileInfo paramFileInfo)
  {
    return onUpdate(paramDbValues, paramFileInfo, false);
  }
  
  public boolean updateObjectDetectionResult(long paramLong1, long paramLong2, List<GalleryDatabase.ObjectDetectionResult> paramList)
  {
    verifyAccess();
    MediaStoreMediaSource localMediaStoreMediaSource;
    if (!isReleased())
    {
      localMediaStoreMediaSource = (MediaStoreMediaSource)getSource();
      localMediaStoreMediaSource.updateObjectDetectionResult(this, paramLong1, paramLong2, paramList);
      this.m_ObjectDetectionVersion = paramLong1;
      this.m_ObjectDetectionFileTime = paramLong2;
      if (!updateObjectDetectionResult(paramList, false)) {
        return true;
      }
    }
    else
    {
      return false;
    }
    if (!isSubMedia()) {}
    for (int i = 0;; i = FLAG_SUB_MEDIA)
    {
      localMediaStoreMediaSource.notifyMediaUpdatedByItself(this, i | FLAG_CONTENT_OBJECT_TYPES_CHANGED);
      break;
    }
  }
  
  public boolean updateSceneDetectionResult(long paramLong1, long paramLong2, List<GalleryDatabase.SceneDetectionResult> paramList)
  {
    verifyAccess();
    MediaStoreMediaSource localMediaStoreMediaSource;
    if (!isReleased())
    {
      localMediaStoreMediaSource = (MediaStoreMediaSource)getSource();
      localMediaStoreMediaSource.updateSceneDetectionResult(this, paramLong1, paramLong2, paramList);
      this.m_SceneDetectionVersion = paramLong1;
      this.m_SceneDetectionFileTime = paramLong2;
      if (!updateSceneDetectionResult(paramList, false)) {
        return true;
      }
    }
    else
    {
      return false;
    }
    if (!isSubMedia()) {}
    for (int i = 0;; i = FLAG_SUB_MEDIA)
    {
      localMediaStoreMediaSource.notifyMediaUpdatedByItself(this, i | FLAG_CONTENT_SCENES_CHANGED);
      break;
    }
  }
  
  private static final class CacheKey
    implements MediaCacheKey, Serializable
  {
    private static final long serialVersionUID = -7760636145522769432L;
    public final String filePath;
    public final long fileSize;
    public final long lastModifiedTime;
    
    public CacheKey(MediaStoreMedia paramMediaStoreMedia)
    {
      this.filePath = paramMediaStoreMedia.getFilePath();
      this.fileSize = paramMediaStoreMedia.getFileSize();
      this.lastModifiedTime = paramMediaStoreMedia.getLastModifiedTime();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof CacheKey)) {
        return false;
      }
      paramObject = (CacheKey)paramObject;
      return (this.fileSize == ((CacheKey)paramObject).fileSize) && (this.lastModifiedTime == ((CacheKey)paramObject).lastModifiedTime) && (TextUtils.equals(this.filePath, ((CacheKey)paramObject).filePath));
    }
    
    public Uri getContentUri()
    {
      return null;
    }
    
    public String getFilePath()
    {
      return this.filePath;
    }
    
    public int hashCode()
    {
      return (int)this.fileSize;
    }
    
    public boolean isExpired()
    {
      try
      {
        if (this.filePath != null)
        {
          File localFile = new File(this.filePath);
          if (this.fileSize == localFile.length())
          {
            long l1 = this.lastModifiedTime;
            long l2 = localFile.lastModified();
            if (l1 == l2) {
              return false;
            }
          }
        }
        else
        {
          return true;
        }
        return true;
      }
      catch (Throwable localThrowable) {}
      return true;
    }
  }
  
  public static class DbValues
  {
    public volatile long addedTime;
    public volatile String data;
    public volatile String displayName;
    public volatile long duration;
    public volatile GalleryDatabase.ExtraMediaInfo extraInfo;
    public volatile int height;
    public volatile long id;
    public volatile double latitude;
    public volatile double longitude;
    public volatile int mediaType;
    public volatile String mimeType;
    public volatile int orientation;
    public volatile long parentId;
    public volatile long takenTime;
    public volatile String title;
    public volatile int width;
    
    public static DbValues read(Cursor paramCursor)
    {
      DbValues localDbValues = new DbValues();
      localDbValues.id = paramCursor.getLong(0);
      localDbValues.mediaType = paramCursor.getInt(1);
      localDbValues.data = paramCursor.getString(2);
      localDbValues.mimeType = paramCursor.getString(4);
      localDbValues.takenTime = paramCursor.getLong(6);
      localDbValues.addedTime = (paramCursor.getLong(7) * 1000L);
      localDbValues.width = paramCursor.getInt(8);
      localDbValues.height = paramCursor.getInt(9);
      localDbValues.latitude = paramCursor.getDouble(10);
      localDbValues.longitude = paramCursor.getDouble(11);
      localDbValues.orientation = paramCursor.getInt(12);
      localDbValues.duration = paramCursor.getLong(13);
      localDbValues.parentId = paramCursor.getLong(14);
      localDbValues.displayName = paramCursor.getString(15);
      localDbValues.title = paramCursor.getString(16);
      return localDbValues;
    }
  }
  
  public static class FileInfo
  {
    public volatile long lastModifiedTime;
    public volatile long size;
    public volatile long takenTime;
    
    public static FileInfo read(String paramString)
    {
      try
      {
        File localFile = new File(paramString);
        FileInfo localFileInfo = new FileInfo();
        localFileInfo.lastModifiedTime = localFile.lastModified();
        localFileInfo.size = localFile.length();
        return localFileInfo;
      }
      catch (Throwable localThrowable)
      {
        Log.e(MediaStoreMedia.TAG, "read() - Fail to read file info of " + paramString, localThrowable);
      }
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaStoreMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */