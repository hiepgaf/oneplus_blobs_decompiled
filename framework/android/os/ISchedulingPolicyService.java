package android.os;

public abstract interface ISchedulingPolicyService
  extends IInterface
{
  public abstract int requestPriority(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISchedulingPolicyService
  {
    private static final String DESCRIPTOR = "android.os.ISchedulingPolicyService";
    static final int TRANSACTION_requestPriority = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.ISchedulingPolicyService");
    }
    
    public static ISchedulingPolicyService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.ISchedulingPolicyService");
      if ((localIInterface != null) && ((localIInterface instanceof ISchedulingPolicyService))) {
        return (ISchedulingPolicyService)localIInterface;
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
        paramParcel2.writeString("android.os.ISchedulingPolicyService");
        return true;
      }
      paramParcel1.enforceInterface("android.os.ISchedulingPolicyService");
      paramInt1 = requestPriority(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements ISchedulingPolicyService
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
        return "android.os.ISchedulingPolicyService";
      }
      
      public int requestPriority(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.ISchedulingPolicyService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ISchedulingPolicyService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */