package android.bluetooth;

import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class BluetoothGattServer
  implements BluetoothProfile
{
  private static final int CALLBACK_REG_TIMEOUT = 10000;
  private static final boolean DBG = true;
  private static final String TAG = "BluetoothGattServer";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothGattServerCallback mBluetoothGattServerCallback = new IBluetoothGattServerCallback.Stub()
  {
    public void onCharacteristicReadRequest(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean, int paramAnonymousInt3, int paramAnonymousInt4, ParcelUuid paramAnonymousParcelUuid1, int paramAnonymousInt5, ParcelUuid paramAnonymousParcelUuid2)
    {
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getUuid();
      paramAnonymousParcelUuid2 = paramAnonymousParcelUuid2.getUuid();
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      paramAnonymousParcelUuid1 = BluetoothGattServer.this.getService(paramAnonymousParcelUuid1, paramAnonymousInt4, paramAnonymousInt3);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getCharacteristic(paramAnonymousParcelUuid2);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onCharacteristicReadRequest(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousParcelUuid1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onCharacteristicWriteRequest(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt4, int paramAnonymousInt5, ParcelUuid paramAnonymousParcelUuid1, int paramAnonymousInt6, ParcelUuid paramAnonymousParcelUuid2, byte[] paramAnonymousArrayOfByte)
    {
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getUuid();
      paramAnonymousParcelUuid2 = paramAnonymousParcelUuid2.getUuid();
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      paramAnonymousParcelUuid1 = BluetoothGattServer.this.getService(paramAnonymousParcelUuid1, paramAnonymousInt5, paramAnonymousInt4);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getCharacteristic(paramAnonymousParcelUuid2);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onCharacteristicWriteRequest(paramAnonymousString, paramAnonymousInt1, paramAnonymousParcelUuid1, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousInt2, paramAnonymousArrayOfByte);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onDescriptorReadRequest(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean, int paramAnonymousInt3, int paramAnonymousInt4, ParcelUuid paramAnonymousParcelUuid1, int paramAnonymousInt5, ParcelUuid paramAnonymousParcelUuid2, ParcelUuid paramAnonymousParcelUuid3)
    {
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getUuid();
      paramAnonymousParcelUuid2 = paramAnonymousParcelUuid2.getUuid();
      paramAnonymousParcelUuid3 = paramAnonymousParcelUuid3.getUuid();
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      paramAnonymousParcelUuid1 = BluetoothGattServer.this.getService(paramAnonymousParcelUuid1, paramAnonymousInt4, paramAnonymousInt3);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getCharacteristic(paramAnonymousParcelUuid2);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getDescriptor(paramAnonymousParcelUuid3);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onDescriptorReadRequest(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousParcelUuid1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onDescriptorWriteRequest(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt4, int paramAnonymousInt5, ParcelUuid paramAnonymousParcelUuid1, int paramAnonymousInt6, ParcelUuid paramAnonymousParcelUuid2, ParcelUuid paramAnonymousParcelUuid3, byte[] paramAnonymousArrayOfByte)
    {
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getUuid();
      paramAnonymousParcelUuid2 = paramAnonymousParcelUuid2.getUuid();
      paramAnonymousParcelUuid3 = paramAnonymousParcelUuid3.getUuid();
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      paramAnonymousParcelUuid1 = BluetoothGattServer.this.getService(paramAnonymousParcelUuid1, paramAnonymousInt5, paramAnonymousInt4);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getCharacteristic(paramAnonymousParcelUuid2);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      paramAnonymousParcelUuid1 = paramAnonymousParcelUuid1.getDescriptor(paramAnonymousParcelUuid3);
      if (paramAnonymousParcelUuid1 == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onDescriptorWriteRequest(paramAnonymousString, paramAnonymousInt1, paramAnonymousParcelUuid1, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousInt2, paramAnonymousArrayOfByte);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onExecuteWrite(String paramAnonymousString, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothGattServer", "onExecuteWrite() - device=" + paramAnonymousString + ", transId=" + paramAnonymousInt + "execWrite=" + paramAnonymousBoolean);
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      if (paramAnonymousString == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onExecuteWrite(paramAnonymousString, paramAnonymousInt, paramAnonymousBoolean);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onMtuChanged(String paramAnonymousString, int paramAnonymousInt)
    {
      Log.d("BluetoothGattServer", "onMtuChanged() - device=" + paramAnonymousString + ", mtu=" + paramAnonymousInt);
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      if (paramAnonymousString == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onMtuChanged(paramAnonymousString, paramAnonymousInt);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception: " + paramAnonymousString);
      }
    }
    
    public void onNotificationSent(String paramAnonymousString, int paramAnonymousInt)
    {
      paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
      if (paramAnonymousString == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onNotificationSent(paramAnonymousString, paramAnonymousInt);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception: " + paramAnonymousString);
      }
    }
    
    public void onScanResult(String paramAnonymousString, int paramAnonymousInt, byte[] paramAnonymousArrayOfByte) {}
    
    public void onServerConnectionState(int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean, String paramAnonymousString)
    {
      Log.d("BluetoothGattServer", "onServerConnectionState() - status=" + paramAnonymousInt1 + " serverIf=" + paramAnonymousInt2 + " device=" + paramAnonymousString);
      try
      {
        BluetoothGattServerCallback localBluetoothGattServerCallback = BluetoothGattServer.-get1(BluetoothGattServer.this);
        paramAnonymousString = BluetoothGattServer.-get0(BluetoothGattServer.this).getRemoteDevice(paramAnonymousString);
        if (paramAnonymousBoolean) {}
        for (paramAnonymousInt2 = 2;; paramAnonymousInt2 = 0)
        {
          localBluetoothGattServerCallback.onConnectionStateChange(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2);
          return;
        }
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onServerRegistered(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      Log.d("BluetoothGattServer", "onServerRegistered() - status=" + paramAnonymousInt1 + " serverIf=" + paramAnonymousInt2);
      synchronized (BluetoothGattServer.-get2(BluetoothGattServer.this))
      {
        if (BluetoothGattServer.-get1(BluetoothGattServer.this) != null)
        {
          BluetoothGattServer.-set0(BluetoothGattServer.this, paramAnonymousInt2);
          BluetoothGattServer.-get2(BluetoothGattServer.this).notify();
          return;
        }
        Log.e("BluetoothGattServer", "onServerRegistered: mCallback is null");
      }
    }
    
    public void onServiceAdded(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, ParcelUuid paramAnonymousParcelUuid)
    {
      paramAnonymousParcelUuid = paramAnonymousParcelUuid.getUuid();
      Log.d("BluetoothGattServer", "onServiceAdded() - service=" + paramAnonymousParcelUuid + "status=" + paramAnonymousInt1);
      paramAnonymousParcelUuid = BluetoothGattServer.this.getService(paramAnonymousParcelUuid, paramAnonymousInt3, paramAnonymousInt2);
      if (paramAnonymousParcelUuid == null) {
        return;
      }
      try
      {
        BluetoothGattServer.-get1(BluetoothGattServer.this).onServiceAdded(paramAnonymousInt1, paramAnonymousParcelUuid);
        return;
      }
      catch (Exception paramAnonymousParcelUuid)
      {
        Log.w("BluetoothGattServer", "Unhandled exception in callback", paramAnonymousParcelUuid);
      }
    }
  };
  private BluetoothGattServerCallback mCallback;
  private int mServerIf;
  private Object mServerIfLock = new Object();
  private IBluetoothGatt mService;
  private List<BluetoothGattService> mServices;
  private int mTransport;
  
  BluetoothGattServer(IBluetoothGatt paramIBluetoothGatt, int paramInt)
  {
    this.mService = paramIBluetoothGatt;
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mCallback = null;
    this.mServerIf = 0;
    this.mTransport = paramInt;
    this.mServices = new ArrayList();
  }
  
  private void unregisterCallback()
  {
    Log.d("BluetoothGattServer", "unregisterCallback() - mServerIf=" + this.mServerIf);
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return;
    }
    try
    {
      this.mCallback = null;
      this.mService.unregisterServer(this.mServerIf);
      this.mServerIf = 0;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGattServer", "", localRemoteException);
    }
  }
  
  public boolean addService(BluetoothGattService paramBluetoothGattService)
  {
    Log.d("BluetoothGattServer", "addService() - service: " + paramBluetoothGattService.getUuid());
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return false;
    }
    this.mServices.add(paramBluetoothGattService);
    Object localObject1;
    Object localObject2;
    try
    {
      this.mService.beginServiceDeclaration(this.mServerIf, paramBluetoothGattService.getType(), paramBluetoothGattService.getInstanceId(), paramBluetoothGattService.getHandles(), new ParcelUuid(paramBluetoothGattService.getUuid()), paramBluetoothGattService.isAdvertisePreferred());
      localObject1 = paramBluetoothGattService.getIncludedServices().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (BluetoothGattService)((Iterator)localObject1).next();
        this.mService.addIncludedService(this.mServerIf, ((BluetoothGattService)localObject2).getType(), ((BluetoothGattService)localObject2).getInstanceId(), new ParcelUuid(((BluetoothGattService)localObject2).getUuid()));
      }
      paramBluetoothGattService = paramBluetoothGattService.getCharacteristics().iterator();
    }
    catch (RemoteException paramBluetoothGattService)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothGattService);
      return false;
    }
    while (paramBluetoothGattService.hasNext())
    {
      localObject1 = (BluetoothGattCharacteristic)paramBluetoothGattService.next();
      int i = ((BluetoothGattCharacteristic)localObject1).getKeySize();
      int j = ((BluetoothGattCharacteristic)localObject1).getPermissions();
      this.mService.addCharacteristic(this.mServerIf, new ParcelUuid(((BluetoothGattCharacteristic)localObject1).getUuid()), ((BluetoothGattCharacteristic)localObject1).getProperties(), (i - 7 << 12) + j);
      localObject2 = ((BluetoothGattCharacteristic)localObject1).getDescriptors().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor)((Iterator)localObject2).next();
        i = ((BluetoothGattCharacteristic)localObject1).getKeySize();
        j = localBluetoothGattDescriptor.getPermissions();
        this.mService.addDescriptor(this.mServerIf, new ParcelUuid(localBluetoothGattDescriptor.getUuid()), (i - 7 << 12) + j);
      }
    }
    this.mService.endServiceDeclaration(this.mServerIf);
    return true;
  }
  
  public void cancelConnection(BluetoothDevice paramBluetoothDevice)
  {
    Log.d("BluetoothGattServer", "cancelConnection() - device: " + paramBluetoothDevice.getAddress());
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return;
    }
    try
    {
      this.mService.serverDisconnect(this.mServerIf, paramBluetoothDevice.getAddress());
      return;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothDevice);
    }
  }
  
  public void clearServices()
  {
    Log.d("BluetoothGattServer", "clearServices()");
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return;
    }
    try
    {
      this.mService.clearServices(this.mServerIf);
      this.mServices.clear();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGattServer", "", localRemoteException);
    }
  }
  
  public void close()
  {
    Log.d("BluetoothGattServer", "close()");
    unregisterCallback();
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    Log.d("BluetoothGattServer", "connect() - device: " + paramBluetoothDevice.getAddress() + ", auto: " + paramBoolean);
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return false;
    }
    try
    {
      IBluetoothGatt localIBluetoothGatt = this.mService;
      int i = this.mServerIf;
      paramBluetoothDevice = paramBluetoothDevice.getAddress();
      if (paramBoolean) {}
      for (paramBoolean = false;; paramBoolean = true)
      {
        localIBluetoothGatt.serverConnect(i, paramBluetoothDevice, paramBoolean, this.mTransport);
        return true;
      }
      return false;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothDevice);
    }
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getConnectedDevices instead.");
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getConnectionState instead.");
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getDevicesMatchingConnectionStates instead.");
  }
  
  public BluetoothGattService getService(UUID paramUUID)
  {
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      if (localBluetoothGattService.getUuid().equals(paramUUID)) {
        return localBluetoothGattService;
      }
    }
    return null;
  }
  
  BluetoothGattService getService(UUID paramUUID, int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      if ((localBluetoothGattService.getType() == paramInt2) && (localBluetoothGattService.getInstanceId() == paramInt1) && (localBluetoothGattService.getUuid().equals(paramUUID))) {
        return localBluetoothGattService;
      }
    }
    return null;
  }
  
  public List<BluetoothGattService> getServices()
  {
    return this.mServices;
  }
  
  public boolean notifyCharacteristicChanged(BluetoothDevice paramBluetoothDevice, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean)
  {
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return false;
    }
    BluetoothGattService localBluetoothGattService = paramBluetoothGattCharacteristic.getService();
    if (localBluetoothGattService == null) {
      return false;
    }
    if (paramBluetoothGattCharacteristic.getValue() == null) {
      throw new IllegalArgumentException("Chracteristic value is empty. Use BluetoothGattCharacteristic#setvalue to update");
    }
    try
    {
      this.mService.sendNotification(this.mServerIf, paramBluetoothDevice.getAddress(), localBluetoothGattService.getType(), localBluetoothGattService.getInstanceId(), new ParcelUuid(localBluetoothGattService.getUuid()), paramBluetoothGattCharacteristic.getInstanceId(), new ParcelUuid(paramBluetoothGattCharacteristic.getUuid()), paramBoolean, paramBluetoothGattCharacteristic.getValue());
      return true;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothDevice);
    }
    return false;
  }
  
  /* Error */
  boolean registerCallback(BluetoothGattServerCallback paramBluetoothGattServerCallback)
  {
    // Byte code:
    //   0: ldc 18
    //   2: ldc_w 305
    //   5: invokestatic 102	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   8: pop
    //   9: aload_0
    //   10: getfield 64	android/bluetooth/BluetoothGattServer:mService	Landroid/bluetooth/IBluetoothGatt;
    //   13: ifnonnull +14 -> 27
    //   16: ldc 18
    //   18: ldc_w 307
    //   21: invokestatic 309	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   24: pop
    //   25: iconst_0
    //   26: ireturn
    //   27: invokestatic 312	java/util/UUID:randomUUID	()Ljava/util/UUID;
    //   30: astore_3
    //   31: ldc 18
    //   33: new 82	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 83	java/lang/StringBuilder:<init>	()V
    //   40: ldc_w 314
    //   43: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: aload_3
    //   47: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   50: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   53: invokestatic 102	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   56: pop
    //   57: aload_0
    //   58: getfield 48	android/bluetooth/BluetoothGattServer:mServerIfLock	Ljava/lang/Object;
    //   61: astore_2
    //   62: aload_2
    //   63: monitorenter
    //   64: aload_0
    //   65: getfield 44	android/bluetooth/BluetoothGattServer:mCallback	Landroid/bluetooth/BluetoothGattServerCallback;
    //   68: ifnull +16 -> 84
    //   71: ldc 18
    //   73: ldc_w 316
    //   76: invokestatic 309	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   79: pop
    //   80: aload_2
    //   81: monitorexit
    //   82: iconst_0
    //   83: ireturn
    //   84: aload_0
    //   85: aload_1
    //   86: putfield 44	android/bluetooth/BluetoothGattServer:mCallback	Landroid/bluetooth/BluetoothGattServerCallback;
    //   89: aload_0
    //   90: getfield 64	android/bluetooth/BluetoothGattServer:mService	Landroid/bluetooth/IBluetoothGatt;
    //   93: new 145	android/os/ParcelUuid
    //   96: dup
    //   97: aload_3
    //   98: invokespecial 148	android/os/ParcelUuid:<init>	(Ljava/util/UUID;)V
    //   101: aload_0
    //   102: getfield 62	android/bluetooth/BluetoothGattServer:mBluetoothGattServerCallback	Landroid/bluetooth/IBluetoothGattServerCallback;
    //   105: invokeinterface 320 3 0
    //   110: aload_0
    //   111: getfield 48	android/bluetooth/BluetoothGattServer:mServerIfLock	Ljava/lang/Object;
    //   114: ldc2_w 321
    //   117: invokevirtual 326	java/lang/Object:wait	(J)V
    //   120: aload_0
    //   121: getfield 52	android/bluetooth/BluetoothGattServer:mServerIf	I
    //   124: ifne +70 -> 194
    //   127: aload_0
    //   128: aconst_null
    //   129: putfield 44	android/bluetooth/BluetoothGattServer:mCallback	Landroid/bluetooth/BluetoothGattServerCallback;
    //   132: aload_2
    //   133: monitorexit
    //   134: iconst_0
    //   135: ireturn
    //   136: astore_1
    //   137: ldc 18
    //   139: ldc 110
    //   141: aload_1
    //   142: invokestatic 114	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   145: pop
    //   146: aload_0
    //   147: aconst_null
    //   148: putfield 44	android/bluetooth/BluetoothGattServer:mCallback	Landroid/bluetooth/BluetoothGattServerCallback;
    //   151: aload_2
    //   152: monitorexit
    //   153: iconst_0
    //   154: ireturn
    //   155: astore_1
    //   156: ldc 18
    //   158: new 82	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 83	java/lang/StringBuilder:<init>	()V
    //   165: ldc 110
    //   167: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: aload_1
    //   171: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   174: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: invokestatic 309	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   180: pop
    //   181: aload_0
    //   182: aconst_null
    //   183: putfield 44	android/bluetooth/BluetoothGattServer:mCallback	Landroid/bluetooth/BluetoothGattServerCallback;
    //   186: goto -66 -> 120
    //   189: astore_1
    //   190: aload_2
    //   191: monitorexit
    //   192: aload_1
    //   193: athrow
    //   194: aload_2
    //   195: monitorexit
    //   196: iconst_1
    //   197: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	BluetoothGattServer
    //   0	198	1	paramBluetoothGattServerCallback	BluetoothGattServerCallback
    //   61	134	2	localObject	Object
    //   30	68	3	localUUID	UUID
    // Exception table:
    //   from	to	target	type
    //   89	110	136	android/os/RemoteException
    //   110	120	155	java/lang/InterruptedException
    //   64	80	189	finally
    //   84	89	189	finally
    //   89	110	189	finally
    //   110	120	189	finally
    //   120	132	189	finally
    //   137	151	189	finally
    //   156	186	189	finally
  }
  
  public boolean removeService(BluetoothGattService paramBluetoothGattService)
  {
    Log.d("BluetoothGattServer", "removeService() - service: " + paramBluetoothGattService.getUuid());
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return false;
    }
    BluetoothGattService localBluetoothGattService = getService(paramBluetoothGattService.getUuid(), paramBluetoothGattService.getInstanceId(), paramBluetoothGattService.getType());
    if (localBluetoothGattService == null) {
      return false;
    }
    try
    {
      this.mService.removeService(this.mServerIf, paramBluetoothGattService.getType(), paramBluetoothGattService.getInstanceId(), new ParcelUuid(paramBluetoothGattService.getUuid()));
      this.mServices.remove(localBluetoothGattService);
      return true;
    }
    catch (RemoteException paramBluetoothGattService)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothGattService);
    }
    return false;
  }
  
  public boolean sendResponse(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    if ((this.mService == null) || (this.mServerIf == 0)) {
      return false;
    }
    try
    {
      this.mService.sendResponse(this.mServerIf, paramBluetoothDevice.getAddress(), paramInt1, paramInt2, paramInt3, paramArrayOfByte);
      return true;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothGattServer", "", paramBluetoothDevice);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */