package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.accounts.AccountManagerInternal;
import android.accounts.AccountManagerInternal.OnAppPermissionChangeListener;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncAdapter.Stub;
import android.content.ISyncContext.Stub;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.ServiceConnection;
import android.content.SyncActivityTooManyDeletes;
import android.content.SyncAdapterType;
import android.content.SyncAdaptersCache;
import android.content.SyncInfo;
import android.content.SyncResult;
import android.content.SyncStats;
import android.content.SyncStatusInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ProviderInfo;
import android.content.pm.RegisteredServicesCache.ServiceInfo;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteCallback;
import android.os.RemoteCallback.OnResultListener;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings.Global;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.accounts.AccountManagerService;
import com.android.server.backup.AccountSyncSettingsBackupHelper;
import com.android.server.job.JobSchedulerInternal;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SyncManager
{
  private static final boolean DEBUG_ACCOUNT_ACCESS = false;
  private static final long DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS = 3600L;
  private static final int DELAY_RETRY_SYNC_IN_PROGRESS_IN_SECONDS = 10;
  private static final String HANDLE_SYNC_ALARM_WAKE_LOCK = "SyncManagerHandleSyncAlarm";
  private static final AccountAndUser[] INITIAL_ACCOUNTS_ARRAY = new AccountAndUser[0];
  private static final long INITIAL_SYNC_RETRY_TIME_IN_MS = 30000L;
  private static final long LOCAL_SYNC_DELAY = SystemProperties.getLong("sync.local_sync_delay", 30000L);
  private static final int MAX_SYNC_JOB_ID = 110000;
  private static final int MIN_SYNC_JOB_ID = 100000;
  private static final long SYNC_DELAY_ON_CONFLICT = 10000L;
  private static final long SYNC_DELAY_ON_LOW_STORAGE = 3600000L;
  private static final String SYNC_LOOP_WAKE_LOCK = "SyncLoopWakeLock";
  private static final int SYNC_MONITOR_PROGRESS_THRESHOLD_BYTES = 10;
  private static final long SYNC_MONITOR_WINDOW_LENGTH_MILLIS = 60000L;
  private static final int SYNC_OP_STATE_INVALID = 1;
  private static final int SYNC_OP_STATE_INVALID_NO_ACCOUNT_ACCESS = 2;
  private static final int SYNC_OP_STATE_VALID = 0;
  private static final String SYNC_WAKE_LOCK_PREFIX = "*sync*/";
  static final String TAG = "SyncManager";
  private final AccountManager mAccountManager;
  private final AccountManagerInternal mAccountManagerInternal;
  private final BroadcastReceiver mAccountsUpdatedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      SyncManager.-wrap27(SyncManager.this, SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL);
    }
  };
  protected final ArrayList<ActiveSyncContext> mActiveSyncContexts = Lists.newArrayList();
  private final IBatteryStats mBatteryStats;
  private volatile boolean mBootCompleted = false;
  private final BroadcastReceiver mBootCompletedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      SyncManager.-set0(SyncManager.this, true);
      SyncManager.-wrap28(SyncManager.this);
      SyncManager.-get13(SyncManager.this).onBootCompleted();
    }
  };
  private ConnectivityManager mConnManagerDoNotUseDirectly;
  private BroadcastReceiver mConnectivityIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = SyncManager.-get4(SyncManager.this);
      SyncManager.-set1(SyncManager.this, SyncManager.-wrap6(SyncManager.this));
      if (SyncManager.-get4(SyncManager.this))
      {
        if ((!bool) && (Log.isLoggable("SyncManager", 2))) {
          Slog.v("SyncManager", "Reconnection detected: clearing all backoffs");
        }
        SyncManager.-wrap10(SyncManager.this);
      }
    }
  };
  private Context mContext;
  private volatile boolean mDataConnectionIsConnected = false;
  private volatile boolean mDeviceIsIdle = false;
  private volatile PowerManager.WakeLock mHandleAlarmWakeLock;
  private JobScheduler mJobScheduler;
  private JobSchedulerInternal mJobSchedulerInternal;
  private volatile boolean mJobServiceReady = false;
  private final NotificationManager mNotificationMgr;
  private final PackageManagerInternal mPackageManagerInternal;
  private final PowerManager mPowerManager;
  private volatile boolean mProvisioned;
  private final Random mRand;
  private volatile boolean mReportedSyncActive = false;
  private volatile AccountAndUser[] mRunningAccounts = INITIAL_ACCOUNTS_ARRAY;
  private BroadcastReceiver mShutdownIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Log.w("SyncManager", "Writing sync state before shutdown...");
      SyncManager.this.getSyncStorageEngine().writeAllState();
    }
  };
  private final BroadcastReceiver mStorageIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.intent.action.DEVICE_STORAGE_LOW".equals(paramAnonymousContext))
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "Internal storage is low.");
        }
        SyncManager.-set5(SyncManager.this, true);
        SyncManager.this.cancelActiveSync(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL, null);
      }
      while (!"android.intent.action.DEVICE_STORAGE_OK".equals(paramAnonymousContext)) {
        return;
      }
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "Internal storage is ok.");
      }
      SyncManager.-set5(SyncManager.this, false);
      SyncManager.-wrap21(SyncManager.this, SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL);
    }
  };
  private volatile boolean mStorageIsLow = false;
  protected SyncAdaptersCache mSyncAdapters;
  private final SyncHandler mSyncHandler;
  private SyncJobService mSyncJobService;
  private volatile PowerManager.WakeLock mSyncManagerWakeLock;
  private SyncStorageEngine mSyncStorageEngine;
  private BroadcastReceiver mUserIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
      if (i == 55536) {
        return;
      }
      if ("android.intent.action.USER_REMOVED".equals(paramAnonymousContext)) {
        SyncManager.-wrap15(SyncManager.this, i);
      }
      do
      {
        return;
        if ("android.intent.action.USER_UNLOCKED".equals(paramAnonymousContext))
        {
          SyncManager.-wrap17(SyncManager.this, i);
          return;
        }
      } while (!"android.intent.action.USER_STOPPED".equals(paramAnonymousContext));
      SyncManager.-wrap16(SyncManager.this, i);
    }
  };
  private final UserManager mUserManager;
  
  public SyncManager(Context arg1, boolean paramBoolean)
  {
    this.mContext = ???;
    SyncStorageEngine.init(???);
    this.mSyncStorageEngine = SyncStorageEngine.getSingleton();
    this.mSyncStorageEngine.setOnSyncRequestListener(new SyncStorageEngine.OnSyncRequestListener()
    {
      public void onSyncRequest(SyncStorageEngine.EndPoint paramAnonymousEndPoint, int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        SyncManager.this.scheduleSync(paramAnonymousEndPoint.account, paramAnonymousEndPoint.userId, paramAnonymousInt, paramAnonymousEndPoint.provider, paramAnonymousBundle, -2);
      }
    });
    this.mSyncStorageEngine.setPeriodicSyncAddedListener(new SyncStorageEngine.PeriodicSyncAddedListener()
    {
      public void onPeriodicSyncAdded(SyncStorageEngine.EndPoint paramAnonymousEndPoint, Bundle paramAnonymousBundle, long paramAnonymousLong1, long paramAnonymousLong2)
      {
        SyncManager.this.updateOrAddPeriodicSync(paramAnonymousEndPoint, paramAnonymousLong1, paramAnonymousLong2, paramAnonymousBundle);
      }
    });
    this.mSyncStorageEngine.setOnAuthorityRemovedListener(new SyncStorageEngine.OnAuthorityRemovedListener()
    {
      public void onAuthorityRemoved(SyncStorageEngine.EndPoint paramAnonymousEndPoint)
      {
        SyncManager.-wrap20(SyncManager.this, paramAnonymousEndPoint);
      }
    });
    this.mSyncAdapters = new SyncAdaptersCache(this.mContext);
    this.mSyncHandler = new SyncHandler(BackgroundThread.get().getLooper());
    this.mSyncAdapters.setListener(new RegisteredServicesCacheListener()
    {
      public void onServiceChanged(SyncAdapterType paramAnonymousSyncAdapterType, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (!paramAnonymousBoolean) {
          SyncManager.this.scheduleSync(null, -1, -3, paramAnonymousSyncAdapterType.authority, null, -2);
        }
      }
    }, this.mSyncHandler);
    this.mRand = new Random(System.currentTimeMillis());
    final Object localObject1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    ???.registerReceiver(this.mConnectivityIntentReceiver, (IntentFilter)localObject1);
    if (!paramBoolean)
    {
      localObject1 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
      ((IntentFilter)localObject1).setPriority(1000);
      ???.registerReceiver(this.mBootCompletedReceiver, (IntentFilter)localObject1);
    }
    localObject1 = new IntentFilter("android.intent.action.DEVICE_STORAGE_LOW");
    ((IntentFilter)localObject1).addAction("android.intent.action.DEVICE_STORAGE_OK");
    ???.registerReceiver(this.mStorageIntentReceiver, (IntentFilter)localObject1);
    localObject1 = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
    ((IntentFilter)localObject1).setPriority(100);
    ???.registerReceiver(this.mShutdownIntentReceiver, (IntentFilter)localObject1);
    localObject1 = new IntentFilter();
    ((IntentFilter)localObject1).addAction("android.intent.action.USER_REMOVED");
    ((IntentFilter)localObject1).addAction("android.intent.action.USER_UNLOCKED");
    ((IntentFilter)localObject1).addAction("android.intent.action.USER_STOPPED");
    this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, (IntentFilter)localObject1, null, null);
    if (!paramBoolean) {
      this.mNotificationMgr = ((NotificationManager)???.getSystemService("notification"));
    }
    for (;;)
    {
      this.mPowerManager = ((PowerManager)???.getSystemService("power"));
      this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
      this.mAccountManager = ((AccountManager)this.mContext.getSystemService("account"));
      this.mAccountManagerInternal = ((AccountManagerInternal)LocalServices.getService(AccountManagerInternal.class));
      this.mPackageManagerInternal = ((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class));
      this.mAccountManagerInternal.addOnAppPermissionChangeListener(new -void__init__android_content_Context_context_boolean_factoryTest_LambdaImpl0());
      this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
      this.mHandleAlarmWakeLock = this.mPowerManager.newWakeLock(1, "SyncManagerHandleSyncAlarm");
      this.mHandleAlarmWakeLock.setReferenceCounted(false);
      this.mSyncManagerWakeLock = this.mPowerManager.newWakeLock(1, "SyncLoopWakeLock");
      this.mSyncManagerWakeLock.setReferenceCounted(false);
      this.mProvisioned = isDeviceProvisioned();
      ContentObserver local12;
      if (!this.mProvisioned)
      {
        localObject1 = ???.getContentResolver();
        local12 = new ContentObserver(null)
        {
          public void onChange(boolean paramAnonymousBoolean)
          {
            SyncManager localSyncManager = SyncManager.this;
            SyncManager.-set3(localSyncManager, SyncManager.-get10(localSyncManager) | SyncManager.-wrap4(SyncManager.this));
            if (SyncManager.-get10(SyncManager.this))
            {
              SyncManager.-get13(SyncManager.this).onDeviceProvisioned();
              localObject1.unregisterContentObserver(this);
            }
          }
        };
      }
      synchronized (this.mSyncHandler)
      {
        ((ContentResolver)localObject1).registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, local12);
        this.mProvisioned |= isDeviceProvisioned();
        if (this.mProvisioned) {
          ((ContentResolver)localObject1).unregisterContentObserver(local12);
        }
        if (!paramBoolean) {
          this.mContext.registerReceiverAsUser(this.mAccountsUpdatedReceiver, UserHandle.ALL, new IntentFilter("android.accounts.LOGIN_ACCOUNTS_CHANGED"), null, null);
        }
        ??? = new Intent(this.mContext, SyncJobService.class);
        ???.putExtra("messenger", new Messenger(this.mSyncHandler));
        new Handler(this.mContext.getMainLooper()).post(new Runnable()
        {
          public void run()
          {
            SyncManager.-get3(SyncManager.this).startService(paramContext);
          }
        });
        whiteListExistingSyncAdaptersIfNeeded();
        return;
        this.mNotificationMgr = null;
      }
    }
  }
  
  private boolean canAccessAccount(Account paramAccount, String paramString, int paramInt)
  {
    if (this.mAccountManager.hasAccountAccess(paramAccount, paramString, UserHandle.getUserHandleForUid(paramInt))) {
      return true;
    }
    try
    {
      this.mContext.getPackageManager().getApplicationInfoAsUser(paramString, 1048576, UserHandle.getUserId(paramInt));
      return true;
    }
    catch (PackageManager.NameNotFoundException paramAccount) {}
    return false;
  }
  
  private void cleanupJobs()
  {
    this.mSyncHandler.postAtFrontOfQueue(new Runnable()
    {
      public void run()
      {
        List localList = SyncManager.-wrap8(SyncManager.this);
        HashSet localHashSet = new HashSet();
        Iterator localIterator1 = localList.iterator();
        while (localIterator1.hasNext())
        {
          SyncOperation localSyncOperation1 = (SyncOperation)localIterator1.next();
          if (!localHashSet.contains(localSyncOperation1.key))
          {
            localHashSet.add(localSyncOperation1.key);
            Iterator localIterator2 = localList.iterator();
            while (localIterator2.hasNext())
            {
              SyncOperation localSyncOperation2 = (SyncOperation)localIterator2.next();
              if ((localSyncOperation1 != localSyncOperation2) && (localSyncOperation1.key.equals(localSyncOperation2.key))) {
                SyncManager.-get5(SyncManager.this).cancel(localSyncOperation2.jobId);
              }
            }
          }
        }
      }
    });
  }
  
  private void clearAllBackoffs()
  {
    this.mSyncStorageEngine.clearAllBackoffsLocked();
    rescheduleSyncs(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL);
  }
  
  private void clearBackoffSetting(SyncStorageEngine.EndPoint paramEndPoint)
  {
    Pair localPair = this.mSyncStorageEngine.getBackoff(paramEndPoint);
    if ((localPair != null) && (((Long)localPair.first).longValue() == -1L) && (((Long)localPair.second).longValue() == -1L)) {
      return;
    }
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "Clearing backoffs for " + paramEndPoint);
    }
    this.mSyncStorageEngine.setBackoff(paramEndPoint, -1L, -1L);
    rescheduleSyncs(paramEndPoint);
  }
  
  private int computeSyncable(Account paramAccount, int paramInt, String paramString)
  {
    return computeSyncable(paramAccount, paramInt, paramString, true);
  }
  
  private boolean containsAccountAndUser(AccountAndUser[] paramArrayOfAccountAndUser, Account paramAccount, int paramInt)
  {
    boolean bool2 = false;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < paramArrayOfAccountAndUser.length)
      {
        if ((paramArrayOfAccountAndUser[i].userId == paramInt) && (paramArrayOfAccountAndUser[i].account.equals(paramAccount))) {
          bool1 = true;
        }
      }
      else {
        return bool1;
      }
      i += 1;
    }
  }
  
  private void doDatabaseCleanup()
  {
    Iterator localIterator = this.mUserManager.getUsers(true).iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if (!localUserInfo.partial)
      {
        Account[] arrayOfAccount = AccountManagerService.getSingleton().getAccounts(localUserInfo.id, this.mContext.getOpPackageName());
        this.mSyncStorageEngine.doDatabaseCleanup(arrayOfAccount, localUserInfo.id);
      }
    }
  }
  
  private void dumpDayStatistic(PrintWriter paramPrintWriter, SyncStorageEngine.DayStats paramDayStats)
  {
    paramPrintWriter.print("Success (");
    paramPrintWriter.print(paramDayStats.successCount);
    if (paramDayStats.successCount > 0)
    {
      paramPrintWriter.print(" for ");
      dumpTimeSec(paramPrintWriter, paramDayStats.successTime);
      paramPrintWriter.print(" avg=");
      dumpTimeSec(paramPrintWriter, paramDayStats.successTime / paramDayStats.successCount);
    }
    paramPrintWriter.print(") Failure (");
    paramPrintWriter.print(paramDayStats.failureCount);
    if (paramDayStats.failureCount > 0)
    {
      paramPrintWriter.print(" for ");
      dumpTimeSec(paramPrintWriter, paramDayStats.failureTime);
      paramPrintWriter.print(" avg=");
      dumpTimeSec(paramPrintWriter, paramDayStats.failureTime / paramDayStats.failureCount);
    }
    paramPrintWriter.println(")");
  }
  
  private void dumpDayStatistics(PrintWriter paramPrintWriter)
  {
    SyncStorageEngine.DayStats[] arrayOfDayStats = this.mSyncStorageEngine.getDayStatistics();
    if ((arrayOfDayStats != null) && (arrayOfDayStats[0] != null))
    {
      paramPrintWriter.println();
      paramPrintWriter.println("Sync Statistics");
      paramPrintWriter.print("  Today:  ");
      dumpDayStatistic(paramPrintWriter, arrayOfDayStats[0]);
      int m = arrayOfDayStats[0].day;
      int i = 1;
      Object localObject1;
      label83:
      int k;
      int j;
      label89:
      int n;
      if ((i <= 6) && (i < arrayOfDayStats.length))
      {
        localObject1 = arrayOfDayStats[i];
        if (localObject1 != null) {}
      }
      else
      {
        k = m;
        j = i;
        if (j >= arrayOfDayStats.length) {
          return;
        }
        localObject1 = null;
        n = k - 7;
      }
      for (;;)
      {
        i = j;
        SyncStorageEngine.DayStats localDayStats;
        if (j < arrayOfDayStats.length)
        {
          localDayStats = arrayOfDayStats[j];
          if (localDayStats != null) {
            break label231;
          }
          i = arrayOfDayStats.length;
        }
        label231:
        do
        {
          j = i;
          k = n;
          if (localObject1 == null) {
            break label89;
          }
          paramPrintWriter.print("  Week-");
          paramPrintWriter.print((m - n) / 7);
          paramPrintWriter.print(": ");
          dumpDayStatistic(paramPrintWriter, (SyncStorageEngine.DayStats)localObject1);
          j = i;
          k = n;
          break label89;
          j = m - ((SyncStorageEngine.DayStats)localObject1).day;
          if (j > 6) {
            break label83;
          }
          paramPrintWriter.print("  Day-");
          paramPrintWriter.print(j);
          paramPrintWriter.print(":  ");
          dumpDayStatistic(paramPrintWriter, (SyncStorageEngine.DayStats)localObject1);
          i += 1;
          break;
          i = j;
        } while (n - localDayStats.day > 6);
        j += 1;
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new SyncStorageEngine.DayStats(n);
        }
        ((SyncStorageEngine.DayStats)localObject2).successCount += localDayStats.successCount;
        ((SyncStorageEngine.DayStats)localObject2).successTime += localDayStats.successTime;
        ((SyncStorageEngine.DayStats)localObject2).failureCount += localDayStats.failureCount;
        ((SyncStorageEngine.DayStats)localObject2).failureTime += localDayStats.failureTime;
        localObject1 = localObject2;
      }
    }
  }
  
  private void dumpRecentHistory(PrintWriter paramPrintWriter)
  {
    ArrayList localArrayList = this.mSyncStorageEngine.getSyncHistory();
    if ((localArrayList != null) && (localArrayList.size() > 0))
    {
      Object localObject5 = Maps.newHashMap();
      long l2 = 0L;
      long l1 = 0L;
      int n = localArrayList.size();
      int j = 0;
      int i = 0;
      Object localObject6 = localArrayList.iterator();
      Object localObject3;
      Object localObject1;
      Object localObject2;
      int k;
      long l3;
      if (((Iterator)localObject6).hasNext())
      {
        localObject3 = (SyncStorageEngine.SyncHistoryItem)((Iterator)localObject6).next();
        localObject1 = this.mSyncStorageEngine.getAuthority(((SyncStorageEngine.SyncHistoryItem)localObject3).authorityId);
        if (localObject1 != null) {
          localObject2 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.provider;
        }
        for (localObject1 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.name + "/" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.type + " u" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.userId;; localObject1 = "Unknown")
        {
          int m = ((String)localObject2).length();
          k = j;
          if (m > j) {
            k = m;
          }
          m = ((String)localObject1).length();
          j = i;
          if (m > i) {
            j = m;
          }
          l3 = ((SyncStorageEngine.SyncHistoryItem)localObject3).elapsedTime;
          l2 += l3;
          l1 += 1L;
          localObject4 = (AuthoritySyncStats)((Map)localObject5).get(localObject2);
          localObject3 = localObject4;
          if (localObject4 == null)
          {
            localObject3 = new AuthoritySyncStats((String)localObject2, null);
            ((Map)localObject5).put(localObject2, localObject3);
          }
          ((AuthoritySyncStats)localObject3).elapsedTime += l3;
          ((AuthoritySyncStats)localObject3).times += 1;
          localObject4 = ((AuthoritySyncStats)localObject3).accountMap;
          localObject3 = (AccountSyncStats)((Map)localObject4).get(localObject1);
          localObject2 = localObject3;
          if (localObject3 == null)
          {
            localObject2 = new AccountSyncStats((String)localObject1, null);
            ((Map)localObject4).put(localObject1, localObject2);
          }
          ((AccountSyncStats)localObject2).elapsedTime += l3;
          ((AccountSyncStats)localObject2).times += 1;
          i = j;
          j = k;
          break;
          localObject2 = "Unknown";
        }
      }
      Object localObject7;
      String str;
      if (l2 > 0L)
      {
        paramPrintWriter.println();
        paramPrintWriter.printf("Detailed Statistics (Recent history):  %d (# of times) %ds (sync time)\n", new Object[] { Long.valueOf(l1), Long.valueOf(l2 / 1000L) });
        localObject4 = new ArrayList(((Map)localObject5).values());
        Collections.sort((List)localObject4, new Comparator()
        {
          public int compare(SyncManager.AuthoritySyncStats paramAnonymousAuthoritySyncStats1, SyncManager.AuthoritySyncStats paramAnonymousAuthoritySyncStats2)
          {
            int j = Integer.compare(paramAnonymousAuthoritySyncStats2.times, paramAnonymousAuthoritySyncStats1.times);
            int i = j;
            if (j == 0) {
              i = Long.compare(paramAnonymousAuthoritySyncStats2.elapsedTime, paramAnonymousAuthoritySyncStats1.elapsedTime);
            }
            return i;
          }
        });
        k = Math.max(j, i + 3);
        localObject1 = new char[k + 4 + 2 + 10 + 11];
        Arrays.fill((char[])localObject1, '-');
        localObject1 = new String((char[])localObject1);
        localObject2 = String.format("  %%-%ds: %%-9s  %%-11s\n", new Object[] { Integer.valueOf(k + 2) });
        localObject3 = String.format("    %%-%ds:   %%-9s  %%-11s\n", new Object[] { Integer.valueOf(k) });
        paramPrintWriter.println((String)localObject1);
        localObject4 = ((Iterable)localObject4).iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (AuthoritySyncStats)((Iterator)localObject4).next();
          localObject6 = ((AuthoritySyncStats)localObject5).name;
          l3 = ((AuthoritySyncStats)localObject5).elapsedTime;
          k = ((AuthoritySyncStats)localObject5).times;
          localObject7 = String.format("%ds/%d%%", new Object[] { Long.valueOf(l3 / 1000L), Long.valueOf(100L * l3 / l2) });
          paramPrintWriter.printf((String)localObject2, new Object[] { localObject6, String.format("%d/%d%%", new Object[] { Integer.valueOf(k), Long.valueOf(k * 100 / l1) }), localObject7 });
          localObject5 = new ArrayList(((AuthoritySyncStats)localObject5).accountMap.values());
          Collections.sort((List)localObject5, new Comparator()
          {
            public int compare(SyncManager.AccountSyncStats paramAnonymousAccountSyncStats1, SyncManager.AccountSyncStats paramAnonymousAccountSyncStats2)
            {
              int j = Integer.compare(paramAnonymousAccountSyncStats2.times, paramAnonymousAccountSyncStats1.times);
              int i = j;
              if (j == 0) {
                i = Long.compare(paramAnonymousAccountSyncStats2.elapsedTime, paramAnonymousAccountSyncStats1.elapsedTime);
              }
              return i;
            }
          });
          localObject5 = ((Iterable)localObject5).iterator();
          while (((Iterator)localObject5).hasNext())
          {
            localObject6 = (AccountSyncStats)((Iterator)localObject5).next();
            l3 = ((AccountSyncStats)localObject6).elapsedTime;
            k = ((AccountSyncStats)localObject6).times;
            localObject7 = String.format("%ds/%d%%", new Object[] { Long.valueOf(l3 / 1000L), Long.valueOf(100L * l3 / l2) });
            str = String.format("%d/%d%%", new Object[] { Integer.valueOf(k), Long.valueOf(k * 100 / l1) });
            paramPrintWriter.printf((String)localObject3, new Object[] { ((AccountSyncStats)localObject6).name, str, localObject7 });
          }
          paramPrintWriter.println((String)localObject1);
        }
      }
      paramPrintWriter.println();
      paramPrintWriter.println("Recent Sync History");
      Object localObject4 = "  %-" + i + "s  %-" + j + "s %s\n";
      localObject5 = Maps.newHashMap();
      localObject6 = this.mContext.getPackageManager();
      i = 0;
      if (i < n)
      {
        localObject7 = (SyncStorageEngine.SyncHistoryItem)localArrayList.get(i);
        localObject1 = this.mSyncStorageEngine.getAuthority(((SyncStorageEngine.SyncHistoryItem)localObject7).authorityId);
        if (localObject1 != null)
        {
          localObject3 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.provider;
          localObject2 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.name + "/" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.type + " u" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.userId;
          label1074:
          l1 = ((SyncStorageEngine.SyncHistoryItem)localObject7).elapsedTime;
          localObject1 = new Time();
          l2 = ((SyncStorageEngine.SyncHistoryItem)localObject7).eventTime;
          ((Time)localObject1).set(l2);
          str = (String)localObject3 + "/" + (String)localObject2;
          localObject1 = (Long)((Map)localObject5).get(str);
          if (localObject1 != null) {
            break label1370;
          }
          localObject1 = "";
          label1156:
          ((Map)localObject5).put(str, Long.valueOf(l2));
          paramPrintWriter.printf("  #%-3d: %s %8s  %5.1fs  %8s", new Object[] { Integer.valueOf(i + 1), formatTime(l2), SyncStorageEngine.SOURCES[localObject7.source], Float.valueOf((float)l1 / 1000.0F), localObject1 });
          paramPrintWriter.printf((String)localObject4, new Object[] { localObject2, localObject3, SyncOperation.reasonToString((PackageManager)localObject6, ((SyncStorageEngine.SyncHistoryItem)localObject7).reason) });
          if ((((SyncStorageEngine.SyncHistoryItem)localObject7).event == 1) && (((SyncStorageEngine.SyncHistoryItem)localObject7).upstreamActivity == 0L)) {
            break label1510;
          }
          label1283:
          paramPrintWriter.printf("    event=%d upstreamActivity=%d downstreamActivity=%d\n", new Object[] { Integer.valueOf(((SyncStorageEngine.SyncHistoryItem)localObject7).event), Long.valueOf(((SyncStorageEngine.SyncHistoryItem)localObject7).upstreamActivity), Long.valueOf(((SyncStorageEngine.SyncHistoryItem)localObject7).downstreamActivity) });
          label1328:
          if ((((SyncStorageEngine.SyncHistoryItem)localObject7).mesg != null) && (!"success".equals(((SyncStorageEngine.SyncHistoryItem)localObject7).mesg))) {
            break label1523;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          localObject3 = "Unknown";
          localObject2 = "Unknown";
          break label1074;
          label1370:
          l3 = (((Long)localObject1).longValue() - l2) / 1000L;
          if (l3 < 60L)
          {
            localObject1 = String.valueOf(l3);
            break label1156;
          }
          if (l3 < 3600L)
          {
            localObject1 = String.format("%02d:%02d", new Object[] { Long.valueOf(l3 / 60L), Long.valueOf(l3 % 60L) });
            break label1156;
          }
          long l4 = l3 % 3600L;
          localObject1 = String.format("%02d:%02d:%02d", new Object[] { Long.valueOf(l3 / 3600L), Long.valueOf(l4 / 60L), Long.valueOf(l4 % 60L) });
          break label1156;
          label1510:
          if (((SyncStorageEngine.SyncHistoryItem)localObject7).downstreamActivity == 0L) {
            break label1328;
          }
          break label1283;
          label1523:
          paramPrintWriter.printf("    mesg=%s\n", new Object[] { ((SyncStorageEngine.SyncHistoryItem)localObject7).mesg });
        }
      }
      paramPrintWriter.println();
      paramPrintWriter.println("Recent Sync History Extras");
      i = 0;
      while (i < n)
      {
        localObject3 = (SyncStorageEngine.SyncHistoryItem)localArrayList.get(i);
        localObject5 = ((SyncStorageEngine.SyncHistoryItem)localObject3).extras;
        if ((localObject5 == null) || (((Bundle)localObject5).size() == 0))
        {
          i += 1;
        }
        else
        {
          localObject1 = this.mSyncStorageEngine.getAuthority(((SyncStorageEngine.SyncHistoryItem)localObject3).authorityId);
          if (localObject1 != null) {
            localObject2 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.provider;
          }
          for (localObject1 = ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.name + "/" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.account.type + " u" + ((SyncStorageEngine.AuthorityInfo)localObject1).target.userId;; localObject1 = "Unknown")
          {
            localObject6 = new Time();
            l1 = ((SyncStorageEngine.SyncHistoryItem)localObject3).eventTime;
            ((Time)localObject6).set(l1);
            paramPrintWriter.printf("  #%-3d: %s %8s ", new Object[] { Integer.valueOf(i + 1), formatTime(l1), SyncStorageEngine.SOURCES[localObject3.source] });
            paramPrintWriter.printf((String)localObject4, new Object[] { localObject1, localObject2, localObject5 });
            break;
            localObject2 = "Unknown";
          }
        }
      }
    }
  }
  
  private void dumpSyncAdapters(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println();
    Object localObject1 = getAllUsers();
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (UserInfo)((Iterator)localObject1).next();
        paramIndentingPrintWriter.println("Sync adapters for " + localObject2 + ":");
        paramIndentingPrintWriter.increaseIndent();
        localObject2 = this.mSyncAdapters.getAllServices(((UserInfo)localObject2).id).iterator();
        while (((Iterator)localObject2).hasNext()) {
          paramIndentingPrintWriter.println((RegisteredServicesCache.ServiceInfo)((Iterator)localObject2).next());
        }
        paramIndentingPrintWriter.decreaseIndent();
        paramIndentingPrintWriter.println();
      }
    }
  }
  
  private void dumpTimeSec(PrintWriter paramPrintWriter, long paramLong)
  {
    paramPrintWriter.print(paramLong / 1000L);
    paramPrintWriter.print('.');
    paramPrintWriter.print(paramLong / 100L % 10L);
    paramPrintWriter.print('s');
  }
  
  static String formatTime(long paramLong)
  {
    Time localTime = new Time();
    localTime.set(paramLong);
    return localTime.format("%Y-%m-%d %H:%M:%S");
  }
  
  private List<SyncOperation> getAllPendingSyncs()
  {
    verifyJobScheduler();
    Object localObject = this.mJobSchedulerInternal.getSystemScheduledPendingJobs();
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SyncOperation localSyncOperation = SyncOperation.maybeCreateFromJobExtras(((JobInfo)((Iterator)localObject).next()).getExtras());
      if (localSyncOperation != null) {
        localArrayList.add(localSyncOperation);
      }
    }
    return localArrayList;
  }
  
  private List<UserInfo> getAllUsers()
  {
    return this.mUserManager.getUsers();
  }
  
  private ConnectivityManager getConnectivityManager()
  {
    try
    {
      if (this.mConnManagerDoNotUseDirectly == null) {
        this.mConnManagerDoNotUseDirectly = ((ConnectivityManager)this.mContext.getSystemService("connectivity"));
      }
      ConnectivityManager localConnectivityManager = this.mConnManagerDoNotUseDirectly;
      return localConnectivityManager;
    }
    finally {}
  }
  
  private Context getContextForUser(UserHandle paramUserHandle)
  {
    try
    {
      paramUserHandle = this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, paramUserHandle);
      return paramUserHandle;
    }
    catch (PackageManager.NameNotFoundException paramUserHandle) {}
    return this.mContext;
  }
  
  private int getIsSyncable(Account paramAccount, int paramInt, String paramString)
  {
    int i = this.mSyncStorageEngine.getIsSyncable(paramAccount, paramInt, paramString);
    UserInfo localUserInfo = UserManager.get(this.mContext).getUserInfo(paramInt);
    if ((localUserInfo != null) && (localUserInfo.isRestricted()))
    {
      paramString = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(paramString, paramAccount.type), paramInt);
      if (paramString == null) {
        return 0;
      }
    }
    else
    {
      return i;
    }
    try
    {
      paramString = AppGlobals.getPackageManager().getPackageInfo(paramString.componentName.getPackageName(), 0, paramInt);
      if (paramString == null) {
        return 0;
      }
    }
    catch (RemoteException paramAccount)
    {
      return 0;
    }
    if ((paramString.restrictedAccountType != null) && (paramString.restrictedAccountType.equals(paramAccount.type))) {
      return i;
    }
    return 0;
  }
  
  private JobScheduler getJobScheduler()
  {
    verifyJobScheduler();
    return this.mJobScheduler;
  }
  
  private long getTotalBytesTransferredByUid(int paramInt)
  {
    return TrafficStats.getUidRxBytes(paramInt) + TrafficStats.getUidTxBytes(paramInt);
  }
  
  private int getUnusedJobIdH()
  {
    int i;
    do
    {
      i = 100000 + this.mRand.nextInt(10000);
    } while (isJobIdInUseLockedH(i, this.mJobSchedulerInternal.getSystemScheduledPendingJobs()));
    return i;
  }
  
  private void increaseBackoffSetting(SyncStorageEngine.EndPoint paramEndPoint)
  {
    long l3 = SystemClock.elapsedRealtime();
    Pair localPair = this.mSyncStorageEngine.getBackoff(paramEndPoint);
    long l2 = -1L;
    if (localPair != null)
    {
      if (l3 < ((Long)localPair.first).longValue())
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "Still in backoff, do not increase it. Remaining: " + (((Long)localPair.first).longValue() - l3) / 1000L + " seconds.");
        }
        return;
      }
      l2 = ((Long)localPair.second).longValue() * 2L;
    }
    long l1 = l2;
    if (l2 <= 0L) {
      l1 = jitterize(30000L, 33000L);
    }
    long l4 = Settings.Global.getLong(this.mContext.getContentResolver(), "sync_max_retry_delay_in_seconds", 3600L);
    l2 = l1;
    if (l1 > 1000L * l4) {
      l2 = l4 * 1000L;
    }
    l1 = l3 + l2;
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "Backoff until: " + l1 + ", delayTime: " + l2);
    }
    this.mSyncStorageEngine.setBackoff(paramEndPoint, l1, l2);
    rescheduleSyncs(paramEndPoint);
  }
  
  private boolean isAdapterDelayed(SyncStorageEngine.EndPoint paramEndPoint)
  {
    long l = SystemClock.elapsedRealtime();
    Pair localPair = this.mSyncStorageEngine.getBackoff(paramEndPoint);
    if ((localPair != null) && (((Long)localPair.first).longValue() != -1L) && (((Long)localPair.first).longValue() > l)) {
      return true;
    }
    return this.mSyncStorageEngine.getDelayUntilTime(paramEndPoint) > l;
  }
  
  private boolean isDeviceProvisioned()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isJobIdInUseLockedH(int paramInt, List<JobInfo> paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      if (((JobInfo)paramList.next()).getId() == paramInt) {
        return true;
      }
    }
    paramList = this.mActiveSyncContexts.iterator();
    while (paramList.hasNext()) {
      if (((ActiveSyncContext)paramList.next()).mSyncOperation.jobId == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean isSyncSetting(String paramString)
  {
    if (paramString.equals("expedited")) {
      return true;
    }
    if (paramString.equals("ignore_settings")) {
      return true;
    }
    if (paramString.equals("ignore_backoff")) {
      return true;
    }
    if (paramString.equals("do_not_retry")) {
      return true;
    }
    if (paramString.equals("force")) {
      return true;
    }
    if (paramString.equals("upload")) {
      return true;
    }
    if (paramString.equals("deletions_override")) {
      return true;
    }
    if (paramString.equals("discard_deletions")) {
      return true;
    }
    if (paramString.equals("expected_upload")) {
      return true;
    }
    if (paramString.equals("expected_download")) {
      return true;
    }
    if (paramString.equals("sync_priority")) {
      return true;
    }
    if (paramString.equals("allow_metered")) {
      return true;
    }
    return paramString.equals("initialize");
  }
  
  private boolean isSyncStillActiveH(ActiveSyncContext paramActiveSyncContext)
  {
    Iterator localIterator = this.mActiveSyncContexts.iterator();
    while (localIterator.hasNext()) {
      if ((ActiveSyncContext)localIterator.next() == paramActiveSyncContext) {
        return true;
      }
    }
    return false;
  }
  
  private long jitterize(long paramLong1, long paramLong2)
  {
    Random localRandom = new Random(SystemClock.elapsedRealtime());
    paramLong2 -= paramLong1;
    if (paramLong2 > 2147483647L) {
      throw new IllegalArgumentException("the difference between the maxValue and the minValue must be less than 2147483647");
    }
    return localRandom.nextInt((int)paramLong2) + paramLong1;
  }
  
  private void maybeRescheduleSync(SyncResult paramSyncResult, SyncOperation paramSyncOperation)
  {
    boolean bool = Log.isLoggable("SyncManager", 3);
    if (bool) {
      Log.d("SyncManager", "encountered error(s) during the sync: " + paramSyncResult + ", " + paramSyncOperation);
    }
    if (paramSyncOperation.extras.getBoolean("ignore_backoff", false)) {
      paramSyncOperation.extras.remove("ignore_backoff");
    }
    if ((!paramSyncOperation.extras.getBoolean("do_not_retry", false)) || (paramSyncResult.syncAlreadyInProgress))
    {
      if ((paramSyncOperation.extras.getBoolean("upload", false)) && (!paramSyncResult.syncAlreadyInProgress)) {
        break label182;
      }
      if (!paramSyncResult.tooManyRetries) {
        break label228;
      }
      if (bool) {
        Log.d("SyncManager", "not retrying sync operation because it retried too many times: " + paramSyncOperation);
      }
    }
    while (!bool) {
      return;
    }
    Log.d("SyncManager", "not retrying sync operation because SYNC_EXTRAS_DO_NOT_RETRY was specified " + paramSyncOperation);
    return;
    label182:
    paramSyncOperation.extras.remove("upload");
    if (bool) {
      Log.d("SyncManager", "retrying sync operation as a two-way sync because an upload-only sync encountered an error: " + paramSyncOperation);
    }
    scheduleSyncOperationH(paramSyncOperation);
    return;
    label228:
    if (paramSyncResult.madeSomeProgress())
    {
      if (bool) {
        Log.d("SyncManager", "retrying sync operation because even though it had an error it achieved some success");
      }
      scheduleSyncOperationH(paramSyncOperation);
      return;
    }
    if (paramSyncResult.syncAlreadyInProgress)
    {
      if (bool) {
        Log.d("SyncManager", "retrying sync operation that failed because there was already a sync in progress: " + paramSyncOperation);
      }
      scheduleSyncOperationH(paramSyncOperation, 10000L);
      return;
    }
    if (paramSyncResult.hasSoftError())
    {
      if (bool) {
        Log.d("SyncManager", "retrying sync operation because it encountered a soft error: " + paramSyncOperation);
      }
      scheduleSyncOperationH(paramSyncOperation);
      return;
    }
    Log.d("SyncManager", "not retrying sync operation because the error is a hard error: " + paramSyncOperation);
  }
  
  private void onUserRemoved(int paramInt)
  {
    updateRunningAccounts(null);
    this.mSyncStorageEngine.doDatabaseCleanup(new Account[0], paramInt);
    Iterator localIterator = getAllPendingSyncs().iterator();
    while (localIterator.hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
      if (localSyncOperation.target.userId == paramInt) {
        getJobScheduler().cancel(localSyncOperation.jobId);
      }
    }
  }
  
  private void onUserStopped(int paramInt)
  {
    updateRunningAccounts(null);
    cancelActiveSync(new SyncStorageEngine.EndPoint(null, null, paramInt), null);
  }
  
  private void onUserUnlocked(int paramInt)
  {
    AccountManagerService.getSingleton().validateAccounts(paramInt);
    this.mSyncAdapters.invalidateCache(paramInt);
    updateRunningAccounts(new SyncStorageEngine.EndPoint(null, null, paramInt));
    Account[] arrayOfAccount = AccountManagerService.getSingleton().getAccounts(paramInt, this.mContext.getOpPackageName());
    int j = arrayOfAccount.length;
    int i = 0;
    while (i < j)
    {
      scheduleSync(arrayOfAccount[i], paramInt, -8, null, null, -1);
      i += 1;
    }
  }
  
  private void postMonitorSyncProgressMessage(ActiveSyncContext paramActiveSyncContext)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "posting MESSAGE_SYNC_MONITOR in 60s");
    }
    paramActiveSyncContext.mBytesTransferredAtLastPoll = getTotalBytesTransferredByUid(paramActiveSyncContext.mSyncAdapterUid);
    paramActiveSyncContext.mLastPolledTimeElapsed = SystemClock.elapsedRealtime();
    paramActiveSyncContext = this.mSyncHandler.obtainMessage(8, paramActiveSyncContext);
    this.mSyncHandler.sendMessageDelayed(paramActiveSyncContext, 60000L);
  }
  
  private void postScheduleSyncMessage(SyncOperation paramSyncOperation, long paramLong)
  {
    paramSyncOperation = new ScheduleSyncMessagePayload(paramSyncOperation, paramLong);
    this.mSyncHandler.obtainMessage(12, paramSyncOperation).sendToTarget();
  }
  
  private boolean readDataConnectionState()
  {
    NetworkInfo localNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
    if (localNetworkInfo != null) {
      return localNetworkInfo.isConnected();
    }
    return false;
  }
  
  private void removeSyncsForAuthority(SyncStorageEngine.EndPoint paramEndPoint)
  {
    verifyJobScheduler();
    Iterator localIterator = getAllPendingSyncs().iterator();
    while (localIterator.hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
      if (localSyncOperation.target.matchesSpec(paramEndPoint)) {
        getJobScheduler().cancel(localSyncOperation.jobId);
      }
    }
  }
  
  private void rescheduleSyncs(SyncStorageEngine.EndPoint paramEndPoint)
  {
    Object localObject = getAllPendingSyncs();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)((Iterator)localObject).next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint)))
      {
        i += 1;
        getJobScheduler().cancel(localSyncOperation.jobId);
        postScheduleSyncMessage(localSyncOperation, 0L);
      }
    }
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "Rescheduled " + i + " syncs for " + paramEndPoint);
    }
  }
  
  private void scheduleSync(Account paramAccount, int paramInt1, int paramInt2, String paramString, Bundle paramBundle, int paramInt3, long paramLong)
  {
    boolean bool2 = Log.isLoggable("SyncManager", 2);
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    if (bool2) {
      Log.d("SyncManager", "one-time sync for: " + paramAccount + " " + localBundle.toString() + " " + paramString);
    }
    Object localObject1 = null;
    if (paramAccount != null) {
      if (paramInt1 != -1)
      {
        paramBundle = new AccountAndUser[1];
        paramBundle[0] = new AccountAndUser(paramAccount, paramInt1);
      }
    }
    Object localObject2;
    int i;
    int j;
    Object localObject3;
    while (ArrayUtils.isEmpty(paramBundle))
    {
      if (bool2) {
        Slog.v("SyncManager", "scheduleSync: no accounts configured, dropping");
      }
      return;
      localObject2 = this.mRunningAccounts;
      i = 0;
      j = localObject2.length;
      for (;;)
      {
        paramBundle = (Bundle)localObject1;
        if (i >= j) {
          break;
        }
        localObject3 = localObject2[i];
        paramBundle = (Bundle)localObject1;
        if (paramAccount.equals(((AccountAndUser)localObject3).account)) {
          paramBundle = (AccountAndUser[])ArrayUtils.appendElement(AccountAndUser.class, (Object[])localObject1, localObject3);
        }
        i += 1;
        localObject1 = paramBundle;
      }
      paramBundle = this.mRunningAccounts;
    }
    boolean bool3 = localBundle.getBoolean("upload", false);
    boolean bool1 = localBundle.getBoolean("force", false);
    if (bool1)
    {
      localBundle.putBoolean("ignore_backoff", true);
      localBundle.putBoolean("ignore_settings", true);
    }
    boolean bool4 = localBundle.getBoolean("ignore_settings", false);
    if (bool3)
    {
      i = 1;
      int n = paramBundle.length;
      j = 0;
      label293:
      if (j >= n) {
        return;
      }
      paramAccount = paramBundle[j];
      if ((paramInt1 < 0) || (paramAccount.userId < 0) || (paramInt1 == paramAccount.userId)) {
        break label362;
      }
    }
    for (;;)
    {
      j += 1;
      break label293;
      if (bool1)
      {
        i = 3;
        break;
      }
      if (paramString == null)
      {
        i = 2;
        break;
      }
      i = 0;
      break;
      label362:
      localObject1 = new HashSet();
      localObject2 = this.mSyncAdapters.getAllServices(paramAccount.userId).iterator();
      while (((Iterator)localObject2).hasNext()) {
        ((HashSet)localObject1).add(((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)((Iterator)localObject2).next()).type).authority);
      }
      if (paramString != null)
      {
        bool1 = ((HashSet)localObject1).contains(paramString);
        ((HashSet)localObject1).clear();
        if (bool1) {
          ((HashSet)localObject1).add(paramString);
        }
      }
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        int m = computeSyncable(paramAccount.account, paramAccount.userId, (String)localObject2);
        if (m != 0)
        {
          localObject3 = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey((String)localObject2, paramAccount.account.type), paramAccount.userId);
          if (localObject3 != null)
          {
            int i1 = ((RegisteredServicesCache.ServiceInfo)localObject3).uid;
            Object localObject4;
            if (m == 3)
            {
              if (bool2) {
                Slog.v("SyncManager", "    Not scheduling sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS");
              }
              localObject4 = new Bundle(localBundle);
              localObject3 = ((RegisteredServicesCache.ServiceInfo)localObject3).componentName.getPackageName();
              if (this.mPackageManagerInternal.wasPackageEverLaunched((String)localObject3, paramInt1)) {
                this.mAccountManagerInternal.requestAccountAccess(paramAccount.account, (String)localObject3, paramInt1, new RemoteCallback(new -void_scheduleSync_android_accounts_Account_requestedAccount_int_userId_int_reason_java_lang_String_requestedAuthority_android_os_Bundle_extras_int_targetSyncState_long_minDelayMillis_LambdaImpl0(paramAccount, paramInt1, paramInt2, (String)localObject2, (Bundle)localObject4, paramInt3, paramLong)));
              }
            }
            else
            {
              boolean bool5 = ((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject3).type).allowParallelSyncs();
              bool1 = ((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject3).type).isAlwaysSyncable();
              int k = m;
              if (m < 0)
              {
                k = m;
                if (bool1)
                {
                  this.mSyncStorageEngine.setIsSyncable(paramAccount.account, paramAccount.userId, (String)localObject2, 1);
                  k = 1;
                }
              }
              if (((paramInt3 == -2) || (paramInt3 == k)) && ((((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject3).type).supportsUploading()) || (!bool3)))
              {
                if ((k >= 0) && (!bool4))
                {
                  if (!this.mSyncStorageEngine.getMasterSyncAutomatically(paramAccount.userId)) {
                    break label848;
                  }
                  bool1 = this.mSyncStorageEngine.getSyncAutomatically(paramAccount.account, paramAccount.userId, (String)localObject2);
                }
                for (;;)
                {
                  if (bool1) {
                    break label854;
                  }
                  if (!bool2) {
                    break;
                  }
                  Log.d("SyncManager", "scheduleSync: sync of " + paramAccount + ", " + (String)localObject2 + " is not allowed, dropping request");
                  break;
                  bool1 = true;
                  continue;
                  label848:
                  bool1 = false;
                }
                label854:
                localObject4 = new SyncStorageEngine.EndPoint(paramAccount.account, (String)localObject2, paramAccount.userId);
                long l = this.mSyncStorageEngine.getDelayUntilTime((SyncStorageEngine.EndPoint)localObject4);
                localObject3 = ((RegisteredServicesCache.ServiceInfo)localObject3).componentName.getPackageName();
                if (k == -1)
                {
                  localObject4 = new Bundle();
                  ((Bundle)localObject4).putBoolean("initialize", true);
                  if (bool2) {
                    Slog.v("SyncManager", "schedule initialisation Sync:, delay until " + l + ", run by " + 0 + ", flexMillis " + 0 + ", source " + i + ", account " + paramAccount + ", authority " + (String)localObject2 + ", extras " + localObject4);
                  }
                  postScheduleSyncMessage(new SyncOperation(paramAccount.account, paramAccount.userId, i1, (String)localObject3, paramInt2, i, (String)localObject2, (Bundle)localObject4, bool5), paramLong);
                }
                else if ((paramInt3 == -2) || (paramInt3 == k))
                {
                  if (bool2) {
                    Slog.v("SyncManager", "scheduleSync: delay until " + l + ", source " + i + ", account " + paramAccount + ", authority " + (String)localObject2 + ", extras " + localBundle);
                  }
                  postScheduleSyncMessage(new SyncOperation(paramAccount.account, paramAccount.userId, i1, (String)localObject3, paramInt2, i, (String)localObject2, localBundle, bool5), paramLong);
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void scheduleSyncOperationH(SyncOperation paramSyncOperation)
  {
    scheduleSyncOperationH(paramSyncOperation, 0L);
  }
  
  private void scheduleSyncOperationH(SyncOperation paramSyncOperation, long paramLong)
  {
    boolean bool = Log.isLoggable("SyncManager", 2);
    if (paramSyncOperation == null)
    {
      Slog.e("SyncManager", "Can't schedule null sync operation.");
      return;
    }
    long l1 = paramLong;
    long l3;
    if (!paramSyncOperation.ignoreBackoff())
    {
      localObject2 = this.mSyncStorageEngine.getBackoff(paramSyncOperation.target);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        Slog.e("SyncManager", "Couldn't find backoff values for " + paramSyncOperation.target);
        localObject1 = new Pair(Long.valueOf(-1L), Long.valueOf(-1L));
      }
      l2 = SystemClock.elapsedRealtime();
      if (((Long)((Pair)localObject1).first).longValue() != -1L) {
        break label312;
      }
      l1 = 0L;
      l3 = this.mSyncStorageEngine.getDelayUntilTime(paramSyncOperation.target);
      if (l3 <= l2) {
        break label331;
      }
    }
    label312:
    label331:
    for (long l2 = l3 - l2;; l2 = 0L)
    {
      if (bool) {
        Slog.v("SyncManager", "backoff delay:" + l1 + " delayUntil delay:" + l2);
      }
      l1 = Math.max(paramLong, Math.max(l1, l2));
      paramLong = l1;
      if (l1 < 0L) {
        paramLong = 0L;
      }
      if (paramSyncOperation.isPeriodic) {
        break label591;
      }
      localObject1 = this.mActiveSyncContexts.iterator();
      do
      {
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
      } while (!((ActiveSyncContext)((Iterator)localObject1).next()).mSyncOperation.key.equals(paramSyncOperation.key));
      if (bool) {
        Log.v("SyncManager", "Duplicate sync is already running. Not scheduling " + paramSyncOperation);
      }
      return;
      l1 = ((Long)((Pair)localObject1).first).longValue() - l2;
      break;
    }
    int i = 0;
    paramSyncOperation.expectedRuntime = (SystemClock.elapsedRealtime() + paramLong);
    List localList = getAllPendingSyncs();
    Object localObject1 = paramSyncOperation;
    Iterator localIterator = localList.iterator();
    SyncOperation localSyncOperation;
    while (localIterator.hasNext())
    {
      localSyncOperation = (SyncOperation)localIterator.next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.key.equals(paramSyncOperation.key)))
      {
        localObject2 = localObject1;
        if (((SyncOperation)localObject1).expectedRuntime > localSyncOperation.expectedRuntime) {
          localObject2 = localSyncOperation;
        }
        i += 1;
        localObject1 = localObject2;
      }
    }
    if (i > 1) {
      Slog.e("SyncManager", "FATAL ERROR! File a bug if you see this.");
    }
    Object localObject2 = localList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localSyncOperation = (SyncOperation)((Iterator)localObject2).next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.key.equals(paramSyncOperation.key)) && (localSyncOperation != localObject1))
      {
        if (bool) {
          Slog.v("SyncManager", "Cancelling duplicate sync " + localSyncOperation);
        }
        getJobScheduler().cancel(localSyncOperation.jobId);
      }
    }
    if (localObject1 != paramSyncOperation)
    {
      if (bool) {
        Slog.v("SyncManager", "Not scheduling because a duplicate exists.");
      }
      return;
    }
    label591:
    if (paramSyncOperation.jobId == -1) {
      paramSyncOperation.jobId = getUnusedJobIdH();
    }
    if (bool) {
      Slog.v("SyncManager", "scheduling sync operation " + paramSyncOperation.toString());
    }
    int j = paramSyncOperation.findPriority();
    if (paramSyncOperation.isNotAllowedOnMetered())
    {
      i = 2;
      localObject1 = new JobInfo.Builder(paramSyncOperation.jobId, new ComponentName(this.mContext, SyncJobService.class)).setExtras(paramSyncOperation.toJobInfoExtras()).setRequiredNetworkType(i).setPersisted(true).setPriority(j);
      if (!paramSyncOperation.isPeriodic) {
        break label781;
      }
      ((JobInfo.Builder)localObject1).setPeriodic(paramSyncOperation.periodMillis, paramSyncOperation.flexMillis);
    }
    for (;;)
    {
      if (paramSyncOperation.extras.getBoolean("require_charging")) {
        ((JobInfo.Builder)localObject1).setRequiresCharging(true);
      }
      getJobScheduler().scheduleAsPackage(((JobInfo.Builder)localObject1).build(), paramSyncOperation.owningPackage, paramSyncOperation.target.userId, paramSyncOperation.wakeLockName());
      return;
      i = 1;
      break;
      label781:
      if (paramLong > 0L) {
        ((JobInfo.Builder)localObject1).setMinimumLatency(paramLong);
      }
      getSyncStorageEngine().markPending(paramSyncOperation.target, true);
    }
  }
  
  private void sendCancelSyncsMessage(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "sending MESSAGE_CANCEL");
    }
    Message localMessage = this.mSyncHandler.obtainMessage();
    localMessage.what = 6;
    localMessage.setData(paramBundle);
    localMessage.obj = paramEndPoint;
    this.mSyncHandler.sendMessage(localMessage);
  }
  
  private void sendSyncFinishedOrCanceledMessage(ActiveSyncContext paramActiveSyncContext, SyncResult paramSyncResult)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "sending MESSAGE_SYNC_FINISHED");
    }
    Message localMessage = this.mSyncHandler.obtainMessage();
    localMessage.what = 1;
    localMessage.obj = new SyncFinishedOrCancelledMessagePayload(paramActiveSyncContext, paramSyncResult);
    this.mSyncHandler.sendMessage(localMessage);
  }
  
  private void setAuthorityPendingState(SyncStorageEngine.EndPoint paramEndPoint)
  {
    Iterator localIterator = getAllPendingSyncs().iterator();
    while (localIterator.hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint)))
      {
        getSyncStorageEngine().markPending(paramEndPoint, true);
        return;
      }
    }
    getSyncStorageEngine().markPending(paramEndPoint, false);
  }
  
  private void setDelayUntilTime(SyncStorageEngine.EndPoint paramEndPoint, long paramLong)
  {
    paramLong *= 1000L;
    long l = System.currentTimeMillis();
    if (paramLong > l) {}
    for (paramLong = SystemClock.elapsedRealtime() + (paramLong - l);; paramLong = 0L)
    {
      this.mSyncStorageEngine.setDelayUntilTime(paramEndPoint, paramLong);
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "Delay Until time set to " + paramLong + " for " + paramEndPoint);
      }
      rescheduleSyncs(paramEndPoint);
      return;
    }
  }
  
  public static boolean syncExtrasEquals(Bundle paramBundle1, Bundle paramBundle2, boolean paramBoolean)
  {
    if (paramBundle1 == paramBundle2) {
      return true;
    }
    if ((paramBoolean) && (paramBundle1.size() != paramBundle2.size())) {
      return false;
    }
    Bundle localBundle;
    if (paramBundle1.size() > paramBundle2.size())
    {
      localBundle = paramBundle1;
      if (paramBundle1.size() <= paramBundle2.size()) {
        break label108;
      }
      paramBundle1 = paramBundle2;
      label50:
      paramBundle2 = localBundle.keySet().iterator();
    }
    for (;;)
    {
      if (paramBundle2.hasNext())
      {
        String str = (String)paramBundle2.next();
        if ((paramBoolean) || (!isSyncSetting(str)))
        {
          if (!paramBundle1.containsKey(str))
          {
            return false;
            localBundle = paramBundle2;
            break;
            label108:
            break label50;
          }
          if (!Objects.equals(localBundle.get(str), paramBundle1.get(str))) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private void updateRunningAccounts(SyncStorageEngine.EndPoint paramEndPoint)
  {
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "sending MESSAGE_ACCOUNTS_UPDATED");
    }
    Message localMessage = this.mSyncHandler.obtainMessage(9);
    localMessage.obj = paramEndPoint;
    localMessage.sendToTarget();
  }
  
  private void verifyJobScheduler()
  {
    try
    {
      Object localObject1 = this.mJobScheduler;
      if (localObject1 != null) {
        return;
      }
      if (Log.isLoggable("SyncManager", 2)) {
        Log.d("SyncManager", "initializing JobScheduler object.");
      }
      this.mJobScheduler = ((JobScheduler)this.mContext.getSystemService("jobscheduler"));
      this.mJobSchedulerInternal = ((JobSchedulerInternal)LocalServices.getService(JobSchedulerInternal.class));
      localObject1 = this.mJobScheduler.getAllPendingJobs().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        SyncOperation localSyncOperation = SyncOperation.maybeCreateFromJobExtras(((JobInfo)((Iterator)localObject1).next()).getExtras());
        if ((localSyncOperation != null) && (!localSyncOperation.isPeriodic)) {
          this.mSyncStorageEngine.markPending(localSyncOperation.target, true);
        }
      }
      cleanupJobs();
    }
    finally {}
  }
  
  private void whiteListExistingSyncAdaptersIfNeeded()
  {
    if (!this.mSyncStorageEngine.shouldGrantSyncAdaptersAccountAccess()) {
      return;
    }
    List localList = this.mUserManager.getUsers(true);
    int k = localList.size();
    int i = 0;
    while (i < k)
    {
      UserHandle localUserHandle = ((UserInfo)localList.get(i)).getUserHandle();
      int m = localUserHandle.getIdentifier();
      Iterator localIterator = this.mSyncAdapters.getAllServices(m).iterator();
      while (localIterator.hasNext())
      {
        RegisteredServicesCache.ServiceInfo localServiceInfo = (RegisteredServicesCache.ServiceInfo)localIterator.next();
        String str = localServiceInfo.componentName.getPackageName();
        Account[] arrayOfAccount = this.mAccountManager.getAccountsByTypeAsUser(((SyncAdapterType)localServiceInfo.type).accountType, localUserHandle);
        int j = 0;
        int n = arrayOfAccount.length;
        while (j < n)
        {
          Account localAccount = arrayOfAccount[j];
          if (!canAccessAccount(localAccount, str, m)) {
            this.mAccountManager.updateAppPermission(localAccount, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", localServiceInfo.uid, true);
          }
          j += 1;
        }
      }
      i += 1;
    }
  }
  
  public void cancelActiveSync(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
  {
    sendCancelSyncsMessage(paramEndPoint, paramBundle);
  }
  
  public void cancelScheduledSyncOperation(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
  {
    Iterator localIterator = getAllPendingSyncs().iterator();
    while (localIterator.hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint)) && (syncExtrasEquals(paramBundle, localSyncOperation.extras, false))) {
        getJobScheduler().cancel(localSyncOperation.jobId);
      }
    }
    setAuthorityPendingState(paramEndPoint);
    if (!this.mSyncStorageEngine.isSyncPending(paramEndPoint)) {
      this.mSyncStorageEngine.setBackoff(paramEndPoint, -1L, -1L);
    }
  }
  
  public void clearScheduledSyncOperations(SyncStorageEngine.EndPoint paramEndPoint)
  {
    Iterator localIterator = getAllPendingSyncs().iterator();
    while (localIterator.hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
      if ((!localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint)))
      {
        getJobScheduler().cancel(localSyncOperation.jobId);
        getSyncStorageEngine().markPending(localSyncOperation.target, false);
      }
    }
    this.mSyncStorageEngine.setBackoff(paramEndPoint, -1L, -1L);
  }
  
  public int computeSyncable(Account paramAccount, int paramInt, String paramString, boolean paramBoolean)
  {
    int i = getIsSyncable(paramAccount, paramInt, paramString);
    if (i == 0) {
      return 0;
    }
    paramString = SyncAdapterType.newKey(paramString, paramAccount.type);
    paramString = this.mSyncAdapters.getServiceInfo(paramString, paramInt);
    if (paramString == null) {
      return 0;
    }
    paramInt = paramString.uid;
    String str = paramString.componentName.getPackageName();
    try
    {
      if (ActivityManagerNative.getDefault().getAppStartMode(paramInt, str) == 2)
      {
        Slog.w("SyncManager", "Not scheduling job " + paramString.uid + ":" + paramString.componentName + " -- package not allowed to start");
        return 0;
      }
    }
    catch (RemoteException localRemoteException)
    {
      if ((!paramBoolean) || (canAccessAccount(paramAccount, str, paramInt))) {
        return i;
      }
      Log.w("SyncManager", "Access to " + paramAccount + " denied for package " + str + " in UID " + paramString.uid);
    }
    return 3;
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter)
  {
    paramFileDescriptor = new IndentingPrintWriter(paramPrintWriter, "  ");
    dumpPendingSyncs(paramPrintWriter);
    dumpPeriodicSyncs(paramPrintWriter);
    dumpSyncState(paramFileDescriptor);
    dumpSyncHistory(paramFileDescriptor);
    dumpSyncAdapters(paramFileDescriptor);
  }
  
  protected void dumpPendingSyncs(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Pending Syncs:");
    Object localObject = getAllPendingSyncs();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)((Iterator)localObject).next();
      if (!localSyncOperation.isPeriodic)
      {
        paramPrintWriter.println(localSyncOperation.dump(null, false));
        i += 1;
      }
    }
    paramPrintWriter.println("Total: " + i);
    paramPrintWriter.println();
  }
  
  protected void dumpPeriodicSyncs(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Periodic Syncs:");
    Object localObject = getAllPendingSyncs();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)((Iterator)localObject).next();
      if (localSyncOperation.isPeriodic)
      {
        paramPrintWriter.println(localSyncOperation.dump(null, false));
        i += 1;
      }
    }
    paramPrintWriter.println("Total: " + i);
    paramPrintWriter.println();
  }
  
  protected void dumpSyncHistory(PrintWriter paramPrintWriter)
  {
    dumpRecentHistory(paramPrintWriter);
    dumpDayStatistics(paramPrintWriter);
  }
  
  protected void dumpSyncState(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("data connected: ");
    paramPrintWriter.println(this.mDataConnectionIsConnected);
    paramPrintWriter.print("auto sync: ");
    Object localObject1 = getAllUsers();
    Object localObject2;
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (UserInfo)((Iterator)localObject1).next();
        paramPrintWriter.print("u" + ((UserInfo)localObject2).id + "=" + this.mSyncStorageEngine.getMasterSyncAutomatically(((UserInfo)localObject2).id) + " ");
      }
      paramPrintWriter.println();
    }
    paramPrintWriter.print("memory low: ");
    paramPrintWriter.println(this.mStorageIsLow);
    paramPrintWriter.print("device idle: ");
    paramPrintWriter.println(this.mDeviceIsIdle);
    paramPrintWriter.print("reported active: ");
    paramPrintWriter.println(this.mReportedSyncActive);
    AccountAndUser[] arrayOfAccountAndUser = AccountManagerService.getSingleton().getAllAccounts();
    paramPrintWriter.print("accounts: ");
    long l1;
    if (arrayOfAccountAndUser != INITIAL_ACCOUNTS_ARRAY)
    {
      paramPrintWriter.println(arrayOfAccountAndUser.length);
      l1 = SystemClock.elapsedRealtime();
      paramPrintWriter.print("now: ");
      paramPrintWriter.print(l1);
      paramPrintWriter.println(" (" + formatTime(System.currentTimeMillis()) + ")");
      paramPrintWriter.println(" (HH:MM:SS)");
      paramPrintWriter.print("uptime: ");
      paramPrintWriter.print(DateUtils.formatElapsedTime(l1 / 1000L));
      paramPrintWriter.println(" (HH:MM:SS)");
      paramPrintWriter.print("time spent syncing: ");
      paramPrintWriter.print(DateUtils.formatElapsedTime(this.mSyncHandler.mSyncTimeTracker.timeSpentSyncing() / 1000L));
      paramPrintWriter.print(" (HH:MM:SS), sync ");
      if (!this.mSyncHandler.mSyncTimeTracker.mLastWasSyncing) {
        break label499;
      }
    }
    Object localObject3;
    label499:
    for (localObject1 = "";; localObject1 = "not ")
    {
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.println("in progress");
      paramPrintWriter.println();
      paramPrintWriter.println("Active Syncs: " + this.mActiveSyncContexts.size());
      localObject1 = this.mContext.getPackageManager();
      localObject2 = this.mActiveSyncContexts.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ActiveSyncContext)((Iterator)localObject2).next();
        long l2 = (l1 - ((ActiveSyncContext)localObject3).mStartTime) / 1000L;
        paramPrintWriter.print("  ");
        paramPrintWriter.print(DateUtils.formatElapsedTime(l2));
        paramPrintWriter.print(" - ");
        paramPrintWriter.print(((ActiveSyncContext)localObject3).mSyncOperation.dump((PackageManager)localObject1, false));
        paramPrintWriter.println();
      }
      paramPrintWriter.println("not known yet");
      break;
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Sync Status");
    int n = arrayOfAccountAndUser.length;
    int j = 0;
    while (j < n)
    {
      localObject3 = arrayOfAccountAndUser[j];
      paramPrintWriter.printf("Account %s u%d %s\n", new Object[] { ((AccountAndUser)localObject3).account.name, Integer.valueOf(((AccountAndUser)localObject3).userId), ((AccountAndUser)localObject3).account.type });
      paramPrintWriter.println("=======================================================================");
      PrintTable localPrintTable = new PrintTable(12);
      localPrintTable.set(0, 0, new Object[] { "Authority", "Syncable", "Enabled", "Delay", "Loc", "Poll", "Per", "Serv", "User", "Tot", "Time", "Last Sync" });
      localObject1 = Lists.newArrayList();
      ((List)localObject1).addAll(this.mSyncAdapters.getAllServices(((AccountAndUser)localObject3).userId));
      Collections.sort((List)localObject1, new Comparator()
      {
        public int compare(RegisteredServicesCache.ServiceInfo<SyncAdapterType> paramAnonymousServiceInfo1, RegisteredServicesCache.ServiceInfo<SyncAdapterType> paramAnonymousServiceInfo2)
        {
          return ((SyncAdapterType)paramAnonymousServiceInfo1.type).authority.compareTo(((SyncAdapterType)paramAnonymousServiceInfo2.type).authority);
        }
      });
      Iterator localIterator = ((Iterable)localObject1).iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (RegisteredServicesCache.ServiceInfo)localIterator.next();
        if (((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject1).type).accountType.equals(((AccountAndUser)localObject3).account.type))
        {
          int k = localPrintTable.getNumRows();
          localObject1 = this.mSyncStorageEngine.getCopyOfAuthorityWithSyncStatus(new SyncStorageEngine.EndPoint(((AccountAndUser)localObject3).account, ((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject1).type).authority, ((AccountAndUser)localObject3).userId));
          SyncStorageEngine.AuthorityInfo localAuthorityInfo = (SyncStorageEngine.AuthorityInfo)((Pair)localObject1).first;
          SyncStatusInfo localSyncStatusInfo = (SyncStatusInfo)((Pair)localObject1).second;
          localObject2 = localAuthorityInfo.target.provider;
          localObject1 = localObject2;
          if (((String)localObject2).length() > 50) {
            localObject1 = ((String)localObject2).substring(((String)localObject2).length() - 50);
          }
          localPrintTable.set(k, 0, new Object[] { localObject1, Integer.valueOf(localAuthorityInfo.syncable), Boolean.valueOf(localAuthorityInfo.enabled) });
          localPrintTable.set(k, 4, new Object[] { Integer.valueOf(localSyncStatusInfo.numSourceLocal), Integer.valueOf(localSyncStatusInfo.numSourcePoll), Integer.valueOf(localSyncStatusInfo.numSourcePeriodic), Integer.valueOf(localSyncStatusInfo.numSourceServer), Integer.valueOf(localSyncStatusInfo.numSourceUser), Integer.valueOf(localSyncStatusInfo.numSyncs), DateUtils.formatElapsedTime(localSyncStatusInfo.totalElapsedTime / 1000L) });
          int i = k;
          int m;
          if (localAuthorityInfo.delayUntil > l1)
          {
            m = k + 1;
            localPrintTable.set(k, 12, new Object[] { "D: " + (localAuthorityInfo.delayUntil - l1) / 1000L });
            i = m;
            if (localAuthorityInfo.backoffTime > l1)
            {
              k = m + 1;
              localPrintTable.set(m, 12, new Object[] { "B: " + (localAuthorityInfo.backoffTime - l1) / 1000L });
              i = k + 1;
              localPrintTable.set(k, 12, new Object[] { Long.valueOf(localAuthorityInfo.backoffDelay / 1000L) });
            }
          }
          k = i;
          if (localSyncStatusInfo.lastSuccessTime != 0L)
          {
            m = i + 1;
            localPrintTable.set(i, 11, new Object[] { SyncStorageEngine.SOURCES[localSyncStatusInfo.lastSuccessSource] + " " + "SUCCESS" });
            k = m + 1;
            localPrintTable.set(m, 11, new Object[] { formatTime(localSyncStatusInfo.lastSuccessTime) });
          }
          if (localSyncStatusInfo.lastFailureTime != 0L)
          {
            i = k + 1;
            localPrintTable.set(k, 11, new Object[] { SyncStorageEngine.SOURCES[localSyncStatusInfo.lastFailureSource] + " " + "FAILURE" });
            k = i + 1;
            localPrintTable.set(i, 11, new Object[] { formatTime(localSyncStatusInfo.lastFailureTime) });
            localPrintTable.set(k, 11, new Object[] { localSyncStatusInfo.lastFailureMesg });
          }
        }
      }
      localPrintTable.writeTo(paramPrintWriter);
      j += 1;
    }
  }
  
  public List<PeriodicSync> getPeriodicSyncs(SyncStorageEngine.EndPoint paramEndPoint)
  {
    Object localObject = getAllPendingSyncs();
    ArrayList localArrayList = new ArrayList();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SyncOperation localSyncOperation = (SyncOperation)((Iterator)localObject).next();
      if ((localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint))) {
        localArrayList.add(new PeriodicSync(localSyncOperation.target.account, localSyncOperation.target.provider, localSyncOperation.extras, localSyncOperation.periodMillis / 1000L, localSyncOperation.flexMillis / 1000L));
      }
    }
    return localArrayList;
  }
  
  public String[] getSyncAdapterPackagesForAuthorityAsUser(String paramString, int paramInt)
  {
    return this.mSyncAdapters.getSyncAdapterPackagesForAuthority(paramString, paramInt);
  }
  
  public SyncAdapterType[] getSyncAdapterTypes(int paramInt)
  {
    Object localObject = this.mSyncAdapters.getAllServices(paramInt);
    SyncAdapterType[] arrayOfSyncAdapterType = new SyncAdapterType[((Collection)localObject).size()];
    paramInt = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      arrayOfSyncAdapterType[paramInt] = ((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)((Iterator)localObject).next()).type);
      paramInt += 1;
    }
    return arrayOfSyncAdapterType;
  }
  
  public SyncStorageEngine getSyncStorageEngine()
  {
    return this.mSyncStorageEngine;
  }
  
  public void removePeriodicSync(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
  {
    paramEndPoint = this.mSyncHandler.obtainMessage(14, paramEndPoint);
    paramEndPoint.setData(paramBundle);
    paramEndPoint.sendToTarget();
  }
  
  public void scheduleLocalSync(Account paramAccount, int paramInt1, int paramInt2, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("upload", true);
    scheduleSync(paramAccount, paramInt1, paramInt2, paramString, localBundle, -2, LOCAL_SYNC_DELAY);
  }
  
  public void scheduleSync(Account paramAccount, int paramInt1, int paramInt2, String paramString, Bundle paramBundle, int paramInt3)
  {
    scheduleSync(paramAccount, paramInt1, paramInt2, paramString, paramBundle, paramInt3, 0L);
  }
  
  public void updateOrAddPeriodicSync(SyncStorageEngine.EndPoint paramEndPoint, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    paramEndPoint = new UpdatePeriodicSyncMessagePayload(paramEndPoint, paramLong1, paramLong2, paramBundle);
    this.mSyncHandler.obtainMessage(13, paramEndPoint).sendToTarget();
  }
  
  private static class AccountSyncStats
  {
    long elapsedTime;
    String name;
    int times;
    
    private AccountSyncStats(String paramString)
    {
      this.name = paramString;
    }
  }
  
  class ActiveSyncContext
    extends ISyncContext.Stub
    implements ServiceConnection, IBinder.DeathRecipient
  {
    boolean mBound;
    long mBytesTransferredAtLastPoll;
    String mEventName;
    final long mHistoryRowId;
    boolean mIsLinkedToDeath = false;
    long mLastPolledTimeElapsed;
    final long mStartTime;
    ISyncAdapter mSyncAdapter;
    final int mSyncAdapterUid;
    SyncInfo mSyncInfo;
    final SyncOperation mSyncOperation;
    final PowerManager.WakeLock mSyncWakeLock;
    long mTimeoutStartTime;
    
    public ActiveSyncContext(SyncOperation paramSyncOperation, long paramLong, int paramInt)
    {
      this.mSyncAdapterUid = paramInt;
      this.mSyncOperation = paramSyncOperation;
      this.mHistoryRowId = paramLong;
      this.mSyncAdapter = null;
      this.mStartTime = SystemClock.elapsedRealtime();
      this.mTimeoutStartTime = this.mStartTime;
      this.mSyncWakeLock = SyncManager.SyncHandler.-wrap0(SyncManager.-get13(SyncManager.this), this.mSyncOperation);
      this.mSyncWakeLock.setWorkSource(new WorkSource(paramInt));
      this.mSyncWakeLock.acquire();
    }
    
    boolean bindToSyncAdapter(ComponentName paramComponentName, int paramInt)
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Log.d("SyncManager", "bindToSyncAdapter: " + paramComponentName + ", connection " + this);
      }
      Intent localIntent = new Intent();
      localIntent.setAction("android.content.SyncAdapter");
      localIntent.setComponent(paramComponentName);
      localIntent.putExtra("android.intent.extra.client_label", 17040513);
      localIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivityAsUser(SyncManager.-get3(SyncManager.this), 0, new Intent("android.settings.SYNC_SETTINGS"), 0, null, new UserHandle(paramInt)));
      this.mBound = true;
      boolean bool = SyncManager.-get3(SyncManager.this).bindServiceAsUser(localIntent, this, 21, new UserHandle(this.mSyncOperation.target.userId));
      if (!bool)
      {
        this.mBound = false;
        return bool;
      }
      try
      {
        this.mEventName = this.mSyncOperation.wakeLockName();
        SyncManager.-get1(SyncManager.this).noteSyncStart(this.mEventName, this.mSyncAdapterUid);
        return bool;
      }
      catch (RemoteException paramComponentName) {}
      return bool;
    }
    
    public void binderDied()
    {
      SyncManager.-wrap24(SyncManager.this, this, null);
    }
    
    protected void close()
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Log.d("SyncManager", "unBindFromSyncAdapter: connection " + this);
      }
      if (this.mBound)
      {
        this.mBound = false;
        SyncManager.-get3(SyncManager.this).unbindService(this);
      }
      try
      {
        SyncManager.-get1(SyncManager.this).noteSyncFinish(this.mEventName, this.mSyncAdapterUid);
        this.mSyncWakeLock.release();
        this.mSyncWakeLock.setWorkSource(null);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
    
    public void onFinished(SyncResult paramSyncResult)
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "onFinished: " + this);
      }
      SyncManager.-wrap24(SyncManager.this, this, paramSyncResult);
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      paramComponentName = SyncManager.-get13(SyncManager.this).obtainMessage();
      paramComponentName.what = 4;
      paramComponentName.obj = new SyncManager.ServiceConnectionData(SyncManager.this, this, paramIBinder);
      SyncManager.-get13(SyncManager.this).sendMessage(paramComponentName);
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      paramComponentName = SyncManager.-get13(SyncManager.this).obtainMessage();
      paramComponentName.what = 5;
      paramComponentName.obj = new SyncManager.ServiceConnectionData(SyncManager.this, this, null);
      SyncManager.-get13(SyncManager.this).sendMessage(paramComponentName);
    }
    
    public void sendHeartbeat() {}
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      toString(localStringBuilder);
      return localStringBuilder.toString();
    }
    
    public void toString(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append("startTime ").append(this.mStartTime).append(", mTimeoutStartTime ").append(this.mTimeoutStartTime).append(", mHistoryRowId ").append(this.mHistoryRowId).append(", syncOperation ").append(this.mSyncOperation);
    }
  }
  
  private static class AuthoritySyncStats
  {
    Map<String, SyncManager.AccountSyncStats> accountMap = Maps.newHashMap();
    long elapsedTime;
    String name;
    int times;
    
    private AuthoritySyncStats(String paramString)
    {
      this.name = paramString;
    }
  }
  
  static class PrintTable
  {
    private final int mCols;
    private ArrayList<Object[]> mTable = Lists.newArrayList();
    
    PrintTable(int paramInt)
    {
      this.mCols = paramInt;
    }
    
    private void printRow(PrintWriter paramPrintWriter, String[] paramArrayOfString, Object[] paramArrayOfObject)
    {
      int i = 0;
      int j = paramArrayOfObject.length;
      while (i < j)
      {
        paramPrintWriter.printf(String.format(paramArrayOfString[i], new Object[] { paramArrayOfObject[i].toString() }), new Object[0]);
        paramPrintWriter.print("  ");
        i += 1;
      }
      paramPrintWriter.println();
    }
    
    public int getNumRows()
    {
      return this.mTable.size();
    }
    
    void set(int paramInt1, int paramInt2, Object... paramVarArgs)
    {
      if (paramVarArgs.length + paramInt2 > this.mCols) {
        throw new IndexOutOfBoundsException("Table only has " + this.mCols + " columns. can't set " + paramVarArgs.length + " at column " + paramInt2);
      }
      int i = this.mTable.size();
      while (i <= paramInt1)
      {
        Object[] arrayOfObject = new Object[this.mCols];
        this.mTable.add(arrayOfObject);
        int j = 0;
        while (j < this.mCols)
        {
          arrayOfObject[j] = "";
          j += 1;
        }
        i += 1;
      }
      System.arraycopy(paramVarArgs, 0, this.mTable.get(paramInt1), paramInt2, paramVarArgs.length);
    }
    
    void writeTo(PrintWriter paramPrintWriter)
    {
      String[] arrayOfString = new String[this.mCols];
      int i = 0;
      int j = 0;
      while (j < this.mCols)
      {
        k = 0;
        Iterator localIterator = this.mTable.iterator();
        while (localIterator.hasNext())
        {
          int m = ((Object[])localIterator.next())[j].toString().length();
          if (m > k) {
            k = m;
          }
        }
        i += k;
        arrayOfString[j] = String.format("%%-%ds", new Object[] { Integer.valueOf(k) });
        j += 1;
      }
      arrayOfString[(this.mCols - 1)] = "%s";
      printRow(paramPrintWriter, arrayOfString, (Object[])this.mTable.get(0));
      int k = this.mCols;
      j = 0;
      while (j < i + (k - 1) * 2)
      {
        paramPrintWriter.print("-");
        j += 1;
      }
      paramPrintWriter.println();
      i = 1;
      j = this.mTable.size();
      while (i < j)
      {
        printRow(paramPrintWriter, arrayOfString, (Object[])this.mTable.get(i));
        i += 1;
      }
    }
  }
  
  private static class ScheduleSyncMessagePayload
  {
    final long minDelayMillis;
    final SyncOperation syncOperation;
    
    ScheduleSyncMessagePayload(SyncOperation paramSyncOperation, long paramLong)
    {
      this.syncOperation = paramSyncOperation;
      this.minDelayMillis = paramLong;
    }
  }
  
  class ServiceConnectionData
  {
    public final SyncManager.ActiveSyncContext activeSyncContext;
    public final IBinder adapter;
    
    ServiceConnectionData(SyncManager.ActiveSyncContext paramActiveSyncContext, IBinder paramIBinder)
    {
      this.activeSyncContext = paramActiveSyncContext;
      this.adapter = paramIBinder;
    }
  }
  
  private class SyncFinishedOrCancelledMessagePayload
  {
    public final SyncManager.ActiveSyncContext activeSyncContext;
    public final SyncResult syncResult;
    
    SyncFinishedOrCancelledMessagePayload(SyncManager.ActiveSyncContext paramActiveSyncContext, SyncResult paramSyncResult)
    {
      this.activeSyncContext = paramActiveSyncContext;
      this.syncResult = paramSyncResult;
    }
  }
  
  class SyncHandler
    extends Handler
  {
    private static final int MESSAGE_ACCOUNTS_UPDATED = 9;
    private static final int MESSAGE_CANCEL = 6;
    static final int MESSAGE_JOBSERVICE_OBJECT = 7;
    private static final int MESSAGE_MONITOR_SYNC = 8;
    private static final int MESSAGE_RELEASE_MESSAGES_FROM_QUEUE = 2;
    static final int MESSAGE_REMOVE_PERIODIC_SYNC = 14;
    static final int MESSAGE_SCHEDULE_SYNC = 12;
    private static final int MESSAGE_SERVICE_CONNECTED = 4;
    private static final int MESSAGE_SERVICE_DISCONNECTED = 5;
    static final int MESSAGE_START_SYNC = 10;
    static final int MESSAGE_STOP_SYNC = 11;
    private static final int MESSAGE_SYNC_FINISHED = 1;
    static final int MESSAGE_UPDATE_PERIODIC_SYNC = 13;
    public final SyncManager.SyncTimeTracker mSyncTimeTracker = new SyncManager.SyncTimeTracker(SyncManager.this, null);
    private List<Message> mUnreadyQueue = new ArrayList();
    private final HashMap<String, PowerManager.WakeLock> mWakeLocks = Maps.newHashMap();
    
    public SyncHandler(Looper paramLooper)
    {
      super();
    }
    
    private void cancelActiveSyncH(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
    {
      Iterator localIterator = new ArrayList(SyncManager.this.mActiveSyncContexts).iterator();
      while (localIterator.hasNext())
      {
        SyncManager.ActiveSyncContext localActiveSyncContext = (SyncManager.ActiveSyncContext)localIterator.next();
        if ((localActiveSyncContext != null) && (localActiveSyncContext.mSyncOperation.target.matchesSpec(paramEndPoint)) && ((paramBundle == null) || (SyncManager.syncExtrasEquals(localActiveSyncContext.mSyncOperation.extras, paramBundle, false))))
        {
          SyncManager.-get14(SyncManager.this).callJobFinished(localActiveSyncContext.mSyncOperation.jobId, false);
          runSyncFinishedOrCanceledH(null, localActiveSyncContext);
        }
      }
    }
    
    private void closeActiveSyncContext(SyncManager.ActiveSyncContext paramActiveSyncContext)
    {
      paramActiveSyncContext.close();
      SyncManager.this.mActiveSyncContexts.remove(paramActiveSyncContext);
      SyncManager.-get16(SyncManager.this).removeActiveSync(paramActiveSyncContext.mSyncInfo, paramActiveSyncContext.mSyncOperation.target.userId);
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "removing all MESSAGE_MONITOR_SYNC & MESSAGE_SYNC_EXPIRED for " + paramActiveSyncContext.toString());
      }
      SyncManager.-get13(SyncManager.this).removeMessages(8, paramActiveSyncContext);
    }
    
    private int computeSyncOpState(SyncOperation paramSyncOperation)
    {
      boolean bool2 = Log.isLoggable("SyncManager", 2);
      SyncStorageEngine.EndPoint localEndPoint = paramSyncOperation.target;
      AccountAndUser[] arrayOfAccountAndUser = SyncManager.-get11(SyncManager.this);
      if (!SyncManager.-wrap2(SyncManager.this, arrayOfAccountAndUser, localEndPoint.account, localEndPoint.userId))
      {
        if (bool2) {
          Slog.v("SyncManager", "    Dropping sync operation: account doesn't exist.");
        }
        return 1;
      }
      int i = SyncManager.-wrap7(SyncManager.this, localEndPoint.account, localEndPoint.userId, localEndPoint.provider);
      if (i == 3)
      {
        if (bool2) {
          Slog.v("SyncManager", "    Dropping sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS");
        }
        return 2;
      }
      if (i == 0)
      {
        if (bool2) {
          Slog.v("SyncManager", "    Dropping sync operation: isSyncable == NOT_SYNCABLE");
        }
        return 1;
      }
      boolean bool1;
      if (SyncManager.-get16(SyncManager.this).getMasterSyncAutomatically(localEndPoint.userId))
      {
        bool1 = SyncManager.-get16(SyncManager.this).getSyncAutomatically(localEndPoint.account, localEndPoint.userId, localEndPoint.provider);
        if ((!paramSyncOperation.isIgnoreSettings()) && (i >= 0)) {
          break label194;
        }
      }
      label194:
      for (i = 1;; i = 0)
      {
        if ((!bool1) && (i == 0)) {
          break label199;
        }
        return 0;
        bool1 = false;
        break;
      }
      label199:
      if (bool2) {
        Slog.v("SyncManager", "    Dropping sync operation: disallowed by settings/network.");
      }
      return 1;
    }
    
    private void deferActiveSyncH(SyncManager.ActiveSyncContext paramActiveSyncContext)
    {
      SyncOperation localSyncOperation = paramActiveSyncContext.mSyncOperation;
      runSyncFinishedOrCanceledH(null, paramActiveSyncContext);
      deferSyncH(localSyncOperation, 10000L);
    }
    
    private void deferStoppedSyncH(SyncOperation paramSyncOperation, long paramLong)
    {
      if (paramSyncOperation.isPeriodic)
      {
        SyncManager.-wrap23(SyncManager.this, paramSyncOperation.createOneTimeSyncOperation(), paramLong);
        return;
      }
      SyncManager.-wrap23(SyncManager.this, paramSyncOperation, paramLong);
    }
    
    private void deferSyncH(SyncOperation paramSyncOperation, long paramLong)
    {
      SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
      if (paramSyncOperation.isPeriodic)
      {
        SyncManager.-wrap23(SyncManager.this, paramSyncOperation.createOneTimeSyncOperation(), paramLong);
        return;
      }
      SyncManager.-wrap0(SyncManager.this).cancel(paramSyncOperation.jobId);
      SyncManager.-wrap23(SyncManager.this, paramSyncOperation, paramLong);
    }
    
    private boolean dispatchSyncOperation(SyncOperation paramSyncOperation)
    {
      if (Log.isLoggable("SyncManager", 2))
      {
        Slog.v("SyncManager", "dispatchSyncOperation: we are going to sync " + paramSyncOperation);
        Slog.v("SyncManager", "num active syncs: " + SyncManager.this.mActiveSyncContexts.size());
        localObject1 = SyncManager.this.mActiveSyncContexts.iterator();
        while (((Iterator)localObject1).hasNext()) {
          Slog.v("SyncManager", ((SyncManager.ActiveSyncContext)((Iterator)localObject1).next()).toString());
        }
      }
      Object localObject1 = paramSyncOperation.target;
      Object localObject2 = SyncAdapterType.newKey(((SyncStorageEngine.EndPoint)localObject1).provider, ((SyncStorageEngine.EndPoint)localObject1).account.type);
      RegisteredServicesCache.ServiceInfo localServiceInfo = SyncManager.this.mSyncAdapters.getServiceInfo(localObject2, ((SyncStorageEngine.EndPoint)localObject1).userId);
      if (localServiceInfo == null)
      {
        Log.d("SyncManager", "can't find a sync adapter for " + localObject2 + ", removing settings for it");
        SyncManager.-get16(SyncManager.this).removeAuthority((SyncStorageEngine.EndPoint)localObject1);
        return false;
      }
      int i = localServiceInfo.uid;
      localObject2 = localServiceInfo.componentName;
      paramSyncOperation = new SyncManager.ActiveSyncContext(SyncManager.this, paramSyncOperation, insertStartSyncEvent(paramSyncOperation), i);
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "dispatchSyncOperation: starting " + paramSyncOperation);
      }
      paramSyncOperation.mSyncInfo = SyncManager.-get16(SyncManager.this).addActiveSync(paramSyncOperation);
      SyncManager.this.mActiveSyncContexts.add(paramSyncOperation);
      SyncManager.-wrap18(SyncManager.this, paramSyncOperation);
      if (!paramSyncOperation.bindToSyncAdapter((ComponentName)localObject2, ((SyncStorageEngine.EndPoint)localObject1).userId))
      {
        Slog.e("SyncManager", "Bind attempt failed - target: " + localObject2);
        closeActiveSyncContext(paramSyncOperation);
        return false;
      }
      return true;
    }
    
    private SyncManager.ActiveSyncContext findActiveSyncContextH(int paramInt)
    {
      Iterator localIterator = SyncManager.this.mActiveSyncContexts.iterator();
      while (localIterator.hasNext())
      {
        SyncManager.ActiveSyncContext localActiveSyncContext = (SyncManager.ActiveSyncContext)localIterator.next();
        SyncOperation localSyncOperation = localActiveSyncContext.mSyncOperation;
        if ((localSyncOperation != null) && (localSyncOperation.jobId == paramInt)) {
          return localActiveSyncContext;
        }
      }
      return null;
    }
    
    private PowerManager.WakeLock getSyncWakeLock(SyncOperation paramSyncOperation)
    {
      String str = paramSyncOperation.wakeLockName();
      PowerManager.WakeLock localWakeLock = (PowerManager.WakeLock)this.mWakeLocks.get(str);
      paramSyncOperation = localWakeLock;
      if (localWakeLock == null)
      {
        paramSyncOperation = "*sync*/" + str;
        paramSyncOperation = SyncManager.-get9(SyncManager.this).newWakeLock(1, paramSyncOperation);
        paramSyncOperation.setReferenceCounted(false);
        this.mWakeLocks.put(str, paramSyncOperation);
      }
      return paramSyncOperation;
    }
    
    private void handleSyncMessage(Message paramMessage)
    {
      boolean bool3 = Log.isLoggable("SyncManager", 2);
      Object localObject1;
      Object localObject2;
      do
      {
        for (;;)
        {
          try
          {
            SyncManager.-set1(SyncManager.this, SyncManager.-wrap6(SyncManager.this));
            int i = paramMessage.what;
            switch (i)
            {
            case 2: 
            case 3: 
            case 7: 
            case 9: 
            default: 
              return;
            }
          }
          finally
          {
            this.mSyncTimeTracker.update();
          }
          paramMessage = (SyncManager.ScheduleSyncMessagePayload)paramMessage.obj;
          localObject1 = paramMessage.syncOperation;
          SyncManager.-wrap23(SyncManager.this, (SyncOperation)localObject1, paramMessage.minDelayMillis);
          continue;
          startSyncH((SyncOperation)paramMessage.obj);
        }
        localObject1 = (SyncOperation)paramMessage.obj;
        if (bool3) {
          Slog.v("SyncManager", "Stop sync received.");
        }
        localObject2 = findActiveSyncContextH(((SyncOperation)localObject1).jobId);
      } while (localObject2 == null);
      runSyncFinishedOrCanceledH(null, (SyncManager.ActiveSyncContext)localObject2);
      boolean bool1;
      if (paramMessage.arg1 != 0)
      {
        bool1 = true;
        label217:
        if (paramMessage.arg2 == 0) {
          break label878;
        }
      }
      label878:
      for (boolean bool2 = true;; bool2 = false)
      {
        if (bool3) {
          Slog.v("SyncManager", "Stopping sync. Reschedule: " + bool1 + "Backoff: " + bool2);
        }
        if (bool2) {
          SyncManager.-wrap13(SyncManager.this, ((SyncOperation)localObject1).target);
        }
        if (!bool1) {
          break;
        }
        deferStoppedSyncH((SyncOperation)localObject1, 0L);
        break;
        paramMessage = (SyncManager.UpdatePeriodicSyncMessagePayload)paramMessage.obj;
        updateOrAddPeriodicSyncH(paramMessage.target, paramMessage.pollFrequency, paramMessage.flex, paramMessage.extras);
        break;
        removePeriodicSyncH((SyncStorageEngine.EndPoint)paramMessage.obj, paramMessage.getData());
        break;
        localObject1 = (SyncStorageEngine.EndPoint)paramMessage.obj;
        paramMessage = paramMessage.peekData();
        if (Log.isLoggable("SyncManager", 3)) {
          Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_CANCEL: " + localObject1 + " bundle: " + paramMessage);
        }
        cancelActiveSyncH((SyncStorageEngine.EndPoint)localObject1, paramMessage);
        break;
        paramMessage = (SyncManager.SyncFinishedOrCancelledMessagePayload)paramMessage.obj;
        if (!SyncManager.-wrap5(SyncManager.this, paramMessage.activeSyncContext))
        {
          Log.d("SyncManager", "handleSyncHandlerMessage: dropping since the sync is no longer active: " + paramMessage.activeSyncContext);
          break;
        }
        if (bool3) {
          Slog.v("SyncManager", "syncFinished" + paramMessage.activeSyncContext.mSyncOperation);
        }
        SyncManager.-get14(SyncManager.this).callJobFinished(paramMessage.activeSyncContext.mSyncOperation.jobId, false);
        runSyncFinishedOrCanceledH(paramMessage.syncResult, paramMessage.activeSyncContext);
        break;
        paramMessage = (SyncManager.ServiceConnectionData)paramMessage.obj;
        if (Log.isLoggable("SyncManager", 2)) {
          Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_CONNECTED: " + paramMessage.activeSyncContext);
        }
        if (!SyncManager.-wrap5(SyncManager.this, paramMessage.activeSyncContext)) {
          break;
        }
        runBoundToAdapterH(paramMessage.activeSyncContext, paramMessage.adapter);
        break;
        paramMessage = ((SyncManager.ServiceConnectionData)paramMessage.obj).activeSyncContext;
        if (Log.isLoggable("SyncManager", 2)) {
          Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_DISCONNECTED: " + paramMessage);
        }
        bool1 = SyncManager.-wrap5(SyncManager.this, paramMessage);
        if (!bool1) {
          break;
        }
        try
        {
          if (paramMessage.mSyncAdapter != null) {
            paramMessage.mSyncAdapter.cancelSync(paramMessage);
          }
          localObject1 = new SyncResult();
          localObject2 = ((SyncResult)localObject1).stats;
          ((SyncStats)localObject2).numIoExceptions += 1L;
          SyncManager.-get14(SyncManager.this).callJobFinished(paramMessage.mSyncOperation.jobId, false);
          runSyncFinishedOrCanceledH((SyncResult)localObject1, paramMessage);
          break;
          paramMessage = (SyncManager.ActiveSyncContext)paramMessage.obj;
          if (Log.isLoggable("SyncManager", 3)) {
            Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_MONITOR_SYNC: " + paramMessage.mSyncOperation.target);
          }
          if (isSyncNotUsingNetworkH(paramMessage))
          {
            Log.w("SyncManager", String.format("Detected sync making no progress for %s. cancelling.", new Object[] { paramMessage }));
            SyncManager.-get14(SyncManager.this).callJobFinished(paramMessage.mSyncOperation.jobId, false);
            runSyncFinishedOrCanceledH(null, paramMessage);
            break;
          }
          SyncManager.-wrap18(SyncManager.this, paramMessage);
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
        bool1 = false;
        break label217;
      }
    }
    
    private void installHandleTooManyDeletesNotification(Account paramAccount, String paramString, long paramLong, int paramInt)
    {
      if (SyncManager.-get7(SyncManager.this) == null) {
        return;
      }
      Object localObject1 = SyncManager.-get3(SyncManager.this).getPackageManager().resolveContentProvider(paramString, 0);
      if (localObject1 == null) {
        return;
      }
      Object localObject2 = ((ProviderInfo)localObject1).loadLabel(SyncManager.-get3(SyncManager.this).getPackageManager());
      Object localObject3 = new Intent(SyncManager.-get3(SyncManager.this), SyncActivityTooManyDeletes.class);
      ((Intent)localObject3).putExtra("account", paramAccount);
      ((Intent)localObject3).putExtra("authority", paramString);
      ((Intent)localObject3).putExtra("provider", ((CharSequence)localObject2).toString());
      ((Intent)localObject3).putExtra("numDeletes", paramLong);
      if (!isActivityAvailable((Intent)localObject3))
      {
        Log.w("SyncManager", "No activity found to handle too many deletes.");
        return;
      }
      localObject1 = new UserHandle(paramInt);
      localObject3 = PendingIntent.getActivityAsUser(SyncManager.-get3(SyncManager.this), 0, (Intent)localObject3, 268435456, null, (UserHandle)localObject1);
      CharSequence localCharSequence = SyncManager.-get3(SyncManager.this).getResources().getText(17039638);
      Context localContext = SyncManager.-wrap1(SyncManager.this, (UserHandle)localObject1);
      localObject2 = new Notification.Builder(localContext).setSmallIcon(17303261).setTicker(SyncManager.-get3(SyncManager.this).getString(17039636)).setWhen(System.currentTimeMillis()).setColor(localContext.getColor(17170523)).setContentTitle(localContext.getString(17039637)).setContentText(String.format(localCharSequence.toString(), new Object[] { localObject2 })).setContentIntent((PendingIntent)localObject3).build();
      ((Notification)localObject2).flags |= 0x2;
      SyncManager.-get7(SyncManager.this).notifyAsUser(null, paramAccount.hashCode() ^ paramString.hashCode(), (Notification)localObject2, (UserHandle)localObject1);
    }
    
    private boolean isActivityAvailable(Intent paramIntent)
    {
      paramIntent = SyncManager.-get3(SyncManager.this).getPackageManager().queryIntentActivities(paramIntent, 0);
      int j = paramIntent.size();
      int i = 0;
      while (i < j)
      {
        if ((((ResolveInfo)paramIntent.get(i)).activityInfo.applicationInfo.flags & 0x1) != 0) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    private boolean isSyncNotUsingNetworkH(SyncManager.ActiveSyncContext paramActiveSyncContext)
    {
      long l1 = SyncManager.-wrap9(SyncManager.this, paramActiveSyncContext.mSyncAdapterUid) - paramActiveSyncContext.mBytesTransferredAtLastPoll;
      if (Log.isLoggable("SyncManager", 3))
      {
        long l2 = l1 / 1048576L;
        long l3 = l1 % 1048576L;
        long l4 = l3 / 1024L;
        Log.d("SyncManager", String.format("Time since last update: %ds. Delta transferred: %dMBs,%dKBs,%dBs", new Object[] { Long.valueOf((SystemClock.elapsedRealtime() - paramActiveSyncContext.mLastPolledTimeElapsed) / 1000L), Long.valueOf(l2), Long.valueOf(l4), Long.valueOf(l3 % 1024L) }));
      }
      return l1 <= 10L;
    }
    
    private void maybeUpdateSyncPeriodH(SyncOperation paramSyncOperation, long paramLong1, long paramLong2)
    {
      if ((paramLong1 != paramSyncOperation.periodMillis) || (paramLong2 != paramSyncOperation.flexMillis))
      {
        if (Log.isLoggable("SyncManager", 2)) {
          Slog.v("SyncManager", "updating period " + paramSyncOperation + " to " + paramLong1 + " and flex to " + paramLong2);
        }
        SyncOperation localSyncOperation = new SyncOperation(paramSyncOperation, paramLong1, paramLong2);
        localSyncOperation.jobId = paramSyncOperation.jobId;
        SyncManager.-wrap22(SyncManager.this, localSyncOperation);
      }
    }
    
    private void removePeriodicSyncH(SyncStorageEngine.EndPoint paramEndPoint, Bundle paramBundle)
    {
      SyncManager.-wrap28(SyncManager.this);
      Iterator localIterator = SyncManager.-wrap8(SyncManager.this).iterator();
      while (localIterator.hasNext())
      {
        SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
        if ((localSyncOperation.isPeriodic) && (localSyncOperation.target.matchesSpec(paramEndPoint)) && (SyncManager.syncExtrasEquals(localSyncOperation.extras, paramBundle, true))) {
          removePeriodicSyncInternalH(localSyncOperation);
        }
      }
    }
    
    private void removePeriodicSyncInternalH(SyncOperation paramSyncOperation)
    {
      Iterator localIterator = SyncManager.-wrap8(SyncManager.this).iterator();
      while (localIterator.hasNext())
      {
        SyncOperation localSyncOperation = (SyncOperation)localIterator.next();
        if ((localSyncOperation.sourcePeriodicId == paramSyncOperation.jobId) || (localSyncOperation.jobId == paramSyncOperation.jobId))
        {
          SyncManager.ActiveSyncContext localActiveSyncContext = findActiveSyncContextH(paramSyncOperation.jobId);
          if (localActiveSyncContext != null)
          {
            SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
            runSyncFinishedOrCanceledH(null, localActiveSyncContext);
          }
          SyncManager.-wrap0(SyncManager.this).cancel(localSyncOperation.jobId);
        }
      }
    }
    
    private void reschedulePeriodicSyncH(SyncOperation paramSyncOperation)
    {
      Object localObject2 = null;
      Iterator localIterator = SyncManager.-wrap8(SyncManager.this).iterator();
      Object localObject1;
      do
      {
        localObject1 = localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = (SyncOperation)localIterator.next();
      } while ((!((SyncOperation)localObject1).isPeriodic) || (!paramSyncOperation.matchesPeriodicOperation((SyncOperation)localObject1)));
      if (localObject1 == null) {
        return;
      }
      SyncManager.-wrap22(SyncManager.this, (SyncOperation)localObject1);
    }
    
    private void runBoundToAdapterH(SyncManager.ActiveSyncContext paramActiveSyncContext, IBinder paramIBinder)
    {
      SyncOperation localSyncOperation = paramActiveSyncContext.mSyncOperation;
      try
      {
        paramActiveSyncContext.mIsLinkedToDeath = true;
        paramIBinder.linkToDeath(paramActiveSyncContext, 0);
        paramActiveSyncContext.mSyncAdapter = ISyncAdapter.Stub.asInterface(paramIBinder);
        paramActiveSyncContext.mSyncAdapter.startSync(paramActiveSyncContext, localSyncOperation.target.provider, localSyncOperation.target.account, localSyncOperation.extras);
        return;
      }
      catch (RuntimeException paramIBinder)
      {
        closeActiveSyncContext(paramActiveSyncContext);
        Slog.e("SyncManager", "Caught RuntimeException while starting the sync " + localSyncOperation, paramIBinder);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        Log.d("SyncManager", "maybeStartNextSync: caught a RemoteException, rescheduling", paramIBinder);
        closeActiveSyncContext(paramActiveSyncContext);
        SyncManager.-wrap13(SyncManager.this, localSyncOperation.target);
        SyncManager.-wrap22(SyncManager.this, localSyncOperation);
      }
    }
    
    private void runSyncFinishedOrCanceledH(SyncResult paramSyncResult, SyncManager.ActiveSyncContext paramActiveSyncContext)
    {
      boolean bool = Log.isLoggable("SyncManager", 2);
      SyncOperation localSyncOperation = paramActiveSyncContext.mSyncOperation;
      SyncStorageEngine.EndPoint localEndPoint = localSyncOperation.target;
      if (paramActiveSyncContext.mIsLinkedToDeath)
      {
        paramActiveSyncContext.mSyncAdapter.asBinder().unlinkToDeath(paramActiveSyncContext, 0);
        paramActiveSyncContext.mIsLinkedToDeath = false;
      }
      closeActiveSyncContext(paramActiveSyncContext);
      long l1 = SystemClock.elapsedRealtime();
      long l2 = paramActiveSyncContext.mStartTime;
      if (!localSyncOperation.isPeriodic) {
        SyncManager.-wrap0(SyncManager.this).cancel(localSyncOperation.jobId);
      }
      String str1;
      if (paramSyncResult != null)
      {
        if (bool) {
          Slog.v("SyncManager", "runSyncFinishedOrCanceled [finished]: " + localSyncOperation + ", result " + paramSyncResult);
        }
        if (!paramSyncResult.hasError())
        {
          String str2 = "success";
          SyncManager.-wrap11(SyncManager.this, localSyncOperation.target);
          str1 = str2;
          if (localSyncOperation.isDerivedFromFailedPeriodicSync())
          {
            reschedulePeriodicSyncH(localSyncOperation);
            str1 = str2;
          }
          SyncManager.-wrap26(SyncManager.this, localSyncOperation.target, paramSyncResult.delayUntil);
        }
      }
      for (;;)
      {
        stopSyncEvent(paramActiveSyncContext.mHistoryRowId, localSyncOperation, str1, 0, 0, l1 - l2);
        if ((paramSyncResult != null) && (paramSyncResult.tooManyDeletions))
        {
          installHandleTooManyDeletesNotification(localEndPoint.account, localEndPoint.provider, paramSyncResult.stats.numDeletes, localEndPoint.userId);
          if ((paramSyncResult != null) && (paramSyncResult.fullSyncRequested)) {
            SyncManager.-wrap22(SyncManager.this, new SyncOperation(localEndPoint.account, localEndPoint.userId, localSyncOperation.owningUid, localSyncOperation.owningPackage, localSyncOperation.reason, localSyncOperation.syncSource, localEndPoint.provider, new Bundle(), localSyncOperation.allowParallelSyncs));
          }
          return;
          Log.d("SyncManager", "failed sync operation " + localSyncOperation + ", " + paramSyncResult);
          SyncManager.-wrap13(SyncManager.this, localSyncOperation.target);
          if (!localSyncOperation.isPeriodic) {
            SyncManager.-wrap14(SyncManager.this, paramSyncResult, localSyncOperation);
          }
          for (;;)
          {
            str1 = ContentResolver.syncErrorToString(syncResultToErrorNumber(paramSyncResult));
            break;
            SyncManager.-wrap19(SyncManager.this, localSyncOperation.createOneTimeSyncOperation(), 0L);
          }
          if (bool) {
            Slog.v("SyncManager", "runSyncFinishedOrCanceled [canceled]: " + localSyncOperation);
          }
          if (paramActiveSyncContext.mSyncAdapter == null) {}
        }
        try
        {
          paramActiveSyncContext.mSyncAdapter.cancelSync(paramActiveSyncContext);
          str1 = "canceled";
          continue;
          SyncManager.-get7(SyncManager.this).cancelAsUser(null, localEndPoint.account.hashCode() ^ localEndPoint.provider.hashCode(), new UserHandle(localEndPoint.userId));
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void startSyncH(SyncOperation paramSyncOperation)
    {
      boolean bool = Log.isLoggable("SyncManager", 2);
      if (bool) {
        Slog.v("SyncManager", paramSyncOperation.toString());
      }
      if (SyncManager.-get12(SyncManager.this))
      {
        deferSyncH(paramSyncOperation, 3600000L);
        return;
      }
      if (paramSyncOperation.isPeriodic)
      {
        localIterator = SyncManager.-wrap8(SyncManager.this).iterator();
        while (localIterator.hasNext()) {
          if (((SyncOperation)localIterator.next()).sourcePeriodicId == paramSyncOperation.jobId)
          {
            SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
            return;
          }
        }
        localIterator = SyncManager.this.mActiveSyncContexts.iterator();
        while (localIterator.hasNext()) {
          if (((SyncManager.ActiveSyncContext)localIterator.next()).mSyncOperation.sourcePeriodicId == paramSyncOperation.jobId)
          {
            SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
            return;
          }
        }
        if (SyncManager.-wrap3(SyncManager.this, paramSyncOperation.target))
        {
          deferSyncH(paramSyncOperation, 0L);
          return;
        }
      }
      Iterator localIterator = SyncManager.this.mActiveSyncContexts.iterator();
      while (localIterator.hasNext())
      {
        SyncManager.ActiveSyncContext localActiveSyncContext = (SyncManager.ActiveSyncContext)localIterator.next();
        if (localActiveSyncContext.mSyncOperation.isConflict(paramSyncOperation))
        {
          if (localActiveSyncContext.mSyncOperation.findPriority() >= paramSyncOperation.findPriority())
          {
            if (bool) {
              Slog.v("SyncManager", "Rescheduling sync due to conflict " + paramSyncOperation.toString());
            }
            deferSyncH(paramSyncOperation, 10000L);
            return;
          }
          if (bool) {
            Slog.v("SyncManager", "Pushing back running sync due to a higher priority sync");
          }
          deferActiveSyncH(localActiveSyncContext);
        }
      }
      switch (computeSyncOpState(paramSyncOperation))
      {
      default: 
        if (!dispatchSyncOperation(paramSyncOperation)) {
          SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
        }
        SyncManager.-wrap25(SyncManager.this, paramSyncOperation.target);
        return;
      }
      SyncManager.-get14(SyncManager.this).callJobFinished(paramSyncOperation.jobId, false);
    }
    
    private int syncResultToErrorNumber(SyncResult paramSyncResult)
    {
      if (paramSyncResult.syncAlreadyInProgress) {
        return 1;
      }
      if (paramSyncResult.stats.numAuthExceptions > 0L) {
        return 2;
      }
      if (paramSyncResult.stats.numIoExceptions > 0L) {
        return 3;
      }
      if (paramSyncResult.stats.numParseExceptions > 0L) {
        return 4;
      }
      if (paramSyncResult.stats.numConflictDetectedExceptions > 0L) {
        return 5;
      }
      if (paramSyncResult.tooManyDeletions) {
        return 6;
      }
      if (paramSyncResult.tooManyRetries) {
        return 7;
      }
      if (paramSyncResult.databaseError) {
        return 8;
      }
      throw new IllegalStateException("we are not in an error state, " + paramSyncResult);
    }
    
    private boolean tryEnqueueMessageUntilReadyToRun(Message paramMessage)
    {
      try
      {
        if ((SyncManager.-get2(SyncManager.this)) && (SyncManager.-get10(SyncManager.this)))
        {
          boolean bool = SyncManager.-get6(SyncManager.this);
          if (bool) {
            return false;
          }
        }
        paramMessage = Message.obtain(paramMessage);
        this.mUnreadyQueue.add(paramMessage);
        return true;
      }
      finally {}
    }
    
    private void updateOrAddPeriodicSyncH(SyncStorageEngine.EndPoint paramEndPoint, long paramLong1, long paramLong2, Bundle paramBundle)
    {
      boolean bool = Log.isLoggable("SyncManager", 2);
      SyncManager.-wrap28(SyncManager.this);
      long l1 = paramLong1 * 1000L;
      long l2 = paramLong2 * 1000L;
      if (bool) {
        Slog.v("SyncManager", "Addition to periodic syncs requested: " + paramEndPoint + " period: " + paramLong1 + " flexMillis: " + paramLong2 + " extras: " + paramBundle.toString());
      }
      Object localObject1 = SyncManager.-wrap8(SyncManager.this).iterator();
      Object localObject2;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (SyncOperation)((Iterator)localObject1).next();
        if ((((SyncOperation)localObject2).isPeriodic) && (((SyncOperation)localObject2).target.matchesSpec(paramEndPoint)) && (SyncManager.syncExtrasEquals(((SyncOperation)localObject2).extras, paramBundle, true)))
        {
          maybeUpdateSyncPeriodH((SyncOperation)localObject2, l1, l2);
          return;
        }
      }
      if (bool) {
        Slog.v("SyncManager", "Adding new periodic sync: " + paramEndPoint + " period: " + paramLong1 + " flexMillis: " + paramLong2 + " extras: " + paramBundle.toString());
      }
      localObject1 = SyncManager.this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(paramEndPoint.provider, paramEndPoint.account.type), paramEndPoint.userId);
      if (localObject1 == null) {
        return;
      }
      localObject1 = new SyncOperation(paramEndPoint, ((RegisteredServicesCache.ServiceInfo)localObject1).uid, ((RegisteredServicesCache.ServiceInfo)localObject1).componentName.getPackageName(), -4, 4, paramBundle, ((SyncAdapterType)((RegisteredServicesCache.ServiceInfo)localObject1).type).allowParallelSyncs(), true, -1, l1, l2);
      switch (computeSyncOpState((SyncOperation)localObject1))
      {
      default: 
        SyncManager.-wrap22(SyncManager.this, (SyncOperation)localObject1);
        SyncManager.-get16(SyncManager.this).reportChange(1);
        return;
      case 2: 
        localObject2 = ((SyncOperation)localObject1).owningPackage;
        int i = UserHandle.getUserId(((SyncOperation)localObject1).owningUid);
        if (!SyncManager.-get8(SyncManager.this).wasPackageEverLaunched((String)localObject2, i)) {
          return;
        }
        SyncManager.-get0(SyncManager.this).requestAccountAccess(((SyncOperation)localObject1).target.account, (String)localObject2, i, new RemoteCallback(new -void_updateOrAddPeriodicSyncH_com_android_server_content_SyncStorageEngine.EndPoint_target_long_pollFrequency_long_flex_android_os_Bundle_extras_LambdaImpl0(paramEndPoint, paramLong1, paramLong2, paramBundle)));
        return;
      }
    }
    
    private void updateRunningAccountsH(SyncStorageEngine.EndPoint paramEndPoint)
    {
      AccountAndUser[] arrayOfAccountAndUser = SyncManager.-get11(SyncManager.this);
      SyncManager.-set4(SyncManager.this, AccountManagerService.getSingleton().getRunningAccounts());
      if (Log.isLoggable("SyncManager", 2))
      {
        Slog.v("SyncManager", "Accounts list: ");
        localObject1 = SyncManager.-get11(SyncManager.this);
        i = 0;
        j = localObject1.length;
        while (i < j)
        {
          Slog.v("SyncManager", localObject1[i].toString());
          i += 1;
        }
      }
      if (SyncManager.-get2(SyncManager.this)) {
        SyncManager.-wrap12(SyncManager.this);
      }
      Object localObject1 = SyncManager.-get11(SyncManager.this);
      Object localObject2 = SyncManager.this.mActiveSyncContexts.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        SyncManager.ActiveSyncContext localActiveSyncContext = (SyncManager.ActiveSyncContext)((Iterator)localObject2).next();
        if (!SyncManager.-wrap2(SyncManager.this, (AccountAndUser[])localObject1, localActiveSyncContext.mSyncOperation.target.account, localActiveSyncContext.mSyncOperation.target.userId))
        {
          Log.d("SyncManager", "canceling sync since the account is no longer running");
          SyncManager.-wrap24(SyncManager.this, localActiveSyncContext, null);
        }
      }
      localObject1 = SyncManager.-get11(SyncManager.this);
      int i = 0;
      int j = localObject1.length;
      for (;;)
      {
        if (i < j)
        {
          localObject2 = localObject1[i];
          if (!SyncManager.-wrap2(SyncManager.this, arrayOfAccountAndUser, ((AccountAndUser)localObject2).account, ((AccountAndUser)localObject2).userId))
          {
            if (Log.isLoggable("SyncManager", 3)) {
              Log.d("SyncManager", "Account " + ((AccountAndUser)localObject2).account + " added, checking sync restore data");
            }
            AccountSyncSettingsBackupHelper.accountAdded(SyncManager.-get3(SyncManager.this));
          }
        }
        else
        {
          arrayOfAccountAndUser = AccountManagerService.getSingleton().getAllAccounts();
          localObject1 = SyncManager.-wrap8(SyncManager.this).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (SyncOperation)((Iterator)localObject1).next();
            if (!SyncManager.-wrap2(SyncManager.this, arrayOfAccountAndUser, ((SyncOperation)localObject2).target.account, ((SyncOperation)localObject2).target.userId)) {
              SyncManager.-wrap0(SyncManager.this).cancel(((SyncOperation)localObject2).jobId);
            }
          }
        }
        i += 1;
      }
      if (paramEndPoint != null) {
        SyncManager.this.scheduleSync(paramEndPoint.account, paramEndPoint.userId, -2, paramEndPoint.provider, null, -1);
      }
    }
    
    void checkIfDeviceReady()
    {
      if ((SyncManager.-get10(SyncManager.this)) && (SyncManager.-get2(SyncManager.this)) && (SyncManager.-get6(SyncManager.this))) {}
      try
      {
        SyncManager.-get16(SyncManager.this).restoreAllPeriodicSyncs();
        obtainMessage(2).sendToTarget();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      for (;;)
      {
        try
        {
          SyncManager.-get15(SyncManager.this).acquire();
          if (paramMessage.what == 7)
          {
            Slog.i("SyncManager", "Got SyncJobService instance.");
            SyncManager.-set6(SyncManager.this, (SyncJobService)paramMessage.obj);
            SyncManager.-set2(SyncManager.this, true);
            checkIfDeviceReady();
            return;
          }
          if (paramMessage.what == 9)
          {
            if (Log.isLoggable("SyncManager", 2)) {
              Slog.v("SyncManager", "handleSyncHandlerMessage: MESSAGE_ACCOUNTS_UPDATED");
            }
            updateRunningAccountsH((SyncStorageEngine.EndPoint)paramMessage.obj);
            continue;
          }
          if (paramMessage.what != 2) {
            break label179;
          }
        }
        finally
        {
          SyncManager.-get15(SyncManager.this).release();
        }
        if (this.mUnreadyQueue != null)
        {
          paramMessage = this.mUnreadyQueue.iterator();
          while (paramMessage.hasNext()) {
            handleSyncMessage((Message)paramMessage.next());
          }
          this.mUnreadyQueue = null;
          continue;
          label179:
          if (!tryEnqueueMessageUntilReadyToRun(paramMessage)) {
            handleSyncMessage(paramMessage);
          }
        }
      }
    }
    
    public long insertStartSyncEvent(SyncOperation paramSyncOperation)
    {
      long l = System.currentTimeMillis();
      EventLog.writeEvent(2720, paramSyncOperation.toEventLog(0));
      return SyncManager.-get16(SyncManager.this).insertStartSyncEvent(paramSyncOperation, l);
    }
    
    void onBootCompleted()
    {
      if (Log.isLoggable("SyncManager", 2)) {
        Slog.v("SyncManager", "Boot completed.");
      }
      checkIfDeviceReady();
    }
    
    void onDeviceProvisioned()
    {
      if (Log.isLoggable("SyncManager", 3)) {
        Log.d("SyncManager", "mProvisioned=" + SyncManager.-get10(SyncManager.this));
      }
      checkIfDeviceReady();
    }
    
    public void stopSyncEvent(long paramLong1, SyncOperation paramSyncOperation, String paramString, int paramInt1, int paramInt2, long paramLong2)
    {
      EventLog.writeEvent(2720, paramSyncOperation.toEventLog(1));
      SyncManager.-get16(SyncManager.this).stopSyncEvent(paramLong1, paramLong2, paramString, paramInt2, paramInt1);
    }
  }
  
  private class SyncTimeTracker
  {
    boolean mLastWasSyncing = false;
    private long mTimeSpentSyncing;
    long mWhenSyncStarted = 0L;
    
    private SyncTimeTracker() {}
    
    public long timeSpentSyncing()
    {
      try
      {
        if (!this.mLastWasSyncing)
        {
          l1 = this.mTimeSpentSyncing;
          return l1;
        }
        long l1 = SystemClock.elapsedRealtime();
        long l2 = this.mTimeSpentSyncing;
        long l3 = this.mWhenSyncStarted;
        return l2 + (l1 - l3);
      }
      finally {}
    }
    
    /* Error */
    public void update()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 18	com/android/server/content/SyncManager$SyncTimeTracker:this$0	Lcom/android/server/content/SyncManager;
      //   6: getfield 43	com/android/server/content/SyncManager:mActiveSyncContexts	Ljava/util/ArrayList;
      //   9: invokevirtual 49	java/util/ArrayList:isEmpty	()Z
      //   12: ifeq +18 -> 30
      //   15: iconst_0
      //   16: istore_1
      //   17: aload_0
      //   18: getfield 23	com/android/server/content/SyncManager$SyncTimeTracker:mLastWasSyncing	Z
      //   21: istore_2
      //   22: iload_1
      //   23: iload_2
      //   24: if_icmpne +11 -> 35
      //   27: aload_0
      //   28: monitorexit
      //   29: return
      //   30: iconst_1
      //   31: istore_1
      //   32: goto -15 -> 17
      //   35: invokestatic 38	android/os/SystemClock:elapsedRealtime	()J
      //   38: lstore_3
      //   39: iload_1
      //   40: ifeq +16 -> 56
      //   43: aload_0
      //   44: lload_3
      //   45: putfield 25	com/android/server/content/SyncManager$SyncTimeTracker:mWhenSyncStarted	J
      //   48: aload_0
      //   49: iload_1
      //   50: putfield 23	com/android/server/content/SyncManager$SyncTimeTracker:mLastWasSyncing	Z
      //   53: aload_0
      //   54: monitorexit
      //   55: return
      //   56: aload_0
      //   57: aload_0
      //   58: getfield 33	com/android/server/content/SyncManager$SyncTimeTracker:mTimeSpentSyncing	J
      //   61: lload_3
      //   62: aload_0
      //   63: getfield 25	com/android/server/content/SyncManager$SyncTimeTracker:mWhenSyncStarted	J
      //   66: lsub
      //   67: ladd
      //   68: putfield 33	com/android/server/content/SyncManager$SyncTimeTracker:mTimeSpentSyncing	J
      //   71: goto -23 -> 48
      //   74: astore 5
      //   76: aload_0
      //   77: monitorexit
      //   78: aload 5
      //   80: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	81	0	this	SyncTimeTracker
      //   16	34	1	bool1	boolean
      //   21	4	2	bool2	boolean
      //   38	24	3	l	long
      //   74	5	5	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	15	74	finally
      //   17	22	74	finally
      //   35	39	74	finally
      //   43	48	74	finally
      //   48	53	74	finally
      //   56	71	74	finally
    }
  }
  
  private class UpdatePeriodicSyncMessagePayload
  {
    public final Bundle extras;
    public final long flex;
    public final long pollFrequency;
    public final SyncStorageEngine.EndPoint target;
    
    UpdatePeriodicSyncMessagePayload(SyncStorageEngine.EndPoint paramEndPoint, long paramLong1, long paramLong2, Bundle paramBundle)
    {
      this.target = paramEndPoint;
      this.pollFrequency = paramLong1;
      this.flex = paramLong2;
      this.extras = paramBundle;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/content/SyncManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */