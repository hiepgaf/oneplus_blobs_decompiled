package android.app;

import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IInstrumentationWatcher
  extends IInterface
{
  public abstract void instrumentationFinished(ComponentName paramComponentName, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void instrumentationStatus(ComponentName paramComponentName, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IInstrumentationWatcher
  {
    private static final String DESCRIPTOR = "android.app.IInstrumentationWatcher";
    static final int TRANSACTION_instrumentationFinished = 2;
    static final int TRANSACTION_instrumentationStatus = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IInstrumentationWatcher");
    }
    
    public static IInstrumentationWatcher asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IInstrumentationWatcher");
      if ((localIInterface != null) && ((localIInterface instanceof IInstrumentationWatcher))) {
        return (IInstrumentationWatcher)localIInterface;
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
      ComponentName localComponentName;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IInstrumentationWatcher");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IInstrumentationWatcher");
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label126;
          }
        }
        label126:
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          instrumentationStatus(localComponentName, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localComponentName = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.app.IInstrumentationWatcher");
      if (paramParcel1.readInt() != 0)
      {
        localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label203;
        }
      }
      label203:
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        instrumentationFinished(localComponentName, paramInt1, paramParcel1);
        paramParcel2.writeNoException();
        return true;
        localComponentName = null;
        break;
      }
    }
    
    private static class Proxy
      implements IInstrumentationWatcher
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
        return "android.app.IInstrumentationWatcher";
      }
      
      public void instrumentationFinished(ComponentName paramComponentName, int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IInstrumentationWatcher");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void instrumentationStatus(ComponentName paramComponentName, int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IInstrumentationWatcher");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IInstrumentationWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */