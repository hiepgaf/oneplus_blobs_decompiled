package android.media;

import android.media.session.ISessionController;
import android.media.session.ISessionController.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRemoteVolumeController
  extends IInterface
{
  public abstract void remoteVolumeChanged(ISessionController paramISessionController, int paramInt)
    throws RemoteException;
  
  public abstract void updateRemoteController(ISessionController paramISessionController)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRemoteVolumeController
  {
    private static final String DESCRIPTOR = "android.media.IRemoteVolumeController";
    static final int TRANSACTION_remoteVolumeChanged = 1;
    static final int TRANSACTION_updateRemoteController = 2;
    
    public Stub()
    {
      attachInterface(this, "android.media.IRemoteVolumeController");
    }
    
    public static IRemoteVolumeController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IRemoteVolumeController");
      if ((localIInterface != null) && ((localIInterface instanceof IRemoteVolumeController))) {
        return (IRemoteVolumeController)localIInterface;
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
        paramParcel2.writeString("android.media.IRemoteVolumeController");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.IRemoteVolumeController");
        remoteVolumeChanged(ISessionController.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.media.IRemoteVolumeController");
      updateRemoteController(ISessionController.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IRemoteVolumeController
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
        return "android.media.IRemoteVolumeController";
      }
      
      public void remoteVolumeChanged(ISessionController paramISessionController, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IRemoteVolumeController");
          if (paramISessionController != null) {
            localIBinder = paramISessionController.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void updateRemoteController(ISessionController paramISessionController)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IRemoteVolumeController");
          if (paramISessionController != null) {
            localIBinder = paramISessionController.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IRemoteVolumeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */