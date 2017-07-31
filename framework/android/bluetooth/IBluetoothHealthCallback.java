package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBluetoothHealthCallback
  extends IInterface
{
  public abstract void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
    throws RemoteException;
  
  public abstract void onHealthChannelStateChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt3)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothHealthCallback
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothHealthCallback";
    static final int TRANSACTION_onHealthAppConfigurationStatusChange = 1;
    static final int TRANSACTION_onHealthChannelStateChange = 2;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothHealthCallback");
    }
    
    public static IBluetoothHealthCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothHealthCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothHealthCallback))) {
        return (IBluetoothHealthCallback)localIInterface;
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
      BluetoothHealthAppConfiguration localBluetoothHealthAppConfiguration;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothHealthCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealthCallback");
        if (paramParcel1.readInt() != 0) {}
        for (localBluetoothHealthAppConfiguration = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);; localBluetoothHealthAppConfiguration = null)
        {
          onHealthAppConfigurationStatusChange(localBluetoothHealthAppConfiguration, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothHealthCallback");
      BluetoothDevice localBluetoothDevice;
      if (paramParcel1.readInt() != 0)
      {
        localBluetoothHealthAppConfiguration = (BluetoothHealthAppConfiguration)BluetoothHealthAppConfiguration.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label212;
        }
        localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);
        label151:
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label218;
        }
      }
      label212:
      label218:
      for (ParcelFileDescriptor localParcelFileDescriptor = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; localParcelFileDescriptor = null)
      {
        onHealthChannelStateChange(localBluetoothHealthAppConfiguration, localBluetoothDevice, paramInt1, paramInt2, localParcelFileDescriptor, paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
        localBluetoothHealthAppConfiguration = null;
        break;
        localBluetoothDevice = null;
        break label151;
      }
    }
    
    private static class Proxy
      implements IBluetoothHealthCallback
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
        return "android.bluetooth.IBluetoothHealthCallback";
      }
      
      /* Error */
      public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +49 -> 65
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/bluetooth/BluetoothHealthAppConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/bluetooth/IBluetoothHealthCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 56 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 59	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 62	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 62	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   70: goto -40 -> 30
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 62	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 62	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramBluetoothHealthAppConfiguration	BluetoothHealthAppConfiguration
        //   0	85	2	paramInt	int
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	73	finally
        //   19	30	73	finally
        //   30	55	73	finally
        //   65	70	73	finally
      }
      
      public void onHealthChannelStateChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHealthCallback");
            if (paramBluetoothHealthAppConfiguration != null)
            {
              localParcel1.writeInt(1);
              paramBluetoothHealthAppConfiguration.writeToParcel(localParcel1, 0);
              if (paramBluetoothDevice != null)
              {
                localParcel1.writeInt(1);
                paramBluetoothDevice.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt1);
                localParcel1.writeInt(paramInt2);
                if (paramParcelFileDescriptor == null) {
                  break label153;
                }
                localParcel1.writeInt(1);
                paramParcelFileDescriptor.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt3);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
          label153:
          localParcel1.writeInt(0);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothHealthCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */