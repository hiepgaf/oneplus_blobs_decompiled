package android.media.tv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITvRemoteProvider
  extends IInterface
{
  public abstract void onInputBridgeConnected(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void setRemoteServiceInputSink(ITvRemoteServiceInput paramITvRemoteServiceInput)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvRemoteProvider
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvRemoteProvider";
    static final int TRANSACTION_onInputBridgeConnected = 2;
    static final int TRANSACTION_setRemoteServiceInputSink = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvRemoteProvider");
    }
    
    public static ITvRemoteProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvRemoteProvider");
      if ((localIInterface != null) && ((localIInterface instanceof ITvRemoteProvider))) {
        return (ITvRemoteProvider)localIInterface;
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
        paramParcel2.writeString("android.media.tv.ITvRemoteProvider");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvRemoteProvider");
        setRemoteServiceInputSink(ITvRemoteServiceInput.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel1.enforceInterface("android.media.tv.ITvRemoteProvider");
      onInputBridgeConnected(paramParcel1.readStrongBinder());
      return true;
    }
    
    private static class Proxy
      implements ITvRemoteProvider
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
        return "android.media.tv.ITvRemoteProvider";
      }
      
      public void onInputBridgeConnected(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvRemoteProvider");
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRemoteServiceInputSink(ITvRemoteServiceInput paramITvRemoteServiceInput)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvRemoteProvider");
          if (paramITvRemoteServiceInput != null) {
            localIBinder = paramITvRemoteServiceInput.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(1, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvRemoteProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */