package com.oneplus.gallery2.media;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.LongSparseArray;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.database.CursorUtils;
import com.oneplus.gallery2.GalleryLib;
import com.oneplus.gallery2.MediaContentThread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class GalleryDatabase
{
  private static final String COLUMN_ADDRESS_ADDRESS_LINE_0 = "address_address_line_0";
  private static final String COLUMN_ADDRESS_ADDRESS_LINE_1 = "address_address_line_1";
  private static final String COLUMN_ADDRESS_ADMIN_AREA = "address_admin_area";
  private static final String COLUMN_ADDRESS_COUNTRY = "address_country";
  private static final String COLUMN_ADDRESS_FEATURE = "address_feature";
  private static final String COLUMN_ADDRESS_LATITUDE = "address_latitude";
  private static final String COLUMN_ADDRESS_LOCALE = "address_locale";
  private static final String COLUMN_ADDRESS_LOCALITY = "address_locality";
  private static final String COLUMN_ADDRESS_LONGITUDE = "address_longitude";
  private static final String COLUMN_ADDRESS_SUB_ADMIN_AREA = "address_sub_admin_area";
  private static final String COLUMN_ADDRESS_SUB_LOCALITY = "address_sub_locality";
  private static final String COLUMN_ALBUM_ID = "album_id";
  private static final String COLUMN_DATE_ADDED = "date_added";
  private static final String COLUMN_DATE_MEDIA_ADDED = "date_media_added";
  private static final String COLUMN_DATE_MODIFIED = "date_modified";
  private static final String COLUMN_DISPLAY_NAME = "_display_name";
  private static final String COLUMN_ID = "_id";
  public static final String COLUMN_MEDIA_ID = "media_id";
  private static final String COLUMN_OBJECT_DETECTION_FILE_TIME = "object_detection_file_time";
  private static final String COLUMN_OBJECT_DETECTION_RESULT = "object_detection_result";
  private static final String COLUMN_OBJECT_DETECTION_VERSION = "object_detection_version";
  public static final String COLUMN_ONEPLUS_FLAGS = "oneplus_flags";
  private static final String COLUMN_SCENE_DETECTION_FILE_TIME = "scene_detection_file_time";
  private static final String COLUMN_SCENE_DETECTION_RESULT = "scene_detection_result";
  private static final String COLUMN_SCENE_DETECTION_VERSION = "scene_detection_version";
  private static final String CONTENT_URI_PREFIX_EXTRA_MEDIA_INFO = "content://oneplus.gallery/media/";
  private static final String DB_NAME = "gallery.db";
  private static final int DB_VERSION = 13;
  private static final String GALLERY_CONTENT_PROVIDER_URI_PREFIX = "content://oneplus.gallery/";
  private static final String INDEX_ALBUM_ID = "album_id_index";
  private static final String INDEX_MEDIA_ID = "media_id_index";
  private static final boolean IS_SERVER_MODE;
  private static final boolean READ_ONLY_MODE = false;
  private static final String SQL_ALTER_ALBUM_TABLE_ADD_COLUMN_ONEPLUS_FLAG = "ALTER TABLE album ADD oneplus_flags INTEGER;";
  private static final String SQL_ALTER_DIRECTORY_TABLE_ADD_COLUMN_ONEPLUS_FLAG = "ALTER TABLE directory ADD oneplus_flags INTEGER;";
  private static final String SQL_ALTER_MEDIA_TABLE_ADD_COLUMN_ONEPLUS_FLAG = "ALTER TABLE media ADD oneplus_flags INTEGER;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_ADDRESS_LINE_0 = "ALTER TABLE media ADD address_address_line_0 TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_ADDRESS_LINE_1 = "ALTER TABLE media ADD address_address_line_1 TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_ADMIN_AREA = "ALTER TABLE media ADD address_admin_area TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_COUNTRY = "ALTER TABLE media ADD address_country TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_FEATURE = "ALTER TABLE media ADD address_feature TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_LATITUDE = "ALTER TABLE media ADD address_latitude REAL;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_LOCALE = "ALTER TABLE media ADD address_locale TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_LOCALITY = "ALTER TABLE media ADD address_locality TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_LONGITUDE = "ALTER TABLE media ADD address_longitude REAL;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_SUB_ADMIN_AREA = "ALTER TABLE media ADD address_sub_admin_area TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_ADDRESS_SUB_LOCALITY = "ALTER TABLE media ADD address_sub_locality TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_DATE_ADDED = "ALTER TABLE album ADD date_added INTEGER;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_DATE_MEDIA_ADDED = "ALTER TABLE album ADD date_media_added INTEGER;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_DATE_MODIFIED = "ALTER TABLE album ADD date_modified INTEGER;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_OBJECT_DETECTION_FILE_TIME = "ALTER TABLE media ADD object_detection_file_time INTEGER DEFAULT 0;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_OBJECT_DETECTION_RESULT = "ALTER TABLE media ADD object_detection_result TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_OBJECT_DETECTION_VERSION = "ALTER TABLE media ADD object_detection_version INTEGER DEFAULT 0;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_SCENE_DETECTION_FILE_TIME = "ALTER TABLE media ADD scene_detection_file_time INTEGER DEFAULT 0;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_SCENE_DETECTION_RESULT = "ALTER TABLE media ADD scene_detection_result TEXT;";
  private static final String SQL_ALTER_TABLE_ADD_COLUMN_SCENE_DETECTION_VERSION = "ALTER TABLE media ADD scene_detection_version INTEGER DEFAULT 0;";
  private static final String SQL_CREATE_INDEX_ALBUM_ID = "CREATE INDEX album_id_index ON album_media(album_id);";
  private static final String SQL_CREATE_INDEX_MEDIA_ID = "CREATE INDEX media_id_index ON media(media_id);";
  private static final String SQL_CREATE_TABLE_ALBUM = "CREATE TABLE album (_id INTEGER PRIMARY KEY,_display_name TEXT,date_added INTEGER,date_modified INTEGER,date_media_added INTEGER,oneplus_flags INTEGER);";
  private static final String SQL_CREATE_TABLE_ALBUM_MEDIA = "CREATE TABLE album_media (album_id INTEGER,media_id INTEGER);";
  private static final String SQL_CREATE_TABLE_DIRECTORY = "CREATE TABLE directory (_id INTEGER PRIMARY KEY,date_media_added INTEGER,oneplus_flags INTEGER);";
  private static final String SQL_CREATE_TABLE_MEDIA = "CREATE TABLE media (media_id INTEGER,oneplus_flags INTEGER,address_locale TEXT,address_latitude REAL,address_longitude REAL,address_country TEXT,address_admin_area TEXT,address_sub_admin_area TEXT,address_locality TEXT,address_sub_locality TEXT,address_feature TEXT,address_address_line_0 TEXT,address_address_line_1 TEXT,object_detection_version INTEGER DEFAULT 0,object_detection_file_time INTEGER DEFAULT 0,object_detection_result TEXT,scene_detection_version INTEGER DEFAULT 0,scene_detection_file_time INTEGER DEFAULT 0,scene_detection_result TEXT);";
  private static final String TABLE_ALBUM = "album";
  private static final String TABLE_ALBUM_MEDIA = "album_media";
  private static final String[] TABLE_ALBUM_MEDIA_COLUMNS;
  private static final String TABLE_DIRECTORY = "directory";
  private static final String TABLE_MEDIA = "media";
  private static final String[] TABLE_MEDIA_COLUMNS;
  private static final String TAG;
  private static final List<CallbackHandle<ChangeCallback<AlbumInfo>>> m_AlbumInfoChangeCBHandles;
  private static final Object m_AlbumInfoLock;
  private static volatile LongSparseArray<AlbumInfo> m_AlbumInfoTable;
  private static final List<CallbackHandle<ChangeCallback<AlbumMediaRelation>>> m_AlbumMediaRelationChangeCBHandles;
  private static final Object m_AlbumMediaRelationLock;
  private static volatile LongSparseArray<Set<AlbumMediaRelation>> m_AlbumMediaRelationTable;
  private static volatile SQLiteDatabase m_Database;
  private static final Object m_DatabaseLock;
  private static final List<CallbackHandle<ChangeCallback<ExtraDirectoryInfo>>> m_ExtraDirectoryInfoChangeCBHandles;
  private static final Object m_ExtraDirectoryInfoLock;
  private static volatile LongSparseArray<ExtraDirectoryInfo> m_ExtraDirectoryInfoTable;
  private static final List<CallbackHandle<ChangeCallback<ExtraMediaInfo>>> m_ExtraMediaInfoChangeCBHandles;
  private static final Object m_ExtraMediaInfoLock;
  private static volatile LongSparseArray<ExtraMediaInfo> m_ExtraMediaInfoTable;
  
  static
  {
    boolean bool = false;
    TABLE_ALBUM_MEDIA_COLUMNS = new String[] { "album_id", "media_id" };
    TABLE_MEDIA_COLUMNS = new String[] { "media_id", "oneplus_flags", "address_locale", "address_latitude", "address_longitude", "address_country", "address_admin_area", "address_sub_admin_area", "address_locality", "address_sub_locality", "address_feature", "address_address_line_0", "address_address_line_1", "object_detection_version", "object_detection_file_time", "object_detection_result", "scene_detection_version", "scene_detection_file_time", "scene_detection_result" };
    m_AlbumInfoLock = new Object();
    m_AlbumInfoChangeCBHandles = new ArrayList();
    m_AlbumMediaRelationLock = new Object();
    m_AlbumMediaRelationChangeCBHandles = new ArrayList();
    m_DatabaseLock = new Object();
    m_ExtraDirectoryInfoChangeCBHandles = new ArrayList();
    m_ExtraDirectoryInfoLock = new Object();
    m_ExtraMediaInfoChangeCBHandles = new ArrayList();
    m_ExtraMediaInfoLock = new Object();
    BaseApplication localBaseApplication = BaseApplication.current();
    Object localObject = GalleryDatabase.class.getSimpleName();
    if (!GalleryLib.isClient()) {
      bool = true;
    }
    IS_SERVER_MODE = bool;
    if (!IS_SERVER_MODE) {
      Log.d((String)localObject, "Access database as client");
    }
    for (;;)
    {
      TAG = (String)localObject;
      if (!IS_SERVER_MODE) {
        break;
      }
      return;
      localObject = localObject + "(Server)";
      Log.d((String)localObject, "Access database as server");
    }
    localObject = new ContentObserver(localBaseApplication.getHandler())
    {
      public void onChange(boolean paramAnonymousBoolean, final Uri paramAnonymousUri)
      {
        if (HandlerUtils.post(MediaContentThread.current(), new Runnable()
        {
          public void run()
          {
            GalleryDatabase.onGalleryContentChanged(paramAnonymousUri);
          }
        })) {
          return;
        }
        GalleryDatabase.onGalleryContentChanged(paramAnonymousUri);
      }
    };
    localBaseApplication.getContentResolver().registerContentObserver(Uri.parse("content://oneplus.gallery/media"), true, (ContentObserver)localObject);
  }
  
  public static AlbumInfo addAlbumInfo(String paramString)
  {
    if (setupAlbumInfoTable()) {
      synchronized (m_AlbumInfoLock)
      {
        AlbumInfo localAlbumInfo = new AlbumInfo();
        ContentValues localContentValues = new ContentValues(5);
        localAlbumInfo.name = paramString;
        localAlbumInfo.creationTime = System.currentTimeMillis();
        localAlbumInfo.lastModifiedTime = localAlbumInfo.creationTime;
        localAlbumInfo.lastMediaAddedTime = localAlbumInfo.creationTime;
        localContentValues.put("_display_name", localAlbumInfo.name);
        localContentValues.put("date_added", Long.valueOf(localAlbumInfo.creationTime));
        localContentValues.put("date_modified", Long.valueOf(localAlbumInfo.lastModifiedTime));
        localContentValues.put("date_media_added", Long.valueOf(localAlbumInfo.lastMediaAddedTime));
        localContentValues.put("oneplus_flags", Long.valueOf(localAlbumInfo.oneplusFlags));
        localAlbumInfo.albumId = insert("album", localContentValues);
        if (localAlbumInfo.albumId == -1L) {
          return null;
        }
        m_AlbumInfoTable.put(localAlbumInfo.albumId, localAlbumInfo);
        paramString = localAlbumInfo.clone();
        notifyAdded(m_AlbumInfoChangeCBHandles, paramString);
        return paramString;
      }
    }
    return null;
  }
  
  public static boolean addAlbumMediaRelation(AlbumMediaRelation arg0)
  {
    if (??? != null)
    {
      if (setupAlbumMediaRelationTable())
      {
        AlbumMediaRelation localAlbumMediaRelation = ???.clone();
        long l = localAlbumMediaRelation.albumId;
        Object localObject2;
        synchronized (m_AlbumMediaRelationLock)
        {
          localObject2 = (Set)m_AlbumMediaRelationTable.get(l);
          if (localObject2 == null) {}
          while (((Set)localObject2).add(localAlbumMediaRelation))
          {
            ContentValues localContentValues = new ContentValues(2);
            localContentValues.put("album_id", Long.valueOf(l));
            localContentValues.put("media_id", Long.valueOf(localAlbumMediaRelation.mediaId));
            if (insert("album_media", localContentValues) != -1L) {
              break label133;
            }
            if (localObject2 != null) {
              break;
            }
            return false;
          }
          return true;
          ((Set)localObject2).remove(localAlbumMediaRelation);
        }
        label133:
        if (localObject2 != null) {}
        for (;;)
        {
          notifyAdded(m_AlbumMediaRelationChangeCBHandles, ((AlbumMediaRelation)localObject1).clone());
          return true;
          localObject2 = new HashSet();
          ((Set)localObject2).add(localObject1);
          m_AlbumMediaRelationTable.put(l, localObject2);
        }
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static Handle addExtraDirectoryInfoChangeCallback(ChangeCallback<ExtraDirectoryInfo> paramChangeCallback)
  {
    if (paramChangeCallback != null) {
      synchronized (m_ExtraDirectoryInfoLock)
      {
        paramChangeCallback = new CallbackHandle("ExtraDirectoryInfoChangeCallback", paramChangeCallback, null)
        {
          protected void onClose(int paramAnonymousInt)
          {
            synchronized (GalleryDatabase.m_ExtraDirectoryInfoLock)
            {
              GalleryDatabase.m_ExtraDirectoryInfoChangeCBHandles.remove(this);
              return;
            }
          }
        };
        m_ExtraDirectoryInfoChangeCBHandles.add(paramChangeCallback);
        return paramChangeCallback;
      }
    }
    return null;
  }
  
  public static boolean addExtraMediaInfo(ExtraMediaInfo paramExtraMediaInfo)
  {
    if ((paramExtraMediaInfo == null) || (setupExtraMediaInfoTable())) {}
    synchronized (m_ExtraMediaInfoLock)
    {
      ExtraMediaInfo localExtraMediaInfo = paramExtraMediaInfo.clone();
      if (m_ExtraMediaInfoTable.get(localExtraMediaInfo.mediaId) == null)
      {
        ContentValues localContentValues = new ContentValues();
        localExtraMediaInfo.toContentValues(localContentValues);
        if (insert("media", localContentValues) != -1L)
        {
          m_ExtraMediaInfoTable.put(localExtraMediaInfo.mediaId, localExtraMediaInfo);
          notifyAdded(m_ExtraMediaInfoChangeCBHandles, paramExtraMediaInfo);
          return true;
          Log.e(TAG, "addExtraMediaInfo() - No media info to add");
          return false;
          return false;
        }
      }
      else
      {
        return false;
      }
      Log.e(TAG, "addExtraMediaInfo() - Fail to insert");
      return false;
    }
  }
  
  public static Handle addExtraMediaInfoChangeCallback(ChangeCallback<ExtraMediaInfo> paramChangeCallback)
  {
    if (paramChangeCallback != null) {
      synchronized (m_ExtraMediaInfoLock)
      {
        paramChangeCallback = new CallbackHandle("ExtraMediaInfoChangeCallback", paramChangeCallback, null)
        {
          protected void onClose(int paramAnonymousInt)
          {
            synchronized (GalleryDatabase.m_ExtraMediaInfoLock)
            {
              GalleryDatabase.m_ExtraMediaInfoChangeCBHandles.remove(this);
              return;
            }
          }
        };
        m_ExtraMediaInfoChangeCBHandles.add(paramChangeCallback);
        return paramChangeCallback;
      }
    }
    return null;
  }
  
  public static Uri createExtraMediaInfoUri(long paramLong)
  {
    return Uri.parse("content://oneplus.gallery/media/" + paramLong);
  }
  
  private static boolean delete(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    boolean bool = true;
    try
    {
      Uri localUri;
      if (!IS_SERVER_MODE)
      {
        localUri = Uri.parse("content://oneplus.gallery/" + paramString1);
        paramString1 = BaseApplication.current().getContentResolver().acquireUnstableContentProviderClient(localUri);
        if (paramString1 == null) {
          break label101;
        }
      }
      try
      {
        int i = paramString1.delete(localUri, paramString2, paramArrayOfString);
        if (i <= 0) {
          bool = false;
        }
        return bool;
      }
      finally
      {
        label101:
        paramString1.release();
      }
      if (openDatabase())
      {
        if (m_Database.delete(paramString1, paramString2, paramArrayOfString) <= 0) {
          return false;
        }
      }
      else
      {
        Log.e(TAG, "delete() - Fail to open database");
        return false;
        Log.w(TAG, "delete() - No gallery content provider");
        return false;
      }
    }
    catch (Throwable paramString1)
    {
      Log.e(TAG, "delete() - Fail to delete", paramString1);
      return false;
    }
    return true;
  }
  
  public static boolean deleteAlbumInfo(long paramLong)
  {
    if (setupAlbumInfoTable()) {
      synchronized (m_AlbumInfoLock)
      {
        AlbumInfo localAlbumInfo = (AlbumInfo)m_AlbumInfoTable.get(paramLong);
        if (localAlbumInfo != null)
        {
          m_AlbumInfoTable.delete(paramLong);
          delete("album", "_id=" + paramLong, null);
          notifyDeleted(m_AlbumInfoChangeCBHandles, localAlbumInfo);
          return true;
        }
        return false;
      }
    }
    return false;
  }
  
  public static boolean deleteAlbumMediaRelation(AlbumMediaRelation arg0)
  {
    if (??? != null)
    {
      if (setupAlbumMediaRelationTable())
      {
        AlbumMediaRelation localAlbumMediaRelation = ???.clone();
        synchronized (m_AlbumMediaRelationLock)
        {
          Set localSet = (Set)m_AlbumMediaRelationTable.get(localAlbumMediaRelation.albumId);
          if (localSet == null) {}
          while (!localSet.remove(localAlbumMediaRelation)) {
            return false;
          }
          delete("album_media", "album_id=" + localAlbumMediaRelation.albumId + " AND " + "media_id" + "=" + localAlbumMediaRelation.mediaId, null);
          notifyDeleted(m_AlbumMediaRelationChangeCBHandles, localAlbumMediaRelation);
          return true;
        }
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static boolean deleteAlbumMediaRelationsByAlbumId(long paramLong)
  {
    if (setupAlbumMediaRelationTable()) {
      synchronized (m_AlbumMediaRelationLock)
      {
        Object localObject2 = (Set)m_AlbumMediaRelationTable.get(paramLong);
        if (localObject2 == null) {
          break label112;
        }
        if (((Set)localObject2).isEmpty()) {
          break label116;
        }
        m_AlbumMediaRelationTable.delete(paramLong);
        delete("album_media", "album_id=" + paramLong, null);
        localObject2 = ((Set)localObject2).iterator();
        if (!((Iterator)localObject2).hasNext()) {
          break label127;
        }
        AlbumMediaRelation localAlbumMediaRelation = (AlbumMediaRelation)((Iterator)localObject2).next();
        notifyDeleted(m_AlbumMediaRelationChangeCBHandles, localAlbumMediaRelation);
      }
    }
    return false;
    label112:
    return false;
    label116:
    m_AlbumMediaRelationTable.delete(paramLong);
    return false;
    label127:
    return true;
  }
  
  public static boolean deleteAlbumMediaRelationsByMediaId(long paramLong)
  {
    Object localObject1 = null;
    int i;
    ArrayList localArrayList2;
    label94:
    label117:
    ArrayList localArrayList1;
    if (setupAlbumMediaRelationTable())
    {
      long l;
      synchronized (m_AlbumMediaRelationLock)
      {
        i = m_AlbumMediaRelationTable.size() - 1;
        localArrayList2 = null;
        if (i < 0) {
          break label299;
        }
        l = m_AlbumMediaRelationTable.keyAt(i);
        Object localObject4 = m_AlbumMediaRelationTable.valueAt(i);
        if (!(localObject4 instanceof Set)) {
          break label282;
        }
        AlbumMediaRelation localAlbumMediaRelation = new AlbumMediaRelation(l, paramLong);
        localObject4 = (Set)localObject4;
        if (((Set)localObject4).remove(localAlbumMediaRelation)) {
          break label291;
        }
        break label282;
        localArrayList2.add(localAlbumMediaRelation);
        if (!((Set)localObject4).isEmpty())
        {
          break label282;
          localArrayList2 = new ArrayList();
        }
      }
      if (localObject2 != null) {}
      for (;;)
      {
        ((List)localObject2).add(Long.valueOf(l));
        break;
        localArrayList1 = new ArrayList();
      }
    }
    for (;;)
    {
      label169:
      delete("album_media", "media_id=" + paramLong, null);
      i = localArrayList2.size() - 1;
      while (i >= 0)
      {
        notifyDeleted(m_AlbumMediaRelationChangeCBHandles, (AlbumMediaRelation)localArrayList2.get(i));
        i -= 1;
        continue;
        label231:
        return false;
      }
      label282:
      label291:
      label299:
      do
      {
        i = localArrayList1.size() - 1;
        while (i >= 0)
        {
          m_AlbumMediaRelationTable.delete(((Long)localArrayList1.get(i)).longValue());
          i -= 1;
        }
        break label169;
        return true;
        i -= 1;
        break;
        return false;
        if (localArrayList2 == null) {
          break label117;
        }
        break label94;
        if (localArrayList2 == null) {
          break label231;
        }
      } while (localArrayList1 != null);
    }
  }
  
  public static boolean deleteExtraDirectoryInfo(long paramLong)
  {
    if (setupExtraDirectoryInfoTable()) {
      synchronized (m_ExtraDirectoryInfoLock)
      {
        ExtraDirectoryInfo localExtraDirectoryInfo = (ExtraDirectoryInfo)m_ExtraDirectoryInfoTable.get(paramLong);
        if (localExtraDirectoryInfo != null)
        {
          m_ExtraDirectoryInfoTable.delete(paramLong);
          delete("directory", "_id=" + paramLong, null);
          notifyDeleted(m_ExtraDirectoryInfoChangeCBHandles, localExtraDirectoryInfo);
          return true;
        }
        return false;
      }
    }
    return false;
  }
  
  public static boolean deleteExtraMediaInfo(long paramLong)
  {
    if (setupExtraMediaInfoTable()) {
      synchronized (m_ExtraMediaInfoLock)
      {
        ExtraMediaInfo localExtraMediaInfo = (ExtraMediaInfo)m_ExtraMediaInfoTable.get(paramLong);
        if (localExtraMediaInfo != null)
        {
          m_ExtraMediaInfoTable.delete(paramLong);
          delete("media", "media_id=" + paramLong, null);
          notifyDeleted(m_ExtraMediaInfoChangeCBHandles, localExtraMediaInfo);
          return true;
        }
        return false;
      }
    }
    return false;
  }
  
  public static List<AlbumInfo> getAlbumInfos()
  {
    if (setupAlbumInfoTable()) {}
    synchronized (m_AlbumInfoLock)
    {
      ArrayList localArrayList = new ArrayList();
      int i = m_AlbumInfoTable.size() - 1;
      while (i >= 0)
      {
        Object localObject2 = m_AlbumInfoTable.valueAt(i);
        boolean bool = localObject2 instanceof AlbumInfo;
        if (!bool)
        {
          i -= 1;
          continue;
          return Collections.EMPTY_LIST;
        }
        else
        {
          localArrayList.add(((AlbumInfo)localObject2).clone());
        }
      }
    }
    return localList;
  }
  
  public static List<AlbumMediaRelation> getAlbumMediaRelationsByAlbumId(long paramLong)
  {
    if (setupAlbumMediaRelationTable()) {}
    synchronized (m_AlbumMediaRelationLock)
    {
      Object localObject2 = (Set)m_AlbumMediaRelationTable.get(paramLong);
      if (localObject2 == null)
      {
        return Collections.EMPTY_LIST;
        return Collections.EMPTY_LIST;
      }
      ArrayList localArrayList = new ArrayList(((Set)localObject2).size());
      localObject2 = ((Set)localObject2).iterator();
      if (((Iterator)localObject2).hasNext()) {
        localArrayList.add(((AlbumMediaRelation)((Iterator)localObject2).next()).clone());
      }
    }
    return localList;
  }
  
  public static ExtraDirectoryInfo getExtraDirectoryInfo(long paramLong)
  {
    if (setupExtraDirectoryInfoTable()) {
      synchronized (m_ExtraDirectoryInfoLock)
      {
        ExtraDirectoryInfo localExtraDirectoryInfo = (ExtraDirectoryInfo)m_ExtraDirectoryInfoTable.get(paramLong);
        if (localExtraDirectoryInfo == null)
        {
          localExtraDirectoryInfo = null;
          return localExtraDirectoryInfo;
        }
        localExtraDirectoryInfo = localExtraDirectoryInfo.clone();
      }
    }
    return null;
  }
  
  public static ExtraMediaInfo getExtraMediaInfo(long paramLong)
  {
    if (setupExtraMediaInfoTable()) {
      synchronized (m_ExtraMediaInfoLock)
      {
        ExtraMediaInfo localExtraMediaInfo = (ExtraMediaInfo)m_ExtraMediaInfoTable.get(paramLong);
        if (localExtraMediaInfo == null)
        {
          localExtraMediaInfo = null;
          return localExtraMediaInfo;
        }
        localExtraMediaInfo = localExtraMediaInfo.clone();
      }
    }
    return null;
  }
  
  public static List<ExtraMediaInfo> getExtraMediaInfos()
  {
    if (setupExtraMediaInfoTable()) {}
    synchronized (m_ExtraMediaInfoLock)
    {
      ArrayList localArrayList = new ArrayList();
      int i = m_ExtraMediaInfoTable.size() - 1;
      while (i >= 0)
      {
        Object localObject2 = m_ExtraMediaInfoTable.valueAt(i);
        boolean bool = localObject2 instanceof ExtraMediaInfo;
        if (!bool)
        {
          i -= 1;
          continue;
          return Collections.EMPTY_LIST;
        }
        else
        {
          localArrayList.add(((ExtraMediaInfo)localObject2).clone());
        }
      }
    }
    return localList;
  }
  
  private static long insert(String paramString, ContentValues paramContentValues)
  {
    try
    {
      Uri localUri;
      if (!IS_SERVER_MODE)
      {
        localUri = Uri.parse("content://oneplus.gallery/" + paramString);
        paramString = BaseApplication.current().getContentResolver().acquireUnstableContentProviderClient(localUri);
        if (paramString == null) {
          break label93;
        }
      }
      try
      {
        paramContentValues = paramString.insert(localUri, paramContentValues);
        if (paramContentValues == null)
        {
          return -1L;
          if (openDatabase()) {
            return m_Database.insert(paramString, null, paramContentValues);
          }
          Log.e(TAG, "insert() - Fail to open database");
          return -1L;
          label93:
          Log.w(TAG, "update() - No gallery content provider");
          return -1L;
        }
        long l = ContentUris.parseId(paramContentValues);
        return l;
      }
      finally
      {
        paramString.release();
      }
      return -1L;
    }
    catch (Throwable paramString)
    {
      Log.e(TAG, "insert() - Fail to insert", paramString);
    }
  }
  
  private static void invalidateExtraMediaInfo(long paramLong)
  {
    for (;;)
    {
      Object localObject3;
      synchronized (m_ExtraMediaInfoLock)
      {
        if (m_ExtraMediaInfoTable != null)
        {
          ExtraMediaInfo localExtraMediaInfo = (ExtraMediaInfo)m_ExtraMediaInfoTable.get(paramLong);
          localObject3 = new SimpleRef();
          if (query("media", null, "media_id=" + paramLong, null, null, new QueryCallback()
          {
            public void onQuery(Cursor paramAnonymousCursor)
            {
              if (!paramAnonymousCursor.moveToNext()) {
                return;
              }
              GalleryDatabase.this.set(new GalleryDatabase.ExtraMediaInfo(paramAnonymousCursor));
            }
          }))
          {
            localObject3 = (ExtraMediaInfo)((Ref)localObject3).get();
            if (localExtraMediaInfo == null) {
              break label117;
            }
            if (!localExtraMediaInfo.equals(localObject3)) {
              break label161;
            }
          }
        }
        else
        {
          return;
        }
        Log.e(TAG, "invalidateExtraMediaInfo() - Fail to query from database");
      }
      label117:
      if (localObject3 != null)
      {
        m_ExtraMediaInfoTable.put(paramLong, localObject3);
        Log.d(TAG, "invalidateExtraMediaInfo() - Extra info of ", Long.valueOf(paramLong), " added");
        notifyAdded(m_ExtraMediaInfoChangeCBHandles, ((ExtraMediaInfo)localObject3).clone());
        continue;
        label161:
        if (localObject3 == null)
        {
          m_ExtraMediaInfoTable.delete(paramLong);
          Log.d(TAG, "invalidateExtraMediaInfo() - Extra info of ", Long.valueOf(paramLong), " deleted");
          notifyDeleted(m_ExtraMediaInfoChangeCBHandles, localObject2);
        }
        else
        {
          m_ExtraMediaInfoTable.put(paramLong, localObject3);
          Log.d(TAG, "invalidateExtraMediaInfo() - Extra info of ", Long.valueOf(paramLong), " updated");
          notifyUpdated(m_ExtraMediaInfoChangeCBHandles, localObject2, ((ExtraMediaInfo)localObject3).clone());
        }
      }
    }
  }
  
  private static boolean isColumnExist(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2)
  {
    if (paramSQLiteDatabase == null) {}
    while ((paramString1 == null) || (paramString2 == null))
    {
      Log.w(TAG, "isColumnExist() - db or table name or column name is null");
      return false;
    }
    paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("PRAGMA table_info(" + paramString1 + ")", null);
    if (paramSQLiteDatabase == null) {}
    int i;
    label73:
    label85:
    do
    {
      break label73;
      return false;
      for (;;)
      {
        i = paramSQLiteDatabase.getColumnIndex("name");
        if (i != -1) {
          break label85;
        }
        if (!paramSQLiteDatabase.moveToNext()) {
          break;
        }
      }
    } while (!paramString2.equals(paramSQLiteDatabase.getString(i)));
    Log.d(TAG, "isColumnExist() - column " + paramString2 + " exist in the table " + paramString1 + ", index:" + i);
    return true;
  }
  
  private static <TValue> void notifyAdded(List<CallbackHandle<ChangeCallback<TValue>>> paramList, TValue paramTValue)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ((ChangeCallback)((CallbackHandle)paramList.get(i)).getCallback()).onAdded(paramTValue);
      i -= 1;
    }
  }
  
  private static <TValue> void notifyDeleted(List<CallbackHandle<ChangeCallback<TValue>>> paramList, TValue paramTValue)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ((ChangeCallback)((CallbackHandle)paramList.get(i)).getCallback()).onDeleted(paramTValue);
      i -= 1;
    }
  }
  
  private static <TValue> void notifyUpdated(List<CallbackHandle<ChangeCallback<TValue>>> paramList, TValue paramTValue1, TValue paramTValue2)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ((ChangeCallback)((CallbackHandle)paramList.get(i)).getCallback()).onUpdated(paramTValue1, paramTValue2);
      i -= 1;
    }
  }
  
  private static void onDatabaseCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    onDatabaseUpgrade(paramSQLiteDatabase, 0, 13);
  }
  
  private static void onDatabaseDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    Log.w(TAG, "onDatabaseDowngrade() - DB downgrade from " + paramInt1 + " to " + paramInt2);
  }
  
  private static void onDatabaseUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    Log.w(TAG, "onDatabaseUpgrade() - Upgrade from " + paramInt1 + " to " + paramInt2);
    if (paramInt1 >= 6)
    {
      if (paramInt1 == 6) {
        break label152;
      }
      if (paramInt1 == 7) {
        break label255;
      }
      if (paramInt1 == 8) {
        break label316;
      }
      label57:
      if (paramInt1 == 9) {
        break label328;
      }
      if (paramInt1 == 10) {
        break label554;
      }
      if (paramInt1 == 11) {
        break label600;
      }
      if (paramInt1 == 12) {
        break label666;
      }
    }
    label152:
    label163:
    label246:
    label255:
    label316:
    label328:
    label339:
    label350:
    label361:
    label372:
    label383:
    label394:
    label405:
    label416:
    label427:
    label438:
    label464:
    label473:
    label482:
    label491:
    label500:
    label509:
    label518:
    label527:
    label536:
    label545:
    label554:
    label565:
    label591:
    label600:
    label611:
    label622:
    label648:
    label657:
    label666:
    label715:
    label722:
    for (;;)
    {
      return;
      if (paramInt1 <= 0) {}
      for (;;)
      {
        paramSQLiteDatabase.execSQL("CREATE TABLE media (media_id INTEGER,oneplus_flags INTEGER,address_locale TEXT,address_latitude REAL,address_longitude REAL,address_country TEXT,address_admin_area TEXT,address_sub_admin_area TEXT,address_locality TEXT,address_sub_locality TEXT,address_feature TEXT,address_address_line_0 TEXT,address_address_line_1 TEXT,object_detection_version INTEGER DEFAULT 0,object_detection_file_time INTEGER DEFAULT 0,object_detection_result TEXT,scene_detection_version INTEGER DEFAULT 0,scene_detection_file_time INTEGER DEFAULT 0,scene_detection_result TEXT);");
        paramSQLiteDatabase.execSQL("CREATE TABLE album (_id INTEGER PRIMARY KEY,_display_name TEXT,date_added INTEGER,date_modified INTEGER,date_media_added INTEGER,oneplus_flags INTEGER);");
        paramSQLiteDatabase.execSQL("CREATE TABLE album_media (album_id INTEGER,media_id INTEGER);");
        paramSQLiteDatabase.execSQL("CREATE TABLE directory (_id INTEGER PRIMARY KEY,date_media_added INTEGER,oneplus_flags INTEGER);");
        paramSQLiteDatabase.execSQL("CREATE INDEX album_id_index ON album_media(album_id);");
        paramSQLiteDatabase.execSQL("CREATE INDEX media_id_index ON media(media_id);");
        paramInt1 = 13;
        break;
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS media");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS album");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS album_media");
      }
      if (isColumnExist(paramSQLiteDatabase, "album", "date_added")) {
        if (!isColumnExist(paramSQLiteDatabase, "album", "date_modified")) {
          break label246;
        }
      }
      long l;
      ContentValues localContentValues;
      for (;;)
      {
        l = System.currentTimeMillis();
        localContentValues = new ContentValues();
        localContentValues.put("date_added", Long.valueOf(l));
        localContentValues.put("date_modified", Long.valueOf(l));
        localContentValues.put("date_media_added", Long.valueOf(l));
        paramSQLiteDatabase.update("album", localContentValues, null, null);
        paramInt1 = 7;
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE album ADD date_added INTEGER;");
        break label163;
        paramSQLiteDatabase.execSQL("ALTER TABLE album ADD date_modified INTEGER;");
      }
      if (isColumnExist(paramSQLiteDatabase, "album", "date_media_added")) {}
      for (;;)
      {
        l = System.currentTimeMillis();
        localContentValues = new ContentValues();
        localContentValues.put("date_media_added", Long.valueOf(l));
        paramSQLiteDatabase.update("album", localContentValues, null, null);
        paramInt1 = 8;
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE album ADD date_media_added INTEGER;");
      }
      paramSQLiteDatabase.execSQL("CREATE TABLE directory (_id INTEGER PRIMARY KEY,date_media_added INTEGER,oneplus_flags INTEGER);");
      paramInt1 = 9;
      break label57;
      if (isColumnExist(paramSQLiteDatabase, "media", "address_locale"))
      {
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_latitude")) {
          break label464;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_longitude")) {
          break label473;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_country")) {
          break label482;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_admin_area")) {
          break label491;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_sub_admin_area")) {
          break label500;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_locality")) {
          break label509;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_sub_locality")) {
          break label518;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_feature")) {
          break label527;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_address_line_0")) {
          break label536;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "address_address_line_1")) {
          break label545;
        }
      }
      for (;;)
      {
        paramInt1 = 10;
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_locale TEXT;");
        break label339;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_latitude REAL;");
        break label350;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_longitude REAL;");
        break label361;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_country TEXT;");
        break label372;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_admin_area TEXT;");
        break label383;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_sub_admin_area TEXT;");
        break label394;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_locality TEXT;");
        break label405;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_sub_locality TEXT;");
        break label416;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_feature TEXT;");
        break label427;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_address_line_0 TEXT;");
        break label438;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD address_address_line_1 TEXT;");
      }
      if (isColumnExist(paramSQLiteDatabase, "album", "oneplus_flags")) {
        if (!isColumnExist(paramSQLiteDatabase, "directory", "oneplus_flags")) {
          break label591;
        }
      }
      for (;;)
      {
        paramInt1 = 11;
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE album ADD oneplus_flags INTEGER;");
        break label565;
        paramSQLiteDatabase.execSQL("ALTER TABLE directory ADD oneplus_flags INTEGER;");
      }
      if (isColumnExist(paramSQLiteDatabase, "media", "object_detection_version"))
      {
        if (!isColumnExist(paramSQLiteDatabase, "media", "object_detection_file_time")) {
          break label648;
        }
        if (!isColumnExist(paramSQLiteDatabase, "media", "object_detection_result")) {
          break label657;
        }
      }
      for (;;)
      {
        paramInt1 = 12;
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD object_detection_version INTEGER DEFAULT 0;");
        break label611;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD object_detection_file_time INTEGER DEFAULT 0;");
        break label622;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD object_detection_result TEXT;");
      }
      if (isColumnExist(paramSQLiteDatabase, "media", "scene_detection_version")) {
        if (!isColumnExist(paramSQLiteDatabase, "media", "scene_detection_file_time")) {
          break label715;
        }
      }
      for (;;)
      {
        if (isColumnExist(paramSQLiteDatabase, "media", "scene_detection_result")) {
          break label722;
        }
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD scene_detection_result TEXT;");
        return;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD scene_detection_version INTEGER DEFAULT 0;");
        break;
        paramSQLiteDatabase.execSQL("ALTER TABLE media ADD scene_detection_file_time INTEGER DEFAULT 0;");
      }
    }
  }
  
  private static void onGalleryContentChanged(Uri paramUri)
  {
    Log.d(TAG, "onGalleryContentChanged() - URI : ", paramUri);
    if (!paramUri.toString().startsWith("content://oneplus.gallery/media/")) {
      return;
    }
    try
    {
      long l = ContentUris.parseId(paramUri);
      invalidateExtraMediaInfo(l);
      return;
    }
    catch (Throwable paramUri)
    {
      Log.e(TAG, "onGalleryContentChanged() - Fail to get media ID", paramUri);
    }
  }
  
  private static boolean openDatabase()
  {
    if (!IS_SERVER_MODE) {}
    while (m_Database != null) {
      return true;
    }
    synchronized (m_DatabaseLock)
    {
      long l1;
      SQLiteOpenHelper local5;
      if (m_Database == null)
      {
        Log.d(TAG, "openDatabase() - Start");
        l1 = SystemClock.elapsedRealtime();
        local5 = new SQLiteOpenHelper(BaseApplication.current(), "gallery.db", null, 13)
        {
          public void onCreate(SQLiteDatabase paramAnonymousSQLiteDatabase)
          {
            GalleryDatabase.onDatabaseCreate(paramAnonymousSQLiteDatabase);
          }
          
          public void onDowngrade(SQLiteDatabase paramAnonymousSQLiteDatabase, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            GalleryDatabase.onDatabaseDowngrade(paramAnonymousSQLiteDatabase, paramAnonymousInt1, paramAnonymousInt2);
          }
          
          public void onUpgrade(SQLiteDatabase paramAnonymousSQLiteDatabase, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            GalleryDatabase.onDatabaseUpgrade(paramAnonymousSQLiteDatabase, paramAnonymousInt1, paramAnonymousInt2);
          }
        };
      }
      try
      {
        m_Database = local5.getWritableDatabase();
        long l2 = SystemClock.elapsedRealtime();
        Log.d(TAG, "openDatabase() - Take " + (l2 - l1) + " ms to open as writable");
        return true;
      }
      catch (Throwable localThrowable)
      {
        Log.e(TAG, "openDatabase() - Fail to open database", localThrowable);
        return false;
      }
      return true;
    }
  }
  
  /* Error */
  private static boolean query(String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, QueryCallback paramQueryCallback)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: getstatic 327	com/oneplus/gallery2/media/GalleryDatabase:IS_SERVER_MODE	Z
    //   6: ifne +79 -> 85
    //   9: new 339	java/lang/StringBuilder
    //   12: dup
    //   13: ldc -120
    //   15: invokespecial 346	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   18: aload_0
    //   19: invokevirtual 352	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: invokevirtual 355	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   25: invokestatic 376	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   28: astore 9
    //   30: invokestatic 313	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   33: invokevirtual 368	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   36: aload 9
    //   38: invokevirtual 561	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   41: astore 8
    //   43: aload 8
    //   45: ifnull +153 -> 198
    //   48: aload 7
    //   50: astore_0
    //   51: aload 8
    //   53: aload 9
    //   55: aload_1
    //   56: aload_2
    //   57: aload_3
    //   58: aload 4
    //   60: invokevirtual 845	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   63: astore_2
    //   64: aload 5
    //   66: aload_2
    //   67: invokeinterface 849 2 0
    //   72: aload_2
    //   73: ifnonnull +136 -> 209
    //   76: aload 8
    //   78: invokevirtual 569	android/content/ContentProviderClient:release	()Z
    //   81: pop
    //   82: goto +193 -> 275
    //   85: invokestatic 572	com/oneplus/gallery2/media/GalleryDatabase:openDatabase	()Z
    //   88: istore 6
    //   90: iload 6
    //   92: ifeq +66 -> 158
    //   95: getstatic 574	com/oneplus/gallery2/media/GalleryDatabase:m_Database	Landroid/database/sqlite/SQLiteDatabase;
    //   98: aload_0
    //   99: aload_1
    //   100: aload_2
    //   101: aload_3
    //   102: aconst_null
    //   103: aconst_null
    //   104: aload 4
    //   106: invokevirtual 852	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   109: astore_0
    //   110: aload 5
    //   112: aload_0
    //   113: invokeinterface 849 2 0
    //   118: aload_0
    //   119: ifnull +156 -> 275
    //   122: aload_0
    //   123: invokeinterface 855 1 0
    //   128: goto +147 -> 275
    //   131: astore_0
    //   132: aconst_null
    //   133: astore_1
    //   134: aload_1
    //   135: ifnull +50 -> 185
    //   138: aload_1
    //   139: aload_0
    //   140: if_acmpne +50 -> 190
    //   143: aload_1
    //   144: athrow
    //   145: astore_0
    //   146: getstatic 337	com/oneplus/gallery2/media/GalleryDatabase:TAG	Ljava/lang/String;
    //   149: ldc_w 857
    //   152: aload_0
    //   153: invokestatic 591	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   156: iconst_0
    //   157: ireturn
    //   158: getstatic 337	com/oneplus/gallery2/media/GalleryDatabase:TAG	Ljava/lang/String;
    //   161: ldc_w 859
    //   164: invokestatic 541	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   167: iconst_0
    //   168: ireturn
    //   169: astore_1
    //   170: aload_0
    //   171: ifnonnull +5 -> 176
    //   174: aload_1
    //   175: athrow
    //   176: aload_0
    //   177: invokeinterface 855 1 0
    //   182: goto -8 -> 174
    //   185: aload_0
    //   186: astore_1
    //   187: goto -44 -> 143
    //   190: aload_1
    //   191: aload_0
    //   192: invokevirtual 863	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   195: goto -52 -> 143
    //   198: getstatic 337	com/oneplus/gallery2/media/GalleryDatabase:TAG	Ljava/lang/String;
    //   201: ldc_w 865
    //   204: invokestatic 586	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   207: iconst_0
    //   208: ireturn
    //   209: aload 7
    //   211: astore_0
    //   212: aload_2
    //   213: invokeinterface 855 1 0
    //   218: goto -142 -> 76
    //   221: astore_1
    //   222: aload_0
    //   223: ifnull +39 -> 262
    //   226: aload_0
    //   227: aload_1
    //   228: if_acmpne +39 -> 267
    //   231: aload_0
    //   232: athrow
    //   233: astore_0
    //   234: aload 8
    //   236: invokevirtual 569	android/content/ContentProviderClient:release	()Z
    //   239: pop
    //   240: aload_0
    //   241: athrow
    //   242: astore_1
    //   243: aload_2
    //   244: ifnonnull +7 -> 251
    //   247: aload_1
    //   248: astore_0
    //   249: aload_1
    //   250: athrow
    //   251: aload_1
    //   252: astore_0
    //   253: aload_2
    //   254: invokeinterface 855 1 0
    //   259: goto -12 -> 247
    //   262: aload_1
    //   263: astore_0
    //   264: goto -33 -> 231
    //   267: aload_0
    //   268: aload_1
    //   269: invokevirtual 863	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   272: goto -41 -> 231
    //   275: iconst_1
    //   276: ireturn
    //   277: astore_0
    //   278: goto -144 -> 134
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	paramString1	String
    //   0	281	1	paramArrayOfString1	String[]
    //   0	281	2	paramString2	String
    //   0	281	3	paramArrayOfString2	String[]
    //   0	281	4	paramString3	String
    //   0	281	5	paramQueryCallback	QueryCallback
    //   88	3	6	bool	boolean
    //   1	209	7	localObject	Object
    //   41	194	8	localContentProviderClient	ContentProviderClient
    //   28	26	9	localUri	Uri
    // Exception table:
    //   from	to	target	type
    //   95	110	131	finally
    //   122	128	131	finally
    //   3	43	145	java/lang/Throwable
    //   76	82	145	java/lang/Throwable
    //   85	90	145	java/lang/Throwable
    //   143	145	145	java/lang/Throwable
    //   158	167	145	java/lang/Throwable
    //   190	195	145	java/lang/Throwable
    //   198	207	145	java/lang/Throwable
    //   234	242	145	java/lang/Throwable
    //   110	118	169	finally
    //   51	64	221	finally
    //   212	218	221	finally
    //   249	251	221	finally
    //   253	259	221	finally
    //   231	233	233	finally
    //   267	272	233	finally
    //   64	72	242	finally
    //   174	176	277	finally
    //   176	182	277	finally
  }
  
  private static boolean setupAlbumInfoTable()
  {
    if (m_AlbumInfoTable == null) {
      synchronized (m_AlbumInfoLock)
      {
        if (m_AlbumInfoTable == null)
        {
          Log.d(TAG, "setupAlbumInfoTable() - Start");
          LongSparseArray localLongSparseArray = new LongSparseArray();
          if (openDatabase())
          {
            long l1 = SystemClock.elapsedRealtime();
            if (!query("album", null, null, null, null, new QueryCallback()
            {
              public void onQuery(Cursor paramAnonymousCursor)
              {
                while (paramAnonymousCursor.moveToNext())
                {
                  GalleryDatabase.AlbumInfo localAlbumInfo = new GalleryDatabase.AlbumInfo(paramAnonymousCursor);
                  GalleryDatabase.this.put(localAlbumInfo.albumId, localAlbumInfo);
                }
              }
            })) {
              break label147;
            }
            long l2 = SystemClock.elapsedRealtime();
            Log.d(TAG, "setupAlbumInfoTable() - Take " + (l2 - l1) + " ms to setup " + localLongSparseArray.size() + " entries");
            m_AlbumInfoTable = localLongSparseArray;
            return true;
          }
        }
        else
        {
          return true;
        }
        Log.e(TAG, "setupAlbumInfoTable() - Fail to open database");
        return false;
        label147:
        Log.e(TAG, "setupAlbumInfoTable() - Fail to query from database");
        return false;
      }
    }
    return true;
  }
  
  private static boolean setupAlbumMediaRelationTable()
  {
    if (m_AlbumMediaRelationTable == null) {
      synchronized (m_AlbumMediaRelationLock)
      {
        if (m_AlbumMediaRelationTable == null)
        {
          Log.d(TAG, "setupAlbumMediaRelationTable() - Start");
          LongSparseArray localLongSparseArray = new LongSparseArray();
          if (openDatabase())
          {
            long l1 = SystemClock.elapsedRealtime();
            if (!query("album_media", TABLE_ALBUM_MEDIA_COLUMNS, null, null, null, new QueryCallback()
            {
              public void onQuery(Cursor paramAnonymousCursor)
              {
                if (paramAnonymousCursor.moveToNext())
                {
                  GalleryDatabase.AlbumMediaRelation localAlbumMediaRelation = new GalleryDatabase.AlbumMediaRelation(paramAnonymousCursor.getLong(0), paramAnonymousCursor.getLong(1));
                  Object localObject = (Set)GalleryDatabase.this.get(localAlbumMediaRelation.albumId);
                  if (localObject != null) {}
                  for (;;)
                  {
                    ((Set)localObject).add(localAlbumMediaRelation);
                    break;
                    localObject = new HashSet();
                    GalleryDatabase.this.put(localAlbumMediaRelation.albumId, localObject);
                  }
                }
              }
            })) {
              break label149;
            }
            long l2 = SystemClock.elapsedRealtime();
            Log.d(TAG, "setupAlbumMediaRelationTable() - Take " + (l2 - l1) + " ms to setup " + localLongSparseArray.size() + " entries");
            m_AlbumMediaRelationTable = localLongSparseArray;
            return true;
          }
        }
        else
        {
          return true;
        }
        Log.e(TAG, "setupAlbumMediaRelationTable() - Fail to open database");
        return false;
        label149:
        Log.e(TAG, "setupAlbumMediaRelationTable() - Fail to query from database");
        return false;
      }
    }
    return true;
  }
  
  private static boolean setupExtraDirectoryInfoTable()
  {
    if (m_ExtraDirectoryInfoTable == null) {
      synchronized (m_ExtraDirectoryInfoLock)
      {
        if (m_ExtraDirectoryInfoTable == null)
        {
          Log.d(TAG, "setupExtraDirectoryInfoTable() - Start");
          LongSparseArray localLongSparseArray = new LongSparseArray();
          if (openDatabase())
          {
            long l1 = SystemClock.elapsedRealtime();
            if (!query("directory", null, null, null, null, new QueryCallback()
            {
              public void onQuery(Cursor paramAnonymousCursor)
              {
                while (paramAnonymousCursor.moveToNext())
                {
                  GalleryDatabase.ExtraDirectoryInfo localExtraDirectoryInfo = new GalleryDatabase.ExtraDirectoryInfo(paramAnonymousCursor);
                  GalleryDatabase.this.put(localExtraDirectoryInfo.directoryId, localExtraDirectoryInfo);
                }
              }
            })) {
              break label147;
            }
            long l2 = SystemClock.elapsedRealtime();
            Log.d(TAG, "setupExtraDirectoryInfoTable() - Take " + (l2 - l1) + " ms to setup " + localLongSparseArray.size() + " entries");
            m_ExtraDirectoryInfoTable = localLongSparseArray;
            return true;
          }
        }
        else
        {
          return true;
        }
        Log.e(TAG, "setupExtraDirectoryInfoTable() - Fail to open database");
        return false;
        label147:
        Log.e(TAG, "setupExtraDirectoryInfoTable() - Fail to query from database");
        return false;
      }
    }
    return true;
  }
  
  private static boolean setupExtraMediaInfoTable()
  {
    if (m_ExtraMediaInfoTable == null) {
      synchronized (m_ExtraMediaInfoLock)
      {
        if (m_ExtraMediaInfoTable == null)
        {
          Log.d(TAG, "setupExtraMediaInfoTable() - Start");
          LongSparseArray localLongSparseArray = new LongSparseArray();
          if (openDatabase())
          {
            long l1 = SystemClock.elapsedRealtime();
            if (!query("media", null, null, null, null, new QueryCallback()
            {
              public void onQuery(Cursor paramAnonymousCursor)
              {
                while (paramAnonymousCursor.moveToNext())
                {
                  GalleryDatabase.ExtraMediaInfo localExtraMediaInfo = new GalleryDatabase.ExtraMediaInfo(paramAnonymousCursor);
                  GalleryDatabase.this.put(localExtraMediaInfo.mediaId, localExtraMediaInfo);
                }
              }
            })) {
              break label147;
            }
            long l2 = SystemClock.elapsedRealtime();
            Log.d(TAG, "setupExtraMediaInfoTable() - Take " + (l2 - l1) + " ms to setup " + localLongSparseArray.size() + " entries");
            m_ExtraMediaInfoTable = localLongSparseArray;
            return true;
          }
        }
        else
        {
          return true;
        }
        Log.e(TAG, "setupExtraMediaInfoTable() - Fail to open database");
        return false;
        label147:
        Log.e(TAG, "setupExtraMediaInfoTable() - Fail to query from database");
        return false;
      }
    }
    return true;
  }
  
  private static int update(String paramString1, ContentValues paramContentValues, String paramString2, String[] paramArrayOfString)
  {
    try
    {
      Uri localUri;
      if (!IS_SERVER_MODE)
      {
        localUri = Uri.parse("content://oneplus.gallery/" + paramString1);
        paramString1 = BaseApplication.current().getContentResolver().acquireUnstableContentProviderClient(localUri);
        if (paramString1 == null) {
          break label90;
        }
      }
      try
      {
        int i = paramString1.update(localUri, paramContentValues, paramString2, paramArrayOfString);
        return i;
      }
      finally
      {
        paramString1.release();
      }
      if (openDatabase()) {
        return m_Database.update(paramString1, paramContentValues, paramString2, paramArrayOfString);
      }
      Log.e(TAG, "update() - Fail to open database");
      return 0;
      label90:
      Log.w(TAG, "update() - No gallery content provider");
      return 0;
    }
    catch (Throwable paramString1)
    {
      Log.e(TAG, "update() - Fail to update", paramString1);
    }
    return 0;
  }
  
  public static boolean updateAlbumInfo(AlbumInfo paramAlbumInfo)
  {
    if (paramAlbumInfo != null)
    {
      if (setupAlbumInfoTable())
      {
        AlbumInfo localAlbumInfo = paramAlbumInfo.clone();
        long l = localAlbumInfo.albumId;
        synchronized (m_AlbumInfoLock)
        {
          ContentValues localContentValues = new ContentValues(4);
          localContentValues.put("_display_name", paramAlbumInfo.name);
          localContentValues.put("date_modified", Long.valueOf(paramAlbumInfo.lastModifiedTime));
          localContentValues.put("date_media_added", Long.valueOf(paramAlbumInfo.lastMediaAddedTime));
          localContentValues.put("oneplus_flags", Long.valueOf(paramAlbumInfo.oneplusFlags));
          paramAlbumInfo = (AlbumInfo)m_AlbumInfoTable.get(l);
          if (paramAlbumInfo != null)
          {
            if (update("album", localContentValues, "_id=" + l, null) != 0)
            {
              m_AlbumInfoTable.put(l, localAlbumInfo);
              notifyUpdated(m_AlbumInfoChangeCBHandles, paramAlbumInfo, localAlbumInfo);
              return true;
            }
          }
          else {
            return false;
          }
          return false;
        }
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static boolean updateExtraDirectoryInfo(ExtraDirectoryInfo paramExtraDirectoryInfo)
  {
    if (paramExtraDirectoryInfo != null)
    {
      if (setupExtraDirectoryInfoTable())
      {
        ExtraDirectoryInfo localExtraDirectoryInfo = paramExtraDirectoryInfo.clone();
        long l = localExtraDirectoryInfo.directoryId;
        synchronized (m_ExtraDirectoryInfoLock)
        {
          ContentValues localContentValues = new ContentValues(3);
          localContentValues.put("date_media_added", Long.valueOf(paramExtraDirectoryInfo.mediaAddedTime));
          localContentValues.put("oneplus_flags", Long.valueOf(paramExtraDirectoryInfo.oneplusFlags));
          paramExtraDirectoryInfo = (ExtraDirectoryInfo)m_ExtraDirectoryInfoTable.get(l);
          if (paramExtraDirectoryInfo != null)
          {
            if (update("directory", localContentValues, "_id=" + l, null) <= 0)
            {
              Log.e(TAG, "updateExtraDirectoryInfo() - Fail to update");
              return false;
            }
          }
          else
          {
            localContentValues.put("_id", Long.valueOf(l));
            if (insert("directory", localContentValues) != -1L)
            {
              m_ExtraDirectoryInfoTable.put(l, localExtraDirectoryInfo);
              notifyAdded(m_ExtraDirectoryInfoChangeCBHandles, localExtraDirectoryInfo);
              return true;
            }
            Log.e(TAG, "updateExtraDirectoryInfo() - Fail to insert");
            return false;
          }
          m_ExtraDirectoryInfoTable.put(l, localExtraDirectoryInfo);
          notifyUpdated(m_ExtraDirectoryInfoChangeCBHandles, paramExtraDirectoryInfo, localExtraDirectoryInfo);
          return true;
        }
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static boolean updateExtraMediaInfo(ExtraMediaInfo arg0)
  {
    if (??? != null)
    {
      if (setupExtraMediaInfoTable())
      {
        ExtraMediaInfo localExtraMediaInfo1 = ???.clone();
        long l = localExtraMediaInfo1.mediaId;
        synchronized (m_ExtraMediaInfoLock)
        {
          ContentValues localContentValues = new ContentValues();
          localExtraMediaInfo1.toContentValues(localContentValues);
          ExtraMediaInfo localExtraMediaInfo2 = (ExtraMediaInfo)m_ExtraMediaInfoTable.get(l);
          if (localExtraMediaInfo2 != null)
          {
            if (update("media", localContentValues, "media_id=" + l, null) <= 0)
            {
              Log.e(TAG, "updateExtraMediaInfo() - Fail to update");
              return false;
            }
          }
          else
          {
            if (insert("media", localContentValues) != -1L)
            {
              m_ExtraMediaInfoTable.put(l, localExtraMediaInfo1);
              notifyAdded(m_ExtraMediaInfoChangeCBHandles, localExtraMediaInfo1);
              return true;
            }
            Log.e(TAG, "updateExtraMediaInfo() - Fail to insert");
            return false;
          }
          m_ExtraMediaInfoTable.put(l, localExtraMediaInfo1);
          notifyUpdated(m_ExtraMediaInfoChangeCBHandles, localExtraMediaInfo2, localExtraMediaInfo1);
          return true;
        }
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static final class AlbumInfo
    implements Cloneable
  {
    public volatile long albumId = -1L;
    public volatile long creationTime;
    public volatile long lastMediaAddedTime;
    public volatile long lastModifiedTime;
    public volatile String name;
    public volatile long oneplusFlags;
    
    public AlbumInfo() {}
    
    public AlbumInfo(long paramLong)
    {
      this.albumId = paramLong;
    }
    
    public AlbumInfo(Cursor paramCursor)
    {
      this.albumId = CursorUtils.getLong(paramCursor, "_id", 0L);
      this.name = CursorUtils.getString(paramCursor, "_display_name");
      this.creationTime = CursorUtils.getLong(paramCursor, "date_added", 0L);
      this.lastModifiedTime = CursorUtils.getLong(paramCursor, "date_modified", 0L);
      this.lastMediaAddedTime = CursorUtils.getLong(paramCursor, "date_media_added", 0L);
      this.oneplusFlags = CursorUtils.getLong(paramCursor, "oneplus_flags", 0L);
    }
    
    public AlbumInfo clone()
    {
      try
      {
        AlbumInfo localAlbumInfo = (AlbumInfo)super.clone();
        return localAlbumInfo;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
    }
  }
  
  public static final class AlbumMediaRelation
    implements Cloneable
  {
    public volatile long albumId;
    public volatile long mediaId;
    
    public AlbumMediaRelation(long paramLong1, long paramLong2)
    {
      this.albumId = paramLong1;
      this.mediaId = paramLong2;
    }
    
    public AlbumMediaRelation clone()
    {
      try
      {
        AlbumMediaRelation localAlbumMediaRelation = (AlbumMediaRelation)super.clone();
        return localAlbumMediaRelation;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof AlbumMediaRelation)) {
        return false;
      }
      paramObject = (AlbumMediaRelation)paramObject;
      return (((AlbumMediaRelation)paramObject).albumId == this.albumId) && (((AlbumMediaRelation)paramObject).mediaId == this.mediaId);
    }
    
    public int hashCode()
    {
      return (int)(this.albumId & 0xFFFFFFFFFFFF0000) | (int)(this.mediaId & 0xFFFF);
    }
  }
  
  public static abstract class ChangeCallback<TValue>
  {
    void onAdded(TValue paramTValue) {}
    
    void onDeleted(TValue paramTValue) {}
    
    void onUpdated(TValue paramTValue1, TValue paramTValue2) {}
  }
  
  public static final class ExtraDirectoryInfo
    implements Cloneable
  {
    public volatile long directoryId;
    public volatile long mediaAddedTime;
    public volatile long oneplusFlags;
    
    public ExtraDirectoryInfo(long paramLong)
    {
      this(paramLong, 0L, 0L);
    }
    
    public ExtraDirectoryInfo(long paramLong1, long paramLong2, long paramLong3)
    {
      this.directoryId = paramLong1;
      this.mediaAddedTime = paramLong2;
      this.oneplusFlags = paramLong3;
    }
    
    public ExtraDirectoryInfo(Cursor paramCursor)
    {
      this.directoryId = CursorUtils.getLong(paramCursor, "_id", 0L);
      this.mediaAddedTime = CursorUtils.getLong(paramCursor, "date_media_added", 0L);
      this.oneplusFlags = CursorUtils.getLong(paramCursor, "oneplus_flags", 0L);
    }
    
    public ExtraDirectoryInfo clone()
    {
      try
      {
        ExtraDirectoryInfo localExtraDirectoryInfo = (ExtraDirectoryInfo)super.clone();
        return localExtraDirectoryInfo;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      return new ExtraDirectoryInfo(this.directoryId, this.mediaAddedTime, this.oneplusFlags);
    }
  }
  
  public static final class ExtraMediaInfo
    implements Cloneable
  {
    public static final String[] COLUMNS = GalleryDatabase.TABLE_MEDIA_COLUMNS;
    public volatile String addressAddressLine0;
    public volatile String addressAddressLine1;
    public volatile String addressAdminArea;
    public volatile String addressCountry;
    public volatile String addressFeature;
    public volatile double addressLatitude;
    public volatile String addressLocale;
    public volatile String addressLocality;
    public volatile double addressLongitude;
    public volatile String addressSubAdminArea;
    public volatile String addressSubLocality;
    public volatile long mediaId;
    public volatile long objectDetectionFileTime;
    public volatile List<GalleryDatabase.ObjectDetectionResult> objectDetectionResult;
    public volatile long objectDetectionVersion;
    public volatile long oneplusFlags;
    public volatile long sceneDetectionFileTime;
    public volatile List<GalleryDatabase.SceneDetectionResult> sceneDetectionResult;
    public volatile long sceneDetectionVersion;
    
    public ExtraMediaInfo() {}
    
    public ExtraMediaInfo(long paramLong)
    {
      this.mediaId = paramLong;
    }
    
    public ExtraMediaInfo(ContentValues paramContentValues)
    {
      Long localLong = paramContentValues.getAsLong("media_id");
      if (localLong != null)
      {
        this.mediaId = localLong.longValue();
        paramContentValues = paramContentValues.getAsInteger("oneplus_flags");
        if (paramContentValues != null) {}
      }
      else
      {
        throw new RuntimeException("No media ID");
      }
      this.oneplusFlags = paramContentValues.intValue();
    }
    
    public ExtraMediaInfo(Cursor paramCursor)
    {
      this.mediaId = CursorUtils.getLong(paramCursor, "media_id", 0L);
      this.oneplusFlags = CursorUtils.getLong(paramCursor, "oneplus_flags", 0L);
      this.addressLocale = CursorUtils.getString(paramCursor, "address_locale");
      this.addressLatitude = CursorUtils.getDouble(paramCursor, "address_latitude", 0.0D);
      this.addressLongitude = CursorUtils.getDouble(paramCursor, "address_longitude", 0.0D);
      this.addressCountry = CursorUtils.getString(paramCursor, "address_country");
      this.addressAdminArea = CursorUtils.getString(paramCursor, "address_admin_area");
      this.addressSubAdminArea = CursorUtils.getString(paramCursor, "address_sub_admin_area");
      this.addressLocality = CursorUtils.getString(paramCursor, "address_locality");
      this.addressSubLocality = CursorUtils.getString(paramCursor, "address_sub_locality");
      this.addressFeature = CursorUtils.getString(paramCursor, "address_feature");
      this.addressAddressLine0 = CursorUtils.getString(paramCursor, "address_address_line_0");
      this.addressAddressLine1 = CursorUtils.getString(paramCursor, "address_address_line_1");
      this.objectDetectionVersion = CursorUtils.getLong(paramCursor, "object_detection_version", 0L);
      this.objectDetectionFileTime = CursorUtils.getLong(paramCursor, "object_detection_file_time", 0L);
      String str = CursorUtils.getString(paramCursor, "object_detection_result");
      if (str == null)
      {
        this.sceneDetectionVersion = CursorUtils.getLong(paramCursor, "scene_detection_version", 0L);
        this.sceneDetectionFileTime = CursorUtils.getLong(paramCursor, "scene_detection_file_time", 0L);
        paramCursor = CursorUtils.getString(paramCursor, "scene_detection_result");
        if (paramCursor != null) {}
      }
      else
      {
        i = str.indexOf(']');
        j = 0;
        label220:
        if (i > 0) {
          if (this.objectDetectionResult == null) {
            break label273;
          }
        }
        for (;;)
        {
          this.objectDetectionResult.add(new GalleryDatabase.ObjectDetectionResult(str.substring(j, i + 1)));
          j = i + 1;
          i = str.indexOf(']', j);
          break label220;
          break;
          label273:
          this.objectDetectionResult = new ArrayList();
        }
      }
      int i = paramCursor.indexOf(']');
      int j = k;
      label297:
      if (i > 0) {
        if (this.sceneDetectionResult == null) {
          break label348;
        }
      }
      for (;;)
      {
        this.sceneDetectionResult.add(new GalleryDatabase.SceneDetectionResult(paramCursor.substring(j, i + 1)));
        j = i + 1;
        i = paramCursor.indexOf(']', j);
        break label297;
        break;
        label348:
        this.sceneDetectionResult = new ArrayList();
      }
    }
    
    private String serializeObjectDetectionResult()
    {
      int i = 0;
      if (this.objectDetectionResult == null) {}
      while (this.objectDetectionResult.isEmpty()) {
        return null;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int j = this.objectDetectionResult.size();
      while (i < j)
      {
        localStringBuilder.append(this.objectDetectionResult.get(i));
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    private String serializeSceneDetectionResult()
    {
      int i = 0;
      if (this.sceneDetectionResult == null) {}
      while (this.sceneDetectionResult.isEmpty()) {
        return null;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int j = this.sceneDetectionResult.size();
      while (i < j)
      {
        localStringBuilder.append(this.sceneDetectionResult.get(i));
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    public ExtraMediaInfo clone()
    {
      try
      {
        ExtraMediaInfo localExtraMediaInfo = (ExtraMediaInfo)super.clone();
        if (this.objectDetectionResult == null) {}
        while (this.sceneDetectionResult == null)
        {
          return localExtraMediaInfo;
          if (!this.objectDetectionResult.isEmpty()) {
            localExtraMediaInfo.objectDetectionResult = new ArrayList(this.objectDetectionResult);
          }
        }
        if (this.sceneDetectionResult.isEmpty()) {
          return localCloneNotSupportedException;
        }
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
      localCloneNotSupportedException.sceneDetectionResult = new ArrayList(this.sceneDetectionResult);
      return localCloneNotSupportedException;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ExtraMediaInfo)) {
        return false;
      }
      paramObject = (ExtraMediaInfo)paramObject;
      return (this.mediaId == ((ExtraMediaInfo)paramObject).mediaId) && (this.oneplusFlags == ((ExtraMediaInfo)paramObject).oneplusFlags);
    }
    
    public int hashCode()
    {
      return (int)this.mediaId;
    }
    
    public void toContentValues(ContentValues paramContentValues)
    {
      paramContentValues.put("media_id", Long.valueOf(this.mediaId));
      paramContentValues.put("oneplus_flags", Long.valueOf(this.oneplusFlags));
      paramContentValues.put("address_locale", this.addressLocale);
      if (Double.isNaN(this.addressLatitude)) {
        if (!Double.isNaN(this.addressLongitude)) {
          break label225;
        }
      }
      for (;;)
      {
        paramContentValues.put("address_country", this.addressCountry);
        paramContentValues.put("address_admin_area", this.addressAdminArea);
        paramContentValues.put("address_sub_admin_area", this.addressSubAdminArea);
        paramContentValues.put("address_locality", this.addressLocality);
        paramContentValues.put("address_sub_locality", this.addressSubLocality);
        paramContentValues.put("address_feature", this.addressFeature);
        paramContentValues.put("address_address_line_0", this.addressAddressLine0);
        paramContentValues.put("address_address_line_1", this.addressAddressLine1);
        paramContentValues.put("object_detection_version", Long.valueOf(this.objectDetectionVersion));
        paramContentValues.put("object_detection_file_time", Long.valueOf(this.objectDetectionFileTime));
        paramContentValues.put("object_detection_result", serializeObjectDetectionResult());
        paramContentValues.put("scene_detection_version", Long.valueOf(this.sceneDetectionVersion));
        paramContentValues.put("scene_detection_file_time", Long.valueOf(this.sceneDetectionFileTime));
        paramContentValues.put("scene_detection_result", serializeSceneDetectionResult());
        return;
        paramContentValues.put("address_latitude", Double.valueOf(this.addressLatitude));
        break;
        label225:
        paramContentValues.put("address_longitude", Double.valueOf(this.addressLongitude));
      }
    }
    
    public void toCursor(MatrixCursor paramMatrixCursor)
    {
      paramMatrixCursor.addRow(new Object[] { Long.valueOf(this.mediaId), Long.valueOf(this.oneplusFlags), this.addressLocale, Double.valueOf(this.addressLatitude), Double.valueOf(this.addressLongitude), this.addressCountry, this.addressAdminArea, this.addressSubAdminArea, this.addressLocality, this.addressSubLocality, this.addressFeature, this.addressAddressLine0, this.addressAddressLine1, Long.valueOf(this.objectDetectionVersion), Long.valueOf(this.objectDetectionFileTime), serializeObjectDetectionResult(), Long.valueOf(this.sceneDetectionVersion), Long.valueOf(this.sceneDetectionFileTime), serializeSceneDetectionResult() });
    }
  }
  
  public static final class ObjectDetectionResult
  {
    private static final int VERSION = 1;
    public volatile int bottom;
    public volatile float confidence;
    public volatile int left;
    public volatile int objectTypeId = -1;
    public volatile int right;
    public volatile int top;
    
    public ObjectDetectionResult() {}
    
    public ObjectDetectionResult(int paramInt1, float paramFloat, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.objectTypeId = paramInt1;
      this.confidence = paramFloat;
      this.left = paramInt2;
      this.top = paramInt3;
      this.right = paramInt4;
      this.bottom = paramInt5;
    }
    
    public ObjectDetectionResult(String paramString)
    {
      try
      {
        paramString = paramString.substring(1, paramString.length() - 1).split("\\,");
        this.objectTypeId = Integer.parseInt(paramString[1]);
        this.confidence = Float.parseFloat(paramString[2]);
        this.left = Integer.parseInt(paramString[3]);
        this.top = Integer.parseInt(paramString[4]);
        this.right = Integer.parseInt(paramString[5]);
        this.bottom = Integer.parseInt(paramString[6]);
        return;
      }
      catch (Throwable paramString) {}
    }
    
    public String toString()
    {
      return "[1," + this.objectTypeId + "," + this.confidence + "," + this.left + "," + this.top + "," + this.right + "," + this.bottom + "]";
    }
  }
  
  private static abstract interface QueryCallback
  {
    public abstract void onQuery(Cursor paramCursor);
  }
  
  public static final class SceneDetectionResult
  {
    private static final int VERSION = 1;
    public volatile float confidence;
    public volatile int sceneId = -1;
    
    public SceneDetectionResult() {}
    
    public SceneDetectionResult(int paramInt, float paramFloat)
    {
      this.sceneId = paramInt;
      this.confidence = paramFloat;
    }
    
    public SceneDetectionResult(String paramString)
    {
      try
      {
        paramString = paramString.substring(1, paramString.length() - 1).split("\\,");
        this.sceneId = Integer.parseInt(paramString[1]);
        this.confidence = Float.parseFloat(paramString[2]);
        return;
      }
      catch (Throwable paramString) {}
    }
    
    public String toString()
    {
      return "[1," + this.sceneId + "," + this.confidence + "]";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/GalleryDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */