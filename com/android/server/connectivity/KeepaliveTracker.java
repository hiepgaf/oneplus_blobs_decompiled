package com.android.server.connectivity;

import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.util.IpUtils;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.HexDump;
import com.android.internal.util.IndentingPrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class KeepaliveTracker
{
  private static final boolean DBG = false;
  public static final String PERMISSION = "android.permission.PACKET_KEEPALIVE_OFFLOAD";
  private static final String TAG = "KeepaliveTracker";
  private final Handler mConnectivityServiceHandler;
  private final HashMap<NetworkAgentInfo, HashMap<Integer, KeepaliveInfo>> mKeepalives = new HashMap();
  
  public KeepaliveTracker(Handler paramHandler)
  {
    this.mConnectivityServiceHandler = paramHandler;
  }
  
  private int findFirstFreeSlot(NetworkAgentInfo paramNetworkAgentInfo)
  {
    HashMap localHashMap2 = (HashMap)this.mKeepalives.get(paramNetworkAgentInfo);
    HashMap localHashMap1 = localHashMap2;
    if (localHashMap2 == null)
    {
      localHashMap1 = new HashMap();
      this.mKeepalives.put(paramNetworkAgentInfo, localHashMap1);
    }
    int i = 1;
    while (i <= localHashMap1.size())
    {
      if (localHashMap1.get(Integer.valueOf(i)) == null) {
        return i;
      }
      i += 1;
    }
    return i;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("Packet keepalives:");
    paramIndentingPrintWriter.increaseIndent();
    Iterator localIterator1 = this.mKeepalives.keySet().iterator();
    while (localIterator1.hasNext())
    {
      NetworkAgentInfo localNetworkAgentInfo = (NetworkAgentInfo)localIterator1.next();
      paramIndentingPrintWriter.println(localNetworkAgentInfo.name());
      paramIndentingPrintWriter.increaseIndent();
      Iterator localIterator2 = ((HashMap)this.mKeepalives.get(localNetworkAgentInfo)).keySet().iterator();
      while (localIterator2.hasNext())
      {
        int i = ((Integer)localIterator2.next()).intValue();
        KeepaliveInfo localKeepaliveInfo = (KeepaliveInfo)((HashMap)this.mKeepalives.get(localNetworkAgentInfo)).get(Integer.valueOf(i));
        paramIndentingPrintWriter.println(i + ": " + localKeepaliveInfo.toString());
      }
      paramIndentingPrintWriter.decreaseIndent();
    }
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  public void handleCheckKeepalivesStillValid(NetworkAgentInfo paramNetworkAgentInfo)
  {
    Object localObject1 = (HashMap)this.mKeepalives.get(paramNetworkAgentInfo);
    if (localObject1 != null)
    {
      Object localObject2 = new ArrayList();
      Iterator localIterator = ((HashMap)localObject1).keySet().iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        int j = KeepaliveInfo.-wrap0((KeepaliveInfo)((HashMap)localObject1).get(Integer.valueOf(i)));
        if (j != 0) {
          ((ArrayList)localObject2).add(Pair.create(Integer.valueOf(i), Integer.valueOf(j)));
        }
      }
      localObject1 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Pair)((Iterator)localObject1).next();
        handleStopKeepalive(paramNetworkAgentInfo, ((Integer)((Pair)localObject2).first).intValue(), ((Integer)((Pair)localObject2).second).intValue());
      }
    }
  }
  
  public void handleEventPacketKeepalive(NetworkAgentInfo paramNetworkAgentInfo, Message paramMessage)
  {
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    paramMessage = null;
    try
    {
      KeepaliveInfo localKeepaliveInfo = (KeepaliveInfo)((HashMap)this.mKeepalives.get(paramNetworkAgentInfo)).get(Integer.valueOf(i));
      paramMessage = localKeepaliveInfo;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;) {}
    }
    if (paramMessage == null)
    {
      Log.e("KeepaliveTracker", "Event for unknown keepalive " + i + " on " + paramNetworkAgentInfo.name());
      return;
    }
    if ((j != 0) || (paramMessage.isStarted))
    {
      paramMessage.isStarted = false;
      if (j == 0) {}
      handleStopKeepalive(paramNetworkAgentInfo, i, j);
      return;
    }
    paramMessage.isStarted = true;
    paramMessage.notifyMessenger(i, j);
  }
  
  public void handleStartKeepalive(Message paramMessage)
  {
    paramMessage = (KeepaliveInfo)paramMessage.obj;
    NetworkAgentInfo localNetworkAgentInfo = paramMessage.getNai();
    int i = findFirstFreeSlot(localNetworkAgentInfo);
    ((HashMap)this.mKeepalives.get(localNetworkAgentInfo)).put(Integer.valueOf(i), paramMessage);
    paramMessage.start(i);
  }
  
  public void handleStopAllKeepalives(NetworkAgentInfo paramNetworkAgentInfo, int paramInt)
  {
    HashMap localHashMap = (HashMap)this.mKeepalives.get(paramNetworkAgentInfo);
    if (localHashMap != null)
    {
      Iterator localIterator = localHashMap.values().iterator();
      while (localIterator.hasNext()) {
        ((KeepaliveInfo)localIterator.next()).stop(paramInt);
      }
      localHashMap.clear();
      this.mKeepalives.remove(paramNetworkAgentInfo);
    }
  }
  
  public void handleStopKeepalive(NetworkAgentInfo paramNetworkAgentInfo, int paramInt1, int paramInt2)
  {
    if (paramNetworkAgentInfo == null) {}
    HashMap localHashMap;
    for (String str = "(null)";; str = paramNetworkAgentInfo.name())
    {
      localHashMap = (HashMap)this.mKeepalives.get(paramNetworkAgentInfo);
      if (localHashMap != null) {
        break;
      }
      Log.e("KeepaliveTracker", "Attempt to stop keepalive on nonexistent network " + str);
      return;
    }
    KeepaliveInfo localKeepaliveInfo = (KeepaliveInfo)localHashMap.get(Integer.valueOf(paramInt1));
    if (localKeepaliveInfo == null)
    {
      Log.e("KeepaliveTracker", "Attempt to stop nonexistent keepalive " + paramInt1 + " on " + str);
      return;
    }
    localKeepaliveInfo.stop(paramInt2);
    localHashMap.remove(Integer.valueOf(paramInt1));
    if (localHashMap.isEmpty()) {
      this.mKeepalives.remove(paramNetworkAgentInfo);
    }
  }
  
  void notifyMessenger(Messenger paramMessenger, int paramInt1, int paramInt2)
  {
    Message localMessage = Message.obtain();
    localMessage.what = 528397;
    localMessage.arg1 = paramInt1;
    localMessage.arg2 = paramInt2;
    localMessage.obj = null;
    try
    {
      paramMessenger.send(localMessage);
      return;
    }
    catch (RemoteException paramMessenger) {}
  }
  
  public void startNattKeepalive(NetworkAgentInfo paramNetworkAgentInfo, int paramInt1, Messenger paramMessenger, IBinder paramIBinder, String paramString1, int paramInt2, String paramString2, int paramInt3)
  {
    if (paramNetworkAgentInfo == null)
    {
      notifyMessenger(paramMessenger, -1, -20);
      return;
    }
    try
    {
      paramString1 = NetworkUtils.numericToInetAddress(paramString1);
      paramString2 = NetworkUtils.numericToInetAddress(paramString2);
      return;
    }
    catch (IllegalArgumentException paramNetworkAgentInfo)
    {
      try
      {
        paramString1 = KeepalivePacketData.nattKeepalivePacket(paramString1, paramInt2, paramString2, 4500);
        paramNetworkAgentInfo = new KeepaliveInfo(paramMessenger, paramIBinder, paramNetworkAgentInfo, paramString1, paramInt1);
        Log.d("KeepaliveTracker", "Created keepalive: " + paramNetworkAgentInfo.toString());
        this.mConnectivityServiceHandler.obtainMessage(528395, paramNetworkAgentInfo).sendToTarget();
        return;
      }
      catch (KeepalivePacketData.InvalidPacketException paramNetworkAgentInfo)
      {
        notifyMessenger(paramMessenger, -1, paramNetworkAgentInfo.error);
      }
      paramNetworkAgentInfo = paramNetworkAgentInfo;
      notifyMessenger(paramMessenger, -1, -21);
      return;
    }
  }
  
  class KeepaliveInfo
    implements IBinder.DeathRecipient
  {
    public boolean isStarted;
    private final IBinder mBinder;
    private final int mInterval;
    private final Messenger mMessenger;
    private final NetworkAgentInfo mNai;
    private final KeepalivePacketData mPacket;
    private final int mPid;
    private int mSlot = -1;
    private final int mUid;
    
    public KeepaliveInfo(Messenger paramMessenger, IBinder paramIBinder, NetworkAgentInfo paramNetworkAgentInfo, KeepalivePacketData paramKeepalivePacketData, int paramInt)
    {
      this.mMessenger = paramMessenger;
      this.mBinder = paramIBinder;
      this.mPid = Binder.getCallingPid();
      this.mUid = Binder.getCallingUid();
      this.mNai = paramNetworkAgentInfo;
      this.mPacket = paramKeepalivePacketData;
      this.mInterval = paramInt;
      try
      {
        this.mBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1)
      {
        binderDied();
      }
    }
    
    private int checkInterval()
    {
      if (this.mInterval >= 20) {
        return 0;
      }
      return -24;
    }
    
    private int checkNetworkConnected()
    {
      if (!this.mNai.networkInfo.isConnectedOrConnecting()) {
        return -20;
      }
      return 0;
    }
    
    private int checkSourceAddress()
    {
      Iterator localIterator = this.mNai.linkProperties.getAddresses().iterator();
      while (localIterator.hasNext()) {
        if (((InetAddress)localIterator.next()).equals(this.mPacket.srcAddress)) {
          return 0;
        }
      }
      return -21;
    }
    
    private int isValid()
    {
      synchronized (this.mNai)
      {
        int j = checkInterval();
        int i = j;
        if (j == 0) {
          i = checkNetworkConnected();
        }
        j = i;
        if (i == 0) {
          j = checkSourceAddress();
        }
        return j;
      }
    }
    
    public void binderDied()
    {
      KeepaliveTracker.-get0(KeepaliveTracker.this).obtainMessage(528396, this.mSlot, -10, this.mNai.network).sendToTarget();
    }
    
    public NetworkAgentInfo getNai()
    {
      return this.mNai;
    }
    
    void notifyMessenger(int paramInt1, int paramInt2)
    {
      KeepaliveTracker.this.notifyMessenger(this.mMessenger, paramInt1, paramInt2);
    }
    
    void start(int paramInt)
    {
      int i = isValid();
      if (i == 0)
      {
        this.mSlot = paramInt;
        Log.d("KeepaliveTracker", "Starting keepalive " + this.mSlot + " on " + this.mNai.name());
        this.mNai.asyncChannel.sendMessage(528395, paramInt, this.mInterval, this.mPacket);
        return;
      }
      notifyMessenger(-1, i);
    }
    
    void stop(int paramInt)
    {
      int i = Binder.getCallingUid();
      if (((i == this.mUid) || (i == 1000)) || (this.isStarted))
      {
        Log.d("KeepaliveTracker", "Stopping keepalive " + this.mSlot + " on " + this.mNai.name());
        this.mNai.asyncChannel.sendMessage(528396, this.mSlot);
      }
      notifyMessenger(this.mSlot, paramInt);
      unlinkDeathRecipient();
    }
    
    public String toString()
    {
      return "KeepaliveInfo [" + " network=" + this.mNai.network + " isStarted=" + this.isStarted + " " + IpUtils.addressAndPortToString(this.mPacket.srcAddress, this.mPacket.srcPort) + "->" + IpUtils.addressAndPortToString(this.mPacket.dstAddress, this.mPacket.dstPort) + new StringBuilder().append(" interval=").append(this.mInterval).toString() + new StringBuilder().append(" data=").append(HexDump.toHexString(this.mPacket.data)).toString() + " uid=" + this.mUid + " pid=" + this.mPid + " ]";
    }
    
    void unlinkDeathRecipient()
    {
      if (this.mBinder != null) {
        this.mBinder.unlinkToDeath(this, 0);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/KeepaliveTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */