package android.net.ip;

import android.net.IpPrefix;
import android.net.NetworkUtils;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructTimeval;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoBridge;

public class RouterAdvertisementDaemon
{
  private static final byte[] ALL_NODES = { -1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
  private static final int DAY_IN_SECONDS = 86400;
  private static final int DEFAULT_LIFETIME = 3600;
  private static final byte ICMPV6_ND_ROUTER_ADVERT;
  private static final byte ICMPV6_ND_ROUTER_SOLICIT;
  private static final int IPV6_MIN_MTU = 1280;
  private static final int MAX_RTR_ADV_INTERVAL_SEC = 600;
  private static final int MAX_URGENT_RTR_ADVERTISEMENTS = 5;
  private static final int MIN_DELAY_BETWEEN_RAS_SEC = 3;
  private static final int MIN_RA_HEADER_SIZE = 16;
  private static final int MIN_RTR_ADV_INTERVAL_SEC = 300;
  private static final String TAG = RouterAdvertisementDaemon.class.getSimpleName();
  private final InetSocketAddress mAllNodes;
  @GuardedBy("mLock")
  private final DeprecatedInfoTracker mDeprecatedInfoTracker;
  private final byte[] mHwAddr;
  private final int mIfIndex;
  private final String mIfName;
  private final Object mLock = new Object();
  private volatile MulticastTransmitter mMulticastTransmitter;
  @GuardedBy("mLock")
  private final byte[] mRA = new byte['Ԁ'];
  @GuardedBy("mLock")
  private int mRaLength;
  @GuardedBy("mLock")
  private RaParams mRaParams;
  private volatile FileDescriptor mSocket;
  private volatile UnicastResponder mUnicastResponder;
  
  static
  {
    ICMPV6_ND_ROUTER_SOLICIT = asByte(133);
    ICMPV6_ND_ROUTER_ADVERT = asByte(134);
  }
  
  public RouterAdvertisementDaemon(String paramString, int paramInt, byte[] paramArrayOfByte)
  {
    this.mIfName = paramString;
    this.mIfIndex = paramInt;
    this.mHwAddr = paramArrayOfByte;
    this.mAllNodes = new InetSocketAddress(getAllNodesForScopeId(this.mIfIndex), 0);
    this.mDeprecatedInfoTracker = new DeprecatedInfoTracker(null);
  }
  
  private static byte asByte(int paramInt)
  {
    return (byte)paramInt;
  }
  
  private static short asShort(int paramInt)
  {
    return (short)paramInt;
  }
  
  private void assembleRaLocked()
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(this.mRA);
    localByteBuffer.order(ByteOrder.BIG_ENDIAN);
    int j = 0;
    int m = 0;
    k = 0;
    i = m;
    try
    {
      if (this.mRaParams == null) {
        break label388;
      }
      i = m;
      bool = this.mRaParams.hasDefaultRoute;
    }
    catch (BufferOverflowException localBufferOverflowException)
    {
      for (;;)
      {
        Object localObject;
        Log.e(TAG, "Could not construct new RA: " + localBufferOverflowException);
        k = i;
        continue;
        boolean bool = false;
      }
    }
    i = m;
    putHeader(localByteBuffer, bool);
    i = m;
    putSlla(localByteBuffer, this.mHwAddr);
    i = m;
    this.mRaLength = localByteBuffer.position();
    i = m;
    if (this.mRaParams != null)
    {
      i = m;
      putMtu(localByteBuffer, this.mRaParams.mtu);
      i = m;
      this.mRaLength = localByteBuffer.position();
      i = m;
      localObject = this.mRaParams.prefixes.iterator();
      for (;;)
      {
        i = k;
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        i = k;
        putPio(localByteBuffer, (IpPrefix)((Iterator)localObject).next(), 3600, 3600);
        i = k;
        this.mRaLength = localByteBuffer.position();
        k = 1;
      }
      j = k;
      i = k;
      if (this.mRaParams.dnses.size() > 0)
      {
        i = k;
        putRdnss(localByteBuffer, this.mRaParams.dnses, 3600);
        i = k;
        this.mRaLength = localByteBuffer.position();
        j = 1;
      }
    }
    i = j;
    localObject = this.mDeprecatedInfoTracker.getPrefixes().iterator();
    for (;;)
    {
      i = j;
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
      i = j;
      putPio(localByteBuffer, (IpPrefix)((Iterator)localObject).next(), 0, 0);
      i = j;
      this.mRaLength = localByteBuffer.position();
      j = 1;
    }
    i = j;
    localObject = this.mDeprecatedInfoTracker.getDnses();
    k = j;
    i = j;
    if (!((Set)localObject).isEmpty())
    {
      i = j;
      putRdnss(localByteBuffer, (Set)localObject, 0);
      i = j;
      this.mRaLength = localByteBuffer.position();
      k = 1;
    }
    if (k == 0) {
      this.mRaLength = 0;
    }
  }
  
  private void closeSocket()
  {
    if (this.mSocket != null) {}
    try
    {
      IoBridge.closeAndSignalBlockedThreads(this.mSocket);
      this.mSocket = null;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  private boolean createSocket()
  {
    try
    {
      this.mSocket = Os.socket(OsConstants.AF_INET6, OsConstants.SOCK_RAW, OsConstants.IPPROTO_ICMPV6);
      Os.setsockoptTimeval(this.mSocket, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, StructTimeval.fromMillis(300L));
      Os.setsockoptIfreq(this.mSocket, OsConstants.SOL_SOCKET, OsConstants.SO_BINDTODEVICE, this.mIfName);
      NetworkUtils.protectFromVpn(this.mSocket);
      NetworkUtils.setupRaSocket(this.mSocket, this.mIfIndex);
      return true;
    }
    catch (ErrnoException|IOException localErrnoException)
    {
      Log.e(TAG, "Failed to create RA daemon socket: " + localErrnoException);
    }
    return false;
  }
  
  private static Inet6Address getAllNodesForScopeId(int paramInt)
  {
    try
    {
      Inet6Address localInet6Address = Inet6Address.getByAddress("ff02::1", ALL_NODES, paramInt);
      return localInet6Address;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      Log.wtf(TAG, "Failed to construct ff02::1 InetAddress: " + localUnknownHostException);
    }
    return null;
  }
  
  private boolean isSocketValid()
  {
    FileDescriptor localFileDescriptor = this.mSocket;
    if (localFileDescriptor != null) {
      return localFileDescriptor.valid();
    }
    return false;
  }
  
  private boolean isSuitableDestination(InetSocketAddress paramInetSocketAddress)
  {
    if (this.mAllNodes.equals(paramInetSocketAddress)) {
      return true;
    }
    paramInetSocketAddress = paramInetSocketAddress.getAddress();
    if (((paramInetSocketAddress instanceof Inet6Address)) && (paramInetSocketAddress.isLinkLocalAddress())) {
      return ((Inet6Address)paramInetSocketAddress).getScopeId() == this.mIfIndex;
    }
    return false;
  }
  
  private void maybeNotifyMulticastTransmitter()
  {
    MulticastTransmitter localMulticastTransmitter = this.mMulticastTransmitter;
    if (localMulticastTransmitter != null) {
      localMulticastTransmitter.hup();
    }
  }
  
  private void maybeSendRA(InetSocketAddress paramInetSocketAddress)
  {
    if ((paramInetSocketAddress != null) && (isSuitableDestination(paramInetSocketAddress))) {}
    try
    {
      synchronized (this.mLock)
      {
        for (;;)
        {
          int i = this.mRaLength;
          if (i >= 16) {
            break;
          }
          return;
          paramInetSocketAddress = this.mAllNodes;
        }
        Os.sendto(this.mSocket, this.mRA, 0, this.mRaLength, 0, paramInetSocketAddress);
        Log.d(TAG, "RA sendto " + paramInetSocketAddress.getAddress().getHostAddress());
        return;
      }
      return;
    }
    catch (ErrnoException|SocketException paramInetSocketAddress)
    {
      if (isSocketValid()) {
        Log.e(TAG, "sendto error: " + paramInetSocketAddress);
      }
    }
  }
  
  private static void putExpandedFlagsOption(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put((byte)26).put((byte)1).putShort(asShort(0)).putInt(0);
  }
  
  private static void putHeader(ByteBuffer paramByteBuffer, boolean paramBoolean)
  {
    paramByteBuffer = paramByteBuffer.put(ICMPV6_ND_ROUTER_ADVERT).put(asByte(0)).putShort(asShort(0)).put((byte)64);
    byte b;
    if (paramBoolean)
    {
      b = asByte(8);
      paramByteBuffer = paramByteBuffer.put(b);
      if (!paramBoolean) {
        break label77;
      }
    }
    label77:
    for (short s = asShort(3600);; s = asShort(0))
    {
      paramByteBuffer.putShort(s).putInt(0).putInt(0);
      return;
      b = asByte(0);
      break;
    }
  }
  
  private static void putMtu(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramByteBuffer = paramByteBuffer.put((byte)5).put((byte)1).putShort(asShort(0));
    int i = paramInt;
    if (paramInt < 1280) {
      i = 1280;
    }
    paramByteBuffer.putInt(i);
  }
  
  private static void putPio(ByteBuffer paramByteBuffer, IpPrefix paramIpPrefix, int paramInt1, int paramInt2)
  {
    int j = paramIpPrefix.getPrefixLength();
    if (j != 64) {
      return;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 < 0) {
      paramInt1 = 0;
    }
    paramInt2 = paramInt1;
    if (paramInt1 > i) {
      paramInt2 = i;
    }
    paramIpPrefix = paramIpPrefix.getAddress().getAddress();
    paramByteBuffer.put((byte)3).put((byte)4).put(asByte(j)).put(asByte(192)).putInt(i).putInt(paramInt2).putInt(0).put(paramIpPrefix);
  }
  
  private static void putRdnss(ByteBuffer paramByteBuffer, Set<Inet6Address> paramSet, int paramInt)
  {
    byte b = asByte(paramSet.size() * 2 + 1);
    paramByteBuffer.put((byte)25).put(b).putShort(asShort(0)).putInt(paramInt);
    paramSet = paramSet.iterator();
    while (paramSet.hasNext()) {
      paramByteBuffer.put(((Inet6Address)paramSet.next()).getAddress());
    }
  }
  
  private static void putRio(ByteBuffer paramByteBuffer, IpPrefix paramIpPrefix)
  {
    int j = paramIpPrefix.getPrefixLength();
    if (j > 64) {
      return;
    }
    if (j == 0)
    {
      i = 1;
      byte b = asByte(i);
      paramIpPrefix = paramIpPrefix.getAddress().getAddress();
      paramByteBuffer.put((byte)24).put(b).put(asByte(j)).put(asByte(24)).putInt(3600);
      if (j > 0) {
        if (j > 64) {
          break label108;
        }
      }
    }
    label108:
    for (int i = 8;; i = 16)
    {
      paramByteBuffer.put(paramIpPrefix, 0, i);
      return;
      if (j <= 8)
      {
        i = 2;
        break;
      }
      i = 3;
      break;
    }
  }
  
  private static void putSlla(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length != 6)) {
      return;
    }
    paramByteBuffer.put((byte)1).put((byte)1).put(paramArrayOfByte);
  }
  
  public void buildNewRa(RaParams paramRaParams1, RaParams paramRaParams2)
  {
    Object localObject = this.mLock;
    if (paramRaParams1 != null) {}
    try
    {
      this.mDeprecatedInfoTracker.putPrefixes(paramRaParams1.prefixes);
      this.mDeprecatedInfoTracker.putDnses(paramRaParams1.dnses);
      if (paramRaParams2 != null)
      {
        this.mDeprecatedInfoTracker.removePrefixes(paramRaParams2.prefixes);
        this.mDeprecatedInfoTracker.removeDnses(paramRaParams2.dnses);
      }
      this.mRaParams = paramRaParams2;
      assembleRaLocked();
      maybeNotifyMulticastTransmitter();
      return;
    }
    finally {}
  }
  
  public boolean start()
  {
    if (!createSocket()) {
      return false;
    }
    this.mMulticastTransmitter = new MulticastTransmitter(null);
    this.mMulticastTransmitter.start();
    this.mUnicastResponder = new UnicastResponder(null);
    this.mUnicastResponder.start();
    return true;
  }
  
  public void stop()
  {
    closeSocket();
    this.mMulticastTransmitter = null;
    this.mUnicastResponder = null;
  }
  
  private static class DeprecatedInfoTracker
  {
    private final HashMap<Inet6Address, Integer> mDnses = new HashMap();
    private final HashMap<IpPrefix, Integer> mPrefixes = new HashMap();
    
    private <T> boolean decrementCounter(HashMap<T, Integer> paramHashMap)
    {
      boolean bool = false;
      paramHashMap = paramHashMap.entrySet().iterator();
      while (paramHashMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramHashMap.next();
        if (((Integer)localEntry.getValue()).intValue() == 0)
        {
          paramHashMap.remove();
          bool = true;
        }
        else
        {
          localEntry.setValue(Integer.valueOf(((Integer)localEntry.getValue()).intValue() - 1));
        }
      }
      return bool;
    }
    
    private boolean decrementCounters()
    {
      return decrementCounter(this.mPrefixes) | decrementCounter(this.mDnses);
    }
    
    Set<Inet6Address> getDnses()
    {
      return this.mDnses.keySet();
    }
    
    Set<IpPrefix> getPrefixes()
    {
      return this.mPrefixes.keySet();
    }
    
    boolean isEmpty()
    {
      if (this.mPrefixes.isEmpty()) {
        return this.mDnses.isEmpty();
      }
      return false;
    }
    
    void putDnses(Set<Inet6Address> paramSet)
    {
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        Inet6Address localInet6Address = (Inet6Address)paramSet.next();
        this.mDnses.put(localInet6Address, Integer.valueOf(5));
      }
    }
    
    void putPrefixes(Set<IpPrefix> paramSet)
    {
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        IpPrefix localIpPrefix = (IpPrefix)paramSet.next();
        this.mPrefixes.put(localIpPrefix, Integer.valueOf(5));
      }
    }
    
    void removeDnses(Set<Inet6Address> paramSet)
    {
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        Inet6Address localInet6Address = (Inet6Address)paramSet.next();
        this.mDnses.remove(localInet6Address);
      }
    }
    
    void removePrefixes(Set<IpPrefix> paramSet)
    {
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        IpPrefix localIpPrefix = (IpPrefix)paramSet.next();
        this.mPrefixes.remove(localIpPrefix);
      }
    }
  }
  
  private final class MulticastTransmitter
    extends Thread
  {
    private final Random mRandom = new Random();
    private final AtomicInteger mUrgentAnnouncements = new AtomicInteger(0);
    
    private MulticastTransmitter() {}
    
    private long getNextMulticastTransmitDelayMs()
    {
      return getNextMulticastTransmitDelaySec() * 1000L;
    }
    
    private int getNextMulticastTransmitDelaySec()
    {
      synchronized (RouterAdvertisementDaemon.-get4(RouterAdvertisementDaemon.this))
      {
        int i = RouterAdvertisementDaemon.-get5(RouterAdvertisementDaemon.this);
        if (i < 16) {
          return 86400;
        }
        boolean bool = RouterAdvertisementDaemon.-get3(RouterAdvertisementDaemon.this).isEmpty();
        if (bool)
        {
          i = 0;
          if ((this.mUrgentAnnouncements.getAndDecrement() > 0) || (i != 0)) {
            return 3;
          }
        }
        else
        {
          i = 1;
        }
      }
      return this.mRandom.nextInt(300) + 300;
    }
    
    public void hup()
    {
      this.mUrgentAnnouncements.set(4);
      interrupt();
    }
    
    public void run()
    {
      if (RouterAdvertisementDaemon.-wrap0(RouterAdvertisementDaemon.this)) {}
      try
      {
        Thread.sleep(getNextMulticastTransmitDelayMs());
        RouterAdvertisementDaemon.-wrap2(RouterAdvertisementDaemon.this, RouterAdvertisementDaemon.-get2(RouterAdvertisementDaemon.this));
        synchronized (RouterAdvertisementDaemon.-get4(RouterAdvertisementDaemon.this))
        {
          if (RouterAdvertisementDaemon.DeprecatedInfoTracker.-wrap0(RouterAdvertisementDaemon.-get3(RouterAdvertisementDaemon.this))) {
            RouterAdvertisementDaemon.-wrap1(RouterAdvertisementDaemon.this);
          }
        }
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
  }
  
  public static class RaParams
  {
    public HashSet<Inet6Address> dnses;
    public boolean hasDefaultRoute;
    public int mtu;
    public HashSet<IpPrefix> prefixes;
    
    public RaParams()
    {
      this.hasDefaultRoute = false;
      this.mtu = 1280;
      this.prefixes = new HashSet();
      this.dnses = new HashSet();
    }
    
    public RaParams(RaParams paramRaParams)
    {
      this.hasDefaultRoute = paramRaParams.hasDefaultRoute;
      this.mtu = paramRaParams.mtu;
      this.prefixes = ((HashSet)paramRaParams.prefixes.clone());
      this.dnses = ((HashSet)paramRaParams.dnses.clone());
    }
    
    public static RaParams getDeprecatedRaParams(RaParams paramRaParams1, RaParams paramRaParams2)
    {
      RaParams localRaParams = new RaParams();
      if (paramRaParams1 != null)
      {
        Object localObject = paramRaParams1.prefixes.iterator();
        while (((Iterator)localObject).hasNext())
        {
          IpPrefix localIpPrefix = (IpPrefix)((Iterator)localObject).next();
          if ((paramRaParams2 == null) || (!paramRaParams2.prefixes.contains(localIpPrefix))) {
            localRaParams.prefixes.add(localIpPrefix);
          }
        }
        paramRaParams1 = paramRaParams1.dnses.iterator();
        while (paramRaParams1.hasNext())
        {
          localObject = (Inet6Address)paramRaParams1.next();
          if ((paramRaParams2 == null) || (!paramRaParams2.dnses.contains(localObject))) {
            localRaParams.dnses.add(localObject);
          }
        }
      }
      return localRaParams;
    }
  }
  
  private final class UnicastResponder
    extends Thread
  {
    private final byte[] mSolication = new byte['Ԁ'];
    private final InetSocketAddress solicitor = new InetSocketAddress();
    
    private UnicastResponder() {}
    
    public void run()
    {
      while (RouterAdvertisementDaemon.-wrap0(RouterAdvertisementDaemon.this))
      {
        try
        {
          if (Os.recvfrom(RouterAdvertisementDaemon.-get6(RouterAdvertisementDaemon.this), this.mSolication, 0, this.mSolication.length, 0, this.solicitor) < 1) {
            continue;
          }
          int i = this.mSolication[0];
          int j = RouterAdvertisementDaemon.-get0();
          if (i != j) {
            continue;
          }
          RouterAdvertisementDaemon.-wrap2(RouterAdvertisementDaemon.this, this.solicitor);
        }
        catch (ErrnoException|SocketException localErrnoException) {}
        if (RouterAdvertisementDaemon.-wrap0(RouterAdvertisementDaemon.this)) {
          Log.e(RouterAdvertisementDaemon.-get1(), "recvfrom error: " + localErrnoException);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ip/RouterAdvertisementDaemon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */