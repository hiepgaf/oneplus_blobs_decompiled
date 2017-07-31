package android.os;

public abstract interface IRecoverySystem
  extends IInterface
{
  public abstract boolean clearBcb()
    throws RemoteException;
  
  public abstract void rebootRecoveryWithCommand(String paramString)
    throws RemoteException;
  
  public abstract boolean setupBcb(String paramString)
    throws RemoteException;
  
  public abstract boolean uncrypt(String paramString, IRecoverySystemProgressListener paramIRecoverySystemProgressListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecoverySystem
  {
    private static final String DESCRIPTOR = "android.os.IRecoverySystem";
    static final int TRANSACTION_clearBcb = 3;
    static final int TRANSACTION_rebootRecoveryWithCommand = 4;
    static final int TRANSACTION_setupBcb = 2;
    static final int TRANSACTION_uncrypt = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.IRecoverySystem");
    }
    
    public static IRecoverySystem asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IRecoverySystem");
      if ((localIInterface != null) && ((localIInterface instanceof IRecoverySystem))) {
        return (IRecoverySystem)localIInterface;
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
      int j = 0;
      int k = 0;
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IRecoverySystem");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IRecoverySystem");
        bool = uncrypt(paramParcel1.readString(), IRecoverySystemProgressListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IRecoverySystem");
        bool = setupBcb(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.os.IRecoverySystem");
        bool = clearBcb();
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.os.IRecoverySystem");
      rebootRecoveryWithCommand(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IRecoverySystem
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
      public boolean clearBcb()
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
        //   16: getfield 19	android/os/IRecoverySystem$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_3
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 44 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 47	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 54	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 54	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IRecoverySystem";
      }
      
      public void rebootRecoveryWithCommand(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IRecoverySystem");
          localParcel1.writeString(paramString);
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
      
      /* Error */
      public boolean setupBcb(String paramString)
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
        //   20: invokevirtual 61	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/IRecoverySystem$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_2
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 44 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 47	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 51	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 54	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   49	2	2	i	int
        //   55	15	3	bool	boolean
        //   3	77	4	localParcel1	Parcel
        //   8	67	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	50	73	finally
      }
      
      /* Error */
      public boolean uncrypt(String paramString, IRecoverySystemProgressListener paramIRecoverySystemProgressListener)
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
        //   20: aload 6
        //   22: aload_1
        //   23: invokevirtual 61	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 5
        //   28: astore_1
        //   29: aload_2
        //   30: ifnull +10 -> 40
        //   33: aload_2
        //   34: invokeinterface 69 1 0
        //   39: astore_1
        //   40: aload 6
        //   42: aload_1
        //   43: invokevirtual 72	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   46: aload_0
        //   47: getfield 19	android/os/IRecoverySystem$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload 6
        //   53: aload 7
        //   55: iconst_0
        //   56: invokeinterface 44 5 0
        //   61: pop
        //   62: aload 7
        //   64: invokevirtual 47	android/os/Parcel:readException	()V
        //   67: aload 7
        //   69: invokevirtual 51	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: iload_3
        //   74: ifeq +19 -> 93
        //   77: iconst_1
        //   78: istore 4
        //   80: aload 7
        //   82: invokevirtual 54	android/os/Parcel:recycle	()V
        //   85: aload 6
        //   87: invokevirtual 54	android/os/Parcel:recycle	()V
        //   90: iload 4
        //   92: ireturn
        //   93: iconst_0
        //   94: istore 4
        //   96: goto -16 -> 80
        //   99: astore_1
        //   100: aload 7
        //   102: invokevirtual 54	android/os/Parcel:recycle	()V
        //   105: aload 6
        //   107: invokevirtual 54	android/os/Parcel:recycle	()V
        //   110: aload_1
        //   111: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	112	0	this	Proxy
        //   0	112	1	paramString	String
        //   0	112	2	paramIRecoverySystemProgressListener	IRecoverySystemProgressListener
        //   72	2	3	i	int
        //   78	17	4	bool	boolean
        //   1	26	5	localObject	Object
        //   6	100	6	localParcel1	Parcel
        //   11	90	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	99	finally
        //   33	40	99	finally
        //   40	73	99	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IRecoverySystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */