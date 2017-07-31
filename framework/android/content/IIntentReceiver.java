package android.content;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IIntentReceiver
  extends IInterface
{
  public abstract void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IIntentReceiver
  {
    private static final String DESCRIPTOR = "android.content.IIntentReceiver";
    static final int TRANSACTION_performReceive = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.IIntentReceiver");
    }
    
    public static IIntentReceiver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.IIntentReceiver");
      if ((localIInterface != null) && ((localIInterface instanceof IIntentReceiver))) {
        return (IIntentReceiver)localIInterface;
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
        paramParcel2.writeString("android.content.IIntentReceiver");
        return true;
      }
      paramParcel1.enforceInterface("android.content.IIntentReceiver");
      String str;
      Bundle localBundle;
      label104:
      boolean bool1;
      if (paramParcel1.readInt() != 0)
      {
        paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
        paramInt1 = paramParcel1.readInt();
        str = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {
          break label149;
        }
        localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label155;
        }
        bool1 = true;
        label114:
        if (paramParcel1.readInt() == 0) {
          break label161;
        }
      }
      label149:
      label155:
      label161:
      for (boolean bool2 = true;; bool2 = false)
      {
        performReceive(paramParcel2, paramInt1, str, localBundle, bool1, bool2, paramParcel1.readInt());
        return true;
        paramParcel2 = null;
        break;
        localBundle = null;
        break label104;
        bool1 = false;
        break label114;
      }
    }
    
    private static class Proxy
      implements IIntentReceiver
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
        return "android.content.IIntentReceiver";
      }
      
      public void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.IIntentReceiver");
            if (paramIntent != null)
            {
              localParcel.writeInt(1);
              paramIntent.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt1);
              localParcel.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                break label150;
                localParcel.writeInt(paramInt1);
                if (!paramBoolean2) {
                  break label145;
                }
                paramInt1 = i;
                label80:
                localParcel.writeInt(paramInt1);
                localParcel.writeInt(paramInt2);
                this.mRemote.transact(1, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          label145:
          label150:
          while (!paramBoolean1)
          {
            paramInt1 = 0;
            break;
            paramInt1 = 0;
            break label80;
          }
          paramInt1 = 1;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IIntentReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */