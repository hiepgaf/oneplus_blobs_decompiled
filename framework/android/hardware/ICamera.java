package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICamera
  extends IInterface
{
  public abstract void disconnect()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICamera
  {
    private static final String DESCRIPTOR = "android.hardware.ICamera";
    static final int TRANSACTION_disconnect = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.ICamera");
    }
    
    public static ICamera asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.ICamera");
      if ((localIInterface != null) && ((localIInterface instanceof ICamera))) {
        return (ICamera)localIInterface;
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
        paramParcel2.writeString("android.hardware.ICamera");
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.ICamera");
      disconnect();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements ICamera
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
      
      public void disconnect()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICamera");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.ICamera";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ICamera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */