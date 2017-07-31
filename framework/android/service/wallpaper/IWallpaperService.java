package android.service.wallpaper;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IWallpaperService
  extends IInterface
{
  public abstract void attach(IWallpaperConnection paramIWallpaperConnection, IBinder paramIBinder, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, Rect paramRect)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWallpaperService
  {
    private static final String DESCRIPTOR = "android.service.wallpaper.IWallpaperService";
    static final int TRANSACTION_attach = 1;
    
    public Stub()
    {
      attachInterface(this, "android.service.wallpaper.IWallpaperService");
    }
    
    public static IWallpaperService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.wallpaper.IWallpaperService");
      if ((localIInterface != null) && ((localIInterface instanceof IWallpaperService))) {
        return (IWallpaperService)localIInterface;
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
        paramParcel2.writeString("android.service.wallpaper.IWallpaperService");
        return true;
      }
      paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperService");
      paramParcel2 = IWallpaperConnection.Stub.asInterface(paramParcel1.readStrongBinder());
      IBinder localIBinder = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      boolean bool;
      int i;
      if (paramParcel1.readInt() != 0)
      {
        bool = true;
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label136;
        }
      }
      label136:
      for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        attach(paramParcel2, localIBinder, paramInt1, bool, paramInt2, i, paramParcel1);
        return true;
        bool = false;
        break;
      }
    }
    
    private static class Proxy
      implements IWallpaperService
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
      
      /* Error */
      public void attach(IWallpaperConnection paramIWallpaperConnection, IBinder paramIBinder, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 9
        //   3: iconst_1
        //   4: istore 8
        //   6: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 10
        //   11: aload 10
        //   13: ldc 34
        //   15: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   18: aload_1
        //   19: ifnull +11 -> 30
        //   22: aload_1
        //   23: invokeinterface 42 1 0
        //   28: astore 9
        //   30: aload 10
        //   32: aload 9
        //   34: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   37: aload 10
        //   39: aload_2
        //   40: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   43: aload 10
        //   45: iload_3
        //   46: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   49: iload 4
        //   51: ifeq +66 -> 117
        //   54: iload 8
        //   56: istore_3
        //   57: aload 10
        //   59: iload_3
        //   60: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   63: aload 10
        //   65: iload 5
        //   67: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   70: aload 10
        //   72: iload 6
        //   74: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   77: aload 7
        //   79: ifnull +43 -> 122
        //   82: aload 10
        //   84: iconst_1
        //   85: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   88: aload 7
        //   90: aload 10
        //   92: iconst_0
        //   93: invokevirtual 55	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   96: aload_0
        //   97: getfield 19	android/service/wallpaper/IWallpaperService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   100: iconst_1
        //   101: aload 10
        //   103: aconst_null
        //   104: iconst_1
        //   105: invokeinterface 61 5 0
        //   110: pop
        //   111: aload 10
        //   113: invokevirtual 64	android/os/Parcel:recycle	()V
        //   116: return
        //   117: iconst_0
        //   118: istore_3
        //   119: goto -62 -> 57
        //   122: aload 10
        //   124: iconst_0
        //   125: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   128: goto -32 -> 96
        //   131: astore_1
        //   132: aload 10
        //   134: invokevirtual 64	android/os/Parcel:recycle	()V
        //   137: aload_1
        //   138: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	139	0	this	Proxy
        //   0	139	1	paramIWallpaperConnection	IWallpaperConnection
        //   0	139	2	paramIBinder	IBinder
        //   0	139	3	paramInt1	int
        //   0	139	4	paramBoolean	boolean
        //   0	139	5	paramInt2	int
        //   0	139	6	paramInt3	int
        //   0	139	7	paramRect	Rect
        //   4	51	8	i	int
        //   1	32	9	localIBinder	IBinder
        //   9	124	10	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	18	131	finally
        //   22	30	131	finally
        //   30	49	131	finally
        //   57	77	131	finally
        //   82	96	131	finally
        //   96	111	131	finally
        //   122	128	131	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.wallpaper.IWallpaperService";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/wallpaper/IWallpaperService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */