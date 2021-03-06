package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITransientNotification
  extends IInterface
{
  public abstract void hide()
    throws RemoteException;
  
  public abstract void show(IBinder paramIBinder)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITransientNotification
  {
    private static final String DESCRIPTOR = "android.app.ITransientNotification";
    static final int TRANSACTION_hide = 2;
    static final int TRANSACTION_show = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.ITransientNotification");
    }
    
    public static ITransientNotification asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.ITransientNotification");
      if ((localIInterface != null) && ((localIInterface instanceof ITransientNotification))) {
        return (ITransientNotification)localIInterface;
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
        paramParcel2.writeString("android.app.ITransientNotification");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.ITransientNotification");
        show(paramParcel1.readStrongBinder());
        return true;
      }
      paramParcel1.enforceInterface("android.app.ITransientNotification");
      hide();
      return true;
    }
    
    private static class Proxy
      implements ITransientNotification
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
        return "android.app.ITransientNotification";
      }
      
      public void hide()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITransientNotification");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void show(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITransientNotification");
          localParcel.writeStrongBinder(paramIBinder);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ITransientNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */