package android.printservice.recommendation;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRecommendationService
  extends IInterface
{
  public abstract void registerCallbacks(IRecommendationServiceCallbacks paramIRecommendationServiceCallbacks)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecommendationService
  {
    private static final String DESCRIPTOR = "android.printservice.recommendation.IRecommendationService";
    static final int TRANSACTION_registerCallbacks = 1;
    
    public Stub()
    {
      attachInterface(this, "android.printservice.recommendation.IRecommendationService");
    }
    
    public static IRecommendationService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.printservice.recommendation.IRecommendationService");
      if ((localIInterface != null) && ((localIInterface instanceof IRecommendationService))) {
        return (IRecommendationService)localIInterface;
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
        paramParcel2.writeString("android.printservice.recommendation.IRecommendationService");
        return true;
      }
      paramParcel1.enforceInterface("android.printservice.recommendation.IRecommendationService");
      registerCallbacks(IRecommendationServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IRecommendationService
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
        return "android.printservice.recommendation.IRecommendationService";
      }
      
      public void registerCallbacks(IRecommendationServiceCallbacks paramIRecommendationServiceCallbacks)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.recommendation.IRecommendationService");
          if (paramIRecommendationServiceCallbacks != null) {
            localIBinder = paramIRecommendationServiceCallbacks.asBinder();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/recommendation/IRecommendationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */