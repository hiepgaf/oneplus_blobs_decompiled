package android.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGnssStatusProvider
  extends IInterface
{
  public abstract void registerGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
    throws RemoteException;
  
  public abstract void unregisterGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGnssStatusProvider
  {
    private static final String DESCRIPTOR = "android.location.IGnssStatusProvider";
    static final int TRANSACTION_registerGnssStatusCallback = 1;
    static final int TRANSACTION_unregisterGnssStatusCallback = 2;
    
    public Stub()
    {
      attachInterface(this, "android.location.IGnssStatusProvider");
    }
    
    public static IGnssStatusProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IGnssStatusProvider");
      if ((localIInterface != null) && ((localIInterface instanceof IGnssStatusProvider))) {
        return (IGnssStatusProvider)localIInterface;
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
        paramParcel2.writeString("android.location.IGnssStatusProvider");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.IGnssStatusProvider");
        registerGnssStatusCallback(IGnssStatusListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.location.IGnssStatusProvider");
      unregisterGnssStatusCallback(IGnssStatusListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGnssStatusProvider
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
        return "android.location.IGnssStatusProvider";
      }
      
      public void registerGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IGnssStatusProvider");
          if (paramIGnssStatusListener != null) {
            localIBinder = paramIGnssStatusListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void unregisterGnssStatusCallback(IGnssStatusListener paramIGnssStatusListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IGnssStatusProvider");
          if (paramIGnssStatusListener != null) {
            localIBinder = paramIGnssStatusListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IGnssStatusProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */