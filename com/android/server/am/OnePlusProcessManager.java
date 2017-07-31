package com.android.server.am;

import android.app.AlarmManager;
import android.app.IAlarmManager;
import android.app.IAlarmManager.Stub;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.IProcessObserver;
import android.app.IProcessObserver.Stub;
import android.app.IUidObserver;
import android.app.IUidObserver.Stub;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.server.LocationManagerService;
import com.android.server.power.PowerManagerService;
import com.android.server.wm.WindowManagerService;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.oneplus.odm.insight.tracker.AppTracker;
import org.json.JSONArray;

public class OnePlusProcessManager
{
  private static final String APP_CFG_FILE = "cfg.xml";
  private static final String APP_CFG_PATH = "/data/data_bpm/cfg.xml";
  private static final String BLACK_ALARM_FILE = "black_alarm.xml";
  private static final String BLACK_ALARM_PATH = "/data/data_bpm/black_alarm.xml";
  private static final String BLACK_BRD_APP_FILE = "black_brd.xml";
  private static final String BLACK_BRD_APP_PATH = "/data/data_bpm/black_brd.xml";
  private static final String BLACK_GPS_APP_FILE = "black_gps.xml";
  private static final String BLACK_GPS_APP_PATH = "/data/data_bpm/black_gps.xml";
  private static final String BPM_DIR = "/data/data_bpm/";
  private static final String BPM_FILE = "bpm.xml";
  private static final String BPM_PATH = "/data/data_bpm/bpm.xml";
  private static final String BPM_STATUS_FILE = "bpm_sts.xml";
  public static final String BPM_STATUS_PATH = "/data/data_bpm/bpm_sts.xml";
  private static final String BRD_FILE = "brd.xml";
  private static final String BRD_PATH = "/data/data_bpm/brd.xml";
  private static int CFG_VERSOON = 1;
  private static String CONFIG_NAME;
  public static boolean DEBUG = false;
  public static boolean DEBUG_DETAIL = false;
  public static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  private static final String DEVICE_IDLE_SERVICE = "deviceidle";
  private static final int FORCE_SWITCH_IGNORE = 0;
  private static final int FORCE_SWITCH_OFF = 2;
  private static final int FORCE_SWITCH_ON = 1;
  private static final int GET_CONFIG = 1;
  public static final int GLOBAL_FLAG_SETTED_SIM_COUNTRY = 1;
  private static final int INIT_CONFIG_DATA = 112;
  private static final int MESSAGE_COMPLUTE_TRAFFIC = 116;
  private static final int MESSAGE_SUSPEND_DELAY = 113;
  private static final int MESSAGE_SUSPEND_UID_ADDPID = 115;
  private static final int MESSAGE_UPDATE_FORGEGROUND = 119;
  private static final int MESSAGE_UPDATE_STATE = 118;
  private static final int MESSAGE_WRITE_PID_DELAY = 114;
  private static final String PHONE_PKG_NAME = "com.android.phone";
  private static final String PKG_FILE = "pkg.xml";
  private static final String PKG_PATH = "/data/data_bpm/pkg.xml";
  public static final boolean POLICY_USE_CGROUP = true;
  private static final String PROP_FLAGS = "persist.sys.cgroup.flags";
  private static final String PROP_REGION = "persist.sys.oem.region";
  private static final String PROP_SIM_COUNTRY = "gsm.sim.operator.iso-country";
  private static final String PROP_USING = "persist.sys.cgroup.using";
  static final int ReceiverFailMax = 4;
  private static String SCREEN_OFF_INTENT;
  private static final String SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
  private static final int START_WATCHING = 111;
  private static final long SUSPEND_DELAY = 65000L;
  static final int SUSPEND_DELAY_DEFAULT = 1;
  static final int SUSPEND_DELAY_LONG = 3;
  static final int SUSPEND_DELAY_SHORT = 2;
  private static final long SUSPEND_PRE_APP_DELAY = 200000L;
  private static final String SYS_BLACK_ALARM_FILE = "/system/bpm/black_alarm.xml";
  private static final String SYS_BLACK_BRD_FILE = "/system/bpm/black_brd.xml";
  private static final String SYS_BLACK_GPS_FILE = "/system/bpm/black_gps.xml";
  private static final String SYS_BRD_FILE = "/system/bpm/brd.xml";
  private static final String SYS_CFG_FILE = "/system/bpm/cfg.xml";
  private static final String SYS_PKG_FILE = "/system/bpm/pkg.xml";
  public static final String SYS_STATUS_FILE = "/system/bpm/bpm_sts.xml";
  private static final String SYS_VERSION_FILE = "/system/bpm/version.xml";
  public static final String TAG = "OnePlusProcessManager";
  private static final long TRAFFIC_THRESHOLD = 102400L;
  private static final int UPDATE_APPWIDGET = 105;
  private static final int UPDATE_BAD = 104;
  private static final int UPDATE_BPM = 101;
  private static final int UPDATE_BRD = 102;
  private static final int UPDATE_PKG = 103;
  private static final int UPDATE_STS = 100;
  public static final int VERSION = 15112601;
  private static final String VERSION_FILE = "version.xml";
  private static final String VERSION_PATH = "/data/data_bpm/version.xml";
  static long computeTrafficTime = 0L;
  private static boolean isAlarmAdjust = false;
  private static boolean isChargeringCloseForzen = false;
  static boolean isSuppoerted = false;
  private static boolean isUsing = false;
  private static LocationManagerService lm;
  public static ActivityManagerService mActivityManager;
  static HashSet<Integer> mAdjustUids;
  private static AlarmManager mAlarmManager;
  static HashSet<Integer> mAudioUids;
  private static boolean mBPMStatus = false;
  private static boolean mBPMStatusing = false;
  static ArrayList<Integer> mCanFrozenUids;
  static final String mCgroupFreezerPath = "/sys/fs/cgroup/freezer/";
  private static boolean mCharging;
  private static IDeviceIdleController mDeviceIdleService;
  static HashSet<Integer> mDoThawedUids;
  private static int mGlobalFlags = 0;
  static ArrayMap<Integer, Integer> mLoactionFailUidsCount;
  public static Object mLock;
  public static Object mLockProcess;
  public static Object mMessageLock;
  static ArrayList<Integer> mNotAllowSensorUids;
  private static INotificationManager mNotification;
  private static OnePlusProcessManager mOnePlusProcessManager;
  private static PackageManager mPackageManager;
  static int mPendingUid;
  static boolean mPhoneAppReady = false;
  public static Object mProcess;
  private static String mRegion = "";
  private static boolean mResumeFirst;
  public static Object mScreenLock;
  private static PendingIntent mScreenOffCheckIntent;
  static int mScreenOffCount;
  private static Intent mScreenOffIntent;
  static boolean mScreen_ON;
  static boolean mScreen_ON_ING;
  static ArrayMap<Integer, Integer> mSuspendFailUidsCount;
  static ArrayList<Integer> mSuspendUids;
  static HashSet<Integer> mTrafficUids;
  static ArrayMap<Integer, Integer> mTrafficUidsCount;
  static ArrayList<String> mTrafficeWhiteUids;
  static ArrayMap<Integer, String> mUidPackageNames;
  static ArrayMap<Integer, Traffic> mUidTraffic;
  static ArrayMap<Integer, String> mUnFrozenReasonUids;
  public static Object mWakeLock;
  public static Object mWhiteLock;
  static int[] mWhiteUids;
  public static int sInputMethodUid;
  static long screenOffCheckDelayTime;
  static long suspendUidDelayTime;
  private double SUSPEND_FAIL_DEFAULT = 3.0D;
  private double SUSPEND_FAIL_LONG = 2.0D;
  private double SUSPEND_FAIL_NOTRY = 1.0D;
  private double SUSPEND_FAIL_SLEEP = 4.0D;
  private double SUSPEND_OK = 0.0D;
  AppTracker appTracker = null;
  IAlarmManager mAlarm = null;
  HashSet<Integer> mAlarmAdjust = new HashSet();
  private AudioManager mAudioManager;
  private List<String> mBPMList = new ArrayList();
  private List<String> mBlackAlarmList = new ArrayList();
  private List<String> mBlackBrdList = new ArrayList();
  private List<String> mBlackGpsList = new ArrayList();
  private List<String> mBrdList = new ArrayList();
  private Handler mConfigHandler;
  private Context mContext;
  private FileObserverPolicy mDataBpmDirFileObserver;
  int mForceSwitch = 0;
  HashSet<Integer> mFrontActivityUids = new HashSet();
  HashSet<Integer> mFrontWindowTouchUids = new HashSet();
  ArrayList<Integer> mGpsReceiverLocationUids = new ArrayList();
  HashSet<Integer> mImportantUids = new HashSet();
  private Handler mOneplusProcessHandler = null;
  ArrayList<Integer> mOtherReceiverLocationUids = new ArrayList();
  private List<String> mPkgList = new ArrayList();
  HashSet<Integer> mPowerAdjust = new HashSet();
  IPowerManager mPowerManager = null;
  private PowerManagerInternal mPowerManagerInternal = null;
  private PowerManagerService mPowerManagerService = null;
  private final BroadcastReceiver mPowerSaveWhitelistReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      OnePlusProcessManager.this.updatePowerSaveWhitelistLocked();
    }
  };
  private ConfigObserver mProcessFreezerConfigObserver;
  private IProcessObserver mProcessObserver = new IProcessObserver.Stub()
  {
    public void onForegroundActivitiesChanged(int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean)
    {
      if (OnePlusProcessManager.DEBUG_DETAIL) {
        Log.e("OnePlusProcessManager", "onForegroundActivitiesChanged  uid =" + paramAnonymousInt2 + " begin");
      }
      if (paramAnonymousBoolean) {
        if ((OnePlusProcessManager.this.mFrontActivityUids.size() != 1) || (OnePlusProcessManager.this.mFrontActivityUids.contains(Integer.valueOf(paramAnonymousInt2))))
        {
          OnePlusProcessManager.this.mFrontActivityUids.clear();
          OnePlusProcessManager.this.mFrontActivityUids.remove(Integer.valueOf(paramAnonymousInt2));
          OnePlusProcessManager.this.mFrontActivityUids.add(Integer.valueOf(paramAnonymousInt2));
        }
      }
      Object localObject4;
      ArrayList localArrayList2;
      label543:
      synchronized (OnePlusProcessManager.this.mRulesLock)
      {
        localObject4 = (ArrayList)OnePlusProcessManager.this.mUidPidState.get(Integer.valueOf(paramAnonymousInt2).intValue());
        if ((localObject4 != null) && (((ArrayList)localObject4).size() >= 0) && (paramAnonymousInt2 >= 10000))
        {
          if (!paramAnonymousBoolean) {
            break label543;
          }
          synchronized (OnePlusProcessManager.mLoactionFailUidsCount)
          {
            OnePlusProcessManager.mLoactionFailUidsCount.remove(Integer.valueOf(paramAnonymousInt2));
            synchronized (OnePlusProcessManager.mSuspendFailUidsCount)
            {
              OnePlusProcessManager.mSuspendFailUidsCount.remove(Integer.valueOf(paramAnonymousInt2));
              OnePlusProcessManager.-get19(OnePlusProcessManager.this).removeMessages(paramAnonymousInt2 - 10000);
              OnePlusProcessManager.-wrap1(OnePlusProcessManager.-get15(), paramAnonymousInt2, "onForegroundActivitiesChanged");
              OnePlusProcessManager.-wrap13(OnePlusProcessManager.this, paramAnonymousInt2);
            }
          }
        }
      }
    }
    
    public void onProcessDied(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      synchronized (OnePlusProcessManager.this.mRulesLock)
      {
        ArrayList localArrayList = (ArrayList)OnePlusProcessManager.this.mUidPidState.get(paramAnonymousInt2);
        OnePlusProcessManager.resumeProcessByUID_out_Delay(paramAnonymousInt2, "onProcessDied", 2);
        if (localArrayList != null)
        {
          localArrayList.remove(Integer.valueOf(paramAnonymousInt1));
          if (localArrayList.size() <= 0) {
            OnePlusProcessManager.this.mUidPidState.remove(paramAnonymousInt2);
          }
        }
        return;
      }
    }
    
    public void onProcessStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
  };
  private Handler mResumeProcessHandler = null;
  final SparseArray<ArrayMap<String, Integer>> mResumeUidBroadcasts = new SparseArray();
  final Object mRulesLock = new Object();
  BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (OnePlusProcessManager.DEBUG) {
        Log.e("OnePlusProcessManager", "mScreenStateReceiver: action=" + paramAnonymousContext);
      }
      if ("android.intent.action.SCREEN_OFF".equals(paramAnonymousContext))
      {
        OnePlusProcessManager.-wrap10(OnePlusProcessManager.this);
        OnePlusProcessManager.mAudioUids.clear();
        OnePlusProcessManager.setScreenState(false);
        OnePlusProcessManager.mScreen_ON_ING = false;
        OnePlusProcessManager.-get7().setExact(1, System.currentTimeMillis() + OnePlusProcessManager.screenOffCheckDelayTime, OnePlusProcessManager.-get18());
      }
      do
      {
        do
        {
          return;
          if ("android.intent.action.SCREEN_ON".equals(paramAnonymousContext))
          {
            OnePlusProcessManager.-wrap10(OnePlusProcessManager.this);
            OnePlusProcessManager.mAudioUids.clear();
            OnePlusProcessManager.updateScreenState(true);
            OnePlusProcessManager.-get7().cancel(OnePlusProcessManager.-get18());
            return;
          }
          if (OnePlusProcessManager.-get0().equals(paramAnonymousContext))
          {
            OnePlusProcessManager.-wrap8(OnePlusProcessManager.this);
            OnePlusProcessManager.-wrap17(OnePlusProcessManager.this);
            return;
          }
        } while (!"android.intent.action.BATTERY_CHANGED".equals(paramAnonymousContext));
        if (OnePlusProcessManager.-get6()) {
          return;
        }
        if (paramAnonymousIntent.getIntExtra("plugged", 0) != 0) {}
        for (boolean bool = true;; bool = false)
        {
          OnePlusProcessManager.-set1(bool);
          if (!OnePlusProcessManager.-get12()) {
            break;
          }
          OnePlusProcessManager.resumeAllProcess("mCharging");
          OnePlusProcessManager.-set3(true);
          return;
        }
      } while (!OnePlusProcessManager.-get17());
      OnePlusProcessManager.-set3(false);
      paramAnonymousContext = OnePlusProcessManager.this.mRulesLock;
      int i = 0;
      try
      {
        while (i < OnePlusProcessManager.this.mUidPidState.size())
        {
          int j = OnePlusProcessManager.this.mUidPidState.keyAt(i);
          OnePlusProcessManager.-wrap14(OnePlusProcessManager.this, Integer.valueOf(j).intValue(), OnePlusProcessManager.suspendUidDelayTime);
          i += 1;
        }
        return;
      }
      finally
      {
        paramAnonymousIntent = finally;
        throw paramAnonymousIntent;
      }
    }
  };
  private SensorManager mSensorManager;
  private SettingsObserver mSettingsObserver;
  private BroadcastReceiver mSimReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        OnePlusProcessManager.-wrap0(OnePlusProcessManager.this);
      }
    }
  };
  ArrayList<Integer> mStatusLocationUids = new ArrayList();
  private Handler mSuspendProcessHandler = null;
  private HashMap<String, String> mSysCfgMapOnlineConifg = new HashMap();
  private HandlerThread mThread;
  private final IUidObserver mUidObserver = new IUidObserver.Stub()
  {
    public void onUidActive(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidGone(int paramAnonymousInt)
      throws RemoteException
    {
      synchronized (OnePlusProcessManager.this.mRulesLock)
      {
        OnePlusProcessManager.this.removeUidStateLocked(paramAnonymousInt);
      }
      synchronized (OnePlusProcessManager.mCanFrozenUids)
      {
        OnePlusProcessManager.mCanFrozenUids.remove(Integer.valueOf(paramAnonymousInt));
        return;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    
    public void onUidIdle(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidStateChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {
      synchronized (OnePlusProcessManager.this.mRulesLock)
      {
        OnePlusProcessManager.this.updateUidStateLocked(paramAnonymousInt1, paramAnonymousInt2);
        return;
      }
    }
  };
  final SparseArray<ArrayList<Integer>> mUidPidState = new SparseArray();
  final SparseIntArray mUidState = new SparseIntArray();
  private PowerManager.WakeLock mWakeLockFrozen;
  private WindowManagerService mWindowManager = null;
  PowerManager pm = null;
  ContentResolver resolver = null;
  
  static
  {
    mOnePlusProcessManager = null;
    mActivityManager = null;
    mBPMStatus = false;
    mCharging = false;
    mResumeFirst = false;
    mBPMStatusing = false;
    mDeviceIdleService = null;
    mSuspendUids = new ArrayList();
    mCanFrozenUids = new ArrayList();
    mSuspendFailUidsCount = new ArrayMap();
    mLoactionFailUidsCount = new ArrayMap();
    mNotification = null;
    mLock = new Object();
    mScreenLock = new Object();
    mLockProcess = new Object();
    mWhiteLock = new Object();
    mProcess = new Object();
    SCREEN_OFF_INTENT = "com.oneplus.android.screenOffCheckProcessState";
    mWakeLock = new Object();
    mWhiteUids = new int[0];
    mPackageManager = null;
    lm = null;
    mMessageLock = new Object();
    mScreen_ON = true;
    mScreen_ON_ING = true;
    mScreenOffCount = 0;
    screenOffCheckDelayTime = 300000L;
    suspendUidDelayTime = 60000L;
    computeTrafficTime = 30000L;
    mUidTraffic = new ArrayMap();
    mTrafficUids = new HashSet();
    mAudioUids = new HashSet();
    mTrafficUidsCount = new ArrayMap();
    sInputMethodUid = 0;
    mTrafficeWhiteUids = new ArrayList();
    mDoThawedUids = new HashSet();
    mAdjustUids = new HashSet();
    mNotAllowSensorUids = new ArrayList();
    mUnFrozenReasonUids = new ArrayMap();
    isSuppoerted = false;
    isUsing = true;
    isAlarmAdjust = false;
    mPendingUid = 0;
    mUidPackageNames = new ArrayMap();
    CONFIG_NAME = "ProcessFreezer";
    isChargeringCloseForzen = false;
  }
  
  protected OnePlusProcessManager(ActivityManagerService paramActivityManagerService)
  {
    isSuppoerted = paramActivityManagerService.mContext.getPackageManager().hasSystemFeature("oem.background.control");
    isUsing = SystemProperties.getBoolean("persist.sys.cgroup.using", true);
    isAlarmAdjust = SystemProperties.getBoolean("persist.sys.alarmAdjust.using", true);
    if (!isUsing) {
      isSuppoerted = false;
    }
    if (!isSuppoerted)
    {
      Slog.i("OnePlusProcessManager", "OnePlusProcessManager isSuppoerted =" + isSuppoerted);
      return;
    }
    mActivityManager = paramActivityManagerService;
    this.mContext = paramActivityManagerService.mContext;
    Slog.i("OnePlusProcessManager", "OnePlusProcessManager VERSION = v15112601");
    SystemProperties.set("sys.cgroup.version", "v15112601");
    DEBUG = SystemProperties.getBoolean("persist.sys.cgroup.debug", false);
    DEBUG_DETAIL = SystemProperties.getBoolean("persist.sys.cgroup.debugdetail", false);
    this.resolver = this.mContext.getContentResolver();
    registerSimReceiver();
    paramActivityManagerService = new HandlerThread("OneplusProcessThread");
    paramActivityManagerService.start();
    this.mOneplusProcessHandler = new oneplusProcessHandler(paramActivityManagerService.getLooper());
    this.mOneplusProcessHandler.sendEmptyMessageDelayed(111, 5000L);
    this.mOneplusProcessHandler.sendEmptyMessageDelayed(112, 3000L);
    paramActivityManagerService = new HandlerThread("SuspenPorcessThread");
    paramActivityManagerService.start();
    HandlerThread localHandlerThread = new HandlerThread("ResumeProcessThread");
    localHandlerThread.start();
    this.mSuspendProcessHandler = new suspendProcessHandler(paramActivityManagerService.getLooper());
    this.mResumeProcessHandler = new resumeProcessHandler(localHandlerThread.getLooper());
    prepareBPMConfigFiles();
    mNotification = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    mAlarmManager = (AlarmManager)mActivityManager.mContext.getSystemService("alarm");
    paramActivityManagerService = new IntentFilter();
    mScreenOffIntent = new Intent(SCREEN_OFF_INTENT);
    paramActivityManagerService.addAction("android.intent.action.SCREEN_OFF");
    paramActivityManagerService.addAction("android.intent.action.SCREEN_ON");
    paramActivityManagerService.addAction(SCREEN_OFF_INTENT);
    paramActivityManagerService.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
    paramActivityManagerService.addAction("android.intent.action.BATTERY_CHANGED");
    mScreenOffCheckIntent = PendingIntent.getBroadcast(mActivityManager.mContext, 0, mScreenOffIntent, 0);
    mActivityManager.mContext.registerReceiver(this.mScreenStateReceiver, paramActivityManagerService);
    this.pm = ((PowerManager)mActivityManager.mContext.getSystemService("power"));
    this.mWakeLockFrozen = this.pm.newWakeLock(1, "frozenApp");
    mActivityManager.registerUidObserver(this.mUidObserver, 3);
    mDeviceIdleService = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    paramActivityManagerService = new IntentFilter("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
    mActivityManager.mContext.registerReceiver(this.mPowerSaveWhitelistReceiver, paramActivityManagerService);
    updatePowerSaveWhitelistLocked();
    mPackageManager = mActivityManager.mContext.getPackageManager();
    lm = (LocationManagerService)ServiceManager.getService("location");
    this.mPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    this.mSensorManager = ((SensorManager)mActivityManager.mContext.getSystemService("sensor"));
    this.appTracker = new AppTracker(mActivityManager.mContext);
    this.mAlarm = IAlarmManager.Stub.asInterface(ServiceManager.getService("alarm"));
    initialOnlineConfig();
    isChargeringCloseForzen = SystemProperties.getBoolean("persist.sys.cgroup.charger", true);
    this.mSettingsObserver = new SettingsObserver(this.mOneplusProcessHandler);
    this.resolver.registerContentObserver(Settings.System.getUriFor("doze_mode_policy"), false, this.mSettingsObserver, -1);
  }
  
  private void add3rdAppProcessState(int paramInt1, int paramInt2)
  {
    if (UserHandle.isApp(paramInt1)) {}
    synchronized (this.mRulesLock)
    {
      ArrayList localArrayList2 = (ArrayList)this.mUidPidState.get(paramInt1);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.mUidPidState.put(paramInt1, localArrayList1);
      }
      localArrayList1.remove(Integer.valueOf(paramInt2));
      localArrayList1.add(Integer.valueOf(paramInt2));
      return;
    }
  }
  
  private static void addDozeWhiteList(String paramString)
  {
    if (mDeviceIdleService != null) {
      try
      {
        if (DEBUG_ONEPLUS) {
          Slog.d("OnePlusProcessManager", "addDozeWhiteList: " + paramString);
        }
        mDeviceIdleService.addPowerSaveWhitelistApp(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.w("OnePlusProcessManager", "Falied to add package to doze whitelist");
        return;
      }
    }
    Slog.w("OnePlusProcessManager", "Cannot get DeviceIdleController");
  }
  
  private boolean addLocationFailCount(int paramInt)
  {
    synchronized (mLoactionFailUidsCount)
    {
      Integer localInteger = (Integer)mLoactionFailUidsCount.get(Integer.valueOf(paramInt));
      if ((localInteger != null) && (localInteger.intValue() != 0))
      {
        mLoactionFailUidsCount.remove(Integer.valueOf(paramInt));
        ArrayMap localArrayMap2 = mLoactionFailUidsCount;
        int i = localInteger.intValue() + 1;
        localInteger = Integer.valueOf(i);
        localArrayMap2.put(Integer.valueOf(paramInt), Integer.valueOf(i));
        paramInt = localInteger.intValue();
        if (paramInt >= 4) {
          return true;
        }
      }
      else
      {
        localInteger = Integer.valueOf(1);
        mLoactionFailUidsCount.put(Integer.valueOf(paramInt), Integer.valueOf(1));
      }
    }
    return false;
  }
  
  static final void addPidToCgroupTasksWithJudge(ProcessRecord paramProcessRecord, int paramInt)
  {
    if (!isSuppoerted) {
      return;
    }
    if ((paramProcessRecord != null) && (UserHandle.isApp(paramProcessRecord.uid)) && (paramProcessRecord.info != null) && ((paramProcessRecord.info.flags & 0x81) == 0) && (mOnePlusProcessManager != null) && (mOnePlusProcessManager.mOneplusProcessHandler != null) && (paramInt > 0))
    {
      mOnePlusProcessManager.add3rdAppProcessState(paramProcessRecord.uid, paramInt);
      if (mBPMStatus)
      {
        mOnePlusProcessManager.scheduleResumeUid(paramProcessRecord.uid, "addPidToCgroupTasksWithJudge");
        Message localMessage = Message.obtain();
        localMessage.what = 115;
        localMessage.arg1 = paramInt;
        localMessage.arg2 = paramProcessRecord.uid;
        mOnePlusProcessManager.mOneplusProcessHandler.sendMessage(localMessage);
        mUidPackageNames.remove(Integer.valueOf(paramProcessRecord.uid));
        mUidPackageNames.put(Integer.valueOf(paramProcessRecord.uid), paramProcessRecord.info.packageName);
      }
    }
  }
  
  private long addSuspendDelayCount(int paramInt)
  {
    synchronized (mSuspendFailUidsCount)
    {
      Integer localInteger = (Integer)mSuspendFailUidsCount.get(Integer.valueOf(paramInt));
      if ((localInteger != null) && (localInteger.intValue() != 0))
      {
        mSuspendFailUidsCount.remove(Integer.valueOf(paramInt));
        Object localObject2 = mSuspendFailUidsCount;
        int i = localInteger.intValue() + 1;
        localInteger = Integer.valueOf(i);
        ((ArrayMap)localObject2).put(Integer.valueOf(paramInt), Integer.valueOf(i));
        localObject2 = localInteger;
        if (localInteger.intValue() >= 30) {
          localObject2 = Integer.valueOf(30);
        }
        long l1 = ((Integer)localObject2).intValue();
        long l2 = suspendUidDelayTime;
        return l1 * l2;
      }
      localInteger = Integer.valueOf(1);
      mSuspendFailUidsCount.put(Integer.valueOf(paramInt), Integer.valueOf(1));
    }
  }
  
  private void addTempWhiteList(int paramInt)
  {
    String str = getPackageNameForUid(paramInt);
    try
    {
      if ((!str.equals("")) && (mDeviceIdleService != null))
      {
        if (mDeviceIdleService.isPowerSaveWhitelistApp(str)) {
          return;
        }
        mTrafficeWhiteUids.add(str);
        addDozeWhiteList(str);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("OnePlusProcessManager", "Falied to add package to doze whitelist");
    }
  }
  
  private void adjustSuspendMessage(int paramInt, String arg2, long paramLong)
  {
    if ((mPendingUid == paramInt) || (mDoThawedUids.contains(Integer.valueOf(paramInt))))
    {
      if (DEBUG) {
        Slog.d("OnePlusProcessManager", "adjustSuspendMessage message" + paramInt + " why = " + ???);
      }
      scheduleSuspendUid(paramInt, paramLong);
    }
    synchronized (mAdjustUids)
    {
      mAdjustUids.remove(Integer.valueOf(paramInt));
      mAdjustUids.add(Integer.valueOf(paramInt));
      return;
    }
  }
  
  private double canSuspendUid(int paramInt)
  {
    if (!UserHandle.isApp(paramInt)) {
      return this.SUSPEND_FAIL_NOTRY;
    }
    synchronized (this.mRulesLock)
    {
      ArrayList localArrayList = (ArrayList)this.mUidPidState.get(Integer.valueOf(paramInt).intValue());
      if ((localArrayList == null) || (localArrayList.size() <= 0))
      {
        double d = this.SUSPEND_FAIL_NOTRY;
        return d;
      }
      if (this.mFrontActivityUids.contains(Integer.valueOf(paramInt)))
      {
        Slog.d("OnePlusProcessManager", "check mFrontActivityUids fail " + paramInt);
        return this.SUSPEND_FAIL_NOTRY;
      }
    }
    if (checkWhiteUid(paramInt))
    {
      Slog.d("OnePlusProcessManager", "checkWhiteUid fail " + paramInt);
      return this.SUSPEND_FAIL_NOTRY;
    }
    if (checkWhitePackageUid(paramInt))
    {
      Slog.d("OnePlusProcessManager", "checkWhitePackageUid fail " + paramInt);
      return this.SUSPEND_FAIL_NOTRY;
    }
    if (this.mUidState.get(paramInt, 16) <= 2)
    {
      Slog.d("OnePlusProcessManager", "checkWhiteUid import top " + paramInt);
      return this.SUSPEND_FAIL_NOTRY;
    }
    if (this.mFrontWindowTouchUids.contains(Integer.valueOf(paramInt)))
    {
      Slog.d("OnePlusProcessManager", "check mFrontWindowTouchUids fail " + paramInt);
      return this.SUSPEND_FAIL_LONG;
    }
    if (sInputMethodUid == paramInt) {
      return this.SUSPEND_FAIL_LONG;
    }
    if (this.mSuspendProcessHandler.hasMessages(paramInt - 10000))
    {
      Slog.d("OnePlusProcessManager", "suspend hanlder already hasMessage " + paramInt);
      return this.SUSPEND_FAIL_DEFAULT;
    }
    if (checkActiveAudioUids(paramInt))
    {
      Slog.d("OnePlusProcessManager", "checkActiveAudioUids fail " + paramInt);
      return this.SUSPEND_FAIL_LONG;
    }
    if (mTrafficUids.contains(Integer.valueOf(paramInt)))
    {
      Slog.d("OnePlusProcessManager", "checkTrafficUid fail " + paramInt);
      return this.SUSPEND_FAIL_LONG;
    }
    if ((!mNotAllowSensorUids.contains(Integer.valueOf(paramInt))) && (checkActiveSensor(paramInt)))
    {
      Slog.d("OnePlusProcessManager", "checkActiveSensor fail " + paramInt);
      return this.SUSPEND_FAIL_LONG;
    }
    if (mAdjustUids.contains(Integer.valueOf(paramInt)))
    {
      Slog.d("OnePlusProcessManager", "check mAdjustUids fail " + paramInt);
      return this.SUSPEND_FAIL_DEFAULT;
    }
    if (this.mImportantUids.contains(Integer.valueOf(paramInt)))
    {
      Slog.d("OnePlusProcessManager", "check mImportantUids fail " + paramInt);
      return this.SUSPEND_FAIL_DEFAULT;
    }
    if (this.mGpsReceiverLocationUids.contains(Integer.valueOf(paramInt)))
    {
      ??? = getPackageNameForUid(paramInt);
      if (!this.mBlackGpsList.contains(???))
      {
        Slog.d("OnePlusProcessManager", "skip suspend " + paramInt + " due to mGpsReceiverLocationUids");
        return this.SUSPEND_FAIL_DEFAULT;
      }
      Slog.d("OnePlusProcessManager", "mGpsReceiverUids plan to suspend " + paramInt);
    }
    if ((this.mOtherReceiverLocationUids.contains(Integer.valueOf(paramInt))) && (!addLocationFailCount(paramInt))) {
      return this.SUSPEND_FAIL_DEFAULT;
    }
    return this.SUSPEND_OK;
  }
  
  private void cancelNotificationsForUid(final int paramInt)
  {
    mActivityManager.mHandler.post(new Runnable()
    {
      public void run()
      {
        OnePlusProcessManager.cancelNotificationsWithPkg(OnePlusProcessManager.-wrap5(OnePlusProcessManager.this, paramInt), -1);
      }
    });
  }
  
  public static void cancelNotificationsWithPkg(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.d("OnePlusProcessManager", "cancelNotificationsWithPkg():pkg=" + paramString);
    }
    try
    {
      if (mNotification == null) {
        return;
      }
      mNotification.cancelAllNotifications(paramString, paramInt);
      return;
    }
    catch (Exception localException)
    {
      Slog.d("OnePlusProcessManager", "Exception---->cancelNotificationsWithPkg():pkg=" + paramString);
      localException.printStackTrace();
    }
  }
  
  private List<Integer> changeIntToList(int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      localArrayList.add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    return localArrayList;
  }
  
  private boolean checkActiveAudioUids(int paramInt)
  {
    String[] arrayOfString = getActiveAudioUids();
    if (DEBUG) {
      Slog.d("OnePlusProcessManager", "ActiveAudioUid = " + Arrays.toString(arrayOfString));
    }
    if (arrayOfString != null)
    {
      int i = 0;
      if (i < arrayOfString.length)
      {
        if (arrayOfString[i].isEmpty()) {}
        int j;
        do
        {
          i += 1;
          break;
          j = Integer.valueOf(arrayOfString[i]).intValue();
          if (j == 0) {
            return false;
          }
          if (DEBUG) {
            Slog.d("OnePlusProcessManager", "WakeLock owner uid: " + paramInt);
          }
        } while (j != paramInt);
        return true;
      }
    }
    return false;
  }
  
  private boolean checkActiveSensor(int paramInt)
  {
    Object localObject = this.mSensorManager.getActiveSensorList();
    if (DEBUG) {
      Slog.d("OnePlusProcessManager", "checkActiveSensor activeSensor= " + (String)localObject);
    }
    localObject = parseActiveAudioUidsStr((String)localObject);
    if (localObject != null)
    {
      int i = 0;
      if (i < localObject.length)
      {
        if (localObject[i].isEmpty()) {}
        int j;
        do
        {
          i += 1;
          break;
          j = Integer.valueOf(localObject[i]).intValue();
          if (j == 0) {
            return false;
          }
        } while (j != paramInt);
        return true;
      }
    }
    return false;
  }
  
  static final void checkAppInLaunchingProviders(ProcessRecord paramProcessRecord)
  {
    Slog.d("OnePlusProcessManager", paramProcessRecord + " died but not restart......");
    Object localObject;
    if (!paramProcessRecord.pubProviders.isEmpty())
    {
      localObject = paramProcessRecord.pubProviders.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)((Iterator)localObject).next();
        mActivityManager.removeDyingProviderLocked(paramProcessRecord, localContentProviderRecord, true);
        localContentProviderRecord.provider = null;
        localContentProviderRecord.proc = null;
      }
      paramProcessRecord.pubProviders.clear();
    }
    int j = mActivityManager.mLaunchingProviders.size();
    int i = 0;
    while (i < j)
    {
      localObject = (ContentProviderRecord)mActivityManager.mLaunchingProviders.get(i);
      if (((ContentProviderRecord)localObject).launchingApp == paramProcessRecord)
      {
        mActivityManager.removeDyingProviderLocked(paramProcessRecord, (ContentProviderRecord)localObject, true);
        j = mActivityManager.mLaunchingProviders.size();
      }
      i += 1;
    }
  }
  
  static final boolean checkBroadcast(BroadcastQueue paramBroadcastQueue, ProcessRecord paramProcessRecord, BroadcastRecord paramBroadcastRecord)
    throws RemoteException
  {
    if (!isSuppoerted) {
      return true;
    }
    if (!mBPMStatus) {
      return true;
    }
    if (mOnePlusProcessManager == null) {
      return true;
    }
    if (!mOnePlusProcessManager.skipBroadcast(paramProcessRecord, paramBroadcastRecord, paramBroadcastRecord.ordered)) {
      return true;
    }
    if (DEBUG_DETAIL) {
      Slog.d("OnePlusProcessManager", "BPM skip broadcast " + paramBroadcastRecord.intent.toString() + " to " + paramProcessRecord + " (pid=" + paramProcessRecord.pid + " sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")" + " is ordered " + paramBroadcastRecord.ordered);
    }
    paramBroadcastQueue.skipCurrentReceiverLocked(paramProcessRecord);
    return false;
  }
  
  static final boolean checkBroadcastIsPackageCanStart(BroadcastQueue paramBroadcastQueue, ResolveInfo paramResolveInfo, BroadcastRecord paramBroadcastRecord)
  {
    if (!isSuppoerted) {
      return false;
    }
    if (!mBPMStatus) {
      return false;
    }
    if (mOnePlusProcessManager == null) {
      return false;
    }
    if ((paramResolveInfo == null) || (paramResolveInfo.activityInfo == null)) {
      return false;
    }
    ApplicationInfo localApplicationInfo = paramResolveInfo.activityInfo.applicationInfo;
    if (localApplicationInfo == null) {
      return false;
    }
    if ((localApplicationInfo.flags & 0x81) != 0) {
      return false;
    }
    if ((paramBroadcastRecord.callerApp != null) && ((paramBroadcastRecord.callerApp.info.flags & 0x81) == 0)) {
      return false;
    }
    if (mOnePlusProcessManager.checkBroadcastIsPackageNotCanStartImpl(localApplicationInfo, paramBroadcastRecord))
    {
      if (DEBUG_DETAIL) {
        Slog.w("OnePlusProcessManager", "Do not want to launch app " + paramResolveInfo.activityInfo.applicationInfo.packageName + "/" + paramResolveInfo.activityInfo.applicationInfo.uid + " for broadcast " + paramBroadcastRecord.intent + " callUid:" + paramBroadcastRecord.callingUid + " callPid:" + paramBroadcastRecord.callingPid);
      }
      paramBroadcastQueue.finishReceiverLocked(paramBroadcastRecord, paramBroadcastRecord.resultCode, paramBroadcastRecord.resultData, paramBroadcastRecord.resultExtras, paramBroadcastRecord.resultAbort, true);
      paramBroadcastQueue.scheduleBroadcastsLocked();
      return true;
    }
    return false;
  }
  
  private boolean checkBroadcastIsPackageNotCanStartImpl(ApplicationInfo paramApplicationInfo, BroadcastRecord paramBroadcastRecord)
  {
    paramBroadcastRecord = paramBroadcastRecord.intent.getAction();
    int j = 0;
    if (paramBroadcastRecord == null) {
      return false;
    }
    Iterator localIterator = this.mBrdList.iterator();
    do
    {
      i = j;
      if (!localIterator.hasNext()) {
        break;
      }
    } while (!((String)localIterator.next()).equals(paramBroadcastRecord));
    int i = 1;
    if (i != 0)
    {
      if (checkWhiteUid(paramApplicationInfo.uid)) {
        return false;
      }
      if (checkWhitePackageUid(paramApplicationInfo.uid)) {
        return false;
      }
    }
    return (!this.mFrontActivityUids.contains(Integer.valueOf(paramApplicationInfo.uid))) && (!this.mFrontWindowTouchUids.contains(Integer.valueOf(paramApplicationInfo.uid)));
  }
  
  static final boolean checkProcessCanRestart(ProcessRecord paramProcessRecord)
  {
    if (!isSuppoerted) {
      return true;
    }
    if (paramProcessRecord.killedByAm) {
      return true;
    }
    if (mOnePlusProcessManager != null) {
      return mOnePlusProcessManager.checkProcessCanRestartImpl(paramProcessRecord);
    }
    return true;
  }
  
  private boolean checkProcessCanRestartImpl(ProcessRecord paramProcessRecord)
  {
    if (!mBPMStatus) {
      return true;
    }
    if (paramProcessRecord == null) {
      return true;
    }
    if (paramProcessRecord.curAdj <= 200) {
      return true;
    }
    if ((paramProcessRecord.info.flags & 0x81) != 0) {
      return true;
    }
    return checkProcessRecord(paramProcessRecord);
  }
  
  private boolean checkProcessRecord(ProcessRecord paramProcessRecord)
  {
    if (paramProcessRecord == null) {
      return false;
    }
    if (paramProcessRecord.uid < 10000)
    {
      if (DEBUG_DETAIL) {
        Slog.v("OnePlusProcessManager", " app.uid < Process.FIRST_APPLICATION_UID: " + paramProcessRecord);
      }
      return true;
    }
    if (isHomeProcess(paramProcessRecord)) {
      return true;
    }
    if (checkWhiteUid(paramProcessRecord.uid))
    {
      if (DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "checkProcessRecord ->#return true, it's white app ");
      }
      return true;
    }
    synchronized (this.mPkgList)
    {
      Iterator localIterator = this.mPkgList.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = isInclude((String)localIterator.next(), paramProcessRecord.getPackageList());
        if (bool) {
          return true;
        }
      }
      return false;
    }
  }
  
  static final boolean checkProcessWhileBroadcastTimeout(ProcessRecord paramProcessRecord)
  {
    if (!isSuppoerted) {
      return false;
    }
    if (!mBPMStatus) {
      return false;
    }
    if (mOnePlusProcessManager == null) {
      return false;
    }
    if (DEBUG_DETAIL) {
      Slog.d("OnePlusProcessManager", "checkProcessWhileBroadcastTimeout(): " + paramProcessRecord);
    }
    if (mOnePlusProcessManager.scheduleResumeUid(paramProcessRecord.uid, "checkProcessWhileBroadcastTimeout "))
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("OnePlusProcessManager", "checkProcessWhileBroadcastTimeout(): and resumeUid" + paramProcessRecord);
      }
      return false;
    }
    return false;
  }
  
  static final boolean checkProcessWhileTimeout(ProcessRecord paramProcessRecord)
  {
    if (!isSuppoerted) {
      return false;
    }
    if (!mBPMStatus) {
      return false;
    }
    if (mOnePlusProcessManager == null) {
      return false;
    }
    if (DEBUG_DETAIL) {
      Slog.d("OnePlusProcessManager", "checkProcessWhileTimeout(): " + paramProcessRecord);
    }
    if (mOnePlusProcessManager.scheduleResumeUid(paramProcessRecord.uid, "checkProcessWhileTimeout"))
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("OnePlusProcessManager", "checkProcessWhileTimeout(): and resumeUid" + paramProcessRecord);
      }
      return false;
    }
    return false;
  }
  
  private boolean checkVersion()
  {
    HashMap localHashMap1 = loadXmlLocked("/data/data_bpm/version.xml");
    HashMap localHashMap2 = loadXmlLocked("/system/bpm/version.xml");
    if (localHashMap2 == null) {
      return false;
    }
    int j = Integer.valueOf((String)localHashMap2.get("version")).intValue();
    if (localHashMap1 == null) {}
    for (int i = 0;; i = Integer.valueOf((String)localHashMap1.get("version")).intValue())
    {
      Slog.d("OnePlusProcessManager", "checkVersion  sv=" + j + " dv =" + i);
      if (j <= i) {
        break;
      }
      return true;
    }
    return false;
  }
  
  private boolean checkWhitePackageUid(int paramInt)
  {
    String str = getPackageNameForUid(paramInt);
    List localList = this.mPkgList;
    if (str != null) {}
    try
    {
      boolean bool = this.mPkgList.contains(str);
      if (bool) {
        return true;
      }
      if ((str != null) && (str.contains("com.cttl"))) {
        return true;
      }
    }
    finally {}
    return false;
  }
  
  private boolean checkWhiteUid(int paramInt)
  {
    synchronized (mWhiteLock)
    {
      if ((mWhiteUids != null) && (mWhiteUids.length != 0))
      {
        int i = 0;
        while (i < mWhiteUids.length)
        {
          if (mWhiteUids[i] == paramInt)
          {
            if (DEBUG_DETAIL) {
              Slog.d("OnePlusProcessManager", "checkWhiteUid ->#return true, it's white app ");
            }
            return true;
          }
          i += 1;
        }
      }
      return false;
    }
  }
  
  private int computeUidTraffic(int paramInt)
  {
    for (;;)
    {
      synchronized (mUidTraffic)
      {
        Object localObject1 = (Integer)mTrafficUidsCount.get(Integer.valueOf(paramInt));
        if (DEBUG_ONEPLUS) {
          Log.e("OnePlusProcessManager", "computeUidTraffic begin uid =" + paramInt);
        }
        if (mUidTraffic.containsKey(Integer.valueOf(paramInt)))
        {
          if (checkTrafficUid(paramInt))
          {
            Object localObject4 = new Traffic(SystemClock.elapsedRealtime(), TrafficStats.getUidTxBytes(paramInt), TrafficStats.getUidRxBytes(paramInt));
            mUidTraffic.remove(Integer.valueOf(paramInt));
            mUidTraffic.put(Integer.valueOf(paramInt), localObject4);
            mTrafficUids.remove(Integer.valueOf(paramInt));
            mTrafficUids.add(Integer.valueOf(paramInt));
            if (!mScreen_ON) {
              addTempWhiteList(paramInt);
            }
            localObject4 = localObject1;
            if (localObject1 != null)
            {
              localObject4 = localObject1;
              if (((Integer)localObject1).intValue() != 0)
              {
                mTrafficUidsCount.remove(Integer.valueOf(paramInt));
                ArrayMap localArrayMap2 = mTrafficUidsCount;
                int i = ((Integer)localObject1).intValue() + 1;
                localObject4 = Integer.valueOf(i);
                localArrayMap2.put(Integer.valueOf(paramInt), Integer.valueOf(i));
              }
            }
            localObject1 = localObject4;
            if (DEBUG_ONEPLUS)
            {
              Log.e("OnePlusProcessManager", "computeUidTraffic uid =" + paramInt);
              localObject1 = localObject4;
            }
            localObject4 = localObject1;
            if (localObject1 == null) {
              localObject4 = Integer.valueOf(0);
            }
            paramInt = ((Integer)localObject4).intValue();
            return paramInt;
          }
          if (DEBUG_ONEPLUS) {
            Log.e("OnePlusProcessManager", "computeUidTraffic not check  uid =" + paramInt);
          }
          mUidTraffic.remove(Integer.valueOf(paramInt));
          mTrafficUids.remove(Integer.valueOf(paramInt));
          mTrafficUidsCount.remove(Integer.valueOf(paramInt));
          if ((mScreen_ON) || (mAudioUids.contains(Integer.valueOf(paramInt))))
          {
            if (mOnePlusProcessManager != null) {
              mOnePlusProcessManager.scheduleSuspendUid(paramInt, suspendUidDelayTime / 2L);
            }
            localObject1 = Integer.valueOf(0);
            continue;
          }
          localObject1 = getPackageNameForUid(paramInt);
          if (!mTrafficeWhiteUids.contains(localObject1)) {
            continue;
          }
          removeDozeWhiteList((String)localObject1);
          mTrafficeWhiteUids.remove(localObject1);
        }
      }
      Object localObject3 = new Traffic(SystemClock.elapsedRealtime(), TrafficStats.getUidTxBytes(paramInt), TrafficStats.getUidRxBytes(paramInt));
      mUidTraffic.put(Integer.valueOf(paramInt), localObject3);
      mTrafficUidsCount.remove(Integer.valueOf(paramInt));
      localObject3 = Integer.valueOf(1);
      mTrafficUidsCount.put(Integer.valueOf(paramInt), Integer.valueOf(1));
    }
  }
  
  public static void continueSuspendUid(int paramInt)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus) {
      return;
    }
    if (mOnePlusProcessManager == null) {
      return;
    }
    if (!UserHandle.isApp(paramInt)) {
      return;
    }
    if ((!mAdjustUids.contains(Integer.valueOf(paramInt))) && (mDoThawedUids.contains(Integer.valueOf(paramInt))))
    {
      if (DEBUG) {
        Slog.d("OnePlusProcessManager", "continueSuspendUid =" + paramInt);
      }
      if (mPendingUid != paramInt)
      {
        mOnePlusProcessManager.mSuspendProcessHandler.removeMessages(paramInt - 10000);
        mOnePlusProcessManager.scheduleSuspendUid(paramInt, 1000L);
      }
    }
  }
  
  private void copyFile(String paramString1, String paramString2, boolean paramBoolean)
    throws IOException
  {
    File localFile = new File(paramString2);
    if ((!localFile.exists()) || (paramBoolean))
    {
      Slog.d("OnePlusProcessManager", "copyFile():fromFile=" + paramString1 + ", toFile=" + paramString2);
      FileUtils.copyFile(new File(paramString1), localFile);
      return;
    }
  }
  
  private String[] getActiveAudioUids()
  {
    if (this.mAudioManager == null) {
      this.mAudioManager = ((AudioManager)mActivityManager.mContext.getSystemService("audio"));
    }
    if (this.mAudioManager != null) {}
    for (String str = this.mAudioManager.getParameters("get_uid");; str = ":0")
    {
      if (DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "getActiveAudioPids():pids=" + str);
      }
      return parseActiveAudioUidsStr(str);
    }
  }
  
  public static boolean getBPMEnable_out()
  {
    if (mOnePlusProcessManager == null)
    {
      Slog.e("OnePlusProcessManager", "Fatal: mOnePlusProcessManager = null");
      return false;
    }
    return mOnePlusProcessManager.getBPMEnable();
  }
  
  public static final OnePlusProcessManager getInstance(ActivityManagerService paramActivityManagerService)
  {
    if (mOnePlusProcessManager == null) {
      mOnePlusProcessManager = new OnePlusProcessManager(paramActivityManagerService);
    }
    return mOnePlusProcessManager;
  }
  
  private String getPackageNameForUid(int paramInt)
  {
    Object localObject = (String)mUidPackageNames.get(Integer.valueOf(paramInt));
    if ((localObject == null) || (((String)localObject).equals("")))
    {
      if (mPackageManager != null)
      {
        localObject = mPackageManager.getPackagesForUid(paramInt);
        if (localObject != null) {
          paramInt = 0;
        }
      }
    }
    else {
      while (paramInt < localObject.length)
      {
        try
        {
          ApplicationInfo localApplicationInfo = mPackageManager.getApplicationInfo(localObject[paramInt], 0);
          if ((localApplicationInfo == null) || ((localApplicationInfo.flags & 0x1) != 0)) {
            break label91;
          }
          localObject = localObject[paramInt];
          return (String)localObject;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          return "";
        }
        return (String)localObject;
        label91:
        paramInt += 1;
      }
    }
    return "";
  }
  
  private ArrayList<ProcessRecord> getProcessForUid(int paramInt)
  {
    ArrayList localArrayList2;
    synchronized (mLockProcess)
    {
      ArrayList localArrayList1 = new ArrayList();
      localArrayList2 = (ArrayList)this.mUidPidState.clone().get(Integer.valueOf(paramInt).intValue());
      if (localArrayList2 != null)
      {
        paramInt = localArrayList2.size();
        if (paramInt > 0) {}
      }
      else
      {
        return localArrayList1;
      }
      localArrayList2 = (ArrayList)localArrayList2.clone();
      new SparseArray();
    }
    SparseArray localSparseArray;
    synchronized (mActivityManager.mPidsSelfLocked)
    {
      localSparseArray = mActivityManager.mPidsSelfLocked.clone();
      paramInt = 0;
      int i = localArrayList2.size();
      if (paramInt >= i) {}
    }
    return localArrayList;
  }
  
  private ArrayList<Integer> getRelatedUids(int paramInt)
  {
    ArrayList localArrayList1 = getProcessForUid(paramInt);
    ArrayList localArrayList2 = new ArrayList();
    if ((localArrayList1 == null) || (localArrayList1.size() == 0)) {
      return null;
    }
    int i = 0;
    if (i < localArrayList1.size())
    {
      ProcessRecord localProcessRecord = (ProcessRecord)localArrayList1.get(i);
      if (localProcessRecord == null) {}
      for (;;)
      {
        i += 1;
        break;
        if (localProcessRecord.permRequestCount > 0)
        {
          if (DEBUG_ONEPLUS) {
            Slog.d("OnePlusProcessManager", "skip suspend due to requesting perm uid =" + paramInt);
          }
          return null;
        }
        label136:
        Object localObject;
        int j;
        int k;
        if ((localProcessRecord.services != null) && (localProcessRecord.services.size() > 0))
        {
          Iterator localIterator1 = localProcessRecord.services.iterator();
          if (localIterator1.hasNext()) {
            try
            {
              localObject = (ServiceRecord)localIterator1.next();
              if ((localObject == null) || (((ServiceRecord)localObject).connections == null)) {
                break label136;
              }
              j = 0;
              while (j < ((ServiceRecord)localObject).connections.size())
              {
                ArrayList localArrayList3 = (ArrayList)((ServiceRecord)localObject).connections.valueAt(j);
                k = 0;
                if (k < localArrayList3.size())
                {
                  ConnectionRecord localConnectionRecord = (ConnectionRecord)localArrayList3.get(k);
                  int m;
                  if ((localConnectionRecord != null) && (localConnectionRecord.binding != null) && (localProcessRecord.uid != localConnectionRecord.binding.client.uid))
                  {
                    m = localConnectionRecord.binding.client.uid;
                    if (!isUidSuspended(Integer.valueOf(m).intValue())) {
                      break label292;
                    }
                  }
                  for (;;)
                  {
                    k += 1;
                    break;
                    label292:
                    if (canSuspendUid(m) != this.SUSPEND_OK) {
                      break label331;
                    }
                    localArrayList2.remove(Integer.valueOf(m));
                    localArrayList2.add(Integer.valueOf(m));
                  }
                  label331:
                  return null;
                }
                j += 1;
              }
              if (localProcessRecord.pubProviders == null) {
                continue;
              }
            }
            catch (Exception localException1) {}
          }
        }
        if (localProcessRecord.pubProviders.size() > 0)
        {
          j = 0;
          while (j < localProcessRecord.pubProviders.size())
          {
            Iterator localIterator2 = ((ContentProviderRecord)localProcessRecord.pubProviders.valueAt(j)).connections.iterator();
            for (;;)
            {
              if (localIterator2.hasNext()) {
                try
                {
                  localObject = (ContentProviderConnection)localIterator2.next();
                  if ((localObject != null) && (localProcessRecord.uid != ((ContentProviderConnection)localObject).client.uid))
                  {
                    k = ((ContentProviderConnection)localObject).client.uid;
                    if (!isUidSuspended(Integer.valueOf(k).intValue())) {
                      if (canSuspendUid(k) == this.SUSPEND_OK)
                      {
                        localArrayList2.remove(Integer.valueOf(k));
                        localArrayList2.add(Integer.valueOf(k));
                      }
                      else
                      {
                        return null;
                      }
                    }
                  }
                }
                catch (Exception localException2) {}
              }
            }
            j += 1;
          }
        }
      }
    }
    return localArrayList2;
  }
  
  public static boolean getScreenState()
  {
    synchronized (mScreenLock)
    {
      boolean bool = mScreen_ON;
      return bool;
    }
  }
  
  private String[] getSilent_AudioUids()
  {
    if (this.mAudioManager == null) {
      this.mAudioManager = ((AudioManager)mActivityManager.mContext.getSystemService("audio"));
    }
    if (this.mAudioManager != null) {}
    for (String str = this.mAudioManager.getParameters("get_silent_uid");; str = ":0")
    {
      if (DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "getSilent_AudioUids():pids=" + str);
      }
      return parseActiveAudioUidsStr(str);
    }
  }
  
  private void handleAudioUid()
  {
    String[] arrayOfString1 = getSilent_AudioUids();
    String[] arrayOfString2 = getActiveAudioUids();
    if (DEBUG_ONEPLUS) {
      Log.e("OnePlusProcessManager", "getMode=" + this.mAudioManager.getMode());
    }
    int i;
    if ((arrayOfString1 != null) && (this.mAudioManager != null) && (this.mAudioManager.getMode() != 2) && (this.mAudioManager.getMode() != 3))
    {
      i = 0;
      if (i < arrayOfString1.length) {
        if ((arrayOfString1[i] != null) && (!arrayOfString1[i].isEmpty())) {}
      }
    }
    for (;;)
    {
      i += 1;
      break;
      int j = Integer.valueOf(arrayOfString1[i]).intValue();
      if ((j >= 10000) && (!checkWhiteUid(j)) && (!checkWhitePackageUid(j)))
      {
        String str = getPackageNameForUid(j);
        int n;
        int m;
        int k;
        synchronized (mActivityManager)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          Log.e("OnePlusProcessManager", "forceStopPackage: silentPackage=" + str);
          mActivityManager.forceStopPackage(str, 0);
          ActivityManagerService.resetPriorityAfterLockedSection();
        }
      }
    }
  }
  
  private void handlePackageChange(List<Integer> paramList1, List<Integer> paramList2)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    Integer localInteger;
    while (i < paramList2.size())
    {
      localInteger = (Integer)paramList2.get(i);
      if (!paramList1.contains(localInteger)) {
        localArrayList1.add(localInteger);
      }
      i += 1;
    }
    i = 0;
    while (i < paramList1.size())
    {
      localInteger = (Integer)paramList1.get(i);
      if (!paramList2.contains(localInteger)) {
        localArrayList2.add(localInteger);
      }
      i += 1;
    }
    i = 0;
    while (i < localArrayList1.size())
    {
      resumeProcessByUID_out(((Integer)localArrayList1.get(i)).intValue(), "handlePackageChange");
      i += 1;
    }
    i = 0;
    while (i < localArrayList2.size())
    {
      scheduleSuspendUid(((Integer)localArrayList2.get(i)).intValue(), suspendUidDelayTime);
      i += 1;
    }
  }
  
  private void handlePackageForPackageChange(List<String> paramList1, List<String> paramList2)
  {
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    int i = 0;
    String str;
    while (i < paramList2.size())
    {
      str = (String)paramList2.get(i);
      if (!paramList1.contains(str)) {
        localArrayList2.add(str);
      }
      i += 1;
    }
    i = 0;
    while (i < paramList1.size())
    {
      str = (String)paramList1.get(i);
      if (!paramList2.contains(str)) {
        localArrayList1.add(str);
      }
      i += 1;
    }
    i = 0;
    for (;;)
    {
      if (i < localArrayList2.size()) {
        paramList1 = (String)localArrayList2.get(i);
      }
      try
      {
        resumeProcessByUID_out(mPackageManager.getApplicationInfo(paramList1, 1).uid, "handlePackageChange");
        i += 1;
        continue;
        i = 0;
        for (;;)
        {
          if (i < localArrayList1.size()) {
            paramList1 = (String)localArrayList1.get(i);
          }
          try
          {
            scheduleSuspendUid(mPackageManager.getApplicationInfo(paramList1, 1).uid, suspendUidDelayTime * 2L);
            i += 1;
            continue;
            return;
          }
          catch (PackageManager.NameNotFoundException paramList1)
          {
            for (;;) {}
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramList1)
      {
        for (;;) {}
      }
    }
  }
  
  private void handleSettingsChangedLocked()
  {
    int i = 0;
    for (;;)
    {
      try
      {
        ContentResolver localContentResolver = this.resolver;
        if (mBPMStatus) {
          i = 1;
        }
        i = Settings.System.getIntForUser(localContentResolver, "doze_mode_policy", i, -2);
        if (!DEBUG) {
          break label107;
        }
        Slog.i("OnePlusProcessManager", "handleSettingsChangedLocked # mBPMStatus=" + mBPMStatus + ", policy=" + i);
      }
      finally {}
      if (bool != mBPMStatus)
      {
        setBPMEnableFromDB(bool);
        saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", bool);
      }
      return;
      boolean bool = true;
      continue;
      label107:
      if (i == 0) {
        bool = false;
      }
    }
  }
  
  private void handleUidChange(HashSet paramHashSet1, HashSet paramHashSet2)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject = paramHashSet2.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject).next();
      if (!paramHashSet1.contains(localInteger)) {
        localArrayList1.add(localInteger);
      }
    }
    paramHashSet1 = paramHashSet1.iterator();
    while (paramHashSet1.hasNext())
    {
      localObject = (Integer)paramHashSet1.next();
      if (!paramHashSet2.contains(localObject)) {
        localArrayList2.add(localObject);
      }
    }
    int i = 0;
    while (i < localArrayList1.size())
    {
      resumeProcessByUID_out(((Integer)localArrayList1.get(i)).intValue(), "handleUidChange");
      i += 1;
    }
    i = 0;
    while (i < localArrayList2.size())
    {
      scheduleSuspendUid(((Integer)localArrayList2.get(i)).intValue(), suspendUidDelayTime);
      i += 1;
    }
  }
  
  private void initialOnlineConfig()
  {
    this.mProcessFreezerConfigObserver = new ConfigObserver(this.mContext, this.mConfigHandler, new ProcessFreezerConfigUpdater(), CONFIG_NAME);
    this.mProcessFreezerConfigObserver.register();
  }
  
  public static boolean isDeliverDisplayChange(int paramInt)
  {
    if (paramInt < 10000) {
      return true;
    }
    if (mOnePlusProcessManager != null) {
      return mOnePlusProcessManager.isDeliverDisplayChangeLock(paramInt);
    }
    return true;
  }
  
  private boolean isDeliverDisplayChangeLock(int paramInt)
  {
    synchronized (this.mRulesLock)
    {
      ArrayList localArrayList = (ArrayList)this.mUidPidState.get(Integer.valueOf(paramInt).intValue());
      if ((localArrayList == null) || (localArrayList.size() < 0)) {
        break label77;
      }
      if (this.mFrontWindowTouchUids.contains(Integer.valueOf(paramInt))) {
        return true;
      }
    }
    return this.mUidState.get(paramInt, 16) < 4;
    label77:
    return true;
  }
  
  private boolean isHomeProcess(ProcessRecord paramProcessRecord)
  {
    return paramProcessRecord == mActivityManager.mHomeProcess;
  }
  
  private boolean isInclude(String paramString, String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0)) {}
    while (paramString == null) {
      return false;
    }
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfString[i].equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static boolean isSupportFrozenApp()
  {
    if (isSuppoerted) {
      return mBPMStatus;
    }
    return false;
  }
  
  public static boolean isUidSuspended(int paramInt)
  {
    Trace.traceBegin(8L, "isUidSuspended");
    synchronized (mLock)
    {
      if ((mSuspendUids == null) || (mSuspendUids.size() == 0))
      {
        Trace.traceEnd(8L);
        return false;
      }
      boolean bool = mSuspendUids.contains(Integer.valueOf(paramInt));
      Trace.traceEnd(8L);
      return bool;
    }
  }
  
  /* Error */
  public static List<String> loadStateLocked(String paramString)
  {
    // Byte code:
    //   0: new 567	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 570	java/util/ArrayList:<init>	()V
    //   7: astore 7
    //   9: new 1503	java/io/File
    //   12: dup
    //   13: aload_0
    //   14: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   17: astore 5
    //   19: aload 5
    //   21: invokevirtual 1507	java/io/File:exists	()Z
    //   24: ifne +38 -> 62
    //   27: ldc -70
    //   29: new 781	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   36: ldc_w 1702
    //   39: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_0
    //   43: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: ldc_w 1704
    //   49: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 1030	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aload 7
    //   61: areturn
    //   62: new 567	java/util/ArrayList
    //   65: dup
    //   66: invokespecial 570	java/util/ArrayList:<init>	()V
    //   69: astore 8
    //   71: aconst_null
    //   72: astore 4
    //   74: aconst_null
    //   75: astore 6
    //   77: iconst_0
    //   78: istore_3
    //   79: iconst_0
    //   80: istore_2
    //   81: new 1706	java/io/FileInputStream
    //   84: dup
    //   85: aload 5
    //   87: invokespecial 1709	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   90: astore 5
    //   92: invokestatic 1715	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   95: astore 4
    //   97: aload 4
    //   99: aload 5
    //   101: aconst_null
    //   102: invokeinterface 1721 3 0
    //   107: aload 4
    //   109: invokeinterface 1723 1 0
    //   114: istore_1
    //   115: iload_1
    //   116: iconst_2
    //   117: if_icmpne +45 -> 162
    //   120: ldc_w 1725
    //   123: aload 4
    //   125: invokeinterface 1728 1 0
    //   130: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   133: ifeq +29 -> 162
    //   136: aload 4
    //   138: aconst_null
    //   139: ldc_w 1730
    //   142: invokeinterface 1734 3 0
    //   147: astore 6
    //   149: aload 6
    //   151: ifnull +11 -> 162
    //   154: aload 8
    //   156: aload 6
    //   158: invokevirtual 1012	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   161: pop
    //   162: iload_1
    //   163: iconst_1
    //   164: if_icmpne -57 -> 107
    //   167: iconst_1
    //   168: istore_2
    //   169: iconst_1
    //   170: istore_1
    //   171: aload 5
    //   173: ifnull +68 -> 241
    //   176: aload 5
    //   178: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   181: getstatic 554	com/android/server/am/OnePlusProcessManager:DEBUG_DETAIL	Z
    //   184: ifeq +43 -> 227
    //   187: ldc -70
    //   189: new 781	java/lang/StringBuilder
    //   192: dup
    //   193: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   196: ldc_w 1702
    //   199: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: aload_0
    //   203: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: ldc_w 1739
    //   209: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: aload 8
    //   214: invokevirtual 1113	java/util/ArrayList:size	()I
    //   217: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   220: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   223: invokestatic 1030	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   226: pop
    //   227: iload_1
    //   228: ifeq +118 -> 346
    //   231: aload 8
    //   233: areturn
    //   234: astore 4
    //   236: aload 4
    //   238: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   241: iload_2
    //   242: istore_1
    //   243: goto -62 -> 181
    //   246: astore 4
    //   248: aload 6
    //   250: astore 5
    //   252: aload 4
    //   254: astore 6
    //   256: aload 5
    //   258: astore 4
    //   260: ldc -70
    //   262: new 781	java/lang/StringBuilder
    //   265: dup
    //   266: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   269: ldc_w 1702
    //   272: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   275: aload_0
    //   276: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   279: ldc_w 1742
    //   282: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   285: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   288: aload 6
    //   290: invokestatic 1745	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   293: pop
    //   294: iload_3
    //   295: istore_1
    //   296: aload 5
    //   298: ifnull -117 -> 181
    //   301: aload 5
    //   303: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   306: iload_2
    //   307: istore_1
    //   308: goto -127 -> 181
    //   311: astore 4
    //   313: aload 4
    //   315: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   318: iload_3
    //   319: istore_1
    //   320: goto -139 -> 181
    //   323: astore_0
    //   324: aload 4
    //   326: ifnull +8 -> 334
    //   329: aload 4
    //   331: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   334: aload_0
    //   335: athrow
    //   336: astore 4
    //   338: aload 4
    //   340: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   343: goto -9 -> 334
    //   346: getstatic 554	com/android/server/am/OnePlusProcessManager:DEBUG_DETAIL	Z
    //   349: ifeq +35 -> 384
    //   352: ldc -70
    //   354: new 781	java/lang/StringBuilder
    //   357: dup
    //   358: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   361: ldc_w 1747
    //   364: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   367: aload_0
    //   368: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: ldc_w 1749
    //   374: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   377: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   380: invokestatic 1030	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   383: pop
    //   384: aload 7
    //   386: areturn
    //   387: astore_0
    //   388: aload 5
    //   390: astore 4
    //   392: goto -68 -> 324
    //   395: astore 6
    //   397: goto -141 -> 256
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	400	0	paramString	String
    //   114	206	1	i	int
    //   80	227	2	j	int
    //   78	241	3	k	int
    //   72	65	4	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   234	3	4	localIOException1	IOException
    //   246	7	4	localException1	Exception
    //   258	1	4	localObject1	Object
    //   311	19	4	localIOException2	IOException
    //   336	3	4	localIOException3	IOException
    //   390	1	4	localObject2	Object
    //   17	372	5	localObject3	Object
    //   75	214	6	localObject4	Object
    //   395	1	6	localException2	Exception
    //   7	378	7	localArrayList1	ArrayList
    //   69	163	8	localArrayList2	ArrayList
    // Exception table:
    //   from	to	target	type
    //   176	181	234	java/io/IOException
    //   81	92	246	java/lang/Exception
    //   301	306	311	java/io/IOException
    //   81	92	323	finally
    //   260	294	323	finally
    //   329	334	336	java/io/IOException
    //   92	107	387	finally
    //   107	115	387	finally
    //   120	149	387	finally
    //   154	162	387	finally
    //   92	107	395	java/lang/Exception
    //   107	115	395	java/lang/Exception
    //   120	149	395	java/lang/Exception
    //   154	162	395	java/lang/Exception
  }
  
  /* Error */
  public static HashMap<String, String> loadXmlLocked(String paramString)
  {
    // Byte code:
    //   0: new 781	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 1752
    //   10: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_0
    //   14: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   20: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   23: new 1503	java/io/File
    //   26: dup
    //   27: aload_0
    //   28: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   31: astore 4
    //   33: aload 4
    //   35: invokevirtual 1507	java/io/File:exists	()Z
    //   38: ifne +34 -> 72
    //   41: new 781	java/lang/StringBuilder
    //   44: dup
    //   45: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   48: ldc_w 1757
    //   51: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: aload_0
    //   55: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: ldc_w 1704
    //   61: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   67: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   70: aconst_null
    //   71: areturn
    //   72: new 726	java/util/HashMap
    //   75: dup
    //   76: invokespecial 727	java/util/HashMap:<init>	()V
    //   79: astore 6
    //   81: aconst_null
    //   82: astore_3
    //   83: aconst_null
    //   84: astore 5
    //   86: iconst_0
    //   87: istore_2
    //   88: new 1706	java/io/FileInputStream
    //   91: dup
    //   92: aload 4
    //   94: invokespecial 1709	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   97: astore 4
    //   99: invokestatic 1715	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   102: astore_3
    //   103: aload_3
    //   104: aload 4
    //   106: aconst_null
    //   107: invokeinterface 1721 3 0
    //   112: aload_3
    //   113: invokeinterface 1723 1 0
    //   118: istore_1
    //   119: iload_1
    //   120: iconst_2
    //   121: if_icmpne +49 -> 170
    //   124: ldc_w 1759
    //   127: aload_3
    //   128: invokeinterface 1728 1 0
    //   133: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   136: ifeq +34 -> 170
    //   139: aload_3
    //   140: aconst_null
    //   141: ldc_w 1761
    //   144: invokeinterface 1734 3 0
    //   149: astore 5
    //   151: aload 5
    //   153: ifnull +17 -> 170
    //   156: aload 6
    //   158: aload 5
    //   160: aload_3
    //   161: invokeinterface 1764 1 0
    //   166: invokevirtual 1765	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   169: pop
    //   170: iload_1
    //   171: iconst_1
    //   172: if_icmpne -60 -> 112
    //   175: iconst_1
    //   176: istore_1
    //   177: aload 4
    //   179: ifnull +8 -> 187
    //   182: aload 4
    //   184: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   187: new 781	java/lang/StringBuilder
    //   190: dup
    //   191: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   194: ldc_w 1757
    //   197: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: aload_0
    //   201: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: ldc_w 1767
    //   207: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: aload 6
    //   212: invokevirtual 1768	java/util/HashMap:size	()I
    //   215: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   218: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   221: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   224: iload_1
    //   225: ifeq +105 -> 330
    //   228: aload 6
    //   230: areturn
    //   231: astore_3
    //   232: aload_3
    //   233: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   236: goto -49 -> 187
    //   239: astore_3
    //   240: aload 5
    //   242: astore 4
    //   244: aload_3
    //   245: astore 5
    //   247: aload 4
    //   249: astore_3
    //   250: new 781	java/lang/StringBuilder
    //   253: dup
    //   254: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   257: ldc_w 1757
    //   260: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_0
    //   264: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: ldc_w 1742
    //   270: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   273: aload 5
    //   275: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   278: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   281: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   284: iload_2
    //   285: istore_1
    //   286: aload 4
    //   288: ifnull -101 -> 187
    //   291: aload 4
    //   293: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   296: iload_2
    //   297: istore_1
    //   298: goto -111 -> 187
    //   301: astore_3
    //   302: aload_3
    //   303: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   306: iload_2
    //   307: istore_1
    //   308: goto -121 -> 187
    //   311: astore_0
    //   312: aload_3
    //   313: ifnull +7 -> 320
    //   316: aload_3
    //   317: invokevirtual 1737	java/io/FileInputStream:close	()V
    //   320: aload_0
    //   321: athrow
    //   322: astore_3
    //   323: aload_3
    //   324: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   327: goto -7 -> 320
    //   330: new 781	java/lang/StringBuilder
    //   333: dup
    //   334: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   337: ldc_w 1770
    //   340: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: aload_0
    //   344: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: ldc_w 1749
    //   350: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   353: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   356: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   359: aconst_null
    //   360: areturn
    //   361: astore_0
    //   362: aload 4
    //   364: astore_3
    //   365: goto -53 -> 312
    //   368: astore 5
    //   370: goto -123 -> 247
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	373	0	paramString	String
    //   118	190	1	i	int
    //   87	220	2	j	int
    //   82	79	3	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   231	2	3	localIOException1	IOException
    //   239	6	3	localException1	Exception
    //   249	1	3	localObject1	Object
    //   301	16	3	localIOException2	IOException
    //   322	2	3	localIOException3	IOException
    //   364	1	3	localObject2	Object
    //   31	332	4	localObject3	Object
    //   84	190	5	localObject4	Object
    //   368	1	5	localException2	Exception
    //   79	150	6	localHashMap	HashMap
    // Exception table:
    //   from	to	target	type
    //   182	187	231	java/io/IOException
    //   88	99	239	java/lang/Exception
    //   291	296	301	java/io/IOException
    //   88	99	311	finally
    //   250	284	311	finally
    //   316	320	322	java/io/IOException
    //   99	112	361	finally
    //   112	119	361	finally
    //   124	151	361	finally
    //   156	170	361	finally
    //   99	112	368	java/lang/Exception
    //   112	119	368	java/lang/Exception
    //   124	151	368	java/lang/Exception
    //   156	170	368	java/lang/Exception
  }
  
  public static void myLog(String paramString)
  {
    Slog.d("OnePlusProcessManager", paramString);
  }
  
  private String[] parseActiveAudioUidsStr(String paramString)
  {
    if (DEBUG_DETAIL) {
      Slog.d("OnePlusProcessManager", "parseActiveAudioPidsStr():pids=" + paramString);
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    if (!paramString.contains(":")) {
      return null;
    }
    return paramString.split(":");
  }
  
  private void printList(String paramString, PrintWriter paramPrintWriter, List<String> paramList)
  {
    paramPrintWriter.println("\n" + paramString);
    paramString = paramList.iterator();
    while (paramString.hasNext())
    {
      paramList = (String)paramString.next();
      paramPrintWriter.println("\t" + paramList);
    }
  }
  
  private void registerSimReceiver()
  {
    if (this.mContext == null)
    {
      Slog.e("OnePlusProcessManager", "Fatal Exception # registerGeneralReceiver # mContext=null");
      return;
    }
    if (DEBUG) {
      Slog.i("OnePlusProcessManager", "registerSimReceiver");
    }
    mGlobalFlags = SystemProperties.getInt("persist.sys.cgroup.flags", 0);
    mRegion = SystemProperties.get("persist.sys.oem.region", "");
    if (!mRegion.equalsIgnoreCase("CN")) {
      if (((mGlobalFlags & 0x1) == 0) && (!responseSIMStateChanged()))
      {
        updateDozePolicyToDB(false);
        setBPMEnable(false);
        saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", false);
        localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mContext.registerReceiver(this.mSimReceiver, localIntentFilter);
      }
    }
    while ((mGlobalFlags & 0x1) != 0)
    {
      IntentFilter localIntentFilter;
      return;
    }
    mGlobalFlags |= 0x1;
    SystemProperties.set("persist.sys.cgroup.flags", mGlobalFlags + "");
  }
  
  private static void removeDozeWhiteList(String paramString)
  {
    if (mDeviceIdleService != null) {
      try
      {
        if (DEBUG_ONEPLUS) {
          Slog.d("OnePlusProcessManager", "removeDozeWhiteList: " + paramString);
        }
        mDeviceIdleService.removePowerSaveWhitelistApp(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.w("OnePlusProcessManager", "Falied to add package to doze whitelist");
        return;
      }
    }
    Slog.w("OnePlusProcessManager", "Cannot get DeviceIdleController");
  }
  
  private void removeTempWhiteList()
  {
    int i = 0;
    while (i < mTrafficeWhiteUids.size())
    {
      removeDozeWhiteList((String)mTrafficeWhiteUids.get(i));
      i += 1;
    }
    mTrafficeWhiteUids.clear();
  }
  
  /* Error */
  private void resolveConfigFromJSON(JSONArray paramJSONArray)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: iconst_0
    //   6: istore_2
    //   7: iload_2
    //   8: aload_1
    //   9: invokevirtual 1824	org/json/JSONArray:length	()I
    //   12: if_icmpge +258 -> 270
    //   15: aload_1
    //   16: iload_2
    //   17: invokevirtual 1828	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   20: astore 6
    //   22: aload 6
    //   24: ldc_w 1761
    //   27: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   30: ldc_w 1835
    //   33: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   36: ifeq +103 -> 139
    //   39: aload 6
    //   41: ldc_w 1837
    //   44: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   47: astore 5
    //   49: aload_0
    //   50: getfield 376	com/android/server/am/OnePlusProcessManager:mBlackAlarmList	Ljava/util/List;
    //   53: astore 4
    //   55: aload 4
    //   57: monitorenter
    //   58: aload_0
    //   59: getfield 376	com/android/server/am/OnePlusProcessManager:mBlackAlarmList	Ljava/util/List;
    //   62: invokeinterface 1842 1 0
    //   67: iconst_0
    //   68: istore_3
    //   69: iload_3
    //   70: aload 5
    //   72: invokevirtual 1824	org/json/JSONArray:length	()I
    //   75: if_icmpge +42 -> 117
    //   78: aload_0
    //   79: getfield 376	com/android/server/am/OnePlusProcessManager:mBlackAlarmList	Ljava/util/List;
    //   82: aload 5
    //   84: iload_3
    //   85: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   88: invokeinterface 1845 2 0
    //   93: pop
    //   94: aload_0
    //   95: getfield 376	com/android/server/am/OnePlusProcessManager:mBlackAlarmList	Ljava/util/List;
    //   98: aload 5
    //   100: iload_3
    //   101: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   104: invokeinterface 1197 2 0
    //   109: pop
    //   110: iload_3
    //   111: iconst_1
    //   112: iadd
    //   113: istore_3
    //   114: goto -45 -> 69
    //   117: aload 4
    //   119: monitorexit
    //   120: ldc -70
    //   122: ldc_w 1847
    //   125: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   128: pop
    //   129: aload_0
    //   130: getfield 376	com/android/server/am/OnePlusProcessManager:mBlackAlarmList	Ljava/util/List;
    //   133: ldc 53
    //   135: invokestatic 1852	com/android/server/am/OnePlusProcessManager:saveConfigXml	(Ljava/util/List;Ljava/lang/String;)Z
    //   138: pop
    //   139: aload 6
    //   141: ldc_w 1761
    //   144: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   147: ldc_w 1854
    //   150: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   153: ifeq +140 -> 293
    //   156: aload 6
    //   158: ldc_w 1837
    //   161: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   164: astore 5
    //   166: aload_0
    //   167: getfield 678	com/android/server/am/OnePlusProcessManager:mBlackGpsList	Ljava/util/List;
    //   170: invokeinterface 1842 1 0
    //   175: aload_0
    //   176: getfield 678	com/android/server/am/OnePlusProcessManager:mBlackGpsList	Ljava/util/List;
    //   179: astore 4
    //   181: aload 4
    //   183: monitorenter
    //   184: iconst_0
    //   185: istore_3
    //   186: iload_3
    //   187: aload 5
    //   189: invokevirtual 1824	org/json/JSONArray:length	()I
    //   192: if_icmpge +79 -> 271
    //   195: aload_0
    //   196: getfield 678	com/android/server/am/OnePlusProcessManager:mBlackGpsList	Ljava/util/List;
    //   199: aload 5
    //   201: iload_3
    //   202: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   205: invokeinterface 1845 2 0
    //   210: pop
    //   211: aload_0
    //   212: getfield 678	com/android/server/am/OnePlusProcessManager:mBlackGpsList	Ljava/util/List;
    //   215: aload 5
    //   217: iload_3
    //   218: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   221: invokeinterface 1197 2 0
    //   226: pop
    //   227: iload_3
    //   228: iconst_1
    //   229: iadd
    //   230: istore_3
    //   231: goto -45 -> 186
    //   234: astore_1
    //   235: aload 4
    //   237: monitorexit
    //   238: aload_1
    //   239: athrow
    //   240: astore_1
    //   241: ldc -70
    //   243: new 781	java/lang/StringBuilder
    //   246: dup
    //   247: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   250: ldc_w 1856
    //   253: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: aload_1
    //   257: invokevirtual 1859	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   260: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   266: invokestatic 1455	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   269: pop
    //   270: return
    //   271: aload 4
    //   273: monitorexit
    //   274: ldc -70
    //   276: ldc_w 1861
    //   279: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   282: pop
    //   283: aload_0
    //   284: getfield 678	com/android/server/am/OnePlusProcessManager:mBlackGpsList	Ljava/util/List;
    //   287: ldc 65
    //   289: invokestatic 1852	com/android/server/am/OnePlusProcessManager:saveConfigXml	(Ljava/util/List;Ljava/lang/String;)Z
    //   292: pop
    //   293: aload 6
    //   295: ldc_w 1761
    //   298: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   301: ldc_w 1863
    //   304: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   307: ifeq +363 -> 670
    //   310: aload 6
    //   312: ldc_w 1837
    //   315: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   318: astore 8
    //   320: aload_0
    //   321: getfield 729	com/android/server/am/OnePlusProcessManager:mSysCfgMapOnlineConifg	Ljava/util/HashMap;
    //   324: invokevirtual 1864	java/util/HashMap:clear	()V
    //   327: ldc_w 663
    //   330: astore 4
    //   332: aload_0
    //   333: getfield 729	com/android/server/am/OnePlusProcessManager:mSysCfgMapOnlineConifg	Ljava/util/HashMap;
    //   336: astore 7
    //   338: aload 7
    //   340: monitorenter
    //   341: iconst_0
    //   342: istore_3
    //   343: iload_3
    //   344: aload 8
    //   346: invokevirtual 1824	org/json/JSONArray:length	()I
    //   349: if_icmpge +275 -> 624
    //   352: aload 8
    //   354: iload_3
    //   355: invokevirtual 1828	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   358: ldc_w 1866
    //   361: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   364: ldc_w 1868
    //   367: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   370: ifeq +88 -> 458
    //   373: aload 8
    //   375: iload_3
    //   376: invokevirtual 1828	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   379: ldc_w 1870
    //   382: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   385: astore 5
    //   387: aload_0
    //   388: getfield 729	com/android/server/am/OnePlusProcessManager:mSysCfgMapOnlineConifg	Ljava/util/HashMap;
    //   391: ldc_w 1868
    //   394: aload 5
    //   396: invokevirtual 1765	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   399: pop
    //   400: ldc -70
    //   402: new 781	java/lang/StringBuilder
    //   405: dup
    //   406: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   409: ldc_w 1872
    //   412: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   415: aload 5
    //   417: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   420: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   423: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   426: pop
    //   427: aload 5
    //   429: astore 4
    //   431: aload 5
    //   433: ldc_w 1874
    //   436: invokevirtual 1446	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   439: ifeq +19 -> 458
    //   442: aload_0
    //   443: iconst_0
    //   444: invokevirtual 1813	com/android/server/am/OnePlusProcessManager:setBPMEnable	(Z)V
    //   447: ldc 80
    //   449: iconst_0
    //   450: invokestatic 1660	com/android/server/am/OnePlusProcessManager:saveBpmStsLocked	(Ljava/lang/String;Z)Z
    //   453: pop
    //   454: aload 5
    //   456: astore 4
    //   458: aload 8
    //   460: iload_3
    //   461: invokevirtual 1828	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   464: ldc_w 1866
    //   467: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   470: ldc_w 1436
    //   473: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   476: ifeq +103 -> 579
    //   479: aload 8
    //   481: iload_3
    //   482: invokevirtual 1828	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   485: ldc_w 1870
    //   488: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   491: astore 5
    //   493: aload_0
    //   494: getfield 729	com/android/server/am/OnePlusProcessManager:mSysCfgMapOnlineConifg	Ljava/util/HashMap;
    //   497: ldc_w 1436
    //   500: aload 5
    //   502: invokevirtual 1765	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   505: pop
    //   506: aload 5
    //   508: invokestatic 1216	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   511: astore 5
    //   513: ldc -70
    //   515: new 781	java/lang/StringBuilder
    //   518: dup
    //   519: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   522: ldc_w 1876
    //   525: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload 5
    //   530: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   533: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   536: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   539: pop
    //   540: aload 5
    //   542: ifnull +37 -> 579
    //   545: aload 5
    //   547: invokevirtual 1039	java/lang/Integer:intValue	()I
    //   550: getstatic 661	com/android/server/am/OnePlusProcessManager:CFG_VERSOON	I
    //   553: if_icmple +26 -> 579
    //   556: aload 4
    //   558: ldc_w 1874
    //   561: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   564: ifeq +28 -> 592
    //   567: aload_0
    //   568: iconst_0
    //   569: invokevirtual 1813	com/android/server/am/OnePlusProcessManager:setBPMEnable	(Z)V
    //   572: ldc 80
    //   574: iconst_0
    //   575: invokestatic 1660	com/android/server/am/OnePlusProcessManager:saveBpmStsLocked	(Ljava/lang/String;Z)Z
    //   578: pop
    //   579: iload_3
    //   580: iconst_1
    //   581: iadd
    //   582: istore_3
    //   583: goto -240 -> 343
    //   586: astore_1
    //   587: aload 4
    //   589: monitorexit
    //   590: aload_1
    //   591: athrow
    //   592: aload 4
    //   594: ldc_w 1878
    //   597: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   600: ifeq -21 -> 579
    //   603: aload_0
    //   604: iconst_1
    //   605: invokevirtual 1813	com/android/server/am/OnePlusProcessManager:setBPMEnable	(Z)V
    //   608: ldc 80
    //   610: iconst_1
    //   611: invokestatic 1660	com/android/server/am/OnePlusProcessManager:saveBpmStsLocked	(Ljava/lang/String;Z)Z
    //   614: pop
    //   615: goto -36 -> 579
    //   618: astore_1
    //   619: aload 7
    //   621: monitorexit
    //   622: aload_1
    //   623: athrow
    //   624: ldc 47
    //   626: aload_0
    //   627: getfield 729	com/android/server/am/OnePlusProcessManager:mSysCfgMapOnlineConifg	Ljava/util/HashMap;
    //   630: invokestatic 1882	com/android/server/am/OnePlusProcessManager:saveXmlLocked	(Ljava/lang/String;Ljava/util/HashMap;)Z
    //   633: pop
    //   634: aload_0
    //   635: invokevirtual 1885	com/android/server/am/OnePlusProcessManager:prepareConfigStatus	()V
    //   638: aload_0
    //   639: getfield 714	com/android/server/am/OnePlusProcessManager:mForceSwitch	I
    //   642: iconst_2
    //   643: if_icmpne +15 -> 658
    //   646: aload_0
    //   647: iconst_0
    //   648: invokevirtual 1813	com/android/server/am/OnePlusProcessManager:setBPMEnable	(Z)V
    //   651: ldc 80
    //   653: iconst_0
    //   654: invokestatic 1660	com/android/server/am/OnePlusProcessManager:saveBpmStsLocked	(Ljava/lang/String;Z)Z
    //   657: pop
    //   658: aload 7
    //   660: monitorexit
    //   661: ldc -70
    //   663: ldc_w 1887
    //   666: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   669: pop
    //   670: aload 6
    //   672: ldc_w 1761
    //   675: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   678: ldc_w 1889
    //   681: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   684: ifeq +103 -> 787
    //   687: aload 6
    //   689: ldc_w 1837
    //   692: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   695: astore 5
    //   697: aload_0
    //   698: getfield 680	com/android/server/am/OnePlusProcessManager:mBlackBrdList	Ljava/util/List;
    //   701: astore 4
    //   703: aload 4
    //   705: monitorenter
    //   706: aload_0
    //   707: getfield 680	com/android/server/am/OnePlusProcessManager:mBlackBrdList	Ljava/util/List;
    //   710: invokeinterface 1842 1 0
    //   715: iconst_0
    //   716: istore_3
    //   717: iload_3
    //   718: aload 5
    //   720: invokevirtual 1824	org/json/JSONArray:length	()I
    //   723: if_icmpge +42 -> 765
    //   726: aload_0
    //   727: getfield 680	com/android/server/am/OnePlusProcessManager:mBlackBrdList	Ljava/util/List;
    //   730: aload 5
    //   732: iload_3
    //   733: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   736: invokeinterface 1845 2 0
    //   741: pop
    //   742: aload_0
    //   743: getfield 680	com/android/server/am/OnePlusProcessManager:mBlackBrdList	Ljava/util/List;
    //   746: aload 5
    //   748: iload_3
    //   749: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   752: invokeinterface 1197 2 0
    //   757: pop
    //   758: iload_3
    //   759: iconst_1
    //   760: iadd
    //   761: istore_3
    //   762: goto -45 -> 717
    //   765: aload 4
    //   767: monitorexit
    //   768: ldc -70
    //   770: ldc_w 1891
    //   773: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   776: pop
    //   777: aload_0
    //   778: getfield 680	com/android/server/am/OnePlusProcessManager:mBlackBrdList	Ljava/util/List;
    //   781: ldc 59
    //   783: invokestatic 1852	com/android/server/am/OnePlusProcessManager:saveConfigXml	(Ljava/util/List;Ljava/lang/String;)Z
    //   786: pop
    //   787: aload 6
    //   789: ldc_w 1761
    //   792: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   795: ldc_w 1893
    //   798: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   801: ifeq +109 -> 910
    //   804: aload 6
    //   806: ldc_w 1837
    //   809: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   812: astore 5
    //   814: aload_0
    //   815: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   818: invokeinterface 1842 1 0
    //   823: aload_0
    //   824: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   827: astore 4
    //   829: aload 4
    //   831: monitorenter
    //   832: iconst_0
    //   833: istore_3
    //   834: iload_3
    //   835: aload 5
    //   837: invokevirtual 1824	org/json/JSONArray:length	()I
    //   840: if_icmpge +48 -> 888
    //   843: aload_0
    //   844: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   847: aload 5
    //   849: iload_3
    //   850: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   853: invokeinterface 1845 2 0
    //   858: pop
    //   859: aload_0
    //   860: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   863: aload 5
    //   865: iload_3
    //   866: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   869: invokeinterface 1197 2 0
    //   874: pop
    //   875: iload_3
    //   876: iconst_1
    //   877: iadd
    //   878: istore_3
    //   879: goto -45 -> 834
    //   882: astore_1
    //   883: aload 4
    //   885: monitorexit
    //   886: aload_1
    //   887: athrow
    //   888: aload 4
    //   890: monitorexit
    //   891: ldc -70
    //   893: ldc_w 1895
    //   896: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   899: pop
    //   900: aload_0
    //   901: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   904: ldc 86
    //   906: invokestatic 1852	com/android/server/am/OnePlusProcessManager:saveConfigXml	(Ljava/util/List;Ljava/lang/String;)Z
    //   909: pop
    //   910: aload 6
    //   912: ldc_w 1761
    //   915: invokevirtual 1833	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   918: ldc_w 1897
    //   921: invokevirtual 1091	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   924: ifeq +149 -> 1073
    //   927: aload 6
    //   929: ldc_w 1837
    //   932: invokevirtual 1841	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   935: astore 5
    //   937: new 567	java/util/ArrayList
    //   940: dup
    //   941: invokespecial 570	java/util/ArrayList:<init>	()V
    //   944: astore 6
    //   946: aload 6
    //   948: aload_0
    //   949: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   952: invokeinterface 1901 2 0
    //   957: pop
    //   958: aload_0
    //   959: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   962: astore 4
    //   964: aload 4
    //   966: monitorenter
    //   967: aload_0
    //   968: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   971: invokeinterface 1842 1 0
    //   976: iconst_0
    //   977: istore_3
    //   978: iload_3
    //   979: aload 5
    //   981: invokevirtual 1824	org/json/JSONArray:length	()I
    //   984: if_icmpge +48 -> 1032
    //   987: aload_0
    //   988: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   991: aload 5
    //   993: iload_3
    //   994: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   997: invokeinterface 1845 2 0
    //   1002: pop
    //   1003: aload_0
    //   1004: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   1007: aload 5
    //   1009: iload_3
    //   1010: invokevirtual 1844	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   1013: invokeinterface 1197 2 0
    //   1018: pop
    //   1019: iload_3
    //   1020: iconst_1
    //   1021: iadd
    //   1022: istore_3
    //   1023: goto -45 -> 978
    //   1026: astore_1
    //   1027: aload 4
    //   1029: monitorexit
    //   1030: aload_1
    //   1031: athrow
    //   1032: aload 4
    //   1034: monitorexit
    //   1035: aload_0
    //   1036: aload 6
    //   1038: aload_0
    //   1039: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   1042: invokespecial 1903	com/android/server/am/OnePlusProcessManager:handlePackageForPackageChange	(Ljava/util/List;Ljava/util/List;)V
    //   1045: aload_0
    //   1046: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   1049: ldc 127
    //   1051: invokestatic 1852	com/android/server/am/OnePlusProcessManager:saveConfigXml	(Ljava/util/List;Ljava/lang/String;)Z
    //   1054: pop
    //   1055: ldc -70
    //   1057: ldc_w 1905
    //   1060: invokestatic 1848	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1063: pop
    //   1064: goto +9 -> 1073
    //   1067: astore_1
    //   1068: aload 4
    //   1070: monitorexit
    //   1071: aload_1
    //   1072: athrow
    //   1073: iload_2
    //   1074: iconst_1
    //   1075: iadd
    //   1076: istore_2
    //   1077: goto -1070 -> 7
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1080	0	this	OnePlusProcessManager
    //   0	1080	1	paramJSONArray	JSONArray
    //   6	1071	2	i	int
    //   68	955	3	j	int
    //   47	961	5	localObject2	Object
    //   20	1017	6	localObject3	Object
    //   336	323	7	localHashMap	HashMap
    //   318	162	8	localJSONArray	JSONArray
    // Exception table:
    //   from	to	target	type
    //   58	67	234	finally
    //   69	110	234	finally
    //   7	58	240	org/json/JSONException
    //   117	139	240	org/json/JSONException
    //   139	184	240	org/json/JSONException
    //   235	240	240	org/json/JSONException
    //   271	293	240	org/json/JSONException
    //   293	327	240	org/json/JSONException
    //   332	341	240	org/json/JSONException
    //   587	592	240	org/json/JSONException
    //   619	624	240	org/json/JSONException
    //   658	670	240	org/json/JSONException
    //   670	706	240	org/json/JSONException
    //   765	787	240	org/json/JSONException
    //   787	832	240	org/json/JSONException
    //   883	888	240	org/json/JSONException
    //   888	910	240	org/json/JSONException
    //   910	967	240	org/json/JSONException
    //   1027	1032	240	org/json/JSONException
    //   1032	1064	240	org/json/JSONException
    //   1068	1073	240	org/json/JSONException
    //   186	227	586	finally
    //   343	352	618	finally
    //   352	427	618	finally
    //   431	454	618	finally
    //   458	540	618	finally
    //   545	579	618	finally
    //   592	615	618	finally
    //   624	658	618	finally
    //   706	715	882	finally
    //   717	758	882	finally
    //   834	875	1026	finally
    //   967	976	1067	finally
    //   978	1019	1067	finally
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
        break label146;
      }
      setBPMEnable(true);
      saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", true);
    }
    for (;;)
    {
      bool = true;
      mGlobalFlags |= 0x1;
      SystemProperties.set("persist.sys.cgroup.flags", mGlobalFlags + "");
      if ((DEBUG) || (bool)) {
        Slog.i("OnePlusProcessManager", "responseSIMStateChanged # mccCountry=" + str + ", ret=" + bool);
      }
      return bool;
      label146:
      setBPMEnable(false);
      saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", false);
    }
  }
  
  public static void resumeAllProcess(String paramString)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus) {
      return;
    }
    if (mOnePlusProcessManager == null) {
      return;
    }
    if (mOnePlusProcessManager != null) {
      mOnePlusProcessManager.resumeAllProcessLock(paramString);
    }
  }
  
  public static final void resumeProcessByUID_out(int paramInt, String paramString)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus) {
      return;
    }
    resumeProcessByUID_out(paramInt, paramString, false);
  }
  
  public static final void resumeProcessByUID_out(int paramInt, String paramString, boolean paramBoolean)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus) {
      return;
    }
    if (mOnePlusProcessManager == null) {
      return;
    }
    if (!UserHandle.isApp(paramInt)) {
      return;
    }
    mOnePlusProcessManager.scheduleResumeUid(paramInt, paramString);
  }
  
  public static final void resumeProcessByUID_out_Delay(int paramInt1, String paramString, int paramInt2)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus) {
      return;
    }
    if (mOnePlusProcessManager == null) {
      return;
    }
    if (!UserHandle.isApp(paramInt1)) {
      return;
    }
    mOnePlusProcessManager.scheduleResumeUid(paramInt1, paramString, paramInt2);
  }
  
  private void resumeRelateProcess(ProcessRecord paramProcessRecord)
  {
    if (paramProcessRecord == null) {
      return;
    }
    if (paramProcessRecord.uid < 10000) {
      return;
    }
    Object localObject;
    if ((paramProcessRecord.connections != null) && (paramProcessRecord.connections.size() > 0))
    {
      Iterator localIterator1 = paramProcessRecord.connections.iterator();
      while (localIterator1.hasNext()) {
        try
        {
          localObject = (ConnectionRecord)localIterator1.next();
          if ((localObject != null) && (((ConnectionRecord)localObject).binding != null) && (((ConnectionRecord)localObject).binding.service != null) && (((ConnectionRecord)localObject).binding.service.app != null) && (paramProcessRecord.uid != ((ConnectionRecord)localObject).binding.service.app.uid)) {
            scheduleResumeUid(((ConnectionRecord)localObject).binding.service.app.uid, "resumeRelateProcess connection");
          }
        }
        catch (Exception localException1) {}
      }
    }
    if ((paramProcessRecord.conProviders != null) && (paramProcessRecord.conProviders.size() > 0))
    {
      Iterator localIterator2 = ((ArrayList)paramProcessRecord.conProviders.clone()).iterator();
      while (localIterator2.hasNext()) {
        try
        {
          localObject = (ContentProviderConnection)localIterator2.next();
          if ((localObject != null) && (((ContentProviderConnection)localObject).provider != null) && (((ContentProviderConnection)localObject).provider.proc != null) && (((ContentProviderConnection)localObject).provider.proc.uid != paramProcessRecord.uid)) {
            scheduleResumeUid(((ContentProviderConnection)localObject).provider.proc.uid, "resumeRelateProcess conProviders");
          }
        }
        catch (Exception localException2) {}
      }
    }
    if ((paramProcessRecord.adjSource != null) && ((paramProcessRecord.adjSource instanceof ProcessRecord)))
    {
      ProcessRecord localProcessRecord = (ProcessRecord)paramProcessRecord.adjSource;
      if ((localProcessRecord != null) && (localProcessRecord.uid != paramProcessRecord.uid)) {
        scheduleResumeUid(localProcessRecord.uid, "resumeRelateProcess adjSource");
      }
    }
  }
  
  private void resumeRelateUid(int paramInt)
  {
    synchronized (mProcess)
    {
      ArrayList localArrayList = mOnePlusProcessManager.getProcessForUid(paramInt);
      if (localArrayList != null)
      {
        paramInt = localArrayList.size();
        if (paramInt != 0) {}
      }
      else
      {
        return;
      }
      paramInt = 0;
      while (paramInt < localArrayList.size())
      {
        mOnePlusProcessManager.resumeRelateProcess((ProcessRecord)localArrayList.get(paramInt));
        paramInt += 1;
      }
      return;
    }
  }
  
  /* Error */
  private static boolean saveBpmStsLocked(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 552	com/android/server/am/OnePlusProcessManager:DEBUG	Z
    //   3: ifeq +36 -> 39
    //   6: new 781	java/lang/StringBuilder
    //   9: dup
    //   10: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   13: ldc_w 1954
    //   16: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: aload_0
    //   20: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: ldc_w 1956
    //   26: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: iload_1
    //   30: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   33: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   39: ldc_w 1958
    //   42: invokestatic 1963	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   45: astore 5
    //   47: aconst_null
    //   48: astore_2
    //   49: aconst_null
    //   50: astore 4
    //   52: new 1965	java/io/FileOutputStream
    //   55: dup
    //   56: new 1503	java/io/File
    //   59: dup
    //   60: aload_0
    //   61: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   64: invokespecial 1966	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   67: astore_3
    //   68: invokestatic 1970	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   71: astore 4
    //   73: aload 4
    //   75: aload_3
    //   76: ldc_w 1972
    //   79: invokeinterface 1978 3 0
    //   84: aload 4
    //   86: aconst_null
    //   87: iconst_1
    //   88: invokestatic 1983	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   91: invokeinterface 1987 3 0
    //   96: aload 4
    //   98: aload 5
    //   100: invokeinterface 1991 2 0
    //   105: pop
    //   106: aload 4
    //   108: aconst_null
    //   109: ldc_w 1993
    //   112: invokeinterface 1997 3 0
    //   117: pop
    //   118: aload 4
    //   120: aload 5
    //   122: invokeinterface 1991 2 0
    //   127: pop
    //   128: aload 4
    //   130: aconst_null
    //   131: ldc_w 1725
    //   134: invokeinterface 1997 3 0
    //   139: pop
    //   140: iload_1
    //   141: ifeq +81 -> 222
    //   144: ldc_w 1999
    //   147: astore_2
    //   148: aload 4
    //   150: aconst_null
    //   151: ldc_w 1730
    //   154: aload_2
    //   155: invokeinterface 2003 4 0
    //   160: pop
    //   161: aload 4
    //   163: aconst_null
    //   164: ldc_w 1725
    //   167: invokeinterface 2006 3 0
    //   172: pop
    //   173: aload 4
    //   175: aload 5
    //   177: invokeinterface 1991 2 0
    //   182: pop
    //   183: aload 4
    //   185: aconst_null
    //   186: ldc_w 1993
    //   189: invokeinterface 2006 3 0
    //   194: pop
    //   195: aload 4
    //   197: aload 5
    //   199: invokeinterface 1991 2 0
    //   204: pop
    //   205: aload 4
    //   207: invokeinterface 2009 1 0
    //   212: aload_3
    //   213: ifnull +7 -> 220
    //   216: aload_3
    //   217: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   220: iconst_1
    //   221: ireturn
    //   222: ldc_w 2012
    //   225: astore_2
    //   226: goto -78 -> 148
    //   229: astore_0
    //   230: aload_0
    //   231: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   234: iconst_1
    //   235: ireturn
    //   236: astore_2
    //   237: aload 4
    //   239: astore_3
    //   240: aload_2
    //   241: astore 4
    //   243: aload_3
    //   244: astore_2
    //   245: new 781	java/lang/StringBuilder
    //   248: dup
    //   249: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   252: ldc_w 2014
    //   255: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: aload_0
    //   259: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   262: aload 4
    //   264: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   267: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   270: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   273: aload_3
    //   274: ifnull +7 -> 281
    //   277: aload_3
    //   278: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   281: iconst_0
    //   282: ireturn
    //   283: astore_0
    //   284: aload_0
    //   285: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   288: iconst_0
    //   289: ireturn
    //   290: astore_0
    //   291: aload_2
    //   292: ifnull +7 -> 299
    //   295: aload_2
    //   296: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   299: aload_0
    //   300: athrow
    //   301: astore_2
    //   302: aload_2
    //   303: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   306: goto -7 -> 299
    //   309: astore_0
    //   310: aload_3
    //   311: astore_2
    //   312: goto -21 -> 291
    //   315: astore 4
    //   317: goto -74 -> 243
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	320	0	paramString	String
    //   0	320	1	paramBoolean	boolean
    //   48	178	2	str1	String
    //   236	5	2	localIOException1	IOException
    //   244	52	2	localObject1	Object
    //   301	2	2	localIOException2	IOException
    //   311	1	2	localObject2	Object
    //   67	244	3	localObject3	Object
    //   50	213	4	localObject4	Object
    //   315	1	4	localIOException3	IOException
    //   45	153	5	str2	String
    // Exception table:
    //   from	to	target	type
    //   216	220	229	java/io/IOException
    //   52	68	236	java/io/IOException
    //   277	281	283	java/io/IOException
    //   52	68	290	finally
    //   245	273	290	finally
    //   295	299	301	java/io/IOException
    //   68	140	309	finally
    //   148	212	309	finally
    //   68	140	315	java/io/IOException
    //   148	212	315	java/io/IOException
  }
  
  /* Error */
  private static boolean saveConfigXml(List<String> paramList, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore 4
    //   5: ldc_w 1958
    //   8: invokestatic 1963	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   11: astore 5
    //   13: new 1965	java/io/FileOutputStream
    //   16: dup
    //   17: new 1503	java/io/File
    //   20: dup
    //   21: aload_1
    //   22: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   25: invokespecial 1966	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: astore_3
    //   29: invokestatic 1970	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   32: astore_2
    //   33: aload_2
    //   34: aload_3
    //   35: ldc_w 1972
    //   38: invokeinterface 1978 3 0
    //   43: aload_2
    //   44: aconst_null
    //   45: iconst_1
    //   46: invokestatic 1983	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   49: invokeinterface 1987 3 0
    //   54: aload_2
    //   55: aload 5
    //   57: invokeinterface 1991 2 0
    //   62: pop
    //   63: aload_0
    //   64: invokeinterface 2015 1 0
    //   69: astore_0
    //   70: aload_0
    //   71: invokeinterface 1253 1 0
    //   76: ifeq +137 -> 213
    //   79: aload_0
    //   80: invokeinterface 1257 1 0
    //   85: checkcast 1088	java/lang/String
    //   88: astore 4
    //   90: ldc -70
    //   92: new 781	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   99: ldc_w 2017
    //   102: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: aload 4
    //   107: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 2018	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: aload 4
    //   119: ifnull -49 -> 70
    //   122: aload_2
    //   123: aconst_null
    //   124: ldc_w 1725
    //   127: invokeinterface 1997 3 0
    //   132: pop
    //   133: aload_2
    //   134: aconst_null
    //   135: ldc_w 1730
    //   138: aload 4
    //   140: invokeinterface 2003 4 0
    //   145: pop
    //   146: aload_2
    //   147: aconst_null
    //   148: ldc_w 1725
    //   151: invokeinterface 2006 3 0
    //   156: pop
    //   157: aload_2
    //   158: aload 5
    //   160: invokeinterface 1991 2 0
    //   165: pop
    //   166: goto -96 -> 70
    //   169: astore_2
    //   170: aload_3
    //   171: astore_0
    //   172: aload_2
    //   173: astore_3
    //   174: aload_0
    //   175: astore_2
    //   176: new 781	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   183: ldc_w 2014
    //   186: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: aload_1
    //   190: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: aload_3
    //   194: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   197: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   200: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   203: aload_0
    //   204: ifnull +7 -> 211
    //   207: aload_0
    //   208: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   211: iconst_0
    //   212: ireturn
    //   213: aload_2
    //   214: aload 5
    //   216: invokeinterface 1991 2 0
    //   221: pop
    //   222: aload_2
    //   223: invokeinterface 2009 1 0
    //   228: aload_3
    //   229: ifnull +7 -> 236
    //   232: aload_3
    //   233: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   236: iconst_0
    //   237: ireturn
    //   238: astore_0
    //   239: aload_0
    //   240: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   243: iconst_0
    //   244: ireturn
    //   245: astore_0
    //   246: aload_0
    //   247: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   250: iconst_0
    //   251: ireturn
    //   252: astore_0
    //   253: aload_2
    //   254: ifnull +7 -> 261
    //   257: aload_2
    //   258: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   261: aload_0
    //   262: athrow
    //   263: astore_1
    //   264: aload_1
    //   265: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   268: goto -7 -> 261
    //   271: astore_0
    //   272: aload_3
    //   273: astore_2
    //   274: goto -21 -> 253
    //   277: astore_3
    //   278: aload 4
    //   280: astore_0
    //   281: goto -107 -> 174
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	paramList	List<String>
    //   0	284	1	paramString	String
    //   1	157	2	localXmlSerializer	org.xmlpull.v1.XmlSerializer
    //   169	4	2	localIOException1	IOException
    //   175	99	2	localObject1	Object
    //   28	245	3	localObject2	Object
    //   277	1	3	localIOException2	IOException
    //   3	276	4	str1	String
    //   11	204	5	str2	String
    // Exception table:
    //   from	to	target	type
    //   29	70	169	java/io/IOException
    //   70	117	169	java/io/IOException
    //   122	166	169	java/io/IOException
    //   213	228	169	java/io/IOException
    //   232	236	238	java/io/IOException
    //   207	211	245	java/io/IOException
    //   13	29	252	finally
    //   176	203	252	finally
    //   257	261	263	java/io/IOException
    //   29	70	271	finally
    //   70	117	271	finally
    //   122	166	271	finally
    //   213	228	271	finally
    //   13	29	277	java/io/IOException
  }
  
  /* Error */
  private static boolean saveXmlLocked(String paramString, HashMap<String, String> paramHashMap)
  {
    // Byte code:
    //   0: new 781	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 2021
    //   10: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_0
    //   14: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   20: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   23: aload_1
    //   24: ifnonnull +5 -> 29
    //   27: iconst_0
    //   28: ireturn
    //   29: ldc_w 1958
    //   32: invokestatic 1963	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   35: astore 4
    //   37: aconst_null
    //   38: astore_2
    //   39: aconst_null
    //   40: astore_3
    //   41: new 1965	java/io/FileOutputStream
    //   44: dup
    //   45: new 1503	java/io/File
    //   48: dup
    //   49: aload_0
    //   50: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   53: invokespecial 1966	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   56: astore_0
    //   57: invokestatic 1970	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   60: astore_2
    //   61: aload_2
    //   62: aload_0
    //   63: ldc_w 1972
    //   66: invokeinterface 1978 3 0
    //   71: aload_2
    //   72: aconst_null
    //   73: iconst_1
    //   74: invokestatic 1983	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   77: invokeinterface 1987 3 0
    //   82: aload_2
    //   83: aload 4
    //   85: invokeinterface 1991 2 0
    //   90: pop
    //   91: aload_2
    //   92: aconst_null
    //   93: ldc_w 1863
    //   96: invokeinterface 1997 3 0
    //   101: pop
    //   102: aload_2
    //   103: aload 4
    //   105: invokeinterface 1991 2 0
    //   110: pop
    //   111: aload_1
    //   112: invokevirtual 2025	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   115: invokeinterface 1385 1 0
    //   120: astore_1
    //   121: aload_1
    //   122: invokeinterface 1253 1 0
    //   127: ifeq +119 -> 246
    //   130: aload_1
    //   131: invokeinterface 1257 1 0
    //   136: checkcast 2027	java/util/Map$Entry
    //   139: astore_3
    //   140: aload_2
    //   141: aconst_null
    //   142: ldc_w 1759
    //   145: invokeinterface 1997 3 0
    //   150: pop
    //   151: aload_2
    //   152: aconst_null
    //   153: ldc_w 1761
    //   156: aload_3
    //   157: invokeinterface 2030 1 0
    //   162: checkcast 1088	java/lang/String
    //   165: invokeinterface 2003 4 0
    //   170: pop
    //   171: aload_2
    //   172: aload_3
    //   173: invokeinterface 2033 1 0
    //   178: checkcast 1088	java/lang/String
    //   181: invokeinterface 1991 2 0
    //   186: pop
    //   187: aload_2
    //   188: aconst_null
    //   189: ldc_w 1759
    //   192: invokeinterface 2006 3 0
    //   197: pop
    //   198: aload_2
    //   199: aload 4
    //   201: invokeinterface 1991 2 0
    //   206: pop
    //   207: goto -86 -> 121
    //   210: astore_1
    //   211: aload_0
    //   212: astore_2
    //   213: new 781	java/lang/StringBuilder
    //   216: dup
    //   217: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   220: ldc_w 2014
    //   223: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: aload_1
    //   227: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   230: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokestatic 1755	com/android/server/am/OnePlusProcessManager:myLog	(Ljava/lang/String;)V
    //   236: aload_0
    //   237: ifnull +7 -> 244
    //   240: aload_0
    //   241: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   244: iconst_0
    //   245: ireturn
    //   246: aload_2
    //   247: aconst_null
    //   248: ldc_w 1863
    //   251: invokeinterface 2006 3 0
    //   256: pop
    //   257: aload_2
    //   258: aload 4
    //   260: invokeinterface 1991 2 0
    //   265: pop
    //   266: aload_2
    //   267: invokeinterface 2009 1 0
    //   272: aload_0
    //   273: ifnull +7 -> 280
    //   276: aload_0
    //   277: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   280: iconst_1
    //   281: ireturn
    //   282: astore_0
    //   283: aload_0
    //   284: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   287: iconst_1
    //   288: ireturn
    //   289: astore_0
    //   290: aload_0
    //   291: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   294: iconst_0
    //   295: ireturn
    //   296: astore_0
    //   297: aload_2
    //   298: ifnull +7 -> 305
    //   301: aload_2
    //   302: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   305: aload_0
    //   306: athrow
    //   307: astore_1
    //   308: aload_1
    //   309: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   312: goto -7 -> 305
    //   315: astore_1
    //   316: aload_0
    //   317: astore_2
    //   318: aload_1
    //   319: astore_0
    //   320: goto -23 -> 297
    //   323: astore_1
    //   324: aload_3
    //   325: astore_0
    //   326: goto -115 -> 211
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	329	0	paramString	String
    //   0	329	1	paramHashMap	HashMap<String, String>
    //   38	280	2	localObject	Object
    //   40	285	3	localEntry	java.util.Map.Entry
    //   35	224	4	str	String
    // Exception table:
    //   from	to	target	type
    //   57	121	210	java/io/IOException
    //   121	207	210	java/io/IOException
    //   246	272	210	java/io/IOException
    //   276	280	282	java/io/IOException
    //   240	244	289	java/io/IOException
    //   41	57	296	finally
    //   213	236	296	finally
    //   301	305	307	java/io/IOException
    //   57	121	315	finally
    //   121	207	315	finally
    //   246	272	315	finally
    //   41	57	323	java/io/IOException
  }
  
  private void scheduleForegroundChangeMessage(int paramInt)
  {
    Message localMessage = Message.obtain();
    localMessage.what = 119;
    localMessage.arg1 = paramInt;
    this.mOneplusProcessHandler.sendMessageDelayed(localMessage, 10000L);
  }
  
  private void scheduleResumeMessage(int paramInt1, long paramLong, String paramString, int paramInt2)
  {
    synchronized (mAdjustUids)
    {
      mAdjustUids.add(Integer.valueOf(paramInt1));
      ??? = Message.obtain();
      ((Message)???).what = (paramInt1 - 10000);
      ((Message)???).arg1 = paramInt2;
      ((Message)???).arg2 = paramInt1;
      ((Message)???).obj = paramString;
      this.mResumeProcessHandler.sendMessageDelayed((Message)???, paramLong);
      return;
    }
  }
  
  private boolean scheduleResumeUid(int paramInt, String paramString)
  {
    return scheduleResumeUid(paramInt, paramString, 1);
  }
  
  private boolean scheduleResumeUid(int paramInt1, String paramString, int paramInt2)
  {
    if (!mBPMStatus) {
      return false;
    }
    if (paramInt1 < 10000) {
      return false;
    }
    int i;
    if (mPendingUid == paramInt1) {
      i = 1;
    }
    while ((isUidSuspended(Integer.valueOf(paramInt1).intValue())) || (i != 0)) {
      synchronized (mMessageLock)
      {
        this.mSuspendProcessHandler.removeMessages(paramInt1 - 10000);
        boolean bool = this.mResumeProcessHandler.hasMessages(paramInt1 - 10000);
        if (bool)
        {
          return true;
          i = 0;
        }
        else
        {
          scheduleResumeMessage(paramInt1, 0L, paramString, paramInt2);
          synchronized (mLock)
          {
            Binder.setBlockUid(paramInt1, false);
            if (DEBUG_ONEPLUS) {
              Slog.d("OnePlusProcessManager", "scheduleResumeUid =" + paramInt1 + " why = " + paramString);
            }
            return true;
          }
        }
      }
    }
    if (DEBUG) {
      Slog.d("OnePlusProcessManager", "scheduleResumeUid -> adjustSuspendMessage for uid " + paramInt1 + " delay code " + paramInt2);
    }
    if (paramInt2 == 2)
    {
      adjustSuspendMessage(paramInt1, paramString, 20000L);
      return false;
    }
    adjustSuspendMessage(paramInt1, paramString, suspendUidDelayTime);
    return false;
  }
  
  private void scheduleSuspendUid(int paramInt, long paramLong)
  {
    synchronized (this.mFrontActivityUids)
    {
      if (this.mFrontActivityUids.contains(Integer.valueOf(paramInt)))
      {
        if (DEBUG) {
          Slog.d("OnePlusProcessManager", "scheduleSuspendUid not suspend FG Uid" + paramInt);
        }
        mDoThawedUids.remove(Integer.valueOf(paramInt));
        return;
      }
      if (DEBUG) {
        Slog.d("OnePlusProcessManager", "scheduleSuspendUid =" + paramInt + " delay= " + paramLong);
      }
      ??? = Message.obtain();
      ((Message)???).what = (paramInt - 10000);
      ((Message)???).arg1 = paramInt;
      this.mSuspendProcessHandler.sendMessageDelayed((Message)???, paramLong);
      return;
    }
  }
  
  public static void sendSignal(int paramInt1, int paramInt2)
  {
    Process.sendSignal(paramInt1, paramInt2);
  }
  
  /* Error */
  private boolean setCGroupStateLocked(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 574	com/android/server/am/OnePlusProcessManager:mCanFrozenUids	Ljava/util/ArrayList;
    //   3: astore 4
    //   5: aload 4
    //   7: monitorenter
    //   8: getstatic 574	com/android/server/am/OnePlusProcessManager:mCanFrozenUids	Ljava/util/ArrayList;
    //   11: iload_1
    //   12: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   15: invokevirtual 2083	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   18: istore_3
    //   19: iload_3
    //   20: iflt +205 -> 225
    //   23: getstatic 574	com/android/server/am/OnePlusProcessManager:mCanFrozenUids	Ljava/util/ArrayList;
    //   26: iload_3
    //   27: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   30: checkcast 1001	java/lang/Integer
    //   33: astore 6
    //   35: aload 4
    //   37: monitorexit
    //   38: iload_1
    //   39: sipush 10000
    //   42: if_icmplt +1327 -> 1369
    //   45: aconst_null
    //   46: astore 5
    //   48: aconst_null
    //   49: astore 7
    //   51: aload 5
    //   53: astore 4
    //   55: new 781	java/lang/StringBuilder
    //   58: dup
    //   59: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   62: ldc -22
    //   64: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: iload_1
    //   68: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   71: ldc_w 2085
    //   74: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   80: astore 8
    //   82: aload 5
    //   84: astore 4
    //   86: new 1503	java/io/File
    //   89: dup
    //   90: aload 8
    //   92: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   95: astore 9
    //   97: aload 5
    //   99: astore 4
    //   101: aload 9
    //   103: invokevirtual 1507	java/io/File:exists	()Z
    //   106: ifne +153 -> 259
    //   109: aload 5
    //   111: astore 4
    //   113: aload 9
    //   115: invokevirtual 2089	java/io/File:getParentFile	()Ljava/io/File;
    //   118: astore 10
    //   120: aload 5
    //   122: astore 4
    //   124: aload 10
    //   126: invokevirtual 1507	java/io/File:exists	()Z
    //   129: ifne +13 -> 142
    //   132: aload 5
    //   134: astore 4
    //   136: aload 10
    //   138: invokevirtual 2092	java/io/File:mkdirs	()Z
    //   141: pop
    //   142: aload 5
    //   144: astore 4
    //   146: aload 9
    //   148: invokevirtual 2095	java/io/File:createNewFile	()Z
    //   151: pop
    //   152: aload 5
    //   154: astore 4
    //   156: aload 9
    //   158: invokevirtual 1507	java/io/File:exists	()Z
    //   161: ifne +98 -> 259
    //   164: aload 5
    //   166: astore 4
    //   168: ldc -70
    //   170: new 781	java/lang/StringBuilder
    //   173: dup
    //   174: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   177: ldc_w 2097
    //   180: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: aload 8
    //   185: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   191: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   194: pop
    //   195: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   198: astore 4
    //   200: aload 4
    //   202: monitorenter
    //   203: aload_0
    //   204: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   207: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   210: ifeq +10 -> 220
    //   213: aload_0
    //   214: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   217: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   220: aload 4
    //   222: monitorexit
    //   223: iconst_0
    //   224: ireturn
    //   225: iload_1
    //   226: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   229: astore 6
    //   231: getstatic 574	com/android/server/am/OnePlusProcessManager:mCanFrozenUids	Ljava/util/ArrayList;
    //   234: aload 6
    //   236: invokevirtual 1012	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   239: pop
    //   240: goto -205 -> 35
    //   243: astore 5
    //   245: aload 4
    //   247: monitorexit
    //   248: aload 5
    //   250: athrow
    //   251: astore 5
    //   253: aload 4
    //   255: monitorexit
    //   256: aload 5
    //   258: athrow
    //   259: aload 5
    //   261: astore 4
    //   263: new 1965	java/io/FileOutputStream
    //   266: dup
    //   267: aload 8
    //   269: invokespecial 2106	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   272: astore 5
    //   274: iload_2
    //   275: ifeq +745 -> 1020
    //   278: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   281: astore 4
    //   283: aload 4
    //   285: monitorenter
    //   286: getstatic 643	com/android/server/am/OnePlusProcessManager:mAdjustUids	Ljava/util/HashSet;
    //   289: astore 7
    //   291: aload 7
    //   293: monitorenter
    //   294: getstatic 643	com/android/server/am/OnePlusProcessManager:mAdjustUids	Ljava/util/HashSet;
    //   297: iload_1
    //   298: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   301: invokevirtual 1101	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   304: istore_2
    //   305: aload 7
    //   307: monitorexit
    //   308: iload_2
    //   309: ifne +279 -> 588
    //   312: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   315: iload_1
    //   316: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   319: invokevirtual 2083	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   322: istore_3
    //   323: iload_3
    //   324: ifge +19 -> 343
    //   327: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   330: iload_1
    //   331: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   334: invokevirtual 1012	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   337: pop
    //   338: iload_1
    //   339: iconst_1
    //   340: invokestatic 2053	android/os/Binder:setBlockUid	(IZ)V
    //   343: aload 4
    //   345: monitorexit
    //   346: getstatic 643	com/android/server/am/OnePlusProcessManager:mAdjustUids	Ljava/util/HashSet;
    //   349: astore 4
    //   351: aload 4
    //   353: monitorenter
    //   354: getstatic 643	com/android/server/am/OnePlusProcessManager:mAdjustUids	Ljava/util/HashSet;
    //   357: iload_1
    //   358: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   361: invokevirtual 1101	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   364: ifeq +458 -> 822
    //   367: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   370: astore 6
    //   372: aload 6
    //   374: monitorenter
    //   375: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   378: iload_1
    //   379: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   382: invokevirtual 2083	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   385: istore_3
    //   386: iload_3
    //   387: iflt +16 -> 403
    //   390: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   393: iload_3
    //   394: invokevirtual 2108	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   397: pop
    //   398: iload_1
    //   399: iconst_0
    //   400: invokestatic 2053	android/os/Binder:setBlockUid	(IZ)V
    //   403: aload 6
    //   405: monitorexit
    //   406: getstatic 559	com/android/server/am/OnePlusProcessManager:DEBUG_ONEPLUS	Z
    //   409: ifeq +35 -> 444
    //   412: ldc -70
    //   414: new 781	java/lang/StringBuilder
    //   417: dup
    //   418: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   421: ldc_w 2110
    //   424: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   427: iload_1
    //   428: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   431: ldc_w 2112
    //   434: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   437: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   440: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   443: pop
    //   444: aload 4
    //   446: monitorexit
    //   447: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   450: astore 4
    //   452: aload 4
    //   454: monitorenter
    //   455: aload_0
    //   456: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   459: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   462: ifeq +10 -> 472
    //   465: aload_0
    //   466: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   469: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   472: aload 4
    //   474: monitorexit
    //   475: aload 5
    //   477: ifnull +918 -> 1395
    //   480: aload 5
    //   482: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   485: iconst_0
    //   486: ireturn
    //   487: astore 6
    //   489: aload 7
    //   491: monitorexit
    //   492: aload 6
    //   494: athrow
    //   495: astore 6
    //   497: aload 4
    //   499: monitorexit
    //   500: aload 6
    //   502: athrow
    //   503: astore 6
    //   505: aload 5
    //   507: astore 4
    //   509: ldc -70
    //   511: new 781	java/lang/StringBuilder
    //   514: dup
    //   515: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   518: ldc_w 2114
    //   521: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   524: aload 6
    //   526: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   529: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   532: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   535: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   538: pop
    //   539: aload 5
    //   541: astore 4
    //   543: aload 6
    //   545: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   548: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   551: astore 4
    //   553: aload 4
    //   555: monitorenter
    //   556: aload_0
    //   557: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   560: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   563: ifeq +10 -> 573
    //   566: aload_0
    //   567: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   570: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   573: aload 4
    //   575: monitorexit
    //   576: aload 5
    //   578: ifnull +8 -> 586
    //   581: aload 5
    //   583: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   586: iconst_0
    //   587: ireturn
    //   588: getstatic 559	com/android/server/am/OnePlusProcessManager:DEBUG_ONEPLUS	Z
    //   591: ifeq +35 -> 626
    //   594: ldc -70
    //   596: new 781	java/lang/StringBuilder
    //   599: dup
    //   600: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   603: ldc_w 2110
    //   606: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   609: iload_1
    //   610: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   613: ldc_w 2112
    //   616: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   619: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   622: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   625: pop
    //   626: aload 4
    //   628: monitorexit
    //   629: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   632: astore 4
    //   634: aload 4
    //   636: monitorenter
    //   637: aload_0
    //   638: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   641: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   644: ifeq +10 -> 654
    //   647: aload_0
    //   648: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   651: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   654: aload 4
    //   656: monitorexit
    //   657: aload 5
    //   659: ifnull +738 -> 1397
    //   662: aload 5
    //   664: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   667: iconst_0
    //   668: ireturn
    //   669: astore 5
    //   671: aload 4
    //   673: monitorexit
    //   674: aload 5
    //   676: athrow
    //   677: astore 4
    //   679: ldc -70
    //   681: new 781	java/lang/StringBuilder
    //   684: dup
    //   685: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   688: ldc_w 2117
    //   691: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   694: aload 4
    //   696: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   699: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   702: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   705: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   708: pop
    //   709: aload 4
    //   711: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   714: iconst_0
    //   715: ireturn
    //   716: astore 7
    //   718: aload 6
    //   720: monitorexit
    //   721: aload 7
    //   723: athrow
    //   724: astore 6
    //   726: aload 4
    //   728: monitorexit
    //   729: aload 6
    //   731: athrow
    //   732: astore 4
    //   734: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   737: astore 6
    //   739: aload 6
    //   741: monitorenter
    //   742: aload_0
    //   743: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   746: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   749: ifeq +10 -> 759
    //   752: aload_0
    //   753: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   756: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   759: aload 6
    //   761: monitorexit
    //   762: aload 5
    //   764: ifnull +8 -> 772
    //   767: aload 5
    //   769: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   772: aload 4
    //   774: athrow
    //   775: astore 5
    //   777: aload 4
    //   779: monitorexit
    //   780: aload 5
    //   782: athrow
    //   783: astore 4
    //   785: ldc -70
    //   787: new 781	java/lang/StringBuilder
    //   790: dup
    //   791: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   794: ldc_w 2117
    //   797: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   800: aload 4
    //   802: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   805: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   808: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   811: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   814: pop
    //   815: aload 4
    //   817: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   820: iconst_0
    //   821: ireturn
    //   822: aload 4
    //   824: monitorexit
    //   825: iload_3
    //   826: ifge +120 -> 946
    //   829: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   832: astore 4
    //   834: aload 4
    //   836: monitorenter
    //   837: getstatic 608	com/android/server/am/OnePlusProcessManager:mScreen_ON	Z
    //   840: ifne +15 -> 855
    //   843: aload_0
    //   844: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   847: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   850: istore_2
    //   851: iload_2
    //   852: ifeq +134 -> 986
    //   855: aload 4
    //   857: monitorexit
    //   858: aload 6
    //   860: ifnull +86 -> 946
    //   863: aload 6
    //   865: monitorenter
    //   866: aload 5
    //   868: ldc_w 2119
    //   871: invokevirtual 2123	java/lang/String:getBytes	()[B
    //   874: invokevirtual 2127	java/io/FileOutputStream:write	([B)V
    //   877: getstatic 559	com/android/server/am/OnePlusProcessManager:DEBUG_ONEPLUS	Z
    //   880: ifeq +35 -> 915
    //   883: ldc -70
    //   885: new 781	java/lang/StringBuilder
    //   888: dup
    //   889: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   892: ldc_w 2129
    //   895: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   898: iload_1
    //   899: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   902: ldc_w 2131
    //   905: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   908: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   911: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   914: pop
    //   915: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   918: astore 4
    //   920: aload 4
    //   922: monitorenter
    //   923: aload_0
    //   924: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   927: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   930: ifeq +10 -> 940
    //   933: aload_0
    //   934: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   937: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   940: aload 4
    //   942: monitorexit
    //   943: aload 6
    //   945: monitorexit
    //   946: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   949: astore 4
    //   951: aload 4
    //   953: monitorenter
    //   954: aload_0
    //   955: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   958: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   961: ifeq +10 -> 971
    //   964: aload_0
    //   965: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   968: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   971: aload 4
    //   973: monitorexit
    //   974: aload 5
    //   976: ifnull +417 -> 1393
    //   979: aload 5
    //   981: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   984: iconst_1
    //   985: ireturn
    //   986: aload_0
    //   987: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   990: invokevirtual 2134	android/os/PowerManager$WakeLock:acquire	()V
    //   993: goto -138 -> 855
    //   996: astore 6
    //   998: aload 4
    //   1000: monitorexit
    //   1001: aload 6
    //   1003: athrow
    //   1004: astore 7
    //   1006: aload 4
    //   1008: monitorexit
    //   1009: aload 7
    //   1011: athrow
    //   1012: astore 4
    //   1014: aload 6
    //   1016: monitorexit
    //   1017: aload 4
    //   1019: athrow
    //   1020: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   1023: astore 4
    //   1025: aload 4
    //   1027: monitorenter
    //   1028: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   1031: iload_1
    //   1032: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1035: invokevirtual 2083	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   1038: istore_3
    //   1039: iload_3
    //   1040: iflt +16 -> 1056
    //   1043: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   1046: iload_3
    //   1047: invokevirtual 2108	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   1050: pop
    //   1051: iload_1
    //   1052: iconst_0
    //   1053: invokestatic 2053	android/os/Binder:setBlockUid	(IZ)V
    //   1056: aload 4
    //   1058: monitorexit
    //   1059: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   1062: astore 4
    //   1064: aload 4
    //   1066: monitorenter
    //   1067: getstatic 608	com/android/server/am/OnePlusProcessManager:mScreen_ON	Z
    //   1070: ifne +15 -> 1085
    //   1073: aload_0
    //   1074: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   1077: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   1080: istore_2
    //   1081: iload_2
    //   1082: ifeq +111 -> 1193
    //   1085: aload 4
    //   1087: monitorexit
    //   1088: aload 6
    //   1090: ifnull -144 -> 946
    //   1093: aload 6
    //   1095: monitorenter
    //   1096: aload 5
    //   1098: ldc_w 2136
    //   1101: invokevirtual 2123	java/lang/String:getBytes	()[B
    //   1104: invokevirtual 2127	java/io/FileOutputStream:write	([B)V
    //   1107: getstatic 559	com/android/server/am/OnePlusProcessManager:DEBUG_ONEPLUS	Z
    //   1110: ifeq +35 -> 1145
    //   1113: ldc -70
    //   1115: new 781	java/lang/StringBuilder
    //   1118: dup
    //   1119: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   1122: ldc_w 2129
    //   1125: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1128: iload_1
    //   1129: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1132: ldc_w 2138
    //   1135: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1138: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1141: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1144: pop
    //   1145: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   1148: astore 4
    //   1150: aload 4
    //   1152: monitorenter
    //   1153: getstatic 608	com/android/server/am/OnePlusProcessManager:mScreen_ON	Z
    //   1156: ifne +20 -> 1176
    //   1159: aload_0
    //   1160: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   1163: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   1166: ifeq +10 -> 1176
    //   1169: aload_0
    //   1170: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   1173: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   1176: aload 4
    //   1178: monitorexit
    //   1179: aload 6
    //   1181: monitorexit
    //   1182: goto -236 -> 946
    //   1185: astore 6
    //   1187: aload 4
    //   1189: monitorexit
    //   1190: aload 6
    //   1192: athrow
    //   1193: aload_0
    //   1194: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   1197: invokevirtual 2134	android/os/PowerManager$WakeLock:acquire	()V
    //   1200: goto -115 -> 1085
    //   1203: astore 6
    //   1205: aload 4
    //   1207: monitorexit
    //   1208: aload 6
    //   1210: athrow
    //   1211: astore 7
    //   1213: aload 4
    //   1215: monitorexit
    //   1216: aload 7
    //   1218: athrow
    //   1219: astore 4
    //   1221: aload 6
    //   1223: monitorexit
    //   1224: aload 4
    //   1226: athrow
    //   1227: astore 5
    //   1229: aload 4
    //   1231: monitorexit
    //   1232: aload 5
    //   1234: athrow
    //   1235: astore 4
    //   1237: ldc -70
    //   1239: new 781	java/lang/StringBuilder
    //   1242: dup
    //   1243: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   1246: ldc_w 2117
    //   1249: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1252: aload 4
    //   1254: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   1257: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1260: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1263: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1266: pop
    //   1267: aload 4
    //   1269: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   1272: iconst_1
    //   1273: ireturn
    //   1274: astore 5
    //   1276: aload 4
    //   1278: monitorexit
    //   1279: aload 5
    //   1281: athrow
    //   1282: astore 4
    //   1284: ldc -70
    //   1286: new 781	java/lang/StringBuilder
    //   1289: dup
    //   1290: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   1293: ldc_w 2117
    //   1296: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1299: aload 4
    //   1301: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   1304: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1307: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1310: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1313: pop
    //   1314: aload 4
    //   1316: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   1319: iconst_0
    //   1320: ireturn
    //   1321: astore 4
    //   1323: aload 6
    //   1325: monitorexit
    //   1326: aload 4
    //   1328: athrow
    //   1329: astore 5
    //   1331: ldc -70
    //   1333: new 781	java/lang/StringBuilder
    //   1336: dup
    //   1337: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   1340: ldc_w 2117
    //   1343: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1346: aload 5
    //   1348: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   1351: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1354: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1357: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1360: pop
    //   1361: aload 5
    //   1363: invokevirtual 1740	java/io/IOException:printStackTrace	()V
    //   1366: goto -594 -> 772
    //   1369: iconst_1
    //   1370: ireturn
    //   1371: astore 6
    //   1373: aload 4
    //   1375: astore 5
    //   1377: aload 6
    //   1379: astore 4
    //   1381: goto -647 -> 734
    //   1384: astore 6
    //   1386: aload 7
    //   1388: astore 5
    //   1390: goto -885 -> 505
    //   1393: iconst_1
    //   1394: ireturn
    //   1395: iconst_0
    //   1396: ireturn
    //   1397: iconst_0
    //   1398: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1399	0	this	OnePlusProcessManager
    //   0	1399	1	paramInt	int
    //   0	1399	2	paramBoolean	boolean
    //   18	1029	3	i	int
    //   3	669	4	localObject1	Object
    //   677	50	4	localIOException1	IOException
    //   732	46	4	localObject2	Object
    //   783	40	4	localIOException2	IOException
    //   1012	6	4	localObject4	Object
    //   1219	11	4	localObject6	Object
    //   1235	42	4	localIOException3	IOException
    //   1282	33	4	localIOException4	IOException
    //   1321	53	4	localObject7	Object
    //   1379	1	4	localObject8	Object
    //   46	119	5	localObject9	Object
    //   243	6	5	localObject10	Object
    //   251	9	5	localObject11	Object
    //   272	391	5	localFileOutputStream	java.io.FileOutputStream
    //   669	99	5	localObject12	Object
    //   775	322	5	localObject13	Object
    //   1227	6	5	localObject14	Object
    //   1274	6	5	localObject15	Object
    //   1329	33	5	localIOException5	IOException
    //   1375	14	5	localObject16	Object
    //   33	371	6	localObject17	Object
    //   487	6	6	localObject18	Object
    //   495	6	6	localObject19	Object
    //   503	216	6	localIOException6	IOException
    //   724	6	6	localObject20	Object
    //   737	207	6	localObject21	Object
    //   996	184	6	localObject22	Object
    //   1185	6	6	localObject23	Object
    //   1203	121	6	localObject24	Object
    //   1371	7	6	localObject25	Object
    //   1384	1	6	localIOException7	IOException
    //   49	441	7	localHashSet	HashSet
    //   716	6	7	localObject26	Object
    //   1004	6	7	localObject27	Object
    //   1211	176	7	localObject28	Object
    //   80	188	8	str	String
    //   95	62	9	localFile1	File
    //   118	19	10	localFile2	File
    // Exception table:
    //   from	to	target	type
    //   8	19	243	finally
    //   23	35	243	finally
    //   225	240	243	finally
    //   203	220	251	finally
    //   294	305	487	finally
    //   286	294	495	finally
    //   305	308	495	finally
    //   312	323	495	finally
    //   327	343	495	finally
    //   489	495	495	finally
    //   588	626	495	finally
    //   278	286	503	java/io/IOException
    //   343	354	503	java/io/IOException
    //   444	447	503	java/io/IOException
    //   497	503	503	java/io/IOException
    //   626	629	503	java/io/IOException
    //   726	732	503	java/io/IOException
    //   822	825	503	java/io/IOException
    //   829	837	503	java/io/IOException
    //   855	858	503	java/io/IOException
    //   863	866	503	java/io/IOException
    //   943	946	503	java/io/IOException
    //   998	1004	503	java/io/IOException
    //   1014	1020	503	java/io/IOException
    //   1020	1028	503	java/io/IOException
    //   1056	1067	503	java/io/IOException
    //   1085	1088	503	java/io/IOException
    //   1093	1096	503	java/io/IOException
    //   1179	1182	503	java/io/IOException
    //   1187	1193	503	java/io/IOException
    //   1205	1211	503	java/io/IOException
    //   1221	1227	503	java/io/IOException
    //   637	654	669	finally
    //   662	667	677	java/io/IOException
    //   375	386	716	finally
    //   390	403	716	finally
    //   354	375	724	finally
    //   403	444	724	finally
    //   718	724	724	finally
    //   278	286	732	finally
    //   343	354	732	finally
    //   444	447	732	finally
    //   497	503	732	finally
    //   626	629	732	finally
    //   726	732	732	finally
    //   822	825	732	finally
    //   829	837	732	finally
    //   855	858	732	finally
    //   863	866	732	finally
    //   943	946	732	finally
    //   998	1004	732	finally
    //   1014	1020	732	finally
    //   1020	1028	732	finally
    //   1056	1067	732	finally
    //   1085	1088	732	finally
    //   1093	1096	732	finally
    //   1179	1182	732	finally
    //   1187	1193	732	finally
    //   1205	1211	732	finally
    //   1221	1227	732	finally
    //   455	472	775	finally
    //   480	485	783	java/io/IOException
    //   837	851	996	finally
    //   986	993	996	finally
    //   923	940	1004	finally
    //   866	915	1012	finally
    //   915	923	1012	finally
    //   940	943	1012	finally
    //   1006	1012	1012	finally
    //   1028	1039	1185	finally
    //   1043	1056	1185	finally
    //   1067	1081	1203	finally
    //   1193	1200	1203	finally
    //   1153	1176	1211	finally
    //   1096	1145	1219	finally
    //   1145	1153	1219	finally
    //   1176	1179	1219	finally
    //   1213	1219	1219	finally
    //   954	971	1227	finally
    //   979	984	1235	java/io/IOException
    //   556	573	1274	finally
    //   581	586	1282	java/io/IOException
    //   742	759	1321	finally
    //   767	772	1329	java/io/IOException
    //   55	82	1371	finally
    //   86	97	1371	finally
    //   101	109	1371	finally
    //   113	120	1371	finally
    //   124	132	1371	finally
    //   136	142	1371	finally
    //   146	152	1371	finally
    //   156	164	1371	finally
    //   168	195	1371	finally
    //   263	274	1371	finally
    //   509	539	1371	finally
    //   543	548	1371	finally
    //   55	82	1384	java/io/IOException
    //   86	97	1384	java/io/IOException
    //   101	109	1384	java/io/IOException
    //   113	120	1384	java/io/IOException
    //   124	132	1384	java/io/IOException
    //   136	142	1384	java/io/IOException
    //   146	152	1384	java/io/IOException
    //   156	164	1384	java/io/IOException
    //   168	195	1384	java/io/IOException
    //   263	274	1384	java/io/IOException
  }
  
  public static void setCurrentInputMethod(ServiceInfo paramServiceInfo)
  {
    sInputMethodUid = paramServiceInfo.applicationInfo.uid;
  }
  
  public static boolean setScreenState(boolean paramBoolean)
  {
    synchronized (mScreenLock)
    {
      mScreen_ON = paramBoolean;
      return true;
    }
  }
  
  static final boolean skipBroadcast(BroadcastFilter paramBroadcastFilter, BroadcastRecord paramBroadcastRecord, boolean paramBoolean)
  {
    if (!isSuppoerted) {
      return false;
    }
    if (!mBPMStatus) {
      return false;
    }
    if (mOnePlusProcessManager == null) {
      return false;
    }
    Trace.traceBegin(8L, "skipBroadcast");
    if (mOnePlusProcessManager.skipBroadcast(paramBroadcastFilter.receiverList.app, paramBroadcastRecord, paramBoolean))
    {
      if (DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "BPM Denial: receiving " + paramBroadcastRecord.intent.toString() + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")" + " due to sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")" + " is ordered " + paramBroadcastRecord.ordered + " ;  ordered " + paramBoolean);
      }
      Trace.traceEnd(8L);
      return true;
    }
    Trace.traceEnd(8L);
    return false;
  }
  
  private boolean skipBroadcast(ProcessRecord paramProcessRecord, BroadcastRecord paramBroadcastRecord, boolean paramBoolean)
  {
    if (paramProcessRecord == null) {
      return false;
    }
    if (paramProcessRecord.uid < 10000) {
      return false;
    }
    if (paramProcessRecord.info == null) {
      return false;
    }
    if ((paramProcessRecord.info.flags & 0x81) != 0) {
      return false;
    }
    String str1 = paramBroadcastRecord.intent.getAction();
    if (str1 != null) {
      if ((str1.equals("android.intent.action.ANY_DATA_STATE")) && (paramProcessRecord.info.packageName.equals("cn.kuwo.player"))) {
        return true;
      }
    }
    synchronized (this.mBlackBrdList)
    {
      Iterator localIterator = this.mBlackBrdList.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str1.equals(str2))
        {
          if (checkProcessRecord(paramProcessRecord))
          {
            scheduleResumeUid(paramProcessRecord.uid, "checkProcessRecord " + paramProcessRecord.uid + " for action =" + str2);
            return false;
          }
          if ((this.mUidState.get(paramProcessRecord.uid, 16) <= 4) || (this.mFrontActivityUids.contains(Integer.valueOf(paramProcessRecord.uid))) || (this.mFrontWindowTouchUids.contains(Integer.valueOf(paramProcessRecord.uid))))
          {
            scheduleResumeUid(paramProcessRecord.uid, "resume " + paramProcessRecord.uid + " for action =" + str2);
            return false;
          }
          if (DEBUG) {
            Slog.d("OnePlusProcessManager", "skip Broadcast: r=" + paramBroadcastRecord + "; app=" + paramProcessRecord);
          }
          return true;
        }
      }
      if ((paramBroadcastRecord.callingUid < 10000) || (paramProcessRecord.uid < 10000) || (paramBroadcastRecord.callingUid == paramProcessRecord.uid) || (str1 == null)) {
        break label542;
      }
      if ((str1.contains("AlarmTaskSchedule")) || (str1.contains("com.igexin.sdk.action")) || (str1.contains("AlarmTaskScheduleBak")))
      {
        if (DEBUG) {
          Slog.d("OnePlusProcessManager", "skip Broadcast: a=" + str1 + " r=" + paramBroadcastRecord + "; app=" + paramProcessRecord);
        }
        return true;
      }
    }
    if (this.mBlackAlarmList.contains(str1))
    {
      if (DEBUG) {
        Slog.d("OnePlusProcessManager", "skip Broadcast mBlackAlarmList: a=" + str1 + " r=" + paramBroadcastRecord + "; app=" + paramProcessRecord);
      }
      return true;
    }
    label542:
    scheduleResumeUid(paramProcessRecord.uid, "resume " + paramProcessRecord.uid + " for action =" + str1);
    return false;
  }
  
  private void startComputeUidTraffic(int paramInt)
  {
    Message localMessage = Message.obtain();
    localMessage.what = 116;
    localMessage.arg1 = paramInt;
    this.mOneplusProcessHandler.sendMessage(localMessage);
  }
  
  private void startResumeUid(int paramInt)
  {
    setCGroupState(paramInt, false);
    synchronized (mSuspendFailUidsCount)
    {
      mSuspendFailUidsCount.remove(Integer.valueOf(paramInt));
      if (DEBUG) {
        Slog.d("OnePlusProcessManager", "startResumeUid --------- end");
      }
      return;
    }
  }
  
  /* Error */
  private boolean startSuspendUid(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: iconst_1
    //   3: invokevirtual 2194	com/android/server/am/OnePlusProcessManager:setCGroupState	(IZ)Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +215 -> 223
    //   11: getstatic 579	com/android/server/am/OnePlusProcessManager:mSuspendFailUidsCount	Landroid/util/ArrayMap;
    //   14: astore_3
    //   15: aload_3
    //   16: monitorenter
    //   17: getstatic 579	com/android/server/am/OnePlusProcessManager:mSuspendFailUidsCount	Landroid/util/ArrayMap;
    //   20: iload_1
    //   21: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   24: invokevirtual 1041	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   27: pop
    //   28: aload_3
    //   29: monitorexit
    //   30: aload_0
    //   31: getfield 724	com/android/server/am/OnePlusProcessManager:mPowerAdjust	Ljava/util/HashSet;
    //   34: iload_1
    //   35: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   38: invokevirtual 1101	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   41: ifne +26 -> 67
    //   44: aload_0
    //   45: getfield 724	com/android/server/am/OnePlusProcessManager:mPowerAdjust	Ljava/util/HashSet;
    //   48: iload_1
    //   49: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   52: invokevirtual 1110	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   55: pop
    //   56: aload_0
    //   57: getfield 691	com/android/server/am/OnePlusProcessManager:mPowerManager	Landroid/os/IPowerManager;
    //   60: iload_1
    //   61: iconst_1
    //   62: invokeinterface 2201 3 0
    //   67: aload_0
    //   68: getfield 705	com/android/server/am/OnePlusProcessManager:mStatusLocationUids	Ljava/util/ArrayList;
    //   71: iload_1
    //   72: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   75: invokevirtual 1145	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   78: ifeq +12 -> 90
    //   81: getstatic 604	com/android/server/am/OnePlusProcessManager:lm	Lcom/android/server/LocationManagerService;
    //   84: astore_3
    //   85: iload_1
    //   86: iconst_1
    //   87: invokestatic 2204	com/android/server/LocationManagerService:updateUidBlock	(IZ)V
    //   90: aload_0
    //   91: getfield 703	com/android/server/am/OnePlusProcessManager:mGpsReceiverLocationUids	Ljava/util/ArrayList;
    //   94: iload_1
    //   95: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   98: invokevirtual 1145	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   101: ifne +17 -> 118
    //   104: aload_0
    //   105: getfield 701	com/android/server/am/OnePlusProcessManager:mOtherReceiverLocationUids	Ljava/util/ArrayList;
    //   108: iload_1
    //   109: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   112: invokevirtual 1145	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   115: ifeq +11 -> 126
    //   118: getstatic 604	com/android/server/am/OnePlusProcessManager:lm	Lcom/android/server/LocationManagerService;
    //   121: iload_1
    //   122: iconst_1
    //   123: invokevirtual 2207	com/android/server/LocationManagerService:updateReceiverBlockRequest	(IZ)V
    //   126: getstatic 421	com/android/server/am/OnePlusProcessManager:isAlarmAdjust	Z
    //   129: ifeq +51 -> 180
    //   132: aload_0
    //   133: getfield 722	com/android/server/am/OnePlusProcessManager:mAlarmAdjust	Ljava/util/HashSet;
    //   136: iload_1
    //   137: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   140: invokevirtual 1101	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   143: ifne +37 -> 180
    //   146: aload_0
    //   147: getfield 722	com/android/server/am/OnePlusProcessManager:mAlarmAdjust	Ljava/util/HashSet;
    //   150: iload_1
    //   151: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   154: invokevirtual 1110	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   157: pop
    //   158: aload_0
    //   159: iload_1
    //   160: invokespecial 529	com/android/server/am/OnePlusProcessManager:getPackageNameForUid	(I)Ljava/lang/String;
    //   163: astore_3
    //   164: aload_3
    //   165: ifnull +15 -> 180
    //   168: aload_0
    //   169: getfield 693	com/android/server/am/OnePlusProcessManager:mAlarm	Landroid/app/IAlarmManager;
    //   172: aload_3
    //   173: iconst_1
    //   174: iconst_1
    //   175: invokeinterface 2213 4 0
    //   180: getstatic 647	com/android/server/am/OnePlusProcessManager:mUnFrozenReasonUids	Landroid/util/ArrayMap;
    //   183: astore_3
    //   184: aload_3
    //   185: monitorenter
    //   186: getstatic 647	com/android/server/am/OnePlusProcessManager:mUnFrozenReasonUids	Landroid/util/ArrayMap;
    //   189: iload_1
    //   190: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   193: invokevirtual 1041	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   196: pop
    //   197: aload_3
    //   198: monitorexit
    //   199: getstatic 645	com/android/server/am/OnePlusProcessManager:mNotAllowSensorUids	Ljava/util/ArrayList;
    //   202: astore_3
    //   203: aload_3
    //   204: monitorenter
    //   205: getstatic 645	com/android/server/am/OnePlusProcessManager:mNotAllowSensorUids	Ljava/util/ArrayList;
    //   208: iload_1
    //   209: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   212: invokevirtual 1012	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   215: pop
    //   216: aload_3
    //   217: monitorexit
    //   218: aload_0
    //   219: iload_1
    //   220: invokespecial 2215	com/android/server/am/OnePlusProcessManager:cancelNotificationsForUid	(I)V
    //   223: getstatic 552	com/android/server/am/OnePlusProcessManager:DEBUG	Z
    //   226: ifeq +45 -> 271
    //   229: ldc -70
    //   231: new 781	java/lang/StringBuilder
    //   234: dup
    //   235: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   238: ldc_w 2217
    //   241: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   244: iload_1
    //   245: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   248: ldc_w 2219
    //   251: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: iload_2
    //   255: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   258: ldc_w 2221
    //   261: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   267: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   270: pop
    //   271: iload_2
    //   272: ireturn
    //   273: astore 4
    //   275: aload_3
    //   276: monitorexit
    //   277: aload 4
    //   279: athrow
    //   280: astore_3
    //   281: ldc -70
    //   283: new 781	java/lang/StringBuilder
    //   286: dup
    //   287: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   290: ldc_w 2223
    //   293: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: aload_3
    //   297: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   300: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   306: pop
    //   307: goto -240 -> 67
    //   310: astore 4
    //   312: aload_3
    //   313: monitorexit
    //   314: aload 4
    //   316: athrow
    //   317: astore 4
    //   319: aload_3
    //   320: monitorexit
    //   321: aload 4
    //   323: athrow
    //   324: astore_3
    //   325: goto -145 -> 180
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	328	0	this	OnePlusProcessManager
    //   0	328	1	paramInt	int
    //   6	266	2	bool	boolean
    //   280	40	3	localException	Exception
    //   324	1	3	localRemoteException	RemoteException
    //   273	5	4	localObject2	Object
    //   310	5	4	localObject3	Object
    //   317	5	4	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   17	28	273	finally
    //   56	67	280	java/lang/Exception
    //   186	197	310	finally
    //   205	216	317	finally
    //   168	180	324	android/os/RemoteException
  }
  
  private void tryAddScreenOffTrafficUids()
  {
    synchronized (mUidTraffic)
    {
      Iterator localIterator = ((HashSet)mTrafficUids.clone()).iterator();
      if (localIterator.hasNext()) {
        computeUidTraffic(((Integer)localIterator.next()).intValue());
      }
    }
  }
  
  /* Error */
  private void updateBPMEnableState(boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 552	com/android/server/am/OnePlusProcessManager:DEBUG	Z
    //   3: ifeq +29 -> 32
    //   6: ldc -70
    //   8: new 781	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   15: ldc_w 2227
    //   18: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: iload_1
    //   22: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   25: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 800	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: iload_1
    //   33: ifne +61 -> 94
    //   36: getstatic 563	com/android/server/am/OnePlusProcessManager:mActivityManager	Lcom/android/server/am/ActivityManagerService;
    //   39: aload_0
    //   40: getfield 750	com/android/server/am/OnePlusProcessManager:mProcessObserver	Landroid/app/IProcessObserver;
    //   43: invokevirtual 2231	com/android/server/am/ActivityManagerService:unregisterProcessObserver	(Landroid/app/IProcessObserver;)V
    //   46: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   49: astore_3
    //   50: aload_3
    //   51: monitorenter
    //   52: iconst_0
    //   53: istore_2
    //   54: iload_2
    //   55: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   58: invokevirtual 1113	java/util/ArrayList:size	()I
    //   61: if_icmpge +31 -> 92
    //   64: aload_0
    //   65: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   68: iload_2
    //   69: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   72: checkcast 1001	java/lang/Integer
    //   75: invokevirtual 1039	java/lang/Integer:intValue	()I
    //   78: ldc_w 2232
    //   81: invokespecial 452	com/android/server/am/OnePlusProcessManager:scheduleResumeUid	(ILjava/lang/String;)Z
    //   84: pop
    //   85: iload_2
    //   86: iconst_1
    //   87: iadd
    //   88: istore_2
    //   89: goto -35 -> 54
    //   92: aload_3
    //   93: monitorexit
    //   94: aload_0
    //   95: invokevirtual 2235	com/android/server/am/OnePlusProcessManager:computeThreeAppState	()V
    //   98: iload_1
    //   99: ifeq +70 -> 169
    //   102: getstatic 563	com/android/server/am/OnePlusProcessManager:mActivityManager	Lcom/android/server/am/ActivityManagerService;
    //   105: aload_0
    //   106: getfield 750	com/android/server/am/OnePlusProcessManager:mProcessObserver	Landroid/app/IProcessObserver;
    //   109: invokevirtual 2238	com/android/server/am/ActivityManagerService:registerProcessObserver	(Landroid/app/IProcessObserver;)V
    //   112: aload_0
    //   113: getfield 712	com/android/server/am/OnePlusProcessManager:mRulesLock	Ljava/lang/Object;
    //   116: astore_3
    //   117: aload_3
    //   118: monitorenter
    //   119: iconst_0
    //   120: istore_2
    //   121: iload_2
    //   122: aload_0
    //   123: getfield 710	com/android/server/am/OnePlusProcessManager:mUidPidState	Landroid/util/SparseArray;
    //   126: invokevirtual 2239	android/util/SparseArray:size	()I
    //   129: if_icmpge +38 -> 167
    //   132: aload_0
    //   133: aload_0
    //   134: getfield 710	com/android/server/am/OnePlusProcessManager:mUidPidState	Landroid/util/SparseArray;
    //   137: iload_2
    //   138: invokevirtual 2242	android/util/SparseArray:keyAt	(I)I
    //   141: invokestatic 1005	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   144: invokevirtual 1039	java/lang/Integer:intValue	()I
    //   147: getstatic 620	com/android/server/am/OnePlusProcessManager:suspendUidDelayTime	J
    //   150: invokespecial 480	com/android/server/am/OnePlusProcessManager:scheduleSuspendUid	(IJ)V
    //   153: iload_2
    //   154: iconst_1
    //   155: iadd
    //   156: istore_2
    //   157: goto -36 -> 121
    //   160: astore 4
    //   162: aload_3
    //   163: monitorexit
    //   164: aload 4
    //   166: athrow
    //   167: aload_3
    //   168: monitorexit
    //   169: return
    //   170: astore 4
    //   172: aload_3
    //   173: monitorexit
    //   174: aload 4
    //   176: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	this	OnePlusProcessManager
    //   0	177	1	paramBoolean	boolean
    //   53	104	2	i	int
    //   49	124	3	localObject1	Object
    //   160	5	4	localObject2	Object
    //   170	5	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   54	85	160	finally
    //   121	153	170	finally
  }
  
  private void updateDozePolicyToDB(boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.i("OnePlusProcessManager", "updateDozePolicyToDB # flag=" + paramBoolean);
    }
    if (2 == this.mForceSwitch) {
      paramBoolean = false;
    }
    if (paramBoolean)
    {
      Settings.System.putInt(this.resolver, "doze_mode_policy", 1);
      return;
    }
    Settings.System.putInt(this.resolver, "doze_mode_policy", 0);
  }
  
  private void updateForegroundActivityChange(int paramInt)
  {
    Object localObject;
    if (this.mStatusLocationUids.contains(Integer.valueOf(paramInt)))
    {
      localObject = lm;
      LocationManagerService.updateUidBlock(paramInt, false);
    }
    if ((this.mGpsReceiverLocationUids.contains(Integer.valueOf(paramInt))) || (this.mOtherReceiverLocationUids.contains(Integer.valueOf(paramInt)))) {
      lm.updateReceiverBlockRequest(paramInt, false);
    }
    if ((isAlarmAdjust) && (this.mAlarmAdjust.contains(Integer.valueOf(paramInt))))
    {
      this.mAlarmAdjust.remove(Integer.valueOf(paramInt));
      localObject = getPackageNameForUid(paramInt);
      if (localObject == null) {}
    }
    try
    {
      this.mAlarm.setBlockAlarmUid((String)localObject, false, 1);
      try
      {
        this.mPowerManager.updateBlockedUids(paramInt, false);
        this.mPowerAdjust.remove(Integer.valueOf(paramInt));
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Slog.d("OnePlusProcessManager", "updateBlockedUids " + localException);
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public static void updateImportantUidChange(HashSet<Integer> paramHashSet)
  {
    if (!isSuppoerted) {
      return;
    }
    if (mOnePlusProcessManager != null)
    {
      mOnePlusProcessManager.handleUidChange(mOnePlusProcessManager.mImportantUids, paramHashSet);
      if (DEBUG_ONEPLUS) {
        Log.e("OnePlusProcessManager", "updateImportantUidChange change =" + paramHashSet);
      }
      mOnePlusProcessManager.mImportantUids.clear();
      mOnePlusProcessManager.mImportantUids.addAll(paramHashSet);
    }
  }
  
  public static void updateLocationReceiverUidsChange(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    updateLocationReceiverUidsChange(paramInt, paramBoolean1, paramBoolean2, false);
  }
  
  public static void updateLocationReceiverUidsChange(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (!isSuppoerted) {
      return;
    }
    if ((paramInt >= 10000) && (mOnePlusProcessManager != null))
    {
      if (!paramBoolean2) {
        break label156;
      }
      if (!paramBoolean1) {
        break label62;
      }
      if (!paramBoolean3) {
        break label47;
      }
      mOnePlusProcessManager.mGpsReceiverLocationUids.add(Integer.valueOf(paramInt));
    }
    label47:
    label62:
    label156:
    do
    {
      do
      {
        do
        {
          return;
          mOnePlusProcessManager.mOtherReceiverLocationUids.add(Integer.valueOf(paramInt));
          return;
          if (!paramBoolean3) {
            break;
          }
        } while (!mOnePlusProcessManager.mGpsReceiverLocationUids.contains(Integer.valueOf(paramInt)));
        mOnePlusProcessManager.mGpsReceiverLocationUids.remove(Integer.valueOf(paramInt));
        mOnePlusProcessManager.scheduleSuspendUid(paramInt, suspendUidDelayTime / 2L);
        return;
      } while (!mOnePlusProcessManager.mOtherReceiverLocationUids.contains(Integer.valueOf(paramInt)));
      mOnePlusProcessManager.mOtherReceiverLocationUids.remove(Integer.valueOf(paramInt));
      mOnePlusProcessManager.scheduleSuspendUid(paramInt, suspendUidDelayTime / 2L);
      return;
      if (paramBoolean1)
      {
        mOnePlusProcessManager.mStatusLocationUids.add(Integer.valueOf(paramInt));
        return;
      }
    } while (!mOnePlusProcessManager.mStatusLocationUids.contains(Integer.valueOf(paramInt)));
    mOnePlusProcessManager.mStatusLocationUids.remove(Integer.valueOf(paramInt));
  }
  
  public static void updateScreenState(boolean paramBoolean)
  {
    if (!isSuppoerted) {
      return;
    }
    if (!mBPMStatus)
    {
      if (paramBoolean)
      {
        mScreen_ON_ING = true;
        setScreenState(true);
      }
      return;
    }
    synchronized (mActivityManager)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (paramBoolean)
      {
        mScreenOffCount = 0;
        mScreen_ON_ING = true;
        setScreenState(true);
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  public static void updateTouchWindowUidChange(HashSet<Integer> paramHashSet)
  {
    if (!isSuppoerted) {
      return;
    }
    if (mOnePlusProcessManager != null)
    {
      mOnePlusProcessManager.handleUidChange(mOnePlusProcessManager.mFrontWindowTouchUids, paramHashSet);
      if (DEBUG_ONEPLUS) {
        Log.e("OnePlusProcessManager", "updateTouchWindowUidChange change =" + paramHashSet);
      }
      mOnePlusProcessManager.mFrontWindowTouchUids.clear();
      mOnePlusProcessManager.mFrontWindowTouchUids.addAll(paramHashSet);
    }
  }
  
  private static void writePidToTasksFile(int paramInt1, int paramInt2)
  {
    writePidToTasksFile(paramInt1, paramInt2, true);
  }
  
  /* Error */
  private static void writePidToTasksFile(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: iload_0
    //   1: sipush 10000
    //   4: if_icmpge +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore 6
    //   11: aconst_null
    //   12: astore 7
    //   14: aconst_null
    //   15: astore 8
    //   17: aconst_null
    //   18: astore 5
    //   20: aload 7
    //   22: astore 4
    //   24: aload 8
    //   26: astore_3
    //   27: new 781	java/lang/StringBuilder
    //   30: dup
    //   31: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   34: ldc -22
    //   36: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: iload_0
    //   40: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   43: ldc_w 2085
    //   46: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   52: astore 9
    //   54: aload 7
    //   56: astore 4
    //   58: aload 8
    //   60: astore_3
    //   61: new 1503	java/io/File
    //   64: dup
    //   65: aload 9
    //   67: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   70: astore 7
    //   72: aload 7
    //   74: invokevirtual 1507	java/io/File:exists	()Z
    //   77: ifne +63 -> 140
    //   80: aload 7
    //   82: invokevirtual 2089	java/io/File:getParentFile	()Ljava/io/File;
    //   85: astore_3
    //   86: aload_3
    //   87: invokevirtual 1507	java/io/File:exists	()Z
    //   90: ifne +8 -> 98
    //   93: aload_3
    //   94: invokevirtual 2092	java/io/File:mkdirs	()Z
    //   97: pop
    //   98: aload 7
    //   100: invokevirtual 2095	java/io/File:createNewFile	()Z
    //   103: pop
    //   104: aload 7
    //   106: invokevirtual 1507	java/io/File:exists	()Z
    //   109: ifne +31 -> 140
    //   112: ldc -70
    //   114: new 781	java/lang/StringBuilder
    //   117: dup
    //   118: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   121: ldc_w 2097
    //   124: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: aload 9
    //   129: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   138: pop
    //   139: return
    //   140: new 1965	java/io/FileOutputStream
    //   143: dup
    //   144: aload 9
    //   146: invokespecial 2106	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   149: astore_3
    //   150: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   153: astore 4
    //   155: aload 4
    //   157: monitorenter
    //   158: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   161: ifnull +24 -> 185
    //   164: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   167: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   170: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   173: ifeq +12 -> 185
    //   176: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   179: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   182: invokevirtual 2134	android/os/PowerManager$WakeLock:acquire	()V
    //   185: aload 4
    //   187: monitorexit
    //   188: aload_3
    //   189: ldc_w 2136
    //   192: invokevirtual 2123	java/lang/String:getBytes	()[B
    //   195: invokevirtual 2127	java/io/FileOutputStream:write	([B)V
    //   198: getstatic 598	com/android/server/am/OnePlusProcessManager:mWakeLock	Ljava/lang/Object;
    //   201: astore 4
    //   203: aload 4
    //   205: monitorenter
    //   206: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   209: ifnull +24 -> 233
    //   212: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   215: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   218: invokevirtual 2102	android/os/PowerManager$WakeLock:isHeld	()Z
    //   221: ifeq +12 -> 233
    //   224: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   227: getfield 921	com/android/server/am/OnePlusProcessManager:mWakeLockFrozen	Landroid/os/PowerManager$WakeLock;
    //   230: invokevirtual 2105	android/os/PowerManager$WakeLock:release	()V
    //   233: aload 4
    //   235: monitorexit
    //   236: new 781	java/lang/StringBuilder
    //   239: dup
    //   240: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   243: ldc -22
    //   245: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: iload_0
    //   249: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   252: ldc_w 2270
    //   255: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   261: astore 4
    //   263: new 1503	java/io/File
    //   266: dup
    //   267: aload 4
    //   269: invokespecial 1504	java/io/File:<init>	(Ljava/lang/String;)V
    //   272: astore 5
    //   274: aload 5
    //   276: invokevirtual 1507	java/io/File:exists	()Z
    //   279: ifne +268 -> 547
    //   282: aload 5
    //   284: invokevirtual 2089	java/io/File:getParentFile	()Ljava/io/File;
    //   287: invokevirtual 1507	java/io/File:exists	()Z
    //   290: ifne +18 -> 308
    //   293: aload 5
    //   295: invokevirtual 2089	java/io/File:getParentFile	()Ljava/io/File;
    //   298: invokevirtual 2092	java/io/File:mkdirs	()Z
    //   301: pop
    //   302: aload 5
    //   304: invokevirtual 2095	java/io/File:createNewFile	()Z
    //   307: pop
    //   308: aload 5
    //   310: invokevirtual 1507	java/io/File:exists	()Z
    //   313: ifne +234 -> 547
    //   316: ldc -70
    //   318: new 781	java/lang/StringBuilder
    //   321: dup
    //   322: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   325: aload 5
    //   327: invokevirtual 2273	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   330: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   333: ldc_w 2275
    //   336: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   339: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   342: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   345: pop
    //   346: aload_3
    //   347: ifnull +533 -> 880
    //   350: aload_3
    //   351: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   354: return
    //   355: astore 5
    //   357: aload 4
    //   359: monitorexit
    //   360: aload 5
    //   362: athrow
    //   363: astore 5
    //   365: aload_3
    //   366: astore 4
    //   368: ldc -70
    //   370: new 781	java/lang/StringBuilder
    //   373: dup
    //   374: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   377: ldc_w 2277
    //   380: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   383: iload_0
    //   384: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   387: ldc_w 2279
    //   390: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   393: iload_1
    //   394: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   397: ldc_w 2281
    //   400: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   403: iload_2
    //   404: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   407: ldc_w 2283
    //   410: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: ldc_w 2285
    //   416: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   419: iconst_m1
    //   420: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   423: ldc_w 2283
    //   426: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   429: aload 5
    //   431: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   434: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   437: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   440: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   443: pop
    //   444: iload_2
    //   445: ifeq +14 -> 459
    //   448: aload_3
    //   449: astore 4
    //   451: getstatic 395	com/android/server/am/OnePlusProcessManager:mOnePlusProcessManager	Lcom/android/server/am/OnePlusProcessManager;
    //   454: iload_0
    //   455: iload_1
    //   456: invokevirtual 2288	com/android/server/am/OnePlusProcessManager:writePidToTasksFileDelayed	(II)V
    //   459: aload_3
    //   460: ifnull +7 -> 467
    //   463: aload_3
    //   464: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   467: return
    //   468: astore 5
    //   470: aload 4
    //   472: monitorexit
    //   473: aload 5
    //   475: athrow
    //   476: astore 5
    //   478: aload_3
    //   479: astore 4
    //   481: aload 5
    //   483: astore_3
    //   484: aload 4
    //   486: ifnull +8 -> 494
    //   489: aload 4
    //   491: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   494: aload_3
    //   495: athrow
    //   496: astore_3
    //   497: ldc -70
    //   499: new 781	java/lang/StringBuilder
    //   502: dup
    //   503: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   506: ldc_w 2277
    //   509: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   512: iload_0
    //   513: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   516: ldc_w 2279
    //   519: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   522: iload_1
    //   523: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   526: ldc_w 2290
    //   529: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   532: aload_3
    //   533: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   536: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   539: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   542: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   545: pop
    //   546: return
    //   547: new 1965	java/io/FileOutputStream
    //   550: dup
    //   551: aload 4
    //   553: invokespecial 2106	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   556: astore 5
    //   558: aload 5
    //   560: astore 4
    //   562: aload 5
    //   564: astore_3
    //   565: aload 5
    //   567: new 781	java/lang/StringBuilder
    //   570: dup
    //   571: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   574: iload_1
    //   575: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   578: ldc_w 663
    //   581: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   587: invokevirtual 2123	java/lang/String:getBytes	()[B
    //   590: invokevirtual 2127	java/io/FileOutputStream:write	([B)V
    //   593: aload 5
    //   595: astore 4
    //   597: aload 5
    //   599: astore_3
    //   600: getstatic 552	com/android/server/am/OnePlusProcessManager:DEBUG	Z
    //   603: ifeq +62 -> 665
    //   606: aload 5
    //   608: astore 4
    //   610: aload 5
    //   612: astore_3
    //   613: ldc -70
    //   615: new 781	java/lang/StringBuilder
    //   618: dup
    //   619: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   622: ldc_w 2292
    //   625: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   628: iload_0
    //   629: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   632: ldc_w 2279
    //   635: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   638: iload_1
    //   639: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   642: ldc_w 2294
    //   645: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   648: iload_2
    //   649: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   652: ldc_w 2296
    //   655: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   658: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   661: invokestatic 1020	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   664: pop
    //   665: aload 5
    //   667: ifnull -200 -> 467
    //   670: aload 5
    //   672: invokevirtual 2010	java/io/FileOutputStream:close	()V
    //   675: goto -208 -> 467
    //   678: astore_3
    //   679: ldc -70
    //   681: new 781	java/lang/StringBuilder
    //   684: dup
    //   685: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   688: ldc_w 2277
    //   691: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   694: iload_0
    //   695: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   698: ldc_w 2279
    //   701: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   704: iload_1
    //   705: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   708: ldc_w 2290
    //   711: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   714: aload_3
    //   715: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   718: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   721: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   724: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   727: pop
    //   728: return
    //   729: astore_3
    //   730: ldc -70
    //   732: new 781	java/lang/StringBuilder
    //   735: dup
    //   736: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   739: ldc_w 2277
    //   742: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   745: iload_0
    //   746: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   749: ldc_w 2279
    //   752: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   755: iload_1
    //   756: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   759: ldc_w 2290
    //   762: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   765: aload_3
    //   766: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   769: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   772: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   775: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   778: pop
    //   779: return
    //   780: astore 4
    //   782: ldc -70
    //   784: new 781	java/lang/StringBuilder
    //   787: dup
    //   788: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   791: ldc_w 2277
    //   794: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   797: iload_0
    //   798: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   801: ldc_w 2279
    //   804: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   807: iload_1
    //   808: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   811: ldc_w 2290
    //   814: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   817: aload 4
    //   819: invokevirtual 2115	java/io/IOException:getMessage	()Ljava/lang/String;
    //   822: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   825: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   828: invokestatic 1536	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   831: pop
    //   832: goto -338 -> 494
    //   835: astore_3
    //   836: goto -352 -> 484
    //   839: astore_3
    //   840: aload 6
    //   842: astore 4
    //   844: goto -360 -> 484
    //   847: astore 5
    //   849: aload_3
    //   850: astore 4
    //   852: aload 5
    //   854: astore_3
    //   855: goto -371 -> 484
    //   858: astore 5
    //   860: goto -495 -> 365
    //   863: astore 4
    //   865: aload 5
    //   867: astore_3
    //   868: aload 4
    //   870: astore 5
    //   872: goto -507 -> 365
    //   875: astore 5
    //   877: goto -512 -> 365
    //   880: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	881	0	paramInt1	int
    //   0	881	1	paramInt2	int
    //   0	881	2	paramBoolean	boolean
    //   26	469	3	localObject1	Object
    //   496	37	3	localIOException1	IOException
    //   564	49	3	localFileOutputStream1	java.io.FileOutputStream
    //   678	37	3	localIOException2	IOException
    //   729	37	3	localIOException3	IOException
    //   835	1	3	localObject2	Object
    //   839	11	3	localObject3	Object
    //   854	14	3	localObject4	Object
    //   22	587	4	localObject5	Object
    //   780	38	4	localIOException4	IOException
    //   842	9	4	localObject6	Object
    //   863	6	4	localIOException5	IOException
    //   18	308	5	localFile1	File
    //   355	6	5	localObject7	Object
    //   363	67	5	localIOException6	IOException
    //   468	6	5	localObject8	Object
    //   476	6	5	localObject9	Object
    //   556	115	5	localFileOutputStream2	java.io.FileOutputStream
    //   847	6	5	localObject10	Object
    //   858	8	5	localIOException7	IOException
    //   870	1	5	localObject11	Object
    //   875	1	5	localIOException8	IOException
    //   9	832	6	localObject12	Object
    //   12	93	7	localFile2	File
    //   15	44	8	localObject13	Object
    //   52	93	9	str	String
    // Exception table:
    //   from	to	target	type
    //   158	185	355	finally
    //   150	158	363	java/io/IOException
    //   185	206	363	java/io/IOException
    //   233	274	363	java/io/IOException
    //   357	363	363	java/io/IOException
    //   470	476	363	java/io/IOException
    //   206	233	468	finally
    //   150	158	476	finally
    //   185	206	476	finally
    //   233	274	476	finally
    //   357	363	476	finally
    //   470	476	476	finally
    //   350	354	496	java/io/IOException
    //   670	675	678	java/io/IOException
    //   463	467	729	java/io/IOException
    //   489	494	780	java/io/IOException
    //   27	54	835	finally
    //   61	72	835	finally
    //   368	444	835	finally
    //   451	459	835	finally
    //   565	593	835	finally
    //   600	606	835	finally
    //   613	665	835	finally
    //   72	98	839	finally
    //   98	139	839	finally
    //   140	150	839	finally
    //   274	308	847	finally
    //   308	346	847	finally
    //   547	558	847	finally
    //   27	54	858	java/io/IOException
    //   61	72	858	java/io/IOException
    //   565	593	858	java/io/IOException
    //   600	606	858	java/io/IOException
    //   613	665	858	java/io/IOException
    //   72	98	863	java/io/IOException
    //   98	139	863	java/io/IOException
    //   140	150	863	java/io/IOException
    //   274	308	875	java/io/IOException
    //   308	346	875	java/io/IOException
    //   547	558	875	java/io/IOException
  }
  
  boolean checkTrafficUid(int paramInt)
  {
    long l2 = SystemClock.elapsedRealtime();
    Traffic localTraffic = (Traffic)mUidTraffic.get(Integer.valueOf(paramInt));
    if (localTraffic == null) {
      return false;
    }
    long l3 = TrafficStats.getUidTxBytes(paramInt);
    long l4 = TrafficStats.getUidRxBytes(paramInt);
    long l1 = 1L;
    if (suspendUidDelayTime > 0L) {
      l1 = (l2 - localTraffic.startTime) / computeTrafficTime;
    }
    l2 = l1;
    if (l1 <= 1L) {
      l2 = 1L;
    }
    if (102400L < (l4 - localTraffic.rxBytes) / l2) {
      return true;
    }
    return 102400L < (l3 - localTraffic.txBytes) / l2;
  }
  
  public void computeThreeAppState()
  {
    synchronized (mActivityManager)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      ArrayList localArrayList = (ArrayList)mActivityManager.mLruProcesses.clone();
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (localArrayList == null) {
        return;
      }
    }
    int i = 0;
    while (i < ((ArrayList)localObject2).size())
    {
      ??? = (ProcessRecord)((ArrayList)localObject2).get(i);
      if ((??? != null) && (UserHandle.isApp(((ProcessRecord)???).uid)) && (((ProcessRecord)???).info != null) && ((((ProcessRecord)???).info.flags & 0x81) == 0))
      {
        add3rdAppProcessState(((ProcessRecord)???).uid, ((ProcessRecord)???).pid);
        mUidPackageNames.remove(Integer.valueOf(((ProcessRecord)???).uid));
        mUidPackageNames.put(Integer.valueOf(((ProcessRecord)???).uid), ((ProcessRecord)???).info.packageName);
      }
      i += 1;
    }
  }
  
  /* Error */
  public void dump(PrintWriter paramPrintWriter)
  {
    // Byte code:
    //   0: aload_1
    //   1: new 781	java/lang/StringBuilder
    //   4: dup
    //   5: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   8: ldc_w 2312
    //   11: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   14: getstatic 431	com/android/server/am/OnePlusProcessManager:mBPMStatus	Z
    //   17: invokevirtual 791	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   20: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   23: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   26: aload_0
    //   27: ldc_w 2314
    //   30: aload_1
    //   31: aload_0
    //   32: getfield 674	com/android/server/am/OnePlusProcessManager:mBPMList	Ljava/util/List;
    //   35: invokespecial 2316	com/android/server/am/OnePlusProcessManager:printList	(Ljava/lang/String;Ljava/io/PrintWriter;Ljava/util/List;)V
    //   38: aload_0
    //   39: ldc_w 2318
    //   42: aload_1
    //   43: aload_0
    //   44: getfield 676	com/android/server/am/OnePlusProcessManager:mPkgList	Ljava/util/List;
    //   47: invokespecial 2316	com/android/server/am/OnePlusProcessManager:printList	(Ljava/lang/String;Ljava/io/PrintWriter;Ljava/util/List;)V
    //   50: aload_0
    //   51: ldc_w 2320
    //   54: aload_1
    //   55: aload_0
    //   56: getfield 379	com/android/server/am/OnePlusProcessManager:mBrdList	Ljava/util/List;
    //   59: invokespecial 2316	com/android/server/am/OnePlusProcessManager:printList	(Ljava/lang/String;Ljava/io/PrintWriter;Ljava/util/List;)V
    //   62: aload_1
    //   63: ldc_w 2322
    //   66: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   69: aload_0
    //   70: getfield 712	com/android/server/am/OnePlusProcessManager:mRulesLock	Ljava/lang/Object;
    //   73: astore 5
    //   75: aload 5
    //   77: monitorenter
    //   78: iconst_0
    //   79: istore_2
    //   80: iload_2
    //   81: aload_0
    //   82: getfield 710	com/android/server/am/OnePlusProcessManager:mUidPidState	Landroid/util/SparseArray;
    //   85: invokevirtual 2239	android/util/SparseArray:size	()I
    //   88: if_icmpge +101 -> 189
    //   91: aload_1
    //   92: new 781	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   99: ldc_w 2324
    //   102: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: aload_0
    //   106: getfield 710	com/android/server/am/OnePlusProcessManager:mUidPidState	Landroid/util/SparseArray;
    //   109: iload_2
    //   110: invokevirtual 2242	android/util/SparseArray:keyAt	(I)I
    //   113: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   116: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   119: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   122: aload_0
    //   123: getfield 710	com/android/server/am/OnePlusProcessManager:mUidPidState	Landroid/util/SparseArray;
    //   126: iload_2
    //   127: invokevirtual 2325	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   130: checkcast 567	java/util/ArrayList
    //   133: astore 6
    //   135: iconst_0
    //   136: istore_3
    //   137: iload_3
    //   138: aload 6
    //   140: invokevirtual 1113	java/util/ArrayList:size	()I
    //   143: if_icmpge +39 -> 182
    //   146: aload_1
    //   147: new 781	java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   154: ldc_w 2327
    //   157: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload 6
    //   162: iload_3
    //   163: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   166: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   169: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   175: iload_3
    //   176: iconst_1
    //   177: iadd
    //   178: istore_3
    //   179: goto -42 -> 137
    //   182: iload_2
    //   183: iconst_1
    //   184: iadd
    //   185: istore_2
    //   186: goto -106 -> 80
    //   189: aload 5
    //   191: monitorexit
    //   192: aload_1
    //   193: ldc_w 2329
    //   196: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   199: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   202: astore 5
    //   204: aload 5
    //   206: monitorenter
    //   207: iconst_0
    //   208: istore_2
    //   209: iload_2
    //   210: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   213: invokevirtual 1113	java/util/ArrayList:size	()I
    //   216: if_icmpge +46 -> 262
    //   219: aload_1
    //   220: new 781	java/lang/StringBuilder
    //   223: dup
    //   224: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   227: ldc_w 2331
    //   230: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: getstatic 572	com/android/server/am/OnePlusProcessManager:mSuspendUids	Ljava/util/ArrayList;
    //   236: iload_2
    //   237: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   240: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   243: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   249: iload_2
    //   250: iconst_1
    //   251: iadd
    //   252: istore_2
    //   253: goto -44 -> 209
    //   256: astore_1
    //   257: aload 5
    //   259: monitorexit
    //   260: aload_1
    //   261: athrow
    //   262: aload 5
    //   264: monitorexit
    //   265: aload_1
    //   266: ldc_w 2333
    //   269: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   272: iconst_0
    //   273: istore_2
    //   274: iload_2
    //   275: aload_0
    //   276: getfield 703	com/android/server/am/OnePlusProcessManager:mGpsReceiverLocationUids	Ljava/util/ArrayList;
    //   279: invokevirtual 1113	java/util/ArrayList:size	()I
    //   282: if_icmpge +47 -> 329
    //   285: aload_1
    //   286: new 781	java/lang/StringBuilder
    //   289: dup
    //   290: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   293: ldc_w 2335
    //   296: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   299: aload_0
    //   300: getfield 703	com/android/server/am/OnePlusProcessManager:mGpsReceiverLocationUids	Ljava/util/ArrayList;
    //   303: iload_2
    //   304: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   307: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   310: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   313: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   316: iload_2
    //   317: iconst_1
    //   318: iadd
    //   319: istore_2
    //   320: goto -46 -> 274
    //   323: astore_1
    //   324: aload 5
    //   326: monitorexit
    //   327: aload_1
    //   328: athrow
    //   329: aload_1
    //   330: ldc_w 2337
    //   333: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   336: iconst_0
    //   337: istore_2
    //   338: iload_2
    //   339: aload_0
    //   340: getfield 701	com/android/server/am/OnePlusProcessManager:mOtherReceiverLocationUids	Ljava/util/ArrayList;
    //   343: invokevirtual 1113	java/util/ArrayList:size	()I
    //   346: if_icmpge +41 -> 387
    //   349: aload_1
    //   350: new 781	java/lang/StringBuilder
    //   353: dup
    //   354: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   357: ldc_w 2339
    //   360: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: aload_0
    //   364: getfield 701	com/android/server/am/OnePlusProcessManager:mOtherReceiverLocationUids	Ljava/util/ArrayList;
    //   367: iload_2
    //   368: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   371: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   374: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   377: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   380: iload_2
    //   381: iconst_1
    //   382: iadd
    //   383: istore_2
    //   384: goto -46 -> 338
    //   387: aload_1
    //   388: ldc_w 2341
    //   391: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   394: iconst_0
    //   395: istore_2
    //   396: iload_2
    //   397: aload_0
    //   398: getfield 705	com/android/server/am/OnePlusProcessManager:mStatusLocationUids	Ljava/util/ArrayList;
    //   401: invokevirtual 1113	java/util/ArrayList:size	()I
    //   404: if_icmpge +41 -> 445
    //   407: aload_1
    //   408: new 781	java/lang/StringBuilder
    //   411: dup
    //   412: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   415: ldc_w 2343
    //   418: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   421: aload_0
    //   422: getfield 705	com/android/server/am/OnePlusProcessManager:mStatusLocationUids	Ljava/util/ArrayList;
    //   425: iload_2
    //   426: invokevirtual 1278	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   429: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   432: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   435: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   438: iload_2
    //   439: iconst_1
    //   440: iadd
    //   441: istore_2
    //   442: goto -46 -> 396
    //   445: aload_1
    //   446: ldc_w 2345
    //   449: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   452: getstatic 586	com/android/server/am/OnePlusProcessManager:mLock	Ljava/lang/Object;
    //   455: astore 5
    //   457: aload 5
    //   459: monitorenter
    //   460: aload_0
    //   461: getfield 699	com/android/server/am/OnePlusProcessManager:mFrontWindowTouchUids	Ljava/util/HashSet;
    //   464: invokevirtual 1663	java/util/HashSet:iterator	()Ljava/util/Iterator;
    //   467: astore 6
    //   469: aload 6
    //   471: invokeinterface 1253 1 0
    //   476: ifeq +49 -> 525
    //   479: aload 6
    //   481: invokeinterface 1257 1 0
    //   486: checkcast 1001	java/lang/Integer
    //   489: astore 7
    //   491: aload_1
    //   492: new 781	java/lang/StringBuilder
    //   495: dup
    //   496: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   499: ldc_w 2347
    //   502: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   505: aload 7
    //   507: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   510: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   513: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   516: goto -47 -> 469
    //   519: astore_1
    //   520: aload 5
    //   522: monitorexit
    //   523: aload_1
    //   524: athrow
    //   525: aload 5
    //   527: monitorexit
    //   528: aload_1
    //   529: ldc_w 2349
    //   532: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   535: iconst_0
    //   536: istore_2
    //   537: iload_2
    //   538: getstatic 647	com/android/server/am/OnePlusProcessManager:mUnFrozenReasonUids	Landroid/util/ArrayMap;
    //   541: invokevirtual 1585	android/util/ArrayMap:size	()I
    //   544: if_icmpge +59 -> 603
    //   547: aload_1
    //   548: new 781	java/lang/StringBuilder
    //   551: dup
    //   552: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   555: ldc_w 2351
    //   558: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   561: getstatic 647	com/android/server/am/OnePlusProcessManager:mUnFrozenReasonUids	Landroid/util/ArrayMap;
    //   564: iload_2
    //   565: invokevirtual 2353	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   568: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   571: ldc_w 2355
    //   574: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   577: getstatic 647	com/android/server/am/OnePlusProcessManager:mUnFrozenReasonUids	Landroid/util/ArrayMap;
    //   580: iload_2
    //   581: invokevirtual 1588	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   584: checkcast 1088	java/lang/String
    //   587: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   590: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   593: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   596: iload_2
    //   597: iconst_1
    //   598: iadd
    //   599: istore_2
    //   600: goto -63 -> 537
    //   603: aload_1
    //   604: ldc_w 2357
    //   607: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   610: aload_0
    //   611: getfield 716	com/android/server/am/OnePlusProcessManager:mResumeUidBroadcasts	Landroid/util/SparseArray;
    //   614: ifnull +163 -> 777
    //   617: iconst_0
    //   618: istore_2
    //   619: iload_2
    //   620: aload_0
    //   621: getfield 716	com/android/server/am/OnePlusProcessManager:mResumeUidBroadcasts	Landroid/util/SparseArray;
    //   624: invokevirtual 2239	android/util/SparseArray:size	()I
    //   627: if_icmpge +150 -> 777
    //   630: aload_0
    //   631: getfield 716	com/android/server/am/OnePlusProcessManager:mResumeUidBroadcasts	Landroid/util/SparseArray;
    //   634: iload_2
    //   635: invokevirtual 2325	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   638: checkcast 576	android/util/ArrayMap
    //   641: astore 5
    //   643: aload_0
    //   644: getfield 716	com/android/server/am/OnePlusProcessManager:mResumeUidBroadcasts	Landroid/util/SparseArray;
    //   647: iload_2
    //   648: invokevirtual 2242	android/util/SparseArray:keyAt	(I)I
    //   651: istore 4
    //   653: aload_1
    //   654: new 781	java/lang/StringBuilder
    //   657: dup
    //   658: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   661: ldc_w 2359
    //   664: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   667: iload 4
    //   669: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   672: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   675: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   678: aload 5
    //   680: ifnull +90 -> 770
    //   683: iconst_0
    //   684: istore_3
    //   685: iload_3
    //   686: aload 5
    //   688: invokevirtual 1585	android/util/ArrayMap:size	()I
    //   691: if_icmpge +79 -> 770
    //   694: aload 5
    //   696: iload_3
    //   697: invokevirtual 2353	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   700: checkcast 1088	java/lang/String
    //   703: astore 6
    //   705: aload 5
    //   707: iload_3
    //   708: invokevirtual 1588	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   711: checkcast 1001	java/lang/Integer
    //   714: astore 7
    //   716: aload_1
    //   717: new 781	java/lang/StringBuilder
    //   720: dup
    //   721: invokespecial 782	java/lang/StringBuilder:<init>	()V
    //   724: ldc_w 2359
    //   727: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   730: iload 4
    //   732: invokevirtual 1106	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   735: ldc_w 2361
    //   738: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   741: aload 6
    //   743: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   746: ldc_w 2363
    //   749: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   752: aload 7
    //   754: invokevirtual 1232	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   757: invokevirtual 794	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   760: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   763: iload_3
    //   764: iconst_1
    //   765: iadd
    //   766: istore_3
    //   767: goto -82 -> 685
    //   770: iload_2
    //   771: iconst_1
    //   772: iadd
    //   773: istore_2
    //   774: goto -155 -> 619
    //   777: aload_1
    //   778: ldc_w 2365
    //   781: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   784: aload_0
    //   785: getfield 409	com/android/server/am/OnePlusProcessManager:mSuspendProcessHandler	Landroid/os/Handler;
    //   788: ifnull +24 -> 812
    //   791: aload_0
    //   792: getfield 409	com/android/server/am/OnePlusProcessManager:mSuspendProcessHandler	Landroid/os/Handler;
    //   795: invokevirtual 2366	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   798: new 2368	android/util/PrintWriterPrinter
    //   801: dup
    //   802: aload_1
    //   803: invokespecial 2370	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   806: ldc_w 2372
    //   809: invokevirtual 2377	android/os/Looper:dump	(Landroid/util/Printer;Ljava/lang/String;)V
    //   812: aload_1
    //   813: ldc_w 2379
    //   816: invokevirtual 1790	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   819: aload_0
    //   820: getfield 682	com/android/server/am/OnePlusProcessManager:mResumeProcessHandler	Landroid/os/Handler;
    //   823: ifnull +24 -> 847
    //   826: aload_0
    //   827: getfield 682	com/android/server/am/OnePlusProcessManager:mResumeProcessHandler	Landroid/os/Handler;
    //   830: invokevirtual 2366	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   833: new 2368	android/util/PrintWriterPrinter
    //   836: dup
    //   837: aload_1
    //   838: invokespecial 2370	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   841: ldc_w 2372
    //   844: invokevirtual 2377	android/os/Looper:dump	(Landroid/util/Printer;Ljava/lang/String;)V
    //   847: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	848	0	this	OnePlusProcessManager
    //   0	848	1	paramPrintWriter	PrintWriter
    //   79	695	2	i	int
    //   136	631	3	j	int
    //   651	80	4	k	int
    //   133	609	6	localObject2	Object
    //   489	264	7	localInteger	Integer
    // Exception table:
    //   from	to	target	type
    //   80	135	256	finally
    //   137	175	256	finally
    //   209	249	323	finally
    //   460	469	519	finally
    //   469	516	519	finally
  }
  
  public boolean getBPMEnable()
  {
    if (DEBUG_DETAIL) {
      Slog.d("OnePlusProcessManager", "getBPMEnable():mBPMStatus = " + mBPMStatus);
    }
    return mBPMStatus;
  }
  
  void loadNPMConfigFiles()
  {
    this.mBrdList = loadStateLocked("/data/data_bpm/brd.xml");
    this.mPkgList = loadStateLocked("/data/data_bpm/pkg.xml");
    this.mBlackGpsList = loadStateLocked("/data/data_bpm/black_gps.xml");
    this.mBlackBrdList = loadStateLocked("/data/data_bpm/black_brd.xml");
    this.mBlackAlarmList = loadStateLocked("/data/data_bpm/black_alarm.xml");
    Slog.d("OnePlusProcessManager", "loadNPMConfigFiles: mPkgList" + this.mPkgList);
    Slog.d("OnePlusProcessManager", "loadNPMConfigFiles: mBrdList" + this.mBrdList);
    Slog.d("OnePlusProcessManager", "loadNPMConfigFiles mBlackBrdList= " + this.mBlackBrdList);
    Slog.d("OnePlusProcessManager", "loadNPMConfigFiles mBlackGpsList= " + this.mBlackGpsList);
    Slog.d("OnePlusProcessManager", "loadNPMConfigFiles mBlackAlarmList= " + this.mBlackAlarmList);
    List localList = loadStateLocked("/data/data_bpm/bpm_sts.xml");
    boolean bool;
    if ((localList != null) && (localList.size() == 1))
    {
      if (!((String)localList.get(0)).equals("true")) {
        break label322;
      }
      bool = true;
      Slog.d("OnePlusProcessManager", "[FO]UPDATE_STS: before: temp=" + bool);
      prepareConfigStatus();
      if ((mGlobalFlags & 0x1) != 0)
      {
        if (this.mForceSwitch != 1) {
          break label327;
        }
        setBPMEnable(true);
        saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", true);
      }
    }
    for (;;)
    {
      updateProperties();
      Slog.d("OnePlusProcessManager", "[FO]UPDATE_STS: before: mBPMStatus=" + mBPMStatus);
      return;
      label322:
      bool = false;
      break;
      label327:
      if (this.mForceSwitch == 2)
      {
        setBPMEnable(false);
        saveBpmStsLocked("/data/data_bpm/bpm_sts.xml", false);
      }
      else
      {
        setBPMEnable(bool);
      }
    }
  }
  
  public void openDebug(boolean paramBoolean)
  {
    DEBUG = paramBoolean;
    DEBUG_DETAIL = paramBoolean;
  }
  
  void prepareBPMConfigFiles()
  {
    Slog.d("OnePlusProcessManager", "[FO]prepareBPMConfigFiles()");
    try
    {
      File localFile = new File("/data/data_bpm/");
      if (!localFile.exists()) {
        localFile.mkdirs();
      }
      boolean bool = false;
      if (checkVersion()) {
        bool = true;
      }
      Slog.d("OnePlusProcessManager", "[FO]prepareBPMConfigFiles() isForceUpdate =" + bool);
      copyFile("/system/bpm/pkg.xml", "/data/data_bpm/pkg.xml", bool);
      copyFile("/system/bpm/brd.xml", "/data/data_bpm/brd.xml", bool);
      copyFile("/system/bpm/bpm_sts.xml", "/data/data_bpm/bpm_sts.xml", false);
      copyFile("/system/bpm/black_gps.xml", "/data/data_bpm/black_gps.xml", bool);
      copyFile("/system/bpm/black_brd.xml", "/data/data_bpm/black_brd.xml", bool);
      copyFile("/system/bpm/black_alarm.xml", "/data/data_bpm/black_alarm.xml", bool);
      copyFile("/system/bpm/version.xml", "/data/data_bpm/version.xml", bool);
      return;
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/");
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/pkg.xml");
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/brd.xml");
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/bpm_sts.xml");
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/black_gps.xml");
          Runtime.getRuntime().exec("chmod 0770 /data/data_bpm/black_brd.xml");
          return;
        }
        catch (Exception localException2)
        {
          Slog.w("OnePlusProcessManager", "[FO]prepareBPMConfigFiles(): failed " + localException2);
          localException2.printStackTrace();
        }
        localException1 = localException1;
        Slog.w("OnePlusProcessManager", "[FO]initBPMConfigFiles(): failed " + localException1);
      }
    }
  }
  
  void prepareConfigStatus()
  {
    myLog("prepareConfigStatus()");
    Object localObject = loadXmlLocked("/data/data_bpm/cfg.xml");
    HashMap localHashMap = loadXmlLocked("/system/bpm/cfg.xml");
    int j = 0;
    int i = 0;
    if (localHashMap == null) {
      return;
    }
    String str = (String)localHashMap.get("version");
    if (str != null) {
      j = Integer.valueOf(str).intValue();
    }
    if (localObject == null)
    {
      i = 0;
      CFG_VERSOON = i;
      if (j > i)
      {
        CFG_VERSOON = j;
        localObject = (String)localHashMap.get("forceSwitch");
        if (localObject != null)
        {
          if (!"on".equals(localObject)) {
            break label145;
          }
          this.mForceSwitch = 1;
        }
      }
    }
    for (;;)
    {
      saveXmlLocked("/data/data_bpm/cfg.xml", localHashMap);
      return;
      localObject = (String)((HashMap)localObject).get("version");
      if (localObject == null) {
        break;
      }
      i = Integer.valueOf((String)localObject).intValue();
      break;
      label145:
      if ("off".equals(localObject)) {
        this.mForceSwitch = 2;
      }
    }
  }
  
  void removeUidStateLocked(int paramInt)
  {
    paramInt = this.mUidState.indexOfKey(paramInt);
    if (paramInt >= 0)
    {
      this.mUidState.valueAt(paramInt);
      this.mUidState.removeAt(paramInt);
    }
  }
  
  public void resumeAllProcessLock(String paramString)
  {
    Object localObject = mLock;
    int i = 0;
    try
    {
      while (i < mSuspendUids.size())
      {
        mOnePlusProcessManager.scheduleResumeUid(((Integer)mSuspendUids.get(i)).intValue(), paramString, 2);
        i += 1;
      }
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void setBPMEnable(boolean paramBoolean)
  {
    if (2 == this.mForceSwitch) {
      paramBoolean = false;
    }
    if (paramBoolean) {
      SystemProperties.set("sys.cgroup.active", "1");
    }
    for (;;)
    {
      if (paramBoolean != mBPMStatus)
      {
        mBPMStatusing = paramBoolean;
        updateBPMEnableState(paramBoolean);
        updateDozePolicyToDB(paramBoolean);
        mBPMStatus = paramBoolean;
      }
      return;
      SystemProperties.set("sys.cgroup.active", "0");
    }
  }
  
  public void setBPMEnableFromDB(boolean paramBoolean)
  {
    if (2 == this.mForceSwitch) {
      paramBoolean = false;
    }
    if ((mGlobalFlags & 0x1) == 0)
    {
      mGlobalFlags |= 0x1;
      SystemProperties.set("persist.sys.cgroup.flags", mGlobalFlags + "");
    }
    if (paramBoolean) {
      SystemProperties.set("sys.cgroup.active", "1");
    }
    for (;;)
    {
      if (paramBoolean != mBPMStatus)
      {
        mBPMStatusing = paramBoolean;
        updateBPMEnableState(paramBoolean);
        mBPMStatus = paramBoolean;
      }
      return;
      SystemProperties.set("sys.cgroup.active", "0");
    }
  }
  
  public boolean setCGroupState(int paramInt, boolean paramBoolean)
  {
    return setCGroupStateLocked(paramInt, paramBoolean);
  }
  
  void updatePowerSaveWhitelistLocked()
  {
    try
    {
      int[] arrayOfInt = new int[0];
      if (mDeviceIdleService != null) {
        arrayOfInt = mDeviceIdleService.getAppIdWhitelist();
      }
      synchronized (mWhiteLock)
      {
        handlePackageChange(changeIntToList(mWhiteUids), changeIntToList(arrayOfInt));
        mWhiteUids = arrayOfInt;
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      mWhiteUids = null;
    }
  }
  
  void updateProperties() {}
  
  void updateUidStateLocked(int paramInt1, int paramInt2)
  {
    if (this.mUidState.get(paramInt1, 16) != paramInt2) {
      this.mUidState.put(paramInt1, paramInt2);
    }
  }
  
  void writePidToTasksFileDelayed(int paramInt1, int paramInt2)
  {
    Message localMessage = Message.obtain();
    localMessage.what = 114;
    localMessage.arg1 = paramInt1;
    localMessage.arg2 = paramInt2;
    this.mOneplusProcessHandler.sendMessageDelayed(localMessage, 2000L);
  }
  
  private class FileObserverPolicy
    extends FileObserver
  {
    String mDirPath = null;
    
    public FileObserverPolicy(String paramString)
    {
      super(4095);
      this.mDirPath = paramString;
    }
    
    public void onEvent(int paramInt, String paramString)
    {
      int j = 1;
      if (OnePlusProcessManager.DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "[FO]:FileObserverPolicy.onEvent(): path=" + paramString + ", event=" + Integer.toHexString(paramInt));
      }
      if (paramString == null) {
        return;
      }
      File localFile = new File(this.mDirPath, paramString);
      if (!localFile.exists()) {
        Slog.d("OnePlusProcessManager", "[FO]:FileObserverPolicy.onEvent(): not exists -> " + localFile.getAbsolutePath());
      }
      int i;
      if (paramInt == 2)
      {
        i = 1;
        if (paramInt != 8) {
          break label167;
        }
        paramInt = j;
        label118:
        if ((paramInt | i) != 0)
        {
          if (!paramString.equals("bpm_sts.xml")) {
            break label172;
          }
          OnePlusProcessManager.-get16(OnePlusProcessManager.this).removeMessages(100);
          OnePlusProcessManager.-get16(OnePlusProcessManager.this).sendEmptyMessageDelayed(100, 2000L);
        }
      }
      label167:
      label172:
      do
      {
        return;
        i = 0;
        break;
        paramInt = 0;
        break label118;
        if (paramString.equals("pkg.xml"))
        {
          OnePlusProcessManager.-get16(OnePlusProcessManager.this).removeMessages(103);
          OnePlusProcessManager.-get16(OnePlusProcessManager.this).sendEmptyMessageDelayed(103, 2000L);
          return;
        }
      } while (!paramString.equals("brd.xml"));
      OnePlusProcessManager.-get16(OnePlusProcessManager.this).removeMessages(102);
      OnePlusProcessManager.-get16(OnePlusProcessManager.this).sendEmptyMessageDelayed(102, 2000L);
    }
  }
  
  class ProcessFreezerConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    ProcessFreezerConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      OnePlusProcessManager.-wrap11(OnePlusProcessManager.this, paramJSONArray);
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      OnePlusProcessManager.-wrap9(OnePlusProcessManager.this);
    }
  }
  
  static class SuspendMsgObj
  {
    ProcessRecord app;
    long rxBytes;
    long txBytes;
    
    public SuspendMsgObj(ProcessRecord paramProcessRecord, long paramLong1, long paramLong2)
    {
      this.app = paramProcessRecord;
      this.txBytes = paramLong1;
      this.rxBytes = paramLong2;
    }
  }
  
  class Traffic
  {
    long rxBytes;
    long startTime;
    long txBytes;
    
    public Traffic(long paramLong1, long paramLong2, long paramLong3)
    {
      this.startTime = paramLong1;
      this.txBytes = paramLong2;
      this.rxBytes = paramLong3;
    }
  }
  
  private class oneplusProcessHandler
    extends Handler
  {
    public oneplusProcessHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (OnePlusProcessManager.DEBUG_DETAIL) {
        Slog.d("OnePlusProcessManager", "[FO]handleMessage(): msg=" + paramMessage);
      }
      switch (paramMessage.what)
      {
      case 101: 
      case 103: 
      case 104: 
      case 105: 
      case 106: 
      case 107: 
      case 108: 
      case 109: 
      case 110: 
      case 113: 
      case 117: 
      default: 
      case 111: 
      case 112: 
      case 100: 
      case 102: 
      case 114: 
      case 115: 
      case 116: 
      case 118: 
        label380:
        label386:
        do
        {
          int j;
          do
          {
            do
            {
              do
              {
                return;
                OnePlusProcessManager.-set2(OnePlusProcessManager.this, new OnePlusProcessManager.FileObserverPolicy(OnePlusProcessManager.this, "/data/data_bpm/"));
                OnePlusProcessManager.-get13(OnePlusProcessManager.this).startWatching();
                return;
                OnePlusProcessManager.this.loadNPMConfigFiles();
                try
                {
                  OnePlusProcessManager.this.mAlarm.setBlackAlarm(OnePlusProcessManager.-get10(OnePlusProcessManager.this));
                  return;
                }
                catch (RemoteException paramMessage)
                {
                  return;
                }
                boolean bool2 = OnePlusProcessManager.-get8();
                paramMessage = OnePlusProcessManager.loadStateLocked("/data/data_bpm/bpm_sts.xml");
                boolean bool1;
                HashMap localHashMap;
                if ((paramMessage != null) && (paramMessage.size() == 1))
                {
                  if (OnePlusProcessManager.DEBUG_DETAIL) {
                    Slog.d("OnePlusProcessManager", "[FO]UPDATE_STS: before: mBPMStatus=" + OnePlusProcessManager.-get8());
                  }
                  if (!((String)paramMessage.get(0)).equals("true")) {
                    break label380;
                  }
                  bool1 = true;
                  if (bool2 != bool1)
                  {
                    localHashMap = new HashMap();
                    if (!bool1) {
                      break label386;
                    }
                  }
                }
                for (paramMessage = "true";; paramMessage = "false")
                {
                  localHashMap.put("BPMStatus", paramMessage);
                  if (OnePlusProcessManager.this.appTracker != null) {
                    OnePlusProcessManager.this.appTracker.onEvent("OnePlusProcessManager", localHashMap);
                  }
                  if ((OnePlusProcessManager.-get14() & 0x1) != 0) {
                    OnePlusProcessManager.this.setBPMEnable(bool1);
                  }
                  Slog.d("OnePlusProcessManager", "[FO]UPDATE_STS: after: mBPMStatus=" + OnePlusProcessManager.-get8());
                  OnePlusProcessManager.this.updateProperties();
                  return;
                  bool1 = false;
                  break;
                }
                OnePlusProcessManager.-set0(OnePlusProcessManager.this, OnePlusProcessManager.loadStateLocked("/data/data_bpm/brd.xml"));
                Slog.d("OnePlusProcessManager", "[FO]UPDATE_STS: after: mBrdList=" + OnePlusProcessManager.-get11(OnePlusProcessManager.this));
                return;
                OnePlusProcessManager.-wrap19(paramMessage.arg1, paramMessage.arg2, false);
                return;
                i = paramMessage.arg1;
                j = paramMessage.arg2;
                OnePlusProcessManager.-wrap20(j, i);
              } while (!OnePlusProcessManager.-get8());
              OnePlusProcessManager.-wrap14(OnePlusProcessManager.-get15(), j, OnePlusProcessManager.suspendUidDelayTime * 2L);
              return;
              i = paramMessage.arg1;
            } while (OnePlusProcessManager.this.mFrontActivityUids.contains(Integer.valueOf(i)));
            j = OnePlusProcessManager.-wrap4(OnePlusProcessManager.this, i);
          } while (j < 1);
          paramMessage = Message.obtain();
          paramMessage.what = 116;
          paramMessage.arg1 = i;
          OnePlusProcessManager.-get16(OnePlusProcessManager.this).sendMessageDelayed(paramMessage, OnePlusProcessManager.computeTrafficTime * j);
          return;
        } while (OnePlusProcessManager.-get15() == null);
        OnePlusProcessManager.-get15().resumeAllProcessLock("updateScreenState");
        return;
      }
      int i = paramMessage.arg1;
      OnePlusProcessManager.-wrap18(OnePlusProcessManager.this, i);
    }
  }
  
  private class resumeProcessHandler
    extends Handler
  {
    public resumeProcessHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message arg1)
    {
      int i = ???.arg1;
      int j = ???.arg2;
      String str = (String)???.obj;
      if (j < 10000) {
        return;
      }
      if (OnePlusProcessManager.DEBUG) {
        Log.e("OnePlusProcessManager", "resumeProcessHandler handleMessage uid =" + j);
      }
      if (!OnePlusProcessManager.isUidSuspended(j))
      {
        OnePlusProcessManager.-wrap14(OnePlusProcessManager.this, j, OnePlusProcessManager.suspendUidDelayTime);
        return;
      }
      OnePlusProcessManager.-get19(OnePlusProcessManager.this).removeMessages(j - 10000);
      OnePlusProcessManager.-wrap16(OnePlusProcessManager.this, j);
      if (OnePlusProcessManager.DEBUG) {
        Slog.d("OnePlusProcessManager", "resumeProcessHandler startResumeUid done ");
      }
      OnePlusProcessManager.-wrap12(OnePlusProcessManager.this, j);
      for (;;)
      {
        synchronized (OnePlusProcessManager.mUnFrozenReasonUids)
        {
          OnePlusProcessManager.mUnFrozenReasonUids.remove(Integer.valueOf(j));
          OnePlusProcessManager.mUnFrozenReasonUids.put(Integer.valueOf(j), "resumeProcessHandler =" + str);
          if (!OnePlusProcessManager.-get12())
          {
            if (i == 2) {
              OnePlusProcessManager.-wrap14(OnePlusProcessManager.this, j, 20000L);
            }
          }
          else
          {
            OnePlusProcessManager.mDoThawedUids.remove(Integer.valueOf(j));
            OnePlusProcessManager.mDoThawedUids.add(Integer.valueOf(j));
            if (OnePlusProcessManager.mPendingUid == j) {}
          }
        }
        synchronized (OnePlusProcessManager.mAdjustUids)
        {
          OnePlusProcessManager.mAdjustUids.remove(Integer.valueOf(j));
          if (OnePlusProcessManager.DEBUG) {
            Slog.d("OnePlusProcessManager", "resumeProcessHandler end ---------");
          }
          return;
          localObject1 = finally;
          throw ((Throwable)localObject1);
          OnePlusProcessManager.-wrap14(OnePlusProcessManager.this, j, OnePlusProcessManager.suspendUidDelayTime);
        }
      }
    }
  }
  
  private class suspendProcessHandler
    extends Handler
  {
    public suspendProcessHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message arg1)
    {
      int j = ???.arg1;
      if (!UserHandle.isApp(j)) {
        return;
      }
      if ((OnePlusProcessManager.-get8()) && (OnePlusProcessManager.-get9())) {}
      synchronized (OnePlusProcessManager.this.mRulesLock)
      {
        if (OnePlusProcessManager.this.mUidPidState.indexOfKey(Integer.valueOf(j).intValue()) < 0) {
          synchronized (OnePlusProcessManager.mUnFrozenReasonUids)
          {
            OnePlusProcessManager.mUnFrozenReasonUids.remove(Integer.valueOf(j));
            return;
            return;
          }
        }
      }
      if (OnePlusProcessManager.isUidSuspended(j)) {
        return;
      }
      synchronized (OnePlusProcessManager.mAdjustUids)
      {
        OnePlusProcessManager.mAdjustUids.remove(Integer.valueOf(j));
        if (OnePlusProcessManager.-get12())
        {
          OnePlusProcessManager.mDoThawedUids.remove(Integer.valueOf(j));
          OnePlusProcessManager.-get19(OnePlusProcessManager.this).removeMessages(j - 10000);
          if (OnePlusProcessManager.DEBUG) {
            Log.e("OnePlusProcessManager", "suspendProcessHandler mCharging  so skip uid =" + j);
          }
          return;
        }
      }
      if (OnePlusProcessManager.-get19(OnePlusProcessManager.this).hasMessages(j - 10000))
      {
        if (OnePlusProcessManager.DEBUG) {
          Log.e("OnePlusProcessManager", "suspendProcessHandler hasMessages alreay so skip uid =" + j);
        }
        return;
      }
      OnePlusProcessManager.mPendingUid = j;
      if (OnePlusProcessManager.DEBUG) {
        Log.e("OnePlusProcessManager", "suspendProcessHandler handleMessage uid =" + j);
      }
      ??? = null;
      int k = 0;
      double d = OnePlusProcessManager.-wrap3(OnePlusProcessManager.this, j);
      if (d == OnePlusProcessManager.-get4(OnePlusProcessManager.this)) {
        ??? = OnePlusProcessManager.-wrap6(OnePlusProcessManager.this, j);
      }
      boolean bool;
      if ((d == OnePlusProcessManager.-get4(OnePlusProcessManager.this)) && (??? != null)) {
        bool = OnePlusProcessManager.-wrap2(OnePlusProcessManager.this, j);
      }
      long l2;
      synchronized (OnePlusProcessManager.mUnFrozenReasonUids)
      {
        OnePlusProcessManager.mUnFrozenReasonUids.remove(Integer.valueOf(j));
        k = bool;
        if (!bool) {
          break label604;
        }
        int i = 0;
        k = bool;
        if (i >= ???.size()) {
          break label604;
        }
        OnePlusProcessManager.-wrap2(OnePlusProcessManager.this, ((Integer)???.get(i)).intValue());
        i += 1;
      }
      if (d == OnePlusProcessManager.-get2(OnePlusProcessManager.this))
      {
        if (OnePlusProcessManager.DEBUG_ONEPLUS) {
          Slog.d("OnePlusProcessManager", "suspendProcessHandler skip suspend: SUSPEND_FAIL_NOTRY");
        }
        synchronized (OnePlusProcessManager.mAdjustUids)
        {
          OnePlusProcessManager.mAdjustUids.remove(Integer.valueOf(j));
          OnePlusProcessManager.mDoThawedUids.remove(Integer.valueOf(j));
          OnePlusProcessManager.mPendingUid = 0;
          return;
        }
      }
      long l1;
      if (d == OnePlusProcessManager.-get1(OnePlusProcessManager.this))
      {
        l1 = l2 * 5L;
        OnePlusProcessManager.-wrap14(OnePlusProcessManager.this, j, l1);
      }
      synchronized (OnePlusProcessManager.mUnFrozenReasonUids)
      {
        OnePlusProcessManager.mUnFrozenReasonUids.remove(Integer.valueOf(j));
        OnePlusProcessManager.mUnFrozenReasonUids.put(Integer.valueOf(j), "suspendReturn =" + d);
        label604:
        if (k == 0) {}
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/OnePlusProcessManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */