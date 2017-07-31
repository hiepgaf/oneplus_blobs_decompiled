package com.android.server.connectivity;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkMisc;
import android.net.RouteInfo;
import android.net.UidRange;
import android.net.Uri;
import android.os.Binder;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemService;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.server.net.BaseNetworkObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;

public class Vpn
{
  private static final boolean LOGD = true;
  private static final String NETWORKTYPE = "VPN";
  private static final String TAG = "Vpn";
  private boolean mAlwaysOn = false;
  @GuardedBy("this")
  private Set<UidRange> mBlockedUsers = new ArraySet();
  private VpnConfig mConfig;
  private Connection mConnection;
  private Context mContext;
  private volatile boolean mEnableTeardown = true;
  private String mInterface;
  private boolean mIsPackageIntentReceiverRegistered = false;
  private LegacyVpnRunner mLegacyVpnRunner;
  private boolean mLockdown = false;
  private final Looper mLooper;
  private final INetworkManagementService mNetd;
  private NetworkAgent mNetworkAgent;
  private final NetworkCapabilities mNetworkCapabilities;
  private NetworkInfo mNetworkInfo;
  private INetworkManagementEventObserver mObserver = new BaseNetworkObserver()
  {
    public void interfaceRemoved(String paramAnonymousString)
    {
      synchronized (Vpn.this)
      {
        if ((paramAnonymousString.equals(Vpn.-get4(Vpn.this))) && (Vpn.-wrap1(Vpn.this, paramAnonymousString) == 0))
        {
          Vpn.-set4(Vpn.this, null);
          Vpn.-set5(Vpn.this, null);
          Vpn.-set0(Vpn.this, null);
          Vpn.-set2(Vpn.this, null);
          if (Vpn.-get1(Vpn.this) == null) {
            break label114;
          }
          Vpn.-get2(Vpn.this).unbindService(Vpn.-get1(Vpn.this));
          Vpn.-set1(Vpn.this, null);
          Vpn.-wrap3(Vpn.this);
        }
        label114:
        while (Vpn.-get5(Vpn.this) == null) {
          return;
        }
        Vpn.-get5(Vpn.this).exit();
        Vpn.-set3(Vpn.this, null);
      }
    }
    
    public void interfaceStatusChanged(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      Vpn localVpn = Vpn.this;
      if (!paramAnonymousBoolean) {}
      try
      {
        if (Vpn.-get5(Vpn.this) != null) {
          Vpn.-get5(Vpn.this).check(paramAnonymousString);
        }
        return;
      }
      finally
      {
        paramAnonymousString = finally;
        throw paramAnonymousString;
      }
    }
  };
  private int mOwnerUID;
  private String mPackage;
  private final BroadcastReceiver mPackageIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = 0;
      paramAnonymousContext = null;
      ??? = paramAnonymousIntent.getData();
      if (??? == null) {}
      while (paramAnonymousContext == null)
      {
        return;
        paramAnonymousContext = ((Uri)???).getSchemeSpecificPart();
      }
      for (;;)
      {
        synchronized (Vpn.this)
        {
          boolean bool = paramAnonymousContext.equals(Vpn.this.getAlwaysOnPackage());
          if (!bool) {
            return;
          }
          String str = paramAnonymousIntent.getAction();
          Log.i("Vpn", "Received broadcast " + str + " for always-on package " + paramAnonymousContext + " in user " + Vpn.-get8(Vpn.this));
          if (str.equals("android.intent.action.PACKAGE_REPLACED"))
          {
            Vpn.this.startAlwaysOnVpn();
            return;
          }
          if (!str.equals("android.intent.action.PACKAGE_REMOVED")) {
            continue;
          }
          if (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))
          {
            if (i == 0) {
              continue;
            }
            Vpn.-wrap0(Vpn.this, null, false);
          }
        }
        i = 1;
      }
    }
  };
  private PendingIntent mStatusIntent;
  private final int mUserHandle;
  @GuardedBy("this")
  private Set<UidRange> mVpnUsers = null;
  
  public Vpn(Looper paramLooper, Context paramContext, INetworkManagementService paramINetworkManagementService, int paramInt)
  {
    this.mContext = paramContext;
    this.mNetd = paramINetworkManagementService;
    this.mUserHandle = paramInt;
    this.mLooper = paramLooper;
    this.mPackage = "[Legacy VPN]";
    this.mOwnerUID = getAppUid(this.mPackage, this.mUserHandle);
    try
    {
      paramINetworkManagementService.registerObserver(this.mObserver);
      this.mNetworkInfo = new NetworkInfo(17, 0, "VPN", "");
      this.mNetworkCapabilities = new NetworkCapabilities();
      this.mNetworkCapabilities.addTransportType(4);
      this.mNetworkCapabilities.removeCapability(15);
      return;
    }
    catch (RemoteException paramLooper)
    {
      for (;;)
      {
        Log.wtf("Vpn", "Problem registering observer", paramLooper);
      }
    }
  }
  
  /* Error */
  private void agentConnect()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: invokespecial 226	com/android/server/connectivity/Vpn:makeLinkProperties	()Landroid/net/LinkProperties;
    //   6: astore 5
    //   8: aload 5
    //   10: invokevirtual 232	android/net/LinkProperties:hasIPv4DefaultRoute	()Z
    //   13: ifne +11 -> 24
    //   16: aload 5
    //   18: invokevirtual 235	android/net/LinkProperties:hasIPv6DefaultRoute	()Z
    //   21: ifeq +180 -> 201
    //   24: aload_0
    //   25: getfield 207	com/android/server/connectivity/Vpn:mNetworkCapabilities	Landroid/net/NetworkCapabilities;
    //   28: bipush 12
    //   30: invokevirtual 238	android/net/NetworkCapabilities:addCapability	(I)Landroid/net/NetworkCapabilities;
    //   33: pop
    //   34: aload_0
    //   35: getfield 97	com/android/server/connectivity/Vpn:mNetworkInfo	Landroid/net/NetworkInfo;
    //   38: getstatic 244	android/net/NetworkInfo$DetailedState:CONNECTING	Landroid/net/NetworkInfo$DetailedState;
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 248	android/net/NetworkInfo:setDetailedState	(Landroid/net/NetworkInfo$DetailedState;Ljava/lang/String;Ljava/lang/String;)V
    //   46: new 250	android/net/NetworkMisc
    //   49: dup
    //   50: invokespecial 251	android/net/NetworkMisc:<init>	()V
    //   53: astore 6
    //   55: iload_2
    //   56: istore_1
    //   57: aload_0
    //   58: getfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   61: getfield 256	com/android/internal/net/VpnConfig:allowBypass	Z
    //   64: ifeq +12 -> 76
    //   67: aload_0
    //   68: getfield 163	com/android/server/connectivity/Vpn:mLockdown	Z
    //   71: ifeq +143 -> 214
    //   74: iload_2
    //   75: istore_1
    //   76: aload 6
    //   78: iload_1
    //   79: putfield 257	android/net/NetworkMisc:allowBypass	Z
    //   82: invokestatic 263	android/os/Binder:clearCallingIdentity	()J
    //   85: lstore_3
    //   86: aload_0
    //   87: new 10	com/android/server/connectivity/Vpn$3
    //   90: dup
    //   91: aload_0
    //   92: aload_0
    //   93: getfield 179	com/android/server/connectivity/Vpn:mLooper	Landroid/os/Looper;
    //   96: aload_0
    //   97: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   100: ldc 25
    //   102: aload_0
    //   103: getfield 97	com/android/server/connectivity/Vpn:mNetworkInfo	Landroid/net/NetworkInfo;
    //   106: aload_0
    //   107: getfield 207	com/android/server/connectivity/Vpn:mNetworkCapabilities	Landroid/net/NetworkCapabilities;
    //   110: aload 5
    //   112: iconst_0
    //   113: aload 6
    //   115: invokespecial 266	com/android/server/connectivity/Vpn$3:<init>	(Lcom/android/server/connectivity/Vpn;Landroid/os/Looper;Landroid/content/Context;Ljava/lang/String;Landroid/net/NetworkInfo;Landroid/net/NetworkCapabilities;Landroid/net/LinkProperties;ILandroid/net/NetworkMisc;)V
    //   118: putfield 268	com/android/server/connectivity/Vpn:mNetworkAgent	Landroid/net/NetworkAgent;
    //   121: lload_3
    //   122: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   125: aload_0
    //   126: aload_0
    //   127: aload_0
    //   128: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   131: aload_0
    //   132: getfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   135: getfield 276	com/android/internal/net/VpnConfig:allowedApplications	Ljava/util/List;
    //   138: aload_0
    //   139: getfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   142: getfield 279	com/android/internal/net/VpnConfig:disallowedApplications	Ljava/util/List;
    //   145: invokevirtual 283	com/android/server/connectivity/Vpn:createUserAndRestrictedProfilesRanges	(ILjava/util/List;Ljava/util/List;)Ljava/util/Set;
    //   148: putfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   151: aload_0
    //   152: getfield 268	com/android/server/connectivity/Vpn:mNetworkAgent	Landroid/net/NetworkAgent;
    //   155: aload_0
    //   156: getfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   159: aload_0
    //   160: getfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   163: invokeinterface 289 1 0
    //   168: anewarray 291	android/net/UidRange
    //   171: invokeinterface 295 2 0
    //   176: checkcast 297	[Landroid/net/UidRange;
    //   179: invokevirtual 303	android/net/NetworkAgent:addUidRanges	([Landroid/net/UidRange;)V
    //   182: aload_0
    //   183: getfield 97	com/android/server/connectivity/Vpn:mNetworkInfo	Landroid/net/NetworkInfo;
    //   186: iconst_1
    //   187: invokevirtual 307	android/net/NetworkInfo:setIsAvailable	(Z)V
    //   190: aload_0
    //   191: getstatic 310	android/net/NetworkInfo$DetailedState:CONNECTED	Landroid/net/NetworkInfo$DetailedState;
    //   194: ldc_w 311
    //   197: invokespecial 153	com/android/server/connectivity/Vpn:updateState	(Landroid/net/NetworkInfo$DetailedState;Ljava/lang/String;)V
    //   200: return
    //   201: aload_0
    //   202: getfield 207	com/android/server/connectivity/Vpn:mNetworkCapabilities	Landroid/net/NetworkCapabilities;
    //   205: bipush 12
    //   207: invokevirtual 214	android/net/NetworkCapabilities:removeCapability	(I)Landroid/net/NetworkCapabilities;
    //   210: pop
    //   211: goto -177 -> 34
    //   214: iconst_1
    //   215: istore_1
    //   216: goto -140 -> 76
    //   219: astore 5
    //   221: lload_3
    //   222: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   225: aload 5
    //   227: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	this	Vpn
    //   56	160	1	bool1	boolean
    //   1	74	2	bool2	boolean
    //   85	137	3	l	long
    //   6	105	5	localLinkProperties	LinkProperties
    //   219	7	5	localObject	Object
    //   53	61	6	localNetworkMisc	NetworkMisc
    // Exception table:
    //   from	to	target	type
    //   86	121	219	finally
  }
  
  private void agentDisconnect()
  {
    if (this.mNetworkInfo.isConnected())
    {
      agentDisconnect(this.mNetworkInfo, this.mNetworkAgent);
      this.mNetworkAgent = null;
    }
  }
  
  private void agentDisconnect(NetworkAgent paramNetworkAgent)
  {
    agentDisconnect(new NetworkInfo(this.mNetworkInfo), paramNetworkAgent);
  }
  
  private void agentDisconnect(NetworkInfo paramNetworkInfo, NetworkAgent paramNetworkAgent)
  {
    paramNetworkInfo.setIsAvailable(false);
    paramNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
    if (paramNetworkAgent != null) {
      paramNetworkAgent.sendNetworkInfo(paramNetworkInfo);
    }
  }
  
  private boolean canHaveRestrictedProfile(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = UserManager.get(this.mContext).canHaveRestrictedProfile(paramInt);
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void enforceControlPermission()
  {
    this.mContext.enforceCallingPermission("android.permission.CONTROL_VPN", "Unauthorized Caller");
  }
  
  private void enforceControlPermissionOrInternalCaller()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_VPN", "Unauthorized Caller");
  }
  
  private static RouteInfo findIPv4DefaultRoute(LinkProperties paramLinkProperties)
  {
    paramLinkProperties = paramLinkProperties.getAllRoutes().iterator();
    while (paramLinkProperties.hasNext())
    {
      RouteInfo localRouteInfo = (RouteInfo)paramLinkProperties.next();
      if ((localRouteInfo.isDefaultRoute()) && ((localRouteInfo.getGateway() instanceof Inet4Address))) {
        return localRouteInfo;
      }
    }
    throw new IllegalStateException("Unable to find IPv4 default gateway");
  }
  
  private int getAppUid(String paramString, int paramInt)
  {
    if ("[Legacy VPN]".equals(paramString)) {
      return Process.myUid();
    }
    PackageManager localPackageManager = this.mContext.getPackageManager();
    try
    {
      paramInt = localPackageManager.getPackageUidAsUser(paramString, paramInt);
      return paramInt;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return -1;
  }
  
  private SortedSet<Integer> getAppsUids(List<String> paramList, int paramInt)
  {
    TreeSet localTreeSet = new TreeSet();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      int i = getAppUid((String)paramList.next(), paramInt);
      if (i != -1) {
        localTreeSet.add(Integer.valueOf(i));
      }
    }
    return localTreeSet;
  }
  
  private boolean isCallerEstablishedOwnerLocked()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isRunningLocked())
    {
      bool1 = bool2;
      if (Binder.getCallingUid() == this.mOwnerUID) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isCurrentPreparedPackage(String paramString)
  {
    return getAppUid(paramString, this.mUserHandle) == this.mOwnerUID;
  }
  
  private static boolean isNullOrLegacyVpn(String paramString)
  {
    if (paramString != null) {
      return "[Legacy VPN]".equals(paramString);
    }
    return true;
  }
  
  private boolean isRunningLocked()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mNetworkAgent != null)
    {
      bool1 = bool2;
      if (this.mInterface != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isVpnUserPreConsented(String paramString)
  {
    boolean bool = false;
    if (((AppOpsManager)this.mContext.getSystemService("appops")).noteOpNoThrow(47, Binder.getCallingUid(), paramString) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private native boolean jniAddAddress(String paramString1, String paramString2, int paramInt);
  
  private native int jniCheck(String paramString);
  
  private native int jniCreate(int paramInt);
  
  private native boolean jniDelAddress(String paramString1, String paramString2, int paramInt);
  
  private native String jniGetName(int paramInt);
  
  private native void jniReset(String paramString);
  
  private native int jniSetAddresses(String paramString1, String paramString2);
  
  private LinkProperties makeLinkProperties()
  {
    boolean bool4 = this.mConfig.allowIPv4;
    boolean bool2 = this.mConfig.allowIPv6;
    LinkProperties localLinkProperties = new LinkProperties();
    localLinkProperties.setInterfaceName(this.mInterface);
    boolean bool1 = bool4;
    boolean bool3 = bool2;
    Object localObject2;
    if (this.mConfig.addresses != null)
    {
      localObject1 = this.mConfig.addresses.iterator();
      for (;;)
      {
        bool1 = bool4;
        bool3 = bool2;
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        localObject2 = (LinkAddress)((Iterator)localObject1).next();
        localLinkProperties.addLinkAddress((LinkAddress)localObject2);
        bool4 |= ((LinkAddress)localObject2).getAddress() instanceof Inet4Address;
        bool2 |= ((LinkAddress)localObject2).getAddress() instanceof Inet6Address;
      }
    }
    bool2 = bool1;
    bool4 = bool3;
    if (this.mConfig.routes != null)
    {
      localObject1 = this.mConfig.routes.iterator();
      for (;;)
      {
        bool2 = bool1;
        bool4 = bool3;
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        localObject2 = (RouteInfo)((Iterator)localObject1).next();
        localLinkProperties.addRoute((RouteInfo)localObject2);
        localObject2 = ((RouteInfo)localObject2).getDestination().getAddress();
        bool1 |= localObject2 instanceof Inet4Address;
        bool3 |= localObject2 instanceof Inet6Address;
      }
    }
    bool1 = bool2;
    bool3 = bool4;
    if (this.mConfig.dnsServers != null)
    {
      localObject1 = this.mConfig.dnsServers.iterator();
      for (;;)
      {
        bool1 = bool2;
        bool3 = bool4;
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        localObject2 = InetAddress.parseNumericAddress((String)((Iterator)localObject1).next());
        localLinkProperties.addDnsServer((InetAddress)localObject2);
        bool2 |= localObject2 instanceof Inet4Address;
        bool4 |= localObject2 instanceof Inet6Address;
      }
    }
    if (!bool1) {
      localLinkProperties.addRoute(new RouteInfo(new IpPrefix(Inet4Address.ANY, 0), 7));
    }
    if (!bool3) {
      localLinkProperties.addRoute(new RouteInfo(new IpPrefix(Inet6Address.ANY, 0), 7));
    }
    Object localObject1 = new StringBuilder();
    if (this.mConfig.searchDomains != null)
    {
      localObject2 = this.mConfig.searchDomains.iterator();
      while (((Iterator)localObject2).hasNext()) {
        ((StringBuilder)localObject1).append((String)((Iterator)localObject2).next()).append(' ');
      }
    }
    localLinkProperties.setDomains(((StringBuilder)localObject1).toString().trim());
    return localLinkProperties;
  }
  
  private void maybeRegisterPackageChangeReceiverLocked(String paramString)
  {
    unregisterPackageChangeReceiverLocked();
    if (!isNullOrLegacyVpn(paramString))
    {
      this.mIsPackageIntentReceiverRegistered = true;
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addDataScheme("package");
      localIntentFilter.addDataSchemeSpecificPart(paramString, 0);
      this.mContext.registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.of(this.mUserHandle), localIntentFilter, null, null);
    }
  }
  
  /* Error */
  private void prepareInternal(String paramString)
  {
    // Byte code:
    //   0: invokestatic 263	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: aload_0
    //   5: getfield 89	com/android/server/connectivity/Vpn:mInterface	Ljava/lang/String;
    //   8: ifnull +30 -> 38
    //   11: aload_0
    //   12: aconst_null
    //   13: putfield 117	com/android/server/connectivity/Vpn:mStatusIntent	Landroid/app/PendingIntent;
    //   16: aload_0
    //   17: invokespecial 143	com/android/server/connectivity/Vpn:agentDisconnect	()V
    //   20: aload_0
    //   21: aload_0
    //   22: getfield 89	com/android/server/connectivity/Vpn:mInterface	Ljava/lang/String;
    //   25: invokespecial 589	com/android/server/connectivity/Vpn:jniReset	(Ljava/lang/String;)V
    //   28: aload_0
    //   29: aconst_null
    //   30: putfield 89	com/android/server/connectivity/Vpn:mInterface	Ljava/lang/String;
    //   33: aload_0
    //   34: aconst_null
    //   35: putfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   38: aload_0
    //   39: getfield 77	com/android/server/connectivity/Vpn:mConnection	Lcom/android/server/connectivity/Vpn$Connection;
    //   42: astore 4
    //   44: aload 4
    //   46: ifnull +143 -> 189
    //   49: aload_0
    //   50: getfield 77	com/android/server/connectivity/Vpn:mConnection	Lcom/android/server/connectivity/Vpn$Connection;
    //   53: invokestatic 592	com/android/server/connectivity/Vpn$Connection:-get0	(Lcom/android/server/connectivity/Vpn$Connection;)Landroid/os/IBinder;
    //   56: ldc_w 593
    //   59: invokestatic 599	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   62: aconst_null
    //   63: iconst_1
    //   64: invokeinterface 605 5 0
    //   69: pop
    //   70: aload_0
    //   71: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   74: aload_0
    //   75: getfield 77	com/android/server/connectivity/Vpn:mConnection	Lcom/android/server/connectivity/Vpn$Connection;
    //   78: invokevirtual 609	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 77	com/android/server/connectivity/Vpn:mConnection	Lcom/android/server/connectivity/Vpn$Connection;
    //   86: aload_0
    //   87: getfield 177	com/android/server/connectivity/Vpn:mNetd	Landroid/os/INetworkManagementService;
    //   90: aload_0
    //   91: getfield 189	com/android/server/connectivity/Vpn:mOwnerUID	I
    //   94: invokeinterface 613 2 0
    //   99: ldc 28
    //   101: new 528	java/lang/StringBuilder
    //   104: dup
    //   105: invokespecial 529	java/lang/StringBuilder:<init>	()V
    //   108: ldc_w 615
    //   111: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: aload_0
    //   115: getfield 183	com/android/server/connectivity/Vpn:mPackage	Ljava/lang/String;
    //   118: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: ldc_w 617
    //   124: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: aload_1
    //   128: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: invokevirtual 543	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   134: invokestatic 620	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   137: pop
    //   138: aload_0
    //   139: aload_1
    //   140: putfield 183	com/android/server/connectivity/Vpn:mPackage	Ljava/lang/String;
    //   143: aload_0
    //   144: aload_0
    //   145: aload_1
    //   146: aload_0
    //   147: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   150: invokespecial 187	com/android/server/connectivity/Vpn:getAppUid	(Ljava/lang/String;I)I
    //   153: putfield 189	com/android/server/connectivity/Vpn:mOwnerUID	I
    //   156: aload_0
    //   157: getfield 177	com/android/server/connectivity/Vpn:mNetd	Landroid/os/INetworkManagementService;
    //   160: aload_0
    //   161: getfield 189	com/android/server/connectivity/Vpn:mOwnerUID	I
    //   164: invokeinterface 623 2 0
    //   169: aload_0
    //   170: aconst_null
    //   171: putfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   174: aload_0
    //   175: getstatic 626	android/net/NetworkInfo$DetailedState:IDLE	Landroid/net/NetworkInfo$DetailedState;
    //   178: ldc_w 628
    //   181: invokespecial 153	com/android/server/connectivity/Vpn:updateState	(Landroid/net/NetworkInfo$DetailedState;Ljava/lang/String;)V
    //   184: lload_2
    //   185: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   188: return
    //   189: aload_0
    //   190: getfield 93	com/android/server/connectivity/Vpn:mLegacyVpnRunner	Lcom/android/server/connectivity/Vpn$LegacyVpnRunner;
    //   193: ifnull -107 -> 86
    //   196: aload_0
    //   197: getfield 93	com/android/server/connectivity/Vpn:mLegacyVpnRunner	Lcom/android/server/connectivity/Vpn$LegacyVpnRunner;
    //   200: invokevirtual 631	com/android/server/connectivity/Vpn$LegacyVpnRunner:exit	()V
    //   203: aload_0
    //   204: aconst_null
    //   205: putfield 93	com/android/server/connectivity/Vpn:mLegacyVpnRunner	Lcom/android/server/connectivity/Vpn$LegacyVpnRunner;
    //   208: goto -122 -> 86
    //   211: astore_1
    //   212: lload_2
    //   213: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   216: aload_1
    //   217: athrow
    //   218: astore 4
    //   220: ldc 28
    //   222: new 528	java/lang/StringBuilder
    //   225: dup
    //   226: invokespecial 529	java/lang/StringBuilder:<init>	()V
    //   229: ldc_w 633
    //   232: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: aload_0
    //   236: getfield 189	com/android/server/connectivity/Vpn:mOwnerUID	I
    //   239: invokevirtual 636	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   242: ldc_w 638
    //   245: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: aload 4
    //   250: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   253: invokevirtual 543	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   256: invokestatic 643	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   259: pop
    //   260: goto -161 -> 99
    //   263: astore_1
    //   264: ldc 28
    //   266: new 528	java/lang/StringBuilder
    //   269: dup
    //   270: invokespecial 529	java/lang/StringBuilder:<init>	()V
    //   273: ldc_w 645
    //   276: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   279: aload_0
    //   280: getfield 189	com/android/server/connectivity/Vpn:mOwnerUID	I
    //   283: invokevirtual 636	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   286: ldc_w 638
    //   289: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload_1
    //   293: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   296: invokevirtual 543	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   299: invokestatic 643	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   302: pop
    //   303: goto -134 -> 169
    //   306: astore 4
    //   308: goto -238 -> 70
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	311	0	this	Vpn
    //   0	311	1	paramString	String
    //   3	210	2	l	long
    //   42	3	4	localConnection	Connection
    //   218	31	4	localException1	Exception
    //   306	1	4	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   4	38	211	finally
    //   38	44	211	finally
    //   49	70	211	finally
    //   70	86	211	finally
    //   86	99	211	finally
    //   99	156	211	finally
    //   156	169	211	finally
    //   169	184	211	finally
    //   189	208	211	finally
    //   220	260	211	finally
    //   264	303	211	finally
    //   86	99	218	java/lang/Exception
    //   156	169	263	java/lang/Exception
    //   49	70	306	java/lang/Exception
  }
  
  private void prepareStatusIntent()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mStatusIntent = VpnConfig.getIntentForStatusPanel(this.mContext);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void removeVpnUserLocked(int paramInt)
  {
    if (this.mVpnUsers == null) {
      throw new IllegalStateException("VPN is not active");
    }
    List localList = uidRangesForUser(paramInt);
    if (this.mNetworkAgent != null) {
      this.mNetworkAgent.removeUidRanges((UidRange[])localList.toArray(new UidRange[localList.size()]));
    }
    this.mVpnUsers.removeAll(localList);
  }
  
  @GuardedBy("this")
  private boolean setAllowOnlyVpnForUids(boolean paramBoolean, Collection<UidRange> paramCollection)
  {
    if (paramCollection.size() == 0) {
      return true;
    }
    UidRange[] arrayOfUidRange = (UidRange[])paramCollection.toArray(new UidRange[paramCollection.size()]);
    try
    {
      this.mNetd.setAllowOnlyVpnForUids(paramBoolean, arrayOfUidRange);
      if (paramBoolean)
      {
        this.mBlockedUsers.addAll(paramCollection);
        return true;
      }
    }
    catch (RemoteException|RuntimeException localRemoteException)
    {
      Log.e("Vpn", "Updating blocked=" + paramBoolean + " for UIDs " + Arrays.toString(paramCollection.toArray()) + " failed", localRemoteException);
      return false;
    }
    this.mBlockedUsers.removeAll(paramCollection);
    return true;
  }
  
  private boolean setAndSaveAlwaysOnPackage(String paramString, boolean paramBoolean)
  {
    try
    {
      if (setAlwaysOnPackage(paramString, paramBoolean))
      {
        saveAlwaysOnPackage();
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
  
  @GuardedBy("this")
  private void setVpnForcedLocked(boolean paramBoolean)
  {
    ArraySet localArraySet = new ArraySet(this.mBlockedUsers);
    if (paramBoolean)
    {
      Set localSet = createUserAndRestrictedProfilesRanges(this.mUserHandle, null, Collections.singletonList(this.mPackage));
      localArraySet.removeAll(localSet);
      localSet.removeAll(this.mBlockedUsers);
      setAllowOnlyVpnForUids(false, localArraySet);
      setAllowOnlyVpnForUids(true, localSet);
      return;
    }
    setAllowOnlyVpnForUids(false, localArraySet);
  }
  
  private void startLegacyVpn(VpnConfig paramVpnConfig, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    try
    {
      stopLegacyVpnPrivileged();
      prepareInternal("[Legacy VPN]");
      updateState(NetworkInfo.DetailedState.CONNECTING, "startLegacyVpn");
      this.mLegacyVpnRunner = new LegacyVpnRunner(paramVpnConfig, paramArrayOfString1, paramArrayOfString2);
      this.mLegacyVpnRunner.start();
      return;
    }
    finally
    {
      paramVpnConfig = finally;
      throw paramVpnConfig;
    }
  }
  
  private List<UidRange> uidRangesForUser(int paramInt)
  {
    UidRange localUidRange1 = UidRange.createForUser(paramInt);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mVpnUsers.iterator();
    while (localIterator.hasNext())
    {
      UidRange localUidRange2 = (UidRange)localIterator.next();
      if (localUidRange1.containsRange(localUidRange2)) {
        localArrayList.add(localUidRange2);
      }
    }
    return localArrayList;
  }
  
  private void unregisterPackageChangeReceiverLocked()
  {
    if (this.mIsPackageIntentReceiverRegistered)
    {
      this.mContext.unregisterReceiver(this.mPackageIntentReceiver);
      this.mIsPackageIntentReceiverRegistered = false;
    }
  }
  
  private void updateState(NetworkInfo.DetailedState paramDetailedState, String paramString)
  {
    Log.d("Vpn", "setting state=" + paramDetailedState + ", reason=" + paramString);
    this.mNetworkInfo.setDetailedState(paramDetailedState, paramString, null);
    if (this.mNetworkAgent != null) {
      this.mNetworkAgent.sendNetworkInfo(this.mNetworkInfo);
    }
  }
  
  public boolean addAddress(String paramString, int paramInt)
  {
    try
    {
      boolean bool = isCallerEstablishedOwnerLocked();
      if (!bool) {
        return false;
      }
      bool = jniAddAddress(this.mInterface, paramString, paramInt);
      this.mNetworkAgent.sendLinkProperties(makeLinkProperties());
      return bool;
    }
    finally {}
  }
  
  void addUserToRanges(Set<UidRange> paramSet, int paramInt, List<String> paramList1, List<String> paramList2)
  {
    int i;
    if (paramList1 != null)
    {
      int j = -1;
      int k = -1;
      paramList1 = getAppsUids(paramList1, paramInt).iterator();
      if (paramList1.hasNext())
      {
        paramInt = ((Integer)paramList1.next()).intValue();
        if (j == -1) {
          i = paramInt;
        }
        for (;;)
        {
          j = i;
          k = paramInt;
          break;
          i = j;
          if (paramInt != k + 1)
          {
            paramSet.add(new UidRange(j, k));
            i = paramInt;
          }
        }
      }
      if (j != -1) {
        paramSet.add(new UidRange(j, k));
      }
    }
    do
    {
      return;
      if (paramList2 == null) {
        break;
      }
      paramList1 = UidRange.createForUser(paramInt);
      i = paramList1.start;
      paramList2 = getAppsUids(paramList2, paramInt).iterator();
      paramInt = i;
      while (paramList2.hasNext())
      {
        i = ((Integer)paramList2.next()).intValue();
        if (i == paramInt)
        {
          paramInt += 1;
        }
        else
        {
          paramSet.add(new UidRange(paramInt, i - 1));
          paramInt = i + 1;
        }
      }
    } while (paramInt > paramList1.stop);
    paramSet.add(new UidRange(paramInt, paramList1.stop));
    return;
    paramSet.add(UidRange.createForUser(paramInt));
  }
  
  public boolean appliesToUid(int paramInt)
  {
    try
    {
      boolean bool = isRunningLocked();
      if (!bool) {
        return false;
      }
      Iterator localIterator = this.mVpnUsers.iterator();
      while (localIterator.hasNext())
      {
        bool = ((UidRange)localIterator.next()).contains(paramInt);
        if (bool) {
          return true;
        }
      }
      return false;
    }
    finally {}
  }
  
  Set<UidRange> createUserAndRestrictedProfilesRanges(int paramInt, List<String> paramList1, List<String> paramList2)
  {
    localArraySet = new ArraySet();
    addUserToRanges(localArraySet, paramInt, paramList1, paramList2);
    if (canHaveRestrictedProfile(paramInt))
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        Object localObject = UserManager.get(this.mContext).getUsers(true);
        Binder.restoreCallingIdentity(l);
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          UserInfo localUserInfo = (UserInfo)((Iterator)localObject).next();
          if ((localUserInfo.isRestricted()) && (localUserInfo.restrictedProfileParentId == paramInt)) {
            addUserToRanges(localArraySet, localUserInfo.id, paramList1, paramList2);
          }
        }
        return localArraySet;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  public ParcelFileDescriptor establish(VpnConfig paramVpnConfig)
  {
    boolean bool;
    long l;
    try
    {
      localObject1 = UserManager.get(this.mContext);
      int i = Binder.getCallingUid();
      int j = this.mOwnerUID;
      if (i != j) {
        return null;
      }
      bool = isVpnUserPreConsented(this.mPackage);
      if (!bool) {
        return null;
      }
      Intent localIntent = new Intent("android.net.VpnService");
      localIntent.setClassName(this.mPackage, paramVpnConfig.user);
      l = Binder.clearCallingIdentity();
      try
      {
        if (((UserManager)localObject1).getUserInfo(this.mUserHandle).isRestricted()) {
          throw new SecurityException("Restricted users cannot establish VPNs");
        }
      }
      catch (RemoteException localRemoteException)
      {
        throw new SecurityException("Cannot find " + paramVpnConfig.user);
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      localObject1 = AppGlobals.getPackageManager().resolveService(localRemoteException, null, 0, this.mUserHandle);
    }
    finally {}
    if (localObject1 == null) {
      throw new SecurityException("Cannot find " + paramVpnConfig.user);
    }
    if (!"android.permission.BIND_VPN_SERVICE".equals(((ResolveInfo)localObject1).serviceInfo.permission)) {
      throw new SecurityException(paramVpnConfig.user + " does not require " + "android.permission.BIND_VPN_SERVICE");
    }
    Binder.restoreCallingIdentity(l);
    Object localObject1 = this.mConfig;
    String str1 = this.mInterface;
    Connection localConnection = this.mConnection;
    NetworkAgent localNetworkAgent = this.mNetworkAgent;
    this.mNetworkAgent = null;
    Set localSet = this.mVpnUsers;
    ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.adoptFd(jniCreate(paramVpnConfig.mtu));
    String str2;
    try
    {
      updateState(NetworkInfo.DetailedState.CONNECTING, "establish");
      str2 = jniGetName(localParcelFileDescriptor.getFd());
      localObject2 = new StringBuilder();
      Iterator localIterator = paramVpnConfig.addresses.iterator();
      while (localIterator.hasNext())
      {
        LinkAddress localLinkAddress = (LinkAddress)localIterator.next();
        ((StringBuilder)localObject2).append(" ").append(localLinkAddress);
      }
      if (jniSetAddresses(str2, ((StringBuilder)localObject2).toString()) >= 1) {
        break label460;
      }
    }
    catch (RuntimeException paramVpnConfig)
    {
      IoUtils.closeQuietly(localParcelFileDescriptor);
      agentDisconnect();
      this.mConfig = ((VpnConfig)localObject1);
      this.mConnection = localConnection;
      this.mVpnUsers = localSet;
      this.mNetworkAgent = localNetworkAgent;
      this.mInterface = str1;
      throw paramVpnConfig;
    }
    throw new IllegalArgumentException("At least one address must be specified");
    label460:
    Object localObject2 = new Connection(null);
    if (!this.mContext.bindServiceAsUser(localRemoteException, (ServiceConnection)localObject2, 67108865, new UserHandle(this.mUserHandle))) {
      throw new IllegalStateException("Cannot bind " + paramVpnConfig.user);
    }
    this.mConnection = ((Connection)localObject2);
    this.mInterface = str2;
    paramVpnConfig.user = this.mPackage;
    paramVpnConfig.interfaze = this.mInterface;
    paramVpnConfig.startTime = SystemClock.elapsedRealtime();
    this.mConfig = paramVpnConfig;
    agentConnect();
    if (localConnection != null) {
      this.mContext.unbindService(localConnection);
    }
    agentDisconnect(localNetworkAgent);
    if (str1 != null)
    {
      bool = str1.equals(str2);
      if (!bool) {
        break label672;
      }
    }
    for (;;)
    {
      try
      {
        IoUtils.setBlocking(localParcelFileDescriptor.getFileDescriptor(), paramVpnConfig.blocking);
        Log.i("Vpn", "Established by " + paramVpnConfig.user + " on " + this.mInterface);
        return localParcelFileDescriptor;
      }
      catch (IOException localIOException)
      {
        label672:
        throw new IllegalStateException("Cannot set tunnel's fd as blocking=" + paramVpnConfig.blocking, localIOException);
      }
      jniReset(str1);
    }
  }
  
  /* Error */
  public String getAlwaysOnPackage()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 945	com/android/server/connectivity/Vpn:enforceControlPermissionOrInternalCaller	()V
    //   6: aload_0
    //   7: getfield 161	com/android/server/connectivity/Vpn:mAlwaysOn	Z
    //   10: ifeq +12 -> 22
    //   13: aload_0
    //   14: getfield 183	com/android/server/connectivity/Vpn:mPackage	Ljava/lang/String;
    //   17: astore_1
    //   18: aload_0
    //   19: monitorexit
    //   20: aload_1
    //   21: areturn
    //   22: aconst_null
    //   23: astore_1
    //   24: goto -6 -> 18
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	Vpn
    //   17	7	1	str	String
    //   27	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	18	27	finally
  }
  
  public VpnConfig getLegacyVpnConfig()
  {
    if (this.mLegacyVpnRunner != null) {
      return this.mConfig;
    }
    return null;
  }
  
  public LegacyVpnInfo getLegacyVpnInfo()
  {
    try
    {
      enforceControlPermission();
      LegacyVpnInfo localLegacyVpnInfo = getLegacyVpnInfoPrivileged();
      return localLegacyVpnInfo;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public LegacyVpnInfo getLegacyVpnInfoPrivileged()
  {
    try
    {
      Object localObject1 = this.mLegacyVpnRunner;
      if (localObject1 == null) {
        return null;
      }
      localObject1 = new LegacyVpnInfo();
      ((LegacyVpnInfo)localObject1).key = this.mConfig.user;
      ((LegacyVpnInfo)localObject1).state = LegacyVpnInfo.stateFromNetworkInfo(this.mNetworkInfo);
      if (this.mNetworkInfo.isConnected()) {
        ((LegacyVpnInfo)localObject1).intent = this.mStatusIntent;
      }
      return (LegacyVpnInfo)localObject1;
    }
    finally {}
  }
  
  public int getNetId()
  {
    if (this.mNetworkAgent != null) {
      return this.mNetworkAgent.netId;
    }
    return 0;
  }
  
  public NetworkInfo getNetworkInfo()
  {
    return this.mNetworkInfo;
  }
  
  public Network[] getUnderlyingNetworks()
  {
    try
    {
      boolean bool = isRunningLocked();
      if (!bool) {
        return null;
      }
      Network[] arrayOfNetwork = this.mConfig.underlyingNetworks;
      return arrayOfNetwork;
    }
    finally {}
  }
  
  public VpnConfig getVpnConfig()
  {
    enforceControlPermission();
    return this.mConfig;
  }
  
  public VpnInfo getVpnInfo()
  {
    try
    {
      boolean bool = isRunningLocked();
      if (!bool) {
        return null;
      }
      VpnInfo localVpnInfo = new VpnInfo();
      localVpnInfo.ownerUid = this.mOwnerUID;
      localVpnInfo.vpnIface = this.mInterface;
      return localVpnInfo;
    }
    finally {}
  }
  
  /* Error */
  @Deprecated
  public void interfaceStatusChanged(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 101	com/android/server/connectivity/Vpn:mObserver	Landroid/net/INetworkManagementEventObserver;
    //   6: aload_1
    //   7: iload_2
    //   8: invokeinterface 1001 3 0
    //   13: aload_0
    //   14: monitorexit
    //   15: return
    //   16: astore_1
    //   17: aload_0
    //   18: monitorexit
    //   19: aload_1
    //   20: athrow
    //   21: astore_1
    //   22: goto -9 -> 13
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	Vpn
    //   0	25	1	paramString	String
    //   0	25	2	paramBoolean	boolean
    // Exception table:
    //   from	to	target	type
    //   2	13	16	finally
    //   2	13	21	android/os/RemoteException
  }
  
  public boolean isBlockingUid(int paramInt)
  {
    boolean bool1 = false;
    try
    {
      boolean bool2 = this.mLockdown;
      if (!bool2) {
        return false;
      }
      if (this.mNetworkInfo.isConnected())
      {
        bool2 = appliesToUid(paramInt);
        if (bool2) {}
        for (;;)
        {
          return bool1;
          bool1 = true;
        }
      }
      Iterator localIterator = this.mBlockedUsers.iterator();
      while (localIterator.hasNext())
      {
        bool1 = ((UidRange)localIterator.next()).contains(paramInt);
        if (bool1) {
          return true;
        }
      }
      return false;
    }
    finally {}
  }
  
  /* Error */
  public void onUserAdded(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   4: invokestatic 335	android/os/UserManager:get	(Landroid/content/Context;)Landroid/os/UserManager;
    //   7: iload_1
    //   8: invokevirtual 831	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   11: astore_2
    //   12: aload_2
    //   13: invokevirtual 802	android/content/pm/UserInfo:isRestricted	()Z
    //   16: ifeq +103 -> 119
    //   19: aload_2
    //   20: getfield 805	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   23: aload_0
    //   24: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   27: if_icmpne +92 -> 119
    //   30: aload_0
    //   31: monitorenter
    //   32: aload_0
    //   33: getfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   36: astore_2
    //   37: aload_2
    //   38: ifnull +64 -> 102
    //   41: aload_0
    //   42: aload_0
    //   43: getfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   46: iload_1
    //   47: aload_0
    //   48: getfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   51: getfield 276	com/android/internal/net/VpnConfig:allowedApplications	Ljava/util/List;
    //   54: aload_0
    //   55: getfield 72	com/android/server/connectivity/Vpn:mConfig	Lcom/android/internal/net/VpnConfig;
    //   58: getfield 279	com/android/internal/net/VpnConfig:disallowedApplications	Ljava/util/List;
    //   61: invokevirtual 792	com/android/server/connectivity/Vpn:addUserToRanges	(Ljava/util/Set;ILjava/util/List;Ljava/util/List;)V
    //   64: aload_0
    //   65: getfield 268	com/android/server/connectivity/Vpn:mNetworkAgent	Landroid/net/NetworkAgent;
    //   68: ifnull +34 -> 102
    //   71: aload_0
    //   72: iload_1
    //   73: invokespecial 656	com/android/server/connectivity/Vpn:uidRangesForUser	(I)Ljava/util/List;
    //   76: astore_2
    //   77: aload_0
    //   78: getfield 268	com/android/server/connectivity/Vpn:mNetworkAgent	Landroid/net/NetworkAgent;
    //   81: aload_2
    //   82: aload_2
    //   83: invokeinterface 659 1 0
    //   88: anewarray 291	android/net/UidRange
    //   91: invokeinterface 660 2 0
    //   96: checkcast 297	[Landroid/net/UidRange;
    //   99: invokevirtual 303	android/net/NetworkAgent:addUidRanges	([Landroid/net/UidRange;)V
    //   102: aload_0
    //   103: getfield 161	com/android/server/connectivity/Vpn:mAlwaysOn	Z
    //   106: ifeq +11 -> 117
    //   109: aload_0
    //   110: aload_0
    //   111: getfield 163	com/android/server/connectivity/Vpn:mLockdown	Z
    //   114: invokespecial 1008	com/android/server/connectivity/Vpn:setVpnForcedLocked	(Z)V
    //   117: aload_0
    //   118: monitorexit
    //   119: return
    //   120: astore_2
    //   121: ldc 28
    //   123: ldc_w 1010
    //   126: aload_2
    //   127: invokestatic 222	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   130: pop
    //   131: goto -29 -> 102
    //   134: astore_2
    //   135: aload_0
    //   136: monitorexit
    //   137: aload_2
    //   138: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	Vpn
    //   0	139	1	paramInt	int
    //   11	72	2	localObject1	Object
    //   120	7	2	localException	Exception
    //   134	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   41	102	120	java/lang/Exception
    //   32	37	134	finally
    //   41	102	134	finally
    //   102	117	134	finally
    //   121	131	134	finally
  }
  
  /* Error */
  public void onUserRemoved(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   4: invokestatic 335	android/os/UserManager:get	(Landroid/content/Context;)Landroid/os/UserManager;
    //   7: iload_1
    //   8: invokevirtual 831	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   11: astore_2
    //   12: aload_2
    //   13: invokevirtual 802	android/content/pm/UserInfo:isRestricted	()Z
    //   16: ifeq +47 -> 63
    //   19: aload_2
    //   20: getfield 805	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   23: aload_0
    //   24: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   27: if_icmpne +36 -> 63
    //   30: aload_0
    //   31: monitorenter
    //   32: aload_0
    //   33: getfield 121	com/android/server/connectivity/Vpn:mVpnUsers	Ljava/util/Set;
    //   36: astore_2
    //   37: aload_2
    //   38: ifnull +8 -> 46
    //   41: aload_0
    //   42: iload_1
    //   43: invokespecial 1013	com/android/server/connectivity/Vpn:removeVpnUserLocked	(I)V
    //   46: aload_0
    //   47: getfield 161	com/android/server/connectivity/Vpn:mAlwaysOn	Z
    //   50: ifeq +11 -> 61
    //   53: aload_0
    //   54: aload_0
    //   55: getfield 163	com/android/server/connectivity/Vpn:mLockdown	Z
    //   58: invokespecial 1008	com/android/server/connectivity/Vpn:setVpnForcedLocked	(Z)V
    //   61: aload_0
    //   62: monitorexit
    //   63: return
    //   64: astore_2
    //   65: ldc 28
    //   67: ldc_w 1015
    //   70: aload_2
    //   71: invokestatic 222	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   74: pop
    //   75: goto -29 -> 46
    //   78: astore_2
    //   79: aload_0
    //   80: monitorexit
    //   81: aload_2
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	Vpn
    //   0	83	1	paramInt	int
    //   11	27	2	localObject1	Object
    //   64	7	2	localException	Exception
    //   78	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   41	46	64	java/lang/Exception
    //   32	37	78	finally
    //   41	46	78	finally
    //   46	61	78	finally
    //   65	75	78	finally
  }
  
  public void onUserStopped()
  {
    try
    {
      setVpnForcedLocked(false);
      this.mAlwaysOn = false;
      unregisterPackageChangeReceiverLocked();
      agentDisconnect();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean prepare(String paramString1, String paramString2)
  {
    if (paramString1 != null) {}
    try
    {
      if ((!this.mAlwaysOn) || (isCurrentPreparedPackage(paramString1)))
      {
        if (isCurrentPreparedPackage(paramString1)) {
          break label63;
        }
        if ((!paramString1.equals("[Legacy VPN]")) && (isVpnUserPreConsented(paramString1)))
        {
          prepareInternal(paramString1);
          return true;
        }
      }
      else
      {
        return false;
      }
      return false;
      label63:
      if ((paramString1.equals("[Legacy VPN]")) || (isVpnUserPreConsented(paramString1)))
      {
        if (paramString2 != null)
        {
          if (!paramString2.equals("[Legacy VPN]"))
          {
            boolean bool = isCurrentPreparedPackage(paramString2);
            if (!bool) {}
          }
        }
        else {
          return true;
        }
      }
      else
      {
        prepareInternal("[Legacy VPN]");
        return false;
      }
      enforceControlPermission();
      if ((!this.mAlwaysOn) || (isCurrentPreparedPackage(paramString2)))
      {
        prepareInternal(paramString2);
        return true;
      }
      return false;
    }
    finally {}
  }
  
  public boolean removeAddress(String paramString, int paramInt)
  {
    try
    {
      boolean bool = isCallerEstablishedOwnerLocked();
      if (!bool) {
        return false;
      }
      bool = jniDelAddress(this.mInterface, paramString, paramInt);
      this.mNetworkAgent.sendLinkProperties(makeLinkProperties());
      return bool;
    }
    finally {}
  }
  
  /* Error */
  public void saveAlwaysOnPackage()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: invokestatic 263	android/os/Binder:clearCallingIdentity	()J
    //   5: lstore_2
    //   6: aload_0
    //   7: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   10: invokevirtual 1026	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   13: astore 4
    //   15: aload 4
    //   17: ldc_w 1028
    //   20: aload_0
    //   21: invokevirtual 1030	com/android/server/connectivity/Vpn:getAlwaysOnPackage	()Ljava/lang/String;
    //   24: aload_0
    //   25: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   28: invokestatic 1036	android/provider/Settings$Secure:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   31: pop
    //   32: aload_0
    //   33: getfield 163	com/android/server/connectivity/Vpn:mLockdown	Z
    //   36: ifeq +26 -> 62
    //   39: iconst_1
    //   40: istore_1
    //   41: aload 4
    //   43: ldc_w 1038
    //   46: iload_1
    //   47: aload_0
    //   48: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   51: invokestatic 1042	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
    //   54: pop
    //   55: lload_2
    //   56: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   59: aload_0
    //   60: monitorexit
    //   61: return
    //   62: iconst_0
    //   63: istore_1
    //   64: goto -23 -> 41
    //   67: astore 4
    //   69: lload_2
    //   70: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   73: aload 4
    //   75: athrow
    //   76: astore 4
    //   78: aload_0
    //   79: monitorexit
    //   80: aload 4
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	Vpn
    //   40	24	1	i	int
    //   5	65	2	l	long
    //   13	29	4	localContentResolver	android.content.ContentResolver
    //   67	7	4	localObject1	Object
    //   76	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   6	39	67	finally
    //   41	55	67	finally
    //   2	6	76	finally
    //   55	59	76	finally
    //   69	76	76	finally
  }
  
  public boolean setAlwaysOnPackage(String paramString, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        enforceControlPermissionOrInternalCaller();
        if ("[Legacy VPN]".equals(paramString))
        {
          Log.w("Vpn", "Not setting legacy VPN \"" + paramString + "\" as always-on.");
          return false;
        }
        if (paramString != null)
        {
          boolean bool = setPackageAuthorization(paramString, true);
          if (!bool) {
            return false;
          }
          this.mAlwaysOn = true;
          if (this.mAlwaysOn)
          {
            this.mLockdown = paramBoolean;
            if (!isCurrentPreparedPackage(paramString)) {
              prepareInternal(paramString);
            }
            maybeRegisterPackageChangeReceiverLocked(paramString);
            setVpnForcedLocked(this.mLockdown);
            return true;
          }
        }
        else
        {
          paramString = "[Legacy VPN]";
          this.mAlwaysOn = false;
          continue;
        }
        paramBoolean = false;
      }
      finally {}
    }
  }
  
  public void setEnableTeardown(boolean paramBoolean)
  {
    this.mEnableTeardown = paramBoolean;
  }
  
  /* Error */
  public boolean setPackageAuthorization(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 945	com/android/server/connectivity/Vpn:enforceControlPermissionOrInternalCaller	()V
    //   4: aload_0
    //   5: aload_1
    //   6: aload_0
    //   7: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   10: invokespecial 187	com/android/server/connectivity/Vpn:getAppUid	(Ljava/lang/String;I)I
    //   13: istore 4
    //   15: iload 4
    //   17: iconst_m1
    //   18: if_icmpeq +12 -> 30
    //   21: ldc -75
    //   23: aload_1
    //   24: invokevirtual 399	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   27: ifeq +5 -> 32
    //   30: iconst_0
    //   31: ireturn
    //   32: invokestatic 263	android/os/Binder:clearCallingIdentity	()J
    //   35: lstore 5
    //   37: aload_0
    //   38: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   41: ldc_w 444
    //   44: invokevirtual 448	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   47: checkcast 450	android/app/AppOpsManager
    //   50: astore 7
    //   52: iload_2
    //   53: ifeq +23 -> 76
    //   56: iconst_0
    //   57: istore_3
    //   58: aload 7
    //   60: bipush 47
    //   62: iload 4
    //   64: aload_1
    //   65: iload_3
    //   66: invokevirtual 1059	android/app/AppOpsManager:setMode	(IILjava/lang/String;I)V
    //   69: lload 5
    //   71: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   74: iconst_1
    //   75: ireturn
    //   76: iconst_1
    //   77: istore_3
    //   78: goto -20 -> 58
    //   81: astore 7
    //   83: ldc 28
    //   85: new 528	java/lang/StringBuilder
    //   88: dup
    //   89: invokespecial 529	java/lang/StringBuilder:<init>	()V
    //   92: ldc_w 1061
    //   95: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: aload_1
    //   99: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: ldc_w 1063
    //   105: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   108: iload 4
    //   110: invokevirtual 636	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   113: invokevirtual 543	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   116: aload 7
    //   118: invokestatic 222	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   121: pop
    //   122: lload 5
    //   124: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   127: iconst_0
    //   128: ireturn
    //   129: astore_1
    //   130: lload 5
    //   132: invokestatic 272	android/os/Binder:restoreCallingIdentity	(J)V
    //   135: aload_1
    //   136: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	this	Vpn
    //   0	137	1	paramString	String
    //   0	137	2	paramBoolean	boolean
    //   57	21	3	i	int
    //   13	96	4	j	int
    //   35	96	5	l	long
    //   50	9	7	localAppOpsManager	AppOpsManager
    //   81	36	7	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   37	52	81	java/lang/Exception
    //   58	69	81	java/lang/Exception
    //   37	52	129	finally
    //   58	69	129	finally
    //   83	122	129	finally
  }
  
  public boolean setUnderlyingNetworks(Network[] paramArrayOfNetwork)
  {
    for (;;)
    {
      int i;
      try
      {
        boolean bool = isCallerEstablishedOwnerLocked();
        if (!bool) {
          return false;
        }
        if (paramArrayOfNetwork == null)
        {
          this.mConfig.underlyingNetworks = null;
          return true;
        }
        this.mConfig.underlyingNetworks = new Network[paramArrayOfNetwork.length];
        i = 0;
        if (i >= paramArrayOfNetwork.length) {
          continue;
        }
        if (paramArrayOfNetwork[i] == null) {
          this.mConfig.underlyingNetworks[i] = null;
        } else {
          this.mConfig.underlyingNetworks[i] = new Network(paramArrayOfNetwork[i].netId);
        }
      }
      finally {}
      i += 1;
    }
  }
  
  /* Error */
  public boolean startAlwaysOnVpn()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 1030	com/android/server/connectivity/Vpn:getAlwaysOnPackage	()Ljava/lang/String;
    //   6: astore_3
    //   7: aload_3
    //   8: ifnonnull +7 -> 15
    //   11: aload_0
    //   12: monitorexit
    //   13: iconst_1
    //   14: ireturn
    //   15: aload_0
    //   16: invokevirtual 1072	com/android/server/connectivity/Vpn:getNetworkInfo	()Landroid/net/NetworkInfo;
    //   19: invokevirtual 314	android/net/NetworkInfo:isConnected	()Z
    //   22: istore_1
    //   23: iload_1
    //   24: ifeq +7 -> 31
    //   27: aload_0
    //   28: monitorexit
    //   29: iconst_1
    //   30: ireturn
    //   31: aload_0
    //   32: monitorexit
    //   33: new 817	android/content/Intent
    //   36: dup
    //   37: ldc_w 819
    //   40: invokespecial 820	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   43: astore_2
    //   44: aload_2
    //   45: aload_3
    //   46: invokevirtual 1076	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   49: pop
    //   50: aload_0
    //   51: getfield 81	com/android/server/connectivity/Vpn:mContext	Landroid/content/Context;
    //   54: aload_2
    //   55: aload_0
    //   56: getfield 105	com/android/server/connectivity/Vpn:mUserHandle	I
    //   59: invokestatic 580	android/os/UserHandle:of	(I)Landroid/os/UserHandle;
    //   62: invokevirtual 1080	android/content/Context:startServiceAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)Landroid/content/ComponentName;
    //   65: astore_3
    //   66: aload_3
    //   67: ifnull +10 -> 77
    //   70: iconst_1
    //   71: ireturn
    //   72: astore_2
    //   73: aload_0
    //   74: monitorexit
    //   75: aload_2
    //   76: athrow
    //   77: iconst_0
    //   78: ireturn
    //   79: astore_3
    //   80: ldc 28
    //   82: new 528	java/lang/StringBuilder
    //   85: dup
    //   86: invokespecial 529	java/lang/StringBuilder:<init>	()V
    //   89: ldc_w 1082
    //   92: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: aload_2
    //   96: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   99: ldc_w 1084
    //   102: invokevirtual 536	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: invokevirtual 543	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   108: aload_3
    //   109: invokestatic 701	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   112: pop
    //   113: iconst_0
    //   114: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	Vpn
    //   22	2	1	bool	boolean
    //   43	12	2	localIntent	Intent
    //   72	24	2	localObject1	Object
    //   6	61	3	localObject2	Object
    //   79	30	3	localRuntimeException	RuntimeException
    // Exception table:
    //   from	to	target	type
    //   2	7	72	finally
    //   15	23	72	finally
    //   50	66	79	java/lang/RuntimeException
  }
  
  public void startLegacyVpn(VpnProfile paramVpnProfile, KeyStore paramKeyStore, LinkProperties paramLinkProperties)
  {
    enforceControlPermission();
    long l = Binder.clearCallingIdentity();
    try
    {
      startLegacyVpnPrivileged(paramVpnProfile, paramKeyStore, paramLinkProperties);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void startLegacyVpnPrivileged(VpnProfile paramVpnProfile, KeyStore paramKeyStore, LinkProperties paramLinkProperties)
  {
    Object localObject = UserManager.get(this.mContext);
    if ((((UserManager)localObject).getUserInfo(this.mUserHandle).isRestricted()) || (((UserManager)localObject).hasUserRestriction("no_config_vpn", new UserHandle(this.mUserHandle)))) {
      throw new SecurityException("Restricted users cannot establish VPNs");
    }
    paramLinkProperties = findIPv4DefaultRoute(paramLinkProperties);
    String str4 = paramLinkProperties.getGateway().getHostAddress();
    String str3 = paramLinkProperties.getInterface();
    String str2 = "";
    paramLinkProperties = "";
    localObject = "";
    String str1 = "";
    if (!paramVpnProfile.ipsecUserCert.isEmpty())
    {
      str2 = "USRPKEY_" + paramVpnProfile.ipsecUserCert;
      paramLinkProperties = paramKeyStore.get("USRCERT_" + paramVpnProfile.ipsecUserCert);
      if (paramLinkProperties == null) {
        paramLinkProperties = null;
      }
    }
    else
    {
      if (!paramVpnProfile.ipsecCaCert.isEmpty())
      {
        localObject = paramKeyStore.get("CACERT_" + paramVpnProfile.ipsecCaCert);
        if (localObject != null) {
          break label288;
        }
        localObject = null;
      }
      label208:
      if (!paramVpnProfile.ipsecServerCert.isEmpty())
      {
        paramKeyStore = paramKeyStore.get("USRCERT_" + paramVpnProfile.ipsecServerCert);
        if (paramKeyStore != null) {
          break label305;
        }
        str1 = null;
      }
      label253:
      if ((str2 != null) && (paramLinkProperties != null)) {
        break label321;
      }
    }
    label288:
    label305:
    label321:
    while ((localObject == null) || (str1 == null))
    {
      throw new IllegalStateException("Cannot load credentials");
      paramLinkProperties = new String(paramLinkProperties, StandardCharsets.UTF_8);
      break;
      localObject = new String((byte[])localObject, StandardCharsets.UTF_8);
      break label208;
      str1 = new String(paramKeyStore, StandardCharsets.UTF_8);
      break label253;
    }
    paramKeyStore = null;
    switch (paramVpnProfile.type)
    {
    default: 
      paramLinkProperties = null;
      switch (paramVpnProfile.type)
      {
      }
      break;
    }
    for (;;)
    {
      localObject = new VpnConfig();
      ((VpnConfig)localObject).legacy = true;
      ((VpnConfig)localObject).user = paramVpnProfile.key;
      ((VpnConfig)localObject).interfaze = str3;
      ((VpnConfig)localObject).session = paramVpnProfile.name;
      ((VpnConfig)localObject).addLegacyRoutes(paramVpnProfile.routes);
      if (!paramVpnProfile.dnsServers.isEmpty()) {
        ((VpnConfig)localObject).dnsServers = Arrays.asList(paramVpnProfile.dnsServers.split(" +"));
      }
      if (!paramVpnProfile.searchDomains.isEmpty()) {
        ((VpnConfig)localObject).searchDomains = Arrays.asList(paramVpnProfile.searchDomains.split(" +"));
      }
      startLegacyVpn((VpnConfig)localObject, paramKeyStore, paramLinkProperties);
      return;
      paramKeyStore = new String[6];
      paramKeyStore[0] = str3;
      paramKeyStore[1] = paramVpnProfile.server;
      paramKeyStore[2] = "udppsk";
      paramKeyStore[3] = paramVpnProfile.ipsecIdentifier;
      paramKeyStore[4] = paramVpnProfile.ipsecSecret;
      paramKeyStore[5] = "1701";
      break;
      paramKeyStore = new String[8];
      paramKeyStore[0] = str3;
      paramKeyStore[1] = paramVpnProfile.server;
      paramKeyStore[2] = "udprsa";
      paramKeyStore[3] = str2;
      paramKeyStore[4] = paramLinkProperties;
      paramKeyStore[5] = localObject;
      paramKeyStore[6] = str1;
      paramKeyStore[7] = "1701";
      break;
      paramKeyStore = new String[9];
      paramKeyStore[0] = str3;
      paramKeyStore[1] = paramVpnProfile.server;
      paramKeyStore[2] = "xauthpsk";
      paramKeyStore[3] = paramVpnProfile.ipsecIdentifier;
      paramKeyStore[4] = paramVpnProfile.ipsecSecret;
      paramKeyStore[5] = paramVpnProfile.username;
      paramKeyStore[6] = paramVpnProfile.password;
      paramKeyStore[7] = "";
      paramKeyStore[8] = str4;
      break;
      paramKeyStore = new String[11];
      paramKeyStore[0] = str3;
      paramKeyStore[1] = paramVpnProfile.server;
      paramKeyStore[2] = "xauthrsa";
      paramKeyStore[3] = str2;
      paramKeyStore[4] = paramLinkProperties;
      paramKeyStore[5] = localObject;
      paramKeyStore[6] = str1;
      paramKeyStore[7] = paramVpnProfile.username;
      paramKeyStore[8] = paramVpnProfile.password;
      paramKeyStore[9] = "";
      paramKeyStore[10] = str4;
      break;
      paramKeyStore = new String[9];
      paramKeyStore[0] = str3;
      paramKeyStore[1] = paramVpnProfile.server;
      paramKeyStore[2] = "hybridrsa";
      paramKeyStore[3] = localObject;
      paramKeyStore[4] = str1;
      paramKeyStore[5] = paramVpnProfile.username;
      paramKeyStore[6] = paramVpnProfile.password;
      paramKeyStore[7] = "";
      paramKeyStore[8] = str4;
      break;
      localObject = new String[20];
      localObject[0] = str3;
      localObject[1] = "pptp";
      localObject[2] = paramVpnProfile.server;
      localObject[3] = "1723";
      localObject[4] = "name";
      localObject[5] = paramVpnProfile.username;
      localObject[6] = "password";
      localObject[7] = paramVpnProfile.password;
      localObject[8] = "linkname";
      localObject[9] = "vpn";
      localObject[10] = "refuse-eap";
      localObject[11] = "nodefaultroute";
      localObject[12] = "usepeerdns";
      localObject[13] = "idle";
      localObject[14] = "1800";
      localObject[15] = "mtu";
      localObject[16] = "1400";
      localObject[17] = "mru";
      localObject[18] = "1400";
      if (paramVpnProfile.mppe) {}
      for (paramLinkProperties = "+mppe";; paramLinkProperties = "nomppe")
      {
        localObject[19] = paramLinkProperties;
        paramLinkProperties = (LinkProperties)localObject;
        break;
      }
      paramLinkProperties = new String[20];
      paramLinkProperties[0] = str3;
      paramLinkProperties[1] = "l2tp";
      paramLinkProperties[2] = paramVpnProfile.server;
      paramLinkProperties[3] = "1701";
      paramLinkProperties[4] = paramVpnProfile.l2tpSecret;
      paramLinkProperties[5] = "name";
      paramLinkProperties[6] = paramVpnProfile.username;
      paramLinkProperties[7] = "password";
      paramLinkProperties[8] = paramVpnProfile.password;
      paramLinkProperties[9] = "linkname";
      paramLinkProperties[10] = "vpn";
      paramLinkProperties[11] = "refuse-eap";
      paramLinkProperties[12] = "nodefaultroute";
      paramLinkProperties[13] = "usepeerdns";
      paramLinkProperties[14] = "idle";
      paramLinkProperties[15] = "1800";
      paramLinkProperties[16] = "mtu";
      paramLinkProperties[17] = "1400";
      paramLinkProperties[18] = "mru";
      paramLinkProperties[19] = "1400";
    }
  }
  
  public void stopLegacyVpnPrivileged()
  {
    try
    {
      if (this.mLegacyVpnRunner != null)
      {
        this.mLegacyVpnRunner.exit();
        this.mLegacyVpnRunner = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private class Connection
    implements ServiceConnection
  {
    private IBinder mService;
    
    private Connection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.mService = paramIBinder;
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      this.mService = null;
    }
  }
  
  private class LegacyVpnRunner
    extends Thread
  {
    private static final String TAG = "LegacyVpnRunner";
    private final String[][] mArguments;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (!Vpn.-get3(Vpn.this)) {
          return;
        }
        if ((paramAnonymousIntent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) && (paramAnonymousIntent.getIntExtra("networkType", -1) == Vpn.LegacyVpnRunner.-get0(Vpn.LegacyVpnRunner.this).get()))
        {
          paramAnonymousContext = (NetworkInfo)paramAnonymousIntent.getExtra("networkInfo");
          if ((paramAnonymousContext != null) && (!paramAnonymousContext.isConnectedOrConnecting())) {}
        }
        else
        {
          return;
        }
        try
        {
          Vpn.-get7(Vpn.this).interfaceStatusChanged(Vpn.LegacyVpnRunner.-get1(Vpn.LegacyVpnRunner.this), false);
          return;
        }
        catch (RemoteException paramAnonymousContext) {}
      }
    };
    private final String[] mDaemons;
    private final AtomicInteger mOuterConnection = new AtomicInteger(-1);
    private final String mOuterInterface;
    private final LocalSocket[] mSockets;
    private long mTimer = -1L;
    
    public LegacyVpnRunner(VpnConfig paramVpnConfig, String[] paramArrayOfString1, String[] paramArrayOfString2)
    {
      super();
      Vpn.-set0(Vpn.this, paramVpnConfig);
      this.mDaemons = new String[] { "racoon", "mtpd" };
      this.mArguments = new String[][] { paramArrayOfString1, paramArrayOfString2 };
      this.mSockets = new LocalSocket[this.mDaemons.length];
      this.mOuterInterface = Vpn.-get0(Vpn.this).interfaze;
      if (!TextUtils.isEmpty(this.mOuterInterface))
      {
        paramVpnConfig = ConnectivityManager.from(Vpn.-get2(Vpn.this));
        paramArrayOfString1 = paramVpnConfig.getAllNetworks();
        int j = paramArrayOfString1.length;
        while (i < j)
        {
          paramArrayOfString2 = paramArrayOfString1[i];
          LinkProperties localLinkProperties = paramVpnConfig.getLinkProperties(paramArrayOfString2);
          if ((localLinkProperties != null) && (localLinkProperties.getAllInterfaceNames().contains(this.mOuterInterface)))
          {
            paramArrayOfString2 = paramVpnConfig.getNetworkInfo(paramArrayOfString2);
            if (paramArrayOfString2 != null) {
              this.mOuterConnection.set(paramArrayOfString2.getType());
            }
          }
          i += 1;
        }
      }
      paramVpnConfig = new IntentFilter();
      paramVpnConfig.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      Vpn.-get2(Vpn.this).registerReceiver(this.mBroadcastReceiver, paramVpnConfig);
    }
    
    private void checkpoint(boolean paramBoolean)
      throws InterruptedException
    {
      long l = SystemClock.elapsedRealtime();
      if (this.mTimer == -1L)
      {
        this.mTimer = l;
        Thread.sleep(1L);
        return;
      }
      if (l - this.mTimer <= 60000L)
      {
        if (paramBoolean) {}
        for (int i = 200;; i = 1)
        {
          Thread.sleep(i);
          return;
        }
      }
      Vpn.-wrap5(Vpn.this, NetworkInfo.DetailedState.FAILED, "checkpoint");
      throw new IllegalStateException("Time is up");
    }
    
    private void execute()
    {
      int n = 0;
      int m = 0;
      int j = m;
      int i = n;
      int i1;
      Object localObject4;
      for (;;)
      {
        try
        {
          checkpoint(false);
          j = m;
          i = n;
          String[] arrayOfString = this.mDaemons;
          k = 0;
          j = m;
          i = n;
          i1 = arrayOfString.length;
        }
        catch (Exception localException1)
        {
          i = j;
          Log.i("LegacyVpnRunner", "Aborting", localException1);
          i = j;
          Vpn.-wrap5(Vpn.this, NetworkInfo.DetailedState.FAILED, localException1.getMessage());
          i = j;
          exit();
          if (j != 0) {
            break label1770;
          }
          Object localObject1 = this.mDaemons;
          i = 0;
          k = localObject1.length;
          if (i >= k) {
            break label1770;
          }
          SystemService.stop(localObject1[i]);
          i += 1;
          continue;
          k += 1;
          break label1828;
          label152:
          j = m;
          i = n;
          localObject1 = new File("/data/misc/vpn/state");
          j = m;
          i = n;
          ((File)localObject1).delete();
          j = m;
          i = n;
          if (!((File)localObject1).exists()) {
            break;
          }
          j = m;
          i = n;
          throw new IllegalStateException("Cannot delete the state");
        }
        finally
        {
          if (i != 0) {
            break label1798;
          }
        }
        j = m;
        i = n;
        if (!SystemService.isStopped((String)localObject4))
        {
          j = m;
          i = n;
          checkpoint(true);
        }
        else
        {
          localObject4 = this.mDaemons;
          j = 0;
          k = localObject4.length;
        }
      }
      int i2;
      Object localObject3;
      while (j < k)
      {
        SystemService.stop(localObject4[j]);
        j += 1;
        continue;
        j = m;
        i = n;
        new File("/data/misc/vpn/abort").delete();
        i1 = 1;
        n = 1;
        m = 0;
        j = n;
        i = i1;
        localObject4 = this.mArguments;
        k = 0;
        j = n;
        i = i1;
        i2 = localObject4.length;
        i = m;
        j = k;
        break label1843;
        if (i == 0)
        {
          j = n;
          i = i1;
          Vpn.-wrap3(Vpn.this);
          if (1 == 0)
          {
            localObject3 = this.mDaemons;
            i = 0;
            j = localObject3.length;
            while (i < j)
            {
              SystemService.stop(localObject3[i]);
              i += 1;
            }
          }
          if ((1 == 0) || (Vpn.-get6(Vpn.this).getDetailedState() == NetworkInfo.DetailedState.CONNECTING)) {
            Vpn.-wrap3(Vpn.this);
          }
          return;
        }
        j = n;
        i = i1;
        Vpn.-wrap5(Vpn.this, NetworkInfo.DetailedState.CONNECTING, "execute");
        k = 0;
      }
      for (;;)
      {
        j = n;
        i = i1;
        Object localObject6;
        if (k < this.mDaemons.length)
        {
          j = n;
          i = i1;
          localObject4 = this.mArguments[k];
          if (localObject4 != null)
          {
            j = n;
            i = i1;
            localObject6 = this.mDaemons[k];
            j = n;
            i = i1;
            SystemService.start((String)localObject6);
            for (;;)
            {
              j = n;
              i = i1;
              if (SystemService.isRunning((String)localObject6)) {
                break;
              }
              j = n;
              i = i1;
              checkpoint(true);
            }
            j = n;
            i = i1;
            this.mSockets[k] = new LocalSocket();
            j = n;
            i = i1;
            localObject6 = new LocalSocketAddress((String)localObject6, LocalSocketAddress.Namespace.RESERVED);
            i = i1;
            for (;;)
            {
              try
              {
                this.mSockets[k].connect((LocalSocketAddress)localObject6);
                j = n;
                i = i1;
                this.mSockets[k].setSoTimeout(500);
                j = n;
                i = i1;
                localObject6 = this.mSockets[k].getOutputStream();
                m = 0;
                j = n;
                i = i1;
                i2 = localObject4.length;
                if (m >= i2) {
                  break label739;
                }
                j = n;
                i = i1;
                byte[] arrayOfByte = localObject4[m].getBytes(StandardCharsets.UTF_8);
                j = n;
                i = i1;
                if (arrayOfByte.length < 65535) {
                  break label675;
                }
                j = n;
                i = i1;
                throw new IllegalArgumentException("Argument is too large");
              }
              catch (Exception localException3)
              {
                j = n;
                i = i1;
                checkpoint(true);
              }
              break;
              label675:
              j = n;
              i = i1;
              ((OutputStream)localObject6).write(localException3.length >> 8);
              j = n;
              i = i1;
              ((OutputStream)localObject6).write(localException3.length);
              j = n;
              i = i1;
              ((OutputStream)localObject6).write(localException3);
              j = n;
              i = i1;
              checkpoint(false);
              m += 1;
            }
            label739:
            j = n;
            i = i1;
            ((OutputStream)localObject6).write(255);
            j = n;
            i = i1;
            ((OutputStream)localObject6).write(255);
            j = n;
            i = i1;
            ((OutputStream)localObject6).flush();
            j = n;
            i = i1;
            localObject4 = this.mSockets[k].getInputStream();
            for (;;)
            {
              i = i1;
              try
              {
                j = ((InputStream)localObject4).read();
                if (j == -1) {
                  break label1878;
                }
              }
              catch (Exception localException2)
              {
                for (;;)
                {
                  boolean bool;
                  Object localObject5;
                  continue;
                  k += 1;
                }
              }
              j = n;
              i = i1;
              checkpoint(true);
            }
          }
        }
        else
        {
          do
          {
            j = n;
            i = i1;
            checkpoint(true);
            j = n;
            i = i1;
            if (((File)localObject3).exists()) {
              break;
            }
            k = 0;
            j = n;
            i = i1;
          } while (k >= this.mDaemons.length);
          j = n;
          i = i1;
          localObject4 = this.mDaemons[k];
          j = n;
          i = i1;
          if (this.mArguments[k] == null) {
            break label1890;
          }
          j = n;
          i = i1;
          if (SystemService.isRunning((String)localObject4)) {
            break label1890;
          }
          j = n;
          i = i1;
          throw new IllegalStateException((String)localObject4 + " is dead");
          j = n;
          i = i1;
          localObject3 = FileUtils.readTextFile((File)localObject3, 0, null).split("\n", -1);
          j = n;
          i = i1;
          if (localObject3.length != 7)
          {
            j = n;
            i = i1;
            throw new IllegalStateException("Cannot parse the state");
          }
          j = n;
          i = i1;
          Vpn.-get0(Vpn.this).interfaze = localObject3[0].trim();
          j = n;
          i = i1;
          Vpn.-get0(Vpn.this).addLegacyAddresses(localObject3[1]);
          j = n;
          i = i1;
          if (Vpn.-get0(Vpn.this).routes != null)
          {
            j = n;
            i = i1;
            if (!Vpn.-get0(Vpn.this).routes.isEmpty()) {}
          }
          else
          {
            j = n;
            i = i1;
            Vpn.-get0(Vpn.this).addLegacyRoutes(localObject3[2]);
          }
          j = n;
          i = i1;
          if (Vpn.-get0(Vpn.this).dnsServers != null)
          {
            j = n;
            i = i1;
            if (Vpn.-get0(Vpn.this).dnsServers.size() != 0) {}
          }
          else
          {
            j = n;
            i = i1;
            localObject4 = localObject3[3].trim();
            j = n;
            i = i1;
            if (!((String)localObject4).isEmpty())
            {
              j = n;
              i = i1;
              Vpn.-get0(Vpn.this).dnsServers = Arrays.asList(((String)localObject4).split(" "));
            }
          }
          j = n;
          i = i1;
          if (Vpn.-get0(Vpn.this).searchDomains != null)
          {
            j = n;
            i = i1;
            if (Vpn.-get0(Vpn.this).searchDomains.size() != 0) {}
          }
          else
          {
            j = n;
            i = i1;
            localObject4 = localObject3[4].trim();
            j = n;
            i = i1;
            if (!((String)localObject4).isEmpty())
            {
              j = n;
              i = i1;
              Vpn.-get0(Vpn.this).searchDomains = Arrays.asList(((String)localObject4).split(" "));
            }
          }
          localObject3 = localObject3[5];
          j = n;
          i = i1;
          bool = ((String)localObject3).isEmpty();
          if (!bool)
          {
            j = n;
            i = i1;
          }
          for (;;)
          {
            try
            {
              localObject4 = InetAddress.parseNumericAddress((String)localObject3);
              j = n;
              i = i1;
              if (!(localObject4 instanceof Inet4Address)) {
                continue;
              }
              j = n;
              i = i1;
              Vpn.-get0(Vpn.this).routes.add(new RouteInfo(new IpPrefix((InetAddress)localObject4, 32), 9));
            }
            catch (IllegalArgumentException localIllegalArgumentException)
            {
              j = n;
              i = i1;
              Log.e("LegacyVpnRunner", "Exception constructing throw route to " + (String)localObject3 + ": " + localIllegalArgumentException);
              continue;
              j = n;
              i = i1;
              Log.e("LegacyVpnRunner", "Unknown IP address family for VPN endpoint: " + (String)localObject3);
              continue;
              Vpn.-set2(Vpn.this, Vpn.-get0(Vpn.this).interfaze);
              Vpn.-wrap4(Vpn.this);
              Vpn.-wrap2(Vpn.this);
              Log.i("LegacyVpnRunner", "Connected!");
              j = n;
              i = i1;
              if (1 != 0) {
                continue;
              }
              localObject3 = this.mDaemons;
              i = 0;
              j = localObject3.length;
              if (i >= j) {
                continue;
              }
              SystemService.stop(localObject3[i]);
              i += 1;
              continue;
              if ((1 != 0) && (Vpn.-get6(Vpn.this).getDetailedState() != NetworkInfo.DetailedState.CONNECTING)) {
                break label1769;
              }
              Vpn.-wrap3(Vpn.this);
            }
            j = n;
            i = i1;
            localObject3 = Vpn.this;
            j = n;
            i = i1;
            try
            {
              Vpn.-get0(Vpn.this).startTime = SystemClock.elapsedRealtime();
              checkpoint(false);
              if (Vpn.-wrap1(Vpn.this, Vpn.-get0(Vpn.this).interfaze) != 0) {
                continue;
              }
              throw new IllegalStateException(Vpn.-get0(Vpn.this).interfaze + " is gone");
            }
            finally
            {
              j = n;
              i = i1;
              j = n;
              i = i1;
            }
            j = n;
            i = i1;
            if (!(localInetAddress instanceof Inet6Address)) {
              continue;
            }
            j = n;
            i = i1;
            Vpn.-get0(Vpn.this).routes.add(new RouteInfo(new IpPrefix(localInetAddress, 128), 9));
          }
          label1769:
          label1770:
          do
          {
            return;
          } while ((j != 0) && (Vpn.-get6(Vpn.this).getDetailedState() != NetworkInfo.DetailedState.CONNECTING));
          Vpn.-wrap3(Vpn.this);
          return;
          label1798:
          if ((i == 0) || (Vpn.-get6(Vpn.this).getDetailedState() == NetworkInfo.DetailedState.CONNECTING)) {
            Vpn.-wrap3(Vpn.this);
          }
          throw ((Throwable)localObject3);
          label1828:
          if (k >= i1) {
            break label152;
          }
          localObject5 = localObject3[k];
          break;
          label1843:
          if (j < i2)
          {
            localObject6 = localObject5[j];
            if ((i == 0) && (localObject6 == null)) {
              break label1873;
            }
          }
          label1873:
          for (i = 1;; i = 0)
          {
            j += 1;
            break label1843;
            break;
          }
        }
        label1878:
        k += 1;
      }
    }
    
    /* Error */
    private void monitorDaemons()
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_3
      //   2: iconst_0
      //   3: istore 4
      //   5: iconst_0
      //   6: istore_2
      //   7: aload_0
      //   8: getfield 40	com/android/server/connectivity/Vpn$LegacyVpnRunner:this$0	Lcom/android/server/connectivity/Vpn;
      //   11: invokestatic 237	com/android/server/connectivity/Vpn:-get6	(Lcom/android/server/connectivity/Vpn;)Landroid/net/NetworkInfo;
      //   14: invokevirtual 433	android/net/NetworkInfo:isConnected	()Z
      //   17: ifne +4 -> 21
      //   20: return
      //   21: ldc2_w 434
      //   24: invokestatic 163	java/lang/Thread:sleep	(J)V
      //   27: iconst_0
      //   28: istore_1
      //   29: iload_1
      //   30: aload_0
      //   31: getfield 69	com/android/server/connectivity/Vpn$LegacyVpnRunner:mDaemons	[Ljava/lang/String;
      //   34: arraylength
      //   35: if_icmpge -14 -> 21
      //   38: aload_0
      //   39: getfield 72	com/android/server/connectivity/Vpn$LegacyVpnRunner:mArguments	[[Ljava/lang/String;
      //   42: iload_1
      //   43: aaload
      //   44: ifnull +58 -> 102
      //   47: aload_0
      //   48: getfield 69	com/android/server/connectivity/Vpn$LegacyVpnRunner:mDaemons	[Ljava/lang/String;
      //   51: iload_1
      //   52: aaload
      //   53: invokestatic 195	android/os/SystemService:isStopped	(Ljava/lang/String;)Z
      //   56: istore 5
      //   58: iload 5
      //   60: ifeq +42 -> 102
      //   63: aload_0
      //   64: getfield 69	com/android/server/connectivity/Vpn$LegacyVpnRunner:mDaemons	[Ljava/lang/String;
      //   67: astore 6
      //   69: aload 6
      //   71: arraylength
      //   72: istore_3
      //   73: iload_2
      //   74: istore_1
      //   75: iload_1
      //   76: iload_3
      //   77: if_icmpge +17 -> 94
      //   80: aload 6
      //   82: iload_1
      //   83: aaload
      //   84: invokestatic 213	android/os/SystemService:stop	(Ljava/lang/String;)V
      //   87: iload_1
      //   88: iconst_1
      //   89: iadd
      //   90: istore_1
      //   91: goto -16 -> 75
      //   94: aload_0
      //   95: getfield 40	com/android/server/connectivity/Vpn$LegacyVpnRunner:this$0	Lcom/android/server/connectivity/Vpn;
      //   98: invokestatic 233	com/android/server/connectivity/Vpn:-wrap3	(Lcom/android/server/connectivity/Vpn;)V
      //   101: return
      //   102: iload_1
      //   103: iconst_1
      //   104: iadd
      //   105: istore_1
      //   106: goto -77 -> 29
      //   109: astore 6
      //   111: ldc 12
      //   113: ldc_w 437
      //   116: invokestatic 440	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   119: pop
      //   120: aload_0
      //   121: getfield 69	com/android/server/connectivity/Vpn$LegacyVpnRunner:mDaemons	[Ljava/lang/String;
      //   124: astore 6
      //   126: aload 6
      //   128: arraylength
      //   129: istore_2
      //   130: iload_3
      //   131: istore_1
      //   132: iload_1
      //   133: iload_2
      //   134: if_icmpge +17 -> 151
      //   137: aload 6
      //   139: iload_1
      //   140: aaload
      //   141: invokestatic 213	android/os/SystemService:stop	(Ljava/lang/String;)V
      //   144: iload_1
      //   145: iconst_1
      //   146: iadd
      //   147: istore_1
      //   148: goto -16 -> 132
      //   151: aload_0
      //   152: getfield 40	com/android/server/connectivity/Vpn$LegacyVpnRunner:this$0	Lcom/android/server/connectivity/Vpn;
      //   155: invokestatic 233	com/android/server/connectivity/Vpn:-wrap3	(Lcom/android/server/connectivity/Vpn;)V
      //   158: return
      //   159: astore 6
      //   161: aload_0
      //   162: getfield 69	com/android/server/connectivity/Vpn$LegacyVpnRunner:mDaemons	[Ljava/lang/String;
      //   165: astore 7
      //   167: aload 7
      //   169: arraylength
      //   170: istore_2
      //   171: iload 4
      //   173: istore_1
      //   174: iload_1
      //   175: iload_2
      //   176: if_icmpge +17 -> 193
      //   179: aload 7
      //   181: iload_1
      //   182: aaload
      //   183: invokestatic 213	android/os/SystemService:stop	(Ljava/lang/String;)V
      //   186: iload_1
      //   187: iconst_1
      //   188: iadd
      //   189: istore_1
      //   190: goto -16 -> 174
      //   193: aload_0
      //   194: getfield 40	com/android/server/connectivity/Vpn$LegacyVpnRunner:this$0	Lcom/android/server/connectivity/Vpn;
      //   197: invokestatic 233	com/android/server/connectivity/Vpn:-wrap3	(Lcom/android/server/connectivity/Vpn;)V
      //   200: aload 6
      //   202: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	203	0	this	LegacyVpnRunner
      //   28	162	1	i	int
      //   6	171	2	j	int
      //   1	130	3	k	int
      //   3	169	4	m	int
      //   56	3	5	bool	boolean
      //   67	14	6	arrayOfString1	String[]
      //   109	1	6	localInterruptedException	InterruptedException
      //   124	14	6	arrayOfString2	String[]
      //   159	42	6	localObject	Object
      //   165	15	7	arrayOfString3	String[]
      // Exception table:
      //   from	to	target	type
      //   21	27	109	java/lang/InterruptedException
      //   29	58	109	java/lang/InterruptedException
      //   21	27	159	finally
      //   29	58	159	finally
      //   111	120	159	finally
    }
    
    public void check(String paramString)
    {
      if (paramString.equals(this.mOuterInterface))
      {
        Log.i("LegacyVpnRunner", "Legacy VPN is going down with " + paramString);
        exit();
      }
    }
    
    public void exit()
    {
      interrupt();
      LocalSocket[] arrayOfLocalSocket = this.mSockets;
      int i = 0;
      int j = arrayOfLocalSocket.length;
      while (i < j)
      {
        IoUtils.closeQuietly(arrayOfLocalSocket[i]);
        i += 1;
      }
      Vpn.-wrap3(Vpn.this);
      try
      {
        Vpn.-get2(Vpn.this).unregisterReceiver(this.mBroadcastReceiver);
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    
    public void run()
    {
      Log.v("LegacyVpnRunner", "Waiting");
      try
      {
        Log.v("LegacyVpnRunner", "Executing");
        execute();
        monitorDaemons();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/Vpn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */