package android.bluetooth;

import java.util.List;

public abstract interface BluetoothProfile
{
  public static final int A2DP = 2;
  public static final int A2DP_SINK = 11;
  public static final int AVRCP_CONTROLLER = 12;
  public static final int DUN = 21;
  public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.profile.extra.PREVIOUS_STATE";
  public static final String EXTRA_STATE = "android.bluetooth.profile.extra.STATE";
  public static final int GATT = 7;
  public static final int GATT_SERVER = 8;
  public static final int HEADSET = 1;
  public static final int HEADSET_CLIENT = 16;
  public static final int HEALTH = 3;
  public static final int INPUT_DEVICE = 4;
  public static final int MAP = 9;
  public static final int PAN = 5;
  public static final int PBAP = 6;
  public static final int PBAP_CLIENT = 17;
  public static final int PRIORITY_AUTO_CONNECT = 1000;
  public static final int PRIORITY_OFF = 0;
  public static final int PRIORITY_ON = 100;
  public static final int PRIORITY_UNDEFINED = -1;
  public static final int SAP = 10;
  public static final int STATE_CONNECTED = 2;
  public static final int STATE_CONNECTING = 1;
  public static final int STATE_DISCONNECTED = 0;
  public static final int STATE_DISCONNECTING = 3;
  
  public abstract List<BluetoothDevice> getConnectedDevices();
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice);
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt);
  
  public static abstract interface ServiceListener
  {
    public abstract void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile);
    
    public abstract void onServiceDisconnected(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */