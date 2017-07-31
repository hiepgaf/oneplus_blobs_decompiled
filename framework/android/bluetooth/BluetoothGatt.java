package android.bluetooth;

import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class BluetoothGatt
  implements BluetoothProfile
{
  static final int AUTHENTICATION_MITM = 2;
  static final int AUTHENTICATION_NONE = 0;
  static final int AUTHENTICATION_NO_MITM = 1;
  public static final int CONNECTION_PRIORITY_BALANCED = 0;
  public static final int CONNECTION_PRIORITY_HIGH = 1;
  public static final int CONNECTION_PRIORITY_LOW_POWER = 2;
  private static final int CONN_STATE_CLOSED = 4;
  private static final int CONN_STATE_CONNECTED = 2;
  private static final int CONN_STATE_CONNECTING = 1;
  private static final int CONN_STATE_DISCONNECTING = 3;
  private static final int CONN_STATE_IDLE = 0;
  private static final boolean DBG = true;
  public static final int GATT_CONNECTION_CONGESTED = 143;
  public static final int GATT_FAILURE = 257;
  public static final int GATT_INSUFFICIENT_AUTHENTICATION = 5;
  public static final int GATT_INSUFFICIENT_ENCRYPTION = 15;
  public static final int GATT_INVALID_ATTRIBUTE_LENGTH = 13;
  public static final int GATT_INVALID_OFFSET = 7;
  public static final int GATT_READ_NOT_PERMITTED = 2;
  public static final int GATT_REQUEST_NOT_SUPPORTED = 6;
  public static final int GATT_SUCCESS = 0;
  public static final int GATT_WRITE_NOT_PERMITTED = 3;
  private static final String TAG = "BluetoothGatt";
  private static final boolean VDBG = false;
  private boolean mAuthRetry = false;
  private boolean mAutoConnect;
  private final IBluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallbackWrapper()
  {
    public void onCharacteristicRead(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte)
    {
      Log.w("BluetoothGatt", "onCharacteristicRead() - Device=" + paramAnonymousString + " handle=" + paramAnonymousInt2 + " Status=" + paramAnonymousInt1);
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      synchronized (BluetoothGatt.-get5(BluetoothGatt.this))
      {
        BluetoothGatt.-set3(BluetoothGatt.this, Boolean.valueOf(false));
        if ((paramAnonymousInt1 == 5) || (paramAnonymousInt1 == 15)) {
          if (BluetoothGatt.-get0(BluetoothGatt.this)) {}
        }
      }
      BluetoothGatt.-set0(BluetoothGatt.this, false);
      paramAnonymousString = BluetoothGatt.this.getCharacteristicById(BluetoothGatt.-get4(BluetoothGatt.this), paramAnonymousInt2);
      if (paramAnonymousString == null)
      {
        Log.w("BluetoothGatt", "onCharacteristicRead() failed to find characteristic!");
        return;
      }
      if (paramAnonymousInt1 == 0) {
        paramAnonymousString.setValue(paramAnonymousArrayOfByte);
      }
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onCharacteristicRead(BluetoothGatt.this, paramAnonymousString, paramAnonymousInt1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onCharacteristicWrite(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      synchronized (BluetoothGatt.-get5(BluetoothGatt.this))
      {
        BluetoothGatt.-set3(BluetoothGatt.this, Boolean.valueOf(false));
        ??? = BluetoothGatt.this.getCharacteristicById(BluetoothGatt.-get4(BluetoothGatt.this), paramAnonymousInt2);
        if (??? == null) {
          return;
        }
      }
      if (((paramAnonymousInt1 == 5) || (paramAnonymousInt1 == 15)) && (!BluetoothGatt.-get0(BluetoothGatt.this))) {
        try
        {
          BluetoothGatt.-set0(BluetoothGatt.this, true);
          BluetoothGatt.-get6(BluetoothGatt.this).writeCharacteristic(BluetoothGatt.-get3(BluetoothGatt.this), paramAnonymousString, paramAnonymousInt2, ((BluetoothGattCharacteristic)???).getWriteType(), 2, ((BluetoothGattCharacteristic)???).getValue());
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          Log.e("BluetoothGatt", "", paramAnonymousString);
        }
      }
      BluetoothGatt.-set0(BluetoothGatt.this, false);
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onCharacteristicWrite(BluetoothGatt.this, (BluetoothGattCharacteristic)???, paramAnonymousInt1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    /* Error */
    public void onClientConnectionState(int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean, String arg4)
    {
      // Byte code:
      //   0: ldc 24
      //   2: new 26	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 27	java/lang/StringBuilder:<init>	()V
      //   9: ldc -104
      //   11: invokevirtual 33	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: iload_1
      //   15: invokevirtual 38	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   18: ldc -102
      //   20: invokevirtual 33	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   23: iload_2
      //   24: invokevirtual 38	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   27: ldc -100
      //   29: invokevirtual 33	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   32: aload 4
      //   34: invokevirtual 33	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   37: invokevirtual 44	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   40: invokestatic 159	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   43: pop
      //   44: aload 4
      //   46: aload_0
      //   47: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   50: invokestatic 54	android/bluetooth/BluetoothGatt:-get4	(Landroid/bluetooth/BluetoothGatt;)Landroid/bluetooth/BluetoothDevice;
      //   53: invokevirtual 59	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
      //   56: invokevirtual 65	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   59: ifne +4 -> 63
      //   62: return
      //   63: iload_3
      //   64: ifeq +77 -> 141
      //   67: iconst_2
      //   68: istore_2
      //   69: aload_0
      //   70: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   73: invokestatic 123	android/bluetooth/BluetoothGatt:-get2	(Landroid/bluetooth/BluetoothGatt;)Landroid/bluetooth/BluetoothGattCallback;
      //   76: aload_0
      //   77: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   80: iload_1
      //   81: iload_2
      //   82: invokevirtual 163	android/bluetooth/BluetoothGattCallback:onConnectionStateChange	(Landroid/bluetooth/BluetoothGatt;II)V
      //   85: aload_0
      //   86: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   89: invokestatic 167	android/bluetooth/BluetoothGatt:-get8	(Landroid/bluetooth/BluetoothGatt;)Ljava/lang/Object;
      //   92: astore 4
      //   94: aload 4
      //   96: monitorenter
      //   97: iload_3
      //   98: ifeq +63 -> 161
      //   101: aload_0
      //   102: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   105: iconst_2
      //   106: invokestatic 171	android/bluetooth/BluetoothGatt:-set2	(Landroid/bluetooth/BluetoothGatt;I)I
      //   109: pop
      //   110: aload 4
      //   112: monitorexit
      //   113: aload_0
      //   114: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   117: invokestatic 69	android/bluetooth/BluetoothGatt:-get5	(Landroid/bluetooth/BluetoothGatt;)Ljava/lang/Boolean;
      //   120: astore 4
      //   122: aload 4
      //   124: monitorenter
      //   125: aload_0
      //   126: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   129: iconst_0
      //   130: invokestatic 75	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   133: invokestatic 79	android/bluetooth/BluetoothGatt:-set3	(Landroid/bluetooth/BluetoothGatt;Ljava/lang/Boolean;)Ljava/lang/Boolean;
      //   136: pop
      //   137: aload 4
      //   139: monitorexit
      //   140: return
      //   141: iconst_0
      //   142: istore_2
      //   143: goto -74 -> 69
      //   146: astore 4
      //   148: ldc 24
      //   150: ldc -126
      //   152: aload 4
      //   154: invokestatic 132	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   157: pop
      //   158: goto -73 -> 85
      //   161: aload_0
      //   162: getfield 12	android/bluetooth/BluetoothGatt$1:this$0	Landroid/bluetooth/BluetoothGatt;
      //   165: iconst_0
      //   166: invokestatic 171	android/bluetooth/BluetoothGatt:-set2	(Landroid/bluetooth/BluetoothGatt;I)I
      //   169: pop
      //   170: goto -60 -> 110
      //   173: astore 5
      //   175: aload 4
      //   177: monitorexit
      //   178: aload 5
      //   180: athrow
      //   181: astore 5
      //   183: aload 4
      //   185: monitorexit
      //   186: aload 5
      //   188: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	189	0	this	1
      //   0	189	1	paramAnonymousInt1	int
      //   0	189	2	paramAnonymousInt2	int
      //   0	189	3	paramAnonymousBoolean	boolean
      //   173	6	5	localObject1	Object
      //   181	6	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   69	85	146	java/lang/Exception
      //   101	110	173	finally
      //   161	170	173	finally
      //   125	137	181	finally
    }
    
    public void onClientRegistered(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      boolean bool = false;
      Log.d("BluetoothGatt", "onClientRegistered() - status=" + paramAnonymousInt1 + " clientIf=" + paramAnonymousInt2);
      BluetoothGatt.-set1(BluetoothGatt.this, paramAnonymousInt2);
      if (paramAnonymousInt1 != 0)
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onConnectionStateChange(BluetoothGatt.this, 257, 0);
        synchronized (BluetoothGatt.-get8(BluetoothGatt.this))
        {
          BluetoothGatt.-set2(BluetoothGatt.this, 0);
          return;
        }
      }
      try
      {
        ??? = BluetoothGatt.-get6(BluetoothGatt.this);
        paramAnonymousInt1 = BluetoothGatt.-get3(BluetoothGatt.this);
        String str = BluetoothGatt.-get4(BluetoothGatt.this).getAddress();
        if (BluetoothGatt.-get1(BluetoothGatt.this)) {}
        for (;;)
        {
          ((IBluetoothGatt)???).clientConnect(paramAnonymousInt1, str, bool, BluetoothGatt.-get9(BluetoothGatt.this));
          return;
          bool = true;
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothGatt", "", localRemoteException);
      }
    }
    
    public void onConfigureMTU(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      Log.d("BluetoothGatt", "onConfigureMTU() - Device=" + paramAnonymousString + " mtu=" + paramAnonymousInt1 + " status=" + paramAnonymousInt2);
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onMtuChanged(BluetoothGatt.this, paramAnonymousInt1, paramAnonymousInt2);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onDescriptorRead(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte)
    {
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      synchronized (BluetoothGatt.-get5(BluetoothGatt.this))
      {
        BluetoothGatt.-set3(BluetoothGatt.this, Boolean.valueOf(false));
        ??? = BluetoothGatt.this.getDescriptorById(BluetoothGatt.-get4(BluetoothGatt.this), paramAnonymousInt2);
        if (??? == null) {
          return;
        }
      }
      if (paramAnonymousInt1 == 0) {
        ((BluetoothGattDescriptor)???).setValue(paramAnonymousArrayOfByte);
      }
      if (((paramAnonymousInt1 == 5) || (paramAnonymousInt1 == 15)) && (!BluetoothGatt.-get0(BluetoothGatt.this))) {
        try
        {
          BluetoothGatt.-set0(BluetoothGatt.this, true);
          BluetoothGatt.-get6(BluetoothGatt.this).readDescriptor(BluetoothGatt.-get3(BluetoothGatt.this), paramAnonymousString, paramAnonymousInt2, 2);
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          Log.e("BluetoothGatt", "", paramAnonymousString);
        }
      }
      BluetoothGatt.-set0(BluetoothGatt.this, true);
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onDescriptorRead(BluetoothGatt.this, (BluetoothGattDescriptor)???, paramAnonymousInt1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onDescriptorWrite(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      synchronized (BluetoothGatt.-get5(BluetoothGatt.this))
      {
        BluetoothGatt.-set3(BluetoothGatt.this, Boolean.valueOf(false));
        ??? = BluetoothGatt.this.getDescriptorById(BluetoothGatt.-get4(BluetoothGatt.this), paramAnonymousInt2);
        if (??? == null) {
          return;
        }
      }
      if (((paramAnonymousInt1 == 5) || (paramAnonymousInt1 == 15)) && (!BluetoothGatt.-get0(BluetoothGatt.this))) {
        try
        {
          BluetoothGatt.-set0(BluetoothGatt.this, true);
          BluetoothGatt.-get6(BluetoothGatt.this).writeDescriptor(BluetoothGatt.-get3(BluetoothGatt.this), paramAnonymousString, paramAnonymousInt2, 2, 2, ((BluetoothGattDescriptor)???).getValue());
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          Log.e("BluetoothGatt", "", paramAnonymousString);
        }
      }
      BluetoothGatt.-set0(BluetoothGatt.this, false);
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onDescriptorWrite(BluetoothGatt.this, (BluetoothGattDescriptor)???, paramAnonymousInt1);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onExecuteWrite(String arg1, int paramAnonymousInt)
    {
      if (!???.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      synchronized (BluetoothGatt.-get5(BluetoothGatt.this))
      {
        BluetoothGatt.-set3(BluetoothGatt.this, Boolean.valueOf(false));
      }
    }
    
    public void onNotify(String paramAnonymousString, int paramAnonymousInt, byte[] paramAnonymousArrayOfByte)
    {
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      paramAnonymousString = BluetoothGatt.this.getCharacteristicById(BluetoothGatt.-get4(BluetoothGatt.this), paramAnonymousInt);
      if (paramAnonymousString == null) {
        return;
      }
      paramAnonymousString.setValue(paramAnonymousArrayOfByte);
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onCharacteristicChanged(BluetoothGatt.this, paramAnonymousString);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onReadRemoteRssi(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onReadRemoteRssi(BluetoothGatt.this, paramAnonymousInt1, paramAnonymousInt2);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
    
    public void onSearchComplete(String paramAnonymousString, List<BluetoothGattService> paramAnonymousList, int paramAnonymousInt)
    {
      Log.d("BluetoothGatt", "onSearchComplete() = Device=" + paramAnonymousString + " Status=" + paramAnonymousInt);
      if (!paramAnonymousString.equals(BluetoothGatt.-get4(BluetoothGatt.this).getAddress())) {
        return;
      }
      paramAnonymousString = paramAnonymousList.iterator();
      while (paramAnonymousString.hasNext()) {
        ((BluetoothGattService)paramAnonymousString.next()).setDevice(BluetoothGatt.-get4(BluetoothGatt.this));
      }
      BluetoothGatt.-get7(BluetoothGatt.this).addAll(paramAnonymousList);
      paramAnonymousString = BluetoothGatt.-get7(BluetoothGatt.this).iterator();
      while (paramAnonymousString.hasNext())
      {
        paramAnonymousList = (BluetoothGattService)paramAnonymousString.next();
        Object localObject = new ArrayList(paramAnonymousList.getIncludedServices());
        paramAnonymousList.getIncludedServices().clear();
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          BluetoothGattService localBluetoothGattService = (BluetoothGattService)((Iterator)localObject).next();
          localBluetoothGattService = BluetoothGatt.this.getService(BluetoothGatt.-get4(BluetoothGatt.this), localBluetoothGattService.getUuid(), localBluetoothGattService.getInstanceId(), localBluetoothGattService.getType());
          if (localBluetoothGattService != null) {
            paramAnonymousList.addIncludedService(localBluetoothGattService);
          } else {
            Log.e("BluetoothGatt", "Broken GATT database: can't find included service.");
          }
        }
      }
      try
      {
        BluetoothGatt.-get2(BluetoothGatt.this).onServicesDiscovered(BluetoothGatt.this, paramAnonymousInt);
        return;
      }
      catch (Exception paramAnonymousString)
      {
        Log.w("BluetoothGatt", "Unhandled exception in callback", paramAnonymousString);
      }
    }
  };
  private BluetoothGattCallback mCallback;
  private int mClientIf;
  private int mConnState;
  private BluetoothDevice mDevice;
  private Boolean mDeviceBusy = Boolean.valueOf(false);
  private IBluetoothGatt mService;
  private List<BluetoothGattService> mServices;
  private final Object mStateLock = new Object();
  private int mTransport;
  
  BluetoothGatt(IBluetoothGatt paramIBluetoothGatt, BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    this.mService = paramIBluetoothGatt;
    this.mDevice = paramBluetoothDevice;
    this.mTransport = paramInt;
    this.mServices = new ArrayList();
    this.mConnState = 0;
  }
  
  private boolean registerApp(BluetoothGattCallback paramBluetoothGattCallback)
  {
    Log.d("BluetoothGatt", "registerApp()");
    if (this.mService == null) {
      return false;
    }
    this.mCallback = paramBluetoothGattCallback;
    paramBluetoothGattCallback = UUID.randomUUID();
    Log.d("BluetoothGatt", "registerApp() - UUID=" + paramBluetoothGattCallback);
    try
    {
      this.mService.registerClient(new ParcelUuid(paramBluetoothGattCallback), this.mBluetoothGattCallback);
      return true;
    }
    catch (RemoteException paramBluetoothGattCallback)
    {
      Log.e("BluetoothGatt", "", paramBluetoothGattCallback);
    }
    return false;
  }
  
  private void unregisterApp()
  {
    Log.d("BluetoothGatt", "unregisterApp() - mClientIf=" + this.mClientIf);
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return;
    }
    try
    {
      this.mCallback = null;
      this.mService.unregisterClient(this.mClientIf);
      this.mClientIf = 0;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
  }
  
  public void abortReliableWrite()
  {
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return;
    }
    try
    {
      this.mService.endReliableWrite(this.mClientIf, this.mDevice.getAddress(), false);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
  }
  
  public void abortReliableWrite(BluetoothDevice paramBluetoothDevice)
  {
    abortReliableWrite();
  }
  
  public boolean beginReliableWrite()
  {
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    try
    {
      this.mService.beginReliableWrite(this.mClientIf, this.mDevice.getAddress());
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public void close()
  {
    Log.d("BluetoothGatt", "close()");
    unregisterApp();
    this.mConnState = 4;
  }
  
  public boolean connect()
  {
    try
    {
      this.mService.clientConnect(this.mClientIf, this.mDevice.getAddress(), false, this.mTransport);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  boolean connect(Boolean arg1, BluetoothGattCallback paramBluetoothGattCallback)
  {
    Log.d("BluetoothGatt", "connect() - device: " + this.mDevice.getAddress() + ", auto: " + ???);
    synchronized (this.mStateLock)
    {
      if (this.mConnState != 0) {
        throw new IllegalStateException("Not idle");
      }
    }
    this.mConnState = 1;
    this.mAutoConnect = ???.booleanValue();
    if (!registerApp(paramBluetoothGattCallback)) {
      synchronized (this.mStateLock)
      {
        this.mConnState = 0;
        Log.e("BluetoothGatt", "Failed to register callback");
        return false;
      }
    }
    return true;
  }
  
  public void disconnect()
  {
    Log.d("BluetoothGatt", "cancelOpen() - device: " + this.mDevice.getAddress());
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return;
    }
    try
    {
      this.mService.clientDisconnect(this.mClientIf, this.mDevice.getAddress());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
  }
  
  public boolean discoverServices()
  {
    Log.d("BluetoothGatt", "discoverServices() - device: " + this.mDevice.getAddress());
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    this.mServices.clear();
    try
    {
      this.mService.discoverServices(this.mClientIf, this.mDevice.getAddress());
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public boolean executeReliableWrite()
  {
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    synchronized (this.mDeviceBusy)
    {
      boolean bool = this.mDeviceBusy.booleanValue();
      if (bool) {
        return false;
      }
      this.mDeviceBusy = Boolean.valueOf(true);
    }
    try
    {
      this.mService.endReliableWrite(this.mClientIf, this.mDevice.getAddress(), true);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
      this.mDeviceBusy = Boolean.valueOf(false);
    }
    localObject = finally;
    throw ((Throwable)localObject);
    return false;
  }
  
  BluetoothGattCharacteristic getCharacteristicById(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    BluetoothGattCharacteristic localBluetoothGattCharacteristic;
    do
    {
      paramBluetoothDevice = this.mServices.iterator();
      Iterator localIterator;
      while (!localIterator.hasNext())
      {
        if (!paramBluetoothDevice.hasNext()) {
          break;
        }
        localIterator = ((BluetoothGattService)paramBluetoothDevice.next()).getCharacteristics().iterator();
      }
      localBluetoothGattCharacteristic = (BluetoothGattCharacteristic)localIterator.next();
    } while (localBluetoothGattCharacteristic.getInstanceId() != paramInt);
    return localBluetoothGattCharacteristic;
    return null;
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getConnectedDevices instead.");
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getConnectionState instead.");
  }
  
  BluetoothGattDescriptor getDescriptorById(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    paramBluetoothDevice = this.mServices.iterator();
    label10:
    if (paramBluetoothDevice.hasNext())
    {
      label65:
      BluetoothGattDescriptor localBluetoothGattDescriptor;
      do
      {
        Iterator localIterator1 = ((BluetoothGattService)paramBluetoothDevice.next()).getCharacteristics().iterator();
        break label65;
        if (!localIterator1.hasNext()) {
          break label10;
        }
        Iterator localIterator2 = ((BluetoothGattCharacteristic)localIterator1.next()).getDescriptors().iterator();
        if (!localIterator2.hasNext()) {
          break;
        }
        localBluetoothGattDescriptor = (BluetoothGattDescriptor)localIterator2.next();
      } while (localBluetoothGattDescriptor.getInstanceId() != paramInt);
      return localBluetoothGattDescriptor;
    }
    return null;
  }
  
  public BluetoothDevice getDevice()
  {
    return this.mDevice;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    throw new UnsupportedOperationException("Use BluetoothManager#getDevicesMatchingConnectionStates instead.");
  }
  
  BluetoothGattService getService(BluetoothDevice paramBluetoothDevice, UUID paramUUID, int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      if ((localBluetoothGattService.getDevice().equals(paramBluetoothDevice)) && (localBluetoothGattService.getType() == paramInt2) && (localBluetoothGattService.getInstanceId() == paramInt1) && (localBluetoothGattService.getUuid().equals(paramUUID))) {
        return localBluetoothGattService;
      }
    }
    return null;
  }
  
  public BluetoothGattService getService(UUID paramUUID)
  {
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      if ((localBluetoothGattService.getDevice().equals(this.mDevice)) && (localBluetoothGattService.getUuid().equals(paramUUID))) {
        return localBluetoothGattService;
      }
    }
    return null;
  }
  
  public List<BluetoothGattService> getServices()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      if (localBluetoothGattService.getDevice().equals(this.mDevice)) {
        localArrayList.add(localBluetoothGattService);
      }
    }
    return localArrayList;
  }
  
  public boolean readCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    if ((paramBluetoothGattCharacteristic.getProperties() & 0x2) == 0) {
      return false;
    }
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    ??? = paramBluetoothGattCharacteristic.getService();
    if (??? == null) {
      return false;
    }
    BluetoothDevice localBluetoothDevice = ((BluetoothGattService)???).getDevice();
    if (localBluetoothDevice == null) {
      return false;
    }
    synchronized (this.mDeviceBusy)
    {
      boolean bool = this.mDeviceBusy.booleanValue();
      if (bool) {
        return false;
      }
      this.mDeviceBusy = Boolean.valueOf(true);
    }
    return false;
  }
  
  public boolean readDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    ??? = paramBluetoothGattDescriptor.getCharacteristic();
    if (??? == null) {
      return false;
    }
    ??? = ((BluetoothGattCharacteristic)???).getService();
    if (??? == null) {
      return false;
    }
    BluetoothDevice localBluetoothDevice = ((BluetoothGattService)???).getDevice();
    if (localBluetoothDevice == null) {
      return false;
    }
    synchronized (this.mDeviceBusy)
    {
      boolean bool = this.mDeviceBusy.booleanValue();
      if (bool) {
        return false;
      }
      this.mDeviceBusy = Boolean.valueOf(true);
    }
    return false;
  }
  
  public boolean readRemoteRssi()
  {
    Log.d("BluetoothGatt", "readRssi() - device: " + this.mDevice.getAddress());
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    try
    {
      this.mService.readRemoteRssi(this.mClientIf, this.mDevice.getAddress());
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public boolean refresh()
  {
    Log.d("BluetoothGatt", "refresh() - device: " + this.mDevice.getAddress());
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    try
    {
      this.mService.refreshDevice(this.mClientIf, this.mDevice.getAddress());
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public boolean requestConnectionPriority(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 2)) {
      throw new IllegalArgumentException("connectionPriority not within valid range");
    }
    Log.d("BluetoothGatt", "requestConnectionPriority() - params: " + paramInt);
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    try
    {
      this.mService.connectionParameterUpdate(this.mClientIf, this.mDevice.getAddress(), paramInt);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public boolean requestMtu(int paramInt)
  {
    Log.d("BluetoothGatt", "configureMTU() - device: " + this.mDevice.getAddress() + " mtu: " + paramInt);
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    try
    {
      this.mService.configureMTU(this.mClientIf, this.mDevice.getAddress(), paramInt);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothGatt", "", localRemoteException);
    }
    return false;
  }
  
  public boolean setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean)
  {
    Log.d("BluetoothGatt", "setCharacteristicNotification() - uuid: " + paramBluetoothGattCharacteristic.getUuid() + " enable: " + paramBoolean);
    if ((this.mService == null) || (this.mClientIf == 0)) {
      return false;
    }
    Object localObject = paramBluetoothGattCharacteristic.getService();
    if (localObject == null) {
      return false;
    }
    localObject = ((BluetoothGattService)localObject).getDevice();
    if (localObject == null) {
      return false;
    }
    try
    {
      this.mService.registerForNotification(this.mClientIf, ((BluetoothDevice)localObject).getAddress(), paramBluetoothGattCharacteristic.getInstanceId(), paramBoolean);
      return true;
    }
    catch (RemoteException paramBluetoothGattCharacteristic)
    {
      Log.e("BluetoothGatt", "", paramBluetoothGattCharacteristic);
    }
    return false;
  }
  
  public boolean writeCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    if (((paramBluetoothGattCharacteristic.getProperties() & 0x8) == 0) && ((paramBluetoothGattCharacteristic.getProperties() & 0x4) == 0)) {
      return false;
    }
    if ((this.mService == null) || (this.mClientIf == 0)) {}
    while (paramBluetoothGattCharacteristic.getValue() == null) {
      return false;
    }
    ??? = paramBluetoothGattCharacteristic.getService();
    if (??? == null) {
      return false;
    }
    BluetoothDevice localBluetoothDevice = ((BluetoothGattService)???).getDevice();
    if (localBluetoothDevice == null) {
      return false;
    }
    synchronized (this.mDeviceBusy)
    {
      boolean bool = this.mDeviceBusy.booleanValue();
      if (bool) {
        return false;
      }
      this.mDeviceBusy = Boolean.valueOf(true);
    }
    return false;
  }
  
  public boolean writeDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    if ((this.mService == null) || (this.mClientIf == 0)) {}
    while (paramBluetoothGattDescriptor.getValue() == null) {
      return false;
    }
    ??? = paramBluetoothGattDescriptor.getCharacteristic();
    if (??? == null) {
      return false;
    }
    ??? = ((BluetoothGattCharacteristic)???).getService();
    if (??? == null) {
      return false;
    }
    BluetoothDevice localBluetoothDevice = ((BluetoothGattService)???).getDevice();
    if (localBluetoothDevice == null) {
      return false;
    }
    synchronized (this.mDeviceBusy)
    {
      boolean bool = this.mDeviceBusy.booleanValue();
      if (bool) {
        return false;
      }
      this.mDeviceBusy = Boolean.valueOf(true);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGatt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */