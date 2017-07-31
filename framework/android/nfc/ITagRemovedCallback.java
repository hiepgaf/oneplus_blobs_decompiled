package android.nfc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITagRemovedCallback
  extends IInterface
{
  public abstract void onTagRemoved()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITagRemovedCallback
  {
    private static final String DESCRIPTOR = "android.nfc.ITagRemovedCallback";
    static final int TRANSACTION_onTagRemoved = 1;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.ITagRemovedCallback");
    }
    
    public static ITagRemovedCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.ITagRemovedCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ITagRemovedCallback))) {
        return (ITagRemovedCallback)localIInterface;
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
        paramParcel2.writeString("android.nfc.ITagRemovedCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.ITagRemovedCallback");
      onTagRemoved();
      return true;
    }
    
    private static class Proxy
      implements ITagRemovedCallback
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
        return "android.nfc.ITagRemovedCallback";
      }
      
      public void onTagRemoved()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.nfc.ITagRemovedCallback");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/ITagRemovedCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */