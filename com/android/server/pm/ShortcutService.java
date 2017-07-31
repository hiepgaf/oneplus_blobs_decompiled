package com.android.server.pm;

import android.annotation.IntDef;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.app.IUidObserver.Stub;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IShortcutService.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.ShortcutServiceInternal.ShortcutChangeListener;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class ShortcutService
  extends IShortcutService.Stub
{
  private static Predicate<ResolveInfo> ACTIVITY_NOT_EXPORTED = new -void__clinit___LambdaImpl0();
  private static final String ATTR_VALUE = "value";
  static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  static final boolean DEBUG_LOAD = Build.DEBUG_ONEPLUS;
  static final boolean DEBUG_PROCSTATE = false;
  static final String DEFAULT_ICON_PERSIST_FORMAT = Bitmap.CompressFormat.PNG.name();
  static final int DEFAULT_ICON_PERSIST_QUALITY = 100;
  static final int DEFAULT_MAX_ICON_DIMENSION_DP = 96;
  static final int DEFAULT_MAX_ICON_DIMENSION_LOWRAM_DP = 48;
  static final int DEFAULT_MAX_SHORTCUTS_PER_APP = 5;
  static final int DEFAULT_MAX_UPDATES_PER_INTERVAL = 10;
  static final long DEFAULT_RESET_INTERVAL_SEC = 86400L;
  static final int DEFAULT_SAVE_DELAY_MS = 3000;
  static final String DIRECTORY_BITMAPS = "bitmaps";
  static final String DIRECTORY_PER_USER = "shortcut_service";
  private static List<ResolveInfo> EMPTY_RESOLVE_INFO = new ArrayList(0);
  static final String FILENAME_BASE_STATE = "shortcut_service.xml";
  static final String FILENAME_USER_PACKAGES = "shortcuts.xml";
  private static final String KEY_ICON_SIZE = "iconSize";
  private static final String KEY_LOW_RAM = "lowRam";
  private static final String KEY_SHORTCUT = "shortcut";
  private static final String LAUNCHER_INTENT_CATEGORY = "android.intent.category.LAUNCHER";
  static final int OPERATION_ADD = 1;
  static final int OPERATION_SET = 0;
  static final int OPERATION_UPDATE = 2;
  private static final int PACKAGE_MATCH_FLAGS = 794624;
  private static Predicate<PackageInfo> PACKAGE_NOT_INSTALLED = new -void__clinit___LambdaImpl1();
  private static final int PROCESS_STATE_FOREGROUND_THRESHOLD = 4;
  private static final String[] STAT_LABELS = { "getHomeActivities()", "Launcher permission check", "getPackageInfo()", "getPackageInfo(SIG)", "getApplicationInfo", "cleanupDanglingBitmaps", "getActivity+metadata", "getInstalledPackages", "checkPackageChanges", "getApplicationResources", "resourceNameLookup", "getLauncherActivity", "checkLauncherActivity", "isActivityEnabled", "packageUpdateCheck", "asyncPreloadUserDelay" };
  static final String TAG = "ShortcutService";
  private static final String TAG_LAST_RESET_TIME = "last_reset_time";
  private static final String TAG_ROOT = "root";
  private final ActivityManagerInternal mActivityManagerInternal;
  private final AtomicBoolean mBootCompleted = new AtomicBoolean();
  final Context mContext;
  @GuardedBy("mStatLock")
  private final int[] mCountStats = new int[16];
  @GuardedBy("mLock")
  private List<Integer> mDirtyUserIds = new ArrayList();
  @GuardedBy("mStatLock")
  private final long[] mDurationStats = new long[16];
  private final Handler mHandler;
  private final IPackageManager mIPackageManager;
  private Bitmap.CompressFormat mIconPersistFormat;
  private int mIconPersistQuality;
  @GuardedBy("mLock")
  private Exception mLastWtfStacktrace;
  @GuardedBy("mLock")
  private final ArrayList<ShortcutServiceInternal.ShortcutChangeListener> mListeners = new ArrayList(1);
  private final Object mLock = new Object();
  private int mMaxIconDimension;
  private int mMaxShortcuts;
  int mMaxUpdatesPerInterval;
  private final PackageManagerInternal mPackageManagerInternal;
  final BroadcastReceiver mPackageMonitor = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
      if (i == 55536)
      {
        Slog.w("ShortcutService", "Intent broadcast does not contain user handle: " + paramAnonymousIntent);
        return;
      }
      String str = paramAnonymousIntent.getAction();
      long l = ShortcutService.this.injectClearCallingIdentity();
      for (;;)
      {
        try
        {
          boolean bool;
          synchronized (ShortcutService.-get2(ShortcutService.this))
          {
            if (!ShortcutService.this.isUserUnlockedL(i))
            {
              if (ShortcutService.DEBUG) {
                Slog.d("ShortcutService", "Ignoring package broadcast " + str + " for locked/stopped user " + i);
              }
              return;
            }
            ShortcutService.this.getUserShortcutsLocked(i).clearLauncher();
            bool = "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED".equals(str);
            if (bool) {
              return;
            }
          }
          if (!str.equals("android.intent.action.PACKAGE_CHANGED")) {
            break label357;
          }
        }
        catch (Exception ???)
        {
          ShortcutService.this.wtf("Exception in mPackageMonitor.onReceive", ???);
          return;
          ??? = paramAnonymousIntent.getData();
          if (??? != null)
          {
            ??? = ???.getSchemeSpecificPart();
            if (??? == null) {
              Slog.w("ShortcutService", "Intent broadcast does not contain package name: " + paramAnonymousIntent);
            }
          }
          else
          {
            ??? = null;
            continue;
          }
          bool = paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false);
          if (str.equals("android.intent.action.PACKAGE_ADDED"))
          {
            if (!bool) {
              break label379;
            }
            ShortcutService.-wrap5(ShortcutService.this, ???, i);
            return;
          }
          if (str.equals("android.intent.action.PACKAGE_REMOVED"))
          {
            if (bool) {
              continue;
            }
            ShortcutService.-wrap4(ShortcutService.this, ???, i);
            continue;
          }
        }
        finally
        {
          ShortcutService.this.injectRestoreCallingIdentity(l);
        }
        ShortcutService.-wrap2(ShortcutService.this, ???, i);
        continue;
        label357:
        if (str.equals("android.intent.action.PACKAGE_DATA_CLEARED"))
        {
          ShortcutService.-wrap3(ShortcutService.this, ???, i);
          continue;
          label379:
          ShortcutService.-wrap1(ShortcutService.this, ???, i);
        }
      }
    }
  };
  @GuardedBy("mLock")
  private long mRawLastResetTime;
  final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!ShortcutService.-get0(ShortcutService.this).get()) {
        return;
      }
      try
      {
        if ("android.intent.action.LOCALE_CHANGED".equals(paramAnonymousIntent.getAction())) {
          ShortcutService.this.handleLocaleChanged();
        }
        return;
      }
      catch (Exception paramAnonymousContext)
      {
        ShortcutService.this.wtf("Exception in mReceiver.onReceive", paramAnonymousContext);
      }
    }
  };
  private long mResetInterval;
  private int mSaveDelayMillis;
  private final Runnable mSaveDirtyInfoRunner = new -void__init__android_content_Context_context_android_os_Looper_looper_boolean_onlyForPackageManagerApis_LambdaImpl0();
  final Object mStatLock = new Object();
  @GuardedBy("mLock")
  final SparseLongArray mUidLastForegroundElapsedTime = new SparseLongArray();
  private final IUidObserver mUidObserver = new IUidObserver.Stub()
  {
    public void onUidActive(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidGone(int paramAnonymousInt)
      throws RemoteException
    {
      ShortcutService.this.handleOnUidStateChanged(paramAnonymousInt, 16);
    }
    
    public void onUidIdle(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidStateChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {
      ShortcutService.this.handleOnUidStateChanged(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  @GuardedBy("mLock")
  final SparseIntArray mUidState = new SparseIntArray();
  @GuardedBy("mLock")
  final SparseBooleanArray mUnlockedUsers = new SparseBooleanArray();
  private final UsageStatsManagerInternal mUsageStatsManagerInternal;
  private final UserManager mUserManager;
  @GuardedBy("mLock")
  private final SparseArray<ShortcutUser> mUsers = new SparseArray();
  @GuardedBy("mLock")
  private int mWtfCount = 0;
  
  public ShortcutService(Context paramContext)
  {
    this(paramContext, BackgroundThread.get().getLooper(), false);
  }
  
  ShortcutService(Context paramContext, Looper paramLooper, boolean paramBoolean)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    LocalServices.addService(ShortcutServiceInternal.class, new LocalService(null));
    this.mHandler = new Handler(paramLooper);
    this.mIPackageManager = AppGlobals.getPackageManager();
    this.mPackageManagerInternal = ((PackageManagerInternal)Preconditions.checkNotNull((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class)));
    this.mUserManager = ((UserManager)Preconditions.checkNotNull((UserManager)paramContext.getSystemService(UserManager.class)));
    this.mUsageStatsManagerInternal = ((UsageStatsManagerInternal)Preconditions.checkNotNull((UsageStatsManagerInternal)LocalServices.getService(UsageStatsManagerInternal.class)));
    this.mActivityManagerInternal = ((ActivityManagerInternal)Preconditions.checkNotNull((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)));
    if (paramBoolean) {
      return;
    }
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.PACKAGE_ADDED");
    paramContext.addAction("android.intent.action.PACKAGE_REMOVED");
    paramContext.addAction("android.intent.action.PACKAGE_CHANGED");
    paramContext.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
    paramContext.addDataScheme("package");
    paramContext.setPriority(1000);
    this.mContext.registerReceiverAsUser(this.mPackageMonitor, UserHandle.ALL, paramContext, null, this.mHandler);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED");
    paramContext.setPriority(1000);
    this.mContext.registerReceiverAsUser(this.mPackageMonitor, UserHandle.ALL, paramContext, null, this.mHandler);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.LOCALE_CHANGED");
    paramContext.setPriority(1000);
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, paramContext, null, this.mHandler);
    injectRegisterUidObserver(this.mUidObserver, 3);
  }
  
  private void assignImplicitRanks(List<ShortcutInfo> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ((ShortcutInfo)paramList.get(i)).setImplicitRank(i);
      i -= 1;
    }
  }
  
  private void cleanUpPackageForAllLoadedUsers(String paramString, int paramInt, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      forEachLoadedUserLocked(new -void_cleanUpPackageForAllLoadedUsers_java_lang_String_packageName_int_packageUserId_boolean_appStillExists_LambdaImpl0(paramString, paramInt, paramBoolean));
      return;
    }
  }
  
  private void cleanupDanglingBitmapDirectoriesLocked(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "cleanupDanglingBitmaps: userId=" + paramInt);
    }
    long l = injectElapsedRealtime();
    ShortcutUser localShortcutUser = getUserShortcutsLocked(paramInt);
    File[] arrayOfFile = getUserBitmapFilePath(paramInt).listFiles();
    if (arrayOfFile == null) {
      return;
    }
    int i = 0;
    int j = arrayOfFile.length;
    if (i < j)
    {
      File localFile = arrayOfFile[i];
      if (!localFile.isDirectory()) {}
      for (;;)
      {
        i += 1;
        break;
        String str = localFile.getName();
        if (DEBUG) {
          Slog.d("ShortcutService", "cleanupDanglingBitmaps: Found directory=" + str);
        }
        if (!localShortcutUser.hasPackage(str))
        {
          if (DEBUG) {
            Slog.d("ShortcutService", "Removing dangling bitmap directory: " + str);
          }
          cleanupBitmapsForPackage(paramInt, str);
        }
        else
        {
          cleanupDanglingBitmapFilesLocked(paramInt, localShortcutUser, str, localFile);
        }
      }
    }
    logDurationStat(5, l);
  }
  
  private void cleanupDanglingBitmapFilesLocked(int paramInt, ShortcutUser paramShortcutUser, String paramString, File paramFile)
  {
    paramShortcutUser = paramShortcutUser.getPackageShortcuts(paramString).getUsedBitmapFiles();
    paramString = paramFile.listFiles();
    paramInt = 0;
    int i = paramString.length;
    if (paramInt < i)
    {
      paramFile = paramString[paramInt];
      if (!paramFile.isFile()) {}
      for (;;)
      {
        paramInt += 1;
        break;
        if (!paramShortcutUser.contains(paramFile.getName()))
        {
          if (DEBUG) {
            Slog.d("ShortcutService", "Removing dangling bitmap file: " + paramFile.getAbsolutePath());
          }
          paramFile.delete();
        }
      }
    }
  }
  
  private void dumpCheckin(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      try
      {
        JSONArray localJSONArray = new JSONArray();
        int i = 0;
        while (i < this.mUsers.size())
        {
          localJSONArray.put(((ShortcutUser)this.mUsers.valueAt(i)).dumpCheckin(paramBoolean));
          i += 1;
        }
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("shortcut", localJSONArray);
        localJSONObject.put("lowRam", injectIsLowRamDevice());
        localJSONObject.put("iconSize", this.mMaxIconDimension);
        paramPrintWriter.println(localJSONObject.toString(1));
      }
      catch (JSONException paramPrintWriter)
      {
        for (;;)
        {
          Slog.e("ShortcutService", "Unable to write in json", paramPrintWriter);
        }
      }
      return;
    }
  }
  
  private void dumpInner(PrintWriter paramPrintWriter)
  {
    for (;;)
    {
      Object localObject2;
      synchronized (this.mLock)
      {
        long l = injectCurrentTimeMillis();
        paramPrintWriter.print("Now: [");
        paramPrintWriter.print(l);
        paramPrintWriter.print("] ");
        paramPrintWriter.print(formatTime(l));
        paramPrintWriter.print("  Raw last reset: [");
        paramPrintWriter.print(this.mRawLastResetTime);
        paramPrintWriter.print("] ");
        paramPrintWriter.print(formatTime(this.mRawLastResetTime));
        l = getLastResetTimeLocked();
        paramPrintWriter.print("  Last reset: [");
        paramPrintWriter.print(l);
        paramPrintWriter.print("] ");
        paramPrintWriter.print(formatTime(l));
        l = getNextResetTimeLocked();
        paramPrintWriter.print("  Next reset: [");
        paramPrintWriter.print(l);
        paramPrintWriter.print("] ");
        paramPrintWriter.print(formatTime(l));
        paramPrintWriter.print("  Config:");
        paramPrintWriter.print("    Max icon dim: ");
        paramPrintWriter.println(this.mMaxIconDimension);
        paramPrintWriter.print("    Icon format: ");
        paramPrintWriter.println(this.mIconPersistFormat);
        paramPrintWriter.print("    Icon quality: ");
        paramPrintWriter.println(this.mIconPersistQuality);
        paramPrintWriter.print("    saveDelayMillis: ");
        paramPrintWriter.println(this.mSaveDelayMillis);
        paramPrintWriter.print("    resetInterval: ");
        paramPrintWriter.println(this.mResetInterval);
        paramPrintWriter.print("    maxUpdatesPerInterval: ");
        paramPrintWriter.println(this.mMaxUpdatesPerInterval);
        paramPrintWriter.print("    maxShortcutsPerActivity: ");
        paramPrintWriter.println(this.mMaxShortcuts);
        paramPrintWriter.println();
        paramPrintWriter.println("  Stats:");
        localObject2 = this.mStatLock;
        i = 0;
        if (i < 16) {}
        try
        {
          dumpStatLS(paramPrintWriter, "    ", i);
          i += 1;
        }
        finally {}
        paramPrintWriter.println();
        paramPrintWriter.print("  #Failures: ");
        paramPrintWriter.println(this.mWtfCount);
        if (this.mLastWtfStacktrace == null) {
          break label523;
        }
        paramPrintWriter.print("  Last failure stack trace: ");
        paramPrintWriter.println(Log.getStackTraceString(this.mLastWtfStacktrace));
        break label523;
        if (i < this.mUsers.size())
        {
          paramPrintWriter.println();
          ((ShortcutUser)this.mUsers.valueAt(i)).dump(paramPrintWriter, "  ");
          i += 1;
        }
      }
      paramPrintWriter.println();
      paramPrintWriter.println("  UID state:");
      int i = 0;
      while (i < this.mUidState.size())
      {
        int j = this.mUidState.keyAt(i);
        int k = this.mUidState.valueAt(i);
        paramPrintWriter.print("    UID=");
        paramPrintWriter.print(j);
        paramPrintWriter.print(" state=");
        paramPrintWriter.print(k);
        if (isProcessStateForeground(k)) {
          paramPrintWriter.print("  [FG]");
        }
        paramPrintWriter.print("  last FG=");
        paramPrintWriter.print(this.mUidLastForegroundElapsedTime.get(j));
        paramPrintWriter.println();
        i += 1;
      }
      return;
      label523:
      i = 0;
    }
  }
  
  private void dumpStatLS(PrintWriter paramPrintWriter, String paramString, int paramInt)
  {
    paramPrintWriter.print(paramString);
    int i = this.mCountStats[paramInt];
    long l = this.mDurationStats[paramInt];
    paramString = STAT_LABELS[paramInt];
    if (i == 0) {}
    for (double d = 0.0D;; d = l / i)
    {
      paramPrintWriter.println(String.format("%s: count=%d, total=%dms, avg=%.1fms", new Object[] { paramString, Integer.valueOf(i), Long.valueOf(l), Double.valueOf(d) }));
      return;
    }
  }
  
  private void enforceCallingOrSelfPermission(String paramString1, String paramString2)
  {
    if (isCallerSystem()) {
      return;
    }
    injectEnforceCallingPermission(paramString1, paramString2);
  }
  
  private void enforceResetThrottlingPermission()
  {
    if (isCallerSystem()) {
      return;
    }
    enforceCallingOrSelfPermission("android.permission.RESET_SHORTCUT_MANAGER_THROTTLING", null);
  }
  
  private void enforceShell()
  {
    if (!isCallerShell()) {
      throw new SecurityException("Caller must be shell");
    }
  }
  
  private void enforceSystem()
  {
    if (!isCallerSystem()) {
      throw new SecurityException("Caller must be system");
    }
  }
  
  private void enforceSystemOrShell()
  {
    if (!isCallerSystem()) {}
    for (boolean bool = isCallerShell(); !bool; bool = true) {
      throw new SecurityException("Caller must be system or shell");
    }
  }
  
  private void fillInDefaultActivity(List<ShortcutInfo> paramList)
  {
    Object localObject1 = null;
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)paramList.get(i);
      Object localObject2 = localObject1;
      if (localShortcutInfo.getActivity() == null)
      {
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = injectGetDefaultMainActivity(localShortcutInfo.getPackage(), localShortcutInfo.getUserId());
          if (localObject2 == null) {
            break label118;
          }
        }
      }
      label118:
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkState(bool, "Launcher activity not found for package " + localShortcutInfo.getPackage());
        localShortcutInfo.setActivity((ComponentName)localObject2);
        i -= 1;
        localObject1 = localObject2;
        break;
      }
    }
  }
  
  private void fixUpIncomingShortcutInfo(ShortcutInfo paramShortcutInfo, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramShortcutInfo, "Null shortcut detected");
    if (paramShortcutInfo.getActivity() != null)
    {
      Preconditions.checkState(paramShortcutInfo.getPackage().equals(paramShortcutInfo.getActivity().getPackageName()), "Cannot publish shortcut: activity " + paramShortcutInfo.getActivity() + " does not" + " belong to package " + paramShortcutInfo.getPackage());
      Preconditions.checkState(injectIsMainActivity(paramShortcutInfo.getActivity(), paramShortcutInfo.getUserId()), "Cannot publish shortcut: activity " + paramShortcutInfo.getActivity() + " is not" + " main activity");
    }
    if (!paramBoolean)
    {
      paramShortcutInfo.enforceMandatoryFields();
      Preconditions.checkArgument(injectIsMainActivity(paramShortcutInfo.getActivity(), paramShortcutInfo.getUserId()), "Cannot publish shortcut: " + paramShortcutInfo.getActivity() + " is not main activity");
    }
    if (paramShortcutInfo.getIcon() != null) {
      ShortcutInfo.validateIcon(paramShortcutInfo.getIcon());
    }
    paramShortcutInfo.replaceFlags(0);
  }
  
  private void forUpdatedPackages(int paramInt, long paramLong, boolean paramBoolean, Consumer<ApplicationInfo> paramConsumer)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "forUpdatedPackages for user " + paramInt + ", lastScanTime=" + paramLong);
    }
    List localList = getInstalledPackages(paramInt);
    paramInt = localList.size() - 1;
    while (paramInt >= 0)
    {
      PackageInfo localPackageInfo = (PackageInfo)localList.get(paramInt);
      if ((localPackageInfo.lastUpdateTime >= paramLong) || ((paramBoolean) && (isPureSystemApp(localPackageInfo.applicationInfo))))
      {
        if (DEBUG) {
          Slog.d("ShortcutService", "Found updated package " + localPackageInfo.packageName);
        }
        paramConsumer.accept(localPackageInfo.applicationInfo);
      }
      paramInt -= 1;
    }
  }
  
  static String formatTime(long paramLong)
  {
    Time localTime = new Time();
    localTime.set(paramLong);
    return localTime.format("%Y-%m-%d %H:%M:%S");
  }
  
  private AtomicFile getBaseStateFile()
  {
    File localFile = new File(injectSystemDataPath(), "shortcut_service.xml");
    localFile.mkdirs();
    return new AtomicFile(localFile);
  }
  
  private int getCallingUserId()
  {
    return UserHandle.getUserId(injectBinderCallingUid());
  }
  
  private Intent getMainActivityIntent()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.LAUNCHER");
    return localIntent;
  }
  
  private ParceledListSlice<ShortcutInfo> getShortcutsWithQueryLocked(String paramString, int paramInt1, int paramInt2, Predicate<ShortcutInfo> paramPredicate)
  {
    ArrayList localArrayList = new ArrayList();
    getPackageShortcutsForPublisherLocked(paramString, paramInt1).findAll(localArrayList, paramPredicate, paramInt2);
    return new ParceledListSlice(localArrayList);
  }
  
  private void handlePackageAdded(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("handlePackageAdded: %s user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    synchronized (this.mLock)
    {
      ShortcutUser localShortcutUser = getUserShortcutsLocked(paramInt);
      localShortcutUser.attemptToRestoreIfNeededAndSave(this, paramString, paramInt);
      localShortcutUser.rescanPackageIfNeeded(paramString, true);
      verifyStates();
      return;
    }
  }
  
  private void handlePackageChanged(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("handlePackageChanged: %s user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    synchronized (this.mLock)
    {
      getUserShortcutsLocked(paramInt).rescanPackageIfNeeded(paramString, true);
      verifyStates();
      return;
    }
  }
  
  private void handlePackageDataCleared(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("handlePackageDataCleared: %s user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    cleanUpPackageForAllLoadedUsers(paramString, paramInt, true);
    verifyStates();
  }
  
  private void handlePackageRemoved(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("handlePackageRemoved: %s user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    cleanUpPackageForAllLoadedUsers(paramString, paramInt, false);
    verifyStates();
  }
  
  private void handlePackageUpdateFinished(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("handlePackageUpdateFinished: %s user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    synchronized (this.mLock)
    {
      ShortcutUser localShortcutUser = getUserShortcutsLocked(paramInt);
      localShortcutUser.attemptToRestoreIfNeededAndSave(this, paramString, paramInt);
      if (isPackageInstalled(paramString, paramInt)) {
        localShortcutUser.rescanPackageIfNeeded(paramString, true);
      }
      verifyStates();
      return;
    }
  }
  
  private void initialize()
  {
    synchronized (this.mLock)
    {
      loadConfigurationLocked();
      loadBaseStateLocked();
      return;
    }
  }
  
  private boolean isApplicationFlagSet(String paramString, int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    paramString = injectApplicationInfoWithUninstalled(paramString, paramInt1);
    boolean bool1 = bool2;
    if (paramString != null)
    {
      bool1 = bool2;
      if ((paramString.flags & paramInt2) == paramInt2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isCallerShell()
  {
    int i = injectBinderCallingUid();
    return (i == 2000) || (i == 0);
  }
  
  private boolean isCallerSystem()
  {
    return UserHandle.isSameApp(injectBinderCallingUid(), 1000);
  }
  
  static boolean isClockValid(long paramLong)
  {
    return paramLong >= 1420070400L;
  }
  
  private static boolean isInstalled(ActivityInfo paramActivityInfo)
  {
    if (paramActivityInfo != null) {
      return isInstalled(paramActivityInfo.applicationInfo);
    }
    return false;
  }
  
  private static boolean isInstalled(ApplicationInfo paramApplicationInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramApplicationInfo != null)
    {
      bool1 = bool2;
      if ((paramApplicationInfo.flags & 0x800000) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isInstalled(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo != null) {
      return isInstalled(paramPackageInfo.applicationInfo);
    }
    return false;
  }
  
  private static ActivityInfo isInstalledOrNull(ActivityInfo paramActivityInfo)
  {
    if (isInstalled(paramActivityInfo)) {
      return paramActivityInfo;
    }
    return null;
  }
  
  private static ApplicationInfo isInstalledOrNull(ApplicationInfo paramApplicationInfo)
  {
    if (isInstalled(paramApplicationInfo)) {
      return paramApplicationInfo;
    }
    return null;
  }
  
  private static PackageInfo isInstalledOrNull(PackageInfo paramPackageInfo)
  {
    if (isInstalled(paramPackageInfo)) {
      return paramPackageInfo;
    }
    return null;
  }
  
  private boolean isProcessStateForeground(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt != -1)
    {
      bool1 = bool2;
      if (paramInt <= 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isPureSystemApp(ApplicationInfo paramApplicationInfo)
  {
    return (paramApplicationInfo.isSystemApp()) && (!paramApplicationInfo.isUpdatedSystemApp());
  }
  
  @GuardedBy("mLock")
  private boolean isUserLoadedLocked(int paramInt)
  {
    return this.mUsers.get(paramInt) != null;
  }
  
  /* Error */
  private void loadBaseStateLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: lconst_0
    //   2: putfield 788	com/android/server/pm/ShortcutService:mRawLastResetTime	J
    //   5: aload_0
    //   6: invokespecial 1187	com/android/server/pm/ShortcutService:getBaseStateFile	()Landroid/util/AtomicFile;
    //   9: astore 8
    //   11: getstatic 375	com/android/server/pm/ShortcutService:DEBUG	Z
    //   14: ifeq +33 -> 47
    //   17: ldc -83
    //   19: new 628	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   26: ldc_w 1189
    //   29: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: aload 8
    //   34: invokevirtual 1192	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   37: invokevirtual 983	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   40: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   43: invokestatic 647	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aconst_null
    //   48: astore 5
    //   50: aconst_null
    //   51: astore 7
    //   53: aconst_null
    //   54: astore 6
    //   56: aconst_null
    //   57: astore_3
    //   58: aconst_null
    //   59: astore_2
    //   60: aload 8
    //   62: invokevirtual 1196	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   65: astore 4
    //   67: aload 4
    //   69: astore_2
    //   70: aload 4
    //   72: astore_3
    //   73: invokestatic 1202	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   76: astore 9
    //   78: aload 4
    //   80: astore_2
    //   81: aload 4
    //   83: astore_3
    //   84: aload 9
    //   86: aload 4
    //   88: getstatic 1208	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   91: invokevirtual 1211	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   94: invokeinterface 1217 3 0
    //   99: aload 4
    //   101: astore_2
    //   102: aload 4
    //   104: astore_3
    //   105: aload 9
    //   107: invokeinterface 1220 1 0
    //   112: istore_1
    //   113: iload_1
    //   114: iconst_1
    //   115: if_icmpeq +274 -> 389
    //   118: iload_1
    //   119: iconst_2
    //   120: if_icmpne -21 -> 99
    //   123: aload 4
    //   125: astore_2
    //   126: aload 4
    //   128: astore_3
    //   129: aload 9
    //   131: invokeinterface 1223 1 0
    //   136: istore_1
    //   137: aload 4
    //   139: astore_2
    //   140: aload 4
    //   142: astore_3
    //   143: aload 9
    //   145: invokeinterface 1224 1 0
    //   150: astore 10
    //   152: iload_1
    //   153: iconst_1
    //   154: if_icmpne +86 -> 240
    //   157: aload 4
    //   159: astore_2
    //   160: aload 4
    //   162: astore_3
    //   163: ldc -77
    //   165: aload 10
    //   167: invokevirtual 978	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   170: ifne -71 -> 99
    //   173: aload 4
    //   175: astore_2
    //   176: aload 4
    //   178: astore_3
    //   179: ldc -83
    //   181: new 628	java/lang/StringBuilder
    //   184: dup
    //   185: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   188: ldc_w 1226
    //   191: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: aload 10
    //   196: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   199: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   202: invokestatic 1228	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   205: pop
    //   206: aload 6
    //   208: astore_2
    //   209: aload 4
    //   211: ifnull +11 -> 222
    //   214: aload 4
    //   216: invokevirtual 1233	java/io/FileInputStream:close	()V
    //   219: aload 6
    //   221: astore_2
    //   222: aload_2
    //   223: ifnull +16 -> 239
    //   226: aload_2
    //   227: athrow
    //   228: astore_2
    //   229: aload_0
    //   230: invokevirtual 791	com/android/server/pm/ShortcutService:getLastResetTimeLocked	()J
    //   233: pop2
    //   234: return
    //   235: astore_2
    //   236: goto -14 -> 222
    //   239: return
    //   240: aload 4
    //   242: astore_2
    //   243: aload 4
    //   245: astore_3
    //   246: aload 10
    //   248: ldc -80
    //   250: invokevirtual 978	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   253: ifeq +90 -> 343
    //   256: aload 4
    //   258: astore_2
    //   259: aload 4
    //   261: astore_3
    //   262: aload_0
    //   263: aload 9
    //   265: ldc 107
    //   267: invokestatic 1237	com/android/server/pm/ShortcutService:parseLongAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)J
    //   270: putfield 788	com/android/server/pm/ShortcutService:mRawLastResetTime	J
    //   273: goto -174 -> 99
    //   276: astore_3
    //   277: aload_3
    //   278: athrow
    //   279: astore 4
    //   281: aload_3
    //   282: astore 5
    //   284: aload_2
    //   285: ifnull +10 -> 295
    //   288: aload_2
    //   289: invokevirtual 1233	java/io/FileInputStream:close	()V
    //   292: aload_3
    //   293: astore 5
    //   295: aload 5
    //   297: ifnull +133 -> 430
    //   300: aload 5
    //   302: athrow
    //   303: astore_2
    //   304: ldc -83
    //   306: new 628	java/lang/StringBuilder
    //   309: dup
    //   310: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   313: ldc_w 1239
    //   316: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   319: aload 8
    //   321: invokevirtual 1192	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   324: invokevirtual 983	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   327: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   330: aload_2
    //   331: invokestatic 765	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   334: pop
    //   335: aload_0
    //   336: lconst_0
    //   337: putfield 788	com/android/server/pm/ShortcutService:mRawLastResetTime	J
    //   340: goto -111 -> 229
    //   343: aload 4
    //   345: astore_2
    //   346: aload 4
    //   348: astore_3
    //   349: ldc -83
    //   351: new 628	java/lang/StringBuilder
    //   354: dup
    //   355: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   358: ldc_w 1241
    //   361: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: aload 10
    //   366: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   369: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   372: invokestatic 1228	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   375: pop
    //   376: goto -277 -> 99
    //   379: astore 4
    //   381: aload_3
    //   382: astore_2
    //   383: aload 5
    //   385: astore_3
    //   386: goto -105 -> 281
    //   389: aload 7
    //   391: astore_2
    //   392: aload 4
    //   394: ifnull +11 -> 405
    //   397: aload 4
    //   399: invokevirtual 1233	java/io/FileInputStream:close	()V
    //   402: aload 7
    //   404: astore_2
    //   405: aload_2
    //   406: ifnull -177 -> 229
    //   409: aload_2
    //   410: athrow
    //   411: aload_3
    //   412: astore 5
    //   414: aload_3
    //   415: aload_2
    //   416: if_acmpeq -121 -> 295
    //   419: aload_3
    //   420: aload_2
    //   421: invokevirtual 1245	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   424: aload_3
    //   425: astore 5
    //   427: goto -132 -> 295
    //   430: aload 4
    //   432: athrow
    //   433: astore_2
    //   434: goto -29 -> 405
    //   437: astore_2
    //   438: aload_3
    //   439: ifnonnull -28 -> 411
    //   442: aload_2
    //   443: astore 5
    //   445: goto -150 -> 295
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	448	0	this	ShortcutService
    //   112	43	1	i	int
    //   59	168	2	localObject1	Object
    //   228	1	2	localFileNotFoundException	FileNotFoundException
    //   235	1	2	localThrowable1	Throwable
    //   242	47	2	localObject2	Object
    //   303	28	2	localIOException	IOException
    //   345	76	2	localObject3	Object
    //   433	1	2	localThrowable2	Throwable
    //   437	6	2	localThrowable3	Throwable
    //   57	205	3	localObject4	Object
    //   276	17	3	localThrowable4	Throwable
    //   348	91	3	localObject5	Object
    //   65	195	4	localFileInputStream	java.io.FileInputStream
    //   279	68	4	localObject6	Object
    //   379	52	4	localObject7	Object
    //   48	396	5	localObject8	Object
    //   54	166	6	localObject9	Object
    //   51	352	7	localObject10	Object
    //   9	311	8	localAtomicFile	AtomicFile
    //   76	188	9	localXmlPullParser	XmlPullParser
    //   150	215	10	str	String
    // Exception table:
    //   from	to	target	type
    //   214	219	228	java/io/FileNotFoundException
    //   226	228	228	java/io/FileNotFoundException
    //   288	292	228	java/io/FileNotFoundException
    //   300	303	228	java/io/FileNotFoundException
    //   397	402	228	java/io/FileNotFoundException
    //   409	411	228	java/io/FileNotFoundException
    //   419	424	228	java/io/FileNotFoundException
    //   430	433	228	java/io/FileNotFoundException
    //   214	219	235	java/lang/Throwable
    //   60	67	276	java/lang/Throwable
    //   73	78	276	java/lang/Throwable
    //   84	99	276	java/lang/Throwable
    //   105	113	276	java/lang/Throwable
    //   129	137	276	java/lang/Throwable
    //   143	152	276	java/lang/Throwable
    //   163	173	276	java/lang/Throwable
    //   179	206	276	java/lang/Throwable
    //   246	256	276	java/lang/Throwable
    //   262	273	276	java/lang/Throwable
    //   349	376	276	java/lang/Throwable
    //   277	279	279	finally
    //   214	219	303	java/io/IOException
    //   214	219	303	org/xmlpull/v1/XmlPullParserException
    //   226	228	303	java/io/IOException
    //   226	228	303	org/xmlpull/v1/XmlPullParserException
    //   288	292	303	java/io/IOException
    //   288	292	303	org/xmlpull/v1/XmlPullParserException
    //   300	303	303	java/io/IOException
    //   300	303	303	org/xmlpull/v1/XmlPullParserException
    //   397	402	303	java/io/IOException
    //   397	402	303	org/xmlpull/v1/XmlPullParserException
    //   409	411	303	java/io/IOException
    //   409	411	303	org/xmlpull/v1/XmlPullParserException
    //   419	424	303	java/io/IOException
    //   419	424	303	org/xmlpull/v1/XmlPullParserException
    //   430	433	303	java/io/IOException
    //   430	433	303	org/xmlpull/v1/XmlPullParserException
    //   60	67	379	finally
    //   73	78	379	finally
    //   84	99	379	finally
    //   105	113	379	finally
    //   129	137	379	finally
    //   143	152	379	finally
    //   163	173	379	finally
    //   179	206	379	finally
    //   246	256	379	finally
    //   262	273	379	finally
    //   349	376	379	finally
    //   397	402	433	java/lang/Throwable
    //   288	292	437	java/lang/Throwable
  }
  
  private void loadConfigurationLocked()
  {
    updateConfigurationLocked(injectShortcutManagerConstants());
  }
  
  private ShortcutUser loadUserInternal(int paramInt, InputStream paramInputStream, boolean paramBoolean)
    throws XmlPullParserException, IOException, ShortcutService.InvalidFileFormatException
  {
    Object localObject = new BufferedInputStream(paramInputStream);
    paramInputStream = null;
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput((InputStream)localObject, StandardCharsets.UTF_8.name());
    for (;;)
    {
      int i = localXmlPullParser.next();
      if (i == 1) {
        break;
      }
      if (i == 2)
      {
        int j = localXmlPullParser.getDepth();
        localObject = localXmlPullParser.getName();
        if (DEBUG_LOAD) {
          Slog.d("ShortcutService", String.format("depth=%d type=%d name=%s", new Object[] { Integer.valueOf(j), Integer.valueOf(i), localObject }));
        }
        if ((j == 1) && ("user".equals(localObject))) {
          paramInputStream = ShortcutUser.loadFromXml(this, localXmlPullParser, paramInt, paramBoolean);
        } else {
          throwForInvalidTag(j, (String)localObject);
        }
      }
    }
    return paramInputStream;
  }
  
  /* Error */
  private ShortcutUser loadUserLocked(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokevirtual 1275	com/android/server/pm/ShortcutService:getUserFile	(I)Ljava/io/File;
    //   5: astore 4
    //   7: getstatic 375	com/android/server/pm/ShortcutService:DEBUG	Z
    //   10: ifeq +30 -> 40
    //   13: ldc -83
    //   15: new 628	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   22: ldc_w 1189
    //   25: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: aload 4
    //   30: invokevirtual 983	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   33: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokestatic 647	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: new 1078	android/util/AtomicFile
    //   43: dup
    //   44: aload 4
    //   46: invokespecial 1081	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   49: astore_3
    //   50: aload_3
    //   51: invokevirtual 1196	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   54: astore_2
    //   55: aload_0
    //   56: iload_1
    //   57: aload_2
    //   58: iconst_0
    //   59: invokespecial 1277	com/android/server/pm/ShortcutService:loadUserInternal	(ILjava/io/InputStream;Z)Lcom/android/server/pm/ShortcutUser;
    //   62: astore 4
    //   64: aload_2
    //   65: invokestatic 1283	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   68: aload 4
    //   70: areturn
    //   71: astore_2
    //   72: getstatic 375	com/android/server/pm/ShortcutService:DEBUG	Z
    //   75: ifeq +30 -> 105
    //   78: ldc -83
    //   80: new 628	java/lang/StringBuilder
    //   83: dup
    //   84: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   87: ldc_w 1285
    //   90: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: aload 4
    //   95: invokevirtual 983	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   98: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokestatic 647	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: aconst_null
    //   106: areturn
    //   107: astore 4
    //   109: ldc -83
    //   111: new 628	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   118: ldc_w 1239
    //   121: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_3
    //   125: invokevirtual 1192	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   128: invokevirtual 983	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   131: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   134: aload 4
    //   136: invokestatic 765	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   139: pop
    //   140: aload_2
    //   141: invokestatic 1283	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   144: aconst_null
    //   145: areturn
    //   146: astore_3
    //   147: aload_2
    //   148: invokestatic 1283	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   151: aload_3
    //   152: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	153	0	this	ShortcutService
    //   0	153	1	paramInt	int
    //   54	11	2	localFileInputStream	java.io.FileInputStream
    //   71	77	2	localFileNotFoundException	FileNotFoundException
    //   49	76	3	localAtomicFile	AtomicFile
    //   146	6	3	localObject1	Object
    //   5	89	4	localObject2	Object
    //   107	28	4	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   50	55	71	java/io/FileNotFoundException
    //   55	64	107	java/io/IOException
    //   55	64	107	org/xmlpull/v1/XmlPullParserException
    //   55	64	107	com/android/server/pm/ShortcutService$InvalidFileFormatException
    //   55	64	146	finally
    //   109	140	146	finally
  }
  
  private void notifyListeners(String paramString, int paramInt)
  {
    injectPostToHandler(new -void_notifyListeners_java_lang_String_packageName_int_userId_LambdaImpl0(paramInt, paramString));
  }
  
  static boolean parseBooleanAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseLongAttribute(paramXmlPullParser, paramString) == 1L;
  }
  
  static ComponentName parseComponentNameAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser = parseStringAttribute(paramXmlPullParser, paramString);
    if (TextUtils.isEmpty(paramXmlPullParser)) {
      return null;
    }
    return ComponentName.unflattenFromString(paramXmlPullParser);
  }
  
  static int parseIntAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return (int)parseLongAttribute(paramXmlPullParser, paramString);
  }
  
  static int parseIntAttribute(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    return (int)parseLongAttribute(paramXmlPullParser, paramString, paramInt);
  }
  
  static Intent parseIntentAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramString = parseIntentAttributeNoDefault(paramXmlPullParser, paramString);
    paramXmlPullParser = paramString;
    if (paramString == null) {
      paramXmlPullParser = new Intent("android.intent.action.VIEW");
    }
    return paramXmlPullParser;
  }
  
  static Intent parseIntentAttributeNoDefault(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramString = parseStringAttribute(paramXmlPullParser, paramString);
    paramXmlPullParser = null;
    if (!TextUtils.isEmpty(paramString)) {}
    try
    {
      paramXmlPullParser = Intent.parseUri(paramString, 0);
      return paramXmlPullParser;
    }
    catch (URISyntaxException paramXmlPullParser)
    {
      Slog.e("ShortcutService", "Error parsing intent", paramXmlPullParser);
    }
    return null;
  }
  
  static long parseLongAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseLongAttribute(paramXmlPullParser, paramString, 0L);
  }
  
  static long parseLongAttribute(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = parseStringAttribute(paramXmlPullParser, paramString);
    if (TextUtils.isEmpty(paramXmlPullParser)) {
      return paramLong;
    }
    try
    {
      long l = Long.parseLong(paramXmlPullParser);
      return l;
    }
    catch (NumberFormatException paramString)
    {
      Slog.e("ShortcutService", "Error parsing long " + paramXmlPullParser);
    }
    return paramLong;
  }
  
  static String parseStringAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return paramXmlPullParser.getAttributeValue(null, paramString);
  }
  
  private void rescanUpdatedPackagesLocked(int paramInt, long paramLong, boolean paramBoolean)
  {
    ShortcutUser localShortcutUser = getUserShortcutsLocked(paramInt);
    long l = injectCurrentTimeMillis();
    forUpdatedPackages(paramInt, paramLong, this.mContext.getPackageManager().isUpgrade(), new -void_rescanUpdatedPackagesLocked_int_userId_long_lastScanTime_boolean_forceRescan_LambdaImpl0(localShortcutUser, paramInt, paramBoolean));
    localShortcutUser.setLastAppScanTime(l);
    localShortcutUser.setLastAppScanOsFingerprint(injectBuildFingerprint());
    scheduleSaveUser(paramInt);
  }
  
  private void saveUserInternalLocked(int paramInt, OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(paramOutputStream);
    FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
    localFastXmlSerializer.setOutput(localBufferedOutputStream, StandardCharsets.UTF_8.name());
    localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
    getUserShortcutsLocked(paramInt).saveToXml(localFastXmlSerializer, paramBoolean);
    localFastXmlSerializer.endDocument();
    localBufferedOutputStream.flush();
    paramOutputStream.flush();
  }
  
  private void saveUserLocked(int paramInt)
  {
    Object localObject = getUserFile(paramInt);
    if (DEBUG) {
      Slog.d("ShortcutService", "Saving to " + localObject);
    }
    ((File)localObject).getParentFile().mkdirs();
    AtomicFile localAtomicFile = new AtomicFile((File)localObject);
    localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      localObject = localFileOutputStream;
      saveUserInternalLocked(paramInt, localFileOutputStream, false);
      localObject = localFileOutputStream;
      localAtomicFile.finishWrite(localFileOutputStream);
      localObject = localFileOutputStream;
      cleanupDanglingBitmapDirectoriesLocked(paramInt);
      return;
    }
    catch (XmlPullParserException|IOException localXmlPullParserException)
    {
      Slog.e("ShortcutService", "Failed to write to file " + localAtomicFile.getBaseFile(), localXmlPullParserException);
      localAtomicFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  private void scheduleSaveBaseState()
  {
    scheduleSaveInner(55536);
  }
  
  private void scheduleSaveInner(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "Scheduling to save for " + paramInt);
    }
    synchronized (this.mLock)
    {
      if (!this.mDirtyUserIds.contains(Integer.valueOf(paramInt))) {
        this.mDirtyUserIds.add(Integer.valueOf(paramInt));
      }
      this.mHandler.removeCallbacks(this.mSaveDirtyInfoRunner);
      this.mHandler.postDelayed(this.mSaveDirtyInfoRunner, this.mSaveDelayMillis);
      return;
    }
  }
  
  static Bitmap shrinkBitmap(Bitmap paramBitmap, int paramInt)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if ((i <= paramInt) && (j <= paramInt))
    {
      if (DEBUG) {
        Slog.d("ShortcutService", String.format("Icon size %dx%d, no need to shrink", new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
      }
      return paramBitmap;
    }
    int m = Math.max(i, j);
    int k = i * paramInt / m;
    paramInt = j * paramInt / m;
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("Icon size %dx%d, shrinking to %dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt) }));
    }
    Bitmap localBitmap = Bitmap.createBitmap(k, paramInt, Bitmap.Config.ARGB_8888);
    new Canvas(localBitmap).drawBitmap(paramBitmap, null, new RectF(0.0F, 0.0F, k, paramInt), null);
    return localBitmap;
  }
  
  static IOException throwForInvalidTag(int paramInt, String paramString)
    throws IOException
  {
    throw new IOException(String.format("Invalid tag '%s' found at depth %d", new Object[] { paramString, Integer.valueOf(paramInt) }));
  }
  
  private void unloadUserLocked(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "unloadUserLocked: user=" + paramInt);
    }
    saveDirtyInfo();
    this.mUsers.delete(paramInt);
  }
  
  private void updateTimesLocked()
  {
    long l1 = injectCurrentTimeMillis();
    long l2 = this.mRawLastResetTime;
    if (this.mRawLastResetTime == 0L) {
      this.mRawLastResetTime = l1;
    }
    for (;;)
    {
      if (l2 != this.mRawLastResetTime) {
        scheduleSaveBaseState();
      }
      return;
      if (l1 < this.mRawLastResetTime)
      {
        if (isClockValid(l1))
        {
          Slog.w("ShortcutService", "Clock rewound");
          this.mRawLastResetTime = l1;
        }
      }
      else if (this.mRawLastResetTime + this.mResetInterval <= l1)
      {
        long l3 = this.mRawLastResetTime;
        long l4 = this.mResetInterval;
        this.mRawLastResetTime = (l1 / this.mResetInterval * this.mResetInterval + l3 % l4);
      }
    }
  }
  
  private void verifyCaller(String paramString, int paramInt)
  {
    Preconditions.checkStringNotEmpty(paramString, "packageName");
    if (isCallerSystem()) {
      return;
    }
    if (UserHandle.getUserId(injectBinderCallingUid()) != paramInt) {
      throw new SecurityException("Invalid user-ID");
    }
    if (injectGetPackageUid(paramString, paramInt) == injectBinderCallingUid()) {
      return;
    }
    throw new SecurityException("Calling package name mismatch");
  }
  
  private final void verifyStatesForce()
  {
    verifyStatesInner();
  }
  
  private void verifyStatesInner()
  {
    synchronized (this.mLock)
    {
      forEachLoadedUserLocked(new -void_verifyStatesInner__LambdaImpl0());
      return;
    }
  }
  
  static void warnForInvalidTag(int paramInt, String paramString)
    throws IOException
  {
    Slog.w("ShortcutService", String.format("Invalid tag '%s' found at depth %d", new Object[] { paramString, Integer.valueOf(paramInt) }));
  }
  
  static void writeAttr(XmlSerializer paramXmlSerializer, String paramString, long paramLong)
    throws IOException
  {
    writeAttr(paramXmlSerializer, paramString, String.valueOf(paramLong));
  }
  
  static void writeAttr(XmlSerializer paramXmlSerializer, String paramString, ComponentName paramComponentName)
    throws IOException
  {
    if (paramComponentName == null) {
      return;
    }
    writeAttr(paramXmlSerializer, paramString, paramComponentName.flattenToString());
  }
  
  static void writeAttr(XmlSerializer paramXmlSerializer, String paramString, Intent paramIntent)
    throws IOException
  {
    if (paramIntent == null) {
      return;
    }
    writeAttr(paramXmlSerializer, paramString, paramIntent.toUri(0));
  }
  
  static void writeAttr(XmlSerializer paramXmlSerializer, String paramString, CharSequence paramCharSequence)
    throws IOException
  {
    if (TextUtils.isEmpty(paramCharSequence)) {
      return;
    }
    paramXmlSerializer.attribute(null, paramString, paramCharSequence.toString());
  }
  
  static void writeAttr(XmlSerializer paramXmlSerializer, String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      writeAttr(paramXmlSerializer, paramString, "1");
    }
  }
  
  static void writeTagExtra(XmlSerializer paramXmlSerializer, String paramString, PersistableBundle paramPersistableBundle)
    throws IOException, XmlPullParserException
  {
    if (paramPersistableBundle == null) {
      return;
    }
    paramXmlSerializer.startTag(null, paramString);
    paramPersistableBundle.saveToXml(paramXmlSerializer);
    paramXmlSerializer.endTag(null, paramString);
  }
  
  static void writeTagValue(XmlSerializer paramXmlSerializer, String paramString, long paramLong)
    throws IOException
  {
    writeTagValue(paramXmlSerializer, paramString, Long.toString(paramLong));
  }
  
  static void writeTagValue(XmlSerializer paramXmlSerializer, String paramString, ComponentName paramComponentName)
    throws IOException
  {
    if (paramComponentName == null) {
      return;
    }
    writeTagValue(paramXmlSerializer, paramString, paramComponentName.flattenToString());
  }
  
  static void writeTagValue(XmlSerializer paramXmlSerializer, String paramString1, String paramString2)
    throws IOException
  {
    if (TextUtils.isEmpty(paramString2)) {
      return;
    }
    paramXmlSerializer.startTag(null, paramString1);
    paramXmlSerializer.attribute(null, "value", paramString2);
    paramXmlSerializer.endTag(null, paramString1);
  }
  
  public boolean addDynamicShortcuts(String paramString, ParceledListSlice arg2, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    List localList = ???.getList();
    int j = localList.size();
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      ShortcutPackage localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt);
      localShortcutPackage.ensureImmutableShortcutsNotIncluded(localList);
      fillInDefaultActivity(localList);
      localShortcutPackage.enforceShortcutCountsBeforeOperation(localList, 1);
      localShortcutPackage.clearAllImplicitRanks();
      assignImplicitRanks(localList);
      boolean bool = localShortcutPackage.tryApiCall();
      if (!bool) {
        return false;
      }
      int i = 0;
      while (i < j)
      {
        ShortcutInfo localShortcutInfo = (ShortcutInfo)localList.get(i);
        fixUpIncomingShortcutInfo(localShortcutInfo, false);
        localShortcutInfo.setRankChanged();
        localShortcutPackage.addOrUpdateDynamicShortcut(localShortcutInfo);
        i += 1;
      }
      localShortcutPackage.adjustRanks();
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return true;
    }
  }
  
  public void applyRestore(byte[] paramArrayOfByte, int paramInt)
  {
    enforceSystem();
    if (DEBUG) {
      Slog.d("ShortcutService", "Restoring user " + paramInt);
    }
    synchronized (this.mLock)
    {
      if (!isUserUnlockedL(paramInt))
      {
        wtf("Can't restore: user " + paramInt + " is locked or not running");
        return;
      }
      paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
      try
      {
        paramArrayOfByte = loadUserInternal(paramInt, paramArrayOfByte, true);
        getUserShortcutsLocked(paramInt).mergeRestoredFile(paramArrayOfByte);
        rescanUpdatedPackagesLocked(paramInt, 0L, true);
        saveUserLocked(paramInt);
        return;
      }
      catch (XmlPullParserException|IOException|InvalidFileFormatException paramArrayOfByte)
      {
        Slog.w("ShortcutService", "Restoration failed.", paramArrayOfByte);
        return;
      }
    }
  }
  
  /* Error */
  void checkPackageChanges(int paramInt)
  {
    // Byte code:
    //   0: getstatic 375	com/android/server/pm/ShortcutService:DEBUG	Z
    //   3: ifeq +29 -> 32
    //   6: ldc -83
    //   8: new 628	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 629	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 1707
    //   18: invokevirtual 635	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: iload_1
    //   22: invokevirtual 638	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   25: invokevirtual 641	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 647	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: aload_0
    //   33: invokevirtual 1710	com/android/server/pm/ShortcutService:injectIsSafeModeEnabled	()Z
    //   36: ifeq +13 -> 49
    //   39: ldc -83
    //   41: ldc_w 1712
    //   44: invokestatic 1715	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   47: pop
    //   48: return
    //   49: aload_0
    //   50: invokevirtual 651	com/android/server/pm/ShortcutService:injectElapsedRealtime	()J
    //   53: lstore_3
    //   54: new 392	java/util/ArrayList
    //   57: dup
    //   58: invokespecial 473	java/util/ArrayList:<init>	()V
    //   61: astore 6
    //   63: aload_0
    //   64: getfield 330	com/android/server/pm/ShortcutService:mLock	Ljava/lang/Object;
    //   67: astore 5
    //   69: aload 5
    //   71: monitorenter
    //   72: aload_0
    //   73: iload_1
    //   74: invokevirtual 655	com/android/server/pm/ShortcutService:getUserShortcutsLocked	(I)Lcom/android/server/pm/ShortcutUser;
    //   77: astore 7
    //   79: aload 7
    //   81: new 33	com/android/server/pm/ShortcutService$-void_checkPackageChanges_int_ownerUserId_LambdaImpl0
    //   84: dup
    //   85: aload_0
    //   86: aload 6
    //   88: invokespecial 1718	com/android/server/pm/ShortcutService$-void_checkPackageChanges_int_ownerUserId_LambdaImpl0:<init>	(Lcom/android/server/pm/ShortcutService;Ljava/util/ArrayList;)V
    //   91: invokevirtual 300	com/android/server/pm/ShortcutUser:forAllPackageItems	(Ljava/util/function/Consumer;)V
    //   94: aload 6
    //   96: invokevirtual 1627	java/util/ArrayList:size	()I
    //   99: ifle +49 -> 148
    //   102: aload 6
    //   104: invokevirtual 1627	java/util/ArrayList:size	()I
    //   107: iconst_1
    //   108: isub
    //   109: istore_2
    //   110: iload_2
    //   111: iflt +37 -> 148
    //   114: aload 6
    //   116: iload_2
    //   117: invokevirtual 1628	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   120: checkcast 1607	com/android/server/pm/ShortcutUser$PackageWithUser
    //   123: astore 8
    //   125: aload_0
    //   126: aload 8
    //   128: getfield 1719	com/android/server/pm/ShortcutUser$PackageWithUser:packageName	Ljava/lang/String;
    //   131: iload_1
    //   132: aload 8
    //   134: getfield 1722	com/android/server/pm/ShortcutUser$PackageWithUser:userId	I
    //   137: iconst_0
    //   138: invokevirtual 1586	com/android/server/pm/ShortcutService:cleanUpPackageLocked	(Ljava/lang/String;IIZ)V
    //   141: iload_2
    //   142: iconst_1
    //   143: isub
    //   144: istore_2
    //   145: goto -35 -> 110
    //   148: aload_0
    //   149: iload_1
    //   150: aload 7
    //   152: invokevirtual 1725	com/android/server/pm/ShortcutUser:getLastAppScanTime	()J
    //   155: iconst_0
    //   156: invokespecial 1699	com/android/server/pm/ShortcutService:rescanUpdatedPackagesLocked	(IJZ)V
    //   159: aload 5
    //   161: monitorexit
    //   162: aload_0
    //   163: bipush 8
    //   165: lload_3
    //   166: invokevirtual 691	com/android/server/pm/ShortcutService:logDurationStat	(IJ)V
    //   169: aload_0
    //   170: invokevirtual 1123	com/android/server/pm/ShortcutService:verifyStates	()V
    //   173: return
    //   174: astore 6
    //   176: aload 5
    //   178: monitorexit
    //   179: aload 6
    //   181: athrow
    //   182: astore 5
    //   184: aload_0
    //   185: bipush 8
    //   187: lload_3
    //   188: invokevirtual 691	com/android/server/pm/ShortcutService:logDurationStat	(IJ)V
    //   191: aload 5
    //   193: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	194	0	this	ShortcutService
    //   0	194	1	paramInt	int
    //   109	36	2	i	int
    //   53	135	3	l	long
    //   182	10	5	localObject2	Object
    //   61	54	6	localArrayList	ArrayList
    //   174	6	6	localObject3	Object
    //   77	74	7	localShortcutUser	ShortcutUser
    //   123	10	8	localPackageWithUser	ShortcutUser.PackageWithUser
    // Exception table:
    //   from	to	target	type
    //   72	110	174	finally
    //   114	141	174	finally
    //   148	159	174	finally
    //   54	72	182	finally
    //   159	162	182	finally
    //   176	182	182	finally
  }
  
  void cleanUpPackageLocked(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    boolean bool = isUserLoadedLocked(paramInt1);
    ShortcutUser localShortcutUser = getUserShortcutsLocked(paramInt1);
    int j = 0;
    int i = j;
    if (paramInt2 == paramInt1)
    {
      i = j;
      if (localShortcutUser.removePackage(paramString) != null) {
        i = 1;
      }
    }
    localShortcutUser.removeLauncher(paramInt2, paramString);
    localShortcutUser.forAllLaunchers(new -void_cleanUpPackageLocked_java_lang_String_packageName_int_owningUserId_int_packageUserId_boolean_appStillExists_LambdaImpl0(paramString, paramInt2));
    localShortcutUser.forAllPackages(new -void_cleanUpPackageLocked_java_lang_String_packageName_int_owningUserId_int_packageUserId_boolean_appStillExists_LambdaImpl1());
    scheduleSaveUser(paramInt1);
    if (i != 0) {
      notifyListeners(paramString, paramInt1);
    }
    if ((paramBoolean) && (paramInt2 == paramInt1)) {
      localShortcutUser.rescanPackageIfNeeded(paramString, true);
    }
    if (!bool) {
      unloadUserLocked(paramInt1);
    }
  }
  
  public void cleanupBitmapsForPackage(int paramInt, String paramString)
  {
    paramString = new File(getUserBitmapFilePath(paramInt), paramString);
    if (!paramString.isDirectory()) {
      return;
    }
    if (FileUtils.deleteContents(paramString)) {}
    for (boolean bool = paramString.delete();; bool = false)
    {
      if (!bool) {
        Slog.w("ShortcutService", "Unable to remove directory " + paramString);
      }
      return;
    }
  }
  
  public void disableShortcuts(String paramString, List paramList, CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    verifyCaller(paramString, paramInt2);
    Preconditions.checkNotNull(paramList, "shortcutIds must be provided");
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt2);
      ShortcutPackage localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt2);
      localShortcutPackage.ensureImmutableShortcutsNotIncludedWithIds(paramList);
      if (paramCharSequence == null) {}
      for (paramCharSequence = null;; paramCharSequence = paramCharSequence.toString())
      {
        int i = paramList.size() - 1;
        while (i >= 0)
        {
          localShortcutPackage.disableWithId((String)Preconditions.checkStringNotEmpty((String)paramList.get(i)), paramCharSequence, paramInt1, false);
          i -= 1;
        }
      }
      localShortcutPackage.adjustRanks();
      packageShortcutsChanged(paramString, paramInt2);
      verifyStates();
      return;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    enforceCallingOrSelfPermission("android.permission.DUMP", "can't dump by this caller");
    int k = 0;
    int i = 0;
    boolean bool2 = false;
    boolean bool1 = false;
    if (paramArrayOfString != null)
    {
      int j = 0;
      int m = paramArrayOfString.length;
      k = i;
      bool2 = bool1;
      if (j < m)
      {
        paramFileDescriptor = paramArrayOfString[j];
        if ("-c".equals(paramFileDescriptor)) {
          i = 1;
        }
        for (;;)
        {
          j += 1;
          break;
          if ("--checkin".equals(paramFileDescriptor))
          {
            i = 1;
            bool1 = true;
          }
        }
      }
    }
    if (k != 0)
    {
      dumpCheckin(paramPrintWriter, bool2);
      return;
    }
    dumpInner(paramPrintWriter);
  }
  
  public void enableShortcuts(String paramString, List paramList, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    Preconditions.checkNotNull(paramList, "shortcutIds must be provided");
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      ShortcutPackage localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt);
      localShortcutPackage.ensureImmutableShortcutsNotIncludedWithIds(paramList);
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        localShortcutPackage.enableWithId((String)paramList.get(i));
        i -= 1;
      }
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return;
    }
  }
  
  void enforceMaxActivityShortcuts(int paramInt)
  {
    if (paramInt > this.mMaxShortcuts) {
      throw new IllegalArgumentException("Max number of dynamic shortcuts exceeded");
    }
  }
  
  void fixUpShortcutResourceNamesAndValues(ShortcutInfo paramShortcutInfo)
  {
    Resources localResources = injectGetResourcesForApplicationAsUser(paramShortcutInfo.getPackage(), paramShortcutInfo.getUserId());
    long l;
    if (localResources != null) {
      l = injectElapsedRealtime();
    }
    try
    {
      paramShortcutInfo.lookupAndFillInResourceNames(localResources);
      logDurationStat(10, l);
      paramShortcutInfo.resolveResourceStrings(localResources);
      return;
    }
    finally
    {
      logDurationStat(10, l);
    }
  }
  
  void forEachLoadedUserLocked(Consumer<ShortcutUser> paramConsumer)
  {
    int i = this.mUsers.size() - 1;
    while (i >= 0)
    {
      paramConsumer.accept((ShortcutUser)this.mUsers.valueAt(i));
      i -= 1;
    }
  }
  
  final ActivityInfo getActivityInfoWithMetadata(ComponentName paramComponentName, int paramInt)
  {
    return isInstalledOrNull(injectGetActivityInfoWithMetadataWithUninstalled(paramComponentName, paramInt));
  }
  
  final ApplicationInfo getApplicationInfo(String paramString, int paramInt)
  {
    return isInstalledOrNull(injectApplicationInfoWithUninstalled(paramString, paramInt));
  }
  
  public byte[] getBackupPayload(int paramInt)
  {
    enforceSystem();
    if (DEBUG) {
      Slog.d("ShortcutService", "Backing up user " + paramInt);
    }
    synchronized (this.mLock)
    {
      if (!isUserUnlockedL(paramInt))
      {
        wtf("Can't backup: user " + paramInt + " is locked or not running");
        return null;
      }
      Object localObject2 = getUserShortcutsLocked(paramInt);
      if (localObject2 == null)
      {
        wtf("Can't backup: user not found: id=" + paramInt);
        return null;
      }
      ((ShortcutUser)localObject2).forAllPackageItems(new -byte__getBackupPayload_int_userId_LambdaImpl0());
      ((ShortcutUser)localObject2).forAllLaunchers(new -byte__getBackupPayload_int_userId_LambdaImpl1());
      scheduleSaveUser(paramInt);
      saveDirtyInfo();
      localObject2 = new ByteArrayOutputStream(32768);
      try
      {
        saveUserInternalLocked(paramInt, (OutputStream)localObject2, true);
        localObject2 = ((ByteArrayOutputStream)localObject2).toByteArray();
        return (byte[])localObject2;
      }
      catch (XmlPullParserException|IOException localXmlPullParserException)
      {
        Slog.w("ShortcutService", "Backup failed.", localXmlPullParserException);
        return null;
      }
    }
  }
  
  public ParceledListSlice<ShortcutInfo> getDynamicShortcuts(String paramString, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      paramString = getShortcutsWithQueryLocked(paramString, paramInt, 9, new -android_content_pm_ParceledListSlice_getDynamicShortcuts_java_lang_String_packageName_int_userId_LambdaImpl0());
      return paramString;
    }
  }
  
  public int getIconMaxDimensions(String arg1, int paramInt)
  {
    verifyCaller(???, paramInt);
    synchronized (this.mLock)
    {
      paramInt = this.mMaxIconDimension;
      return paramInt;
    }
  }
  
  Bitmap.CompressFormat getIconPersistFormatForTest()
  {
    return this.mIconPersistFormat;
  }
  
  int getIconPersistQualityForTest()
  {
    return this.mIconPersistQuality;
  }
  
  final List<PackageInfo> getInstalledPackages(int paramInt)
  {
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      List localList = injectGetPackagesWithUninstalled(paramInt);
      localList.removeIf(PACKAGE_NOT_INSTALLED);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.wtf("ShortcutService", "RemoteException", localRemoteException);
      return null;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(7, l1);
    }
  }
  
  long getLastResetTimeLocked()
  {
    updateTimesLocked();
    return this.mRawLastResetTime;
  }
  
  @GuardedBy("mLock")
  ShortcutLauncher getLauncherShortcutsLocked(String paramString, int paramInt1, int paramInt2)
  {
    return getUserShortcutsLocked(paramInt1).getLauncherShortcuts(paramString, paramInt2);
  }
  
  public ParceledListSlice<ShortcutInfo> getManifestShortcuts(String paramString, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      paramString = getShortcutsWithQueryLocked(paramString, paramInt, 9, new -android_content_pm_ParceledListSlice_getManifestShortcuts_java_lang_String_packageName_int_userId_LambdaImpl0());
      return paramString;
    }
  }
  
  int getMaxActivityShortcuts()
  {
    return this.mMaxShortcuts;
  }
  
  int getMaxIconDimensionForTest()
  {
    return this.mMaxIconDimension;
  }
  
  public int getMaxShortcutCountPerActivity(String paramString, int paramInt)
    throws RemoteException
  {
    verifyCaller(paramString, paramInt);
    return this.mMaxShortcuts;
  }
  
  int getMaxShortcutsForTest()
  {
    return this.mMaxShortcuts;
  }
  
  int getMaxUpdatesPerIntervalForTest()
  {
    return this.mMaxUpdatesPerInterval;
  }
  
  long getNextResetTimeLocked()
  {
    updateTimesLocked();
    return this.mRawLastResetTime + this.mResetInterval;
  }
  
  final PackageInfo getPackageInfo(String paramString, int paramInt)
  {
    return getPackageInfo(paramString, paramInt, false);
  }
  
  final PackageInfo getPackageInfo(String paramString, int paramInt, boolean paramBoolean)
  {
    return isInstalledOrNull(injectPackageInfoWithUninstalled(paramString, paramInt, paramBoolean));
  }
  
  final PackageInfo getPackageInfoWithSignatures(String paramString, int paramInt)
  {
    return getPackageInfo(paramString, paramInt, true);
  }
  
  ShortcutInfo getPackageShortcutForTest(String paramString1, String paramString2, int paramInt)
  {
    synchronized (this.mLock)
    {
      paramString1 = getPackageShortcutForTest(paramString1, paramInt);
      if (paramString1 == null) {
        return null;
      }
      paramString1 = paramString1.findShortcutById(paramString2);
      return paramString1;
    }
  }
  
  ShortcutPackage getPackageShortcutForTest(String paramString, int paramInt)
  {
    synchronized (this.mLock)
    {
      ShortcutUser localShortcutUser = (ShortcutUser)this.mUsers.get(paramInt);
      if (localShortcutUser == null) {
        return null;
      }
      paramString = (ShortcutPackage)localShortcutUser.getAllPackagesForTest().get(paramString);
      return paramString;
    }
  }
  
  @GuardedBy("mLock")
  ShortcutPackage getPackageShortcutsForPublisherLocked(String paramString, int paramInt)
  {
    ShortcutPackage localShortcutPackage = getUserShortcutsLocked(paramInt).getPackageShortcuts(paramString);
    localShortcutPackage.getUser().onCalledByPublisher(paramString);
    return localShortcutPackage;
  }
  
  @GuardedBy("mLock")
  ShortcutPackage getPackageShortcutsLocked(String paramString, int paramInt)
  {
    return getUserShortcutsLocked(paramInt).getPackageShortcuts(paramString);
  }
  
  public ParceledListSlice<ShortcutInfo> getPinnedShortcuts(String paramString, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      paramString = getShortcutsWithQueryLocked(paramString, paramInt, 9, new -android_content_pm_ParceledListSlice_getPinnedShortcuts_java_lang_String_packageName_int_userId_LambdaImpl0());
      return paramString;
    }
  }
  
  public long getRateLimitResetTime(String arg1, int paramInt)
  {
    verifyCaller(???, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      long l = getNextResetTimeLocked();
      return l;
    }
  }
  
  public int getRemainingCallCount(String paramString, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      paramString = getPackageShortcutsForPublisherLocked(paramString, paramInt);
      paramInt = this.mMaxUpdatesPerInterval;
      int i = paramString.getApiCallCount();
      return paramInt - i;
    }
  }
  
  long getResetIntervalForTest()
  {
    return this.mResetInterval;
  }
  
  SparseArray<ShortcutUser> getShortcutsForTest()
  {
    return this.mUsers;
  }
  
  long getUidLastForegroundElapsedTimeLocked(int paramInt)
  {
    return this.mUidLastForegroundElapsedTime.get(paramInt);
  }
  
  File getUserBitmapFilePath(int paramInt)
  {
    return new File(injectUserDataPath(paramInt), "bitmaps");
  }
  
  final File getUserFile(int paramInt)
  {
    return new File(injectUserDataPath(paramInt), "shortcuts.xml");
  }
  
  @GuardedBy("mLock")
  ShortcutUser getUserShortcutsLocked(int paramInt)
  {
    if (!isUserUnlockedL(paramInt)) {
      wtf("User still locked");
    }
    ShortcutUser localShortcutUser2 = (ShortcutUser)this.mUsers.get(paramInt);
    ShortcutUser localShortcutUser1 = localShortcutUser2;
    if (localShortcutUser2 == null)
    {
      localShortcutUser2 = loadUserLocked(paramInt);
      localShortcutUser1 = localShortcutUser2;
      if (localShortcutUser2 == null) {
        localShortcutUser1 = new ShortcutUser(this, paramInt);
      }
      this.mUsers.put(paramInt, localShortcutUser1);
      checkPackageChanges(paramInt);
    }
    return localShortcutUser1;
  }
  
  void handleCleanupUser(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "handleCleanupUser: user=" + paramInt);
    }
    synchronized (this.mLock)
    {
      unloadUserLocked(paramInt);
      this.mUnlockedUsers.put(paramInt, false);
      return;
    }
  }
  
  void handleLocaleChanged()
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "handleLocaleChanged");
    }
    scheduleSaveBaseState();
    synchronized (this.mLock)
    {
      long l = injectClearCallingIdentity();
      try
      {
        forEachLoadedUserLocked(new -void_handleLocaleChanged__LambdaImpl0());
        injectRestoreCallingIdentity(l);
        return;
      }
      finally
      {
        localObject2 = finally;
        injectRestoreCallingIdentity(l);
        throw ((Throwable)localObject2);
      }
    }
  }
  
  void handleOnUidStateChanged(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mUidState.put(paramInt1, paramInt2);
      if (isProcessStateForeground(paramInt2)) {
        this.mUidLastForegroundElapsedTime.put(paramInt1, injectElapsedRealtime());
      }
      return;
    }
  }
  
  void handleUnlockUser(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "handleUnlockUser: user=" + paramInt);
    }
    synchronized (this.mLock)
    {
      this.mUnlockedUsers.put(paramInt, true);
      injectRunOnNewThread(new -void_handleUnlockUser_int_userId_LambdaImpl0(injectElapsedRealtime(), paramInt));
      return;
    }
  }
  
  boolean hasShortcutHostPermission(String paramString, int paramInt)
  {
    long l = injectElapsedRealtime();
    try
    {
      boolean bool = hasShortcutHostPermissionInner(paramString, paramInt);
      return bool;
    }
    finally
    {
      logDurationStat(4, l);
    }
  }
  
  boolean hasShortcutHostPermissionInner(String paramString, int paramInt)
  {
    for (;;)
    {
      ShortcutUser localShortcutUser;
      boolean bool;
      Object localObject2;
      synchronized (this.mLock)
      {
        throwIfUserLockedL(paramInt);
        localShortcutUser = getUserShortcutsLocked(paramInt);
        localObject1 = localShortcutUser.getCachedLauncher();
        if (localObject1 != null)
        {
          bool = ((ComponentName)localObject1).getPackageName().equals(paramString);
          if (bool) {
            return true;
          }
        }
        ArrayList localArrayList = new ArrayList();
        long l = injectElapsedRealtime();
        ComponentName localComponentName = this.mPackageManagerInternal.getHomeActivitiesAsUser(localArrayList, paramInt);
        logDurationStat(0, l);
        if (localComponentName != null)
        {
          localObject2 = localComponentName;
          localObject1 = localObject2;
          if (DEBUG)
          {
            Slog.v("ShortcutService", "Default launcher from PM: " + localComponentName);
            localObject1 = localObject2;
          }
          localObject2 = localObject1;
          if (localObject1 != null) {
            break label401;
          }
          int k = localArrayList.size();
          i = Integer.MIN_VALUE;
          paramInt = 0;
          localObject2 = localObject1;
          if (paramInt >= k) {
            break label401;
          }
          localObject2 = (ResolveInfo)localArrayList.get(paramInt);
          if (((ResolveInfo)localObject2).activityInfo.applicationInfo.isSystemApp()) {
            break label325;
          }
          j = i;
          break label468;
        }
        localObject2 = localShortcutUser.getLastKnownLauncher();
        localObject1 = localObject2;
        if (localObject2 == null) {
          continue;
        }
        if (injectIsActivityEnabledAndExported((ComponentName)localObject2, paramInt))
        {
          localObject1 = localObject2;
          if (!DEBUG) {
            continue;
          }
          Slog.v("ShortcutService", "Cached launcher: " + localObject2);
          localObject1 = localObject2;
        }
      }
      Slog.w("ShortcutService", "Cached launcher " + localObject2 + " no longer exists");
      Object localObject1 = null;
      localShortcutUser.clearLauncher();
      continue;
      label325:
      if (DEBUG) {
        Slog.d("ShortcutService", String.format("hasShortcutPermissionInner: pkg=%s prio=%d", new Object[] { ((ResolveInfo)localObject2).activityInfo.getComponentName(), Integer.valueOf(((ResolveInfo)localObject2).priority) }));
      }
      int j = i;
      if (((ResolveInfo)localObject2).priority >= i)
      {
        localObject1 = ((ResolveInfo)localObject2).activityInfo.getComponentName();
        j = ((ResolveInfo)localObject2).priority;
        break label468;
        label401:
        localShortcutUser.setLauncher((ComponentName)localObject2);
        if (localObject2 != null)
        {
          if (DEBUG) {
            Slog.v("ShortcutService", "Detected launcher: " + localObject2);
          }
          bool = ((ComponentName)localObject2).getPackageName().equals(paramString);
          return bool;
        }
        return false;
      }
      label468:
      paramInt += 1;
      int i = j;
    }
  }
  
  ApplicationInfo injectApplicationInfoWithUninstalled(String paramString, int paramInt)
  {
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      paramString = this.mIPackageManager.getApplicationInfo(paramString, 794624, paramInt);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      Slog.wtf("ShortcutService", "RemoteException", paramString);
      return null;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(3, l1);
    }
  }
  
  int injectBinderCallingUid()
  {
    return getCallingUid();
  }
  
  String injectBuildFingerprint()
  {
    return Build.FINGERPRINT;
  }
  
  long injectClearCallingIdentity()
  {
    return Binder.clearCallingIdentity();
  }
  
  long injectCurrentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  int injectDipToPixel(int paramInt)
  {
    return (int)TypedValue.applyDimension(1, paramInt, this.mContext.getResources().getDisplayMetrics());
  }
  
  long injectElapsedRealtime()
  {
    return SystemClock.elapsedRealtime();
  }
  
  void injectEnforceCallingPermission(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingPermission(paramString1, paramString2);
  }
  
  ActivityInfo injectGetActivityInfoWithMetadataWithUninstalled(ComponentName paramComponentName, int paramInt)
  {
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      paramComponentName = this.mIPackageManager.getActivityInfo(paramComponentName, 794752, paramInt);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      Slog.wtf("ShortcutService", "RemoteException", paramComponentName);
      return null;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(6, l1);
    }
  }
  
  /* Error */
  ComponentName injectGetDefaultMainActivity(String paramString, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_0
    //   4: invokevirtual 651	com/android/server/pm/ShortcutService:injectElapsedRealtime	()J
    //   7: lstore_3
    //   8: aload_0
    //   9: invokevirtual 1850	com/android/server/pm/ShortcutService:injectClearCallingIdentity	()J
    //   12: lstore 5
    //   14: aload_0
    //   15: aload_0
    //   16: invokespecial 2064	com/android/server/pm/ShortcutService:getMainActivityIntent	()Landroid/content/Intent;
    //   19: aload_1
    //   20: aconst_null
    //   21: iload_2
    //   22: invokevirtual 2068	com/android/server/pm/ShortcutService:queryActivities	(Landroid/content/Intent;Ljava/lang/String;Landroid/content/ComponentName;I)Ljava/util/List;
    //   25: astore_1
    //   26: aload_1
    //   27: invokeinterface 612 1 0
    //   32: istore_2
    //   33: iload_2
    //   34: ifne +21 -> 55
    //   37: aload 7
    //   39: astore_1
    //   40: aload_0
    //   41: lload 5
    //   43: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   46: aload_0
    //   47: bipush 11
    //   49: lload_3
    //   50: invokevirtual 691	com/android/server/pm/ShortcutService:logDurationStat	(IJ)V
    //   53: aload_1
    //   54: areturn
    //   55: aload_1
    //   56: iconst_0
    //   57: invokeinterface 615 2 0
    //   62: checkcast 304	android/content/pm/ResolveInfo
    //   65: getfield 308	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   68: invokevirtual 2003	android/content/pm/ActivityInfo:getComponentName	()Landroid/content/ComponentName;
    //   71: astore_1
    //   72: goto -32 -> 40
    //   75: astore_1
    //   76: aload_0
    //   77: lload 5
    //   79: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   82: aload_0
    //   83: bipush 11
    //   85: lload_3
    //   86: invokevirtual 691	com/android/server/pm/ShortcutService:logDurationStat	(IJ)V
    //   89: aload_1
    //   90: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	91	0	this	ShortcutService
    //   0	91	1	paramString	String
    //   0	91	2	paramInt	int
    //   7	79	3	l1	long
    //   12	66	5	l2	long
    //   1	37	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   14	33	75	finally
    //   55	72	75	finally
  }
  
  public String injectGetLocaleTagsForUser(int paramInt)
  {
    return LocaleList.getDefault().toLanguageTags();
  }
  
  List<ResolveInfo> injectGetMainActivities(String paramString, int paramInt)
  {
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      paramString = queryActivities(getMainActivityIntent(), paramString, null, paramInt);
      return paramString;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(12, l1);
    }
  }
  
  int injectGetPackageUid(String paramString, int paramInt)
  {
    long l = injectClearCallingIdentity();
    try
    {
      paramInt = this.mIPackageManager.getPackageUid(paramString, 794624, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString)
    {
      Slog.wtf("ShortcutService", "RemoteException", paramString);
      return -1;
    }
    finally
    {
      injectRestoreCallingIdentity(l);
    }
  }
  
  List<PackageInfo> injectGetPackagesWithUninstalled(int paramInt)
    throws RemoteException
  {
    ParceledListSlice localParceledListSlice = this.mIPackageManager.getInstalledPackages(794624, paramInt);
    if (localParceledListSlice == null) {
      return Collections.emptyList();
    }
    return localParceledListSlice.getList();
  }
  
  Resources injectGetResourcesForApplicationAsUser(String paramString, int paramInt)
  {
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      Resources localResources = this.mContext.getPackageManager().getResourcesForApplicationAsUser(paramString, paramInt);
      return localResources;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Slog.e("ShortcutService", "Resources for package " + paramString + " not found");
      return null;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(9, l1);
    }
  }
  
  boolean injectIsActivityEnabledAndExported(ComponentName paramComponentName, int paramInt)
  {
    boolean bool = false;
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      paramInt = queryActivities(new Intent(), paramComponentName.getPackageName(), paramComponentName, paramInt).size();
      if (paramInt > 0) {
        bool = true;
      }
      return bool;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(13, l1);
    }
  }
  
  boolean injectIsLowRamDevice()
  {
    return ActivityManager.isLowRamDeviceStatic();
  }
  
  boolean injectIsMainActivity(ComponentName paramComponentName, int paramInt)
  {
    boolean bool = false;
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      paramInt = queryActivities(getMainActivityIntent(), paramComponentName.getPackageName(), paramComponentName, paramInt).size();
      if (paramInt > 0) {
        bool = true;
      }
      return bool;
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      logDurationStat(12, l1);
    }
  }
  
  boolean injectIsSafeModeEnabled()
  {
    long l = injectClearCallingIdentity();
    try
    {
      boolean bool = IWindowManager.Stub.asInterface(ServiceManager.getService("window")).isSafeModeEnabled();
      injectRestoreCallingIdentity(l);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      injectRestoreCallingIdentity(l);
      return false;
    }
    finally
    {
      localObject = finally;
      injectRestoreCallingIdentity(l);
      throw ((Throwable)localObject);
    }
  }
  
  PackageInfo injectPackageInfoWithUninstalled(String paramString, int paramInt, boolean paramBoolean)
  {
    int i = 2;
    long l1 = injectElapsedRealtime();
    long l2 = injectClearCallingIdentity();
    try
    {
      IPackageManager localIPackageManager = this.mIPackageManager;
      int j;
      if (paramBoolean)
      {
        j = 64;
        paramString = localIPackageManager.getPackageInfo(paramString, j | 0xC2000, paramInt);
        injectRestoreCallingIdentity(l2);
        if (!paramBoolean) {
          break label71;
        }
      }
      label71:
      for (paramInt = 2;; paramInt = 1)
      {
        logDurationStat(paramInt, l1);
        return paramString;
        j = 0;
        break;
      }
      logDurationStat(i, l1);
    }
    catch (RemoteException paramString)
    {
      Slog.wtf("ShortcutService", "RemoteException", paramString);
      injectRestoreCallingIdentity(l2);
      if (paramBoolean) {}
      for (;;)
      {
        logDurationStat(i, l1);
        return null;
        i = 1;
      }
    }
    finally
    {
      injectRestoreCallingIdentity(l2);
      if (!paramBoolean) {}
    }
    for (;;)
    {
      throw paramString;
      i = 1;
    }
  }
  
  void injectPostToHandler(Runnable paramRunnable)
  {
    this.mHandler.post(paramRunnable);
  }
  
  void injectRegisterUidObserver(IUidObserver paramIUidObserver, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().registerUidObserver(paramIUidObserver, paramInt);
      return;
    }
    catch (RemoteException paramIUidObserver) {}
  }
  
  void injectRestoreCallingIdentity(long paramLong)
  {
    Binder.restoreCallingIdentity(paramLong);
  }
  
  void injectRunOnNewThread(Runnable paramRunnable)
  {
    new Thread(paramRunnable).start();
  }
  
  String injectShortcutManagerConstants()
  {
    return Settings.Global.getString(this.mContext.getContentResolver(), "shortcut_manager_constants");
  }
  
  boolean injectShouldPerformVerification()
  {
    return DEBUG;
  }
  
  File injectSystemDataPath()
  {
    return Environment.getDataSystemDirectory();
  }
  
  File injectUserDataPath(int paramInt)
  {
    return new File(Environment.getDataSystemCeDirectory(paramInt), "shortcut_service");
  }
  
  void injectValidateIconResPackage(ShortcutInfo paramShortcutInfo, Icon paramIcon)
  {
    if (!paramShortcutInfo.getPackage().equals(paramIcon.getResPackage())) {
      throw new IllegalArgumentException("Icon resource must reside in shortcut owner package");
    }
  }
  
  XmlResourceParser injectXmlMetaData(ActivityInfo paramActivityInfo, String paramString)
  {
    return paramActivityInfo.loadXmlMetaData(this.mContext.getPackageManager(), paramString);
  }
  
  boolean isPackageInstalled(String paramString, int paramInt)
  {
    return getApplicationInfo(paramString, paramInt) != null;
  }
  
  boolean isUidForegroundLocked(int paramInt)
  {
    if (paramInt == 1000) {
      return true;
    }
    if (isProcessStateForeground(this.mUidState.get(paramInt, 16))) {
      return true;
    }
    return isProcessStateForeground(this.mActivityManagerInternal.getUidProcessState(paramInt));
  }
  
  protected boolean isUserUnlockedL(int paramInt)
  {
    if (this.mUnlockedUsers.get(paramInt)) {
      return true;
    }
    long l = injectClearCallingIdentity();
    try
    {
      boolean bool = this.mUserManager.isUserUnlockingOrUnlocked(paramInt);
      return bool;
    }
    finally
    {
      injectRestoreCallingIdentity(l);
    }
  }
  
  void logDurationStat(int paramInt, long paramLong)
  {
    synchronized (this.mStatLock)
    {
      Object localObject2 = this.mCountStats;
      localObject2[paramInt] += 1;
      localObject2 = this.mDurationStats;
      localObject2[paramInt] += injectElapsedRealtime() - paramLong;
      return;
    }
  }
  
  public void onApplicationActive(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "onApplicationActive: package=" + paramString + "  userid=" + paramInt);
    }
    enforceResetThrottlingPermission();
    synchronized (this.mLock)
    {
      boolean bool = isUserUnlockedL(paramInt);
      if (!bool) {
        return;
      }
      getPackageShortcutsLocked(paramString, paramInt).resetRateLimitingForCommandLineNoSaving();
      saveUserLocked(paramInt);
      return;
    }
  }
  
  void onBootPhase(int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "onBootPhase: " + paramInt);
    }
    switch (paramInt)
    {
    default: 
      return;
    case 480: 
      initialize();
      return;
    }
    this.mBootCompleted.set(true);
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    enforceShell();
    long l = injectClearCallingIdentity();
    try
    {
      paramResultReceiver.send(new MyShellCommand(null).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver), null);
      return;
    }
    finally
    {
      injectRestoreCallingIdentity(l);
    }
  }
  
  FileOutputStreamWithPath openIconFileForWrite(int paramInt, ShortcutInfo paramShortcutInfo)
    throws IOException
  {
    File localFile = new File(getUserBitmapFilePath(paramInt), paramShortcutInfo.getPackage());
    if (!localFile.isDirectory())
    {
      localFile.mkdirs();
      if (!localFile.isDirectory()) {
        throw new IOException("Unable to create directory " + localFile);
      }
      SELinux.restorecon(localFile);
    }
    String str = String.valueOf(injectCurrentTimeMillis());
    paramInt = 0;
    for (;;)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramInt == 0) {}
      for (paramShortcutInfo = str;; paramShortcutInfo = str + "_" + paramInt)
      {
        paramShortcutInfo = new File(localFile, paramShortcutInfo + ".png");
        if (paramShortcutInfo.exists()) {
          break;
        }
        if (DEBUG) {
          Slog.d("ShortcutService", "Saving icon to " + paramShortcutInfo.getAbsolutePath());
        }
        return new FileOutputStreamWithPath(paramShortcutInfo);
      }
      paramInt += 1;
    }
  }
  
  void packageShortcutsChanged(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("Shortcut changes: package=%s, user=%d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    notifyListeners(paramString, paramInt);
    scheduleSaveUser(paramInt);
  }
  
  List<ResolveInfo> queryActivities(Intent paramIntent, String paramString, ComponentName paramComponentName, int paramInt)
  {
    paramIntent.setPackage((String)Preconditions.checkNotNull(paramString));
    if (paramComponentName != null) {
      paramIntent.setComponent(paramComponentName);
    }
    paramIntent = this.mContext.getPackageManager().queryIntentActivitiesAsUser(paramIntent, 794624, paramInt);
    if ((paramIntent == null) || (paramIntent.size() == 0)) {
      return EMPTY_RESOLVE_INFO;
    }
    if (!isInstalled(((ResolveInfo)paramIntent.get(0)).activityInfo)) {
      return EMPTY_RESOLVE_INFO;
    }
    paramIntent.removeIf(ACTIVITY_NOT_EXPORTED);
    return paramIntent;
  }
  
  public void removeAllDynamicShortcuts(String paramString, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      getPackageShortcutsForPublisherLocked(paramString, paramInt).deleteAllDynamicShortcuts();
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return;
    }
  }
  
  public void removeDynamicShortcuts(String paramString, List paramList, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    Preconditions.checkNotNull(paramList, "shortcutIds must be provided");
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      ShortcutPackage localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt);
      localShortcutPackage.ensureImmutableShortcutsNotIncludedWithIds(paramList);
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        localShortcutPackage.deleteDynamicWithId((String)Preconditions.checkStringNotEmpty((String)paramList.get(i)));
        i -= 1;
      }
      localShortcutPackage.adjustRanks();
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return;
    }
  }
  
  void removeIcon(int paramInt, ShortcutInfo paramShortcutInfo)
  {
    paramShortcutInfo.setIconResourceId(0);
    paramShortcutInfo.setIconResName(null);
    paramShortcutInfo.clearFlags(12);
  }
  
  public void reportShortcutUsed(String paramString1, String paramString2, int paramInt)
  {
    verifyCaller(paramString1, paramInt);
    Preconditions.checkNotNull(paramString2);
    if (DEBUG) {
      Slog.d("ShortcutService", String.format("reportShortcutUsed: Shortcut %s package %s used on user %d", new Object[] { paramString2, paramString1, Integer.valueOf(paramInt) }));
    }
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      if (getPackageShortcutsForPublisherLocked(paramString1, paramInt).findShortcutById(paramString2) == null)
      {
        Log.w("ShortcutService", String.format("reportShortcutUsed: package %s doesn't have shortcut %s", new Object[] { paramString1, paramString2 }));
        return;
      }
      l = injectClearCallingIdentity();
    }
  }
  
  void resetAllThrottlingInner()
  {
    synchronized (this.mLock)
    {
      this.mRawLastResetTime = injectCurrentTimeMillis();
      scheduleSaveBaseState();
      Slog.i("ShortcutService", "ShortcutManager: throttling counter reset for all users");
      return;
    }
  }
  
  public void resetThrottling()
  {
    enforceSystemOrShell();
    resetThrottlingInner(getCallingUserId());
  }
  
  void resetThrottlingInner(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (!isUserUnlockedL(paramInt))
      {
        Log.w("ShortcutService", "User " + paramInt + " is locked or not running");
        return;
      }
      getUserShortcutsLocked(paramInt).resetThrottling();
      scheduleSaveUser(paramInt);
      Slog.i("ShortcutService", "ShortcutManager: throttling counter reset for user " + paramInt);
      return;
    }
  }
  
  void saveBaseStateLocked()
  {
    AtomicFile localAtomicFile = getBaseStateFile();
    if (DEBUG) {
      Slog.d("ShortcutService", "Saving to " + localAtomicFile.getBaseFile());
    }
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      localObject = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "root");
      localObject = localFileOutputStream;
      writeTagValue(localFastXmlSerializer, "last_reset_time", this.mRawLastResetTime);
      localObject = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "root");
      localObject = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject = localFileOutputStream;
      localAtomicFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      Slog.e("ShortcutService", "Failed to write to file " + localAtomicFile.getBaseFile(), localIOException);
      localAtomicFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  void saveDirtyInfo()
  {
    if (DEBUG) {
      Slog.d("ShortcutService", "saveDirtyInfo");
    }
    for (;;)
    {
      int i;
      try
      {
        synchronized (this.mLock)
        {
          i = this.mDirtyUserIds.size() - 1;
          if (i >= 0)
          {
            int j = ((Integer)this.mDirtyUserIds.get(i)).intValue();
            if (j == 55536) {
              saveBaseStateLocked();
            } else {
              saveUserLocked(j);
            }
          }
        }
        this.mDirtyUserIds.clear();
      }
      catch (Exception localException)
      {
        wtf("Exception in saveDirtyInfo", localException);
        return;
      }
      return;
      i -= 1;
    }
  }
  
  /* Error */
  void saveIconAndFixUpShortcut(int paramInt, ShortcutInfo paramShortcutInfo)
  {
    // Byte code:
    //   0: aload_2
    //   1: invokevirtual 2341	android/content/pm/ShortcutInfo:hasIconFile	()Z
    //   4: ifne +10 -> 14
    //   7: aload_2
    //   8: invokevirtual 2344	android/content/pm/ShortcutInfo:hasIconResource	()Z
    //   11: ifeq +4 -> 15
    //   14: return
    //   15: aload_0
    //   16: invokevirtual 1850	com/android/server/pm/ShortcutService:injectClearCallingIdentity	()J
    //   19: lstore_3
    //   20: aload_0
    //   21: iload_1
    //   22: aload_2
    //   23: invokevirtual 2346	com/android/server/pm/ShortcutService:removeIcon	(ILandroid/content/pm/ShortcutInfo;)V
    //   26: aload_2
    //   27: invokevirtual 1010	android/content/pm/ShortcutInfo:getIcon	()Landroid/graphics/drawable/Icon;
    //   30: astore 5
    //   32: aload 5
    //   34: ifnonnull +9 -> 43
    //   37: aload_0
    //   38: lload_3
    //   39: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   42: return
    //   43: aload 5
    //   45: invokevirtual 2349	android/graphics/drawable/Icon:getType	()I
    //   48: tableswitch	default:+294->342, 1:+76->124, 2:+45->93
    //   72: invokestatic 2353	android/content/pm/ShortcutInfo:getInvalidIconException	()Ljava/lang/IllegalArgumentException;
    //   75: athrow
    //   76: astore 5
    //   78: aload_2
    //   79: invokevirtual 2356	android/content/pm/ShortcutInfo:clearIcon	()V
    //   82: aload 5
    //   84: athrow
    //   85: astore_2
    //   86: aload_0
    //   87: lload_3
    //   88: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   91: aload_2
    //   92: athrow
    //   93: aload_0
    //   94: aload_2
    //   95: aload 5
    //   97: invokevirtual 2358	com/android/server/pm/ShortcutService:injectValidateIconResPackage	(Landroid/content/pm/ShortcutInfo;Landroid/graphics/drawable/Icon;)V
    //   100: aload_2
    //   101: aload 5
    //   103: invokevirtual 2361	android/graphics/drawable/Icon:getResId	()I
    //   106: invokevirtual 2285	android/content/pm/ShortcutInfo:setIconResourceId	(I)V
    //   109: aload_2
    //   110: iconst_4
    //   111: invokevirtual 2364	android/content/pm/ShortcutInfo:addFlags	(I)V
    //   114: aload_2
    //   115: invokevirtual 2356	android/content/pm/ShortcutInfo:clearIcon	()V
    //   118: aload_0
    //   119: lload_3
    //   120: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   123: return
    //   124: aload 5
    //   126: invokevirtual 2368	android/graphics/drawable/Icon:getBitmap	()Landroid/graphics/Bitmap;
    //   129: astore 7
    //   131: aload 7
    //   133: ifnonnull +22 -> 155
    //   136: ldc -83
    //   138: ldc_w 2370
    //   141: invokestatic 1228	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: aload_2
    //   146: invokevirtual 2356	android/content/pm/ShortcutInfo:clearIcon	()V
    //   149: aload_0
    //   150: lload_3
    //   151: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   154: return
    //   155: aconst_null
    //   156: astore 6
    //   158: aconst_null
    //   159: astore 5
    //   161: aload_0
    //   162: iload_1
    //   163: aload_2
    //   164: invokevirtual 2372	com/android/server/pm/ShortcutService:openIconFileForWrite	(ILandroid/content/pm/ShortcutInfo;)Lcom/android/server/pm/ShortcutService$FileOutputStreamWithPath;
    //   167: astore 8
    //   169: aload 8
    //   171: invokevirtual 2375	com/android/server/pm/ShortcutService$FileOutputStreamWithPath:getFile	()Ljava/io/File;
    //   174: astore 6
    //   176: aload 6
    //   178: astore 5
    //   180: aload 7
    //   182: aload_0
    //   183: getfield 748	com/android/server/pm/ShortcutService:mMaxIconDimension	I
    //   186: invokestatic 2377	com/android/server/pm/ShortcutService:shrinkBitmap	(Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
    //   189: astore 9
    //   191: aload 9
    //   193: aload_0
    //   194: getfield 808	com/android/server/pm/ShortcutService:mIconPersistFormat	Landroid/graphics/Bitmap$CompressFormat;
    //   197: aload_0
    //   198: getfield 815	com/android/server/pm/ShortcutService:mIconPersistQuality	I
    //   201: aload 8
    //   203: invokevirtual 2381	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   206: pop
    //   207: aload 7
    //   209: aload 9
    //   211: if_acmpeq +12 -> 223
    //   214: aload 6
    //   216: astore 5
    //   218: aload 9
    //   220: invokevirtual 2384	android/graphics/Bitmap:recycle	()V
    //   223: aload 6
    //   225: astore 5
    //   227: aload_2
    //   228: aload 8
    //   230: invokevirtual 2375	com/android/server/pm/ShortcutService$FileOutputStreamWithPath:getFile	()Ljava/io/File;
    //   233: invokevirtual 713	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   236: invokevirtual 2387	android/content/pm/ShortcutInfo:setBitmapPath	(Ljava/lang/String;)V
    //   239: aload 6
    //   241: astore 5
    //   243: aload_2
    //   244: bipush 8
    //   246: invokevirtual 2364	android/content/pm/ShortcutInfo:addFlags	(I)V
    //   249: aload 8
    //   251: invokestatic 1283	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   254: aload_2
    //   255: invokevirtual 2356	android/content/pm/ShortcutInfo:clearIcon	()V
    //   258: aload_0
    //   259: lload_3
    //   260: invokevirtual 1860	com/android/server/pm/ShortcutService:injectRestoreCallingIdentity	(J)V
    //   263: return
    //   264: astore 10
    //   266: aload 7
    //   268: aload 9
    //   270: if_acmpeq +12 -> 282
    //   273: aload 6
    //   275: astore 5
    //   277: aload 9
    //   279: invokevirtual 2384	android/graphics/Bitmap:recycle	()V
    //   282: aload 6
    //   284: astore 5
    //   286: aload 10
    //   288: athrow
    //   289: astore 7
    //   291: aload 5
    //   293: astore 6
    //   295: aload 8
    //   297: invokestatic 1283	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   300: aload 5
    //   302: astore 6
    //   304: aload 7
    //   306: athrow
    //   307: astore 5
    //   309: ldc -83
    //   311: ldc_w 2389
    //   314: aload 5
    //   316: invokestatic 1864	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   319: pop
    //   320: aload 6
    //   322: ifnull -68 -> 254
    //   325: aload 6
    //   327: invokevirtual 2253	java/io/File:exists	()Z
    //   330: ifeq -76 -> 254
    //   333: aload 6
    //   335: invokevirtual 716	java/io/File:delete	()Z
    //   338: pop
    //   339: goto -85 -> 254
    //   342: goto -270 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	345	0	this	ShortcutService
    //   0	345	1	paramInt	int
    //   0	345	2	paramShortcutInfo	ShortcutInfo
    //   19	241	3	l	long
    //   30	14	5	localIcon1	Icon
    //   76	49	5	localIcon2	Icon
    //   159	142	5	localObject1	Object
    //   307	8	5	localIOException	IOException
    //   156	178	6	localObject2	Object
    //   129	138	7	localBitmap1	Bitmap
    //   289	16	7	localObject3	Object
    //   167	129	8	localFileOutputStreamWithPath	FileOutputStreamWithPath
    //   189	89	9	localBitmap2	Bitmap
    //   264	23	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   43	72	76	finally
    //   72	76	76	finally
    //   93	114	76	finally
    //   124	131	76	finally
    //   136	145	76	finally
    //   161	169	76	finally
    //   249	254	76	finally
    //   295	300	76	finally
    //   304	307	76	finally
    //   309	320	76	finally
    //   325	339	76	finally
    //   20	32	85	finally
    //   78	85	85	finally
    //   114	118	85	finally
    //   145	149	85	finally
    //   254	258	85	finally
    //   191	207	264	finally
    //   169	176	289	finally
    //   180	191	289	finally
    //   218	223	289	finally
    //   227	239	289	finally
    //   243	249	289	finally
    //   277	282	289	finally
    //   286	289	289	finally
    //   161	169	307	java/io/IOException
    //   161	169	307	java/lang/RuntimeException
    //   249	254	307	java/io/IOException
    //   249	254	307	java/lang/RuntimeException
    //   295	300	307	java/io/IOException
    //   295	300	307	java/lang/RuntimeException
    //   304	307	307	java/io/IOException
    //   304	307	307	java/lang/RuntimeException
  }
  
  void scheduleSaveUser(int paramInt)
  {
    scheduleSaveInner(paramInt);
  }
  
  public boolean setDynamicShortcuts(String paramString, ParceledListSlice arg2, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    List localList = ???.getList();
    int j = localList.size();
    synchronized (this.mLock)
    {
      throwIfUserLockedL(paramInt);
      ShortcutPackage localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt);
      localShortcutPackage.ensureImmutableShortcutsNotIncluded(localList);
      fillInDefaultActivity(localList);
      localShortcutPackage.enforceShortcutCountsBeforeOperation(localList, 0);
      boolean bool = localShortcutPackage.tryApiCall();
      if (!bool) {
        return false;
      }
      localShortcutPackage.clearAllImplicitRanks();
      assignImplicitRanks(localList);
      int i = 0;
      while (i < j)
      {
        fixUpIncomingShortcutInfo((ShortcutInfo)localList.get(i), false);
        i += 1;
      }
      localShortcutPackage.deleteAllDynamicShortcuts();
      i = 0;
      while (i < j)
      {
        localShortcutPackage.addOrUpdateDynamicShortcut((ShortcutInfo)localList.get(i));
        i += 1;
      }
      localShortcutPackage.adjustRanks();
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return true;
    }
  }
  
  boolean shouldBackupApp(PackageInfo paramPackageInfo)
  {
    boolean bool = false;
    if ((paramPackageInfo.applicationInfo.flags & 0x8000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean shouldBackupApp(String paramString, int paramInt)
  {
    return isApplicationFlagSet(paramString, paramInt, 32768);
  }
  
  void throwIfUserLockedL(int paramInt)
  {
    if (!isUserUnlockedL(paramInt)) {
      throw new IllegalStateException("User " + paramInt + " is locked or not running");
    }
  }
  
  boolean updateConfigurationLocked(String paramString)
  {
    boolean bool = true;
    KeyValueListParser localKeyValueListParser = new KeyValueListParser(',');
    try
    {
      localKeyValueListParser.setString(paramString);
      this.mSaveDelayMillis = Math.max(0, (int)localKeyValueListParser.getLong("save_delay_ms", 3000L));
      this.mResetInterval = Math.max(1L, localKeyValueListParser.getLong("reset_interval_sec", 86400L) * 1000L);
      this.mMaxUpdatesPerInterval = Math.max(0, (int)localKeyValueListParser.getLong("max_updates_per_interval", 10L));
      this.mMaxShortcuts = Math.max(0, (int)localKeyValueListParser.getLong("max_shortcuts", 5L));
      if (injectIsLowRamDevice())
      {
        i = (int)localKeyValueListParser.getLong("max_icon_dimension_dp_lowram", 48L);
        this.mMaxIconDimension = injectDipToPixel(Math.max(1, i));
        this.mIconPersistFormat = Bitmap.CompressFormat.valueOf(localKeyValueListParser.getString("icon_format", DEFAULT_ICON_PERSIST_FORMAT));
        this.mIconPersistQuality = ((int)localKeyValueListParser.getLong("icon_quality", 100L));
        return bool;
      }
    }
    catch (IllegalArgumentException paramString)
    {
      for (;;)
      {
        Slog.e("ShortcutService", "Bad shortcut manager settings", paramString);
        bool = false;
        continue;
        int i = (int)localKeyValueListParser.getLong("max_icon_dimension_dp", 96L);
      }
    }
  }
  
  public boolean updateShortcuts(String paramString, ParceledListSlice arg2, int paramInt)
  {
    verifyCaller(paramString, paramInt);
    List localList = ???.getList();
    int k = localList.size();
    for (;;)
    {
      ShortcutPackage localShortcutPackage;
      int i;
      synchronized (this.mLock)
      {
        throwIfUserLockedL(paramInt);
        localShortcutPackage = getPackageShortcutsForPublisherLocked(paramString, paramInt);
        localShortcutPackage.ensureImmutableShortcutsNotIncluded(localList);
        localShortcutPackage.enforceShortcutCountsBeforeOperation(localList, 2);
        boolean bool = localShortcutPackage.tryApiCall();
        if (!bool) {
          return false;
        }
        localShortcutPackage.clearAllImplicitRanks();
        assignImplicitRanks(localList);
        i = 0;
        if (i >= k) {
          break label263;
        }
        ShortcutInfo localShortcutInfo1 = (ShortcutInfo)localList.get(i);
        fixUpIncomingShortcutInfo(localShortcutInfo1, true);
        ShortcutInfo localShortcutInfo2 = localShortcutPackage.findShortcutById(localShortcutInfo1.getId());
        if (localShortcutInfo2 == null) {
          break label282;
        }
        if (localShortcutInfo2.isEnabled() != localShortcutInfo1.isEnabled()) {
          Slog.w("ShortcutService", "ShortcutInfo.enabled cannot be changed with updateShortcuts()");
        }
        if (localShortcutInfo1.hasRank())
        {
          localShortcutInfo2.setRankChanged();
          localShortcutInfo2.setImplicitRank(localShortcutInfo1.getImplicitRank());
        }
        if (localShortcutInfo1.getIcon() != null)
        {
          j = 1;
          if (j != 0) {
            removeIcon(paramInt, localShortcutInfo2);
          }
          localShortcutInfo2.copyNonNullFieldsFrom(localShortcutInfo1);
          localShortcutInfo2.setTimestamp(injectCurrentTimeMillis());
          if (j != 0) {
            saveIconAndFixUpShortcut(paramInt, localShortcutInfo2);
          }
          if ((j == 0) && (!localShortcutInfo1.hasStringResources())) {
            break label282;
          }
          fixUpShortcutResourceNamesAndValues(localShortcutInfo2);
        }
      }
      int j = 0;
      continue;
      label263:
      localShortcutPackage.adjustRanks();
      packageShortcutsChanged(paramString, paramInt);
      verifyStates();
      return true;
      label282:
      i += 1;
    }
  }
  
  final void verifyStates()
  {
    if (injectShouldPerformVerification()) {
      verifyStatesInner();
    }
  }
  
  final void wtf(String paramString)
  {
    wtf(paramString, null);
  }
  
  void wtf(String paramString, Throwable arg2)
  {
    Object localObject = ???;
    if (??? == null) {
      localObject = new RuntimeException("Stacktrace");
    }
    synchronized (this.mLock)
    {
      this.mWtfCount += 1;
      this.mLastWtfStacktrace = new Exception("Last failure was logged here:");
      Slog.wtf("ShortcutService", paramString, (Throwable)localObject);
      return;
    }
  }
  
  static class CommandException
    extends Exception
  {
    public CommandException(String paramString)
    {
      super();
    }
  }
  
  static abstract interface ConfigConstants
  {
    public static final String KEY_ICON_FORMAT = "icon_format";
    public static final String KEY_ICON_QUALITY = "icon_quality";
    public static final String KEY_MAX_ICON_DIMENSION_DP = "max_icon_dimension_dp";
    public static final String KEY_MAX_ICON_DIMENSION_DP_LOWRAM = "max_icon_dimension_dp_lowram";
    public static final String KEY_MAX_SHORTCUTS = "max_shortcuts";
    public static final String KEY_MAX_UPDATES_PER_INTERVAL = "max_updates_per_interval";
    public static final String KEY_RESET_INTERVAL_SEC = "reset_interval_sec";
    public static final String KEY_SAVE_DELAY_MILLIS = "save_delay_ms";
  }
  
  static class FileOutputStreamWithPath
    extends FileOutputStream
  {
    private final File mFile;
    
    public FileOutputStreamWithPath(File paramFile)
      throws FileNotFoundException
    {
      super();
      this.mFile = paramFile;
    }
    
    public File getFile()
    {
      return this.mFile;
    }
  }
  
  static class InvalidFileFormatException
    extends Exception
  {
    public InvalidFileFormatException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    final ShortcutService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
      this.mService = new ShortcutService(paramContext);
    }
    
    public void onBootPhase(int paramInt)
    {
      this.mService.onBootPhase(paramInt);
    }
    
    public void onCleanupUser(int paramInt)
    {
      this.mService.handleCleanupUser(paramInt);
    }
    
    public void onStart()
    {
      publishBinderService("shortcut", this.mService);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mService.handleUnlockUser(paramInt);
    }
  }
  
  private class LocalService
    extends ShortcutServiceInternal
  {
    private LocalService() {}
    
    private ShortcutInfo getShortcutInfoLocked(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
    {
      Preconditions.checkStringNotEmpty(paramString2, "packageName");
      Preconditions.checkStringNotEmpty(paramString3, "shortcutId");
      ShortcutService.this.throwIfUserLockedL(paramInt2);
      ShortcutService.this.throwIfUserLockedL(paramInt1);
      paramString2 = ShortcutService.this.getUserShortcutsLocked(paramInt2).getPackageShortcutsIfExists(paramString2);
      if (paramString2 == null) {
        return null;
      }
      ArrayList localArrayList = new ArrayList(1);
      paramString2.findAll(localArrayList, new -android_content_pm_ShortcutInfo_getShortcutInfoLocked_int_launcherUserId_java_lang_String_callingPackage_java_lang_String_packageName_java_lang_String_shortcutId_int_userId_LambdaImpl0(paramString3), 0, paramString1, paramInt1);
      if (localArrayList.size() == 0) {
        return null;
      }
      return (ShortcutInfo)localArrayList.get(0);
    }
    
    private void getShortcutsInnerLocked(int paramInt1, String paramString1, String paramString2, List<String> paramList, long paramLong, ComponentName paramComponentName, int paramInt2, int paramInt3, ArrayList<ShortcutInfo> paramArrayList, int paramInt4)
    {
      if (paramList == null) {}
      for (paramList = null;; paramList = new ArraySet(paramList))
      {
        paramString2 = ShortcutService.this.getUserShortcutsLocked(paramInt3).getPackageShortcutsIfExists(paramString2);
        if (paramString2 != null) {
          break;
        }
        return;
      }
      paramString2.findAll(paramArrayList, new -void_getShortcutsInnerLocked_int_launcherUserId_java_lang_String_callingPackage_java_lang_String_packageName_java_util_List_shortcutIds_long_changedSince_android_content_ComponentName_componentName_int_queryFlags_int_userId_java_util_ArrayList_ret_int_cloneFlag_LambdaImpl0(paramLong, paramList, paramComponentName, paramInt2), paramInt4, paramString1, paramInt1);
    }
    
    public void addListener(ShortcutServiceInternal.ShortcutChangeListener paramShortcutChangeListener)
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.-get1(ShortcutService.this).add((ShortcutServiceInternal.ShortcutChangeListener)Preconditions.checkNotNull(paramShortcutChangeListener));
        return;
      }
    }
    
    public Intent[] createShortcutIntents(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
    {
      Preconditions.checkStringNotEmpty(paramString2, "packageName can't be empty");
      Preconditions.checkStringNotEmpty(paramString3, "shortcutId can't be empty");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.throwIfUserLockedL(paramInt2);
        ShortcutService.this.throwIfUserLockedL(paramInt1);
        ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt2, paramInt1).attemptToRestoreIfNeededAndSave();
        paramString1 = getShortcutInfoLocked(paramInt1, paramString1, paramString2, paramString3, paramInt2);
        if ((paramString1 != null) && (paramString1.isEnabled()) && (paramString1.isAlive()))
        {
          paramString1 = paramString1.getIntents();
          return paramString1;
        }
        Log.e("ShortcutService", "Shortcut " + paramString3 + " does not exist or disabled");
        return null;
      }
    }
    
    public ParcelFileDescriptor getShortcutIconFd(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
    {
      Preconditions.checkNotNull(paramString1, "callingPackage");
      Preconditions.checkNotNull(paramString2, "packageName");
      Preconditions.checkNotNull(paramString3, "shortcutId");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.throwIfUserLockedL(paramInt2);
        ShortcutService.this.throwIfUserLockedL(paramInt1);
        ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt2, paramInt1).attemptToRestoreIfNeededAndSave();
        paramString1 = ShortcutService.this.getUserShortcutsLocked(paramInt2).getPackageShortcutsIfExists(paramString2);
        if (paramString1 == null) {
          return null;
        }
        paramString1 = paramString1.findShortcutById(paramString3);
        if (paramString1 != null)
        {
          boolean bool = paramString1.hasIconFile();
          if (!bool) {}
        }
        try
        {
          if (paramString1.getBitmapPath() == null)
          {
            Slog.w("ShortcutService", "null bitmap detected in getShortcutIconFd()");
            return null;
            return null;
          }
          paramString2 = ParcelFileDescriptor.open(new File(paramString1.getBitmapPath()), 268435456);
          return paramString2;
        }
        catch (FileNotFoundException paramString2)
        {
          Slog.e("ShortcutService", "Icon file not found: " + paramString1.getBitmapPath());
          return null;
        }
      }
    }
    
    public int getShortcutIconResId(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
    {
      int i = 0;
      Preconditions.checkNotNull(paramString1, "callingPackage");
      Preconditions.checkNotNull(paramString2, "packageName");
      Preconditions.checkNotNull(paramString3, "shortcutId");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.throwIfUserLockedL(paramInt2);
        ShortcutService.this.throwIfUserLockedL(paramInt1);
        ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt2, paramInt1).attemptToRestoreIfNeededAndSave();
        paramString1 = ShortcutService.this.getUserShortcutsLocked(paramInt2).getPackageShortcutsIfExists(paramString2);
        if (paramString1 == null) {
          return 0;
        }
        paramString1 = paramString1.findShortcutById(paramString3);
        paramInt1 = i;
        if (paramString1 != null)
        {
          paramInt1 = i;
          if (paramString1.hasIconResource()) {
            paramInt1 = paramString1.getIconResourceId();
          }
        }
        return paramInt1;
      }
    }
    
    public List<ShortcutInfo> getShortcuts(int paramInt1, String paramString1, long paramLong, String paramString2, List<String> paramList, ComponentName paramComponentName, int paramInt2, int paramInt3)
    {
      ArrayList localArrayList = new ArrayList();
      int i;
      if ((paramInt2 & 0x4) != 0) {
        i = 1;
      }
      for (;;)
      {
        if (i != 0)
        {
          i = 4;
          label27:
          if (paramString2 == null) {
            paramList = null;
          }
        }
        synchronized (ShortcutService.-get2(ShortcutService.this))
        {
          ShortcutService.this.throwIfUserLockedL(paramInt3);
          ShortcutService.this.throwIfUserLockedL(paramInt1);
          ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt3, paramInt1).attemptToRestoreIfNeededAndSave();
          if (paramString2 != null)
          {
            getShortcutsInnerLocked(paramInt1, paramString1, paramString2, paramList, paramLong, paramComponentName, paramInt2, paramInt3, localArrayList, i);
            return localArrayList;
            i = 0;
            continue;
            i = 11;
            break label27;
          }
          ShortcutService.this.getUserShortcutsLocked(paramInt3).forAllPackages(new -java_util_List_getShortcuts_int_launcherUserId_java_lang_String_callingPackage_long_changedSince_java_lang_String_packageName_java_util_List_shortcutIds_android_content_ComponentName_componentName_int_queryFlags_int_userId_LambdaImpl0(paramInt1, paramString1, paramList, paramLong, paramComponentName, paramInt2, paramInt3, localArrayList, i));
        }
      }
    }
    
    public boolean hasShortcutHostPermission(int paramInt, String paramString)
    {
      return ShortcutService.this.hasShortcutHostPermission(paramString, paramInt);
    }
    
    public boolean isPinnedByCaller(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
    {
      Preconditions.checkStringNotEmpty(paramString2, "packageName");
      Preconditions.checkStringNotEmpty(paramString3, "shortcutId");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.throwIfUserLockedL(paramInt2);
        ShortcutService.this.throwIfUserLockedL(paramInt1);
        ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt2, paramInt1).attemptToRestoreIfNeededAndSave();
        paramString1 = getShortcutInfoLocked(paramInt1, paramString1, paramString2, paramString3, paramInt2);
        if (paramString1 != null)
        {
          bool = paramString1.isPinned();
          return bool;
        }
        boolean bool = false;
      }
    }
    
    public void pinShortcuts(int paramInt1, String paramString1, String paramString2, List<String> paramList, int paramInt2)
    {
      Preconditions.checkStringNotEmpty(paramString2, "packageName");
      Preconditions.checkNotNull(paramList, "shortcutIds");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.throwIfUserLockedL(paramInt2);
        ShortcutService.this.throwIfUserLockedL(paramInt1);
        paramString1 = ShortcutService.this.getLauncherShortcutsLocked(paramString1, paramInt2, paramInt1);
        paramString1.attemptToRestoreIfNeededAndSave();
        paramString1.pinShortcuts(paramInt2, paramString2, paramList);
        ShortcutService.this.packageShortcutsChanged(paramString2, paramInt2);
        ShortcutService.this.verifyStates();
        return;
      }
    }
  }
  
  private class MyShellCommand
    extends ShellCommand
  {
    private int mUserId = 0;
    
    private MyShellCommand() {}
    
    private void clearLauncher()
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.getUserShortcutsLocked(this.mUserId).forceClearLauncher();
        return;
      }
    }
    
    private void handleClearDefaultLauncher()
      throws ShortcutService.CommandException
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        parseOptionsLocked(true);
        clearLauncher();
        return;
      }
    }
    
    private void handleClearShortcuts()
      throws ShortcutService.CommandException
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        parseOptionsLocked(true);
        String str = getNextArgRequired();
        Slog.i("ShortcutService", "cmd: handleClearShortcuts: user" + this.mUserId + ", " + str);
        ShortcutService.-wrap0(ShortcutService.this, str, this.mUserId, true);
        return;
      }
    }
    
    private void handleGetDefaultLauncher()
      throws ShortcutService.CommandException
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        parseOptionsLocked(true);
        clearLauncher();
        showLauncher();
        return;
      }
    }
    
    private void handleOverrideConfig()
      throws ShortcutService.CommandException
    {
      String str = getNextArgRequired();
      Slog.i("ShortcutService", "cmd: handleOverrideConfig: " + str);
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        if (!ShortcutService.this.updateConfigurationLocked(str)) {
          throw new ShortcutService.CommandException("override-config failed.  See logcat for details.");
        }
      }
    }
    
    private void handleResetAllThrottling()
    {
      Slog.i("ShortcutService", "cmd: handleResetAllThrottling");
      ShortcutService.this.resetAllThrottlingInner();
    }
    
    private void handleResetConfig()
    {
      Slog.i("ShortcutService", "cmd: handleResetConfig");
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.-wrap6(ShortcutService.this);
        return;
      }
    }
    
    private void handleResetThrottling()
      throws ShortcutService.CommandException
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        parseOptionsLocked(true);
        Slog.i("ShortcutService", "cmd: handleResetThrottling: user=" + this.mUserId);
        ShortcutService.this.resetThrottlingInner(this.mUserId);
        return;
      }
    }
    
    private void handleUnloadUser()
      throws ShortcutService.CommandException
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        parseOptionsLocked(true);
        Slog.i("ShortcutService", "cmd: handleUnloadUser: user=" + this.mUserId);
        ShortcutService.this.handleCleanupUser(this.mUserId);
        return;
      }
    }
    
    private void handleVerifyStates()
      throws ShortcutService.CommandException
    {
      try
      {
        ShortcutService.-wrap7(ShortcutService.this);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw new ShortcutService.CommandException(localThrowable.getMessage() + "\n" + Log.getStackTraceString(localThrowable));
      }
    }
    
    private void parseOptionsLocked(boolean paramBoolean)
      throws ShortcutService.CommandException
    {
      String str;
      do
      {
        str = getNextOption();
        if (str == null) {
          return;
        }
        if ((!str.equals("--user")) || (!paramBoolean)) {
          break;
        }
        this.mUserId = UserHandle.parseUserArg(getNextArgRequired());
      } while (ShortcutService.this.isUserUnlockedL(this.mUserId));
      throw new ShortcutService.CommandException("User " + this.mUserId + " is not running or locked");
      throw new ShortcutService.CommandException("Unknown option: " + str);
    }
    
    private void showLauncher()
    {
      synchronized (ShortcutService.-get2(ShortcutService.this))
      {
        ShortcutService.this.hasShortcutHostPermissionInner("-", this.mUserId);
        getOutPrintWriter().println("Launcher: " + ShortcutService.this.getUserShortcutsLocked(this.mUserId).getLastKnownLauncher());
        return;
      }
    }
    
    public int onCommand(String paramString)
    {
      if (paramString == null) {
        return handleDefaultCommands(paramString);
      }
      PrintWriter localPrintWriter = getOutPrintWriter();
      for (;;)
      {
        try
        {
          if (paramString.equals("reset-throttling"))
          {
            handleResetThrottling();
            localPrintWriter.println("Success");
            return 0;
          }
          if (paramString.equals("reset-all-throttling"))
          {
            handleResetAllThrottling();
            continue;
          }
          if (!paramString.equals("override-config")) {
            break label97;
          }
        }
        catch (ShortcutService.CommandException paramString)
        {
          localPrintWriter.println("Error: " + paramString.getMessage());
          return 1;
        }
        handleOverrideConfig();
        continue;
        label97:
        if (paramString.equals("reset-config"))
        {
          handleResetConfig();
        }
        else if (paramString.equals("clear-default-launcher"))
        {
          handleClearDefaultLauncher();
        }
        else if (paramString.equals("get-default-launcher"))
        {
          handleGetDefaultLauncher();
        }
        else if (paramString.equals("unload-user"))
        {
          handleUnloadUser();
        }
        else if (paramString.equals("clear-shortcuts"))
        {
          handleClearShortcuts();
        }
        else
        {
          if (!paramString.equals("verify-states")) {
            break;
          }
          handleVerifyStates();
        }
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    
    public void onHelp()
    {
      PrintWriter localPrintWriter = getOutPrintWriter();
      localPrintWriter.println("Usage: cmd shortcut COMMAND [options ...]");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut reset-throttling [--user USER_ID]");
      localPrintWriter.println("    Reset throttling for all packages and users");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut reset-all-throttling");
      localPrintWriter.println("    Reset the throttling state for all users");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut override-config CONFIG");
      localPrintWriter.println("    Override the configuration for testing (will last until reboot)");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut reset-config");
      localPrintWriter.println("    Reset the configuration set with \"update-config\"");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut clear-default-launcher [--user USER_ID]");
      localPrintWriter.println("    Clear the cached default launcher");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut get-default-launcher [--user USER_ID]");
      localPrintWriter.println("    Show the default launcher");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut unload-user [--user USER_ID]");
      localPrintWriter.println("    Unload a user from the memory");
      localPrintWriter.println("    (This should not affect any observable behavior)");
      localPrintWriter.println();
      localPrintWriter.println("cmd shortcut clear-shortcuts [--user USER_ID] PACKAGE");
      localPrintWriter.println("    Remove all shortcuts from a package, including pinned shortcuts");
      localPrintWriter.println();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  static @interface ShortcutOperation {}
  
  static abstract interface Stats
  {
    public static final int ASYNC_PRELOAD_USER_DELAY = 15;
    public static final int CHECK_LAUNCHER_ACTIVITY = 12;
    public static final int CHECK_PACKAGE_CHANGES = 8;
    public static final int CLEANUP_DANGLING_BITMAPS = 5;
    public static final int COUNT = 16;
    public static final int GET_ACTIVITY_WITH_METADATA = 6;
    public static final int GET_APPLICATION_INFO = 3;
    public static final int GET_APPLICATION_RESOURCES = 9;
    public static final int GET_DEFAULT_HOME = 0;
    public static final int GET_INSTALLED_PACKAGES = 7;
    public static final int GET_LAUNCHER_ACTIVITY = 11;
    public static final int GET_PACKAGE_INFO = 1;
    public static final int GET_PACKAGE_INFO_WITH_SIG = 2;
    public static final int IS_ACTIVITY_ENABLED = 13;
    public static final int LAUNCHER_PERMISSION_CHECK = 4;
    public static final int PACKAGE_UPDATE_CHECK = 14;
    public static final int RESOURCE_NAME_LOOKUP = 10;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */