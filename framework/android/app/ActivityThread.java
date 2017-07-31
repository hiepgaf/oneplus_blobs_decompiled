package android.app;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.app.backup.BackupAgent;
import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDebug;
import android.database.sqlite.SQLiteDebug.DbStats;
import android.database.sqlite.SQLiteDebug.PagerStats;
import android.ddm.DdmHandleAppName;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.hardware.display.DisplayManagerGlobal;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.os.Messenger;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.TransactionTooLargeException;
import android.os.TransactionTracker;
import android.os.UserHandle;
import android.renderscript.RenderScriptCacheDir;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.SuperNotCalledException;
import android.view.ContextThemeWrapper;
import android.view.IWindowSession;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.webkit.WebView;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.SamplingProfilerIntegration;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.FastPrintWriter;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.android.org.conscrypt.TrustedCertificateStore;
import com.google.android.collect.Lists;
import dalvik.system.CloseGuard;
import dalvik.system.VMDebug;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import libcore.io.DropBox;
import libcore.io.DropBox.Reporter;
import libcore.io.EventLogger;
import libcore.io.EventLogger.Reporter;
import libcore.io.IoUtils;
import libcore.net.event.NetworkEventDispatcher;
import net.oneplus.odm.insight.tracker.AppTracker;

public final class ActivityThread
{
  private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 4;
  private static final boolean DEBUG_BACKUP = false;
  public static final boolean DEBUG_BROADCAST = false;
  public static final boolean DEBUG_CONFIGURATION = false;
  private static final boolean DEBUG_MEMORY_TRIM = false;
  static final boolean DEBUG_MESSAGES = false;
  static final boolean DEBUG_ONEPLUS;
  private static final boolean DEBUG_ORDER = false;
  private static final boolean DEBUG_PROVIDER = false;
  private static final boolean DEBUG_RESULTS = false;
  private static final boolean DEBUG_SERVICE = false;
  private static final int DONT_REPORT = 2;
  private static final String HEAP_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s";
  private static final String HEAP_FULL_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s";
  private static final int LOG_AM_ON_PAUSE_CALLED = 30021;
  private static final int LOG_AM_ON_RESUME_CALLED = 30022;
  private static final int LOG_AM_ON_STOP_CALLED = 30049;
  private static final long MIN_TIME_BETWEEN_GCS = 5000L;
  private static final String ONE_COUNT_COLUMN = "%21s %8d";
  private static final String ONE_COUNT_COLUMN_HEADER = "%21s %8s";
  private static final boolean REPORT_TO_ACTIVITY = true;
  public static final int SERVICE_DONE_EXECUTING_ANON = 0;
  public static final int SERVICE_DONE_EXECUTING_START = 1;
  public static final int SERVICE_DONE_EXECUTING_STOP = 2;
  private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
  public static final String TAG = "ActivityThread";
  private static final Bitmap.Config THUMBNAIL_FORMAT = Bitmap.Config.RGB_565;
  private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
  private static final int USER_LEAVING = 1;
  static final boolean localLOGV = false;
  private static volatile ActivityThread sCurrentActivityThread;
  private static final ThreadLocal<Intent> sCurrentBroadcastIntent = new ThreadLocal();
  private static String sEmbryoPackageName;
  static volatile Handler sMainThreadHandler;
  static volatile IPackageManager sPackageManager;
  final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap();
  final ArrayList<Application> mAllApplications = new ArrayList();
  final ApplicationThread mAppThread = new ApplicationThread(null);
  private Bitmap mAvailThumbnailBitmap = null;
  final ArrayMap<String, BackupAgent> mBackupAgents = new ArrayMap();
  AppBindData mBoundApplication;
  Configuration mCompatConfiguration;
  Configuration mConfiguration;
  Bundle mCoreSettings = null;
  int mCurDefaultDisplayDpi;
  boolean mDensityCompatMode;
  private boolean mDisableTrimMemory = true;
  final GcIdler mGcIdler = new GcIdler();
  boolean mGcIdlerScheduled = false;
  final H mH = new H(null);
  Application mInitialApplication;
  Instrumentation mInstrumentation;
  String mInstrumentationAppDir = null;
  String mInstrumentationLibDir = null;
  String mInstrumentationPackageName = null;
  String[] mInstrumentationSplitAppDirs = null;
  String mInstrumentedAppDir = null;
  String mInstrumentedLibDir = null;
  String[] mInstrumentedSplitAppDirs = null;
  boolean mJitEnabled = false;
  ArrayList<WeakReference<AssistStructure>> mLastAssistStructures = new ArrayList();
  private int mLastSessionId;
  @GuardedBy("mResourcesManager")
  int mLifecycleSeq = 0;
  final ArrayMap<IBinder, ProviderClientRecord> mLocalProviders = new ArrayMap();
  final ArrayMap<ComponentName, ProviderClientRecord> mLocalProvidersByName = new ArrayMap();
  final Looper mLooper = Looper.myLooper();
  private Configuration mMainThreadConfig = new Configuration();
  boolean mNeedToShowPermissinRequest = false;
  ActivityClientRecord mNewActivities = null;
  int mNumVisibleActivities = 0;
  final ArrayMap<Activity, ArrayList<OnActivityPausedListener>> mOnPauseListeners = new ArrayMap();
  final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap();
  Configuration mPendingConfiguration = null;
  Messenger mPermissionService = null;
  Profiler mProfiler;
  final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap = new ArrayMap();
  final ArrayMap<IBinder, ProviderRefCount> mProviderRefCountMap = new ArrayMap();
  final ArrayList<ActivityClientRecord> mRelaunchingActivities = new ArrayList();
  final ArrayMap<String, WeakReference<LoadedApk>> mResourcePackages = new ArrayMap();
  private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();
  final ArrayMap<IBinder, Service> mServices = new ArrayMap();
  boolean mSomeActivitiesChanged = false;
  private ContextImpl mSystemContext;
  boolean mSystemThread = false;
  private Canvas mThumbnailCanvas = null;
  private int mThumbnailHeight = -1;
  private int mThumbnailWidth = -1;
  boolean mUpdatingSystemConfig = false;
  
  static
  {
    DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
    sEmbryoPackageName = null;
  }
  
  private void attach(boolean paramBoolean)
  {
    sCurrentActivityThread = this;
    this.mSystemThread = paramBoolean;
    final IActivityManager localIActivityManager;
    if (!paramBoolean)
    {
      ViewRootImpl.addFirstDrawHandler(new Runnable()
      {
        public void run()
        {
          ActivityThread.this.ensureJitEnabled();
        }
      });
      DdmHandleAppName.setAppName("<pre-initialized>", UserHandle.myUserId());
      RuntimeInit.setApplicationObject(this.mAppThread.asBinder());
      localIActivityManager = ActivityManagerNative.getDefault();
    }
    for (;;)
    {
      try
      {
        localIActivityManager.attachApplication(this.mAppThread);
        BinderInternal.addGcWatcher(new Runnable()
        {
          public void run()
          {
            if (!ActivityThread.this.mSomeActivitiesChanged) {
              return;
            }
            Runtime localRuntime = Runtime.getRuntime();
            long l = localRuntime.maxMemory();
            if (localRuntime.totalMemory() - localRuntime.freeMemory() > 3L * l / 4L) {
              ActivityThread.this.mSomeActivitiesChanged = false;
            }
            try
            {
              localIActivityManager.releaseSomeActivities(ActivityThread.this.mAppThread);
              return;
            }
            catch (RemoteException localRemoteException)
            {
              throw localRemoteException.rethrowFromSystemServer();
            }
          }
        });
        DropBox.setReporter(new DropBoxReporter());
        ViewRootImpl.addConfigCallback(new ComponentCallbacks2()
        {
          public void onConfigurationChanged(Configuration paramAnonymousConfiguration)
          {
            synchronized (ActivityThread.-get0(ActivityThread.this))
            {
              if (ActivityThread.-get0(ActivityThread.this).applyConfigurationToResourcesLocked(paramAnonymousConfiguration, null))
              {
                ActivityThread.-wrap36(ActivityThread.this, ActivityThread.this.mInitialApplication.getApplicationContext(), ActivityThread.-get0(ActivityThread.this).getConfiguration().getLocales());
                if ((ActivityThread.this.mPendingConfiguration == null) || (ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(paramAnonymousConfiguration)))
                {
                  ActivityThread.this.mPendingConfiguration = paramAnonymousConfiguration;
                  ActivityThread.-wrap31(ActivityThread.this, 118, paramAnonymousConfiguration);
                }
              }
              return;
            }
          }
          
          public void onLowMemory() {}
          
          public void onTrimMemory(int paramAnonymousInt) {}
        });
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
      DdmHandleAppName.setAppName("system_process", UserHandle.myUserId());
      try
      {
        this.mInstrumentation = new Instrumentation();
        this.mInitialApplication = ContextImpl.createAppContext(this, getSystemContext().mPackageInfo).mPackageInfo.makeApplication(true, null);
        this.mInitialApplication.onCreate();
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Unable to instantiate Application():" + localException.toString(), localException);
      }
    }
  }
  
  private void callCallActivityOnSaveInstanceState(ActivityClientRecord paramActivityClientRecord)
  {
    paramActivityClientRecord.state = new Bundle();
    paramActivityClientRecord.state.setAllowFds(false);
    if (paramActivityClientRecord.isPersistable())
    {
      paramActivityClientRecord.persistentState = new PersistableBundle();
      this.mInstrumentation.callActivityOnSaveInstanceState(paramActivityClientRecord.activity, paramActivityClientRecord.state, paramActivityClientRecord.persistentState);
      return;
    }
    this.mInstrumentation.callActivityOnSaveInstanceState(paramActivityClientRecord.activity, paramActivityClientRecord.state);
  }
  
  private static boolean checkAndUpdateLifecycleSeq(int paramInt, ActivityClientRecord paramActivityClientRecord, String paramString)
  {
    if (paramActivityClientRecord == null) {
      return true;
    }
    if (paramInt < paramActivityClientRecord.lastProcessedSeq) {
      return false;
    }
    paramActivityClientRecord.lastProcessedSeq = paramInt;
    return true;
  }
  
  static final void cleanUpPendingRemoveWindows(ActivityClientRecord paramActivityClientRecord, boolean paramBoolean)
  {
    if ((!paramActivityClientRecord.mPreserveWindow) || (paramBoolean))
    {
      if (paramActivityClientRecord.mPendingRemoveWindow != null)
      {
        paramActivityClientRecord.mPendingRemoveWindowManager.removeViewImmediate(paramActivityClientRecord.mPendingRemoveWindow.getDecorView());
        IBinder localIBinder = paramActivityClientRecord.mPendingRemoveWindow.getDecorView().getWindowToken();
        if (localIBinder != null) {
          WindowManagerGlobal.getInstance().closeAll(localIBinder, paramActivityClientRecord.activity.getClass().getName(), "Activity");
        }
      }
      paramActivityClientRecord.mPendingRemoveWindow = null;
      paramActivityClientRecord.mPendingRemoveWindowManager = null;
      return;
    }
  }
  
  private Context createBaseContextForActivity(ActivityClientRecord paramActivityClientRecord, Activity paramActivity)
  {
    int i = 0;
    for (;;)
    {
      int j;
      ContextImpl localContextImpl;
      String str;
      try
      {
        j = ActivityManagerNative.getDefault().getActivityDisplayId(paramActivityClientRecord.token);
        localContextImpl = ContextImpl.createActivityContext(this, paramActivityClientRecord.packageInfo, paramActivityClientRecord.token, j, paramActivityClientRecord.overrideConfig);
        localContextImpl.setOuterContext(paramActivity);
        paramActivity = DisplayManagerGlobal.getInstance();
        str = SystemProperties.get("debug.second-display.pkg");
        if ((str == null) || (str.isEmpty())) {
          return localContextImpl;
        }
      }
      catch (RemoteException paramActivityClientRecord)
      {
        throw paramActivityClientRecord.rethrowFromSystemServer();
      }
      if (paramActivityClientRecord.packageInfo.mPackageName.contains(str))
      {
        paramActivityClientRecord = paramActivity.getDisplayIds();
        j = paramActivityClientRecord.length;
        while (i < j)
        {
          int k = paramActivityClientRecord[i];
          if (k != 0) {
            return localContextImpl.createDisplayContext(paramActivity.getCompatibleDisplay(k, localContextImpl.getDisplayAdjustments(k)));
          }
          i += 1;
        }
      }
    }
  }
  
  private static Configuration createNewConfigAndUpdateIfNotNull(Configuration paramConfiguration1, Configuration paramConfiguration2)
  {
    if (paramConfiguration2 == null) {
      return paramConfiguration1;
    }
    paramConfiguration1 = new Configuration(paramConfiguration1);
    paramConfiguration1.updateFrom(paramConfiguration2);
    return paramConfiguration1;
  }
  
  private Bitmap createThumbnailBitmap(ActivityClientRecord paramActivityClientRecord)
  {
    localObject2 = this.mAvailThumbnailBitmap;
    Object localObject1 = localObject2;
    if (localObject2 == null) {}
    try
    {
      int j = this.mThumbnailWidth;
      int i;
      if (j < 0)
      {
        localObject1 = paramActivityClientRecord.activity.getResources();
        j = ((Resources)localObject1).getDimensionPixelSize(17104898);
        this.mThumbnailWidth = j;
        i = ((Resources)localObject1).getDimensionPixelSize(17104897);
        this.mThumbnailHeight = i;
      }
      for (;;)
      {
        localObject1 = localObject2;
        if (j > 0)
        {
          localObject1 = localObject2;
          if (i > 0)
          {
            localObject1 = Bitmap.createBitmap(paramActivityClientRecord.activity.getResources().getDisplayMetrics(), j, i, THUMBNAIL_FORMAT);
            ((Bitmap)localObject1).eraseColor(0);
          }
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          break;
        }
        Object localObject3 = this.mThumbnailCanvas;
        localObject2 = localObject3;
        if (localObject3 == null)
        {
          localObject2 = new Canvas();
          this.mThumbnailCanvas = ((Canvas)localObject2);
        }
        ((Canvas)localObject2).setBitmap((Bitmap)localObject1);
        localObject3 = localObject1;
        if (!paramActivityClientRecord.activity.onCreateThumbnail((Bitmap)localObject1, (Canvas)localObject2))
        {
          this.mAvailThumbnailBitmap = ((Bitmap)localObject1);
          localObject3 = null;
        }
        ((Canvas)localObject2).setBitmap(null);
        return (Bitmap)localObject3;
        i = this.mThumbnailHeight;
      }
      return (Bitmap)localObject2;
    }
    catch (Exception localException)
    {
      if (!this.mInstrumentation.onException(paramActivityClientRecord.activity, localException)) {
        throw new RuntimeException("Unable to create thumbnail of " + paramActivityClientRecord.intent.getComponent().toShortString() + ": " + localException.toString(), localException);
      }
      localObject2 = null;
    }
  }
  
  public static ActivityThread currentActivityThread()
  {
    return sCurrentActivityThread;
  }
  
  public static Application currentApplication()
  {
    Application localApplication = null;
    ActivityThread localActivityThread = currentActivityThread();
    if (localActivityThread != null) {
      localApplication = localActivityThread.mInitialApplication;
    }
    return localApplication;
  }
  
  public static String currentOpPackageName()
  {
    Object localObject2 = null;
    ActivityThread localActivityThread = currentActivityThread();
    Object localObject1 = localObject2;
    if (localActivityThread != null)
    {
      localObject1 = localObject2;
      if (localActivityThread.getApplication() != null) {
        localObject1 = localActivityThread.getApplication().getOpPackageName();
      }
    }
    return (String)localObject1;
  }
  
  public static String currentPackageName()
  {
    ActivityThread localActivityThread = currentActivityThread();
    if ((localActivityThread != null) && (localActivityThread.mBoundApplication != null)) {
      return localActivityThread.mBoundApplication.appInfo.packageName;
    }
    return sEmbryoPackageName;
  }
  
  public static String currentProcessName()
  {
    Object localObject2 = null;
    ActivityThread localActivityThread = currentActivityThread();
    Object localObject1 = localObject2;
    if (localActivityThread != null)
    {
      localObject1 = localObject2;
      if (localActivityThread.mBoundApplication != null) {
        localObject1 = localActivityThread.mBoundApplication.processName;
      }
    }
    return (String)localObject1;
  }
  
  public static String currentResDir()
  {
    Object localObject2 = null;
    ActivityThread localActivityThread = currentActivityThread();
    Object localObject1 = localObject2;
    if (localActivityThread != null)
    {
      localObject1 = localObject2;
      if (localActivityThread.mBoundApplication != null)
      {
        localObject1 = localObject2;
        if (localActivityThread.mBoundApplication.appInfo != null) {
          localObject1 = localActivityThread.mBoundApplication.appInfo.sourceDir;
        }
      }
    }
    return (String)localObject1;
  }
  
  private void deliverNewIntents(ActivityClientRecord paramActivityClientRecord, List<ReferrerIntent> paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      ReferrerIntent localReferrerIntent = (ReferrerIntent)paramList.get(i);
      localReferrerIntent.setExtrasClassLoader(paramActivityClientRecord.activity.getClassLoader());
      localReferrerIntent.prepareToEnterProcess();
      paramActivityClientRecord.activity.mFragments.noteStateNotSaved();
      this.mInstrumentation.callActivityOnNewIntent(paramActivityClientRecord.activity, localReferrerIntent);
      i += 1;
    }
  }
  
  private void deliverResults(ActivityClientRecord paramActivityClientRecord, List<ResultInfo> paramList)
  {
    int j = paramList.size();
    int i = 0;
    ResultInfo localResultInfo;
    while (i < j)
    {
      localResultInfo = (ResultInfo)paramList.get(i);
      try
      {
        if (localResultInfo.mData != null)
        {
          localResultInfo.mData.setExtrasClassLoader(paramActivityClientRecord.activity.getClassLoader());
          localResultInfo.mData.prepareToEnterProcess();
        }
        paramActivityClientRecord.activity.dispatchActivityResult(localResultInfo.mResultWho, localResultInfo.mRequestCode, localResultInfo.mResultCode, localResultInfo.mData);
      }
      catch (Exception localException)
      {
        while (this.mInstrumentation.onException(paramActivityClientRecord.activity, localException)) {}
        throw new RuntimeException("Failure delivering result " + localResultInfo + " to activity " + paramActivityClientRecord.intent.getComponent().toShortString() + ": " + localException.toString(), localException);
      }
      i += 1;
    }
  }
  
  private native void dumpGraphicsInfo(FileDescriptor paramFileDescriptor);
  
  public static void dumpMemInfoTable(PrintWriter paramPrintWriter, Debug.MemoryInfo paramMemoryInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, String paramString, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6)
  {
    if (paramBoolean1)
    {
      paramPrintWriter.print(4);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramInt);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong1);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong4);
      paramPrintWriter.print(',');
      paramPrintWriter.print("N/A,");
      paramPrintWriter.print(paramLong1 + paramLong4);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong2);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong5);
      paramPrintWriter.print(',');
      paramPrintWriter.print("N/A,");
      paramPrintWriter.print(paramLong2 + paramLong5);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong3);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramLong6);
      paramPrintWriter.print(',');
      paramPrintWriter.print("N/A,");
      paramPrintWriter.print(paramLong3 + paramLong6);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativePss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikPss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherPss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalPss());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativeSwappablePss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikSwappablePss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherSwappablePss);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalSwappablePss());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativeSharedDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikSharedDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherSharedDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalSharedDirty());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativeSharedClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikSharedClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherSharedClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalSharedClean());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativePrivateDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikPrivateDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherPrivateDirty);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalPrivateDirty());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativePrivateClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikPrivateClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherPrivateClean);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalPrivateClean());
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.nativeSwappedOut);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.dalvikSwappedOut);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.otherSwappedOut);
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramMemoryInfo.getTotalSwappedOut());
      paramPrintWriter.print(',');
      if (paramMemoryInfo.hasSwappedOutPss)
      {
        paramPrintWriter.print(paramMemoryInfo.nativeSwappedOutPss);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.dalvikSwappedOutPss);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.otherSwappedOutPss);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getTotalSwappedOutPss());
        paramPrintWriter.print(',');
        paramInt = 0;
        label635:
        if (paramInt >= 17) {
          break label842;
        }
        paramPrintWriter.print(Debug.MemoryInfo.getOtherLabel(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherPss(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherSwappablePss(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherSharedDirty(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherSharedClean(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherPrivateDirty(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherPrivateClean(paramInt));
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramMemoryInfo.getOtherSwappedOut(paramInt));
        paramPrintWriter.print(',');
        if (!paramMemoryInfo.hasSwappedOutPss) {
          break label832;
        }
        paramPrintWriter.print(paramMemoryInfo.getOtherSwappedOutPss(paramInt));
        paramPrintWriter.print(',');
      }
      for (;;)
      {
        paramInt += 1;
        break label635;
        paramPrintWriter.print("N/A,");
        paramPrintWriter.print("N/A,");
        paramPrintWriter.print("N/A,");
        paramPrintWriter.print("N/A,");
        break;
        label832:
        paramPrintWriter.print("N/A,");
      }
      label842:
      return;
    }
    if (!paramBoolean4)
    {
      int i;
      int j;
      int k;
      int m;
      int n;
      int i1;
      label1158:
      label1309:
      int i5;
      int i3;
      int i2;
      int i4;
      label1462:
      int i20;
      int i19;
      int i18;
      int i17;
      int i16;
      int i15;
      if (paramBoolean2) {
        if (paramMemoryInfo.hasSwappedOutPss)
        {
          paramString = "SwapPss";
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "Pss", "Pss", "Shared", "Private", "Shared", "Private", paramString, "Heap", "Heap", "Heap" });
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "Total", "Clean", "Dirty", "Dirty", "Clean", "Clean", "Dirty", "Size", "Alloc", "Free" });
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "------", "------", "------", "------", "------", "------", "------", "------", "------", "------" });
          i = paramMemoryInfo.nativePss;
          j = paramMemoryInfo.nativeSwappablePss;
          k = paramMemoryInfo.nativeSharedDirty;
          m = paramMemoryInfo.nativePrivateDirty;
          n = paramMemoryInfo.nativeSharedClean;
          i1 = paramMemoryInfo.nativePrivateClean;
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label1765;
          }
          paramInt = paramMemoryInfo.nativeSwappedOut;
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Native Heap", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i1), Integer.valueOf(paramInt), Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3) });
          i = paramMemoryInfo.dalvikPss;
          j = paramMemoryInfo.dalvikSwappablePss;
          k = paramMemoryInfo.dalvikSharedDirty;
          m = paramMemoryInfo.dalvikPrivateDirty;
          n = paramMemoryInfo.dalvikSharedClean;
          i1 = paramMemoryInfo.dalvikPrivateClean;
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label1774;
          }
          paramInt = paramMemoryInfo.dalvikSwappedOut;
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Dalvik Heap", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i1), Integer.valueOf(paramInt), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6) });
          i5 = paramMemoryInfo.otherPss;
          i3 = paramMemoryInfo.otherSwappablePss;
          i2 = paramMemoryInfo.otherSharedDirty;
          i1 = paramMemoryInfo.otherPrivateDirty;
          n = paramMemoryInfo.otherSharedClean;
          m = paramMemoryInfo.otherPrivateClean;
          i = paramMemoryInfo.otherSwappedOut;
          paramInt = paramMemoryInfo.otherSwappedOutPss;
          i4 = 0;
          if (i4 >= 17) {
            break label2401;
          }
          i20 = paramMemoryInfo.getOtherPss(i4);
          i19 = paramMemoryInfo.getOtherSwappablePss(i4);
          i18 = paramMemoryInfo.getOtherSharedDirty(i4);
          i17 = paramMemoryInfo.getOtherPrivateDirty(i4);
          i16 = paramMemoryInfo.getOtherSharedClean(i4);
          i15 = paramMemoryInfo.getOtherPrivateClean(i4);
          k = paramMemoryInfo.getOtherSwappedOut(i4);
          j = paramMemoryInfo.getOtherSwappedOutPss(i4);
          if ((i20 == 0) && (i18 == 0)) {
            break label2225;
          }
          label1543:
          if (!paramBoolean2) {
            break label2305;
          }
          paramString = Debug.MemoryInfo.getOtherLabel(i4);
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label2298;
          }
        }
      }
      label1716:
      label1765:
      label1774:
      label1795:
      label2015:
      label2207:
      label2216:
      label2225:
      label2298:
      for (int i6 = j;; i6 = k)
      {
        printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { paramString, Integer.valueOf(i20), Integer.valueOf(i19), Integer.valueOf(i18), Integer.valueOf(i17), Integer.valueOf(i16), Integer.valueOf(i15), Integer.valueOf(i6), "", "", "" });
        int i11 = i5 - i20;
        int i8 = i3 - i19;
        int i9 = i2 - i18;
        int i12 = i1 - i17;
        int i10 = n - i16;
        int i13 = m - i15;
        int i7 = i - k;
        i6 = paramInt - j;
        i4 += 1;
        m = i13;
        i1 = i12;
        i5 = i11;
        n = i10;
        i2 = i9;
        i3 = i8;
        i = i7;
        paramInt = i6;
        break label1462;
        paramString = "Swap";
        break;
        paramInt = paramMemoryInfo.nativeSwappedOutPss;
        break label1158;
        paramInt = paramMemoryInfo.dalvikSwappedOutPss;
        break label1309;
        if (paramMemoryInfo.hasSwappedOutPss)
        {
          paramString = "SwapPss";
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "Pss", "Private", "Private", paramString, "Heap", "Heap", "Heap" });
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "Total", "Dirty", "Clean", "Dirty", "Size", "Alloc", "Free" });
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "", "------", "------", "------", "------", "------", "------", "------", "------" });
          i = paramMemoryInfo.nativePss;
          j = paramMemoryInfo.nativePrivateDirty;
          k = paramMemoryInfo.nativePrivateClean;
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label2207;
          }
          paramInt = paramMemoryInfo.nativeSwappedOutPss;
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Native Heap", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt), Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3) });
          i = paramMemoryInfo.dalvikPss;
          j = paramMemoryInfo.dalvikPrivateDirty;
          k = paramMemoryInfo.dalvikPrivateClean;
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label2216;
          }
        }
        for (paramInt = paramMemoryInfo.dalvikSwappedOutPss;; paramInt = paramMemoryInfo.dalvikSwappedOut)
        {
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Dalvik Heap", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6) });
          break;
          paramString = "Swap";
          break label1795;
          paramInt = paramMemoryInfo.nativeSwappedOut;
          break label2015;
        }
        if ((i17 != 0) || (i16 != 0) || (i15 != 0)) {
          break label1543;
        }
        if (paramMemoryInfo.hasSwappedOutPss) {}
        for (int i14 = j;; i14 = k)
        {
          i13 = m;
          i12 = i1;
          i11 = i5;
          i10 = n;
          i9 = i2;
          i8 = i3;
          i7 = i;
          i6 = paramInt;
          if (i14 == 0) {
            break label1716;
          }
          break;
        }
      }
      label2305:
      paramString = Debug.MemoryInfo.getOtherLabel(i4);
      if (paramMemoryInfo.hasSwappedOutPss) {}
      for (i6 = j;; i6 = k)
      {
        printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { paramString, Integer.valueOf(i20), Integer.valueOf(i17), Integer.valueOf(i15), Integer.valueOf(i6), "", "", "" });
        break;
      }
      label2401:
      if (paramBoolean2) {
        if (paramMemoryInfo.hasSwappedOutPss)
        {
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Unknown", Integer.valueOf(i5), Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i1), Integer.valueOf(n), Integer.valueOf(m), Integer.valueOf(paramInt), "", "", "" });
          i = paramMemoryInfo.getTotalPss();
          j = paramMemoryInfo.getTotalSwappablePss();
          k = paramMemoryInfo.getTotalSharedDirty();
          m = paramMemoryInfo.getTotalPrivateDirty();
          n = paramMemoryInfo.getTotalSharedClean();
          i1 = paramMemoryInfo.getTotalPrivateClean();
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label2901;
          }
          paramInt = paramMemoryInfo.getTotalSwappedOutPss();
          label2557:
          printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "TOTAL", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i1), Integer.valueOf(paramInt), Long.valueOf(paramLong1 + paramLong4), Long.valueOf(paramLong2 + paramLong5), Long.valueOf(paramLong3 + paramLong6) });
          if (!paramBoolean3) {
            break label3260;
          }
          paramPrintWriter.println(" ");
          paramPrintWriter.println(" Dalvik Details");
          j = 17;
          label2691:
          if (j >= 25) {
            break label3260;
          }
          m = paramMemoryInfo.getOtherPss(j);
          i2 = paramMemoryInfo.getOtherSwappablePss(j);
          i3 = paramMemoryInfo.getOtherSharedDirty(j);
          n = paramMemoryInfo.getOtherPrivateDirty(j);
          i4 = paramMemoryInfo.getOtherSharedClean(j);
          i1 = paramMemoryInfo.getOtherPrivateClean(j);
          paramInt = paramMemoryInfo.getOtherSwappedOut(j);
          i = paramMemoryInfo.getOtherSwappedOutPss(j);
          if ((m == 0) && (i3 == 0)) {
            break label3120;
          }
          label2772:
          if (!paramBoolean2) {
            break label3168;
          }
          paramString = Debug.MemoryInfo.getOtherLabel(j);
          if (!paramMemoryInfo.hasSwappedOutPss) {
            break label3161;
          }
        }
      }
      for (;;)
      {
        printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s", new Object[] { paramString, Integer.valueOf(m), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(n), Integer.valueOf(i4), Integer.valueOf(i1), Integer.valueOf(i), "", "", "" });
        label2901:
        label2917:
        label3111:
        label3120:
        label3159:
        for (;;)
        {
          j += 1;
          break label2691;
          paramInt = i;
          break;
          paramInt = paramMemoryInfo.getTotalSwappedOut();
          break label2557;
          if (paramMemoryInfo.hasSwappedOutPss)
          {
            printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "Unknown", Integer.valueOf(i5), Integer.valueOf(i1), Integer.valueOf(m), Integer.valueOf(paramInt), "", "", "" });
            i = paramMemoryInfo.getTotalPss();
            j = paramMemoryInfo.getTotalPrivateDirty();
            k = paramMemoryInfo.getTotalPrivateClean();
            if (!paramMemoryInfo.hasSwappedOutPss) {
              break label3111;
            }
          }
          for (paramInt = paramMemoryInfo.getTotalSwappedOutPss();; paramInt = paramMemoryInfo.getTotalSwappedOut())
          {
            printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { "TOTAL", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt), Long.valueOf(paramLong1 + paramLong4), Long.valueOf(paramLong2 + paramLong5), Long.valueOf(paramLong3 + paramLong6) });
            break;
            paramInt = i;
            break label2917;
          }
          if ((n != 0) || (i4 != 0) || (i1 != 0)) {
            break label2772;
          }
          if (paramMemoryInfo.hasSwappedOutPss) {}
          for (k = i;; k = paramInt)
          {
            if (k == 0) {
              break label3159;
            }
            break;
          }
        }
        label3161:
        i = paramInt;
      }
      label3168:
      paramString = Debug.MemoryInfo.getOtherLabel(j);
      if (paramMemoryInfo.hasSwappedOutPss) {}
      for (;;)
      {
        printRow(paramPrintWriter, "%13s %8s %8s %8s %8s %8s %8s %8s", new Object[] { paramString, Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i1), Integer.valueOf(i), "", "", "" });
        break;
        i = paramInt;
      }
    }
    label3260:
    paramPrintWriter.println(" ");
    paramPrintWriter.println(" App Summary");
    printRow(paramPrintWriter, "%21s %8s", new Object[] { "", "Pss(KB)" });
    printRow(paramPrintWriter, "%21s %8s", new Object[] { "", "------" });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Java Heap:", Integer.valueOf(paramMemoryInfo.getSummaryJavaHeap()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Native Heap:", Integer.valueOf(paramMemoryInfo.getSummaryNativeHeap()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Code:", Integer.valueOf(paramMemoryInfo.getSummaryCode()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Stack:", Integer.valueOf(paramMemoryInfo.getSummaryStack()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Graphics:", Integer.valueOf(paramMemoryInfo.getSummaryGraphics()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "Private Other:", Integer.valueOf(paramMemoryInfo.getSummaryPrivateOther()) });
    printRow(paramPrintWriter, "%21s %8d", new Object[] { "System:", Integer.valueOf(paramMemoryInfo.getSummarySystem()) });
    paramPrintWriter.println(" ");
    if (paramMemoryInfo.hasSwappedOutPss)
    {
      printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "TOTAL:", Integer.valueOf(paramMemoryInfo.getSummaryTotalPss()), "TOTAL SWAP PSS:", Integer.valueOf(paramMemoryInfo.getSummaryTotalSwapPss()) });
      return;
    }
    printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "TOTAL:", Integer.valueOf(paramMemoryInfo.getSummaryTotalPss()), "TOTAL SWAP (KB):", Integer.valueOf(paramMemoryInfo.getSummaryTotalSwap()) });
  }
  
  static void freeTextLayoutCachesIfNeeded(int paramInt)
  {
    int i = 0;
    if (paramInt != 0)
    {
      if ((paramInt & 0x4) != 0) {
        i = 1;
      }
      if (i != 0) {
        Canvas.freeTextLayoutCaches();
      }
    }
  }
  
  private String getInstrumentationLibrary(ApplicationInfo paramApplicationInfo, InstrumentationInfo paramInstrumentationInfo)
  {
    if ((paramApplicationInfo.primaryCpuAbi != null) && (paramApplicationInfo.secondaryCpuAbi != null))
    {
      paramApplicationInfo = VMRuntime.getInstructionSet(paramApplicationInfo.secondaryCpuAbi);
      String str = SystemProperties.get("ro.dalvik.vm.isa." + paramApplicationInfo);
      if (str.isEmpty()) {}
      while (VMRuntime.getRuntime().vmInstructionSet().equals(paramApplicationInfo))
      {
        return paramInstrumentationInfo.secondaryNativeLibraryDir;
        paramApplicationInfo = str;
      }
    }
    return paramInstrumentationInfo.nativeLibraryDir;
  }
  
  public static Intent getIntentBeingBroadcast()
  {
    return (Intent)sCurrentBroadcastIntent.get();
  }
  
  private int getLifecycleSeq()
  {
    synchronized (this.mResourcesManager)
    {
      int i = this.mLifecycleSeq;
      this.mLifecycleSeq = (i + 1);
      return i;
    }
  }
  
  private LoadedApk getPackageInfo(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    ResourcesManager localResourcesManager;
    Object localObject1;
    if (UserHandle.myUserId() != UserHandle.getUserId(paramApplicationInfo.uid))
    {
      i = 1;
      localResourcesManager = this.mResourcesManager;
      if (i == 0) {
        break label99;
      }
      localObject1 = null;
      label33:
      if (localObject1 == null) {
        break label295;
      }
    }
    for (;;)
    {
      try
      {
        localObject1 = (LoadedApk)((WeakReference)localObject1).get();
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (((LoadedApk)localObject1).mResources != null)
          {
            bool = ((LoadedApk)localObject1).mResources.getAssets().isUpToDate();
            if (bool) {
              localObject2 = localObject1;
            }
          }
          else
          {
            return (LoadedApk)localObject2;
            i = 0;
            break;
            label99:
            if (paramBoolean2)
            {
              localObject1 = (WeakReference)this.mPackages.get(paramApplicationInfo.packageName);
              break label33;
            }
            localObject1 = (WeakReference)this.mResourcePackages.get(paramApplicationInfo.packageName);
            break label33;
          }
        }
        if (!paramBoolean2) {
          break label263;
        }
        if ((paramApplicationInfo.flags & 0x4) != 0)
        {
          bool = true;
          paramCompatibilityInfo = new LoadedApk(this, paramApplicationInfo, paramCompatibilityInfo, paramClassLoader, paramBoolean1, bool, paramBoolean3);
          if ((this.mSystemThread) && ("android".equals(paramApplicationInfo.packageName))) {
            paramCompatibilityInfo.installSystemApplicationInfo(paramApplicationInfo, getSystemContext().mPackageInfo.getClassLoader());
          }
          localObject2 = paramCompatibilityInfo;
          if (i != 0) {
            continue;
          }
          if (!paramBoolean2) {
            break label269;
          }
          this.mPackages.put(paramApplicationInfo.packageName, new WeakReference(paramCompatibilityInfo));
          localObject2 = paramCompatibilityInfo;
          continue;
        }
        bool = false;
      }
      finally {}
      continue;
      label263:
      boolean bool = false;
      continue;
      label269:
      this.mResourcePackages.put(paramApplicationInfo.packageName, new WeakReference(paramCompatibilityInfo));
      Object localObject2 = paramCompatibilityInfo;
      continue;
      label295:
      localObject1 = null;
    }
  }
  
  public static IPackageManager getPackageManager()
  {
    if (sPackageManager != null) {
      return sPackageManager;
    }
    sPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    return sPackageManager;
  }
  
  /* Error */
  private void handleBindApplication(AppBindData paramAppBindData)
  {
    // Byte code:
    //   0: invokestatic 1413	dalvik/system/VMRuntime:registerSensitiveThread	()V
    //   3: aload_1
    //   4: getfield 1416	android/app/ActivityThread$AppBindData:trackAllocation	Z
    //   7: ifeq +7 -> 14
    //   10: iconst_1
    //   11: invokestatic 1421	org/apache/harmony/dalvik/ddmc/DdmVmInternal:enableRecentAllocations	(Z)V
    //   14: aload_1
    //   15: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   18: getfield 1424	android/content/pm/ApplicationInfo:privateFlags	I
    //   21: ldc_w 1425
    //   24: iand
    //   25: ifeq +11 -> 36
    //   28: invokestatic 1323	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   31: pop
    //   32: iconst_1
    //   33: invokestatic 1428	dalvik/system/VMRuntime:setVMRuntimeFlag	(I)V
    //   36: invokestatic 1434	android/os/SystemClock:elapsedRealtime	()J
    //   39: invokestatic 1437	android/os/SystemClock:uptimeMillis	()J
    //   42: invokestatic 1443	android/os/Process:setStartTimes	(JJ)V
    //   45: aload_0
    //   46: aload_1
    //   47: putfield 957	android/app/ActivityThread:mBoundApplication	Landroid/app/ActivityThread$AppBindData;
    //   50: aload_0
    //   51: new 573	android/content/res/Configuration
    //   54: dup
    //   55: aload_1
    //   56: getfield 1446	android/app/ActivityThread$AppBindData:config	Landroid/content/res/Configuration;
    //   59: invokespecial 875	android/content/res/Configuration:<init>	(Landroid/content/res/Configuration;)V
    //   62: putfield 1448	android/app/ActivityThread:mConfiguration	Landroid/content/res/Configuration;
    //   65: aload_0
    //   66: new 573	android/content/res/Configuration
    //   69: dup
    //   70: aload_1
    //   71: getfield 1446	android/app/ActivityThread$AppBindData:config	Landroid/content/res/Configuration;
    //   74: invokespecial 875	android/content/res/Configuration:<init>	(Landroid/content/res/Configuration;)V
    //   77: putfield 1450	android/app/ActivityThread:mCompatConfiguration	Landroid/content/res/Configuration;
    //   80: aload_0
    //   81: new 64	android/app/ActivityThread$Profiler
    //   84: dup
    //   85: invokespecial 1451	android/app/ActivityThread$Profiler:<init>	()V
    //   88: putfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   91: aload_1
    //   92: getfield 1457	android/app/ActivityThread$AppBindData:initProfilerInfo	Landroid/app/ProfilerInfo;
    //   95: ifnull +59 -> 154
    //   98: aload_0
    //   99: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   102: aload_1
    //   103: getfield 1457	android/app/ActivityThread$AppBindData:initProfilerInfo	Landroid/app/ProfilerInfo;
    //   106: getfield 1462	android/app/ProfilerInfo:profileFile	Ljava/lang/String;
    //   109: putfield 1463	android/app/ActivityThread$Profiler:profileFile	Ljava/lang/String;
    //   112: aload_0
    //   113: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   116: aload_1
    //   117: getfield 1457	android/app/ActivityThread$AppBindData:initProfilerInfo	Landroid/app/ProfilerInfo;
    //   120: getfield 1467	android/app/ProfilerInfo:profileFd	Landroid/os/ParcelFileDescriptor;
    //   123: putfield 1468	android/app/ActivityThread$Profiler:profileFd	Landroid/os/ParcelFileDescriptor;
    //   126: aload_0
    //   127: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   130: aload_1
    //   131: getfield 1457	android/app/ActivityThread$AppBindData:initProfilerInfo	Landroid/app/ProfilerInfo;
    //   134: getfield 1471	android/app/ProfilerInfo:samplingInterval	I
    //   137: putfield 1472	android/app/ActivityThread$Profiler:samplingInterval	I
    //   140: aload_0
    //   141: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   144: aload_1
    //   145: getfield 1457	android/app/ActivityThread$AppBindData:initProfilerInfo	Landroid/app/ProfilerInfo;
    //   148: getfield 1475	android/app/ProfilerInfo:autoStopProfiler	Z
    //   151: putfield 1476	android/app/ActivityThread$Profiler:autoStopProfiler	Z
    //   154: aload_1
    //   155: getfield 970	android/app/ActivityThread$AppBindData:processName	Ljava/lang/String;
    //   158: invokestatic 1479	android/os/Process:setArgV0	(Ljava/lang/String;)V
    //   161: aload_1
    //   162: getfield 970	android/app/ActivityThread$AppBindData:processName	Ljava/lang/String;
    //   165: invokestatic 612	android/os/UserHandle:myUserId	()I
    //   168: invokestatic 618	android/ddm/DdmHandleAppName:setAppName	(Ljava/lang/String;I)V
    //   171: aload_1
    //   172: getfield 970	android/app/ActivityThread$AppBindData:processName	Ljava/lang/String;
    //   175: ifnull +21 -> 196
    //   178: aload_1
    //   179: getfield 970	android/app/ActivityThread$AppBindData:processName	Ljava/lang/String;
    //   182: ldc_w 1481
    //   185: invokevirtual 854	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   188: ifeq +8 -> 196
    //   191: aload_0
    //   192: iconst_0
    //   193: putfield 571	android/app/ActivityThread:mDisableTrimMemory	Z
    //   196: aload_1
    //   197: getfield 1484	android/app/ActivityThread$AppBindData:persistent	Z
    //   200: ifeq +13 -> 213
    //   203: invokestatic 1489	android/app/ActivityManager:isHighEndGfx	()Z
    //   206: ifne +7 -> 213
    //   209: iconst_0
    //   210: invokestatic 1494	android/view/ThreadedRenderer:disable	(Z)V
    //   213: aload_0
    //   214: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   217: getfield 1468	android/app/ActivityThread$Profiler:profileFd	Landroid/os/ParcelFileDescriptor;
    //   220: ifnull +10 -> 230
    //   223: aload_0
    //   224: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   227: invokevirtual 1497	android/app/ActivityThread$Profiler:startProfiling	()V
    //   230: aload_1
    //   231: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   234: getfield 1500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   237: bipush 12
    //   239: if_icmpgt +9 -> 248
    //   242: getstatic 1506	android/os/AsyncTask:THREAD_POOL_EXECUTOR	Ljava/util/concurrent/Executor;
    //   245: invokestatic 1510	android/os/AsyncTask:setDefaultExecutor	(Ljava/util/concurrent/Executor;)V
    //   248: aload_1
    //   249: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   252: getfield 1500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   255: invokestatic 1515	android/os/Message:updateCheckRecycle	(I)V
    //   258: aconst_null
    //   259: invokestatic 1521	java/util/TimeZone:setDefault	(Ljava/util/TimeZone;)V
    //   262: aload_1
    //   263: getfield 1446	android/app/ActivityThread$AppBindData:config	Landroid/content/res/Configuration;
    //   266: invokevirtual 1525	android/content/res/Configuration:getLocales	()Landroid/os/LocaleList;
    //   269: invokestatic 1530	android/os/LocaleList:setDefault	(Landroid/os/LocaleList;)V
    //   272: aload_0
    //   273: getfield 249	android/app/ActivityThread:mResourcesManager	Landroid/app/ResourcesManager;
    //   276: astore 4
    //   278: aload 4
    //   280: monitorenter
    //   281: aload_0
    //   282: getfield 249	android/app/ActivityThread:mResourcesManager	Landroid/app/ResourcesManager;
    //   285: aload_1
    //   286: getfield 1446	android/app/ActivityThread$AppBindData:config	Landroid/content/res/Configuration;
    //   289: aload_1
    //   290: getfield 1534	android/app/ActivityThread$AppBindData:compatInfo	Landroid/content/res/CompatibilityInfo;
    //   293: invokevirtual 1538	android/app/ResourcesManager:applyConfigurationToResourcesLocked	(Landroid/content/res/Configuration;Landroid/content/res/CompatibilityInfo;)Z
    //   296: pop
    //   297: aload_0
    //   298: aload_1
    //   299: getfield 1446	android/app/ActivityThread$AppBindData:config	Landroid/content/res/Configuration;
    //   302: getfield 1541	android/content/res/Configuration:densityDpi	I
    //   305: putfield 1543	android/app/ActivityThread:mCurDefaultDisplayDpi	I
    //   308: aload_0
    //   309: aload_0
    //   310: getfield 1543	android/app/ActivityThread:mCurDefaultDisplayDpi	I
    //   313: invokevirtual 1547	android/app/ActivityThread:applyCompatConfiguration	(I)Landroid/content/res/Configuration;
    //   316: pop
    //   317: aload 4
    //   319: monitorexit
    //   320: aload_1
    //   321: aload_0
    //   322: aload_1
    //   323: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   326: aload_1
    //   327: getfield 1534	android/app/ActivityThread$AppBindData:compatInfo	Landroid/content/res/CompatibilityInfo;
    //   330: invokevirtual 1551	android/app/ActivityThread:getPackageInfoNoCheck	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;)Landroid/app/LoadedApk;
    //   333: putfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   336: aload_1
    //   337: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   340: getfield 1373	android/content/pm/ApplicationInfo:flags	I
    //   343: sipush 8192
    //   346: iand
    //   347: ifne +724 -> 1071
    //   350: aload_0
    //   351: iconst_1
    //   352: putfield 1556	android/app/ActivityThread:mDensityCompatMode	Z
    //   355: sipush 160
    //   358: invokestatic 1559	android/graphics/Bitmap:setDefaultDensity	(I)V
    //   361: aload_0
    //   362: invokespecial 1562	android/app/ActivityThread:updateDefaultDensity	()V
    //   365: ldc_w 1564
    //   368: aload_0
    //   369: getfield 569	android/app/ActivityThread:mCoreSettings	Landroid/os/Bundle;
    //   372: ldc_w 1566
    //   375: invokevirtual 1569	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   378: invokevirtual 1330	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   381: invokestatic 1574	java/text/DateFormat:set24HourTimePref	(Z)V
    //   384: aload_0
    //   385: getfield 569	android/app/ActivityThread:mCoreSettings	Landroid/os/Bundle;
    //   388: ldc_w 1576
    //   391: iconst_0
    //   392: invokevirtual 1580	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   395: ifeq +738 -> 1133
    //   398: iconst_1
    //   399: istore_3
    //   400: iload_3
    //   401: putstatic 1583	android/view/View:mDebugViewAttributes	Z
    //   404: aload_1
    //   405: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   408: getfield 1373	android/content/pm/ApplicationInfo:flags	I
    //   411: sipush 129
    //   414: iand
    //   415: ifeq +7 -> 422
    //   418: invokestatic 1588	android/os/StrictMode:conditionallyEnableDebugLogging	()Z
    //   421: pop
    //   422: aload_1
    //   423: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   426: getfield 1500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   429: bipush 11
    //   431: if_icmplt +6 -> 437
    //   434: invokestatic 1591	android/os/StrictMode:enableDeathOnNetwork	()V
    //   437: aload_1
    //   438: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   441: getfield 1500	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   444: bipush 24
    //   446: if_icmplt +6 -> 452
    //   449: invokestatic 1594	android/os/StrictMode:enableDeathOnFileUriExposure	()V
    //   452: invokestatic 1599	android/security/NetworkSecurityPolicy:getInstance	()Landroid/security/NetworkSecurityPolicy;
    //   455: astore 4
    //   457: aload_1
    //   458: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   461: getfield 1373	android/content/pm/ApplicationInfo:flags	I
    //   464: ldc_w 1600
    //   467: iand
    //   468: ifeq +670 -> 1138
    //   471: iconst_1
    //   472: istore_3
    //   473: aload 4
    //   475: iload_3
    //   476: invokevirtual 1603	android/security/NetworkSecurityPolicy:setCleartextTrafficPermitted	(Z)V
    //   479: aload_1
    //   480: getfield 1606	android/app/ActivityThread$AppBindData:debugMode	I
    //   483: ifeq +87 -> 570
    //   486: sipush 8100
    //   489: invokestatic 1611	android/os/Debug:changeDebugPort	(I)V
    //   492: aload_1
    //   493: getfield 1606	android/app/ActivityThread$AppBindData:debugMode	I
    //   496: iconst_2
    //   497: if_icmpne +658 -> 1155
    //   500: ldc -114
    //   502: new 701	java/lang/StringBuilder
    //   505: dup
    //   506: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   509: ldc_w 1613
    //   512: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   515: aload_1
    //   516: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   519: invokevirtual 1616	android/app/LoadedApk:getPackageName	()Ljava/lang/String;
    //   522: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   525: ldc_w 1618
    //   528: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   531: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   534: invokestatic 1624	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   537: pop
    //   538: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   541: astore 4
    //   543: aload 4
    //   545: aload_0
    //   546: getfield 482	android/app/ActivityThread:mAppThread	Landroid/app/ActivityThread$ApplicationThread;
    //   549: iconst_1
    //   550: invokeinterface 1628 3 0
    //   555: invokestatic 1631	android/os/Debug:waitForDebugger	()V
    //   558: aload 4
    //   560: aload_0
    //   561: getfield 482	android/app/ActivityThread:mAppThread	Landroid/app/ActivityThread$ApplicationThread;
    //   564: iconst_0
    //   565: invokeinterface 1628 3 0
    //   570: aload_1
    //   571: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   574: getfield 1373	android/content/pm/ApplicationInfo:flags	I
    //   577: iconst_2
    //   578: iand
    //   579: ifeq +617 -> 1196
    //   582: iconst_1
    //   583: istore_3
    //   584: iload_3
    //   585: invokestatic 1636	android/os/Trace:setAppTracingAllowed	(Z)V
    //   588: iload_3
    //   589: ifeq +13 -> 602
    //   592: aload_1
    //   593: getfield 1639	android/app/ActivityThread$AppBindData:enableBinderTracking	Z
    //   596: ifeq +6 -> 602
    //   599: invokestatic 1644	android/os/Binder:enableTracing	()V
    //   602: ldc2_w 1645
    //   605: ldc_w 1648
    //   608: invokestatic 1652	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   611: ldc_w 1654
    //   614: invokestatic 1402	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   617: astore 4
    //   619: aload 4
    //   621: ifnull +21 -> 642
    //   624: aload 4
    //   626: invokestatic 1659	android/net/IConnectivityManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/net/IConnectivityManager;
    //   629: astore 4
    //   631: aload 4
    //   633: aconst_null
    //   634: invokeinterface 1665 2 0
    //   639: invokestatic 1671	android/net/Proxy:setHttpProxySystemProperty	(Landroid/net/ProxyInfo;)V
    //   642: ldc2_w 1645
    //   645: invokestatic 1674	android/os/Trace:traceEnd	(J)V
    //   648: aload_1
    //   649: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   652: ifnull +594 -> 1246
    //   655: new 1680	android/app/ApplicationPackageManager
    //   658: dup
    //   659: aconst_null
    //   660: invokestatic 1682	android/app/ActivityThread:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   663: invokespecial 1685	android/app/ApplicationPackageManager:<init>	(Landroid/app/ContextImpl;Landroid/content/pm/IPackageManager;)V
    //   666: aload_1
    //   667: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   670: iconst_0
    //   671: invokevirtual 1689	android/app/ApplicationPackageManager:getInstrumentationInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/InstrumentationInfo;
    //   674: astore 4
    //   676: aload_0
    //   677: aload 4
    //   679: getfield 1690	android/content/pm/InstrumentationInfo:packageName	Ljava/lang/String;
    //   682: putfield 517	android/app/ActivityThread:mInstrumentationPackageName	Ljava/lang/String;
    //   685: aload_0
    //   686: aload 4
    //   688: getfield 1691	android/content/pm/InstrumentationInfo:sourceDir	Ljava/lang/String;
    //   691: putfield 519	android/app/ActivityThread:mInstrumentationAppDir	Ljava/lang/String;
    //   694: aload_0
    //   695: aload 4
    //   697: getfield 1694	android/content/pm/InstrumentationInfo:splitSourceDirs	[Ljava/lang/String;
    //   700: putfield 521	android/app/ActivityThread:mInstrumentationSplitAppDirs	[Ljava/lang/String;
    //   703: aload_0
    //   704: aload_0
    //   705: aload_1
    //   706: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   709: aload 4
    //   711: invokespecial 1696	android/app/ActivityThread:getInstrumentationLibrary	(Landroid/content/pm/ApplicationInfo;Landroid/content/pm/InstrumentationInfo;)Ljava/lang/String;
    //   714: putfield 523	android/app/ActivityThread:mInstrumentationLibDir	Ljava/lang/String;
    //   717: aload_0
    //   718: aload_1
    //   719: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   722: invokevirtual 1699	android/app/LoadedApk:getAppDir	()Ljava/lang/String;
    //   725: putfield 525	android/app/ActivityThread:mInstrumentedAppDir	Ljava/lang/String;
    //   728: aload_0
    //   729: aload_1
    //   730: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   733: invokevirtual 1703	android/app/LoadedApk:getSplitAppDirs	()[Ljava/lang/String;
    //   736: putfield 527	android/app/ActivityThread:mInstrumentedSplitAppDirs	[Ljava/lang/String;
    //   739: aload_0
    //   740: aload_1
    //   741: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   744: invokevirtual 1706	android/app/LoadedApk:getLibDir	()Ljava/lang/String;
    //   747: putfield 529	android/app/ActivityThread:mInstrumentedLibDir	Ljava/lang/String;
    //   750: aload_0
    //   751: aload_1
    //   752: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   755: invokestatic 684	android/app/ContextImpl:createAppContext	(Landroid/app/ActivityThread;Landroid/app/LoadedApk;)Landroid/app/ContextImpl;
    //   758: astore 5
    //   760: aload_0
    //   761: aload 5
    //   763: aload_0
    //   764: getfield 249	android/app/ActivityThread:mResourcesManager	Landroid/app/ResourcesManager;
    //   767: invokevirtual 1710	android/app/ResourcesManager:getConfiguration	()Landroid/content/res/Configuration;
    //   770: invokevirtual 1525	android/content/res/Configuration:getLocales	()Landroid/os/LocaleList;
    //   773: invokespecial 426	android/app/ActivityThread:updateLocaleListFromAppContext	(Landroid/content/Context;Landroid/os/LocaleList;)V
    //   776: invokestatic 1713	android/os/Process:isIsolated	()Z
    //   779: ifne +17 -> 796
    //   782: ldc_w 1378
    //   785: aload 5
    //   787: invokevirtual 1714	android/app/ContextImpl:getPackageName	()Ljava/lang/String;
    //   790: invokevirtual 1330	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   793: ifeq +459 -> 1252
    //   796: ldc2_w 1645
    //   799: ldc_w 1716
    //   802: invokestatic 1652	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   805: aload 5
    //   807: invokestatic 1721	android/security/net/config/NetworkSecurityConfigProvider:install	(Landroid/content/Context;)V
    //   810: ldc2_w 1645
    //   813: invokestatic 1674	android/os/Trace:traceEnd	(J)V
    //   816: aload 4
    //   818: ifnull +616 -> 1434
    //   821: new 963	android/content/pm/ApplicationInfo
    //   824: dup
    //   825: invokespecial 1722	android/content/pm/ApplicationInfo:<init>	()V
    //   828: astore 6
    //   830: aload 4
    //   832: aload 6
    //   834: invokevirtual 1726	android/content/pm/InstrumentationInfo:copyTo	(Landroid/content/pm/ApplicationInfo;)V
    //   837: aload 6
    //   839: invokestatic 612	android/os/UserHandle:myUserId	()I
    //   842: invokevirtual 1729	android/content/pm/ApplicationInfo:initForUser	(I)V
    //   845: aload_0
    //   846: aload_0
    //   847: aload 6
    //   849: aload_1
    //   850: getfield 1534	android/app/ActivityThread$AppBindData:compatInfo	Landroid/content/res/CompatibilityInfo;
    //   853: aload 5
    //   855: invokevirtual 1730	android/app/ContextImpl:getClassLoader	()Ljava/lang/ClassLoader;
    //   858: iconst_0
    //   859: iconst_1
    //   860: iconst_0
    //   861: invokespecial 1732	android/app/ActivityThread:getPackageInfo	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;Ljava/lang/ClassLoader;ZZZ)Landroid/app/LoadedApk;
    //   864: invokestatic 684	android/app/ContextImpl:createAppContext	(Landroid/app/ActivityThread;Landroid/app/LoadedApk;)Landroid/app/ContextImpl;
    //   867: astore 6
    //   869: aload_0
    //   870: aload 6
    //   872: invokevirtual 1730	android/app/ContextImpl:getClassLoader	()Ljava/lang/ClassLoader;
    //   875: aload_1
    //   876: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   879: invokevirtual 1735	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   882: invokevirtual 1741	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   885: invokevirtual 1744	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   888: checkcast 667	android/app/Instrumentation
    //   891: putfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   894: new 936	android/content/ComponentName
    //   897: dup
    //   898: aload 4
    //   900: getfield 1690	android/content/pm/InstrumentationInfo:packageName	Ljava/lang/String;
    //   903: aload 4
    //   905: getfield 1747	android/content/pm/InstrumentationInfo:name	Ljava/lang/String;
    //   908: invokespecial 1750	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   911: astore 7
    //   913: aload_0
    //   914: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   917: aload_0
    //   918: aload 6
    //   920: aload 5
    //   922: aload 7
    //   924: aload_1
    //   925: getfield 1754	android/app/ActivityThread$AppBindData:instrumentationWatcher	Landroid/app/IInstrumentationWatcher;
    //   928: aload_1
    //   929: getfield 1758	android/app/ActivityThread$AppBindData:instrumentationUiAutomationConnection	Landroid/app/IUiAutomationConnection;
    //   932: invokevirtual 1762	android/app/Instrumentation:init	(Landroid/app/ActivityThread;Landroid/content/Context;Landroid/content/Context;Landroid/content/ComponentName;Landroid/app/IInstrumentationWatcher;Landroid/app/IUiAutomationConnection;)V
    //   935: aload_0
    //   936: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   939: getfield 1463	android/app/ActivityThread$Profiler:profileFile	Ljava/lang/String;
    //   942: ifnull +11 -> 953
    //   945: aload 4
    //   947: getfield 1765	android/content/pm/InstrumentationInfo:handleProfiling	Z
    //   950: ifeq +427 -> 1377
    //   953: aload_1
    //   954: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   957: getfield 1373	android/content/pm/ApplicationInfo:flags	I
    //   960: ldc_w 1766
    //   963: iand
    //   964: ifeq +484 -> 1448
    //   967: invokestatic 1323	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   970: invokevirtual 1769	dalvik/system/VMRuntime:clearGrowthLimit	()V
    //   973: invokestatic 1773	android/os/StrictMode:allowThreadDiskWrites	()Landroid/os/StrictMode$ThreadPolicy;
    //   976: astore 4
    //   978: aload_1
    //   979: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   982: aload_1
    //   983: getfield 1776	android/app/ActivityThread$AppBindData:restrictedBackupMode	Z
    //   986: aconst_null
    //   987: invokevirtual 690	android/app/LoadedApk:makeApplication	(ZLandroid/app/Instrumentation;)Landroid/app/Application;
    //   990: astore 5
    //   992: aload_0
    //   993: aload 5
    //   995: putfield 692	android/app/ActivityThread:mInitialApplication	Landroid/app/Application;
    //   998: aload_1
    //   999: getfield 1776	android/app/ActivityThread$AppBindData:restrictedBackupMode	Z
    //   1002: ifne +37 -> 1039
    //   1005: aload_1
    //   1006: getfield 1780	android/app/ActivityThread$AppBindData:providers	Ljava/util/List;
    //   1009: invokestatic 1785	com/android/internal/util/ArrayUtils:isEmpty	(Ljava/util/Collection;)Z
    //   1012: ifne +27 -> 1039
    //   1015: aload_0
    //   1016: aload 5
    //   1018: aload_1
    //   1019: getfield 1780	android/app/ActivityThread$AppBindData:providers	Ljava/util/List;
    //   1022: invokespecial 1789	android/app/ActivityThread:installContentProviders	(Landroid/content/Context;Ljava/util/List;)V
    //   1025: aload_0
    //   1026: getfield 495	android/app/ActivityThread:mH	Landroid/app/ActivityThread$H;
    //   1029: sipush 132
    //   1032: ldc2_w 1790
    //   1035: invokevirtual 1795	android/app/ActivityThread$H:sendEmptyMessageDelayed	(IJ)Z
    //   1038: pop
    //   1039: aload_0
    //   1040: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   1043: aload_1
    //   1044: getfield 1798	android/app/ActivityThread$AppBindData:instrumentationArgs	Landroid/os/Bundle;
    //   1047: invokevirtual 1800	android/app/Instrumentation:onCreate	(Landroid/os/Bundle;)V
    //   1050: aload_0
    //   1051: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   1054: aload 5
    //   1056: invokevirtual 1804	android/app/Instrumentation:callApplicationOnCreate	(Landroid/app/Application;)V
    //   1059: aload 4
    //   1061: invokestatic 1808	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   1064: return
    //   1065: astore_1
    //   1066: aload 4
    //   1068: monitorexit
    //   1069: aload_1
    //   1070: athrow
    //   1071: aload_1
    //   1072: getfield 961	android/app/ActivityThread$AppBindData:appInfo	Landroid/content/pm/ApplicationInfo;
    //   1075: invokevirtual 1811	android/content/pm/ApplicationInfo:getOverrideDensity	()I
    //   1078: istore_2
    //   1079: iload_2
    //   1080: ifeq -719 -> 361
    //   1083: ldc -114
    //   1085: new 701	java/lang/StringBuilder
    //   1088: dup
    //   1089: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1092: ldc_w 1813
    //   1095: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1098: getstatic 1818	android/util/DisplayMetrics:DENSITY_DEVICE	I
    //   1101: invokevirtual 1821	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1104: ldc_w 1823
    //   1107: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: iload_2
    //   1111: invokevirtual 1821	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1114: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1117: invokestatic 1828	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1120: pop
    //   1121: aload_0
    //   1122: iconst_1
    //   1123: putfield 1556	android/app/ActivityThread:mDensityCompatMode	Z
    //   1126: iload_2
    //   1127: invokestatic 1559	android/graphics/Bitmap:setDefaultDensity	(I)V
    //   1130: goto -769 -> 361
    //   1133: iconst_0
    //   1134: istore_3
    //   1135: goto -735 -> 400
    //   1138: iconst_0
    //   1139: istore_3
    //   1140: goto -667 -> 473
    //   1143: astore_1
    //   1144: aload_1
    //   1145: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   1148: athrow
    //   1149: astore_1
    //   1150: aload_1
    //   1151: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   1154: athrow
    //   1155: ldc -114
    //   1157: new 701	java/lang/StringBuilder
    //   1160: dup
    //   1161: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1164: ldc_w 1613
    //   1167: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1170: aload_1
    //   1171: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   1174: invokevirtual 1616	android/app/LoadedApk:getPackageName	()Ljava/lang/String;
    //   1177: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1180: ldc_w 1830
    //   1183: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1186: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1189: invokestatic 1624	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1192: pop
    //   1193: goto -623 -> 570
    //   1196: iconst_0
    //   1197: istore_3
    //   1198: goto -614 -> 584
    //   1201: astore_1
    //   1202: ldc2_w 1645
    //   1205: invokestatic 1674	android/os/Trace:traceEnd	(J)V
    //   1208: aload_1
    //   1209: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   1212: athrow
    //   1213: astore 4
    //   1215: new 699	java/lang/RuntimeException
    //   1218: dup
    //   1219: new 701	java/lang/StringBuilder
    //   1222: dup
    //   1223: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1226: ldc_w 1832
    //   1229: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1232: aload_1
    //   1233: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   1236: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1239: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1242: invokespecial 1834	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   1245: athrow
    //   1246: aconst_null
    //   1247: astore 4
    //   1249: goto -499 -> 750
    //   1252: aload 5
    //   1254: invokevirtual 1838	android/app/ContextImpl:getCacheDir	()Ljava/io/File;
    //   1257: astore 6
    //   1259: aload 6
    //   1261: ifnull +43 -> 1304
    //   1264: ldc_w 1840
    //   1267: aload 6
    //   1269: invokevirtual 1845	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1272: invokestatic 1851	java/lang/System:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1275: pop
    //   1276: aload 5
    //   1278: invokevirtual 1855	android/app/ContextImpl:createDeviceProtectedStorageContext	()Landroid/content/Context;
    //   1281: invokevirtual 1860	android/content/Context:getCodeCacheDir	()Ljava/io/File;
    //   1284: astore 6
    //   1286: aload 6
    //   1288: ifnull +28 -> 1316
    //   1291: aload_0
    //   1292: aload_1
    //   1293: getfield 1554	android/app/ActivityThread$AppBindData:info	Landroid/app/LoadedApk;
    //   1296: aload 6
    //   1298: invokespecial 1864	android/app/ActivityThread:setupGraphicsSupport	(Landroid/app/LoadedApk;Ljava/io/File;)V
    //   1301: goto -505 -> 796
    //   1304: ldc -114
    //   1306: ldc_w 1866
    //   1309: invokestatic 1869	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1312: pop
    //   1313: goto -37 -> 1276
    //   1316: ldc -114
    //   1318: ldc_w 1871
    //   1321: invokestatic 1874	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1324: pop
    //   1325: goto -529 -> 796
    //   1328: astore 4
    //   1330: new 699	java/lang/RuntimeException
    //   1333: dup
    //   1334: new 701	java/lang/StringBuilder
    //   1337: dup
    //   1338: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1341: ldc_w 1876
    //   1344: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1347: aload_1
    //   1348: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   1351: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1354: ldc_w 941
    //   1357: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1360: aload 4
    //   1362: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   1365: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1368: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1371: aload 4
    //   1373: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1376: athrow
    //   1377: aload_0
    //   1378: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   1381: getfield 1468	android/app/ActivityThread$Profiler:profileFd	Landroid/os/ParcelFileDescriptor;
    //   1384: ifnonnull -431 -> 953
    //   1387: aload_0
    //   1388: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   1391: iconst_1
    //   1392: putfield 1879	android/app/ActivityThread$Profiler:handlingProfiling	Z
    //   1395: new 1842	java/io/File
    //   1398: dup
    //   1399: aload_0
    //   1400: getfield 1453	android/app/ActivityThread:mProfiler	Landroid/app/ActivityThread$Profiler;
    //   1403: getfield 1463	android/app/ActivityThread$Profiler:profileFile	Ljava/lang/String;
    //   1406: invokespecial 1880	java/io/File:<init>	(Ljava/lang/String;)V
    //   1409: astore 4
    //   1411: aload 4
    //   1413: invokevirtual 1883	java/io/File:getParentFile	()Ljava/io/File;
    //   1416: invokevirtual 1886	java/io/File:mkdirs	()Z
    //   1419: pop
    //   1420: aload 4
    //   1422: invokevirtual 1887	java/io/File:toString	()Ljava/lang/String;
    //   1425: ldc_w 1888
    //   1428: invokestatic 1891	android/os/Debug:startMethodTracing	(Ljava/lang/String;I)V
    //   1431: goto -478 -> 953
    //   1434: aload_0
    //   1435: new 667	android/app/Instrumentation
    //   1438: dup
    //   1439: invokespecial 668	android/app/Instrumentation:<init>	()V
    //   1442: putfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   1445: goto -492 -> 953
    //   1448: invokestatic 1323	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   1451: invokevirtual 1894	dalvik/system/VMRuntime:clampGrowthLimit	()V
    //   1454: goto -481 -> 973
    //   1457: astore 5
    //   1459: new 699	java/lang/RuntimeException
    //   1462: dup
    //   1463: new 701	java/lang/StringBuilder
    //   1466: dup
    //   1467: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1470: ldc_w 1896
    //   1473: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1476: aload_1
    //   1477: getfield 1678	android/app/ActivityThread$AppBindData:instrumentationName	Landroid/content/ComponentName;
    //   1480: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1483: ldc_w 941
    //   1486: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1489: aload 5
    //   1491: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   1494: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1497: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1500: aload 5
    //   1502: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1505: athrow
    //   1506: astore_1
    //   1507: aload 4
    //   1509: invokestatic 1808	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   1512: aload_1
    //   1513: athrow
    //   1514: astore_1
    //   1515: aload_0
    //   1516: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   1519: aload 5
    //   1521: aload_1
    //   1522: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   1525: ifne -466 -> 1059
    //   1528: new 699	java/lang/RuntimeException
    //   1531: dup
    //   1532: new 701	java/lang/StringBuilder
    //   1535: dup
    //   1536: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   1539: ldc_w 1898
    //   1542: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1545: aload 5
    //   1547: invokevirtual 1899	android/app/Application:getClass	()Ljava/lang/Class;
    //   1550: invokevirtual 800	java/lang/Class:getName	()Ljava/lang/String;
    //   1553: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1556: ldc_w 941
    //   1559: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1562: aload_1
    //   1563: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   1566: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1569: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1572: aload_1
    //   1573: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1576: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1577	0	this	ActivityThread
    //   0	1577	1	paramAppBindData	AppBindData
    //   1078	49	2	i	int
    //   399	799	3	bool	boolean
    //   276	791	4	localObject1	Object
    //   1213	1	4	localNameNotFoundException	PackageManager.NameNotFoundException
    //   1247	1	4	localObject2	Object
    //   1328	44	4	localException1	Exception
    //   1409	99	4	localFile	File
    //   758	519	5	localObject3	Object
    //   1457	89	5	localException2	Exception
    //   828	469	6	localObject4	Object
    //   911	12	7	localComponentName	ComponentName
    // Exception table:
    //   from	to	target	type
    //   281	317	1065	finally
    //   543	555	1143	android/os/RemoteException
    //   558	570	1149	android/os/RemoteException
    //   631	642	1201	android/os/RemoteException
    //   655	676	1213	android/content/pm/PackageManager$NameNotFoundException
    //   869	894	1328	java/lang/Exception
    //   1039	1050	1457	java/lang/Exception
    //   978	1039	1506	finally
    //   1039	1050	1506	finally
    //   1050	1059	1506	finally
    //   1459	1506	1506	finally
    //   1515	1577	1506	finally
    //   1050	1059	1514	java/lang/Exception
  }
  
  /* Error */
  private void handleBindService(BindServiceData paramBindServiceData)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 511	android/app/ActivityThread:mServices	Landroid/util/ArrayMap;
    //   4: aload_1
    //   5: getfield 1900	android/app/ActivityThread$BindServiceData:token	Landroid/os/IBinder;
    //   8: invokevirtual 1370	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   11: checkcast 1902	android/app/Service
    //   14: astore_2
    //   15: aload_2
    //   16: ifnull +159 -> 175
    //   19: aload_1
    //   20: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   23: aload_2
    //   24: invokevirtual 1904	android/app/Service:getClassLoader	()Ljava/lang/ClassLoader;
    //   27: invokevirtual 1019	android/content/Intent:setExtrasClassLoader	(Ljava/lang/ClassLoader;)V
    //   30: aload_1
    //   31: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   34: invokevirtual 1020	android/content/Intent:prepareToEnterProcess	()V
    //   37: aload_1
    //   38: getfield 1907	android/app/ActivityThread$BindServiceData:rebind	Z
    //   41: ifne +34 -> 75
    //   44: aload_2
    //   45: aload_1
    //   46: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   49: invokevirtual 1911	android/app/Service:onBind	(Landroid/content/Intent;)Landroid/os/IBinder;
    //   52: astore_3
    //   53: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   56: aload_1
    //   57: getfield 1900	android/app/ActivityThread$BindServiceData:token	Landroid/os/IBinder;
    //   60: aload_1
    //   61: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   64: aload_3
    //   65: invokeinterface 1915 4 0
    //   70: aload_0
    //   71: invokevirtual 1918	android/app/ActivityThread:ensureJitEnabled	()V
    //   74: return
    //   75: aload_2
    //   76: aload_1
    //   77: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   80: invokevirtual 1922	android/app/Service:onRebind	(Landroid/content/Intent;)V
    //   83: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   86: aload_1
    //   87: getfield 1900	android/app/ActivityThread$BindServiceData:token	Landroid/os/IBinder;
    //   90: iconst_0
    //   91: iconst_0
    //   92: iconst_0
    //   93: invokeinterface 1926 5 0
    //   98: goto -28 -> 70
    //   101: astore_3
    //   102: aload_3
    //   103: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   106: athrow
    //   107: astore_3
    //   108: aload_0
    //   109: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   112: aload_2
    //   113: aload_3
    //   114: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   117: ifne +58 -> 175
    //   120: new 699	java/lang/RuntimeException
    //   123: dup
    //   124: new 701	java/lang/StringBuilder
    //   127: dup
    //   128: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   131: ldc_w 1928
    //   134: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: aload_2
    //   138: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   141: ldc_w 1930
    //   144: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: aload_1
    //   148: getfield 1903	android/app/ActivityThread$BindServiceData:intent	Landroid/content/Intent;
    //   151: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   154: ldc_w 941
    //   157: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload_3
    //   161: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   164: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   170: aload_3
    //   171: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   174: athrow
    //   175: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	ActivityThread
    //   0	176	1	paramBindServiceData	BindServiceData
    //   14	124	2	localService	Service
    //   52	13	3	localIBinder	IBinder
    //   101	2	3	localRemoteException	RemoteException
    //   107	64	3	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   37	70	101	android/os/RemoteException
    //   70	74	101	android/os/RemoteException
    //   75	98	101	android/os/RemoteException
    //   19	37	107	java/lang/Exception
    //   37	70	107	java/lang/Exception
    //   70	74	107	java/lang/Exception
    //   75	98	107	java/lang/Exception
    //   102	107	107	java/lang/Exception
  }
  
  private void handleCreateBackupAgent(CreateBackupAgentData paramCreateBackupAgentData)
  {
    try
    {
      if (getPackageManager().getPackageInfo(paramCreateBackupAgentData.appInfo.packageName, 0, UserHandle.myUserId()).applicationInfo.uid != Process.myUid())
      {
        Slog.w("ActivityThread", "Asked to instantiate non-matching package " + paramCreateBackupAgentData.appInfo.packageName);
        return;
      }
    }
    catch (RemoteException paramCreateBackupAgentData)
    {
      throw paramCreateBackupAgentData.rethrowFromSystemServer();
    }
    unscheduleGcIdler();
    Object localObject4 = getPackageInfoNoCheck(paramCreateBackupAgentData.appInfo, paramCreateBackupAgentData.compatInfo);
    String str = ((LoadedApk)localObject4).mPackageName;
    if (str == null)
    {
      Slog.d("ActivityThread", "Asked to create backup agent for nonexistent package");
      return;
    }
    Object localObject1 = paramCreateBackupAgentData.appInfo.backupAgentName;
    localObject3 = localObject1;
    if (localObject1 == null) {
      if (paramCreateBackupAgentData.backupMode != 1)
      {
        localObject3 = localObject1;
        if (paramCreateBackupAgentData.backupMode != 3) {}
      }
      else
      {
        localObject3 = "android.app.backup.FullBackupAgent";
      }
    }
    Object localObject2 = null;
    do
    {
      do
      {
        for (;;)
        {
          try
          {
            localObject1 = (BackupAgent)this.mBackupAgents.get(str);
            if (localObject1 != null) {
              localObject2 = ((BackupAgent)localObject1).onBind();
            }
          }
          catch (Exception paramCreateBackupAgentData)
          {
            throw new RuntimeException("Unable to create BackupAgent " + (String)localObject3 + ": " + paramCreateBackupAgentData.toString(), paramCreateBackupAgentData);
          }
          try
          {
            ActivityManagerNative.getDefault().backupAgentCreated(str, (IBinder)localObject2);
            return;
          }
          catch (RemoteException paramCreateBackupAgentData)
          {
            throw paramCreateBackupAgentData.rethrowFromSystemServer();
          }
          localObject1 = localObject2;
          try
          {
            BackupAgent localBackupAgent = (BackupAgent)((LoadedApk)localObject4).getClassLoader().loadClass((String)localObject3).newInstance();
            localObject1 = localObject2;
            localObject4 = ContextImpl.createAppContext(this, (LoadedApk)localObject4);
            localObject1 = localObject2;
            ((ContextImpl)localObject4).setOuterContext(localBackupAgent);
            localObject1 = localObject2;
            localBackupAgent.attach((Context)localObject4);
            localObject1 = localObject2;
            localBackupAgent.onCreate();
            localObject1 = localObject2;
            localObject2 = localBackupAgent.onBind();
            localObject1 = localObject2;
            this.mBackupAgents.put(str, localBackupAgent);
          }
          catch (Exception localException)
          {
            Slog.e("ActivityThread", "Agent threw during creation: " + localException);
            localObject2 = localObject1;
          }
        }
      } while (paramCreateBackupAgentData.backupMode == 2);
      localObject2 = localObject1;
    } while (paramCreateBackupAgentData.backupMode == 3);
    throw localException;
  }
  
  /* Error */
  private void handleCreateService(CreateServiceData paramCreateServiceData)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1949	android/app/ActivityThread:unscheduleGcIdler	()V
    //   4: aload_0
    //   5: aload_1
    //   6: getfield 1980	android/app/ActivityThread$CreateServiceData:info	Landroid/content/pm/ServiceInfo;
    //   9: getfield 1983	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   12: aload_1
    //   13: getfield 1984	android/app/ActivityThread$CreateServiceData:compatInfo	Landroid/content/res/CompatibilityInfo;
    //   16: invokevirtual 1551	android/app/ActivityThread:getPackageInfoNoCheck	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;)Landroid/app/LoadedApk;
    //   19: astore 4
    //   21: aconst_null
    //   22: astore_2
    //   23: aload 4
    //   25: invokevirtual 1379	android/app/LoadedApk:getClassLoader	()Ljava/lang/ClassLoader;
    //   28: aload_1
    //   29: getfield 1980	android/app/ActivityThread$CreateServiceData:info	Landroid/content/pm/ServiceInfo;
    //   32: getfield 1985	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   35: invokevirtual 1741	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   38: invokevirtual 1744	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   41: checkcast 1902	android/app/Service
    //   44: astore_3
    //   45: aload_3
    //   46: astore_2
    //   47: aload_0
    //   48: aload 4
    //   50: invokestatic 684	android/app/ContextImpl:createAppContext	(Landroid/app/ActivityThread;Landroid/app/LoadedApk;)Landroid/app/ContextImpl;
    //   53: astore_3
    //   54: aload_3
    //   55: aload_2
    //   56: invokevirtual 830	android/app/ContextImpl:setOuterContext	(Landroid/content/Context;)V
    //   59: aload 4
    //   61: iconst_0
    //   62: aload_0
    //   63: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   66: invokevirtual 690	android/app/LoadedApk:makeApplication	(ZLandroid/app/Instrumentation;)Landroid/app/Application;
    //   69: astore 4
    //   71: aload_2
    //   72: aload_3
    //   73: aload_0
    //   74: aload_1
    //   75: getfield 1980	android/app/ActivityThread$CreateServiceData:info	Landroid/content/pm/ServiceInfo;
    //   78: getfield 1985	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   81: aload_1
    //   82: getfield 1986	android/app/ActivityThread$CreateServiceData:token	Landroid/os/IBinder;
    //   85: aload 4
    //   87: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   90: invokevirtual 1989	android/app/Service:attach	(Landroid/content/Context;Landroid/app/ActivityThread;Ljava/lang/String;Landroid/os/IBinder;Landroid/app/Application;Ljava/lang/Object;)V
    //   93: aload_2
    //   94: invokevirtual 1990	android/app/Service:onCreate	()V
    //   97: aload_0
    //   98: getfield 511	android/app/ActivityThread:mServices	Landroid/util/ArrayMap;
    //   101: aload_1
    //   102: getfield 1986	android/app/ActivityThread$CreateServiceData:token	Landroid/os/IBinder;
    //   105: aload_2
    //   106: invokevirtual 1390	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   109: pop
    //   110: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   113: aload_1
    //   114: getfield 1986	android/app/ActivityThread$CreateServiceData:token	Landroid/os/IBinder;
    //   117: iconst_0
    //   118: iconst_0
    //   119: iconst_0
    //   120: invokeinterface 1926 5 0
    //   125: return
    //   126: astore_3
    //   127: aload_0
    //   128: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   131: aconst_null
    //   132: aload_3
    //   133: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   136: ifne -89 -> 47
    //   139: new 699	java/lang/RuntimeException
    //   142: dup
    //   143: new 701	java/lang/StringBuilder
    //   146: dup
    //   147: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   150: ldc_w 1992
    //   153: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: aload_1
    //   157: getfield 1980	android/app/ActivityThread$CreateServiceData:info	Landroid/content/pm/ServiceInfo;
    //   160: getfield 1985	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   163: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: ldc_w 941
    //   169: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: aload_3
    //   173: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   176: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: aload_3
    //   183: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   186: athrow
    //   187: astore_3
    //   188: aload_3
    //   189: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   192: athrow
    //   193: astore_3
    //   194: aload_0
    //   195: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   198: aload_2
    //   199: aload_3
    //   200: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   203: ifne -78 -> 125
    //   206: new 699	java/lang/RuntimeException
    //   209: dup
    //   210: new 701	java/lang/StringBuilder
    //   213: dup
    //   214: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   217: ldc_w 1994
    //   220: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: aload_1
    //   224: getfield 1980	android/app/ActivityThread$CreateServiceData:info	Landroid/content/pm/ServiceInfo;
    //   227: getfield 1985	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   230: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: ldc_w 941
    //   236: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: aload_3
    //   240: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   243: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   249: aload_3
    //   250: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   253: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	254	0	this	ActivityThread
    //   0	254	1	paramCreateServiceData	CreateServiceData
    //   22	177	2	localObject1	Object
    //   44	29	3	localObject2	Object
    //   126	57	3	localException1	Exception
    //   187	2	3	localRemoteException	RemoteException
    //   193	57	3	localException2	Exception
    //   19	67	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   23	45	126	java/lang/Exception
    //   110	125	187	android/os/RemoteException
    //   47	110	193	java/lang/Exception
    //   110	125	193	java/lang/Exception
    //   188	193	193	java/lang/Exception
  }
  
  private void handleDestroyActivity(IBinder paramIBinder, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    ActivityClientRecord localActivityClientRecord = performDestroyActivity(paramIBinder, paramBoolean1, paramInt, paramBoolean2);
    Object localObject;
    View localView;
    if (localActivityClientRecord != null)
    {
      cleanUpPendingRemoveWindows(localActivityClientRecord, paramBoolean1);
      localObject = localActivityClientRecord.activity.getWindowManager();
      localView = localActivityClientRecord.activity.mDecor;
      if (localView != null)
      {
        if (localActivityClientRecord.activity.mVisibleFromServer) {
          this.mNumVisibleActivities -= 1;
        }
        IBinder localIBinder = localView.getWindowToken();
        if (localActivityClientRecord.activity.mWindowAdded)
        {
          if (!localActivityClientRecord.mPreserveWindow) {
            break label251;
          }
          localActivityClientRecord.mPendingRemoveWindow = localActivityClientRecord.window;
          localActivityClientRecord.mPendingRemoveWindowManager = ((WindowManager)localObject);
          localActivityClientRecord.window.clearContentView();
        }
        if ((localIBinder == null) || (localActivityClientRecord.mPendingRemoveWindow != null)) {
          break label263;
        }
        WindowManagerGlobal.getInstance().closeAll(localIBinder, localActivityClientRecord.activity.getClass().getName(), "Activity");
      }
    }
    for (;;)
    {
      localActivityClientRecord.activity.mDecor = null;
      if (localActivityClientRecord.mPendingRemoveWindow == null) {
        WindowManagerGlobal.getInstance().closeAll(paramIBinder, localActivityClientRecord.activity.getClass().getName(), "Activity");
      }
      localObject = localActivityClientRecord.activity.getBaseContext();
      if ((localObject instanceof ContextImpl)) {
        ((ContextImpl)localObject).scheduleFinalCleanup(localActivityClientRecord.activity.getClass().getName(), "Activity");
      }
      if (paramBoolean1) {}
      try
      {
        ActivityManagerNative.getDefault().activityDestroyed(paramIBinder);
        this.mSomeActivitiesChanged = true;
        return;
      }
      catch (RemoteException paramIBinder)
      {
        label251:
        throw paramIBinder.rethrowFromSystemServer();
      }
      ((WindowManager)localObject).removeViewImmediate(localView);
      break;
      label263:
      if (localActivityClientRecord.mPendingRemoveWindow != null) {
        WindowManagerGlobal.getInstance().closeAllExceptView(paramIBinder, localView, localActivityClientRecord.activity.getClass().getName(), "Activity");
      }
    }
  }
  
  private void handleDestroyBackupAgent(CreateBackupAgentData paramCreateBackupAgentData)
  {
    String str = getPackageInfoNoCheck(paramCreateBackupAgentData.appInfo, paramCreateBackupAgentData.compatInfo).mPackageName;
    BackupAgent localBackupAgent = (BackupAgent)this.mBackupAgents.get(str);
    if (localBackupAgent != null) {
      try
      {
        localBackupAgent.onDestroy();
        this.mBackupAgents.remove(str);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Slog.w("ActivityThread", "Exception thrown in onDestroy by backup agent of " + paramCreateBackupAgentData.appInfo);
          localException.printStackTrace();
        }
      }
    }
    Slog.w("ActivityThread", "Attempt to destroy unknown backup agent " + paramCreateBackupAgentData);
  }
  
  private void handleDumpActivity(DumpComponentInfo paramDumpComponentInfo)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskWrites();
    try
    {
      ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramDumpComponentInfo.token);
      if ((localActivityClientRecord != null) && (localActivityClientRecord.activity != null))
      {
        FastPrintWriter localFastPrintWriter = new FastPrintWriter(new FileOutputStream(paramDumpComponentInfo.fd.getFileDescriptor()));
        localActivityClientRecord.activity.dump(paramDumpComponentInfo.prefix, paramDumpComponentInfo.fd.getFileDescriptor(), localFastPrintWriter, paramDumpComponentInfo.args);
        localFastPrintWriter.flush();
      }
      return;
    }
    finally
    {
      IoUtils.closeQuietly(paramDumpComponentInfo.fd);
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
  
  static final void handleDumpHeap(boolean paramBoolean, DumpHeapData paramDumpHeapData)
  {
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        Debug.dumpHprofData(paramDumpHeapData.path, paramDumpHeapData.fd.getFileDescriptor());
      }
      catch (IOException localIOException2)
      {
        localIOException2 = localIOException2;
        Slog.w("ActivityThread", "Managed heap dump failed on path " + paramDumpHeapData.path + " -- can the process access this path?");
        try
        {
          paramDumpHeapData.fd.close();
        }
        catch (IOException localIOException3)
        {
          Slog.w("ActivityThread", "Failure closing profile fd", localIOException3);
        }
        continue;
      }
      finally {}
      try
      {
        paramDumpHeapData.fd.close();
      }
      catch (IOException localIOException1)
      {
        try
        {
          ActivityManagerNative.getDefault().dumpHeapFinished(paramDumpHeapData.path);
          return;
        }
        catch (RemoteException paramDumpHeapData)
        {
          throw paramDumpHeapData.rethrowFromSystemServer();
        }
        localIOException1 = localIOException1;
        Slog.w("ActivityThread", "Failure closing profile fd", localIOException1);
        continue;
      }
      try
      {
        paramDumpHeapData.fd.close();
        throw ((Throwable)localObject);
      }
      catch (IOException paramDumpHeapData)
      {
        for (;;)
        {
          Slog.w("ActivityThread", "Failure closing profile fd", paramDumpHeapData);
        }
      }
      Debug.dumpNativeHeap(paramDumpHeapData.fd.getFileDescriptor());
    }
  }
  
  private void handleDumpProvider(DumpComponentInfo paramDumpComponentInfo)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskWrites();
    try
    {
      ProviderClientRecord localProviderClientRecord = (ProviderClientRecord)this.mLocalProviders.get(paramDumpComponentInfo.token);
      if ((localProviderClientRecord != null) && (localProviderClientRecord.mLocalProvider != null))
      {
        FastPrintWriter localFastPrintWriter = new FastPrintWriter(new FileOutputStream(paramDumpComponentInfo.fd.getFileDescriptor()));
        localProviderClientRecord.mLocalProvider.dump(paramDumpComponentInfo.fd.getFileDescriptor(), localFastPrintWriter, paramDumpComponentInfo.args);
        localFastPrintWriter.flush();
      }
      return;
    }
    finally
    {
      IoUtils.closeQuietly(paramDumpComponentInfo.fd);
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
  
  private void handleDumpService(DumpComponentInfo paramDumpComponentInfo)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskWrites();
    try
    {
      Service localService = (Service)this.mServices.get(paramDumpComponentInfo.token);
      if (localService != null)
      {
        FastPrintWriter localFastPrintWriter = new FastPrintWriter(new FileOutputStream(paramDumpComponentInfo.fd.getFileDescriptor()));
        localService.dump(paramDumpComponentInfo.fd.getFileDescriptor(), localFastPrintWriter, paramDumpComponentInfo.args);
        localFastPrintWriter.flush();
      }
      return;
    }
    finally
    {
      IoUtils.closeQuietly(paramDumpComponentInfo.fd);
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
  
  private void handleEnterAnimationComplete(IBinder paramIBinder)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.dispatchEnterAnimationComplete();
    }
  }
  
  private void handleLaunchActivity(ActivityClientRecord paramActivityClientRecord, Intent paramIntent, String paramString)
  {
    boolean bool1 = true;
    unscheduleGcIdler();
    this.mSomeActivitiesChanged = true;
    if (paramActivityClientRecord.profilerInfo != null)
    {
      this.mProfiler.setProfiler(paramActivityClientRecord.profilerInfo);
      this.mProfiler.startProfiling();
    }
    handleConfigurationChanged(null, null);
    WindowManagerGlobal.initialize();
    if (performLaunchActivity(paramActivityClientRecord, paramIntent) != null)
    {
      paramActivityClientRecord.createdConfig = new Configuration(this.mConfiguration);
      reportSizeConfigurations(paramActivityClientRecord);
      paramIntent = paramActivityClientRecord.state;
      IBinder localIBinder = paramActivityClientRecord.token;
      boolean bool2 = paramActivityClientRecord.isForward;
      if ((paramActivityClientRecord.activity.mFinished) || (paramActivityClientRecord.startsNotResumed)) {
        bool1 = false;
      }
      handleResumeActivity(localIBinder, false, bool2, bool1, paramActivityClientRecord.lastProcessedSeq, paramString);
      if ((!paramActivityClientRecord.activity.mFinished) && (paramActivityClientRecord.startsNotResumed))
      {
        performPauseActivityIfNeeded(paramActivityClientRecord, paramString);
        if (paramActivityClientRecord.isPreHoneycomb()) {
          paramActivityClientRecord.state = paramIntent;
        }
      }
      return;
    }
    try
    {
      ActivityManagerNative.getDefault().finishActivity(paramActivityClientRecord.token, 0, null, 0);
      return;
    }
    catch (RemoteException paramActivityClientRecord)
    {
      throw paramActivityClientRecord.rethrowFromSystemServer();
    }
  }
  
  private void handleLocalVoiceInteractionStarted(IBinder paramIBinder, IVoiceInteractor paramIVoiceInteractor)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null)
    {
      paramIBinder.voiceInteractor = paramIVoiceInteractor;
      paramIBinder.activity.setVoiceInteractor(paramIVoiceInteractor);
      if (paramIVoiceInteractor == null) {
        paramIBinder.activity.onLocalVoiceInteractionStopped();
      }
    }
    else
    {
      return;
    }
    paramIBinder.activity.onLocalVoiceInteractionStarted();
  }
  
  private void handleMultiWindowModeChanged(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.dispatchMultiWindowModeChanged(paramBoolean);
    }
  }
  
  private void handleNewIntent(NewIntentData paramNewIntentData)
  {
    performNewIntents(paramNewIntentData.token, paramNewIntentData.intents, paramNewIntentData.andPause);
  }
  
  private void handlePauseActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, boolean paramBoolean3, int paramInt2)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (!checkAndUpdateLifecycleSeq(paramInt2, localActivityClientRecord, "pauseActivity")) {
      return;
    }
    if (localActivityClientRecord != null)
    {
      if (paramBoolean2) {
        performUserLeavingActivity(localActivityClientRecord);
      }
      Activity localActivity = localActivityClientRecord.activity;
      localActivity.mConfigChangeFlags |= paramInt1;
      performPauseActivity(paramIBinder, paramBoolean1, localActivityClientRecord.isPreHoneycomb(), "handlePauseActivity");
      if (localActivityClientRecord.isPreHoneycomb()) {
        QueuedWork.waitToFinish();
      }
      if (paramBoolean3) {}
    }
    try
    {
      ActivityManagerNative.getDefault().activityPaused(paramIBinder);
      this.mSomeActivitiesChanged = true;
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  private void handlePictureInPictureModeChanged(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.dispatchPictureInPictureModeChanged(paramBoolean);
    }
  }
  
  private void handleReceiver(ReceiverData paramReceiverData)
  {
    unscheduleGcIdler();
    String str = paramReceiverData.intent.getComponent().getClassName();
    Object localObject2 = getPackageInfoNoCheck(paramReceiverData.info.applicationInfo, paramReceiverData.compatInfo);
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    label396:
    for (;;)
    {
      try
      {
        Object localObject1 = ((LoadedApk)localObject2).getClassLoader();
        paramReceiverData.intent.setExtrasClassLoader((ClassLoader)localObject1);
        paramReceiverData.intent.prepareToEnterProcess();
        paramReceiverData.setExtrasClassLoader((ClassLoader)localObject1);
        localObject1 = (BroadcastReceiver)((ClassLoader)localObject1).loadClass(str).newInstance();
        long l;
        sCurrentBroadcastIntent.set(null);
      }
      catch (Exception localException1)
      {
        try
        {
          localObject2 = (ContextImpl)((LoadedApk)localObject2).makeApplication(false, this.mInstrumentation).getBaseContext();
          sCurrentBroadcastIntent.set(paramReceiverData.intent);
          ((BroadcastReceiver)localObject1).setPendingResult(paramReceiverData);
          l = SystemClock.uptimeMillis();
          if (DEBUG_ONEPLUS) {
            Log.i("ActivityThread", localObject1 + " onReceive " + paramReceiverData.intent.getAction() + " start");
          }
          ((BroadcastReceiver)localObject1).onReceive(((ContextImpl)localObject2).getReceiverRestrictedContext(), paramReceiverData.intent);
          if (DEBUG_ONEPLUS) {
            Log.i("ActivityThread", localObject1 + " onReceive " + paramReceiverData.intent.getAction() + " in " + (SystemClock.uptimeMillis() - l) + "ms");
          }
          sCurrentBroadcastIntent.set(null);
          if (((BroadcastReceiver)localObject1).getPendingResult() != null) {
            paramReceiverData.finish();
          }
          return;
        }
        catch (Exception localException2)
        {
          paramReceiverData.sendFinished(localIActivityManager);
          if (this.mInstrumentation.onException(localException1, localException2)) {
            break label396;
          }
          throw new RuntimeException("Unable to start receiver " + str + ": " + localException2.toString(), localException2);
        }
        finally
        {
          sCurrentBroadcastIntent.set(null);
        }
        localException1 = localException1;
        paramReceiverData.sendFinished(localIActivityManager);
        throw new RuntimeException("Unable to instantiate receiver " + str + ": " + localException1.toString(), localException1);
      }
    }
  }
  
  private void handleRelaunchActivity(ActivityClientRecord paramActivityClientRecord)
  {
    unscheduleGcIdler();
    this.mSomeActivitiesChanged = true;
    Object localObject1 = null;
    int k = 0;
    ActivityClientRecord localActivityClientRecord;
    Object localObject2;
    for (;;)
    {
      synchronized (this.mResourcesManager)
      {
        int j = this.mRelaunchingActivities.size();
        IBinder localIBinder = paramActivityClientRecord.token;
        localActivityClientRecord = null;
        int i = 0;
        if (i < j)
        {
          localObject2 = (ActivityClientRecord)this.mRelaunchingActivities.get(i);
          int i1 = j;
          int m = k;
          int n = i;
          paramActivityClientRecord = localActivityClientRecord;
          if (((ActivityClientRecord)localObject2).token == localIBinder)
          {
            paramActivityClientRecord = (ActivityClientRecord)localObject2;
            m = k | ((ActivityClientRecord)localObject2).pendingConfigChanges;
            this.mRelaunchingActivities.remove(i);
            n = i - 1;
            i1 = j - 1;
          }
          i = n + 1;
          j = i1;
          k = m;
          localActivityClientRecord = paramActivityClientRecord;
          continue;
        }
        if (localActivityClientRecord == null) {
          return;
        }
        paramActivityClientRecord = (ActivityClientRecord)localObject1;
        if (this.mPendingConfiguration != null)
        {
          paramActivityClientRecord = this.mPendingConfiguration;
          this.mPendingConfiguration = null;
        }
        if (localActivityClientRecord.lastProcessedSeq > localActivityClientRecord.relaunchSeq)
        {
          Slog.wtf("ActivityThread", "For some reason target: " + localActivityClientRecord + " has lower sequence: " + localActivityClientRecord.relaunchSeq + " than current sequence: " + localActivityClientRecord.lastProcessedSeq);
          localObject1 = paramActivityClientRecord;
          if (localActivityClientRecord.createdConfig != null) {
            if (this.mConfiguration != null)
            {
              localObject1 = paramActivityClientRecord;
              if (localActivityClientRecord.createdConfig.isOtherSeqNewer(this.mConfiguration))
              {
                localObject1 = paramActivityClientRecord;
                if (this.mConfiguration.diff(localActivityClientRecord.createdConfig) == 0) {}
              }
            }
            else if (paramActivityClientRecord != null)
            {
              localObject1 = paramActivityClientRecord;
              if (!localActivityClientRecord.createdConfig.isOtherSeqNewer(paramActivityClientRecord)) {}
            }
            else
            {
              localObject1 = localActivityClientRecord.createdConfig;
            }
          }
          if (localObject1 != null)
          {
            this.mCurDefaultDisplayDpi = ((Configuration)localObject1).densityDpi;
            updateDefaultDensity();
            handleConfigurationChanged((Configuration)localObject1, null);
          }
          paramActivityClientRecord = (ActivityClientRecord)this.mActivities.get(localActivityClientRecord.token);
          if (paramActivityClientRecord != null) {
            break;
          }
          if (localActivityClientRecord.onlyLocalRequest) {}
        }
      }
      localActivityClientRecord.lastProcessedSeq = localActivityClientRecord.relaunchSeq;
    }
    localObject1 = paramActivityClientRecord.activity;
    ((Activity)localObject1).mConfigChangeFlags |= k;
    paramActivityClientRecord.onlyLocalRequest = localActivityClientRecord.onlyLocalRequest;
    paramActivityClientRecord.mPreserveWindow = localActivityClientRecord.mPreserveWindow;
    paramActivityClientRecord.lastProcessedSeq = localActivityClientRecord.lastProcessedSeq;
    paramActivityClientRecord.relaunchSeq = localActivityClientRecord.relaunchSeq;
    localObject1 = paramActivityClientRecord.activity.mIntent;
    paramActivityClientRecord.activity.mChangingConfigurations = true;
    for (;;)
    {
      try
      {
        if ((paramActivityClientRecord.mPreserveWindow) || (paramActivityClientRecord.onlyLocalRequest))
        {
          localObject2 = WindowManagerGlobal.getWindowSession();
          ??? = paramActivityClientRecord.token;
          if (paramActivityClientRecord.onlyLocalRequest)
          {
            bool = false;
            ((IWindowSession)localObject2).prepareToReplaceWindows((IBinder)???, bool);
          }
        }
        else
        {
          if (!paramActivityClientRecord.paused) {
            performPauseActivity(paramActivityClientRecord.token, false, paramActivityClientRecord.isPreHoneycomb(), "handleRelaunchActivity");
          }
          if ((paramActivityClientRecord.state == null) && (!paramActivityClientRecord.stopped)) {
            break label717;
          }
          handleDestroyActivity(paramActivityClientRecord.token, false, k, true);
          paramActivityClientRecord.activity = null;
          paramActivityClientRecord.window = null;
          paramActivityClientRecord.hideForNow = false;
          paramActivityClientRecord.nextIdle = null;
          if (localActivityClientRecord.pendingResults != null)
          {
            if (paramActivityClientRecord.pendingResults != null) {
              break label732;
            }
            paramActivityClientRecord.pendingResults = localActivityClientRecord.pendingResults;
          }
          if (localActivityClientRecord.pendingIntents != null)
          {
            if (paramActivityClientRecord.pendingIntents != null) {
              break label750;
            }
            paramActivityClientRecord.pendingIntents = localActivityClientRecord.pendingIntents;
          }
          paramActivityClientRecord.startsNotResumed = localActivityClientRecord.startsNotResumed;
          paramActivityClientRecord.overrideConfig = localActivityClientRecord.overrideConfig;
          handleLaunchActivity(paramActivityClientRecord, (Intent)localObject1, "handleRelaunchActivity");
          if (localActivityClientRecord.onlyLocalRequest) {}
        }
      }
      catch (RemoteException paramActivityClientRecord)
      {
        boolean bool;
        throw paramActivityClientRecord.rethrowFromSystemServer();
      }
      try
      {
        ActivityManagerNative.getDefault().activityRelaunched(paramActivityClientRecord.token);
        if (paramActivityClientRecord.window != null) {
          paramActivityClientRecord.window.reportActivityRelaunched();
        }
        return;
      }
      catch (RemoteException paramActivityClientRecord)
      {
        throw paramActivityClientRecord.rethrowFromSystemServer();
      }
      bool = true;
      continue;
      label717:
      if (!paramActivityClientRecord.isPreHoneycomb())
      {
        callCallActivityOnSaveInstanceState(paramActivityClientRecord);
        continue;
        label732:
        paramActivityClientRecord.pendingResults.addAll(localActivityClientRecord.pendingResults);
        continue;
        label750:
        paramActivityClientRecord.pendingIntents.addAll(localActivityClientRecord.pendingIntents);
      }
    }
  }
  
  private void handleSendResult(ResultData paramResultData)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramResultData.token);
    if (localActivityClientRecord != null)
    {
      int i;
      if (localActivityClientRecord.paused) {
        i = 0;
      }
      for (;;)
      {
        if ((!localActivityClientRecord.activity.mFinished) && (localActivityClientRecord.activity.mDecor != null) && (localActivityClientRecord.hideForNow) && (i != 0)) {
          updateVisibility(localActivityClientRecord, true);
        }
        if (i != 0) {
          try
          {
            localActivityClientRecord.activity.mCalled = false;
            localActivityClientRecord.activity.mTemporaryPause = true;
            this.mInstrumentation.callActivityOnPause(localActivityClientRecord.activity);
            if (!localActivityClientRecord.activity.mCalled) {
              throw new SuperNotCalledException("Activity " + localActivityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPause()");
            }
          }
          catch (SuperNotCalledException paramResultData)
          {
            throw paramResultData;
            i = 1;
          }
          catch (Exception localException)
          {
            if (!this.mInstrumentation.onException(localActivityClientRecord.activity, localException)) {
              throw new RuntimeException("Unable to pause activity " + localActivityClientRecord.intent.getComponent().toShortString() + ": " + localException.toString(), localException);
            }
          }
        }
      }
      deliverResults(localActivityClientRecord, paramResultData.results);
      if (i != 0)
      {
        localActivityClientRecord.activity.performResume();
        localActivityClientRecord.activity.mTemporaryPause = false;
      }
    }
  }
  
  private void handleServiceArgs(ServiceArgsData paramServiceArgsData)
  {
    Service localService = (Service)this.mServices.get(paramServiceArgsData.token);
    if (localService != null) {
      try
      {
        if (paramServiceArgsData.args != null)
        {
          paramServiceArgsData.args.setExtrasClassLoader(localService.getClassLoader());
          paramServiceArgsData.args.prepareToEnterProcess();
        }
        if (!paramServiceArgsData.taskRemoved) {}
        for (int i = localService.onStartCommand(paramServiceArgsData.args, paramServiceArgsData.flags, paramServiceArgsData.startId);; i = 1000)
        {
          QueuedWork.waitToFinish();
          try
          {
            ActivityManagerNative.getDefault().serviceDoneExecuting(paramServiceArgsData.token, 1, paramServiceArgsData.startId, i);
            ensureJitEnabled();
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw localRemoteException.rethrowFromSystemServer();
          }
          localService.onTaskRemoved(paramServiceArgsData.args);
        }
        return;
      }
      catch (Exception localException)
      {
        if (!this.mInstrumentation.onException(localService, localException)) {
          throw new RuntimeException("Unable to start service " + localService + " with " + paramServiceArgsData.args + ": " + localException.toString(), localException);
        }
      }
    }
  }
  
  private void handleSetCoreSettings(Bundle paramBundle)
  {
    synchronized (this.mResourcesManager)
    {
      this.mCoreSettings = paramBundle;
      onCoreSettingsChange();
      return;
    }
  }
  
  private void handleSleeping(IBinder paramIBinder, boolean paramBoolean)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (localActivityClientRecord == null)
    {
      Log.w("ActivityThread", "handleSleeping: no activity for token " + paramIBinder);
      return;
    }
    if (paramBoolean) {
      if ((localActivityClientRecord.stopped) || (localActivityClientRecord.isPreHoneycomb())) {
        if (!localActivityClientRecord.isPreHoneycomb()) {
          QueuedWork.waitToFinish();
        }
      }
    }
    while ((!localActivityClientRecord.stopped) || (!localActivityClientRecord.activity.mVisibleFromServer)) {
      for (;;)
      {
        try
        {
          ActivityManagerNative.getDefault().activitySlept(localActivityClientRecord.token);
          return;
        }
        catch (RemoteException paramIBinder)
        {
          throw paramIBinder.rethrowFromSystemServer();
        }
        if ((!localActivityClientRecord.activity.mFinished) && (localActivityClientRecord.state == null)) {
          callCallActivityOnSaveInstanceState(localActivityClientRecord);
        }
        try
        {
          localActivityClientRecord.activity.performStop(false);
          localActivityClientRecord.stopped = true;
          EventLog.writeEvent(30049, new Object[] { Integer.valueOf(UserHandle.myUserId()), localActivityClientRecord.activity.getComponentName().getClassName(), "sleeping" });
        }
        catch (Exception paramIBinder)
        {
          if (!this.mInstrumentation.onException(localActivityClientRecord.activity, paramIBinder)) {
            throw new RuntimeException("Unable to stop activity " + localActivityClientRecord.intent.getComponent().toShortString() + ": " + paramIBinder.toString(), paramIBinder);
          }
        }
      }
    }
    localActivityClientRecord.activity.performRestart();
    localActivityClientRecord.stopped = false;
  }
  
  private void handleStartBinderTracking() {}
  
  private void handleStopActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if ((localActivityClientRecord == null) || (localActivityClientRecord.activity == null))
    {
      Slog.e("ActivityThread", "handleStopActivity # cannot get r from token " + paramIBinder);
      return;
    }
    if (!checkAndUpdateLifecycleSeq(paramInt2, localActivityClientRecord, "stopActivity")) {
      return;
    }
    paramIBinder = localActivityClientRecord.activity;
    paramIBinder.mConfigChangeFlags |= paramInt1;
    paramIBinder = new StopInfo(null);
    performStopActivityInner(localActivityClientRecord, paramIBinder, paramBoolean, true, "handleStopActivity");
    updateVisibility(localActivityClientRecord, paramBoolean);
    if (!localActivityClientRecord.isPreHoneycomb()) {
      QueuedWork.waitToFinish();
    }
    paramIBinder.activity = localActivityClientRecord;
    paramIBinder.state = localActivityClientRecord.state;
    paramIBinder.persistentState = localActivityClientRecord.persistentState;
    this.mH.post(paramIBinder);
    this.mSomeActivitiesChanged = true;
  }
  
  private void handleStopBinderTrackingAndDump(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    try
    {
      Binder.disableTracing();
      Binder.getTransactionTracker().writeTracesToFile(paramParcelFileDescriptor);
      return;
    }
    finally
    {
      IoUtils.closeQuietly(paramParcelFileDescriptor);
      Binder.getTransactionTracker().clearTraces();
    }
  }
  
  private void handleStopService(IBinder paramIBinder)
  {
    Service localService = (Service)this.mServices.remove(paramIBinder);
    if (localService != null) {
      try
      {
        localService.onDestroy();
        Context localContext = localService.getBaseContext();
        if ((localContext instanceof ContextImpl))
        {
          String str = localService.getClassName();
          ((ContextImpl)localContext).scheduleFinalCleanup(str, "Service");
        }
        QueuedWork.waitToFinish();
        try
        {
          ActivityManagerNative.getDefault().serviceDoneExecuting(paramIBinder, 2, 0, 0);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
        Slog.i("ActivityThread", "handleStopService: token=" + paramIBinder + " not found.");
      }
      catch (Exception localException)
      {
        if (!this.mInstrumentation.onException(localService, localException)) {
          throw new RuntimeException("Unable to stop service " + localService + ": " + localException.toString(), localException);
        }
        Slog.i("ActivityThread", "handleStopService: exception for " + paramIBinder, localException);
        return;
      }
    }
  }
  
  private void handleUnbindService(BindServiceData paramBindServiceData)
  {
    Service localService = (Service)this.mServices.get(paramBindServiceData.token);
    if (localService != null) {
      try
      {
        paramBindServiceData.intent.setExtrasClassLoader(localService.getClassLoader());
        paramBindServiceData.intent.prepareToEnterProcess();
        boolean bool = localService.onUnbind(paramBindServiceData.intent);
        if (bool) {}
        try
        {
          ActivityManagerNative.getDefault().unbindFinished(paramBindServiceData.token, paramBindServiceData.intent, bool);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
        ActivityManagerNative.getDefault().serviceDoneExecuting(paramBindServiceData.token, 0, 0, 0);
        return;
      }
      catch (Exception localException)
      {
        if (!this.mInstrumentation.onException(localService, localException)) {
          throw new RuntimeException("Unable to unbind to service " + localService + " with " + paramBindServiceData.intent + ": " + localException.toString(), localException);
        }
      }
    }
  }
  
  private void handleUpdatePackageCompatibilityInfo(UpdateCompatibilityData paramUpdateCompatibilityData)
  {
    LoadedApk localLoadedApk = peekPackageInfo(paramUpdateCompatibilityData.pkg, false);
    if (localLoadedApk != null) {
      localLoadedApk.setCompatibilityInfo(paramUpdateCompatibilityData.info);
    }
    localLoadedApk = peekPackageInfo(paramUpdateCompatibilityData.pkg, true);
    if (localLoadedApk != null) {
      localLoadedApk.setCompatibilityInfo(paramUpdateCompatibilityData.info);
    }
    handleConfigurationChanged(this.mConfiguration, paramUpdateCompatibilityData.info);
    WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
  }
  
  private void handleWindowVisibility(IBinder paramIBinder, boolean paramBoolean)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (localActivityClientRecord == null)
    {
      Log.w("ActivityThread", "handleWindowVisibility: no activity for token " + paramIBinder);
      return;
    }
    if ((paramBoolean) || (localActivityClientRecord.stopped)) {
      if ((paramBoolean) && (localActivityClientRecord.stopped))
      {
        unscheduleGcIdler();
        localActivityClientRecord.activity.performRestart();
        localActivityClientRecord.stopped = false;
      }
    }
    for (;;)
    {
      if (localActivityClientRecord.activity.mDecor != null) {
        updateVisibility(localActivityClientRecord, paramBoolean);
      }
      this.mSomeActivitiesChanged = true;
      return;
      performStopActivityInner(localActivityClientRecord, null, paramBoolean, false, "handleWindowVisibility");
    }
  }
  
  public static boolean inCompatConfigList(int paramInt, String paramString)
  {
    try
    {
      boolean bool = getPackageManager().inCompatConfigList(paramInt, paramString);
      return bool;
    }
    catch (Exception paramString)
    {
      return false;
    }
    catch (LinkageError paramString)
    {
      for (;;) {}
    }
  }
  
  private final void incProviderRefLocked(ProviderRefCount paramProviderRefCount, boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      paramProviderRefCount.stableCount += 1;
      if (paramProviderRefCount.stableCount == 1)
      {
        if (!paramProviderRefCount.removePending) {
          break label66;
        }
        i = -1;
        paramProviderRefCount.removePending = false;
        this.mH.removeMessages(131, paramProviderRefCount);
      }
    }
    for (;;)
    {
      label66:
      try
      {
        ActivityManagerNative.getDefault().refContentProvider(paramProviderRefCount.holder.connection, 1, i);
        return;
      }
      catch (RemoteException paramProviderRefCount) {}
      i = 0;
      continue;
      paramProviderRefCount.unstableCount += 1;
      if (paramProviderRefCount.unstableCount == 1)
      {
        if (paramProviderRefCount.removePending)
        {
          paramProviderRefCount.removePending = false;
          this.mH.removeMessages(131, paramProviderRefCount);
          return;
        }
        try
        {
          ActivityManagerNative.getDefault().refContentProvider(paramProviderRefCount.holder.connection, 0, 1);
          return;
        }
        catch (RemoteException paramProviderRefCount) {}
      }
    }
  }
  
  private void installContentProviders(Context paramContext, List<ProviderInfo> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      IActivityManager.ContentProviderHolder localContentProviderHolder = installProvider(paramContext, null, (ProviderInfo)paramList.next(), false, true, true);
      if (localContentProviderHolder != null)
      {
        localContentProviderHolder.noReleaseNeeded = true;
        localArrayList.add(localContentProviderHolder);
      }
    }
    try
    {
      ActivityManagerNative.getDefault().publishContentProviders(getApplicationThread(), localArrayList);
      return;
    }
    catch (RemoteException paramContext)
    {
      throw paramContext.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  private IActivityManager.ContentProviderHolder installProvider(Context paramContext, IActivityManager.ContentProviderHolder paramContentProviderHolder, ProviderInfo paramProviderInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_2
    //   4: ifnull +10 -> 14
    //   7: aload_2
    //   8: getfield 2591	android/app/IActivityManager$ContentProviderHolder:provider	Landroid/content/IContentProvider;
    //   11: ifnonnull +391 -> 402
    //   14: iload 4
    //   16: ifeq +45 -> 61
    //   19: ldc -114
    //   21: new 701	java/lang/StringBuilder
    //   24: dup
    //   25: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   28: ldc_w 2593
    //   31: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: aload_3
    //   35: getfield 2596	android/content/pm/ProviderInfo:authority	Ljava/lang/String;
    //   38: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: ldc_w 941
    //   44: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: aload_3
    //   48: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   51: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   57: invokestatic 1953	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   60: pop
    //   61: aconst_null
    //   62: astore 7
    //   64: aload_3
    //   65: getfield 2598	android/content/pm/ProviderInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   68: astore 8
    //   70: aload_1
    //   71: invokevirtual 2599	android/content/Context:getPackageName	()Ljava/lang/String;
    //   74: aload 8
    //   76: getfield 966	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   79: invokevirtual 1330	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   82: ifeq +52 -> 134
    //   85: aload_1
    //   86: ifnonnull +95 -> 181
    //   89: ldc -114
    //   91: new 701	java/lang/StringBuilder
    //   94: dup
    //   95: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   98: ldc_w 2601
    //   101: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: aload 8
    //   106: getfield 966	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   109: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: ldc_w 2603
    //   115: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: aload_3
    //   119: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   122: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   128: invokestatic 1624	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   131: pop
    //   132: aconst_null
    //   133: areturn
    //   134: aload_0
    //   135: getfield 692	android/app/ActivityThread:mInitialApplication	Landroid/app/Application;
    //   138: ifnull +29 -> 167
    //   141: aload_0
    //   142: getfield 692	android/app/ActivityThread:mInitialApplication	Landroid/app/Application;
    //   145: invokevirtual 2604	android/app/Application:getPackageName	()Ljava/lang/String;
    //   148: aload 8
    //   150: getfield 966	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   153: invokevirtual 1330	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   156: ifeq +11 -> 167
    //   159: aload_0
    //   160: getfield 692	android/app/ActivityThread:mInitialApplication	Landroid/app/Application;
    //   163: astore_1
    //   164: goto -79 -> 85
    //   167: aload_1
    //   168: aload 8
    //   170: getfield 966	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   173: iconst_1
    //   174: invokevirtual 2608	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   177: astore_1
    //   178: goto -93 -> 85
    //   181: aload_1
    //   182: invokevirtual 2609	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   185: aload_3
    //   186: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   189: invokevirtual 1741	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   192: invokevirtual 1744	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   195: checkcast 2120	android/content/ContentProvider
    //   198: astore 8
    //   200: aload 8
    //   202: invokevirtual 2613	android/content/ContentProvider:getIContentProvider	()Landroid/content/IContentProvider;
    //   205: astore 7
    //   207: aload 7
    //   209: ifnonnull +50 -> 259
    //   212: ldc -114
    //   214: new 701	java/lang/StringBuilder
    //   217: dup
    //   218: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   221: ldc_w 2615
    //   224: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   227: aload_3
    //   228: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   231: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   234: ldc_w 2617
    //   237: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: aload_3
    //   241: getfield 2598	android/content/pm/ProviderInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   244: getfield 974	android/content/pm/ApplicationInfo:sourceDir	Ljava/lang/String;
    //   247: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokestatic 1975	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   256: pop
    //   257: aconst_null
    //   258: areturn
    //   259: aload 8
    //   261: aload_1
    //   262: aload_3
    //   263: invokevirtual 2621	android/content/ContentProvider:attachInfo	(Landroid/content/Context;Landroid/content/pm/ProviderInfo;)V
    //   266: aload 8
    //   268: astore_1
    //   269: aload_0
    //   270: getfield 553	android/app/ActivityThread:mProviderMap	Landroid/util/ArrayMap;
    //   273: astore 8
    //   275: aload 8
    //   277: monitorenter
    //   278: aload 7
    //   280: invokeinterface 2624 1 0
    //   285: astore 9
    //   287: aload_1
    //   288: ifnull +184 -> 472
    //   291: new 936	android/content/ComponentName
    //   294: dup
    //   295: aload_3
    //   296: getfield 2625	android/content/pm/ProviderInfo:packageName	Ljava/lang/String;
    //   299: aload_3
    //   300: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   303: invokespecial 1750	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   306: astore 10
    //   308: aload_0
    //   309: getfield 559	android/app/ActivityThread:mLocalProvidersByName	Landroid/util/ArrayMap;
    //   312: aload 10
    //   314: invokevirtual 1370	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   317: checkcast 67	android/app/ActivityThread$ProviderClientRecord
    //   320: astore_2
    //   321: aload_2
    //   322: ifnull +96 -> 418
    //   325: aload_2
    //   326: getfield 2628	android/app/ActivityThread$ProviderClientRecord:mProvider	Landroid/content/IContentProvider;
    //   329: astore_1
    //   330: aload_2
    //   331: astore_1
    //   332: aload_1
    //   333: getfield 2631	android/app/ActivityThread$ProviderClientRecord:mHolder	Landroid/app/IActivityManager$ContentProviderHolder;
    //   336: astore_1
    //   337: aload 8
    //   339: monitorexit
    //   340: aload_1
    //   341: areturn
    //   342: astore_1
    //   343: aload_0
    //   344: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   347: aconst_null
    //   348: aload_1
    //   349: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   352: ifne +48 -> 400
    //   355: new 699	java/lang/RuntimeException
    //   358: dup
    //   359: new 701	java/lang/StringBuilder
    //   362: dup
    //   363: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   366: ldc_w 2633
    //   369: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   372: aload_3
    //   373: getfield 2597	android/content/pm/ProviderInfo:name	Ljava/lang/String;
    //   376: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: ldc_w 941
    //   382: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   385: aload_1
    //   386: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   389: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   392: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   395: aload_1
    //   396: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   399: athrow
    //   400: aconst_null
    //   401: areturn
    //   402: aload_2
    //   403: getfield 2591	android/app/IActivityManager$ContentProviderHolder:provider	Landroid/content/IContentProvider;
    //   406: astore 8
    //   408: aload 7
    //   410: astore_1
    //   411: aload 8
    //   413: astore 7
    //   415: goto -146 -> 269
    //   418: new 2542	android/app/IActivityManager$ContentProviderHolder
    //   421: dup
    //   422: aload_3
    //   423: invokespecial 2636	android/app/IActivityManager$ContentProviderHolder:<init>	(Landroid/content/pm/ProviderInfo;)V
    //   426: astore_2
    //   427: aload_2
    //   428: aload 7
    //   430: putfield 2591	android/app/IActivityManager$ContentProviderHolder:provider	Landroid/content/IContentProvider;
    //   433: aload_2
    //   434: iconst_1
    //   435: putfield 2575	android/app/IActivityManager$ContentProviderHolder:noReleaseNeeded	Z
    //   438: aload_0
    //   439: aload 7
    //   441: aload_1
    //   442: aload_2
    //   443: invokespecial 2640	android/app/ActivityThread:installProviderAuthoritiesLocked	(Landroid/content/IContentProvider;Landroid/content/ContentProvider;Landroid/app/IActivityManager$ContentProviderHolder;)Landroid/app/ActivityThread$ProviderClientRecord;
    //   446: astore_1
    //   447: aload_0
    //   448: getfield 557	android/app/ActivityThread:mLocalProviders	Landroid/util/ArrayMap;
    //   451: aload 9
    //   453: aload_1
    //   454: invokevirtual 1390	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   457: pop
    //   458: aload_0
    //   459: getfield 559	android/app/ActivityThread:mLocalProvidersByName	Landroid/util/ArrayMap;
    //   462: aload 10
    //   464: aload_1
    //   465: invokevirtual 1390	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   468: pop
    //   469: goto -137 -> 332
    //   472: aload_0
    //   473: getfield 555	android/app/ActivityThread:mProviderRefCountMap	Landroid/util/ArrayMap;
    //   476: aload 9
    //   478: invokevirtual 1370	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   481: checkcast 73	android/app/ActivityThread$ProviderRefCount
    //   484: astore_3
    //   485: aload_3
    //   486: ifnull +41 -> 527
    //   489: aload_3
    //   490: astore_1
    //   491: iload 5
    //   493: ifne +26 -> 519
    //   496: aload_0
    //   497: aload_3
    //   498: iload 6
    //   500: invokespecial 2642	android/app/ActivityThread:incProviderRefLocked	(Landroid/app/ActivityThread$ProviderRefCount;Z)V
    //   503: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   506: aload_2
    //   507: getfield 2545	android/app/IActivityManager$ContentProviderHolder:connection	Landroid/os/IBinder;
    //   510: iload 6
    //   512: invokeinterface 2645 3 0
    //   517: aload_3
    //   518: astore_1
    //   519: aload_1
    //   520: getfield 2540	android/app/ActivityThread$ProviderRefCount:holder	Landroid/app/IActivityManager$ContentProviderHolder;
    //   523: astore_1
    //   524: goto -187 -> 337
    //   527: aload_0
    //   528: aload 7
    //   530: aload_1
    //   531: aload_2
    //   532: invokespecial 2640	android/app/ActivityThread:installProviderAuthoritiesLocked	(Landroid/content/IContentProvider;Landroid/content/ContentProvider;Landroid/app/IActivityManager$ContentProviderHolder;)Landroid/app/ActivityThread$ProviderClientRecord;
    //   535: astore_1
    //   536: iload 5
    //   538: ifeq +39 -> 577
    //   541: new 73	android/app/ActivityThread$ProviderRefCount
    //   544: dup
    //   545: aload_2
    //   546: aload_1
    //   547: sipush 1000
    //   550: sipush 1000
    //   553: invokespecial 2648	android/app/ActivityThread$ProviderRefCount:<init>	(Landroid/app/IActivityManager$ContentProviderHolder;Landroid/app/ActivityThread$ProviderClientRecord;II)V
    //   556: astore_1
    //   557: aload_0
    //   558: getfield 555	android/app/ActivityThread:mProviderRefCountMap	Landroid/util/ArrayMap;
    //   561: aload 9
    //   563: aload_1
    //   564: invokevirtual 1390	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   567: pop
    //   568: goto -49 -> 519
    //   571: astore_1
    //   572: aload 8
    //   574: monitorexit
    //   575: aload_1
    //   576: athrow
    //   577: iload 6
    //   579: ifeq +18 -> 597
    //   582: new 73	android/app/ActivityThread$ProviderRefCount
    //   585: dup
    //   586: aload_2
    //   587: aload_1
    //   588: iconst_1
    //   589: iconst_0
    //   590: invokespecial 2648	android/app/ActivityThread$ProviderRefCount:<init>	(Landroid/app/IActivityManager$ContentProviderHolder;Landroid/app/ActivityThread$ProviderClientRecord;II)V
    //   593: astore_1
    //   594: goto -37 -> 557
    //   597: new 73	android/app/ActivityThread$ProviderRefCount
    //   600: dup
    //   601: aload_2
    //   602: aload_1
    //   603: iconst_0
    //   604: iconst_1
    //   605: invokespecial 2648	android/app/ActivityThread$ProviderRefCount:<init>	(Landroid/app/IActivityManager$ContentProviderHolder;Landroid/app/ActivityThread$ProviderClientRecord;II)V
    //   608: astore_1
    //   609: goto -52 -> 557
    //   612: astore_1
    //   613: goto -41 -> 572
    //   616: astore_1
    //   617: aload_3
    //   618: astore_1
    //   619: goto -100 -> 519
    //   622: astore_1
    //   623: aload 7
    //   625: astore_1
    //   626: goto -541 -> 85
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	629	0	this	ActivityThread
    //   0	629	1	paramContext	Context
    //   0	629	2	paramContentProviderHolder	IActivityManager.ContentProviderHolder
    //   0	629	3	paramProviderInfo	ProviderInfo
    //   0	629	4	paramBoolean1	boolean
    //   0	629	5	paramBoolean2	boolean
    //   0	629	6	paramBoolean3	boolean
    //   1	623	7	localObject1	Object
    //   68	505	8	localObject2	Object
    //   285	277	9	localIBinder	IBinder
    //   306	157	10	localComponentName	ComponentName
    // Exception table:
    //   from	to	target	type
    //   181	207	342	java/lang/Exception
    //   212	257	342	java/lang/Exception
    //   259	266	342	java/lang/Exception
    //   278	287	571	finally
    //   291	321	571	finally
    //   325	330	571	finally
    //   332	337	571	finally
    //   418	427	571	finally
    //   472	485	571	finally
    //   496	503	571	finally
    //   503	517	571	finally
    //   519	524	571	finally
    //   527	536	571	finally
    //   541	557	571	finally
    //   557	568	571	finally
    //   582	594	571	finally
    //   597	609	571	finally
    //   427	469	612	finally
    //   503	517	616	android/os/RemoteException
    //   167	178	622	android/content/pm/PackageManager$NameNotFoundException
  }
  
  private ProviderClientRecord installProviderAuthoritiesLocked(IContentProvider paramIContentProvider, ContentProvider paramContentProvider, IActivityManager.ContentProviderHolder paramContentProviderHolder)
  {
    String[] arrayOfString = paramContentProviderHolder.info.authority.split(";");
    int j = UserHandle.getUserId(paramContentProviderHolder.info.applicationInfo.uid);
    paramIContentProvider = new ProviderClientRecord(arrayOfString, paramIContentProvider, paramContentProvider, paramContentProviderHolder);
    int i = 0;
    int k = arrayOfString.length;
    if (i < k)
    {
      paramContentProvider = arrayOfString[i];
      paramContentProviderHolder = new ProviderKey(paramContentProvider, j);
      if ((ProviderClientRecord)this.mProviderMap.get(paramContentProviderHolder) != null) {
        Slog.w("ActivityThread", "Content provider " + paramIContentProvider.mHolder.info.name + " already published as " + paramContentProvider);
      }
      for (;;)
      {
        i += 1;
        break;
        this.mProviderMap.put(paramContentProviderHolder, paramIContentProvider);
      }
    }
    return paramIContentProvider;
  }
  
  public static boolean isSystem()
  {
    if (sCurrentActivityThread != null) {
      return sCurrentActivityThread.mSystemThread;
    }
    return false;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    Trace.traceBegin(64L, "ActivityThreadMain");
    SamplingProfilerIntegration.start();
    CloseGuard.setEnabled(false);
    Environment.initForCurrentUser();
    EventLogger.setReporter(new EventLoggingReporter(null));
    TrustedCertificateStore.setDefaultUserDirectory(Environment.getUserConfigDirectory(UserHandle.myUserId()));
    Process.setArgV0("<pre-initialized>");
    Looper.prepareMainLooper();
    paramArrayOfString = new ActivityThread();
    paramArrayOfString.attach(false);
    if (sMainThreadHandler == null) {
      sMainThreadHandler = paramArrayOfString.getHandler();
    }
    Trace.traceEnd(64L);
    Looper.loop();
    throw new RuntimeException("Main thread loop unexpectedly exited");
  }
  
  private void onCoreSettingsChange()
  {
    if (this.mCoreSettings.getInt("debug_view_attributes", 0) != 0) {}
    for (boolean bool = true; bool != View.mDebugViewAttributes; bool = false)
    {
      View.mDebugViewAttributes = bool;
      Iterator localIterator = this.mActivities.entrySet().iterator();
      while (localIterator.hasNext()) {
        requestRelaunchActivity((IBinder)((Map.Entry)localIterator.next()).getKey(), null, null, 0, false, null, null, false, false);
      }
    }
  }
  
  private void performConfigurationChanged(ComponentCallbacks2 paramComponentCallbacks2, IBinder paramIBinder, Configuration paramConfiguration1, Configuration paramConfiguration2, boolean paramBoolean)
  {
    Activity localActivity = null;
    if ((paramComponentCallbacks2 instanceof Activity)) {
      localActivity = (Activity)paramComponentCallbacks2;
    }
    if (localActivity != null) {
      localActivity.mCalled = false;
    }
    int j = 0;
    int i;
    if ((localActivity == null) || (localActivity.mCurrentConfig == null)) {
      i = 1;
    }
    for (;;)
    {
      if (i != 0)
      {
        Configuration localConfiguration = null;
        if ((paramComponentCallbacks2 instanceof ContextThemeWrapper)) {
          localConfiguration = ((ContextThemeWrapper)paramComponentCallbacks2).getOverrideConfiguration();
        }
        if (paramIBinder != null)
        {
          paramConfiguration2 = createNewConfigAndUpdateIfNotNull(paramConfiguration2, localConfiguration);
          this.mResourcesManager.updateResourcesForActivity(paramIBinder, paramConfiguration2);
        }
        if (paramBoolean) {
          paramComponentCallbacks2.onConfigurationChanged(createNewConfigAndUpdateIfNotNull(paramConfiguration1, localConfiguration));
        }
        if (localActivity != null)
        {
          if ((paramBoolean) && (!localActivity.mCalled)) {
            break;
          }
          localActivity.mConfigChangeFlags = 0;
          localActivity.mCurrentConfig = new Configuration(paramConfiguration1);
        }
      }
      return;
      int k = localActivity.mCurrentConfig.diff(paramConfiguration1);
      if (k == 0)
      {
        i = j;
        if (this.mResourcesManager.isSameResourcesOverrideConfig(paramIBinder, paramConfiguration2)) {}
      }
      else
      {
        if ((!this.mUpdatingSystemConfig) || ((localActivity.mActivityInfo.getRealConfigChanged() & k) == 0)) {}
        while (!paramBoolean)
        {
          i = 1;
          break;
        }
        i = j;
      }
    }
    throw new SuperNotCalledException("Activity " + localActivity.getLocalClassName() + " did not call through to super.onConfigurationChanged()");
  }
  
  private void performConfigurationChangedForActivity(ActivityClientRecord paramActivityClientRecord, Configuration paramConfiguration, boolean paramBoolean)
  {
    ActivityClientRecord.-get0(paramActivityClientRecord).setTo(paramConfiguration);
    if (paramActivityClientRecord.overrideConfig != null) {
      ActivityClientRecord.-get0(paramActivityClientRecord).updateFrom(paramActivityClientRecord.overrideConfig);
    }
    performConfigurationChanged(paramActivityClientRecord.activity, paramActivityClientRecord.token, ActivityClientRecord.-get0(paramActivityClientRecord), paramActivityClientRecord.overrideConfig, paramBoolean);
    freeTextLayoutCachesIfNeeded(paramActivityClientRecord.activity.mCurrentConfig.diff(ActivityClientRecord.-get0(paramActivityClientRecord)));
  }
  
  /* Error */
  private ActivityClientRecord performDestroyActivity(IBinder paramIBinder, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 500	android/app/ActivityThread:mActivities	Landroid/util/ArrayMap;
    //   4: aload_1
    //   5: invokevirtual 1370	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8: checkcast 14	android/app/ActivityThread$ActivityClientRecord
    //   11: astore 7
    //   13: aconst_null
    //   14: astore 5
    //   16: aload 7
    //   18: ifnull +385 -> 403
    //   21: aload 7
    //   23: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   26: invokevirtual 795	android/app/Activity:getClass	()Ljava/lang/Class;
    //   29: astore 6
    //   31: aload 7
    //   33: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   36: astore 5
    //   38: aload 5
    //   40: aload 5
    //   42: getfield 2213	android/app/Activity:mConfigChangeFlags	I
    //   45: iload_3
    //   46: ior
    //   47: putfield 2213	android/app/Activity:mConfigChangeFlags	I
    //   50: iload_2
    //   51: ifeq +12 -> 63
    //   54: aload 7
    //   56: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   59: iconst_1
    //   60: putfield 2157	android/app/Activity:mFinished	Z
    //   63: aload_0
    //   64: aload 7
    //   66: ldc_w 2786
    //   69: invokespecial 2168	android/app/ActivityThread:performPauseActivityIfNeeded	(Landroid/app/ActivityThread$ActivityClientRecord;Ljava/lang/String;)V
    //   72: aload 7
    //   74: getfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   77: ifne +62 -> 139
    //   80: aload 7
    //   82: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   85: aload 7
    //   87: getfield 759	android/app/ActivityThread$ActivityClientRecord:mPreserveWindow	Z
    //   90: invokevirtual 2423	android/app/Activity:performStop	(Z)V
    //   93: aload 7
    //   95: iconst_1
    //   96: putfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   99: sipush 30049
    //   102: iconst_3
    //   103: anewarray 4	java/lang/Object
    //   106: dup
    //   107: iconst_0
    //   108: invokestatic 612	android/os/UserHandle:myUserId	()I
    //   111: invokestatic 1226	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   114: aastore
    //   115: dup
    //   116: iconst_1
    //   117: aload 7
    //   119: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   122: invokevirtual 2426	android/app/Activity:getComponentName	()Landroid/content/ComponentName;
    //   125: invokevirtual 1735	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   128: aastore
    //   129: dup
    //   130: iconst_2
    //   131: ldc_w 2786
    //   134: aastore
    //   135: invokestatic 2434	android/util/EventLog:writeEvent	(I[Ljava/lang/Object;)I
    //   138: pop
    //   139: iload 4
    //   141: ifeq +16 -> 157
    //   144: aload 7
    //   146: aload 7
    //   148: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   151: invokevirtual 2790	android/app/Activity:retainNonConfigurationInstances	()Landroid/app/Activity$NonConfigurationInstances;
    //   154: putfield 2794	android/app/ActivityThread$ActivityClientRecord:lastNonConfigurationInstances	Landroid/app/Activity$NonConfigurationInstances;
    //   157: aload 7
    //   159: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   162: iconst_0
    //   163: putfield 2370	android/app/Activity:mCalled	Z
    //   166: aload_0
    //   167: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   170: aload 7
    //   172: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   175: invokevirtual 2797	android/app/Instrumentation:callActivityOnDestroy	(Landroid/app/Activity;)V
    //   178: aload 7
    //   180: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   183: getfield 2370	android/app/Activity:mCalled	Z
    //   186: ifne +193 -> 379
    //   189: new 2363	android/util/SuperNotCalledException
    //   192: dup
    //   193: new 701	java/lang/StringBuilder
    //   196: dup
    //   197: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   200: ldc_w 2379
    //   203: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: aload 7
    //   208: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   211: invokestatic 2801	android/app/ActivityThread:safeToComponentShortString	(Landroid/content/Intent;)Ljava/lang/String;
    //   214: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: ldc_w 2803
    //   220: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   226: invokespecial 2382	android/util/SuperNotCalledException:<init>	(Ljava/lang/String;)V
    //   229: athrow
    //   230: astore_1
    //   231: aload_1
    //   232: athrow
    //   233: astore 5
    //   235: aload_0
    //   236: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   239: aload 7
    //   241: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   244: aload 5
    //   246: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   249: ifne -156 -> 93
    //   252: new 699	java/lang/RuntimeException
    //   255: dup
    //   256: new 701	java/lang/StringBuilder
    //   259: dup
    //   260: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   263: ldc_w 2436
    //   266: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload 7
    //   271: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   274: invokestatic 2801	android/app/ActivityThread:safeToComponentShortString	(Landroid/content/Intent;)Ljava/lang/String;
    //   277: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: ldc_w 941
    //   283: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: aload 5
    //   288: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   291: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   297: aload 5
    //   299: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   302: athrow
    //   303: astore_1
    //   304: aload_1
    //   305: athrow
    //   306: astore 5
    //   308: aload_0
    //   309: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   312: aload 7
    //   314: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   317: aload 5
    //   319: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   322: ifne -165 -> 157
    //   325: new 699	java/lang/RuntimeException
    //   328: dup
    //   329: new 701	java/lang/StringBuilder
    //   332: dup
    //   333: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   336: ldc_w 2805
    //   339: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: aload 7
    //   344: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   347: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   350: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   353: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: ldc_w 941
    //   359: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: aload 5
    //   364: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   367: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   370: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   373: aload 5
    //   375: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   378: athrow
    //   379: aload 6
    //   381: astore 5
    //   383: aload 7
    //   385: getfield 2017	android/app/ActivityThread$ActivityClientRecord:window	Landroid/view/Window;
    //   388: ifnull +15 -> 403
    //   391: aload 7
    //   393: getfield 2017	android/app/ActivityThread$ActivityClientRecord:window	Landroid/view/Window;
    //   396: invokevirtual 2808	android/view/Window:closeAllPanels	()V
    //   399: aload 6
    //   401: astore 5
    //   403: aload_0
    //   404: getfield 500	android/app/ActivityThread:mActivities	Landroid/util/ArrayMap;
    //   407: aload_1
    //   408: invokevirtual 2039	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   411: pop
    //   412: aload 5
    //   414: invokestatic 2812	android/os/StrictMode:decrementExpectedActivityCount	(Ljava/lang/Class;)V
    //   417: aload 7
    //   419: areturn
    //   420: astore 8
    //   422: aload 6
    //   424: astore 5
    //   426: aload_0
    //   427: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   430: aload 7
    //   432: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   435: aload 8
    //   437: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   440: ifne -37 -> 403
    //   443: new 699	java/lang/RuntimeException
    //   446: dup
    //   447: new 701	java/lang/StringBuilder
    //   450: dup
    //   451: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   454: ldc_w 2814
    //   457: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   460: aload 7
    //   462: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   465: invokestatic 2801	android/app/ActivityThread:safeToComponentShortString	(Landroid/content/Intent;)Ljava/lang/String;
    //   468: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   471: ldc_w 941
    //   474: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: aload 8
    //   479: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   482: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   485: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   488: aload 8
    //   490: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   493: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	494	0	this	ActivityThread
    //   0	494	1	paramIBinder	IBinder
    //   0	494	2	paramBoolean1	boolean
    //   0	494	3	paramInt	int
    //   0	494	4	paramBoolean2	boolean
    //   14	27	5	localActivity	Activity
    //   233	65	5	localException1	Exception
    //   306	68	5	localException2	Exception
    //   381	44	5	localObject	Object
    //   29	394	6	localClass	Class
    //   11	450	7	localActivityClientRecord	ActivityClientRecord
    //   420	69	8	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   157	230	230	android/util/SuperNotCalledException
    //   383	399	230	android/util/SuperNotCalledException
    //   80	93	233	java/lang/Exception
    //   80	93	303	android/util/SuperNotCalledException
    //   144	157	306	java/lang/Exception
    //   157	230	420	java/lang/Exception
    //   383	399	420	java/lang/Exception
  }
  
  /* Error */
  private Activity performLaunchActivity(ActivityClientRecord paramActivityClientRecord, Intent paramIntent)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4: astore 4
    //   6: aload_1
    //   7: getfield 819	android/app/ActivityThread$ActivityClientRecord:packageInfo	Landroid/app/LoadedApk;
    //   10: ifnonnull +21 -> 31
    //   13: aload_1
    //   14: aload_0
    //   15: aload 4
    //   17: getfield 2236	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   20: aload_1
    //   21: getfield 2818	android/app/ActivityThread$ActivityClientRecord:compatInfo	Landroid/content/res/CompatibilityInfo;
    //   24: iconst_1
    //   25: invokevirtual 2821	android/app/ActivityThread:getPackageInfo	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;I)Landroid/app/LoadedApk;
    //   28: putfield 819	android/app/ActivityThread$ActivityClientRecord:packageInfo	Landroid/app/LoadedApk;
    //   31: aload_1
    //   32: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   35: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   38: astore 5
    //   40: aload 5
    //   42: astore 4
    //   44: aload 5
    //   46: ifnonnull +29 -> 75
    //   49: aload_1
    //   50: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   53: aload_0
    //   54: getfield 692	android/app/ActivityThread:mInitialApplication	Landroid/app/Application;
    //   57: invokevirtual 2824	android/app/Application:getPackageManager	()Landroid/content/pm/PackageManager;
    //   60: invokevirtual 2828	android/content/Intent:resolveActivity	(Landroid/content/pm/PackageManager;)Landroid/content/ComponentName;
    //   63: astore 4
    //   65: aload_1
    //   66: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   69: aload 4
    //   71: invokevirtual 2832	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   74: pop
    //   75: aload 4
    //   77: astore 5
    //   79: aload_1
    //   80: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   83: getfield 2835	android/content/pm/ActivityInfo:targetActivity	Ljava/lang/String;
    //   86: ifnull +26 -> 112
    //   89: new 936	android/content/ComponentName
    //   92: dup
    //   93: aload_1
    //   94: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   97: getfield 2836	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   100: aload_1
    //   101: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   104: getfield 2835	android/content/pm/ActivityInfo:targetActivity	Ljava/lang/String;
    //   107: invokespecial 1750	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   110: astore 5
    //   112: aconst_null
    //   113: astore 6
    //   115: aload 6
    //   117: astore 4
    //   119: aload_1
    //   120: getfield 819	android/app/ActivityThread$ActivityClientRecord:packageInfo	Landroid/app/LoadedApk;
    //   123: invokevirtual 1379	android/app/LoadedApk:getClassLoader	()Ljava/lang/ClassLoader;
    //   126: astore 8
    //   128: aload 6
    //   130: astore 4
    //   132: aload_0
    //   133: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   136: aload 8
    //   138: aload 5
    //   140: invokevirtual 1735	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   143: aload_1
    //   144: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   147: invokevirtual 2840	android/app/Instrumentation:newActivity	(Ljava/lang/ClassLoader;Ljava/lang/String;Landroid/content/Intent;)Landroid/app/Activity;
    //   150: astore 7
    //   152: aload 7
    //   154: astore 4
    //   156: aload 7
    //   158: invokevirtual 795	android/app/Activity:getClass	()Ljava/lang/Class;
    //   161: invokestatic 2843	android/os/StrictMode:incrementExpectedActivityCount	(Ljava/lang/Class;)V
    //   164: aload 7
    //   166: astore 4
    //   168: aload_1
    //   169: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   172: aload 8
    //   174: invokevirtual 1019	android/content/Intent:setExtrasClassLoader	(Ljava/lang/ClassLoader;)V
    //   177: aload 7
    //   179: astore 4
    //   181: aload_1
    //   182: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   185: invokevirtual 1020	android/content/Intent:prepareToEnterProcess	()V
    //   188: aload 7
    //   190: astore 6
    //   192: aload 7
    //   194: astore 4
    //   196: aload_1
    //   197: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   200: ifnull +20 -> 220
    //   203: aload 7
    //   205: astore 4
    //   207: aload_1
    //   208: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   211: aload 8
    //   213: invokevirtual 2846	android/os/Bundle:setClassLoader	(Ljava/lang/ClassLoader;)V
    //   216: aload 7
    //   218: astore 6
    //   220: aload_1
    //   221: getfield 819	android/app/ActivityThread$ActivityClientRecord:packageInfo	Landroid/app/LoadedApk;
    //   224: iconst_0
    //   225: aload_0
    //   226: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   229: invokevirtual 690	android/app/LoadedApk:makeApplication	(ZLandroid/app/Instrumentation;)Landroid/app/Application;
    //   232: astore 8
    //   234: aload 6
    //   236: ifnull +623 -> 859
    //   239: aload_0
    //   240: aload_1
    //   241: aload 6
    //   243: invokespecial 2848	android/app/ActivityThread:createBaseContextForActivity	(Landroid/app/ActivityThread$ActivityClientRecord;Landroid/app/Activity;)Landroid/content/Context;
    //   246: astore 9
    //   248: aload_1
    //   249: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   252: aload 9
    //   254: invokevirtual 2849	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   257: invokevirtual 2853	android/content/pm/ActivityInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   260: astore 10
    //   262: new 573	android/content/res/Configuration
    //   265: dup
    //   266: aload_0
    //   267: getfield 1450	android/app/ActivityThread:mCompatConfiguration	Landroid/content/res/Configuration;
    //   270: invokespecial 875	android/content/res/Configuration:<init>	(Landroid/content/res/Configuration;)V
    //   273: astore 11
    //   275: aload_1
    //   276: getfield 822	android/app/ActivityThread$ActivityClientRecord:overrideConfig	Landroid/content/res/Configuration;
    //   279: ifnull +13 -> 292
    //   282: aload 11
    //   284: aload_1
    //   285: getfield 822	android/app/ActivityThread$ActivityClientRecord:overrideConfig	Landroid/content/res/Configuration;
    //   288: invokevirtual 879	android/content/res/Configuration:updateFrom	(Landroid/content/res/Configuration;)I
    //   291: pop
    //   292: aconst_null
    //   293: astore 7
    //   295: aload 7
    //   297: astore 4
    //   299: aload_1
    //   300: getfield 763	android/app/ActivityThread$ActivityClientRecord:mPendingRemoveWindow	Landroid/view/Window;
    //   303: ifnull +30 -> 333
    //   306: aload 7
    //   308: astore 4
    //   310: aload_1
    //   311: getfield 759	android/app/ActivityThread$ActivityClientRecord:mPreserveWindow	Z
    //   314: ifeq +19 -> 333
    //   317: aload_1
    //   318: getfield 763	android/app/ActivityThread$ActivityClientRecord:mPendingRemoveWindow	Landroid/view/Window;
    //   321: astore 4
    //   323: aload_1
    //   324: aconst_null
    //   325: putfield 763	android/app/ActivityThread$ActivityClientRecord:mPendingRemoveWindow	Landroid/view/Window;
    //   328: aload_1
    //   329: aconst_null
    //   330: putfield 767	android/app/ActivityThread$ActivityClientRecord:mPendingRemoveWindowManager	Landroid/view/WindowManager;
    //   333: aload 6
    //   335: aload 9
    //   337: aload_0
    //   338: aload_0
    //   339: invokevirtual 2857	android/app/ActivityThread:getInstrumentation	()Landroid/app/Instrumentation;
    //   342: aload_1
    //   343: getfield 812	android/app/ActivityThread$ActivityClientRecord:token	Landroid/os/IBinder;
    //   346: aload_1
    //   347: getfield 2860	android/app/ActivityThread$ActivityClientRecord:ident	I
    //   350: aload 8
    //   352: aload_1
    //   353: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   356: aload_1
    //   357: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   360: aload 10
    //   362: aload_1
    //   363: getfield 2863	android/app/ActivityThread$ActivityClientRecord:parent	Landroid/app/Activity;
    //   366: aload_1
    //   367: getfield 2866	android/app/ActivityThread$ActivityClientRecord:embeddedID	Ljava/lang/String;
    //   370: aload_1
    //   371: getfield 2794	android/app/ActivityThread$ActivityClientRecord:lastNonConfigurationInstances	Landroid/app/Activity$NonConfigurationInstances;
    //   374: aload 11
    //   376: aload_1
    //   377: getfield 2869	android/app/ActivityThread$ActivityClientRecord:referrer	Ljava/lang/String;
    //   380: aload_1
    //   381: getfield 2179	android/app/ActivityThread$ActivityClientRecord:voiceInteractor	Lcom/android/internal/app/IVoiceInteractor;
    //   384: aload 4
    //   386: invokevirtual 2872	android/app/Activity:attach	(Landroid/content/Context;Landroid/app/ActivityThread;Landroid/app/Instrumentation;Landroid/os/IBinder;ILandroid/app/Application;Landroid/content/Intent;Landroid/content/pm/ActivityInfo;Ljava/lang/CharSequence;Landroid/app/Activity;Ljava/lang/String;Landroid/app/Activity$NonConfigurationInstances;Landroid/content/res/Configuration;Ljava/lang/String;Lcom/android/internal/app/IVoiceInteractor;Landroid/view/Window;)V
    //   389: aload_2
    //   390: ifnull +9 -> 399
    //   393: aload 6
    //   395: aload_2
    //   396: putfield 2322	android/app/Activity:mIntent	Landroid/content/Intent;
    //   399: aload_1
    //   400: aconst_null
    //   401: putfield 2794	android/app/ActivityThread$ActivityClientRecord:lastNonConfigurationInstances	Landroid/app/Activity$NonConfigurationInstances;
    //   404: aload 6
    //   406: iconst_0
    //   407: putfield 2875	android/app/Activity:mStartedActivity	Z
    //   410: aload_1
    //   411: getfield 2817	android/app/ActivityThread$ActivityClientRecord:activityInfo	Landroid/content/pm/ActivityInfo;
    //   414: invokevirtual 2878	android/content/pm/ActivityInfo:getThemeResource	()I
    //   417: istore_3
    //   418: iload_3
    //   419: ifeq +9 -> 428
    //   422: aload 6
    //   424: iload_3
    //   425: invokevirtual 2881	android/app/Activity:setTheme	(I)V
    //   428: aload 6
    //   430: iconst_0
    //   431: putfield 2370	android/app/Activity:mCalled	Z
    //   434: aload_1
    //   435: invokevirtual 731	android/app/ActivityThread$ActivityClientRecord:isPersistable	()Z
    //   438: ifeq +139 -> 577
    //   441: aload_0
    //   442: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   445: aload 6
    //   447: aload_1
    //   448: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   451: aload_1
    //   452: getfield 738	android/app/ActivityThread$ActivityClientRecord:persistentState	Landroid/os/PersistableBundle;
    //   455: invokevirtual 2884	android/app/Instrumentation:callActivityOnCreate	(Landroid/app/Activity;Landroid/os/Bundle;Landroid/os/PersistableBundle;)V
    //   458: aload 6
    //   460: getfield 2370	android/app/Activity:mCalled	Z
    //   463: ifne +187 -> 650
    //   466: new 2363	android/util/SuperNotCalledException
    //   469: dup
    //   470: new 701	java/lang/StringBuilder
    //   473: dup
    //   474: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   477: ldc_w 2379
    //   480: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   483: aload_1
    //   484: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   487: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   490: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   493: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   496: ldc_w 2886
    //   499: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   502: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   505: invokespecial 2382	android/util/SuperNotCalledException:<init>	(Ljava/lang/String;)V
    //   508: athrow
    //   509: astore_1
    //   510: aload_1
    //   511: athrow
    //   512: astore 7
    //   514: aload 4
    //   516: astore 6
    //   518: aload_0
    //   519: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   522: aload 4
    //   524: aload 7
    //   526: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   529: ifne -309 -> 220
    //   532: new 699	java/lang/RuntimeException
    //   535: dup
    //   536: new 701	java/lang/StringBuilder
    //   539: dup
    //   540: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   543: ldc_w 2888
    //   546: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   549: aload 5
    //   551: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   554: ldc_w 941
    //   557: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   560: aload 7
    //   562: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   565: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   568: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   571: aload 7
    //   573: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   576: athrow
    //   577: aload_0
    //   578: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   581: aload 6
    //   583: aload_1
    //   584: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   587: invokevirtual 2890	android/app/Instrumentation:callActivityOnCreate	(Landroid/app/Activity;Landroid/os/Bundle;)V
    //   590: goto -132 -> 458
    //   593: astore_1
    //   594: aload_0
    //   595: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   598: aload 6
    //   600: aload_1
    //   601: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   604: ifne +273 -> 877
    //   607: new 699	java/lang/RuntimeException
    //   610: dup
    //   611: new 701	java/lang/StringBuilder
    //   614: dup
    //   615: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   618: ldc_w 2892
    //   621: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   624: aload 5
    //   626: invokevirtual 1038	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   629: ldc_w 941
    //   632: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   635: aload_1
    //   636: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   639: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   642: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   645: aload_1
    //   646: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   649: athrow
    //   650: aload_1
    //   651: aload 6
    //   653: putfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   656: aload_1
    //   657: iconst_1
    //   658: putfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   661: aload_1
    //   662: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   665: getfield 2157	android/app/Activity:mFinished	Z
    //   668: ifne +13 -> 681
    //   671: aload 6
    //   673: invokevirtual 2895	android/app/Activity:performStart	()V
    //   676: aload_1
    //   677: iconst_0
    //   678: putfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   681: aload_1
    //   682: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   685: getfield 2157	android/app/Activity:mFinished	Z
    //   688: ifne +41 -> 729
    //   691: aload_1
    //   692: invokevirtual 731	android/app/ActivityThread$ActivityClientRecord:isPersistable	()Z
    //   695: ifeq +125 -> 820
    //   698: aload_1
    //   699: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   702: ifnonnull +10 -> 712
    //   705: aload_1
    //   706: getfield 738	android/app/ActivityThread$ActivityClientRecord:persistentState	Landroid/os/PersistableBundle;
    //   709: ifnull +20 -> 729
    //   712: aload_0
    //   713: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   716: aload 6
    //   718: aload_1
    //   719: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   722: aload_1
    //   723: getfield 738	android/app/ActivityThread$ActivityClientRecord:persistentState	Landroid/os/PersistableBundle;
    //   726: invokevirtual 2898	android/app/Instrumentation:callActivityOnRestoreInstanceState	(Landroid/app/Activity;Landroid/os/Bundle;Landroid/os/PersistableBundle;)V
    //   729: aload_1
    //   730: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   733: getfield 2157	android/app/Activity:mFinished	Z
    //   736: ifne +123 -> 859
    //   739: aload 6
    //   741: iconst_0
    //   742: putfield 2370	android/app/Activity:mCalled	Z
    //   745: aload_1
    //   746: invokevirtual 731	android/app/ActivityThread$ActivityClientRecord:isPersistable	()Z
    //   749: ifeq +94 -> 843
    //   752: aload_0
    //   753: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   756: aload 6
    //   758: aload_1
    //   759: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   762: aload_1
    //   763: getfield 738	android/app/ActivityThread$ActivityClientRecord:persistentState	Landroid/os/PersistableBundle;
    //   766: invokevirtual 2901	android/app/Instrumentation:callActivityOnPostCreate	(Landroid/app/Activity;Landroid/os/Bundle;Landroid/os/PersistableBundle;)V
    //   769: aload 6
    //   771: getfield 2370	android/app/Activity:mCalled	Z
    //   774: ifne +85 -> 859
    //   777: new 2363	android/util/SuperNotCalledException
    //   780: dup
    //   781: new 701	java/lang/StringBuilder
    //   784: dup
    //   785: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   788: ldc_w 2379
    //   791: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   794: aload_1
    //   795: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   798: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   801: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   804: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   807: ldc_w 2903
    //   810: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   813: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   816: invokespecial 2382	android/util/SuperNotCalledException:<init>	(Ljava/lang/String;)V
    //   819: athrow
    //   820: aload_1
    //   821: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   824: ifnull -95 -> 729
    //   827: aload_0
    //   828: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   831: aload 6
    //   833: aload_1
    //   834: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   837: invokevirtual 2905	android/app/Instrumentation:callActivityOnRestoreInstanceState	(Landroid/app/Activity;Landroid/os/Bundle;)V
    //   840: goto -111 -> 729
    //   843: aload_0
    //   844: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   847: aload 6
    //   849: aload_1
    //   850: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   853: invokevirtual 2907	android/app/Instrumentation:callActivityOnPostCreate	(Landroid/app/Activity;Landroid/os/Bundle;)V
    //   856: goto -87 -> 769
    //   859: aload_1
    //   860: iconst_1
    //   861: putfield 2337	android/app/ActivityThread$ActivityClientRecord:paused	Z
    //   864: aload_0
    //   865: getfield 500	android/app/ActivityThread:mActivities	Landroid/util/ArrayMap;
    //   868: aload_1
    //   869: getfield 812	android/app/ActivityThread$ActivityClientRecord:token	Landroid/os/IBinder;
    //   872: aload_1
    //   873: invokevirtual 1390	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   876: pop
    //   877: aload 6
    //   879: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	880	0	this	ActivityThread
    //   0	880	1	paramActivityClientRecord	ActivityClientRecord
    //   0	880	2	paramIntent	Intent
    //   417	8	3	i	int
    //   4	519	4	localObject1	Object
    //   38	587	5	localObject2	Object
    //   113	765	6	localObject3	Object
    //   150	157	7	localActivity	Activity
    //   512	60	7	localException	Exception
    //   126	225	8	localObject4	Object
    //   246	90	9	localContext	Context
    //   260	101	10	localCharSequence	CharSequence
    //   273	102	11	localConfiguration	Configuration
    // Exception table:
    //   from	to	target	type
    //   220	234	509	android/util/SuperNotCalledException
    //   239	292	509	android/util/SuperNotCalledException
    //   299	306	509	android/util/SuperNotCalledException
    //   310	333	509	android/util/SuperNotCalledException
    //   333	389	509	android/util/SuperNotCalledException
    //   393	399	509	android/util/SuperNotCalledException
    //   399	418	509	android/util/SuperNotCalledException
    //   422	428	509	android/util/SuperNotCalledException
    //   428	458	509	android/util/SuperNotCalledException
    //   458	509	509	android/util/SuperNotCalledException
    //   577	590	509	android/util/SuperNotCalledException
    //   650	681	509	android/util/SuperNotCalledException
    //   681	712	509	android/util/SuperNotCalledException
    //   712	729	509	android/util/SuperNotCalledException
    //   729	769	509	android/util/SuperNotCalledException
    //   769	820	509	android/util/SuperNotCalledException
    //   820	840	509	android/util/SuperNotCalledException
    //   843	856	509	android/util/SuperNotCalledException
    //   859	877	509	android/util/SuperNotCalledException
    //   119	128	512	java/lang/Exception
    //   132	152	512	java/lang/Exception
    //   156	164	512	java/lang/Exception
    //   168	177	512	java/lang/Exception
    //   181	188	512	java/lang/Exception
    //   196	203	512	java/lang/Exception
    //   207	216	512	java/lang/Exception
    //   220	234	593	java/lang/Exception
    //   239	292	593	java/lang/Exception
    //   299	306	593	java/lang/Exception
    //   310	333	593	java/lang/Exception
    //   333	389	593	java/lang/Exception
    //   393	399	593	java/lang/Exception
    //   399	418	593	java/lang/Exception
    //   422	428	593	java/lang/Exception
    //   428	458	593	java/lang/Exception
    //   458	509	593	java/lang/Exception
    //   577	590	593	java/lang/Exception
    //   650	681	593	java/lang/Exception
    //   681	712	593	java/lang/Exception
    //   712	729	593	java/lang/Exception
    //   729	769	593	java/lang/Exception
    //   769	820	593	java/lang/Exception
    //   820	840	593	java/lang/Exception
    //   843	856	593	java/lang/Exception
    //   859	877	593	java/lang/Exception
  }
  
  private void performPauseActivityIfNeeded(ActivityClientRecord paramActivityClientRecord, String paramString)
  {
    if (paramActivityClientRecord.paused) {
      return;
    }
    try
    {
      paramActivityClientRecord.activity.mCalled = false;
      this.mInstrumentation.callActivityOnPause(paramActivityClientRecord.activity);
      EventLog.writeEvent(30021, new Object[] { Integer.valueOf(UserHandle.myUserId()), paramActivityClientRecord.activity.getComponentName().getClassName(), paramString });
      if (!paramActivityClientRecord.activity.mCalled) {
        throw new SuperNotCalledException("Activity " + safeToComponentShortString(paramActivityClientRecord.intent) + " did not call through to super.onPause()");
      }
    }
    catch (SuperNotCalledException paramActivityClientRecord)
    {
      throw paramActivityClientRecord;
    }
    catch (Exception paramString)
    {
      if (!this.mInstrumentation.onException(paramActivityClientRecord.activity, paramString)) {
        throw new RuntimeException("Unable to pause activity " + safeToComponentShortString(paramActivityClientRecord.intent) + ": " + paramString.toString(), paramString);
      }
      paramActivityClientRecord.paused = true;
    }
  }
  
  /* Error */
  private void performStopActivityInner(ActivityClientRecord paramActivityClientRecord, StopInfo paramStopInfo, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +190 -> 191
    //   4: iload_3
    //   5: ifne +82 -> 87
    //   8: aload_1
    //   9: getfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   12: ifeq +75 -> 87
    //   15: aload_1
    //   16: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   19: getfield 2157	android/app/Activity:mFinished	Z
    //   22: ifeq +4 -> 26
    //   25: return
    //   26: new 699	java/lang/RuntimeException
    //   29: dup
    //   30: new 701	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   37: ldc_w 2909
    //   40: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: aload_1
    //   44: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   47: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   50: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   53: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   59: invokespecial 1834	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   62: astore 6
    //   64: ldc -114
    //   66: aload 6
    //   68: invokevirtual 2912	java/lang/RuntimeException:getMessage	()Ljava/lang/String;
    //   71: aload 6
    //   73: invokestatic 2914	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   76: pop
    //   77: ldc -114
    //   79: aload_1
    //   80: invokevirtual 2917	android/app/ActivityThread$ActivityClientRecord:getStateString	()Ljava/lang/String;
    //   83: invokestatic 1975	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   86: pop
    //   87: aload_0
    //   88: aload_1
    //   89: aload 5
    //   91: invokespecial 2168	android/app/ActivityThread:performPauseActivityIfNeeded	(Landroid/app/ActivityThread$ActivityClientRecord;Ljava/lang/String;)V
    //   94: aload_2
    //   95: ifnull +14 -> 109
    //   98: aload_2
    //   99: aload_1
    //   100: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   103: invokevirtual 2921	android/app/Activity:onCreateDescription	()Ljava/lang/CharSequence;
    //   106: putfield 2925	android/app/ActivityThread$StopInfo:description	Ljava/lang/CharSequence;
    //   109: aload_1
    //   110: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   113: getfield 2157	android/app/Activity:mFinished	Z
    //   116: ifne +20 -> 136
    //   119: iload 4
    //   121: ifeq +15 -> 136
    //   124: aload_1
    //   125: getfield 723	android/app/ActivityThread$ActivityClientRecord:state	Landroid/os/Bundle;
    //   128: ifnonnull +8 -> 136
    //   131: aload_0
    //   132: aload_1
    //   133: invokespecial 2358	android/app/ActivityThread:callCallActivityOnSaveInstanceState	(Landroid/app/ActivityThread$ActivityClientRecord;)V
    //   136: iload_3
    //   137: ifne +54 -> 191
    //   140: aload_1
    //   141: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   144: iconst_0
    //   145: invokevirtual 2423	android/app/Activity:performStop	(Z)V
    //   148: aload_1
    //   149: iconst_1
    //   150: putfield 2341	android/app/ActivityThread$ActivityClientRecord:stopped	Z
    //   153: sipush 30049
    //   156: iconst_3
    //   157: anewarray 4	java/lang/Object
    //   160: dup
    //   161: iconst_0
    //   162: invokestatic 612	android/os/UserHandle:myUserId	()I
    //   165: invokestatic 1226	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   168: aastore
    //   169: dup
    //   170: iconst_1
    //   171: aload_1
    //   172: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   175: invokevirtual 2426	android/app/Activity:getComponentName	()Landroid/content/ComponentName;
    //   178: invokevirtual 1735	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   181: aastore
    //   182: dup
    //   183: iconst_2
    //   184: aload 5
    //   186: aastore
    //   187: invokestatic 2434	android/util/EventLog:writeEvent	(I[Ljava/lang/Object;)I
    //   190: pop
    //   191: return
    //   192: astore_2
    //   193: aload_0
    //   194: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   197: aload_1
    //   198: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   201: aload_2
    //   202: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   205: ifne -96 -> 109
    //   208: new 699	java/lang/RuntimeException
    //   211: dup
    //   212: new 701	java/lang/StringBuilder
    //   215: dup
    //   216: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   219: ldc_w 2927
    //   222: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: aload_1
    //   226: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   229: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   232: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   235: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   238: ldc_w 941
    //   241: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   244: aload_2
    //   245: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   248: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   254: aload_2
    //   255: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   258: athrow
    //   259: astore_2
    //   260: aload_0
    //   261: getfield 670	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   264: aload_1
    //   265: getfield 742	android/app/ActivityThread$ActivityClientRecord:activity	Landroid/app/Activity;
    //   268: aload_2
    //   269: invokevirtual 922	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   272: ifne -124 -> 148
    //   275: new 699	java/lang/RuntimeException
    //   278: dup
    //   279: new 701	java/lang/StringBuilder
    //   282: dup
    //   283: invokespecial 702	java/lang/StringBuilder:<init>	()V
    //   286: ldc_w 2436
    //   289: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload_1
    //   293: getfield 928	android/app/ActivityThread$ActivityClientRecord:intent	Landroid/content/Intent;
    //   296: invokevirtual 934	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   299: invokevirtual 939	android/content/ComponentName:toShortString	()Ljava/lang/String;
    //   302: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: ldc_w 941
    //   308: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   311: aload_2
    //   312: invokevirtual 712	java/lang/Exception:toString	()Ljava/lang/String;
    //   315: invokevirtual 708	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: invokevirtual 713	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   321: aload_2
    //   322: invokespecial 716	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   325: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	326	0	this	ActivityThread
    //   0	326	1	paramActivityClientRecord	ActivityClientRecord
    //   0	326	2	paramStopInfo	StopInfo
    //   0	326	3	paramBoolean1	boolean
    //   0	326	4	paramBoolean2	boolean
    //   0	326	5	paramString	String
    //   62	10	6	localRuntimeException	RuntimeException
    // Exception table:
    //   from	to	target	type
    //   98	109	192	java/lang/Exception
    //   140	148	259	java/lang/Exception
  }
  
  static void printRow(PrintWriter paramPrintWriter, String paramString, Object... paramVarArgs)
  {
    paramPrintWriter.println(String.format(paramString, paramVarArgs));
  }
  
  private void reportSizeConfigurations(ActivityClientRecord paramActivityClientRecord)
  {
    Configuration[] arrayOfConfiguration = paramActivityClientRecord.activity.getResources().getSizeConfigurations();
    if (arrayOfConfiguration == null) {
      return;
    }
    SparseIntArray localSparseIntArray1 = new SparseIntArray();
    SparseIntArray localSparseIntArray2 = new SparseIntArray();
    SparseIntArray localSparseIntArray3 = new SparseIntArray();
    int i = arrayOfConfiguration.length - 1;
    while (i >= 0)
    {
      Configuration localConfiguration = arrayOfConfiguration[i];
      if (localConfiguration.screenHeightDp != 0) {
        localSparseIntArray2.put(localConfiguration.screenHeightDp, 0);
      }
      if (localConfiguration.screenWidthDp != 0) {
        localSparseIntArray1.put(localConfiguration.screenWidthDp, 0);
      }
      if (localConfiguration.smallestScreenWidthDp != 0) {
        localSparseIntArray3.put(localConfiguration.smallestScreenWidthDp, 0);
      }
      i -= 1;
    }
    try
    {
      ActivityManagerNative.getDefault().reportSizeConfigurations(paramActivityClientRecord.token, localSparseIntArray1.copyKeys(), localSparseIntArray2.copyKeys(), localSparseIntArray3.copyKeys());
      return;
    }
    catch (RemoteException paramActivityClientRecord)
    {
      throw paramActivityClientRecord.rethrowFromSystemServer();
    }
  }
  
  private static String safeToComponentShortString(Intent paramIntent)
  {
    paramIntent = paramIntent.getComponent();
    if (paramIntent == null) {
      return "[Unknown]";
    }
    return paramIntent.toShortString();
  }
  
  private void sendMessage(int paramInt, Object paramObject)
  {
    sendMessage(paramInt, paramObject, 0, 0, false);
  }
  
  private void sendMessage(int paramInt1, Object paramObject, int paramInt2)
  {
    sendMessage(paramInt1, paramObject, paramInt2, 0, false);
  }
  
  private void sendMessage(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
  {
    sendMessage(paramInt1, paramObject, paramInt2, paramInt3, false);
  }
  
  private void sendMessage(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt1;
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramObject;
    localSomeArgs.argi1 = paramInt2;
    localSomeArgs.argi2 = paramInt3;
    localSomeArgs.argi3 = paramInt4;
    localMessage.obj = localSomeArgs;
    this.mH.sendMessage(localMessage);
  }
  
  private void sendMessage(int paramInt1, Object paramObject, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt1;
    localMessage.obj = paramObject;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    if (paramBoolean) {
      localMessage.setAsynchronous(true);
    }
    this.mH.sendMessage(localMessage);
  }
  
  private void sendOPInsightLog(final Context paramContext, final String paramString)
  {
    AsyncTask.SERIAL_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        new AppTracker(paramContext).onEvent(paramString, null);
      }
    });
  }
  
  private void setupGraphicsSupport(LoadedApk paramLoadedApk, File paramFile)
  {
    if (Process.isIsolated()) {
      return;
    }
    Trace.traceBegin(64L, "setupGraphicsSupport");
    try
    {
      int i = Process.myUid();
      if (getPackageManager().getPackagesForUid(i) != null)
      {
        ThreadedRenderer.setupDiskCache(paramFile);
        RenderScriptCacheDir.setupDiskCache(paramFile);
      }
      return;
    }
    catch (RemoteException paramLoadedApk)
    {
      throw paramLoadedApk.rethrowFromSystemServer();
    }
    finally
    {
      Trace.traceEnd(64L);
    }
  }
  
  public static ActivityThread systemMain()
  {
    if (!ActivityManager.isHighEndGfx()) {
      ThreadedRenderer.disable(true);
    }
    for (;;)
    {
      ActivityThread localActivityThread = new ActivityThread();
      localActivityThread.attach(true);
      return localActivityThread;
      ThreadedRenderer.enableForegroundTrimming();
    }
  }
  
  private void updateDefaultDensity()
  {
    int i = this.mCurDefaultDisplayDpi;
    if ((!this.mDensityCompatMode) && (i != 0) && (i != DisplayMetrics.DENSITY_DEVICE))
    {
      DisplayMetrics.DENSITY_DEVICE = i;
      Bitmap.setDefaultDensity(i);
    }
  }
  
  private void updateLocaleListFromAppContext(Context paramContext, LocaleList paramLocaleList)
  {
    paramContext = paramContext.getResources().getConfiguration().getLocales().get(0);
    int j = paramLocaleList.size();
    int i = 0;
    while (i < j)
    {
      if (paramContext.equals(paramLocaleList.get(i)))
      {
        LocaleList.setDefault(paramLocaleList, 0);
        return;
      }
      i += 1;
    }
    LocaleList.setDefault(new LocaleList(paramContext, paramLocaleList));
  }
  
  private void updateVisibility(ActivityClientRecord paramActivityClientRecord, boolean paramBoolean)
  {
    View localView = paramActivityClientRecord.activity.mDecor;
    if (localView != null)
    {
      if (!paramBoolean) {
        break label84;
      }
      if (!paramActivityClientRecord.activity.mVisibleFromServer)
      {
        paramActivityClientRecord.activity.mVisibleFromServer = true;
        this.mNumVisibleActivities += 1;
        if (paramActivityClientRecord.activity.mVisibleFromClient) {
          paramActivityClientRecord.activity.makeVisible();
        }
      }
      if (paramActivityClientRecord.newConfig != null)
      {
        performConfigurationChangedForActivity(paramActivityClientRecord, paramActivityClientRecord.newConfig, true);
        paramActivityClientRecord.newConfig = null;
      }
    }
    label84:
    while (!paramActivityClientRecord.activity.mVisibleFromServer) {
      return;
    }
    paramActivityClientRecord.activity.mVisibleFromServer = false;
    this.mNumVisibleActivities -= 1;
    localView.setVisibility(4);
  }
  
  public final IContentProvider acquireExistingProvider(Context arg1, String paramString, int paramInt, boolean paramBoolean)
  {
    synchronized (this.mProviderMap)
    {
      Object localObject = new ProviderKey(paramString, paramInt);
      localObject = (ProviderClientRecord)this.mProviderMap.get(localObject);
      if (localObject == null) {
        return null;
      }
      localObject = ((ProviderClientRecord)localObject).mProvider;
      IBinder localIBinder = ((IContentProvider)localObject).asBinder();
      if (!localIBinder.isBinderAlive())
      {
        Log.i("ActivityThread", "Acquiring provider " + paramString + " for user " + paramInt + ": existing object's process dead");
        handleUnstableProviderDiedLocked(localIBinder, true);
        return null;
      }
      paramString = (ProviderRefCount)this.mProviderRefCountMap.get(localIBinder);
      if (paramString != null) {
        incProviderRefLocked(paramString, paramBoolean);
      }
      return (IContentProvider)localObject;
    }
  }
  
  public final IContentProvider acquireProvider(Context paramContext, String paramString, int paramInt, boolean paramBoolean)
  {
    Object localObject = acquireExistingProvider(paramContext, paramString, paramInt, paramBoolean);
    if (localObject != null) {
      return (IContentProvider)localObject;
    }
    try
    {
      localObject = ActivityManagerNative.getDefault().getContentProvider(getApplicationThread(), paramString, paramInt, paramBoolean);
      if (localObject == null)
      {
        Slog.e("ActivityThread", "Failed to find provider info for " + paramString);
        return null;
      }
    }
    catch (RemoteException paramContext)
    {
      throw paramContext.rethrowFromSystemServer();
    }
    return installProvider(paramContext, (IActivityManager.ContentProviderHolder)localObject, ((IActivityManager.ContentProviderHolder)localObject).info, true, ((IActivityManager.ContentProviderHolder)localObject).noReleaseNeeded, paramBoolean).provider;
  }
  
  final void appNotRespondingViaProvider(IBinder paramIBinder)
  {
    synchronized (this.mProviderMap)
    {
      paramIBinder = (ProviderRefCount)this.mProviderRefCountMap.get(paramIBinder);
      if (paramIBinder != null) {}
      try
      {
        ActivityManagerNative.getDefault().appNotRespondingViaProvider(paramIBinder.holder.connection);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        throw paramIBinder.rethrowFromSystemServer();
      }
    }
  }
  
  final Configuration applyCompatConfiguration(int paramInt)
  {
    Configuration localConfiguration = this.mConfiguration;
    if (this.mCompatConfiguration == null) {
      this.mCompatConfiguration = new Configuration();
    }
    this.mCompatConfiguration.setTo(this.mConfiguration);
    if (this.mResourcesManager.applyCompatConfigurationLocked(paramInt, this.mCompatConfiguration)) {
      localConfiguration = this.mCompatConfiguration;
    }
    return localConfiguration;
  }
  
  Configuration applyConfigCompatMainThread(int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo)
  {
    if (paramConfiguration == null) {
      return null;
    }
    Configuration localConfiguration = paramConfiguration;
    if (!paramCompatibilityInfo.supportsScreen())
    {
      this.mMainThreadConfig.setTo(paramConfiguration);
      localConfiguration = this.mMainThreadConfig;
      paramCompatibilityInfo.applyToConfiguration(paramInt, localConfiguration);
    }
    return localConfiguration;
  }
  
  public final void applyConfigurationToResources(Configuration paramConfiguration)
  {
    synchronized (this.mResourcesManager)
    {
      this.mResourcesManager.applyConfigurationToResourcesLocked(paramConfiguration, null);
      return;
    }
  }
  
  public boolean checkNeedToShowPermissionRequst()
  {
    return this.mNeedToShowPermissinRequest;
  }
  
  ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean paramBoolean, Configuration arg2)
  {
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      synchronized (this.mResourcesManager)
      {
        j = this.mAllApplications.size();
        i = 0;
        if (i < j)
        {
          localArrayList.add((ComponentCallbacks2)this.mAllApplications.get(i));
          i += 1;
          continue;
        }
        j = this.mActivities.size();
        i = 0;
        if (i < j)
        {
          ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.valueAt(i);
          Activity localActivity = localActivityClientRecord.activity;
          if (localActivity == null) {
            break label282;
          }
          Configuration localConfiguration = applyConfigCompatMainThread(this.mCurDefaultDisplayDpi, ???, localActivityClientRecord.packageInfo.getCompatibilityInfo());
          if ((localActivityClientRecord.activity.mFinished) || ((!paramBoolean) && (localActivityClientRecord.paused)))
          {
            if (localConfiguration != null) {
              localActivityClientRecord.newConfig = localConfiguration;
            }
          }
          else {
            localArrayList.add(localActivity);
          }
        }
      }
      int j = this.mServices.size();
      int i = 0;
      while (i < j)
      {
        localArrayList.add((ComponentCallbacks2)this.mServices.valueAt(i));
        i += 1;
      }
      synchronized (this.mProviderMap)
      {
        j = this.mLocalProviders.size();
        i = 0;
        while (i < j)
        {
          localArrayList.add(((ProviderClientRecord)this.mLocalProviders.valueAt(i)).mLocalProvider);
          i += 1;
        }
        return localArrayList;
      }
      label282:
      i += 1;
    }
  }
  
  final void completeRemoveProvider(ProviderRefCount paramProviderRefCount)
  {
    synchronized (this.mProviderMap)
    {
      boolean bool = paramProviderRefCount.removePending;
      if (!bool) {
        return;
      }
      paramProviderRefCount.removePending = false;
      IBinder localIBinder = paramProviderRefCount.holder.provider.asBinder();
      if ((ProviderRefCount)this.mProviderRefCountMap.get(localIBinder) == paramProviderRefCount) {
        this.mProviderRefCountMap.remove(localIBinder);
      }
      int i = this.mProviderMap.size() - 1;
      while (i >= 0)
      {
        if (((ProviderClientRecord)this.mProviderMap.valueAt(i)).mProvider.asBinder() == localIBinder) {
          this.mProviderMap.removeAt(i);
        }
        i -= 1;
      }
    }
  }
  
  void doGcIfNeeded()
  {
    this.mGcIdlerScheduled = false;
    long l = SystemClock.uptimeMillis();
    if (BinderInternal.getLastGcTime() + 5000L < l) {
      BinderInternal.forceGc("bg");
    }
  }
  
  void ensureJitEnabled()
  {
    if (!this.mJitEnabled)
    {
      this.mJitEnabled = true;
      VMRuntime.getRuntime().startJitCompilation();
    }
  }
  
  final void finishInstrumentation(int paramInt, Bundle paramBundle)
  {
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    if ((this.mProfiler.profileFile != null) && (this.mProfiler.handlingProfiling) && (this.mProfiler.profileFd == null)) {
      Debug.stopMethodTracing();
    }
    try
    {
      localIActivityManager.finishInstrumentation(this.mAppThread, paramInt, paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      throw paramBundle.rethrowFromSystemServer();
    }
  }
  
  public final Activity getActivity(IBinder paramIBinder)
  {
    return ((ActivityClientRecord)this.mActivities.get(paramIBinder)).activity;
  }
  
  public Application getApplication()
  {
    return this.mInitialApplication;
  }
  
  public ApplicationThread getApplicationThread()
  {
    return this.mAppThread;
  }
  
  final Handler getHandler()
  {
    return this.mH;
  }
  
  public Instrumentation getInstrumentation()
  {
    return this.mInstrumentation;
  }
  
  public int getIntCoreSetting(String paramString, int paramInt)
  {
    synchronized (this.mResourcesManager)
    {
      if (this.mCoreSettings != null)
      {
        paramInt = this.mCoreSettings.getInt(paramString, paramInt);
        return paramInt;
      }
      return paramInt;
    }
  }
  
  public Looper getLooper()
  {
    return this.mLooper;
  }
  
  public final LoadedApk getPackageInfo(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
  {
    boolean bool2 = false;
    if ((paramInt & 0x1) != 0) {
      bool2 = true;
    }
    boolean bool1;
    if ((bool2) && (paramApplicationInfo.uid != 0) && (paramApplicationInfo.uid != 1000)) {
      if (this.mBoundApplication != null) {
        if (UserHandle.isSameApp(paramApplicationInfo.uid, this.mBoundApplication.appInfo.uid))
        {
          bool1 = false;
          if ((!bool2) || ((0x40000000 & paramInt) == 0)) {
            break label227;
          }
        }
      }
    }
    label227:
    for (boolean bool3 = true;; bool3 = false)
    {
      if (((paramInt & 0x3) != 1) || (!bool1)) {
        break label233;
      }
      paramCompatibilityInfo = "Requesting code from " + paramApplicationInfo.packageName + " (with uid " + paramApplicationInfo.uid + ")";
      paramApplicationInfo = paramCompatibilityInfo;
      if (this.mBoundApplication != null) {
        paramApplicationInfo = paramCompatibilityInfo + " to be run in process " + this.mBoundApplication.processName + " (with uid " + this.mBoundApplication.appInfo.uid + ")";
      }
      throw new SecurityException(paramApplicationInfo);
      bool1 = true;
      break;
      bool1 = true;
      break;
      bool1 = false;
      break;
    }
    label233:
    return getPackageInfo(paramApplicationInfo, paramCompatibilityInfo, null, bool1, bool2, bool3);
  }
  
  public final LoadedApk getPackageInfo(String paramString, CompatibilityInfo paramCompatibilityInfo, int paramInt)
  {
    return getPackageInfo(paramString, paramCompatibilityInfo, paramInt, UserHandle.myUserId());
  }
  
  public final LoadedApk getPackageInfo(String paramString, CompatibilityInfo paramCompatibilityInfo, int paramInt1, int paramInt2)
  {
    int i;
    ResourcesManager localResourcesManager;
    Object localObject;
    if (UserHandle.myUserId() != paramInt2)
    {
      i = 1;
      localResourcesManager = this.mResourcesManager;
      if (i == 0) {
        break label159;
      }
      localObject = null;
      label28:
      if (localObject == null) {
        break label197;
      }
    }
    for (;;)
    {
      try
      {
        localObject = (LoadedApk)((WeakReference)localObject).get();
        if ((localObject == null) || ((((LoadedApk)localObject).mResources != null) && (!((LoadedApk)localObject).mResources.getAssets().isUpToDate()))) {
          break label209;
        }
        if ((!((LoadedApk)localObject).isSecurityViolation()) || ((paramInt1 & 0x2) != 0)) {
          break label203;
        }
        throw new SecurityException("Requesting code from " + paramString + " to be run in process " + this.mBoundApplication.processName + "/" + this.mBoundApplication.appInfo.uid);
      }
      finally {}
      i = 0;
      break;
      label159:
      if ((paramInt1 & 0x1) != 0)
      {
        localObject = (WeakReference)this.mPackages.get(paramString);
        break label28;
      }
      localObject = (WeakReference)this.mResourcePackages.get(paramString);
      break label28;
      label197:
      localObject = null;
    }
    label203:
    return (LoadedApk)localObject;
    try
    {
      label209:
      paramString = getPackageManager().getApplicationInfo(paramString, 268436480, paramInt2);
      if (paramString != null) {
        return getPackageInfo(paramString, paramCompatibilityInfo, paramInt1);
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return null;
  }
  
  public final LoadedApk getPackageInfoNoCheck(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo)
  {
    return getPackageInfo(paramApplicationInfo, paramCompatibilityInfo, null, false, true, false);
  }
  
  public Messenger getPermissionService(int paramInt)
  {
    Object localObject = null;
    try
    {
      IBinder localIBinder = ActivityManagerNative.getDefault().getPermissionServiceBinderProxy(paramInt);
      localObject = localIBinder;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    if (localObject != null)
    {
      this.mPermissionService = new Messenger((IBinder)localObject);
      return this.mPermissionService;
    }
    return null;
  }
  
  public String getProcessName()
  {
    return this.mBoundApplication.processName;
  }
  
  public String getProfileFilePath()
  {
    return this.mProfiler.profileFile;
  }
  
  public ContextImpl getSystemContext()
  {
    try
    {
      if (this.mSystemContext == null) {
        this.mSystemContext = ContextImpl.createSystemContext(this);
      }
      ContextImpl localContextImpl = this.mSystemContext;
      return localContextImpl;
    }
    finally {}
  }
  
  Resources getTopLevelResources(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, LoadedApk paramLoadedApk)
  {
    return this.mResourcesManager.getResources(paramString1, null, paramString2, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramInt, null, paramLoadedApk.getCompatibilityInfo(), paramLoadedApk.getClassLoader());
  }
  
  final void handleActivityConfigurationChanged(ActivityConfigChangeData paramActivityConfigChangeData, boolean paramBoolean)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramActivityConfigChangeData.activityToken);
    if ((localActivityClientRecord == null) || (localActivityClientRecord.activity == null)) {
      return;
    }
    localActivityClientRecord.overrideConfig = paramActivityConfigChangeData.overrideConfig;
    performConfigurationChangedForActivity(localActivityClientRecord, this.mCompatConfiguration, paramBoolean);
    this.mSomeActivitiesChanged = true;
  }
  
  public void handleCancelVisibleBehind(IBinder paramIBinder)
  {
    Object localObject = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (localObject != null)
    {
      this.mSomeActivitiesChanged = true;
      localObject = ((ActivityClientRecord)localObject).activity;
      if (((Activity)localObject).mVisibleBehind)
      {
        ((Activity)localObject).mCalled = false;
        ((Activity)localObject).onVisibleBehindCanceled();
        if (!((Activity)localObject).mCalled) {
          throw new SuperNotCalledException("Activity " + ((Activity)localObject).getLocalClassName() + " did not call through to super.onVisibleBehindCanceled()");
        }
        ((Activity)localObject).mVisibleBehind = false;
      }
    }
    try
    {
      ActivityManagerNative.getDefault().backgroundResourcesReleased(paramIBinder);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  final void handleConfigurationChanged(Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo)
  {
    ResourcesManager localResourcesManager = this.mResourcesManager;
    Object localObject = paramConfiguration;
    for (;;)
    {
      try
      {
        if (this.mPendingConfiguration != null)
        {
          localObject = paramConfiguration;
          if (!this.mPendingConfiguration.isOtherSeqNewer(paramConfiguration))
          {
            localObject = this.mPendingConfiguration;
            this.mCurDefaultDisplayDpi = ((Configuration)localObject).densityDpi;
            updateDefaultDensity();
          }
          this.mPendingConfiguration = null;
        }
        if (localObject == null) {
          return;
        }
        this.mResourcesManager.applyConfigurationToResourcesLocked((Configuration)localObject, paramCompatibilityInfo);
        updateLocaleListFromAppContext(this.mInitialApplication.getApplicationContext(), this.mResourcesManager.getConfiguration().getLocales());
        if (this.mConfiguration == null) {
          this.mConfiguration = new Configuration();
        }
        boolean bool = this.mConfiguration.isOtherSeqNewer((Configuration)localObject);
        if ((!bool) && (paramCompatibilityInfo == null)) {
          return;
        }
        int i = this.mConfiguration.updateFrom((Configuration)localObject);
        paramConfiguration = applyCompatConfiguration(this.mCurDefaultDisplayDpi);
        paramCompatibilityInfo = getSystemContext().getTheme();
        if ((paramCompatibilityInfo.getChangingConfigurations() & i) != 0) {
          paramCompatibilityInfo.rebase();
        }
        paramCompatibilityInfo = collectComponentCallbacks(false, paramConfiguration);
        freeTextLayoutCachesIfNeeded(i);
        if (paramCompatibilityInfo == null) {
          break;
        }
        int j = paramCompatibilityInfo.size();
        i = 0;
        if (i >= j) {
          break;
        }
        localObject = (ComponentCallbacks2)paramCompatibilityInfo.get(i);
        if ((localObject instanceof Activity))
        {
          localObject = (Activity)localObject;
          performConfigurationChangedForActivity((ActivityClientRecord)this.mActivities.get(((Activity)localObject).getActivityToken()), paramConfiguration, true);
          i += 1;
        }
        else
        {
          performConfigurationChanged((ComponentCallbacks2)localObject, null, paramConfiguration, null, true);
        }
      }
      finally {}
    }
  }
  
  final void handleDispatchPackageBroadcast(int paramInt, String[] paramArrayOfString)
  {
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool1 = false;
    boolean bool2 = bool1;
    switch (paramInt)
    {
    default: 
      bool2 = bool1;
    case 1: 
      ApplicationPackageManager.handlePackageBroadcast(paramInt, paramArrayOfString, bool2);
      return;
    case 0: 
    case 2: 
      if (paramInt != 0) {
        break;
      }
    }
    for (int i = 1;; i = 0)
    {
      bool2 = bool1;
      if (paramArrayOfString == null) {
        break;
      }
      for (;;)
      {
        int j;
        Object localObject1;
        synchronized (this.mResourcesManager)
        {
          j = paramArrayOfString.length - 1;
          bool1 = bool3;
          bool2 = bool1;
          localObject1 = ???;
          if (j >= 0)
          {
            bool2 = bool1;
            if (!bool1)
            {
              localObject1 = (WeakReference)this.mPackages.get(paramArrayOfString[j]);
              if ((localObject1 != null) && (((WeakReference)localObject1).get() != null)) {
                bool2 = true;
              }
            }
            else
            {
              if (i == 0) {
                break label520;
              }
              this.mPackages.remove(paramArrayOfString[j]);
              this.mResourcePackages.remove(paramArrayOfString[j]);
              break label520;
            }
            localObject1 = (WeakReference)this.mResourcePackages.get(paramArrayOfString[j]);
            bool2 = bool1;
            if (localObject1 == null) {
              continue;
            }
            localObject1 = ((WeakReference)localObject1).get();
            bool2 = bool1;
            if (localObject1 == null) {
              continue;
            }
            bool2 = true;
            continue;
          }
        }
        bool2 = bool1;
        if (paramArrayOfString == null) {
          break;
        }
        synchronized (this.mResourcesManager)
        {
          i = paramArrayOfString.length - 1;
          bool1 = bool4;
          bool2 = bool1;
          localObject1 = ???;
          if (i < 0) {
            continue;
          }
          localObject1 = (WeakReference)this.mPackages.get(paramArrayOfString[i]);
          if (localObject1 != null) {}
          ApplicationInfo localApplicationInfo;
          for (localObject1 = (LoadedApk)((WeakReference)localObject1).get();; localObject2 = null)
          {
            if (localObject1 == null) {
              break label439;
            }
            bool1 = true;
            ??? = localObject1;
            if (??? != null)
            {
              localObject1 = paramArrayOfString[i];
              try
              {
                localApplicationInfo = sPackageManager.getApplicationInfo((String)localObject1, 0, UserHandle.myUserId());
                if (this.mActivities.size() <= 0) {
                  break label494;
                }
                Iterator localIterator = this.mActivities.values().iterator();
                while (localIterator.hasNext())
                {
                  ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)localIterator.next();
                  if (localActivityClientRecord.activityInfo.applicationInfo.packageName.equals(localObject1))
                  {
                    localActivityClientRecord.activityInfo.applicationInfo = localApplicationInfo;
                    localActivityClientRecord.packageInfo = ((LoadedApk)???);
                  }
                }
                i -= 1;
              }
              catch (RemoteException localRemoteException) {}
            }
            break;
          }
          label439:
          Object localObject2 = (WeakReference)this.mResourcePackages.get(paramArrayOfString[i]);
          if (localObject2 != null) {}
          for (localObject2 = (LoadedApk)((WeakReference)localObject2).get();; localObject2 = null)
          {
            ??? = localObject2;
            if (localObject2 == null) {
              break;
            }
            bool1 = true;
            ??? = localObject2;
            break;
          }
          label494:
          ((LoadedApk)???).updateApplicationInfo(localApplicationInfo, sPackageManager.getPreviousCodePaths((String)localObject2));
        }
        label520:
        j -= 1;
        bool1 = bool2;
      }
    }
  }
  
  public void handleInstallProvider(ProviderInfo paramProviderInfo)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskWrites();
    try
    {
      installContentProviders(this.mInitialApplication, Lists.newArrayList(new ProviderInfo[] { paramProviderInfo }));
      return;
    }
    finally
    {
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
  
  final void handleLowMemory()
  {
    ArrayList localArrayList = collectComponentCallbacks(true, null);
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((ComponentCallbacks2)localArrayList.get(i)).onLowMemory();
      i += 1;
    }
    if (Process.myUid() != 1000) {
      EventLog.writeEvent(75003, SQLiteDatabase.releaseMemory());
    }
    Canvas.freeCaches();
    Canvas.freeTextLayoutCaches();
    BinderInternal.forceGc("mem");
  }
  
  public void handleOnBackgroundVisibleBehindChanged(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.onBackgroundVisibleBehindChanged(paramBoolean);
    }
  }
  
  final void handleProfilerControl(boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt)
  {
    if (paramBoolean) {
      try
      {
        this.mProfiler.setProfiler(paramProfilerInfo);
        this.mProfiler.startProfiling();
        try
        {
          paramProfilerInfo.profileFd.close();
          return;
        }
        catch (IOException paramProfilerInfo)
        {
          Slog.w("ActivityThread", "Failure closing profile fd", paramProfilerInfo);
          return;
        }
        try
        {
          paramProfilerInfo.profileFd.close();
          throw ((Throwable)localObject);
        }
        catch (IOException paramProfilerInfo)
        {
          for (;;)
          {
            Slog.w("ActivityThread", "Failure closing profile fd", paramProfilerInfo);
          }
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        localRuntimeException = localRuntimeException;
        Slog.w("ActivityThread", "Profiling failed on path " + paramProfilerInfo.profileFile + " -- can the process access this path?");
        try
        {
          paramProfilerInfo.profileFd.close();
          return;
        }
        catch (IOException paramProfilerInfo)
        {
          Slog.w("ActivityThread", "Failure closing profile fd", paramProfilerInfo);
          return;
        }
      }
      finally {}
    }
    this.mProfiler.stopProfiling();
  }
  
  public void handleRequestAssistContextExtras(RequestAssistContextExtras paramRequestAssistContextExtras)
  {
    if (this.mLastSessionId != paramRequestAssistContextExtras.sessionId)
    {
      this.mLastSessionId = paramRequestAssistContextExtras.sessionId;
      int i = this.mLastAssistStructures.size() - 1;
      while (i >= 0)
      {
        localObject1 = (AssistStructure)((WeakReference)this.mLastAssistStructures.get(i)).get();
        if (localObject1 != null) {
          ((AssistStructure)localObject1).clearSendChannel();
        }
        this.mLastAssistStructures.remove(i);
        i -= 1;
      }
    }
    Bundle localBundle = new Bundle();
    Object localObject4 = null;
    AssistContent localAssistContent = new AssistContent();
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramRequestAssistContextExtras.activityToken);
    Object localObject2 = null;
    Object localObject1 = localObject4;
    Object localObject3;
    if (localActivityClientRecord != null)
    {
      localActivityClientRecord.activity.getApplication().dispatchOnProvideAssistData(localActivityClientRecord.activity, localBundle);
      localActivityClientRecord.activity.onProvideAssistData(localBundle);
      localObject3 = localActivityClientRecord.activity.onProvideReferrer();
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (paramRequestAssistContextExtras.requestType == 1)
      {
        localObject1 = new AssistStructure(localActivityClientRecord.activity);
        localObject2 = localActivityClientRecord.activity.getIntent();
        if ((localObject2 == null) || ((localActivityClientRecord.window != null) && ((localActivityClientRecord.window.getAttributes().flags & 0x2000) != 0))) {
          break label337;
        }
        localObject2 = new Intent((Intent)localObject2);
        ((Intent)localObject2).setFlags(((Intent)localObject2).getFlags() & 0xFFFFFFBD);
        ((Intent)localObject2).removeUnsafeExtras();
        localAssistContent.setDefaultIntent((Intent)localObject2);
      }
    }
    for (;;)
    {
      localActivityClientRecord.activity.onProvideAssistContent(localAssistContent);
      localObject2 = localObject3;
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = new AssistStructure();
      }
      this.mLastAssistStructures.add(new WeakReference(localObject3));
      localObject1 = ActivityManagerNative.getDefault();
      try
      {
        ((IActivityManager)localObject1).reportAssistContextExtras(paramRequestAssistContextExtras.requestToken, localBundle, (AssistStructure)localObject3, localAssistContent, (Uri)localObject2);
        return;
      }
      catch (RemoteException paramRequestAssistContextExtras)
      {
        label337:
        throw paramRequestAssistContextExtras.rethrowFromSystemServer();
      }
      localAssistContent.setDefaultIntent(new Intent());
    }
  }
  
  final void handleResumeActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, String paramString)
  {
    if (!checkAndUpdateLifecycleSeq(paramInt, (ActivityClientRecord)this.mActivities.get(paramIBinder), "resumeActivity")) {
      return;
    }
    unscheduleGcIdler();
    this.mSomeActivitiesChanged = true;
    paramString = performResumeActivity(paramIBinder, paramBoolean1, paramString);
    if (paramString != null)
    {
      Activity localActivity = paramString.activity;
      if (paramBoolean2)
      {
        paramInt = 256;
        if (!localActivity.mStartedActivity) {
          break label224;
        }
        paramBoolean1 = false;
        label73:
        paramBoolean2 = paramBoolean1;
        if (paramBoolean1) {}
      }
      for (;;)
      {
        try
        {
          paramBoolean2 = ActivityManagerNative.getDefault().willActivityBeVisible(localActivity.getActivityToken());
          if ((paramString.window != null) || (localActivity.mFinished))
          {
            if (!paramBoolean2) {
              paramString.hideForNow = true;
            }
            cleanUpPendingRemoveWindows(paramString, false);
            if ((!paramString.activity.mFinished) && (paramBoolean2) && (paramString.activity.mDecor != null) && (!paramString.hideForNow)) {
              break label384;
            }
            if (!paramString.onlyLocalRequest)
            {
              paramString.nextIdle = this.mNewActivities;
              this.mNewActivities = paramString;
              Looper.myQueue().addIdleHandler(new Idler(null));
            }
            paramString.onlyLocalRequest = false;
            if (!paramBoolean3) {}
          }
        }
        catch (RemoteException paramIBinder)
        {
          label224:
          throw paramIBinder.rethrowFromSystemServer();
        }
        try
        {
          ActivityManagerNative.getDefault().activityResumed(paramIBinder);
          return;
        }
        catch (RemoteException paramIBinder)
        {
          Object localObject;
          WindowManager localWindowManager;
          WindowManager.LayoutParams localLayoutParams;
          ViewRootImpl localViewRootImpl;
          throw paramIBinder.rethrowFromSystemServer();
        }
        paramInt = 0;
        break;
        paramBoolean1 = true;
        break label73;
        if (paramBoolean2)
        {
          paramString.window = paramString.activity.getWindow();
          localObject = paramString.window.getDecorView();
          ((View)localObject).setVisibility(4);
          localWindowManager = localActivity.getWindowManager();
          localLayoutParams = paramString.window.getAttributes();
          localActivity.mDecor = ((View)localObject);
          localLayoutParams.type = 1;
          localLayoutParams.softInputMode |= paramInt;
          if (paramString.mPreserveWindow)
          {
            localActivity.mWindowAdded = true;
            paramString.mPreserveWindow = false;
            localViewRootImpl = ((View)localObject).getViewRootImpl();
            if (localViewRootImpl != null) {
              localViewRootImpl.notifyChildRebuilt();
            }
          }
          if ((localActivity.mVisibleFromClient) && (!localActivity.mWindowAdded))
          {
            localActivity.mWindowAdded = true;
            localWindowManager.addView((View)localObject, localLayoutParams);
            continue;
            label384:
            if (paramString.newConfig != null)
            {
              performConfigurationChangedForActivity(paramString, paramString.newConfig, true);
              paramString.newConfig = null;
            }
            localObject = paramString.window.getAttributes();
            if ((((WindowManager.LayoutParams)localObject).softInputMode & 0x100) != paramInt)
            {
              ((WindowManager.LayoutParams)localObject).softInputMode = (((WindowManager.LayoutParams)localObject).softInputMode & 0xFEFF | paramInt);
              if (paramString.activity.mVisibleFromClient) {
                localActivity.getWindowManager().updateViewLayout(paramString.window.getDecorView(), (ViewGroup.LayoutParams)localObject);
              }
            }
            paramString.activity.mVisibleFromServer = true;
            this.mNumVisibleActivities += 1;
            if (paramString.activity.mVisibleFromClient) {
              paramString.activity.makeVisible();
            }
          }
        }
      }
    }
    try
    {
      ActivityManagerNative.getDefault().finishActivity(paramIBinder, 0, null, 0);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public void handleTranslucentConversionComplete(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.onTranslucentConversionComplete(paramBoolean);
    }
  }
  
  final void handleTrimMemory(int paramInt)
  {
    if ((this.mDisableTrimMemory) && (paramInt >= 15) && (this.mBoundApplication != null) && (this.mBoundApplication.appInfo != null) && ((this.mBoundApplication.appInfo.privateFlags & 0x2000) != 0)) {
      return;
    }
    ArrayList localArrayList = collectComponentCallbacks(true, null);
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((ComponentCallbacks2)localArrayList.get(i)).onTrimMemory(paramInt);
      i += 1;
    }
    WindowManagerGlobal.getInstance().trimMemory(paramInt);
  }
  
  final void handleUnstableProviderDied(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mProviderMap)
    {
      handleUnstableProviderDiedLocked(paramIBinder, paramBoolean);
      return;
    }
  }
  
  final void handleUnstableProviderDiedLocked(IBinder paramIBinder, boolean paramBoolean)
  {
    ProviderRefCount localProviderRefCount = (ProviderRefCount)this.mProviderRefCountMap.get(paramIBinder);
    if (localProviderRefCount != null)
    {
      this.mProviderRefCountMap.remove(paramIBinder);
      int i = this.mProviderMap.size() - 1;
      while (i >= 0)
      {
        ProviderClientRecord localProviderClientRecord = (ProviderClientRecord)this.mProviderMap.valueAt(i);
        if ((localProviderClientRecord != null) && (localProviderClientRecord.mProvider.asBinder() == paramIBinder))
        {
          Slog.i("ActivityThread", "Removing dead content provider:" + localProviderClientRecord.mProvider.toString());
          this.mProviderMap.removeAt(i);
        }
        i -= 1;
      }
      if (!paramBoolean) {}
    }
    try
    {
      ActivityManagerNative.getDefault().unstableProviderDied(localProviderRefCount.holder.connection);
      return;
    }
    catch (RemoteException paramIBinder) {}
  }
  
  public void installSystemApplicationInfo(ApplicationInfo paramApplicationInfo, ClassLoader paramClassLoader)
  {
    try
    {
      getSystemContext().installSystemApplicationInfo(paramApplicationInfo, paramClassLoader);
      this.mProfiler = new Profiler();
      return;
    }
    finally
    {
      paramApplicationInfo = finally;
      throw paramApplicationInfo;
    }
  }
  
  public final void installSystemProviders(List<ProviderInfo> paramList)
  {
    if (paramList != null) {
      installContentProviders(this.mInitialApplication, paramList);
    }
  }
  
  public boolean isProfiling()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mProfiler != null)
    {
      bool1 = bool2;
      if (this.mProfiler.profileFile != null)
      {
        bool1 = bool2;
        if (this.mProfiler.profileFd == null) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public void onNewActivityOptions(IBinder paramIBinder, ActivityOptions paramActivityOptions)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.activity.onNewActivityOptions(paramActivityOptions);
    }
  }
  
  public final LoadedApk peekPackageInfo(String paramString, boolean paramBoolean)
  {
    LoadedApk localLoadedApk = null;
    localResourcesManager = this.mResourcesManager;
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        paramString = (WeakReference)this.mPackages.get(paramString);
        if (paramString != null) {
          localLoadedApk = (LoadedApk)paramString.get();
        }
        return localLoadedApk;
      }
      finally {}
      paramString = (WeakReference)this.mResourcePackages.get(paramString);
    }
  }
  
  public final ActivityClientRecord performDestroyActivity(IBinder paramIBinder, boolean paramBoolean)
  {
    return performDestroyActivity(paramIBinder, paramBoolean, 0, false);
  }
  
  void performNewIntents(IBinder paramIBinder, List<ReferrerIntent> paramList, boolean paramBoolean)
  {
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (localActivityClientRecord == null) {
      return;
    }
    if (localActivityClientRecord.paused) {}
    for (int i = 0;; i = 1)
    {
      if (i != 0)
      {
        localActivityClientRecord.activity.mTemporaryPause = true;
        this.mInstrumentation.callActivityOnPause(localActivityClientRecord.activity);
      }
      deliverNewIntents(localActivityClientRecord, paramList);
      if (i != 0)
      {
        localActivityClientRecord.activity.performResume();
        localActivityClientRecord.activity.mTemporaryPause = false;
      }
      if ((localActivityClientRecord.paused) && (paramBoolean))
      {
        performResumeActivity(paramIBinder, false, "performNewIntents");
        performPauseActivityIfNeeded(localActivityClientRecord, "performNewIntents");
      }
      return;
    }
  }
  
  final Bundle performPauseActivity(ActivityClientRecord paramActivityClientRecord, boolean paramBoolean1, boolean paramBoolean2, String arg4)
  {
    Object localObject;
    if (paramActivityClientRecord.paused)
    {
      if (paramActivityClientRecord.activity.mFinished) {
        return null;
      }
      localObject = new RuntimeException("Performing pause of activity that is not resumed: " + paramActivityClientRecord.intent.getComponent().toShortString());
      Slog.e("ActivityThread", ((RuntimeException)localObject).getMessage(), (Throwable)localObject);
    }
    if (paramBoolean1) {
      paramActivityClientRecord.activity.mFinished = true;
    }
    if (OpFeatures.isSupport(new int[] { 2 })) {
      sendOPInsightLog(paramActivityClientRecord.activity, "stop");
    }
    if ((!paramActivityClientRecord.activity.mFinished) && (paramBoolean2)) {
      callCallActivityOnSaveInstanceState(paramActivityClientRecord);
    }
    performPauseActivityIfNeeded(paramActivityClientRecord, ???);
    for (;;)
    {
      int i;
      synchronized (this.mOnPauseListeners)
      {
        localObject = (ArrayList)this.mOnPauseListeners.remove(paramActivityClientRecord.activity);
        if (localObject != null)
        {
          i = ((ArrayList)localObject).size();
          int j = 0;
          if (j >= i) {
            break;
          }
          ((OnActivityPausedListener)((ArrayList)localObject).get(j)).onPaused(paramActivityClientRecord.activity);
          j += 1;
        }
      }
    }
    if ((!paramActivityClientRecord.activity.mFinished) && (paramBoolean2)) {
      return paramActivityClientRecord.state;
    }
    return null;
  }
  
  final Bundle performPauseActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    Object localObject = null;
    ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    paramIBinder = (IBinder)localObject;
    if (localActivityClientRecord != null) {
      paramIBinder = performPauseActivity(localActivityClientRecord, paramBoolean1, paramBoolean2, paramString);
    }
    return paramIBinder;
  }
  
  final void performRestartActivity(IBinder paramIBinder)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if (paramIBinder.stopped)
    {
      paramIBinder.activity.performRestart();
      paramIBinder.stopped = false;
    }
  }
  
  public final ActivityClientRecord performResumeActivity(IBinder paramIBinder, boolean paramBoolean, String paramString)
  {
    paramIBinder = (ActivityClientRecord)this.mActivities.get(paramIBinder);
    if ((paramIBinder == null) || (paramIBinder.activity.mFinished)) {
      return paramIBinder;
    }
    if (paramBoolean)
    {
      paramIBinder.hideForNow = false;
      paramIBinder.activity.mStartedActivity = false;
    }
    for (;;)
    {
      int i;
      try
      {
        paramIBinder.activity.onStateNotSaved();
        paramIBinder.activity.mFragments.noteStateNotSaved();
        if (paramIBinder.pendingIntents != null)
        {
          deliverNewIntents(paramIBinder, paramIBinder.pendingIntents);
          paramIBinder.pendingIntents = null;
        }
        if (paramIBinder.pendingResults != null)
        {
          deliverResults(paramIBinder, paramIBinder.pendingResults);
          paramIBinder.pendingResults = null;
        }
        paramIBinder.activity.performResume();
        i = this.mRelaunchingActivities.size() - 1;
        if (i >= 0)
        {
          ActivityClientRecord localActivityClientRecord = (ActivityClientRecord)this.mRelaunchingActivities.get(i);
          if ((localActivityClientRecord.token != paramIBinder.token) || (!localActivityClientRecord.onlyLocalRequest) || (!localActivityClientRecord.startsNotResumed)) {
            break label328;
          }
          localActivityClientRecord.startsNotResumed = false;
          break label328;
        }
        EventLog.writeEvent(30022, new Object[] { Integer.valueOf(UserHandle.myUserId()), paramIBinder.activity.getComponentName().getClassName(), paramString });
        if (OpFeatures.isSupport(new int[] { 2 })) {
          sendOPInsightLog(paramIBinder.activity, "start");
        }
        paramIBinder.paused = false;
        paramIBinder.stopped = false;
        paramIBinder.state = null;
        paramIBinder.persistentState = null;
        return paramIBinder;
      }
      catch (Exception paramString) {}
      if (this.mInstrumentation.onException(paramIBinder.activity, paramString)) {
        break;
      }
      throw new RuntimeException("Unable to resume activity " + paramIBinder.intent.getComponent().toShortString() + ": " + paramString.toString(), paramString);
      label328:
      i -= 1;
    }
  }
  
  final void performStopActivity(IBinder paramIBinder, boolean paramBoolean, String paramString)
  {
    performStopActivityInner((ActivityClientRecord)this.mActivities.get(paramIBinder), null, false, paramBoolean, paramString);
  }
  
  final void performUserLeavingActivity(ActivityClientRecord paramActivityClientRecord)
  {
    this.mInstrumentation.callActivityOnUserLeaving(paramActivityClientRecord.activity);
  }
  
  public void registerOnActivityPausedListener(Activity paramActivity, OnActivityPausedListener paramOnActivityPausedListener)
  {
    synchronized (this.mOnPauseListeners)
    {
      ArrayList localArrayList2 = (ArrayList)this.mOnPauseListeners.get(paramActivity);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.mOnPauseListeners.put(paramActivity, localArrayList1);
      }
      localArrayList1.add(paramOnActivityPausedListener);
      return;
    }
  }
  
  public final boolean releaseProvider(IContentProvider arg1, boolean paramBoolean)
  {
    int j = 0;
    if (??? == null) {
      return false;
    }
    Object localObject1 = ???.asBinder();
    synchronized (this.mProviderMap)
    {
      localObject1 = (ProviderRefCount)this.mProviderRefCountMap.get(localObject1);
      if (localObject1 == null) {
        return false;
      }
      int i = 0;
      if (paramBoolean)
      {
        int k = ((ProviderRefCount)localObject1).stableCount;
        if (k == 0) {
          return false;
        }
        ((ProviderRefCount)localObject1).stableCount -= 1;
        if (((ProviderRefCount)localObject1).stableCount == 0)
        {
          i = ((ProviderRefCount)localObject1).unstableCount;
          if (i != 0) {
            break label182;
          }
          i = 1;
        }
      }
      label182:
      do
      {
        for (;;)
        {
          try
          {
            IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
            IBinder localIBinder = ((ProviderRefCount)localObject1).holder.connection;
            if (i != 0) {
              j = 1;
            }
            localIActivityManager.refContentProvider(localIBinder, -1, j);
          }
          catch (RemoteException localRemoteException2)
          {
            continue;
          }
          if (i != 0)
          {
            if (((ProviderRefCount)localObject1).removePending) {
              continue;
            }
            ((ProviderRefCount)localObject1).removePending = true;
            localObject1 = this.mH.obtainMessage(131, localObject1);
            this.mH.sendMessage((Message)localObject1);
          }
          return true;
          i = 0;
        }
        j = ((ProviderRefCount)localObject1).unstableCount;
        if (j == 0) {
          return false;
        }
        ((ProviderRefCount)localObject1).unstableCount -= 1;
      } while (((ProviderRefCount)localObject1).unstableCount != 0);
      i = ((ProviderRefCount)localObject1).stableCount;
      if (i == 0) {}
      for (j = 1;; j = 0)
      {
        i = j;
        if (j != 0) {
          break;
        }
        try
        {
          ActivityManagerNative.getDefault().refContentProvider(((ProviderRefCount)localObject1).holder.connection, 0, -1);
          i = j;
        }
        catch (RemoteException localRemoteException1)
        {
          i = j;
        }
        break;
      }
      Slog.w("ActivityThread", "Duplicate remove pending of provider " + ((ProviderRefCount)localObject1).holder.info.name);
    }
  }
  
  /* Error */
  public final void requestRelaunchActivity(IBinder paramIBinder, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, int paramInt, boolean paramBoolean1, Configuration paramConfiguration1, Configuration paramConfiguration2, boolean paramBoolean2, boolean paramBoolean3)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 249	android/app/ActivityThread:mResourcesManager	Landroid/app/ResourcesManager;
    //   4: astore 13
    //   6: aload 13
    //   8: monitorenter
    //   9: iconst_0
    //   10: istore 10
    //   12: iload 10
    //   14: aload_0
    //   15: getfield 547	android/app/ActivityThread:mRelaunchingActivities	Ljava/util/ArrayList;
    //   18: invokevirtual 2288	java/util/ArrayList:size	()I
    //   21: if_icmpge +315 -> 336
    //   24: aload_0
    //   25: getfield 547	android/app/ActivityThread:mRelaunchingActivities	Ljava/util/ArrayList;
    //   28: iload 10
    //   30: invokevirtual 2289	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   33: checkcast 14	android/app/ActivityThread$ActivityClientRecord
    //   36: astore 12
    //   38: aload 12
    //   40: getfield 812	android/app/ActivityThread$ActivityClientRecord:token	Landroid/os/IBinder;
    //   43: aload_1
    //   44: if_acmpne +273 -> 317
    //   47: aload_2
    //   48: ifnull +23 -> 71
    //   51: aload 12
    //   53: getfield 2350	android/app/ActivityThread$ActivityClientRecord:pendingResults	Ljava/util/List;
    //   56: ifnull +231 -> 287
    //   59: aload 12
    //   61: getfield 2350	android/app/ActivityThread$ActivityClientRecord:pendingResults	Ljava/util/List;
    //   64: aload_2
    //   65: invokeinterface 2361 2 0
    //   70: pop
    //   71: aload_3
    //   72: ifnull +23 -> 95
    //   75: aload 12
    //   77: getfield 2353	android/app/ActivityThread$ActivityClientRecord:pendingIntents	Ljava/util/List;
    //   80: ifnull +222 -> 302
    //   83: aload 12
    //   85: getfield 2353	android/app/ActivityThread$ActivityClientRecord:pendingIntents	Ljava/util/List;
    //   88: aload_3
    //   89: invokeinterface 2361 2 0
    //   94: pop
    //   95: aload 12
    //   97: getfield 2316	android/app/ActivityThread$ActivityClientRecord:onlyLocalRequest	Z
    //   100: istore 11
    //   102: iload 11
    //   104: ifne +17 -> 121
    //   107: iload 8
    //   109: ifeq +12 -> 121
    //   112: invokestatic 633	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   115: aload_1
    //   116: invokeinterface 2319 2 0
    //   121: aload 12
    //   123: ifnonnull +207 -> 330
    //   126: new 14	android/app/ActivityThread$ActivityClientRecord
    //   129: dup
    //   130: invokespecial 3481	android/app/ActivityThread$ActivityClientRecord:<init>	()V
    //   133: astore 12
    //   135: aload 12
    //   137: aload_1
    //   138: putfield 812	android/app/ActivityThread$ActivityClientRecord:token	Landroid/os/IBinder;
    //   141: aload 12
    //   143: aload_2
    //   144: putfield 2350	android/app/ActivityThread$ActivityClientRecord:pendingResults	Ljava/util/List;
    //   147: aload 12
    //   149: aload_3
    //   150: putfield 2353	android/app/ActivityThread$ActivityClientRecord:pendingIntents	Ljava/util/List;
    //   153: aload 12
    //   155: iload 9
    //   157: putfield 759	android/app/ActivityThread$ActivityClientRecord:mPreserveWindow	Z
    //   160: iload 8
    //   162: ifne +43 -> 205
    //   165: aload_0
    //   166: getfield 500	android/app/ActivityThread:mActivities	Landroid/util/ArrayMap;
    //   169: aload_1
    //   170: invokevirtual 1370	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   173: checkcast 14	android/app/ActivityThread$ActivityClientRecord
    //   176: astore_1
    //   177: aload_1
    //   178: ifnull +21 -> 199
    //   181: aload 12
    //   183: aload_1
    //   184: getfield 2337	android/app/ActivityThread$ActivityClientRecord:paused	Z
    //   187: putfield 2160	android/app/ActivityThread$ActivityClientRecord:startsNotResumed	Z
    //   190: aload 12
    //   192: aload_1
    //   193: getfield 822	android/app/ActivityThread$ActivityClientRecord:overrideConfig	Landroid/content/res/Configuration;
    //   196: putfield 822	android/app/ActivityThread$ActivityClientRecord:overrideConfig	Landroid/content/res/Configuration;
    //   199: aload 12
    //   201: iconst_1
    //   202: putfield 2316	android/app/ActivityThread$ActivityClientRecord:onlyLocalRequest	Z
    //   205: aload_0
    //   206: getfield 547	android/app/ActivityThread:mRelaunchingActivities	Ljava/util/ArrayList;
    //   209: aload 12
    //   211: invokevirtual 2578	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   214: pop
    //   215: aload_0
    //   216: bipush 126
    //   218: aload 12
    //   220: invokespecial 400	android/app/ActivityThread:sendMessage	(ILjava/lang/Object;)V
    //   223: aload 12
    //   225: astore_1
    //   226: iload 8
    //   228: ifeq +14 -> 242
    //   231: aload_1
    //   232: iload 5
    //   234: putfield 2160	android/app/ActivityThread$ActivityClientRecord:startsNotResumed	Z
    //   237: aload_1
    //   238: iconst_0
    //   239: putfield 2316	android/app/ActivityThread$ActivityClientRecord:onlyLocalRequest	Z
    //   242: aload 6
    //   244: ifnull +9 -> 253
    //   247: aload_1
    //   248: aload 6
    //   250: putfield 2148	android/app/ActivityThread$ActivityClientRecord:createdConfig	Landroid/content/res/Configuration;
    //   253: aload 7
    //   255: ifnull +9 -> 264
    //   258: aload_1
    //   259: aload 7
    //   261: putfield 822	android/app/ActivityThread$ActivityClientRecord:overrideConfig	Landroid/content/res/Configuration;
    //   264: aload_1
    //   265: aload_1
    //   266: getfield 2292	android/app/ActivityThread$ActivityClientRecord:pendingConfigChanges	I
    //   269: iload 4
    //   271: ior
    //   272: putfield 2292	android/app/ActivityThread$ActivityClientRecord:pendingConfigChanges	I
    //   275: aload_1
    //   276: aload_0
    //   277: invokespecial 260	android/app/ActivityThread:getLifecycleSeq	()I
    //   280: putfield 2297	android/app/ActivityThread$ActivityClientRecord:relaunchSeq	I
    //   283: aload 13
    //   285: monitorexit
    //   286: return
    //   287: aload 12
    //   289: aload_2
    //   290: putfield 2350	android/app/ActivityThread$ActivityClientRecord:pendingResults	Ljava/util/List;
    //   293: goto -222 -> 71
    //   296: astore_1
    //   297: aload 13
    //   299: monitorexit
    //   300: aload_1
    //   301: athrow
    //   302: aload 12
    //   304: aload_3
    //   305: putfield 2353	android/app/ActivityThread$ActivityClientRecord:pendingIntents	Ljava/util/List;
    //   308: goto -213 -> 95
    //   311: astore_1
    //   312: aload_1
    //   313: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   316: athrow
    //   317: iload 10
    //   319: iconst_1
    //   320: iadd
    //   321: istore 10
    //   323: goto -311 -> 12
    //   326: astore_1
    //   327: goto -30 -> 297
    //   330: aload 12
    //   332: astore_1
    //   333: goto -107 -> 226
    //   336: aconst_null
    //   337: astore 12
    //   339: goto -218 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	342	0	this	ActivityThread
    //   0	342	1	paramIBinder	IBinder
    //   0	342	2	paramList	List<ResultInfo>
    //   0	342	3	paramList1	List<ReferrerIntent>
    //   0	342	4	paramInt	int
    //   0	342	5	paramBoolean1	boolean
    //   0	342	6	paramConfiguration1	Configuration
    //   0	342	7	paramConfiguration2	Configuration
    //   0	342	8	paramBoolean2	boolean
    //   0	342	9	paramBoolean3	boolean
    //   10	312	10	i	int
    //   100	3	11	bool	boolean
    //   36	302	12	localActivityClientRecord	ActivityClientRecord
    //   4	294	13	localResourcesManager	ResourcesManager
    // Exception table:
    //   from	to	target	type
    //   12	47	296	finally
    //   51	71	296	finally
    //   75	95	296	finally
    //   95	102	296	finally
    //   112	121	296	finally
    //   135	160	296	finally
    //   165	177	296	finally
    //   181	199	296	finally
    //   199	205	296	finally
    //   205	223	296	finally
    //   231	242	296	finally
    //   247	253	296	finally
    //   258	264	296	finally
    //   264	283	296	finally
    //   287	293	296	finally
    //   302	308	296	finally
    //   312	317	296	finally
    //   112	121	311	android/os/RemoteException
    //   126	135	326	finally
  }
  
  public final ActivityInfo resolveActivityInfo(Intent paramIntent)
  {
    ActivityInfo localActivityInfo = paramIntent.resolveActivityInfo(this.mInitialApplication.getPackageManager(), 1024);
    if (localActivityInfo == null) {
      Instrumentation.checkStartActivityResult(-2, paramIntent);
    }
    return localActivityInfo;
  }
  
  final void scheduleContextCleanup(ContextImpl paramContextImpl, String paramString1, String paramString2)
  {
    ContextCleanupInfo localContextCleanupInfo = new ContextCleanupInfo();
    localContextCleanupInfo.context = paramContextImpl;
    localContextCleanupInfo.who = paramString1;
    localContextCleanupInfo.what = paramString2;
    sendMessage(119, localContextCleanupInfo);
  }
  
  void scheduleGcIdler()
  {
    if (!this.mGcIdlerScheduled)
    {
      this.mGcIdlerScheduled = true;
      Looper.myQueue().addIdleHandler(this.mGcIdler);
    }
    this.mH.removeMessages(120);
  }
  
  public final void sendActivityResult(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new ResultInfo(paramString, paramInt1, paramInt2, paramIntent));
    this.mAppThread.scheduleSendResult(paramIBinder, localArrayList);
  }
  
  public void setShowPermissionRequest()
  {
    this.mNeedToShowPermissinRequest = true;
  }
  
  public final Activity startActivityNow(Activity paramActivity, String paramString, Intent paramIntent, ActivityInfo paramActivityInfo, IBinder paramIBinder, Bundle paramBundle, Activity.NonConfigurationInstances paramNonConfigurationInstances)
  {
    ActivityClientRecord localActivityClientRecord = new ActivityClientRecord();
    localActivityClientRecord.token = paramIBinder;
    localActivityClientRecord.ident = 0;
    localActivityClientRecord.intent = paramIntent;
    localActivityClientRecord.state = paramBundle;
    localActivityClientRecord.parent = paramActivity;
    localActivityClientRecord.embeddedID = paramString;
    localActivityClientRecord.activityInfo = paramActivityInfo;
    localActivityClientRecord.lastNonConfigurationInstances = paramNonConfigurationInstances;
    return performLaunchActivity(localActivityClientRecord, null);
  }
  
  public void stopProfiling()
  {
    this.mProfiler.stopProfiling();
  }
  
  public void unregisterOnActivityPausedListener(Activity paramActivity, OnActivityPausedListener paramOnActivityPausedListener)
  {
    synchronized (this.mOnPauseListeners)
    {
      paramActivity = (ArrayList)this.mOnPauseListeners.get(paramActivity);
      if (paramActivity != null) {
        paramActivity.remove(paramOnActivityPausedListener);
      }
      return;
    }
  }
  
  void unscheduleGcIdler()
  {
    if (this.mGcIdlerScheduled)
    {
      this.mGcIdlerScheduled = false;
      Looper.myQueue().removeIdleHandler(this.mGcIdler);
    }
    this.mH.removeMessages(120);
  }
  
  static final class ActivityClientRecord
  {
    Activity activity;
    ActivityInfo activityInfo;
    CompatibilityInfo compatInfo;
    Configuration createdConfig;
    String embeddedID = null;
    boolean hideForNow = false;
    int ident;
    Intent intent;
    boolean isForward;
    Activity.NonConfigurationInstances lastNonConfigurationInstances;
    int lastProcessedSeq = 0;
    Window mPendingRemoveWindow;
    WindowManager mPendingRemoveWindowManager;
    boolean mPreserveWindow;
    Configuration newConfig;
    ActivityClientRecord nextIdle = null;
    boolean onlyLocalRequest;
    Configuration overrideConfig;
    LoadedApk packageInfo;
    Activity parent = null;
    boolean paused = false;
    int pendingConfigChanges;
    List<ReferrerIntent> pendingIntents;
    List<ResultInfo> pendingResults;
    PersistableBundle persistentState;
    ProfilerInfo profilerInfo;
    String referrer;
    int relaunchSeq = 0;
    boolean startsNotResumed;
    Bundle state;
    boolean stopped = false;
    private Configuration tmpConfig = new Configuration();
    IBinder token;
    IVoiceInteractor voiceInteractor;
    Window window;
    
    public String getStateString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ActivityClientRecord{");
      localStringBuilder.append("paused=").append(this.paused);
      localStringBuilder.append(", stopped=").append(this.stopped);
      localStringBuilder.append(", hideForNow=").append(this.hideForNow);
      localStringBuilder.append(", startsNotResumed=").append(this.startsNotResumed);
      localStringBuilder.append(", isForward=").append(this.isForward);
      localStringBuilder.append(", pendingConfigChanges=").append(this.pendingConfigChanges);
      localStringBuilder.append(", onlyLocalRequest=").append(this.onlyLocalRequest);
      localStringBuilder.append(", preserveWindow=").append(this.mPreserveWindow);
      if (this.activity != null)
      {
        localStringBuilder.append(", Activity{");
        localStringBuilder.append("resumed=").append(this.activity.mResumed);
        localStringBuilder.append(", stopped=").append(this.activity.mStopped);
        localStringBuilder.append(", finished=").append(this.activity.isFinishing());
        localStringBuilder.append(", destroyed=").append(this.activity.isDestroyed());
        localStringBuilder.append(", startedActivity=").append(this.activity.mStartedActivity);
        localStringBuilder.append(", temporaryPause=").append(this.activity.mTemporaryPause);
        localStringBuilder.append(", changingConfigurations=").append(this.activity.mChangingConfigurations);
        localStringBuilder.append(", visibleBehind=").append(this.activity.mVisibleBehind);
        localStringBuilder.append("}");
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public boolean isPersistable()
    {
      return this.activityInfo.persistableMode == 2;
    }
    
    public boolean isPreHoneycomb()
    {
      boolean bool = false;
      if (this.activity != null)
      {
        if (this.activity.getApplicationInfo().targetSdkVersion < 11) {
          bool = true;
        }
        return bool;
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder;
      if (this.intent != null)
      {
        localObject = this.intent.getComponent();
        localStringBuilder = new StringBuilder().append("ActivityRecord{").append(Integer.toHexString(System.identityHashCode(this))).append(" token=").append(this.token).append(" ");
        if (localObject != null) {
          break label81;
        }
      }
      label81:
      for (Object localObject = "no component name";; localObject = ((ComponentName)localObject).toShortString())
      {
        return (String)localObject + "}";
        localObject = null;
        break;
      }
    }
  }
  
  static final class ActivityConfigChangeData
  {
    final IBinder activityToken;
    final Configuration overrideConfig;
    
    public ActivityConfigChangeData(IBinder paramIBinder, Configuration paramConfiguration)
    {
      this.activityToken = paramIBinder;
      this.overrideConfig = paramConfiguration;
    }
  }
  
  static final class AppBindData
  {
    ApplicationInfo appInfo;
    CompatibilityInfo compatInfo;
    Configuration config;
    int debugMode;
    boolean enableBinderTracking;
    LoadedApk info;
    ProfilerInfo initProfilerInfo;
    Bundle instrumentationArgs;
    ComponentName instrumentationName;
    IUiAutomationConnection instrumentationUiAutomationConnection;
    IInstrumentationWatcher instrumentationWatcher;
    boolean persistent;
    String processName;
    List<ProviderInfo> providers;
    boolean restrictedBackupMode;
    boolean trackAllocation;
    
    public String toString()
    {
      return "AppBindData{appInfo=" + this.appInfo + "}";
    }
  }
  
  private class ApplicationThread
    extends ApplicationThreadNative
  {
    private static final String DB_INFO_FORMAT = "  %8s %8s %14s %14s  %s";
    private int mLastProcessState = -1;
    
    private ApplicationThread() {}
    
    private void dumpDatabaseInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    {
      paramFileDescriptor = new FastPrintWriter(new FileOutputStream(paramFileDescriptor));
      SQLiteDebug.dump(new PrintWriterPrinter(paramFileDescriptor), paramArrayOfString);
      paramFileDescriptor.flush();
    }
    
    private void dumpMemInfo(PrintWriter paramPrintWriter, Debug.MemoryInfo paramMemoryInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
    {
      long l1 = Debug.getNativeHeapSize() / 1024L;
      long l2 = Debug.getNativeHeapAllocatedSize() / 1024L;
      long l3 = Debug.getNativeHeapFreeSize() / 1024L;
      Object localObject = Runtime.getRuntime();
      ((Runtime)localObject).gc();
      long l4 = ((Runtime)localObject).totalMemory() / 1024L;
      long l5 = ((Runtime)localObject).freeMemory() / 1024L;
      localObject = VMDebug.countInstancesOfClasses(new Class[] { ContextImpl.class, Activity.class, WebView.class, OpenSSLSocketImpl.class }, true);
      long l6 = localObject[0];
      long l7 = localObject[1];
      long l8 = localObject[2];
      long l9 = localObject[3];
      long l10 = ViewDebug.getViewInstanceCount();
      long l11 = ViewDebug.getViewRootImplCount();
      int i = AssetManager.getGlobalAssetCount();
      int j = AssetManager.getGlobalAssetManagerCount();
      int k = Debug.getBinderLocalObjectCount();
      int m = Debug.getBinderProxyObjectCount();
      int n = Debug.getBinderDeathObjectCount();
      long l12 = Parcel.getGlobalAllocSize();
      long l13 = Parcel.getGlobalAllocCount();
      SQLiteDebug.PagerStats localPagerStats = SQLiteDebug.getDatabaseInfo();
      int i1 = Process.myPid();
      if (ActivityThread.this.mBoundApplication != null) {}
      for (localObject = ActivityThread.this.mBoundApplication.processName;; localObject = "unknown")
      {
        ActivityThread.dumpMemInfoTable(paramPrintWriter, paramMemoryInfo, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, i1, (String)localObject, l1, l2, l3, l4, l4 - l5, l5);
        if (!paramBoolean1) {
          break label550;
        }
        paramPrintWriter.print(l10);
        paramPrintWriter.print(',');
        paramPrintWriter.print(l11);
        paramPrintWriter.print(',');
        paramPrintWriter.print(l6);
        paramPrintWriter.print(',');
        paramPrintWriter.print(l7);
        paramPrintWriter.print(',');
        paramPrintWriter.print(i);
        paramPrintWriter.print(',');
        paramPrintWriter.print(j);
        paramPrintWriter.print(',');
        paramPrintWriter.print(k);
        paramPrintWriter.print(',');
        paramPrintWriter.print(m);
        paramPrintWriter.print(',');
        paramPrintWriter.print(n);
        paramPrintWriter.print(',');
        paramPrintWriter.print(l9);
        paramPrintWriter.print(',');
        paramPrintWriter.print(localPagerStats.memoryUsed / 1024);
        paramPrintWriter.print(',');
        paramPrintWriter.print(localPagerStats.memoryUsed / 1024);
        paramPrintWriter.print(',');
        paramPrintWriter.print(localPagerStats.pageCacheOverflow / 1024);
        paramPrintWriter.print(',');
        paramPrintWriter.print(localPagerStats.largestMemAlloc / 1024);
        i = 0;
        while (i < localPagerStats.dbStats.size())
        {
          paramMemoryInfo = (SQLiteDebug.DbStats)localPagerStats.dbStats.get(i);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.dbName);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.pageSize);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.dbSize);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.lookaside);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.cache);
          paramPrintWriter.print(',');
          paramPrintWriter.print(paramMemoryInfo.cache);
          i += 1;
        }
      }
      paramPrintWriter.println();
      return;
      label550:
      paramPrintWriter.println(" ");
      paramPrintWriter.println(" Objects");
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "Views:", Long.valueOf(l10), "ViewRootImpl:", Long.valueOf(l11) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "AppContexts:", Long.valueOf(l6), "Activities:", Long.valueOf(l7) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "Assets:", Integer.valueOf(i), "AssetManagers:", Integer.valueOf(j) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "Local Binders:", Integer.valueOf(k), "Proxy Binders:", Integer.valueOf(m) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "Parcel memory:", Long.valueOf(l12 / 1024L), "Parcel count:", Long.valueOf(l13) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "Death Recipients:", Integer.valueOf(n), "OpenSSL Sockets:", Long.valueOf(l9) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d", new Object[] { "WebViews:", Long.valueOf(l8) });
      paramPrintWriter.println(" ");
      paramPrintWriter.println(" SQL");
      ActivityThread.printRow(paramPrintWriter, "%21s %8d", new Object[] { "MEMORY_USED:", Integer.valueOf(localPagerStats.memoryUsed / 1024) });
      ActivityThread.printRow(paramPrintWriter, "%21s %8d %21s %8d", new Object[] { "PAGECACHE_OVERFLOW:", Integer.valueOf(localPagerStats.pageCacheOverflow / 1024), "MALLOC_SIZE:", Integer.valueOf(localPagerStats.largestMemAlloc / 1024) });
      paramPrintWriter.println(" ");
      j = localPagerStats.dbStats.size();
      if (j > 0)
      {
        paramPrintWriter.println(" DATABASES");
        ActivityThread.printRow(paramPrintWriter, "  %8s %8s %14s %14s  %s", new Object[] { "pgsz", "dbsz", "Lookaside(b)", "cache", "Dbname" });
        i = 0;
        if (i < j)
        {
          SQLiteDebug.DbStats localDbStats = (SQLiteDebug.DbStats)localPagerStats.dbStats.get(i);
          if (localDbStats.pageSize > 0L)
          {
            paramMemoryInfo = String.valueOf(localDbStats.pageSize);
            label1023:
            if (localDbStats.dbSize <= 0L) {
              break label1116;
            }
            localObject = String.valueOf(localDbStats.dbSize);
            label1043:
            if (localDbStats.lookaside <= 0) {
              break label1123;
            }
          }
          label1116:
          label1123:
          for (String str = String.valueOf(localDbStats.lookaside);; str = " ")
          {
            ActivityThread.printRow(paramPrintWriter, "  %8s %8s %14s %14s  %s", new Object[] { paramMemoryInfo, localObject, str, localDbStats.cache, localDbStats.dbName });
            i += 1;
            break;
            paramMemoryInfo = " ";
            break label1023;
            localObject = " ";
            break label1043;
          }
        }
      }
      paramMemoryInfo = AssetManager.getAssetAllocations();
      if (paramMemoryInfo != null)
      {
        paramPrintWriter.println(" ");
        paramPrintWriter.println(" Asset Allocations");
        paramPrintWriter.print(paramMemoryInfo);
      }
      if (paramBoolean5) {
        if ((ActivityThread.this.mBoundApplication == null) || ((ActivityThread.this.mBoundApplication.appInfo.flags & 0x2) == 0)) {
          break label1215;
        }
      }
      label1215:
      for (paramBoolean1 = true;; paramBoolean1 = Build.IS_DEBUGGABLE)
      {
        paramPrintWriter.println(" ");
        paramPrintWriter.println(" Unreachable memory");
        paramPrintWriter.print(Debug.getUnreachableMemory(100, paramBoolean1));
        return;
      }
    }
    
    private void updatePendingConfiguration(Configuration paramConfiguration)
    {
      synchronized (ActivityThread.-get0(ActivityThread.this))
      {
        if ((ActivityThread.this.mPendingConfiguration == null) || (ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(paramConfiguration))) {
          ActivityThread.this.mPendingConfiguration = paramConfiguration;
        }
        return;
      }
    }
    
    public final void bindApplication(String paramString, ApplicationInfo paramApplicationInfo, List<ProviderInfo> paramList, ComponentName paramComponentName, ProfilerInfo paramProfilerInfo, Bundle paramBundle1, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, Map<String, IBinder> paramMap, Bundle paramBundle2)
    {
      if ((paramMap == null) || (EmbryoApp.isEmbryo())) {}
      for (;;)
      {
        setCoreSettings(paramBundle2);
        paramMap = new ActivityThread.AppBindData();
        paramMap.processName = paramString;
        paramMap.appInfo = paramApplicationInfo;
        paramMap.providers = paramList;
        paramMap.instrumentationName = paramComponentName;
        paramMap.instrumentationArgs = paramBundle1;
        paramMap.instrumentationWatcher = paramIInstrumentationWatcher;
        paramMap.instrumentationUiAutomationConnection = paramIUiAutomationConnection;
        paramMap.debugMode = paramInt;
        paramMap.enableBinderTracking = paramBoolean1;
        paramMap.trackAllocation = paramBoolean2;
        paramMap.restrictedBackupMode = paramBoolean3;
        paramMap.persistent = paramBoolean4;
        paramMap.config = paramConfiguration;
        paramMap.compatInfo = paramCompatibilityInfo;
        paramMap.initProfilerInfo = paramProfilerInfo;
        ActivityThread.-wrap31(ActivityThread.this, 110, paramMap);
        return;
        ServiceManager.initServiceCache(paramMap);
      }
    }
    
    public void clearDnsCache()
    {
      InetAddress.clearDnsCache();
      NetworkEventDispatcher.getInstance().onNetworkConfigurationChanged();
    }
    
    public void dispatchPackageBroadcast(int paramInt, String[] paramArrayOfString)
    {
      ActivityThread.-wrap32(ActivityThread.this, 133, paramArrayOfString, paramInt);
    }
    
    public void dumpActivity(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String paramString, String[] paramArrayOfString)
    {
      ActivityThread.DumpComponentInfo localDumpComponentInfo = new ActivityThread.DumpComponentInfo();
      try
      {
        localDumpComponentInfo.fd = ParcelFileDescriptor.dup(paramFileDescriptor);
        localDumpComponentInfo.token = paramIBinder;
        localDumpComponentInfo.prefix = paramString;
        localDumpComponentInfo.args = paramArrayOfString;
        ActivityThread.-wrap33(ActivityThread.this, 136, localDumpComponentInfo, 0, 0, true);
        return;
      }
      catch (IOException paramFileDescriptor)
      {
        Slog.w("ActivityThread", "dumpActivity failed", paramFileDescriptor);
      }
    }
    
    public void dumpDbInfo(FileDescriptor paramFileDescriptor, final String[] paramArrayOfString)
    {
      if (ActivityThread.this.mSystemThread) {
        try
        {
          final ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.dup(paramFileDescriptor);
          AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
          {
            public void run()
            {
              try
              {
                ActivityThread.ApplicationThread.-wrap0(ActivityThread.ApplicationThread.this, localParcelFileDescriptor.getFileDescriptor(), paramArrayOfString);
                return;
              }
              finally
              {
                IoUtils.closeQuietly(localParcelFileDescriptor);
              }
            }
          });
          return;
        }
        catch (IOException paramArrayOfString)
        {
          Log.w("ActivityThread", "Could not dup FD " + paramFileDescriptor.getInt$());
          return;
        }
      }
      dumpDatabaseInfo(paramFileDescriptor, paramArrayOfString);
    }
    
    public void dumpGfxInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    {
      ActivityThread.-wrap1(ActivityThread.this, paramFileDescriptor);
      WindowManagerGlobal.getInstance().dumpGfxInfo(paramFileDescriptor, paramArrayOfString);
    }
    
    public void dumpHeap(boolean paramBoolean, String paramString, ParcelFileDescriptor paramParcelFileDescriptor)
    {
      ActivityThread.DumpHeapData localDumpHeapData = new ActivityThread.DumpHeapData();
      localDumpHeapData.path = paramString;
      localDumpHeapData.fd = paramParcelFileDescriptor;
      paramString = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap33(paramString, 135, localDumpHeapData, i, 0, true);
        return;
      }
    }
    
    public void dumpMemInfo(FileDescriptor paramFileDescriptor, Debug.MemoryInfo paramMemoryInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, String[] paramArrayOfString)
    {
      paramFileDescriptor = new FastPrintWriter(new FileOutputStream(paramFileDescriptor));
      try
      {
        dumpMemInfo(paramFileDescriptor, paramMemoryInfo, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5);
        return;
      }
      finally
      {
        paramFileDescriptor.flush();
      }
    }
    
    public void dumpProvider(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    {
      ActivityThread.DumpComponentInfo localDumpComponentInfo = new ActivityThread.DumpComponentInfo();
      try
      {
        localDumpComponentInfo.fd = ParcelFileDescriptor.dup(paramFileDescriptor);
        localDumpComponentInfo.token = paramIBinder;
        localDumpComponentInfo.args = paramArrayOfString;
        ActivityThread.-wrap33(ActivityThread.this, 141, localDumpComponentInfo, 0, 0, true);
        return;
      }
      catch (IOException paramFileDescriptor)
      {
        Slog.w("ActivityThread", "dumpProvider failed", paramFileDescriptor);
      }
    }
    
    public void dumpService(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    {
      ActivityThread.DumpComponentInfo localDumpComponentInfo = new ActivityThread.DumpComponentInfo();
      try
      {
        localDumpComponentInfo.fd = ParcelFileDescriptor.dup(paramFileDescriptor);
        localDumpComponentInfo.token = paramIBinder;
        localDumpComponentInfo.args = paramArrayOfString;
        ActivityThread.-wrap33(ActivityThread.this, 123, localDumpComponentInfo, 0, 0, true);
        return;
      }
      catch (IOException paramFileDescriptor)
      {
        Slog.w("ActivityThread", "dumpService failed", paramFileDescriptor);
      }
    }
    
    public void notifyCleartextNetwork(byte[] paramArrayOfByte)
    {
      if (StrictMode.vmCleartextNetworkEnabled()) {
        StrictMode.onCleartextNetworkDetected(paramArrayOfByte);
      }
    }
    
    public void processInBackground()
    {
      ActivityThread.this.mH.removeMessages(120);
      ActivityThread.this.mH.sendMessage(ActivityThread.this.mH.obtainMessage(120));
    }
    
    public void profilerControl(boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap34(localActivityThread, 127, paramProfilerInfo, i, paramInt);
        return;
      }
    }
    
    public void requestAssistContextExtras(IBinder paramIBinder1, IBinder paramIBinder2, int paramInt1, int paramInt2)
    {
      ActivityThread.RequestAssistContextExtras localRequestAssistContextExtras = new ActivityThread.RequestAssistContextExtras();
      localRequestAssistContextExtras.activityToken = paramIBinder1;
      localRequestAssistContextExtras.requestToken = paramIBinder2;
      localRequestAssistContextExtras.requestType = paramInt1;
      localRequestAssistContextExtras.sessionId = paramInt2;
      ActivityThread.-wrap31(ActivityThread.this, 143, localRequestAssistContextExtras);
    }
    
    public void scheduleActivityConfigurationChanged(IBinder paramIBinder, Configuration paramConfiguration, boolean paramBoolean)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      paramIBinder = new ActivityThread.ActivityConfigChangeData(paramIBinder, paramConfiguration);
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 125, paramIBinder, i);
        return;
      }
    }
    
    public void scheduleBackgroundVisibleBehindChanged(IBinder paramIBinder, boolean paramBoolean)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 148, paramIBinder, i);
        return;
      }
    }
    
    public final void scheduleBindService(IBinder paramIBinder, Intent paramIntent, boolean paramBoolean, int paramInt)
    {
      updateProcessState(paramInt, false);
      ActivityThread.BindServiceData localBindServiceData = new ActivityThread.BindServiceData();
      localBindServiceData.token = paramIBinder;
      localBindServiceData.intent = paramIntent;
      localBindServiceData.rebind = paramBoolean;
      ActivityThread.-wrap31(ActivityThread.this, 121, localBindServiceData);
    }
    
    public void scheduleCancelVisibleBehind(IBinder paramIBinder)
    {
      ActivityThread.-wrap31(ActivityThread.this, 147, paramIBinder);
    }
    
    public void scheduleConfigurationChanged(Configuration paramConfiguration)
    {
      updatePendingConfiguration(paramConfiguration);
      ActivityThread.-wrap31(ActivityThread.this, 118, paramConfiguration);
    }
    
    public void scheduleCrash(String paramString)
    {
      ActivityThread.-wrap31(ActivityThread.this, 134, paramString);
    }
    
    public final void scheduleCreateBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    {
      ActivityThread.CreateBackupAgentData localCreateBackupAgentData = new ActivityThread.CreateBackupAgentData();
      localCreateBackupAgentData.appInfo = paramApplicationInfo;
      localCreateBackupAgentData.compatInfo = paramCompatibilityInfo;
      localCreateBackupAgentData.backupMode = paramInt;
      ActivityThread.-wrap31(ActivityThread.this, 128, localCreateBackupAgentData);
    }
    
    public final void scheduleCreateService(IBinder paramIBinder, ServiceInfo paramServiceInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    {
      updateProcessState(paramInt, false);
      ActivityThread.CreateServiceData localCreateServiceData = new ActivityThread.CreateServiceData();
      localCreateServiceData.token = paramIBinder;
      localCreateServiceData.info = paramServiceInfo;
      localCreateServiceData.compatInfo = paramCompatibilityInfo;
      ActivityThread.-wrap31(ActivityThread.this, 114, localCreateServiceData);
    }
    
    public final void scheduleDestroyActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap34(localActivityThread, 109, paramIBinder, i, paramInt);
        return;
      }
    }
    
    public final void scheduleDestroyBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo)
    {
      ActivityThread.CreateBackupAgentData localCreateBackupAgentData = new ActivityThread.CreateBackupAgentData();
      localCreateBackupAgentData.appInfo = paramApplicationInfo;
      localCreateBackupAgentData.compatInfo = paramCompatibilityInfo;
      ActivityThread.-wrap31(ActivityThread.this, 129, localCreateBackupAgentData);
    }
    
    public void scheduleEnterAnimationComplete(IBinder paramIBinder)
    {
      ActivityThread.-wrap31(ActivityThread.this, 149, paramIBinder);
    }
    
    public final void scheduleExit()
    {
      ActivityThread.-wrap31(ActivityThread.this, 111, null);
    }
    
    public void scheduleInstallProvider(ProviderInfo paramProviderInfo)
    {
      ActivityThread.-wrap31(ActivityThread.this, 145, paramProviderInfo);
    }
    
    public final void scheduleLaunchActivity(Intent paramIntent, IBinder paramIBinder, int paramInt1, ActivityInfo paramActivityInfo, Configuration paramConfiguration1, Configuration paramConfiguration2, CompatibilityInfo paramCompatibilityInfo, String paramString, IVoiceInteractor paramIVoiceInteractor, int paramInt2, Bundle paramBundle, PersistableBundle paramPersistableBundle, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, boolean paramBoolean1, boolean paramBoolean2, ProfilerInfo paramProfilerInfo)
    {
      updateProcessState(paramInt2, false);
      ActivityThread.ActivityClientRecord localActivityClientRecord = new ActivityThread.ActivityClientRecord();
      localActivityClientRecord.token = paramIBinder;
      localActivityClientRecord.ident = paramInt1;
      localActivityClientRecord.intent = paramIntent;
      localActivityClientRecord.referrer = paramString;
      localActivityClientRecord.voiceInteractor = paramIVoiceInteractor;
      localActivityClientRecord.activityInfo = paramActivityInfo;
      localActivityClientRecord.compatInfo = paramCompatibilityInfo;
      localActivityClientRecord.state = paramBundle;
      localActivityClientRecord.persistentState = paramPersistableBundle;
      localActivityClientRecord.pendingResults = paramList;
      localActivityClientRecord.pendingIntents = paramList1;
      localActivityClientRecord.startsNotResumed = paramBoolean1;
      localActivityClientRecord.isForward = paramBoolean2;
      localActivityClientRecord.profilerInfo = paramProfilerInfo;
      localActivityClientRecord.overrideConfig = paramConfiguration2;
      updatePendingConfiguration(paramConfiguration1);
      ActivityThread.-wrap31(ActivityThread.this, 100, localActivityClientRecord);
    }
    
    public void scheduleLocalVoiceInteractionStarted(IBinder paramIBinder, IVoiceInteractor paramIVoiceInteractor)
      throws RemoteException
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramIBinder;
      localSomeArgs.arg2 = paramIVoiceInteractor;
      ActivityThread.-wrap31(ActivityThread.this, 154, localSomeArgs);
    }
    
    public void scheduleLowMemory()
    {
      ActivityThread.-wrap31(ActivityThread.this, 124, null);
    }
    
    public void scheduleMultiWindowModeChanged(IBinder paramIBinder, boolean paramBoolean)
      throws RemoteException
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 152, paramIBinder, i);
        return;
      }
    }
    
    public final void scheduleNewIntent(List<ReferrerIntent> paramList, IBinder paramIBinder, boolean paramBoolean)
    {
      ActivityThread.NewIntentData localNewIntentData = new ActivityThread.NewIntentData();
      localNewIntentData.intents = paramList;
      localNewIntentData.token = paramIBinder;
      localNewIntentData.andPause = paramBoolean;
      ActivityThread.-wrap31(ActivityThread.this, 112, localNewIntentData);
    }
    
    public void scheduleOnNewActivityOptions(IBinder paramIBinder, ActivityOptions paramActivityOptions)
    {
      ActivityThread.-wrap31(ActivityThread.this, 146, new Pair(paramIBinder, paramActivityOptions));
    }
    
    public final void schedulePauseActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
    {
      int k = 0;
      int m = ActivityThread.-wrap0(ActivityThread.this);
      ActivityThread localActivityThread = ActivityThread.this;
      int i;
      if (paramBoolean1)
      {
        i = 102;
        if (!paramBoolean2) {
          break label66;
        }
      }
      label66:
      for (int j = 1;; j = 0)
      {
        if (paramBoolean3) {
          k = 2;
        }
        ActivityThread.-wrap35(localActivityThread, i, paramIBinder, j | k, paramInt, m);
        return;
        i = 101;
        break;
      }
    }
    
    public void schedulePictureInPictureModeChanged(IBinder paramIBinder, boolean paramBoolean)
      throws RemoteException
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 153, paramIBinder, i);
        return;
      }
    }
    
    public void schedulePreload(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration, Map<String, IBinder> paramMap)
    {
      ActivityThread.-set0(paramApplicationInfo.packageName);
      if (paramMap != null)
      {
        ServiceManager.initServiceCache(paramMap);
        EmbryoApp.setMyself();
      }
      paramMap = EmbryoApp.getInstance();
      ActivityThread.-get0(ActivityThread.this).applyConfigurationToResourcesLocked(paramConfiguration, paramCompatibilityInfo);
      paramApplicationInfo = ActivityThread.this.getPackageInfoNoCheck(paramApplicationInfo, paramCompatibilityInfo);
      paramMap.attach(ContextImpl.createAppContext(ActivityThread.this, paramApplicationInfo));
      ActivityThread.this.mH.post(paramMap.getRunnable());
    }
    
    public final void scheduleReceiver(Intent paramIntent, ActivityInfo paramActivityInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4)
    {
      updateProcessState(paramInt3, false);
      paramIntent = new ActivityThread.ReceiverData(paramIntent, paramInt1, paramString, paramBundle, paramBoolean, false, ActivityThread.this.mAppThread.asBinder(), paramInt2, paramInt4);
      paramIntent.info = paramActivityInfo;
      paramIntent.compatInfo = paramCompatibilityInfo;
      ActivityThread.-wrap31(ActivityThread.this, 113, paramIntent);
    }
    
    public void scheduleRegisteredReceiver(IIntentReceiver paramIIntentReceiver, Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
      throws RemoteException
    {
      updateProcessState(paramInt3, false);
      paramIIntentReceiver.performReceive(paramIntent, paramInt1, paramString, paramBundle, paramBoolean1, paramBoolean2, paramInt2);
    }
    
    public final void scheduleRelaunchActivity(IBinder paramIBinder, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, int paramInt, boolean paramBoolean1, Configuration paramConfiguration1, Configuration paramConfiguration2, boolean paramBoolean2)
    {
      ActivityThread.this.requestRelaunchActivity(paramIBinder, paramList, paramList1, paramInt, paramBoolean1, paramConfiguration1, paramConfiguration2, true, paramBoolean2);
    }
    
    public final void scheduleResumeActivity(IBinder paramIBinder, int paramInt, boolean paramBoolean, Bundle paramBundle)
    {
      int i = ActivityThread.-wrap0(ActivityThread.this);
      updateProcessState(paramInt, false);
      paramBundle = ActivityThread.this;
      if (paramBoolean) {}
      for (paramInt = 1;; paramInt = 0)
      {
        ActivityThread.-wrap35(paramBundle, 107, paramIBinder, paramInt, 0, i);
        return;
      }
    }
    
    public final void scheduleSendResult(IBinder paramIBinder, List<ResultInfo> paramList)
    {
      ActivityThread.ResultData localResultData = new ActivityThread.ResultData();
      localResultData.token = paramIBinder;
      localResultData.results = paramList;
      ActivityThread.-wrap31(ActivityThread.this, 108, localResultData);
    }
    
    public final void scheduleServiceArgs(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2, Intent paramIntent)
    {
      ActivityThread.ServiceArgsData localServiceArgsData = new ActivityThread.ServiceArgsData();
      localServiceArgsData.token = paramIBinder;
      localServiceArgsData.taskRemoved = paramBoolean;
      localServiceArgsData.startId = paramInt1;
      localServiceArgsData.flags = paramInt2;
      localServiceArgsData.args = paramIntent;
      ActivityThread.-wrap31(ActivityThread.this, 115, localServiceArgsData);
    }
    
    public final void scheduleSleeping(IBinder paramIBinder, boolean paramBoolean)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 137, paramIBinder, i);
        return;
      }
    }
    
    public final void scheduleStopActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    {
      int j = ActivityThread.-wrap0(ActivityThread.this);
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 103;; i = 104)
      {
        ActivityThread.-wrap35(localActivityThread, i, paramIBinder, 0, paramInt, j);
        return;
      }
    }
    
    public final void scheduleStopService(IBinder paramIBinder)
    {
      ActivityThread.-wrap31(ActivityThread.this, 116, paramIBinder);
    }
    
    public final void scheduleSuicide()
    {
      ActivityThread.-wrap31(ActivityThread.this, 130, null);
    }
    
    public void scheduleTranslucentConversionComplete(IBinder paramIBinder, boolean paramBoolean)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        ActivityThread.-wrap32(localActivityThread, 144, paramIBinder, i);
        return;
      }
    }
    
    public void scheduleTrimMemory(int paramInt)
    {
      ActivityThread.-wrap32(ActivityThread.this, 140, null, paramInt);
    }
    
    public final void scheduleUnbindService(IBinder paramIBinder, Intent paramIntent)
    {
      ActivityThread.BindServiceData localBindServiceData = new ActivityThread.BindServiceData();
      localBindServiceData.token = paramIBinder;
      localBindServiceData.intent = paramIntent;
      ActivityThread.-wrap31(ActivityThread.this, 122, localBindServiceData);
    }
    
    public final void scheduleWindowVisibility(IBinder paramIBinder, boolean paramBoolean)
    {
      ActivityThread localActivityThread = ActivityThread.this;
      if (paramBoolean) {}
      for (int i = 105;; i = 106)
      {
        ActivityThread.-wrap31(localActivityThread, i, paramIBinder);
        return;
      }
    }
    
    public void setCoreSettings(Bundle paramBundle)
    {
      ActivityThread.-wrap31(ActivityThread.this, 138, paramBundle);
    }
    
    public void setHttpProxy(String paramString1, String paramString2, String paramString3, Uri paramUri)
    {
      ConnectivityManager localConnectivityManager = ConnectivityManager.from(ActivityThread.this.getSystemContext());
      if (localConnectivityManager.getBoundNetworkForProcess() != null)
      {
        Proxy.setHttpProxySystemProperty(localConnectivityManager.getDefaultProxy());
        return;
      }
      Proxy.setHttpProxySystemProperty(paramString1, paramString2, paramString3, paramUri);
    }
    
    public void setProcessState(int paramInt)
    {
      updateProcessState(paramInt, true);
    }
    
    public void setSchedulingGroup(int paramInt)
    {
      try
      {
        Process.setProcessGroup(Process.myPid(), paramInt);
        return;
      }
      catch (Exception localException)
      {
        Slog.w("ActivityThread", "Failed setting process group to " + paramInt, localException);
      }
    }
    
    public void startBinderTracking()
    {
      ActivityThread.-wrap31(ActivityThread.this, 150, null);
    }
    
    public void stopBinderTrackingAndDump(FileDescriptor paramFileDescriptor)
    {
      try
      {
        ActivityThread.-wrap31(ActivityThread.this, 151, ParcelFileDescriptor.dup(paramFileDescriptor));
        return;
      }
      catch (IOException paramFileDescriptor) {}
    }
    
    public void unstableProviderDied(IBinder paramIBinder)
    {
      ActivityThread.-wrap31(ActivityThread.this, 142, paramIBinder);
    }
    
    public void updatePackageCompatibilityInfo(String paramString, CompatibilityInfo paramCompatibilityInfo)
    {
      ActivityThread.UpdateCompatibilityData localUpdateCompatibilityData = new ActivityThread.UpdateCompatibilityData();
      localUpdateCompatibilityData.pkg = paramString;
      localUpdateCompatibilityData.info = paramCompatibilityInfo;
      ActivityThread.-wrap31(ActivityThread.this, 139, localUpdateCompatibilityData);
    }
    
    public void updateProcessState(int paramInt, boolean paramBoolean)
    {
      try
      {
        if (this.mLastProcessState != paramInt)
        {
          this.mLastProcessState = paramInt;
          int i = 1;
          if (paramInt <= 6) {
            i = 0;
          }
          VMRuntime.getRuntime().updateProcessState(i);
        }
        return;
      }
      finally {}
    }
    
    public final void updateTimePrefs(boolean paramBoolean)
    {
      DateFormat.set24HourTimePref(paramBoolean);
    }
    
    public void updateTimeZone()
    {
      TimeZone.setDefault(null);
    }
  }
  
  static final class BindServiceData
  {
    Intent intent;
    boolean rebind;
    IBinder token;
    
    public String toString()
    {
      return "BindServiceData{token=" + this.token + " intent=" + this.intent + "}";
    }
  }
  
  static final class ContextCleanupInfo
  {
    ContextImpl context;
    String what;
    String who;
  }
  
  static final class CreateBackupAgentData
  {
    ApplicationInfo appInfo;
    int backupMode;
    CompatibilityInfo compatInfo;
    
    public String toString()
    {
      return "CreateBackupAgentData{appInfo=" + this.appInfo + " backupAgent=" + this.appInfo.backupAgentName + " mode=" + this.backupMode + "}";
    }
  }
  
  static final class CreateServiceData
  {
    CompatibilityInfo compatInfo;
    ServiceInfo info;
    Intent intent;
    IBinder token;
    
    public String toString()
    {
      return "CreateServiceData{token=" + this.token + " className=" + this.info.name + " packageName=" + this.info.packageName + " intent=" + this.intent + "}";
    }
  }
  
  private class DropBoxReporter
    implements DropBox.Reporter
  {
    private DropBoxManager dropBox;
    
    public DropBoxReporter() {}
    
    private void ensureInitialized()
    {
      try
      {
        if (this.dropBox == null) {
          this.dropBox = ((DropBoxManager)ActivityThread.this.getSystemContext().getSystemService("dropbox"));
        }
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void addData(String paramString, byte[] paramArrayOfByte, int paramInt)
    {
      ensureInitialized();
      this.dropBox.addData(paramString, paramArrayOfByte, paramInt);
    }
    
    public void addText(String paramString1, String paramString2)
    {
      ensureInitialized();
      this.dropBox.addText(paramString1, paramString2);
    }
  }
  
  static final class DumpComponentInfo
  {
    String[] args;
    ParcelFileDescriptor fd;
    String prefix;
    IBinder token;
  }
  
  static final class DumpHeapData
  {
    ParcelFileDescriptor fd;
    String path;
  }
  
  private static class EventLoggingReporter
    implements EventLogger.Reporter
  {
    public void report(int paramInt, Object... paramVarArgs)
    {
      EventLog.writeEvent(paramInt, paramVarArgs);
    }
  }
  
  final class GcIdler
    implements MessageQueue.IdleHandler
  {
    GcIdler() {}
    
    public final boolean queueIdle()
    {
      ActivityThread.this.doGcIfNeeded();
      return false;
    }
  }
  
  private class H
    extends Handler
  {
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int BACKGROUND_VISIBLE_BEHIND_CHANGED = 148;
    public static final int BIND_APPLICATION = 110;
    public static final int BIND_SERVICE = 121;
    public static final int CANCEL_VISIBLE_BEHIND = 147;
    public static final int CLEAN_UP_CONTEXT = 119;
    public static final int CONFIGURATION_CHANGED = 118;
    public static final int CREATE_BACKUP_AGENT = 128;
    public static final int CREATE_SERVICE = 114;
    public static final int DESTROY_ACTIVITY = 109;
    public static final int DESTROY_BACKUP_AGENT = 129;
    public static final int DISPATCH_PACKAGE_BROADCAST = 133;
    public static final int DUMP_ACTIVITY = 136;
    public static final int DUMP_HEAP = 135;
    public static final int DUMP_PROVIDER = 141;
    public static final int DUMP_SERVICE = 123;
    public static final int ENABLE_JIT = 132;
    public static final int ENTER_ANIMATION_COMPLETE = 149;
    public static final int EXIT_APPLICATION = 111;
    public static final int GC_WHEN_IDLE = 120;
    public static final int HIDE_WINDOW = 106;
    public static final int INSTALL_PROVIDER = 145;
    public static final int LAUNCH_ACTIVITY = 100;
    public static final int LOCAL_VOICE_INTERACTION_STARTED = 154;
    public static final int LOW_MEMORY = 124;
    public static final int MULTI_WINDOW_MODE_CHANGED = 152;
    public static final int NEW_INTENT = 112;
    public static final int ON_NEW_ACTIVITY_OPTIONS = 146;
    public static final int PAUSE_ACTIVITY = 101;
    public static final int PAUSE_ACTIVITY_FINISHING = 102;
    public static final int PICTURE_IN_PICTURE_MODE_CHANGED = 153;
    public static final int PROFILER_CONTROL = 127;
    public static final int RECEIVER = 113;
    public static final int RELAUNCH_ACTIVITY = 126;
    public static final int REMOVE_PROVIDER = 131;
    public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
    public static final int RESUME_ACTIVITY = 107;
    public static final int SCHEDULE_CRASH = 134;
    public static final int SEND_RESULT = 108;
    public static final int SERVICE_ARGS = 115;
    public static final int SET_CORE_SETTINGS = 138;
    public static final int SHOW_WINDOW = 105;
    public static final int SLEEPING = 137;
    public static final int START_BINDER_TRACKING = 150;
    public static final int STOP_ACTIVITY_HIDE = 104;
    public static final int STOP_ACTIVITY_SHOW = 103;
    public static final int STOP_BINDER_TRACKING_AND_DUMP = 151;
    public static final int STOP_SERVICE = 116;
    public static final int SUICIDE = 130;
    public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
    public static final int TRIM_MEMORY = 140;
    public static final int UNBIND_SERVICE = 122;
    public static final int UNSTABLE_PROVIDER_DIED = 142;
    public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
    
    private H() {}
    
    private void maybeSnapshot()
    {
      Object localObject1;
      if ((ActivityThread.this.mBoundApplication != null) && (SamplingProfilerIntegration.isEnabled()))
      {
        str = ActivityThread.this.mBoundApplication.info.mPackageName;
        localObject1 = null;
      }
      try
      {
        Object localObject2 = ActivityThread.this.getSystemContext();
        if (localObject2 == null)
        {
          Log.e("ActivityThread", "cannot get a valid context");
          return;
        }
        localObject2 = ((Context)localObject2).getPackageManager();
        if (localObject2 == null)
        {
          Log.e("ActivityThread", "cannot get a valid PackageManager");
          return;
        }
        localObject2 = ((PackageManager)localObject2).getPackageInfo(str, 1);
        localObject1 = localObject2;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          Log.e("ActivityThread", "cannot get package info for " + str, localNameNotFoundException);
        }
      }
      SamplingProfilerIntegration.writeSnapshot(ActivityThread.this.mBoundApplication.processName, (PackageInfo)localObject1);
    }
    
    String codeToString(int paramInt)
    {
      return Integer.toString(paramInt);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        paramMessage = paramMessage.obj;
        if ((paramMessage instanceof SomeArgs)) {
          ((SomeArgs)paramMessage).recycle();
        }
        return;
        Trace.traceBegin(64L, "activityStart");
        Object localObject1 = (ActivityThread.ActivityClientRecord)paramMessage.obj;
        ((ActivityThread.ActivityClientRecord)localObject1).packageInfo = ActivityThread.this.getPackageInfoNoCheck(((ActivityThread.ActivityClientRecord)localObject1).activityInfo.applicationInfo, ((ActivityThread.ActivityClientRecord)localObject1).compatInfo);
        ActivityThread.-wrap12(ActivityThread.this, (ActivityThread.ActivityClientRecord)localObject1, null, "LAUNCH_ACTIVITY");
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityRestart");
        localObject1 = (ActivityThread.ActivityClientRecord)paramMessage.obj;
        ActivityThread.-wrap19(ActivityThread.this, (ActivityThread.ActivityClientRecord)localObject1);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityPause");
        localObject1 = (SomeArgs)paramMessage.obj;
        Object localObject2 = ActivityThread.this;
        IBinder localIBinder = (IBinder)((SomeArgs)localObject1).arg1;
        label406:
        int i;
        if ((((SomeArgs)localObject1).argi1 & 0x1) != 0)
        {
          bool1 = true;
          i = ((SomeArgs)localObject1).argi2;
          if ((((SomeArgs)localObject1).argi1 & 0x2) == 0) {
            break label460;
          }
        }
        label460:
        for (boolean bool2 = true;; bool2 = false)
        {
          ActivityThread.-wrap16((ActivityThread)localObject2, localIBinder, false, bool1, i, bool2, ((SomeArgs)localObject1).argi3);
          maybeSnapshot();
          Trace.traceEnd(64L);
          break;
          bool1 = false;
          break label406;
        }
        Trace.traceBegin(64L, "activityPause");
        localObject1 = (SomeArgs)paramMessage.obj;
        localObject2 = ActivityThread.this;
        localIBinder = (IBinder)((SomeArgs)localObject1).arg1;
        if ((((SomeArgs)localObject1).argi1 & 0x1) != 0)
        {
          bool1 = true;
          label512:
          i = ((SomeArgs)localObject1).argi2;
          if ((((SomeArgs)localObject1).argi1 & 0x2) == 0) {
            break label562;
          }
        }
        label562:
        for (bool2 = true;; bool2 = false)
        {
          ActivityThread.-wrap16((ActivityThread)localObject2, localIBinder, true, bool1, i, bool2, ((SomeArgs)localObject1).argi3);
          Trace.traceEnd(64L);
          break;
          bool1 = false;
          break label512;
        }
        Trace.traceBegin(64L, "activityStop");
        localObject1 = (SomeArgs)paramMessage.obj;
        ActivityThread.-wrap25(ActivityThread.this, (IBinder)((SomeArgs)localObject1).arg1, true, ((SomeArgs)localObject1).argi2, ((SomeArgs)localObject1).argi3);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityStop");
        localObject1 = (SomeArgs)paramMessage.obj;
        ActivityThread.-wrap25(ActivityThread.this, (IBinder)((SomeArgs)localObject1).arg1, false, ((SomeArgs)localObject1).argi2, ((SomeArgs)localObject1).argi3);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityShowWindow");
        ActivityThread.-wrap30(ActivityThread.this, (IBinder)paramMessage.obj, true);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityHideWindow");
        ActivityThread.-wrap30(ActivityThread.this, (IBinder)paramMessage.obj, false);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityResume");
        localObject1 = (SomeArgs)paramMessage.obj;
        localObject2 = ActivityThread.this;
        localIBinder = (IBinder)((SomeArgs)localObject1).arg1;
        if (((SomeArgs)localObject1).argi1 != 0) {}
        for (boolean bool1 = true;; bool1 = false)
        {
          ((ActivityThread)localObject2).handleResumeActivity(localIBinder, true, bool1, true, ((SomeArgs)localObject1).argi3, "RESUME_ACTIVITY");
          Trace.traceEnd(64L);
          break;
        }
        Trace.traceBegin(64L, "activityDeliverResult");
        ActivityThread.-wrap20(ActivityThread.this, (ActivityThread.ResultData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityDestroy");
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          ActivityThread.-wrap6((ActivityThread)localObject1, (IBinder)localObject2, bool1, paramMessage.arg2, false);
          Trace.traceEnd(64L);
          break;
        }
        Trace.traceBegin(64L, "bindApplication");
        localObject1 = (ActivityThread.AppBindData)paramMessage.obj;
        ActivityThread.-wrap2(ActivityThread.this, (ActivityThread.AppBindData)localObject1);
        Trace.traceEnd(64L);
        continue;
        if (ActivityThread.this.mInitialApplication != null) {
          ActivityThread.this.mInitialApplication.onTerminate();
        }
        Looper.myLooper().quit();
        continue;
        Trace.traceBegin(64L, "activityNewIntent");
        ActivityThread.-wrap15(ActivityThread.this, (ActivityThread.NewIntentData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "broadcastReceiveComp");
        ActivityThread.-wrap18(ActivityThread.this, (ActivityThread.ReceiverData)paramMessage.obj);
        maybeSnapshot();
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "serviceCreate: " + String.valueOf(paramMessage.obj));
        ActivityThread.-wrap5(ActivityThread.this, (ActivityThread.CreateServiceData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "serviceBind");
        ActivityThread.-wrap3(ActivityThread.this, (ActivityThread.BindServiceData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "serviceUnbind");
        ActivityThread.-wrap28(ActivityThread.this, (ActivityThread.BindServiceData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "serviceStart: " + String.valueOf(paramMessage.obj));
        ActivityThread.-wrap21(ActivityThread.this, (ActivityThread.ServiceArgsData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "serviceStop");
        ActivityThread.-wrap27(ActivityThread.this, (IBinder)paramMessage.obj);
        maybeSnapshot();
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "configChanged");
        ActivityThread.this.mCurDefaultDisplayDpi = ((Configuration)paramMessage.obj).densityDpi;
        ActivityThread.this.mUpdatingSystemConfig = true;
        ActivityThread.this.handleConfigurationChanged((Configuration)paramMessage.obj, null);
        ActivityThread.this.mUpdatingSystemConfig = false;
        Trace.traceEnd(64L);
        continue;
        localObject1 = (ActivityThread.ContextCleanupInfo)paramMessage.obj;
        ((ActivityThread.ContextCleanupInfo)localObject1).context.performFinalCleanup(((ActivityThread.ContextCleanupInfo)localObject1).who, ((ActivityThread.ContextCleanupInfo)localObject1).what);
        continue;
        ActivityThread.this.scheduleGcIdler();
        continue;
        ActivityThread.-wrap10(ActivityThread.this, (ActivityThread.DumpComponentInfo)paramMessage.obj);
        continue;
        Trace.traceBegin(64L, "lowMemory");
        ActivityThread.this.handleLowMemory();
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "activityConfigChanged");
        localObject1 = ActivityThread.this;
        localObject2 = (ActivityThread.ActivityConfigChangeData)paramMessage.obj;
        if (paramMessage.arg1 == 1) {}
        for (bool1 = true;; bool1 = false)
        {
          ((ActivityThread)localObject1).handleActivityConfigurationChanged((ActivityThread.ActivityConfigChangeData)localObject2, bool1);
          Trace.traceEnd(64L);
          break;
        }
        localObject1 = ActivityThread.this;
        if (paramMessage.arg1 != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          ((ActivityThread)localObject1).handleProfilerControl(bool1, (ProfilerInfo)paramMessage.obj, paramMessage.arg2);
          break;
        }
        Trace.traceBegin(64L, "backupCreateAgent");
        ActivityThread.-wrap4(ActivityThread.this, (ActivityThread.CreateBackupAgentData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Trace.traceBegin(64L, "backupDestroyAgent");
        ActivityThread.-wrap7(ActivityThread.this, (ActivityThread.CreateBackupAgentData)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        Process.killProcess(Process.myPid());
        continue;
        Trace.traceBegin(64L, "providerRemove");
        ActivityThread.this.completeRemoveProvider((ActivityThread.ProviderRefCount)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        ActivityThread.this.ensureJitEnabled();
        continue;
        Trace.traceBegin(64L, "broadcastPackage");
        ActivityThread.this.handleDispatchPackageBroadcast(paramMessage.arg1, (String[])paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        throw new RemoteServiceException((String)paramMessage.obj);
        if (paramMessage.arg1 != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          ActivityThread.handleDumpHeap(bool1, (ActivityThread.DumpHeapData)paramMessage.obj);
          break;
        }
        ActivityThread.-wrap8(ActivityThread.this, (ActivityThread.DumpComponentInfo)paramMessage.obj);
        continue;
        ActivityThread.-wrap9(ActivityThread.this, (ActivityThread.DumpComponentInfo)paramMessage.obj);
        continue;
        Trace.traceBegin(64L, "sleeping");
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          ActivityThread.-wrap23((ActivityThread)localObject1, (IBinder)localObject2, bool1);
          Trace.traceEnd(64L);
          break;
        }
        Trace.traceBegin(64L, "setCoreSettings");
        ActivityThread.-wrap22(ActivityThread.this, (Bundle)paramMessage.obj);
        Trace.traceEnd(64L);
        continue;
        ActivityThread.-wrap29(ActivityThread.this, (ActivityThread.UpdateCompatibilityData)paramMessage.obj);
        continue;
        Trace.traceBegin(64L, "trimMemory");
        ActivityThread.this.handleTrimMemory(paramMessage.arg1);
        Trace.traceEnd(64L);
        continue;
        ActivityThread.this.handleUnstableProviderDied((IBinder)paramMessage.obj, false);
        continue;
        ActivityThread.this.handleRequestAssistContextExtras((ActivityThread.RequestAssistContextExtras)paramMessage.obj);
        continue;
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 == 1) {}
        for (bool1 = true;; bool1 = false)
        {
          ((ActivityThread)localObject1).handleTranslucentConversionComplete((IBinder)localObject2, bool1);
          break;
        }
        ActivityThread.this.handleInstallProvider((ProviderInfo)paramMessage.obj);
        continue;
        localObject1 = (Pair)paramMessage.obj;
        ActivityThread.this.onNewActivityOptions((IBinder)((Pair)localObject1).first, (ActivityOptions)((Pair)localObject1).second);
        continue;
        ActivityThread.this.handleCancelVisibleBehind((IBinder)paramMessage.obj);
        continue;
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 > 0) {}
        for (bool1 = true;; bool1 = false)
        {
          ((ActivityThread)localObject1).handleOnBackgroundVisibleBehindChanged((IBinder)localObject2, bool1);
          break;
        }
        ActivityThread.-wrap11(ActivityThread.this, (IBinder)paramMessage.obj);
        continue;
        ActivityThread.-wrap24(ActivityThread.this);
        continue;
        ActivityThread.-wrap26(ActivityThread.this, (ParcelFileDescriptor)paramMessage.obj);
        continue;
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 == 1) {}
        for (bool1 = true;; bool1 = false)
        {
          ActivityThread.-wrap14((ActivityThread)localObject1, (IBinder)localObject2, bool1);
          break;
        }
        localObject1 = ActivityThread.this;
        localObject2 = (IBinder)paramMessage.obj;
        if (paramMessage.arg1 == 1) {}
        for (bool1 = true;; bool1 = false)
        {
          ActivityThread.-wrap17((ActivityThread)localObject1, (IBinder)localObject2, bool1);
          break;
        }
        ActivityThread.-wrap13(ActivityThread.this, (IBinder)((SomeArgs)paramMessage.obj).arg1, (IVoiceInteractor)((SomeArgs)paramMessage.obj).arg2);
      }
    }
  }
  
  private class Idler
    implements MessageQueue.IdleHandler
  {
    private Idler() {}
    
    public final boolean queueIdle()
    {
      Object localObject = ActivityThread.this.mNewActivities;
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (ActivityThread.this.mBoundApplication != null)
      {
        bool1 = bool2;
        if (ActivityThread.this.mProfiler.profileFd != null)
        {
          bool1 = bool2;
          if (ActivityThread.this.mProfiler.autoStopProfiler) {
            bool1 = true;
          }
        }
      }
      IActivityManager localIActivityManager;
      if (localObject != null)
      {
        ActivityThread.this.mNewActivities = null;
        localIActivityManager = ActivityManagerNative.getDefault();
        if ((((ActivityThread.ActivityClientRecord)localObject).activity != null) && (!((ActivityThread.ActivityClientRecord)localObject).activity.mFinished)) {
          break label130;
        }
      }
      for (;;)
      {
        ActivityThread.ActivityClientRecord localActivityClientRecord = ((ActivityThread.ActivityClientRecord)localObject).nextIdle;
        ((ActivityThread.ActivityClientRecord)localObject).nextIdle = null;
        localObject = localActivityClientRecord;
        if (localActivityClientRecord != null) {
          break;
        }
        if (bool1) {
          ActivityThread.this.mProfiler.stopProfiling();
        }
        ActivityThread.this.ensureJitEnabled();
        return false;
        try
        {
          label130:
          localIActivityManager.activityIdle(((ActivityThread.ActivityClientRecord)localObject).token, ((ActivityThread.ActivityClientRecord)localObject).createdConfig, bool1);
          ((ActivityThread.ActivityClientRecord)localObject).createdConfig = null;
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
    }
  }
  
  static final class NewIntentData
  {
    boolean andPause;
    List<ReferrerIntent> intents;
    IBinder token;
    
    public String toString()
    {
      return "NewIntentData{intents=" + this.intents + " token=" + this.token + " andPause=" + this.andPause + "}";
    }
  }
  
  static final class Profiler
  {
    boolean autoStopProfiler;
    boolean handlingProfiling;
    ParcelFileDescriptor profileFd;
    String profileFile;
    boolean profiling;
    int samplingInterval;
    
    public void setProfiler(ProfilerInfo paramProfilerInfo)
    {
      ParcelFileDescriptor localParcelFileDescriptor = paramProfilerInfo.profileFd;
      if (this.profiling)
      {
        if (localParcelFileDescriptor != null) {}
        try
        {
          localParcelFileDescriptor.close();
          return;
        }
        catch (IOException paramProfilerInfo)
        {
          return;
        }
      }
      if (this.profileFd != null) {}
      try
      {
        this.profileFd.close();
        this.profileFile = paramProfilerInfo.profileFile;
        this.profileFd = localParcelFileDescriptor;
        this.samplingInterval = paramProfilerInfo.samplingInterval;
        this.autoStopProfiler = paramProfilerInfo.autoStopProfiler;
        return;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
    }
    
    public void startProfiling()
    {
      boolean bool = true;
      if ((this.profileFd == null) || (this.profiling)) {
        return;
      }
      try
      {
        int i = SystemProperties.getInt("debug.traceview-buffer-size-mb", 8);
        String str = this.profileFile;
        FileDescriptor localFileDescriptor = this.profileFd.getFileDescriptor();
        if (this.samplingInterval != 0) {}
        for (;;)
        {
          VMDebug.startMethodTracing(str, localFileDescriptor, i * 1024 * 1024, 0, bool, this.samplingInterval);
          this.profiling = true;
          return;
          bool = false;
        }
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        Slog.w("ActivityThread", "Profiling failed on path " + this.profileFile);
        try
        {
          this.profileFd.close();
          this.profileFd = null;
          return;
        }
        catch (IOException localIOException)
        {
          Slog.w("ActivityThread", "Failure closing profile fd", localIOException);
        }
      }
    }
    
    public void stopProfiling()
    {
      if (this.profiling)
      {
        this.profiling = false;
        Debug.stopMethodTracing();
        if (this.profileFd == null) {}
      }
      try
      {
        this.profileFd.close();
        this.profileFd = null;
        this.profileFile = null;
        return;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
    }
  }
  
  final class ProviderClientRecord
  {
    final IActivityManager.ContentProviderHolder mHolder;
    final ContentProvider mLocalProvider;
    final String[] mNames;
    final IContentProvider mProvider;
    
    ProviderClientRecord(String[] paramArrayOfString, IContentProvider paramIContentProvider, ContentProvider paramContentProvider, IActivityManager.ContentProviderHolder paramContentProviderHolder)
    {
      this.mNames = paramArrayOfString;
      this.mProvider = paramIContentProvider;
      this.mLocalProvider = paramContentProvider;
      this.mHolder = paramContentProviderHolder;
    }
  }
  
  private static final class ProviderKey
  {
    final String authority;
    final int userId;
    
    public ProviderKey(String paramString, int paramInt)
    {
      this.authority = paramString;
      this.userId = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof ProviderKey))
      {
        paramObject = (ProviderKey)paramObject;
        boolean bool1 = bool2;
        if (Objects.equals(this.authority, ((ProviderKey)paramObject).authority))
        {
          bool1 = bool2;
          if (this.userId == ((ProviderKey)paramObject).userId) {
            bool1 = true;
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      if (this.authority != null) {}
      for (int i = this.authority.hashCode();; i = 0) {
        return i ^ this.userId;
      }
    }
  }
  
  private static final class ProviderRefCount
  {
    public final ActivityThread.ProviderClientRecord client;
    public final IActivityManager.ContentProviderHolder holder;
    public boolean removePending;
    public int stableCount;
    public int unstableCount;
    
    ProviderRefCount(IActivityManager.ContentProviderHolder paramContentProviderHolder, ActivityThread.ProviderClientRecord paramProviderClientRecord, int paramInt1, int paramInt2)
    {
      this.holder = paramContentProviderHolder;
      this.client = paramProviderClientRecord;
      this.stableCount = paramInt1;
      this.unstableCount = paramInt2;
    }
  }
  
  static final class ReceiverData
    extends BroadcastReceiver.PendingResult
  {
    CompatibilityInfo compatInfo;
    ActivityInfo info;
    Intent intent;
    
    public ReceiverData(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, IBinder paramIBinder, int paramInt2, int paramInt3)
    {
      super(paramString, paramBundle, 0, paramBoolean1, paramBoolean2, paramIBinder, paramInt2, paramIntent.getFlags());
      this.intent = paramIntent;
      setHascode(paramInt3);
    }
    
    public String toString()
    {
      return "ReceiverData{intent=" + this.intent + " packageName=" + this.info.packageName + " resultCode=" + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(false) + "}";
    }
  }
  
  static final class RequestAssistContextExtras
  {
    IBinder activityToken;
    IBinder requestToken;
    int requestType;
    int sessionId;
  }
  
  static final class ResultData
  {
    List<ResultInfo> results;
    IBinder token;
    
    public String toString()
    {
      return "ResultData{token=" + this.token + " results" + this.results + "}";
    }
  }
  
  static final class ServiceArgsData
  {
    Intent args;
    int flags;
    int startId;
    boolean taskRemoved;
    IBinder token;
    
    public String toString()
    {
      return "ServiceArgsData{token=" + this.token + " startId=" + this.startId + " args=" + this.args + "}";
    }
  }
  
  private static class StopInfo
    implements Runnable
  {
    ActivityThread.ActivityClientRecord activity;
    CharSequence description;
    PersistableBundle persistentState;
    Bundle state;
    
    public void run()
    {
      try
      {
        ActivityManagerNative.getDefault().activityStopped(this.activity.token, this.state, this.persistentState, this.description);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        if (((localRemoteException instanceof TransactionTooLargeException)) && (this.activity.packageInfo.getTargetSdkVersion() < 24))
        {
          Log.e("ActivityThread", "App sent too much data in instance state, so it was ignored", localRemoteException);
          return;
        }
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "com.android.vending";
        int i = 0;
        while (i < arrayOfString.length)
        {
          if ((arrayOfString[i] != null) && (arrayOfString[i].equals(this.activity.packageInfo.getPackageName()))) {
            try
            {
              if (Thread.currentThread().isAlive())
              {
                if (ActivityThread.DEBUG_ONEPLUS) {
                  Log.w("ActivityThread", "Killing the current thread: [" + Thread.currentThread().toString() + "].");
                }
                Thread.currentThread().interrupt();
              }
            }
            catch (Exception localException) {}finally
            {
              Log.w("ActivityThread", "Ignored to send too much data in instance state: " + arrayOfString[i]);
              return;
            }
          }
          i += 1;
        }
        throw ((RemoteException)localObject).rethrowFromSystemServer();
      }
    }
  }
  
  static final class UpdateCompatibilityData
  {
    CompatibilityInfo info;
    String pkg;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */