package com.android.server.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.ICameraService;
import android.hardware.ICameraService.Stub;
import android.hardware.ICameraServiceProxy.Stub;
import android.nfc.INfcAdapter;
import android.nfc.INfcAdapter.Stub;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CameraService
  extends SystemService
  implements Handler.Callback, IBinder.DeathRecipient
{
  private static final String CAMERA_SERVICE_BINDER_NAME = "media.camera";
  public static final String CAMERA_SERVICE_PROXY_BINDER_NAME = "media.camera.proxy";
  public static final int CAMERA_STATE_ACTIVE = 1;
  public static final int CAMERA_STATE_CLOSED = 3;
  public static final int CAMERA_STATE_IDLE = 2;
  public static final int CAMERA_STATE_OPEN = 0;
  private static final boolean DEBUG = false;
  public static final int DISABLE_POLLING_FLAGS = 4096;
  public static final int ENABLE_POLLING_FLAGS = 0;
  private static final int MSG_SWITCH_USER = 1;
  private static final String NFC_NOTIFICATION_PROP = "ro.camera.notify_nfc";
  private static final String NFC_SERVICE_BINDER_NAME = "nfc";
  private static final int RETRY_DELAY_TIME = 20;
  private static final String TAG = "CameraService_proxy";
  private static final IBinder nfcInterfaceToken = new Binder();
  private int mActiveCameraCount = 0;
  private final ArraySet<String> mActiveCameraIds = new ArraySet();
  private final ICameraServiceProxy.Stub mCameraServiceProxy = new ICameraServiceProxy.Stub()
  {
    public void notifyCameraState(String paramAnonymousString, int paramAnonymousInt)
    {
      CameraService.-wrap0(paramAnonymousInt);
      CameraService.-wrap3(CameraService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public void pingForUserUpdate()
    {
      CameraService.-wrap1(CameraService.this, 30);
    }
  };
  private ICameraService mCameraServiceRaw;
  private final Context mContext;
  private Set<Integer> mEnabledCameraUsers;
  private final Handler mHandler;
  private final ServiceThread mHandlerThread;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      ??? = paramAnonymousIntent.getAction();
      if (??? == null) {
        return;
      }
      if (???.equals("android.intent.action.USER_ADDED")) {}
      synchronized (CameraService.-get2(CameraService.this))
      {
        do
        {
          paramAnonymousIntent = CameraService.-get0(CameraService.this);
          if (paramAnonymousIntent != null) {
            break;
          }
          return;
        } while ((???.equals("android.intent.action.USER_REMOVED")) || (???.equals("android.intent.action.USER_INFO_CHANGED")) || (???.equals("android.intent.action.MANAGED_PROFILE_ADDED")) || (???.equals("android.intent.action.MANAGED_PROFILE_REMOVED")));
        return;
        CameraService.-wrap2(CameraService.this, CameraService.-get1(CameraService.this));
        return;
      }
    }
  };
  private int mLastUser;
  private final Object mLock = new Object();
  private final boolean mNotifyNfc;
  private UserManager mUserManager;
  
  public CameraService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mHandlerThread = new ServiceThread("CameraService_proxy", -4, false);
    this.mHandlerThread.start();
    this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
    if (SystemProperties.getInt("ro.camera.notify_nfc", 0) > 0) {
      bool = true;
    }
    this.mNotifyNfc = bool;
  }
  
  private static String cameraStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "CAMERA_STATE_UNKNOWN";
    case 0: 
      return "CAMERA_STATE_OPEN";
    case 1: 
      return "CAMERA_STATE_ACTIVE";
    case 2: 
      return "CAMERA_STATE_IDLE";
    }
    return "CAMERA_STATE_CLOSED";
  }
  
  private Set<Integer> getEnabledUserHandles(int paramInt)
  {
    int[] arrayOfInt = this.mUserManager.getEnabledProfileIds(paramInt);
    ArraySet localArraySet = new ArraySet(arrayOfInt.length);
    paramInt = 0;
    int i = arrayOfInt.length;
    while (paramInt < i)
    {
      localArraySet.add(Integer.valueOf(arrayOfInt[paramInt]));
      paramInt += 1;
    }
    return localArraySet;
  }
  
  private boolean notifyMediaserverLocked(int paramInt, Set<Integer> paramSet)
  {
    IBinder localIBinder;
    if (this.mCameraServiceRaw == null)
    {
      localIBinder = getBinderService("media.camera");
      if (localIBinder == null)
      {
        Slog.w("CameraService_proxy", "Could not notify mediaserver, camera service not available.");
        return false;
      }
    }
    try
    {
      localIBinder.linkToDeath(this, 0);
      this.mCameraServiceRaw = ICameraService.Stub.asInterface(localIBinder);
      return false;
    }
    catch (RemoteException paramSet)
    {
      try
      {
        this.mCameraServiceRaw.notifySystemEvent(paramInt, toArray(paramSet));
        return true;
      }
      catch (RemoteException paramSet)
      {
        Slog.w("CameraService_proxy", "Could not notify mediaserver, remote exception: " + paramSet);
      }
      paramSet = paramSet;
      Slog.w("CameraService_proxy", "Could not link to death of native camera service");
      return false;
    }
  }
  
  private void notifyNfcService(boolean paramBoolean)
  {
    Object localObject = getBinderService("nfc");
    if (localObject == null)
    {
      Slog.w("CameraService_proxy", "Could not connect to NFC service to notify it of camera state");
      return;
    }
    localObject = INfcAdapter.Stub.asInterface((IBinder)localObject);
    if (paramBoolean) {}
    for (int i = 0;; i = 4096) {
      try
      {
        ((INfcAdapter)localObject).setReaderMode(nfcInterfaceToken, null, i, null);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("CameraService_proxy", "Could not notify NFC service, remote exception: " + localRemoteException);
      }
    }
  }
  
  private void notifySwitchWithRetries(int paramInt)
  {
    synchronized (this.mLock)
    {
      Set localSet = this.mEnabledCameraUsers;
      if (localSet == null) {
        return;
      }
      boolean bool = notifyMediaserverLocked(1, this.mEnabledCameraUsers);
      if (bool) {
        paramInt = 0;
      }
      if (paramInt <= 0) {
        return;
      }
    }
    Slog.i("CameraService_proxy", "Could not notify camera service of user switch, retrying...");
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, paramInt - 1, 0, null), 20L);
  }
  
  private void switchUserLocked(int paramInt)
  {
    Set localSet = getEnabledUserHandles(paramInt);
    this.mLastUser = paramInt;
    if ((this.mEnabledCameraUsers != null) && (this.mEnabledCameraUsers.equals(localSet))) {
      return;
    }
    this.mEnabledCameraUsers = localSet;
    notifyMediaserverLocked(1, localSet);
  }
  
  private static int[] toArray(Collection<Integer> paramCollection)
  {
    int[] arrayOfInt = new int[paramCollection.size()];
    int i = 0;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      arrayOfInt[i] = ((Integer)paramCollection.next()).intValue();
      i += 1;
    }
    return arrayOfInt;
  }
  
  private void updateActivityCount(String paramString, int paramInt)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        boolean bool1 = this.mActiveCameraIds.isEmpty();
        switch (paramInt)
        {
        case 0: 
          boolean bool2 = this.mActiveCameraIds.isEmpty();
          if ((this.mNotifyNfc) && (bool1 != bool2)) {
            notifyNfcService(bool2);
          }
          return;
        case 1: 
          this.mActiveCameraIds.add(paramString);
        }
      }
      this.mActiveCameraIds.remove(paramString);
    }
  }
  
  public void binderDied()
  {
    synchronized (this.mLock)
    {
      this.mCameraServiceRaw = null;
      boolean bool1 = this.mActiveCameraIds.isEmpty();
      this.mActiveCameraIds.clear();
      boolean bool2 = this.mNotifyNfc;
      if ((!bool2) || (bool1)) {
        return;
      }
      notifyNfcService(true);
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      Slog.e("CameraService_proxy", "CameraService error, invalid message: " + paramMessage.what);
    }
    for (;;)
    {
      return true;
      notifySwitchWithRetries(paramMessage.arg1);
    }
  }
  
  public void onStart()
  {
    this.mUserManager = UserManager.get(this.mContext);
    if (this.mUserManager == null) {
      throw new IllegalStateException("UserManagerService must start before CameraService!");
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_ADDED");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    localIntentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
    this.mContext.registerReceiver(this.mIntentReceiver, localIntentFilter);
    publishBinderService("media.camera.proxy", this.mCameraServiceProxy);
  }
  
  public void onStartUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mEnabledCameraUsers == null) {
        switchUserLocked(paramInt);
      }
      return;
    }
  }
  
  public void onSwitchUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      switchUserLocked(paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/camera/CameraService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */