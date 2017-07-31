package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IActivityContainerCallback
  extends IInterface
{
  public abstract void onAllActivitiesComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void setVisible(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IActivityContainerCallback
  {
    private static final String DESCRIPTOR = "android.app.IActivityContainerCallback";
    static final int TRANSACTION_onAllActivitiesComplete = 2;
    static final int TRANSACTION_setVisible = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.IActivityContainerCallback");
    }
    
    public static IActivityContainerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IActivityContainerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IActivityContainerCallback))) {
        return (IActivityContainerCallback)localIInterface;
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
        paramParcel2.writeString("android.app.IActivityContainerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IActivityContainerCallback");
        paramParcel2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setVisible(paramParcel2, bool);
        return true;
      }
      paramParcel1.enforceInterface("android.app.IActivityContainerCallback");
      onAllActivitiesComplete(paramParcel1.readStrongBinder());
      return true;
    }
    
    private static class Proxy
      implements IActivityContainerCallback
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
        return "android.app.IActivityContainerCallback";
      }
      
      public void onAllActivitiesComplete(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IActivityContainerCallback");
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void setVisible(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload 4
        //   16: aload_1
        //   17: invokevirtual 42	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   20: iload_2
        //   21: ifeq +30 -> 51
        //   24: aload 4
        //   26: iload_3
        //   27: invokevirtual 58	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/app/IActivityContainerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 48 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 51	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -29 -> 24
        //   56: astore_1
        //   57: aload 4
        //   59: invokevirtual 51	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramIBinder	IBinder
        //   0	64	2	paramBoolean	boolean
        //   1	52	3	i	int
        //   5	53	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	20	56	finally
        //   24	45	56	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IActivityContainerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */