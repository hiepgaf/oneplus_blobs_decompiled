package android.app.job;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IJobCallback
  extends IInterface
{
  public abstract void acknowledgeStartMessage(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void acknowledgeStopMessage(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void jobFinished(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IJobCallback
  {
    private static final String DESCRIPTOR = "android.app.job.IJobCallback";
    static final int TRANSACTION_acknowledgeStartMessage = 1;
    static final int TRANSACTION_acknowledgeStopMessage = 2;
    static final int TRANSACTION_jobFinished = 3;
    
    public Stub()
    {
      attachInterface(this, "android.app.job.IJobCallback");
    }
    
    public static IJobCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.job.IJobCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IJobCallback))) {
        return (IJobCallback)localIInterface;
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
      boolean bool3 = false;
      boolean bool1 = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.job.IJobCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.job.IJobCallback");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        acknowledgeStartMessage(paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.job.IJobCallback");
        paramInt1 = paramParcel1.readInt();
        bool1 = bool2;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        acknowledgeStopMessage(paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.app.job.IJobCallback");
      paramInt1 = paramParcel1.readInt();
      bool1 = bool3;
      if (paramParcel1.readInt() != 0) {
        bool1 = true;
      }
      jobFinished(paramInt1, bool1);
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IJobCallback
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void acknowledgeStartMessage(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: iload_1
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: iload_2
        //   26: ifeq +43 -> 69
        //   29: iload_3
        //   30: istore_1
        //   31: aload 4
        //   33: iload_1
        //   34: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   37: aload_0
        //   38: getfield 19	android/app/job/IJobCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   41: iconst_1
        //   42: aload 4
        //   44: aload 5
        //   46: iconst_0
        //   47: invokeinterface 46 5 0
        //   52: pop
        //   53: aload 5
        //   55: invokevirtual 49	android/os/Parcel:readException	()V
        //   58: aload 5
        //   60: invokevirtual 52	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 52	android/os/Parcel:recycle	()V
        //   68: return
        //   69: iconst_0
        //   70: istore_1
        //   71: goto -40 -> 31
        //   74: astore 6
        //   76: aload 5
        //   78: invokevirtual 52	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 52	android/os/Parcel:recycle	()V
        //   86: aload 6
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramInt	int
        //   0	89	2	paramBoolean	boolean
        //   1	29	3	i	int
        //   5	77	4	localParcel1	Parcel
        //   10	67	5	localParcel2	Parcel
        //   74	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   12	25	74	finally
        //   31	58	74	finally
      }
      
      public void acknowledgeStopMessage(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.job.IJobCallback");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.job.IJobCallback";
      }
      
      public void jobFinished(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.job.IJobCallback");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/IJobCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */