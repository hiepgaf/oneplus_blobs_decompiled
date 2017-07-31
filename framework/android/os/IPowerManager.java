package android.os;

public abstract interface IPowerManager
  extends IInterface
{
  public abstract void acquireWakeLock(IBinder paramIBinder, int paramInt, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3)
    throws RemoteException;
  
  public abstract void acquireWakeLockWithUid(IBinder paramIBinder, int paramInt1, String paramString1, String paramString2, int paramInt2)
    throws RemoteException;
  
  public abstract void boostScreenBrightness(long paramLong)
    throws RemoteException;
  
  public abstract void crash(String paramString)
    throws RemoteException;
  
  public abstract void goToSleep(long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean isDeviceIdleMode()
    throws RemoteException;
  
  public abstract boolean isInteractive()
    throws RemoteException;
  
  public abstract boolean isLightDeviceIdleMode()
    throws RemoteException;
  
  public abstract boolean isPowerSaveMode()
    throws RemoteException;
  
  public abstract boolean isScreenBrightnessBoosted()
    throws RemoteException;
  
  public abstract boolean isWakeLockLevelSupported(int paramInt)
    throws RemoteException;
  
  public abstract void nap(long paramLong)
    throws RemoteException;
  
  public abstract void powerHint(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void reboot(boolean paramBoolean1, String paramString, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void rebootSafeMode(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void releaseWakeLock(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void setAttentionLight(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean setPowerSaveMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setStayOnSetting(int paramInt)
    throws RemoteException;
  
  public abstract void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float paramFloat)
    throws RemoteException;
  
  public abstract void setTemporaryScreenBrightnessSettingOverride(int paramInt)
    throws RemoteException;
  
  public abstract void shutdown(boolean paramBoolean1, String paramString, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void updateBlockedUids(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void updateWakeLockUids(IBinder paramIBinder, int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract void updateWakeLockWorkSource(IBinder paramIBinder, WorkSource paramWorkSource, String paramString)
    throws RemoteException;
  
  public abstract void userActivity(long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void wakeUp(long paramLong, String paramString1, String paramString2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPowerManager
  {
    private static final String DESCRIPTOR = "android.os.IPowerManager";
    static final int TRANSACTION_acquireWakeLock = 1;
    static final int TRANSACTION_acquireWakeLockWithUid = 2;
    static final int TRANSACTION_boostScreenBrightness = 22;
    static final int TRANSACTION_crash = 20;
    static final int TRANSACTION_goToSleep = 10;
    static final int TRANSACTION_isDeviceIdleMode = 15;
    static final int TRANSACTION_isInteractive = 12;
    static final int TRANSACTION_isLightDeviceIdleMode = 16;
    static final int TRANSACTION_isPowerSaveMode = 13;
    static final int TRANSACTION_isScreenBrightnessBoosted = 23;
    static final int TRANSACTION_isWakeLockLevelSupported = 7;
    static final int TRANSACTION_nap = 11;
    static final int TRANSACTION_powerHint = 5;
    static final int TRANSACTION_reboot = 17;
    static final int TRANSACTION_rebootSafeMode = 18;
    static final int TRANSACTION_releaseWakeLock = 3;
    static final int TRANSACTION_setAttentionLight = 26;
    static final int TRANSACTION_setPowerSaveMode = 14;
    static final int TRANSACTION_setStayOnSetting = 21;
    static final int TRANSACTION_setTemporaryScreenAutoBrightnessAdjustmentSettingOverride = 25;
    static final int TRANSACTION_setTemporaryScreenBrightnessSettingOverride = 24;
    static final int TRANSACTION_shutdown = 19;
    static final int TRANSACTION_updateBlockedUids = 27;
    static final int TRANSACTION_updateWakeLockUids = 4;
    static final int TRANSACTION_updateWakeLockWorkSource = 6;
    static final int TRANSACTION_userActivity = 8;
    static final int TRANSACTION_wakeUp = 9;
    
    public Stub()
    {
      attachInterface(this, "android.os.IPowerManager");
    }
    
    public static IPowerManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IPowerManager");
      if ((localIInterface != null) && ((localIInterface instanceof IPowerManager))) {
        return (IPowerManager)localIInterface;
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
      IBinder localIBinder;
      Object localObject;
      label755:
      boolean bool2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IPowerManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        localIBinder = paramParcel1.readStrongBinder();
        paramInt1 = paramParcel1.readInt();
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          acquireWakeLock(localIBinder, paramInt1, str1, str2, (WorkSource)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        acquireWakeLockWithUid(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        releaseWakeLock(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        updateWakeLockUids(paramParcel1.readStrongBinder(), paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        powerHint(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          updateWakeLockWorkSource(localIBinder, (WorkSource)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isWakeLockLevelSupported(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        userActivity(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        wakeUp(paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        goToSleep(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        nap(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isInteractive();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isPowerSaveMode();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setPowerSaveMode(bool1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label755;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 15: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isDeviceIdleMode();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isLightDeviceIdleMode();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label884;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          reboot(bool1, (String)localObject, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 18: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label936;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          rebootSafeMode(bool1, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 19: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label996;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          shutdown(bool1, (String)localObject, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 20: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        crash(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        setStayOnSetting(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        boostScreenBrightness(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        bool1 = isScreenBrightnessBoosted();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        setTemporaryScreenBrightnessSettingOverride(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.os.IPowerManager");
        setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(paramParcel1.readFloat());
        paramParcel2.writeNoException();
        return true;
      case 26: 
        label884:
        label936:
        label996:
        paramParcel1.enforceInterface("android.os.IPowerManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setAttentionLight(bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.os.IPowerManager");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        updateBlockedUids(paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IPowerManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void acquireWakeLock(IBinder paramIBinder, int paramInt, String paramString1, String paramString2, WorkSource paramWorkSource, String paramString3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload 7
        //   25: iload_2
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload 7
        //   31: aload_3
        //   32: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload 7
        //   37: aload 4
        //   39: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   42: aload 5
        //   44: ifnull +56 -> 100
        //   47: aload 7
        //   49: iconst_1
        //   50: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   53: aload 5
        //   55: aload 7
        //   57: iconst_0
        //   58: invokevirtual 52	android/os/WorkSource:writeToParcel	(Landroid/os/Parcel;I)V
        //   61: aload 7
        //   63: aload 6
        //   65: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   68: aload_0
        //   69: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   72: iconst_1
        //   73: aload 7
        //   75: aload 8
        //   77: iconst_0
        //   78: invokeinterface 58 5 0
        //   83: pop
        //   84: aload 8
        //   86: invokevirtual 61	android/os/Parcel:readException	()V
        //   89: aload 8
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload 7
        //   96: invokevirtual 64	android/os/Parcel:recycle	()V
        //   99: return
        //   100: aload 7
        //   102: iconst_0
        //   103: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   106: goto -45 -> 61
        //   109: astore_1
        //   110: aload 8
        //   112: invokevirtual 64	android/os/Parcel:recycle	()V
        //   115: aload 7
        //   117: invokevirtual 64	android/os/Parcel:recycle	()V
        //   120: aload_1
        //   121: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	122	0	this	Proxy
        //   0	122	1	paramIBinder	IBinder
        //   0	122	2	paramInt	int
        //   0	122	3	paramString1	String
        //   0	122	4	paramString2	String
        //   0	122	5	paramWorkSource	WorkSource
        //   0	122	6	paramString3	String
        //   3	113	7	localParcel1	Parcel
        //   8	103	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	42	109	finally
        //   47	61	109	finally
        //   61	89	109	finally
        //   100	106	109	finally
      }
      
      public void acquireWakeLockWithUid(IBinder paramIBinder, int paramInt1, String paramString1, String paramString2, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt2);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void boostScreenBrightness(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void crash(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
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
        return "android.os.IPowerManager";
      }
      
      public void goToSleep(long paramLong, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      public boolean isDeviceIdleMode()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 15
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 85	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isInteractive()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 12
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 85	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isLightDeviceIdleMode()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 16
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 85	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isPowerSaveMode()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 13
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 85	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isScreenBrightnessBoosted()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 23
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 85	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isWakeLockLevelSupported(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 7
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 85	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 64	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void nap(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void powerHint(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.os.IPowerManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void reboot(boolean paramBoolean1, String paramString, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: iload_1
        //   21: ifeq +67 -> 88
        //   24: iconst_1
        //   25: istore 4
        //   27: aload 6
        //   29: iload 4
        //   31: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   34: aload 6
        //   36: aload_2
        //   37: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: iload_3
        //   41: ifeq +53 -> 94
        //   44: iload 5
        //   46: istore 4
        //   48: aload 6
        //   50: iload 4
        //   52: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   55: aload_0
        //   56: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 17
        //   61: aload 6
        //   63: aload 7
        //   65: iconst_0
        //   66: invokeinterface 58 5 0
        //   71: pop
        //   72: aload 7
        //   74: invokevirtual 61	android/os/Parcel:readException	()V
        //   77: aload 7
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload 6
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: return
        //   88: iconst_0
        //   89: istore 4
        //   91: goto -64 -> 27
        //   94: iconst_0
        //   95: istore 4
        //   97: goto -49 -> 48
        //   100: astore_2
        //   101: aload 7
        //   103: invokevirtual 64	android/os/Parcel:recycle	()V
        //   106: aload 6
        //   108: invokevirtual 64	android/os/Parcel:recycle	()V
        //   111: aload_2
        //   112: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	113	0	this	Proxy
        //   0	113	1	paramBoolean1	boolean
        //   0	113	2	paramString	String
        //   0	113	3	paramBoolean2	boolean
        //   25	71	4	i	int
        //   1	44	5	j	int
        //   6	101	6	localParcel1	Parcel
        //   11	91	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	100	finally
        //   27	40	100	finally
        //   48	77	100	finally
      }
      
      /* Error */
      public void rebootSafeMode(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: iload_1
        //   21: ifeq +57 -> 78
        //   24: iconst_1
        //   25: istore_3
        //   26: aload 5
        //   28: iload_3
        //   29: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   32: iload_2
        //   33: ifeq +50 -> 83
        //   36: iload 4
        //   38: istore_3
        //   39: aload 5
        //   41: iload_3
        //   42: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   45: aload_0
        //   46: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 18
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 58 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 61	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 64	android/os/Parcel:recycle	()V
        //   72: aload 5
        //   74: invokevirtual 64	android/os/Parcel:recycle	()V
        //   77: return
        //   78: iconst_0
        //   79: istore_3
        //   80: goto -54 -> 26
        //   83: iconst_0
        //   84: istore_3
        //   85: goto -46 -> 39
        //   88: astore 7
        //   90: aload 6
        //   92: invokevirtual 64	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 64	android/os/Parcel:recycle	()V
        //   100: aload 7
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramBoolean1	boolean
        //   0	103	2	paramBoolean2	boolean
        //   25	60	3	i	int
        //   1	36	4	j	int
        //   6	90	5	localParcel1	Parcel
        //   11	80	6	localParcel2	Parcel
        //   88	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   13	20	88	finally
        //   26	32	88	finally
        //   39	67	88	finally
      }
      
      public void releaseWakeLock(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public void setAttentionLight(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
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
      public boolean setPowerSaveMode(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 14
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 58 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 61	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 85	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 64	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 64	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      public void setStayOnSetting(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeFloat(paramFloat);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setTemporaryScreenBrightnessSettingOverride(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
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
      public void shutdown(boolean paramBoolean1, String paramString, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: iload_1
        //   21: ifeq +67 -> 88
        //   24: iconst_1
        //   25: istore 4
        //   27: aload 6
        //   29: iload 4
        //   31: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   34: aload 6
        //   36: aload_2
        //   37: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: iload_3
        //   41: ifeq +53 -> 94
        //   44: iload 5
        //   46: istore 4
        //   48: aload 6
        //   50: iload 4
        //   52: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   55: aload_0
        //   56: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 19
        //   61: aload 6
        //   63: aload 7
        //   65: iconst_0
        //   66: invokeinterface 58 5 0
        //   71: pop
        //   72: aload 7
        //   74: invokevirtual 61	android/os/Parcel:readException	()V
        //   77: aload 7
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload 6
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: return
        //   88: iconst_0
        //   89: istore 4
        //   91: goto -64 -> 27
        //   94: iconst_0
        //   95: istore 4
        //   97: goto -49 -> 48
        //   100: astore_2
        //   101: aload 7
        //   103: invokevirtual 64	android/os/Parcel:recycle	()V
        //   106: aload 6
        //   108: invokevirtual 64	android/os/Parcel:recycle	()V
        //   111: aload_2
        //   112: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	113	0	this	Proxy
        //   0	113	1	paramBoolean1	boolean
        //   0	113	2	paramString	String
        //   0	113	3	paramBoolean2	boolean
        //   25	71	4	i	int
        //   1	44	5	j	int
        //   6	101	6	localParcel1	Parcel
        //   11	91	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	100	finally
        //   27	40	100	finally
        //   48	77	100	finally
      }
      
      public void updateBlockedUids(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void updateWakeLockUids(IBinder paramIBinder, int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeIntArray(paramArrayOfInt);
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
      public void updateWakeLockWorkSource(IBinder paramIBinder, WorkSource paramWorkSource, String paramString)
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
        //   20: invokevirtual 39	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 52	android/os/WorkSource:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 46	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/os/IPowerManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 6
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 58 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 64	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 64	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramWorkSource	WorkSource
        //   0	101	3	paramString	String
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void userActivity(long paramLong, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void wakeUp(long paramLong, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IPowerManager");
          localParcel1.writeLong(paramLong);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IPowerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */