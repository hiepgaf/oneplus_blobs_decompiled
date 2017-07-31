package com.fingerprints.extension.engineering;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IImageInjectionCallback
  extends IInterface
{
  public abstract void onCancel()
    throws RemoteException;
  
  public abstract byte[] onInject()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IImageInjectionCallback
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.engineering.IImageInjectionCallback";
    static final int TRANSACTION_onCancel = 2;
    static final int TRANSACTION_onInject = 1;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.engineering.IImageInjectionCallback");
    }
    
    public static IImageInjectionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.engineering.IImageInjectionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IImageInjectionCallback))) {
        return (IImageInjectionCallback)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.engineering.IImageInjectionCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IImageInjectionCallback");
        paramParcel1 = onInject();
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IImageInjectionCallback");
      onCancel();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IImageInjectionCallback
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
        return "com.fingerprints.extension.engineering.IImageInjectionCallback";
      }
      
      public void onCancel()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IImageInjectionCallback");
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
      
      public byte[] onInject()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IImageInjectionCallback");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/engineering/IImageInjectionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */