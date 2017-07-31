package com.android.server.content;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentService.Stub;
import android.content.ISyncStatusObserver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncRequest;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageManagerInternal.SyncAdapterPackagesProvider;
import android.content.pm.ProviderInfo;
import android.database.IContentObserver;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.OnePlusProcessManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class ContentService
  extends IContentService.Stub
{
  static final boolean DEBUG = false;
  static final String TAG = "ContentService";
  @GuardedBy("mCache")
  private final SparseArray<ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>>> mCache = new SparseArray();
  private BroadcastReceiver mCacheReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      synchronized (ContentService.-get0(ContentService.this))
      {
        if ("android.intent.action.LOCALE_CHANGED".equals(paramAnonymousIntent.getAction())) {
          ContentService.-get0(ContentService.this).clear();
        }
        Uri localUri;
        do
        {
          return;
          localUri = paramAnonymousIntent.getData();
        } while (localUri == null);
        int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
        paramAnonymousIntent = localUri.getSchemeSpecificPart();
        ContentService.-wrap0(ContentService.this, i, paramAnonymousIntent, null);
      }
    }
  };
  private Context mContext;
  private boolean mFactoryTest;
  private final ObserverNode mRootNode = new ObserverNode("");
  private SyncManager mSyncManager = null;
  private final Object mSyncManagerLock = new Object();
  
  ContentService(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mFactoryTest = paramBoolean;
    ((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class)).setSyncAdapterPackagesprovider(new PackageManagerInternal.SyncAdapterPackagesProvider()
    {
      public String[] getPackages(String paramAnonymousString, int paramAnonymousInt)
      {
        return ContentService.this.getSyncAdapterPackagesForAuthorityAsUser(paramAnonymousString, paramAnonymousInt);
      }
    });
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.PACKAGE_ADDED");
    paramContext.addAction("android.intent.action.PACKAGE_CHANGED");
    paramContext.addAction("android.intent.action.PACKAGE_REMOVED");
    paramContext.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
    paramContext.addDataScheme("package");
    this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, paramContext, null, null);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.LOCALE_CHANGED");
    this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, paramContext, null, null);
  }
  
  private int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().checkUriPermission(paramUri, paramInt1, paramInt2, paramInt3, paramInt4, null);
      return paramInt1;
    }
    catch (RemoteException paramUri) {}
    return -1;
  }
  
  private long clampPeriod(long paramLong)
  {
    long l2 = JobInfo.getMinPeriodMillis() / 1000L;
    long l1 = paramLong;
    if (paramLong < l2)
    {
      Slog.w("ContentService", "Requested poll frequency of " + paramLong + " seconds being rounded up to " + l2 + "s.");
      l1 = l2;
    }
    return l1;
  }
  
  private void enforceCrossUserPermission(int paramInt, String paramString)
  {
    if (UserHandle.getCallingUserId() != paramInt) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", paramString);
    }
  }
  
  private ArrayMap<Pair<String, Uri>, Bundle> findOrCreateCacheLocked(int paramInt, String paramString)
  {
    Object localObject2 = (ArrayMap)this.mCache.get(paramInt);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new ArrayMap();
      this.mCache.put(paramInt, localObject1);
    }
    ArrayMap localArrayMap = (ArrayMap)((ArrayMap)localObject1).get(paramString);
    localObject2 = localArrayMap;
    if (localArrayMap == null)
    {
      localObject2 = new ArrayMap();
      ((ArrayMap)localObject1).put(paramString, localObject2);
    }
    return (ArrayMap<Pair<String, Uri>, Bundle>)localObject2;
  }
  
  private String getProviderPackageName(Uri paramUri)
  {
    Object localObject = null;
    ProviderInfo localProviderInfo = this.mContext.getPackageManager().resolveContentProvider(paramUri.getAuthority(), 0);
    paramUri = (Uri)localObject;
    if (localProviderInfo != null) {
      paramUri = localProviderInfo.packageName;
    }
    return paramUri;
  }
  
  private SyncManager getSyncManager()
  {
    if (SystemProperties.getBoolean("config.disable_network", false)) {
      return null;
    }
    synchronized (this.mSyncManagerLock)
    {
      try
      {
        if (this.mSyncManager == null) {
          this.mSyncManager = new SyncManager(this.mContext, this.mFactoryTest);
        }
        SyncManager localSyncManager = this.mSyncManager;
        return localSyncManager;
      }
      catch (SQLiteException localSQLiteException)
      {
        for (;;)
        {
          Log.e("ContentService", "Can't create SyncManager", localSQLiteException);
        }
      }
    }
  }
  
  private void handleContentNotifyResumeProcess(ArrayList<ObserverCall> paramArrayList)
  {
    if (!OnePlusProcessManager.isSupportFrozenApp()) {
      return;
    }
    try
    {
      Object localObject1 = new HashSet();
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((HashSet)localObject1).add(Integer.valueOf(((ObserverCall)paramArrayList.get(i)).mUid));
        i += 1;
      }
      localObject1 = ((HashSet)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        OnePlusProcessManager.resumeProcessByUID_out(((Integer)((Iterator)localObject1).next()).intValue(), "notifyChange");
      }
    }
    finally {}
  }
  
  private int handleIncomingUser(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt4;
    if (paramInt4 == -2) {
      i = ActivityManager.getCurrentUser();
    }
    if (i == -1) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "ContentService");
    }
    do
    {
      return i;
      if (i < 0) {
        throw new IllegalArgumentException("Invalid user: " + i);
      }
    } while ((i == UserHandle.getCallingUserId()) || (checkUriPermission(paramUri, paramInt1, paramInt2, paramInt3, i) == 0));
    this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "ContentService");
    return i;
  }
  
  private void invalidateCacheLocked(int paramInt, String paramString, Uri paramUri)
  {
    Object localObject = (ArrayMap)this.mCache.get(paramInt);
    if (localObject == null) {
      return;
    }
    paramString = (ArrayMap)((ArrayMap)localObject).get(paramString);
    if (paramString == null) {
      return;
    }
    if (paramUri != null)
    {
      paramInt = 0;
      while (paramInt < paramString.size())
      {
        localObject = (Pair)paramString.keyAt(paramInt);
        if ((((Pair)localObject).second != null) && (((Uri)((Pair)localObject).second).toString().startsWith(paramUri.toString()))) {
          paramString.removeAt(paramInt);
        } else {
          paramInt += 1;
        }
      }
    }
    paramString.clear();
  }
  
  private static int normalizeSyncable(int paramInt)
  {
    if (paramInt > 0) {
      return 1;
    }
    if (paramInt == 0) {
      return 0;
    }
    return -2;
  }
  
  public void addPeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong)
  {
    Bundle.setDefusable(paramBundle, true);
    if (paramAccount == null) {
      throw new IllegalArgumentException("Account must not be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must not be empty.");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
    int i = UserHandle.getCallingUserId();
    paramLong = clampPeriod(paramLong);
    long l1 = SyncStorageEngine.calculateDefaultFlexTime(paramLong);
    long l2 = clearCallingIdentity();
    try
    {
      paramAccount = new SyncStorageEngine.EndPoint(paramAccount, paramString, i);
      getSyncManager().updateOrAddPeriodicSync(paramAccount, paramLong, l1, paramBundle);
      return;
    }
    finally
    {
      restoreCallingIdentity(l2);
    }
  }
  
  public void addStatusChangeListener(int paramInt, ISyncStatusObserver paramISyncStatusObserver)
  {
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if ((localSyncManager != null) && (paramISyncStatusObserver != null)) {
        localSyncManager.getSyncStorageEngine().addStatusChangeListener(paramInt, paramISyncStatusObserver);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void cancelRequest(SyncRequest paramSyncRequest)
  {
    SyncManager localSyncManager = getSyncManager();
    if (localSyncManager == null) {
      return;
    }
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      Bundle localBundle = new Bundle(paramSyncRequest.getBundle());
      SyncStorageEngine.EndPoint localEndPoint = new SyncStorageEngine.EndPoint(paramSyncRequest.getAccount(), paramSyncRequest.getProvider(), i);
      if (paramSyncRequest.isPeriodic())
      {
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        getSyncManager().removePeriodicSync(localEndPoint, localBundle);
      }
      localSyncManager.cancelScheduledSyncOperation(localEndPoint, localBundle);
      localSyncManager.cancelActiveSync(localEndPoint, localBundle);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void cancelSync(Account paramAccount, String paramString, ComponentName paramComponentName)
  {
    cancelSyncAsUser(paramAccount, paramString, paramComponentName, UserHandle.getCallingUserId());
  }
  
  public void cancelSyncAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
  {
    if ((paramString != null) && (paramString.length() == 0)) {
      throw new IllegalArgumentException("Authority must be non-empty");
    }
    enforceCrossUserPermission(paramInt, "no permission to modify the sync settings for user " + paramInt);
    long l = clearCallingIdentity();
    if (paramComponentName != null)
    {
      Slog.e("ContentService", "cname not null.");
      return;
    }
    try
    {
      paramComponentName = getSyncManager();
      if (paramComponentName != null)
      {
        paramAccount = new SyncStorageEngine.EndPoint(paramAccount, paramString, paramInt);
        paramComponentName.clearScheduledSyncOperations(paramAccount);
        paramComponentName.cancelActiveSync(paramAccount, null);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 85	com/android/server/content/ContentService:mContext	Landroid/content/Context;
    //   6: ldc_w 469
    //   9: ldc_w 471
    //   12: invokevirtual 196	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   15: new 473	com/android/internal/util/IndentingPrintWriter
    //   18: dup
    //   19: aload_2
    //   20: ldc_w 475
    //   23: invokespecial 478	com/android/internal/util/IndentingPrintWriter:<init>	(Ljava/io/Writer;Ljava/lang/String;)V
    //   26: astore_2
    //   27: invokestatic 388	com/android/server/content/ContentService:clearCallingIdentity	()J
    //   30: lstore 6
    //   32: aload_0
    //   33: getfield 70	com/android/server/content/ContentService:mSyncManager	Lcom/android/server/content/SyncManager;
    //   36: ifnonnull +112 -> 148
    //   39: aload_2
    //   40: ldc_w 480
    //   43: invokevirtual 483	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   46: aload_2
    //   47: invokevirtual 485	com/android/internal/util/IndentingPrintWriter:println	()V
    //   50: aload_2
    //   51: ldc_w 487
    //   54: invokevirtual 483	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   57: aload_0
    //   58: getfield 68	com/android/server/content/ContentService:mRootNode	Lcom/android/server/content/ContentService$ObserverNode;
    //   61: astore 8
    //   63: aload 8
    //   65: monitorenter
    //   66: iconst_2
    //   67: newarray <illegal type>
    //   69: astore 9
    //   71: new 489	android/util/SparseIntArray
    //   74: dup
    //   75: invokespecial 490	android/util/SparseIntArray:<init>	()V
    //   78: astore 10
    //   80: aload_0
    //   81: getfield 68	com/android/server/content/ContentService:mRootNode	Lcom/android/server/content/ContentService$ObserverNode;
    //   84: aload_1
    //   85: aload_2
    //   86: aload_3
    //   87: ldc 63
    //   89: ldc_w 475
    //   92: aload 9
    //   94: aload 10
    //   96: invokevirtual 494	com/android/server/content/ContentService$ObserverNode:dumpLocked	(Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[ILandroid/util/SparseIntArray;)V
    //   99: aload_2
    //   100: invokevirtual 485	com/android/internal/util/IndentingPrintWriter:println	()V
    //   103: new 276	java/util/ArrayList
    //   106: dup
    //   107: invokespecial 495	java/util/ArrayList:<init>	()V
    //   110: astore_1
    //   111: iconst_0
    //   112: istore 4
    //   114: iload 4
    //   116: aload 10
    //   118: invokevirtual 496	android/util/SparseIntArray:size	()I
    //   121: if_icmpge +52 -> 173
    //   124: aload_1
    //   125: aload 10
    //   127: iload 4
    //   129: invokevirtual 498	android/util/SparseIntArray:keyAt	(I)I
    //   132: invokestatic 290	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   135: invokevirtual 499	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   138: pop
    //   139: iload 4
    //   141: iconst_1
    //   142: iadd
    //   143: istore 4
    //   145: goto -31 -> 114
    //   148: aload_0
    //   149: getfield 70	com/android/server/content/ContentService:mSyncManager	Lcom/android/server/content/SyncManager;
    //   152: aload_1
    //   153: aload_2
    //   154: invokevirtual 502	com/android/server/content/SyncManager:dump	(Ljava/io/FileDescriptor;Ljava/io/PrintWriter;)V
    //   157: goto -111 -> 46
    //   160: astore_1
    //   161: lload 6
    //   163: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   166: aload_1
    //   167: athrow
    //   168: astore_1
    //   169: aload_0
    //   170: monitorexit
    //   171: aload_1
    //   172: athrow
    //   173: aload_1
    //   174: new 8	com/android/server/content/ContentService$2
    //   177: dup
    //   178: aload_0
    //   179: aload 10
    //   181: invokespecial 505	com/android/server/content/ContentService$2:<init>	(Lcom/android/server/content/ContentService;Landroid/util/SparseIntArray;)V
    //   184: invokestatic 511	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   187: iconst_0
    //   188: istore 4
    //   190: iload 4
    //   192: aload_1
    //   193: invokevirtual 279	java/util/ArrayList:size	()I
    //   196: if_icmpge +64 -> 260
    //   199: aload_1
    //   200: iload 4
    //   202: invokevirtual 280	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   205: checkcast 286	java/lang/Integer
    //   208: invokevirtual 310	java/lang/Integer:intValue	()I
    //   211: istore 5
    //   213: aload_2
    //   214: ldc_w 513
    //   217: invokevirtual 516	com/android/internal/util/IndentingPrintWriter:print	(Ljava/lang/String;)V
    //   220: aload_2
    //   221: iload 5
    //   223: invokevirtual 519	com/android/internal/util/IndentingPrintWriter:print	(I)V
    //   226: aload_2
    //   227: ldc_w 521
    //   230: invokevirtual 516	com/android/internal/util/IndentingPrintWriter:print	(Ljava/lang/String;)V
    //   233: aload_2
    //   234: aload 10
    //   236: iload 5
    //   238: invokevirtual 523	android/util/SparseIntArray:get	(I)I
    //   241: invokevirtual 519	com/android/internal/util/IndentingPrintWriter:print	(I)V
    //   244: aload_2
    //   245: ldc_w 525
    //   248: invokevirtual 483	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   251: iload 4
    //   253: iconst_1
    //   254: iadd
    //   255: istore 4
    //   257: goto -67 -> 190
    //   260: aload_2
    //   261: invokevirtual 485	com/android/internal/util/IndentingPrintWriter:println	()V
    //   264: aload_2
    //   265: ldc_w 527
    //   268: invokevirtual 516	com/android/internal/util/IndentingPrintWriter:print	(Ljava/lang/String;)V
    //   271: aload_2
    //   272: aload 9
    //   274: iconst_0
    //   275: iaload
    //   276: invokevirtual 529	com/android/internal/util/IndentingPrintWriter:println	(I)V
    //   279: aload_2
    //   280: ldc_w 531
    //   283: invokevirtual 516	com/android/internal/util/IndentingPrintWriter:print	(Ljava/lang/String;)V
    //   286: aload_2
    //   287: aload 9
    //   289: iconst_1
    //   290: iaload
    //   291: invokevirtual 529	com/android/internal/util/IndentingPrintWriter:println	(I)V
    //   294: aload 8
    //   296: monitorexit
    //   297: aload_0
    //   298: getfield 49	com/android/server/content/ContentService:mCache	Landroid/util/SparseArray;
    //   301: astore_1
    //   302: aload_1
    //   303: monitorenter
    //   304: aload_2
    //   305: invokevirtual 485	com/android/internal/util/IndentingPrintWriter:println	()V
    //   308: aload_2
    //   309: ldc_w 533
    //   312: invokevirtual 483	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   315: aload_2
    //   316: invokevirtual 536	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   319: iconst_0
    //   320: istore 4
    //   322: iload 4
    //   324: aload_0
    //   325: getfield 49	com/android/server/content/ContentService:mCache	Landroid/util/SparseArray;
    //   328: invokevirtual 537	android/util/SparseArray:size	()I
    //   331: if_icmpge +77 -> 408
    //   334: aload_2
    //   335: new 160	java/lang/StringBuilder
    //   338: dup
    //   339: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   342: ldc_w 539
    //   345: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   348: aload_0
    //   349: getfield 49	com/android/server/content/ContentService:mCache	Landroid/util/SparseArray;
    //   352: iload 4
    //   354: invokevirtual 540	android/util/SparseArray:keyAt	(I)I
    //   357: invokevirtual 329	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   360: ldc_w 542
    //   363: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   369: invokevirtual 483	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   372: aload_2
    //   373: invokevirtual 536	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   376: aload_2
    //   377: aload_0
    //   378: getfield 49	com/android/server/content/ContentService:mCache	Landroid/util/SparseArray;
    //   381: iload 4
    //   383: invokevirtual 545	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   386: invokevirtual 548	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/Object;)V
    //   389: aload_2
    //   390: invokevirtual 551	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   393: iload 4
    //   395: iconst_1
    //   396: iadd
    //   397: istore 4
    //   399: goto -77 -> 322
    //   402: astore_1
    //   403: aload 8
    //   405: monitorexit
    //   406: aload_1
    //   407: athrow
    //   408: aload_2
    //   409: invokevirtual 551	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   412: aload_1
    //   413: monitorexit
    //   414: lload 6
    //   416: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   419: aload_0
    //   420: monitorexit
    //   421: return
    //   422: astore_2
    //   423: aload_1
    //   424: monitorexit
    //   425: aload_2
    //   426: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	427	0	this	ContentService
    //   0	427	2	paramPrintWriter	PrintWriter
    //   0	427	3	paramArrayOfString	String[]
    //   112	286	4	i	int
    //   211	26	5	j	int
    //   30	385	6	l	long
    //   69	219	9	arrayOfInt	int[]
    //   78	157	10	localSparseIntArray	SparseIntArray
    // Exception table:
    //   from	to	target	type
    //   32	46	160	finally
    //   46	66	160	finally
    //   148	157	160	finally
    //   294	304	160	finally
    //   403	408	160	finally
    //   412	414	160	finally
    //   423	427	160	finally
    //   2	32	168	finally
    //   161	168	168	finally
    //   414	419	168	finally
    //   66	111	402	finally
    //   114	139	402	finally
    //   173	187	402	finally
    //   190	251	402	finally
    //   260	294	402	finally
    //   304	319	422	finally
    //   322	393	422	finally
    //   408	412	422	finally
  }
  
  public Bundle getCache(String arg1, Uri paramUri, int paramInt)
  {
    enforceCrossUserPermission(paramInt, "ContentService");
    this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", "ContentService");
    ((AppOpsManager)this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), ???);
    String str = getProviderPackageName(paramUri);
    paramUri = Pair.create(???, paramUri);
    synchronized (this.mCache)
    {
      paramUri = (Bundle)findOrCreateCacheLocked(paramInt, str).get(paramUri);
      return paramUri;
    }
  }
  
  public List<SyncInfo> getCurrentSyncs()
  {
    return getCurrentSyncsAsUser(UserHandle.getCallingUserId());
  }
  
  /* Error */
  public List<SyncInfo> getCurrentSyncsAsUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: new 160	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 585
    //   12: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: iload_1
    //   16: invokevirtual 329	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   19: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   22: invokespecial 457	com/android/server/content/ContentService:enforceCrossUserPermission	(ILjava/lang/String;)V
    //   25: aload_0
    //   26: getfield 85	com/android/server/content/ContentService:mContext	Landroid/content/Context;
    //   29: ldc_w 587
    //   32: ldc_w 589
    //   35: invokevirtual 196	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   38: aload_0
    //   39: getfield 85	com/android/server/content/ContentService:mContext	Landroid/content/Context;
    //   42: ldc_w 591
    //   45: invokevirtual 595	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   48: ifne +30 -> 78
    //   51: iconst_1
    //   52: istore_2
    //   53: invokestatic 388	com/android/server/content/ContentService:clearCallingIdentity	()J
    //   56: lstore_3
    //   57: aload_0
    //   58: invokespecial 395	com/android/server/content/ContentService:getSyncManager	()Lcom/android/server/content/SyncManager;
    //   61: invokevirtual 409	com/android/server/content/SyncManager:getSyncStorageEngine	()Lcom/android/server/content/SyncStorageEngine;
    //   64: iload_1
    //   65: iload_2
    //   66: invokevirtual 599	com/android/server/content/SyncStorageEngine:getCurrentSyncsCopy	(IZ)Ljava/util/List;
    //   69: astore 5
    //   71: lload_3
    //   72: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   75: aload 5
    //   77: areturn
    //   78: iconst_0
    //   79: istore_2
    //   80: goto -27 -> 53
    //   83: astore 5
    //   85: lload_3
    //   86: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   89: aload 5
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	ContentService
    //   0	92	1	paramInt	int
    //   52	28	2	bool	boolean
    //   56	30	3	l	long
    //   69	7	5	localList	List
    //   83	7	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   57	71	83	finally
  }
  
  public int getIsSyncable(Account paramAccount, String paramString)
  {
    return getIsSyncableAsUser(paramAccount, paramString, UserHandle.getCallingUserId());
  }
  
  public int getIsSyncableAsUser(Account paramAccount, String paramString, int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to read the sync settings for user " + paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null)
      {
        paramInt = localSyncManager.computeSyncable(paramAccount, paramInt, paramString, false);
        return paramInt;
      }
      return -1;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public boolean getMasterSyncAutomatically()
  {
    return getMasterSyncAutomaticallyAsUser(UserHandle.getCallingUserId());
  }
  
  public boolean getMasterSyncAutomaticallyAsUser(int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to read the sync settings for user " + paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null)
      {
        boolean bool = localSyncManager.getSyncStorageEngine().getMasterSyncAutomatically(paramInt);
        return bool;
      }
      return false;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public List<PeriodicSync> getPeriodicSyncs(Account paramAccount, String paramString, ComponentName paramComponentName)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("Account must not be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must not be empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      paramAccount = getSyncManager().getPeriodicSyncs(new SyncStorageEngine.EndPoint(paramAccount, paramString, i));
      return paramAccount;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public String[] getSyncAdapterPackagesForAuthorityAsUser(String paramString, int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to read sync settings for user " + paramInt);
    long l = clearCallingIdentity();
    try
    {
      paramString = getSyncManager().getSyncAdapterPackagesForAuthorityAsUser(paramString, paramInt);
      return paramString;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public SyncAdapterType[] getSyncAdapterTypes()
  {
    return getSyncAdapterTypesAsUser(UserHandle.getCallingUserId());
  }
  
  public SyncAdapterType[] getSyncAdapterTypesAsUser(int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to read sync settings for user " + paramInt);
    long l = clearCallingIdentity();
    try
    {
      SyncAdapterType[] arrayOfSyncAdapterType = getSyncManager().getSyncAdapterTypes(paramInt);
      return arrayOfSyncAdapterType;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public boolean getSyncAutomatically(Account paramAccount, String paramString)
  {
    return getSyncAutomaticallyAsUser(paramAccount, paramString, UserHandle.getCallingUserId());
  }
  
  public boolean getSyncAutomaticallyAsUser(Account paramAccount, String paramString, int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to read the sync settings for user " + paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null)
      {
        boolean bool = localSyncManager.getSyncStorageEngine().getSyncAutomatically(paramAccount, paramInt, paramString);
        return bool;
      }
      return false;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public SyncStatusInfo getSyncStatus(Account paramAccount, String paramString, ComponentName paramComponentName)
  {
    return getSyncStatusAsUser(paramAccount, paramString, paramComponentName, UserHandle.getCallingUserId());
  }
  
  public SyncStatusInfo getSyncStatusAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must not be empty");
    }
    enforceCrossUserPermission(paramInt, "no permission to read the sync stats for user " + paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
    long l = clearCallingIdentity();
    try
    {
      paramComponentName = getSyncManager();
      if (paramComponentName == null) {
        return null;
      }
      if ((paramAccount != null) && (paramString != null))
      {
        paramAccount = new SyncStorageEngine.EndPoint(paramAccount, paramString, paramInt);
        paramAccount = paramComponentName.getSyncStorageEngine().getStatusByAuthority(paramAccount);
        return paramAccount;
      }
      throw new IllegalArgumentException("Must call sync status with valid authority");
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public boolean isSyncActive(Account paramAccount, String paramString, ComponentName paramComponentName)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      paramComponentName = getSyncManager();
      if (paramComponentName == null) {
        return false;
      }
      boolean bool = paramComponentName.getSyncStorageEngine().isSyncActive(new SyncStorageEngine.EndPoint(paramAccount, paramString, i));
      return bool;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public boolean isSyncPending(Account paramAccount, String paramString, ComponentName paramComponentName)
  {
    return isSyncPendingAsUser(paramAccount, paramString, paramComponentName, UserHandle.getCallingUserId());
  }
  
  public boolean isSyncPendingAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
    enforceCrossUserPermission(paramInt, "no permission to retrieve the sync settings for user " + paramInt);
    l = clearCallingIdentity();
    paramComponentName = getSyncManager();
    if (paramComponentName == null) {
      return false;
    }
    if ((paramAccount != null) && (paramString != null)) {}
    try
    {
      paramAccount = new SyncStorageEngine.EndPoint(paramAccount, paramString, paramInt);
      boolean bool = paramComponentName.getSyncStorageEngine().isSyncPending(paramAccount);
      return bool;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
    throw new IllegalArgumentException("Invalid authority specified");
  }
  
  public void notifyChange(Uri paramUri, IContentObserver arg2, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramUri == null) {
      throw new NullPointerException("Uri must not be null");
    }
    int n = Binder.getCallingUid();
    int i = Binder.getCallingPid();
    int i1 = UserHandle.getCallingUserId();
    int i2 = handleIncomingUser(paramUri, i, n, 2, paramInt2);
    Object localObject1 = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).checkContentProviderAccess(paramUri.getAuthority(), i2);
    if (localObject1 != null)
    {
      Log.w("ContentService", "Ignoring notify for " + paramUri + " from " + n + ": " + (String)localObject1);
      return;
    }
    long l = clearCallingIdentity();
    try
    {
      localObject1 = new ArrayList();
      int i3;
      IBinder localIBinder;
      int j;
      int m;
      int k;
      if ((paramInt1 & 0x1) == 0) {
        break label396;
      }
    }
    finally
    {
      synchronized (this.mRootNode)
      {
        this.mRootNode.collectObserversLocked(paramUri, 0, ???, paramBoolean, paramInt1, i2, (ArrayList)localObject1);
        handleContentNotifyResumeProcess((ArrayList)localObject1);
        i3 = ((ArrayList)localObject1).size();
        i = 0;
        if (i >= i3) {
          break label367;
        }
        ??? = (ObserverCall)((ArrayList)localObject1).get(i);
      }
      restoreCallingIdentity(l);
    }
    label367:
    ??? = getSyncManager();
    if (??? != null) {
      ???.scheduleLocalSync(null, i1, n, paramUri.getAuthority());
    }
    label396:
    synchronized (this.mCache)
    {
      invalidateCacheLocked(i2, getProviderPackageName(paramUri), paramUri);
      restoreCallingIdentity(l);
      return;
    }
  }
  
  public void notifyChange(Uri paramUri, IContentObserver paramIContentObserver, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {}
    for (int i = 1;; i = 0)
    {
      notifyChange(paramUri, paramIContentObserver, paramBoolean1, i, UserHandle.getCallingUserId());
      return;
    }
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    try
    {
      boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      return bool;
    }
    catch (RuntimeException paramParcel1)
    {
      if (!(paramParcel1 instanceof SecurityException)) {
        Slog.wtf("ContentService", "Content Service Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  public void putCache(String arg1, Uri paramUri, Bundle paramBundle, int paramInt)
  {
    Bundle.setDefusable(paramBundle, true);
    enforceCrossUserPermission(paramInt, "ContentService");
    this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", "ContentService");
    ((AppOpsManager)this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), ???);
    Object localObject = getProviderPackageName(paramUri);
    paramUri = Pair.create(???, paramUri);
    synchronized (this.mCache)
    {
      localObject = findOrCreateCacheLocked(paramInt, (String)localObject);
      if (paramBundle != null)
      {
        ((ArrayMap)localObject).put(paramUri, paramBundle);
        return;
      }
      ((ArrayMap)localObject).remove(paramUri);
    }
  }
  
  public void registerContentObserver(Uri paramUri, boolean paramBoolean, IContentObserver paramIContentObserver)
  {
    registerContentObserver(paramUri, paramBoolean, paramIContentObserver, UserHandle.getCallingUserId());
  }
  
  public void registerContentObserver(Uri paramUri, boolean paramBoolean, IContentObserver paramIContentObserver, int paramInt)
  {
    if ((paramIContentObserver == null) || (paramUri == null)) {
      throw new IllegalArgumentException("You must pass a valid uri and observer");
    }
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    paramInt = handleIncomingUser(paramUri, j, i, 1, paramInt);
    ??? = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).checkContentProviderAccess(paramUri.getAuthority(), paramInt);
    if (??? != null)
    {
      Log.w("ContentService", "Ignoring content changes for " + paramUri + " from " + i + ": " + (String)???);
      return;
    }
    synchronized (this.mRootNode)
    {
      this.mRootNode.addObserverLocked(paramUri, paramIContentObserver, paramBoolean, this.mRootNode, i, j, paramInt);
      return;
    }
  }
  
  public void removePeriodicSync(Account paramAccount, String paramString, Bundle paramBundle)
  {
    Bundle.setDefusable(paramBundle, true);
    if (paramAccount == null) {
      throw new IllegalArgumentException("Account must not be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must not be empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      getSyncManager().removePeriodicSync(new SyncStorageEngine.EndPoint(paramAccount, paramString, i), paramBundle);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void removeStatusChangeListener(ISyncStatusObserver paramISyncStatusObserver)
  {
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if ((localSyncManager != null) && (paramISyncStatusObserver != null)) {
        localSyncManager.getSyncStorageEngine().removeStatusChangeListener(paramISyncStatusObserver);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void requestSync(Account paramAccount, String paramString, Bundle paramBundle)
  {
    Bundle.setDefusable(paramBundle, true);
    ContentResolver.validateSyncExtrasBundle(paramBundle);
    int i = UserHandle.getCallingUserId();
    int j = Binder.getCallingUid();
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null) {
        localSyncManager.scheduleSync(paramAccount, i, j, paramString, paramBundle, -2);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void setIsSyncable(Account paramAccount, String paramString, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must not be empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
    paramInt = normalizeSyncable(paramInt);
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null) {
        localSyncManager.getSyncStorageEngine().setIsSyncable(paramAccount, i, paramString, paramInt);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void setMasterSyncAutomatically(boolean paramBoolean)
  {
    setMasterSyncAutomaticallyAsUser(paramBoolean, UserHandle.getCallingUserId());
  }
  
  public void setMasterSyncAutomaticallyAsUser(boolean paramBoolean, int paramInt)
  {
    enforceCrossUserPermission(paramInt, "no permission to set the sync status for user " + paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null) {
        localSyncManager.getSyncStorageEngine().setMasterSyncAutomatically(paramBoolean, paramInt);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void setSyncAutomatically(Account paramAccount, String paramString, boolean paramBoolean)
  {
    setSyncAutomaticallyAsUser(paramAccount, paramString, paramBoolean, UserHandle.getCallingUserId());
  }
  
  public void setSyncAutomaticallyAsUser(Account paramAccount, String paramString, boolean paramBoolean, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Authority must be non-empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
    enforceCrossUserPermission(paramInt, "no permission to modify the sync settings for user " + paramInt);
    long l = clearCallingIdentity();
    try
    {
      SyncManager localSyncManager = getSyncManager();
      if (localSyncManager != null) {
        localSyncManager.getSyncStorageEngine().setSyncAutomatically(paramAccount, paramInt, paramString, paramBoolean);
      }
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void sync(SyncRequest paramSyncRequest)
  {
    syncAsUser(paramSyncRequest, UserHandle.getCallingUserId());
  }
  
  /* Error */
  public void syncAsUser(SyncRequest paramSyncRequest, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_2
    //   2: new 160	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 829
    //   12: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: iload_2
    //   16: invokevirtual 329	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   19: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   22: invokespecial 457	com/android/server/content/ContentService:enforceCrossUserPermission	(ILjava/lang/String;)V
    //   25: invokestatic 565	android/os/Binder:getCallingUid	()I
    //   28: istore_3
    //   29: invokestatic 388	com/android/server/content/ContentService:clearCallingIdentity	()J
    //   32: lstore 4
    //   34: aload_0
    //   35: invokespecial 395	com/android/server/content/ContentService:getSyncManager	()Lcom/android/server/content/SyncManager;
    //   38: astore 11
    //   40: aload 11
    //   42: ifnonnull +9 -> 51
    //   45: lload 4
    //   47: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   50: return
    //   51: aload_1
    //   52: invokevirtual 419	android/content/SyncRequest:getBundle	()Landroid/os/Bundle;
    //   55: astore 10
    //   57: aload_1
    //   58: invokevirtual 832	android/content/SyncRequest:getSyncFlexTime	()J
    //   61: lstore 6
    //   63: aload_1
    //   64: invokevirtual 835	android/content/SyncRequest:getSyncRunTime	()J
    //   67: lstore 8
    //   69: aload_1
    //   70: invokevirtual 432	android/content/SyncRequest:isPeriodic	()Z
    //   73: ifeq +61 -> 134
    //   76: aload_0
    //   77: getfield 85	com/android/server/content/ContentService:mContext	Landroid/content/Context;
    //   80: ldc_w 376
    //   83: ldc_w 378
    //   86: invokevirtual 196	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   89: new 390	com/android/server/content/SyncStorageEngine$EndPoint
    //   92: dup
    //   93: aload_1
    //   94: invokevirtual 426	android/content/SyncRequest:getAccount	()Landroid/accounts/Account;
    //   97: aload_1
    //   98: invokevirtual 429	android/content/SyncRequest:getProvider	()Ljava/lang/String;
    //   101: iload_2
    //   102: invokespecial 393	com/android/server/content/SyncStorageEngine$EndPoint:<init>	(Landroid/accounts/Account;Ljava/lang/String;I)V
    //   105: astore_1
    //   106: aload_0
    //   107: lload 8
    //   109: invokespecial 380	com/android/server/content/ContentService:clampPeriod	(J)J
    //   112: lstore 8
    //   114: aload_0
    //   115: invokespecial 395	com/android/server/content/ContentService:getSyncManager	()Lcom/android/server/content/SyncManager;
    //   118: aload_1
    //   119: lload 8
    //   121: lload 6
    //   123: aload 10
    //   125: invokevirtual 399	com/android/server/content/SyncManager:updateOrAddPeriodicSync	(Lcom/android/server/content/SyncStorageEngine$EndPoint;JJLandroid/os/Bundle;)V
    //   128: lload 4
    //   130: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   133: return
    //   134: aload 11
    //   136: aload_1
    //   137: invokevirtual 426	android/content/SyncRequest:getAccount	()Landroid/accounts/Account;
    //   140: iload_2
    //   141: iload_3
    //   142: aload_1
    //   143: invokevirtual 429	android/content/SyncRequest:getProvider	()Ljava/lang/String;
    //   146: aload 10
    //   148: bipush -2
    //   150: invokevirtual 797	com/android/server/content/SyncManager:scheduleSync	(Landroid/accounts/Account;IILjava/lang/String;Landroid/os/Bundle;I)V
    //   153: goto -25 -> 128
    //   156: astore_1
    //   157: lload 4
    //   159: invokestatic 403	com/android/server/content/ContentService:restoreCallingIdentity	(J)V
    //   162: aload_1
    //   163: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	ContentService
    //   0	164	1	paramSyncRequest	SyncRequest
    //   0	164	2	paramInt	int
    //   28	114	3	i	int
    //   32	126	4	l1	long
    //   61	61	6	l2	long
    //   67	53	8	l3	long
    //   55	92	10	localBundle	Bundle
    //   38	97	11	localSyncManager	SyncManager
    // Exception table:
    //   from	to	target	type
    //   34	40	156	finally
    //   51	128	156	finally
    //   134	153	156	finally
  }
  
  void systemReady()
  {
    getSyncManager();
  }
  
  public void unregisterContentObserver(IContentObserver paramIContentObserver)
  {
    if (paramIContentObserver == null) {
      throw new IllegalArgumentException("You must pass a valid observer");
    }
    synchronized (this.mRootNode)
    {
      this.mRootNode.removeObserverLocked(paramIContentObserver);
      return;
    }
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private ContentService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mService.systemReady();
      }
    }
    
    public void onCleanupUser(int paramInt)
    {
      synchronized (ContentService.-get0(this.mService))
      {
        ContentService.-get0(this.mService).remove(paramInt);
        return;
      }
    }
    
    public void onStart()
    {
      if (FactoryTest.getMode() == 1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mService = new ContentService(getContext(), bool);
        publishBinderService("content", this.mService);
        return;
      }
    }
  }
  
  public static final class ObserverCall
  {
    final ContentService.ObserverNode mNode;
    final IContentObserver mObserver;
    final int mObserverUserId;
    final boolean mSelfChange;
    final int mUid;
    
    ObserverCall(ContentService.ObserverNode paramObserverNode, IContentObserver paramIContentObserver, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      this.mNode = paramObserverNode;
      this.mObserver = paramIContentObserver;
      this.mSelfChange = paramBoolean;
      this.mObserverUserId = paramInt1;
      this.mUid = paramInt2;
    }
  }
  
  public static final class ObserverNode
  {
    public static final int DELETE_TYPE = 2;
    public static final int INSERT_TYPE = 0;
    public static final int UPDATE_TYPE = 1;
    private ArrayList<ObserverNode> mChildren = new ArrayList();
    private String mName;
    private ArrayList<ObserverEntry> mObservers = new ArrayList();
    
    public ObserverNode(String paramString)
    {
      this.mName = paramString;
    }
    
    private void addObserverLocked(Uri paramUri, int paramInt1, IContentObserver paramIContentObserver, boolean paramBoolean, Object paramObject, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramInt1 == countUriSegments(paramUri))
      {
        this.mObservers.add(new ObserverEntry(paramIContentObserver, paramBoolean, paramObject, paramInt2, paramInt3, paramInt4));
        return;
      }
      Object localObject = getUriSegment(paramUri, paramInt1);
      if (localObject == null) {
        throw new IllegalArgumentException("Invalid Uri (" + paramUri + ") used for observer");
      }
      int j = this.mChildren.size();
      int i = 0;
      while (i < j)
      {
        ObserverNode localObserverNode = (ObserverNode)this.mChildren.get(i);
        if (localObserverNode.mName.equals(localObject))
        {
          localObserverNode.addObserverLocked(paramUri, paramInt1 + 1, paramIContentObserver, paramBoolean, paramObject, paramInt2, paramInt3, paramInt4);
          return;
        }
        i += 1;
      }
      localObject = new ObserverNode((String)localObject);
      this.mChildren.add(localObject);
      ((ObserverNode)localObject).addObserverLocked(paramUri, paramInt1 + 1, paramIContentObserver, paramBoolean, paramObject, paramInt2, paramInt3, paramInt4);
    }
    
    private void collectMyObserversLocked(boolean paramBoolean1, IContentObserver paramIContentObserver, boolean paramBoolean2, int paramInt1, int paramInt2, ArrayList<ContentService.ObserverCall> paramArrayList)
    {
      int j = this.mObservers.size();
      int i;
      label18:
      ObserverEntry localObserverEntry;
      boolean bool;
      if (paramIContentObserver == null)
      {
        paramIContentObserver = null;
        i = 0;
        if (i >= j) {
          return;
        }
        localObserverEntry = (ObserverEntry)this.mObservers.get(i);
        if (localObserverEntry.observer.asBinder() != paramIContentObserver) {
          break label118;
        }
        bool = true;
        label56:
        if ((!bool) || (paramBoolean2))
        {
          if ((paramInt2 != -1) && (ObserverEntry.-get0(localObserverEntry) != -1)) {
            break label124;
          }
          label80:
          if (!paramBoolean1) {
            break label137;
          }
          if (((paramInt1 & 0x2) == 0) || (!localObserverEntry.notifyForDescendants)) {
            break label145;
          }
        }
      }
      for (;;)
      {
        i += 1;
        break label18;
        paramIContentObserver = paramIContentObserver.asBinder();
        break;
        label118:
        bool = false;
        break label56;
        label124:
        if (paramInt2 == ObserverEntry.-get0(localObserverEntry))
        {
          break label80;
          label137:
          if (localObserverEntry.notifyForDescendants) {
            label145:
            paramArrayList.add(new ContentService.ObserverCall(this, localObserverEntry.observer, bool, UserHandle.getUserId(localObserverEntry.uid), localObserverEntry.uid));
          }
        }
      }
    }
    
    private int countUriSegments(Uri paramUri)
    {
      if (paramUri == null) {
        return 0;
      }
      return paramUri.getPathSegments().size() + 1;
    }
    
    private String getUriSegment(Uri paramUri, int paramInt)
    {
      if (paramUri != null)
      {
        if (paramInt == 0) {
          return paramUri.getAuthority();
        }
        return (String)paramUri.getPathSegments().get(paramInt - 1);
      }
      return null;
    }
    
    public void addObserverLocked(Uri paramUri, IContentObserver paramIContentObserver, boolean paramBoolean, Object paramObject, int paramInt1, int paramInt2, int paramInt3)
    {
      addObserverLocked(paramUri, 0, paramIContentObserver, paramBoolean, paramObject, paramInt1, paramInt2, paramInt3);
    }
    
    public void collectObserversLocked(Uri paramUri, int paramInt1, IContentObserver paramIContentObserver, boolean paramBoolean, int paramInt2, int paramInt3, ArrayList<ContentService.ObserverCall> paramArrayList)
    {
      Object localObject = null;
      int i = countUriSegments(paramUri);
      int j;
      if (paramInt1 >= i)
      {
        collectMyObserversLocked(true, paramIContentObserver, paramBoolean, paramInt2, paramInt3, paramArrayList);
        j = this.mChildren.size();
        i = 0;
      }
      for (;;)
      {
        if (i < j)
        {
          ObserverNode localObserverNode = (ObserverNode)this.mChildren.get(i);
          if ((localObject == null) || (localObserverNode.mName.equals(localObject)))
          {
            localObserverNode.collectObserversLocked(paramUri, paramInt1 + 1, paramIContentObserver, paramBoolean, paramInt2, paramInt3, paramArrayList);
            if (localObject == null) {}
          }
        }
        else
        {
          return;
          if (paramInt1 >= i) {
            break;
          }
          localObject = getUriSegment(paramUri, paramInt1);
          collectMyObserversLocked(false, paramIContentObserver, paramBoolean, paramInt2, paramInt3, paramArrayList);
          break;
        }
        i += 1;
      }
    }
    
    public void dumpLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, String paramString1, String paramString2, int[] paramArrayOfInt, SparseIntArray paramSparseIntArray)
    {
      Object localObject1 = null;
      Object localObject2;
      int i;
      if (this.mObservers.size() > 0)
      {
        if ("".equals(paramString1)) {}
        for (localObject2 = this.mName;; localObject2 = paramString1 + "/" + this.mName)
        {
          i = 0;
          for (;;)
          {
            localObject1 = localObject2;
            if (i >= this.mObservers.size()) {
              break;
            }
            paramArrayOfInt[1] += 1;
            ((ObserverEntry)this.mObservers.get(i)).dumpLocked(paramFileDescriptor, paramPrintWriter, paramArrayOfString, (String)localObject2, paramString2, paramSparseIntArray);
            i += 1;
          }
        }
      }
      if (this.mChildren.size() > 0)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          if (!"".equals(paramString1)) {
            break label218;
          }
        }
        label218:
        for (localObject2 = this.mName;; localObject2 = paramString1 + "/" + this.mName)
        {
          i = 0;
          while (i < this.mChildren.size())
          {
            paramArrayOfInt[0] += 1;
            ((ObserverNode)this.mChildren.get(i)).dumpLocked(paramFileDescriptor, paramPrintWriter, paramArrayOfString, (String)localObject2, paramString2, paramArrayOfInt, paramSparseIntArray);
            i += 1;
          }
        }
      }
    }
    
    public boolean removeObserverLocked(IContentObserver paramIContentObserver)
    {
      int j = this.mChildren.size();
      int i = 0;
      while (i < j)
      {
        int m = i;
        int k = j;
        if (((ObserverNode)this.mChildren.get(i)).removeObserverLocked(paramIContentObserver))
        {
          this.mChildren.remove(i);
          m = i - 1;
          k = j - 1;
        }
        i = m + 1;
        j = k;
      }
      paramIContentObserver = paramIContentObserver.asBinder();
      j = this.mObservers.size();
      i = 0;
      for (;;)
      {
        if (i < j)
        {
          ObserverEntry localObserverEntry = (ObserverEntry)this.mObservers.get(i);
          if (localObserverEntry.observer.asBinder() == paramIContentObserver)
          {
            this.mObservers.remove(i);
            paramIContentObserver.unlinkToDeath(localObserverEntry, 0);
          }
        }
        else
        {
          if ((this.mChildren.size() != 0) || (this.mObservers.size() != 0)) {
            break;
          }
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    private class ObserverEntry
      implements IBinder.DeathRecipient
    {
      public final boolean notifyForDescendants;
      public final IContentObserver observer;
      private final Object observersLock;
      public final int pid;
      public final int uid;
      private final int userHandle;
      
      public ObserverEntry(IContentObserver paramIContentObserver, boolean paramBoolean, Object paramObject, int paramInt1, int paramInt2, int paramInt3)
      {
        this.observersLock = paramObject;
        this.observer = paramIContentObserver;
        this.uid = paramInt1;
        this.pid = paramInt2;
        this.userHandle = paramInt3;
        this.notifyForDescendants = paramBoolean;
        try
        {
          this.observer.asBinder().linkToDeath(this, 0);
          return;
        }
        catch (RemoteException this$1)
        {
          binderDied();
        }
      }
      
      public void binderDied()
      {
        synchronized (this.observersLock)
        {
          ContentService.ObserverNode.this.removeObserverLocked(this.observer);
          return;
        }
      }
      
      public void dumpLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, String paramString1, String paramString2, SparseIntArray paramSparseIntArray)
      {
        paramFileDescriptor = null;
        paramSparseIntArray.put(this.pid, paramSparseIntArray.get(this.pid) + 1);
        paramPrintWriter.print(paramString2);
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print(": pid=");
        paramPrintWriter.print(this.pid);
        paramPrintWriter.print(" uid=");
        paramPrintWriter.print(this.uid);
        paramPrintWriter.print(" user=");
        paramPrintWriter.print(this.userHandle);
        paramPrintWriter.print(" target=");
        if (this.observer != null) {
          paramFileDescriptor = this.observer.asBinder();
        }
        paramPrintWriter.println(Integer.toHexString(System.identityHashCode(paramFileDescriptor)));
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/content/ContentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */