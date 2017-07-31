package android.print;

import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IPrintSpoolerCallbacks
  extends IInterface
{
  public abstract void customPrinterIconCacheCleared(int paramInt)
    throws RemoteException;
  
  public abstract void onCancelPrintJobResult(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void onCustomPrinterIconCached(int paramInt)
    throws RemoteException;
  
  public abstract void onGetCustomPrinterIconResult(Icon paramIcon, int paramInt)
    throws RemoteException;
  
  public abstract void onGetPrintJobInfoResult(PrintJobInfo paramPrintJobInfo, int paramInt)
    throws RemoteException;
  
  public abstract void onGetPrintJobInfosResult(List<PrintJobInfo> paramList, int paramInt)
    throws RemoteException;
  
  public abstract void onSetPrintJobStateResult(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void onSetPrintJobTagResult(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintSpoolerCallbacks
  {
    private static final String DESCRIPTOR = "android.print.IPrintSpoolerCallbacks";
    static final int TRANSACTION_customPrinterIconCacheCleared = 8;
    static final int TRANSACTION_onCancelPrintJobResult = 2;
    static final int TRANSACTION_onCustomPrinterIconCached = 7;
    static final int TRANSACTION_onGetCustomPrinterIconResult = 6;
    static final int TRANSACTION_onGetPrintJobInfoResult = 5;
    static final int TRANSACTION_onGetPrintJobInfosResult = 1;
    static final int TRANSACTION_onSetPrintJobStateResult = 3;
    static final int TRANSACTION_onSetPrintJobTagResult = 4;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintSpoolerCallbacks");
    }
    
    public static IPrintSpoolerCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintSpoolerCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintSpoolerCallbacks))) {
        return (IPrintSpoolerCallbacks)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.print.IPrintSpoolerCallbacks");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        onGetPrintJobInfosResult(paramParcel1.createTypedArrayList(PrintJobInfo.CREATOR), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onCancelPrintJobResult(bool, paramParcel1.readInt());
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onSetPrintJobStateResult(bool, paramParcel1.readInt());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onSetPrintJobTagResult(bool, paramParcel1.readInt());
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onGetPrintJobInfoResult(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Icon)Icon.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onGetCustomPrinterIconResult(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
        onCustomPrinterIconCached(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.print.IPrintSpoolerCallbacks");
      customPrinterIconCacheCleared(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IPrintSpoolerCallbacks
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
      
      public void customPrinterIconCacheCleared(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpoolerCallbacks");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.print.IPrintSpoolerCallbacks";
      }
      
      /* Error */
      public void onCancelPrintJobResult(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +36 -> 51
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: iload_2
        //   27: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpoolerCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 50	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -35 -> 18
        //   56: astore 5
        //   58: aload 4
        //   60: invokevirtual 50	android/os/Parcel:recycle	()V
        //   63: aload 5
        //   65: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	66	0	this	Proxy
        //   0	66	1	paramBoolean	boolean
        //   0	66	2	paramInt	int
        //   1	52	3	i	int
        //   5	54	4	localParcel	Parcel
        //   56	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	14	56	finally
        //   18	45	56	finally
      }
      
      public void onCustomPrinterIconCached(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpoolerCallbacks");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onGetCustomPrinterIconResult(Icon paramIcon, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 64	android/graphics/drawable/Icon:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpoolerCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 6
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 50	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 50	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramIcon	Icon
        //   0	65	2	paramInt	int
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void onGetPrintJobInfoResult(PrintJobInfo paramPrintJobInfo, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 69	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpoolerCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 47 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 50	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 50	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramPrintJobInfo	PrintJobInfo
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void onGetPrintJobInfosResult(List<PrintJobInfo> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpoolerCallbacks");
          localParcel.writeTypedList(paramList);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onSetPrintJobStateResult(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +36 -> 51
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: iload_2
        //   27: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpoolerCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 50	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -35 -> 18
        //   56: astore 5
        //   58: aload 4
        //   60: invokevirtual 50	android/os/Parcel:recycle	()V
        //   63: aload 5
        //   65: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	66	0	this	Proxy
        //   0	66	1	paramBoolean	boolean
        //   0	66	2	paramInt	int
        //   1	52	3	i	int
        //   5	54	4	localParcel	Parcel
        //   56	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	14	56	finally
        //   18	45	56	finally
      }
      
      /* Error */
      public void onSetPrintJobTagResult(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +36 -> 51
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: iload_2
        //   27: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpoolerCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_4
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 50	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -35 -> 18
        //   56: astore 5
        //   58: aload 4
        //   60: invokevirtual 50	android/os/Parcel:recycle	()V
        //   63: aload 5
        //   65: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	66	0	this	Proxy
        //   0	66	1	paramBoolean	boolean
        //   0	66	2	paramInt	int
        //   1	52	3	i	int
        //   5	54	4	localParcel	Parcel
        //   56	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	14	56	finally
        //   18	45	56	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintSpoolerCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */