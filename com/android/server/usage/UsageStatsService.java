package com.android.server.usage;

import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.ConfigurationStats;
import android.app.usage.IUsageStatsManager.Stub;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.app.usage.UsageStatsManagerInternal.AppIdleStateChangeListener;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.net.NetworkScoreManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.view.Display;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UsageStatsService
  extends SystemService
  implements UserUsageStatsService.StatsUpdatedListener
{
  static final boolean COMPRESS_TIME = false;
  static final boolean DEBUG = false;
  private static final long FLUSH_INTERVAL = 1200000L;
  static final int MSG_CHECK_IDLE_STATES = 5;
  static final int MSG_CHECK_PAROLE_TIMEOUT = 6;
  static final int MSG_FLUSH_TO_DISK = 1;
  static final int MSG_FORCE_IDLE_STATE = 4;
  static final int MSG_INFORM_LISTENERS = 3;
  static final int MSG_ONE_TIME_CHECK_IDLE_STATES = 10;
  static final int MSG_PAROLE_END_TIMEOUT = 7;
  static final int MSG_PAROLE_STATE_CHANGED = 9;
  static final int MSG_REMOVE_USER = 2;
  static final int MSG_REPORT_CONTENT_PROVIDER_USAGE = 8;
  static final int MSG_REPORT_EVENT = 0;
  private static final long ONE_MINUTE = 60000L;
  static final String TAG = "UsageStatsService";
  private static final long TEN_SECONDS = 10000L;
  private static final long TIME_CHANGE_THRESHOLD_MILLIS = 2000L;
  private static final long TWENTY_MINUTES = 1200000L;
  boolean mAppIdleEnabled;
  @GuardedBy("mLock")
  private AppIdleHistory mAppIdleHistory;
  long mAppIdleParoleDurationMillis;
  long mAppIdleParoleIntervalMillis;
  long mAppIdleScreenThresholdMillis;
  boolean mAppIdleTempParoled;
  long mAppIdleWallclockThresholdMillis;
  AppOpsManager mAppOps;
  AppWidgetManager mAppWidgetManager;
  private IBatteryStats mBatteryStats;
  private List<String> mCarrierPrivilegedApps;
  boolean mCharging;
  long mCheckIdleIntervalMillis;
  IDeviceIdleController mDeviceIdleController;
  private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
  {
    public void onDisplayAdded(int paramAnonymousInt) {}
    
    public void onDisplayChanged(int paramAnonymousInt)
    {
      boolean bool;
      if (paramAnonymousInt == 0) {
        bool = UsageStatsService.-wrap1(UsageStatsService.this);
      }
      synchronized (UsageStatsService.-get1(UsageStatsService.this))
      {
        UsageStatsService.-get0(UsageStatsService.this).updateDisplayLocked(bool, SystemClock.elapsedRealtime());
        return;
      }
    }
    
    public void onDisplayRemoved(int paramAnonymousInt) {}
  };
  private DisplayManager mDisplayManager;
  Handler mHandler;
  private boolean mHaveCarrierPrivilegedApps;
  private long mLastAppIdleParoledTime;
  private final Object mLock = new Object();
  private ArrayList<UsageStatsManagerInternal.AppIdleStateChangeListener> mPackageAccessListeners = new ArrayList();
  PackageManager mPackageManager;
  private volatile boolean mPendingOneTimeCheckIdleStates;
  private PowerManager mPowerManager;
  long mRealTimeSnapshot;
  private boolean mSystemServicesReady = false;
  long mSystemTimeSnapshot;
  private File mUsageStatsDir;
  UserManager mUserManager;
  private final SparseArray<UserUsageStatsService> mUserState = new SparseArray();
  
  public UsageStatsService(Context paramContext)
  {
    super(paramContext);
  }
  
  private long checkAndGetTimeLocked()
  {
    long l1 = System.currentTimeMillis();
    long l2 = SystemClock.elapsedRealtime();
    long l3 = l2 - this.mRealTimeSnapshot + this.mSystemTimeSnapshot;
    long l4 = l1 - l3;
    if (Math.abs(l4) > 2000L)
    {
      Slog.i("UsageStatsService", "Time changed in UsageStats by " + l4 / 1000L + " seconds");
      int j = this.mUserState.size();
      int i = 0;
      while (i < j)
      {
        ((UserUsageStatsService)this.mUserState.valueAt(i)).onTimeChanged(l3, l1);
        i += 1;
      }
      this.mRealTimeSnapshot = l2;
      this.mSystemTimeSnapshot = l1;
    }
    return l1;
  }
  
  private void cleanUpRemovedUsersLocked()
  {
    List localList = this.mUserManager.getUsers(true);
    if ((localList == null) || (localList.size() == 0)) {
      throw new IllegalStateException("There can't be no users");
    }
    ArraySet localArraySet = new ArraySet();
    String[] arrayOfString = this.mUsageStatsDir.list();
    if (arrayOfString == null) {
      return;
    }
    localArraySet.addAll(Arrays.asList(arrayOfString));
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      localArraySet.remove(Integer.toString(((UserInfo)localList.get(i)).id));
      i += 1;
    }
    j = localArraySet.size();
    i = 0;
    while (i < j)
    {
      deleteRecursively(new File(this.mUsageStatsDir, (String)localArraySet.valueAt(i)));
      i += 1;
    }
  }
  
  private void convertToSystemTimeLocked(UsageEvents.Event paramEvent)
  {
    paramEvent.mTimeStamp = (Math.max(0L, paramEvent.mTimeStamp - this.mRealTimeSnapshot) + this.mSystemTimeSnapshot);
  }
  
  private static void deleteRecursively(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
    {
      int i = 0;
      int j = arrayOfFile.length;
      while (i < j)
      {
        deleteRecursively(arrayOfFile[i]);
        i += 1;
      }
    }
    if (!paramFile.delete()) {
      Slog.e("UsageStatsService", "Failed to delete " + paramFile);
    }
  }
  
  private void fetchCarrierPrivilegedAppsLocked()
  {
    this.mCarrierPrivilegedApps = ((TelephonyManager)getContext().getSystemService(TelephonyManager.class)).getPackagesWithCarrierPrivileges();
    this.mHaveCarrierPrivilegedApps = true;
  }
  
  private void flushToDiskLocked()
  {
    int j = this.mUserState.size();
    int i = 0;
    while (i < j)
    {
      ((UserUsageStatsService)this.mUserState.valueAt(i)).persistActiveStats();
      this.mAppIdleHistory.writeAppIdleTimesLocked(this.mUserState.keyAt(i));
      i += 1;
    }
    this.mAppIdleHistory.writeAppIdleDurationsLocked();
    this.mHandler.removeMessages(1);
  }
  
  private UserUsageStatsService getUserDataAndInitializeIfNeededLocked(int paramInt, long paramLong)
  {
    UserUsageStatsService localUserUsageStatsService2 = (UserUsageStatsService)this.mUserState.get(paramInt);
    UserUsageStatsService localUserUsageStatsService1 = localUserUsageStatsService2;
    if (localUserUsageStatsService2 == null)
    {
      localUserUsageStatsService1 = new UserUsageStatsService(getContext(), paramInt, new File(this.mUsageStatsDir, Integer.toString(paramInt)), this);
      localUserUsageStatsService1.init(paramLong);
      this.mUserState.put(paramInt, localUserUsageStatsService1);
    }
    return localUserUsageStatsService1;
  }
  
  private void initializeDefaultsForSystemApps(int paramInt)
  {
    Slog.d("UsageStatsService", "Initializing defaults for system apps on user " + paramInt);
    long l = SystemClock.elapsedRealtime();
    List localList = this.mPackageManager.getInstalledPackagesAsUser(512, paramInt);
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      PackageInfo localPackageInfo = (PackageInfo)localList.get(i);
      String str = localPackageInfo.packageName;
      if ((localPackageInfo.applicationInfo != null) && (localPackageInfo.applicationInfo.isSystemApp())) {
        this.mAppIdleHistory.reportUsageLocked(str, paramInt, l);
      }
      i += 1;
    }
  }
  
  private boolean isActiveDeviceAdmin(String paramString, int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getContext().getSystemService(DevicePolicyManager.class);
    if (localDevicePolicyManager == null) {
      return false;
    }
    return localDevicePolicyManager.packageHasActiveAdmins(paramString, paramInt);
  }
  
  private boolean isActiveNetworkScorer(String paramString)
  {
    NetworkScoreManager localNetworkScoreManager = (NetworkScoreManager)getContext().getSystemService("network_score");
    if (paramString != null) {
      return paramString.equals(localNetworkScoreManager.getActiveScorerPackage());
    }
    return false;
  }
  
  private boolean isAppIdleFiltered(String paramString, int paramInt1, int paramInt2, long paramLong)
  {
    if (paramString == null) {
      return false;
    }
    if (!this.mAppIdleEnabled) {
      return false;
    }
    if (paramInt1 < 10000) {
      return false;
    }
    if (paramString.equals("android")) {
      return false;
    }
    if (this.mSystemServicesReady)
    {
      try
      {
        boolean bool = this.mDeviceIdleController.isPowerSaveWhitelistExceptIdleApp(paramString);
        if (bool) {
          return false;
        }
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
      if (isActiveDeviceAdmin(paramString, paramInt2)) {
        return false;
      }
      if (isActiveNetworkScorer(paramString)) {
        return false;
      }
      if ((this.mAppWidgetManager != null) && (this.mAppWidgetManager.isBoundWidgetPackage(paramString, paramInt2))) {
        return false;
      }
      if (isDeviceProvisioningPackage(paramString)) {
        return false;
      }
    }
    if (!isAppIdleUnfiltered(paramString, paramInt2, paramLong)) {
      return false;
    }
    return !isCarrierApp(paramString);
  }
  
  private boolean isAppIdleUnfiltered(String paramString, int paramInt, long paramLong)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mAppIdleHistory.isIdleLocked(paramString, paramInt, paramLong);
      return bool;
    }
  }
  
  private boolean isCarrierApp(String paramString)
  {
    synchronized (this.mLock)
    {
      if (!this.mHaveCarrierPrivilegedApps) {
        fetchCarrierPrivilegedAppsLocked();
      }
      if (this.mCarrierPrivilegedApps != null)
      {
        boolean bool = this.mCarrierPrivilegedApps.contains(paramString);
        return bool;
      }
      return false;
    }
  }
  
  private boolean isDeviceProvisioningPackage(String paramString)
  {
    String str = getContext().getResources().getString(17039486);
    if (str != null) {
      return str.equals(paramString);
    }
    return false;
  }
  
  private boolean isDisplayOn()
  {
    boolean bool = false;
    if (this.mDisplayManager.getDisplay(0).getState() == 2) {
      bool = true;
    }
    return bool;
  }
  
  private void notifyBatteryStats(String paramString, int paramInt, boolean paramBoolean)
  {
    try
    {
      paramInt = this.mPackageManager.getPackageUidAsUser(paramString, 8192, paramInt);
      if (paramBoolean)
      {
        this.mBatteryStats.noteEvent(15, paramString, paramInt);
        return;
      }
      this.mBatteryStats.noteEvent(16, paramString, paramInt);
      return;
    }
    catch (PackageManager.NameNotFoundException|RemoteException paramString) {}
  }
  
  private void postNextParoleTimeout()
  {
    this.mHandler.removeMessages(6);
    long l2 = this.mLastAppIdleParoledTime + this.mAppIdleParoleIntervalMillis - checkAndGetTimeLocked();
    long l1 = l2;
    if (l2 < 0L) {
      l1 = 0L;
    }
    this.mHandler.sendEmptyMessageDelayed(6, l1);
  }
  
  private void postParoleEndTimeout()
  {
    this.mHandler.removeMessages(7);
    this.mHandler.sendEmptyMessageDelayed(7, this.mAppIdleParoleDurationMillis);
  }
  
  private void postParoleStateChanged()
  {
    this.mHandler.removeMessages(9);
    this.mHandler.sendEmptyMessage(9);
  }
  
  private static boolean validRange(long paramLong1, long paramLong2, long paramLong3)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramLong2 <= paramLong1)
    {
      bool1 = bool2;
      if (paramLong2 < paramLong3) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  void addListener(UsageStatsManagerInternal.AppIdleStateChangeListener paramAppIdleStateChangeListener)
  {
    synchronized (this.mLock)
    {
      if (!this.mPackageAccessListeners.contains(paramAppIdleStateChangeListener)) {
        this.mPackageAccessListeners.add(paramAppIdleStateChangeListener);
      }
      return;
    }
  }
  
  boolean checkIdleStates(int paramInt)
  {
    if (!this.mAppIdleEnabled) {
      return false;
    }
    boolean bool;
    long l;
    int m;
    try
    {
      int[] arrayOfInt = ActivityManagerNative.getDefault().getRunningUserIds();
      if (paramInt != -1)
      {
        bool = ArrayUtils.contains(arrayOfInt, paramInt);
        if (!bool) {}
      }
      else
      {
        l = SystemClock.elapsedRealtime();
        int i = 0;
        for (;;)
        {
          if (i >= arrayOfInt.length) {
            break label248;
          }
          m = arrayOfInt[i];
          if ((paramInt == -1) || (paramInt == m)) {
            break;
          }
          i += 1;
        }
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    List localList = this.mPackageManager.getInstalledPackagesAsUser(512, m);
    int n = localList.size();
    int j = 0;
    while (j < n)
    {
      ??? = (PackageInfo)localList.get(j);
      String str = ((PackageInfo)???).packageName;
      bool = isAppIdleFiltered(str, UserHandle.getAppId(((PackageInfo)???).applicationInfo.uid), m, l);
      ??? = this.mHandler;
      Handler localHandler = this.mHandler;
      int k;
      if (bool)
      {
        k = 1;
        ((Handler)???).sendMessage(localHandler.obtainMessage(3, m, k, str));
        if (!bool) {}
      }
      synchronized (this.mLock)
      {
        this.mAppIdleHistory.setIdle(str, m, l);
        j += 1;
        continue;
        k = 0;
      }
    }
    label248:
    return true;
  }
  
  void checkParoleTimeout()
  {
    synchronized (this.mLock)
    {
      if (!this.mAppIdleTempParoled)
      {
        if (checkAndGetTimeLocked() - this.mLastAppIdleParoledTime > this.mAppIdleParoleIntervalMillis) {
          setAppIdleParoled(true);
        }
      }
      else {
        return;
      }
      postNextParoleTimeout();
    }
  }
  
  void clearAppIdleForPackage(String paramString, int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mAppIdleHistory.clearUsageLocked(paramString, paramInt);
      return;
    }
  }
  
  void clearCarrierPrivilegedApps()
  {
    synchronized (this.mLock)
    {
      this.mHaveCarrierPrivilegedApps = false;
      this.mCarrierPrivilegedApps = null;
      return;
    }
  }
  
  void dump(String[] paramArrayOfString, PrintWriter paramPrintWriter)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        IndentingPrintWriter localIndentingPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
        ArraySet localArraySet = new ArraySet();
        localArraySet.addAll(Arrays.asList(paramArrayOfString));
        int j = this.mUserState.size();
        int i = 0;
        if (i >= j) {
          break;
        }
        localIndentingPrintWriter.printPair("user", Integer.valueOf(this.mUserState.keyAt(i)));
        localIndentingPrintWriter.println();
        localIndentingPrintWriter.increaseIndent();
        if (localArraySet.contains("--checkin"))
        {
          ((UserUsageStatsService)this.mUserState.valueAt(i)).checkin(localIndentingPrintWriter);
          this.mAppIdleHistory.dump(localIndentingPrintWriter, this.mUserState.keyAt(i));
          localIndentingPrintWriter.decreaseIndent();
          i += 1;
          continue;
        }
        ((UserUsageStatsService)this.mUserState.valueAt(i)).dump(localIndentingPrintWriter);
        localIndentingPrintWriter.println();
        if (paramArrayOfString.length <= 0) {
          continue;
        }
        if ("history".equals(paramArrayOfString[0])) {
          this.mAppIdleHistory.dumpHistory(localIndentingPrintWriter, this.mUserState.keyAt(i));
        }
      }
      if ("flush".equals(paramArrayOfString[0]))
      {
        flushToDiskLocked();
        paramPrintWriter.println("Flushed stats to disk");
      }
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Carrier privileged apps (have=" + this.mHaveCarrierPrivilegedApps + "): " + this.mCarrierPrivilegedApps);
    paramPrintWriter.println();
    paramPrintWriter.println("Settings:");
    paramPrintWriter.print("  mAppIdleDurationMillis=");
    TimeUtils.formatDuration(this.mAppIdleScreenThresholdMillis, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("  mAppIdleWallclockThresholdMillis=");
    TimeUtils.formatDuration(this.mAppIdleWallclockThresholdMillis, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("  mCheckIdleIntervalMillis=");
    TimeUtils.formatDuration(this.mCheckIdleIntervalMillis, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("  mAppIdleParoleIntervalMillis=");
    TimeUtils.formatDuration(this.mAppIdleParoleIntervalMillis, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("  mAppIdleParoleDurationMillis=");
    TimeUtils.formatDuration(this.mAppIdleParoleDurationMillis, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.println();
    paramPrintWriter.print("mAppIdleEnabled=");
    paramPrintWriter.print(this.mAppIdleEnabled);
    paramPrintWriter.print(" mAppIdleTempParoled=");
    paramPrintWriter.print(this.mAppIdleTempParoled);
    paramPrintWriter.print(" mCharging=");
    paramPrintWriter.print(this.mCharging);
    paramPrintWriter.print(" mLastAppIdleParoledTime=");
    TimeUtils.formatDuration(this.mLastAppIdleParoledTime, paramPrintWriter);
    paramPrintWriter.println();
  }
  
  void flushToDisk()
  {
    synchronized (this.mLock)
    {
      flushToDiskLocked();
      return;
    }
  }
  
  void forceIdleState(String paramString, int paramInt, boolean paramBoolean)
  {
    int i = getAppId(paramString);
    if (i < 0) {
      return;
    }
    synchronized (this.mLock)
    {
      long l = SystemClock.elapsedRealtime();
      boolean bool1 = isAppIdleFiltered(paramString, i, paramInt, l);
      this.mAppIdleHistory.setIdleLocked(paramString, paramInt, paramBoolean, l);
      boolean bool2 = isAppIdleFiltered(paramString, i, paramInt, l);
      if (bool1 != bool2)
      {
        Handler localHandler1 = this.mHandler;
        Handler localHandler2 = this.mHandler;
        if (!bool2) {
          break label122;
        }
        i = 1;
        localHandler1.sendMessage(localHandler2.obtainMessage(3, paramInt, i, paramString));
        if (!bool2) {
          notifyBatteryStats(paramString, paramInt, paramBoolean);
        }
      }
      return;
      label122:
      i = 0;
    }
  }
  
  int getAppId(String paramString)
  {
    try
    {
      int i = this.mPackageManager.getApplicationInfo(paramString, 8704).uid;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return -1;
  }
  
  int[] getIdleUidsForUser(int paramInt)
  {
    if (!this.mAppIdleEnabled) {
      return new int[0];
    }
    long l = SystemClock.elapsedRealtime();
    boolean bool;
    int k;
    for (;;)
    {
      try
      {
        Object localObject1 = AppGlobals.getPackageManager().getInstalledApplications(0, paramInt);
        if (localObject1 == null) {
          return new int[0];
        }
        localObject2 = ((ParceledListSlice)localObject1).getList();
        localObject1 = new SparseIntArray();
        i = ((List)localObject2).size() - 1;
        if (i < 0) {
          break label204;
        }
        ApplicationInfo localApplicationInfo = (ApplicationInfo)((List)localObject2).get(i);
        bool = isAppIdleFiltered(localApplicationInfo.packageName, UserHandle.getAppId(localApplicationInfo.uid), paramInt, l);
        k = ((SparseIntArray)localObject1).indexOfKey(localApplicationInfo.uid);
        if (k >= 0) {
          break;
        }
        k = localApplicationInfo.uid;
        if (bool)
        {
          j = 65536;
          ((SparseIntArray)localObject1).put(k, j + 1);
          i -= 1;
        }
        else
        {
          j = 0;
        }
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    int m = localRemoteException.valueAt(k);
    if (bool) {}
    for (int j = 65536;; j = 0)
    {
      localRemoteException.setValueAt(k, j + (m + 1));
      break;
    }
    label204:
    int i = 0;
    paramInt = localRemoteException.size() - 1;
    while (paramInt >= 0)
    {
      k = localRemoteException.valueAt(paramInt);
      j = i;
      if ((k & 0x7FFF) == k >> 16) {
        j = i + 1;
      }
      paramInt -= 1;
      i = j;
    }
    Object localObject2 = new int[i];
    i = 0;
    paramInt = localRemoteException.size() - 1;
    while (paramInt >= 0)
    {
      k = localRemoteException.valueAt(paramInt);
      j = i;
      if ((k & 0x7FFF) == k >> 16)
      {
        localObject2[i] = localRemoteException.keyAt(paramInt);
        j = i + 1;
      }
      paramInt -= 1;
      i = j;
    }
    return (int[])localObject2;
  }
  
  void informListeners(String paramString, int paramInt, boolean paramBoolean)
  {
    Iterator localIterator = this.mPackageAccessListeners.iterator();
    while (localIterator.hasNext()) {
      ((UsageStatsManagerInternal.AppIdleStateChangeListener)localIterator.next()).onAppIdleStateChanged(paramString, paramInt, paramBoolean);
    }
  }
  
  void informParoleStateChanged()
  {
    boolean bool = isParoledOrCharging();
    Iterator localIterator = this.mPackageAccessListeners.iterator();
    while (localIterator.hasNext()) {
      ((UsageStatsManagerInternal.AppIdleStateChangeListener)localIterator.next()).onParoleStateChanged(bool);
    }
  }
  
  boolean isAppIdleFilteredOrParoled(String paramString, int paramInt, long paramLong)
  {
    if (isParoledOrCharging()) {
      return false;
    }
    return isAppIdleFiltered(paramString, getAppId(paramString), paramInt, paramLong);
  }
  
  boolean isParoledOrCharging()
  {
    synchronized (this.mLock)
    {
      if (!this.mAppIdleTempParoled)
      {
        bool = this.mCharging;
        return bool;
      }
      boolean bool = true;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      ??? = new SettingsObserver(this.mHandler);
      ((SettingsObserver)???).registerObserver();
      ((SettingsObserver)???).updateSettings();
      this.mAppWidgetManager = ((AppWidgetManager)getContext().getSystemService(AppWidgetManager.class));
      this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
      this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
      this.mDisplayManager = ((DisplayManager)getContext().getSystemService("display"));
      this.mPowerManager = ((PowerManager)getContext().getSystemService(PowerManager.class));
      this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
    }
    while (paramInt != 1000) {
      synchronized (this.mLock)
      {
        this.mAppIdleHistory.updateDisplayLocked(isDisplayOn(), SystemClock.elapsedRealtime());
        if (this.mPendingOneTimeCheckIdleStates) {
          postOneTimeCheckIdleStates();
        }
        this.mSystemServicesReady = true;
        return;
      }
    }
    setChargingState(((BatteryManager)getContext().getSystemService(BatteryManager.class)).isCharging());
  }
  
  void onDeviceIdleModeChanged()
  {
    boolean bool = this.mPowerManager.isDeviceIdleMode();
    synchronized (this.mLock)
    {
      long l1 = checkAndGetTimeLocked();
      long l2 = this.mLastAppIdleParoledTime;
      if ((!bool) && (l1 - l2 >= this.mAppIdleParoleIntervalMillis)) {
        setAppIdleParoled(true);
      }
      while (!bool) {
        return;
      }
      setAppIdleParoled(false);
    }
  }
  
  public void onNewUpdate(int paramInt)
  {
    initializeDefaultsForSystemApps(paramInt);
  }
  
  public void onStart()
  {
    this.mAppOps = ((AppOpsManager)getContext().getSystemService("appops"));
    this.mUserManager = ((UserManager)getContext().getSystemService("user"));
    this.mPackageManager = getContext().getPackageManager();
    this.mHandler = new H(BackgroundThread.get().getLooper());
    this.mUsageStatsDir = new File(new File(Environment.getDataDirectory(), "system"), "usagestats");
    this.mUsageStatsDir.mkdirs();
    if (!this.mUsageStatsDir.exists()) {
      throw new IllegalStateException("Usage stats directory does not exist: " + this.mUsageStatsDir.getAbsolutePath());
    }
    ??? = new IntentFilter("android.intent.action.USER_REMOVED");
    ((IntentFilter)???).addAction("android.intent.action.USER_STARTED");
    getContext().registerReceiverAsUser(new UserActionsReceiver(null), UserHandle.ALL, (IntentFilter)???, null, this.mHandler);
    ??? = new IntentFilter();
    ((IntentFilter)???).addAction("android.intent.action.PACKAGE_ADDED");
    ((IntentFilter)???).addAction("android.intent.action.PACKAGE_CHANGED");
    ((IntentFilter)???).addAction("android.intent.action.PACKAGE_REMOVED");
    ((IntentFilter)???).addDataScheme("package");
    getContext().registerReceiverAsUser(new PackageReceiver(null), UserHandle.ALL, (IntentFilter)???, null, this.mHandler);
    this.mAppIdleEnabled = getContext().getResources().getBoolean(17956884);
    if (this.mAppIdleEnabled)
    {
      ??? = new IntentFilter("android.intent.action.BATTERY_CHANGED");
      ((IntentFilter)???).addAction("android.os.action.DISCHARGING");
      ((IntentFilter)???).addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
      getContext().registerReceiver(new DeviceStateReceiver(null), (IntentFilter)???);
    }
    synchronized (this.mLock)
    {
      cleanUpRemovedUsersLocked();
      this.mAppIdleHistory = new AppIdleHistory(SystemClock.elapsedRealtime());
      this.mRealTimeSnapshot = SystemClock.elapsedRealtime();
      this.mSystemTimeSnapshot = System.currentTimeMillis();
      publishLocalService(UsageStatsManagerInternal.class, new LocalService(null));
      publishBinderService("usagestats", new BinderService(null));
      return;
    }
  }
  
  public void onStatsReloaded()
  {
    postOneTimeCheckIdleStates();
  }
  
  public void onStatsUpdated()
  {
    this.mHandler.sendEmptyMessageDelayed(1, 1200000L);
  }
  
  void onUserRemoved(int paramInt)
  {
    synchronized (this.mLock)
    {
      Slog.i("UsageStatsService", "Removing user " + paramInt + " and all data.");
      this.mUserState.remove(paramInt);
      this.mAppIdleHistory.onUserRemoved(paramInt);
      cleanUpRemovedUsersLocked();
      return;
    }
  }
  
  void postCheckIdleStates(int paramInt)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramInt, 0));
  }
  
  void postOneTimeCheckIdleStates()
  {
    if (this.mDeviceIdleController == null)
    {
      this.mPendingOneTimeCheckIdleStates = true;
      return;
    }
    this.mHandler.sendEmptyMessage(10);
    this.mPendingOneTimeCheckIdleStates = false;
  }
  
  List<ConfigurationStats> queryConfigurationStats(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    synchronized (this.mLock)
    {
      long l = checkAndGetTimeLocked();
      boolean bool = validRange(l, paramLong1, paramLong2);
      if (!bool) {
        return null;
      }
      List localList = getUserDataAndInitializeIfNeededLocked(paramInt1, l).queryConfigurationStats(paramInt2, paramLong1, paramLong2);
      return localList;
    }
  }
  
  UsageEvents queryEvents(int paramInt, long paramLong1, long paramLong2)
  {
    synchronized (this.mLock)
    {
      long l = checkAndGetTimeLocked();
      boolean bool = validRange(l, paramLong1, paramLong2);
      if (!bool) {
        return null;
      }
      UsageEvents localUsageEvents = getUserDataAndInitializeIfNeededLocked(paramInt, l).queryEvents(paramLong1, paramLong2);
      return localUsageEvents;
    }
  }
  
  List<UsageStats> queryUsageStats(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    synchronized (this.mLock)
    {
      long l = checkAndGetTimeLocked();
      boolean bool = validRange(l, paramLong1, paramLong2);
      if (!bool) {
        return null;
      }
      List localList = getUserDataAndInitializeIfNeededLocked(paramInt1, l).queryUsageStats(paramInt2, paramLong1, paramLong2);
      return localList;
    }
  }
  
  void removeListener(UsageStatsManagerInternal.AppIdleStateChangeListener paramAppIdleStateChangeListener)
  {
    synchronized (this.mLock)
    {
      this.mPackageAccessListeners.remove(paramAppIdleStateChangeListener);
      return;
    }
  }
  
  void reportContentProviderUsage(String paramString1, String paramString2, int paramInt)
  {
    int i = 0;
    paramString1 = ContentResolver.getSyncAdapterPackagesForAuthorityAsUser(paramString1, paramInt);
    int j = paramString1.length;
    for (;;)
    {
      if (i < j)
      {
        String str = paramString1[i];
        try
        {
          PackageInfo localPackageInfo = this.mPackageManager.getPackageInfoAsUser(str, 1048576, paramInt);
          if ((localPackageInfo == null) || (localPackageInfo.applicationInfo == null) || (str.equals(paramString2))) {
            break label83;
          }
          forceIdleState(str, paramInt, false);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      }
      return;
      label83:
      i += 1;
    }
  }
  
  void reportEvent(UsageEvents.Event paramEvent, int paramInt)
  {
    synchronized (this.mLock)
    {
      long l1 = checkAndGetTimeLocked();
      long l2 = SystemClock.elapsedRealtime();
      convertToSystemTimeLocked(paramEvent);
      UserUsageStatsService localUserUsageStatsService = getUserDataAndInitializeIfNeededLocked(paramInt, l1);
      boolean bool = this.mAppIdleHistory.isIdleLocked(paramEvent.mPackage, paramInt, l2);
      localUserUsageStatsService.reportEvent(paramEvent);
      if ((paramEvent.mEventType == 1) || (paramEvent.mEventType == 2))
      {
        this.mAppIdleHistory.reportUsageLocked(paramEvent.mPackage, paramInt, l2);
        if (bool)
        {
          this.mHandler.sendMessage(this.mHandler.obtainMessage(3, paramInt, 0, paramEvent.mPackage));
          notifyBatteryStats(paramEvent.mPackage, paramInt, false);
        }
      }
      int i;
      do
      {
        return;
        if (paramEvent.mEventType == 6) {
          break;
        }
        i = paramEvent.mEventType;
      } while (i != 7);
    }
  }
  
  void setAppIdle(String paramString, boolean paramBoolean, int paramInt)
  {
    if (paramString == null) {
      return;
    }
    Handler localHandler = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(4, paramInt, i, paramString).sendToTarget();
      return;
    }
  }
  
  void setAppIdleParoled(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mAppIdleTempParoled != paramBoolean)
      {
        this.mAppIdleTempParoled = paramBoolean;
        if (paramBoolean)
        {
          postParoleEndTimeout();
          postParoleStateChanged();
        }
      }
      else
      {
        return;
      }
      this.mLastAppIdleParoledTime = checkAndGetTimeLocked();
      postNextParoleTimeout();
    }
  }
  
  void setChargingState(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mCharging != paramBoolean)
      {
        this.mCharging = paramBoolean;
        postParoleStateChanged();
      }
      return;
    }
  }
  
  void shutdown()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(0);
      flushToDiskLocked();
      return;
    }
  }
  
  private final class BinderService
    extends IUsageStatsManager.Stub
  {
    private BinderService() {}
    
    private boolean hasPermission(String paramString)
    {
      int i = Binder.getCallingUid();
      if (i == 1000) {
        return true;
      }
      i = UsageStatsService.this.mAppOps.checkOp(43, i, paramString);
      if (i == 3) {
        return UsageStatsService.this.getContext().checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") == 0;
      }
      return i == 0;
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (UsageStatsService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump UsageStats from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
        return;
      }
      UsageStatsService.this.dump(paramArrayOfString, paramPrintWriter);
    }
    
    /* Error */
    public boolean isAppInactive(String paramString, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 99	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
      //   3: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   6: invokestatic 28	android/os/Binder:getCallingUid	()I
      //   9: iload_2
      //   10: iconst_0
      //   11: iconst_1
      //   12: ldc 100
      //   14: aconst_null
      //   15: invokeinterface 106 8 0
      //   20: istore_2
      //   21: invokestatic 110	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore_3
      //   25: aload_0
      //   26: getfield 13	com/android/server/usage/UsageStatsService$BinderService:this$0	Lcom/android/server/usage/UsageStatsService;
      //   29: aload_1
      //   30: iload_2
      //   31: invokestatic 115	android/os/SystemClock:elapsedRealtime	()J
      //   34: invokevirtual 119	com/android/server/usage/UsageStatsService:isAppIdleFilteredOrParoled	(Ljava/lang/String;IJ)Z
      //   37: istore 5
      //   39: lload_3
      //   40: invokestatic 123	android/os/Binder:restoreCallingIdentity	(J)V
      //   43: iload 5
      //   45: ireturn
      //   46: astore_1
      //   47: aload_1
      //   48: invokevirtual 127	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   51: athrow
      //   52: astore_1
      //   53: lload_3
      //   54: invokestatic 123	android/os/Binder:restoreCallingIdentity	(J)V
      //   57: aload_1
      //   58: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	59	0	this	BinderService
      //   0	59	1	paramString	String
      //   0	59	2	paramInt	int
      //   24	30	3	l	long
      //   37	7	5	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   0	21	46	android/os/RemoteException
      //   25	39	52	finally
    }
    
    public void onCarrierPrivilegedAppsChanged()
    {
      UsageStatsService.this.getContext().enforceCallingOrSelfPermission("android.permission.BIND_CARRIER_SERVICES", "onCarrierPrivilegedAppsChanged can only be called by privileged apps.");
      UsageStatsService.this.clearCarrierPrivilegedApps();
    }
    
    public ParceledListSlice<ConfigurationStats> queryConfigurationStats(int paramInt, long paramLong1, long paramLong2, String paramString)
      throws RemoteException
    {
      if (!hasPermission(paramString)) {
        return null;
      }
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = UsageStatsService.this.queryConfigurationStats(i, paramInt, paramLong1, paramLong2);
        if (paramString != null)
        {
          paramString = new ParceledListSlice(paramString);
          return paramString;
        }
        return null;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public UsageEvents queryEvents(long paramLong1, long paramLong2, String paramString)
    {
      if (!hasPermission(paramString)) {
        return null;
      }
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = UsageStatsService.this.queryEvents(i, paramLong1, paramLong2);
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public ParceledListSlice<UsageStats> queryUsageStats(int paramInt, long paramLong1, long paramLong2, String paramString)
    {
      if (!hasPermission(paramString)) {
        return null;
      }
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = UsageStatsService.this.queryUsageStats(i, paramInt, paramLong1, paramLong2);
        if (paramString != null)
        {
          paramString = new ParceledListSlice(paramString);
          return paramString;
        }
        return null;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void setAppInactive(String paramString, boolean paramBoolean, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 28	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: invokestatic 99	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
      //   8: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   11: iload 4
      //   13: iload_3
      //   14: iconst_0
      //   15: iconst_1
      //   16: ldc -84
      //   18: aconst_null
      //   19: invokeinterface 106 8 0
      //   24: istore_3
      //   25: aload_0
      //   26: getfield 13	com/android/server/usage/UsageStatsService$BinderService:this$0	Lcom/android/server/usage/UsageStatsService;
      //   29: invokevirtual 42	com/android/server/usage/UsageStatsService:getContext	()Landroid/content/Context;
      //   32: ldc -82
      //   34: ldc -80
      //   36: invokevirtual 179	android/content/Context:enforceCallingPermission	(Ljava/lang/String;Ljava/lang/String;)V
      //   39: invokestatic 110	android/os/Binder:clearCallingIdentity	()J
      //   42: lstore 5
      //   44: aload_0
      //   45: getfield 13	com/android/server/usage/UsageStatsService$BinderService:this$0	Lcom/android/server/usage/UsageStatsService;
      //   48: aload_1
      //   49: invokevirtual 182	com/android/server/usage/UsageStatsService:getAppId	(Ljava/lang/String;)I
      //   52: istore 4
      //   54: iload 4
      //   56: ifge +15 -> 71
      //   59: lload 5
      //   61: invokestatic 123	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: aload_1
      //   67: invokevirtual 127	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   70: athrow
      //   71: aload_0
      //   72: getfield 13	com/android/server/usage/UsageStatsService$BinderService:this$0	Lcom/android/server/usage/UsageStatsService;
      //   75: aload_1
      //   76: iload_2
      //   77: iload_3
      //   78: invokevirtual 184	com/android/server/usage/UsageStatsService:setAppIdle	(Ljava/lang/String;ZI)V
      //   81: lload 5
      //   83: invokestatic 123	android/os/Binder:restoreCallingIdentity	(J)V
      //   86: return
      //   87: astore_1
      //   88: lload 5
      //   90: invokestatic 123	android/os/Binder:restoreCallingIdentity	(J)V
      //   93: aload_1
      //   94: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	95	0	this	BinderService
      //   0	95	1	paramString	String
      //   0	95	2	paramBoolean	boolean
      //   0	95	3	paramInt	int
      //   3	52	4	i	int
      //   42	47	5	l	long
      // Exception table:
      //   from	to	target	type
      //   5	25	65	android/os/RemoteException
      //   44	54	87	finally
      //   71	81	87	finally
    }
    
    public void whitelistAppTemporarily(String paramString, long paramLong, int paramInt)
      throws RemoteException
    {
      StringBuilder localStringBuilder = new StringBuilder(32);
      localStringBuilder.append("from:");
      UserHandle.formatUid(localStringBuilder, Binder.getCallingUid());
      UsageStatsService.this.mDeviceIdleController.addPowerSaveTempWhitelistApp(paramString, paramLong, paramInt, localStringBuilder.toString());
    }
  }
  
  private class DeviceStateReceiver
    extends BroadcastReceiver
  {
    private DeviceStateReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      boolean bool = false;
      paramContext = paramIntent.getAction();
      if ("android.intent.action.BATTERY_CHANGED".equals(paramContext))
      {
        paramContext = UsageStatsService.this;
        if (paramIntent.getIntExtra("plugged", 0) != 0) {
          bool = true;
        }
        paramContext.setChargingState(bool);
      }
      while (!"android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(paramContext)) {
        return;
      }
      UsageStatsService.this.onDeviceIdleModeChanged();
    }
  }
  
  class H
    extends Handler
  {
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      switch (paramMessage.what)
      {
      default: 
        super.handleMessage(paramMessage);
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        do
        {
          return;
          UsageStatsService.this.reportEvent((UsageEvents.Event)paramMessage.obj, paramMessage.arg1);
          return;
          UsageStatsService.this.flushToDisk();
          return;
          UsageStatsService.this.onUserRemoved(paramMessage.arg1);
          return;
          UsageStatsService localUsageStatsService = UsageStatsService.this;
          String str = (String)paramMessage.obj;
          int i = paramMessage.arg1;
          if (paramMessage.arg2 == 1) {}
          for (;;)
          {
            localUsageStatsService.informListeners(str, i, bool1);
            return;
            bool1 = false;
          }
          localUsageStatsService = UsageStatsService.this;
          str = (String)paramMessage.obj;
          i = paramMessage.arg1;
          if (paramMessage.arg2 == 1) {}
          for (bool1 = bool2;; bool1 = false)
          {
            localUsageStatsService.forceIdleState(str, i, bool1);
            return;
          }
        } while (!UsageStatsService.this.checkIdleStates(paramMessage.arg1));
        UsageStatsService.this.mHandler.sendMessageDelayed(UsageStatsService.this.mHandler.obtainMessage(5, paramMessage.arg1, 0), UsageStatsService.this.mCheckIdleIntervalMillis);
        return;
      case 10: 
        UsageStatsService.this.mHandler.removeMessages(10);
        UsageStatsService.this.checkIdleStates(-1);
        return;
      case 6: 
        UsageStatsService.this.checkParoleTimeout();
        return;
      case 7: 
        UsageStatsService.this.setAppIdleParoled(false);
        return;
      case 8: 
        paramMessage = (SomeArgs)paramMessage.obj;
        UsageStatsService.this.reportContentProviderUsage((String)paramMessage.arg1, (String)paramMessage.arg2, ((Integer)paramMessage.arg3).intValue());
        paramMessage.recycle();
        return;
      }
      UsageStatsService.this.informParoleStateChanged();
    }
  }
  
  private final class LocalService
    extends UsageStatsManagerInternal
  {
    private LocalService() {}
    
    public void addAppIdleStateChangeListener(UsageStatsManagerInternal.AppIdleStateChangeListener paramAppIdleStateChangeListener)
    {
      UsageStatsService.this.addListener(paramAppIdleStateChangeListener);
      paramAppIdleStateChangeListener.onParoleStateChanged(isAppIdleParoleOn());
    }
    
    public void applyRestoredPayload(int paramInt, String paramString, byte[] paramArrayOfByte)
    {
      if (paramInt == 0) {
        UsageStatsService.-wrap2(UsageStatsService.this, paramInt, UsageStatsService.-wrap3(UsageStatsService.this)).applyRestoredPayload(paramString, paramArrayOfByte);
      }
    }
    
    public byte[] getBackupPayload(int paramInt, String paramString)
    {
      if (paramInt == 0) {
        return UsageStatsService.-wrap2(UsageStatsService.this, paramInt, UsageStatsService.-wrap3(UsageStatsService.this)).getBackupPayload(paramString);
      }
      return null;
    }
    
    public int[] getIdleUidsForUser(int paramInt)
    {
      return UsageStatsService.this.getIdleUidsForUser(paramInt);
    }
    
    public boolean isAppIdle(String paramString, int paramInt1, int paramInt2)
    {
      return UsageStatsService.-wrap0(UsageStatsService.this, paramString, paramInt1, paramInt2, SystemClock.elapsedRealtime());
    }
    
    public boolean isAppIdleParoleOn()
    {
      return UsageStatsService.this.isParoledOrCharging();
    }
    
    public void prepareShutdown()
    {
      UsageStatsService.this.shutdown();
    }
    
    public void removeAppIdleStateChangeListener(UsageStatsManagerInternal.AppIdleStateChangeListener paramAppIdleStateChangeListener)
    {
      UsageStatsService.this.removeListener(paramAppIdleStateChangeListener);
    }
    
    public void reportConfigurationChange(Configuration paramConfiguration, int paramInt)
    {
      if (paramConfiguration == null)
      {
        Slog.w("UsageStatsService", "Configuration event reported with a null config");
        return;
      }
      UsageEvents.Event localEvent = new UsageEvents.Event();
      localEvent.mPackage = "android";
      localEvent.mTimeStamp = SystemClock.elapsedRealtime();
      localEvent.mEventType = 5;
      localEvent.mConfiguration = new Configuration(paramConfiguration);
      UsageStatsService.this.mHandler.obtainMessage(0, paramInt, 0, localEvent).sendToTarget();
    }
    
    public void reportContentProviderUsage(String paramString1, String paramString2, int paramInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString1;
      localSomeArgs.arg2 = paramString2;
      localSomeArgs.arg3 = Integer.valueOf(paramInt);
      UsageStatsService.this.mHandler.obtainMessage(8, localSomeArgs).sendToTarget();
    }
    
    public void reportEvent(ComponentName paramComponentName, int paramInt1, int paramInt2)
    {
      if (paramComponentName == null)
      {
        Slog.w("UsageStatsService", "Event reported without a component name");
        return;
      }
      UsageEvents.Event localEvent = new UsageEvents.Event();
      localEvent.mPackage = paramComponentName.getPackageName();
      localEvent.mClass = paramComponentName.getClassName();
      localEvent.mTimeStamp = SystemClock.elapsedRealtime();
      localEvent.mEventType = paramInt2;
      UsageStatsService.this.mHandler.obtainMessage(0, paramInt1, 0, localEvent).sendToTarget();
    }
    
    public void reportEvent(String paramString, int paramInt1, int paramInt2)
    {
      if (paramString == null)
      {
        Slog.w("UsageStatsService", "Event reported without a package name");
        return;
      }
      UsageEvents.Event localEvent = new UsageEvents.Event();
      localEvent.mPackage = paramString;
      localEvent.mTimeStamp = SystemClock.elapsedRealtime();
      localEvent.mEventType = paramInt2;
      UsageStatsService.this.mHandler.obtainMessage(0, paramInt1, 0, localEvent).sendToTarget();
    }
    
    public void reportShortcutUsage(String paramString1, String paramString2, int paramInt)
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        Slog.w("UsageStatsService", "Event reported without a package name or a shortcut ID");
        return;
      }
      UsageEvents.Event localEvent = new UsageEvents.Event();
      localEvent.mPackage = paramString1.intern();
      localEvent.mShortcutId = paramString2.intern();
      localEvent.mTimeStamp = SystemClock.elapsedRealtime();
      localEvent.mEventType = 8;
      UsageStatsService.this.mHandler.obtainMessage(0, paramInt, 0, localEvent).sendToTarget();
    }
  }
  
  private class PackageReceiver
    extends BroadcastReceiver
  {
    private PackageReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if (("android.intent.action.PACKAGE_ADDED".equals(paramContext)) || ("android.intent.action.PACKAGE_CHANGED".equals(paramContext))) {
        UsageStatsService.this.clearCarrierPrivilegedApps();
      }
      if (((!"android.intent.action.PACKAGE_REMOVED".equals(paramContext)) && (!"android.intent.action.PACKAGE_ADDED".equals(paramContext))) || (paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false))) {
        return;
      }
      UsageStatsService.this.clearAppIdleForPackage(paramIntent.getData().getSchemeSpecificPart(), getSendingUserId());
    }
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    private static final String KEY_IDLE_DURATION = "idle_duration2";
    @Deprecated
    private static final String KEY_IDLE_DURATION_OLD = "idle_duration";
    private static final String KEY_PAROLE_DURATION = "parole_duration";
    private static final String KEY_PAROLE_INTERVAL = "parole_interval";
    private static final String KEY_WALLCLOCK_THRESHOLD = "wallclock_threshold";
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      updateSettings();
      UsageStatsService.this.postOneTimeCheckIdleStates();
    }
    
    void registerObserver()
    {
      UsageStatsService.this.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("app_idle_constants"), false, this);
    }
    
    void updateSettings()
    {
      synchronized (UsageStatsService.-get1(UsageStatsService.this))
      {
        try
        {
          this.mParser.setString(Settings.Global.getString(UsageStatsService.this.getContext().getContentResolver(), "app_idle_constants"));
          UsageStatsService.this.mAppIdleScreenThresholdMillis = this.mParser.getLong("idle_duration2", 43200000L);
          UsageStatsService.this.mAppIdleWallclockThresholdMillis = this.mParser.getLong("wallclock_threshold", 172800000L);
          UsageStatsService.this.mCheckIdleIntervalMillis = Math.min(UsageStatsService.this.mAppIdleScreenThresholdMillis / 4L, 28800000L);
          UsageStatsService.this.mAppIdleParoleIntervalMillis = this.mParser.getLong("parole_interval", 86400000L);
          UsageStatsService.this.mAppIdleParoleDurationMillis = this.mParser.getLong("parole_duration", 600000L);
          UsageStatsService.-get0(UsageStatsService.this).setThresholds(UsageStatsService.this.mAppIdleWallclockThresholdMillis, UsageStatsService.this.mAppIdleScreenThresholdMillis);
          return;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            Slog.e("UsageStatsService", "Bad value for app idle settings: " + localIllegalArgumentException.getMessage());
          }
        }
      }
    }
  }
  
  private class UserActionsReceiver
    extends BroadcastReceiver
  {
    private UserActionsReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      int i = paramIntent.getIntExtra("android.intent.extra.user_handle", -1);
      paramContext = paramIntent.getAction();
      if ("android.intent.action.USER_REMOVED".equals(paramContext)) {
        if (i >= 0) {
          UsageStatsService.this.mHandler.obtainMessage(2, i, 0).sendToTarget();
        }
      }
      while ((!"android.intent.action.USER_STARTED".equals(paramContext)) || (i < 0)) {
        return;
      }
      UsageStatsService.this.postCheckIdleStates(i);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UsageStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */