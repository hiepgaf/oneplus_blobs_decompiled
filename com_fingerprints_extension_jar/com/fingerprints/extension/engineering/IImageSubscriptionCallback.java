package com.fingerprints.extension.engineering;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IImageSubscriptionCallback
  extends IInterface
{
  public abstract void onImage(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IImageSubscriptionCallback
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.engineering.IImageSubscriptionCallback";
    static final int TRANSACTION_onImage = 1;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.engineering.IImageSubscriptionCallback");
    }
    
    public static IImageSubscriptionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.engineering.IImageSubscriptionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IImageSubscriptionCallback))) {
        return (IImageSubscriptionCallback)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.engineering.IImageSubscriptionCallback");
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IImageSubscriptionCallback");
      onImage(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IImageSubscriptionCallback
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
        return "com.fingerprints.extension.engineering.IImageSubscriptionCallback";
      }
      
      public void onImage(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IImageSubscriptionCallback");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/engineering/IImageSubscriptionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */