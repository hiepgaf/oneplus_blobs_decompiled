package android.net;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.server.NetworkManagementSocketTagger;
import dalvik.system.SocketTagger;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class TrafficStats
{
  public static final long GB_IN_BYTES = 1073741824L;
  public static final long KB_IN_BYTES = 1024L;
  public static final long MB_IN_BYTES = 1048576L;
  public static final long PB_IN_BYTES = 1125899906842624L;
  public static final int TAG_SYSTEM_BACKUP = -253;
  public static final int TAG_SYSTEM_DOWNLOAD = -255;
  public static final int TAG_SYSTEM_MEDIA = -254;
  public static final int TAG_SYSTEM_RESTORE = -252;
  public static final long TB_IN_BYTES = 1099511627776L;
  private static final int TYPE_RX_BYTES = 0;
  private static final int TYPE_RX_PACKETS = 1;
  private static final int TYPE_TCP_RX_PACKETS = 4;
  private static final int TYPE_TCP_TX_PACKETS = 5;
  private static final int TYPE_TX_BYTES = 2;
  private static final int TYPE_TX_PACKETS = 3;
  public static final int UID_REMOVED = -4;
  public static final int UID_TETHERING = -5;
  public static final int UNSUPPORTED = -1;
  private static NetworkStats sActiveProfilingStart;
  private static Object sProfilingLock = new Object();
  private static INetworkStatsService sStatsService;
  
  public static void clearThreadStatsTag()
  {
    NetworkManagementSocketTagger.setThreadSocketStatsTag(-1);
  }
  
  public static void clearThreadStatsUid()
  {
    NetworkManagementSocketTagger.setThreadSocketStatsUid(-1);
  }
  
  public static void closeQuietly(INetworkStatsSession paramINetworkStatsSession)
  {
    if (paramINetworkStatsSession != null) {}
    try
    {
      paramINetworkStatsSession.close();
      return;
    }
    catch (Exception paramINetworkStatsSession) {}catch (RuntimeException paramINetworkStatsSession)
    {
      throw paramINetworkStatsSession;
    }
  }
  
  private static NetworkStats getDataLayerSnapshotForUid(Context paramContext)
  {
    int i = Process.myUid();
    try
    {
      paramContext = getStatsService().getDataLayerSnapshotForUid(i);
      return paramContext;
    }
    catch (RemoteException paramContext)
    {
      throw paramContext.rethrowFromSystemServer();
    }
  }
  
  private static String[] getMobileIfaces()
  {
    try
    {
      String[] arrayOfString = getStatsService().getMobileIfaces();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static long getMobileRxBytes()
  {
    long l = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      l += getRxBytes(arrayOfString[i]);
      i += 1;
    }
    return l;
  }
  
  public static long getMobileRxPackets()
  {
    long l = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      l += getRxPackets(arrayOfString[i]);
      i += 1;
    }
    return l;
  }
  
  public static long getMobileTcpRxPackets()
  {
    long l1 = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      long l3 = nativeGetIfaceStat(arrayOfString[i], 4);
      long l2 = l1;
      if (l3 != -1L) {
        l2 = l1 + l3;
      }
      i += 1;
      l1 = l2;
    }
    return l1;
  }
  
  public static long getMobileTcpTxPackets()
  {
    long l1 = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      long l3 = nativeGetIfaceStat(arrayOfString[i], 5);
      long l2 = l1;
      if (l3 != -1L) {
        l2 = l1 + l3;
      }
      i += 1;
      l1 = l2;
    }
    return l1;
  }
  
  public static long getMobileTxBytes()
  {
    long l = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      l += getTxBytes(arrayOfString[i]);
      i += 1;
    }
    return l;
  }
  
  public static long getMobileTxPackets()
  {
    long l = 0L;
    String[] arrayOfString = getMobileIfaces();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      l += getTxPackets(arrayOfString[i]);
      i += 1;
    }
    return l;
  }
  
  public static long getRxBytes(String paramString)
  {
    return nativeGetIfaceStat(paramString, 0);
  }
  
  public static long getRxPackets(String paramString)
  {
    return nativeGetIfaceStat(paramString, 1);
  }
  
  private static INetworkStatsService getStatsService()
  {
    try
    {
      if (sStatsService == null) {
        sStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
      }
      INetworkStatsService localINetworkStatsService = sStatsService;
      return localINetworkStatsService;
    }
    finally {}
  }
  
  public static int getThreadStatsTag()
  {
    return NetworkManagementSocketTagger.getThreadSocketStatsTag();
  }
  
  public static long getTotalRxBytes()
  {
    return nativeGetTotalStat(0);
  }
  
  public static long getTotalRxPackets()
  {
    return nativeGetTotalStat(1);
  }
  
  public static long getTotalTxBytes()
  {
    return nativeGetTotalStat(2);
  }
  
  public static long getTotalTxPackets()
  {
    return nativeGetTotalStat(3);
  }
  
  public static long getTxBytes(String paramString)
  {
    return nativeGetIfaceStat(paramString, 2);
  }
  
  public static long getTxPackets(String paramString)
  {
    return nativeGetIfaceStat(paramString, 3);
  }
  
  public static long getUidRxBytes(int paramInt)
  {
    int i = Process.myUid();
    if ((i == 1000) || (i == paramInt)) {
      return nativeGetUidStat(paramInt, 0);
    }
    return -1L;
  }
  
  public static long getUidRxPackets(int paramInt)
  {
    int i = Process.myUid();
    if ((i == 1000) || (i == paramInt)) {
      return nativeGetUidStat(paramInt, 1);
    }
    return -1L;
  }
  
  @Deprecated
  public static long getUidTcpRxBytes(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidTcpRxSegments(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidTcpTxBytes(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidTcpTxSegments(int paramInt)
  {
    return -1L;
  }
  
  public static long getUidTxBytes(int paramInt)
  {
    int i = Process.myUid();
    if ((i == 1000) || (i == paramInt)) {
      return nativeGetUidStat(paramInt, 2);
    }
    return -1L;
  }
  
  public static long getUidTxPackets(int paramInt)
  {
    int i = Process.myUid();
    if ((i == 1000) || (i == paramInt)) {
      return nativeGetUidStat(paramInt, 3);
    }
    return -1L;
  }
  
  @Deprecated
  public static long getUidUdpRxBytes(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidUdpRxPackets(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidUdpTxBytes(int paramInt)
  {
    return -1L;
  }
  
  @Deprecated
  public static long getUidUdpTxPackets(int paramInt)
  {
    return -1L;
  }
  
  public static void incrementOperationCount(int paramInt)
  {
    incrementOperationCount(getThreadStatsTag(), paramInt);
  }
  
  public static void incrementOperationCount(int paramInt1, int paramInt2)
  {
    int i = Process.myUid();
    try
    {
      getStatsService().incrementOperationCount(i, paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private static native long nativeGetIfaceStat(String paramString, int paramInt);
  
  private static native long nativeGetTotalStat(int paramInt);
  
  private static native long nativeGetUidStat(int paramInt1, int paramInt2);
  
  public static void setThreadStatsTag(int paramInt)
  {
    NetworkManagementSocketTagger.setThreadSocketStatsTag(paramInt);
  }
  
  public static void setThreadStatsTagBackup()
  {
    setThreadStatsTag(65283);
  }
  
  public static void setThreadStatsTagRestore()
  {
    setThreadStatsTag(65284);
  }
  
  public static void setThreadStatsUid(int paramInt)
  {
    NetworkManagementSocketTagger.setThreadSocketStatsUid(paramInt);
  }
  
  public static void startDataProfiling(Context paramContext)
  {
    synchronized (sProfilingLock)
    {
      if (sActiveProfilingStart != null) {
        throw new IllegalStateException("already profiling data");
      }
    }
    sActiveProfilingStart = getDataLayerSnapshotForUid(paramContext);
  }
  
  public static NetworkStats stopDataProfiling(Context paramContext)
  {
    synchronized (sProfilingLock)
    {
      if (sActiveProfilingStart == null) {
        throw new IllegalStateException("not profiling data");
      }
    }
    paramContext = NetworkStats.subtract(getDataLayerSnapshotForUid(paramContext), sActiveProfilingStart, null, null);
    sActiveProfilingStart = null;
    return paramContext;
  }
  
  public static void tagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    SocketTagger.get().tag(paramDatagramSocket);
  }
  
  public static void tagSocket(Socket paramSocket)
    throws SocketException
  {
    SocketTagger.get().tag(paramSocket);
  }
  
  public static void untagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    SocketTagger.get().untag(paramDatagramSocket);
  }
  
  public static void untagSocket(Socket paramSocket)
    throws SocketException
  {
    SocketTagger.get().untag(paramSocket);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/TrafficStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */