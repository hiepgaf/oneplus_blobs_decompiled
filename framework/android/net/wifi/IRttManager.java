package android.net.wifi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRttManager
  extends IInterface
{
  public abstract Messenger getMessenger()
    throws RemoteException;
  
  public abstract RttManager.RttCapabilities getRttCapabilities()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRttManager
  {
    private static final String DESCRIPTOR = "android.net.wifi.IRttManager";
    static final int TRANSACTION_getMessenger = 1;
    static final int TRANSACTION_getRttCapabilities = 2;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.IRttManager");
    }
    
    public static IRttManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.IRttManager");
      if ((localIInterface != null) && ((localIInterface instanceof IRttManager))) {
        return (IRttManager)localIInterface;
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
        paramParcel2.writeString("android.net.wifi.IRttManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.IRttManager");
        paramParcel1 = getMessenger();
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
      paramParcel1.enforceInterface("android.net.wifi.IRttManager");
      paramParcel1 = getRttCapabilities();
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
      implements IRttManager
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
        return "android.net.wifi.IRttManager";
      }
      
      /* Error */
      public Messenger getMessenger()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IRttManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_1
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 46 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 49	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 53	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 59	android/os/Messenger:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 65 2 0
        //   48: checkcast 55	android/os/Messenger
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 68	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localMessenger	Messenger
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public RttManager.RttCapabilities getRttCapabilities()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IRttManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 46 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 49	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 53	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 74	android/net/wifi/RttManager$RttCapabilities:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 65 2 0
        //   48: checkcast 73	android/net/wifi/RttManager$RttCapabilities
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 68	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localRttCapabilities	RttManager.RttCapabilities
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/IRttManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */