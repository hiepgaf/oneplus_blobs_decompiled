package android.os;

public abstract interface IUpdateEngineCallback
  extends IInterface
{
  public abstract void onPayloadApplicationComplete(int paramInt)
    throws RemoteException;
  
  public abstract void onStatusUpdate(int paramInt, float paramFloat)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUpdateEngineCallback
  {
    private static final String DESCRIPTOR = "android.os.IUpdateEngineCallback";
    static final int TRANSACTION_onPayloadApplicationComplete = 2;
    static final int TRANSACTION_onStatusUpdate = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.IUpdateEngineCallback");
    }
    
    public static IUpdateEngineCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IUpdateEngineCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IUpdateEngineCallback))) {
        return (IUpdateEngineCallback)localIInterface;
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
        paramParcel2.writeString("android.os.IUpdateEngineCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IUpdateEngineCallback");
        onStatusUpdate(paramParcel1.readInt(), paramParcel1.readFloat());
        return true;
      }
      paramParcel1.enforceInterface("android.os.IUpdateEngineCallback");
      onPayloadApplicationComplete(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IUpdateEngineCallback
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
        return "android.os.IUpdateEngineCallback";
      }
      
      public void onPayloadApplicationComplete(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.os.IUpdateEngineCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStatusUpdate(int paramInt, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.os.IUpdateEngineCallback");
          localParcel.writeInt(paramInt);
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(1, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IUpdateEngineCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */