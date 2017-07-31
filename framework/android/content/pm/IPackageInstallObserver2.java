package android.content.pm;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPackageInstallObserver2
  extends IInterface
{
  public abstract void onPackageInstalled(String paramString1, int paramInt, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onUserActionRequired(Intent paramIntent)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPackageInstallObserver2
  {
    private static final String DESCRIPTOR = "android.content.pm.IPackageInstallObserver2";
    static final int TRANSACTION_onPackageInstalled = 2;
    static final int TRANSACTION_onUserActionRequired = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IPackageInstallObserver2");
    }
    
    public static IPackageInstallObserver2 asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IPackageInstallObserver2");
      if ((localIInterface != null) && ((localIInterface instanceof IPackageInstallObserver2))) {
        return (IPackageInstallObserver2)localIInterface;
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
        paramParcel2.writeString("android.content.pm.IPackageInstallObserver2");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallObserver2");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onUserActionRequired(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.pm.IPackageInstallObserver2");
      paramParcel2 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      String str = paramParcel1.readString();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onPackageInstalled(paramParcel2, paramInt1, str, paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IPackageInstallObserver2
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
        return "android.content.pm.IPackageInstallObserver2";
      }
      
      /* Error */
      public void onPackageInstalled(String paramString1, int paramInt, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 5
        //   14: aload_1
        //   15: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   18: aload 5
        //   20: iload_2
        //   21: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   24: aload 5
        //   26: aload_3
        //   27: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   30: aload 4
        //   32: ifnull +38 -> 70
        //   35: aload 5
        //   37: iconst_1
        //   38: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokevirtual 53	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: aload_0
        //   50: getfield 19	android/content/pm/IPackageInstallObserver2$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   53: iconst_2
        //   54: aload 5
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 59 5 0
        //   63: pop
        //   64: aload 5
        //   66: invokevirtual 62	android/os/Parcel:recycle	()V
        //   69: return
        //   70: aload 5
        //   72: iconst_0
        //   73: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   76: goto -27 -> 49
        //   79: astore_1
        //   80: aload 5
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString1	String
        //   0	87	2	paramInt	int
        //   0	87	3	paramString2	String
        //   0	87	4	paramBundle	Bundle
        //   3	78	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	30	79	finally
        //   35	49	79	finally
        //   49	64	79	finally
        //   70	76	79	finally
      }
      
      /* Error */
      public void onUserActionRequired(Intent paramIntent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageInstallObserver2$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 59 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 62	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramIntent	Intent
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IPackageInstallObserver2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */