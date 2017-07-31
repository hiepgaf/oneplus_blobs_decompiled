package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.INetd;
import android.net.INetd.Stub;
import android.net.INetworkManagementEventObserver;
import android.net.InterfaceConfiguration;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkStats;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.UidRange;
import android.net.wifi.WifiConfiguration;
import android.os.Binder;
import android.os.Handler;
import android.os.INetworkActivityListener;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.net.NetworkStatsFactory;
import com.android.internal.util.HexDump;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

public class NetworkManagementService
  extends INetworkManagementService.Stub
  implements Watchdog.Monitor
{
  static final int DAEMON_MSG_MOBILE_CONN_REAL_TIME_INFO = 1;
  private static final boolean DBG = Log.isLoggable("NetworkManagement", 3);
  public static final int DNS_RESOLVER_DEFAULT_MAX_SAMPLES = 64;
  public static final int DNS_RESOLVER_DEFAULT_MIN_SAMPLES = 8;
  public static final int DNS_RESOLVER_DEFAULT_SAMPLE_VALIDITY_SECONDS = 1800;
  public static final int DNS_RESOLVER_DEFAULT_SUCCESS_THRESHOLD_PERCENT = 25;
  public static final String LIMIT_GLOBAL_ALERT = "globalAlert";
  private static final int MAX_UID_RANGES_PER_COMMAND = 10;
  private static final String NETD_SERVICE_NAME = "netd";
  private static final String NETD_TAG = "NetdConnector";
  public static final String PERMISSION_NETWORK = "NETWORK";
  public static final String PERMISSION_SYSTEM = "SYSTEM";
  static final String SOFT_AP_COMMAND = "softap";
  static final String SOFT_AP_COMMAND_SUCCESS = "Ok";
  private static final String TAG = "NetworkManagement";
  @GuardedBy("mQuotaLock")
  private HashMap<String, Long> mActiveAlerts = Maps.newHashMap();
  private HashMap<String, IdleTimerParams> mActiveIdleTimers = Maps.newHashMap();
  @GuardedBy("mQuotaLock")
  private HashMap<String, Long> mActiveQuotas = Maps.newHashMap();
  private volatile boolean mBandwidthControlEnabled;
  private IBatteryStats mBatteryStats;
  private CountDownLatch mConnectedSignal = new CountDownLatch(1);
  private final NativeDaemonConnector mConnector;
  private final Context mContext;
  private final Handler mDaemonHandler;
  @GuardedBy("mQuotaLock")
  private boolean mDataSaverMode;
  private final Handler mFgHandler;
  @GuardedBy("mQuotaLock")
  final SparseBooleanArray mFirewallChainStates = new SparseBooleanArray();
  private volatile boolean mFirewallEnabled;
  private Object mIdleTimerLock = new Object();
  private int mLastPowerStateFromRadio = 1;
  private int mLastPowerStateFromWifi = 1;
  private boolean mMobileActivityFromRadio = false;
  private INetd mNetdService;
  private boolean mNetworkActive;
  private final RemoteCallbackList<INetworkActivityListener> mNetworkActivityListeners = new RemoteCallbackList();
  private final RemoteCallbackList<INetworkManagementEventObserver> mObservers = new RemoteCallbackList();
  private Object mQuotaLock = new Object();
  private final NetworkStatsFactory mStatsFactory = new NetworkStatsFactory();
  private volatile boolean mStrictEnabled;
  private final Thread mThread;
  @GuardedBy("mQuotaLock")
  private SparseBooleanArray mUidAllowOnMetered = new SparseBooleanArray();
  @GuardedBy("mQuotaLock")
  private SparseIntArray mUidCleartextPolicy = new SparseIntArray();
  @GuardedBy("mQuotaLock")
  private SparseIntArray mUidFirewallDozableRules = new SparseIntArray();
  @GuardedBy("mQuotaLock")
  private SparseIntArray mUidFirewallPowerSaveRules = new SparseIntArray();
  @GuardedBy("mQuotaLock")
  private SparseIntArray mUidFirewallRules = new SparseIntArray();
  @GuardedBy("mQuotaLock")
  private SparseIntArray mUidFirewallStandbyRules = new SparseIntArray();
  @GuardedBy("mQuotaLock")
  private SparseBooleanArray mUidRejectOnMetered = new SparseBooleanArray();
  private BroadcastReceiver mZeroBalanceReceiver = new BroadcastReceiver()
  {
    /* Error */
    public void onReceive(Context paramAnonymousContext, android.content.Intent paramAnonymousIntent)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore 4
      //   3: iload 4
      //   5: istore_3
      //   6: aload_2
      //   7: ifnull +51 -> 58
      //   10: iload 4
      //   12: istore_3
      //   13: aload_2
      //   14: invokevirtual 26	android/content/Intent:getAction	()Ljava/lang/String;
      //   17: ldc 28
      //   19: invokevirtual 34	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   22: ifeq +36 -> 58
      //   25: aload_2
      //   26: ldc 36
      //   28: iconst_0
      //   29: invokevirtual 40	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
      //   32: istore_3
      //   33: ldc 42
      //   35: new 44	java/lang/StringBuilder
      //   38: dup
      //   39: invokespecial 45	java/lang/StringBuilder:<init>	()V
      //   42: ldc 47
      //   44: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   47: iload_3
      //   48: invokevirtual 54	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   51: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   54: invokestatic 63	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
      //   57: pop
      //   58: aload_0
      //   59: getfield 12	com/android/server/NetworkManagementService$1:this$0	Lcom/android/server/NetworkManagementService;
      //   62: invokestatic 67	com/android/server/NetworkManagementService:-get3	(Lcom/android/server/NetworkManagementService;)Landroid/content/Context;
      //   65: ldc 69
      //   67: ldc 71
      //   69: invokevirtual 77	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
      //   72: aload_0
      //   73: getfield 12	com/android/server/NetworkManagementService$1:this$0	Lcom/android/server/NetworkManagementService;
      //   76: invokestatic 81	com/android/server/NetworkManagementService:-get0	(Lcom/android/server/NetworkManagementService;)Z
      //   79: ifne +4 -> 83
      //   82: return
      //   83: ldc 42
      //   85: new 44	java/lang/StringBuilder
      //   88: dup
      //   89: invokespecial 45	java/lang/StringBuilder:<init>	()V
      //   92: ldc 83
      //   94: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   97: iload_3
      //   98: invokevirtual 54	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   101: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   104: invokestatic 63	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
      //   107: pop
      //   108: aload_0
      //   109: getfield 12	com/android/server/NetworkManagementService$1:this$0	Lcom/android/server/NetworkManagementService;
      //   112: invokestatic 87	com/android/server/NetworkManagementService:-get2	(Lcom/android/server/NetworkManagementService;)Lcom/android/server/NativeDaemonConnector;
      //   115: astore_2
      //   116: iload_3
      //   117: ifeq +22 -> 139
      //   120: ldc 89
      //   122: astore_1
      //   123: aload_2
      //   124: ldc 91
      //   126: iconst_1
      //   127: anewarray 93	java/lang/Object
      //   130: dup
      //   131: iconst_0
      //   132: aload_1
      //   133: aastore
      //   134: invokevirtual 99	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
      //   137: pop
      //   138: return
      //   139: ldc 101
      //   141: astore_1
      //   142: goto -19 -> 123
      //   145: astore_1
      //   146: aload_1
      //   147: invokevirtual 105	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
      //   150: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	151	0	this	1
      //   0	151	1	paramAnonymousContext	Context
      //   0	151	2	paramAnonymousIntent	android.content.Intent
      //   5	112	3	bool1	boolean
      //   1	10	4	bool2	boolean
      // Exception table:
      //   from	to	target	type
      //   83	116	145	com/android/server/NativeDaemonConnectorException
      //   123	138	145	com/android/server/NativeDaemonConnectorException
    }
  };
  
  private NetworkManagementService(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mFgHandler = new Handler(FgThread.get().getLooper());
    this.mConnector = new NativeDaemonConnector(new NetdCallbackReceiver(null), paramString, 10, "NetdConnector", 160, null, FgThread.get().getLooper());
    this.mThread = new Thread(this.mConnector, "NetdConnector");
    this.mDaemonHandler = new Handler(FgThread.get().getLooper());
    Watchdog.getInstance().addMonitor(this);
  }
  
  private void closeSocketsForFirewallChainLocked(int paramInt, String paramString)
  {
    Object localObject2 = getUidFirewallRules(paramInt);
    int j = 0;
    int i = 0;
    Object localObject1;
    if (getFirewallType(paramInt) == 0)
    {
      UidRange[] arrayOfUidRange = new UidRange[1];
      arrayOfUidRange[0] = new UidRange(10000, Integer.MAX_VALUE);
      int[] arrayOfInt = new int[((SparseIntArray)localObject2).size()];
      paramInt = 0;
      while (paramInt < arrayOfInt.length)
      {
        j = i;
        if (((SparseIntArray)localObject2).valueAt(paramInt) == 1)
        {
          arrayOfInt[i] = ((SparseIntArray)localObject2).keyAt(paramInt);
          j = i + 1;
        }
        paramInt += 1;
        i = j;
      }
      localObject1 = arrayOfInt;
      localObject2 = arrayOfUidRange;
      if (i != arrayOfInt.length)
      {
        localObject1 = Arrays.copyOf(arrayOfInt, i);
        localObject2 = arrayOfUidRange;
      }
    }
    for (;;)
    {
      try
      {
        this.mNetdService.socketDestroy((UidRange[])localObject2, (int[])localObject1);
        return;
      }
      catch (RemoteException|ServiceSpecificException localRemoteException)
      {
        Slog.e("NetworkManagement", "Error closing sockets after enabling chain " + paramString + ": " + localRemoteException);
      }
      localObject1 = new UidRange[((SparseIntArray)localObject2).size()];
      paramInt = 0;
      i = j;
      if (paramInt < localObject1.length)
      {
        j = i;
        if (((SparseIntArray)localObject2).valueAt(paramInt) == 2)
        {
          j = ((SparseIntArray)localObject2).keyAt(paramInt);
          localObject1[i] = new UidRange(j, j);
          j = i + 1;
        }
        paramInt += 1;
        i = j;
      }
      else
      {
        localObject2 = localObject1;
        if (i != localObject1.length) {
          localObject2 = (UidRange[])Arrays.copyOf((Object[])localObject1, i);
        }
        localObject1 = new int[0];
      }
    }
  }
  
  private void connectNativeNetdService()
  {
    int i = 0;
    try
    {
      this.mNetdService = INetd.Stub.asInterface(ServiceManager.getService("netd"));
      boolean bool = this.mNetdService.isAlive();
      i = bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    if (i == 0) {
      Slog.wtf("NetworkManagement", "Can't connect to NativeNetdService netd");
    }
  }
  
  public static NetworkManagementService create(Context paramContext)
    throws InterruptedException
  {
    return create(paramContext, "netd");
  }
  
  static NetworkManagementService create(Context paramContext, String paramString)
    throws InterruptedException
  {
    paramContext = new NetworkManagementService(paramContext, paramString);
    paramString = paramContext.mConnectedSignal;
    if (DBG) {
      Slog.d("NetworkManagement", "Creating NetworkManagementService");
    }
    paramContext.mThread.start();
    if (DBG) {
      Slog.d("NetworkManagement", "Awaiting socket connection");
    }
    paramString.await();
    if (DBG) {
      Slog.d("NetworkManagement", "Connected");
    }
    paramContext.connectNativeNetdService();
    return paramContext;
  }
  
  private void dumpUidFirewallRule(PrintWriter paramPrintWriter, String paramString, SparseIntArray paramSparseIntArray)
  {
    paramPrintWriter.print("UID firewall ");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" rule: [");
    int j = paramSparseIntArray.size();
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.print(paramSparseIntArray.keyAt(i));
      paramPrintWriter.print(":");
      paramPrintWriter.print(paramSparseIntArray.valueAt(i));
      if (i < j - 1) {
        paramPrintWriter.print(",");
      }
      i += 1;
    }
    paramPrintWriter.println("]");
  }
  
  private void dumpUidRuleOnQuotaLocked(PrintWriter paramPrintWriter, String paramString, SparseBooleanArray paramSparseBooleanArray)
  {
    paramPrintWriter.print("UID bandwith control ");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" rule: [");
    int j = paramSparseBooleanArray.size();
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.print(paramSparseBooleanArray.keyAt(i));
      if (i < j - 1) {
        paramPrintWriter.print(",");
      }
      i += 1;
    }
    paramPrintWriter.println("]");
  }
  
  private static void enforceSystemUid()
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("Only available to AID_SYSTEM");
    }
  }
  
  private List<InterfaceAddress> excludeLinkLocal(List<InterfaceAddress> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      InterfaceAddress localInterfaceAddress = (InterfaceAddress)paramList.next();
      if (!localInterfaceAddress.getAddress().isLinkLocalAddress()) {
        localArrayList.add(localInterfaceAddress);
      }
    }
    return localArrayList;
  }
  
  private void executeOrLogWithMessage(String paramString1, Object[] paramArrayOfObject, int paramInt, String paramString2, String paramString3)
    throws NativeDaemonConnectorException
  {
    paramString1 = this.mConnector.execute(paramString1, paramArrayOfObject);
    if ((paramString1.getCode() == paramInt) && (paramString1.getMessage().equals(paramString2))) {
      return;
    }
    Log.e("NetworkManagement", paramString3 + ": event = " + paramString1);
  }
  
  private IBatteryStats getBatteryStats()
  {
    try
    {
      if (this.mBatteryStats != null)
      {
        localIBatteryStats = this.mBatteryStats;
        return localIBatteryStats;
      }
      this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
      IBatteryStats localIBatteryStats = this.mBatteryStats;
      return localIBatteryStats;
    }
    finally {}
  }
  
  private String getFirewallRuleName(int paramInt1, int paramInt2)
  {
    if (getFirewallType(paramInt1) == 0)
    {
      if (paramInt2 == 1) {
        return "allow";
      }
      return "deny";
    }
    if (paramInt2 == 2) {
      return "deny";
    }
    return "allow";
  }
  
  private int getFirewallType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      if (isFirewallEnabled()) {
        return 0;
      }
      break;
    case 2: 
      return 1;
    case 1: 
      return 0;
    case 3: 
      return 0;
    }
    return 1;
  }
  
  private static String getSecurityType(WifiConfiguration paramWifiConfiguration)
  {
    switch (paramWifiConfiguration.getAuthType())
    {
    case 2: 
    case 3: 
    default: 
      return "open";
    case 1: 
      return "wpa-psk";
    }
    return "wpa2-psk";
  }
  
  private SparseIntArray getUidFirewallRules(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown chain:" + paramInt);
    case 2: 
      return this.mUidFirewallStandbyRules;
    case 1: 
      return this.mUidFirewallDozableRules;
    case 3: 
      return this.mUidFirewallPowerSaveRules;
    }
    return this.mUidFirewallRules;
  }
  
  private void modifyInterfaceForward(boolean paramBoolean, String paramString1, String paramString2)
  {
    if (paramBoolean) {}
    for (String str = "add";; str = "remove")
    {
      paramString1 = new NativeDaemonConnector.Command("ipfwd", new Object[] { str, paramString1, paramString2 });
      try
      {
        this.mConnector.execute(paramString1);
        return;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        throw paramString1.rethrowAsParcelableException();
      }
    }
  }
  
  private void modifyInterfaceInNetwork(String paramString1, String paramString2, String paramString3)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "interface", paramString1, paramString2, paramString3 });
      return;
    }
    catch (NativeDaemonConnectorException paramString1)
    {
      throw paramString1.rethrowAsParcelableException();
    }
  }
  
  private void modifyNat(String paramString1, String paramString2, String paramString3)
    throws SocketException
  {
    paramString1 = new NativeDaemonConnector.Command("nat", new Object[] { paramString1, paramString2, paramString3 });
    paramString2 = NetworkInterface.getByName(paramString2);
    if (paramString2 == null) {
      paramString1.appendArg("0");
    }
    for (;;)
    {
      try
      {
        this.mConnector.execute(paramString1);
        return;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        InetAddress localInetAddress;
        throw paramString1.rethrowAsParcelableException();
      }
      paramString2 = excludeLinkLocal(paramString2.getInterfaceAddresses());
      paramString1.appendArg(Integer.valueOf(paramString2.size()));
      paramString2 = paramString2.iterator();
      if (paramString2.hasNext())
      {
        paramString3 = (InterfaceAddress)paramString2.next();
        localInetAddress = NetworkUtils.getNetworkPart(paramString3.getAddress(), paramString3.getNetworkPrefixLength());
        paramString1.appendArg(localInetAddress.getHostAddress() + "/" + paramString3.getNetworkPrefixLength());
      }
    }
  }
  
  private void modifyRoute(String paramString1, String paramString2, RouteInfo paramRouteInfo)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    paramString1 = new NativeDaemonConnector.Command("network", new Object[] { "route", paramString1, paramString2 });
    paramString1.appendArg(paramRouteInfo.getInterface());
    paramString1.appendArg(paramRouteInfo.getDestination().toString());
    switch (paramRouteInfo.getType())
    {
    }
    for (;;)
    {
      try
      {
        this.mConnector.execute(paramString1);
        return;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        throw paramString1.rethrowAsParcelableException();
      }
      if (paramRouteInfo.hasGateway())
      {
        paramString1.appendArg(paramRouteInfo.getGateway().getHostAddress());
        continue;
        paramString1.appendArg("unreachable");
        continue;
        paramString1.appendArg("throw");
      }
    }
  }
  
  /* Error */
  private void notifyAddressRemoved(String paramString, LinkAddress paramLinkAddress)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 4
    //   9: iconst_0
    //   10: istore_3
    //   11: iload_3
    //   12: iload 4
    //   14: if_icmpge +28 -> 42
    //   17: aload_0
    //   18: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   21: iload_3
    //   22: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   25: checkcast 710	android/net/INetworkManagementEventObserver
    //   28: aload_1
    //   29: aload_2
    //   30: invokeinterface 713 3 0
    //   35: iload_3
    //   36: iconst_1
    //   37: iadd
    //   38: istore_3
    //   39: goto -28 -> 11
    //   42: aload_0
    //   43: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   46: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   49: return
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   55: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   58: aload_1
    //   59: athrow
    //   60: astore 5
    //   62: goto -27 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	NetworkManagementService
    //   0	65	1	paramString	String
    //   0	65	2	paramLinkAddress	LinkAddress
    //   10	29	3	i	int
    //   7	8	4	j	int
    //   60	1	5	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	35	50	finally
    //   17	35	60	android/os/RemoteException
    //   17	35	60	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyAddressUpdated(String paramString, LinkAddress paramLinkAddress)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 4
    //   9: iconst_0
    //   10: istore_3
    //   11: iload_3
    //   12: iload 4
    //   14: if_icmpge +28 -> 42
    //   17: aload_0
    //   18: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   21: iload_3
    //   22: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   25: checkcast 710	android/net/INetworkManagementEventObserver
    //   28: aload_1
    //   29: aload_2
    //   30: invokeinterface 719 3 0
    //   35: iload_3
    //   36: iconst_1
    //   37: iadd
    //   38: istore_3
    //   39: goto -28 -> 11
    //   42: aload_0
    //   43: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   46: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   49: return
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   55: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   58: aload_1
    //   59: athrow
    //   60: astore 5
    //   62: goto -27 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	NetworkManagementService
    //   0	65	1	paramString	String
    //   0	65	2	paramLinkAddress	LinkAddress
    //   10	29	3	i	int
    //   7	8	4	j	int
    //   60	1	5	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	35	50	finally
    //   17	35	60	android/os/RemoteException
    //   17	35	60	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyInterfaceAdded(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore_3
    //   8: iconst_0
    //   9: istore_2
    //   10: iload_2
    //   11: iload_3
    //   12: if_icmpge +27 -> 39
    //   15: aload_0
    //   16: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   19: iload_2
    //   20: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   23: checkcast 710	android/net/INetworkManagementEventObserver
    //   26: aload_1
    //   27: invokeinterface 722 2 0
    //   32: iload_2
    //   33: iconst_1
    //   34: iadd
    //   35: istore_2
    //   36: goto -26 -> 10
    //   39: aload_0
    //   40: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   43: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   46: return
    //   47: astore_1
    //   48: aload_0
    //   49: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   52: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   55: aload_1
    //   56: athrow
    //   57: astore 4
    //   59: goto -27 -> 32
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	NetworkManagementService
    //   0	62	1	paramString	String
    //   9	27	2	i	int
    //   7	6	3	j	int
    //   57	1	4	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   15	32	47	finally
    //   15	32	57	android/os/RemoteException
    //   15	32	57	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyInterfaceClassActivity(int paramInt1, int paramInt2, long paramLong, int paramInt3, boolean paramBoolean)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 728	android/net/ConnectivityManager:isNetworkTypeMobile	(I)Z
    //   4: istore 9
    //   6: iload_2
    //   7: istore 7
    //   9: iload 9
    //   11: ifeq +52 -> 63
    //   14: iload 6
    //   16: ifne +172 -> 188
    //   19: aload_0
    //   20: getfield 278	com/android/server/NetworkManagementService:mMobileActivityFromRadio	Z
    //   23: ifeq +8 -> 31
    //   26: aload_0
    //   27: getfield 280	com/android/server/NetworkManagementService:mLastPowerStateFromRadio	I
    //   30: istore_2
    //   31: iload_2
    //   32: istore 7
    //   34: aload_0
    //   35: getfield 280	com/android/server/NetworkManagementService:mLastPowerStateFromRadio	I
    //   38: iload_2
    //   39: if_icmpeq +24 -> 63
    //   42: aload_0
    //   43: iload_2
    //   44: putfield 280	com/android/server/NetworkManagementService:mLastPowerStateFromRadio	I
    //   47: aload_0
    //   48: invokespecial 730	com/android/server/NetworkManagementService:getBatteryStats	()Lcom/android/internal/app/IBatteryStats;
    //   51: iload_2
    //   52: lload_3
    //   53: iload 5
    //   55: invokeinterface 736 5 0
    //   60: iload_2
    //   61: istore 7
    //   63: iload_1
    //   64: invokestatic 739	android/net/ConnectivityManager:isNetworkTypeWifi	(I)Z
    //   67: ifeq +32 -> 99
    //   70: aload_0
    //   71: getfield 282	com/android/server/NetworkManagementService:mLastPowerStateFromWifi	I
    //   74: iload 7
    //   76: if_icmpeq +23 -> 99
    //   79: aload_0
    //   80: iload 7
    //   82: putfield 282	com/android/server/NetworkManagementService:mLastPowerStateFromWifi	I
    //   85: aload_0
    //   86: invokespecial 730	com/android/server/NetworkManagementService:getBatteryStats	()Lcom/android/internal/app/IBatteryStats;
    //   89: iload 7
    //   91: lload_3
    //   92: iload 5
    //   94: invokeinterface 742 5 0
    //   99: iload 7
    //   101: iconst_2
    //   102: if_icmpeq +94 -> 196
    //   105: iload 7
    //   107: iconst_3
    //   108: if_icmpne +94 -> 202
    //   111: iconst_1
    //   112: istore 8
    //   114: iload 9
    //   116: ifeq +92 -> 208
    //   119: iload 6
    //   121: ifne +87 -> 208
    //   124: aload_0
    //   125: getfield 278	com/android/server/NetworkManagementService:mMobileActivityFromRadio	Z
    //   128: ifeq +80 -> 208
    //   131: iconst_0
    //   132: istore 6
    //   134: aload_0
    //   135: getfield 274	com/android/server/NetworkManagementService:mIdleTimerLock	Ljava/lang/Object;
    //   138: astore 10
    //   140: aload 10
    //   142: monitorenter
    //   143: aload_0
    //   144: getfield 276	com/android/server/NetworkManagementService:mActiveIdleTimers	Ljava/util/HashMap;
    //   147: invokevirtual 747	java/util/HashMap:isEmpty	()Z
    //   150: ifeq +6 -> 156
    //   153: iconst_1
    //   154: istore 8
    //   156: aload_0
    //   157: getfield 749	com/android/server/NetworkManagementService:mNetworkActive	Z
    //   160: iload 8
    //   162: if_icmpeq +13 -> 175
    //   165: aload_0
    //   166: iload 8
    //   168: putfield 749	com/android/server/NetworkManagementService:mNetworkActive	Z
    //   171: iload 8
    //   173: istore 6
    //   175: aload 10
    //   177: monitorexit
    //   178: iload 6
    //   180: ifeq +7 -> 187
    //   183: aload_0
    //   184: invokespecial 752	com/android/server/NetworkManagementService:reportNetworkActive	()V
    //   187: return
    //   188: aload_0
    //   189: iconst_1
    //   190: putfield 278	com/android/server/NetworkManagementService:mMobileActivityFromRadio	Z
    //   193: goto -162 -> 31
    //   196: iconst_1
    //   197: istore 8
    //   199: goto -85 -> 114
    //   202: iconst_0
    //   203: istore 8
    //   205: goto -91 -> 114
    //   208: aload_0
    //   209: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   212: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   215: istore 5
    //   217: iconst_0
    //   218: istore_2
    //   219: iload_2
    //   220: iload 5
    //   222: if_icmpge +33 -> 255
    //   225: aload_0
    //   226: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   229: iload_2
    //   230: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   233: checkcast 710	android/net/INetworkManagementEventObserver
    //   236: iload_1
    //   237: invokestatic 755	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   240: iload 8
    //   242: lload_3
    //   243: invokeinterface 759 5 0
    //   248: iload_2
    //   249: iconst_1
    //   250: iadd
    //   251: istore_2
    //   252: goto -33 -> 219
    //   255: aload_0
    //   256: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   259: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   262: goto -131 -> 131
    //   265: astore 10
    //   267: aload_0
    //   268: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   271: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   274: aload 10
    //   276: athrow
    //   277: astore 11
    //   279: aload 10
    //   281: monitorexit
    //   282: aload 11
    //   284: athrow
    //   285: astore 10
    //   287: goto -39 -> 248
    //   290: astore 10
    //   292: goto -193 -> 99
    //   295: astore 10
    //   297: iload_2
    //   298: istore 7
    //   300: goto -237 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	303	0	this	NetworkManagementService
    //   0	303	1	paramInt1	int
    //   0	303	2	paramInt2	int
    //   0	303	3	paramLong	long
    //   0	303	5	paramInt3	int
    //   0	303	6	paramBoolean	boolean
    //   7	292	7	i	int
    //   112	129	8	bool1	boolean
    //   4	111	9	bool2	boolean
    //   138	38	10	localObject1	Object
    //   265	15	10	localObject2	Object
    //   285	1	10	localRemoteException1	RemoteException
    //   290	1	10	localRemoteException2	RemoteException
    //   295	1	10	localRemoteException3	RemoteException
    //   277	6	11	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   225	248	265	finally
    //   143	153	277	finally
    //   156	171	277	finally
    //   225	248	285	android/os/RemoteException
    //   225	248	285	java/lang/RuntimeException
    //   85	99	290	android/os/RemoteException
    //   47	60	295	android/os/RemoteException
  }
  
  /* Error */
  private void notifyInterfaceDnsServerInfo(String paramString, long paramLong, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 6
    //   9: iconst_0
    //   10: istore 5
    //   12: iload 5
    //   14: iload 6
    //   16: if_icmpge +33 -> 49
    //   19: aload_0
    //   20: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   23: iload 5
    //   25: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   28: checkcast 710	android/net/INetworkManagementEventObserver
    //   31: aload_1
    //   32: lload_2
    //   33: aload 4
    //   35: invokeinterface 762 5 0
    //   40: iload 5
    //   42: iconst_1
    //   43: iadd
    //   44: istore 5
    //   46: goto -34 -> 12
    //   49: aload_0
    //   50: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   53: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   56: return
    //   57: astore_1
    //   58: aload_0
    //   59: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   62: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   65: aload_1
    //   66: athrow
    //   67: astore 7
    //   69: goto -29 -> 40
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	NetworkManagementService
    //   0	72	1	paramString	String
    //   0	72	2	paramLong	long
    //   0	72	4	paramArrayOfString	String[]
    //   10	35	5	i	int
    //   7	10	6	j	int
    //   67	1	7	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   19	40	57	finally
    //   19	40	67	android/os/RemoteException
    //   19	40	67	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyInterfaceLinkStateChanged(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 4
    //   9: iconst_0
    //   10: istore_3
    //   11: iload_3
    //   12: iload 4
    //   14: if_icmpge +28 -> 42
    //   17: aload_0
    //   18: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   21: iload_3
    //   22: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   25: checkcast 710	android/net/INetworkManagementEventObserver
    //   28: aload_1
    //   29: iload_2
    //   30: invokeinterface 765 3 0
    //   35: iload_3
    //   36: iconst_1
    //   37: iadd
    //   38: istore_3
    //   39: goto -28 -> 11
    //   42: aload_0
    //   43: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   46: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   49: return
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   55: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   58: aload_1
    //   59: athrow
    //   60: astore 5
    //   62: goto -27 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	NetworkManagementService
    //   0	65	1	paramString	String
    //   0	65	2	paramBoolean	boolean
    //   10	29	3	i	int
    //   7	8	4	j	int
    //   60	1	5	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	35	50	finally
    //   17	35	60	android/os/RemoteException
    //   17	35	60	java/lang/RuntimeException
  }
  
  private void notifyInterfaceMessage(String paramString)
  {
    int j = this.mObservers.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        ((INetworkManagementEventObserver)this.mObservers.getBroadcastItem(i)).interfaceMessageRecevied(paramString);
        i += 1;
        continue;
        this.mObservers.finishBroadcast();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  private void notifyInterfaceRemoved(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 250	com/android/server/NetworkManagementService:mActiveAlerts	Ljava/util/HashMap;
    //   4: aload_1
    //   5: invokevirtual 771	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8: pop
    //   9: aload_0
    //   10: getfield 248	com/android/server/NetworkManagementService:mActiveQuotas	Ljava/util/HashMap;
    //   13: aload_1
    //   14: invokevirtual 771	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: pop
    //   18: aload_0
    //   19: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   22: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   25: istore_3
    //   26: iconst_0
    //   27: istore_2
    //   28: iload_2
    //   29: iload_3
    //   30: if_icmpge +27 -> 57
    //   33: aload_0
    //   34: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   37: iload_2
    //   38: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   41: checkcast 710	android/net/INetworkManagementEventObserver
    //   44: aload_1
    //   45: invokeinterface 774 2 0
    //   50: iload_2
    //   51: iconst_1
    //   52: iadd
    //   53: istore_2
    //   54: goto -26 -> 28
    //   57: aload_0
    //   58: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   61: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   64: return
    //   65: astore_1
    //   66: aload_0
    //   67: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   70: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   73: aload_1
    //   74: athrow
    //   75: astore 4
    //   77: goto -27 -> 50
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	NetworkManagementService
    //   0	80	1	paramString	String
    //   27	27	2	i	int
    //   25	6	3	j	int
    //   75	1	4	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   33	50	65	finally
    //   33	50	75	android/os/RemoteException
    //   33	50	75	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyInterfaceStatusChanged(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 4
    //   9: iconst_0
    //   10: istore_3
    //   11: iload_3
    //   12: iload 4
    //   14: if_icmpge +28 -> 42
    //   17: aload_0
    //   18: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   21: iload_3
    //   22: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   25: checkcast 710	android/net/INetworkManagementEventObserver
    //   28: aload_1
    //   29: iload_2
    //   30: invokeinterface 777 3 0
    //   35: iload_3
    //   36: iconst_1
    //   37: iadd
    //   38: istore_3
    //   39: goto -28 -> 11
    //   42: aload_0
    //   43: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   46: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   49: return
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   55: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   58: aload_1
    //   59: athrow
    //   60: astore 5
    //   62: goto -27 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	NetworkManagementService
    //   0	65	1	paramString	String
    //   0	65	2	paramBoolean	boolean
    //   10	29	3	i	int
    //   7	8	4	j	int
    //   60	1	5	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	35	50	finally
    //   17	35	60	android/os/RemoteException
    //   17	35	60	java/lang/RuntimeException
  }
  
  /* Error */
  private void notifyLimitReached(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore 4
    //   9: iconst_0
    //   10: istore_3
    //   11: iload_3
    //   12: iload 4
    //   14: if_icmpge +28 -> 42
    //   17: aload_0
    //   18: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   21: iload_3
    //   22: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   25: checkcast 710	android/net/INetworkManagementEventObserver
    //   28: aload_1
    //   29: aload_2
    //   30: invokeinterface 780 3 0
    //   35: iload_3
    //   36: iconst_1
    //   37: iadd
    //   38: istore_3
    //   39: goto -28 -> 11
    //   42: aload_0
    //   43: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   46: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   49: return
    //   50: astore_1
    //   51: aload_0
    //   52: getfield 230	com/android/server/NetworkManagementService:mObservers	Landroid/os/RemoteCallbackList;
    //   55: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   58: aload_1
    //   59: athrow
    //   60: astore 5
    //   62: goto -27 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	NetworkManagementService
    //   0	65	1	paramString1	String
    //   0	65	2	paramString2	String
    //   10	29	3	i	int
    //   7	8	4	j	int
    //   60	1	5	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	35	50	finally
    //   17	35	60	android/os/RemoteException
    //   17	35	60	java/lang/RuntimeException
  }
  
  private void notifyRouteChange(String paramString, RouteInfo paramRouteInfo)
  {
    int j = this.mObservers.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        if (paramString.equals("updated")) {
          ((INetworkManagementEventObserver)this.mObservers.getBroadcastItem(i)).routeUpdated(paramRouteInfo);
        } else {
          ((INetworkManagementEventObserver)this.mObservers.getBroadcastItem(i)).routeRemoved(paramRouteInfo);
        }
      }
      catch (RemoteException|RuntimeException localRemoteException)
      {
        break label90;
        return;
      }
      finally
      {
        this.mObservers.finishBroadcast();
      }
      label90:
      i += 1;
    }
  }
  
  /* Error */
  private void prepareNativeDaemon()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: putfield 120	com/android/server/NetworkManagementService:mBandwidthControlEnabled	Z
    //   5: new 791	java/io/File
    //   8: dup
    //   9: ldc_w 793
    //   12: invokespecial 794	java/io/File:<init>	(Ljava/lang/String;)V
    //   15: invokevirtual 797	java/io/File:exists	()Z
    //   18: ifeq +262 -> 280
    //   21: ldc 63
    //   23: ldc_w 799
    //   26: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   29: pop
    //   30: aload_0
    //   31: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   34: ldc_w 801
    //   37: iconst_1
    //   38: anewarray 237	java/lang/Object
    //   41: dup
    //   42: iconst_0
    //   43: ldc_w 803
    //   46: aastore
    //   47: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   50: pop
    //   51: aload_0
    //   52: iconst_1
    //   53: putfield 120	com/android/server/NetworkManagementService:mBandwidthControlEnabled	Z
    //   56: aload_0
    //   57: getfield 120	com/android/server/NetworkManagementService:mBandwidthControlEnabled	Z
    //   60: ifeq +232 -> 292
    //   63: ldc_w 805
    //   66: astore_3
    //   67: ldc_w 807
    //   70: aload_3
    //   71: invokestatic 812	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   74: aload_0
    //   75: getfield 120	com/android/server/NetworkManagementService:mBandwidthControlEnabled	Z
    //   78: ifeq +12 -> 90
    //   81: aload_0
    //   82: invokespecial 730	com/android/server/NetworkManagementService:getBatteryStats	()Lcom/android/internal/app/IBatteryStats;
    //   85: invokeinterface 815 1 0
    //   90: aload_0
    //   91: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   94: ldc_w 817
    //   97: iconst_1
    //   98: anewarray 237	java/lang/Object
    //   101: dup
    //   102: iconst_0
    //   103: ldc_w 803
    //   106: aastore
    //   107: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   110: pop
    //   111: aload_0
    //   112: iconst_1
    //   113: putfield 819	com/android/server/NetworkManagementService:mStrictEnabled	Z
    //   116: aload_0
    //   117: getfield 240	com/android/server/NetworkManagementService:mQuotaLock	Ljava/lang/Object;
    //   120: astore_3
    //   121: aload_3
    //   122: monitorenter
    //   123: aload_0
    //   124: aload_0
    //   125: getfield 821	com/android/server/NetworkManagementService:mDataSaverMode	Z
    //   128: invokevirtual 825	com/android/server/NetworkManagementService:setDataSaverModeEnabled	(Z)Z
    //   131: pop
    //   132: aload_0
    //   133: getfield 248	com/android/server/NetworkManagementService:mActiveQuotas	Ljava/util/HashMap;
    //   136: invokevirtual 826	java/util/HashMap:size	()I
    //   139: istore_1
    //   140: iload_1
    //   141: ifle +172 -> 313
    //   144: getstatic 216	com/android/server/NetworkManagementService:DBG	Z
    //   147: ifeq +35 -> 182
    //   150: ldc 63
    //   152: new 381	java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   159: ldc_w 828
    //   162: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: iload_1
    //   166: invokevirtual 593	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: ldc_w 830
    //   172: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   178: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   181: pop
    //   182: aload_0
    //   183: getfield 248	com/android/server/NetworkManagementService:mActiveQuotas	Ljava/util/HashMap;
    //   186: astore 4
    //   188: aload_0
    //   189: invokestatic 246	com/google/android/collect/Maps:newHashMap	()Ljava/util/HashMap;
    //   192: putfield 248	com/android/server/NetworkManagementService:mActiveQuotas	Ljava/util/HashMap;
    //   195: aload 4
    //   197: invokevirtual 834	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   200: invokeinterface 503 1 0
    //   205: astore 4
    //   207: aload 4
    //   209: invokeinterface 508 1 0
    //   214: ifeq +99 -> 313
    //   217: aload 4
    //   219: invokeinterface 512 1 0
    //   224: checkcast 836	java/util/Map$Entry
    //   227: astore 5
    //   229: aload_0
    //   230: aload 5
    //   232: invokeinterface 839 1 0
    //   237: checkcast 547	java/lang/String
    //   240: aload 5
    //   242: invokeinterface 842 1 0
    //   247: checkcast 844	java/lang/Long
    //   250: invokevirtual 848	java/lang/Long:longValue	()J
    //   253: invokevirtual 852	com/android/server/NetworkManagementService:setInterfaceQuota	(Ljava/lang/String;J)V
    //   256: goto -49 -> 207
    //   259: astore 4
    //   261: aload_3
    //   262: monitorexit
    //   263: aload 4
    //   265: athrow
    //   266: astore_3
    //   267: ldc 63
    //   269: ldc_w 854
    //   272: aload_3
    //   273: invokestatic 857	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   276: pop
    //   277: goto -221 -> 56
    //   280: ldc 63
    //   282: ldc_w 859
    //   285: invokestatic 862	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   288: pop
    //   289: goto -233 -> 56
    //   292: ldc_w 639
    //   295: astore_3
    //   296: goto -229 -> 67
    //   299: astore_3
    //   300: ldc 63
    //   302: ldc_w 864
    //   305: aload_3
    //   306: invokestatic 857	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   309: pop
    //   310: goto -194 -> 116
    //   313: aload_0
    //   314: getfield 250	com/android/server/NetworkManagementService:mActiveAlerts	Ljava/util/HashMap;
    //   317: invokevirtual 826	java/util/HashMap:size	()I
    //   320: istore_1
    //   321: iload_1
    //   322: ifle +118 -> 440
    //   325: getstatic 216	com/android/server/NetworkManagementService:DBG	Z
    //   328: ifeq +35 -> 363
    //   331: ldc 63
    //   333: new 381	java/lang/StringBuilder
    //   336: dup
    //   337: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   340: ldc_w 828
    //   343: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   346: iload_1
    //   347: invokevirtual 593	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   350: ldc_w 866
    //   353: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   359: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   362: pop
    //   363: aload_0
    //   364: getfield 250	com/android/server/NetworkManagementService:mActiveAlerts	Ljava/util/HashMap;
    //   367: astore 4
    //   369: aload_0
    //   370: invokestatic 246	com/google/android/collect/Maps:newHashMap	()Ljava/util/HashMap;
    //   373: putfield 250	com/android/server/NetworkManagementService:mActiveAlerts	Ljava/util/HashMap;
    //   376: aload 4
    //   378: invokevirtual 834	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   381: invokeinterface 503 1 0
    //   386: astore 4
    //   388: aload 4
    //   390: invokeinterface 508 1 0
    //   395: ifeq +45 -> 440
    //   398: aload 4
    //   400: invokeinterface 512 1 0
    //   405: checkcast 836	java/util/Map$Entry
    //   408: astore 5
    //   410: aload_0
    //   411: aload 5
    //   413: invokeinterface 839 1 0
    //   418: checkcast 547	java/lang/String
    //   421: aload 5
    //   423: invokeinterface 842 1 0
    //   428: checkcast 844	java/lang/Long
    //   431: invokevirtual 848	java/lang/Long:longValue	()J
    //   434: invokevirtual 869	com/android/server/NetworkManagementService:setInterfaceAlert	(Ljava/lang/String;J)V
    //   437: goto -49 -> 388
    //   440: aload_0
    //   441: getfield 255	com/android/server/NetworkManagementService:mUidRejectOnMetered	Landroid/util/SparseBooleanArray;
    //   444: invokevirtual 476	android/util/SparseBooleanArray:size	()I
    //   447: istore_1
    //   448: iload_1
    //   449: ifle +92 -> 541
    //   452: getstatic 216	com/android/server/NetworkManagementService:DBG	Z
    //   455: ifeq +35 -> 490
    //   458: ldc 63
    //   460: new 381	java/lang/StringBuilder
    //   463: dup
    //   464: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   467: ldc_w 828
    //   470: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   473: iload_1
    //   474: invokevirtual 593	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   477: ldc_w 871
    //   480: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   483: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   486: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   489: pop
    //   490: aload_0
    //   491: getfield 255	com/android/server/NetworkManagementService:mUidRejectOnMetered	Landroid/util/SparseBooleanArray;
    //   494: astore 4
    //   496: aload_0
    //   497: new 252	android/util/SparseBooleanArray
    //   500: dup
    //   501: invokespecial 253	android/util/SparseBooleanArray:<init>	()V
    //   504: putfield 255	com/android/server/NetworkManagementService:mUidRejectOnMetered	Landroid/util/SparseBooleanArray;
    //   507: iconst_0
    //   508: istore_1
    //   509: iload_1
    //   510: aload 4
    //   512: invokevirtual 476	android/util/SparseBooleanArray:size	()I
    //   515: if_icmpge +26 -> 541
    //   518: aload_0
    //   519: aload 4
    //   521: iload_1
    //   522: invokevirtual 477	android/util/SparseBooleanArray:keyAt	(I)I
    //   525: aload 4
    //   527: iload_1
    //   528: invokevirtual 873	android/util/SparseBooleanArray:valueAt	(I)Z
    //   531: invokevirtual 877	com/android/server/NetworkManagementService:setUidMeteredNetworkBlacklist	(IZ)V
    //   534: iload_1
    //   535: iconst_1
    //   536: iadd
    //   537: istore_1
    //   538: goto -29 -> 509
    //   541: aload_0
    //   542: getfield 257	com/android/server/NetworkManagementService:mUidAllowOnMetered	Landroid/util/SparseBooleanArray;
    //   545: invokevirtual 476	android/util/SparseBooleanArray:size	()I
    //   548: istore_1
    //   549: iload_1
    //   550: ifle +92 -> 642
    //   553: getstatic 216	com/android/server/NetworkManagementService:DBG	Z
    //   556: ifeq +35 -> 591
    //   559: ldc 63
    //   561: new 381	java/lang/StringBuilder
    //   564: dup
    //   565: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   568: ldc_w 828
    //   571: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   574: iload_1
    //   575: invokevirtual 593	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   578: ldc_w 879
    //   581: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   587: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   590: pop
    //   591: aload_0
    //   592: getfield 257	com/android/server/NetworkManagementService:mUidAllowOnMetered	Landroid/util/SparseBooleanArray;
    //   595: astore 4
    //   597: aload_0
    //   598: new 252	android/util/SparseBooleanArray
    //   601: dup
    //   602: invokespecial 253	android/util/SparseBooleanArray:<init>	()V
    //   605: putfield 257	com/android/server/NetworkManagementService:mUidAllowOnMetered	Landroid/util/SparseBooleanArray;
    //   608: iconst_0
    //   609: istore_1
    //   610: iload_1
    //   611: aload 4
    //   613: invokevirtual 476	android/util/SparseBooleanArray:size	()I
    //   616: if_icmpge +26 -> 642
    //   619: aload_0
    //   620: aload 4
    //   622: iload_1
    //   623: invokevirtual 477	android/util/SparseBooleanArray:keyAt	(I)I
    //   626: aload 4
    //   628: iload_1
    //   629: invokevirtual 873	android/util/SparseBooleanArray:valueAt	(I)Z
    //   632: invokevirtual 882	com/android/server/NetworkManagementService:setUidMeteredNetworkWhitelist	(IZ)V
    //   635: iload_1
    //   636: iconst_1
    //   637: iadd
    //   638: istore_1
    //   639: goto -29 -> 610
    //   642: aload_0
    //   643: getfield 262	com/android/server/NetworkManagementService:mUidCleartextPolicy	Landroid/util/SparseIntArray;
    //   646: invokevirtual 354	android/util/SparseIntArray:size	()I
    //   649: istore_1
    //   650: iload_1
    //   651: ifle +92 -> 743
    //   654: getstatic 216	com/android/server/NetworkManagementService:DBG	Z
    //   657: ifeq +35 -> 692
    //   660: ldc 63
    //   662: new 381	java/lang/StringBuilder
    //   665: dup
    //   666: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   669: ldc_w 828
    //   672: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   675: iload_1
    //   676: invokevirtual 593	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   679: ldc_w 884
    //   682: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   685: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   688: invokestatic 439	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   691: pop
    //   692: aload_0
    //   693: getfield 262	com/android/server/NetworkManagementService:mUidCleartextPolicy	Landroid/util/SparseIntArray;
    //   696: astore 4
    //   698: aload_0
    //   699: new 259	android/util/SparseIntArray
    //   702: dup
    //   703: invokespecial 260	android/util/SparseIntArray:<init>	()V
    //   706: putfield 262	com/android/server/NetworkManagementService:mUidCleartextPolicy	Landroid/util/SparseIntArray;
    //   709: iconst_0
    //   710: istore_1
    //   711: iload_1
    //   712: aload 4
    //   714: invokevirtual 354	android/util/SparseIntArray:size	()I
    //   717: if_icmpge +26 -> 743
    //   720: aload_0
    //   721: aload 4
    //   723: iload_1
    //   724: invokevirtual 360	android/util/SparseIntArray:keyAt	(I)I
    //   727: aload 4
    //   729: iload_1
    //   730: invokevirtual 357	android/util/SparseIntArray:valueAt	(I)I
    //   733: invokevirtual 887	com/android/server/NetworkManagementService:setUidCleartextNetworkPolicy	(II)V
    //   736: iload_1
    //   737: iconst_1
    //   738: iadd
    //   739: istore_1
    //   740: goto -29 -> 711
    //   743: aload_0
    //   744: getfield 889	com/android/server/NetworkManagementService:mFirewallEnabled	Z
    //   747: ifne +114 -> 861
    //   750: invokestatic 894	com/android/server/net/LockdownVpnTracker:isEnabled	()Z
    //   753: istore_2
    //   754: aload_0
    //   755: iload_2
    //   756: invokevirtual 898	com/android/server/NetworkManagementService:setFirewallEnabled	(Z)V
    //   759: aload_0
    //   760: iconst_0
    //   761: aload_0
    //   762: getfield 264	com/android/server/NetworkManagementService:mUidFirewallRules	Landroid/util/SparseIntArray;
    //   765: ldc_w 900
    //   768: invokespecial 904	com/android/server/NetworkManagementService:syncFirewallChainLocked	(ILandroid/util/SparseIntArray;Ljava/lang/String;)V
    //   771: aload_0
    //   772: iconst_2
    //   773: aload_0
    //   774: getfield 266	com/android/server/NetworkManagementService:mUidFirewallStandbyRules	Landroid/util/SparseIntArray;
    //   777: ldc_w 906
    //   780: invokespecial 904	com/android/server/NetworkManagementService:syncFirewallChainLocked	(ILandroid/util/SparseIntArray;Ljava/lang/String;)V
    //   783: aload_0
    //   784: iconst_1
    //   785: aload_0
    //   786: getfield 268	com/android/server/NetworkManagementService:mUidFirewallDozableRules	Landroid/util/SparseIntArray;
    //   789: ldc_w 908
    //   792: invokespecial 904	com/android/server/NetworkManagementService:syncFirewallChainLocked	(ILandroid/util/SparseIntArray;Ljava/lang/String;)V
    //   795: aload_0
    //   796: iconst_3
    //   797: aload_0
    //   798: getfield 270	com/android/server/NetworkManagementService:mUidFirewallPowerSaveRules	Landroid/util/SparseIntArray;
    //   801: ldc_w 910
    //   804: invokespecial 904	com/android/server/NetworkManagementService:syncFirewallChainLocked	(ILandroid/util/SparseIntArray;Ljava/lang/String;)V
    //   807: aload_0
    //   808: getfield 272	com/android/server/NetworkManagementService:mFirewallChainStates	Landroid/util/SparseBooleanArray;
    //   811: iconst_2
    //   812: invokevirtual 912	android/util/SparseBooleanArray:get	(I)Z
    //   815: ifeq +9 -> 824
    //   818: aload_0
    //   819: iconst_2
    //   820: iconst_1
    //   821: invokevirtual 915	com/android/server/NetworkManagementService:setFirewallChainEnabled	(IZ)V
    //   824: aload_0
    //   825: getfield 272	com/android/server/NetworkManagementService:mFirewallChainStates	Landroid/util/SparseBooleanArray;
    //   828: iconst_1
    //   829: invokevirtual 912	android/util/SparseBooleanArray:get	(I)Z
    //   832: ifeq +9 -> 841
    //   835: aload_0
    //   836: iconst_1
    //   837: iconst_1
    //   838: invokevirtual 915	com/android/server/NetworkManagementService:setFirewallChainEnabled	(IZ)V
    //   841: aload_0
    //   842: getfield 272	com/android/server/NetworkManagementService:mFirewallChainStates	Landroid/util/SparseBooleanArray;
    //   845: iconst_3
    //   846: invokevirtual 912	android/util/SparseBooleanArray:get	(I)Z
    //   849: ifeq +9 -> 858
    //   852: aload_0
    //   853: iconst_3
    //   854: iconst_1
    //   855: invokevirtual 915	com/android/server/NetworkManagementService:setFirewallChainEnabled	(IZ)V
    //   858: aload_3
    //   859: monitorexit
    //   860: return
    //   861: iconst_1
    //   862: istore_2
    //   863: goto -109 -> 754
    //   866: astore_3
    //   867: goto -777 -> 90
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	870	0	this	NetworkManagementService
    //   139	601	1	i	int
    //   753	110	2	bool	boolean
    //   266	7	3	localNativeDaemonConnectorException1	NativeDaemonConnectorException
    //   295	1	3	str	String
    //   299	560	3	localNativeDaemonConnectorException2	NativeDaemonConnectorException
    //   866	1	3	localRemoteException	RemoteException
    //   186	32	4	localObject2	Object
    //   259	5	4	localObject3	Object
    //   367	361	4	localObject4	Object
    //   227	195	5	localEntry	Map.Entry
    // Exception table:
    //   from	to	target	type
    //   123	140	259	finally
    //   144	182	259	finally
    //   182	207	259	finally
    //   207	256	259	finally
    //   313	321	259	finally
    //   325	363	259	finally
    //   363	388	259	finally
    //   388	437	259	finally
    //   440	448	259	finally
    //   452	490	259	finally
    //   490	507	259	finally
    //   509	534	259	finally
    //   541	549	259	finally
    //   553	591	259	finally
    //   591	608	259	finally
    //   610	635	259	finally
    //   642	650	259	finally
    //   654	692	259	finally
    //   692	709	259	finally
    //   711	736	259	finally
    //   743	754	259	finally
    //   754	824	259	finally
    //   824	841	259	finally
    //   841	858	259	finally
    //   30	56	266	com/android/server/NativeDaemonConnectorException
    //   90	116	299	com/android/server/NativeDaemonConnectorException
    //   81	90	866	android/os/RemoteException
  }
  
  /* Error */
  private ArrayList<String> readRouteList(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: new 493	java/util/ArrayList
    //   7: dup
    //   8: invokespecial 920	java/util/ArrayList:<init>	()V
    //   11: astore 4
    //   13: new 922	java/io/FileInputStream
    //   16: dup
    //   17: aload_1
    //   18: invokespecial 923	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   21: astore_1
    //   22: new 925	java/io/BufferedReader
    //   25: dup
    //   26: new 927	java/io/InputStreamReader
    //   29: dup
    //   30: new 929	java/io/DataInputStream
    //   33: dup
    //   34: aload_1
    //   35: invokespecial 932	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   38: invokespecial 933	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   41: invokespecial 936	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   44: astore_2
    //   45: aload_2
    //   46: invokevirtual 939	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   49: astore_3
    //   50: aload_3
    //   51: ifnull +32 -> 83
    //   54: aload_3
    //   55: invokevirtual 942	java/lang/String:length	()I
    //   58: ifeq +25 -> 83
    //   61: aload 4
    //   63: aload_3
    //   64: invokevirtual 527	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   67: pop
    //   68: goto -23 -> 45
    //   71: astore_2
    //   72: aload_1
    //   73: ifnull +7 -> 80
    //   76: aload_1
    //   77: invokevirtual 945	java/io/FileInputStream:close	()V
    //   80: aload 4
    //   82: areturn
    //   83: aload_1
    //   84: ifnull +7 -> 91
    //   87: aload_1
    //   88: invokevirtual 945	java/io/FileInputStream:close	()V
    //   91: aload 4
    //   93: areturn
    //   94: astore_1
    //   95: goto -4 -> 91
    //   98: astore_1
    //   99: aload 4
    //   101: areturn
    //   102: astore_1
    //   103: aload_2
    //   104: ifnull +7 -> 111
    //   107: aload_2
    //   108: invokevirtual 945	java/io/FileInputStream:close	()V
    //   111: aload_1
    //   112: athrow
    //   113: astore_2
    //   114: goto -3 -> 111
    //   117: astore_3
    //   118: aload_1
    //   119: astore_2
    //   120: aload_3
    //   121: astore_1
    //   122: goto -19 -> 103
    //   125: astore_1
    //   126: aload_3
    //   127: astore_1
    //   128: goto -56 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	this	NetworkManagementService
    //   0	131	1	paramString	String
    //   1	45	2	localBufferedReader	java.io.BufferedReader
    //   71	37	2	localIOException1	IOException
    //   113	1	2	localIOException2	IOException
    //   119	1	2	str1	String
    //   3	61	3	str2	String
    //   117	10	3	localObject	Object
    //   11	89	4	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   22	45	71	java/io/IOException
    //   45	50	71	java/io/IOException
    //   54	68	71	java/io/IOException
    //   87	91	94	java/io/IOException
    //   76	80	98	java/io/IOException
    //   13	22	102	finally
    //   107	111	113	java/io/IOException
    //   22	45	117	finally
    //   45	50	117	finally
    //   54	68	117	finally
    //   13	22	125	java/io/IOException
  }
  
  /* Error */
  private void reportNetworkActive()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 284	com/android/server/NetworkManagementService:mNetworkActivityListeners	Landroid/os/RemoteCallbackList;
    //   4: invokevirtual 704	android/os/RemoteCallbackList:beginBroadcast	()I
    //   7: istore_2
    //   8: iconst_0
    //   9: istore_1
    //   10: iload_1
    //   11: iload_2
    //   12: if_icmpge +26 -> 38
    //   15: aload_0
    //   16: getfield 284	com/android/server/NetworkManagementService:mNetworkActivityListeners	Landroid/os/RemoteCallbackList;
    //   19: iload_1
    //   20: invokevirtual 708	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   23: checkcast 948	android/os/INetworkActivityListener
    //   26: invokeinterface 951 1 0
    //   31: iload_1
    //   32: iconst_1
    //   33: iadd
    //   34: istore_1
    //   35: goto -25 -> 10
    //   38: aload_0
    //   39: getfield 284	com/android/server/NetworkManagementService:mNetworkActivityListeners	Landroid/os/RemoteCallbackList;
    //   42: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   45: return
    //   46: astore_3
    //   47: aload_0
    //   48: getfield 284	com/android/server/NetworkManagementService:mNetworkActivityListeners	Landroid/os/RemoteCallbackList;
    //   51: invokevirtual 716	android/os/RemoteCallbackList:finishBroadcast	()V
    //   54: aload_3
    //   55: athrow
    //   56: astore_3
    //   57: goto -26 -> 31
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	this	NetworkManagementService
    //   9	26	1	i	int
    //   7	6	2	j	int
    //   46	9	3	localObject	Object
    //   56	1	3	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   15	31	46	finally
    //   15	31	56	android/os/RemoteException
    //   15	31	56	java/lang/RuntimeException
  }
  
  private void setFirewallUidRuleLocked(int paramInt1, int paramInt2, int paramInt3)
  {
    if (updateFirewallUidRuleLocked(paramInt1, paramInt2, paramInt3)) {}
    try
    {
      this.mConnector.execute("firewall", new Object[] { "set_uid_rule", getFirewallChainName(paramInt1), Integer.valueOf(paramInt2), getFirewallRuleName(paramInt1, paramInt3) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  private void setUidOnMeteredNetworkList(SparseBooleanArray paramSparseBooleanArray, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    String str1;
    if (paramBoolean1) {
      str1 = "naughtyapps";
    }
    for (;;)
    {
      String str2;
      if (paramBoolean2) {
        str2 = "add";
      }
      synchronized (this.mQuotaLock)
      {
        for (;;)
        {
          paramBoolean1 = paramSparseBooleanArray.get(paramInt, false);
          if (paramBoolean1 != paramBoolean2) {
            break label81;
          }
          return;
          str1 = "niceapps";
          break;
          str2 = "remove";
        }
        try
        {
          label81:
          this.mConnector.execute("bandwidth", new Object[] { str2 + str1, Integer.valueOf(paramInt) });
          if (paramBoolean2) {
            paramSparseBooleanArray.put(paramInt, true);
          }
          for (;;)
          {
            return;
            paramSparseBooleanArray.delete(paramInt);
          }
          paramSparseBooleanArray = finally;
        }
        catch (NativeDaemonConnectorException paramSparseBooleanArray)
        {
          throw paramSparseBooleanArray.rethrowAsParcelableException();
        }
      }
    }
  }
  
  private void syncFirewallChainLocked(int paramInt, SparseIntArray paramSparseIntArray, String paramString)
  {
    int i = paramSparseIntArray.size();
    if (i > 0)
    {
      SparseIntArray localSparseIntArray = paramSparseIntArray.clone();
      paramSparseIntArray.clear();
      if (DBG) {
        Slog.d("NetworkManagement", "Pushing " + i + " active firewall " + paramString + "UID rules");
      }
      i = 0;
      while (i < localSparseIntArray.size())
      {
        setFirewallUidRuleLocked(paramInt, localSparseIntArray.keyAt(i), localSparseIntArray.valueAt(i));
        i += 1;
      }
    }
  }
  
  private boolean updateFirewallUidRuleLocked(int paramInt1, int paramInt2, int paramInt3)
  {
    SparseIntArray localSparseIntArray = getUidFirewallRules(paramInt1);
    int i = localSparseIntArray.get(paramInt2, 0);
    if (DBG) {
      Slog.d("NetworkManagement", "oldRule = " + i + ", newRule=" + paramInt3 + " for uid=" + paramInt2 + " on chain " + paramInt1);
    }
    if (i == paramInt3)
    {
      if (DBG) {
        Slog.d("NetworkManagement", "!!!!! Skipping change");
      }
      return false;
    }
    String str1 = getFirewallRuleName(paramInt1, paramInt3);
    String str2 = getFirewallRuleName(paramInt1, i);
    if (paramInt3 == 0) {
      localSparseIntArray.delete(paramInt2);
    }
    while (str1.equals(str2))
    {
      return false;
      localSparseIntArray.put(paramInt2, paramInt3);
    }
    return true;
  }
  
  public void addIdleTimer(String paramString, int paramInt1, final int paramInt2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (DBG) {
      Slog.d("NetworkManagement", "Adding idletimer");
    }
    synchronized (this.mIdleTimerLock)
    {
      IdleTimerParams localIdleTimerParams = (IdleTimerParams)this.mActiveIdleTimers.get(paramString);
      if (localIdleTimerParams != null)
      {
        localIdleTimerParams.networkCount += 1;
        return;
      }
      try
      {
        this.mConnector.execute("idletimer", new Object[] { "add", paramString, Integer.toString(paramInt1), Integer.toString(paramInt2) });
        this.mActiveIdleTimers.put(paramString, new IdleTimerParams(paramInt1, paramInt2));
        if (ConnectivityManager.isNetworkTypeMobile(paramInt2)) {
          this.mNetworkActive = false;
        }
        this.mDaemonHandler.post(new Runnable()
        {
          public void run()
          {
            NetworkManagementService.-wrap4(NetworkManagementService.this, paramInt2, 3, SystemClock.elapsedRealtimeNanos(), -1, false);
          }
        });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void addInterfaceToLocalNetwork(String paramString, List<RouteInfo> paramList)
  {
    modifyInterfaceInNetwork("add", "local", paramString);
    paramString = paramList.iterator();
    while (paramString.hasNext())
    {
      paramList = (RouteInfo)paramString.next();
      if (!paramList.isDefaultRoute()) {
        modifyRoute("add", "local", paramList);
      }
    }
  }
  
  public void addInterfaceToNetwork(String paramString, int paramInt)
  {
    modifyInterfaceInNetwork("add", "" + paramInt, paramString);
  }
  
  public void addLegacyRouteForNetId(int paramInt1, RouteInfo paramRouteInfo, int paramInt2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    NativeDaemonConnector.Command localCommand = new NativeDaemonConnector.Command("network", new Object[] { "route", "legacy", Integer.valueOf(paramInt2), "add", Integer.valueOf(paramInt1) });
    LinkAddress localLinkAddress = paramRouteInfo.getDestinationLinkAddress();
    localCommand.appendArg(paramRouteInfo.getInterface());
    localCommand.appendArg(localLinkAddress.getAddress().getHostAddress() + "/" + localLinkAddress.getPrefixLength());
    if (paramRouteInfo.hasGateway()) {
      localCommand.appendArg(paramRouteInfo.getGateway().getHostAddress());
    }
    try
    {
      this.mConnector.execute(localCommand);
      return;
    }
    catch (NativeDaemonConnectorException paramRouteInfo)
    {
      throw paramRouteInfo.rethrowAsParcelableException();
    }
  }
  
  public void addRoute(int paramInt, RouteInfo paramRouteInfo)
  {
    modifyRoute("add", "" + paramInt, paramRouteInfo);
  }
  
  public void addVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    Object[] arrayOfObject = new Object[13];
    arrayOfObject[0] = "users";
    arrayOfObject[1] = "add";
    arrayOfObject[2] = Integer.valueOf(paramInt);
    paramInt = 3;
    int i = 0;
    if (i < paramArrayOfUidRange.length)
    {
      int j = paramInt + 1;
      arrayOfObject[paramInt] = paramArrayOfUidRange[i].toString();
      if ((i == paramArrayOfUidRange.length - 1) || (j == arrayOfObject.length)) {}
      for (;;)
      {
        try
        {
          this.mConnector.execute("network", Arrays.copyOf(arrayOfObject, j));
          paramInt = 3;
          i += 1;
        }
        catch (NativeDaemonConnectorException paramArrayOfUidRange)
        {
          throw paramArrayOfUidRange.rethrowAsParcelableException();
        }
        paramInt = j;
      }
    }
  }
  
  public void allowProtect(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "protect", "allow", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void attachPppd(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("pppd", new Object[] { "attach", paramString1, NetworkUtils.numericToInetAddress(paramString2).getHostAddress(), NetworkUtils.numericToInetAddress(paramString3).getHostAddress(), NetworkUtils.numericToInetAddress(paramString4).getHostAddress(), NetworkUtils.numericToInetAddress(paramString5).getHostAddress() });
      return;
    }
    catch (NativeDaemonConnectorException paramString1)
    {
      throw paramString1.rethrowAsParcelableException();
    }
  }
  
  public void blackListWifiDevice(String paramString1, boolean paramBoolean, String paramString2)
  {
    int i = 1;
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        executeOrLogWithMessage("softap", new Object[] { "blacklist", paramString1, Integer.toString(i), paramString2 }, 214, "Ok", "blackListWifiDevice Error black wifi devcie");
        return;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        throw paramString1.rethrowAsParcelableException();
      }
      i = 0;
    }
  }
  
  public void clearDefaultNetId()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "default", "clear" });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void clearInterfaceAddresses(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("interface", new Object[] { "clearaddrs", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void clearPermission(int[] paramArrayOfInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    Object[] arrayOfObject = new Object[13];
    arrayOfObject[0] = "permission";
    arrayOfObject[1] = "user";
    arrayOfObject[2] = "clear";
    int i = 3;
    int j = 0;
    if (j < paramArrayOfInt.length)
    {
      int k = i + 1;
      arrayOfObject[i] = Integer.valueOf(paramArrayOfInt[j]);
      if ((j == paramArrayOfInt.length - 1) || (k == arrayOfObject.length)) {}
      for (;;)
      {
        try
        {
          this.mConnector.execute("network", Arrays.copyOf(arrayOfObject, k));
          i = 3;
          j += 1;
        }
        catch (NativeDaemonConnectorException paramArrayOfInt)
        {
          throw paramArrayOfInt.rethrowAsParcelableException();
        }
        i = k;
      }
    }
  }
  
  public void createPhysicalNetwork(int paramInt, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramString != null) {}
    try
    {
      this.mConnector.execute("network", new Object[] { "create", Integer.valueOf(paramInt), paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
    this.mConnector.execute("network", new Object[] { "create", Integer.valueOf(paramInt) });
  }
  
  public void createSoftApInterface(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("softap", new Object[] { "create", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void createVirtualNetwork(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    String str3;
    try
    {
      localNativeDaemonConnector = this.mConnector;
      if (!paramBoolean1) {
        break label101;
      }
      str1 = "1";
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      NativeDaemonConnector localNativeDaemonConnector;
      String str1;
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
    localNativeDaemonConnector.execute("network", new Object[] { "create", Integer.valueOf(paramInt), "vpn", str1, str3 });
    return;
    label101:
    label107:
    for (;;)
    {
      str3 = "0";
      break;
      for (;;)
      {
        if (!paramBoolean2) {
          break label107;
        }
        str3 = "1";
        break;
        String str2 = "0";
      }
    }
  }
  
  public void deleteSoftApInterface(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("softap", new Object[] { "remove", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void denyProtect(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "protect", "deny", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void detachPppd(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("pppd", new Object[] { "detach", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void disableIpv6(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("interface", new Object[] { "ipv6", paramString, "disable" });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void disableNat(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      modifyNat("disable", paramString1, paramString2);
      NetPluginDelegate.natStopped(paramString1, paramString2);
      return;
    }
    catch (SocketException paramString1)
    {
      throw new IllegalStateException(paramString1);
    }
  }
  
  public void doOemMyftmCommand(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    paramString = new StringTokenizer(paramString);
    Object[] arrayOfObject = new Object[paramString.countTokens() + 1];
    arrayOfObject[0] = "myftm";
    int i = 0;
    while (paramString.hasMoreTokens())
    {
      arrayOfObject[(i + 1)] = paramString.nextToken();
      i += 1;
    }
    try
    {
      executeOrLogWithMessage("softap", arrayOfObject, 214, "Ok", "doOemMyftmcommand request");
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "NetworkManagement");
    paramPrintWriter.println("NetworkManagementService NativeDaemonConnector Log:");
    this.mConnector.dump(???, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.println();
    paramPrintWriter.print("Bandwidth control enabled: ");
    paramPrintWriter.println(this.mBandwidthControlEnabled);
    paramPrintWriter.print("mMobileActivityFromRadio=");
    paramPrintWriter.print(this.mMobileActivityFromRadio);
    paramPrintWriter.print(" mLastPowerStateFromRadio=");
    paramPrintWriter.println(this.mLastPowerStateFromRadio);
    paramPrintWriter.print("mNetworkActive=");
    paramPrintWriter.println(this.mNetworkActive);
    synchronized (this.mQuotaLock)
    {
      paramPrintWriter.print("Active quota ifaces: ");
      paramPrintWriter.println(this.mActiveQuotas.toString());
      paramPrintWriter.print("Active alert ifaces: ");
      paramPrintWriter.println(this.mActiveAlerts.toString());
      paramPrintWriter.print("Data saver mode: ");
      paramPrintWriter.println(this.mDataSaverMode);
      dumpUidRuleOnQuotaLocked(paramPrintWriter, "blacklist", this.mUidRejectOnMetered);
      dumpUidRuleOnQuotaLocked(paramPrintWriter, "whitelist", this.mUidAllowOnMetered);
      synchronized (this.mUidFirewallRules)
      {
        dumpUidFirewallRule(paramPrintWriter, "", this.mUidFirewallRules);
        paramPrintWriter.print("UID firewall standby chain enabled: ");
        paramPrintWriter.println(this.mFirewallChainStates.get(2));
        synchronized (this.mUidFirewallStandbyRules)
        {
          dumpUidFirewallRule(paramPrintWriter, "standby", this.mUidFirewallStandbyRules);
          paramPrintWriter.print("UID firewall dozable chain enabled: ");
          paramPrintWriter.println(this.mFirewallChainStates.get(1));
          synchronized (this.mUidFirewallDozableRules)
          {
            dumpUidFirewallRule(paramPrintWriter, "dozable", this.mUidFirewallDozableRules);
            paramPrintWriter.println("UID firewall powersave chain enabled: " + this.mFirewallChainStates.get(3));
          }
        }
      }
    }
    synchronized (this.mUidFirewallPowerSaveRules)
    {
      dumpUidFirewallRule(paramPrintWriter, "powersave", this.mUidFirewallPowerSaveRules);
      synchronized (this.mIdleTimerLock)
      {
        paramPrintWriter.println("Idle timers:");
        paramArrayOfString = this.mActiveIdleTimers.entrySet().iterator();
        if (!paramArrayOfString.hasNext()) {
          break label498;
        }
        Object localObject = (Map.Entry)paramArrayOfString.next();
        paramPrintWriter.print("  ");
        paramPrintWriter.print((String)((Map.Entry)localObject).getKey());
        paramPrintWriter.println(":");
        localObject = (IdleTimerParams)((Map.Entry)localObject).getValue();
        paramPrintWriter.print("    timeout=");
        paramPrintWriter.print(((IdleTimerParams)localObject).timeout);
        paramPrintWriter.print(" type=");
        paramPrintWriter.print(((IdleTimerParams)localObject).type);
        paramPrintWriter.print(" networkCount=");
        paramPrintWriter.println(((IdleTimerParams)localObject).networkCount);
      }
      paramPrintWriter = finally;
      throw paramPrintWriter;
      paramPrintWriter = finally;
      throw paramPrintWriter;
      paramPrintWriter = finally;
      throw paramPrintWriter;
      paramPrintWriter = finally;
      throw paramPrintWriter;
    }
    label498:
    paramPrintWriter.print("Firewall enabled: ");
    paramPrintWriter.println(this.mFirewallEnabled);
    paramPrintWriter.print("Netd service status: ");
    if (this.mNetdService == null)
    {
      paramPrintWriter.println("disconnected");
      return;
    }
    for (;;)
    {
      try
      {
        if (this.mNetdService.isAlive())
        {
          ??? = "alive";
          paramPrintWriter.println(???);
          return;
        }
      }
      catch (RemoteException ???)
      {
        paramPrintWriter.println("unreachable");
        return;
      }
      ??? = "dead";
    }
  }
  
  public void enableIpv6(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("interface", new Object[] { "ipv6", paramString, "enable" });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void enableNat(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      modifyNat("enable", paramString1, paramString2);
      NetPluginDelegate.natStarted(paramString1, paramString2);
      return;
    }
    catch (SocketException paramString1)
    {
      throw new IllegalStateException(paramString1);
    }
  }
  
  public String[] getDnsForwarders()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("tether", new Object[] { "dns", "list" }), 112);
      return arrayOfString;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public String getFirewallChainName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown chain:" + paramInt);
    case 2: 
      return "standby";
    case 1: 
      return "dozable";
    case 3: 
      return "powersave";
    }
    return "none";
  }
  
  public InterfaceConfiguration getInterfaceConfig(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    InterfaceConfiguration localInterfaceConfiguration;
    try
    {
      NativeDaemonEvent localNativeDaemonEvent = this.mConnector.execute("interface", new Object[] { "getcfg", paramString });
      localNativeDaemonEvent.checkCode(213);
      StringTokenizer localStringTokenizer = new StringTokenizer(localNativeDaemonEvent.getMessage());
      try
      {
        localInterfaceConfiguration = new InterfaceConfiguration();
        localInterfaceConfiguration.setHardwareAddress(localStringTokenizer.nextToken(" "));
        paramString = null;
        i = 0;
      }
      catch (NoSuchElementException paramString)
      {
        int i;
        InetAddress localInetAddress;
        label99:
        int j;
        label110:
        throw new IllegalStateException("Invalid response from daemon: " + localNativeDaemonEvent);
      }
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
    try
    {
      localInetAddress = NetworkUtils.numericToInetAddress(localStringTokenizer.nextToken());
      paramString = localInetAddress;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Slog.e("NetworkManagement", "Failed to parse ipaddr", localIllegalArgumentException);
      break label99;
    }
    try
    {
      j = Integer.parseInt(localStringTokenizer.nextToken());
      i = j;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Slog.e("NetworkManagement", "Failed to parse prefixLength", localNumberFormatException);
      break label110;
    }
    localInterfaceConfiguration.setLinkAddress(new LinkAddress(paramString, i));
    while (localStringTokenizer.hasMoreTokens()) {
      localInterfaceConfiguration.setFlag(localStringTokenizer.nextToken());
    }
    return localInterfaceConfiguration;
  }
  
  public boolean getIpForwardingEnabled()
    throws IllegalStateException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NativeDaemonEvent localNativeDaemonEvent = this.mConnector.execute("ipfwd", new Object[] { "status" });
      localNativeDaemonEvent.checkCode(211);
      return localNativeDaemonEvent.getMessage().endsWith("enabled");
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public INetd getNetdService()
    throws RemoteException
  {
    CountDownLatch localCountDownLatch = this.mConnectedSignal;
    if (localCountDownLatch != null) {}
    try
    {
      localCountDownLatch.await();
      return this.mNetdService;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  public NetworkStats getNetworkStatsDetail()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NetworkStats localNetworkStats = this.mStatsFactory.readNetworkStatsDetail(-1, null, -1, null);
      return localNetworkStats;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  public NetworkStats getNetworkStatsSummaryDev()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NetworkStats localNetworkStats = this.mStatsFactory.readNetworkStatsSummaryDev();
      return localNetworkStats;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  public NetworkStats getNetworkStatsSummaryXt()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NetworkStats localNetworkStats = this.mStatsFactory.readNetworkStatsSummaryXt();
      return localNetworkStats;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  /* Error */
  public NetworkStats getNetworkStatsTethering()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 133	com/android/server/NetworkManagementService:mContext	Landroid/content/Context;
    //   4: ldc_w 617
    //   7: ldc 63
    //   9: invokevirtual 622	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: new 1322	android/net/NetworkStats
    //   15: dup
    //   16: invokestatic 1327	android/os/SystemClock:elapsedRealtime	()J
    //   19: iconst_1
    //   20: invokespecial 1330	android/net/NetworkStats:<init>	(JI)V
    //   23: astore 4
    //   25: aload_0
    //   26: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   29: ldc_w 801
    //   32: iconst_1
    //   33: anewarray 237	java/lang/Object
    //   36: dup
    //   37: iconst_0
    //   38: ldc_w 1332
    //   41: aastore
    //   42: invokevirtual 1243	com/android/server/NativeDaemonConnector:executeForList	(Ljava/lang/String;[Ljava/lang/Object;)[Lcom/android/server/NativeDaemonEvent;
    //   45: astore 5
    //   47: iconst_0
    //   48: istore_1
    //   49: aload 5
    //   51: arraylength
    //   52: istore_2
    //   53: iload_1
    //   54: iload_2
    //   55: if_icmpge +210 -> 265
    //   58: aload 5
    //   60: iload_1
    //   61: aaload
    //   62: astore_3
    //   63: aload_3
    //   64: invokevirtual 542	com/android/server/NativeDaemonEvent:getCode	()I
    //   67: bipush 114
    //   69: if_icmpeq +6 -> 75
    //   72: goto +196 -> 268
    //   75: new 1134	java/util/StringTokenizer
    //   78: dup
    //   79: aload_3
    //   80: invokevirtual 545	com/android/server/NativeDaemonEvent:getMessage	()Ljava/lang/String;
    //   83: invokespecial 1135	java/util/StringTokenizer:<init>	(Ljava/lang/String;)V
    //   86: astore 6
    //   88: aload 6
    //   90: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   93: pop
    //   94: aload 6
    //   96: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   99: astore 7
    //   101: new 1334	android/net/NetworkStats$Entry
    //   104: dup
    //   105: invokespecial 1335	android/net/NetworkStats$Entry:<init>	()V
    //   108: astore 8
    //   110: aload 8
    //   112: aload 7
    //   114: putfield 1338	android/net/NetworkStats$Entry:iface	Ljava/lang/String;
    //   117: aload 8
    //   119: bipush -5
    //   121: putfield 1341	android/net/NetworkStats$Entry:uid	I
    //   124: aload 8
    //   126: iconst_0
    //   127: putfield 1343	android/net/NetworkStats$Entry:set	I
    //   130: aload 8
    //   132: iconst_0
    //   133: putfield 1346	android/net/NetworkStats$Entry:tag	I
    //   136: aload 8
    //   138: aload 6
    //   140: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   143: invokestatic 1350	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   146: putfield 1354	android/net/NetworkStats$Entry:rxBytes	J
    //   149: aload 8
    //   151: aload 6
    //   153: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   156: invokestatic 1350	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   159: putfield 1357	android/net/NetworkStats$Entry:rxPackets	J
    //   162: aload 8
    //   164: aload 6
    //   166: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   169: invokestatic 1350	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   172: putfield 1360	android/net/NetworkStats$Entry:txBytes	J
    //   175: aload 8
    //   177: aload 6
    //   179: invokevirtual 1146	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   182: invokestatic 1350	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   185: putfield 1363	android/net/NetworkStats$Entry:txPackets	J
    //   188: aload 4
    //   190: aload 8
    //   192: invokevirtual 1367	android/net/NetworkStats:combineValues	(Landroid/net/NetworkStats$Entry;)Landroid/net/NetworkStats;
    //   195: pop
    //   196: goto +72 -> 268
    //   199: astore 4
    //   201: new 1128	java/lang/IllegalStateException
    //   204: dup
    //   205: new 381	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 1369
    //   215: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_3
    //   219: invokevirtual 393	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   222: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokespecial 1288	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   228: athrow
    //   229: astore_3
    //   230: aload_3
    //   231: invokevirtual 613	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   234: athrow
    //   235: astore 4
    //   237: new 1128	java/lang/IllegalStateException
    //   240: dup
    //   241: new 381	java/lang/StringBuilder
    //   244: dup
    //   245: invokespecial 382	java/lang/StringBuilder:<init>	()V
    //   248: ldc_w 1369
    //   251: invokevirtual 388	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: aload_3
    //   255: invokevirtual 393	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   258: invokevirtual 397	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   261: invokespecial 1288	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   264: athrow
    //   265: aload 4
    //   267: areturn
    //   268: iload_1
    //   269: iconst_1
    //   270: iadd
    //   271: istore_1
    //   272: goto -219 -> 53
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	275	0	this	NetworkManagementService
    //   48	224	1	i	int
    //   52	4	2	j	int
    //   62	157	3	localNativeDaemonEvent	NativeDaemonEvent
    //   229	26	3	localNativeDaemonConnectorException	NativeDaemonConnectorException
    //   23	166	4	localNetworkStats	NetworkStats
    //   199	1	4	localNoSuchElementException	NoSuchElementException
    //   235	31	4	localNumberFormatException	NumberFormatException
    //   45	14	5	arrayOfNativeDaemonEvent	NativeDaemonEvent[]
    //   86	92	6	localStringTokenizer	StringTokenizer
    //   99	14	7	str	String
    //   108	83	8	localEntry	android.net.NetworkStats.Entry
    // Exception table:
    //   from	to	target	type
    //   88	196	199	java/util/NoSuchElementException
    //   25	47	229	com/android/server/NativeDaemonConnectorException
    //   49	53	229	com/android/server/NativeDaemonConnectorException
    //   63	72	229	com/android/server/NativeDaemonConnectorException
    //   75	88	229	com/android/server/NativeDaemonConnectorException
    //   88	196	229	com/android/server/NativeDaemonConnectorException
    //   201	229	229	com/android/server/NativeDaemonConnectorException
    //   237	265	229	com/android/server/NativeDaemonConnectorException
    //   88	196	235	java/lang/NumberFormatException
  }
  
  public NetworkStats getNetworkStatsUidDetail(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NetworkStats localNetworkStats = this.mStatsFactory.readNetworkStatsDetail(paramInt, null, -1, null);
      return localNetworkStats;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  public boolean isBandwidthControlEnabled()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    return this.mBandwidthControlEnabled;
  }
  
  public boolean isClatdStarted(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      paramString = this.mConnector.execute("clatd", new Object[] { "status", paramString });
      paramString.checkCode(223);
      return paramString.getMessage().endsWith("started");
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public boolean isFirewallEnabled()
  {
    enforceSystemUid();
    return this.mFirewallEnabled;
  }
  
  public boolean isNetworkActive()
  {
    synchronized (this.mNetworkActivityListeners)
    {
      if (!this.mNetworkActive)
      {
        bool = this.mActiveIdleTimers.isEmpty();
        return bool;
      }
      boolean bool = true;
    }
  }
  
  public boolean isTetheringStarted()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      NativeDaemonEvent localNativeDaemonEvent = this.mConnector.execute("tether", new Object[] { "status" });
      localNativeDaemonEvent.checkCode(210);
      return localNativeDaemonEvent.getMessage().endsWith("started");
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public String[] listInterfaces()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("interface", new Object[] { "list" }), 110);
      return arrayOfString;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public String[] listTetheredInterfaces()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("tether", new Object[] { "interface", "list" }), 111);
      return arrayOfString;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public String[] listTtys()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      String[] arrayOfString = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("list_ttys", new Object[0]), 113);
      return arrayOfString;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void monitor()
  {
    if (this.mConnector != null) {
      this.mConnector.monitor();
    }
  }
  
  public void registerNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
  {
    this.mNetworkActivityListeners.register(paramINetworkActivityListener);
  }
  
  public void registerObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    this.mObservers.register(paramINetworkManagementEventObserver);
  }
  
  public void removeIdleTimer(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (DBG) {
      Slog.d("NetworkManagement", "Removing idletimer");
    }
    synchronized (this.mIdleTimerLock)
    {
      final IdleTimerParams localIdleTimerParams = (IdleTimerParams)this.mActiveIdleTimers.get(paramString);
      if (localIdleTimerParams != null)
      {
        int i = localIdleTimerParams.networkCount - 1;
        localIdleTimerParams.networkCount = i;
        if (i <= 0) {}
      }
      else
      {
        return;
      }
      try
      {
        this.mConnector.execute("idletimer", new Object[] { "remove", paramString, Integer.toString(localIdleTimerParams.timeout), Integer.toString(localIdleTimerParams.type) });
        this.mActiveIdleTimers.remove(paramString);
        this.mDaemonHandler.post(new Runnable()
        {
          public void run()
          {
            NetworkManagementService.-wrap4(NetworkManagementService.this, localIdleTimerParams.type, 1, SystemClock.elapsedRealtimeNanos(), -1, false);
          }
        });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void removeInterfaceAlert(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    synchronized (this.mQuotaLock)
    {
      boolean bool = this.mActiveAlerts.containsKey(paramString);
      if (!bool) {
        return;
      }
      try
      {
        this.mConnector.execute("bandwidth", new Object[] { "removeinterfacealert", paramString });
        this.mActiveAlerts.remove(paramString);
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void removeInterfaceFromLocalNetwork(String paramString)
  {
    modifyInterfaceInNetwork("remove", "local", paramString);
  }
  
  public void removeInterfaceFromNetwork(String paramString, int paramInt)
  {
    modifyInterfaceInNetwork("remove", "" + paramInt, paramString);
  }
  
  public void removeInterfaceQuota(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    synchronized (this.mQuotaLock)
    {
      boolean bool = this.mActiveQuotas.containsKey(paramString);
      if (!bool) {
        return;
      }
      this.mActiveQuotas.remove(paramString);
      this.mActiveAlerts.remove(paramString);
      try
      {
        this.mConnector.execute("bandwidth", new Object[] { "removeiquota", paramString });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void removeNetwork(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "destroy", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void removeRoute(int paramInt, RouteInfo paramRouteInfo)
  {
    modifyRoute("remove", "" + paramInt, paramRouteInfo);
  }
  
  public int removeRoutesFromLocalNetwork(List<RouteInfo> paramList)
  {
    int i = 0;
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      RouteInfo localRouteInfo = (RouteInfo)paramList.next();
      try
      {
        modifyRoute("remove", "local", localRouteInfo);
      }
      catch (IllegalStateException localIllegalStateException)
      {
        i += 1;
      }
    }
    return i;
  }
  
  public void removeVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    Object[] arrayOfObject = new Object[13];
    arrayOfObject[0] = "users";
    arrayOfObject[1] = "remove";
    arrayOfObject[2] = Integer.valueOf(paramInt);
    paramInt = 3;
    int i = 0;
    if (i < paramArrayOfUidRange.length)
    {
      int j = paramInt + 1;
      arrayOfObject[paramInt] = paramArrayOfUidRange[i].toString();
      if ((i == paramArrayOfUidRange.length - 1) || (j == arrayOfObject.length)) {}
      for (;;)
      {
        try
        {
          this.mConnector.execute("network", Arrays.copyOf(arrayOfObject, j));
          paramInt = 3;
          i += 1;
        }
        catch (NativeDaemonConnectorException paramArrayOfUidRange)
        {
          throw paramArrayOfUidRange.rethrowAsParcelableException();
        }
        paramInt = j;
      }
    }
  }
  
  public void setAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramWifiConfiguration == null) {}
    for (;;)
    {
      try
      {
        paramWifiConfiguration = new Object[2];
        paramWifiConfiguration[0] = "set";
        paramWifiConfiguration[1] = paramString;
        executeOrLogWithMessage("softap", paramWifiConfiguration, 214, "Ok", "startAccessPoint Error setting up softap");
        return;
      }
      catch (NativeDaemonConnectorException paramWifiConfiguration)
      {
        Object[] arrayOfObject;
        throw paramWifiConfiguration.rethrowAsParcelableException();
      }
      arrayOfObject = new Object[7];
      arrayOfObject[0] = "set";
      arrayOfObject[1] = paramString;
      arrayOfObject[2] = paramWifiConfiguration.SSID;
      arrayOfObject[3] = "broadcast";
      arrayOfObject[4] = "6";
      arrayOfObject[5] = getSecurityType(paramWifiConfiguration);
      arrayOfObject[6] = new NativeDaemonConnector.SensitiveArg(paramWifiConfiguration.preSharedKey);
      paramWifiConfiguration = arrayOfObject;
    }
  }
  
  public void setAllowOnlyVpnForUids(boolean paramBoolean, UidRange[] paramArrayOfUidRange)
    throws ServiceSpecificException
  {
    try
    {
      this.mNetdService.networkRejectNonSecureVpn(paramBoolean, paramArrayOfUidRange);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("NetworkManagement", "setAllowOnlyVpnForUids(" + paramBoolean + ", " + Arrays.toString(paramArrayOfUidRange) + ")" + ": netd command failed", localRemoteException);
      throw localRemoteException.rethrowAsRuntimeException();
    }
    catch (ServiceSpecificException localServiceSpecificException)
    {
      Log.w("NetworkManagement", "setAllowOnlyVpnForUids(" + paramBoolean + ", " + Arrays.toString(paramArrayOfUidRange) + ")" + ": netd command failed", localServiceSpecificException);
      throw localServiceSpecificException;
    }
  }
  
  public boolean setDataSaverModeEnabled(boolean paramBoolean)
  {
    if (DBG) {
      Log.d("NetworkManagement", "setDataSaverMode: " + paramBoolean);
    }
    synchronized (this.mQuotaLock)
    {
      if (this.mDataSaverMode == paramBoolean)
      {
        Log.w("NetworkManagement", "setDataSaverMode(): already " + this.mDataSaverMode);
        return true;
      }
      try
      {
        boolean bool = this.mNetdService.bandwidthEnableDataSaver(paramBoolean);
        if (bool) {
          this.mDataSaverMode = paramBoolean;
        }
        for (;;)
        {
          return bool;
          Log.w("NetworkManagement", "setDataSaverMode(" + paramBoolean + "): netd command silently failed");
        }
        localObject2 = finally;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("NetworkManagement", "setDataSaverMode(" + paramBoolean + "): netd command failed", localRemoteException);
        return false;
      }
    }
  }
  
  public void setDefaultNetId(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("network", new Object[] { "default", "set", Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void setDnsConfigurationForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int j = Settings.Global.getInt(localContentResolver, "dns_resolver_sample_validity_seconds", 1800);
    int i;
    if (j >= 0)
    {
      i = j;
      if (j <= 65535) {}
    }
    else
    {
      Slog.w("NetworkManagement", "Invalid sampleValidity=" + j + ", using default=" + 1800);
      i = 1800;
    }
    int k = Settings.Global.getInt(localContentResolver, "dns_resolver_success_threshold_percent", 25);
    if (k >= 0)
    {
      j = k;
      if (k <= 100) {}
    }
    else
    {
      Slog.w("NetworkManagement", "Invalid successThreshold=" + k + ", using default=" + 25);
      j = 25;
    }
    int n = Settings.Global.getInt(localContentResolver, "dns_resolver_min_samples", 8);
    int i1 = Settings.Global.getInt(localContentResolver, "dns_resolver_max_samples", 64);
    int m;
    if ((n < 0) || (n > i1))
    {
      Slog.w("NetworkManagement", "Invalid sample count (min, max)=(" + n + ", " + i1 + "), using default=(" + 8 + ", " + 64 + ")");
      k = 8;
      m = 64;
      label275:
      if (paramString != null) {
        break label338;
      }
    }
    for (paramString = new String[0];; paramString = paramString.split(" "))
    {
      try
      {
        this.mNetdService.setResolverConfiguration(paramInt, paramArrayOfString, paramString, new int[] { i, j, k, m });
        return;
      }
      catch (RemoteException paramArrayOfString)
      {
        label338:
        throw new RuntimeException(paramArrayOfString);
      }
      m = i1;
      k = n;
      if (i1 <= 64) {
        break label275;
      }
      break;
    }
  }
  
  public void setDnsForwarders(Network paramNetwork, String[] paramArrayOfString)
  {
    int j = 0;
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramNetwork != null) {}
    for (int i = paramNetwork.netId;; i = 0)
    {
      paramNetwork = new NativeDaemonConnector.Command("tether", new Object[] { "dns", "set", Integer.valueOf(i) });
      int k = paramArrayOfString.length;
      i = j;
      while (i < k)
      {
        paramNetwork.appendArg(NetworkUtils.numericToInetAddress(paramArrayOfString[i]).getHostAddress());
        i += 1;
      }
    }
    try
    {
      this.mConnector.execute(paramNetwork);
      return;
    }
    catch (NativeDaemonConnectorException paramNetwork)
    {
      throw paramNetwork.rethrowAsParcelableException();
    }
  }
  
  public void setDnsServersForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
  {
    int i = 0;
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramArrayOfString.length > 0)
    {
      Object localObject = paramString;
      if (paramString == null) {
        localObject = "";
      }
      localObject = new NativeDaemonConnector.Command("resolver", new Object[] { "setnetdns", Integer.valueOf(paramInt), localObject });
      int j = paramArrayOfString.length;
      paramInt = i;
      for (;;)
      {
        paramString = (String)localObject;
        if (paramInt >= j) {
          break;
        }
        paramString = NetworkUtils.numericToInetAddress(paramArrayOfString[paramInt]);
        if (!paramString.isAnyLocalAddress()) {
          ((NativeDaemonConnector.Command)localObject).appendArg(paramString.getHostAddress());
        }
        paramInt += 1;
      }
    }
    paramString = new NativeDaemonConnector.Command("resolver", new Object[] { "clearnetdns", Integer.valueOf(paramInt) });
    try
    {
      this.mConnector.execute(paramString);
      return;
    }
    catch (NativeDaemonConnectorException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowAsParcelableException();
    }
  }
  
  public void setFirewallChainEnabled(int paramInt, boolean paramBoolean)
  {
    
    synchronized (this.mQuotaLock)
    {
      boolean bool = this.mFirewallChainStates.get(paramInt);
      if (bool == paramBoolean) {
        return;
      }
      this.mFirewallChainStates.put(paramInt, paramBoolean);
      if (paramBoolean)
      {
        str3 = "enable_chain";
        break label189;
        throw new IllegalArgumentException("Bad child chain: " + paramInt);
      }
    }
    String str3 = "disable_chain";
    break label189;
    String str1 = "standby";
    for (;;)
    {
      try
      {
        this.mConnector.execute("firewall", new Object[] { str3, str1 });
        if (paramBoolean)
        {
          if (DBG) {
            Slog.d("NetworkManagement", "Closing sockets after enabling chain " + str1);
          }
          closeSocketsForFirewallChainLocked(paramInt, str1);
        }
        return;
      }
      catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
      {
        throw localNativeDaemonConnectorException.rethrowAsParcelableException();
      }
      str1 = "dozable";
      continue;
      switch (paramInt)
      {
      case 2: 
      case 1: 
      default: 
        break;
      case 3: 
        label189:
        String str2 = "powersave";
      }
    }
  }
  
  public void setFirewallEgressDestRule(String paramString, int paramInt, boolean paramBoolean)
  {
    enforceSystemUid();
    Preconditions.checkState(this.mFirewallEnabled);
    if (paramBoolean) {}
    for (String str = "allow";; str = "deny") {
      try
      {
        this.mConnector.execute("firewall", new Object[] { "set_egress_dest_rule", paramString, Integer.valueOf(paramInt), str });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void setFirewallEgressSourceRule(String paramString, boolean paramBoolean)
  {
    enforceSystemUid();
    Preconditions.checkState(this.mFirewallEnabled);
    if (paramBoolean) {}
    for (String str = "allow";; str = "deny") {
      try
      {
        this.mConnector.execute("firewall", new Object[] { "set_egress_source_rule", paramString, str });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  /* Error */
  public void setFirewallEnabled(boolean paramBoolean)
  {
    // Byte code:
    //   0: invokestatic 1379	com/android/server/NetworkManagementService:enforceSystemUid	()V
    //   3: aload_0
    //   4: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   7: astore_3
    //   8: iload_1
    //   9: ifeq +35 -> 44
    //   12: ldc_w 1183
    //   15: astore_2
    //   16: aload_3
    //   17: ldc_w 959
    //   20: iconst_2
    //   21: anewarray 237	java/lang/Object
    //   24: dup
    //   25: iconst_0
    //   26: ldc_w 803
    //   29: aastore
    //   30: dup
    //   31: iconst_1
    //   32: aload_2
    //   33: aastore
    //   34: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   37: pop
    //   38: aload_0
    //   39: iload_1
    //   40: putfield 889	com/android/server/NetworkManagementService:mFirewallEnabled	Z
    //   43: return
    //   44: ldc_w 1084
    //   47: astore_2
    //   48: goto -32 -> 16
    //   51: astore_2
    //   52: aload_2
    //   53: invokevirtual 613	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	NetworkManagementService
    //   0	57	1	paramBoolean	boolean
    //   15	33	2	str	String
    //   51	2	2	localNativeDaemonConnectorException	NativeDaemonConnectorException
    //   7	10	3	localNativeDaemonConnector	NativeDaemonConnector
    // Exception table:
    //   from	to	target	type
    //   3	8	51	com/android/server/NativeDaemonConnectorException
    //   16	43	51	com/android/server/NativeDaemonConnectorException
  }
  
  public void setFirewallInterfaceRule(String paramString, boolean paramBoolean)
  {
    enforceSystemUid();
    Preconditions.checkState(this.mFirewallEnabled);
    if (paramBoolean) {}
    for (String str = "allow";; str = "deny") {
      try
      {
        this.mConnector.execute("firewall", new Object[] { "set_interface_rule", paramString, str });
        return;
      }
      catch (NativeDaemonConnectorException paramString)
      {
        throw paramString.rethrowAsParcelableException();
      }
    }
  }
  
  public void setFirewallUidRule(int paramInt1, int paramInt2, int paramInt3)
  {
    
    synchronized (this.mQuotaLock)
    {
      setFirewallUidRuleLocked(paramInt1, paramInt2, paramInt3);
      return;
    }
  }
  
  public void setFirewallUidRules(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    
    for (;;)
    {
      int i;
      synchronized (this.mQuotaLock)
      {
        SparseIntArray localSparseIntArray1 = getUidFirewallRules(paramInt);
        SparseIntArray localSparseIntArray2 = new SparseIntArray();
        i = paramArrayOfInt1.length - 1;
        int j;
        if (i >= 0)
        {
          j = paramArrayOfInt1[i];
          int k = paramArrayOfInt2[i];
          updateFirewallUidRuleLocked(paramInt, j, k);
          localSparseIntArray2.put(j, k);
          i -= 1;
          continue;
        }
        paramArrayOfInt2 = new SparseIntArray();
        i = localSparseIntArray1.size() - 1;
        if (i >= 0)
        {
          j = localSparseIntArray1.keyAt(i);
          if (localSparseIntArray2.indexOfKey(j) >= 0) {
            break label313;
          }
          paramArrayOfInt2.put(j, 0);
          break label313;
        }
        i = paramArrayOfInt2.size() - 1;
        if (i >= 0)
        {
          updateFirewallUidRuleLocked(paramInt, paramArrayOfInt2.keyAt(i), 0);
          i -= 1;
          continue;
        }
        switch (paramInt)
        {
        default: 
        case 1: 
          try
          {
            Slog.d("NetworkManagement", "setFirewallUidRules() called on invalid chain: " + paramInt);
            return;
            this.mNetdService.firewallReplaceUidChain("fw_dozable", true, paramArrayOfInt1);
            continue;
          }
          catch (RemoteException paramArrayOfInt1)
          {
            Slog.w("NetworkManagement", "Error flushing firewall chain " + paramInt, paramArrayOfInt1);
            continue;
          }
        }
      }
      this.mNetdService.firewallReplaceUidChain("fw_standby", false, paramArrayOfInt1);
      continue;
      this.mNetdService.firewallReplaceUidChain("fw_powersave", true, paramArrayOfInt1);
      continue;
      label313:
      i -= 1;
    }
  }
  
  public void setGlobalAlert(long paramLong)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    try
    {
      this.mConnector.execute("bandwidth", new Object[] { "setglobalalert", Long.valueOf(paramLong) });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void setInterfaceAlert(String paramString, long paramLong)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    if (!this.mActiveQuotas.containsKey(paramString)) {
      throw new IllegalStateException("setting alert requires existing quota on iface");
    }
    synchronized (this.mQuotaLock)
    {
      if (this.mActiveAlerts.containsKey(paramString)) {
        throw new IllegalStateException("iface " + paramString + " already has alert");
      }
    }
    try
    {
      this.mConnector.execute("bandwidth", new Object[] { "setinterfacealert", paramString, Long.valueOf(paramLong) });
      this.mActiveAlerts.put(paramString, Long.valueOf(paramLong));
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void setInterfaceConfig(String paramString, InterfaceConfiguration paramInterfaceConfiguration)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    LinkAddress localLinkAddress = paramInterfaceConfiguration.getLinkAddress();
    if ((localLinkAddress == null) || (localLinkAddress.getAddress() == null)) {
      throw new IllegalStateException("Null LinkAddress given");
    }
    paramString = new NativeDaemonConnector.Command("interface", new Object[] { "setcfg", paramString, localLinkAddress.getAddress().getHostAddress(), Integer.valueOf(localLinkAddress.getPrefixLength()) });
    paramInterfaceConfiguration = paramInterfaceConfiguration.getFlags().iterator();
    while (paramInterfaceConfiguration.hasNext()) {
      paramString.appendArg((String)paramInterfaceConfiguration.next());
    }
    try
    {
      this.mConnector.execute(paramString);
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void setInterfaceDown(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    InterfaceConfiguration localInterfaceConfiguration = getInterfaceConfig(paramString);
    localInterfaceConfiguration.setInterfaceDown();
    setInterfaceConfig(paramString, localInterfaceConfiguration);
  }
  
  /* Error */
  public void setInterfaceIpv6NdOffload(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 133	com/android/server/NetworkManagementService:mContext	Landroid/content/Context;
    //   4: ldc_w 617
    //   7: ldc 63
    //   9: invokevirtual 622	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   16: astore 4
    //   18: iload_2
    //   19: ifeq +35 -> 54
    //   22: ldc_w 803
    //   25: astore_3
    //   26: aload 4
    //   28: ldc_w 626
    //   31: iconst_3
    //   32: anewarray 237	java/lang/Object
    //   35: dup
    //   36: iconst_0
    //   37: ldc_w 1626
    //   40: aastore
    //   41: dup
    //   42: iconst_1
    //   43: aload_1
    //   44: aastore
    //   45: dup
    //   46: iconst_2
    //   47: aload_3
    //   48: aastore
    //   49: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   52: pop
    //   53: return
    //   54: ldc_w 1118
    //   57: astore_3
    //   58: goto -32 -> 26
    //   61: astore_1
    //   62: aload_1
    //   63: invokevirtual 613	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	NetworkManagementService
    //   0	67	1	paramString	String
    //   0	67	2	paramBoolean	boolean
    //   25	33	3	str	String
    //   16	11	4	localNativeDaemonConnector	NativeDaemonConnector
    // Exception table:
    //   from	to	target	type
    //   12	18	61	com/android/server/NativeDaemonConnectorException
    //   26	53	61	com/android/server/NativeDaemonConnectorException
  }
  
  /* Error */
  public void setInterfaceIpv6PrivacyExtensions(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 133	com/android/server/NetworkManagementService:mContext	Landroid/content/Context;
    //   4: ldc_w 617
    //   7: ldc 63
    //   9: invokevirtual 622	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   16: astore 4
    //   18: iload_2
    //   19: ifeq +35 -> 54
    //   22: ldc_w 803
    //   25: astore_3
    //   26: aload 4
    //   28: ldc_w 626
    //   31: iconst_3
    //   32: anewarray 237	java/lang/Object
    //   35: dup
    //   36: iconst_0
    //   37: ldc_w 1629
    //   40: aastore
    //   41: dup
    //   42: iconst_1
    //   43: aload_1
    //   44: aastore
    //   45: dup
    //   46: iconst_2
    //   47: aload_3
    //   48: aastore
    //   49: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   52: pop
    //   53: return
    //   54: ldc_w 1118
    //   57: astore_3
    //   58: goto -32 -> 26
    //   61: astore_1
    //   62: aload_1
    //   63: invokevirtual 613	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	NetworkManagementService
    //   0	67	1	paramString	String
    //   0	67	2	paramBoolean	boolean
    //   25	33	3	str	String
    //   16	11	4	localNativeDaemonConnector	NativeDaemonConnector
    // Exception table:
    //   from	to	target	type
    //   12	18	61	com/android/server/NativeDaemonConnectorException
    //   26	53	61	com/android/server/NativeDaemonConnectorException
  }
  
  public void setInterfaceQuota(String paramString, long paramLong)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (!this.mBandwidthControlEnabled) {
      return;
    }
    synchronized (this.mQuotaLock)
    {
      if (this.mActiveQuotas.containsKey(paramString)) {
        throw new IllegalStateException("iface " + paramString + " already has quota");
      }
    }
    try
    {
      this.mConnector.execute("bandwidth", new Object[] { "setiquota", paramString, Long.valueOf(paramLong) });
      this.mActiveQuotas.put(paramString, Long.valueOf(paramLong));
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void setInterfaceUp(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    InterfaceConfiguration localInterfaceConfiguration = getInterfaceConfig(paramString);
    localInterfaceConfiguration.setInterfaceUp();
    setInterfaceConfig(paramString, localInterfaceConfiguration);
  }
  
  /* Error */
  public void setIpForwardingEnabled(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 133	com/android/server/NetworkManagementService:mContext	Landroid/content/Context;
    //   4: ldc_w 617
    //   7: ldc 63
    //   9: invokevirtual 622	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 129	com/android/server/NetworkManagementService:mConnector	Lcom/android/server/NativeDaemonConnector;
    //   16: astore_3
    //   17: iload_1
    //   18: ifeq +30 -> 48
    //   21: ldc_w 803
    //   24: astore_2
    //   25: aload_3
    //   26: ldc_w 601
    //   29: iconst_2
    //   30: anewarray 237	java/lang/Object
    //   33: dup
    //   34: iconst_0
    //   35: aload_2
    //   36: aastore
    //   37: dup
    //   38: iconst_1
    //   39: ldc_w 1639
    //   42: aastore
    //   43: invokevirtual 537	com/android/server/NativeDaemonConnector:execute	(Ljava/lang/String;[Ljava/lang/Object;)Lcom/android/server/NativeDaemonEvent;
    //   46: pop
    //   47: return
    //   48: ldc_w 1118
    //   51: astore_2
    //   52: goto -27 -> 25
    //   55: astore_2
    //   56: aload_2
    //   57: invokevirtual 613	com/android/server/NativeDaemonConnectorException:rethrowAsParcelableException	()Ljava/lang/IllegalArgumentException;
    //   60: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	NetworkManagementService
    //   0	61	1	paramBoolean	boolean
    //   24	28	2	str	String
    //   55	2	2	localNativeDaemonConnectorException	NativeDaemonConnectorException
    //   16	10	3	localNativeDaemonConnector	NativeDaemonConnector
    // Exception table:
    //   from	to	target	type
    //   12	17	55	com/android/server/NativeDaemonConnectorException
    //   25	47	55	com/android/server/NativeDaemonConnectorException
  }
  
  public void setMtu(String paramString, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("interface", new Object[] { "setmtu", paramString, Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void setNetworkPermission(int paramInt, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramString != null) {}
    try
    {
      this.mConnector.execute("network", new Object[] { "permission", "network", "set", paramString, Integer.valueOf(paramInt) });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
    this.mConnector.execute("network", new Object[] { "permission", "network", "clear", Integer.valueOf(paramInt) });
  }
  
  public void setPermission(String paramString, int[] paramArrayOfInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    Object[] arrayOfObject = new Object[14];
    arrayOfObject[0] = "permission";
    arrayOfObject[1] = "user";
    arrayOfObject[2] = "set";
    arrayOfObject[3] = paramString;
    int i = 4;
    int j = 0;
    if (j < paramArrayOfInt.length)
    {
      int k = i + 1;
      arrayOfObject[i] = Integer.valueOf(paramArrayOfInt[j]);
      if ((j == paramArrayOfInt.length - 1) || (k == arrayOfObject.length)) {}
      for (;;)
      {
        try
        {
          this.mConnector.execute("network", Arrays.copyOf(arrayOfObject, k));
          i = 4;
          j += 1;
        }
        catch (NativeDaemonConnectorException paramString)
        {
          throw paramString.rethrowAsParcelableException();
        }
        i = k;
      }
    }
  }
  
  public void setPortForwardRules(boolean paramBoolean, String paramString1, String paramString2, String paramString3)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramBoolean) {}
    for (String str = "add";; str = "remove")
    {
      paramString1 = new NativeDaemonConnector.Command("nat", new Object[] { "portForward", str, paramString1, paramString2, paramString3 });
      try
      {
        this.mConnector.execute(paramString1);
        return;
      }
      catch (NativeDaemonConnectorException paramString1)
      {
        throw paramString1.rethrowAsParcelableException();
      }
    }
  }
  
  public void setUidCleartextNetworkPolicy(int paramInt1, int paramInt2)
  {
    if (Binder.getCallingUid() != paramInt1) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    }
    synchronized (this.mQuotaLock)
    {
      int i = this.mUidCleartextPolicy.get(paramInt1, 0);
      if (i == paramInt2) {
        return;
      }
      if (!this.mStrictEnabled)
      {
        this.mUidCleartextPolicy.put(paramInt1, paramInt2);
        return;
      }
      switch (paramInt2)
      {
      default: 
        throw new IllegalArgumentException("Unknown policy " + paramInt2);
      }
    }
    String str1 = "accept";
    for (;;)
    {
      try
      {
        this.mConnector.execute("strict", new Object[] { "set_uid_cleartext_policy", Integer.valueOf(paramInt1), str1 });
        this.mUidCleartextPolicy.put(paramInt1, paramInt2);
        return;
      }
      catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
      {
        throw localNativeDaemonConnectorException.rethrowAsParcelableException();
      }
      str1 = "log";
      continue;
      String str2 = "reject";
    }
  }
  
  public void setUidMeteredNetworkBlacklist(int paramInt, boolean paramBoolean)
  {
    setUidOnMeteredNetworkList(this.mUidRejectOnMetered, paramInt, true, paramBoolean);
  }
  
  public void setUidMeteredNetworkWhitelist(int paramInt, boolean paramBoolean)
  {
    setUidOnMeteredNetworkList(this.mUidAllowOnMetered, paramInt, false, paramBoolean);
  }
  
  public void shutdown()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.SHUTDOWN", "NetworkManagement");
    Slog.i("NetworkManagement", "Shutting down");
  }
  
  public void startAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    if (paramWifiConfiguration == null) {}
    for (;;)
    {
      try
      {
        paramWifiConfiguration = new Object[2];
        paramWifiConfiguration[0] = "set";
        paramWifiConfiguration[1] = paramString;
        executeOrLogWithMessage("softap", paramWifiConfiguration, 214, "Ok", "startAccessPoint Error setting up softap");
        executeOrLogWithMessage("softap", new Object[] { "startap", paramString }, 214, "Ok", "startAccessPoint Error starting softap");
        return;
      }
      catch (NativeDaemonConnectorException paramWifiConfiguration)
      {
        Object localObject2;
        Object localObject1;
        int i;
        throw paramWifiConfiguration.rethrowAsParcelableException();
      }
      localObject2 = "broadcast";
      localObject1 = localObject2;
      if (this.mContext.getResources().getBoolean(17957070))
      {
        localObject1 = localObject2;
        if (paramWifiConfiguration.hiddenSSID) {
          localObject1 = "hidden";
        }
      }
      if (this.mContext.getResources().getBoolean(17957069))
      {
        i = Settings.System.getInt(this.mContext.getContentResolver(), "WIFI_HOTSPOT_MAX_CLIENT_NUM", 8);
        if (DBG) {
          Slog.d("NetworkManagement", "clientNum: " + i);
        }
        localObject2 = new Object[8];
        localObject2[0] = "set";
        localObject2[1] = paramString;
        localObject2[2] = paramWifiConfiguration.SSID;
        localObject2[3] = localObject1;
        localObject2[4] = Integer.toString(paramWifiConfiguration.apChannel);
        localObject2[5] = getSecurityType(paramWifiConfiguration);
        localObject2[6] = new NativeDaemonConnector.SensitiveArg(paramWifiConfiguration.preSharedKey);
        localObject2[7] = Integer.valueOf(i);
        paramWifiConfiguration = (WifiConfiguration)localObject2;
      }
      else
      {
        localObject2 = new Object[7];
        localObject2[0] = "set";
        localObject2[1] = paramString;
        localObject2[2] = paramWifiConfiguration.SSID;
        localObject2[3] = localObject1;
        localObject2[4] = Integer.toString(paramWifiConfiguration.apChannel);
        localObject2[5] = getSecurityType(paramWifiConfiguration);
        localObject2[6] = new NativeDaemonConnector.SensitiveArg(paramWifiConfiguration.preSharedKey);
        paramWifiConfiguration = (WifiConfiguration)localObject2;
      }
    }
  }
  
  public void startClatd(String paramString)
    throws IllegalStateException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("clatd", new Object[] { "start", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void startInterfaceForwarding(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    modifyInterfaceForward(true, paramString1, paramString2);
  }
  
  public void startTethering(String[] paramArrayOfString)
  {
    int i = 0;
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    NativeDaemonConnector.Command localCommand = new NativeDaemonConnector.Command("tether", new Object[] { "start" });
    int j = paramArrayOfString.length;
    while (i < j)
    {
      localCommand.appendArg(paramArrayOfString[i]);
      i += 1;
    }
    try
    {
      this.mConnector.execute(localCommand);
      return;
    }
    catch (NativeDaemonConnectorException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowAsParcelableException();
    }
  }
  
  public void startWigigAccessPoint()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("softap", new Object[] { "qccmd", "set", "enable_wigig_softap=1" });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void stopAccessPoint(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      executeOrLogWithMessage("softap", new Object[] { "stopap" }, 214, "Ok", "stopAccessPoint Error stopping softap");
      wifiFirmwareReload(paramString, "STA");
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void stopClatd(String paramString)
    throws IllegalStateException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("clatd", new Object[] { "stop", paramString });
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void stopInterfaceForwarding(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    modifyInterfaceForward(false, paramString1, paramString2);
  }
  
  public void stopTethering()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("tether", new Object[] { "stop" });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void stopWigigAccessPoint()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("softap", new Object[] { "qccmd", "set", "enable_wigig_softap=0" });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
  }
  
  public void systemReady()
  {
    if (DBG)
    {
      long l1 = System.currentTimeMillis();
      prepareNativeDaemon();
      long l2 = System.currentTimeMillis();
      Slog.d("NetworkManagement", "Prepared in " + (l2 - l1) + "ms");
      return;
    }
    prepareNativeDaemon();
    if (this.mContext.getResources().getBoolean(17957061))
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("org.codeaurora.restrictData");
      this.mContext.registerReceiver(this.mZeroBalanceReceiver, localIntentFilter);
    }
    if (DBG) {
      Slog.d("NetworkManagement", "ZeroBalance registering receiver");
    }
  }
  
  public void tetherInterface(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("tether", new Object[] { "interface", "add", paramString });
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(new RouteInfo(getInterfaceConfig(paramString).getLinkAddress(), null, paramString));
      addInterfaceToLocalNetwork(paramString, localArrayList);
      return;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      throw paramString.rethrowAsParcelableException();
    }
  }
  
  public void unregisterNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
  {
    this.mNetworkActivityListeners.unregister(paramINetworkActivityListener);
  }
  
  public void unregisterObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    this.mObservers.unregister(paramINetworkManagementEventObserver);
  }
  
  public void untetherInterface(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    try
    {
      this.mConnector.execute("tether", new Object[] { "interface", "remove", paramString });
      return;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      throw localNativeDaemonConnectorException.rethrowAsParcelableException();
    }
    finally
    {
      removeInterfaceFromLocalNetwork(paramString);
    }
  }
  
  public void wifiFirmwareReload(String paramString1, String paramString2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkManagement");
    String str = "wifiFirmwareReload Error reloading " + paramString1 + " fw in " + paramString2 + " mode";
    try
    {
      executeOrLogWithMessage("softap", new Object[] { "fwreload", paramString1, paramString2 }, 214, "Ok", str);
      this.mConnector.waitForCallbacks();
      return;
    }
    catch (NativeDaemonConnectorException paramString1)
    {
      throw paramString1.rethrowAsParcelableException();
    }
  }
  
  private static class IdleTimerParams
  {
    public int networkCount;
    public final int timeout;
    public final int type;
    
    IdleTimerParams(int paramInt1, int paramInt2)
    {
      this.timeout = paramInt1;
      this.type = paramInt2;
      this.networkCount = 1;
    }
  }
  
  private class NetdCallbackReceiver
    implements INativeDaemonConnectorCallbacks
  {
    private NetdCallbackReceiver() {}
    
    public boolean onCheckHoldWakeLock(int paramInt)
    {
      return paramInt == 613;
    }
    
    public void onDaemonConnected()
    {
      Slog.i("NetworkManagement", "onDaemonConnected()");
      if (NetworkManagementService.-get1(NetworkManagementService.this) != null)
      {
        NetworkManagementService.-get1(NetworkManagementService.this).countDown();
        NetworkManagementService.-set0(NetworkManagementService.this, null);
        return;
      }
      NetworkManagementService.-get4(NetworkManagementService.this).post(new Runnable()
      {
        public void run()
        {
          NetworkManagementService.-wrap0(NetworkManagementService.this);
          NetworkManagementService.-wrap12(NetworkManagementService.this);
        }
      });
    }
    
    public boolean onEvent(int paramInt, String paramString, String[] paramArrayOfString)
    {
      String str = String.format("Invalid event from daemon (%s)", new Object[] { paramString });
      switch (paramInt)
      {
      }
      for (;;)
      {
        return false;
        if ((paramArrayOfString.length >= 4) && (paramArrayOfString[1].equals("Iface")))
        {
          if (paramArrayOfString[2].equals("added"))
          {
            NetworkManagementService.-wrap3(NetworkManagementService.this, paramArrayOfString[3]);
            return true;
          }
        }
        else {
          throw new IllegalStateException(str);
        }
        if (paramArrayOfString[2].equals("removed"))
        {
          NetworkManagementService.-wrap8(NetworkManagementService.this, paramArrayOfString[3]);
          return true;
        }
        if ((paramArrayOfString[2].equals("changed")) && (paramArrayOfString.length == 5))
        {
          NetworkManagementService.-wrap9(NetworkManagementService.this, paramArrayOfString[3], paramArrayOfString[4].equals("up"));
          return true;
        }
        if ((paramArrayOfString[2].equals("linkstate")) && (paramArrayOfString.length == 5))
        {
          NetworkManagementService.-wrap6(NetworkManagementService.this, paramArrayOfString[3], paramArrayOfString[4].equals("up"));
          return true;
        }
        throw new IllegalStateException(str);
        if ((paramArrayOfString.length >= 5) && (paramArrayOfString[1].equals("limit")))
        {
          if (paramArrayOfString[2].equals("alert"))
          {
            NetworkManagementService.-wrap10(NetworkManagementService.this, paramArrayOfString[3], paramArrayOfString[4]);
            return true;
          }
        }
        else {
          throw new IllegalStateException(str);
        }
        throw new IllegalStateException(str);
        if ((paramArrayOfString.length >= 3) && (paramArrayOfString[2].equals("IfaceMessage")))
        {
          Slog.d("NetworkManagement", "onEvent: " + paramString);
          if (paramArrayOfString[5] == null) {
            break label424;
          }
          NetworkManagementService.-wrap7(NetworkManagementService.this, paramArrayOfString[4] + " " + paramArrayOfString[5]);
        }
        for (;;)
        {
          return true;
          throw new IllegalStateException(str);
          label424:
          NetworkManagementService.-wrap7(NetworkManagementService.this, paramArrayOfString[4]);
        }
        if ((paramArrayOfString.length >= 4) && (paramArrayOfString[1].equals("IfaceClass")))
        {
          l2 = 0L;
          i = -1;
          if (paramArrayOfString.length < 5) {
            break label562;
          }
        }
        try
        {
          long l3 = Long.parseLong(paramArrayOfString[4]);
          l1 = l3;
          paramInt = i;
          l2 = l3;
          if (paramArrayOfString.length == 6)
          {
            l2 = l3;
            paramInt = Integer.parseInt(paramArrayOfString[5]);
            l1 = l3;
          }
        }
        catch (NumberFormatException paramString)
        {
          for (;;)
          {
            boolean bool;
            int j;
            Object localObject;
            InetAddress localInetAddress;
            long l1 = l2;
            paramInt = i;
          }
        }
        bool = paramArrayOfString[2].equals("active");
        paramString = NetworkManagementService.this;
        j = Integer.parseInt(paramArrayOfString[3]);
        if (bool) {}
        for (i = 3;; i = 1)
        {
          NetworkManagementService.-wrap4(paramString, j, i, l1, paramInt, false);
          return true;
          throw new IllegalStateException(str);
          label562:
          l1 = SystemClock.elapsedRealtimeNanos();
          paramInt = i;
          break;
        }
        if ((paramArrayOfString.length >= 7) && (paramArrayOfString[1].equals("Address"))) {
          paramString = paramArrayOfString[4];
        }
        for (;;)
        {
          try
          {
            paramInt = Integer.parseInt(paramArrayOfString[5]);
            i = Integer.parseInt(paramArrayOfString[6]);
            localObject = new LinkAddress(paramArrayOfString[3], paramInt, i);
            if (!paramArrayOfString[2].equals("updated")) {
              break label689;
            }
            NetworkManagementService.-wrap2(NetworkManagementService.this, paramString, (LinkAddress)localObject);
            return true;
          }
          catch (IllegalArgumentException paramString)
          {
            throw new IllegalStateException(str, paramString);
          }
          catch (NumberFormatException paramString)
          {
            throw new IllegalStateException(str, paramString);
          }
          throw new IllegalStateException(str);
          label689:
          NetworkManagementService.-wrap1(NetworkManagementService.this, paramString, (LinkAddress)localObject);
        }
        if ((paramArrayOfString.length == 6) && (paramArrayOfString[1].equals("DnsInfo")) && (paramArrayOfString[2].equals("servers"))) {}
        try
        {
          l1 = Long.parseLong(paramArrayOfString[4]);
          paramString = paramArrayOfString[5].split(",");
          NetworkManagementService.-wrap5(NetworkManagementService.this, paramArrayOfString[3], l1, paramString);
          return true;
        }
        catch (NumberFormatException paramString)
        {
          throw new IllegalStateException(str);
        }
        if ((!paramArrayOfString[1].equals("Route")) || (paramArrayOfString.length < 6)) {
          throw new IllegalStateException(str);
        }
        localObject = null;
        paramString = null;
        paramInt = 1;
        i = 4;
        if ((i + 1 < paramArrayOfString.length) && (paramInt != 0))
        {
          if (paramArrayOfString[i].equals("dev")) {
            if (paramString == null) {
              paramString = paramArrayOfString[(i + 1)];
            }
          }
          for (;;)
          {
            i += 2;
            break;
            paramInt = 0;
            continue;
            if (paramArrayOfString[i].equals("via"))
            {
              if (localObject == null) {
                localObject = paramArrayOfString[(i + 1)];
              } else {
                paramInt = 0;
              }
            }
            else {
              paramInt = 0;
            }
          }
        }
        if (paramInt != 0)
        {
          localInetAddress = null;
          if (localObject != null) {}
          try
          {
            localInetAddress = InetAddress.parseNumericAddress((String)localObject);
            paramString = new RouteInfo(new IpPrefix(paramArrayOfString[3]), localInetAddress, paramString);
            NetworkManagementService.-wrap11(NetworkManagementService.this, paramArrayOfString[2], paramString);
            return true;
          }
          catch (IllegalArgumentException paramString) {}
        }
        throw new IllegalStateException(str);
        paramInt = Integer.parseInt(paramArrayOfString[1]);
        paramString = HexDump.hexStringToByteArray(paramArrayOfString[2]);
        try
        {
          ActivityManagerNative.getDefault().notifyCleartextNetwork(paramInt, paramString);
        }
        catch (RemoteException paramString) {}
      }
    }
  }
  
  class NetdResponseCode
  {
    public static final int BandwidthControl = 601;
    public static final int ClatdStatusResult = 223;
    public static final int DnsProxyQueryResult = 222;
    public static final int InterfaceAddressChange = 614;
    public static final int InterfaceChange = 600;
    public static final int InterfaceClassActivity = 613;
    public static final int InterfaceDnsServerInfo = 615;
    public static final int InterfaceGetCfgResult = 213;
    public static final int InterfaceListResult = 110;
    public static final int InterfaceMessage = 618;
    public static final int InterfaceRxCounterResult = 216;
    public static final int InterfaceTxCounterResult = 217;
    public static final int IpFwdStatusResult = 211;
    public static final int QuotaCounterResult = 220;
    public static final int RouteChange = 616;
    public static final int SoftapStatusResult = 214;
    public static final int StrictCleartext = 617;
    public static final int TetherDnsFwdTgtListResult = 112;
    public static final int TetherInterfaceListResult = 111;
    public static final int TetherStatusResult = 210;
    public static final int TetheringStatsListResult = 114;
    public static final int TetheringStatsResult = 221;
    public static final int TtyListResult = 113;
    
    NetdResponseCode() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NetworkManagementService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */