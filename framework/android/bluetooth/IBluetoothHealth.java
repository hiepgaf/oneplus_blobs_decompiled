package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IBluetoothHealth
  extends IInterface
{
  public abstract boolean connectChannelToSink(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
    throws RemoteException;
  
  public abstract boolean connectChannelToSource(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
    throws RemoteException;
  
  public abstract boolean disconnectChannel(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getConnectedHealthDevices()
    throws RemoteException;
  
  public abstract int getHealthDeviceConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getHealthDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor getMainChannelFd(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
    throws RemoteException;
  
  public abstract boolean registerAppConfiguration(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, IBluetoothHealthCallback paramIBluetoothHealthCallback)
    throws RemoteException;
  
  public abstract boolean unregisterAppConfiguration(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothHealth
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothHealth";
    static final int TRANSACTION_connectChannelToSink = 4;
    static final int TRANSACTION_connectChannelToSource = 3;
    static final int TRANSACTION_disconnectChannel = 5;
    static final int TRANSACTION_getConnectedHealthDevices = 7;
    static final int TRANSACTION_getHealthDeviceConnectionState = 9;
    static final int TRANSACTION_getHealthDevicesMatchingConnectionStates = 8;
    static final int TRANSACTION_getMainChannelFd = 6;
    static final int TRANSACTION_registerAppConfiguration = 1;
    static final int TRANSACTION_unregisterAppConfiguration = 2;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothHealth");
    }
    
    public static IBluetoothHealth asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothHealth");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothHealth))) {
        return (IBluetoothHealth)localIInterface;
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
      Object localObject;
      boolean bool;
      label176:
      label237:
      label289:
      label322:
      label327:
      BluetoothHealthAppConfiguration localBluetoothHealthAppConfiguration;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothHealth");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          bool = registerAppConfiguration((BluetoothHealthAppConfiguration)localObject, IBluetoothHealthCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          if (!bool) {
            break label176;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          bool = unregisterAppConfiguration(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label237;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label322;
          }
          paramParcel1 = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          bool = connectChannelToSource((BluetoothDevice)localObject, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label327;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
          paramParcel1 = null;
          break label289;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label418;
          }
          localBluetoothHealthAppConfiguration = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          bool = connectChannelToSink((BluetoothDevice)localObject, localBluetoothHealthAppConfiguration, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label424;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
          localBluetoothHealthAppConfiguration = null;
          break label380;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label515;
          }
          localBluetoothHealthAppConfiguration = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          bool = disconnectChannel((BluetoothDevice)localObject, localBluetoothHealthAppConfiguration, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label521;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
          localBluetoothHealthAppConfiguration = null;
          break label477;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label608;
          }
          paramParcel1 = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getMainChannelFd((BluetoothDevice)localObject, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label613;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          paramParcel1 = null;
          break label573;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        paramParcel1 = getConnectedHealthDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 8: 
        label380:
        label418:
        label424:
        label477:
        label515:
        label521:
        label573:
        label608:
        label613:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
        paramParcel1 = getHealthDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealth");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = getHealthDeviceConnectionState(paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothHealth
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
      
      public boolean connectChannelToSink(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              if (paramBluetoothHealthAppConfiguration != null)
              {
                localParcel1.writeInt(1);
                paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label135;
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
          label135:
          boolean bool = false;
        }
      }
      
      public boolean connectChannelToSource(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              if (paramBluetoothHealthAppConfiguration != null)
              {
                localParcel1.writeInt(1);
                paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label129;
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
          label129:
          boolean bool = false;
        }
      }
      
      public boolean disconnectChannel(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              if (paramBluetoothHealthAppConfiguration != null)
              {
                localParcel1.writeInt(1);
                paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label135;
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
          label135:
          boolean bool = false;
        }
      }
      
      public List<BluetoothDevice> getConnectedHealthDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      public int getHealthDeviceConnectionState(BluetoothDevice paramBluetoothDevice)
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
        //   31: getfield 19	android/bluetooth/IBluetoothHealth$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 9
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 57 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 60	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 64	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 67	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 67	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 67	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 67	android/os/Parcel:recycle	()V
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
      
      public List<BluetoothDevice> getHealthDevicesMatchingConnectionStates(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
        return "android.bluetooth.IBluetoothHealth";
      }
      
      public ParcelFileDescriptor getMainChannelFd(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              if (paramBluetoothHealthAppConfiguration != null)
              {
                localParcel1.writeInt(1);
                paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label127;
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
          label127:
          paramBluetoothDevice = null;
        }
      }
      
      public boolean registerAppConfiguration(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, IBluetoothHealthCallback paramIBluetoothHealthCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothHealthAppConfiguration != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
              paramBluetoothHealthAppConfiguration = (BluetoothHealthAppConfiguration)localObject;
              if (paramIBluetoothHealthCallback != null) {
                paramBluetoothHealthAppConfiguration = paramIBluetoothHealthCallback.asBinder();
              }
              localParcel1.writeStrongBinder(paramBluetoothHealthAppConfiguration);
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
      
      public boolean unregisterAppConfiguration(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealth");
            if (paramBluetoothHealthAppConfiguration != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothHealth.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */