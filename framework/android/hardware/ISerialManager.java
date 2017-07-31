package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public abstract interface ISerialManager
  extends IInterface
{
  public abstract String[] getSerialPorts()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openSerialPort(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISerialManager
  {
    private static final String DESCRIPTOR = "android.hardware.ISerialManager";
    static final int TRANSACTION_getSerialPorts = 1;
    static final int TRANSACTION_openSerialPort = 2;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.ISerialManager");
    }
    
    public static ISerialManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.ISerialManager");
      if ((localIInterface != null) && ((localIInterface instanceof ISerialManager))) {
        return (ISerialManager)localIInterface;
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
        paramParcel2.writeString("android.hardware.ISerialManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.ISerialManager");
        paramParcel1 = getSerialPorts();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.ISerialManager");
      paramParcel1 = openSerialPort(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements ISerialManager
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
        return "android.hardware.ISerialManager";
      }
      
      public String[] getSerialPorts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ISerialManager");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParcelFileDescriptor openSerialPort(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 61	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/hardware/ISerialManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_2
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 46 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 49	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 71	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 77 2 0
        //   53: checkcast 67	android/os/ParcelFileDescriptor
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 55	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramString	String
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ISerialManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */