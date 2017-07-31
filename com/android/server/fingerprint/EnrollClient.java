package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import java.util.Arrays;

public abstract class EnrollClient
  extends ClientMonitor
{
  private static final int ENROLLMENT_TIMEOUT_MS = 60000;
  private static final long MS_PER_SEC = 1000L;
  private byte[] mCryptoToken;
  
  public EnrollClient(Context paramContext, long paramLong, IBinder paramIBinder, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt1, int paramInt2, byte[] paramArrayOfByte, boolean paramBoolean, String paramString)
  {
    super(paramContext, paramLong, paramIBinder, paramIFingerprintServiceReceiver, paramInt1, paramInt2, paramBoolean, paramString);
    this.mCryptoToken = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  private boolean sendEnrollResult(int paramInt1, int paramInt2, int paramInt3)
  {
    IFingerprintServiceReceiver localIFingerprintServiceReceiver = getReceiver();
    if (localIFingerprintServiceReceiver == null) {
      return true;
    }
    FingerprintUtils.vibrateFingerprintSuccess(getContext());
    MetricsLogger.action(getContext(), 251);
    try
    {
      localIFingerprintServiceReceiver.onEnrollResult(getHalDeviceId(), paramInt1, paramInt2, paramInt3);
      return paramInt3 == 0;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("FingerprintService", "Failed to notify EnrollResult:", localRemoteException);
    }
    return true;
  }
  
  public boolean onAuthenticated(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onAuthenticated() called for enroll!");
    return true;
  }
  
  public boolean onEnrollResult(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 != getGroupId()) {
      Slog.w("FingerprintService", "groupId != getGroupId(), groupId: " + paramInt2 + " getGroupId():" + getGroupId());
    }
    if (paramInt3 == 0) {
      FingerprintUtils.getInstance().addFingerprintForUser(getContext(), paramInt1, getTargetUserId());
    }
    return sendEnrollResult(paramInt1, paramInt2, paramInt3);
  }
  
  public boolean onEnumerationResult(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onEnumerationResult() called for enroll!");
    return true;
  }
  
  public boolean onRemoved(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onRemoved() called for enroll!");
    return true;
  }
  
  public int start()
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon == null)
    {
      Slog.w("FingerprintService", "enroll: no fingeprintd!");
      return 3;
    }
    try
    {
      int i = localIFingerprintDaemon.enroll(this.mCryptoToken, getGroupId(), 60);
      if (i != 0)
      {
        Slog.w("FingerprintService", "startEnroll failed, result=" + i);
        MetricsLogger.histogram(getContext(), "fingerprintd_enroll_start_error", i);
        onError(1);
        return i;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "startEnroll failed", localRemoteException);
    }
    return 0;
  }
  
  public int stop(boolean paramBoolean)
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon == null)
    {
      Slog.w("FingerprintService", "stopEnrollment: no fingeprintd!");
      return 3;
    }
    try
    {
      int i = localIFingerprintDaemon.cancelEnrollment();
      if (i != 0)
      {
        Slog.w("FingerprintService", "startEnrollCancel failed, result = " + i);
        return i;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "stopEnrollment failed", localRemoteException);
      if (paramBoolean) {
        onError(5);
      }
    }
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/EnrollClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */