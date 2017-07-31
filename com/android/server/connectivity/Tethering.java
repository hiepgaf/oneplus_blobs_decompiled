package com.android.server.connectivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.INetworkPolicyManager;
import android.net.INetworkStatsService;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.NetworkState;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.WifiDevice;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.IoThread;
import com.android.server.NetPluginDelegate;
import com.android.server.connectivity.tethering.IControlsTethering;
import com.android.server.connectivity.tethering.IPv6TetheringCoordinator;
import com.android.server.connectivity.tethering.TetherInterfaceStateMachine;
import com.android.server.net.BaseNetworkObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Tethering
  extends BaseNetworkObserver
  implements IControlsTethering
{
  private static final long AUTO_SHUT_DOWN_SOFTAP_MS = 300000L;
  private static final int AUTO_SHUT_DOWN_SOFTAP_REQUEST = 1;
  private static final int BLACKED_LIST = 0;
  private static final boolean DBG = true;
  private static final String[] DHCP_DEFAULT_RANGE = { "192.168.42.2", "192.168.42.254", "192.168.43.2", "192.168.43.254", "192.168.44.2", "192.168.44.254", "192.168.45.2", "192.168.45.254", "192.168.46.2", "192.168.46.254", "192.168.47.2", "192.168.47.254", "192.168.48.2", "192.168.48.254", "192.168.49.2", "192.168.49.254" };
  private static final int DNSMASQ_POLLING_INTERVAL = 1000;
  private static final int DNSMASQ_POLLING_MAX_TIMES = 10;
  private static final String DNS_DEFAULT_SERVER1 = "8.8.8.8";
  private static final String DNS_DEFAULT_SERVER2 = "8.8.4.4";
  private static final Integer DUN_TYPE;
  private static final String GROUP_NAME = "com.android.server.connectivity.tethering";
  private static final Integer HIPRI_TYPE;
  private static final Integer MOBILE_TYPE;
  private static final int SOFTAP_AUTO_SHUT_DOWN_OFF = 0;
  private static final String TAG = "Tethering";
  private static final ComponentName TETHER_SERVICE;
  private static final int UNBLACKED_LIST = 1;
  private static final boolean VDBG = true;
  private static final String defaultSoftApIfaceName = "wlan0";
  private static final String dhcpLocation = "/data/misc/dhcp/dnsmasq.leases";
  private static HashMap<String, WifiDevice> mSoftApDeviceMap = new HashMap();
  private static int mWifiApState = 11;
  private static final Class[] messageClasses = { Tethering.class, TetherMasterSM.class, TetherInterfaceStateMachine.class };
  private static final SparseArray<String> sMagicDecoderRing = MessageUtils.findMessageNames(messageClasses);
  private final String SOFTAP_CONCURRENCY_INTERFACE = "softap0";
  private AlarmManager mAlarmManager;
  private boolean mBluetoothTethered = false;
  private HashMap<String, WifiDevice> mConnectedDeviceMap = new HashMap();
  private final Context mContext;
  private String mCurrentUpstreamIface;
  private String[] mDefaultDnsServers;
  private String[] mDhcpRange;
  private PendingIntent mIntentAutoShutDownSoftAP;
  private HashMap<String, WifiDevice> mL2ConnectedDeviceMap = new HashMap();
  private int mLastNotificationId;
  private final Looper mLooper;
  private final INetworkManagementService mNMService;
  private final INetworkPolicyManager mPolicyManager;
  private int mPreferredUpstreamMobileApn = -1;
  private final Object mPublicSync;
  private int mRestoreWifiBand;
  private boolean mRndisEnabled;
  private SoftApAutoShutDownObserver mSoftApAutoShutDownObserver;
  private final BroadcastReceiver mStateReceiver;
  private final INetworkStatsService mStatsService;
  private final StateMachine mTetherMasterSM;
  private final ArrayMap<String, TetherState> mTetherStates;
  private String[] mTetherableBluetoothRegexs;
  private String[] mTetherableUsbRegexs;
  private String[] mTetherableWifiRegexs;
  private Notification.Builder mTetheredNotificationBuilder;
  private Collection<Integer> mUpstreamIfaceTypes;
  private final UpstreamNetworkMonitor mUpstreamNetworkMonitor;
  private boolean mUsbTetherRequested;
  private boolean mUsbTethered = false;
  private boolean mWifiTetherRequested;
  private boolean mWifiTethered = false;
  
  static
  {
    MOBILE_TYPE = new Integer(0);
    HIPRI_TYPE = new Integer(5);
    DUN_TYPE = new Integer(4);
    TETHER_SERVICE = ComponentName.unflattenFromString(Resources.getSystem().getString(17039414));
  }
  
  public Tethering(Context paramContext, INetworkManagementService paramINetworkManagementService, INetworkStatsService paramINetworkStatsService, INetworkPolicyManager paramINetworkPolicyManager)
  {
    this.mContext = paramContext;
    this.mNMService = paramINetworkManagementService;
    this.mStatsService = paramINetworkStatsService;
    this.mPolicyManager = paramINetworkPolicyManager;
    this.mPublicSync = new Object();
    this.mTetherStates = new ArrayMap();
    this.mLooper = IoThread.get().getLooper();
    this.mTetherMasterSM = new TetherMasterSM("TetherMaster", this.mLooper);
    this.mTetherMasterSM.start();
    this.mUpstreamNetworkMonitor = new UpstreamNetworkMonitor();
    this.mStateReceiver = new StateReceiver(null);
    paramINetworkManagementService = new IntentFilter();
    paramINetworkManagementService.addAction("android.hardware.usb.action.USB_STATE");
    paramINetworkManagementService.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    paramINetworkManagementService.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
    paramINetworkManagementService.addAction("android.intent.action.CONFIGURATION_CHANGED");
    this.mContext.registerReceiver(this.mStateReceiver, paramINetworkManagementService);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    paramINetworkManagementService = new Intent("codeaurora.net.conn.TETHER_AUTO_SHUT_DOWN_SOFTAP");
    this.mIntentAutoShutDownSoftAP = PendingIntent.getBroadcast(this.mContext, 1, paramINetworkManagementService, 0);
    paramINetworkManagementService = new HandlerThread("SoftApAutoShutDown");
    paramINetworkManagementService.start();
    this.mSoftApAutoShutDownObserver = new SoftApAutoShutDownObserver(new Handler(paramINetworkManagementService.getLooper()));
    paramINetworkManagementService = new IntentFilter();
    paramINetworkManagementService.addAction("android.intent.action.MEDIA_SHARED");
    paramINetworkManagementService.addAction("android.intent.action.MEDIA_UNSHARED");
    paramINetworkManagementService.addDataScheme("file");
    this.mContext.registerReceiver(this.mStateReceiver, paramINetworkManagementService);
    this.mDhcpRange = paramContext.getResources().getStringArray(17235993);
    if ((this.mDhcpRange.length == 0) || (this.mDhcpRange.length % 2 == 1)) {
      this.mDhcpRange = DHCP_DEFAULT_RANGE;
    }
    updateConfiguration();
    this.mDefaultDnsServers = new String[2];
    this.mDefaultDnsServers[0] = "8.8.8.8";
    this.mDefaultDnsServers[1] = "8.8.4.4";
  }
  
  private void cancelTetherProvisioningRechecks(int paramInt)
  {
    Intent localIntent;
    long l;
    if (getConnectivityManager().isTetheringSupported())
    {
      localIntent = new Intent();
      localIntent.putExtra("extraRemTetherType", paramInt);
      localIntent.setComponent(TETHER_SERVICE);
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.mContext.startServiceAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void checkDunRequired()
  {
    int i = 2;
    Object localObject1 = (TelephonyManager)this.mContext.getSystemService("phone");
    if (localObject1 != null) {
      i = ((TelephonyManager)localObject1).getTetherApnRequired();
    }
    localObject1 = this.mPublicSync;
    if (i != 2)
    {
      if (i == 1) {
        i = 4;
      }
      for (;;)
      {
        if (i != 4) {
          break label172;
        }
        try
        {
          while (this.mUpstreamIfaceTypes.contains(MOBILE_TYPE)) {
            this.mUpstreamIfaceTypes.remove(MOBILE_TYPE);
          }
          i = 5;
        }
        finally {}
      }
      while (this.mUpstreamIfaceTypes.contains(HIPRI_TYPE)) {
        this.mUpstreamIfaceTypes.remove(HIPRI_TYPE);
      }
      if (!this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {
        this.mUpstreamIfaceTypes.add(DUN_TYPE);
      }
    }
    if (this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {}
    for (this.mPreferredUpstreamMobileApn = 4;; this.mPreferredUpstreamMobileApn = 5)
    {
      return;
      label172:
      while (this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {
        this.mUpstreamIfaceTypes.remove(DUN_TYPE);
      }
      if (!this.mUpstreamIfaceTypes.contains(MOBILE_TYPE)) {
        this.mUpstreamIfaceTypes.add(MOBILE_TYPE);
      }
      if (this.mUpstreamIfaceTypes.contains(HIPRI_TYPE)) {
        break;
      }
      this.mUpstreamIfaceTypes.add(HIPRI_TYPE);
      break;
    }
  }
  
  private void clearTetheredNotification()
  {
    NotificationManager localNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
    if ((localNotificationManager != null) && (this.mLastNotificationId != 0))
    {
      localNotificationManager.cancelAsUser(null, this.mLastNotificationId, UserHandle.ALL);
      this.mLastNotificationId = 0;
    }
  }
  
  private void enableTetheringInternal(int paramInt, boolean paramBoolean, ResultReceiver paramResultReceiver)
  {
    if (paramBoolean) {}
    for (boolean bool = isTetherProvisioningRequired();; bool = false) {
      switch (paramInt)
      {
      default: 
        Log.w("Tethering", "Invalid tether type.");
        sendTetherResult(paramResultReceiver, 1);
        return;
      }
    }
    int i = setWifiTethering(paramBoolean);
    if ((bool) && (i == 0)) {
      scheduleProvisioningRechecks(paramInt);
    }
    sendTetherResult(paramResultReceiver, i);
    return;
    i = setUsbTethering(paramBoolean);
    if ((bool) && (i == 0)) {
      scheduleProvisioningRechecks(paramInt);
    }
    sendTetherResult(paramResultReceiver, i);
    return;
    setBluetoothTethering(paramBoolean, paramResultReceiver);
  }
  
  private ConnectivityManager getConnectivityManager()
  {
    return (ConnectivityManager)this.mContext.getSystemService("connectivity");
  }
  
  private ResultReceiver getProxyReceiver(final int paramInt, final ResultReceiver paramResultReceiver)
  {
    Object localObject = new ResultReceiver(null)
    {
      protected void onReceiveResult(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramAnonymousInt == 0)
        {
          Tethering.-wrap4(Tethering.this, paramInt, true, paramResultReceiver);
          return;
        }
        Tethering.-wrap10(Tethering.this, paramResultReceiver, paramAnonymousInt);
      }
    };
    paramResultReceiver = Parcel.obtain();
    ((ResultReceiver)localObject).writeToParcel(paramResultReceiver, 0);
    paramResultReceiver.setDataPosition(0);
    localObject = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramResultReceiver);
    paramResultReceiver.recycle();
    return (ResultReceiver)localObject;
  }
  
  private int ifaceNameToType(String paramString)
  {
    if (isWifi(paramString))
    {
      if (SystemProperties.get("wigig.interface", "wigig0").equals(paramString)) {
        return 3;
      }
      return 0;
    }
    if (isUsb(paramString)) {
      return 1;
    }
    if (isBluetooth(paramString)) {
      return 2;
    }
    return -1;
  }
  
  private boolean isBluetooth(String paramString)
  {
    synchronized (this.mPublicSync)
    {
      String[] arrayOfString = this.mTetherableBluetoothRegexs;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        boolean bool = paramString.matches(arrayOfString[i]);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
  }
  
  private boolean isIpv6TetheringEnabled()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "enable_aosp_v6_tethering", 0) == 1;
  }
  
  private boolean isTetherProvisioningRequired()
  {
    boolean bool = false;
    String[] arrayOfString = this.mContext.getResources().getStringArray(17235994);
    if ((SystemProperties.getBoolean("net.tethering.noprovisioning", false)) || (arrayOfString == null)) {
      return false;
    }
    CarrierConfigManager localCarrierConfigManager = (CarrierConfigManager)this.mContext.getSystemService("carrier_config");
    if ((localCarrierConfigManager != null) && (localCarrierConfigManager.getConfig() != null) && (!localCarrierConfigManager.getConfig().getBoolean("require_entitlement_checks_bool"))) {
      return false;
    }
    if (arrayOfString.length == 2) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isUsb(String paramString)
  {
    synchronized (this.mPublicSync)
    {
      String[] arrayOfString = this.mTetherableUsbRegexs;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        boolean bool = paramString.matches(arrayOfString[i]);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
  }
  
  private boolean isWifi(String paramString)
  {
    synchronized (this.mPublicSync)
    {
      String[] arrayOfString = this.mTetherableWifiRegexs;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        boolean bool = paramString.matches(arrayOfString[i]);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
  }
  
  private void maybeLogMessage(State paramState, int paramInt)
  {
    Log.d("Tethering", paramState.getName() + " got " + (String)sMagicDecoderRing.get(paramInt, Integer.toString(paramInt)));
  }
  
  /* Error */
  private boolean readDeviceInfoFromDnsmasq(WifiDevice paramWifiDevice)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_0
    //   4: istore_3
    //   5: aconst_null
    //   6: astore 5
    //   8: aconst_null
    //   9: astore 7
    //   11: new 749	java/io/FileInputStream
    //   14: dup
    //   15: ldc 106
    //   17: invokespecial 750	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   20: astore 6
    //   22: new 752	java/io/BufferedReader
    //   25: dup
    //   26: new 754	java/io/InputStreamReader
    //   29: dup
    //   30: new 756	java/io/DataInputStream
    //   33: dup
    //   34: aload 6
    //   36: invokespecial 759	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   39: invokespecial 760	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   42: invokespecial 763	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   45: astore 5
    //   47: aload 5
    //   49: invokevirtual 766	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   52: astore 7
    //   54: iload_3
    //   55: istore_2
    //   56: aload 7
    //   58: ifnull +62 -> 120
    //   61: iload_3
    //   62: istore_2
    //   63: aload 7
    //   65: invokevirtual 769	java/lang/String:length	()I
    //   68: ifeq +52 -> 120
    //   71: aload 7
    //   73: ldc_w 771
    //   76: invokevirtual 775	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   79: astore 8
    //   81: aload 8
    //   83: arraylength
    //   84: iconst_3
    //   85: if_icmple -38 -> 47
    //   88: aload 8
    //   90: iconst_1
    //   91: aaload
    //   92: astore 7
    //   94: aload 8
    //   96: iconst_3
    //   97: aaload
    //   98: astore 8
    //   100: aload 7
    //   102: aload_1
    //   103: getfield 780	android/net/wifi/WifiDevice:deviceAddress	Ljava/lang/String;
    //   106: invokevirtual 668	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   109: ifeq -62 -> 47
    //   112: aload_1
    //   113: aload 8
    //   115: putfield 783	android/net/wifi/WifiDevice:deviceName	Ljava/lang/String;
    //   118: iconst_1
    //   119: istore_2
    //   120: aload 6
    //   122: ifnull +8 -> 130
    //   125: aload 6
    //   127: invokevirtual 786	java/io/FileInputStream:close	()V
    //   130: iload_2
    //   131: ireturn
    //   132: astore_1
    //   133: goto -3 -> 130
    //   136: astore 6
    //   138: aload 7
    //   140: astore_1
    //   141: aload_1
    //   142: astore 5
    //   144: ldc 96
    //   146: new 719	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 720	java/lang/StringBuilder:<init>	()V
    //   153: ldc_w 788
    //   156: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: aload 6
    //   161: invokevirtual 791	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   164: invokevirtual 742	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   167: invokestatic 794	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   170: pop
    //   171: iload 4
    //   173: istore_2
    //   174: aload_1
    //   175: ifnull -45 -> 130
    //   178: aload_1
    //   179: invokevirtual 786	java/io/FileInputStream:close	()V
    //   182: iconst_0
    //   183: ireturn
    //   184: astore_1
    //   185: iconst_0
    //   186: ireturn
    //   187: astore_1
    //   188: aload 5
    //   190: ifnull +8 -> 198
    //   193: aload 5
    //   195: invokevirtual 786	java/io/FileInputStream:close	()V
    //   198: aload_1
    //   199: athrow
    //   200: astore 5
    //   202: goto -4 -> 198
    //   205: astore_1
    //   206: aload 6
    //   208: astore 5
    //   210: goto -22 -> 188
    //   213: astore 5
    //   215: aload 6
    //   217: astore_1
    //   218: aload 5
    //   220: astore 6
    //   222: goto -81 -> 141
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	225	0	this	Tethering
    //   0	225	1	paramWifiDevice	WifiDevice
    //   55	119	2	bool1	boolean
    //   4	58	3	bool2	boolean
    //   1	171	4	bool3	boolean
    //   6	188	5	localObject1	Object
    //   200	1	5	localIOException1	java.io.IOException
    //   208	1	5	localObject2	Object
    //   213	6	5	localIOException2	java.io.IOException
    //   20	106	6	localFileInputStream	java.io.FileInputStream
    //   136	80	6	localIOException3	java.io.IOException
    //   220	1	6	localIOException4	java.io.IOException
    //   9	130	7	str	String
    //   79	35	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   125	130	132	java/io/IOException
    //   11	22	136	java/io/IOException
    //   178	182	184	java/io/IOException
    //   11	22	187	finally
    //   144	171	187	finally
    //   193	198	200	java/io/IOException
    //   22	47	205	finally
    //   47	54	205	finally
    //   63	88	205	finally
    //   100	118	205	finally
    //   22	47	213	java/io/IOException
    //   47	54	213	java/io/IOException
    //   63	88	213	java/io/IOException
    //   100	118	213	java/io/IOException
  }
  
  /* Error */
  private void resetAlarmTrigger()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 205	com/android/server/connectivity/Tethering:mContext	Landroid/content/Context;
    //   6: invokevirtual 684	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   9: ldc_w 796
    //   12: iconst_0
    //   13: invokestatic 799	android/provider/Settings$Secure:getInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)I
    //   16: istore_1
    //   17: aload_0
    //   18: getfield 219	com/android/server/connectivity/Tethering:mL2ConnectedDeviceMap	Ljava/util/HashMap;
    //   21: invokevirtual 802	java/util/HashMap:size	()I
    //   24: istore_2
    //   25: iload_1
    //   26: ifle +77 -> 103
    //   29: getstatic 240	com/android/server/connectivity/Tethering:mWifiApState	I
    //   32: bipush 13
    //   34: if_icmpne +69 -> 103
    //   37: iload_2
    //   38: ifne +65 -> 103
    //   41: ldc 96
    //   43: new 719	java/lang/StringBuilder
    //   46: dup
    //   47: invokespecial 720	java/lang/StringBuilder:<init>	()V
    //   50: ldc_w 804
    //   53: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: iload_1
    //   57: invokevirtual 807	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   60: invokevirtual 742	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokestatic 745	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   66: pop
    //   67: aload_0
    //   68: getfield 475	com/android/server/connectivity/Tethering:mAlarmManager	Landroid/app/AlarmManager;
    //   71: aload_0
    //   72: getfield 489	com/android/server/connectivity/Tethering:mIntentAutoShutDownSoftAP	Landroid/app/PendingIntent;
    //   75: invokevirtual 811	android/app/AlarmManager:cancel	(Landroid/app/PendingIntent;)V
    //   78: aload_0
    //   79: getfield 475	com/android/server/connectivity/Tethering:mAlarmManager	Landroid/app/AlarmManager;
    //   82: iconst_2
    //   83: invokestatic 816	android/os/SystemClock:elapsedRealtime	()J
    //   86: iload_1
    //   87: i2l
    //   88: ldc2_w 64
    //   91: lmul
    //   92: ladd
    //   93: aload_0
    //   94: getfield 489	com/android/server/connectivity/Tethering:mIntentAutoShutDownSoftAP	Landroid/app/PendingIntent;
    //   97: invokevirtual 820	android/app/AlarmManager:setExact	(IJLandroid/app/PendingIntent;)V
    //   100: aload_0
    //   101: monitorexit
    //   102: return
    //   103: ldc 96
    //   105: new 719	java/lang/StringBuilder
    //   108: dup
    //   109: invokespecial 720	java/lang/StringBuilder:<init>	()V
    //   112: ldc_w 822
    //   115: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: iload_1
    //   119: invokevirtual 807	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   122: ldc_w 824
    //   125: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: getstatic 240	com/android/server/connectivity/Tethering:mWifiApState	I
    //   131: invokevirtual 807	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   134: ldc_w 826
    //   137: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: iload_2
    //   141: invokevirtual 807	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   144: invokevirtual 742	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   147: invokestatic 745	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   150: pop
    //   151: aload_0
    //   152: getfield 475	com/android/server/connectivity/Tethering:mAlarmManager	Landroid/app/AlarmManager;
    //   155: aload_0
    //   156: getfield 489	com/android/server/connectivity/Tethering:mIntentAutoShutDownSoftAP	Landroid/app/PendingIntent;
    //   159: invokevirtual 811	android/app/AlarmManager:cancel	(Landroid/app/PendingIntent;)V
    //   162: goto -62 -> 100
    //   165: astore_3
    //   166: aload_0
    //   167: monitorexit
    //   168: aload_3
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	Tethering
    //   16	103	1	i	int
    //   24	117	2	j	int
    //   165	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	25	165	finally
    //   29	37	165	finally
    //   41	100	165	finally
    //   103	162	165	finally
  }
  
  private void resetAlarmTriggerAndDeviceMaps()
  {
    synchronized (mSoftApDeviceMap)
    {
      mSoftApDeviceMap.clear();
      this.mConnectedDeviceMap.clear();
      this.mL2ConnectedDeviceMap.clear();
      resetAlarmTrigger();
      return;
    }
  }
  
  private void runSilentTetherProvisioningAndEnable(int paramInt, ResultReceiver paramResultReceiver)
  {
    sendSilentTetherProvisionIntent(paramInt, getProxyReceiver(paramInt, paramResultReceiver));
  }
  
  private void runUiTetherProvisioningAndEnable(int paramInt, ResultReceiver paramResultReceiver)
  {
    sendUiTetherProvisionIntent(paramInt, getProxyReceiver(paramInt, paramResultReceiver));
  }
  
  private void scheduleProvisioningRechecks(int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("extraAddTetherType", paramInt);
    localIntent.putExtra("extraSetAlarm", true);
    localIntent.setComponent(TETHER_SERVICE);
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.startServiceAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void sendSilentTetherProvisionIntent(int paramInt, ResultReceiver paramResultReceiver)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("extraAddTetherType", paramInt);
    localIntent.putExtra("extraRunProvision", true);
    localIntent.putExtra("extraProvisionCallback", paramResultReceiver);
    localIntent.setComponent(TETHER_SERVICE);
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.startServiceAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void sendTetherConnectStateChangedBroadcast()
  {
    if (!getConnectivityManager().isTetheringSupported()) {
      return;
    }
    Intent localIntent = new Intent("codeaurora.net.conn.TETHER_CONNECT_STATE_CHANGED");
    localIntent.addFlags(603979776);
    this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
    if ((this.mUsbTethered) || (this.mBluetoothTethered))
    {
      showTetheredNotification(17303326, true);
      return;
    }
    showTetheredNotification(17303328, this.mContext.getResources().getBoolean(17957071));
  }
  
  private void sendTetherResult(ResultReceiver paramResultReceiver, int paramInt)
  {
    if (paramResultReceiver != null) {
      paramResultReceiver.send(paramInt, null);
    }
  }
  
  private void sendTetherStateChangedBroadcast()
  {
    if (!getConnectivityManager().isTetheringSupported()) {
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    this.mWifiTethered = false;
    this.mUsbTethered = false;
    this.mBluetoothTethered = false;
    Object localObject = this.mPublicSync;
    int i = 0;
    for (;;)
    {
      TetherState localTetherState;
      String str;
      try
      {
        if (i >= this.mTetherStates.size()) {
          break label210;
        }
        localTetherState = (TetherState)this.mTetherStates.valueAt(i);
        str = (String)this.mTetherStates.keyAt(i);
        if (localTetherState.mLastError != 0) {
          localArrayList4.add(str);
        } else if (localTetherState.mLastState == 1) {
          localArrayList1.add(str);
        }
      }
      finally {}
      if (localTetherState.mLastState == 2)
      {
        if (isUsb(str)) {
          this.mUsbTethered = true;
        }
        for (;;)
        {
          localArrayList3.add(str);
          break;
          if (isWifi(str)) {
            this.mWifiTethered = true;
          } else if (isBluetooth(str)) {
            this.mBluetoothTethered = true;
          }
        }
        label210:
        localObject = new Intent("android.net.conn.TETHER_STATE_CHANGED");
        ((Intent)localObject).addFlags(603979776);
        ((Intent)localObject).putStringArrayListExtra("availableArray", localArrayList2);
        ((Intent)localObject).putStringArrayListExtra("activeArray", localArrayList3);
        ((Intent)localObject).putStringArrayListExtra("erroredArray", localArrayList4);
        this.mContext.sendStickyBroadcastAsUser((Intent)localObject, UserHandle.ALL);
        Log.d("Tethering", String.format("sendTetherStateChangedBroadcast avail=[%s] active=[%s] error=[%s]", new Object[] { TextUtils.join(",", localArrayList2), TextUtils.join(",", localArrayList3), TextUtils.join(",", localArrayList4) }));
        if (this.mUsbTethered)
        {
          if ((this.mWifiTethered) || (this.mBluetoothTethered))
          {
            showTetheredNotification(17303326, true);
            return;
          }
          showTetheredNotification(17303327, true);
          return;
        }
        if (this.mWifiTethered)
        {
          if (this.mBluetoothTethered)
          {
            showTetheredNotification(17303326, true);
            return;
          }
          clearTetheredNotification();
          showTetheredNotification(17303328, this.mContext.getResources().getBoolean(17957071));
          return;
        }
        if (this.mBluetoothTethered)
        {
          showTetheredNotification(17303325, true);
          return;
        }
        clearTetheredNotification();
        return;
      }
      i += 1;
    }
  }
  
  private void sendUiTetherProvisionIntent(int paramInt, ResultReceiver paramResultReceiver)
  {
    Intent localIntent = new Intent("android.settings.TETHER_PROVISIONING_UI");
    localIntent.putExtra("extraAddTetherType", paramInt);
    localIntent.putExtra("extraProvisionCallback", paramResultReceiver);
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mContext.startActivityAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void setBluetoothTethering(final boolean paramBoolean, final ResultReceiver paramResultReceiver)
  {
    final BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (localBluetoothAdapter.isEnabled()))
    {
      localBluetoothAdapter.getProfileProxy(this.mContext, new BluetoothProfile.ServiceListener()
      {
        public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
        {
          ((BluetoothPan)paramAnonymousBluetoothProfile).setBluetoothTethering(paramBoolean);
          if (((BluetoothPan)paramAnonymousBluetoothProfile).isTetheringOn() == paramBoolean) {}
          for (paramAnonymousInt = 0;; paramAnonymousInt = 5)
          {
            Tethering.-wrap10(Tethering.this, paramResultReceiver, paramAnonymousInt);
            if ((paramBoolean) && (Tethering.-wrap1(Tethering.this))) {
              Tethering.-wrap8(Tethering.this, 2);
            }
            localBluetoothAdapter.closeProfileProxy(5, paramAnonymousBluetoothProfile);
            return;
          }
        }
        
        public void onServiceDisconnected(int paramAnonymousInt) {}
      }, 5);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder().append("Tried to enable bluetooth tethering with null or disabled adapter. null: ");
    if (localBluetoothAdapter == null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Log.w("Tethering", paramBoolean);
      sendTetherResult(paramResultReceiver, 2);
      return;
    }
  }
  
  private int setWifiTethering(boolean paramBoolean)
  {
    synchronized (this.mPublicSync)
    {
      this.mWifiTetherRequested = paramBoolean;
      paramBoolean = ((WifiManager)this.mContext.getSystemService("wifi")).setWifiApEnabled(null, paramBoolean);
      if (paramBoolean) {
        return 0;
      }
      return 5;
    }
  }
  
  private void showTetheredNotification(int paramInt, boolean paramBoolean)
  {
    NotificationManager localNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
    if (localNotificationManager == null) {
      return;
    }
    Object localObject1;
    Object localObject4;
    CharSequence localCharSequence;
    int i;
    if (this.mLastNotificationId != 0)
    {
      if ((this.mLastNotificationId != paramInt) || ((this.mContext.getResources().getBoolean(17956873)) && (paramInt == 17303328)))
      {
        localNotificationManager.cancelAsUser(null, this.mLastNotificationId, UserHandle.ALL);
        this.mLastNotificationId = 0;
      }
    }
    else
    {
      localObject1 = new Intent();
      ((Intent)localObject1).setClassName("com.android.settings", "com.android.settings.TetherSettings");
      ((Intent)localObject1).setFlags(1073741824);
      localObject4 = PendingIntent.getActivityAsUser(this.mContext, 0, (Intent)localObject1, 0, null, UserHandle.CURRENT);
      localObject1 = Resources.getSystem();
      localCharSequence = ((Resources)localObject1).getText(17040537);
      i = this.mConnectedDeviceMap.size();
      if ((!this.mContext.getResources().getBoolean(17956873)) || (paramInt != 17303328)) {
        break label433;
      }
      if (i != 0) {
        break label364;
      }
      localObject1 = ((Resources)localObject1).getText(17040539);
    }
    for (;;)
    {
      Notification localNotification = null;
      synchronized (this.mPublicSync)
      {
        if (this.mTetheredNotificationBuilder == null)
        {
          this.mTetheredNotificationBuilder = new Notification.Builder(this.mContext);
          this.mTetheredNotificationBuilder.setWhen(0L).setOngoing(true).setColor(this.mContext.getColor(17170523)).setVisibility(1).setCategory("status");
        }
        this.mTetheredNotificationBuilder.setSmallIcon(paramInt).setContentTitle(localCharSequence).setContentText((CharSequence)localObject1).setContentIntent((PendingIntent)localObject4).setGroup("com.android.server.connectivity.tethering").setPriority(0);
        localObject1 = localNotification;
        try
        {
          localNotification = this.mTetheredNotificationBuilder.build();
          localObject1 = localNotification;
          localObject4 = localNotification.extras;
          if (!paramBoolean) {
            break label446;
          }
          paramBoolean = false;
          localObject1 = localNotification;
          ((Bundle)localObject4).putBoolean("hide_icon", paramBoolean);
          localObject1 = localNotification;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("Tethering", "TetheredNotificationBuilder build  e:" + localException);
          }
        }
        this.mLastNotificationId = paramInt;
        if (localObject1 != null) {
          localNotificationManager.notifyAsUser(null, this.mLastNotificationId, (Notification)localObject1, UserHandle.ALL);
        }
        return;
        return;
        label364:
        if (i == 1)
        {
          localObject1 = String.format(((Resources)localObject1).getText(17040540).toString(), new Object[] { Integer.valueOf(i) });
          continue;
        }
        localObject1 = String.format(((Resources)localObject1).getText(17040541).toString(), new Object[] { Integer.valueOf(i) });
        continue;
        label433:
        localObject1 = ((Resources)localObject1).getText(17040538);
        continue;
        label446:
        paramBoolean = true;
      }
    }
  }
  
  private void tetherMatchingInterfaces(boolean paramBoolean, int paramInt)
  {
    int i = 0;
    Log.d("Tethering", "tetherMatchingInterfaces(" + paramBoolean + ", " + paramInt + ")");
    WifiManager localWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
    try
    {
      String[] arrayOfString = this.mNMService.listInterfaces();
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (arrayOfString != null)
      {
        int j = arrayOfString.length;
        for (;;)
        {
          localObject1 = localObject2;
          if (i >= j) {
            break;
          }
          localObject1 = arrayOfString[i];
          if (ifaceNameToType((String)localObject1) == paramInt)
          {
            if ((paramInt != 0) || (!localWifiManager.getWifiStaSapConcurrency()) || (((String)localObject1).matches("softap0"))) {
              break;
            }
            Log.d("Tethering", "For STA + SoftAp concurrency skip tethering on " + (String)localObject1);
          }
          i += 1;
        }
      }
      if (localException != null) {
        break label216;
      }
    }
    catch (Exception localException)
    {
      Log.e("Tethering", "Error listing Interfaces", localException);
      return;
    }
    Log.e("Tethering", "could not find iface of type " + paramInt);
    return;
    label216:
    if (paramBoolean) {}
    for (paramInt = tether(localException); paramInt != 0; paramInt = untether(localException))
    {
      Log.e("Tethering", "unable start or stop tethering on iface " + localException);
      return;
    }
  }
  
  private void trackNewTetherableInterface(String paramString, int paramInt)
  {
    TetherState localTetherState = new TetherState(new TetherInterfaceStateMachine(paramString, this.mLooper, paramInt, this.mNMService, this.mStatsService, this, isIpv6TetheringEnabled()));
    this.mTetherStates.put(paramString, localTetherState);
    localTetherState.mStateMachine.start();
  }
  
  public boolean blackListWifiDevice(String paramString, boolean paramBoolean)
  {
    ??? = ((WifiManager)this.mContext.getSystemService("wifi")).getSoftApInterfaceName();
    Object localObject1 = ???;
    if (??? == null) {
      localObject1 = "wlan0";
    }
    synchronized (mSoftApDeviceMap)
    {
      try
      {
        this.mNMService.blackListWifiDevice(paramString, paramBoolean, (String)localObject1);
        bool = true;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("Tethering", "Error black wifi device :" + localException);
          boolean bool = false;
          continue;
          label143:
          if ((localException != null) && (localException.deviceState == 2)) {
            mSoftApDeviceMap.remove(paramString);
          }
        }
      }
      if ((bool) && (mSoftApDeviceMap.containsKey(paramString)))
      {
        localObject1 = (WifiDevice)mSoftApDeviceMap.get(paramString);
        if (!paramBoolean) {
          break label143;
        }
        if ((localObject1 != null) && (((WifiDevice)localObject1).deviceState != 2)) {
          ((WifiDevice)localObject1).deviceState = 2;
        }
      }
      return bool;
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump ConnectivityService.Tether from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    paramPrintWriter.println("Tethering:");
    paramPrintWriter.increaseIndent();
    paramPrintWriter.print("mUpstreamIfaceTypes:");
    Object localObject;
    synchronized (this.mPublicSync)
    {
      paramArrayOfString = this.mUpstreamIfaceTypes.iterator();
      if (paramArrayOfString.hasNext())
      {
        localObject = (Integer)paramArrayOfString.next();
        paramPrintWriter.print(" " + ConnectivityManager.getNetworkTypeName(((Integer)localObject).intValue()));
      }
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Tether state:");
    paramPrintWriter.increaseIndent();
    int i = 0;
    if (i < this.mTetherStates.size())
    {
      paramArrayOfString = (String)this.mTetherStates.keyAt(i);
      localObject = (TetherState)this.mTetherStates.valueAt(i);
      paramPrintWriter.print(paramArrayOfString + " - ");
      switch (((TetherState)localObject).mLastState)
      {
      }
    }
    for (;;)
    {
      paramPrintWriter.print("UnknownState");
      for (;;)
      {
        paramPrintWriter.println(" - lastError = " + ((TetherState)localObject).mLastError);
        i += 1;
        break;
        paramPrintWriter.print("UnavailableState");
        continue;
        paramPrintWriter.print("AvailableState");
        continue;
        paramPrintWriter.print("TetheredState");
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.decreaseIndent();
      return;
    }
  }
  
  public String[] getErroredIfaces()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.mPublicSync;
    int i = 0;
    try
    {
      while (i < this.mTetherStates.size())
      {
        if (((TetherState)this.mTetherStates.valueAt(i)).mLastError != 0) {
          localArrayList.add((String)this.mTetherStates.keyAt(i));
        }
        i += 1;
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public int getLastTetherError(String paramString)
  {
    synchronized (this.mPublicSync)
    {
      TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
      if (localTetherState == null)
      {
        Log.e("Tethering", "Tried to getLastTetherError on an unknown iface :" + paramString + ", ignoring");
        return 1;
      }
      int i = localTetherState.mLastError;
      return i;
    }
  }
  
  public List<WifiDevice> getTetherConnectedSta()
  {
    ArrayList localArrayList = new ArrayList();
    if (this.mContext.getResources().getBoolean(17956873))
    {
      Iterator localIterator = this.mConnectedDeviceMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        WifiDevice localWifiDevice = (WifiDevice)this.mConnectedDeviceMap.get(str);
        Log.d("Tethering", "getTetherConnectedSta: addr=" + str + " name=" + localWifiDevice.deviceName);
        localArrayList.add(localWifiDevice);
      }
    }
    return localArrayList;
  }
  
  public List<WifiDevice> getTetherSoftApSta(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      WifiDevice localWifiDevice;
      synchronized (mSoftApDeviceMap)
      {
        Iterator localIterator = mSoftApDeviceMap.keySet().iterator();
        if (!localIterator.hasNext()) {
          break label199;
        }
        String str = (String)localIterator.next();
        localWifiDevice = (WifiDevice)mSoftApDeviceMap.get(str);
        Log.d("Tethering", "getTetherSoftApSta: addr=" + str + " name=" + localWifiDevice.deviceName + " state = " + localWifiDevice.deviceState);
        switch (paramInt)
        {
        case 0: 
          localArrayList.add(localWifiDevice);
        }
      }
      if (localWifiDevice.deviceState == 2)
      {
        localList.add(localWifiDevice);
        continue;
        if (localWifiDevice.deviceState != 2)
        {
          localList.add(localWifiDevice);
          continue;
          label199:
          return localList;
        }
      }
    }
  }
  
  public String[] getTetherableBluetoothRegexs()
  {
    return this.mTetherableBluetoothRegexs;
  }
  
  public String[] getTetherableIfaces()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.mPublicSync;
    int i = 0;
    try
    {
      while (i < this.mTetherStates.size())
      {
        if (((TetherState)this.mTetherStates.valueAt(i)).mLastState == 1) {
          localArrayList.add((String)this.mTetherStates.keyAt(i));
        }
        i += 1;
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public String[] getTetherableUsbRegexs()
  {
    return this.mTetherableUsbRegexs;
  }
  
  public String[] getTetherableWifiRegexs()
  {
    return this.mTetherableWifiRegexs;
  }
  
  public String[] getTetheredDhcpRanges()
  {
    return this.mDhcpRange;
  }
  
  public String[] getTetheredIfaces()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.mPublicSync;
    int i = 0;
    try
    {
      while (i < this.mTetherStates.size())
      {
        if (((TetherState)this.mTetherStates.valueAt(i)).mLastState == 2) {
          localArrayList.add((String)this.mTetherStates.keyAt(i));
        }
        i += 1;
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public int[] getUpstreamIfaceTypes()
  {
    synchronized (this.mPublicSync)
    {
      updateConfiguration();
      int[] arrayOfInt = new int[this.mUpstreamIfaceTypes.size()];
      Iterator localIterator = this.mUpstreamIfaceTypes.iterator();
      int i = 0;
      while (i < this.mUpstreamIfaceTypes.size())
      {
        arrayOfInt[i] = ((Integer)localIterator.next()).intValue();
        i += 1;
      }
      return arrayOfInt;
    }
  }
  
  public void interfaceAdded(String paramString)
  {
    Log.d("Tethering", "interfaceAdded " + paramString);
    synchronized (this.mPublicSync)
    {
      int i = ifaceNameToType(paramString);
      if (i == -1)
      {
        Log.d("Tethering", paramString + " is not a tetherable iface, ignoring");
        return;
      }
      if ((TetherState)this.mTetherStates.get(paramString) == null)
      {
        trackNewTetherableInterface(paramString, i);
        return;
      }
      Log.d("Tethering", "active iface (" + paramString + ") reported as added, ignoring");
    }
  }
  
  public void interfaceLinkStateChanged(String paramString, boolean paramBoolean)
  {
    interfaceStatusChanged(paramString, paramBoolean);
  }
  
  public void interfaceMessageRecevied(String paramString)
  {
    if (!this.mContext.getResources().getBoolean(17956873)) {
      return;
    }
    Log.d("Tethering", "interfaceMessageRecevied: message=" + paramString);
    try
    {
      paramString = new WifiDevice(paramString);
      if (paramString.deviceState != 1) {
        break label245;
      }
      paramString.connectedTime = System.currentTimeMillis();
      this.mL2ConnectedDeviceMap.put(paramString.deviceAddress, paramString);
      if (this.mL2ConnectedDeviceMap.size() == 1) {
        resetAlarmTrigger();
      }
      if (readDeviceInfoFromDnsmasq(paramString)) {
        synchronized (mSoftApDeviceMap)
        {
          if (mSoftApDeviceMap.containsKey(paramString.deviceAddress)) {
            mSoftApDeviceMap.remove(paramString.deviceAddress);
          }
          mSoftApDeviceMap.put(paramString.deviceAddress, paramString);
          this.mConnectedDeviceMap.put(paramString.deviceAddress, paramString);
          sendTetherConnectStateChangedBroadcast();
          return;
        }
      }
      Log.d("Tethering", "Starting poll device info for " + paramString.deviceAddress);
    }
    catch (IllegalArgumentException paramString)
    {
      Log.e("Tethering", "WifiDevice IllegalArgument: " + paramString);
      return;
    }
    new DnsmasqThread(this, paramString, 1000, 10).start();
    return;
    label245:
    if (paramString.deviceState == 0)
    {
      this.mL2ConnectedDeviceMap.remove(paramString.deviceAddress);
      this.mConnectedDeviceMap.remove(paramString.deviceAddress);
      synchronized (mSoftApDeviceMap)
      {
        if (mSoftApDeviceMap.containsKey(paramString.deviceAddress))
        {
          WifiDevice localWifiDevice = (WifiDevice)mSoftApDeviceMap.get(paramString.deviceAddress);
          if ((localWifiDevice != null) && (localWifiDevice.deviceState != 2)) {
            mSoftApDeviceMap.remove(paramString.deviceAddress);
          }
        }
        resetAlarmTrigger();
        sendTetherConnectStateChangedBroadcast();
        return;
      }
    }
  }
  
  public void interfaceRemoved(String paramString)
  {
    Log.d("Tethering", "interfaceRemoved " + paramString);
    synchronized (this.mPublicSync)
    {
      TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
      if (localTetherState == null)
      {
        Log.e("Tethering", "attempting to remove unknown iface (" + paramString + "), ignoring");
        return;
      }
      localTetherState.mStateMachine.sendMessage(327784);
      this.mTetherStates.remove(paramString);
      return;
    }
  }
  
  public void interfaceStatusChanged(String paramString, boolean paramBoolean)
  {
    Log.d("Tethering", "interfaceStatusChanged " + paramString + ", " + paramBoolean);
    for (;;)
    {
      synchronized (this.mPublicSync)
      {
        int i = ifaceNameToType(paramString);
        if (i == -1) {
          return;
        }
        TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
        if (paramBoolean)
        {
          if (localTetherState == null) {
            trackNewTetherableInterface(paramString, i);
          }
          return;
        }
        if (i == 2)
        {
          localTetherState.mStateMachine.sendMessage(327784);
          this.mTetherStates.remove(paramString);
        }
      }
      Log.d("Tethering", "ignore interface down for " + paramString);
    }
  }
  
  public void notifyInterfaceStateChange(String paramString, TetherInterfaceStateMachine paramTetherInterfaceStateMachine, int paramInt1, int paramInt2)
  {
    for (;;)
    {
      synchronized (this.mPublicSync)
      {
        TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
        if ((localTetherState != null) && (localTetherState.mStateMachine.equals(paramTetherInterfaceStateMachine)))
        {
          localTetherState.mLastState = paramInt1;
          localTetherState.mLastError = paramInt2;
          Log.d("Tethering", "iface " + paramString + " notified that it was in state " + paramInt1 + " with error " + paramInt2);
        }
        try
        {
          ??? = this.mPolicyManager;
          if (paramInt1 != 2) {
            break label212;
          }
          bool = true;
          ((INetworkPolicyManager)???).onTetheringChanged(paramString, bool);
        }
        catch (RemoteException paramString)
        {
          boolean bool;
          continue;
        }
        if (paramInt2 == 5) {
          this.mTetherMasterSM.sendMessage(327686, paramTetherInterfaceStateMachine);
        }
        switch (paramInt1)
        {
        default: 
          sendTetherStateChangedBroadcast();
          return;
          Log.d("Tethering", "got notification from stale iface " + paramString);
        }
      }
      label212:
      bool = false;
      continue;
      this.mTetherMasterSM.sendMessage(327682, paramTetherInterfaceStateMachine);
      continue;
      this.mTetherMasterSM.sendMessage(327681, paramTetherInterfaceStateMachine);
    }
  }
  
  boolean pertainsToCurrentUpstream(NetworkState paramNetworkState)
  {
    if ((paramNetworkState != null) && (paramNetworkState.linkProperties != null) && (this.mCurrentUpstreamIface != null))
    {
      paramNetworkState = paramNetworkState.linkProperties.getAllInterfaceNames().iterator();
      while (paramNetworkState.hasNext())
      {
        String str = (String)paramNetworkState.next();
        if (this.mCurrentUpstreamIface.equals(str)) {
          return true;
        }
      }
    }
    return false;
  }
  
  /* Error */
  public int setUsbTethering(boolean paramBoolean)
  {
    // Byte code:
    //   0: ldc 96
    //   2: new 719	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 720	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 1364
    //   12: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: iload_1
    //   16: invokevirtual 954	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   19: ldc_w 1079
    //   22: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: invokevirtual 742	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: invokestatic 745	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: aload_0
    //   33: getfield 205	com/android/server/connectivity/Tethering:mContext	Landroid/content/Context;
    //   36: ldc_w 1366
    //   39: invokevirtual 471	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   42: checkcast 1368	android/hardware/usb/UsbManager
    //   45: astore 5
    //   47: aload_0
    //   48: getfield 231	com/android/server/connectivity/Tethering:mPublicSync	Ljava/lang/Object;
    //   51: astore 4
    //   53: aload 4
    //   55: monitorenter
    //   56: iload_1
    //   57: ifeq +62 -> 119
    //   60: aload_0
    //   61: getfield 178	com/android/server/connectivity/Tethering:mRndisEnabled	Z
    //   64: ifeq +39 -> 103
    //   67: invokestatic 549	android/os/Binder:clearCallingIdentity	()J
    //   70: lstore_2
    //   71: aload_0
    //   72: iconst_1
    //   73: iconst_1
    //   74: invokespecial 266	com/android/server/connectivity/Tethering:tetherMatchingInterfaces	(ZI)V
    //   77: lload_2
    //   78: invokestatic 563	android/os/Binder:restoreCallingIdentity	(J)V
    //   81: aload 4
    //   83: monitorexit
    //   84: iconst_0
    //   85: ireturn
    //   86: astore 5
    //   88: lload_2
    //   89: invokestatic 563	android/os/Binder:restoreCallingIdentity	(J)V
    //   92: aload 5
    //   94: athrow
    //   95: astore 5
    //   97: aload 4
    //   99: monitorexit
    //   100: aload 5
    //   102: athrow
    //   103: aload_0
    //   104: iconst_1
    //   105: putfield 201	com/android/server/connectivity/Tethering:mUsbTetherRequested	Z
    //   108: aload 5
    //   110: ldc_w 1370
    //   113: invokevirtual 1373	android/hardware/usb/UsbManager:setCurrentFunction	(Ljava/lang/String;)V
    //   116: goto -35 -> 81
    //   119: invokestatic 549	android/os/Binder:clearCallingIdentity	()J
    //   122: lstore_2
    //   123: aload_0
    //   124: iconst_0
    //   125: iconst_1
    //   126: invokespecial 266	com/android/server/connectivity/Tethering:tetherMatchingInterfaces	(ZI)V
    //   129: lload_2
    //   130: invokestatic 563	android/os/Binder:restoreCallingIdentity	(J)V
    //   133: aload_0
    //   134: getfield 178	com/android/server/connectivity/Tethering:mRndisEnabled	Z
    //   137: ifeq +9 -> 146
    //   140: aload 5
    //   142: aconst_null
    //   143: invokevirtual 1373	android/hardware/usb/UsbManager:setCurrentFunction	(Ljava/lang/String;)V
    //   146: aload_0
    //   147: iconst_0
    //   148: putfield 201	com/android/server/connectivity/Tethering:mUsbTetherRequested	Z
    //   151: goto -70 -> 81
    //   154: astore 5
    //   156: lload_2
    //   157: invokestatic 563	android/os/Binder:restoreCallingIdentity	(J)V
    //   160: aload 5
    //   162: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	Tethering
    //   0	163	1	paramBoolean	boolean
    //   70	87	2	l	long
    //   51	47	4	localObject1	Object
    //   45	1	5	localUsbManager	android.hardware.usb.UsbManager
    //   86	7	5	localObject2	Object
    //   95	46	5	localObject3	Object
    //   154	7	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   71	77	86	finally
    //   60	71	95	finally
    //   77	81	95	finally
    //   88	95	95	finally
    //   103	116	95	finally
    //   119	123	95	finally
    //   129	146	95	finally
    //   146	151	95	finally
    //   156	163	95	finally
    //   123	129	154	finally
  }
  
  public void startTethering(int paramInt, ResultReceiver paramResultReceiver, boolean paramBoolean)
  {
    WifiManager localWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
    if (localWifiManager != null)
    {
      Log.d("Tethering", "get ready to enable tethering when concurrency enabled");
      this.mRestoreWifiBand = localWifiManager.getFrequencyBand();
      localWifiManager.requestRunningSoftap();
    }
    if (!isTetherProvisioningRequired())
    {
      enableTetheringInternal(paramInt, true, paramResultReceiver);
      return;
    }
    if (paramBoolean)
    {
      runUiTetherProvisioningAndEnable(paramInt, paramResultReceiver);
      return;
    }
    runSilentTetherProvisioningAndEnable(paramInt, paramResultReceiver);
  }
  
  public void stopTethering(int paramInt)
  {
    enableTetheringInternal(paramInt, false, null);
    if (isTetherProvisioningRequired()) {
      cancelTetherProvisioningRechecks(paramInt);
    }
    WifiManager localWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
    if ((localWifiManager != null) && (localWifiManager.getWifiStaSapConcurrency())) {
      localWifiManager.setFrequencyBand(this.mRestoreWifiBand, true);
    }
  }
  
  public int tether(String paramString)
  {
    Log.d("Tethering", "Tethering " + paramString);
    synchronized (this.mPublicSync)
    {
      TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
      if (localTetherState == null)
      {
        Log.e("Tethering", "Tried to Tether an unknown iface: " + paramString + ", ignoring");
        return 1;
      }
      if (localTetherState.mLastState != 1)
      {
        Log.e("Tethering", "Tried to Tether an unavailable iface: " + paramString + ", ignoring");
        return 4;
      }
      localTetherState.mStateMachine.sendMessage(327782);
      return 0;
    }
  }
  
  public int untether(String paramString)
  {
    Log.d("Tethering", "Untethering " + paramString);
    synchronized (this.mPublicSync)
    {
      TetherState localTetherState = (TetherState)this.mTetherStates.get(paramString);
      if (localTetherState == null)
      {
        Log.e("Tethering", "Tried to Untether an unknown iface :" + paramString + ", ignoring");
        return 1;
      }
      if (localTetherState.mLastState != 2)
      {
        Log.e("Tethering", "Tried to untether an untethered iface :" + paramString + ", ignoring");
        return 4;
      }
      localTetherState.mStateMachine.sendMessage(327783);
      return 0;
    }
  }
  
  public void untetherAll()
  {
    stopTethering(0);
    stopTethering(1);
    stopTethering(2);
  }
  
  void updateConfiguration()
  {
    int i = 0;
    String[] arrayOfString2 = this.mContext.getResources().getStringArray(17235989);
    String[] arrayOfString3 = this.mContext.getResources().getStringArray(17235992);
    String[] arrayOfString1;
    if (SystemProperties.getInt("persist.fst.softap.en", 0) == 1)
    {
      arrayOfString1 = new String[1];
      arrayOfString1[0] = "bond0";
    }
    ArrayList localArrayList;
    for (;;)
    {
      ??? = this.mContext.getResources().getIntArray(17235995);
      localArrayList = new ArrayList();
      int j = ???.length;
      while (i < j)
      {
        localArrayList.add(new Integer(???[i]));
        i += 1;
      }
      arrayOfString1 = this.mContext.getResources().getStringArray(17235990);
    }
    synchronized (this.mPublicSync)
    {
      this.mTetherableUsbRegexs = arrayOfString2;
      this.mTetherableWifiRegexs = arrayOfString1;
      this.mTetherableBluetoothRegexs = arrayOfString3;
      this.mUpstreamIfaceTypes = localArrayList;
      checkDunRequired();
      return;
    }
  }
  
  private static class DnsmasqThread
    extends Thread
  {
    private WifiDevice mDevice;
    private int mInterval;
    private int mMaxTimes;
    private final Tethering mTethering;
    
    public DnsmasqThread(Tethering paramTethering, WifiDevice paramWifiDevice, int paramInt1, int paramInt2)
    {
      super();
      this.mTethering = paramTethering;
      this.mInterval = paramInt1;
      this.mMaxTimes = paramInt2;
      this.mDevice = paramWifiDevice;
    }
    
    public void run()
    {
      boolean bool = false;
      try
      {
        if (this.mMaxTimes > 0)
        {
          bool = Tethering.-wrap2(this.mTethering, this.mDevice);
          if (bool) {
            Log.d("Tethering", "Successfully poll device info for " + this.mDevice.deviceAddress);
          }
        }
        else
        {
          if (!bool) {
            Log.d("Tethering", "Pulling timeout, suppose STA uses static ip " + this.mDevice.deviceAddress);
          }
          ??? = (WifiDevice)Tethering.-get6(this.mTethering).get(this.mDevice.deviceAddress);
          if ((??? == null) || (((WifiDevice)???).deviceState != 1)) {
            break label282;
          }
          Tethering.-get1(this.mTethering).put(this.mDevice.deviceAddress, this.mDevice);
        }
      }
      catch (Exception localException)
      {
        synchronized (Tethering.-get11())
        {
          for (;;)
          {
            if (Tethering.-get11().containsKey(this.mDevice.deviceAddress)) {
              Tethering.-get11().remove(this.mDevice.deviceAddress);
            }
            Tethering.-get11().put(this.mDevice.deviceAddress, this.mDevice);
            Tethering.-wrap9(this.mTethering);
            return;
            this.mMaxTimes -= 1;
            Thread.sleep(this.mInterval);
          }
          localException = localException;
          bool = false;
          Log.e("Tethering", "Pulling " + this.mDevice.deviceAddress + "error" + localException);
        }
      }
      label282:
      Log.d("Tethering", "Device " + this.mDevice.deviceAddress + "already disconnected, ignoring");
    }
  }
  
  private class SoftApAutoShutDownObserver
    extends ContentObserver
  {
    public SoftApAutoShutDownObserver(Handler paramHandler)
    {
      super();
      Tethering.-get2(Tethering.this).getContentResolver().registerContentObserver(Settings.Secure.getUriFor("hotspot_auto_shut_down"), false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      Tethering.-wrap7(Tethering.this);
    }
  }
  
  private class StateReceiver
    extends BroadcastReceiver
  {
    private StateReceiver() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      ??? = paramIntent.getAction();
      if (??? == null) {
        return;
      }
      if (???.equals("android.hardware.usb.action.USB_STATE")) {}
      label98:
      do
      {
        synchronized (Tethering.-get9(Tethering.this))
        {
          boolean bool = paramIntent.getBooleanExtra("connected", false);
          Tethering.-set1(Tethering.this, paramIntent.getBooleanExtra("rndis", false));
          if ((bool) && (Tethering.-get10(Tethering.this)) && (Tethering.-get16(Tethering.this))) {
            Tethering.-wrap11(Tethering.this, true, 1);
          }
          Tethering.-set2(Tethering.this, false);
          return;
        }
        if (!???.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
          break;
        }
        ??? = (NetworkInfo)paramIntent.getParcelableExtra("networkInfo");
      } while ((??? == null) || (???.getDetailedState() == NetworkInfo.DetailedState.FAILED));
      Log.d("Tethering", "Tethering got CONNECTIVITY_ACTION");
      Tethering.-get12(Tethering.this).sendMessage(327683);
      return;
      if (???.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {}
      for (;;)
      {
        int i;
        synchronized (Tethering.-get9(Tethering.this))
        {
          i = paramIntent.getIntExtra("wifi_state", 11);
          Tethering.-set3(i);
          ??? = (Context)???;
          switch (i)
          {
          case 12: 
            Log.d("Tethering", "Canceling WiFi tethering request - AP_STATE=" + i);
            i = 0;
            if (i < Tethering.-get13(Tethering.this).size())
            {
              ??? = ((Tethering.TetherState)Tethering.-get13(Tethering.this).valueAt(i)).mStateMachine;
              if (???.interfaceType() != 0) {
                break label349;
              }
              ???.sendMessage(327783);
            }
            Tethering.-set4(Tethering.this, false);
            ??? = (Context)???;
          }
        }
        Tethering.-wrap6(Tethering.this);
        Tethering.-wrap11(Tethering.this, true, 0);
        ??? = (Context)???;
        break;
        Tethering.-wrap6(Tethering.this);
        continue;
        label349:
        i += 1;
        continue;
        if (!???.equals("android.intent.action.CONFIGURATION_CHANGED")) {
          break label98;
        }
        Tethering.this.updateConfiguration();
        return;
      }
    }
  }
  
  class TetherMasterSM
    extends StateMachine
  {
    private static final int BASE_MASTER = 327680;
    static final int CMD_CLEAR_ERROR = 327686;
    static final int CMD_RETRY_UPSTREAM = 327684;
    static final int CMD_TETHER_MODE_REQUESTED = 327681;
    static final int CMD_TETHER_MODE_UNREQUESTED = 327682;
    static final int CMD_UPSTREAM_CHANGED = 327683;
    static final int EVENT_UPSTREAM_CALLBACK = 327685;
    private static final int UPSTREAM_SETTLE_TIME_MS = 10000;
    private SimChangeBroadcastReceiver mBroadcastReceiver = null;
    private final IPv6TetheringCoordinator mIPv6TetheringCoordinator;
    private State mInitialState = new InitialState();
    private int mMobileApnReserved = -1;
    private ConnectivityManager.NetworkCallback mMobileUpstreamCallback;
    private final ArrayList<TetherInterfaceStateMachine> mNotifyList;
    private State mSetDnsForwardersErrorState;
    private State mSetIpForwardingDisabledErrorState;
    private State mSetIpForwardingEnabledErrorState;
    private final AtomicInteger mSimBcastGenerationNumber = new AtomicInteger(0);
    private State mStartTetheringErrorState;
    private State mStopTetheringErrorState;
    private State mTetherModeAliveState;
    
    TetherMasterSM(String paramString, Looper paramLooper)
    {
      super(paramLooper);
      addState(this.mInitialState);
      this.mTetherModeAliveState = new TetherModeAliveState();
      addState(this.mTetherModeAliveState);
      this.mSetIpForwardingEnabledErrorState = new SetIpForwardingEnabledErrorState();
      addState(this.mSetIpForwardingEnabledErrorState);
      this.mSetIpForwardingDisabledErrorState = new SetIpForwardingDisabledErrorState();
      addState(this.mSetIpForwardingDisabledErrorState);
      this.mStartTetheringErrorState = new StartTetheringErrorState();
      addState(this.mStartTetheringErrorState);
      this.mStopTetheringErrorState = new StopTetheringErrorState();
      addState(this.mStopTetheringErrorState);
      this.mSetDnsForwardersErrorState = new SetDnsForwardersErrorState();
      addState(this.mSetDnsForwardersErrorState);
      this.mNotifyList = new ArrayList();
      this.mIPv6TetheringCoordinator = new IPv6TetheringCoordinator(this.mNotifyList);
      setInitialState(this.mInitialState);
    }
    
    private void startListeningForSimChanges()
    {
      Log.d("Tethering", "startListeningForSimChanges");
      if (this.mBroadcastReceiver == null)
      {
        this.mBroadcastReceiver = new SimChangeBroadcastReceiver(this.mSimBcastGenerationNumber.incrementAndGet());
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        Tethering.-get2(Tethering.this).registerReceiver(this.mBroadcastReceiver, localIntentFilter);
      }
    }
    
    private void stopListeningForSimChanges()
    {
      Log.d("Tethering", "stopListeningForSimChanges");
      if (this.mBroadcastReceiver != null)
      {
        this.mSimBcastGenerationNumber.incrementAndGet();
        Tethering.-get2(Tethering.this).unregisterReceiver(this.mBroadcastReceiver);
        this.mBroadcastReceiver = null;
      }
    }
    
    class ErrorState
      extends State
    {
      int mErrorNotification;
      
      ErrorState() {}
      
      void notify(int paramInt)
      {
        this.mErrorNotification = paramInt;
        Iterator localIterator = Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).iterator();
        while (localIterator.hasNext()) {
          ((TetherInterfaceStateMachine)localIterator.next()).sendMessage(paramInt);
        }
      }
      
      public boolean processMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return false;
        case 327681: 
          ((TetherInterfaceStateMachine)paramMessage.obj).sendMessage(this.mErrorNotification);
          return true;
        }
        this.mErrorNotification = 0;
        Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get1(Tethering.TetherMasterSM.this));
        return true;
      }
    }
    
    class InitialState
      extends Tethering.TetherMasterSM.TetherMasterUtilState
    {
      InitialState()
      {
        super();
      }
      
      public boolean processMessage(Message paramMessage)
      {
        Tethering.-wrap5(Tethering.this, this, paramMessage.what);
        switch (paramMessage.what)
        {
        default: 
          return false;
        case 327681: 
          paramMessage = (TetherInterfaceStateMachine)paramMessage.obj;
          Log.d("Tethering", "Tether Mode requested by " + paramMessage);
          if (Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).indexOf(paramMessage) < 0)
          {
            Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).add(paramMessage);
            Tethering.TetherMasterSM.-get0(Tethering.TetherMasterSM.this).addActiveDownstream(paramMessage);
          }
          Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get11(Tethering.TetherMasterSM.this));
          return true;
        }
        paramMessage = (TetherInterfaceStateMachine)paramMessage.obj;
        Log.d("Tethering", "Tether Mode unrequested by " + paramMessage);
        Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).remove(paramMessage);
        Tethering.TetherMasterSM.-get0(Tethering.TetherMasterSM.this).removeActiveDownstream(paramMessage);
        return true;
      }
    }
    
    class SetDnsForwardersErrorState
      extends Tethering.TetherMasterSM.ErrorState
    {
      SetDnsForwardersErrorState()
      {
        super();
      }
      
      public void enter()
      {
        Log.e("Tethering", "Error in setDnsForwarders");
        notify(327791);
        try
        {
          Tethering.-get7(Tethering.this).stopTethering();
          try
          {
            Tethering.-get7(Tethering.this).setIpForwardingEnabled(false);
            return;
          }
          catch (Exception localException1) {}
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
      }
    }
    
    class SetIpForwardingDisabledErrorState
      extends Tethering.TetherMasterSM.ErrorState
    {
      SetIpForwardingDisabledErrorState()
      {
        super();
      }
      
      public void enter()
      {
        Log.e("Tethering", "Error in setIpForwardingDisabled");
        notify(327788);
      }
    }
    
    class SetIpForwardingEnabledErrorState
      extends Tethering.TetherMasterSM.ErrorState
    {
      SetIpForwardingEnabledErrorState()
      {
        super();
      }
      
      public void enter()
      {
        Log.e("Tethering", "Error in setIpForwardingEnabled");
        notify(327787);
      }
    }
    
    class SimChangeBroadcastReceiver
      extends BroadcastReceiver
    {
      private final int mGenerationNumber;
      private boolean mSimNotLoadedSeen = false;
      
      public SimChangeBroadcastReceiver(int paramInt)
      {
        this.mGenerationNumber = paramInt;
      }
      
      /* Error */
      public void onReceive(Context paramContext, Intent paramIntent)
      {
        // Byte code:
        //   0: ldc 34
        //   2: new 36	java/lang/StringBuilder
        //   5: dup
        //   6: invokespecial 37	java/lang/StringBuilder:<init>	()V
        //   9: ldc 39
        //   11: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   14: aload_0
        //   15: getfield 27	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mGenerationNumber	I
        //   18: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   21: ldc 48
        //   23: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   26: aload_0
        //   27: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   30: invokestatic 52	com/android/server/connectivity/Tethering$TetherMasterSM:-get8	(Lcom/android/server/connectivity/Tethering$TetherMasterSM;)Ljava/util/concurrent/atomic/AtomicInteger;
        //   33: invokevirtual 58	java/util/concurrent/atomic/AtomicInteger:get	()I
        //   36: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   39: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   42: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   45: pop
        //   46: aload_0
        //   47: getfield 27	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mGenerationNumber	I
        //   50: aload_0
        //   51: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   54: invokestatic 52	com/android/server/connectivity/Tethering$TetherMasterSM:-get8	(Lcom/android/server/connectivity/Tethering$TetherMasterSM;)Ljava/util/concurrent/atomic/AtomicInteger;
        //   57: invokevirtual 58	java/util/concurrent/atomic/AtomicInteger:get	()I
        //   60: if_icmpeq +4 -> 64
        //   63: return
        //   64: aload_2
        //   65: ldc 70
        //   67: invokevirtual 76	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
        //   70: astore_1
        //   71: ldc 34
        //   73: new 36	java/lang/StringBuilder
        //   76: dup
        //   77: invokespecial 37	java/lang/StringBuilder:<init>	()V
        //   80: ldc 78
        //   82: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   85: aload_1
        //   86: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   89: ldc 80
        //   91: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   94: aload_0
        //   95: getfield 25	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mSimNotLoadedSeen	Z
        //   98: invokevirtual 83	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
        //   101: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   104: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   107: pop
        //   108: aload_0
        //   109: getfield 25	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mSimNotLoadedSeen	Z
        //   112: ifne +12 -> 124
        //   115: ldc 85
        //   117: aload_1
        //   118: invokevirtual 91	java/lang/String:equals	(Ljava/lang/Object;)Z
        //   121: ifeq +123 -> 244
        //   124: aload_0
        //   125: getfield 25	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mSimNotLoadedSeen	Z
        //   128: ifeq +194 -> 322
        //   131: ldc 85
        //   133: aload_1
        //   134: invokevirtual 91	java/lang/String:equals	(Ljava/lang/Object;)Z
        //   137: ifeq +185 -> 322
        //   140: aload_0
        //   141: iconst_0
        //   142: putfield 25	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mSimNotLoadedSeen	Z
        //   145: aload_0
        //   146: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   149: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   152: invokestatic 99	com/android/server/connectivity/Tethering:-get2	(Lcom/android/server/connectivity/Tethering;)Landroid/content/Context;
        //   155: invokevirtual 105	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   158: ldc 106
        //   160: invokevirtual 112	android/content/res/Resources:getString	(I)Ljava/lang/String;
        //   163: invokevirtual 116	java/lang/String:isEmpty	()Z
        //   166: ifne +250 -> 416
        //   169: new 118	java/util/ArrayList
        //   172: dup
        //   173: invokespecial 119	java/util/ArrayList:<init>	()V
        //   176: astore_2
        //   177: aload_0
        //   178: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   181: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   184: invokestatic 123	com/android/server/connectivity/Tethering:-get9	(Lcom/android/server/connectivity/Tethering;)Ljava/lang/Object;
        //   187: astore_1
        //   188: aload_1
        //   189: monitorenter
        //   190: iconst_0
        //   191: istore_3
        //   192: iload_3
        //   193: aload_0
        //   194: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   197: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   200: invokestatic 127	com/android/server/connectivity/Tethering:-get13	(Lcom/android/server/connectivity/Tethering;)Landroid/util/ArrayMap;
        //   203: invokevirtual 132	android/util/ArrayMap:size	()I
        //   206: if_icmpge +117 -> 323
        //   209: aload_0
        //   210: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   213: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   216: invokestatic 127	com/android/server/connectivity/Tethering:-get13	(Lcom/android/server/connectivity/Tethering;)Landroid/util/ArrayMap;
        //   219: iload_3
        //   220: invokevirtual 136	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
        //   223: checkcast 138	com/android/server/connectivity/Tethering$TetherState
        //   226: getfield 141	com/android/server/connectivity/Tethering$TetherState:mLastState	I
        //   229: istore 4
        //   231: iload 4
        //   233: iconst_2
        //   234: if_icmpeq +18 -> 252
        //   237: iload_3
        //   238: iconst_1
        //   239: iadd
        //   240: istore_3
        //   241: goto -49 -> 192
        //   244: aload_0
        //   245: iconst_1
        //   246: putfield 25	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:mSimNotLoadedSeen	Z
        //   249: goto -125 -> 124
        //   252: aload_0
        //   253: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   256: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   259: invokestatic 127	com/android/server/connectivity/Tethering:-get13	(Lcom/android/server/connectivity/Tethering;)Landroid/util/ArrayMap;
        //   262: iload_3
        //   263: invokevirtual 144	android/util/ArrayMap:keyAt	(I)Ljava/lang/Object;
        //   266: checkcast 87	java/lang/String
        //   269: astore 5
        //   271: aload_0
        //   272: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   275: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   278: aload 5
        //   280: invokestatic 148	com/android/server/connectivity/Tethering:-wrap3	(Lcom/android/server/connectivity/Tethering;Ljava/lang/String;)I
        //   283: istore 4
        //   285: iload 4
        //   287: iconst_m1
        //   288: if_icmpeq -51 -> 237
        //   291: aload_2
        //   292: new 150	java/lang/Integer
        //   295: dup
        //   296: iload 4
        //   298: invokespecial 153	java/lang/Integer:<init>	(I)V
        //   301: invokevirtual 156	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   304: pop
        //   305: goto -68 -> 237
        //   308: astore_2
        //   309: aload_1
        //   310: monitorexit
        //   311: aload_2
        //   312: athrow
        //   313: astore_1
        //   314: ldc 34
        //   316: ldc -98
        //   318: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   321: pop
        //   322: return
        //   323: aload_1
        //   324: monitorexit
        //   325: aload_2
        //   326: invokeinterface 164 1 0
        //   331: astore_1
        //   332: aload_1
        //   333: invokeinterface 169 1 0
        //   338: ifeq +69 -> 407
        //   341: aload_1
        //   342: invokeinterface 173 1 0
        //   347: checkcast 150	java/lang/Integer
        //   350: invokevirtual 176	java/lang/Integer:intValue	()I
        //   353: istore_3
        //   354: new 72	android/content/Intent
        //   357: dup
        //   358: invokespecial 177	android/content/Intent:<init>	()V
        //   361: astore_2
        //   362: aload_2
        //   363: ldc -77
        //   365: iload_3
        //   366: invokevirtual 183	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
        //   369: pop
        //   370: aload_2
        //   371: ldc -71
        //   373: iconst_1
        //   374: invokevirtual 188	android/content/Intent:putExtra	(Ljava/lang/String;Z)Landroid/content/Intent;
        //   377: pop
        //   378: aload_2
        //   379: invokestatic 192	com/android/server/connectivity/Tethering:-get0	()Landroid/content/ComponentName;
        //   382: invokevirtual 196	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
        //   385: pop
        //   386: aload_0
        //   387: getfield 20	com/android/server/connectivity/Tethering$TetherMasterSM$SimChangeBroadcastReceiver:this$1	Lcom/android/server/connectivity/Tethering$TetherMasterSM;
        //   390: getfield 95	com/android/server/connectivity/Tethering$TetherMasterSM:this$0	Lcom/android/server/connectivity/Tethering;
        //   393: invokestatic 99	com/android/server/connectivity/Tethering:-get2	(Lcom/android/server/connectivity/Tethering;)Landroid/content/Context;
        //   396: aload_2
        //   397: getstatic 202	android/os/UserHandle:CURRENT	Landroid/os/UserHandle;
        //   400: invokevirtual 206	android/content/Context:startServiceAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)Landroid/content/ComponentName;
        //   403: pop
        //   404: goto -72 -> 332
        //   407: ldc 34
        //   409: ldc -48
        //   411: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   414: pop
        //   415: return
        //   416: ldc 34
        //   418: ldc -98
        //   420: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   423: pop
        //   424: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	425	0	this	SimChangeBroadcastReceiver
        //   0	425	1	paramContext	Context
        //   0	425	2	paramIntent	Intent
        //   191	175	3	i	int
        //   229	68	4	j	int
        //   269	10	5	str	String
        // Exception table:
        //   from	to	target	type
        //   192	231	308	finally
        //   252	285	308	finally
        //   291	305	308	finally
        //   145	190	313	android/content/res/Resources$NotFoundException
        //   309	313	313	android/content/res/Resources$NotFoundException
        //   323	332	313	android/content/res/Resources$NotFoundException
        //   332	404	313	android/content/res/Resources$NotFoundException
        //   407	415	313	android/content/res/Resources$NotFoundException
        //   416	424	313	android/content/res/Resources$NotFoundException
      }
    }
    
    class StartTetheringErrorState
      extends Tethering.TetherMasterSM.ErrorState
    {
      StartTetheringErrorState()
      {
        super();
      }
      
      public void enter()
      {
        Log.e("Tethering", "Error in startTethering");
        notify(327789);
        try
        {
          Tethering.-get7(Tethering.this).setIpForwardingEnabled(false);
          return;
        }
        catch (Exception localException) {}
      }
    }
    
    class StopTetheringErrorState
      extends Tethering.TetherMasterSM.ErrorState
    {
      StopTetheringErrorState()
      {
        super();
      }
      
      public void enter()
      {
        Log.e("Tethering", "Error in stopTethering");
        notify(327790);
        try
        {
          Tethering.-get7(Tethering.this).setIpForwardingEnabled(false);
          return;
        }
        catch (Exception localException) {}
      }
    }
    
    class TetherMasterUtilState
      extends State
    {
      TetherMasterUtilState() {}
      
      protected void chooseUpstreamType(boolean paramBoolean)
      {
        ConnectivityManager localConnectivityManager = Tethering.-wrap0(Tethering.this);
        int j = -1;
        Object localObject4 = null;
        Object localObject5 = null;
        Tethering.this.updateConfiguration();
        Object localObject6;
        synchronized (Tethering.-get9(Tethering.this))
        {
          Log.d("Tethering", "chooseUpstreamType has upstream iface types:");
          Iterator localIterator = Tethering.-get14(Tethering.this).iterator();
          if (localIterator.hasNext())
          {
            localObject6 = (Integer)localIterator.next();
            Log.d("Tethering", " " + localObject6);
          }
        }
        Object localObject3 = Tethering.-get14(Tethering.this).iterator();
        NetworkInfo localNetworkInfo;
        do
        {
          i = j;
          if (!((Iterator)localObject3).hasNext()) {
            break;
          }
          localObject6 = (Integer)((Iterator)localObject3).next();
          localNetworkInfo = localConnectivityManager.getNetworkInfo(((Integer)localObject6).intValue());
        } while ((localNetworkInfo == null) || (!localNetworkInfo.isConnected()));
        int i = ((Integer)localObject6).intValue();
        Log.d("Tethering", "chooseUpstreamType(" + paramBoolean + ")," + " preferredApn=" + ConnectivityManager.getNetworkTypeName(Tethering.-get8(Tethering.this)) + ", got type=" + ConnectivityManager.getNetworkTypeName(i));
        switch (i)
        {
        default: 
          turnOffUpstreamMobileConnection();
          localObject3 = null;
          if (i != -1)
          {
            localObject3 = Tethering.-wrap0(Tethering.this).getNetworkForType(i);
            NetPluginDelegate.setUpstream((Network)localObject3);
            localObject6 = localConnectivityManager.getLinkProperties(i);
            ??? = localObject5;
            if (localObject6 != null)
            {
              Log.i("Tethering", "Finding IPv4 upstream interface on: " + localObject6);
              localObject4 = RouteInfo.selectBestRoute(((LinkProperties)localObject6).getAllRoutes(), Inet4Address.ANY);
              if (localObject4 == null) {
                break;
              }
              ??? = ((RouteInfo)localObject4).getInterface();
              Log.i("Tethering", "Found interface " + ((RouteInfo)localObject4).getInterface());
            }
            label434:
            localObject4 = ???;
            if (??? != null)
            {
              localObject3 = localConnectivityManager.getNetworkForType(i);
              if (localObject3 == null) {
                Log.e("Tethering", "No Network for upstream type " + i + "!");
              }
              setDnsForwarders((Network)localObject3, (LinkProperties)localObject6);
              localObject4 = ???;
            }
          }
          notifyTetheredOfNewUpstreamIface((String)localObject4);
          ??? = Tethering.-get15(Tethering.this).lookup((Network)localObject3);
          if ((??? != null) && (Tethering.this.pertainsToCurrentUpstream((NetworkState)???))) {
            handleNewUpstreamNetworkState((NetworkState)???);
          }
          break;
        }
        while (Tethering.-get3(Tethering.this) != null)
        {
          return;
          turnOnUpstreamMobileConnection(i);
          break;
          if ((paramBoolean) && (turnOnUpstreamMobileConnection(Tethering.-get8(Tethering.this)))) {
            break;
          }
          Tethering.TetherMasterSM.this.sendMessageDelayed(327684, 10000L);
          break;
          Log.i("Tethering", "No IPv4 upstream interface, giving up.");
          ??? = localObject5;
          break label434;
        }
        handleNewUpstreamNetworkState(null);
      }
      
      protected void handleNewUpstreamNetworkState(NetworkState paramNetworkState)
      {
        Tethering.TetherMasterSM.-get0(Tethering.TetherMasterSM.this).updateUpstreamNetworkState(paramNetworkState);
      }
      
      protected void notifyTetheredOfNewUpstreamIface(String paramString)
      {
        Log.d("Tethering", "Notifying tethered with upstream=" + paramString);
        Tethering.-set0(Tethering.this, paramString);
        Iterator localIterator = Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).iterator();
        while (localIterator.hasNext()) {
          ((TetherInterfaceStateMachine)localIterator.next()).sendMessage(327792, paramString);
        }
      }
      
      public boolean processMessage(Message paramMessage)
      {
        return false;
      }
      
      protected void setDnsForwarders(Network paramNetwork, LinkProperties paramLinkProperties)
      {
        String[] arrayOfString = Tethering.-get4(Tethering.this);
        List localList = paramLinkProperties.getDnsServers();
        paramLinkProperties = arrayOfString;
        if (localList != null) {
          if (!localList.isEmpty()) {
            break label93;
          }
        }
        for (paramLinkProperties = arrayOfString;; paramLinkProperties = NetworkUtils.makeStrings(localList))
        {
          Log.d("Tethering", "Setting DNS forwarders: Network=" + paramNetwork + ", dnsServers=" + Arrays.toString(paramLinkProperties));
          try
          {
            Tethering.-get7(Tethering.this).setDnsForwarders(paramNetwork, paramLinkProperties);
            return;
          }
          catch (Exception paramNetwork)
          {
            label93:
            Log.e("Tethering", "Setting DNS forwarders failed!");
            Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get5(Tethering.TetherMasterSM.this));
          }
        }
      }
      
      protected boolean turnOffMasterTetherSettings()
      {
        try
        {
          Tethering.-get7(Tethering.this).stopTethering();
          return false;
        }
        catch (Exception localException1)
        {
          try
          {
            Tethering.-get7(Tethering.this).setIpForwardingEnabled(false);
            Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get1(Tethering.TetherMasterSM.this));
            return true;
          }
          catch (Exception localException2)
          {
            Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get6(Tethering.TetherMasterSM.this));
          }
          localException1 = localException1;
          Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get10(Tethering.TetherMasterSM.this));
          return false;
        }
      }
      
      protected void turnOffUpstreamMobileConnection()
      {
        if (Tethering.TetherMasterSM.-get3(Tethering.TetherMasterSM.this) != null)
        {
          Tethering.-wrap0(Tethering.this).unregisterNetworkCallback(Tethering.TetherMasterSM.-get3(Tethering.TetherMasterSM.this));
          Tethering.TetherMasterSM.-set1(Tethering.TetherMasterSM.this, null);
        }
        Tethering.TetherMasterSM.-set0(Tethering.TetherMasterSM.this, -1);
      }
      
      protected boolean turnOnMasterTetherSettings()
      {
        try
        {
          Tethering.-get7(Tethering.this).setIpForwardingEnabled(true);
          return false;
        }
        catch (Exception localException1)
        {
          try
          {
            Tethering.-get7(Tethering.this).startTethering(Tethering.-get5(Tethering.this));
            return true;
          }
          catch (Exception localException2)
          {
            try
            {
              Tethering.-get7(Tethering.this).stopTethering();
              Tethering.-get7(Tethering.this).startTethering(Tethering.-get5(Tethering.this));
              return true;
            }
            catch (Exception localException3)
            {
              Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get9(Tethering.TetherMasterSM.this));
            }
          }
          localException1 = localException1;
          Tethering.TetherMasterSM.-wrap2(Tethering.TetherMasterSM.this, Tethering.TetherMasterSM.-get7(Tethering.TetherMasterSM.this));
          return false;
        }
      }
      
      protected boolean turnOnUpstreamMobileConnection(int paramInt)
      {
        if (paramInt == -1) {
          return false;
        }
        if (paramInt != Tethering.TetherMasterSM.-get2(Tethering.TetherMasterSM.this)) {
          turnOffUpstreamMobileConnection();
        }
        if (Tethering.TetherMasterSM.-get3(Tethering.TetherMasterSM.this) != null) {
          return true;
        }
        switch (paramInt)
        {
        case 1: 
        case 2: 
        case 3: 
        default: 
          return false;
        }
        Tethering.TetherMasterSM.-set0(Tethering.TetherMasterSM.this, paramInt);
        Object localObject = new NetworkRequest.Builder().addTransportType(0);
        if (paramInt == 4) {
          ((NetworkRequest.Builder)localObject).removeCapability(13).addCapability(2);
        }
        for (;;)
        {
          localObject = ((NetworkRequest.Builder)localObject).build();
          Tethering.TetherMasterSM.-set1(Tethering.TetherMasterSM.this, new ConnectivityManager.NetworkCallback());
          Log.d("Tethering", "requesting mobile upstream network: " + localObject);
          Tethering.-wrap0(Tethering.this).requestNetwork((NetworkRequest)localObject, Tethering.TetherMasterSM.-get3(Tethering.TetherMasterSM.this), 0, paramInt);
          return true;
          ((NetworkRequest.Builder)localObject).addCapability(12);
        }
      }
    }
    
    class TetherModeAliveState
      extends Tethering.TetherMasterSM.TetherMasterUtilState
    {
      boolean mTryCell = true;
      
      TetherModeAliveState()
      {
        super();
      }
      
      public void enter()
      {
        boolean bool = true;
        turnOnMasterTetherSettings();
        Tethering.TetherMasterSM.-wrap0(Tethering.TetherMasterSM.this);
        Tethering.-get15(Tethering.this).start();
        this.mTryCell = true;
        chooseUpstreamType(this.mTryCell);
        if (this.mTryCell) {
          bool = false;
        }
        this.mTryCell = bool;
      }
      
      public void exit()
      {
        turnOffUpstreamMobileConnection();
        Tethering.-get15(Tethering.this).stop();
        Tethering.TetherMasterSM.-wrap1(Tethering.TetherMasterSM.this);
        notifyTetheredOfNewUpstreamIface(null);
        handleNewUpstreamNetworkState(null);
      }
      
      public boolean processMessage(Message paramMessage)
      {
        boolean bool3 = false;
        boolean bool1 = false;
        Tethering.-wrap5(Tethering.this, this, paramMessage.what);
        boolean bool2 = true;
        switch (paramMessage.what)
        {
        default: 
          bool1 = false;
        }
        Object localObject;
        do
        {
          return bool1;
          paramMessage = (TetherInterfaceStateMachine)paramMessage.obj;
          Log.d("Tethering", "Tether Mode requested by " + paramMessage);
          if (Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).indexOf(paramMessage) < 0)
          {
            Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).add(paramMessage);
            Tethering.TetherMasterSM.-get0(Tethering.TetherMasterSM.this).addActiveDownstream(paramMessage);
          }
          paramMessage.sendMessage(327792, Tethering.-get3(Tethering.this));
          return true;
          paramMessage = (TetherInterfaceStateMachine)paramMessage.obj;
          Log.d("Tethering", "Tether Mode unrequested by " + paramMessage);
          if (Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).remove(paramMessage))
          {
            Log.d("Tethering", "TetherModeAlive removing notifyee " + paramMessage);
            if (Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).isEmpty()) {
              turnOffMasterTetherSettings();
            }
          }
          for (;;)
          {
            Tethering.TetherMasterSM.-get0(Tethering.TetherMasterSM.this).removeActiveDownstream(paramMessage);
            return true;
            Log.d("Tethering", "TetherModeAlive still has " + Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).size() + " live requests:");
            localObject = Tethering.TetherMasterSM.-get4(Tethering.TetherMasterSM.this).iterator();
            while (((Iterator)localObject).hasNext())
            {
              TetherInterfaceStateMachine localTetherInterfaceStateMachine = (TetherInterfaceStateMachine)((Iterator)localObject).next();
              Log.d("Tethering", "  " + localTetherInterfaceStateMachine);
            }
            continue;
            Log.e("Tethering", "TetherModeAliveState UNREQUESTED has unknown who: " + paramMessage);
          }
          this.mTryCell = true;
          chooseUpstreamType(this.mTryCell);
          if (this.mTryCell) {}
          for (;;)
          {
            this.mTryCell = bool1;
            return true;
            bool1 = true;
          }
          chooseUpstreamType(this.mTryCell);
          if (this.mTryCell) {}
          for (bool1 = bool3;; bool1 = true)
          {
            this.mTryCell = bool1;
            return true;
          }
          localObject = Tethering.-get15(Tethering.this).processCallback(paramMessage.arg1, paramMessage.obj);
          if ((localObject != null) && (Tethering.this.pertainsToCurrentUpstream((NetworkState)localObject))) {
            bool1 = bool2;
          }
          switch (paramMessage.arg1)
          {
          case 1: 
          default: 
            return true;
          case 2: 
            handleNewUpstreamNetworkState((NetworkState)localObject);
            return true;
            bool1 = bool2;
          }
        } while (Tethering.-get3(Tethering.this) != null);
        chooseUpstreamType(false);
        return true;
        setDnsForwarders(((NetworkState)localObject).network, ((NetworkState)localObject).linkProperties);
        handleNewUpstreamNetworkState((NetworkState)localObject);
        return true;
        handleNewUpstreamNetworkState(null);
        return true;
      }
    }
  }
  
  private static class TetherState
  {
    public int mLastError;
    public int mLastState;
    public final TetherInterfaceStateMachine mStateMachine;
    
    public TetherState(TetherInterfaceStateMachine paramTetherInterfaceStateMachine)
    {
      this.mStateMachine = paramTetherInterfaceStateMachine;
      this.mLastState = 1;
      this.mLastError = 0;
    }
  }
  
  class UpstreamNetworkCallback
    extends ConnectivityManager.NetworkCallback
  {
    UpstreamNetworkCallback() {}
    
    public void onAvailable(Network paramNetwork)
    {
      Tethering.-get12(Tethering.this).sendMessage(327685, 1, 0, paramNetwork);
    }
    
    public void onCapabilitiesChanged(Network paramNetwork, NetworkCapabilities paramNetworkCapabilities)
    {
      Tethering.-get12(Tethering.this).sendMessage(327685, 2, 0, new NetworkState(null, null, paramNetworkCapabilities, paramNetwork, null, null));
    }
    
    public void onLinkPropertiesChanged(Network paramNetwork, LinkProperties paramLinkProperties)
    {
      Tethering.-get12(Tethering.this).sendMessage(327685, 3, 0, new NetworkState(null, paramLinkProperties, null, paramNetwork, null, null));
    }
    
    public void onLost(Network paramNetwork)
    {
      Tethering.-get12(Tethering.this).sendMessage(327685, 4, 0, paramNetwork);
    }
  }
  
  class UpstreamNetworkMonitor
  {
    static final int EVENT_ON_AVAILABLE = 1;
    static final int EVENT_ON_CAPABILITIES = 2;
    static final int EVENT_ON_LINKPROPERTIES = 3;
    static final int EVENT_ON_LOST = 4;
    ConnectivityManager.NetworkCallback mDefaultNetworkCallback;
    ConnectivityManager.NetworkCallback mDunTetheringCallback;
    final HashMap<Network, NetworkState> mNetworkMap = new HashMap();
    
    UpstreamNetworkMonitor() {}
    
    NetworkState lookup(Network paramNetwork)
    {
      NetworkState localNetworkState = null;
      if (paramNetwork != null) {
        localNetworkState = (NetworkState)this.mNetworkMap.get(paramNetwork);
      }
      return localNetworkState;
    }
    
    NetworkState processCallback(int paramInt, Object paramObject)
    {
      Object localObject;
      switch (paramInt)
      {
      default: 
        return null;
      case 1: 
        paramObject = (Network)paramObject;
        Log.d("Tethering", "EVENT_ON_AVAILABLE for " + paramObject);
        if (!this.mNetworkMap.containsKey(paramObject)) {
          this.mNetworkMap.put(paramObject, new NetworkState(null, null, null, (Network)paramObject, null, null));
        }
        localObject = Tethering.-wrap0(Tethering.this);
        if (this.mDefaultNetworkCallback != null)
        {
          ((ConnectivityManager)localObject).requestNetworkCapabilities(this.mDefaultNetworkCallback);
          ((ConnectivityManager)localObject).requestLinkProperties(this.mDefaultNetworkCallback);
        }
        return (NetworkState)this.mNetworkMap.get(paramObject);
      case 2: 
        paramObject = (NetworkState)paramObject;
        if (!this.mNetworkMap.containsKey(((NetworkState)paramObject).network)) {
          return null;
        }
        Log.d("Tethering", String.format("EVENT_ON_CAPABILITIES for %s: %s", new Object[] { ((NetworkState)paramObject).network, ((NetworkState)paramObject).networkCapabilities }));
        localObject = (NetworkState)this.mNetworkMap.get(((NetworkState)paramObject).network);
        this.mNetworkMap.put(((NetworkState)paramObject).network, new NetworkState(null, ((NetworkState)localObject).linkProperties, ((NetworkState)paramObject).networkCapabilities, ((NetworkState)paramObject).network, null, null));
        return (NetworkState)this.mNetworkMap.get(((NetworkState)paramObject).network);
      case 3: 
        paramObject = (NetworkState)paramObject;
        if (!this.mNetworkMap.containsKey(((NetworkState)paramObject).network)) {
          return null;
        }
        Log.d("Tethering", String.format("EVENT_ON_LINKPROPERTIES for %s: %s", new Object[] { ((NetworkState)paramObject).network, ((NetworkState)paramObject).linkProperties }));
        localObject = (NetworkState)this.mNetworkMap.get(((NetworkState)paramObject).network);
        this.mNetworkMap.put(((NetworkState)paramObject).network, new NetworkState(null, ((NetworkState)paramObject).linkProperties, ((NetworkState)localObject).networkCapabilities, ((NetworkState)paramObject).network, null, null));
        return (NetworkState)this.mNetworkMap.get(((NetworkState)paramObject).network);
      }
      paramObject = (Network)paramObject;
      Log.d("Tethering", "EVENT_ON_LOST for " + paramObject);
      return (NetworkState)this.mNetworkMap.remove(paramObject);
    }
    
    void start()
    {
      stop();
      this.mDefaultNetworkCallback = new Tethering.UpstreamNetworkCallback(Tethering.this);
      Tethering.-wrap0(Tethering.this).registerDefaultNetworkCallback(this.mDefaultNetworkCallback);
      NetworkRequest localNetworkRequest = new NetworkRequest.Builder().addTransportType(0).removeCapability(13).addCapability(2).build();
      this.mDunTetheringCallback = new Tethering.UpstreamNetworkCallback(Tethering.this);
      Tethering.-wrap0(Tethering.this).registerNetworkCallback(localNetworkRequest, this.mDunTetheringCallback);
    }
    
    void stop()
    {
      if (this.mDefaultNetworkCallback != null)
      {
        Tethering.-wrap0(Tethering.this).unregisterNetworkCallback(this.mDefaultNetworkCallback);
        this.mDefaultNetworkCallback = null;
      }
      if (this.mDunTetheringCallback != null)
      {
        Tethering.-wrap0(Tethering.this).unregisterNetworkCallback(this.mDunTetheringCallback);
        this.mDunTetheringCallback = null;
      }
      this.mNetworkMap.clear();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/Tethering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */