package com.fingerprints.extension.authenticator;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFingerprintAuthenticator
  extends IInterface
{
  public abstract void cancel()
    throws RemoteException;
  
  public abstract int isUserValid(long paramLong)
    throws RemoteException;
  
  public abstract int verifyUser(IVerifyUserCallback paramIVerifyUserCallback, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintAuthenticator
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.authenticator.IFingerprintAuthenticator";
    static final int TRANSACTION_cancel = 3;
    static final int TRANSACTION_isUserValid = 2;
    static final int TRANSACTION_verifyUser = 1;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
    }
    
    public static IFingerprintAuthenticator asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintAuthenticator))) {
        return (IFingerprintAuthenticator)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
        paramInt1 = verifyUser(IVerifyUserCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createByteArray(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
        paramInt1 = isUserValid(paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
      cancel();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFingerprintAuthenticator
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
      
      public void cancel()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
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
      
      public String getInterfaceDescriptor()
      {
        return "com.fingerprints.extension.authenticator.IFingerprintAuthenticator";
      }
      
      public int isUserValid(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      
      public int verifyUser(IVerifyUserCallback paramIVerifyUserCallback, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
          if (paramIVerifyUserCallback != null) {
            localIBinder = paramIVerifyUserCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeByteArray(paramArrayOfByte1);
          localParcel1.writeByteArray(paramArrayOfByte2);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/authenticator/IFingerprintAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */