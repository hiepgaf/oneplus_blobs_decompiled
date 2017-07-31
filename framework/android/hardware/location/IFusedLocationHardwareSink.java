package android.hardware.location;

import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFusedLocationHardwareSink
  extends IInterface
{
  public abstract void onCapabilities(int paramInt)
    throws RemoteException;
  
  public abstract void onDiagnosticDataAvailable(String paramString)
    throws RemoteException;
  
  public abstract void onLocationAvailable(Location[] paramArrayOfLocation)
    throws RemoteException;
  
  public abstract void onStatusChanged(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFusedLocationHardwareSink
  {
    private static final String DESCRIPTOR = "android.hardware.location.IFusedLocationHardwareSink";
    static final int TRANSACTION_onCapabilities = 3;
    static final int TRANSACTION_onDiagnosticDataAvailable = 2;
    static final int TRANSACTION_onLocationAvailable = 1;
    static final int TRANSACTION_onStatusChanged = 4;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IFusedLocationHardwareSink");
    }
    
    public static IFusedLocationHardwareSink asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IFusedLocationHardwareSink");
      if ((localIInterface != null) && ((localIInterface instanceof IFusedLocationHardwareSink))) {
        return (IFusedLocationHardwareSink)localIInterface;
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
        paramParcel2.writeString("android.hardware.location.IFusedLocationHardwareSink");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardwareSink");
        onLocationAvailable((Location[])paramParcel1.createTypedArray(Location.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardwareSink");
        onDiagnosticDataAvailable(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardwareSink");
        onCapabilities(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardwareSink");
      onStatusChanged(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFusedLocationHardwareSink
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
        return "android.hardware.location.IFusedLocationHardwareSink";
      }
      
      public void onCapabilities(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardwareSink");
          localParcel1.writeInt(paramInt);
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
      
      public void onDiagnosticDataAvailable(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardwareSink");
          localParcel1.writeString(paramString);
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
      
      public void onLocationAvailable(Location[] paramArrayOfLocation)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardwareSink");
          localParcel1.writeTypedArray(paramArrayOfLocation, 0);
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
      
      public void onStatusChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardwareSink");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IFusedLocationHardwareSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */