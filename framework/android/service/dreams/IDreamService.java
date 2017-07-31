package android.service.dreams;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IDreamService
  extends IInterface
{
  public abstract void attach(IBinder paramIBinder, boolean paramBoolean, IRemoteCallback paramIRemoteCallback)
    throws RemoteException;
  
  public abstract void detach()
    throws RemoteException;
  
  public abstract void wakeUp()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IDreamService
  {
    private static final String DESCRIPTOR = "android.service.dreams.IDreamService";
    static final int TRANSACTION_attach = 1;
    static final int TRANSACTION_detach = 2;
    static final int TRANSACTION_wakeUp = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.dreams.IDreamService");
    }
    
    public static IDreamService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.dreams.IDreamService");
      if ((localIInterface != null) && ((localIInterface instanceof IDreamService))) {
        return (IDreamService)localIInterface;
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
        paramParcel2.writeString("android.service.dreams.IDreamService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamService");
        paramParcel2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          attach(paramParcel2, bool, IRemoteCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.dreams.IDreamService");
        detach();
        return true;
      }
      paramParcel1.enforceInterface("android.service.dreams.IDreamService");
      wakeUp();
      return true;
    }
    
    private static class Proxy
      implements IDreamService
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
      public void attach(IBinder paramIBinder, boolean paramBoolean, IRemoteCallback paramIRemoteCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: iconst_1
        //   4: istore 4
        //   6: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 6
        //   11: aload 6
        //   13: ldc 34
        //   15: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   18: aload 6
        //   20: aload_1
        //   21: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   24: iload_2
        //   25: ifeq +51 -> 76
        //   28: aload 6
        //   30: iload 4
        //   32: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   35: aload 5
        //   37: astore_1
        //   38: aload_3
        //   39: ifnull +10 -> 49
        //   42: aload_3
        //   43: invokeinterface 49 1 0
        //   48: astore_1
        //   49: aload 6
        //   51: aload_1
        //   52: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 19	android/service/dreams/IDreamService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: iconst_1
        //   60: aload 6
        //   62: aconst_null
        //   63: iconst_1
        //   64: invokeinterface 55 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 58	android/os/Parcel:recycle	()V
        //   75: return
        //   76: iconst_0
        //   77: istore 4
        //   79: goto -51 -> 28
        //   82: astore_1
        //   83: aload 6
        //   85: invokevirtual 58	android/os/Parcel:recycle	()V
        //   88: aload_1
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramIBinder	IBinder
        //   0	90	2	paramBoolean	boolean
        //   0	90	3	paramIRemoteCallback	IRemoteCallback
        //   4	74	4	i	int
        //   1	35	5	localObject	Object
        //   9	75	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	24	82	finally
        //   28	35	82	finally
        //   42	49	82	finally
        //   49	70	82	finally
      }
      
      public void detach()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.dreams.IDreamService");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.dreams.IDreamService";
      }
      
      public void wakeUp()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.dreams.IDreamService");
          this.mRemote.transact(3, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/dreams/IDreamService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */