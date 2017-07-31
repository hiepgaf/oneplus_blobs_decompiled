package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IOtaDexopt
  extends IInterface
{
  public abstract void cleanup()
    throws RemoteException;
  
  public abstract void dexoptNextPackage()
    throws RemoteException;
  
  public abstract float getProgress()
    throws RemoteException;
  
  public abstract boolean isDone()
    throws RemoteException;
  
  public abstract String nextDexoptCommand()
    throws RemoteException;
  
  public abstract void prepare()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IOtaDexopt
  {
    private static final String DESCRIPTOR = "android.content.pm.IOtaDexopt";
    static final int TRANSACTION_cleanup = 2;
    static final int TRANSACTION_dexoptNextPackage = 5;
    static final int TRANSACTION_getProgress = 4;
    static final int TRANSACTION_isDone = 3;
    static final int TRANSACTION_nextDexoptCommand = 6;
    static final int TRANSACTION_prepare = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IOtaDexopt");
    }
    
    public static IOtaDexopt asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IOtaDexopt");
      if ((localIInterface != null) && ((localIInterface instanceof IOtaDexopt))) {
        return (IOtaDexopt)localIInterface;
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
        paramParcel2.writeString("android.content.pm.IOtaDexopt");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
        prepare();
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
        cleanup();
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
        boolean bool = isDone();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
        float f = getProgress();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
        dexoptNextPackage();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.content.pm.IOtaDexopt");
      paramParcel1 = nextDexoptCommand();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IOtaDexopt
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
      
      public void cleanup()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IOtaDexopt");
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
      
      public void dexoptNextPackage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IOtaDexopt");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.IOtaDexopt";
      }
      
      public float getProgress()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IOtaDexopt");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isDone()
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
        //   16: getfield 19	android/content/pm/IOtaDexopt$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_3
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 43 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 46	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 64	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 49	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 49	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 49	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 49	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public String nextDexoptCommand()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IOtaDexopt");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void prepare()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IOtaDexopt");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IOtaDexopt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */