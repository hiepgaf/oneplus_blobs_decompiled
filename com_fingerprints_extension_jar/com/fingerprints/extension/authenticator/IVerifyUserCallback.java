package com.fingerprints.extension.authenticator;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IVerifyUserCallback
  extends IInterface
{
  public abstract void onHelp(int paramInt)
    throws RemoteException;
  
  public abstract void onResult(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IVerifyUserCallback
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.authenticator.IVerifyUserCallback";
    static final int TRANSACTION_onHelp = 2;
    static final int TRANSACTION_onResult = 1;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.authenticator.IVerifyUserCallback");
    }
    
    public static IVerifyUserCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.authenticator.IVerifyUserCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IVerifyUserCallback))) {
        return (IVerifyUserCallback)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.authenticator.IVerifyUserCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.authenticator.IVerifyUserCallback");
        onResult(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.authenticator.IVerifyUserCallback");
      onHelp(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IVerifyUserCallback
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
        return "com.fingerprints.extension.authenticator.IVerifyUserCallback";
      }
      
      public void onHelp(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.authenticator.IVerifyUserCallback");
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
      
      public void onResult(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.authenticator.IVerifyUserCallback");
          localParcel1.writeInt(paramInt);
          localParcel1.writeLong(paramLong1);
          localParcel1.writeLong(paramLong2);
          localParcel1.writeByteArray(paramArrayOfByte);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/authenticator/IVerifyUserCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */