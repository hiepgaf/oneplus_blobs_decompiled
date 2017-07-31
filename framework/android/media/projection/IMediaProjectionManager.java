package android.media.projection;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaProjectionManager
  extends IInterface
{
  public abstract void addCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    throws RemoteException;
  
  public abstract IMediaProjection createProjection(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract MediaProjectionInfo getActiveProjectionInfo()
    throws RemoteException;
  
  public abstract boolean hasProjectionPermission(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract boolean isValidMediaProjection(IMediaProjection paramIMediaProjection)
    throws RemoteException;
  
  public abstract void removeCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    throws RemoteException;
  
  public abstract void stopActiveProjection()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaProjectionManager
  {
    private static final String DESCRIPTOR = "android.media.projection.IMediaProjectionManager";
    static final int TRANSACTION_addCallback = 6;
    static final int TRANSACTION_createProjection = 2;
    static final int TRANSACTION_getActiveProjectionInfo = 4;
    static final int TRANSACTION_hasProjectionPermission = 1;
    static final int TRANSACTION_isValidMediaProjection = 3;
    static final int TRANSACTION_removeCallback = 7;
    static final int TRANSACTION_stopActiveProjection = 5;
    
    public Stub()
    {
      attachInterface(this, "android.media.projection.IMediaProjectionManager");
    }
    
    public static IMediaProjectionManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.projection.IMediaProjectionManager");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaProjectionManager))) {
        return (IMediaProjectionManager)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.projection.IMediaProjectionManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        bool = hasProjectionPermission(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        paramInt1 = paramParcel1.readInt();
        String str = paramParcel1.readString();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          paramParcel1 = createProjection(paramInt1, str, paramInt2, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label210;
          }
        }
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
          bool = false;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        bool = isValidMediaProjection(IMediaProjection.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        paramParcel1 = getActiveProjectionInfo();
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
      case 5: 
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        stopActiveProjection();
        paramParcel2.writeNoException();
        return true;
      case 6: 
        label210:
        paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
        addCallback(IMediaProjectionWatcherCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.media.projection.IMediaProjectionManager");
      removeCallback(IMediaProjectionWatcherCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMediaProjectionManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.projection.IMediaProjectionManager");
          if (paramIMediaProjectionWatcherCallback != null) {
            localIBinder = paramIMediaProjectionWatcherCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public IMediaProjection createProjection(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.projection.IMediaProjectionManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = IMediaProjection.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public MediaProjectionInfo getActiveProjectionInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/media/projection/IMediaProjectionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_4
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 51 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 54	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 82	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 88	android/media/projection/MediaProjectionInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 94 2 0
        //   48: checkcast 84	android/media/projection/MediaProjectionInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 57	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 57	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localMediaProjectionInfo	MediaProjectionInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.projection.IMediaProjectionManager";
      }
      
      /* Error */
      public boolean hasProjectionPermission(int paramInt, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/media/projection/IMediaProjectionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 51 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 54	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 82	android/os/Parcel:readInt	()I
        //   55: istore_1
        //   56: iload_1
        //   57: ifeq +17 -> 74
        //   60: iconst_1
        //   61: istore_3
        //   62: aload 5
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: iload_3
        //   73: ireturn
        //   74: iconst_0
        //   75: istore_3
        //   76: goto -14 -> 62
        //   79: astore_2
        //   80: aload 5
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload 4
        //   87: invokevirtual 57	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   0	92	2	paramString	String
        //   61	15	3	bool	boolean
        //   3	83	4	localParcel1	Parcel
        //   8	73	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	56	79	finally
      }
      
      /* Error */
      public boolean isValidMediaProjection(IMediaProjection paramIMediaProjection)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 103 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/media/projection/IMediaProjectionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_3
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 51 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 54	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 82	android/os/Parcel:readInt	()I
        //   65: istore_2
        //   66: iload_2
        //   67: ifeq +17 -> 84
        //   70: iconst_1
        //   71: istore_3
        //   72: aload 6
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: aload 5
        //   79: invokevirtual 57	android/os/Parcel:recycle	()V
        //   82: iload_3
        //   83: ireturn
        //   84: iconst_0
        //   85: istore_3
        //   86: goto -14 -> 72
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 57	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 57	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramIMediaProjection	IMediaProjection
        //   65	2	2	i	int
        //   71	15	3	bool	boolean
        //   1	34	4	localIBinder	IBinder
        //   6	90	5	localParcel1	Parcel
        //   11	80	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	89	finally
        //   24	32	89	finally
        //   32	66	89	finally
      }
      
      public void removeCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.projection.IMediaProjectionManager");
          if (paramIMediaProjectionWatcherCallback != null) {
            localIBinder = paramIMediaProjectionWatcherCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void stopActiveProjection()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.projection.IMediaProjectionManager");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/projection/IMediaProjectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */