package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IBluetoothInputDevice
  extends IInterface
{
  public abstract boolean connect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getConnectedDevices()
    throws RemoteException;
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract boolean getIdleTime(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract int getPriority(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean getProtocolMode(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract boolean getReport(BluetoothDevice paramBluetoothDevice, byte paramByte1, byte paramByte2, int paramInt)
    throws RemoteException;
  
  public abstract boolean sendData(BluetoothDevice paramBluetoothDevice, String paramString)
    throws RemoteException;
  
  public abstract boolean setIdleTime(BluetoothDevice paramBluetoothDevice, byte paramByte)
    throws RemoteException;
  
  public abstract boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean setProtocolMode(BluetoothDevice paramBluetoothDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean setReport(BluetoothDevice paramBluetoothDevice, byte paramByte, String paramString)
    throws RemoteException;
  
  public abstract boolean virtualUnplug(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothInputDevice
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothInputDevice";
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_disconnect = 2;
    static final int TRANSACTION_getConnectedDevices = 3;
    static final int TRANSACTION_getConnectionState = 5;
    static final int TRANSACTION_getDevicesMatchingConnectionStates = 4;
    static final int TRANSACTION_getIdleTime = 14;
    static final int TRANSACTION_getPriority = 7;
    static final int TRANSACTION_getProtocolMode = 8;
    static final int TRANSACTION_getReport = 11;
    static final int TRANSACTION_sendData = 13;
    static final int TRANSACTION_setIdleTime = 15;
    static final int TRANSACTION_setPriority = 6;
    static final int TRANSACTION_setProtocolMode = 10;
    static final int TRANSACTION_setReport = 12;
    static final int TRANSACTION_virtualUnplug = 9;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothInputDevice");
    }
    
    public static IBluetoothInputDevice asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothInputDevice");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothInputDevice))) {
        return (IBluetoothInputDevice)localIInterface;
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
      label214:
      label275:
      BluetoothDevice localBluetoothDevice;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothInputDevice");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = connect(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label214;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = disconnect(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label275;
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
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        paramParcel1 = getConnectedDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        paramParcel1 = getDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getConnectionState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setPriority(localBluetoothDevice, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label439;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBluetoothDevice = null;
          break;
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getPriority(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = getProtocolMode(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label548;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = virtualUnplug(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label609;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setProtocolMode(localBluetoothDevice, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label677;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBluetoothDevice = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = getReport(localBluetoothDevice, paramParcel1.readByte(), paramParcel1.readByte(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label753;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBluetoothDevice = null;
          break;
        }
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = setReport(localBluetoothDevice, paramParcel1.readByte(), paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label825;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBluetoothDevice = null;
          break;
        }
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = sendData(localBluetoothDevice, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label893;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBluetoothDevice = null;
          break;
        }
      case 14: 
        label439:
        label548:
        label609:
        label677:
        label753:
        label825:
        label893:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          bool = getIdleTime(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label954;
          }
        }
        label954:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothInputDevice");
      if (paramParcel1.readInt() != 0)
      {
        localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
        bool = setIdleTime(localBluetoothDevice, paramParcel1.readByte());
        paramParcel2.writeNoException();
        if (!bool) {
          break label1022;
        }
      }
      label1022:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        localBluetoothDevice = null;
        break;
      }
    }
    
    private static class Proxy
      implements IBluetoothInputDevice
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
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
      
      public boolean disconnect(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
      
      public List<BluetoothDevice> getConnectedDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 48	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetoothInputDevice$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 54 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 57	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 61	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: aload 4
        //   58: invokevirtual 64	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 64	android/os/Parcel:recycle	()V
        //   65: iload_2
        //   66: ireturn
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 64	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
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
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
      
      public boolean getIdleTime(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetoothInputDevice";
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
        //   31: getfield 19	android/bluetooth/IBluetoothInputDevice$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 7
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
      
      public boolean getProtocolMode(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      public boolean getReport(BluetoothDevice paramBluetoothDevice, byte paramByte1, byte paramByte2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeByte(paramByte1);
              localParcel1.writeByte(paramByte2);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public boolean sendData(BluetoothDevice paramBluetoothDevice, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      
      public boolean setIdleTime(BluetoothDevice paramBluetoothDevice, byte paramByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeByte(paramByte);
              this.mRemote.transact(15, localParcel1, localParcel2, 0);
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
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
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
      
      public boolean setProtocolMode(BluetoothDevice paramBluetoothDevice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public boolean setReport(BluetoothDevice paramBluetoothDevice, byte paramByte, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeByte(paramByte);
              localParcel1.writeString(paramString);
              this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public boolean virtualUnplug(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothInputDevice");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(9, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothInputDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */