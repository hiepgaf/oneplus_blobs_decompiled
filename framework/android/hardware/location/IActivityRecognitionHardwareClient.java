package android.hardware.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IActivityRecognitionHardwareClient
  extends IInterface
{
  public abstract void onAvailabilityChanged(boolean paramBoolean, IActivityRecognitionHardware paramIActivityRecognitionHardware)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IActivityRecognitionHardwareClient
  {
    private static final String DESCRIPTOR = "android.hardware.location.IActivityRecognitionHardwareClient";
    static final int TRANSACTION_onAvailabilityChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IActivityRecognitionHardwareClient");
    }
    
    public static IActivityRecognitionHardwareClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IActivityRecognitionHardwareClient");
      if ((localIInterface != null) && ((localIInterface instanceof IActivityRecognitionHardwareClient))) {
        return (IActivityRecognitionHardwareClient)localIInterface;
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
        paramParcel2.writeString("android.hardware.location.IActivityRecognitionHardwareClient");
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardwareClient");
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        onAvailabilityChanged(bool, IActivityRecognitionHardware.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IActivityRecognitionHardwareClient
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
        return "android.hardware.location.IActivityRecognitionHardwareClient";
      }
      
      /* Error */
      public void onAvailabilityChanged(boolean paramBoolean, IActivityRecognitionHardware paramIActivityRecognitionHardware)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: iconst_1
        //   4: istore_3
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   13: astore 6
        //   15: aload 5
        //   17: ldc 26
        //   19: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   22: iload_1
        //   23: ifeq +60 -> 83
        //   26: aload 5
        //   28: iload_3
        //   29: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   32: aload_2
        //   33: ifnull +11 -> 44
        //   36: aload_2
        //   37: invokeinterface 48 1 0
        //   42: astore 4
        //   44: aload 5
        //   46: aload 4
        //   48: invokevirtual 51	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   51: aload_0
        //   52: getfield 19	android/hardware/location/IActivityRecognitionHardwareClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   55: iconst_1
        //   56: aload 5
        //   58: aload 6
        //   60: iconst_0
        //   61: invokeinterface 57 5 0
        //   66: pop
        //   67: aload 6
        //   69: invokevirtual 60	android/os/Parcel:readException	()V
        //   72: aload 6
        //   74: invokevirtual 63	android/os/Parcel:recycle	()V
        //   77: aload 5
        //   79: invokevirtual 63	android/os/Parcel:recycle	()V
        //   82: return
        //   83: iconst_0
        //   84: istore_3
        //   85: goto -59 -> 26
        //   88: astore_2
        //   89: aload 6
        //   91: invokevirtual 63	android/os/Parcel:recycle	()V
        //   94: aload 5
        //   96: invokevirtual 63	android/os/Parcel:recycle	()V
        //   99: aload_2
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramBoolean	boolean
        //   0	101	2	paramIActivityRecognitionHardware	IActivityRecognitionHardware
        //   4	81	3	i	int
        //   1	46	4	localIBinder	IBinder
        //   8	87	5	localParcel1	Parcel
        //   13	77	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   15	22	88	finally
        //   26	32	88	finally
        //   36	44	88	finally
        //   44	72	88	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IActivityRecognitionHardwareClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */