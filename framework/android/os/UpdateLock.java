package android.os;

import android.util.Log;

public class UpdateLock
{
  private static final boolean DEBUG = false;
  public static final String NOW_IS_CONVENIENT = "nowisconvenient";
  private static final String TAG = "UpdateLock";
  public static final String TIMESTAMP = "timestamp";
  public static final String UPDATE_LOCK_CHANGED = "android.os.UpdateLock.UPDATE_LOCK_CHANGED";
  private static IUpdateLock sService;
  int mCount = 0;
  boolean mHeld = false;
  boolean mRefCounted = true;
  final String mTag;
  IBinder mToken;
  
  public UpdateLock(String paramString)
  {
    this.mTag = paramString;
    this.mToken = new Binder();
  }
  
  private void acquireLocked()
  {
    if (this.mRefCounted)
    {
      int i = this.mCount;
      this.mCount = (i + 1);
      if (i != 0) {}
    }
    else if (sService == null) {}
    try
    {
      sService.acquireUpdateLock(this.mToken, this.mTag);
      this.mHeld = true;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("UpdateLock", "Unable to contact service to acquire");
      }
    }
  }
  
  private static void checkService()
  {
    if (sService == null) {
      sService = IUpdateLock.Stub.asInterface(ServiceManager.getService("updatelock"));
    }
  }
  
  private void releaseLocked()
  {
    if (this.mRefCounted)
    {
      int i = this.mCount - 1;
      this.mCount = i;
      if (i != 0) {}
    }
    else if (sService == null) {}
    try
    {
      sService.releaseUpdateLock(this.mToken);
      this.mHeld = false;
      if (this.mCount < 0) {
        throw new RuntimeException("UpdateLock under-locked");
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("UpdateLock", "Unable to contact service to release");
      }
    }
  }
  
  public void acquire()
  {
    
    synchronized (this.mToken)
    {
      acquireLocked();
      return;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    synchronized (this.mToken)
    {
      if (this.mHeld) {
        Log.wtf("UpdateLock", "UpdateLock finalized while still held");
      }
      try
      {
        sService.releaseUpdateLock(this.mToken);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("UpdateLock", "Unable to contact service to release");
        }
      }
    }
  }
  
  public boolean isHeld()
  {
    synchronized (this.mToken)
    {
      boolean bool = this.mHeld;
      return bool;
    }
  }
  
  public void release()
  {
    
    synchronized (this.mToken)
    {
      releaseLocked();
      return;
    }
  }
  
  public void setReferenceCounted(boolean paramBoolean)
  {
    this.mRefCounted = paramBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UpdateLock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */