package android.hardware.fingerprint;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.keystore.AndroidKeyStoreProvider;
import android.util.Log;
import android.util.Slog;
import java.security.Signature;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.Mac;

public class FingerprintManager
{
  private static final boolean DEBUG = true;
  public static final int FINGERPRINT_ACQUIRED_GOOD = 0;
  public static final int FINGERPRINT_ACQUIRED_IMAGER_DIRTY = 3;
  public static final int FINGERPRINT_ACQUIRED_INSUFFICIENT = 2;
  public static final int FINGERPRINT_ACQUIRED_PARTIAL = 1;
  public static final int FINGERPRINT_ACQUIRED_TOO_FAST = 5;
  public static final int FINGERPRINT_ACQUIRED_TOO_SLOW = 4;
  public static final int FINGERPRINT_ACQUIRED_VENDOR_BASE = 1000;
  public static final int FINGERPRINT_ERROR_CANCELED = 5;
  public static final int FINGERPRINT_ERROR_HW_UNAVAILABLE = 1;
  public static final int FINGERPRINT_ERROR_LOCKOUT = 7;
  public static final int FINGERPRINT_ERROR_NO_SPACE = 4;
  public static final int FINGERPRINT_ERROR_TIMEOUT = 3;
  public static final int FINGERPRINT_ERROR_UNABLE_TO_PROCESS = 2;
  public static final int FINGERPRINT_ERROR_UNABLE_TO_REMOVE = 6;
  public static final int FINGERPRINT_ERROR_VENDOR_BASE = 1000;
  private static final int MSG_ACQUIRED = 101;
  private static final int MSG_AUTHENTICATION_FAILED = 103;
  private static final int MSG_AUTHENTICATION_SUCCEEDED = 102;
  private static final int MSG_ENROLL_RESULT = 100;
  private static final int MSG_ERROR = 104;
  private static final int MSG_REMOVED = 105;
  private static final String TAG = "FingerprintManager";
  private AuthenticationCallback mAuthenticationCallback;
  private Context mContext;
  private CryptoObject mCryptoObject;
  private EnrollmentCallback mEnrollmentCallback;
  private Handler mHandler;
  private RemovalCallback mRemovalCallback;
  private Fingerprint mRemovalFingerprint;
  private IFingerprintService mService;
  private IFingerprintServiceReceiver mServiceReceiver = new IFingerprintServiceReceiver.Stub()
  {
    public void onAcquired(long paramAnonymousLong, int paramAnonymousInt)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(101, paramAnonymousInt, 0, Long.valueOf(paramAnonymousLong)).sendToTarget();
    }
    
    public void onAuthenticationFailed(long paramAnonymousLong)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(103).sendToTarget();
    }
    
    public void onAuthenticationSucceeded(long paramAnonymousLong, Fingerprint paramAnonymousFingerprint, int paramAnonymousInt)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(102, paramAnonymousInt, 0, paramAnonymousFingerprint).sendToTarget();
    }
    
    public void onEnrollResult(long paramAnonymousLong, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(100, paramAnonymousInt3, 0, new Fingerprint(null, paramAnonymousInt2, paramAnonymousInt1, paramAnonymousLong)).sendToTarget();
    }
    
    public void onError(long paramAnonymousLong, int paramAnonymousInt)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(104, paramAnonymousInt, 0, Long.valueOf(paramAnonymousLong)).sendToTarget();
    }
    
    public void onRemoved(long paramAnonymousLong, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      FingerprintManager.-get3(FingerprintManager.this).obtainMessage(105, paramAnonymousInt1, paramAnonymousInt2, Long.valueOf(paramAnonymousLong)).sendToTarget();
    }
  };
  private IBinder mToken = new Binder();
  
  public FingerprintManager(Context paramContext, IFingerprintService paramIFingerprintService)
  {
    this.mContext = paramContext;
    this.mService = paramIFingerprintService;
    if (this.mService == null) {
      Slog.v("FingerprintManager", "FingerprintManagerService was null");
    }
    this.mHandler = new MyHandler(paramContext, null);
  }
  
  private void cancelAuthentication(CryptoObject paramCryptoObject)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.cancelAuthentication(this.mToken, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramCryptoObject)
    {
      throw paramCryptoObject.rethrowFromSystemServer();
    }
  }
  
  private void cancelEnrollment()
  {
    if (this.mService != null) {}
    try
    {
      this.mService.cancelEnrollment(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private String getAcquiredString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      if (paramInt >= 1000)
      {
        paramInt -= 1000;
        String[] arrayOfString = this.mContext.getResources().getStringArray(17236078);
        if (paramInt < arrayOfString.length) {
          return arrayOfString[paramInt];
        }
      }
      break;
    case 0: 
      return null;
    case 1: 
      return this.mContext.getString(17039861);
    case 2: 
      return this.mContext.getString(17039862);
    case 3: 
      return this.mContext.getString(17039863);
    case 4: 
      return this.mContext.getString(17039865);
    case 5: 
      return this.mContext.getString(17039864);
    }
    return null;
  }
  
  private int getCurrentUserId()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getCurrentUser().id;
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private String getErrorString(int paramInt)
  {
    switch (paramInt)
    {
    case 6: 
    default: 
      if (paramInt >= 1000)
      {
        paramInt -= 1000;
        String[] arrayOfString = this.mContext.getResources().getStringArray(17236079);
        if (paramInt < arrayOfString.length) {
          return arrayOfString[paramInt];
        }
      }
      break;
    case 2: 
      return this.mContext.getString(17039871);
    case 1: 
      return this.mContext.getString(17039866);
    case 4: 
      return this.mContext.getString(17039867);
    case 3: 
      return this.mContext.getString(17039868);
    case 5: 
      return this.mContext.getString(17039869);
    case 7: 
      return this.mContext.getString(17039870);
    }
    return null;
  }
  
  private void useHandler(Handler paramHandler)
  {
    if (paramHandler != null) {
      this.mHandler = new MyHandler(paramHandler.getLooper(), null);
    }
    while (this.mHandler.getLooper() == this.mContext.getMainLooper()) {
      return;
    }
    this.mHandler = new MyHandler(this.mContext.getMainLooper(), null);
  }
  
  public void addLockoutResetCallback(final LockoutResetCallback paramLockoutResetCallback)
  {
    if (this.mService != null) {
      try
      {
        final PowerManager localPowerManager = (PowerManager)this.mContext.getSystemService(PowerManager.class);
        this.mService.addLockoutResetCallback(new IFingerprintServiceLockoutResetCallback.Stub()
        {
          public void onLockoutReset(long paramAnonymousLong)
            throws RemoteException
          {
            final PowerManager.WakeLock localWakeLock = localPowerManager.newWakeLock(1, "lockoutResetCallback");
            localWakeLock.acquire();
            FingerprintManager.-get3(FingerprintManager.this).post(new Runnable()
            {
              public void run()
              {
                try
                {
                  this.val$callback.onLockoutReset();
                  return;
                }
                finally
                {
                  localWakeLock.release();
                }
              }
            });
          }
        });
        return;
      }
      catch (RemoteException paramLockoutResetCallback)
      {
        throw paramLockoutResetCallback.rethrowFromSystemServer();
      }
    }
    Log.w("FingerprintManager", "addLockoutResetCallback(): Service not connected!");
  }
  
  public void authenticate(CryptoObject paramCryptoObject, CancellationSignal paramCancellationSignal, int paramInt, AuthenticationCallback paramAuthenticationCallback, Handler paramHandler)
  {
    authenticate(paramCryptoObject, paramCancellationSignal, paramInt, paramAuthenticationCallback, paramHandler, UserHandle.myUserId());
  }
  
  public void authenticate(CryptoObject paramCryptoObject, CancellationSignal paramCancellationSignal, int paramInt1, AuthenticationCallback paramAuthenticationCallback, Handler paramHandler, int paramInt2)
  {
    if (paramAuthenticationCallback == null) {
      throw new IllegalArgumentException("Must supply an authentication callback");
    }
    if (paramCancellationSignal != null)
    {
      if (paramCancellationSignal.isCanceled())
      {
        Log.w("FingerprintManager", "authentication already canceled");
        return;
      }
      paramCancellationSignal.setOnCancelListener(new OnAuthenticationCancelListener(paramCryptoObject));
    }
    if (this.mService != null) {}
    for (;;)
    {
      try
      {
        useHandler(paramHandler);
        this.mAuthenticationCallback = paramAuthenticationCallback;
        this.mCryptoObject = paramCryptoObject;
        if (paramCryptoObject != null)
        {
          l = paramCryptoObject.getOpId();
          this.mHandler.removeMessages(104);
          this.mService.authenticate(this.mToken, l, paramInt2, this.mServiceReceiver, paramInt1, this.mContext.getOpPackageName());
          return;
        }
      }
      catch (RemoteException paramCryptoObject)
      {
        long l;
        Log.w("FingerprintManager", "Remote exception while authenticating: ", paramCryptoObject);
        if (paramAuthenticationCallback == null) {
          continue;
        }
        paramAuthenticationCallback.onAuthenticationError(1, getErrorString(1));
      }
      l = 0L;
    }
  }
  
  public void enroll(byte[] paramArrayOfByte, CancellationSignal paramCancellationSignal, int paramInt1, int paramInt2, EnrollmentCallback paramEnrollmentCallback)
  {
    int i = paramInt2;
    if (paramInt2 == -2) {
      i = getCurrentUserId();
    }
    if (paramEnrollmentCallback == null) {
      throw new IllegalArgumentException("Must supply an enrollment callback");
    }
    if (paramCancellationSignal != null)
    {
      if (paramCancellationSignal.isCanceled())
      {
        Log.w("FingerprintManager", "enrollment already canceled");
        return;
      }
      paramCancellationSignal.setOnCancelListener(new OnEnrollCancelListener(null));
    }
    if (this.mService != null) {}
    try
    {
      this.mEnrollmentCallback = paramEnrollmentCallback;
      this.mService.enroll(this.mToken, paramArrayOfByte, i, this.mServiceReceiver, paramInt1, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramArrayOfByte)
    {
      do
      {
        Log.w("FingerprintManager", "Remote exception in enroll: ", paramArrayOfByte);
      } while (paramEnrollmentCallback == null);
      paramEnrollmentCallback.onEnrollmentError(1, getErrorString(1));
    }
  }
  
  public long getAuthenticatorId()
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getAuthenticatorId(this.mContext.getOpPackageName());
        return l;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    Log.w("FingerprintManager", "getAuthenticatorId(): Service not connected!");
    return 0L;
  }
  
  public List<Fingerprint> getEnrolledFingerprints()
  {
    return getEnrolledFingerprints(UserHandle.myUserId());
  }
  
  public List<Fingerprint> getEnrolledFingerprints(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        List localList = this.mService.getEnrolledFingerprints(paramInt, this.mContext.getOpPackageName());
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public boolean hasEnrolledFingerprints()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.hasEnrolledFingerprints(UserHandle.myUserId(), this.mContext.getOpPackageName());
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean hasEnrolledFingerprints(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.hasEnrolledFingerprints(paramInt, this.mContext.getOpPackageName());
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isHardwareDetected()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isHardwareDetected(0L, this.mContext.getOpPackageName());
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    Log.w("FingerprintManager", "isFingerprintHardwareDetected(): Service not connected!");
    return false;
  }
  
  public int postEnroll()
  {
    int i = 0;
    if (this.mService != null) {}
    try
    {
      i = this.mService.postEnroll(this.mToken);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public long preEnroll()
  {
    long l = 0L;
    if (this.mService != null) {}
    try
    {
      l = this.mService.preEnroll(this.mToken);
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void remove(Fingerprint paramFingerprint, int paramInt, RemovalCallback paramRemovalCallback)
  {
    if (this.mService != null) {}
    try
    {
      this.mRemovalCallback = paramRemovalCallback;
      this.mRemovalFingerprint = paramFingerprint;
      this.mService.remove(this.mToken, paramFingerprint.getFingerId(), paramFingerprint.getGroupId(), paramInt, this.mServiceReceiver);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      do
      {
        Log.w("FingerprintManager", "Remote exception in remove: ", localRemoteException);
      } while (paramRemovalCallback == null);
      paramRemovalCallback.onRemovalError(paramFingerprint, 1, getErrorString(1));
    }
  }
  
  public void rename(int paramInt1, int paramInt2, String paramString)
  {
    if (this.mService != null) {
      try
      {
        this.mService.rename(paramInt1, paramInt2, paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    Log.w("FingerprintManager", "rename(): Service not connected!");
  }
  
  public void resetTimeout(byte[] paramArrayOfByte)
  {
    if (this.mService != null) {
      try
      {
        this.mService.resetTimeout(paramArrayOfByte);
        return;
      }
      catch (RemoteException paramArrayOfByte)
      {
        throw paramArrayOfByte.rethrowFromSystemServer();
      }
    }
    Log.w("FingerprintManager", "resetTimeout(): Service not connected!");
  }
  
  public void setActiveUser(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setActiveUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int updateStatus(int paramInt)
  {
    int i = 0;
    if (this.mService != null) {}
    try
    {
      i = this.mService.updateStatus(paramInt);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("FingerprintManager", "Remote exception in updateStatus", localRemoteException);
    }
    return 0;
  }
  
  public static abstract class AuthenticationCallback
  {
    public void onAuthenticationAcquired(int paramInt) {}
    
    public void onAuthenticationError(int paramInt, CharSequence paramCharSequence) {}
    
    public void onAuthenticationFailed() {}
    
    public void onAuthenticationHelp(int paramInt, CharSequence paramCharSequence) {}
    
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult paramAuthenticationResult) {}
  }
  
  public static class AuthenticationResult
  {
    private FingerprintManager.CryptoObject mCryptoObject;
    private Fingerprint mFingerprint;
    private int mUserId;
    
    public AuthenticationResult(FingerprintManager.CryptoObject paramCryptoObject, Fingerprint paramFingerprint, int paramInt)
    {
      this.mCryptoObject = paramCryptoObject;
      this.mFingerprint = paramFingerprint;
      this.mUserId = paramInt;
    }
    
    public FingerprintManager.CryptoObject getCryptoObject()
    {
      return this.mCryptoObject;
    }
    
    public Fingerprint getFingerprint()
    {
      return this.mFingerprint;
    }
    
    public int getUserId()
    {
      return this.mUserId;
    }
  }
  
  public static final class CryptoObject
  {
    private final Object mCrypto;
    
    public CryptoObject(Signature paramSignature)
    {
      this.mCrypto = paramSignature;
    }
    
    public CryptoObject(Cipher paramCipher)
    {
      this.mCrypto = paramCipher;
    }
    
    public CryptoObject(Mac paramMac)
    {
      this.mCrypto = paramMac;
    }
    
    public Cipher getCipher()
    {
      if ((this.mCrypto instanceof Cipher)) {
        return (Cipher)this.mCrypto;
      }
      return null;
    }
    
    public Mac getMac()
    {
      if ((this.mCrypto instanceof Mac)) {
        return (Mac)this.mCrypto;
      }
      return null;
    }
    
    public long getOpId()
    {
      if (this.mCrypto != null) {
        return AndroidKeyStoreProvider.getKeyStoreOperationHandle(this.mCrypto);
      }
      return 0L;
    }
    
    public Signature getSignature()
    {
      if ((this.mCrypto instanceof Signature)) {
        return (Signature)this.mCrypto;
      }
      return null;
    }
  }
  
  public static abstract class EnrollmentCallback
  {
    public void onEnrollmentError(int paramInt, CharSequence paramCharSequence) {}
    
    public void onEnrollmentHelp(int paramInt, CharSequence paramCharSequence) {}
    
    public void onEnrollmentProgress(int paramInt) {}
  }
  
  public static abstract class LockoutResetCallback
  {
    public void onLockoutReset() {}
  }
  
  private class MyHandler
    extends Handler
  {
    private MyHandler(Context paramContext)
    {
      super();
    }
    
    private MyHandler(Looper paramLooper)
    {
      super();
    }
    
    private void sendAcquiredResult(long paramLong, int paramInt)
    {
      if (FingerprintManager.-get0(FingerprintManager.this) != null) {
        FingerprintManager.-get0(FingerprintManager.this).onAuthenticationAcquired(paramInt);
      }
      String str = FingerprintManager.-wrap0(FingerprintManager.this, paramInt);
      if (str == null) {
        return;
      }
      if (FingerprintManager.-get2(FingerprintManager.this) != null) {
        FingerprintManager.-get2(FingerprintManager.this).onEnrollmentHelp(paramInt, str);
      }
      while (FingerprintManager.-get0(FingerprintManager.this) == null) {
        return;
      }
      FingerprintManager.-get0(FingerprintManager.this).onAuthenticationHelp(paramInt, str);
    }
    
    private void sendAuthenticatedFailed()
    {
      if (FingerprintManager.-get0(FingerprintManager.this) != null) {
        FingerprintManager.-get0(FingerprintManager.this).onAuthenticationFailed();
      }
    }
    
    private void sendAuthenticatedSucceeded(Fingerprint paramFingerprint, int paramInt)
    {
      if (FingerprintManager.-get0(FingerprintManager.this) != null)
      {
        paramFingerprint = new FingerprintManager.AuthenticationResult(FingerprintManager.-get1(FingerprintManager.this), paramFingerprint, paramInt);
        FingerprintManager.-get0(FingerprintManager.this).onAuthenticationSucceeded(paramFingerprint);
      }
    }
    
    private void sendEnrollResult(Fingerprint paramFingerprint, int paramInt)
    {
      if (FingerprintManager.-get2(FingerprintManager.this) != null) {
        FingerprintManager.-get2(FingerprintManager.this).onEnrollmentProgress(paramInt);
      }
    }
    
    private void sendErrorResult(long paramLong, int paramInt)
    {
      if (FingerprintManager.-get2(FingerprintManager.this) != null) {
        FingerprintManager.-get2(FingerprintManager.this).onEnrollmentError(paramInt, FingerprintManager.-wrap1(FingerprintManager.this, paramInt));
      }
      do
      {
        return;
        if (FingerprintManager.-get0(FingerprintManager.this) != null)
        {
          FingerprintManager.-get0(FingerprintManager.this).onAuthenticationError(paramInt, FingerprintManager.-wrap1(FingerprintManager.this, paramInt));
          return;
        }
      } while (FingerprintManager.-get4(FingerprintManager.this) == null);
      FingerprintManager.-get4(FingerprintManager.this).onRemovalError(FingerprintManager.-get5(FingerprintManager.this), paramInt, FingerprintManager.-wrap1(FingerprintManager.this, paramInt));
    }
    
    private void sendRemovedResult(long paramLong, int paramInt1, int paramInt2)
    {
      if (FingerprintManager.-get4(FingerprintManager.this) != null)
      {
        int i = FingerprintManager.-get5(FingerprintManager.this).getFingerId();
        int j = FingerprintManager.-get5(FingerprintManager.this).getGroupId();
        if ((i != 0) && (paramInt1 != 0) && (paramInt1 != i))
        {
          Log.w("FingerprintManager", "Finger id didn't match: " + paramInt1 + " != " + i);
          return;
        }
        if (paramInt2 != j)
        {
          Log.w("FingerprintManager", "Group id didn't match: " + paramInt2 + " != " + j);
          return;
        }
        FingerprintManager.-get4(FingerprintManager.this).onRemovalSucceeded(new Fingerprint(null, paramInt2, paramInt1, paramLong));
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 100: 
        sendEnrollResult((Fingerprint)paramMessage.obj, paramMessage.arg1);
        return;
      case 101: 
        sendAcquiredResult(((Long)paramMessage.obj).longValue(), paramMessage.arg1);
        return;
      case 102: 
        sendAuthenticatedSucceeded((Fingerprint)paramMessage.obj, paramMessage.arg1);
        return;
      case 103: 
        sendAuthenticatedFailed();
        return;
      case 104: 
        sendErrorResult(((Long)paramMessage.obj).longValue(), paramMessage.arg1);
        return;
      }
      sendRemovedResult(((Long)paramMessage.obj).longValue(), paramMessage.arg1, paramMessage.arg2);
    }
  }
  
  private class OnAuthenticationCancelListener
    implements CancellationSignal.OnCancelListener
  {
    private FingerprintManager.CryptoObject mCrypto;
    
    public OnAuthenticationCancelListener(FingerprintManager.CryptoObject paramCryptoObject)
    {
      this.mCrypto = paramCryptoObject;
    }
    
    public void onCancel()
    {
      FingerprintManager.-wrap2(FingerprintManager.this, this.mCrypto);
    }
  }
  
  private class OnEnrollCancelListener
    implements CancellationSignal.OnCancelListener
  {
    private OnEnrollCancelListener() {}
    
    public void onCancel()
    {
      FingerprintManager.-wrap3(FingerprintManager.this);
    }
  }
  
  public static abstract class RemovalCallback
  {
    public void onRemovalError(Fingerprint paramFingerprint, int paramInt, CharSequence paramCharSequence) {}
    
    public void onRemovalSucceeded(Fingerprint paramFingerprint) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/FingerprintManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */