package android.printservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import java.util.List;

public abstract interface IPrintService
  extends IInterface
{
  public abstract void createPrinterDiscoverySession()
    throws RemoteException;
  
  public abstract void destroyPrinterDiscoverySession()
    throws RemoteException;
  
  public abstract void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
    throws RemoteException;
  
  public abstract void requestCancelPrintJob(PrintJobInfo paramPrintJobInfo)
    throws RemoteException;
  
  public abstract void requestCustomPrinterIcon(PrinterId paramPrinterId)
    throws RemoteException;
  
  public abstract void setClient(IPrintServiceClient paramIPrintServiceClient)
    throws RemoteException;
  
  public abstract void startPrinterDiscovery(List<PrinterId> paramList)
    throws RemoteException;
  
  public abstract void startPrinterStateTracking(PrinterId paramPrinterId)
    throws RemoteException;
  
  public abstract void stopPrinterDiscovery()
    throws RemoteException;
  
  public abstract void stopPrinterStateTracking(PrinterId paramPrinterId)
    throws RemoteException;
  
  public abstract void validatePrinters(List<PrinterId> paramList)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintService
  {
    private static final String DESCRIPTOR = "android.printservice.IPrintService";
    static final int TRANSACTION_createPrinterDiscoverySession = 4;
    static final int TRANSACTION_destroyPrinterDiscoverySession = 11;
    static final int TRANSACTION_onPrintJobQueued = 3;
    static final int TRANSACTION_requestCancelPrintJob = 2;
    static final int TRANSACTION_requestCustomPrinterIcon = 9;
    static final int TRANSACTION_setClient = 1;
    static final int TRANSACTION_startPrinterDiscovery = 5;
    static final int TRANSACTION_startPrinterStateTracking = 8;
    static final int TRANSACTION_stopPrinterDiscovery = 6;
    static final int TRANSACTION_stopPrinterStateTracking = 10;
    static final int TRANSACTION_validatePrinters = 7;
    
    public Stub()
    {
      attachInterface(this, "android.printservice.IPrintService");
    }
    
    public static IPrintService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.printservice.IPrintService");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintService))) {
        return (IPrintService)localIInterface;
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
        paramParcel2.writeString("android.printservice.IPrintService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        setClient(IPrintServiceClient.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestCancelPrintJob(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrintJobQueued(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        createPrinterDiscoverySession();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        startPrinterDiscovery(paramParcel1.createTypedArrayList(PrinterId.CREATOR));
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        stopPrinterDiscovery();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        validatePrinters(paramParcel1.createTypedArrayList(PrinterId.CREATOR));
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startPrinterStateTracking(paramParcel1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestCustomPrinterIcon(paramParcel1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.printservice.IPrintService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          stopPrinterStateTracking(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.printservice.IPrintService");
      destroyPrinterDiscoverySession();
      return true;
    }
    
    private static class Proxy
      implements IPrintService
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
      
      public void createPrinterDiscoverySession()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void destroyPrinterDiscoverySession()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.printservice.IPrintService";
      }
      
      /* Error */
      public void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 62	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/printservice/IPrintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramPrintJobInfo	PrintJobInfo
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void requestCancelPrintJob(PrintJobInfo paramPrintJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 62	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/printservice/IPrintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramPrintJobInfo	PrintJobInfo
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void requestCustomPrinterIcon(PrinterId paramPrinterId)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/printservice/IPrintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 9
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 43 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 46	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 46	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramPrinterId	PrinterId
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void setClient(IPrintServiceClient paramIPrintServiceClient)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          if (paramIPrintServiceClient != null) {
            localIBinder = paramIPrintServiceClient.asBinder();
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
      
      public void startPrinterDiscovery(List<PrinterId> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void startPrinterStateTracking(PrinterId paramPrinterId)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/printservice/IPrintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 43 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 46	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 46	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramPrinterId	PrinterId
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void stopPrinterDiscovery()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void stopPrinterStateTracking(PrinterId paramPrinterId)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/printservice/IPrintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 10
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 43 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 46	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 46	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramPrinterId	PrinterId
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void validatePrinters(List<PrinterId> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.printservice.IPrintService");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(7, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/IPrintService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */