package android.app;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IAppTask
  extends IInterface
{
  public abstract void finishAndRemoveTask()
    throws RemoteException;
  
  public abstract ActivityManager.RecentTaskInfo getTaskInfo()
    throws RemoteException;
  
  public abstract void moveToFront()
    throws RemoteException;
  
  public abstract void setExcludeFromRecents(boolean paramBoolean)
    throws RemoteException;
  
  public abstract int startActivity(IBinder paramIBinder, String paramString1, Intent paramIntent, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAppTask
  {
    private static final String DESCRIPTOR = "android.app.IAppTask";
    static final int TRANSACTION_finishAndRemoveTask = 1;
    static final int TRANSACTION_getTaskInfo = 2;
    static final int TRANSACTION_moveToFront = 3;
    static final int TRANSACTION_setExcludeFromRecents = 5;
    static final int TRANSACTION_startActivity = 4;
    
    public Stub()
    {
      attachInterface(this, "android.app.IAppTask");
    }
    
    public static IAppTask asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IAppTask");
      if ((localIInterface != null) && ((localIInterface instanceof IAppTask))) {
        return (IAppTask)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IAppTask");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IAppTask");
        finishAndRemoveTask();
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.IAppTask");
        paramParcel1 = getTaskInfo();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.IAppTask");
        moveToFront();
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.IAppTask");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        String str1 = paramParcel1.readString();
        Intent localIntent;
        String str2;
        if (paramParcel1.readInt() != 0)
        {
          localIntent = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          str2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label249;
          }
        }
        label249:
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = startActivity(localIBinder, str1, localIntent, str2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localIntent = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.app.IAppTask");
      if (paramParcel1.readInt() != 0) {
        bool = true;
      }
      setExcludeFromRecents(bool);
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IAppTask
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
      
      public void finishAndRemoveTask()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAppTask");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IAppTask";
      }
      
      /* Error */
      public ActivityManager.RecentTaskInfo getTaskInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/app/IAppTask$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 43 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 46	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 58	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 64	android/app/ActivityManager$RecentTaskInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 70 2 0
        //   48: checkcast 60	android/app/ActivityManager$RecentTaskInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 49	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 49	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localRecentTaskInfo	ActivityManager.RecentTaskInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public void moveToFront()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAppTask");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setExcludeFromRecents(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAppTask");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int startActivity(IBinder paramIBinder, String paramString1, Intent paramIntent, String paramString2, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IAppTask");
            localParcel1.writeStrongBinder(paramIBinder);
            localParcel1.writeString(paramString1);
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString2);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                return i;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IAppTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */