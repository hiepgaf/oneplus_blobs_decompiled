package com.android.server;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.INetworkPolicyManager;
import android.net.INetworkPolicyManager.Stub;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IDeviceIdleController.Stub;
import android.os.IMaintenanceActivityListener;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.MutableLong;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.view.Display;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.AtomicFile;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import com.android.server.am.BatteryStatsService;
import com.android.server.lights.LightsManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class DeviceIdleController
  extends SystemService
  implements AnyMotionDetector.DeviceIdleCallback
{
  private static final boolean COMPRESS_TIME = false;
  private static final boolean DEBUG = true;
  private static final int EVENT_BUFFER_SIZE = 100;
  private static final int EVENT_DEEP_IDLE = 4;
  private static final int EVENT_DEEP_MAINTENANCE = 5;
  private static final int EVENT_LIGHT_IDLE = 2;
  private static final int EVENT_LIGHT_MAINTENANCE = 3;
  private static final int EVENT_NORMAL = 1;
  private static final int EVENT_NULL = 0;
  private static final int LIGHT_STATE_ACTIVE = 0;
  private static final int LIGHT_STATE_IDLE = 4;
  private static final int LIGHT_STATE_IDLE_MAINTENANCE = 6;
  private static final int LIGHT_STATE_INACTIVE = 1;
  private static final int LIGHT_STATE_OVERRIDE = 7;
  private static final int LIGHT_STATE_PRE_IDLE = 3;
  private static final int LIGHT_STATE_WAITING_FOR_NETWORK = 5;
  static final int MSG_FINISH_IDLE_OP = 8;
  static final int MSG_REPORT_ACTIVE = 5;
  static final int MSG_REPORT_IDLE_OFF = 4;
  static final int MSG_REPORT_IDLE_ON = 2;
  static final int MSG_REPORT_IDLE_ON_LIGHT = 3;
  static final int MSG_REPORT_MAINTENANCE_ACTIVITY = 7;
  static final int MSG_TEMP_APP_WHITELIST_TIMEOUT = 6;
  static final int MSG_WRITE_CONFIG = 1;
  private static final int STATE_ACTIVE = 0;
  private static final int STATE_IDLE = 5;
  private static final int STATE_IDLE_MAINTENANCE = 6;
  private static final int STATE_IDLE_PENDING = 2;
  private static final int STATE_INACTIVE = 1;
  private static final int STATE_LOCATING = 4;
  private static final int STATE_SENSING = 3;
  private static final String TAG = "DeviceIdleController";
  static boolean isDozeChangeSupport = false;
  static boolean mDozeChange = false;
  static LocationManagerService mLocationManagerService = null;
  static Object mLock;
  static boolean mStopGps = true;
  static ArrayList<String> mWhiteUids = new ArrayList();
  boolean isFirstReport = false;
  boolean isHasGpsRequest = false;
  private int mActiveIdleOpCount;
  private PowerManager.WakeLock mActiveIdleWakeLock;
  private AlarmManager mAlarmManager;
  private boolean mAlarmsActive;
  private AnyMotionDetector mAnyMotionDetector;
  private IBatteryStats mBatteryStats;
  BinderService mBinderService;
  private boolean mCharging;
  public final AtomicFile mConfigFile = new AtomicFile(new File(getSystemDir(), "deviceidle.xml"));
  private ConnectivityService mConnectivityService;
  private Constants mConstants;
  private Display mCurDisplay;
  private long mCurIdleBudget;
  private final AlarmManager.OnAlarmListener mDeepAlarmListener = new AlarmManager.OnAlarmListener()
  {
    public void onAlarm()
    {
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.stepIdleStateLocked("s:alarm");
        return;
      }
    }
  };
  private boolean mDeepEnabled;
  private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
  {
    public void onDisplayAdded(int paramAnonymousInt) {}
    
    public void onDisplayChanged(int paramAnonymousInt)
    {
      if (paramAnonymousInt == 0) {}
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.updateDisplayLocked();
        return;
      }
    }
    
    public void onDisplayRemoved(int paramAnonymousInt) {}
  };
  private DisplayManager mDisplayManager;
  private final int[] mEventCmds = new int[100];
  private final long[] mEventTimes = new long[100];
  private boolean mForceIdle;
  private final LocationListener mGenericLocationListener = new LocationListener()
  {
    public void onLocationChanged(Location paramAnonymousLocation)
    {
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.receivedGenericLocationLocked(paramAnonymousLocation);
        return;
      }
    }
    
    public void onProviderDisabled(String paramAnonymousString) {}
    
    public void onProviderEnabled(String paramAnonymousString) {}
    
    public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
  };
  private final LocationListener mGpsLocationListener = new LocationListener()
  {
    public void onLocationChanged(Location paramAnonymousLocation)
    {
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.receivedGpsLocationLocked(paramAnonymousLocation);
        return;
      }
    }
    
    public void onProviderDisabled(String paramAnonymousString) {}
    
    public void onProviderEnabled(String paramAnonymousString) {}
    
    public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
  };
  final MyHandler mHandler = new MyHandler(BackgroundThread.getHandler().getLooper());
  private boolean mHasGps;
  private boolean mHasNetworkLocation;
  private Intent mIdleIntent;
  private final BroadcastReceiver mIdleStartedDoneReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        DeviceIdleController.this.mHandler.sendEmptyMessageDelayed(8, DeviceIdleController.-get1(DeviceIdleController.this).MIN_DEEP_MAINTENANCE_TIME);
        return;
      }
      DeviceIdleController.this.mHandler.sendEmptyMessageDelayed(8, DeviceIdleController.-get1(DeviceIdleController.this).MIN_LIGHT_MAINTENANCE_TIME);
    }
  };
  private long mInactiveTimeout;
  private boolean mJobsActive;
  private Location mLastGenericLocation;
  private Location mLastGpsLocation;
  private final AlarmManager.OnAlarmListener mLightAlarmListener = new AlarmManager.OnAlarmListener()
  {
    public void onAlarm()
    {
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.stepLightIdleStateLocked("s:alarm");
        return;
      }
    }
  };
  private boolean mLightEnabled;
  private Intent mLightIdleIntent;
  private int mLightState;
  private LightsManager mLightsManager;
  private AlarmManagerService.LocalService mLocalAlarmManager;
  private PowerManagerInternal mLocalPowerManager;
  private boolean mLocated;
  private boolean mLocating;
  private LocationManager mLocationManager;
  private LocationRequest mLocationRequest;
  private final RemoteCallbackList<IMaintenanceActivityListener> mMaintenanceActivityListeners = new RemoteCallbackList();
  private long mMaintenanceStartTime;
  private final MotionListener mMotionListener = new MotionListener(null);
  private Sensor mMotionSensor;
  private boolean mNetworkConnected;
  private INetworkPolicyManager mNetworkPolicyManager;
  Runnable mNetworkPolicyTempWhitelistCallback;
  private long mNextAlarmTime;
  private long mNextIdleDelay;
  private long mNextIdlePendingDelay;
  private long mNextLightAlarmTime;
  private long mNextLightIdleDelay;
  private long mNextSensingTimeoutAlarmTime;
  private boolean mNotMoving;
  private PowerManager mPowerManager;
  private int[] mPowerSaveWhitelistAllAppIdArray = new int[0];
  private final SparseBooleanArray mPowerSaveWhitelistAllAppIds = new SparseBooleanArray();
  private final ArrayMap<String, Integer> mPowerSaveWhitelistApps = new ArrayMap();
  private final ArrayMap<String, Integer> mPowerSaveWhitelistAppsExceptIdle = new ArrayMap();
  private int[] mPowerSaveWhitelistExceptIdleAppIdArray = new int[0];
  private final SparseBooleanArray mPowerSaveWhitelistExceptIdleAppIds = new SparseBooleanArray();
  private final SparseBooleanArray mPowerSaveWhitelistSystemAppIds = new SparseBooleanArray();
  private final SparseBooleanArray mPowerSaveWhitelistSystemAppIdsExceptIdle = new SparseBooleanArray();
  private int[] mPowerSaveWhitelistUserAppIdArray = new int[0];
  private final SparseBooleanArray mPowerSaveWhitelistUserAppIds = new SparseBooleanArray();
  private final ArrayMap<String, Integer> mPowerSaveWhitelistUserApps = new ArrayMap();
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      boolean bool = false;
      ??? = paramAnonymousIntent.getAction();
      if (???.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
        DeviceIdleController.this.updateConnectivityState(paramAnonymousIntent);
      }
      for (;;)
      {
        return;
        if (???.equals("android.intent.action.BATTERY_CHANGED")) {}
        synchronized (DeviceIdleController.this)
        {
          int i = paramAnonymousIntent.getIntExtra("plugged", 0);
          paramAnonymousIntent = DeviceIdleController.this;
          if (i != 0) {
            bool = true;
          }
          paramAnonymousIntent.updateChargingLocked(bool);
          return;
          if ((!???.equals("android.intent.action.PACKAGE_REMOVED")) || (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))) {
            continue;
          }
          ??? = paramAnonymousIntent.getData();
          if (??? == null) {
            continue;
          }
          ??? = ???.getSchemeSpecificPart();
          if (??? == null) {
            continue;
          }
          DeviceIdleController.this.removePowerSaveWhitelistAppInternal(???);
          return;
        }
      }
    }
  };
  private boolean mReportedMaintenanceActivity;
  private boolean mScreenOn;
  private final AlarmManager.OnAlarmListener mSensingTimeoutAlarmListener = new AlarmManager.OnAlarmListener()
  {
    public void onAlarm()
    {
      if (DeviceIdleController.-get15(DeviceIdleController.this) == 3) {}
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.this.becomeInactiveIfAppropriateLocked();
        return;
      }
    }
  };
  private SensorManager mSensorManager;
  private int mState;
  private int[] mTempWhitelistAppIdArray = new int[0];
  private final SparseArray<Pair<MutableLong, String>> mTempWhitelistAppIdEndTimes = new SparseArray();
  
  static
  {
    mLock = new Object();
  }
  
  public DeviceIdleController(Context paramContext)
  {
    super(paramContext);
  }
  
  private void addEvent(int paramInt)
  {
    if (this.mEventCmds[0] != paramInt)
    {
      System.arraycopy(this.mEventCmds, 0, this.mEventCmds, 1, 99);
      System.arraycopy(this.mEventTimes, 0, this.mEventTimes, 1, 99);
      this.mEventCmds[0] = paramInt;
      this.mEventTimes[0] = SystemClock.elapsedRealtime();
    }
  }
  
  private static int[] buildAppIdArray(ArrayMap<String, Integer> paramArrayMap1, ArrayMap<String, Integer> paramArrayMap2, SparseBooleanArray paramSparseBooleanArray)
  {
    paramSparseBooleanArray.clear();
    if (paramArrayMap1 != null)
    {
      i = 0;
      while (i < paramArrayMap1.size())
      {
        paramSparseBooleanArray.put(((Integer)paramArrayMap1.valueAt(i)).intValue(), true);
        i += 1;
      }
    }
    if (paramArrayMap2 != null)
    {
      i = 0;
      while (i < paramArrayMap2.size())
      {
        paramSparseBooleanArray.put(((Integer)paramArrayMap2.valueAt(i)).intValue(), true);
        i += 1;
      }
    }
    int j = paramSparseBooleanArray.size();
    paramArrayMap1 = new int[j];
    int i = 0;
    while (i < j)
    {
      paramArrayMap1[i] = paramSparseBooleanArray.keyAt(i);
      i += 1;
    }
    return paramArrayMap1;
  }
  
  private void chearWhiteUid()
  {
    Object localObject1 = mLock;
    int i = 0;
    try
    {
      while (i < mWhiteUids.size())
      {
        removePowerSaveWhitelistAppInternal((String)mWhiteUids.get(i));
        i += 1;
      }
      mWhiteUids.clear();
      return;
    }
    finally {}
  }
  
  private boolean checkLoctionWhiteUid()
  {
    if (mLocationManagerService == null) {
      mLocationManagerService = (LocationManagerService)ServiceManager.getService("location");
    }
    ArrayMap localArrayMap = null;
    if (mLocationManagerService != null) {
      localArrayMap = mLocationManagerService.getActiveLocationUidType();
    }
    if ((localArrayMap == null) || (localArrayMap.size() == 0)) {
      return false;
    }
    PackageManager localPackageManager = getContext().getPackageManager();
    int i = 0;
    if (i < localArrayMap.size())
    {
      String str = (String)localArrayMap.valueAt(i);
      Object localObject1 = (Integer)localArrayMap.keyAt(i);
      if (localObject1 == null) {}
      while (((Integer)localObject1).intValue() < 10000)
      {
        i += 1;
        break;
      }
      localObject1 = localPackageManager.getPackagesForUid(((Integer)localObject1).intValue());
      int j = 0;
      while (j < localObject1.length)
      {
        if (localObject1[j] != null) {}
        try
        {
          if ((localObject1[j].equals("com.amap.android.ams")) && (str.equals("gps"))) {
            return true;
          }
          ??? = localPackageManager.getApplicationInfo(localObject1[j], 0);
          if ((??? != null) && ((((ApplicationInfo)???).flags & 0x1) == 0) && (str.equals("gps")))
          {
            if (isPowerSaveWhitelistExceptIdleAppInternal(localObject1[j])) {
              break label292;
            }
            synchronized (mLock)
            {
              Log.e("DeviceIdleController", "checkLoctionWhiteUid package = " + localObject1[j]);
              mWhiteUids.add(localObject1[j]);
              addPowerSaveWhitelistAppInternal(localObject1[j]);
              return true;
            }
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          j += 1;
        }
      }
    }
    return false;
    label292:
    return true;
  }
  
  static void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Device idle controller (deviceidle) commands:");
    paramPrintWriter.println("  help");
    paramPrintWriter.println("    Print this help text.");
    paramPrintWriter.println("  step [light|deep]");
    paramPrintWriter.println("    Immediately step to next state, without waiting for alarm.");
    paramPrintWriter.println("  force-idle [light|deep]");
    paramPrintWriter.println("    Force directly into idle mode, regardless of other device state.");
    paramPrintWriter.println("  force-inactive");
    paramPrintWriter.println("    Force to be inactive, ready to freely step idle states.");
    paramPrintWriter.println("  unforce");
    paramPrintWriter.println("    Resume normal functioning after force-idle or force-inactive.");
    paramPrintWriter.println("  get [light|deep|force|screen|charging|network]");
    paramPrintWriter.println("    Retrieve the current given state.");
    paramPrintWriter.println("  disable [light|deep|all]");
    paramPrintWriter.println("    Completely disable device idle mode.");
    paramPrintWriter.println("  enable [light|deep|all]");
    paramPrintWriter.println("    Re-enable device idle mode after it had previously been disabled.");
    paramPrintWriter.println("  enabled [light|deep|all]");
    paramPrintWriter.println("    Print 1 if device idle mode is currently enabled, else 0.");
    paramPrintWriter.println("  aggressive [true|false]");
    paramPrintWriter.println("    Activate aggressive doze (true) or deactivate it (false)");
    paramPrintWriter.println("  whitelist");
    paramPrintWriter.println("    Print currently whitelisted apps.");
    paramPrintWriter.println("  whitelist [package ...]");
    paramPrintWriter.println("    Add (prefix with +) or remove (prefix with -) packages.");
    paramPrintWriter.println("  tempwhitelist");
    paramPrintWriter.println("    Print packages that are temporarily whitelisted.");
    paramPrintWriter.println("  tempwhitelist [-u] [package ..]");
    paramPrintWriter.println("    Temporarily place packages in whitelist for 10 seconds.");
  }
  
  private ContentObserver getDozeModeEnabledObserver()
  {
    new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        DeviceIdleController.-wrap0(DeviceIdleController.this);
        if ((DeviceIdleController.-get5(DeviceIdleController.this)) || (DeviceIdleController.-get2(DeviceIdleController.this)))
        {
          DeviceIdleController.this.exitForceIdleLocked();
          DeviceIdleController.this.becomeInactiveIfAppropriateLocked();
          Slog.d("DeviceIdleController", "Idle mode enabled");
          return;
        }
        DeviceIdleController.this.becomeActiveLocked("disabled", Process.myUid());
        Slog.d("DeviceIdleController", "Idle mode disabled");
      }
    };
  }
  
  private static File getSystemDir()
  {
    return new File(Environment.getDataDirectory(), "system");
  }
  
  private boolean isHasGpsReport()
  {
    if (mLocationManagerService == null) {
      mLocationManagerService = (LocationManagerService)ServiceManager.getService("location");
    }
    ArrayMap localArrayMap = null;
    if (mLocationManagerService != null) {
      localArrayMap = mLocationManagerService.getActiveLocationUidType();
    }
    if ((localArrayMap == null) || (localArrayMap.size() == 0))
    {
      Slog.d("DeviceIdleController", "isHasGpsReport activeLocationArrayMap = 0");
      return false;
    }
    PackageManager localPackageManager = getContext().getPackageManager();
    int i = 0;
    if (i < localArrayMap.size())
    {
      String str = (String)localArrayMap.valueAt(i);
      Object localObject = (Integer)localArrayMap.keyAt(i);
      Slog.d("DeviceIdleController", "isHasGpsReport uid =" + localObject + "provider = " + str);
      if (localObject == null) {}
      for (;;)
      {
        i += 1;
        break;
        if (((Integer)localObject).intValue() >= 10000)
        {
          localObject = localPackageManager.getPackagesForUid(((Integer)localObject).intValue());
          int j = 0;
          while (j < localObject.length)
          {
            if (localObject[j] != null) {
              Slog.d("DeviceIdleController", "isHasGpsReport package =" + localObject[j]);
            }
            if ((localObject[j] != null) && (localObject[j].equals("com.amap.android.ams")) && (str.equals("gps"))) {
              return true;
            }
            try
            {
              ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(localObject[j], 0);
              if ((localApplicationInfo != null) && ((localApplicationInfo.flags & 0x1) == 0))
              {
                boolean bool = str.equals("gps");
                if (bool) {
                  return true;
                }
              }
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException)
            {
              j += 1;
            }
          }
        }
      }
    }
    return false;
  }
  
  private static String lightStateToString(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "ACTIVE";
    case 1: 
      return "INACTIVE";
    case 3: 
      return "PRE_IDLE";
    case 4: 
      return "IDLE";
    case 5: 
      return "WAITING_FOR_NETWORK";
    case 6: 
      return "IDLE_MAINTENANCE";
    }
    return "OVERRIDE";
  }
  
  private void postTempActiveTimeoutMessage(int paramInt, long paramLong)
  {
    Slog.d("DeviceIdleController", "postTempActiveTimeoutMessage: uid=" + paramInt + ", delay=" + paramLong);
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(6, paramInt, 0), paramLong);
  }
  
  private void readConfigFileLocked(XmlPullParser paramXmlPullParser)
  {
    PackageManager localPackageManager = getContext().getPackageManager();
    try
    {
      do
      {
        i = paramXmlPullParser.next();
      } while ((i != 2) && (i != 1));
      if (i != 2) {
        throw new IllegalStateException("no start tag found");
      }
    }
    catch (IllegalStateException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
      return;
      int i = paramXmlPullParser.getDepth();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4)) {
          if (paramXmlPullParser.getName().equals("wl"))
          {
            Object localObject = paramXmlPullParser.getAttributeValue(null, "n");
            if (localObject != null) {
              try
              {
                localObject = localPackageManager.getApplicationInfo((String)localObject, 8192);
                this.mPowerSaveWhitelistUserApps.put(((ApplicationInfo)localObject).packageName, Integer.valueOf(UserHandle.getAppId(((ApplicationInfo)localObject).uid)));
              }
              catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
            }
          }
          else
          {
            Slog.w("DeviceIdleController", "Unknown element under <config>: " + paramXmlPullParser.getName());
            XmlUtils.skipCurrentTag(paramXmlPullParser);
          }
        }
      }
    }
    catch (NullPointerException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
      return;
    }
    catch (IndexOutOfBoundsException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
      return;
    }
    catch (IOException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
      return;
    }
    catch (XmlPullParserException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
      return;
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      Slog.w("DeviceIdleController", "Failed parsing config " + paramXmlPullParser);
    }
  }
  
  private void reportPowerSaveWhitelistChangedLocked()
  {
    Intent localIntent = new Intent("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
    localIntent.addFlags(1073741824);
    getContext().sendBroadcastAsUser(localIntent, UserHandle.SYSTEM);
  }
  
  private void reportTempWhitelistChangedLocked()
  {
    Intent localIntent = new Intent("android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED");
    localIntent.addFlags(1073741824);
    getContext().sendBroadcastAsUser(localIntent, UserHandle.SYSTEM);
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "ACTIVE";
    case 1: 
      return "INACTIVE";
    case 2: 
      return "IDLE_PENDING";
    case 3: 
      return "SENSING";
    case 4: 
      return "LOCATING";
    case 5: 
      return "IDLE";
    }
    return "IDLE_MAINTENANCE";
  }
  
  private void updateEnabledStatus()
  {
    boolean bool1 = true;
    boolean bool2 = getContext().getResources().getBoolean(17956884);
    ContentResolver localContentResolver = getContext().getContentResolver();
    int i;
    if (bool2)
    {
      i = 1;
      if (Settings.System.getInt(localContentResolver, "doze_mode_enabaled", i) != 1) {
        break label60;
      }
    }
    for (;;)
    {
      this.mDeepEnabled = bool1;
      this.mLightEnabled = bool1;
      return;
      i = 0;
      break;
      label60:
      bool1 = false;
    }
  }
  
  private void updateTempWhitelistAppIdsLocked()
  {
    int j = this.mTempWhitelistAppIdEndTimes.size();
    if (this.mTempWhitelistAppIdArray.length != j) {
      this.mTempWhitelistAppIdArray = new int[j];
    }
    int i = 0;
    while (i < j)
    {
      this.mTempWhitelistAppIdArray[i] = this.mTempWhitelistAppIdEndTimes.keyAt(i);
      i += 1;
    }
    if (this.mLocalPowerManager != null)
    {
      Slog.d("DeviceIdleController", "Setting wakelock temp whitelist to " + Arrays.toString(this.mTempWhitelistAppIdArray));
      this.mLocalPowerManager.setDeviceIdleTempWhitelist(this.mTempWhitelistAppIdArray);
    }
  }
  
  private void updateWhitelistAppIdsLocked()
  {
    this.mPowerSaveWhitelistExceptIdleAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistAppsExceptIdle, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistExceptIdleAppIds);
    this.mPowerSaveWhitelistAllAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistApps, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistAllAppIds);
    this.mPowerSaveWhitelistUserAppIdArray = buildAppIdArray(null, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistUserAppIds);
    if (this.mLocalPowerManager != null)
    {
      Slog.d("DeviceIdleController", "Setting wakelock whitelist to " + Arrays.toString(this.mPowerSaveWhitelistAllAppIdArray));
      this.mLocalPowerManager.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray);
    }
    if (this.mLocalAlarmManager != null)
    {
      Slog.d("DeviceIdleController", "Setting alarm whitelist to " + Arrays.toString(this.mPowerSaveWhitelistUserAppIdArray));
      this.mLocalAlarmManager.setDeviceIdleUserWhitelist(this.mPowerSaveWhitelistUserAppIdArray);
    }
  }
  
  void addPowerSaveTempWhitelistAppChecked(String paramString1, long paramLong, int paramInt, String paramString2)
    throws RemoteException
  {
    getContext().enforceCallingPermission("android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST", "No permission to change device idle whitelist");
    int i = Binder.getCallingUid();
    paramInt = ActivityManagerNative.getDefault().handleIncomingUser(Binder.getCallingPid(), i, paramInt, false, false, "addPowerSaveTempWhitelistApp", null);
    long l = Binder.clearCallingIdentity();
    try
    {
      addPowerSaveTempWhitelistAppInternal(i, paramString1, paramLong, paramInt, true, paramString2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  void addPowerSaveTempWhitelistAppDirectInternal(int paramInt1, int paramInt2, long paramLong, boolean paramBoolean, String paramString)
  {
    long l = SystemClock.elapsedRealtime();
    Object localObject3 = null;
    Object localObject2 = null;
    try
    {
      int i = UserHandle.getAppId(paramInt1);
      if ((i >= 10000) && (!this.mPowerSaveWhitelistSystemAppIds.get(i))) {
        throw new SecurityException("Calling app " + UserHandle.formatUid(paramInt1) + " is not on whitelist");
      }
    }
    finally {}
    paramLong = Math.min(paramLong, this.mConstants.MAX_TEMP_APP_WHITELIST_DURATION);
    Object localObject1 = (Pair)this.mTempWhitelistAppIdEndTimes.get(paramInt2);
    boolean bool;
    if (localObject1 == null) {
      bool = true;
    }
    for (;;)
    {
      if (bool)
      {
        localObject1 = new Pair(new MutableLong(0L), paramString);
        this.mTempWhitelistAppIdEndTimes.put(paramInt2, localObject1);
      }
      ((MutableLong)((Pair)localObject1).first).value = (l + paramLong);
      Slog.d("DeviceIdleController", "Adding AppId " + paramInt2 + " to temp whitelist. New entry: " + bool);
      localObject1 = localObject3;
      if (bool) {}
      try
      {
        this.mBatteryStats.noteEvent(32785, paramString, paramInt2);
        postTempActiveTimeoutMessage(paramInt2, paramLong);
        updateTempWhitelistAppIdsLocked();
        paramString = (String)localObject2;
        if (this.mNetworkPolicyTempWhitelistCallback != null)
        {
          if (paramBoolean) {
            break label297;
          }
          this.mHandler.post(this.mNetworkPolicyTempWhitelistCallback);
        }
        label297:
        for (paramString = (String)localObject2;; paramString = this.mNetworkPolicyTempWhitelistCallback)
        {
          reportTempWhitelistChangedLocked();
          localObject1 = paramString;
          if (localObject1 != null) {
            ((Runnable)localObject1).run();
          }
          return;
          bool = false;
          break;
        }
      }
      catch (RemoteException paramString)
      {
        for (;;) {}
      }
    }
  }
  
  void addPowerSaveTempWhitelistAppInternal(int paramInt1, String paramString1, long paramLong, int paramInt2, boolean paramBoolean, String paramString2)
  {
    try
    {
      addPowerSaveTempWhitelistAppDirectInternal(paramInt1, UserHandle.getAppId(getContext().getPackageManager().getPackageUidAsUser(paramString1, paramInt2)), paramLong, paramBoolean, paramString2);
      return;
    }
    catch (PackageManager.NameNotFoundException paramString1) {}
  }
  
  public boolean addPowerSaveWhitelistAppInternal(String paramString)
  {
    try
    {
      ApplicationInfo localApplicationInfo = getContext().getPackageManager().getApplicationInfo(paramString, 8192);
      if (this.mPowerSaveWhitelistUserApps.put(paramString, Integer.valueOf(UserHandle.getAppId(localApplicationInfo.uid))) == null)
      {
        reportPowerSaveWhitelistChangedLocked();
        updateWhitelistAppIdsLocked();
        writeConfigFileLocked();
      }
      return true;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      paramString = paramString;
      return false;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  void becomeActiveLocked(String paramString, int paramInt)
  {
    Slog.i("DeviceIdleController", "becomeActiveLocked, reason = " + paramString);
    if ((this.mState != 0) || (this.mLightState != 0))
    {
      EventLogTags.writeDeviceIdle(0, paramString);
      EventLogTags.writeDeviceIdleLight(0, paramString);
      scheduleReportActiveLocked(paramString, paramInt);
      this.mState = 0;
      this.mLightState = 0;
      this.mInactiveTimeout = this.mConstants.INACTIVE_TIMEOUT;
      this.mCurIdleBudget = 0L;
      this.mMaintenanceStartTime = 0L;
      resetIdleManagementLocked();
      resetLightIdleManagementLocked();
      addEvent(1);
    }
  }
  
  void becomeInactiveIfAppropriateLocked()
  {
    Slog.d("DeviceIdleController", "becomeInactiveIfAppropriateLocked()");
    if (((!this.mScreenOn) && (!this.mCharging)) || (this.mForceIdle))
    {
      if ((this.mState == 0) && (this.mDeepEnabled))
      {
        this.mState = 1;
        Slog.d("DeviceIdleController", "Moved from STATE_ACTIVE to STATE_INACTIVE");
        resetIdleManagementLocked();
        scheduleAlarmLocked(this.mInactiveTimeout, false);
        EventLogTags.writeDeviceIdle(this.mState, "no activity");
      }
      if ((this.mLightState == 0) && (this.mLightEnabled))
      {
        this.mLightState = 1;
        Slog.d("DeviceIdleController", "Moved from LIGHT_STATE_ACTIVE to LIGHT_STATE_INACTIVE");
        resetLightIdleManagementLocked();
        scheduleLightAlarmLocked(this.mConstants.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT);
        EventLogTags.writeDeviceIdleLight(this.mLightState, "no activity");
      }
    }
  }
  
  void cancelAlarmLocked()
  {
    if (this.mNextAlarmTime != 0L)
    {
      this.mNextAlarmTime = 0L;
      this.mAlarmManager.cancel(this.mDeepAlarmListener);
    }
  }
  
  void cancelLightAlarmLocked()
  {
    if (this.mNextLightAlarmTime != 0L)
    {
      this.mNextLightAlarmTime = 0L;
      this.mAlarmManager.cancel(this.mLightAlarmListener);
    }
  }
  
  void cancelLocatingLocked()
  {
    if (this.mLocating)
    {
      this.mLocationManager.removeUpdates(this.mGenericLocationListener);
      this.mLocationManager.removeUpdates(this.mGpsLocationListener);
      if (!OpFeatures.isSupport(new int[] { 1 })) {
        this.mLocationManager.clearAllPendingBroadcastsLocked();
      }
      this.mLocating = false;
    }
  }
  
  void cancelSensingTimeoutAlarmLocked()
  {
    if (this.mNextSensingTimeoutAlarmTime != 0L)
    {
      this.mNextSensingTimeoutAlarmTime = 0L;
      this.mAlarmManager.cancel(this.mSensingTimeoutAlarmListener);
    }
  }
  
  /* Error */
  void checkTempAppWhitelistTimeout(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 431	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore_2
    //   4: ldc 89
    //   6: new 530	java/lang/StringBuilder
    //   9: dup
    //   10: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   13: ldc_w 1071
    //   16: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: iload_1
    //   20: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   23: ldc_w 1073
    //   26: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: lload_2
    //   30: invokevirtual 678	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   33: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: aload_0
    //   41: monitorenter
    //   42: aload_0
    //   43: getfield 343	com/android/server/DeviceIdleController:mTempWhitelistAppIdEndTimes	Landroid/util/SparseArray;
    //   46: iload_1
    //   47: invokevirtual 908	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   50: checkcast 910	android/util/Pair
    //   53: astore 4
    //   55: aload 4
    //   57: ifnonnull +6 -> 63
    //   60: aload_0
    //   61: monitorexit
    //   62: return
    //   63: lload_2
    //   64: aload 4
    //   66: getfield 923	android/util/Pair:first	Ljava/lang/Object;
    //   69: checkcast 912	android/util/MutableLong
    //   72: getfield 926	android/util/MutableLong:value	J
    //   75: lcmp
    //   76: iflt +94 -> 170
    //   79: aload_0
    //   80: getfield 343	com/android/server/DeviceIdleController:mTempWhitelistAppIdEndTimes	Landroid/util/SparseArray;
    //   83: iload_1
    //   84: invokevirtual 1076	android/util/SparseArray:delete	(I)V
    //   87: ldc 89
    //   89: new 530	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   96: ldc_w 1078
    //   99: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: iload_1
    //   103: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   106: ldc_w 1080
    //   109: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   115: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   118: pop
    //   119: aload_0
    //   120: invokespecial 944	com/android/server/DeviceIdleController:updateTempWhitelistAppIdsLocked	()V
    //   123: aload_0
    //   124: getfield 946	com/android/server/DeviceIdleController:mNetworkPolicyTempWhitelistCallback	Ljava/lang/Runnable;
    //   127: ifnull +15 -> 142
    //   130: aload_0
    //   131: getfield 414	com/android/server/DeviceIdleController:mHandler	Lcom/android/server/DeviceIdleController$MyHandler;
    //   134: aload_0
    //   135: getfield 946	com/android/server/DeviceIdleController:mNetworkPolicyTempWhitelistCallback	Ljava/lang/Runnable;
    //   138: invokevirtual 950	com/android/server/DeviceIdleController$MyHandler:post	(Ljava/lang/Runnable;)Z
    //   141: pop
    //   142: aload_0
    //   143: invokespecial 952	com/android/server/DeviceIdleController:reportTempWhitelistChangedLocked	()V
    //   146: aload_0
    //   147: getfield 219	com/android/server/DeviceIdleController:mBatteryStats	Lcom/android/internal/app/IBatteryStats;
    //   150: sipush 16401
    //   153: aload 4
    //   155: getfield 1083	android/util/Pair:second	Ljava/lang/Object;
    //   158: checkcast 467	java/lang/String
    //   161: iload_1
    //   162: invokeinterface 940 4 0
    //   167: aload_0
    //   168: monitorexit
    //   169: return
    //   170: ldc 89
    //   172: new 530	java/lang/StringBuilder
    //   175: dup
    //   176: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   179: ldc_w 1085
    //   182: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: iload_1
    //   186: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   189: ldc_w 1087
    //   192: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: aload 4
    //   197: getfield 923	android/util/Pair:first	Ljava/lang/Object;
    //   200: checkcast 912	android/util/MutableLong
    //   203: getfield 926	android/util/MutableLong:value	J
    //   206: invokevirtual 678	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   209: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   215: pop
    //   216: aload_0
    //   217: iload_1
    //   218: aload 4
    //   220: getfield 923	android/util/Pair:first	Ljava/lang/Object;
    //   223: checkcast 912	android/util/MutableLong
    //   226: getfield 926	android/util/MutableLong:value	J
    //   229: lload_2
    //   230: lsub
    //   231: invokespecial 942	com/android/server/DeviceIdleController:postTempActiveTimeoutMessage	(IJ)V
    //   234: goto -67 -> 167
    //   237: astore 4
    //   239: aload_0
    //   240: monitorexit
    //   241: aload 4
    //   243: athrow
    //   244: astore 4
    //   246: goto -79 -> 167
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	249	0	this	DeviceIdleController
    //   0	249	1	paramInt	int
    //   3	227	2	l	long
    //   53	166	4	localPair	Pair
    //   237	5	4	localObject	Object
    //   244	1	4	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   42	55	237	finally
    //   63	142	237	finally
    //   142	146	237	finally
    //   146	167	237	finally
    //   170	234	237	finally
    //   146	167	244	android/os/RemoteException
  }
  
  void decActiveIdleOps()
  {
    try
    {
      this.mActiveIdleOpCount -= 1;
      if (this.mActiveIdleOpCount <= 0)
      {
        exitMaintenanceEarlyIfNeededLocked();
        this.mActiveIdleWakeLock.release();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump DeviceIdleController from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
      return;
    }
    if (paramArrayOfString != null)
    {
      j = 0;
      i = 0;
      if (i < paramArrayOfString.length)
      {
        Object localObject = paramArrayOfString[i];
        if ("-h".equals(localObject))
        {
          dumpHelp(paramPrintWriter);
          return;
        }
        int m;
        int k;
        if ("-u".equals(localObject))
        {
          i += 1;
          m = i;
          k = j;
          if (i < paramArrayOfString.length)
          {
            k = Integer.parseInt(paramArrayOfString[i]);
            m = i;
          }
        }
        do
        {
          i = m + 1;
          j = k;
          break;
          m = i;
          k = j;
        } while ("-a".equals(localObject));
        if ((((String)localObject).length() > 0) && (((String)localObject).charAt(0) == '-'))
        {
          paramPrintWriter.println("Unknown option: " + (String)localObject);
          return;
        }
        paramPrintWriter = new Shell();
        paramPrintWriter.userId = j;
        localObject = new String[paramArrayOfString.length - i];
        System.arraycopy(paramArrayOfString, i, localObject, 0, paramArrayOfString.length - i);
        paramPrintWriter.exec(this.mBinderService, null, paramFileDescriptor, null, (String[])localObject, new ResultReceiver(null));
        return;
      }
    }
    for (;;)
    {
      try
      {
        this.mConstants.dump(paramPrintWriter);
        if (this.mEventCmds[0] == 0) {
          break;
        }
        paramPrintWriter.println("  Idling history:");
        long l = SystemClock.elapsedRealtime();
        i = 99;
        if (i < 0) {
          break;
        }
        if (this.mEventCmds[i] == 0) {
          break label1471;
        }
        switch (this.mEventCmds[i])
        {
        case 1: 
          label380:
          paramPrintWriter.print("    ");
          paramPrintWriter.print(paramFileDescriptor);
          paramPrintWriter.print(": ");
          TimeUtils.formatDuration(this.mEventTimes[i], l, paramPrintWriter);
          paramPrintWriter.println();
        }
      }
      finally {}
      paramFileDescriptor = "     normal";
    }
    int j = this.mPowerSaveWhitelistAppsExceptIdle.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist (except idle) system apps:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)this.mPowerSaveWhitelistAppsExceptIdle.keyAt(i));
        i += 1;
      }
    }
    j = this.mPowerSaveWhitelistApps.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist system apps:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)this.mPowerSaveWhitelistApps.keyAt(i));
        i += 1;
      }
    }
    j = this.mPowerSaveWhitelistUserApps.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist user apps:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)this.mPowerSaveWhitelistUserApps.keyAt(i));
        i += 1;
      }
    }
    j = this.mPowerSaveWhitelistExceptIdleAppIds.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist (except idle) all app ids:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.keyAt(i));
        paramPrintWriter.println();
        i += 1;
      }
    }
    j = this.mPowerSaveWhitelistUserAppIds.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist user app ids:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.print(this.mPowerSaveWhitelistUserAppIds.keyAt(i));
        paramPrintWriter.println();
        i += 1;
      }
    }
    j = this.mPowerSaveWhitelistAllAppIds.size();
    if (j > 0)
    {
      paramPrintWriter.println("  Whitelist all app ids:");
      i = 0;
      while (i < j)
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.print(this.mPowerSaveWhitelistAllAppIds.keyAt(i));
        paramPrintWriter.println();
        i += 1;
      }
    }
    dumpTempWhitelistSchedule(paramPrintWriter, true);
    if (this.mTempWhitelistAppIdArray != null) {}
    for (int i = this.mTempWhitelistAppIdArray.length;; i = 0)
    {
      if (i > 0)
      {
        paramPrintWriter.println("  Temp whitelist app ids:");
        j = 0;
        while (j < i)
        {
          paramPrintWriter.print("    ");
          paramPrintWriter.print(this.mTempWhitelistAppIdArray[j]);
          paramPrintWriter.println();
          j += 1;
        }
      }
      paramPrintWriter.print("  mLightEnabled=");
      paramPrintWriter.print(this.mLightEnabled);
      paramPrintWriter.print("  mDeepEnabled=");
      paramPrintWriter.println(this.mDeepEnabled);
      paramPrintWriter.print("  mForceIdle=");
      paramPrintWriter.println(this.mForceIdle);
      paramPrintWriter.print("  mMotionSensor=");
      paramPrintWriter.println(this.mMotionSensor);
      paramPrintWriter.print("  mCurDisplay=");
      paramPrintWriter.println(this.mCurDisplay);
      paramPrintWriter.print("  mScreenOn=");
      paramPrintWriter.println(this.mScreenOn);
      paramPrintWriter.print("  mNetworkConnected=");
      paramPrintWriter.println(this.mNetworkConnected);
      paramPrintWriter.print("  mCharging=");
      paramPrintWriter.println(this.mCharging);
      paramPrintWriter.print("  mMotionActive=");
      paramPrintWriter.println(this.mMotionListener.active);
      paramPrintWriter.print("  mNotMoving=");
      paramPrintWriter.println(this.mNotMoving);
      paramPrintWriter.print("  mLocating=");
      paramPrintWriter.print(this.mLocating);
      paramPrintWriter.print(" mHasGps=");
      paramPrintWriter.print(this.mHasGps);
      paramPrintWriter.print(" mHasNetwork=");
      paramPrintWriter.print(this.mHasNetworkLocation);
      paramPrintWriter.print(" mLocated=");
      paramPrintWriter.println(this.mLocated);
      if (this.mLastGenericLocation != null)
      {
        paramPrintWriter.print("  mLastGenericLocation=");
        paramPrintWriter.println(this.mLastGenericLocation);
      }
      if (this.mLastGpsLocation != null)
      {
        paramPrintWriter.print("  mLastGpsLocation=");
        paramPrintWriter.println(this.mLastGpsLocation);
      }
      paramPrintWriter.print("  mState=");
      paramPrintWriter.print(stateToString(this.mState));
      paramPrintWriter.print(" mLightState=");
      paramPrintWriter.println(lightStateToString(this.mLightState));
      paramPrintWriter.print("  mInactiveTimeout=");
      TimeUtils.formatDuration(this.mInactiveTimeout, paramPrintWriter);
      paramPrintWriter.println();
      if (this.mActiveIdleOpCount != 0)
      {
        paramPrintWriter.print("  mActiveIdleOpCount=");
        paramPrintWriter.println(this.mActiveIdleOpCount);
      }
      if (this.mNextAlarmTime != 0L)
      {
        paramPrintWriter.print("  mNextAlarmTime=");
        TimeUtils.formatDuration(this.mNextAlarmTime, SystemClock.elapsedRealtime(), paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mNextIdlePendingDelay != 0L)
      {
        paramPrintWriter.print("  mNextIdlePendingDelay=");
        TimeUtils.formatDuration(this.mNextIdlePendingDelay, paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mNextIdleDelay != 0L)
      {
        paramPrintWriter.print("  mNextIdleDelay=");
        TimeUtils.formatDuration(this.mNextIdleDelay, paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mNextLightIdleDelay != 0L)
      {
        paramPrintWriter.print("  mNextIdleDelay=");
        TimeUtils.formatDuration(this.mNextLightIdleDelay, paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mNextLightAlarmTime != 0L)
      {
        paramPrintWriter.print("  mNextLightAlarmTime=");
        TimeUtils.formatDuration(this.mNextLightAlarmTime, SystemClock.elapsedRealtime(), paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mCurIdleBudget != 0L)
      {
        paramPrintWriter.print("  mCurIdleBudget=");
        TimeUtils.formatDuration(this.mCurIdleBudget, paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mMaintenanceStartTime != 0L)
      {
        paramPrintWriter.print("  mMaintenanceStartTime=");
        TimeUtils.formatDuration(this.mMaintenanceStartTime, SystemClock.elapsedRealtime(), paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mJobsActive)
      {
        paramPrintWriter.print("  mJobsActive=");
        paramPrintWriter.println(this.mJobsActive);
      }
      if (this.mAlarmsActive)
      {
        paramPrintWriter.print("  mAlarmsActive=");
        paramPrintWriter.println(this.mAlarmsActive);
      }
      return;
      label1471:
      i -= 1;
      break;
      paramFileDescriptor = "         ??";
      break label380;
      paramFileDescriptor = " light-idle";
      break label380;
      paramFileDescriptor = "light-maint";
      break label380;
      paramFileDescriptor = "  deep-idle";
      break label380;
      paramFileDescriptor = " deep-maint";
      break label380;
    }
  }
  
  void dumpTempWhitelistSchedule(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    int j = this.mTempWhitelistAppIdEndTimes.size();
    if (j > 0)
    {
      String str = "";
      if (paramBoolean)
      {
        paramPrintWriter.println("  Temp whitelist schedule:");
        str = "    ";
      }
      long l = SystemClock.elapsedRealtime();
      int i = 0;
      while (i < j)
      {
        paramPrintWriter.print(str);
        paramPrintWriter.print("UID=");
        paramPrintWriter.print(this.mTempWhitelistAppIdEndTimes.keyAt(i));
        paramPrintWriter.print(": ");
        Pair localPair = (Pair)this.mTempWhitelistAppIdEndTimes.valueAt(i);
        TimeUtils.formatDuration(((MutableLong)localPair.first).value, l, paramPrintWriter);
        paramPrintWriter.print(" - ");
        paramPrintWriter.println((String)localPair.second);
        i += 1;
      }
    }
  }
  
  void exitForceIdleLocked()
  {
    if (this.mForceIdle)
    {
      this.mForceIdle = false;
      if ((this.mScreenOn) || (this.mCharging)) {
        becomeActiveLocked("exit-force", Process.myUid());
      }
    }
  }
  
  public void exitIdleInternal(String paramString)
  {
    try
    {
      becomeActiveLocked(paramString, Binder.getCallingUid());
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  void exitMaintenanceEarlyIfNeededLocked()
  {
    if ((this.mState == 6) || (this.mLightState == 6)) {}
    for (;;)
    {
      if (isOpsInactiveLocked())
      {
        long l = SystemClock.elapsedRealtime();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Exit: start=");
        TimeUtils.formatDuration(this.mMaintenanceStartTime, localStringBuilder);
        localStringBuilder.append(" now=");
        TimeUtils.formatDuration(l, localStringBuilder);
        Slog.d("DeviceIdleController", localStringBuilder.toString());
        if (this.mState != 6) {
          break;
        }
        stepIdleStateLocked("s:early");
      }
      do
      {
        return;
      } while (this.mLightState != 3);
    }
    if (this.mLightState == 3)
    {
      stepLightIdleStateLocked("s:predone");
      return;
    }
    stepLightIdleStateLocked("s:early");
  }
  
  public int[] getAppIdTempWhitelistInternal()
  {
    try
    {
      int[] arrayOfInt = this.mTempWhitelistAppIdArray;
      return arrayOfInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int[] getAppIdUserWhitelistInternal()
  {
    try
    {
      int[] arrayOfInt = this.mPowerSaveWhitelistUserAppIdArray;
      return arrayOfInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int[] getAppIdWhitelistExceptIdleInternal()
  {
    try
    {
      int[] arrayOfInt = this.mPowerSaveWhitelistExceptIdleAppIdArray;
      return arrayOfInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int[] getAppIdWhitelistInternal()
  {
    try
    {
      int[] arrayOfInt = this.mPowerSaveWhitelistAllAppIdArray;
      return arrayOfInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public String[] getFullPowerWhitelistExceptIdleInternal()
  {
    for (;;)
    {
      try
      {
        String[] arrayOfString = new String[this.mPowerSaveWhitelistAppsExceptIdle.size() + this.mPowerSaveWhitelistUserApps.size()];
        int i = 0;
        j = 0;
        if (j < this.mPowerSaveWhitelistAppsExceptIdle.size())
        {
          arrayOfString[i] = ((String)this.mPowerSaveWhitelistAppsExceptIdle.keyAt(j));
          i += 1;
          j += 1;
          continue;
          if (j < this.mPowerSaveWhitelistUserApps.size())
          {
            arrayOfString[i] = ((String)this.mPowerSaveWhitelistUserApps.keyAt(j));
            i += 1;
            j += 1;
            continue;
          }
          return arrayOfString;
        }
      }
      finally {}
      int j = 0;
    }
  }
  
  public String[] getFullPowerWhitelistInternal()
  {
    for (;;)
    {
      try
      {
        String[] arrayOfString = new String[this.mPowerSaveWhitelistApps.size() + this.mPowerSaveWhitelistUserApps.size()];
        int i = 0;
        j = 0;
        if (j < this.mPowerSaveWhitelistApps.size())
        {
          arrayOfString[i] = ((String)this.mPowerSaveWhitelistApps.keyAt(j));
          i += 1;
          j += 1;
          continue;
          if (j < this.mPowerSaveWhitelistUserApps.size())
          {
            arrayOfString[i] = ((String)this.mPowerSaveWhitelistUserApps.keyAt(j));
            i += 1;
            j += 1;
            continue;
          }
          return arrayOfString;
        }
      }
      finally {}
      int j = 0;
    }
  }
  
  public boolean getPowerSaveWhitelistAppInternal(String paramString)
  {
    try
    {
      boolean bool = this.mPowerSaveWhitelistUserApps.containsKey(paramString);
      return bool;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  int[] getPowerSaveWhitelistUserAppIds()
  {
    try
    {
      int[] arrayOfInt = this.mPowerSaveWhitelistUserAppIdArray;
      return arrayOfInt;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public String[] getSystemPowerWhitelistExceptIdleInternal()
  {
    try
    {
      int j = this.mPowerSaveWhitelistAppsExceptIdle.size();
      String[] arrayOfString = new String[j];
      int i = 0;
      while (i < j)
      {
        arrayOfString[i] = ((String)this.mPowerSaveWhitelistAppsExceptIdle.keyAt(i));
        i += 1;
      }
      return arrayOfString;
    }
    finally {}
  }
  
  public String[] getSystemPowerWhitelistInternal()
  {
    try
    {
      int j = this.mPowerSaveWhitelistApps.size();
      String[] arrayOfString = new String[j];
      int i = 0;
      while (i < j)
      {
        arrayOfString[i] = ((String)this.mPowerSaveWhitelistApps.keyAt(i));
        i += 1;
      }
      return arrayOfString;
    }
    finally {}
  }
  
  public String[] getUserPowerWhitelistInternal()
  {
    try
    {
      String[] arrayOfString = new String[this.mPowerSaveWhitelistUserApps.size()];
      int i = 0;
      while (i < this.mPowerSaveWhitelistUserApps.size())
      {
        arrayOfString[i] = ((String)this.mPowerSaveWhitelistUserApps.keyAt(i));
        i += 1;
      }
      return arrayOfString;
    }
    finally {}
  }
  
  void handleMotionDetectedLocked(long paramLong, String paramString)
  {
    int i = 0;
    if (this.mState != 0)
    {
      scheduleReportActiveLocked(paramString, Process.myUid());
      this.mState = 0;
      this.mInactiveTimeout = paramLong;
      this.mCurIdleBudget = 0L;
      this.mMaintenanceStartTime = 0L;
      EventLogTags.writeDeviceIdle(this.mState, paramString);
      addEvent(1);
      i = 1;
    }
    if (this.mLightState == 7)
    {
      this.mLightState = 0;
      EventLogTags.writeDeviceIdleLight(this.mLightState, paramString);
      i = 1;
    }
    if (i != 0) {
      becomeInactiveIfAppropriateLocked();
    }
  }
  
  void handleWriteConfigFile()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (;;)
    {
      try {}catch (IOException localIOException1)
      {
        Object localObject1;
        FileOutputStream localFileOutputStream;
        label93:
        continue;
      }
      try
      {
        localObject1 = new FastXmlSerializer();
        ((XmlSerializer)localObject1).setOutput(localByteArrayOutputStream, StandardCharsets.UTF_8.name());
        writeConfigFileLocked((XmlSerializer)localObject1);
        localAtomicFile = this.mConfigFile;
        localObject1 = null;
      }
      finally
      {
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    try
    {
      localFileOutputStream = this.mConfigFile.startWrite();
      localObject1 = localFileOutputStream;
      localByteArrayOutputStream.writeTo(localFileOutputStream);
      localObject1 = localFileOutputStream;
      localFileOutputStream.flush();
      localObject1 = localFileOutputStream;
      FileUtils.sync(localFileOutputStream);
      localObject1 = localFileOutputStream;
      localFileOutputStream.close();
      localObject1 = localFileOutputStream;
      this.mConfigFile.finishWrite(localFileOutputStream);
    }
    catch (IOException localIOException2)
    {
      Slog.w("DeviceIdleController", "Error writing config file", localIOException2);
      this.mConfigFile.failWrite(localIOException1);
      break label93;
    }
    finally {}
  }
  
  void incActiveIdleOps()
  {
    try
    {
      this.mActiveIdleOpCount += 1;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  boolean isOpsInactiveLocked()
  {
    if ((this.mActiveIdleOpCount > 0) || (this.mJobsActive)) {}
    while (this.mAlarmsActive) {
      return false;
    }
    return true;
  }
  
  /* Error */
  public boolean isPowerSaveWhitelistAppInternal(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 317	com/android/server/DeviceIdleController:mPowerSaveWhitelistApps	Landroid/util/ArrayMap;
    //   6: aload_1
    //   7: invokevirtual 1355	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   10: ifne +16 -> 26
    //   13: aload_0
    //   14: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   17: aload_1
    //   18: invokevirtual 1355	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   21: istore_2
    //   22: aload_0
    //   23: monitorexit
    //   24: iload_2
    //   25: ireturn
    //   26: iconst_1
    //   27: istore_2
    //   28: goto -6 -> 22
    //   31: astore_1
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_1
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	DeviceIdleController
    //   0	36	1	paramString	String
    //   21	7	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	22	31	finally
  }
  
  /* Error */
  public boolean isPowerSaveWhitelistExceptIdleAppInternal(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   6: aload_1
    //   7: invokevirtual 1355	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   10: ifne +16 -> 26
    //   13: aload_0
    //   14: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   17: aload_1
    //   18: invokevirtual 1355	android/util/ArrayMap:containsKey	(Ljava/lang/Object;)Z
    //   21: istore_2
    //   22: aload_0
    //   23: monitorexit
    //   24: iload_2
    //   25: ireturn
    //   26: iconst_1
    //   27: istore_2
    //   28: goto -6 -> 22
    //   31: astore_1
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_1
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	DeviceIdleController
    //   0	36	1	paramString	String
    //   21	7	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	22	31	finally
  }
  
  void motionLocked()
  {
    Slog.d("DeviceIdleController", "motionLocked()");
    handleMotionDetectedLocked(this.mConstants.MOTION_INACTIVE_TIMEOUT, "motion");
  }
  
  /* Error */
  public void onAnyMotionResult(int paramInt)
  {
    // Byte code:
    //   0: ldc 89
    //   2: new 530	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 1439
    //   12: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: iload_1
    //   16: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   19: ldc_w 1441
    //   22: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: iload_1
    //   33: iconst_m1
    //   34: if_icmpeq +11 -> 45
    //   37: aload_0
    //   38: monitorenter
    //   39: aload_0
    //   40: invokevirtual 1443	com/android/server/DeviceIdleController:cancelSensingTimeoutAlarmLocked	()V
    //   43: aload_0
    //   44: monitorexit
    //   45: iload_1
    //   46: iconst_1
    //   47: if_icmpeq +8 -> 55
    //   50: iload_1
    //   51: iconst_m1
    //   52: if_icmpne +9 -> 61
    //   55: getstatic 302	com/android/server/DeviceIdleController:mDozeChange	Z
    //   58: ifeq +37 -> 95
    //   61: iload_1
    //   62: ifne +27 -> 89
    //   65: aload_0
    //   66: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   69: iconst_3
    //   70: if_icmpne +54 -> 124
    //   73: aload_0
    //   74: monitorenter
    //   75: aload_0
    //   76: iconst_1
    //   77: putfield 1228	com/android/server/DeviceIdleController:mNotMoving	Z
    //   80: aload_0
    //   81: ldc_w 1445
    //   84: invokevirtual 1338	com/android/server/DeviceIdleController:stepIdleStateLocked	(Ljava/lang/String;)V
    //   87: aload_0
    //   88: monitorexit
    //   89: return
    //   90: astore_2
    //   91: aload_0
    //   92: monitorexit
    //   93: aload_2
    //   94: athrow
    //   95: aload_0
    //   96: monitorenter
    //   97: aload_0
    //   98: aload_0
    //   99: getfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   102: getfield 992	com/android/server/DeviceIdleController$Constants:INACTIVE_TIMEOUT	J
    //   105: ldc_w 1447
    //   108: invokevirtual 1436	com/android/server/DeviceIdleController:handleMotionDetectedLocked	(JLjava/lang/String;)V
    //   111: goto -24 -> 87
    //   114: astore_2
    //   115: aload_0
    //   116: monitorexit
    //   117: aload_2
    //   118: athrow
    //   119: astore_2
    //   120: aload_0
    //   121: monitorexit
    //   122: aload_2
    //   123: athrow
    //   124: aload_0
    //   125: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   128: iconst_4
    //   129: if_icmpne -40 -> 89
    //   132: aload_0
    //   133: monitorenter
    //   134: aload_0
    //   135: iconst_1
    //   136: putfield 1228	com/android/server/DeviceIdleController:mNotMoving	Z
    //   139: aload_0
    //   140: getfield 1242	com/android/server/DeviceIdleController:mLocated	Z
    //   143: ifeq -56 -> 87
    //   146: aload_0
    //   147: ldc_w 1445
    //   150: invokevirtual 1338	com/android/server/DeviceIdleController:stepIdleStateLocked	(Ljava/lang/String;)V
    //   153: goto -66 -> 87
    //   156: astore_2
    //   157: aload_0
    //   158: monitorexit
    //   159: aload_2
    //   160: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	161	0	this	DeviceIdleController
    //   0	161	1	paramInt	int
    //   90	4	2	localObject1	Object
    //   114	4	2	localObject2	Object
    //   119	4	2	localObject3	Object
    //   156	4	2	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   39	43	90	finally
    //   97	111	114	finally
    //   75	87	119	finally
    //   134	153	156	finally
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500) {}
    try
    {
      this.mAlarmManager = ((AlarmManager)getContext().getSystemService("alarm"));
      this.mBatteryStats = BatteryStatsService.getService();
      this.mLocalPowerManager = ((PowerManagerInternal)getLocalService(PowerManagerInternal.class));
      mLocationManagerService = (LocationManagerService)ServiceManager.getService("location");
      this.mPowerManager = ((PowerManager)getContext().getSystemService(PowerManager.class));
      this.mActiveIdleWakeLock = this.mPowerManager.newWakeLock(1, "deviceidle_maint");
      this.mActiveIdleWakeLock.setReferenceCounted(false);
      this.mConnectivityService = ((ConnectivityService)ServiceManager.getService("connectivity"));
      this.mLocalAlarmManager = ((AlarmManagerService.LocalService)getLocalService(AlarmManagerService.LocalService.class));
      this.mNetworkPolicyManager = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
      this.mDisplayManager = ((DisplayManager)getContext().getSystemService("display"));
      this.mSensorManager = ((SensorManager)getContext().getSystemService("sensor"));
      this.mLightsManager = ((LightsManager)getLocalService(LightsManager.class));
      paramInt = getContext().getResources().getInteger(17694733);
      if (paramInt > 0) {
        this.mMotionSensor = this.mSensorManager.getDefaultSensor(paramInt, true);
      }
      if ((this.mMotionSensor == null) && (getContext().getResources().getBoolean(17956885))) {
        this.mMotionSensor = this.mSensorManager.getDefaultSensor(26, true);
      }
      if (this.mMotionSensor == null) {
        this.mMotionSensor = this.mSensorManager.getDefaultSensor(17, true);
      }
      if (getContext().getResources().getBoolean(17956886))
      {
        this.mLocationManager = ((LocationManager)getContext().getSystemService("location"));
        this.mLocationRequest = new LocationRequest().setQuality(100).setInterval(0L).setFastestInterval(0L).setNumUpdates(1);
      }
      float f = getContext().getResources().getInteger(17694732) / 100.0F;
      this.mAnyMotionDetector = new AnyMotionDetector((PowerManager)getContext().getSystemService("power"), this.mHandler, this.mSensorManager, this, f);
      this.mIdleIntent = new Intent("android.os.action.DEVICE_IDLE_MODE_CHANGED");
      this.mIdleIntent.addFlags(1342177280);
      this.mLightIdleIntent = new Intent("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
      this.mLightIdleIntent.addFlags(1342177280);
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
      getContext().registerReceiver(this.mReceiver, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addDataScheme("package");
      getContext().registerReceiver(this.mReceiver, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      getContext().registerReceiver(this.mReceiver, localIntentFilter);
      this.mLocalPowerManager.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray);
      this.mLocalAlarmManager.setDeviceIdleUserWhitelist(this.mPowerSaveWhitelistUserAppIdArray);
      this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
      updateDisplayLocked();
      Constants.-wrap0(this.mConstants);
      updateConnectivityState(null);
      return;
    }
    finally {}
  }
  
  /* Error */
  int onShellCommand(Shell paramShell, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 1590	com/android/server/DeviceIdleController$Shell:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   4: astore 10
    //   6: ldc_w 1592
    //   9: aload_2
    //   10: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   13: ifeq +157 -> 170
    //   16: aload_0
    //   17: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   20: ldc_w 1594
    //   23: aconst_null
    //   24: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   27: aload_0
    //   28: monitorenter
    //   29: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   32: lstore 7
    //   34: aload_1
    //   35: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   38: astore_1
    //   39: aload_1
    //   40: ifnull +13 -> 53
    //   43: ldc_w 1602
    //   46: aload_1
    //   47: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   50: ifeq +39 -> 89
    //   53: aload_0
    //   54: ldc_w 1604
    //   57: invokevirtual 1338	com/android/server/DeviceIdleController:stepIdleStateLocked	(Ljava/lang/String;)V
    //   60: aload 10
    //   62: ldc_w 1606
    //   65: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   68: aload 10
    //   70: aload_0
    //   71: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   74: invokestatic 1254	com/android/server/DeviceIdleController:stateToString	(I)Ljava/lang/String;
    //   77: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   80: lload 7
    //   82: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   85: aload_0
    //   86: monitorexit
    //   87: iconst_0
    //   88: ireturn
    //   89: ldc_w 1608
    //   92: aload_1
    //   93: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   96: ifeq +46 -> 142
    //   99: aload_0
    //   100: ldc_w 1604
    //   103: invokevirtual 1343	com/android/server/DeviceIdleController:stepLightIdleStateLocked	(Ljava/lang/String;)V
    //   106: aload 10
    //   108: ldc_w 1610
    //   111: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   114: aload 10
    //   116: aload_0
    //   117: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   120: invokestatic 1258	com/android/server/DeviceIdleController:lightStateToString	(I)Ljava/lang/String;
    //   123: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   126: goto -46 -> 80
    //   129: astore_1
    //   130: lload 7
    //   132: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   135: aload_1
    //   136: athrow
    //   137: astore_1
    //   138: aload_0
    //   139: monitorexit
    //   140: aload_1
    //   141: athrow
    //   142: aload 10
    //   144: new 530	java/lang/StringBuilder
    //   147: dup
    //   148: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   151: ldc_w 1612
    //   154: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   157: aload_1
    //   158: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   167: goto -87 -> 80
    //   170: ldc_w 1614
    //   173: aload_2
    //   174: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   177: ifeq +292 -> 469
    //   180: aload_0
    //   181: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   184: ldc_w 1594
    //   187: aconst_null
    //   188: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   191: aload_0
    //   192: monitorenter
    //   193: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   196: lstore 7
    //   198: aload_1
    //   199: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   202: astore_1
    //   203: aload_1
    //   204: ifnull +13 -> 217
    //   207: ldc_w 1602
    //   210: aload_1
    //   211: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   214: ifeq +123 -> 337
    //   217: aload_0
    //   218: getfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   221: ifne +20 -> 241
    //   224: aload 10
    //   226: ldc_w 1616
    //   229: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   232: lload 7
    //   234: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   237: aload_0
    //   238: monitorexit
    //   239: iconst_m1
    //   240: ireturn
    //   241: aload_0
    //   242: iconst_1
    //   243: putfield 1015	com/android/server/DeviceIdleController:mForceIdle	Z
    //   246: aload_0
    //   247: invokevirtual 1363	com/android/server/DeviceIdleController:becomeInactiveIfAppropriateLocked	()V
    //   250: aload_0
    //   251: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   254: istore_3
    //   255: iload_3
    //   256: iconst_5
    //   257: if_icmpeq +59 -> 316
    //   260: aload_0
    //   261: ldc_w 1604
    //   264: invokevirtual 1338	com/android/server/DeviceIdleController:stepIdleStateLocked	(Ljava/lang/String;)V
    //   267: iload_3
    //   268: aload_0
    //   269: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   272: if_icmpne +36 -> 308
    //   275: aload 10
    //   277: ldc_w 1618
    //   280: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   283: aload 10
    //   285: aload_0
    //   286: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   289: invokestatic 1254	com/android/server/DeviceIdleController:stateToString	(I)Ljava/lang/String;
    //   292: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   295: aload_0
    //   296: invokevirtual 1620	com/android/server/DeviceIdleController:exitForceIdleLocked	()V
    //   299: lload 7
    //   301: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   304: aload_0
    //   305: monitorexit
    //   306: iconst_m1
    //   307: ireturn
    //   308: aload_0
    //   309: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   312: istore_3
    //   313: goto -58 -> 255
    //   316: aload 10
    //   318: ldc_w 1622
    //   321: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   324: lload 7
    //   326: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   329: goto -244 -> 85
    //   332: astore_1
    //   333: aload_0
    //   334: monitorexit
    //   335: aload_1
    //   336: athrow
    //   337: ldc_w 1608
    //   340: aload_1
    //   341: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   344: ifeq +97 -> 441
    //   347: aload_0
    //   348: iconst_1
    //   349: putfield 1015	com/android/server/DeviceIdleController:mForceIdle	Z
    //   352: aload_0
    //   353: invokevirtual 1363	com/android/server/DeviceIdleController:becomeInactiveIfAppropriateLocked	()V
    //   356: aload_0
    //   357: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   360: istore_3
    //   361: iload_3
    //   362: iconst_4
    //   363: if_icmpeq +59 -> 422
    //   366: aload_0
    //   367: ldc_w 1604
    //   370: invokevirtual 1338	com/android/server/DeviceIdleController:stepIdleStateLocked	(Ljava/lang/String;)V
    //   373: iload_3
    //   374: aload_0
    //   375: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   378: if_icmpne +36 -> 414
    //   381: aload 10
    //   383: ldc_w 1624
    //   386: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   389: aload 10
    //   391: aload_0
    //   392: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   395: invokestatic 1258	com/android/server/DeviceIdleController:lightStateToString	(I)Ljava/lang/String;
    //   398: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   401: aload_0
    //   402: invokevirtual 1620	com/android/server/DeviceIdleController:exitForceIdleLocked	()V
    //   405: lload 7
    //   407: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   410: aload_0
    //   411: monitorexit
    //   412: iconst_m1
    //   413: ireturn
    //   414: aload_0
    //   415: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   418: istore_3
    //   419: goto -58 -> 361
    //   422: aload 10
    //   424: ldc_w 1626
    //   427: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   430: goto -106 -> 324
    //   433: astore_1
    //   434: lload 7
    //   436: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   439: aload_1
    //   440: athrow
    //   441: aload 10
    //   443: new 530	java/lang/StringBuilder
    //   446: dup
    //   447: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   450: ldc_w 1612
    //   453: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   456: aload_1
    //   457: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   460: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   463: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   466: goto -142 -> 324
    //   469: ldc_w 1628
    //   472: aload_2
    //   473: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   476: ifeq +91 -> 567
    //   479: aload_0
    //   480: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   483: ldc_w 1594
    //   486: aconst_null
    //   487: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   490: aload_0
    //   491: monitorenter
    //   492: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   495: lstore 7
    //   497: aload_0
    //   498: iconst_1
    //   499: putfield 1015	com/android/server/DeviceIdleController:mForceIdle	Z
    //   502: aload_0
    //   503: invokevirtual 1363	com/android/server/DeviceIdleController:becomeInactiveIfAppropriateLocked	()V
    //   506: aload 10
    //   508: ldc_w 1630
    //   511: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   514: aload 10
    //   516: aload_0
    //   517: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   520: invokestatic 1258	com/android/server/DeviceIdleController:lightStateToString	(I)Ljava/lang/String;
    //   523: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   526: aload 10
    //   528: ldc_w 1632
    //   531: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   534: aload 10
    //   536: aload_0
    //   537: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   540: invokestatic 1254	com/android/server/DeviceIdleController:stateToString	(I)Ljava/lang/String;
    //   543: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   546: lload 7
    //   548: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   551: goto -466 -> 85
    //   554: astore_1
    //   555: aload_0
    //   556: monitorexit
    //   557: aload_1
    //   558: athrow
    //   559: astore_1
    //   560: lload 7
    //   562: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   565: aload_1
    //   566: athrow
    //   567: ldc_w 1634
    //   570: aload_2
    //   571: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   574: ifeq +86 -> 660
    //   577: aload_0
    //   578: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   581: ldc_w 1594
    //   584: aconst_null
    //   585: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   588: aload_0
    //   589: monitorenter
    //   590: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   593: lstore 7
    //   595: aload_0
    //   596: invokevirtual 1620	com/android/server/DeviceIdleController:exitForceIdleLocked	()V
    //   599: aload 10
    //   601: ldc_w 1630
    //   604: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   607: aload 10
    //   609: aload_0
    //   610: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   613: invokestatic 1258	com/android/server/DeviceIdleController:lightStateToString	(I)Ljava/lang/String;
    //   616: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   619: aload 10
    //   621: ldc_w 1632
    //   624: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   627: aload 10
    //   629: aload_0
    //   630: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   633: invokestatic 1254	com/android/server/DeviceIdleController:stateToString	(I)Ljava/lang/String;
    //   636: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   639: lload 7
    //   641: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   644: goto -559 -> 85
    //   647: astore_1
    //   648: aload_0
    //   649: monitorexit
    //   650: aload_1
    //   651: athrow
    //   652: astore_1
    //   653: lload 7
    //   655: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   658: aload_1
    //   659: athrow
    //   660: ldc_w 1635
    //   663: aload_2
    //   664: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   667: ifeq +225 -> 892
    //   670: aload_0
    //   671: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   674: ldc_w 1594
    //   677: aconst_null
    //   678: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   681: aload_0
    //   682: monitorenter
    //   683: aload_1
    //   684: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   687: astore_1
    //   688: aload_1
    //   689: ifnull +192 -> 881
    //   692: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   695: lstore 7
    //   697: aload_1
    //   698: ldc_w 1608
    //   701: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   704: ifeq +28 -> 732
    //   707: aload 10
    //   709: aload_0
    //   710: getfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   713: invokestatic 1258	com/android/server/DeviceIdleController:lightStateToString	(I)Ljava/lang/String;
    //   716: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   719: lload 7
    //   721: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   724: goto -639 -> 85
    //   727: astore_1
    //   728: aload_0
    //   729: monitorexit
    //   730: aload_1
    //   731: athrow
    //   732: aload_1
    //   733: ldc_w 1602
    //   736: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   739: ifeq +26 -> 765
    //   742: aload 10
    //   744: aload_0
    //   745: getfield 248	com/android/server/DeviceIdleController:mState	I
    //   748: invokestatic 1254	com/android/server/DeviceIdleController:stateToString	(I)Ljava/lang/String;
    //   751: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   754: goto -35 -> 719
    //   757: astore_1
    //   758: lload 7
    //   760: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   763: aload_1
    //   764: athrow
    //   765: aload_1
    //   766: ldc_w 1637
    //   769: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   772: ifeq +15 -> 787
    //   775: aload 10
    //   777: aload_0
    //   778: getfield 1015	com/android/server/DeviceIdleController:mForceIdle	Z
    //   781: invokevirtual 1200	java/io/PrintWriter:println	(Z)V
    //   784: goto -65 -> 719
    //   787: aload_1
    //   788: ldc_w 1639
    //   791: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   794: ifeq +15 -> 809
    //   797: aload 10
    //   799: aload_0
    //   800: getfield 1011	com/android/server/DeviceIdleController:mScreenOn	Z
    //   803: invokevirtual 1200	java/io/PrintWriter:println	(Z)V
    //   806: goto -87 -> 719
    //   809: aload_1
    //   810: ldc_w 1641
    //   813: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   816: ifeq +15 -> 831
    //   819: aload 10
    //   821: aload_0
    //   822: getfield 1013	com/android/server/DeviceIdleController:mCharging	Z
    //   825: invokevirtual 1200	java/io/PrintWriter:println	(Z)V
    //   828: goto -109 -> 719
    //   831: aload_1
    //   832: ldc_w 1643
    //   835: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   838: ifeq +15 -> 853
    //   841: aload 10
    //   843: aload_0
    //   844: getfield 1217	com/android/server/DeviceIdleController:mNetworkConnected	Z
    //   847: invokevirtual 1200	java/io/PrintWriter:println	(Z)V
    //   850: goto -131 -> 719
    //   853: aload 10
    //   855: new 530	java/lang/StringBuilder
    //   858: dup
    //   859: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   862: ldc_w 1645
    //   865: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   868: aload_1
    //   869: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   872: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   875: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   878: goto -159 -> 719
    //   881: aload 10
    //   883: ldc_w 1647
    //   886: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   889: goto -804 -> 85
    //   892: ldc_w 1649
    //   895: aload_2
    //   896: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   899: ifeq +259 -> 1158
    //   902: aload_0
    //   903: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   906: ldc_w 1594
    //   909: aconst_null
    //   910: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   913: aload_0
    //   914: monitorenter
    //   915: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   918: lstore 7
    //   920: aload_1
    //   921: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   924: astore_2
    //   925: iconst_0
    //   926: istore 5
    //   928: iconst_0
    //   929: istore 4
    //   931: aload_2
    //   932: ifnull +26 -> 958
    //   935: ldc_w 1602
    //   938: aload_2
    //   939: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   942: ifne +16 -> 958
    //   945: iload 5
    //   947: istore_3
    //   948: ldc_w 1651
    //   951: aload_2
    //   952: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   955: ifeq +39 -> 994
    //   958: iconst_1
    //   959: istore 6
    //   961: iload 5
    //   963: istore_3
    //   964: iload 6
    //   966: istore 4
    //   968: aload_0
    //   969: getfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   972: ifeq +22 -> 994
    //   975: aload_0
    //   976: iconst_0
    //   977: putfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   980: iconst_1
    //   981: istore_3
    //   982: aload 10
    //   984: ldc_w 1653
    //   987: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   990: iload 6
    //   992: istore 4
    //   994: aload_2
    //   995: ifnull +26 -> 1021
    //   998: ldc_w 1608
    //   1001: aload_2
    //   1002: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1005: ifne +16 -> 1021
    //   1008: iload_3
    //   1009: istore 5
    //   1011: ldc_w 1651
    //   1014: aload_2
    //   1015: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1018: ifeq +40 -> 1058
    //   1021: iconst_1
    //   1022: istore 6
    //   1024: iload_3
    //   1025: istore 5
    //   1027: iload 6
    //   1029: istore 4
    //   1031: aload_0
    //   1032: getfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1035: ifeq +23 -> 1058
    //   1038: aload_0
    //   1039: iconst_0
    //   1040: putfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1043: iconst_1
    //   1044: istore 5
    //   1046: aload 10
    //   1048: ldc_w 1655
    //   1051: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1054: iload 6
    //   1056: istore 4
    //   1058: iload 5
    //   1060: ifeq +42 -> 1102
    //   1063: new 530	java/lang/StringBuilder
    //   1066: dup
    //   1067: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1070: astore 9
    //   1072: aload_2
    //   1073: ifnonnull +72 -> 1145
    //   1076: ldc_w 1651
    //   1079: astore_1
    //   1080: aload_0
    //   1081: aload 9
    //   1083: aload_1
    //   1084: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1087: ldc_w 1657
    //   1090: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1093: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1096: invokestatic 1320	android/os/Process:myUid	()I
    //   1099: invokevirtual 1322	com/android/server/DeviceIdleController:becomeActiveLocked	(Ljava/lang/String;I)V
    //   1102: iload 4
    //   1104: ifne +28 -> 1132
    //   1107: aload 10
    //   1109: new 530	java/lang/StringBuilder
    //   1112: dup
    //   1113: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1116: ldc_w 1612
    //   1119: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1122: aload_2
    //   1123: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1126: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1129: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1132: lload 7
    //   1134: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1137: goto -1052 -> 85
    //   1140: astore_1
    //   1141: aload_0
    //   1142: monitorexit
    //   1143: aload_1
    //   1144: athrow
    //   1145: aload_2
    //   1146: astore_1
    //   1147: goto -67 -> 1080
    //   1150: astore_1
    //   1151: lload 7
    //   1153: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1156: aload_1
    //   1157: athrow
    //   1158: ldc_w 1659
    //   1161: aload_2
    //   1162: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1165: ifeq +219 -> 1384
    //   1168: aload_0
    //   1169: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   1172: ldc_w 1594
    //   1175: aconst_null
    //   1176: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   1179: aload_0
    //   1180: monitorenter
    //   1181: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   1184: lstore 7
    //   1186: aload_1
    //   1187: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   1190: astore_1
    //   1191: iconst_0
    //   1192: istore 5
    //   1194: iconst_0
    //   1195: istore 4
    //   1197: aload_1
    //   1198: ifnull +26 -> 1224
    //   1201: ldc_w 1602
    //   1204: aload_1
    //   1205: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1208: ifne +16 -> 1224
    //   1211: iload 5
    //   1213: istore_3
    //   1214: ldc_w 1651
    //   1217: aload_1
    //   1218: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1221: ifeq +39 -> 1260
    //   1224: iconst_1
    //   1225: istore 6
    //   1227: iload 5
    //   1229: istore_3
    //   1230: iload 6
    //   1232: istore 4
    //   1234: aload_0
    //   1235: getfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   1238: ifne +22 -> 1260
    //   1241: aload_0
    //   1242: iconst_1
    //   1243: putfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   1246: iconst_1
    //   1247: istore_3
    //   1248: aload 10
    //   1250: ldc_w 1661
    //   1253: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1256: iload 6
    //   1258: istore 4
    //   1260: aload_1
    //   1261: ifnull +26 -> 1287
    //   1264: ldc_w 1608
    //   1267: aload_1
    //   1268: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1271: ifne +16 -> 1287
    //   1274: iload_3
    //   1275: istore 5
    //   1277: ldc_w 1651
    //   1280: aload_1
    //   1281: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1284: ifeq +40 -> 1324
    //   1287: iconst_1
    //   1288: istore 6
    //   1290: iload_3
    //   1291: istore 5
    //   1293: iload 6
    //   1295: istore 4
    //   1297: aload_0
    //   1298: getfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1301: ifne +23 -> 1324
    //   1304: aload_0
    //   1305: iconst_1
    //   1306: putfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1309: iconst_1
    //   1310: istore 5
    //   1312: aload 10
    //   1314: ldc_w 1663
    //   1317: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1320: iload 6
    //   1322: istore 4
    //   1324: iload 5
    //   1326: ifeq +7 -> 1333
    //   1329: aload_0
    //   1330: invokevirtual 1363	com/android/server/DeviceIdleController:becomeInactiveIfAppropriateLocked	()V
    //   1333: iload 4
    //   1335: ifne +28 -> 1363
    //   1338: aload 10
    //   1340: new 530	java/lang/StringBuilder
    //   1343: dup
    //   1344: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1347: ldc_w 1612
    //   1350: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1353: aload_1
    //   1354: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1357: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1360: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1363: lload 7
    //   1365: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1368: goto -1283 -> 85
    //   1371: astore_1
    //   1372: aload_0
    //   1373: monitorexit
    //   1374: aload_1
    //   1375: athrow
    //   1376: astore_1
    //   1377: lload 7
    //   1379: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1382: aload_1
    //   1383: athrow
    //   1384: ldc_w 1665
    //   1387: aload_2
    //   1388: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1391: ifeq +168 -> 1559
    //   1394: aload_0
    //   1395: monitorenter
    //   1396: aload_1
    //   1397: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   1400: astore_1
    //   1401: aload_1
    //   1402: ifnull +13 -> 1415
    //   1405: ldc_w 1651
    //   1408: aload_1
    //   1409: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1412: ifeq +43 -> 1455
    //   1415: aload_0
    //   1416: getfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   1419: ifeq +28 -> 1447
    //   1422: aload_0
    //   1423: getfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1426: ifeq +21 -> 1447
    //   1429: ldc_w 1667
    //   1432: astore_1
    //   1433: aload 10
    //   1435: aload_1
    //   1436: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   1439: goto -1354 -> 85
    //   1442: astore_1
    //   1443: aload_0
    //   1444: monitorexit
    //   1445: aload_1
    //   1446: athrow
    //   1447: iconst_0
    //   1448: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1451: astore_1
    //   1452: goto -19 -> 1433
    //   1455: ldc_w 1602
    //   1458: aload_1
    //   1459: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1462: ifeq +31 -> 1493
    //   1465: aload_0
    //   1466: getfield 252	com/android/server/DeviceIdleController:mDeepEnabled	Z
    //   1469: ifeq +16 -> 1485
    //   1472: ldc_w 1667
    //   1475: astore_1
    //   1476: aload 10
    //   1478: aload_1
    //   1479: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   1482: goto -1397 -> 85
    //   1485: iconst_0
    //   1486: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1489: astore_1
    //   1490: goto -14 -> 1476
    //   1493: ldc_w 1608
    //   1496: aload_1
    //   1497: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1500: ifeq +31 -> 1531
    //   1503: aload_0
    //   1504: getfield 263	com/android/server/DeviceIdleController:mLightEnabled	Z
    //   1507: ifeq +16 -> 1523
    //   1510: ldc_w 1667
    //   1513: astore_1
    //   1514: aload 10
    //   1516: aload_1
    //   1517: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   1520: goto -1435 -> 85
    //   1523: iconst_0
    //   1524: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1527: astore_1
    //   1528: goto -14 -> 1514
    //   1531: aload 10
    //   1533: new 530	java/lang/StringBuilder
    //   1536: dup
    //   1537: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1540: ldc_w 1612
    //   1543: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1546: aload_1
    //   1547: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1550: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1553: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1556: goto -1471 -> 85
    //   1559: ldc_w 1669
    //   1562: aload_2
    //   1563: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1566: ifeq +460 -> 2026
    //   1569: aload_1
    //   1570: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   1573: astore_2
    //   1574: aload_2
    //   1575: ifnull +253 -> 1828
    //   1578: aload_0
    //   1579: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   1582: ldc_w 1594
    //   1585: aconst_null
    //   1586: invokevirtual 1597	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   1589: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   1592: lstore 7
    //   1594: aload_2
    //   1595: invokevirtual 1128	java/lang/String:length	()I
    //   1598: iconst_1
    //   1599: if_icmplt +33 -> 1632
    //   1602: aload_2
    //   1603: iconst_0
    //   1604: invokevirtual 1132	java/lang/String:charAt	(I)C
    //   1607: bipush 45
    //   1609: if_icmpeq +55 -> 1664
    //   1612: aload_2
    //   1613: iconst_0
    //   1614: invokevirtual 1132	java/lang/String:charAt	(I)C
    //   1617: bipush 43
    //   1619: if_icmpeq +45 -> 1664
    //   1622: aload_2
    //   1623: iconst_0
    //   1624: invokevirtual 1132	java/lang/String:charAt	(I)C
    //   1627: bipush 61
    //   1629: if_icmpeq +35 -> 1664
    //   1632: aload 10
    //   1634: new 530	java/lang/StringBuilder
    //   1637: dup
    //   1638: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1641: ldc_w 1671
    //   1644: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1647: aload_2
    //   1648: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1651: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1654: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1657: lload 7
    //   1659: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1662: iconst_m1
    //   1663: ireturn
    //   1664: aload_2
    //   1665: iconst_0
    //   1666: invokevirtual 1132	java/lang/String:charAt	(I)C
    //   1669: istore_3
    //   1670: aload_2
    //   1671: iconst_1
    //   1672: invokevirtual 1674	java/lang/String:substring	(I)Ljava/lang/String;
    //   1675: astore_2
    //   1676: iload_3
    //   1677: bipush 43
    //   1679: if_icmpne +94 -> 1773
    //   1682: aload_0
    //   1683: aload_2
    //   1684: invokevirtual 553	com/android/server/DeviceIdleController:addPowerSaveWhitelistAppInternal	(Ljava/lang/String;)Z
    //   1687: ifeq +50 -> 1737
    //   1690: aload 10
    //   1692: new 530	java/lang/StringBuilder
    //   1695: dup
    //   1696: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1699: ldc_w 1676
    //   1702: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1705: aload_2
    //   1706: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1709: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1712: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1715: aload_1
    //   1716: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   1719: astore 9
    //   1721: aload 9
    //   1723: astore_2
    //   1724: aload 9
    //   1726: ifnonnull -132 -> 1594
    //   1729: lload 7
    //   1731: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1734: goto -1647 -> 87
    //   1737: aload 10
    //   1739: new 530	java/lang/StringBuilder
    //   1742: dup
    //   1743: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1746: ldc_w 1678
    //   1749: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1752: aload_2
    //   1753: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1756: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1759: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1762: goto -47 -> 1715
    //   1765: astore_1
    //   1766: lload 7
    //   1768: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   1771: aload_1
    //   1772: athrow
    //   1773: iload_3
    //   1774: bipush 45
    //   1776: if_icmpne +39 -> 1815
    //   1779: aload_0
    //   1780: aload_2
    //   1781: invokevirtual 471	com/android/server/DeviceIdleController:removePowerSaveWhitelistAppInternal	(Ljava/lang/String;)Z
    //   1784: ifeq -69 -> 1715
    //   1787: aload 10
    //   1789: new 530	java/lang/StringBuilder
    //   1792: dup
    //   1793: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   1796: ldc_w 1680
    //   1799: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1802: aload_2
    //   1803: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1806: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1809: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1812: goto -97 -> 1715
    //   1815: aload 10
    //   1817: aload_0
    //   1818: aload_2
    //   1819: invokevirtual 1682	com/android/server/DeviceIdleController:getPowerSaveWhitelistAppInternal	(Ljava/lang/String;)Z
    //   1822: invokevirtual 1200	java/io/PrintWriter:println	(Z)V
    //   1825: goto -110 -> 1715
    //   1828: aload_0
    //   1829: monitorenter
    //   1830: iconst_0
    //   1831: istore_3
    //   1832: iload_3
    //   1833: aload_0
    //   1834: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   1837: invokevirtual 440	android/util/ArrayMap:size	()I
    //   1840: if_icmpge +547 -> 2387
    //   1843: aload 10
    //   1845: ldc_w 1684
    //   1848: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1851: aload 10
    //   1853: aload_0
    //   1854: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   1857: iload_3
    //   1858: invokevirtual 502	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   1861: checkcast 467	java/lang/String
    //   1864: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1867: aload 10
    //   1869: ldc_w 1686
    //   1872: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1875: aload 10
    //   1877: aload_0
    //   1878: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   1881: iload_3
    //   1882: invokevirtual 444	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   1885: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   1888: iload_3
    //   1889: iconst_1
    //   1890: iadd
    //   1891: istore_3
    //   1892: goto -60 -> 1832
    //   1895: iload_3
    //   1896: aload_0
    //   1897: getfield 317	com/android/server/DeviceIdleController:mPowerSaveWhitelistApps	Landroid/util/ArrayMap;
    //   1900: invokevirtual 440	android/util/ArrayMap:size	()I
    //   1903: if_icmpge +489 -> 2392
    //   1906: aload 10
    //   1908: ldc_w 1688
    //   1911: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1914: aload 10
    //   1916: aload_0
    //   1917: getfield 317	com/android/server/DeviceIdleController:mPowerSaveWhitelistApps	Landroid/util/ArrayMap;
    //   1920: iload_3
    //   1921: invokevirtual 502	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   1924: checkcast 467	java/lang/String
    //   1927: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1930: aload 10
    //   1932: ldc_w 1686
    //   1935: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1938: aload 10
    //   1940: aload_0
    //   1941: getfield 317	com/android/server/DeviceIdleController:mPowerSaveWhitelistApps	Landroid/util/ArrayMap;
    //   1944: iload_3
    //   1945: invokevirtual 444	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   1948: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   1951: iload_3
    //   1952: iconst_1
    //   1953: iadd
    //   1954: istore_3
    //   1955: goto -60 -> 1895
    //   1958: iload_3
    //   1959: aload_0
    //   1960: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   1963: invokevirtual 440	android/util/ArrayMap:size	()I
    //   1966: if_icmpge -1881 -> 85
    //   1969: aload 10
    //   1971: ldc_w 1690
    //   1974: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1977: aload 10
    //   1979: aload_0
    //   1980: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   1983: iload_3
    //   1984: invokevirtual 502	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
    //   1987: checkcast 467	java/lang/String
    //   1990: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1993: aload 10
    //   1995: ldc_w 1686
    //   1998: invokevirtual 1161	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   2001: aload 10
    //   2003: aload_0
    //   2004: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   2007: iload_3
    //   2008: invokevirtual 444	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   2011: invokevirtual 1207	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   2014: iload_3
    //   2015: iconst_1
    //   2016: iadd
    //   2017: istore_3
    //   2018: goto -60 -> 1958
    //   2021: astore_1
    //   2022: aload_0
    //   2023: monitorexit
    //   2024: aload_1
    //   2025: athrow
    //   2026: ldc_w 1692
    //   2029: aload_2
    //   2030: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2033: ifeq +118 -> 2151
    //   2036: aload_1
    //   2037: invokevirtual 1695	com/android/server/DeviceIdleController$Shell:getNextOption	()Ljava/lang/String;
    //   2040: astore_2
    //   2041: aload_2
    //   2042: ifnull +43 -> 2085
    //   2045: ldc_w 1120
    //   2048: aload_2
    //   2049: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2052: ifeq -16 -> 2036
    //   2055: aload_1
    //   2056: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   2059: astore_2
    //   2060: aload_2
    //   2061: ifnonnull +13 -> 2074
    //   2064: aload 10
    //   2066: ldc_w 1697
    //   2069: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   2072: iconst_m1
    //   2073: ireturn
    //   2074: aload_1
    //   2075: aload_2
    //   2076: invokestatic 1123	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2079: putfield 1138	com/android/server/DeviceIdleController$Shell:userId	I
    //   2082: goto -46 -> 2036
    //   2085: aload_1
    //   2086: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   2089: astore_2
    //   2090: aload_2
    //   2091: ifnull +50 -> 2141
    //   2094: aload_0
    //   2095: aload_2
    //   2096: ldc2_w 1698
    //   2099: aload_1
    //   2100: getfield 1138	com/android/server/DeviceIdleController$Shell:userId	I
    //   2103: ldc_w 1701
    //   2106: invokevirtual 1703	com/android/server/DeviceIdleController:addPowerSaveTempWhitelistAppChecked	(Ljava/lang/String;JILjava/lang/String;)V
    //   2109: goto -2022 -> 87
    //   2112: astore_1
    //   2113: aload 10
    //   2115: new 530	java/lang/StringBuilder
    //   2118: dup
    //   2119: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   2122: ldc_w 1705
    //   2125: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2128: aload_1
    //   2129: invokevirtual 644	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2132: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2135: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   2138: goto -2051 -> 87
    //   2141: aload_0
    //   2142: aload 10
    //   2144: iconst_0
    //   2145: invokevirtual 1189	com/android/server/DeviceIdleController:dumpTempWhitelistSchedule	(Ljava/io/PrintWriter;Z)V
    //   2148: goto -2061 -> 87
    //   2151: ldc_w 1707
    //   2154: aload_2
    //   2155: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2158: ifeq +223 -> 2381
    //   2161: invokestatic 875	android/os/Binder:clearCallingIdentity	()J
    //   2164: lstore 7
    //   2166: aload_1
    //   2167: invokevirtual 1600	com/android/server/DeviceIdleController$Shell:getNextArg	()Ljava/lang/String;
    //   2170: astore_1
    //   2171: aload_1
    //   2172: ifnull +171 -> 2343
    //   2175: ldc 89
    //   2177: new 530	java/lang/StringBuilder
    //   2180: dup
    //   2181: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   2184: ldc_w 1709
    //   2187: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2190: aload_1
    //   2191: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2194: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2197: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   2200: pop
    //   2201: aload_0
    //   2202: getfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   2205: invokevirtual 1712	com/android/server/DeviceIdleController$Constants:getPolicy	()I
    //   2208: istore 4
    //   2210: aload_1
    //   2211: ldc_w 1714
    //   2214: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2217: ifeq +77 -> 2294
    //   2220: iconst_1
    //   2221: istore_3
    //   2222: aload_0
    //   2223: getfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   2226: iload_3
    //   2227: invokevirtual 1717	com/android/server/DeviceIdleController$Constants:setPolicy	(I)V
    //   2230: aload_0
    //   2231: getfield 277	com/android/server/DeviceIdleController:mLocalPowerManager	Landroid/os/PowerManagerInternal;
    //   2234: ifnull +16 -> 2250
    //   2237: iload_3
    //   2238: iconst_1
    //   2239: if_icmpne +85 -> 2324
    //   2242: aload_0
    //   2243: getfield 277	com/android/server/DeviceIdleController:mLocalPowerManager	Landroid/os/PowerManagerInternal;
    //   2246: iconst_1
    //   2247: invokevirtual 1720	android/os/PowerManagerInternal:setDeviceIdleAggressive	(Z)V
    //   2250: aload 10
    //   2252: new 530	java/lang/StringBuilder
    //   2255: dup
    //   2256: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   2259: ldc_w 1722
    //   2262: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2265: iload 4
    //   2267: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2270: ldc_w 1724
    //   2273: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2276: iload_3
    //   2277: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2280: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2283: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   2286: lload 7
    //   2288: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   2291: goto -2204 -> 87
    //   2294: aload_1
    //   2295: ldc_w 1726
    //   2298: invokevirtual 514	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2301: ifeq +8 -> 2309
    //   2304: iconst_0
    //   2305: istore_3
    //   2306: goto -84 -> 2222
    //   2309: aload 10
    //   2311: ldc_w 1728
    //   2314: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   2317: lload 7
    //   2319: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   2322: iconst_0
    //   2323: ireturn
    //   2324: aload_0
    //   2325: getfield 277	com/android/server/DeviceIdleController:mLocalPowerManager	Landroid/os/PowerManagerInternal;
    //   2328: iconst_0
    //   2329: invokevirtual 1720	android/os/PowerManagerInternal:setDeviceIdleAggressive	(Z)V
    //   2332: goto -82 -> 2250
    //   2335: astore_1
    //   2336: lload 7
    //   2338: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   2341: aload_1
    //   2342: athrow
    //   2343: aload 10
    //   2345: new 530	java/lang/StringBuilder
    //   2348: dup
    //   2349: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   2352: ldc_w 1730
    //   2355: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2358: aload_0
    //   2359: getfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   2362: invokevirtual 1712	com/android/server/DeviceIdleController$Constants:getPolicy	()I
    //   2365: invokevirtual 673	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2368: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2371: invokevirtual 563	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   2374: lload 7
    //   2376: invokestatic 883	android/os/Binder:restoreCallingIdentity	(J)V
    //   2379: iconst_0
    //   2380: ireturn
    //   2381: aload_1
    //   2382: aload_2
    //   2383: invokevirtual 1733	com/android/server/DeviceIdleController$Shell:handleDefaultCommands	(Ljava/lang/String;)I
    //   2386: ireturn
    //   2387: iconst_0
    //   2388: istore_3
    //   2389: goto -494 -> 1895
    //   2392: iconst_0
    //   2393: istore_3
    //   2394: goto -436 -> 1958
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2397	0	this	DeviceIdleController
    //   0	2397	1	paramShell	Shell
    //   0	2397	2	paramString	String
    //   254	2140	3	i	int
    //   929	1337	4	j	int
    //   926	399	5	k	int
    //   959	362	6	m	int
    //   32	2343	7	l	long
    //   1070	655	9	localObject	Object
    //   4	2340	10	localPrintWriter	PrintWriter
    // Exception table:
    //   from	to	target	type
    //   43	53	129	finally
    //   53	80	129	finally
    //   89	126	129	finally
    //   142	167	129	finally
    //   29	39	137	finally
    //   80	85	137	finally
    //   130	137	137	finally
    //   193	203	332	finally
    //   232	237	332	finally
    //   299	304	332	finally
    //   324	329	332	finally
    //   405	410	332	finally
    //   434	441	332	finally
    //   207	217	433	finally
    //   217	232	433	finally
    //   241	255	433	finally
    //   260	299	433	finally
    //   308	313	433	finally
    //   316	324	433	finally
    //   337	361	433	finally
    //   366	405	433	finally
    //   414	419	433	finally
    //   422	430	433	finally
    //   441	466	433	finally
    //   492	497	554	finally
    //   546	551	554	finally
    //   560	567	554	finally
    //   497	546	559	finally
    //   590	595	647	finally
    //   639	644	647	finally
    //   653	660	647	finally
    //   595	639	652	finally
    //   683	688	727	finally
    //   692	697	727	finally
    //   719	724	727	finally
    //   758	765	727	finally
    //   881	889	727	finally
    //   697	719	757	finally
    //   732	754	757	finally
    //   765	784	757	finally
    //   787	806	757	finally
    //   809	828	757	finally
    //   831	850	757	finally
    //   853	878	757	finally
    //   915	925	1140	finally
    //   1132	1137	1140	finally
    //   1151	1158	1140	finally
    //   935	945	1150	finally
    //   948	958	1150	finally
    //   968	980	1150	finally
    //   982	990	1150	finally
    //   998	1008	1150	finally
    //   1011	1021	1150	finally
    //   1031	1043	1150	finally
    //   1046	1054	1150	finally
    //   1063	1072	1150	finally
    //   1080	1102	1150	finally
    //   1107	1132	1150	finally
    //   1181	1191	1371	finally
    //   1363	1368	1371	finally
    //   1377	1384	1371	finally
    //   1201	1211	1376	finally
    //   1214	1224	1376	finally
    //   1234	1246	1376	finally
    //   1248	1256	1376	finally
    //   1264	1274	1376	finally
    //   1277	1287	1376	finally
    //   1297	1309	1376	finally
    //   1312	1320	1376	finally
    //   1329	1333	1376	finally
    //   1338	1363	1376	finally
    //   1396	1401	1442	finally
    //   1405	1415	1442	finally
    //   1415	1429	1442	finally
    //   1433	1439	1442	finally
    //   1447	1452	1442	finally
    //   1455	1472	1442	finally
    //   1476	1482	1442	finally
    //   1485	1490	1442	finally
    //   1493	1510	1442	finally
    //   1514	1520	1442	finally
    //   1523	1528	1442	finally
    //   1531	1556	1442	finally
    //   1594	1632	1765	finally
    //   1632	1657	1765	finally
    //   1664	1676	1765	finally
    //   1682	1715	1765	finally
    //   1715	1721	1765	finally
    //   1737	1762	1765	finally
    //   1779	1812	1765	finally
    //   1815	1825	1765	finally
    //   1832	1888	2021	finally
    //   1895	1951	2021	finally
    //   1958	2014	2021	finally
    //   2094	2109	2112	android/os/RemoteException
    //   2166	2171	2335	finally
    //   2175	2220	2335	finally
    //   2222	2237	2335	finally
    //   2242	2250	2335	finally
    //   2250	2286	2335	finally
    //   2294	2304	2335	finally
    //   2309	2317	2335	finally
    //   2324	2332	2335	finally
    //   2343	2374	2335	finally
  }
  
  /* Error */
  public void onStart()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   4: invokevirtual 500	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   7: astore_3
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: invokespecial 283	com/android/server/DeviceIdleController:updateEnabledStatus	()V
    //   14: aload_3
    //   15: ldc_w 1736
    //   18: invokevirtual 1739	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   21: putstatic 300	com/android/server/DeviceIdleController:isDozeChangeSupport	Z
    //   24: invokestatic 1745	com/android/server/SystemConfig:getInstance	()Lcom/android/server/SystemConfig;
    //   27: astore 4
    //   29: aload 4
    //   31: invokevirtual 1749	com/android/server/SystemConfig:getAllowInPowerSaveExceptIdle	()Landroid/util/ArraySet;
    //   34: astore 5
    //   36: iconst_0
    //   37: istore_1
    //   38: iload_1
    //   39: aload 5
    //   41: invokevirtual 1752	android/util/ArraySet:size	()I
    //   44: if_icmpge +67 -> 111
    //   47: aload 5
    //   49: iload_1
    //   50: invokevirtual 1753	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
    //   53: checkcast 467	java/lang/String
    //   56: astore 6
    //   58: aload_3
    //   59: aload 6
    //   61: ldc_w 1754
    //   64: invokevirtual 520	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   67: astore 6
    //   69: aload 6
    //   71: getfield 734	android/content/pm/ApplicationInfo:uid	I
    //   74: invokestatic 739	android/os/UserHandle:getAppId	(I)I
    //   77: istore_2
    //   78: aload_0
    //   79: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   82: aload 6
    //   84: getfield 731	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   87: iload_2
    //   88: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   91: invokevirtual 746	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   94: pop
    //   95: aload_0
    //   96: getfield 324	com/android/server/DeviceIdleController:mPowerSaveWhitelistSystemAppIdsExceptIdle	Landroid/util/SparseBooleanArray;
    //   99: iload_2
    //   100: iconst_1
    //   101: invokevirtual 453	android/util/SparseBooleanArray:put	(IZ)V
    //   104: iload_1
    //   105: iconst_1
    //   106: iadd
    //   107: istore_1
    //   108: goto -70 -> 38
    //   111: aload 4
    //   113: invokevirtual 1757	com/android/server/SystemConfig:getAllowInPowerSave	()Landroid/util/ArraySet;
    //   116: astore 4
    //   118: iconst_0
    //   119: istore_1
    //   120: iload_1
    //   121: aload 4
    //   123: invokevirtual 1752	android/util/ArraySet:size	()I
    //   126: if_icmpge +93 -> 219
    //   129: aload 4
    //   131: iload_1
    //   132: invokevirtual 1753	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
    //   135: checkcast 467	java/lang/String
    //   138: astore 5
    //   140: aload_3
    //   141: aload 5
    //   143: ldc_w 1754
    //   146: invokevirtual 520	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   149: astore 5
    //   151: aload 5
    //   153: getfield 734	android/content/pm/ApplicationInfo:uid	I
    //   156: invokestatic 739	android/os/UserHandle:getAppId	(I)I
    //   159: istore_2
    //   160: aload_0
    //   161: getfield 315	com/android/server/DeviceIdleController:mPowerSaveWhitelistAppsExceptIdle	Landroid/util/ArrayMap;
    //   164: aload 5
    //   166: getfield 731	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   169: iload_2
    //   170: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: invokevirtual 746	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   176: pop
    //   177: aload_0
    //   178: getfield 324	com/android/server/DeviceIdleController:mPowerSaveWhitelistSystemAppIdsExceptIdle	Landroid/util/SparseBooleanArray;
    //   181: iload_2
    //   182: iconst_1
    //   183: invokevirtual 453	android/util/SparseBooleanArray:put	(IZ)V
    //   186: aload_0
    //   187: getfield 317	com/android/server/DeviceIdleController:mPowerSaveWhitelistApps	Landroid/util/ArrayMap;
    //   190: aload 5
    //   192: getfield 731	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   195: iload_2
    //   196: invokestatic 743	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   199: invokevirtual 746	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   202: pop
    //   203: aload_0
    //   204: getfield 326	com/android/server/DeviceIdleController:mPowerSaveWhitelistSystemAppIds	Landroid/util/SparseBooleanArray;
    //   207: iload_2
    //   208: iconst_1
    //   209: invokevirtual 453	android/util/SparseBooleanArray:put	(IZ)V
    //   212: iload_1
    //   213: iconst_1
    //   214: iadd
    //   215: istore_1
    //   216: goto -96 -> 120
    //   219: aload_0
    //   220: new 29	com/android/server/DeviceIdleController$Constants
    //   223: dup
    //   224: aload_0
    //   225: aload_0
    //   226: getfield 414	com/android/server/DeviceIdleController:mHandler	Lcom/android/server/DeviceIdleController$MyHandler;
    //   229: aload_0
    //   230: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   233: invokevirtual 797	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   236: invokespecial 1760	com/android/server/DeviceIdleController$Constants:<init>	(Lcom/android/server/DeviceIdleController;Landroid/os/Handler;Landroid/content/ContentResolver;)V
    //   239: putfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   242: aload_0
    //   243: invokevirtual 494	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
    //   246: invokevirtual 797	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   249: ldc_w 799
    //   252: invokestatic 1764	android/provider/Settings$System:getUriFor	(Ljava/lang/String;)Landroid/net/Uri;
    //   255: iconst_0
    //   256: aload_0
    //   257: invokespecial 1766	com/android/server/DeviceIdleController:getDozeModeEnabledObserver	()Landroid/database/ContentObserver;
    //   260: invokevirtual 1772	android/content/ContentResolver:registerContentObserver	(Landroid/net/Uri;ZLandroid/database/ContentObserver;)V
    //   263: aload_0
    //   264: invokevirtual 1774	com/android/server/DeviceIdleController:readConfigFileLocked	()V
    //   267: aload_0
    //   268: invokespecial 967	com/android/server/DeviceIdleController:updateWhitelistAppIdsLocked	()V
    //   271: aload_0
    //   272: iconst_1
    //   273: putfield 1217	com/android/server/DeviceIdleController:mNetworkConnected	Z
    //   276: aload_0
    //   277: iconst_1
    //   278: putfield 1011	com/android/server/DeviceIdleController:mScreenOn	Z
    //   281: aload_0
    //   282: iconst_1
    //   283: putfield 1013	com/android/server/DeviceIdleController:mCharging	Z
    //   286: aload_0
    //   287: iconst_0
    //   288: putfield 248	com/android/server/DeviceIdleController:mState	I
    //   291: aload_0
    //   292: iconst_0
    //   293: putfield 269	com/android/server/DeviceIdleController:mLightState	I
    //   296: aload_0
    //   297: aload_0
    //   298: getfield 224	com/android/server/DeviceIdleController:mConstants	Lcom/android/server/DeviceIdleController$Constants;
    //   301: getfield 992	com/android/server/DeviceIdleController$Constants:INACTIVE_TIMEOUT	J
    //   304: putfield 994	com/android/server/DeviceIdleController:mInactiveTimeout	J
    //   307: aload_0
    //   308: monitorexit
    //   309: aload_0
    //   310: new 26	com/android/server/DeviceIdleController$BinderService
    //   313: dup
    //   314: aload_0
    //   315: aconst_null
    //   316: invokespecial 1777	com/android/server/DeviceIdleController$BinderService:<init>	(Lcom/android/server/DeviceIdleController;Lcom/android/server/DeviceIdleController$BinderService;)V
    //   319: putfield 1143	com/android/server/DeviceIdleController:mBinderService	Lcom/android/server/DeviceIdleController$BinderService;
    //   322: aload_0
    //   323: ldc_w 1779
    //   326: aload_0
    //   327: getfield 1143	com/android/server/DeviceIdleController:mBinderService	Lcom/android/server/DeviceIdleController$BinderService;
    //   330: invokevirtual 1783	com/android/server/DeviceIdleController:publishBinderService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   333: aload_0
    //   334: ldc 32
    //   336: new 32	com/android/server/DeviceIdleController$LocalService
    //   339: dup
    //   340: aload_0
    //   341: invokespecial 1784	com/android/server/DeviceIdleController$LocalService:<init>	(Lcom/android/server/DeviceIdleController;)V
    //   344: invokevirtual 1788	com/android/server/DeviceIdleController:publishLocalService	(Ljava/lang/Class;Ljava/lang/Object;)V
    //   347: return
    //   348: astore_3
    //   349: aload_0
    //   350: monitorexit
    //   351: aload_3
    //   352: athrow
    //   353: astore 5
    //   355: goto -143 -> 212
    //   358: astore 6
    //   360: goto -256 -> 104
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	363	0	this	DeviceIdleController
    //   37	179	1	i	int
    //   77	131	2	j	int
    //   7	134	3	localPackageManager	PackageManager
    //   348	4	3	localObject1	Object
    //   27	103	4	localObject2	Object
    //   34	157	5	localObject3	Object
    //   353	1	5	localNameNotFoundException1	PackageManager.NameNotFoundException
    //   56	27	6	localObject4	Object
    //   358	1	6	localNameNotFoundException2	PackageManager.NameNotFoundException
    // Exception table:
    //   from	to	target	type
    //   10	36	348	finally
    //   38	58	348	finally
    //   58	104	348	finally
    //   111	118	348	finally
    //   120	140	348	finally
    //   140	212	348	finally
    //   219	307	348	finally
    //   140	212	353	android/content/pm/PackageManager$NameNotFoundException
    //   58	104	358	android/content/pm/PackageManager$NameNotFoundException
  }
  
  /* Error */
  void readConfigFileLocked()
  {
    // Byte code:
    //   0: ldc 89
    //   2: new 530	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 531	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 1792
    //   12: invokevirtual 537	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: aload_0
    //   16: getfield 397	com/android/server/DeviceIdleController:mConfigFile	Lcom/android/internal/os/AtomicFile;
    //   19: invokevirtual 1795	com/android/internal/os/AtomicFile:getBaseFile	()Ljava/io/File;
    //   22: invokevirtual 644	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   25: invokevirtual 541	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 639	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: aload_0
    //   33: getfield 319	com/android/server/DeviceIdleController:mPowerSaveWhitelistUserApps	Landroid/util/ArrayMap;
    //   36: invokevirtual 1796	android/util/ArrayMap:clear	()V
    //   39: aload_0
    //   40: getfield 397	com/android/server/DeviceIdleController:mConfigFile	Lcom/android/internal/os/AtomicFile;
    //   43: invokevirtual 1800	com/android/internal/os/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   46: astore_1
    //   47: invokestatic 1806	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   50: astore_2
    //   51: aload_2
    //   52: aload_1
    //   53: getstatic 1376	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   56: invokevirtual 1381	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   59: invokeinterface 1810 3 0
    //   64: aload_0
    //   65: aload_2
    //   66: invokespecial 1812	com/android/server/DeviceIdleController:readConfigFileLocked	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   69: aload_1
    //   70: invokevirtual 1815	java/io/FileInputStream:close	()V
    //   73: return
    //   74: astore_1
    //   75: return
    //   76: astore_1
    //   77: return
    //   78: astore_2
    //   79: aload_1
    //   80: invokevirtual 1815	java/io/FileInputStream:close	()V
    //   83: return
    //   84: astore_1
    //   85: return
    //   86: astore_2
    //   87: aload_1
    //   88: invokevirtual 1815	java/io/FileInputStream:close	()V
    //   91: aload_2
    //   92: athrow
    //   93: astore_1
    //   94: goto -3 -> 91
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	DeviceIdleController
    //   46	24	1	localFileInputStream	java.io.FileInputStream
    //   74	1	1	localFileNotFoundException	java.io.FileNotFoundException
    //   76	4	1	localIOException1	IOException
    //   84	4	1	localIOException2	IOException
    //   93	1	1	localIOException3	IOException
    //   50	16	2	localXmlPullParser	XmlPullParser
    //   78	1	2	localXmlPullParserException	XmlPullParserException
    //   86	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   39	47	74	java/io/FileNotFoundException
    //   69	73	76	java/io/IOException
    //   47	69	78	org/xmlpull/v1/XmlPullParserException
    //   79	83	84	java/io/IOException
    //   47	69	86	finally
    //   87	91	93	java/io/IOException
  }
  
  void receivedGenericLocationLocked(Location paramLocation)
  {
    if (this.mState != 4)
    {
      cancelLocatingLocked();
      return;
    }
    Slog.d("DeviceIdleController", "Generic location: " + paramLocation);
    this.mLastGenericLocation = new Location(paramLocation);
    if ((paramLocation.getAccuracy() > this.mConstants.LOCATION_ACCURACY) && (this.mHasGps)) {
      return;
    }
    this.mLocated = true;
    if (this.mNotMoving) {
      stepIdleStateLocked("s:location");
    }
  }
  
  void receivedGpsLocationLocked(Location paramLocation)
  {
    if (this.mState != 4)
    {
      cancelLocatingLocked();
      return;
    }
    Slog.d("DeviceIdleController", "GPS location: " + paramLocation);
    this.mLastGpsLocation = new Location(paramLocation);
    mStopGps = true;
    if ((mDozeChange) && (this.isHasGpsRequest) && (this.mConstants.getPolicy() == 1) && (paramLocation.getAccuracy() > 30.0F))
    {
      this.isFirstReport = false;
      return;
    }
    if ((mDozeChange) && (this.isHasGpsRequest) && (this.mConstants.getPolicy() == 1) && (this.isFirstReport))
    {
      this.isFirstReport = false;
      return;
    }
    if ((!mDozeChange) || (!this.isHasGpsRequest) || (this.mConstants.getPolicy() != 1) || (this.isFirstReport))
    {
      if (paramLocation.getAccuracy() <= this.mConstants.LOCATION_ACCURACY) {}
    }
    else if (checkLoctionWhiteUid()) {
      mStopGps = false;
    }
    this.mLocated = true;
    if (this.mNotMoving) {
      stepIdleStateLocked("s:gps");
    }
  }
  
  boolean registerMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
  {
    try
    {
      this.mMaintenanceActivityListeners.register(paramIMaintenanceActivityListener);
      boolean bool = this.mReportedMaintenanceActivity;
      return bool;
    }
    finally
    {
      paramIMaintenanceActivityListener = finally;
      throw paramIMaintenanceActivityListener;
    }
  }
  
  public boolean removePowerSaveWhitelistAppInternal(String paramString)
  {
    try
    {
      if (this.mPowerSaveWhitelistUserApps.remove(paramString) != null)
      {
        reportPowerSaveWhitelistChangedLocked();
        updateWhitelistAppIdsLocked();
        writeConfigFileLocked();
        return true;
      }
      return false;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  void reportMaintenanceActivityIfNeededLocked()
  {
    boolean bool = this.mJobsActive;
    if (bool == this.mReportedMaintenanceActivity) {
      return;
    }
    this.mReportedMaintenanceActivity = bool;
    Object localObject = this.mHandler;
    if (this.mReportedMaintenanceActivity) {}
    for (int i = 1;; i = 0)
    {
      localObject = ((MyHandler)localObject).obtainMessage(7, i, 0);
      this.mHandler.sendMessage((Message)localObject);
      return;
    }
  }
  
  void resetIdleManagementLocked()
  {
    this.mNextIdlePendingDelay = 0L;
    this.mNextIdleDelay = 0L;
    this.mNextLightIdleDelay = 0L;
    cancelAlarmLocked();
    cancelSensingTimeoutAlarmLocked();
    cancelLocatingLocked();
    stopMonitoringMotionLocked();
    this.mAnyMotionDetector.stop();
  }
  
  void resetLightIdleManagementLocked()
  {
    cancelLightAlarmLocked();
  }
  
  void scheduleAlarmLocked(long paramLong, boolean paramBoolean)
  {
    Slog.d("DeviceIdleController", "scheduleAlarmLocked(" + paramLong + ", " + paramBoolean + ")");
    if (this.mMotionSensor == null) {
      return;
    }
    this.mNextAlarmTime = (SystemClock.elapsedRealtime() + paramLong);
    if (paramBoolean)
    {
      this.mAlarmManager.setIdleUntil(2, this.mNextAlarmTime, "DeviceIdleController.deep", this.mDeepAlarmListener, this.mHandler);
      return;
    }
    this.mAlarmManager.set(2, this.mNextAlarmTime, "DeviceIdleController.deep", this.mDeepAlarmListener, this.mHandler);
  }
  
  void scheduleLightAlarmLocked(long paramLong)
  {
    Slog.d("DeviceIdleController", "scheduleLightAlarmLocked(" + paramLong + ")");
    this.mNextLightAlarmTime = (SystemClock.elapsedRealtime() + paramLong);
    this.mAlarmManager.set(2, this.mNextLightAlarmTime, "DeviceIdleController.light", this.mLightAlarmListener, this.mHandler);
  }
  
  void scheduleReportActiveLocked(String paramString, int paramInt)
  {
    paramString = this.mHandler.obtainMessage(5, paramInt, 0, paramString);
    this.mHandler.sendMessage(paramString);
  }
  
  void scheduleSensingTimeoutAlarmLocked(long paramLong)
  {
    Slog.d("DeviceIdleController", "scheduleSensingAlarmLocked(" + paramLong + ")");
    this.mNextSensingTimeoutAlarmTime = (SystemClock.elapsedRealtime() + paramLong);
    this.mAlarmManager.set(2, this.mNextSensingTimeoutAlarmTime, "DeviceIdleController.sensing", this.mSensingTimeoutAlarmListener, this.mHandler);
  }
  
  void setAlarmsActive(boolean paramBoolean)
  {
    try
    {
      this.mAlarmsActive = paramBoolean;
      if (!paramBoolean) {
        exitMaintenanceEarlyIfNeededLocked();
      }
      return;
    }
    finally {}
  }
  
  void setJobsActive(boolean paramBoolean)
  {
    try
    {
      this.mJobsActive = paramBoolean;
      reportMaintenanceActivityIfNeededLocked();
      if (!paramBoolean) {
        exitMaintenanceEarlyIfNeededLocked();
      }
      return;
    }
    finally {}
  }
  
  public void setNetworkPolicyTempWhitelistCallbackInternal(Runnable paramRunnable)
  {
    try
    {
      this.mNetworkPolicyTempWhitelistCallback = paramRunnable;
      return;
    }
    finally
    {
      paramRunnable = finally;
      throw paramRunnable;
    }
  }
  
  void startMonitoringMotionLocked()
  {
    Slog.d("DeviceIdleController", "startMonitoringMotionLocked()");
    if ((this.mMotionSensor == null) || (this.mMotionListener.active)) {
      return;
    }
    this.mMotionListener.registerLocked();
  }
  
  void stepIdleStateLocked(String paramString)
  {
    Slog.d("DeviceIdleController", "stepIdleStateLocked: mState=" + this.mState);
    EventLogTags.writeDeviceIdleStep();
    long l = SystemClock.elapsedRealtime();
    if (this.mConstants.MIN_TIME_TO_ALARM + l > this.mAlarmManager.getNextWakeFromIdleTime())
    {
      if (this.mState != 0)
      {
        becomeActiveLocked("alarm", Process.myUid());
        becomeInactiveIfAppropriateLocked();
      }
      return;
    }
    switch (this.mState)
    {
    default: 
      return;
    case 1: 
      if (!mDozeChange) {
        startMonitoringMotionLocked();
      }
      scheduleAlarmLocked(this.mConstants.IDLE_AFTER_INACTIVE_TIMEOUT, false);
      this.mNextIdlePendingDelay = this.mConstants.IDLE_PENDING_TIMEOUT;
      this.mNextIdleDelay = this.mConstants.IDLE_TIMEOUT;
      this.mState = 2;
      Slog.d("DeviceIdleController", "Moved from STATE_INACTIVE to STATE_IDLE_PENDING.");
      EventLogTags.writeDeviceIdle(this.mState, paramString);
      return;
    case 2: 
      this.mState = 3;
      Slog.d("DeviceIdleController", "Moved from STATE_IDLE_PENDING to STATE_SENSING.");
      EventLogTags.writeDeviceIdle(this.mState, paramString);
      scheduleSensingTimeoutAlarmLocked(this.mConstants.SENSING_TIMEOUT);
      cancelLocatingLocked();
      this.mNotMoving = false;
      this.mLocated = false;
      this.mLastGenericLocation = null;
      this.mLastGpsLocation = null;
      this.mAnyMotionDetector.checkForAnyMotion();
      return;
    case 3: 
      cancelSensingTimeoutAlarmLocked();
      this.mState = 4;
      Slog.d("DeviceIdleController", "Moved from STATE_SENSING to STATE_LOCATING.");
      EventLogTags.writeDeviceIdle(this.mState, paramString);
      if (mDozeChange) {
        this.isHasGpsRequest = isHasGpsReport();
      }
      Slog.d("DeviceIdleController", "isHasGpsRequest =" + this.isHasGpsRequest);
      if (mDozeChange) {
        if (this.isHasGpsRequest)
        {
          scheduleAlarmLocked(120000L, false);
          if ((!mDozeChange) || (!this.isHasGpsRequest) || (this.mLocationManager.getProvider("gps") == null) || (!this.mLocationManager.isProviderEnabled("gps"))) {
            break label654;
          }
          this.mHasGps = true;
          this.isFirstReport = true;
          this.mLocationManager.requestLocationUpdates("gps", 1000L, 10.0F, this.mGpsLocationListener, this.mHandler.getLooper());
          this.mLocating = true;
        }
      }
    case 4: 
    case 6: 
      label343:
      while (!this.mLocating)
      {
        cancelAlarmLocked();
        cancelLocatingLocked();
        this.mAnyMotionDetector.stop();
        scheduleAlarmLocked(this.mNextIdleDelay, true);
        Slog.d("DeviceIdleController", "Moved to STATE_IDLE. Next alarm in " + this.mNextIdleDelay + " ms.");
        this.mNextIdleDelay = (((float)this.mNextIdleDelay * this.mConstants.IDLE_FACTOR));
        Slog.d("DeviceIdleController", "Setting mNextIdleDelay = " + this.mNextIdleDelay);
        this.mNextIdleDelay = Math.min(this.mNextIdleDelay, this.mConstants.MAX_IDLE_TIMEOUT);
        if (this.mNextIdleDelay < this.mConstants.IDLE_TIMEOUT) {
          this.mNextIdleDelay = this.mConstants.IDLE_TIMEOUT;
        }
        this.mState = 5;
        if (this.mLightState != 7)
        {
          this.mLightState = 7;
          cancelLightAlarmLocked();
        }
        EventLogTags.writeDeviceIdle(this.mState, paramString);
        addEvent(4);
        this.mHandler.sendEmptyMessage(2);
        return;
        scheduleAlarmLocked(10000L, false);
        break label343;
        scheduleAlarmLocked(this.mConstants.LOCATING_TIMEOUT, false);
        break label343;
        label654:
        if ((this.mLocationManager != null) && (this.mLocationManager.getProvider("network") != null))
        {
          this.mLocationManager.requestLocationUpdates(this.mLocationRequest, this.mGenericLocationListener, this.mHandler.getLooper());
          this.mLocating = true;
        }
        for (;;)
        {
          if ((this.mLocationManager == null) || (this.mLocationManager.getProvider("gps") == null)) {
            break label769;
          }
          this.mHasGps = true;
          this.mLocationManager.requestLocationUpdates("gps", 1000L, 5.0F, this.mGpsLocationListener, this.mHandler.getLooper());
          this.mLocating = true;
          break;
          this.mHasNetworkLocation = false;
        }
        label769:
        this.mHasGps = false;
      }
    }
    this.mActiveIdleOpCount = 1;
    this.mActiveIdleWakeLock.acquire();
    scheduleAlarmLocked(this.mNextIdlePendingDelay, false);
    Slog.d("DeviceIdleController", "Moved from STATE_IDLE to STATE_IDLE_MAINTENANCE. Next alarm in " + this.mNextIdlePendingDelay + " ms.");
    this.mMaintenanceStartTime = SystemClock.elapsedRealtime();
    this.mNextIdlePendingDelay = Math.min(this.mConstants.MAX_IDLE_PENDING_TIMEOUT, ((float)this.mNextIdlePendingDelay * this.mConstants.IDLE_PENDING_FACTOR));
    if (this.mNextIdlePendingDelay < this.mConstants.IDLE_PENDING_TIMEOUT) {
      this.mNextIdlePendingDelay = this.mConstants.IDLE_PENDING_TIMEOUT;
    }
    this.mState = 6;
    EventLogTags.writeDeviceIdle(this.mState, paramString);
    addEvent(5);
    this.mHandler.sendEmptyMessage(4);
  }
  
  void stepLightIdleStateLocked(String paramString)
  {
    if (this.mLightState == 7) {
      return;
    }
    Slog.d("DeviceIdleController", "stepLightIdleStateLocked: mLightState=" + this.mLightState);
    EventLogTags.writeDeviceIdleLightStep();
    switch (this.mLightState)
    {
    case 2: 
    default: 
      return;
    case 1: 
      this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
      this.mNextLightIdleDelay = this.mConstants.LIGHT_IDLE_TIMEOUT;
      this.mMaintenanceStartTime = 0L;
      if (!isOpsInactiveLocked())
      {
        this.mLightState = 3;
        EventLogTags.writeDeviceIdleLight(this.mLightState, paramString);
        scheduleLightAlarmLocked(this.mConstants.LIGHT_PRE_IDLE_TIMEOUT);
        return;
      }
    case 3: 
    case 6: 
      long l;
      if (this.mMaintenanceStartTime != 0L)
      {
        l = SystemClock.elapsedRealtime() - this.mMaintenanceStartTime;
        if (l >= this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET) {
          break label296;
        }
      }
      label296:
      for (this.mCurIdleBudget += this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET - l;; this.mCurIdleBudget -= l - this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET)
      {
        this.mMaintenanceStartTime = 0L;
        scheduleLightAlarmLocked(this.mNextLightIdleDelay);
        this.mNextLightIdleDelay = Math.min(this.mConstants.LIGHT_MAX_IDLE_TIMEOUT, ((float)this.mNextLightIdleDelay * this.mConstants.LIGHT_IDLE_FACTOR));
        if (this.mNextLightIdleDelay < this.mConstants.LIGHT_IDLE_TIMEOUT) {
          this.mNextLightIdleDelay = this.mConstants.LIGHT_IDLE_TIMEOUT;
        }
        Slog.d("DeviceIdleController", "Moved to LIGHT_STATE_IDLE.");
        this.mLightState = 4;
        EventLogTags.writeDeviceIdleLight(this.mLightState, paramString);
        addEvent(2);
        this.mHandler.sendEmptyMessage(3);
        return;
      }
    }
    if ((this.mNetworkConnected) || (this.mLightState == 5))
    {
      this.mActiveIdleOpCount = 1;
      this.mActiveIdleWakeLock.acquire();
      this.mMaintenanceStartTime = SystemClock.elapsedRealtime();
      if (this.mCurIdleBudget < this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET) {
        this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
      }
      for (;;)
      {
        scheduleLightAlarmLocked(this.mCurIdleBudget);
        Slog.d("DeviceIdleController", "Moved from LIGHT_STATE_IDLE to LIGHT_STATE_IDLE_MAINTENANCE.");
        this.mLightState = 6;
        EventLogTags.writeDeviceIdleLight(this.mLightState, paramString);
        addEvent(3);
        this.mHandler.sendEmptyMessage(4);
        return;
        if (this.mCurIdleBudget > this.mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET) {
          this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET;
        }
      }
    }
    scheduleLightAlarmLocked(this.mNextLightIdleDelay);
    Slog.d("DeviceIdleController", "Moved to LIGHT_WAITING_FOR_NETWORK.");
    this.mLightState = 5;
    EventLogTags.writeDeviceIdleLight(this.mLightState, paramString);
  }
  
  void stopMonitoringMotionLocked()
  {
    Slog.d("DeviceIdleController", "stopMonitoringMotionLocked()");
    if ((this.mMotionSensor != null) && (this.mMotionListener.active)) {
      this.mMotionListener.unregisterLocked();
    }
  }
  
  void unregisterMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
  {
    try
    {
      this.mMaintenanceActivityListeners.unregister(paramIMaintenanceActivityListener);
      return;
    }
    finally
    {
      paramIMaintenanceActivityListener = finally;
      throw paramIMaintenanceActivityListener;
    }
  }
  
  void updateChargingLocked(boolean paramBoolean)
  {
    Slog.i("DeviceIdleController", "updateChargingLocked: charging=" + paramBoolean);
    if ((!paramBoolean) && (this.mCharging))
    {
      this.mCharging = false;
      if (!this.mForceIdle) {
        becomeInactiveIfAppropriateLocked();
      }
    }
    do
    {
      do
      {
        return;
      } while (!paramBoolean);
      this.mCharging = paramBoolean;
    } while (this.mForceIdle);
    becomeActiveLocked("charging", Process.myUid());
  }
  
  void updateConnectivityState(Intent paramIntent)
  {
    try
    {
      localObject = this.mConnectivityService;
      if (localObject == null) {
        return;
      }
    }
    finally {}
    Object localObject = ((ConnectivityService)localObject).getActiveNetworkInfo();
    boolean bool;
    if (localObject == null) {
      bool = false;
    }
    for (;;)
    {
      try
      {
        if (bool != this.mNetworkConnected)
        {
          this.mNetworkConnected = bool;
          if ((bool) && (this.mLightState == 5)) {
            stepLightIdleStateLocked("network");
          }
        }
        return;
      }
      finally {}
      if (paramIntent == null)
      {
        bool = ((NetworkInfo)localObject).isConnected();
      }
      else
      {
        int i = paramIntent.getIntExtra("networkType", -1);
        int j = ((NetworkInfo)localObject).getType();
        if (j != i) {
          return;
        }
        bool = paramIntent.getBooleanExtra("noConnectivity", false);
        if (bool) {
          bool = false;
        } else {
          bool = true;
        }
      }
    }
  }
  
  void updateDisplayLocked()
  {
    this.mCurDisplay = this.mDisplayManager.getDisplay(0);
    boolean bool;
    if (this.mCurDisplay.getState() == 2)
    {
      bool = true;
      Slog.d("DeviceIdleController", "updateDisplayLocked: screenOn=" + bool);
      if ((bool) || (!this.mScreenOn)) {
        break label88;
      }
      this.mScreenOn = false;
      if (!this.mForceIdle) {
        becomeInactiveIfAppropriateLocked();
      }
    }
    for (;;)
    {
      chearWhiteUid();
      return;
      bool = false;
      break;
      label88:
      if (bool)
      {
        this.mScreenOn = true;
        if (!this.mForceIdle) {
          becomeActiveLocked("screen", Process.myUid());
        }
      }
    }
  }
  
  void writeConfigFileLocked()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessageDelayed(1, 5000L);
  }
  
  void writeConfigFileLocked(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startDocument(null, Boolean.valueOf(true));
    paramXmlSerializer.startTag(null, "config");
    int i = 0;
    while (i < this.mPowerSaveWhitelistUserApps.size())
    {
      String str = (String)this.mPowerSaveWhitelistUserApps.keyAt(i);
      paramXmlSerializer.startTag(null, "wl");
      paramXmlSerializer.attribute(null, "n", str);
      paramXmlSerializer.endTag(null, "wl");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "config");
    paramXmlSerializer.endDocument();
  }
  
  private final class BinderService
    extends IDeviceIdleController.Stub
  {
    private BinderService() {}
    
    public void addPowerSaveTempWhitelistApp(String paramString1, long paramLong, int paramInt, String paramString2)
      throws RemoteException
    {
      DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(paramString1, paramLong, paramInt, paramString2);
    }
    
    public long addPowerSaveTempWhitelistAppForMms(String paramString1, int paramInt, String paramString2)
      throws RemoteException
    {
      long l = DeviceIdleController.-get1(DeviceIdleController.this).MMS_TEMP_APP_WHITELIST_DURATION;
      DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(paramString1, l, paramInt, paramString2);
      return l;
    }
    
    public long addPowerSaveTempWhitelistAppForSms(String paramString1, int paramInt, String paramString2)
      throws RemoteException
    {
      long l = DeviceIdleController.-get1(DeviceIdleController.this).SMS_TEMP_APP_WHITELIST_DURATION;
      DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(paramString1, l, paramInt, paramString2);
      return l;
    }
    
    public void addPowerSaveWhitelistApp(String paramString)
    {
      DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        DeviceIdleController.this.addPowerSaveWhitelistAppInternal(paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      DeviceIdleController.this.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void exitIdle(String paramString)
    {
      DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        DeviceIdleController.this.exitIdleInternal(paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int[] getAppIdTempWhitelist()
    {
      return DeviceIdleController.this.getAppIdTempWhitelistInternal();
    }
    
    public int[] getAppIdUserWhitelist()
    {
      return DeviceIdleController.this.getAppIdUserWhitelistInternal();
    }
    
    public int[] getAppIdWhitelist()
    {
      return DeviceIdleController.this.getAppIdWhitelistInternal();
    }
    
    public int[] getAppIdWhitelistExceptIdle()
    {
      return DeviceIdleController.this.getAppIdWhitelistExceptIdleInternal();
    }
    
    public String[] getFullPowerWhitelist()
    {
      return DeviceIdleController.this.getFullPowerWhitelistInternal();
    }
    
    public String[] getFullPowerWhitelistExceptIdle()
    {
      return DeviceIdleController.this.getFullPowerWhitelistExceptIdleInternal();
    }
    
    public int getIdleStateDetailed()
    {
      DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      return DeviceIdleController.-get15(DeviceIdleController.this);
    }
    
    public int getLightIdleStateDetailed()
    {
      DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      return DeviceIdleController.-get7(DeviceIdleController.this);
    }
    
    public String[] getSystemPowerWhitelist()
    {
      return DeviceIdleController.this.getSystemPowerWhitelistInternal();
    }
    
    public String[] getSystemPowerWhitelistExceptIdle()
    {
      return DeviceIdleController.this.getSystemPowerWhitelistExceptIdleInternal();
    }
    
    public String[] getUserPowerWhitelist()
    {
      return DeviceIdleController.this.getUserPowerWhitelistInternal();
    }
    
    public boolean isPowerSaveWhitelistApp(String paramString)
    {
      return DeviceIdleController.this.isPowerSaveWhitelistAppInternal(paramString);
    }
    
    public boolean isPowerSaveWhitelistExceptIdleApp(String paramString)
    {
      return DeviceIdleController.this.isPowerSaveWhitelistExceptIdleAppInternal(paramString);
    }
    
    public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    {
      new DeviceIdleController.Shell(DeviceIdleController.this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
    }
    
    public boolean registerMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
    {
      return DeviceIdleController.this.registerMaintenanceActivityListener(paramIMaintenanceActivityListener);
    }
    
    public void removePowerSaveWhitelistApp(String paramString)
    {
      DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
      long l = Binder.clearCallingIdentity();
      try
      {
        DeviceIdleController.this.removePowerSaveWhitelistAppInternal(paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void unregisterMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
    {
      DeviceIdleController.this.unregisterMaintenanceActivityListener(paramIMaintenanceActivityListener);
    }
  }
  
  private final class Constants
    extends ContentObserver
  {
    public static final int AGGRESSIVE_POLICY = 1;
    public static final int DEFAULT_POLICY = 0;
    private static final String KEY_IDLE_AFTER_INACTIVE_TIMEOUT = "idle_after_inactive_to";
    private static final String KEY_IDLE_FACTOR = "idle_factor";
    private static final String KEY_IDLE_PENDING_FACTOR = "idle_pending_factor";
    private static final String KEY_IDLE_PENDING_TIMEOUT = "idle_pending_to";
    private static final String KEY_IDLE_TIMEOUT = "idle_to";
    private static final String KEY_INACTIVE_TIMEOUT = "inactive_to";
    private static final String KEY_LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT = "light_after_inactive_to";
    private static final String KEY_LIGHT_IDLE_FACTOR = "light_idle_factor";
    private static final String KEY_LIGHT_IDLE_MAINTENANCE_MAX_BUDGET = "light_idle_maintenance_max_budget";
    private static final String KEY_LIGHT_IDLE_MAINTENANCE_MIN_BUDGET = "light_idle_maintenance_min_budget";
    private static final String KEY_LIGHT_IDLE_TIMEOUT = "light_idle_to";
    private static final String KEY_LIGHT_MAX_IDLE_TIMEOUT = "light_max_idle_to";
    private static final String KEY_LIGHT_PRE_IDLE_TIMEOUT = "light_pre_idle_to";
    private static final String KEY_LOCATING_TIMEOUT = "locating_to";
    private static final String KEY_LOCATION_ACCURACY = "location_accuracy";
    private static final String KEY_MAX_IDLE_PENDING_TIMEOUT = "max_idle_pending_to";
    private static final String KEY_MAX_IDLE_TIMEOUT = "max_idle_to";
    private static final String KEY_MAX_TEMP_APP_WHITELIST_DURATION = "max_temp_app_whitelist_duration";
    private static final String KEY_MIN_DEEP_MAINTENANCE_TIME = "min_deep_maintenance_time";
    private static final String KEY_MIN_LIGHT_MAINTENANCE_TIME = "min_light_maintenance_time";
    private static final String KEY_MIN_TIME_TO_ALARM = "min_time_to_alarm";
    private static final String KEY_MMS_TEMP_APP_WHITELIST_DURATION = "mms_temp_app_whitelist_duration";
    private static final String KEY_MOTION_INACTIVE_TIMEOUT = "motion_inactive_to";
    private static final String KEY_NOTIFICATION_WHITELIST_DURATION = "notification_whitelist_duration";
    private static final String KEY_SENSING_TIMEOUT = "sensing_to";
    private static final String KEY_SMS_TEMP_APP_WHITELIST_DURATION = "sms_temp_app_whitelist_duration";
    public static final int MAX_POLICY = 2;
    public long IDLE_AFTER_INACTIVE_TIMEOUT;
    public float IDLE_FACTOR;
    public float IDLE_PENDING_FACTOR;
    public long IDLE_PENDING_TIMEOUT;
    public long IDLE_TIMEOUT;
    public long INACTIVE_TIMEOUT;
    public long LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT;
    public float LIGHT_IDLE_FACTOR;
    public long LIGHT_IDLE_MAINTENANCE_MAX_BUDGET;
    public long LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
    public long LIGHT_IDLE_TIMEOUT;
    public long LIGHT_MAX_IDLE_TIMEOUT;
    public long LIGHT_PRE_IDLE_TIMEOUT;
    public long LOCATING_TIMEOUT;
    public float LOCATION_ACCURACY;
    public long MAX_IDLE_PENDING_TIMEOUT;
    public long MAX_IDLE_TIMEOUT;
    public long MAX_TEMP_APP_WHITELIST_DURATION;
    public long MIN_DEEP_MAINTENANCE_TIME;
    public long MIN_LIGHT_MAINTENANCE_TIME;
    public long MIN_TIME_TO_ALARM;
    public long MMS_TEMP_APP_WHITELIST_DURATION;
    public long MOTION_INACTIVE_TIMEOUT;
    public long NOTIFICATION_WHITELIST_DURATION;
    public long SENSING_TIMEOUT;
    public long SMS_TEMP_APP_WHITELIST_DURATION;
    private final boolean mHasWatch;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private int mPolicy = 0;
    private final ContentResolver mResolver;
    
    public Constants(Handler paramHandler, ContentResolver paramContentResolver)
    {
      super();
      this.mResolver = paramContentResolver;
      this.mHasWatch = DeviceIdleController.this.getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch");
      paramHandler = this.mResolver;
      if (this.mHasWatch) {}
      for (this$1 = "device_idle_constants_watch";; this$1 = "device_idle_constants")
      {
        paramHandler.registerContentObserver(Settings.Global.getUriFor(DeviceIdleController.this), false, this);
        this.mResolver.registerContentObserver(Settings.System.getUriFor("doze_mode_policy"), false, this);
        this.mPolicy = Settings.System.getIntForUser(this.mResolver, "doze_mode_policy", 1, 0);
        updateConstants();
        return;
      }
    }
    
    private void updateConstants()
    {
      for (;;)
      {
        synchronized (DeviceIdleController.this)
        {
          try
          {
            KeyValueListParser localKeyValueListParser = this.mParser;
            ContentResolver localContentResolver = this.mResolver;
            if (!this.mHasWatch) {
              continue;
            }
            str = "device_idle_constants_watch";
            localKeyValueListParser.setString(Settings.Global.getString(localContentResolver, str));
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            String str;
            long l;
            Slog.e("DeviceIdleController", "Bad device idle settings", localIllegalArgumentException);
            continue;
          }
          this.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT = this.mParser.getLong("light_after_inactive_to", 300000L);
          this.LIGHT_PRE_IDLE_TIMEOUT = this.mParser.getLong("light_pre_idle_to", 600000L);
          this.LIGHT_IDLE_TIMEOUT = this.mParser.getLong("light_idle_to", 300000L);
          this.LIGHT_IDLE_FACTOR = this.mParser.getFloat("light_idle_factor", 2.0F);
          this.LIGHT_MAX_IDLE_TIMEOUT = this.mParser.getLong("light_max_idle_to", 900000L);
          this.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET = this.mParser.getLong("light_idle_maintenance_min_budget", 60000L);
          this.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET = this.mParser.getLong("light_idle_maintenance_max_budget", 300000L);
          this.MIN_LIGHT_MAINTENANCE_TIME = this.mParser.getLong("min_light_maintenance_time", 5000L);
          this.MIN_DEEP_MAINTENANCE_TIME = this.mParser.getLong("min_deep_maintenance_time", 30000L);
          if (this.mHasWatch)
          {
            i = 15;
            l = i * 60;
            this.INACTIVE_TIMEOUT = this.mParser.getLong("inactive_to", l * 1000L);
            this.SENSING_TIMEOUT = this.mParser.getLong("sensing_to", 240000L);
            this.LOCATING_TIMEOUT = this.mParser.getLong("locating_to", 30000L);
            this.LOCATION_ACCURACY = this.mParser.getFloat("location_accuracy", 20.0F);
            this.MOTION_INACTIVE_TIMEOUT = this.mParser.getLong("motion_inactive_to", 600000L);
            if (!this.mHasWatch) {
              break label757;
            }
            i = 15;
            l = i * 60;
            this.IDLE_AFTER_INACTIVE_TIMEOUT = this.mParser.getLong("idle_after_inactive_to", l * 1000L);
            this.IDLE_PENDING_TIMEOUT = this.mParser.getLong("idle_pending_to", 300000L);
            this.MAX_IDLE_PENDING_TIMEOUT = this.mParser.getLong("max_idle_pending_to", 600000L);
            this.IDLE_PENDING_FACTOR = this.mParser.getFloat("idle_pending_factor", 2.0F);
            this.IDLE_TIMEOUT = this.mParser.getLong("idle_to", 3600000L);
            this.MAX_IDLE_TIMEOUT = this.mParser.getLong("max_idle_to", 21600000L);
            this.IDLE_FACTOR = this.mParser.getFloat("idle_factor", 2.0F);
            this.MIN_TIME_TO_ALARM = this.mParser.getLong("min_time_to_alarm", 3600000L);
            this.MAX_TEMP_APP_WHITELIST_DURATION = this.mParser.getLong("max_temp_app_whitelist_duration", 300000L);
            this.MMS_TEMP_APP_WHITELIST_DURATION = this.mParser.getLong("mms_temp_app_whitelist_duration", 60000L);
            this.SMS_TEMP_APP_WHITELIST_DURATION = this.mParser.getLong("sms_temp_app_whitelist_duration", 20000L);
            this.NOTIFICATION_WHITELIST_DURATION = this.mParser.getLong("notification_whitelist_duration", 30000L);
            Slog.d("DeviceIdleController", "isDozeChangeSupport = " + DeviceIdleController.isDozeChangeSupport + " mPolicy=" + this.mPolicy);
            if (DeviceIdleController.isDozeChangeSupport)
            {
              if (this.mPolicy != 1) {
                break label763;
              }
              if (DeviceIdleController.-get9(DeviceIdleController.this) != null) {
                DeviceIdleController.-get9(DeviceIdleController.this).setDeviceIdleAggressive(true);
              }
              DeviceIdleController.mDozeChange = true;
              this.LOCATION_ACCURACY = this.mParser.getFloat("location_accuracy", 10.0F);
              this.IDLE_AFTER_INACTIVE_TIMEOUT = this.mParser.getLong("idle_after_inactive_to", 420000L);
              this.INACTIVE_TIMEOUT = this.mParser.getLong("inactive_to", 960000L);
              this.MAX_IDLE_PENDING_TIMEOUT = this.mParser.getLong("max_idle_pending_to", 120000L);
              this.IDLE_PENDING_TIMEOUT = this.mParser.getLong("idle_pending_to", 60000L);
              this.MOTION_INACTIVE_TIMEOUT = this.mParser.getLong("motion_inactive_to", 60000L);
              this.IDLE_FACTOR = this.mParser.getFloat("idle_factor", 3.0F);
              this.MIN_TIME_TO_ALARM = this.mParser.getLong("min_time_to_alarm", 60000L);
              this.MAX_IDLE_TIMEOUT = this.mParser.getLong("max_idle_to", 86400000L);
            }
            return;
            str = "device_idle_constants";
          }
        }
        int i = 30;
        continue;
        label757:
        i = 30;
        continue;
        label763:
        if (DeviceIdleController.-get9(DeviceIdleController.this) != null) {
          DeviceIdleController.-get9(DeviceIdleController.this).setDeviceIdleAggressive(false);
        }
        DeviceIdleController.mDozeChange = false;
      }
    }
    
    void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("  Settings:");
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_after_inactive_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_pre_idle_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_PRE_IDLE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_idle_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_IDLE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_idle_factor");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.LIGHT_IDLE_FACTOR);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_max_idle_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_MAX_IDLE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_idle_maintenance_min_budget");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("light_idle_maintenance_max_budget");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_light_maintenance_time");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MIN_LIGHT_MAINTENANCE_TIME, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_deep_maintenance_time");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MIN_DEEP_MAINTENANCE_TIME, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("inactive_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.INACTIVE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("sensing_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.SENSING_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("locating_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LOCATING_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("location_accuracy");
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.LOCATION_ACCURACY);
      paramPrintWriter.print("m");
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("motion_inactive_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MOTION_INACTIVE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("idle_after_inactive_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.IDLE_AFTER_INACTIVE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("idle_pending_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.IDLE_PENDING_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("max_idle_pending_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MAX_IDLE_PENDING_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("idle_pending_factor");
      paramPrintWriter.print("=");
      paramPrintWriter.println(this.IDLE_PENDING_FACTOR);
      paramPrintWriter.print("    ");
      paramPrintWriter.print("idle_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.IDLE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("max_idle_to");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MAX_IDLE_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("idle_factor");
      paramPrintWriter.print("=");
      paramPrintWriter.println(this.IDLE_FACTOR);
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_time_to_alarm");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MIN_TIME_TO_ALARM, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("max_temp_app_whitelist_duration");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MAX_TEMP_APP_WHITELIST_DURATION, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("mms_temp_app_whitelist_duration");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MMS_TEMP_APP_WHITELIST_DURATION, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("sms_temp_app_whitelist_duration");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.SMS_TEMP_APP_WHITELIST_DURATION, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("notification_whitelist_duration");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.NOTIFICATION_WHITELIST_DURATION, paramPrintWriter);
      paramPrintWriter.println();
    }
    
    public int getPolicy()
    {
      return this.mPolicy;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (Settings.System.getUriFor("doze_mode_policy").equals(paramUri))
      {
        this.mPolicy = Settings.System.getIntForUser(this.mResolver, "doze_mode_policy", 0, 0);
        Slog.d("DeviceIdleController", "doze policy is set to " + this.mPolicy);
      }
      updateConstants();
    }
    
    public void setPolicy(int paramInt)
    {
      this.mPolicy = paramInt;
      updateConstants();
    }
  }
  
  public final class LocalService
  {
    public LocalService() {}
    
    public void addPowerSaveTempWhitelistAppDirect(int paramInt, long paramLong, boolean paramBoolean, String paramString)
    {
      DeviceIdleController.this.addPowerSaveTempWhitelistAppDirectInternal(0, paramInt, paramLong, paramBoolean, paramString);
    }
    
    public long getNotificationWhitelistDuration()
    {
      return DeviceIdleController.-get1(DeviceIdleController.this).NOTIFICATION_WHITELIST_DURATION;
    }
    
    public int[] getPowerSaveWhitelistUserAppIds()
    {
      return DeviceIdleController.this.getPowerSaveWhitelistUserAppIds();
    }
    
    public void setAlarmsActive(boolean paramBoolean)
    {
      DeviceIdleController.this.setAlarmsActive(paramBoolean);
    }
    
    public void setJobsActive(boolean paramBoolean)
    {
      DeviceIdleController.this.setJobsActive(paramBoolean);
    }
    
    public void setNetworkPolicyTempWhitelistCallback(Runnable paramRunnable)
    {
      DeviceIdleController.this.setNetworkPolicyTempWhitelistCallbackInternal(paramRunnable);
    }
  }
  
  private final class MotionListener
    extends TriggerEventListener
    implements SensorEventListener
  {
    boolean active = false;
    
    private MotionListener() {}
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent arg1)
    {
      synchronized (DeviceIdleController.this)
      {
        DeviceIdleController.-get14(DeviceIdleController.this).unregisterListener(this, DeviceIdleController.-get12(DeviceIdleController.this));
        this.active = false;
        DeviceIdleController.this.motionLocked();
        return;
      }
    }
    
    public void onTrigger(TriggerEvent arg1)
    {
      synchronized (DeviceIdleController.this)
      {
        this.active = false;
        DeviceIdleController.this.motionLocked();
        return;
      }
    }
    
    public boolean registerLocked()
    {
      if (DeviceIdleController.-get12(DeviceIdleController.this).getReportingMode() == 2) {}
      for (boolean bool = DeviceIdleController.-get14(DeviceIdleController.this).requestTriggerSensor(DeviceIdleController.-get11(DeviceIdleController.this), DeviceIdleController.-get12(DeviceIdleController.this)); bool; bool = DeviceIdleController.-get14(DeviceIdleController.this).registerListener(DeviceIdleController.-get11(DeviceIdleController.this), DeviceIdleController.-get12(DeviceIdleController.this), 3))
      {
        this.active = true;
        return bool;
      }
      Slog.e("DeviceIdleController", "Unable to register for " + DeviceIdleController.-get12(DeviceIdleController.this));
      return bool;
    }
    
    public void unregisterLocked()
    {
      if (DeviceIdleController.-get12(DeviceIdleController.this).getReportingMode() == 2) {
        DeviceIdleController.-get14(DeviceIdleController.this).cancelTriggerSensor(DeviceIdleController.-get11(DeviceIdleController.this), DeviceIdleController.-get12(DeviceIdleController.this));
      }
      for (;;)
      {
        this.active = false;
        return;
        DeviceIdleController.-get14(DeviceIdleController.this).unregisterListener(DeviceIdleController.-get11(DeviceIdleController.this));
      }
    }
  }
  
  final class MyHandler
    extends Handler
  {
    MyHandler(Looper paramLooper)
    {
      super();
    }
    
    /* Error */
    public void handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: ldc 23
      //   2: new 25	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 28	java/lang/StringBuilder:<init>	()V
      //   9: ldc 30
      //   11: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: aload_1
      //   15: getfield 40	android/os/Message:what	I
      //   18: invokevirtual 43	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   21: ldc 45
      //   23: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   26: invokevirtual 49	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   29: invokestatic 55	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   32: pop
      //   33: aload_1
      //   34: getfield 40	android/os/Message:what	I
      //   37: tableswitch	default:+47->84, 1:+48->85, 2:+56->93, 3:+56->93, 4:+296->333, 5:+495->532, 6:+675->712, 7:+689->726, 8:+776->813
      //   84: return
      //   85: aload_0
      //   86: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   89: invokevirtual 58	com/android/server/DeviceIdleController:handleWriteConfigFile	()V
      //   92: return
      //   93: invokestatic 63	com/android/server/EventLogTags:writeDeviceIdleOnStart	()V
      //   96: aload_0
      //   97: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   100: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   103: aload_0
      //   104: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   107: invokestatic 71	com/android/server/DeviceIdleController:-get15	(Lcom/android/server/DeviceIdleController;)I
      //   110: invokevirtual 77	android/os/PowerManagerInternal:setDeviceIdleState	(I)V
      //   113: aload_1
      //   114: getfield 40	android/os/Message:what	I
      //   117: iconst_2
      //   118: if_icmpne +181 -> 299
      //   121: aload_0
      //   122: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   125: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   128: iconst_1
      //   129: invokevirtual 81	android/os/PowerManagerInternal:setDeviceIdleMode	(Z)Z
      //   132: istore 6
      //   134: aload_0
      //   135: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   138: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   141: iconst_0
      //   142: invokevirtual 84	android/os/PowerManagerInternal:setLightDeviceIdleMode	(Z)Z
      //   145: istore 7
      //   147: iload 6
      //   149: istore 4
      //   151: iload 7
      //   153: istore 5
      //   155: aload_0
      //   156: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   159: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   162: ifnull +23 -> 185
      //   165: aload_0
      //   166: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   169: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   172: iconst_1
      //   173: invokevirtual 91	com/android/server/lights/LightsManager:setDeviceIdleMode	(Z)Z
      //   176: pop
      //   177: iload 7
      //   179: istore 5
      //   181: iload 6
      //   183: istore 4
      //   185: aload_0
      //   186: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   189: invokestatic 95	com/android/server/DeviceIdleController:-get13	(Lcom/android/server/DeviceIdleController;)Landroid/net/INetworkPolicyManager;
      //   192: iconst_1
      //   193: invokeinterface 100 2 0
      //   198: aload_0
      //   199: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   202: invokestatic 104	com/android/server/DeviceIdleController:-get0	(Lcom/android/server/DeviceIdleController;)Lcom/android/internal/app/IBatteryStats;
      //   205: astore 8
      //   207: aload_1
      //   208: getfield 40	android/os/Message:what	I
      //   211: iconst_2
      //   212: if_icmpne +116 -> 328
      //   215: iconst_2
      //   216: istore_2
      //   217: aload 8
      //   219: iload_2
      //   220: aconst_null
      //   221: invokestatic 110	android/os/Process:myUid	()I
      //   224: invokeinterface 116 4 0
      //   229: iload 4
      //   231: ifeq +39 -> 270
      //   234: aload_0
      //   235: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   238: invokestatic 120	com/android/server/DeviceIdleController:-get3	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   241: ldc 122
      //   243: getstatic 126	com/android/server/DeviceIdleController:mStopGps	Z
      //   246: invokevirtual 132	android/content/Intent:putExtra	(Ljava/lang/String;Z)Landroid/content/Intent;
      //   249: pop
      //   250: aload_0
      //   251: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   254: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   257: aload_0
      //   258: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   261: invokestatic 120	com/android/server/DeviceIdleController:-get3	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   264: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   267: invokevirtual 148	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
      //   270: iload 5
      //   272: ifeq +23 -> 295
      //   275: aload_0
      //   276: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   279: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   282: aload_0
      //   283: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   286: invokestatic 151	com/android/server/DeviceIdleController:-get6	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   289: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   292: invokevirtual 148	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
      //   295: invokestatic 154	com/android/server/EventLogTags:writeDeviceIdleOnComplete	()V
      //   298: return
      //   299: aload_0
      //   300: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   303: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   306: iconst_0
      //   307: invokevirtual 81	android/os/PowerManagerInternal:setDeviceIdleMode	(Z)Z
      //   310: istore 4
      //   312: aload_0
      //   313: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   316: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   319: iconst_1
      //   320: invokevirtual 84	android/os/PowerManagerInternal:setLightDeviceIdleMode	(Z)Z
      //   323: istore 5
      //   325: goto -140 -> 185
      //   328: iconst_1
      //   329: istore_2
      //   330: goto -113 -> 217
      //   333: ldc -100
      //   335: invokestatic 160	com/android/server/EventLogTags:writeDeviceIdleOffStart	(Ljava/lang/String;)V
      //   338: aload_0
      //   339: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   342: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   345: aload_0
      //   346: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   349: invokestatic 71	com/android/server/DeviceIdleController:-get15	(Lcom/android/server/DeviceIdleController;)I
      //   352: invokevirtual 77	android/os/PowerManagerInternal:setDeviceIdleState	(I)V
      //   355: aload_0
      //   356: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   359: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   362: iconst_0
      //   363: invokevirtual 81	android/os/PowerManagerInternal:setDeviceIdleMode	(Z)Z
      //   366: istore 4
      //   368: aload_0
      //   369: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   372: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   375: iconst_0
      //   376: invokevirtual 84	android/os/PowerManagerInternal:setLightDeviceIdleMode	(Z)Z
      //   379: istore 5
      //   381: aload_0
      //   382: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   385: invokestatic 95	com/android/server/DeviceIdleController:-get13	(Lcom/android/server/DeviceIdleController;)Landroid/net/INetworkPolicyManager;
      //   388: iconst_0
      //   389: invokeinterface 100 2 0
      //   394: aload_0
      //   395: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   398: invokestatic 104	com/android/server/DeviceIdleController:-get0	(Lcom/android/server/DeviceIdleController;)Lcom/android/internal/app/IBatteryStats;
      //   401: iconst_0
      //   402: aconst_null
      //   403: invokestatic 110	android/os/Process:myUid	()I
      //   406: invokeinterface 116 4 0
      //   411: iload 4
      //   413: ifeq +64 -> 477
      //   416: aload_0
      //   417: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   420: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   423: ifnull +15 -> 438
      //   426: aload_0
      //   427: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   430: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   433: iconst_0
      //   434: invokevirtual 91	com/android/server/lights/LightsManager:setDeviceIdleMode	(Z)Z
      //   437: pop
      //   438: aload_0
      //   439: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   442: invokevirtual 163	com/android/server/DeviceIdleController:incActiveIdleOps	()V
      //   445: aload_0
      //   446: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   449: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   452: aload_0
      //   453: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   456: invokestatic 120	com/android/server/DeviceIdleController:-get3	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   459: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   462: aconst_null
      //   463: aload_0
      //   464: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   467: invokestatic 167	com/android/server/DeviceIdleController:-get4	(Lcom/android/server/DeviceIdleController;)Landroid/content/BroadcastReceiver;
      //   470: aconst_null
      //   471: iconst_0
      //   472: aconst_null
      //   473: aconst_null
      //   474: invokevirtual 171	android/content/Context:sendOrderedBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;Ljava/lang/String;Landroid/content/BroadcastReceiver;Landroid/os/Handler;ILjava/lang/String;Landroid/os/Bundle;)V
      //   477: iload 5
      //   479: ifeq +42 -> 521
      //   482: aload_0
      //   483: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   486: invokevirtual 163	com/android/server/DeviceIdleController:incActiveIdleOps	()V
      //   489: aload_0
      //   490: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   493: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   496: aload_0
      //   497: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   500: invokestatic 151	com/android/server/DeviceIdleController:-get6	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   503: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   506: aconst_null
      //   507: aload_0
      //   508: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   511: invokestatic 167	com/android/server/DeviceIdleController:-get4	(Lcom/android/server/DeviceIdleController;)Landroid/content/BroadcastReceiver;
      //   514: aconst_null
      //   515: iconst_0
      //   516: aconst_null
      //   517: aconst_null
      //   518: invokevirtual 171	android/content/Context:sendOrderedBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;Ljava/lang/String;Landroid/content/BroadcastReceiver;Landroid/os/Handler;ILjava/lang/String;Landroid/os/Bundle;)V
      //   521: aload_0
      //   522: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   525: invokevirtual 174	com/android/server/DeviceIdleController:decActiveIdleOps	()V
      //   528: invokestatic 177	com/android/server/EventLogTags:writeDeviceIdleOffComplete	()V
      //   531: return
      //   532: aload_1
      //   533: getfield 181	android/os/Message:obj	Ljava/lang/Object;
      //   536: checkcast 183	java/lang/String
      //   539: astore 8
      //   541: aload_1
      //   542: getfield 186	android/os/Message:arg1	I
      //   545: istore_2
      //   546: aload 8
      //   548: ifnull +158 -> 706
      //   551: aload 8
      //   553: astore_1
      //   554: aload_1
      //   555: invokestatic 160	com/android/server/EventLogTags:writeDeviceIdleOffStart	(Ljava/lang/String;)V
      //   558: aload_0
      //   559: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   562: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   565: aload_0
      //   566: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   569: invokestatic 71	com/android/server/DeviceIdleController:-get15	(Lcom/android/server/DeviceIdleController;)I
      //   572: invokevirtual 77	android/os/PowerManagerInternal:setDeviceIdleState	(I)V
      //   575: aload_0
      //   576: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   579: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   582: iconst_0
      //   583: invokevirtual 81	android/os/PowerManagerInternal:setDeviceIdleMode	(Z)Z
      //   586: istore 4
      //   588: aload_0
      //   589: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   592: invokestatic 67	com/android/server/DeviceIdleController:-get9	(Lcom/android/server/DeviceIdleController;)Landroid/os/PowerManagerInternal;
      //   595: iconst_0
      //   596: invokevirtual 84	android/os/PowerManagerInternal:setLightDeviceIdleMode	(Z)Z
      //   599: istore 5
      //   601: aload_0
      //   602: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   605: invokestatic 95	com/android/server/DeviceIdleController:-get13	(Lcom/android/server/DeviceIdleController;)Landroid/net/INetworkPolicyManager;
      //   608: iconst_0
      //   609: invokeinterface 100 2 0
      //   614: aload_0
      //   615: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   618: invokestatic 104	com/android/server/DeviceIdleController:-get0	(Lcom/android/server/DeviceIdleController;)Lcom/android/internal/app/IBatteryStats;
      //   621: iconst_0
      //   622: aload 8
      //   624: iload_2
      //   625: invokeinterface 116 4 0
      //   630: iload 4
      //   632: ifeq +45 -> 677
      //   635: aload_0
      //   636: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   639: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   642: ifnull +15 -> 657
      //   645: aload_0
      //   646: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   649: invokestatic 88	com/android/server/DeviceIdleController:-get8	(Lcom/android/server/DeviceIdleController;)Lcom/android/server/lights/LightsManager;
      //   652: iconst_0
      //   653: invokevirtual 91	com/android/server/lights/LightsManager:setDeviceIdleMode	(Z)Z
      //   656: pop
      //   657: aload_0
      //   658: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   661: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   664: aload_0
      //   665: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   668: invokestatic 120	com/android/server/DeviceIdleController:-get3	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   671: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   674: invokevirtual 148	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
      //   677: iload 5
      //   679: ifeq +23 -> 702
      //   682: aload_0
      //   683: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   686: invokevirtual 136	com/android/server/DeviceIdleController:getContext	()Landroid/content/Context;
      //   689: aload_0
      //   690: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   693: invokestatic 151	com/android/server/DeviceIdleController:-get6	(Lcom/android/server/DeviceIdleController;)Landroid/content/Intent;
      //   696: getstatic 142	android/os/UserHandle:ALL	Landroid/os/UserHandle;
      //   699: invokevirtual 148	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
      //   702: invokestatic 177	com/android/server/EventLogTags:writeDeviceIdleOffComplete	()V
      //   705: return
      //   706: ldc -100
      //   708: astore_1
      //   709: goto -155 -> 554
      //   712: aload_1
      //   713: getfield 186	android/os/Message:arg1	I
      //   716: istore_2
      //   717: aload_0
      //   718: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   721: iload_2
      //   722: invokevirtual 189	com/android/server/DeviceIdleController:checkTempAppWhitelistTimeout	(I)V
      //   725: return
      //   726: aload_1
      //   727: getfield 186	android/os/Message:arg1	I
      //   730: iconst_1
      //   731: if_icmpne +52 -> 783
      //   734: iconst_1
      //   735: istore 4
      //   737: aload_0
      //   738: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   741: invokestatic 193	com/android/server/DeviceIdleController:-get10	(Lcom/android/server/DeviceIdleController;)Landroid/os/RemoteCallbackList;
      //   744: invokevirtual 198	android/os/RemoteCallbackList:beginBroadcast	()I
      //   747: istore_3
      //   748: iconst_0
      //   749: istore_2
      //   750: iload_2
      //   751: iload_3
      //   752: if_icmpge +37 -> 789
      //   755: aload_0
      //   756: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   759: invokestatic 193	com/android/server/DeviceIdleController:-get10	(Lcom/android/server/DeviceIdleController;)Landroid/os/RemoteCallbackList;
      //   762: iload_2
      //   763: invokevirtual 202	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
      //   766: checkcast 204	android/os/IMaintenanceActivityListener
      //   769: iload 4
      //   771: invokeinterface 207 2 0
      //   776: iload_2
      //   777: iconst_1
      //   778: iadd
      //   779: istore_2
      //   780: goto -30 -> 750
      //   783: iconst_0
      //   784: istore 4
      //   786: goto -49 -> 737
      //   789: aload_0
      //   790: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   793: invokestatic 193	com/android/server/DeviceIdleController:-get10	(Lcom/android/server/DeviceIdleController;)Landroid/os/RemoteCallbackList;
      //   796: invokevirtual 210	android/os/RemoteCallbackList:finishBroadcast	()V
      //   799: return
      //   800: astore_1
      //   801: aload_0
      //   802: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   805: invokestatic 193	com/android/server/DeviceIdleController:-get10	(Lcom/android/server/DeviceIdleController;)Landroid/os/RemoteCallbackList;
      //   808: invokevirtual 210	android/os/RemoteCallbackList:finishBroadcast	()V
      //   811: aload_1
      //   812: athrow
      //   813: aload_0
      //   814: getfield 13	com/android/server/DeviceIdleController$MyHandler:this$0	Lcom/android/server/DeviceIdleController;
      //   817: invokevirtual 174	com/android/server/DeviceIdleController:decActiveIdleOps	()V
      //   820: return
      //   821: astore_1
      //   822: goto -46 -> 776
      //   825: astore_1
      //   826: goto -196 -> 630
      //   829: astore_1
      //   830: goto -419 -> 411
      //   833: astore_1
      //   834: goto -605 -> 229
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	837	0	this	MyHandler
      //   0	837	1	paramMessage	Message
      //   216	564	2	i	int
      //   747	6	3	j	int
      //   149	636	4	bool1	boolean
      //   153	525	5	bool2	boolean
      //   132	50	6	bool3	boolean
      //   145	33	7	bool4	boolean
      //   205	418	8	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   755	776	800	finally
      //   755	776	821	android/os/RemoteException
      //   601	630	825	android/os/RemoteException
      //   381	411	829	android/os/RemoteException
      //   185	215	833	android/os/RemoteException
      //   217	229	833	android/os/RemoteException
    }
  }
  
  class Shell
    extends ShellCommand
  {
    int userId = 0;
    
    Shell() {}
    
    public int onCommand(String paramString)
    {
      return DeviceIdleController.this.onShellCommand(this, paramString);
    }
    
    public void onHelp()
    {
      DeviceIdleController.dumpHelp(getOutPrintWriter());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/DeviceIdleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */