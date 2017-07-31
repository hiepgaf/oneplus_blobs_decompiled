package android.content;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ISyncContext
  extends IInterface
{
  public abstract void onFinished(SyncResult paramSyncResult)
    throws RemoteException;
  
  public abstract void sendHeartbeat()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISyncContext
  {
    private static final String DESCRIPTOR = "android.content.ISyncContext";
    static final int TRANSACTION_onFinished = 2;
    static final int TRANSACTION_sendHeartbeat = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.ISyncContext");
    }
    
    public static ISyncContext asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.ISyncContext");
      if ((localIInterface != null) && ((localIInterface instanceof ISyncContext))) {
        return (ISyncContext)localIInterface;
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
        paramParcel2.writeString("android.content.ISyncContext");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.ISyncContext");
        sendHeartbeat();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.content.ISyncContext");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (SyncResult)SyncResult.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onFinished(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements ISyncContext
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
        return "android.content.ISyncContext";
      }
      
      /* Error */
      public void onFinished(SyncResult paramSyncResult)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 50	android/content/SyncResult:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/content/ISyncContext$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_2
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 56 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 59	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 62	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 62	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 62	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramSyncResult	SyncResult
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      public void sendHeartbeat()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.ISyncContext");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ISyncContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */