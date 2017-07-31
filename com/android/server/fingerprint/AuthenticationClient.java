package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;

public abstract class AuthenticationClient
  extends ClientMonitor
{
  private long mOpId;
  
  public AuthenticationClient(Context paramContext, long paramLong1, IBinder paramIBinder, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt1, int paramInt2, long paramLong2, boolean paramBoolean, String paramString)
  {
    super(paramContext, paramLong1, paramIBinder, paramIFingerprintServiceReceiver, paramInt1, paramInt2, paramBoolean, paramString);
    this.mOpId = paramLong2;
  }
  
  public abstract boolean handleFailedAttempt();
  
  /* Error */
  public boolean onAuthenticated(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: iload_1
    //   3: ifeq +101 -> 104
    //   6: iconst_1
    //   7: istore 4
    //   9: aload_0
    //   10: invokevirtual 24	com/android/server/fingerprint/AuthenticationClient:getReceiver	()Landroid/hardware/fingerprint/IFingerprintServiceReceiver;
    //   13: astore 6
    //   15: aload 6
    //   17: ifnull +213 -> 230
    //   20: aload_0
    //   21: invokevirtual 28	com/android/server/fingerprint/AuthenticationClient:getContext	()Landroid/content/Context;
    //   24: sipush 252
    //   27: iload 4
    //   29: invokestatic 34	com/android/internal/logging/MetricsLogger:action	(Landroid/content/Context;IZ)V
    //   32: iload 4
    //   34: ifne +76 -> 110
    //   37: aload 6
    //   39: aload_0
    //   40: invokevirtual 38	com/android/server/fingerprint/AuthenticationClient:getHalDeviceId	()J
    //   43: invokeinterface 44 3 0
    //   48: iload_3
    //   49: istore_1
    //   50: iload 4
    //   52: ifne +198 -> 250
    //   55: aload 6
    //   57: ifnull +10 -> 67
    //   60: aload_0
    //   61: invokevirtual 28	com/android/server/fingerprint/AuthenticationClient:getContext	()Landroid/content/Context;
    //   64: invokestatic 50	com/android/server/fingerprint/FingerprintUtils:vibrateFingerprintError	(Landroid/content/Context;)V
    //   67: aload_0
    //   68: invokevirtual 52	com/android/server/fingerprint/AuthenticationClient:handleFailedAttempt	()Z
    //   71: istore 4
    //   73: iload 4
    //   75: ifeq +24 -> 99
    //   78: ldc 54
    //   80: ldc 56
    //   82: invokestatic 62	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   85: pop
    //   86: aload 6
    //   88: aload_0
    //   89: invokevirtual 38	com/android/server/fingerprint/AuthenticationClient:getHalDeviceId	()J
    //   92: bipush 7
    //   94: invokeinterface 66 4 0
    //   99: iload_1
    //   100: iload 4
    //   102: ior
    //   103: ireturn
    //   104: iconst_0
    //   105: istore 4
    //   107: goto -98 -> 9
    //   110: ldc 54
    //   112: new 68	java/lang/StringBuilder
    //   115: dup
    //   116: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   119: ldc 73
    //   121: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_0
    //   125: invokevirtual 81	com/android/server/fingerprint/AuthenticationClient:getOwnerString	()Ljava/lang/String;
    //   128: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: ldc 83
    //   133: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: iload_1
    //   137: invokevirtual 86	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   140: ldc 88
    //   142: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: iload_2
    //   146: invokevirtual 86	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   149: ldc 90
    //   151: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: invokevirtual 93	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   157: invokestatic 96	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   160: pop
    //   161: aload_0
    //   162: invokevirtual 99	com/android/server/fingerprint/AuthenticationClient:getIsRestricted	()Z
    //   165: ifne +59 -> 224
    //   168: new 101	android/hardware/fingerprint/Fingerprint
    //   171: dup
    //   172: ldc 103
    //   174: iload_2
    //   175: iload_1
    //   176: aload_0
    //   177: invokevirtual 38	com/android/server/fingerprint/AuthenticationClient:getHalDeviceId	()J
    //   180: invokespecial 106	android/hardware/fingerprint/Fingerprint:<init>	(Ljava/lang/CharSequence;IIJ)V
    //   183: astore 5
    //   185: aload 6
    //   187: aload_0
    //   188: invokevirtual 38	com/android/server/fingerprint/AuthenticationClient:getHalDeviceId	()J
    //   191: aload 5
    //   193: aload_0
    //   194: invokevirtual 110	com/android/server/fingerprint/AuthenticationClient:getTargetUserId	()I
    //   197: invokeinterface 114 5 0
    //   202: iload_3
    //   203: istore_1
    //   204: goto -154 -> 50
    //   207: astore 5
    //   209: ldc 54
    //   211: ldc 116
    //   213: aload 5
    //   215: invokestatic 119	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   218: pop
    //   219: iconst_1
    //   220: istore_1
    //   221: goto -171 -> 50
    //   224: aconst_null
    //   225: astore 5
    //   227: goto -42 -> 185
    //   230: iconst_1
    //   231: istore_1
    //   232: goto -182 -> 50
    //   235: astore 5
    //   237: ldc 54
    //   239: ldc 121
    //   241: aload 5
    //   243: invokestatic 119	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   246: pop
    //   247: goto -148 -> 99
    //   250: aload 6
    //   252: ifnull +10 -> 262
    //   255: aload_0
    //   256: invokevirtual 28	com/android/server/fingerprint/AuthenticationClient:getContext	()Landroid/content/Context;
    //   259: invokestatic 124	com/android/server/fingerprint/FingerprintUtils:vibrateFingerprintSuccess	(Landroid/content/Context;)V
    //   262: aload_0
    //   263: invokevirtual 127	com/android/server/fingerprint/AuthenticationClient:resetFailedAttempts	()V
    //   266: iload_1
    //   267: iconst_1
    //   268: ior
    //   269: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	270	0	this	AuthenticationClient
    //   0	270	1	paramInt1	int
    //   0	270	2	paramInt2	int
    //   1	202	3	i	int
    //   7	99	4	j	int
    //   183	9	5	localFingerprint	android.hardware.fingerprint.Fingerprint
    //   207	7	5	localRemoteException1	RemoteException
    //   225	1	5	localObject	Object
    //   235	7	5	localRemoteException2	RemoteException
    //   13	238	6	localIFingerprintServiceReceiver	IFingerprintServiceReceiver
    // Exception table:
    //   from	to	target	type
    //   20	32	207	android/os/RemoteException
    //   37	48	207	android/os/RemoteException
    //   110	185	207	android/os/RemoteException
    //   185	202	207	android/os/RemoteException
    //   78	99	235	android/os/RemoteException
  }
  
  public boolean onEnrollResult(int paramInt1, int paramInt2, int paramInt3)
  {
    Slog.w("FingerprintService", "onEnrollResult() called for authenticate!");
    return true;
  }
  
  public boolean onEnumerationResult(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onEnumerationResult() called for authenticate!");
    return true;
  }
  
  public boolean onRemoved(int paramInt1, int paramInt2)
  {
    Slog.w("FingerprintService", "onRemoved() called for authenticate!");
    return true;
  }
  
  public abstract void resetFailedAttempts();
  
  public int start()
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon == null)
    {
      Slog.w("FingerprintService", "start authentication: no fingeprintd!");
      return 3;
    }
    try
    {
      int i = localIFingerprintDaemon.authenticate(this.mOpId, getGroupId());
      if (i != 0)
      {
        Slog.w("FingerprintService", "startAuthentication failed, result=" + i);
        MetricsLogger.histogram(getContext(), "fingeprintd_auth_start_error", i);
        onError(1);
        return i;
      }
      Slog.w("FingerprintService", "client " + getOwnerString() + " is authenticating...");
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "startAuthentication failed", localRemoteException);
    }
    return 3;
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
      int i = localIFingerprintDaemon.cancelAuthentication();
      if (i != 0)
      {
        Slog.w("FingerprintService", "stopAuthentication failed, result=" + i);
        return i;
      }
      Slog.w("FingerprintService", "client " + getOwnerString() + " is no longer authenticating");
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("FingerprintService", "stopAuthentication failed", localRemoteException);
    }
    return 3;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/AuthenticationClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */