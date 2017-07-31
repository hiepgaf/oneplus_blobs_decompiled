package android.bluetooth;

import android.content.Context;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public final class BluetoothDevice
  implements Parcelable
{
  public static final int ACCESS_ALLOWED = 1;
  public static final int ACCESS_REJECTED = 2;
  public static final int ACCESS_UNKNOWN = 0;
  public static final String ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";
  public static final String ACTION_ACL_DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED";
  public static final String ACTION_ACL_DISCONNECT_REQUESTED = "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED";
  public static final String ACTION_ALIAS_CHANGED = "android.bluetooth.device.action.ALIAS_CHANGED";
  public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";
  public static final String ACTION_CLASS_CHANGED = "android.bluetooth.device.action.CLASS_CHANGED";
  public static final String ACTION_CONNECTION_ACCESS_CANCEL = "android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL";
  public static final String ACTION_CONNECTION_ACCESS_REPLY = "android.bluetooth.device.action.CONNECTION_ACCESS_REPLY";
  public static final String ACTION_CONNECTION_ACCESS_REQUEST = "android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST";
  public static final String ACTION_DISAPPEARED = "android.bluetooth.device.action.DISAPPEARED";
  public static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND";
  public static final String ACTION_MAS_INSTANCE = "android.bluetooth.device.action.MAS_INSTANCE";
  public static final String ACTION_NAME_CHANGED = "android.bluetooth.device.action.NAME_CHANGED";
  public static final String ACTION_NAME_FAILED = "android.bluetooth.device.action.NAME_FAILED";
  public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
  public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
  public static final String ACTION_SDP_RECORD = "android.bluetooth.device.action.SDP_RECORD";
  public static final String ACTION_UUID = "android.bluetooth.device.action.UUID";
  public static final int BOND_BONDED = 12;
  public static final int BOND_BONDING = 11;
  public static final int BOND_NONE = 10;
  public static final int BOND_SUCCESS = 0;
  public static final int CONNECTION_ACCESS_NO = 2;
  public static final int CONNECTION_ACCESS_YES = 1;
  private static final int CONNECTION_STATE_CONNECTED = 1;
  private static final int CONNECTION_STATE_DISCONNECTED = 0;
  private static final int CONNECTION_STATE_ENCRYPTED_BREDR = 2;
  private static final int CONNECTION_STATE_ENCRYPTED_LE = 4;
  public static final Parcelable.Creator<BluetoothDevice> CREATOR = new Parcelable.Creator()
  {
    public BluetoothDevice createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothDevice(paramAnonymousParcel.readString());
    }
    
    public BluetoothDevice[] newArray(int paramAnonymousInt)
    {
      return new BluetoothDevice[paramAnonymousInt];
    }
  };
  private static final boolean DBG = false;
  public static final int DEVICE_TYPE_CLASSIC = 1;
  public static final int DEVICE_TYPE_DUAL = 3;
  public static final int DEVICE_TYPE_LE = 2;
  public static final int DEVICE_TYPE_UNKNOWN = 0;
  public static final int ERROR = Integer.MIN_VALUE;
  public static final String EXTRA_ACCESS_REQUEST_TYPE = "android.bluetooth.device.extra.ACCESS_REQUEST_TYPE";
  public static final String EXTRA_ALWAYS_ALLOWED = "android.bluetooth.device.extra.ALWAYS_ALLOWED";
  public static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE";
  public static final String EXTRA_CLASS = "android.bluetooth.device.extra.CLASS";
  public static final String EXTRA_CLASS_NAME = "android.bluetooth.device.extra.CLASS_NAME";
  public static final String EXTRA_CONNECTION_ACCESS_RESULT = "android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT";
  public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
  public static final String EXTRA_MAS_INSTANCE = "android.bluetooth.device.extra.MAS_INSTANCE";
  public static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME";
  public static final String EXTRA_PACKAGE_NAME = "android.bluetooth.device.extra.PACKAGE_NAME";
  public static final String EXTRA_PAIRING_KEY = "android.bluetooth.device.extra.PAIRING_KEY";
  public static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
  public static final String EXTRA_PREVIOUS_BOND_STATE = "android.bluetooth.device.extra.PREVIOUS_BOND_STATE";
  public static final String EXTRA_REASON = "android.bluetooth.device.extra.REASON";
  public static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI";
  public static final String EXTRA_SDP_RECORD = "android.bluetooth.device.extra.SDP_RECORD";
  public static final String EXTRA_SDP_SEARCH_STATUS = "android.bluetooth.device.extra.SDP_SEARCH_STATUS";
  public static final String EXTRA_UUID = "android.bluetooth.device.extra.UUID";
  public static final int PAIRING_VARIANT_CONSENT = 3;
  public static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
  public static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
  public static final int PAIRING_VARIANT_OOB_CONSENT = 6;
  public static final int PAIRING_VARIANT_PASSKEY = 1;
  public static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
  public static final int PAIRING_VARIANT_PIN = 0;
  public static final int PAIRING_VARIANT_PIN_16_DIGITS = 7;
  public static final int REQUEST_TYPE_MESSAGE_ACCESS = 3;
  public static final int REQUEST_TYPE_PHONEBOOK_ACCESS = 2;
  public static final int REQUEST_TYPE_PROFILE_CONNECTION = 1;
  public static final int REQUEST_TYPE_SIM_ACCESS = 4;
  private static final String TAG = "BluetoothDevice";
  public static final int TRANSPORT_AUTO = 0;
  public static final int TRANSPORT_BREDR = 1;
  public static final int TRANSPORT_LE = 2;
  public static final int UNBOND_REASON_AUTH_CANCELED = 3;
  public static final int UNBOND_REASON_AUTH_FAILED = 1;
  public static final int UNBOND_REASON_AUTH_REJECTED = 2;
  public static final int UNBOND_REASON_AUTH_TIMEOUT = 6;
  public static final int UNBOND_REASON_DISCOVERY_IN_PROGRESS = 5;
  public static final int UNBOND_REASON_REMOTE_AUTH_CANCELED = 8;
  public static final int UNBOND_REASON_REMOTE_DEVICE_DOWN = 4;
  public static final int UNBOND_REASON_REMOVED = 9;
  public static final int UNBOND_REASON_REPEATED_ATTEMPTS = 7;
  static IBluetoothManagerCallback mStateChangeCallback = new IBluetoothManagerCallback.Stub()
  {
    public void onBluetoothServiceDown()
      throws RemoteException
    {
      try
      {
        BluetoothDevice.-set0(null);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void onBluetoothServiceUp(IBluetooth paramAnonymousIBluetooth)
      throws RemoteException
    {
      try
      {
        if (BluetoothDevice.-get0() != null) {
          Log.w("BluetoothDevice", "sService is not NULL");
        }
        BluetoothDevice.-set0(paramAnonymousIBluetooth);
        return;
      }
      finally {}
    }
    
    public void onBrEdrDown() {}
  };
  private static IBluetooth sService;
  private final String mAddress;
  
  BluetoothDevice(String paramString)
  {
    getService();
    if (!BluetoothAdapter.checkBluetoothAddress(paramString)) {
      throw new IllegalArgumentException(paramString + " is not a valid Bluetooth address");
    }
    this.mAddress = paramString;
  }
  
  public static byte[] convertPinToBytes(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      paramString = paramString.getBytes("UTF-8");
      if ((paramString.length <= 0) || (paramString.length > 16)) {
        return null;
      }
    }
    catch (UnsupportedEncodingException paramString)
    {
      Log.e("BluetoothDevice", "UTF-8 not supported?!?");
      return null;
    }
    return paramString;
  }
  
  static IBluetooth getService()
  {
    try
    {
      if (sService == null) {
        sService = BluetoothAdapter.getDefaultAdapter().getBluetoothService(mStateChangeCallback);
      }
      return sService;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean cancelBondProcess()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot cancel Remote Device bond");
      return false;
    }
    try
    {
      Log.i("BluetoothDevice", "cancelBondProcess() for device " + getAddress() + " called by pid: " + Process.myPid() + " tid: " + Process.myTid());
      boolean bool = sService.cancelBondProcess(this);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean cancelPairingUserInput()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot create pairing user input");
      return false;
    }
    try
    {
      boolean bool = sService.cancelBondProcess(this);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public BluetoothGatt connectGatt(Context paramContext, boolean paramBoolean, BluetoothGattCallback paramBluetoothGattCallback)
  {
    return connectGatt(paramContext, paramBoolean, paramBluetoothGattCallback, 0);
  }
  
  public BluetoothGatt connectGatt(Context paramContext, boolean paramBoolean, BluetoothGattCallback paramBluetoothGattCallback, int paramInt)
  {
    paramContext = BluetoothAdapter.getDefaultAdapter().getBluetoothManager();
    try
    {
      paramContext = paramContext.getBluetoothGatt();
      if (paramContext == null) {
        return null;
      }
      paramContext = new BluetoothGatt(paramContext, this, paramInt);
      paramContext.connect(Boolean.valueOf(paramBoolean), paramBluetoothGattCallback);
      return paramContext;
    }
    catch (RemoteException paramContext)
    {
      Log.e("BluetoothDevice", "", paramContext);
    }
    return null;
  }
  
  public boolean createBond()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot create bond to Remote Device");
      return false;
    }
    try
    {
      Log.i("BluetoothDevice", "createBond() for device " + getAddress() + " called by pid: " + Process.myPid() + " tid: " + Process.myTid());
      boolean bool = sService.createBond(this, 0);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean createBond(int paramInt)
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot create bond to Remote Device");
      return false;
    }
    if ((paramInt < 0) || (paramInt > 2)) {
      throw new IllegalArgumentException(paramInt + " is not a valid Bluetooth transport");
    }
    try
    {
      Log.i("BluetoothDevice", "createBond() for device " + getAddress() + " called by pid: " + Process.myPid() + " tid: " + Process.myTid());
      boolean bool = sService.createBond(this, paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean createBondOutOfBand(int paramInt, OobData paramOobData)
  {
    try
    {
      boolean bool = sService.createBondOutOfBand(this, paramInt, paramOobData);
      return bool;
    }
    catch (RemoteException paramOobData)
    {
      Log.e("BluetoothDevice", "", paramOobData);
    }
    return false;
  }
  
  public BluetoothSocket createInsecureL2capSocket(int paramInt)
    throws IOException
  {
    return new BluetoothSocket(3, -1, false, false, this, paramInt, null);
  }
  
  public BluetoothSocket createInsecureRfcommSocket(int paramInt)
    throws IOException
  {
    if (!isBluetoothEnabled())
    {
      Log.e("BluetoothDevice", "Bluetooth is not enabled");
      throw new IOException();
    }
    return new BluetoothSocket(1, -1, false, false, this, paramInt, null);
  }
  
  public BluetoothSocket createInsecureRfcommSocketToServiceRecord(UUID paramUUID)
    throws IOException
  {
    if (!isBluetoothEnabled())
    {
      Log.e("BluetoothDevice", "Bluetooth is not enabled");
      throw new IOException();
    }
    return new BluetoothSocket(1, -1, false, false, this, -1, new ParcelUuid(paramUUID));
  }
  
  public BluetoothSocket createL2capSocket(int paramInt)
    throws IOException
  {
    return new BluetoothSocket(3, -1, true, true, this, paramInt, null);
  }
  
  public BluetoothSocket createRfcommSocket(int paramInt)
    throws IOException
  {
    if (!isBluetoothEnabled())
    {
      Log.e("BluetoothDevice", "Bluetooth is not enabled");
      throw new IOException();
    }
    return new BluetoothSocket(1, -1, true, true, this, paramInt, null);
  }
  
  public BluetoothSocket createRfcommSocketToServiceRecord(UUID paramUUID)
    throws IOException
  {
    if (!isBluetoothEnabled())
    {
      Log.e("BluetoothDevice", "Bluetooth is not enabled");
      throw new IOException();
    }
    return new BluetoothSocket(1, -1, true, true, this, -1, new ParcelUuid(paramUUID));
  }
  
  public BluetoothSocket createScoSocket()
    throws IOException
  {
    if (!isBluetoothEnabled())
    {
      Log.e("BluetoothDevice", "Bluetooth is not enabled");
      throw new IOException();
    }
    return new BluetoothSocket(2, -1, true, true, this, -1, null);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof BluetoothDevice)) {
      return this.mAddress.equals(((BluetoothDevice)paramObject).getAddress());
    }
    return false;
  }
  
  public boolean fetchUuidsWithSdp()
  {
    IBluetooth localIBluetooth = sService;
    if ((localIBluetooth == null) || (!isBluetoothEnabled()))
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot fetchUuidsWithSdp");
      return false;
    }
    try
    {
      boolean bool = localIBluetooth.fetchRemoteUuids(this);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public String getAddress()
  {
    return this.mAddress;
  }
  
  public String getAlias()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get Remote Device Alias");
      return null;
    }
    try
    {
      String str = sService.getRemoteAlias(this);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return null;
  }
  
  public String getAliasName()
  {
    String str2 = getAlias();
    String str1 = str2;
    if (str2 == null) {
      str1 = getName();
    }
    return str1;
  }
  
  public BluetoothClass getBluetoothClass()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get Bluetooth Class");
      return null;
    }
    try
    {
      int i = sService.getRemoteClass(this);
      if (i == -16777216) {
        return null;
      }
      BluetoothClass localBluetoothClass = new BluetoothClass(i);
      return localBluetoothClass;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return null;
  }
  
  public int getBondState()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get bond state");
      return 10;
    }
    try
    {
      int i = sService.getBondState(this);
      return i;
    }
    catch (NullPointerException localNullPointerException)
    {
      Log.e("BluetoothDevice", "NullPointerException for getBondState() of device (" + getAddress() + ")", localNullPointerException);
      return 10;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return 10;
  }
  
  public int getMessageAccessPermission()
  {
    if (sService == null) {
      return 0;
    }
    try
    {
      int i = sService.getMessageAccessPermission(this);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return 0;
  }
  
  public String getName()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get Remote Device name");
      return null;
    }
    try
    {
      String str = sService.getRemoteName(this);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return null;
  }
  
  public int getPhonebookAccessPermission()
  {
    if (sService == null) {
      return 0;
    }
    try
    {
      int i = sService.getPhonebookAccessPermission(this);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return 0;
  }
  
  public int getSimAccessPermission()
  {
    if (sService == null) {
      return 0;
    }
    try
    {
      int i = sService.getSimAccessPermission(this);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return 0;
  }
  
  public int getType()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get Remote Device type");
      return 0;
    }
    try
    {
      int i = sService.getRemoteType(this);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return 0;
  }
  
  public ParcelUuid[] getUuids()
  {
    if ((sService == null) || (!isBluetoothEnabled()))
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot get remote device Uuids");
      return null;
    }
    try
    {
      ParcelUuid[] arrayOfParcelUuid = sService.getRemoteUuids(this);
      return arrayOfParcelUuid;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return null;
  }
  
  public int hashCode()
  {
    return this.mAddress.hashCode();
  }
  
  public boolean isBluetoothDock()
  {
    return false;
  }
  
  boolean isBluetoothEnabled()
  {
    boolean bool2 = false;
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean bool1 = bool2;
    if (localBluetoothAdapter != null)
    {
      bool1 = bool2;
      if (localBluetoothAdapter.isEnabled()) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isConnected()
  {
    boolean bool = false;
    if (sService == null) {
      return false;
    }
    try
    {
      int i = sService.getConnectionState(this);
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean isEncrypted()
  {
    if (sService == null) {
      return false;
    }
    try
    {
      int i = sService.getConnectionState(this);
      return i > 1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean removeBond()
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot remove Remote Device bond");
      return false;
    }
    try
    {
      Log.i("BluetoothDevice", "removeBond() for device " + getAddress() + " called by pid: " + Process.myPid() + " tid: " + Process.myTid());
      boolean bool = sService.removeBond(this);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean sdpSearch(ParcelUuid paramParcelUuid)
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot query remote device sdp records");
      return false;
    }
    try
    {
      boolean bool = sService.sdpSearch(this, paramParcelUuid);
      return bool;
    }
    catch (RemoteException paramParcelUuid)
    {
      Log.e("BluetoothDevice", "", paramParcelUuid);
    }
    return false;
  }
  
  public boolean setAlias(String paramString)
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot set Remote Device name");
      return false;
    }
    try
    {
      boolean bool = sService.setRemoteAlias(this, paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      Log.e("BluetoothDevice", "", paramString);
    }
    return false;
  }
  
  public boolean setDeviceOutOfBandData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return false;
  }
  
  public boolean setMessageAccessPermission(int paramInt)
  {
    if (sService == null) {
      return false;
    }
    try
    {
      boolean bool = sService.setMessageAccessPermission(this, paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean setPairingConfirmation(boolean paramBoolean)
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot set pairing confirmation");
      return false;
    }
    try
    {
      paramBoolean = sService.setPairingConfirmation(this, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean setPasskey(int paramInt)
  {
    return false;
  }
  
  public boolean setPhonebookAccessPermission(int paramInt)
  {
    if (sService == null) {
      return false;
    }
    try
    {
      boolean bool = sService.setPhonebookAccessPermission(this, paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public boolean setPin(byte[] paramArrayOfByte)
  {
    if (sService == null)
    {
      Log.e("BluetoothDevice", "BT not enabled. Cannot set Remote Device pin");
      return false;
    }
    try
    {
      boolean bool = sService.setPin(this, true, paramArrayOfByte.length, paramArrayOfByte);
      return bool;
    }
    catch (RemoteException paramArrayOfByte)
    {
      Log.e("BluetoothDevice", "", paramArrayOfByte);
    }
    return false;
  }
  
  public boolean setRemoteOutOfBandData()
  {
    return false;
  }
  
  public boolean setSimAccessPermission(int paramInt)
  {
    if (sService == null) {
      return false;
    }
    try
    {
      boolean bool = sService.setSimAccessPermission(this, paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothDevice", "", localRemoteException);
    }
    return false;
  }
  
  public String toString()
  {
    return this.mAddress;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mAddress);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */