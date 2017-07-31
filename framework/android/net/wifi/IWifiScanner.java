package android.net.wifi;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWifiScanner
  extends IInterface
{
  public abstract Bundle getAvailableChannels(int paramInt)
    throws RemoteException;
  
  public abstract Messenger getMessenger()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWifiScanner
  {
    private static final String DESCRIPTOR = "android.net.wifi.IWifiScanner";
    static final int TRANSACTION_getAvailableChannels = 2;
    static final int TRANSACTION_getMessenger = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.IWifiScanner");
    }
    
    public static IWifiScanner asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.IWifiScanner");
      if ((localIInterface != null) && ((localIInterface instanceof IWifiScanner))) {
        return (IWifiScanner)localIInterface;
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
        paramParcel2.writeString("android.net.wifi.IWifiScanner");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiScanner");
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
      paramParcel1.enforceInterface("android.net.wifi.IWifiScanner");
      paramParcel1 = getAvailableChannels(paramParcel1.readInt());
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
      implements IWifiScanner
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
      
      /* Error */
      public Bundle getAvailableChannels(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/wifi/IWifiScanner$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_2
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 48 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 61	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 67 2 0
        //   58: checkcast 57	android/os/Bundle
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 70	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 70	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 70	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 70	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localBundle	Bundle
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.wifi.IWifiScanner";
      }
      
      /* Error */
      public Messenger getMessenger()
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
        //   15: getfield 19	android/net/wifi/IWifiScanner$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_1
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 48 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 51	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 55	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 78	android/os/Messenger:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 67 2 0
        //   48: checkcast 77	android/os/Messenger
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 70	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 70	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 70	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 70	android/os/Parcel:recycle	()V
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/IWifiScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */