package android.os;

public abstract interface IBatteryPropertiesRegistrar
  extends IInterface
{
  public abstract int getProperty(int paramInt, BatteryProperty paramBatteryProperty)
    throws RemoteException;
  
  public abstract void registerListener(IBatteryPropertiesListener paramIBatteryPropertiesListener)
    throws RemoteException;
  
  public abstract void unregisterListener(IBatteryPropertiesListener paramIBatteryPropertiesListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBatteryPropertiesRegistrar
  {
    private static final String DESCRIPTOR = "android.os.IBatteryPropertiesRegistrar";
    static final int TRANSACTION_getProperty = 3;
    static final int TRANSACTION_registerListener = 1;
    static final int TRANSACTION_unregisterListener = 2;
    
    public Stub()
    {
      attachInterface(this, "android.os.IBatteryPropertiesRegistrar");
    }
    
    public static IBatteryPropertiesRegistrar asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IBatteryPropertiesRegistrar");
      if ((localIInterface != null) && ((localIInterface instanceof IBatteryPropertiesRegistrar))) {
        return (IBatteryPropertiesRegistrar)localIInterface;
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
        paramParcel2.writeString("android.os.IBatteryPropertiesRegistrar");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IBatteryPropertiesRegistrar");
        registerListener(IBatteryPropertiesListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IBatteryPropertiesRegistrar");
        unregisterListener(IBatteryPropertiesListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.os.IBatteryPropertiesRegistrar");
      paramInt1 = paramParcel1.readInt();
      paramParcel1 = new BatteryProperty();
      paramInt1 = getProperty(paramInt1, paramParcel1);
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements IBatteryPropertiesRegistrar
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
        return "android.os.IBatteryPropertiesRegistrar";
      }
      
      public int getProperty(int paramInt, BatteryProperty paramBatteryProperty)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IBatteryPropertiesRegistrar");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (localParcel2.readInt() != 0) {
            paramBatteryProperty.readFromParcel(localParcel2);
          }
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerListener(IBatteryPropertiesListener paramIBatteryPropertiesListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IBatteryPropertiesRegistrar");
          if (paramIBatteryPropertiesListener != null) {
            localIBinder = paramIBatteryPropertiesListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void unregisterListener(IBatteryPropertiesListener paramIBatteryPropertiesListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IBatteryPropertiesRegistrar");
          if (paramIBatteryPropertiesListener != null) {
            localIBinder = paramIBatteryPropertiesListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IBatteryPropertiesRegistrar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */