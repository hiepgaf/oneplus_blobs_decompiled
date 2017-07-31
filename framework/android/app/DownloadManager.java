package android.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.Downloads.Impl;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadManager
{
  public static final String ACTION_DOWNLOAD_COMPLETE = "android.intent.action.DOWNLOAD_COMPLETE";
  public static final String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED";
  public static final String ACTION_VIEW_DOWNLOADS = "android.intent.action.VIEW_DOWNLOADS";
  public static final String COLUMN_ALLOW_WRITE = "allow_write";
  public static final String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";
  public static final String COLUMN_DESCRIPTION = "description";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";
  @Deprecated
  public static final String COLUMN_LOCAL_FILENAME = "local_filename";
  public static final String COLUMN_LOCAL_URI = "local_uri";
  public static final String COLUMN_MEDIAPROVIDER_URI = "mediaprovider_uri";
  public static final String COLUMN_MEDIA_TYPE = "media_type";
  public static final String COLUMN_REASON = "reason";
  public static final String COLUMN_STATUS = "status";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_TOTAL_SIZE_BYTES = "total_size";
  public static final String COLUMN_URI = "uri";
  public static final int ERROR_BLOCKED = 1010;
  public static final int ERROR_CANNOT_RESUME = 1008;
  public static final int ERROR_DEVICE_NOT_FOUND = 1007;
  public static final int ERROR_FILE_ALREADY_EXISTS = 1009;
  public static final int ERROR_FILE_ERROR = 1001;
  public static final int ERROR_HTTP_DATA_ERROR = 1004;
  public static final int ERROR_INSUFFICIENT_SPACE = 1006;
  public static final int ERROR_TOO_MANY_REDIRECTS = 1005;
  public static final int ERROR_UNHANDLED_HTTP_CODE = 1002;
  public static final int ERROR_UNKNOWN = 1000;
  public static final String EXTRA_DOWNLOAD_ID = "extra_download_id";
  public static final String EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS = "extra_click_download_ids";
  public static final String INTENT_EXTRAS_SORT_BY_SIZE = "android.app.DownloadManager.extra_sortBySize";
  private static final String NON_DOWNLOADMANAGER_DOWNLOAD = "non-dwnldmngr-download-dont-retry2download";
  public static final int PAUSED_QUEUED_FOR_WIFI = 3;
  public static final int PAUSED_UNKNOWN = 4;
  public static final int PAUSED_WAITING_FOR_NETWORK = 2;
  public static final int PAUSED_WAITING_TO_RETRY = 1;
  public static final int STATUS_FAILED = 16;
  public static final int STATUS_PAUSED = 4;
  public static final int STATUS_PENDING = 1;
  public static final int STATUS_RUNNING = 2;
  public static final int STATUS_SUCCESSFUL = 8;
  public static final String[] UNDERLYING_COLUMNS = { "_id", "_data AS local_filename", "mediaprovider_uri", "destination", "title", "description", "uri", "status", "hint", "mimetype AS media_type", "total_bytes AS total_size", "lastmod AS last_modified_timestamp", "current_bytes AS bytes_so_far", "allow_write", "'placeholder' AS local_uri", "'placeholder' AS reason" };
  private boolean mAccessFilename;
  private Uri mBaseUri = Downloads.Impl.CONTENT_URI;
  private final String mPackageName;
  private final ContentResolver mResolver;
  
  public DownloadManager(Context paramContext)
  {
    this.mResolver = paramContext.getContentResolver();
    this.mPackageName = paramContext.getPackageName();
    if (paramContext.getApplicationInfo().targetSdkVersion < 24) {}
    for (boolean bool = true;; bool = false)
    {
      this.mAccessFilename = bool;
      return;
    }
  }
  
  public static long getActiveNetworkWarningBytes(Context paramContext)
  {
    return -1L;
  }
  
  public static Long getMaxBytesOverMobile(Context paramContext)
  {
    try
    {
      long l = Settings.Global.getLong(paramContext.getContentResolver(), "download_manager_max_bytes_over_mobile");
      return Long.valueOf(l);
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return null;
  }
  
  public static Long getRecommendedMaxBytesOverMobile(Context paramContext)
  {
    try
    {
      long l = Settings.Global.getLong(paramContext.getContentResolver(), "download_manager_recommended_max_bytes_over_mobile");
      return Long.valueOf(l);
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return null;
  }
  
  static String[] getWhereArgsForIds(long[] paramArrayOfLong)
  {
    String[] arrayOfString = new String[paramArrayOfLong.length];
    int i = 0;
    while (i < paramArrayOfLong.length)
    {
      arrayOfString[i] = Long.toString(paramArrayOfLong[i]);
      i += 1;
    }
    return arrayOfString;
  }
  
  static String getWhereClauseForIds(long[] paramArrayOfLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    int i = 0;
    while (i < paramArrayOfLong.length)
    {
      if (i > 0) {
        localStringBuilder.append("OR ");
      }
      localStringBuilder.append("_id");
      localStringBuilder.append(" = ? ");
      i += 1;
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public static boolean isActiveNetworkExpensive(Context paramContext)
  {
    return false;
  }
  
  private static void validateArgumentIsNonEmpty(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException(paramString1 + " can't be null");
    }
  }
  
  public long addCompletedDownload(String paramString1, String paramString2, boolean paramBoolean1, String paramString3, String paramString4, long paramLong, boolean paramBoolean2)
  {
    return addCompletedDownload(paramString1, paramString2, paramBoolean1, paramString3, paramString4, paramLong, paramBoolean2, false, null, null);
  }
  
  public long addCompletedDownload(String paramString1, String paramString2, boolean paramBoolean1, String paramString3, String paramString4, long paramLong, boolean paramBoolean2, Uri paramUri1, Uri paramUri2)
  {
    return addCompletedDownload(paramString1, paramString2, paramBoolean1, paramString3, paramString4, paramLong, paramBoolean2, false, paramUri1, paramUri2);
  }
  
  public long addCompletedDownload(String paramString1, String paramString2, boolean paramBoolean1, String paramString3, String paramString4, long paramLong, boolean paramBoolean2, boolean paramBoolean3)
  {
    return addCompletedDownload(paramString1, paramString2, paramBoolean1, paramString3, paramString4, paramLong, paramBoolean2, paramBoolean3, null, null);
  }
  
  public long addCompletedDownload(String paramString1, String paramString2, boolean paramBoolean1, String paramString3, String paramString4, long paramLong, boolean paramBoolean2, boolean paramBoolean3, Uri paramUri1, Uri paramUri2)
  {
    validateArgumentIsNonEmpty("title", paramString1);
    validateArgumentIsNonEmpty("description", paramString2);
    validateArgumentIsNonEmpty("path", paramString4);
    validateArgumentIsNonEmpty("mimeType", paramString3);
    if (paramLong < 0L) {
      throw new IllegalArgumentException(" invalid value for param: totalBytes");
    }
    if (paramUri1 != null)
    {
      paramUri1 = new Request(paramUri1);
      paramUri1.setTitle(paramString1).setDescription(paramString2).setMimeType(paramString3);
      if (paramUri2 != null) {
        paramUri1.addRequestHeader("Referer", paramUri2.toString());
      }
      paramString1 = paramUri1.toContentValues(null);
      paramString1.put("destination", Integer.valueOf(6));
      paramString1.put("_data", paramString4);
      paramString1.put("status", Integer.valueOf(200));
      paramString1.put("total_bytes", Long.valueOf(paramLong));
      if (!paramBoolean1) {
        break label240;
      }
      i = 0;
      label155:
      paramString1.put("scanned", Integer.valueOf(i));
      if (!paramBoolean2) {
        break label246;
      }
      i = 3;
      label175:
      paramString1.put("visibility", Integer.valueOf(i));
      if (!paramBoolean3) {
        break label252;
      }
    }
    label240:
    label246:
    label252:
    for (int i = 1;; i = 0)
    {
      paramString1.put("allow_write", Integer.valueOf(i));
      paramString1 = this.mResolver.insert(Downloads.Impl.CONTENT_URI, paramString1);
      if (paramString1 != null) {
        break label258;
      }
      return -1L;
      paramUri1 = new Request("non-dwnldmngr-download-dont-retry2download");
      break;
      i = 2;
      break label155;
      i = 2;
      break label175;
    }
    label258:
    return Long.parseLong(paramString1.getLastPathSegment());
  }
  
  public long enqueue(Request paramRequest)
  {
    paramRequest = paramRequest.toContentValues(this.mPackageName);
    return Long.parseLong(this.mResolver.insert(Downloads.Impl.CONTENT_URI, paramRequest).getLastPathSegment());
  }
  
  public void forceDownload(long... paramVarArgs)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("status", Integer.valueOf(190));
    localContentValues.put("control", Integer.valueOf(0));
    localContentValues.put("bypass_recommended_size_limit", Integer.valueOf(1));
    this.mResolver.update(this.mBaseUri, localContentValues, getWhereClauseForIds(paramVarArgs), getWhereArgsForIds(paramVarArgs));
  }
  
  public Uri getDownloadUri(long paramLong)
  {
    return ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, paramLong);
  }
  
  public String getMimeTypeForDownloadedFile(long paramLong)
  {
    Object localObject2 = new Query().setFilterById(new long[] { paramLong });
    Object localObject1 = null;
    try
    {
      localObject2 = query((Query)localObject2);
      if (localObject2 == null) {
        return null;
      }
      localObject1 = localObject2;
      if (((Cursor)localObject2).moveToFirst())
      {
        localObject1 = localObject2;
        String str = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndexOrThrow("media_type"));
        return str;
      }
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  public Uri getUriForDownloadedFile(long paramLong)
  {
    Object localObject2 = new Query().setFilterById(new long[] { paramLong });
    Object localObject1 = null;
    try
    {
      localObject2 = query((Query)localObject2);
      if (localObject2 == null) {
        return null;
      }
      localObject1 = localObject2;
      if (((Cursor)localObject2).moveToFirst())
      {
        localObject1 = localObject2;
        if (8 == ((Cursor)localObject2).getInt(((Cursor)localObject2).getColumnIndexOrThrow("status")))
        {
          localObject1 = localObject2;
          Uri localUri = ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, paramLong);
          return localUri;
        }
      }
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  public int markRowDeleted(long... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw new IllegalArgumentException("input param 'ids' can't be null");
    }
    return this.mResolver.delete(this.mBaseUri, getWhereClauseForIds(paramVarArgs), getWhereArgsForIds(paramVarArgs));
  }
  
  public ParcelFileDescriptor openDownloadedFile(long paramLong)
    throws FileNotFoundException
  {
    return this.mResolver.openFileDescriptor(getDownloadUri(paramLong), "r");
  }
  
  public Cursor query(Query paramQuery)
  {
    paramQuery = paramQuery.runQuery(this.mResolver, UNDERLYING_COLUMNS, this.mBaseUri);
    if (paramQuery == null) {
      return null;
    }
    return new CursorTranslator(paramQuery, this.mBaseUri, this.mAccessFilename);
  }
  
  public int remove(long... paramVarArgs)
  {
    return markRowDeleted(paramVarArgs);
  }
  
  public boolean rename(Context paramContext, long paramLong, String paramString)
  {
    if (!FileUtils.isValidFatFilename(paramString)) {
      throw new SecurityException(paramString + " is not a valid filename");
    }
    Object localObject2 = new Query().setFilterById(new long[] { paramLong });
    Object localObject1 = null;
    String str2 = null;
    String str1 = null;
    try
    {
      localObject2 = query((Query)localObject2);
      if (localObject2 == null) {
        return false;
      }
      localObject1 = localObject2;
      if (((Cursor)localObject2).moveToFirst())
      {
        localObject1 = localObject2;
        int i = ((Cursor)localObject2).getInt(((Cursor)localObject2).getColumnIndexOrThrow("status"));
        if (8 != i) {
          return false;
        }
        localObject1 = localObject2;
        str2 = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndexOrThrow("title"));
        localObject1 = localObject2;
        str1 = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndexOrThrow("media_type"));
      }
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
      if ((str2 == null) || (str1 == null)) {
        throw new IllegalStateException("Document with id " + paramLong + " does not exist");
      }
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
    localObject1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    localObject2 = new File((File)localObject1, str2);
    localObject1 = new File((File)localObject1, paramString);
    if (((File)localObject1).exists()) {
      throw new IllegalStateException("Already exists " + localObject1);
    }
    if (!((File)localObject2).renameTo((File)localObject1)) {
      throw new IllegalStateException("Failed to rename to " + localObject1);
    }
    if (str1.startsWith("image/"))
    {
      paramContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_data=?", new String[] { ((File)localObject2).getAbsolutePath() });
      localObject2 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
      ((Intent)localObject2).setData(Uri.fromFile((File)localObject1));
      paramContext.sendBroadcast((Intent)localObject2);
    }
    paramContext = new ContentValues();
    paramContext.put("title", paramString);
    paramContext.put("_data", ((File)localObject1).toString());
    paramContext.putNull("mediaprovider_uri");
    paramString = new long[1];
    paramString[0] = paramLong;
    return this.mResolver.update(this.mBaseUri, paramContext, getWhereClauseForIds(paramString), getWhereArgsForIds(paramString)) == 1;
  }
  
  public void restartDownload(long... paramVarArgs)
  {
    Object localObject = query(new Query().setFilterById(paramVarArgs));
    for (;;)
    {
      try
      {
        ((Cursor)localObject).moveToFirst();
        if (((Cursor)localObject).isAfterLast()) {
          break;
        }
        int i = ((Cursor)localObject).getInt(((Cursor)localObject).getColumnIndex("status"));
        if ((i != 8) && (i != 16)) {
          throw new IllegalArgumentException("Cannot restart incomplete download: " + ((Cursor)localObject).getLong(((Cursor)localObject).getColumnIndex("_id")));
        }
      }
      finally
      {
        ((Cursor)localObject).close();
      }
      ((Cursor)localObject).moveToNext();
    }
    ((Cursor)localObject).close();
    localObject = new ContentValues();
    ((ContentValues)localObject).put("current_bytes", Integer.valueOf(0));
    ((ContentValues)localObject).put("total_bytes", Integer.valueOf(-1));
    ((ContentValues)localObject).putNull("_data");
    ((ContentValues)localObject).put("status", Integer.valueOf(190));
    ((ContentValues)localObject).put("numfailed", Integer.valueOf(0));
    this.mResolver.update(this.mBaseUri, (ContentValues)localObject, getWhereClauseForIds(paramVarArgs), getWhereArgsForIds(paramVarArgs));
  }
  
  public void setAccessAllDownloads(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mBaseUri = Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI;
      return;
    }
    this.mBaseUri = Downloads.Impl.CONTENT_URI;
  }
  
  public void setAccessFilename(boolean paramBoolean)
  {
    this.mAccessFilename = paramBoolean;
  }
  
  private static class CursorTranslator
    extends CursorWrapper
  {
    private final boolean mAccessFilename;
    private final Uri mBaseUri;
    
    static
    {
      if (CursorTranslator.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public CursorTranslator(Cursor paramCursor, Uri paramUri, boolean paramBoolean)
    {
      super();
      this.mBaseUri = paramUri;
      this.mAccessFilename = paramBoolean;
    }
    
    private long getErrorCode(int paramInt)
    {
      if ((400 <= paramInt) && (paramInt < 488)) {}
      while ((500 <= paramInt) && (paramInt < 600)) {
        return paramInt;
      }
      switch (paramInt)
      {
      default: 
        return 1000L;
      case 492: 
        return 1001L;
      case 493: 
      case 494: 
        return 1002L;
      case 495: 
        return 1004L;
      case 497: 
        return 1005L;
      case 198: 
        return 1006L;
      case 199: 
        return 1007L;
      case 489: 
        return 1008L;
      }
      return 1009L;
    }
    
    private String getLocalUri()
    {
      long l = getLong(getColumnIndex("destination"));
      if ((l == 4L) || (l == 0L)) {}
      String str;
      while (l == 6L)
      {
        str = super.getString(getColumnIndex("local_filename"));
        if (str != null) {
          break;
        }
        return null;
      }
      l = getLong(getColumnIndex("_id"));
      return ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, l).toString();
      return Uri.fromFile(new File(str)).toString();
    }
    
    private long getPausedReason(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 4L;
      case 194: 
        return 1L;
      case 195: 
        return 2L;
      }
      return 3L;
    }
    
    private long getReason(int paramInt)
    {
      switch (translateStatus(paramInt))
      {
      default: 
        return 0L;
      case 16: 
        return getErrorCode(paramInt);
      }
      return getPausedReason(paramInt);
    }
    
    private int translateStatus(int paramInt)
    {
      switch (paramInt)
      {
      case 191: 
      case 197: 
      case 198: 
      case 199: 
      default: 
        if ((!-assertionsDisabled) && (!Downloads.Impl.isStatusError(paramInt))) {
          throw new AssertionError();
        }
        break;
      case 190: 
        return 1;
      case 192: 
        return 2;
      case 193: 
      case 194: 
      case 195: 
      case 196: 
        return 4;
      case 200: 
        return 8;
      }
      return 16;
    }
    
    public int getInt(int paramInt)
    {
      return (int)getLong(paramInt);
    }
    
    public long getLong(int paramInt)
    {
      if (getColumnName(paramInt).equals("reason")) {
        return getReason(super.getInt(getColumnIndex("status")));
      }
      if (getColumnName(paramInt).equals("status")) {
        return translateStatus(super.getInt(getColumnIndex("status")));
      }
      return super.getLong(paramInt);
    }
    
    public String getString(int paramInt)
    {
      String str = getColumnName(paramInt);
      if (str.equals("local_uri")) {
        return getLocalUri();
      }
      if ((str.equals("local_filename")) && (!this.mAccessFilename)) {
        throw new SecurityException("COLUMN_LOCAL_FILENAME is deprecated; use ContentResolver.openFileDescriptor() instead");
      }
      return super.getString(paramInt);
    }
  }
  
  public static class Query
  {
    public static final int ORDER_ASCENDING = 1;
    public static final int ORDER_DESCENDING = 2;
    private long[] mIds = null;
    private boolean mOnlyIncludeVisibleInDownloadsUi = false;
    private String mOrderByColumn = "lastmod";
    private int mOrderDirection = 2;
    private Integer mStatusFlags = null;
    
    private String joinStrings(String paramString, Iterable<String> paramIterable)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 1;
      paramIterable = paramIterable.iterator();
      while (paramIterable.hasNext())
      {
        String str = (String)paramIterable.next();
        if (i == 0) {
          localStringBuilder.append(paramString);
        }
        localStringBuilder.append(str);
        i = 0;
      }
      return localStringBuilder.toString();
    }
    
    private String statusClause(String paramString, int paramInt)
    {
      return "status" + paramString + "'" + paramInt + "'";
    }
    
    public Query orderBy(String paramString, int paramInt)
    {
      if ((paramInt != 1) && (paramInt != 2)) {
        throw new IllegalArgumentException("Invalid direction: " + paramInt);
      }
      if (paramString.equals("last_modified_timestamp")) {}
      for (this.mOrderByColumn = "lastmod";; this.mOrderByColumn = "total_bytes")
      {
        this.mOrderDirection = paramInt;
        return this;
        if (!paramString.equals("total_size")) {
          break;
        }
      }
      throw new IllegalArgumentException("Cannot order by " + paramString);
    }
    
    Cursor runQuery(ContentResolver paramContentResolver, String[] paramArrayOfString, Uri paramUri)
    {
      Object localObject1 = new ArrayList();
      String[] arrayOfString = null;
      if (this.mIds != null)
      {
        ((List)localObject1).add(DownloadManager.getWhereClauseForIds(this.mIds));
        arrayOfString = DownloadManager.getWhereArgsForIds(this.mIds);
      }
      if (this.mStatusFlags != null)
      {
        localObject2 = new ArrayList();
        if ((this.mStatusFlags.intValue() & 0x1) != 0) {
          ((List)localObject2).add(statusClause("=", 190));
        }
        if ((this.mStatusFlags.intValue() & 0x2) != 0) {
          ((List)localObject2).add(statusClause("=", 192));
        }
        if ((this.mStatusFlags.intValue() & 0x4) != 0)
        {
          ((List)localObject2).add(statusClause("=", 193));
          ((List)localObject2).add(statusClause("=", 194));
          ((List)localObject2).add(statusClause("=", 195));
          ((List)localObject2).add(statusClause("=", 196));
        }
        if ((this.mStatusFlags.intValue() & 0x8) != 0) {
          ((List)localObject2).add(statusClause("=", 200));
        }
        if ((this.mStatusFlags.intValue() & 0x10) != 0) {
          ((List)localObject2).add("(" + statusClause(">=", 400) + " AND " + statusClause("<", 600) + ")");
        }
        ((List)localObject1).add(joinStrings(" OR ", (Iterable)localObject2));
      }
      if (this.mOnlyIncludeVisibleInDownloadsUi) {
        ((List)localObject1).add("is_visible_in_downloads_ui != '0'");
      }
      ((List)localObject1).add("deleted != '1'");
      Object localObject2 = joinStrings(" AND ", (Iterable)localObject1);
      if (this.mOrderDirection == 1) {}
      for (localObject1 = "ASC";; localObject1 = "DESC") {
        return paramContentResolver.query(paramUri, paramArrayOfString, (String)localObject2, arrayOfString, this.mOrderByColumn + " " + (String)localObject1);
      }
    }
    
    public Query setFilterById(long... paramVarArgs)
    {
      this.mIds = paramVarArgs;
      return this;
    }
    
    public Query setFilterByStatus(int paramInt)
    {
      this.mStatusFlags = Integer.valueOf(paramInt);
      return this;
    }
    
    public Query setOnlyIncludeVisibleInDownloadsUi(boolean paramBoolean)
    {
      this.mOnlyIncludeVisibleInDownloadsUi = paramBoolean;
      return this;
    }
  }
  
  public static class Request
  {
    @Deprecated
    public static final int NETWORK_BLUETOOTH = 4;
    public static final int NETWORK_MOBILE = 1;
    public static final int NETWORK_WIFI = 2;
    private static final int SCANNABLE_VALUE_NO = 2;
    private static final int SCANNABLE_VALUE_YES = 0;
    public static final int VISIBILITY_HIDDEN = 2;
    public static final int VISIBILITY_VISIBLE = 0;
    public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;
    public static final int VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3;
    private int mAllowedNetworkTypes = -1;
    private CharSequence mDescription;
    private Uri mDestinationUri;
    private int mFlags = 0;
    private boolean mIsVisibleInDownloadsUi = true;
    private boolean mMeteredAllowed = true;
    private String mMimeType;
    private int mNotificationVisibility = 0;
    private List<Pair<String, String>> mRequestHeaders = new ArrayList();
    private boolean mRoamingAllowed = true;
    private boolean mScannable = false;
    private CharSequence mTitle;
    private Uri mUri;
    private boolean mUseSystemCache = false;
    
    static
    {
      if (Request.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public Request(Uri paramUri)
    {
      if (paramUri == null) {
        throw new NullPointerException();
      }
      String str = paramUri.getScheme();
      if ((str != null) && ((str.equals("http")) || (str.equals("https"))))
      {
        this.mUri = paramUri;
        return;
      }
      throw new IllegalArgumentException("Can only download HTTP/HTTPS URIs: " + paramUri);
    }
    
    Request(String paramString)
    {
      this.mUri = Uri.parse(paramString);
    }
    
    private void encodeHttpHeaders(ContentValues paramContentValues)
    {
      int i = 0;
      Iterator localIterator = this.mRequestHeaders.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (Pair)localIterator.next();
        localObject = (String)((Pair)localObject).first + ": " + (String)((Pair)localObject).second;
        paramContentValues.put("http_header_" + i, (String)localObject);
        i += 1;
      }
    }
    
    private void putIfNonNull(ContentValues paramContentValues, String paramString, Object paramObject)
    {
      if (paramObject != null) {
        paramContentValues.put(paramString, paramObject.toString());
      }
    }
    
    private void setDestinationFromBase(File paramFile, String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException("subPath cannot be null");
      }
      this.mDestinationUri = Uri.withAppendedPath(Uri.fromFile(paramFile), paramString);
    }
    
    public Request addRequestHeader(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        throw new NullPointerException("header cannot be null");
      }
      if (paramString1.contains(":")) {
        throw new IllegalArgumentException("header may not contain ':'");
      }
      String str = paramString2;
      if (paramString2 == null) {
        str = "";
      }
      this.mRequestHeaders.add(Pair.create(paramString1, str));
      return this;
    }
    
    public void allowScanningByMediaScanner()
    {
      this.mScannable = true;
    }
    
    public Request setAllowedNetworkTypes(int paramInt)
    {
      this.mAllowedNetworkTypes = paramInt;
      return this;
    }
    
    public Request setAllowedOverMetered(boolean paramBoolean)
    {
      this.mMeteredAllowed = paramBoolean;
      return this;
    }
    
    public Request setAllowedOverRoaming(boolean paramBoolean)
    {
      this.mRoamingAllowed = paramBoolean;
      return this;
    }
    
    public Request setDescription(CharSequence paramCharSequence)
    {
      this.mDescription = paramCharSequence;
      return this;
    }
    
    public Request setDestinationInExternalFilesDir(Context paramContext, String paramString1, String paramString2)
    {
      paramContext = paramContext.getExternalFilesDir(paramString1);
      if (paramContext == null) {
        throw new IllegalStateException("Failed to get external storage files directory");
      }
      if (paramContext.exists())
      {
        if (!paramContext.isDirectory()) {
          throw new IllegalStateException(paramContext.getAbsolutePath() + " already exists and is not a directory");
        }
      }
      else if (!paramContext.mkdirs()) {
        throw new IllegalStateException("Unable to create directory: " + paramContext.getAbsolutePath());
      }
      setDestinationFromBase(paramContext, paramString2);
      return this;
    }
    
    public Request setDestinationInExternalPublicDir(String paramString1, String paramString2)
    {
      paramString1 = Environment.getExternalStoragePublicDirectory(paramString1);
      if (paramString1 == null) {
        throw new IllegalStateException("Failed to get external storage public directory");
      }
      if (paramString1.exists())
      {
        if (!paramString1.isDirectory()) {
          throw new IllegalStateException(paramString1.getAbsolutePath() + " already exists and is not a directory");
        }
      }
      else if (!paramString1.mkdirs()) {
        throw new IllegalStateException("Unable to create directory: " + paramString1.getAbsolutePath());
      }
      setDestinationFromBase(paramString1, paramString2);
      return this;
    }
    
    public Request setDestinationToSystemCache()
    {
      this.mUseSystemCache = true;
      return this;
    }
    
    public Request setDestinationUri(Uri paramUri)
    {
      this.mDestinationUri = paramUri;
      return this;
    }
    
    public Request setMimeType(String paramString)
    {
      this.mMimeType = paramString;
      return this;
    }
    
    public Request setNotificationVisibility(int paramInt)
    {
      this.mNotificationVisibility = paramInt;
      return this;
    }
    
    public Request setRequiresCharging(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= 0x1;
        return this;
      }
      this.mFlags &= 0xFFFFFFFE;
      return this;
    }
    
    public Request setRequiresDeviceIdle(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= 0x2;
        return this;
      }
      this.mFlags &= 0xFFFFFFFD;
      return this;
    }
    
    @Deprecated
    public Request setShowRunningNotification(boolean paramBoolean)
    {
      if (paramBoolean) {
        return setNotificationVisibility(0);
      }
      return setNotificationVisibility(2);
    }
    
    public Request setTitle(CharSequence paramCharSequence)
    {
      this.mTitle = paramCharSequence;
      return this;
    }
    
    public Request setVisibleInDownloadsUi(boolean paramBoolean)
    {
      this.mIsVisibleInDownloadsUi = paramBoolean;
      return this;
    }
    
    ContentValues toContentValues(String paramString)
    {
      int j = 2;
      ContentValues localContentValues = new ContentValues();
      if (!-assertionsDisabled)
      {
        if (this.mUri != null) {}
        for (i = 1; i == 0; i = 0) {
          throw new AssertionError();
        }
      }
      localContentValues.put("uri", this.mUri.toString());
      localContentValues.put("is_public_api", Boolean.valueOf(true));
      localContentValues.put("notificationpackage", paramString);
      if (this.mDestinationUri != null)
      {
        localContentValues.put("destination", Integer.valueOf(4));
        localContentValues.put("hint", this.mDestinationUri.toString());
        i = j;
        if (this.mScannable) {
          i = 0;
        }
        localContentValues.put("scanned", Integer.valueOf(i));
        if (!this.mRequestHeaders.isEmpty()) {
          encodeHttpHeaders(localContentValues);
        }
        putIfNonNull(localContentValues, "title", this.mTitle);
        putIfNonNull(localContentValues, "description", this.mDescription);
        putIfNonNull(localContentValues, "mimetype", this.mMimeType);
        localContentValues.put("visibility", Integer.valueOf(this.mNotificationVisibility));
        localContentValues.put("allowed_network_types", Integer.valueOf(this.mAllowedNetworkTypes));
        localContentValues.put("allow_roaming", Boolean.valueOf(this.mRoamingAllowed));
        localContentValues.put("allow_metered", Boolean.valueOf(this.mMeteredAllowed));
        localContentValues.put("flags", Integer.valueOf(this.mFlags));
        localContentValues.put("is_visible_in_downloads_ui", Boolean.valueOf(this.mIsVisibleInDownloadsUi));
        return localContentValues;
      }
      if (this.mUseSystemCache) {}
      for (int i = 5;; i = 2)
      {
        localContentValues.put("destination", Integer.valueOf(i));
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/DownloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */