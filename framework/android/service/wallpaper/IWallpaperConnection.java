package android.service.wallpaper;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public abstract interface IWallpaperConnection
  extends IInterface
{
  public abstract void attachEngine(IWallpaperEngine paramIWallpaperEngine)
    throws RemoteException;
  
  public abstract void engineShown(IWallpaperEngine paramIWallpaperEngine)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor setWallpaper(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWallpaperConnection
  {
    private static final String DESCRIPTOR = "android.service.wallpaper.IWallpaperConnection";
    static final int TRANSACTION_attachEngine = 1;
    static final int TRANSACTION_engineShown = 2;
    static final int TRANSACTION_setWallpaper = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.wallpaper.IWallpaperConnection");
    }
    
    public static IWallpaperConnection asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.wallpaper.IWallpaperConnection");
      if ((localIInterface != null) && ((localIInterface instanceof IWallpaperConnection))) {
        return (IWallpaperConnection)localIInterface;
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
        paramParcel2.writeString("android.service.wallpaper.IWallpaperConnection");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperConnection");
        attachEngine(IWallpaperEngine.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperConnection");
        engineShown(IWallpaperEngine.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperConnection");
      paramParcel1 = setWallpaper(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements IWallpaperConnection
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
      
      public void attachEngine(IWallpaperEngine paramIWallpaperEngine)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.wallpaper.IWallpaperConnection");
          if (paramIWallpaperEngine != null) {
            localIBinder = paramIWallpaperEngine.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void engineShown(IWallpaperEngine paramIWallpaperEngine)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.wallpaper.IWallpaperConnection");
          if (paramIWallpaperEngine != null) {
            localIBinder = paramIWallpaperEngine.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.service.wallpaper.IWallpaperConnection";
      }
      
      /* Error */
      public ParcelFileDescriptor setWallpaper(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 66	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/service/wallpaper/IWallpaperConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_3
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 51 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 54	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 70	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 76	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 82 2 0
        //   53: checkcast 72	android/os/ParcelFileDescriptor
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 57	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 57	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 57	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramString	String
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/wallpaper/IWallpaperConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */