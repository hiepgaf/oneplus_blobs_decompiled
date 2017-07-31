package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICameraServiceListener
  extends IInterface
{
  public static final int STATUS_ENUMERATING = 2;
  public static final int STATUS_NOT_AVAILABLE = -2;
  public static final int STATUS_NOT_PRESENT = 0;
  public static final int STATUS_PRESENT = 1;
  public static final int STATUS_UNKNOWN = -1;
  public static final int TORCH_STATUS_AVAILABLE_OFF = 1;
  public static final int TORCH_STATUS_AVAILABLE_ON = 2;
  public static final int TORCH_STATUS_NOT_AVAILABLE = 0;
  public static final int TORCH_STATUS_UNKNOWN = -1;
  
  public abstract void onStatusChanged(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onTorchStatusChanged(int paramInt, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICameraServiceListener
  {
    private static final String DESCRIPTOR = "android.hardware.ICameraServiceListener";
    static final int TRANSACTION_onStatusChanged = 1;
    static final int TRANSACTION_onTorchStatusChanged = 2;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.ICameraServiceListener");
    }
    
    public static ICameraServiceListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.ICameraServiceListener");
      if ((localIInterface != null) && ((localIInterface instanceof ICameraServiceListener))) {
        return (ICameraServiceListener)localIInterface;
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
        paramParcel2.writeString("android.hardware.ICameraServiceListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.ICameraServiceListener");
        onStatusChanged(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.ICameraServiceListener");
      onTorchStatusChanged(paramParcel1.readInt(), paramParcel1.readString());
      return true;
    }
    
    private static class Proxy
      implements ICameraServiceListener
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
        return "android.hardware.ICameraServiceListener";
      }
      
      public void onStatusChanged(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.ICameraServiceListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTorchStatusChanged(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.ICameraServiceListener");
          localParcel.writeInt(paramInt);
          localParcel.writeString(paramString);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ICameraServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */