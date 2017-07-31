package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaRouterClient
  extends IInterface
{
  public abstract void onStateChanged()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaRouterClient
  {
    private static final String DESCRIPTOR = "android.media.IMediaRouterClient";
    static final int TRANSACTION_onStateChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.IMediaRouterClient");
    }
    
    public static IMediaRouterClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IMediaRouterClient");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaRouterClient))) {
        return (IMediaRouterClient)localIInterface;
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
        paramParcel2.writeString("android.media.IMediaRouterClient");
        return true;
      }
      paramParcel1.enforceInterface("android.media.IMediaRouterClient");
      onStateChanged();
      return true;
    }
    
    private static class Proxy
      implements IMediaRouterClient
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
        return "android.media.IMediaRouterClient";
      }
      
      public void onStateChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IMediaRouterClient");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IMediaRouterClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */