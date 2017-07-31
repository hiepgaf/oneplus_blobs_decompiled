package com.android.server.am;

import android.app.ActivityManager.HighPowerApp;
import android.app.INotificationManager;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.OpFeatures;
import android.util.Slog;
import com.android.internal.os.ProcessCpuTracker.Stats;
import com.android.server.SystemEventCollector;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;

class OnePlusHighPowerDetector
{
  private static final String ACTION_FORCE_STOP_PKG = "com.oem.intent.action.force_stop_pkg";
  private static final String ACTION_TEST = "ohpd.action.test";
  static int APP_LAST_CONTACT_PROVIDER_TIME_THRESHOLD = 0;
  static int APP_LAST_FOREGROUND_TIME_THRESHOLD = 0;
  private static String BACKGROUND_DETECTION_CONFIG_NAME;
  static int[] BG_DETECTION_CPU_USAGE_THRESHOLD_MAX;
  static int[] BG_DETECTION_CPU_USAGE_THRESHOLD_MIN;
  static int BG_DETECTION_NETWORK_USAGE_THRESHOLD = 0;
  public static final int CHECK_CUSTOMIZED_NOTIFICATION_MSG = 55005;
  public static final int CHECK_EXCESSIVE_CPU_DEFAULT_MSG = 55000;
  public static final int CHECK_EXCESSIVE_CPU_MODE1_MSG = 55001;
  public static final int CHECK_EXCESSIVE_CPU_MODE2_MSG = 55002;
  public static final int CHECK_EXCESSIVE_CPU_MODE3_MSG = 55006;
  public static final int CLEANUP_PACKAGE_RECORD_MSG = 55004;
  static int[] CPU_CHECK_DELAY;
  public static boolean DEBUG = false;
  static boolean DEBUG_BG_USAGE_QUICK = false;
  private static final String DEVICE_TEMP_PATH = "/sys/class/thermal/thermal_zone5/temp";
  public static boolean ENABLE = false;
  public static final int FORCE_STOP_PKG_MSG = 55008;
  public static final int GLOBAL_FLAG_SETTED_SIM_COUNTRY = 1;
  static final long MILLIS_PER_DAY = 86400000L;
  static final long MILLIS_PER_HOUR = 3600000L;
  static final int[] MODE_MSGS;
  static long NOTIFY_INTERVAL = 0L;
  public static final int NUM_CPU_MONITOR_LEVELS = 4;
  static boolean ONLINE_CONFIG = false;
  static int PD_LAST_FG_TIME_THOLD = 0;
  public static final int POST_CUSTOMIZED_NOTIFICATION_MSG = 55003;
  static int POWER_DRAIN_TEMP_THOLD = 0;
  static int POWER_DRAIN_USG_THOLD = 0;
  private static final String PROP_DEBUG = "persist.sys.ohpd.debug";
  private static final String PROP_DEBUG_BG_USAGE_QUICK = "persist.sys.ohpd.debug.bg";
  private static final String PROP_ENABLE = "persist.sys.ohpd.enable";
  private static final String PROP_FLAGS = "persist.sys.ohpd.flags";
  private static final String PROP_KILL_ON = "persist.sys.ohpd.kcheck";
  private static final String PROP_NOTIFY_INTERVAL = "persist.sys.ohpd.notify";
  private static final String PROP_ONLINE_CONFIG = "persist.sys.ohpd.onlineconfig";
  private static final String PROP_POWER_DRAIN_LAST_FG_THOLD = "persist.sys.ohpd.pd.lastfg";
  private static final String PROP_POWER_DRAIN_TEMP_THOLD = "persist.sys.ohpd.pd.temp.thold";
  private static final String PROP_POWER_DRAIN_USG_THOLD = "persist.sys.ohpd.pd.usg.thold";
  private static final String PROP_SIM_COUNTRY = "gsm.sim.operator.iso-country";
  private static final String PROP_THRESHOLD = "persist.sys.ohpd.threshold";
  private static final String SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
  public static final String TAG = "OHPD";
  public static final int VERSION = 17021301;
  private static ActivityManagerService mAms;
  private static BatteryStatsService mBatteryStatsService;
  private static Context mContext;
  private static int mGlobalFlags;
  private static Handler mHandler;
  private static boolean mKillMechanism;
  private static String mRegion;
  static Object sBattUpdteLock;
  static Object sConfigLock;
  private ArrayList<String> blackAppListSet;
  private ArrayList<String> blackExAppListSet;
  private ArrayList<String> killProcList;
  HashMap<String, AppForkedProc> mAppForkedProcMap = new HashMap();
  private AudioManager mAudioManager;
  private ConfigObserver mBackgroundDetectionConfigObserver;
  boolean mBgDetectStartMonitoring = false;
  String mCurNotifyPkgName = null;
  HashSet<String> mCurNotifyPkgNameSet = new HashSet();
  private BroadcastReceiver mGeneralReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (OnePlusHighPowerDetector.DEBUG) {
        OnePlusHighPowerDetector.myLog("# mGeneralReceiver # onReceive # action=" + paramAnonymousContext);
      }
      if ("ohpd.action.test".equals(paramAnonymousContext))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("code");
        OnePlusHighPowerDetector.myLog("# mGeneralReceiver # onReceive # code = " + paramAnonymousContext);
        if ((paramAnonymousContext != null) && (paramAnonymousContext.startsWith("notify#")))
        {
          paramAnonymousContext = paramAnonymousContext.substring("notify#".length());
          OnePlusHighPowerDetector.myLog("pkg=" + paramAnonymousContext);
          OnePlusHighPowerDetector.-wrap1(OnePlusHighPowerDetector.this, paramAnonymousContext, 0, "broadcast");
        }
      }
      do
      {
        do
        {
          return;
          if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousContext))
          {
            OnePlusHighPowerDetector.-wrap0(OnePlusHighPowerDetector.this);
            return;
          }
        } while (!"com.oem.intent.action.force_stop_pkg".equals(paramAnonymousContext));
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("pkg");
      } while ((paramAnonymousContext == null) || (paramAnonymousContext.isEmpty()));
      OnePlusHighPowerDetector.-wrap3(OnePlusHighPowerDetector.this, paramAnonymousContext);
    }
  };
  private SystemEventCollector mHighPowerEventCollector;
  HashMap<String, ActivityManager.HighPowerApp> mHighPowerPkgMap = new HashMap();
  HashMap<String, ActivityManager.HighPowerApp> mHugePowerPkgMap = new HashMap();
  boolean mIsPowerDrain = false;
  long mLastBatteryDropTime = 0L;
  int mLastBatteryPercent = 0;
  long mLastBgDetectCleanUpTime = 0L;
  long[] mLastCpuCheckUptime = new long[4];
  HashMap<String, ActivityManager.HighPowerApp> mMediumPowerPkgMap = new HashMap();
  HashMap<String, Long> mNotifyPkgMap = new HashMap();
  ArrayList<String> mPendingRemoveList = new ArrayList();
  ArrayList<Integer> mWorkingForkedPidList = new ArrayList();
  private ArrayList<String> whiteAppListSet;
  
  static
  {
    int j = 60;
    ENABLE = true;
    DEBUG = Build.DEBUG_ONEPLUS;
    DEBUG_BG_USAGE_QUICK = SystemProperties.getBoolean("persist.sys.ohpd.debug.bg", false);
    NOTIFY_INTERVAL = SystemProperties.getLong("persist.sys.ohpd.notify", 3600000L);
    ONLINE_CONFIG = SystemProperties.getBoolean("persist.sys.ohpd.onlineconfig", true);
    int i;
    label73:
    label90:
    label96:
    int k;
    if (DEBUG_BG_USAGE_QUICK)
    {
      i = 60;
      APP_LAST_FOREGROUND_TIME_THRESHOLD = i * 1000;
      PD_LAST_FG_TIME_THOLD = 14400000;
      if (!DEBUG_BG_USAGE_QUICK) {
        break label294;
      }
      i = 60;
      APP_LAST_CONTACT_PROVIDER_TIME_THRESHOLD = i * 1000;
      if (!DEBUG_BG_USAGE_QUICK) {
        break label301;
      }
      i = 60;
      if (!DEBUG_BG_USAGE_QUICK) {
        break label308;
      }
      if (!DEBUG_BG_USAGE_QUICK) {
        break label315;
      }
      k = 120;
      label105:
      if (!DEBUG_BG_USAGE_QUICK) {
        break label322;
      }
    }
    label294:
    label301:
    label308:
    label315:
    label322:
    for (int m = 120;; m = 1200)
    {
      CPU_CHECK_DELAY = new int[] { i * 1000, j * 1000, k * 1000, m * 1000 };
      MODE_MSGS = new int[] { 55000, 55001, 55002, 55006 };
      BG_DETECTION_CPU_USAGE_THRESHOLD_MAX = new int[] { 1000, 20, 15, 1000 };
      BG_DETECTION_CPU_USAGE_THRESHOLD_MIN = new int[] { 20, 15, 10, 20 };
      BG_DETECTION_NETWORK_USAGE_THRESHOLD = 20971520;
      POWER_DRAIN_USG_THOLD = 5;
      POWER_DRAIN_TEMP_THOLD = 48;
      sConfigLock = new Object();
      sBattUpdteLock = new Object();
      mRegion = "";
      mGlobalFlags = 0;
      mKillMechanism = false;
      BACKGROUND_DETECTION_CONFIG_NAME = "BackgroundDetection";
      return;
      i = 300;
      break;
      i = 300;
      break label73;
      i = 300;
      break label90;
      j = 300;
      break label96;
      k = 600;
      break label105;
    }
  }
  
  public OnePlusHighPowerDetector(ActivityManagerService paramActivityManagerService, Context paramContext, Handler paramHandler, BatteryStatsService paramBatteryStatsService)
  {
    ENABLE = SystemProperties.getBoolean("persist.sys.ohpd.enable", ENABLE);
    DEBUG = SystemProperties.getBoolean("persist.sys.ohpd.debug", DEBUG);
    if (!ENABLE)
    {
      Slog.e("OHPD", "disabled");
      return;
    }
    myLog("OnePlusHighPowerDetector--constructor");
    mAms = paramActivityManagerService;
    mContext = paramContext;
    mHandler = paramHandler;
    mBatteryStatsService = paramBatteryStatsService;
    this.mHighPowerEventCollector = new SystemEventCollector(mContext, "HighPowerDetector");
    init();
  }
  
  /* Error */
  private int checkExcessiveCpuUsageLocked(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: getstatic 214	com/android/server/am/OnePlusHighPowerDetector:DEBUG	Z
    //   3: ifeq +39 -> 42
    //   6: ldc 121
    //   8: new 353	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 356
    //   18: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: iload_1
    //   22: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   25: ldc_w 365
    //   28: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: iload_2
    //   32: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   35: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   38: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   41: pop
    //   42: invokestatic 378	android/os/SystemClock:uptimeMillis	()J
    //   45: lstore 7
    //   47: iconst_1
    //   48: istore 18
    //   50: iconst_0
    //   51: istore 17
    //   53: iconst_0
    //   54: istore 19
    //   56: iconst_0
    //   57: istore_3
    //   58: iconst_0
    //   59: istore 4
    //   61: new 306	java/util/ArrayList
    //   64: dup
    //   65: invokespecial 307	java/util/ArrayList:<init>	()V
    //   68: astore 29
    //   70: new 306	java/util/ArrayList
    //   73: dup
    //   74: invokespecial 307	java/util/ArrayList:<init>	()V
    //   77: astore 21
    //   79: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   82: invokevirtual 383	com/android/server/am/ActivityManagerService:updateCpuStatsNow	()V
    //   85: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   88: astore 20
    //   90: aload 20
    //   92: monitorenter
    //   93: invokestatic 386	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   96: getstatic 335	com/android/server/am/OnePlusHighPowerDetector:mBatteryStatsService	Lcom/android/server/am/BatteryStatsService;
    //   99: invokevirtual 392	com/android/server/am/BatteryStatsService:getActiveStatistics	()Lcom/android/internal/os/BatteryStatsImpl;
    //   102: astore 32
    //   104: aload_0
    //   105: getfield 276	com/android/server/am/OnePlusHighPowerDetector:mLastCpuCheckUptime	[J
    //   108: iload_1
    //   109: laload
    //   110: lconst_0
    //   111: lcmp
    //   112: ifeq +160 -> 272
    //   115: iconst_1
    //   116: newarray <illegal type>
    //   118: dup
    //   119: iconst_0
    //   120: bipush 14
    //   122: iastore
    //   123: invokestatic 398	android/util/OpFeatures:isSupport	([I)Z
    //   126: ifeq +146 -> 272
    //   129: invokestatic 378	android/os/SystemClock:uptimeMillis	()J
    //   132: lstore 11
    //   134: lload 11
    //   136: aload_0
    //   137: getfield 276	com/android/server/am/OnePlusHighPowerDetector:mLastCpuCheckUptime	[J
    //   140: iload_1
    //   141: laload
    //   142: lsub
    //   143: lstore 9
    //   145: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   148: lstore 13
    //   150: aload_0
    //   151: getfield 274	com/android/server/am/OnePlusHighPowerDetector:mLastBgDetectCleanUpTime	J
    //   154: lconst_0
    //   155: lcmp
    //   156: ifne +122 -> 278
    //   159: aload_0
    //   160: lload 13
    //   162: putfield 274	com/android/server/am/OnePlusHighPowerDetector:mLastBgDetectCleanUpTime	J
    //   165: lload 9
    //   167: iload_2
    //   168: i2l
    //   169: lcmp
    //   170: ifge +142 -> 312
    //   173: ldc 121
    //   175: new 353	java/lang/StringBuilder
    //   178: dup
    //   179: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   182: ldc_w 403
    //   185: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: iload_1
    //   189: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   192: ldc_w 405
    //   195: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: lload 9
    //   200: ldc2_w 406
    //   203: ldiv
    //   204: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   207: ldc_w 412
    //   210: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   213: iconst_0
    //   214: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   217: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   223: pop
    //   224: getstatic 241	com/android/server/am/OnePlusHighPowerDetector:MODE_MSGS	[I
    //   227: iload_1
    //   228: iaload
    //   229: istore_1
    //   230: getstatic 333	com/android/server/am/OnePlusHighPowerDetector:mHandler	Landroid/os/Handler;
    //   233: iload_1
    //   234: invokevirtual 421	android/os/Handler:removeMessages	(I)V
    //   237: getstatic 333	com/android/server/am/OnePlusHighPowerDetector:mHandler	Landroid/os/Handler;
    //   240: iload_1
    //   241: invokevirtual 425	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
    //   244: astore 21
    //   246: getstatic 333	com/android/server/am/OnePlusHighPowerDetector:mHandler	Landroid/os/Handler;
    //   249: aload 21
    //   251: iload_2
    //   252: i2l
    //   253: lload 9
    //   255: lsub
    //   256: ldc2_w 426
    //   259: ladd
    //   260: invokevirtual 431	android/os/Handler:sendMessageDelayed	(Landroid/os/Message;J)Z
    //   263: pop
    //   264: aload 20
    //   266: monitorexit
    //   267: invokestatic 434	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   270: iconst_m1
    //   271: ireturn
    //   272: iconst_0
    //   273: istore 18
    //   275: goto -146 -> 129
    //   278: lload 13
    //   280: aload_0
    //   281: getfield 274	com/android/server/am/OnePlusHighPowerDetector:mLastBgDetectCleanUpTime	J
    //   284: lsub
    //   285: ldc2_w 63
    //   288: lcmp
    //   289: ifle -124 -> 165
    //   292: aload_0
    //   293: lload 13
    //   295: putfield 274	com/android/server/am/OnePlusHighPowerDetector:mLastBgDetectCleanUpTime	J
    //   298: goto -133 -> 165
    //   301: astore 21
    //   303: aload 20
    //   305: monitorexit
    //   306: invokestatic 434	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   309: aload 21
    //   311: athrow
    //   312: aload 20
    //   314: monitorexit
    //   315: invokestatic 434	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   318: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   321: getfield 438	com/android/server/am/ActivityManagerService:mWindowManager	Lcom/android/server/wm/WindowManagerService;
    //   324: invokevirtual 444	com/android/server/wm/WindowManagerService:getVisibleWindowUids	()Ljava/util/List;
    //   327: astore 30
    //   329: aload_0
    //   330: invokespecial 448	com/android/server/am/OnePlusHighPowerDetector:getActiveAudioUids	()[Ljava/lang/String;
    //   333: astore 31
    //   335: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   338: getfield 452	com/android/server/am/ActivityManagerService:mStackSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   341: invokevirtual 457	com/android/server/am/ActivityStackSupervisor:getRecentAppLockedPackages	()Ljava/util/List;
    //   344: astore 22
    //   346: iload_1
    //   347: ifne +66 -> 413
    //   350: getstatic 259	com/android/server/am/OnePlusHighPowerDetector:sBattUpdteLock	Ljava/lang/Object;
    //   353: astore 20
    //   355: aload 20
    //   357: monitorenter
    //   358: aload_0
    //   359: invokevirtual 461	com/android/server/am/OnePlusHighPowerDetector:getDeviceTemp	()I
    //   362: istore_2
    //   363: aload_0
    //   364: getfield 284	com/android/server/am/OnePlusHighPowerDetector:mIsPowerDrain	Z
    //   367: ifne +14 -> 381
    //   370: iload 19
    //   372: istore 17
    //   374: iload_2
    //   375: getstatic 252	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_TEMP_THOLD	I
    //   378: if_icmplt +32 -> 410
    //   381: ldc 121
    //   383: new 353	java/lang/StringBuilder
    //   386: dup
    //   387: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   390: ldc_w 463
    //   393: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   396: iload_2
    //   397: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   400: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   403: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   406: pop
    //   407: iconst_1
    //   408: istore 17
    //   410: aload 20
    //   412: monitorexit
    //   413: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   416: astore 20
    //   418: aload 20
    //   420: monitorenter
    //   421: invokestatic 386	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   424: aload_0
    //   425: getfield 276	com/android/server/am/OnePlusHighPowerDetector:mLastCpuCheckUptime	[J
    //   428: iload_1
    //   429: lload 11
    //   431: lastore
    //   432: ldc 121
    //   434: new 353	java/lang/StringBuilder
    //   437: dup
    //   438: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   441: ldc_w 465
    //   444: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   447: iload_1
    //   448: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   451: ldc_w 467
    //   454: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   457: iload 18
    //   459: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   462: ldc_w 469
    //   465: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   468: iload 17
    //   470: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   473: ldc_w 471
    //   476: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   479: lload 9
    //   481: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   484: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   487: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   490: pop
    //   491: new 473	android/util/ArrayMap
    //   494: dup
    //   495: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   498: astore 28
    //   500: new 473	android/util/ArrayMap
    //   503: dup
    //   504: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   507: astore 26
    //   509: new 473	android/util/ArrayMap
    //   512: dup
    //   513: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   516: astore 23
    //   518: new 473	android/util/ArrayMap
    //   521: dup
    //   522: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   525: astore 25
    //   527: new 473	android/util/ArrayMap
    //   530: dup
    //   531: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   534: astore 27
    //   536: new 473	android/util/ArrayMap
    //   539: dup
    //   540: invokespecial 474	android/util/ArrayMap:<init>	()V
    //   543: astore 24
    //   545: iload_1
    //   546: ifne +22 -> 568
    //   549: aload_0
    //   550: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   553: astore 33
    //   555: aload 33
    //   557: monitorenter
    //   558: aload_0
    //   559: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   562: invokevirtual 477	java/util/HashMap:clear	()V
    //   565: aload 33
    //   567: monitorexit
    //   568: iload_1
    //   569: iconst_1
    //   570: if_icmpne +22 -> 592
    //   573: aload_0
    //   574: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   577: astore 33
    //   579: aload 33
    //   581: monitorenter
    //   582: aload_0
    //   583: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   586: invokevirtual 477	java/util/HashMap:clear	()V
    //   589: aload 33
    //   591: monitorexit
    //   592: iload_1
    //   593: iconst_2
    //   594: if_icmpne +22 -> 616
    //   597: aload_0
    //   598: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   601: astore 33
    //   603: aload 33
    //   605: monitorenter
    //   606: aload_0
    //   607: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   610: invokevirtual 477	java/util/HashMap:clear	()V
    //   613: aload 33
    //   615: monitorexit
    //   616: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   619: getfield 480	com/android/server/am/ActivityManagerService:mLruProcesses	Ljava/util/ArrayList;
    //   622: invokevirtual 483	java/util/ArrayList:size	()I
    //   625: istore_2
    //   626: iload_2
    //   627: ifle +1674 -> 2301
    //   630: iload_2
    //   631: iconst_1
    //   632: isub
    //   633: istore 5
    //   635: getstatic 331	com/android/server/am/OnePlusHighPowerDetector:mAms	Lcom/android/server/am/ActivityManagerService;
    //   638: getfield 480	com/android/server/am/ActivityManagerService:mLruProcesses	Ljava/util/ArrayList;
    //   641: iload 5
    //   643: invokevirtual 487	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   646: checkcast 489	com/android/server/am/ProcessRecord
    //   649: astore 33
    //   651: aload_0
    //   652: aload 33
    //   654: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   657: getfield 498	android/content/pm/ApplicationInfo:flags	I
    //   660: invokevirtual 502	com/android/server/am/OnePlusHighPowerDetector:hasSystemFlag	(I)Z
    //   663: ifeq +12 -> 675
    //   666: aload_0
    //   667: aload 33
    //   669: invokevirtual 506	com/android/server/am/OnePlusHighPowerDetector:checkKillList	(Lcom/android/server/am/ProcessRecord;)Z
    //   672: ifeq +155 -> 827
    //   675: iload 18
    //   677: ifne +274 -> 951
    //   680: aload_0
    //   681: aload 33
    //   683: iload_1
    //   684: invokevirtual 510	com/android/server/am/OnePlusHighPowerDetector:updateProcUsg	(Lcom/android/server/am/ProcessRecord;I)V
    //   687: iload 5
    //   689: istore_2
    //   690: goto -64 -> 626
    //   693: astore 22
    //   695: aload 22
    //   697: invokevirtual 513	java/lang/Exception:printStackTrace	()V
    //   700: ldc 121
    //   702: new 353	java/lang/StringBuilder
    //   705: dup
    //   706: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   709: ldc_w 515
    //   712: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   715: aload 22
    //   717: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   720: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   723: invokestatic 324	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   726: pop
    //   727: aload 20
    //   729: monitorexit
    //   730: invokestatic 434	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   733: aload 21
    //   735: ifnull +5392 -> 6127
    //   738: aload 21
    //   740: invokevirtual 483	java/util/ArrayList:size	()I
    //   743: ifle +5384 -> 6127
    //   746: ldc_w 261
    //   749: astore 20
    //   751: iconst_0
    //   752: istore_2
    //   753: iload_2
    //   754: aload 21
    //   756: invokevirtual 483	java/util/ArrayList:size	()I
    //   759: if_icmpge +5336 -> 6095
    //   762: iload_2
    //   763: ifne +5294 -> 6057
    //   766: aload 21
    //   768: iload_2
    //   769: invokevirtual 487	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   772: checkcast 520	java/lang/String
    //   775: astore 20
    //   777: iload_2
    //   778: iconst_1
    //   779: iadd
    //   780: istore_2
    //   781: goto -28 -> 753
    //   784: astore 21
    //   786: aload 20
    //   788: monitorexit
    //   789: aload 21
    //   791: athrow
    //   792: astore 21
    //   794: aload 33
    //   796: monitorexit
    //   797: aload 21
    //   799: athrow
    //   800: astore 21
    //   802: aload 20
    //   804: monitorexit
    //   805: invokestatic 434	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   808: aload 21
    //   810: athrow
    //   811: astore 21
    //   813: aload 33
    //   815: monitorexit
    //   816: aload 21
    //   818: athrow
    //   819: astore 21
    //   821: aload 33
    //   823: monitorexit
    //   824: aload 21
    //   826: athrow
    //   827: aload_0
    //   828: aload 26
    //   830: aload 33
    //   832: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   835: new 353	java/lang/StringBuilder
    //   838: dup
    //   839: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   842: ldc_w 525
    //   845: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   848: aload 33
    //   850: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   853: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   856: ldc_w 530
    //   859: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   862: aload 33
    //   864: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   867: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   870: ldc_w 535
    //   873: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   876: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   879: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   882: iload 5
    //   884: istore_2
    //   885: iload 17
    //   887: ifeq -261 -> 626
    //   890: aload_0
    //   891: aload 23
    //   893: aload 33
    //   895: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   898: new 353	java/lang/StringBuilder
    //   901: dup
    //   902: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   905: ldc_w 525
    //   908: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   911: aload 33
    //   913: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   916: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   919: ldc_w 530
    //   922: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   925: aload 33
    //   927: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   930: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   933: ldc_w 535
    //   936: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   939: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   942: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   945: iload 5
    //   947: istore_2
    //   948: goto -322 -> 626
    //   951: aload 33
    //   953: getfield 542	com/android/server/am/ProcessRecord:curCpuTimeBgMonitor	J
    //   956: aload 33
    //   958: getfield 545	com/android/server/am/ProcessRecord:lastCpuTimeBgMonitor	[J
    //   961: iload_1
    //   962: laload
    //   963: lsub
    //   964: lstore 13
    //   966: aload 33
    //   968: getfield 545	com/android/server/am/ProcessRecord:lastCpuTimeBgMonitor	[J
    //   971: iload_1
    //   972: laload
    //   973: lconst_0
    //   974: lcmp
    //   975: ifgt +203 -> 1178
    //   978: aload_0
    //   979: aload 33
    //   981: iload_1
    //   982: invokevirtual 510	com/android/server/am/OnePlusHighPowerDetector:updateProcUsg	(Lcom/android/server/am/ProcessRecord;I)V
    //   985: new 547	java/lang/StringBuffer
    //   988: dup
    //   989: invokespecial 548	java/lang/StringBuffer:<init>	()V
    //   992: astore 34
    //   994: iload 17
    //   996: ifeq +34 -> 1030
    //   999: aload_0
    //   1000: aload 33
    //   1002: lload 11
    //   1004: iload_1
    //   1005: aload 34
    //   1007: iconst_1
    //   1008: invokespecial 552	com/android/server/am/OnePlusHighPowerDetector:skipCheck	(Lcom/android/server/am/ProcessRecord;JILjava/lang/StringBuffer;Z)Z
    //   1011: ifeq +19 -> 1030
    //   1014: aload_0
    //   1015: aload 23
    //   1017: aload 33
    //   1019: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1022: aload 34
    //   1024: invokevirtual 553	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   1027: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   1030: new 547	java/lang/StringBuffer
    //   1033: dup
    //   1034: invokespecial 548	java/lang/StringBuffer:<init>	()V
    //   1037: astore 34
    //   1039: iload 5
    //   1041: istore_2
    //   1042: aload_0
    //   1043: aload 33
    //   1045: lload 11
    //   1047: iload_1
    //   1048: aload 34
    //   1050: iconst_0
    //   1051: invokespecial 552	com/android/server/am/OnePlusHighPowerDetector:skipCheck	(Lcom/android/server/am/ProcessRecord;JILjava/lang/StringBuffer;Z)Z
    //   1054: ifeq -428 -> 626
    //   1057: aload_0
    //   1058: aload 26
    //   1060: aload 33
    //   1062: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1065: aload 34
    //   1067: invokevirtual 553	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   1070: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   1073: ldc2_w 554
    //   1076: lload 13
    //   1078: lmul
    //   1079: lload 9
    //   1081: ldiv
    //   1082: lstore 13
    //   1084: aload 27
    //   1086: aload 33
    //   1088: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1091: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1094: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   1097: ifeq +55 -> 1152
    //   1100: aload 27
    //   1102: aload 33
    //   1104: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1107: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1110: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   1113: checkcast 557	java/lang/Integer
    //   1116: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1119: istore_2
    //   1120: lload 13
    //   1122: l2i
    //   1123: istore 6
    //   1125: aload 27
    //   1127: aload 33
    //   1129: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1132: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1135: iload_2
    //   1136: iload 6
    //   1138: iadd
    //   1139: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1142: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1145: pop
    //   1146: iload 5
    //   1148: istore_2
    //   1149: goto -523 -> 626
    //   1152: aload 27
    //   1154: aload 33
    //   1156: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1159: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1162: lload 13
    //   1164: l2i
    //   1165: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1168: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1171: pop
    //   1172: iload 5
    //   1174: istore_2
    //   1175: goto -549 -> 626
    //   1178: aload_0
    //   1179: aload 33
    //   1181: invokevirtual 578	com/android/server/am/OnePlusHighPowerDetector:isProcInBlackExList	(Lcom/android/server/am/ProcessRecord;)Z
    //   1184: ifeq +19 -> 1203
    //   1187: aload 29
    //   1189: aload 33
    //   1191: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1194: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1197: invokeinterface 583 2 0
    //   1202: pop
    //   1203: ldc2_w 554
    //   1206: lload 13
    //   1208: lmul
    //   1209: lload 9
    //   1211: ldiv
    //   1212: lstore 15
    //   1214: lload 15
    //   1216: ldc2_w 584
    //   1219: lcmp
    //   1220: ifle +75 -> 1295
    //   1223: new 353	java/lang/StringBuilder
    //   1226: dup
    //   1227: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1230: aload 33
    //   1232: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1235: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1238: ldc_w 530
    //   1241: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1244: aload 33
    //   1246: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   1249: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1252: ldc_w 587
    //   1255: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1258: iload_1
    //   1259: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1262: ldc_w 589
    //   1265: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1268: lload 15
    //   1270: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1273: ldc_w 591
    //   1276: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1279: aload 33
    //   1281: getfield 545	com/android/server/am/ProcessRecord:lastCpuTimeBgMonitor	[J
    //   1284: iload_1
    //   1285: laload
    //   1286: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1289: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1292: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   1295: aload_0
    //   1296: aload 33
    //   1298: iload_1
    //   1299: invokevirtual 595	com/android/server/am/OnePlusHighPowerDetector:needCheckProc	(Lcom/android/server/am/ProcessRecord;I)Z
    //   1302: ifne +30 -> 1332
    //   1305: aload_0
    //   1306: aload 33
    //   1308: invokevirtual 598	com/android/server/am/OnePlusHighPowerDetector:checkBlackList	(Lcom/android/server/am/ProcessRecord;)Z
    //   1311: ifne +21 -> 1332
    //   1314: aload_0
    //   1315: aload 33
    //   1317: invokevirtual 506	com/android/server/am/OnePlusHighPowerDetector:checkKillList	(Lcom/android/server/am/ProcessRecord;)Z
    //   1320: ifne +12 -> 1332
    //   1323: aload_0
    //   1324: aload 33
    //   1326: invokevirtual 601	com/android/server/am/OnePlusHighPowerDetector:checkBlackExList	(Lcom/android/server/am/ProcessRecord;)Z
    //   1329: ifeq +753 -> 2082
    //   1332: new 547	java/lang/StringBuffer
    //   1335: dup
    //   1336: invokespecial 548	java/lang/StringBuffer:<init>	()V
    //   1339: astore 34
    //   1341: iload 17
    //   1343: ifeq +34 -> 1377
    //   1346: aload_0
    //   1347: aload 33
    //   1349: lload 11
    //   1351: iload_1
    //   1352: aload 34
    //   1354: iconst_1
    //   1355: invokespecial 552	com/android/server/am/OnePlusHighPowerDetector:skipCheck	(Lcom/android/server/am/ProcessRecord;JILjava/lang/StringBuffer;Z)Z
    //   1358: ifeq +19 -> 1377
    //   1361: aload_0
    //   1362: aload 23
    //   1364: aload 33
    //   1366: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1369: aload 34
    //   1371: invokevirtual 553	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   1374: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   1377: new 547	java/lang/StringBuffer
    //   1380: dup
    //   1381: invokespecial 548	java/lang/StringBuffer:<init>	()V
    //   1384: astore 34
    //   1386: aload_0
    //   1387: aload 33
    //   1389: lload 11
    //   1391: iload_1
    //   1392: aload 34
    //   1394: iconst_0
    //   1395: invokespecial 552	com/android/server/am/OnePlusHighPowerDetector:skipCheck	(Lcom/android/server/am/ProcessRecord;JILjava/lang/StringBuffer;Z)Z
    //   1398: ifeq +201 -> 1599
    //   1401: aload_0
    //   1402: aload 33
    //   1404: iload_1
    //   1405: invokevirtual 510	com/android/server/am/OnePlusHighPowerDetector:updateProcUsg	(Lcom/android/server/am/ProcessRecord;I)V
    //   1408: aload_0
    //   1409: aload 26
    //   1411: aload 33
    //   1413: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1416: aload 34
    //   1418: invokevirtual 553	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   1421: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   1424: lload 15
    //   1426: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   1429: iload_1
    //   1430: iaload
    //   1431: i2l
    //   1432: lcmp
    //   1433: iflt +72 -> 1505
    //   1436: ldc 121
    //   1438: new 353	java/lang/StringBuilder
    //   1441: dup
    //   1442: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1445: ldc_w 603
    //   1448: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1451: aload 33
    //   1453: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1456: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1459: ldc_w 605
    //   1462: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1465: aload 33
    //   1467: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1470: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1473: ldc_w 607
    //   1476: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1479: aload 26
    //   1481: aload 33
    //   1483: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1486: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1489: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   1492: checkcast 520	java/lang/String
    //   1495: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1498: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1501: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1504: pop
    //   1505: aload 27
    //   1507: aload 33
    //   1509: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1512: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1515: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   1518: ifeq +55 -> 1573
    //   1521: aload 27
    //   1523: aload 33
    //   1525: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1528: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1531: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   1534: checkcast 557	java/lang/Integer
    //   1537: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1540: istore_2
    //   1541: lload 15
    //   1543: l2i
    //   1544: istore 6
    //   1546: aload 27
    //   1548: aload 33
    //   1550: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1553: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1556: iload_2
    //   1557: iload 6
    //   1559: iadd
    //   1560: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1563: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1566: pop
    //   1567: iload 5
    //   1569: istore_2
    //   1570: goto -944 -> 626
    //   1573: aload 27
    //   1575: aload 33
    //   1577: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1580: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1583: lload 15
    //   1585: l2i
    //   1586: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1589: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1592: pop
    //   1593: iload 5
    //   1595: istore_2
    //   1596: goto -970 -> 626
    //   1599: lload 15
    //   1601: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   1604: iload_1
    //   1605: iaload
    //   1606: i2l
    //   1607: lcmp
    //   1608: iflt +288 -> 1896
    //   1611: lload 15
    //   1613: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   1616: iload_1
    //   1617: iaload
    //   1618: i2l
    //   1619: lcmp
    //   1620: ifge +276 -> 1896
    //   1623: getstatic 214	com/android/server/am/OnePlusHighPowerDetector:DEBUG	Z
    //   1626: ifeq +68 -> 1694
    //   1629: ldc 121
    //   1631: new 353	java/lang/StringBuilder
    //   1634: dup
    //   1635: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1638: ldc_w 609
    //   1641: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1644: iload_1
    //   1645: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1648: ldc_w 611
    //   1651: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1654: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   1657: iload_1
    //   1658: iaload
    //   1659: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1662: ldc_w 613
    //   1665: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1668: lload 15
    //   1670: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1673: ldc_w 615
    //   1676: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1679: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   1682: iload_1
    //   1683: iaload
    //   1684: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1687: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1690: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1693: pop
    //   1694: iload_1
    //   1695: ifne +31 -> 1726
    //   1698: aload 32
    //   1700: monitorenter
    //   1701: aload 32
    //   1703: aload 33
    //   1705: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1708: getfield 616	android/content/pm/ApplicationInfo:uid	I
    //   1711: aload 33
    //   1713: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1716: lload 9
    //   1718: lload 13
    //   1720: invokevirtual 622	com/android/internal/os/BatteryStatsImpl:reportExcessiveCpuLocked	(ILjava/lang/String;JJ)V
    //   1723: aload 32
    //   1725: monitorexit
    //   1726: aload 33
    //   1728: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1731: ifnull +30 -> 1761
    //   1734: aload 33
    //   1736: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1739: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   1742: ifnull +19 -> 1761
    //   1745: aload 28
    //   1747: aload 33
    //   1749: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1752: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   1755: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   1758: ifeq +54 -> 1812
    //   1761: iload_1
    //   1762: ifne +29 -> 1791
    //   1765: aload 33
    //   1767: ifnull +24 -> 1791
    //   1770: aload 33
    //   1772: getfield 629	com/android/server/am/ProcessRecord:baseProcessTracker	Lcom/android/internal/app/procstats/ProcessState;
    //   1775: ifnull +16 -> 1791
    //   1778: aload 33
    //   1780: getfield 629	com/android/server/am/ProcessRecord:baseProcessTracker	Lcom/android/internal/app/procstats/ProcessState;
    //   1783: aload 33
    //   1785: getfield 633	com/android/server/am/ProcessRecord:pkgList	Landroid/util/ArrayMap;
    //   1788: invokevirtual 639	com/android/internal/app/procstats/ProcessState:reportExcessiveCpu	(Landroid/util/ArrayMap;)V
    //   1791: aload_0
    //   1792: aload 33
    //   1794: iload_1
    //   1795: invokevirtual 510	com/android/server/am/OnePlusHighPowerDetector:updateProcUsg	(Lcom/android/server/am/ProcessRecord;I)V
    //   1798: iload 5
    //   1800: istore_2
    //   1801: goto -1175 -> 626
    //   1804: astore 22
    //   1806: aload 32
    //   1808: monitorexit
    //   1809: aload 22
    //   1811: athrow
    //   1812: ldc 121
    //   1814: new 353	java/lang/StringBuilder
    //   1817: dup
    //   1818: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1821: ldc_w 641
    //   1824: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1827: aload 33
    //   1829: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1832: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1835: ldc_w 530
    //   1838: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1841: aload 33
    //   1843: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   1846: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1849: ldc_w 587
    //   1852: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1855: iload_1
    //   1856: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1859: ldc_w 589
    //   1862: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1865: lload 15
    //   1867: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1870: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1873: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1876: pop
    //   1877: aload 28
    //   1879: aload 33
    //   1881: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1884: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   1887: aload 33
    //   1889: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1892: pop
    //   1893: goto -132 -> 1761
    //   1896: aload 25
    //   1898: aload 33
    //   1900: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1903: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1906: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   1909: ifeq +150 -> 2059
    //   1912: aload 25
    //   1914: aload 33
    //   1916: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1919: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1922: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   1925: checkcast 557	java/lang/Integer
    //   1928: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1931: istore_2
    //   1932: lload 15
    //   1934: l2i
    //   1935: istore 6
    //   1937: aload 25
    //   1939: aload 33
    //   1941: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1944: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1947: iload_2
    //   1948: iload 6
    //   1950: iadd
    //   1951: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1954: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1957: pop
    //   1958: aload 24
    //   1960: aload 33
    //   1962: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1965: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1968: new 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   1971: dup
    //   1972: aload 33
    //   1974: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   1977: aload 33
    //   1979: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   1982: aload 33
    //   1984: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   1987: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   1990: invokespecial 644	com/android/server/am/OnePlusHighPowerDetector$AppInfo:<init>	(IILjava/lang/String;)V
    //   1993: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1996: pop
    //   1997: lload 15
    //   1999: lconst_0
    //   2000: lcmp
    //   2001: ifle -210 -> 1791
    //   2004: new 353	java/lang/StringBuilder
    //   2007: dup
    //   2008: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   2011: ldc_w 646
    //   2014: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2017: lload 15
    //   2019: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2022: ldc_w 648
    //   2025: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2028: aload 33
    //   2030: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   2033: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2036: ldc_w 650
    //   2039: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2042: aload 33
    //   2044: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2047: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2050: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2053: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   2056: goto -265 -> 1791
    //   2059: aload 25
    //   2061: aload 33
    //   2063: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2066: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2069: lload 15
    //   2071: l2i
    //   2072: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2075: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   2078: pop
    //   2079: goto -121 -> 1958
    //   2082: aload_0
    //   2083: aload 26
    //   2085: aload 33
    //   2087: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2090: new 353	java/lang/StringBuilder
    //   2093: dup
    //   2094: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   2097: ldc_w 525
    //   2100: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2103: aload 33
    //   2105: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   2108: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2111: ldc_w 530
    //   2114: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2117: aload 33
    //   2119: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   2122: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2125: ldc_w 652
    //   2128: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2131: aload 33
    //   2133: getfield 655	com/android/server/am/ProcessRecord:setProcState	I
    //   2136: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2139: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2142: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2145: iload 17
    //   2147: ifeq +66 -> 2213
    //   2150: aload_0
    //   2151: aload 23
    //   2153: aload 33
    //   2155: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2158: new 353	java/lang/StringBuilder
    //   2161: dup
    //   2162: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   2165: ldc_w 525
    //   2168: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2171: aload 33
    //   2173: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   2176: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2179: ldc_w 530
    //   2182: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2185: aload 33
    //   2187: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   2190: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2193: ldc_w 652
    //   2196: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2199: aload 33
    //   2201: getfield 655	com/android/server/am/ProcessRecord:setProcState	I
    //   2204: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2207: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2210: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2213: aload 27
    //   2215: aload 33
    //   2217: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2220: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2223: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   2226: ifeq +52 -> 2278
    //   2229: aload 27
    //   2231: aload 33
    //   2233: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2236: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2239: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2242: checkcast 557	java/lang/Integer
    //   2245: invokevirtual 571	java/lang/Integer:intValue	()I
    //   2248: istore_2
    //   2249: lload 15
    //   2251: l2i
    //   2252: istore 6
    //   2254: aload 27
    //   2256: aload 33
    //   2258: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2261: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2264: iload_2
    //   2265: iload 6
    //   2267: iadd
    //   2268: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2271: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   2274: pop
    //   2275: goto -484 -> 1791
    //   2278: aload 27
    //   2280: aload 33
    //   2282: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   2285: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2288: lload 15
    //   2290: l2i
    //   2291: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2294: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   2297: pop
    //   2298: goto -507 -> 1791
    //   2301: iload 18
    //   2303: ifeq +3656 -> 5959
    //   2306: aload 30
    //   2308: invokeinterface 656 1 0
    //   2313: ifle +82 -> 2395
    //   2316: aload 30
    //   2318: invokeinterface 662 1 0
    //   2323: astore 32
    //   2325: aload 32
    //   2327: invokeinterface 667 1 0
    //   2332: ifeq +63 -> 2395
    //   2335: aload 32
    //   2337: invokeinterface 671 1 0
    //   2342: checkcast 557	java/lang/Integer
    //   2345: astore 33
    //   2347: aload 29
    //   2349: aload 33
    //   2351: invokeinterface 674 2 0
    //   2356: ifne -31 -> 2325
    //   2359: aload_0
    //   2360: aload 26
    //   2362: aload 33
    //   2364: invokevirtual 571	java/lang/Integer:intValue	()I
    //   2367: ldc_w 676
    //   2370: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2373: iload 17
    //   2375: ifeq -50 -> 2325
    //   2378: aload_0
    //   2379: aload 23
    //   2381: aload 33
    //   2383: invokevirtual 571	java/lang/Integer:intValue	()I
    //   2386: ldc_w 676
    //   2389: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2392: goto -67 -> 2325
    //   2395: aload 31
    //   2397: ifnull +134 -> 2531
    //   2400: iconst_0
    //   2401: istore_2
    //   2402: aload 31
    //   2404: arraylength
    //   2405: istore 5
    //   2407: iload_2
    //   2408: iload 5
    //   2410: if_icmpge +121 -> 2531
    //   2413: aload 31
    //   2415: iload_2
    //   2416: aaload
    //   2417: astore 32
    //   2419: aload 32
    //   2421: ifnull +3825 -> 6246
    //   2424: aload 32
    //   2426: invokevirtual 679	java/lang/String:isEmpty	()Z
    //   2429: ifeq +6 -> 2435
    //   2432: goto +3814 -> 6246
    //   2435: aload 29
    //   2437: aload 32
    //   2439: invokeinterface 674 2 0
    //   2444: ifne +39 -> 2483
    //   2447: aload_0
    //   2448: aload 26
    //   2450: aload 32
    //   2452: invokestatic 683	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2455: ldc_w 685
    //   2458: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2461: iload 17
    //   2463: ifeq +3783 -> 6246
    //   2466: aload_0
    //   2467: aload 23
    //   2469: aload 32
    //   2471: invokestatic 683	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2474: ldc_w 685
    //   2477: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2480: goto +3766 -> 6246
    //   2483: aload 30
    //   2485: aload 32
    //   2487: invokeinterface 674 2 0
    //   2492: ifeq +3754 -> 6246
    //   2495: aload_0
    //   2496: aload 26
    //   2498: aload 32
    //   2500: invokestatic 683	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2503: ldc_w 687
    //   2506: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2509: iload 17
    //   2511: ifeq +3735 -> 6246
    //   2514: aload_0
    //   2515: aload 23
    //   2517: aload 32
    //   2519: invokestatic 683	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2522: ldc_w 687
    //   2525: invokevirtual 539	com/android/server/am/OnePlusHighPowerDetector:updateSkipMap	(Landroid/util/ArrayMap;ILjava/lang/String;)V
    //   2528: goto +3718 -> 6246
    //   2531: new 689	android/util/SparseArray
    //   2534: dup
    //   2535: invokespecial 690	android/util/SparseArray:<init>	()V
    //   2538: astore 29
    //   2540: aload_0
    //   2541: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   2544: invokevirtual 691	java/util/HashMap:size	()I
    //   2547: ifle +757 -> 3304
    //   2550: aload_0
    //   2551: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   2554: astore 30
    //   2556: aload 30
    //   2558: monitorenter
    //   2559: aload_0
    //   2560: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   2563: invokevirtual 695	java/util/HashMap:values	()Ljava/util/Collection;
    //   2566: invokeinterface 662 1 0
    //   2571: astore 31
    //   2573: aload 31
    //   2575: invokeinterface 667 1 0
    //   2580: ifeq +721 -> 3301
    //   2583: aload 31
    //   2585: invokeinterface 671 1 0
    //   2590: checkcast 10	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc
    //   2593: astore 32
    //   2595: aload_0
    //   2596: aload 32
    //   2598: getfield 696	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:flags	I
    //   2601: invokevirtual 502	com/android/server/am/OnePlusHighPowerDetector:hasSystemFlag	(I)Z
    //   2604: ifne -31 -> 2573
    //   2607: aload 32
    //   2609: getfield 697	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:curCpuTimeBgMonitor	J
    //   2612: aload 32
    //   2614: getfield 698	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:lastCpuTimeBgMonitor	[J
    //   2617: iload_1
    //   2618: laload
    //   2619: lsub
    //   2620: lstore 11
    //   2622: aload 26
    //   2624: aload 32
    //   2626: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2629: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2632: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   2635: ifne +26 -> 2661
    //   2638: aload 32
    //   2640: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   2643: ldc_w 704
    //   2646: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2649: ifne +12 -> 2661
    //   2652: aload_0
    //   2653: aload 32
    //   2655: invokevirtual 711	com/android/server/am/OnePlusHighPowerDetector:isForkedProcInWhiteList	(Lcom/android/server/am/OnePlusHighPowerDetector$AppForkedProc;)Z
    //   2658: ifeq +165 -> 2823
    //   2661: new 353	java/lang/StringBuilder
    //   2664: dup
    //   2665: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   2668: ldc_w 713
    //   2671: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2674: aload 32
    //   2676: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   2679: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2682: ldc_w 605
    //   2685: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2688: aload 32
    //   2690: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2693: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2696: ldc_w 607
    //   2699: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2702: aload 26
    //   2704: aload 32
    //   2706: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2709: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2712: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2715: checkcast 520	java/lang/String
    //   2718: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2721: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2724: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   2727: aload 32
    //   2729: getfield 698	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:lastCpuTimeBgMonitor	[J
    //   2732: iload_1
    //   2733: aload 32
    //   2735: getfield 697	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:curCpuTimeBgMonitor	J
    //   2738: lastore
    //   2739: aload 27
    //   2741: aload 32
    //   2743: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2746: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2749: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   2752: ifeq -179 -> 2573
    //   2755: ldc2_w 554
    //   2758: lload 11
    //   2760: lmul
    //   2761: lload 9
    //   2763: ldiv
    //   2764: lstore 11
    //   2766: aload 27
    //   2768: aload 32
    //   2770: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2773: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2776: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2779: checkcast 557	java/lang/Integer
    //   2782: invokevirtual 571	java/lang/Integer:intValue	()I
    //   2785: istore_2
    //   2786: lload 11
    //   2788: l2i
    //   2789: istore 5
    //   2791: aload 27
    //   2793: aload 32
    //   2795: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2798: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2801: iload_2
    //   2802: iload 5
    //   2804: iadd
    //   2805: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2808: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   2811: pop
    //   2812: goto -239 -> 2573
    //   2815: astore 22
    //   2817: aload 30
    //   2819: monitorexit
    //   2820: aload 22
    //   2822: athrow
    //   2823: aload 32
    //   2825: getfield 698	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:lastCpuTimeBgMonitor	[J
    //   2828: iload_1
    //   2829: laload
    //   2830: lconst_0
    //   2831: lcmp
    //   2832: ifle +191 -> 3023
    //   2835: ldc2_w 554
    //   2838: lload 11
    //   2840: lmul
    //   2841: lload 9
    //   2843: ldiv
    //   2844: lstore 13
    //   2846: lload 13
    //   2848: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   2851: iload_1
    //   2852: iaload
    //   2853: i2l
    //   2854: lcmp
    //   2855: iflt +183 -> 3038
    //   2858: lload 13
    //   2860: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   2863: iload_1
    //   2864: iaload
    //   2865: i2l
    //   2866: lcmp
    //   2867: ifge +171 -> 3038
    //   2870: aload 29
    //   2872: aload 32
    //   2874: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2877: aconst_null
    //   2878: invokevirtual 716	android/util/SparseArray:get	(ILjava/lang/Object;)Ljava/lang/Object;
    //   2881: ifnonnull +80 -> 2961
    //   2884: ldc 121
    //   2886: new 353	java/lang/StringBuilder
    //   2889: dup
    //   2890: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   2893: ldc_w 718
    //   2896: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2899: aload 32
    //   2901: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   2904: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2907: ldc_w 530
    //   2910: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2913: aload 32
    //   2915: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   2918: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2921: ldc_w 587
    //   2924: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2927: iload_1
    //   2928: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2931: ldc_w 589
    //   2934: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2937: lload 13
    //   2939: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2942: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2945: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   2948: pop
    //   2949: aload 29
    //   2951: aload 32
    //   2953: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2956: aload 32
    //   2958: invokevirtual 722	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   2961: aload 27
    //   2963: aload 32
    //   2965: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2968: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2971: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   2974: ifeq +49 -> 3023
    //   2977: aload 27
    //   2979: aload 32
    //   2981: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   2984: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2987: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2990: checkcast 557	java/lang/Integer
    //   2993: invokevirtual 571	java/lang/Integer:intValue	()I
    //   2996: istore_2
    //   2997: lload 13
    //   2999: l2i
    //   3000: istore 5
    //   3002: aload 27
    //   3004: aload 32
    //   3006: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3009: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3012: iload_2
    //   3013: iload 5
    //   3015: iadd
    //   3016: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3019: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3022: pop
    //   3023: aload 32
    //   3025: getfield 698	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:lastCpuTimeBgMonitor	[J
    //   3028: iload_1
    //   3029: aload 32
    //   3031: getfield 697	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:curCpuTimeBgMonitor	J
    //   3034: lastore
    //   3035: goto -462 -> 2573
    //   3038: ldc 121
    //   3040: new 353	java/lang/StringBuilder
    //   3043: dup
    //   3044: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3047: ldc_w 724
    //   3050: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3053: aload 32
    //   3055: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   3058: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3061: ldc_w 530
    //   3064: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3067: aload 32
    //   3069: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   3072: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3075: ldc_w 726
    //   3078: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3081: aload 32
    //   3083: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3086: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3089: ldc_w 728
    //   3092: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3095: lload 11
    //   3097: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3100: ldc_w 730
    //   3103: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3106: lload 9
    //   3108: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3111: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3114: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3117: pop
    //   3118: aload 25
    //   3120: aload 32
    //   3122: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3125: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3128: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   3131: ifeq +147 -> 3278
    //   3134: aload 25
    //   3136: aload 32
    //   3138: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3141: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3144: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3147: checkcast 557	java/lang/Integer
    //   3150: invokevirtual 571	java/lang/Integer:intValue	()I
    //   3153: istore_2
    //   3154: lload 13
    //   3156: l2i
    //   3157: istore 5
    //   3159: aload 25
    //   3161: aload 32
    //   3163: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3166: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3169: iload_2
    //   3170: iload 5
    //   3172: iadd
    //   3173: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3176: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3179: pop
    //   3180: aload 24
    //   3182: aload 32
    //   3184: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3187: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3190: new 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   3193: dup
    //   3194: aload 32
    //   3196: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3199: aload 32
    //   3201: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   3204: aload 32
    //   3206: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   3209: invokespecial 644	com/android/server/am/OnePlusHighPowerDetector$AppInfo:<init>	(IILjava/lang/String;)V
    //   3212: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3215: pop
    //   3216: lload 13
    //   3218: lconst_0
    //   3219: lcmp
    //   3220: ifle -259 -> 2961
    //   3223: new 353	java/lang/StringBuilder
    //   3226: dup
    //   3227: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3230: ldc_w 646
    //   3233: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3236: lload 13
    //   3238: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3241: ldc_w 648
    //   3244: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3247: aload 32
    //   3249: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   3252: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3255: ldc_w 650
    //   3258: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3261: aload 32
    //   3263: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3266: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3269: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3272: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   3275: goto -314 -> 2961
    //   3278: aload 25
    //   3280: aload 32
    //   3282: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   3285: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3288: lload 13
    //   3290: l2i
    //   3291: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3294: invokevirtual 575	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3297: pop
    //   3298: goto -118 -> 3180
    //   3301: aload 30
    //   3303: monitorexit
    //   3304: aload 28
    //   3306: invokevirtual 734	android/util/ArrayMap:size	()I
    //   3309: ifle +1008 -> 4317
    //   3312: aload 28
    //   3314: invokevirtual 735	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   3317: invokeinterface 662 1 0
    //   3322: astore 28
    //   3324: iload 4
    //   3326: istore_2
    //   3327: iload_2
    //   3328: istore_3
    //   3329: aload 28
    //   3331: invokeinterface 667 1 0
    //   3336: ifeq +981 -> 4317
    //   3339: aload 28
    //   3341: invokeinterface 671 1 0
    //   3346: checkcast 489	com/android/server/am/ProcessRecord
    //   3349: astore 30
    //   3351: aload 26
    //   3353: aload 30
    //   3355: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   3358: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3361: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   3364: ifeq +409 -> 3773
    //   3367: getstatic 267	com/android/server/am/OnePlusHighPowerDetector:mKillMechanism	Z
    //   3370: ifeq +331 -> 3701
    //   3373: iload_1
    //   3374: iconst_3
    //   3375: if_icmpne +326 -> 3701
    //   3378: aload_0
    //   3379: aload 30
    //   3381: invokevirtual 506	com/android/server/am/OnePlusHighPowerDetector:checkKillList	(Lcom/android/server/am/ProcessRecord;)Z
    //   3384: ifeq +317 -> 3701
    //   3387: iconst_0
    //   3388: istore 5
    //   3390: iconst_0
    //   3391: istore_3
    //   3392: iload 5
    //   3394: istore 4
    //   3396: aload 30
    //   3398: getfield 739	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   3401: ifnull +256 -> 3657
    //   3404: iload 5
    //   3406: istore 4
    //   3408: aload 30
    //   3410: getfield 739	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   3413: invokevirtual 742	android/util/ArraySet:size	()I
    //   3416: ifle +241 -> 3657
    //   3419: aload 30
    //   3421: getfield 739	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   3424: invokevirtual 743	android/util/ArraySet:iterator	()Ljava/util/Iterator;
    //   3427: astore 31
    //   3429: aload 31
    //   3431: invokeinterface 667 1 0
    //   3436: istore 18
    //   3438: iload_3
    //   3439: istore 4
    //   3441: iload 18
    //   3443: ifeq +214 -> 3657
    //   3446: aload 31
    //   3448: invokeinterface 671 1 0
    //   3453: checkcast 745	com/android/server/am/ServiceRecord
    //   3456: astore 32
    //   3458: aload 32
    //   3460: ifnull -31 -> 3429
    //   3463: aload 32
    //   3465: getfield 748	com/android/server/am/ServiceRecord:connections	Landroid/util/ArrayMap;
    //   3468: ifnull -39 -> 3429
    //   3471: iconst_0
    //   3472: istore 5
    //   3474: iload_3
    //   3475: istore 4
    //   3477: iload 4
    //   3479: istore_3
    //   3480: iload 5
    //   3482: aload 32
    //   3484: getfield 748	com/android/server/am/ServiceRecord:connections	Landroid/util/ArrayMap;
    //   3487: invokevirtual 734	android/util/ArrayMap:size	()I
    //   3490: if_icmpge -61 -> 3429
    //   3493: aload 32
    //   3495: getfield 748	com/android/server/am/ServiceRecord:connections	Landroid/util/ArrayMap;
    //   3498: iload 5
    //   3500: invokevirtual 751	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   3503: checkcast 306	java/util/ArrayList
    //   3506: astore 33
    //   3508: iconst_0
    //   3509: istore 6
    //   3511: iload 4
    //   3513: istore_3
    //   3514: iload 6
    //   3516: aload 33
    //   3518: invokevirtual 483	java/util/ArrayList:size	()I
    //   3521: if_icmpge +2732 -> 6253
    //   3524: aload 33
    //   3526: iload 6
    //   3528: invokevirtual 487	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3531: checkcast 753	com/android/server/am/ConnectionRecord
    //   3534: astore 34
    //   3536: aload 34
    //   3538: ifnull +2727 -> 6265
    //   3541: aload 34
    //   3543: getfield 757	com/android/server/am/ConnectionRecord:binding	Lcom/android/server/am/AppBindRecord;
    //   3546: ifnull +2719 -> 6265
    //   3549: aload 34
    //   3551: getfield 757	com/android/server/am/ConnectionRecord:binding	Lcom/android/server/am/AppBindRecord;
    //   3554: getfield 763	com/android/server/am/AppBindRecord:client	Lcom/android/server/am/ProcessRecord;
    //   3557: astore 34
    //   3559: aload 34
    //   3561: getfield 655	com/android/server/am/ProcessRecord:setProcState	I
    //   3564: iconst_2
    //   3565: if_icmpeq +12 -> 3577
    //   3568: aload 34
    //   3570: getfield 655	com/android/server/am/ProcessRecord:setProcState	I
    //   3573: iconst_1
    //   3574: if_icmpne +2691 -> 6265
    //   3577: iconst_1
    //   3578: istore_3
    //   3579: ldc 121
    //   3581: new 353	java/lang/StringBuilder
    //   3584: dup
    //   3585: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3588: ldc_w 765
    //   3591: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3594: aload 30
    //   3596: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   3599: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3602: ldc_w 767
    //   3605: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3608: aload 34
    //   3610: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   3613: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3616: ldc_w 769
    //   3619: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3622: aload 34
    //   3624: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3627: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3630: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3633: ldc_w 771
    //   3636: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3639: aload 34
    //   3641: getfield 655	com/android/server/am/ProcessRecord:setProcState	I
    //   3644: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3647: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3650: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3653: pop
    //   3654: goto +2599 -> 6253
    //   3657: iload 4
    //   3659: ifne +2623 -> 6282
    //   3662: aload 30
    //   3664: new 353	java/lang/StringBuilder
    //   3667: dup
    //   3668: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3671: ldc_w 773
    //   3674: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3677: aload 30
    //   3679: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   3682: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3685: ldc_w 775
    //   3688: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3691: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3694: iconst_1
    //   3695: invokevirtual 779	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   3698: goto +2584 -> 6282
    //   3701: ldc 121
    //   3703: new 353	java/lang/StringBuilder
    //   3706: dup
    //   3707: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3710: ldc_w 603
    //   3713: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3716: aload 30
    //   3718: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   3721: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3724: ldc_w 605
    //   3727: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3730: aload 30
    //   3732: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   3735: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3738: ldc_w 607
    //   3741: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3744: aload 26
    //   3746: aload 30
    //   3748: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   3751: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3754: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3757: checkcast 520	java/lang/String
    //   3760: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3763: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3766: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3769: pop
    //   3770: goto +2512 -> 6282
    //   3773: aload 25
    //   3775: aload 30
    //   3777: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   3780: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3783: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   3786: ifeq +17 -> 3803
    //   3789: aload 25
    //   3791: aload 30
    //   3793: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   3796: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3799: invokevirtual 782	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3802: pop
    //   3803: iload_1
    //   3804: ifne +281 -> 4085
    //   3807: aload 22
    //   3809: aload 30
    //   3811: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3814: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3817: invokeinterface 674 2 0
    //   3822: ifeq +121 -> 3943
    //   3825: ldc 121
    //   3827: new 353	java/lang/StringBuilder
    //   3830: dup
    //   3831: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3834: ldc_w 784
    //   3837: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3840: aload 30
    //   3842: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3845: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3848: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3851: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3854: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3857: pop
    //   3858: aload_0
    //   3859: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   3862: astore 31
    //   3864: aload 31
    //   3866: monitorenter
    //   3867: aload_0
    //   3868: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   3871: aload 30
    //   3873: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3876: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3879: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   3882: dup
    //   3883: aload 30
    //   3885: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3888: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3891: iconst_1
    //   3892: iconst_0
    //   3893: iconst_0
    //   3894: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   3897: aload 30
    //   3899: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   3902: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   3905: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3908: pop
    //   3909: aload 31
    //   3911: monitorexit
    //   3912: aload_0
    //   3913: aload 30
    //   3915: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   3918: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   3921: aload 30
    //   3923: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   3926: ldc_w 790
    //   3929: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   3932: goto +2357 -> 6289
    //   3935: astore 22
    //   3937: aload 31
    //   3939: monitorexit
    //   3940: aload 22
    //   3942: athrow
    //   3943: aload_0
    //   3944: aload 30
    //   3946: invokevirtual 506	com/android/server/am/OnePlusHighPowerDetector:checkKillList	(Lcom/android/server/am/ProcessRecord;)Z
    //   3949: ifeq +42 -> 3991
    //   3952: aload 30
    //   3954: new 353	java/lang/StringBuilder
    //   3957: dup
    //   3958: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   3961: ldc_w 773
    //   3964: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3967: aload 30
    //   3969: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   3972: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3975: ldc_w 775
    //   3978: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3981: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3984: iconst_1
    //   3985: invokevirtual 779	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   3988: goto +2301 -> 6289
    //   3991: ldc 121
    //   3993: new 353	java/lang/StringBuilder
    //   3996: dup
    //   3997: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4000: ldc_w 792
    //   4003: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4006: aload 30
    //   4008: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4011: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4014: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4017: ldc_w 794
    //   4020: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4023: aload 30
    //   4025: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   4028: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4031: ldc_w 587
    //   4034: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4037: iload_1
    //   4038: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4041: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4044: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   4047: pop
    //   4048: aload_0
    //   4049: aload 30
    //   4051: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4054: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4057: aload 30
    //   4059: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   4062: invokestatic 800	android/os/UserHandle:getUserId	(I)I
    //   4065: invokespecial 804	com/android/server/am/OnePlusHighPowerDetector:forceStopPackage	(Ljava/lang/String;I)V
    //   4068: aload 21
    //   4070: aload 30
    //   4072: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4075: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4078: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4081: pop
    //   4082: goto +2207 -> 6289
    //   4085: iload_1
    //   4086: iconst_1
    //   4087: if_icmpne +88 -> 4175
    //   4090: aload_0
    //   4091: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   4094: astore 31
    //   4096: aload 31
    //   4098: monitorenter
    //   4099: aload_0
    //   4100: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   4103: aload 30
    //   4105: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4108: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4111: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   4114: dup
    //   4115: aload 30
    //   4117: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4120: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4123: iconst_1
    //   4124: iconst_0
    //   4125: iconst_0
    //   4126: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   4129: aload 30
    //   4131: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   4134: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   4137: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4140: pop
    //   4141: aload 31
    //   4143: monitorexit
    //   4144: aload_0
    //   4145: aload 30
    //   4147: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4150: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4153: aload 30
    //   4155: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   4158: ldc_w 807
    //   4161: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   4164: goto -837 -> 3327
    //   4167: astore 22
    //   4169: aload 31
    //   4171: monitorexit
    //   4172: aload 22
    //   4174: athrow
    //   4175: iload_1
    //   4176: iconst_2
    //   4177: if_icmpne +68 -> 4245
    //   4180: aload_0
    //   4181: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   4184: astore 31
    //   4186: aload 31
    //   4188: monitorenter
    //   4189: aload_0
    //   4190: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   4193: aload 30
    //   4195: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4198: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4201: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   4204: dup
    //   4205: aload 30
    //   4207: getfield 493	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   4210: getfield 625	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4213: iconst_0
    //   4214: iconst_0
    //   4215: iconst_0
    //   4216: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   4219: aload 30
    //   4221: getfield 533	com/android/server/am/ProcessRecord:pid	I
    //   4224: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   4227: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4230: pop
    //   4231: aload 31
    //   4233: monitorexit
    //   4234: goto -907 -> 3327
    //   4237: astore 22
    //   4239: aload 31
    //   4241: monitorexit
    //   4242: aload 22
    //   4244: athrow
    //   4245: iload_1
    //   4246: iconst_3
    //   4247: if_icmpne -920 -> 3327
    //   4250: aload_0
    //   4251: aload 30
    //   4253: iload_1
    //   4254: invokevirtual 810	com/android/server/am/OnePlusHighPowerDetector:isProviderBoundByFG	(Lcom/android/server/am/ProcessRecord;I)Z
    //   4257: ifne -930 -> 3327
    //   4260: aload 30
    //   4262: new 353	java/lang/StringBuilder
    //   4265: dup
    //   4266: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4269: ldc_w 773
    //   4272: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4275: aload 30
    //   4277: getfield 528	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   4280: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4283: ldc_w 794
    //   4286: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4289: aload 30
    //   4291: getfield 523	com/android/server/am/ProcessRecord:uid	I
    //   4294: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4297: ldc_w 587
    //   4300: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4303: iload_1
    //   4304: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4307: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4310: iconst_1
    //   4311: invokevirtual 779	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   4314: goto -987 -> 3327
    //   4317: iload_3
    //   4318: istore_2
    //   4319: aload 29
    //   4321: invokevirtual 811	android/util/SparseArray:size	()I
    //   4324: ifle +492 -> 4816
    //   4327: iconst_0
    //   4328: istore 4
    //   4330: iload_3
    //   4331: istore_2
    //   4332: iload 4
    //   4334: aload 29
    //   4336: invokevirtual 811	android/util/SparseArray:size	()I
    //   4339: if_icmpge +477 -> 4816
    //   4342: aload 29
    //   4344: iload 4
    //   4346: invokevirtual 812	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   4349: checkcast 10	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc
    //   4352: astore 28
    //   4354: aload 28
    //   4356: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4359: ifnull +394 -> 4753
    //   4362: aload 25
    //   4364: aload 28
    //   4366: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   4369: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4372: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   4375: ifeq +17 -> 4392
    //   4378: aload 25
    //   4380: aload 28
    //   4382: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   4385: invokestatic 561	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4388: invokevirtual 782	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4391: pop
    //   4392: iload_1
    //   4393: ifne +209 -> 4602
    //   4396: aload 22
    //   4398: aload 28
    //   4400: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4403: invokeinterface 674 2 0
    //   4408: ifeq +109 -> 4517
    //   4411: ldc 121
    //   4413: new 353	java/lang/StringBuilder
    //   4416: dup
    //   4417: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4420: ldc_w 784
    //   4423: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4426: aload 28
    //   4428: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4431: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4434: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4437: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   4440: pop
    //   4441: aload_0
    //   4442: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   4445: astore 30
    //   4447: aload 30
    //   4449: monitorenter
    //   4450: aload_0
    //   4451: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   4454: aload 28
    //   4456: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4459: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   4462: dup
    //   4463: aload 28
    //   4465: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4468: iconst_1
    //   4469: iconst_0
    //   4470: iconst_0
    //   4471: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   4474: aload 28
    //   4476: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4479: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   4482: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4485: pop
    //   4486: aload 30
    //   4488: monitorexit
    //   4489: aload_0
    //   4490: aload 28
    //   4492: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4495: aload 28
    //   4497: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4500: ldc_w 814
    //   4503: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   4506: goto +1790 -> 6296
    //   4509: astore 22
    //   4511: aload 30
    //   4513: monitorexit
    //   4514: aload 22
    //   4516: athrow
    //   4517: ldc 121
    //   4519: new 353	java/lang/StringBuilder
    //   4522: dup
    //   4523: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4526: ldc_w 816
    //   4529: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4532: aload 28
    //   4534: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4537: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4540: ldc_w 794
    //   4543: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4546: aload 28
    //   4548: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   4551: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4554: ldc_w 587
    //   4557: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4560: iload_1
    //   4561: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4564: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4567: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   4570: pop
    //   4571: aload_0
    //   4572: aload 28
    //   4574: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4577: aload 28
    //   4579: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   4582: invokestatic 800	android/os/UserHandle:getUserId	(I)I
    //   4585: invokespecial 804	com/android/server/am/OnePlusHighPowerDetector:forceStopPackage	(Ljava/lang/String;I)V
    //   4588: aload 21
    //   4590: aload 28
    //   4592: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4595: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4598: pop
    //   4599: goto +1697 -> 6296
    //   4602: iload_1
    //   4603: iconst_1
    //   4604: if_icmpne +81 -> 4685
    //   4607: aload_0
    //   4608: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   4611: astore 30
    //   4613: aload 30
    //   4615: monitorenter
    //   4616: aload_0
    //   4617: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   4620: aload 28
    //   4622: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4625: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   4628: dup
    //   4629: aload 28
    //   4631: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4634: iconst_1
    //   4635: iconst_0
    //   4636: iconst_0
    //   4637: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   4640: aload 28
    //   4642: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4645: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   4648: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4651: pop
    //   4652: aload 30
    //   4654: monitorexit
    //   4655: aload_0
    //   4656: aload 28
    //   4658: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4661: aload 28
    //   4663: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4666: ldc_w 818
    //   4669: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   4672: iload_3
    //   4673: istore_2
    //   4674: goto +1626 -> 6300
    //   4677: astore 22
    //   4679: aload 30
    //   4681: monitorexit
    //   4682: aload 22
    //   4684: athrow
    //   4685: iload_3
    //   4686: istore_2
    //   4687: iload_1
    //   4688: iconst_2
    //   4689: if_icmpne +1611 -> 6300
    //   4692: aload_0
    //   4693: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   4696: astore 30
    //   4698: aload 30
    //   4700: monitorenter
    //   4701: aload_0
    //   4702: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   4705: aload 28
    //   4707: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4710: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   4713: dup
    //   4714: aload 28
    //   4716: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   4719: iconst_0
    //   4720: iconst_0
    //   4721: iconst_0
    //   4722: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   4725: aload 28
    //   4727: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4730: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   4733: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4736: pop
    //   4737: aload 30
    //   4739: monitorexit
    //   4740: iload_3
    //   4741: istore_2
    //   4742: goto +1558 -> 6300
    //   4745: astore 22
    //   4747: aload 30
    //   4749: monitorexit
    //   4750: aload 22
    //   4752: athrow
    //   4753: ldc 121
    //   4755: new 353	java/lang/StringBuilder
    //   4758: dup
    //   4759: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4762: ldc_w 724
    //   4765: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4768: aload 28
    //   4770: getfield 702	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:name	Ljava/lang/String;
    //   4773: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4776: ldc_w 820
    //   4779: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4782: aload 28
    //   4784: getfield 699	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:uid	I
    //   4787: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4790: ldc_w 822
    //   4793: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4796: aload 28
    //   4798: getfield 719	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pid	I
    //   4801: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4804: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4807: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   4810: pop
    //   4811: iload_3
    //   4812: istore_2
    //   4813: goto +1487 -> 6300
    //   4816: aload 25
    //   4818: invokevirtual 826	android/util/ArrayMap:entrySet	()Ljava/util/Set;
    //   4821: invokeinterface 662 1 0
    //   4826: astore 28
    //   4828: aload 28
    //   4830: invokeinterface 667 1 0
    //   4835: ifeq +640 -> 5475
    //   4838: aload 28
    //   4840: invokeinterface 671 1 0
    //   4845: checkcast 828	java/util/Map$Entry
    //   4848: astore 29
    //   4850: aload 29
    //   4852: invokeinterface 831 1 0
    //   4857: checkcast 557	java/lang/Integer
    //   4860: astore 30
    //   4862: aload 29
    //   4864: invokeinterface 834 1 0
    //   4869: checkcast 557	java/lang/Integer
    //   4872: astore 31
    //   4874: aload 24
    //   4876: aload 30
    //   4878: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4881: checkcast 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   4884: getfield 835	com/android/server/am/OnePlusHighPowerDetector$AppInfo:pkgName	Ljava/lang/String;
    //   4887: astore 29
    //   4889: aload 24
    //   4891: aload 30
    //   4893: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4896: checkcast 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   4899: getfield 836	com/android/server/am/OnePlusHighPowerDetector$AppInfo:pid	I
    //   4902: istore_3
    //   4903: aload 31
    //   4905: invokevirtual 571	java/lang/Integer:intValue	()I
    //   4908: ifle +49 -> 4957
    //   4911: new 353	java/lang/StringBuilder
    //   4914: dup
    //   4915: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   4918: ldc_w 838
    //   4921: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4924: aload 30
    //   4926: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4929: ldc_w 840
    //   4932: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4935: aload 29
    //   4937: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4940: ldc_w 589
    //   4943: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4946: aload 31
    //   4948: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4951: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4954: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   4957: aload 31
    //   4959: invokevirtual 571	java/lang/Integer:intValue	()I
    //   4962: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   4965: iload_1
    //   4966: iaload
    //   4967: if_icmplt -139 -> 4828
    //   4970: aload 31
    //   4972: invokevirtual 571	java/lang/Integer:intValue	()I
    //   4975: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   4978: iload_1
    //   4979: iaload
    //   4980: if_icmpge -152 -> 4828
    //   4983: aload 26
    //   4985: aload 30
    //   4987: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   4990: ifeq +63 -> 5053
    //   4993: ldc 121
    //   4995: new 353	java/lang/StringBuilder
    //   4998: dup
    //   4999: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5002: ldc_w 842
    //   5005: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5008: aload 29
    //   5010: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5013: ldc_w 605
    //   5016: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5019: aload 30
    //   5021: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5024: ldc_w 607
    //   5027: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5030: aload 26
    //   5032: aload 30
    //   5034: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5037: checkcast 520	java/lang/String
    //   5040: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5043: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5046: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5049: pop
    //   5050: goto -222 -> 4828
    //   5053: iload_1
    //   5054: ifne +185 -> 5239
    //   5057: aload 22
    //   5059: aload 29
    //   5061: invokeinterface 674 2 0
    //   5066: ifeq +89 -> 5155
    //   5069: ldc 121
    //   5071: new 353	java/lang/StringBuilder
    //   5074: dup
    //   5075: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5078: ldc_w 784
    //   5081: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5084: aload 29
    //   5086: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5089: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5092: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5095: pop
    //   5096: aload_0
    //   5097: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5100: astore 30
    //   5102: aload 30
    //   5104: monitorenter
    //   5105: aload_0
    //   5106: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5109: aload 29
    //   5111: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   5114: dup
    //   5115: aload 29
    //   5117: iconst_1
    //   5118: iconst_0
    //   5119: iconst_0
    //   5120: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   5123: iload_3
    //   5124: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   5127: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   5130: pop
    //   5131: aload 30
    //   5133: monitorexit
    //   5134: aload_0
    //   5135: aload 29
    //   5137: iload_3
    //   5138: ldc_w 844
    //   5141: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   5144: goto +1167 -> 6311
    //   5147: astore 22
    //   5149: aload 30
    //   5151: monitorexit
    //   5152: aload 22
    //   5154: athrow
    //   5155: ldc 121
    //   5157: new 353	java/lang/StringBuilder
    //   5160: dup
    //   5161: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5164: ldc_w 846
    //   5167: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5170: aload 31
    //   5172: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5175: ldc_w 848
    //   5178: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5181: aload 29
    //   5183: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5186: ldc_w 794
    //   5189: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5192: aload 30
    //   5194: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5197: ldc_w 587
    //   5200: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5203: iload_1
    //   5204: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   5207: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5210: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5213: pop
    //   5214: aload_0
    //   5215: aload 29
    //   5217: aload 30
    //   5219: invokevirtual 571	java/lang/Integer:intValue	()I
    //   5222: invokestatic 800	android/os/UserHandle:getUserId	(I)I
    //   5225: invokespecial 804	com/android/server/am/OnePlusHighPowerDetector:forceStopPackage	(Ljava/lang/String;I)V
    //   5228: aload 21
    //   5230: aload 29
    //   5232: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   5235: pop
    //   5236: goto +1075 -> 6311
    //   5239: iload_1
    //   5240: iconst_1
    //   5241: if_icmpne +121 -> 5362
    //   5244: ldc 121
    //   5246: new 353	java/lang/StringBuilder
    //   5249: dup
    //   5250: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5253: ldc_w 850
    //   5256: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5259: aload 31
    //   5261: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5264: ldc_w 852
    //   5267: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5270: aload 29
    //   5272: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5275: ldc_w 794
    //   5278: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5281: aload 30
    //   5283: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5286: ldc_w 587
    //   5289: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5292: iload_1
    //   5293: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   5296: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5299: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5302: pop
    //   5303: aload_0
    //   5304: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5307: astore 30
    //   5309: aload 30
    //   5311: monitorenter
    //   5312: aload_0
    //   5313: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5316: aload 29
    //   5318: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   5321: dup
    //   5322: aload 29
    //   5324: iconst_1
    //   5325: iconst_0
    //   5326: iconst_0
    //   5327: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   5330: iload_3
    //   5331: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   5334: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   5337: pop
    //   5338: aload 30
    //   5340: monitorexit
    //   5341: aload_0
    //   5342: aload 29
    //   5344: iload_3
    //   5345: ldc_w 854
    //   5348: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   5351: goto -523 -> 4828
    //   5354: astore 22
    //   5356: aload 30
    //   5358: monitorexit
    //   5359: aload 22
    //   5361: athrow
    //   5362: iload_1
    //   5363: iconst_2
    //   5364: if_icmpne -536 -> 4828
    //   5367: ldc 121
    //   5369: new 353	java/lang/StringBuilder
    //   5372: dup
    //   5373: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5376: ldc_w 856
    //   5379: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5382: aload 31
    //   5384: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5387: ldc_w 858
    //   5390: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5393: aload 29
    //   5395: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5398: ldc_w 794
    //   5401: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5404: aload 30
    //   5406: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5409: ldc_w 587
    //   5412: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5415: iload_1
    //   5416: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   5419: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5422: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5425: pop
    //   5426: aload_0
    //   5427: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   5430: astore 30
    //   5432: aload 30
    //   5434: monitorenter
    //   5435: aload_0
    //   5436: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   5439: aload 29
    //   5441: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   5444: dup
    //   5445: aload 29
    //   5447: iconst_0
    //   5448: iconst_0
    //   5449: iconst_0
    //   5450: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   5453: iload_3
    //   5454: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   5457: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   5460: pop
    //   5461: aload 30
    //   5463: monitorexit
    //   5464: goto -636 -> 4828
    //   5467: astore 22
    //   5469: aload 30
    //   5471: monitorexit
    //   5472: aload 22
    //   5474: athrow
    //   5475: iload 17
    //   5477: ifeq +573 -> 6050
    //   5480: iload_2
    //   5481: ifne +569 -> 6050
    //   5484: aload 27
    //   5486: invokevirtual 826	android/util/ArrayMap:entrySet	()Ljava/util/Set;
    //   5489: invokeinterface 662 1 0
    //   5494: astore 26
    //   5496: iload_2
    //   5497: istore_3
    //   5498: aload 26
    //   5500: invokeinterface 667 1 0
    //   5505: ifeq +94 -> 5599
    //   5508: aload 26
    //   5510: invokeinterface 671 1 0
    //   5515: checkcast 828	java/util/Map$Entry
    //   5518: astore 28
    //   5520: aload 28
    //   5522: invokeinterface 834 1 0
    //   5527: checkcast 557	java/lang/Integer
    //   5530: astore 27
    //   5532: aload 28
    //   5534: invokeinterface 831 1 0
    //   5539: checkcast 557	java/lang/Integer
    //   5542: astore 28
    //   5544: aload 27
    //   5546: invokevirtual 571	java/lang/Integer:intValue	()I
    //   5549: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   5552: iload_1
    //   5553: iaload
    //   5554: if_icmplt -58 -> 5496
    //   5557: iload_2
    //   5558: iconst_1
    //   5559: iadd
    //   5560: istore_3
    //   5561: ldc 121
    //   5563: new 353	java/lang/StringBuilder
    //   5566: dup
    //   5567: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5570: ldc_w 860
    //   5573: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5576: aload 28
    //   5578: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5581: ldc_w 862
    //   5584: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5587: aload 27
    //   5589: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5592: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5595: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5598: pop
    //   5599: iload_3
    //   5600: ifne +450 -> 6050
    //   5603: ldc_w 864
    //   5606: invokestatic 329	com/android/server/am/OnePlusHighPowerDetector:myLog	(Ljava/lang/String;)V
    //   5609: aload 25
    //   5611: invokevirtual 826	android/util/ArrayMap:entrySet	()Ljava/util/Set;
    //   5614: invokeinterface 662 1 0
    //   5619: astore 25
    //   5621: aload 25
    //   5623: invokeinterface 667 1 0
    //   5628: ifeq +422 -> 6050
    //   5631: aload 25
    //   5633: invokeinterface 671 1 0
    //   5638: checkcast 828	java/util/Map$Entry
    //   5641: astore 27
    //   5643: aload 27
    //   5645: invokeinterface 831 1 0
    //   5650: checkcast 557	java/lang/Integer
    //   5653: astore 26
    //   5655: aload 27
    //   5657: invokeinterface 834 1 0
    //   5662: checkcast 557	java/lang/Integer
    //   5665: astore 28
    //   5667: aload 24
    //   5669: aload 26
    //   5671: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5674: checkcast 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   5677: getfield 835	com/android/server/am/OnePlusHighPowerDetector$AppInfo:pkgName	Ljava/lang/String;
    //   5680: astore 27
    //   5682: aload 24
    //   5684: aload 26
    //   5686: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5689: checkcast 13	com/android/server/am/OnePlusHighPowerDetector$AppInfo
    //   5692: getfield 836	com/android/server/am/OnePlusHighPowerDetector$AppInfo:pid	I
    //   5695: istore_2
    //   5696: aload 28
    //   5698: invokevirtual 571	java/lang/Integer:intValue	()I
    //   5701: getstatic 250	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_USG_THOLD	I
    //   5704: if_icmplt -83 -> 5621
    //   5707: aload 23
    //   5709: aload 26
    //   5711: invokevirtual 565	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   5714: ifeq +63 -> 5777
    //   5717: ldc 121
    //   5719: new 353	java/lang/StringBuilder
    //   5722: dup
    //   5723: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5726: ldc_w 866
    //   5729: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5732: aload 27
    //   5734: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5737: ldc_w 605
    //   5740: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5743: aload 26
    //   5745: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5748: ldc_w 607
    //   5751: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5754: aload 23
    //   5756: aload 26
    //   5758: invokevirtual 568	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5761: checkcast 520	java/lang/String
    //   5764: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5767: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5770: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5773: pop
    //   5774: goto -153 -> 5621
    //   5777: aload 22
    //   5779: aload 27
    //   5781: invokeinterface 674 2 0
    //   5786: ifeq +89 -> 5875
    //   5789: ldc 121
    //   5791: new 353	java/lang/StringBuilder
    //   5794: dup
    //   5795: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5798: ldc_w 868
    //   5801: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5804: aload 27
    //   5806: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5809: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5812: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5815: pop
    //   5816: aload_0
    //   5817: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5820: astore 26
    //   5822: aload 26
    //   5824: monitorenter
    //   5825: aload_0
    //   5826: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   5829: aload 27
    //   5831: new 19	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp
    //   5834: dup
    //   5835: aload 27
    //   5837: iconst_1
    //   5838: iconst_0
    //   5839: iconst_0
    //   5840: invokestatic 401	android/os/SystemClock:elapsedRealtime	()J
    //   5843: iload_2
    //   5844: invokespecial 787	com/android/server/am/OnePlusHighPowerDetector$ExtendHighPowerApp:<init>	(Ljava/lang/String;IZZJI)V
    //   5847: invokevirtual 788	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   5850: pop
    //   5851: aload 26
    //   5853: monitorexit
    //   5854: aload_0
    //   5855: aload 27
    //   5857: iload_2
    //   5858: ldc_w 844
    //   5861: invokespecial 191	com/android/server/am/OnePlusHighPowerDetector:notifyBgDetectIfNecessary	(Ljava/lang/String;ILjava/lang/String;)V
    //   5864: goto -243 -> 5621
    //   5867: astore 22
    //   5869: aload 26
    //   5871: monitorexit
    //   5872: aload 22
    //   5874: athrow
    //   5875: ldc 121
    //   5877: new 353	java/lang/StringBuilder
    //   5880: dup
    //   5881: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   5884: ldc_w 870
    //   5887: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5890: aload 28
    //   5892: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5895: ldc_w 848
    //   5898: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5901: aload 27
    //   5903: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5906: ldc_w 794
    //   5909: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5912: aload 26
    //   5914: invokevirtual 518	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5917: ldc_w 587
    //   5920: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5923: iload_1
    //   5924: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   5927: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5930: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   5933: pop
    //   5934: aload_0
    //   5935: aload 27
    //   5937: aload 26
    //   5939: invokevirtual 571	java/lang/Integer:intValue	()I
    //   5942: invokestatic 800	android/os/UserHandle:getUserId	(I)I
    //   5945: invokespecial 804	com/android/server/am/OnePlusHighPowerDetector:forceStopPackage	(Ljava/lang/String;I)V
    //   5948: aload 21
    //   5950: aload 27
    //   5952: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   5955: pop
    //   5956: goto -335 -> 5621
    //   5959: aload_0
    //   5960: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   5963: invokevirtual 691	java/util/HashMap:size	()I
    //   5966: ifle +84 -> 6050
    //   5969: aload_0
    //   5970: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   5973: astore 22
    //   5975: aload 22
    //   5977: monitorenter
    //   5978: aload_0
    //   5979: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   5982: invokevirtual 695	java/util/HashMap:values	()Ljava/util/Collection;
    //   5985: invokeinterface 662 1 0
    //   5990: astore 23
    //   5992: aload 23
    //   5994: invokeinterface 667 1 0
    //   5999: ifeq +48 -> 6047
    //   6002: aload 23
    //   6004: invokeinterface 671 1 0
    //   6009: checkcast 10	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc
    //   6012: astore 24
    //   6014: aload 24
    //   6016: getfield 697	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:curCpuTimeBgMonitor	J
    //   6019: lconst_0
    //   6020: lcmp
    //   6021: ifle -29 -> 5992
    //   6024: aload 24
    //   6026: getfield 698	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:lastCpuTimeBgMonitor	[J
    //   6029: iload_1
    //   6030: aload 24
    //   6032: getfield 697	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:curCpuTimeBgMonitor	J
    //   6035: lastore
    //   6036: goto -44 -> 5992
    //   6039: astore 23
    //   6041: aload 22
    //   6043: monitorexit
    //   6044: aload 23
    //   6046: athrow
    //   6047: aload 22
    //   6049: monitorexit
    //   6050: aload_0
    //   6051: invokevirtual 873	com/android/server/am/OnePlusHighPowerDetector:cancelBgDetectNotificationIfNeeded	()V
    //   6054: goto -5327 -> 727
    //   6057: new 353	java/lang/StringBuilder
    //   6060: dup
    //   6061: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   6064: aload 20
    //   6066: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6069: ldc_w 875
    //   6072: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6075: aload 21
    //   6077: iload_2
    //   6078: invokevirtual 487	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6081: checkcast 520	java/lang/String
    //   6084: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6087: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   6090: astore 20
    //   6092: goto -5315 -> 777
    //   6095: new 877	com/android/server/SystemEvent
    //   6098: dup
    //   6099: ldc_w 879
    //   6102: invokespecial 881	com/android/server/SystemEvent:<init>	(Ljava/lang/String;)V
    //   6105: astore 21
    //   6107: aload 21
    //   6109: ldc_w 882
    //   6112: aload 20
    //   6114: invokevirtual 883	com/android/server/SystemEvent:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   6117: pop
    //   6118: aload_0
    //   6119: getfield 344	com/android/server/am/OnePlusHighPowerDetector:mHighPowerEventCollector	Lcom/android/server/SystemEventCollector;
    //   6122: aload 21
    //   6124: invokevirtual 887	com/android/server/SystemEventCollector:submit	(Lcom/android/server/SystemEvent;)V
    //   6127: iload 17
    //   6129: ifeq +19 -> 6148
    //   6132: getstatic 259	com/android/server/am/OnePlusHighPowerDetector:sBattUpdteLock	Ljava/lang/Object;
    //   6135: astore 20
    //   6137: aload 20
    //   6139: monitorenter
    //   6140: aload_0
    //   6141: iconst_0
    //   6142: putfield 284	com/android/server/am/OnePlusHighPowerDetector:mIsPowerDrain	Z
    //   6145: aload 20
    //   6147: monitorexit
    //   6148: getstatic 214	com/android/server/am/OnePlusHighPowerDetector:DEBUG	Z
    //   6151: ifeq +50 -> 6201
    //   6154: ldc 121
    //   6156: new 353	java/lang/StringBuilder
    //   6159: dup
    //   6160: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   6163: ldc_w 889
    //   6166: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6169: iload_1
    //   6170: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   6173: ldc_w 891
    //   6176: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6179: invokestatic 378	android/os/SystemClock:uptimeMillis	()J
    //   6182: lload 7
    //   6184: lsub
    //   6185: invokevirtual 410	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   6188: ldc_w 893
    //   6191: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6194: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   6197: invokestatic 372	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   6200: pop
    //   6201: iconst_0
    //   6202: ireturn
    //   6203: astore 20
    //   6205: ldc 121
    //   6207: new 353	java/lang/StringBuilder
    //   6210: dup
    //   6211: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   6214: ldc_w 895
    //   6217: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6220: aload 20
    //   6222: invokevirtual 898	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   6225: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6228: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   6231: invokestatic 324	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   6234: pop
    //   6235: goto -108 -> 6127
    //   6238: astore 21
    //   6240: aload 20
    //   6242: monitorexit
    //   6243: aload 21
    //   6245: athrow
    //   6246: iload_2
    //   6247: iconst_1
    //   6248: iadd
    //   6249: istore_2
    //   6250: goto -3843 -> 2407
    //   6253: iload 5
    //   6255: iconst_1
    //   6256: iadd
    //   6257: istore 5
    //   6259: iload_3
    //   6260: istore 4
    //   6262: goto -2785 -> 3477
    //   6265: iload 6
    //   6267: iconst_1
    //   6268: iadd
    //   6269: istore 6
    //   6271: goto -2760 -> 3511
    //   6274: astore 31
    //   6276: iload_3
    //   6277: istore 4
    //   6279: goto -2622 -> 3657
    //   6282: iload_2
    //   6283: iconst_1
    //   6284: iadd
    //   6285: istore_2
    //   6286: goto -2959 -> 3327
    //   6289: iload_2
    //   6290: iconst_1
    //   6291: iadd
    //   6292: istore_2
    //   6293: goto -2966 -> 3327
    //   6296: iload_3
    //   6297: iconst_1
    //   6298: iadd
    //   6299: istore_2
    //   6300: iload 4
    //   6302: iconst_1
    //   6303: iadd
    //   6304: istore 4
    //   6306: iload_2
    //   6307: istore_3
    //   6308: goto -1978 -> 4330
    //   6311: iload_2
    //   6312: iconst_1
    //   6313: iadd
    //   6314: istore_2
    //   6315: goto -1487 -> 4828
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	6318	0	this	OnePlusHighPowerDetector
    //   0	6318	1	paramInt1	int
    //   0	6318	2	paramInt2	int
    //   57	6251	3	i	int
    //   59	6246	4	j	int
    //   633	5625	5	k	int
    //   1123	5147	6	m	int
    //   45	6138	7	l1	long
    //   143	2964	9	l2	long
    //   132	2964	11	l3	long
    //   148	3141	13	l4	long
    //   1212	1077	15	l5	long
    //   51	6077	17	bool1	boolean
    //   48	3394	18	bool2	boolean
    //   54	317	19	bool3	boolean
    //   6203	38	20	localException1	Exception
    //   77	173	21	localObject2	Object
    //   301	466	21	localObject3	Object
    //   784	6	21	localObject4	Object
    //   792	6	21	localObject5	Object
    //   800	9	21	localObject6	Object
    //   811	6	21	localObject7	Object
    //   819	5257	21	localObject8	Object
    //   6105	18	21	localSystemEvent	com.android.server.SystemEvent
    //   6238	6	21	localObject9	Object
    //   344	1	22	localList	List
    //   693	23	22	localException2	Exception
    //   1804	6	22	localObject10	Object
    //   2815	993	22	localObject11	Object
    //   3935	6	22	localObject12	Object
    //   4167	6	22	localObject13	Object
    //   4237	160	22	localObject14	Object
    //   4509	6	22	localObject15	Object
    //   4677	6	22	localObject16	Object
    //   4745	313	22	localObject17	Object
    //   5147	6	22	localObject18	Object
    //   5354	6	22	localObject19	Object
    //   5467	311	22	localObject20	Object
    //   5867	6	22	localObject21	Object
    //   516	5487	23	localObject22	Object
    //   6039	6	23	localObject23	Object
    //   543	5488	24	localObject24	Object
    //   525	5107	25	localObject25	Object
    //   534	5417	27	localObject27	Object
    //   498	5393	28	localObject28	Object
    //   68	5378	29	localObject29	Object
    //   6274	1	31	localException3	Exception
    //   102	3392	32	localObject32	Object
    //   553	2972	33	localObject33	Object
    //   992	2648	34	localObject34	Object
    // Exception table:
    //   from	to	target	type
    //   93	129	301	finally
    //   129	165	301	finally
    //   173	264	301	finally
    //   278	298	301	finally
    //   616	626	693	java/lang/Exception
    //   635	675	693	java/lang/Exception
    //   680	687	693	java/lang/Exception
    //   827	882	693	java/lang/Exception
    //   890	945	693	java/lang/Exception
    //   951	994	693	java/lang/Exception
    //   999	1030	693	java/lang/Exception
    //   1030	1039	693	java/lang/Exception
    //   1042	1120	693	java/lang/Exception
    //   1125	1146	693	java/lang/Exception
    //   1152	1172	693	java/lang/Exception
    //   1178	1203	693	java/lang/Exception
    //   1203	1214	693	java/lang/Exception
    //   1223	1295	693	java/lang/Exception
    //   1295	1332	693	java/lang/Exception
    //   1332	1341	693	java/lang/Exception
    //   1346	1377	693	java/lang/Exception
    //   1377	1505	693	java/lang/Exception
    //   1505	1541	693	java/lang/Exception
    //   1546	1567	693	java/lang/Exception
    //   1573	1593	693	java/lang/Exception
    //   1599	1694	693	java/lang/Exception
    //   1698	1701	693	java/lang/Exception
    //   1723	1726	693	java/lang/Exception
    //   1726	1761	693	java/lang/Exception
    //   1770	1791	693	java/lang/Exception
    //   1791	1798	693	java/lang/Exception
    //   1806	1812	693	java/lang/Exception
    //   1812	1893	693	java/lang/Exception
    //   1896	1932	693	java/lang/Exception
    //   1937	1958	693	java/lang/Exception
    //   1958	1997	693	java/lang/Exception
    //   2004	2056	693	java/lang/Exception
    //   2059	2079	693	java/lang/Exception
    //   2082	2145	693	java/lang/Exception
    //   2150	2213	693	java/lang/Exception
    //   2213	2249	693	java/lang/Exception
    //   2254	2275	693	java/lang/Exception
    //   2278	2298	693	java/lang/Exception
    //   2306	2325	693	java/lang/Exception
    //   2325	2373	693	java/lang/Exception
    //   2378	2392	693	java/lang/Exception
    //   2402	2407	693	java/lang/Exception
    //   2424	2432	693	java/lang/Exception
    //   2435	2461	693	java/lang/Exception
    //   2466	2480	693	java/lang/Exception
    //   2483	2509	693	java/lang/Exception
    //   2514	2528	693	java/lang/Exception
    //   2531	2559	693	java/lang/Exception
    //   2817	2823	693	java/lang/Exception
    //   3301	3304	693	java/lang/Exception
    //   3304	3324	693	java/lang/Exception
    //   3329	3373	693	java/lang/Exception
    //   3378	3387	693	java/lang/Exception
    //   3396	3404	693	java/lang/Exception
    //   3408	3429	693	java/lang/Exception
    //   3429	3438	693	java/lang/Exception
    //   3463	3471	693	java/lang/Exception
    //   3480	3508	693	java/lang/Exception
    //   3514	3536	693	java/lang/Exception
    //   3541	3577	693	java/lang/Exception
    //   3579	3654	693	java/lang/Exception
    //   3662	3698	693	java/lang/Exception
    //   3701	3770	693	java/lang/Exception
    //   3773	3803	693	java/lang/Exception
    //   3807	3867	693	java/lang/Exception
    //   3909	3932	693	java/lang/Exception
    //   3937	3943	693	java/lang/Exception
    //   3943	3988	693	java/lang/Exception
    //   3991	4082	693	java/lang/Exception
    //   4090	4099	693	java/lang/Exception
    //   4141	4164	693	java/lang/Exception
    //   4169	4175	693	java/lang/Exception
    //   4180	4189	693	java/lang/Exception
    //   4231	4234	693	java/lang/Exception
    //   4239	4245	693	java/lang/Exception
    //   4250	4314	693	java/lang/Exception
    //   4319	4327	693	java/lang/Exception
    //   4332	4392	693	java/lang/Exception
    //   4396	4450	693	java/lang/Exception
    //   4486	4506	693	java/lang/Exception
    //   4511	4517	693	java/lang/Exception
    //   4517	4599	693	java/lang/Exception
    //   4607	4616	693	java/lang/Exception
    //   4652	4672	693	java/lang/Exception
    //   4679	4685	693	java/lang/Exception
    //   4692	4701	693	java/lang/Exception
    //   4737	4740	693	java/lang/Exception
    //   4747	4753	693	java/lang/Exception
    //   4753	4811	693	java/lang/Exception
    //   4816	4828	693	java/lang/Exception
    //   4828	4957	693	java/lang/Exception
    //   4957	5050	693	java/lang/Exception
    //   5057	5105	693	java/lang/Exception
    //   5131	5144	693	java/lang/Exception
    //   5149	5155	693	java/lang/Exception
    //   5155	5236	693	java/lang/Exception
    //   5244	5312	693	java/lang/Exception
    //   5338	5351	693	java/lang/Exception
    //   5356	5362	693	java/lang/Exception
    //   5367	5435	693	java/lang/Exception
    //   5461	5464	693	java/lang/Exception
    //   5469	5475	693	java/lang/Exception
    //   5484	5496	693	java/lang/Exception
    //   5498	5557	693	java/lang/Exception
    //   5561	5599	693	java/lang/Exception
    //   5603	5621	693	java/lang/Exception
    //   5621	5774	693	java/lang/Exception
    //   5777	5825	693	java/lang/Exception
    //   5851	5864	693	java/lang/Exception
    //   5869	5875	693	java/lang/Exception
    //   5875	5956	693	java/lang/Exception
    //   5959	5978	693	java/lang/Exception
    //   6041	6047	693	java/lang/Exception
    //   6047	6050	693	java/lang/Exception
    //   6050	6054	693	java/lang/Exception
    //   358	370	784	finally
    //   374	381	784	finally
    //   381	407	784	finally
    //   558	565	792	finally
    //   421	545	800	finally
    //   549	558	800	finally
    //   565	568	800	finally
    //   573	582	800	finally
    //   589	592	800	finally
    //   597	606	800	finally
    //   613	616	800	finally
    //   616	626	800	finally
    //   635	675	800	finally
    //   680	687	800	finally
    //   695	727	800	finally
    //   794	800	800	finally
    //   813	819	800	finally
    //   821	827	800	finally
    //   827	882	800	finally
    //   890	945	800	finally
    //   951	994	800	finally
    //   999	1030	800	finally
    //   1030	1039	800	finally
    //   1042	1120	800	finally
    //   1125	1146	800	finally
    //   1152	1172	800	finally
    //   1178	1203	800	finally
    //   1203	1214	800	finally
    //   1223	1295	800	finally
    //   1295	1332	800	finally
    //   1332	1341	800	finally
    //   1346	1377	800	finally
    //   1377	1505	800	finally
    //   1505	1541	800	finally
    //   1546	1567	800	finally
    //   1573	1593	800	finally
    //   1599	1694	800	finally
    //   1698	1701	800	finally
    //   1723	1726	800	finally
    //   1726	1761	800	finally
    //   1770	1791	800	finally
    //   1791	1798	800	finally
    //   1806	1812	800	finally
    //   1812	1893	800	finally
    //   1896	1932	800	finally
    //   1937	1958	800	finally
    //   1958	1997	800	finally
    //   2004	2056	800	finally
    //   2059	2079	800	finally
    //   2082	2145	800	finally
    //   2150	2213	800	finally
    //   2213	2249	800	finally
    //   2254	2275	800	finally
    //   2278	2298	800	finally
    //   2306	2325	800	finally
    //   2325	2373	800	finally
    //   2378	2392	800	finally
    //   2402	2407	800	finally
    //   2424	2432	800	finally
    //   2435	2461	800	finally
    //   2466	2480	800	finally
    //   2483	2509	800	finally
    //   2514	2528	800	finally
    //   2531	2559	800	finally
    //   2817	2823	800	finally
    //   3301	3304	800	finally
    //   3304	3324	800	finally
    //   3329	3373	800	finally
    //   3378	3387	800	finally
    //   3396	3404	800	finally
    //   3408	3429	800	finally
    //   3429	3438	800	finally
    //   3446	3458	800	finally
    //   3463	3471	800	finally
    //   3480	3508	800	finally
    //   3514	3536	800	finally
    //   3541	3577	800	finally
    //   3579	3654	800	finally
    //   3662	3698	800	finally
    //   3701	3770	800	finally
    //   3773	3803	800	finally
    //   3807	3867	800	finally
    //   3909	3932	800	finally
    //   3937	3943	800	finally
    //   3943	3988	800	finally
    //   3991	4082	800	finally
    //   4090	4099	800	finally
    //   4141	4164	800	finally
    //   4169	4175	800	finally
    //   4180	4189	800	finally
    //   4231	4234	800	finally
    //   4239	4245	800	finally
    //   4250	4314	800	finally
    //   4319	4327	800	finally
    //   4332	4392	800	finally
    //   4396	4450	800	finally
    //   4486	4506	800	finally
    //   4511	4517	800	finally
    //   4517	4599	800	finally
    //   4607	4616	800	finally
    //   4652	4672	800	finally
    //   4679	4685	800	finally
    //   4692	4701	800	finally
    //   4737	4740	800	finally
    //   4747	4753	800	finally
    //   4753	4811	800	finally
    //   4816	4828	800	finally
    //   4828	4957	800	finally
    //   4957	5050	800	finally
    //   5057	5105	800	finally
    //   5131	5144	800	finally
    //   5149	5155	800	finally
    //   5155	5236	800	finally
    //   5244	5312	800	finally
    //   5338	5351	800	finally
    //   5356	5362	800	finally
    //   5367	5435	800	finally
    //   5461	5464	800	finally
    //   5469	5475	800	finally
    //   5484	5496	800	finally
    //   5498	5557	800	finally
    //   5561	5599	800	finally
    //   5603	5621	800	finally
    //   5621	5774	800	finally
    //   5777	5825	800	finally
    //   5851	5864	800	finally
    //   5869	5875	800	finally
    //   5875	5956	800	finally
    //   5959	5978	800	finally
    //   6041	6047	800	finally
    //   6047	6050	800	finally
    //   6050	6054	800	finally
    //   582	589	811	finally
    //   606	613	819	finally
    //   1701	1723	1804	finally
    //   2559	2573	2815	finally
    //   2573	2661	2815	finally
    //   2661	2786	2815	finally
    //   2791	2812	2815	finally
    //   2823	2961	2815	finally
    //   2961	2997	2815	finally
    //   3002	3023	2815	finally
    //   3023	3035	2815	finally
    //   3038	3154	2815	finally
    //   3159	3180	2815	finally
    //   3180	3216	2815	finally
    //   3223	3275	2815	finally
    //   3278	3298	2815	finally
    //   3867	3909	3935	finally
    //   4099	4141	4167	finally
    //   4189	4231	4237	finally
    //   4450	4486	4509	finally
    //   4616	4652	4677	finally
    //   4701	4737	4745	finally
    //   5105	5131	5147	finally
    //   5312	5338	5354	finally
    //   5435	5461	5467	finally
    //   5825	5851	5867	finally
    //   5978	5992	6039	finally
    //   5992	6036	6039	finally
    //   738	746	6203	java/lang/Exception
    //   753	762	6203	java/lang/Exception
    //   766	777	6203	java/lang/Exception
    //   6057	6092	6203	java/lang/Exception
    //   6095	6127	6203	java/lang/Exception
    //   6140	6145	6238	finally
    //   3446	3458	6274	java/lang/Exception
  }
  
  private void cleanUpWhenPkgRemoved(String paramString)
  {
    Slog.i("OHPD", "[BgDetect] pkg " + paramString + " removed ");
    synchronized (this.mHugePowerPkgMap)
    {
      if (this.mHugePowerPkgMap.containsKey(paramString)) {
        this.mHugePowerPkgMap.remove(paramString);
      }
      synchronized (this.mHighPowerPkgMap)
      {
        if (this.mHighPowerPkgMap.containsKey(paramString)) {
          this.mHighPowerPkgMap.remove(paramString);
        }
      }
    }
    synchronized (this.mMediumPowerPkgMap)
    {
      if (this.mMediumPowerPkgMap.containsKey(paramString)) {
        this.mMediumPowerPkgMap.remove(paramString);
      }
      return;
      paramString = finally;
      throw paramString;
      paramString = finally;
      throw paramString;
    }
  }
  
  private void forceStopPackage(String paramString, int paramInt)
  {
    mAms.forceStopPackage(paramString, paramInt);
    if (OnePlusAppBootManager.IN_USING)
    {
      OnePlusAppBootManager.getInstance(null).updateAppStopInfo(paramString);
      OnePlusAppBootManager.getInstance(null).updatePowerFlag(paramString, 32768);
    }
    postProcessOfForceStop(paramString);
  }
  
  private String[] getActiveAudioUids()
  {
    if (this.mAudioManager == null) {
      this.mAudioManager = ((AudioManager)mContext.getSystemService("audio"));
    }
    if (this.mAudioManager != null) {}
    for (String str = this.mAudioManager.getParameters("get_uid");; str = ":0") {
      return parseActiveAudioUidsStr(str);
    }
  }
  
  private int getLastFGTimeThold(boolean paramBoolean)
  {
    if (paramBoolean) {
      return PD_LAST_FG_TIME_THOLD;
    }
    return APP_LAST_FOREGROUND_TIME_THRESHOLD;
  }
  
  private void init()
  {
    this.whiteAppListSet = new ArrayList(Arrays.asList(mContext.getResources().getStringArray(84344834)));
    this.blackAppListSet = new ArrayList(Arrays.asList(mContext.getResources().getStringArray(84344835)));
    this.blackExAppListSet = new ArrayList(Arrays.asList(mContext.getResources().getStringArray(84344837)));
    this.killProcList = new ArrayList(Arrays.asList(mContext.getResources().getStringArray(84344836)));
    mRegion = Build.REGION;
    mGlobalFlags = SystemProperties.getInt("persist.sys.ohpd.flags", 0);
    mKillMechanism = SystemProperties.getBoolean("persist.sys.ohpd.kcheck", false);
    updateCpuThreshold(null);
    registerReceiver();
    initOnlineConfig();
    if (DEBUG)
    {
      POWER_DRAIN_USG_THOLD = SystemProperties.getInt("persist.sys.ohpd.pd.usg.thold", POWER_DRAIN_USG_THOLD);
      POWER_DRAIN_TEMP_THOLD = SystemProperties.getInt("persist.sys.ohpd.pd.temp.thold", POWER_DRAIN_TEMP_THOLD);
      PD_LAST_FG_TIME_THOLD = SystemProperties.getInt("persist.sys.ohpd.pd.lastfg", PD_LAST_FG_TIME_THOLD);
    }
  }
  
  public static void myLog(String paramString)
  {
    if (DEBUG) {
      Slog.d("OHPD", paramString);
    }
  }
  
  private void notifyBgDetectIfNecessary(String paramString1, int paramInt, String paramString2)
  {
    try
    {
      if (DEBUG) {
        Slog.i("OHPD", "notifyBgDetectIfNecessary # " + paramString1 + ", pid=" + paramInt + ", reason=" + paramString2);
      }
      paramString2 = mHandler.obtainMessage(55003);
      paramString2.obj = paramString1;
      paramString2.arg1 = paramInt;
      mHandler.sendMessageDelayed(paramString2, 1000L);
      return;
    }
    catch (Exception paramString2)
    {
      Slog.e("OHPD", "Error when notifyBgDetectIfNecessary " + paramString1);
      paramString2.printStackTrace();
    }
  }
  
  private String[] parseActiveAudioUidsStr(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    if (!paramString.contains(":")) {
      return null;
    }
    return paramString.split(":");
  }
  
  private void registerReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context arg1, Intent paramAnonymousIntent)
      {
        if (!OpFeatures.isSupport(new int[] { 14 })) {
          return;
        }
        int i = paramAnonymousIntent.getIntExtra("level", -1);
        int j = paramAnonymousIntent.getIntExtra("scale", -1);
        if (j == 0)
        {
          Slog.e("OHPD", "[BgDetect] batt scale is 0");
          return;
        }
        i = i * 100 / j;
        if ((i > 100) || (i < 0)) {
          return;
        }
        synchronized (OnePlusHighPowerDetector.sBattUpdteLock)
        {
          if ((OnePlusHighPowerDetector.this.mLastBatteryPercent > 0) && (OnePlusHighPowerDetector.this.mLastBatteryPercent - i >= 1))
          {
            long l = SystemClock.uptimeMillis();
            if ((OnePlusHighPowerDetector.this.mLastBatteryDropTime > 0L) && (l - OnePlusHighPowerDetector.this.mLastBatteryDropTime <= OnePlusHighPowerDetector.CPU_CHECK_DELAY[0]))
            {
              Slog.i("OHPD", "[BgDetect] batt level (" + OnePlusHighPowerDetector.this.mLastBatteryPercent + " to " + i + ") within " + (l - OnePlusHighPowerDetector.this.mLastBatteryDropTime));
              OnePlusHighPowerDetector.this.mIsPowerDrain = true;
            }
            OnePlusHighPowerDetector.this.mLastBatteryDropTime = l;
          }
          OnePlusHighPowerDetector.this.mLastBatteryPercent = i;
          return;
        }
      }
    }, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.setPriority(Integer.MAX_VALUE);
    if (!mRegion.equalsIgnoreCase("CN")) {
      if (((mGlobalFlags & 0x1) == 0) && (!responseSIMStateChanged())) {
        localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
      }
    }
    for (;;)
    {
      if (DEBUG) {
        localIntentFilter.addAction("ohpd.action.test");
      }
      localIntentFilter.addAction("com.oem.intent.action.force_stop_pkg");
      mContext.registerReceiver(this.mGeneralReceiver, localIntentFilter);
      return;
      setKillMechanismState(true);
    }
  }
  
  /* Error */
  private void resolveBackgroundDetectionConfigFromJSON(JSONArray paramJSONArray)
  {
    // Byte code:
    //   0: getstatic 230	com/android/server/am/OnePlusHighPowerDetector:ONLINE_CONFIG	Z
    //   3: ifne +4 -> 7
    //   6: return
    //   7: aload_1
    //   8: ifnonnull +4 -> 12
    //   11: return
    //   12: getstatic 257	com/android/server/am/OnePlusHighPowerDetector:sConfigLock	Ljava/lang/Object;
    //   15: astore 4
    //   17: aload 4
    //   19: monitorenter
    //   20: iconst_0
    //   21: istore_2
    //   22: iload_2
    //   23: aload_1
    //   24: invokevirtual 1059	org/json/JSONArray:length	()I
    //   27: if_icmpge +1146 -> 1173
    //   30: aload_1
    //   31: iload_2
    //   32: invokevirtual 1063	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   35: astore 5
    //   37: aload 5
    //   39: ldc_w 882
    //   42: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   45: ldc_w 1070
    //   48: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   51: ifeq +64 -> 115
    //   54: aload 5
    //   56: ldc_w 1072
    //   59: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   62: astore 7
    //   64: aload_0
    //   65: getfield 973	com/android/server/am/OnePlusHighPowerDetector:whiteAppListSet	Ljava/util/ArrayList;
    //   68: astore 6
    //   70: aload 6
    //   72: monitorenter
    //   73: aload_0
    //   74: getfield 973	com/android/server/am/OnePlusHighPowerDetector:whiteAppListSet	Ljava/util/ArrayList;
    //   77: invokevirtual 1077	java/util/ArrayList:clear	()V
    //   80: iconst_0
    //   81: istore_3
    //   82: iload_3
    //   83: aload 7
    //   85: invokevirtual 1059	org/json/JSONArray:length	()I
    //   88: if_icmpge +24 -> 112
    //   91: aload_0
    //   92: getfield 973	com/android/server/am/OnePlusHighPowerDetector:whiteAppListSet	Ljava/util/ArrayList;
    //   95: aload 7
    //   97: iload_3
    //   98: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   101: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   104: pop
    //   105: iload_3
    //   106: iconst_1
    //   107: iadd
    //   108: istore_3
    //   109: goto -27 -> 82
    //   112: aload 6
    //   114: monitorexit
    //   115: aload 5
    //   117: ldc_w 882
    //   120: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   123: ldc_w 1082
    //   126: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   129: ifeq +107 -> 236
    //   132: aload 5
    //   134: ldc_w 1072
    //   137: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   140: astore 7
    //   142: aload_0
    //   143: getfield 976	com/android/server/am/OnePlusHighPowerDetector:blackAppListSet	Ljava/util/ArrayList;
    //   146: astore 6
    //   148: aload 6
    //   150: monitorenter
    //   151: aload_0
    //   152: getfield 976	com/android/server/am/OnePlusHighPowerDetector:blackAppListSet	Ljava/util/ArrayList;
    //   155: invokevirtual 1077	java/util/ArrayList:clear	()V
    //   158: iconst_0
    //   159: istore_3
    //   160: iload_3
    //   161: aload 7
    //   163: invokevirtual 1059	org/json/JSONArray:length	()I
    //   166: if_icmpge +67 -> 233
    //   169: aload_0
    //   170: getfield 976	com/android/server/am/OnePlusHighPowerDetector:blackAppListSet	Ljava/util/ArrayList;
    //   173: aload 7
    //   175: iload_3
    //   176: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   179: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   182: pop
    //   183: iload_3
    //   184: iconst_1
    //   185: iadd
    //   186: istore_3
    //   187: goto -27 -> 160
    //   190: astore_1
    //   191: aload 6
    //   193: monitorexit
    //   194: aload_1
    //   195: athrow
    //   196: astore_1
    //   197: aload 4
    //   199: monitorexit
    //   200: aload_1
    //   201: athrow
    //   202: astore_1
    //   203: ldc 121
    //   205: new 353	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 1084
    //   215: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_1
    //   219: invokevirtual 1085	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   222: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   228: invokestatic 324	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   231: pop
    //   232: return
    //   233: aload 6
    //   235: monitorexit
    //   236: aload 5
    //   238: ldc_w 882
    //   241: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   244: ldc_w 1087
    //   247: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   250: ifeq +70 -> 320
    //   253: aload 5
    //   255: ldc_w 1072
    //   258: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   261: astore 7
    //   263: aload_0
    //   264: getfield 979	com/android/server/am/OnePlusHighPowerDetector:blackExAppListSet	Ljava/util/ArrayList;
    //   267: astore 6
    //   269: aload 6
    //   271: monitorenter
    //   272: aload_0
    //   273: getfield 979	com/android/server/am/OnePlusHighPowerDetector:blackExAppListSet	Ljava/util/ArrayList;
    //   276: invokevirtual 1077	java/util/ArrayList:clear	()V
    //   279: iconst_0
    //   280: istore_3
    //   281: iload_3
    //   282: aload 7
    //   284: invokevirtual 1059	org/json/JSONArray:length	()I
    //   287: if_icmpge +30 -> 317
    //   290: aload_0
    //   291: getfield 979	com/android/server/am/OnePlusHighPowerDetector:blackExAppListSet	Ljava/util/ArrayList;
    //   294: aload 7
    //   296: iload_3
    //   297: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   300: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   303: pop
    //   304: iload_3
    //   305: iconst_1
    //   306: iadd
    //   307: istore_3
    //   308: goto -27 -> 281
    //   311: astore_1
    //   312: aload 6
    //   314: monitorexit
    //   315: aload_1
    //   316: athrow
    //   317: aload 6
    //   319: monitorexit
    //   320: aload 5
    //   322: ldc_w 882
    //   325: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   328: ldc_w 1089
    //   331: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   334: ifeq +70 -> 404
    //   337: aload 5
    //   339: ldc_w 1072
    //   342: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   345: astore 7
    //   347: aload_0
    //   348: getfield 982	com/android/server/am/OnePlusHighPowerDetector:killProcList	Ljava/util/ArrayList;
    //   351: astore 6
    //   353: aload 6
    //   355: monitorenter
    //   356: aload_0
    //   357: getfield 982	com/android/server/am/OnePlusHighPowerDetector:killProcList	Ljava/util/ArrayList;
    //   360: invokevirtual 1077	java/util/ArrayList:clear	()V
    //   363: iconst_0
    //   364: istore_3
    //   365: iload_3
    //   366: aload 7
    //   368: invokevirtual 1059	org/json/JSONArray:length	()I
    //   371: if_icmpge +30 -> 401
    //   374: aload_0
    //   375: getfield 982	com/android/server/am/OnePlusHighPowerDetector:killProcList	Ljava/util/ArrayList;
    //   378: aload 7
    //   380: iload_3
    //   381: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   384: invokevirtual 805	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   387: pop
    //   388: iload_3
    //   389: iconst_1
    //   390: iadd
    //   391: istore_3
    //   392: goto -27 -> 365
    //   395: astore_1
    //   396: aload 6
    //   398: monitorexit
    //   399: aload_1
    //   400: athrow
    //   401: aload 6
    //   403: monitorexit
    //   404: aload 5
    //   406: ldc_w 882
    //   409: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   412: ldc_w 1091
    //   415: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   418: ifeq +90 -> 508
    //   421: aload 5
    //   423: ldc_w 1072
    //   426: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   429: astore 6
    //   431: iconst_0
    //   432: istore_3
    //   433: iload_3
    //   434: iconst_4
    //   435: if_icmpge +73 -> 508
    //   438: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   441: iload_3
    //   442: aload 6
    //   444: iload_3
    //   445: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   448: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   451: invokevirtual 571	java/lang/Integer:intValue	()I
    //   454: iastore
    //   455: ldc 121
    //   457: new 353	java/lang/StringBuilder
    //   460: dup
    //   461: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   464: ldc_w 1096
    //   467: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   470: iload_3
    //   471: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   474: ldc_w 1098
    //   477: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   480: getstatic 245	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MIN	[I
    //   483: iload_3
    //   484: iaload
    //   485: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   488: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   491: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   494: pop
    //   495: iload_3
    //   496: iconst_1
    //   497: iadd
    //   498: istore_3
    //   499: goto -66 -> 433
    //   502: astore_1
    //   503: aload 6
    //   505: monitorexit
    //   506: aload_1
    //   507: athrow
    //   508: aload 5
    //   510: ldc_w 882
    //   513: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   516: ldc_w 1103
    //   519: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   522: ifeq +84 -> 606
    //   525: aload 5
    //   527: ldc_w 1072
    //   530: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   533: astore 6
    //   535: iconst_0
    //   536: istore_3
    //   537: iload_3
    //   538: iconst_4
    //   539: if_icmpge +67 -> 606
    //   542: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   545: iload_3
    //   546: aload 6
    //   548: iload_3
    //   549: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   552: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   555: invokevirtual 571	java/lang/Integer:intValue	()I
    //   558: iastore
    //   559: ldc 121
    //   561: new 353	java/lang/StringBuilder
    //   564: dup
    //   565: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   568: ldc_w 1105
    //   571: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   574: iload_3
    //   575: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   578: ldc_w 1098
    //   581: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: getstatic 243	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_CPU_USAGE_THRESHOLD_MAX	[I
    //   587: iload_3
    //   588: iaload
    //   589: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   592: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   595: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   598: pop
    //   599: iload_3
    //   600: iconst_1
    //   601: iadd
    //   602: istore_3
    //   603: goto -66 -> 537
    //   606: aload 5
    //   608: ldc_w 882
    //   611: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   614: ldc_w 1107
    //   617: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   620: ifeq +52 -> 672
    //   623: aload 5
    //   625: ldc_w 1072
    //   628: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   631: iconst_0
    //   632: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   635: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   638: invokevirtual 571	java/lang/Integer:intValue	()I
    //   641: putstatic 248	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_NETWORK_USAGE_THRESHOLD	I
    //   644: ldc 121
    //   646: new 353	java/lang/StringBuilder
    //   649: dup
    //   650: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   653: ldc_w 1109
    //   656: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   659: getstatic 248	com/android/server/am/OnePlusHighPowerDetector:BG_DETECTION_NETWORK_USAGE_THRESHOLD	I
    //   662: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   665: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   668: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   671: pop
    //   672: aload 5
    //   674: ldc_w 882
    //   677: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   680: ldc_w 1111
    //   683: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   686: ifeq +84 -> 770
    //   689: aload 5
    //   691: ldc_w 1072
    //   694: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   697: astore 6
    //   699: iconst_0
    //   700: istore_3
    //   701: iload_3
    //   702: iconst_4
    //   703: if_icmpge +67 -> 770
    //   706: getstatic 239	com/android/server/am/OnePlusHighPowerDetector:CPU_CHECK_DELAY	[I
    //   709: iload_3
    //   710: aload 6
    //   712: iload_3
    //   713: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   716: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   719: invokevirtual 571	java/lang/Integer:intValue	()I
    //   722: iastore
    //   723: ldc 121
    //   725: new 353	java/lang/StringBuilder
    //   728: dup
    //   729: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   732: ldc_w 1113
    //   735: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   738: iload_3
    //   739: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   742: ldc_w 1098
    //   745: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   748: getstatic 239	com/android/server/am/OnePlusHighPowerDetector:CPU_CHECK_DELAY	[I
    //   751: iload_3
    //   752: iaload
    //   753: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   756: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   759: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   762: pop
    //   763: iload_3
    //   764: iconst_1
    //   765: iadd
    //   766: istore_3
    //   767: goto -66 -> 701
    //   770: aload 5
    //   772: ldc_w 882
    //   775: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   778: ldc_w 1115
    //   781: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   784: ifeq +52 -> 836
    //   787: aload 5
    //   789: ldc_w 1072
    //   792: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   795: iconst_0
    //   796: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   799: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   802: invokevirtual 571	java/lang/Integer:intValue	()I
    //   805: putstatic 1118	com/android/server/am/ActivityManagerService:CPU_MIN_CHECK_DURATION	I
    //   808: ldc 121
    //   810: new 353	java/lang/StringBuilder
    //   813: dup
    //   814: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   817: ldc_w 1120
    //   820: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   823: getstatic 1118	com/android/server/am/ActivityManagerService:CPU_MIN_CHECK_DURATION	I
    //   826: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   829: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   832: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   835: pop
    //   836: aload 5
    //   838: ldc_w 882
    //   841: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   844: ldc_w 1122
    //   847: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   850: ifeq +52 -> 902
    //   853: aload 5
    //   855: ldc_w 1072
    //   858: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   861: iconst_0
    //   862: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   865: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   868: invokevirtual 571	java/lang/Integer:intValue	()I
    //   871: putstatic 232	com/android/server/am/OnePlusHighPowerDetector:APP_LAST_FOREGROUND_TIME_THRESHOLD	I
    //   874: ldc 121
    //   876: new 353	java/lang/StringBuilder
    //   879: dup
    //   880: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   883: ldc_w 1124
    //   886: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   889: getstatic 232	com/android/server/am/OnePlusHighPowerDetector:APP_LAST_FOREGROUND_TIME_THRESHOLD	I
    //   892: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   895: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   898: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   901: pop
    //   902: aload 5
    //   904: ldc_w 882
    //   907: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   910: ldc_w 1126
    //   913: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   916: ifeq +52 -> 968
    //   919: aload 5
    //   921: ldc_w 1072
    //   924: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   927: iconst_0
    //   928: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   931: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   934: invokevirtual 571	java/lang/Integer:intValue	()I
    //   937: putstatic 237	com/android/server/am/OnePlusHighPowerDetector:APP_LAST_CONTACT_PROVIDER_TIME_THRESHOLD	I
    //   940: ldc 121
    //   942: new 353	java/lang/StringBuilder
    //   945: dup
    //   946: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   949: ldc_w 1128
    //   952: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   955: getstatic 237	com/android/server/am/OnePlusHighPowerDetector:APP_LAST_CONTACT_PROVIDER_TIME_THRESHOLD	I
    //   958: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   961: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   964: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   967: pop
    //   968: aload 5
    //   970: ldc_w 882
    //   973: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   976: ldc_w 1130
    //   979: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   982: ifeq +52 -> 1034
    //   985: aload 5
    //   987: ldc_w 1072
    //   990: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   993: iconst_0
    //   994: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   997: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   1000: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1003: putstatic 250	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_USG_THOLD	I
    //   1006: ldc 121
    //   1008: new 353	java/lang/StringBuilder
    //   1011: dup
    //   1012: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1015: ldc_w 1132
    //   1018: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1021: getstatic 250	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_USG_THOLD	I
    //   1024: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1027: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1030: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1033: pop
    //   1034: aload 5
    //   1036: ldc_w 882
    //   1039: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1042: ldc_w 1134
    //   1045: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1048: ifeq +52 -> 1100
    //   1051: aload 5
    //   1053: ldc_w 1072
    //   1056: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   1059: iconst_0
    //   1060: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   1063: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   1066: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1069: putstatic 252	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_TEMP_THOLD	I
    //   1072: ldc 121
    //   1074: new 353	java/lang/StringBuilder
    //   1077: dup
    //   1078: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1081: ldc_w 1136
    //   1084: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1087: getstatic 252	com/android/server/am/OnePlusHighPowerDetector:POWER_DRAIN_TEMP_THOLD	I
    //   1090: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1093: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1096: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1099: pop
    //   1100: aload 5
    //   1102: ldc_w 882
    //   1105: invokevirtual 1068	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1108: ldc_w 1138
    //   1111: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1114: ifeq +52 -> 1166
    //   1117: aload 5
    //   1119: ldc_w 1072
    //   1122: invokevirtual 1076	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   1125: iconst_0
    //   1126: invokevirtual 1080	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   1129: invokestatic 1094	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   1132: invokevirtual 571	java/lang/Integer:intValue	()I
    //   1135: putstatic 235	com/android/server/am/OnePlusHighPowerDetector:PD_LAST_FG_TIME_THOLD	I
    //   1138: ldc 121
    //   1140: new 353	java/lang/StringBuilder
    //   1143: dup
    //   1144: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1147: ldc_w 1140
    //   1150: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1153: getstatic 235	com/android/server/am/OnePlusHighPowerDetector:PD_LAST_FG_TIME_THOLD	I
    //   1156: invokevirtual 363	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1159: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1162: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1165: pop
    //   1166: iload_2
    //   1167: iconst_1
    //   1168: iadd
    //   1169: istore_2
    //   1170: goto -1148 -> 22
    //   1173: aload 4
    //   1175: monitorexit
    //   1176: ldc 121
    //   1178: ldc_w 1142
    //   1181: invokestatic 1101	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1184: pop
    //   1185: return
    //   1186: astore_1
    //   1187: ldc 121
    //   1189: new 353	java/lang/StringBuilder
    //   1192: dup
    //   1193: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   1196: ldc_w 1084
    //   1199: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1202: aload_1
    //   1203: invokevirtual 898	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   1206: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1209: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1212: invokestatic 324	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1215: pop
    //   1216: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1217	0	this	OnePlusHighPowerDetector
    //   0	1217	1	paramJSONArray	JSONArray
    //   21	1149	2	i	int
    //   81	686	3	j	int
    //   15	1159	4	localObject1	Object
    //   35	1083	5	localJSONObject	org.json.JSONObject
    //   62	317	7	localJSONArray	JSONArray
    // Exception table:
    //   from	to	target	type
    //   73	80	190	finally
    //   82	105	190	finally
    //   22	73	196	finally
    //   112	115	196	finally
    //   115	151	196	finally
    //   191	196	196	finally
    //   233	236	196	finally
    //   236	272	196	finally
    //   312	317	196	finally
    //   317	320	196	finally
    //   320	356	196	finally
    //   396	401	196	finally
    //   401	404	196	finally
    //   404	431	196	finally
    //   438	495	196	finally
    //   503	508	196	finally
    //   508	535	196	finally
    //   542	599	196	finally
    //   606	672	196	finally
    //   672	699	196	finally
    //   706	763	196	finally
    //   770	836	196	finally
    //   836	902	196	finally
    //   902	968	196	finally
    //   968	1034	196	finally
    //   1034	1100	196	finally
    //   1100	1166	196	finally
    //   12	20	202	org/json/JSONException
    //   197	202	202	org/json/JSONException
    //   1173	1185	202	org/json/JSONException
    //   151	158	311	finally
    //   160	183	311	finally
    //   272	279	395	finally
    //   281	304	395	finally
    //   356	363	502	finally
    //   365	388	502	finally
    //   12	20	1186	java/lang/Exception
    //   197	202	1186	java/lang/Exception
    //   1173	1185	1186	java/lang/Exception
  }
  
  private boolean responseSIMStateChanged()
  {
    if ((mGlobalFlags & 0x1) != 0) {
      return true;
    }
    boolean bool = false;
    String str = SystemProperties.get("gsm.sim.operator.iso-country", "");
    if (str.length() >= 2)
    {
      if ((!str.contains("in")) && (!str.contains("cn"))) {
        break label94;
      }
      setKillMechanismState(true);
    }
    for (;;)
    {
      bool = true;
      Slog.i("OHPD", "[BgDetect] responseSIMStateChanged # mccCountry=" + str + ", ret=" + true);
      return bool;
      label94:
      setKillMechanismState(false);
    }
  }
  
  private void scheduleForceStopPkg(String paramString)
  {
    try
    {
      Slog.i("OHPD", "[BgDetect] scheduleForceStopPkg # " + paramString);
      Message localMessage = mHandler.obtainMessage(55008);
      localMessage.obj = paramString;
      mHandler.sendMessage(localMessage);
      return;
    }
    catch (Exception localException)
    {
      Slog.e("OHPD", "Error when scheduleForceStopPkg " + paramString);
      localException.printStackTrace();
    }
  }
  
  private void setKillMechanismState(boolean paramBoolean)
  {
    mKillMechanism = paramBoolean;
    if (paramBoolean) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("persist.sys.ohpd.kcheck", str);
      Slog.i("OHPD", "[BgDetect] mKillMechanism " + mKillMechanism);
      if ((mGlobalFlags & 0x1) == 0)
      {
        mGlobalFlags |= 0x1;
        SystemProperties.set("persist.sys.ohpd.flags", mGlobalFlags + "");
      }
      return;
    }
  }
  
  private boolean skipCheck(ProcessRecord paramProcessRecord, long paramLong, int paramInt, StringBuffer paramStringBuffer, boolean paramBoolean)
  {
    if (isProcInBlackExList(paramProcessRecord)) {
      return false;
    }
    if (isProcInWhiteList(paramProcessRecord))
    {
      paramStringBuffer.append("white list pkg " + paramProcessRecord.info.packageName);
      return true;
    }
    if ((checkKillList(paramProcessRecord)) || (isProcInBlackList(paramProcessRecord))) {}
    while ((!checkKillList(paramProcessRecord)) && ((paramProcessRecord.processName.startsWith("com.android")) || (paramProcessRecord.processName.startsWith("com.google")) || (paramProcessRecord.processName.startsWith("android.process"))))
    {
      paramStringBuffer.append("important proc");
      return true;
      if ((paramInt <= 2) && (paramProcessRecord.curAdj <= 400) && (paramProcessRecord.curAdj != 200))
      {
        paramStringBuffer.append("small adj " + paramProcessRecord.curAdj);
        return true;
      }
    }
    if ((paramProcessRecord.lastFgTime != 0L) && (paramLong - paramProcessRecord.lastFgTime < getLastFGTimeThold(paramBoolean)))
    {
      paramStringBuffer.append("app " + paramProcessRecord.processName + "(" + paramProcessRecord.pid + ") due to just switch to bg " + (paramLong - paramProcessRecord.lastFgTime) + " ms ago , thold " + getLastFGTimeThold(paramBoolean));
      return true;
    }
    if ((paramProcessRecord.lastContactProviderTime != 0L) && (paramLong - paramProcessRecord.lastContactProviderTime < APP_LAST_CONTACT_PROVIDER_TIME_THRESHOLD))
    {
      paramStringBuffer.append("app " + paramProcessRecord.processName + "(" + paramProcessRecord.pid + ") due to just access contacts provider " + (paramLong - paramProcessRecord.lastContactProviderTime) + " ms ago");
      return true;
    }
    paramLong = TrafficStats.getUidRxBytes(paramProcessRecord.uid) - paramProcessRecord.lastRxBytes[paramInt];
    if (paramLong > BG_DETECTION_NETWORK_USAGE_THRESHOLD)
    {
      paramStringBuffer.append("app " + paramProcessRecord.processName + "(" + paramProcessRecord.pid + ") due to net rx usage is " + paramLong + " bytes");
      return true;
    }
    paramLong = TrafficStats.getUidTxBytes(paramProcessRecord.uid) - paramProcessRecord.lastTxBytes[paramInt];
    if (paramLong > BG_DETECTION_NETWORK_USAGE_THRESHOLD)
    {
      paramStringBuffer.append("app " + paramProcessRecord.processName + "(" + paramProcessRecord.pid + ") due to net tx usage is " + paramLong + " bytes");
      return true;
    }
    return false;
  }
  
  private void stopBgPowerHungryApp(String paramString, int paramInt, boolean paramBoolean)
  {
    myLog("stopBgPowerHungryApp +# pkg=" + paramString + ", powerLevel=" + paramInt + ", remove=" + paramBoolean);
    long l = SystemClock.uptimeMillis();
    if (UserHandle.getAppId(Binder.getCallingUid()) == 1000) {}
    try
    {
      forceStopPackage(paramString, -2);
      Slog.i("OHPD", "[BgDetect]- stopBgPowerHungryApp : pkg " + paramString + " level " + paramInt + " in " + (SystemClock.uptimeMillis() - l) + "ms");
      return;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public static void updateCpuThreshold(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = SystemProperties.get("persist.sys.ohpd.threshold", null);
    }
    myLog("updateCpuThreshold # thresh=" + paramString + ", threshold=" + str);
    if (str != null)
    {
      paramString = str.split(",");
      if (paramString.length != 4) {}
    }
    synchronized (sConfigLock)
    {
      try
      {
        BG_DETECTION_CPU_USAGE_THRESHOLD_MAX[0] = 2097152;
        BG_DETECTION_CPU_USAGE_THRESHOLD_MIN[0] = Integer.parseInt(paramString[0]);
        int i = 1;
        while (i < 4)
        {
          BG_DETECTION_CPU_USAGE_THRESHOLD_MIN[i] = Integer.parseInt(paramString[i]);
          BG_DETECTION_CPU_USAGE_THRESHOLD_MAX[i] = BG_DETECTION_CPU_USAGE_THRESHOLD_MIN[(i - 1)];
          i += 1;
        }
        return;
      }
      catch (NumberFormatException paramString)
      {
        Slog.e("OHPD", "updateCpuThreshold # NumberFormatException : threshold=" + str);
        if (DEBUG)
        {
          str = "";
          paramString = "";
          i = 0;
          while (i < 4)
          {
            str = str + "," + BG_DETECTION_CPU_USAGE_THRESHOLD_MIN[i];
            paramString = paramString + "," + BG_DETECTION_CPU_USAGE_THRESHOLD_MAX[i];
            i += 1;
          }
          Slog.d("OHPD", "updateCpuThreshold # min=" + str + " max=" + paramString);
        }
      }
    }
  }
  
  public void beginCpuStatistics()
  {
    if (DEBUG) {
      Slog.d("OHPD", "beginCpuStatistics");
    }
    synchronized (this.mAppForkedProcMap)
    {
      this.mWorkingForkedPidList.clear();
      this.mPendingRemoveList.clear();
      return;
    }
  }
  
  public void cancelBgDetectNotificationIfNeeded()
  {
    if (DEBUG) {
      Slog.i("OHPD", "cancelBgDetectNotificationIfNeeded");
    }
    try
    {
      if (mHandler.hasMessages(55005)) {
        mHandler.removeMessages(55005);
      }
      Message localMessage = mHandler.obtainMessage(55005);
      mHandler.sendMessageDelayed(localMessage, 1000L);
      return;
    }
    catch (Exception localException)
    {
      Slog.e("OHPD", "Error when cancelBgDetectNotificationIfNeeded");
      localException.printStackTrace();
    }
  }
  
  boolean checkBlackExList(ProcessRecord paramProcessRecord)
  {
    if (isProcInBlackExList(paramProcessRecord))
    {
      if ((paramProcessRecord.setProcState == 3) || (paramProcessRecord.setProcState == 7)) {}
      while ((paramProcessRecord.setProcState == 6) || (paramProcessRecord.setProcState == 5)) {
        return true;
      }
    }
    return false;
  }
  
  boolean checkBlackList(ProcessRecord paramProcessRecord)
  {
    if ((!isProcInBlackList(paramProcessRecord)) || (paramProcessRecord.info.packageName.equals("com.truecaller"))) {}
    while ((paramProcessRecord.setProcState != 3) && (paramProcessRecord.setProcState != 7)) {
      return false;
    }
    return true;
  }
  
  boolean checkKillList(ProcessRecord paramProcessRecord)
  {
    synchronized (this.killProcList)
    {
      if (this.killProcList != null)
      {
        bool = this.killProcList.isEmpty();
        if (!bool) {
          break label30;
        }
      }
      label30:
      while ((paramProcessRecord.info == null) || (paramProcessRecord.processName == null)) {
        return false;
      }
      boolean bool = this.killProcList.contains(paramProcessRecord.processName);
      return bool;
    }
  }
  
  public void doCpuStatistics(ProcessCpuTracker.Stats paramStats)
  {
    if ((paramStats.uid >= 10000) && (paramStats.rel_uptime > 0L)) {}
    synchronized (this.mAppForkedProcMap)
    {
      String str = String.valueOf(paramStats.pid) + String.valueOf(paramStats.uid);
      AppForkedProc localAppForkedProc = (AppForkedProc)this.mAppForkedProcMap.get(str);
      if (localAppForkedProc == null)
      {
        this.mAppForkedProcMap.put(str, new AppForkedProc(paramStats.uid, paramStats.pid, paramStats.name, paramStats.rel_utime + paramStats.rel_stime));
        this.mWorkingForkedPidList.add(Integer.valueOf(paramStats.pid));
        if (DEBUG) {
          Slog.d("OHPD", "doCpuStatistics # forkedPid=" + paramStats.pid);
        }
        return;
      }
      localAppForkedProc.addTime(paramStats.rel_utime + paramStats.rel_stime);
    }
  }
  
  public void finishCpuStatistics()
  {
    if (this.mAppForkedProcMap.size() > 0)
    {
      Object localObject2;
      synchronized (this.mAppForkedProcMap)
      {
        Iterator localIterator1 = this.mAppForkedProcMap.values().iterator();
        while (localIterator1.hasNext())
        {
          localObject2 = (AppForkedProc)localIterator1.next();
          if (!this.mWorkingForkedPidList.contains(Integer.valueOf(((AppForkedProc)localObject2).pid))) {
            this.mPendingRemoveList.add(String.valueOf(((AppForkedProc)localObject2).pid) + String.valueOf(((AppForkedProc)localObject2).uid));
          }
        }
      }
      Iterator localIterator2 = this.mPendingRemoveList.iterator();
      while (localIterator2.hasNext())
      {
        localObject2 = (String)localIterator2.next();
        this.mAppForkedProcMap.remove(localObject2);
      }
    }
    if (DEBUG) {
      Slog.d("OHPD", "finishCpuStatistics");
    }
  }
  
  public void forceUpdateOnlineConfigImmediately()
  {
    resolveBackgroundDetectionConfigFromJSON(new ConfigGrabber(mContext, BACKGROUND_DETECTION_CONFIG_NAME).grabConfig());
  }
  
  public boolean getBgMonitorMode()
  {
    return false;
  }
  
  public List<ActivityManager.HighPowerApp> getBgPowerHungryList()
  {
    long l = SystemClock.uptimeMillis();
    if (UserHandle.getAppId(Binder.getCallingUid()) == 1000)
    {
      ArrayList localArrayList1 = new ArrayList();
      try
      {
        List localList = mAms.mStackSupervisor.getRecentAppLockedPackages();
        HashSet localHashSet1 = new HashSet();
        ArrayList localArrayList2 = new ArrayList();
        HashSet localHashSet2 = new HashSet();
        Iterator localIterator;
        Object localObject7;
        synchronized (mAms)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          localIterator = mAms.mLruProcesses.iterator();
          if (localIterator.hasNext())
          {
            localObject7 = (ProcessRecord)localIterator.next();
            localHashSet1.add(((ProcessRecord)localObject7).info.packageName);
            localHashSet2.add(Integer.valueOf(((ProcessRecord)localObject7).pid));
          }
        }
        int j;
        int i;
        boolean bool;
        label554:
        label607:
        label763:
        label816:
        label957:
        return null;
      }
      catch (Exception localException)
      {
        Slog.e("OHPD", "[BgDetect] Error in getBgPowerHungryList");
        localException.printStackTrace();
        for (;;)
        {
          Slog.i("OHPD", "[BgDetect] getBgPowerHungryList result size " + localArrayList1.size() + " in " + (SystemClock.uptimeMillis() - l) + " ms");
          if (!DEBUG) {
            break;
          }
          j = localArrayList1.size();
          i = 0;
          while (i < j)
          {
            Slog.i("OHPD", "dump# " + i + " pkg:" + ((ActivityManager.HighPowerApp)localArrayList1.get(i)).pkgName + " powerLevel:" + ((ActivityManager.HighPowerApp)localArrayList1.get(i)).powerLevel);
            i += 1;
          }
          ActivityManagerService.resetPriorityAfterLockedSection();
          if (this.mAppForkedProcMap.size() > 0) {
            synchronized (this.mAppForkedProcMap)
            {
              localIterator = this.mAppForkedProcMap.values().iterator();
              if (localIterator.hasNext())
              {
                localObject7 = (AppForkedProc)localIterator.next();
                localHashSet1.add(((AppForkedProc)localObject7).pkgName);
                localHashSet2.add(Integer.valueOf(((AppForkedProc)localObject7).pid));
              }
            }
          }
          for (;;)
          {
            synchronized (this.mHugePowerPkgMap)
            {
              localIterator = this.mHugePowerPkgMap.entrySet().iterator();
              if (!localIterator.hasNext()) {
                break;
              }
              localObject7 = (ExtendHighPowerApp)((Map.Entry)localIterator.next()).getValue();
              if (((List)localObject2).contains(((ExtendHighPowerApp)localObject7).pkgName))
              {
                bool = true;
                ((ExtendHighPowerApp)localObject7).isLocked = bool;
                if (!localHashSet1.contains(((ExtendHighPowerApp)localObject7).pkgName)) {
                  break label607;
                }
                if (!localHashSet2.contains(Integer.valueOf(((ExtendHighPowerApp)localObject7).pid))) {
                  break label554;
                }
                localArrayList1.add(localObject7);
                localArrayList2.add(((ExtendHighPowerApp)localObject7).pkgName);
              }
            }
            bool = false;
            continue;
            if (DEBUG)
            {
              Slog.d("OHPD", "huge dead pid:" + ((ExtendHighPowerApp)localObject7).pid + " " + ((ExtendHighPowerApp)localObject7).pkgName);
              continue;
              localIterator.remove();
            }
          }
          for (;;)
          {
            synchronized (this.mHighPowerPkgMap)
            {
              localIterator = this.mHighPowerPkgMap.entrySet().iterator();
              if (!localIterator.hasNext()) {
                break;
              }
              localObject7 = (ExtendHighPowerApp)((Map.Entry)localIterator.next()).getValue();
              if (((List)localObject3).contains(((ExtendHighPowerApp)localObject7).pkgName))
              {
                bool = true;
                ((ExtendHighPowerApp)localObject7).isLocked = bool;
                if (!localHashSet1.contains(((ExtendHighPowerApp)localObject7).pkgName)) {
                  break label816;
                }
                if (!localHashSet2.contains(Integer.valueOf(((ExtendHighPowerApp)localObject7).pid))) {
                  break label763;
                }
                localArrayList1.add(localObject7);
                localArrayList2.add(((ExtendHighPowerApp)localObject7).pkgName);
              }
            }
            bool = false;
            continue;
            if (DEBUG)
            {
              Slog.d("OHPD", "high dead pid:" + ((ExtendHighPowerApp)localObject7).pid + " " + ((ExtendHighPowerApp)localObject7).pkgName);
              continue;
              localIterator.remove();
            }
          }
          for (;;)
          {
            synchronized (this.mMediumPowerPkgMap)
            {
              localIterator = this.mMediumPowerPkgMap.entrySet().iterator();
              if (!localIterator.hasNext()) {
                break;
              }
              localObject7 = (ExtendHighPowerApp)((Map.Entry)localIterator.next()).getValue();
              if (((List)localObject4).contains(((ExtendHighPowerApp)localObject7).pkgName))
              {
                bool = true;
                ((ExtendHighPowerApp)localObject7).isLocked = bool;
                if ((localHashSet1.contains(((ExtendHighPowerApp)localObject7).pkgName)) && (!localArrayList2.contains(((ExtendHighPowerApp)localObject7).pkgName))) {
                  break label957;
                }
                localIterator.remove();
              }
            }
            bool = false;
            continue;
            if (localHashSet2.contains(Integer.valueOf(((ExtendHighPowerApp)localObject7).pid))) {
              localArrayList1.add(localObject7);
            } else if (DEBUG) {
              Slog.d("OHPD", "medium dead pid:" + ((ExtendHighPowerApp)localObject7).pid + " " + ((ExtendHighPowerApp)localObject7).pkgName);
            }
          }
        }
        return localArrayList1;
      }
    }
  }
  
  int getDeviceTemp()
  {
    try
    {
      char[] arrayOfChar = new char[10];
      FileReader localFileReader = new FileReader("/sys/class/thermal/thermal_zone5/temp");
      try
      {
        int i = Integer.parseInt(new String(arrayOfChar, 0, localFileReader.read(arrayOfChar, 0, 10)).trim());
        return i;
      }
      finally
      {
        localFileReader.close();
      }
      return 0;
    }
    catch (Exception localException)
    {
      Slog.w("OHPD", "Can't get device temp w /sys/class/thermal/thermal_zone5/temp");
    }
  }
  
  public void handleMessage(Message arg1)
  {
    myLog("handleMessage # msg.what=" + ???.what);
    Object localObject8;
    Object localObject9;
    Object localObject6;
    switch (???.what)
    {
    case 55007: 
    default: 
    case 55000: 
    case 55001: 
    case 55002: 
    case 55006: 
      do
      {
        do
        {
          do
          {
            do
            {
              return;
            } while (checkExcessiveCpuUsageLocked(0, ActivityManagerService.CPU_MIN_CHECK_DURATION) != 0);
            synchronized (mAms)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              mHandler.removeMessages(55000);
              Message localMessage1 = mHandler.obtainMessage(55000);
              mHandler.sendMessageDelayed(localMessage1, CPU_CHECK_DELAY[0]);
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
          } while (checkExcessiveCpuUsageLocked(1, CPU_CHECK_DELAY[1]) != 0);
          synchronized (mAms)
          {
            ActivityManagerService.boostPriorityForLockedSection();
            mHandler.removeMessages(55001);
            Message localMessage2 = mHandler.obtainMessage(55001);
            mHandler.sendMessageDelayed(localMessage2, CPU_CHECK_DELAY[1]);
            ActivityManagerService.resetPriorityAfterLockedSection();
            return;
          }
        } while (checkExcessiveCpuUsageLocked(2, CPU_CHECK_DELAY[2]) != 0);
        synchronized (mAms)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          mHandler.removeMessages(55002);
          Message localMessage3 = mHandler.obtainMessage(55002);
          mHandler.sendMessageDelayed(localMessage3, CPU_CHECK_DELAY[2]);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
      } while (checkExcessiveCpuUsageLocked(3, CPU_CHECK_DELAY[3]) != 0);
      synchronized (mAms)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        mHandler.removeMessages(55006);
        Message localMessage4 = mHandler.obtainMessage(55006);
        mHandler.sendMessageDelayed(localMessage4, CPU_CHECK_DELAY[3]);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    case 55003: 
      try
      {
        localObject8 = (String)???.obj;
        if (localObject8 == null) {
          return;
        }
        long l1 = SystemClock.uptimeMillis();
        if (this.mNotifyPkgMap.containsKey(localObject8))
        {
          long l2 = ((Long)this.mNotifyPkgMap.get(localObject8)).longValue();
          if (l1 - l2 < NOTIFY_INTERVAL)
          {
            if (!DEBUG) {
              break label1945;
            }
            Slog.d("OHPD", "Notification # too short time to notify: " + (l1 - l2) + ", " + (String)localObject8);
            return;
          }
        }
        this.mNotifyPkgMap.put(localObject8, Long.valueOf(l1));
        ??? = null;
        try
        {
          localObject9 = mContext.getPackageManager().getApplicationInfo((String)localObject8, 0);
          localObject5 = ???;
          if (localObject9 != null) {
            localObject5 = (String)mContext.getPackageManager().getApplicationLabel((ApplicationInfo)localObject9);
          }
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            Object localObject5;
            Slog.e("OHPD", "get highpower pkg label error");
            localObject6 = ???;
          }
        }
        if ((localObject5 == null) || (((String)localObject5).length() < 1))
        {
          Slog.e("OHPD", "[BgDetect] error: cannot get pkg label : " + (String)localObject8);
          return;
        }
      }
      catch (Exception ???)
      {
        Slog.e("OHPD", "Error posting power intensive notification");
        ???.printStackTrace();
        return;
      }
      ??? = String.valueOf(mContext.getText(84541487));
      localObject9 = String.valueOf(mContext.getText(84541486));
      if (DEBUG)
      {
        Slog.i("OHPD", "Notification # title:84541487 " + (String)???);
        Slog.i("OHPD", "Notification # content:84541486 " + (String)localObject9);
        Slog.i("OHPD", "Notification # triggerPkgName:" + (String)localObject8 + ",pkgLabel:" + (String)localObject6);
      }
      localObject6 = (String)localObject6 + " " + (String)localObject9;
      localObject9 = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
      ((Intent)localObject9).putExtra("classname", "com.android.settings.fuelgauge.PowerUsageSummary");
      Object localObject10 = new Intent("com.oem.intent.action.force_stop_pkg");
      ((Intent)localObject10).putExtra("pkg", (String)localObject8);
      localObject10 = PendingIntent.getBroadcastAsUser(mContext, 0, (Intent)localObject10, 134217728, UserHandle.SYSTEM);
      localObject10 = new Notification.Action.Builder(17302315, mContext.getText(84541493), (PendingIntent)localObject10).build();
      localObject6 = new Notification.Builder(mContext).setSmallIcon(17301642).setColor(mContext.getColor(17170523)).setContentTitle((CharSequence)???).setContentText((CharSequence)localObject6).setContentIntent(PendingIntent.getActivityAsUser(mContext, 0, (Intent)localObject9, 134217728, null, new UserHandle(UserHandle.myUserId()))).setAutoCancel(true).setVisibility(1).addAction((Notification.Action)localObject10).build();
      ??? = new int[1];
      NotificationManager.getService().enqueueNotificationWithTag("android", "android", null, 84541486, (Notification)localObject6, (int[])???, UserHandle.myUserId());
      this.mCurNotifyPkgNameSet.add(localObject8);
      this.mCurNotifyPkgName = ((String)localObject8);
      Slog.i("OHPD", "[BgDetect][Notification] notify for pkg " + (String)localObject8 + " pid " + ???.arg1);
      return;
    case 55004: 
      try
      {
        cleanUpWhenPkgRemoved((String)???.obj);
        return;
      }
      catch (Exception ???)
      {
        Slog.e("OHPD", "Error when clean up package removed record");
        ???.printStackTrace();
        return;
      }
    case 55005: 
      for (;;)
      {
        try
        {
          if ((this.mHugePowerPkgMap.isEmpty()) && (this.mHighPowerPkgMap.isEmpty()) && (this.mMediumPowerPkgMap.isEmpty()))
          {
            ??? = NotificationManager.getService();
            localObject6 = ???.getActiveNotifications("android");
            if ((localObject6 != null) && (localObject6.length >= 1)) {
              break;
            }
            if (!DEBUG) {
              return;
            }
            Slog.i("OHPD", "[BgDetect][Notification] activeList null");
            return;
          }
          ??? = new HashSet();
          localObject6 = new HashSet();
          synchronized (mAms)
          {
            ActivityManagerService.boostPriorityForLockedSection();
            localObject8 = mAms.mLruProcesses.iterator();
            if (((Iterator)localObject8).hasNext())
            {
              localObject9 = (ProcessRecord)((Iterator)localObject8).next();
              ???.add(((ProcessRecord)localObject9).info.packageName);
              ((HashSet)localObject6).add(Integer.valueOf(((ProcessRecord)localObject9).pid));
            }
          }
        }
        catch (Exception ???)
        {
          Slog.w("OHPD", "[BgDetect] cancel notification fail", ???);
          return;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if (this.mAppForkedProcMap.size() > 0) {
          synchronized (this.mAppForkedProcMap)
          {
            localObject8 = this.mAppForkedProcMap.values().iterator();
            if (((Iterator)localObject8).hasNext())
            {
              localObject9 = (AppForkedProc)((Iterator)localObject8).next();
              ???.add(((AppForkedProc)localObject9).pkgName);
              ((HashSet)localObject6).add(Integer.valueOf(((AppForkedProc)localObject9).pid));
            }
          }
        }
        synchronized (this.mHugePowerPkgMap)
        {
          localObject8 = this.mHugePowerPkgMap.keySet().iterator();
          while (((Iterator)localObject8).hasNext())
          {
            localObject9 = (String)((Iterator)localObject8).next();
            if (???.contains(localObject9))
            {
              localObject9 = (ExtendHighPowerApp)this.mHugePowerPkgMap.get(localObject9);
              if ((((HashSet)localObject6).contains(Integer.valueOf(((ExtendHighPowerApp)localObject9).pid))) && (((ExtendHighPowerApp)localObject9).pkgName.equals(this.mCurNotifyPkgName)))
              {
                if (DEBUG) {
                  Slog.i("OHPD", "[BgDetect][Notification] cancel abort: huge running: " + localObject9);
                }
                return;
              }
            }
          }
        }
        synchronized (this.mHighPowerPkgMap)
        {
          localObject8 = this.mHighPowerPkgMap.keySet().iterator();
          while (((Iterator)localObject8).hasNext())
          {
            localObject9 = (String)((Iterator)localObject8).next();
            if (???.contains(localObject9))
            {
              localObject9 = (ExtendHighPowerApp)this.mHighPowerPkgMap.get(localObject9);
              if ((((HashSet)localObject6).contains(Integer.valueOf(((ExtendHighPowerApp)localObject9).pid))) && (((ExtendHighPowerApp)localObject9).pkgName.equals(this.mCurNotifyPkgName)))
              {
                if (DEBUG) {
                  Slog.i("OHPD", "[BgDetect][Notification] cancel abort: high running: " + localObject9);
                }
                return;
                ??? = finally;
                throw ???;
              }
            }
          }
        }
        synchronized (this.mMediumPowerPkgMap)
        {
          localObject8 = this.mMediumPowerPkgMap.keySet().iterator();
          while (((Iterator)localObject8).hasNext())
          {
            localObject9 = (String)((Iterator)localObject8).next();
            if (???.contains(localObject9))
            {
              localObject9 = (ExtendHighPowerApp)this.mMediumPowerPkgMap.get(localObject9);
              if ((((HashSet)localObject6).contains(Integer.valueOf(((ExtendHighPowerApp)localObject9).pid))) && (((ExtendHighPowerApp)localObject9).pkgName.equals(this.mCurNotifyPkgName)))
              {
                if (DEBUG) {
                  Slog.i("OHPD", "[BgDetect][Notification] cancel abort: medium running: " + localObject9);
                }
                return;
                ??? = finally;
                throw ???;
              }
            }
          }
        }
      }
      int i = 0;
      int j = localObject6.length;
      while (i < j)
      {
        if (localObject6[i].getId() == 84541486)
        {
          Slog.i("OHPD", "[BgDetect][Notification] notification exists, cancel it");
          ???.cancelNotificationWithTag("android", null, 84541486, UserHandle.myUserId());
          ??? = this.mCurNotifyPkgNameSet.iterator();
          while (???.hasNext())
          {
            localObject6 = (String)???.next();
            this.mNotifyPkgMap.put(localObject6, Long.valueOf(-NOTIFY_INTERVAL));
          }
          this.mCurNotifyPkgNameSet.clear();
          this.mCurNotifyPkgName = null;
          return;
        }
        i += 1;
      }
    }
    try
    {
      NotificationManager.getService().cancelNotificationWithTag("android", null, 84541486, UserHandle.myUserId());
      try
      {
        forceStopPackage((String)???.obj, -2);
        return;
      }
      catch (Exception ???)
      {
        Slog.e("OHPD", "[BgDetect] Error when force stop pkg");
        ???.printStackTrace();
        return;
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        Slog.e("OHPD", "[BgDetect] Error when cancel notification");
      }
    }
    label1945:
    return;
  }
  
  boolean hasSystemFlag(int paramInt)
  {
    boolean bool = false;
    if ((paramInt & 0x81) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void initOnlineConfig()
  {
    this.mBackgroundDetectionConfigObserver = new ConfigObserver(mContext, mHandler, new BackgroundDetectionConfigUpdater(), BACKGROUND_DETECTION_CONFIG_NAME);
    this.mBackgroundDetectionConfigObserver.register();
  }
  
  boolean isForkedProcInWhiteList(AppForkedProc paramAppForkedProc)
  {
    synchronized (this.whiteAppListSet)
    {
      if (this.whiteAppListSet != null)
      {
        bool = this.whiteAppListSet.isEmpty();
        if (!bool) {
          break label30;
        }
      }
      label30:
      while (paramAppForkedProc.pkgName == null) {
        return false;
      }
      boolean bool = this.whiteAppListSet.contains(paramAppForkedProc.pkgName);
      return bool;
    }
  }
  
  boolean isProcInBlackExList(ProcessRecord paramProcessRecord)
  {
    synchronized (this.blackExAppListSet)
    {
      if (this.blackExAppListSet != null)
      {
        bool = this.blackExAppListSet.isEmpty();
        if (!bool) {
          break label30;
        }
      }
      label30:
      while ((paramProcessRecord.info == null) || (paramProcessRecord.info.packageName == null)) {
        return false;
      }
      boolean bool = this.blackExAppListSet.contains(paramProcessRecord.info.packageName);
      return bool;
    }
  }
  
  boolean isProcInBlackList(ProcessRecord paramProcessRecord)
  {
    synchronized (this.blackAppListSet)
    {
      if (this.blackAppListSet != null)
      {
        bool = this.blackAppListSet.isEmpty();
        if (!bool) {
          break label30;
        }
      }
      label30:
      while ((paramProcessRecord.info == null) || (paramProcessRecord.info.packageName == null)) {
        return false;
      }
      boolean bool = this.blackAppListSet.contains(paramProcessRecord.info.packageName);
      return bool;
    }
  }
  
  boolean isProcInWhiteList(ProcessRecord paramProcessRecord)
  {
    synchronized (this.whiteAppListSet)
    {
      if (this.whiteAppListSet != null)
      {
        bool = this.whiteAppListSet.isEmpty();
        if (!bool) {
          break label30;
        }
      }
      label30:
      while ((paramProcessRecord.info == null) || (paramProcessRecord.info.packageName == null)) {
        return false;
      }
      boolean bool = this.whiteAppListSet.contains(paramProcessRecord.info.packageName);
      return bool;
    }
  }
  
  boolean isProviderBoundByFG(ProcessRecord paramProcessRecord, int paramInt)
  {
    if ((paramProcessRecord.pubProviders != null) && (paramProcessRecord.pubProviders.size() > 0))
    {
      int i = 0;
      while (i < paramProcessRecord.pubProviders.size())
      {
        Iterator localIterator = ((ContentProviderRecord)paramProcessRecord.pubProviders.valueAt(i)).connections.iterator();
        for (;;)
        {
          if (localIterator.hasNext()) {
            try
            {
              Object localObject = (ContentProviderConnection)localIterator.next();
              if ((localObject != null) && (paramProcessRecord.uid != ((ContentProviderConnection)localObject).client.uid))
              {
                localObject = ((ContentProviderConnection)localObject).client;
                if ((((ProcessRecord)localObject).setProcState == 2) || (((ProcessRecord)localObject).setProcState == 1))
                {
                  Slog.i("OHPD", "[BgDetect] skip " + paramProcessRecord.processName + ", bound by " + ((ProcessRecord)localObject).processName + "(" + ((ProcessRecord)localObject).info.packageName + ") state " + ((ProcessRecord)localObject).setProcState + " level " + paramInt);
                  return true;
                }
              }
            }
            catch (Exception localException) {}
          }
        }
        i += 1;
      }
    }
    return false;
  }
  
  boolean needCheckProc(ProcessRecord paramProcessRecord, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
    case 1: 
    case 2: 
      if ((paramProcessRecord.setProcState >= 12) || (paramProcessRecord.setProcState == 10)) {}
      while (paramProcessRecord.setProcState == 4) {
        return true;
      }
      return false;
    }
    if ((paramProcessRecord.setProcState >= 12) || (paramProcessRecord.setProcState == 10)) {}
    while ((paramProcessRecord.setProcState == 4) || (paramProcessRecord.setProcState == 3) || (paramProcessRecord.setProcState == 6) || (paramProcessRecord.setProcState == 7)) {
      return true;
    }
    return false;
  }
  
  /* Error */
  public void postProcessOfForceStop(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   11: ifnull +23 -> 34
    //   14: aload_0
    //   15: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   18: aload_1
    //   19: invokevirtual 904	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   22: ifeq +12 -> 34
    //   25: aload_0
    //   26: getfield 298	com/android/server/am/OnePlusHighPowerDetector:mHugePowerPkgMap	Ljava/util/HashMap;
    //   29: aload_1
    //   30: invokevirtual 905	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   33: pop
    //   34: aload_2
    //   35: monitorexit
    //   36: aload_0
    //   37: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   40: astore_2
    //   41: aload_2
    //   42: monitorenter
    //   43: aload_0
    //   44: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   47: ifnull +23 -> 70
    //   50: aload_0
    //   51: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   54: aload_1
    //   55: invokevirtual 904	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   58: ifeq +12 -> 70
    //   61: aload_0
    //   62: getfield 300	com/android/server/am/OnePlusHighPowerDetector:mHighPowerPkgMap	Ljava/util/HashMap;
    //   65: aload_1
    //   66: invokevirtual 905	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   69: pop
    //   70: aload_2
    //   71: monitorexit
    //   72: aload_0
    //   73: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   76: astore_2
    //   77: aload_2
    //   78: monitorenter
    //   79: aload_0
    //   80: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   83: ifnull +23 -> 106
    //   86: aload_0
    //   87: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   90: aload_1
    //   91: invokevirtual 904	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   94: ifeq +12 -> 106
    //   97: aload_0
    //   98: getfield 302	com/android/server/am/OnePlusHighPowerDetector:mMediumPowerPkgMap	Ljava/util/HashMap;
    //   101: aload_1
    //   102: invokevirtual 905	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   105: pop
    //   106: aload_2
    //   107: monitorexit
    //   108: aload_0
    //   109: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   112: astore_2
    //   113: aload_2
    //   114: monitorenter
    //   115: aload_0
    //   116: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   119: ifnull +110 -> 229
    //   122: aload_0
    //   123: getfield 304	com/android/server/am/OnePlusHighPowerDetector:mAppForkedProcMap	Ljava/util/HashMap;
    //   126: invokevirtual 1342	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   129: invokeinterface 1345 1 0
    //   134: astore_3
    //   135: aload_3
    //   136: invokeinterface 667 1 0
    //   141: ifeq +88 -> 229
    //   144: aload_1
    //   145: aload_3
    //   146: invokeinterface 671 1 0
    //   151: checkcast 828	java/util/Map$Entry
    //   154: invokeinterface 834 1 0
    //   159: checkcast 10	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc
    //   162: getfield 733	com/android/server/am/OnePlusHighPowerDetector$AppForkedProc:pkgName	Ljava/lang/String;
    //   165: invokevirtual 707	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   168: ifeq -33 -> 135
    //   171: aload_3
    //   172: invokeinterface 1355 1 0
    //   177: goto -42 -> 135
    //   180: astore_3
    //   181: aload_2
    //   182: monitorexit
    //   183: aload_3
    //   184: athrow
    //   185: astore_2
    //   186: ldc 121
    //   188: new 353	java/lang/StringBuilder
    //   191: dup
    //   192: invokespecial 354	java/lang/StringBuilder:<init>	()V
    //   195: ldc_w 1608
    //   198: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload_1
    //   202: invokevirtual 360	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 369	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: aload_2
    //   209: invokestatic 1610	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   212: pop
    //   213: return
    //   214: astore_3
    //   215: aload_2
    //   216: monitorexit
    //   217: aload_3
    //   218: athrow
    //   219: astore_3
    //   220: aload_2
    //   221: monitorexit
    //   222: aload_3
    //   223: athrow
    //   224: astore_3
    //   225: aload_2
    //   226: monitorexit
    //   227: aload_3
    //   228: athrow
    //   229: aload_2
    //   230: monitorexit
    //   231: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	OnePlusHighPowerDetector
    //   0	232	1	paramString	String
    //   185	45	2	localException	Exception
    //   134	38	3	localIterator	Iterator
    //   180	4	3	localObject1	Object
    //   214	4	3	localObject2	Object
    //   219	4	3	localObject3	Object
    //   224	4	3	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   115	135	180	finally
    //   135	177	180	finally
    //   0	7	185	java/lang/Exception
    //   34	43	185	java/lang/Exception
    //   70	79	185	java/lang/Exception
    //   106	115	185	java/lang/Exception
    //   181	185	185	java/lang/Exception
    //   215	219	185	java/lang/Exception
    //   220	224	185	java/lang/Exception
    //   225	229	185	java/lang/Exception
    //   229	231	185	java/lang/Exception
    //   7	34	214	finally
    //   43	70	219	finally
    //   79	106	224	finally
  }
  
  public void setBgMonitorMode(boolean paramBoolean) {}
  
  public void startMonitor()
  {
    Message localMessage = mHandler.obtainMessage(55000);
    mHandler.sendMessageDelayed(localMessage, CPU_CHECK_DELAY[0]);
    localMessage = mHandler.obtainMessage(55001);
    mHandler.sendMessageDelayed(localMessage, CPU_CHECK_DELAY[1]);
    localMessage = mHandler.obtainMessage(55002);
    mHandler.sendMessageDelayed(localMessage, CPU_CHECK_DELAY[2]);
    localMessage = mHandler.obtainMessage(55006);
    mHandler.sendMessageDelayed(localMessage, CPU_CHECK_DELAY[3]);
    Slog.i("OHPD", "[BgDetect] startMonitor # queue CHECK_EXCESSIVE_CPU MSGs");
    this.mBgDetectStartMonitoring = true;
  }
  
  public void stopBgPowerHungryApp(String paramString, int paramInt)
  {
    stopBgPowerHungryApp(paramString, paramInt, true);
  }
  
  void updateProcUsg(ProcessRecord paramProcessRecord, int paramInt)
  {
    paramProcessRecord.lastCpuTime = paramProcessRecord.curCpuTime;
    paramProcessRecord.lastCpuTimeBgMonitor[paramInt] = paramProcessRecord.curCpuTimeBgMonitor;
    paramProcessRecord.lastRxBytes[paramInt] = TrafficStats.getUidRxBytes(paramProcessRecord.uid);
    paramProcessRecord.lastTxBytes[paramInt] = TrafficStats.getUidTxBytes(paramProcessRecord.uid);
  }
  
  void updateSkipMap(ArrayMap<Integer, String> paramArrayMap, int paramInt, String paramString)
  {
    if (!paramArrayMap.containsKey(Integer.valueOf(paramInt))) {
      paramArrayMap.put(Integer.valueOf(paramInt), paramString);
    }
  }
  
  public static class AppForkedProc
  {
    public long curCpuTimeBgMonitor = 0L;
    public int flags = 0;
    public long[] lastCpuTimeBgMonitor = new long[4];
    public String name;
    public final int pid;
    public String pkgName;
    public final int uid;
    
    AppForkedProc(int paramInt1, int paramInt2, String paramString, int paramInt3)
    {
      this.uid = paramInt1;
      this.pid = paramInt2;
      this.name = paramString;
      this.curCpuTimeBgMonitor += paramInt3;
      this.pkgName = OnePlusHighPowerDetector.-get0().getPackageManager().getNameForUid(paramInt1);
      if (this.pkgName != null) {
        try
        {
          paramString = OnePlusHighPowerDetector.-get0().getPackageManager().getApplicationInfo(this.pkgName, 0);
          if (paramString != null) {
            this.flags = paramString.flags;
          }
          return;
        }
        catch (PackageManager.NameNotFoundException paramString)
        {
          paramString.printStackTrace();
          Slog.e("OHPD", "[BgDetect]err when get info of " + this.name + "(" + this.pkgName + ") uid " + this.uid + " pid " + this.pid);
          return;
        }
      }
      Slog.e("OHPD", "[BgDetect] AppForkedProc " + this.name + " pid " + this.pid + " uid " + this.uid + " can't find pkgName");
    }
    
    public void addTime(long paramLong)
    {
      this.curCpuTimeBgMonitor += paramLong;
    }
  }
  
  public static class AppInfo
  {
    public final int pid;
    public String pkgName;
    public final int uid;
    
    AppInfo(int paramInt1, int paramInt2, String paramString)
    {
      this.uid = paramInt1;
      this.pid = paramInt2;
      this.pkgName = paramString;
    }
  }
  
  class BackgroundDetectionConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    BackgroundDetectionConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      OnePlusHighPowerDetector.-wrap2(OnePlusHighPowerDetector.this, paramJSONArray);
    }
  }
  
  public static class ExtendHighPowerApp
    extends ActivityManager.HighPowerApp
  {
    public int pid;
    
    public ExtendHighPowerApp(String paramString, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, long paramLong, int paramInt2)
    {
      super(paramInt1, paramBoolean1, paramBoolean2, paramLong);
      this.pid = paramInt2;
    }
    
    public String toString()
    {
      String str = "ExtendHighPowerApp{" + "pkg=" + this.pkgName;
      str = str + ",level=" + this.powerLevel;
      str = str + ",pid=" + this.pid;
      str = str + ",locked=" + this.isLocked;
      str = str + ",stopped=" + this.isStopped;
      return str + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/OnePlusHighPowerDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */