package android.bluetooth;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityThread;
import android.app.Application;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter.Builder;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SynchronousResultReceiver;
import android.os.SynchronousResultReceiver.Result;
import android.os.SystemProperties;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.Permission;
import android.util.SeempLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public final class BluetoothAdapter
{
  public static final String ACTION_BLE_ACL_CONNECTED = "android.bluetooth.adapter.action.BLE_ACL_CONNECTED";
  public static final String ACTION_BLE_ACL_DISCONNECTED = "android.bluetooth.adapter.action.BLE_ACL_DISCONNECTED";
  public static final String ACTION_BLE_STATE_CHANGED = "android.bluetooth.adapter.action.BLE_STATE_CHANGED";
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
  public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";
  public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
  public static final String ACTION_REQUEST_BLE_SCAN_ALWAYS_AVAILABLE = "android.bluetooth.adapter.action.REQUEST_BLE_SCAN_ALWAYS_AVAILABLE";
  public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
  public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";
  public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";
  public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
  private static final int ADDRESS_LENGTH = 17;
  public static final String BLUETOOTH_MANAGER_SERVICE = "bluetooth_manager";
  private static final boolean DBG = true;
  public static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";
  public static final int ERROR = Integer.MIN_VALUE;
  public static final String EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE";
  public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";
  public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";
  public static final String EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE";
  public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";
  public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";
  public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";
  public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";
  public static final int SCAN_MODE_CONNECTABLE = 21;
  public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;
  public static final int SCAN_MODE_NONE = 20;
  public static final int SOCKET_CHANNEL_AUTO_STATIC_NO_SDP = -2;
  public static final int STATE_BLE_ON = 15;
  public static final int STATE_BLE_TURNING_OFF = 16;
  public static final int STATE_BLE_TURNING_ON = 14;
  public static final int STATE_CONNECTED = 2;
  public static final int STATE_CONNECTING = 1;
  public static final int STATE_DISCONNECTED = 0;
  public static final int STATE_DISCONNECTING = 3;
  public static final int STATE_OFF = 10;
  public static final int STATE_ON = 12;
  public static final int STATE_TURNING_OFF = 13;
  public static final int STATE_TURNING_ON = 11;
  private static final String TAG = "BluetoothAdapter";
  private static final boolean VDBG = false;
  private static BluetoothAdapter sAdapter;
  private static BluetoothLeAdvertiser sBluetoothLeAdvertiser;
  private static BluetoothLeScanner sBluetoothLeScanner;
  private final Map<LeScanCallback, ScanCallback> mLeScanClients;
  private final Object mLock = new Object();
  private final IBluetoothManagerCallback mManagerCallback = new IBluetoothManagerCallback.Stub()
  {
    public void onBluetoothServiceDown()
    {
      Log.d("BluetoothAdapter", "onBluetoothServiceDown: " + BluetoothAdapter.-get2(BluetoothAdapter.this));
      for (;;)
      {
        try
        {
          BluetoothAdapter.-get3(BluetoothAdapter.this).writeLock().lock();
          BluetoothAdapter.-set0(BluetoothAdapter.this, null);
          if (BluetoothAdapter.-get0(BluetoothAdapter.this) != null) {
            BluetoothAdapter.-get0(BluetoothAdapter.this).clear();
          }
          if (BluetoothAdapter.-get4() != null) {
            BluetoothAdapter.-get4().cleanup();
          }
          if (BluetoothAdapter.-get5() != null) {
            BluetoothAdapter.-get5().cleanup();
          }
          BluetoothAdapter.-get3(BluetoothAdapter.this).writeLock().unlock();
          synchronized (BluetoothAdapter.-get1(BluetoothAdapter.this))
          {
            Log.d("BluetoothAdapter", "onBluetoothServiceDown: Sending callbacks to " + BluetoothAdapter.-get1(BluetoothAdapter.this).size() + " clients");
            Iterator localIterator = BluetoothAdapter.-get1(BluetoothAdapter.this).iterator();
            if (!localIterator.hasNext()) {
              break;
            }
            IBluetoothManagerCallback localIBluetoothManagerCallback = (IBluetoothManagerCallback)localIterator.next();
            if (localIBluetoothManagerCallback != null) {
              try
              {
                localIBluetoothManagerCallback.onBluetoothServiceDown();
              }
              catch (Exception localException)
              {
                Log.e("BluetoothAdapter", "", localException);
              }
            }
          }
          Log.d("BluetoothAdapter", "onBluetoothServiceDown: cb is null!!!");
        }
        finally
        {
          BluetoothAdapter.-get3(BluetoothAdapter.this).writeLock().unlock();
        }
      }
      Log.d("BluetoothAdapter", "onBluetoothServiceDown: Finished sending callbacks to registered clients");
    }
    
    public void onBluetoothServiceUp(IBluetooth paramAnonymousIBluetooth)
    {
      Log.d("BluetoothAdapter", "onBluetoothServiceUp: " + paramAnonymousIBluetooth);
      BluetoothAdapter.-get3(BluetoothAdapter.this).writeLock().lock();
      BluetoothAdapter.-set0(BluetoothAdapter.this, paramAnonymousIBluetooth);
      BluetoothAdapter.-get3(BluetoothAdapter.this).writeLock().unlock();
      for (;;)
      {
        synchronized (BluetoothAdapter.-get1(BluetoothAdapter.this))
        {
          Iterator localIterator = BluetoothAdapter.-get1(BluetoothAdapter.this).iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          IBluetoothManagerCallback localIBluetoothManagerCallback = (IBluetoothManagerCallback)localIterator.next();
          if (localIBluetoothManagerCallback != null) {
            try
            {
              localIBluetoothManagerCallback.onBluetoothServiceUp(paramAnonymousIBluetooth);
            }
            catch (Exception localException)
            {
              Log.e("BluetoothAdapter", "", localException);
            }
          }
        }
        Log.d("BluetoothAdapter", "onBluetoothServiceUp: cb is null!!!");
      }
    }
    
    public void onBrEdrDown()
    {
      Log.i("BluetoothAdapter", "onBrEdrDown:");
    }
  };
  private final IBluetoothManager mManagerService;
  private final ArrayList<IBluetoothManagerCallback> mProxyServiceStateCallbacks = new ArrayList();
  private IBluetooth mService;
  private final ReentrantReadWriteLock mServiceLock = new ReentrantReadWriteLock();
  private final IBinder mToken;
  
  BluetoothAdapter(IBluetoothManager paramIBluetoothManager)
  {
    if (paramIBluetoothManager == null) {
      throw new IllegalArgumentException("bluetooth manager service is null");
    }
    try
    {
      this.mServiceLock.writeLock().lock();
      this.mService = paramIBluetoothManager.registerAdapter(this.mManagerCallback);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("BluetoothAdapter", "", localRemoteException);
        this.mServiceLock.writeLock().unlock();
      }
    }
    finally
    {
      this.mServiceLock.writeLock().unlock();
    }
    this.mManagerService = paramIBluetoothManager;
    this.mLeScanClients = new HashMap();
    this.mToken = new Binder();
  }
  
  public static boolean checkBluetoothAddress(String paramString)
  {
    if ((paramString == null) || (paramString.length() != 17)) {
      return false;
    }
    int i = 0;
    if (i < 17)
    {
      int j = paramString.charAt(i);
      switch (i % 3)
      {
      }
      do
      {
        do
        {
          i += 1;
          break;
        } while (((j >= 48) && (j <= 57)) || ((j >= 65) && (j <= 70)));
        return false;
      } while (j == 58);
      return false;
    }
    return true;
  }
  
  private BluetoothServerSocket createNewRfcommSocketAndRecord(String paramString, UUID paramUUID, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    paramUUID = new BluetoothServerSocket(1, paramBoolean1, paramBoolean2, new ParcelUuid(paramUUID));
    paramUUID.setServiceName(paramString);
    int i = paramUUID.mSocket.bindListen();
    if (i != 0) {
      throw new IOException("Error: " + i);
    }
    return paramUUID;
  }
  
  private String getCurProcessName()
  {
    Object localObject = ActivityThread.currentApplication().getApplicationContext();
    int i = Process.myPid();
    localObject = ((ActivityManager)((Context)localObject).getSystemService("activity")).getRunningAppProcesses().iterator();
    while (((Iterator)localObject).hasNext())
    {
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject).next();
      if (localRunningAppProcessInfo.pid == i) {
        return localRunningAppProcessInfo.processName;
      }
    }
    return null;
  }
  
  /* Error */
  public static BluetoothAdapter getDefaultAdapter()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 355	android/bluetooth/BluetoothAdapter:sAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   6: ifnonnull +27 -> 33
    //   9: ldc 60
    //   11: invokestatic 361	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   14: astore_0
    //   15: aload_0
    //   16: ifnull +26 -> 42
    //   19: new 2	android/bluetooth/BluetoothAdapter
    //   22: dup
    //   23: aload_0
    //   24: invokestatic 367	android/bluetooth/IBluetoothManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/bluetooth/IBluetoothManager;
    //   27: invokespecial 369	android/bluetooth/BluetoothAdapter:<init>	(Landroid/bluetooth/IBluetoothManager;)V
    //   30: putstatic 355	android/bluetooth/BluetoothAdapter:sAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   33: getstatic 355	android/bluetooth/BluetoothAdapter:sAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   36: astore_0
    //   37: ldc 2
    //   39: monitorexit
    //   40: aload_0
    //   41: areturn
    //   42: ldc 124
    //   44: ldc_w 371
    //   47: invokestatic 374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   50: pop
    //   51: goto -18 -> 33
    //   54: astore_0
    //   55: ldc 2
    //   57: monitorexit
    //   58: aload_0
    //   59: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   14	27	0	localObject1	Object
    //   54	5	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	15	54	finally
    //   19	33	54	finally
    //   33	37	54	finally
    //   42	51	54	finally
  }
  
  public static BluetoothServerSocket listenUsingScoOn()
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(2, false, false, -1);
    if (localBluetoothServerSocket.mSocket.bindListen() < 0) {}
    return localBluetoothServerSocket;
  }
  
  /* Error */
  private void notifyUserAction(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 166	android/bluetooth/BluetoothAdapter:mServiceLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   4: invokevirtual 385	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   7: invokevirtual 388	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:lock	()V
    //   10: aload_0
    //   11: getfield 162	android/bluetooth/BluetoothAdapter:mService	Landroid/bluetooth/IBluetooth;
    //   14: ifnonnull +23 -> 37
    //   17: ldc 124
    //   19: ldc_w 390
    //   22: invokestatic 374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   25: pop
    //   26: aload_0
    //   27: getfield 166	android/bluetooth/BluetoothAdapter:mServiceLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   30: invokevirtual 385	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   33: invokevirtual 391	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   36: return
    //   37: iload_1
    //   38: ifeq +23 -> 61
    //   41: aload_0
    //   42: getfield 162	android/bluetooth/BluetoothAdapter:mService	Landroid/bluetooth/IBluetooth;
    //   45: invokeinterface 396 1 0
    //   50: aload_0
    //   51: getfield 166	android/bluetooth/BluetoothAdapter:mServiceLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   54: invokevirtual 385	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   57: invokevirtual 391	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   60: return
    //   61: aload_0
    //   62: getfield 162	android/bluetooth/BluetoothAdapter:mService	Landroid/bluetooth/IBluetooth;
    //   65: invokeinterface 399 1 0
    //   70: goto -20 -> 50
    //   73: astore_2
    //   74: ldc 124
    //   76: ldc -23
    //   78: aload_2
    //   79: invokestatic 239	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   82: pop
    //   83: aload_0
    //   84: getfield 166	android/bluetooth/BluetoothAdapter:mServiceLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   87: invokevirtual 385	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   90: invokevirtual 391	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   93: return
    //   94: astore_2
    //   95: aload_0
    //   96: getfield 166	android/bluetooth/BluetoothAdapter:mServiceLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock;
    //   99: invokevirtual 385	java/util/concurrent/locks/ReentrantReadWriteLock:readLock	()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   102: invokevirtual 391	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   105: aload_2
    //   106: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	this	BluetoothAdapter
    //   0	107	1	paramBoolean	boolean
    //   73	6	2	localRemoteException	RemoteException
    //   94	12	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	26	73	android/os/RemoteException
    //   41	50	73	android/os/RemoteException
    //   61	70	73	android/os/RemoteException
    //   0	26	94	finally
    //   41	50	94	finally
    //   61	70	94	finally
    //   74	83	94	finally
  }
  
  private Set<BluetoothDevice> toDeviceSet(BluetoothDevice[] paramArrayOfBluetoothDevice)
  {
    return Collections.unmodifiableSet(new HashSet(Arrays.asList(paramArrayOfBluetoothDevice)));
  }
  
  public boolean cancelDiscovery()
  {
    if (getState() != 12) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.cancelDiscovery();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean changeApplicationBluetoothState(boolean paramBoolean, BluetoothStateChangeCallback paramBluetoothStateChangeCallback)
  {
    return paramBluetoothStateChangeCallback != null;
  }
  
  public void closeProfileProxy(int paramInt, BluetoothProfile paramBluetoothProfile)
  {
    if (paramBluetoothProfile == null) {
      return;
    }
    switch (paramInt)
    {
    case 6: 
    case 13: 
    case 14: 
    case 15: 
    case 18: 
    case 19: 
    case 20: 
    default: 
      return;
    case 1: 
      ((BluetoothHeadset)paramBluetoothProfile).close();
      return;
    case 2: 
      ((BluetoothA2dp)paramBluetoothProfile).close();
      return;
    case 11: 
      ((BluetoothA2dpSink)paramBluetoothProfile).close();
      return;
    case 12: 
      ((BluetoothAvrcpController)paramBluetoothProfile).close();
      return;
    case 4: 
      ((BluetoothInputDevice)paramBluetoothProfile).close();
      return;
    case 5: 
      ((BluetoothPan)paramBluetoothProfile).close();
      return;
    case 21: 
      ((BluetoothDun)paramBluetoothProfile).close();
      return;
    case 3: 
      ((BluetoothHealth)paramBluetoothProfile).close();
      return;
    case 7: 
      ((BluetoothGatt)paramBluetoothProfile).close();
      return;
    case 8: 
      ((BluetoothGattServer)paramBluetoothProfile).close();
      return;
    case 9: 
      ((BluetoothMap)paramBluetoothProfile).close();
      return;
    case 16: 
      ((BluetoothHeadsetClient)paramBluetoothProfile).close();
      return;
    case 10: 
      ((BluetoothSap)paramBluetoothProfile).close();
      return;
    }
    ((BluetoothPbapClient)paramBluetoothProfile).close();
  }
  
  public boolean configHciSnoopLog(boolean paramBoolean)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        paramBoolean = this.mService.configHciSnoopLog(paramBoolean);
        return paramBoolean;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("BluetoothAdapter", "", localRemoteException);
        this.mServiceLock.readLock().unlock();
      }
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
    return false;
  }
  
  public boolean disable()
  {
    SeempLog.record(57);
    try
    {
      boolean bool = this.mManagerService.disable(true);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean disable(boolean paramBoolean)
  {
    SeempLog.record(57);
    try
    {
      paramBoolean = this.mManagerService.disable(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean disableBLE()
  {
    if (!isBleScanAlwaysAvailable()) {
      return false;
    }
    int i = getLeState();
    if (i == 12)
    {
      Log.d("BluetoothAdapter", "STATE_ON: shouldn't disable");
      try
      {
        this.mManagerService.updateBleAppCount(this.mToken, false);
        return true;
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("BluetoothAdapter", "", localRemoteException1);
        return true;
      }
    }
    if (i == 15)
    {
      Log.d("BluetoothAdapter", "STATE_BLE_ON");
      i = 0;
      try
      {
        int j = this.mManagerService.updateBleAppCount(this.mToken, false);
        i = j;
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;)
        {
          Log.e("BluetoothAdapter", "", localRemoteException2);
        }
      }
      if (i == 0) {
        notifyUserAction(false);
      }
      return true;
    }
    Log.d("BluetoothAdapter", "STATE_OFF: Already disabled");
    return false;
  }
  
  public boolean enable()
  {
    if ((OpFeatures.isSupport(new int[] { 12 })) && (!new Permission(ActivityThread.currentApplication().getApplicationContext()).requestPermissionAuto("CUSTOM_PERMISSION_CONTROL_BLUETOOTH"))) {
      return false;
    }
    SeempLog.record(56);
    if (isEnabled())
    {
      Log.d("BluetoothAdapter", "enable(): BT is already enabled..!");
      return true;
    }
    try
    {
      boolean bool = this.mManagerService.enable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean enableBLE()
  {
    if (!isBleScanAlwaysAvailable()) {
      return false;
    }
    try
    {
      this.mManagerService.updateBleAppCount(this.mToken, true);
      if (isLeEnabled())
      {
        Log.d("BluetoothAdapter", "enableBLE(): Bluetooth already enabled");
        return true;
      }
      Log.d("BluetoothAdapter", "enableBLE(): Calling enable");
      boolean bool = this.mManagerService.enable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean enableNoAutoConnect()
  {
    if (isEnabled())
    {
      Log.d("BluetoothAdapter", "enableNoAutoConnect(): BT is already enabled..!");
      return true;
    }
    try
    {
      boolean bool = this.mManagerService.enableNoAutoConnect();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean factoryReset()
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.factoryReset();
        return bool;
      }
      SystemProperties.set("persist.bluetooth.factoryreset", "true");
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("BluetoothAdapter", "", localRemoteException);
        this.mServiceLock.readLock().unlock();
      }
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
    return false;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mManagerService.unregisterAdapter(this.mManagerCallback);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public String getAddress()
  {
    try
    {
      String str = this.mManagerService.getAddress();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return null;
  }
  
  public BluetoothLeAdvertiser getBluetoothLeAdvertiser()
  {
    if (!getLeAccess()) {
      return null;
    }
    if ((isMultipleAdvertisementSupported()) || (isPeripheralModeSupported())) {}
    synchronized (this.mLock)
    {
      if (sBluetoothLeAdvertiser == null) {
        sBluetoothLeAdvertiser = new BluetoothLeAdvertiser(this.mManagerService);
      }
      return sBluetoothLeAdvertiser;
      Log.e("BluetoothAdapter", "Bluetooth LE advertising not supported");
      return null;
    }
  }
  
  public BluetoothLeScanner getBluetoothLeScanner()
  {
    if (!getLeAccess()) {
      return null;
    }
    synchronized (this.mLock)
    {
      if (sBluetoothLeScanner == null) {
        sBluetoothLeScanner = new BluetoothLeScanner(this.mManagerService);
      }
      return sBluetoothLeScanner;
    }
  }
  
  IBluetoothManager getBluetoothManager()
  {
    return this.mManagerService;
  }
  
  IBluetooth getBluetoothService(IBluetoothManagerCallback paramIBluetoothManagerCallback)
  {
    localArrayList = this.mProxyServiceStateCallbacks;
    if (paramIBluetoothManagerCallback == null) {}
    for (;;)
    {
      try
      {
        Log.w("BluetoothAdapter", "getBluetoothService() called with no BluetoothManagerCallback");
        return this.mService;
      }
      finally {}
      if (!this.mProxyServiceStateCallbacks.contains(paramIBluetoothManagerCallback)) {
        this.mProxyServiceStateCallbacks.add(paramIBluetoothManagerCallback);
      }
    }
  }
  
  public Set<BluetoothDevice> getBondedDevices()
  {
    SeempLog.record(61);
    if (getState() != 12) {
      return toDeviceSet(new BluetoothDevice[0]);
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        localSet = toDeviceSet(this.mService.getBondedDevices());
        return localSet;
      }
      Set localSet = toDeviceSet(new BluetoothDevice[0]);
      return localSet;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return null;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public int getConnectionState()
  {
    if (getState() != 12) {
      return 0;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        int i = this.mService.getAdapterConnectionState();
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "getConnectionState:", localRemoteException);
      return 0;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  @Deprecated
  public BluetoothActivityEnergyInfo getControllerActivityEnergyInfo(int paramInt)
  {
    Object localObject = new SynchronousResultReceiver();
    requestControllerActivityEnergyInfo((ResultReceiver)localObject);
    try
    {
      localObject = ((SynchronousResultReceiver)localObject).awaitResult(1000L);
      if (((SynchronousResultReceiver.Result)localObject).bundle != null)
      {
        localObject = (BluetoothActivityEnergyInfo)((SynchronousResultReceiver.Result)localObject).bundle.getParcelable("controller_activity");
        return (BluetoothActivityEnergyInfo)localObject;
      }
    }
    catch (TimeoutException localTimeoutException)
    {
      Log.e("BluetoothAdapter", "getControllerActivityEnergyInfo timed out");
    }
    return null;
  }
  
  public int getDiscoverableTimeout()
  {
    if (getState() != 12) {
      return -1;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        int i = this.mService.getDiscoverableTimeout();
        return i;
      }
      return -1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return -1;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  boolean getLeAccess()
  {
    if (getLeState() == 12) {
      return true;
    }
    return getLeState() == 15;
  }
  
  public int getLeState()
  {
    int i = 10;
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null) {
        i = this.mService.getState();
      }
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return 10;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public String getName()
  {
    try
    {
      String str = this.mManagerService.getName();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return null;
  }
  
  public int getProfileConnectionState(int paramInt)
  {
    SeempLog.record(64);
    if (getState() != 12) {
      return 0;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        paramInt = this.mService.getProfileConnectionState(paramInt);
        return paramInt;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "getProfileConnectionState:", localRemoteException);
      return 0;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean getProfileProxy(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener, int paramInt)
  {
    if ((paramContext == null) || (paramServiceListener == null)) {
      return false;
    }
    if (paramInt == 1)
    {
      new BluetoothHeadset(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 2)
    {
      new BluetoothA2dp(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 11)
    {
      new BluetoothA2dpSink(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 12)
    {
      new BluetoothAvrcpController(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 4)
    {
      new BluetoothInputDevice(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 5)
    {
      new BluetoothPan(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 21)
    {
      new BluetoothDun(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 3)
    {
      new BluetoothHealth(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 9)
    {
      new BluetoothMap(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 16)
    {
      new BluetoothHeadsetClient(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 10)
    {
      new BluetoothSap(paramContext, paramServiceListener);
      return true;
    }
    if (paramInt == 17)
    {
      new BluetoothPbapClient(paramContext, paramServiceListener);
      return true;
    }
    return false;
  }
  
  public BluetoothDevice getRemoteDevice(String paramString)
  {
    SeempLog.record(62);
    return new BluetoothDevice(paramString);
  }
  
  public BluetoothDevice getRemoteDevice(byte[] paramArrayOfByte)
  {
    SeempLog.record(62);
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length != 6)) {
      throw new IllegalArgumentException("Bluetooth address must have 6 bytes");
    }
    return new BluetoothDevice(String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", new Object[] { Byte.valueOf(paramArrayOfByte[0]), Byte.valueOf(paramArrayOfByte[1]), Byte.valueOf(paramArrayOfByte[2]), Byte.valueOf(paramArrayOfByte[3]), Byte.valueOf(paramArrayOfByte[4]), Byte.valueOf(paramArrayOfByte[5]) }));
  }
  
  public int getScanMode()
  {
    if (getState() != 12) {
      return 20;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        int i = this.mService.getScanMode();
        return i;
      }
      return 20;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return 20;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public int getState()
  {
    int j = 10;
    SeempLog.record(63);
    int i;
    try
    {
      this.mServiceLock.readLock().lock();
      i = j;
      if (this.mService != null) {
        i = this.mService.getState();
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("BluetoothAdapter", "", localRemoteException);
        this.mServiceLock.readLock().unlock();
        i = j;
      }
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
    if ((i == 15) || (i == 14)) {}
    for (;;)
    {
      j = 10;
      do
      {
        return j;
        j = i;
      } while (i != 16);
    }
  }
  
  public ParcelUuid[] getUuids()
  {
    if (getState() != 12) {
      return null;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        ParcelUuid[] arrayOfParcelUuid = this.mService.getUuids();
        return arrayOfParcelUuid;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return null;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isBleScanAlwaysAvailable()
  {
    try
    {
      boolean bool = this.mManagerService.isBleScanAlwaysAvailable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "remote expection when calling isBleScanAlwaysAvailable", localRemoteException);
    }
    return false;
  }
  
  public boolean isDiscovering()
  {
    if (getState() != 12) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isDiscovering();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isEnabled()
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isEnabled();
        return bool;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("BluetoothAdapter", "", localRemoteException);
        this.mServiceLock.readLock().unlock();
      }
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
    return false;
  }
  
  public boolean isHardwareTrackingFiltersAvailable()
  {
    boolean bool = false;
    if (!getLeAccess()) {
      return false;
    }
    try
    {
      IBluetoothGatt localIBluetoothGatt = this.mManagerService.getBluetoothGatt();
      if (localIBluetoothGatt == null) {
        return false;
      }
      int i = localIBluetoothGatt.numHwTrackFiltersAvailable();
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
    }
    return false;
  }
  
  public boolean isLeEnabled()
  {
    int i = getLeState();
    if (i == 12) {
      Log.d("BluetoothAdapter", "STATE_ON");
    }
    for (;;)
    {
      return true;
      if (i != 15) {
        break;
      }
      Log.d("BluetoothAdapter", "STATE_BLE_ON");
    }
    Log.d("BluetoothAdapter", "STATE_OFF");
    return false;
  }
  
  public boolean isMultipleAdvertisementSupported()
  {
    if (!getLeAccess()) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isMultiAdvertisementSupported();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "failed to get isMultipleAdvertisementSupported, error: ", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isOffloadedFilteringSupported()
  {
    if (!getLeAccess()) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isOffloadedFilteringSupported();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "failed to get isOffloadedFilteringSupported, error: ", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isOffloadedScanBatchingSupported()
  {
    if (!getLeAccess()) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isOffloadedScanBatchingSupported();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "failed to get isOffloadedScanBatchingSupported, error: ", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isPeripheralModeSupported()
  {
    if (!getLeAccess()) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.isPeripheralModeSupported();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "failed to get peripheral mode capability: ", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public BluetoothServerSocket listenUsingEncryptedRfcommOn(int paramInt)
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(1, false, true, paramInt);
    int i = localBluetoothServerSocket.mSocket.bindListen();
    if (paramInt == -2) {
      localBluetoothServerSocket.setChannel(localBluetoothServerSocket.mSocket.getPort());
    }
    if (i < 0) {
      throw new IOException("Error: " + i);
    }
    return localBluetoothServerSocket;
  }
  
  public BluetoothServerSocket listenUsingEncryptedRfcommWithServiceRecord(String paramString, UUID paramUUID)
    throws IOException
  {
    return createNewRfcommSocketAndRecord(paramString, paramUUID, false, true);
  }
  
  public BluetoothServerSocket listenUsingInsecureL2capOn(int paramInt)
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(3, false, false, paramInt, false, false);
    int i = localBluetoothServerSocket.mSocket.bindListen();
    if (paramInt == -2) {
      localBluetoothServerSocket.setChannel(localBluetoothServerSocket.mSocket.getPort());
    }
    if (i != 0) {
      throw new IOException("Error: " + i);
    }
    return localBluetoothServerSocket;
  }
  
  public BluetoothServerSocket listenUsingInsecureRfcommOn(int paramInt)
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(1, false, false, paramInt);
    int i = localBluetoothServerSocket.mSocket.bindListen();
    if (paramInt == -2) {
      localBluetoothServerSocket.setChannel(localBluetoothServerSocket.mSocket.getPort());
    }
    if (i != 0) {
      throw new IOException("Error: " + i);
    }
    return localBluetoothServerSocket;
  }
  
  public BluetoothServerSocket listenUsingInsecureRfcommWithServiceRecord(String paramString, UUID paramUUID)
    throws IOException
  {
    SeempLog.record(59);
    return createNewRfcommSocketAndRecord(paramString, paramUUID, false, false);
  }
  
  public BluetoothServerSocket listenUsingL2capOn(int paramInt)
    throws IOException
  {
    return listenUsingL2capOn(paramInt, false, false);
  }
  
  public BluetoothServerSocket listenUsingL2capOn(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(3, true, true, paramInt, paramBoolean1, paramBoolean2);
    int i = localBluetoothServerSocket.mSocket.bindListen();
    if (paramInt == -2) {
      localBluetoothServerSocket.setChannel(localBluetoothServerSocket.mSocket.getPort());
    }
    if (i != 0) {
      throw new IOException("Error: " + i);
    }
    return localBluetoothServerSocket;
  }
  
  public BluetoothServerSocket listenUsingRfcommOn(int paramInt)
    throws IOException
  {
    return listenUsingRfcommOn(paramInt, false, false);
  }
  
  public BluetoothServerSocket listenUsingRfcommOn(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    BluetoothServerSocket localBluetoothServerSocket = new BluetoothServerSocket(1, true, true, paramInt, paramBoolean1, paramBoolean2);
    int i = localBluetoothServerSocket.mSocket.bindListen();
    if (paramInt == -2) {
      localBluetoothServerSocket.setChannel(localBluetoothServerSocket.mSocket.getPort());
    }
    if (i != 0) {
      throw new IOException("Error: " + i);
    }
    return localBluetoothServerSocket;
  }
  
  public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String paramString, UUID paramUUID)
    throws IOException
  {
    return createNewRfcommSocketAndRecord(paramString, paramUUID, true, true);
  }
  
  public Pair<byte[], byte[]> readOutOfBandData()
  {
    if (getState() != 12) {
      return null;
    }
    return null;
  }
  
  void removeServiceStateCallback(IBluetoothManagerCallback paramIBluetoothManagerCallback)
  {
    synchronized (this.mProxyServiceStateCallbacks)
    {
      this.mProxyServiceStateCallbacks.remove(paramIBluetoothManagerCallback);
      return;
    }
  }
  
  public void requestControllerActivityEnergyInfo(ResultReceiver paramResultReceiver)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      ResultReceiver localResultReceiver = paramResultReceiver;
      if (this.mService != null)
      {
        this.mService.requestActivityInfo(paramResultReceiver);
        localResultReceiver = null;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "getControllerActivityEnergyInfoCallback: " + localRemoteException);
      return;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
      if (paramResultReceiver != null) {
        paramResultReceiver.send(0, null);
      }
    }
  }
  
  public void setDiscoverableTimeout(int paramInt)
  {
    if (getState() != 12) {
      return;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null) {
        this.mService.setDiscoverableTimeout(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean setName(String paramString)
  {
    if (getState() != 12) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.setName(paramString);
        return bool;
      }
      return false;
    }
    catch (RemoteException paramString)
    {
      Log.e("BluetoothAdapter", "", paramString);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean setScanMode(int paramInt)
  {
    if (getState() != 12) {
      return false;
    }
    return setScanMode(paramInt, getDiscoverableTimeout());
  }
  
  public boolean setScanMode(int paramInt1, int paramInt2)
  {
    if (getState() != 12) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.setScanMode(paramInt1, paramInt2);
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean startDiscovery()
  {
    SeempLog.record(58);
    if (getState() != 12) {
      return false;
    }
    try
    {
      this.mServiceLock.readLock().lock();
      if (this.mService != null)
      {
        boolean bool = this.mService.startDiscovery();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothAdapter", "", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  @Deprecated
  public boolean startLeScan(LeScanCallback paramLeScanCallback)
  {
    Log.d("BluetoothAdapter", "get call process name: " + getCurProcessName());
    return startLeScan(null, paramLeScanCallback);
  }
  
  @Deprecated
  public boolean startLeScan(final UUID[] paramArrayOfUUID, final LeScanCallback paramLeScanCallback)
  {
    Log.d("BluetoothAdapter", "startLeScan(): " + Arrays.toString(paramArrayOfUUID));
    Log.d("BluetoothAdapter", "get call process name: " + getCurProcessName());
    if (paramLeScanCallback == null)
    {
      Log.e("BluetoothAdapter", "startLeScan: null callback");
      return false;
    }
    BluetoothLeScanner localBluetoothLeScanner = getBluetoothLeScanner();
    if (localBluetoothLeScanner == null)
    {
      Log.e("BluetoothAdapter", "startLeScan: cannot get BluetoothLeScanner");
      return false;
    }
    synchronized (this.mLeScanClients)
    {
      if (this.mLeScanClients.containsKey(paramLeScanCallback))
      {
        Log.e("BluetoothAdapter", "LE Scan has already started");
        return false;
      }
      try
      {
        Object localObject = this.mManagerService.getBluetoothGatt();
        if (localObject == null) {
          return false;
        }
        localObject = new ScanCallback()
        {
          public void onScanResult(int paramAnonymousInt, ScanResult paramAnonymousScanResult)
          {
            if (paramAnonymousInt != 1)
            {
              Log.e("BluetoothAdapter", "LE Scan has already started");
              return;
            }
            ScanRecord localScanRecord = paramAnonymousScanResult.getScanRecord();
            if (localScanRecord == null) {
              return;
            }
            if (paramArrayOfUUID != null)
            {
              ArrayList localArrayList = new ArrayList();
              Object localObject = paramArrayOfUUID;
              paramAnonymousInt = 0;
              int i = localObject.length;
              while (paramAnonymousInt < i)
              {
                localArrayList.add(new ParcelUuid(localObject[paramAnonymousInt]));
                paramAnonymousInt += 1;
              }
              localObject = localScanRecord.getServiceUuids();
              if ((localObject == null) || (!((List)localObject).containsAll(localArrayList))) {}
            }
            else
            {
              paramLeScanCallback.onLeScan(paramAnonymousScanResult.getDevice(), paramAnonymousScanResult.getRssi(), localScanRecord.getBytes());
              return;
            }
            Log.d("BluetoothAdapter", "uuids does not match");
          }
        };
        ScanSettings localScanSettings = new ScanSettings.Builder().setCallbackType(1).setScanMode(2).build();
        ArrayList localArrayList = new ArrayList();
        if ((paramArrayOfUUID != null) && (paramArrayOfUUID.length > 0)) {
          localArrayList.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(paramArrayOfUUID[0])).build());
        }
        localBluetoothLeScanner.startScan(localArrayList, localScanSettings, (ScanCallback)localObject);
        this.mLeScanClients.put(paramLeScanCallback, localObject);
        return true;
      }
      catch (RemoteException paramArrayOfUUID)
      {
        Log.e("BluetoothAdapter", "", paramArrayOfUUID);
        return false;
      }
    }
  }
  
  @Deprecated
  public void stopLeScan(LeScanCallback paramLeScanCallback)
  {
    Log.d("BluetoothAdapter", "stopLeScan()");
    BluetoothLeScanner localBluetoothLeScanner = getBluetoothLeScanner();
    if (localBluetoothLeScanner == null) {
      return;
    }
    synchronized (this.mLeScanClients)
    {
      paramLeScanCallback = (ScanCallback)this.mLeScanClients.remove(paramLeScanCallback);
      if (paramLeScanCallback == null)
      {
        Log.d("BluetoothAdapter", "scan not started yet");
        return;
      }
      localBluetoothLeScanner.stopScan(paramLeScanCallback);
      return;
    }
  }
  
  public static abstract interface BluetoothStateChangeCallback
  {
    public abstract void onBluetoothStateChange(boolean paramBoolean);
  }
  
  public static abstract interface LeScanCallback
  {
    public abstract void onLeScan(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte);
  }
  
  public class StateChangeCallbackWrapper
    extends IBluetoothStateChangeCallback.Stub
  {
    private BluetoothAdapter.BluetoothStateChangeCallback mCallback;
    
    StateChangeCallbackWrapper(BluetoothAdapter.BluetoothStateChangeCallback paramBluetoothStateChangeCallback)
    {
      this.mCallback = paramBluetoothStateChangeCallback;
    }
    
    public void onBluetoothStateChange(boolean paramBoolean)
    {
      this.mCallback.onBluetoothStateChange(paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */