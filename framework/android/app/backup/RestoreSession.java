package android.app.backup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class RestoreSession
{
  static final String TAG = "RestoreSession";
  IRestoreSession mBinder;
  final Context mContext;
  RestoreObserverWrapper mObserver = null;
  
  RestoreSession(Context paramContext, IRestoreSession paramIRestoreSession)
  {
    this.mContext = paramContext;
    this.mBinder = paramIRestoreSession;
  }
  
  public void endRestoreSession()
  {
    try
    {
      this.mBinder.endRestoreSession();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.d("RestoreSession", "Can't contact server to get available sets");
      }
    }
    finally
    {
      this.mBinder = null;
    }
  }
  
  public int getAvailableRestoreSets(RestoreObserver paramRestoreObserver)
  {
    paramRestoreObserver = new RestoreObserverWrapper(this.mContext, paramRestoreObserver);
    try
    {
      int i = this.mBinder.getAvailableRestoreSets(paramRestoreObserver);
      return i;
    }
    catch (RemoteException paramRestoreObserver)
    {
      Log.d("RestoreSession", "Can't contact server to get available sets");
    }
    return -1;
  }
  
  public int restoreAll(long paramLong, RestoreObserver paramRestoreObserver)
  {
    if (this.mObserver != null)
    {
      Log.d("RestoreSession", "restoreAll() called during active restore");
      return -1;
    }
    this.mObserver = new RestoreObserverWrapper(this.mContext, paramRestoreObserver);
    try
    {
      int i = this.mBinder.restoreAll(paramLong, this.mObserver);
      return i;
    }
    catch (RemoteException paramRestoreObserver)
    {
      Log.d("RestoreSession", "Can't contact server to restore");
    }
    return -1;
  }
  
  public int restorePackage(String paramString, RestoreObserver paramRestoreObserver)
  {
    if (this.mObserver != null)
    {
      Log.d("RestoreSession", "restorePackage() called during active restore");
      return -1;
    }
    this.mObserver = new RestoreObserverWrapper(this.mContext, paramRestoreObserver);
    try
    {
      int i = this.mBinder.restorePackage(paramString, this.mObserver);
      return i;
    }
    catch (RemoteException paramString)
    {
      Log.d("RestoreSession", "Can't contact server to restore package");
    }
    return -1;
  }
  
  public int restoreSome(long paramLong, RestoreObserver paramRestoreObserver, String[] paramArrayOfString)
  {
    if (this.mObserver != null)
    {
      Log.d("RestoreSession", "restoreAll() called during active restore");
      return -1;
    }
    this.mObserver = new RestoreObserverWrapper(this.mContext, paramRestoreObserver);
    try
    {
      int i = this.mBinder.restoreSome(paramLong, this.mObserver, paramArrayOfString);
      return i;
    }
    catch (RemoteException paramRestoreObserver)
    {
      Log.d("RestoreSession", "Can't contact server to restore packages");
    }
    return -1;
  }
  
  private class RestoreObserverWrapper
    extends IRestoreObserver.Stub
  {
    static final int MSG_RESTORE_FINISHED = 3;
    static final int MSG_RESTORE_SETS_AVAILABLE = 4;
    static final int MSG_RESTORE_STARTING = 1;
    static final int MSG_UPDATE = 2;
    final RestoreObserver mAppObserver;
    final Handler mHandler;
    
    RestoreObserverWrapper(Context paramContext, RestoreObserver paramRestoreObserver)
    {
      this.mHandler = new Handler(paramContext.getMainLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          default: 
            return;
          case 1: 
            RestoreSession.RestoreObserverWrapper.this.mAppObserver.restoreStarting(paramAnonymousMessage.arg1);
            return;
          case 2: 
            RestoreSession.RestoreObserverWrapper.this.mAppObserver.onUpdate(paramAnonymousMessage.arg1, (String)paramAnonymousMessage.obj);
            return;
          case 3: 
            RestoreSession.RestoreObserverWrapper.this.mAppObserver.restoreFinished(paramAnonymousMessage.arg1);
            return;
          }
          RestoreSession.RestoreObserverWrapper.this.mAppObserver.restoreSetsAvailable((RestoreSet[])paramAnonymousMessage.obj);
        }
      };
      this.mAppObserver = paramRestoreObserver;
    }
    
    public void onUpdate(int paramInt, String paramString)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(2, paramInt, 0, paramString));
    }
    
    public void restoreFinished(int paramInt)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(3, paramInt, 0));
    }
    
    public void restoreSetsAvailable(RestoreSet[] paramArrayOfRestoreSet)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(4, paramArrayOfRestoreSet));
    }
    
    public void restoreStarting(int paramInt)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramInt, 0));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/RestoreSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */