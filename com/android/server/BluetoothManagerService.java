package com.android.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothCallback;
import android.bluetooth.IBluetoothCallback.Stub;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothHeadset;
import android.bluetooth.IBluetoothManager.Stub;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothProfileServiceConnection;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

class BluetoothManagerService
  extends IBluetoothManager.Stub
{
  private static final String ACTION_SERVICE_STATE_CHANGED = "com.android.bluetooth.btservice.action.STATE_CHANGED";
  private static final int ADD_PROXY_DELAY_MS = 100;
  private static final String BLUETOOTH_ADMIN_PERM = "android.permission.BLUETOOTH_ADMIN";
  private static final int BLUETOOTH_OFF = 0;
  private static final int BLUETOOTH_ON_AIRPLANE = 2;
  private static final int BLUETOOTH_ON_BLUETOOTH = 1;
  private static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";
  private static final int CHECK_SDCARD_DELAY_MS = 1000;
  private static final boolean DBG = true;
  private static final int ERROR_RESTART_TIME_MS = 3000;
  private static final String EXTRA_ACTION = "action";
  private static final int MAX_CHECK_SDCARD_RETRY = 5;
  private static final int MAX_ERROR_RESTART_RETRIES = 6;
  private static final int MAX_SAVE_RETRIES = 3;
  private static final int MESSAGE_ADD_PROXY_DELAYED = 400;
  private static final int MESSAGE_BIND_PROFILE_SERVICE = 401;
  private static final int MESSAGE_BLUETOOTH_SERVICE_CONNECTED = 40;
  private static final int MESSAGE_BLUETOOTH_SERVICE_DISCONNECTED = 41;
  private static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 60;
  private static final int MESSAGE_CHECK_SDCARD = 50;
  private static final int MESSAGE_DISABLE = 2;
  private static final int MESSAGE_ENABLE = 1;
  private static final int MESSAGE_GET_NAME_AND_ADDRESS = 200;
  private static final int MESSAGE_REGISTER_ADAPTER = 20;
  private static final int MESSAGE_REGISTER_STATE_CHANGE_CALLBACK = 30;
  private static final int MESSAGE_RESTART_BLUETOOTH_SERVICE = 42;
  private static final int MESSAGE_TIMEOUT_BIND = 100;
  private static final int MESSAGE_TIMEOUT_UNBIND = 101;
  private static final int MESSAGE_UNREGISTER_ADAPTER = 21;
  private static final int MESSAGE_UNREGISTER_STATE_CHANGE_CALLBACK = 31;
  private static final int MESSAGE_USER_SWITCHED = 300;
  private static final int MESSAGE_USER_UNLOCKED = 301;
  private static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
  private static final String SECURE_SETTINGS_BLUETOOTH_ADDR_VALID = "bluetooth_addr_valid";
  private static final String SECURE_SETTINGS_BLUETOOTH_NAME = "bluetooth_name";
  private static final int SERVICE_IBLUETOOTH = 1;
  private static final int SERVICE_IBLUETOOTHGATT = 2;
  private static final int SERVICE_RESTART_TIME_MS = 200;
  private static final String TAG = "BluetoothManagerService";
  private static final int TIMEOUT_BIND_MS = 3000;
  private static final int TIMEOUT_SAVE_MS = 500;
  private static final int USER_SWITCHED_TIME_MS = 200;
  private static int mBleAppCount = 0;
  private static int mCheckSdcardRetryCount = 0;
  private String mAddress;
  private boolean mBinding;
  Map<IBinder, ClientDeathRecipient> mBleApps = new HashMap();
  private IBluetooth mBluetooth;
  private IBinder mBluetoothBinder;
  private final IBluetoothCallback mBluetoothCallback = new IBluetoothCallback.Stub()
  {
    public void onBluetoothStateChange(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {
      Message localMessage = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(60, paramAnonymousInt1, paramAnonymousInt2);
      BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessage(localMessage);
    }
  };
  private IBluetoothGatt mBluetoothGatt;
  private final ReentrantReadWriteLock mBluetoothLock = new ReentrantReadWriteLock();
  private final RemoteCallbackList<IBluetoothManagerCallback> mCallbacks;
  private BluetoothServiceConnection mConnection = new BluetoothServiceConnection(null);
  private final ContentResolver mContentResolver;
  private final Context mContext;
  private boolean mEnable;
  private boolean mEnableExternal;
  private int mErrorRecoveryRetryCounter;
  private final BluetoothHandler mHandler = new BluetoothHandler(IoThread.get().getLooper());
  private boolean mIntentPending = false;
  private String mName;
  private final Map<Integer, ProfileServiceConnections> mProfileServices = new HashMap();
  private boolean mQuietEnable = false;
  private boolean mQuietEnableExternal;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent arg2)
    {
      String str = ???.getAction();
      paramAnonymousContext = Boolean.valueOf(false);
      if ("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED".equals(str))
      {
        paramAnonymousContext = ???.getStringExtra("android.bluetooth.adapter.extra.LOCAL_NAME");
        Slog.d("BluetoothManagerService", "Bluetooth Adapter name changed to " + paramAnonymousContext);
        if (paramAnonymousContext != null) {
          BluetoothManagerService.-wrap19(BluetoothManagerService.this, paramAnonymousContext, null);
        }
      }
      while (!"android.intent.action.AIRPLANE_MODE".equals(str)) {
        return;
      }
      for (;;)
      {
        int j;
        synchronized (BluetoothManagerService.-get16(BluetoothManagerService.this))
        {
          if (BluetoothManagerService.-wrap1(BluetoothManagerService.this))
          {
            if (BluetoothManagerService.-wrap0(BluetoothManagerService.this)) {
              BluetoothManagerService.-wrap11(BluetoothManagerService.this, 2);
            }
          }
          else {
            j = 10;
          }
          try
          {
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().lock();
            i = j;
            if (BluetoothManagerService.-get2(BluetoothManagerService.this) != null) {
              i = BluetoothManagerService.-get2(BluetoothManagerService.this).getState();
            }
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("BluetoothManagerService", "Unable to call getState", localRemoteException);
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
            int i = j;
            continue;
          }
          finally
          {
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
          }
          Slog.d("BluetoothManagerService", "Airplane Mode change - current state: " + i);
          if (!BluetoothManagerService.-wrap0(BluetoothManagerService.this)) {
            break label456;
          }
          if (BluetoothManagerService.-get1() != 0) {
            paramAnonymousContext = Boolean.valueOf(true);
          }
          try
          {
            BluetoothManagerService.-set1(0);
            BluetoothManagerService.this.mBleApps.clear();
            if ((i != 15) || (!paramAnonymousContext.booleanValue())) {
              break label438;
            }
            Slog.d("BluetoothManagerService", "BLE is using before airplane on");
          }
          finally {}
          try
          {
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().lock();
            if (BluetoothManagerService.-get2(BluetoothManagerService.this) != null)
            {
              BluetoothManagerService.-get2(BluetoothManagerService.this).onBrEdrDown();
              BluetoothManagerService.-set5(BluetoothManagerService.this, false);
              BluetoothManagerService.-set6(BluetoothManagerService.this, false);
            }
          }
          catch (RemoteException paramAnonymousContext)
          {
            Slog.e("BluetoothManagerService", "Unable to call onBrEdrDown", paramAnonymousContext);
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
            continue;
          }
          finally
          {
            BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
          }
          return;
          BluetoothManagerService.-wrap11(BluetoothManagerService.this, 1);
        }
        label438:
        Slog.d("BluetoothManagerService", "Calling disable");
        BluetoothManagerService.-wrap16(BluetoothManagerService.this);
        continue;
        label456:
        if (BluetoothManagerService.-get10(BluetoothManagerService.this))
        {
          Slog.d("BluetoothManagerService", "Calling enable");
          BluetoothManagerService.-wrap17(BluetoothManagerService.this, BluetoothManagerService.-get15(BluetoothManagerService.this));
        }
      }
    }
  };
  private int mState;
  private final RemoteCallbackList<IBluetoothStateChangeCallback> mStateChangeCallbacks;
  private final int mSystemUiUid;
  private boolean mUnbinding;
  
  BluetoothManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mBluetooth = null;
    this.mBluetoothBinder = null;
    this.mBluetoothGatt = null;
    this.mBinding = false;
    this.mUnbinding = false;
    this.mEnable = false;
    this.mState = 10;
    this.mQuietEnableExternal = false;
    this.mEnableExternal = false;
    this.mAddress = null;
    this.mName = null;
    this.mErrorRecoveryRetryCounter = 0;
    this.mContentResolver = paramContext.getContentResolver();
    registerForBleScanModeChange();
    this.mCallbacks = new RemoteCallbackList();
    this.mStateChangeCallbacks = new RemoteCallbackList();
    paramContext = new IntentFilter("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
    registerForAirplaneMode(paramContext);
    paramContext.setPriority(1000);
    this.mContext.registerReceiver(this.mReceiver, paramContext);
    loadStoredNameAndAddress();
    if (isBluetoothPersistedStateOn())
    {
      Slog.d("BluetoothManagerService", "Startup: Bluetooth persisted state is ON.");
      this.mEnableExternal = true;
    }
    int i = -1;
    try
    {
      int j = this.mContext.getPackageManager().getPackageUidAsUser("com.android.systemui", 1048576, 0);
      i = j;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        Slog.w("BluetoothManagerService", "Unable to resolve SystemUI's UID.", paramContext);
      }
    }
    this.mSystemUiUid = i;
  }
  
  private void bluetoothStateChangeHandler(int paramInt1, int paramInt2)
  {
    boolean bool = true;
    int m = 1;
    Slog.d("BluetoothManagerService", "bluetoothStateChangeHandler: " + paramInt1 + " ->  " + paramInt2);
    int j;
    int i;
    int k;
    if (paramInt1 != paramInt2)
    {
      if ((paramInt2 != 15) && (paramInt2 != 10)) {
        break label389;
      }
      if (paramInt1 != 13) {
        break label200;
      }
      if (paramInt2 != 15) {
        break label194;
      }
      j = 1;
      if (paramInt2 != 10) {
        break label220;
      }
      Slog.d("BluetoothManagerService", "Bluetooth is complete turn off");
      sendBluetoothServiceDownCallback();
      unbindAndFinish();
      sendBleStateChanged(paramInt1, paramInt2);
      if (this.mIntentPending) {
        break label206;
      }
      i = 0;
      k = paramInt2;
      label115:
      if (i != 0)
      {
        if (paramInt1 != 15) {
          break label487;
        }
        paramInt2 = 10;
      }
    }
    for (;;)
    {
      Intent localIntent = new Intent("android.bluetooth.adapter.action.STATE_CHANGED");
      localIntent.putExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", paramInt2);
      localIntent.putExtra("android.bluetooth.adapter.extra.STATE", k);
      localIntent.addFlags(67108864);
      localIntent.addFlags(268435456);
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.BLUETOOTH");
      return;
      label194:
      j = 0;
      break;
      label200:
      j = 0;
      break;
      label206:
      this.mIntentPending = false;
      i = m;
      k = paramInt2;
      break label115;
      label220:
      if (j == 0)
      {
        Slog.d("BluetoothManagerService", "Bluetooth is in LE only mode");
        if (this.mBluetoothGatt != null)
        {
          Slog.d("BluetoothManagerService", "Calling BluetoothGattServiceUp");
          onBluetoothGattServiceUp();
        }
        for (;;)
        {
          sendBleStateChanged(paramInt1, paramInt2);
          i = 0;
          k = paramInt2;
          break;
          Slog.d("BluetoothManagerService", "Binding Bluetooth GATT service");
          if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            doBind(new Intent(IBluetoothGatt.class.getName()), this.mConnection, 65, UserHandle.CURRENT);
          }
        }
      }
      i = m;
      k = paramInt2;
      if (j == 0) {
        break label115;
      }
      Slog.d("BluetoothManagerService", "Intermediate off, back to LE only mode");
      sendBleStateChanged(paramInt1, paramInt2);
      sendBluetoothStateCallback(false);
      k = 10;
      sendBrEdrDownCallback();
      if (!isBleAppPresent())
      {
        i = 0;
        this.mIntentPending = true;
        break label115;
      }
      this.mIntentPending = false;
      i = 1;
      break label115;
      label389:
      if (paramInt2 == 12)
      {
        if (paramInt2 == 12) {}
        for (;;)
        {
          sendBluetoothStateCallback(bool);
          sendBleStateChanged(paramInt1, paramInt2);
          i = m;
          k = paramInt2;
          break;
          bool = false;
        }
      }
      if ((paramInt2 == 14) || (paramInt2 == 16))
      {
        sendBleStateChanged(paramInt1, paramInt2);
        i = 0;
        k = paramInt2;
        break label115;
      }
      if (paramInt2 != 11)
      {
        i = m;
        k = paramInt2;
        if (paramInt2 != 13) {
          break label115;
        }
      }
      sendBleStateChanged(paramInt1, paramInt2);
      i = m;
      k = paramInt2;
      break label115;
      label487:
      paramInt2 = paramInt1;
      if (paramInt1 == 16) {
        paramInt2 = 13;
      }
    }
  }
  
  private boolean checkIfCallerIsForegroundUser()
  {
    int j = UserHandle.getCallingUserId();
    int k = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    UserInfo localUserInfo = ((UserManager)this.mContext.getSystemService("user")).getProfileParent(j);
    int i;
    if (localUserInfo != null)
    {
      i = localUserInfo.id;
      k = UserHandle.getAppId(k);
    }
    label182:
    for (;;)
    {
      try
      {
        int m = ActivityManager.getCurrentUser();
        if (j != m)
        {
          if (i == m)
          {
            break label182;
            Slog.d("BluetoothManagerService", "checkIfCallerIsForegroundUser: valid=" + bool + " callingUser=" + j + " parentUser=" + i + " foregroundUser=" + m);
            return bool;
            i = 55536;
            break;
          }
          if (k != 1027)
          {
            int n = this.mSystemUiUid;
            if (k == n)
            {
              bool = true;
              continue;
            }
            bool = false;
            continue;
          }
        }
        boolean bool = true;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private void clearBleApps()
  {
    try
    {
      this.mBleApps.clear();
      mBleAppCount = 0;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void clearCheckSdcardCount()
  {
    mCheckSdcardRetryCount = 0;
  }
  
  private void disableBleScanMode()
  {
    try
    {
      this.mBluetoothLock.writeLock().lock();
      if ((this.mBluetooth != null) && (this.mBluetooth.getState() != 12))
      {
        Slog.d("BluetoothManagerService", "Reseting the mEnable flag for clean disable");
        this.mEnable = false;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("BluetoothManagerService", "getState()", localRemoteException);
      return;
    }
    finally
    {
      this.mBluetoothLock.writeLock().unlock();
    }
  }
  
  private void handleDisable()
  {
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null)
      {
        Slog.d("BluetoothManagerService", "Sending off request.");
        if (!this.mBluetooth.disable()) {
          Slog.e("BluetoothManagerService", "IBluetooth.disable() returned false");
        }
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("BluetoothManagerService", "Unable to call disable()", localRemoteException);
      return;
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
  }
  
  /* Error */
  private void handleEnable(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: putfield 179	com/android/server/BluetoothManagerService:mQuietEnable	Z
    //   5: aload_0
    //   6: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   9: invokevirtual 608	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   12: invokevirtual 613	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
    //   15: aload_0
    //   16: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   19: ifnonnull +10 -> 29
    //   22: aload_0
    //   23: getfield 156	com/android/server/BluetoothManagerService:mBinding	Z
    //   26: ifeq +51 -> 77
    //   29: aload_0
    //   30: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   33: astore_2
    //   34: aload_2
    //   35: ifnull +31 -> 66
    //   38: aload_0
    //   39: getfield 179	com/android/server/BluetoothManagerService:mQuietEnable	Z
    //   42: ifne +119 -> 161
    //   45: aload_0
    //   46: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   49: invokeinterface 650 1 0
    //   54: ifne +12 -> 66
    //   57: ldc 104
    //   59: ldc_w 652
    //   62: invokestatic 644	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   65: pop
    //   66: aload_0
    //   67: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   70: invokevirtual 608	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   73: invokevirtual 623	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
    //   76: return
    //   77: aload_0
    //   78: getfield 172	com/android/server/BluetoothManagerService:mHandler	Lcom/android/server/BluetoothManagerService$BluetoothHandler;
    //   81: bipush 100
    //   83: invokevirtual 656	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
    //   86: astore_2
    //   87: aload_0
    //   88: getfield 172	com/android/server/BluetoothManagerService:mHandler	Lcom/android/server/BluetoothManagerService$BluetoothHandler;
    //   91: aload_2
    //   92: ldc2_w 657
    //   95: invokevirtual 662	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
    //   98: pop
    //   99: aload_0
    //   100: new 479	android/content/Intent
    //   103: dup
    //   104: ldc_w 615
    //   107: invokevirtual 525	java/lang/Class:getName	()Ljava/lang/String;
    //   110: invokespecial 482	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   113: aload_0
    //   114: getfield 212	com/android/server/BluetoothManagerService:mConnection	Lcom/android/server/BluetoothManagerService$BluetoothServiceConnection;
    //   117: bipush 65
    //   119: getstatic 528	android/os/UserHandle:CURRENT	Landroid/os/UserHandle;
    //   122: invokevirtual 532	com/android/server/BluetoothManagerService:doBind	(Landroid/content/Intent;Landroid/content/ServiceConnection;ILandroid/os/UserHandle;)Z
    //   125: ifne +28 -> 153
    //   128: aload_0
    //   129: getfield 172	com/android/server/BluetoothManagerService:mHandler	Lcom/android/server/BluetoothManagerService$BluetoothHandler;
    //   132: bipush 100
    //   134: invokevirtual 665	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
    //   137: goto -71 -> 66
    //   140: astore_2
    //   141: aload_0
    //   142: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   145: invokevirtual 608	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   148: invokevirtual 623	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
    //   151: aload_2
    //   152: athrow
    //   153: aload_0
    //   154: iconst_1
    //   155: putfield 156	com/android/server/BluetoothManagerService:mBinding	Z
    //   158: goto -92 -> 66
    //   161: aload_0
    //   162: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   165: invokeinterface 668 1 0
    //   170: ifne -104 -> 66
    //   173: ldc 104
    //   175: ldc_w 670
    //   178: invokestatic 644	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   181: pop
    //   182: goto -116 -> 66
    //   185: astore_2
    //   186: ldc 104
    //   188: ldc_w 672
    //   191: aload_2
    //   192: invokestatic 628	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   195: pop
    //   196: goto -130 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	this	BluetoothManagerService
    //   0	199	1	paramBoolean	boolean
    //   33	59	2	localObject1	Object
    //   140	12	2	localObject2	Object
    //   185	7	2	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   5	29	140	finally
    //   29	34	140	finally
    //   38	66	140	finally
    //   77	137	140	finally
    //   153	158	140	finally
    //   161	182	140	finally
    //   186	196	140	finally
    //   38	66	185	android/os/RemoteException
    //   161	182	185	android/os/RemoteException
  }
  
  private final boolean isAirplaneModeOn()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
  }
  
  private final boolean isBluetoothPersistedStateOn()
  {
    boolean bool = false;
    int i = Settings.Global.getInt(this.mContentResolver, "bluetooth_on", -1);
    Slog.d("BluetoothManagerService", "Bluetooth persisted state: " + i);
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  private final boolean isBluetoothPersistedStateOnBluetooth()
  {
    return Settings.Global.getInt(this.mContentResolver, "bluetooth_on", 1) == 1;
  }
  
  private boolean isNameAndAddressSet()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mName != null)
    {
      bool1 = bool2;
      if (this.mAddress != null)
      {
        bool1 = bool2;
        if (this.mName.length() > 0)
        {
          bool1 = bool2;
          if (this.mAddress.length() > 0) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  private boolean isStrictOpEnable()
  {
    return SystemProperties.getBoolean("persist.sys.strict_op_enable", false);
  }
  
  private void loadStoredNameAndAddress()
  {
    Slog.d("BluetoothManagerService", "Loading stored name and address");
    if ((this.mContext.getResources().getBoolean(17956954)) && (Settings.Secure.getInt(this.mContentResolver, "bluetooth_addr_valid", 0) == 0))
    {
      Slog.d("BluetoothManagerService", "invalid bluetooth name and address stored");
      return;
    }
    this.mName = Settings.Secure.getString(this.mContentResolver, "bluetooth_name");
    this.mAddress = Settings.Secure.getString(this.mContentResolver, "bluetooth_address");
    Slog.d("BluetoothManagerService", "Stored bluetooth Name=" + this.mName + ",Address=" + this.mAddress);
  }
  
  private void onBluetoothGattServiceUp()
  {
    Slog.d("BluetoothManagerService", "BluetoothGatt Service is Up");
    try
    {
      this.mBluetoothLock.readLock().lock();
      if ((!isBleAppPresent()) && (this.mBluetooth != null) && (this.mBluetooth.getState() == 15))
      {
        this.mBluetooth.onLeServiceUp();
        long l = Binder.clearCallingIdentity();
        persistBluetoothSetting(1);
        Binder.restoreCallingIdentity(l);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("BluetoothManagerService", "Unable to call onServiceUp", localRemoteException);
      return;
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
  }
  
  private void persistBluetoothSetting(int paramInt)
  {
    Slog.d("BluetoothManagerService", "Persisting Bluetooth Setting: " + paramInt);
    Settings.Global.putInt(this.mContext.getContentResolver(), "bluetooth_on", paramInt);
  }
  
  private void recoverBluetoothServiceFromError()
  {
    Slog.e("BluetoothManagerService", "recoverBluetoothServiceFromError");
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null) {
        this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        int i;
        Message localMessage;
        Slog.e("BluetoothManagerService", "Unable to unregister", localRemoteException);
        this.mBluetoothLock.readLock().unlock();
      }
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
    waitForMonitoredOnOff(false, true);
    sendBluetoothServiceDownCallback();
    this.mHandler.removeMessages(60);
    this.mState = 10;
    this.mEnable = false;
    i = this.mErrorRecoveryRetryCounter;
    this.mErrorRecoveryRetryCounter = (i + 1);
    if (i < 6)
    {
      localMessage = this.mHandler.obtainMessage(42);
      this.mHandler.sendMessageDelayed(localMessage, 3000L);
    }
  }
  
  private void registerForAirplaneMode(IntentFilter paramIntentFilter)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    String str = Settings.Global.getString(localContentResolver, "airplane_mode_radios");
    Settings.Global.getString(localContentResolver, "airplane_mode_toggleable_radios");
    if (str == null) {}
    for (boolean bool = true;; bool = str.contains("bluetooth"))
    {
      if (bool) {
        paramIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
      }
      return;
    }
  }
  
  private void registerForBleScanModeChange()
  {
    ContentObserver local3 = new ContentObserver(null)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        if (!BluetoothManagerService.this.isBleScanAlwaysAvailable())
        {
          BluetoothManagerService.-wrap7(BluetoothManagerService.this);
          BluetoothManagerService.-wrap6(BluetoothManagerService.this);
        }
        try
        {
          BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().lock();
          if (BluetoothManagerService.-get2(BluetoothManagerService.this) != null) {
            BluetoothManagerService.-get2(BluetoothManagerService.this).onBrEdrDown();
          }
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Slog.e("BluetoothManagerService", "error when disabling bluetooth", localRemoteException);
          return;
        }
        finally
        {
          BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
        }
      }
    };
    this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("ble_scan_always_enabled"), false, local3);
  }
  
  private void sendBleStateChanged(int paramInt1, int paramInt2)
  {
    Slog.d("BluetoothManagerService", "BLE State Change Intent: " + paramInt1 + " -> " + paramInt2);
    Intent localIntent = new Intent("android.bluetooth.adapter.action.BLE_STATE_CHANGED");
    localIntent.putExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", paramInt1);
    localIntent.putExtra("android.bluetooth.adapter.extra.STATE", paramInt2);
    localIntent.addFlags(67108864);
    localIntent.setFlags(268435456);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.BLUETOOTH");
  }
  
  private void sendBluetoothServiceDownCallback()
  {
    Slog.d("BluetoothManagerService", "Calling onBluetoothServiceDown callbacks");
    try
    {
      int j = this.mCallbacks.beginBroadcast();
      Slog.d("BluetoothManagerService", "Broadcasting onBluetoothServiceDown() to " + j + " receivers.");
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((IBluetoothManagerCallback)this.mCallbacks.getBroadcastItem(i)).onBluetoothServiceDown();
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.e("BluetoothManagerService", "Unable to call onBluetoothServiceDown() on callback #" + i, localRemoteException);
            }
          }
        }
      }
    }
    finally
    {
      this.mCallbacks.finishBroadcast();
    }
  }
  
  private void sendBluetoothServiceUpCallback()
  {
    Slog.d("BluetoothManagerService", "Calling onBluetoothServiceUp callbacks");
    try
    {
      int j = this.mCallbacks.beginBroadcast();
      Slog.d("BluetoothManagerService", "Broadcasting onBluetoothServiceUp() to " + j + " receivers.");
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((IBluetoothManagerCallback)this.mCallbacks.getBroadcastItem(i)).onBluetoothServiceUp(this.mBluetooth);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.e("BluetoothManagerService", "Unable to call onBluetoothServiceUp() on callback #" + i, localRemoteException);
            }
          }
        }
      }
    }
    finally
    {
      this.mCallbacks.finishBroadcast();
    }
  }
  
  private void sendBluetoothStateCallback(boolean paramBoolean)
  {
    try
    {
      int j = this.mStateChangeCallbacks.beginBroadcast();
      Slog.d("BluetoothManagerService", "Broadcasting onBluetoothStateChange(" + paramBoolean + ") to " + j + " receivers.");
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((IBluetoothStateChangeCallback)this.mStateChangeCallbacks.getBroadcastItem(i)).onBluetoothStateChange(paramBoolean);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.e("BluetoothManagerService", "Unable to call onBluetoothStateChange() on callback #" + i, localRemoteException);
            }
          }
        }
      }
    }
    finally
    {
      this.mStateChangeCallbacks.finishBroadcast();
    }
  }
  
  private void sendBrEdrDownCallback()
  {
    Slog.d("BluetoothManagerService", "Calling sendBrEdrDownCallback callbacks");
    if (this.mBluetooth == null)
    {
      Slog.w("BluetoothManagerService", "Bluetooth handle is null");
      return;
    }
    if (!isBleAppPresent()) {
      try
      {
        this.mBluetoothLock.readLock().lock();
        if (this.mBluetooth != null) {
          this.mBluetooth.onBrEdrDown();
        }
        return;
      }
      catch (RemoteException localRemoteException1)
      {
        Slog.e("BluetoothManagerService", "Call to onBrEdrDown() failed.", localRemoteException1);
        return;
      }
      finally
      {
        this.mBluetoothLock.readLock().unlock();
      }
    }
    try
    {
      this.mBluetoothGatt.unregAll();
      return;
    }
    catch (RemoteException localRemoteException2)
    {
      Slog.e("BluetoothManagerService", "Unable to disconnect all apps.", localRemoteException2);
    }
  }
  
  private void sendCheckSDCardMsg(int paramInt)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(50, Integer.valueOf(paramInt)));
  }
  
  private void sendCheckSDCardMsgDelay(int paramInt)
  {
    Message localMessage = this.mHandler.obtainMessage(50);
    localMessage.arg1 = paramInt;
    this.mHandler.sendMessageDelayed(localMessage, 1000L);
  }
  
  private void sendDisableMsg()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
  }
  
  private void sendEnableMsg(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      sendCheckSDCardMsg(i);
      return;
    }
  }
  
  private void sendTrueEnableMsg(int paramInt)
  {
    terminateCheckSdcardCount();
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1, Integer.valueOf(paramInt)));
  }
  
  private boolean shouldContinueCheckSDcard()
  {
    Slog.e("BluetoothManagerService", "CheckSdcardRetryCount = " + mCheckSdcardRetryCount);
    if (mCheckSdcardRetryCount >= 5)
    {
      clearCheckSdcardCount();
      return false;
    }
    mCheckSdcardRetryCount += 1;
    return true;
  }
  
  private void storeNameAndAddress(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      Settings.Secure.putString(this.mContentResolver, "bluetooth_name", paramString1);
      this.mName = paramString1;
      Slog.d("BluetoothManagerService", "Stored Bluetooth name: " + Settings.Secure.getString(this.mContentResolver, "bluetooth_name"));
    }
    if (paramString2 != null)
    {
      Settings.Secure.putString(this.mContentResolver, "bluetooth_address", paramString2);
      this.mAddress = paramString2;
      Slog.d("BluetoothManagerService", "Stored Bluetoothaddress: " + Settings.Secure.getString(this.mContentResolver, "bluetooth_address"));
    }
    if ((paramString1 != null) && (paramString2 != null)) {
      Settings.Secure.putInt(this.mContentResolver, "bluetooth_addr_valid", 1);
    }
  }
  
  private void terminateCheckSdcardCount()
  {
    clearCheckSdcardCount();
    this.mHandler.removeMessages(50);
  }
  
  private void unbindAllBluetoothProfileServices()
  {
    for (;;)
    {
      Object localObject2;
      synchronized (this.mProfileServices)
      {
        Iterator localIterator = this.mProfileServices.keySet().iterator();
        if (!localIterator.hasNext()) {
          break label111;
        }
        localObject2 = (Integer)localIterator.next();
        localObject2 = (ProfileServiceConnections)this.mProfileServices.get(localObject2);
      }
      try
      {
        this.mContext.unbindService((ServiceConnection)localObject2);
        ProfileServiceConnections.-wrap2((ProfileServiceConnections)localObject2);
        continue;
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          Slog.e("BluetoothManagerService", "Unable to unbind service with intent: " + ((ProfileServiceConnections)localObject2).mIntent, localIllegalArgumentException);
        }
      }
    }
    label111:
    this.mProfileServices.clear();
  }
  
  private boolean waitForMonitoredOnOff(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (i < 10) {}
    synchronized (this.mConnection)
    {
      try
      {
        IBluetooth localIBluetooth = this.mBluetooth;
        if (localIBluetooth != null) {
          break label42;
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          label42:
          int j;
          Slog.e("BluetoothManagerService", "getState()", localRemoteException);
        }
      }
      Slog.e("BluetoothManagerService", "waitForMonitoredOnOff time out");
      return false;
      if (paramBoolean1)
      {
        j = this.mBluetooth.getState();
        if (j == 12) {
          return true;
        }
        if (this.mBluetooth.getState() == 15)
        {
          bluetoothStateChangeHandler(14, 15);
          paramBoolean1 = waitForOnOff(paramBoolean1, paramBoolean2);
          return paramBoolean1;
        }
      }
      else if (paramBoolean2)
      {
        j = this.mBluetooth.getState();
        if (j == 10) {
          return true;
        }
        if (this.mBluetooth.getState() == 15)
        {
          bluetoothStateChangeHandler(13, 15);
          paramBoolean1 = waitForOnOff(paramBoolean1, paramBoolean2);
          return paramBoolean1;
        }
      }
      else
      {
        j = this.mBluetooth.getState();
        if (j != 12) {
          return true;
        }
      }
    }
    if ((paramBoolean1) || (paramBoolean2)) {
      SystemClock.sleep(300L);
    }
    for (;;)
    {
      i += 1;
      break;
      SystemClock.sleep(50L);
    }
  }
  
  private boolean waitForOnOff(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (i < 16) {}
    try
    {
      this.mBluetoothLock.readLock().lock();
      IBluetooth localIBluetooth = this.mBluetooth;
      if (localIBluetooth != null) {
        break label50;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        int j;
        Slog.e("BluetoothManagerService", "getState()", localRemoteException);
        this.mBluetoothLock.readLock().unlock();
      }
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
    Slog.e("BluetoothManagerService", "waitForOnOff time out");
    return false;
    label50:
    if (paramBoolean1)
    {
      j = this.mBluetooth.getState();
      if (j == 12)
      {
        this.mBluetoothLock.readLock().unlock();
        return true;
      }
    }
    else if (paramBoolean2)
    {
      j = this.mBluetooth.getState();
      if (j == 10)
      {
        this.mBluetoothLock.readLock().unlock();
        return true;
      }
    }
    else
    {
      j = this.mBluetooth.getState();
      if (j != 12)
      {
        this.mBluetoothLock.readLock().unlock();
        return true;
      }
    }
    this.mBluetoothLock.readLock().unlock();
    if ((paramBoolean1) || (paramBoolean2)) {
      SystemClock.sleep(500L);
    }
    for (;;)
    {
      i += 1;
      break;
      SystemClock.sleep(30L);
    }
  }
  
  public boolean bindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
  {
    if (!this.mEnable)
    {
      Slog.d("BluetoothManagerService", "Trying to bind to profile: " + paramInt + ", while Bluetooth was disabled");
      return false;
    }
    synchronized (this.mProfileServices)
    {
      if ((ProfileServiceConnections)this.mProfileServices.get(new Integer(paramInt)) == null)
      {
        Slog.d("BluetoothManagerService", "Creating new ProfileServiceConnections object for profile: " + paramInt);
        if (paramInt != 1) {
          return false;
        }
        ProfileServiceConnections localProfileServiceConnections = new ProfileServiceConnections(new Intent(IBluetoothHeadset.class.getName()));
        boolean bool = ProfileServiceConnections.-wrap0(localProfileServiceConnections);
        if (!bool) {
          return false;
        }
        this.mProfileServices.put(new Integer(paramInt), localProfileServiceConnections);
      }
      ??? = this.mHandler.obtainMessage(400);
      ((Message)???).arg1 = paramInt;
      ((Message)???).obj = paramIBluetoothProfileServiceConnection;
      this.mHandler.sendMessageDelayed((Message)???, 100L);
      return true;
    }
  }
  
  /* Error */
  public boolean disable(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 220	com/android/server/BluetoothManagerService:mContext	Landroid/content/Context;
    //   4: ldc 32
    //   6: ldc_w 971
    //   9: invokevirtual 974	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: invokestatic 552	android/os/Binder:getCallingUid	()I
    //   15: sipush 1000
    //   18: if_icmpeq +10 -> 28
    //   21: aload_0
    //   22: invokespecial 976	com/android/server/BluetoothManagerService:checkIfCallerIsForegroundUser	()Z
    //   25: ifeq +85 -> 110
    //   28: ldc 104
    //   30: new 453	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 454	java/lang/StringBuilder:<init>	()V
    //   37: ldc_w 978
    //   40: invokevirtual 460	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: aload_0
    //   44: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   47: invokevirtual 920	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   50: ldc_w 980
    //   53: invokevirtual 460	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: aload_0
    //   57: getfield 156	com/android/server/BluetoothManagerService:mBinding	Z
    //   60: invokevirtual 586	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   63: invokevirtual 469	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokestatic 430	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   69: pop
    //   70: aload_0
    //   71: getfield 186	com/android/server/BluetoothManagerService:mReceiver	Landroid/content/BroadcastReceiver;
    //   74: astore 4
    //   76: aload 4
    //   78: monitorenter
    //   79: iload_1
    //   80: ifeq +41 -> 121
    //   83: invokestatic 556	android/os/Binder:clearCallingIdentity	()J
    //   86: lstore_2
    //   87: aload_0
    //   88: iconst_0
    //   89: invokespecial 267	com/android/server/BluetoothManagerService:persistBluetoothSetting	(I)V
    //   92: lload_2
    //   93: invokestatic 596	android/os/Binder:restoreCallingIdentity	(J)V
    //   96: aload_0
    //   97: iconst_0
    //   98: putfield 164	com/android/server/BluetoothManagerService:mEnableExternal	Z
    //   101: aload_0
    //   102: invokespecial 287	com/android/server/BluetoothManagerService:sendDisableMsg	()V
    //   105: aload 4
    //   107: monitorexit
    //   108: iconst_1
    //   109: ireturn
    //   110: ldc 104
    //   112: ldc_w 982
    //   115: invokestatic 834	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   118: pop
    //   119: iconst_0
    //   120: ireturn
    //   121: aload_0
    //   122: monitorenter
    //   123: iconst_0
    //   124: putstatic 161	com/android/server/BluetoothManagerService:mBleAppCount	I
    //   127: aload_0
    //   128: getfield 369	com/android/server/BluetoothManagerService:mBleApps	Ljava/util/Map;
    //   131: invokeinterface 601 1 0
    //   136: aload_0
    //   137: monitorexit
    //   138: aload_0
    //   139: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   142: invokevirtual 632	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   145: invokevirtual 635	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:lock	()V
    //   148: aload_0
    //   149: iconst_0
    //   150: putfield 164	com/android/server/BluetoothManagerService:mEnableExternal	Z
    //   153: aload_0
    //   154: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   157: ifnull +31 -> 188
    //   160: aload_0
    //   161: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   164: invokeinterface 618 1 0
    //   169: bipush 15
    //   171: if_icmpne +45 -> 216
    //   174: aload_0
    //   175: iconst_0
    //   176: putfield 223	com/android/server/BluetoothManagerService:mEnable	Z
    //   179: aload_0
    //   180: getfield 197	com/android/server/BluetoothManagerService:mBluetooth	Landroid/bluetooth/IBluetooth;
    //   183: invokeinterface 837 1 0
    //   188: aload_0
    //   189: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   192: invokevirtual 632	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   195: invokevirtual 645	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   198: goto -93 -> 105
    //   201: astore 5
    //   203: aload 4
    //   205: monitorexit
    //   206: aload 5
    //   208: athrow
    //   209: astore 5
    //   211: aload_0
    //   212: monitorexit
    //   213: aload 5
    //   215: athrow
    //   216: aload_0
    //   217: invokespecial 287	com/android/server/BluetoothManagerService:sendDisableMsg	()V
    //   220: goto -32 -> 188
    //   223: astore 5
    //   225: ldc 104
    //   227: ldc_w 984
    //   230: aload 5
    //   232: invokestatic 628	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   235: pop
    //   236: aload_0
    //   237: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   240: invokevirtual 632	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   243: invokevirtual 645	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   246: goto -141 -> 105
    //   249: astore 5
    //   251: aload_0
    //   252: getfield 205	com/android/server/BluetoothManagerService:mBluetoothLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   255: invokevirtual 632	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   258: invokevirtual 645	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   261: aload 5
    //   263: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	264	0	this	BluetoothManagerService
    //   0	264	1	paramBoolean	boolean
    //   86	7	2	l	long
    //   74	130	4	localBroadcastReceiver	BroadcastReceiver
    //   201	6	5	localObject1	Object
    //   209	5	5	localObject2	Object
    //   223	8	5	localRemoteException	RemoteException
    //   249	13	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   83	105	201	finally
    //   121	123	201	finally
    //   136	138	201	finally
    //   188	198	201	finally
    //   211	216	201	finally
    //   236	246	201	finally
    //   251	264	201	finally
    //   123	136	209	finally
    //   138	188	223	android/os/RemoteException
    //   216	220	223	android/os/RemoteException
    //   138	188	249	finally
    //   216	220	249	finally
    //   225	236	249	finally
  }
  
  boolean doBind(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, UserHandle paramUserHandle)
  {
    ComponentName localComponentName = paramIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    paramIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(paramIntent, paramServiceConnection, paramInt, paramUserHandle))) {
      return true;
    }
    Slog.e("BluetoothManagerService", "Fail to bind to: " + paramIntent);
    return false;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "BluetoothManagerService");
    Object localObject = null;
    if (this.mBluetoothBinder == null) {
      paramFileDescriptor = "Bluetooth Service not connected";
    }
    while (paramFileDescriptor != null) {
      if ((paramArrayOfString.length > 0) && (paramArrayOfString[0].startsWith("--proto")))
      {
        return;
        try
        {
          this.mBluetoothBinder.dump(paramFileDescriptor, paramArrayOfString);
          paramFileDescriptor = (FileDescriptor)localObject;
        }
        catch (RemoteException paramFileDescriptor)
        {
          paramFileDescriptor = "RemoteException while calling Bluetooth Service";
        }
      }
      else
      {
        paramPrintWriter.println(paramFileDescriptor);
      }
    }
  }
  
  public boolean enable()
  {
    if ((Binder.getCallingUid() == 1000) || (checkIfCallerIsForegroundUser()))
    {
      this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN", "Need BLUETOOTH ADMIN permission");
      if (isStrictOpEnable())
      {
        ??? = (AppOpsManager)this.mContext.getSystemService(AppOpsManager.class);
        String str = this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
        if ((Binder.getCallingUid() > 10000) && (str.indexOf("android.uid.systemui") != 0) && (str.indexOf("android.uid.system") != 0) && (((AppOpsManager)???).noteOp(66, Binder.getCallingUid(), str) == 1)) {
          return false;
        }
      }
    }
    else
    {
      Slog.w("BluetoothManagerService", "enable(): not allowed for non-active and non system user");
      return false;
    }
    Slog.d("BluetoothManagerService", "enable():  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding + " mState = " + this.mState);
    synchronized (this.mReceiver)
    {
      this.mQuietEnableExternal = false;
      this.mEnableExternal = true;
      sendEnableMsg(false);
      Slog.d("BluetoothManagerService", "enable returning");
      return true;
    }
  }
  
  public boolean enableNoAutoConnect()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN", "Need BLUETOOTH ADMIN permission");
    Slog.d("BluetoothManagerService", "enableNoAutoConnect():  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding);
    if (UserHandle.getAppId(Binder.getCallingUid()) != 1027) {
      throw new SecurityException("no permission to enable Bluetooth quietly");
    }
    synchronized (this.mReceiver)
    {
      this.mQuietEnableExternal = true;
      this.mEnableExternal = true;
      sendEnableMsg(true);
      return true;
    }
  }
  
  public String getAddress()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
    if ((Binder.getCallingUid() == 1000) || (checkIfCallerIsForegroundUser()))
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.LOCAL_MAC_ADDRESS") != 0) {
        return "02:00:00:00:00:00";
      }
    }
    else
    {
      Slog.w("BluetoothManagerService", "getAddress(): not allowed for non-active and non system user");
      return null;
    }
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null)
      {
        String str = this.mBluetooth.getAddress();
        return str;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e("BluetoothManagerService", "getAddress(): Unable to retrieve address remotely. Returning cached address", localRemoteException);
        this.mBluetoothLock.readLock().unlock();
      }
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
    return this.mAddress;
  }
  
  public IBluetoothGatt getBluetoothGatt()
  {
    return this.mBluetoothGatt;
  }
  
  public String getName()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
    if ((Binder.getCallingUid() == 1000) || (checkIfCallerIsForegroundUser())) {}
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null)
      {
        String str = this.mBluetooth.getName();
        return str;
        Slog.w("BluetoothManagerService", "getName(): not allowed for non-active and non system user");
        return null;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e("BluetoothManagerService", "getName(): Unable to retrieve name remotely. Returning cached name", localRemoteException);
        this.mBluetoothLock.readLock().unlock();
      }
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
    return this.mName;
  }
  
  public int getState()
  {
    if ((Binder.getCallingUid() == 1000) || (checkIfCallerIsForegroundUser())) {}
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null)
      {
        int i = this.mBluetooth.getState();
        return i;
        Slog.w("BluetoothManagerService", "getState(): not allowed for non-active and non system user");
        return 10;
      }
      return 10;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("BluetoothManagerService", "getState()", localRemoteException);
      return 10;
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
  }
  
  public void handleOnBootPhase()
  {
    Slog.d("BluetoothManagerService", "Bluetooth boot completed");
    if ((this.mEnableExternal) && (isBluetoothPersistedStateOnBluetooth()))
    {
      Slog.d("BluetoothManagerService", "Auto-enabling Bluetooth.");
      sendEnableMsg(this.mQuietEnableExternal);
    }
    while (isNameAndAddressSet()) {
      return;
    }
    Slog.d("BluetoothManagerService", "Getting adapter name and address");
    Message localMessage = this.mHandler.obtainMessage(200);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void handleOnSwitchUser(int paramInt)
  {
    Slog.d("BluetoothManagerService", "User " + paramInt + " switched");
    this.mHandler.obtainMessage(300, paramInt, 0).sendToTarget();
  }
  
  public void handleOnUnlockUser(int paramInt)
  {
    Slog.d("BluetoothManagerService", "User " + paramInt + " unlocked");
    this.mHandler.obtainMessage(301, paramInt, 0).sendToTarget();
  }
  
  public boolean isBleAppPresent()
  {
    boolean bool = false;
    Slog.d("BluetoothManagerService", "isBleAppPresent() count: " + mBleAppCount);
    if (mBleAppCount > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isBleScanAlwaysAvailable()
  {
    boolean bool = false;
    if ((!isAirplaneModeOn()) || (this.mEnable)) {}
    try
    {
      int i = Settings.Global.getInt(this.mContentResolver, "ble_scan_always_enabled");
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return false;
    return false;
  }
  
  public boolean isEnabled()
  {
    if ((Binder.getCallingUid() == 1000) || (checkIfCallerIsForegroundUser())) {}
    try
    {
      this.mBluetoothLock.readLock().lock();
      if (this.mBluetooth != null)
      {
        boolean bool = this.mBluetooth.isEnabled();
        return bool;
        Slog.w("BluetoothManagerService", "isEnabled(): not allowed for non-active and non system user");
        return false;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("BluetoothManagerService", "isEnabled()", localRemoteException);
      return false;
    }
    finally
    {
      this.mBluetoothLock.readLock().unlock();
    }
  }
  
  public IBluetooth registerAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
  {
    if (paramIBluetoothManagerCallback == null)
    {
      Slog.w("BluetoothManagerService", "Callback is null in registerAdapter");
      return null;
    }
    Message localMessage = this.mHandler.obtainMessage(20);
    localMessage.obj = paramIBluetoothManagerCallback;
    this.mHandler.sendMessage(localMessage);
    return this.mBluetooth;
  }
  
  public void registerStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
    Message localMessage = this.mHandler.obtainMessage(30);
    localMessage.obj = paramIBluetoothStateChangeCallback;
    this.mHandler.sendMessage(localMessage);
  }
  
  public void unbindAndFinish()
  {
    Slog.d("BluetoothManagerService", "unbindAndFinish(): " + this.mBluetooth + " mBinding = " + this.mBinding);
    for (;;)
    {
      try
      {
        this.mBluetoothLock.writeLock().lock();
        boolean bool = this.mUnbinding;
        if (bool) {
          return;
        }
        this.mUnbinding = true;
        this.mHandler.removeMessages(60);
        this.mHandler.removeMessages(401);
        IBluetooth localIBluetooth = this.mBluetooth;
        if (localIBluetooth != null) {
          try
          {
            this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
            Slog.d("BluetoothManagerService", "Sending unbind request.");
            this.mBluetoothBinder = null;
            this.mBluetooth = null;
            this.mContext.unbindService(this.mConnection);
            this.mUnbinding = false;
            this.mBinding = false;
            this.mBluetoothGatt = null;
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("BluetoothManagerService", "Unable to unregister BluetoothCallback", localRemoteException);
            continue;
          }
        }
        this.mUnbinding = false;
      }
      finally
      {
        this.mBluetoothLock.writeLock().unlock();
      }
    }
  }
  
  public void unbindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
  {
    synchronized (this.mProfileServices)
    {
      ProfileServiceConnections localProfileServiceConnections = (ProfileServiceConnections)this.mProfileServices.get(new Integer(paramInt));
      if (localProfileServiceConnections == null) {
        return;
      }
      ProfileServiceConnections.-wrap3(localProfileServiceConnections, paramIBluetoothProfileServiceConnection);
      return;
    }
  }
  
  public void unregisterAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
  {
    if (paramIBluetoothManagerCallback == null)
    {
      Slog.w("BluetoothManagerService", "Callback is null in unregisterAdapter");
      return;
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
    Message localMessage = this.mHandler.obtainMessage(21);
    localMessage.obj = paramIBluetoothManagerCallback;
    this.mHandler.sendMessage(localMessage);
  }
  
  public void unregisterStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
    Message localMessage = this.mHandler.obtainMessage(31);
    localMessage.obj = paramIBluetoothStateChangeCallback;
    this.mHandler.sendMessage(localMessage);
  }
  
  public int updateBleAppCount(IBinder paramIBinder, boolean paramBoolean)
  {
    ClientDeathRecipient localClientDeathRecipient;
    if (paramBoolean) {
      if ((ClientDeathRecipient)this.mBleApps.get(paramIBinder) == null) {
        localClientDeathRecipient = new ClientDeathRecipient();
      }
    }
    for (;;)
    {
      try
      {
        paramIBinder.linkToDeath(localClientDeathRecipient, 0);
        this.mBleApps.put(paramIBinder, localClientDeathRecipient);
        localClientDeathRecipient = (ClientDeathRecipient)this.mBleApps.get(paramIBinder);
      }
      catch (RemoteException paramIBinder)
      {
        try
        {
          mBleAppCount += 1;
          Slog.d("BluetoothManagerService", "Registered for death Notification");
          Slog.d("BluetoothManagerService", "Updated BleAppCount" + mBleAppCount);
          if ((mBleAppCount == 0) && (this.mEnable)) {
            disableBleScanMode();
          }
          return mBleAppCount;
        }
        finally {}
        paramIBinder = paramIBinder;
        throw new IllegalArgumentException("Wake lock is already dead.");
      }
      if (localClientDeathRecipient == null) {
        continue;
      }
      paramIBinder.unlinkToDeath(localClientDeathRecipient, 0);
      this.mBleApps.remove(paramIBinder);
      try
      {
        if (mBleAppCount > 0) {
          mBleAppCount -= 1;
        }
        Slog.d("BluetoothManagerService", "Unregistered for death Notification");
      }
      finally {}
    }
  }
  
  private class BluetoothHandler
    extends Handler
  {
    boolean mGetNameAddressOnly = false;
    
    public BluetoothHandler(Looper paramLooper)
    {
      super();
    }
    
    /* Error */
    public void handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: ldc 27
      //   2: new 29	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   9: ldc 34
      //   11: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: aload_1
      //   15: getfield 44	android/os/Message:what	I
      //   18: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   21: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   24: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   27: pop
      //   28: aload_1
      //   29: getfield 44	android/os/Message:what	I
      //   32: lookupswitch	default:+156->188, 1:+475->507, 2:+807->839, 20:+900->932, 21:+977->1009, 30:+1054->1086, 31:+1079->1111, 40:+1182->1214, 41:+1966->1998, 42:+2299->2331, 50:+420->452, 60:+1674->1706, 100:+1629->1661, 101:+2332->2364, 200:+157->189, 300:+2377->2409, 301:+2952->2984, 400:+1104->1136, 401:+1150->1182
      //   188: return
      //   189: ldc 27
      //   191: ldc 59
      //   193: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   196: pop
      //   197: aload_0
      //   198: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   201: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   204: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   207: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   210: aload_0
      //   211: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   214: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   217: ifnonnull +13 -> 230
      //   220: aload_0
      //   221: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   224: invokestatic 82	com/android/server/BluetoothManagerService:-get0	(Lcom/android/server/BluetoothManagerService;)Z
      //   227: ifeq +82 -> 309
      //   230: aload_0
      //   231: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   234: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   237: astore_1
      //   238: aload_1
      //   239: ifnull +56 -> 295
      //   242: aload_0
      //   243: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   246: aload_0
      //   247: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   250: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   253: invokeinterface 87 1 0
      //   258: aload_0
      //   259: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   262: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   265: invokeinterface 90 1 0
      //   270: invokestatic 94	com/android/server/BluetoothManagerService:-wrap19	(Lcom/android/server/BluetoothManagerService;Ljava/lang/String;Ljava/lang/String;)V
      //   273: aload_0
      //   274: getfield 20	com/android/server/BluetoothManagerService$BluetoothHandler:mGetNameAddressOnly	Z
      //   277: ifeq +13 -> 290
      //   280: aload_0
      //   281: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   284: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   287: ifeq +155 -> 442
      //   290: aload_0
      //   291: iconst_0
      //   292: putfield 20	com/android/server/BluetoothManagerService$BluetoothHandler:mGetNameAddressOnly	Z
      //   295: aload_0
      //   296: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   299: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   302: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   305: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   308: return
      //   309: ldc 27
      //   311: ldc 102
      //   313: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   316: pop
      //   317: aload_0
      //   318: iconst_1
      //   319: putfield 20	com/android/server/BluetoothManagerService$BluetoothHandler:mGetNameAddressOnly	Z
      //   322: aload_0
      //   323: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   326: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   329: bipush 100
      //   331: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   334: astore_1
      //   335: aload_0
      //   336: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   339: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   342: aload_1
      //   343: ldc2_w 111
      //   346: invokevirtual 116	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   349: pop
      //   350: new 118	android/content/Intent
      //   353: dup
      //   354: ldc 84
      //   356: invokevirtual 121	java/lang/Class:getName	()Ljava/lang/String;
      //   359: invokespecial 124	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   362: astore_1
      //   363: aload_0
      //   364: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   367: aload_1
      //   368: aload_0
      //   369: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   372: invokestatic 128	com/android/server/BluetoothManagerService:-get6	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothServiceConnection;
      //   375: bipush 65
      //   377: getstatic 134	android/os/UserHandle:CURRENT	Landroid/os/UserHandle;
      //   380: invokevirtual 138	com/android/server/BluetoothManagerService:doBind	(Landroid/content/Intent;Landroid/content/ServiceConnection;ILandroid/os/UserHandle;)Z
      //   383: ifne +34 -> 417
      //   386: aload_0
      //   387: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   390: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   393: bipush 100
      //   395: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   398: goto -103 -> 295
      //   401: astore_1
      //   402: aload_0
      //   403: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   406: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   409: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   412: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   415: aload_1
      //   416: athrow
      //   417: aload_0
      //   418: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   421: iconst_1
      //   422: invokestatic 146	com/android/server/BluetoothManagerService:-set0	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   425: pop
      //   426: goto -131 -> 295
      //   429: astore_1
      //   430: ldc 27
      //   432: ldc -108
      //   434: aload_1
      //   435: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   438: pop
      //   439: goto -166 -> 273
      //   442: aload_0
      //   443: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   446: invokevirtual 155	com/android/server/BluetoothManagerService:unbindAndFinish	()V
      //   449: goto -159 -> 290
      //   452: aload_1
      //   453: getfield 158	android/os/Message:arg1	I
      //   456: istore_2
      //   457: ldc -96
      //   459: ldc -94
      //   461: invokestatic 168	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      //   464: ldc -86
      //   466: invokevirtual 176	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   469: ifne +22 -> 491
      //   472: aload_0
      //   473: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   476: invokestatic 179	com/android/server/BluetoothManagerService:-wrap3	(Lcom/android/server/BluetoothManagerService;)Z
      //   479: ifeq +12 -> 491
      //   482: aload_0
      //   483: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   486: iload_2
      //   487: invokestatic 183	com/android/server/BluetoothManagerService:-wrap15	(Lcom/android/server/BluetoothManagerService;I)V
      //   490: return
      //   491: aload_0
      //   492: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   495: invokestatic 187	com/android/server/BluetoothManagerService:-wrap20	(Lcom/android/server/BluetoothManagerService;)V
      //   498: aload_0
      //   499: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   502: iload_2
      //   503: invokestatic 190	com/android/server/BluetoothManagerService:-wrap18	(Lcom/android/server/BluetoothManagerService;I)V
      //   506: return
      //   507: ldc 27
      //   509: new 29	java/lang/StringBuilder
      //   512: dup
      //   513: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   516: ldc -64
      //   518: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   521: aload_0
      //   522: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   525: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   528: invokevirtual 195	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   531: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   534: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   537: pop
      //   538: aload_0
      //   539: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   542: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   545: bipush 42
      //   547: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   550: aload_0
      //   551: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   554: iconst_1
      //   555: invokestatic 198	com/android/server/BluetoothManagerService:-set5	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   558: pop
      //   559: aload_0
      //   560: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   563: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   566: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   569: invokevirtual 205	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:lock	()V
      //   572: aload_0
      //   573: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   576: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   579: ifnull +72 -> 651
      //   582: aload_0
      //   583: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   586: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   589: invokeinterface 209 1 0
      //   594: bipush 15
      //   596: if_icmpne +55 -> 651
      //   599: ldc 27
      //   601: ldc -45
      //   603: invokestatic 214	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   606: pop
      //   607: aload_0
      //   608: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   611: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   614: invokeinterface 217 1 0
      //   619: invokestatic 223	android/os/Binder:clearCallingIdentity	()J
      //   622: lstore 4
      //   624: aload_0
      //   625: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   628: iconst_1
      //   629: invokestatic 226	com/android/server/BluetoothManagerService:-wrap11	(Lcom/android/server/BluetoothManagerService;I)V
      //   632: lload 4
      //   634: invokestatic 230	android/os/Binder:restoreCallingIdentity	(J)V
      //   637: aload_0
      //   638: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   641: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   644: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   647: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   650: return
      //   651: aload_0
      //   652: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   655: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   658: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   661: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   664: aload_0
      //   665: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   668: astore 7
      //   670: aload_1
      //   671: getfield 158	android/os/Message:arg1	I
      //   674: iconst_1
      //   675: if_icmpne +83 -> 758
      //   678: iconst_1
      //   679: istore 6
      //   681: aload 7
      //   683: iload 6
      //   685: invokestatic 234	com/android/server/BluetoothManagerService:-set8	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   688: pop
      //   689: aload_0
      //   690: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   693: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   696: ifnonnull +68 -> 764
      //   699: aload_0
      //   700: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   703: aload_0
      //   704: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   707: invokestatic 237	com/android/server/BluetoothManagerService:-get14	(Lcom/android/server/BluetoothManagerService;)Z
      //   710: invokestatic 241	com/android/server/BluetoothManagerService:-wrap9	(Lcom/android/server/BluetoothManagerService;Z)V
      //   713: return
      //   714: astore 7
      //   716: ldc 27
      //   718: ldc -13
      //   720: aload 7
      //   722: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   725: pop
      //   726: aload_0
      //   727: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   730: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   733: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   736: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   739: goto -75 -> 664
      //   742: astore_1
      //   743: aload_0
      //   744: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   747: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   750: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   753: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   756: aload_1
      //   757: athrow
      //   758: iconst_0
      //   759: istore 6
      //   761: goto -80 -> 681
      //   764: aload_0
      //   765: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   768: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   771: invokeinterface 209 1 0
      //   776: istore_2
      //   777: iload_2
      //   778: bipush 13
      //   780: if_icmpeq +9 -> 789
      //   783: iload_2
      //   784: bipush 16
      //   786: if_icmpne +13 -> 799
      //   789: aload_0
      //   790: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   793: iconst_0
      //   794: iconst_1
      //   795: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   798: pop
      //   799: aload_0
      //   800: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   803: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   806: bipush 42
      //   808: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   811: astore_1
      //   812: aload_0
      //   813: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   816: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   819: aload_1
      //   820: ldc2_w 248
      //   823: invokevirtual 116	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   826: pop
      //   827: return
      //   828: astore_1
      //   829: ldc 27
      //   831: ldc -5
      //   833: aload_1
      //   834: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   837: pop
      //   838: return
      //   839: aload_0
      //   840: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   843: invokestatic 187	com/android/server/BluetoothManagerService:-wrap20	(Lcom/android/server/BluetoothManagerService;)V
      //   846: aload_0
      //   847: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   850: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   853: bipush 42
      //   855: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   858: aload_0
      //   859: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   862: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   865: ifeq +50 -> 915
      //   868: aload_0
      //   869: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   872: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   875: ifnull +40 -> 915
      //   878: aload_0
      //   879: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   882: iconst_1
      //   883: iconst_0
      //   884: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   887: pop
      //   888: aload_0
      //   889: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   892: iconst_0
      //   893: invokestatic 198	com/android/server/BluetoothManagerService:-set5	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   896: pop
      //   897: aload_0
      //   898: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   901: invokestatic 254	com/android/server/BluetoothManagerService:-wrap8	(Lcom/android/server/BluetoothManagerService;)V
      //   904: aload_0
      //   905: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   908: iconst_0
      //   909: iconst_0
      //   910: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   913: pop
      //   914: return
      //   915: aload_0
      //   916: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   919: iconst_0
      //   920: invokestatic 198	com/android/server/BluetoothManagerService:-set5	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   923: pop
      //   924: aload_0
      //   925: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   928: invokestatic 254	com/android/server/BluetoothManagerService:-wrap8	(Lcom/android/server/BluetoothManagerService;)V
      //   931: return
      //   932: aload_1
      //   933: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   936: checkcast 260	android/bluetooth/IBluetoothManagerCallback
      //   939: astore 7
      //   941: aload_0
      //   942: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   945: invokestatic 264	com/android/server/BluetoothManagerService:-get5	(Lcom/android/server/BluetoothManagerService;)Landroid/os/RemoteCallbackList;
      //   948: aload 7
      //   950: invokevirtual 270	android/os/RemoteCallbackList:register	(Landroid/os/IInterface;)Z
      //   953: istore 6
      //   955: new 29	java/lang/StringBuilder
      //   958: dup
      //   959: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   962: ldc_w 272
      //   965: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   968: astore 8
      //   970: aload 7
      //   972: astore_1
      //   973: aload 7
      //   975: ifnonnull +7 -> 982
      //   978: ldc_w 274
      //   981: astore_1
      //   982: ldc 27
      //   984: aload 8
      //   986: aload_1
      //   987: invokevirtual 195	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   990: ldc_w 276
      //   993: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   996: iload 6
      //   998: invokevirtual 279	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1001: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1004: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   1007: pop
      //   1008: return
      //   1009: aload_1
      //   1010: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1013: checkcast 260	android/bluetooth/IBluetoothManagerCallback
      //   1016: astore 7
      //   1018: aload_0
      //   1019: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1022: invokestatic 264	com/android/server/BluetoothManagerService:-get5	(Lcom/android/server/BluetoothManagerService;)Landroid/os/RemoteCallbackList;
      //   1025: aload 7
      //   1027: invokevirtual 282	android/os/RemoteCallbackList:unregister	(Landroid/os/IInterface;)Z
      //   1030: istore 6
      //   1032: new 29	java/lang/StringBuilder
      //   1035: dup
      //   1036: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   1039: ldc_w 284
      //   1042: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1045: astore 8
      //   1047: aload 7
      //   1049: astore_1
      //   1050: aload 7
      //   1052: ifnonnull +7 -> 1059
      //   1055: ldc_w 274
      //   1058: astore_1
      //   1059: ldc 27
      //   1061: aload 8
      //   1063: aload_1
      //   1064: invokevirtual 195	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1067: ldc_w 276
      //   1070: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1073: iload 6
      //   1075: invokevirtual 279	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1078: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1081: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   1084: pop
      //   1085: return
      //   1086: aload_1
      //   1087: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1090: checkcast 286	android/bluetooth/IBluetoothStateChangeCallback
      //   1093: astore_1
      //   1094: aload_1
      //   1095: ifnull -907 -> 188
      //   1098: aload_0
      //   1099: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1102: invokestatic 289	com/android/server/BluetoothManagerService:-get18	(Lcom/android/server/BluetoothManagerService;)Landroid/os/RemoteCallbackList;
      //   1105: aload_1
      //   1106: invokevirtual 270	android/os/RemoteCallbackList:register	(Landroid/os/IInterface;)Z
      //   1109: pop
      //   1110: return
      //   1111: aload_1
      //   1112: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1115: checkcast 286	android/bluetooth/IBluetoothStateChangeCallback
      //   1118: astore_1
      //   1119: aload_1
      //   1120: ifnull -932 -> 188
      //   1123: aload_0
      //   1124: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1127: invokestatic 289	com/android/server/BluetoothManagerService:-get18	(Lcom/android/server/BluetoothManagerService;)Landroid/os/RemoteCallbackList;
      //   1130: aload_1
      //   1131: invokevirtual 282	android/os/RemoteCallbackList:unregister	(Landroid/os/IInterface;)Z
      //   1134: pop
      //   1135: return
      //   1136: aload_0
      //   1137: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1140: invokestatic 293	com/android/server/BluetoothManagerService:-get13	(Lcom/android/server/BluetoothManagerService;)Ljava/util/Map;
      //   1143: new 295	java/lang/Integer
      //   1146: dup
      //   1147: aload_1
      //   1148: getfield 158	android/os/Message:arg1	I
      //   1151: invokespecial 297	java/lang/Integer:<init>	(I)V
      //   1154: invokeinterface 302 2 0
      //   1159: checkcast 304	com/android/server/BluetoothManagerService$ProfileServiceConnections
      //   1162: astore 7
      //   1164: aload 7
      //   1166: ifnull -978 -> 188
      //   1169: aload 7
      //   1171: aload_1
      //   1172: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1175: checkcast 306	android/bluetooth/IBluetoothProfileServiceConnection
      //   1178: invokestatic 310	com/android/server/BluetoothManagerService$ProfileServiceConnections:-wrap1	(Lcom/android/server/BluetoothManagerService$ProfileServiceConnections;Landroid/bluetooth/IBluetoothProfileServiceConnection;)V
      //   1181: return
      //   1182: aload_1
      //   1183: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1186: checkcast 304	com/android/server/BluetoothManagerService$ProfileServiceConnections
      //   1189: astore 7
      //   1191: aload_0
      //   1192: sipush 401
      //   1195: aload_1
      //   1196: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1199: invokevirtual 313	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(ILjava/lang/Object;)V
      //   1202: aload 7
      //   1204: ifnull -1016 -> 188
      //   1207: aload 7
      //   1209: invokestatic 317	com/android/server/BluetoothManagerService$ProfileServiceConnections:-wrap0	(Lcom/android/server/BluetoothManagerService$ProfileServiceConnections;)Z
      //   1212: pop
      //   1213: return
      //   1214: ldc 27
      //   1216: new 29	java/lang/StringBuilder
      //   1219: dup
      //   1220: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   1223: ldc_w 319
      //   1226: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1229: aload_1
      //   1230: getfield 158	android/os/Message:arg1	I
      //   1233: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1236: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1239: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   1242: pop
      //   1243: aload_1
      //   1244: getfield 258	android/os/Message:obj	Ljava/lang/Object;
      //   1247: checkcast 321	android/os/IBinder
      //   1250: astore 7
      //   1252: aload_0
      //   1253: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1256: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1259: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1262: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   1265: aload_1
      //   1266: getfield 158	android/os/Message:arg1	I
      //   1269: iconst_2
      //   1270: if_icmpne +37 -> 1307
      //   1273: aload_0
      //   1274: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1277: aload 7
      //   1279: invokestatic 327	android/bluetooth/IBluetoothGatt$Stub:asInterface	(Landroid/os/IBinder;)Landroid/bluetooth/IBluetoothGatt;
      //   1282: invokestatic 331	com/android/server/BluetoothManagerService:-set4	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetoothGatt;)Landroid/bluetooth/IBluetoothGatt;
      //   1285: pop
      //   1286: aload_0
      //   1287: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1290: invokestatic 334	com/android/server/BluetoothManagerService:-wrap10	(Lcom/android/server/BluetoothManagerService;)V
      //   1293: aload_0
      //   1294: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1297: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1300: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1303: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   1306: return
      //   1307: aload_0
      //   1308: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1311: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   1314: bipush 100
      //   1316: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   1319: aload_0
      //   1320: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1323: iconst_0
      //   1324: invokestatic 146	com/android/server/BluetoothManagerService:-set0	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   1327: pop
      //   1328: aload_0
      //   1329: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1332: aload 7
      //   1334: invokestatic 338	com/android/server/BluetoothManagerService:-set3	(Lcom/android/server/BluetoothManagerService;Landroid/os/IBinder;)Landroid/os/IBinder;
      //   1337: pop
      //   1338: aload_0
      //   1339: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1342: aload 7
      //   1344: invokestatic 343	android/bluetooth/IBluetooth$Stub:asInterface	(Landroid/os/IBinder;)Landroid/bluetooth/IBluetooth;
      //   1347: invokestatic 347	com/android/server/BluetoothManagerService:-set2	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetooth;)Landroid/bluetooth/IBluetooth;
      //   1350: pop
      //   1351: aload_0
      //   1352: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1355: invokestatic 350	com/android/server/BluetoothManagerService:-wrap2	(Lcom/android/server/BluetoothManagerService;)Z
      //   1358: ifne +54 -> 1412
      //   1361: aload_0
      //   1362: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1365: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   1368: sipush 200
      //   1371: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   1374: astore_1
      //   1375: aload_0
      //   1376: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1379: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   1382: aload_1
      //   1383: invokevirtual 354	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessage	(Landroid/os/Message;)Z
      //   1386: pop
      //   1387: aload_0
      //   1388: getfield 20	com/android/server/BluetoothManagerService$BluetoothHandler:mGetNameAddressOnly	Z
      //   1391: istore 6
      //   1393: iload 6
      //   1395: ifeq +17 -> 1412
      //   1398: aload_0
      //   1399: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1402: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1405: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1408: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   1411: return
      //   1412: aload_0
      //   1413: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1416: invokestatic 358	com/android/server/BluetoothManagerService:-get7	(Lcom/android/server/BluetoothManagerService;)Landroid/content/ContentResolver;
      //   1419: ldc_w 360
      //   1422: iconst_0
      //   1423: invokestatic 366	android/provider/Settings$Secure:getInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)I
      //   1426: iconst_1
      //   1427: if_icmpne +143 -> 1570
      //   1430: iconst_1
      //   1431: istore 6
      //   1433: aload_0
      //   1434: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1437: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1440: iload 6
      //   1442: invokeinterface 370 2 0
      //   1447: ifne +12 -> 1459
      //   1450: ldc 27
      //   1452: ldc_w 372
      //   1455: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1458: pop
      //   1459: aload_0
      //   1460: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1463: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1466: aload_0
      //   1467: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1470: invokestatic 378	com/android/server/BluetoothManagerService:-get3	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetoothCallback;
      //   1473: invokeinterface 382 2 0
      //   1478: aload_0
      //   1479: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1482: invokestatic 385	com/android/server/BluetoothManagerService:-wrap14	(Lcom/android/server/BluetoothManagerService;)V
      //   1485: aload_0
      //   1486: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1489: invokestatic 237	com/android/server/BluetoothManagerService:-get14	(Lcom/android/server/BluetoothManagerService;)Z
      //   1492: ifne +128 -> 1620
      //   1495: aload_0
      //   1496: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1499: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1502: invokeinterface 389 1 0
      //   1507: ifne +12 -> 1519
      //   1510: ldc 27
      //   1512: ldc_w 391
      //   1515: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1518: pop
      //   1519: aload_0
      //   1520: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1523: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1526: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1529: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   1532: aload_0
      //   1533: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1536: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   1539: ifne -1351 -> 188
      //   1542: aload_0
      //   1543: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1546: iconst_1
      //   1547: iconst_0
      //   1548: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   1551: pop
      //   1552: aload_0
      //   1553: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1556: invokestatic 254	com/android/server/BluetoothManagerService:-wrap8	(Lcom/android/server/BluetoothManagerService;)V
      //   1559: aload_0
      //   1560: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1563: iconst_0
      //   1564: iconst_0
      //   1565: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   1568: pop
      //   1569: return
      //   1570: iconst_0
      //   1571: istore 6
      //   1573: goto -140 -> 1433
      //   1576: astore_1
      //   1577: ldc 27
      //   1579: ldc_w 393
      //   1582: aload_1
      //   1583: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1586: pop
      //   1587: goto -128 -> 1459
      //   1590: astore_1
      //   1591: aload_0
      //   1592: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1595: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1598: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1601: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   1604: aload_1
      //   1605: athrow
      //   1606: astore_1
      //   1607: ldc 27
      //   1609: ldc_w 395
      //   1612: aload_1
      //   1613: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1616: pop
      //   1617: goto -139 -> 1478
      //   1620: aload_0
      //   1621: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1624: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1627: invokeinterface 398 1 0
      //   1632: ifne -113 -> 1519
      //   1635: ldc 27
      //   1637: ldc_w 400
      //   1640: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1643: pop
      //   1644: goto -125 -> 1519
      //   1647: astore_1
      //   1648: ldc 27
      //   1650: ldc_w 402
      //   1653: aload_1
      //   1654: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1657: pop
      //   1658: goto -139 -> 1519
      //   1661: ldc 27
      //   1663: ldc_w 404
      //   1666: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   1669: pop
      //   1670: aload_0
      //   1671: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1674: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1677: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1680: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   1683: aload_0
      //   1684: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1687: iconst_0
      //   1688: invokestatic 146	com/android/server/BluetoothManagerService:-set0	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   1691: pop
      //   1692: aload_0
      //   1693: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1696: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   1699: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   1702: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   1705: return
      //   1706: aload_1
      //   1707: getfield 158	android/os/Message:arg1	I
      //   1710: istore_2
      //   1711: aload_1
      //   1712: getfield 407	android/os/Message:arg2	I
      //   1715: istore_3
      //   1716: ldc 27
      //   1718: new 29	java/lang/StringBuilder
      //   1721: dup
      //   1722: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   1725: ldc_w 409
      //   1728: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1731: iload_2
      //   1732: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1735: ldc_w 411
      //   1738: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1741: iload_3
      //   1742: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1745: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1748: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   1751: pop
      //   1752: aload_0
      //   1753: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1756: iload_3
      //   1757: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   1760: pop
      //   1761: aload_0
      //   1762: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1765: iload_2
      //   1766: iload_3
      //   1767: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   1770: iload_2
      //   1771: bipush 14
      //   1773: if_icmpne +36 -> 1809
      //   1776: iload_3
      //   1777: bipush 10
      //   1779: if_icmpne +30 -> 1809
      //   1782: aload_0
      //   1783: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1786: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1789: ifnull +20 -> 1809
      //   1792: aload_0
      //   1793: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1796: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   1799: ifeq +10 -> 1809
      //   1802: aload_0
      //   1803: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1806: invokestatic 422	com/android/server/BluetoothManagerService:-wrap12	(Lcom/android/server/BluetoothManagerService;)V
      //   1809: iload_2
      //   1810: bipush 11
      //   1812: if_icmpne +37 -> 1849
      //   1815: iload_3
      //   1816: bipush 10
      //   1818: if_icmpne +31 -> 1849
      //   1821: aload_0
      //   1822: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1825: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1828: ifnull +21 -> 1849
      //   1831: aload_0
      //   1832: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1835: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   1838: ifeq +11 -> 1849
      //   1841: aload_0
      //   1842: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1845: iconst_0
      //   1846: invokestatic 226	com/android/server/BluetoothManagerService:-wrap11	(Lcom/android/server/BluetoothManagerService;I)V
      //   1849: iload_2
      //   1850: bipush 11
      //   1852: if_icmpne +36 -> 1888
      //   1855: iload_3
      //   1856: bipush 15
      //   1858: if_icmpne +30 -> 1888
      //   1861: aload_0
      //   1862: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1865: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   1868: ifnull +20 -> 1888
      //   1871: aload_0
      //   1872: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1875: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   1878: ifeq +10 -> 1888
      //   1881: aload_0
      //   1882: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1885: invokestatic 422	com/android/server/BluetoothManagerService:-wrap12	(Lcom/android/server/BluetoothManagerService;)V
      //   1888: iload_2
      //   1889: bipush 16
      //   1891: if_icmpne +66 -> 1957
      //   1894: iload_3
      //   1895: bipush 10
      //   1897: if_icmpne +60 -> 1957
      //   1900: aload_0
      //   1901: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1904: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   1907: ifeq +50 -> 1957
      //   1910: ldc 27
      //   1912: ldc_w 424
      //   1915: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   1918: pop
      //   1919: aload_0
      //   1920: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1923: iconst_0
      //   1924: iconst_1
      //   1925: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   1928: pop
      //   1929: aload_0
      //   1930: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1933: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   1936: bipush 42
      //   1938: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   1941: astore_1
      //   1942: aload_0
      //   1943: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1946: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   1949: aload_1
      //   1950: ldc2_w 248
      //   1953: invokevirtual 116	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   1956: pop
      //   1957: iload_3
      //   1958: bipush 12
      //   1960: if_icmpeq +9 -> 1969
      //   1963: iload_3
      //   1964: bipush 15
      //   1966: if_icmpne -1778 -> 188
      //   1969: aload_0
      //   1970: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1973: invokestatic 428	com/android/server/BluetoothManagerService:-get11	(Lcom/android/server/BluetoothManagerService;)I
      //   1976: ifeq -1788 -> 188
      //   1979: ldc 27
      //   1981: ldc_w 430
      //   1984: invokestatic 214	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   1987: pop
      //   1988: aload_0
      //   1989: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   1992: iconst_0
      //   1993: invokestatic 433	com/android/server/BluetoothManagerService:-set7	(Lcom/android/server/BluetoothManagerService;I)I
      //   1996: pop
      //   1997: return
      //   1998: ldc 27
      //   2000: new 29	java/lang/StringBuilder
      //   2003: dup
      //   2004: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   2007: ldc_w 435
      //   2010: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2013: aload_1
      //   2014: getfield 158	android/os/Message:arg1	I
      //   2017: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2020: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2023: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   2026: pop
      //   2027: aload_0
      //   2028: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2031: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2034: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2037: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   2040: aload_1
      //   2041: getfield 158	android/os/Message:arg1	I
      //   2044: iconst_1
      //   2045: if_icmpne +196 -> 2241
      //   2048: aload_0
      //   2049: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2052: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2055: astore_1
      //   2056: aload_1
      //   2057: ifnonnull +17 -> 2074
      //   2060: aload_0
      //   2061: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2064: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2067: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2070: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2073: return
      //   2074: aload_0
      //   2075: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2078: aconst_null
      //   2079: invokestatic 347	com/android/server/BluetoothManagerService:-set2	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetooth;)Landroid/bluetooth/IBluetooth;
      //   2082: pop
      //   2083: aload_0
      //   2084: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2087: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2090: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2093: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2096: aload_0
      //   2097: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2100: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   2103: ifeq +40 -> 2143
      //   2106: aload_0
      //   2107: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2110: iconst_0
      //   2111: invokestatic 198	com/android/server/BluetoothManagerService:-set5	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   2114: pop
      //   2115: aload_0
      //   2116: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2119: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2122: bipush 42
      //   2124: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   2127: astore_1
      //   2128: aload_0
      //   2129: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2132: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2135: aload_1
      //   2136: ldc2_w 436
      //   2139: invokevirtual 116	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   2142: pop
      //   2143: aload_0
      //   2144: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2147: invokestatic 440	com/android/server/BluetoothManagerService:-wrap13	(Lcom/android/server/BluetoothManagerService;)V
      //   2150: aload_0
      //   2151: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2154: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2157: bipush 11
      //   2159: if_icmpeq +15 -> 2174
      //   2162: aload_0
      //   2163: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2166: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2169: bipush 12
      //   2171: if_icmpne +24 -> 2195
      //   2174: aload_0
      //   2175: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2178: bipush 12
      //   2180: bipush 13
      //   2182: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2185: aload_0
      //   2186: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2189: bipush 13
      //   2191: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   2194: pop
      //   2195: aload_0
      //   2196: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2199: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2202: bipush 13
      //   2204: if_icmpne +14 -> 2218
      //   2207: aload_0
      //   2208: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2211: bipush 13
      //   2213: bipush 10
      //   2215: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2218: aload_0
      //   2219: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2222: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2225: bipush 60
      //   2227: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   2230: aload_0
      //   2231: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2234: bipush 10
      //   2236: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   2239: pop
      //   2240: return
      //   2241: aload_1
      //   2242: getfield 158	android/os/Message:arg1	I
      //   2245: iconst_2
      //   2246: if_icmpne +26 -> 2272
      //   2249: aload_0
      //   2250: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2253: aconst_null
      //   2254: invokestatic 331	com/android/server/BluetoothManagerService:-set4	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetoothGatt;)Landroid/bluetooth/IBluetoothGatt;
      //   2257: pop
      //   2258: aload_0
      //   2259: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2262: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2265: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2268: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2271: return
      //   2272: ldc 27
      //   2274: new 29	java/lang/StringBuilder
      //   2277: dup
      //   2278: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   2281: ldc_w 445
      //   2284: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2287: aload_1
      //   2288: getfield 158	android/os/Message:arg1	I
      //   2291: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2294: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2297: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   2300: pop
      //   2301: aload_0
      //   2302: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2305: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2308: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2311: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2314: return
      //   2315: astore_1
      //   2316: aload_0
      //   2317: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2320: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2323: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2326: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2329: aload_1
      //   2330: athrow
      //   2331: ldc 27
      //   2333: ldc_w 447
      //   2336: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   2339: pop
      //   2340: aload_0
      //   2341: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2344: iconst_1
      //   2345: invokestatic 198	com/android/server/BluetoothManagerService:-set5	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   2348: pop
      //   2349: aload_0
      //   2350: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2353: aload_0
      //   2354: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2357: invokestatic 237	com/android/server/BluetoothManagerService:-get14	(Lcom/android/server/BluetoothManagerService;)Z
      //   2360: invokestatic 241	com/android/server/BluetoothManagerService:-wrap9	(Lcom/android/server/BluetoothManagerService;Z)V
      //   2363: return
      //   2364: ldc 27
      //   2366: ldc_w 449
      //   2369: invokestatic 374	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   2372: pop
      //   2373: aload_0
      //   2374: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2377: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2380: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2383: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   2386: aload_0
      //   2387: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2390: iconst_0
      //   2391: invokestatic 452	com/android/server/BluetoothManagerService:-set10	(Lcom/android/server/BluetoothManagerService;Z)Z
      //   2394: pop
      //   2395: aload_0
      //   2396: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2399: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2402: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2405: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2408: return
      //   2409: ldc 27
      //   2411: ldc_w 454
      //   2414: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   2417: pop
      //   2418: aload_0
      //   2419: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2422: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2425: sipush 300
      //   2428: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   2431: aload_0
      //   2432: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2435: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   2438: ifeq +453 -> 2891
      //   2441: aload_0
      //   2442: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2445: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2448: ifnull +443 -> 2891
      //   2451: aload_0
      //   2452: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2455: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2458: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   2461: invokevirtual 205	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:lock	()V
      //   2464: aload_0
      //   2465: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2468: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2471: ifnull +22 -> 2493
      //   2474: aload_0
      //   2475: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2478: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2481: aload_0
      //   2482: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2485: invokestatic 378	com/android/server/BluetoothManagerService:-get3	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetoothCallback;
      //   2488: invokeinterface 457 2 0
      //   2493: aload_0
      //   2494: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2497: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2500: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   2503: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   2506: aload_0
      //   2507: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2510: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2513: bipush 13
      //   2515: if_icmpne +29 -> 2544
      //   2518: aload_0
      //   2519: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2522: aload_0
      //   2523: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2526: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2529: bipush 10
      //   2531: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2534: aload_0
      //   2535: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2538: bipush 10
      //   2540: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   2543: pop
      //   2544: aload_0
      //   2545: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2548: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2551: bipush 10
      //   2553: if_icmpne +29 -> 2582
      //   2556: aload_0
      //   2557: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2560: aload_0
      //   2561: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2564: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2567: bipush 11
      //   2569: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2572: aload_0
      //   2573: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2576: bipush 11
      //   2578: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   2581: pop
      //   2582: aload_0
      //   2583: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2586: iconst_1
      //   2587: iconst_0
      //   2588: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   2591: pop
      //   2592: aload_0
      //   2593: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2596: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2599: bipush 11
      //   2601: if_icmpne +19 -> 2620
      //   2604: aload_0
      //   2605: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2608: aload_0
      //   2609: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2612: invokestatic 443	com/android/server/BluetoothManagerService:-get17	(Lcom/android/server/BluetoothManagerService;)I
      //   2615: bipush 12
      //   2617: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2620: aload_0
      //   2621: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2624: invokestatic 460	com/android/server/BluetoothManagerService:-wrap21	(Lcom/android/server/BluetoothManagerService;)V
      //   2627: aload_0
      //   2628: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2631: invokestatic 463	com/android/server/BluetoothManagerService:-wrap6	(Lcom/android/server/BluetoothManagerService;)V
      //   2634: aload_0
      //   2635: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2638: invokestatic 254	com/android/server/BluetoothManagerService:-wrap8	(Lcom/android/server/BluetoothManagerService;)V
      //   2641: aload_0
      //   2642: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2645: bipush 12
      //   2647: bipush 13
      //   2649: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2652: aload_0
      //   2653: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2656: iconst_0
      //   2657: iconst_1
      //   2658: invokestatic 247	com/android/server/BluetoothManagerService:-wrap4	(Lcom/android/server/BluetoothManagerService;ZZ)Z
      //   2661: ifeq +200 -> 2861
      //   2664: iconst_0
      //   2665: istore_2
      //   2666: aload_0
      //   2667: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2670: bipush 13
      //   2672: bipush 10
      //   2674: invokestatic 419	com/android/server/BluetoothManagerService:-wrap5	(Lcom/android/server/BluetoothManagerService;II)V
      //   2677: aload_0
      //   2678: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2681: invokestatic 440	com/android/server/BluetoothManagerService:-wrap13	(Lcom/android/server/BluetoothManagerService;)V
      //   2684: iload_2
      //   2685: ifne +74 -> 2759
      //   2688: aload_0
      //   2689: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2692: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2695: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2698: invokevirtual 74	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
      //   2701: aload_0
      //   2702: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2705: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2708: ifnull +29 -> 2737
      //   2711: aload_0
      //   2712: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2715: aconst_null
      //   2716: invokestatic 347	com/android/server/BluetoothManagerService:-set2	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetooth;)Landroid/bluetooth/IBluetooth;
      //   2719: pop
      //   2720: aload_0
      //   2721: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2724: invokestatic 467	com/android/server/BluetoothManagerService:-get8	(Lcom/android/server/BluetoothManagerService;)Landroid/content/Context;
      //   2727: aload_0
      //   2728: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2731: invokestatic 128	com/android/server/BluetoothManagerService:-get6	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothServiceConnection;
      //   2734: invokevirtual 473	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
      //   2737: aload_0
      //   2738: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2741: aconst_null
      //   2742: invokestatic 331	com/android/server/BluetoothManagerService:-set4	(Lcom/android/server/BluetoothManagerService;Landroid/bluetooth/IBluetoothGatt;)Landroid/bluetooth/IBluetoothGatt;
      //   2745: pop
      //   2746: aload_0
      //   2747: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2750: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2753: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2756: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2759: iload_2
      //   2760: ifeq +122 -> 2882
      //   2763: ldc2_w 111
      //   2766: invokestatic 478	android/os/SystemClock:sleep	(J)V
      //   2769: aload_0
      //   2770: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2773: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2776: bipush 41
      //   2778: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   2781: aload_0
      //   2782: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2785: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2788: bipush 60
      //   2790: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   2793: aload_0
      //   2794: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2797: bipush 10
      //   2799: invokestatic 415	com/android/server/BluetoothManagerService:-set9	(Lcom/android/server/BluetoothManagerService;I)I
      //   2802: pop
      //   2803: aload_0
      //   2804: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2807: aload_0
      //   2808: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2811: invokestatic 237	com/android/server/BluetoothManagerService:-get14	(Lcom/android/server/BluetoothManagerService;)Z
      //   2814: invokestatic 241	com/android/server/BluetoothManagerService:-wrap9	(Lcom/android/server/BluetoothManagerService;Z)V
      //   2817: return
      //   2818: astore_1
      //   2819: ldc 27
      //   2821: ldc_w 480
      //   2824: aload_1
      //   2825: invokestatic 152	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   2828: pop
      //   2829: aload_0
      //   2830: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2833: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2836: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   2839: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   2842: goto -336 -> 2506
      //   2845: astore_1
      //   2846: aload_0
      //   2847: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2850: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2853: invokevirtual 202	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
      //   2856: invokevirtual 231	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
      //   2859: aload_1
      //   2860: athrow
      //   2861: iconst_1
      //   2862: istore_2
      //   2863: goto -197 -> 2666
      //   2866: astore_1
      //   2867: aload_0
      //   2868: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2871: invokestatic 63	com/android/server/BluetoothManagerService:-get4	(Lcom/android/server/BluetoothManagerService;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
      //   2874: invokevirtual 69	java/util/concurrent/locks/ReentrantReadWriteLock:writeLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
      //   2877: invokevirtual 100	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
      //   2880: aload_1
      //   2881: athrow
      //   2882: ldc2_w 481
      //   2885: invokestatic 478	android/os/SystemClock:sleep	(J)V
      //   2888: goto -107 -> 2781
      //   2891: aload_0
      //   2892: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2895: invokestatic 82	com/android/server/BluetoothManagerService:-get0	(Lcom/android/server/BluetoothManagerService;)Z
      //   2898: ifne +13 -> 2911
      //   2901: aload_0
      //   2902: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2905: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   2908: ifnull -2720 -> 188
      //   2911: aload_0
      //   2912: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2915: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2918: sipush 300
      //   2921: invokevirtual 110	com/android/server/BluetoothManagerService$BluetoothHandler:obtainMessage	(I)Landroid/os/Message;
      //   2924: astore 7
      //   2926: aload 7
      //   2928: aload_1
      //   2929: getfield 407	android/os/Message:arg2	I
      //   2932: iconst_1
      //   2933: iadd
      //   2934: putfield 407	android/os/Message:arg2	I
      //   2937: aload_0
      //   2938: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2941: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   2944: aload 7
      //   2946: ldc2_w 436
      //   2949: invokevirtual 116	com/android/server/BluetoothManagerService$BluetoothHandler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   2952: pop
      //   2953: ldc 27
      //   2955: new 29	java/lang/StringBuilder
      //   2958: dup
      //   2959: invokespecial 32	java/lang/StringBuilder:<init>	()V
      //   2962: ldc_w 484
      //   2965: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2968: aload 7
      //   2970: getfield 407	android/os/Message:arg2	I
      //   2973: invokevirtual 47	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2976: invokevirtual 51	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2979: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   2982: pop
      //   2983: return
      //   2984: ldc 27
      //   2986: ldc_w 486
      //   2989: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   2992: pop
      //   2993: aload_0
      //   2994: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   2997: invokestatic 106	com/android/server/BluetoothManagerService:-get12	(Lcom/android/server/BluetoothManagerService;)Lcom/android/server/BluetoothManagerService$BluetoothHandler;
      //   3000: sipush 300
      //   3003: invokevirtual 142	com/android/server/BluetoothManagerService$BluetoothHandler:removeMessages	(I)V
      //   3006: aload_0
      //   3007: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   3010: invokestatic 97	com/android/server/BluetoothManagerService:-get9	(Lcom/android/server/BluetoothManagerService;)Z
      //   3013: ifeq -2825 -> 188
      //   3016: aload_0
      //   3017: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   3020: invokestatic 82	com/android/server/BluetoothManagerService:-get0	(Lcom/android/server/BluetoothManagerService;)Z
      //   3023: ifne -2835 -> 188
      //   3026: aload_0
      //   3027: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   3030: invokestatic 78	com/android/server/BluetoothManagerService:-get2	(Lcom/android/server/BluetoothManagerService;)Landroid/bluetooth/IBluetooth;
      //   3033: ifnonnull -2845 -> 188
      //   3036: ldc 27
      //   3038: ldc_w 488
      //   3041: invokestatic 57	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   3044: pop
      //   3045: aload_0
      //   3046: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   3049: aload_0
      //   3050: getfield 15	com/android/server/BluetoothManagerService$BluetoothHandler:this$0	Lcom/android/server/BluetoothManagerService;
      //   3053: invokestatic 237	com/android/server/BluetoothManagerService:-get14	(Lcom/android/server/BluetoothManagerService;)Z
      //   3056: invokestatic 241	com/android/server/BluetoothManagerService:-wrap9	(Lcom/android/server/BluetoothManagerService;Z)V
      //   3059: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	3060	0	this	BluetoothHandler
      //   0	3060	1	paramMessage	Message
      //   456	2407	2	i	int
      //   1715	252	3	j	int
      //   622	11	4	l	long
      //   679	893	6	bool	boolean
      //   668	14	7	localBluetoothManagerService	BluetoothManagerService
      //   714	7	7	localRemoteException	RemoteException
      //   939	2030	7	localObject	Object
      //   968	94	8	localStringBuilder	StringBuilder
      // Exception table:
      //   from	to	target	type
      //   197	230	401	finally
      //   230	238	401	finally
      //   242	273	401	finally
      //   273	290	401	finally
      //   290	295	401	finally
      //   309	398	401	finally
      //   417	426	401	finally
      //   430	439	401	finally
      //   442	449	401	finally
      //   242	273	429	android/os/RemoteException
      //   559	637	714	android/os/RemoteException
      //   559	637	742	finally
      //   716	726	742	finally
      //   764	777	828	android/os/RemoteException
      //   1412	1430	1576	android/os/RemoteException
      //   1433	1459	1576	android/os/RemoteException
      //   1252	1293	1590	finally
      //   1307	1393	1590	finally
      //   1412	1430	1590	finally
      //   1433	1459	1590	finally
      //   1459	1478	1590	finally
      //   1478	1485	1590	finally
      //   1485	1519	1590	finally
      //   1577	1587	1590	finally
      //   1607	1617	1590	finally
      //   1620	1644	1590	finally
      //   1648	1658	1590	finally
      //   1459	1478	1606	android/os/RemoteException
      //   1485	1519	1647	android/os/RemoteException
      //   1620	1644	1647	android/os/RemoteException
      //   2027	2056	2315	finally
      //   2074	2083	2315	finally
      //   2241	2258	2315	finally
      //   2272	2301	2315	finally
      //   2451	2493	2818	android/os/RemoteException
      //   2451	2493	2845	finally
      //   2819	2829	2845	finally
      //   2688	2737	2866	finally
      //   2737	2746	2866	finally
    }
  }
  
  private class BluetoothServiceConnection
    implements ServiceConnection
  {
    private BluetoothServiceConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      Slog.d("BluetoothManagerService", "BluetoothServiceConnection: " + paramComponentName.getClassName());
      Message localMessage = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(40);
      if (paramComponentName.getClassName().equals("com.android.bluetooth.btservice.AdapterService")) {}
      for (localMessage.arg1 = 1;; localMessage.arg1 = 2)
      {
        localMessage.obj = paramIBinder;
        BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessage(localMessage);
        return;
        if (!paramComponentName.getClassName().equals("com.android.bluetooth.gatt.GattService")) {
          break;
        }
      }
      Slog.e("BluetoothManagerService", "Unknown service connected: " + paramComponentName.getClassName());
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      Slog.d("BluetoothManagerService", "BluetoothServiceConnection, disconnected: " + paramComponentName.getClassName());
      Message localMessage = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(41);
      if (paramComponentName.getClassName().equals("com.android.bluetooth.btservice.AdapterService")) {}
      for (localMessage.arg1 = 1;; localMessage.arg1 = 2)
      {
        BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessage(localMessage);
        return;
        if (!paramComponentName.getClassName().equals("com.android.bluetooth.gatt.GattService")) {
          break;
        }
      }
      Slog.e("BluetoothManagerService", "Unknown service disconnected: " + paramComponentName.getClassName());
    }
  }
  
  class ClientDeathRecipient
    implements IBinder.DeathRecipient
  {
    ClientDeathRecipient() {}
    
    public void binderDied()
    {
      Slog.d("BluetoothManagerService", "Binder is dead - unregister Ble App");
      if (BluetoothManagerService.-get1() > 0) {
        BluetoothManagerService.-set1(BluetoothManagerService.-get1() - 1);
      }
      if (BluetoothManagerService.-get1() == 0) {
        Slog.d("BluetoothManagerService", "Disabling LE only mode after application crash");
      }
      try
      {
        BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().lock();
        if ((BluetoothManagerService.-get2(BluetoothManagerService.this) != null) && (BluetoothManagerService.-get2(BluetoothManagerService.this).getState() == 15))
        {
          BluetoothManagerService.-set5(BluetoothManagerService.this, false);
          BluetoothManagerService.-get2(BluetoothManagerService.this).onBrEdrDown();
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("BluetoothManagerService", "Unable to call onBrEdrDown", localRemoteException);
        return;
      }
      finally
      {
        BluetoothManagerService.-get4(BluetoothManagerService.this).readLock().unlock();
      }
    }
  }
  
  private final class ProfileServiceConnections
    implements ServiceConnection, IBinder.DeathRecipient
  {
    ComponentName mClassName = null;
    Intent mIntent;
    boolean mInvokingProxyCallbacks = false;
    final RemoteCallbackList<IBluetoothProfileServiceConnection> mProxies = new RemoteCallbackList();
    IBinder mService = null;
    
    ProfileServiceConnections(Intent paramIntent)
    {
      this.mIntent = paramIntent;
    }
    
    private void addProxy(IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
    {
      this.mProxies.register(paramIBluetoothProfileServiceConnection);
      if (this.mService != null) {}
      while (BluetoothManagerService.-get12(BluetoothManagerService.this).hasMessages(401, this)) {
        try
        {
          paramIBluetoothProfileServiceConnection.onServiceConnected(this.mClassName, this.mService);
          return;
        }
        catch (RemoteException paramIBluetoothProfileServiceConnection)
        {
          Slog.e("BluetoothManagerService", "Unable to connect to proxy", paramIBluetoothProfileServiceConnection);
          return;
        }
      }
      paramIBluetoothProfileServiceConnection = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(401);
      paramIBluetoothProfileServiceConnection.obj = this;
      BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessage(paramIBluetoothProfileServiceConnection);
    }
    
    private boolean bindService()
    {
      if ((this.mIntent != null) && (this.mService == null) && (BluetoothManagerService.this.doBind(this.mIntent, this, 0, UserHandle.CURRENT_OR_SELF)))
      {
        Message localMessage = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(401);
        localMessage.obj = this;
        BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessageDelayed(localMessage, 3000L);
        return true;
      }
      Slog.w("BluetoothManagerService", "Unable to bind with intent: " + this.mIntent);
      return false;
    }
    
    private void removeAllProxies()
    {
      onServiceDisconnected(this.mClassName);
      this.mProxies.kill();
    }
    
    private void removeProxy(IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
    {
      if (paramIBluetoothProfileServiceConnection != null)
      {
        if (this.mProxies.unregister(paramIBluetoothProfileServiceConnection)) {}
        try
        {
          paramIBluetoothProfileServiceConnection.onServiceDisconnected(this.mClassName);
          return;
        }
        catch (RemoteException paramIBluetoothProfileServiceConnection)
        {
          Slog.e("BluetoothManagerService", "Unable to disconnect proxy", paramIBluetoothProfileServiceConnection);
          return;
        }
      }
      Slog.w("BluetoothManagerService", "Trying to remove a null proxy");
    }
    
    public void binderDied()
    {
      Slog.w("BluetoothManagerService", "Profile service for profile: " + this.mClassName + " died.");
      onServiceDisconnected(this.mClassName);
      Message localMessage = BluetoothManagerService.-get12(BluetoothManagerService.this).obtainMessage(401);
      localMessage.obj = this;
      BluetoothManagerService.-get12(BluetoothManagerService.this).sendMessageDelayed(localMessage, 3000L);
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      BluetoothManagerService.-get12(BluetoothManagerService.this).removeMessages(401, this);
      this.mService = paramIBinder;
      this.mClassName = paramComponentName;
      try
      {
        this.mService.linkToDeath(this, 0);
        if (this.mInvokingProxyCallbacks)
        {
          Slog.e("BluetoothManagerService", "Proxy callbacks already in progress.");
          return;
        }
      }
      catch (RemoteException localRemoteException1)
      {
        for (;;)
        {
          Slog.e("BluetoothManagerService", "Unable to linkToDeath", localRemoteException1);
        }
        this.mInvokingProxyCallbacks = true;
        int j = this.mProxies.beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            ((IBluetoothProfileServiceConnection)this.mProxies.getBroadcastItem(i)).onServiceConnected(paramComponentName, paramIBinder);
            i += 1;
          }
          catch (RemoteException localRemoteException2)
          {
            for (;;)
            {
              Slog.e("BluetoothManagerService", "Unable to connect to proxy", localRemoteException2);
            }
          }
          finally
          {
            this.mProxies.finishBroadcast();
            this.mInvokingProxyCallbacks = false;
          }
        }
        this.mProxies.finishBroadcast();
        this.mInvokingProxyCallbacks = false;
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      if (this.mService == null) {
        return;
      }
      try
      {
        this.mService.unlinkToDeath(this, 0);
        this.mService = null;
        this.mClassName = null;
        if (this.mInvokingProxyCallbacks)
        {
          Slog.e("BluetoothManagerService", "Proxy callbacks already in progress.");
          return;
        }
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        for (;;)
        {
          Slog.e("BluetoothManagerService", "Not registered to binder", localNoSuchElementException);
        }
        this.mInvokingProxyCallbacks = true;
        int j = this.mProxies.beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            ((IBluetoothProfileServiceConnection)this.mProxies.getBroadcastItem(i)).onServiceDisconnected(paramComponentName);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.e("BluetoothManagerService", "Unable to disconnect from proxy", localRemoteException);
            }
          }
          finally
          {
            this.mProxies.finishBroadcast();
            this.mInvokingProxyCallbacks = false;
          }
        }
        this.mProxies.finishBroadcast();
        this.mInvokingProxyCallbacks = false;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/BluetoothManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */