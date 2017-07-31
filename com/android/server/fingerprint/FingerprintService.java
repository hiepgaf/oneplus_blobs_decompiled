package com.android.server.fingerprint;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.PendingIntent;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.IFingerprintDaemon;
import android.hardware.fingerprint.IFingerprintDaemon.Stub;
import android.hardware.fingerprint.IFingerprintDaemonCallback;
import android.hardware.fingerprint.IFingerprintDaemonCallback.Stub;
import android.hardware.fingerprint.IFingerprintService.Stub;
import android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.server.SystemService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FingerprintService
  extends SystemService
  implements IBinder.DeathRecipient
{
  private static final String ACTION_LOCKOUT_RESET = "com.android.server.fingerprint.ACTION_LOCKOUT_RESET";
  private static final long CANCEL_TIMEOUT_LIMIT = 3000L;
  static final boolean DEBUG = true;
  private static final int DISABLE_FP_LONGPRESS = 4;
  private static final int ENABLE_FP_LONGPRESS = 3;
  private static final long FAIL_LOCKOUT_TIMEOUT_MS = 30000L;
  private static final String FINGERPRINTD = "android.hardware.fingerprint.IFingerprintDaemon";
  private static final String FP_DATA_DIR = "fpdata";
  private static final int MAX_FAILED_ATTEMPTS = 5;
  private static final int MSG_USER_SWITCHING = 10;
  static final String TAG = "FingerprintService";
  private final AlarmManager mAlarmManager;
  private final AppOpsManager mAppOps;
  private Context mContext;
  private HashMap<Integer, PerformanceStats> mCryptoPerformanceMap = new HashMap();
  private long mCurrentAuthenticatorId;
  private ClientMonitor mCurrentClient;
  private int mCurrentUserId = -2;
  private IFingerprintDaemon mDaemon;
  private IFingerprintDaemonCallback mDaemonCallback = new IFingerprintDaemonCallback.Stub()
  {
    public void onAcquired(final long paramAnonymousLong, int paramAnonymousInt)
    {
      Slog.d("FingerprintService", "onAcquired, " + paramAnonymousInt + ", " + FingerprintService.-wrap1(FingerprintService.this));
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleAcquired(paramAnonymousLong, this.val$acquiredInfo);
        }
      });
    }
    
    public void onAuthenticated(final long paramAnonymousLong, int paramAnonymousInt1, final int paramAnonymousInt2)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleAuthenticated(paramAnonymousLong, paramAnonymousInt2, this.val$groupId);
        }
      });
    }
    
    public void onEnrollResult(final long paramAnonymousLong, int paramAnonymousInt1, final int paramAnonymousInt2, final int paramAnonymousInt3)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleEnrollResult(paramAnonymousLong, paramAnonymousInt2, paramAnonymousInt3, this.val$remaining);
        }
      });
    }
    
    public void onEnumerate(final long paramAnonymousLong, int[] paramAnonymousArrayOfInt1, final int[] paramAnonymousArrayOfInt2)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleEnumerate(paramAnonymousLong, paramAnonymousArrayOfInt2, this.val$groupIds);
        }
      });
    }
    
    public void onError(final long paramAnonymousLong, int paramAnonymousInt)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleError(paramAnonymousLong, this.val$error);
        }
      });
    }
    
    public void onRemoved(final long paramAnonymousLong, int paramAnonymousInt1, final int paramAnonymousInt2)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.handleRemoved(paramAnonymousLong, paramAnonymousInt2, this.val$groupId);
        }
      });
    }
  };
  private int mFailedAttempts;
  private final FingerprintUtils mFingerprintUtils = FingerprintUtils.getInstance();
  private long mHalDeviceId;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        Slog.w("FingerprintService", "Unknown message:" + paramAnonymousMessage.what);
        return;
      }
      FingerprintService.this.handleUserSwitching(paramAnonymousMessage.arg1);
    }
  };
  private final String mKeyguardPackage;
  private final ArrayList<FingerprintServiceLockoutResetMonitor> mLockoutMonitors = new ArrayList();
  private final BroadcastReceiver mLockoutReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("com.android.server.fingerprint.ACTION_LOCKOUT_RESET".equals(paramAnonymousIntent.getAction())) {
        FingerprintService.this.resetFailedAttempts();
      }
    }
  };
  private ClientMonitor mPendingClient;
  private HashMap<Integer, PerformanceStats> mPerformanceMap = new HashMap();
  private PerformanceStats mPerformanceStats;
  private final PowerManager mPowerManager;
  private ClientMonitor mRemovingClient;
  private final Runnable mResetClientState = new Runnable()
  {
    public void run()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Client ");
      if (FingerprintService.-get2(FingerprintService.this) != null)
      {
        str = FingerprintService.-get2(FingerprintService.this).getOwnerString();
        localStringBuilder = localStringBuilder.append(str).append(" failed to respond to cancel, starting client ");
        if (FingerprintService.-get8(FingerprintService.this) == null) {
          break label111;
        }
      }
      label111:
      for (String str = FingerprintService.-get8(FingerprintService.this).getOwnerString();; str = "null")
      {
        Slog.w("FingerprintService", str);
        FingerprintService.-set0(FingerprintService.this, null);
        FingerprintService.-wrap10(FingerprintService.this, FingerprintService.-get8(FingerprintService.this), false);
        return;
        str = "null";
        break;
      }
    }
  };
  private final Runnable mResetFailedAttemptsRunnable = new Runnable()
  {
    public void run()
    {
      FingerprintService.this.resetFailedAttempts();
    }
  };
  private final UserManager mUserManager;
  
  public FingerprintService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mKeyguardPackage = ComponentName.unflattenFromString(paramContext.getResources().getString(17039466)).getPackageName();
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService(AppOpsManager.class));
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService(PowerManager.class));
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService(AlarmManager.class));
    this.mContext.registerReceiver(this.mLockoutReceiver, new IntentFilter("com.android.server.fingerprint.ACTION_LOCKOUT_RESET"), "android.permission.RESET_FINGERPRINT_LOCKOUT", null);
    this.mUserManager = UserManager.get(this.mContext);
  }
  
  private void addLockoutResetMonitor(FingerprintServiceLockoutResetMonitor paramFingerprintServiceLockoutResetMonitor)
  {
    if (!this.mLockoutMonitors.contains(paramFingerprintServiceLockoutResetMonitor)) {
      this.mLockoutMonitors.add(paramFingerprintServiceLockoutResetMonitor);
    }
  }
  
  private boolean canUseFingerprint(String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    checkPermission("android.permission.USE_FINGERPRINT");
    if (isKeyguard(paramString)) {
      return true;
    }
    if (!isCurrentUserOrProfile(UserHandle.getCallingUserId()))
    {
      Slog.w("FingerprintService", "Rejecting " + paramString + " ; not a current user or profile");
      return false;
    }
    if (this.mAppOps.noteOp(55, paramInt1, paramString) != 0)
    {
      Slog.w("FingerprintService", "Rejecting " + paramString + " ; permission denied");
      return false;
    }
    if ((!paramBoolean) || (isForegroundActivity(paramInt1, paramInt2)) || (currentClient(paramString))) {
      return true;
    }
    Slog.w("FingerprintService", "Rejecting " + paramString + " ; not in foreground");
    return false;
  }
  
  private void cancelLockoutReset()
  {
    this.mAlarmManager.cancel(getLockoutResetIntent());
  }
  
  private boolean currentClient(String paramString)
  {
    if (this.mCurrentClient != null) {
      return this.mCurrentClient.getOwnerString().equals(paramString);
    }
    return false;
  }
  
  private void dumpInternal(PrintWriter paramPrintWriter)
  {
    JSONObject localJSONObject1 = new JSONObject();
    Object localObject;
    for (;;)
    {
      try
      {
        localJSONObject1.put("service", "Fingerprint Manager");
        JSONArray localJSONArray = new JSONArray();
        localObject = UserManager.get(getContext()).getUsers().iterator();
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        i = ((UserInfo)((Iterator)localObject).next()).getUserHandle().getIdentifier();
        int j = this.mFingerprintUtils.getFingerprintsForUser(this.mContext, i).size();
        PerformanceStats localPerformanceStats1 = (PerformanceStats)this.mPerformanceMap.get(Integer.valueOf(i));
        PerformanceStats localPerformanceStats2 = (PerformanceStats)this.mCryptoPerformanceMap.get(Integer.valueOf(i));
        JSONObject localJSONObject2 = new JSONObject();
        localJSONObject2.put("id", i);
        localJSONObject2.put("count", j);
        if (localPerformanceStats1 == null) {
          break label352;
        }
        i = localPerformanceStats1.accept;
        localJSONObject2.put("accept", i);
        if (localPerformanceStats1 == null) {
          break label357;
        }
        i = localPerformanceStats1.reject;
        localJSONObject2.put("reject", i);
        if (localPerformanceStats1 == null) {
          break label362;
        }
        i = localPerformanceStats1.acquire;
        localJSONObject2.put("acquire", i);
        if (localPerformanceStats1 == null) {
          break label367;
        }
        i = localPerformanceStats1.lockout;
        localJSONObject2.put("lockout", i);
        if (localPerformanceStats2 == null) {
          break label372;
        }
        i = localPerformanceStats2.accept;
        localJSONObject2.put("acceptCrypto", i);
        if (localPerformanceStats2 == null) {
          break label377;
        }
        i = localPerformanceStats2.reject;
        localJSONObject2.put("rejectCrypto", i);
        if (localPerformanceStats2 == null) {
          break label382;
        }
        i = localPerformanceStats2.acquire;
        localJSONObject2.put("acquireCrypto", i);
        if (localPerformanceStats2 == null) {
          break label387;
        }
        i = localPerformanceStats2.lockout;
        localJSONObject2.put("lockoutCrypto", i);
        localJSONArray.put(localJSONObject2);
        continue;
        paramPrintWriter.println(localJSONObject1);
      }
      catch (JSONException localJSONException)
      {
        Slog.e("FingerprintService", "dump formatting failure", localJSONException);
      }
      return;
      label352:
      int i = 0;
      continue;
      label357:
      i = 0;
      continue;
      label362:
      i = 0;
      continue;
      label367:
      i = 0;
      continue;
      label372:
      i = 0;
      continue;
      label377:
      i = 0;
      continue;
      label382:
      i = 0;
      continue;
      label387:
      i = 0;
    }
    localJSONObject1.put("prints", localJSONException);
    if (this.mCurrentClient != null)
    {
      if (!(this.mCurrentClient instanceof AuthenticationClient)) {
        break label712;
      }
      paramPrintWriter.println("AuthenticationClient " + this.mCurrentClient.getOwnerString() + ", " + this.mCurrentClient.getToken());
    }
    for (;;)
    {
      if (this.mPendingClient != null) {
        paramPrintWriter.println("PendingClient: " + this.mPendingClient.getOwnerString() + ", " + this.mPendingClient.getToken());
      }
      paramPrintWriter.println("FailedAttempts: " + this.mFailedAttempts);
      paramPrintWriter.println("CurrentUserId: " + this.mCurrentUserId);
      paramPrintWriter.println("CurrentAuthenticatorId: " + this.mCurrentAuthenticatorId);
      paramPrintWriter.println("hasEnrolledFingerprints: " + hasEnrolledFingerprints(this.mCurrentUserId));
      StringBuilder localStringBuilder = new StringBuilder().append("FP_ERROR_VIBRATE_PATTERN: ");
      localObject = this.mFingerprintUtils;
      paramPrintWriter.println(Arrays.toString(FingerprintUtils.FP_ERROR_VIBRATE_PATTERN));
      localStringBuilder = new StringBuilder().append("FP_SUCCESS_VIBRATE_PATTERN: ");
      localObject = this.mFingerprintUtils;
      paramPrintWriter.println(Arrays.toString(FingerprintUtils.FP_SUCCESS_VIBRATE_PATTERN));
      break;
      label712:
      paramPrintWriter.println("EnrollClient: " + this.mCurrentClient.getOwnerString() + ", " + this.mCurrentClient.getToken());
    }
  }
  
  private void forceCancelAuthentication(String paramString, boolean paramBoolean)
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon == null)
    {
      Slog.w("FingerprintService", "forceCancelAuthentication: no fingeprintd!");
      return;
    }
    if ((paramBoolean) && (inLockoutMode())) {}
    do
    {
      Slog.d("FingerprintService", "forceCancelAuthentication: packageName=" + paramString + ", no client=" + paramBoolean);
      try
      {
        int i = localIFingerprintDaemon.cancelAuthentication();
        if (i != 0) {
          Slog.w("FingerprintService", "forceCancelAuthentication failed, result=" + i);
        }
      }
      catch (RemoteException paramString)
      {
        for (;;)
        {
          Slog.e("FingerprintService", "forceCancelAuthentication failed", paramString);
        }
      }
      handleError(this.mHalDeviceId, 5);
      return;
      if (isKeyguard(paramString)) {
        return;
      }
    } while ((this.mCurrentClient != null) && (paramString != null) && (paramString.equals(this.mCurrentClient.getOwnerString())));
  }
  
  private String getAuthPackageInternal()
  {
    if ((this.mCurrentClient != null) && ((this.mCurrentClient instanceof AuthenticationClient))) {
      return this.mCurrentClient.getOwnerString();
    }
    return null;
  }
  
  private PendingIntent getLockoutResetIntent()
  {
    return PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.server.fingerprint.ACTION_LOCKOUT_RESET"), 134217728);
  }
  
  private int getUserOrWorkProfileId(String paramString, int paramInt)
  {
    if ((!isKeyguard(paramString)) && (isWorkProfile(paramInt))) {
      return paramInt;
    }
    return getEffectiveUserId(paramInt);
  }
  
  private boolean inLockoutMode()
  {
    return this.mFailedAttempts >= 5;
  }
  
  private boolean isForegroundActivity(int paramInt1, int paramInt2)
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getRunningAppProcesses();
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localList.get(i);
        if ((localRunningAppProcessInfo.pid == paramInt2) && (localRunningAppProcessInfo.uid == paramInt1))
        {
          int k = localRunningAppProcessInfo.importance;
          if (k == 100) {
            return true;
          }
        }
        i += 1;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("FingerprintService", "am.getRunningAppProcesses() failed");
    }
  }
  
  private boolean isKeyguard(String paramString)
  {
    return this.mKeyguardPackage.equals(paramString);
  }
  
  private boolean isWorkProfile(int paramInt)
  {
    UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
    if (localUserInfo != null) {
      return localUserInfo.isManagedProfile();
    }
    return false;
  }
  
  private void listenForUserSwitches()
  {
    try
    {
      ActivityManagerNative.getDefault().registerUserSwitchObserver(new SynchronousUserSwitchObserver()
      {
        public void onForegroundProfileSwitch(int paramAnonymousInt) {}
        
        public void onUserSwitchComplete(int paramAnonymousInt)
          throws RemoteException
        {}
        
        public void onUserSwitching(int paramAnonymousInt)
          throws RemoteException
        {
          FingerprintService.-get7(FingerprintService.this).obtainMessage(10, paramAnonymousInt, 0).sendToTarget();
        }
      }, "FingerprintService");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("FingerprintService", "Failed to listen for user switching event", localRemoteException);
    }
  }
  
  private void notifyLockoutResetMonitors()
  {
    int i = 0;
    while (i < this.mLockoutMonitors.size())
    {
      ((FingerprintServiceLockoutResetMonitor)this.mLockoutMonitors.get(i)).sendLockoutReset();
      i += 1;
    }
  }
  
  private void removeClient(ClientMonitor paramClientMonitor)
  {
    if (paramClientMonitor != null)
    {
      paramClientMonitor.destroy();
      if ((paramClientMonitor != this.mCurrentClient) && (this.mCurrentClient != null)) {
        if ("Unexpected client: " + paramClientMonitor.getOwnerString() + "expected: " + this.mCurrentClient == null) {
          break label119;
        }
      }
    }
    label119:
    for (String str = this.mCurrentClient.getOwnerString();; str = "null")
    {
      Slog.w("FingerprintService", str);
      if (this.mCurrentClient != null)
      {
        Slog.v("FingerprintService", "Done with client: " + paramClientMonitor.getOwnerString());
        this.mCurrentClient = null;
      }
      return;
    }
  }
  
  private void removeLockoutResetCallback(FingerprintServiceLockoutResetMonitor paramFingerprintServiceLockoutResetMonitor)
  {
    this.mLockoutMonitors.remove(paramFingerprintServiceLockoutResetMonitor);
  }
  
  private void scheduleLockoutReset()
  {
    this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + 30000L, getLockoutResetIntent());
  }
  
  private void startAuthentication(IBinder paramIBinder, long paramLong, int paramInt1, int paramInt2, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt3, boolean paramBoolean, String paramString)
  {
    updateActiveGroup(paramInt2, paramString);
    Slog.v("FingerprintService", "startAuthentication(" + paramString + ")");
    paramIBinder = new AuthenticationClient(getContext(), this.mHalDeviceId, paramIBinder, paramIFingerprintServiceReceiver, this.mCurrentUserId, paramInt2, paramLong, paramBoolean, paramString)
    {
      public IFingerprintDaemon getFingerprintDaemon()
      {
        return FingerprintService.this.getFingerprintDaemon();
      }
      
      public boolean handleFailedAttempt()
      {
        Object localObject = FingerprintService.this;
        FingerprintService.-set1((FingerprintService)localObject, FingerprintService.-get4((FingerprintService)localObject) + 1);
        if (FingerprintService.-get4(FingerprintService.this) == 5)
        {
          localObject = FingerprintService.-get10(FingerprintService.this);
          ((FingerprintService.PerformanceStats)localObject).lockout += 1;
        }
        if (FingerprintService.-wrap1(FingerprintService.this))
        {
          if (FingerprintService.-wrap2(FingerprintService.this, this.val$opPackageName)) {
            FingerprintService.-wrap6(FingerprintService.this, this.val$opPackageName, true);
          }
          FingerprintService.-wrap8(FingerprintService.this);
          return true;
        }
        return false;
      }
      
      public void notifyUserActivity()
      {
        FingerprintService.-wrap13(FingerprintService.this);
      }
      
      public void resetFailedAttempts()
      {
        FingerprintService.this.resetFailedAttempts();
      }
    };
    if (inLockoutMode())
    {
      Slog.v("FingerprintService", "In lockout mode; disallowing authentication , " + paramString);
      if (!paramIBinder.onError(7)) {
        Slog.w("FingerprintService", "Cannot send timeout message to client");
      }
      return;
    }
    startClient(paramIBinder, true);
  }
  
  private void startClient(ClientMonitor paramClientMonitor, boolean paramBoolean)
  {
    ClientMonitor localClientMonitor = this.mCurrentClient;
    if (localClientMonitor != null)
    {
      Slog.v("FingerprintService", "request stop current client " + localClientMonitor.getOwnerString());
      this.mRemovingClient = localClientMonitor;
      localClientMonitor.stop(paramBoolean);
      this.mPendingClient = paramClientMonitor;
      this.mHandler.removeCallbacks(this.mResetClientState);
      this.mHandler.postDelayed(this.mResetClientState, 3000L);
    }
    while (paramClientMonitor == null) {
      return;
    }
    this.mCurrentClient = paramClientMonitor;
    Slog.v("FingerprintService", "starting client " + paramClientMonitor.getClass().getSuperclass().getSimpleName() + "(" + paramClientMonitor.getOwnerString() + ")" + ", initiatedByClient = " + paramBoolean + ")");
    if (isKeyguard(paramClientMonitor.getOwnerString())) {
      updateStatus(3);
    }
    for (;;)
    {
      paramClientMonitor.start();
      if ((this.mPendingClient == null) || (this.mPendingClient.getOwnerString() == null) || (!this.mPendingClient.getOwnerString().equals(paramClientMonitor.getOwnerString()))) {
        break;
      }
      Slog.d("FingerprintService", "remove pending client: " + this.mPendingClient.getOwnerString());
      this.mPendingClient = null;
      return;
      updateStatus(4);
    }
  }
  
  private void startEnrollment(IBinder paramIBinder, byte[] paramArrayOfByte, int paramInt1, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt2, boolean paramBoolean, String paramString)
  {
    updateActiveGroup(paramInt1, paramString);
    startClient(new EnrollClient(getContext(), this.mHalDeviceId, paramIBinder, paramIFingerprintServiceReceiver, paramInt1, paramInt1, paramArrayOfByte, paramBoolean, paramString)
    {
      public IFingerprintDaemon getFingerprintDaemon()
      {
        return FingerprintService.this.getFingerprintDaemon();
      }
      
      public void notifyUserActivity()
      {
        FingerprintService.-wrap13(FingerprintService.this);
      }
    }, true);
  }
  
  private void updateActiveGroup(int paramInt, String paramString)
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon != null) {}
    try
    {
      paramInt = getUserOrWorkProfileId(paramString, paramInt);
      if (paramInt != this.mCurrentUserId)
      {
        paramString = new File(Environment.getUserSystemDirectory(paramInt), "fpdata");
        if (!paramString.exists())
        {
          if (!paramString.mkdir())
          {
            Slog.v("FingerprintService", "Cannot make directory: " + paramString.getAbsolutePath());
            return;
          }
          if (!SELinux.restorecon(paramString))
          {
            Slog.w("FingerprintService", "Restorecons failed. Directory will have wrong label.");
            return;
          }
        }
        localIFingerprintDaemon.setActiveGroup(paramInt, paramString.getAbsolutePath().getBytes());
        this.mCurrentUserId = paramInt;
      }
      this.mCurrentAuthenticatorId = localIFingerprintDaemon.getAuthenticatorId();
      return;
    }
    catch (RemoteException paramString)
    {
      Slog.e("FingerprintService", "Failed to setActiveGroup():", paramString);
    }
  }
  
  private void userActivity()
  {
    long l = SystemClock.uptimeMillis();
    this.mPowerManager.userActivity(l, 2, 0);
  }
  
  public void binderDied()
  {
    Slog.v("FingerprintService", "fingerprintd died");
    MetricsLogger.count(this.mContext, "fingerprintd_died", 1);
    this.mDaemon = null;
    this.mCurrentUserId = -2;
    handleError(this.mHalDeviceId, 1);
  }
  
  void checkPermission(String paramString)
  {
    getContext().enforceCallingOrSelfPermission(paramString, "Must have " + paramString + " permission.");
  }
  
  public long getAuthenticatorId(String paramString)
  {
    paramString = getFingerprintDaemon();
    if (paramString != null) {}
    try
    {
      this.mCurrentAuthenticatorId = paramString.getAuthenticatorId();
      return this.mCurrentAuthenticatorId;
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        Slog.e("FingerprintService", "Failed to setActiveGroup():", paramString);
      }
    }
  }
  
  int getEffectiveUserId(int paramInt)
  {
    UserManager localUserManager = UserManager.get(this.mContext);
    if (localUserManager != null)
    {
      long l = Binder.clearCallingIdentity();
      paramInt = localUserManager.getCredentialOwnerProfile(paramInt);
      Binder.restoreCallingIdentity(l);
      return paramInt;
    }
    Slog.e("FingerprintService", "Unable to acquire UserManager");
    return paramInt;
  }
  
  public List<Fingerprint> getEnrolledFingerprints(int paramInt)
  {
    return this.mFingerprintUtils.getFingerprintsForUser(this.mContext, paramInt);
  }
  
  public IFingerprintDaemon getFingerprintDaemon()
  {
    if (this.mDaemon == null)
    {
      this.mDaemon = IFingerprintDaemon.Stub.asInterface(ServiceManager.getService("android.hardware.fingerprint.IFingerprintDaemon"));
      if (this.mDaemon == null) {
        break label137;
      }
    }
    for (;;)
    {
      try
      {
        this.mDaemon.asBinder().linkToDeath(this, 0);
        this.mDaemon.init(this.mDaemonCallback);
        this.mHalDeviceId = this.mDaemon.openHal();
        if (this.mHalDeviceId != 0L)
        {
          updateActiveGroup(ActivityManager.getCurrentUser(), null);
          return this.mDaemon;
        }
        Slog.w("FingerprintService", "Failed to open Fingerprint HAL!");
        MetricsLogger.count(this.mContext, "fingerprintd_openhal_error", 1);
        this.mDaemon = null;
        continue;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("FingerprintService", "Failed to open fingeprintd HAL", localRemoteException);
        this.mDaemon = null;
        continue;
      }
      label137:
      Slog.w("FingerprintService", "fingerprint service not available");
    }
  }
  
  public int getStatus()
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon != null) {
      try
      {
        int i = localIFingerprintDaemon.getStatus();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("FingerprintService", "getStatus failed", localRemoteException);
      }
    }
    return 0;
  }
  
  protected void handleAcquired(long paramLong, int paramInt)
  {
    Object localObject = this.mCurrentClient;
    if ((localObject != null) && (((ClientMonitor)localObject).onAcquired(paramInt))) {
      removeClient((ClientMonitor)localObject);
    }
    if ((this.mPerformanceStats == null) || (inLockoutMode())) {}
    while (!(localObject instanceof AuthenticationClient)) {
      return;
    }
    localObject = this.mPerformanceStats;
    ((PerformanceStats)localObject).acquire += 1;
  }
  
  protected void handleAuthenticated(long paramLong, int paramInt1, int paramInt2)
  {
    Object localObject = this.mCurrentClient;
    if ((paramInt1 == 0) && (3 == getStatus())) {
      return;
    }
    if ((localObject != null) && (((ClientMonitor)localObject).onAuthenticated(paramInt1, paramInt2))) {
      removeClient((ClientMonitor)localObject);
    }
    if (paramInt1 != 0)
    {
      localObject = this.mPerformanceStats;
      ((PerformanceStats)localObject).accept += 1;
      return;
    }
    localObject = this.mPerformanceStats;
    ((PerformanceStats)localObject).reject += 1;
  }
  
  protected void handleEnrollResult(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    ClientMonitor localClientMonitor = this.mCurrentClient;
    if ((localClientMonitor != null) && (localClientMonitor.onEnrollResult(paramInt1, paramInt2, paramInt3))) {
      removeClient(localClientMonitor);
    }
    do
    {
      return;
      Slog.d("FingerprintService", "handleEnrollResult when client == null, remaining = " + paramInt3 + ", fingerId = " + paramInt1 + ", groupId = " + paramInt2);
    } while (paramInt3 != 0);
    this.mFingerprintUtils.addFingerprintForUser(this.mContext, paramInt1, paramInt2);
  }
  
  protected void handleEnumerate(long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1.length != paramArrayOfInt2.length)
    {
      Slog.w("FingerprintService", "fingerIds and groupIds differ in length: f[]=" + Arrays.toString(paramArrayOfInt1) + ", g[]=" + Arrays.toString(paramArrayOfInt2));
      return;
    }
    Slog.w("FingerprintService", "Enumerate: f[]=" + paramArrayOfInt1 + ", g[]=" + paramArrayOfInt2);
  }
  
  protected void handleError(long paramLong, int paramInt)
  {
    ClientMonitor localClientMonitor = this.mCurrentClient;
    StringBuilder localStringBuilder = new StringBuilder().append("handleError: removing client: ");
    if (this.mRemovingClient == null)
    {
      str = null;
      localStringBuilder = localStringBuilder.append(str).append(", current client = ");
      if (localClientMonitor != null) {
        break label92;
      }
    }
    label92:
    for (String str = null;; str = localClientMonitor.getOwnerString())
    {
      Slog.d("FingerprintService", str);
      if (localClientMonitor == this.mRemovingClient) {
        break label102;
      }
      return;
      str = this.mRemovingClient.getOwnerString();
      break;
    }
    label102:
    if ((localClientMonitor != null) && (localClientMonitor.onError(paramInt))) {
      removeClient(localClientMonitor);
    }
    localStringBuilder = new StringBuilder().append("handleError(client=");
    if (localClientMonitor != null) {}
    for (str = localClientMonitor.getOwnerString();; str = "null")
    {
      Slog.v("FingerprintService", str + ", error = " + paramInt + ")");
      if (paramInt == 5)
      {
        this.mHandler.removeCallbacks(this.mResetClientState);
        if (this.mPendingClient != null)
        {
          Slog.v("FingerprintService", "start pending client " + this.mPendingClient.getOwnerString());
          startClient(this.mPendingClient, false);
          this.mPendingClient = null;
        }
      }
      return;
    }
  }
  
  protected void handleRemoved(long paramLong, int paramInt1, int paramInt2)
  {
    ClientMonitor localClientMonitor = this.mCurrentClient;
    if ((localClientMonitor != null) && (localClientMonitor.onRemoved(paramInt1, paramInt2))) {
      removeClient(localClientMonitor);
    }
  }
  
  void handleUserSwitching(int paramInt)
  {
    updateActiveGroup(paramInt, null);
  }
  
  public boolean hasEnrolledFingerprints(int paramInt)
  {
    boolean bool = false;
    if (paramInt != UserHandle.getCallingUserId()) {
      checkPermission("android.permission.INTERACT_ACROSS_USERS");
    }
    if (this.mFingerprintUtils.getFingerprintsForUser(this.mContext, paramInt).size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean hasPermission(String paramString)
  {
    boolean bool = false;
    if (getContext().checkCallingOrSelfPermission(paramString) == 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isCurrentUserOrProfile(int paramInt)
  {
    int[] arrayOfInt = UserManager.get(this.mContext).getEnabledProfileIds(paramInt);
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (arrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onStart()
  {
    publishBinderService("fingerprint", new FingerprintServiceWrapper(null));
    getFingerprintDaemon();
    Slog.v("FingerprintService", "Fingerprint HAL id: " + this.mHalDeviceId);
    listenForUserSwitches();
  }
  
  protected void resetFailedAttempts()
  {
    if (inLockoutMode()) {
      Slog.v("FingerprintService", "Reset fingerprint lockout");
    }
    this.mFailedAttempts = 0;
    cancelLockoutReset();
    notifyLockoutResetMonitors();
  }
  
  public int startPostEnroll(IBinder paramIBinder)
  {
    paramIBinder = getFingerprintDaemon();
    if (paramIBinder == null)
    {
      Slog.w("FingerprintService", "startPostEnroll: no fingeprintd!");
      return 0;
    }
    try
    {
      int i = paramIBinder.postEnroll();
      return i;
    }
    catch (RemoteException paramIBinder)
    {
      Slog.e("FingerprintService", "startPostEnroll failed", paramIBinder);
    }
    return 0;
  }
  
  public long startPreEnroll(IBinder paramIBinder)
  {
    paramIBinder = getFingerprintDaemon();
    if (paramIBinder == null)
    {
      Slog.w("FingerprintService", "startPreEnroll: no fingeprintd!");
      return 0L;
    }
    try
    {
      long l = paramIBinder.preEnroll();
      return l;
    }
    catch (RemoteException paramIBinder)
    {
      Slog.e("FingerprintService", "startPreEnroll failed", paramIBinder);
    }
    return 0L;
  }
  
  void startRemove(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, boolean paramBoolean)
  {
    if (getFingerprintDaemon() == null)
    {
      Slog.w("FingerprintService", "startRemove: no fingeprintd!");
      return;
    }
    startClient(new RemovalClient(getContext(), this.mHalDeviceId, paramIBinder, paramIFingerprintServiceReceiver, paramInt1, paramInt2, paramInt3, paramBoolean, paramIBinder.toString())
    {
      public IFingerprintDaemon getFingerprintDaemon()
      {
        return FingerprintService.this.getFingerprintDaemon();
      }
      
      public void notifyUserActivity()
      {
        FingerprintService.-wrap13(FingerprintService.this);
      }
    }, true);
  }
  
  public int updateStatus(int paramInt)
  {
    IFingerprintDaemon localIFingerprintDaemon = getFingerprintDaemon();
    if (localIFingerprintDaemon != null) {
      try
      {
        Slog.d("FingerprintService", "updateStatus , " + paramInt);
        paramInt = localIFingerprintDaemon.updateStatus(paramInt);
        return paramInt;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("FingerprintService", "updateStatus failed", localRemoteException);
      }
    }
    return 0;
  }
  
  private class FingerprintServiceLockoutResetMonitor
  {
    private final IFingerprintServiceLockoutResetCallback mCallback;
    private final Runnable mRemoveCallbackRunnable = new Runnable()
    {
      public void run()
      {
        FingerprintService.-wrap7(FingerprintService.this, FingerprintService.FingerprintServiceLockoutResetMonitor.this);
      }
    };
    
    public FingerprintServiceLockoutResetMonitor(IFingerprintServiceLockoutResetCallback paramIFingerprintServiceLockoutResetCallback)
    {
      this.mCallback = paramIFingerprintServiceLockoutResetCallback;
    }
    
    public void sendLockoutReset()
    {
      if (this.mCallback != null) {}
      try
      {
        this.mCallback.onLockoutReset(FingerprintService.-get6(FingerprintService.this));
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("FingerprintService", "Failed to invoke onLockoutReset: ", localRemoteException);
        return;
      }
      catch (DeadObjectException localDeadObjectException)
      {
        Slog.w("FingerprintService", "Death object while invoking onLockoutReset: ", localDeadObjectException);
        FingerprintService.-get7(FingerprintService.this).post(this.mRemoveCallbackRunnable);
      }
    }
  }
  
  private final class FingerprintServiceWrapper
    extends IFingerprintService.Stub
  {
    private FingerprintServiceWrapper() {}
    
    private boolean isRestricted()
    {
      return !FingerprintService.this.hasPermission("android.permission.MANAGE_FINGERPRINT");
    }
    
    private boolean shouldBlockAuthenticate(String paramString)
    {
      return ("com.oneplus.applocker".equals(paramString)) || ("com.oneplus.filemanager".equals(paramString));
    }
    
    public void addLockoutResetCallback(final IFingerprintServiceLockoutResetCallback paramIFingerprintServiceLockoutResetCallback)
      throws RemoteException
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.-wrap4(FingerprintService.this, new FingerprintService.FingerprintServiceLockoutResetMonitor(FingerprintService.this, paramIFingerprintServiceLockoutResetCallback));
        }
      });
    }
    
    public void authenticate(IBinder paramIBinder, final long paramLong, final int paramInt1, final IFingerprintServiceReceiver paramIFingerprintServiceReceiver, final int paramInt2, final String paramString)
    {
      final int i = Binder.getCallingUid();
      final int j = UserHandle.getCallingUserId();
      final int k = Binder.getCallingPid();
      final boolean bool = isRestricted();
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          int i = 1;
          if (!FingerprintService.-wrap0(FingerprintService.this, paramString, true, i, k))
          {
            Slog.v("FingerprintService", "authenticate(): reject " + paramString);
            return;
          }
          try
          {
            if ((FingerprintService.-get2(FingerprintService.this) != null) && (FingerprintService.FingerprintServiceWrapper.-wrap0(FingerprintService.FingerprintServiceWrapper.this, paramString)) && (FingerprintService.-wrap2(FingerprintService.this, FingerprintService.-get2(FingerprintService.this).getOwnerString())))
            {
              boolean bool = ActivityManagerNative.getDefault().isKeyguardDone();
              if (!bool) {
                break label294;
              }
            }
          }
          catch (Exception localException)
          {
            Object localObject;
            label294:
            label308:
            for (;;) {}
          }
          localObject = FingerprintService.-get0(FingerprintService.this);
          if (paramLong != 0L)
          {
            MetricsLogger.histogram((Context)localObject, "fingerprint_token", i);
            if (paramLong != 0L) {
              break label308;
            }
          }
          for (localObject = FingerprintService.-get9(FingerprintService.this);; localObject = FingerprintService.-get1(FingerprintService.this))
          {
            FingerprintService.PerformanceStats localPerformanceStats2 = (FingerprintService.PerformanceStats)((HashMap)localObject).get(Integer.valueOf(FingerprintService.-get3(FingerprintService.this)));
            FingerprintService.PerformanceStats localPerformanceStats1 = localPerformanceStats2;
            if (localPerformanceStats2 == null)
            {
              localPerformanceStats1 = new FingerprintService.PerformanceStats(FingerprintService.this, null);
              ((HashMap)localObject).put(Integer.valueOf(FingerprintService.-get3(FingerprintService.this)), localPerformanceStats1);
            }
            FingerprintService.-set3(FingerprintService.this, localPerformanceStats1);
            FingerprintService.-wrap9(FingerprintService.this, j, paramLong, paramInt1, paramIFingerprintServiceReceiver, paramInt2, bool, this.val$restricted, paramString);
            return;
            Slog.v("FingerprintService", "authenticate(): reject app authenticating due to systemui in used");
            return;
            i = 0;
            break;
          }
        }
      });
    }
    
    public void cancelAuthentication(final IBinder paramIBinder, final String paramString)
    {
      final int i = Binder.getCallingUid();
      final int j = Binder.getCallingPid();
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          boolean bool = true;
          if (!FingerprintService.-wrap0(FingerprintService.this, paramString, false, i, j)) {
            Slog.v("FingerprintService", "cancelAuthentication(): reject " + paramString);
          }
          ClientMonitor localClientMonitor;
          do
          {
            for (;;)
            {
              return;
              localClientMonitor = FingerprintService.-get2(FingerprintService.this);
              if (!(localClientMonitor instanceof AuthenticationClient)) {
                break label259;
              }
              if (localClientMonitor.getToken() != paramIBinder) {
                break;
              }
              Slog.v("FingerprintService", "stop client " + localClientMonitor.getOwnerString());
              FingerprintService.-set4(FingerprintService.this, localClientMonitor);
              if (localClientMonitor.getToken() == paramIBinder) {}
              while (localClientMonitor.stop(bool) == 0)
              {
                FingerprintService.this.handleError(FingerprintService.-get6(FingerprintService.this), 5);
                return;
                bool = false;
              }
            }
            Slog.v("FingerprintService", "can't stop client " + localClientMonitor.getOwnerString() + " since tokens don't match");
          } while ((FingerprintService.-get8(FingerprintService.this) == null) || (FingerprintService.-get8(FingerprintService.this).getToken() != paramIBinder));
          Slog.v("FingerprintService", "the cancel request is sent from pending client, remove pending client");
          FingerprintService.-set2(FingerprintService.this, null);
          return;
          label259:
          if (localClientMonitor != null)
          {
            Slog.v("FingerprintService", "can't cancel non-authenticating client " + localClientMonitor.getOwnerString());
            return;
          }
          Slog.v("FingerprintService", "force cancel authentication in lockout mode");
          FingerprintService.-wrap6(FingerprintService.this, paramString, true);
        }
      });
    }
    
    public void cancelEnrollment(final IBinder paramIBinder)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          ClientMonitor localClientMonitor = FingerprintService.-get2(FingerprintService.this);
          if (((localClientMonitor instanceof EnrollClient)) && (localClientMonitor.getToken() == paramIBinder))
          {
            FingerprintService.-set4(FingerprintService.this, localClientMonitor);
            if (localClientMonitor.getToken() != paramIBinder) {
              break label61;
            }
          }
          label61:
          for (boolean bool = true;; bool = false)
          {
            localClientMonitor.stop(bool);
            return;
          }
        }
      });
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (FingerprintService.-get0(FingerprintService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump Fingerprint from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        FingerprintService.-wrap5(FingerprintService.this, paramPrintWriter);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void enroll(final IBinder paramIBinder, final byte[] paramArrayOfByte, final int paramInt1, final IFingerprintServiceReceiver paramIFingerprintServiceReceiver, final int paramInt2, final String paramString)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      int i = FingerprintService.-get0(FingerprintService.this).getResources().getInteger(17694881);
      if (FingerprintService.this.getEnrolledFingerprints(paramInt1).size() >= i)
      {
        Slog.w("FingerprintService", "Too many fingerprints registered");
        return;
      }
      if (!FingerprintService.this.isCurrentUserOrProfile(paramInt1)) {
        return;
      }
      final boolean bool = isRestricted();
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.-wrap11(FingerprintService.this, paramIBinder, paramArrayOfByte, paramInt1, paramIFingerprintServiceReceiver, paramInt2, bool, paramString);
        }
      });
    }
    
    public void forceStopAuthentication(final String paramString)
    {
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.-wrap6(FingerprintService.this, paramString, false);
        }
      });
    }
    
    public String getAuthenticatedPackage()
    {
      return FingerprintService.-wrap3(FingerprintService.this);
    }
    
    public long getAuthenticatorId(String paramString)
    {
      return FingerprintService.this.getAuthenticatorId(paramString);
    }
    
    public List<Fingerprint> getEnrolledFingerprints(int paramInt, String paramString)
    {
      if (!FingerprintService.-wrap0(FingerprintService.this, paramString, false, Binder.getCallingUid(), Binder.getCallingPid())) {
        return Collections.emptyList();
      }
      if (!FingerprintService.this.isCurrentUserOrProfile(paramInt)) {
        return Collections.emptyList();
      }
      return FingerprintService.this.getEnrolledFingerprints(paramInt);
    }
    
    public int getStatus()
    {
      return FingerprintService.this.getStatus();
    }
    
    public boolean hasEnrolledFingerprints(int paramInt, String paramString)
    {
      if (!FingerprintService.-wrap0(FingerprintService.this, paramString, false, Binder.getCallingUid(), Binder.getCallingPid())) {
        return false;
      }
      if (!FingerprintService.this.isCurrentUserOrProfile(paramInt)) {
        return false;
      }
      return FingerprintService.this.hasEnrolledFingerprints(paramInt);
    }
    
    public boolean isHardwareDetected(long paramLong, String paramString)
    {
      boolean bool = false;
      if (!FingerprintService.-wrap0(FingerprintService.this, paramString, false, Binder.getCallingUid(), Binder.getCallingPid())) {
        return false;
      }
      if (FingerprintService.-get6(FingerprintService.this) != 0L) {
        bool = true;
      }
      return bool;
    }
    
    public int postEnroll(IBinder paramIBinder)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      return FingerprintService.this.startPostEnroll(paramIBinder);
    }
    
    public long preEnroll(IBinder paramIBinder)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      return FingerprintService.this.startPreEnroll(paramIBinder);
    }
    
    public void remove(final IBinder paramIBinder, final int paramInt1, final int paramInt2, final int paramInt3, final IFingerprintServiceReceiver paramIFingerprintServiceReceiver)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      final boolean bool = isRestricted();
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.this.startRemove(paramIBinder, paramInt1, paramInt2, paramInt3, paramIFingerprintServiceReceiver, bool);
        }
      });
    }
    
    public void rename(final int paramInt1, final int paramInt2, final String paramString)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      if (!FingerprintService.this.isCurrentUserOrProfile(paramInt2)) {
        return;
      }
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.-get5(FingerprintService.this).renameFingerprintForUser(FingerprintService.-get0(FingerprintService.this), paramInt1, paramInt2, paramString);
        }
      });
    }
    
    public void resetTimeout(byte[] paramArrayOfByte)
    {
      FingerprintService.this.checkPermission("android.permission.RESET_FINGERPRINT_LOCKOUT");
      FingerprintService.-get7(FingerprintService.this).post(FingerprintService.-get11(FingerprintService.this));
    }
    
    public void setActiveUser(final int paramInt)
    {
      FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
      FingerprintService.-get7(FingerprintService.this).post(new Runnable()
      {
        public void run()
        {
          FingerprintService.-wrap12(FingerprintService.this, paramInt, null);
        }
      });
    }
    
    public int updateStatus(int paramInt)
    {
      return FingerprintService.this.updateStatus(paramInt);
    }
  }
  
  private class PerformanceStats
  {
    int accept;
    int acquire;
    int lockout;
    int reject;
    
    private PerformanceStats() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/FingerprintService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */