package android.net.wifi.nan;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IWifiNanEventListener
  extends IInterface
{
  public abstract void onConfigCompleted(ConfigRequest paramConfigRequest)
    throws RemoteException;
  
  public abstract void onConfigFailed(ConfigRequest paramConfigRequest, int paramInt)
    throws RemoteException;
  
  public abstract void onIdentityChanged()
    throws RemoteException;
  
  public abstract void onNanDown(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWifiNanEventListener
  {
    private static final String DESCRIPTOR = "android.net.wifi.nan.IWifiNanEventListener";
    static final int TRANSACTION_onConfigCompleted = 1;
    static final int TRANSACTION_onConfigFailed = 2;
    static final int TRANSACTION_onIdentityChanged = 4;
    static final int TRANSACTION_onNanDown = 3;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.nan.IWifiNanEventListener");
    }
    
    public static IWifiNanEventListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.nan.IWifiNanEventListener");
      if ((localIInterface != null) && ((localIInterface instanceof IWifiNanEventListener))) {
        return (IWifiNanEventListener)localIInterface;
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
        paramParcel2.writeString("android.net.wifi.nan.IWifiNanEventListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanEventListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ConfigRequest)ConfigRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onConfigCompleted(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanEventListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ConfigRequest)ConfigRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onConfigFailed(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanEventListener");
        onNanDown(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanEventListener");
      onIdentityChanged();
      return true;
    }
    
    private static class Proxy
      implements IWifiNanEventListener
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
        return "android.net.wifi.nan.IWifiNanEventListener";
      }
      
      /* Error */
      public void onConfigCompleted(ConfigRequest paramConfigRequest)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/wifi/nan/ConfigRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/net/wifi/nan/IWifiNanEventListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramConfigRequest	ConfigRequest
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onConfigFailed(ConfigRequest paramConfigRequest, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/wifi/nan/ConfigRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/net/wifi/nan/IWifiNanEventListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramConfigRequest	ConfigRequest
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void onIdentityChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanEventListener");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onNanDown(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanEventListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/IWifiNanEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */