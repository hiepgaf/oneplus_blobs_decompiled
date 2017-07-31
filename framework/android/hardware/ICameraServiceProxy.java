package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICameraServiceProxy
  extends IInterface
{
  public abstract void notifyCameraState(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void pingForUserUpdate()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICameraServiceProxy
  {
    private static final String DESCRIPTOR = "android.hardware.ICameraServiceProxy";
    static final int TRANSACTION_notifyCameraState = 2;
    static final int TRANSACTION_pingForUserUpdate = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.ICameraServiceProxy");
    }
    
    public static ICameraServiceProxy asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.ICameraServiceProxy");
      if ((localIInterface != null) && ((localIInterface instanceof ICameraServiceProxy))) {
        return (ICameraServiceProxy)localIInterface;
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
        paramParcel2.writeString("android.hardware.ICameraServiceProxy");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.ICameraServiceProxy");
        pingForUserUpdate();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.ICameraServiceProxy");
      notifyCameraState(paramParcel1.readString(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ICameraServiceProxy
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
        return "android.hardware.ICameraServiceProxy";
      }
      
      public void notifyCameraState(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.ICameraServiceProxy");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void pingForUserUpdate()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.ICameraServiceProxy");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ICameraServiceProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */