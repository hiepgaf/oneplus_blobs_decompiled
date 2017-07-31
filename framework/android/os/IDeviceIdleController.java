package android.os;

public abstract interface IDeviceIdleController
  extends IInterface
{
  public abstract void addPowerSaveTempWhitelistApp(String paramString1, long paramLong, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract long addPowerSaveTempWhitelistAppForMms(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract long addPowerSaveTempWhitelistAppForSms(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract void addPowerSaveWhitelistApp(String paramString)
    throws RemoteException;
  
  public abstract void exitIdle(String paramString)
    throws RemoteException;
  
  public abstract int[] getAppIdTempWhitelist()
    throws RemoteException;
  
  public abstract int[] getAppIdUserWhitelist()
    throws RemoteException;
  
  public abstract int[] getAppIdWhitelist()
    throws RemoteException;
  
  public abstract int[] getAppIdWhitelistExceptIdle()
    throws RemoteException;
  
  public abstract String[] getFullPowerWhitelist()
    throws RemoteException;
  
  public abstract String[] getFullPowerWhitelistExceptIdle()
    throws RemoteException;
  
  public abstract int getIdleStateDetailed()
    throws RemoteException;
  
  public abstract int getLightIdleStateDetailed()
    throws RemoteException;
  
  public abstract String[] getSystemPowerWhitelist()
    throws RemoteException;
  
  public abstract String[] getSystemPowerWhitelistExceptIdle()
    throws RemoteException;
  
  public abstract String[] getUserPowerWhitelist()
    throws RemoteException;
  
  public abstract boolean isPowerSaveWhitelistApp(String paramString)
    throws RemoteException;
  
  public abstract boolean isPowerSaveWhitelistExceptIdleApp(String paramString)
    throws RemoteException;
  
  public abstract boolean registerMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
    throws RemoteException;
  
  public abstract void removePowerSaveWhitelistApp(String paramString)
    throws RemoteException;
  
  public abstract void unregisterMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IDeviceIdleController
  {
    private static final String DESCRIPTOR = "android.os.IDeviceIdleController";
    static final int TRANSACTION_addPowerSaveTempWhitelistApp = 14;
    static final int TRANSACTION_addPowerSaveTempWhitelistAppForMms = 15;
    static final int TRANSACTION_addPowerSaveTempWhitelistAppForSms = 16;
    static final int TRANSACTION_addPowerSaveWhitelistApp = 1;
    static final int TRANSACTION_exitIdle = 17;
    static final int TRANSACTION_getAppIdTempWhitelist = 11;
    static final int TRANSACTION_getAppIdUserWhitelist = 10;
    static final int TRANSACTION_getAppIdWhitelist = 9;
    static final int TRANSACTION_getAppIdWhitelistExceptIdle = 8;
    static final int TRANSACTION_getFullPowerWhitelist = 7;
    static final int TRANSACTION_getFullPowerWhitelistExceptIdle = 6;
    static final int TRANSACTION_getIdleStateDetailed = 20;
    static final int TRANSACTION_getLightIdleStateDetailed = 21;
    static final int TRANSACTION_getSystemPowerWhitelist = 4;
    static final int TRANSACTION_getSystemPowerWhitelistExceptIdle = 3;
    static final int TRANSACTION_getUserPowerWhitelist = 5;
    static final int TRANSACTION_isPowerSaveWhitelistApp = 13;
    static final int TRANSACTION_isPowerSaveWhitelistExceptIdleApp = 12;
    static final int TRANSACTION_registerMaintenanceActivityListener = 18;
    static final int TRANSACTION_removePowerSaveWhitelistApp = 2;
    static final int TRANSACTION_unregisterMaintenanceActivityListener = 19;
    
    public Stub()
    {
      attachInterface(this, "android.os.IDeviceIdleController");
    }
    
    public static IDeviceIdleController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IDeviceIdleController");
      if ((localIInterface != null) && ((localIInterface instanceof IDeviceIdleController))) {
        return (IDeviceIdleController)localIInterface;
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
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IDeviceIdleController");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        addPowerSaveWhitelistApp(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        removePowerSaveWhitelistApp(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getSystemPowerWhitelistExceptIdle();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getSystemPowerWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getUserPowerWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getFullPowerWhitelistExceptIdle();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getFullPowerWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getAppIdWhitelistExceptIdle();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getAppIdWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getAppIdUserWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramParcel1 = getAppIdTempWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        bool = isPowerSaveWhitelistExceptIdleApp(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        bool = isPowerSaveWhitelistApp(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        addPowerSaveTempWhitelistApp(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        l = addPowerSaveTempWhitelistAppForMms(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        l = addPowerSaveTempWhitelistAppForSms(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        exitIdle(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        bool = registerMaintenanceActivityListener(IMaintenanceActivityListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        unregisterMaintenanceActivityListener(IMaintenanceActivityListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.os.IDeviceIdleController");
        paramInt1 = getIdleStateDetailed();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.os.IDeviceIdleController");
      paramInt1 = getLightIdleStateDetailed();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IDeviceIdleController
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addPowerSaveTempWhitelistApp(String paramString1, long paramLong, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString1);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
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
      
      public long addPowerSaveTempWhitelistAppForMms(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long addPowerSaveTempWhitelistAppForSms(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addPowerSaveWhitelistApp(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString);
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
      
      public void exitIdle(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getAppIdTempWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getAppIdUserWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getAppIdWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getAppIdWhitelistExceptIdle()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getFullPowerWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      public String[] getFullPowerWhitelistExceptIdle()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
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
      
      public int getIdleStateDetailed()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
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
        return "android.os.IDeviceIdleController";
      }
      
      public int getLightIdleStateDetailed()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
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
      
      public String[] getSystemPowerWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public String[] getSystemPowerWhitelistExceptIdle()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public String[] getUserPowerWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      public boolean isPowerSaveWhitelistApp(String paramString)
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
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/IDeviceIdleController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 13
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 53 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 56	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 90	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 59	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
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
      public boolean isPowerSaveWhitelistExceptIdleApp(String paramString)
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
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/IDeviceIdleController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 12
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 53 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 56	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 90	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 59	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
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
      public boolean registerMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
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
        //   25: invokeinterface 105 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 108	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/os/IDeviceIdleController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 18
        //   45: aload 5
        //   47: aload 6
        //   49: iconst_0
        //   50: invokeinterface 53 5 0
        //   55: pop
        //   56: aload 6
        //   58: invokevirtual 56	android/os/Parcel:readException	()V
        //   61: aload 6
        //   63: invokevirtual 90	android/os/Parcel:readInt	()I
        //   66: istore_2
        //   67: iload_2
        //   68: ifeq +17 -> 85
        //   71: iconst_1
        //   72: istore_3
        //   73: aload 6
        //   75: invokevirtual 59	android/os/Parcel:recycle	()V
        //   78: aload 5
        //   80: invokevirtual 59	android/os/Parcel:recycle	()V
        //   83: iload_3
        //   84: ireturn
        //   85: iconst_0
        //   86: istore_3
        //   87: goto -14 -> 73
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 59	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 59	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramIMaintenanceActivityListener	IMaintenanceActivityListener
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
      
      public void removePowerSaveWhitelistApp(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          localParcel1.writeString(paramString);
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
      
      public void unregisterMaintenanceActivityListener(IMaintenanceActivityListener paramIMaintenanceActivityListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IDeviceIdleController");
          if (paramIMaintenanceActivityListener != null) {
            localIBinder = paramIMaintenanceActivityListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IDeviceIdleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */