package android.service.quicksettings;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IQSTileService
  extends IInterface
{
  public abstract void onClick(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onStartListening()
    throws RemoteException;
  
  public abstract void onStopListening()
    throws RemoteException;
  
  public abstract void onTileAdded()
    throws RemoteException;
  
  public abstract void onTileRemoved()
    throws RemoteException;
  
  public abstract void onUnlockComplete()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IQSTileService
  {
    private static final String DESCRIPTOR = "android.service.quicksettings.IQSTileService";
    static final int TRANSACTION_onClick = 5;
    static final int TRANSACTION_onStartListening = 3;
    static final int TRANSACTION_onStopListening = 4;
    static final int TRANSACTION_onTileAdded = 1;
    static final int TRANSACTION_onTileRemoved = 2;
    static final int TRANSACTION_onUnlockComplete = 6;
    
    public Stub()
    {
      attachInterface(this, "android.service.quicksettings.IQSTileService");
    }
    
    public static IQSTileService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.quicksettings.IQSTileService");
      if ((localIInterface != null) && ((localIInterface instanceof IQSTileService))) {
        return (IQSTileService)localIInterface;
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
        paramParcel2.writeString("android.service.quicksettings.IQSTileService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
        onTileAdded();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
        onTileRemoved();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
        onStartListening();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
        onStopListening();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
        onClick(paramParcel1.readStrongBinder());
        return true;
      }
      paramParcel1.enforceInterface("android.service.quicksettings.IQSTileService");
      onUnlockComplete();
      return true;
    }
    
    private static class Proxy
      implements IQSTileService
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
        return "android.service.quicksettings.IQSTileService";
      }
      
      public void onClick(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStartListening()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStopListening()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTileAdded()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTileRemoved()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onUnlockComplete()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.quicksettings.IQSTileService");
          this.mRemote.transact(6, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/quicksettings/IQSTileService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */