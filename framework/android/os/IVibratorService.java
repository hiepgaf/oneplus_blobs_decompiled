package android.os;

public abstract interface IVibratorService
  extends IInterface
{
  public abstract void cancelVibrate(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean hasVibrator()
    throws RemoteException;
  
  public abstract void vibrate(int paramInt1, String paramString, long paramLong, int paramInt2, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void vibratePattern(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, int paramInt3, IBinder paramIBinder)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IVibratorService
  {
    private static final String DESCRIPTOR = "android.os.IVibratorService";
    static final int TRANSACTION_cancelVibrate = 4;
    static final int TRANSACTION_hasVibrator = 1;
    static final int TRANSACTION_vibrate = 2;
    static final int TRANSACTION_vibratePattern = 3;
    
    public Stub()
    {
      attachInterface(this, "android.os.IVibratorService");
    }
    
    public static IVibratorService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IVibratorService");
      if ((localIInterface != null) && ((localIInterface instanceof IVibratorService))) {
        return (IVibratorService)localIInterface;
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
        paramParcel2.writeString("android.os.IVibratorService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IVibratorService");
        boolean bool = hasVibrator();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.os.IVibratorService");
        vibrate(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.os.IVibratorService");
        vibratePattern(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.createLongArray(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.os.IVibratorService");
      cancelVibrate(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IVibratorService
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
      
      public void cancelVibrate(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IVibratorService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IVibratorService";
      }
      
      /* Error */
      public boolean hasVibrator()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IVibratorService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 46 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 49	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 61	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public void vibrate(int paramInt1, String paramString, long paramLong, int paramInt2, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IVibratorService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void vibratePattern(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, int paramInt3, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IVibratorService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeLongArray(paramArrayOfLong);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeStrongBinder(paramIBinder);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IVibratorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */