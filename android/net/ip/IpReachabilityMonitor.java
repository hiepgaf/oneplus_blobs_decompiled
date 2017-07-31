package android.net.ip;

import android.content.Context;
import android.net.LinkProperties;
import android.net.LinkProperties.ProvisioningChange;
import android.net.RouteInfo;
import android.net.metrics.IpConnectivityLog;
import android.net.metrics.IpReachabilityEvent;
import android.net.netlink.NetlinkConstants;
import android.net.netlink.NetlinkErrorMessage;
import android.net.netlink.NetlinkMessage;
import android.net.netlink.NetlinkSocket;
import android.net.netlink.RtNetlinkNeighborMessage;
import android.net.netlink.StructNdMsg;
import android.net.netlink.StructNlMsgHdr;
import android.net.util.AvoidBadWifiTracker;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.system.ErrnoException;
import android.system.NetlinkSocketAddress;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.io.InterruptedIOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IpReachabilityMonitor
{
  private static final boolean DBG = false;
  private static final String TAG = "IpReachabilityMonitor";
  private static final boolean VDBG = false;
  private final AvoidBadWifiTracker mAvoidBadWifiTracker;
  private final Callback mCallback;
  private final int mInterfaceIndex;
  private final String mInterfaceName;
  @GuardedBy("mLock")
  private Map<InetAddress, Short> mIpWatchList = new HashMap();
  @GuardedBy("mLock")
  private int mIpWatchListVersion;
  private volatile long mLastProbeTimeMs;
  @GuardedBy("mLock")
  private LinkProperties mLinkProperties = new LinkProperties();
  private final Object mLock = new Object();
  private final IpConnectivityLog mMetricsLog = new IpConnectivityLog();
  private final NetlinkSocketObserver mNetlinkSocketObserver;
  private final Thread mObserverThread;
  @GuardedBy("mLock")
  private boolean mRunning;
  private final PowerManager.WakeLock mWakeLock;
  
  public IpReachabilityMonitor(Context paramContext, String paramString, Callback paramCallback)
  {
    this(paramContext, paramString, paramCallback, null);
  }
  
  public IpReachabilityMonitor(Context paramContext, String paramString, Callback paramCallback, AvoidBadWifiTracker paramAvoidBadWifiTracker)
    throws IllegalArgumentException
  {
    this.mInterfaceName = paramString;
    try
    {
      this.mInterfaceIndex = NetworkInterface.getByName(paramString).getIndex();
      this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "IpReachabilityMonitor." + this.mInterfaceName);
      this.mCallback = paramCallback;
      this.mAvoidBadWifiTracker = paramAvoidBadWifiTracker;
      this.mNetlinkSocketObserver = new NetlinkSocketObserver(null);
      this.mObserverThread = new Thread(this.mNetlinkSocketObserver);
      this.mObserverThread.start();
      return;
    }
    catch (SocketException|NullPointerException paramContext)
    {
      throw new IllegalArgumentException("invalid interface '" + paramString + "': ", paramContext);
    }
  }
  
  private boolean avoidingBadLinks()
  {
    if (this.mAvoidBadWifiTracker != null) {
      return this.mAvoidBadWifiTracker.currentValue();
    }
    return true;
  }
  
  private String describeWatchList()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;)
    {
      synchronized (this.mLock)
      {
        localStringBuilder.append("iface{").append(this.mInterfaceName).append("/").append(this.mInterfaceIndex).append("}, ");
        localStringBuilder.append("v{").append(this.mIpWatchListVersion).append("}, ");
        localStringBuilder.append("ntable=[");
        int i = 1;
        Iterator localIterator = this.mIpWatchList.entrySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (i != 0)
        {
          i = 0;
          localStringBuilder.append(((InetAddress)localEntry.getKey()).getHostAddress()).append("/").append(StructNdMsg.stringForNudState(((Short)localEntry.getValue()).shortValue()));
        }
      }
      ((StringBuilder)localObject2).append(", ");
    }
    ((StringBuilder)localObject2).append("]");
    return ((StringBuilder)localObject2).toString();
  }
  
  private short getNeighborStateLocked(InetAddress paramInetAddress)
  {
    if (this.mIpWatchList.containsKey(paramInetAddress)) {
      return ((Short)this.mIpWatchList.get(paramInetAddress)).shortValue();
    }
    return 0;
  }
  
  private static long getProbeWakeLockDuration()
  {
    return 3500L;
  }
  
  private void handleNeighborLost(String paramString)
  {
    Object localObject1 = null;
    LinkProperties localLinkProperties;
    for (;;)
    {
      synchronized (this.mLock)
      {
        localLinkProperties = new LinkProperties(this.mLinkProperties);
        Iterator localIterator = this.mIpWatchList.entrySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        localObject2 = (Map.Entry)localIterator.next();
        if (((Short)((Map.Entry)localObject2).getValue()).shortValue() != 32) {
          continue;
        }
        localObject2 = (InetAddress)((Map.Entry)localObject2).getKey();
        localObject1 = this.mLinkProperties.getRoutes().iterator();
        if (((Iterator)localObject1).hasNext())
        {
          RouteInfo localRouteInfo = (RouteInfo)((Iterator)localObject1).next();
          if (!((InetAddress)localObject2).equals(localRouteInfo.getGateway())) {
            continue;
          }
          localLinkProperties.removeRoute(localRouteInfo);
        }
      }
      if (!avoidingBadLinks())
      {
        localObject1 = localObject2;
        if ((localObject2 instanceof Inet6Address)) {}
      }
      else
      {
        localLinkProperties.removeDnsServer((InetAddress)localObject2);
        localObject1 = localObject2;
      }
    }
    Object localObject2 = LinkProperties.compareProvisioning(this.mLinkProperties, localLinkProperties);
    if (localObject2 == LinkProperties.ProvisioningChange.LOST_PROVISIONING)
    {
      paramString = "FAILURE: LOST_PROVISIONING, " + paramString;
      Log.w("IpReachabilityMonitor", paramString);
      if (this.mCallback != null) {
        this.mCallback.notifyLost((InetAddress)localObject1, paramString);
      }
    }
    logNudFailed((LinkProperties.ProvisioningChange)localObject2);
  }
  
  private static boolean isOnLink(List<RouteInfo> paramList, InetAddress paramInetAddress)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      RouteInfo localRouteInfo = (RouteInfo)paramList.next();
      if ((!localRouteInfo.hasGateway()) && (localRouteInfo.matches(paramInetAddress))) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isWatching(InetAddress paramInetAddress)
  {
    synchronized (this.mLock)
    {
      if (this.mRunning)
      {
        bool = this.mIpWatchList.containsKey(paramInetAddress);
        return bool;
      }
      boolean bool = false;
    }
  }
  
  private void logEvent(int paramInt1, int paramInt2)
  {
    this.mMetricsLog.log(new IpReachabilityEvent(this.mInterfaceName, paramInt1 | paramInt2 & 0xFF));
  }
  
  private void logNudFailed(LinkProperties.ProvisioningChange paramProvisioningChange)
  {
    boolean bool1;
    if (SystemClock.elapsedRealtime() - this.mLastProbeTimeMs < getProbeWakeLockDuration())
    {
      bool1 = true;
      if (paramProvisioningChange != LinkProperties.ProvisioningChange.LOST_PROVISIONING) {
        break label59;
      }
    }
    label59:
    for (boolean bool2 = true;; bool2 = false)
    {
      int i = IpReachabilityEvent.nudFailureEventType(bool1, bool2);
      this.mMetricsLog.log(new IpReachabilityEvent(this.mInterfaceName, i));
      return;
      bool1 = false;
      break;
    }
  }
  
  /* Error */
  private static int probeNeighbor(int paramInt, InetAddress paramInetAddress)
  {
    // Byte code:
    //   0: new 132	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 359
    //   10: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_1
    //   14: invokevirtual 229	java/net/InetAddress:getHostAddress	()Ljava/lang/String;
    //   17: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: ldc_w 361
    //   23: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: iload_0
    //   27: invokevirtual 190	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   30: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   33: astore 6
    //   35: iconst_1
    //   36: aload_1
    //   37: bipush 16
    //   39: iload_0
    //   40: aconst_null
    //   41: invokestatic 367	android/net/netlink/RtNetlinkNeighborMessage:newNewNeighborMessage	(ILjava/net/InetAddress;SI[B)[B
    //   44: astore_1
    //   45: getstatic 372	android/system/OsConstants:EPROTO	I
    //   48: ineg
    //   49: istore_0
    //   50: aconst_null
    //   51: astore_3
    //   52: new 374	android/net/netlink/NetlinkSocket
    //   55: dup
    //   56: getstatic 377	android/system/OsConstants:NETLINK_ROUTE	I
    //   59: invokespecial 380	android/net/netlink/NetlinkSocket:<init>	(I)V
    //   62: astore 4
    //   64: aload 4
    //   66: invokevirtual 383	android/net/netlink/NetlinkSocket:connectToKernel	()V
    //   69: aload 4
    //   71: aload_1
    //   72: iconst_0
    //   73: aload_1
    //   74: arraylength
    //   75: ldc2_w 384
    //   78: invokevirtual 389	android/net/netlink/NetlinkSocket:sendMessage	([BIIJ)Z
    //   81: pop
    //   82: aload 4
    //   84: ldc2_w 384
    //   87: invokevirtual 393	android/net/netlink/NetlinkSocket:recvMessage	(J)Ljava/nio/ByteBuffer;
    //   90: astore_1
    //   91: aload_1
    //   92: invokestatic 399	android/net/netlink/NetlinkMessage:parse	(Ljava/nio/ByteBuffer;)Landroid/net/netlink/NetlinkMessage;
    //   95: astore 5
    //   97: aload 5
    //   99: ifnull +140 -> 239
    //   102: aload 5
    //   104: instanceof 401
    //   107: ifeq +132 -> 239
    //   110: aload 5
    //   112: checkcast 401	android/net/netlink/NetlinkErrorMessage
    //   115: invokevirtual 405	android/net/netlink/NetlinkErrorMessage:getNlMsgError	()Landroid/net/netlink/StructNlMsgErr;
    //   118: ifnull +121 -> 239
    //   121: aload 5
    //   123: checkcast 401	android/net/netlink/NetlinkErrorMessage
    //   126: invokevirtual 405	android/net/netlink/NetlinkErrorMessage:getNlMsgError	()Landroid/net/netlink/StructNlMsgErr;
    //   129: getfield 410	android/net/netlink/StructNlMsgErr:error	I
    //   132: istore_2
    //   133: iload_2
    //   134: istore_0
    //   135: iload_2
    //   136: ifeq +46 -> 182
    //   139: ldc 17
    //   141: new 132	java/lang/StringBuilder
    //   144: dup
    //   145: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   148: ldc_w 412
    //   151: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: aload 6
    //   156: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: ldc_w 414
    //   162: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload 5
    //   167: invokevirtual 415	android/net/netlink/NetlinkMessage:toString	()Ljava/lang/String;
    //   170: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   176: invokestatic 418	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   179: pop
    //   180: iload_2
    //   181: istore_0
    //   182: aload_3
    //   183: astore_1
    //   184: aload 4
    //   186: ifnull +10 -> 196
    //   189: aload 4
    //   191: invokevirtual 421	android/net/netlink/NetlinkSocket:close	()V
    //   194: aload_3
    //   195: astore_1
    //   196: aload_1
    //   197: ifnull +40 -> 237
    //   200: aload_1
    //   201: athrow
    //   202: astore_1
    //   203: ldc 17
    //   205: new 132	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 412
    //   215: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload 6
    //   220: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   226: aload_1
    //   227: invokestatic 424	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   230: pop
    //   231: aload_1
    //   232: getfield 427	android/system/ErrnoException:errno	I
    //   235: ineg
    //   236: istore_0
    //   237: iload_0
    //   238: ireturn
    //   239: aload 5
    //   241: ifnonnull +135 -> 376
    //   244: aload_1
    //   245: iconst_0
    //   246: invokevirtual 433	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   249: pop
    //   250: new 132	java/lang/StringBuilder
    //   253: dup
    //   254: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   257: ldc_w 435
    //   260: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_1
    //   264: invokestatic 441	android/net/netlink/NetlinkConstants:hexify	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
    //   267: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   270: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   273: astore_1
    //   274: ldc 17
    //   276: new 132	java/lang/StringBuilder
    //   279: dup
    //   280: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   283: ldc_w 412
    //   286: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: aload 6
    //   291: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: ldc_w 414
    //   297: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: aload_1
    //   301: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   304: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   307: invokestatic 418	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   310: pop
    //   311: goto -129 -> 182
    //   314: astore_1
    //   315: aload_1
    //   316: athrow
    //   317: astore_3
    //   318: aload_1
    //   319: astore 5
    //   321: aload 4
    //   323: ifnull +11 -> 334
    //   326: aload 4
    //   328: invokevirtual 421	android/net/netlink/NetlinkSocket:close	()V
    //   331: aload_1
    //   332: astore 5
    //   334: aload 5
    //   336: ifnull +121 -> 457
    //   339: aload 5
    //   341: athrow
    //   342: astore_1
    //   343: ldc 17
    //   345: new 132	java/lang/StringBuilder
    //   348: dup
    //   349: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   352: ldc_w 412
    //   355: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: aload 6
    //   360: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   366: aload_1
    //   367: invokestatic 424	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   370: pop
    //   371: getstatic 444	android/system/OsConstants:ETIMEDOUT	I
    //   374: ineg
    //   375: ireturn
    //   376: aload 5
    //   378: invokevirtual 415	android/net/netlink/NetlinkMessage:toString	()Ljava/lang/String;
    //   381: astore_1
    //   382: goto -108 -> 274
    //   385: astore_1
    //   386: goto -190 -> 196
    //   389: astore 4
    //   391: aload_1
    //   392: ifnonnull +10 -> 402
    //   395: aload 4
    //   397: astore 5
    //   399: goto -65 -> 334
    //   402: aload_1
    //   403: astore 5
    //   405: aload_1
    //   406: aload 4
    //   408: if_acmpeq -74 -> 334
    //   411: aload_1
    //   412: aload 4
    //   414: invokevirtual 448	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   417: aload_1
    //   418: astore 5
    //   420: goto -86 -> 334
    //   423: astore_1
    //   424: ldc 17
    //   426: new 132	java/lang/StringBuilder
    //   429: dup
    //   430: invokespecial 133	java/lang/StringBuilder:<init>	()V
    //   433: ldc_w 412
    //   436: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   439: aload 6
    //   441: invokevirtual 139	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   444: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   447: aload_1
    //   448: invokestatic 424	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   451: pop
    //   452: getstatic 451	android/system/OsConstants:EIO	I
    //   455: ineg
    //   456: ireturn
    //   457: aload_3
    //   458: athrow
    //   459: astore_3
    //   460: aconst_null
    //   461: astore_1
    //   462: aconst_null
    //   463: astore 4
    //   465: goto -147 -> 318
    //   468: astore_3
    //   469: aconst_null
    //   470: astore_1
    //   471: goto -153 -> 318
    //   474: astore_1
    //   475: aconst_null
    //   476: astore 4
    //   478: goto -163 -> 315
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	481	0	paramInt	int
    //   0	481	1	paramInetAddress	InetAddress
    //   132	49	2	i	int
    //   51	144	3	localObject1	Object
    //   317	141	3	localObject2	Object
    //   459	1	3	localObject3	Object
    //   468	1	3	localObject4	Object
    //   62	265	4	localNetlinkSocket	NetlinkSocket
    //   389	24	4	localThrowable	Throwable
    //   463	14	4	localObject5	Object
    //   95	324	5	localObject6	Object
    //   33	407	6	str	String
    // Exception table:
    //   from	to	target	type
    //   189	194	202	android/system/ErrnoException
    //   200	202	202	android/system/ErrnoException
    //   326	331	202	android/system/ErrnoException
    //   339	342	202	android/system/ErrnoException
    //   411	417	202	android/system/ErrnoException
    //   457	459	202	android/system/ErrnoException
    //   64	97	314	java/lang/Throwable
    //   102	133	314	java/lang/Throwable
    //   139	180	314	java/lang/Throwable
    //   244	274	314	java/lang/Throwable
    //   274	311	314	java/lang/Throwable
    //   376	382	314	java/lang/Throwable
    //   315	317	317	finally
    //   189	194	342	java/io/InterruptedIOException
    //   200	202	342	java/io/InterruptedIOException
    //   326	331	342	java/io/InterruptedIOException
    //   339	342	342	java/io/InterruptedIOException
    //   411	417	342	java/io/InterruptedIOException
    //   457	459	342	java/io/InterruptedIOException
    //   189	194	385	java/lang/Throwable
    //   326	331	389	java/lang/Throwable
    //   189	194	423	java/net/SocketException
    //   200	202	423	java/net/SocketException
    //   326	331	423	java/net/SocketException
    //   339	342	423	java/net/SocketException
    //   411	417	423	java/net/SocketException
    //   457	459	423	java/net/SocketException
    //   52	64	459	finally
    //   64	97	468	finally
    //   102	133	468	finally
    //   139	180	468	finally
    //   244	274	468	finally
    //   274	311	468	finally
    //   376	382	468	finally
    //   52	64	474	java/lang/Throwable
  }
  
  private boolean stillRunning()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRunning;
      return bool;
    }
  }
  
  public void clearLinkProperties()
  {
    synchronized (this.mLock)
    {
      this.mLinkProperties.clear();
      this.mIpWatchList.clear();
      this.mIpWatchListVersion += 1;
      return;
    }
  }
  
  public void probeAll()
  {
    Object localObject2 = new HashSet();
    for (;;)
    {
      synchronized (this.mLock)
      {
        ((Set)localObject2).addAll(this.mIpWatchList.keySet());
        if ((!((Set)localObject2).isEmpty()) && (stillRunning())) {
          this.mWakeLock.acquire(getProbeWakeLockDuration());
        }
        ??? = ((Iterable)localObject2).iterator();
        if (((Iterator)???).hasNext())
        {
          localObject2 = (InetAddress)((Iterator)???).next();
          if (stillRunning()) {}
        }
        else
        {
          this.mLastProbeTimeMs = SystemClock.elapsedRealtime();
          return;
        }
      }
      logEvent(256, probeNeighbor(this.mInterfaceIndex, localInetAddress));
    }
  }
  
  public void stop()
  {
    synchronized (this.mLock)
    {
      this.mRunning = false;
      clearLinkProperties();
      NetlinkSocketObserver.-wrap0(this.mNetlinkSocketObserver);
      return;
    }
  }
  
  public void updateLinkProperties(LinkProperties paramLinkProperties)
  {
    if (!this.mInterfaceName.equals(paramLinkProperties.getInterfaceName()))
    {
      Log.wtf("IpReachabilityMonitor", "requested LinkProperties interface '" + paramLinkProperties.getInterfaceName() + "' does not match: " + this.mInterfaceName);
      return;
    }
    HashMap localHashMap;
    List localList;
    Object localObject2;
    synchronized (this.mLock)
    {
      this.mLinkProperties = new LinkProperties(paramLinkProperties);
      localHashMap = new HashMap();
      localList = this.mLinkProperties.getRoutes();
      localObject2 = localList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (RouteInfo)((Iterator)localObject2).next();
        if (((RouteInfo)localObject3).hasGateway())
        {
          localObject3 = ((RouteInfo)localObject3).getGateway();
          if (isOnLink(localList, (InetAddress)localObject3)) {
            localHashMap.put(localObject3, Short.valueOf(getNeighborStateLocked((InetAddress)localObject3)));
          }
        }
      }
    }
    paramLinkProperties = paramLinkProperties.getDnsServers().iterator();
    while (paramLinkProperties.hasNext())
    {
      localObject2 = (InetAddress)paramLinkProperties.next();
      if (isOnLink(localList, (InetAddress)localObject2)) {
        localHashMap.put(localObject2, Short.valueOf(getNeighborStateLocked((InetAddress)localObject2)));
      }
    }
    this.mIpWatchList = localHashMap;
    this.mIpWatchListVersion += 1;
  }
  
  public static abstract interface Callback
  {
    public abstract void notifyLost(InetAddress paramInetAddress, String paramString);
  }
  
  private final class NetlinkSocketObserver
    implements Runnable
  {
    private NetlinkSocket mSocket;
    
    private NetlinkSocketObserver() {}
    
    private void clearNetlinkSocket()
    {
      if (this.mSocket != null) {
        this.mSocket.close();
      }
    }
    
    private void evaluateRtNetlinkNeighborMessage(RtNetlinkNeighborMessage arg1, long paramLong)
    {
      Object localObject2 = ???.getNdHeader();
      if ((localObject2 == null) || (((StructNdMsg)localObject2).ndm_ifindex != IpReachabilityMonitor.-get0(IpReachabilityMonitor.this))) {
        return;
      }
      InetAddress localInetAddress = ???.getDestination();
      if (!IpReachabilityMonitor.-wrap0(IpReachabilityMonitor.this, localInetAddress)) {
        return;
      }
      short s1 = ???.getHeader().nlmsg_type;
      short s2 = ((StructNdMsg)localObject2).ndm_state;
      localObject2 = "NeighborEvent{elapsedMs=" + paramLong + ", " + localInetAddress.getHostAddress() + ", " + "[" + NetlinkConstants.hexify(???.getLinkLayerAddress()) + "], " + NetlinkConstants.stringForNlMsgType(s1) + ", " + StructNdMsg.stringForNudState(s2) + "}";
      synchronized (IpReachabilityMonitor.-get2(IpReachabilityMonitor.this))
      {
        if (IpReachabilityMonitor.-get1(IpReachabilityMonitor.this).containsKey(localInetAddress))
        {
          if (s1 == 29)
          {
            s1 = 0;
            IpReachabilityMonitor.-get1(IpReachabilityMonitor.this).put(localInetAddress, Short.valueOf(s1));
          }
        }
        else
        {
          if (s2 == 32)
          {
            Log.w("IpReachabilityMonitor", "ALERT: " + (String)localObject2);
            IpReachabilityMonitor.-wrap2(IpReachabilityMonitor.this, (String)localObject2);
          }
          return;
        }
        s1 = s2;
      }
    }
    
    private void parseNetlinkMessageBuffer(ByteBuffer paramByteBuffer, long paramLong)
    {
      for (;;)
      {
        NetlinkMessage localNetlinkMessage;
        if (paramByteBuffer.remaining() > 0)
        {
          i = paramByteBuffer.position();
          localNetlinkMessage = NetlinkMessage.parse(paramByteBuffer);
          if ((localNetlinkMessage == null) || (localNetlinkMessage.getHeader() == null))
          {
            paramByteBuffer.position(i);
            Log.e("IpReachabilityMonitor", "unparsable netlink msg: " + NetlinkConstants.hexify(paramByteBuffer));
          }
        }
        else
        {
          return;
        }
        int i = localNetlinkMessage.getHeader().nlmsg_pid;
        if (i != 0)
        {
          Log.e("IpReachabilityMonitor", "non-kernel source portId: " + (i & 0xFFFFFFFF));
          return;
        }
        if ((localNetlinkMessage instanceof NetlinkErrorMessage)) {
          Log.e("IpReachabilityMonitor", "netlink error: " + localNetlinkMessage);
        } else if ((localNetlinkMessage instanceof RtNetlinkNeighborMessage)) {
          evaluateRtNetlinkNeighborMessage((RtNetlinkNeighborMessage)localNetlinkMessage, paramLong);
        }
      }
    }
    
    private ByteBuffer recvKernelReply()
      throws ErrnoException
    {
      try
      {
        ByteBuffer localByteBuffer = this.mSocket.recvMessage(0L);
        return localByteBuffer;
      }
      catch (ErrnoException localErrnoException)
      {
        if (localErrnoException.errno != OsConstants.EAGAIN) {
          throw localErrnoException;
        }
      }
      catch (InterruptedIOException localInterruptedIOException) {}
      return null;
    }
    
    private void setupNetlinkSocket()
      throws ErrnoException, SocketException
    {
      clearNetlinkSocket();
      this.mSocket = new NetlinkSocket(OsConstants.NETLINK_ROUTE);
      NetlinkSocketAddress localNetlinkSocketAddress = new NetlinkSocketAddress(0, OsConstants.RTMGRP_NEIGH);
      this.mSocket.bind(localNetlinkSocketAddress);
    }
    
    public void run()
    {
      long l;
      synchronized (IpReachabilityMonitor.-get2(IpReachabilityMonitor.this))
      {
        IpReachabilityMonitor.-set0(IpReachabilityMonitor.this, true);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ip/IpReachabilityMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */