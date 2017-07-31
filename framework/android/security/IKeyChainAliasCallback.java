package android.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IKeyChainAliasCallback
  extends IInterface
{
  public abstract void alias(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IKeyChainAliasCallback
  {
    private static final String DESCRIPTOR = "android.security.IKeyChainAliasCallback";
    static final int TRANSACTION_alias = 1;
    
    public Stub()
    {
      attachInterface(this, "android.security.IKeyChainAliasCallback");
    }
    
    public static IKeyChainAliasCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.security.IKeyChainAliasCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IKeyChainAliasCallback))) {
        return (IKeyChainAliasCallback)localIInterface;
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
        paramParcel2.writeString("android.security.IKeyChainAliasCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.security.IKeyChainAliasCallback");
      alias(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IKeyChainAliasCallback
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void alias(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainAliasCallback");
          localParcel1.writeString(paramString);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.security.IKeyChainAliasCallback";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/IKeyChainAliasCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */