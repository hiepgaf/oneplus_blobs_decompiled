package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IUpdateLock.Stub;
import android.os.RemoteException;
import android.os.TokenWatcher;
import android.os.UserHandle;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class UpdateLockService
  extends IUpdateLock.Stub
{
  static final boolean DEBUG = false;
  static final String PERMISSION = "android.permission.UPDATE_LOCK";
  static final String TAG = "UpdateLockService";
  Context mContext;
  LockWatcher mLocks;
  
  UpdateLockService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLocks = new LockWatcher(new Handler(), "UpdateLocks");
    sendLockChangedBroadcast(true);
  }
  
  private String makeTag(String paramString)
  {
    return "{tag=" + paramString + " uid=" + Binder.getCallingUid() + " pid=" + Binder.getCallingPid() + '}';
  }
  
  public void acquireUpdateLock(IBinder paramIBinder, String paramString)
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "acquireUpdateLock");
    this.mLocks.acquire(paramIBinder, makeTag(paramString));
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump update lock service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    this.mLocks.dump(paramPrintWriter);
  }
  
  public void releaseUpdateLock(IBinder paramIBinder)
    throws RemoteException
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "releaseUpdateLock");
    this.mLocks.release(paramIBinder);
  }
  
  void sendLockChangedBroadcast(boolean paramBoolean)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      Intent localIntent = new Intent("android.os.UpdateLock.UPDATE_LOCK_CHANGED").putExtra("nowisconvenient", paramBoolean).putExtra("timestamp", System.currentTimeMillis()).addFlags(67108864);
      this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  class LockWatcher
    extends TokenWatcher
  {
    LockWatcher(Handler paramHandler, String paramString)
    {
      super(paramString);
    }
    
    public void acquired()
    {
      UpdateLockService.this.sendLockChangedBroadcast(false);
    }
    
    public void released()
    {
      UpdateLockService.this.sendLockChangedBroadcast(true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/UpdateLockService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */