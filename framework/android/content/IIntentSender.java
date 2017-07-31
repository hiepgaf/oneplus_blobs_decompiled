package android.content;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IIntentSender
  extends IInterface
{
  public abstract void send(int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IIntentSender
  {
    private static final String DESCRIPTOR = "android.content.IIntentSender";
    static final int TRANSACTION_send = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.IIntentSender");
    }
    
    public static IIntentSender asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.IIntentSender");
      if ((localIInterface != null) && ((localIInterface instanceof IIntentSender))) {
        return (IIntentSender)localIInterface;
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
        paramParcel2.writeString("android.content.IIntentSender");
        return true;
      }
      paramParcel1.enforceInterface("android.content.IIntentSender");
      paramInt1 = paramParcel1.readInt();
      String str1;
      IIntentReceiver localIIntentReceiver;
      String str2;
      if (paramParcel1.readInt() != 0)
      {
        paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
        str1 = paramParcel1.readString();
        localIIntentReceiver = IIntentReceiver.Stub.asInterface(paramParcel1.readStrongBinder());
        str2 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {
          break label138;
        }
      }
      label138:
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        send(paramInt1, paramParcel2, str1, localIIntentReceiver, str2, paramParcel1);
        return true;
        paramParcel2 = null;
        break;
      }
    }
    
    private static class Proxy
      implements IIntentSender
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
        return "android.content.IIntentSender";
      }
      
      public void send(int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.IIntentSender");
            localParcel.writeInt(paramInt);
            if (paramIntent != null)
            {
              localParcel.writeInt(1);
              paramIntent.writeToParcel(localParcel, 0);
              localParcel.writeString(paramString1);
              paramIntent = (Intent)localObject;
              if (paramIIntentReceiver != null) {
                paramIntent = paramIIntentReceiver.asBinder();
              }
              localParcel.writeStrongBinder(paramIntent);
              localParcel.writeString(paramString2);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
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
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IIntentSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */