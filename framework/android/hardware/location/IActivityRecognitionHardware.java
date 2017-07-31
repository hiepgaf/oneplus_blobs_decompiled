package android.hardware.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IActivityRecognitionHardware
  extends IInterface
{
  public abstract boolean disableActivityEvent(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean enableActivityEvent(String paramString, int paramInt, long paramLong)
    throws RemoteException;
  
  public abstract boolean flush()
    throws RemoteException;
  
  public abstract String[] getSupportedActivities()
    throws RemoteException;
  
  public abstract boolean isActivitySupported(String paramString)
    throws RemoteException;
  
  public abstract boolean registerSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
    throws RemoteException;
  
  public abstract boolean unregisterSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IActivityRecognitionHardware
  {
    private static final String DESCRIPTOR = "android.hardware.location.IActivityRecognitionHardware";
    static final int TRANSACTION_disableActivityEvent = 6;
    static final int TRANSACTION_enableActivityEvent = 5;
    static final int TRANSACTION_flush = 7;
    static final int TRANSACTION_getSupportedActivities = 1;
    static final int TRANSACTION_isActivitySupported = 2;
    static final int TRANSACTION_registerSink = 3;
    static final int TRANSACTION_unregisterSink = 4;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IActivityRecognitionHardware");
    }
    
    public static IActivityRecognitionHardware asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IActivityRecognitionHardware");
      if ((localIInterface != null) && ((localIInterface instanceof IActivityRecognitionHardware))) {
        return (IActivityRecognitionHardware)localIInterface;
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
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.location.IActivityRecognitionHardware");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        paramParcel1 = getSupportedActivities();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        bool = isActivitySupported(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        bool = registerSink(IActivityRecognitionHardwareSink.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        bool = unregisterSink(IActivityRecognitionHardwareSink.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        bool = enableActivityEvent(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramInt1 = m;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
        bool = disableActivityEvent(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = n;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.location.IActivityRecognitionHardware");
      boolean bool = flush();
      paramParcel2.writeNoException();
      paramInt1 = i1;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IActivityRecognitionHardware
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
      public boolean disableActivityEvent(String paramString, int paramInt)
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
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean enableActivityEvent(String paramString, int paramInt, long paramLong)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload 6
        //   31: lload_3
        //   32: invokevirtual 68	android/os/Parcel:writeLong	(J)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_5
        //   40: aload 6
        //   42: aload 7
        //   44: iconst_0
        //   45: invokeinterface 51 5 0
        //   50: pop
        //   51: aload 7
        //   53: invokevirtual 54	android/os/Parcel:readException	()V
        //   56: aload 7
        //   58: invokevirtual 58	android/os/Parcel:readInt	()I
        //   61: istore_2
        //   62: iload_2
        //   63: ifeq +19 -> 82
        //   66: iconst_1
        //   67: istore 5
        //   69: aload 7
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload 6
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: iload 5
        //   81: ireturn
        //   82: iconst_0
        //   83: istore 5
        //   85: goto -16 -> 69
        //   88: astore_1
        //   89: aload 7
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload 6
        //   96: invokevirtual 61	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString	String
        //   0	101	2	paramInt	int
        //   0	101	3	paramLong	long
        //   67	17	5	bool	boolean
        //   3	92	6	localParcel1	Parcel
        //   8	82	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	62	88	finally
      }
      
      /* Error */
      public boolean flush()
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
        //   16: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 51 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 54	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.location.IActivityRecognitionHardware";
      }
      
      public String[] getSupportedActivities()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IActivityRecognitionHardware");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      public boolean isActivitySupported(String paramString)
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
        //   24: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_2
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
      
      /* Error */
      public boolean registerSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
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
        //   25: invokeinterface 85 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 88	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_3
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 51 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 54	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 58	android/os/Parcel:readInt	()I
        //   65: istore_2
        //   66: iload_2
        //   67: ifeq +17 -> 84
        //   70: iconst_1
        //   71: istore_3
        //   72: aload 6
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload 5
        //   79: invokevirtual 61	android/os/Parcel:recycle	()V
        //   82: iload_3
        //   83: ireturn
        //   84: iconst_0
        //   85: istore_3
        //   86: goto -14 -> 72
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 61	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 61	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramIActivityRecognitionHardwareSink	IActivityRecognitionHardwareSink
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
      
      /* Error */
      public boolean unregisterSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
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
        //   25: invokeinterface 85 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 88	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_0
        //   40: getfield 19	android/hardware/location/IActivityRecognitionHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_4
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 51 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 54	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 58	android/os/Parcel:readInt	()I
        //   65: istore_2
        //   66: iload_2
        //   67: ifeq +17 -> 84
        //   70: iconst_1
        //   71: istore_3
        //   72: aload 6
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload 5
        //   79: invokevirtual 61	android/os/Parcel:recycle	()V
        //   82: iload_3
        //   83: ireturn
        //   84: iconst_0
        //   85: istore_3
        //   86: goto -14 -> 72
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 61	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 61	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramIActivityRecognitionHardwareSink	IActivityRecognitionHardwareSink
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IActivityRecognitionHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */