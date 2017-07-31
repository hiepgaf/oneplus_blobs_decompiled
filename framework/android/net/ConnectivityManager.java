package android.net;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.wifi.WifiDevice;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkActivityListener;
import android.os.INetworkActivityListener.Stub;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.Preconditions;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.net.event.NetworkEventDispatcher;

public class ConnectivityManager
{
  @Deprecated
  public static final String ACTION_BACKGROUND_DATA_SETTING_CHANGED = "android.net.conn.BACKGROUND_DATA_SETTING_CHANGED";
  public static final String ACTION_CAPTIVE_PORTAL_SIGN_IN = "android.net.conn.CAPTIVE_PORTAL";
  public static final String ACTION_CAPTIVE_PORTAL_TEST_COMPLETED = "android.net.conn.CAPTIVE_PORTAL_TEST_COMPLETED";
  public static final String ACTION_DATA_ACTIVITY_CHANGE = "android.net.conn.DATA_ACTIVITY_CHANGE";
  public static final String ACTION_PROMPT_LOST_VALIDATION = "android.net.conn.PROMPT_LOST_VALIDATION";
  public static final String ACTION_PROMPT_UNVALIDATED = "android.net.conn.PROMPT_UNVALIDATED";
  public static final String ACTION_RESTRICT_BACKGROUND_CHANGED = "android.net.conn.RESTRICT_BACKGROUND_CHANGED";
  public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
  private static final int BASE = 524288;
  public static final int CALLBACK_AVAILABLE = 524290;
  public static final int CALLBACK_CAP_CHANGED = 524294;
  public static final int CALLBACK_EXIT = 524297;
  public static final int CALLBACK_IP_CHANGED = 524295;
  public static final int CALLBACK_LOSING = 524291;
  public static final int CALLBACK_LOST = 524292;
  public static final int CALLBACK_PRECHECK = 524289;
  public static final int CALLBACK_RELEASED = 524296;
  public static final int CALLBACK_RESUMED = 524300;
  public static final int CALLBACK_SUSPENDED = 524299;
  public static final int CALLBACK_UNAVAIL = 524293;
  public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
  public static final String CONNECTIVITY_ACTION_SUPL = "android.net.conn.CONNECTIVITY_CHANGE_SUPL";
  @Deprecated
  public static final int DEFAULT_NETWORK_PREFERENCE = 1;
  private static final int EXPIRE_LEGACY_REQUEST = 524298;
  public static final String EXTRA_ACTIVE_TETHER = "activeArray";
  public static final String EXTRA_ADD_TETHER_TYPE = "extraAddTetherType";
  public static final String EXTRA_AVAILABLE_TETHER = "availableArray";
  public static final String EXTRA_CAPTIVE_PORTAL = "android.net.extra.CAPTIVE_PORTAL";
  public static final String EXTRA_CAPTIVE_PORTAL_URL = "android.net.extra.CAPTIVE_PORTAL_URL";
  public static final String EXTRA_DEVICE_TYPE = "deviceType";
  public static final String EXTRA_ERRORED_TETHER = "erroredArray";
  public static final String EXTRA_EXTRA_INFO = "extraInfo";
  public static final String EXTRA_INET_CONDITION = "inetCondition";
  public static final String EXTRA_IS_ACTIVE = "isActive";
  public static final String EXTRA_IS_CAPTIVE_PORTAL = "captivePortal";
  public static final String EXTRA_IS_FAILOVER = "isFailover";
  public static final String EXTRA_NETWORK = "android.net.extra.NETWORK";
  @Deprecated
  public static final String EXTRA_NETWORK_INFO = "networkInfo";
  public static final String EXTRA_NETWORK_REQUEST = "android.net.extra.NETWORK_REQUEST";
  public static final String EXTRA_NETWORK_TYPE = "networkType";
  public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
  public static final String EXTRA_OTHER_NETWORK_INFO = "otherNetwork";
  public static final String EXTRA_PROVISION_CALLBACK = "extraProvisionCallback";
  public static final String EXTRA_REALTIME_NS = "tsNanos";
  public static final String EXTRA_REASON = "reason";
  public static final String EXTRA_REM_TETHER_TYPE = "extraRemTetherType";
  public static final String EXTRA_RUN_PROVISION = "extraRunProvision";
  public static final String EXTRA_SET_ALARM = "extraSetAlarm";
  public static final String INET_CONDITION_ACTION = "android.net.conn.INET_CONDITION_ACTION";
  private static final int LISTEN = 1;
  public static final int MAX_NETWORK_REQUEST_TIMEOUT_MS = 6000000;
  public static final int MAX_NETWORK_TYPE = 17;
  public static final int MAX_RADIO_TYPE = 17;
  public static final int NETID_UNSET = 0;
  private static final int REQUEST = 2;
  public static final int REQUEST_ID_UNSET = 0;
  public static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1;
  public static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3;
  public static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2;
  private static final String TAG = "ConnectivityManager";
  public static final int TETHERING_BLUETOOTH = 2;
  public static final int TETHERING_INVALID = -1;
  public static final int TETHERING_USB = 1;
  public static final int TETHERING_WIFI = 0;
  public static final int TETHERING_WIGIG = 3;
  public static final String TETHER_AUTO_SHUT_DOWN_SOFTAP = "codeaurora.net.conn.TETHER_AUTO_SHUT_DOWN_SOFTAP";
  public static final String TETHER_CONNECT_STATE_CHANGED = "codeaurora.net.conn.TETHER_CONNECT_STATE_CHANGED";
  public static final int TETHER_ERROR_DISABLE_NAT_ERROR = 9;
  public static final int TETHER_ERROR_ENABLE_NAT_ERROR = 8;
  public static final int TETHER_ERROR_IFACE_CFG_ERROR = 10;
  public static final int TETHER_ERROR_MASTER_ERROR = 5;
  public static final int TETHER_ERROR_NO_ERROR = 0;
  public static final int TETHER_ERROR_PROVISION_FAILED = 11;
  public static final int TETHER_ERROR_SERVICE_UNAVAIL = 2;
  public static final int TETHER_ERROR_TETHER_IFACE_ERROR = 6;
  public static final int TETHER_ERROR_UNAVAIL_IFACE = 4;
  public static final int TETHER_ERROR_UNKNOWN_IFACE = 1;
  public static final int TETHER_ERROR_UNSUPPORTED = 3;
  public static final int TETHER_ERROR_UNTETHER_IFACE_ERROR = 7;
  public static final int TYPE_BLUETOOTH = 7;
  public static final int TYPE_DUMMY = 8;
  public static final int TYPE_ETHERNET = 9;
  public static final int TYPE_MOBILE = 0;
  public static final int TYPE_MOBILE_CBS = 12;
  public static final int TYPE_MOBILE_DUN = 4;
  public static final int TYPE_MOBILE_EMERGENCY = 15;
  public static final int TYPE_MOBILE_FOTA = 10;
  public static final int TYPE_MOBILE_HIPRI = 5;
  public static final int TYPE_MOBILE_IA = 14;
  public static final int TYPE_MOBILE_IMS = 11;
  public static final int TYPE_MOBILE_MMS = 2;
  public static final int TYPE_MOBILE_SUPL = 3;
  public static final int TYPE_NONE = -1;
  public static final int TYPE_PROXY = 16;
  public static final int TYPE_VPN = 17;
  public static final int TYPE_WIFI = 1;
  public static final int TYPE_WIFI_P2P = 13;
  public static final int TYPE_WIMAX = 6;
  static CallbackHandler sCallbackHandler = null;
  static final AtomicInteger sCallbackRefCount;
  private static ConnectivityManager sInstance;
  private static HashMap<NetworkCapabilities, LegacyRequest> sLegacyRequests = new HashMap();
  static final HashMap<NetworkRequest, NetworkCallback> sNetworkCallback = new HashMap();
  private final Context mContext;
  private INetworkManagementService mNMService;
  private INetworkPolicyManager mNPManager;
  private final ArrayMap<OnNetworkActiveListener, INetworkActivityListener> mNetworkActivityListeners = new ArrayMap();
  private final IConnectivityManager mService;
  
  static
  {
    sCallbackRefCount = new AtomicInteger(0);
  }
  
  public ConnectivityManager(Context paramContext, IConnectivityManager paramIConnectivityManager)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "missing context"));
    this.mService = ((IConnectivityManager)Preconditions.checkNotNull(paramIConnectivityManager, "missing IConnectivityManager"));
    sInstance = this;
  }
  
  public static final boolean checkChangePermission(Context paramContext)
  {
    int i = Binder.getCallingUid();
    return Settings.checkAndNoteChangeNetworkStateOperation(paramContext, i, Settings.getPackageNameForUid(paramContext, i), false);
  }
  
  private void checkLegacyRoutingApiAccess()
  {
    if (this.mContext.checkCallingOrSelfPermission("com.android.permission.INJECT_OMADM_SETTINGS") == 0) {
      return;
    }
    unsupportedStartingFrom(23);
  }
  
  private void checkPendingIntent(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      throw new IllegalArgumentException("PendingIntent cannot be null.");
    }
  }
  
  private void decCallbackHandlerRefCount()
  {
    synchronized (sCallbackRefCount)
    {
      if (sCallbackRefCount.decrementAndGet() == 0)
      {
        sCallbackHandler.obtainMessage(524297).sendToTarget();
        sCallbackHandler = null;
      }
      return;
    }
  }
  
  public static final void enforceChangePermission(Context paramContext)
  {
    int i = Binder.getCallingUid();
    Settings.checkAndNoteChangeNetworkStateOperation(paramContext, i, Settings.getPackageNameForUid(paramContext, i), true);
  }
  
  public static final void enforceTetherChangePermission(Context paramContext)
  {
    if (paramContext.getResources().getStringArray(17235994).length == 2)
    {
      paramContext.enforceCallingOrSelfPermission("android.permission.TETHER_PRIVILEGED", "ConnectivityService");
      return;
    }
    int i = Binder.getCallingUid();
    Settings.checkAndNoteWriteSettingsOperation(paramContext, i, Settings.getPackageNameForUid(paramContext, i), true);
  }
  
  private void expireRequest(NetworkCapabilities paramNetworkCapabilities, int paramInt)
  {
    synchronized (sLegacyRequests)
    {
      LegacyRequest localLegacyRequest = (LegacyRequest)sLegacyRequests.get(paramNetworkCapabilities);
      if (localLegacyRequest == null) {
        return;
      }
      int i = localLegacyRequest.expireSequenceNumber;
      if (localLegacyRequest.expireSequenceNumber == paramInt) {
        removeRequestForFeature(paramNetworkCapabilities);
      }
      Log.d("ConnectivityManager", "expireRequest with " + i + ", " + paramInt);
      return;
    }
  }
  
  private NetworkRequest findRequestForFeature(NetworkCapabilities paramNetworkCapabilities)
  {
    synchronized (sLegacyRequests)
    {
      paramNetworkCapabilities = (LegacyRequest)sLegacyRequests.get(paramNetworkCapabilities);
      if (paramNetworkCapabilities != null)
      {
        paramNetworkCapabilities = paramNetworkCapabilities.networkRequest;
        return paramNetworkCapabilities;
      }
      return null;
    }
  }
  
  public static ConnectivityManager from(Context paramContext)
  {
    return (ConnectivityManager)paramContext.getSystemService("connectivity");
  }
  
  private static ConnectivityManager getInstance()
  {
    if (getInstanceOrNull() == null) {
      throw new IllegalStateException("No ConnectivityManager yet constructed");
    }
    return getInstanceOrNull();
  }
  
  static ConnectivityManager getInstanceOrNull()
  {
    return sInstance;
  }
  
  private INetworkManagementService getNetworkManagementService()
  {
    try
    {
      if (this.mNMService != null)
      {
        localINetworkManagementService = this.mNMService;
        return localINetworkManagementService;
      }
      this.mNMService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
      INetworkManagementService localINetworkManagementService = this.mNMService;
      return localINetworkManagementService;
    }
    finally {}
  }
  
  private INetworkPolicyManager getNetworkPolicyManager()
  {
    try
    {
      if (this.mNPManager != null)
      {
        localINetworkPolicyManager = this.mNPManager;
        return localINetworkPolicyManager;
      }
      this.mNPManager = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
      INetworkPolicyManager localINetworkPolicyManager = this.mNPManager;
      return localINetworkPolicyManager;
    }
    finally {}
  }
  
  public static String getNetworkTypeName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "MOBILE";
    case 1: 
      return "WIFI";
    case 2: 
      return "MOBILE_MMS";
    case 3: 
      return "MOBILE_SUPL";
    case 4: 
      return "MOBILE_DUN";
    case 5: 
      return "MOBILE_HIPRI";
    case 6: 
      return "WIMAX";
    case 7: 
      return "BLUETOOTH";
    case 8: 
      return "DUMMY";
    case 9: 
      return "ETHERNET";
    case 10: 
      return "MOBILE_FOTA";
    case 11: 
      return "MOBILE_IMS";
    case 12: 
      return "MOBILE_CBS";
    case 13: 
      return "WIFI_P2P";
    case 14: 
      return "MOBILE_IA";
    case 15: 
      return "MOBILE_EMERGENCY";
    case 16: 
      return "PROXY";
    }
    return "VPN";
  }
  
  public static Network getProcessDefaultNetwork()
  {
    int i = NetworkUtils.getBoundNetworkForProcess();
    if (i == 0) {
      return null;
    }
    return new Network(i);
  }
  
  private void incCallbackHandlerRefCount()
  {
    synchronized (sCallbackRefCount)
    {
      if (sCallbackRefCount.incrementAndGet() == 1)
      {
        HandlerThread localHandlerThread = new HandlerThread("ConnectivityManager");
        localHandlerThread.start();
        sCallbackHandler = new CallbackHandler(localHandlerThread.getLooper(), sNetworkCallback, sCallbackRefCount, this);
      }
      return;
    }
  }
  
  private int inferLegacyTypeForNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    if (paramNetworkCapabilities == null) {
      return -1;
    }
    if (!paramNetworkCapabilities.hasTransport(0)) {
      return -1;
    }
    if (!paramNetworkCapabilities.hasCapability(1)) {
      return -1;
    }
    Object localObject = null;
    int i = -1;
    if (paramNetworkCapabilities.hasCapability(5))
    {
      localObject = "enableCBS";
      i = 12;
    }
    while (localObject != null)
    {
      localObject = networkCapabilitiesForFeature(0, (String)localObject);
      if ((!((NetworkCapabilities)localObject).equalsNetCapabilities(paramNetworkCapabilities)) || (!((NetworkCapabilities)localObject).equalsTransportTypes(paramNetworkCapabilities))) {
        break;
      }
      return i;
      if (paramNetworkCapabilities.hasCapability(4))
      {
        localObject = "enableIMS";
        i = 11;
      }
      else if (paramNetworkCapabilities.hasCapability(3))
      {
        localObject = "enableFOTA";
        i = 10;
      }
      else if (paramNetworkCapabilities.hasCapability(2))
      {
        localObject = "enableDUN";
        i = 4;
      }
      else if (paramNetworkCapabilities.hasCapability(1))
      {
        localObject = "enableSUPL";
        i = 3;
      }
      else if (paramNetworkCapabilities.hasCapability(12))
      {
        localObject = "enableHIPRI";
        i = 5;
      }
    }
    return -1;
  }
  
  public static boolean isNetworkTypeMobile(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 13: 
    default: 
      return false;
    }
    return true;
  }
  
  public static boolean isNetworkTypeValid(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 17) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isNetworkTypeWifi(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private int legacyTypeForNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    if (paramNetworkCapabilities == null) {
      return -1;
    }
    if (paramNetworkCapabilities.hasCapability(5)) {
      return 12;
    }
    if (paramNetworkCapabilities.hasCapability(4)) {
      return 11;
    }
    if (paramNetworkCapabilities.hasCapability(3)) {
      return 10;
    }
    if (paramNetworkCapabilities.hasCapability(2)) {
      return 4;
    }
    if (paramNetworkCapabilities.hasCapability(1)) {
      return 3;
    }
    if (paramNetworkCapabilities.hasCapability(0)) {
      return 2;
    }
    if (paramNetworkCapabilities.hasCapability(12)) {
      return 5;
    }
    if (paramNetworkCapabilities.hasCapability(6)) {
      return 13;
    }
    return -1;
  }
  
  private NetworkCapabilities networkCapabilitiesForFeature(int paramInt, String paramString)
  {
    if (paramInt == 0)
    {
      if ("enableMMS".equals(paramString)) {
        paramInt = 0;
      }
      for (;;)
      {
        paramString = new NetworkCapabilities();
        paramString.addTransportType(0).addCapability(paramInt);
        paramString.maybeMarkCapabilitiesRestricted();
        return paramString;
        if ("enableSUPL".equals(paramString))
        {
          paramInt = 1;
        }
        else if (("enableDUN".equals(paramString)) || ("enableDUNAlways".equals(paramString)))
        {
          paramInt = 2;
        }
        else if ("enableHIPRI".equals(paramString))
        {
          paramInt = 12;
        }
        else if ("enableFOTA".equals(paramString))
        {
          paramInt = 3;
        }
        else if ("enableIMS".equals(paramString))
        {
          paramInt = 4;
        }
        else
        {
          if (!"enableCBS".equals(paramString)) {
            break;
          }
          paramInt = 5;
        }
      }
      return null;
    }
    if ((paramInt == 1) && ("p2p".equals(paramString)))
    {
      paramString = new NetworkCapabilities();
      paramString.addTransportType(1);
      paramString.addCapability(6);
      paramString.maybeMarkCapabilitiesRestricted();
      return paramString;
    }
    return null;
  }
  
  private boolean removeRequestForFeature(NetworkCapabilities paramNetworkCapabilities)
  {
    synchronized (sLegacyRequests)
    {
      paramNetworkCapabilities = (LegacyRequest)sLegacyRequests.remove(paramNetworkCapabilities);
      if (paramNetworkCapabilities == null) {
        return false;
      }
    }
    unregisterNetworkCallback(paramNetworkCapabilities.networkCallback);
    LegacyRequest.-wrap0(paramNetworkCapabilities);
    return true;
  }
  
  private void renewRequestLocked(LegacyRequest paramLegacyRequest)
  {
    paramLegacyRequest.expireSequenceNumber += 1;
    Log.d("ConnectivityManager", "renewing request to seqNum " + paramLegacyRequest.expireSequenceNumber);
    sendExpireMsgForFeature(paramLegacyRequest.networkCapabilities, paramLegacyRequest.expireSequenceNumber, paramLegacyRequest.delay);
  }
  
  private NetworkRequest requestNetworkForFeatureLocked(NetworkCapabilities paramNetworkCapabilities)
  {
    int i = legacyTypeForNetworkCapabilities(paramNetworkCapabilities);
    int j;
    LegacyRequest localLegacyRequest;
    try
    {
      j = this.mService.getRestoreDefaultNetworkDelay(i);
      localLegacyRequest = new LegacyRequest(null);
      localLegacyRequest.networkCapabilities = paramNetworkCapabilities;
      localLegacyRequest.delay = j;
      localLegacyRequest.expireSequenceNumber = 0;
      localLegacyRequest.networkRequest = sendRequestForNetwork(paramNetworkCapabilities, localLegacyRequest.networkCallback, 0, 2, i);
      if (localLegacyRequest.networkRequest == null) {
        return null;
      }
    }
    catch (RemoteException paramNetworkCapabilities)
    {
      throw paramNetworkCapabilities.rethrowFromSystemServer();
    }
    sLegacyRequests.put(paramNetworkCapabilities, localLegacyRequest);
    sendExpireMsgForFeature(paramNetworkCapabilities, localLegacyRequest.expireSequenceNumber, j);
    return localLegacyRequest.networkRequest;
  }
  
  private void sendExpireMsgForFeature(NetworkCapabilities paramNetworkCapabilities, int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 0)
    {
      Log.d("ConnectivityManager", "sending expire msg with seqNum " + paramInt1 + " and delay " + paramInt2);
      paramNetworkCapabilities = sCallbackHandler.obtainMessage(524298, paramInt1, 0, paramNetworkCapabilities);
      sCallbackHandler.sendMessageDelayed(paramNetworkCapabilities, paramInt2);
    }
  }
  
  /* Error */
  private NetworkRequest sendRequestForNetwork(NetworkCapabilities paramNetworkCapabilities, NetworkCallback paramNetworkCallback, int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnonnull +14 -> 15
    //   4: new 356	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 679
    //   11: invokespecial 361	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_1
    //   16: ifnonnull +20 -> 36
    //   19: iload 4
    //   21: iconst_2
    //   22: if_icmpeq +14 -> 36
    //   25: new 356	java/lang/IllegalArgumentException
    //   28: dup
    //   29: ldc_w 681
    //   32: invokespecial 361	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   35: athrow
    //   36: aload_0
    //   37: invokespecial 683	android/net/ConnectivityManager:incCallbackHandlerRefCount	()V
    //   40: getstatic 290	android/net/ConnectivityManager:sNetworkCallback	Ljava/util/HashMap;
    //   43: astore 6
    //   45: aload 6
    //   47: monitorenter
    //   48: iload 4
    //   50: iconst_1
    //   51: if_icmpne +73 -> 124
    //   54: aload_2
    //   55: aload_0
    //   56: getfield 272	android/net/ConnectivityManager:mService	Landroid/net/IConnectivityManager;
    //   59: aload_1
    //   60: new 685	android/os/Messenger
    //   63: dup
    //   64: getstatic 299	android/net/ConnectivityManager:sCallbackHandler	Landroid/net/ConnectivityManager$CallbackHandler;
    //   67: invokespecial 688	android/os/Messenger:<init>	(Landroid/os/Handler;)V
    //   70: new 328	android/os/Binder
    //   73: dup
    //   74: invokespecial 689	android/os/Binder:<init>	()V
    //   77: invokeinterface 693 4 0
    //   82: invokestatic 697	android/net/ConnectivityManager$NetworkCallback:-set0	(Landroid/net/ConnectivityManager$NetworkCallback;Landroid/net/NetworkRequest;)Landroid/net/NetworkRequest;
    //   85: pop
    //   86: aload_2
    //   87: invokestatic 700	android/net/ConnectivityManager$NetworkCallback:-get0	(Landroid/net/ConnectivityManager$NetworkCallback;)Landroid/net/NetworkRequest;
    //   90: ifnull +15 -> 105
    //   93: getstatic 290	android/net/ConnectivityManager:sNetworkCallback	Ljava/util/HashMap;
    //   96: aload_2
    //   97: invokestatic 700	android/net/ConnectivityManager$NetworkCallback:-get0	(Landroid/net/ConnectivityManager$NetworkCallback;)Landroid/net/NetworkRequest;
    //   100: aload_2
    //   101: invokevirtual 666	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   104: pop
    //   105: aload 6
    //   107: monitorexit
    //   108: aload_2
    //   109: invokestatic 700	android/net/ConnectivityManager$NetworkCallback:-get0	(Landroid/net/ConnectivityManager$NetworkCallback;)Landroid/net/NetworkRequest;
    //   112: ifnonnull +7 -> 119
    //   115: aload_0
    //   116: invokespecial 702	android/net/ConnectivityManager:decCallbackHandlerRefCount	()V
    //   119: aload_2
    //   120: invokestatic 700	android/net/ConnectivityManager$NetworkCallback:-get0	(Landroid/net/ConnectivityManager$NetworkCallback;)Landroid/net/NetworkRequest;
    //   123: areturn
    //   124: aload_2
    //   125: aload_0
    //   126: getfield 272	android/net/ConnectivityManager:mService	Landroid/net/IConnectivityManager;
    //   129: aload_1
    //   130: new 685	android/os/Messenger
    //   133: dup
    //   134: getstatic 299	android/net/ConnectivityManager:sCallbackHandler	Landroid/net/ConnectivityManager$CallbackHandler;
    //   137: invokespecial 688	android/os/Messenger:<init>	(Landroid/os/Handler;)V
    //   140: iload_3
    //   141: new 328	android/os/Binder
    //   144: dup
    //   145: invokespecial 689	android/os/Binder:<init>	()V
    //   148: iload 5
    //   150: invokeinterface 706 6 0
    //   155: invokestatic 697	android/net/ConnectivityManager$NetworkCallback:-set0	(Landroid/net/ConnectivityManager$NetworkCallback;Landroid/net/NetworkRequest;)Landroid/net/NetworkRequest;
    //   158: pop
    //   159: goto -73 -> 86
    //   162: astore_1
    //   163: aload 6
    //   165: monitorexit
    //   166: aload_1
    //   167: athrow
    //   168: astore_1
    //   169: aload_1
    //   170: invokevirtual 663	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   173: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	174	0	this	ConnectivityManager
    //   0	174	1	paramNetworkCapabilities	NetworkCapabilities
    //   0	174	2	paramNetworkCallback	NetworkCallback
    //   0	174	3	paramInt1	int
    //   0	174	4	paramInt2	int
    //   0	174	5	paramInt3	int
    //   43	121	6	localHashMap	HashMap
    // Exception table:
    //   from	to	target	type
    //   54	86	162	finally
    //   86	105	162	finally
    //   124	159	162	finally
    //   36	48	168	android/os/RemoteException
    //   105	108	168	android/os/RemoteException
    //   163	168	168	android/os/RemoteException
  }
  
  public static boolean setProcessDefaultNetwork(Network paramNetwork)
  {
    if (paramNetwork == null) {}
    for (int i = 0; i == NetworkUtils.getBoundNetworkForProcess(); i = paramNetwork.netId) {
      return true;
    }
    if (NetworkUtils.bindProcessToNetwork(i)) {
      try
      {
        Proxy.setHttpProxySystemProperty(getInstance().getDefaultProxy());
        InetAddress.clearDnsCache();
        NetworkEventDispatcher.getInstance().onNetworkConfigurationChanged();
        return true;
      }
      catch (SecurityException paramNetwork)
      {
        for (;;)
        {
          Log.e("ConnectivityManager", "Can't set proxy properties", paramNetwork);
        }
      }
    }
    return false;
  }
  
  public static boolean setProcessDefaultNetworkForHostResolution(Network paramNetwork)
  {
    if (paramNetwork == null) {}
    for (int i = 0;; i = paramNetwork.netId) {
      return NetworkUtils.bindProcessToNetworkForHostResolution(i);
    }
  }
  
  private void unsupportedStartingFrom(int paramInt)
  {
    if (Process.myUid() == 1000) {
      return;
    }
    if (this.mContext.getApplicationInfo().targetSdkVersion >= paramInt) {
      throw new UnsupportedOperationException("This method is not supported in target SDK version " + paramInt + " and above");
    }
  }
  
  private static final String whatToString(int paramInt)
  {
    return (String)NoPreloadHolder.sMagicDecoderRing.get(paramInt, Integer.toString(paramInt));
  }
  
  public void addDefaultNetworkActiveListener(final OnNetworkActiveListener paramOnNetworkActiveListener)
  {
    INetworkActivityListener.Stub local1 = new INetworkActivityListener.Stub()
    {
      public void onNetworkActive()
        throws RemoteException
      {
        paramOnNetworkActiveListener.onNetworkActive();
      }
    };
    try
    {
      getNetworkManagementService().registerNetworkActivityListener(local1);
      this.mNetworkActivityListeners.put(paramOnNetworkActiveListener, local1);
      return;
    }
    catch (RemoteException paramOnNetworkActiveListener)
    {
      throw paramOnNetworkActiveListener.rethrowFromSystemServer();
    }
  }
  
  public boolean bindProcessToNetwork(Network paramNetwork)
  {
    return setProcessDefaultNetwork(paramNetwork);
  }
  
  public boolean blackListWifiDevice(String paramString, boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.blackListWifiDevice(paramString, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException paramString) {}
    return false;
  }
  
  public int checkMobileProvisioning(int paramInt)
  {
    try
    {
      paramInt = this.mService.checkMobileProvisioning(paramInt);
      return paramInt;
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
  
  public LinkProperties getActiveLinkProperties()
  {
    try
    {
      LinkProperties localLinkProperties = this.mService.getActiveLinkProperties();
      return localLinkProperties;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Network getActiveNetwork()
  {
    try
    {
      Network localNetwork = this.mService.getActiveNetwork();
      return localNetwork;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Network getActiveNetworkForUid(int paramInt)
  {
    return getActiveNetworkForUid(paramInt, false);
  }
  
  public Network getActiveNetworkForUid(int paramInt, boolean paramBoolean)
  {
    try
    {
      Network localNetwork = this.mService.getActiveNetworkForUid(paramInt, paramBoolean);
      return localNetwork;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkInfo getActiveNetworkInfo()
  {
    try
    {
      NetworkInfo localNetworkInfo = this.mService.getActiveNetworkInfo();
      return localNetworkInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkInfo getActiveNetworkInfoForUid(int paramInt)
  {
    return getActiveNetworkInfoForUid(paramInt, false);
  }
  
  public NetworkInfo getActiveNetworkInfoForUid(int paramInt, boolean paramBoolean)
  {
    try
    {
      NetworkInfo localNetworkInfo = this.mService.getActiveNetworkInfoForUid(paramInt, paramBoolean);
      return localNetworkInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkQuotaInfo getActiveNetworkQuotaInfo()
  {
    try
    {
      NetworkQuotaInfo localNetworkQuotaInfo = this.mService.getActiveNetworkQuotaInfo();
      return localNetworkQuotaInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkInfo[] getAllNetworkInfo()
  {
    try
    {
      NetworkInfo[] arrayOfNetworkInfo = this.mService.getAllNetworkInfo();
      return arrayOfNetworkInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Network[] getAllNetworks()
  {
    try
    {
      Network[] arrayOfNetwork = this.mService.getAllNetworks();
      return arrayOfNetwork;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getAlwaysOnVpnPackageForUser(int paramInt)
  {
    try
    {
      String str = this.mService.getAlwaysOnVpnPackage(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean getBackgroundDataSetting()
  {
    return true;
  }
  
  public Network getBoundNetworkForProcess()
  {
    return getProcessDefaultNetwork();
  }
  
  public String getCaptivePortalServerUrl()
  {
    try
    {
      String str = this.mService.getCaptivePortalServerUrl();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int paramInt)
  {
    try
    {
      NetworkCapabilities[] arrayOfNetworkCapabilities = this.mService.getDefaultNetworkCapabilitiesForUser(paramInt);
      return arrayOfNetworkCapabilities;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ProxyInfo getDefaultProxy()
  {
    return getProxyForNetwork(getBoundNetworkForProcess());
  }
  
  public ProxyInfo getGlobalProxy()
  {
    try
    {
      ProxyInfo localProxyInfo = this.mService.getGlobalProxy();
      return localProxyInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getLastTetherError(String paramString)
  {
    try
    {
      int i = this.mService.getLastTetherError(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public LinkProperties getLinkProperties(int paramInt)
  {
    try
    {
      LinkProperties localLinkProperties = this.mService.getLinkPropertiesForType(paramInt);
      return localLinkProperties;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public LinkProperties getLinkProperties(Network paramNetwork)
  {
    try
    {
      paramNetwork = this.mService.getLinkProperties(paramNetwork);
      return paramNetwork;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public boolean getMobileDataEnabled()
  {
    Object localObject = ServiceManager.getService("phone");
    if (localObject != null) {
      try
      {
        localObject = ITelephony.Stub.asInterface((IBinder)localObject);
        int i = SubscriptionManager.getDefaultDataSubscriptionId();
        Log.d("ConnectivityManager", "getMobileDataEnabled()+ subId=" + i);
        boolean bool = ((ITelephony)localObject).getDataEnabled(i);
        Log.d("ConnectivityManager", "getMobileDataEnabled()- subId=" + i + " retVal=" + bool);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    Log.d("ConnectivityManager", "getMobileDataEnabled()- remote exception retVal=false");
    return false;
  }
  
  public String getMobileProvisioningUrl()
  {
    try
    {
      String str = this.mService.getMobileProvisioningUrl();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkCapabilities getNetworkCapabilities(Network paramNetwork)
  {
    try
    {
      paramNetwork = this.mService.getNetworkCapabilities(paramNetwork);
      return paramNetwork;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public Network getNetworkForType(int paramInt)
  {
    try
    {
      Network localNetwork = this.mService.getNetworkForType(paramInt);
      return localNetwork;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkInfo getNetworkInfo(int paramInt)
  {
    try
    {
      NetworkInfo localNetworkInfo = this.mService.getNetworkInfo(paramInt);
      return localNetworkInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public NetworkInfo getNetworkInfo(Network paramNetwork)
  {
    return getNetworkInfoForUid(paramNetwork, Process.myUid(), false);
  }
  
  public NetworkInfo getNetworkInfoForUid(Network paramNetwork, int paramInt, boolean paramBoolean)
  {
    try
    {
      paramNetwork = this.mService.getNetworkInfoForUid(paramNetwork, paramInt, paramBoolean);
      return paramNetwork;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public int getNetworkPreference()
  {
    return -1;
  }
  
  public ProxyInfo getProxyForNetwork(Network paramNetwork)
  {
    try
    {
      paramNetwork = this.mService.getProxyForNetwork(paramNetwork);
      return paramNetwork;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public int getRestrictBackgroundStatus()
  {
    try
    {
      int i = getNetworkPolicyManager().getRestrictBackgroundByCaller();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<WifiDevice> getTetherConnectedSta()
  {
    try
    {
      List localList = this.mService.getTetherConnectedSta();
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public List<WifiDevice> getTetherSoftApSta(int paramInt)
  {
    try
    {
      List localList = this.mService.getTetherSoftApSta(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public String[] getTetherableBluetoothRegexs()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetherableBluetoothRegexs();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetherableIfaces()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetherableIfaces();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetherableUsbRegexs()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetherableUsbRegexs();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetherableWifiRegexs()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetherableWifiRegexs();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetheredDhcpRanges()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetheredDhcpRanges();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetheredIfaces()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetheredIfaces();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String[] getTetheringErroredIfaces()
  {
    try
    {
      String[] arrayOfString = this.mService.getTetheringErroredIfaces();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isActiveNetworkMetered()
  {
    try
    {
      boolean bool = this.mService.isActiveNetworkMetered();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isDefaultNetworkActive()
  {
    try
    {
      boolean bool = getNetworkManagementService().isNetworkActive();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isNetworkSupported(int paramInt)
  {
    try
    {
      boolean bool = this.mService.isNetworkSupported(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isTetheringSupported()
  {
    try
    {
      boolean bool = this.mService.isTetheringSupported();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void registerDefaultNetworkCallback(NetworkCallback paramNetworkCallback)
  {
    sendRequestForNetwork(null, paramNetworkCallback, 0, 2, -1);
  }
  
  public int registerNetworkAgent(Messenger paramMessenger, NetworkInfo paramNetworkInfo, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt, NetworkMisc paramNetworkMisc)
  {
    try
    {
      paramInt = this.mService.registerNetworkAgent(paramMessenger, paramNetworkInfo, paramLinkProperties, paramNetworkCapabilities, paramInt, paramNetworkMisc);
      return paramInt;
    }
    catch (RemoteException paramMessenger)
    {
      throw paramMessenger.rethrowFromSystemServer();
    }
  }
  
  public void registerNetworkCallback(NetworkRequest paramNetworkRequest, PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    try
    {
      this.mService.pendingListenForNetwork(paramNetworkRequest.networkCapabilities, paramPendingIntent);
      return;
    }
    catch (RemoteException paramNetworkRequest)
    {
      throw paramNetworkRequest.rethrowFromSystemServer();
    }
  }
  
  public void registerNetworkCallback(NetworkRequest paramNetworkRequest, NetworkCallback paramNetworkCallback)
  {
    sendRequestForNetwork(paramNetworkRequest.networkCapabilities, paramNetworkCallback, 0, 1, -1);
  }
  
  public void registerNetworkFactory(Messenger paramMessenger, String paramString)
  {
    try
    {
      this.mService.registerNetworkFactory(paramMessenger, paramString);
      return;
    }
    catch (RemoteException paramMessenger)
    {
      throw paramMessenger.rethrowFromSystemServer();
    }
  }
  
  public void releaseNetworkRequest(PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    try
    {
      this.mService.releasePendingNetworkRequest(paramPendingIntent);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public void removeDefaultNetworkActiveListener(OnNetworkActiveListener paramOnNetworkActiveListener)
  {
    INetworkActivityListener localINetworkActivityListener = (INetworkActivityListener)this.mNetworkActivityListeners.get(paramOnNetworkActiveListener);
    if (localINetworkActivityListener == null) {
      throw new IllegalArgumentException("Listener not registered: " + paramOnNetworkActiveListener);
    }
    try
    {
      getNetworkManagementService().unregisterNetworkActivityListener(localINetworkActivityListener);
      return;
    }
    catch (RemoteException paramOnNetworkActiveListener)
    {
      throw paramOnNetworkActiveListener.rethrowFromSystemServer();
    }
  }
  
  public void reportBadNetwork(Network paramNetwork)
  {
    try
    {
      this.mService.reportNetworkConnectivity(paramNetwork, true);
      this.mService.reportNetworkConnectivity(paramNetwork, false);
      return;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public void reportInetCondition(int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.reportInetCondition(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportNetworkConnectivity(Network paramNetwork, boolean paramBoolean)
  {
    try
    {
      this.mService.reportNetworkConnectivity(paramNetwork, paramBoolean);
      return;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public boolean requestBandwidthUpdate(Network paramNetwork)
  {
    try
    {
      boolean bool = this.mService.requestBandwidthUpdate(paramNetwork);
      return bool;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public void requestLinkProperties(NetworkCallback paramNetworkCallback)
  {
    try
    {
      this.mService.requestLinkProperties(NetworkCallback.-get0(paramNetworkCallback));
      return;
    }
    catch (RemoteException paramNetworkCallback)
    {
      throw paramNetworkCallback.rethrowFromSystemServer();
    }
  }
  
  public void requestNetwork(NetworkRequest paramNetworkRequest, PendingIntent paramPendingIntent)
  {
    checkPendingIntent(paramPendingIntent);
    try
    {
      this.mService.pendingRequestForNetwork(paramNetworkRequest.networkCapabilities, paramPendingIntent);
      return;
    }
    catch (RemoteException paramNetworkRequest)
    {
      throw paramNetworkRequest.rethrowFromSystemServer();
    }
  }
  
  public void requestNetwork(NetworkRequest paramNetworkRequest, NetworkCallback paramNetworkCallback)
  {
    requestNetwork(paramNetworkRequest, paramNetworkCallback, 0, inferLegacyTypeForNetworkCapabilities(paramNetworkRequest.networkCapabilities));
  }
  
  public void requestNetwork(NetworkRequest paramNetworkRequest, NetworkCallback paramNetworkCallback, int paramInt)
  {
    requestNetwork(paramNetworkRequest, paramNetworkCallback, paramInt, inferLegacyTypeForNetworkCapabilities(paramNetworkRequest.networkCapabilities));
  }
  
  public void requestNetwork(NetworkRequest paramNetworkRequest, NetworkCallback paramNetworkCallback, int paramInt1, int paramInt2)
  {
    sendRequestForNetwork(paramNetworkRequest.networkCapabilities, paramNetworkCallback, paramInt1, 2, paramInt2);
  }
  
  public void requestNetworkCapabilities(NetworkCallback paramNetworkCallback)
  {
    try
    {
      this.mService.requestNetworkCapabilities(NetworkCallback.-get0(paramNetworkCallback));
      return;
    }
    catch (RemoteException paramNetworkCallback)
    {
      throw paramNetworkCallback.rethrowFromSystemServer();
    }
  }
  
  public boolean requestRouteToHost(int paramInt1, int paramInt2)
  {
    return requestRouteToHostAddress(paramInt1, NetworkUtils.intToInetAddress(paramInt2));
  }
  
  public boolean requestRouteToHostAddress(int paramInt, InetAddress paramInetAddress)
  {
    checkLegacyRoutingApiAccess();
    try
    {
      boolean bool = this.mService.requestRouteToHostAddress(paramInt, paramInetAddress.getAddress());
      return bool;
    }
    catch (RemoteException paramInetAddress)
    {
      throw paramInetAddress.rethrowFromSystemServer();
    }
  }
  
  public void setAcceptUnvalidated(Network paramNetwork, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mService.setAcceptUnvalidated(paramNetwork, paramBoolean1, paramBoolean2);
      return;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  public void setAirplaneMode(boolean paramBoolean)
  {
    try
    {
      this.mService.setAirplaneMode(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean setAlwaysOnVpnPackageForUser(int paramInt, String paramString, boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.setAlwaysOnVpnPackage(paramInt, paramString, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setAvoidUnvalidated(Network paramNetwork)
  {
    try
    {
      this.mService.setAvoidUnvalidated(paramNetwork);
      return;
    }
    catch (RemoteException paramNetwork)
    {
      throw paramNetwork.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setBackgroundDataSetting(boolean paramBoolean) {}
  
  public void setGlobalProxy(ProxyInfo paramProxyInfo)
  {
    try
    {
      this.mService.setGlobalProxy(paramProxyInfo);
      return;
    }
    catch (RemoteException paramProxyInfo)
    {
      throw paramProxyInfo.rethrowFromSystemServer();
    }
  }
  
  public void setNetworkPreference(int paramInt) {}
  
  public void setProvisioningNotificationVisible(boolean paramBoolean, int paramInt, String paramString)
  {
    try
    {
      this.mService.setProvisioningNotificationVisible(paramBoolean, paramInt, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int setUsbTethering(boolean paramBoolean)
  {
    try
    {
      int i = this.mService.setUsbTethering(paramBoolean);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public PacketKeepalive startNattKeepalive(Network paramNetwork, int paramInt1, PacketKeepaliveCallback paramPacketKeepaliveCallback, InetAddress paramInetAddress1, int paramInt2, InetAddress paramInetAddress2)
  {
    paramPacketKeepaliveCallback = new PacketKeepalive(paramNetwork, paramPacketKeepaliveCallback, null);
    try
    {
      this.mService.startNattKeepalive(paramNetwork, paramInt1, PacketKeepalive.-get1(paramPacketKeepaliveCallback), new Binder(), paramInetAddress1.getHostAddress(), paramInt2, paramInetAddress2.getHostAddress());
      return paramPacketKeepaliveCallback;
    }
    catch (RemoteException paramNetwork)
    {
      Log.e("ConnectivityManager", "Error starting packet keepalive: ", paramNetwork);
      paramPacketKeepaliveCallback.stopLooper();
    }
    return null;
  }
  
  public void startTethering(int paramInt, boolean paramBoolean, OnStartTetheringCallback paramOnStartTetheringCallback)
  {
    startTethering(paramInt, paramBoolean, paramOnStartTetheringCallback, null);
  }
  
  public void startTethering(int paramInt, boolean paramBoolean, final OnStartTetheringCallback paramOnStartTetheringCallback, Handler paramHandler)
  {
    paramOnStartTetheringCallback = new ResultReceiver(paramHandler)
    {
      protected void onReceiveResult(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramAnonymousInt == 0)
        {
          paramOnStartTetheringCallback.onTetheringStarted();
          return;
        }
        paramOnStartTetheringCallback.onTetheringFailed();
      }
    };
    try
    {
      this.mService.startTethering(paramInt, paramOnStartTetheringCallback, paramBoolean);
      return;
    }
    catch (RemoteException paramHandler)
    {
      Log.e("ConnectivityManager", "Exception trying to start tethering.", paramHandler);
      paramOnStartTetheringCallback.send(2, null);
    }
  }
  
  public int startUsingNetworkFeature(int paramInt, String arg2)
  {
    checkLegacyRoutingApiAccess();
    Object localObject1 = networkCapabilitiesForFeature(paramInt, ???);
    if (localObject1 == null)
    {
      Log.d("ConnectivityManager", "Can't satisfy startUsingNetworkFeature for " + paramInt + ", " + ???);
      return 3;
    }
    synchronized (sLegacyRequests)
    {
      LegacyRequest localLegacyRequest = (LegacyRequest)sLegacyRequests.get(localObject1);
      if (localLegacyRequest != null)
      {
        Log.d("ConnectivityManager", "renewing startUsingNetworkFeature request " + localLegacyRequest.networkRequest);
        renewRequestLocked(localLegacyRequest);
        localObject1 = localLegacyRequest.currentNetwork;
        if (localObject1 != null) {
          return 0;
        }
        return 1;
      }
      localObject1 = requestNetworkForFeatureLocked((NetworkCapabilities)localObject1);
      if (localObject1 != null)
      {
        Log.d("ConnectivityManager", "starting startUsingNetworkFeature for request " + localObject1);
        return 1;
      }
    }
    Log.d("ConnectivityManager", " request Failed");
    return 3;
  }
  
  public void stopTethering(int paramInt)
  {
    try
    {
      this.mService.stopTethering(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int stopUsingNetworkFeature(int paramInt, String paramString)
  {
    checkLegacyRoutingApiAccess();
    NetworkCapabilities localNetworkCapabilities = networkCapabilitiesForFeature(paramInt, paramString);
    if (localNetworkCapabilities == null)
    {
      Log.d("ConnectivityManager", "Can't satisfy stopUsingNetworkFeature for " + paramInt + ", " + paramString);
      return -1;
    }
    if (removeRequestForFeature(localNetworkCapabilities)) {
      Log.d("ConnectivityManager", "stopUsingNetworkFeature for " + paramInt + ", " + paramString);
    }
    return 1;
  }
  
  public int tether(String paramString)
  {
    try
    {
      int i = this.mService.tether(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void unregisterNetworkCallback(PendingIntent paramPendingIntent)
  {
    releaseNetworkRequest(paramPendingIntent);
  }
  
  public void unregisterNetworkCallback(NetworkCallback paramNetworkCallback)
  {
    if ((paramNetworkCallback == null) || (NetworkCallback.-get0(paramNetworkCallback) == null)) {}
    while (NetworkCallback.-get0(paramNetworkCallback).requestId == 0) {
      throw new IllegalArgumentException("Invalid NetworkCallback");
    }
    try
    {
      this.mService.releaseNetworkRequest(NetworkCallback.-get0(paramNetworkCallback));
      return;
    }
    catch (RemoteException paramNetworkCallback)
    {
      throw paramNetworkCallback.rethrowFromSystemServer();
    }
  }
  
  public void unregisterNetworkFactory(Messenger paramMessenger)
  {
    try
    {
      this.mService.unregisterNetworkFactory(paramMessenger);
      return;
    }
    catch (RemoteException paramMessenger)
    {
      throw paramMessenger.rethrowFromSystemServer();
    }
  }
  
  public int untether(String paramString)
  {
    try
    {
      int i = this.mService.untether(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean updateLockdownVpn()
  {
    try
    {
      boolean bool = this.mService.updateLockdownVpn();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private class CallbackHandler
    extends Handler
  {
    private static final boolean DBG = false;
    private static final String TAG = "ConnectivityManager.CallbackHandler";
    private final HashMap<NetworkRequest, ConnectivityManager.NetworkCallback> mCallbackMap;
    private final ConnectivityManager mCm;
    private final AtomicInteger mRefCount;
    
    CallbackHandler(HashMap<NetworkRequest, ConnectivityManager.NetworkCallback> paramHashMap, AtomicInteger paramAtomicInteger, ConnectivityManager paramConnectivityManager)
    {
      super();
      this.mCallbackMap = paramAtomicInteger;
      this.mRefCount = paramConnectivityManager;
      ConnectivityManager localConnectivityManager;
      this.mCm = localConnectivityManager;
    }
    
    private ConnectivityManager.NetworkCallback getCallback(NetworkRequest paramNetworkRequest, String paramString)
    {
      synchronized (this.mCallbackMap)
      {
        paramNetworkRequest = (ConnectivityManager.NetworkCallback)this.mCallbackMap.get(paramNetworkRequest);
        if (paramNetworkRequest == null) {
          Log.e("ConnectivityManager.CallbackHandler", "callback not found for " + paramString + " message");
        }
        return paramNetworkRequest;
      }
    }
    
    private Object getObject(Message paramMessage, Class paramClass)
    {
      return paramMessage.getData().getParcelable(paramClass.getSimpleName());
    }
    
    public void handleMessage(Message arg1)
    {
      Object localObject4 = (NetworkRequest)getObject(???, NetworkRequest.class);
      Object localObject1 = (Network)getObject(???, Network.class);
      switch (???.what)
      {
      default: 
      case 524289: 
      case 524290: 
      case 524291: 
      case 524292: 
      case 524293: 
      case 524294: 
      case 524295: 
      case 524299: 
      case 524300: 
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
                          ??? = getCallback((NetworkRequest)localObject4, "PRECHECK");
                        } while (??? == null);
                        ???.onPreCheck((Network)localObject1);
                        return;
                        ??? = getCallback((NetworkRequest)localObject4, "AVAILABLE");
                      } while (??? == null);
                      ???.onAvailable((Network)localObject1);
                      return;
                      localObject4 = getCallback((NetworkRequest)localObject4, "LOSING");
                    } while (localObject4 == null);
                    ((ConnectivityManager.NetworkCallback)localObject4).onLosing((Network)localObject1, ???.arg1);
                    return;
                    ??? = getCallback((NetworkRequest)localObject4, "LOST");
                  } while (??? == null);
                  ???.onLost((Network)localObject1);
                  return;
                  ??? = getCallback((NetworkRequest)localObject4, "UNAVAIL");
                } while (??? == null);
                ???.onUnavailable();
                return;
                localObject4 = getCallback((NetworkRequest)localObject4, "CAP_CHANGED");
              } while (localObject4 == null);
              ((ConnectivityManager.NetworkCallback)localObject4).onCapabilitiesChanged((Network)localObject1, (NetworkCapabilities)getObject(???, NetworkCapabilities.class));
              return;
              localObject4 = getCallback((NetworkRequest)localObject4, "IP_CHANGED");
            } while (localObject4 == null);
            ((ConnectivityManager.NetworkCallback)localObject4).onLinkPropertiesChanged((Network)localObject1, (LinkProperties)getObject(???, LinkProperties.class));
            return;
            ??? = getCallback((NetworkRequest)localObject4, "SUSPENDED");
          } while (??? == null);
          ???.onNetworkSuspended((Network)localObject1);
          return;
          ??? = getCallback((NetworkRequest)localObject4, "RESUMED");
        } while (??? == null);
        ???.onNetworkResumed((Network)localObject1);
        return;
      case 524296: 
        synchronized (this.mCallbackMap)
        {
          localObject1 = (ConnectivityManager.NetworkCallback)this.mCallbackMap.remove(localObject4);
          if (localObject1 == null) {
            break label336;
          }
        }
        synchronized (this.mRefCount)
        {
          if (this.mRefCount.decrementAndGet() == 0) {
            getLooper().quit();
          }
          return;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        Log.e("ConnectivityManager.CallbackHandler", "callback not found for RELEASED message");
        return;
      case 524297: 
        label336:
        Log.d("ConnectivityManager.CallbackHandler", "Listener quitting");
        getLooper().quit();
        return;
      }
      ConnectivityManager.-wrap0(ConnectivityManager.this, (NetworkCapabilities)???.obj, ???.arg1);
    }
  }
  
  private static class LegacyRequest
  {
    Network currentNetwork;
    int delay = -1;
    int expireSequenceNumber;
    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback()
    {
      public void onAvailable(Network paramAnonymousNetwork)
      {
        ConnectivityManager.LegacyRequest.this.currentNetwork = paramAnonymousNetwork;
        Log.d("ConnectivityManager", "startUsingNetworkFeature got Network:" + paramAnonymousNetwork);
        ConnectivityManager.setProcessDefaultNetworkForHostResolution(paramAnonymousNetwork);
      }
      
      public void onLost(Network paramAnonymousNetwork)
      {
        if (paramAnonymousNetwork.equals(ConnectivityManager.LegacyRequest.this.currentNetwork)) {
          ConnectivityManager.LegacyRequest.-wrap0(ConnectivityManager.LegacyRequest.this);
        }
        Log.d("ConnectivityManager", "startUsingNetworkFeature lost Network:" + paramAnonymousNetwork);
      }
    };
    NetworkCapabilities networkCapabilities;
    NetworkRequest networkRequest;
    
    private void clearDnsBinding()
    {
      if (this.currentNetwork != null)
      {
        this.currentNetwork = null;
        ConnectivityManager.setProcessDefaultNetworkForHostResolution(null);
      }
    }
  }
  
  public static class NetworkCallback
  {
    private NetworkRequest networkRequest;
    
    public void onAvailable(Network paramNetwork) {}
    
    public void onCapabilitiesChanged(Network paramNetwork, NetworkCapabilities paramNetworkCapabilities) {}
    
    public void onLinkPropertiesChanged(Network paramNetwork, LinkProperties paramLinkProperties) {}
    
    public void onLosing(Network paramNetwork, int paramInt) {}
    
    public void onLost(Network paramNetwork) {}
    
    public void onNetworkResumed(Network paramNetwork) {}
    
    public void onNetworkSuspended(Network paramNetwork) {}
    
    public void onPreCheck(Network paramNetwork) {}
    
    public void onUnavailable() {}
  }
  
  private static class NoPreloadHolder
  {
    public static final SparseArray<String> sMagicDecoderRing = MessageUtils.findMessageNames(new Class[] { ConnectivityManager.class }, new String[] { "CALLBACK_" });
  }
  
  public static abstract interface OnNetworkActiveListener
  {
    public abstract void onNetworkActive();
  }
  
  public static abstract class OnStartTetheringCallback
  {
    public void onTetheringFailed() {}
    
    public void onTetheringStarted() {}
  }
  
  public class PacketKeepalive
  {
    public static final int BINDER_DIED = -10;
    public static final int ERROR_HARDWARE_ERROR = -31;
    public static final int ERROR_HARDWARE_UNSUPPORTED = -30;
    public static final int ERROR_INVALID_INTERVAL = -24;
    public static final int ERROR_INVALID_IP_ADDRESS = -21;
    public static final int ERROR_INVALID_LENGTH = -23;
    public static final int ERROR_INVALID_NETWORK = -20;
    public static final int ERROR_INVALID_PORT = -22;
    public static final int NATT_PORT = 4500;
    public static final int NO_KEEPALIVE = -1;
    public static final int SUCCESS = 0;
    private static final String TAG = "PacketKeepalive";
    private final ConnectivityManager.PacketKeepaliveCallback mCallback;
    private final Looper mLooper;
    private final Messenger mMessenger;
    private final Network mNetwork;
    private volatile Integer mSlot;
    
    private PacketKeepalive(Network paramNetwork, ConnectivityManager.PacketKeepaliveCallback paramPacketKeepaliveCallback)
    {
      Preconditions.checkNotNull(paramNetwork, "network cannot be null");
      Preconditions.checkNotNull(paramPacketKeepaliveCallback, "callback cannot be null");
      this.mNetwork = paramNetwork;
      this.mCallback = paramPacketKeepaliveCallback;
      this$1 = new HandlerThread("PacketKeepalive");
      ConnectivityManager.this.start();
      this.mLooper = ConnectivityManager.this.getLooper();
      this.mMessenger = new Messenger(new Handler(this.mLooper)
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          default: 
            Log.e("PacketKeepalive", "Unhandled message " + Integer.toHexString(paramAnonymousMessage.what));
            return;
          }
          int i = paramAnonymousMessage.arg2;
          if (i == 0)
          {
            try
            {
              if (ConnectivityManager.PacketKeepalive.-get2(ConnectivityManager.PacketKeepalive.this) == null)
              {
                ConnectivityManager.PacketKeepalive.-set0(ConnectivityManager.PacketKeepalive.this, Integer.valueOf(paramAnonymousMessage.arg1));
                ConnectivityManager.PacketKeepalive.-get0(ConnectivityManager.PacketKeepalive.this).onStarted();
                return;
              }
            }
            catch (Exception paramAnonymousMessage)
            {
              Log.e("PacketKeepalive", "Exception in keepalive callback(" + i + ")", paramAnonymousMessage);
              return;
            }
            ConnectivityManager.PacketKeepalive.-set0(ConnectivityManager.PacketKeepalive.this, null);
            ConnectivityManager.PacketKeepalive.this.stopLooper();
            ConnectivityManager.PacketKeepalive.-get0(ConnectivityManager.PacketKeepalive.this).onStopped();
            return;
          }
          ConnectivityManager.PacketKeepalive.this.stopLooper();
          ConnectivityManager.PacketKeepalive.-get0(ConnectivityManager.PacketKeepalive.this).onError(i);
        }
      });
    }
    
    public void stop()
    {
      try
      {
        ConnectivityManager.-get0(ConnectivityManager.this).stopKeepalive(this.mNetwork, this.mSlot.intValue());
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("PacketKeepalive", "Error stopping packet keepalive: ", localRemoteException);
        stopLooper();
      }
    }
    
    void stopLooper()
    {
      this.mLooper.quit();
    }
  }
  
  public static class PacketKeepaliveCallback
  {
    public void onError(int paramInt) {}
    
    public void onStarted() {}
    
    public void onStopped() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ConnectivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */