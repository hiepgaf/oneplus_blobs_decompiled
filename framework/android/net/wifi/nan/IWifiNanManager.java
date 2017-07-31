package android.net.wifi.nan;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IWifiNanManager
  extends IInterface
{
  public abstract void connect(IBinder paramIBinder, IWifiNanEventListener paramIWifiNanEventListener, int paramInt)
    throws RemoteException;
  
  public abstract int createSession(IWifiNanSessionListener paramIWifiNanSessionListener, int paramInt)
    throws RemoteException;
  
  public abstract void destroySession(int paramInt)
    throws RemoteException;
  
  public abstract void disconnect(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void publish(int paramInt, PublishData paramPublishData, PublishSettings paramPublishSettings)
    throws RemoteException;
  
  public abstract void requestConfig(ConfigRequest paramConfigRequest)
    throws RemoteException;
  
  public abstract void sendMessage(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void stopSession(int paramInt)
    throws RemoteException;
  
  public abstract void subscribe(int paramInt, SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWifiNanManager
  {
    private static final String DESCRIPTOR = "android.net.wifi.nan.IWifiNanManager";
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_createSession = 4;
    static final int TRANSACTION_destroySession = 9;
    static final int TRANSACTION_disconnect = 2;
    static final int TRANSACTION_publish = 5;
    static final int TRANSACTION_requestConfig = 3;
    static final int TRANSACTION_sendMessage = 7;
    static final int TRANSACTION_stopSession = 8;
    static final int TRANSACTION_subscribe = 6;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.nan.IWifiNanManager");
    }
    
    public static IWifiNanManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.nan.IWifiNanManager");
      if ((localIInterface != null) && ((localIInterface instanceof IWifiNanManager))) {
        return (IWifiNanManager)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.wifi.nan.IWifiNanManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        connect(paramParcel1.readStrongBinder(), IWifiNanEventListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        disconnect(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ConfigRequest)ConfigRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestConfig(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        paramInt1 = createSession(IWifiNanSessionListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PublishData)PublishData.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label308;
          }
        }
        for (paramParcel1 = (PublishSettings)PublishSettings.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          publish(paramInt1, (PublishData)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (SubscribeData)SubscribeData.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label385;
          }
        }
        for (paramParcel1 = (SubscribeSettings)SubscribeSettings.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          subscribe(paramInt1, (SubscribeData)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 7: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        sendMessage(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        label308:
        label385:
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
        stopSession(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanManager");
      destroySession(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IWifiNanManager
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
      
      public void connect(IBinder paramIBinder, IWifiNanEventListener paramIWifiNanEventListener, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          localParcel1.writeStrongBinder(paramIBinder);
          paramIBinder = (IBinder)localObject;
          if (paramIWifiNanEventListener != null) {
            paramIBinder = paramIWifiNanEventListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int createSession(IWifiNanSessionListener paramIWifiNanSessionListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          if (paramIWifiNanSessionListener != null) {
            localIBinder = paramIWifiNanSessionListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void destroySession(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void disconnect(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.wifi.nan.IWifiNanManager";
      }
      
      public void publish(int paramInt, PublishData paramPublishData, PublishSettings paramPublishSettings)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
            localParcel1.writeInt(paramInt);
            if (paramPublishData != null)
            {
              localParcel1.writeInt(1);
              paramPublishData.writeToParcel(localParcel1, 0);
              if (paramPublishSettings != null)
              {
                localParcel1.writeInt(1);
                paramPublishSettings.writeToParcel(localParcel1, 0);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
        }
      }
      
      /* Error */
      public void requestConfig(ConfigRequest paramConfigRequest)
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
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 91	android/net/wifi/nan/ConfigRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/wifi/nan/IWifiNanManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 55 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 58	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 61	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 61	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramConfigRequest	ConfigRequest
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      public void sendMessage(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopSession(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void subscribe(int paramInt, SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.nan.IWifiNanManager");
            localParcel1.writeInt(paramInt);
            if (paramSubscribeData != null)
            {
              localParcel1.writeInt(1);
              paramSubscribeData.writeToParcel(localParcel1, 0);
              if (paramSubscribeSettings != null)
              {
                localParcel1.writeInt(1);
                paramSubscribeSettings.writeToParcel(localParcel1, 0);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
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
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/IWifiNanManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */