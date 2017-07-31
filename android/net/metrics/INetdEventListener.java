package android.net.metrics;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface INetdEventListener
  extends IInterface
{
  public static final int EVENT_GETADDRINFO = 1;
  public static final int EVENT_GETHOSTBYNAME = 2;
  
  public abstract void onDnsEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetdEventListener
  {
    private static final String DESCRIPTOR = "android.net.metrics.INetdEventListener";
    static final int TRANSACTION_onDnsEvent = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.metrics.INetdEventListener");
    }
    
    public static INetdEventListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.metrics.INetdEventListener");
      if ((localIInterface != null) && ((localIInterface instanceof INetdEventListener))) {
        return (INetdEventListener)localIInterface;
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
        paramParcel2.writeString("android.net.metrics.INetdEventListener");
        return true;
      }
      paramParcel1.enforceInterface("android.net.metrics.INetdEventListener");
      onDnsEvent(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements INetdEventListener
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
        return "android.net.metrics.INetdEventListener";
      }
      
      public void onDnsEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.metrics.INetdEventListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/INetdEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */