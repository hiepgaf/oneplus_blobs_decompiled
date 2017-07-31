package android.print;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.List;

public abstract interface IPrintSpooler
  extends IInterface
{
  public abstract void clearCustomPrinterIconCache(IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
    throws RemoteException;
  
  public abstract void createPrintJob(PrintJobInfo paramPrintJobInfo)
    throws RemoteException;
  
  public abstract void getCustomPrinterIcon(PrinterId paramPrinterId, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
    throws RemoteException;
  
  public abstract void getPrintJobInfo(PrintJobId paramPrintJobId, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void getPrintJobInfos(IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
    throws RemoteException;
  
  public abstract void pruneApprovedPrintServices(List<ComponentName> paramList)
    throws RemoteException;
  
  public abstract void removeObsoletePrintJobs()
    throws RemoteException;
  
  public abstract void setClient(IPrintSpoolerClient paramIPrintSpoolerClient)
    throws RemoteException;
  
  public abstract void setPrintJobCancelling(PrintJobId paramPrintJobId, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPrintJobState(PrintJobId paramPrintJobId, int paramInt1, String paramString, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt2)
    throws RemoteException;
  
  public abstract void setPrintJobTag(PrintJobId paramPrintJobId, String paramString, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
    throws RemoteException;
  
  public abstract void setProgress(PrintJobId paramPrintJobId, float paramFloat)
    throws RemoteException;
  
  public abstract void setStatus(PrintJobId paramPrintJobId, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void setStatusRes(PrintJobId paramPrintJobId, int paramInt, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void writePrintJobData(ParcelFileDescriptor paramParcelFileDescriptor, PrintJobId paramPrintJobId)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintSpooler
  {
    private static final String DESCRIPTOR = "android.print.IPrintSpooler";
    static final int TRANSACTION_clearCustomPrinterIconCache = 11;
    static final int TRANSACTION_createPrintJob = 4;
    static final int TRANSACTION_getCustomPrinterIcon = 10;
    static final int TRANSACTION_getPrintJobInfo = 3;
    static final int TRANSACTION_getPrintJobInfos = 2;
    static final int TRANSACTION_onCustomPrinterIconLoaded = 9;
    static final int TRANSACTION_pruneApprovedPrintServices = 16;
    static final int TRANSACTION_removeObsoletePrintJobs = 1;
    static final int TRANSACTION_setClient = 14;
    static final int TRANSACTION_setPrintJobCancelling = 15;
    static final int TRANSACTION_setPrintJobState = 5;
    static final int TRANSACTION_setPrintJobTag = 12;
    static final int TRANSACTION_setProgress = 6;
    static final int TRANSACTION_setStatus = 7;
    static final int TRANSACTION_setStatusRes = 8;
    static final int TRANSACTION_writePrintJobData = 13;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintSpooler");
    }
    
    public static IPrintSpooler asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintSpooler");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintSpooler))) {
        return (IPrintSpooler)localIInterface;
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
        paramParcel2.writeString("android.print.IPrintSpooler");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        removeObsoletePrintJobs();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        localObject = IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          getPrintJobInfos((IPrintSpoolerCallbacks)localObject, paramParcel2, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          getPrintJobInfo(paramParcel2, IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          createPrintJob(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          setPrintJobState(paramParcel2, paramParcel1.readInt(), paramParcel1.readString(), IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          setProgress(paramParcel2, paramParcel1.readFloat());
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label488;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setStatus(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label558;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setStatusRes(paramParcel2, paramInt1, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label635;
          }
        }
        for (localObject = (Icon)Icon.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          onCustomPrinterIconLoaded(paramParcel2, (Icon)localObject, IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
          paramParcel2 = null;
          break;
        }
      case 10: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          getCustomPrinterIcon(paramParcel2, IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        clearCustomPrinterIconCache(IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          setPrintJobTag(paramParcel2, paramParcel1.readString(), IPrintSpoolerCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label825;
          }
        }
        for (paramParcel1 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          writePrintJobData(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 14: 
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        setClient(IPrintSpoolerClient.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 15: 
        label488:
        label558:
        label635:
        label825:
        paramParcel1.enforceInterface("android.print.IPrintSpooler");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label899;
          }
        }
        label899:
        for (boolean bool = true;; bool = false)
        {
          setPrintJobCancelling(paramParcel2, bool);
          return true;
          paramParcel2 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.print.IPrintSpooler");
      pruneApprovedPrintServices(paramParcel1.createTypedArrayList(ComponentName.CREATOR));
      return true;
    }
    
    private static class Proxy
      implements IPrintSpooler
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
      
      public void clearCustomPrinterIconCache(IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpooler");
          if (paramIPrintSpoolerCallbacks != null) {
            localIBinder = paramIPrintSpoolerCallbacks.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void createPrintJob(PrintJobInfo paramPrintJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 67	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 55 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 58	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
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
      public void getCustomPrinterIcon(PrinterId paramPrinterId, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +64 -> 80
        //   19: aload 5
        //   21: iconst_1
        //   22: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 5
        //   28: iconst_0
        //   29: invokevirtual 72	android/print/PrinterId:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 4
        //   34: astore_1
        //   35: aload_2
        //   36: ifnull +10 -> 46
        //   39: aload_2
        //   40: invokeinterface 42 1 0
        //   45: astore_1
        //   46: aload 5
        //   48: aload_1
        //   49: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   52: aload 5
        //   54: iload_3
        //   55: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   58: aload_0
        //   59: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: bipush 10
        //   64: aload 5
        //   66: aconst_null
        //   67: iconst_1
        //   68: invokeinterface 55 5 0
        //   73: pop
        //   74: aload 5
        //   76: invokevirtual 58	android/os/Parcel:recycle	()V
        //   79: return
        //   80: aload 5
        //   82: iconst_0
        //   83: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   86: goto -54 -> 32
        //   89: astore_1
        //   90: aload 5
        //   92: invokevirtual 58	android/os/Parcel:recycle	()V
        //   95: aload_1
        //   96: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	97	0	this	Proxy
        //   0	97	1	paramPrinterId	PrinterId
        //   0	97	2	paramIPrintSpoolerCallbacks	IPrintSpoolerCallbacks
        //   0	97	3	paramInt	int
        //   1	32	4	localObject	Object
        //   6	85	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	89	finally
        //   19	32	89	finally
        //   39	46	89	finally
        //   46	74	89	finally
        //   80	86	89	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.print.IPrintSpooler";
      }
      
      /* Error */
      public void getPrintJobInfo(PrintJobId paramPrintJobId, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +70 -> 86
        //   19: aload 6
        //   21: iconst_1
        //   22: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 6
        //   28: iconst_0
        //   29: invokevirtual 79	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 5
        //   34: astore_1
        //   35: aload_2
        //   36: ifnull +10 -> 46
        //   39: aload_2
        //   40: invokeinterface 42 1 0
        //   45: astore_1
        //   46: aload 6
        //   48: aload_1
        //   49: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   52: aload 6
        //   54: iload_3
        //   55: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   58: aload 6
        //   60: iload 4
        //   62: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   65: aload_0
        //   66: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   69: iconst_3
        //   70: aload 6
        //   72: aconst_null
        //   73: iconst_1
        //   74: invokeinterface 55 5 0
        //   79: pop
        //   80: aload 6
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 6
        //   88: iconst_0
        //   89: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   92: goto -60 -> 32
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 58	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramPrintJobId	PrintJobId
        //   0	103	2	paramIPrintSpoolerCallbacks	IPrintSpoolerCallbacks
        //   0	103	3	paramInt1	int
        //   0	103	4	paramInt2	int
        //   1	32	5	localObject	Object
        //   6	91	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	95	finally
        //   19	32	95	finally
        //   39	46	95	finally
        //   46	80	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public void getPrintJobInfos(IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 6
        //   27: aload 7
        //   29: aload 6
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload_2
        //   35: ifnull +57 -> 92
        //   38: aload 7
        //   40: iconst_1
        //   41: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   44: aload_2
        //   45: aload 7
        //   47: iconst_0
        //   48: invokevirtual 84	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   51: aload 7
        //   53: iload_3
        //   54: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   57: aload 7
        //   59: iload 4
        //   61: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   64: aload 7
        //   66: iload 5
        //   68: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   71: aload_0
        //   72: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   75: iconst_2
        //   76: aload 7
        //   78: aconst_null
        //   79: iconst_1
        //   80: invokeinterface 55 5 0
        //   85: pop
        //   86: aload 7
        //   88: invokevirtual 58	android/os/Parcel:recycle	()V
        //   91: return
        //   92: aload 7
        //   94: iconst_0
        //   95: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   98: goto -47 -> 51
        //   101: astore_1
        //   102: aload 7
        //   104: invokevirtual 58	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramIPrintSpoolerCallbacks	IPrintSpoolerCallbacks
        //   0	109	2	paramComponentName	ComponentName
        //   0	109	3	paramInt1	int
        //   0	109	4	paramInt2	int
        //   0	109	5	paramInt3	int
        //   1	29	6	localIBinder	IBinder
        //   6	97	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	101	finally
        //   19	27	101	finally
        //   27	34	101	finally
        //   38	51	101	finally
        //   51	86	101	finally
        //   92	98	101	finally
      }
      
      public void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintSpooler");
            if (paramPrinterId != null)
            {
              localParcel.writeInt(1);
              paramPrinterId.writeToParcel(localParcel, 0);
              if (paramIcon != null)
              {
                localParcel.writeInt(1);
                paramIcon.writeToParcel(localParcel, 0);
                paramPrinterId = (PrinterId)localObject;
                if (paramIPrintSpoolerCallbacks != null) {
                  paramPrinterId = paramIPrintSpoolerCallbacks.asBinder();
                }
                localParcel.writeStrongBinder(paramPrinterId);
                localParcel.writeInt(paramInt);
                this.mRemote.transact(9, localParcel, null, 1);
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
        }
      }
      
      public void pruneApprovedPrintServices(List<ComponentName> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpooler");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(16, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeObsoletePrintJobs()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpooler");
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setClient(IPrintSpoolerClient paramIPrintSpoolerClient)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpooler");
          if (paramIPrintSpoolerClient != null) {
            localIBinder = paramIPrintSpoolerClient.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(14, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setPrintJobCancelling(PrintJobId paramPrintJobId, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintSpooler");
            if (paramPrintJobId != null)
            {
              localParcel.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel, 0);
              break label84;
              localParcel.writeInt(i);
              this.mRemote.transact(15, localParcel, null, 1);
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
          label84:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public void setPrintJobState(PrintJobId paramPrintJobId, int paramInt1, String paramString, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +78 -> 94
        //   19: aload 7
        //   21: iconst_1
        //   22: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 7
        //   28: iconst_0
        //   29: invokevirtual 79	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 7
        //   34: iload_2
        //   35: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   38: aload 7
        //   40: aload_3
        //   41: invokevirtual 109	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   44: aload 6
        //   46: astore_1
        //   47: aload 4
        //   49: ifnull +11 -> 60
        //   52: aload 4
        //   54: invokeinterface 42 1 0
        //   59: astore_1
        //   60: aload 7
        //   62: aload_1
        //   63: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   66: aload 7
        //   68: iload 5
        //   70: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   73: aload_0
        //   74: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   77: iconst_5
        //   78: aload 7
        //   80: aconst_null
        //   81: iconst_1
        //   82: invokeinterface 55 5 0
        //   87: pop
        //   88: aload 7
        //   90: invokevirtual 58	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 7
        //   96: iconst_0
        //   97: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   100: goto -68 -> 32
        //   103: astore_1
        //   104: aload 7
        //   106: invokevirtual 58	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramPrintJobId	PrintJobId
        //   0	111	2	paramInt1	int
        //   0	111	3	paramString	String
        //   0	111	4	paramIPrintSpoolerCallbacks	IPrintSpoolerCallbacks
        //   0	111	5	paramInt2	int
        //   1	44	6	localObject	Object
        //   6	99	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	103	finally
        //   19	32	103	finally
        //   32	44	103	finally
        //   52	60	103	finally
        //   60	88	103	finally
        //   94	100	103	finally
      }
      
      /* Error */
      public void setPrintJobTag(PrintJobId paramPrintJobId, String paramString, IPrintSpoolerCallbacks paramIPrintSpoolerCallbacks, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +71 -> 87
        //   19: aload 6
        //   21: iconst_1
        //   22: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 6
        //   28: iconst_0
        //   29: invokevirtual 79	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 6
        //   34: aload_2
        //   35: invokevirtual 109	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   38: aload 5
        //   40: astore_1
        //   41: aload_3
        //   42: ifnull +10 -> 52
        //   45: aload_3
        //   46: invokeinterface 42 1 0
        //   51: astore_1
        //   52: aload 6
        //   54: aload_1
        //   55: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   58: aload 6
        //   60: iload 4
        //   62: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   65: aload_0
        //   66: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   69: bipush 12
        //   71: aload 6
        //   73: aconst_null
        //   74: iconst_1
        //   75: invokeinterface 55 5 0
        //   80: pop
        //   81: aload 6
        //   83: invokevirtual 58	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 6
        //   89: iconst_0
        //   90: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   93: goto -61 -> 32
        //   96: astore_1
        //   97: aload 6
        //   99: invokevirtual 58	android/os/Parcel:recycle	()V
        //   102: aload_1
        //   103: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	104	0	this	Proxy
        //   0	104	1	paramPrintJobId	PrintJobId
        //   0	104	2	paramString	String
        //   0	104	3	paramIPrintSpoolerCallbacks	IPrintSpoolerCallbacks
        //   0	104	4	paramInt	int
        //   1	38	5	localObject	Object
        //   6	92	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	96	finally
        //   19	32	96	finally
        //   32	38	96	finally
        //   45	52	96	finally
        //   52	81	96	finally
        //   87	93	96	finally
      }
      
      /* Error */
      public void setProgress(PrintJobId paramPrintJobId, float paramFloat)
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
        //   16: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 79	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: fload_2
        //   27: invokevirtual 117	android/os/Parcel:writeFloat	(F)V
        //   30: aload_0
        //   31: getfield 19	android/print/IPrintSpooler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 6
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 58	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramPrintJobId	PrintJobId
        //   0	65	2	paramFloat	float
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      public void setStatus(PrintJobId paramPrintJobId, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintSpooler");
            if (paramPrintJobId != null)
            {
              localParcel.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel, 0);
              if (paramCharSequence != null)
              {
                localParcel.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel, 0);
                this.mRemote.transact(7, localParcel, null, 1);
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
        }
      }
      
      public void setStatusRes(PrintJobId paramPrintJobId, int paramInt, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintSpooler");
            if (paramPrintJobId != null)
            {
              localParcel.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt);
              if (paramCharSequence != null)
              {
                localParcel.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel, 0);
                this.mRemote.transact(8, localParcel, null, 1);
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
        }
      }
      
      public void writePrintJobData(ParcelFileDescriptor paramParcelFileDescriptor, PrintJobId paramPrintJobId)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.print.IPrintSpooler");
            if (paramParcelFileDescriptor != null)
            {
              localParcel.writeInt(1);
              paramParcelFileDescriptor.writeToParcel(localParcel, 0);
              if (paramPrintJobId != null)
              {
                localParcel.writeInt(1);
                paramPrintJobId.writeToParcel(localParcel, 0);
                this.mRemote.transact(13, localParcel, null, 1);
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
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintSpooler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */