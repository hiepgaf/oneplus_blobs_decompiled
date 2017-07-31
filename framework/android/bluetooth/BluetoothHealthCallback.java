package android.bluetooth;

import android.os.ParcelFileDescriptor;
import android.util.Log;

public abstract class BluetoothHealthCallback
{
  private static final String TAG = "BluetoothHealthCallback";
  
  public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
  {
    Log.d("BluetoothHealthCallback", "onHealthAppConfigurationStatusChange: " + paramBluetoothHealthAppConfiguration + "Status: " + paramInt);
  }
  
  public void onHealthChannelStateChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt3)
  {
    Log.d("BluetoothHealthCallback", "onHealthChannelStateChange: " + paramBluetoothHealthAppConfiguration + "Device: " + paramBluetoothDevice + "prevState:" + paramInt1 + "newState:" + paramInt2 + "ParcelFd:" + paramParcelFileDescriptor + "ChannelId:" + paramInt3);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHealthCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */