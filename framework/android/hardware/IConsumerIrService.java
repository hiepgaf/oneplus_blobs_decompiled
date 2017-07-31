package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IConsumerIrService
  extends IInterface
{
  public abstract int[] getCarrierFrequencies()
    throws RemoteException;
  
  public abstract boolean hasIrEmitter()
    throws RemoteException;
  
  public abstract void transmit(String paramString, int paramInt, int[] paramArrayOfInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IConsumerIrService
  {
    private static final String DESCRIPTOR = "android.hardware.IConsumerIrService";
    static final int TRANSACTION_getCarrierFrequencies = 3;
    static final int TRANSACTION_hasIrEmitter = 1;
    static final int TRANSACTION_transmit = 2;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.IConsumerIrService");
    }
    
    public static IConsumerIrService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.IConsumerIrService");
      if ((localIInterface != null) && ((localIInterface instanceof IConsumerIrService))) {
        return (IConsumerIrService)localIInterface;
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
        paramParcel2.writeString("android.hardware.IConsumerIrService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.IConsumerIrService");
        boolean bool = hasIrEmitter();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.hardware.IConsumerIrService");
        transmit(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.IConsumerIrService");
      paramParcel1 = getCarrierFrequencies();
      paramParcel2.writeNoException();
      paramParcel2.writeIntArray(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IConsumerIrService
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
      
      public int[] getCarrierFrequencies()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.IConsumerIrService");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.IConsumerIrService";
      }
      
      /* Error */
      public boolean hasIrEmitter()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/hardware/IConsumerIrService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 44 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 47	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 62	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 53	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 53	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 53	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 53	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public void transmit(String paramString, int paramInt, int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.IConsumerIrService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/IConsumerIrService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */