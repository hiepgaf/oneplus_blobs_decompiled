package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IUiModeManager
  extends IInterface
{
  public abstract void disableCarMode(int paramInt)
    throws RemoteException;
  
  public abstract void enableCarMode(int paramInt)
    throws RemoteException;
  
  public abstract int getCurrentModeType()
    throws RemoteException;
  
  public abstract int getNightMode()
    throws RemoteException;
  
  public abstract boolean isNightModeLocked()
    throws RemoteException;
  
  public abstract boolean isUiModeLocked()
    throws RemoteException;
  
  public abstract void setNightMode(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUiModeManager
  {
    private static final String DESCRIPTOR = "android.app.IUiModeManager";
    static final int TRANSACTION_disableCarMode = 2;
    static final int TRANSACTION_enableCarMode = 1;
    static final int TRANSACTION_getCurrentModeType = 3;
    static final int TRANSACTION_getNightMode = 5;
    static final int TRANSACTION_isNightModeLocked = 7;
    static final int TRANSACTION_isUiModeLocked = 6;
    static final int TRANSACTION_setNightMode = 4;
    
    public Stub()
    {
      attachInterface(this, "android.app.IUiModeManager");
    }
    
    public static IUiModeManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IUiModeManager");
      if ((localIInterface != null) && ((localIInterface instanceof IUiModeManager))) {
        return (IUiModeManager)localIInterface;
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
      int j = 0;
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IUiModeManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        enableCarMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        disableCarMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        paramInt1 = getCurrentModeType();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        setNightMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        paramInt1 = getNightMode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.IUiModeManager");
        bool = isUiModeLocked();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.app.IUiModeManager");
      boolean bool = isNightModeLocked();
      paramParcel2.writeNoException();
      paramInt1 = j;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IUiModeManager
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
      
      public void disableCarMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiModeManager");
          localParcel1.writeInt(paramInt);
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
      
      public void enableCarMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiModeManager");
          localParcel1.writeInt(paramInt);
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
      
      public int getCurrentModeType()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiModeManager");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IUiModeManager";
      }
      
      public int getNightMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiModeManager");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isNightModeLocked()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/app/IUiModeManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 47 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 50	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 60	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 53	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 53	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 53	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 53	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean isUiModeLocked()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/app/IUiModeManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 6
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 47 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 50	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 60	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 53	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 53	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 53	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 53	android/os/Parcel:recycle	()V
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
      
      public void setNightMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiModeManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IUiModeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */