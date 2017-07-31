package android.bluetooth;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IBluetoothAvrcpController
  extends IInterface
{
  public abstract List<BluetoothDevice> getConnectedDevices()
    throws RemoteException;
  
  public abstract int getConnectionState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract MediaMetadata getMetadata(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract PlaybackState getPlaybackState(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract BluetoothAvrcpPlayerSettings getPlayerSettings(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public abstract void sendGroupNavigationCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void sendPassThroughCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean setPlayerApplicationSetting(BluetoothAvrcpPlayerSettings paramBluetoothAvrcpPlayerSettings)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothAvrcpController
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothAvrcpController";
    static final int TRANSACTION_getConnectedDevices = 1;
    static final int TRANSACTION_getConnectionState = 3;
    static final int TRANSACTION_getDevicesMatchingConnectionStates = 2;
    static final int TRANSACTION_getMetadata = 6;
    static final int TRANSACTION_getPlaybackState = 7;
    static final int TRANSACTION_getPlayerSettings = 5;
    static final int TRANSACTION_sendGroupNavigationCmd = 9;
    static final int TRANSACTION_sendPassThroughCmd = 4;
    static final int TRANSACTION_setPlayerApplicationSetting = 8;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothAvrcpController");
    }
    
    public static IBluetoothAvrcpController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothAvrcpController");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothAvrcpController))) {
        return (IBluetoothAvrcpController)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothAvrcpController");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        paramParcel1 = getConnectedDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        paramParcel1 = getDevicesMatchingConnectionStates(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getConnectionState(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0) {}
        for (localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localBluetoothDevice = null)
        {
          sendPassThroughCmd(localBluetoothDevice, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getPlayerSettings(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label317;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getMetadata(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label383;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getPlaybackState(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label449;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 8: 
        label317:
        label383:
        label449:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (BluetoothAvrcpPlayerSettings)BluetoothAvrcpPlayerSettings.CREATOR.createFromParcel(paramParcel1);
          boolean bool = setPlayerApplicationSetting(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label513;
          }
        }
        label513:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothAvrcpController");
      if (paramParcel1.readInt() != 0) {}
      for (BluetoothDevice localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localBluetoothDevice = null)
      {
        sendGroupNavigationCmd(localBluetoothDevice, paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothAvrcpController
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
      
      public List<BluetoothDevice> getConnectedDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
        //   21: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 73	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/bluetooth/IBluetoothAvrcpController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 44 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 47	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 77	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: aload 4
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 60	android/os/Parcel:recycle	()V
        //   65: iload_2
        //   66: ireturn
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 60	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 60	android/os/Parcel:recycle	()V
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
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
        return "android.bluetooth.IBluetoothAvrcpController";
      }
      
      public MediaMetadata getMetadata(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramBluetoothDevice = (MediaMetadata)MediaMetadata.CREATOR.createFromParcel(localParcel2);
                return paramBluetoothDevice;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramBluetoothDevice = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public PlaybackState getPlaybackState(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramBluetoothDevice = (PlaybackState)PlaybackState.CREATOR.createFromParcel(localParcel2);
                return paramBluetoothDevice;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramBluetoothDevice = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public BluetoothAvrcpPlayerSettings getPlayerSettings(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
            if (paramBluetoothDevice != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramBluetoothDevice = (BluetoothAvrcpPlayerSettings)BluetoothAvrcpPlayerSettings.CREATOR.createFromParcel(localParcel2);
                return paramBluetoothDevice;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramBluetoothDevice = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void sendGroupNavigationCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
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
        //   17: aload_1
        //   18: ifnull +61 -> 79
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 73	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: iload_2
        //   37: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/bluetooth/IBluetoothAvrcpController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 9
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 44 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 47	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 60	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 60	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 60	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 60	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramBluetoothDevice	BluetoothDevice
        //   0	101	2	paramInt1	int
        //   0	101	3	paramInt2	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	88	finally
        //   21	34	88	finally
        //   34	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void sendPassThroughCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
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
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 73	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: iload_2
        //   37: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/bluetooth/IBluetoothAvrcpController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_4
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 44 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 47	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 60	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 60	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 60	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 60	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramBluetoothDevice	BluetoothDevice
        //   0	100	2	paramInt1	int
        //   0	100	3	paramInt2	int
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public boolean setPlayerApplicationSetting(BluetoothAvrcpPlayerSettings paramBluetoothAvrcpPlayerSettings)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothAvrcpController");
            if (paramBluetoothAvrcpPlayerSettings != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothAvrcpPlayerSettings.writeToParcel(localParcel1, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothAvrcpController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */