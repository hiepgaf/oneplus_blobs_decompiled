package com.android.server;

import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.ObbInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Environment.UserEnvironment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.OnCloseListener;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.IMountService.Stub;
import android.os.storage.IMountServiceListener;
import android.os.storage.IMountShutdownObserver;
import android.os.storage.IObbActionListener;
import android.os.storage.MountServiceInternal;
import android.os.storage.MountServiceInternal.ExternalStorageMountPolicy;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.IMediaContainerService.Stub;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.HexDump;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.pm.PackageManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class MountService
  extends IMountService.Stub
  implements INativeDaemonConnectorCallbacks, Watchdog.Monitor
{
  private static final String ATTR_CREATED_MILLIS = "createdMillis";
  private static final String ATTR_FORCE_ADOPTABLE = "forceAdoptable";
  private static final String ATTR_FS_UUID = "fsUuid";
  private static final String ATTR_LAST_BENCH_MILLIS = "lastBenchMillis";
  private static final String ATTR_LAST_TRIM_MILLIS = "lastTrimMillis";
  private static final String ATTR_NICKNAME = "nickname";
  private static final String ATTR_PART_GUID = "partGuid";
  private static final String ATTR_PRIMARY_STORAGE_UUID = "primaryStorageUuid";
  private static final String ATTR_TYPE = "type";
  private static final String ATTR_USER_FLAGS = "userFlags";
  private static final String ATTR_VERSION = "version";
  private static final String CRYPTD_TAG = "CryptdConnector";
  private static final int CRYPTO_ALGORITHM_KEY_SIZE = 128;
  public static final String[] CRYPTO_TYPES = { "password", "default", "pattern", "pin" };
  private static boolean DBG = false;
  private static final boolean DEBUG_EVENTS = false;
  private static final boolean DEBUG_OBB = false;
  static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName("com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
  private static final int H_DAEMON_CONNECTED = 2;
  private static final int H_FSTRIM = 4;
  private static final int H_INTERNAL_BROADCAST = 7;
  private static final int H_PARTITION_FORGET = 9;
  private static final int H_RESET = 10;
  private static final int H_SHUTDOWN = 3;
  private static final int H_SYSTEM_READY = 1;
  private static final int H_VOLUME_BROADCAST = 6;
  private static final int H_VOLUME_MOUNT = 5;
  private static final int H_VOLUME_UNMOUNT = 8;
  private static final String LAST_FSTRIM_FILE = "last-fstrim";
  private static final int MAX_CONTAINERS = 250;
  private static final int MOVE_STATUS_COPY_FINISHED = 82;
  private static final int OBB_FLUSH_MOUNT_STATE = 5;
  private static final int OBB_MCS_BOUND = 2;
  private static final int OBB_MCS_RECONNECT = 4;
  private static final int OBB_MCS_UNBIND = 3;
  private static final int OBB_RUN_ACTION = 1;
  private static final int PBKDF2_HASH_ROUNDS = 1024;
  private static final String TAG = "MountService";
  private static final String TAG_STORAGE_BENCHMARK = "storage_benchmark";
  private static final String TAG_STORAGE_TRIM = "storage_trim";
  private static final String TAG_VOLUME = "volume";
  private static final String TAG_VOLUMES = "volumes";
  private static final int VERSION_ADD_PRIMARY = 2;
  private static final int VERSION_FIX_PRIMARY = 3;
  private static final int VERSION_INIT = 1;
  private static final String VOLD_TAG = "VoldConnector";
  private static final boolean WATCHDOG_ENABLE = false;
  static MountService sSelf = null;
  private final HashSet<String> mAsecMountSet;
  private final CountDownLatch mAsecsScanned;
  private volatile boolean mBootCompleted;
  private final Callbacks mCallbacks;
  private final CountDownLatch mConnectedSignal;
  private final NativeDaemonConnector mConnector;
  private final Thread mConnectorThread;
  private IMediaContainerService mContainerService;
  private final Context mContext;
  private final NativeDaemonConnector mCryptConnector;
  private final Thread mCryptConnectorThread;
  private volatile int mCurrentUserId;
  private volatile boolean mDaemonConnected;
  private final DefaultContainerConnection mDefContainerConn;
  @GuardedBy("mLock")
  private ArrayMap<String, CountDownLatch> mDiskScanLatches;
  @GuardedBy("mLock")
  private ArrayMap<String, DiskInfo> mDisks;
  @GuardedBy("mLock")
  private boolean mForceAdoptable;
  private final Handler mHandler;
  private long mLastMaintenance;
  private final File mLastMaintenanceFile;
  @GuardedBy("mLock")
  private int[] mLocalUnlockedUsers;
  private final Object mLock;
  private final LockPatternUtils mLockPatternUtils;
  private final MountServiceInternalImpl mMountServiceInternal;
  @GuardedBy("mLock")
  private IPackageMoveObserver mMoveCallback;
  @GuardedBy("mLock")
  private String mMoveTargetUuid;
  private final ObbActionHandler mObbActionHandler;
  private final Map<IBinder, List<ObbState>> mObbMounts;
  private final Map<String, ObbState> mObbPathToStateMap;
  private PackageManagerService mPms;
  @GuardedBy("mLock")
  private String mPrimaryStorageUuid;
  @GuardedBy("mLock")
  private ArrayMap<String, VolumeRecord> mRecords;
  private final AtomicFile mSettingsFile;
  private volatile boolean mSystemReady;
  @GuardedBy("mLock")
  private int[] mSystemUnlockedUsers;
  private final Object mUnmountLock;
  @GuardedBy("mUnmountLock")
  private CountDownLatch mUnmountSignal;
  private BroadcastReceiver mUserReceiver;
  @GuardedBy("mLock")
  private final ArrayMap<String, VolumeInfo> mVolumes;
  
  static
  {
    DBG = Build.IS_DEBUGGABLE;
  }
  
  /* Error */
  public MountService(Context arg1)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 407	android/os/storage/IMountService$Stub:<init>	()V
    //   4: aload_0
    //   5: new 409	java/lang/Object
    //   8: dup
    //   9: invokespecial 410	java/lang/Object:<init>	()V
    //   12: putfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   15: aload_0
    //   16: getstatic 417	libcore/util/EmptyArray:INT	[I
    //   19: putfield 419	com/android/server/MountService:mLocalUnlockedUsers	[I
    //   22: aload_0
    //   23: getstatic 417	libcore/util/EmptyArray:INT	[I
    //   26: putfield 421	com/android/server/MountService:mSystemUnlockedUsers	[I
    //   29: aload_0
    //   30: new 423	android/util/ArrayMap
    //   33: dup
    //   34: invokespecial 424	android/util/ArrayMap:<init>	()V
    //   37: putfield 426	com/android/server/MountService:mDisks	Landroid/util/ArrayMap;
    //   40: aload_0
    //   41: new 423	android/util/ArrayMap
    //   44: dup
    //   45: invokespecial 424	android/util/ArrayMap:<init>	()V
    //   48: putfield 244	com/android/server/MountService:mVolumes	Landroid/util/ArrayMap;
    //   51: aload_0
    //   52: new 423	android/util/ArrayMap
    //   55: dup
    //   56: invokespecial 424	android/util/ArrayMap:<init>	()V
    //   59: putfield 428	com/android/server/MountService:mRecords	Landroid/util/ArrayMap;
    //   62: aload_0
    //   63: new 423	android/util/ArrayMap
    //   66: dup
    //   67: invokespecial 424	android/util/ArrayMap:<init>	()V
    //   70: putfield 430	com/android/server/MountService:mDiskScanLatches	Landroid/util/ArrayMap;
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 281	com/android/server/MountService:mCurrentUserId	I
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 432	com/android/server/MountService:mSystemReady	Z
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 434	com/android/server/MountService:mBootCompleted	Z
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 436	com/android/server/MountService:mDaemonConnected	Z
    //   93: aload_0
    //   94: new 438	java/util/concurrent/CountDownLatch
    //   97: dup
    //   98: iconst_2
    //   99: invokespecial 440	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   102: putfield 442	com/android/server/MountService:mConnectedSignal	Ljava/util/concurrent/CountDownLatch;
    //   105: aload_0
    //   106: new 438	java/util/concurrent/CountDownLatch
    //   109: dup
    //   110: iconst_1
    //   111: invokespecial 440	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   114: putfield 444	com/android/server/MountService:mAsecsScanned	Ljava/util/concurrent/CountDownLatch;
    //   117: aload_0
    //   118: new 409	java/lang/Object
    //   121: dup
    //   122: invokespecial 410	java/lang/Object:<init>	()V
    //   125: putfield 446	com/android/server/MountService:mUnmountLock	Ljava/lang/Object;
    //   128: aload_0
    //   129: new 448	java/util/HashSet
    //   132: dup
    //   133: invokespecial 449	java/util/HashSet:<init>	()V
    //   136: putfield 451	com/android/server/MountService:mAsecMountSet	Ljava/util/HashSet;
    //   139: aload_0
    //   140: new 453	java/util/HashMap
    //   143: dup
    //   144: invokespecial 454	java/util/HashMap:<init>	()V
    //   147: putfield 237	com/android/server/MountService:mObbMounts	Ljava/util/Map;
    //   150: aload_0
    //   151: new 453	java/util/HashMap
    //   154: dup
    //   155: invokespecial 454	java/util/HashMap:<init>	()V
    //   158: putfield 240	com/android/server/MountService:mObbPathToStateMap	Ljava/util/Map;
    //   161: aload_0
    //   162: new 31	com/android/server/MountService$MountServiceInternalImpl
    //   165: dup
    //   166: aload_0
    //   167: aconst_null
    //   168: invokespecial 457	com/android/server/MountService$MountServiceInternalImpl:<init>	(Lcom/android/server/MountService;Lcom/android/server/MountService$MountServiceInternalImpl;)V
    //   171: putfield 459	com/android/server/MountService:mMountServiceInternal	Lcom/android/server/MountService$MountServiceInternalImpl;
    //   174: aload_0
    //   175: new 19	com/android/server/MountService$DefaultContainerConnection
    //   178: dup
    //   179: aload_0
    //   180: invokespecial 461	com/android/server/MountService$DefaultContainerConnection:<init>	(Lcom/android/server/MountService;)V
    //   183: putfield 259	com/android/server/MountService:mDefContainerConn	Lcom/android/server/MountService$DefaultContainerConnection;
    //   186: aload_0
    //   187: aconst_null
    //   188: putfield 248	com/android/server/MountService:mContainerService	Lcom/android/internal/app/IMediaContainerService;
    //   191: aload_0
    //   192: new 10	com/android/server/MountService$1
    //   195: dup
    //   196: aload_0
    //   197: invokespecial 462	com/android/server/MountService$1:<init>	(Lcom/android/server/MountService;)V
    //   200: putfield 464	com/android/server/MountService:mUserReceiver	Landroid/content/BroadcastReceiver;
    //   203: aload_0
    //   204: putstatic 373	com/android/server/MountService:sSelf	Lcom/android/server/MountService;
    //   207: aload_0
    //   208: aload_1
    //   209: putfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   212: aload_0
    //   213: new 16	com/android/server/MountService$Callbacks
    //   216: dup
    //   217: invokestatic 470	com/android/server/FgThread:get	()Lcom/android/server/FgThread;
    //   220: invokevirtual 474	com/android/server/FgThread:getLooper	()Landroid/os/Looper;
    //   223: invokespecial 477	com/android/server/MountService$Callbacks:<init>	(Landroid/os/Looper;)V
    //   226: putfield 479	com/android/server/MountService:mCallbacks	Lcom/android/server/MountService$Callbacks;
    //   229: aload_0
    //   230: new 481	com/android/internal/widget/LockPatternUtils
    //   233: dup
    //   234: aload_0
    //   235: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   238: invokespecial 483	com/android/internal/widget/LockPatternUtils:<init>	(Landroid/content/Context;)V
    //   241: putfield 485	com/android/server/MountService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   244: aload_0
    //   245: ldc_w 487
    //   248: invokestatic 493	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   251: checkcast 495	com/android/server/pm/PackageManagerService
    //   254: putfield 497	com/android/server/MountService:mPms	Lcom/android/server/pm/PackageManagerService;
    //   257: new 499	android/os/HandlerThread
    //   260: dup
    //   261: ldc -123
    //   263: invokespecial 501	android/os/HandlerThread:<init>	(Ljava/lang/String;)V
    //   266: astore_1
    //   267: aload_1
    //   268: invokevirtual 502	android/os/HandlerThread:start	()V
    //   271: aload_0
    //   272: new 28	com/android/server/MountService$MountServiceHandler
    //   275: dup
    //   276: aload_0
    //   277: aload_1
    //   278: invokevirtual 503	android/os/HandlerThread:getLooper	()Landroid/os/Looper;
    //   281: invokespecial 506	com/android/server/MountService$MountServiceHandler:<init>	(Lcom/android/server/MountService;Landroid/os/Looper;)V
    //   284: putfield 263	com/android/server/MountService:mHandler	Landroid/os/Handler;
    //   287: aload_0
    //   288: new 37	com/android/server/MountService$ObbActionHandler
    //   291: dup
    //   292: aload_0
    //   293: invokestatic 511	com/android/server/IoThread:get	()Lcom/android/server/IoThread;
    //   296: invokevirtual 512	com/android/server/IoThread:getLooper	()Landroid/os/Looper;
    //   299: invokespecial 513	com/android/server/MountService$ObbActionHandler:<init>	(Lcom/android/server/MountService;Landroid/os/Looper;)V
    //   302: putfield 275	com/android/server/MountService:mObbActionHandler	Lcom/android/server/MountService$ObbActionHandler;
    //   305: aload_0
    //   306: new 515	java/io/File
    //   309: dup
    //   310: new 515	java/io/File
    //   313: dup
    //   314: invokestatic 521	android/os/Environment:getDataDirectory	()Ljava/io/File;
    //   317: ldc_w 523
    //   320: invokespecial 526	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   323: ldc 119
    //   325: invokespecial 526	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   328: putfield 271	com/android/server/MountService:mLastMaintenanceFile	Ljava/io/File;
    //   331: aload_0
    //   332: getfield 271	com/android/server/MountService:mLastMaintenanceFile	Ljava/io/File;
    //   335: invokevirtual 529	java/io/File:exists	()Z
    //   338: ifne +261 -> 599
    //   341: new 531	java/io/FileOutputStream
    //   344: dup
    //   345: aload_0
    //   346: getfield 271	com/android/server/MountService:mLastMaintenanceFile	Ljava/io/File;
    //   349: invokespecial 534	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   352: invokevirtual 537	java/io/FileOutputStream:close	()V
    //   355: aload_0
    //   356: new 539	android/util/AtomicFile
    //   359: dup
    //   360: new 515	java/io/File
    //   363: dup
    //   364: invokestatic 542	android/os/Environment:getDataSystemDirectory	()Ljava/io/File;
    //   367: ldc_w 544
    //   370: invokespecial 526	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   373: invokespecial 545	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   376: putfield 547	com/android/server/MountService:mSettingsFile	Landroid/util/AtomicFile;
    //   379: aload_0
    //   380: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   383: astore_1
    //   384: aload_1
    //   385: monitorenter
    //   386: aload_0
    //   387: invokespecial 550	com/android/server/MountService:readSettingsLocked	()V
    //   390: aload_1
    //   391: monitorexit
    //   392: ldc_w 552
    //   395: aload_0
    //   396: getfield 459	com/android/server/MountService:mMountServiceInternal	Lcom/android/server/MountService$MountServiceInternalImpl;
    //   399: invokestatic 558	com/android/server/LocalServices:addService	(Ljava/lang/Class;Ljava/lang/Object;)V
    //   402: aload_0
    //   403: new 560	com/android/server/NativeDaemonConnector
    //   406: dup
    //   407: aload_0
    //   408: ldc_w 562
    //   411: sipush 500
    //   414: ldc -105
    //   416: bipush 25
    //   418: aconst_null
    //   419: invokespecial 565	com/android/server/NativeDaemonConnector:<init>	(Lcom/android/server/INativeDaemonConnectorCallbacks;Ljava/lang/String;ILjava/lang/String;ILandroid/os/PowerManager$WakeLock;)V
    //   422: putfield 233	com/android/server/MountService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   425: aload_0
    //   426: getfield 233	com/android/server/MountService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   429: iconst_1
    //   430: invokevirtual 569	com/android/server/NativeDaemonConnector:setDebug	(Z)V
    //   433: aload_0
    //   434: getfield 233	com/android/server/MountService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   437: aload_0
    //   438: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   441: invokevirtual 573	com/android/server/NativeDaemonConnector:setWarnIfHeld	(Ljava/lang/Object;)V
    //   444: aload_0
    //   445: new 575	java/lang/Thread
    //   448: dup
    //   449: aload_0
    //   450: getfield 233	com/android/server/MountService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   453: ldc -105
    //   455: invokespecial 578	java/lang/Thread:<init>	(Ljava/lang/Runnable;Ljava/lang/String;)V
    //   458: putfield 580	com/android/server/MountService:mConnectorThread	Ljava/lang/Thread;
    //   461: aload_0
    //   462: new 560	com/android/server/NativeDaemonConnector
    //   465: dup
    //   466: aload_0
    //   467: ldc_w 582
    //   470: sipush 500
    //   473: ldc 84
    //   475: bipush 25
    //   477: aconst_null
    //   478: invokespecial 565	com/android/server/NativeDaemonConnector:<init>	(Lcom/android/server/INativeDaemonConnectorCallbacks;Ljava/lang/String;ILjava/lang/String;ILandroid/os/PowerManager$WakeLock;)V
    //   481: putfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   484: aload_0
    //   485: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   488: iconst_1
    //   489: invokevirtual 569	com/android/server/NativeDaemonConnector:setDebug	(Z)V
    //   492: aload_0
    //   493: new 575	java/lang/Thread
    //   496: dup
    //   497: aload_0
    //   498: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   501: ldc 84
    //   503: invokespecial 578	java/lang/Thread:<init>	(Ljava/lang/Runnable;Ljava/lang/String;)V
    //   506: putfield 584	com/android/server/MountService:mCryptConnectorThread	Ljava/lang/Thread;
    //   509: new 586	android/content/IntentFilter
    //   512: dup
    //   513: invokespecial 587	android/content/IntentFilter:<init>	()V
    //   516: astore_1
    //   517: aload_1
    //   518: ldc_w 589
    //   521: invokevirtual 592	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   524: aload_1
    //   525: ldc_w 594
    //   528: invokevirtual 592	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   531: aload_0
    //   532: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   535: aload_0
    //   536: getfield 464	com/android/server/MountService:mUserReceiver	Landroid/content/BroadcastReceiver;
    //   539: aload_1
    //   540: aconst_null
    //   541: aload_0
    //   542: getfield 263	com/android/server/MountService:mHandler	Landroid/os/Handler;
    //   545: invokevirtual 600	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   548: pop
    //   549: aload_0
    //   550: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   553: astore_1
    //   554: aload_1
    //   555: monitorenter
    //   556: aload_0
    //   557: invokespecial 603	com/android/server/MountService:addInternalVolumeLocked	()V
    //   560: aload_1
    //   561: monitorexit
    //   562: return
    //   563: astore_1
    //   564: ldc -123
    //   566: new 605	java/lang/StringBuilder
    //   569: dup
    //   570: invokespecial 606	java/lang/StringBuilder:<init>	()V
    //   573: ldc_w 608
    //   576: invokevirtual 612	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   579: aload_0
    //   580: getfield 271	com/android/server/MountService:mLastMaintenanceFile	Ljava/io/File;
    //   583: invokevirtual 616	java/io/File:getPath	()Ljava/lang/String;
    //   586: invokevirtual 612	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   589: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   592: invokestatic 625	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   595: pop
    //   596: goto -241 -> 355
    //   599: aload_0
    //   600: aload_0
    //   601: getfield 271	com/android/server/MountService:mLastMaintenanceFile	Ljava/io/File;
    //   604: invokevirtual 629	java/io/File:lastModified	()J
    //   607: putfield 267	com/android/server/MountService:mLastMaintenance	J
    //   610: goto -255 -> 355
    //   613: astore_2
    //   614: aload_1
    //   615: monitorexit
    //   616: aload_2
    //   617: athrow
    //   618: astore_2
    //   619: aload_1
    //   620: monitorexit
    //   621: aload_2
    //   622: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	623	0	this	MountService
    //   613	4	2	localObject1	Object
    //   618	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   341	355	563	java/io/IOException
    //   386	390	613	finally
    //   556	560	618	finally
  }
  
  private void addInternalVolumeLocked()
  {
    VolumeInfo localVolumeInfo = new VolumeInfo("private", 1, null, null);
    localVolumeInfo.state = 2;
    localVolumeInfo.path = Environment.getDataDirectory().getAbsolutePath();
    this.mVolumes.put(localVolumeInfo.id, localVolumeInfo);
  }
  
  private void addObbStateLocked(ObbState paramObbState)
    throws RemoteException
  {
    localIBinder = paramObbState.getBinder();
    List localList = (List)this.mObbMounts.get(localIBinder);
    if (localList == null)
    {
      localObject = new ArrayList();
      this.mObbMounts.put(localIBinder, localObject);
      ((List)localObject).add(paramObbState);
    }
    try
    {
      paramObbState.link();
      this.mObbPathToStateMap.put(paramObbState.rawPath, paramObbState);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Iterator localIterator;
      ((List)localObject).remove(paramObbState);
      if (!((List)localObject).isEmpty()) {
        break label158;
      }
      this.mObbMounts.remove(localIBinder);
      throw localRemoteException;
    }
    localIterator = localList.iterator();
    do
    {
      localObject = localList;
      if (!localIterator.hasNext()) {
        break;
      }
    } while (!((ObbState)localIterator.next()).rawPath.equals(paramObbState.rawPath));
    throw new IllegalStateException("Attempt to add ObbState twice. This indicates an error in the MountService logic.");
  }
  
  private void bootCompleted()
  {
    this.mBootCompleted = true;
  }
  
  private void broadcastForMediaStorage(VolumeInfo paramVolumeInfo, StorageVolume paramStorageVolume)
  {
    if (paramVolumeInfo != null)
    {
      String str = null;
      if (paramVolumeInfo.getState() == 0) {
        str = "android.intent.action.MEDIA_EJECT";
      }
      if ((str != null) && (paramVolumeInfo.getPath() != null) && (paramStorageVolume != null))
      {
        paramVolumeInfo = paramVolumeInfo.getPath().getPath();
        paramVolumeInfo = new Intent(str, Uri.parse("file://" + paramVolumeInfo));
        paramVolumeInfo.putExtra("android.os.storage.extra.STORAGE_VOLUME", paramStorageVolume);
        if (DBG) {
          Slog.i("MountService", "sendMediaStorageIntent: [" + paramVolumeInfo + "] to " + paramStorageVolume.getOwner());
        }
        this.mContext.sendBroadcastAsUser(paramVolumeInfo, paramStorageVolume.getOwner());
      }
    }
  }
  
  private void copyLocaleFromMountService()
  {
    try
    {
      String str = getField("SystemLocale");
      if (TextUtils.isEmpty(str)) {
        return;
      }
    }
    catch (RemoteException localRemoteException1)
    {
      return;
    }
    Slog.d("MountService", "Got locale " + localRemoteException1 + " from mount service");
    Locale localLocale = Locale.forLanguageTag(localRemoteException1);
    Configuration localConfiguration = new Configuration();
    localConfiguration.setLocale(localLocale);
    try
    {
      ActivityManagerNative.getDefault().updatePersistentConfiguration(localConfiguration);
      Slog.d("MountService", "Setting system properties to " + localRemoteException1 + " from mount service");
      SystemProperties.set("persist.sys.locale", localLocale.toLanguageTag());
      return;
    }
    catch (RemoteException localRemoteException2)
    {
      for (;;)
      {
        Slog.e("MountService", "Error setting system locale from mount service", localRemoteException2);
      }
    }
  }
  
  private NativeDaemonConnector.SensitiveArg encodeBytes(byte[] paramArrayOfByte)
  {
    if (ArrayUtils.isEmpty(paramArrayOfByte)) {
      return new NativeDaemonConnector.SensitiveArg("!");
    }
    return new NativeDaemonConnector.SensitiveArg(HexDump.toHexString(paramArrayOfByte));
  }
  
  private int encryptStorageExtended(int paramInt, String paramString, boolean paramBoolean)
  {
    if ((TextUtils.isEmpty(paramString)) && (paramInt != 1)) {
      throw new IllegalArgumentException("password cannot be empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to access the crypt keeper");
    waitForReady();
    if (paramInt == 1) {}
    for (;;)
    {
      try
      {
        localObject = this.mCryptConnector;
        if (!paramBoolean) {
          break label166;
        }
        paramString = "wipe";
        ((NativeDaemonConnector)localObject).execute("cryptfs", new Object[] { "enablecrypto", paramString, CRYPTO_TYPES[paramInt] });
        return 0;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        NativeDaemonConnector localNativeDaemonConnector;
        return paramString.getCode();
      }
      localNativeDaemonConnector = this.mCryptConnector;
      if (paramBoolean)
      {
        localObject = "wipe";
        localNativeDaemonConnector.execute("cryptfs", new Object[] { "enablecrypto", localObject, CRYPTO_TYPES[paramInt], new NativeDaemonConnector.SensitiveArg(paramString) });
        return 0;
      }
      Object localObject = "inplace";
      continue;
      label166:
      paramString = "inplace";
    }
  }
  
  private void enforceAdminUser()
  {
    UserManager localUserManager = (UserManager)this.mContext.getSystemService("user");
    int i = UserHandle.getCallingUserId();
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = localUserManager.getUserInfo(i).isAdmin();
      Binder.restoreCallingIdentity(l);
      if (!bool) {
        throw new SecurityException("Only admin users can adopt sd cards");
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void enforcePermission(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission(paramString, paramString);
  }
  
  private static String escapeNull(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return "!";
    }
    if ((paramString.indexOf(0) != -1) || (paramString.indexOf(' ') != -1)) {
      throw new IllegalArgumentException(paramString);
    }
    return paramString;
  }
  
  private CountDownLatch findOrCreateDiskScanLatch(String paramString)
  {
    synchronized (this.mLock)
    {
      CountDownLatch localCountDownLatch2 = (CountDownLatch)this.mDiskScanLatches.get(paramString);
      CountDownLatch localCountDownLatch1 = localCountDownLatch2;
      if (localCountDownLatch2 == null)
      {
        localCountDownLatch1 = new CountDownLatch(1);
        this.mDiskScanLatches.put(paramString, localCountDownLatch1);
      }
      return localCountDownLatch1;
    }
  }
  
  private VolumeRecord findRecordForPath(String paramString)
  {
    Object localObject = this.mLock;
    int i = 0;
    try
    {
      while (i < this.mVolumes.size())
      {
        VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.valueAt(i);
        if ((localVolumeInfo.path != null) && (paramString.startsWith(localVolumeInfo.path)))
        {
          paramString = (VolumeRecord)this.mRecords.get(localVolumeInfo.fsUuid);
          return paramString;
        }
        i += 1;
      }
      return null;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  private VolumeInfo findStorageForUuid(String paramString)
  {
    StorageManager localStorageManager = (StorageManager)this.mContext.getSystemService(StorageManager.class);
    if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, paramString)) {
      return localStorageManager.findVolumeById("emulated");
    }
    if (Objects.equals("primary_physical", paramString)) {
      return localStorageManager.getPrimaryPhysicalVolume();
    }
    return localStorageManager.findEmulatedForPrivate(localStorageManager.findVolumeByUuid(paramString));
  }
  
  private VolumeInfo findVolumeByIdOrThrow(String paramString)
  {
    synchronized (this.mLock)
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.get(paramString);
      if (localVolumeInfo != null) {
        return localVolumeInfo;
      }
      throw new IllegalArgumentException("No volume found for ID " + paramString);
    }
  }
  
  private String findVolumeIdForPathOrThrow(String paramString)
  {
    Object localObject = this.mLock;
    int i = 0;
    try
    {
      while (i < this.mVolumes.size())
      {
        VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.valueAt(i);
        if ((localVolumeInfo.path != null) && (paramString.startsWith(localVolumeInfo.path)))
        {
          paramString = localVolumeInfo.id;
          return paramString;
        }
        i += 1;
      }
      throw new IllegalArgumentException("No volume found for path " + paramString);
    }
    finally {}
  }
  
  private void forgetPartition(String paramString)
  {
    try
    {
      this.mConnector.execute("volume", new Object[] { "forget_partition", paramString });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.w("MountService", "Failed to forget key for " + paramString + ": " + localNativeDaemonConnectorException);
    }
  }
  
  private String getDefaultPrimaryStorageUuid()
  {
    if (SystemProperties.getBoolean("ro.vold.primary_physical", false)) {
      return "primary_physical";
    }
    return StorageManager.UUID_PRIVATE_INTERNAL;
  }
  
  private void handleDaemonConnected()
  {
    initIfReadyAndConnected();
    resetIfReadyAndConnected();
    this.mConnectedSignal.countDown();
    if (this.mConnectedSignal.getCount() != 0L) {
      return;
    }
    if ("".equals(SystemProperties.get("vold.encrypt_progress"))) {
      copyLocaleFromMountService();
    }
    this.mPms.scanAvailableAsecs();
    this.mAsecsScanned.countDown();
  }
  
  private void handleSystemReady()
  {
    initIfReadyAndConnected();
    resetIfReadyAndConnected();
    MountServiceIdler.scheduleIdlePass(this.mContext);
  }
  
  private void initIfReadyAndConnected()
  {
    Slog.d("MountService", "Thinking about init, mSystemReady=" + this.mSystemReady + ", mDaemonConnected=" + this.mDaemonConnected);
    if ((!this.mSystemReady) || (!this.mDaemonConnected) || (StorageManager.isFileEncryptedNativeOnly())) {}
    for (;;)
    {
      return;
      boolean bool = StorageManager.isFileEncryptedEmulatedOnly();
      Slog.d("MountService", "Setting up emulation state, initlocked=" + bool);
      Iterator localIterator = ((UserManager)this.mContext.getSystemService(UserManager.class)).getUsers().iterator();
      while (localIterator.hasNext())
      {
        UserInfo localUserInfo = (UserInfo)localIterator.next();
        if (bool) {
          try
          {
            this.mCryptConnector.execute("cryptfs", new Object[] { "lock_user_key", Integer.valueOf(localUserInfo.id) });
          }
          catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
          {
            Slog.w("MountService", "Failed to init vold", localNativeDaemonConnectorException);
          }
        } else {
          this.mCryptConnector.execute("cryptfs", new Object[] { "unlock_user_key", Integer.valueOf(localNativeDaemonConnectorException.id), Integer.valueOf(localNativeDaemonConnectorException.serialNumber), "!", "!" });
        }
      }
    }
  }
  
  private boolean isBroadcastWorthy(VolumeInfo paramVolumeInfo)
  {
    switch (paramVolumeInfo.getType())
    {
    default: 
      return false;
    }
    switch (paramVolumeInfo.getState())
    {
    case 1: 
    case 4: 
    case 7: 
    default: 
      return false;
    }
    return true;
  }
  
  private boolean isMountDisallowed(VolumeInfo paramVolumeInfo)
  {
    UserManager localUserManager = (UserManager)this.mContext.getSystemService(UserManager.class);
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramVolumeInfo.disk != null)
    {
      bool1 = bool2;
      if (paramVolumeInfo.disk.isUsb()) {
        bool1 = localUserManager.hasUserRestriction("no_usb_file_transfer", Binder.getCallingUserHandle());
      }
    }
    bool2 = false;
    if ((paramVolumeInfo.type == 0) || (paramVolumeInfo.type == 1)) {
      bool2 = localUserManager.hasUserRestriction("no_physical_media", Binder.getCallingUserHandle());
    }
    if (!bool1) {
      return bool2;
    }
    return true;
  }
  
  private boolean isReady()
  {
    try
    {
      boolean bool = this.mConnectedSignal.await(0L, TimeUnit.MILLISECONDS);
      return bool;
    }
    catch (InterruptedException localInterruptedException) {}
    return false;
  }
  
  private boolean isUidOwnerOfPackageOrSystem(String paramString, int paramInt)
  {
    if (paramInt == 1000) {
      return true;
    }
    if (paramString == null) {
      return false;
    }
    return paramInt == this.mPms.getPackageUid(paramString, 268435456, UserHandle.getUserId(paramInt));
  }
  
  private boolean isUsbDisk(VolumeInfo paramVolumeInfo)
  {
    boolean bool = false;
    if (paramVolumeInfo != null)
    {
      if (paramVolumeInfo.getDisk() != null) {
        bool = paramVolumeInfo.getDisk().isUsb();
      }
    }
    else if (DBG)
    {
      paramVolumeInfo = new StringBuilder().append("isUsbDisk = ");
      if (!bool) {
        break label68;
      }
    }
    label68:
    for (int i = 1;; i = 0)
    {
      Slog.d("MountService", i);
      return bool;
      bool = false;
      break;
    }
  }
  
  @Deprecated
  private void killMediaProvider(List<UserInfo> paramList)
  {
    if (paramList == null) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      paramList = paramList.iterator();
      Object localObject;
      IActivityManager localIActivityManager;
      for (;;)
      {
        if (paramList.hasNext())
        {
          localObject = (UserInfo)paramList.next();
          if (((UserInfo)localObject).isSystemOnly()) {
            continue;
          }
          localObject = this.mPms.resolveContentProvider("media", 786432, ((UserInfo)localObject).id);
          if (localObject == null) {
            continue;
          }
          localIActivityManager = ActivityManagerNative.getDefault();
        }
      }
    }
    finally
    {
      try
      {
        localIActivityManager.killApplication(((ProviderInfo)localObject).applicationInfo.packageName, UserHandle.getAppId(((ProviderInfo)localObject).applicationInfo.uid), -1, "vold reset");
        Binder.restoreCallingIdentity(l);
        return;
      }
      catch (RemoteException localRemoteException) {}
      paramList = finally;
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void onCleanupUser(int paramInt)
  {
    Slog.d("MountService", "onCleanupUser " + paramInt);
    try
    {
      this.mConnector.execute("volume", new Object[] { "user_stopped", Integer.valueOf(paramInt) });
      synchronized (this.mVolumes)
      {
        this.mSystemUnlockedUsers = ArrayUtils.removeInt(this.mSystemUnlockedUsers, paramInt);
        return;
      }
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      for (;;) {}
    }
  }
  
  private void onDiskScannedLocked(DiskInfo paramDiskInfo)
  {
    int j = 0;
    int i = 0;
    while (i < this.mVolumes.size())
    {
      localObject = (VolumeInfo)this.mVolumes.valueAt(i);
      int k = j;
      if (Objects.equals(paramDiskInfo.id, ((VolumeInfo)localObject).getDiskId())) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    Object localObject = new Intent("android.os.storage.action.DISK_SCANNED");
    ((Intent)localObject).addFlags(83886080);
    ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", paramDiskInfo.id);
    ((Intent)localObject).putExtra("android.os.storage.extra.VOLUME_COUNT", j);
    this.mHandler.obtainMessage(7, localObject).sendToTarget();
    localObject = (CountDownLatch)this.mDiskScanLatches.remove(paramDiskInfo.id);
    if (localObject != null) {
      ((CountDownLatch)localObject).countDown();
    }
    paramDiskInfo.volumeCount = j;
    Callbacks.-wrap1(this.mCallbacks, paramDiskInfo, j);
  }
  
  private boolean onEventLocked(int paramInt, String paramString, String[] paramArrayOfString)
  {
    switch (paramInt)
    {
    case 645: 
    case 646: 
    case 647: 
    case 648: 
    case 657: 
    case 658: 
    default: 
      Slog.d("MountService", "Unhandled vold event " + paramInt);
    }
    for (;;)
    {
      return true;
      if (paramArrayOfString.length == 3)
      {
        paramString = paramArrayOfString[1];
        int i = Integer.parseInt(paramArrayOfString[2]);
        if (!SystemProperties.getBoolean("persist.fw.force_adoptable", false))
        {
          paramInt = i;
          if (!this.mForceAdoptable) {}
        }
        else
        {
          paramInt = i | 0x1;
        }
        i = paramInt;
        if (StorageManager.isFileEncryptedNativeOnly()) {
          i = paramInt & 0xFFFFFFFE;
        }
        this.mDisks.put(paramString, new DiskInfo(paramString, i));
        continue;
        if (paramArrayOfString.length == 3)
        {
          paramString = (DiskInfo)this.mDisks.get(paramArrayOfString[1]);
          if (paramString != null)
          {
            paramString.size = Long.parseLong(paramArrayOfString[2]);
            continue;
            paramString = (DiskInfo)this.mDisks.get(paramArrayOfString[1]);
            if (paramString != null)
            {
              Object localObject = new StringBuilder();
              paramInt = 2;
              while (paramInt < paramArrayOfString.length)
              {
                ((StringBuilder)localObject).append(paramArrayOfString[paramInt]).append(' ');
                paramInt += 1;
              }
              paramString.label = ((StringBuilder)localObject).toString().trim();
              continue;
              if (paramArrayOfString.length == 2)
              {
                paramString = (DiskInfo)this.mDisks.get(paramArrayOfString[1]);
                if (paramString != null)
                {
                  onDiskScannedLocked(paramString);
                  continue;
                  if (paramArrayOfString.length == 3)
                  {
                    paramString = (DiskInfo)this.mDisks.get(paramArrayOfString[1]);
                    if (paramString != null)
                    {
                      paramString.sysPath = paramArrayOfString[2];
                      continue;
                      if (paramArrayOfString.length == 2)
                      {
                        paramString = (DiskInfo)this.mDisks.remove(paramArrayOfString[1]);
                        if (paramString != null)
                        {
                          Callbacks.-wrap0(this.mCallbacks, paramString);
                          continue;
                          paramString = paramArrayOfString[1];
                          paramInt = Integer.parseInt(paramArrayOfString[2]);
                          localObject = TextUtils.nullIfEmpty(paramArrayOfString[3]);
                          paramArrayOfString = TextUtils.nullIfEmpty(paramArrayOfString[4]);
                          paramArrayOfString = new VolumeInfo(paramString, paramInt, (DiskInfo)this.mDisks.get(localObject), paramArrayOfString);
                          this.mVolumes.put(paramString, paramArrayOfString);
                          onVolumeCreatedLocked(paramArrayOfString);
                          continue;
                          if (paramArrayOfString.length == 3)
                          {
                            paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                            if (paramString != null)
                            {
                              paramInt = paramString.state;
                              i = Integer.parseInt(paramArrayOfString[2]);
                              paramString.state = i;
                              onVolumeStateChangedLocked(paramString, paramInt, i);
                              continue;
                              if (paramArrayOfString.length == 3)
                              {
                                paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                                if (paramString != null)
                                {
                                  paramString.fsType = paramArrayOfString[2];
                                  continue;
                                  if (paramArrayOfString.length == 3)
                                  {
                                    paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                                    if (paramString != null)
                                    {
                                      paramString.fsUuid = paramArrayOfString[2];
                                      continue;
                                      paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                                      if (paramString != null)
                                      {
                                        localObject = new StringBuilder();
                                        paramInt = 2;
                                        while (paramInt < paramArrayOfString.length)
                                        {
                                          ((StringBuilder)localObject).append(paramArrayOfString[paramInt]).append(' ');
                                          paramInt += 1;
                                        }
                                        paramString.fsLabel = ((StringBuilder)localObject).toString().trim();
                                        continue;
                                        if (paramArrayOfString.length == 3)
                                        {
                                          paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                                          if (paramString != null)
                                          {
                                            paramString.path = paramArrayOfString[2];
                                            continue;
                                            if (paramArrayOfString.length == 3)
                                            {
                                              paramString = (VolumeInfo)this.mVolumes.get(paramArrayOfString[1]);
                                              if (paramString != null)
                                              {
                                                paramString.internalPath = paramArrayOfString[2];
                                                continue;
                                                if (paramArrayOfString.length == 2)
                                                {
                                                  this.mVolumes.remove(paramArrayOfString[1]);
                                                  continue;
                                                  onMoveStatusLocked(Integer.parseInt(paramArrayOfString[1]));
                                                  continue;
                                                  if (paramArrayOfString.length == 7)
                                                  {
                                                    paramString = paramArrayOfString[1];
                                                    localObject = paramArrayOfString[2];
                                                    long l1 = Long.parseLong(paramArrayOfString[3]);
                                                    Long.parseLong(paramArrayOfString[4]);
                                                    long l2 = Long.parseLong(paramArrayOfString[5]);
                                                    long l3 = Long.parseLong(paramArrayOfString[6]);
                                                    ((DropBoxManager)this.mContext.getSystemService(DropBoxManager.class)).addText("storage_benchmark", scrubPath(paramString) + " " + (String)localObject + " " + l1 + " " + l2 + " " + l3);
                                                    paramString = findRecordForPath(paramString);
                                                    if (paramString != null)
                                                    {
                                                      paramString.lastBenchMillis = System.currentTimeMillis();
                                                      writeSettingsLocked();
                                                      continue;
                                                      if (paramArrayOfString.length == 4)
                                                      {
                                                        paramString = paramArrayOfString[1];
                                                        l1 = Long.parseLong(paramArrayOfString[2]);
                                                        l2 = Long.parseLong(paramArrayOfString[3]);
                                                        ((DropBoxManager)this.mContext.getSystemService(DropBoxManager.class)).addText("storage_trim", scrubPath(paramString) + " " + l1 + " " + l2);
                                                        paramString = findRecordForPath(paramString);
                                                        if (paramString != null)
                                                        {
                                                          paramString.lastTrimMillis = System.currentTimeMillis();
                                                          writeSettingsLocked();
                                                        }
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void onMoveStatusLocked(int paramInt)
  {
    if (this.mMoveCallback == null)
    {
      Slog.w("MountService", "Odd, status but no move requested");
      return;
    }
    try
    {
      this.mMoveCallback.onStatusChanged(-1, paramInt, -1L);
      if (paramInt == 82)
      {
        Slog.d("MountService", "Move to " + this.mMoveTargetUuid + " copy phase finshed; persisting");
        this.mPrimaryStorageUuid = this.mMoveTargetUuid;
        writeSettingsLocked();
      }
      if (PackageManager.isMoveStatusFinished(paramInt))
      {
        Slog.d("MountService", "Move to " + this.mMoveTargetUuid + " finished with status " + paramInt);
        this.mMoveCallback = null;
        this.mMoveTargetUuid = null;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private void onUnlockUser(int paramInt)
  {
    Slog.d("MountService", "onUnlockUser " + paramInt);
    try
    {
      this.mConnector.execute("volume", new Object[] { "user_started", Integer.valueOf(paramInt) });
      ArrayMap localArrayMap = this.mVolumes;
      i = 0;
      try
      {
        if (i < this.mVolumes.size())
        {
          VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.valueAt(i);
          if ((localVolumeInfo.isVisibleForRead(paramInt)) && (localVolumeInfo.isMountedReadable()))
          {
            StorageVolume localStorageVolume = localVolumeInfo.buildStorageVolume(this.mContext, paramInt, false);
            this.mHandler.obtainMessage(6, localStorageVolume).sendToTarget();
            String str = VolumeInfo.getEnvironmentForState(localVolumeInfo.getState());
            Callbacks.-wrap2(this.mCallbacks, localStorageVolume.getPath(), str, str);
            if ((isUsbDisk(localVolumeInfo)) && (localVolumeInfo.getState() == 0)) {
              broadcastForMediaStorage(localVolumeInfo, localStorageVolume);
            }
          }
        }
        else
        {
          this.mSystemUnlockedUsers = ArrayUtils.appendInt(this.mSystemUnlockedUsers, paramInt);
          return;
        }
      }
      finally {}
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      for (;;)
      {
        int i;
        continue;
        i += 1;
      }
    }
  }
  
  private void onVolumeCreatedLocked(VolumeInfo paramVolumeInfo)
  {
    boolean bool = SystemProperties.getBoolean("ro.alarm_boot", false);
    VolumeInfo localVolumeInfo;
    if ((!this.mPms.isOnlyCoreApps()) || (bool))
    {
      if (paramVolumeInfo.type != 2) {
        break label236;
      }
      localVolumeInfo = ((StorageManager)this.mContext.getSystemService(StorageManager.class)).findPrivateForEmulated(paramVolumeInfo);
      if ((!Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, this.mPrimaryStorageUuid)) || (!"private".equals(localVolumeInfo.id))) {
        break label163;
      }
      Slog.v("MountService", "Found primary storage at " + paramVolumeInfo);
      paramVolumeInfo.mountFlags |= 0x1;
      paramVolumeInfo.mountFlags |= 0x2;
      this.mHandler.obtainMessage(5, paramVolumeInfo).sendToTarget();
    }
    label163:
    while (!Objects.equals(localVolumeInfo.fsUuid, this.mPrimaryStorageUuid))
    {
      return;
      Slog.d("MountService", "System booted in core-only mode; ignoring volume " + paramVolumeInfo.getId());
      return;
    }
    Slog.v("MountService", "Found primary storage at " + paramVolumeInfo);
    paramVolumeInfo.mountFlags |= 0x1;
    paramVolumeInfo.mountFlags |= 0x2;
    this.mHandler.obtainMessage(5, paramVolumeInfo).sendToTarget();
    return;
    label236:
    if (paramVolumeInfo.type == 0)
    {
      if ((Objects.equals("primary_physical", this.mPrimaryStorageUuid)) && (paramVolumeInfo.disk.isDefaultPrimary()))
      {
        Slog.v("MountService", "Found primary storage at " + paramVolumeInfo);
        paramVolumeInfo.mountFlags |= 0x1;
        paramVolumeInfo.mountFlags |= 0x2;
      }
      if (isUsbDisk(paramVolumeInfo))
      {
        Slog.d("MountService", "Make visible for usb storage.");
        paramVolumeInfo.mountFlags |= 0x2;
      }
      if (paramVolumeInfo.disk.isAdoptable()) {
        paramVolumeInfo.mountFlags |= 0x2;
      }
      paramVolumeInfo.mountUserId = this.mCurrentUserId;
      this.mHandler.obtainMessage(5, paramVolumeInfo).sendToTarget();
      return;
    }
    if (paramVolumeInfo.type == 1)
    {
      this.mHandler.obtainMessage(5, paramVolumeInfo).sendToTarget();
      return;
    }
    Slog.d("MountService", "Skipping automatic mounting of " + paramVolumeInfo);
  }
  
  private void onVolumeStateChangedLocked(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2)
  {
    if ((!paramVolumeInfo.isMountedReadable()) || (TextUtils.isEmpty(paramVolumeInfo.fsUuid))) {}
    for (;;)
    {
      Callbacks.-wrap5(this.mCallbacks, paramVolumeInfo, paramInt1, paramInt2);
      if ((this.mBootCompleted) && (isBroadcastWorthy(paramVolumeInfo)))
      {
        localObject = new Intent("android.os.storage.action.VOLUME_STATE_CHANGED");
        ((Intent)localObject).putExtra("android.os.storage.extra.VOLUME_ID", paramVolumeInfo.id);
        ((Intent)localObject).putExtra("android.os.storage.extra.VOLUME_STATE", paramInt2);
        ((Intent)localObject).putExtra("android.os.storage.extra.FS_UUID", paramVolumeInfo.fsUuid);
        ((Intent)localObject).addFlags(83886080);
        this.mHandler.obtainMessage(7, localObject).sendToTarget();
      }
      Object localObject = VolumeInfo.getEnvironmentForState(paramInt1);
      String str = VolumeInfo.getEnvironmentForState(paramInt2);
      if ((paramVolumeInfo.getPath() != null) && (DBG)) {
        Slog.d("MountService", "onVolumeStateChangedLocked: " + paramVolumeInfo.getPath() + " (" + (String)localObject + " -> " + str + ")");
      }
      if (Objects.equals(localObject, str)) {
        break;
      }
      int[] arrayOfInt = this.mSystemUnlockedUsers;
      paramInt1 = 0;
      paramInt2 = arrayOfInt.length;
      while (paramInt1 < paramInt2)
      {
        int i = arrayOfInt[paramInt1];
        if (paramVolumeInfo.isVisibleForRead(i))
        {
          StorageVolume localStorageVolume = paramVolumeInfo.buildStorageVolume(this.mContext, i, false);
          this.mHandler.obtainMessage(6, localStorageVolume).sendToTarget();
          Callbacks.-wrap2(this.mCallbacks, localStorageVolume.getPath(), (String)localObject, str);
        }
        paramInt1 += 1;
      }
      localObject = (VolumeRecord)this.mRecords.get(paramVolumeInfo.fsUuid);
      if (localObject == null)
      {
        localObject = new VolumeRecord(paramVolumeInfo.type, paramVolumeInfo.fsUuid);
        ((VolumeRecord)localObject).partGuid = paramVolumeInfo.partGuid;
        ((VolumeRecord)localObject).createdMillis = System.currentTimeMillis();
        if (paramVolumeInfo.type == 1) {
          ((VolumeRecord)localObject).nickname = paramVolumeInfo.disk.getDescription();
        }
        this.mRecords.put(((VolumeRecord)localObject).fsUuid, localObject);
        writeSettingsLocked();
      }
      else if (TextUtils.isEmpty(((VolumeRecord)localObject).partGuid))
      {
        ((VolumeRecord)localObject).partGuid = paramVolumeInfo.partGuid;
        writeSettingsLocked();
      }
    }
    if ((paramVolumeInfo.type == 0) && (paramVolumeInfo.state == 5)) {
      this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(5, paramVolumeInfo.path));
    }
  }
  
  private void readSettingsLocked()
  {
    this.mRecords.clear();
    this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
    this.mForceAdoptable = false;
    Object localObject4 = null;
    Object localObject5 = null;
    Object localObject1 = null;
    Object localObject6 = null;
    int i;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mSettingsFile.openRead();
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localXmlPullParser = Xml.newPullParser();
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        FileInputStream localFileInputStream;
        XmlPullParser localXmlPullParser;
        boolean bool;
        return;
        i = 1;
        continue;
        i = 1;
        continue;
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        if (!"volume".equals(localObject7)) {
          continue;
        }
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        Object localObject7 = readVolumeRecord(localXmlPullParser);
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        this.mRecords.put(((VolumeRecord)localObject7).fsUuid, localObject7);
        continue;
      }
      catch (IOException localIOException)
      {
        localObject2 = localObject4;
        Slog.wtf("MountService", "Failed reading metadata", localIOException);
        return;
        IoUtils.closeQuietly(localIOException);
        return;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        localObject2 = localObject5;
        Slog.wtf("MountService", "Failed reading metadata", localXmlPullParserException);
        return;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject2);
      }
      localObject6 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      i = localXmlPullParser.next();
      if (i != 1)
      {
        if (i != 2) {
          continue;
        }
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject7 = localXmlPullParser.getName();
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        if ("volumes".equals(localObject7))
        {
          localObject6 = localFileInputStream;
          localObject4 = localFileInputStream;
          localObject5 = localFileInputStream;
          localObject1 = localFileInputStream;
          i = XmlUtils.readIntAttribute(localXmlPullParser, "version", 1);
          localObject6 = localFileInputStream;
          localObject4 = localFileInputStream;
          localObject5 = localFileInputStream;
          localObject1 = localFileInputStream;
          bool = SystemProperties.getBoolean("ro.vold.primary_physical", false);
          if (i < 3)
          {
            if (i < 2) {
              break label452;
            }
            if (!bool) {
              continue;
            }
            break label452;
          }
        }
      }
    }
    for (;;)
    {
      if (i != 0)
      {
        localObject6 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        this.mPrimaryStorageUuid = XmlUtils.readStringAttribute(localXmlPullParser, "primaryStorageUuid");
      }
      localObject6 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      this.mForceAdoptable = XmlUtils.readBooleanAttribute(localXmlPullParser, "forceAdoptable", false);
      break;
      Object localObject2;
      label452:
      i = 0;
    }
  }
  
  public static VolumeRecord readVolumeRecord(XmlPullParser paramXmlPullParser)
    throws IOException
  {
    VolumeRecord localVolumeRecord = new VolumeRecord(XmlUtils.readIntAttribute(paramXmlPullParser, "type"), XmlUtils.readStringAttribute(paramXmlPullParser, "fsUuid"));
    localVolumeRecord.partGuid = XmlUtils.readStringAttribute(paramXmlPullParser, "partGuid");
    localVolumeRecord.nickname = XmlUtils.readStringAttribute(paramXmlPullParser, "nickname");
    localVolumeRecord.userFlags = XmlUtils.readIntAttribute(paramXmlPullParser, "userFlags");
    localVolumeRecord.createdMillis = XmlUtils.readLongAttribute(paramXmlPullParser, "createdMillis");
    localVolumeRecord.lastTrimMillis = XmlUtils.readLongAttribute(paramXmlPullParser, "lastTrimMillis");
    localVolumeRecord.lastBenchMillis = XmlUtils.readLongAttribute(paramXmlPullParser, "lastBenchMillis");
    return localVolumeRecord;
  }
  
  private void remountUidExternalStorage(int paramInt1, int paramInt2)
  {
    waitForReady();
    String str = "none";
    switch (paramInt2)
    {
    }
    for (;;)
    {
      try
      {
        this.mConnector.execute("volume", new Object[] { "remount_uid", Integer.valueOf(paramInt1), str });
        return;
      }
      catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
      {
        Slog.w("MountService", "Failed to remount UID " + paramInt1 + " as " + str + ": " + localNativeDaemonConnectorException);
      }
      str = "default";
      continue;
      str = "read";
      continue;
      str = "write";
    }
  }
  
  private void removeObbStateLocked(ObbState paramObbState)
  {
    IBinder localIBinder = paramObbState.getBinder();
    List localList = (List)this.mObbMounts.get(localIBinder);
    if (localList != null)
    {
      if (localList.remove(paramObbState)) {
        paramObbState.unlink();
      }
      if (localList.isEmpty()) {
        this.mObbMounts.remove(localIBinder);
      }
    }
    this.mObbPathToStateMap.remove(paramObbState.rawPath);
  }
  
  private void resetIfReadyAndConnected()
  {
    Slog.d("MountService", "Thinking about reset, mSystemReady=" + this.mSystemReady + ", mDaemonConnected=" + this.mDaemonConnected);
    Object localObject3;
    if ((this.mSystemReady) && (this.mDaemonConnected))
    {
      localObject3 = ((UserManager)this.mContext.getSystemService(UserManager.class)).getUsers();
      killMediaProvider((List)localObject3);
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        int[] arrayOfInt = this.mSystemUnlockedUsers;
        this.mDisks.clear();
        this.mVolumes.clear();
        addInternalVolumeLocked();
        try
        {
          this.mConnector.execute("volume", new Object[] { "reset" });
          ??? = ((Iterable)localObject3).iterator();
          if (((Iterator)???).hasNext())
          {
            localObject3 = (UserInfo)((Iterator)???).next();
            this.mConnector.execute("volume", new Object[] { "user_added", Integer.valueOf(((UserInfo)localObject3).id), Integer.valueOf(((UserInfo)localObject3).serialNumber) });
            continue;
            return;
          }
        }
        catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
        {
          Slog.w("MountService", "Failed to reset vold", localNativeDaemonConnectorException);
        }
      }
      int j = localObject1.length;
      int i = 0;
      while (i < j)
      {
        int k = localObject1[i];
        this.mConnector.execute("volume", new Object[] { "user_started", Integer.valueOf(k) });
        i += 1;
      }
    }
  }
  
  private String scrubPath(String paramString)
  {
    if (paramString.startsWith(Environment.getDataDirectory().getAbsolutePath())) {
      return "internal";
    }
    paramString = findRecordForPath(paramString);
    if ((paramString == null) || (paramString.createdMillis == 0L)) {
      return "unknown";
    }
    return "ext:" + (int)((System.currentTimeMillis() - paramString.createdMillis) / 604800000L) + "w";
  }
  
  private boolean shouldBenchmark()
  {
    long l1 = Settings.Global.getLong(this.mContext.getContentResolver(), "storage_benchmark_interval", 604800000L);
    if (l1 == -1L) {
      return false;
    }
    if (l1 == 0L) {
      return true;
    }
    Object localObject1 = this.mLock;
    int i = 0;
    try
    {
      while (i < this.mVolumes.size())
      {
        VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.valueAt(i);
        VolumeRecord localVolumeRecord = (VolumeRecord)this.mRecords.get(localVolumeInfo.fsUuid);
        if ((localVolumeInfo.isMountedWritable()) && (localVolumeRecord != null))
        {
          long l2 = System.currentTimeMillis();
          long l3 = localVolumeRecord.lastBenchMillis;
          if (l2 - l3 >= l1) {
            return true;
          }
        }
        i += 1;
      }
      return false;
    }
    finally {}
  }
  
  private void start()
  {
    this.mConnectorThread.start();
    this.mCryptConnectorThread.start();
  }
  
  private void systemReady()
  {
    this.mSystemReady = true;
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  private void waitForLatch(CountDownLatch paramCountDownLatch, String paramString)
  {
    try
    {
      waitForLatch(paramCountDownLatch, paramString, -1L);
      return;
    }
    catch (TimeoutException paramCountDownLatch) {}
  }
  
  private void waitForLatch(CountDownLatch paramCountDownLatch, String paramString, long paramLong)
    throws TimeoutException
  {
    long l = SystemClock.elapsedRealtime();
    do
    {
      try
      {
        if (paramCountDownLatch.await(5000L, TimeUnit.MILLISECONDS)) {
          return;
        }
        Slog.w("MountService", "Thread " + Thread.currentThread().getName() + " still waiting for " + paramString + "...");
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Slog.w("MountService", "Interrupt while waiting for " + paramString);
        }
      }
    } while ((paramLong <= 0L) || (SystemClock.elapsedRealtime() <= l + paramLong));
    throw new TimeoutException("Thread " + Thread.currentThread().getName() + " gave up waiting for " + paramString + " after " + paramLong + "ms");
  }
  
  private void waitForReady()
  {
    waitForLatch(this.mConnectedSignal, "mConnectedSignal");
  }
  
  private void warnOnNotMounted()
  {
    Object localObject1 = this.mLock;
    int i = 0;
    try
    {
      while (i < this.mVolumes.size())
      {
        VolumeInfo localVolumeInfo = (VolumeInfo)this.mVolumes.valueAt(i);
        if (localVolumeInfo.isPrimary())
        {
          boolean bool = localVolumeInfo.isMountedWritable();
          if (bool) {
            return;
          }
        }
        i += 1;
      }
      Slog.w("MountService", "No primary storage mounted!");
      return;
    }
    finally {}
  }
  
  private void writeSettingsLocked()
  {
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = this.mSettingsFile.startWrite();
      localObject = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "volumes");
      localObject = localFileOutputStream;
      XmlUtils.writeIntAttribute(localFastXmlSerializer, "version", 3);
      localObject = localFileOutputStream;
      XmlUtils.writeStringAttribute(localFastXmlSerializer, "primaryStorageUuid", this.mPrimaryStorageUuid);
      localObject = localFileOutputStream;
      XmlUtils.writeBooleanAttribute(localFastXmlSerializer, "forceAdoptable", this.mForceAdoptable);
      localObject = localFileOutputStream;
      int j = this.mRecords.size();
      int i = 0;
      while (i < j)
      {
        localObject = localFileOutputStream;
        writeVolumeRecord(localFastXmlSerializer, (VolumeRecord)this.mRecords.valueAt(i));
        i += 1;
      }
      localObject = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "volumes");
      localObject = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject = localFileOutputStream;
      this.mSettingsFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      while (localObject == null) {}
      this.mSettingsFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  public static void writeVolumeRecord(XmlSerializer paramXmlSerializer, VolumeRecord paramVolumeRecord)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "volume");
    XmlUtils.writeIntAttribute(paramXmlSerializer, "type", paramVolumeRecord.type);
    XmlUtils.writeStringAttribute(paramXmlSerializer, "fsUuid", paramVolumeRecord.fsUuid);
    XmlUtils.writeStringAttribute(paramXmlSerializer, "partGuid", paramVolumeRecord.partGuid);
    XmlUtils.writeStringAttribute(paramXmlSerializer, "nickname", paramVolumeRecord.nickname);
    XmlUtils.writeIntAttribute(paramXmlSerializer, "userFlags", paramVolumeRecord.userFlags);
    XmlUtils.writeLongAttribute(paramXmlSerializer, "createdMillis", paramVolumeRecord.createdMillis);
    XmlUtils.writeLongAttribute(paramXmlSerializer, "lastTrimMillis", paramVolumeRecord.lastTrimMillis);
    XmlUtils.writeLongAttribute(paramXmlSerializer, "lastBenchMillis", paramVolumeRecord.lastBenchMillis);
    paramXmlSerializer.endTag(null, "volume");
  }
  
  public void addUserKeyAuth(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    enforcePermission("android.permission.STORAGE_INTERNAL");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "add_user_key_auth", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), encodeBytes(paramArrayOfByte1), encodeBytes(paramArrayOfByte2) });
      return;
    }
    catch (NativeDaemonConnectorException paramArrayOfByte1)
    {
      throw paramArrayOfByte1.rethrowAsParcelableException();
    }
  }
  
  public long benchmark(String paramString)
  {
    enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
    waitForReady();
    try
    {
      long l = Long.parseLong(this.mConnector.execute(180000L, "volume", new Object[] { "benchmark", paramString }).getMessage());
      return l;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
    catch (NativeDaemonTimeoutException paramString) {}
    return Long.MAX_VALUE;
  }
  
  /* Error */
  public int changeEncryptionPassword(int paramInt, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   4: ldc_w 851
    //   7: ldc_w 853
    //   10: invokevirtual 856	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   13: aload_0
    //   14: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   17: ldc_w 1688
    //   20: invokestatic 493	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   23: invokestatic 1694	com/android/internal/widget/ILockSettings$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/widget/ILockSettings;
    //   26: astore 5
    //   28: ldc_w 1696
    //   31: astore_3
    //   32: aload 5
    //   34: invokeinterface 1701 1 0
    //   39: astore 4
    //   41: aload 4
    //   43: astore_3
    //   44: aload_0
    //   45: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   48: ldc_w 860
    //   51: iconst_4
    //   52: anewarray 409	java/lang/Object
    //   55: dup
    //   56: iconst_0
    //   57: ldc_w 1703
    //   60: aastore
    //   61: dup
    //   62: iconst_1
    //   63: getstatic 390	com/android/server/MountService:CRYPTO_TYPES	[Ljava/lang/String;
    //   66: iload_1
    //   67: aaload
    //   68: aastore
    //   69: dup
    //   70: iconst_2
    //   71: new 830	com/android/server/NativeDaemonConnector$SensitiveArg
    //   74: dup
    //   75: aload_3
    //   76: invokespecial 834	com/android/server/NativeDaemonConnector$SensitiveArg:<init>	(Ljava/lang/Object;)V
    //   79: aastore
    //   80: dup
    //   81: iconst_3
    //   82: new 830	com/android/server/NativeDaemonConnector$SensitiveArg
    //   85: dup
    //   86: aload_2
    //   87: invokespecial 834	com/android/server/NativeDaemonConnector$SensitiveArg:<init>	(Ljava/lang/Object;)V
    //   90: aastore
    //   91: invokevirtual 866	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   94: astore_2
    //   95: aload 5
    //   97: invokeinterface 1706 1 0
    //   102: aload_2
    //   103: invokevirtual 1682	com/android/server/NativeDaemonEvent:getMessage	()Ljava/lang/String;
    //   106: invokestatic 1205	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   109: istore_1
    //   110: iload_1
    //   111: ireturn
    //   112: astore 4
    //   114: ldc -123
    //   116: new 605	java/lang/StringBuilder
    //   119: dup
    //   120: invokespecial 606	java/lang/StringBuilder:<init>	()V
    //   123: ldc_w 1708
    //   126: invokevirtual 612	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload 4
    //   131: invokevirtual 745	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   134: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   137: invokestatic 625	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   140: pop
    //   141: goto -97 -> 44
    //   144: astore_3
    //   145: ldc -123
    //   147: new 605	java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial 606	java/lang/StringBuilder:<init>	()V
    //   154: ldc_w 1710
    //   157: invokevirtual 612	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload_3
    //   161: invokevirtual 745	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   164: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   167: invokestatic 625	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   170: pop
    //   171: goto -69 -> 102
    //   174: astore_2
    //   175: aload_2
    //   176: invokevirtual 869	com/android/server/NativeDaemonConnectorException:getCode	()I
    //   179: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	this	MountService
    //   0	180	1	paramInt	int
    //   0	180	2	paramString	String
    //   31	45	3	localObject	Object
    //   144	17	3	localRemoteException1	RemoteException
    //   39	3	4	str	String
    //   112	18	4	localRemoteException2	RemoteException
    //   26	70	5	localILockSettings	com.android.internal.widget.ILockSettings
    // Exception table:
    //   from	to	target	type
    //   32	41	112	android/os/RemoteException
    //   95	102	144	android/os/RemoteException
    //   44	95	174	com/android/server/NativeDaemonConnectorException
    //   95	102	174	com/android/server/NativeDaemonConnectorException
    //   102	110	174	com/android/server/NativeDaemonConnectorException
    //   145	171	174	com/android/server/NativeDaemonConnectorException
  }
  
  public void clearPassword()
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "only keyguard can clear password");
    if (!isReady()) {
      return;
    }
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "clearpw" });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public int createSecureContainer(String paramString1, int paramInt1, String arg3, String paramString3, int paramInt2, boolean paramBoolean)
  {
    enforcePermission("android.permission.ASEC_CREATE");
    waitForReady();
    warnOnNotMounted();
    int i = 0;
    try
    {
      localNativeDaemonConnector = this.mConnector;
      localSensitiveArg = new NativeDaemonConnector.SensitiveArg(paramString3);
      if (!paramBoolean) {
        break label126;
      }
      paramString3 = "1";
    }
    catch (NativeDaemonConnectorException ???)
    {
      synchronized (this.mAsecMountSet)
      {
        for (;;)
        {
          NativeDaemonConnector localNativeDaemonConnector;
          NativeDaemonConnector.SensitiveArg localSensitiveArg;
          this.mAsecMountSet.add(paramString1);
          return paramInt1;
          paramString3 = "0";
        }
        ??? = ???;
        paramInt1 = -1;
      }
    }
    localNativeDaemonConnector.execute("asec", new Object[] { "create", paramString1, Integer.valueOf(paramInt1), ???, localSensitiveArg, Integer.valueOf(paramInt2), paramString3 });
    paramInt1 = i;
    if (paramInt1 != 0) {}
  }
  
  /* Error */
  public void createUserKey(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: aload_0
    //   4: ldc_w 1656
    //   7: invokespecial 1658	com/android/server/MountService:enforcePermission	(Ljava/lang/String;)V
    //   10: aload_0
    //   11: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   14: aload_0
    //   15: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   18: astore 5
    //   20: iload_3
    //   21: ifeq +45 -> 66
    //   24: aload 5
    //   26: ldc_w 860
    //   29: iconst_4
    //   30: anewarray 409	java/lang/Object
    //   33: dup
    //   34: iconst_0
    //   35: ldc_w 1732
    //   38: aastore
    //   39: dup
    //   40: iconst_1
    //   41: iload_1
    //   42: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   45: aastore
    //   46: dup
    //   47: iconst_2
    //   48: iload_2
    //   49: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: iload 4
    //   57: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   60: aastore
    //   61: invokevirtual 866	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   64: pop
    //   65: return
    //   66: iconst_0
    //   67: istore 4
    //   69: goto -45 -> 24
    //   72: astore 5
    //   74: aload 5
    //   76: invokevirtual 1666	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	MountService
    //   0	80	1	paramInt1	int
    //   0	80	2	paramInt2	int
    //   0	80	3	paramBoolean	boolean
    //   1	67	4	i	int
    //   18	7	5	localNativeDaemonConnector	NativeDaemonConnector
    //   72	3	5	localNativeDaemonConnectorException	NativeDaemonConnectorException
    // Exception table:
    //   from	to	target	type
    //   14	20	72	com/android/server/NativeDaemonConnectorException
    //   24	65	72	com/android/server/NativeDaemonConnectorException
  }
  
  public int decryptStorage(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("password cannot be empty");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to access the crypt keeper");
    waitForReady();
    try
    {
      int i = Integer.parseInt(this.mCryptConnector.execute("cryptfs", new Object[] { "checkpw", new NativeDaemonConnector.SensitiveArg(paramString) }).getMessage());
      if (i == 0) {
        this.mHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            try
            {
              MountService.-get4(MountService.this).execute("cryptfs", new Object[] { "restart" });
              return;
            }
            catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
            {
              Slog.e("MountService", "problem executing in background", localNativeDaemonConnectorException);
            }
          }
        }, 1000L);
      }
      return i;
    }
    catch (NativeDaemonConnectorException paramString) {}
    return paramString.getCode();
  }
  
  public int destroySecureContainer(String paramString, boolean paramBoolean)
  {
    enforcePermission("android.permission.ASEC_DESTROY");
    waitForReady();
    warnOnNotMounted();
    Runtime.getRuntime().gc();
    i = 0;
    try
    {
      ??? = new NativeDaemonConnector.Command("asec", new Object[] { "destroy", paramString });
      if (paramBoolean) {
        ((NativeDaemonConnector.Command)???).appendArg("force");
      }
      this.mConnector.execute((NativeDaemonConnector.Command)???);
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      synchronized (this.mAsecMountSet)
      {
        for (;;)
        {
          if (this.mAsecMountSet.contains(paramString)) {
            this.mAsecMountSet.remove(paramString);
          }
          return i;
          localNativeDaemonConnectorException = localNativeDaemonConnectorException;
          if (localNativeDaemonConnectorException.getCode() != 405) {
            break;
          }
          i = -7;
        }
        i = -1;
      }
    }
    if (i != 0) {}
  }
  
  public void destroyUserKey(int paramInt)
  {
    enforcePermission("android.permission.STORAGE_INTERNAL");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "destroy_user_key", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void destroyUserStorage(String paramString, int paramInt1, int paramInt2)
  {
    enforcePermission("android.permission.STORAGE_INTERNAL");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "destroy_user_storage", escapeNull(paramString), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "MountService");
    paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ", 160);
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println("Disks:");
        paramPrintWriter.increaseIndent();
        i = 0;
        if (i < this.mDisks.size())
        {
          ((DiskInfo)this.mDisks.valueAt(i)).dump(paramPrintWriter);
          i += 1;
          continue;
        }
        paramPrintWriter.decreaseIndent();
        paramPrintWriter.println();
        paramPrintWriter.println("Volumes:");
        paramPrintWriter.increaseIndent();
        i = 0;
        if (i < this.mVolumes.size())
        {
          localObject2 = (VolumeInfo)this.mVolumes.valueAt(i);
          if ("private".equals(((VolumeInfo)localObject2).id)) {
            break label688;
          }
          ((VolumeInfo)localObject2).dump(paramPrintWriter);
        }
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("Records:");
      paramPrintWriter.increaseIndent();
      int i = 0;
      while (i < this.mRecords.size())
      {
        ((VolumeRecord)this.mRecords.valueAt(i)).dump(paramPrintWriter);
        i += 1;
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("Primary storage UUID: " + this.mPrimaryStorageUuid);
      paramPrintWriter.println("Force adoptable: " + this.mForceAdoptable);
      paramPrintWriter.println();
      paramPrintWriter.println("Local unlocked users: " + Arrays.toString(this.mLocalUnlockedUsers));
      paramPrintWriter.println("System unlocked users: " + Arrays.toString(this.mSystemUnlockedUsers));
      Object localObject3;
      for (;;)
      {
        synchronized (this.mObbMounts)
        {
          paramPrintWriter.println();
          paramPrintWriter.println("mObbMounts:");
          paramPrintWriter.increaseIndent();
          localObject2 = this.mObbMounts.entrySet().iterator();
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          paramPrintWriter.println(((Map.Entry)localObject3).getKey() + ":");
          paramPrintWriter.increaseIndent();
          localObject3 = ((List)((Map.Entry)localObject3).getValue()).iterator();
          if (((Iterator)localObject3).hasNext()) {
            paramPrintWriter.println((ObbState)((Iterator)localObject3).next());
          }
        }
        paramPrintWriter.decreaseIndent();
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("mObbPathToStateMap:");
      paramPrintWriter.increaseIndent();
      Object localObject2 = this.mObbPathToStateMap.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        paramPrintWriter.print((String)((Map.Entry)localObject3).getKey());
        paramPrintWriter.print(" -> ");
        paramPrintWriter.println(((Map.Entry)localObject3).getValue());
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("mConnector:");
      paramPrintWriter.increaseIndent();
      this.mConnector.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("mCryptConnector:");
      paramPrintWriter.increaseIndent();
      this.mCryptConnector.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.print("Last maintenance: ");
      paramPrintWriter.println(TimeUtils.formatForLogging(this.mLastMaintenance));
      return;
      label688:
      i += 1;
    }
  }
  
  public int encryptStorage(int paramInt, String paramString)
  {
    return encryptStorageExtended(paramInt, paramString, false);
  }
  
  public int encryptWipeStorage(int paramInt, String paramString)
  {
    return encryptStorageExtended(paramInt, paramString, true);
  }
  
  public int finalizeSecureContainer(String paramString)
  {
    enforcePermission("android.permission.ASEC_CREATE");
    warnOnNotMounted();
    try
    {
      this.mConnector.execute("asec", new Object[] { "finalize", paramString });
      return 0;
    }
    catch (NativeDaemonConnectorException paramString) {}
    return -1;
  }
  
  public void finishMediaUpdate()
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("no permission to call finishMediaUpdate()");
    }
    if (this.mUnmountSignal != null)
    {
      this.mUnmountSignal.countDown();
      return;
    }
    Slog.w("MountService", "Odd, nobody asked to unmount?");
  }
  
  public int fixPermissionsSecureContainer(String paramString1, int paramInt, String paramString2)
  {
    enforcePermission("android.permission.ASEC_CREATE");
    warnOnNotMounted();
    try
    {
      this.mConnector.execute("asec", new Object[] { "fixperms", paramString1, Integer.valueOf(paramInt), paramString2 });
      return 0;
    }
    catch (NativeDaemonConnectorException paramString1) {}
    return -1;
  }
  
  public void fixateNewestUserKeyAuth(int paramInt)
  {
    enforcePermission("android.permission.STORAGE_INTERNAL");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "fixate_newest_user_key_auth", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void forgetAllVolumes()
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    Object localObject1 = this.mLock;
    int i = 0;
    try
    {
      while (i < this.mRecords.size())
      {
        String str = (String)this.mRecords.keyAt(i);
        VolumeRecord localVolumeRecord = (VolumeRecord)this.mRecords.valueAt(i);
        if (!TextUtils.isEmpty(localVolumeRecord.partGuid)) {
          this.mHandler.obtainMessage(9, localVolumeRecord.partGuid).sendToTarget();
        }
        Callbacks.-wrap3(this.mCallbacks, str);
        i += 1;
      }
      this.mRecords.clear();
      if (!Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, this.mPrimaryStorageUuid)) {
        this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
      }
      writeSettingsLocked();
      this.mHandler.obtainMessage(10).sendToTarget();
      return;
    }
    finally {}
  }
  
  public void forgetVolume(String paramString)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    Preconditions.checkNotNull(paramString);
    synchronized (this.mLock)
    {
      VolumeRecord localVolumeRecord = (VolumeRecord)this.mRecords.remove(paramString);
      if ((localVolumeRecord == null) || (TextUtils.isEmpty(localVolumeRecord.partGuid)))
      {
        Callbacks.-wrap3(this.mCallbacks, paramString);
        if (Objects.equals(this.mPrimaryStorageUuid, paramString))
        {
          this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
          this.mHandler.obtainMessage(10).sendToTarget();
        }
        writeSettingsLocked();
        return;
      }
      this.mHandler.obtainMessage(9, localVolumeRecord.partGuid).sendToTarget();
    }
  }
  
  public void format(String paramString)
  {
    enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
    waitForReady();
    paramString = findVolumeByIdOrThrow(paramString);
    try
    {
      this.mConnector.execute("volume", new Object[] { "format", paramString.id, "auto" });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public int formatVolume(String paramString)
  {
    format(findVolumeIdForPathOrThrow(paramString));
    return 0;
  }
  
  public DiskInfo[] getDisks()
  {
    synchronized (this.mLock)
    {
      DiskInfo[] arrayOfDiskInfo = new DiskInfo[this.mDisks.size()];
      int i = 0;
      while (i < this.mDisks.size())
      {
        arrayOfDiskInfo[i] = ((DiskInfo)this.mDisks.valueAt(i));
        i += 1;
      }
      return arrayOfDiskInfo;
    }
  }
  
  public int getEncryptionState()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to access the crypt keeper");
    waitForReady();
    try
    {
      int i = Integer.parseInt(this.mCryptConnector.execute("cryptfs", new Object[] { "cryptocomplete" }).getMessage());
      return i;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.w("MountService", "Error in communicating with cryptfs in validating");
      return -1;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Slog.w("MountService", "Unable to parse result from cryptfs cryptocomplete");
    }
    return -1;
  }
  
  public String getField(String paramString)
    throws RemoteException
  {
    int i = 0;
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "no permission to access the crypt keeper");
    waitForReady();
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mCryptConnector.executeForList("cryptfs", new Object[] { "getfield", paramString }), 113);
      paramString = new String();
      int j = arrayOfString.length;
      while (i < j)
      {
        String str = arrayOfString[i];
        paramString = paramString + str;
        i += 1;
      }
      return paramString;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public String getMountedObbPath(String paramString)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    waitForReady();
    warnOnNotMounted();
    ObbState localObbState;
    synchronized (this.mObbMounts)
    {
      localObbState = (ObbState)this.mObbPathToStateMap.get(paramString);
      if (localObbState == null)
      {
        Slog.w("MountService", "Failed to find OBB mounted at " + paramString);
        return null;
      }
    }
    try
    {
      paramString = this.mConnector.execute("obb", new Object[] { "path", localObbState.canonicalPath });
      paramString.checkCode(211);
      paramString = paramString.getMessage();
      return paramString;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      int i = paramString.getCode();
      if (i == 406) {
        return null;
      }
      throw new IllegalStateException(String.format("Unexpected response code %d", new Object[] { Integer.valueOf(i) }));
    }
  }
  
  public String getPassword()
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "only keyguard can retrieve password");
    if (!isReady()) {
      return new String();
    }
    try
    {
      Object localObject = this.mCryptConnector.execute("cryptfs", new Object[] { "getpw" });
      if ("-1".equals(((NativeDaemonEvent)localObject).getMessage())) {
        return null;
      }
      localObject = ((NativeDaemonEvent)localObject).getMessage();
      return (String)localObject;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Slog.e("MountService", "Invalid response to getPassword");
      return null;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public int getPasswordType()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "no permission to access the crypt keeper");
    waitForReady();
    for (;;)
    {
      int i;
      try
      {
        NativeDaemonEvent localNativeDaemonEvent = this.mCryptConnector.execute("cryptfs", new Object[] { "getpwtype" });
        i = 0;
        if (i < CRYPTO_TYPES.length)
        {
          if (CRYPTO_TYPES[i].equals(localNativeDaemonEvent.getMessage())) {
            return i;
          }
        }
        else {
          throw new IllegalStateException("unexpected return from cryptfs");
        }
      }
      catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
      {
        throw localNativeDaemonConnectorException.rethrowAsParcelableException();
      }
      i += 1;
    }
  }
  
  public String getPrimaryStorageUuid()
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    synchronized (this.mLock)
    {
      String str = this.mPrimaryStorageUuid;
      return str;
    }
  }
  
  public String getSecureContainerFilesystemPath(String paramString)
  {
    enforcePermission("android.permission.ASEC_ACCESS");
    waitForReady();
    warnOnNotMounted();
    try
    {
      Object localObject = this.mConnector.execute("asec", new Object[] { "fspath", paramString });
      ((NativeDaemonEvent)localObject).checkCode(211);
      localObject = ((NativeDaemonEvent)localObject).getMessage();
      return (String)localObject;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      int i = localNativeDaemonConnectorException.getCode();
      if (i == 406)
      {
        Slog.i("MountService", String.format("Container '%s' not found", new Object[] { paramString }));
        return null;
      }
      throw new IllegalStateException(String.format("Unexpected response code %d", new Object[] { Integer.valueOf(i) }));
    }
  }
  
  public String[] getSecureContainerList()
  {
    enforcePermission("android.permission.ASEC_ACCESS");
    waitForReady();
    warnOnNotMounted();
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("asec", new Object[] { "list" }), 111);
      return arrayOfString;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException) {}
    return new String[0];
  }
  
  public String getSecureContainerPath(String paramString)
  {
    enforcePermission("android.permission.ASEC_ACCESS");
    waitForReady();
    warnOnNotMounted();
    try
    {
      Object localObject = this.mConnector.execute("asec", new Object[] { "path", paramString });
      ((NativeDaemonEvent)localObject).checkCode(211);
      localObject = ((NativeDaemonEvent)localObject).getMessage();
      return (String)localObject;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      int i = localNativeDaemonConnectorException.getCode();
      if (i == 406)
      {
        Slog.i("MountService", String.format("Container '%s' not found", new Object[] { paramString }));
        return null;
      }
      throw new IllegalStateException(String.format("Unexpected response code %d", new Object[] { Integer.valueOf(i) }));
    }
  }
  
  public int[] getStorageUsers(String paramString)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("storage", new Object[] { "users", paramString }), 112);
      int[] arrayOfInt = new int[arrayOfString.length];
      int i = 0;
      while (i < arrayOfString.length)
      {
        paramString = arrayOfString[i].split(" ");
        try
        {
          arrayOfInt[i] = Integer.parseInt(paramString[0]);
          i += 1;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Slog.e("MountService", String.format("Error parsing pid %s", new Object[] { paramString[0] }));
          return new int[0];
        }
      }
      return arrayOfInt;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      Slog.e("MountService", "Failed to retrieve storage users list", paramString);
    }
    return new int[0];
  }
  
  /* Error */
  public StorageVolume[] getVolumeList(int paramInt1, String paramString, int paramInt2)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 1094	android/os/UserHandle:getUserId	(I)I
    //   4: istore 8
    //   6: iload_3
    //   7: sipush 256
    //   10: iand
    //   11: ifeq +156 -> 167
    //   14: iconst_1
    //   15: istore 4
    //   17: iload_3
    //   18: sipush 512
    //   21: iand
    //   22: ifeq +151 -> 173
    //   25: iconst_1
    //   26: istore 5
    //   28: iload_3
    //   29: sipush 1024
    //   32: iand
    //   33: ifeq +146 -> 179
    //   36: iconst_1
    //   37: istore_3
    //   38: invokestatic 890	android/os/Binder:clearCallingIdentity	()J
    //   41: lstore 10
    //   43: aload_0
    //   44: iload 8
    //   46: invokevirtual 2008	com/android/server/MountService:isUserKeyUnlocked	(I)Z
    //   49: istore 14
    //   51: aload_0
    //   52: getfield 459	com/android/server/MountService:mMountServiceInternal	Lcom/android/server/MountService$MountServiceInternalImpl;
    //   55: iload_1
    //   56: aload_2
    //   57: invokevirtual 2012	com/android/server/MountService$MountServiceInternalImpl:hasExternalStorage	(ILjava/lang/String;)Z
    //   60: istore 15
    //   62: lload 10
    //   64: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   67: iconst_0
    //   68: istore 6
    //   70: new 667	java/util/ArrayList
    //   73: dup
    //   74: invokespecial 668	java/util/ArrayList:<init>	()V
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   82: astore 16
    //   84: aload 16
    //   86: monitorenter
    //   87: iconst_0
    //   88: istore_1
    //   89: iload_1
    //   90: aload_0
    //   91: getfield 244	com/android/server/MountService:mVolumes	Landroid/util/ArrayMap;
    //   94: invokevirtual 922	android/util/ArrayMap:size	()I
    //   97: if_icmpge +222 -> 319
    //   100: aload_0
    //   101: getfield 244	com/android/server/MountService:mVolumes	Landroid/util/ArrayMap;
    //   104: iload_1
    //   105: invokevirtual 926	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   108: checkcast 631	android/os/storage/VolumeInfo
    //   111: astore 17
    //   113: aload 17
    //   115: invokevirtual 1056	android/os/storage/VolumeInfo:getType	()I
    //   118: istore 9
    //   120: iload 6
    //   122: istore 7
    //   124: iload 9
    //   126: tableswitch	default:+26->152, 0:+66->192, 1:+30->156, 2:+66->192
    //   152: iload 6
    //   154: istore 7
    //   156: iload_1
    //   157: iconst_1
    //   158: iadd
    //   159: istore_1
    //   160: iload 7
    //   162: istore 6
    //   164: goto -75 -> 89
    //   167: iconst_0
    //   168: istore 4
    //   170: goto -153 -> 17
    //   173: iconst_0
    //   174: istore 5
    //   176: goto -148 -> 28
    //   179: iconst_0
    //   180: istore_3
    //   181: goto -143 -> 38
    //   184: astore_2
    //   185: lload 10
    //   187: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   190: aload_2
    //   191: athrow
    //   192: iload 4
    //   194: ifeq +77 -> 271
    //   197: aload 17
    //   199: iload 8
    //   201: invokevirtual 2015	android/os/storage/VolumeInfo:isVisibleForWrite	(I)Z
    //   204: istore 12
    //   206: iload 6
    //   208: istore 7
    //   210: iload 12
    //   212: ifeq -56 -> 156
    //   215: iconst_0
    //   216: istore 13
    //   218: aload 17
    //   220: invokevirtual 1056	android/os/storage/VolumeInfo:getType	()I
    //   223: iconst_2
    //   224: if_icmpne +208 -> 432
    //   227: iload 14
    //   229: ifeq +236 -> 465
    //   232: goto +200 -> 432
    //   235: aload 17
    //   237: aload_0
    //   238: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   241: iload 8
    //   243: iload 12
    //   245: invokevirtual 1329	android/os/storage/VolumeInfo:buildStorageVolume	(Landroid/content/Context;IZ)Landroid/os/storage/StorageVolume;
    //   248: astore 18
    //   250: aload 17
    //   252: invokevirtual 1590	android/os/storage/VolumeInfo:isPrimary	()Z
    //   255: ifeq +44 -> 299
    //   258: aload_2
    //   259: iconst_0
    //   260: aload 18
    //   262: invokevirtual 2018	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   265: iconst_1
    //   266: istore 7
    //   268: goto -112 -> 156
    //   271: aload 17
    //   273: iload 8
    //   275: invokevirtual 1322	android/os/storage/VolumeInfo:isVisibleForRead	(I)Z
    //   278: ifne +175 -> 453
    //   281: iload_3
    //   282: ifeq +177 -> 459
    //   285: aload 17
    //   287: invokevirtual 721	android/os/storage/VolumeInfo:getPath	()Ljava/io/File;
    //   290: ifnull +169 -> 459
    //   293: iconst_1
    //   294: istore 12
    //   296: goto -90 -> 206
    //   299: aload_2
    //   300: aload 18
    //   302: invokevirtual 2019	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   305: pop
    //   306: iload 6
    //   308: istore 7
    //   310: goto -154 -> 156
    //   313: astore_2
    //   314: aload 16
    //   316: monitorexit
    //   317: aload_2
    //   318: athrow
    //   319: aload 16
    //   321: monitorexit
    //   322: iload 6
    //   324: ifne +87 -> 411
    //   327: ldc -123
    //   329: ldc_w 2021
    //   332: invokestatic 2024	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   335: pop
    //   336: ldc_w 985
    //   339: iconst_0
    //   340: invokestatic 989	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   343: istore 13
    //   345: invokestatic 2027	android/os/Environment:getLegacyExternalStorageDirectory	()Ljava/io/File;
    //   348: astore 16
    //   350: aload_0
    //   351: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   354: ldc_w 2028
    //   357: invokevirtual 2031	android/content/Context:getString	(I)Ljava/lang/String;
    //   360: astore 17
    //   362: iload 13
    //   364: ifeq +62 -> 426
    //   367: iconst_0
    //   368: istore 12
    //   370: aload_2
    //   371: iconst_0
    //   372: new 749	android/os/storage/StorageVolume
    //   375: dup
    //   376: ldc_w 2033
    //   379: iconst_0
    //   380: aload 16
    //   382: aload 17
    //   384: iconst_1
    //   385: iload 13
    //   387: iload 12
    //   389: lconst_0
    //   390: iconst_0
    //   391: lconst_0
    //   392: new 882	android/os/UserHandle
    //   395: dup
    //   396: iload 8
    //   398: invokespecial 2034	android/os/UserHandle:<init>	(I)V
    //   401: aconst_null
    //   402: ldc_w 2036
    //   405: invokespecial 2039	android/os/storage/StorageVolume:<init>	(Ljava/lang/String;ILjava/io/File;Ljava/lang/String;ZZZJZJLandroid/os/UserHandle;Ljava/lang/String;Ljava/lang/String;)V
    //   408: invokevirtual 2018	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   411: aload_2
    //   412: aload_2
    //   413: invokevirtual 2040	java/util/ArrayList:size	()I
    //   416: anewarray 749	android/os/storage/StorageVolume
    //   419: invokevirtual 2044	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   422: checkcast 2046	[Landroid/os/storage/StorageVolume;
    //   425: areturn
    //   426: iconst_1
    //   427: istore 12
    //   429: goto -59 -> 370
    //   432: iload 13
    //   434: istore 12
    //   436: iload 15
    //   438: ifne -203 -> 235
    //   441: iload 5
    //   443: ifeq +28 -> 471
    //   446: iload 13
    //   448: istore 12
    //   450: goto -215 -> 235
    //   453: iconst_1
    //   454: istore 12
    //   456: goto -250 -> 206
    //   459: iconst_0
    //   460: istore 12
    //   462: goto -256 -> 206
    //   465: iconst_1
    //   466: istore 12
    //   468: goto -233 -> 235
    //   471: iconst_1
    //   472: istore 12
    //   474: goto -239 -> 235
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	477	0	this	MountService
    //   0	477	1	paramInt1	int
    //   0	477	2	paramString	String
    //   0	477	3	paramInt2	int
    //   15	178	4	i	int
    //   26	416	5	j	int
    //   68	255	6	k	int
    //   122	187	7	m	int
    //   4	393	8	n	int
    //   118	7	9	i1	int
    //   41	145	10	l	long
    //   204	269	12	bool1	boolean
    //   216	231	13	bool2	boolean
    //   49	179	14	bool3	boolean
    //   60	377	15	bool4	boolean
    //   82	299	16	localObject1	Object
    //   111	272	17	localObject2	Object
    //   248	53	18	localStorageVolume	StorageVolume
    // Exception table:
    //   from	to	target	type
    //   43	62	184	finally
    //   89	120	313	finally
    //   197	206	313	finally
    //   218	227	313	finally
    //   235	265	313	finally
    //   271	281	313	finally
    //   285	293	313	finally
    //   299	306	313	finally
  }
  
  public VolumeRecord[] getVolumeRecords(int paramInt)
  {
    synchronized (this.mLock)
    {
      VolumeRecord[] arrayOfVolumeRecord = new VolumeRecord[this.mRecords.size()];
      paramInt = 0;
      while (paramInt < this.mRecords.size())
      {
        arrayOfVolumeRecord[paramInt] = ((VolumeRecord)this.mRecords.valueAt(paramInt));
        paramInt += 1;
      }
      return arrayOfVolumeRecord;
    }
  }
  
  public String getVolumeState(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public VolumeInfo[] getVolumes(int paramInt)
  {
    synchronized (this.mLock)
    {
      VolumeInfo[] arrayOfVolumeInfo = new VolumeInfo[this.mVolumes.size()];
      paramInt = 0;
      while (paramInt < this.mVolumes.size())
      {
        arrayOfVolumeInfo[paramInt] = ((VolumeInfo)this.mVolumes.valueAt(paramInt));
        paramInt += 1;
      }
      return arrayOfVolumeInfo;
    }
  }
  
  public boolean isConvertibleToFBE()
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "no permission to access the crypt keeper");
    waitForReady();
    try
    {
      int i = Integer.parseInt(this.mCryptConnector.execute("cryptfs", new Object[] { "isConvertibleToFBE" }).getMessage());
      return i != 0;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public boolean isExternalStorageEmulated()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isObbMounted(String paramString)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    synchronized (this.mObbMounts)
    {
      boolean bool = this.mObbPathToStateMap.containsKey(paramString);
      return bool;
    }
  }
  
  public boolean isSecureContainerMounted(String paramString)
  {
    enforcePermission("android.permission.ASEC_ACCESS");
    waitForReady();
    warnOnNotMounted();
    synchronized (this.mAsecMountSet)
    {
      boolean bool = this.mAsecMountSet.contains(paramString);
      return bool;
    }
  }
  
  public boolean isUsbMassStorageConnected()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isUsbMassStorageEnabled()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isUserKeyUnlocked(int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = ArrayUtils.contains(this.mLocalUnlockedUsers, paramInt);
      return bool;
    }
  }
  
  public long lastMaintenance()
  {
    return this.mLastMaintenance;
  }
  
  /* Error */
  public void lockUserKey(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1656
    //   4: invokespecial 1658	com/android/server/MountService:enforcePermission	(Ljava/lang/String;)V
    //   7: aload_0
    //   8: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   11: aload_0
    //   12: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   15: ldc_w 860
    //   18: iconst_2
    //   19: anewarray 409	java/lang/Object
    //   22: dup
    //   23: iconst_0
    //   24: ldc_w 1035
    //   27: aastore
    //   28: dup
    //   29: iconst_1
    //   30: iload_1
    //   31: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   34: aastore
    //   35: invokevirtual 866	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   38: pop
    //   39: aload_0
    //   40: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   43: astore_2
    //   44: aload_2
    //   45: monitorenter
    //   46: aload_0
    //   47: aload_0
    //   48: getfield 419	com/android/server/MountService:mLocalUnlockedUsers	[I
    //   51: iload_1
    //   52: invokestatic 1155	com/android/internal/util/ArrayUtils:removeInt	([II)[I
    //   55: putfield 419	com/android/server/MountService:mLocalUnlockedUsers	[I
    //   58: aload_2
    //   59: monitorexit
    //   60: return
    //   61: astore_2
    //   62: aload_2
    //   63: invokevirtual 1666	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   66: athrow
    //   67: astore_3
    //   68: aload_2
    //   69: monitorexit
    //   70: aload_3
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	MountService
    //   0	72	1	paramInt	int
    //   61	8	2	localNativeDaemonConnectorException	NativeDaemonConnectorException
    //   67	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   11	39	61	com/android/server/NativeDaemonConnectorException
    //   46	58	67	finally
  }
  
  public int mkdirs(String paramString1, String paramString2)
  {
    Environment.UserEnvironment localUserEnvironment = new Environment.UserEnvironment(UserHandle.getUserId(Binder.getCallingUid()));
    ((AppOpsManager)this.mContext.getSystemService("appops")).checkPackage(Binder.getCallingUid(), paramString1);
    try
    {
      File localFile = new File(paramString2).getCanonicalFile();
      if ((FileUtils.contains(localUserEnvironment.buildExternalStorageAppDataDirs(paramString1), localFile)) || (FileUtils.contains(localUserEnvironment.buildExternalStorageAppObbDirs(paramString1), localFile)) || (FileUtils.contains(localUserEnvironment.buildExternalStorageAppMediaDirs(paramString1), localFile)))
      {
        paramString2 = localFile.getAbsolutePath();
        paramString1 = paramString2;
        if (!paramString2.endsWith("/")) {
          paramString1 = paramString2 + "/";
        }
      }
      throw new SecurityException("Invalid mkdirs path: " + localFile);
    }
    catch (IOException paramString1)
    {
      try
      {
        this.mConnector.execute("volume", new Object[] { "mkdirs", paramString1 });
        return 0;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        return paramString1.getCode();
      }
      paramString1 = paramString1;
      Slog.e("MountService", "Failed to resolve " + paramString2 + ": " + paramString1);
      return -1;
    }
  }
  
  public void monitor()
  {
    if (this.mConnector != null) {
      this.mConnector.monitor();
    }
    if (this.mCryptConnector != null) {
      this.mCryptConnector.monitor();
    }
  }
  
  public void mount(String paramString)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    VolumeInfo localVolumeInfo = findVolumeByIdOrThrow(paramString);
    if (isMountDisallowed(localVolumeInfo)) {
      throw new SecurityException("Mounting " + paramString + " restricted by policy");
    }
    try
    {
      this.mConnector.execute("volume", new Object[] { "mount", localVolumeInfo.id, Integer.valueOf(localVolumeInfo.mountFlags), Integer.valueOf(localVolumeInfo.mountUserId) });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public ParcelFileDescriptor mountAppFuse(final String paramString)
    throws RemoteException
  {
    try
    {
      i = Binder.getCallingUid();
      j = Binder.getCallingPid();
      localNativeDaemonEvent = this.mConnector.execute("appfuse", new Object[] { "mount", Integer.valueOf(i), Integer.valueOf(j), paramString });
      if (localNativeDaemonEvent.getFileDescriptors() == null) {
        throw new RemoteException("AppFuse FD from vold is null.");
      }
    }
    catch (NativeDaemonConnectorException paramString)
    {
      final int i;
      final int j;
      NativeDaemonEvent localNativeDaemonEvent;
      throw paramString.rethrowAsParcelableException();
      paramString = ParcelFileDescriptor.fromFd(localNativeDaemonEvent.getFileDescriptors()[0], this.mHandler, new ParcelFileDescriptor.OnCloseListener()
      {
        public void onClose(IOException paramAnonymousIOException)
        {
          try
          {
            MountService.-get1(MountService.this).execute("appfuse", new Object[] { "unmount", Integer.valueOf(i), Integer.valueOf(j), paramString });
            return;
          }
          catch (NativeDaemonConnectorException paramAnonymousIOException)
          {
            Log.e("MountService", "Failed to unmount appfuse.");
          }
        }
      });
      return paramString;
    }
    catch (IOException paramString)
    {
      throw new RemoteException(paramString.getMessage());
    }
  }
  
  public void mountObb(String paramString1, String paramString2, String paramString3, IObbActionListener paramIObbActionListener, int paramInt)
  {
    Preconditions.checkNotNull(paramString1, "rawPath cannot be null");
    Preconditions.checkNotNull(paramString2, "canonicalPath cannot be null");
    Preconditions.checkNotNull(paramIObbActionListener, "token cannot be null");
    int i = Binder.getCallingUid();
    paramString1 = new MountObbAction(new ObbState(paramString1, paramString2, i, paramIObbActionListener, paramInt), paramString3, i);
    this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(1, paramString1));
  }
  
  public int mountSecureContainer(String paramString1, String arg2, int paramInt, boolean paramBoolean)
  {
    enforcePermission("android.permission.ASEC_MOUNT_UNMOUNT");
    waitForReady();
    warnOnNotMounted();
    synchronized (this.mAsecMountSet)
    {
      boolean bool = this.mAsecMountSet.contains(paramString1);
      if (bool) {
        return -6;
      }
      i = 0;
      try
      {
        ??? = this.mConnector;
        localSensitiveArg = new NativeDaemonConnector.SensitiveArg(???);
        if (!paramBoolean) {
          break label148;
        }
        ??? = "ro";
      }
      catch (NativeDaemonConnectorException ???)
      {
        synchronized (this.mAsecMountSet)
        {
          do
          {
            for (;;)
            {
              NativeDaemonConnector.SensitiveArg localSensitiveArg;
              this.mAsecMountSet.add(paramString1);
              return paramInt;
              paramString1 = finally;
              throw paramString1;
              ??? = "rw";
            }
            ??? = ???;
            paramInt = i;
          } while (???.getCode() == 405);
          paramInt = -1;
        }
      }
      ((NativeDaemonConnector)???).execute("asec", new Object[] { "mount", paramString1, localSensitiveArg, Integer.valueOf(paramInt), ??? });
      paramInt = i;
      if (paramInt != 0) {}
    }
  }
  
  public int mountVolume(String paramString)
  {
    mount(findVolumeIdForPathOrThrow(paramString));
    return 0;
  }
  
  public boolean onCheckHoldWakeLock(int paramInt)
  {
    return false;
  }
  
  public void onDaemonConnected()
  {
    this.mDaemonConnected = true;
    this.mHandler.obtainMessage(2).sendToTarget();
  }
  
  public boolean onEvent(int paramInt, String paramString, String[] paramArrayOfString)
  {
    synchronized (this.mLock)
    {
      boolean bool = onEventLocked(paramInt, paramString, paramArrayOfString);
      return bool;
    }
  }
  
  public void partitionMixed(String paramString, int paramInt)
  {
    enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
    enforceAdminUser();
    waitForReady();
    CountDownLatch localCountDownLatch = findOrCreateDiskScanLatch(paramString);
    try
    {
      this.mConnector.execute("volume", new Object[] { "partition", paramString, "mixed", Integer.valueOf(paramInt) });
      waitForLatch(localCountDownLatch, "partitionMixed", 180000L);
      return;
    }
    catch (TimeoutException paramString)
    {
      throw new IllegalStateException(paramString);
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void partitionPrivate(String paramString)
  {
    enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
    enforceAdminUser();
    waitForReady();
    CountDownLatch localCountDownLatch = findOrCreateDiskScanLatch(paramString);
    try
    {
      this.mConnector.execute("volume", new Object[] { "partition", paramString, "private" });
      waitForLatch(localCountDownLatch, "partitionPrivate", 180000L);
      return;
    }
    catch (TimeoutException paramString)
    {
      throw new IllegalStateException(paramString);
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void partitionPublic(String paramString)
  {
    enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
    waitForReady();
    CountDownLatch localCountDownLatch = findOrCreateDiskScanLatch(paramString);
    try
    {
      this.mConnector.execute("volume", new Object[] { "partition", paramString, "public" });
      waitForLatch(localCountDownLatch, "partitionPublic", 180000L);
      return;
    }
    catch (TimeoutException paramString)
    {
      throw new IllegalStateException(paramString);
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void prepareUserStorage(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    enforcePermission("android.permission.STORAGE_INTERNAL");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "prepare_user_storage", escapeNull(paramString), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void registerListener(IMountServiceListener paramIMountServiceListener)
  {
    this.mCallbacks.register(paramIMountServiceListener);
  }
  
  public int renameSecureContainer(String paramString1, String paramString2)
  {
    enforcePermission("android.permission.ASEC_RENAME");
    waitForReady();
    warnOnNotMounted();
    synchronized (this.mAsecMountSet)
    {
      if (!this.mAsecMountSet.contains(paramString1))
      {
        boolean bool = this.mAsecMountSet.contains(paramString2);
        if (!bool) {}
      }
      else
      {
        return -6;
      }
    }
    try
    {
      this.mConnector.execute("asec", new Object[] { "rename", paramString1, paramString2 });
      return 0;
    }
    catch (NativeDaemonConnectorException paramString1) {}
    paramString1 = finally;
    throw paramString1;
    return -1;
  }
  
  public int resizeSecureContainer(String paramString1, int paramInt, String paramString2)
  {
    enforcePermission("android.permission.ASEC_CREATE");
    waitForReady();
    warnOnNotMounted();
    try
    {
      this.mConnector.execute("asec", new Object[] { "resize", paramString1, Integer.valueOf(paramInt), new NativeDaemonConnector.SensitiveArg(paramString2) });
      return 0;
    }
    catch (NativeDaemonConnectorException paramString1) {}
    return -1;
  }
  
  void runIdleMaintenance(Runnable paramRunnable)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, paramRunnable));
  }
  
  public void runMaintenance()
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    runIdleMaintenance(null);
  }
  
  /* Error */
  public void setDebugFlags(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1897
    //   4: invokespecial 1658	com/android/server/MountService:enforcePermission	(Ljava/lang/String;)V
    //   7: aload_0
    //   8: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   11: iload_2
    //   12: iconst_2
    //   13: iand
    //   14: ifeq +87 -> 101
    //   17: invokestatic 1024	android/os/storage/StorageManager:isFileEncryptedNativeOnly	()Z
    //   20: ifeq +14 -> 34
    //   23: new 699	java/lang/IllegalStateException
    //   26: dup
    //   27: ldc_w 2215
    //   30: invokespecial 702	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   33: athrow
    //   34: aload_0
    //   35: getfield 485	com/android/server/MountService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   38: iconst_0
    //   39: invokevirtual 2219	com/android/internal/widget/LockPatternUtils:isCredentialRequiredToDecrypt	(Z)Z
    //   42: ifeq +14 -> 56
    //   45: new 699	java/lang/IllegalStateException
    //   48: dup
    //   49: ldc_w 2221
    //   52: invokespecial 702	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   55: athrow
    //   56: invokestatic 890	android/os/Binder:clearCallingIdentity	()J
    //   59: lstore_3
    //   60: iload_1
    //   61: iconst_2
    //   62: iand
    //   63: ifeq +61 -> 124
    //   66: iconst_1
    //   67: istore 5
    //   69: ldc_w 2223
    //   72: iload 5
    //   74: invokestatic 2226	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   77: invokestatic 816	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   80: aload_0
    //   81: getfield 252	com/android/server/MountService:mContext	Landroid/content/Context;
    //   84: ldc_w 2228
    //   87: invokevirtual 941	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   90: checkcast 2228	android/os/PowerManager
    //   93: aconst_null
    //   94: invokevirtual 2231	android/os/PowerManager:reboot	(Ljava/lang/String;)V
    //   97: lload_3
    //   98: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   101: iload_2
    //   102: iconst_1
    //   103: iand
    //   104: ifeq +78 -> 182
    //   107: invokestatic 1024	android/os/storage/StorageManager:isFileEncryptedNativeOnly	()Z
    //   110: ifeq +29 -> 139
    //   113: new 699	java/lang/IllegalStateException
    //   116: dup
    //   117: ldc_w 2233
    //   120: invokespecial 702	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   123: athrow
    //   124: iconst_0
    //   125: istore 5
    //   127: goto -58 -> 69
    //   130: astore 6
    //   132: lload_3
    //   133: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   136: aload 6
    //   138: athrow
    //   139: aload_0
    //   140: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   143: astore 6
    //   145: aload 6
    //   147: monitorenter
    //   148: iload_1
    //   149: iconst_1
    //   150: iand
    //   151: ifeq +78 -> 229
    //   154: iconst_1
    //   155: istore 5
    //   157: aload_0
    //   158: iload 5
    //   160: putfield 1209	com/android/server/MountService:mForceAdoptable	Z
    //   163: aload_0
    //   164: invokespecial 1285	com/android/server/MountService:writeSettingsLocked	()V
    //   167: aload_0
    //   168: getfield 263	com/android/server/MountService:mHandler	Landroid/os/Handler;
    //   171: bipush 10
    //   173: invokevirtual 1550	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
    //   176: invokevirtual 1190	android/os/Message:sendToTarget	()V
    //   179: aload 6
    //   181: monitorexit
    //   182: iload_2
    //   183: bipush 12
    //   185: iand
    //   186: ifeq +42 -> 228
    //   189: iload_1
    //   190: iconst_4
    //   191: iand
    //   192: ifeq +51 -> 243
    //   195: ldc_w 2235
    //   198: astore 6
    //   200: invokestatic 890	android/os/Binder:clearCallingIdentity	()J
    //   203: lstore_3
    //   204: ldc_w 2237
    //   207: aload 6
    //   209: invokestatic 816	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   212: aload_0
    //   213: getfield 263	com/android/server/MountService:mHandler	Landroid/os/Handler;
    //   216: bipush 10
    //   218: invokevirtual 1550	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
    //   221: invokevirtual 1190	android/os/Message:sendToTarget	()V
    //   224: lload_3
    //   225: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   228: return
    //   229: iconst_0
    //   230: istore 5
    //   232: goto -75 -> 157
    //   235: astore 7
    //   237: aload 6
    //   239: monitorexit
    //   240: aload 7
    //   242: athrow
    //   243: iload_1
    //   244: bipush 8
    //   246: iand
    //   247: ifeq +11 -> 258
    //   250: ldc_w 2239
    //   253: astore 6
    //   255: goto -55 -> 200
    //   258: ldc_w 1000
    //   261: astore 6
    //   263: goto -63 -> 200
    //   266: astore 6
    //   268: lload_3
    //   269: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   272: aload 6
    //   274: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	275	0	this	MountService
    //   0	275	1	paramInt1	int
    //   0	275	2	paramInt2	int
    //   59	210	3	l	long
    //   67	164	5	bool	boolean
    //   130	7	6	localObject1	Object
    //   143	119	6	localObject2	Object
    //   266	7	6	localObject3	Object
    //   235	6	7	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   69	97	130	finally
    //   157	179	235	finally
    //   204	224	266	finally
  }
  
  public void setField(String paramString1, String paramString2)
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STORAGE_INTERNAL", "no permission to access the crypt keeper");
    waitForReady();
    try
    {
      this.mCryptConnector.execute("cryptfs", new Object[] { "setfield", paramString1, paramString2 });
      return;
    }
    catch (NativeDaemonConnectorException paramString1)
    {
      throw paramString1.rethrowAsParcelableException();
    }
  }
  
  public void setPrimaryStorageUuid(String paramString, IPackageMoveObserver paramIPackageMoveObserver)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    synchronized (this.mLock)
    {
      if (Objects.equals(this.mPrimaryStorageUuid, paramString)) {
        throw new IllegalArgumentException("Primary storage already at " + paramString);
      }
    }
    if (this.mMoveCallback != null) {
      throw new IllegalStateException("Move already in progress");
    }
    this.mMoveCallback = paramIPackageMoveObserver;
    this.mMoveTargetUuid = paramString;
    if ((Objects.equals("primary_physical", this.mPrimaryStorageUuid)) || (Objects.equals("primary_physical", paramString)))
    {
      Slog.d("MountService", "Skipping move to/from primary physical");
      onMoveStatusLocked(82);
      onMoveStatusLocked(-100);
      this.mHandler.obtainMessage(10).sendToTarget();
      return;
    }
    paramIPackageMoveObserver = findStorageForUuid(this.mPrimaryStorageUuid);
    VolumeInfo localVolumeInfo = findStorageForUuid(paramString);
    if (paramIPackageMoveObserver == null)
    {
      Slog.w("MountService", "Failing move due to missing from volume " + this.mPrimaryStorageUuid);
      onMoveStatusLocked(-6);
      return;
    }
    if (localVolumeInfo == null)
    {
      Slog.w("MountService", "Failing move due to missing to volume " + paramString);
      onMoveStatusLocked(-6);
      return;
    }
    try
    {
      this.mConnector.execute("volume", new Object[] { "move_storage", paramIPackageMoveObserver.id, localVolumeInfo.id });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void setUsbMassStorageEnabled(boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public void setVolumeNickname(String paramString1, String paramString2)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    Preconditions.checkNotNull(paramString1);
    synchronized (this.mLock)
    {
      paramString1 = (VolumeRecord)this.mRecords.get(paramString1);
      paramString1.nickname = paramString2;
      Callbacks.-wrap4(this.mCallbacks, paramString1);
      writeSettingsLocked();
      return;
    }
  }
  
  public void setVolumeUserFlags(String paramString, int paramInt1, int paramInt2)
  {
    enforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
    waitForReady();
    Preconditions.checkNotNull(paramString);
    synchronized (this.mLock)
    {
      paramString = (VolumeRecord)this.mRecords.get(paramString);
      paramString.userFlags = (paramString.userFlags & paramInt2 | paramInt1 & paramInt2);
      Callbacks.-wrap4(this.mCallbacks, paramString);
      writeSettingsLocked();
      return;
    }
  }
  
  public void shutdown(IMountShutdownObserver paramIMountShutdownObserver)
  {
    enforcePermission("android.permission.SHUTDOWN");
    Slog.i("MountService", "Shutting down");
    this.mHandler.obtainMessage(3, paramIMountShutdownObserver).sendToTarget();
  }
  
  /* Error */
  public void unlockUserKey(int paramInt1, int paramInt2, byte[] arg3, byte[] paramArrayOfByte2)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1656
    //   4: invokespecial 1658	com/android/server/MountService:enforcePermission	(Ljava/lang/String;)V
    //   7: aload_0
    //   8: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   11: invokestatic 2274	android/os/storage/StorageManager:isFileEncryptedNativeOrEmulated	()Z
    //   14: ifeq +101 -> 115
    //   17: aload_0
    //   18: getfield 485	com/android/server/MountService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   21: iload_1
    //   22: invokevirtual 2277	com/android/internal/widget/LockPatternUtils:isSecure	(I)Z
    //   25: ifeq +38 -> 63
    //   28: aload_3
    //   29: invokestatic 828	com/android/internal/util/ArrayUtils:isEmpty	([B)Z
    //   32: ifeq +31 -> 63
    //   35: new 699	java/lang/IllegalStateException
    //   38: dup
    //   39: new 605	java/lang/StringBuilder
    //   42: dup
    //   43: invokespecial 606	java/lang/StringBuilder:<init>	()V
    //   46: ldc_w 2279
    //   49: invokevirtual 612	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: iload_1
    //   53: invokevirtual 1108	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   56: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   59: invokespecial 702	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   62: athrow
    //   63: aload_0
    //   64: getfield 255	com/android/server/MountService:mCryptConnector	Lcom/android/server/NativeDaemonConnector;
    //   67: ldc_w 860
    //   70: iconst_5
    //   71: anewarray 409	java/lang/Object
    //   74: dup
    //   75: iconst_0
    //   76: ldc_w 1049
    //   79: aastore
    //   80: dup
    //   81: iconst_1
    //   82: iload_1
    //   83: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   86: aastore
    //   87: dup
    //   88: iconst_2
    //   89: iload_2
    //   90: invokestatic 1043	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   93: aastore
    //   94: dup
    //   95: iconst_3
    //   96: aload_0
    //   97: aload_3
    //   98: invokespecial 1662	com/android/server/MountService:encodeBytes	([B)Lcom/android/server/NativeDaemonConnector$SensitiveArg;
    //   101: aastore
    //   102: dup
    //   103: iconst_4
    //   104: aload_0
    //   105: aload 4
    //   107: invokespecial 1662	com/android/server/MountService:encodeBytes	([B)Lcom/android/server/NativeDaemonConnector$SensitiveArg;
    //   110: aastore
    //   111: invokevirtual 866	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   114: pop
    //   115: aload_0
    //   116: getfield 412	com/android/server/MountService:mLock	Ljava/lang/Object;
    //   119: astore_3
    //   120: aload_3
    //   121: monitorenter
    //   122: aload_0
    //   123: aload_0
    //   124: getfield 419	com/android/server/MountService:mLocalUnlockedUsers	[I
    //   127: iload_1
    //   128: invokestatic 1344	com/android/internal/util/ArrayUtils:appendInt	([II)[I
    //   131: putfield 419	com/android/server/MountService:mLocalUnlockedUsers	[I
    //   134: aload_3
    //   135: monitorexit
    //   136: return
    //   137: astore_3
    //   138: aload_3
    //   139: invokevirtual 1666	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   142: athrow
    //   143: astore 4
    //   145: aload_3
    //   146: monitorexit
    //   147: aload 4
    //   149: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	MountService
    //   0	150	1	paramInt1	int
    //   0	150	2	paramInt2	int
    //   0	150	4	paramArrayOfByte2	byte[]
    // Exception table:
    //   from	to	target	type
    //   63	115	137	com/android/server/NativeDaemonConnectorException
    //   122	134	143	finally
  }
  
  /* Error */
  public void unmount(String arg1)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1897
    //   4: invokespecial 1658	com/android/server/MountService:enforcePermission	(Ljava/lang/String;)V
    //   7: aload_0
    //   8: invokespecial 330	com/android/server/MountService:waitForReady	()V
    //   11: aload_0
    //   12: aload_1
    //   13: invokespecial 1912	com/android/server/MountService:findVolumeByIdOrThrow	(Ljava/lang/String;)Landroid/os/storage/VolumeInfo;
    //   16: astore 4
    //   18: aload 4
    //   20: invokevirtual 2283	android/os/storage/VolumeInfo:isPrimaryPhysical	()Z
    //   23: ifeq +57 -> 80
    //   26: invokestatic 890	android/os/Binder:clearCallingIdentity	()J
    //   29: lstore_2
    //   30: aload_0
    //   31: getfield 446	com/android/server/MountService:mUnmountLock	Ljava/lang/Object;
    //   34: astore_1
    //   35: aload_1
    //   36: monitorenter
    //   37: aload_0
    //   38: new 438	java/util/concurrent/CountDownLatch
    //   41: dup
    //   42: iconst_1
    //   43: invokespecial 440	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   46: putfield 1885	com/android/server/MountService:mUnmountSignal	Ljava/util/concurrent/CountDownLatch;
    //   49: aload_0
    //   50: getfield 497	com/android/server/MountService:mPms	Lcom/android/server/pm/PackageManagerService;
    //   53: iconst_0
    //   54: iconst_1
    //   55: invokevirtual 2287	com/android/server/pm/PackageManagerService:updateExternalMediaStatus	(ZZ)V
    //   58: aload_0
    //   59: aload_0
    //   60: getfield 1885	com/android/server/MountService:mUnmountSignal	Ljava/util/concurrent/CountDownLatch;
    //   63: ldc_w 2288
    //   66: invokespecial 1587	com/android/server/MountService:waitForLatch	(Ljava/util/concurrent/CountDownLatch;Ljava/lang/String;)V
    //   69: aload_0
    //   70: aconst_null
    //   71: putfield 1885	com/android/server/MountService:mUnmountSignal	Ljava/util/concurrent/CountDownLatch;
    //   74: aload_1
    //   75: monitorexit
    //   76: lload_2
    //   77: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   80: aload_0
    //   81: getfield 233	com/android/server/MountService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   84: ldc -114
    //   86: iconst_2
    //   87: anewarray 409	java/lang/Object
    //   90: dup
    //   91: iconst_0
    //   92: ldc_w 2289
    //   95: aastore
    //   96: dup
    //   97: iconst_1
    //   98: aload 4
    //   100: getfield 648	android/os/storage/VolumeInfo:id	Ljava/lang/String;
    //   103: aastore
    //   104: invokevirtual 866	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   107: pop
    //   108: return
    //   109: astore 4
    //   111: aload_1
    //   112: monitorexit
    //   113: aload 4
    //   115: athrow
    //   116: astore_1
    //   117: lload_2
    //   118: invokestatic 903	android/os/Binder:restoreCallingIdentity	(J)V
    //   121: aload_1
    //   122: athrow
    //   123: astore_1
    //   124: aload_1
    //   125: invokevirtual 1666	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   128: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	this	MountService
    //   29	89	2	l	long
    //   16	83	4	localVolumeInfo	VolumeInfo
    //   109	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   37	74	109	finally
    //   30	37	116	finally
    //   74	76	116	finally
    //   111	116	116	finally
    //   80	108	123	com/android/server/NativeDaemonConnectorException
  }
  
  public void unmountObb(String paramString, boolean paramBoolean, IObbActionListener paramIObbActionListener, int paramInt)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    synchronized (this.mObbMounts)
    {
      ObbState localObbState = (ObbState)this.mObbPathToStateMap.get(paramString);
      if (localObbState != null)
      {
        int i = Binder.getCallingUid();
        paramString = new UnmountObbAction(new ObbState(paramString, localObbState.canonicalPath, i, paramIObbActionListener, paramInt), paramBoolean);
        this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(1, paramString));
        return;
      }
    }
    Slog.w("MountService", "Unknown OBB mount at " + paramString);
  }
  
  public int unmountSecureContainer(String paramString, boolean paramBoolean)
  {
    enforcePermission("android.permission.ASEC_MOUNT_UNMOUNT");
    waitForReady();
    warnOnNotMounted();
    synchronized (this.mAsecMountSet)
    {
      boolean bool = this.mAsecMountSet.contains(paramString);
      if (!bool) {
        return -5;
      }
      Runtime.getRuntime().gc();
      i = 0;
      try
      {
        ??? = new NativeDaemonConnector.Command("asec", new Object[] { "unmount", paramString });
        if (paramBoolean) {
          ((NativeDaemonConnector.Command)???).appendArg("force");
        }
        this.mConnector.execute((NativeDaemonConnector.Command)???);
      }
      catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
      {
        synchronized (this.mAsecMountSet)
        {
          for (;;)
          {
            this.mAsecMountSet.remove(paramString);
            return i;
            paramString = finally;
            throw paramString;
            localNativeDaemonConnectorException = localNativeDaemonConnectorException;
            if (localNativeDaemonConnectorException.getCode() != 405) {
              break;
            }
            i = -7;
          }
          i = -1;
        }
      }
      if (i != 0) {}
    }
  }
  
  public void unmountVolume(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    unmount(findVolumeIdForPathOrThrow(paramString));
  }
  
  public void unregisterListener(IMountServiceListener paramIMountServiceListener)
  {
    this.mCallbacks.unregister(paramIMountServiceListener);
  }
  
  public int verifyEncryptionPassword(String paramString)
    throws RemoteException
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("no permission to access the crypt keeper");
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to access the crypt keeper");
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("password cannot be empty");
    }
    waitForReady();
    try
    {
      paramString = this.mCryptConnector.execute("cryptfs", new Object[] { "verifypw", new NativeDaemonConnector.SensitiveArg(paramString) });
      Slog.i("MountService", "cryptfs verifypw => " + paramString.getMessage());
      int i = Integer.parseInt(paramString.getMessage());
      return i;
    }
    catch (NativeDaemonConnectorException paramString) {}
    return paramString.getCode();
  }
  
  public void waitForAsecScan()
  {
    waitForLatch(this.mAsecsScanned, "mAsecsScanned");
  }
  
  private static class Callbacks
    extends Handler
  {
    private static final int MSG_DISK_DESTROYED = 6;
    private static final int MSG_DISK_SCANNED = 5;
    private static final int MSG_STORAGE_STATE_CHANGED = 1;
    private static final int MSG_VOLUME_FORGOTTEN = 4;
    private static final int MSG_VOLUME_RECORD_CHANGED = 3;
    private static final int MSG_VOLUME_STATE_CHANGED = 2;
    private final RemoteCallbackList<IMountServiceListener> mCallbacks = new RemoteCallbackList();
    
    public Callbacks(Looper paramLooper)
    {
      super();
    }
    
    private void invokeCallback(IMountServiceListener paramIMountServiceListener, int paramInt, SomeArgs paramSomeArgs)
      throws RemoteException
    {
      switch (paramInt)
      {
      default: 
        return;
      case 1: 
        paramIMountServiceListener.onStorageStateChanged((String)paramSomeArgs.arg1, (String)paramSomeArgs.arg2, (String)paramSomeArgs.arg3);
        return;
      case 2: 
        paramIMountServiceListener.onVolumeStateChanged((VolumeInfo)paramSomeArgs.arg1, paramSomeArgs.argi2, paramSomeArgs.argi3);
        return;
      case 3: 
        paramIMountServiceListener.onVolumeRecordChanged((VolumeRecord)paramSomeArgs.arg1);
        return;
      case 4: 
        paramIMountServiceListener.onVolumeForgotten((String)paramSomeArgs.arg1);
        return;
      case 5: 
        paramIMountServiceListener.onDiskScanned((DiskInfo)paramSomeArgs.arg1, paramSomeArgs.argi2);
        return;
      }
      paramIMountServiceListener.onDiskDestroyed((DiskInfo)paramSomeArgs.arg1);
    }
    
    private void notifyDiskDestroyed(DiskInfo paramDiskInfo)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramDiskInfo.clone();
      obtainMessage(6, localSomeArgs).sendToTarget();
    }
    
    private void notifyDiskScanned(DiskInfo paramDiskInfo, int paramInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramDiskInfo.clone();
      localSomeArgs.argi2 = paramInt;
      obtainMessage(5, localSomeArgs).sendToTarget();
    }
    
    private void notifyStorageStateChanged(String paramString1, String paramString2, String paramString3)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString1;
      localSomeArgs.arg2 = paramString2;
      localSomeArgs.arg3 = paramString3;
      obtainMessage(1, localSomeArgs).sendToTarget();
    }
    
    private void notifyVolumeForgotten(String paramString)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      obtainMessage(4, localSomeArgs).sendToTarget();
    }
    
    private void notifyVolumeRecordChanged(VolumeRecord paramVolumeRecord)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramVolumeRecord.clone();
      obtainMessage(3, localSomeArgs).sendToTarget();
    }
    
    private void notifyVolumeStateChanged(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramVolumeInfo.clone();
      localSomeArgs.argi2 = paramInt1;
      localSomeArgs.argi3 = paramInt2;
      obtainMessage(2, localSomeArgs).sendToTarget();
    }
    
    public void handleMessage(Message paramMessage)
    {
      SomeArgs localSomeArgs = (SomeArgs)paramMessage.obj;
      int j = this.mCallbacks.beginBroadcast();
      int i = 0;
      for (;;)
      {
        IMountServiceListener localIMountServiceListener;
        if (i < j) {
          localIMountServiceListener = (IMountServiceListener)this.mCallbacks.getBroadcastItem(i);
        }
        try
        {
          invokeCallback(localIMountServiceListener, paramMessage.what, localSomeArgs);
          i += 1;
          continue;
          this.mCallbacks.finishBroadcast();
          localSomeArgs.recycle();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    public void register(IMountServiceListener paramIMountServiceListener)
    {
      this.mCallbacks.register(paramIMountServiceListener);
    }
    
    public void unregister(IMountServiceListener paramIMountServiceListener)
    {
      this.mCallbacks.unregister(paramIMountServiceListener);
    }
  }
  
  class DefaultContainerConnection
    implements ServiceConnection
  {
    DefaultContainerConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      paramComponentName = IMediaContainerService.Stub.asInterface(paramIBinder);
      MountService.-get9(MountService.this).sendMessage(MountService.-get9(MountService.this).obtainMessage(2, paramComponentName));
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName) {}
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private MountService mMountService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        MountService.-wrap15(this.mMountService);
      }
      while (paramInt != 1000) {
        return;
      }
      MountService.-wrap5(this.mMountService);
    }
    
    public void onCleanupUser(int paramInt)
    {
      MountService.-wrap9(this.mMountService, paramInt);
    }
    
    public void onStart()
    {
      this.mMountService = new MountService(getContext());
      publishBinderService("mount", this.mMountService);
      MountService.-wrap14(this.mMountService);
    }
    
    public void onSwitchUser(int paramInt)
    {
      MountService.-set1(this.mMountService, paramInt);
    }
    
    public void onUnlockUser(int paramInt)
    {
      MountService.-wrap10(this.mMountService, paramInt);
    }
  }
  
  class MountObbAction
    extends MountService.ObbAction
  {
    private final int mCallingUid;
    private final String mKey;
    
    MountObbAction(MountService.ObbState paramObbState, String paramString, int paramInt)
    {
      super(paramObbState);
      this.mKey = paramString;
      this.mCallingUid = paramInt;
    }
    
    public void handleError()
    {
      sendNewStatusOrIgnore(20);
    }
    
    /* Error */
    public void handleExecute()
      throws IOException, RemoteException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   4: invokestatic 46	com/android/server/MountService:-wrap16	(Lcom/android/server/MountService;)V
      //   7: aload_0
      //   8: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   11: invokestatic 49	com/android/server/MountService:-wrap17	(Lcom/android/server/MountService;)V
      //   14: aload_0
      //   15: invokevirtual 53	com/android/server/MountService$MountObbAction:getObbInfo	()Landroid/content/res/ObbInfo;
      //   18: astore 4
      //   20: aload_0
      //   21: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   24: aload 4
      //   26: getfield 58	android/content/res/ObbInfo:packageName	Ljava/lang/String;
      //   29: aload_0
      //   30: getfield 24	com/android/server/MountService$MountObbAction:mCallingUid	I
      //   33: invokestatic 62	com/android/server/MountService:-wrap2	(Lcom/android/server/MountService;Ljava/lang/String;I)Z
      //   36: ifne +52 -> 88
      //   39: ldc 64
      //   41: new 66	java/lang/StringBuilder
      //   44: dup
      //   45: invokespecial 68	java/lang/StringBuilder:<init>	()V
      //   48: ldc 70
      //   50: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   53: aload 4
      //   55: getfield 77	android/content/res/ObbInfo:filename	Ljava/lang/String;
      //   58: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   61: ldc 79
      //   63: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   66: aload 4
      //   68: getfield 58	android/content/res/ObbInfo:packageName	Ljava/lang/String;
      //   71: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   74: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   77: invokestatic 89	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   80: pop
      //   81: aload_0
      //   82: bipush 25
      //   84: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   87: return
      //   88: aload_0
      //   89: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   92: invokestatic 93	com/android/server/MountService:-get10	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   95: astore_3
      //   96: aload_3
      //   97: monitorenter
      //   98: aload_0
      //   99: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   102: invokestatic 96	com/android/server/MountService:-get11	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   105: aload_0
      //   106: getfield 100	com/android/server/MountService$MountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   109: getfield 105	com/android/server/MountService$ObbState:rawPath	Ljava/lang/String;
      //   112: invokeinterface 111 2 0
      //   117: istore_2
      //   118: aload_3
      //   119: monitorexit
      //   120: iload_2
      //   121: ifeq +46 -> 167
      //   124: ldc 64
      //   126: new 66	java/lang/StringBuilder
      //   129: dup
      //   130: invokespecial 68	java/lang/StringBuilder:<init>	()V
      //   133: ldc 113
      //   135: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   138: aload 4
      //   140: getfield 77	android/content/res/ObbInfo:filename	Ljava/lang/String;
      //   143: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   146: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   149: invokestatic 89	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   152: pop
      //   153: aload_0
      //   154: bipush 24
      //   156: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   159: return
      //   160: astore 4
      //   162: aload_3
      //   163: monitorexit
      //   164: aload 4
      //   166: athrow
      //   167: aload_0
      //   168: getfield 22	com/android/server/MountService$MountObbAction:mKey	Ljava/lang/String;
      //   171: ifnonnull +97 -> 268
      //   174: ldc 115
      //   176: astore_3
      //   177: iconst_0
      //   178: istore_1
      //   179: aload_0
      //   180: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   183: invokestatic 119	com/android/server/MountService:-get1	(Lcom/android/server/MountService;)Lcom/android/server/NativeDaemonConnector;
      //   186: ldc 121
      //   188: iconst_4
      //   189: anewarray 123	java/lang/Object
      //   192: dup
      //   193: iconst_0
      //   194: ldc 125
      //   196: aastore
      //   197: dup
      //   198: iconst_1
      //   199: aload_0
      //   200: getfield 100	com/android/server/MountService$MountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   203: getfield 128	com/android/server/MountService$ObbState:canonicalPath	Ljava/lang/String;
      //   206: aastore
      //   207: dup
      //   208: iconst_2
      //   209: new 130	com/android/server/NativeDaemonConnector$SensitiveArg
      //   212: dup
      //   213: aload_3
      //   214: invokespecial 133	com/android/server/NativeDaemonConnector$SensitiveArg:<init>	(Ljava/lang/Object;)V
      //   217: aastore
      //   218: dup
      //   219: iconst_3
      //   220: aload_0
      //   221: getfield 100	com/android/server/MountService$MountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   224: getfield 136	com/android/server/MountService$ObbState:ownerGid	I
      //   227: invokestatic 142	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   230: aastore
      //   231: invokevirtual 148	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
      //   234: pop
      //   235: iload_1
      //   236: ifne +143 -> 379
      //   239: aload_0
      //   240: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   243: invokestatic 93	com/android/server/MountService:-get10	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   246: astore_3
      //   247: aload_3
      //   248: monitorenter
      //   249: aload_0
      //   250: getfield 17	com/android/server/MountService$MountObbAction:this$0	Lcom/android/server/MountService;
      //   253: aload_0
      //   254: getfield 100	com/android/server/MountService$MountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   257: invokestatic 151	com/android/server/MountService:-wrap4	(Lcom/android/server/MountService;Lcom/android/server/MountService$ObbState;)V
      //   260: aload_3
      //   261: monitorexit
      //   262: aload_0
      //   263: iconst_1
      //   264: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   267: return
      //   268: new 153	java/math/BigInteger
      //   271: dup
      //   272: ldc -101
      //   274: invokestatic 161	javax/crypto/SecretKeyFactory:getInstance	(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;
      //   277: new 163	javax/crypto/spec/PBEKeySpec
      //   280: dup
      //   281: aload_0
      //   282: getfield 22	com/android/server/MountService$MountObbAction:mKey	Ljava/lang/String;
      //   285: invokevirtual 169	java/lang/String:toCharArray	()[C
      //   288: aload 4
      //   290: getfield 173	android/content/res/ObbInfo:salt	[B
      //   293: sipush 1024
      //   296: sipush 128
      //   299: invokespecial 176	javax/crypto/spec/PBEKeySpec:<init>	([C[BII)V
      //   302: invokevirtual 180	javax/crypto/SecretKeyFactory:generateSecret	(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;
      //   305: invokeinterface 186 1 0
      //   310: invokespecial 189	java/math/BigInteger:<init>	([B)V
      //   313: bipush 16
      //   315: invokevirtual 192	java/math/BigInteger:toString	(I)Ljava/lang/String;
      //   318: astore_3
      //   319: goto -142 -> 177
      //   322: astore_3
      //   323: ldc 64
      //   325: ldc -62
      //   327: aload_3
      //   328: invokestatic 198	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   331: pop
      //   332: aload_0
      //   333: bipush 20
      //   335: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   338: return
      //   339: astore_3
      //   340: ldc 64
      //   342: ldc -56
      //   344: aload_3
      //   345: invokestatic 198	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   348: pop
      //   349: aload_0
      //   350: bipush 20
      //   352: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   355: return
      //   356: astore_3
      //   357: aload_3
      //   358: invokevirtual 204	com/android/server/NativeDaemonConnectorException:getCode	()I
      //   361: sipush 405
      //   364: if_icmpeq -129 -> 235
      //   367: iconst_m1
      //   368: istore_1
      //   369: goto -134 -> 235
      //   372: astore 4
      //   374: aload_3
      //   375: monitorexit
      //   376: aload 4
      //   378: athrow
      //   379: ldc 64
      //   381: new 66	java/lang/StringBuilder
      //   384: dup
      //   385: invokespecial 68	java/lang/StringBuilder:<init>	()V
      //   388: ldc -50
      //   390: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   393: iload_1
      //   394: invokevirtual 209	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   397: invokevirtual 83	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   400: invokestatic 211	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   403: pop
      //   404: aload_0
      //   405: bipush 21
      //   407: invokevirtual 31	com/android/server/MountService$MountObbAction:sendNewStatusOrIgnore	(I)V
      //   410: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	411	0	this	MountObbAction
      //   178	216	1	i	int
      //   117	4	2	bool	boolean
      //   322	6	3	localInvalidKeySpecException	java.security.spec.InvalidKeySpecException
      //   339	6	3	localNoSuchAlgorithmException	java.security.NoSuchAlgorithmException
      //   356	19	3	localNativeDaemonConnectorException	NativeDaemonConnectorException
      //   18	121	4	localObbInfo	ObbInfo
      //   160	129	4	localObject2	Object
      //   372	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   98	118	160	finally
      //   268	319	322	java/security/spec/InvalidKeySpecException
      //   268	319	339	java/security/NoSuchAlgorithmException
      //   179	235	356	com/android/server/NativeDaemonConnectorException
      //   249	260	372	finally
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("MountObbAction{");
      localStringBuilder.append(this.mObbState);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  class MountServiceHandler
    extends Handler
  {
    public MountServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
      case 1: 
      case 2: 
      case 4: 
      case 3: 
      case 5: 
      case 8: 
      case 6: 
        do
        {
          do
          {
            for (;;)
            {
              return;
              MountService.-wrap8(MountService.this);
              return;
              MountService.-wrap7(MountService.this);
              return;
              if (!MountService.-wrap1(MountService.this))
              {
                Slog.i("MountService", "fstrim requested, but no daemon connection yet; trying again");
                sendMessageDelayed(obtainMessage(4, paramMessage.obj), 1000L);
                return;
              }
              Slog.i("MountService", "Running fstrim idle maintenance");
              try
              {
                MountService.-set2(MountService.this, System.currentTimeMillis());
                MountService.-get8(MountService.this).setLastModified(MountService.-get7(MountService.this));
                bool1 = MountService.-wrap3(MountService.this);
                try
                {
                  NativeDaemonConnector localNativeDaemonConnector = MountService.-get1(MountService.this);
                  if (!bool1) {
                    break label224;
                  }
                  String str1 = "dotrimbench";
                  localNativeDaemonConnector.execute("fstrim", new Object[] { str1 });
                }
                catch (NativeDaemonConnectorException localNativeDaemonConnectorException1)
                {
                  for (;;)
                  {
                    String str2;
                    Slog.e("MountService", "Failed to run fstrim!");
                  }
                }
                paramMessage = (Runnable)paramMessage.obj;
                if (paramMessage != null)
                {
                  paramMessage.run();
                  return;
                }
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  Slog.e("MountService", "Unable to record last fstrim!");
                  continue;
                  str2 = "dotrim";
                }
              }
            }
            paramMessage = (IMountShutdownObserver)paramMessage.obj;
            boolean bool1 = false;
            try
            {
              boolean bool2 = MountService.-get1(MountService.this).execute("volume", new Object[] { "shutdown" }).isClassOk();
              bool1 = bool2;
            }
            catch (NativeDaemonConnectorException localNativeDaemonConnectorException2)
            {
              int i;
              Object localObject;
              for (;;) {}
            }
          } while (paramMessage == null);
          if (bool1) {}
          for (i = 0;; i = -1) {
            try
            {
              paramMessage.onShutDownComplete(i);
              return;
            }
            catch (RemoteException paramMessage)
            {
              return;
            }
          }
          paramMessage = (VolumeInfo)paramMessage.obj;
          if (MountService.-wrap0(MountService.this, paramMessage))
          {
            Slog.i("MountService", "Ignoring mount " + paramMessage.getId() + " due to policy");
            return;
          }
          try
          {
            MountService.-get1(MountService.this).execute("volume", new Object[] { "mount", paramMessage.id, Integer.valueOf(paramMessage.mountFlags), Integer.valueOf(paramMessage.mountUserId) });
            return;
          }
          catch (NativeDaemonConnectorException paramMessage)
          {
            return;
          }
          paramMessage = (VolumeInfo)paramMessage.obj;
          MountService.this.unmount(paramMessage.getId());
          return;
          paramMessage = (StorageVolume)paramMessage.obj;
          localObject = paramMessage.getState();
          Slog.d("MountService", "Volume " + paramMessage.getId() + " broadcasting " + (String)localObject + " to " + paramMessage.getOwner());
          localObject = VolumeInfo.getBroadcastForEnvironment((String)localObject);
        } while (localObject == null);
        if (MountService.-get0()) {
          Slog.d("MountService", "sendBroadcastAsUser: action = " + (String)localObject);
        }
        localObject = new Intent((String)localObject, Uri.fromFile(paramMessage.getPathFile()));
        ((Intent)localObject).putExtra("android.os.storage.extra.STORAGE_VOLUME", paramMessage);
        ((Intent)localObject).addFlags(67108864);
        MountService.-get3(MountService.this).sendBroadcastAsUser((Intent)localObject, paramMessage.getOwner());
        return;
      case 7: 
        paramMessage = (Intent)paramMessage.obj;
        MountService.-get3(MountService.this).sendBroadcastAsUser(paramMessage, UserHandle.ALL, "android.permission.WRITE_MEDIA_STORAGE");
        return;
      case 9: 
        paramMessage = (String)paramMessage.obj;
        MountService.-wrap6(MountService.this, paramMessage);
        return;
      case 10: 
        label224:
        MountService.-wrap13(MountService.this);
        return;
      }
    }
  }
  
  private final class MountServiceInternalImpl
    extends MountServiceInternal
  {
    private final CopyOnWriteArrayList<MountServiceInternal.ExternalStorageMountPolicy> mPolicies = new CopyOnWriteArrayList();
    
    private MountServiceInternalImpl() {}
    
    public void addExternalStoragePolicy(MountServiceInternal.ExternalStorageMountPolicy paramExternalStorageMountPolicy)
    {
      this.mPolicies.add(paramExternalStorageMountPolicy);
    }
    
    public int getExternalStorageMountMode(int paramInt, String paramString)
    {
      int i = Integer.MAX_VALUE;
      Iterator localIterator = this.mPolicies.iterator();
      while (localIterator.hasNext())
      {
        int j = ((MountServiceInternal.ExternalStorageMountPolicy)localIterator.next()).getMountMode(paramInt, paramString);
        if (j == 0) {
          return 0;
        }
        i = Math.min(i, j);
      }
      if (i == Integer.MAX_VALUE) {
        return 0;
      }
      return i;
    }
    
    public boolean hasExternalStorage(int paramInt, String paramString)
    {
      if (paramInt == 1000) {
        return true;
      }
      Iterator localIterator = this.mPolicies.iterator();
      while (localIterator.hasNext()) {
        if (!((MountServiceInternal.ExternalStorageMountPolicy)localIterator.next()).hasExternalStorage(paramInt, paramString)) {
          return false;
        }
      }
      return true;
    }
    
    public void onExternalStoragePolicyChanged(int paramInt, String paramString)
    {
      int i = getExternalStorageMountMode(paramInt, paramString);
      MountService.-wrap11(MountService.this, paramInt, i);
    }
  }
  
  abstract class ObbAction
  {
    private static final int MAX_RETRIES = 3;
    MountService.ObbState mObbState;
    private int mRetries;
    
    ObbAction(MountService.ObbState paramObbState)
    {
      this.mObbState = paramObbState;
    }
    
    public void execute(MountService.ObbActionHandler paramObbActionHandler)
    {
      try
      {
        this.mRetries += 1;
        if (this.mRetries > 3)
        {
          Slog.w("MountService", "Failed to invoke remote methods on default container service. Giving up");
          MountService.-get9(MountService.this).sendEmptyMessage(3);
          handleError();
          return;
        }
        handleExecute();
        MountService.-get9(MountService.this).sendEmptyMessage(3);
        return;
      }
      catch (RemoteException paramObbActionHandler)
      {
        MountService.-get9(MountService.this).sendEmptyMessage(4);
        return;
      }
      catch (Exception paramObbActionHandler)
      {
        handleError();
        MountService.-get9(MountService.this).sendEmptyMessage(3);
      }
    }
    
    protected ObbInfo getObbInfo()
      throws IOException
    {
      try
      {
        ObbInfo localObbInfo1 = MountService.-get2(MountService.this).getObbInfo(this.mObbState.canonicalPath);
        if (localObbInfo1 == null) {
          throw new IOException("Couldn't read OBB file: " + this.mObbState.canonicalPath);
        }
      }
      catch (RemoteException localRemoteException)
      {
        ObbInfo localObbInfo2;
        for (;;)
        {
          Slog.d("MountService", "Couldn't call DefaultContainerService to fetch OBB info for " + this.mObbState.canonicalPath);
          localObbInfo2 = null;
        }
        return localObbInfo2;
      }
    }
    
    abstract void handleError();
    
    abstract void handleExecute()
      throws RemoteException, IOException;
    
    protected void sendNewStatusOrIgnore(int paramInt)
    {
      if ((this.mObbState == null) || (this.mObbState.token == null)) {
        return;
      }
      try
      {
        this.mObbState.token.onObbResult(this.mObbState.rawPath, this.mObbState.nonce, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("MountService", "MountServiceListener went away while calling onObbStateChanged");
      }
    }
  }
  
  private class ObbActionHandler
    extends Handler
  {
    private final List<MountService.ObbAction> mActions = new LinkedList();
    private boolean mBound = false;
    
    ObbActionHandler(Looper paramLooper)
    {
      super();
    }
    
    private boolean connectToService()
    {
      Intent localIntent = new Intent().setComponent(MountService.DEFAULT_CONTAINER_COMPONENT);
      if (MountService.-get3(MountService.this).bindServiceAsUser(localIntent, MountService.-get5(MountService.this), 1, UserHandle.SYSTEM))
      {
        this.mBound = true;
        return true;
      }
      return false;
    }
    
    private void disconnectService()
    {
      MountService.-set0(MountService.this, null);
      this.mBound = false;
      MountService.-get3(MountService.this).unbindService(MountService.-get5(MountService.this));
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
      case 1: 
      case 2: 
      case 4: 
      case 3: 
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                ??? = (MountService.ObbAction)???.obj;
                if ((!this.mBound) && (!connectToService()))
                {
                  Slog.e("MountService", "Failed to bind to media container service");
                  ???.handleError();
                  return;
                }
                this.mActions.add(???);
                return;
                if (???.obj != null) {
                  MountService.-set0(MountService.this, (IMediaContainerService)???.obj);
                }
                if (MountService.-get2(MountService.this) == null)
                {
                  Slog.e("MountService", "Cannot bind to media container service");
                  ??? = this.mActions.iterator();
                  while (???.hasNext()) {
                    ((MountService.ObbAction)???.next()).handleError();
                  }
                  this.mActions.clear();
                  return;
                }
                if (this.mActions.size() <= 0) {
                  break;
                }
                ??? = (MountService.ObbAction)this.mActions.get(0);
              } while (??? == null);
              ???.execute(this);
              return;
              Slog.w("MountService", "Empty queue");
              return;
            } while (this.mActions.size() <= 0);
            if (this.mBound) {
              disconnectService();
            }
          } while (connectToService());
          Slog.e("MountService", "Failed to bind to media container service");
          ??? = this.mActions.iterator();
          while (???.hasNext()) {
            ((MountService.ObbAction)???.next()).handleError();
          }
          this.mActions.clear();
          return;
          if (this.mActions.size() > 0) {
            this.mActions.remove(0);
          }
          if (this.mActions.size() != 0) {
            break;
          }
        } while (!this.mBound);
        disconnectService();
        return;
        MountService.-get9(MountService.this).sendEmptyMessage(2);
        return;
      }
      String str = (String)???.obj;
      Object localObject2;
      synchronized (MountService.-get10(MountService.this))
      {
        localObject2 = new LinkedList();
        Iterator localIterator2 = MountService.-get11(MountService.this).values().iterator();
        while (localIterator2.hasNext())
        {
          MountService.ObbState localObbState = (MountService.ObbState)localIterator2.next();
          if (localObbState.canonicalPath.startsWith(str)) {
            ((List)localObject2).add(localObbState);
          }
        }
      }
      Iterator localIterator1 = ((Iterable)localObject2).iterator();
      while (localIterator1.hasNext())
      {
        localObject2 = (MountService.ObbState)localIterator1.next();
        MountService.-wrap12(MountService.this, (MountService.ObbState)localObject2);
        try
        {
          ((MountService.ObbState)localObject2).token.onObbResult(((MountService.ObbState)localObject2).rawPath, ((MountService.ObbState)localObject2).nonce, 2);
        }
        catch (RemoteException localRemoteException)
        {
          Slog.i("MountService", "Couldn't send unmount notification for  OBB: " + ((MountService.ObbState)localObject2).rawPath);
        }
      }
    }
  }
  
  class ObbState
    implements IBinder.DeathRecipient
  {
    final String canonicalPath;
    final int nonce;
    final int ownerGid;
    final String rawPath;
    final IObbActionListener token;
    
    public ObbState(String paramString1, String paramString2, int paramInt1, IObbActionListener paramIObbActionListener, int paramInt2)
    {
      this.rawPath = paramString1;
      this.canonicalPath = paramString2;
      this.ownerGid = UserHandle.getSharedAppGid(paramInt1);
      this.token = paramIObbActionListener;
      this.nonce = paramInt2;
    }
    
    public void binderDied()
    {
      MountService.UnmountObbAction localUnmountObbAction = new MountService.UnmountObbAction(MountService.this, this, true);
      MountService.-get9(MountService.this).sendMessage(MountService.-get9(MountService.this).obtainMessage(1, localUnmountObbAction));
    }
    
    public IBinder getBinder()
    {
      return this.token.asBinder();
    }
    
    public void link()
      throws RemoteException
    {
      getBinder().linkToDeath(this, 0);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("ObbState{");
      localStringBuilder.append("rawPath=").append(this.rawPath);
      localStringBuilder.append(",canonicalPath=").append(this.canonicalPath);
      localStringBuilder.append(",ownerGid=").append(this.ownerGid);
      localStringBuilder.append(",token=").append(this.token);
      localStringBuilder.append(",binder=").append(getBinder());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void unlink()
    {
      getBinder().unlinkToDeath(this, 0);
    }
  }
  
  class UnmountObbAction
    extends MountService.ObbAction
  {
    private final boolean mForceUnmount;
    
    UnmountObbAction(MountService.ObbState paramObbState, boolean paramBoolean)
    {
      super(paramObbState);
      this.mForceUnmount = paramBoolean;
    }
    
    public void handleError()
    {
      sendNewStatusOrIgnore(20);
    }
    
    /* Error */
    public void handleExecute()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   4: invokestatic 36	com/android/server/MountService:-wrap16	(Lcom/android/server/MountService;)V
      //   7: aload_0
      //   8: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   11: invokestatic 39	com/android/server/MountService:-wrap17	(Lcom/android/server/MountService;)V
      //   14: aload_0
      //   15: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   18: invokestatic 43	com/android/server/MountService:-get10	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   21: astore_3
      //   22: aload_3
      //   23: monitorenter
      //   24: aload_0
      //   25: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   28: invokestatic 46	com/android/server/MountService:-get11	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   31: aload_0
      //   32: getfield 50	com/android/server/MountService$UnmountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   35: getfield 56	com/android/server/MountService$ObbState:rawPath	Ljava/lang/String;
      //   38: invokeinterface 62 2 0
      //   43: checkcast 52	com/android/server/MountService$ObbState
      //   46: astore_2
      //   47: aload_3
      //   48: monitorexit
      //   49: aload_2
      //   50: ifnonnull +15 -> 65
      //   53: aload_0
      //   54: bipush 23
      //   56: invokevirtual 27	com/android/server/MountService$UnmountObbAction:sendNewStatusOrIgnore	(I)V
      //   59: return
      //   60: astore_2
      //   61: aload_3
      //   62: monitorexit
      //   63: aload_2
      //   64: athrow
      //   65: aload_2
      //   66: getfield 66	com/android/server/MountService$ObbState:ownerGid	I
      //   69: aload_0
      //   70: getfield 50	com/android/server/MountService$UnmountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   73: getfield 66	com/android/server/MountService$ObbState:ownerGid	I
      //   76: if_icmpeq +55 -> 131
      //   79: ldc 68
      //   81: new 70	java/lang/StringBuilder
      //   84: dup
      //   85: invokespecial 72	java/lang/StringBuilder:<init>	()V
      //   88: ldc 74
      //   90: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   93: aload_2
      //   94: getfield 56	com/android/server/MountService$ObbState:rawPath	Ljava/lang/String;
      //   97: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   100: ldc 80
      //   102: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   105: aload_2
      //   106: getfield 66	com/android/server/MountService$ObbState:ownerGid	I
      //   109: invokevirtual 83	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   112: ldc 85
      //   114: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   117: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   120: invokestatic 95	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   123: pop
      //   124: aload_0
      //   125: bipush 25
      //   127: invokevirtual 27	com/android/server/MountService$UnmountObbAction:sendNewStatusOrIgnore	(I)V
      //   130: return
      //   131: iconst_0
      //   132: istore_1
      //   133: new 97	com/android/server/NativeDaemonConnector$Command
      //   136: dup
      //   137: ldc 99
      //   139: iconst_2
      //   140: anewarray 101	java/lang/Object
      //   143: dup
      //   144: iconst_0
      //   145: ldc 103
      //   147: aastore
      //   148: dup
      //   149: iconst_1
      //   150: aload_0
      //   151: getfield 50	com/android/server/MountService$UnmountObbAction:mObbState	Lcom/android/server/MountService$ObbState;
      //   154: getfield 106	com/android/server/MountService$ObbState:canonicalPath	Ljava/lang/String;
      //   157: aastore
      //   158: invokespecial 109	com/android/server/NativeDaemonConnector$Command:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
      //   161: astore_3
      //   162: aload_0
      //   163: getfield 20	com/android/server/MountService$UnmountObbAction:mForceUnmount	Z
      //   166: ifeq +10 -> 176
      //   169: aload_3
      //   170: ldc 111
      //   172: invokevirtual 115	com/android/server/NativeDaemonConnector$Command:appendArg	(Ljava/lang/Object;)Lcom/android/server/NativeDaemonConnector$Command;
      //   175: pop
      //   176: aload_0
      //   177: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   180: invokestatic 119	com/android/server/MountService:-get1	(Lcom/android/server/MountService;)Lcom/android/server/NativeDaemonConnector;
      //   183: aload_3
      //   184: invokevirtual 125	com/android/server/NativeDaemonConnector:execute	(Lcom/android/server/NativeDaemonConnector$Command;)Lcom/android/server/NativeDaemonEvent;
      //   187: pop
      //   188: iload_1
      //   189: ifne +70 -> 259
      //   192: aload_0
      //   193: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   196: invokestatic 43	com/android/server/MountService:-get10	(Lcom/android/server/MountService;)Ljava/util/Map;
      //   199: astore_3
      //   200: aload_3
      //   201: monitorenter
      //   202: aload_0
      //   203: getfield 15	com/android/server/MountService$UnmountObbAction:this$0	Lcom/android/server/MountService;
      //   206: aload_2
      //   207: invokestatic 128	com/android/server/MountService:-wrap12	(Lcom/android/server/MountService;Lcom/android/server/MountService$ObbState;)V
      //   210: aload_3
      //   211: monitorexit
      //   212: aload_0
      //   213: iconst_2
      //   214: invokevirtual 27	com/android/server/MountService$UnmountObbAction:sendNewStatusOrIgnore	(I)V
      //   217: return
      //   218: astore_3
      //   219: aload_3
      //   220: invokevirtual 132	com/android/server/NativeDaemonConnectorException:getCode	()I
      //   223: istore_1
      //   224: iload_1
      //   225: sipush 405
      //   228: if_icmpne +9 -> 237
      //   231: bipush -7
      //   233: istore_1
      //   234: goto -46 -> 188
      //   237: iload_1
      //   238: sipush 406
      //   241: if_icmpne +8 -> 249
      //   244: iconst_0
      //   245: istore_1
      //   246: goto -58 -> 188
      //   249: iconst_m1
      //   250: istore_1
      //   251: goto -63 -> 188
      //   254: astore_2
      //   255: aload_3
      //   256: monitorexit
      //   257: aload_2
      //   258: athrow
      //   259: ldc 68
      //   261: new 70	java/lang/StringBuilder
      //   264: dup
      //   265: invokespecial 72	java/lang/StringBuilder:<init>	()V
      //   268: ldc -122
      //   270: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   273: aload_2
      //   274: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   277: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   280: invokestatic 95	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   283: pop
      //   284: aload_0
      //   285: bipush 22
      //   287: invokevirtual 27	com/android/server/MountService$UnmountObbAction:sendNewStatusOrIgnore	(I)V
      //   290: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	291	0	this	UnmountObbAction
      //   132	119	1	i	int
      //   46	4	2	localObbState1	MountService.ObbState
      //   60	147	2	localObbState2	MountService.ObbState
      //   254	20	2	localObject1	Object
      //   218	38	3	localNativeDaemonConnectorException	NativeDaemonConnectorException
      // Exception table:
      //   from	to	target	type
      //   24	47	60	finally
      //   133	176	218	com/android/server/NativeDaemonConnectorException
      //   176	188	218	com/android/server/NativeDaemonConnectorException
      //   202	210	254	finally
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UnmountObbAction{");
      localStringBuilder.append(this.mObbState);
      localStringBuilder.append(",force=");
      localStringBuilder.append(this.mForceUnmount);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  class VoldResponseCode
  {
    public static final int AsecListResult = 111;
    public static final int AsecPathResult = 211;
    public static final int BENCHMARK_RESULT = 661;
    public static final int CryptfsGetfieldResult = 113;
    public static final int DISK_CREATED = 640;
    public static final int DISK_DESTROYED = 649;
    public static final int DISK_LABEL_CHANGED = 642;
    public static final int DISK_SCANNED = 643;
    public static final int DISK_SIZE_CHANGED = 641;
    public static final int DISK_SYS_PATH_CHANGED = 644;
    public static final int MOVE_STATUS = 660;
    public static final int OpFailedMediaBlank = 402;
    public static final int OpFailedMediaCorrupt = 403;
    public static final int OpFailedNoMedia = 401;
    public static final int OpFailedStorageBusy = 405;
    public static final int OpFailedStorageNotFound = 406;
    public static final int OpFailedVolNotMounted = 404;
    public static final int ShareEnabledResult = 212;
    public static final int ShareStatusResult = 210;
    public static final int StorageUsersListResult = 112;
    public static final int TRIM_RESULT = 662;
    public static final int VOLUME_CREATED = 650;
    public static final int VOLUME_DESTROYED = 659;
    public static final int VOLUME_FS_LABEL_CHANGED = 654;
    public static final int VOLUME_FS_TYPE_CHANGED = 652;
    public static final int VOLUME_FS_UUID_CHANGED = 653;
    public static final int VOLUME_INTERNAL_PATH_CHANGED = 656;
    public static final int VOLUME_PATH_CHANGED = 655;
    public static final int VOLUME_STATE_CHANGED = 651;
    public static final int VolumeListResult = 110;
    
    VoldResponseCode() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/MountService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */