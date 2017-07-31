package android.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NetworkAgent
  extends Handler
{
  private static final int BASE = 528384;
  private static final long BW_REFRESH_MIN_WIN_MS = 500L;
  public static final int CMD_PREVENT_AUTOMATIC_RECONNECT = 528399;
  public static final int CMD_REPORT_NETWORK_STATUS = 528391;
  public static final int CMD_REQUEST_BANDWIDTH_UPDATE = 528394;
  public static final int CMD_SAVE_ACCEPT_UNVALIDATED = 528393;
  public static final int CMD_SET_SIGNAL_STRENGTH_THRESHOLDS = 528398;
  public static final int CMD_START_PACKET_KEEPALIVE = 528395;
  public static final int CMD_STOP_PACKET_KEEPALIVE = 528396;
  public static final int CMD_SUSPECT_BAD = 528384;
  private static final boolean DBG = true;
  public static final int EVENT_NETWORK_CAPABILITIES_CHANGED = 528386;
  public static final int EVENT_NETWORK_INFO_CHANGED = 528385;
  public static final int EVENT_NETWORK_PROPERTIES_CHANGED = 528387;
  public static final int EVENT_NETWORK_SCORE_CHANGED = 528388;
  public static final int EVENT_PACKET_KEEPALIVE = 528397;
  public static final int EVENT_SET_EXPLICITLY_SELECTED = 528392;
  public static final int EVENT_UID_RANGES_ADDED = 528389;
  public static final int EVENT_UID_RANGES_REMOVED = 528390;
  public static final int INVALID_NETWORK = 2;
  public static String REDIRECT_URL_KEY = "redirect URL";
  public static final int VALID_NETWORK = 1;
  private static final boolean VDBG = false;
  public static final int WIFI_BASE_SCORE = 60;
  private final String LOG_TAG;
  private volatile AsyncChannel mAsyncChannel;
  private final Context mContext;
  private volatile long mLastBwRefreshTime = 0L;
  private AtomicBoolean mPollLcePending = new AtomicBoolean(false);
  private boolean mPollLceScheduled = false;
  private final ArrayList<Message> mPreConnectedQueue = new ArrayList();
  public final int netId;
  
  public NetworkAgent(Looper paramLooper, Context paramContext, String paramString, NetworkInfo paramNetworkInfo, NetworkCapabilities paramNetworkCapabilities, LinkProperties paramLinkProperties, int paramInt)
  {
    this(paramLooper, paramContext, paramString, paramNetworkInfo, paramNetworkCapabilities, paramLinkProperties, paramInt, null);
  }
  
  public NetworkAgent(Looper paramLooper, Context paramContext, String paramString, NetworkInfo paramNetworkInfo, NetworkCapabilities paramNetworkCapabilities, LinkProperties paramLinkProperties, int paramInt, NetworkMisc paramNetworkMisc)
  {
    super(paramLooper);
    this.LOG_TAG = paramString;
    this.mContext = paramContext;
    if ((paramNetworkInfo == null) || (paramNetworkCapabilities == null)) {}
    while (paramLinkProperties == null) {
      throw new IllegalArgumentException();
    }
    this.netId = ((ConnectivityManager)this.mContext.getSystemService("connectivity")).registerNetworkAgent(new Messenger(this), new NetworkInfo(paramNetworkInfo), new LinkProperties(paramLinkProperties), new NetworkCapabilities(paramNetworkCapabilities), paramInt, paramNetworkMisc);
  }
  
  private void queueOrSendMessage(int paramInt1, int paramInt2, int paramInt3)
  {
    queueOrSendMessage(paramInt1, paramInt2, paramInt3, null);
  }
  
  private void queueOrSendMessage(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    localMessage.obj = paramObject;
    queueOrSendMessage(localMessage);
  }
  
  private void queueOrSendMessage(int paramInt, Object paramObject)
  {
    queueOrSendMessage(paramInt, 0, 0, paramObject);
  }
  
  private void queueOrSendMessage(Message paramMessage)
  {
    synchronized (this.mPreConnectedQueue)
    {
      if (this.mAsyncChannel != null)
      {
        this.mAsyncChannel.sendMessage(paramMessage);
        return;
      }
      this.mPreConnectedQueue.add(paramMessage);
    }
  }
  
  public void addUidRanges(UidRange[] paramArrayOfUidRange)
  {
    queueOrSendMessage(528389, paramArrayOfUidRange);
  }
  
  public void explicitlySelected(boolean paramBoolean)
  {
    queueOrSendMessage(528392, Boolean.valueOf(paramBoolean));
  }
  
  public void handleMessage(Message arg1)
  {
    Object localObject3;
    switch (???.what)
    {
    default: 
    case 69633: 
    case 69635: 
    case 69636: 
    case 528384: 
    case 528394: 
      long l;
      do
      {
        do
        {
          do
          {
            return;
            if (this.mAsyncChannel != null)
            {
              log("Received new connection while already connected!");
              return;
            }
            AsyncChannel localAsyncChannel = new AsyncChannel();
            localAsyncChannel.connected(null, this, ???.replyTo);
            localAsyncChannel.replyToMessage(???, 69634, 0);
            synchronized (this.mPreConnectedQueue)
            {
              this.mAsyncChannel = localAsyncChannel;
              Iterator localIterator = this.mPreConnectedQueue.iterator();
              if (localIterator.hasNext()) {
                localAsyncChannel.sendMessage((Message)localIterator.next());
              }
            }
            this.mPreConnectedQueue.clear();
            return;
          } while (this.mAsyncChannel == null);
          this.mAsyncChannel.disconnect();
          return;
          log("NetworkAgent channel lost");
          unwanted();
          synchronized (this.mPreConnectedQueue)
          {
            this.mAsyncChannel = null;
            return;
          }
          log("Unhandled Message " + ???);
          return;
          l = System.currentTimeMillis();
          if (l < this.mLastBwRefreshTime + 500L) {
            break;
          }
          this.mPollLceScheduled = false;
        } while (this.mPollLcePending.getAndSet(true));
        pollLceData();
        return;
      } while (this.mPollLceScheduled);
      this.mPollLceScheduled = sendEmptyMessageDelayed(528394, this.mLastBwRefreshTime + 500L - l + 1L);
      return;
    case 528391: 
      localObject3 = ((Bundle)???.obj).getString(REDIRECT_URL_KEY);
      networkStatus(???.arg1, (String)localObject3);
      return;
    case 528393: 
      if (???.arg1 != 0) {}
      for (boolean bool = true;; bool = false)
      {
        saveAcceptUnvalidated(bool);
        return;
      }
    case 528395: 
      startPacketKeepalive(???);
      return;
    case 528396: 
      stopPacketKeepalive(???);
      return;
    case 528398: 
      ??? = ((Bundle)???.obj).getIntegerArrayList("thresholds");
      if (??? != null) {}
      for (int i = ???.size();; i = 0)
      {
        localObject3 = new int[i];
        i = 0;
        while (i < localObject3.length)
        {
          localObject3[i] = ((Integer)???.get(i)).intValue();
          i += 1;
        }
      }
      setSignalStrengthThresholds((int[])localObject3);
      return;
    }
    preventAutomaticReconnect();
  }
  
  protected void log(String paramString)
  {
    Log.d(this.LOG_TAG, "NetworkAgent: " + paramString);
  }
  
  protected void networkStatus(int paramInt, String paramString) {}
  
  public void onPacketKeepaliveEvent(int paramInt1, int paramInt2)
  {
    queueOrSendMessage(528397, paramInt1, paramInt2);
  }
  
  protected void pollLceData() {}
  
  protected void preventAutomaticReconnect() {}
  
  public void removeUidRanges(UidRange[] paramArrayOfUidRange)
  {
    queueOrSendMessage(528390, paramArrayOfUidRange);
  }
  
  protected void saveAcceptUnvalidated(boolean paramBoolean) {}
  
  public void sendLinkProperties(LinkProperties paramLinkProperties)
  {
    queueOrSendMessage(528387, new LinkProperties(paramLinkProperties));
  }
  
  public void sendNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mPollLcePending.set(false);
    this.mLastBwRefreshTime = System.currentTimeMillis();
    queueOrSendMessage(528386, new NetworkCapabilities(paramNetworkCapabilities));
  }
  
  public void sendNetworkInfo(NetworkInfo paramNetworkInfo)
  {
    queueOrSendMessage(528385, new NetworkInfo(paramNetworkInfo));
  }
  
  public void sendNetworkScore(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Score must be >= 0");
    }
    queueOrSendMessage(528388, new Integer(paramInt));
  }
  
  protected void setSignalStrengthThresholds(int[] paramArrayOfInt) {}
  
  protected void startPacketKeepalive(Message paramMessage)
  {
    onPacketKeepaliveEvent(paramMessage.arg1, -30);
  }
  
  protected void stopPacketKeepalive(Message paramMessage)
  {
    onPacketKeepaliveEvent(paramMessage.arg1, -30);
  }
  
  protected abstract void unwanted();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */