package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaScannerService
  extends IInterface
{
  public abstract void requestScanFile(String paramString1, String paramString2, IMediaScannerListener paramIMediaScannerListener)
    throws RemoteException;
  
  public abstract void scanFile(String paramString1, String paramString2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaScannerService
  {
    private static final String DESCRIPTOR = "android.media.IMediaScannerService";
    static final int TRANSACTION_requestScanFile = 1;
    static final int TRANSACTION_scanFile = 2;
    
    public Stub()
    {
      attachInterface(this, "android.media.IMediaScannerService");
    }
    
    public static IMediaScannerService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IMediaScannerService");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaScannerService))) {
        return (IMediaScannerService)localIInterface;
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
        paramParcel2.writeString("android.media.IMediaScannerService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.IMediaScannerService");
        requestScanFile(paramParcel1.readString(), paramParcel1.readString(), IMediaScannerListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.media.IMediaScannerService");
      scanFile(paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMediaScannerService
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
        return "android.media.IMediaScannerService";
      }
      
      public void requestScanFile(String paramString1, String paramString2, IMediaScannerListener paramIMediaScannerListener)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaScannerService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          paramString1 = (String)localObject;
          if (paramIMediaScannerListener != null) {
            paramString1 = paramIMediaScannerListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramString1);
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
      
      public void scanFile(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaScannerService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IMediaScannerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */