package android.service.wallpaper;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.MotionEvent;

public abstract interface IWallpaperEngine
  extends IInterface
{
  public abstract void destroy()
    throws RemoteException;
  
  public abstract void dispatchPointer(MotionEvent paramMotionEvent)
    throws RemoteException;
  
  public abstract void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void setDesiredSize(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setDisplayPadding(Rect paramRect)
    throws RemoteException;
  
  public abstract void setVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWallpaperEngine
  {
    private static final String DESCRIPTOR = "android.service.wallpaper.IWallpaperEngine";
    static final int TRANSACTION_destroy = 6;
    static final int TRANSACTION_dispatchPointer = 4;
    static final int TRANSACTION_dispatchWallpaperCommand = 5;
    static final int TRANSACTION_setDesiredSize = 1;
    static final int TRANSACTION_setDisplayPadding = 2;
    static final int TRANSACTION_setVisibility = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.wallpaper.IWallpaperEngine");
    }
    
    public static IWallpaperEngine asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.wallpaper.IWallpaperEngine");
      if ((localIInterface != null) && ((localIInterface instanceof IWallpaperEngine))) {
        return (IWallpaperEngine)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.wallpaper.IWallpaperEngine");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
        setDesiredSize(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setDisplayPadding(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setVisibility(bool);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (MotionEvent)MotionEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          dispatchPointer(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
        paramParcel2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        int i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          dispatchWallpaperCommand(paramParcel2, paramInt1, paramInt2, i, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.service.wallpaper.IWallpaperEngine");
      destroy();
      return true;
    }
    
    private static class Proxy
      implements IWallpaperEngine
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
      
      public void destroy()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.wallpaper.IWallpaperEngine");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void dispatchPointer(MotionEvent paramMotionEvent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 59	android/view/MotionEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/service/wallpaper/IWallpaperEngine$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramMotionEvent	MotionEvent
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: aload 6
        //   7: ldc 33
        //   9: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 6
        //   14: aload_1
        //   15: invokevirtual 64	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   18: aload 6
        //   20: iload_2
        //   21: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   24: aload 6
        //   26: iload_3
        //   27: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   30: aload 6
        //   32: iload 4
        //   34: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   37: aload 5
        //   39: ifnull +38 -> 77
        //   42: aload 6
        //   44: iconst_1
        //   45: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   48: aload 5
        //   50: aload 6
        //   52: iconst_0
        //   53: invokevirtual 67	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload_0
        //   57: getfield 19	android/service/wallpaper/IWallpaperEngine$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   60: iconst_5
        //   61: aload 6
        //   63: aconst_null
        //   64: iconst_1
        //   65: invokeinterface 43 5 0
        //   70: pop
        //   71: aload 6
        //   73: invokevirtual 46	android/os/Parcel:recycle	()V
        //   76: return
        //   77: aload 6
        //   79: iconst_0
        //   80: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   83: goto -27 -> 56
        //   86: astore_1
        //   87: aload 6
        //   89: invokevirtual 46	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString	String
        //   0	94	2	paramInt1	int
        //   0	94	3	paramInt2	int
        //   0	94	4	paramInt3	int
        //   0	94	5	paramBundle	Bundle
        //   3	85	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	37	86	finally
        //   42	56	86	finally
        //   56	71	86	finally
        //   77	83	86	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.wallpaper.IWallpaperEngine";
      }
      
      public void setDesiredSize(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.wallpaper.IWallpaperEngine");
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
      
      /* Error */
      public void setDisplayPadding(Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 76	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/service/wallpaper/IWallpaperEngine$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramRect	Rect
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void setVisibility(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 33
        //   9: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +27 -> 40
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/service/wallpaper/IWallpaperEngine$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_3
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 43 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 46	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 46	android/os/Parcel:recycle	()V
        //   51: aload 4
        //   53: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	54	0	this	Proxy
        //   0	54	1	paramBoolean	boolean
        //   1	41	2	i	int
        //   5	43	3	localParcel	Parcel
        //   45	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	45	finally
        //   16	35	45	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/wallpaper/IWallpaperEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */