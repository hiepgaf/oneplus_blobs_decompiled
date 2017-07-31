package android.print;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.printservice.PrintServiceInfo;
import android.printservice.recommendation.IRecommendationsChangeListener;
import android.printservice.recommendation.IRecommendationsChangeListener.Stub;
import android.printservice.recommendation.RecommendationInfo;
import java.util.ArrayList;
import java.util.List;

public abstract interface IPrintManager
  extends IInterface
{
  public abstract void addPrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener, int paramInt)
    throws RemoteException;
  
  public abstract void addPrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener, int paramInt)
    throws RemoteException;
  
  public abstract void cancelPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void createPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
    throws RemoteException;
  
  public abstract void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
    throws RemoteException;
  
  public abstract Icon getCustomPrinterIcon(PrinterId paramPrinterId, int paramInt)
    throws RemoteException;
  
  public abstract PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract List<PrintJobInfo> getPrintJobInfos(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract List<RecommendationInfo> getPrintServiceRecommendations(int paramInt)
    throws RemoteException;
  
  public abstract List<PrintServiceInfo> getPrintServices(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract Bundle print(String paramString1, IPrintDocumentAdapter paramIPrintDocumentAdapter, PrintAttributes paramPrintAttributes, String paramString2, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void removePrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt)
    throws RemoteException;
  
  public abstract void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener, int paramInt)
    throws RemoteException;
  
  public abstract void removePrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener, int paramInt)
    throws RemoteException;
  
  public abstract void restartPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setPrintServiceEnabled(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void startPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> paramList, int paramInt)
    throws RemoteException;
  
  public abstract void startPrinterStateTracking(PrinterId paramPrinterId, int paramInt)
    throws RemoteException;
  
  public abstract void stopPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
    throws RemoteException;
  
  public abstract void stopPrinterStateTracking(PrinterId paramPrinterId, int paramInt)
    throws RemoteException;
  
  public abstract void validatePrinters(List<PrinterId> paramList, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintManager
  {
    private static final String DESCRIPTOR = "android.print.IPrintManager";
    static final int TRANSACTION_addPrintJobStateChangeListener = 6;
    static final int TRANSACTION_addPrintServiceRecommendationsChangeListener = 12;
    static final int TRANSACTION_addPrintServicesChangeListener = 8;
    static final int TRANSACTION_cancelPrintJob = 4;
    static final int TRANSACTION_createPrinterDiscoverySession = 15;
    static final int TRANSACTION_destroyPrinterDiscoverySession = 22;
    static final int TRANSACTION_getCustomPrinterIcon = 20;
    static final int TRANSACTION_getPrintJobInfo = 2;
    static final int TRANSACTION_getPrintJobInfos = 1;
    static final int TRANSACTION_getPrintServiceRecommendations = 14;
    static final int TRANSACTION_getPrintServices = 10;
    static final int TRANSACTION_print = 3;
    static final int TRANSACTION_removePrintJobStateChangeListener = 7;
    static final int TRANSACTION_removePrintServiceRecommendationsChangeListener = 13;
    static final int TRANSACTION_removePrintServicesChangeListener = 9;
    static final int TRANSACTION_restartPrintJob = 5;
    static final int TRANSACTION_setPrintServiceEnabled = 11;
    static final int TRANSACTION_startPrinterDiscovery = 16;
    static final int TRANSACTION_startPrinterStateTracking = 19;
    static final int TRANSACTION_stopPrinterDiscovery = 17;
    static final int TRANSACTION_stopPrinterStateTracking = 21;
    static final int TRANSACTION_validatePrinters = 18;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintManager");
    }
    
    public static IPrintManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintManager");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintManager))) {
        return (IPrintManager)localIInterface;
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
        paramParcel2.writeString("android.print.IPrintManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        paramParcel1 = getPrintJobInfos(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getPrintJobInfo((PrintJobId)localObject, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label313;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        String str = paramParcel1.readString();
        IPrintDocumentAdapter localIPrintDocumentAdapter = IPrintDocumentAdapter.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintAttributes)PrintAttributes.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = print(str, localIPrintDocumentAdapter, (PrintAttributes)localObject, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label413;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 4: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          cancelPrintJob((PrintJobId)localObject, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          restartPrintJob((PrintJobId)localObject, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        addPrintJobStateChangeListener(IPrintJobStateChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        removePrintJobStateChangeListener(IPrintJobStateChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        addPrintServicesChangeListener(IPrintServicesChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        removePrintServicesChangeListener(IPrintServicesChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        paramParcel1 = getPrintServices(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label730;
          }
        }
        for (boolean bool = true;; bool = false)
        {
          setPrintServiceEnabled((ComponentName)localObject, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 12: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        paramParcel1 = getPrintServiceRecommendations(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        createPrinterDiscoverySession(IPrinterDiscoveryObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        startPrinterDiscovery(IPrinterDiscoveryObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createTypedArrayList(PrinterId.CREATOR), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        stopPrinterDiscovery(IPrinterDiscoveryObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        validatePrinters(paramParcel1.createTypedArrayList(PrinterId.CREATOR), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          startPrinterStateTracking((PrinterId)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getCustomPrinterIcon((PrinterId)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1045;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 21: 
        label313:
        label413:
        label730:
        label1045:
        paramParcel1.enforceInterface("android.print.IPrintManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          stopPrinterStateTracking((PrinterId)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.print.IPrintManager");
      destroyPrinterDiscoverySession(IPrinterDiscoveryObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IPrintManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addPrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt1, int paramInt2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrintJobStateChangeListener != null) {
            localIBinder = paramIPrintJobStateChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIRecommendationsChangeListener != null) {
            localIBinder = paramIRecommendationsChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addPrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrintServicesChangeListener != null) {
            localIBinder = paramIPrintServicesChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public void cancelPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 80	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: iload_2
        //   37: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/print/IPrintManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_4
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 55 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 58	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 61	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramPrintJobId	PrintJobId
        //   0	100	2	paramInt1	int
        //   0	100	3	paramInt2	int
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public void createPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrinterDiscoveryObserver != null) {
            localIBinder = paramIPrinterDiscoveryObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrinterDiscoveryObserver != null) {
            localIBinder = paramIPrinterDiscoveryObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Icon getCustomPrinterIcon(PrinterId paramPrinterId, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.print.IPrintManager");
            if (paramPrinterId != null)
            {
              localParcel1.writeInt(1);
              paramPrinterId.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(20, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramPrinterId = (Icon)Icon.CREATOR.createFromParcel(localParcel2);
                return paramPrinterId;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramPrinterId = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.print.IPrintManager";
      }
      
      public PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.print.IPrintManager");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramPrintJobId = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(localParcel2);
                return paramPrintJobId;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramPrintJobId = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public List<PrintJobInfo> getPrintJobInfos(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(PrintJobInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<RecommendationInfo> getPrintServiceRecommendations(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(RecommendationInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<PrintServiceInfo> getPrintServices(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(PrintServiceInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Bundle print(String paramString1, IPrintDocumentAdapter paramIPrintDocumentAdapter, PrintAttributes paramPrintAttributes, String paramString2, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.print.IPrintManager");
            localParcel1.writeString(paramString1);
            paramString1 = (String)localObject;
            if (paramIPrintDocumentAdapter != null) {
              paramString1 = paramIPrintDocumentAdapter.asBinder();
            }
            localParcel1.writeStrongBinder(paramString1);
            if (paramPrintAttributes != null)
            {
              localParcel1.writeInt(1);
              paramPrintAttributes.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString2);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString1 = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
                return paramString1;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString1 = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void removePrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrintJobStateChangeListener != null) {
            localIBinder = paramIPrintJobStateChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIRecommendationsChangeListener != null) {
            localIBinder = paramIRecommendationsChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removePrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrintServicesChangeListener != null) {
            localIBinder = paramIPrintServicesChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void restartPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 80	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: iload_2
        //   37: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/print/IPrintManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_5
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 55 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 58	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 61	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramPrintJobId	PrintJobId
        //   0	100	2	paramInt1	int
        //   0	100	3	paramInt2	int
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public void setPrintServiceEnabled(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.print.IPrintManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label114;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(11, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label114:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void startPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> paramList, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrinterDiscoveryObserver != null) {
            localIBinder = paramIPrinterDiscoveryObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeTypedList(paramList);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void startPrinterStateTracking(PrinterId paramPrinterId, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 91	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/print/IPrintManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 19
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramPrinterId	PrinterId
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void stopPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          if (paramIPrinterDiscoveryObserver != null) {
            localIBinder = paramIPrinterDiscoveryObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void stopPrinterStateTracking(PrinterId paramPrinterId, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 91	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/print/IPrintManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 21
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramPrinterId	PrinterId
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void validatePrinters(List<PrinterId> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.print.IPrintManager");
          localParcel1.writeTypedList(paramList);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */