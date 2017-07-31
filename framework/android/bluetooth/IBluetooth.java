package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;

public abstract interface IBluetooth
  extends IInterface
{
  public abstract boolean cancelBondProcess(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean cancelDiscovery()
    throws RemoteException;
  
  public abstract boolean configHciSnoopLog(boolean paramBoolean)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor connectSocket(BluetoothDevice paramBluetoothDevice, int paramInt1, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean createBond(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean createBondOutOfBand(BluetoothDevice paramBluetoothDevice, int paramInt, OobData paramOobData)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor createSocketChannel(int paramInt1, String paramString, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean disable()
    throws RemoteException;
  
  public abstract boolean enable()
    throws RemoteException;
  
  public abstract boolean enableNoAutoConnect()
    throws RemoteException;
  
  public abstract boolean factoryReset()
    throws RemoteException;
  
  public abstract boolean fetchRemoteUuids(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getAdapterConnectionState()
    throws RemoteException;
  
  public abstract String getAddress()
    throws RemoteException;
  
  public abstract int getBondState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract BluetoothDevice[] getBondedDevices()
    throws RemoteException;
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getDiscoverableTimeout()
    throws RemoteException;
  
  public abstract int getMessageAccessPermission(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract String getName()
    throws RemoteException;
  
  public abstract int getPhonebookAccessPermission(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getProfileConnectionState(int paramInt)
    throws RemoteException;
  
  public abstract String getRemoteAlias(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getRemoteClass(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract String getRemoteName(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getRemoteType(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract ParcelUuid[] getRemoteUuids(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getScanMode()
    throws RemoteException;
  
  public abstract int getSimAccessPermission(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getSocketOpt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int getState()
    throws RemoteException;
  
  public abstract ParcelUuid[] getUuids()
    throws RemoteException;
  
  public abstract boolean isActivityAndEnergyReportingSupported()
    throws RemoteException;
  
  public abstract boolean isDiscovering()
    throws RemoteException;
  
  public abstract boolean isEnabled()
    throws RemoteException;
  
  public abstract boolean isMultiAdvertisementSupported()
    throws RemoteException;
  
  public abstract boolean isOffloadedFilteringSupported()
    throws RemoteException;
  
  public abstract boolean isOffloadedScanBatchingSupported()
    throws RemoteException;
  
  public abstract boolean isPeripheralModeSupported()
    throws RemoteException;
  
  public abstract void onBrEdrDown()
    throws RemoteException;
  
  public abstract void onLeServiceUp()
    throws RemoteException;
  
  public abstract void registerCallback(IBluetoothCallback paramIBluetoothCallback)
    throws RemoteException;
  
  public abstract boolean removeBond(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract BluetoothActivityEnergyInfo reportActivityInfo()
    throws RemoteException;
  
  public abstract void requestActivityInfo(ResultReceiver paramResultReceiver)
    throws RemoteException;
  
  public abstract boolean sdpSearch(BluetoothDevice paramBluetoothDevice, ParcelUuid paramParcelUuid)
    throws RemoteException;
  
  public abstract void sendConnectionStateChange(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean setDiscoverableTimeout(int paramInt)
    throws RemoteException;
  
  public abstract boolean setMessageAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean setName(String paramString)
    throws RemoteException;
  
  public abstract boolean setPairingConfirmation(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setPasskey(BluetoothDevice paramBluetoothDevice, boolean paramBoolean, int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract boolean setPhonebookAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean setPin(BluetoothDevice paramBluetoothDevice, boolean paramBoolean, int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract boolean setRemoteAlias(BluetoothDevice paramBluetoothDevice, String paramString)
    throws RemoteException;
  
  public abstract boolean setScanMode(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean setSimAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract int setSocketOpt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4)
    throws RemoteException;
  
  public abstract boolean startDiscovery()
    throws RemoteException;
  
  public abstract void unregisterCallback(IBluetoothCallback paramIBluetoothCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetooth
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetooth";
    static final int TRANSACTION_cancelBondProcess = 22;
    static final int TRANSACTION_cancelDiscovery = 15;
    static final int TRANSACTION_configHciSnoopLog = 48;
    static final int TRANSACTION_connectSocket = 46;
    static final int TRANSACTION_createBond = 20;
    static final int TRANSACTION_createBondOutOfBand = 21;
    static final int TRANSACTION_createSocketChannel = 47;
    static final int TRANSACTION_disable = 5;
    static final int TRANSACTION_enable = 3;
    static final int TRANSACTION_enableNoAutoConnect = 4;
    static final int TRANSACTION_factoryReset = 49;
    static final int TRANSACTION_fetchRemoteUuids = 32;
    static final int TRANSACTION_getAdapterConnectionState = 17;
    static final int TRANSACTION_getAddress = 6;
    static final int TRANSACTION_getBondState = 24;
    static final int TRANSACTION_getBondedDevices = 19;
    static final int TRANSACTION_getConnectionState = 25;
    static final int TRANSACTION_getDiscoverableTimeout = 12;
    static final int TRANSACTION_getMessageAccessPermission = 39;
    static final int TRANSACTION_getName = 9;
    static final int TRANSACTION_getPhonebookAccessPermission = 37;
    static final int TRANSACTION_getProfileConnectionState = 18;
    static final int TRANSACTION_getRemoteAlias = 28;
    static final int TRANSACTION_getRemoteClass = 30;
    static final int TRANSACTION_getRemoteName = 26;
    static final int TRANSACTION_getRemoteType = 27;
    static final int TRANSACTION_getRemoteUuids = 31;
    static final int TRANSACTION_getScanMode = 10;
    static final int TRANSACTION_getSimAccessPermission = 41;
    static final int TRANSACTION_getSocketOpt = 60;
    static final int TRANSACTION_getState = 2;
    static final int TRANSACTION_getUuids = 7;
    static final int TRANSACTION_isActivityAndEnergyReportingSupported = 54;
    static final int TRANSACTION_isDiscovering = 16;
    static final int TRANSACTION_isEnabled = 1;
    static final int TRANSACTION_isMultiAdvertisementSupported = 50;
    static final int TRANSACTION_isOffloadedFilteringSupported = 52;
    static final int TRANSACTION_isOffloadedScanBatchingSupported = 53;
    static final int TRANSACTION_isPeripheralModeSupported = 51;
    static final int TRANSACTION_onBrEdrDown = 58;
    static final int TRANSACTION_onLeServiceUp = 57;
    static final int TRANSACTION_registerCallback = 44;
    static final int TRANSACTION_removeBond = 23;
    static final int TRANSACTION_reportActivityInfo = 55;
    static final int TRANSACTION_requestActivityInfo = 56;
    static final int TRANSACTION_sdpSearch = 33;
    static final int TRANSACTION_sendConnectionStateChange = 43;
    static final int TRANSACTION_setDiscoverableTimeout = 13;
    static final int TRANSACTION_setMessageAccessPermission = 40;
    static final int TRANSACTION_setName = 8;
    static final int TRANSACTION_setPairingConfirmation = 36;
    static final int TRANSACTION_setPasskey = 35;
    static final int TRANSACTION_setPhonebookAccessPermission = 38;
    static final int TRANSACTION_setPin = 34;
    static final int TRANSACTION_setRemoteAlias = 29;
    static final int TRANSACTION_setScanMode = 11;
    static final int TRANSACTION_setSimAccessPermission = 42;
    static final int TRANSACTION_setSocketOpt = 59;
    static final int TRANSACTION_startDiscovery = 14;
    static final int TRANSACTION_unregisterCallback = 45;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetooth");
    }
    
    public static IBluetooth asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetooth");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetooth))) {
        return (IBluetooth)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool;
      Object localObject1;
      label1151:
      label1208:
      label1242:
      label1247:
      label1308:
      label1369:
      label1677:
      label1835:
      label1887:
      label1920:
      label1925:
      label1967:
      label2009:
      label2015:
      label2057:
      label2099:
      label2105:
      label2147:
      label2181:
      label2187:
      label2303:
      label2419:
      label2535:
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetooth");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isEnabled();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = getState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = enable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = enableNoAutoConnect();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = disable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramParcel1 = getAddress();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramParcel1 = getUuids();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = setName(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramParcel1 = getName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = getScanMode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = setScanMode(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = getDiscoverableTimeout();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = setDiscoverableTimeout(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = startDiscovery();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = cancelDiscovery();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isDiscovering();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = getAdapterConnectionState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = getProfileConnectionState(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramParcel1 = getBondedDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = createBond((BluetoothDevice)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label1151;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 21: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1242;
          }
          paramParcel1 = (OobData)OobData.CREATOR.createFromParcel(paramParcel1);
          bool = createBondOutOfBand((BluetoothDevice)localObject1, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1247;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label1208;
        }
      case 22: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = cancelBondProcess(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1308;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 23: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = removeBond(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1369;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 24: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getBondState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getConnectionState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getRemoteName(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getRemoteType(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 28: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getRemoteAlias(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setRemoteAlias((BluetoothDevice)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label1677;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 30: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getRemoteClass(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getRemoteUuids(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        }
      case 32: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = fetchRemoteUuids(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1835;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 33: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1920;
          }
          paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          bool = sdpSearch((BluetoothDevice)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1925;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label1887;
        }
      case 34: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2009;
          }
          bool = true;
          bool = setPin((BluetoothDevice)localObject1, bool, paramParcel1.readInt(), paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2015;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label1967;
        }
      case 35: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2099;
          }
          bool = true;
          bool = setPasskey((BluetoothDevice)localObject1, bool, paramParcel1.readInt(), paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2105;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label2057;
        }
      case 36: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2181;
          }
          bool = true;
          bool = setPairingConfirmation((BluetoothDevice)localObject1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label2187;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label2147;
        }
      case 37: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getPhonebookAccessPermission(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 38: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setPhonebookAccessPermission((BluetoothDevice)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2303;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 39: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getMessageAccessPermission(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 40: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setMessageAccessPermission((BluetoothDevice)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2419;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 41: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getSimAccessPermission(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 42: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setSimAccessPermission((BluetoothDevice)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2535;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 43: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          sendConnectionStateChange((BluetoothDevice)localObject1, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 44: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        registerCallback(IBluetoothCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 45: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        unregisterCallback(IBluetoothCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 46: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2741;
          }
          localObject2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = connectSocket((BluetoothDevice)localObject1, paramInt1, (ParcelUuid)localObject2, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2747;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label2696;
          paramParcel2.writeInt(0);
        }
      case 47: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = paramParcel1.readInt();
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = createSocketChannel(paramInt1, (String)localObject2, (ParcelUuid)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2838;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 48: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = configHciSnoopLog(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label2894;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 49: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = factoryReset();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 50: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isMultiAdvertisementSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 51: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isPeripheralModeSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 52: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isOffloadedFilteringSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 53: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isOffloadedScanBatchingSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 54: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        bool = isActivityAndEnergyReportingSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 55: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramParcel1 = reportActivityInfo();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 56: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestActivityInfo(paramParcel1);
          return true;
        }
      case 57: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        onLeServiceUp();
        paramParcel2.writeNoException();
        return true;
      case 58: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        onBrEdrDown();
        paramParcel2.writeNoException();
        return true;
      case 59: 
        label2696:
        label2741:
        label2747:
        label2838:
        label2894:
        paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
        paramInt1 = setSocketOpt(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetooth");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      int i = paramParcel1.readInt();
      int j = paramParcel1.readInt();
      if (j < 0) {}
      for (paramParcel1 = null;; paramParcel1 = new byte[j])
      {
        paramInt1 = getSocketOpt(paramInt1, paramInt2, i, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetooth
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public boolean cancelBondProcess(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(22, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean cancelDiscovery()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 15
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean configHciSnoopLog(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 48
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 54 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 57	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 64	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 64	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      public ParcelFileDescriptor connectSocket(BluetoothDevice paramBluetoothDevice, int paramInt1, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              if (paramParcelUuid != null)
              {
                localParcel1.writeInt(1);
                paramParcelUuid.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt2);
                localParcel1.writeInt(paramInt3);
                this.mRemote.transact(46, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label158;
                }
                paramBluetoothDevice = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramBluetoothDevice;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label158:
          paramBluetoothDevice = null;
        }
      }
      
      public boolean createBond(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(20, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean createBondOutOfBand(BluetoothDevice paramBluetoothDevice, int paramInt, OobData paramOobData)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramOobData != null)
              {
                localParcel1.writeInt(1);
                paramOobData.writeToParcel(localParcel1, 0);
                this.mRemote.transact(21, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label136;
                }
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label136:
          boolean bool = false;
        }
      }
      
      public ParcelFileDescriptor createSocketChannel(int paramInt1, String paramString, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            localParcel1.writeInt(paramInt1);
            localParcel1.writeString(paramString);
            if (paramParcelUuid != null)
            {
              localParcel1.writeInt(1);
              paramParcelUuid.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt2);
              localParcel1.writeInt(paramInt3);
              this.mRemote.transact(47, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean disable()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_5
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 54 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 57	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 61	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 64	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      /* Error */
      public boolean enable()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_3
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 54 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 57	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 61	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 64	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      /* Error */
      public boolean enableNoAutoConnect()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_4
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 54 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 57	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 61	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 64	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      /* Error */
      public boolean factoryReset()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 49
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public boolean fetchRemoteUuids(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(32, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getAdapterConnectionState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getAddress()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int getBondState(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 24
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public BluetoothDevice[] getBondedDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          BluetoothDevice[] arrayOfBluetoothDevice = (BluetoothDevice[])localParcel2.createTypedArray(BluetoothDevice.CREATOR);
          return arrayOfBluetoothDevice;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int getConnectionState(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 25
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public int getDiscoverableTimeout()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetooth";
      }
      
      /* Error */
      public int getMessageAccessPermission(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 39
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public String getName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int getPhonebookAccessPermission(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 37
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public int getProfileConnectionState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getRemoteAlias(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 28
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 54 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 57	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 109	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 64	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramBluetoothDevice	BluetoothDevice
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public int getRemoteClass(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 30
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      /* Error */
      public String getRemoteName(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 26
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 54 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 57	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 109	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 64	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramBluetoothDevice	BluetoothDevice
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public int getRemoteType(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 27
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      /* Error */
      public ParcelUuid[] getRemoteUuids(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +54 -> 69
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 31
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 54 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 57	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: getstatic 136	android/os/ParcelUuid:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: invokevirtual 118	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   55: checkcast 138	[Landroid/os/ParcelUuid;
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 64	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: aload_1
        //   68: areturn
        //   69: aload_2
        //   70: iconst_0
        //   71: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   74: goto -45 -> 29
        //   77: astore_1
        //   78: aload_3
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   3	80	2	localParcel1	Parcel
        //   7	72	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	77	finally
        //   18	29	77	finally
        //   29	59	77	finally
        //   69	74	77	finally
      }
      
      public int getScanMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int getSimAccessPermission(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 41
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramBluetoothDevice	BluetoothDevice
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      /* Error */
      public int getSocketOpt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   35: aload 4
        //   37: ifnonnull +56 -> 93
        //   40: aload 5
        //   42: iconst_m1
        //   43: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 60
        //   52: aload 5
        //   54: aload 6
        //   56: iconst_0
        //   57: invokeinterface 54 5 0
        //   62: pop
        //   63: aload 6
        //   65: invokevirtual 57	android/os/Parcel:readException	()V
        //   68: aload 6
        //   70: invokevirtual 61	android/os/Parcel:readInt	()I
        //   73: istore_1
        //   74: aload 6
        //   76: aload 4
        //   78: invokevirtual 146	android/os/Parcel:readByteArray	([B)V
        //   81: aload 6
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload 5
        //   88: invokevirtual 64	android/os/Parcel:recycle	()V
        //   91: iload_1
        //   92: ireturn
        //   93: aload 5
        //   95: aload 4
        //   97: arraylength
        //   98: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   101: goto -55 -> 46
        //   104: astore 4
        //   106: aload 6
        //   108: invokevirtual 64	android/os/Parcel:recycle	()V
        //   111: aload 5
        //   113: invokevirtual 64	android/os/Parcel:recycle	()V
        //   116: aload 4
        //   118: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	119	0	this	Proxy
        //   0	119	1	paramInt1	int
        //   0	119	2	paramInt2	int
        //   0	119	3	paramInt3	int
        //   0	119	4	paramArrayOfByte	byte[]
        //   3	109	5	localParcel1	Parcel
        //   8	99	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	104	finally
        //   40	46	104	finally
        //   46	81	104	finally
        //   93	101	104	finally
      }
      
      public int getState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ParcelUuid[] getUuids()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ParcelUuid[] arrayOfParcelUuid = (ParcelUuid[])localParcel2.createTypedArray(ParcelUuid.CREATOR);
          return arrayOfParcelUuid;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isActivityAndEnergyReportingSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 54
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isDiscovering()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 16
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isEnabled()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 54 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 57	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 61	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 64	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      /* Error */
      public boolean isMultiAdvertisementSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 50
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isOffloadedFilteringSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 52
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isOffloadedScanBatchingSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 53
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isPeripheralModeSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 51
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void onBrEdrDown()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(58, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onLeServiceUp()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          this.mRemote.transact(57, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerCallback(IBluetoothCallback paramIBluetoothCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          if (paramIBluetoothCallback != null) {
            localIBinder = paramIBluetoothCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(44, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean removeBond(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(23, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public BluetoothActivityEnergyInfo reportActivityInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 55
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 54 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 57	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 61	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 173	android/bluetooth/BluetoothActivityEnergyInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 86 2 0
        //   49: checkcast 172	android/bluetooth/BluetoothActivityEnergyInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 64	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 64	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localBluetoothActivityEnergyInfo	BluetoothActivityEnergyInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public void requestActivityInfo(ResultReceiver paramResultReceiver)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 178	android/os/ResultReceiver:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 56
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 64	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 64	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramResultReceiver	ResultReceiver
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public boolean sdpSearch(BluetoothDevice paramBluetoothDevice, ParcelUuid paramParcelUuid)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              if (paramParcelUuid != null)
              {
                localParcel1.writeInt(1);
                paramParcelUuid.writeToParcel(localParcel1, 0);
                this.mRemote.transact(33, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
                }
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label130:
          boolean bool = false;
        }
      }
      
      /* Error */
      public void sendConnectionStateChange(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +68 -> 86
        //   21: aload 5
        //   23: iconst_1
        //   24: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 5
        //   30: iconst_0
        //   31: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 5
        //   36: iload_2
        //   37: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 43
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 54 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 57	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   92: goto -58 -> 34
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 64	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 64	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramBluetoothDevice	BluetoothDevice
        //   0	108	2	paramInt1	int
        //   0	108	3	paramInt2	int
        //   0	108	4	paramInt3	int
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	95	finally
        //   21	34	95	finally
        //   34	75	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public boolean setDiscoverableTimeout(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 13
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 54 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 57	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 61	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 64	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public boolean setMessageAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(40, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean setName(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 98	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 8
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 57	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 61	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public boolean setPairingConfirmation(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              break label123;
              localParcel1.writeInt(i);
              this.mRemote.transact(36, localParcel1, localParcel2, 0);
              localParcel2.readException();
              i = localParcel2.readInt();
              if (i != 0)
              {
                paramBoolean = true;
                label79:
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label123:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label79;
          } while (!paramBoolean);
        }
      }
      
      public boolean setPasskey(BluetoothDevice paramBluetoothDevice, boolean paramBoolean, int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              break label139;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt);
              localParcel1.writeByteArray(paramArrayOfByte);
              this.mRemote.transact(35, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                paramBoolean = true;
                label94:
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label139:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label94;
          } while (!paramBoolean);
        }
      }
      
      public boolean setPhonebookAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(38, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean setPin(BluetoothDevice paramBluetoothDevice, boolean paramBoolean, int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              break label139;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt);
              localParcel1.writeByteArray(paramArrayOfByte);
              this.mRemote.transact(34, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                paramBoolean = true;
                label94:
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label139:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label94;
          } while (!paramBoolean);
        }
      }
      
      public boolean setRemoteAlias(BluetoothDevice paramBluetoothDevice, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(29, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean setScanMode(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 54 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 57	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 61	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 64	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public boolean setSimAccessPermission(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(42, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int setSocketOpt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt4);
          this.mRemote.transact(59, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean startDiscovery()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetooth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 14
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 54 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 57	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void unregisterCallback(IBluetoothCallback paramIBluetoothCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetooth");
          if (paramIBluetoothCallback != null) {
            localIBinder = paramIBluetoothCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(45, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetooth.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */