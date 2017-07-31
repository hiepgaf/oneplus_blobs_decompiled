package android.location;

import android.hardware.location.IFusedLocationHardware;
import android.hardware.location.IFusedLocationHardware.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFusedProvider
  extends IInterface
{
  public abstract void onFusedLocationHardwareChange(IFusedLocationHardware paramIFusedLocationHardware)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFusedProvider
  {
    private static final String DESCRIPTOR = "android.location.IFusedProvider";
    static final int TRANSACTION_onFusedLocationHardwareChange = 1;
    
    public Stub()
    {
      attachInterface(this, "android.location.IFusedProvider");
    }
    
    public static IFusedProvider asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IFusedProvider");
      if ((localIInterface != null) && ((localIInterface instanceof IFusedProvider))) {
        return (IFusedProvider)localIInterface;
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
        paramParcel2.writeString("android.location.IFusedProvider");
        return true;
      }
      paramParcel1.enforceInterface("android.location.IFusedProvider");
      onFusedLocationHardwareChange(IFusedLocationHardware.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFusedProvider
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
        return "android.location.IFusedProvider";
      }
      
      public void onFusedLocationHardwareChange(IFusedLocationHardware paramIFusedLocationHardware)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedProvider");
          if (paramIFusedLocationHardware != null) {
            localIBinder = paramIFusedLocationHardware.asBinder();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IFusedProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */