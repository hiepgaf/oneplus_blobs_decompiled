package android.print;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPrintSpoolerClient
  extends IInterface
{
  public abstract void onAllPrintJobsForServiceHandled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void onAllPrintJobsHandled()
    throws RemoteException;
  
  public abstract void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
    throws RemoteException;
  
  public abstract void onPrintJobStateChanged(PrintJobInfo paramPrintJobInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintSpoolerClient
  {
    private static final String DESCRIPTOR = "android.print.IPrintSpoolerClient";
    static final int TRANSACTION_onAllPrintJobsForServiceHandled = 2;
    static final int TRANSACTION_onAllPrintJobsHandled = 3;
    static final int TRANSACTION_onPrintJobQueued = 1;
    static final int TRANSACTION_onPrintJobStateChanged = 4;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintSpoolerClient");
    }
    
    public static IPrintSpoolerClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintSpoolerClient");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintSpoolerClient))) {
        return (IPrintSpoolerClient)localIInterface;
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
        paramParcel2.writeString("android.print.IPrintSpoolerClient");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrintJobQueued(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onAllPrintJobsForServiceHandled(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.print.IPrintSpoolerClient");
        onAllPrintJobsHandled();
        return true;
      }
      paramParcel1.enforceInterface("android.print.IPrintSpoolerClient");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (PrintJobInfo)PrintJobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onPrintJobStateChanged(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IPrintSpoolerClient
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
        return "android.print.IPrintSpoolerClient";
      }
      
      /* Error */
      public void onAllPrintJobsForServiceHandled(ComponentName paramComponentName)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/print/IPrintSpoolerClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramComponentName	ComponentName
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onAllPrintJobsHandled()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.print.IPrintSpoolerClient");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 66	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/print/IPrintSpoolerClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
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
      public void onPrintJobStateChanged(PrintJobInfo paramPrintJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 66	android/print/PrintJobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/print/IPrintSpoolerClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintSpoolerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */