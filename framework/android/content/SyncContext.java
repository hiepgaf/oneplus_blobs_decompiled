package android.content;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

public class SyncContext
{
  private static final long HEARTBEAT_SEND_INTERVAL_IN_MS = 1000L;
  private long mLastHeartbeatSendTime;
  private ISyncContext mSyncContext;
  
  public SyncContext(ISyncContext paramISyncContext)
  {
    this.mSyncContext = paramISyncContext;
    this.mLastHeartbeatSendTime = 0L;
  }
  
  private void updateHeartbeat()
  {
    long l = SystemClock.elapsedRealtime();
    if (l < this.mLastHeartbeatSendTime + 1000L) {
      return;
    }
    try
    {
      this.mLastHeartbeatSendTime = l;
      if (this.mSyncContext != null) {
        this.mSyncContext.sendHeartbeat();
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public IBinder getSyncContextBinder()
  {
    if (this.mSyncContext == null) {
      return null;
    }
    return this.mSyncContext.asBinder();
  }
  
  public void onFinished(SyncResult paramSyncResult)
  {
    try
    {
      if (this.mSyncContext != null) {
        this.mSyncContext.onFinished(paramSyncResult);
      }
      return;
    }
    catch (RemoteException paramSyncResult) {}
  }
  
  public void setStatusText(String paramString)
  {
    updateHeartbeat();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */