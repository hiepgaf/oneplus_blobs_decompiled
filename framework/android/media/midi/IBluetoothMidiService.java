package android.media.midi;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBluetoothMidiService
  extends IInterface
{
  public abstract IBinder addBluetoothDevice(BluetoothDevice paramBluetoothDevice)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothMidiService
  {
    private static final String DESCRIPTOR = "android.media.midi.IBluetoothMidiService";
    static final int TRANSACTION_addBluetoothDevice = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.midi.IBluetoothMidiService");
    }
    
    public static IBluetoothMidiService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.midi.IBluetoothMidiService");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothMidiService))) {
        return (IBluetoothMidiService)localIInterface;
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
        paramParcel2.writeString("android.media.midi.IBluetoothMidiService");
        return true;
      }
      paramParcel1.enforceInterface("android.media.midi.IBluetoothMidiService");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramParcel1 = addBluetoothDevice(paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothMidiService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public IBinder addBluetoothDevice(BluetoothDevice paramBluetoothDevice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +47 -> 62
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/media/midi/IBluetoothMidiService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 52 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 55	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 59	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 62	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 62	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aload_2
        //   63: iconst_0
        //   64: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   67: goto -38 -> 29
        //   70: astore_1
        //   71: aload_3
        //   72: invokevirtual 62	android/os/Parcel:recycle	()V
        //   75: aload_2
        //   76: invokevirtual 62	android/os/Parcel:recycle	()V
        //   79: aload_1
        //   80: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	81	0	this	Proxy
        //   0	81	1	paramBluetoothDevice	BluetoothDevice
        //   3	73	2	localParcel1	Parcel
        //   7	65	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	70	finally
        //   18	29	70	finally
        //   29	52	70	finally
        //   62	67	70	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.midi.IBluetoothMidiService";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/IBluetoothMidiService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */