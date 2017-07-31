package android.printservice;

import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public abstract interface IPrintServiceClient
  extends IInterface
{
  public abstract PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId)
    throws RemoteException;
  
  public abstract List<PrintJobInfo> getPrintJobInfos()
    throws RemoteException;
  
  public abstract void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon)
    throws RemoteException;
  
  public abstract void onPrintersAdded(ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract void onPrintersRemoved(ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract boolean setPrintJobState(PrintJobId paramPrintJobId, int paramInt, String paramString)
    throws RemoteException;
  
  public abstract boolean setPrintJobTag(PrintJobId paramPrintJobId, String paramString)
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
    implements IPrintServiceClient
  {
    private static final String DESCRIPTOR = "android.printservice.IPrintServiceClient";
    static final int TRANSACTION_getPrintJobInfo = 2;
    static final int TRANSACTION_getPrintJobInfos = 1;
    static final int TRANSACTION_onCustomPrinterIconLoaded = 11;
    static final int TRANSACTION_onPrintersAdded = 9;
    static final int TRANSACTION_onPrintersRemoved = 10;
    static final int TRANSACTION_setPrintJobState = 3;
    static final int TRANSACTION_setPrintJobTag = 4;
    static final int TRANSACTION_setProgress = 6;
    static final int TRANSACTION_setStatus = 7;
    static final int TRANSACTION_setStatusRes = 8;
    static final int TRANSACTION_writePrintJobData = 5;
    
    public Stub()
    {
      attachInterface(this, "android.printservice.IPrintServiceClient");
    }
    
    public static IPrintServiceClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.printservice.IPrintServiceClient");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintServiceClient))) {
        return (IPrintServiceClient)localIInterface;
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
      label206:
      Object localObject;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.printservice.IPrintServiceClient");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        paramParcel1 = getPrintJobInfos();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getPrintJobInfo(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label206;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          bool = setPrintJobState((PrintJobId)localObject, paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label281;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          bool = setPrintJobTag((PrintJobId)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label349;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label413;
          }
        }
        for (paramParcel1 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          writePrintJobData(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setProgress((PrintJobId)localObject, paramParcel1.readFloat());
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label533;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setStatus((PrintJobId)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label610;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setStatusRes((PrintJobId)localObject, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrintersAdded(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        label281:
        label349:
        label413:
        label533:
        label610:
        paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrintersRemoved(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.printservice.IPrintServiceClient");
      if (paramParcel1.readInt() != 0)
      {
        localObject = (PrinterId)PrinterId.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label765;
        }
      }
      label765:
      for (paramParcel1 = (Icon)Icon.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onCustomPrinterIconLoaded((PrinterId)localObject, paramParcel1);
        paramParcel2.writeNoException();
        return true;
        localObject = null;
        break;
      }
    }
    
    private static class Proxy
      implements IPrintServiceClient
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
        return "android.printservice.IPrintServiceClient";
      }
      
      public PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
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
      
      public List<PrintJobInfo> getPrintJobInfos()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
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
      
      public void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrinterId != null)
            {
              localParcel1.writeInt(1);
              paramPrinterId.writeToParcel(localParcel1, 0);
              if (paramIcon != null)
              {
                localParcel1.writeInt(1);
                paramIcon.writeToParcel(localParcel1, 0);
                this.mRemote.transact(11, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void onPrintersAdded(ParceledListSlice paramParceledListSlice)
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
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 100	android/content/pm/ParceledListSlice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/printservice/IPrintServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 9
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 78	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 78	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 78	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 78	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramParceledListSlice	ParceledListSlice
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void onPrintersRemoved(ParceledListSlice paramParceledListSlice)
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
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 100	android/content/pm/ParceledListSlice:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/printservice/IPrintServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 10
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 78	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 78	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 78	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 78	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramParceledListSlice	ParceledListSlice
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public boolean setPrintJobState(PrintJobId paramPrintJobId, int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              localParcel1.writeString(paramString);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean setPrintJobTag(PrintJobId paramPrintJobId, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void setProgress(PrintJobId paramPrintJobId, float paramFloat)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: fload_2
        //   32: invokevirtual 114	android/os/Parcel:writeFloat	(F)V
        //   35: aload_0
        //   36: getfield 19	android/printservice/IPrintServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 6
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 56 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 59	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 78	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 78	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 78	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 78	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramPrintJobId	PrintJobId
        //   0	86	2	paramFloat	float
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void setStatus(PrintJobId paramPrintJobId, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(7, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setStatusRes(PrintJobId paramPrintJobId, int paramInt, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramPrintJobId != null)
            {
              localParcel1.writeInt(1);
              paramPrintJobId.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(8, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
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
            localParcel.writeInterfaceToken("android.printservice.IPrintServiceClient");
            if (paramParcelFileDescriptor != null)
            {
              localParcel.writeInt(1);
              paramParcelFileDescriptor.writeToParcel(localParcel, 0);
              if (paramPrintJobId != null)
              {
                localParcel.writeInt(1);
                paramPrintJobId.writeToParcel(localParcel, 0);
                this.mRemote.transact(5, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/IPrintServiceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */