package android.os;

public abstract interface IUpdateEngine
  extends IInterface
{
  public abstract void applyPayload(String paramString, long paramLong1, long paramLong2, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract boolean bind(IUpdateEngineCallback paramIUpdateEngineCallback)
    throws RemoteException;
  
  public abstract void cancel()
    throws RemoteException;
  
  public abstract void resetStatus()
    throws RemoteException;
  
  public abstract void resume()
    throws RemoteException;
  
  public abstract void suspend()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUpdateEngine
  {
    private static final String DESCRIPTOR = "android.os.IUpdateEngine";
    static final int TRANSACTION_applyPayload = 1;
    static final int TRANSACTION_bind = 2;
    static final int TRANSACTION_cancel = 5;
    static final int TRANSACTION_resetStatus = 6;
    static final int TRANSACTION_resume = 4;
    static final int TRANSACTION_suspend = 3;
    
    public Stub()
    {
      attachInterface(this, "android.os.IUpdateEngine");
    }
    
    public static IUpdateEngine asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IUpdateEngine");
      if ((localIInterface != null) && ((localIInterface instanceof IUpdateEngine))) {
        return (IUpdateEngine)localIInterface;
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
        paramParcel2.writeString("android.os.IUpdateEngine");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IUpdateEngine");
        applyPayload(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IUpdateEngine");
        boolean bool = bind(IUpdateEngineCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.os.IUpdateEngine");
        suspend();
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.os.IUpdateEngine");
        resume();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.os.IUpdateEngine");
        cancel();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.os.IUpdateEngine");
      resetStatus();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IUpdateEngine
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void applyPayload(String paramString, long paramLong1, long paramLong2, String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUpdateEngine");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong1);
          localParcel1.writeLong(paramLong2);
          localParcel1.writeStringArray(paramArrayOfString);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public boolean bind(IUpdateEngineCallback paramIUpdateEngineCallback)
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
        //   25: invokeinterface 68 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 71	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/os/IUpdateEngine$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_2
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 53 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 56	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 75	android/os/Parcel:readInt	()I
        //   65: istore_2
        //   66: iload_2
        //   67: ifeq +17 -> 84
        //   70: iconst_1
        //   71: istore_3
        //   72: aload 6
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload 5
        //   79: invokevirtual 59	android/os/Parcel:recycle	()V
        //   82: iload_3
        //   83: ireturn
        //   84: iconst_0
        //   85: istore_3
        //   86: goto -14 -> 72
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 59	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 59	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramIUpdateEngineCallback	IUpdateEngineCallback
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
      
      public void cancel()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUpdateEngine");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IUpdateEngine";
      }
      
      public void resetStatus()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUpdateEngine");
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
      
      public void resume()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUpdateEngine");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void suspend()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUpdateEngine");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IUpdateEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */