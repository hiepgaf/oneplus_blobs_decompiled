package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITaskStackListener
  extends IInterface
{
  public abstract void onActivityDismissingDockedStack()
    throws RemoteException;
  
  public abstract void onActivityForcedResizable(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onActivityPinned()
    throws RemoteException;
  
  public abstract void onPinnedActivityRestartAttempt()
    throws RemoteException;
  
  public abstract void onPinnedStackAnimationEnded()
    throws RemoteException;
  
  public abstract void onTaskStackChanged()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITaskStackListener
  {
    private static final String DESCRIPTOR = "android.app.ITaskStackListener";
    static final int TRANSACTION_onActivityDismissingDockedStack = 6;
    static final int TRANSACTION_onActivityForcedResizable = 5;
    static final int TRANSACTION_onActivityPinned = 2;
    static final int TRANSACTION_onPinnedActivityRestartAttempt = 3;
    static final int TRANSACTION_onPinnedStackAnimationEnded = 4;
    static final int TRANSACTION_onTaskStackChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.ITaskStackListener");
    }
    
    public static ITaskStackListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.ITaskStackListener");
      if ((localIInterface != null) && ((localIInterface instanceof ITaskStackListener))) {
        return (ITaskStackListener)localIInterface;
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
        paramParcel2.writeString("android.app.ITaskStackListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.ITaskStackListener");
        onTaskStackChanged();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.ITaskStackListener");
        onActivityPinned();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.ITaskStackListener");
        onPinnedActivityRestartAttempt();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.ITaskStackListener");
        onPinnedStackAnimationEnded();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.ITaskStackListener");
        onActivityForcedResizable(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.app.ITaskStackListener");
      onActivityDismissingDockedStack();
      return true;
    }
    
    private static class Proxy
      implements ITaskStackListener
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
        return "android.app.ITaskStackListener";
      }
      
      public void onActivityDismissingDockedStack()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onActivityForcedResizable(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onActivityPinned()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPinnedActivityRestartAttempt()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPinnedStackAnimationEnded()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTaskStackChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.ITaskStackListener");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ITaskStackListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */