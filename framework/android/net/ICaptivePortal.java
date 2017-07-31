package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICaptivePortal
  extends IInterface
{
  public abstract void appResponse(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICaptivePortal
  {
    private static final String DESCRIPTOR = "android.net.ICaptivePortal";
    static final int TRANSACTION_appResponse = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.ICaptivePortal");
    }
    
    public static ICaptivePortal asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.ICaptivePortal");
      if ((localIInterface != null) && ((localIInterface instanceof ICaptivePortal))) {
        return (ICaptivePortal)localIInterface;
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
        paramParcel2.writeString("android.net.ICaptivePortal");
        return true;
      }
      paramParcel1.enforceInterface("android.net.ICaptivePortal");
      appResponse(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ICaptivePortal
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void appResponse(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.ICaptivePortal");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.ICaptivePortal";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ICaptivePortal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */