package com.android.server.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.DataUsageRequest;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.INetworkStatsSession.Stub;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.NetworkIdentity;
import android.net.NetworkInfo;
import android.net.NetworkState;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStats.NonMonotonicObserver;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.NtpTrustedTime;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.TrustedTime;
import com.android.internal.net.VpnInfo;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.Preconditions;
import com.android.server.EventLogTags;
import com.android.server.NetPluginDelegate;
import com.android.server.NetworkManagementSocketTagger;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class NetworkStatsService
  extends INetworkStatsService.Stub
{
  public static final String ACTION_NETWORK_STATS_POLL = "com.android.server.action.NETWORK_STATS_POLL";
  public static final String ACTION_NETWORK_STATS_UPDATED = "com.android.server.action.NETWORK_STATS_UPDATED";
  private static final String DIALER_PACKEAGE_NAME = "com.android.dialer";
  private static final int FLAG_PERSIST_ALL = 3;
  private static final int FLAG_PERSIST_FORCE = 256;
  private static final int FLAG_PERSIST_NETWORK = 1;
  private static final int FLAG_PERSIST_UID = 2;
  private static final boolean LOGV = false;
  private static final int MSG_PERFORM_POLL = 1;
  private static final int MSG_REGISTER_GLOBAL_ALERT = 3;
  private static final int MSG_UPDATE_IFACES = 2;
  private static final String PREFIX_DEV = "dev";
  private static final String PREFIX_UID = "uid";
  private static final String PREFIX_UID_TAG = "uid_tag";
  private static final String PREFIX_XT = "xt";
  private static final String TAG = "NetworkStats";
  private static final String TAG_NETSTATS_ERROR = "netstats_error";
  public static final String VT_INTERFACE = "vt_data0";
  private String mActiveIface;
  private final ArrayMap<String, NetworkIdentitySet> mActiveIfaces = new ArrayMap();
  private SparseIntArray mActiveUidCounterSet = new SparseIntArray();
  private final ArrayMap<String, NetworkIdentitySet> mActiveUidIfaces = new ArrayMap();
  private final AlarmManager mAlarmManager;
  private INetworkManagementEventObserver mAlertObserver = new BaseNetworkObserver()
  {
    public void limitReached(String paramAnonymousString1, String paramAnonymousString2)
    {
      NetworkStatsService.-get0(NetworkStatsService.this).enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkStats");
      if ("globalAlert".equals(paramAnonymousString1))
      {
        NetworkStatsService.-get1(NetworkStatsService.this).obtainMessage(1, 1, 0).sendToTarget();
        NetworkStatsService.-get1(NetworkStatsService.this).obtainMessage(3).sendToTarget();
      }
    }
  };
  private final File mBaseDir;
  private boolean mConfigEnableDataUsage = false;
  private IConnectivityManager mConnManager;
  private final Context mContext;
  private NetworkStatsRecorder mDevRecorder;
  private long mGlobalAlertBytes;
  private final ContentObserver mGlobalAlertBytesObserver = new ContentObserver(this.mStatsHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      long l = NetworkStatsService.-get3(NetworkStatsService.this).getGlobalAlertBytes(NetworkStatsService.-get2(NetworkStatsService.this));
      if (l > 0L)
      {
        NetworkStatsService.-set0(NetworkStatsService.this, l);
        return;
      }
      NetworkStatsService.-set0(NetworkStatsService.this, NetworkStatsService.-get2(NetworkStatsService.this));
    }
  };
  private Handler mHandler;
  private Handler.Callback mHandlerCallback;
  private String[] mMobileIfaces = new String[0];
  private final INetworkManagementService mNetworkManager;
  private final DropBoxNonMonotonicObserver mNonMonotonicObserver = new DropBoxNonMonotonicObserver(null);
  private long mPersistThreshold = 2097152L;
  private PendingIntent mPollIntent;
  private BroadcastReceiver mPollReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      NetworkStatsService.-wrap3(NetworkStatsService.this, 3);
      NetworkStatsService.-wrap4(NetworkStatsService.this);
    }
  };
  private BroadcastReceiver mRemovedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
      if (i == -1) {
        return;
      }
      synchronized (NetworkStatsService.-get4(NetworkStatsService.this))
      {
        NetworkStatsService.-get7(NetworkStatsService.this).acquire();
        try
        {
          NetworkStatsService.-wrap5(NetworkStatsService.this, new int[] { i });
          NetworkStatsService.-get7(NetworkStatsService.this).release();
          return;
        }
        finally
        {
          paramAnonymousIntent = finally;
          NetworkStatsService.-get7(NetworkStatsService.this).release();
          throw paramAnonymousIntent;
        }
      }
    }
  };
  private final NetworkStatsSettings mSettings;
  private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      synchronized (NetworkStatsService.-get4(NetworkStatsService.this))
      {
        NetworkStatsService.-wrap7(NetworkStatsService.this);
        return;
      }
    }
  };
  private Handler mStatsHandler = null;
  private final Object mStatsLock = new Object();
  private final NetworkStatsObservers mStatsObservers;
  private final File mSystemDir;
  private boolean mSystemReady;
  private final TelephonyManager mTeleManager;
  private BroadcastReceiver mTetherReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      NetworkStatsService.-wrap3(NetworkStatsService.this, 1);
    }
  };
  private final TrustedTime mTime;
  private NetworkStats mUidOperations = new NetworkStats(0L, 10);
  private NetworkStatsRecorder mUidRecorder;
  private NetworkStatsRecorder mUidTagRecorder;
  private BroadcastReceiver mUserReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
      if (i == -1) {
        return;
      }
      synchronized (NetworkStatsService.-get4(NetworkStatsService.this))
      {
        NetworkStatsService.-get7(NetworkStatsService.this).acquire();
        try
        {
          NetworkStatsService.-wrap6(NetworkStatsService.this, i);
          NetworkStatsService.-get7(NetworkStatsService.this).release();
          return;
        }
        finally
        {
          paramAnonymousIntent = finally;
          NetworkStatsService.-get7(NetworkStatsService.this).release();
          throw paramAnonymousIntent;
        }
      }
    }
  };
  private NetworkStats.Entry mVideoCallMobileDataEntry;
  private NetworkStats.Entry mVideoCallWifiDataEntry;
  private final PowerManager.WakeLock mWakeLock;
  private NetworkStatsRecorder mXtRecorder;
  private NetworkStatsCollection mXtStatsCached;
  
  NetworkStatsService(Context paramContext, INetworkManagementService paramINetworkManagementService, AlarmManager paramAlarmManager, PowerManager.WakeLock paramWakeLock, TrustedTime paramTrustedTime, TelephonyManager paramTelephonyManager, NetworkStatsSettings paramNetworkStatsSettings, NetworkStatsObservers paramNetworkStatsObservers, File paramFile1, File paramFile2)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "missing Context"));
    this.mNetworkManager = ((INetworkManagementService)Preconditions.checkNotNull(paramINetworkManagementService, "missing INetworkManagementService"));
    this.mAlarmManager = ((AlarmManager)Preconditions.checkNotNull(paramAlarmManager, "missing AlarmManager"));
    this.mTime = ((TrustedTime)Preconditions.checkNotNull(paramTrustedTime, "missing TrustedTime"));
    this.mSettings = ((NetworkStatsSettings)Preconditions.checkNotNull(paramNetworkStatsSettings, "missing NetworkStatsSettings"));
    this.mTeleManager = ((TelephonyManager)Preconditions.checkNotNull(paramTelephonyManager, "missing TelephonyManager"));
    this.mWakeLock = ((PowerManager.WakeLock)Preconditions.checkNotNull(paramWakeLock, "missing WakeLock"));
    this.mStatsObservers = ((NetworkStatsObservers)Preconditions.checkNotNull(paramNetworkStatsObservers, "missing NetworkStatsObservers"));
    this.mSystemDir = ((File)Preconditions.checkNotNull(paramFile1, "missing systemDir"));
    this.mBaseDir = ((File)Preconditions.checkNotNull(paramFile2, "missing baseDir"));
    paramContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("netstats_global_alert_bytes"), false, this.mGlobalAlertBytesObserver);
    this.mConfigEnableDataUsage = this.mContext.getResources().getBoolean(17957067);
  }
  
  private void assertBandwidthControlEnabled()
  {
    if (!isBandwidthControlEnabled()) {
      throw new IllegalStateException("Bandwidth module disabled");
    }
  }
  
  private void bootstrapStatsLocked()
  {
    if (this.mTime.hasCache()) {}
    for (long l = this.mTime.currentTimeMillis();; l = System.currentTimeMillis()) {
      try
      {
        recordSnapshotLocked(l);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Slog.w("NetworkStats", "problem reading network stats: " + localIllegalStateException);
      }
    }
  }
  
  private NetworkStatsRecorder buildRecorder(String paramString, NetworkStatsService.NetworkStatsSettings.Config paramConfig, boolean paramBoolean)
  {
    DropBoxManager localDropBoxManager = (DropBoxManager)this.mContext.getSystemService("dropbox");
    return new NetworkStatsRecorder(new FileRotator(this.mBaseDir, paramString, paramConfig.rotateAgeMillis, paramConfig.deleteAgeMillis), this.mNonMonotonicObserver, localDropBoxManager, paramString, paramConfig.bucketDuration, paramBoolean);
  }
  
  private int checkAccessLevel(String paramString)
  {
    return NetworkStatsAccess.checkAccessLevel(this.mContext, Binder.getCallingUid(), paramString);
  }
  
  private void combineVideoCallEntryValues(NetworkStats paramNetworkStats)
  {
    if (this.mVideoCallMobileDataEntry != null) {}
    synchronized (this.mVideoCallMobileDataEntry)
    {
      paramNetworkStats.combineValues(this.mVideoCallMobileDataEntry);
      if (this.mVideoCallWifiDataEntry == null) {}
    }
    synchronized (this.mVideoCallWifiDataEntry)
    {
      paramNetworkStats.combineValues(this.mVideoCallWifiDataEntry);
      return;
      paramNetworkStats = finally;
      throw paramNetworkStats;
    }
  }
  
  public static NetworkStatsService create(Context paramContext, INetworkManagementService paramINetworkManagementService)
  {
    paramContext = new NetworkStatsService(paramContext, paramINetworkManagementService, (AlarmManager)paramContext.getSystemService("alarm"), ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "NetworkStats"), NtpTrustedTime.getInstance(paramContext), TelephonyManager.getDefault(), new DefaultNetworkStatsSettings(paramContext), new NetworkStatsObservers(), getDefaultSystemDir(), getDefaultBaseDir());
    paramINetworkManagementService = new HandlerThread("NetworkStats");
    HandlerCallback localHandlerCallback = new HandlerCallback(paramContext);
    paramINetworkManagementService.start();
    paramContext.setHandler(new Handler(paramINetworkManagementService.getLooper(), localHandlerCallback), localHandlerCallback);
    paramINetworkManagementService = new HandlerThread("StatsObserver");
    paramINetworkManagementService.start();
    new Handler(paramINetworkManagementService.getLooper());
    return paramContext;
  }
  
  private INetworkStatsSession createSession(final String paramString, boolean paramBoolean)
  {
    assertBandwidthControlEnabled();
    long l;
    if (paramBoolean) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      performPoll(3);
      new INetworkStatsSession.Stub()
      {
        private String mCallingPackage = paramString;
        private NetworkStatsCollection mUidComplete;
        private NetworkStatsCollection mUidTagComplete;
        
        private NetworkStatsCollection getUidComplete()
        {
          synchronized (NetworkStatsService.-get4(NetworkStatsService.this))
          {
            if (this.mUidComplete == null) {
              this.mUidComplete = NetworkStatsService.-get5(NetworkStatsService.this).getOrLoadCompleteLocked();
            }
            NetworkStatsCollection localNetworkStatsCollection = this.mUidComplete;
            return localNetworkStatsCollection;
          }
        }
        
        private NetworkStatsCollection getUidTagComplete()
        {
          synchronized (NetworkStatsService.-get4(NetworkStatsService.this))
          {
            if (this.mUidTagComplete == null) {
              this.mUidTagComplete = NetworkStatsService.-get6(NetworkStatsService.this).getOrLoadCompleteLocked();
            }
            NetworkStatsCollection localNetworkStatsCollection = this.mUidTagComplete;
            return localNetworkStatsCollection;
          }
        }
        
        public void close()
        {
          this.mUidComplete = null;
          this.mUidTagComplete = null;
        }
        
        public NetworkStats getDeviceSummaryForNetwork(NetworkTemplate paramAnonymousNetworkTemplate, long paramAnonymousLong1, long paramAnonymousLong2)
        {
          if (NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage) < 2) {
            throw new SecurityException("Calling package " + this.mCallingPackage + " cannot access device summary network stats");
          }
          NetworkStats localNetworkStats = new NetworkStats(paramAnonymousLong2 - paramAnonymousLong1, 1);
          long l = Binder.clearCallingIdentity();
          try
          {
            localNetworkStats.combineAllValues(NetworkStatsService.-wrap1(NetworkStatsService.this, paramAnonymousNetworkTemplate, paramAnonymousLong1, paramAnonymousLong2, 3));
            return localNetworkStats;
          }
          finally
          {
            Binder.restoreCallingIdentity(l);
          }
        }
        
        public NetworkStatsHistory getHistoryForNetwork(NetworkTemplate paramAnonymousNetworkTemplate, int paramAnonymousInt)
        {
          int i = NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage);
          return NetworkStatsService.-wrap0(NetworkStatsService.this, paramAnonymousNetworkTemplate, paramAnonymousInt, i);
        }
        
        public NetworkStatsHistory getHistoryForUid(NetworkTemplate paramAnonymousNetworkTemplate, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          int i = NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage);
          if (paramAnonymousInt3 == 0) {
            return getUidComplete().getHistory(paramAnonymousNetworkTemplate, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, i);
          }
          return getUidTagComplete().getHistory(paramAnonymousNetworkTemplate, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, i);
        }
        
        public NetworkStatsHistory getHistoryIntervalForUid(NetworkTemplate paramAnonymousNetworkTemplate, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, long paramAnonymousLong1, long paramAnonymousLong2)
        {
          int i = NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage);
          if (paramAnonymousInt3 == 0) {
            return getUidComplete().getHistory(paramAnonymousNetworkTemplate, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousLong1, paramAnonymousLong2, i);
          }
          if (paramAnonymousInt1 == Binder.getCallingUid()) {
            return getUidTagComplete().getHistory(paramAnonymousNetworkTemplate, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousLong1, paramAnonymousLong2, i);
          }
          throw new SecurityException("Calling package " + this.mCallingPackage + " cannot access tag information from a different uid");
        }
        
        public int[] getRelevantUids()
        {
          return getUidComplete().getRelevantUids(NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage));
        }
        
        public NetworkStats getSummaryForAllUid(NetworkTemplate paramAnonymousNetworkTemplate, long paramAnonymousLong1, long paramAnonymousLong2, boolean paramAnonymousBoolean)
        {
          int i = NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage);
          NetworkStats localNetworkStats = getUidComplete().getSummary(paramAnonymousNetworkTemplate, paramAnonymousLong1, paramAnonymousLong2, i);
          if (paramAnonymousBoolean) {
            localNetworkStats.combineAllValues(getUidTagComplete().getSummary(paramAnonymousNetworkTemplate, paramAnonymousLong1, paramAnonymousLong2, i));
          }
          return localNetworkStats;
        }
        
        public NetworkStats getSummaryForNetwork(NetworkTemplate paramAnonymousNetworkTemplate, long paramAnonymousLong1, long paramAnonymousLong2)
        {
          int i = NetworkStatsService.-wrap2(NetworkStatsService.this, this.mCallingPackage);
          return NetworkStatsService.-wrap1(NetworkStatsService.this, paramAnonymousNetworkTemplate, paramAnonymousLong1, paramAnonymousLong2, i);
        }
      };
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private NetworkStats.Entry createVideoCallDataEntry(String paramString)
  {
    NetworkStats.Entry localEntry = new NetworkStats.Entry();
    Object localObject2 = this.mContext.getPackageManager();
    Object localObject1 = null;
    try
    {
      localObject2 = ((PackageManager)localObject2).getApplicationInfo("com.android.dialer", 1);
      localObject1 = localObject2;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.d("NetworkStats", "get dialer getApplicationInfo failed ");
      }
    }
    if (localObject1 != null) {
      localEntry.uid = ((ApplicationInfo)localObject1).uid;
    }
    localEntry.iface = paramString;
    return localEntry;
  }
  
  private static <K> NetworkIdentitySet findOrCreateNetworkIdentitySet(ArrayMap<K, NetworkIdentitySet> paramArrayMap, K paramK)
  {
    NetworkIdentitySet localNetworkIdentitySet2 = (NetworkIdentitySet)paramArrayMap.get(paramK);
    NetworkIdentitySet localNetworkIdentitySet1 = localNetworkIdentitySet2;
    if (localNetworkIdentitySet2 == null)
    {
      localNetworkIdentitySet1 = new NetworkIdentitySet();
      paramArrayMap.put(paramK, localNetworkIdentitySet1);
    }
    return localNetworkIdentitySet1;
  }
  
  private static File getDefaultBaseDir()
  {
    File localFile = new File(getDefaultSystemDir(), "netstats");
    localFile.mkdirs();
    return localFile;
  }
  
  private static File getDefaultSystemDir()
  {
    return new File(Environment.getDataDirectory(), "system");
  }
  
  private NetworkStats getNetworkStatsTethering()
    throws RemoteException
  {
    try
    {
      NetworkStats localNetworkStats = this.mNetworkManager.getNetworkStatsTethering();
      return localNetworkStats;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.wtf("NetworkStats", "problem reading network stats", localIllegalStateException);
    }
    return new NetworkStats(0L, 10);
  }
  
  private NetworkStats getNetworkStatsUidDetail()
    throws RemoteException
  {
    NetworkStats localNetworkStats = this.mNetworkManager.getNetworkStatsUidDetail(-1);
    localNetworkStats.combineAllValues(getNetworkStatsTethering());
    localNetworkStats.combineAllValues(this.mUidOperations);
    return localNetworkStats;
  }
  
  private NetworkStats getNetworkStatsXtAndVt()
    throws RemoteException
  {
    NetworkStats localNetworkStats1 = this.mNetworkManager.getNetworkStatsSummaryXt();
    long l = ((TelephonyManager)this.mContext.getSystemService("phone")).getVtDataUsage();
    NetworkStats localNetworkStats2 = new NetworkStats(SystemClock.elapsedRealtime(), 1);
    NetworkStats.Entry localEntry = new NetworkStats.Entry();
    localEntry.iface = "vt_data0";
    localEntry.uid = -1;
    localEntry.set = -1;
    localEntry.tag = 0;
    localEntry.rxBytes = (l / 2L);
    localEntry.rxPackets = 0L;
    localEntry.txBytes = (l - localEntry.rxBytes);
    localEntry.txPackets = 0L;
    localNetworkStats2.combineValues(localEntry);
    localNetworkStats1.combineAllValues(localNetworkStats2);
    return localNetworkStats1;
  }
  
  private boolean hasImsNetworkCapability(NetworkState paramNetworkState)
  {
    return (this.mConfigEnableDataUsage) && (paramNetworkState.networkCapabilities.hasCapability(4));
  }
  
  private NetworkStatsHistory internalGetHistoryForNetwork(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2)
  {
    return this.mXtStatsCached.getHistory(paramNetworkTemplate, -1, -1, 0, paramInt1, paramInt2);
  }
  
  private NetworkStats internalGetSummaryForNetwork(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, int paramInt)
  {
    return this.mXtStatsCached.getSummary(paramNetworkTemplate, paramLong1, paramLong2, paramInt);
  }
  
  private boolean isBandwidthControlEnabled()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = this.mNetworkManager.isBandwidthControlEnabled();
      Binder.restoreCallingIdentity(l);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      Binder.restoreCallingIdentity(l);
      return false;
    }
    finally
    {
      localObject = finally;
      Binder.restoreCallingIdentity(l);
      throw ((Throwable)localObject);
    }
  }
  
  private void maybeUpgradeLegacyStatsLocked()
  {
    try
    {
      File localFile = new File(this.mSystemDir, "netstats.bin");
      if (localFile.exists())
      {
        this.mDevRecorder.importLegacyNetworkLocked(localFile);
        localFile.delete();
      }
      localFile = new File(this.mSystemDir, "netstats_xt.bin");
      if (localFile.exists()) {
        localFile.delete();
      }
      localFile = new File(this.mSystemDir, "netstats_uid.bin");
      if (localFile.exists())
      {
        this.mUidRecorder.importLegacyUidLocked(localFile);
        this.mUidTagRecorder.importLegacyUidLocked(localFile);
        localFile.delete();
      }
      return;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      Log.wtf("NetworkStats", "problem during legacy upgrade", localOutOfMemoryError);
      return;
    }
    catch (IOException localIOException)
    {
      Log.wtf("NetworkStats", "problem during legacy upgrade", localIOException);
    }
  }
  
  private void performPoll(int paramInt)
  {
    if (this.mTime.getCacheAge() > this.mSettings.getTimeCacheMaxAge()) {
      this.mTime.forceRefresh();
    }
    synchronized (this.mStatsLock)
    {
      this.mWakeLock.acquire();
      try
      {
        performPollLocked(paramInt);
        this.mWakeLock.release();
        return;
      }
      finally
      {
        localObject2 = finally;
        this.mWakeLock.release();
        throw ((Throwable)localObject2);
      }
    }
  }
  
  private void performPollLocked(int paramInt)
  {
    if (!this.mSystemReady) {
      return;
    }
    SystemClock.elapsedRealtime();
    int i;
    int j;
    label28:
    label38:
    long l;
    if ((paramInt & 0x1) != 0)
    {
      i = 1;
      if ((paramInt & 0x2) == 0) {
        break label164;
      }
      j = 1;
      if ((paramInt & 0x100) == 0) {
        break label169;
      }
      paramInt = 1;
      if (!this.mTime.hasCache()) {
        break label174;
      }
      l = this.mTime.currentTimeMillis();
    }
    for (;;)
    {
      try
      {
        recordSnapshotLocked(l);
        if (paramInt == 0) {
          break label199;
        }
        this.mDevRecorder.forcePersistLocked(l);
        this.mXtRecorder.forcePersistLocked(l);
        this.mUidRecorder.forcePersistLocked(l);
        this.mUidTagRecorder.forcePersistLocked(l);
        if (this.mSettings.getSampleEnabled()) {
          performSampleLocked();
        }
        Intent localIntent = new Intent("com.android.server.action.NETWORK_STATS_UPDATED");
        localIntent.setFlags(1073741824);
        this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.READ_NETWORK_USAGE_HISTORY");
        return;
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        label164:
        label169:
        label174:
        Log.wtf("NetworkStats", "problem reading network stats", localIllegalStateException);
        return;
      }
      i = 0;
      break;
      j = 0;
      break label28;
      paramInt = 0;
      break label38;
      l = System.currentTimeMillis();
      continue;
      label199:
      if (i != 0)
      {
        this.mDevRecorder.maybePersistLocked(l);
        this.mXtRecorder.maybePersistLocked(l);
      }
      if (j != 0)
      {
        this.mUidRecorder.maybePersistLocked(l);
        this.mUidTagRecorder.maybePersistLocked(l);
      }
    }
  }
  
  private void performSampleLocked()
  {
    if (this.mTime.hasCache()) {}
    for (long l = this.mTime.currentTimeMillis();; l = -1L)
    {
      Object localObject = NetworkTemplate.buildTemplateMobileWildcard();
      NetworkStats.Entry localEntry1 = this.mDevRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      NetworkStats.Entry localEntry2 = this.mXtRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      localObject = this.mUidRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      EventLogTags.writeNetstatsMobileSample(localEntry1.rxBytes, localEntry1.rxPackets, localEntry1.txBytes, localEntry1.txPackets, localEntry2.rxBytes, localEntry2.rxPackets, localEntry2.txBytes, localEntry2.txPackets, ((NetworkStats.Entry)localObject).rxBytes, ((NetworkStats.Entry)localObject).rxPackets, ((NetworkStats.Entry)localObject).txBytes, ((NetworkStats.Entry)localObject).txPackets, l);
      localObject = NetworkTemplate.buildTemplateWifiWildcard();
      localEntry1 = this.mDevRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      localEntry2 = this.mXtRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      localObject = this.mUidRecorder.getTotalSinceBootLocked((NetworkTemplate)localObject);
      EventLogTags.writeNetstatsWifiSample(localEntry1.rxBytes, localEntry1.rxPackets, localEntry1.txBytes, localEntry1.txPackets, localEntry2.rxBytes, localEntry2.rxPackets, localEntry2.txBytes, localEntry2.txPackets, ((NetworkStats.Entry)localObject).rxBytes, ((NetworkStats.Entry)localObject).rxPackets, ((NetworkStats.Entry)localObject).txBytes, ((NetworkStats.Entry)localObject).txPackets, l);
      return;
    }
  }
  
  private void recordSnapshotLocked(long paramLong)
    throws RemoteException
  {
    NetworkStats localNetworkStats1 = getNetworkStatsUidDetail();
    NetworkStats localNetworkStats2 = getNetworkStatsXtAndVt();
    Object localObject = this.mNetworkManager.getNetworkStatsSummaryDev();
    NetPluginDelegate.getTetherStats(localNetworkStats1, localNetworkStats2, (NetworkStats)localObject);
    if (this.mConfigEnableDataUsage) {
      combineVideoCallEntryValues(localNetworkStats1);
    }
    this.mDevRecorder.recordSnapshotLocked((NetworkStats)localObject, this.mActiveIfaces, null, paramLong);
    this.mXtRecorder.recordSnapshotLocked(localNetworkStats2, this.mActiveIfaces, null, paramLong);
    localObject = this.mConnManager.getAllVpnInfo();
    this.mUidRecorder.recordSnapshotLocked(localNetworkStats1, this.mActiveUidIfaces, (VpnInfo[])localObject, paramLong);
    this.mUidTagRecorder.recordSnapshotLocked(localNetworkStats1, this.mActiveUidIfaces, (VpnInfo[])localObject, paramLong);
    this.mStatsObservers.updateStats(localNetworkStats2, localNetworkStats1, new ArrayMap(this.mActiveIfaces), new ArrayMap(this.mActiveUidIfaces), (VpnInfo[])localObject, paramLong);
  }
  
  private void registerGlobalAlert()
  {
    try
    {
      this.mNetworkManager.setGlobalAlert(this.mGlobalAlertBytes);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Slog.w("NetworkStats", "problem registering for global alert: " + localIllegalStateException);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void registerPollAlarmLocked()
  {
    if (this.mPollIntent != null) {
      this.mAlarmManager.cancel(this.mPollIntent);
    }
    this.mPollIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.server.action.NETWORK_STATS_POLL"), 0);
    long l = SystemClock.elapsedRealtime();
    this.mAlarmManager.setInexactRepeating(3, l, this.mSettings.getPollInterval(), this.mPollIntent);
  }
  
  private void removeUidsLocked(int... paramVarArgs)
  {
    performPollLocked(3);
    this.mUidRecorder.removeUidsLocked(paramVarArgs);
    this.mUidTagRecorder.removeUidsLocked(paramVarArgs);
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      NetworkManagementSocketTagger.resetKernelUidStats(paramVarArgs[i]);
      i += 1;
    }
  }
  
  private void removeUserLocked(int paramInt)
  {
    int[] arrayOfInt = new int[0];
    Iterator localIterator = this.mContext.getPackageManager().getInstalledApplications(8704).iterator();
    while (localIterator.hasNext()) {
      arrayOfInt = ArrayUtils.appendInt(arrayOfInt, UserHandle.getUid(paramInt, ((ApplicationInfo)localIterator.next()).uid));
    }
    removeUidsLocked(arrayOfInt);
  }
  
  private void shutdownLocked()
  {
    this.mContext.unregisterReceiver(this.mTetherReceiver);
    this.mContext.unregisterReceiver(this.mPollReceiver);
    this.mContext.unregisterReceiver(this.mRemovedReceiver);
    this.mContext.unregisterReceiver(this.mShutdownReceiver);
    if (this.mTime.hasCache()) {}
    for (long l = this.mTime.currentTimeMillis();; l = System.currentTimeMillis())
    {
      this.mDevRecorder.forcePersistLocked(l);
      this.mXtRecorder.forcePersistLocked(l);
      this.mUidRecorder.forcePersistLocked(l);
      this.mUidTagRecorder.forcePersistLocked(l);
      this.mDevRecorder = null;
      this.mXtRecorder = null;
      this.mUidRecorder = null;
      this.mUidTagRecorder = null;
      this.mXtStatsCached = null;
      this.mSystemReady = false;
      return;
    }
  }
  
  private void updateIfaces()
  {
    synchronized (this.mStatsLock)
    {
      this.mWakeLock.acquire();
      try
      {
        updateIfacesLocked();
        this.mWakeLock.release();
        return;
      }
      finally
      {
        localObject2 = finally;
        this.mWakeLock.release();
        throw ((Throwable)localObject2);
      }
    }
  }
  
  private void updateIfacesLocked()
  {
    if (!this.mSystemReady) {
      return;
    }
    performPollLocked(1);
    Object localObject2;
    for (;;)
    {
      int i;
      NetworkIdentity localNetworkIdentity1;
      try
      {
        NetworkState[] arrayOfNetworkState = this.mConnManager.getAllNetworkState();
        Object localObject1 = this.mConnManager.getActiveLinkProperties();
        if (localObject1 != null)
        {
          localObject1 = ((LinkProperties)localObject1).getInterfaceName();
          this.mActiveIface = ((String)localObject1);
          this.mActiveIfaces.clear();
          this.mActiveUidIfaces.clear();
          localObject1 = new ArraySet();
          int j = arrayOfNetworkState.length;
          i = 0;
          if (i >= j) {
            break;
          }
          Object localObject3 = arrayOfNetworkState[i];
          if ((((NetworkState)localObject3).networkInfo == null) || (!((NetworkState)localObject3).networkInfo.isConnected())) {
            break label364;
          }
          boolean bool = ConnectivityManager.isNetworkTypeMobile(((NetworkState)localObject3).networkInfo.getType());
          localNetworkIdentity1 = NetworkIdentity.buildNetworkIdentity(this.mContext, (NetworkState)localObject3);
          String str = ((NetworkState)localObject3).linkProperties.getInterfaceName();
          if (str != null)
          {
            findOrCreateNetworkIdentitySet(this.mActiveIfaces, str).add(localNetworkIdentity1);
            findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, str).add(localNetworkIdentity1);
            if ((((NetworkState)localObject3).networkCapabilities.hasCapability(4)) && (!localNetworkIdentity1.getMetered())) {
              break label296;
            }
            if (bool) {
              ((ArraySet)localObject1).add(str);
            }
          }
          localObject3 = ((NetworkState)localObject3).linkProperties.getStackedLinks().iterator();
          if (!((Iterator)localObject3).hasNext()) {
            break label364;
          }
          str = ((LinkProperties)((Iterator)localObject3).next()).getInterfaceName();
          if (str == null) {
            continue;
          }
          findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, str).add(localNetworkIdentity1);
          if (!bool) {
            continue;
          }
          ((ArraySet)localObject1).add(str);
          continue;
        }
        localObject2 = null;
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      continue;
      label296:
      NetworkIdentity localNetworkIdentity2 = new NetworkIdentity(localNetworkIdentity1.getType(), localNetworkIdentity1.getSubType(), localNetworkIdentity1.getSubscriberId(), localNetworkIdentity1.getNetworkId(), localNetworkIdentity1.getRoaming(), true);
      findOrCreateNetworkIdentitySet(this.mActiveIfaces, "vt_data0").add(localNetworkIdentity2);
      findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, "vt_data0").add(localNetworkIdentity2);
      continue;
      label364:
      i += 1;
    }
    this.mMobileIfaces = ((String[])((ArraySet)localObject2).toArray(new String[((ArraySet)localObject2).size()]));
  }
  
  private void updatePersistThresholds()
  {
    this.mDevRecorder.setPersistThreshold(this.mSettings.getDevPersistBytes(this.mPersistThreshold));
    this.mXtRecorder.setPersistThreshold(this.mSettings.getXtPersistBytes(this.mPersistThreshold));
    this.mUidRecorder.setPersistThreshold(this.mSettings.getUidPersistBytes(this.mPersistThreshold));
    this.mUidTagRecorder.setPersistThreshold(this.mSettings.getUidTagPersistBytes(this.mPersistThreshold));
    this.mGlobalAlertBytes = this.mSettings.getGlobalAlertBytes(this.mPersistThreshold);
  }
  
  public void advisePersistThreshold(long paramLong)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MODIFY_NETWORK_ACCOUNTING", "NetworkStats");
    assertBandwidthControlEnabled();
    this.mPersistThreshold = MathUtils.constrain(paramLong, 131072L, 2097152L);
    if (this.mTime.hasCache()) {
      paramLong = this.mTime.currentTimeMillis();
    }
    synchronized (this.mStatsLock)
    {
      for (;;)
      {
        boolean bool = this.mSystemReady;
        if (bool) {
          break;
        }
        return;
        paramLong = System.currentTimeMillis();
      }
      updatePersistThresholds();
      this.mDevRecorder.maybePersistLocked(paramLong);
      this.mXtRecorder.maybePersistLocked(paramLong);
      this.mUidRecorder.maybePersistLocked(paramLong);
      this.mUidTagRecorder.maybePersistLocked(paramLong);
      registerGlobalAlert();
      return;
    }
  }
  
  public void bindConnectivityManager(IConnectivityManager paramIConnectivityManager)
  {
    this.mConnManager = ((IConnectivityManager)Preconditions.checkNotNull(paramIConnectivityManager, "missing IConnectivityManager"));
  }
  
  /* Error */
  protected void dump(java.io.FileDescriptor paramFileDescriptor, java.io.PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 153	com/android/server/net/NetworkStatsService:mContext	Landroid/content/Context;
    //   4: ldc_w 1035
    //   7: ldc 75
    //   9: invokevirtual 1015	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: ldc2_w 1036
    //   15: lstore 6
    //   17: new 1039	java/util/HashSet
    //   20: dup
    //   21: invokespecial 1040	java/util/HashSet:<init>	()V
    //   24: astore_1
    //   25: iconst_0
    //   26: istore 4
    //   28: aload_3
    //   29: arraylength
    //   30: istore 5
    //   32: iload 4
    //   34: iload 5
    //   36: if_icmpge +56 -> 92
    //   39: aload_3
    //   40: iload 4
    //   42: aaload
    //   43: astore 15
    //   45: aload_1
    //   46: aload 15
    //   48: invokevirtual 1041	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   51: pop
    //   52: lload 6
    //   54: lstore 8
    //   56: aload 15
    //   58: ldc_w 1043
    //   61: invokevirtual 1047	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   64: ifeq +15 -> 79
    //   67: aload 15
    //   69: bipush 11
    //   71: invokevirtual 1051	java/lang/String:substring	(I)Ljava/lang/String;
    //   74: invokestatic 1057	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   77: lstore 8
    //   79: iload 4
    //   81: iconst_1
    //   82: iadd
    //   83: istore 4
    //   85: lload 8
    //   87: lstore 6
    //   89: goto -57 -> 32
    //   92: aload_1
    //   93: ldc_w 1059
    //   96: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   99: ifne +124 -> 223
    //   102: aload_1
    //   103: ldc_w 1064
    //   106: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   109: istore 10
    //   111: aload_1
    //   112: ldc_w 1066
    //   115: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   118: istore 14
    //   120: aload_1
    //   121: ldc_w 1068
    //   124: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   127: ifne +102 -> 229
    //   130: aload_1
    //   131: ldc_w 1070
    //   134: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   137: istore 11
    //   139: aload_1
    //   140: ldc_w 1072
    //   143: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   146: ifne +89 -> 235
    //   149: aload_1
    //   150: ldc_w 1074
    //   153: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   156: istore 12
    //   158: aload_1
    //   159: ldc_w 1076
    //   162: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   165: ifne +76 -> 241
    //   168: aload_1
    //   169: ldc_w 1074
    //   172: invokevirtual 1062	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   175: istore 13
    //   177: new 1078	com/android/internal/util/IndentingPrintWriter
    //   180: dup
    //   181: aload_2
    //   182: ldc_w 1080
    //   185: invokespecial 1083	com/android/internal/util/IndentingPrintWriter:<init>	(Ljava/io/Writer;Ljava/lang/String;)V
    //   188: astore 16
    //   190: aload_0
    //   191: getfield 170	com/android/server/net/NetworkStatsService:mStatsLock	Ljava/lang/Object;
    //   194: astore 15
    //   196: aload 15
    //   198: monitorenter
    //   199: iload 10
    //   201: ifeq +46 -> 247
    //   204: aload_0
    //   205: sipush 259
    //   208: invokespecial 739	com/android/server/net/NetworkStatsService:performPollLocked	(I)V
    //   211: aload 16
    //   213: ldc_w 1085
    //   216: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   219: aload 15
    //   221: monitorexit
    //   222: return
    //   223: iconst_1
    //   224: istore 10
    //   226: goto -115 -> 111
    //   229: iconst_1
    //   230: istore 11
    //   232: goto -93 -> 139
    //   235: iconst_1
    //   236: istore 12
    //   238: goto -80 -> 158
    //   241: iconst_1
    //   242: istore 13
    //   244: goto -67 -> 177
    //   247: iload 14
    //   249: ifeq +129 -> 378
    //   252: invokestatic 412	java/lang/System:currentTimeMillis	()J
    //   255: lstore 8
    //   257: lload 8
    //   259: lload 6
    //   261: lsub
    //   262: lstore 6
    //   264: aload 16
    //   266: ldc_w 1090
    //   269: invokevirtual 1093	com/android/internal/util/IndentingPrintWriter:print	(Ljava/lang/String;)V
    //   272: aload 16
    //   274: lload 6
    //   276: ldc2_w 1094
    //   279: ldiv
    //   280: invokevirtual 1097	com/android/internal/util/IndentingPrintWriter:print	(J)V
    //   283: aload 16
    //   285: bipush 44
    //   287: invokevirtual 1100	com/android/internal/util/IndentingPrintWriter:print	(C)V
    //   290: aload 16
    //   292: lload 8
    //   294: ldc2_w 1094
    //   297: ldiv
    //   298: invokevirtual 1097	com/android/internal/util/IndentingPrintWriter:print	(J)V
    //   301: aload 16
    //   303: invokevirtual 1102	com/android/internal/util/IndentingPrintWriter:println	()V
    //   306: aload 16
    //   308: ldc 72
    //   310: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   313: aload_0
    //   314: getfield 749	com/android/server/net/NetworkStatsService:mXtRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   317: aload_2
    //   318: lload 6
    //   320: lload 8
    //   322: invokevirtual 1106	com/android/server/net/NetworkStatsRecorder:dumpCheckin	(Ljava/io/PrintWriter;JJ)V
    //   325: iload 12
    //   327: ifeq +22 -> 349
    //   330: aload 16
    //   332: ldc 66
    //   334: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   337: aload_0
    //   338: getfield 174	com/android/server/net/NetworkStatsService:mUidRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   341: aload_2
    //   342: lload 6
    //   344: lload 8
    //   346: invokevirtual 1106	com/android/server/net/NetworkStatsRecorder:dumpCheckin	(Ljava/io/PrintWriter;JJ)V
    //   349: iload 13
    //   351: ifeq +23 -> 374
    //   354: aload 16
    //   356: ldc_w 1107
    //   359: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   362: aload_0
    //   363: getfield 177	com/android/server/net/NetworkStatsService:mUidTagRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   366: aload_2
    //   367: lload 6
    //   369: lload 8
    //   371: invokevirtual 1106	com/android/server/net/NetworkStatsRecorder:dumpCheckin	(Ljava/io/PrintWriter;JJ)V
    //   374: aload 15
    //   376: monitorexit
    //   377: return
    //   378: aload 16
    //   380: ldc_w 1109
    //   383: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   386: aload 16
    //   388: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   391: iconst_0
    //   392: istore 4
    //   394: iload 4
    //   396: aload_0
    //   397: getfield 245	com/android/server/net/NetworkStatsService:mActiveIfaces	Landroid/util/ArrayMap;
    //   400: invokevirtual 1113	android/util/ArrayMap:size	()I
    //   403: if_icmpge +51 -> 454
    //   406: aload 16
    //   408: ldc_w 1114
    //   411: aload_0
    //   412: getfield 245	com/android/server/net/NetworkStatsService:mActiveIfaces	Landroid/util/ArrayMap;
    //   415: iload 4
    //   417: invokevirtual 1118	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   420: invokevirtual 1122	com/android/internal/util/IndentingPrintWriter:printPair	(Ljava/lang/String;Ljava/lang/Object;)V
    //   423: aload 16
    //   425: ldc_w 1124
    //   428: aload_0
    //   429: getfield 245	com/android/server/net/NetworkStatsService:mActiveIfaces	Landroid/util/ArrayMap;
    //   432: iload 4
    //   434: invokevirtual 1127	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   437: invokevirtual 1122	com/android/internal/util/IndentingPrintWriter:printPair	(Ljava/lang/String;Ljava/lang/Object;)V
    //   440: aload 16
    //   442: invokevirtual 1102	com/android/internal/util/IndentingPrintWriter:println	()V
    //   445: iload 4
    //   447: iconst_1
    //   448: iadd
    //   449: istore 4
    //   451: goto -57 -> 394
    //   454: aload 16
    //   456: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   459: aload 16
    //   461: ldc_w 1132
    //   464: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   467: aload 16
    //   469: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   472: iconst_0
    //   473: istore 4
    //   475: iload 4
    //   477: aload_0
    //   478: getfield 247	com/android/server/net/NetworkStatsService:mActiveUidIfaces	Landroid/util/ArrayMap;
    //   481: invokevirtual 1113	android/util/ArrayMap:size	()I
    //   484: if_icmpge +51 -> 535
    //   487: aload 16
    //   489: ldc_w 1114
    //   492: aload_0
    //   493: getfield 247	com/android/server/net/NetworkStatsService:mActiveUidIfaces	Landroid/util/ArrayMap;
    //   496: iload 4
    //   498: invokevirtual 1118	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   501: invokevirtual 1122	com/android/internal/util/IndentingPrintWriter:printPair	(Ljava/lang/String;Ljava/lang/Object;)V
    //   504: aload 16
    //   506: ldc_w 1124
    //   509: aload_0
    //   510: getfield 247	com/android/server/net/NetworkStatsService:mActiveUidIfaces	Landroid/util/ArrayMap;
    //   513: iload 4
    //   515: invokevirtual 1127	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   518: invokevirtual 1122	com/android/internal/util/IndentingPrintWriter:printPair	(Ljava/lang/String;Ljava/lang/Object;)V
    //   521: aload 16
    //   523: invokevirtual 1102	com/android/internal/util/IndentingPrintWriter:println	()V
    //   526: iload 4
    //   528: iconst_1
    //   529: iadd
    //   530: istore 4
    //   532: goto -57 -> 475
    //   535: aload 16
    //   537: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   540: aload 16
    //   542: ldc_w 1134
    //   545: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   548: aload 16
    //   550: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   553: aload_0
    //   554: getfield 708	com/android/server/net/NetworkStatsService:mDevRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   557: aload 16
    //   559: iload 11
    //   561: invokevirtual 1138	com/android/server/net/NetworkStatsRecorder:dumpLocked	(Lcom/android/internal/util/IndentingPrintWriter;Z)V
    //   564: aload 16
    //   566: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   569: aload 16
    //   571: ldc_w 1140
    //   574: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   577: aload 16
    //   579: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   582: aload_0
    //   583: getfield 749	com/android/server/net/NetworkStatsService:mXtRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   586: aload 16
    //   588: iload 11
    //   590: invokevirtual 1138	com/android/server/net/NetworkStatsRecorder:dumpLocked	(Lcom/android/internal/util/IndentingPrintWriter;Z)V
    //   593: aload 16
    //   595: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   598: iload 12
    //   600: ifeq +32 -> 632
    //   603: aload 16
    //   605: ldc_w 1142
    //   608: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   611: aload 16
    //   613: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   616: aload_0
    //   617: getfield 174	com/android/server/net/NetworkStatsService:mUidRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   620: aload 16
    //   622: iload 11
    //   624: invokevirtual 1138	com/android/server/net/NetworkStatsRecorder:dumpLocked	(Lcom/android/internal/util/IndentingPrintWriter;Z)V
    //   627: aload 16
    //   629: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   632: iload 13
    //   634: ifeq +32 -> 666
    //   637: aload 16
    //   639: ldc_w 1144
    //   642: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   645: aload 16
    //   647: invokevirtual 1112	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   650: aload_0
    //   651: getfield 177	com/android/server/net/NetworkStatsService:mUidTagRecorder	Lcom/android/server/net/NetworkStatsRecorder;
    //   654: aload 16
    //   656: iload 11
    //   658: invokevirtual 1138	com/android/server/net/NetworkStatsRecorder:dumpLocked	(Lcom/android/internal/util/IndentingPrintWriter;Z)V
    //   661: aload 16
    //   663: invokevirtual 1130	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   666: aconst_null
    //   667: astore_1
    //   668: aconst_null
    //   669: astore_3
    //   670: new 1146	java/io/RandomAccessFile
    //   673: dup
    //   674: ldc_w 1148
    //   677: ldc_w 1150
    //   680: invokespecial 1152	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   683: astore_2
    //   684: aload_2
    //   685: invokevirtual 1155	java/io/RandomAccessFile:readLine	()Ljava/lang/String;
    //   688: astore_1
    //   689: aload_1
    //   690: ifnull +29 -> 719
    //   693: aload 16
    //   695: ldc_w 1157
    //   698: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   701: aload_1
    //   702: ifnull +17 -> 719
    //   705: aload 16
    //   707: aload_1
    //   708: invokevirtual 1088	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   711: aload_2
    //   712: invokevirtual 1155	java/io/RandomAccessFile:readLine	()Ljava/lang/String;
    //   715: astore_1
    //   716: goto -15 -> 701
    //   719: aload_2
    //   720: ifnull +7 -> 727
    //   723: aload_2
    //   724: invokevirtual 1160	java/io/RandomAccessFile:close	()V
    //   727: aload 15
    //   729: monitorexit
    //   730: return
    //   731: astore_1
    //   732: aload_1
    //   733: invokevirtual 1163	java/io/IOException:printStackTrace	()V
    //   736: goto -9 -> 727
    //   739: astore_1
    //   740: aload 15
    //   742: monitorexit
    //   743: aload_1
    //   744: athrow
    //   745: astore_1
    //   746: aload_3
    //   747: astore_2
    //   748: aload_1
    //   749: astore_3
    //   750: aload_2
    //   751: astore_1
    //   752: aload_3
    //   753: invokevirtual 1163	java/io/IOException:printStackTrace	()V
    //   756: aload_2
    //   757: ifnull -30 -> 727
    //   760: aload_2
    //   761: invokevirtual 1160	java/io/RandomAccessFile:close	()V
    //   764: goto -37 -> 727
    //   767: astore_1
    //   768: aload_1
    //   769: invokevirtual 1163	java/io/IOException:printStackTrace	()V
    //   772: goto -45 -> 727
    //   775: astore_3
    //   776: aload_1
    //   777: astore_2
    //   778: aload_3
    //   779: astore_1
    //   780: aload_2
    //   781: ifnull +7 -> 788
    //   784: aload_2
    //   785: invokevirtual 1160	java/io/RandomAccessFile:close	()V
    //   788: aload_1
    //   789: athrow
    //   790: astore_2
    //   791: aload_2
    //   792: invokevirtual 1163	java/io/IOException:printStackTrace	()V
    //   795: goto -7 -> 788
    //   798: astore_1
    //   799: goto -19 -> 780
    //   802: astore_3
    //   803: goto -53 -> 750
    //   806: astore 15
    //   808: lload 6
    //   810: lstore 8
    //   812: goto -733 -> 79
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	815	0	this	NetworkStatsService
    //   0	815	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	815	2	paramPrintWriter	java.io.PrintWriter
    //   0	815	3	paramArrayOfString	String[]
    //   26	505	4	i	int
    //   30	7	5	j	int
    //   15	794	6	l1	long
    //   54	757	8	l2	long
    //   109	116	10	bool1	boolean
    //   137	520	11	bool2	boolean
    //   156	443	12	bool3	boolean
    //   175	458	13	bool4	boolean
    //   118	130	14	bool5	boolean
    //   43	698	15	localObject	Object
    //   806	1	15	localNumberFormatException	NumberFormatException
    //   188	518	16	localIndentingPrintWriter	com.android.internal.util.IndentingPrintWriter
    // Exception table:
    //   from	to	target	type
    //   723	727	731	java/io/IOException
    //   204	219	739	finally
    //   252	257	739	finally
    //   264	325	739	finally
    //   330	349	739	finally
    //   354	374	739	finally
    //   378	391	739	finally
    //   394	445	739	finally
    //   454	472	739	finally
    //   475	526	739	finally
    //   535	598	739	finally
    //   603	632	739	finally
    //   637	666	739	finally
    //   723	727	739	finally
    //   732	736	739	finally
    //   760	764	739	finally
    //   768	772	739	finally
    //   784	788	739	finally
    //   788	790	739	finally
    //   791	795	739	finally
    //   670	684	745	java/io/IOException
    //   760	764	767	java/io/IOException
    //   670	684	775	finally
    //   752	756	775	finally
    //   784	788	790	java/io/IOException
    //   684	689	798	finally
    //   693	701	798	finally
    //   705	716	798	finally
    //   684	689	802	java/io/IOException
    //   693	701	802	java/io/IOException
    //   705	716	802	java/io/IOException
    //   67	79	806	java/lang/NumberFormatException
  }
  
  public void forceUpdate()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_NETWORK_USAGE_HISTORY", "NetworkStats");
    assertBandwidthControlEnabled();
    long l = Binder.clearCallingIdentity();
    try
    {
      performPoll(3);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void forceUpdateIfaces()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_NETWORK_USAGE_HISTORY", "NetworkStats");
    assertBandwidthControlEnabled();
    long l = Binder.clearCallingIdentity();
    try
    {
      updateIfaces();
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public NetworkStats getDataLayerSnapshotForUid(int paramInt)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 469	android/os/Binder:getCallingUid	()I
    //   3: iload_1
    //   4: if_icmpeq +15 -> 19
    //   7: aload_0
    //   8: getfield 153	com/android/server/net/NetworkStatsService:mContext	Landroid/content/Context;
    //   11: ldc_w 1168
    //   14: ldc 75
    //   16: invokevirtual 1015	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   19: aload_0
    //   20: invokespecial 548	com/android/server/net/NetworkStatsService:assertBandwidthControlEnabled	()V
    //   23: invokestatic 551	android/os/Binder:clearCallingIdentity	()J
    //   26: lstore_2
    //   27: aload_0
    //   28: getfield 314	com/android/server/net/NetworkStatsService:mNetworkManager	Landroid/os/INetworkManagementService;
    //   31: iload_1
    //   32: invokeinterface 633 2 0
    //   37: astore 5
    //   39: lload_2
    //   40: invokestatic 554	android/os/Binder:restoreCallingIdentity	(J)V
    //   43: aload 5
    //   45: aload_0
    //   46: getfield 270	com/android/server/net/NetworkStatsService:mUidOperations	Landroid/net/NetworkStats;
    //   49: invokevirtual 1171	android/net/NetworkStats:spliceOperationsFrom	(Landroid/net/NetworkStats;)V
    //   52: new 265	android/net/NetworkStats
    //   55: dup
    //   56: aload 5
    //   58: invokevirtual 1174	android/net/NetworkStats:getElapsedRealtime	()J
    //   61: aload 5
    //   63: invokevirtual 1175	android/net/NetworkStats:size	()I
    //   66: invokespecial 268	android/net/NetworkStats:<init>	(JI)V
    //   69: astore 6
    //   71: aconst_null
    //   72: astore 4
    //   74: iconst_0
    //   75: istore_1
    //   76: iload_1
    //   77: aload 5
    //   79: invokevirtual 1175	android/net/NetworkStats:size	()I
    //   82: if_icmpge +45 -> 127
    //   85: aload 5
    //   87: iload_1
    //   88: aload 4
    //   90: invokevirtual 1179	android/net/NetworkStats:getValues	(ILandroid/net/NetworkStats$Entry;)Landroid/net/NetworkStats$Entry;
    //   93: astore 4
    //   95: aload 4
    //   97: getstatic 1182	android/net/NetworkStats:IFACE_ALL	Ljava/lang/String;
    //   100: putfield 582	android/net/NetworkStats$Entry:iface	Ljava/lang/String;
    //   103: aload 6
    //   105: aload 4
    //   107: invokevirtual 482	android/net/NetworkStats:combineValues	(Landroid/net/NetworkStats$Entry;)Landroid/net/NetworkStats;
    //   110: pop
    //   111: iload_1
    //   112: iconst_1
    //   113: iadd
    //   114: istore_1
    //   115: goto -39 -> 76
    //   118: astore 4
    //   120: lload_2
    //   121: invokestatic 554	android/os/Binder:restoreCallingIdentity	(J)V
    //   124: aload 4
    //   126: athrow
    //   127: aload 6
    //   129: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	NetworkStatsService
    //   0	130	1	paramInt	int
    //   26	95	2	l	long
    //   72	34	4	localEntry	NetworkStats.Entry
    //   118	7	4	localObject	Object
    //   37	49	5	localNetworkStats1	NetworkStats
    //   69	59	6	localNetworkStats2	NetworkStats
    // Exception table:
    //   from	to	target	type
    //   27	39	118	finally
  }
  
  public String[] getMobileIfaces()
  {
    return this.mMobileIfaces;
  }
  
  public long getNetworkTotalBytes(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.READ_NETWORK_USAGE_HISTORY", "NetworkStats");
    assertBandwidthControlEnabled();
    return internalGetSummaryForNetwork(paramNetworkTemplate, paramLong1, paramLong2, 3).getTotalBytes();
  }
  
  public void incrementOperationCount(int paramInt1, int paramInt2, int paramInt3)
  {
    if (Binder.getCallingUid() != paramInt1) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.MODIFY_NETWORK_ACCOUNTING", "NetworkStats");
    }
    if (paramInt3 < 0) {
      throw new IllegalArgumentException("operation count can only be incremented");
    }
    if (paramInt2 == 0) {
      throw new IllegalArgumentException("operation count must have specific tag");
    }
    synchronized (this.mStatsLock)
    {
      int i = this.mActiveUidCounterSet.get(paramInt1, 0);
      this.mUidOperations.combineValues(this.mActiveIface, paramInt1, i, paramInt2, 0L, 0L, 0L, 0L, paramInt3);
      this.mUidOperations.combineValues(this.mActiveIface, paramInt1, i, 0, 0L, 0L, 0L, 0L, paramInt3);
      return;
    }
  }
  
  public INetworkStatsSession openSession()
  {
    return createSession(null, false);
  }
  
  public INetworkStatsSession openSessionForUsageStats(String paramString)
  {
    return createSession(paramString, true);
  }
  
  public NetworkStats peekTetherStats()
  {
    return NetPluginDelegate.peekTetherStats();
  }
  
  public void recordVideoCallData(String arg1, int paramInt, long paramLong1, long paramLong2)
  {
    Log.d("NetworkStats", "recordVideoCallData  service ifaceType = " + paramInt + " iface = " + ??? + " rxBytes = " + paramLong1 + "txBytes = " + paramLong2);
    if (paramInt == 0)
    {
      if (this.mVideoCallMobileDataEntry == null) {
        this.mVideoCallMobileDataEntry = createVideoCallDataEntry(???);
      }
      synchronized (this.mVideoCallMobileDataEntry)
      {
        NetworkStats.Entry localEntry1 = this.mVideoCallMobileDataEntry;
        localEntry1.rxBytes += paramLong1;
        localEntry1 = this.mVideoCallMobileDataEntry;
        localEntry1.txBytes += paramLong2;
      }
    }
    NetworkStats.Entry localEntry2;
    synchronized (this.mStatsLock)
    {
      performPollLocked(3);
      return;
      localObject1 = finally;
      throw ((Throwable)localObject1);
      if (paramInt == 1) {
        if (this.mVideoCallWifiDataEntry == null) {
          this.mVideoCallWifiDataEntry = createVideoCallDataEntry(???);
        }
      }
    }
  }
  
  public DataUsageRequest registerUsageCallback(String paramString, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder)
  {
    Preconditions.checkNotNull(paramString, "calling package is null");
    Preconditions.checkNotNull(paramDataUsageRequest, "DataUsageRequest is null");
    Preconditions.checkNotNull(paramDataUsageRequest.template, "NetworkTemplate is null");
    Preconditions.checkNotNull(paramMessenger, "messenger is null");
    Preconditions.checkNotNull(paramIBinder, "binder is null");
    int i = Binder.getCallingUid();
    int j = checkAccessLevel(paramString);
    long l = Binder.clearCallingIdentity();
    try
    {
      paramString = this.mStatsObservers.register(paramDataUsageRequest, paramMessenger, paramIBinder, i, j);
      Binder.restoreCallingIdentity(l);
      this.mHandler.sendMessage(this.mHandler.obtainMessage(1, Integer.valueOf(3)));
      return paramString;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  void setHandler(Handler paramHandler, Handler.Callback paramCallback)
  {
    this.mHandler = paramHandler;
    this.mHandlerCallback = paramCallback;
  }
  
  /* Error */
  public void setUidForeground(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 153	com/android/server/net/NetworkStatsService:mContext	Landroid/content/Context;
    //   4: ldc_w 1011
    //   7: ldc 75
    //   9: invokevirtual 1015	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 170	com/android/server/net/NetworkStatsService:mStatsLock	Ljava/lang/Object;
    //   16: astore 4
    //   18: aload 4
    //   20: monitorenter
    //   21: iload_2
    //   22: ifeq +36 -> 58
    //   25: iconst_1
    //   26: istore_3
    //   27: aload_0
    //   28: getfield 263	com/android/server/net/NetworkStatsService:mActiveUidCounterSet	Landroid/util/SparseIntArray;
    //   31: iload_1
    //   32: iconst_0
    //   33: invokevirtual 1200	android/util/SparseIntArray:get	(II)I
    //   36: iload_3
    //   37: if_icmpeq +17 -> 54
    //   40: aload_0
    //   41: getfield 263	com/android/server/net/NetworkStatsService:mActiveUidCounterSet	Landroid/util/SparseIntArray;
    //   44: iload_1
    //   45: iload_3
    //   46: invokevirtual 1273	android/util/SparseIntArray:put	(II)V
    //   49: iload_1
    //   50: iload_3
    //   51: invokestatic 1276	com/android/server/NetworkManagementSocketTagger:setKernelCounterSet	(II)V
    //   54: aload 4
    //   56: monitorexit
    //   57: return
    //   58: iconst_0
    //   59: istore_3
    //   60: goto -33 -> 27
    //   63: astore 5
    //   65: aload 4
    //   67: monitorexit
    //   68: aload 5
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	NetworkStatsService
    //   0	71	1	paramInt	int
    //   0	71	2	paramBoolean	boolean
    //   26	34	3	i	int
    //   16	50	4	localObject1	Object
    //   63	6	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   27	54	63	finally
  }
  
  public void systemReady()
  {
    this.mSystemReady = true;
    if (!isBandwidthControlEnabled())
    {
      Slog.w("NetworkStats", "bandwidth controls disabled, unable to track stats");
      return;
    }
    this.mDevRecorder = buildRecorder("dev", this.mSettings.getDevConfig(), false);
    this.mXtRecorder = buildRecorder("xt", this.mSettings.getXtConfig(), false);
    this.mUidRecorder = buildRecorder("uid", this.mSettings.getUidConfig(), false);
    this.mUidTagRecorder = buildRecorder("uid_tag", this.mSettings.getUidTagConfig(), true);
    updatePersistThresholds();
    synchronized (this.mStatsLock)
    {
      maybeUpgradeLegacyStatsLocked();
      this.mXtStatsCached = this.mXtRecorder.getOrLoadCompleteLocked();
      bootstrapStatsLocked();
      ??? = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
      this.mContext.registerReceiver(this.mTetherReceiver, (IntentFilter)???, null, this.mHandler);
      ??? = new IntentFilter("com.android.server.action.NETWORK_STATS_POLL");
      this.mContext.registerReceiver(this.mPollReceiver, (IntentFilter)???, "android.permission.READ_NETWORK_USAGE_HISTORY", this.mHandler);
      ??? = new IntentFilter("android.intent.action.UID_REMOVED");
      this.mContext.registerReceiver(this.mRemovedReceiver, (IntentFilter)???, null, this.mHandler);
      ??? = new IntentFilter("android.intent.action.USER_REMOVED");
      this.mContext.registerReceiver(this.mUserReceiver, (IntentFilter)???, null, this.mHandler);
      ??? = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
      this.mContext.registerReceiver(this.mShutdownReceiver, (IntentFilter)???);
    }
    try
    {
      this.mNetworkManager.registerObserver(this.mAlertObserver);
      registerPollAlarmLocked();
      registerGlobalAlert();
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void unregisterUsageRequest(DataUsageRequest paramDataUsageRequest)
  {
    Preconditions.checkNotNull(paramDataUsageRequest, "DataUsageRequest is null");
    int i = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mStatsObservers.unregister(paramDataUsageRequest, i);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private static class DefaultNetworkStatsSettings
    implements NetworkStatsService.NetworkStatsSettings
  {
    private final ContentResolver mResolver;
    
    public DefaultNetworkStatsSettings(Context paramContext)
    {
      this.mResolver = ((ContentResolver)Preconditions.checkNotNull(paramContext.getContentResolver()));
    }
    
    private boolean getGlobalBoolean(String paramString, boolean paramBoolean)
    {
      boolean bool = false;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        paramBoolean = bool;
        if (Settings.Global.getInt(this.mResolver, paramString, i) != 0) {
          paramBoolean = true;
        }
        return paramBoolean;
      }
    }
    
    private long getGlobalLong(String paramString, long paramLong)
    {
      return Settings.Global.getLong(this.mResolver, paramString, paramLong);
    }
    
    public NetworkStatsService.NetworkStatsSettings.Config getDevConfig()
    {
      return new NetworkStatsService.NetworkStatsSettings.Config(getGlobalLong("netstats_dev_bucket_duration", 3600000L), getGlobalLong("netstats_dev_rotate_age", 1296000000L), getGlobalLong("netstats_dev_delete_age", 7776000000L));
    }
    
    public long getDevPersistBytes(long paramLong)
    {
      return getGlobalLong("netstats_dev_persist_bytes", paramLong);
    }
    
    public long getGlobalAlertBytes(long paramLong)
    {
      return getGlobalLong("netstats_global_alert_bytes", paramLong);
    }
    
    public long getPollInterval()
    {
      return getGlobalLong("netstats_poll_interval", 1800000L);
    }
    
    public boolean getSampleEnabled()
    {
      return getGlobalBoolean("netstats_sample_enabled", true);
    }
    
    public long getTimeCacheMaxAge()
    {
      return getGlobalLong("netstats_time_cache_max_age", 86400000L);
    }
    
    public NetworkStatsService.NetworkStatsSettings.Config getUidConfig()
    {
      return new NetworkStatsService.NetworkStatsSettings.Config(getGlobalLong("netstats_uid_bucket_duration", 7200000L), getGlobalLong("netstats_uid_rotate_age", 1296000000L), getGlobalLong("netstats_uid_delete_age", 7776000000L));
    }
    
    public long getUidPersistBytes(long paramLong)
    {
      return getGlobalLong("netstats_uid_persist_bytes", paramLong);
    }
    
    public NetworkStatsService.NetworkStatsSettings.Config getUidTagConfig()
    {
      return new NetworkStatsService.NetworkStatsSettings.Config(getGlobalLong("netstats_uid_tag_bucket_duration", 7200000L), getGlobalLong("netstats_uid_tag_rotate_age", 432000000L), getGlobalLong("netstats_uid_tag_delete_age", 1296000000L));
    }
    
    public long getUidTagPersistBytes(long paramLong)
    {
      return getGlobalLong("netstats_uid_tag_persist_bytes", paramLong);
    }
    
    public NetworkStatsService.NetworkStatsSettings.Config getXtConfig()
    {
      return getDevConfig();
    }
    
    public long getXtPersistBytes(long paramLong)
    {
      return getDevPersistBytes(paramLong);
    }
  }
  
  private class DropBoxNonMonotonicObserver
    implements NetworkStats.NonMonotonicObserver<String>
  {
    private DropBoxNonMonotonicObserver() {}
    
    public void foundNonMonotonic(NetworkStats paramNetworkStats1, int paramInt1, NetworkStats paramNetworkStats2, int paramInt2, String paramString)
    {
      Log.w("NetworkStats", "found non-monotonic values; saving to dropbox");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("found non-monotonic ").append(paramString).append(" values at left[").append(paramInt1).append("] - right[").append(paramInt2).append("]\n");
      localStringBuilder.append("left=").append(paramNetworkStats1).append('\n');
      localStringBuilder.append("right=").append(paramNetworkStats2).append('\n');
      ((DropBoxManager)NetworkStatsService.-get0(NetworkStatsService.this).getSystemService("dropbox")).addText("netstats_error", localStringBuilder.toString());
    }
  }
  
  static class HandlerCallback
    implements Handler.Callback
  {
    private final NetworkStatsService mService;
    
    HandlerCallback(NetworkStatsService paramNetworkStatsService)
    {
      this.mService = paramNetworkStatsService;
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 1: 
        int i = paramMessage.arg1;
        NetworkStatsService.-wrap3(this.mService, i);
        return true;
      case 2: 
        NetworkStatsService.-wrap8(this.mService);
        return true;
      }
      NetworkStatsService.-wrap4(this.mService);
      return true;
    }
  }
  
  public static abstract interface NetworkStatsSettings
  {
    public abstract Config getDevConfig();
    
    public abstract long getDevPersistBytes(long paramLong);
    
    public abstract long getGlobalAlertBytes(long paramLong);
    
    public abstract long getPollInterval();
    
    public abstract boolean getSampleEnabled();
    
    public abstract long getTimeCacheMaxAge();
    
    public abstract Config getUidConfig();
    
    public abstract long getUidPersistBytes(long paramLong);
    
    public abstract Config getUidTagConfig();
    
    public abstract long getUidTagPersistBytes(long paramLong);
    
    public abstract Config getXtConfig();
    
    public abstract long getXtPersistBytes(long paramLong);
    
    public static class Config
    {
      public final long bucketDuration;
      public final long deleteAgeMillis;
      public final long rotateAgeMillis;
      
      public Config(long paramLong1, long paramLong2, long paramLong3)
      {
        this.bucketDuration = paramLong1;
        this.rotateAgeMillis = paramLong2;
        this.deleteAgeMillis = paramLong3;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */