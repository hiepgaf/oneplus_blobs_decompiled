package android.content.pm.permission;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteCallback;
import android.os.RemoteException;

public abstract interface IRuntimePermissionPresenter
  extends IInterface
{
  public abstract void getAppPermissions(String paramString, RemoteCallback paramRemoteCallback)
    throws RemoteException;
  
  public abstract void getAppsUsingPermissions(boolean paramBoolean, RemoteCallback paramRemoteCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRuntimePermissionPresenter
  {
    private static final String DESCRIPTOR = "android.content.pm.permission.IRuntimePermissionPresenter";
    static final int TRANSACTION_getAppPermissions = 1;
    static final int TRANSACTION_getAppsUsingPermissions = 2;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.permission.IRuntimePermissionPresenter");
    }
    
    public static IRuntimePermissionPresenter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.permission.IRuntimePermissionPresenter");
      if ((localIInterface != null) && ((localIInterface instanceof IRuntimePermissionPresenter))) {
        return (IRuntimePermissionPresenter)localIInterface;
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
        paramParcel2.writeString("android.content.pm.permission.IRuntimePermissionPresenter");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.permission.IRuntimePermissionPresenter");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (RemoteCallback)RemoteCallback.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          getAppPermissions(paramParcel2, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.pm.permission.IRuntimePermissionPresenter");
      boolean bool;
      if (paramParcel1.readInt() != 0)
      {
        bool = true;
        if (paramParcel1.readInt() == 0) {
          break label149;
        }
      }
      label149:
      for (paramParcel1 = (RemoteCallback)RemoteCallback.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        getAppsUsingPermissions(bool, paramParcel1);
        return true;
        bool = false;
        break;
      }
    }
    
    private static class Proxy
      implements IRuntimePermissionPresenter
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
      
      /* Error */
      public void getAppPermissions(String paramString, RemoteCallback paramRemoteCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 51	android/os/RemoteCallback:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/permission/IRuntimePermissionPresenter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 57 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 60	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramString	String
        //   0	64	2	paramRemoteCallback	RemoteCallback
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      /* Error */
      public void getAppsUsingPermissions(boolean paramBoolean, RemoteCallback paramRemoteCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +47 -> 62
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: ifnull +42 -> 67
        //   28: aload 4
        //   30: iconst_1
        //   31: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   34: aload_2
        //   35: aload 4
        //   37: iconst_0
        //   38: invokevirtual 51	android/os/RemoteCallback:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload_0
        //   42: getfield 19	android/content/pm/permission/IRuntimePermissionPresenter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: iconst_2
        //   46: aload 4
        //   48: aconst_null
        //   49: iconst_1
        //   50: invokeinterface 57 5 0
        //   55: pop
        //   56: aload 4
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: return
        //   62: iconst_0
        //   63: istore_3
        //   64: goto -46 -> 18
        //   67: aload 4
        //   69: iconst_0
        //   70: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   73: goto -32 -> 41
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 60	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramBoolean	boolean
        //   0	84	2	paramRemoteCallback	RemoteCallback
        //   1	63	3	i	int
        //   5	73	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	76	finally
        //   18	24	76	finally
        //   28	41	76	finally
        //   41	56	76	finally
        //   67	73	76	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.permission.IRuntimePermissionPresenter";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/permission/IRuntimePermissionPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */