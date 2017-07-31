package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;

public abstract class RemovalClient
  extends ClientMonitor
{
  private int mFingerId;
  
  public RemovalClient(Context paramContext, long paramLong, IBinder paramIBinder, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString)
  {
    super(paramContext, paramLong, paramIBinder, paramIFingerprintServiceReceiver, paramInt3, paramInt2, paramBoolean, paramString);
    this.mFingerId = paramInt1;
  }
  
  private boolean sendRemoved(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    IFingerprintServiceReceiver localIFingerprintServiceReceiver = getReceiver();
    if (localIFingerprintServiceReceiver != null) {}
    try
    {
      localIFingerprintServiceReceiver.onRemoved(getHalDeviceId(), paramInt1, paramInt2);
      if (paramInt1 == 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("FingerprintService", "Failed to notify Removed:", localRemoteException);
      }
    }
  }
  
  public boolean onAuthenticated(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onAuthenticated() called for remove!");
    return true;
  }
  
  public boolean onEnrollResult(int paramInt1, int paramInt2, int paramInt3)
  {
    Slog.w("FingerprintService", "onEnrollResult() called for remove!");
    return true;
  }
  
  public boolean onEnumerationResult(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onEnumerationResult() called for remove!");
    return false;
  }
  
  public boolean onRemoved(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 0) {
      FingerprintUtils.getInstance().removeFingerprintIdForUser(getContext(), paramInt1, getTargetUserId());
    }
    return sendRemoved(paramInt1, getGroupId());
  }
  
  public int start()
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    try
    {
      int i = localIFingerprintDaemon.remove(this.mFingerId, getGroupId());
      if (i != 0)
      {
        Slog.w("FingerprintService", "startRemove with id = " + this.mFingerId + " failed, result=" + i);
        MetricsLogger.histogram(getContext(), "fingerprintd_remove_start_error", i);
        onError(1);
        return i;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "startRemove failed", localRemoteException);
    }
    return 0;
  }
  
  public int stop(boolean paramBoolean)
  {
    if (paramBoolean) {
      onError(5);
    }
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/RemovalClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */