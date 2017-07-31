package android.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPrintJobStateChangeListener
  extends IInterface
{
  public abstract void onPrintJobStateChanged(PrintJobId paramPrintJobId)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPrintJobStateChangeListener
  {
    private static final String DESCRIPTOR = "android.print.IPrintJobStateChangeListener";
    static final int TRANSACTION_onPrintJobStateChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.print.IPrintJobStateChangeListener");
    }
    
    public static IPrintJobStateChangeListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.print.IPrintJobStateChangeListener");
      if ((localIInterface != null) && ((localIInterface instanceof IPrintJobStateChangeListener))) {
        return (IPrintJobStateChangeListener)localIInterface;
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
        paramParcel2.writeString("android.print.IPrintJobStateChangeListener");
        return true;
      }
      paramParcel1.enforceInterface("android.print.IPrintJobStateChangeListener");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (PrintJobId)PrintJobId.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onPrintJobStateChanged(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IPrintJobStateChangeListener
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
        return "android.print.IPrintJobStateChangeListener";
      }
      
      /* Error */
      public void onPrintJobStateChanged(PrintJobId paramPrintJobId)
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
        //   22: invokevirtual 50	android/print/PrintJobId:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/print/IPrintJobStateChangeListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
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
        //   0	59	1	paramPrintJobId	PrintJobId
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/IPrintJobStateChangeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */