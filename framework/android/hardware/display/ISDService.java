package android.hardware.display;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ISDService
  extends IInterface
{
  public abstract void SetUsrColorBalanceConfig(double paramDouble1, double paramDouble2, double paramDouble3)
    throws RemoteException;
  
  public abstract void SetUsrSharpness(int paramInt)
    throws RemoteException;
  
  public abstract void enableColorBalance(int paramInt)
    throws RemoteException;
  
  public abstract void enableMode(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISDService
  {
    private static final String DESCRIPTOR = "android.hardware.display.ISDService";
    static final int TRANSACTION_SetUsrColorBalanceConfig = 3;
    static final int TRANSACTION_SetUsrSharpness = 4;
    static final int TRANSACTION_enableColorBalance = 2;
    static final int TRANSACTION_enableMode = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.display.ISDService");
    }
    
    public static ISDService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.display.ISDService");
      if ((localIInterface != null) && ((localIInterface instanceof ISDService))) {
        return (ISDService)localIInterface;
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
        paramParcel2.writeString("android.hardware.display.ISDService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.display.ISDService");
        enableMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.display.ISDService");
        enableColorBalance(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.display.ISDService");
        SetUsrColorBalanceConfig(paramParcel1.readDouble(), paramParcel1.readDouble(), paramParcel1.readDouble());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.display.ISDService");
      SetUsrSharpness(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements ISDService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void SetUsrColorBalanceConfig(double paramDouble1, double paramDouble2, double paramDouble3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.ISDService");
          localParcel1.writeDouble(paramDouble1);
          localParcel1.writeDouble(paramDouble2);
          localParcel1.writeDouble(paramDouble3);
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
      
      public void SetUsrSharpness(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.ISDService");
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void enableColorBalance(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.ISDService");
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
      
      public void enableMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.ISDService");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.display.ISDService";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/ISDService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */