package android.os;

public abstract interface IPermissionController
  extends IInterface
{
  public abstract boolean checkPermission(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String[] getPackagesForUid(int paramInt)
    throws RemoteException;
  
  public abstract boolean isRuntimePermission(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPermissionController
  {
    private static final String DESCRIPTOR = "android.os.IPermissionController";
    static final int TRANSACTION_checkPermission = 1;
    static final int TRANSACTION_getPackagesForUid = 2;
    static final int TRANSACTION_isRuntimePermission = 3;
    
    public Stub()
    {
      attachInterface(this, "android.os.IPermissionController");
    }
    
    public static IPermissionController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IPermissionController");
      if ((localIInterface != null) && ((localIInterface instanceof IPermissionController))) {
        return (IPermissionController)localIInterface;
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
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IPermissionController");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IPermissionController");
        bool = checkPermission(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IPermissionController");
        paramParcel1 = getPackagesForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.os.IPermissionController");
      boolean bool = isRuntimePermission(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramInt1 = j;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IPermissionController
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
      public boolean checkPermission(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/os/IPermissionController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload 5
        //   42: aload 6
        //   44: iconst_0
        //   45: invokeinterface 51 5 0
        //   50: pop
        //   51: aload 6
        //   53: invokevirtual 54	android/os/Parcel:readException	()V
        //   56: aload 6
        //   58: invokevirtual 58	android/os/Parcel:readInt	()I
        //   61: istore_2
        //   62: iload_2
        //   63: ifeq +19 -> 82
        //   66: iconst_1
        //   67: istore 4
        //   69: aload 6
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: iload 4
        //   81: ireturn
        //   82: iconst_0
        //   83: istore 4
        //   85: goto -16 -> 69
        //   88: astore_1
        //   89: aload 6
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload 5
        //   96: invokevirtual 61	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString	String
        //   0	101	2	paramInt1	int
        //   0	101	3	paramInt2	int
        //   67	17	4	bool	boolean
        //   3	92	5	localParcel1	Parcel
        //   8	82	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	62	88	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IPermissionController";
      }
      
      public String[] getPackagesForUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPermissionController");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isRuntimePermission(String paramString)
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
        //   24: getfield 19	android/os/IPermissionController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_3
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 51 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 54	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 58	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IPermissionController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */