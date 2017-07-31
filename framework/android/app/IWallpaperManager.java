package android.app;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IWallpaperManager
  extends IInterface
{
  public abstract void clearWallpaper(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int getHeightHint()
    throws RemoteException;
  
  public abstract String getName()
    throws RemoteException;
  
  public abstract boolean getWaitingForUnLock()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt1, Bundle paramBundle, int paramInt2)
    throws RemoteException;
  
  public abstract int getWallpaperIdForUser(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract WallpaperInfo getWallpaperInfo(int paramInt)
    throws RemoteException;
  
  public abstract int getWidthHint()
    throws RemoteException;
  
  public abstract boolean hasNamedWallpaper(String paramString)
    throws RemoteException;
  
  public abstract boolean isSetWallpaperAllowed(String paramString)
    throws RemoteException;
  
  public abstract boolean isWallpaperBackupEligible(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean isWallpaperSupported(String paramString)
    throws RemoteException;
  
  public abstract void setDimensionHints(int paramInt1, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void setDisplayPadding(Rect paramRect, String paramString)
    throws RemoteException;
  
  public abstract boolean setLockWallpaperCallback(IWallpaperManagerCallback paramIWallpaperManagerCallback)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor setWallpaper(String paramString1, String paramString2, Rect paramRect, boolean paramBoolean, Bundle paramBundle, int paramInt1, IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt2)
    throws RemoteException;
  
  public abstract void setWallpaperComponent(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void setWallpaperComponentChecked(ComponentName paramComponentName, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void settingsRestored()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWallpaperManager
  {
    private static final String DESCRIPTOR = "android.app.IWallpaperManager";
    static final int TRANSACTION_clearWallpaper = 7;
    static final int TRANSACTION_getHeightHint = 11;
    static final int TRANSACTION_getName = 13;
    static final int TRANSACTION_getWaitingForUnLock = 19;
    static final int TRANSACTION_getWallpaper = 4;
    static final int TRANSACTION_getWallpaperIdForUser = 5;
    static final int TRANSACTION_getWallpaperInfo = 6;
    static final int TRANSACTION_getWidthHint = 10;
    static final int TRANSACTION_hasNamedWallpaper = 8;
    static final int TRANSACTION_isSetWallpaperAllowed = 16;
    static final int TRANSACTION_isWallpaperBackupEligible = 17;
    static final int TRANSACTION_isWallpaperSupported = 15;
    static final int TRANSACTION_setDimensionHints = 9;
    static final int TRANSACTION_setDisplayPadding = 12;
    static final int TRANSACTION_setLockWallpaperCallback = 18;
    static final int TRANSACTION_setWallpaper = 1;
    static final int TRANSACTION_setWallpaperComponent = 3;
    static final int TRANSACTION_setWallpaperComponentChecked = 2;
    static final int TRANSACTION_settingsRestored = 14;
    
    public Stub()
    {
      attachInterface(this, "android.app.IWallpaperManager");
    }
    
    public static IWallpaperManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IWallpaperManager");
      if ((localIInterface != null) && ((localIInterface instanceof IWallpaperManager))) {
        return (IWallpaperManager)localIInterface;
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
      Object localObject2;
      Object localObject1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IWallpaperManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        localObject2 = paramParcel1.readString();
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label322;
          }
          bool = true;
          Bundle localBundle = new Bundle();
          paramParcel1 = setWallpaper((String)localObject2, str, (Rect)localObject1, bool, localBundle, paramParcel1.readInt(), IWallpaperManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label328;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          if (localBundle == null) {
            break label336;
          }
          paramParcel2.writeInt(1);
          localBundle.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label239;
          paramParcel2.writeInt(0);
          break label297;
          paramParcel2.writeInt(0);
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setWallpaperComponentChecked((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setWallpaperComponent(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        localObject2 = IWallpaperManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        localObject1 = new Bundle();
        paramParcel1 = getWallpaper((IWallpaperManagerCallback)localObject2, paramInt1, (Bundle)localObject1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          if (localObject1 == null) {
            break label528;
          }
          paramParcel2.writeInt(1);
          ((Bundle)localObject1).writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
          break;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        paramInt1 = getWallpaperIdForUser(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        paramParcel1 = getWallpaperInfo(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        clearWallpaper(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        bool = hasNamedWallpaper(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        setDimensionHints(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        paramInt1 = getWidthHint();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        paramInt1 = getHeightHint();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setDisplayPadding((Rect)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        paramParcel1 = getName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        settingsRestored();
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        bool = isWallpaperSupported(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        bool = isSetWallpaperAllowed(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        bool = isWallpaperBackupEligible(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 18: 
        label239:
        label297:
        label322:
        label328:
        label336:
        label528:
        paramParcel1.enforceInterface("android.app.IWallpaperManager");
        bool = setLockWallpaperCallback(IWallpaperManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.app.IWallpaperManager");
      boolean bool = getWaitingForUnLock();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IWallpaperManager
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
      
      public void clearWallpaper(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getHeightHint()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IWallpaperManager";
      }
      
      public String getName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean getWaitingForUnLock()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 19
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 51 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 54	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 63	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 57	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt1, Bundle paramBundle, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 77 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 80	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 6
        //   41: iload_2
        //   42: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   45: aload 6
        //   47: iload 4
        //   49: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   52: aload_0
        //   53: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   56: iconst_4
        //   57: aload 6
        //   59: aload 7
        //   61: iconst_0
        //   62: invokeinterface 51 5 0
        //   67: pop
        //   68: aload 7
        //   70: invokevirtual 54	android/os/Parcel:readException	()V
        //   73: aload 7
        //   75: invokevirtual 63	android/os/Parcel:readInt	()I
        //   78: ifeq +43 -> 121
        //   81: getstatic 86	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   84: aload 7
        //   86: invokeinterface 92 2 0
        //   91: checkcast 82	android/os/ParcelFileDescriptor
        //   94: astore_1
        //   95: aload 7
        //   97: invokevirtual 63	android/os/Parcel:readInt	()I
        //   100: ifeq +9 -> 109
        //   103: aload_3
        //   104: aload 7
        //   106: invokevirtual 98	android/os/Bundle:readFromParcel	(Landroid/os/Parcel;)V
        //   109: aload 7
        //   111: invokevirtual 57	android/os/Parcel:recycle	()V
        //   114: aload 6
        //   116: invokevirtual 57	android/os/Parcel:recycle	()V
        //   119: aload_1
        //   120: areturn
        //   121: aconst_null
        //   122: astore_1
        //   123: goto -28 -> 95
        //   126: astore_1
        //   127: aload 7
        //   129: invokevirtual 57	android/os/Parcel:recycle	()V
        //   132: aload 6
        //   134: invokevirtual 57	android/os/Parcel:recycle	()V
        //   137: aload_1
        //   138: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	139	0	this	Proxy
        //   0	139	1	paramIWallpaperManagerCallback	IWallpaperManagerCallback
        //   0	139	2	paramInt1	int
        //   0	139	3	paramBundle	Bundle
        //   0	139	4	paramInt2	int
        //   1	34	5	localIBinder	IBinder
        //   6	127	6	localParcel1	Parcel
        //   11	117	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	126	finally
        //   24	32	126	finally
        //   32	95	126	finally
        //   95	109	126	finally
      }
      
      public int getWallpaperIdForUser(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public WallpaperInfo getWallpaperInfo(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 51 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 54	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 63	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 105	android/app/WallpaperInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 92 2 0
        //   59: checkcast 104	android/app/WallpaperInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 57	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localWallpaperInfo	WallpaperInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public int getWidthHint()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean hasNamedWallpaper(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 8
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 63	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isSetWallpaperAllowed(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 16
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 63	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isWallpaperBackupEligible(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 17
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 63	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 57	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 57	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean isWallpaperSupported(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 15
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 63	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public void setDimensionHints(int paramInt1, int paramInt2, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void setDisplayPadding(Rect paramRect, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 122	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 12
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 51 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 54	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 57	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 57	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 57	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramRect	Rect
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public boolean setLockWallpaperCallback(IWallpaperManagerCallback paramIWallpaperManagerCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 77 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 80	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 18
        //   45: aload 5
        //   47: aload 6
        //   49: iconst_0
        //   50: invokeinterface 51 5 0
        //   55: pop
        //   56: aload 6
        //   58: invokevirtual 54	android/os/Parcel:readException	()V
        //   61: aload 6
        //   63: invokevirtual 63	android/os/Parcel:readInt	()I
        //   66: istore_2
        //   67: iload_2
        //   68: ifeq +17 -> 85
        //   71: iconst_1
        //   72: istore_3
        //   73: aload 6
        //   75: invokevirtual 57	android/os/Parcel:recycle	()V
        //   78: aload 5
        //   80: invokevirtual 57	android/os/Parcel:recycle	()V
        //   83: iload_3
        //   84: ireturn
        //   85: iconst_0
        //   86: istore_3
        //   87: goto -14 -> 73
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 57	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 57	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramIWallpaperManagerCallback	IWallpaperManagerCallback
        //   66	2	2	i	int
        //   72	15	3	bool	boolean
        //   1	34	4	localIBinder	IBinder
        //   6	91	5	localParcel1	Parcel
        //   11	81	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	90	finally
        //   24	32	90	finally
        //   32	67	90	finally
      }
      
      public ParcelFileDescriptor setWallpaper(String paramString1, String paramString2, Rect paramRect, boolean paramBoolean, Bundle paramBundle, int paramInt1, IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt2)
        throws RemoteException
      {
        Object localObject = null;
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
            localParcel1.writeString(paramString1);
            localParcel1.writeString(paramString2);
            if (paramRect != null)
            {
              localParcel1.writeInt(1);
              paramRect.writeToParcel(localParcel1, 0);
              break label201;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt1);
              paramString1 = (String)localObject;
              if (paramIWallpaperManagerCallback != null) {
                paramString1 = paramIWallpaperManagerCallback.asBinder();
              }
              localParcel1.writeStrongBinder(paramString1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                label141:
                if (localParcel2.readInt() != 0) {
                  paramBundle.readFromParcel(localParcel2);
                }
                return paramString1;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label201:
          do
          {
            i = 0;
            break;
            paramString1 = null;
            break label141;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public void setWallpaperComponent(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 131	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 51 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 54	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 57	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 57	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 57	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramComponentName	ComponentName
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      /* Error */
      public void setWallpaperComponentChecked(ComponentName paramComponentName, String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 131	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/app/IWallpaperManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_2
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 51 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 54	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 57	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 57	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramComponentName	ComponentName
        //   0	100	2	paramString	String
        //   0	100	3	paramInt	int
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public void settingsRestored()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IWallpaperManager");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IWallpaperManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */