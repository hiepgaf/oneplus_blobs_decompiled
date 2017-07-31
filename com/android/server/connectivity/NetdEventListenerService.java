package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest.Builder;
import android.net.metrics.DnsEvent;
import android.net.metrics.INetdEventListener.Stub;
import android.net.metrics.IpConnectivityLog;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class NetdEventListenerService
  extends INetdEventListener.Stub
{
  private static final boolean DBG = true;
  private static final int MAX_LOOKUPS_PER_DNS_EVENT = 100;
  public static final String SERVICE_NAME = "netd_listener";
  private static final String TAG = NetdEventListenerService.class.getSimpleName();
  private static final boolean VDBG = false;
  private final ConnectivityManager mCm;
  @GuardedBy("this")
  private final SortedMap<Integer, DnsEventBatch> mEventBatches = new TreeMap();
  private final IpConnectivityLog mMetricsLog;
  private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onLost(Network paramAnonymousNetwork)
    {
      synchronized (NetdEventListenerService.this)
      {
        paramAnonymousNetwork = (NetdEventListenerService.DnsEventBatch)NetdEventListenerService.-get0(NetdEventListenerService.this).remove(Integer.valueOf(paramAnonymousNetwork.netId));
        if (paramAnonymousNetwork != null) {
          paramAnonymousNetwork.logAndClear();
        }
        return;
      }
    }
  };
  
  public NetdEventListenerService(Context paramContext)
  {
    this((ConnectivityManager)paramContext.getSystemService(ConnectivityManager.class), new IpConnectivityLog());
  }
  
  public NetdEventListenerService(ConnectivityManager paramConnectivityManager, IpConnectivityLog paramIpConnectivityLog)
  {
    this.mCm = paramConnectivityManager;
    this.mMetricsLog = paramIpConnectivityLog;
    paramConnectivityManager = new NetworkRequest.Builder().clearCapabilities().build();
    this.mCm.registerNetworkCallback(paramConnectivityManager, this.mNetworkCallback);
  }
  
  private static void maybeLog(String paramString)
  {
    Log.d(TAG, paramString);
  }
  
  private static void maybeVerboseLog(String paramString) {}
  
  public void dump(PrintWriter paramPrintWriter)
  {
    try
    {
      paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
      paramPrintWriter.println(TAG + ":");
      paramPrintWriter.increaseIndent();
      Iterator localIterator = this.mEventBatches.values().iterator();
      while (localIterator.hasNext()) {
        paramPrintWriter.println(((DnsEventBatch)localIterator.next()).toString());
      }
      paramPrintWriter.decreaseIndent();
    }
    finally {}
  }
  
  public void onDnsEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      maybeVerboseLog(String.format("onDnsEvent(%d, %d, %d, %d)", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) }));
      DnsEventBatch localDnsEventBatch2 = (DnsEventBatch)this.mEventBatches.get(Integer.valueOf(paramInt1));
      DnsEventBatch localDnsEventBatch1 = localDnsEventBatch2;
      if (localDnsEventBatch2 == null)
      {
        localDnsEventBatch1 = new DnsEventBatch(paramInt1);
        this.mEventBatches.put(Integer.valueOf(paramInt1), localDnsEventBatch1);
      }
      localDnsEventBatch1.addResult((byte)paramInt2, (byte)paramInt3, paramInt4);
      return;
    }
    finally {}
  }
  
  private class DnsEventBatch
  {
    private int mEventCount;
    private final byte[] mEventTypes = new byte[100];
    private final int[] mLatenciesMs = new int[100];
    private final int mNetId;
    private final byte[] mReturnCodes = new byte[100];
    
    public DnsEventBatch(int paramInt)
    {
      this.mNetId = paramInt;
    }
    
    public void addResult(byte paramByte1, byte paramByte2, int paramInt)
    {
      this.mEventTypes[this.mEventCount] = paramByte1;
      this.mReturnCodes[this.mEventCount] = paramByte2;
      this.mLatenciesMs[this.mEventCount] = paramInt;
      this.mEventCount += 1;
      if (this.mEventCount == 100) {
        logAndClear();
      }
    }
    
    public void logAndClear()
    {
      if (this.mEventCount == 0) {
        return;
      }
      byte[] arrayOfByte1 = Arrays.copyOf(this.mEventTypes, this.mEventCount);
      byte[] arrayOfByte2 = Arrays.copyOf(this.mReturnCodes, this.mEventCount);
      int[] arrayOfInt = Arrays.copyOf(this.mLatenciesMs, this.mEventCount);
      NetdEventListenerService.-get1(NetdEventListenerService.this).log(new DnsEvent(this.mNetId, arrayOfByte1, arrayOfByte2, arrayOfInt));
      NetdEventListenerService.-wrap0(String.format("Logging %d results for netId %d", new Object[] { Integer.valueOf(this.mEventCount), Integer.valueOf(this.mNetId) }));
      this.mEventCount = 0;
    }
    
    public String toString()
    {
      return String.format("%s %d %d", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.mNetId), Integer.valueOf(this.mEventCount) });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/NetdEventListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */