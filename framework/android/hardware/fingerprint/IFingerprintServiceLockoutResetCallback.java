package android.hardware.fingerprint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFingerprintServiceLockoutResetCallback
  extends IInterface
{
  public abstract void onLockoutReset(long paramLong)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintServiceLockoutResetCallback
  {
    private static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback";
    static final int TRANSACTION_onLockoutReset = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback");
    }
    
    public static IFingerprintServiceLockoutResetCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintServiceLockoutResetCallback))) {
        return (IFingerprintServiceLockoutResetCallback)localIInterface;
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
        paramParcel2.writeString("android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback");
      onLockoutReset(paramParcel1.readLong());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFingerprintServiceLockoutResetCallback
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
        return "android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback";
      }
      
      public void onLockoutReset(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback");
          localParcel1.writeLong(paramLong);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/IFingerprintServiceLockoutResetCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */