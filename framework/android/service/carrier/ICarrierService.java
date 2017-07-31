package android.service.carrier;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;

public abstract interface ICarrierService
  extends IInterface
{
  public abstract PersistableBundle getCarrierConfig(CarrierIdentifier paramCarrierIdentifier)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICarrierService
  {
    private static final String DESCRIPTOR = "android.service.carrier.ICarrierService";
    static final int TRANSACTION_getCarrierConfig = 1;
    
    public Stub()
    {
      attachInterface(this, "android.service.carrier.ICarrierService");
    }
    
    public static ICarrierService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.carrier.ICarrierService");
      if ((localIInterface != null) && ((localIInterface instanceof ICarrierService))) {
        return (ICarrierService)localIInterface;
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
        paramParcel2.writeString("android.service.carrier.ICarrierService");
        return true;
      }
      paramParcel1.enforceInterface("android.service.carrier.ICarrierService");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (CarrierIdentifier)CarrierIdentifier.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramParcel1 = getCarrierConfig(paramParcel1);
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements ICarrierService
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
      
      public PersistableBundle getCarrierConfig(CarrierIdentifier paramCarrierIdentifier)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.service.carrier.ICarrierService");
            if (paramCarrierIdentifier != null)
            {
              localParcel1.writeInt(1);
              paramCarrierIdentifier.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramCarrierIdentifier = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(localParcel2);
                return paramCarrierIdentifier;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramCarrierIdentifier = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.carrier.ICarrierService";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/carrier/ICarrierService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */