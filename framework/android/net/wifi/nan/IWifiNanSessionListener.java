package android.net.wifi.nan;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWifiNanSessionListener
  extends IInterface
{
  public abstract void onMatch(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
    throws RemoteException;
  
  public abstract void onMessageReceived(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws RemoteException;
  
  public abstract void onMessageSendFail(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onMessageSendSuccess(int paramInt)
    throws RemoteException;
  
  public abstract void onPublishFail(int paramInt)
    throws RemoteException;
  
  public abstract void onPublishTerminated(int paramInt)
    throws RemoteException;
  
  public abstract void onSubscribeFail(int paramInt)
    throws RemoteException;
  
  public abstract void onSubscribeTerminated(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWifiNanSessionListener
  {
    private static final String DESCRIPTOR = "android.net.wifi.nan.IWifiNanSessionListener";
    static final int TRANSACTION_onMatch = 5;
    static final int TRANSACTION_onMessageReceived = 8;
    static final int TRANSACTION_onMessageSendFail = 7;
    static final int TRANSACTION_onMessageSendSuccess = 6;
    static final int TRANSACTION_onPublishFail = 1;
    static final int TRANSACTION_onPublishTerminated = 2;
    static final int TRANSACTION_onSubscribeFail = 3;
    static final int TRANSACTION_onSubscribeTerminated = 4;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.nan.IWifiNanSessionListener");
    }
    
    public static IWifiNanSessionListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.nan.IWifiNanSessionListener");
      if ((localIInterface != null) && ((localIInterface instanceof IWifiNanSessionListener))) {
        return (IWifiNanSessionListener)localIInterface;
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
        paramParcel2.writeString("android.net.wifi.nan.IWifiNanSessionListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onPublishFail(paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onPublishTerminated(paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onSubscribeFail(paramParcel1.readInt());
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onSubscribeTerminated(paramParcel1.readInt());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onMatch(paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onMessageSendSuccess(paramParcel1.readInt());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
        onMessageSendFail(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.net.wifi.nan.IWifiNanSessionListener");
      onMessageReceived(paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IWifiNanSessionListener
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
        return "android.net.wifi.nan.IWifiNanSessionListener";
      }
      
      public void onMatch(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeByteArray(paramArrayOfByte1);
          localParcel.writeInt(paramInt2);
          localParcel.writeByteArray(paramArrayOfByte2);
          localParcel.writeInt(paramInt3);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onMessageReceived(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeByteArray(paramArrayOfByte);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onMessageSendFail(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onMessageSendSuccess(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPublishFail(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPublishTerminated(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSubscribeFail(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSubscribeTerminated(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.wifi.nan.IWifiNanSessionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(4, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/IWifiNanSessionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */