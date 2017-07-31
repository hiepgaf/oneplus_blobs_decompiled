package android.hardware.location;

import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGeofenceHardwareCallback
  extends IInterface
{
  public abstract void onGeofenceAdd(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onGeofencePause(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onGeofenceRemove(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onGeofenceResume(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onGeofenceTransition(int paramInt1, int paramInt2, Location paramLocation, long paramLong, int paramInt3)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGeofenceHardwareCallback
  {
    private static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardwareCallback";
    static final int TRANSACTION_onGeofenceAdd = 2;
    static final int TRANSACTION_onGeofencePause = 4;
    static final int TRANSACTION_onGeofenceRemove = 3;
    static final int TRANSACTION_onGeofenceResume = 5;
    static final int TRANSACTION_onGeofenceTransition = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IGeofenceHardwareCallback");
    }
    
    public static IGeofenceHardwareCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IGeofenceHardwareCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IGeofenceHardwareCallback))) {
        return (IGeofenceHardwareCallback)localIInterface;
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
        paramParcel2.writeString("android.hardware.location.IGeofenceHardwareCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardwareCallback");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Location)Location.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onGeofenceTransition(paramInt1, paramInt2, paramParcel2, paramParcel1.readLong(), paramParcel1.readInt());
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardwareCallback");
        onGeofenceAdd(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardwareCallback");
        onGeofenceRemove(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardwareCallback");
        onGeofencePause(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.location.IGeofenceHardwareCallback");
      onGeofenceResume(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IGeofenceHardwareCallback
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
        return "android.hardware.location.IGeofenceHardwareCallback";
      }
      
      public void onGeofenceAdd(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.location.IGeofenceHardwareCallback");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onGeofencePause(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.location.IGeofenceHardwareCallback");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onGeofenceRemove(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.location.IGeofenceHardwareCallback");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onGeofenceResume(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.location.IGeofenceHardwareCallback");
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
      public void onGeofenceTransition(int paramInt1, int paramInt2, Location paramLocation, long paramLong, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: aload 7
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 7
        //   14: iload_1
        //   15: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   18: aload 7
        //   20: iload_2
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_3
        //   25: ifnull +51 -> 76
        //   28: aload 7
        //   30: iconst_1
        //   31: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   34: aload_3
        //   35: aload 7
        //   37: iconst_0
        //   38: invokevirtual 65	android/location/Location:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload 7
        //   43: lload 4
        //   45: invokevirtual 69	android/os/Parcel:writeLong	(J)V
        //   48: aload 7
        //   50: iload 6
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: aload_0
        //   56: getfield 19	android/hardware/location/IGeofenceHardwareCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: iconst_1
        //   60: aload 7
        //   62: aconst_null
        //   63: iconst_1
        //   64: invokeinterface 50 5 0
        //   69: pop
        //   70: aload 7
        //   72: invokevirtual 53	android/os/Parcel:recycle	()V
        //   75: return
        //   76: aload 7
        //   78: iconst_0
        //   79: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   82: goto -41 -> 41
        //   85: astore_3
        //   86: aload 7
        //   88: invokevirtual 53	android/os/Parcel:recycle	()V
        //   91: aload_3
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt1	int
        //   0	93	2	paramInt2	int
        //   0	93	3	paramLocation	Location
        //   0	93	4	paramLong	long
        //   0	93	6	paramInt3	int
        //   3	84	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	24	85	finally
        //   28	41	85	finally
        //   41	70	85	finally
        //   76	82	85	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IGeofenceHardwareCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */