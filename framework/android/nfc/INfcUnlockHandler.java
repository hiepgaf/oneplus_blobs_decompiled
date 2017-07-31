package android.nfc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INfcUnlockHandler
  extends IInterface
{
  public abstract boolean onUnlockAttempted(Tag paramTag)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcUnlockHandler
  {
    private static final String DESCRIPTOR = "android.nfc.INfcUnlockHandler";
    static final int TRANSACTION_onUnlockAttempted = 1;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcUnlockHandler");
    }
    
    public static INfcUnlockHandler asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcUnlockHandler");
      if ((localIInterface != null) && ((localIInterface instanceof INfcUnlockHandler))) {
        return (INfcUnlockHandler)localIInterface;
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
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.nfc.INfcUnlockHandler");
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.INfcUnlockHandler");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Tag)Tag.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        boolean bool = onUnlockAttempted(paramParcel1);
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements INfcUnlockHandler
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
        return "android.nfc.INfcUnlockHandler";
      }
      
      public boolean onUnlockAttempted(Tag paramTag)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcUnlockHandler");
            if (paramTag != null)
            {
              localParcel1.writeInt(1);
              paramTag.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcUnlockHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */