package android.content;

import android.accounts.Account;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ISyncAdapter
  extends IInterface
{
  public abstract void cancelSync(ISyncContext paramISyncContext)
    throws RemoteException;
  
  public abstract void initialize(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract void startSync(ISyncContext paramISyncContext, String paramString, Account paramAccount, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISyncAdapter
  {
    private static final String DESCRIPTOR = "android.content.ISyncAdapter";
    static final int TRANSACTION_cancelSync = 2;
    static final int TRANSACTION_initialize = 3;
    static final int TRANSACTION_startSync = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.ISyncAdapter");
    }
    
    public static ISyncAdapter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.ISyncAdapter");
      if ((localIInterface != null) && ((localIInterface instanceof ISyncAdapter))) {
        return (ISyncAdapter)localIInterface;
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
        paramParcel2.writeString("android.content.ISyncAdapter");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.ISyncAdapter");
        ISyncContext localISyncContext = ISyncContext.Stub.asInterface(paramParcel1.readStrongBinder());
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label140;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startSync(localISyncContext, str, paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 2: 
        label140:
        paramParcel1.enforceInterface("android.content.ISyncAdapter");
        cancelSync(ISyncContext.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel1.enforceInterface("android.content.ISyncAdapter");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
      {
        initialize(paramParcel2, paramParcel1.readString());
        return true;
      }
    }
    
    private static class Proxy
      implements ISyncAdapter
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
      
      public void cancelSync(ISyncContext paramISyncContext)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.content.ISyncAdapter");
          if (paramISyncContext != null) {
            localIBinder = paramISyncContext.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.ISyncAdapter";
      }
      
      /* Error */
      public void initialize(Account paramAccount, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 69	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/ISyncAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 51 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 54	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramAccount	Account
        //   0	64	2	paramString	String
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void startSync(ISyncContext paramISyncContext, String paramString, Account paramAccount, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.ISyncAdapter");
            if (paramISyncContext != null) {
              localIBinder = paramISyncContext.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            localParcel.writeString(paramString);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ISyncAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */