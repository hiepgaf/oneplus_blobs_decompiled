package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBluetoothGattServerCallback
  extends IInterface
{
  public abstract void onCharacteristicReadRequest(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid1, int paramInt5, ParcelUuid paramParcelUuid2)
    throws RemoteException;
  
  public abstract void onCharacteristicWriteRequest(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, ParcelUuid paramParcelUuid1, int paramInt6, ParcelUuid paramParcelUuid2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onDescriptorReadRequest(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid1, int paramInt5, ParcelUuid paramParcelUuid2, ParcelUuid paramParcelUuid3)
    throws RemoteException;
  
  public abstract void onDescriptorWriteRequest(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, ParcelUuid paramParcelUuid1, int paramInt6, ParcelUuid paramParcelUuid2, ParcelUuid paramParcelUuid3, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onExecuteWrite(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onMtuChanged(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onNotificationSent(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onScanResult(String paramString, int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onServerConnectionState(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
    throws RemoteException;
  
  public abstract void onServerRegistered(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onServiceAdded(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothGattServerCallback
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGattServerCallback";
    static final int TRANSACTION_onCharacteristicReadRequest = 5;
    static final int TRANSACTION_onCharacteristicWriteRequest = 7;
    static final int TRANSACTION_onDescriptorReadRequest = 6;
    static final int TRANSACTION_onDescriptorWriteRequest = 8;
    static final int TRANSACTION_onExecuteWrite = 9;
    static final int TRANSACTION_onMtuChanged = 11;
    static final int TRANSACTION_onNotificationSent = 10;
    static final int TRANSACTION_onScanResult = 2;
    static final int TRANSACTION_onServerConnectionState = 3;
    static final int TRANSACTION_onServerRegistered = 1;
    static final int TRANSACTION_onServiceAdded = 4;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothGattServerCallback");
    }
    
    public static IBluetoothGattServerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothGattServerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothGattServerCallback))) {
        return (IBluetoothGattServerCallback)localIInterface;
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
      boolean bool1;
      int i;
      Object localObject1;
      int j;
      label343:
      int k;
      label396:
      label401:
      Object localObject2;
      label471:
      label498:
      label547:
      label552:
      label558:
      boolean bool2;
      label612:
      label644:
      int m;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothGattServerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        onServerRegistered(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        onScanResult(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.createByteArray());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          onServerConnectionState(paramInt1, paramInt2, bool1, paramParcel1.readString());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onServiceAdded(paramInt1, paramInt2, i, paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          i = paramParcel1.readInt();
          j = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label396;
          }
          paramParcel2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          k = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label401;
          }
        }
        for (paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onCharacteristicReadRequest((String)localObject1, paramInt1, paramInt2, bool1, i, j, paramParcel2, k, paramParcel1);
          return true;
          bool1 = false;
          break;
          paramParcel2 = null;
          break label343;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        localObject2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          i = paramParcel1.readInt();
          j = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label547;
          }
          paramParcel2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          k = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label552;
          }
          localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label558;
          }
        }
        for (paramParcel1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onDescriptorReadRequest((String)localObject2, paramInt1, paramInt2, bool1, i, j, paramParcel2, k, (ParcelUuid)localObject1, paramParcel1);
          return true;
          bool1 = false;
          break;
          paramParcel2 = null;
          break label471;
          localObject1 = null;
          break label498;
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        localObject2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label707;
          }
          bool2 = true;
          j = paramParcel1.readInt();
          k = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label713;
          }
          paramParcel2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          m = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label718;
          }
        }
        for (localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          onCharacteristicWriteRequest((String)localObject2, paramInt1, paramInt2, i, bool1, bool2, j, k, paramParcel2, m, (ParcelUuid)localObject1, paramParcel1.createByteArray());
          return true;
          bool1 = false;
          break;
          bool2 = false;
          break label612;
          paramParcel2 = null;
          break label644;
        }
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        String str = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label891;
          }
          bool2 = true;
          j = paramParcel1.readInt();
          k = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label897;
          }
          paramParcel2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          m = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label902;
          }
          localObject1 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label908;
          }
        }
        for (localObject2 = (ParcelUuid)ParcelUuid.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          onDescriptorWriteRequest(str, paramInt1, paramInt2, i, bool1, bool2, j, k, paramParcel2, m, (ParcelUuid)localObject1, (ParcelUuid)localObject2, paramParcel1.createByteArray());
          return true;
          bool1 = false;
          break;
          bool2 = false;
          break label773;
          paramParcel2 = null;
          break label805;
          localObject1 = null;
          break label832;
        }
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        paramParcel2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          onExecuteWrite(paramParcel2, paramInt1, bool1);
          return true;
        }
      case 10: 
        label707:
        label713:
        label718:
        label773:
        label805:
        label832:
        label891:
        label897:
        label902:
        label908:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
        onNotificationSent(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothGattServerCallback");
      onMtuChanged(paramParcel1.readString(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IBluetoothGattServerCallback
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
        return "android.bluetooth.IBluetoothGattServerCallback";
      }
      
      public void onCharacteristicReadRequest(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid1, int paramInt5, ParcelUuid paramParcelUuid2)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        label149:
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
            localParcel.writeString(paramString);
            localParcel.writeInt(paramInt1);
            localParcel.writeInt(paramInt2);
            if (paramBoolean)
            {
              paramInt1 = i;
              localParcel.writeInt(paramInt1);
              localParcel.writeInt(paramInt3);
              localParcel.writeInt(paramInt4);
              if (paramParcelUuid1 != null)
              {
                localParcel.writeInt(1);
                paramParcelUuid1.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt5);
                if (paramParcelUuid2 == null) {
                  break label149;
                }
                localParcel.writeInt(1);
                paramParcelUuid2.writeToParcel(localParcel, 0);
                this.mRemote.transact(5, localParcel, null, 1);
              }
            }
            else
            {
              paramInt1 = 0;
              continue;
            }
            localParcel.writeInt(0);
            continue;
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void onCharacteristicWriteRequest(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, ParcelUuid paramParcelUuid1, int paramInt6, ParcelUuid paramParcelUuid2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        label178:
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
            localParcel.writeString(paramString);
            localParcel.writeInt(paramInt1);
            localParcel.writeInt(paramInt2);
            localParcel.writeInt(paramInt3);
            if (paramBoolean1)
            {
              paramInt1 = 1;
              localParcel.writeInt(paramInt1);
              if (paramBoolean2)
              {
                paramInt1 = 1;
                localParcel.writeInt(paramInt1);
                localParcel.writeInt(paramInt4);
                localParcel.writeInt(paramInt5);
                if (paramParcelUuid1 == null) {
                  continue;
                }
                localParcel.writeInt(1);
                paramParcelUuid1.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt6);
                if (paramParcelUuid2 == null) {
                  break label178;
                }
                localParcel.writeInt(1);
                paramParcelUuid2.writeToParcel(localParcel, 0);
                localParcel.writeByteArray(paramArrayOfByte);
                this.mRemote.transact(7, localParcel, null, 1);
              }
            }
            else
            {
              paramInt1 = 0;
              continue;
            }
            paramInt1 = 0;
            continue;
            localParcel.writeInt(0);
            continue;
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void onDescriptorReadRequest(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, ParcelUuid paramParcelUuid1, int paramInt5, ParcelUuid paramParcelUuid2, ParcelUuid paramParcelUuid3)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
            localParcel.writeString(paramString);
            localParcel.writeInt(paramInt1);
            localParcel.writeInt(paramInt2);
            if (paramBoolean)
            {
              paramInt1 = i;
              localParcel.writeInt(paramInt1);
              localParcel.writeInt(paramInt3);
              localParcel.writeInt(paramInt4);
              if (paramParcelUuid1 != null)
              {
                localParcel.writeInt(1);
                paramParcelUuid1.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt5);
                if (paramParcelUuid2 == null) {
                  break label169;
                }
                localParcel.writeInt(1);
                paramParcelUuid2.writeToParcel(localParcel, 0);
                if (paramParcelUuid3 == null) {
                  break label178;
                }
                localParcel.writeInt(1);
                paramParcelUuid3.writeToParcel(localParcel, 0);
                this.mRemote.transact(6, localParcel, null, 1);
              }
            }
            else
            {
              paramInt1 = 0;
              continue;
            }
            localParcel.writeInt(0);
            continue;
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          label169:
          continue;
          label178:
          localParcel.writeInt(0);
        }
      }
      
      public void onDescriptorWriteRequest(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, ParcelUuid paramParcelUuid1, int paramInt6, ParcelUuid paramParcelUuid2, ParcelUuid paramParcelUuid3, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
            localParcel.writeString(paramString);
            localParcel.writeInt(paramInt1);
            localParcel.writeInt(paramInt2);
            localParcel.writeInt(paramInt3);
            if (paramBoolean1)
            {
              paramInt1 = 1;
              localParcel.writeInt(paramInt1);
              if (paramBoolean2)
              {
                paramInt1 = 1;
                localParcel.writeInt(paramInt1);
                localParcel.writeInt(paramInt4);
                localParcel.writeInt(paramInt5);
                if (paramParcelUuid1 == null) {
                  continue;
                }
                localParcel.writeInt(1);
                paramParcelUuid1.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt6);
                if (paramParcelUuid2 == null) {
                  break label197;
                }
                localParcel.writeInt(1);
                paramParcelUuid2.writeToParcel(localParcel, 0);
                if (paramParcelUuid3 == null) {
                  break label206;
                }
                localParcel.writeInt(1);
                paramParcelUuid3.writeToParcel(localParcel, 0);
                localParcel.writeByteArray(paramArrayOfByte);
                this.mRemote.transact(8, localParcel, null, 1);
              }
            }
            else
            {
              paramInt1 = 0;
              continue;
            }
            paramInt1 = 0;
            continue;
            localParcel.writeInt(0);
            continue;
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          label197:
          continue;
          label206:
          localParcel.writeInt(0);
        }
      }
      
      /* Error */
      public void onExecuteWrite(String paramString, int paramInt, boolean paramBoolean)
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
        //   17: aload_1
        //   18: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload 5
        //   23: iload_2
        //   24: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   27: iload_3
        //   28: ifeq +34 -> 62
        //   31: iload 4
        //   33: istore_2
        //   34: aload 5
        //   36: iload_2
        //   37: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   40: aload_0
        //   41: getfield 19	android/bluetooth/IBluetoothGattServerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   44: bipush 9
        //   46: aload 5
        //   48: aconst_null
        //   49: iconst_1
        //   50: invokeinterface 59 5 0
        //   55: pop
        //   56: aload 5
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: return
        //   62: iconst_0
        //   63: istore_2
        //   64: goto -30 -> 34
        //   67: astore_1
        //   68: aload 5
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramString	String
        //   0	75	2	paramInt	int
        //   0	75	3	paramBoolean	boolean
        //   1	31	4	i	int
        //   6	63	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	27	67	finally
        //   34	56	67	finally
      }
      
      public void onMtuChanged(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onNotificationSent(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onScanResult(String paramString, int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onServerConnectionState(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
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
        //   18: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   21: aload 6
        //   23: iload_2
        //   24: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   27: iload_3
        //   28: ifeq +40 -> 68
        //   31: iload 5
        //   33: istore_1
        //   34: aload 6
        //   36: iload_1
        //   37: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   40: aload 6
        //   42: aload 4
        //   44: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   47: aload_0
        //   48: getfield 19	android/bluetooth/IBluetoothGattServerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: iconst_3
        //   52: aload 6
        //   54: aconst_null
        //   55: iconst_1
        //   56: invokeinterface 59 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: return
        //   68: iconst_0
        //   69: istore_1
        //   70: goto -36 -> 34
        //   73: astore 4
        //   75: aload 6
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
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
      
      public void onServerRegistered(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothGattServerCallback");
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
      
      /* Error */
      public void onServiceAdded(int paramInt1, int paramInt2, int paramInt3, ParcelUuid paramParcelUuid)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 5
        //   14: iload_1
        //   15: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   18: aload 5
        //   20: iload_2
        //   21: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   24: aload 5
        //   26: iload_3
        //   27: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   30: aload 4
        //   32: ifnull +38 -> 70
        //   35: aload 5
        //   37: iconst_1
        //   38: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokevirtual 53	android/os/ParcelUuid:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: aload_0
        //   50: getfield 19	android/bluetooth/IBluetoothGattServerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   53: iconst_4
        //   54: aload 5
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 59 5 0
        //   63: pop
        //   64: aload 5
        //   66: invokevirtual 62	android/os/Parcel:recycle	()V
        //   69: return
        //   70: aload 5
        //   72: iconst_0
        //   73: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   76: goto -27 -> 49
        //   79: astore 4
        //   81: aload 5
        //   83: invokevirtual 62	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramInt1	int
        //   0	89	2	paramInt2	int
        //   0	89	3	paramInt3	int
        //   0	89	4	paramParcelUuid	ParcelUuid
        //   3	79	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	30	79	finally
        //   35	49	79	finally
        //   49	64	79	finally
        //   70	76	79	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothGattServerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */