package android.content;

import android.accounts.Account;
import android.annotation.RequiresPermission.Read;
import android.annotation.RequiresPermission.Write;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.database.CrossProcessCursorWrapper;
import android.database.Cursor;
import android.database.IContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.util.SeempLog;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.MimeIconUtils;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ContentResolver
{
  public static final Intent ACTION_SYNC_CONN_STATUS_CHANGED = new Intent("com.android.sync.SYNC_CONN_STATUS_CHANGED");
  public static final String ANY_CURSOR_ITEM_TYPE = "vnd.android.cursor.item/*";
  public static final String CONTENT_SERVICE_NAME = "content";
  public static final String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir";
  public static final String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item";
  private static final boolean DBG = Build.DEBUG_ONEPLUS | DBG_ALL;
  private static final boolean DBG_ALL;
  private static final boolean DBG_DUMP_STACK;
  private static final boolean ENABLE_CONTENT_SAMPLE = false;
  public static final String EXTRA_SIZE = "android.content.extra.SIZE";
  public static final int NOTIFY_SKIP_NOTIFY_FOR_DESCENDANTS = 2;
  public static final int NOTIFY_SYNC_TO_NETWORK = 1;
  private static final boolean SAVE_DBG_MSG = ContentDebugUtils.SAVE_DBG_MSG;
  public static final String SCHEME_ANDROID_RESOURCE = "android.resource";
  public static final String SCHEME_CONTENT = "content";
  public static final String SCHEME_FILE = "file";
  private static final int SLOW_THRESHOLD_MILLIS = 500;
  public static final int SYNC_ERROR_AUTHENTICATION = 2;
  public static final int SYNC_ERROR_CONFLICT = 5;
  public static final int SYNC_ERROR_INTERNAL = 8;
  public static final int SYNC_ERROR_IO = 3;
  private static final String[] SYNC_ERROR_NAMES = { "already-in-progress", "authentication-error", "io-error", "parse-error", "conflict", "too-many-deletions", "too-many-retries", "internal-error" };
  public static final int SYNC_ERROR_PARSE = 4;
  public static final int SYNC_ERROR_SYNC_ALREADY_IN_PROGRESS = 1;
  public static final int SYNC_ERROR_TOO_MANY_DELETIONS = 6;
  public static final int SYNC_ERROR_TOO_MANY_RETRIES = 7;
  @Deprecated
  public static final String SYNC_EXTRAS_ACCOUNT = "account";
  public static final String SYNC_EXTRAS_DISALLOW_METERED = "allow_metered";
  public static final String SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = "discard_deletions";
  public static final String SYNC_EXTRAS_DO_NOT_RETRY = "do_not_retry";
  public static final String SYNC_EXTRAS_EXPECTED_DOWNLOAD = "expected_download";
  public static final String SYNC_EXTRAS_EXPECTED_UPLOAD = "expected_upload";
  public static final String SYNC_EXTRAS_EXPEDITED = "expedited";
  @Deprecated
  public static final String SYNC_EXTRAS_FORCE = "force";
  public static final String SYNC_EXTRAS_IGNORE_BACKOFF = "ignore_backoff";
  public static final String SYNC_EXTRAS_IGNORE_SETTINGS = "ignore_settings";
  public static final String SYNC_EXTRAS_INITIALIZE = "initialize";
  public static final String SYNC_EXTRAS_MANUAL = "force";
  public static final String SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = "deletions_override";
  public static final String SYNC_EXTRAS_PRIORITY = "sync_priority";
  public static final String SYNC_EXTRAS_REQUIRE_CHARGING = "require_charging";
  public static final String SYNC_EXTRAS_UPLOAD = "upload";
  public static final int SYNC_OBSERVER_TYPE_ACTIVE = 4;
  public static final int SYNC_OBSERVER_TYPE_ALL = Integer.MAX_VALUE;
  public static final int SYNC_OBSERVER_TYPE_PENDING = 2;
  public static final int SYNC_OBSERVER_TYPE_SETTINGS = 1;
  public static final int SYNC_OBSERVER_TYPE_STATUS = 8;
  private static final String TAG = "ContentResolver";
  private static IContentService sContentService;
  private final Context mContext;
  final String mPackageName;
  private final Random mRandom = new Random();
  
  static
  {
    DBG_ALL = ContentDebugUtils.DBG_ALL;
    DBG_DUMP_STACK = ContentDebugUtils.DBG_DUMP_STACK;
  }
  
  public ContentResolver(Context paramContext)
  {
    if (paramContext != null) {}
    for (;;)
    {
      this.mContext = paramContext;
      this.mPackageName = this.mContext.getOpPackageName();
      return;
      paramContext = ActivityThread.currentApplication();
    }
  }
  
  public static void addPeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong)
  {
    validateSyncExtrasBundle(paramBundle);
    if ((paramBundle.getBoolean("force", false)) || (paramBundle.getBoolean("do_not_retry", false)) || (paramBundle.getBoolean("ignore_backoff", false)) || (paramBundle.getBoolean("ignore_settings", false)) || (paramBundle.getBoolean("initialize", false)) || (paramBundle.getBoolean("force", false)) || (paramBundle.getBoolean("expedited", false))) {
      throw new IllegalArgumentException("illegal extras were set");
    }
    try
    {
      getContentService().addPeriodicSync(paramAccount, paramString, paramBundle, paramLong);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  public static Object addStatusChangeListener(int paramInt, SyncStatusObserver paramSyncStatusObserver)
  {
    if (paramSyncStatusObserver == null) {
      throw new IllegalArgumentException("you passed in a null callback");
    }
    try
    {
      paramSyncStatusObserver = new ISyncStatusObserver.Stub()
      {
        public void onStatusChanged(int paramAnonymousInt)
          throws RemoteException
        {
          this.val$callback.onStatusChanged(paramAnonymousInt);
        }
      };
      getContentService().addStatusChangeListener(paramInt, paramSyncStatusObserver);
      return paramSyncStatusObserver;
    }
    catch (RemoteException paramSyncStatusObserver)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramSyncStatusObserver);
    }
  }
  
  public static void cancelSync(Account paramAccount, String paramString)
  {
    try
    {
      getContentService().cancelSync(paramAccount, paramString, null);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  public static void cancelSync(SyncRequest paramSyncRequest)
  {
    if (paramSyncRequest == null) {
      throw new IllegalArgumentException("request cannot be null");
    }
    try
    {
      getContentService().cancelRequest(paramSyncRequest);
      return;
    }
    catch (RemoteException paramSyncRequest) {}
  }
  
  public static void cancelSyncAsUser(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      getContentService().cancelSyncAsUser(paramAccount, paramString, null, paramInt);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  public static IContentService getContentService()
  {
    if (sContentService != null) {
      return sContentService;
    }
    sContentService = IContentService.Stub.asInterface(ServiceManager.getService("content"));
    return sContentService;
  }
  
  @Deprecated
  public static SyncInfo getCurrentSync()
  {
    try
    {
      Object localObject = getContentService().getCurrentSyncs();
      if (((List)localObject).isEmpty()) {
        return null;
      }
      localObject = (SyncInfo)((List)localObject).get(0);
      return (SyncInfo)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static List<SyncInfo> getCurrentSyncs()
  {
    try
    {
      List localList = getContentService().getCurrentSyncs();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static List<SyncInfo> getCurrentSyncsAsUser(int paramInt)
  {
    try
    {
      List localList = getContentService().getCurrentSyncsAsUser(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static int getIsSyncable(Account paramAccount, String paramString)
  {
    try
    {
      int i = getContentService().getIsSyncable(paramAccount, paramString);
      return i;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static int getIsSyncableAsUser(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      paramInt = getContentService().getIsSyncableAsUser(paramAccount, paramString, paramInt);
      return paramInt;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static boolean getMasterSyncAutomatically()
  {
    try
    {
      boolean bool = getContentService().getMasterSyncAutomatically();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static boolean getMasterSyncAutomaticallyAsUser(int paramInt)
  {
    try
    {
      boolean bool = getContentService().getMasterSyncAutomaticallyAsUser(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static List<PeriodicSync> getPeriodicSyncs(Account paramAccount, String paramString)
  {
    try
    {
      paramAccount = getContentService().getPeriodicSyncs(paramAccount, paramString, null);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static String[] getSyncAdapterPackagesForAuthorityAsUser(String paramString, int paramInt)
  {
    try
    {
      paramString = getContentService().getSyncAdapterPackagesForAuthorityAsUser(paramString, paramInt);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return (String[])ArrayUtils.emptyArray(String.class);
  }
  
  public static SyncAdapterType[] getSyncAdapterTypes()
  {
    try
    {
      SyncAdapterType[] arrayOfSyncAdapterType = getContentService().getSyncAdapterTypes();
      return arrayOfSyncAdapterType;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static SyncAdapterType[] getSyncAdapterTypesAsUser(int paramInt)
  {
    try
    {
      SyncAdapterType[] arrayOfSyncAdapterType = getContentService().getSyncAdapterTypesAsUser(paramInt);
      return arrayOfSyncAdapterType;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("the ContentService should always be reachable", localRemoteException);
    }
  }
  
  public static boolean getSyncAutomatically(Account paramAccount, String paramString)
  {
    try
    {
      boolean bool = getContentService().getSyncAutomatically(paramAccount, paramString);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static boolean getSyncAutomaticallyAsUser(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      boolean bool = getContentService().getSyncAutomaticallyAsUser(paramAccount, paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static SyncStatusInfo getSyncStatus(Account paramAccount, String paramString)
  {
    try
    {
      paramAccount = getContentService().getSyncStatus(paramAccount, paramString, null);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static SyncStatusInfo getSyncStatusAsUser(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      paramAccount = getContentService().getSyncStatusAsUser(paramAccount, paramString, null, paramInt);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static boolean invalidPeriodicExtras(Bundle paramBundle)
  {
    return (paramBundle.getBoolean("force", false)) || (paramBundle.getBoolean("do_not_retry", false)) || (paramBundle.getBoolean("ignore_backoff", false)) || (paramBundle.getBoolean("ignore_settings", false)) || (paramBundle.getBoolean("initialize", false)) || (paramBundle.getBoolean("force", false)) || (paramBundle.getBoolean("expedited", false));
  }
  
  public static boolean isSyncActive(Account paramAccount, String paramString)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account must not be null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authority must not be null");
    }
    try
    {
      boolean bool = getContentService().isSyncActive(paramAccount, paramString, null);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static boolean isSyncPending(Account paramAccount, String paramString)
  {
    return isSyncPendingAsUser(paramAccount, paramString, UserHandle.myUserId());
  }
  
  public static boolean isSyncPendingAsUser(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      boolean bool = getContentService().isSyncPendingAsUser(paramAccount, paramString, null, paramInt);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  private void maybeLogQueryToEventLog(long paramLong, Uri paramUri, String[] paramArrayOfString, String paramString1, String paramString2) {}
  
  private void maybeLogUpdateToEventLog(long paramLong, Uri paramUri, String paramString1, String paramString2) {}
  
  public static void removePeriodicSync(Account paramAccount, String paramString, Bundle paramBundle)
  {
    validateSyncExtrasBundle(paramBundle);
    try
    {
      getContentService().removePeriodicSync(paramAccount, paramString, paramBundle);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw new RuntimeException("the ContentService should always be reachable", paramAccount);
    }
  }
  
  public static void removeStatusChangeListener(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("you passed in a null handle");
    }
    try
    {
      getContentService().removeStatusChangeListener((ISyncStatusObserver.Stub)paramObject);
      return;
    }
    catch (RemoteException paramObject) {}
  }
  
  private boolean requestModifyPermission(Uri paramUri)
  {
    String str = paramUri.toString();
    Object localObject = null;
    if (str.startsWith("content://com.android.contacts")) {
      paramUri = "android.permission.READ_CONTACTS";
    }
    while ((paramUri != null) && (!new Permission(this.mContext).requestPermissionAuto(paramUri)))
    {
      return false;
      if (str.startsWith("content://sms"))
      {
        paramUri = "android.permission.READ_SMS";
      }
      else
      {
        paramUri = (Uri)localObject;
        if (str.startsWith("content://mms"))
        {
          paramUri = (Uri)localObject;
          if (!str.startsWith("content://mms/part/"))
          {
            paramUri = (Uri)localObject;
            if (!str.startsWith("content://mms/drm/")) {
              paramUri = "CUSTOM_PERMISSION_READ_MMS";
            }
          }
        }
      }
    }
    return true;
  }
  
  public static void requestSync(Account paramAccount, String paramString, Bundle paramBundle)
  {
    requestSyncAsUser(paramAccount, paramString, UserHandle.myUserId(), paramBundle);
  }
  
  public static void requestSync(SyncRequest paramSyncRequest)
  {
    try
    {
      getContentService().sync(paramSyncRequest);
      return;
    }
    catch (RemoteException paramSyncRequest) {}
  }
  
  public static void requestSyncAsUser(Account paramAccount, String paramString, int paramInt, Bundle paramBundle)
  {
    if (paramBundle == null) {
      throw new IllegalArgumentException("Must specify extras.");
    }
    paramAccount = new SyncRequest.Builder().setSyncAdapter(paramAccount, paramString).setExtras(paramBundle).syncOnce().build();
    try
    {
      getContentService().syncAsUser(paramAccount, paramInt);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  private int samplePercentForDuration(long paramLong)
  {
    if (paramLong >= 500L) {
      return 100;
    }
    return (int)(100L * paramLong / 500L) + 1;
  }
  
  public static void setIsSyncable(Account paramAccount, String paramString, int paramInt)
  {
    try
    {
      getContentService().setIsSyncable(paramAccount, paramString, paramInt);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  public static void setMasterSyncAutomatically(boolean paramBoolean)
  {
    setMasterSyncAutomaticallyAsUser(paramBoolean, UserHandle.myUserId());
  }
  
  public static void setMasterSyncAutomaticallyAsUser(boolean paramBoolean, int paramInt)
  {
    try
    {
      getContentService().setMasterSyncAutomaticallyAsUser(paramBoolean, paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public static void setSyncAutomatically(Account paramAccount, String paramString, boolean paramBoolean)
  {
    setSyncAutomaticallyAsUser(paramAccount, paramString, paramBoolean, UserHandle.myUserId());
  }
  
  public static void setSyncAutomaticallyAsUser(Account paramAccount, String paramString, boolean paramBoolean, int paramInt)
  {
    try
    {
      getContentService().setSyncAutomaticallyAsUser(paramAccount, paramString, paramBoolean, paramInt);
      return;
    }
    catch (RemoteException paramAccount) {}
  }
  
  public static int syncErrorStringToInt(String paramString)
  {
    int i = 0;
    int j = SYNC_ERROR_NAMES.length;
    while (i < j)
    {
      if (SYNC_ERROR_NAMES[i].equals(paramString)) {
        return i + 1;
      }
      i += 1;
    }
    if (paramString != null) {
      try
      {
        i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.d("ContentResolver", "error parsing sync error: " + paramString);
      }
    }
    return 0;
  }
  
  public static String syncErrorToString(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > SYNC_ERROR_NAMES.length)) {
      return String.valueOf(paramInt);
    }
    return SYNC_ERROR_NAMES[(paramInt - 1)];
  }
  
  public static void validateSyncExtrasBundle(Bundle paramBundle)
  {
    try
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = paramBundle.get((String)localIterator.next());
        if ((localObject != null) && (!(localObject instanceof Long)) && (!(localObject instanceof Integer)) && (!(localObject instanceof Boolean)) && (!(localObject instanceof Float)) && (!(localObject instanceof Double)) && (!(localObject instanceof String)) && (!(localObject instanceof Account))) {
          throw new IllegalArgumentException("unexpected value type: " + localObject.getClass().getName());
        }
      }
    }
    catch (IllegalArgumentException paramBundle)
    {
      throw paramBundle;
    }
    catch (RuntimeException paramBundle)
    {
      throw new IllegalArgumentException("error unparceling Bundle", paramBundle);
    }
  }
  
  public final ContentProviderClient acquireContentProviderClient(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    paramUri = acquireProvider(paramUri);
    if (paramUri != null) {
      return new ContentProviderClient(this, paramUri, true);
    }
    return null;
  }
  
  public final ContentProviderClient acquireContentProviderClient(String paramString)
  {
    Preconditions.checkNotNull(paramString, "name");
    paramString = acquireProvider(paramString);
    if (paramString != null) {
      return new ContentProviderClient(this, paramString, true);
    }
    return null;
  }
  
  protected IContentProvider acquireExistingProvider(Context paramContext, String paramString)
  {
    return acquireProvider(paramContext, paramString);
  }
  
  public final IContentProvider acquireExistingProvider(Uri paramUri)
  {
    if (!"content".equals(paramUri.getScheme())) {
      return null;
    }
    paramUri = paramUri.getAuthority();
    if (paramUri != null) {
      return acquireExistingProvider(this.mContext, paramUri);
    }
    return null;
  }
  
  protected abstract IContentProvider acquireProvider(Context paramContext, String paramString);
  
  public final IContentProvider acquireProvider(Uri paramUri)
  {
    if (!"content".equals(paramUri.getScheme())) {
      return null;
    }
    paramUri = paramUri.getAuthority();
    if (paramUri != null) {
      return acquireProvider(this.mContext, paramUri);
    }
    return null;
  }
  
  public final IContentProvider acquireProvider(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return acquireProvider(this.mContext, paramString);
  }
  
  public final ContentProviderClient acquireUnstableContentProviderClient(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    paramUri = acquireUnstableProvider(paramUri);
    if (paramUri != null) {
      return new ContentProviderClient(this, paramUri, false);
    }
    return null;
  }
  
  public final ContentProviderClient acquireUnstableContentProviderClient(String paramString)
  {
    Preconditions.checkNotNull(paramString, "name");
    paramString = acquireUnstableProvider(paramString);
    if (paramString != null) {
      return new ContentProviderClient(this, paramString, false);
    }
    return null;
  }
  
  protected abstract IContentProvider acquireUnstableProvider(Context paramContext, String paramString);
  
  public final IContentProvider acquireUnstableProvider(Uri paramUri)
  {
    if (!"content".equals(paramUri.getScheme())) {
      return null;
    }
    if (paramUri.getAuthority() != null) {
      return acquireUnstableProvider(this.mContext, paramUri.getAuthority());
    }
    return null;
  }
  
  public final IContentProvider acquireUnstableProvider(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return acquireUnstableProvider(this.mContext, paramString);
  }
  
  public void appNotRespondingViaProvider(IContentProvider paramIContentProvider)
  {
    throw new UnsupportedOperationException("appNotRespondingViaProvider");
  }
  
  public ContentProviderResult[] applyBatch(String paramString, ArrayList<ContentProviderOperation> paramArrayList)
    throws RemoteException, OperationApplicationException
  {
    Preconditions.checkNotNull(paramString, "authority");
    Preconditions.checkNotNull(paramArrayList, "operations");
    if ((OpFeatures.isSupport(new int[] { 12 })) && (paramArrayList.size() > 0))
    {
      localObject = (ContentProviderOperation)paramArrayList.get(0);
      if (((((ContentProviderOperation)localObject).isInsert()) || (((ContentProviderOperation)localObject).isDelete()) || (((ContentProviderOperation)localObject).isUpdate())) && (!requestModifyPermission(((ContentProviderOperation)localObject).getUri()))) {
        return new ContentProviderResult[0];
      }
    }
    Object localObject = acquireContentProviderClient(paramString);
    if (localObject == null) {
      throw new IllegalArgumentException("Unknown authority " + paramString);
    }
    try
    {
      paramString = ((ContentProviderClient)localObject).applyBatch(paramArrayList);
      return paramString;
    }
    finally
    {
      ((ContentProviderClient)localObject).release();
    }
  }
  
  public final int bulkInsert(@RequiresPermission.Write Uri paramUri, ContentValues[] paramArrayOfContentValues)
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramArrayOfContentValues, "values");
    IContentProvider localIContentProvider = acquireProvider(paramUri);
    if (localIContentProvider == null) {
      throw new IllegalArgumentException("Unknown URL " + paramUri);
    }
    try
    {
      long l = SystemClock.uptimeMillis();
      int i = localIContentProvider.bulkInsert(this.mPackageName, paramUri, paramArrayOfContentValues);
      maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - l, paramUri, "bulkinsert", null);
      releaseProvider(localIContentProvider);
      return i;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return 0;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  public final Bundle call(Uri paramUri, String paramString1, String paramString2, Bundle paramBundle)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    Preconditions.checkNotNull(paramString1, "method");
    IContentProvider localIContentProvider = acquireProvider(paramUri);
    if (localIContentProvider == null) {
      throw new IllegalArgumentException("Unknown URI " + paramUri);
    }
    try
    {
      paramUri = localIContentProvider.call(this.mPackageName, paramString1, paramString2, paramBundle);
      Bundle.setDefusable(paramUri, true);
      releaseProvider(localIContentProvider);
      return paramUri;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return null;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  @Deprecated
  public void cancelSync(Uri paramUri)
  {
    if (paramUri != null) {}
    for (paramUri = paramUri.getAuthority();; paramUri = null)
    {
      cancelSync(null, paramUri);
      return;
    }
  }
  
  public final Uri canonicalize(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri, "url");
    IContentProvider localIContentProvider = acquireProvider(paramUri);
    if (localIContentProvider == null) {
      return null;
    }
    try
    {
      paramUri = localIContentProvider.canonicalize(this.mPackageName, paramUri);
      releaseProvider(localIContentProvider);
      return paramUri;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return null;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  public final int delete(@RequiresPermission.Write Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    Preconditions.checkNotNull(paramUri, "url");
    IContentProvider localIContentProvider;
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestModifyPermission(paramUri)))
    {
      localIContentProvider = acquireProvider(paramUri);
      if (localIContentProvider == null) {
        throw new IllegalArgumentException("Unknown URL " + paramUri);
      }
    }
    else
    {
      return -1;
    }
    try
    {
      long l = SystemClock.uptimeMillis();
      int i = localIContentProvider.delete(this.mPackageName, paramUri, paramString, paramArrayOfString);
      maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - l, paramUri, "delete", paramString);
      if ((SAVE_DBG_MSG) && (i > 0) && (ContentDebugUtils.isExternalMediaUri(paramUri))) {
        ContentDebugUtils.saveDbgMsg(localIContentProvider, "ContentResolver", "delete", paramUri, paramString, paramArrayOfString, this.mPackageName);
      }
      return i;
    }
    catch (RemoteException paramUri)
    {
      return -1;
    }
    finally
    {
      releaseProvider(localIContentProvider);
    }
  }
  
  public Bundle getCache(Uri paramUri)
  {
    try
    {
      paramUri = getContentService().getCache(this.mContext.getPackageName(), paramUri, this.mContext.getUserId());
      if (paramUri != null) {
        paramUri.setClassLoader(this.mContext.getClassLoader());
      }
      return paramUri;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  public List<UriPermission> getOutgoingPersistedUriPermissions()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getPersistedUriPermissions(this.mPackageName, false).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Activity manager has died", localRemoteException);
    }
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public List<UriPermission> getPersistedUriPermissions()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getPersistedUriPermissions(this.mPackageName, true).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Activity manager has died", localRemoteException);
    }
  }
  
  public OpenResourceIdResult getResourceId(Uri paramUri)
    throws FileNotFoundException
  {
    String str = paramUri.getAuthority();
    if (TextUtils.isEmpty(str)) {
      throw new FileNotFoundException("No authority: " + paramUri);
    }
    List localList;
    try
    {
      Resources localResources = this.mContext.getPackageManager().getResourcesForApplication(str);
      localList = paramUri.getPathSegments();
      if (localList == null) {
        throw new FileNotFoundException("No path: " + paramUri);
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new FileNotFoundException("No package found for authority: " + paramUri);
    }
    int i = localList.size();
    if (i == 1) {}
    for (;;)
    {
      try
      {
        i = Integer.parseInt((String)localList.get(0));
        if (i != 0) {
          break;
        }
        throw new FileNotFoundException("No resource found for: " + paramUri);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new FileNotFoundException("Single path segment is not a resource ID: " + paramUri);
      }
      if (i == 2) {
        i = localNumberFormatException.getIdentifier((String)localList.get(1), (String)localList.get(0), str);
      } else {
        throw new FileNotFoundException("More than two path segments: " + paramUri);
      }
    }
    paramUri = new OpenResourceIdResult();
    paramUri.r = localNumberFormatException;
    paramUri.id = i;
    return paramUri;
  }
  
  public String[] getStreamTypes(Uri paramUri, String paramString)
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramString, "mimeTypeFilter");
    IContentProvider localIContentProvider = acquireProvider(paramUri);
    if (localIContentProvider == null) {
      return null;
    }
    try
    {
      paramUri = localIContentProvider.getStreamTypes(paramUri, paramString);
      releaseProvider(localIContentProvider);
      return paramUri;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return null;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  public final String getType(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri, "url");
    Object localObject = acquireExistingProvider(paramUri);
    if (localObject != null) {
      try
      {
        String str = ((IContentProvider)localObject).getType(paramUri);
        return str;
      }
      catch (Exception localException2)
      {
        Log.w("ContentResolver", "Failed to get type for: " + paramUri + " (" + localException2.getMessage() + ")");
        return null;
      }
      catch (RemoteException paramUri)
      {
        return null;
      }
      finally
      {
        releaseProvider((IContentProvider)localObject);
      }
    }
    if (!"content".equals(paramUri.getScheme())) {
      return null;
    }
    try
    {
      localObject = ActivityManagerNative.getDefault().getProviderMimeType(ContentProvider.getUriWithoutUserId(paramUri), resolveUserId(paramUri));
      return (String)localObject;
    }
    catch (Exception localException1)
    {
      Log.w("ContentResolver", "Failed to get type for: " + paramUri + " (" + localException1.getMessage() + ")");
      return null;
    }
    catch (RemoteException paramUri) {}
    return null;
  }
  
  public Drawable getTypeDrawable(String paramString)
  {
    return MimeIconUtils.loadMimeIcon(this.mContext, paramString);
  }
  
  public final Uri insert(@RequiresPermission.Write Uri paramUri, ContentValues paramContentValues)
  {
    SeempLog.record_uri(37, paramUri);
    Preconditions.checkNotNull(paramUri, "url");
    IContentProvider localIContentProvider;
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestModifyPermission(paramUri)))
    {
      localIContentProvider = acquireProvider(paramUri);
      if (localIContentProvider == null) {
        throw new IllegalArgumentException("Unknown URL " + paramUri);
      }
    }
    else
    {
      return null;
    }
    try
    {
      long l = SystemClock.uptimeMillis();
      Uri localUri = localIContentProvider.insert(this.mPackageName, paramUri, paramContentValues);
      maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - l, paramUri, "insert", null);
      if ((SAVE_DBG_MSG) && (ContentDebugUtils.isExternalMediaUri(localUri)))
      {
        paramUri = paramContentValues.getAsString("_data");
        if (((paramUri != null) && (paramUri.toLowerCase().endsWith(".nomedia"))) || (DBG_ALL)) {
          ContentDebugUtils.saveDbgMsg(localIContentProvider, "ContentResolver", "insert", localUri, paramUri, null, this.mPackageName);
        }
      }
      return localUri;
    }
    catch (RemoteException paramUri)
    {
      return null;
    }
    finally
    {
      releaseProvider(localIContentProvider);
    }
  }
  
  public void notifyChange(Uri paramUri, ContentObserver paramContentObserver)
  {
    notifyChange(paramUri, paramContentObserver, true);
  }
  
  public void notifyChange(Uri paramUri, ContentObserver paramContentObserver, int paramInt)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    notifyChange(ContentProvider.getUriWithoutUserId(paramUri), paramContentObserver, paramInt, ContentProvider.getUserIdFromUri(paramUri, UserHandle.myUserId()));
  }
  
  public void notifyChange(Uri paramUri, ContentObserver paramContentObserver, int paramInt1, int paramInt2)
  {
    IContentObserver localIContentObserver = null;
    try
    {
      IContentService localIContentService = getContentService();
      if (paramContentObserver == null) {
        if (paramContentObserver == null) {
          break label47;
        }
      }
      label47:
      for (boolean bool = paramContentObserver.deliverSelfNotifications();; bool = false)
      {
        localIContentService.notifyChange(paramUri, localIContentObserver, bool, paramInt1, paramInt2);
        return;
        localIContentObserver = paramContentObserver.getContentObserver();
        break;
      }
      return;
    }
    catch (RemoteException paramUri) {}
  }
  
  public void notifyChange(Uri paramUri, ContentObserver paramContentObserver, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    notifyChange(ContentProvider.getUriWithoutUserId(paramUri), paramContentObserver, paramBoolean, ContentProvider.getUserIdFromUri(paramUri, UserHandle.myUserId()));
  }
  
  public void notifyChange(Uri paramUri, ContentObserver paramContentObserver, boolean paramBoolean, int paramInt)
  {
    int i = 0;
    IContentObserver localIContentObserver = null;
    for (;;)
    {
      try
      {
        IContentService localIContentService = getContentService();
        if (paramContentObserver == null)
        {
          if (paramContentObserver != null)
          {
            bool = paramContentObserver.deliverSelfNotifications();
            break label62;
            localIContentService.notifyChange(paramUri, localIContentObserver, bool, i, paramInt);
          }
        }
        else
        {
          localIContentObserver = paramContentObserver.getContentObserver();
          continue;
        }
        boolean bool = false;
      }
      catch (RemoteException paramUri)
      {
        return;
      }
      label62:
      if (paramBoolean) {
        i = 1;
      }
    }
  }
  
  public final AssetFileDescriptor openAssetFileDescriptor(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    return openAssetFileDescriptor(paramUri, paramString, null);
  }
  
  /* Error */
  public final AssetFileDescriptor openAssetFileDescriptor(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 580
    //   4: invokestatic 586	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_2
    //   9: ldc_w 956
    //   12: invokestatic 586	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_1
    //   17: invokevirtual 608	android/net/Uri:getScheme	()Ljava/lang/String;
    //   20: astore 4
    //   22: ldc 48
    //   24: aload 4
    //   26: invokevirtual 504	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   29: ifeq +90 -> 119
    //   32: ldc_w 957
    //   35: aload_2
    //   36: invokevirtual 504	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   39: ifne +31 -> 70
    //   42: new 792	java/io/FileNotFoundException
    //   45: dup
    //   46: new 511	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   53: ldc_w 959
    //   56: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: aload_1
    //   60: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   69: athrow
    //   70: aload_0
    //   71: aload_1
    //   72: invokevirtual 961	android/content/ContentResolver:getResourceId	(Landroid/net/Uri;)Landroid/content/ContentResolver$OpenResourceIdResult;
    //   75: astore_2
    //   76: aload_2
    //   77: getfield 839	android/content/ContentResolver$OpenResourceIdResult:r	Landroid/content/res/Resources;
    //   80: aload_2
    //   81: getfield 842	android/content/ContentResolver$OpenResourceIdResult:id	I
    //   84: invokevirtual 965	android/content/res/Resources:openRawResourceFd	(I)Landroid/content/res/AssetFileDescriptor;
    //   87: astore_2
    //   88: aload_2
    //   89: areturn
    //   90: astore_2
    //   91: new 792	java/io/FileNotFoundException
    //   94: dup
    //   95: new 511	java/lang/StringBuilder
    //   98: dup
    //   99: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   102: ldc_w 967
    //   105: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   108: aload_1
    //   109: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   112: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   115: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   118: athrow
    //   119: ldc 52
    //   121: aload 4
    //   123: invokevirtual 504	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   126: ifeq +33 -> 159
    //   129: new 969	android/content/res/AssetFileDescriptor
    //   132: dup
    //   133: new 971	java/io/File
    //   136: dup
    //   137: aload_1
    //   138: invokevirtual 974	android/net/Uri:getPath	()Ljava/lang/String;
    //   141: invokespecial 975	java/io/File:<init>	(Ljava/lang/String;)V
    //   144: aload_2
    //   145: invokestatic 980	android/os/ParcelFileDescriptor:parseMode	(Ljava/lang/String;)I
    //   148: invokestatic 984	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
    //   151: lconst_0
    //   152: ldc2_w 985
    //   155: invokespecial 989	android/content/res/AssetFileDescriptor:<init>	(Landroid/os/ParcelFileDescriptor;JJ)V
    //   158: areturn
    //   159: ldc_w 957
    //   162: aload_2
    //   163: invokevirtual 504	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   166: ifeq +14 -> 180
    //   169: aload_0
    //   170: aload_1
    //   171: ldc_w 991
    //   174: aconst_null
    //   175: aload_3
    //   176: invokevirtual 995	android/content/ContentResolver:openTypedAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/Bundle;Landroid/os/CancellationSignal;)Landroid/content/res/AssetFileDescriptor;
    //   179: areturn
    //   180: aload_0
    //   181: aload_1
    //   182: invokevirtual 617	android/content/ContentResolver:acquireUnstableProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   185: astore 11
    //   187: aload 11
    //   189: ifnonnull +31 -> 220
    //   192: new 792	java/io/FileNotFoundException
    //   195: dup
    //   196: new 511	java/lang/StringBuilder
    //   199: dup
    //   200: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   203: ldc_w 997
    //   206: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: aload_1
    //   210: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   213: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   216: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   219: athrow
    //   220: aconst_null
    //   221: astore 14
    //   223: aconst_null
    //   224: astore 8
    //   226: aconst_null
    //   227: astore 15
    //   229: aconst_null
    //   230: astore 13
    //   232: aconst_null
    //   233: astore 12
    //   235: aload_3
    //   236: ifnull +94 -> 330
    //   239: aload 13
    //   241: astore 5
    //   243: aload 11
    //   245: astore 9
    //   247: aload 14
    //   249: astore 4
    //   251: aload 11
    //   253: astore 7
    //   255: aload 15
    //   257: astore 6
    //   259: aload 11
    //   261: astore 10
    //   263: aload_3
    //   264: invokevirtual 1002	android/os/CancellationSignal:throwIfCanceled	()V
    //   267: aload 13
    //   269: astore 5
    //   271: aload 11
    //   273: astore 9
    //   275: aload 14
    //   277: astore 4
    //   279: aload 11
    //   281: astore 7
    //   283: aload 15
    //   285: astore 6
    //   287: aload 11
    //   289: astore 10
    //   291: aload 11
    //   293: invokeinterface 1006 1 0
    //   298: astore 12
    //   300: aload 13
    //   302: astore 5
    //   304: aload 11
    //   306: astore 9
    //   308: aload 14
    //   310: astore 4
    //   312: aload 11
    //   314: astore 7
    //   316: aload 15
    //   318: astore 6
    //   320: aload 11
    //   322: astore 10
    //   324: aload_3
    //   325: aload 12
    //   327: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   330: aload 13
    //   332: astore 5
    //   334: aload 11
    //   336: astore 9
    //   338: aload 14
    //   340: astore 4
    //   342: aload 11
    //   344: astore 7
    //   346: aload 15
    //   348: astore 6
    //   350: aload 11
    //   352: astore 10
    //   354: aload 11
    //   356: aload_0
    //   357: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   360: aload_1
    //   361: aload_2
    //   362: aload 12
    //   364: invokeinterface 1014 5 0
    //   369: astore 16
    //   371: aload 16
    //   373: astore_2
    //   374: aload_2
    //   375: astore 12
    //   377: aload 8
    //   379: astore 6
    //   381: aload_2
    //   382: ifnonnull +305 -> 687
    //   385: aload_3
    //   386: ifnull +8 -> 394
    //   389: aload_3
    //   390: aconst_null
    //   391: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   394: aload 11
    //   396: ifnull +10 -> 406
    //   399: aload_0
    //   400: aload 11
    //   402: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   405: pop
    //   406: aconst_null
    //   407: areturn
    //   408: astore 4
    //   410: aload 13
    //   412: astore 5
    //   414: aload 11
    //   416: astore 9
    //   418: aload 14
    //   420: astore 4
    //   422: aload 11
    //   424: astore 7
    //   426: aload 15
    //   428: astore 6
    //   430: aload 11
    //   432: astore 10
    //   434: aload_0
    //   435: aload 11
    //   437: invokevirtual 1020	android/content/ContentResolver:unstableProviderDied	(Landroid/content/IContentProvider;)V
    //   440: aload 13
    //   442: astore 5
    //   444: aload 11
    //   446: astore 9
    //   448: aload 14
    //   450: astore 4
    //   452: aload 11
    //   454: astore 7
    //   456: aload 15
    //   458: astore 6
    //   460: aload 11
    //   462: astore 10
    //   464: aload_0
    //   465: aload_1
    //   466: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   469: astore 8
    //   471: aload 8
    //   473: ifnonnull +128 -> 601
    //   476: aload 8
    //   478: astore 5
    //   480: aload 11
    //   482: astore 9
    //   484: aload 8
    //   486: astore 4
    //   488: aload 11
    //   490: astore 7
    //   492: aload 8
    //   494: astore 6
    //   496: aload 11
    //   498: astore 10
    //   500: new 792	java/io/FileNotFoundException
    //   503: dup
    //   504: new 511	java/lang/StringBuilder
    //   507: dup
    //   508: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   511: ldc_w 997
    //   514: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   517: aload_1
    //   518: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   521: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   524: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   527: athrow
    //   528: astore_2
    //   529: aload 5
    //   531: astore 4
    //   533: aload 9
    //   535: astore 7
    //   537: new 792	java/io/FileNotFoundException
    //   540: dup
    //   541: new 511	java/lang/StringBuilder
    //   544: dup
    //   545: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   548: ldc_w 1022
    //   551: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   554: aload_1
    //   555: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   558: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   561: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   564: athrow
    //   565: astore_1
    //   566: aload_3
    //   567: ifnull +8 -> 575
    //   570: aload_3
    //   571: aconst_null
    //   572: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   575: aload 4
    //   577: ifnull +10 -> 587
    //   580: aload_0
    //   581: aload 4
    //   583: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   586: pop
    //   587: aload 7
    //   589: ifnull +10 -> 599
    //   592: aload_0
    //   593: aload 7
    //   595: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   598: pop
    //   599: aload_1
    //   600: athrow
    //   601: aload 8
    //   603: astore 5
    //   605: aload 11
    //   607: astore 9
    //   609: aload 8
    //   611: astore 4
    //   613: aload 11
    //   615: astore 7
    //   617: aload 8
    //   619: astore 6
    //   621: aload 11
    //   623: astore 10
    //   625: aload 8
    //   627: aload_0
    //   628: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   631: aload_1
    //   632: aload_2
    //   633: aload 12
    //   635: invokeinterface 1014 5 0
    //   640: astore_2
    //   641: aload_2
    //   642: astore 12
    //   644: aload 8
    //   646: astore 6
    //   648: aload_2
    //   649: ifnonnull +38 -> 687
    //   652: aload_3
    //   653: ifnull +8 -> 661
    //   656: aload_3
    //   657: aconst_null
    //   658: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   661: aload 8
    //   663: ifnull +10 -> 673
    //   666: aload_0
    //   667: aload 8
    //   669: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   672: pop
    //   673: aload 11
    //   675: ifnull +10 -> 685
    //   678: aload_0
    //   679: aload 11
    //   681: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   684: pop
    //   685: aconst_null
    //   686: areturn
    //   687: aload 6
    //   689: astore_2
    //   690: aload 6
    //   692: ifnonnull +29 -> 721
    //   695: aload 6
    //   697: astore 5
    //   699: aload 11
    //   701: astore 9
    //   703: aload 6
    //   705: astore 4
    //   707: aload 11
    //   709: astore 7
    //   711: aload 11
    //   713: astore 10
    //   715: aload_0
    //   716: aload_1
    //   717: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   720: astore_2
    //   721: aload_2
    //   722: astore 5
    //   724: aload 11
    //   726: astore 9
    //   728: aload_2
    //   729: astore 4
    //   731: aload 11
    //   733: astore 7
    //   735: aload_2
    //   736: astore 6
    //   738: aload 11
    //   740: astore 10
    //   742: aload_0
    //   743: aload 11
    //   745: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   748: pop
    //   749: aconst_null
    //   750: astore 11
    //   752: aconst_null
    //   753: astore 13
    //   755: aconst_null
    //   756: astore 8
    //   758: aload_2
    //   759: astore 5
    //   761: aload 8
    //   763: astore 9
    //   765: aload_2
    //   766: astore 4
    //   768: aload 11
    //   770: astore 7
    //   772: aload_2
    //   773: astore 6
    //   775: aload 13
    //   777: astore 10
    //   779: new 14	android/content/ContentResolver$ParcelFileDescriptorInner
    //   782: dup
    //   783: aload_0
    //   784: aload 12
    //   786: invokevirtual 1026	android/content/res/AssetFileDescriptor:getParcelFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   789: aload_2
    //   790: invokespecial 1029	android/content/ContentResolver$ParcelFileDescriptorInner:<init>	(Landroid/content/ContentResolver;Landroid/os/ParcelFileDescriptor;Landroid/content/IContentProvider;)V
    //   793: astore_2
    //   794: aconst_null
    //   795: astore 4
    //   797: aconst_null
    //   798: astore 6
    //   800: aconst_null
    //   801: astore 5
    //   803: aload 8
    //   805: astore 9
    //   807: aload 11
    //   809: astore 7
    //   811: aload 13
    //   813: astore 10
    //   815: new 969	android/content/res/AssetFileDescriptor
    //   818: dup
    //   819: aload_2
    //   820: aload 12
    //   822: invokevirtual 1032	android/content/res/AssetFileDescriptor:getStartOffset	()J
    //   825: aload 12
    //   827: invokevirtual 1035	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   830: invokespecial 989	android/content/res/AssetFileDescriptor:<init>	(Landroid/os/ParcelFileDescriptor;JJ)V
    //   833: astore_2
    //   834: aload_3
    //   835: ifnull +8 -> 843
    //   838: aload_3
    //   839: aconst_null
    //   840: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   843: aload_2
    //   844: areturn
    //   845: astore_1
    //   846: aload 6
    //   848: astore 4
    //   850: aload 10
    //   852: astore 7
    //   854: aload_1
    //   855: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	856	0	this	ContentResolver
    //   0	856	1	paramUri	Uri
    //   0	856	2	paramString	String
    //   0	856	3	paramCancellationSignal	CancellationSignal
    //   20	321	4	localObject1	Object
    //   408	1	4	localDeadObjectException	android.os.DeadObjectException
    //   420	429	4	localObject2	Object
    //   241	561	5	localObject3	Object
    //   257	590	6	localObject4	Object
    //   253	600	7	localObject5	Object
    //   224	580	8	localIContentProvider1	IContentProvider
    //   245	561	9	localIContentProvider2	IContentProvider
    //   261	590	10	localObject6	Object
    //   185	623	11	localIContentProvider3	IContentProvider
    //   233	593	12	localObject7	Object
    //   230	582	13	localObject8	Object
    //   221	228	14	localObject9	Object
    //   227	230	15	localObject10	Object
    //   369	3	16	localAssetFileDescriptor	AssetFileDescriptor
    // Exception table:
    //   from	to	target	type
    //   76	88	90	android/content/res/Resources$NotFoundException
    //   354	371	408	android/os/DeadObjectException
    //   263	267	528	android/os/RemoteException
    //   291	300	528	android/os/RemoteException
    //   324	330	528	android/os/RemoteException
    //   354	371	528	android/os/RemoteException
    //   434	440	528	android/os/RemoteException
    //   464	471	528	android/os/RemoteException
    //   500	528	528	android/os/RemoteException
    //   625	641	528	android/os/RemoteException
    //   715	721	528	android/os/RemoteException
    //   742	749	528	android/os/RemoteException
    //   779	794	528	android/os/RemoteException
    //   815	834	528	android/os/RemoteException
    //   263	267	565	finally
    //   291	300	565	finally
    //   324	330	565	finally
    //   354	371	565	finally
    //   434	440	565	finally
    //   464	471	565	finally
    //   500	528	565	finally
    //   537	565	565	finally
    //   625	641	565	finally
    //   715	721	565	finally
    //   742	749	565	finally
    //   779	794	565	finally
    //   815	834	565	finally
    //   854	856	565	finally
    //   263	267	845	java/io/FileNotFoundException
    //   291	300	845	java/io/FileNotFoundException
    //   324	330	845	java/io/FileNotFoundException
    //   354	371	845	java/io/FileNotFoundException
    //   434	440	845	java/io/FileNotFoundException
    //   464	471	845	java/io/FileNotFoundException
    //   500	528	845	java/io/FileNotFoundException
    //   625	641	845	java/io/FileNotFoundException
    //   715	721	845	java/io/FileNotFoundException
    //   742	749	845	java/io/FileNotFoundException
    //   779	794	845	java/io/FileNotFoundException
    //   815	834	845	java/io/FileNotFoundException
  }
  
  public final ParcelFileDescriptor openFileDescriptor(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    return openFileDescriptor(paramUri, paramString, null);
  }
  
  public final ParcelFileDescriptor openFileDescriptor(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    paramUri = openAssetFileDescriptor(paramUri, paramString, paramCancellationSignal);
    if (paramUri == null) {
      return null;
    }
    if (paramUri.getDeclaredLength() < 0L) {
      return paramUri.getParcelFileDescriptor();
    }
    try
    {
      paramUri.close();
      throw new FileNotFoundException("Not a whole file");
    }
    catch (IOException paramUri)
    {
      for (;;) {}
    }
  }
  
  public final InputStream openInputStream(Uri paramUri)
    throws FileNotFoundException
  {
    Object localObject1 = null;
    Preconditions.checkNotNull(paramUri, "uri");
    Object localObject2 = paramUri.getScheme();
    if ("android.resource".equals(localObject2))
    {
      localObject1 = getResourceId(paramUri);
      try
      {
        localObject1 = ((OpenResourceIdResult)localObject1).r.openRawResource(((OpenResourceIdResult)localObject1).id);
        return (InputStream)localObject1;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        throw new FileNotFoundException("Resource does not exist: " + paramUri);
      }
    }
    if ("file".equals(localObject2)) {
      return new FileInputStream(paramUri.getPath());
    }
    localObject2 = openAssetFileDescriptor(paramUri, "r", null);
    paramUri = localNotFoundException;
    if (localObject2 != null) {}
    try
    {
      paramUri = ((AssetFileDescriptor)localObject2).createInputStream();
      return paramUri;
    }
    catch (IOException paramUri)
    {
      throw new FileNotFoundException("Unable to create stream");
    }
  }
  
  public final OutputStream openOutputStream(Uri paramUri)
    throws FileNotFoundException
  {
    return openOutputStream(paramUri, "w");
  }
  
  public final OutputStream openOutputStream(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    Object localObject = null;
    paramString = openAssetFileDescriptor(paramUri, paramString, null);
    paramUri = (Uri)localObject;
    if (paramString != null) {}
    try
    {
      paramUri = paramString.createOutputStream();
      return paramUri;
    }
    catch (IOException paramUri)
    {
      throw new FileNotFoundException("Unable to create stream");
    }
  }
  
  public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri paramUri, String paramString, Bundle paramBundle)
    throws FileNotFoundException
  {
    return openTypedAssetFileDescriptor(paramUri, paramString, paramBundle, null);
  }
  
  /* Error */
  public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri paramUri, String paramString, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 580
    //   4: invokestatic 586	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_2
    //   9: ldc_w 1075
    //   12: invokestatic 586	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_0
    //   17: aload_1
    //   18: invokevirtual 617	android/content/ContentResolver:acquireUnstableProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   21: astore 12
    //   23: aload 12
    //   25: ifnonnull +31 -> 56
    //   28: new 792	java/io/FileNotFoundException
    //   31: dup
    //   32: new 511	java/lang/StringBuilder
    //   35: dup
    //   36: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   39: ldc_w 997
    //   42: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_1
    //   46: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   49: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   52: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   55: athrow
    //   56: aconst_null
    //   57: astore 14
    //   59: aconst_null
    //   60: astore 15
    //   62: aconst_null
    //   63: astore 16
    //   65: aconst_null
    //   66: astore 9
    //   68: aconst_null
    //   69: astore 13
    //   71: aload 4
    //   73: ifnull +96 -> 169
    //   76: aload 12
    //   78: astore 10
    //   80: aload 9
    //   82: astore 6
    //   84: aload 12
    //   86: astore 8
    //   88: aload 14
    //   90: astore 5
    //   92: aload 12
    //   94: astore 11
    //   96: aload 16
    //   98: astore 7
    //   100: aload 4
    //   102: invokevirtual 1002	android/os/CancellationSignal:throwIfCanceled	()V
    //   105: aload 12
    //   107: astore 10
    //   109: aload 9
    //   111: astore 6
    //   113: aload 12
    //   115: astore 8
    //   117: aload 14
    //   119: astore 5
    //   121: aload 12
    //   123: astore 11
    //   125: aload 16
    //   127: astore 7
    //   129: aload 12
    //   131: invokeinterface 1006 1 0
    //   136: astore 13
    //   138: aload 12
    //   140: astore 10
    //   142: aload 9
    //   144: astore 6
    //   146: aload 12
    //   148: astore 8
    //   150: aload 14
    //   152: astore 5
    //   154: aload 12
    //   156: astore 11
    //   158: aload 16
    //   160: astore 7
    //   162: aload 4
    //   164: aload 13
    //   166: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   169: aload 12
    //   171: astore 10
    //   173: aload 9
    //   175: astore 6
    //   177: aload 12
    //   179: astore 8
    //   181: aload 14
    //   183: astore 5
    //   185: aload 12
    //   187: astore 11
    //   189: aload 16
    //   191: astore 7
    //   193: aload 12
    //   195: aload_0
    //   196: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   199: aload_1
    //   200: aload_2
    //   201: aload_3
    //   202: aload 13
    //   204: invokeinterface 1079 6 0
    //   209: astore 17
    //   211: aload 17
    //   213: astore_2
    //   214: aload 15
    //   216: astore 7
    //   218: aload_2
    //   219: astore_3
    //   220: aload_2
    //   221: ifnonnull +311 -> 532
    //   224: aload 4
    //   226: ifnull +9 -> 235
    //   229: aload 4
    //   231: aconst_null
    //   232: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   235: aload 12
    //   237: ifnull +10 -> 247
    //   240: aload_0
    //   241: aload 12
    //   243: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   246: pop
    //   247: aconst_null
    //   248: areturn
    //   249: astore 5
    //   251: aload 12
    //   253: astore 10
    //   255: aload 9
    //   257: astore 6
    //   259: aload 12
    //   261: astore 8
    //   263: aload 14
    //   265: astore 5
    //   267: aload 12
    //   269: astore 11
    //   271: aload 16
    //   273: astore 7
    //   275: aload_0
    //   276: aload 12
    //   278: invokevirtual 1020	android/content/ContentResolver:unstableProviderDied	(Landroid/content/IContentProvider;)V
    //   281: aload 12
    //   283: astore 10
    //   285: aload 9
    //   287: astore 6
    //   289: aload 12
    //   291: astore 8
    //   293: aload 14
    //   295: astore 5
    //   297: aload 12
    //   299: astore 11
    //   301: aload 16
    //   303: astore 7
    //   305: aload_0
    //   306: aload_1
    //   307: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   310: astore 9
    //   312: aload 9
    //   314: ifnonnull +130 -> 444
    //   317: aload 12
    //   319: astore 10
    //   321: aload 9
    //   323: astore 6
    //   325: aload 12
    //   327: astore 8
    //   329: aload 9
    //   331: astore 5
    //   333: aload 12
    //   335: astore 11
    //   337: aload 9
    //   339: astore 7
    //   341: new 792	java/io/FileNotFoundException
    //   344: dup
    //   345: new 511	java/lang/StringBuilder
    //   348: dup
    //   349: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   352: ldc_w 997
    //   355: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: aload_1
    //   359: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   362: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   365: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   368: athrow
    //   369: astore_2
    //   370: aload 10
    //   372: astore 8
    //   374: aload 6
    //   376: astore 5
    //   378: new 792	java/io/FileNotFoundException
    //   381: dup
    //   382: new 511	java/lang/StringBuilder
    //   385: dup
    //   386: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   389: ldc_w 1022
    //   392: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   395: aload_1
    //   396: invokevirtual 690	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   399: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   402: invokespecial 802	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   405: athrow
    //   406: astore_1
    //   407: aload 4
    //   409: ifnull +9 -> 418
    //   412: aload 4
    //   414: aconst_null
    //   415: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   418: aload 5
    //   420: ifnull +10 -> 430
    //   423: aload_0
    //   424: aload 5
    //   426: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   429: pop
    //   430: aload 8
    //   432: ifnull +10 -> 442
    //   435: aload_0
    //   436: aload 8
    //   438: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   441: pop
    //   442: aload_1
    //   443: athrow
    //   444: aload 12
    //   446: astore 10
    //   448: aload 9
    //   450: astore 6
    //   452: aload 12
    //   454: astore 8
    //   456: aload 9
    //   458: astore 5
    //   460: aload 12
    //   462: astore 11
    //   464: aload 9
    //   466: astore 7
    //   468: aload 9
    //   470: aload_0
    //   471: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   474: aload_1
    //   475: aload_2
    //   476: aload_3
    //   477: aload 13
    //   479: invokeinterface 1079 6 0
    //   484: astore_2
    //   485: aload 9
    //   487: astore 7
    //   489: aload_2
    //   490: astore_3
    //   491: aload_2
    //   492: ifnonnull +40 -> 532
    //   495: aload 4
    //   497: ifnull +9 -> 506
    //   500: aload 4
    //   502: aconst_null
    //   503: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   506: aload 9
    //   508: ifnull +10 -> 518
    //   511: aload_0
    //   512: aload 9
    //   514: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   517: pop
    //   518: aload 12
    //   520: ifnull +10 -> 530
    //   523: aload_0
    //   524: aload 12
    //   526: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   529: pop
    //   530: aconst_null
    //   531: areturn
    //   532: aload 7
    //   534: astore_2
    //   535: aload 7
    //   537: ifnonnull +29 -> 566
    //   540: aload 12
    //   542: astore 10
    //   544: aload 7
    //   546: astore 6
    //   548: aload 12
    //   550: astore 8
    //   552: aload 7
    //   554: astore 5
    //   556: aload 12
    //   558: astore 11
    //   560: aload_0
    //   561: aload_1
    //   562: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   565: astore_2
    //   566: aload 12
    //   568: astore 10
    //   570: aload_2
    //   571: astore 6
    //   573: aload 12
    //   575: astore 8
    //   577: aload_2
    //   578: astore 5
    //   580: aload 12
    //   582: astore 11
    //   584: aload_2
    //   585: astore 7
    //   587: aload_0
    //   588: aload 12
    //   590: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   593: pop
    //   594: aconst_null
    //   595: astore 12
    //   597: aconst_null
    //   598: astore 13
    //   600: aconst_null
    //   601: astore 9
    //   603: aload 9
    //   605: astore 10
    //   607: aload_2
    //   608: astore 6
    //   610: aload 12
    //   612: astore 8
    //   614: aload_2
    //   615: astore 5
    //   617: aload 13
    //   619: astore 11
    //   621: aload_2
    //   622: astore 7
    //   624: new 14	android/content/ContentResolver$ParcelFileDescriptorInner
    //   627: dup
    //   628: aload_0
    //   629: aload_3
    //   630: invokevirtual 1026	android/content/res/AssetFileDescriptor:getParcelFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   633: aload_2
    //   634: invokespecial 1029	android/content/ContentResolver$ParcelFileDescriptorInner:<init>	(Landroid/content/ContentResolver;Landroid/os/ParcelFileDescriptor;Landroid/content/IContentProvider;)V
    //   637: astore_2
    //   638: aconst_null
    //   639: astore 5
    //   641: aconst_null
    //   642: astore 7
    //   644: aconst_null
    //   645: astore 6
    //   647: aload 9
    //   649: astore 10
    //   651: aload 12
    //   653: astore 8
    //   655: aload 13
    //   657: astore 11
    //   659: new 969	android/content/res/AssetFileDescriptor
    //   662: dup
    //   663: aload_2
    //   664: aload_3
    //   665: invokevirtual 1032	android/content/res/AssetFileDescriptor:getStartOffset	()J
    //   668: aload_3
    //   669: invokevirtual 1035	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   672: invokespecial 989	android/content/res/AssetFileDescriptor:<init>	(Landroid/os/ParcelFileDescriptor;JJ)V
    //   675: astore_2
    //   676: aload 4
    //   678: ifnull +9 -> 687
    //   681: aload 4
    //   683: aconst_null
    //   684: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   687: aload_2
    //   688: areturn
    //   689: astore_1
    //   690: aload 11
    //   692: astore 8
    //   694: aload 7
    //   696: astore 5
    //   698: aload_1
    //   699: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	700	0	this	ContentResolver
    //   0	700	1	paramUri	Uri
    //   0	700	2	paramString	String
    //   0	700	3	paramBundle	Bundle
    //   0	700	4	paramCancellationSignal	CancellationSignal
    //   90	94	5	localObject1	Object
    //   249	1	5	localDeadObjectException	android.os.DeadObjectException
    //   265	432	5	localObject2	Object
    //   82	564	6	localObject3	Object
    //   98	597	7	localObject4	Object
    //   86	607	8	localObject5	Object
    //   66	582	9	localIContentProvider1	IContentProvider
    //   78	572	10	localIContentProvider2	IContentProvider
    //   94	597	11	localObject6	Object
    //   21	631	12	localIContentProvider3	IContentProvider
    //   69	587	13	localICancellationSignal	android.os.ICancellationSignal
    //   57	237	14	localObject7	Object
    //   60	155	15	localObject8	Object
    //   63	239	16	localObject9	Object
    //   209	3	17	localAssetFileDescriptor	AssetFileDescriptor
    // Exception table:
    //   from	to	target	type
    //   193	211	249	android/os/DeadObjectException
    //   100	105	369	android/os/RemoteException
    //   129	138	369	android/os/RemoteException
    //   162	169	369	android/os/RemoteException
    //   193	211	369	android/os/RemoteException
    //   275	281	369	android/os/RemoteException
    //   305	312	369	android/os/RemoteException
    //   341	369	369	android/os/RemoteException
    //   468	485	369	android/os/RemoteException
    //   560	566	369	android/os/RemoteException
    //   587	594	369	android/os/RemoteException
    //   624	638	369	android/os/RemoteException
    //   659	676	369	android/os/RemoteException
    //   100	105	406	finally
    //   129	138	406	finally
    //   162	169	406	finally
    //   193	211	406	finally
    //   275	281	406	finally
    //   305	312	406	finally
    //   341	369	406	finally
    //   378	406	406	finally
    //   468	485	406	finally
    //   560	566	406	finally
    //   587	594	406	finally
    //   624	638	406	finally
    //   659	676	406	finally
    //   698	700	406	finally
    //   100	105	689	java/io/FileNotFoundException
    //   129	138	689	java/io/FileNotFoundException
    //   162	169	689	java/io/FileNotFoundException
    //   193	211	689	java/io/FileNotFoundException
    //   275	281	689	java/io/FileNotFoundException
    //   305	312	689	java/io/FileNotFoundException
    //   341	369	689	java/io/FileNotFoundException
    //   468	485	689	java/io/FileNotFoundException
    //   560	566	689	java/io/FileNotFoundException
    //   587	594	689	java/io/FileNotFoundException
    //   624	638	689	java/io/FileNotFoundException
    //   659	676	689	java/io/FileNotFoundException
  }
  
  public void putCache(Uri paramUri, Bundle paramBundle)
  {
    try
    {
      getContentService().putCache(this.mContext.getPackageName(), paramUri, paramBundle, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  public final Cursor query(@RequiresPermission.Read Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    SeempLog.record_uri(13, paramUri);
    return query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, null);
  }
  
  /* Error */
  public final Cursor query(@RequiresPermission.Read Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: bipush 13
    //   2: aload_1
    //   3: invokestatic 897	android/util/SeempLog:record_uri	(ILandroid/net/Uri;)I
    //   6: pop
    //   7: aload_1
    //   8: ldc_w 580
    //   11: invokestatic 586	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   14: pop
    //   15: iconst_1
    //   16: newarray <illegal type>
    //   18: dup
    //   19: iconst_0
    //   20: bipush 12
    //   22: iastore
    //   23: invokestatic 641	android/util/OpFeatures:isSupport	([I)Z
    //   26: ifeq +194 -> 220
    //   29: aload_1
    //   30: invokevirtual 413	android/net/Uri:toString	()Ljava/lang/String;
    //   33: astore 11
    //   35: aconst_null
    //   36: astore 10
    //   38: aload 11
    //   40: ldc_w 430
    //   43: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   46: ifeq +83 -> 129
    //   49: ldc_w 432
    //   52: astore 9
    //   54: aload 9
    //   56: ifnull +164 -> 220
    //   59: new 423	android/util/Permission
    //   62: dup
    //   63: aload_0
    //   64: getfield 194	android/content/ContentResolver:mContext	Landroid/content/Context;
    //   67: invokespecial 425	android/util/Permission:<init>	(Landroid/content/Context;)V
    //   70: aload 9
    //   72: invokevirtual 428	android/util/Permission:requestPermissionAuto	(Ljava/lang/String;)Z
    //   75: ifne +145 -> 220
    //   78: ldc 126
    //   80: new 511	java/lang/StringBuilder
    //   83: dup
    //   84: invokespecial 512	java/lang/StringBuilder:<init>	()V
    //   87: ldc_w 1092
    //   90: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: aload 9
    //   95: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: ldc_w 1094
    //   101: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: aload_0
    //   105: getfield 194	android/content/ContentResolver:mContext	Landroid/content/Context;
    //   108: invokevirtual 749	android/content/Context:getPackageName	()Ljava/lang/String;
    //   111: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: ldc_w 1096
    //   117: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual 519	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: invokestatic 868	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   126: pop
    //   127: aconst_null
    //   128: areturn
    //   129: aload 11
    //   131: ldc_w 434
    //   134: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   137: ifeq +41 -> 178
    //   140: aload 10
    //   142: astore 9
    //   144: aload 11
    //   146: ldc_w 436
    //   149: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   152: ifne -98 -> 54
    //   155: aload 10
    //   157: astore 9
    //   159: aload 11
    //   161: ldc_w 438
    //   164: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   167: ifne -113 -> 54
    //   170: ldc_w 440
    //   173: astore 9
    //   175: goto -121 -> 54
    //   178: aload 11
    //   180: ldc_w 415
    //   183: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   186: ifeq +11 -> 197
    //   189: ldc_w 421
    //   192: astore 9
    //   194: goto -140 -> 54
    //   197: aload 10
    //   199: astore 9
    //   201: aload 11
    //   203: ldc_w 1098
    //   206: invokevirtual 419	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   209: ifeq -155 -> 54
    //   212: ldc_w 1100
    //   215: astore 9
    //   217: goto -163 -> 54
    //   220: aload_0
    //   221: aload_1
    //   222: invokevirtual 617	android/content/ContentResolver:acquireUnstableProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   225: astore 20
    //   227: aload 20
    //   229: ifnonnull +5 -> 234
    //   232: aconst_null
    //   233: areturn
    //   234: aconst_null
    //   235: astore 9
    //   237: aconst_null
    //   238: astore 18
    //   240: aconst_null
    //   241: astore 17
    //   243: aconst_null
    //   244: astore 16
    //   246: aconst_null
    //   247: astore 15
    //   249: aload 9
    //   251: astore 10
    //   253: aload 15
    //   255: astore 12
    //   257: aload 18
    //   259: astore 11
    //   261: aload 16
    //   263: astore 13
    //   265: invokestatic 696	android/os/SystemClock:uptimeMillis	()J
    //   268: lstore 7
    //   270: aconst_null
    //   271: astore 14
    //   273: aload 6
    //   275: ifnull +72 -> 347
    //   278: aload 9
    //   280: astore 10
    //   282: aload 15
    //   284: astore 12
    //   286: aload 18
    //   288: astore 11
    //   290: aload 16
    //   292: astore 13
    //   294: aload 6
    //   296: invokevirtual 1002	android/os/CancellationSignal:throwIfCanceled	()V
    //   299: aload 9
    //   301: astore 10
    //   303: aload 15
    //   305: astore 12
    //   307: aload 18
    //   309: astore 11
    //   311: aload 16
    //   313: astore 13
    //   315: aload 20
    //   317: invokeinterface 1006 1 0
    //   322: astore 14
    //   324: aload 9
    //   326: astore 10
    //   328: aload 15
    //   330: astore 12
    //   332: aload 18
    //   334: astore 11
    //   336: aload 16
    //   338: astore 13
    //   340: aload 6
    //   342: aload 14
    //   344: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   347: aload 9
    //   349: astore 10
    //   351: aload 15
    //   353: astore 12
    //   355: aload 18
    //   357: astore 11
    //   359: aload 16
    //   361: astore 13
    //   363: aload 20
    //   365: aload_0
    //   366: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   369: aload_1
    //   370: aload_2
    //   371: aload_3
    //   372: aload 4
    //   374: aload 5
    //   376: aload 14
    //   378: invokeinterface 1103 8 0
    //   383: astore 19
    //   385: aload 19
    //   387: astore 9
    //   389: aload 17
    //   391: astore 4
    //   393: aload 9
    //   395: ifnonnull +190 -> 585
    //   398: aload 9
    //   400: ifnull +10 -> 410
    //   403: aload 9
    //   405: invokeinterface 1106 1 0
    //   410: aload 6
    //   412: ifnull +9 -> 421
    //   415: aload 6
    //   417: aconst_null
    //   418: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   421: aload 20
    //   423: ifnull +10 -> 433
    //   426: aload_0
    //   427: aload 20
    //   429: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   432: pop
    //   433: aload 4
    //   435: ifnull +10 -> 445
    //   438: aload_0
    //   439: aload 4
    //   441: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   444: pop
    //   445: aconst_null
    //   446: areturn
    //   447: astore 10
    //   449: aload 9
    //   451: astore 10
    //   453: aload 15
    //   455: astore 12
    //   457: aload 18
    //   459: astore 11
    //   461: aload 16
    //   463: astore 13
    //   465: aload_0
    //   466: aload 20
    //   468: invokevirtual 1020	android/content/ContentResolver:unstableProviderDied	(Landroid/content/IContentProvider;)V
    //   471: aload 9
    //   473: astore 10
    //   475: aload 15
    //   477: astore 12
    //   479: aload 18
    //   481: astore 11
    //   483: aload 16
    //   485: astore 13
    //   487: aload_0
    //   488: aload_1
    //   489: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   492: astore 9
    //   494: aload 9
    //   496: ifnonnull +40 -> 536
    //   499: aload 6
    //   501: ifnull +9 -> 510
    //   504: aload 6
    //   506: aconst_null
    //   507: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   510: aload 20
    //   512: ifnull +10 -> 522
    //   515: aload_0
    //   516: aload 20
    //   518: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   521: pop
    //   522: aload 9
    //   524: ifnull +10 -> 534
    //   527: aload_0
    //   528: aload 9
    //   530: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   533: pop
    //   534: aconst_null
    //   535: areturn
    //   536: aload 9
    //   538: astore 10
    //   540: aload 15
    //   542: astore 12
    //   544: aload 9
    //   546: astore 11
    //   548: aload 16
    //   550: astore 13
    //   552: aload 9
    //   554: aload_0
    //   555: getfield 202	android/content/ContentResolver:mPackageName	Ljava/lang/String;
    //   558: aload_1
    //   559: aload_2
    //   560: aload_3
    //   561: aload 4
    //   563: aload 5
    //   565: aload 14
    //   567: invokeinterface 1103 8 0
    //   572: astore 14
    //   574: aload 9
    //   576: astore 4
    //   578: aload 14
    //   580: astore 9
    //   582: goto -189 -> 393
    //   585: aload 4
    //   587: astore 10
    //   589: aload 9
    //   591: astore 12
    //   593: aload 4
    //   595: astore 11
    //   597: aload 9
    //   599: astore 13
    //   601: aload 9
    //   603: invokeinterface 1109 1 0
    //   608: pop
    //   609: aload 4
    //   611: astore 10
    //   613: aload 9
    //   615: astore 12
    //   617: aload 4
    //   619: astore 11
    //   621: aload 9
    //   623: astore 13
    //   625: aload_0
    //   626: invokestatic 696	android/os/SystemClock:uptimeMillis	()J
    //   629: lload 7
    //   631: lsub
    //   632: aload_1
    //   633: aload_2
    //   634: aload_3
    //   635: aload 5
    //   637: invokespecial 1111	android/content/ContentResolver:maybeLogQueryToEventLog	(JLandroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   640: aload 4
    //   642: ifnull +59 -> 701
    //   645: aload 4
    //   647: astore_1
    //   648: aload 4
    //   650: astore 10
    //   652: aload 9
    //   654: astore 12
    //   656: aload 4
    //   658: astore 11
    //   660: aload 9
    //   662: astore 13
    //   664: new 8	android/content/ContentResolver$CursorWrapperInner
    //   667: dup
    //   668: aload_0
    //   669: aload 9
    //   671: aload_1
    //   672: invokespecial 1114	android/content/ContentResolver$CursorWrapperInner:<init>	(Landroid/content/ContentResolver;Landroid/database/Cursor;Landroid/content/IContentProvider;)V
    //   675: astore_1
    //   676: aload 6
    //   678: ifnull +9 -> 687
    //   681: aload 6
    //   683: aconst_null
    //   684: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   687: aload 20
    //   689: ifnull +10 -> 699
    //   692: aload_0
    //   693: aload 20
    //   695: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   698: pop
    //   699: aload_1
    //   700: areturn
    //   701: aload 4
    //   703: astore 10
    //   705: aload 9
    //   707: astore 12
    //   709: aload 4
    //   711: astore 11
    //   713: aload 9
    //   715: astore 13
    //   717: aload_0
    //   718: aload_1
    //   719: invokevirtual 590	android/content/ContentResolver:acquireProvider	(Landroid/net/Uri;)Landroid/content/IContentProvider;
    //   722: astore_1
    //   723: goto -75 -> 648
    //   726: astore_1
    //   727: aload 12
    //   729: ifnull +10 -> 739
    //   732: aload 12
    //   734: invokeinterface 1106 1 0
    //   739: aload 6
    //   741: ifnull +9 -> 750
    //   744: aload 6
    //   746: aconst_null
    //   747: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   750: aload 20
    //   752: ifnull +10 -> 762
    //   755: aload_0
    //   756: aload 20
    //   758: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   761: pop
    //   762: aload 10
    //   764: ifnull +10 -> 774
    //   767: aload_0
    //   768: aload 10
    //   770: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   773: pop
    //   774: aconst_null
    //   775: areturn
    //   776: astore_1
    //   777: aload 13
    //   779: ifnull +10 -> 789
    //   782: aload 13
    //   784: invokeinterface 1106 1 0
    //   789: aload 6
    //   791: ifnull +9 -> 800
    //   794: aload 6
    //   796: aconst_null
    //   797: invokevirtual 1010	android/os/CancellationSignal:setRemote	(Landroid/os/ICancellationSignal;)V
    //   800: aload 20
    //   802: ifnull +10 -> 812
    //   805: aload_0
    //   806: aload 20
    //   808: invokevirtual 1017	android/content/ContentResolver:releaseUnstableProvider	(Landroid/content/IContentProvider;)Z
    //   811: pop
    //   812: aload 11
    //   814: ifnull +10 -> 824
    //   817: aload_0
    //   818: aload 11
    //   820: invokevirtual 709	android/content/ContentResolver:releaseProvider	(Landroid/content/IContentProvider;)Z
    //   823: pop
    //   824: aload_1
    //   825: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	826	0	this	ContentResolver
    //   0	826	1	paramUri	Uri
    //   0	826	2	paramArrayOfString1	String[]
    //   0	826	3	paramString1	String
    //   0	826	4	paramArrayOfString2	String[]
    //   0	826	5	paramString2	String
    //   0	826	6	paramCancellationSignal	CancellationSignal
    //   268	362	7	l	long
    //   52	662	9	localObject1	Object
    //   36	314	10	localObject2	Object
    //   447	1	10	localDeadObjectException	android.os.DeadObjectException
    //   451	318	10	localObject3	Object
    //   33	786	11	localObject4	Object
    //   255	478	12	localObject5	Object
    //   263	520	13	localObject6	Object
    //   271	308	14	localObject7	Object
    //   247	294	15	localObject8	Object
    //   244	305	16	localObject9	Object
    //   241	149	17	localObject10	Object
    //   238	242	18	localObject11	Object
    //   383	3	19	localCursor	Cursor
    //   225	582	20	localIContentProvider	IContentProvider
    // Exception table:
    //   from	to	target	type
    //   363	385	447	android/os/DeadObjectException
    //   265	270	726	android/os/RemoteException
    //   294	299	726	android/os/RemoteException
    //   315	324	726	android/os/RemoteException
    //   340	347	726	android/os/RemoteException
    //   363	385	726	android/os/RemoteException
    //   465	471	726	android/os/RemoteException
    //   487	494	726	android/os/RemoteException
    //   552	574	726	android/os/RemoteException
    //   601	609	726	android/os/RemoteException
    //   625	640	726	android/os/RemoteException
    //   664	676	726	android/os/RemoteException
    //   717	723	726	android/os/RemoteException
    //   265	270	776	finally
    //   294	299	776	finally
    //   315	324	776	finally
    //   340	347	776	finally
    //   363	385	776	finally
    //   465	471	776	finally
    //   487	494	776	finally
    //   552	574	776	finally
    //   601	609	776	finally
    //   625	640	776	finally
    //   664	676	776	finally
    //   717	723	776	finally
  }
  
  public final void registerContentObserver(Uri paramUri, boolean paramBoolean, ContentObserver paramContentObserver)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    Preconditions.checkNotNull(paramContentObserver, "observer");
    registerContentObserver(ContentProvider.getUriWithoutUserId(paramUri), paramBoolean, paramContentObserver, ContentProvider.getUserIdFromUri(paramUri, UserHandle.myUserId()));
  }
  
  public final void registerContentObserver(Uri paramUri, boolean paramBoolean, ContentObserver paramContentObserver, int paramInt)
  {
    try
    {
      getContentService().registerContentObserver(paramUri, paramBoolean, paramContentObserver.getContentObserver(), paramInt);
      return;
    }
    catch (RemoteException paramUri) {}
  }
  
  public void releasePersistableUriPermission(Uri paramUri, int paramInt)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    try
    {
      ActivityManagerNative.getDefault().releasePersistableUriPermission(ContentProvider.getUriWithoutUserId(paramUri), paramInt, resolveUserId(paramUri));
      return;
    }
    catch (RemoteException paramUri) {}
  }
  
  public abstract boolean releaseProvider(IContentProvider paramIContentProvider);
  
  public abstract boolean releaseUnstableProvider(IContentProvider paramIContentProvider);
  
  public int resolveUserId(Uri paramUri)
  {
    return ContentProvider.getUserIdFromUri(paramUri, this.mContext.getUserId());
  }
  
  @Deprecated
  public void startSync(Uri paramUri, Bundle paramBundle)
  {
    String str1 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramBundle != null)
    {
      String str2 = paramBundle.getString("account");
      localObject1 = localObject2;
      if (!TextUtils.isEmpty(str2)) {
        localObject1 = new Account(str2, "com.google");
      }
      paramBundle.remove("account");
    }
    if (paramUri != null) {
      str1 = paramUri.getAuthority();
    }
    requestSync((Account)localObject1, str1, paramBundle);
  }
  
  public void takePersistableUriPermission(Uri paramUri, int paramInt)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    try
    {
      ActivityManagerNative.getDefault().takePersistableUriPermission(ContentProvider.getUriWithoutUserId(paramUri), paramInt, resolveUserId(paramUri));
      return;
    }
    catch (RemoteException paramUri) {}
  }
  
  public final Uri uncanonicalize(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri, "url");
    IContentProvider localIContentProvider = acquireProvider(paramUri);
    if (localIContentProvider == null) {
      return null;
    }
    try
    {
      paramUri = localIContentProvider.uncanonicalize(this.mPackageName, paramUri);
      releaseProvider(localIContentProvider);
      return paramUri;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return null;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  public final void unregisterContentObserver(ContentObserver paramContentObserver)
  {
    Preconditions.checkNotNull(paramContentObserver, "observer");
    try
    {
      paramContentObserver = paramContentObserver.releaseContentObserver();
      if (paramContentObserver != null) {
        getContentService().unregisterContentObserver(paramContentObserver);
      }
      return;
    }
    catch (RemoteException paramContentObserver) {}
  }
  
  public abstract void unstableProviderDied(IContentProvider paramIContentProvider);
  
  public final int update(@RequiresPermission.Write Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    Preconditions.checkNotNull(paramUri, "uri");
    IContentProvider localIContentProvider;
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestModifyPermission(paramUri)))
    {
      localIContentProvider = acquireProvider(paramUri);
      if (localIContentProvider == null) {
        throw new IllegalArgumentException("Unknown URI " + paramUri);
      }
    }
    else
    {
      return -1;
    }
    try
    {
      long l = SystemClock.uptimeMillis();
      int i = localIContentProvider.update(this.mPackageName, paramUri, paramContentValues, paramString, paramArrayOfString);
      maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - l, paramUri, "update", paramString);
      releaseProvider(localIContentProvider);
      return i;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      releaseProvider(localIContentProvider);
      return -1;
    }
    finally
    {
      paramUri = finally;
      releaseProvider(localIContentProvider);
      throw paramUri;
    }
  }
  
  private final class CursorWrapperInner
    extends CrossProcessCursorWrapper
  {
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final IContentProvider mContentProvider;
    private final AtomicBoolean mProviderReleased = new AtomicBoolean();
    
    CursorWrapperInner(Cursor paramCursor, IContentProvider paramIContentProvider)
    {
      super();
      this.mContentProvider = paramIContentProvider;
      this.mCloseGuard.open("close");
    }
    
    public void close()
    {
      this.mCloseGuard.close();
      super.close();
      if (this.mProviderReleased.compareAndSet(false, true)) {
        ContentResolver.this.releaseProvider(this.mContentProvider);
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        this.mCloseGuard.warnIfOpen();
        close();
        return;
      }
      finally
      {
        super.finalize();
      }
    }
  }
  
  public class OpenResourceIdResult
  {
    public int id;
    public Resources r;
    
    public OpenResourceIdResult() {}
  }
  
  private final class ParcelFileDescriptorInner
    extends ParcelFileDescriptor
  {
    private final IContentProvider mContentProvider;
    private final AtomicBoolean mProviderReleased = new AtomicBoolean();
    
    ParcelFileDescriptorInner(ParcelFileDescriptor paramParcelFileDescriptor, IContentProvider paramIContentProvider)
    {
      super();
      this.mContentProvider = paramIContentProvider;
    }
    
    public void releaseResources()
    {
      if (this.mProviderReleased.compareAndSet(false, true)) {
        ContentResolver.this.releaseProvider(this.mContentProvider);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */