package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaResourceMonitor
  extends IInterface
{
  public abstract void notifyResourceGranted(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaResourceMonitor
  {
    private static final String DESCRIPTOR = "android.media.IMediaResourceMonitor";
    static final int TRANSACTION_notifyResourceGranted = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.IMediaResourceMonitor");
    }
    
    public static IMediaResourceMonitor asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IMediaResourceMonitor");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaResourceMonitor))) {
        return (IMediaResourceMonitor)localIInterface;
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
        paramParcel2.writeString("android.media.IMediaResourceMonitor");
        return true;
      }
      paramParcel1.enforceInterface("android.media.IMediaResourceMonitor");
      notifyResourceGranted(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IMediaResourceMonitor
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
        return "android.media.IMediaResourceMonitor";
      }
      
      public void notifyResourceGranted(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IMediaResourceMonitor");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IMediaResourceMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */