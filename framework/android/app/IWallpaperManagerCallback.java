package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWallpaperManagerCallback
  extends IInterface
{
  public abstract void onWallpaperChanged()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWallpaperManagerCallback
  {
    private static final String DESCRIPTOR = "android.app.IWallpaperManagerCallback";
    static final int TRANSACTION_onWallpaperChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IWallpaperManagerCallback");
    }
    
    public static IWallpaperManagerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IWallpaperManagerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IWallpaperManagerCallback))) {
        return (IWallpaperManagerCallback)localIInterface;
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
        paramParcel2.writeString("android.app.IWallpaperManagerCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.app.IWallpaperManagerCallback");
      onWallpaperChanged();
      return true;
    }
    
    private static class Proxy
      implements IWallpaperManagerCallback
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
        return "android.app.IWallpaperManagerCallback";
      }
      
      public void onWallpaperChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IWallpaperManagerCallback");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IWallpaperManagerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */