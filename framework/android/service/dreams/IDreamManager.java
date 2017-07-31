package android.service.dreams;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IDreamManager
  extends IInterface
{
  public abstract void awaken()
    throws RemoteException;
  
  public abstract void dream()
    throws RemoteException;
  
  public abstract void finishSelf(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ComponentName getDefaultDreamComponent()
    throws RemoteException;
  
  public abstract ComponentName[] getDreamComponents()
    throws RemoteException;
  
  public abstract boolean isDreaming()
    throws RemoteException;
  
  public abstract void setDreamComponents(ComponentName[] paramArrayOfComponentName)
    throws RemoteException;
  
  public abstract void startDozing(IBinder paramIBinder, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void stopDozing(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void testDream(ComponentName paramComponentName)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IDreamManager
  {
    private static final String DESCRIPTOR = "android.service.dreams.IDreamManager";
    static final int TRANSACTION_awaken = 2;
    static final int TRANSACTION_dream = 1;
    static final int TRANSACTION_finishSelf = 8;
    static final int TRANSACTION_getDefaultDreamComponent = 5;
    static final int TRANSACTION_getDreamComponents = 4;
    static final int TRANSACTION_isDreaming = 7;
    static final int TRANSACTION_setDreamComponents = 3;
    static final int TRANSACTION_startDozing = 9;
    static final int TRANSACTION_stopDozing = 10;
    static final int TRANSACTION_testDream = 6;
    
    public Stub()
    {
      attachInterface(this, "android.service.dreams.IDreamManager");
    }
    
    public static IDreamManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.dreams.IDreamManager");
      if ((localIInterface != null) && ((localIInterface instanceof IDreamManager))) {
        return (IDreamManager)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.dreams.IDreamManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        dream();
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        awaken();
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        setDreamComponents((ComponentName[])paramParcel1.createTypedArray(ComponentName.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        paramParcel1 = getDreamComponents();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        paramParcel1 = getDefaultDreamComponent();
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
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          testDream(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        bool = isDreaming();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          finishSelf(localIBinder, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
        startDozing(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.service.dreams.IDreamManager");
      stopDozing(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IDreamManager
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
      
      public void awaken()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
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
      
      public void dream()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
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
      
      public void finishSelf(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getDefaultDreamComponent()
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
        //   15: getfield 19	android/service/dreams/IDreamManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_5
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 43 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 46	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 66	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 72	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 78 2 0
        //   48: checkcast 68	android/content/ComponentName
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
        //   51	13	1	localComponentName	ComponentName
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public ComponentName[] getDreamComponents()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ComponentName[] arrayOfComponentName = (ComponentName[])localParcel2.createTypedArray(ComponentName.CREATOR);
          return arrayOfComponentName;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.dreams.IDreamManager";
      }
      
      /* Error */
      public boolean isDreaming()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/service/dreams/IDreamManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 43 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 46	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 66	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 49	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 49	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 49	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void setDreamComponents(ComponentName[] paramArrayOfComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
          localParcel1.writeTypedArray(paramArrayOfComponentName, 0);
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
      
      public void startDozing(IBinder paramIBinder, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopDozing(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.dreams.IDreamManager");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void testDream(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 105	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/service/dreams/IDreamManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 49	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 49	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 49	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 49	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/dreams/IDreamManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */