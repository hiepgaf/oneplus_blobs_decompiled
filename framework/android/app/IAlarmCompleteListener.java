package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IAlarmCompleteListener
  extends IInterface
{
  public abstract void alarmComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAlarmCompleteListener
  {
    private static final String DESCRIPTOR = "android.app.IAlarmCompleteListener";
    static final int TRANSACTION_alarmComplete = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IAlarmCompleteListener");
    }
    
    public static IAlarmCompleteListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IAlarmCompleteListener");
      if ((localIInterface != null) && ((localIInterface instanceof IAlarmCompleteListener))) {
        return (IAlarmCompleteListener)localIInterface;
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
        paramParcel2.writeString("android.app.IAlarmCompleteListener");
        return true;
      }
      paramParcel1.enforceInterface("android.app.IAlarmCompleteListener");
      alarmComplete(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IAlarmCompleteListener
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void alarmComplete(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmCompleteListener");
          localParcel1.writeStrongBinder(paramIBinder);
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
        return "android.app.IAlarmCompleteListener";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IAlarmCompleteListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */