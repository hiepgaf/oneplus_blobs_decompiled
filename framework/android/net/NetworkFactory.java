package android.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class NetworkFactory
  extends Handler
{
  private static final int BASE = 536576;
  public static final int CMD_CANCEL_REQUEST = 536577;
  public static final int CMD_REQUEST_NETWORK = 536576;
  private static final int CMD_SET_FILTER = 536579;
  private static final int CMD_SET_SCORE = 536578;
  private static final boolean DBG = true;
  private static final boolean VDBG = true;
  private final String LOG_TAG;
  private NetworkCapabilities mCapabilityFilter;
  private final Context mContext;
  private Messenger mMessenger = null;
  private final SparseArray<NetworkRequestInfo> mNetworkRequests = new SparseArray();
  private int mRefCount = 0;
  private int mScore;
  
  public NetworkFactory(Looper paramLooper, Context paramContext, String paramString, NetworkCapabilities paramNetworkCapabilities)
  {
    super(paramLooper);
    this.LOG_TAG = paramString;
    this.mContext = paramContext;
    this.mCapabilityFilter = paramNetworkCapabilities;
  }
  
  private void evalRequest(NetworkRequestInfo paramNetworkRequestInfo)
  {
    log("evalRequest");
    if ((!paramNetworkRequestInfo.requested) && (paramNetworkRequestInfo.score < this.mScore) && (paramNetworkRequestInfo.request.networkCapabilities.satisfiedByNetworkCapabilities(this.mCapabilityFilter)) && (acceptRequest(paramNetworkRequestInfo.request, paramNetworkRequestInfo.score)))
    {
      log("  needNetworkFor");
      needNetworkFor(paramNetworkRequestInfo.request, paramNetworkRequestInfo.score);
      paramNetworkRequestInfo.requested = true;
      return;
    }
    if (paramNetworkRequestInfo.requested)
    {
      if ((paramNetworkRequestInfo.score > this.mScore) || (!paramNetworkRequestInfo.request.networkCapabilities.satisfiedByNetworkCapabilities(this.mCapabilityFilter))) {}
      while (!acceptRequest(paramNetworkRequestInfo.request, paramNetworkRequestInfo.score))
      {
        log("  releaseNetworkFor");
        releaseNetworkFor(paramNetworkRequestInfo.request);
        paramNetworkRequestInfo.requested = false;
        return;
      }
    }
    log("  done");
  }
  
  private void evalRequests()
  {
    int i = 0;
    while (i < this.mNetworkRequests.size())
    {
      evalRequest((NetworkRequestInfo)this.mNetworkRequests.valueAt(i));
      i += 1;
    }
  }
  
  private void handleSetFilter(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mCapabilityFilter = paramNetworkCapabilities;
    evalRequests();
  }
  
  private void handleSetScore(int paramInt)
  {
    this.mScore = paramInt;
    evalRequests();
  }
  
  public boolean acceptRequest(NetworkRequest paramNetworkRequest, int paramInt)
  {
    return true;
  }
  
  public void addNetworkRequest(NetworkRequest paramNetworkRequest, int paramInt)
  {
    sendMessage(obtainMessage(536576, new NetworkRequestInfo(paramNetworkRequest, paramInt)));
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new IndentingPrintWriter(paramPrintWriter, "  ");
    paramFileDescriptor.println(toString());
    paramFileDescriptor.increaseIndent();
    int i = 0;
    while (i < this.mNetworkRequests.size())
    {
      paramFileDescriptor.println(this.mNetworkRequests.valueAt(i));
      i += 1;
    }
    paramFileDescriptor.decreaseIndent();
  }
  
  protected int getRequestCount()
  {
    return this.mNetworkRequests.size();
  }
  
  protected void handleAddRequest(NetworkRequest paramNetworkRequest, int paramInt)
  {
    NetworkRequestInfo localNetworkRequestInfo = (NetworkRequestInfo)this.mNetworkRequests.get(paramNetworkRequest.requestId);
    if (localNetworkRequestInfo == null)
    {
      log("got request " + paramNetworkRequest + " with score " + paramInt);
      paramNetworkRequest = new NetworkRequestInfo(paramNetworkRequest, paramInt);
      this.mNetworkRequests.put(paramNetworkRequest.request.requestId, paramNetworkRequest);
    }
    for (;;)
    {
      log("  my score=" + this.mScore + ", my filter=" + this.mCapabilityFilter);
      evalRequest(paramNetworkRequest);
      return;
      log("new score " + paramInt + " for exisiting request " + paramNetworkRequest);
      localNetworkRequestInfo.score = paramInt;
      paramNetworkRequest = localNetworkRequestInfo;
    }
  }
  
  public void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    case 536576: 
      handleAddRequest((NetworkRequest)paramMessage.obj, paramMessage.arg1);
      return;
    case 536577: 
      handleRemoveRequest((NetworkRequest)paramMessage.obj);
      return;
    case 536578: 
      handleSetScore(paramMessage.arg1);
      return;
    }
    handleSetFilter((NetworkCapabilities)paramMessage.obj);
  }
  
  protected void handleRemoveRequest(NetworkRequest paramNetworkRequest)
  {
    NetworkRequestInfo localNetworkRequestInfo = (NetworkRequestInfo)this.mNetworkRequests.get(paramNetworkRequest.requestId);
    if (localNetworkRequestInfo != null)
    {
      this.mNetworkRequests.remove(paramNetworkRequest.requestId);
      if (localNetworkRequestInfo.requested) {
        releaseNetworkFor(localNetworkRequestInfo.request);
      }
    }
  }
  
  protected void log(String paramString)
  {
    Log.d(this.LOG_TAG, paramString);
  }
  
  protected void needNetworkFor(NetworkRequest paramNetworkRequest, int paramInt)
  {
    paramInt = this.mRefCount + 1;
    this.mRefCount = paramInt;
    if (paramInt == 1) {
      startNetwork();
    }
  }
  
  public void register()
  {
    log("Registering NetworkFactory");
    if (this.mMessenger == null)
    {
      this.mMessenger = new Messenger(this);
      ConnectivityManager.from(this.mContext).registerNetworkFactory(this.mMessenger, this.LOG_TAG);
    }
  }
  
  protected void releaseNetworkFor(NetworkRequest paramNetworkRequest)
  {
    int i = this.mRefCount - 1;
    this.mRefCount = i;
    if (i == 0) {
      stopNetwork();
    }
  }
  
  public void removeNetworkRequest(NetworkRequest paramNetworkRequest)
  {
    sendMessage(obtainMessage(536577, paramNetworkRequest));
  }
  
  public void setCapabilityFilter(NetworkCapabilities paramNetworkCapabilities)
  {
    sendMessage(obtainMessage(536579, new NetworkCapabilities(paramNetworkCapabilities)));
  }
  
  public void setScoreFilter(int paramInt)
  {
    sendMessage(obtainMessage(536578, paramInt, 0));
  }
  
  protected void startNetwork() {}
  
  protected void stopNetwork() {}
  
  public String toString()
  {
    return "{" + this.LOG_TAG + " - ScoreFilter=" + this.mScore + ", Filter=" + this.mCapabilityFilter + ", requests=" + this.mNetworkRequests.size() + ", refCount=" + this.mRefCount + "}";
  }
  
  public void unregister()
  {
    log("Unregistering NetworkFactory");
    if (this.mMessenger != null)
    {
      ConnectivityManager.from(this.mContext).unregisterNetworkFactory(this.mMessenger);
      this.mMessenger = null;
    }
  }
  
  private class NetworkRequestInfo
  {
    public final NetworkRequest request;
    public boolean requested;
    public int score;
    
    public NetworkRequestInfo(NetworkRequest paramNetworkRequest, int paramInt)
    {
      this.request = paramNetworkRequest;
      this.score = paramInt;
      this.requested = false;
    }
    
    public String toString()
    {
      return "{" + this.request + ", score=" + this.score + ", requested=" + this.requested + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */