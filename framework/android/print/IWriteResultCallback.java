package android.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ICancellationSignal.Stub;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;

public abstract interface IWriteResultCallback
  extends IInterface
{
  public abstract void onWriteCanceled(int paramInt)
    throws RemoteException;
  
  public abstract void onWriteFailed(CharSequence paramCharSequence, int paramInt)
    throws RemoteException;
  
  public abstract void onWriteFinished(PageRange[] paramArrayOfPageRange, int paramInt)
    throws RemoteException;
  
  public abstract void onWriteStarted(ICancellationSignal paramICancellationSignal, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWriteResultCallback
  {
    private static final String DESCRIPTOR = "android.print.IWriteResultCallback";
    static final int TRANSACTION_onWriteCanceled = 4;
    static final int TRANSACTION_onWriteFailed = 3;
    static final int TRANSACTION_onWriteFinished = 2;
    static final int TRANSACTION_onWriteStarted = 1;
    
    public Stub()
    {
      attachInterface(this, "android.print.IWriteResultCallback");
    }
    
    public static IWriteResultCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IWriteResultCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IWriteResultCallback))) {
        return (IWriteResultCallback)localIInterface;
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
        paramParcel2.writeString("android.print.IWriteResultCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IWriteResultCallback");
        onWriteStarted(ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.IWriteResultCallback");
        onWriteFinished((PageRange[])paramParcel1.createTypedArray(PageRange.CREATOR), paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.print.IWriteResultCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onWriteFailed(paramParcel2, paramParcel1.readInt());
          return true;
        }
      }
      paramParcel1.enforceInterface("android.print.IWriteResultCallback");
      onWriteCanceled(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IWriteResultCallback
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
        return "android.print.IWriteResultCallback";
      }
      
      public void onWriteCanceled(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IWriteResultCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onWriteFailed(CharSequence paramCharSequence, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokestatic 61	android/text/TextUtils:writeToParcel	(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IWriteResultCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 52	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramCharSequence	CharSequence
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void onWriteFinished(PageRange[] paramArrayOfPageRange, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IWriteResultCallback");
          localParcel.writeTypedArray(paramArrayOfPageRange, 0);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onWriteStarted(ICancellationSignal paramICancellationSignal, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IWriteResultCallback");
          if (paramICancellationSignal != null) {
            localIBinder = paramICancellationSignal.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IWriteResultCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */