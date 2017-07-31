package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncStatusObserver;
import android.content.PeriodicSync;
import android.content.SyncInfo;
import android.content.SyncRequest.Builder;
import android.content.SyncStatusInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SyncStorageEngine
  extends Handler
{
  private static final int ACCOUNTS_VERSION = 3;
  private static final double DEFAULT_FLEX_PERCENT_SYNC = 0.04D;
  private static final long DEFAULT_MIN_FLEX_ALLOWED_SECS = 5L;
  private static final long DEFAULT_POLL_FREQUENCY_SECONDS = 86400L;
  public static final int EVENT_START = 0;
  public static final int EVENT_STOP = 1;
  public static final int MAX_HISTORY = 100;
  public static final String MESG_CANCELED = "canceled";
  public static final String MESG_SUCCESS = "success";
  static final long MILLIS_IN_4WEEKS = 2419200000L;
  private static final int MSG_WRITE_STATISTICS = 2;
  private static final int MSG_WRITE_STATUS = 1;
  public static final long NOT_IN_BACKOFF_MODE = -1L;
  public static final String[] SOURCES = { "SERVER", "LOCAL", "POLL", "USER", "PERIODIC", "SERVICE" };
  public static final int SOURCE_LOCAL = 1;
  public static final int SOURCE_PERIODIC = 4;
  public static final int SOURCE_POLL = 2;
  public static final int SOURCE_SERVER = 0;
  public static final int SOURCE_USER = 3;
  public static final int STATISTICS_FILE_END = 0;
  public static final int STATISTICS_FILE_ITEM = 101;
  public static final int STATISTICS_FILE_ITEM_OLD = 100;
  public static final int STATUS_FILE_END = 0;
  public static final int STATUS_FILE_ITEM = 100;
  private static final boolean SYNC_ENABLED_DEFAULT = false;
  private static final String TAG = "SyncManager";
  private static final String TAG_FILE = "SyncManagerFile";
  private static final long WRITE_STATISTICS_DELAY = 1800000L;
  private static final long WRITE_STATUS_DELAY = 600000L;
  private static final String XML_ATTR_ENABLED = "enabled";
  private static final String XML_ATTR_LISTEN_FOR_TICKLES = "listen-for-tickles";
  private static final String XML_ATTR_NEXT_AUTHORITY_ID = "nextAuthorityId";
  private static final String XML_ATTR_SYNC_RANDOM_OFFSET = "offsetInSeconds";
  private static final String XML_ATTR_USER = "user";
  private static final String XML_TAG_LISTEN_FOR_TICKLES = "listenForTickles";
  private static PeriodicSyncAddedListener mPeriodicSyncAddedListener;
  private static HashMap<String, String> sAuthorityRenames = new HashMap();
  private static volatile SyncStorageEngine sSyncStorageEngine = null;
  private final AtomicFile mAccountInfoFile;
  private final HashMap<AccountAndUser, AccountInfo> mAccounts = new HashMap();
  private final SparseArray<AuthorityInfo> mAuthorities = new SparseArray();
  private OnAuthorityRemovedListener mAuthorityRemovedListener;
  private final Calendar mCal;
  private final RemoteCallbackList<ISyncStatusObserver> mChangeListeners = new RemoteCallbackList();
  private final Context mContext;
  private final SparseArray<ArrayList<SyncInfo>> mCurrentSyncs = new SparseArray();
  private final DayStats[] mDayStats = new DayStats[28];
  private boolean mDefaultMasterSyncAutomatically;
  private boolean mGrantSyncAdaptersAccountAccess;
  private SparseArray<Boolean> mMasterSyncAutomatically = new SparseArray();
  private int mNextAuthorityId = 0;
  private int mNextHistoryId = 0;
  private final ArrayMap<ComponentName, SparseArray<AuthorityInfo>> mServices = new ArrayMap();
  private final AtomicFile mStatisticsFile;
  private final AtomicFile mStatusFile;
  private final ArrayList<SyncHistoryItem> mSyncHistory = new ArrayList();
  private int mSyncRandomOffset;
  private OnSyncRequestListener mSyncRequestListener;
  private final SparseArray<SyncStatusInfo> mSyncStatus = new SparseArray();
  private int mYear;
  private int mYearInDays;
  
  static
  {
    sAuthorityRenames.put("contacts", "com.android.contacts");
    sAuthorityRenames.put("calendar", "com.android.calendar");
  }
  
  private SyncStorageEngine(Context paramContext, File paramFile)
  {
    this.mContext = paramContext;
    sSyncStorageEngine = this;
    this.mCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
    this.mDefaultMasterSyncAutomatically = this.mContext.getResources().getBoolean(17956984);
    paramContext = new File(new File(paramFile, "system"), "sync");
    paramContext.mkdirs();
    maybeDeleteLegacyPendingInfoLocked(paramContext);
    this.mAccountInfoFile = new AtomicFile(new File(paramContext, "accounts.xml"));
    this.mStatusFile = new AtomicFile(new File(paramContext, "status.bin"));
    this.mStatisticsFile = new AtomicFile(new File(paramContext, "stats.bin"));
    readAccountInfoLocked();
    readStatusLocked();
    readStatisticsLocked();
    readAndDeleteLegacyAccountInfoLocked();
    writeAccountInfoLocked();
    writeStatusLocked();
    writeStatisticsLocked();
  }
  
  public static long calculateDefaultFlexTime(long paramLong)
  {
    if (paramLong < 5L) {
      return 0L;
    }
    if (paramLong < 86400L) {
      return (paramLong * 0.04D);
    }
    return 3456L;
  }
  
  private AuthorityInfo createAuthorityLocked(EndPoint paramEndPoint, int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramInt < 0)
    {
      i = this.mNextAuthorityId;
      this.mNextAuthorityId += 1;
      paramBoolean = true;
    }
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "created a new AuthorityInfo for " + paramEndPoint);
    }
    paramEndPoint = new AuthorityInfo(paramEndPoint, i);
    this.mAuthorities.put(i, paramEndPoint);
    if (paramBoolean) {
      writeAccountInfoLocked();
    }
    return paramEndPoint;
  }
  
  private Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked(AuthorityInfo paramAuthorityInfo)
  {
    SyncStatusInfo localSyncStatusInfo = getOrCreateSyncStatusLocked(paramAuthorityInfo.ident);
    return Pair.create(new AuthorityInfo(paramAuthorityInfo), new SyncStatusInfo(localSyncStatusInfo));
  }
  
  private AuthorityInfo getAuthorityLocked(EndPoint paramEndPoint, String paramString)
  {
    Object localObject = new AccountAndUser(paramEndPoint.account, paramEndPoint.userId);
    AccountInfo localAccountInfo = (AccountInfo)this.mAccounts.get(localObject);
    if (localAccountInfo == null)
    {
      if ((paramString != null) && (Log.isLoggable("SyncManager", 2))) {
        Slog.v("SyncManager", paramString + ": unknown account " + localObject);
      }
      return null;
    }
    localObject = (AuthorityInfo)localAccountInfo.authorities.get(paramEndPoint.provider);
    if (localObject == null)
    {
      if ((paramString != null) && (Log.isLoggable("SyncManager", 2))) {
        Slog.v("SyncManager", paramString + ": unknown provider " + paramEndPoint.provider);
      }
      return null;
    }
    return (AuthorityInfo)localObject;
  }
  
  private int getCurrentDayLocked()
  {
    this.mCal.setTimeInMillis(System.currentTimeMillis());
    int i = this.mCal.get(6);
    if (this.mYear != this.mCal.get(1))
    {
      this.mYear = this.mCal.get(1);
      this.mCal.clear();
      this.mCal.set(1, this.mYear);
      this.mYearInDays = ((int)(this.mCal.getTimeInMillis() / 86400000L));
    }
    return this.mYearInDays + i;
  }
  
  private List<SyncInfo> getCurrentSyncs(int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      List localList = getCurrentSyncsLocked(paramInt);
      return localList;
    }
  }
  
  private List<SyncInfo> getCurrentSyncsLocked(int paramInt)
  {
    ArrayList localArrayList2 = (ArrayList)this.mCurrentSyncs.get(paramInt);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.mCurrentSyncs.put(paramInt, localArrayList1);
    }
    return localArrayList1;
  }
  
  static int getIntColumn(Cursor paramCursor, String paramString)
  {
    return paramCursor.getInt(paramCursor.getColumnIndex(paramString));
  }
  
  static long getLongColumn(Cursor paramCursor, String paramString)
  {
    return paramCursor.getLong(paramCursor.getColumnIndex(paramString));
  }
  
  private AuthorityInfo getOrCreateAuthorityLocked(EndPoint paramEndPoint, int paramInt, boolean paramBoolean)
  {
    Object localObject3 = new AccountAndUser(paramEndPoint.account, paramEndPoint.userId);
    Object localObject2 = (AccountInfo)this.mAccounts.get(localObject3);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new AccountInfo((AccountAndUser)localObject3);
      this.mAccounts.put(localObject3, localObject1);
    }
    localObject3 = (AuthorityInfo)((AccountInfo)localObject1).authorities.get(paramEndPoint.provider);
    localObject2 = localObject3;
    if (localObject3 == null)
    {
      localObject2 = createAuthorityLocked(paramEndPoint, paramInt, paramBoolean);
      ((AccountInfo)localObject1).authorities.put(paramEndPoint.provider, localObject2);
    }
    return (AuthorityInfo)localObject2;
  }
  
  private SyncStatusInfo getOrCreateSyncStatusLocked(int paramInt)
  {
    SyncStatusInfo localSyncStatusInfo2 = (SyncStatusInfo)this.mSyncStatus.get(paramInt);
    SyncStatusInfo localSyncStatusInfo1 = localSyncStatusInfo2;
    if (localSyncStatusInfo2 == null)
    {
      localSyncStatusInfo1 = new SyncStatusInfo(paramInt);
      this.mSyncStatus.put(paramInt, localSyncStatusInfo1);
    }
    return localSyncStatusInfo1;
  }
  
  public static SyncStorageEngine getSingleton()
  {
    if (sSyncStorageEngine == null) {
      throw new IllegalStateException("not initialized");
    }
    return sSyncStorageEngine;
  }
  
  public static void init(Context paramContext)
  {
    if (sSyncStorageEngine != null) {
      return;
    }
    sSyncStorageEngine = new SyncStorageEngine(paramContext, Environment.getDataDirectory());
  }
  
  private void maybeDeleteLegacyPendingInfoLocked(File paramFile)
  {
    paramFile = new File(paramFile, "pending.bin");
    if (!paramFile.exists()) {
      return;
    }
    paramFile.delete();
  }
  
  private boolean maybeMigrateSettingsForRenamedAuthorities()
  {
    boolean bool1 = false;
    Object localObject1 = new ArrayList();
    int j = this.mAuthorities.size();
    int i = 0;
    Object localObject2;
    if (i < j)
    {
      localObject2 = (AuthorityInfo)this.mAuthorities.valueAt(i);
      String str = (String)sAuthorityRenames.get(((AuthorityInfo)localObject2).target.provider);
      boolean bool2;
      if (str == null) {
        bool2 = bool1;
      }
      for (;;)
      {
        i += 1;
        bool1 = bool2;
        break;
        ((ArrayList)localObject1).add(localObject2);
        bool2 = bool1;
        if (((AuthorityInfo)localObject2).enabled)
        {
          localObject2 = new EndPoint(((AuthorityInfo)localObject2).target.account, str, ((AuthorityInfo)localObject2).target.userId);
          bool2 = bool1;
          if (getAuthorityLocked((EndPoint)localObject2, "cleanup") == null)
          {
            getOrCreateAuthorityLocked((EndPoint)localObject2, -1, false).enabled = true;
            bool2 = true;
          }
        }
      }
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (AuthorityInfo)((Iterator)localObject1).next();
      removeAuthorityLocked(((AuthorityInfo)localObject2).target.account, ((AuthorityInfo)localObject2).target.userId, ((AuthorityInfo)localObject2).target.provider, false);
      bool1 = true;
    }
    return bool1;
  }
  
  public static SyncStorageEngine newTestInstance(Context paramContext)
  {
    return new SyncStorageEngine(paramContext, paramContext.getFilesDir());
  }
  
  private AuthorityInfo parseAuthority(XmlPullParser paramXmlPullParser, int paramInt)
  {
    Object localObject1 = null;
    int i = -1;
    try
    {
      j = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "id"));
      i = j;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        try
        {
          Object localObject3;
          Object localObject4;
          String str4;
          Object localObject2;
          paramXmlPullParser.syncable = paramInt;
          localObject1 = paramXmlPullParser;
          return (AuthorityInfo)localObject1;
        }
        catch (NumberFormatException localNumberFormatException2)
        {
          int j;
          boolean bool;
          if (!"unknown".equals(localObject1)) {
            continue;
          }
          paramXmlPullParser.syncable = -1;
          return paramXmlPullParser;
          if (!Boolean.parseBoolean((String)localObject1)) {
            break label528;
          }
        }
        localNullPointerException = localNullPointerException;
        Slog.e("SyncManager", "the id of the authority is null", localNullPointerException);
      }
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      String str1;
      String str2;
      String str3;
      for (;;)
      {
        Slog.e("SyncManager", "error parsing the id of the authority", localNumberFormatException1);
        continue;
        label466:
        j = Integer.parseInt((String)localObject1);
        continue;
        label476:
        bool = true;
        continue;
        label482:
        paramInt = Integer.parseInt((String)localObject1);
      }
      label528:
      for (paramInt = 1;; paramInt = 0)
      {
        paramXmlPullParser.syncable = paramInt;
        return paramXmlPullParser;
      }
      label533:
      Slog.w("SyncManager", "Failure adding authority: account=" + str3 + " auth=" + str1 + " enabled=" + str2 + " syncable=" + (String)localObject1);
    }
    if (i >= 0)
    {
      str1 = paramXmlPullParser.getAttributeValue(null, "authority");
      str2 = paramXmlPullParser.getAttributeValue(null, "enabled");
      localObject3 = paramXmlPullParser.getAttributeValue(null, "syncable");
      str3 = paramXmlPullParser.getAttributeValue(null, "account");
      localObject4 = paramXmlPullParser.getAttributeValue(null, "type");
      localObject1 = paramXmlPullParser.getAttributeValue(null, "user");
      str4 = paramXmlPullParser.getAttributeValue(null, "package");
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "class");
      if (localObject1 != null) {
        break label466;
      }
      j = 0;
      localObject2 = localObject4;
      localObject1 = localObject3;
      if (localObject4 == null)
      {
        localObject2 = localObject4;
        localObject1 = localObject3;
        if (str4 == null)
        {
          localObject2 = "com.google";
          localObject1 = String.valueOf(-1);
        }
      }
      localObject4 = (AuthorityInfo)this.mAuthorities.get(i);
      if (Log.isLoggable("SyncManagerFile", 2)) {
        Slog.v("SyncManagerFile", "Adding authority: account=" + str3 + " accountType=" + (String)localObject2 + " auth=" + str1 + " package=" + str4 + " class=" + paramXmlPullParser + " user=" + j + " enabled=" + str2 + " syncable=" + (String)localObject1);
      }
      paramXmlPullParser = (XmlPullParser)localObject4;
      if (localObject4 == null)
      {
        if (Log.isLoggable("SyncManagerFile", 2)) {
          Slog.v("SyncManagerFile", "Creating authority entry");
        }
        paramXmlPullParser = null;
        localObject3 = paramXmlPullParser;
        if (str3 != null)
        {
          localObject3 = paramXmlPullParser;
          if (str1 != null) {
            localObject3 = new EndPoint(new Account(str3, (String)localObject2), str1, j);
          }
        }
        paramXmlPullParser = (XmlPullParser)localObject4;
        if (localObject3 != null)
        {
          localObject2 = getOrCreateAuthorityLocked((EndPoint)localObject3, i, false);
          paramXmlPullParser = (XmlPullParser)localObject2;
          if (paramInt > 0)
          {
            ((AuthorityInfo)localObject2).periodicSyncs.clear();
            paramXmlPullParser = (XmlPullParser)localObject2;
          }
        }
      }
      if (paramXmlPullParser == null) {
        break label533;
      }
      if (str2 == null) {
        break label476;
      }
      bool = Boolean.parseBoolean(str2);
      paramXmlPullParser.enabled = bool;
      if (localObject1 != null) {
        break label482;
      }
      paramInt = -1;
    }
    return paramXmlPullParser;
  }
  
  private void parseExtra(XmlPullParser paramXmlPullParser, Bundle paramBundle)
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "name");
    String str2 = paramXmlPullParser.getAttributeValue(null, "type");
    String str3 = paramXmlPullParser.getAttributeValue(null, "value1");
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "value2");
    try
    {
      if ("long".equals(str2))
      {
        paramBundle.putLong(str1, Long.parseLong(str3));
        return;
      }
      if ("integer".equals(str2))
      {
        paramBundle.putInt(str1, Integer.parseInt(str3));
        return;
      }
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      Slog.e("SyncManager", "error parsing bundle value", paramXmlPullParser);
      return;
      if ("double".equals(str2))
      {
        paramBundle.putDouble(str1, Double.parseDouble(str3));
        return;
      }
    }
    catch (NullPointerException paramXmlPullParser)
    {
      Slog.e("SyncManager", "error parsing bundle value", paramXmlPullParser);
      return;
    }
    if ("float".equals(str2))
    {
      paramBundle.putFloat(str1, Float.parseFloat(str3));
      return;
    }
    if ("boolean".equals(str2))
    {
      paramBundle.putBoolean(str1, Boolean.parseBoolean(str3));
      return;
    }
    if ("string".equals(str2))
    {
      paramBundle.putString(str1, str3);
      return;
    }
    if ("account".equals(str2)) {
      paramBundle.putParcelable(str1, new Account(str3, paramXmlPullParser));
    }
  }
  
  private void parseListenForTickles(XmlPullParser paramXmlPullParser)
  {
    String str = paramXmlPullParser.getAttributeValue(null, "user");
    int i = 0;
    try
    {
      int j = Integer.parseInt(str);
      i = j;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        Slog.e("SyncManager", "the user in listen-for-tickles is null", localNullPointerException);
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;)
      {
        Slog.e("SyncManager", "error parsing the user for listen-for-tickles", localNumberFormatException);
        continue;
        boolean bool = true;
      }
    }
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "enabled");
    if (paramXmlPullParser != null)
    {
      bool = Boolean.parseBoolean(paramXmlPullParser);
      this.mMasterSyncAutomatically.put(i, Boolean.valueOf(bool));
      return;
    }
  }
  
  /* Error */
  private PeriodicSync parsePeriodicSync(XmlPullParser paramXmlPullParser, AuthorityInfo paramAuthorityInfo)
  {
    // Byte code:
    //   0: new 671	android/os/Bundle
    //   3: dup
    //   4: invokespecial 734	android/os/Bundle:<init>	()V
    //   7: astore 7
    //   9: aload_1
    //   10: aconst_null
    //   11: ldc_w 736
    //   14: invokeinterface 575 3 0
    //   19: astore 8
    //   21: aload_1
    //   22: aconst_null
    //   23: ldc_w 738
    //   26: invokeinterface 575 3 0
    //   31: astore_1
    //   32: aload 8
    //   34: invokestatic 669	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   37: lstore 5
    //   39: aload_1
    //   40: invokestatic 669	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   43: lstore_3
    //   44: new 740	android/content/PeriodicSync
    //   47: dup
    //   48: aload_2
    //   49: getfield 522	com/android/server/content/SyncStorageEngine$AuthorityInfo:target	Lcom/android/server/content/SyncStorageEngine$EndPoint;
    //   52: getfield 398	com/android/server/content/SyncStorageEngine$EndPoint:account	Landroid/accounts/Account;
    //   55: aload_2
    //   56: getfield 522	com/android/server/content/SyncStorageEngine$AuthorityInfo:target	Lcom/android/server/content/SyncStorageEngine$EndPoint;
    //   59: getfield 416	com/android/server/content/SyncStorageEngine$EndPoint:provider	Ljava/lang/String;
    //   62: aload 7
    //   64: lload 5
    //   66: lload_3
    //   67: invokespecial 743	android/content/PeriodicSync:<init>	(Landroid/accounts/Account;Ljava/lang/String;Landroid/os/Bundle;JJ)V
    //   70: astore_1
    //   71: aload_2
    //   72: getfield 626	com/android/server/content/SyncStorageEngine$AuthorityInfo:periodicSyncs	Ljava/util/ArrayList;
    //   75: aload_1
    //   76: invokevirtual 526	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   79: pop
    //   80: aload_1
    //   81: areturn
    //   82: astore_1
    //   83: ldc 83
    //   85: ldc_w 745
    //   88: aload_1
    //   89: invokestatic 641	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   92: pop
    //   93: aconst_null
    //   94: areturn
    //   95: astore_1
    //   96: ldc 83
    //   98: ldc_w 747
    //   101: aload_1
    //   102: invokestatic 641	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   105: pop
    //   106: aconst_null
    //   107: areturn
    //   108: astore_1
    //   109: lload 5
    //   111: invokestatic 749	com/android/server/content/SyncStorageEngine:calculateDefaultFlexTime	(J)J
    //   114: lstore_3
    //   115: ldc 83
    //   117: new 339	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 340	java/lang/StringBuilder:<init>	()V
    //   124: ldc_w 751
    //   127: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: lload 5
    //   132: invokevirtual 754	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   135: ldc_w 756
    //   138: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: lload_3
    //   142: invokevirtual 754	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   145: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokestatic 759	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: goto -108 -> 44
    //   155: astore 8
    //   157: lload 5
    //   159: invokestatic 749	com/android/server/content/SyncStorageEngine:calculateDefaultFlexTime	(J)J
    //   162: lstore_3
    //   163: ldc 83
    //   165: new 339	java/lang/StringBuilder
    //   168: dup
    //   169: invokespecial 340	java/lang/StringBuilder:<init>	()V
    //   172: ldc_w 761
    //   175: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload_1
    //   179: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: ldc_w 763
    //   185: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: lload_3
    //   189: invokevirtual 754	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   192: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   195: invokestatic 765	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: goto -155 -> 44
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	202	0	this	SyncStorageEngine
    //   0	202	1	paramXmlPullParser	XmlPullParser
    //   0	202	2	paramAuthorityInfo	AuthorityInfo
    //   43	146	3	l1	long
    //   37	121	5	l2	long
    //   7	56	7	localBundle	Bundle
    //   19	14	8	str	String
    //   155	1	8	localNumberFormatException	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   32	39	82	java/lang/NullPointerException
    //   32	39	95	java/lang/NumberFormatException
    //   39	44	108	java/lang/NullPointerException
    //   39	44	155	java/lang/NumberFormatException
  }
  
  /* Error */
  private void readAccountInfoLocked()
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_1
    //   2: aconst_null
    //   3: astore 12
    //   5: aconst_null
    //   6: astore 9
    //   8: aconst_null
    //   9: astore 11
    //   11: iload_1
    //   12: istore 4
    //   14: iload_1
    //   15: istore 5
    //   17: iload_1
    //   18: istore_2
    //   19: aload_0
    //   20: getfield 296	com/android/server/content/SyncStorageEngine:mAccountInfoFile	Landroid/util/AtomicFile;
    //   23: invokevirtual 773	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   26: astore 10
    //   28: aload 10
    //   30: astore 11
    //   32: iload_1
    //   33: istore 4
    //   35: aload 10
    //   37: astore 12
    //   39: iload_1
    //   40: istore 5
    //   42: aload 10
    //   44: astore 9
    //   46: iload_1
    //   47: istore_2
    //   48: ldc 86
    //   50: iconst_2
    //   51: invokestatic 337	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   54: ifeq +55 -> 109
    //   57: aload 10
    //   59: astore 11
    //   61: iload_1
    //   62: istore 4
    //   64: aload 10
    //   66: astore 12
    //   68: iload_1
    //   69: istore 5
    //   71: aload 10
    //   73: astore 9
    //   75: iload_1
    //   76: istore_2
    //   77: ldc 86
    //   79: new 339	java/lang/StringBuilder
    //   82: dup
    //   83: invokespecial 340	java/lang/StringBuilder:<init>	()V
    //   86: ldc_w 775
    //   89: invokevirtual 346	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: aload_0
    //   93: getfield 296	com/android/server/content/SyncStorageEngine:mAccountInfoFile	Landroid/util/AtomicFile;
    //   96: invokevirtual 778	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   99: invokevirtual 349	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   102: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   105: invokestatic 359	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   108: pop
    //   109: aload 10
    //   111: astore 11
    //   113: iload_1
    //   114: istore 4
    //   116: aload 10
    //   118: astore 12
    //   120: iload_1
    //   121: istore 5
    //   123: aload 10
    //   125: astore 9
    //   127: iload_1
    //   128: istore_2
    //   129: invokestatic 784	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   132: astore 17
    //   134: aload 10
    //   136: astore 11
    //   138: iload_1
    //   139: istore 4
    //   141: aload 10
    //   143: astore 12
    //   145: iload_1
    //   146: istore 5
    //   148: aload 10
    //   150: astore 9
    //   152: iload_1
    //   153: istore_2
    //   154: aload 17
    //   156: aload 10
    //   158: getstatic 790	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   161: invokevirtual 794	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   164: invokeinterface 798 3 0
    //   169: aload 10
    //   171: astore 11
    //   173: iload_1
    //   174: istore 4
    //   176: aload 10
    //   178: astore 12
    //   180: iload_1
    //   181: istore 5
    //   183: aload 10
    //   185: astore 9
    //   187: iload_1
    //   188: istore_2
    //   189: aload 17
    //   191: invokeinterface 801 1 0
    //   196: istore_3
    //   197: iload_3
    //   198: istore_2
    //   199: iload_2
    //   200: iconst_2
    //   201: if_icmpeq +41 -> 242
    //   204: iload_2
    //   205: iconst_1
    //   206: if_icmpeq +36 -> 242
    //   209: aload 10
    //   211: astore 11
    //   213: iload_1
    //   214: istore 4
    //   216: aload 10
    //   218: astore 12
    //   220: iload_1
    //   221: istore 5
    //   223: aload 10
    //   225: astore 9
    //   227: iload_1
    //   228: istore_2
    //   229: aload 17
    //   231: invokeinterface 803 1 0
    //   236: istore_3
    //   237: iload_3
    //   238: istore_2
    //   239: goto -40 -> 199
    //   242: iload_2
    //   243: iconst_1
    //   244: if_icmpne +58 -> 302
    //   247: aload 10
    //   249: astore 11
    //   251: iload_1
    //   252: istore 4
    //   254: aload 10
    //   256: astore 12
    //   258: iload_1
    //   259: istore 5
    //   261: aload 10
    //   263: astore 9
    //   265: iload_1
    //   266: istore_2
    //   267: ldc 83
    //   269: ldc_w 805
    //   272: invokestatic 808	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   275: pop
    //   276: aload_0
    //   277: iconst_0
    //   278: aload_0
    //   279: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   282: invokestatic 814	java/lang/Math:max	(II)I
    //   285: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   288: aload 10
    //   290: ifnull +8 -> 298
    //   293: aload 10
    //   295: invokevirtual 819	java/io/FileInputStream:close	()V
    //   298: return
    //   299: astore 9
    //   301: return
    //   302: iload_1
    //   303: istore_3
    //   304: aload 10
    //   306: astore 11
    //   308: iload_1
    //   309: istore 4
    //   311: aload 10
    //   313: astore 12
    //   315: iload_1
    //   316: istore 5
    //   318: aload 10
    //   320: astore 9
    //   322: iload_1
    //   323: istore_2
    //   324: ldc_w 821
    //   327: aload 17
    //   329: invokeinterface 824 1 0
    //   334: invokevirtual 648	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   337: ifeq +707 -> 1044
    //   340: aload 10
    //   342: astore 11
    //   344: iload_1
    //   345: istore 4
    //   347: aload 10
    //   349: astore 12
    //   351: iload_1
    //   352: istore 5
    //   354: aload 10
    //   356: astore 9
    //   358: iload_1
    //   359: istore_2
    //   360: aload 17
    //   362: aconst_null
    //   363: ldc 98
    //   365: invokeinterface 575 3 0
    //   370: astore 13
    //   372: aload 10
    //   374: astore 11
    //   376: iload_1
    //   377: istore 4
    //   379: aload 10
    //   381: astore 12
    //   383: iload_1
    //   384: istore 5
    //   386: aload 10
    //   388: astore 9
    //   390: iload_1
    //   391: istore_2
    //   392: aload 17
    //   394: aconst_null
    //   395: ldc_w 826
    //   398: invokeinterface 575 3 0
    //   403: astore 14
    //   405: aload 14
    //   407: ifnonnull +667 -> 1074
    //   410: iconst_0
    //   411: istore 6
    //   413: iload 6
    //   415: iconst_3
    //   416: if_icmpge +28 -> 444
    //   419: aload 10
    //   421: astore 11
    //   423: iload_1
    //   424: istore 4
    //   426: aload 10
    //   428: astore 12
    //   430: iload_1
    //   431: istore 5
    //   433: aload 10
    //   435: astore 9
    //   437: iload_1
    //   438: istore_2
    //   439: aload_0
    //   440: iconst_1
    //   441: putfield 828	com/android/server/content/SyncStorageEngine:mGrantSyncAdaptersAccountAccess	Z
    //   444: aload 10
    //   446: astore 11
    //   448: iload_1
    //   449: istore 4
    //   451: aload 10
    //   453: astore 12
    //   455: iload_1
    //   456: istore 5
    //   458: aload 10
    //   460: astore 9
    //   462: iload_1
    //   463: istore_2
    //   464: aload 17
    //   466: aconst_null
    //   467: ldc 101
    //   469: invokeinterface 575 3 0
    //   474: astore 14
    //   476: aload 14
    //   478: ifnonnull +634 -> 1112
    //   481: iconst_0
    //   482: istore_3
    //   483: aload 10
    //   485: astore 11
    //   487: iload_1
    //   488: istore 4
    //   490: aload 10
    //   492: astore 12
    //   494: iload_1
    //   495: istore 5
    //   497: aload 10
    //   499: astore 9
    //   501: iload_1
    //   502: istore_2
    //   503: aload_0
    //   504: aload_0
    //   505: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   508: iload_3
    //   509: invokestatic 814	java/lang/Math:max	(II)I
    //   512: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   515: aload 10
    //   517: astore 11
    //   519: iload_1
    //   520: istore 4
    //   522: aload 10
    //   524: astore 12
    //   526: iload_1
    //   527: istore 5
    //   529: aload 10
    //   531: astore 9
    //   533: iload_1
    //   534: istore_2
    //   535: aload 17
    //   537: aconst_null
    //   538: ldc 104
    //   540: invokeinterface 575 3 0
    //   545: astore 14
    //   547: aload 14
    //   549: ifnonnull +592 -> 1141
    //   552: iconst_0
    //   553: istore_3
    //   554: aload 10
    //   556: astore 11
    //   558: iload_1
    //   559: istore 4
    //   561: aload 10
    //   563: astore 12
    //   565: iload_1
    //   566: istore 5
    //   568: aload 10
    //   570: astore 9
    //   572: iload_1
    //   573: istore_2
    //   574: aload_0
    //   575: iload_3
    //   576: putfield 830	com/android/server/content/SyncStorageEngine:mSyncRandomOffset	I
    //   579: aload 10
    //   581: astore 11
    //   583: iload_1
    //   584: istore 4
    //   586: aload 10
    //   588: astore 12
    //   590: iload_1
    //   591: istore 5
    //   593: aload 10
    //   595: astore 9
    //   597: iload_1
    //   598: istore_2
    //   599: aload_0
    //   600: getfield 830	com/android/server/content/SyncStorageEngine:mSyncRandomOffset	I
    //   603: ifne +43 -> 646
    //   606: aload 10
    //   608: astore 11
    //   610: iload_1
    //   611: istore 4
    //   613: aload 10
    //   615: astore 12
    //   617: iload_1
    //   618: istore 5
    //   620: aload 10
    //   622: astore 9
    //   624: iload_1
    //   625: istore_2
    //   626: aload_0
    //   627: new 832	java/util/Random
    //   630: dup
    //   631: invokestatic 426	java/lang/System:currentTimeMillis	()J
    //   634: invokespecial 834	java/util/Random:<init>	(J)V
    //   637: ldc_w 835
    //   640: invokevirtual 838	java/util/Random:nextInt	(I)I
    //   643: putfield 830	com/android/server/content/SyncStorageEngine:mSyncRandomOffset	I
    //   646: aload 10
    //   648: astore 11
    //   650: iload_1
    //   651: istore 4
    //   653: aload 10
    //   655: astore 12
    //   657: iload_1
    //   658: istore 5
    //   660: aload 10
    //   662: astore 9
    //   664: iload_1
    //   665: istore_2
    //   666: aload_0
    //   667: getfield 238	com/android/server/content/SyncStorageEngine:mMasterSyncAutomatically	Landroid/util/SparseArray;
    //   670: astore 14
    //   672: aload 13
    //   674: ifnull +572 -> 1246
    //   677: aload 10
    //   679: astore 11
    //   681: iload_1
    //   682: istore 4
    //   684: aload 10
    //   686: astore 12
    //   688: iload_1
    //   689: istore 5
    //   691: aload 10
    //   693: astore 9
    //   695: iload_1
    //   696: istore_2
    //   697: aload 13
    //   699: invokestatic 633	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
    //   702: istore 8
    //   704: aload 10
    //   706: astore 11
    //   708: iload_1
    //   709: istore 4
    //   711: aload 10
    //   713: astore 12
    //   715: iload_1
    //   716: istore 5
    //   718: aload 10
    //   720: astore 9
    //   722: iload_1
    //   723: istore_2
    //   724: aload 14
    //   726: iconst_0
    //   727: iload 8
    //   729: invokestatic 727	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   732: invokevirtual 365	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   735: aload 10
    //   737: astore 11
    //   739: iload_1
    //   740: istore 4
    //   742: aload 10
    //   744: astore 12
    //   746: iload_1
    //   747: istore 5
    //   749: aload 10
    //   751: astore 9
    //   753: iload_1
    //   754: istore_2
    //   755: aload 17
    //   757: invokeinterface 803 1 0
    //   762: istore 7
    //   764: aconst_null
    //   765: astore 15
    //   767: aconst_null
    //   768: astore 16
    //   770: iload_1
    //   771: istore_3
    //   772: iload 7
    //   774: istore_2
    //   775: aload 15
    //   777: astore 14
    //   779: iload_3
    //   780: istore_1
    //   781: aload 16
    //   783: astore 13
    //   785: iload_2
    //   786: iconst_2
    //   787: if_icmpne +205 -> 992
    //   790: aload 10
    //   792: astore 11
    //   794: iload_3
    //   795: istore 4
    //   797: aload 10
    //   799: astore 12
    //   801: iload_3
    //   802: istore 5
    //   804: aload 10
    //   806: astore 9
    //   808: iload_3
    //   809: istore_2
    //   810: aload 17
    //   812: invokeinterface 824 1 0
    //   817: astore 18
    //   819: aload 10
    //   821: astore 11
    //   823: iload_3
    //   824: istore 4
    //   826: aload 10
    //   828: astore 12
    //   830: iload_3
    //   831: istore 5
    //   833: aload 10
    //   835: astore 9
    //   837: iload_3
    //   838: istore_2
    //   839: aload 17
    //   841: invokeinterface 841 1 0
    //   846: iconst_2
    //   847: if_icmpne +625 -> 1472
    //   850: aload 10
    //   852: astore 11
    //   854: iload_3
    //   855: istore 4
    //   857: aload 10
    //   859: astore 12
    //   861: iload_3
    //   862: istore 5
    //   864: aload 10
    //   866: astore 9
    //   868: iload_3
    //   869: istore_2
    //   870: ldc_w 582
    //   873: aload 18
    //   875: invokevirtual 648	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   878: ifeq +486 -> 1364
    //   881: aload 10
    //   883: astore 11
    //   885: iload_3
    //   886: istore 4
    //   888: aload 10
    //   890: astore 12
    //   892: iload_3
    //   893: istore 5
    //   895: aload 10
    //   897: astore 9
    //   899: iload_3
    //   900: istore_2
    //   901: aload_0
    //   902: aload 17
    //   904: iload 6
    //   906: invokespecial 843	com/android/server/content/SyncStorageEngine:parseAuthority	(Lorg/xmlpull/v1/XmlPullParser;I)Lcom/android/server/content/SyncStorageEngine$AuthorityInfo;
    //   909: astore 15
    //   911: aconst_null
    //   912: astore 16
    //   914: aload 15
    //   916: ifnull +336 -> 1252
    //   919: aload 15
    //   921: astore 14
    //   923: iload_3
    //   924: istore_1
    //   925: aload 16
    //   927: astore 13
    //   929: aload 10
    //   931: astore 11
    //   933: iload_3
    //   934: istore 4
    //   936: aload 10
    //   938: astore 12
    //   940: iload_3
    //   941: istore 5
    //   943: aload 10
    //   945: astore 9
    //   947: iload_3
    //   948: istore_2
    //   949: aload 15
    //   951: getfield 370	com/android/server/content/SyncStorageEngine$AuthorityInfo:ident	I
    //   954: iload_3
    //   955: if_icmple +37 -> 992
    //   958: aload 10
    //   960: astore 11
    //   962: iload_3
    //   963: istore 4
    //   965: aload 10
    //   967: astore 12
    //   969: iload_3
    //   970: istore 5
    //   972: aload 10
    //   974: astore 9
    //   976: iload_3
    //   977: istore_2
    //   978: aload 15
    //   980: getfield 370	com/android/server/content/SyncStorageEngine$AuthorityInfo:ident	I
    //   983: istore_1
    //   984: aload 16
    //   986: astore 13
    //   988: aload 15
    //   990: astore 14
    //   992: aload 10
    //   994: astore 11
    //   996: iload_1
    //   997: istore 4
    //   999: aload 10
    //   1001: astore 12
    //   1003: iload_1
    //   1004: istore 5
    //   1006: aload 10
    //   1008: astore 9
    //   1010: iload_1
    //   1011: istore_2
    //   1012: aload 17
    //   1014: invokeinterface 803 1 0
    //   1019: istore_3
    //   1020: iload_3
    //   1021: istore 4
    //   1023: aload 14
    //   1025: astore 15
    //   1027: iload 4
    //   1029: istore_2
    //   1030: iload_1
    //   1031: istore_3
    //   1032: aload 13
    //   1034: astore 16
    //   1036: iload 4
    //   1038: iconst_1
    //   1039: if_icmpne -264 -> 775
    //   1042: iload_1
    //   1043: istore_3
    //   1044: aload_0
    //   1045: iload_3
    //   1046: iconst_1
    //   1047: iadd
    //   1048: aload_0
    //   1049: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1052: invokestatic 814	java/lang/Math:max	(II)I
    //   1055: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1058: aload 10
    //   1060: ifnull +8 -> 1068
    //   1063: aload 10
    //   1065: invokevirtual 819	java/io/FileInputStream:close	()V
    //   1068: aload_0
    //   1069: invokespecial 845	com/android/server/content/SyncStorageEngine:maybeMigrateSettingsForRenamedAuthorities	()Z
    //   1072: pop
    //   1073: return
    //   1074: aload 10
    //   1076: astore 11
    //   1078: iload_1
    //   1079: istore 4
    //   1081: aload 10
    //   1083: astore 12
    //   1085: iload_1
    //   1086: istore 5
    //   1088: aload 10
    //   1090: astore 9
    //   1092: iload_1
    //   1093: istore_2
    //   1094: aload 14
    //   1096: invokestatic 580	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1099: istore 6
    //   1101: goto -688 -> 413
    //   1104: astore 9
    //   1106: iconst_0
    //   1107: istore 6
    //   1109: goto -696 -> 413
    //   1112: aload 10
    //   1114: astore 11
    //   1116: iload_1
    //   1117: istore 4
    //   1119: aload 10
    //   1121: astore 12
    //   1123: iload_1
    //   1124: istore 5
    //   1126: aload 10
    //   1128: astore 9
    //   1130: iload_1
    //   1131: istore_2
    //   1132: aload 14
    //   1134: invokestatic 580	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1137: istore_3
    //   1138: goto -655 -> 483
    //   1141: aload 10
    //   1143: astore 11
    //   1145: iload_1
    //   1146: istore 4
    //   1148: aload 10
    //   1150: astore 12
    //   1152: iload_1
    //   1153: istore 5
    //   1155: aload 10
    //   1157: astore 9
    //   1159: iload_1
    //   1160: istore_2
    //   1161: aload 14
    //   1163: invokestatic 580	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1166: istore_3
    //   1167: goto -613 -> 554
    //   1170: astore 9
    //   1172: aload 10
    //   1174: astore 11
    //   1176: iload_1
    //   1177: istore 4
    //   1179: aload 10
    //   1181: astore 12
    //   1183: iload_1
    //   1184: istore 5
    //   1186: aload 10
    //   1188: astore 9
    //   1190: iload_1
    //   1191: istore_2
    //   1192: aload_0
    //   1193: iconst_0
    //   1194: putfield 830	com/android/server/content/SyncStorageEngine:mSyncRandomOffset	I
    //   1197: goto -618 -> 579
    //   1200: astore 10
    //   1202: aload 11
    //   1204: astore 9
    //   1206: iload 4
    //   1208: istore_2
    //   1209: ldc 83
    //   1211: ldc_w 847
    //   1214: aload 10
    //   1216: invokestatic 849	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1219: pop
    //   1220: aload_0
    //   1221: iload 4
    //   1223: iconst_1
    //   1224: iadd
    //   1225: aload_0
    //   1226: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1229: invokestatic 814	java/lang/Math:max	(II)I
    //   1232: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1235: aload 11
    //   1237: ifnull +8 -> 1245
    //   1240: aload 11
    //   1242: invokevirtual 819	java/io/FileInputStream:close	()V
    //   1245: return
    //   1246: iconst_1
    //   1247: istore 8
    //   1249: goto -545 -> 704
    //   1252: aload 10
    //   1254: astore 11
    //   1256: iload_3
    //   1257: istore 4
    //   1259: aload 10
    //   1261: astore 12
    //   1263: iload_3
    //   1264: istore 5
    //   1266: aload 10
    //   1268: astore 9
    //   1270: iload_3
    //   1271: istore_2
    //   1272: ldc_w 850
    //   1275: iconst_3
    //   1276: anewarray 852	java/lang/Object
    //   1279: dup
    //   1280: iconst_0
    //   1281: ldc_w 854
    //   1284: aastore
    //   1285: dup
    //   1286: iconst_1
    //   1287: iconst_m1
    //   1288: invokestatic 857	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1291: aastore
    //   1292: dup
    //   1293: iconst_2
    //   1294: ldc_w 859
    //   1297: aastore
    //   1298: invokestatic 865	android/util/EventLog:writeEvent	(I[Ljava/lang/Object;)I
    //   1301: pop
    //   1302: aload 15
    //   1304: astore 14
    //   1306: iload_3
    //   1307: istore_1
    //   1308: aload 16
    //   1310: astore 13
    //   1312: goto -320 -> 992
    //   1315: astore 10
    //   1317: aload 12
    //   1319: ifnonnull +425 -> 1744
    //   1322: aload 12
    //   1324: astore 9
    //   1326: iload 5
    //   1328: istore_2
    //   1329: ldc 83
    //   1331: ldc_w 805
    //   1334: invokestatic 808	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1337: pop
    //   1338: aload_0
    //   1339: iload 5
    //   1341: iconst_1
    //   1342: iadd
    //   1343: aload_0
    //   1344: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1347: invokestatic 814	java/lang/Math:max	(II)I
    //   1350: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1353: aload 12
    //   1355: ifnull +8 -> 1363
    //   1358: aload 12
    //   1360: invokevirtual 819	java/io/FileInputStream:close	()V
    //   1363: return
    //   1364: aload 15
    //   1366: astore 14
    //   1368: iload_3
    //   1369: istore_1
    //   1370: aload 16
    //   1372: astore 13
    //   1374: aload 10
    //   1376: astore 11
    //   1378: iload_3
    //   1379: istore 4
    //   1381: aload 10
    //   1383: astore 12
    //   1385: iload_3
    //   1386: istore 5
    //   1388: aload 10
    //   1390: astore 9
    //   1392: iload_3
    //   1393: istore_2
    //   1394: ldc 110
    //   1396: aload 18
    //   1398: invokevirtual 648	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1401: ifeq -409 -> 992
    //   1404: aload 10
    //   1406: astore 11
    //   1408: iload_3
    //   1409: istore 4
    //   1411: aload 10
    //   1413: astore 12
    //   1415: iload_3
    //   1416: istore 5
    //   1418: aload 10
    //   1420: astore 9
    //   1422: iload_3
    //   1423: istore_2
    //   1424: aload_0
    //   1425: aload 17
    //   1427: invokespecial 867	com/android/server/content/SyncStorageEngine:parseListenForTickles	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1430: aload 15
    //   1432: astore 14
    //   1434: iload_3
    //   1435: istore_1
    //   1436: aload 16
    //   1438: astore 13
    //   1440: goto -448 -> 992
    //   1443: astore 10
    //   1445: aload_0
    //   1446: iload_2
    //   1447: iconst_1
    //   1448: iadd
    //   1449: aload_0
    //   1450: getfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1453: invokestatic 814	java/lang/Math:max	(II)I
    //   1456: putfield 232	com/android/server/content/SyncStorageEngine:mNextAuthorityId	I
    //   1459: aload 9
    //   1461: ifnull +8 -> 1469
    //   1464: aload 9
    //   1466: invokevirtual 819	java/io/FileInputStream:close	()V
    //   1469: aload 10
    //   1471: athrow
    //   1472: aload 10
    //   1474: astore 11
    //   1476: iload_3
    //   1477: istore 4
    //   1479: aload 10
    //   1481: astore 12
    //   1483: iload_3
    //   1484: istore 5
    //   1486: aload 10
    //   1488: astore 9
    //   1490: iload_3
    //   1491: istore_2
    //   1492: aload 17
    //   1494: invokeinterface 841 1 0
    //   1499: iconst_3
    //   1500: if_icmpne +98 -> 1598
    //   1503: aload 15
    //   1505: astore 14
    //   1507: iload_3
    //   1508: istore_1
    //   1509: aload 16
    //   1511: astore 13
    //   1513: aload 10
    //   1515: astore 11
    //   1517: iload_3
    //   1518: istore 4
    //   1520: aload 10
    //   1522: astore 12
    //   1524: iload_3
    //   1525: istore 5
    //   1527: aload 10
    //   1529: astore 9
    //   1531: iload_3
    //   1532: istore_2
    //   1533: ldc_w 869
    //   1536: aload 18
    //   1538: invokevirtual 648	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1541: ifeq -549 -> 992
    //   1544: aload 15
    //   1546: astore 14
    //   1548: iload_3
    //   1549: istore_1
    //   1550: aload 16
    //   1552: astore 13
    //   1554: aload 15
    //   1556: ifnull -564 -> 992
    //   1559: aload 10
    //   1561: astore 11
    //   1563: iload_3
    //   1564: istore 4
    //   1566: aload 10
    //   1568: astore 12
    //   1570: iload_3
    //   1571: istore 5
    //   1573: aload 10
    //   1575: astore 9
    //   1577: iload_3
    //   1578: istore_2
    //   1579: aload_0
    //   1580: aload 17
    //   1582: aload 15
    //   1584: invokespecial 871	com/android/server/content/SyncStorageEngine:parsePeriodicSync	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/content/SyncStorageEngine$AuthorityInfo;)Landroid/content/PeriodicSync;
    //   1587: astore 13
    //   1589: aload 15
    //   1591: astore 14
    //   1593: iload_3
    //   1594: istore_1
    //   1595: goto -603 -> 992
    //   1598: aload 15
    //   1600: astore 14
    //   1602: iload_3
    //   1603: istore_1
    //   1604: aload 16
    //   1606: astore 13
    //   1608: aload 10
    //   1610: astore 11
    //   1612: iload_3
    //   1613: istore 4
    //   1615: aload 10
    //   1617: astore 12
    //   1619: iload_3
    //   1620: istore 5
    //   1622: aload 10
    //   1624: astore 9
    //   1626: iload_3
    //   1627: istore_2
    //   1628: aload 17
    //   1630: invokeinterface 841 1 0
    //   1635: iconst_4
    //   1636: if_icmpne -644 -> 992
    //   1639: aload 15
    //   1641: astore 14
    //   1643: iload_3
    //   1644: istore_1
    //   1645: aload 16
    //   1647: astore 13
    //   1649: aload 16
    //   1651: ifnull -659 -> 992
    //   1654: aload 15
    //   1656: astore 14
    //   1658: iload_3
    //   1659: istore_1
    //   1660: aload 16
    //   1662: astore 13
    //   1664: aload 10
    //   1666: astore 11
    //   1668: iload_3
    //   1669: istore 4
    //   1671: aload 10
    //   1673: astore 12
    //   1675: iload_3
    //   1676: istore 5
    //   1678: aload 10
    //   1680: astore 9
    //   1682: iload_3
    //   1683: istore_2
    //   1684: ldc_w 873
    //   1687: aload 18
    //   1689: invokevirtual 648	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1692: ifeq -700 -> 992
    //   1695: aload 10
    //   1697: astore 11
    //   1699: iload_3
    //   1700: istore 4
    //   1702: aload 10
    //   1704: astore 12
    //   1706: iload_3
    //   1707: istore 5
    //   1709: aload 10
    //   1711: astore 9
    //   1713: iload_3
    //   1714: istore_2
    //   1715: aload_0
    //   1716: aload 17
    //   1718: aload 16
    //   1720: getfield 877	android/content/PeriodicSync:extras	Landroid/os/Bundle;
    //   1723: invokespecial 879	com/android/server/content/SyncStorageEngine:parseExtra	(Lorg/xmlpull/v1/XmlPullParser;Landroid/os/Bundle;)V
    //   1726: aload 15
    //   1728: astore 14
    //   1730: iload_3
    //   1731: istore_1
    //   1732: aload 16
    //   1734: astore 13
    //   1736: goto -744 -> 992
    //   1739: astore 9
    //   1741: goto -673 -> 1068
    //   1744: aload 12
    //   1746: astore 9
    //   1748: iload 5
    //   1750: istore_2
    //   1751: ldc 83
    //   1753: ldc_w 847
    //   1756: aload 10
    //   1758: invokestatic 849	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1761: pop
    //   1762: goto -424 -> 1338
    //   1765: astore 9
    //   1767: return
    //   1768: astore 9
    //   1770: return
    //   1771: astore 9
    //   1773: goto -304 -> 1469
    //   1776: astore 9
    //   1778: goto -1263 -> 515
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1781	0	this	SyncStorageEngine
    //   1	1731	1	i	int
    //   18	1733	2	j	int
    //   196	1535	3	k	int
    //   12	1689	4	m	int
    //   15	1734	5	n	int
    //   411	697	6	i1	int
    //   762	11	7	i2	int
    //   702	546	8	bool	boolean
    //   6	258	9	localObject1	Object
    //   299	1	9	localIOException1	IOException
    //   320	771	9	localObject2	Object
    //   1104	1	9	localNumberFormatException1	NumberFormatException
    //   1128	30	9	localObject3	Object
    //   1170	1	9	localNumberFormatException2	NumberFormatException
    //   1188	524	9	localObject4	Object
    //   1739	1	9	localIOException2	IOException
    //   1746	1	9	localObject5	Object
    //   1765	1	9	localIOException3	IOException
    //   1768	1	9	localIOException4	IOException
    //   1771	1	9	localIOException5	IOException
    //   1776	1	9	localNumberFormatException3	NumberFormatException
    //   26	1161	10	localFileInputStream	java.io.FileInputStream
    //   1200	67	10	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
    //   1315	104	10	localIOException6	IOException
    //   1443	314	10	localThrowable	Throwable
    //   9	1689	11	localObject6	Object
    //   3	1742	12	localObject7	Object
    //   370	1365	13	localObject8	Object
    //   403	1326	14	localObject9	Object
    //   765	962	15	localObject10	Object
    //   768	965	16	localObject11	Object
    //   132	1585	17	localXmlPullParser	XmlPullParser
    //   817	871	18	str	String
    // Exception table:
    //   from	to	target	type
    //   293	298	299	java/io/IOException
    //   1094	1101	1104	java/lang/NumberFormatException
    //   574	579	1170	java/lang/NumberFormatException
    //   1161	1167	1170	java/lang/NumberFormatException
    //   19	28	1200	org/xmlpull/v1/XmlPullParserException
    //   48	57	1200	org/xmlpull/v1/XmlPullParserException
    //   77	109	1200	org/xmlpull/v1/XmlPullParserException
    //   129	134	1200	org/xmlpull/v1/XmlPullParserException
    //   154	169	1200	org/xmlpull/v1/XmlPullParserException
    //   189	197	1200	org/xmlpull/v1/XmlPullParserException
    //   229	237	1200	org/xmlpull/v1/XmlPullParserException
    //   267	276	1200	org/xmlpull/v1/XmlPullParserException
    //   324	340	1200	org/xmlpull/v1/XmlPullParserException
    //   360	372	1200	org/xmlpull/v1/XmlPullParserException
    //   392	405	1200	org/xmlpull/v1/XmlPullParserException
    //   439	444	1200	org/xmlpull/v1/XmlPullParserException
    //   464	476	1200	org/xmlpull/v1/XmlPullParserException
    //   503	515	1200	org/xmlpull/v1/XmlPullParserException
    //   535	547	1200	org/xmlpull/v1/XmlPullParserException
    //   574	579	1200	org/xmlpull/v1/XmlPullParserException
    //   599	606	1200	org/xmlpull/v1/XmlPullParserException
    //   626	646	1200	org/xmlpull/v1/XmlPullParserException
    //   666	672	1200	org/xmlpull/v1/XmlPullParserException
    //   697	704	1200	org/xmlpull/v1/XmlPullParserException
    //   724	735	1200	org/xmlpull/v1/XmlPullParserException
    //   755	764	1200	org/xmlpull/v1/XmlPullParserException
    //   810	819	1200	org/xmlpull/v1/XmlPullParserException
    //   839	850	1200	org/xmlpull/v1/XmlPullParserException
    //   870	881	1200	org/xmlpull/v1/XmlPullParserException
    //   901	911	1200	org/xmlpull/v1/XmlPullParserException
    //   949	958	1200	org/xmlpull/v1/XmlPullParserException
    //   978	984	1200	org/xmlpull/v1/XmlPullParserException
    //   1012	1020	1200	org/xmlpull/v1/XmlPullParserException
    //   1094	1101	1200	org/xmlpull/v1/XmlPullParserException
    //   1132	1138	1200	org/xmlpull/v1/XmlPullParserException
    //   1161	1167	1200	org/xmlpull/v1/XmlPullParserException
    //   1192	1197	1200	org/xmlpull/v1/XmlPullParserException
    //   1272	1302	1200	org/xmlpull/v1/XmlPullParserException
    //   1394	1404	1200	org/xmlpull/v1/XmlPullParserException
    //   1424	1430	1200	org/xmlpull/v1/XmlPullParserException
    //   1492	1503	1200	org/xmlpull/v1/XmlPullParserException
    //   1533	1544	1200	org/xmlpull/v1/XmlPullParserException
    //   1579	1589	1200	org/xmlpull/v1/XmlPullParserException
    //   1628	1639	1200	org/xmlpull/v1/XmlPullParserException
    //   1684	1695	1200	org/xmlpull/v1/XmlPullParserException
    //   1715	1726	1200	org/xmlpull/v1/XmlPullParserException
    //   19	28	1315	java/io/IOException
    //   48	57	1315	java/io/IOException
    //   77	109	1315	java/io/IOException
    //   129	134	1315	java/io/IOException
    //   154	169	1315	java/io/IOException
    //   189	197	1315	java/io/IOException
    //   229	237	1315	java/io/IOException
    //   267	276	1315	java/io/IOException
    //   324	340	1315	java/io/IOException
    //   360	372	1315	java/io/IOException
    //   392	405	1315	java/io/IOException
    //   439	444	1315	java/io/IOException
    //   464	476	1315	java/io/IOException
    //   503	515	1315	java/io/IOException
    //   535	547	1315	java/io/IOException
    //   574	579	1315	java/io/IOException
    //   599	606	1315	java/io/IOException
    //   626	646	1315	java/io/IOException
    //   666	672	1315	java/io/IOException
    //   697	704	1315	java/io/IOException
    //   724	735	1315	java/io/IOException
    //   755	764	1315	java/io/IOException
    //   810	819	1315	java/io/IOException
    //   839	850	1315	java/io/IOException
    //   870	881	1315	java/io/IOException
    //   901	911	1315	java/io/IOException
    //   949	958	1315	java/io/IOException
    //   978	984	1315	java/io/IOException
    //   1012	1020	1315	java/io/IOException
    //   1094	1101	1315	java/io/IOException
    //   1132	1138	1315	java/io/IOException
    //   1161	1167	1315	java/io/IOException
    //   1192	1197	1315	java/io/IOException
    //   1272	1302	1315	java/io/IOException
    //   1394	1404	1315	java/io/IOException
    //   1424	1430	1315	java/io/IOException
    //   1492	1503	1315	java/io/IOException
    //   1533	1544	1315	java/io/IOException
    //   1579	1589	1315	java/io/IOException
    //   1628	1639	1315	java/io/IOException
    //   1684	1695	1315	java/io/IOException
    //   1715	1726	1315	java/io/IOException
    //   19	28	1443	finally
    //   48	57	1443	finally
    //   77	109	1443	finally
    //   129	134	1443	finally
    //   154	169	1443	finally
    //   189	197	1443	finally
    //   229	237	1443	finally
    //   267	276	1443	finally
    //   324	340	1443	finally
    //   360	372	1443	finally
    //   392	405	1443	finally
    //   439	444	1443	finally
    //   464	476	1443	finally
    //   503	515	1443	finally
    //   535	547	1443	finally
    //   574	579	1443	finally
    //   599	606	1443	finally
    //   626	646	1443	finally
    //   666	672	1443	finally
    //   697	704	1443	finally
    //   724	735	1443	finally
    //   755	764	1443	finally
    //   810	819	1443	finally
    //   839	850	1443	finally
    //   870	881	1443	finally
    //   901	911	1443	finally
    //   949	958	1443	finally
    //   978	984	1443	finally
    //   1012	1020	1443	finally
    //   1094	1101	1443	finally
    //   1132	1138	1443	finally
    //   1161	1167	1443	finally
    //   1192	1197	1443	finally
    //   1209	1220	1443	finally
    //   1272	1302	1443	finally
    //   1329	1338	1443	finally
    //   1394	1404	1443	finally
    //   1424	1430	1443	finally
    //   1492	1503	1443	finally
    //   1533	1544	1443	finally
    //   1579	1589	1443	finally
    //   1628	1639	1443	finally
    //   1684	1695	1443	finally
    //   1715	1726	1443	finally
    //   1751	1762	1443	finally
    //   1063	1068	1739	java/io/IOException
    //   1358	1363	1765	java/io/IOException
    //   1240	1245	1768	java/io/IOException
    //   1464	1469	1771	java/io/IOException
    //   503	515	1776	java/lang/NumberFormatException
    //   1132	1138	1776	java/lang/NumberFormatException
  }
  
  private void readAndDeleteLegacyAccountInfoLocked()
  {
    Object localObject1 = this.mContext.getDatabasePath("syncmanager.db");
    if (!((File)localObject1).exists()) {
      return;
    }
    String str = ((File)localObject1).getPath();
    Object localObject3 = null;
    try
    {
      localObject1 = SQLiteDatabase.openDatabase(str, null, 1);
      localObject3 = localObject1;
    }
    catch (SQLiteException localSQLiteException)
    {
      int i;
      Object localObject4;
      Object localObject5;
      Object localObject6;
      int j;
      boolean bool;
      for (;;)
      {
        continue;
        i = 0;
        continue;
        localObject2 = null;
        continue;
        bool = false;
      }
      ((Cursor)localObject5).close();
      Object localObject2 = new SQLiteQueryBuilder();
      ((SQLiteQueryBuilder)localObject2).setTables("settings");
      localObject2 = ((SQLiteQueryBuilder)localObject2).query((SQLiteDatabase)localObject3, null, null, null, null, null, null);
      while (((Cursor)localObject2).moveToNext())
      {
        localObject5 = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndex("name"));
        localObject4 = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndex("value"));
        if (localObject5 != null)
        {
          if (((String)localObject5).equals("listen_for_tickles"))
          {
            if (localObject4 != null) {}
            for (bool = Boolean.parseBoolean((String)localObject4);; bool = true)
            {
              setMasterSyncAutomatically(bool, 0);
              break;
            }
          }
          if (((String)localObject5).startsWith("sync_provider_"))
          {
            localObject5 = ((String)localObject5).substring("sync_provider_".length(), ((String)localObject5).length());
            i = this.mAuthorities.size();
            while (i > 0)
            {
              j = i - 1;
              localObject6 = (AuthorityInfo)this.mAuthorities.valueAt(j);
              i = j;
              if (((AuthorityInfo)localObject6).target.provider.equals(localObject5)) {
                if (localObject4 == null) {
                  break label984;
                }
              }
            }
            for (bool = Boolean.parseBoolean((String)localObject4);; bool = true)
            {
              ((AuthorityInfo)localObject6).enabled = bool;
              ((AuthorityInfo)localObject6).syncable = 1;
              i = j;
              break label915;
              break;
            }
          }
        }
      }
      ((Cursor)localObject2).close();
      ((SQLiteDatabase)localObject3).close();
      new File(str).delete();
    }
    if (localObject3 != null) {
      if (((SQLiteDatabase)localObject3).getVersion() >= 11)
      {
        i = 1;
        if (Log.isLoggable("SyncManagerFile", 2)) {
          Slog.v("SyncManagerFile", "Reading legacy sync accounts db");
        }
        localObject1 = new SQLiteQueryBuilder();
        ((SQLiteQueryBuilder)localObject1).setTables("stats, status");
        localObject4 = new HashMap();
        ((HashMap)localObject4).put("_id", "status._id as _id");
        ((HashMap)localObject4).put("account", "stats.account as account");
        if (i != 0) {
          ((HashMap)localObject4).put("account_type", "stats.account_type as account_type");
        }
        ((HashMap)localObject4).put("authority", "stats.authority as authority");
        ((HashMap)localObject4).put("totalElapsedTime", "totalElapsedTime");
        ((HashMap)localObject4).put("numSyncs", "numSyncs");
        ((HashMap)localObject4).put("numSourceLocal", "numSourceLocal");
        ((HashMap)localObject4).put("numSourcePoll", "numSourcePoll");
        ((HashMap)localObject4).put("numSourceServer", "numSourceServer");
        ((HashMap)localObject4).put("numSourceUser", "numSourceUser");
        ((HashMap)localObject4).put("lastSuccessSource", "lastSuccessSource");
        ((HashMap)localObject4).put("lastSuccessTime", "lastSuccessTime");
        ((HashMap)localObject4).put("lastFailureSource", "lastFailureSource");
        ((HashMap)localObject4).put("lastFailureTime", "lastFailureTime");
        ((HashMap)localObject4).put("lastFailureMesg", "lastFailureMesg");
        ((HashMap)localObject4).put("pending", "pending");
        ((SQLiteQueryBuilder)localObject1).setProjectionMap((Map)localObject4);
        ((SQLiteQueryBuilder)localObject1).appendWhere("stats._id = status.stats_id");
        localObject5 = ((SQLiteQueryBuilder)localObject1).query((SQLiteDatabase)localObject3, null, null, null, null, null, null);
        for (;;)
        {
          if (!((Cursor)localObject5).moveToNext()) {
            break label747;
          }
          localObject6 = ((Cursor)localObject5).getString(((Cursor)localObject5).getColumnIndex("account"));
          if (i == 0) {
            break;
          }
          localObject1 = ((Cursor)localObject5).getString(((Cursor)localObject5).getColumnIndex("account_type"));
          localObject4 = localObject1;
          if (localObject1 == null) {
            localObject4 = "com.google";
          }
          localObject1 = ((Cursor)localObject5).getString(((Cursor)localObject5).getColumnIndex("authority"));
          localObject6 = getOrCreateAuthorityLocked(new EndPoint(new Account((String)localObject6, (String)localObject4), (String)localObject1, 0), -1, false);
          if (localObject6 != null)
          {
            j = this.mSyncStatus.size();
            int m = 0;
            localObject1 = null;
            do
            {
              k = m;
              if (j <= 0) {
                break;
              }
              j -= 1;
              localObject4 = (SyncStatusInfo)this.mSyncStatus.valueAt(j);
              localObject1 = localObject4;
            } while (((SyncStatusInfo)localObject4).authorityId != ((AuthorityInfo)localObject6).ident);
            int k = 1;
            localObject1 = localObject4;
            if (k == 0)
            {
              localObject1 = new SyncStatusInfo(((AuthorityInfo)localObject6).ident);
              this.mSyncStatus.put(((AuthorityInfo)localObject6).ident, localObject1);
            }
            ((SyncStatusInfo)localObject1).totalElapsedTime = getLongColumn((Cursor)localObject5, "totalElapsedTime");
            ((SyncStatusInfo)localObject1).numSyncs = getIntColumn((Cursor)localObject5, "numSyncs");
            ((SyncStatusInfo)localObject1).numSourceLocal = getIntColumn((Cursor)localObject5, "numSourceLocal");
            ((SyncStatusInfo)localObject1).numSourcePoll = getIntColumn((Cursor)localObject5, "numSourcePoll");
            ((SyncStatusInfo)localObject1).numSourceServer = getIntColumn((Cursor)localObject5, "numSourceServer");
            ((SyncStatusInfo)localObject1).numSourceUser = getIntColumn((Cursor)localObject5, "numSourceUser");
            ((SyncStatusInfo)localObject1).numSourcePeriodic = 0;
            ((SyncStatusInfo)localObject1).lastSuccessSource = getIntColumn((Cursor)localObject5, "lastSuccessSource");
            ((SyncStatusInfo)localObject1).lastSuccessTime = getLongColumn((Cursor)localObject5, "lastSuccessTime");
            ((SyncStatusInfo)localObject1).lastFailureSource = getIntColumn((Cursor)localObject5, "lastFailureSource");
            ((SyncStatusInfo)localObject1).lastFailureTime = getLongColumn((Cursor)localObject5, "lastFailureTime");
            ((SyncStatusInfo)localObject1).lastFailureMesg = ((Cursor)localObject5).getString(((Cursor)localObject5).getColumnIndex("lastFailureMesg"));
            if (getIntColumn((Cursor)localObject5, "pending") == 0) {
              break label741;
            }
            bool = true;
            ((SyncStatusInfo)localObject1).pending = bool;
          }
        }
      }
    }
    label741:
    label747:
    label915:
    label984:
    return;
  }
  
  private void readStatisticsLocked()
  {
    try
    {
      Object localObject = this.mStatisticsFile.readFully();
      Parcel localParcel = Parcel.obtain();
      localParcel.unmarshall((byte[])localObject, 0, localObject.length);
      localParcel.setDataPosition(0);
      int i = 0;
      int m;
      for (;;)
      {
        m = localParcel.readInt();
        if (m == 0) {
          break label185;
        }
        if ((m != 101) && (m != 100)) {
          break;
        }
        int k = localParcel.readInt();
        int j = k;
        if (m == 100) {
          j = k - 2009 + 14245;
        }
        localObject = new DayStats(j);
        ((DayStats)localObject).successCount = localParcel.readInt();
        ((DayStats)localObject).successTime = localParcel.readLong();
        ((DayStats)localObject).failureCount = localParcel.readInt();
        ((DayStats)localObject).failureTime = localParcel.readLong();
        if (i < this.mDayStats.length)
        {
          this.mDayStats[i] = localObject;
          i += 1;
        }
      }
      Slog.w("SyncManager", "Unknown stats token: " + m);
      label185:
      return;
    }
    catch (IOException localIOException)
    {
      Slog.i("SyncManager", "No initial statistics");
    }
  }
  
  private void readStatusLocked()
  {
    if (Log.isLoggable("SyncManagerFile", 2)) {
      Slog.v("SyncManagerFile", "Reading " + this.mStatusFile.getBaseFile());
    }
    int i;
    try
    {
      Object localObject = this.mStatusFile.readFully();
      Parcel localParcel = Parcel.obtain();
      localParcel.unmarshall((byte[])localObject, 0, localObject.length);
      localParcel.setDataPosition(0);
      for (;;)
      {
        i = localParcel.readInt();
        if (i == 0) {
          break;
        }
        if (i != 100) {
          break label173;
        }
        localObject = new SyncStatusInfo(localParcel);
        if (this.mAuthorities.indexOfKey(((SyncStatusInfo)localObject).authorityId) >= 0)
        {
          ((SyncStatusInfo)localObject).pending = false;
          if (Log.isLoggable("SyncManagerFile", 2)) {
            Slog.v("SyncManagerFile", "Adding status for id " + ((SyncStatusInfo)localObject).authorityId);
          }
          this.mSyncStatus.put(((SyncStatusInfo)localObject).authorityId, localObject);
        }
      }
      return;
    }
    catch (IOException localIOException)
    {
      Slog.i("SyncManager", "No initial status");
    }
    label173:
    Slog.w("SyncManager", "Unknown status token: " + i);
  }
  
  private void removeAuthorityLocked(Account paramAccount, int paramInt, String paramString, boolean paramBoolean)
  {
    paramAccount = (AccountInfo)this.mAccounts.get(new AccountAndUser(paramAccount, paramInt));
    if (paramAccount != null)
    {
      paramAccount = (AuthorityInfo)paramAccount.authorities.remove(paramString);
      if (paramAccount != null)
      {
        if (this.mAuthorityRemovedListener != null) {
          this.mAuthorityRemovedListener.onAuthorityRemoved(paramAccount.target);
        }
        this.mAuthorities.remove(paramAccount.ident);
        if (paramBoolean) {
          writeAccountInfoLocked();
        }
      }
    }
  }
  
  private void requestSync(Account paramAccount, int paramInt1, int paramInt2, String paramString, Bundle paramBundle)
  {
    if ((Process.myUid() == 1000) && (this.mSyncRequestListener != null))
    {
      this.mSyncRequestListener.onSyncRequest(new EndPoint(paramAccount, paramString, paramInt1), paramInt2, paramBundle);
      return;
    }
    ContentResolver.requestSync(paramAccount, paramString, paramBundle);
  }
  
  private void requestSync(AuthorityInfo paramAuthorityInfo, int paramInt, Bundle paramBundle)
  {
    if ((Process.myUid() == 1000) && (this.mSyncRequestListener != null))
    {
      this.mSyncRequestListener.onSyncRequest(paramAuthorityInfo.target, paramInt, paramBundle);
      return;
    }
    paramBundle = new SyncRequest.Builder().syncOnce().setExtras(paramBundle);
    paramBundle.setSyncAdapter(paramAuthorityInfo.target.account, paramAuthorityInfo.target.provider);
    ContentResolver.requestSync(paramBundle.build());
  }
  
  private boolean setBackoffLocked(Account paramAccount, int paramInt, String paramString, long paramLong1, long paramLong2)
  {
    boolean bool1 = false;
    Iterator localIterator = this.mAccounts.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (AccountInfo)localIterator.next();
      if ((paramAccount == null) || (paramAccount.equals(((AccountInfo)localObject).accountAndUser.account))) {}
      while (paramInt == ((AccountInfo)localObject).accountAndUser.userId)
      {
        localObject = ((AccountInfo)localObject).authorities.values().iterator();
        for (boolean bool2 = bool1;; bool2 = true)
        {
          AuthorityInfo localAuthorityInfo;
          do
          {
            bool1 = bool2;
            if (!((Iterator)localObject).hasNext()) {
              break;
            }
            localAuthorityInfo = (AuthorityInfo)((Iterator)localObject).next();
          } while (((paramString != null) && (!paramString.equals(localAuthorityInfo.target.provider))) || ((localAuthorityInfo.backoffTime == paramLong1) && (localAuthorityInfo.backoffDelay == paramLong2)));
          localAuthorityInfo.backoffTime = paramLong1;
          localAuthorityInfo.backoffDelay = paramLong2;
        }
      }
    }
    return bool1;
  }
  
  private void setSyncableStateForEndPoint(EndPoint paramEndPoint, int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      paramEndPoint = getOrCreateAuthorityLocked(paramEndPoint, -1, false);
      int i = paramInt;
      if (paramInt < -1) {
        i = -1;
      }
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.d("SyncManager", "setIsSyncable: " + paramEndPoint.toString() + " -> " + i);
      }
      if (paramEndPoint.syncable == i)
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.d("SyncManager", "setIsSyncable: already set to " + i + ", doing nothing");
        }
        return;
      }
      paramEndPoint.syncable = i;
      writeAccountInfoLocked();
      if (i == 1) {
        requestSync(paramEndPoint, -5, new Bundle());
      }
      reportChange(1);
      return;
    }
  }
  
  private void writeAccountInfoLocked()
  {
    if (Log.isLoggable("SyncManagerFile", 2)) {
      Slog.v("SyncManagerFile", "Writing new " + this.mAccountInfoFile.getBaseFile());
    }
    Object localObject1 = null;
    try
    {
      FileOutputStream localFileOutputStream = this.mAccountInfoFile.startWrite();
      localObject1 = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "accounts");
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "version", Integer.toString(3));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "nextAuthorityId", Integer.toString(this.mNextAuthorityId));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "offsetInSeconds", Integer.toString(this.mSyncRandomOffset));
      localObject1 = localFileOutputStream;
      int j = this.mMasterSyncAutomatically.size();
      int i = 0;
      Object localObject2;
      while (i < j)
      {
        localObject1 = localFileOutputStream;
        int k = this.mMasterSyncAutomatically.keyAt(i);
        localObject1 = localFileOutputStream;
        localObject2 = (Boolean)this.mMasterSyncAutomatically.valueAt(i);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "listenForTickles");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "user", Integer.toString(k));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "enabled", Boolean.toString(((Boolean)localObject2).booleanValue()));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "listenForTickles");
        i += 1;
      }
      localObject1 = localFileOutputStream;
      j = this.mAuthorities.size();
      i = 0;
      while (i < j)
      {
        localObject1 = localFileOutputStream;
        localObject2 = (AuthorityInfo)this.mAuthorities.valueAt(i);
        localObject1 = localFileOutputStream;
        EndPoint localEndPoint = ((AuthorityInfo)localObject2).target;
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "authority");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "id", Integer.toString(((AuthorityInfo)localObject2).ident));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "user", Integer.toString(localEndPoint.userId));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "enabled", Boolean.toString(((AuthorityInfo)localObject2).enabled));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "account", localEndPoint.account.name);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "type", localEndPoint.account.type);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "authority", localEndPoint.provider);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "syncable", Integer.toString(((AuthorityInfo)localObject2).syncable));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "authority");
        i += 1;
      }
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "accounts");
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject1 = localFileOutputStream;
      this.mAccountInfoFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      do
      {
        Slog.w("SyncManager", "Error writing accounts", localIOException);
      } while (localObject1 == null);
      this.mAccountInfoFile.failWrite((FileOutputStream)localObject1);
    }
  }
  
  private void writeStatisticsLocked()
  {
    if (Log.isLoggable("SyncManagerFile", 2)) {
      Slog.v("SyncManager", "Writing new " + this.mStatisticsFile.getBaseFile());
    }
    removeMessages(2);
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = this.mStatisticsFile.startWrite();
      localObject = localFileOutputStream;
      Parcel localParcel = Parcel.obtain();
      localObject = localFileOutputStream;
      int j = this.mDayStats.length;
      int i = 0;
      for (;;)
      {
        DayStats localDayStats;
        if (i < j)
        {
          localObject = localFileOutputStream;
          localDayStats = this.mDayStats[i];
          if (localDayStats != null) {}
        }
        else
        {
          localObject = localFileOutputStream;
          localParcel.writeInt(0);
          localObject = localFileOutputStream;
          localFileOutputStream.write(localParcel.marshall());
          localObject = localFileOutputStream;
          localParcel.recycle();
          localObject = localFileOutputStream;
          this.mStatisticsFile.finishWrite(localFileOutputStream);
          return;
        }
        localObject = localFileOutputStream;
        localParcel.writeInt(101);
        localObject = localFileOutputStream;
        localParcel.writeInt(localDayStats.day);
        localObject = localFileOutputStream;
        localParcel.writeInt(localDayStats.successCount);
        localObject = localFileOutputStream;
        localParcel.writeLong(localDayStats.successTime);
        localObject = localFileOutputStream;
        localParcel.writeInt(localDayStats.failureCount);
        localObject = localFileOutputStream;
        localParcel.writeLong(localDayStats.failureTime);
        i += 1;
      }
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w("SyncManager", "Error writing stats", localIOException);
      if (localObject != null) {
        this.mStatisticsFile.failWrite((FileOutputStream)localObject);
      }
    }
  }
  
  private void writeStatusLocked()
  {
    if (Log.isLoggable("SyncManagerFile", 2)) {
      Slog.v("SyncManagerFile", "Writing new " + this.mStatusFile.getBaseFile());
    }
    removeMessages(1);
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = this.mStatusFile.startWrite();
      localObject = localFileOutputStream;
      Parcel localParcel = Parcel.obtain();
      localObject = localFileOutputStream;
      int j = this.mSyncStatus.size();
      int i = 0;
      while (i < j)
      {
        localObject = localFileOutputStream;
        SyncStatusInfo localSyncStatusInfo = (SyncStatusInfo)this.mSyncStatus.valueAt(i);
        localObject = localFileOutputStream;
        localParcel.writeInt(100);
        localObject = localFileOutputStream;
        localSyncStatusInfo.writeToParcel(localParcel, 0);
        i += 1;
      }
      localObject = localFileOutputStream;
      localParcel.writeInt(0);
      localObject = localFileOutputStream;
      localFileOutputStream.write(localParcel.marshall());
      localObject = localFileOutputStream;
      localParcel.recycle();
      localObject = localFileOutputStream;
      this.mStatusFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      do
      {
        Slog.w("SyncManager", "Error writing status", localIOException);
      } while (localObject == null);
      this.mStatusFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  public SyncInfo addActiveSync(SyncManager.ActiveSyncContext paramActiveSyncContext)
  {
    synchronized (this.mAuthorities)
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "setActiveSync: account= auth=" + paramActiveSyncContext.mSyncOperation.target + " src=" + paramActiveSyncContext.mSyncOperation.syncSource + " extras=" + paramActiveSyncContext.mSyncOperation.extras);
      }
      AuthorityInfo localAuthorityInfo = getOrCreateAuthorityLocked(paramActiveSyncContext.mSyncOperation.target, -1, true);
      paramActiveSyncContext = new SyncInfo(localAuthorityInfo.ident, localAuthorityInfo.target.account, localAuthorityInfo.target.provider, paramActiveSyncContext.mStartTime);
      getCurrentSyncs(localAuthorityInfo.target.userId).add(paramActiveSyncContext);
      reportActiveChange();
      return paramActiveSyncContext;
    }
  }
  
  public void addStatusChangeListener(int paramInt, ISyncStatusObserver paramISyncStatusObserver)
  {
    synchronized (this.mAuthorities)
    {
      this.mChangeListeners.register(paramISyncStatusObserver, Integer.valueOf(paramInt));
      return;
    }
  }
  
  public void clearAllBackoffsLocked()
  {
    int i = 0;
    synchronized (this.mAuthorities)
    {
      Iterator localIterator1 = this.mAccounts.values().iterator();
      if (localIterator1.hasNext())
      {
        AccountInfo localAccountInfo = (AccountInfo)localIterator1.next();
        Iterator localIterator2 = localAccountInfo.authorities.values().iterator();
        for (int j = i;; j = 1)
        {
          AuthorityInfo localAuthorityInfo;
          do
          {
            i = j;
            if (!localIterator2.hasNext()) {
              break;
            }
            localAuthorityInfo = (AuthorityInfo)localIterator2.next();
          } while ((localAuthorityInfo.backoffTime == -1L) && (localAuthorityInfo.backoffDelay == -1L));
          if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "clearAllBackoffsLocked: authority:" + localAuthorityInfo.target + " account:" + localAccountInfo.accountAndUser.account.name + " user:" + localAccountInfo.accountAndUser.userId + " backoffTime was: " + localAuthorityInfo.backoffTime + " backoffDelay was: " + localAuthorityInfo.backoffDelay);
          }
          localAuthorityInfo.backoffTime = -1L;
          localAuthorityInfo.backoffDelay = -1L;
        }
      }
      if (i != 0) {
        reportChange(1);
      }
      return;
    }
  }
  
  public void clearAndReadState()
  {
    synchronized (this.mAuthorities)
    {
      this.mAuthorities.clear();
      this.mAccounts.clear();
      this.mServices.clear();
      this.mSyncStatus.clear();
      this.mSyncHistory.clear();
      readAccountInfoLocked();
      readStatusLocked();
      readStatisticsLocked();
      readAndDeleteLegacyAccountInfoLocked();
      writeAccountInfoLocked();
      writeStatusLocked();
      writeStatisticsLocked();
      return;
    }
  }
  
  public void doDatabaseCleanup(Account[] paramArrayOfAccount, int paramInt)
  {
    if (SystemProperties.getBoolean("ro.alarm_boot", false)) {
      return;
    }
    SparseArray localSparseArray2;
    for (;;)
    {
      Iterator localIterator;
      synchronized (this.mAuthorities)
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "Updating for new accounts...");
        }
        localSparseArray2 = new SparseArray();
        localIterator = this.mAccounts.values().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Object localObject = (AccountInfo)localIterator.next();
        if ((ArrayUtils.contains(paramArrayOfAccount, ((AccountInfo)localObject).accountAndUser.account)) || (((AccountInfo)localObject).accountAndUser.userId != paramInt)) {
          continue;
        }
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "Account removed: " + ((AccountInfo)localObject).accountAndUser);
        }
        localObject = ((AccountInfo)localObject).authorities.values().iterator();
        if (((Iterator)localObject).hasNext())
        {
          AuthorityInfo localAuthorityInfo = (AuthorityInfo)((Iterator)localObject).next();
          localSparseArray2.put(localAuthorityInfo.ident, localAuthorityInfo);
        }
      }
      localIterator.remove();
    }
    int i = localSparseArray2.size();
    if (i > 0)
    {
      if (i > 0)
      {
        int j = i - 1;
        int k = localSparseArray2.keyAt(j);
        paramArrayOfAccount = (AuthorityInfo)localSparseArray2.valueAt(j);
        if (this.mAuthorityRemovedListener != null) {
          this.mAuthorityRemovedListener.onAuthorityRemoved(paramArrayOfAccount.target);
        }
        this.mAuthorities.remove(k);
        paramInt = this.mSyncStatus.size();
        while (paramInt > 0)
        {
          i = paramInt - 1;
          paramInt = i;
          if (this.mSyncStatus.keyAt(i) == k)
          {
            this.mSyncStatus.remove(this.mSyncStatus.keyAt(i));
            paramInt = i;
          }
        }
        paramInt = this.mSyncHistory.size();
        for (;;)
        {
          i = j;
          if (paramInt <= 0) {
            break;
          }
          i = paramInt - 1;
          paramInt = i;
          if (((SyncHistoryItem)this.mSyncHistory.get(i)).authorityId == k)
          {
            this.mSyncHistory.remove(i);
            paramInt = i;
          }
        }
      }
      writeAccountInfoLocked();
      writeStatusLocked();
      writeStatisticsLocked();
    }
  }
  
  public AuthorityInfo getAuthority(int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      AuthorityInfo localAuthorityInfo = (AuthorityInfo)this.mAuthorities.get(paramInt);
      return localAuthorityInfo;
    }
  }
  
  public Pair<Long, Long> getBackoff(EndPoint paramEndPoint)
  {
    synchronized (this.mAuthorities)
    {
      paramEndPoint = getAuthorityLocked(paramEndPoint, "getBackoff");
      if (paramEndPoint != null)
      {
        paramEndPoint = Pair.create(Long.valueOf(paramEndPoint.backoffTime), Long.valueOf(paramEndPoint.backoffDelay));
        return paramEndPoint;
      }
      return null;
    }
  }
  
  public Pair<AuthorityInfo, SyncStatusInfo> getCopyOfAuthorityWithSyncStatus(EndPoint paramEndPoint)
  {
    synchronized (this.mAuthorities)
    {
      paramEndPoint = createCopyPairOfAuthorityWithSyncStatusLocked(getOrCreateAuthorityLocked(paramEndPoint, -1, true));
      return paramEndPoint;
    }
  }
  
  public List<SyncInfo> getCurrentSyncsCopy(int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList;
    for (;;)
    {
      synchronized (this.mAuthorities)
      {
        Object localObject = getCurrentSyncsLocked(paramInt);
        localArrayList = new ArrayList();
        Iterator localIterator = ((Iterable)localObject).iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        localObject = (SyncInfo)localIterator.next();
        if (!paramBoolean)
        {
          localObject = SyncInfo.createAccountRedacted(((SyncInfo)localObject).authorityId, ((SyncInfo)localObject).authority, ((SyncInfo)localObject).startTime);
          localArrayList.add(localObject);
        }
      }
      SyncInfo localSyncInfo2 = new SyncInfo(localSyncInfo1);
    }
    return localArrayList;
  }
  
  public DayStats[] getDayStatistics()
  {
    synchronized (this.mAuthorities)
    {
      DayStats[] arrayOfDayStats = new DayStats[this.mDayStats.length];
      System.arraycopy(this.mDayStats, 0, arrayOfDayStats, 0, arrayOfDayStats.length);
      return arrayOfDayStats;
    }
  }
  
  public long getDelayUntilTime(EndPoint paramEndPoint)
  {
    synchronized (this.mAuthorities)
    {
      paramEndPoint = getAuthorityLocked(paramEndPoint, "getDelayUntil");
      if (paramEndPoint == null) {
        return 0L;
      }
      long l = paramEndPoint.delayUntil;
      return l;
    }
  }
  
  public int getIsSyncable(Account paramAccount, int paramInt, String paramString)
  {
    localSparseArray = this.mAuthorities;
    if (paramAccount != null) {}
    try
    {
      paramAccount = getAuthorityLocked(new EndPoint(paramAccount, paramString, paramInt), "get authority syncable");
      if (paramAccount == null) {
        return -1;
      }
      paramInt = paramAccount.syncable;
      return paramInt;
    }
    finally {}
    paramInt = this.mAuthorities.size();
    do
    {
      int i;
      do
      {
        if (paramInt <= 0) {
          break;
        }
        i = paramInt - 1;
        paramAccount = (AuthorityInfo)this.mAuthorities.valueAt(i);
        paramInt = i;
      } while (paramAccount.target == null);
      paramInt = i;
    } while (!paramAccount.target.provider.equals(paramString));
    paramInt = paramAccount.syncable;
    return paramInt;
    return -1;
  }
  
  public boolean getMasterSyncAutomatically(int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      Boolean localBoolean = (Boolean)this.mMasterSyncAutomatically.get(paramInt);
      if (localBoolean == null)
      {
        bool = this.mDefaultMasterSyncAutomatically;
        return bool;
      }
      boolean bool = localBoolean.booleanValue();
    }
  }
  
  public SyncStatusInfo getStatusByAuthority(EndPoint paramEndPoint)
  {
    if ((paramEndPoint.account == null) || (paramEndPoint.provider == null)) {
      return null;
    }
    synchronized (this.mAuthorities)
    {
      int j = this.mSyncStatus.size();
      int i = 0;
      while (i < j)
      {
        SyncStatusInfo localSyncStatusInfo = (SyncStatusInfo)this.mSyncStatus.valueAt(i);
        AuthorityInfo localAuthorityInfo = (AuthorityInfo)this.mAuthorities.get(localSyncStatusInfo.authorityId);
        if (localAuthorityInfo != null)
        {
          boolean bool = localAuthorityInfo.target.matchesSpec(paramEndPoint);
          if (bool) {
            return localSyncStatusInfo;
          }
        }
        i += 1;
      }
      return null;
    }
  }
  
  public boolean getSyncAutomatically(Account paramAccount, int paramInt, String paramString)
  {
    boolean bool = false;
    localSparseArray = this.mAuthorities;
    if (paramAccount != null) {}
    try
    {
      paramAccount = getAuthorityLocked(new EndPoint(paramAccount, paramString, paramInt), "getSyncAutomatically");
      if (paramAccount != null) {
        bool = paramAccount.enabled;
      }
      return bool;
    }
    finally {}
    int i = this.mAuthorities.size();
    while (i > 0)
    {
      int j = i - 1;
      AuthorityInfo localAuthorityInfo = (AuthorityInfo)this.mAuthorities.valueAt(j);
      i = j;
      if (localAuthorityInfo.target.matchesSpec(new EndPoint(paramAccount, paramString, paramInt)))
      {
        bool = localAuthorityInfo.enabled;
        i = j;
        if (bool) {
          return true;
        }
      }
    }
    return false;
  }
  
  public ArrayList<SyncHistoryItem> getSyncHistory()
  {
    synchronized (this.mAuthorities)
    {
      int j = this.mSyncHistory.size();
      ArrayList localArrayList = new ArrayList(j);
      int i = 0;
      while (i < j)
      {
        localArrayList.add((SyncHistoryItem)this.mSyncHistory.get(i));
        i += 1;
      }
      return localArrayList;
    }
  }
  
  public int getSyncRandomOffset()
  {
    return this.mSyncRandomOffset;
  }
  
  public void handleMessage(Message arg1)
  {
    if (???.what == 1) {}
    for (;;)
    {
      synchronized (this.mAuthorities)
      {
        writeStatusLocked();
        return;
      }
      if (???.what != 2) {
        continue;
      }
      synchronized (this.mAuthorities)
      {
        writeStatisticsLocked();
      }
    }
  }
  
  public long insertStartSyncEvent(SyncOperation paramSyncOperation, long paramLong)
  {
    SyncHistoryItem localSyncHistoryItem;
    synchronized (this.mAuthorities)
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "insertStartSyncEvent: " + paramSyncOperation);
      }
      AuthorityInfo localAuthorityInfo = getAuthorityLocked(paramSyncOperation.target, "insertStartSyncEvent");
      if (localAuthorityInfo == null) {
        return -1L;
      }
      localSyncHistoryItem = new SyncHistoryItem();
      localSyncHistoryItem.initialization = paramSyncOperation.isInitialization();
      localSyncHistoryItem.authorityId = localAuthorityInfo.ident;
      int i = this.mNextHistoryId;
      this.mNextHistoryId = (i + 1);
      localSyncHistoryItem.historyId = i;
      if (this.mNextHistoryId < 0) {
        this.mNextHistoryId = 0;
      }
      localSyncHistoryItem.eventTime = paramLong;
      localSyncHistoryItem.source = paramSyncOperation.syncSource;
      localSyncHistoryItem.reason = paramSyncOperation.reason;
      localSyncHistoryItem.extras = paramSyncOperation.extras;
      localSyncHistoryItem.event = 0;
      this.mSyncHistory.add(0, localSyncHistoryItem);
      if (this.mSyncHistory.size() > 100) {
        this.mSyncHistory.remove(this.mSyncHistory.size() - 1);
      }
    }
    paramLong = localSyncHistoryItem.historyId;
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "returning historyId " + paramLong);
    }
    reportChange(8);
    return paramLong;
  }
  
  public boolean isSyncActive(EndPoint paramEndPoint)
  {
    synchronized (this.mAuthorities)
    {
      Iterator localIterator = getCurrentSyncs(paramEndPoint.userId).iterator();
      while (localIterator.hasNext())
      {
        AuthorityInfo localAuthorityInfo = getAuthority(((SyncInfo)localIterator.next()).authorityId);
        if (localAuthorityInfo != null)
        {
          boolean bool = localAuthorityInfo.target.matchesSpec(paramEndPoint);
          if (bool) {
            return true;
          }
        }
      }
      return false;
    }
  }
  
  public boolean isSyncPending(EndPoint paramEndPoint)
  {
    for (;;)
    {
      int i;
      synchronized (this.mAuthorities)
      {
        int j = this.mSyncStatus.size();
        i = 0;
        if (i < j)
        {
          SyncStatusInfo localSyncStatusInfo = (SyncStatusInfo)this.mSyncStatus.valueAt(i);
          AuthorityInfo localAuthorityInfo = (AuthorityInfo)this.mAuthorities.get(localSyncStatusInfo.authorityId);
          if ((localAuthorityInfo != null) && (localAuthorityInfo.target.matchesSpec(paramEndPoint)))
          {
            boolean bool = localSyncStatusInfo.pending;
            if (bool) {
              return true;
            }
          }
        }
        else
        {
          return false;
        }
      }
      i += 1;
    }
  }
  
  public void markPending(EndPoint paramEndPoint, boolean paramBoolean)
  {
    synchronized (this.mAuthorities)
    {
      paramEndPoint = getOrCreateAuthorityLocked(paramEndPoint, -1, true);
      if (paramEndPoint == null) {
        return;
      }
      getOrCreateSyncStatusLocked(paramEndPoint.ident).pending = paramBoolean;
      reportChange(2);
      return;
    }
  }
  
  public void queueBackup()
  {
    BackupManager.dataChanged("android");
  }
  
  public void removeActiveSync(SyncInfo paramSyncInfo, int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "removeActiveSync: account=" + paramSyncInfo.account + " user=" + paramInt + " auth=" + paramSyncInfo.authority);
      }
      getCurrentSyncs(paramInt).remove(paramSyncInfo);
      reportActiveChange();
      return;
    }
  }
  
  public void removeAuthority(EndPoint paramEndPoint)
  {
    synchronized (this.mAuthorities)
    {
      removeAuthorityLocked(paramEndPoint.account, paramEndPoint.userId, paramEndPoint.provider, true);
      return;
    }
  }
  
  public void removeStatusChangeListener(ISyncStatusObserver paramISyncStatusObserver)
  {
    synchronized (this.mAuthorities)
    {
      this.mChangeListeners.unregister(paramISyncStatusObserver);
      return;
    }
  }
  
  public void reportActiveChange()
  {
    reportChange(4);
  }
  
  void reportChange(int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      int i = this.mChangeListeners.beginBroadcast();
      ArrayList localArrayList = null;
      while (i > 0)
      {
        int j = i - 1;
        i = j;
        try
        {
          if ((((Integer)this.mChangeListeners.getBroadcastCookie(j)).intValue() & paramInt) == 0) {
            continue;
          }
          if (localArrayList != null) {
            break label198;
          }
          localArrayList = new ArrayList(j);
        }
        finally
        {
          for (;;) {}
        }
        localArrayList.add((ISyncStatusObserver)this.mChangeListeners.getBroadcastItem(j));
        i = j;
      }
      this.mChangeListeners.finishBroadcast();
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "reportChange " + paramInt + " to: " + localArrayList);
      }
      if (localArrayList != null)
      {
        i = localArrayList.size();
        for (;;)
        {
          if (i > 0)
          {
            i -= 1;
            try
            {
              ((ISyncStatusObserver)localArrayList.get(i)).onStatusChanged(paramInt);
            }
            catch (RemoteException localRemoteException) {}
          }
        }
      }
    }
  }
  
  boolean restoreAllPeriodicSyncs()
  {
    if (mPeriodicSyncAddedListener == null) {
      return false;
    }
    SparseArray localSparseArray = this.mAuthorities;
    int i = 0;
    for (;;)
    {
      try
      {
        if (i >= this.mAuthorities.size()) {
          break;
        }
        AuthorityInfo localAuthorityInfo = (AuthorityInfo)this.mAuthorities.valueAt(i);
        Iterator localIterator = localAuthorityInfo.periodicSyncs.iterator();
        if (localIterator.hasNext())
        {
          PeriodicSync localPeriodicSync = (PeriodicSync)localIterator.next();
          mPeriodicSyncAddedListener.onPeriodicSyncAdded(localAuthorityInfo.target, localPeriodicSync.extras, localPeriodicSync.period, localPeriodicSync.flexTime);
          continue;
        }
        ((AuthorityInfo)localObject).periodicSyncs.clear();
      }
      finally {}
      i += 1;
    }
    writeAccountInfoLocked();
    return true;
  }
  
  public void setBackoff(EndPoint paramEndPoint, long paramLong1, long paramLong2)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "setBackoff: " + paramEndPoint + " -> nextSyncTime " + paramLong1 + ", nextDelay " + paramLong2);
    }
    synchronized (this.mAuthorities)
    {
      if ((paramEndPoint.account == null) || (paramEndPoint.provider == null)) {}
      for (boolean bool = setBackoffLocked(paramEndPoint.account, paramEndPoint.userId, paramEndPoint.provider, paramLong1, paramLong2);; bool = false)
      {
        if (bool) {
          reportChange(1);
        }
        return;
        paramEndPoint = getOrCreateAuthorityLocked(paramEndPoint, -1, true);
        if ((paramEndPoint.backoffTime != paramLong1) || (paramEndPoint.backoffDelay != paramLong2)) {
          break;
        }
      }
      paramEndPoint.backoffTime = paramLong1;
      paramEndPoint.backoffDelay = paramLong2;
      bool = true;
    }
  }
  
  public void setDelayUntilTime(EndPoint paramEndPoint, long paramLong)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "setDelayUntil: " + paramEndPoint + " -> delayUntil " + paramLong);
    }
    synchronized (this.mAuthorities)
    {
      paramEndPoint = getOrCreateAuthorityLocked(paramEndPoint, -1, true);
      long l = paramEndPoint.delayUntil;
      if (l == paramLong) {
        return;
      }
      paramEndPoint.delayUntil = paramLong;
      reportChange(1);
      return;
    }
  }
  
  public void setIsSyncable(Account paramAccount, int paramInt1, String paramString, int paramInt2)
  {
    setSyncableStateForEndPoint(new EndPoint(paramAccount, paramString, paramInt1), paramInt2);
  }
  
  public void setMasterSyncAutomatically(boolean paramBoolean, int paramInt)
  {
    synchronized (this.mAuthorities)
    {
      Boolean localBoolean = (Boolean)this.mMasterSyncAutomatically.get(paramInt);
      if (localBoolean != null)
      {
        boolean bool = localBoolean.equals(Boolean.valueOf(paramBoolean));
        if (bool) {
          return;
        }
      }
      this.mMasterSyncAutomatically.put(paramInt, Boolean.valueOf(paramBoolean));
      writeAccountInfoLocked();
      if (paramBoolean) {
        requestSync(null, paramInt, -7, null, new Bundle());
      }
      reportChange(1);
      this.mContext.sendBroadcast(ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED);
      queueBackup();
      return;
    }
  }
  
  protected void setOnAuthorityRemovedListener(OnAuthorityRemovedListener paramOnAuthorityRemovedListener)
  {
    if (this.mAuthorityRemovedListener == null) {
      this.mAuthorityRemovedListener = paramOnAuthorityRemovedListener;
    }
  }
  
  protected void setOnSyncRequestListener(OnSyncRequestListener paramOnSyncRequestListener)
  {
    if (this.mSyncRequestListener == null) {
      this.mSyncRequestListener = paramOnSyncRequestListener;
    }
  }
  
  protected void setPeriodicSyncAddedListener(PeriodicSyncAddedListener paramPeriodicSyncAddedListener)
  {
    if (mPeriodicSyncAddedListener == null) {
      mPeriodicSyncAddedListener = paramPeriodicSyncAddedListener;
    }
  }
  
  public void setSyncAutomatically(Account paramAccount, int paramInt, String paramString, boolean paramBoolean)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.d("SyncManager", "setSyncAutomatically:  provider " + paramString + ", user " + paramInt + " -> " + paramBoolean);
    }
    synchronized (this.mAuthorities)
    {
      AuthorityInfo localAuthorityInfo = getOrCreateAuthorityLocked(new EndPoint(paramAccount, paramString, paramInt), -1, false);
      if (localAuthorityInfo.enabled == paramBoolean)
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.d("SyncManager", "setSyncAutomatically: already set to " + paramBoolean + ", doing nothing");
        }
        return;
      }
      if ((paramBoolean) && (localAuthorityInfo.syncable == 2)) {
        localAuthorityInfo.syncable = -1;
      }
      localAuthorityInfo.enabled = paramBoolean;
      writeAccountInfoLocked();
      if (paramBoolean) {
        requestSync(paramAccount, paramInt, -6, paramString, new Bundle());
      }
      reportChange(1);
      queueBackup();
      return;
    }
  }
  
  public boolean shouldGrantSyncAdaptersAccountAccess()
  {
    return this.mGrantSyncAdaptersAccountAccess;
  }
  
  public void stopSyncEvent(long paramLong1, long paramLong2, String paramString, long paramLong3, long paramLong4)
  {
    for (;;)
    {
      int i;
      SyncHistoryItem localSyncHistoryItem;
      SyncStatusInfo localSyncStatusInfo;
      int j;
      DayStats localDayStats;
      int m;
      int k;
      synchronized (this.mAuthorities)
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "stopSyncEvent: historyId=" + paramLong1);
        }
        i = this.mSyncHistory.size();
        localSyncHistoryItem = null;
        if (i > 0)
        {
          i -= 1;
          localSyncHistoryItem = (SyncHistoryItem)this.mSyncHistory.get(i);
          if (localSyncHistoryItem.historyId != paramLong1) {}
        }
        else
        {
          if (localSyncHistoryItem != null) {
            continue;
          }
          Slog.w("SyncManager", "stopSyncEvent: no history for id " + paramLong1);
          return;
        }
        continue;
        localSyncHistoryItem.elapsedTime = paramLong2;
        localSyncHistoryItem.event = 1;
        localSyncHistoryItem.mesg = paramString;
        localSyncHistoryItem.downstreamActivity = paramLong3;
        localSyncHistoryItem.upstreamActivity = paramLong4;
        localSyncStatusInfo = getOrCreateSyncStatusLocked(localSyncHistoryItem.authorityId);
        localSyncStatusInfo.numSyncs += 1;
        localSyncStatusInfo.totalElapsedTime += paramLong2;
        switch (localSyncHistoryItem.source)
        {
        case 1: 
          i = 0;
          j = getCurrentDayLocked();
          if (this.mDayStats[0] != null) {
            break label506;
          }
          this.mDayStats[0] = new DayStats(j);
          j = i;
          localDayStats = this.mDayStats[0];
          paramLong1 = localSyncHistoryItem.eventTime + paramLong2;
          m = 0;
          k = 0;
          i = 0;
          if (!"success".equals(paramString)) {
            break label581;
          }
          if ((localSyncStatusInfo.lastSuccessTime == 0L) || (localSyncStatusInfo.lastFailureTime != 0L)) {
            break label729;
          }
          localSyncStatusInfo.lastSuccessTime = paramLong1;
          localSyncStatusInfo.lastSuccessSource = localSyncHistoryItem.source;
          localSyncStatusInfo.lastFailureTime = 0L;
          localSyncStatusInfo.lastFailureSource = -1;
          localSyncStatusInfo.lastFailureMesg = null;
          localSyncStatusInfo.initialFailureTime = 0L;
          localDayStats.successCount += 1;
          localDayStats.successTime += paramLong2;
          if (i == 0) {
            break label678;
          }
          writeStatusLocked();
          if (j == 0) {
            break label702;
          }
          writeStatisticsLocked();
          reportChange(8);
          return;
          localSyncStatusInfo.numSourceLocal += 1;
        }
      }
      localSyncStatusInfo.numSourcePoll += 1;
      continue;
      localSyncStatusInfo.numSourceUser += 1;
      continue;
      localSyncStatusInfo.numSourceServer += 1;
      continue;
      localSyncStatusInfo.numSourcePeriodic += 1;
      continue;
      label506:
      if (j != this.mDayStats[0].day)
      {
        System.arraycopy(this.mDayStats, 0, this.mDayStats, 1, this.mDayStats.length - 1);
        this.mDayStats[0] = new DayStats(j);
        j = 1;
      }
      else
      {
        j = i;
        if (this.mDayStats[0] == null)
        {
          j = i;
          continue;
          label581:
          i = m;
          if (!"canceled".equals(paramString))
          {
            i = k;
            if (localSyncStatusInfo.lastFailureTime == 0L) {
              i = 1;
            }
            localSyncStatusInfo.lastFailureTime = paramLong1;
            localSyncStatusInfo.lastFailureSource = localSyncHistoryItem.source;
            localSyncStatusInfo.lastFailureMesg = paramString;
            if (localSyncStatusInfo.initialFailureTime == 0L) {
              localSyncStatusInfo.initialFailureTime = paramLong1;
            }
            localDayStats.failureCount += 1;
            localDayStats.failureTime += paramLong2;
            continue;
            label678:
            if (!hasMessages(1))
            {
              sendMessageDelayed(obtainMessage(1), 600000L);
              continue;
              label702:
              if (!hasMessages(2))
              {
                sendMessageDelayed(obtainMessage(2), 1800000L);
                continue;
                continue;
                label729:
                i = 1;
              }
            }
          }
        }
      }
    }
  }
  
  public void writeAllState()
  {
    synchronized (this.mAuthorities)
    {
      writeStatusLocked();
      writeStatisticsLocked();
      return;
    }
  }
  
  static class AccountInfo
  {
    final AccountAndUser accountAndUser;
    final HashMap<String, SyncStorageEngine.AuthorityInfo> authorities = new HashMap();
    
    AccountInfo(AccountAndUser paramAccountAndUser)
    {
      this.accountAndUser = paramAccountAndUser;
    }
  }
  
  public static class AuthorityInfo
  {
    public static final int NOT_INITIALIZED = -1;
    public static final int NOT_SYNCABLE = 0;
    public static final int SYNCABLE = 1;
    public static final int SYNCABLE_NOT_INITIALIZED = 2;
    public static final int SYNCABLE_NO_ACCOUNT_ACCESS = 3;
    public static final int UNDEFINED = -2;
    long backoffDelay;
    long backoffTime;
    long delayUntil;
    boolean enabled;
    final int ident;
    final ArrayList<PeriodicSync> periodicSyncs;
    int syncable;
    final SyncStorageEngine.EndPoint target;
    
    AuthorityInfo(AuthorityInfo paramAuthorityInfo)
    {
      this.target = paramAuthorityInfo.target;
      this.ident = paramAuthorityInfo.ident;
      this.enabled = paramAuthorityInfo.enabled;
      this.syncable = paramAuthorityInfo.syncable;
      this.backoffTime = paramAuthorityInfo.backoffTime;
      this.backoffDelay = paramAuthorityInfo.backoffDelay;
      this.delayUntil = paramAuthorityInfo.delayUntil;
      this.periodicSyncs = new ArrayList();
      paramAuthorityInfo = paramAuthorityInfo.periodicSyncs.iterator();
      while (paramAuthorityInfo.hasNext())
      {
        PeriodicSync localPeriodicSync = (PeriodicSync)paramAuthorityInfo.next();
        this.periodicSyncs.add(new PeriodicSync(localPeriodicSync));
      }
    }
    
    AuthorityInfo(SyncStorageEngine.EndPoint paramEndPoint, int paramInt)
    {
      this.target = paramEndPoint;
      this.ident = paramInt;
      this.enabled = false;
      this.periodicSyncs = new ArrayList();
      defaultInitialisation();
    }
    
    private void defaultInitialisation()
    {
      this.syncable = -1;
      this.backoffTime = -1L;
      this.backoffDelay = -1L;
      if (SyncStorageEngine.-get0() != null) {
        SyncStorageEngine.-get0().onPeriodicSyncAdded(this.target, new Bundle(), 86400L, SyncStorageEngine.calculateDefaultFlexTime(86400L));
      }
    }
    
    public String toString()
    {
      return this.target + ", enabled=" + this.enabled + ", syncable=" + this.syncable + ", backoff=" + this.backoffTime + ", delay=" + this.delayUntil;
    }
  }
  
  public static class DayStats
  {
    public final int day;
    public int failureCount;
    public long failureTime;
    public int successCount;
    public long successTime;
    
    public DayStats(int paramInt)
    {
      this.day = paramInt;
    }
  }
  
  public static class EndPoint
  {
    public static final EndPoint USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL = new EndPoint(null, null, -1);
    final Account account;
    final String provider;
    final int userId;
    
    public EndPoint(Account paramAccount, String paramString, int paramInt)
    {
      this.account = paramAccount;
      this.provider = paramString;
      this.userId = paramInt;
    }
    
    public boolean matchesSpec(EndPoint paramEndPoint)
    {
      if ((this.userId != paramEndPoint.userId) && (this.userId != -1) && (paramEndPoint.userId != -1)) {
        return false;
      }
      boolean bool1;
      if (paramEndPoint.account == null)
      {
        bool1 = true;
        if (paramEndPoint.provider != null) {
          break label68;
        }
      }
      label68:
      for (boolean bool2 = true;; bool2 = this.provider.equals(paramEndPoint.provider))
      {
        if (!bool1) {
          break label83;
        }
        return bool2;
        bool1 = this.account.equals(paramEndPoint.account);
        break;
      }
      label83:
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder();
      StringBuilder localStringBuilder2;
      if (this.account == null)
      {
        str = "ALL ACCS";
        localStringBuilder2 = localStringBuilder1.append(str).append("/");
        if (this.provider != null) {
          break label75;
        }
      }
      label75:
      for (String str = "ALL PDRS";; str = this.provider)
      {
        localStringBuilder2.append(str);
        localStringBuilder1.append(":u").append(this.userId);
        return localStringBuilder1.toString();
        str = this.account.name;
        break;
      }
    }
  }
  
  static abstract interface OnAuthorityRemovedListener
  {
    public abstract void onAuthorityRemoved(SyncStorageEngine.EndPoint paramEndPoint);
  }
  
  static abstract interface OnSyncRequestListener
  {
    public abstract void onSyncRequest(SyncStorageEngine.EndPoint paramEndPoint, int paramInt, Bundle paramBundle);
  }
  
  static abstract interface PeriodicSyncAddedListener
  {
    public abstract void onPeriodicSyncAdded(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle, long paramLong1, long paramLong2);
  }
  
  public static class SyncHistoryItem
  {
    int authorityId;
    long downstreamActivity;
    long elapsedTime;
    int event;
    long eventTime;
    Bundle extras;
    int historyId;
    boolean initialization;
    String mesg;
    int reason;
    int source;
    long upstreamActivity;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/content/SyncStorageEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */