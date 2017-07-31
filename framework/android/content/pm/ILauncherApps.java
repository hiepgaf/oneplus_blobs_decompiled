package android.content.pm;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import java.util.List;

public abstract interface ILauncherApps
  extends IInterface
{
  public abstract void addOnAppsChangedListener(String paramString, IOnAppsChangedListener paramIOnAppsChangedListener)
    throws RemoteException;
  
  public abstract ApplicationInfo getApplicationInfo(String paramString, int paramInt, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract ParceledListSlice getLauncherActivities(String paramString, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor getShortcutIconFd(String paramString1, String paramString2, String paramString3, int paramInt)
    throws RemoteException;
  
  public abstract int getShortcutIconResId(String paramString1, String paramString2, String paramString3, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getShortcuts(String paramString1, long paramLong, String paramString2, List paramList, ComponentName paramComponentName, int paramInt, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract boolean hasShortcutHostPermission(String paramString)
    throws RemoteException;
  
  public abstract boolean isActivityEnabled(ComponentName paramComponentName, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract boolean isPackageEnabled(String paramString, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void pinShortcuts(String paramString1, String paramString2, List<String> paramList, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void removeOnAppsChangedListener(IOnAppsChangedListener paramIOnAppsChangedListener)
    throws RemoteException;
  
  public abstract ActivityInfo resolveActivity(ComponentName paramComponentName, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void showAppDetailsAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void startActivityAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract boolean startShortcut(String paramString1, String paramString2, String paramString3, Rect paramRect, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ILauncherApps
  {
    private static final String DESCRIPTOR = "android.content.pm.ILauncherApps";
    static final int TRANSACTION_addOnAppsChangedListener = 1;
    static final int TRANSACTION_getApplicationInfo = 9;
    static final int TRANSACTION_getLauncherActivities = 3;
    static final int TRANSACTION_getShortcutIconFd = 14;
    static final int TRANSACTION_getShortcutIconResId = 13;
    static final int TRANSACTION_getShortcuts = 10;
    static final int TRANSACTION_hasShortcutHostPermission = 15;
    static final int TRANSACTION_isActivityEnabled = 8;
    static final int TRANSACTION_isPackageEnabled = 7;
    static final int TRANSACTION_pinShortcuts = 11;
    static final int TRANSACTION_removeOnAppsChangedListener = 2;
    static final int TRANSACTION_resolveActivity = 4;
    static final int TRANSACTION_showAppDetailsAsUser = 6;
    static final int TRANSACTION_startActivityAsUser = 5;
    static final int TRANSACTION_startShortcut = 12;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.ILauncherApps");
    }
    
    public static ILauncherApps asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.ILauncherApps");
      if ((localIInterface != null) && ((localIInterface instanceof ILauncherApps))) {
        return (ILauncherApps)localIInterface;
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
      Object localObject1;
      label274:
      label329:
      label364:
      label369:
      Object localObject2;
      label425:
      Object localObject3;
      label446:
      label489:
      label495:
      label501:
      label554:
      label575:
      label618:
      label624:
      label630:
      label699:
      label751:
      label784:
      label789:
      label866:
      Object localObject4;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.pm.ILauncherApps");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        addOnAppsChangedListener(paramParcel1.readString(), IOnAppsChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        removeOnAppsChangedListener(IOnAppsChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLauncherActivities((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label274;
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
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label364;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = resolveActivity((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label369;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label329;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label489;
          }
          localObject2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label495;
          }
          localObject3 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label501;
          }
        }
        for (paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startActivityAsUser((ComponentName)localObject1, (Rect)localObject2, (Bundle)localObject3, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label425;
          localObject3 = null;
          break label446;
        }
      case 6: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label618;
          }
          localObject2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label624;
          }
          localObject3 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label630;
          }
        }
        for (paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          showAppDetailsAsUser((ComponentName)localObject1, (Rect)localObject2, (Bundle)localObject3, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label554;
          localObject3 = null;
          break label575;
        }
      case 7: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          bool = isPackageEnabled((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label699;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label784;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          bool = isActivityEnabled((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label789;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label751;
        }
      case 9: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getApplicationInfo((String)localObject1, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label866;
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
      case 10: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject2 = paramParcel1.readString();
        long l = paramParcel1.readLong();
        localObject3 = paramParcel1.readString();
        localObject4 = paramParcel1.readArrayList(getClass().getClassLoader());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1001;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getShortcuts((String)localObject2, l, (String)localObject3, (List)localObject4, (ComponentName)localObject1, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1006;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label957;
          paramParcel2.writeInt(0);
        }
      case 11: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject1 = paramParcel1.readString();
        localObject2 = paramParcel1.readString();
        localObject3 = paramParcel1.createStringArrayList();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          pinShortcuts((String)localObject1, (String)localObject2, (List)localObject3, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        localObject3 = paramParcel1.readString();
        localObject4 = paramParcel1.readString();
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1190;
          }
          localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          bool = startShortcut((String)localObject3, (String)localObject4, str, (Rect)localObject1, (Bundle)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label1196;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1146;
        }
      case 13: 
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        paramInt1 = getShortcutIconResId(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 14: 
        label957:
        label1001:
        label1006:
        label1146:
        label1190:
        label1196:
        paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
        paramParcel1 = getShortcutIconFd(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
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
      }
      paramParcel1.enforceInterface("android.content.pm.ILauncherApps");
      boolean bool = hasShortcutHostPermission(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements ILauncherApps
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addOnAppsChangedListener(String paramString, IOnAppsChangedListener paramIOnAppsChangedListener)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIOnAppsChangedListener != null) {
            paramString = paramIOnAppsChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public ApplicationInfo getApplicationInfo(String paramString, int paramInt, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            localParcel1.writeString(paramString);
            localParcel1.writeInt(paramInt);
            if (paramUserHandle != null)
            {
              localParcel1.writeInt(1);
              paramUserHandle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(9, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString = (ApplicationInfo)ApplicationInfo.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.ILauncherApps";
      }
      
      public ParceledListSlice getLauncherActivities(String paramString, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            localParcel1.writeString(paramString);
            if (paramUserHandle != null)
            {
              localParcel1.writeInt(1);
              paramUserHandle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public ParcelFileDescriptor getShortcutIconFd(String paramString1, String paramString2, String paramString3, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: aload_3
        //   32: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload 5
        //   37: iload 4
        //   39: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   42: aload_0
        //   43: getfield 19	android/content/pm/ILauncherApps$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   46: bipush 14
        //   48: aload 5
        //   50: aload 6
        //   52: iconst_0
        //   53: invokeinterface 54 5 0
        //   58: pop
        //   59: aload 6
        //   61: invokevirtual 57	android/os/Parcel:readException	()V
        //   64: aload 6
        //   66: invokevirtual 77	android/os/Parcel:readInt	()I
        //   69: ifeq +29 -> 98
        //   72: getstatic 106	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   75: aload 6
        //   77: invokeinterface 89 2 0
        //   82: checkcast 105	android/os/ParcelFileDescriptor
        //   85: astore_1
        //   86: aload 6
        //   88: invokevirtual 60	android/os/Parcel:recycle	()V
        //   91: aload 5
        //   93: invokevirtual 60	android/os/Parcel:recycle	()V
        //   96: aload_1
        //   97: areturn
        //   98: aconst_null
        //   99: astore_1
        //   100: goto -14 -> 86
        //   103: astore_1
        //   104: aload 6
        //   106: invokevirtual 60	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 60	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	116	0	this	Proxy
        //   0	116	1	paramString1	String
        //   0	116	2	paramString2	String
        //   0	116	3	paramString3	String
        //   0	116	4	paramInt	int
        //   3	107	5	localParcel1	Parcel
        //   8	97	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	86	103	finally
      }
      
      public int getShortcutIconResId(String paramString1, String paramString2, String paramString3, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ParceledListSlice getShortcuts(String paramString1, long paramLong, String paramString2, List paramList, ComponentName paramComponentName, int paramInt, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            localParcel1.writeString(paramString1);
            localParcel1.writeLong(paramLong);
            localParcel1.writeString(paramString2);
            localParcel1.writeList(paramList);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(10, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label175;
                }
                paramString1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramString1;
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
          continue;
          label175:
          paramString1 = null;
        }
      }
      
      /* Error */
      public boolean hasShortcutHostPermission(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/pm/ILauncherApps$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 15
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 57	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 77	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 60	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 60	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 60	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public boolean isActivityEnabled(ComponentName paramComponentName, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(8, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
                }
                bool = true;
                return bool;
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
          continue;
          label130:
          boolean bool = false;
        }
      }
      
      public boolean isPackageEnabled(String paramString, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            localParcel1.writeString(paramString);
            if (paramUserHandle != null)
            {
              localParcel1.writeInt(1);
              paramUserHandle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public void pinShortcuts(String paramString1, String paramString2, List<String> paramList, UserHandle paramUserHandle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: aload_3
        //   32: invokevirtual 132	android/os/Parcel:writeStringList	(Ljava/util/List;)V
        //   35: aload 4
        //   37: ifnull +50 -> 87
        //   40: aload 5
        //   42: iconst_1
        //   43: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokevirtual 73	android/os/UserHandle:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: aload_0
        //   55: getfield 19	android/content/pm/ILauncherApps$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   58: bipush 11
        //   60: aload 5
        //   62: aload 6
        //   64: iconst_0
        //   65: invokeinterface 54 5 0
        //   70: pop
        //   71: aload 6
        //   73: invokevirtual 57	android/os/Parcel:readException	()V
        //   76: aload 6
        //   78: invokevirtual 60	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: invokevirtual 60	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 5
        //   89: iconst_0
        //   90: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   93: goto -39 -> 54
        //   96: astore_1
        //   97: aload 6
        //   99: invokevirtual 60	android/os/Parcel:recycle	()V
        //   102: aload 5
        //   104: invokevirtual 60	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString1	String
        //   0	109	2	paramString2	String
        //   0	109	3	paramList	List<String>
        //   0	109	4	paramUserHandle	UserHandle
        //   3	100	5	localParcel1	Parcel
        //   8	90	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	96	finally
        //   40	54	96	finally
        //   54	76	96	finally
        //   87	93	96	finally
      }
      
      public void removeOnAppsChangedListener(IOnAppsChangedListener paramIOnAppsChangedListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
          if (paramIOnAppsChangedListener != null) {
            localIBinder = paramIOnAppsChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ActivityInfo resolveActivity(ComponentName paramComponentName, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label126;
                }
                paramComponentName = (ActivityInfo)ActivityInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
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
          continue;
          label126:
          paramComponentName = null;
        }
      }
      
      public void showAppDetailsAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramRect != null)
              {
                localParcel1.writeInt(1);
                paramRect.writeToParcel(localParcel1, 0);
                if (paramBundle == null) {
                  break label151;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                if (paramUserHandle == null) {
                  break label160;
                }
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
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
          continue;
          label151:
          localParcel1.writeInt(0);
          continue;
          label160:
          localParcel1.writeInt(0);
        }
      }
      
      public void startActivityAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramRect != null)
              {
                localParcel1.writeInt(1);
                paramRect.writeToParcel(localParcel1, 0);
                if (paramBundle == null) {
                  break label150;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                if (paramUserHandle == null) {
                  break label159;
                }
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
          continue;
          label150:
          localParcel1.writeInt(0);
          continue;
          label159:
          localParcel1.writeInt(0);
        }
      }
      
      public boolean startShortcut(String paramString1, String paramString2, String paramString3, Rect paramRect, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.ILauncherApps");
            localParcel1.writeString(paramString1);
            localParcel1.writeString(paramString2);
            localParcel1.writeString(paramString3);
            if (paramRect != null)
            {
              localParcel1.writeInt(1);
              paramRect.writeToParcel(localParcel1, 0);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(12, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label161;
                }
                bool = true;
                return bool;
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
          continue;
          label161:
          boolean bool = false;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ILauncherApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */