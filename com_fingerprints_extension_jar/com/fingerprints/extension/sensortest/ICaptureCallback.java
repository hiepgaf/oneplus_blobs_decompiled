package com.fingerprints.extension.sensortest;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICaptureCallback
  extends IInterface
{
  public abstract void onAcquired(int paramInt)
    throws RemoteException;
  
  public abstract void onError(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICaptureCallback
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.sensortest.ICaptureCallback";
    static final int TRANSACTION_onAcquired = 1;
    static final int TRANSACTION_onError = 2;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.sensortest.ICaptureCallback");
    }
    
    public static ICaptureCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.sensortest.ICaptureCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ICaptureCallback))) {
        return (ICaptureCallback)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.sensortest.ICaptureCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.ICaptureCallback");
        onAcquired(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.ICaptureCallback");
      onError(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements ICaptureCallback
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
        return "com.fingerprints.extension.sensortest.ICaptureCallback";
      }
      
      public void onAcquired(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.ICaptureCallback");
          localParcel1.writeInt(paramInt);
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
      
      public void onError(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.ICaptureCallback");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/ICaptureCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */