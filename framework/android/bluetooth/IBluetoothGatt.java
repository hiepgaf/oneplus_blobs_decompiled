package android.bluetooth;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.WorkSource;
import java.util.List;

public abstract interface IBluetoothGatt
  extends IInterface
{
  public abstract void addCharacteristic(int paramInt1, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void addDescriptor(int paramInt1, ParcelUuid paramParcelUuid, int paramInt2)
    throws RemoteException;
  
  public abstract void addIncludedService(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
    throws RemoteException;
  
  public abstract void beginReliableWrite(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void beginServiceDeclaration(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void clearServices(int paramInt)
    throws RemoteException;
  
  public abstract void clientConnect(int paramInt1, String paramString, boolean paramBoolean, int paramInt2)
    throws RemoteException;
  
  public abstract void clientDisconnect(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void configureMTU(int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract void connectionParameterUpdate(int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract void disconnectAll()
    throws RemoteException;
  
  public abstract void discoverServices(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void endReliableWrite(int paramInt, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void endServiceDeclaration(int paramInt)
    throws RemoteException;
  
  public abstract void flushPendingBatchResults(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract int numHwTrackFiltersAvailable()
    throws RemoteException;
  
  public abstract void readCharacteristic(int paramInt1, String paramString, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void readDescriptor(int paramInt1, String paramString, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void readRemoteRssi(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void refreshDevice(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void registerClient(ParcelUuid paramParcelUuid, IBluetoothGattCallback paramIBluetoothGattCallback)
    throws RemoteException;
  
  public abstract void registerForNotification(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void registerServer(ParcelUuid paramParcelUuid, IBluetoothGattServerCallback paramIBluetoothGattServerCallback)
    throws RemoteException;
  
  public abstract void removeService(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
    throws RemoteException;
  
  public abstract void sendNotification(int paramInt1, String paramString, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid1, int paramInt4, ParcelUuid paramParcelUuid2, boolean paramBoolean, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void sendResponse(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void serverConnect(int paramInt1, String paramString, boolean paramBoolean, int paramInt2)
    throws RemoteException;
  
  public abstract void serverDisconnect(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void startMultiAdvertising(int paramInt, AdvertiseData paramAdvertiseData1, AdvertiseData paramAdvertiseData2, AdvertiseSettings paramAdvertiseSettings)
    throws RemoteException;
  
  public abstract void startScan(int paramInt, boolean paramBoolean, ScanSettings paramScanSettings, List<ScanFilter> paramList, WorkSource paramWorkSource, List paramList1, String paramString)
    throws RemoteException;
  
  public abstract void stopMultiAdvertising(int paramInt)
    throws RemoteException;
  
  public abstract void stopScan(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unregAll()
    throws RemoteException;
  
  public abstract void unregisterClient(int paramInt)
    throws RemoteException;
  
  public abstract void unregisterServer(int paramInt)
    throws RemoteException;
  
  public abstract void writeCharacteristic(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void writeDescriptor(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothGatt
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGatt";
    static final int TRANSACTION_addCharacteristic = 29;
    static final int TRANSACTION_addDescriptor = 30;
    static final int TRANSACTION_addIncludedService = 28;
    static final int TRANSACTION_beginReliableWrite = 18;
    static final int TRANSACTION_beginServiceDeclaration = 27;
    static final int TRANSACTION_clearServices = 33;
    static final int TRANSACTION_clientConnect = 9;
    static final int TRANSACTION_clientDisconnect = 10;
    static final int TRANSACTION_configureMTU = 21;
    static final int TRANSACTION_connectionParameterUpdate = 22;
    static final int TRANSACTION_disconnectAll = 36;
    static final int TRANSACTION_discoverServices = 12;
    static final int TRANSACTION_endReliableWrite = 19;
    static final int TRANSACTION_endServiceDeclaration = 31;
    static final int TRANSACTION_flushPendingBatchResults = 4;
    static final int TRANSACTION_getDevicesMatchingConnectionStates = 1;
    static final int TRANSACTION_numHwTrackFiltersAvailable = 38;
    static final int TRANSACTION_readCharacteristic = 13;
    static final int TRANSACTION_readDescriptor = 15;
    static final int TRANSACTION_readRemoteRssi = 20;
    static final int TRANSACTION_refreshDevice = 11;
    static final int TRANSACTION_registerClient = 7;
    static final int TRANSACTION_registerForNotification = 17;
    static final int TRANSACTION_registerServer = 23;
    static final int TRANSACTION_removeService = 32;
    static final int TRANSACTION_sendNotification = 35;
    static final int TRANSACTION_sendResponse = 34;
    static final int TRANSACTION_serverConnect = 25;
    static final int TRANSACTION_serverDisconnect = 26;
    static final int TRANSACTION_startMultiAdvertising = 5;
    static final int TRANSACTION_startScan = 2;
    static final int TRANSACTION_stopMultiAdvertising = 6;
    static final int TRANSACTION_stopScan = 3;
    static final int TRANSACTION_unregAll = 37;
    static final int TRANSACTION_unregisterClient = 8;
    static final int TRANSACTION_unregisterServer = 24;
    static final int TRANSACTION_writeCharacteristic = 14;
    static final int TRANSACTION_writeDescriptor = 16;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothGatt");
    }
    
    public static IBluetoothGatt asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothGatt");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothGatt))) {
        return (IBluetoothGatt)localIInterface;
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
      label410:
      Object localObject3;
      Object localObject2;
      label480:
      label486:
      label625:
      label667:
      label673:
      int i;
      int j;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothGatt");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramParcel1 = getDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          if (paramParcel1.readInt() == 0) {
            break label480;
          }
          localObject1 = (ScanSettings)ScanSettings.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.createTypedArrayList(ScanFilter.CREATOR);
          if (paramParcel1.readInt() == 0) {
            break label486;
          }
        }
        for (localObject2 = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          startScan(paramInt1, bool, (ScanSettings)localObject1, (List)localObject3, (WorkSource)localObject2, paramParcel1.readArrayList(getClass().getClassLoader()), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          bool = false;
          break;
          localObject1 = null;
          break label410;
        }
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          stopScan(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          flushPendingBatchResults(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (AdvertiseData)AdvertiseData.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label667;
          }
          localObject2 = (AdvertiseData)AdvertiseData.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label673;
          }
        }
        for (paramParcel1 = (AdvertiseSettings)AdvertiseSettings.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startMultiAdvertising(paramInt1, (AdvertiseData)localObject1, (AdvertiseData)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label625;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        stopMultiAdvertising(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          registerClient((ParcelUuid)localObject1, IBluetoothGattCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        unregisterClient(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          clientConnect(paramInt1, (String)localObject1, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        clientDisconnect(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        refreshDevice(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        discoverServices(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        readCharacteristic(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        writeCharacteristic(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        readDescriptor(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        writeDescriptor(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        localObject1 = paramParcel1.readString();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          registerForNotification(paramInt1, (String)localObject1, paramInt2, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        beginReliableWrite(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          endReliableWrite(paramInt1, (String)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        readRemoteRssi(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        configureMTU(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        connectionParameterUpdate(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          registerServer((ParcelUuid)localObject1, IBluetoothGattServerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        unregisterServer(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          serverConnect(paramInt1, (String)localObject1, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        serverDisconnect(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        j = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1481;
          }
        }
        for (bool = true;; bool = false)
        {
          beginServiceDeclaration(paramInt1, paramInt2, i, j, (ParcelUuid)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 28: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addIncludedService(paramInt1, paramInt2, i, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          addCharacteristic(paramInt1, (ParcelUuid)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 30: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          addDescriptor(paramInt1, (ParcelUuid)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        endServiceDeclaration(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          removeService(paramInt1, paramInt2, i, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        clearServices(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 34: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        sendResponse(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        paramInt1 = paramParcel1.readInt();
        localObject3 = paramParcel1.readString();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          j = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1931;
          }
          localObject2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1937;
          }
        }
        for (bool = true;; bool = false)
        {
          sendNotification(paramInt1, (String)localObject3, paramInt2, i, (ParcelUuid)localObject1, j, (ParcelUuid)localObject2, bool, paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1886;
        }
      case 36: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        disconnectAll();
        paramParcel2.writeNoException();
        return true;
      case 37: 
        label1481:
        label1886:
        label1931:
        label1937:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
        unregAll();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothGatt");
      paramInt1 = numHwTrackFiltersAvailable();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IBluetoothGatt
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addCharacteristic(int paramInt1, ParcelUuid paramParcelUuid, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_2
        //   24: ifnull +62 -> 86
        //   27: aload 5
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 5
        //   36: iconst_0
        //   37: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 29
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 52 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 55	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -52 -> 40
        //   95: astore_2
        //   96: aload 6
        //   98: invokevirtual 58	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 58	android/os/Parcel:recycle	()V
        //   106: aload_2
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramInt1	int
        //   0	108	2	paramParcelUuid	ParcelUuid
        //   0	108	3	paramInt2	int
        //   0	108	4	paramInt3	int
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	95	finally
        //   27	40	95	finally
        //   40	75	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public void addDescriptor(int paramInt1, ParcelUuid paramParcelUuid, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 30
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 52 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 55	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 58	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_2
        //   89: aload 5
        //   91: invokevirtual 58	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 58	android/os/Parcel:recycle	()V
        //   99: aload_2
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramInt1	int
        //   0	101	2	paramParcelUuid	ParcelUuid
        //   0	101	3	paramInt2	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void addIncludedService(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload 4
        //   37: ifnull +50 -> 87
        //   40: aload 5
        //   42: iconst_1
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: aload_0
        //   55: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   58: bipush 28
        //   60: aload 5
        //   62: aload 6
        //   64: iconst_0
        //   65: invokeinterface 52 5 0
        //   70: pop
        //   71: aload 6
        //   73: invokevirtual 55	android/os/Parcel:readException	()V
        //   76: aload 6
        //   78: invokevirtual 58	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: invokevirtual 58	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 5
        //   89: iconst_0
        //   90: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   93: goto -39 -> 54
        //   96: astore 4
        //   98: aload 6
        //   100: invokevirtual 58	android/os/Parcel:recycle	()V
        //   103: aload 5
        //   105: invokevirtual 58	android/os/Parcel:recycle	()V
        //   108: aload 4
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramInt1	int
        //   0	111	2	paramInt2	int
        //   0	111	3	paramInt3	int
        //   0	111	4	paramParcelUuid	ParcelUuid
        //   3	101	5	localParcel1	Parcel
        //   8	91	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	96	finally
        //   40	54	96	finally
        //   54	76	96	finally
        //   87	93	96	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void beginReliableWrite(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void beginServiceDeclaration(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
            localParcel1.writeInt(paramInt1);
            localParcel1.writeInt(paramInt2);
            localParcel1.writeInt(paramInt3);
            localParcel1.writeInt(paramInt4);
            if (paramParcelUuid != null)
            {
              localParcel1.writeInt(1);
              paramParcelUuid.writeToParcel(localParcel1, 0);
              break label135;
              localParcel1.writeInt(paramInt1);
              this.mRemote.transact(27, localParcel1, localParcel2, 0);
              localParcel2.readException();
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
          label135:
          do
          {
            paramInt1 = 0;
            break;
          } while (!paramBoolean);
          paramInt1 = i;
        }
      }
      
      public void clearServices(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clientConnect(int paramInt1, String paramString, boolean paramBoolean, int paramInt2)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clientDisconnect(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void configureMTU(int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void connectionParameterUpdate(int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void disconnectAll()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void discoverServices(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void endReliableWrite(int paramInt, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void endServiceDeclaration(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void flushPendingBatchResults(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramArrayOfInt = localParcel2.createTypedArrayList(BluetoothDevice.CREATOR);
          return paramArrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetoothGatt";
      }
      
      public int numHwTrackFiltersAvailable()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          this.mRemote.transact(38, localParcel1, localParcel2, 0);
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
      
      public void readCharacteristic(int paramInt1, String paramString, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void readDescriptor(int paramInt1, String paramString, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void readRemoteRssi(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void refreshDevice(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void registerClient(ParcelUuid paramParcelUuid, IBluetoothGattCallback paramIBluetoothGattCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +68 -> 88
        //   23: aload 4
        //   25: iconst_1
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_1
        //   30: aload 4
        //   32: iconst_0
        //   33: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   36: aload_3
        //   37: astore_1
        //   38: aload_2
        //   39: ifnull +10 -> 49
        //   42: aload_2
        //   43: invokeinterface 122 1 0
        //   48: astore_1
        //   49: aload 4
        //   51: aload_1
        //   52: invokevirtual 125	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 7
        //   61: aload 4
        //   63: aload 5
        //   65: iconst_0
        //   66: invokeinterface 52 5 0
        //   71: pop
        //   72: aload 5
        //   74: invokevirtual 55	android/os/Parcel:readException	()V
        //   77: aload 5
        //   79: invokevirtual 58	android/os/Parcel:recycle	()V
        //   82: aload 4
        //   84: invokevirtual 58	android/os/Parcel:recycle	()V
        //   87: return
        //   88: aload 4
        //   90: iconst_0
        //   91: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   94: goto -58 -> 36
        //   97: astore_1
        //   98: aload 5
        //   100: invokevirtual 58	android/os/Parcel:recycle	()V
        //   103: aload 4
        //   105: invokevirtual 58	android/os/Parcel:recycle	()V
        //   108: aload_1
        //   109: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	110	0	this	Proxy
        //   0	110	1	paramParcelUuid	ParcelUuid
        //   0	110	2	paramIBluetoothGattCallback	IBluetoothGattCallback
        //   1	36	3	localObject	Object
        //   5	99	4	localParcel1	Parcel
        //   10	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	97	finally
        //   23	36	97	finally
        //   42	49	97	finally
        //   49	77	97	finally
        //   88	94	97	finally
      }
      
      public void registerForNotification(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void registerServer(ParcelUuid paramParcelUuid, IBluetoothGattServerCallback paramIBluetoothGattServerCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +68 -> 88
        //   23: aload 4
        //   25: iconst_1
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_1
        //   30: aload 4
        //   32: iconst_0
        //   33: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   36: aload_3
        //   37: astore_1
        //   38: aload_2
        //   39: ifnull +10 -> 49
        //   42: aload_2
        //   43: invokeinterface 132 1 0
        //   48: astore_1
        //   49: aload 4
        //   51: aload_1
        //   52: invokevirtual 125	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 23
        //   61: aload 4
        //   63: aload 5
        //   65: iconst_0
        //   66: invokeinterface 52 5 0
        //   71: pop
        //   72: aload 5
        //   74: invokevirtual 55	android/os/Parcel:readException	()V
        //   77: aload 5
        //   79: invokevirtual 58	android/os/Parcel:recycle	()V
        //   82: aload 4
        //   84: invokevirtual 58	android/os/Parcel:recycle	()V
        //   87: return
        //   88: aload 4
        //   90: iconst_0
        //   91: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   94: goto -58 -> 36
        //   97: astore_1
        //   98: aload 5
        //   100: invokevirtual 58	android/os/Parcel:recycle	()V
        //   103: aload 4
        //   105: invokevirtual 58	android/os/Parcel:recycle	()V
        //   108: aload_1
        //   109: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	110	0	this	Proxy
        //   0	110	1	paramParcelUuid	ParcelUuid
        //   0	110	2	paramIBluetoothGattServerCallback	IBluetoothGattServerCallback
        //   1	36	3	localObject	Object
        //   5	99	4	localParcel1	Parcel
        //   10	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	97	finally
        //   23	36	97	finally
        //   42	49	97	finally
        //   49	77	97	finally
        //   88	94	97	finally
      }
      
      /* Error */
      public void removeService(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload 4
        //   37: ifnull +50 -> 87
        //   40: aload 5
        //   42: iconst_1
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokevirtual 46	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: aload_0
        //   55: getfield 19	android/bluetooth/IBluetoothGatt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   58: bipush 32
        //   60: aload 5
        //   62: aload 6
        //   64: iconst_0
        //   65: invokeinterface 52 5 0
        //   70: pop
        //   71: aload 6
        //   73: invokevirtual 55	android/os/Parcel:readException	()V
        //   76: aload 6
        //   78: invokevirtual 58	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: invokevirtual 58	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 5
        //   89: iconst_0
        //   90: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   93: goto -39 -> 54
        //   96: astore 4
        //   98: aload 6
        //   100: invokevirtual 58	android/os/Parcel:recycle	()V
        //   103: aload 5
        //   105: invokevirtual 58	android/os/Parcel:recycle	()V
        //   108: aload 4
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramInt1	int
        //   0	111	2	paramInt2	int
        //   0	111	3	paramInt3	int
        //   0	111	4	paramParcelUuid	ParcelUuid
        //   3	101	5	localParcel1	Parcel
        //   8	91	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	96	finally
        //   40	54	96	finally
        //   54	76	96	finally
        //   87	93	96	finally
      }
      
      public void sendNotification(int paramInt1, String paramString, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid1, int paramInt4, ParcelUuid paramParcelUuid2, boolean paramBoolean, byte[] paramArrayOfByte)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
            localParcel1.writeInt(paramInt1);
            localParcel1.writeString(paramString);
            localParcel1.writeInt(paramInt2);
            localParcel1.writeInt(paramInt3);
            if (paramParcelUuid1 != null)
            {
              localParcel1.writeInt(1);
              paramParcelUuid1.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt4);
              if (paramParcelUuid2 != null)
              {
                localParcel1.writeInt(1);
                paramParcelUuid2.writeToParcel(localParcel1, 0);
                break label175;
                localParcel1.writeInt(paramInt1);
                localParcel1.writeByteArray(paramArrayOfByte);
                this.mRemote.transact(35, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
          label175:
          while (!paramBoolean)
          {
            paramInt1 = 0;
            break;
          }
          paramInt1 = i;
        }
      }
      
      public void sendResponse(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void serverConnect(int paramInt1, String paramString, boolean paramBoolean, int paramInt2)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void serverDisconnect(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startMultiAdvertising(int paramInt, AdvertiseData paramAdvertiseData1, AdvertiseData paramAdvertiseData2, AdvertiseSettings paramAdvertiseSettings)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
            localParcel1.writeInt(paramInt);
            if (paramAdvertiseData1 != null)
            {
              localParcel1.writeInt(1);
              paramAdvertiseData1.writeToParcel(localParcel1, 0);
              if (paramAdvertiseData2 != null)
              {
                localParcel1.writeInt(1);
                paramAdvertiseData2.writeToParcel(localParcel1, 0);
                if (paramAdvertiseSettings == null) {
                  break label139;
                }
                localParcel1.writeInt(1);
                paramAdvertiseSettings.writeToParcel(localParcel1, 0);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
          label139:
          localParcel1.writeInt(0);
        }
      }
      
      public void startScan(int paramInt, boolean paramBoolean, ScanSettings paramScanSettings, List<ScanFilter> paramList, WorkSource paramWorkSource, List paramList1, String paramString)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label155:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
            localParcel1.writeInt(paramInt);
            if (paramBoolean)
            {
              paramInt = i;
              localParcel1.writeInt(paramInt);
              if (paramScanSettings != null)
              {
                localParcel1.writeInt(1);
                paramScanSettings.writeToParcel(localParcel1, 0);
                localParcel1.writeTypedList(paramList);
                if (paramWorkSource == null) {
                  break label155;
                }
                localParcel1.writeInt(1);
                paramWorkSource.writeToParcel(localParcel1, 0);
                localParcel1.writeList(paramList1);
                localParcel1.writeString(paramString);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              paramInt = 0;
              continue;
            }
            localParcel1.writeInt(0);
            continue;
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void stopMultiAdvertising(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopScan(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregAll()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterClient(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterServer(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void writeCharacteristic(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void writeDescriptor(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothGatt");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothGatt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */