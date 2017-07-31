package android.location;

import android.hardware.location.GeofenceHardwareRequestParcelable;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFusedGeofenceHardware
  extends IInterface
{
  public abstract void addGeofences(GeofenceHardwareRequestParcelable[] paramArrayOfGeofenceHardwareRequestParcelable)
    throws RemoteException;
  
  public abstract boolean isSupported()
    throws RemoteException;
  
  public abstract void modifyGeofenceOptions(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    throws RemoteException;
  
  public abstract void pauseMonitoringGeofence(int paramInt)
    throws RemoteException;
  
  public abstract void removeGeofences(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract void resumeMonitoringGeofence(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFusedGeofenceHardware
  {
    private static final String DESCRIPTOR = "android.location.IFusedGeofenceHardware";
    static final int TRANSACTION_addGeofences = 2;
    static final int TRANSACTION_isSupported = 1;
    static final int TRANSACTION_modifyGeofenceOptions = 6;
    static final int TRANSACTION_pauseMonitoringGeofence = 4;
    static final int TRANSACTION_removeGeofences = 3;
    static final int TRANSACTION_resumeMonitoringGeofence = 5;
    
    public Stub()
    {
      attachInterface(this, "android.location.IFusedGeofenceHardware");
    }
    
    public static IFusedGeofenceHardware asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.IFusedGeofenceHardware");
      if ((localIInterface != null) && ((localIInterface instanceof IFusedGeofenceHardware))) {
        return (IFusedGeofenceHardware)localIInterface;
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
        paramParcel2.writeString("android.location.IFusedGeofenceHardware");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
        boolean bool = isSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
        addGeofences((GeofenceHardwareRequestParcelable[])paramParcel1.createTypedArray(GeofenceHardwareRequestParcelable.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
        removeGeofences(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
        pauseMonitoringGeofence(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
        resumeMonitoringGeofence(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.location.IFusedGeofenceHardware");
      modifyGeofenceOptions(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFusedGeofenceHardware
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addGeofences(GeofenceHardwareRequestParcelable[] paramArrayOfGeofenceHardwareRequestParcelable)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedGeofenceHardware");
          localParcel1.writeTypedArray(paramArrayOfGeofenceHardwareRequestParcelable, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.location.IFusedGeofenceHardware";
      }
      
      /* Error */
      public boolean isSupported()
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
        //   16: getfield 19	android/location/IFusedGeofenceHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 46 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 49	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 63	android/os/Parcel:readInt	()I
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
      
      public void modifyGeofenceOptions(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedGeofenceHardware");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeInt(paramInt5);
          localParcel1.writeInt(paramInt6);
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
      
      public void pauseMonitoringGeofence(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedGeofenceHardware");
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
      
      public void removeGeofences(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedGeofenceHardware");
          localParcel1.writeIntArray(paramArrayOfInt);
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
      
      public void resumeMonitoringGeofence(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.location.IFusedGeofenceHardware");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/IFusedGeofenceHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */