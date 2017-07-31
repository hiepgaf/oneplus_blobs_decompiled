package android.hardware.display;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IColorBalanceManager
  extends IInterface
{
  public abstract void sendMsg(int paramInt)
    throws RemoteException;
  
  public abstract void setActiveMode(int paramInt)
    throws RemoteException;
  
  public abstract void setColorBalance(int paramInt)
    throws RemoteException;
  
  public abstract void setDefaultMode(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IColorBalanceManager
  {
    private static final String DESCRIPTOR = "android.hardware.display.IColorBalanceManager";
    static final int TRANSACTION_sendMsg = 1;
    static final int TRANSACTION_setActiveMode = 2;
    static final int TRANSACTION_setColorBalance = 4;
    static final int TRANSACTION_setDefaultMode = 3;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.display.IColorBalanceManager");
    }
    
    public static IColorBalanceManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.display.IColorBalanceManager");
      if ((localIInterface != null) && ((localIInterface instanceof IColorBalanceManager))) {
        return (IColorBalanceManager)localIInterface;
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
        paramParcel2.writeString("android.hardware.display.IColorBalanceManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.display.IColorBalanceManager");
        sendMsg(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.display.IColorBalanceManager");
        setActiveMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.display.IColorBalanceManager");
        setDefaultMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.display.IColorBalanceManager");
      setColorBalance(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IColorBalanceManager
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
        return "android.hardware.display.IColorBalanceManager";
      }
      
      public void sendMsg(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IColorBalanceManager");
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
      
      public void setActiveMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IColorBalanceManager");
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
      
      public void setColorBalance(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IColorBalanceManager");
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
      
      public void setDefaultMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IColorBalanceManager");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/IColorBalanceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */