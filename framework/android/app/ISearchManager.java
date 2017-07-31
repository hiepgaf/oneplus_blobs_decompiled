package android.app;

import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface ISearchManager
  extends IInterface
{
  public abstract List<ResolveInfo> getGlobalSearchActivities()
    throws RemoteException;
  
  public abstract ComponentName getGlobalSearchActivity()
    throws RemoteException;
  
  public abstract SearchableInfo getSearchableInfo(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract List<SearchableInfo> getSearchablesInGlobalSearch()
    throws RemoteException;
  
  public abstract ComponentName getWebSearchActivity()
    throws RemoteException;
  
  public abstract void launchAssist(Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean launchLegacyAssist(String paramString, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISearchManager
  {
    private static final String DESCRIPTOR = "android.app.ISearchManager";
    static final int TRANSACTION_getGlobalSearchActivities = 3;
    static final int TRANSACTION_getGlobalSearchActivity = 4;
    static final int TRANSACTION_getSearchableInfo = 1;
    static final int TRANSACTION_getSearchablesInGlobalSearch = 2;
    static final int TRANSACTION_getWebSearchActivity = 5;
    static final int TRANSACTION_launchAssist = 6;
    static final int TRANSACTION_launchLegacyAssist = 7;
    
    public Stub()
    {
      attachInterface(this, "android.app.ISearchManager");
    }
    
    public static ISearchManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.ISearchManager");
      if ((localIInterface != null) && ((localIInterface instanceof ISearchManager))) {
        return (ISearchManager)localIInterface;
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
        paramParcel2.writeString("android.app.ISearchManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.ISearchManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getSearchableInfo(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label152;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.ISearchManager");
        paramParcel1 = getSearchablesInGlobalSearch();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.ISearchManager");
        paramParcel1 = getGlobalSearchActivities();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.ISearchManager");
        paramParcel1 = getGlobalSearchActivity();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.ISearchManager");
        paramParcel1 = getWebSearchActivity();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 6: 
        label152:
        paramParcel1.enforceInterface("android.app.ISearchManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          launchAssist(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.app.ISearchManager");
      String str = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        boolean bool = launchLegacyAssist(str, paramInt1, paramParcel1);
        paramParcel2.writeNoException();
        if (!bool) {
          break label396;
        }
      }
      label396:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = null;
        break;
      }
    }
    
    private static class Proxy
      implements ISearchManager
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
      
      public List<ResolveInfo> getGlobalSearchActivities()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.ISearchManager");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(ResolveInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getGlobalSearchActivity()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/app/ISearchManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_4
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 44 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 47	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 69	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 72	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 78 2 0
        //   48: checkcast 71	android/content/ComponentName
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 60	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 60	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 60	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 60	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localComponentName	ComponentName
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.ISearchManager";
      }
      
      public SearchableInfo getSearchableInfo(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.ISearchManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (SearchableInfo)SearchableInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public List<SearchableInfo> getSearchablesInGlobalSearch()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.ISearchManager");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(SearchableInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getWebSearchActivity()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/app/ISearchManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_5
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 44 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 47	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 69	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 72	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 78 2 0
        //   48: checkcast 71	android/content/ComponentName
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 60	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 60	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 60	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 60	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localComponentName	ComponentName
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public void launchAssist(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 101	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/ISearchManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 44 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 47	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 60	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 60	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 60	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 60	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramBundle	Bundle
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public boolean launchLegacyAssist(String paramString, int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.ISearchManager");
            localParcel1.writeString(paramString);
            localParcel1.writeInt(paramInt);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ISearchManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */