package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IBluetoothMap
  extends IInterface
{
  public abstract boolean connect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract BluetoothDevice getClient()
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getConnectedDevices()
    throws RemoteException;
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract int getPriority(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getState()
    throws RemoteException;
  
  public abstract boolean isConnected(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothMap
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothMap";
    static final int TRANSACTION_connect = 3;
    static final int TRANSACTION_disconnect = 4;
    static final int TRANSACTION_getClient = 2;
    static final int TRANSACTION_getConnectedDevices = 6;
    static final int TRANSACTION_getConnectionState = 8;
    static final int TRANSACTION_getDevicesMatchingConnectionStates = 7;
    static final int TRANSACTION_getPriority = 10;
    static final int TRANSACTION_getState = 1;
    static final int TRANSACTION_isConnected = 5;
    static final int TRANSACTION_setPriority = 9;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothMap");
    }
    
    public static IBluetoothMap asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothMap");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothMap))) {
        return (IBluetoothMap)localIInterface;
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
      int j = 0;
      int k = 0;
      int m = 0;
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothMap");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        paramInt1 = getState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        paramParcel1 = getClient();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = connect(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = i;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = disconnect(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = j;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = isConnected(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = k;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        paramParcel1 = getConnectedDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        paramParcel1 = getDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getConnectionState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
        if (paramParcel1.readInt() != 0) {}
        for (BluetoothDevice localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localBluetoothDevice = null)
        {
          bool = setPriority(localBluetoothDevice, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramInt1 = m;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothMap");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = getPriority(paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothMap
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
      
      public boolean connect(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public boolean disconnect(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      public BluetoothDevice getClient()
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
        //   15: getfield 19	android/bluetooth/IBluetoothMap$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 54 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 57	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 61	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 72	android/bluetooth/BluetoothDevice:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 78 2 0
        //   48: checkcast 44	android/bluetooth/BluetoothDevice
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 64	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 64	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localBluetoothDevice	BluetoothDevice
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public List<BluetoothDevice> getConnectedDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(BluetoothDevice.CREATOR);
          return localArrayList;
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
        //   31: getfield 19	android/bluetooth/IBluetoothMap$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 8
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
      
      public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
        return "android.bluetooth.IBluetoothMap";
      }
      
      /* Error */
      public int getPriority(BluetoothDevice paramBluetoothDevice)
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
        //   31: getfield 19	android/bluetooth/IBluetoothMap$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 10
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
      
      public int getState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public boolean isConnected(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      
      public boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothMap");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */