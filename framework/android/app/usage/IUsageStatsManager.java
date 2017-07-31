package android.app.usage;

import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IUsageStatsManager
  extends IInterface
{
  public abstract boolean isAppInactive(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onCarrierPrivilegedAppsChanged()
    throws RemoteException;
  
  public abstract ParceledListSlice queryConfigurationStats(int paramInt, long paramLong1, long paramLong2, String paramString)
    throws RemoteException;
  
  public abstract UsageEvents queryEvents(long paramLong1, long paramLong2, String paramString)
    throws RemoteException;
  
  public abstract ParceledListSlice queryUsageStats(int paramInt, long paramLong1, long paramLong2, String paramString)
    throws RemoteException;
  
  public abstract void setAppInactive(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void whitelistAppTemporarily(String paramString, long paramLong, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUsageStatsManager
  {
    private static final String DESCRIPTOR = "android.app.usage.IUsageStatsManager";
    static final int TRANSACTION_isAppInactive = 5;
    static final int TRANSACTION_onCarrierPrivilegedAppsChanged = 7;
    static final int TRANSACTION_queryConfigurationStats = 2;
    static final int TRANSACTION_queryEvents = 3;
    static final int TRANSACTION_queryUsageStats = 1;
    static final int TRANSACTION_setAppInactive = 4;
    static final int TRANSACTION_whitelistAppTemporarily = 6;
    
    public Stub()
    {
      attachInterface(this, "android.app.usage.IUsageStatsManager");
    }
    
    public static IUsageStatsManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.usage.IUsageStatsManager");
      if ((localIInterface != null) && ((localIInterface instanceof IUsageStatsManager))) {
        return (IUsageStatsManager)localIInterface;
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
        paramParcel2.writeString("android.app.usage.IUsageStatsManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        paramParcel1 = queryUsageStats(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.readString());
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
      case 2: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        paramParcel1 = queryConfigurationStats(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.readString());
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
      case 3: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        paramParcel1 = queryEvents(paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.readString());
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
      case 4: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setAppInactive(str, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        bool = isAppInactive(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
        whitelistAppTemporarily(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.app.usage.IUsageStatsManager");
      onCarrierPrivilegedAppsChanged();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IUsageStatsManager
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
      
      public String getInterfaceDescriptor()
      {
        return "android.app.usage.IUsageStatsManager";
      }
      
      /* Error */
      public boolean isAppInactive(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/usage/IUsageStatsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_5
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 53 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 56	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 60	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: iload_2
        //   57: ifeq +17 -> 74
        //   60: iconst_1
        //   61: istore_3
        //   62: aload 5
        //   64: invokevirtual 63	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 63	android/os/Parcel:recycle	()V
        //   72: iload_3
        //   73: ireturn
        //   74: iconst_0
        //   75: istore_3
        //   76: goto -14 -> 62
        //   79: astore_1
        //   80: aload 5
        //   82: invokevirtual 63	android/os/Parcel:recycle	()V
        //   85: aload 4
        //   87: invokevirtual 63	android/os/Parcel:recycle	()V
        //   90: aload_1
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramString	String
        //   0	92	2	paramInt	int
        //   61	15	3	bool	boolean
        //   3	83	4	localParcel1	Parcel
        //   8	73	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	56	79	finally
      }
      
      public void onCarrierPrivilegedAppsChanged()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.usage.IUsageStatsManager");
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
      
      /* Error */
      public ParceledListSlice queryConfigurationStats(int paramInt, long paramLong1, long paramLong2, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: iload_1
        //   20: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   23: aload 7
        //   25: lload_2
        //   26: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   29: aload 7
        //   31: lload 4
        //   33: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   36: aload 7
        //   38: aload 6
        //   40: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   43: aload_0
        //   44: getfield 19	android/app/usage/IUsageStatsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   47: iconst_2
        //   48: aload 7
        //   50: aload 8
        //   52: iconst_0
        //   53: invokeinterface 53 5 0
        //   58: pop
        //   59: aload 8
        //   61: invokevirtual 56	android/os/Parcel:readException	()V
        //   64: aload 8
        //   66: invokevirtual 60	android/os/Parcel:readInt	()I
        //   69: ifeq +31 -> 100
        //   72: getstatic 77	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   75: aload 8
        //   77: invokeinterface 83 2 0
        //   82: checkcast 73	android/content/pm/ParceledListSlice
        //   85: astore 6
        //   87: aload 8
        //   89: invokevirtual 63	android/os/Parcel:recycle	()V
        //   92: aload 7
        //   94: invokevirtual 63	android/os/Parcel:recycle	()V
        //   97: aload 6
        //   99: areturn
        //   100: aconst_null
        //   101: astore 6
        //   103: goto -16 -> 87
        //   106: astore 6
        //   108: aload 8
        //   110: invokevirtual 63	android/os/Parcel:recycle	()V
        //   113: aload 7
        //   115: invokevirtual 63	android/os/Parcel:recycle	()V
        //   118: aload 6
        //   120: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	121	0	this	Proxy
        //   0	121	1	paramInt	int
        //   0	121	2	paramLong1	long
        //   0	121	4	paramLong2	long
        //   0	121	6	paramString	String
        //   3	111	7	localParcel1	Parcel
        //   8	101	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	87	106	finally
      }
      
      /* Error */
      public UsageEvents queryEvents(long paramLong1, long paramLong2, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: lload_1
        //   20: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   23: aload 6
        //   25: lload_3
        //   26: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   29: aload 6
        //   31: aload 5
        //   33: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   36: aload_0
        //   37: getfield 19	android/app/usage/IUsageStatsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   40: iconst_3
        //   41: aload 6
        //   43: aload 7
        //   45: iconst_0
        //   46: invokeinterface 53 5 0
        //   51: pop
        //   52: aload 7
        //   54: invokevirtual 56	android/os/Parcel:readException	()V
        //   57: aload 7
        //   59: invokevirtual 60	android/os/Parcel:readInt	()I
        //   62: ifeq +31 -> 93
        //   65: getstatic 90	android/app/usage/UsageEvents:CREATOR	Landroid/os/Parcelable$Creator;
        //   68: aload 7
        //   70: invokeinterface 93 2 0
        //   75: checkcast 87	android/app/usage/UsageEvents
        //   78: astore 5
        //   80: aload 7
        //   82: invokevirtual 63	android/os/Parcel:recycle	()V
        //   85: aload 6
        //   87: invokevirtual 63	android/os/Parcel:recycle	()V
        //   90: aload 5
        //   92: areturn
        //   93: aconst_null
        //   94: astore 5
        //   96: goto -16 -> 80
        //   99: astore 5
        //   101: aload 7
        //   103: invokevirtual 63	android/os/Parcel:recycle	()V
        //   106: aload 6
        //   108: invokevirtual 63	android/os/Parcel:recycle	()V
        //   111: aload 5
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	Proxy
        //   0	114	1	paramLong1	long
        //   0	114	3	paramLong2	long
        //   0	114	5	paramString	String
        //   3	104	6	localParcel1	Parcel
        //   8	94	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	80	99	finally
      }
      
      /* Error */
      public ParceledListSlice queryUsageStats(int paramInt, long paramLong1, long paramLong2, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: iload_1
        //   20: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   23: aload 7
        //   25: lload_2
        //   26: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   29: aload 7
        //   31: lload 4
        //   33: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   36: aload 7
        //   38: aload 6
        //   40: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   43: aload_0
        //   44: getfield 19	android/app/usage/IUsageStatsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   47: iconst_1
        //   48: aload 7
        //   50: aload 8
        //   52: iconst_0
        //   53: invokeinterface 53 5 0
        //   58: pop
        //   59: aload 8
        //   61: invokevirtual 56	android/os/Parcel:readException	()V
        //   64: aload 8
        //   66: invokevirtual 60	android/os/Parcel:readInt	()I
        //   69: ifeq +31 -> 100
        //   72: getstatic 77	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   75: aload 8
        //   77: invokeinterface 83 2 0
        //   82: checkcast 73	android/content/pm/ParceledListSlice
        //   85: astore 6
        //   87: aload 8
        //   89: invokevirtual 63	android/os/Parcel:recycle	()V
        //   92: aload 7
        //   94: invokevirtual 63	android/os/Parcel:recycle	()V
        //   97: aload 6
        //   99: areturn
        //   100: aconst_null
        //   101: astore 6
        //   103: goto -16 -> 87
        //   106: astore 6
        //   108: aload 8
        //   110: invokevirtual 63	android/os/Parcel:recycle	()V
        //   113: aload 7
        //   115: invokevirtual 63	android/os/Parcel:recycle	()V
        //   118: aload 6
        //   120: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	121	0	this	Proxy
        //   0	121	1	paramInt	int
        //   0	121	2	paramLong1	long
        //   0	121	4	paramLong2	long
        //   0	121	6	paramString	String
        //   3	111	7	localParcel1	Parcel
        //   8	101	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	87	106	finally
      }
      
      public void setAppInactive(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.usage.IUsageStatsManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
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
      
      public void whitelistAppTemporarily(String paramString, long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.usage.IUsageStatsManager");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/IUsageStatsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */