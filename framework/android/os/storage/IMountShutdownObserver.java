package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMountShutdownObserver
  extends IInterface
{
  public abstract void onShutDownComplete(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMountShutdownObserver
  {
    private static final String DESCRIPTOR = "IMountShutdownObserver";
    static final int TRANSACTION_onShutDownComplete = 1;
    
    public Stub()
    {
      attachInterface(this, "IMountShutdownObserver");
    }
    
    public static IMountShutdownObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("IMountShutdownObserver");
      if ((localIInterface != null) && ((localIInterface instanceof IMountShutdownObserver))) {
        return (IMountShutdownObserver)localIInterface;
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
        paramParcel2.writeString("IMountShutdownObserver");
        return true;
      }
      paramParcel1.enforceInterface("IMountShutdownObserver");
      onShutDownComplete(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMountShutdownObserver
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
        return "IMountShutdownObserver";
      }
      
      public void onShutDownComplete(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountShutdownObserver");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/IMountShutdownObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */