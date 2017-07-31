package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IAlarmListener
  extends IInterface
{
  public abstract void doAlarm(IAlarmCompleteListener paramIAlarmCompleteListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAlarmListener
  {
    private static final String DESCRIPTOR = "android.app.IAlarmListener";
    static final int TRANSACTION_doAlarm = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IAlarmListener");
    }
    
    public static IAlarmListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IAlarmListener");
      if ((localIInterface != null) && ((localIInterface instanceof IAlarmListener))) {
        return (IAlarmListener)localIInterface;
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
        paramParcel2.writeString("android.app.IAlarmListener");
        return true;
      }
      paramParcel1.enforceInterface("android.app.IAlarmListener");
      doAlarm(IAlarmCompleteListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IAlarmListener
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
      
      public void doAlarm(IAlarmCompleteListener paramIAlarmCompleteListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IAlarmListener");
          if (paramIAlarmCompleteListener != null) {
            localIBinder = paramIAlarmCompleteListener.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
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
        return "android.app.IAlarmListener";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IAlarmListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */