package android.hardware.display;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IVirtualDisplayCallback
  extends IInterface
{
  public abstract void onPaused()
    throws RemoteException;
  
  public abstract void onResumed()
    throws RemoteException;
  
  public abstract void onStopped()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IVirtualDisplayCallback
  {
    private static final String DESCRIPTOR = "android.hardware.display.IVirtualDisplayCallback";
    static final int TRANSACTION_onPaused = 1;
    static final int TRANSACTION_onResumed = 2;
    static final int TRANSACTION_onStopped = 3;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.display.IVirtualDisplayCallback");
    }
    
    public static IVirtualDisplayCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.display.IVirtualDisplayCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IVirtualDisplayCallback))) {
        return (IVirtualDisplayCallback)localIInterface;
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
        paramParcel2.writeString("android.hardware.display.IVirtualDisplayCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.display.IVirtualDisplayCallback");
        onPaused();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.display.IVirtualDisplayCallback");
        onResumed();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.display.IVirtualDisplayCallback");
      onStopped();
      return true;
    }
    
    private static class Proxy
      implements IVirtualDisplayCallback
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
        return "android.hardware.display.IVirtualDisplayCallback";
      }
      
      public void onPaused()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.display.IVirtualDisplayCallback");
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onResumed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.display.IVirtualDisplayCallback");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStopped()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.display.IVirtualDisplayCallback");
          this.mRemote.transact(3, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/IVirtualDisplayCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */