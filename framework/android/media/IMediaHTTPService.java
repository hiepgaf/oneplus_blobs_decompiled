package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaHTTPService
  extends IInterface
{
  public abstract IMediaHTTPConnection makeHTTPConnection()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaHTTPService
  {
    private static final String DESCRIPTOR = "android.media.IMediaHTTPService";
    static final int TRANSACTION_makeHTTPConnection = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.IMediaHTTPService");
    }
    
    public static IMediaHTTPService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IMediaHTTPService");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaHTTPService))) {
        return (IMediaHTTPService)localIInterface;
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
      Object localObject = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.IMediaHTTPService");
        return true;
      }
      paramParcel1.enforceInterface("android.media.IMediaHTTPService");
      IMediaHTTPConnection localIMediaHTTPConnection = makeHTTPConnection();
      paramParcel2.writeNoException();
      paramParcel1 = (Parcel)localObject;
      if (localIMediaHTTPConnection != null) {
        paramParcel1 = localIMediaHTTPConnection.asBinder();
      }
      paramParcel2.writeStrongBinder(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IMediaHTTPService
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
        return "android.media.IMediaHTTPService";
      }
      
      public IMediaHTTPConnection makeHTTPConnection()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaHTTPService");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IMediaHTTPConnection localIMediaHTTPConnection = IMediaHTTPConnection.Stub.asInterface(localParcel2.readStrongBinder());
          return localIMediaHTTPConnection;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IMediaHTTPService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */