package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallbackWrapper;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class BluetoothLeAdvertiser
{
  private static final int FLAGS_FIELD_BYTES = 3;
  private static final int MANUFACTURER_SPECIFIC_DATA_LENGTH = 2;
  private static final int MAX_ADVERTISING_DATA_BYTES = 31;
  private static final int OVERHEAD_BYTES_PER_FIELD = 2;
  private static final int SERVICE_DATA_UUID_LENGTH = 2;
  private static final String TAG = "BluetoothLeAdvertiser";
  private BluetoothAdapter mBluetoothAdapter;
  private final IBluetoothManager mBluetoothManager;
  private final Handler mHandler;
  private final Map<AdvertiseCallback, AdvertiseCallbackWrapper> mLeAdvertisers = new HashMap();
  
  public BluetoothLeAdvertiser(IBluetoothManager paramIBluetoothManager)
  {
    this.mBluetoothManager = paramIBluetoothManager;
    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mHandler = new Handler(Looper.getMainLooper());
  }
  
  private int byteLength(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return 0;
    }
    return paramArrayOfByte.length;
  }
  
  private void postStartFailure(final AdvertiseCallback paramAdvertiseCallback, final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        paramAdvertiseCallback.onStartFailure(paramInt);
      }
    });
  }
  
  private void postStartSuccess(final AdvertiseCallback paramAdvertiseCallback, final AdvertiseSettings paramAdvertiseSettings)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        paramAdvertiseCallback.onStartSuccess(paramAdvertiseSettings);
      }
    });
  }
  
  private int totalBytes(AdvertiseData paramAdvertiseData, boolean paramBoolean)
  {
    if (paramAdvertiseData == null) {
      return 0;
    }
    int n;
    int m;
    int k;
    ParcelUuid localParcelUuid;
    if (paramBoolean)
    {
      i = 3;
      j = i;
      if (paramAdvertiseData.getServiceUuids() != null)
      {
        n = 0;
        m = 0;
        k = 0;
        localIterator = paramAdvertiseData.getServiceUuids().iterator();
      }
    }
    else
    {
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label112;
        }
        localParcelUuid = (ParcelUuid)localIterator.next();
        if (BluetoothUuid.is16BitUuid(localParcelUuid))
        {
          n += 1;
          continue;
          i = 0;
          break;
        }
        if (BluetoothUuid.is32BitUuid(localParcelUuid)) {
          m += 1;
        } else {
          k += 1;
        }
      }
      label112:
      j = i;
      if (n != 0) {
        j = i + (n * 2 + 2);
      }
      i = j;
      if (m != 0) {
        i = j + (m * 4 + 2);
      }
      j = i;
      if (k != 0) {
        j = i + (k * 16 + 2);
      }
    }
    Iterator localIterator = paramAdvertiseData.getServiceData().keySet().iterator();
    int i = j;
    while (localIterator.hasNext())
    {
      localParcelUuid = (ParcelUuid)localIterator.next();
      i += byteLength((byte[])paramAdvertiseData.getServiceData().get(localParcelUuid)) + 4;
    }
    int j = 0;
    while (j < paramAdvertiseData.getManufacturerSpecificData().size())
    {
      i += byteLength((byte[])paramAdvertiseData.getManufacturerSpecificData().valueAt(j)) + 4;
      j += 1;
    }
    j = i;
    if (paramAdvertiseData.getIncludeTxPowerLevel()) {
      j = i + 3;
    }
    i = j;
    if (paramAdvertiseData.getIncludeDeviceName())
    {
      i = j;
      if (this.mBluetoothAdapter.getName() != null) {
        i = j + (this.mBluetoothAdapter.getName().length() + 2);
      }
    }
    return i;
  }
  
  public void cleanup()
  {
    this.mLeAdvertisers.clear();
  }
  
  public void startAdvertising(AdvertiseSettings paramAdvertiseSettings, AdvertiseData paramAdvertiseData, AdvertiseCallback paramAdvertiseCallback)
  {
    startAdvertising(paramAdvertiseSettings, paramAdvertiseData, null, paramAdvertiseCallback);
  }
  
  public void startAdvertising(AdvertiseSettings paramAdvertiseSettings, AdvertiseData paramAdvertiseData1, AdvertiseData paramAdvertiseData2, AdvertiseCallback paramAdvertiseCallback)
  {
    synchronized (this.mLeAdvertisers)
    {
      BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
      if (paramAdvertiseCallback == null) {
        throw new IllegalArgumentException("callback cannot be null");
      }
    }
    if ((this.mBluetoothAdapter.isMultipleAdvertisementSupported()) || (this.mBluetoothAdapter.isPeripheralModeSupported()))
    {
      if ((totalBytes(paramAdvertiseData1, paramAdvertiseSettings.isConnectable()) > 31) || (totalBytes(paramAdvertiseData2, false) > 31)) {
        postStartFailure(paramAdvertiseCallback, 1);
      }
    }
    else
    {
      postStartFailure(paramAdvertiseCallback, 5);
      return;
    }
    if (this.mLeAdvertisers.containsKey(paramAdvertiseCallback))
    {
      postStartFailure(paramAdvertiseCallback, 3);
      return;
    }
    try
    {
      IBluetoothGatt localIBluetoothGatt = this.mBluetoothManager.getBluetoothGatt();
      new AdvertiseCallbackWrapper(paramAdvertiseCallback, paramAdvertiseData1, paramAdvertiseData2, paramAdvertiseSettings, localIBluetoothGatt).startRegisteration();
      return;
    }
    catch (RemoteException paramAdvertiseSettings)
    {
      Log.e("BluetoothLeAdvertiser", "Failed to get Bluetooth gatt - ", paramAdvertiseSettings);
      postStartFailure(paramAdvertiseCallback, 4);
    }
  }
  
  public void stopAdvertising(AdvertiseCallback paramAdvertiseCallback)
  {
    Map localMap = this.mLeAdvertisers;
    if (paramAdvertiseCallback == null) {
      try
      {
        throw new IllegalArgumentException("callback cannot be null");
      }
      finally {}
    }
    paramAdvertiseCallback = (AdvertiseCallbackWrapper)this.mLeAdvertisers.get(paramAdvertiseCallback);
    if (paramAdvertiseCallback == null) {
      return;
    }
    paramAdvertiseCallback.stopAdvertising();
  }
  
  private class AdvertiseCallbackWrapper
    extends BluetoothGattCallbackWrapper
  {
    private static final int LE_CALLBACK_TIMEOUT_MILLIS = 2000;
    private final AdvertiseCallback mAdvertiseCallback;
    private final AdvertiseData mAdvertisement;
    private final IBluetoothGatt mBluetoothGatt;
    private int mClientIf;
    private boolean mIsAdvertising = false;
    private final AdvertiseData mScanResponse;
    private final AdvertiseSettings mSettings;
    
    public AdvertiseCallbackWrapper(AdvertiseCallback paramAdvertiseCallback, AdvertiseData paramAdvertiseData1, AdvertiseData paramAdvertiseData2, AdvertiseSettings paramAdvertiseSettings, IBluetoothGatt paramIBluetoothGatt)
    {
      this.mAdvertiseCallback = paramAdvertiseCallback;
      this.mAdvertisement = paramAdvertiseData1;
      this.mScanResponse = paramAdvertiseData2;
      this.mSettings = paramAdvertiseSettings;
      this.mBluetoothGatt = paramIBluetoothGatt;
      this.mClientIf = 0;
    }
    
    /* Error */
    public void onClientRegistered(int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: ldc 52
      //   2: new 54	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 55	java/lang/StringBuilder:<init>	()V
      //   9: ldc 57
      //   11: invokevirtual 61	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: iload_1
      //   15: invokevirtual 64	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   18: ldc 66
      //   20: invokevirtual 61	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   23: iload_2
      //   24: invokevirtual 64	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   27: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   30: invokestatic 76	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   33: pop
      //   34: aload_0
      //   35: monitorenter
      //   36: iload_1
      //   37: ifne +67 -> 104
      //   40: aload_0
      //   41: getfield 45	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mClientIf	I
      //   44: iconst_m1
      //   45: if_icmpne +16 -> 61
      //   48: aload_0
      //   49: getfield 43	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mBluetoothGatt	Landroid/bluetooth/IBluetoothGatt;
      //   52: iload_2
      //   53: invokeinterface 82 2 0
      //   58: aload_0
      //   59: monitorexit
      //   60: return
      //   61: aload_0
      //   62: iload_2
      //   63: putfield 45	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mClientIf	I
      //   66: aload_0
      //   67: getfield 43	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mBluetoothGatt	Landroid/bluetooth/IBluetoothGatt;
      //   70: aload_0
      //   71: getfield 45	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mClientIf	I
      //   74: aload_0
      //   75: getfield 37	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mAdvertisement	Landroid/bluetooth/le/AdvertiseData;
      //   78: aload_0
      //   79: getfield 39	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mScanResponse	Landroid/bluetooth/le/AdvertiseData;
      //   82: aload_0
      //   83: getfield 41	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mSettings	Landroid/bluetooth/le/AdvertiseSettings;
      //   86: invokeinterface 86 5 0
      //   91: goto -33 -> 58
      //   94: astore_3
      //   95: ldc 52
      //   97: ldc 88
      //   99: aload_3
      //   100: invokestatic 92	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   103: pop
      //   104: aload_0
      //   105: iconst_m1
      //   106: putfield 45	android/bluetooth/le/BluetoothLeAdvertiser$AdvertiseCallbackWrapper:mClientIf	I
      //   109: aload_0
      //   110: invokevirtual 97	java/lang/Object:notifyAll	()V
      //   113: aload_0
      //   114: monitorexit
      //   115: return
      //   116: astore_3
      //   117: aload_0
      //   118: monitorexit
      //   119: aload_3
      //   120: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	121	0	this	AdvertiseCallbackWrapper
      //   0	121	1	paramInt1	int
      //   0	121	2	paramInt2	int
      //   94	6	3	localRemoteException	RemoteException
      //   116	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   40	58	94	android/os/RemoteException
      //   61	91	94	android/os/RemoteException
      //   40	58	116	finally
      //   61	91	116	finally
      //   95	104	116	finally
      //   104	113	116	finally
    }
    
    public void onMultiAdvertiseCallback(int paramInt, boolean paramBoolean, AdvertiseSettings paramAdvertiseSettings)
    {
      if (paramBoolean) {
        if (paramInt != 0) {}
      }
      for (;;)
      {
        try
        {
          this.mIsAdvertising = true;
          BluetoothLeAdvertiser.-wrap1(BluetoothLeAdvertiser.this, this.mAdvertiseCallback, paramAdvertiseSettings);
          notifyAll();
          return;
        }
        finally {}
        BluetoothLeAdvertiser.-wrap0(BluetoothLeAdvertiser.this, this.mAdvertiseCallback, paramInt);
        continue;
        try
        {
          this.mBluetoothGatt.unregisterClient(this.mClientIf);
          this.mClientIf = -1;
          this.mIsAdvertising = false;
          BluetoothLeAdvertiser.-get0(BluetoothLeAdvertiser.this).remove(this.mAdvertiseCallback);
        }
        catch (RemoteException paramAdvertiseSettings)
        {
          Log.e("BluetoothLeAdvertiser", "remote exception when unregistering", paramAdvertiseSettings);
        }
      }
    }
    
    public void startRegisteration()
    {
      for (;;)
      {
        try
        {
          int i = this.mClientIf;
          if (i == -1) {
            return;
          }
          try
          {
            UUID localUUID = UUID.randomUUID();
            this.mBluetoothGatt.registerClient(new ParcelUuid(localUUID), this);
            wait(2000L);
            if ((this.mClientIf > 0) && (this.mIsAdvertising))
            {
              BluetoothLeAdvertiser.-get0(BluetoothLeAdvertiser.this).put(this.mAdvertiseCallback, this);
              return;
            }
          }
          catch (InterruptedException|RemoteException localInterruptedException)
          {
            Log.e("BluetoothLeAdvertiser", "Failed to start registeration", localInterruptedException);
            continue;
          }
          if (this.mClientIf > 0) {
            break label131;
          }
        }
        finally {}
        if (this.mClientIf == 0) {
          this.mClientIf = -1;
        }
        BluetoothLeAdvertiser.-wrap0(BluetoothLeAdvertiser.this, this.mAdvertiseCallback, 4);
        continue;
        try
        {
          label131:
          this.mBluetoothGatt.unregisterClient(this.mClientIf);
          this.mClientIf = -1;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("BluetoothLeAdvertiser", "remote exception when unregistering", localRemoteException);
        }
      }
    }
    
    public void stopAdvertising()
    {
      try
      {
        this.mBluetoothGatt.stopMultiAdvertising(this.mClientIf);
        wait(2000L);
        if (BluetoothLeAdvertiser.-get0(BluetoothLeAdvertiser.this).containsKey(this.mAdvertiseCallback)) {
          BluetoothLeAdvertiser.-get0(BluetoothLeAdvertiser.this).remove(this.mAdvertiseCallback);
        }
        return;
      }
      catch (InterruptedException|RemoteException localInterruptedException)
      {
        for (;;)
        {
          Log.e("BluetoothLeAdvertiser", "Failed to stop advertising", localInterruptedException);
        }
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/BluetoothLeAdvertiser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */