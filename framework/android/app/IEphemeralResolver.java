package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IEphemeralResolver
  extends IInterface
{
  public abstract void getEphemeralResolveInfoList(IRemoteCallback paramIRemoteCallback, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IEphemeralResolver
  {
    private static final String DESCRIPTOR = "android.app.IEphemeralResolver";
    static final int TRANSACTION_getEphemeralResolveInfoList = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IEphemeralResolver");
    }
    
    public static IEphemeralResolver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IEphemeralResolver");
      if ((localIInterface != null) && ((localIInterface instanceof IEphemeralResolver))) {
        return (IEphemeralResolver)localIInterface;
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
        paramParcel2.writeString("android.app.IEphemeralResolver");
        return true;
      }
      paramParcel1.enforceInterface("android.app.IEphemeralResolver");
      getEphemeralResolveInfoList(IRemoteCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createIntArray(), paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IEphemeralResolver
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
      
      public void getEphemeralResolveInfoList(IRemoteCallback paramIRemoteCallback, int[] paramArrayOfInt, int paramInt1, int paramInt2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IEphemeralResolver");
          if (paramIRemoteCallback != null) {
            localIBinder = paramIRemoteCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeIntArray(paramArrayOfInt);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IEphemeralResolver";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IEphemeralResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */