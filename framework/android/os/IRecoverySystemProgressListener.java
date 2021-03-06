package android.os;

public abstract interface IRecoverySystemProgressListener
  extends IInterface
{
  public abstract void onProgress(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecoverySystemProgressListener
  {
    private static final String DESCRIPTOR = "android.os.IRecoverySystemProgressListener";
    static final int TRANSACTION_onProgress = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.IRecoverySystemProgressListener");
    }
    
    public static IRecoverySystemProgressListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IRecoverySystemProgressListener");
      if ((localIInterface != null) && ((localIInterface instanceof IRecoverySystemProgressListener))) {
        return (IRecoverySystemProgressListener)localIInterface;
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
        paramParcel2.writeString("android.os.IRecoverySystemProgressListener");
        return true;
      }
      paramParcel1.enforceInterface("android.os.IRecoverySystemProgressListener");
      onProgress(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IRecoverySystemProgressListener
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
        return "android.os.IRecoverySystemProgressListener";
      }
      
      public void onProgress(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.os.IRecoverySystemProgressListener");
          localParcel.writeInt(paramInt);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IRecoverySystemProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */