package android.print;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPrintDocumentAdapter
  extends IInterface
{
  public abstract void finish()
    throws RemoteException;
  
  public abstract void kill(String paramString)
    throws RemoteException;
  
  public abstract void layout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, ILayoutResultCallback paramILayoutResultCallback, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void setObserver(IPrintDocumentAdapterObserver paramIPrintDocumentAdapterObserver)
    throws RemoteException;
  
  public abstract void start()
    throws RemoteException;
  
  public abstract void write(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, IWriteResultCallback paramIWriteResultCallback, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintDocumentAdapter
  {
    private static final String DESCRIPTOR = "android.print.IPrintDocumentAdapter";
    static final int TRANSACTION_finish = 5;
    static final int TRANSACTION_kill = 6;
    static final int TRANSACTION_layout = 3;
    static final int TRANSACTION_setObserver = 1;
    static final int TRANSACTION_start = 2;
    static final int TRANSACTION_write = 4;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintDocumentAdapter");
    }
    
    public static IPrintDocumentAdapter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintDocumentAdapter");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintDocumentAdapter))) {
        return (IPrintDocumentAdapter)localIInterface;
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
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.print.IPrintDocumentAdapter");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
        setObserver(IPrintDocumentAdapterObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
        start();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
        ILayoutResultCallback localILayoutResultCallback;
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrintAttributes)PrintAttributes.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label216;
          }
          localObject = (PrintAttributes)PrintAttributes.CREATOR.createFromParcel(paramParcel1);
          localILayoutResultCallback = ILayoutResultCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() == 0) {
            break label222;
          }
        }
        for (Bundle localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localBundle = null)
        {
          layout(paramParcel2, (PrintAttributes)localObject, localILayoutResultCallback, localBundle, paramParcel1.readInt());
          return true;
          paramParcel2 = null;
          break;
          localObject = null;
          break label164;
        }
      case 4: 
        paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
        localObject = (PageRange[])paramParcel1.createTypedArray(PageRange.CREATOR);
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          write((PageRange[])localObject, paramParcel2, IWriteResultCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        }
      case 5: 
        label164:
        label216:
        label222:
        paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
        finish();
        return true;
      }
      paramParcel1.enforceInterface("android.print.IPrintDocumentAdapter");
      kill(paramParcel1.readString());
      return true;
    }
    
    private static class Proxy
      implements IPrintDocumentAdapter
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
      
      public void finish()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintDocumentAdapter");
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.print.IPrintDocumentAdapter";
      }
      
      public void kill(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintDocumentAdapter");
          localParcel.writeString(paramString);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void layout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, ILayoutResultCallback paramILayoutResultCallback, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintDocumentAdapter");
            if (paramPrintAttributes1 != null)
            {
              localParcel.writeInt(1);
              paramPrintAttributes1.writeToParcel(localParcel, 0);
              if (paramPrintAttributes2 != null)
              {
                localParcel.writeInt(1);
                paramPrintAttributes2.writeToParcel(localParcel, 0);
                paramPrintAttributes1 = (PrintAttributes)localObject;
                if (paramILayoutResultCallback != null) {
                  paramPrintAttributes1 = paramILayoutResultCallback.asBinder();
                }
                localParcel.writeStrongBinder(paramPrintAttributes1);
                if (paramBundle == null) {
                  break label142;
                }
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt);
                this.mRemote.transact(3, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          continue;
          label142:
          localParcel.writeInt(0);
        }
      }
      
      public void setObserver(IPrintDocumentAdapterObserver paramIPrintDocumentAdapterObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintDocumentAdapter");
          if (paramIPrintDocumentAdapterObserver != null) {
            localIBinder = paramIPrintDocumentAdapterObserver.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void start()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintDocumentAdapter");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void write(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, IWriteResultCallback paramIWriteResultCallback, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 6
        //   17: aload_1
        //   18: iconst_0
        //   19: invokevirtual 87	android/os/Parcel:writeTypedArray	([Landroid/os/Parcelable;I)V
        //   22: aload_2
        //   23: ifnull +64 -> 87
        //   26: aload 6
        //   28: iconst_1
        //   29: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   32: aload_2
        //   33: aload 6
        //   35: iconst_0
        //   36: invokevirtual 90	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
        //   39: aload 5
        //   41: astore_1
        //   42: aload_3
        //   43: ifnull +10 -> 53
        //   46: aload_3
        //   47: invokeinterface 93 1 0
        //   52: astore_1
        //   53: aload 6
        //   55: aload_1
        //   56: invokevirtual 72	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   59: aload 6
        //   61: iload 4
        //   63: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   66: aload_0
        //   67: getfield 19	android/print/IPrintDocumentAdapter$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   70: iconst_4
        //   71: aload 6
        //   73: aconst_null
        //   74: iconst_1
        //   75: invokeinterface 43 5 0
        //   80: pop
        //   81: aload 6
        //   83: invokevirtual 46	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 6
        //   89: iconst_0
        //   90: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   93: goto -54 -> 39
        //   96: astore_1
        //   97: aload 6
        //   99: invokevirtual 46	android/os/Parcel:recycle	()V
        //   102: aload_1
        //   103: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	104	0	this	Proxy
        //   0	104	1	paramArrayOfPageRange	PageRange[]
        //   0	104	2	paramParcelFileDescriptor	ParcelFileDescriptor
        //   0	104	3	paramIWriteResultCallback	IWriteResultCallback
        //   0	104	4	paramInt	int
        //   1	39	5	localObject	Object
        //   6	92	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	22	96	finally
        //   26	39	96	finally
        //   46	53	96	finally
        //   53	81	96	finally
        //   87	93	96	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintDocumentAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */