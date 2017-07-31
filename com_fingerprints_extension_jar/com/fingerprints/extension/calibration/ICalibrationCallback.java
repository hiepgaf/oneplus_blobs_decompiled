package com.fingerprints.extension.calibration;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICalibrationCallback
  extends IInterface
{
  public abstract void onError(int paramInt)
    throws RemoteException;
  
  public abstract void onStatus(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICalibrationCallback
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.calibration.ICalibrationCallback";
    static final int TRANSACTION_onError = 2;
    static final int TRANSACTION_onStatus = 1;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.calibration.ICalibrationCallback");
    }
    
    public static ICalibrationCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.calibration.ICalibrationCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ICalibrationCallback))) {
        return (ICalibrationCallback)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.calibration.ICalibrationCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.calibration.ICalibrationCallback");
        onStatus(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.calibration.ICalibrationCallback");
      onError(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements ICalibrationCallback
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
        return "com.fingerprints.extension.calibration.ICalibrationCallback";
      }
      
      public void onError(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.calibration.ICalibrationCallback");
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
      
      public void onStatus(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.calibration.ICalibrationCallback");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/calibration/ICalibrationCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */