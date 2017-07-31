package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IStopUserCallback
  extends IInterface
{
  public abstract void userStopAborted(int paramInt)
    throws RemoteException;
  
  public abstract void userStopped(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IStopUserCallback
  {
    private static final String DESCRIPTOR = "android.app.IStopUserCallback";
    static final int TRANSACTION_userStopAborted = 2;
    static final int TRANSACTION_userStopped = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IStopUserCallback");
    }
    
    public static IStopUserCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IStopUserCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IStopUserCallback))) {
        return (IStopUserCallback)localIInterface;
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
        paramParcel2.writeString("android.app.IStopUserCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IStopUserCallback");
        userStopped(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.app.IStopUserCallback");
      userStopAborted(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IStopUserCallback
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
        return "android.app.IStopUserCallback";
      }
      
      public void userStopAborted(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IStopUserCallback");
          localParcel1.writeInt(paramInt);
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
      
      public void userStopped(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IStopUserCallback");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IStopUserCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */