package android.bluetooth;

public abstract class BluetoothGattServerCallback
{
  public void onCharacteristicReadRequest(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {}
  
  public void onCharacteristicWriteRequest(BluetoothDevice paramBluetoothDevice, int paramInt1, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, byte[] paramArrayOfByte) {}
  
  public void onConnectionStateChange(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2) {}
  
  public void onDescriptorReadRequest(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, BluetoothGattDescriptor paramBluetoothGattDescriptor) {}
  
  public void onDescriptorWriteRequest(BluetoothDevice paramBluetoothDevice, int paramInt1, BluetoothGattDescriptor paramBluetoothGattDescriptor, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, byte[] paramArrayOfByte) {}
  
  public void onExecuteWrite(BluetoothDevice paramBluetoothDevice, int paramInt, boolean paramBoolean) {}
  
  public void onMtuChanged(BluetoothDevice paramBluetoothDevice, int paramInt) {}
  
  public void onNotificationSent(BluetoothDevice paramBluetoothDevice, int paramInt) {}
  
  public void onServiceAdded(int paramInt, BluetoothGattService paramBluetoothGattService) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattServerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */