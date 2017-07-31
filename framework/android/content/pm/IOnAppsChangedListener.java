package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;

public abstract interface IOnAppsChangedListener
  extends IInterface
{
  public abstract void onPackageAdded(UserHandle paramUserHandle, String paramString)
    throws RemoteException;
  
  public abstract void onPackageChanged(UserHandle paramUserHandle, String paramString)
    throws RemoteException;
  
  public abstract void onPackageRemoved(UserHandle paramUserHandle, String paramString)
    throws RemoteException;
  
  public abstract void onPackagesAvailable(UserHandle paramUserHandle, String[] paramArrayOfString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onPackagesSuspended(UserHandle paramUserHandle, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void onPackagesUnavailable(UserHandle paramUserHandle, String[] paramArrayOfString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onPackagesUnsuspended(UserHandle paramUserHandle, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void onShortcutChanged(UserHandle paramUserHandle, String paramString, ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IOnAppsChangedListener
  {
    private static final String DESCRIPTOR = "android.content.pm.IOnAppsChangedListener";
    static final int TRANSACTION_onPackageAdded = 2;
    static final int TRANSACTION_onPackageChanged = 3;
    static final int TRANSACTION_onPackageRemoved = 1;
    static final int TRANSACTION_onPackagesAvailable = 4;
    static final int TRANSACTION_onPackagesSuspended = 6;
    static final int TRANSACTION_onPackagesUnavailable = 5;
    static final int TRANSACTION_onPackagesUnsuspended = 7;
    static final int TRANSACTION_onShortcutChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IOnAppsChangedListener");
    }
    
    public static IOnAppsChangedListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IOnAppsChangedListener");
      if ((localIInterface != null) && ((localIInterface instanceof IOnAppsChangedListener))) {
        return (IOnAppsChangedListener)localIInterface;
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
      boolean bool2 = false;
      boolean bool1 = false;
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.pm.IOnAppsChangedListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onPackageRemoved(paramParcel2, paramParcel1.readString());
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onPackageAdded(paramParcel2, paramParcel1.readString());
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onPackageChanged(paramParcel2, paramParcel1.readString());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          localObject = paramParcel1.createStringArray();
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          onPackagesAvailable(paramParcel2, (String[])localObject, bool1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          localObject = paramParcel1.createStringArray();
          bool1 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          onPackagesUnavailable(paramParcel2, (String[])localObject, bool1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onPackagesSuspended(paramParcel2, paramParcel1.createStringArray());
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onPackagesUnsuspended(paramParcel2, paramParcel1.createStringArray());
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.pm.IOnAppsChangedListener");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {
          break label503;
        }
      }
      label503:
      for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onShortcutChanged(paramParcel2, (String)localObject, paramParcel1);
        return true;
        paramParcel2 = null;
        break;
      }
    }
    
    private static class Proxy
      implements IOnAppsChangedListener
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
        return "android.content.pm.IOnAppsChangedListener";
      }
      
      /* Error */
      public void onPackageAdded(UserHandle paramUserHandle, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 53	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/IOnAppsChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 59 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 62	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramUserHandle	UserHandle
        //   0	64	2	paramString	String
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      /* Error */
      public void onPackageChanged(UserHandle paramUserHandle, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 53	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/IOnAppsChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 59 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 62	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramUserHandle	UserHandle
        //   0	64	2	paramString	String
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      /* Error */
      public void onPackageRemoved(UserHandle paramUserHandle, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 53	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/IOnAppsChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 59 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 62	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramUserHandle	UserHandle
        //   0	64	2	paramString	String
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void onPackagesAvailable(UserHandle paramUserHandle, String[] paramArrayOfString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.pm.IOnAppsChangedListener");
            if (paramUserHandle != null)
            {
              localParcel.writeInt(1);
              paramUserHandle.writeToParcel(localParcel, 0);
              localParcel.writeStringArray(paramArrayOfString);
              if (paramBoolean)
              {
                localParcel.writeInt(i);
                this.mRemote.transact(4, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      /* Error */
      public void onPackagesSuspended(UserHandle paramUserHandle, String[] paramArrayOfString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 71	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/IOnAppsChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 6
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 59 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 62	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 62	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramUserHandle	UserHandle
        //   0	65	2	paramArrayOfString	String[]
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      public void onPackagesUnavailable(UserHandle paramUserHandle, String[] paramArrayOfString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.pm.IOnAppsChangedListener");
            if (paramUserHandle != null)
            {
              localParcel.writeInt(1);
              paramUserHandle.writeToParcel(localParcel, 0);
              localParcel.writeStringArray(paramArrayOfString);
              if (paramBoolean)
              {
                localParcel.writeInt(i);
                this.mRemote.transact(5, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      /* Error */
      public void onPackagesUnsuspended(UserHandle paramUserHandle, String[] paramArrayOfString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: aload_2
        //   27: invokevirtual 71	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   30: aload_0
        //   31: getfield 19	android/content/pm/IOnAppsChangedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 7
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 59 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 62	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 62	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramUserHandle	UserHandle
        //   0	65	2	paramArrayOfString	String[]
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      public void onShortcutChanged(UserHandle paramUserHandle, String paramString, ParceledListSlice paramParceledListSlice)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.content.pm.IOnAppsChangedListener");
            if (paramUserHandle != null)
            {
              localParcel.writeInt(1);
              paramUserHandle.writeToParcel(localParcel, 0);
              localParcel.writeString(paramString);
              if (paramParceledListSlice != null)
              {
                localParcel.writeInt(1);
                paramParceledListSlice.writeToParcel(localParcel, 0);
                this.mRemote.transact(8, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IOnAppsChangedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */