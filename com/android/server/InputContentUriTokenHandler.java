package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.IInputContentUriToken.Stub;

final class InputContentUriTokenHandler
  extends IInputContentUriToken.Stub
{
  private final Object mLock = new Object();
  @GuardedBy("mLock")
  private IBinder mPermissionOwnerToken = null;
  private final int mSourceUid;
  private final int mSourceUserId;
  private final String mTargetPackage;
  private final int mTargetUserId;
  private final Uri mUri;
  
  InputContentUriTokenHandler(Uri paramUri, int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    this.mUri = paramUri;
    this.mSourceUid = paramInt1;
    this.mTargetPackage = paramString;
    this.mSourceUserId = paramInt2;
    this.mTargetUserId = paramInt3;
  }
  
  private void doTakeLocked(IBinder paramIBinder)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      ActivityManagerNative.getDefault().grantUriPermissionFromOwner(paramIBinder, this.mSourceUid, this.mTargetPackage, this.mUri, 1, this.mSourceUserId, this.mTargetUserId);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      for (;;)
      {
        paramIBinder.rethrowFromSystemServer();
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      release();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void release()
  {
    synchronized (this.mLock)
    {
      IBinder localIBinder = this.mPermissionOwnerToken;
      if (localIBinder == null) {
        return;
      }
      try
      {
        ActivityManagerNative.getDefault().revokeUriPermissionFromOwner(this.mPermissionOwnerToken, this.mUri, 1, this.mSourceUserId);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          localRemoteException.rethrowFromSystemServer();
          this.mPermissionOwnerToken = null;
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      finally
      {
        this.mPermissionOwnerToken = null;
      }
      return;
    }
  }
  
  public void take()
  {
    synchronized (this.mLock)
    {
      IBinder localIBinder = this.mPermissionOwnerToken;
      if (localIBinder != null) {
        return;
      }
      try
      {
        this.mPermissionOwnerToken = ActivityManagerNative.getDefault().newUriPermissionOwner("InputContentUriTokenHandler");
        doTakeLocked(this.mPermissionOwnerToken);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          localRemoteException.rethrowFromSystemServer();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/InputContentUriTokenHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */