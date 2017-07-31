package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IProcessObserver
  extends IInterface
{
  public abstract void onForegroundActivitiesChanged(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onProcessDied(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onProcessStateChanged(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IProcessObserver
  {
    private static final String DESCRIPTOR = "android.app.IProcessObserver";
    static final int TRANSACTION_onForegroundActivitiesChanged = 1;
    static final int TRANSACTION_onProcessDied = 3;
    static final int TRANSACTION_onProcessStateChanged = 2;
    
    public Stub()
    {
      attachInterface(this, "android.app.IProcessObserver");
    }
    
    public static IProcessObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IProcessObserver");
      if ((localIInterface != null) && ((localIInterface instanceof IProcessObserver))) {
        return (IProcessObserver)localIInterface;
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
        paramParcel2.writeString("android.app.IProcessObserver");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IProcessObserver");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        onForegroundActivitiesChanged(paramInt1, paramInt2, bool);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.IProcessObserver");
        onProcessStateChanged(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.app.IProcessObserver");
      onProcessDied(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IProcessObserver
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
        return "android.app.IProcessObserver";
      }
      
      /* Error */
      public void onForegroundActivitiesChanged(int paramInt1, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 5
        //   17: iload_1
        //   18: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   21: aload 5
        //   23: iload_2
        //   24: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   27: iload_3
        //   28: ifeq +33 -> 61
        //   31: iload 4
        //   33: istore_1
        //   34: aload 5
        //   36: iload_1
        //   37: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   40: aload_0
        //   41: getfield 19	android/app/IProcessObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   44: iconst_1
        //   45: aload 5
        //   47: aconst_null
        //   48: iconst_1
        //   49: invokeinterface 50 5 0
        //   54: pop
        //   55: aload 5
        //   57: invokevirtual 53	android/os/Parcel:recycle	()V
        //   60: return
        //   61: iconst_0
        //   62: istore_1
        //   63: goto -29 -> 34
        //   66: astore 6
        //   68: aload 5
        //   70: invokevirtual 53	android/os/Parcel:recycle	()V
        //   73: aload 6
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramInt1	int
        //   0	76	2	paramInt2	int
        //   0	76	3	paramBoolean	boolean
        //   1	31	4	i	int
        //   6	63	5	localParcel	Parcel
        //   66	8	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	27	66	finally
        //   34	55	66	finally
      }
      
      public void onProcessDied(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IProcessObserver");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onProcessStateChanged(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IProcessObserver");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IProcessObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */