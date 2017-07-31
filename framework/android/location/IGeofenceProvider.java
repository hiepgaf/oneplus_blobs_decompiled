package android.location;

import android.hardware.location.IGeofenceHardware;
import android.hardware.location.IGeofenceHardware.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGeofenceProvider
  extends IInterface
{
  public abstract void setGeofenceHardware(IGeofenceHardware paramIGeofenceHardware)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGeofenceProvider
  {
    private static final String DESCRIPTOR = "android.location.IGeofenceProvider";
    static final int TRANSACTION_setGeofenceHardware = 1;
    
    public Stub()
    {
      attachInterface(this, "android.location.IGeofenceProvider");
    }
    
    public static IGeofenceProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IGeofenceProvider");
      if ((localIInterface != null) && ((localIInterface instanceof IGeofenceProvider))) {
        return (IGeofenceProvider)localIInterface;
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
        paramParcel2.writeString("android.location.IGeofenceProvider");
        return true;
      }
      paramParcel1.enforceInterface("android.location.IGeofenceProvider");
      setGeofenceHardware(IGeofenceHardware.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGeofenceProvider
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
        return "android.location.IGeofenceProvider";
      }
      
      public void setGeofenceHardware(IGeofenceHardware paramIGeofenceHardware)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IGeofenceProvider");
          if (paramIGeofenceHardware != null) {
            localIBinder = paramIGeofenceHardware.asBinder();
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IGeofenceProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */