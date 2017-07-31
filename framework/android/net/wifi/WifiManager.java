package android.net.wifi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.util.SeempLog;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import com.android.server.net.NetworkPinner;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WifiManager
{
  public static final String ACTION_AUTH_PASSWORD_WRONG = "Auth_password_wrong";
  public static final String ACTION_PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK";
  public static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = "android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE";
  public static final String ACTION_WIFI_DISCONNECT_IN_PROGRESS = "wifi_disconnect_in_progress";
  private static final int BASE = 151552;
  @Deprecated
  public static final String BATCHED_SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.BATCHED_RESULTS";
  public static final int BUSY = 2;
  public static final int CANCEL_WPS = 151566;
  public static final int CANCEL_WPS_FAILED = 151567;
  public static final int CANCEL_WPS_SUCCEDED = 151568;
  public static final int CHANGE_REASON_ADDED = 0;
  public static final int CHANGE_REASON_CONFIG_CHANGE = 2;
  public static final int CHANGE_REASON_REMOVED = 1;
  public static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
  public static final int CONNECT_NETWORK = 151553;
  public static final int CONNECT_NETWORK_FAILED = 151554;
  public static final int CONNECT_NETWORK_SUCCEEDED = 151555;
  public static final int DATA_ACTIVITY_IN = 1;
  public static final int DATA_ACTIVITY_INOUT = 3;
  public static final int DATA_ACTIVITY_NONE = 0;
  public static final int DATA_ACTIVITY_NOTIFICATION = 1;
  public static final int DATA_ACTIVITY_OUT = 2;
  public static final boolean DEFAULT_POOR_NETWORK_AVOIDANCE_ENABLED = false;
  public static final int DISABLE_NETWORK = 151569;
  public static final int DISABLE_NETWORK_FAILED = 151570;
  public static final int DISABLE_NETWORK_SUCCEEDED = 151571;
  public static final int ERROR = 0;
  public static final int ERROR_AUTHENTICATING = 1;
  public static final String EXTRA_BSSID = "bssid";
  public static final String EXTRA_CHANGE_REASON = "changeReason";
  public static final String EXTRA_LINK_PROPERTIES = "linkProperties";
  public static final String EXTRA_MULTIPLE_NETWORKS_CHANGED = "multipleChanges";
  public static final String EXTRA_NETWORK_CAPABILITIES = "networkCapabilities";
  public static final String EXTRA_NETWORK_INFO = "networkInfo";
  public static final String EXTRA_NEW_RSSI = "newRssi";
  public static final String EXTRA_NEW_STATE = "newState";
  public static final String EXTRA_PASSPOINT_ICON_BSSID = "bssid";
  public static final String EXTRA_PASSPOINT_ICON_DATA = "icon";
  public static final String EXTRA_PASSPOINT_ICON_FILE = "file";
  public static final String EXTRA_PASSPOINT_WNM_BSSID = "bssid";
  public static final String EXTRA_PASSPOINT_WNM_DELAY = "delay";
  public static final String EXTRA_PASSPOINT_WNM_ESS = "ess";
  public static final String EXTRA_PASSPOINT_WNM_METHOD = "method";
  public static final String EXTRA_PASSPOINT_WNM_PPOINT_MATCH = "match";
  public static final String EXTRA_PASSPOINT_WNM_URL = "url";
  public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
  public static final String EXTRA_PREVIOUS_WIFI_STATE = "previous_wifi_state";
  public static final String EXTRA_RESULTS_UPDATED = "resultsUpdated";
  public static final String EXTRA_SCAN_AVAILABLE = "scan_enabled";
  public static final String EXTRA_SUPPLICANT_CONNECTED = "connected";
  public static final String EXTRA_SUPPLICANT_ERROR = "supplicantError";
  public static final String EXTRA_WIFI_AP_FAILURE_REASON = "wifi_ap_error_code";
  public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
  public static final String EXTRA_WIFI_CONFIGURATION = "wifiConfiguration";
  public static final String EXTRA_WIFI_CREDENTIAL_EVENT_TYPE = "et";
  public static final String EXTRA_WIFI_CREDENTIAL_SSID = "ssid";
  public static final String EXTRA_WIFI_INFO = "wifiInfo";
  public static final String EXTRA_WIFI_STATE = "wifi_state";
  public static final int FORGET_NETWORK = 151556;
  public static final int FORGET_NETWORK_FAILED = 151557;
  public static final int FORGET_NETWORK_SUCCEEDED = 151558;
  public static final int INVALID_ARGS = 8;
  private static final int INVALID_KEY = 0;
  public static final int IN_PROGRESS = 1;
  public static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
  private static final int MAX_ACTIVE_LOCKS = 50;
  private static final int MAX_RSSI = -55;
  private static final int MIN_RSSI = -100;
  public static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED";
  public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";
  public static final int NOT_AUTHORIZED = 9;
  public static final String PASSPOINT_ICON_RECEIVED_ACTION = "android.net.wifi.PASSPOINT_ICON_RECEIVED";
  public static final String PASSPOINT_WNM_FRAME_RECEIVED_ACTION = "android.net.wifi.PASSPOINT_WNM_FRAME_RECEIVED";
  public static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED";
  public static final int RSSI_LEVELS = 5;
  public static final int RSSI_PKTCNT_FETCH = 151572;
  public static final int RSSI_PKTCNT_FETCH_FAILED = 151574;
  public static final int RSSI_PKTCNT_FETCH_SUCCEEDED = 151573;
  public static final int SAP_START_FAILURE_GENERAL = 0;
  public static final int SAP_START_FAILURE_NO_CHANNEL = 1;
  public static final int SAVE_NETWORK = 151559;
  public static final int SAVE_NETWORK_FAILED = 151560;
  public static final int SAVE_NETWORK_SUCCEEDED = 151561;
  public static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS";
  public static final int START_WPS = 151562;
  public static final int START_WPS_SUCCEEDED = 151563;
  public static final String SUPPLICANT_CONNECTION_CHANGE_ACTION = "android.net.wifi.supplicant.CONNECTION_CHANGE";
  public static final String SUPPLICANT_STATE_CHANGED_ACTION = "android.net.wifi.supplicant.STATE_CHANGE";
  private static final String TAG = "WifiManager";
  public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
  public static final int WIFI_AP_STATE_DISABLED = 11;
  public static final int WIFI_AP_STATE_DISABLING = 10;
  public static final int WIFI_AP_STATE_ENABLED = 13;
  public static final int WIFI_AP_STATE_ENABLING = 12;
  public static final int WIFI_AP_STATE_FAILED = 14;
  public static final int WIFI_AP_STATE_RESTART = 15;
  public static final String WIFI_AP_SUB_SYSTEM_RESTART = "android.net.wifi.WIFI_AP_SUB_SYSTEM_RESTART";
  public static final String WIFI_CREDENTIAL_CHANGED_ACTION = "android.net.wifi.WIFI_CREDENTIAL_CHANGED";
  public static final int WIFI_CREDENTIAL_FORGOT = 1;
  public static final int WIFI_CREDENTIAL_SAVED = 0;
  public static final int WIFI_FEATURE_ADDITIONAL_STA = 2048;
  public static final int WIFI_FEATURE_AP_STA = 32768;
  public static final int WIFI_FEATURE_BATCH_SCAN = 512;
  public static final int WIFI_FEATURE_D2AP_RTT = 256;
  public static final int WIFI_FEATURE_D2D_RTT = 128;
  public static final int WIFI_FEATURE_EPR = 16384;
  public static final int WIFI_FEATURE_HAL_EPNO = 262144;
  public static final int WIFI_FEATURE_INFRA = 1;
  public static final int WIFI_FEATURE_INFRA_5G = 2;
  public static final int WIFI_FEATURE_LINK_LAYER_STATS = 65536;
  public static final int WIFI_FEATURE_LOGGER = 131072;
  public static final int WIFI_FEATURE_MOBILE_HOTSPOT = 16;
  public static final int WIFI_FEATURE_NAN = 64;
  public static final int WIFI_FEATURE_P2P = 8;
  public static final int WIFI_FEATURE_PASSPOINT = 4;
  public static final int WIFI_FEATURE_PNO = 1024;
  public static final int WIFI_FEATURE_SCANNER = 32;
  public static final int WIFI_FEATURE_TDLS = 4096;
  public static final int WIFI_FEATURE_TDLS_OFFCHANNEL = 8192;
  public static final int WIFI_FREQUENCY_BAND_2GHZ = 2;
  public static final int WIFI_FREQUENCY_BAND_5GHZ = 1;
  public static final int WIFI_FREQUENCY_BAND_AUTO = 0;
  public static final int WIFI_MODE_FULL = 1;
  public static final int WIFI_MODE_FULL_HIGH_PERF = 3;
  public static final int WIFI_MODE_NO_LOCKS_HELD = 0;
  public static final int WIFI_MODE_SCAN_ONLY = 2;
  public static final String WIFI_SCAN_AVAILABLE = "wifi_scan_available";
  public static final String WIFI_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";
  public static final int WIFI_STATE_DISABLED = 1;
  public static final int WIFI_STATE_DISABLING = 0;
  public static final int WIFI_STATE_ENABLED = 3;
  public static final int WIFI_STATE_ENABLING = 2;
  public static final int WIFI_STATE_UNKNOWN = 4;
  public static final int WPS_AUTH_FAILURE = 6;
  public static final int WPS_COMPLETED = 151565;
  public static final int WPS_FAILED = 151564;
  public static final int WPS_OVERLAP_ERROR = 3;
  public static final int WPS_TIMED_OUT = 7;
  public static final int WPS_TKIP_ONLY_PROHIBITED = 5;
  public static final int WPS_WEP_PROHIBITED = 4;
  private static final Object sServiceHandlerDispatchLock = new Object();
  private int mActiveLockCount;
  private AsyncChannel mAsyncChannel;
  private CountDownLatch mConnected;
  private Context mContext;
  private int mListenerKey = 1;
  private final SparseArray mListenerMap = new SparseArray();
  private final Object mListenerMapLock = new Object();
  private Looper mLooper;
  IWifiManager mService;
  private final int mTargetSdkVersion;
  
  public WifiManager(Context paramContext, IWifiManager paramIWifiManager, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mService = paramIWifiManager;
    this.mLooper = paramLooper;
    this.mTargetSdkVersion = paramContext.getApplicationInfo().targetSdkVersion;
  }
  
  private int addOrUpdateNetwork(WifiConfiguration paramWifiConfiguration)
  {
    try
    {
      int i = this.mService.addOrUpdateNetwork(paramWifiConfiguration);
      return i;
    }
    catch (RemoteException paramWifiConfiguration)
    {
      throw paramWifiConfiguration.rethrowFromSystemServer();
    }
  }
  
  public static int calculateSignalLevel(int paramInt1, int paramInt2)
  {
    if (paramInt1 <= -100) {
      return 0;
    }
    if (paramInt1 >= -55) {
      return paramInt2 - 1;
    }
    if (paramInt2 == 5)
    {
      int i;
      if (paramInt1 >= -64) {
        i = paramInt2 - 1;
      }
      do
      {
        return i;
        if ((paramInt1 >= -74) && (paramInt1 <= -65)) {
          return paramInt2 - 2;
        }
        if ((paramInt1 >= -82) && (paramInt1 <= -75)) {
          return paramInt2 - 3;
        }
        i = paramInt2;
      } while (paramInt1 > -83);
      return paramInt2 - 4;
    }
    float f = paramInt2 - 1;
    return (int)((paramInt1 + 100) * f / 45.0F);
  }
  
  public static int compareSignalLevel(int paramInt1, int paramInt2)
  {
    return paramInt1 - paramInt2;
  }
  
  private AsyncChannel getChannel()
  {
    try
    {
      if (this.mAsyncChannel != null) {
        break label90;
      }
      Messenger localMessenger1 = getWifiServiceMessenger();
      if (localMessenger1 == null) {
        throw new IllegalStateException("getWifiServiceMessenger() returned null!  This is invalid.");
      }
    }
    finally {}
    this.mAsyncChannel = new AsyncChannel();
    this.mConnected = new CountDownLatch(1);
    ServiceHandler localServiceHandler = new ServiceHandler(this.mLooper);
    this.mAsyncChannel.connect(this.mContext, localServiceHandler, localMessenger2);
    try
    {
      this.mConnected.await();
      label90:
      AsyncChannel localAsyncChannel = this.mAsyncChannel;
      return localAsyncChannel;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        Log.e("WifiManager", "interrupted wait at init");
      }
    }
  }
  
  private int getSupportedFeatures()
  {
    try
    {
      int i = this.mService.getSupportedFeatures();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private boolean isFeatureSupported(int paramInt)
  {
    return (getSupportedFeatures() & paramInt) == paramInt;
  }
  
  private int putListener(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    synchronized (this.mListenerMapLock)
    {
      int i;
      do
      {
        i = this.mListenerKey;
        this.mListenerKey = (i + 1);
      } while (i == 0);
      this.mListenerMap.put(i, paramObject);
      return i;
    }
  }
  
  private Object removeListener(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    synchronized (this.mListenerMapLock)
    {
      Object localObject2 = this.mListenerMap.get(paramInt);
      this.mListenerMap.remove(paramInt);
      return localObject2;
    }
  }
  
  public int addNetwork(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration == null) {
      return -1;
    }
    paramWifiConfiguration.networkId = -1;
    return addOrUpdateNetwork(paramWifiConfiguration);
  }
  
  public int addPasspointManagementObject(String paramString)
  {
    try
    {
      int i = this.mService.addPasspointManagementObject(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean addToBlacklist(String paramString)
  {
    try
    {
      this.mService.addToBlacklist(paramString);
      return true;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public WifiConfiguration buildWifiConfig(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    try
    {
      paramString1 = this.mService.buildWifiConfig(paramString1, paramString2, paramArrayOfByte);
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void cancelWps(WpsCallback paramWpsCallback)
  {
    getChannel().sendMessage(151566, 0, putListener(paramWpsCallback));
  }
  
  public boolean clearBlacklist()
  {
    try
    {
      this.mService.clearBlacklist();
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void connect(int paramInt, ActionListener paramActionListener)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Network id cannot be negative");
    }
    getChannel().sendMessage(151553, paramInt, putListener(paramActionListener));
  }
  
  public void connect(WifiConfiguration paramWifiConfiguration, ActionListener paramActionListener)
  {
    if (paramWifiConfiguration == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    getChannel().sendMessage(151553, -1, putListener(paramActionListener), paramWifiConfiguration);
  }
  
  public MulticastLock createMulticastLock(String paramString)
  {
    return new MulticastLock(paramString, null);
  }
  
  public WifiLock createWifiLock(int paramInt, String paramString)
  {
    return new WifiLock(paramInt, paramString, null);
  }
  
  public WifiLock createWifiLock(String paramString)
  {
    return new WifiLock(1, paramString, null);
  }
  
  public void deauthenticateNetwork(long paramLong, boolean paramBoolean)
  {
    try
    {
      this.mService.deauthenticateNetwork(paramLong, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void disable(int paramInt, ActionListener paramActionListener)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Network id cannot be negative");
    }
    getChannel().sendMessage(151569, paramInt, putListener(paramActionListener));
  }
  
  public void disableEphemeralNetwork(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("SSID cannot be null");
    }
    try
    {
      this.mService.disableEphemeralNetwork(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean disableNetwork(int paramInt)
  {
    try
    {
      boolean bool = this.mService.disableNetwork(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean disconnect()
  {
    try
    {
      this.mService.disconnect();
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void enableAggressiveHandover(int paramInt)
  {
    try
    {
      this.mService.enableAggressiveHandover(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean enableNetwork(int paramInt, boolean paramBoolean)
  {
    int j = 0;
    int i = j;
    if (paramBoolean)
    {
      i = j;
      if (this.mTargetSdkVersion < 21) {
        i = 1;
      }
    }
    if (i != 0)
    {
      NetworkRequest localNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build();
      NetworkPinner.pin(this.mContext, localNetworkRequest);
    }
    try
    {
      paramBoolean = this.mService.enableNetwork(paramInt, paramBoolean);
      if ((i == 0) || (paramBoolean)) {
        return paramBoolean;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    NetworkPinner.unpin();
    return paramBoolean;
  }
  
  public void enableVerboseLogging(int paramInt)
  {
    try
    {
      this.mService.enableVerboseLogging(paramInt);
      return;
    }
    catch (Exception localException)
    {
      Log.e("WifiManager", "enableVerboseLogging " + localException.toString());
    }
  }
  
  public void enableWifiConnectivityManager(boolean paramBoolean)
  {
    try
    {
      this.mService.enableWifiConnectivityManager(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void factoryReset()
  {
    try
    {
      this.mService.factoryReset();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mAsyncChannel != null) {
        this.mAsyncChannel.disconnect();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void forget(int paramInt, ActionListener paramActionListener)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Network id cannot be negative");
    }
    getChannel().sendMessage(151556, paramInt, putListener(paramActionListener));
  }
  
  public int getAggressiveHandover()
  {
    try
    {
      int i = this.mService.getAggressiveHandover();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getAllowScansWithTraffic()
  {
    try
    {
      int i = this.mService.getAllowScansWithTraffic();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public List<BatchedScanResult> getBatchedScanResults()
  {
    return null;
  }
  
  public String getConfigFile()
  {
    try
    {
      String str = this.mService.getConfigFile();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<WifiConfiguration> getConfiguredNetworks()
  {
    try
    {
      List localList = this.mService.getConfiguredNetworks();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiInfo getConnectionInfo()
  {
    try
    {
      WifiInfo localWifiInfo = this.mService.getConnectionInfo();
      return localWifiInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiConnectionStatistics getConnectionStatistics()
  {
    try
    {
      WifiConnectionStatistics localWifiConnectionStatistics = this.mService.getConnectionStatistics();
      return localWifiConnectionStatistics;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public WifiActivityEnergyInfo getControllerActivityEnergyInfo(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 385	android/net/wifi/WifiManager:mService	Landroid/net/wifi/IWifiManager;
    //   4: ifnonnull +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aload_0
    //   10: monitorenter
    //   11: aload_0
    //   12: getfield 385	android/net/wifi/WifiManager:mService	Landroid/net/wifi/IWifiManager;
    //   15: invokeinterface 645 1 0
    //   20: astore_2
    //   21: aload_0
    //   22: monitorexit
    //   23: aload_2
    //   24: areturn
    //   25: astore_2
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_2
    //   29: athrow
    //   30: astore_2
    //   31: aload_2
    //   32: invokevirtual 412	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	WifiManager
    //   0	36	1	paramInt	int
    //   20	4	2	localWifiActivityEnergyInfo	WifiActivityEnergyInfo
    //   25	4	2	localObject	Object
    //   30	2	2	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   11	21	25	finally
    //   9	11	30	android/os/RemoteException
    //   21	23	30	android/os/RemoteException
    //   26	30	30	android/os/RemoteException
  }
  
  public String getCountryCode()
  {
    try
    {
      String str = this.mService.getCountryCode();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Network getCurrentNetwork()
  {
    try
    {
      Network localNetwork = this.mService.getCurrentNetwork();
      return localNetwork;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public DhcpInfo getDhcpInfo()
  {
    try
    {
      DhcpInfo localDhcpInfo = this.mService.getDhcpInfo();
      return localDhcpInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean getEnableAutoJoinWhenAssociated()
  {
    try
    {
      boolean bool = this.mService.getEnableAutoJoinWhenAssociated();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getFrequencyBand()
  {
    try
    {
      int i = this.mService.getFrequencyBand();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiConfiguration getMatchingWifiConfig(ScanResult paramScanResult)
  {
    try
    {
      paramScanResult = this.mService.getMatchingWifiConfig(paramScanResult);
      return paramScanResult;
    }
    catch (RemoteException paramScanResult)
    {
      throw paramScanResult.rethrowFromSystemServer();
    }
  }
  
  public List<WifiConfiguration> getPrivilegedConfiguredNetworks()
  {
    try
    {
      List localList = this.mService.getPrivilegedConfiguredNetworks();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<ScanResult> getScanResults()
  {
    SeempLog.record(55);
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(this.mContext).requestPermissionAuto("android.permission.ACCESS_FINE_LOCATION"))) {
      return null;
    }
    try
    {
      List localList = this.mService.getScanResults(this.mContext.getOpPackageName());
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getSoftApInterfaceName()
  {
    try
    {
      String str = this.mService.getSoftApInterfaceName();
      return str;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public void getTxPacketCount(TxPacketCountListener paramTxPacketCountListener)
  {
    getChannel().sendMessage(151572, 0, putListener(paramTxPacketCountListener));
  }
  
  public int getVerboseLoggingLevel()
  {
    try
    {
      int i = this.mService.getVerboseLoggingLevel();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiConfiguration getWifiApConfiguration()
  {
    try
    {
      WifiConfiguration localWifiConfiguration = this.mService.getWifiApConfiguration();
      return localWifiConfiguration;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getWifiApState()
  {
    try
    {
      int i = this.mService.getWifiApEnabledState();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Messenger getWifiServiceMessenger()
  {
    try
    {
      Messenger localMessenger = this.mService.getWifiServiceMessenger();
      return localMessenger;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean getWifiStaSapConcurrency()
  {
    try
    {
      boolean bool = this.mService.getWifiStaSapConcurrency();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getWifiState()
  {
    try
    {
      int i = this.mService.getWifiEnabledState();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getWpsNfcConfigurationToken(int paramInt)
  {
    try
    {
      String str = this.mService.getWpsNfcConfigurationToken(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean initializeMulticastFiltering()
  {
    try
    {
      this.mService.initializeMulticastFiltering();
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean is5GHzBandSupported()
  {
    return isFeatureSupported(2);
  }
  
  public boolean isAdditionalStaSupported()
  {
    return isFeatureSupported(2048);
  }
  
  @Deprecated
  public boolean isBatchedScanSupported()
  {
    return false;
  }
  
  public boolean isDeviceToApRttSupported()
  {
    return isFeatureSupported(256);
  }
  
  public boolean isDeviceToDeviceRttSupported()
  {
    return isFeatureSupported(128);
  }
  
  public boolean isDualBandSupported()
  {
    try
    {
      boolean bool = this.mService.isDualBandSupported();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isEnhancedPowerReportingSupported()
  {
    return isFeatureSupported(65536);
  }
  
  public boolean isMulticastEnabled()
  {
    try
    {
      boolean bool = this.mService.isMulticastEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isNanSupported()
  {
    return isFeatureSupported(64);
  }
  
  public boolean isOffChannelTdlsSupported()
  {
    return isFeatureSupported(8192);
  }
  
  public boolean isP2pSupported()
  {
    return isFeatureSupported(8);
  }
  
  public boolean isPasspointSupported()
  {
    return isFeatureSupported(4);
  }
  
  public boolean isPortableHotspotSupported()
  {
    return isFeatureSupported(16);
  }
  
  public boolean isPreferredNetworkOffloadSupported()
  {
    return isFeatureSupported(1024);
  }
  
  public boolean isScanAlwaysAvailable()
  {
    try
    {
      boolean bool = this.mService.isScanAlwaysAvailable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isTdlsSupported()
  {
    return isFeatureSupported(4096);
  }
  
  public boolean isWifiApEnabled()
  {
    return getWifiApState() == 13;
  }
  
  public boolean isWifiEnabled()
  {
    return getWifiState() == 3;
  }
  
  public boolean isWifiScannerSupported()
  {
    return isFeatureSupported(32);
  }
  
  public boolean loadFtmDriver()
  {
    try
    {
      boolean bool = this.mService.loadFtmDriver();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public int matchProviderWithCurrentNetwork(String paramString)
  {
    try
    {
      int i = this.mService.matchProviderWithCurrentNetwork(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int modifyPasspointManagementObject(String paramString, List<PasspointManagementObjectDefinition> paramList)
  {
    try
    {
      int i = this.mService.modifyPasspointManagementObject(paramString, paramList);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean pingSupplicant()
  {
    if (this.mService == null) {
      return false;
    }
    try
    {
      boolean bool = this.mService.pingSupplicant();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void queryPasspointIcon(long paramLong, String paramString)
  {
    try
    {
      this.mService.queryPasspointIcon(paramLong, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean reassociate()
  {
    try
    {
      this.mService.reassociate();
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean reconnect()
  {
    try
    {
      this.mService.reconnect();
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean removeNetwork(int paramInt)
  {
    try
    {
      boolean bool = this.mService.removeNetwork(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean requestRunningP2p()
  {
    try
    {
      boolean bool = this.mService.requestRunningP2p();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean requestRunningSoftap()
  {
    try
    {
      boolean bool = this.mService.requestRunningSoftap();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void save(WifiConfiguration paramWifiConfiguration, ActionListener paramActionListener)
  {
    if (paramWifiConfiguration == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    getChannel().sendMessage(151559, 0, putListener(paramActionListener), paramWifiConfiguration);
  }
  
  public boolean saveConfiguration()
  {
    try
    {
      boolean bool = this.mService.saveConfiguration();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setAllowScansWithTraffic(int paramInt)
  {
    try
    {
      this.mService.setAllowScansWithTraffic(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setCountryCode(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setCountryCode(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean setEnableAutoJoinWhenAssociated(boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.setEnableAutoJoinWhenAssociated(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setFrequencyBand(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setFrequencyBand(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setTdlsEnabled(InetAddress paramInetAddress, boolean paramBoolean)
  {
    try
    {
      this.mService.enableTdls(paramInetAddress.getHostAddress(), paramBoolean);
      return;
    }
    catch (RemoteException paramInetAddress)
    {
      throw paramInetAddress.rethrowFromSystemServer();
    }
  }
  
  public void setTdlsEnabledWithMacAddress(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.enableTdlsWithMacAddress(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean setWifiApConfiguration(WifiConfiguration paramWifiConfiguration)
  {
    try
    {
      this.mService.setWifiApConfiguration(paramWifiConfiguration);
      return true;
    }
    catch (RemoteException paramWifiConfiguration)
    {
      throw paramWifiConfiguration.rethrowFromSystemServer();
    }
  }
  
  public boolean setWifiApEnabled(WifiConfiguration paramWifiConfiguration, boolean paramBoolean)
  {
    try
    {
      this.mService.setWifiApEnabled(paramWifiConfiguration, paramBoolean);
      return true;
    }
    catch (RemoteException paramWifiConfiguration)
    {
      throw paramWifiConfiguration.rethrowFromSystemServer();
    }
  }
  
  public boolean setWifiEnabled(boolean paramBoolean)
  {
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(this.mContext).requestPermissionAuto("CUSTOM_PERMISSION_CONTROL_WIFI"))) {
      return false;
    }
    try
    {
      paramBoolean = this.mService.setWifiEnabled(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean setWifiStaSapConcurrencyEnabled(int paramInt)
  {
    try
    {
      boolean bool = this.mService.setWifiStaSapConcurrencyEnabled(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean startCustomizedScan(ScanSettings paramScanSettings)
  {
    try
    {
      this.mService.startScan(paramScanSettings, null);
      return true;
    }
    catch (RemoteException paramScanSettings) {}
    return false;
  }
  
  public boolean startCustomizedScan(ScanSettings paramScanSettings, WorkSource paramWorkSource)
  {
    try
    {
      this.mService.startScan(paramScanSettings, paramWorkSource);
      return true;
    }
    catch (RemoteException paramScanSettings) {}
    return false;
  }
  
  @Deprecated
  public boolean startLocationRestrictedScan(WorkSource paramWorkSource)
  {
    return false;
  }
  
  public boolean startScan()
  {
    try
    {
      this.mService.startScan(null, null);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean startScan(WorkSource paramWorkSource)
  {
    try
    {
      this.mService.startScan(null, paramWorkSource);
      return true;
    }
    catch (RemoteException paramWorkSource)
    {
      throw paramWorkSource.rethrowFromSystemServer();
    }
  }
  
  public void startWps(WpsInfo paramWpsInfo, WpsCallback paramWpsCallback)
  {
    if (paramWpsInfo == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    getChannel().sendMessage(151562, 0, putListener(paramWpsCallback), paramWpsInfo);
  }
  
  public boolean unloadFtmDriver()
  {
    try
    {
      boolean bool = this.mService.unloadFtmDriver();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public int updateNetwork(WifiConfiguration paramWifiConfiguration)
  {
    if ((paramWifiConfiguration == null) || (paramWifiConfiguration.networkId < 0)) {
      return -1;
    }
    return addOrUpdateNetwork(paramWifiConfiguration);
  }
  
  public static abstract interface ActionListener
  {
    public abstract void onFailure(int paramInt);
    
    public abstract void onSuccess();
  }
  
  public class MulticastLock
  {
    private final IBinder mBinder;
    private boolean mHeld;
    private int mRefCount;
    private boolean mRefCounted;
    private String mTag;
    
    private MulticastLock(String paramString)
    {
      this.mTag = paramString;
      this.mBinder = new Binder();
      this.mRefCount = 0;
      this.mRefCounted = true;
      this.mHeld = false;
    }
    
    public void acquire()
    {
      boolean bool;
      do
      {
        synchronized (this.mBinder)
        {
          if (this.mRefCounted)
          {
            int i = this.mRefCount + 1;
            this.mRefCount = i;
            if (i != 1) {
              break;
            }
            try
            {
              WifiManager.this.mService.acquireMulticastLock(this.mBinder, this.mTag);
              synchronized (WifiManager.this)
              {
                if (WifiManager.-get0(WifiManager.this) < 50) {
                  break label129;
                }
                WifiManager.this.mService.releaseMulticastLock();
                throw new UnsupportedOperationException("Exceeded maximum number of wifi locks");
              }
              localObject1 = finally;
            }
            catch (RemoteException localRemoteException)
            {
              throw localRemoteException.rethrowFromSystemServer();
            }
          }
        }
        bool = this.mHeld;
      } while (!bool);
      for (;;)
      {
        return;
        label129:
        WifiManager localWifiManager2 = WifiManager.this;
        WifiManager.-set0(localWifiManager2, WifiManager.-get0(localWifiManager2) + 1);
        this.mHeld = true;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      super.finalize();
      setReferenceCounted(false);
      release();
    }
    
    public boolean isHeld()
    {
      synchronized (this.mBinder)
      {
        boolean bool = this.mHeld;
        return bool;
      }
    }
    
    /* Error */
    public void release()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 32	android/net/wifi/WifiManager$MulticastLock:mBinder	Landroid/os/IBinder;
      //   4: astore_3
      //   5: aload_3
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 36	android/net/wifi/WifiManager$MulticastLock:mRefCounted	Z
      //   11: ifeq +111 -> 122
      //   14: aload_0
      //   15: getfield 34	android/net/wifi/WifiManager$MulticastLock:mRefCount	I
      //   18: iconst_1
      //   19: isub
      //   20: istore_1
      //   21: aload_0
      //   22: iload_1
      //   23: putfield 34	android/net/wifi/WifiManager$MulticastLock:mRefCount	I
      //   26: iload_1
      //   27: ifne +51 -> 78
      //   30: aload_0
      //   31: getfield 22	android/net/wifi/WifiManager$MulticastLock:this$0	Landroid/net/wifi/WifiManager;
      //   34: getfield 49	android/net/wifi/WifiManager:mService	Landroid/net/wifi/IWifiManager;
      //   37: invokeinterface 62 1 0
      //   42: aload_0
      //   43: getfield 22	android/net/wifi/WifiManager$MulticastLock:this$0	Landroid/net/wifi/WifiManager;
      //   46: astore 4
      //   48: aload 4
      //   50: monitorenter
      //   51: aload_0
      //   52: getfield 22	android/net/wifi/WifiManager$MulticastLock:this$0	Landroid/net/wifi/WifiManager;
      //   55: astore 5
      //   57: aload 5
      //   59: aload 5
      //   61: invokestatic 59	android/net/wifi/WifiManager:-get0	(Landroid/net/wifi/WifiManager;)I
      //   64: iconst_1
      //   65: isub
      //   66: invokestatic 77	android/net/wifi/WifiManager:-set0	(Landroid/net/wifi/WifiManager;I)I
      //   69: pop
      //   70: aload 4
      //   72: monitorexit
      //   73: aload_0
      //   74: iconst_0
      //   75: putfield 38	android/net/wifi/WifiManager$MulticastLock:mHeld	Z
      //   78: aload_0
      //   79: getfield 34	android/net/wifi/WifiManager$MulticastLock:mRefCount	I
      //   82: ifge +68 -> 150
      //   85: new 94	java/lang/RuntimeException
      //   88: dup
      //   89: new 96	java/lang/StringBuilder
      //   92: dup
      //   93: invokespecial 97	java/lang/StringBuilder:<init>	()V
      //   96: ldc 99
      //   98: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   101: aload_0
      //   102: getfield 27	android/net/wifi/WifiManager$MulticastLock:mTag	Ljava/lang/String;
      //   105: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   108: invokevirtual 107	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   111: invokespecial 108	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
      //   114: athrow
      //   115: astore 4
      //   117: aload_3
      //   118: monitorexit
      //   119: aload 4
      //   121: athrow
      //   122: aload_0
      //   123: getfield 38	android/net/wifi/WifiManager$MulticastLock:mHeld	Z
      //   126: istore_2
      //   127: iload_2
      //   128: ifeq -50 -> 78
      //   131: goto -101 -> 30
      //   134: astore 5
      //   136: aload 4
      //   138: monitorexit
      //   139: aload 5
      //   141: athrow
      //   142: astore 4
      //   144: aload 4
      //   146: invokevirtual 73	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   149: athrow
      //   150: aload_3
      //   151: monitorexit
      //   152: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	153	0	this	MulticastLock
      //   20	7	1	i	int
      //   126	2	2	bool	boolean
      //   4	147	3	localIBinder	IBinder
      //   115	22	4	localObject1	Object
      //   142	3	4	localRemoteException	RemoteException
      //   55	5	5	localWifiManager2	WifiManager
      //   134	6	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	115	finally
      //   30	51	115	finally
      //   70	73	115	finally
      //   73	78	115	finally
      //   78	115	115	finally
      //   122	127	115	finally
      //   136	142	115	finally
      //   144	150	115	finally
      //   51	70	134	finally
      //   30	51	142	android/os/RemoteException
      //   70	73	142	android/os/RemoteException
      //   136	142	142	android/os/RemoteException
    }
    
    public void setReferenceCounted(boolean paramBoolean)
    {
      this.mRefCounted = paramBoolean;
    }
    
    public String toString()
    {
      synchronized (this.mBinder)
      {
        String str3 = Integer.toHexString(System.identityHashCode(this));
        if (this.mHeld) {}
        for (String str1 = "held; "; this.mRefCounted; str1 = "")
        {
          str2 = "refcounted: refcount = " + this.mRefCount;
          str1 = "MulticastLock{ " + str3 + "; " + str1 + str2 + " }";
          return str1;
        }
        String str2 = "not refcounted";
      }
    }
  }
  
  private class ServiceHandler
    extends Handler
  {
    ServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    private void dispatchMessageToListeners(Message paramMessage)
    {
      Object localObject1 = WifiManager.-wrap0(WifiManager.this, paramMessage.arg2);
      switch (paramMessage.what)
      {
      }
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        return;
                        if (paramMessage.arg1 == 0) {
                          WifiManager.-get1(WifiManager.this).sendMessage(69633);
                        }
                        for (;;)
                        {
                          WifiManager.-get2(WifiManager.this).countDown();
                          return;
                          Log.e("WifiManager", "Failed to set up channel connection");
                          WifiManager.-set1(WifiManager.this, null);
                        }
                        Log.e("WifiManager", "Channel connection lost");
                        WifiManager.-set1(WifiManager.this, null);
                        getLooper().quit();
                        return;
                      } while (localObject1 == null);
                      ((WifiManager.ActionListener)localObject1).onFailure(paramMessage.arg1);
                      return;
                    } while (localObject1 == null);
                    ((WifiManager.ActionListener)localObject1).onSuccess();
                    return;
                  } while (localObject1 == null);
                  ??? = (WpsResult)paramMessage.obj;
                  ((WifiManager.WpsCallback)localObject1).onStarted(((WpsResult)???).pin);
                  synchronized (WifiManager.-get4(WifiManager.this))
                  {
                    WifiManager.-get3(WifiManager.this).put(paramMessage.arg2, localObject1);
                    return;
                  }
                } while (localObject1 == null);
                ((WifiManager.WpsCallback)localObject1).onSucceeded();
                return;
              } while (localObject1 == null);
              ((WifiManager.WpsCallback)localObject1).onFailed(paramMessage.arg1);
              return;
            } while (localObject1 == null);
            ((WifiManager.WpsCallback)localObject1).onSucceeded();
            return;
          } while (localObject1 == null);
          ((WifiManager.WpsCallback)localObject1).onFailed(paramMessage.arg1);
          return;
        } while (localObject1 == null);
        paramMessage = (RssiPacketCountInfo)paramMessage.obj;
        if (paramMessage != null)
        {
          ((WifiManager.TxPacketCountListener)localObject1).onSuccess(paramMessage.txgood + paramMessage.txbad);
          return;
        }
        ((WifiManager.TxPacketCountListener)localObject1).onFailure(0);
        return;
      } while (localObject1 == null);
      ((WifiManager.TxPacketCountListener)localObject1).onFailure(paramMessage.arg1);
    }
    
    public void handleMessage(Message paramMessage)
    {
      synchronized ()
      {
        dispatchMessageToListeners(paramMessage);
        return;
      }
    }
  }
  
  public static abstract interface TxPacketCountListener
  {
    public abstract void onFailure(int paramInt);
    
    public abstract void onSuccess(int paramInt);
  }
  
  public class WifiLock
  {
    private final IBinder mBinder;
    private boolean mHeld;
    int mLockType;
    private int mRefCount;
    private boolean mRefCounted;
    private String mTag;
    private WorkSource mWorkSource;
    
    private WifiLock(int paramInt, String paramString)
    {
      this.mTag = paramString;
      this.mLockType = paramInt;
      this.mBinder = new Binder();
      this.mRefCount = 0;
      this.mRefCounted = true;
      this.mHeld = false;
    }
    
    public void acquire()
    {
      boolean bool;
      do
      {
        synchronized (this.mBinder)
        {
          if (this.mRefCounted)
          {
            int i = this.mRefCount + 1;
            this.mRefCount = i;
            if (i != 1) {
              break;
            }
            try
            {
              WifiManager.this.mService.acquireWifiLock(this.mBinder, this.mLockType, this.mTag, this.mWorkSource);
              synchronized (WifiManager.this)
              {
                if (WifiManager.-get0(WifiManager.this) < 50) {
                  break label143;
                }
                WifiManager.this.mService.releaseWifiLock(this.mBinder);
                throw new UnsupportedOperationException("Exceeded maximum number of wifi locks");
              }
              localObject1 = finally;
            }
            catch (RemoteException localRemoteException)
            {
              throw localRemoteException.rethrowFromSystemServer();
            }
          }
        }
        bool = this.mHeld;
      } while (!bool);
      for (;;)
      {
        return;
        label143:
        WifiManager localWifiManager2 = WifiManager.this;
        WifiManager.-set0(localWifiManager2, WifiManager.-get0(localWifiManager2) + 1);
        this.mHeld = true;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      super.finalize();
      synchronized (this.mBinder)
      {
        boolean bool = this.mHeld;
        if (bool) {}
        try
        {
          WifiManager.this.mService.releaseWifiLock(this.mBinder);
          synchronized (WifiManager.this)
          {
            WifiManager localWifiManager2 = WifiManager.this;
            WifiManager.-set0(localWifiManager2, WifiManager.-get0(localWifiManager2) - 1);
            return;
          }
          localObject1 = finally;
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
    }
    
    public boolean isHeld()
    {
      synchronized (this.mBinder)
      {
        boolean bool = this.mHeld;
        return bool;
      }
    }
    
    /* Error */
    public void release()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 37	android/net/wifi/WifiManager$WifiLock:mBinder	Landroid/os/IBinder;
      //   4: astore_3
      //   5: aload_3
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 41	android/net/wifi/WifiManager$WifiLock:mRefCounted	Z
      //   11: ifeq +116 -> 127
      //   14: aload_0
      //   15: getfield 39	android/net/wifi/WifiManager$WifiLock:mRefCount	I
      //   18: iconst_1
      //   19: isub
      //   20: istore_1
      //   21: aload_0
      //   22: iload_1
      //   23: putfield 39	android/net/wifi/WifiManager$WifiLock:mRefCount	I
      //   26: iload_1
      //   27: ifne +56 -> 83
      //   30: aload_0
      //   31: getfield 25	android/net/wifi/WifiManager$WifiLock:this$0	Landroid/net/wifi/WifiManager;
      //   34: getfield 54	android/net/wifi/WifiManager:mService	Landroid/net/wifi/IWifiManager;
      //   37: aload_0
      //   38: getfield 37	android/net/wifi/WifiManager$WifiLock:mBinder	Landroid/os/IBinder;
      //   41: invokeinterface 70 2 0
      //   46: pop
      //   47: aload_0
      //   48: getfield 25	android/net/wifi/WifiManager$WifiLock:this$0	Landroid/net/wifi/WifiManager;
      //   51: astore 4
      //   53: aload 4
      //   55: monitorenter
      //   56: aload_0
      //   57: getfield 25	android/net/wifi/WifiManager$WifiLock:this$0	Landroid/net/wifi/WifiManager;
      //   60: astore 5
      //   62: aload 5
      //   64: aload 5
      //   66: invokestatic 66	android/net/wifi/WifiManager:-get0	(Landroid/net/wifi/WifiManager;)I
      //   69: iconst_1
      //   70: isub
      //   71: invokestatic 85	android/net/wifi/WifiManager:-set0	(Landroid/net/wifi/WifiManager;I)I
      //   74: pop
      //   75: aload 4
      //   77: monitorexit
      //   78: aload_0
      //   79: iconst_0
      //   80: putfield 43	android/net/wifi/WifiManager$WifiLock:mHeld	Z
      //   83: aload_0
      //   84: getfield 39	android/net/wifi/WifiManager$WifiLock:mRefCount	I
      //   87: ifge +68 -> 155
      //   90: new 96	java/lang/RuntimeException
      //   93: dup
      //   94: new 98	java/lang/StringBuilder
      //   97: dup
      //   98: invokespecial 99	java/lang/StringBuilder:<init>	()V
      //   101: ldc 101
      //   103: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   106: aload_0
      //   107: getfield 30	android/net/wifi/WifiManager$WifiLock:mTag	Ljava/lang/String;
      //   110: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   113: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   116: invokespecial 110	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
      //   119: athrow
      //   120: astore 4
      //   122: aload_3
      //   123: monitorexit
      //   124: aload 4
      //   126: athrow
      //   127: aload_0
      //   128: getfield 43	android/net/wifi/WifiManager$WifiLock:mHeld	Z
      //   131: istore_2
      //   132: iload_2
      //   133: ifeq -50 -> 83
      //   136: goto -106 -> 30
      //   139: astore 5
      //   141: aload 4
      //   143: monitorexit
      //   144: aload 5
      //   146: athrow
      //   147: astore 4
      //   149: aload 4
      //   151: invokevirtual 81	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   154: athrow
      //   155: aload_3
      //   156: monitorexit
      //   157: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	158	0	this	WifiLock
      //   20	7	1	i	int
      //   131	2	2	bool	boolean
      //   4	152	3	localIBinder	IBinder
      //   120	22	4	localObject1	Object
      //   147	3	4	localRemoteException	RemoteException
      //   60	5	5	localWifiManager2	WifiManager
      //   139	6	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	120	finally
      //   30	56	120	finally
      //   75	78	120	finally
      //   78	83	120	finally
      //   83	120	120	finally
      //   127	132	120	finally
      //   141	147	120	finally
      //   149	155	120	finally
      //   56	75	139	finally
      //   30	56	147	android/os/RemoteException
      //   75	78	147	android/os/RemoteException
      //   141	147	147	android/os/RemoteException
    }
    
    public void setReferenceCounted(boolean paramBoolean)
    {
      this.mRefCounted = paramBoolean;
    }
    
    public void setWorkSource(WorkSource paramWorkSource)
    {
      IBinder localIBinder = this.mBinder;
      WorkSource localWorkSource = paramWorkSource;
      if (paramWorkSource != null) {
        localWorkSource = paramWorkSource;
      }
      for (;;)
      {
        boolean bool1;
        try
        {
          if (paramWorkSource.size() == 0) {
            localWorkSource = null;
          }
          bool1 = true;
          if (localWorkSource == null)
          {
            this.mWorkSource = null;
            if (bool1)
            {
              bool1 = this.mHeld;
              if (!bool1) {}
            }
          }
        }
        finally {}
        try
        {
          WifiManager.this.mService.updateWifiLockWorkSource(this.mBinder, this.mWorkSource);
          return;
        }
        catch (RemoteException paramWorkSource)
        {
          boolean bool2;
          throw paramWorkSource.rethrowFromSystemServer();
        }
        localWorkSource.clearNames();
        if (this.mWorkSource == null)
        {
          if (this.mWorkSource != null)
          {
            bool1 = true;
            this.mWorkSource = new WorkSource(localWorkSource);
          }
          else
          {
            bool1 = false;
          }
        }
        else
        {
          bool2 = this.mWorkSource.diff(localWorkSource);
          bool1 = bool2;
          if (bool2)
          {
            this.mWorkSource.set(localWorkSource);
            bool1 = bool2;
          }
        }
      }
    }
    
    public String toString()
    {
      synchronized (this.mBinder)
      {
        String str3 = Integer.toHexString(System.identityHashCode(this));
        if (this.mHeld) {}
        for (String str1 = "held; "; this.mRefCounted; str1 = "")
        {
          str2 = "refcounted: refcount = " + this.mRefCount;
          str1 = "WifiLock{ " + str3 + "; " + str1 + str2 + " }";
          return str1;
        }
        String str2 = "not refcounted";
      }
    }
  }
  
  public static abstract class WpsCallback
  {
    public abstract void onFailed(int paramInt);
    
    public abstract void onStarted(String paramString);
    
    public abstract void onSucceeded();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */