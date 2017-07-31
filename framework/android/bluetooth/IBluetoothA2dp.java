package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IBluetoothA2dp
  extends IInterface
{
  public abstract void adjustAvrcpAbsoluteVolume(int paramInt)
    throws RemoteException;
  
  public abstract boolean connect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract void disableAptXHD()
    throws RemoteException;
  
  public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract void enableAptXHD()
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getConnectedDevices()
    throws RemoteException;
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract int getPriority(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean isA2dpPlaying(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean isAptXHDAvailable(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean isAvrcpAbsoluteVolumeSupported()
    throws RemoteException;
  
  public abstract void setAvrcpAbsoluteVolume(int paramInt)
    throws RemoteException;
  
  public abstract boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothA2dp
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothA2dp";
    static final int TRANSACTION_adjustAvrcpAbsoluteVolume = 9;
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_disableAptXHD = 13;
    static final int TRANSACTION_disconnect = 2;
    static final int TRANSACTION_enableAptXHD = 12;
    static final int TRANSACTION_getConnectedDevices = 3;
    static final int TRANSACTION_getConnectionState = 5;
    static final int TRANSACTION_getDevicesMatchingConnectionStates = 4;
    static final int TRANSACTION_getPriority = 7;
    static final int TRANSACTION_isA2dpPlaying = 11;
    static final int TRANSACTION_isAptXHDAvailable = 14;
    static final int TRANSACTION_isAvrcpAbsoluteVolumeSupported = 8;
    static final int TRANSACTION_setAvrcpAbsoluteVolume = 10;
    static final int TRANSACTION_setPriority = 6;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothA2dp");
    }
    
    public static IBluetoothA2dp asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothA2dp");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothA2dp))) {
        return (IBluetoothA2dp)localIInterface;
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
      int n = 0;
      int i1 = 0;
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothA2dp");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
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
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
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
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        paramParcel1 = getConnectedDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        paramParcel1 = getDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getConnectionState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        if (paramParcel1.readInt() != 0) {}
        for (BluetoothDevice localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localBluetoothDevice = null)
        {
          bool = setPriority(localBluetoothDevice, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramInt1 = k;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getPriority(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        bool = isAvrcpAbsoluteVolumeSupported();
        paramParcel2.writeNoException();
        paramInt1 = m;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        adjustAvrcpAbsoluteVolume(paramParcel1.readInt());
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        setAvrcpAbsoluteVolume(paramParcel1.readInt());
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = isA2dpPlaying(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = n;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        enableAptXHD();
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
        disableAptXHD();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothA2dp");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        bool = isAptXHDAvailable(paramParcel1);
        paramParcel2.writeNoException();
        paramInt1 = i1;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothA2dp
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void adjustAvrcpAbsoluteVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
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
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public void disableAptXHD()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
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
      
      public boolean disconnect(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      
      public void enableAptXHD()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
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
      
      public List<BluetoothDevice> getConnectedDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 59	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetoothA2dp$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 45 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 62	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 66	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: aload 4
        //   58: invokevirtual 48	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 48	android/os/Parcel:recycle	()V
        //   65: iload_2
        //   66: ireturn
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 48	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 48	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramBluetoothDevice	BluetoothDevice
        //   55	11	2	i	int
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	56	75	finally
        //   67	72	75	finally
      }
      
      public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
        return "android.bluetooth.IBluetoothA2dp";
      }
      
      /* Error */
      public int getPriority(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 59	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetoothA2dp$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 7
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 45 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 62	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 66	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 48	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 48	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 48	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 48	android/os/Parcel:recycle	()V
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
      
      public boolean isA2dpPlaying(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public boolean isAptXHDAvailable(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      public boolean isAvrcpAbsoluteVolumeSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetoothA2dp$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 45 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 62	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 66	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 48	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 48	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 48	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 48	android/os/Parcel:recycle	()V
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
      
      public void setAvrcpAbsoluteVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
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
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothA2dp");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothA2dp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */