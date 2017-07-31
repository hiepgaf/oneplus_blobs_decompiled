package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;

public abstract class EnumerateClient
  extends ClientMonitor
{
  public EnumerateClient(Context paramContext, long paramLong, IBinder paramIBinder, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
  {
    super(paramContext, paramLong, paramIBinder, paramIFingerprintServiceReceiver, paramInt1, paramInt2, paramBoolean, paramString);
  }
  
  public boolean onEnumerationResult(int paramInt1, int paramInt2)
  {
    IFingerprintServiceReceiver localIFingerprintServiceReceiver = getReceiver();
    if (localIFingerprintServiceReceiver == null) {
      return true;
    }
    try
    {
      localIFingerprintServiceReceiver.onRemoved(getHalDeviceId(), paramInt1, paramInt2);
      if (paramInt1 == 0) {
        return true;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("FingerprintService", "Failed to notify enumerated:", localRemoteException);
      }
    }
    return false;
  }
  
  public int start()
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    try
    {
      int i = localIFingerprintDaemon.enumerate();
      if (i != 0)
      {
        Slog.w("FingerprintService", "start enumerate for user " + getTargetUserId() + " failed, result=" + i);
        MetricsLogger.histogram(getContext(), "fingerprintd_enum_start_error", i);
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
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon == null)
    {
      Slog.w("FingerprintService", "stopAuthentication: no fingeprintd!");
      return 3;
    }
    try
    {
      int i = localIFingerprintDaemon.cancelEnumeration();
      if (i != 0)
      {
        Slog.w("FingerprintService", "stop enumeration failed, result=" + i);
        return i;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "stop enumeration failed", localRemoteException);
      return 3;
    }
    if (paramBoolean) {
      onError(5);
    }
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/EnumerateClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */