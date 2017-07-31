package android.hardware.location;

import android.location.IFusedGeofenceHardware;
import android.location.IFusedGeofenceHardware.Stub;
import android.location.IGpsGeofenceHardware;
import android.location.IGpsGeofenceHardware.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGeofenceHardware
  extends IInterface
{
  public abstract boolean addCircularFence(int paramInt, GeofenceHardwareRequestParcelable paramGeofenceHardwareRequestParcelable, IGeofenceHardwareCallback paramIGeofenceHardwareCallback)
    throws RemoteException;
  
  public abstract int[] getMonitoringTypes()
    throws RemoteException;
  
  public abstract int getStatusOfMonitoringType(int paramInt)
    throws RemoteException;
  
  public abstract boolean pauseGeofence(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean registerForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
    throws RemoteException;
  
  public abstract boolean removeGeofence(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean resumeGeofence(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setFusedGeofenceHardware(IFusedGeofenceHardware paramIFusedGeofenceHardware)
    throws RemoteException;
  
  public abstract void setGpsGeofenceHardware(IGpsGeofenceHardware paramIGpsGeofenceHardware)
    throws RemoteException;
  
  public abstract boolean unregisterForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGeofenceHardware
  {
    private static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardware";
    static final int TRANSACTION_addCircularFence = 5;
    static final int TRANSACTION_getMonitoringTypes = 3;
    static final int TRANSACTION_getStatusOfMonitoringType = 4;
    static final int TRANSACTION_pauseGeofence = 7;
    static final int TRANSACTION_registerForMonitorStateChangeCallback = 9;
    static final int TRANSACTION_removeGeofence = 6;
    static final int TRANSACTION_resumeGeofence = 8;
    static final int TRANSACTION_setFusedGeofenceHardware = 2;
    static final int TRANSACTION_setGpsGeofenceHardware = 1;
    static final int TRANSACTION_unregisterForMonitorStateChangeCallback = 10;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IGeofenceHardware");
    }
    
    public static IGeofenceHardware asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IGeofenceHardware");
      if ((localIInterface != null) && ((localIInterface instanceof IGeofenceHardware))) {
        return (IGeofenceHardware)localIInterface;
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
        paramParcel2.writeString("android.hardware.location.IGeofenceHardware");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        setGpsGeofenceHardware(IGpsGeofenceHardware.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        setFusedGeofenceHardware(IFusedGeofenceHardware.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        paramParcel1 = getMonitoringTypes();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        paramInt1 = getStatusOfMonitoringType(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        paramInt1 = paramParcel1.readInt();
        GeofenceHardwareRequestParcelable localGeofenceHardwareRequestParcelable;
        if (paramParcel1.readInt() != 0)
        {
          localGeofenceHardwareRequestParcelable = (GeofenceHardwareRequestParcelable)GeofenceHardwareRequestParcelable.CREATOR.createFromParcel(paramParcel1);
          bool = addCircularFence(paramInt1, localGeofenceHardwareRequestParcelable, IGeofenceHardwareCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          if (!bool) {
            break label284;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localGeofenceHardwareRequestParcelable = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        bool = removeGeofence(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        bool = pauseGeofence(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        bool = resumeGeofence(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        label284:
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
        bool = registerForMonitorStateChangeCallback(paramParcel1.readInt(), IGeofenceHardwareMonitorCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardware");
      boolean bool = unregisterForMonitorStateChangeCallback(paramParcel1.readInt(), IGeofenceHardwareMonitorCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IGeofenceHardware
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public boolean addCircularFence(int paramInt, GeofenceHardwareRequestParcelable paramGeofenceHardwareRequestParcelable, IGeofenceHardwareCallback paramIGeofenceHardwareCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.location.IGeofenceHardware");
            localParcel1.writeInt(paramInt);
            if (paramGeofenceHardwareRequestParcelable != null)
            {
              localParcel1.writeInt(1);
              paramGeofenceHardwareRequestParcelable.writeToParcel(localParcel1, 0);
              paramGeofenceHardwareRequestParcelable = (GeofenceHardwareRequestParcelable)localObject;
              if (paramIGeofenceHardwareCallback != null) {
                paramGeofenceHardwareRequestParcelable = paramIGeofenceHardwareCallback.asBinder();
              }
              localParcel1.writeStrongBinder(paramGeofenceHardwareRequestParcelable);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.location.IGeofenceHardware";
      }
      
      public int[] getMonitoringTypes()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IGeofenceHardware");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public int getStatusOfMonitoringType(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IGeofenceHardware");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean pauseGeofence(int paramInt1, int paramInt2)
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
        //   30: getfield 19	android/hardware/location/IGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 7
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 61 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 64	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 68	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 71	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 71	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 71	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 71	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean registerForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
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
        //   20: aload 5
        //   22: iload_1
        //   23: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   26: aload_2
        //   27: ifnull +11 -> 38
        //   30: aload_2
        //   31: invokeinterface 88 1 0
        //   36: astore 4
        //   38: aload 5
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   45: aload_0
        //   46: getfield 19	android/hardware/location/IGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 9
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 61 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 64	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 68	android/os/Parcel:readInt	()I
        //   72: istore_1
        //   73: iload_1
        //   74: ifeq +17 -> 91
        //   77: iconst_1
        //   78: istore_3
        //   79: aload 6
        //   81: invokevirtual 71	android/os/Parcel:recycle	()V
        //   84: aload 5
        //   86: invokevirtual 71	android/os/Parcel:recycle	()V
        //   89: iload_3
        //   90: ireturn
        //   91: iconst_0
        //   92: istore_3
        //   93: goto -14 -> 79
        //   96: astore_2
        //   97: aload 6
        //   99: invokevirtual 71	android/os/Parcel:recycle	()V
        //   102: aload 5
        //   104: invokevirtual 71	android/os/Parcel:recycle	()V
        //   107: aload_2
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramInt	int
        //   0	109	2	paramIGeofenceHardwareMonitorCallback	IGeofenceHardwareMonitorCallback
        //   78	15	3	bool	boolean
        //   1	40	4	localIBinder	IBinder
        //   6	97	5	localParcel1	Parcel
        //   11	87	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	96	finally
        //   30	38	96	finally
        //   38	73	96	finally
      }
      
      /* Error */
      public boolean removeGeofence(int paramInt1, int paramInt2)
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
        //   30: getfield 19	android/hardware/location/IGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 61 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 64	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 68	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 71	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 71	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 71	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 71	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean resumeGeofence(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 8
        //   41: aload 5
        //   43: aload 6
        //   45: iconst_0
        //   46: invokeinterface 61 5 0
        //   51: pop
        //   52: aload 6
        //   54: invokevirtual 64	android/os/Parcel:readException	()V
        //   57: aload 6
        //   59: invokevirtual 68	android/os/Parcel:readInt	()I
        //   62: istore_1
        //   63: iload_1
        //   64: ifeq +19 -> 83
        //   67: iconst_1
        //   68: istore 4
        //   70: aload 6
        //   72: invokevirtual 71	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: invokevirtual 71	android/os/Parcel:recycle	()V
        //   80: iload 4
        //   82: ireturn
        //   83: iconst_0
        //   84: istore 4
        //   86: goto -16 -> 70
        //   89: astore 7
        //   91: aload 6
        //   93: invokevirtual 71	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 71	android/os/Parcel:recycle	()V
        //   101: aload 7
        //   103: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	104	0	this	Proxy
        //   0	104	1	paramInt1	int
        //   0	104	2	paramInt2	int
        //   0	104	3	paramInt3	int
        //   68	17	4	bool	boolean
        //   3	94	5	localParcel1	Parcel
        //   8	84	6	localParcel2	Parcel
        //   89	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	63	89	finally
      }
      
      public void setFusedGeofenceHardware(IFusedGeofenceHardware paramIFusedGeofenceHardware)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IGeofenceHardware");
          if (paramIFusedGeofenceHardware != null) {
            localIBinder = paramIFusedGeofenceHardware.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void setGpsGeofenceHardware(IGpsGeofenceHardware paramIGpsGeofenceHardware)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IGeofenceHardware");
          if (paramIGpsGeofenceHardware != null) {
            localIBinder = paramIGpsGeofenceHardware.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      /* Error */
      public boolean unregisterForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
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
        //   20: aload 5
        //   22: iload_1
        //   23: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   26: aload_2
        //   27: ifnull +11 -> 38
        //   30: aload_2
        //   31: invokeinterface 88 1 0
        //   36: astore 4
        //   38: aload 5
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   45: aload_0
        //   46: getfield 19	android/hardware/location/IGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 10
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 61 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 64	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 68	android/os/Parcel:readInt	()I
        //   72: istore_1
        //   73: iload_1
        //   74: ifeq +17 -> 91
        //   77: iconst_1
        //   78: istore_3
        //   79: aload 6
        //   81: invokevirtual 71	android/os/Parcel:recycle	()V
        //   84: aload 5
        //   86: invokevirtual 71	android/os/Parcel:recycle	()V
        //   89: iload_3
        //   90: ireturn
        //   91: iconst_0
        //   92: istore_3
        //   93: goto -14 -> 79
        //   96: astore_2
        //   97: aload 6
        //   99: invokevirtual 71	android/os/Parcel:recycle	()V
        //   102: aload 5
        //   104: invokevirtual 71	android/os/Parcel:recycle	()V
        //   107: aload_2
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramInt	int
        //   0	109	2	paramIGeofenceHardwareMonitorCallback	IGeofenceHardwareMonitorCallback
        //   78	15	3	bool	boolean
        //   1	40	4	localIBinder	IBinder
        //   6	97	5	localParcel1	Parcel
        //   11	87	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	96	finally
        //   30	38	96	finally
        //   38	73	96	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IGeofenceHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */