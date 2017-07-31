package android.bluetooth;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IBluetoothGattCallback
  extends IInterface
{
  public abstract void onBatchScanResults(List<ScanResult> paramList)
    throws RemoteException;
  
  public abstract void onCharacteristicRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onCharacteristicWrite(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onClientConnectionState(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
    throws RemoteException;
  
  public abstract void onClientRegistered(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onConfigureMTU(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onDescriptorRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onDescriptorWrite(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onExecuteWrite(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onFoundOrLost(boolean paramBoolean, ScanResult paramScanResult)
    throws RemoteException;
  
  public abstract void onMultiAdvertiseCallback(int paramInt, boolean paramBoolean, AdvertiseSettings paramAdvertiseSettings)
    throws RemoteException;
  
  public abstract void onNotify(String paramString, int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onReadRemoteRssi(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onScanManagerErrorCallback(int paramInt)
    throws RemoteException;
  
  public abstract void onScanResult(ScanResult paramScanResult)
    throws RemoteException;
  
  public abstract void onSearchComplete(String paramString, List<BluetoothGattService> paramList, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothGattCallback
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGattCallback";
    static final int TRANSACTION_onBatchScanResults = 4;
    static final int TRANSACTION_onCharacteristicRead = 6;
    static final int TRANSACTION_onCharacteristicWrite = 7;
    static final int TRANSACTION_onClientConnectionState = 2;
    static final int TRANSACTION_onClientRegistered = 1;
    static final int TRANSACTION_onConfigureMTU = 15;
    static final int TRANSACTION_onDescriptorRead = 9;
    static final int TRANSACTION_onDescriptorWrite = 10;
    static final int TRANSACTION_onExecuteWrite = 8;
    static final int TRANSACTION_onFoundOrLost = 16;
    static final int TRANSACTION_onMultiAdvertiseCallback = 13;
    static final int TRANSACTION_onNotify = 11;
    static final int TRANSACTION_onReadRemoteRssi = 12;
    static final int TRANSACTION_onScanManagerErrorCallback = 14;
    static final int TRANSACTION_onScanResult = 3;
    static final int TRANSACTION_onSearchComplete = 5;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothGattCallback");
    }
    
    public static IBluetoothGattCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothGattCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothGattCallback))) {
        return (IBluetoothGattCallback)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothGattCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onClientRegistered(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onClientConnectionState(paramInt1, paramInt2, bool, paramParcel1.readString());
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ScanResult)ScanResult.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onScanResult(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onBatchScanResults(paramParcel1.createTypedArrayList(ScanResult.CREATOR));
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onSearchComplete(paramParcel1.readString(), paramParcel1.createTypedArrayList(BluetoothGattService.CREATOR), paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onCharacteristicRead(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onCharacteristicWrite(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onExecuteWrite(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onDescriptorRead(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onDescriptorWrite(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onNotify(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.createByteArray());
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onReadRemoteRssi(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          if (paramParcel1.readInt() == 0) {
            break label547;
          }
        }
        for (paramParcel1 = (AdvertiseSettings)AdvertiseSettings.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onMultiAdvertiseCallback(paramInt1, bool, paramParcel1);
          return true;
          bool = false;
          break;
        }
      case 14: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onScanManagerErrorCallback(paramParcel1.readInt());
        return true;
      case 15: 
        label547:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
        onConfigureMTU(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattCallback");
      if (paramParcel1.readInt() != 0)
      {
        bool = true;
        if (paramParcel1.readInt() == 0) {
          break label643;
        }
      }
      label643:
      for (paramParcel1 = (ScanResult)ScanResult.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onFoundOrLost(bool, paramParcel1);
        return true;
        bool = false;
        break;
      }
    }
    
    private static class Proxy
      implements IBluetoothGattCallback
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
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetoothGattCallback";
      }
      
      public void onBatchScanResults(List<ScanResult> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onCharacteristicRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onCharacteristicWrite(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onClientConnectionState(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 6
        //   17: iload_1
        //   18: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   21: aload 6
        //   23: iload_2
        //   24: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   27: iload_3
        //   28: ifeq +40 -> 68
        //   31: iload 5
        //   33: istore_1
        //   34: aload 6
        //   36: iload_1
        //   37: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   40: aload 6
        //   42: aload 4
        //   44: invokevirtual 60	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   47: aload_0
        //   48: getfield 19	android/bluetooth/IBluetoothGattCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: iconst_2
        //   52: aload 6
        //   54: aconst_null
        //   55: iconst_1
        //   56: invokeinterface 49 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 52	android/os/Parcel:recycle	()V
        //   67: return
        //   68: iconst_0
        //   69: istore_1
        //   70: goto -36 -> 34
        //   73: astore 4
        //   75: aload 6
        //   77: invokevirtual 52	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt1	int
        //   0	83	2	paramInt2	int
        //   0	83	3	paramBoolean	boolean
        //   0	83	4	paramString	String
        //   1	31	5	i	int
        //   6	70	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	27	73	finally
        //   34	62	73	finally
      }
      
      public void onClientRegistered(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onConfigureMTU(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(15, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDescriptorRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDescriptorWrite(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onExecuteWrite(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onFoundOrLost(boolean paramBoolean, ScanResult paramScanResult)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +48 -> 63
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: ifnull +43 -> 68
        //   28: aload 4
        //   30: iconst_1
        //   31: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   34: aload_2
        //   35: aload 4
        //   37: iconst_0
        //   38: invokevirtual 87	android/bluetooth/le/ScanResult:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload_0
        //   42: getfield 19	android/bluetooth/IBluetoothGattCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: bipush 16
        //   47: aload 4
        //   49: aconst_null
        //   50: iconst_1
        //   51: invokeinterface 49 5 0
        //   56: pop
        //   57: aload 4
        //   59: invokevirtual 52	android/os/Parcel:recycle	()V
        //   62: return
        //   63: iconst_0
        //   64: istore_3
        //   65: goto -47 -> 18
        //   68: aload 4
        //   70: iconst_0
        //   71: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   74: goto -33 -> 41
        //   77: astore_2
        //   78: aload 4
        //   80: invokevirtual 52	android/os/Parcel:recycle	()V
        //   83: aload_2
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramBoolean	boolean
        //   0	85	2	paramScanResult	ScanResult
        //   1	64	3	i	int
        //   5	74	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	77	finally
        //   18	24	77	finally
        //   28	41	77	finally
        //   41	57	77	finally
        //   68	74	77	finally
      }
      
      /* Error */
      public void onMultiAdvertiseCallback(int paramInt, boolean paramBoolean, AdvertiseSettings paramAdvertiseSettings)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 5
        //   17: iload_1
        //   18: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   21: iload_2
        //   22: ifeq +51 -> 73
        //   25: iload 4
        //   27: istore_1
        //   28: aload 5
        //   30: iload_1
        //   31: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   34: aload_3
        //   35: ifnull +43 -> 78
        //   38: aload 5
        //   40: iconst_1
        //   41: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   44: aload_3
        //   45: aload 5
        //   47: iconst_0
        //   48: invokevirtual 92	android/bluetooth/le/AdvertiseSettings:writeToParcel	(Landroid/os/Parcel;I)V
        //   51: aload_0
        //   52: getfield 19	android/bluetooth/IBluetoothGattCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   55: bipush 13
        //   57: aload 5
        //   59: aconst_null
        //   60: iconst_1
        //   61: invokeinterface 49 5 0
        //   66: pop
        //   67: aload 5
        //   69: invokevirtual 52	android/os/Parcel:recycle	()V
        //   72: return
        //   73: iconst_0
        //   74: istore_1
        //   75: goto -47 -> 28
        //   78: aload 5
        //   80: iconst_0
        //   81: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   84: goto -33 -> 51
        //   87: astore_3
        //   88: aload 5
        //   90: invokevirtual 52	android/os/Parcel:recycle	()V
        //   93: aload_3
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt	int
        //   0	95	2	paramBoolean	boolean
        //   0	95	3	paramAdvertiseSettings	AdvertiseSettings
        //   1	25	4	i	int
        //   6	83	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	21	87	finally
        //   28	34	87	finally
        //   38	51	87	finally
        //   51	67	87	finally
        //   78	84	87	finally
      }
      
      public void onNotify(String paramString, int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onReadRemoteRssi(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onScanManagerErrorCallback(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(14, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onScanResult(ScanResult paramScanResult)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 87	android/bluetooth/le/ScanResult:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/bluetooth/IBluetoothGattCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 49 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 52	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramScanResult	ScanResult
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onSearchComplete(String paramString, List<BluetoothGattService> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattCallback");
          localParcel.writeString(paramString);
          localParcel.writeTypedList(paramList);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothGattCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */