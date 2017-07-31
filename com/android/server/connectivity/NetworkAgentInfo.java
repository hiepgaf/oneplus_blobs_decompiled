package com.android.server.connectivity;

import android.content.Context;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkMisc;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Messenger;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.WakeupMessage;
import com.android.server.ConnectivityService;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class NetworkAgentInfo
  implements Comparable<NetworkAgentInfo>
{
  private static final boolean ADD = true;
  public static final int EVENT_NETWORK_LINGER_COMPLETE = 1001;
  private static final int MAXIMUM_NETWORK_SCORE = 100;
  private static final boolean REMOVE = false;
  private static final String TAG = ConnectivityService.class.getSimpleName();
  private static final int UNVALIDATED_SCORE_PENALTY = 40;
  private static final boolean VDBG = false;
  public final AsyncChannel asyncChannel;
  public boolean avoidUnvalidated;
  public Nat464Xlat clatd;
  public boolean created;
  private int currentScore;
  public boolean everCaptivePortalDetected;
  public boolean everConnected;
  public boolean everValidated;
  public boolean lastCaptivePortalDetected;
  public boolean lastValidated;
  public LinkProperties linkProperties;
  private final ConnectivityService mConnService;
  private final Context mContext;
  private final Handler mHandler;
  private long mLingerExpiryMs;
  private WakeupMessage mLingerMessage;
  private final SparseArray<LingerTimer> mLingerTimerForRequest = new SparseArray();
  private final SortedSet<LingerTimer> mLingerTimers = new TreeSet();
  private boolean mLingering;
  private final SparseArray<NetworkRequest> mNetworkRequests = new SparseArray();
  private int mNumBackgroundNetworkRequests = 0;
  private int mNumRequestNetworkRequests = 0;
  public final Messenger messenger;
  public final Network network;
  public NetworkCapabilities networkCapabilities;
  public NetworkInfo networkInfo;
  public final NetworkMisc networkMisc;
  public final NetworkMonitor networkMonitor;
  
  public NetworkAgentInfo(Messenger paramMessenger, AsyncChannel paramAsyncChannel, Network paramNetwork, NetworkInfo paramNetworkInfo, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt, Context paramContext, Handler paramHandler, NetworkMisc paramNetworkMisc, NetworkRequest paramNetworkRequest, ConnectivityService paramConnectivityService)
  {
    this.messenger = paramMessenger;
    this.asyncChannel = paramAsyncChannel;
    this.network = paramNetwork;
    this.networkInfo = paramNetworkInfo;
    this.linkProperties = paramLinkProperties;
    this.networkCapabilities = paramNetworkCapabilities;
    this.currentScore = paramInt;
    this.mConnService = paramConnectivityService;
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.networkMonitor = this.mConnService.createNetworkMonitor(paramContext, paramHandler, this, paramNetworkRequest);
    this.networkMisc = paramNetworkMisc;
  }
  
  private int getCurrentScore(boolean paramBoolean)
  {
    if ((this.networkMisc.explicitlySelected) && ((this.networkMisc.acceptUnvalidated) || (paramBoolean))) {
      return 100;
    }
    int j = this.currentScore;
    int i = j;
    if (!this.lastValidated)
    {
      if (!paramBoolean) {
        break label57;
      }
      i = j;
    }
    for (;;)
    {
      j = i;
      if (i < 0) {
        j = 0;
      }
      return j;
      label57:
      i = j;
      if (!ignoreWifiUnvalidationPenalty()) {
        i = j - 40;
      }
    }
  }
  
  private boolean ignoreWifiUnvalidationPenalty()
  {
    boolean bool1;
    if (this.networkCapabilities.hasTransport(1))
    {
      bool1 = this.networkCapabilities.hasCapability(12);
      if (this.mConnService.avoidBadWifi()) {
        break label51;
      }
    }
    label51:
    for (boolean bool2 = this.avoidUnvalidated;; bool2 = true)
    {
      if ((bool1) && (!bool2)) {
        break label56;
      }
      return false;
      bool1 = false;
      break;
    }
    label56:
    return this.everValidated;
  }
  
  private void updateRequestCounts(boolean paramBoolean, NetworkRequest paramNetworkRequest)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = -1) {
      switch (-getandroid-net-NetworkRequest$TypeSwitchesValues()[paramNetworkRequest.type.ordinal()])
      {
      case 3: 
      default: 
        Log.wtf(TAG, "Unhandled request type " + paramNetworkRequest.type);
      case 2: 
        return;
      }
    }
    this.mNumRequestNetworkRequests += i;
    return;
    this.mNumRequestNetworkRequests += i;
    this.mNumBackgroundNetworkRequests += i;
  }
  
  public boolean addRequest(NetworkRequest paramNetworkRequest)
  {
    NetworkRequest localNetworkRequest = (NetworkRequest)this.mNetworkRequests.get(paramNetworkRequest.requestId);
    if (localNetworkRequest == paramNetworkRequest) {
      return false;
    }
    if (localNetworkRequest != null)
    {
      Log.wtf(TAG, String.format("Duplicate requestId for %s and %s on %s", new Object[] { paramNetworkRequest, localNetworkRequest, name() }));
      updateRequestCounts(false, localNetworkRequest);
    }
    this.mNetworkRequests.put(paramNetworkRequest.requestId, paramNetworkRequest);
    updateRequestCounts(true, paramNetworkRequest);
    return true;
  }
  
  public void clearLingerState()
  {
    if (this.mLingerMessage != null)
    {
      this.mLingerMessage.cancel();
      this.mLingerMessage = null;
    }
    this.mLingerTimers.clear();
    this.mLingerTimerForRequest.clear();
    updateLingerTimer();
    this.mLingering = false;
  }
  
  public int compareTo(NetworkAgentInfo paramNetworkAgentInfo)
  {
    return paramNetworkAgentInfo.getCurrentScore() - getCurrentScore();
  }
  
  public void dumpLingerTimers(PrintWriter paramPrintWriter)
  {
    Iterator localIterator = this.mLingerTimers.iterator();
    while (localIterator.hasNext()) {
      paramPrintWriter.println((LingerTimer)localIterator.next());
    }
  }
  
  public int getCurrentScore()
  {
    return getCurrentScore(false);
  }
  
  public int getCurrentScoreAsValidated()
  {
    return getCurrentScore(true);
  }
  
  public long getLingerExpiry()
  {
    return this.mLingerExpiryMs;
  }
  
  /* Error */
  public android.net.NetworkState getNetworkState()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 167	com/android/server/connectivity/NetworkAgentInfo:networkMisc	Landroid/net/NetworkMisc;
    //   6: ifnull +62 -> 68
    //   9: aload_0
    //   10: getfield 167	com/android/server/connectivity/NetworkAgentInfo:networkMisc	Landroid/net/NetworkMisc;
    //   13: getfield 316	android/net/NetworkMisc:subscriberId	Ljava/lang/String;
    //   16: astore_1
    //   17: new 318	android/net/NetworkState
    //   20: dup
    //   21: new 320	android/net/NetworkInfo
    //   24: dup
    //   25: aload_0
    //   26: getfield 147	com/android/server/connectivity/NetworkAgentInfo:networkInfo	Landroid/net/NetworkInfo;
    //   29: invokespecial 323	android/net/NetworkInfo:<init>	(Landroid/net/NetworkInfo;)V
    //   32: new 325	android/net/LinkProperties
    //   35: dup
    //   36: aload_0
    //   37: getfield 149	com/android/server/connectivity/NetworkAgentInfo:linkProperties	Landroid/net/LinkProperties;
    //   40: invokespecial 328	android/net/LinkProperties:<init>	(Landroid/net/LinkProperties;)V
    //   43: new 185	android/net/NetworkCapabilities
    //   46: dup
    //   47: aload_0
    //   48: getfield 151	com/android/server/connectivity/NetworkAgentInfo:networkCapabilities	Landroid/net/NetworkCapabilities;
    //   51: invokespecial 331	android/net/NetworkCapabilities:<init>	(Landroid/net/NetworkCapabilities;)V
    //   54: aload_0
    //   55: getfield 145	com/android/server/connectivity/NetworkAgentInfo:network	Landroid/net/Network;
    //   58: aload_1
    //   59: aconst_null
    //   60: invokespecial 334	android/net/NetworkState:<init>	(Landroid/net/NetworkInfo;Landroid/net/LinkProperties;Landroid/net/NetworkCapabilities;Landroid/net/Network;Ljava/lang/String;Ljava/lang/String;)V
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: areturn
    //   68: aconst_null
    //   69: astore_1
    //   70: goto -53 -> 17
    //   73: astore_1
    //   74: aload_0
    //   75: monitorexit
    //   76: aload_1
    //   77: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	this	NetworkAgentInfo
    //   16	54	1	localObject1	Object
    //   73	4	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	17	73	finally
    //   17	64	73	finally
  }
  
  public boolean isBackgroundNetwork()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (!isVPN())
    {
      bool1 = bool2;
      if (numForegroundNetworkRequests() == 0)
      {
        bool1 = bool2;
        if (this.mNumBackgroundNetworkRequests > 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean isLingering()
  {
    return this.mLingering;
  }
  
  public boolean isSatisfyingRequest(int paramInt)
  {
    return this.mNetworkRequests.get(paramInt) != null;
  }
  
  public boolean isVPN()
  {
    return this.networkCapabilities.hasTransport(4);
  }
  
  public void linger()
  {
    this.mLingering = true;
  }
  
  public void lingerRequest(NetworkRequest paramNetworkRequest, long paramLong1, long paramLong2)
  {
    if (this.mLingerTimerForRequest.get(paramNetworkRequest.requestId) != null) {
      Log.wtf(TAG, name() + ": request " + paramNetworkRequest.requestId + " already lingered");
    }
    LingerTimer localLingerTimer = new LingerTimer(paramNetworkRequest, paramLong1 + paramLong2);
    this.mLingerTimers.add(localLingerTimer);
    this.mLingerTimerForRequest.put(paramNetworkRequest.requestId, localLingerTimer);
  }
  
  public String name()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("NetworkAgentInfo [").append(this.networkInfo.getTypeName()).append(" (").append(this.networkInfo.getSubtypeName()).append(") - ");
    if (this.network == null) {}
    for (String str = "null";; str = this.network.toString()) {
      return str + "]";
    }
  }
  
  public int numBackgroundNetworkRequests()
  {
    return this.mNumBackgroundNetworkRequests;
  }
  
  public int numForegroundNetworkRequests()
  {
    return this.mNumRequestNetworkRequests - this.mNumBackgroundNetworkRequests;
  }
  
  public int numNetworkRequests()
  {
    return this.mNetworkRequests.size();
  }
  
  public int numRequestNetworkRequests()
  {
    return this.mNumRequestNetworkRequests;
  }
  
  public void removeRequest(int paramInt)
  {
    NetworkRequest localNetworkRequest = (NetworkRequest)this.mNetworkRequests.get(paramInt);
    if (localNetworkRequest == null) {
      return;
    }
    updateRequestCounts(false, localNetworkRequest);
    this.mNetworkRequests.remove(paramInt);
    if (localNetworkRequest.isRequest()) {
      unlingerRequest(localNetworkRequest);
    }
  }
  
  public NetworkRequest requestAt(int paramInt)
  {
    return (NetworkRequest)this.mNetworkRequests.valueAt(paramInt);
  }
  
  public boolean satisfies(NetworkRequest paramNetworkRequest)
  {
    if (this.created) {
      return paramNetworkRequest.networkCapabilities.satisfiedByNetworkCapabilities(this.networkCapabilities);
    }
    return false;
  }
  
  public boolean satisfiesImmutableCapabilitiesOf(NetworkRequest paramNetworkRequest)
  {
    if (this.created) {
      return paramNetworkRequest.networkCapabilities.satisfiedByImmutableNetworkCapabilities(this.networkCapabilities);
    }
    return false;
  }
  
  public void setCurrentScore(int paramInt)
  {
    this.currentScore = paramInt;
  }
  
  public String toString()
  {
    return "NetworkAgentInfo{ ni{" + this.networkInfo + "}  " + "network{" + this.network + "}  nethandle{" + this.network.getNetworkHandle() + "}  " + "lp{" + this.linkProperties + "}  " + "nc{" + this.networkCapabilities + "}  Score{" + getCurrentScore() + "}  " + "everValidated{" + this.everValidated + "}  lastValidated{" + this.lastValidated + "}  " + "created{" + this.created + "} lingering{" + isLingering() + "} " + "explicitlySelected{" + this.networkMisc.explicitlySelected + "} " + "acceptUnvalidated{" + this.networkMisc.acceptUnvalidated + "} " + "everCaptivePortalDetected{" + this.everCaptivePortalDetected + "} " + "lastCaptivePortalDetected{" + this.lastCaptivePortalDetected + "} " + "}";
  }
  
  public void unlinger()
  {
    this.mLingering = false;
  }
  
  public boolean unlingerRequest(NetworkRequest paramNetworkRequest)
  {
    LingerTimer localLingerTimer = (LingerTimer)this.mLingerTimerForRequest.get(paramNetworkRequest.requestId);
    if (localLingerTimer != null)
    {
      this.mLingerTimers.remove(localLingerTimer);
      this.mLingerTimerForRequest.remove(paramNetworkRequest.requestId);
      return true;
    }
    return false;
  }
  
  public void updateLingerTimer()
  {
    if (this.mLingerTimers.isEmpty()) {}
    for (long l = 0L; l == this.mLingerExpiryMs; l = ((LingerTimer)this.mLingerTimers.last()).expiryMs) {
      return;
    }
    if (this.mLingerMessage != null)
    {
      this.mLingerMessage.cancel();
      this.mLingerMessage = null;
    }
    if (l > 0L)
    {
      this.mLingerMessage = this.mConnService.makeWakeupMessage(this.mContext, this.mHandler, "NETWORK_LINGER_COMPLETE." + this.network.netId, 1001, this);
      this.mLingerMessage.schedule(l);
    }
    this.mLingerExpiryMs = l;
  }
  
  public static class LingerTimer
    implements Comparable<LingerTimer>
  {
    public final long expiryMs;
    public final NetworkRequest request;
    
    public LingerTimer(NetworkRequest paramNetworkRequest, long paramLong)
    {
      this.request = paramNetworkRequest;
      this.expiryMs = paramLong;
    }
    
    public int compareTo(LingerTimer paramLingerTimer)
    {
      if (this.expiryMs != paramLingerTimer.expiryMs) {
        return Long.compare(this.expiryMs, paramLingerTimer.expiryMs);
      }
      return Integer.compare(this.request.requestId, paramLingerTimer.request.requestId);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof LingerTimer)) {
        return false;
      }
      paramObject = (LingerTimer)paramObject;
      boolean bool1 = bool2;
      if (this.request.requestId == ((LingerTimer)paramObject).request.requestId)
      {
        bool1 = bool2;
        if (this.expiryMs == ((LingerTimer)paramObject).expiryMs) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { Integer.valueOf(this.request.requestId), Long.valueOf(this.expiryMs) });
    }
    
    public String toString()
    {
      return String.format("%s, expires %dms", new Object[] { this.request.toString(), Long.valueOf(this.expiryMs - SystemClock.elapsedRealtime()) });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/NetworkAgentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */