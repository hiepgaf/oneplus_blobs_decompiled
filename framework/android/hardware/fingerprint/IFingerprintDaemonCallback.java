package android.hardware.fingerprint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFingerprintDaemonCallback
  extends IInterface
{
  public abstract void onAcquired(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onAuthenticated(long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onEnrollResult(long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void onEnumerate(long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws RemoteException;
  
  public abstract void onError(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onRemoved(long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintDaemonCallback
  {
    private static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintDaemonCallback";
    static final int TRANSACTION_onAcquired = 2;
    static final int TRANSACTION_onAuthenticated = 3;
    static final int TRANSACTION_onEnrollResult = 1;
    static final int TRANSACTION_onEnumerate = 6;
    static final int TRANSACTION_onError = 4;
    static final int TRANSACTION_onRemoved = 5;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.fingerprint.IFingerprintDaemonCallback");
    }
    
    public static IFingerprintDaemonCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintDaemonCallback))) {
        return (IFingerprintDaemonCallback)localIInterface;
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
        paramParcel2.writeString("android.hardware.fingerprint.IFingerprintDaemonCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
        onEnrollResult(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
        onAcquired(paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
        onAuthenticated(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
        onError(paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
        onRemoved(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemonCallback");
      onEnumerate(paramParcel1.readLong(), paramParcel1.createIntArray(), paramParcel1.createIntArray());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFingerprintDaemonCallback
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
        return "android.hardware.fingerprint.IFingerprintDaemonCallback";
      }
      
      public void onAcquired(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt);
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
      
      public void onAuthenticated(long paramLong, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void onEnrollResult(long paramLong, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public void onEnumerate(long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
          localParcel1.writeIntArray(paramArrayOfInt1);
          localParcel1.writeIntArray(paramArrayOfInt2);
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
      
      public void onError(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
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
      
      public void onRemoved(long paramLong, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemonCallback");
          localParcel1.writeLong(paramLong);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/IFingerprintDaemonCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */