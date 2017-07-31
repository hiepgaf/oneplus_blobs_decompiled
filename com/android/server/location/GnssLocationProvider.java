package com.android.server.location;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.location.GeofenceHardwareImpl;
import android.location.FusedBatchOptions.SourceTechnologies;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.IGnssStatusListener;
import android.location.IGnssStatusProvider;
import android.location.IGnssStatusProvider.Stub;
import android.location.IGpsGeofenceHardware;
import android.location.IGpsGeofenceHardware.Stub;
import android.location.ILocationManager;
import android.location.INetInitiatedListener;
import android.location.INetInitiatedListener.Stub;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Telephony.Carriers;
import android.provider.Telephony.Sms.Intents;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.util.NtpTrustedTime;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.location.GpsNetInitiatedHandler.GpsNiNotification;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.server.OnePlusGpsNotification;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;

public class GnssLocationProvider
  implements LocationProviderInterface
{
  private static final int ADD_LISTENER = 8;
  private static final int AGPS_DATA_CONNECTION_CLOSED = 0;
  private static final int AGPS_DATA_CONNECTION_OPEN = 2;
  private static final int AGPS_DATA_CONNECTION_OPENING = 1;
  private static final int AGPS_REF_LOCATION_TYPE_GSM_CELLID = 1;
  private static final int AGPS_REF_LOCATION_TYPE_UMTS_CELLID = 2;
  private static final int AGPS_REG_LOCATION_TYPE_MAC = 3;
  private static final int AGPS_RIL_REQUEST_REFLOC_CELLID = 1;
  private static final int AGPS_RIL_REQUEST_REFLOC_MAC = 2;
  private static final int AGPS_RIL_REQUEST_SETID_IMSI = 1;
  private static final int AGPS_RIL_REQUEST_SETID_MSISDN = 2;
  private static final int AGPS_SETID_TYPE_IMSI = 1;
  private static final int AGPS_SETID_TYPE_MSISDN = 2;
  private static final int AGPS_SETID_TYPE_NONE = 0;
  private static final int AGPS_SUPL_MODE_MSA = 2;
  private static final int AGPS_SUPL_MODE_MSB = 1;
  private static final int AGPS_TYPE_C2K = 2;
  private static final int AGPS_TYPE_SUPL = 1;
  private static final String ALARM_TIMEOUT = "com.android.internal.location.ALARM_TIMEOUT";
  private static final String ALARM_WAKEUP = "com.android.internal.location.ALARM_WAKEUP";
  private static final int APN_INVALID = 0;
  private static final int APN_IPV4 = 1;
  private static final int APN_IPV4V6 = 3;
  private static final int APN_IPV6 = 2;
  private static final String BATTERY_SAVER_GPS_MODE = "batterySaverGpsMode";
  private static final int BATTERY_SAVER_MODE_DISABLED_WHEN_SCREEN_OFF = 1;
  private static final int BATTERY_SAVER_MODE_NO_CHANGE = 0;
  private static final int CHECK_LOCATION = 1;
  private static final boolean DEBUG = Log.isLoggable("GnssLocationProvider", 3);
  private static final String DEFAULT_PROPERTIES_FILE = "/vendor/etc/gps.conf";
  private static final int DOWNLOAD_XTRA_DATA = 6;
  private static final int DOWNLOAD_XTRA_DATA_FINISHED = 11;
  private static final int ENABLE = 2;
  private static final int GPS_AGPS_DATA_CONNECTED = 3;
  private static final int GPS_AGPS_DATA_CONN_DONE = 4;
  private static final int GPS_AGPS_DATA_CONN_FAILED = 5;
  private static final int GPS_CAPABILITY_GEOFENCING = 32;
  private static final int GPS_CAPABILITY_MEASUREMENTS = 64;
  private static final int GPS_CAPABILITY_MSA = 4;
  private static final int GPS_CAPABILITY_MSB = 2;
  private static final int GPS_CAPABILITY_NAV_MESSAGES = 128;
  private static final int GPS_CAPABILITY_ON_DEMAND_TIME = 16;
  private static final int GPS_CAPABILITY_SCHEDULING = 1;
  private static final int GPS_CAPABILITY_SINGLE_SHOT = 8;
  private static final int GPS_DELETE_ALL = 65535;
  private static final int GPS_DELETE_ALMANAC = 2;
  private static final int GPS_DELETE_CELLDB_INFO = 32768;
  private static final int GPS_DELETE_EPHEMERIS = 1;
  private static final int GPS_DELETE_HEALTH = 64;
  private static final int GPS_DELETE_IONO = 16;
  private static final int GPS_DELETE_POSITION = 4;
  private static final int GPS_DELETE_RTI = 1024;
  private static final int GPS_DELETE_SADATA = 512;
  private static final int GPS_DELETE_SVDIR = 128;
  private static final int GPS_DELETE_SVSTEER = 256;
  private static final int GPS_DELETE_TIME = 8;
  private static final int GPS_DELETE_UTC = 32;
  private static final int GPS_GEOFENCE_AVAILABLE = 2;
  private static final int GPS_GEOFENCE_ERROR_GENERIC = -149;
  private static final int GPS_GEOFENCE_ERROR_ID_EXISTS = -101;
  private static final int GPS_GEOFENCE_ERROR_ID_UNKNOWN = -102;
  private static final int GPS_GEOFENCE_ERROR_INVALID_TRANSITION = -103;
  private static final int GPS_GEOFENCE_ERROR_TOO_MANY_GEOFENCES = 100;
  private static final int GPS_GEOFENCE_OPERATION_SUCCESS = 0;
  private static final int GPS_GEOFENCE_UNAVAILABLE = 1;
  private static final int GPS_POLLING_THRESHOLD_INTERVAL = 10000;
  private static final int GPS_POSITION_MODE_MS_ASSISTED = 2;
  private static final int GPS_POSITION_MODE_MS_BASED = 1;
  private static final int GPS_POSITION_MODE_STANDALONE = 0;
  private static final int GPS_POSITION_RECURRENCE_PERIODIC = 0;
  private static final int GPS_POSITION_RECURRENCE_SINGLE = 1;
  private static final int GPS_RELEASE_AGPS_DATA_CONN = 2;
  private static final int GPS_REQUEST_AGPS_DATA_CONN = 1;
  private static final int GPS_STATUS_ENGINE_OFF = 4;
  private static final int GPS_STATUS_ENGINE_ON = 3;
  private static final int GPS_STATUS_NONE = 0;
  private static final int GPS_STATUS_SESSION_BEGIN = 1;
  private static final int GPS_STATUS_SESSION_END = 2;
  private static final int INITIALIZE_HANDLER = 13;
  private static final int INJECT_NTP_TIME = 5;
  private static final int INJECT_NTP_TIME_FINISHED = 10;
  private static final int LOCATION_HAS_ACCURACY = 16;
  private static final int LOCATION_HAS_ALTITUDE = 2;
  private static final int LOCATION_HAS_BEARING = 8;
  private static final int LOCATION_HAS_LAT_LONG = 1;
  private static final int LOCATION_HAS_SPEED = 4;
  private static final int LOCATION_INVALID = 0;
  private static final String LPP_PROFILE = "persist.sys.gps.lpp";
  private static final long MAX_RETRY_INTERVAL = 14400000L;
  private static final int MAX_SVS = 64;
  private static final int NO_FIX_TIMEOUT = 60000;
  private static final long NTP_INTERVAL = 86400000L;
  private static final ProviderProperties PROPERTIES;
  private static final String PROPERTIES_FILE_PREFIX = "/vendor/etc/gps";
  private static final String PROPERTIES_FILE_SUFFIX = ".conf";
  private static final long RECENT_FIX_TIMEOUT = 10000L;
  private static final int RELEASE_SUPL_CONNECTION = 15;
  private static final int REMOVE_LISTENER = 9;
  private static final int REQUEST_SUPL_CONNECTION = 14;
  private static final long RETRY_INTERVAL = 300000L;
  private static final int SET_REQUEST = 3;
  private static final String SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
  private static final int STATE_DOWNLOADING = 1;
  private static final int STATE_IDLE = 2;
  private static final int STATE_PENDING_NETWORK = 0;
  private static final int SUBSCRIPTION_OR_SIM_CHANGED = 12;
  private static final String TAG = "GnssLocationProvider";
  private static final int TCP_MAX_PORT = 65535;
  private static final int TCP_MIN_PORT = 0;
  private static final int UPDATE_LOCATION = 7;
  private static final int UPDATE_NETWORK_STATE = 4;
  private static final boolean VERBOSE = Log.isLoggable("GnssLocationProvider", 2);
  private static final String[] VzwGid1List;
  private static final String[] VzwMccMncList;
  private static final String WAKELOCK_KEY = "GnssLocationProvider";
  private static boolean needMSACheck;
  boolean isSupportGpsNotification = false;
  private InetAddress mAGpsDataConnectionIpAddr;
  private int mAGpsDataConnectionState;
  private final AlarmManager mAlarmManager;
  private final IAppOpsService mAppOpsService;
  private final IBatteryStats mBatteryStats;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (GnssLocationProvider.-get0()) {
        Log.d("GnssLocationProvider", "receive broadcast intent, action: " + str);
      }
      if (str == null) {
        return;
      }
      if (str.equals("com.android.internal.location.ALARM_WAKEUP")) {
        GnssLocationProvider.-wrap31(GnssLocationProvider.this, false);
      }
      do
      {
        return;
        if (str.equals("com.android.internal.location.ALARM_TIMEOUT"))
        {
          GnssLocationProvider.-wrap23(GnssLocationProvider.this);
          return;
        }
        if (str.equals("android.intent.action.DATA_SMS_RECEIVED"))
        {
          GnssLocationProvider.-wrap12(GnssLocationProvider.this, paramAnonymousIntent);
          return;
        }
        if (str.equals("android.provider.Telephony.WAP_PUSH_RECEIVED"))
        {
          GnssLocationProvider.-wrap13(GnssLocationProvider.this, paramAnonymousIntent);
          return;
        }
        if (("android.os.action.POWER_SAVE_MODE_CHANGED".equals(str)) || ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(str)) || ("android.intent.action.SCREEN_OFF".equals(str)) || ("android.intent.action.SCREEN_ON".equals(str)))
        {
          GnssLocationProvider.this.mStopGps = false;
          if ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(str))
          {
            GnssLocationProvider.this.mStopGps = ((Boolean)paramAnonymousIntent.getExtra("stopGps")).booleanValue();
            Log.e("GnssLocationProvider", " mStopGps =  " + GnssLocationProvider.this.mStopGps);
          }
          GnssLocationProvider.-wrap33(GnssLocationProvider.this);
          return;
        }
      } while (!str.equals("android.intent.action.SIM_STATE_CHANGED"));
      GnssLocationProvider.-wrap32(GnssLocationProvider.this, paramAnonymousContext);
    }
  };
  private String mC2KServerHost;
  private int mC2KServerPort;
  private WorkSource mClientSource = new WorkSource();
  private float[] mCn0s = new float[64];
  private final ConnectivityManager mConnMgr;
  private final Context mContext;
  private boolean mDisableGps = false;
  private int mDownloadXtraDataPending = 0;
  private boolean mEnabled;
  private int mEngineCapabilities;
  private boolean mEngineOn;
  private int mFixInterval = 1000;
  private long mFixRequestTime = 0L;
  private GeofenceHardwareImpl mGeofenceHardwareImpl;
  private final GnssMeasurementsProvider mGnssMeasurementsProvider;
  private final GnssNavigationMessageProvider mGnssNavigationMessageProvider;
  private final IGnssStatusProvider mGnssStatusProvider = new IGnssStatusProvider.Stub()
  {
    public void registerGnssStatusCallback(IGnssStatusListener paramAnonymousIGnssStatusListener)
    {
      GnssLocationProvider.-get7(GnssLocationProvider.this).addListener(paramAnonymousIGnssStatusListener);
    }
    
    public void unregisterGnssStatusCallback(IGnssStatusListener paramAnonymousIGnssStatusListener)
    {
      GnssLocationProvider.-get7(GnssLocationProvider.this).removeListener(paramAnonymousIGnssStatusListener);
    }
  };
  private IGpsGeofenceHardware mGpsGeofenceBinder = new IGpsGeofenceHardware.Stub()
  {
    public boolean addCircularHardwareGeofence(int paramAnonymousInt1, double paramAnonymousDouble1, double paramAnonymousDouble2, double paramAnonymousDouble3, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
    {
      return GnssLocationProvider.-wrap0(paramAnonymousInt1, paramAnonymousDouble1, paramAnonymousDouble2, paramAnonymousDouble3, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousInt5);
    }
    
    public boolean isHardwareGeofenceSupported()
    {
      return GnssLocationProvider.-wrap2();
    }
    
    public boolean pauseHardwareGeofence(int paramAnonymousInt)
    {
      return GnssLocationProvider.-wrap5(paramAnonymousInt);
    }
    
    public boolean removeHardwareGeofence(int paramAnonymousInt)
    {
      return GnssLocationProvider.-wrap6(paramAnonymousInt);
    }
    
    public boolean resumeHardwareGeofence(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return GnssLocationProvider.-wrap7(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  private Handler mHandler;
  private final ILocationManager mILocationManager;
  private int mInjectNtpTimePending = 0;
  private long mLastFixTime;
  private final GnssStatusListenerHelper mListenerHelper;
  private Location mLocation = new Location("gps");
  private Bundle mLocationExtras = new Bundle();
  private int mLocationFlags = 0;
  private LocationManager mLocationManager;
  private Object mLock = new Object();
  private final GpsNetInitiatedHandler mNIHandler;
  private boolean mNavigating;
  private final INetInitiatedListener mNetInitiatedListener = new INetInitiatedListener.Stub()
  {
    public boolean sendNiResponse(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (GnssLocationProvider.-get0()) {
        Log.d("GnssLocationProvider", "sendNiResponse, notifId: " + paramAnonymousInt1 + ", response: " + paramAnonymousInt2);
      }
      GnssLocationProvider.-wrap26(GnssLocationProvider.this, paramAnonymousInt1, paramAnonymousInt2);
      return true;
    }
  };
  private final ConnectivityManager.NetworkCallback mNetworkConnectivityCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onAvailable(Network paramAnonymousNetwork)
    {
      if (GnssLocationProvider.-get6(GnssLocationProvider.this) == 0) {
        GnssLocationProvider.-wrap29(GnssLocationProvider.this);
      }
      if (GnssLocationProvider.-get4(GnssLocationProvider.this) == 0) {
        GnssLocationProvider.-wrap34(GnssLocationProvider.this);
      }
    }
  };
  private byte[] mNmeaBuffer = new byte[120];
  private BackOff mNtpBackOff = new BackOff(300000L, 14400000L);
  private final NtpTrustedTime mNtpTime;
  private boolean mOnDemandTimeInjection;
  private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener()
  {
    public void onSubscriptionsChanged()
    {
      GnssLocationProvider.-wrap30(GnssLocationProvider.this, 12, 0, null);
    }
  };
  private OnePlusGpsNotification mOneplusGpsNotificaion;
  private int mPositionMode;
  private final PowerManager mPowerManager;
  private Properties mProperties;
  private ProviderRequest mProviderRequest = null;
  private boolean mSingleShot;
  private boolean mStarted;
  private int mStatus = 1;
  private long mStatusUpdateTime = SystemClock.elapsedRealtime();
  boolean mStopGps = true;
  private final ConnectivityManager.NetworkCallback mSuplConnectivityCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onAvailable(Network paramAnonymousNetwork)
    {
      GnssLocationProvider.-wrap30(GnssLocationProvider.this, 4, 0, paramAnonymousNetwork);
    }
    
    public void onLost(Network paramAnonymousNetwork)
    {
      GnssLocationProvider.-wrap27(GnssLocationProvider.this, 2);
    }
    
    public void onUnavailable()
    {
      GnssLocationProvider.-wrap27(GnssLocationProvider.this, 5);
    }
  };
  private boolean mSuplEsEnabled = false;
  private String mSuplServerHost;
  private int mSuplServerPort = 0;
  private boolean mSupportsXtra;
  private float[] mSvAzimuths = new float[64];
  private int mSvCount;
  private float[] mSvElevations = new float[64];
  private int[] mSvidWithFlags = new int[64];
  private int mTimeToFirstFix = 0;
  private final PendingIntent mTimeoutIntent;
  private final PowerManager.WakeLock mWakeLock;
  private final PendingIntent mWakeupIntent;
  private WorkSource mWorkSource = null;
  private BackOff mXtraBackOff = new BackOff(300000L, 14400000L);
  private int mYearOfHardware = 0;
  
  static
  {
    PROPERTIES = new ProviderProperties(true, true, false, false, true, true, true, 3, 1);
    needMSACheck = false;
    VzwMccMncList = new String[] { "311480", "310004", "20404" };
    VzwGid1List = new String[] { "", "", "BAE0000000000000" };
    class_init_native();
  }
  
  public GnssLocationProvider(Context paramContext, ILocationManager paramILocationManager, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mNtpTime = NtpTrustedTime.getInstance(paramContext);
    this.mILocationManager = paramILocationManager;
    this.mLocation.setExtras(this.mLocationExtras);
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mWakeLock = this.mPowerManager.newWakeLock(1, "GnssLocationProvider");
    this.mWakeLock.setReferenceCounted(true);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    this.mWakeupIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.internal.location.ALARM_WAKEUP"), 0);
    this.mTimeoutIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.internal.location.ALARM_TIMEOUT"), 0);
    this.mConnMgr = ((ConnectivityManager)paramContext.getSystemService("connectivity"));
    this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    this.mHandler = new ProviderHandler(paramLooper);
    this.mProperties = new Properties();
    sendMessage(13, 0, null);
    this.mNIHandler = new GpsNetInitiatedHandler(paramContext, this.mNetInitiatedListener, this.mSuplEsEnabled);
    this.mListenerHelper = new GnssStatusListenerHelper(this.mHandler)
    {
      protected boolean isAvailableInPlatform()
      {
        return GnssLocationProvider.isSupported();
      }
      
      protected boolean isGpsEnabled()
      {
        return GnssLocationProvider.this.isEnabled();
      }
    };
    this.mGnssMeasurementsProvider = new GnssMeasurementsProvider(this.mHandler)
    {
      public boolean isAvailableInPlatform()
      {
        return GnssLocationProvider.-wrap3();
      }
      
      protected boolean isGpsEnabled()
      {
        return GnssLocationProvider.this.isEnabled();
      }
      
      protected boolean registerWithService()
      {
        return GnssLocationProvider.-wrap8(GnssLocationProvider.this);
      }
      
      protected void unregisterFromService()
      {
        GnssLocationProvider.-wrap10(GnssLocationProvider.this);
      }
    };
    this.mGnssNavigationMessageProvider = new GnssNavigationMessageProvider(this.mHandler)
    {
      protected boolean isAvailableInPlatform()
      {
        return GnssLocationProvider.-wrap4();
      }
      
      protected boolean isGpsEnabled()
      {
        return GnssLocationProvider.this.isEnabled();
      }
      
      protected boolean registerWithService()
      {
        return GnssLocationProvider.-wrap9(GnssLocationProvider.this);
      }
      
      protected void unregisterFromService()
      {
        GnssLocationProvider.-wrap11(GnssLocationProvider.this);
      }
    };
    this.mLocationManager = ((LocationManager)this.mContext.getSystemService("location"));
    this.isSupportGpsNotification = this.mContext.getPackageManager().hasSystemFeature("oem.gpsnotification.control");
  }
  
  private String agpsDataConnStateAsString()
  {
    switch (this.mAGpsDataConnectionState)
    {
    default: 
      return "<Unknown>";
    case 0: 
      return "CLOSED";
    case 2: 
      return "OPEN";
    }
    return "OPENING";
  }
  
  private String agpsDataConnStatusAsString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "<Unknown>";
    case 3: 
      return "CONNECTED";
    case 4: 
      return "DONE";
    case 5: 
      return "FAILED";
    case 2: 
      return "RELEASE";
    }
    return "REQUEST";
  }
  
  private Location buildLocation(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2, float paramFloat3, long paramLong)
  {
    Location localLocation = new Location("gps");
    if ((paramInt & 0x1) == 1)
    {
      localLocation.setLatitude(paramDouble1);
      localLocation.setLongitude(paramDouble2);
      localLocation.setTime(paramLong);
      localLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
    }
    if ((paramInt & 0x2) == 2) {
      localLocation.setAltitude(paramDouble3);
    }
    if ((paramInt & 0x4) == 4) {
      localLocation.setSpeed(paramFloat1);
    }
    if ((paramInt & 0x8) == 8) {
      localLocation.setBearing(paramFloat2);
    }
    if ((paramInt & 0x10) == 16) {
      localLocation.setAccuracy(paramFloat3);
    }
    return localLocation;
  }
  
  private void checkSmsSuplInit(Intent paramIntent)
  {
    paramIntent = Telephony.Sms.Intents.getMessagesFromIntent(paramIntent);
    if (paramIntent == null)
    {
      Log.e("GnssLocationProvider", "Message does not exist in the intent.");
      return;
    }
    int i = 0;
    int j = paramIntent.length;
    while (i < j)
    {
      byte[] arrayOfByte = paramIntent[i];
      if ((arrayOfByte != null) && (arrayOfByte.mWrappedSmsMessage != null))
      {
        arrayOfByte = arrayOfByte.getUserData();
        if (arrayOfByte != null) {
          native_agps_ni_message(arrayOfByte, arrayOfByte.length);
        }
      }
      i += 1;
    }
  }
  
  private void checkWapSuplInit(Intent paramIntent)
  {
    paramIntent = paramIntent.getByteArrayExtra("data");
    if (paramIntent == null) {
      return;
    }
    native_agps_ni_message(paramIntent, paramIntent.length);
  }
  
  private static native void class_init_native();
  
  private boolean deleteAidingData(Bundle paramBundle)
  {
    if (SystemProperties.get("persist.sys.assert.panic", "false").equals("true")) {}
    for (boolean bool = true;; bool = false)
    {
      Log.e("GnssLocationProvider", "deleteAidingData..debugStatus is:" + bool);
      if (bool) {
        break;
      }
      Log.e("GnssLocationProvider", "Not in Debug Status, not deleteAidingData");
      return true;
    }
    int j;
    if (paramBundle == null) {
      j = 65535;
    }
    while (j != 0)
    {
      native_delete_aiding_data(j);
      return true;
      j = 0;
      if (paramBundle.getBoolean("ephemeris")) {
        j = 1;
      }
      int i = j;
      if (paramBundle.getBoolean("almanac")) {
        i = j | 0x2;
      }
      j = i;
      if (paramBundle.getBoolean("position")) {
        j = i | 0x4;
      }
      i = j;
      if (paramBundle.getBoolean("time")) {
        i = j | 0x8;
      }
      j = i;
      if (paramBundle.getBoolean("iono")) {
        j = i | 0x10;
      }
      i = j;
      if (paramBundle.getBoolean("utc")) {
        i = j | 0x20;
      }
      j = i;
      if (paramBundle.getBoolean("health")) {
        j = i | 0x40;
      }
      i = j;
      if (paramBundle.getBoolean("svdir")) {
        i = j | 0x80;
      }
      j = i;
      if (paramBundle.getBoolean("svsteer")) {
        j = i | 0x100;
      }
      i = j;
      if (paramBundle.getBoolean("sadata")) {
        i = j | 0x200;
      }
      j = i;
      if (paramBundle.getBoolean("rti")) {
        j = i | 0x400;
      }
      i = j;
      if (paramBundle.getBoolean("celldb-info")) {
        i = j | 0x8000;
      }
      j = i;
      if (paramBundle.getBoolean("all")) {
        j = i | 0xFFFF;
      }
    }
    return false;
  }
  
  private void ensureInHandlerThread()
  {
    if ((this.mHandler != null) && (Looper.myLooper() == this.mHandler.getLooper())) {
      return;
    }
    throw new RuntimeException("This method must run on the Handler thread.");
  }
  
  private int getApnIpType(String paramString)
  {
    ensureInHandlerThread();
    if (paramString == null) {
      return 0;
    }
    Object localObject3 = String.format("current = 1 and apn = '%s' and carrier_enabled = 1", new Object[] { paramString });
    localObject2 = null;
    localObject1 = null;
    try
    {
      localObject3 = this.mContext.getContentResolver().query(Telephony.Carriers.CONTENT_URI, new String[] { "protocol" }, (String)localObject3, null, "name ASC");
      if (localObject3 != null)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (((Cursor)localObject3).moveToFirst())
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          int i = translateToApnIpType(((Cursor)localObject3).getString(0), paramString);
          return i;
        }
      }
      localObject1 = localObject3;
      localObject2 = localObject3;
      Log.e("GnssLocationProvider", "No entry found in query for APN: " + paramString);
    }
    catch (Exception localException)
    {
      localObject2 = localObject1;
      Log.e("GnssLocationProvider", "Error encountered on APN query for: " + paramString, localException);
      return 0;
    }
    finally
    {
      if (localObject2 == null) {
        break label224;
      }
      ((Cursor)localObject2).close();
    }
    return 0;
  }
  
  private int getGeofenceStatus(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 0: 
      return 0;
    case -149: 
      return 5;
    case -101: 
      return 2;
    case -103: 
      return 4;
    case 100: 
      return 1;
    }
    return 3;
  }
  
  private String getSelectedApn()
  {
    Object localObject4 = Uri.parse("content://telephony/carriers/preferapn");
    localObject3 = null;
    localObject1 = null;
    try
    {
      localObject4 = this.mContext.getContentResolver().query((Uri)localObject4, new String[] { "apn" }, null, null, "name ASC");
      if (localObject4 != null)
      {
        localObject1 = localObject4;
        localObject3 = localObject4;
        if (((Cursor)localObject4).moveToFirst())
        {
          localObject1 = localObject4;
          localObject3 = localObject4;
          String str = ((Cursor)localObject4).getString(0);
          return str;
        }
      }
      localObject1 = localObject4;
      localObject3 = localObject4;
      Log.e("GnssLocationProvider", "No APN found to select.");
    }
    catch (Exception localException)
    {
      localObject3 = localObject1;
      Log.e("GnssLocationProvider", "Error encountered on selecting the APN.", localException);
      return null;
    }
    finally
    {
      if (localObject3 == null) {
        break label142;
      }
      ((Cursor)localObject3).close();
    }
    return null;
  }
  
  private int getSuplMode(Properties paramProperties, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      paramProperties = paramProperties.getProperty("SUPL_MODE");
      int i = 0;
      if (!TextUtils.isEmpty(paramProperties)) {}
      try
      {
        i = Integer.parseInt(paramProperties);
        if ((hasCapability(2)) && ((i & 0x1) != 0)) {
          return 1;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("GnssLocationProvider", "unable to parse SUPL_MODE: " + paramProperties);
        return 0;
      }
      if ((paramBoolean2) && (hasCapability(4)) && ((i & 0x2) != 0)) {
        return 2;
      }
    }
    return 0;
  }
  
  private void handleDisable()
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "handleDisable");
    }
    updateClientUids(new WorkSource());
    stopNavigating();
    this.mAlarmManager.cancel(this.mWakeupIntent);
    this.mAlarmManager.cancel(this.mTimeoutIntent);
    native_cleanup();
    this.mGnssMeasurementsProvider.onGpsEnabledChanged();
    this.mGnssNavigationMessageProvider.onGpsEnabledChanged();
  }
  
  private void handleDownloadXtraData()
  {
    if (!this.mSupportsXtra)
    {
      Log.d("GnssLocationProvider", "handleDownloadXtraData() called when Xtra not supported");
      return;
    }
    if (this.mDownloadXtraDataPending == 1) {
      return;
    }
    if (!isDataNetworkConnected())
    {
      this.mDownloadXtraDataPending = 0;
      return;
    }
    this.mDownloadXtraDataPending = 1;
    this.mWakeLock.acquire();
    Log.i("GnssLocationProvider", "WakeLock acquired by handleDownloadXtraData()");
    AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        byte[] arrayOfByte = new GpsXtraDownloader(GnssLocationProvider.-get13(GnssLocationProvider.this)).downloadXtraData();
        if (arrayOfByte != null)
        {
          if (GnssLocationProvider.-get0()) {
            Log.d("GnssLocationProvider", "calling native_inject_xtra_data");
          }
          GnssLocationProvider.-wrap25(GnssLocationProvider.this, arrayOfByte, arrayOfByte.length);
          GnssLocationProvider.-get15(GnssLocationProvider.this).reset();
        }
        GnssLocationProvider.-wrap30(GnssLocationProvider.this, 11, 0, null);
        if (arrayOfByte == null) {
          GnssLocationProvider.-get5(GnssLocationProvider.this).sendEmptyMessageDelayed(6, GnssLocationProvider.-get15(GnssLocationProvider.this).nextBackoffMillis());
        }
        GnssLocationProvider.-get14(GnssLocationProvider.this).release();
        Log.i("GnssLocationProvider", "WakeLock released by handleDownloadXtraData()");
      }
    });
  }
  
  private void handleEnable()
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "handleEnable");
    }
    if (native_init())
    {
      this.mSupportsXtra = native_supports_xtra();
      if (this.mSuplServerHost != null) {
        native_set_agps_server(1, this.mSuplServerHost, this.mSuplServerPort);
      }
      if (this.mC2KServerHost != null) {
        native_set_agps_server(2, this.mC2KServerHost, this.mC2KServerPort);
      }
      this.mGnssMeasurementsProvider.onGpsEnabledChanged();
      this.mGnssNavigationMessageProvider.onGpsEnabledChanged();
      return;
    }
    synchronized (this.mLock)
    {
      this.mEnabled = false;
      Log.w("GnssLocationProvider", "Failed to enable location provider");
      return;
    }
  }
  
  private void handleGpsRequest(boolean paramBoolean)
  {
    if (this.mOneplusGpsNotificaion == null) {
      this.mOneplusGpsNotificaion = new OnePlusGpsNotification(this.mContext);
    }
    ArrayList localArrayList = new ArrayList();
    if (paramBoolean) {
      localArrayList = (ArrayList)this.mLocationManager.getCurrentProviderPackageList("gps");
    }
    this.mOneplusGpsNotificaion.updateGpsRequstPackage(localArrayList);
  }
  
  private void handleInjectNtpTime()
  {
    if (this.mInjectNtpTimePending == 1) {
      return;
    }
    if (!isDataNetworkConnected())
    {
      this.mInjectNtpTimePending = 0;
      return;
    }
    this.mInjectNtpTimePending = 1;
    this.mWakeLock.acquire();
    Log.i("GnssLocationProvider", "WakeLock acquired by handleInjectNtpTime()");
    AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        boolean bool = true;
        if (GnssLocationProvider.-get10(GnssLocationProvider.this).getCacheAge() >= 86400000L) {
          bool = GnssLocationProvider.-get10(GnssLocationProvider.this).forceRefresh();
        }
        long l1;
        if (GnssLocationProvider.-get10(GnssLocationProvider.this).getCacheAge() < 86400000L)
        {
          l1 = GnssLocationProvider.-get10(GnssLocationProvider.this).getCachedNtpTime();
          long l2 = GnssLocationProvider.-get10(GnssLocationProvider.this).getCachedNtpTimeReference();
          long l3 = GnssLocationProvider.-get10(GnssLocationProvider.this).getCacheCertainty();
          long l4 = System.currentTimeMillis();
          if (GnssLocationProvider.-get0()) {
            Log.d("GnssLocationProvider", "NTP server returned: " + l1 + " (" + new Date(l1) + ") reference: " + l2 + " certainty: " + l3 + " system time offset: " + (l1 - l4));
          }
          GnssLocationProvider.-wrap24(GnssLocationProvider.this, l1, l2, (int)l3);
          l1 = 86400000L;
          GnssLocationProvider.-get9(GnssLocationProvider.this).reset();
          GnssLocationProvider.-wrap30(GnssLocationProvider.this, 10, 0, null);
          if (GnssLocationProvider.-get0()) {
            Log.d("GnssLocationProvider", String.format("onDemandTimeInjection=%s, refreshSuccess=%s, delay=%s", new Object[] { Boolean.valueOf(GnssLocationProvider.-get11(GnssLocationProvider.this)), Boolean.valueOf(bool), Long.valueOf(l1) }));
          }
          if ((GnssLocationProvider.-get11(GnssLocationProvider.this)) || (!bool)) {
            break label307;
          }
        }
        for (;;)
        {
          GnssLocationProvider.-get14(GnssLocationProvider.this).release();
          Log.i("GnssLocationProvider", "WakeLock released by handleInjectNtpTime()");
          return;
          Log.e("GnssLocationProvider", "requestTime failed");
          l1 = GnssLocationProvider.-get9(GnssLocationProvider.this).nextBackoffMillis();
          break;
          label307:
          GnssLocationProvider.-get5(GnssLocationProvider.this).sendEmptyMessageDelayed(5, l1);
        }
      }
    });
  }
  
  private void handleReleaseSuplConnection(int paramInt)
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", String.format("releaseSuplConnection, state=%s, status=%s", new Object[] { agpsDataConnStateAsString(), agpsDataConnStatusAsString(paramInt) }));
    }
    if (this.mAGpsDataConnectionState == 0) {
      return;
    }
    this.mAGpsDataConnectionState = 0;
    this.mConnMgr.unregisterNetworkCallback(this.mSuplConnectivityCallback);
    switch (paramInt)
    {
    case 3: 
    case 4: 
    default: 
      Log.e("GnssLocationProvider", "Invalid status to release SUPL connection: " + paramInt);
      return;
    case 5: 
      native_agps_data_conn_failed();
      return;
    }
    native_agps_data_conn_closed();
  }
  
  private void handleRequestSuplConnection(InetAddress paramInetAddress)
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", String.format("requestSuplConnection, state=%s, address=%s", new Object[] { agpsDataConnStateAsString(), paramInetAddress }));
    }
    if (this.mAGpsDataConnectionState != 0) {
      return;
    }
    this.mAGpsDataConnectionIpAddr = paramInetAddress;
    this.mAGpsDataConnectionState = 1;
    paramInetAddress = new NetworkRequest.Builder();
    paramInetAddress.addTransportType(0);
    paramInetAddress.addCapability(1);
    paramInetAddress = paramInetAddress.build();
    this.mConnMgr.requestNetwork(paramInetAddress, this.mSuplConnectivityCallback, 6000000);
  }
  
  private void handleSetRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource)
  {
    this.mProviderRequest = paramProviderRequest;
    this.mWorkSource = paramWorkSource;
    updateRequirements();
  }
  
  private void handleUpdateLocation(Location paramLocation)
  {
    if (paramLocation.hasAccuracy()) {
      native_inject_location(paramLocation.getLatitude(), paramLocation.getLongitude(), paramLocation.getAccuracy());
    }
  }
  
  private void handleUpdateNetworkState(Network paramNetwork)
  {
    NetworkInfo localNetworkInfo = this.mConnMgr.getNetworkInfo(paramNetwork);
    if (localNetworkInfo == null) {
      return;
    }
    boolean bool2 = localNetworkInfo.isConnected();
    if (DEBUG) {
      Log.d("GnssLocationProvider", String.format("UpdateNetworkState, state=%s, connected=%s, info=%s, capabilities=%S", new Object[] { agpsDataConnStateAsString(), Boolean.valueOf(bool2), localNetworkInfo, this.mConnMgr.getNetworkCapabilities(paramNetwork) }));
    }
    boolean bool1;
    String str;
    if (native_is_agps_ril_supported())
    {
      bool1 = TelephonyManager.getDefault().getDataEnabled();
      if (localNetworkInfo.isAvailable())
      {
        str = getSelectedApn();
        paramNetwork = str;
        if (str == null) {
          paramNetwork = "dummy-apn";
        }
        native_update_network_state(bool2, localNetworkInfo.getType(), localNetworkInfo.isRoaming(), bool1, localNetworkInfo.getExtraInfo(), paramNetwork);
      }
    }
    for (;;)
    {
      if (this.mAGpsDataConnectionState == 1)
      {
        if (!bool2) {
          break label248;
        }
        str = localNetworkInfo.getExtraInfo();
        paramNetwork = str;
        if (str == null) {
          paramNetwork = "dummy-apn";
        }
        int i = getApnIpType(paramNetwork);
        setRouting();
        if (DEBUG) {
          Log.d("GnssLocationProvider", String.format("native_agps_data_conn_open: mAgpsApn=%s, mApnIpType=%s", new Object[] { paramNetwork, Integer.valueOf(i) }));
        }
        native_agps_data_conn_open(paramNetwork, i);
        this.mAGpsDataConnectionState = 2;
      }
      return;
      bool1 = false;
      break;
      if (DEBUG) {
        Log.d("GnssLocationProvider", "Skipped network state update because GPS HAL AGPS-RIL is not  supported");
      }
    }
    label248:
    handleReleaseSuplConnection(5);
  }
  
  private boolean hasCapability(int paramInt)
  {
    boolean bool = false;
    if ((this.mEngineCapabilities & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void hibernate()
  {
    stopNavigating();
    this.mAlarmManager.cancel(this.mTimeoutIntent);
    this.mAlarmManager.cancel(this.mWakeupIntent);
    long l = SystemClock.elapsedRealtime();
    this.mAlarmManager.set(2, this.mFixInterval + l, this.mWakeupIntent);
  }
  
  private boolean isDataNetworkConnected()
  {
    NetworkInfo localNetworkInfo = this.mConnMgr.getActiveNetworkInfo();
    if (localNetworkInfo != null) {}
    try
    {
      boolean bool = localNetworkInfo.isConnected();
      return bool;
    }
    catch (Exception localException)
    {
      Log.w("GnssLocationProvider", "Return false while checking failed.", localException);
    }
    return false;
    return false;
  }
  
  public static boolean isSupported()
  {
    return native_is_supported();
  }
  
  private final boolean isVerizon(String paramString1, String paramString2, String paramString3)
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "simOperator: " + paramString1);
    }
    if ((TextUtils.isEmpty(paramString1)) && (TextUtils.isEmpty(paramString2))) {}
    for (;;)
    {
      return false;
      int i = 0;
      while (i < VzwMccMncList.length)
      {
        if (((!TextUtils.isEmpty(paramString1)) && (paramString1.equals(VzwMccMncList[i]))) || ((!TextUtils.isEmpty(paramString2)) && (paramString2.startsWith(VzwMccMncList[i])) && ((TextUtils.isEmpty(VzwGid1List[i])) || (VzwGid1List[i].equals(paramString3)))))
        {
          if (DEBUG) {
            Log.d("GnssLocationProvider", "Verizon UICC");
          }
          return true;
        }
        i += 1;
      }
    }
  }
  
  /* Error */
  private boolean loadPropertiesFromFile(String paramString, Properties paramProperties)
  {
    // Byte code:
    //   0: new 1347	java/io/File
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 1348	java/io/File:<init>	(Ljava/lang/String;)V
    //   8: astore 4
    //   10: aconst_null
    //   11: astore_3
    //   12: new 1350	java/io/FileInputStream
    //   15: dup
    //   16: aload 4
    //   18: invokespecial 1353	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   21: astore 4
    //   23: aload_2
    //   24: aload 4
    //   26: invokevirtual 1357	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   29: aload 4
    //   31: invokestatic 1363	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   34: iconst_1
    //   35: ireturn
    //   36: aload_2
    //   37: invokestatic 1363	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   40: aload_3
    //   41: athrow
    //   42: astore_2
    //   43: ldc -40
    //   45: new 923	java/lang/StringBuilder
    //   48: dup
    //   49: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   52: ldc_w 1365
    //   55: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload_1
    //   59: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: invokestatic 1161	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   68: pop
    //   69: iconst_0
    //   70: ireturn
    //   71: astore_3
    //   72: aload 4
    //   74: astore_2
    //   75: goto -39 -> 36
    //   78: astore 4
    //   80: aload_3
    //   81: astore_2
    //   82: aload 4
    //   84: astore_3
    //   85: goto -49 -> 36
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	GnssLocationProvider
    //   0	88	1	paramString	String
    //   0	88	2	paramProperties	Properties
    //   11	30	3	localObject1	Object
    //   71	10	3	localObject2	Object
    //   84	1	3	localObject3	Object
    //   8	65	4	localObject4	Object
    //   78	5	4	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   0	10	42	java/io/IOException
    //   29	34	42	java/io/IOException
    //   36	42	42	java/io/IOException
    //   23	29	71	finally
    //   12	23	78	finally
  }
  
  private void loadPropertiesFromResource(Context paramContext, Properties paramProperties)
  {
    paramContext = paramContext.getResources().getStringArray(17236035);
    int j = paramContext.length;
    int i = 0;
    if (i < j)
    {
      String str = paramContext[i];
      if (DEBUG) {
        Log.d("GnssLocationProvider", "GpsParamsResource: " + str);
      }
      String[] arrayOfString = str.split("=");
      if (arrayOfString.length == 2) {
        paramProperties.setProperty(arrayOfString[0].trim().toUpperCase(), arrayOfString[1]);
      }
      for (;;)
      {
        i += 1;
        break;
        Log.w("GnssLocationProvider", "malformed contents: " + str);
      }
    }
  }
  
  private static native boolean native_add_geofence(int paramInt1, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private native void native_agps_data_conn_closed();
  
  private native void native_agps_data_conn_failed();
  
  private native void native_agps_data_conn_open(String paramString, int paramInt);
  
  private native void native_agps_ni_message(byte[] paramArrayOfByte, int paramInt);
  
  private native void native_agps_set_id(int paramInt, String paramString);
  
  private native void native_agps_set_ref_location_cellid(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private native void native_cleanup();
  
  private static native void native_configuration_update(String paramString);
  
  private native void native_delete_aiding_data(int paramInt);
  
  private native String native_get_internal_state();
  
  private native boolean native_init();
  
  private native void native_inject_location(double paramDouble1, double paramDouble2, float paramFloat);
  
  private native void native_inject_time(long paramLong1, long paramLong2, int paramInt);
  
  private native void native_inject_xtra_data(byte[] paramArrayOfByte, int paramInt);
  
  private static native boolean native_is_agps_ril_supported();
  
  private static native boolean native_is_geofence_supported();
  
  private static native boolean native_is_gnss_configuration_supported();
  
  private static native boolean native_is_measurement_supported();
  
  private static native boolean native_is_navigation_message_supported();
  
  private static native boolean native_is_supported();
  
  private static native boolean native_pause_geofence(int paramInt);
  
  private native int native_read_nmea(byte[] paramArrayOfByte, int paramInt);
  
  private native int native_read_sv_status(int[] paramArrayOfInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3);
  
  private static native boolean native_remove_geofence(int paramInt);
  
  private static native boolean native_resume_geofence(int paramInt1, int paramInt2);
  
  private native void native_send_ni_response(int paramInt1, int paramInt2);
  
  private native void native_set_agps_server(int paramInt1, String paramString, int paramInt2);
  
  private native boolean native_set_position_mode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private native boolean native_start();
  
  private native boolean native_start_measurement_collection();
  
  private native boolean native_start_navigation_message_collection();
  
  private native boolean native_stop();
  
  private native boolean native_stop_measurement_collection();
  
  private native boolean native_stop_navigation_message_collection();
  
  private native boolean native_supports_xtra();
  
  private native void native_update_network_state(boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2);
  
  private void releaseSuplConnection(int paramInt)
  {
    sendMessage(15, paramInt, null);
  }
  
  /* Error */
  private void reloadGpsProperties(Context paramContext, Properties paramProperties)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: getstatic 336	com/android/server/location/GnssLocationProvider:DEBUG	Z
    //   6: ifeq +32 -> 38
    //   9: ldc -40
    //   11: new 923	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   18: ldc_w 1414
    //   21: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_2
    //   25: invokevirtual 1417	java/util/Properties:size	()I
    //   28: invokevirtual 1197	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   31: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   34: invokestatic 1087	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   37: pop
    //   38: aload_0
    //   39: aload_1
    //   40: aload_2
    //   41: invokespecial 1419	com/android/server/location/GnssLocationProvider:loadPropertiesFromResource	(Landroid/content/Context;Ljava/util/Properties;)V
    //   44: iconst_0
    //   45: istore_3
    //   46: ldc_w 1421
    //   49: invokestatic 1423	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   52: astore_1
    //   53: aload_1
    //   54: invokestatic 1073	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   57: ifne +34 -> 91
    //   60: aload_0
    //   61: new 923	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   68: ldc_w 1425
    //   71: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: aload_1
    //   75: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: ldc -64
    //   80: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   86: aload_2
    //   87: invokespecial 1427	com/android/server/location/GnssLocationProvider:loadPropertiesFromFile	(Ljava/lang/String;Ljava/util/Properties;)Z
    //   90: istore_3
    //   91: iload_3
    //   92: ifne +11 -> 103
    //   95: aload_0
    //   96: ldc 93
    //   98: aload_2
    //   99: invokespecial 1427	com/android/server/location/GnssLocationProvider:loadPropertiesFromFile	(Ljava/lang/String;Ljava/util/Properties;)Z
    //   102: pop
    //   103: getstatic 336	com/android/server/location/GnssLocationProvider:DEBUG	Z
    //   106: ifeq +32 -> 138
    //   109: ldc -40
    //   111: new 923	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   118: ldc_w 1429
    //   121: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_2
    //   125: invokevirtual 1417	java/util/Properties:size	()I
    //   128: invokevirtual 1197	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   131: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   134: invokestatic 1087	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   137: pop
    //   138: ldc -82
    //   140: invokestatic 1423	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   143: astore_1
    //   144: aload_1
    //   145: invokestatic 1073	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   148: ifne +12 -> 160
    //   151: aload_2
    //   152: ldc_w 1430
    //   155: aload_1
    //   156: invokevirtual 1395	java/util/Properties:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;
    //   159: pop
    //   160: aload_0
    //   161: aload_2
    //   162: ldc_w 1432
    //   165: invokevirtual 1067	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   168: aload_2
    //   169: ldc_w 1434
    //   172: invokevirtual 1067	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   175: invokespecial 1438	com/android/server/location/GnssLocationProvider:setSuplHostPort	(Ljava/lang/String;Ljava/lang/String;)V
    //   178: aload_0
    //   179: aload_2
    //   180: ldc_w 1440
    //   183: invokevirtual 1067	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   186: putfield 1152	com/android/server/location/GnssLocationProvider:mC2KServerHost	Ljava/lang/String;
    //   189: aload_2
    //   190: ldc_w 1442
    //   193: invokevirtual 1067	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   196: astore_1
    //   197: aload_0
    //   198: getfield 1152	com/android/server/location/GnssLocationProvider:mC2KServerHost	Ljava/lang/String;
    //   201: ifnull +15 -> 216
    //   204: aload_1
    //   205: ifnull +11 -> 216
    //   208: aload_0
    //   209: aload_1
    //   210: invokestatic 1078	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   213: putfield 1154	com/android/server/location/GnssLocationProvider:mC2KServerPort	I
    //   216: invokestatic 1444	com/android/server/location/GnssLocationProvider:native_is_gnss_configuration_supported	()Z
    //   219: ifeq +138 -> 357
    //   222: new 1446	java/io/ByteArrayOutputStream
    //   225: dup
    //   226: sipush 4096
    //   229: invokespecial 1448	java/io/ByteArrayOutputStream:<init>	(I)V
    //   232: astore_1
    //   233: aload_2
    //   234: aload_1
    //   235: aconst_null
    //   236: invokevirtual 1452	java/util/Properties:store	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   239: aload_1
    //   240: invokevirtual 1453	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
    //   243: invokestatic 1455	com/android/server/location/GnssLocationProvider:native_configuration_update	(Ljava/lang/String;)V
    //   246: getstatic 336	com/android/server/location/GnssLocationProvider:DEBUG	Z
    //   249: ifeq +32 -> 281
    //   252: ldc -40
    //   254: new 923	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   261: ldc_w 1457
    //   264: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: aload_1
    //   268: invokevirtual 1453	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
    //   271: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   277: invokestatic 1087	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   280: pop
    //   281: aload_0
    //   282: getfield 357	com/android/server/location/GnssLocationProvider:mProperties	Ljava/util/Properties;
    //   285: ldc_w 1459
    //   288: invokevirtual 1067	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   291: astore_1
    //   292: aload_1
    //   293: ifnull +19 -> 312
    //   296: aload_1
    //   297: invokestatic 1078	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   300: iconst_1
    //   301: if_icmpne +74 -> 375
    //   304: iload 4
    //   306: istore_3
    //   307: aload_0
    //   308: iload_3
    //   309: putfield 646	com/android/server/location/GnssLocationProvider:mSuplEsEnabled	Z
    //   312: return
    //   313: astore 5
    //   315: ldc -40
    //   317: new 923	java/lang/StringBuilder
    //   320: dup
    //   321: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   324: ldc_w 1461
    //   327: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   330: aload_1
    //   331: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   337: invokestatic 884	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   340: pop
    //   341: goto -125 -> 216
    //   344: astore_1
    //   345: ldc -40
    //   347: ldc_w 1463
    //   350: invokestatic 884	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: goto -73 -> 281
    //   357: getstatic 336	com/android/server/location/GnssLocationProvider:DEBUG	Z
    //   360: ifeq -79 -> 281
    //   363: ldc -40
    //   365: ldc_w 1465
    //   368: invokestatic 1087	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   371: pop
    //   372: goto -91 -> 281
    //   375: iconst_0
    //   376: istore_3
    //   377: goto -70 -> 307
    //   380: astore_2
    //   381: ldc -40
    //   383: new 923	java/lang/StringBuilder
    //   386: dup
    //   387: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   390: ldc_w 1467
    //   393: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   396: aload_1
    //   397: invokevirtual 930	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: invokevirtual 936	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   403: invokestatic 884	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   406: pop
    //   407: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	408	0	this	GnssLocationProvider
    //   0	408	1	paramContext	Context
    //   0	408	2	paramProperties	Properties
    //   45	332	3	bool1	boolean
    //   1	304	4	bool2	boolean
    //   313	1	5	localNumberFormatException	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   208	216	313	java/lang/NumberFormatException
    //   222	281	344	java/io/IOException
    //   296	304	380	java/lang/NumberFormatException
    //   307	312	380	java/lang/NumberFormatException
  }
  
  private void reportAGpsStatus(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    switch (paramInt2)
    {
    default: 
      if (DEBUG) {
        Log.d("GnssLocationProvider", "Received Unknown AGPS status: " + paramInt2);
      }
      break;
    }
    do
    {
      do
      {
        do
        {
          return;
          if (DEBUG) {
            Log.d("GnssLocationProvider", "GPS_REQUEST_AGPS_DATA_CONN");
          }
          Log.v("GnssLocationProvider", "Received SUPL IP addr[]: " + Arrays.toString(paramArrayOfByte));
          Object localObject3 = null;
          Object localObject1 = null;
          if (paramArrayOfByte != null) {}
          try
          {
            InetAddress localInetAddress = InetAddress.getByAddress(paramArrayOfByte);
            localObject1 = localInetAddress;
            localObject3 = localInetAddress;
            if (DEBUG)
            {
              localObject3 = localInetAddress;
              Log.d("GnssLocationProvider", "IP address converted to: " + localInetAddress);
              localObject1 = localInetAddress;
            }
          }
          catch (UnknownHostException localUnknownHostException)
          {
            for (;;)
            {
              Log.e("GnssLocationProvider", "Bad IP Address: " + paramArrayOfByte, localUnknownHostException);
              Object localObject2 = localObject3;
            }
          }
          sendMessage(14, 0, localObject1);
          return;
          if (DEBUG) {
            Log.d("GnssLocationProvider", "GPS_RELEASE_AGPS_DATA_CONN");
          }
          releaseSuplConnection(2);
          return;
        } while (!DEBUG);
        Log.d("GnssLocationProvider", "GPS_AGPS_DATA_CONNECTED");
        return;
      } while (!DEBUG);
      Log.d("GnssLocationProvider", "GPS_AGPS_DATA_CONN_DONE");
      return;
    } while (!DEBUG);
    Log.d("GnssLocationProvider", "GPS_AGPS_DATA_CONN_FAILED");
  }
  
  private void reportGeofenceAddStatus(int paramInt1, int paramInt2)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    this.mGeofenceHardwareImpl.reportGeofenceAddStatus(paramInt1, getGeofenceStatus(paramInt2));
  }
  
  private void reportGeofencePauseStatus(int paramInt1, int paramInt2)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    this.mGeofenceHardwareImpl.reportGeofencePauseStatus(paramInt1, getGeofenceStatus(paramInt2));
  }
  
  private void reportGeofenceRemoveStatus(int paramInt1, int paramInt2)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    this.mGeofenceHardwareImpl.reportGeofenceRemoveStatus(paramInt1, getGeofenceStatus(paramInt2));
  }
  
  private void reportGeofenceResumeStatus(int paramInt1, int paramInt2)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    this.mGeofenceHardwareImpl.reportGeofenceResumeStatus(paramInt1, getGeofenceStatus(paramInt2));
  }
  
  private void reportGeofenceStatus(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2, float paramFloat3, long paramLong)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    Location localLocation = buildLocation(paramInt2, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2, paramFloat3, paramLong);
    paramInt2 = 1;
    if (paramInt1 == 2) {
      paramInt2 = 0;
    }
    this.mGeofenceHardwareImpl.reportGeofenceMonitorStatus(0, paramInt2, localLocation, FusedBatchOptions.SourceTechnologies.GNSS);
  }
  
  private void reportGeofenceTransition(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2, float paramFloat3, long paramLong1, int paramInt3, long paramLong2)
  {
    if (this.mGeofenceHardwareImpl == null) {
      this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }
    Location localLocation = buildLocation(paramInt2, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2, paramFloat3, paramLong1);
    this.mGeofenceHardwareImpl.reportGeofenceTransition(paramInt1, localLocation, paramInt3, paramLong2, 0, FusedBatchOptions.SourceTechnologies.GNSS);
  }
  
  private void reportLocation(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2, float paramFloat3, long paramLong)
  {
    Log.v("GnssLocationProvider", "reportLocation lat: " + paramDouble1 + " long: " + paramDouble2 + " timestamp: " + paramLong);
    for (;;)
    {
      synchronized (this.mLocation)
      {
        this.mLocationFlags = paramInt;
        if ((paramInt & 0x1) == 1)
        {
          this.mLocation.setLatitude(paramDouble1);
          this.mLocation.setLongitude(paramDouble2);
          this.mLocation.setTime(paramLong);
          this.mLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        if ((paramInt & 0x2) == 2)
        {
          this.mLocation.setAltitude(paramDouble3);
          if ((paramInt & 0x4) != 4) {
            break label436;
          }
          this.mLocation.setSpeed(paramFloat1);
          if ((paramInt & 0x8) != 8) {
            break label446;
          }
          this.mLocation.setBearing(paramFloat2);
          if ((paramInt & 0x10) != 16) {
            break label456;
          }
          this.mLocation.setAccuracy(paramFloat3);
          this.mLocation.setExtras(this.mLocationExtras);
        }
      }
      try
      {
        this.mILocationManager.reportLocation(this.mLocation, false);
        this.mLastFixTime = System.currentTimeMillis();
        if ((this.mTimeToFirstFix == 0) && ((paramInt & 0x1) == 1))
        {
          this.mTimeToFirstFix = ((int)(this.mLastFixTime - this.mFixRequestTime));
          Log.d("GnssLocationProvider", "TTFF: " + this.mTimeToFirstFix);
          this.mListenerHelper.onFirstFix(this.mTimeToFirstFix);
        }
        if (this.mSingleShot) {
          stopNavigating();
        }
        if ((this.mStarted) && (this.mStatus != 2))
        {
          if ((!hasCapability(1)) && (this.mFixInterval < 60000)) {
            this.mAlarmManager.cancel(this.mTimeoutIntent);
          }
          ??? = new Intent("android.location.GPS_FIX_CHANGE");
          ((Intent)???).putExtra("enabled", true);
          this.mContext.sendBroadcastAsUser((Intent)???, UserHandle.ALL);
          updateStatus(2, this.mSvCount);
        }
        if ((!hasCapability(1)) && (this.mStarted) && (this.mFixInterval > 10000))
        {
          if (DEBUG) {
            Log.d("GnssLocationProvider", "got fix, hibernating");
          }
          hibernate();
        }
        return;
        this.mLocation.removeAltitude();
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
        label436:
        this.mLocation.removeSpeed();
        continue;
        label446:
        this.mLocation.removeBearing();
        continue;
        label456:
        this.mLocation.removeAccuracy();
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("GnssLocationProvider", "RemoteException calling reportLocation");
        }
      }
    }
  }
  
  private void reportMeasurementData(GnssMeasurementsEvent paramGnssMeasurementsEvent)
  {
    this.mGnssMeasurementsProvider.onMeasurementsAvailable(paramGnssMeasurementsEvent);
  }
  
  private void reportNavigationMessage(GnssNavigationMessage paramGnssNavigationMessage)
  {
    this.mGnssNavigationMessageProvider.onNavigationMessageAvailable(paramGnssNavigationMessage);
  }
  
  private void reportNmea(long paramLong)
  {
    int i = native_read_nmea(this.mNmeaBuffer, this.mNmeaBuffer.length);
    String str = new String(this.mNmeaBuffer, 0, i);
    this.mListenerHelper.onNmeaReceived(paramLong, str);
  }
  
  private void reportStatus(int paramInt)
  {
    Log.v("GnssLocationProvider", "reportStatus status: " + paramInt);
    boolean bool = this.mNavigating;
    switch (paramInt)
    {
    }
    for (;;)
    {
      if (bool != this.mNavigating)
      {
        this.mListenerHelper.onStatusChanged(this.mNavigating);
        Intent localIntent = new Intent("android.location.GPS_ENABLED_CHANGE");
        localIntent.putExtra("enabled", this.mNavigating);
        this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
      }
      return;
      this.mNavigating = true;
      this.mEngineOn = true;
      continue;
      this.mNavigating = false;
      continue;
      this.mEngineOn = true;
      continue;
      this.mEngineOn = false;
      this.mNavigating = false;
    }
  }
  
  private void reportSvStatus()
  {
    int m = native_read_sv_status(this.mSvidWithFlags, this.mCn0s, this.mSvElevations, this.mSvAzimuths);
    this.mListenerHelper.onSvStatusChanged(m, this.mSvidWithFlags, this.mCn0s, this.mSvElevations, this.mSvAzimuths);
    if (VERBOSE) {
      Log.v("GnssLocationProvider", "SV count: " + m);
    }
    int j = 0;
    int i = 0;
    Object localObject;
    if (i < m)
    {
      int k = j;
      if ((this.mSvidWithFlags[i] & 0x4) != 0) {
        k = j + 1;
      }
      StringBuilder localStringBuilder;
      if (VERBOSE)
      {
        localStringBuilder = new StringBuilder().append("svid: ").append(this.mSvidWithFlags[i] >> 7).append(" cn0: ").append(this.mCn0s[i] / 10.0F).append(" elev: ").append(this.mSvElevations[i]).append(" azimuth: ").append(this.mSvAzimuths[i]);
        if ((this.mSvidWithFlags[i] & 0x1) != 0) {
          break label280;
        }
        localObject = "  ";
        label205:
        localStringBuilder = localStringBuilder.append((String)localObject);
        if ((this.mSvidWithFlags[i] & 0x2) != 0) {
          break label288;
        }
        localObject = "  ";
        label230:
        localStringBuilder = localStringBuilder.append((String)localObject);
        if ((this.mSvidWithFlags[i] & 0x4) != 0) {
          break label296;
        }
      }
      label280:
      label288:
      label296:
      for (localObject = "";; localObject = "U")
      {
        Log.v("GnssLocationProvider", (String)localObject);
        i += 1;
        j = k;
        break;
        localObject = " E";
        break label205;
        localObject = " A";
        break label230;
      }
    }
    updateStatus(this.mStatus, j);
    if ((this.mNavigating) && (this.mStatus == 2) && (this.mLastFixTime > 0L) && (System.currentTimeMillis() - this.mLastFixTime > 10000L))
    {
      localObject = new Intent("android.location.GPS_FIX_CHANGE");
      ((Intent)localObject).putExtra("enabled", false);
      this.mContext.sendBroadcastAsUser((Intent)localObject, UserHandle.ALL);
      updateStatus(1, this.mSvCount);
    }
  }
  
  private void requestRefLocation(int paramInt)
  {
    TelephonyManager localTelephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
    paramInt = localTelephonyManager.getPhoneType();
    if (paramInt == 1)
    {
      localGsmCellLocation = (GsmCellLocation)localTelephonyManager.getCellLocation();
      if ((localGsmCellLocation != null) && (localTelephonyManager.getNetworkOperator() != null) && (localTelephonyManager.getNetworkOperator().length() > 3))
      {
        i = Integer.parseInt(localTelephonyManager.getNetworkOperator().substring(0, 3));
        j = Integer.parseInt(localTelephonyManager.getNetworkOperator().substring(3));
        paramInt = localTelephonyManager.getNetworkType();
        if ((paramInt != 3) && (paramInt != 8)) {}
      }
    }
    while (paramInt != 2)
    {
      GsmCellLocation localGsmCellLocation;
      int i;
      int j;
      for (paramInt = 2;; paramInt = 1)
      {
        native_agps_set_ref_location_cellid(paramInt, i, j, localGsmCellLocation.getLac(), localGsmCellLocation.getCid());
        return;
        if ((paramInt == 9) || (paramInt == 10) || (paramInt == 15)) {
          break;
        }
      }
      Log.e("GnssLocationProvider", "Error getting cell location info.");
      return;
    }
    Log.e("GnssLocationProvider", "CDMA not supported.");
  }
  
  private void requestSetID(int paramInt)
  {
    Object localObject2 = (TelephonyManager)this.mContext.getSystemService("phone");
    int j = 0;
    String str = "";
    Object localObject1;
    int i;
    if ((paramInt & 0x1) == 1)
    {
      localObject1 = ((TelephonyManager)localObject2).getSubscriberId();
      if (localObject1 == null)
      {
        i = j;
        localObject1 = str;
      }
    }
    for (;;)
    {
      native_agps_set_id(i, (String)localObject1);
      return;
      i = 1;
      continue;
      localObject1 = str;
      i = j;
      if ((paramInt & 0x2) == 2)
      {
        localObject2 = ((TelephonyManager)localObject2).getLine1Number();
        localObject1 = str;
        i = j;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          i = 2;
        }
      }
    }
  }
  
  private void requestUtcTime()
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "utcTimeRequest");
    }
    sendMessage(5, 0, null);
  }
  
  private void sendMessage(int paramInt1, int paramInt2, Object paramObject)
  {
    this.mWakeLock.acquire();
    Log.i("GnssLocationProvider", "WakeLock acquired by sendMessage(" + paramInt1 + ", " + paramInt2 + ", " + paramObject + ")");
    this.mHandler.obtainMessage(paramInt1, paramInt2, 1, paramObject).sendToTarget();
  }
  
  private void setEngineCapabilities(int paramInt)
  {
    boolean bool2 = true;
    this.mEngineCapabilities = paramInt;
    if (hasCapability(16))
    {
      this.mOnDemandTimeInjection = true;
      requestUtcTime();
    }
    Object localObject = this.mGnssMeasurementsProvider;
    if ((paramInt & 0x40) == 64)
    {
      bool1 = true;
      ((GnssMeasurementsProvider)localObject).onCapabilitiesUpdated(bool1);
      localObject = this.mGnssNavigationMessageProvider;
      if ((paramInt & 0x80) != 128) {
        break label79;
      }
    }
    label79:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((GnssNavigationMessageProvider)localObject).onCapabilitiesUpdated(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  private void setGnssYearOfHardware(int paramInt)
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "setGnssYearOfHardware called with " + paramInt);
    }
    this.mYearOfHardware = paramInt;
  }
  
  private void setRouting()
  {
    if (this.mAGpsDataConnectionIpAddr == null) {
      return;
    }
    if (!this.mConnMgr.requestRouteToHostAddress(3, this.mAGpsDataConnectionIpAddr)) {
      Log.e("GnssLocationProvider", "Error requesting route to host: " + this.mAGpsDataConnectionIpAddr);
    }
    while (!DEBUG) {
      return;
    }
    Log.d("GnssLocationProvider", "Successfully requested route to host: " + this.mAGpsDataConnectionIpAddr);
  }
  
  private void setSuplHostPort(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      this.mSuplServerHost = paramString1;
    }
    if (paramString2 != null) {}
    try
    {
      this.mSuplServerPort = Integer.parseInt(paramString2);
      if ((this.mSuplServerHost != null) && (this.mSuplServerPort > 0) && (this.mSuplServerPort <= 65535)) {
        native_set_agps_server(1, this.mSuplServerHost, this.mSuplServerPort);
      }
      return;
    }
    catch (NumberFormatException paramString1)
    {
      for (;;)
      {
        Log.e("GnssLocationProvider", "unable to parse SUPL_PORT: " + paramString2);
      }
    }
  }
  
  private void startNavigating(boolean paramBoolean)
  {
    handleGpsRequest(true);
    if (!this.mStarted)
    {
      Log.d("GnssLocationProvider", "startNavigating, singleShot is " + paramBoolean);
      this.mTimeToFirstFix = 0;
      this.mLastFixTime = 0L;
      this.mStarted = true;
      this.mSingleShot = paramBoolean;
      this.mPositionMode = 0;
      boolean bool;
      String str;
      if (Settings.Global.getInt(this.mContext.getContentResolver(), "assisted_gps_enabled", 1) != 0)
      {
        bool = true;
        this.mPositionMode = getSuplMode(this.mProperties, bool, paramBoolean);
        if (DEBUG) {}
        switch (this.mPositionMode)
        {
        default: 
          str = "unknown";
          label137:
          Log.d("GnssLocationProvider", "setting position_mode to " + str);
          if (!hasCapability(1)) {
            break;
          }
        }
      }
      for (int i = this.mFixInterval;; i = 1000)
      {
        if (native_set_position_mode(this.mPositionMode, 0, i, 0, 0)) {
          break label243;
        }
        this.mStarted = false;
        Log.e("GnssLocationProvider", "set_position_mode failed in startNavigating()");
        return;
        bool = false;
        break;
        str = "standalone";
        break label137;
        str = "MS_ASSISTED";
        break label137;
        str = "MS_BASED";
        break label137;
      }
      label243:
      if (!native_start())
      {
        this.mStarted = false;
        Log.e("GnssLocationProvider", "native_start failed in startNavigating()");
        return;
      }
      updateStatus(1, 0);
      this.mFixRequestTime = System.currentTimeMillis();
      if ((!hasCapability(1)) && (this.mFixInterval >= 60000)) {
        this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + 60000L, this.mTimeoutIntent);
      }
    }
  }
  
  private void stopNavigating()
  {
    Log.d("GnssLocationProvider", "stopNavigating");
    if (this.mStarted)
    {
      handleGpsRequest(false);
      this.mStarted = false;
      this.mSingleShot = false;
      native_stop();
      this.mTimeToFirstFix = 0;
      this.mLastFixTime = 0L;
      this.mLocationFlags = 0;
      updateStatus(1, 0);
    }
  }
  
  private void subscriptionOrSimChanged(Context paramContext)
  {
    if (DEBUG) {
      Log.d("GnssLocationProvider", "received SIM related action: ");
    }
    ??? = (TelephonyManager)this.mContext.getSystemService("phone");
    String str1 = ((TelephonyManager)???).getSimOperator();
    String str2 = ((TelephonyManager)???).getSubscriberId();
    String str3 = ((TelephonyManager)???).getGroupIdLevel1();
    if (!TextUtils.isEmpty(str1)) {
      if (DEBUG) {
        Log.d("GnssLocationProvider", "SIM MCC/MNC is available: " + str1);
      }
    }
    while (!DEBUG) {
      synchronized (this.mLock)
      {
        try
        {
          if (isVerizon(str1, str2, str3))
          {
            loadPropertiesFromResource(paramContext, this.mProperties);
            SystemProperties.set("persist.sys.gps.lpp", this.mProperties.getProperty("LPP_PROFILE"));
          }
          for (;;)
          {
            reloadGpsProperties(paramContext, this.mProperties);
            this.mNIHandler.setSuplEsEnabled(this.mSuplEsEnabled);
            return;
            SystemProperties.set("persist.sys.gps.lpp", "");
          }
        }
        catch (RuntimeException localRuntimeException)
        {
          for (;;)
          {
            Log.e("GnssLocationProvider", "Unable to set SystemProperties for key: persist.sys.gps.lpp");
          }
        }
      }
    }
    Log.d("GnssLocationProvider", "SIM MCC/MNC is still not available");
  }
  
  private int translateToApnIpType(String paramString1, String paramString2)
  {
    if ("IP".equals(paramString1)) {
      return 1;
    }
    if ("IPV6".equals(paramString1)) {
      return 2;
    }
    if ("IPV4V6".equals(paramString1)) {
      return 3;
    }
    Log.e("GnssLocationProvider", String.format("Unknown IP Protocol: %s, for APN: %s", new Object[] { paramString1, paramString2 }));
    return 0;
  }
  
  private void updateClientUids(WorkSource paramWorkSource)
  {
    Object localObject = this.mClientSource.setReturningDiffs(paramWorkSource);
    if (localObject == null) {
      return;
    }
    paramWorkSource = localObject[0];
    localObject = localObject[1];
    int i;
    int k;
    int j;
    int m;
    if (paramWorkSource != null)
    {
      i = -1;
      k = 0;
      while (k < paramWorkSource.size())
      {
        j = i;
        try
        {
          m = paramWorkSource.get(k);
          j = i;
          this.mAppOpsService.startOperation(AppOpsManager.getToken(this.mAppOpsService), 2, m, paramWorkSource.getName(k));
          j = i;
          if (m != i)
          {
            i = m;
            j = i;
            this.mBatteryStats.noteStartGps(m);
            j = i;
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("GnssLocationProvider", "RemoteException", localRemoteException);
          }
        }
        k += 1;
        i = j;
      }
    }
    if (localObject != null)
    {
      i = -1;
      k = 0;
      while (k < ((WorkSource)localObject).size())
      {
        j = i;
        try
        {
          m = ((WorkSource)localObject).get(k);
          j = i;
          this.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAppOpsService), 2, m, ((WorkSource)localObject).getName(k));
          j = i;
          if (m != i)
          {
            i = m;
            j = i;
            this.mBatteryStats.noteStopGps(m);
            j = i;
          }
        }
        catch (RemoteException paramWorkSource)
        {
          for (;;)
          {
            Log.w("GnssLocationProvider", "RemoteException", paramWorkSource);
          }
        }
        k += 1;
        i = j;
      }
    }
  }
  
  private void updateLowPowerMode()
  {
    boolean bool2 = false;
    if (this.mPowerManager.isDeviceIdleMode()) {}
    for (boolean bool3 = this.mStopGps;; bool3 = false) {
      switch (Settings.Secure.getInt(this.mContext.getContentResolver(), "batterySaverGpsMode", 1))
      {
      default: 
        if (bool3 != this.mDisableGps)
        {
          this.mDisableGps = bool3;
          updateRequirements();
        }
        return;
      }
    }
    boolean bool1 = bool2;
    if (this.mPowerManager.isPowerSaveMode()) {
      if (!this.mPowerManager.isInteractive()) {
        break label102;
      }
    }
    label102:
    for (bool1 = bool2;; bool1 = true)
    {
      bool3 |= bool1;
      break;
    }
  }
  
  private void updateRequirements()
  {
    if ((this.mProviderRequest == null) || (this.mWorkSource == null)) {
      return;
    }
    boolean bool1 = false;
    boolean bool2 = bool1;
    Object localObject;
    if (this.mProviderRequest.locationRequests != null)
    {
      bool2 = bool1;
      if (this.mProviderRequest.locationRequests.size() > 0)
      {
        bool1 = true;
        localObject = this.mProviderRequest.locationRequests.iterator();
        while (((Iterator)localObject).hasNext()) {
          if (((LocationRequest)((Iterator)localObject).next()).getNumUpdates() != 1) {
            bool1 = false;
          }
        }
        bool2 = bool1;
        if (needMSACheck)
        {
          bool2 = bool1;
          if (!bool1)
          {
            localObject = this.mProperties.getProperty("SUPL_MODE");
            bool2 = bool1;
            if (!TextUtils.isEmpty((CharSequence)localObject)) {
              bool2 = bool1;
            }
          }
        }
      }
    }
    try
    {
      if (Integer.parseInt((String)localObject) == 2)
      {
        Log.d("GnssLocationProvider", "supl mode is MSA, set singleShot true");
        bool2 = true;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      do
      {
        do
        {
          for (;;)
          {
            Log.e("GnssLocationProvider", "unable to parse SUPL_MODE: " + (String)localObject);
            bool2 = bool1;
          }
        } while (!isEnabled());
        updateClientUids(this.mWorkSource);
        this.mFixInterval = ((int)this.mProviderRequest.interval);
        if (this.mFixInterval != this.mProviderRequest.interval)
        {
          Log.w("GnssLocationProvider", "interval overflow: " + this.mProviderRequest.interval);
          this.mFixInterval = Integer.MAX_VALUE;
        }
        if ((this.mStarted) && (hasCapability(1)))
        {
          if (!native_set_position_mode(this.mPositionMode, 0, this.mFixInterval, 0, 0)) {
            Log.e("GnssLocationProvider", "set_position_mode failed in setMinTime()");
          }
          handleGpsRequest(true);
          return;
        }
      } while (this.mStarted);
      startNavigating(bool2);
    }
    needMSACheck = false;
    if (DEBUG) {
      Log.d("GnssLocationProvider", "setRequest " + this.mProviderRequest);
    }
    if ((!this.mProviderRequest.reportLocation) || (this.mDisableGps))
    {
      updateClientUids(new WorkSource());
      stopNavigating();
      this.mAlarmManager.cancel(this.mWakeupIntent);
      this.mAlarmManager.cancel(this.mTimeoutIntent);
      return;
    }
  }
  
  private void updateStatus(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != this.mStatus) || (paramInt2 != this.mSvCount))
    {
      this.mStatus = paramInt1;
      this.mSvCount = paramInt2;
      this.mLocationExtras.putInt("satellites", paramInt2);
      this.mStatusUpdateTime = SystemClock.elapsedRealtime();
    }
  }
  
  private void xtraDownloadRequest()
  {
    Log.d("GnssLocationProvider", "xtraDownloadRequest");
    sendMessage(6, 0, null);
  }
  
  public void disable()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mEnabled;
      if (!bool) {
        return;
      }
      this.mEnabled = false;
      sendMessage(2, 0, null);
      return;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new StringBuilder();
    paramFileDescriptor.append("  mFixInterval=").append(this.mFixInterval).append('\n');
    paramFileDescriptor.append("  mDisableGps (battery saver mode)=").append(this.mDisableGps).append('\n');
    paramFileDescriptor.append("  mEngineCapabilities=0x").append(Integer.toHexString(this.mEngineCapabilities));
    paramFileDescriptor.append(" ( ");
    if (hasCapability(1)) {
      paramFileDescriptor.append("SCHEDULING ");
    }
    if (hasCapability(2)) {
      paramFileDescriptor.append("MSB ");
    }
    if (hasCapability(4)) {
      paramFileDescriptor.append("MSA ");
    }
    if (hasCapability(8)) {
      paramFileDescriptor.append("SINGLE_SHOT ");
    }
    if (hasCapability(16)) {
      paramFileDescriptor.append("ON_DEMAND_TIME ");
    }
    if (hasCapability(32)) {
      paramFileDescriptor.append("GEOFENCING ");
    }
    if (hasCapability(64)) {
      paramFileDescriptor.append("MEASUREMENTS ");
    }
    if (hasCapability(128)) {
      paramFileDescriptor.append("NAV_MESSAGES ");
    }
    paramFileDescriptor.append(")\n");
    paramFileDescriptor.append(native_get_internal_state());
    paramPrintWriter.append(paramFileDescriptor);
  }
  
  public void enable()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mEnabled;
      if (bool) {
        return;
      }
      this.mEnabled = true;
      sendMessage(2, 1, null);
      return;
    }
  }
  
  public GnssMeasurementsProvider getGnssMeasurementsProvider()
  {
    return this.mGnssMeasurementsProvider;
  }
  
  public GnssNavigationMessageProvider getGnssNavigationMessageProvider()
  {
    return this.mGnssNavigationMessageProvider;
  }
  
  public IGnssStatusProvider getGnssStatusProvider()
  {
    return this.mGnssStatusProvider;
  }
  
  public GnssSystemInfoProvider getGnssSystemInfoProvider()
  {
    new GnssSystemInfoProvider()
    {
      public int getGnssYearOfHardware()
      {
        return GnssLocationProvider.-get16(GnssLocationProvider.this);
      }
    };
  }
  
  public IGpsGeofenceHardware getGpsGeofenceProxy()
  {
    return this.mGpsGeofenceBinder;
  }
  
  public String getName()
  {
    return "gps";
  }
  
  public INetInitiatedListener getNetInitiatedListener()
  {
    return this.mNetInitiatedListener;
  }
  
  public ProviderProperties getProperties()
  {
    return PROPERTIES;
  }
  
  public int getStatus(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.putInt("satellites", this.mSvCount);
    }
    return this.mStatus;
  }
  
  public long getStatusUpdateTime()
  {
    return this.mStatusUpdateTime;
  }
  
  public boolean isEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mEnabled;
      return bool;
    }
  }
  
  public void reportNiNotification(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString1, String paramString2, int paramInt6, int paramInt7, String paramString3)
  {
    Log.i("GnssLocationProvider", "reportNiNotification: entered");
    Log.i("GnssLocationProvider", "notificationId: " + paramInt1 + ", niType: " + paramInt2 + ", notifyFlags: " + paramInt3 + ", timeout: " + paramInt4 + ", defaultResponse: " + paramInt5);
    Log.i("GnssLocationProvider", "requestorId: " + paramString1 + ", text: " + paramString2 + ", requestorIdEncoding: " + paramInt6 + ", textEncoding: " + paramInt7);
    GpsNetInitiatedHandler.GpsNiNotification localGpsNiNotification = new GpsNetInitiatedHandler.GpsNiNotification();
    localGpsNiNotification.notificationId = paramInt1;
    localGpsNiNotification.niType = paramInt2;
    boolean bool;
    if ((paramInt3 & 0x1) != 0) {
      bool = true;
    }
    for (;;)
    {
      localGpsNiNotification.needNotify = bool;
      if ((paramInt3 & 0x2) != 0)
      {
        bool = true;
        label183:
        localGpsNiNotification.needVerify = bool;
        if ((paramInt3 & 0x4) == 0) {
          break label368;
        }
        bool = true;
        localGpsNiNotification.privacyOverride = bool;
        localGpsNiNotification.timeout = paramInt4;
        localGpsNiNotification.defaultResponse = paramInt5;
        localGpsNiNotification.requestorId = paramString1;
        localGpsNiNotification.text = paramString2;
        localGpsNiNotification.requestorIdEncoding = paramInt6;
        localGpsNiNotification.textEncoding = paramInt7;
        paramString2 = new Bundle();
        paramString1 = paramString3;
        if (paramString3 == null) {
          paramString1 = "";
        }
        paramString3 = new Properties();
      }
      try
      {
        paramString3.load(new StringReader(paramString1));
        paramString1 = paramString3.entrySet().iterator();
        for (;;)
        {
          if (paramString1.hasNext())
          {
            paramString3 = (Map.Entry)paramString1.next();
            paramString2.putString((String)paramString3.getKey(), (String)paramString3.getValue());
            continue;
            bool = false;
            break;
            bool = false;
            break label183;
            label368:
            bool = false;
          }
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          Log.e("GnssLocationProvider", "reportNiNotification cannot parse extras data: " + paramString1);
        }
        localGpsNiNotification.extras = paramString2;
        this.mNIHandler.handleNiNotification(localGpsNiNotification);
      }
    }
  }
  
  public boolean sendExtraCommand(String paramString, Bundle paramBundle)
  {
    long l = Binder.clearCallingIdentity();
    boolean bool = false;
    if ("delete_aiding_data".equals(paramString))
    {
      Log.w("GnssLocationProvider", "delete_aiding_data");
      bool = deleteAidingData(paramBundle);
    }
    for (;;)
    {
      Binder.restoreCallingIdentity(l);
      return bool;
      if ("force_time_injection".equals(paramString))
      {
        requestUtcTime();
        bool = true;
      }
      else if ("force_xtra_injection".equals(paramString))
      {
        if (this.mSupportsXtra)
        {
          xtraDownloadRequest();
          bool = true;
        }
      }
      else if ("force_location_inject".equals(paramString))
      {
        paramString = new Location("gps");
        paramString.setLatitude(paramBundle.getDouble("latitude"));
        paramString.setLongitude(paramBundle.getDouble("longitude"));
        paramString.setAccuracy(paramBundle.getFloat("accuracy"));
        Log.w("GnssLocationProvider", "force_inject_position");
        sendMessage(7, 0, paramString);
      }
      else if ("set_position_mode".equals(paramString))
      {
        paramString = paramBundle.getString("host");
        int i = paramBundle.getInt("port");
        int j = paramBundle.getInt("mode");
        Log.d("GnssLocationProvider", "host:" + paramString + ", port:" + i + ", mode:" + j);
        this.mProperties.setProperty("SUPL_HOST", paramString);
        this.mProperties.setProperty("SUPL_PORT", Integer.toString(i));
        this.mProperties.setProperty("SUPL_MODE", Integer.toString(j));
        if (j == 2)
        {
          Log.d("GnssLocationProvider", "MSA mode ,need check singleShot");
          needMSACheck = true;
        }
        setSuplHostPort(paramString, Integer.toString(i));
        native_set_position_mode(getSuplMode(this.mProperties, true, true), 0, this.mFixInterval, 0, 0);
      }
      else
      {
        Log.w("GnssLocationProvider", "sendExtraCommand: unknown command " + paramString);
      }
    }
  }
  
  public void setRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource)
  {
    sendMessage(3, 0, new GpsRequest(paramProviderRequest, paramWorkSource));
  }
  
  private static final class BackOff
  {
    private static final int MULTIPLIER = 2;
    private long mCurrentIntervalMillis;
    private final long mInitIntervalMillis;
    private final long mMaxIntervalMillis;
    
    public BackOff(long paramLong1, long paramLong2)
    {
      this.mInitIntervalMillis = paramLong1;
      this.mMaxIntervalMillis = paramLong2;
      this.mCurrentIntervalMillis = (this.mInitIntervalMillis / 2L);
    }
    
    public long nextBackoffMillis()
    {
      if (this.mCurrentIntervalMillis > this.mMaxIntervalMillis) {
        return this.mMaxIntervalMillis;
      }
      this.mCurrentIntervalMillis *= 2L;
      return this.mCurrentIntervalMillis;
    }
    
    public void reset()
    {
      this.mCurrentIntervalMillis = (this.mInitIntervalMillis / 2L);
    }
  }
  
  public static abstract interface GnssSystemInfoProvider
  {
    public abstract int getGnssYearOfHardware();
  }
  
  private static class GpsRequest
  {
    public ProviderRequest request;
    public WorkSource source;
    
    public GpsRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource)
    {
      this.request = paramProviderRequest;
      this.source = paramWorkSource;
    }
  }
  
  private final class NetworkLocationListener
    implements LocationListener
  {
    private NetworkLocationListener() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      if ("network".equals(paramLocation.getProvider())) {
        GnssLocationProvider.-wrap21(GnssLocationProvider.this, paramLocation);
      }
    }
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
  
  private final class ProviderHandler
    extends Handler
  {
    public ProviderHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    private void handleInitialize()
    {
      GnssLocationProvider.-wrap28(GnssLocationProvider.this, GnssLocationProvider.-get3(GnssLocationProvider.this), GnssLocationProvider.-get13(GnssLocationProvider.this));
      SubscriptionManager.from(GnssLocationProvider.-get3(GnssLocationProvider.this)).addOnSubscriptionsChangedListener(GnssLocationProvider.-get12(GnssLocationProvider.this));
      Object localObject;
      if (GnssLocationProvider.-wrap1())
      {
        localObject = new IntentFilter();
        ((IntentFilter)localObject).addAction("android.intent.action.DATA_SMS_RECEIVED");
        ((IntentFilter)localObject).addDataScheme("sms");
        ((IntentFilter)localObject).addDataAuthority("localhost", "7275");
        GnssLocationProvider.-get3(GnssLocationProvider.this).registerReceiver(GnssLocationProvider.-get1(GnssLocationProvider.this), (IntentFilter)localObject, null, this);
        localObject = new IntentFilter();
        ((IntentFilter)localObject).addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
      }
      for (;;)
      {
        try
        {
          ((IntentFilter)localObject).addDataType("application/vnd.omaloc-supl-init");
          GnssLocationProvider.-get3(GnssLocationProvider.this).registerReceiver(GnssLocationProvider.-get1(GnssLocationProvider.this), (IntentFilter)localObject, null, this);
          localObject = new IntentFilter();
          ((IntentFilter)localObject).addAction("com.android.internal.location.ALARM_WAKEUP");
          ((IntentFilter)localObject).addAction("com.android.internal.location.ALARM_TIMEOUT");
          ((IntentFilter)localObject).addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
          ((IntentFilter)localObject).addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
          ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_OFF");
          ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_ON");
          ((IntentFilter)localObject).addAction("android.intent.action.SIM_STATE_CHANGED");
          GnssLocationProvider.-get3(GnssLocationProvider.this).registerReceiver(GnssLocationProvider.-get1(GnssLocationProvider.this), (IntentFilter)localObject, null, this);
          localObject = new NetworkRequest.Builder();
          ((NetworkRequest.Builder)localObject).addTransportType(0);
          ((NetworkRequest.Builder)localObject).addTransportType(1);
          localObject = ((NetworkRequest.Builder)localObject).build();
          GnssLocationProvider.-get2(GnssLocationProvider.this).registerNetworkCallback((NetworkRequest)localObject, GnssLocationProvider.-get8(GnssLocationProvider.this));
          localObject = (LocationManager)GnssLocationProvider.-get3(GnssLocationProvider.this).getSystemService("location");
          LocationRequest localLocationRequest = LocationRequest.createFromDeprecatedProvider("passive", 0L, 0.0F, false);
          localLocationRequest.setHideFromAppOps(true);
          ((LocationManager)localObject).requestLocationUpdates(localLocationRequest, new GnssLocationProvider.NetworkLocationListener(GnssLocationProvider.this, null), getLooper());
          return;
        }
        catch (IntentFilter.MalformedMimeTypeException localMalformedMimeTypeException)
        {
          Log.w("GnssLocationProvider", "Malformed SUPL init mime type");
          continue;
        }
        if (GnssLocationProvider.-get0()) {
          Log.d("GnssLocationProvider", "Skipped registration for SMS/WAP-PUSH messages because AGPS Ril in GPS HAL is not supported");
        }
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      switch (i)
      {
      }
      for (;;)
      {
        if (paramMessage.arg2 == 1)
        {
          GnssLocationProvider.-get14(GnssLocationProvider.this).release();
          Log.i("GnssLocationProvider", "WakeLock released by handleMessage(" + i + ", " + paramMessage.arg1 + ", " + paramMessage.obj + ")");
        }
        return;
        if (paramMessage.arg1 == 1)
        {
          GnssLocationProvider.-wrap16(GnssLocationProvider.this);
        }
        else
        {
          GnssLocationProvider.-wrap14(GnssLocationProvider.this);
          continue;
          GnssLocationProvider.GpsRequest localGpsRequest = (GnssLocationProvider.GpsRequest)paramMessage.obj;
          GnssLocationProvider.-wrap20(GnssLocationProvider.this, localGpsRequest.request, localGpsRequest.source);
          continue;
          GnssLocationProvider.-wrap22(GnssLocationProvider.this, (Network)paramMessage.obj);
          continue;
          GnssLocationProvider.-wrap19(GnssLocationProvider.this, (InetAddress)paramMessage.obj);
          continue;
          GnssLocationProvider.-wrap18(GnssLocationProvider.this, paramMessage.arg1);
          continue;
          GnssLocationProvider.-wrap17(GnssLocationProvider.this);
          continue;
          GnssLocationProvider.-wrap15(GnssLocationProvider.this);
          continue;
          GnssLocationProvider.-set1(GnssLocationProvider.this, 2);
          continue;
          GnssLocationProvider.-set0(GnssLocationProvider.this, 2);
          continue;
          GnssLocationProvider.-wrap21(GnssLocationProvider.this, (Location)paramMessage.obj);
          continue;
          GnssLocationProvider.-wrap32(GnssLocationProvider.this, GnssLocationProvider.-get3(GnssLocationProvider.this));
          continue;
          handleInitialize();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GnssLocationProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */