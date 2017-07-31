package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Slog;
import java.util.NoSuchElementException;

public abstract class ClientMonitor
  implements IBinder.DeathRecipient
{
  protected static final boolean DEBUG = true;
  protected static final int ERROR_ESRCH = 3;
  protected static final String TAG = "FingerprintService";
  private Context mContext;
  private int mGroupId;
  private long mHalDeviceId;
  private boolean mIsRestricted;
  private String mOwner;
  private IFingerprintServiceReceiver mReceiver;
  private int mTargetUserId;
  private IBinder mToken;
  
  public ClientMonitor(Context paramContext, long paramLong, IBinder paramIBinder, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
  {
    this.mContext = paramContext;
    this.mHalDeviceId = paramLong;
    this.mToken = paramIBinder;
    this.mReceiver = paramIFingerprintServiceReceiver;
    this.mTargetUserId = paramInt1;
    this.mGroupId = paramInt2;
    this.mIsRestricted = paramBoolean;
    this.mOwner = paramString;
    try
    {
      paramIBinder.linkToDeath(this, 0);
      return;
    }
    catch (RemoteException paramContext)
    {
      Slog.w("FingerprintService", "caught remote exception in linkToDeath: ", paramContext);
    }
  }
  
  public void binderDied()
  {
    this.mToken = null;
    this.mReceiver = null;
    onError(1);
  }
  
  public void destroy()
  {
    if (this.mToken != null) {}
    try
    {
      this.mToken.unlinkToDeath(this, 0);
      this.mToken = null;
      this.mReceiver = null;
      return;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      for (;;)
      {
        Slog.e("FingerprintService", "destroy(): " + this + ":", new Exception("here"));
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mToken != null)
      {
        Slog.w("FingerprintService", "removing leaked reference: " + this.mToken);
        onError(1);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public abstract IFingerprintDaemon getFingerprintDaemon();
  
  public final int getGroupId()
  {
    return this.mGroupId;
  }
  
  public final long getHalDeviceId()
  {
    return this.mHalDeviceId;
  }
  
  public final boolean getIsRestricted()
  {
    return this.mIsRestricted;
  }
  
  public final String getOwnerString()
  {
    return this.mOwner;
  }
  
  public final IFingerprintServiceReceiver getReceiver()
  {
    return this.mReceiver;
  }
  
  public final int getTargetUserId()
  {
    return this.mTargetUserId;
  }
  
  public final IBinder getToken()
  {
    return this.mToken;
  }
  
  public abstract void notifyUserActivity();
  
  public boolean onAcquired(int paramInt)
  {
    if (this.mReceiver == null) {
      return true;
    }
    try
    {
      this.mReceiver.onAcquired(getHalDeviceId(), paramInt);
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("FingerprintService", "Failed to invoke sendAcquired:", localRemoteException);
      return true;
    }
    finally
    {
      if (paramInt == 0) {
        notifyUserActivity();
      }
    }
  }
  
  public abstract boolean onAuthenticated(int paramInt1, int paramInt2);
  
  public abstract boolean onEnrollResult(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean onEnumerationResult(int paramInt1, int paramInt2);
  
  public boolean onError(int paramInt)
  {
    if (this.mReceiver != null) {}
    try
    {
      this.mReceiver.onError(getHalDeviceId(), paramInt);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("FingerprintService", "Failed to invoke sendError:", localRemoteException);
      }
    }
  }
  
  public abstract boolean onRemoved(int paramInt1, int paramInt2);
  
  public abstract int start();
  
  public abstract int stop(boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/ClientMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */