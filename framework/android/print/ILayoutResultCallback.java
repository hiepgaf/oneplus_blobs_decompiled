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

public abstract interface ILayoutResultCallback
  extends IInterface
{
  public abstract void onLayoutCanceled(int paramInt)
    throws RemoteException;
  
  public abstract void onLayoutFailed(CharSequence paramCharSequence, int paramInt)
    throws RemoteException;
  
  public abstract void onLayoutFinished(PrintDocumentInfo paramPrintDocumentInfo, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void onLayoutStarted(ICancellationSignal paramICancellationSignal, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ILayoutResultCallback
  {
    private static final String DESCRIPTOR = "android.print.ILayoutResultCallback";
    static final int TRANSACTION_onLayoutCanceled = 4;
    static final int TRANSACTION_onLayoutFailed = 3;
    static final int TRANSACTION_onLayoutFinished = 2;
    static final int TRANSACTION_onLayoutStarted = 1;
    
    public Stub()
    {
      attachInterface(this, "android.print.ILayoutResultCallback");
    }
    
    public static ILayoutResultCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.ILayoutResultCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ILayoutResultCallback))) {
        return (ILayoutResultCallback)localIInterface;
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
        paramParcel2.writeString("android.print.ILayoutResultCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.ILayoutResultCallback");
        onLayoutStarted(ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.ILayoutResultCallback");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrintDocumentInfo)PrintDocumentInfo.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label147;
          }
        }
        for (boolean bool = true;; bool = false)
        {
          onLayoutFinished(paramParcel2, bool, paramParcel1.readInt());
          return true;
          paramParcel2 = null;
          break;
        }
      case 3: 
        label147:
        paramParcel1.enforceInterface("android.print.ILayoutResultCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onLayoutFailed(paramParcel2, paramParcel1.readInt());
          return true;
        }
      }
      paramParcel1.enforceInterface("android.print.ILayoutResultCallback");
      onLayoutCanceled(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ILayoutResultCallback
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
        return "android.print.ILayoutResultCallback";
      }
      
      public void onLayoutCanceled(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.ILayoutResultCallback");
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
      public void onLayoutFailed(CharSequence paramCharSequence, int paramInt)
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
        //   31: getfield 19	android/print/ILayoutResultCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
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
      
      public void onLayoutFinished(PrintDocumentInfo paramPrintDocumentInfo, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.ILayoutResultCallback");
            if (paramPrintDocumentInfo != null)
            {
              localParcel.writeInt(1);
              paramPrintDocumentInfo.writeToParcel(localParcel, 0);
              break label92;
              localParcel.writeInt(i);
              localParcel.writeInt(paramInt);
              this.mRemote.transact(2, localParcel, null, 1);
            }
            else
            {
              localParcel.writeInt(0);
            }
          }
          finally
          {
            localParcel.recycle();
          }
          label92:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void onLayoutStarted(ICancellationSignal paramICancellationSignal, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.ILayoutResultCallback");
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/ILayoutResultCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */