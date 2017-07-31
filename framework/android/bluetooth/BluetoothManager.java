package android.bluetooth;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BluetoothManager
{
  private static final boolean DBG = true;
  private static final String TAG = "BluetoothManager";
  private static final boolean VDBG = true;
  private final BluetoothAdapter mAdapter;
  
  public BluetoothManager(Context paramContext)
  {
    if (paramContext.getApplicationContext() == null) {
      throw new IllegalArgumentException("context not associated with any application (using a mock context?)");
    }
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
  }
  
  public BluetoothAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public List<BluetoothDevice> getConnectedDevices(int paramInt)
  {
    Log.d("BluetoothManager", "getConnectedDevices");
    if ((paramInt != 7) && (paramInt != 8)) {
      throw new IllegalArgumentException("Profile not supported: " + paramInt);
    }
    ArrayList localArrayList = new ArrayList();
    try
    {
      Object localObject = this.mAdapter.getBluetoothManager().getBluetoothGatt();
      if (localObject == null) {
        return localArrayList;
      }
      localObject = ((IBluetoothGatt)localObject).getDevicesMatchingConnectionStates(new int[] { 2 });
      return (List<BluetoothDevice>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothManager", "", localRemoteException);
    }
    return localArrayList;
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    Log.d("BluetoothManager", "getConnectionState()");
    Iterator localIterator = getConnectedDevices(paramInt).iterator();
    while (localIterator.hasNext()) {
      if (paramBluetoothDevice.equals((BluetoothDevice)localIterator.next())) {
        return 2;
      }
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int paramInt, int[] paramArrayOfInt)
  {
    Log.d("BluetoothManager", "getDevicesMatchingConnectionStates");
    if ((paramInt != 7) && (paramInt != 8)) {
      throw new IllegalArgumentException("Profile not supported: " + paramInt);
    }
    ArrayList localArrayList = new ArrayList();
    try
    {
      IBluetoothGatt localIBluetoothGatt = this.mAdapter.getBluetoothManager().getBluetoothGatt();
      if (localIBluetoothGatt == null) {
        return localArrayList;
      }
      paramArrayOfInt = localIBluetoothGatt.getDevicesMatchingConnectionStates(paramArrayOfInt);
      return paramArrayOfInt;
    }
    catch (RemoteException paramArrayOfInt)
    {
      Log.e("BluetoothManager", "", paramArrayOfInt);
    }
    return localArrayList;
  }
  
  public BluetoothGattServer openGattServer(Context paramContext, BluetoothGattServerCallback paramBluetoothGattServerCallback)
  {
    return openGattServer(paramContext, paramBluetoothGattServerCallback, 0);
  }
  
  public BluetoothGattServer openGattServer(Context paramContext, BluetoothGattServerCallback paramBluetoothGattServerCallback, int paramInt)
  {
    if ((paramContext == null) || (paramBluetoothGattServerCallback == null)) {
      throw new IllegalArgumentException("null parameter: " + paramContext + " " + paramBluetoothGattServerCallback);
    }
    try
    {
      paramContext = this.mAdapter.getBluetoothManager().getBluetoothGatt();
      if (paramContext == null)
      {
        Log.e("BluetoothManager", "Fail to get GATT Server connection");
        return null;
      }
      paramContext = new BluetoothGattServer(paramContext, paramInt);
      boolean bool = Boolean.valueOf(paramContext.registerCallback(paramBluetoothGattServerCallback)).booleanValue();
      if (bool) {
        return paramContext;
      }
      return null;
    }
    catch (RemoteException paramContext)
    {
      Log.e("BluetoothManager", "", paramContext);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */