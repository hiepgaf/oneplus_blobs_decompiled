package android.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGpsGeofenceHardware
  extends IInterface
{
  public abstract boolean addCircularHardwareGeofence(int paramInt1, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws RemoteException;
  
  public abstract boolean isHardwareGeofenceSupported()
    throws RemoteException;
  
  public abstract boolean pauseHardwareGeofence(int paramInt)
    throws RemoteException;
  
  public abstract boolean removeHardwareGeofence(int paramInt)
    throws RemoteException;
  
  public abstract boolean resumeHardwareGeofence(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGpsGeofenceHardware
  {
    private static final String DESCRIPTOR = "android.location.IGpsGeofenceHardware";
    static final int TRANSACTION_addCircularHardwareGeofence = 2;
    static final int TRANSACTION_isHardwareGeofenceSupported = 1;
    static final int TRANSACTION_pauseHardwareGeofence = 4;
    static final int TRANSACTION_removeHardwareGeofence = 3;
    static final int TRANSACTION_resumeHardwareGeofence = 5;
    
    public Stub()
    {
      attachInterface(this, "android.location.IGpsGeofenceHardware");
    }
    
    public static IGpsGeofenceHardware asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IGpsGeofenceHardware");
      if ((localIInterface != null) && ((localIInterface instanceof IGpsGeofenceHardware))) {
        return (IGpsGeofenceHardware)localIInterface;
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
        paramParcel2.writeString("android.location.IGpsGeofenceHardware");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.IGpsGeofenceHardware");
        bool = isHardwareGeofenceSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.location.IGpsGeofenceHardware");
        bool = addCircularHardwareGeofence(paramParcel1.readInt(), paramParcel1.readDouble(), paramParcel1.readDouble(), paramParcel1.readDouble(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.location.IGpsGeofenceHardware");
        bool = removeHardwareGeofence(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.location.IGpsGeofenceHardware");
        bool = pauseHardwareGeofence(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.location.IGpsGeofenceHardware");
      boolean bool = resumeHardwareGeofence(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IGpsGeofenceHardware
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public boolean addCircularHardwareGeofence(int paramInt1, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 13
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 14
        //   10: aload 13
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 13
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 13
        //   25: dload_2
        //   26: invokevirtual 44	android/os/Parcel:writeDouble	(D)V
        //   29: aload 13
        //   31: dload 4
        //   33: invokevirtual 44	android/os/Parcel:writeDouble	(D)V
        //   36: aload 13
        //   38: dload 6
        //   40: invokevirtual 44	android/os/Parcel:writeDouble	(D)V
        //   43: aload 13
        //   45: iload 8
        //   47: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   50: aload 13
        //   52: iload 9
        //   54: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   57: aload 13
        //   59: iload 10
        //   61: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   64: aload 13
        //   66: iload 11
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: aload_0
        //   72: getfield 19	android/location/IGpsGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   75: iconst_2
        //   76: aload 13
        //   78: aload 14
        //   80: iconst_0
        //   81: invokeinterface 50 5 0
        //   86: pop
        //   87: aload 14
        //   89: invokevirtual 53	android/os/Parcel:readException	()V
        //   92: aload 14
        //   94: invokevirtual 57	android/os/Parcel:readInt	()I
        //   97: istore_1
        //   98: iload_1
        //   99: ifeq +19 -> 118
        //   102: iconst_1
        //   103: istore 12
        //   105: aload 14
        //   107: invokevirtual 60	android/os/Parcel:recycle	()V
        //   110: aload 13
        //   112: invokevirtual 60	android/os/Parcel:recycle	()V
        //   115: iload 12
        //   117: ireturn
        //   118: iconst_0
        //   119: istore 12
        //   121: goto -16 -> 105
        //   124: astore 15
        //   126: aload 14
        //   128: invokevirtual 60	android/os/Parcel:recycle	()V
        //   131: aload 13
        //   133: invokevirtual 60	android/os/Parcel:recycle	()V
        //   136: aload 15
        //   138: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	139	0	this	Proxy
        //   0	139	1	paramInt1	int
        //   0	139	2	paramDouble1	double
        //   0	139	4	paramDouble2	double
        //   0	139	6	paramDouble3	double
        //   0	139	8	paramInt2	int
        //   0	139	9	paramInt3	int
        //   0	139	10	paramInt4	int
        //   0	139	11	paramInt5	int
        //   103	17	12	bool	boolean
        //   3	129	13	localParcel1	Parcel
        //   8	119	14	localParcel2	Parcel
        //   124	13	15	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	98	124	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.location.IGpsGeofenceHardware";
      }
      
      /* Error */
      public boolean isHardwareGeofenceSupported()
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
        //   16: getfield 19	android/location/IGpsGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 50 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 53	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 57	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 60	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 60	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 60	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean pauseHardwareGeofence(int paramInt)
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
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/location/IGpsGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_4
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 50 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 53	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 57	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 60	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 60	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 60	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      /* Error */
      public boolean removeHardwareGeofence(int paramInt)
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
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/location/IGpsGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_3
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 50 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 53	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 57	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 60	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 60	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 60	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      /* Error */
      public boolean resumeHardwareGeofence(int paramInt1, int paramInt2)
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
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/location/IGpsGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_5
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 50 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 53	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 57	android/os/Parcel:readInt	()I
        //   55: istore_1
        //   56: iload_1
        //   57: ifeq +17 -> 74
        //   60: iconst_1
        //   61: istore_3
        //   62: aload 5
        //   64: invokevirtual 60	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 60	android/os/Parcel:recycle	()V
        //   72: iload_3
        //   73: ireturn
        //   74: iconst_0
        //   75: istore_3
        //   76: goto -14 -> 62
        //   79: astore 6
        //   81: aload 5
        //   83: invokevirtual 60	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 60	android/os/Parcel:recycle	()V
        //   91: aload 6
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramInt1	int
        //   0	94	2	paramInt2	int
        //   61	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        //   79	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	56	79	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IGpsGeofenceHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */