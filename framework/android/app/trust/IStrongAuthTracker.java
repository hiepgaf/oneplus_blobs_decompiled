package android.app.trust;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IStrongAuthTracker
  extends IInterface
{
  public abstract void onStrongAuthRequiredChanged(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IStrongAuthTracker
  {
    private static final String DESCRIPTOR = "android.app.trust.IStrongAuthTracker";
    static final int TRANSACTION_onStrongAuthRequiredChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.trust.IStrongAuthTracker");
    }
    
    public static IStrongAuthTracker asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.trust.IStrongAuthTracker");
      if ((localIInterface != null) && ((localIInterface instanceof IStrongAuthTracker))) {
        return (IStrongAuthTracker)localIInterface;
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
        paramParcel2.writeString("android.app.trust.IStrongAuthTracker");
        return true;
      }
      paramParcel1.enforceInterface("android.app.trust.IStrongAuthTracker");
      onStrongAuthRequiredChanged(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IStrongAuthTracker
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
        return "android.app.trust.IStrongAuthTracker";
      }
      
      public void onStrongAuthRequiredChanged(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.trust.IStrongAuthTracker");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/trust/IStrongAuthTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */