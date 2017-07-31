package android.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface INetInitiatedListener
  extends IInterface
{
  public abstract boolean sendNiResponse(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetInitiatedListener
  {
    private static final String DESCRIPTOR = "android.location.INetInitiatedListener";
    static final int TRANSACTION_sendNiResponse = 1;
    
    public Stub()
    {
      attachInterface(this, "android.location.INetInitiatedListener");
    }
    
    public static INetInitiatedListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.INetInitiatedListener");
      if ((localIInterface != null) && ((localIInterface instanceof INetInitiatedListener))) {
        return (INetInitiatedListener)localIInterface;
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
        paramParcel2.writeString("android.location.INetInitiatedListener");
        return true;
      }
      paramParcel1.enforceInterface("android.location.INetInitiatedListener");
      boolean bool = sendNiResponse(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements INetInitiatedListener
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
        return "android.location.INetInitiatedListener";
      }
      
      /* Error */
      public boolean sendNiResponse(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/location/INetInitiatedListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 50 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 53	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 57	android/os/Parcel:readInt	()I
        //   55: istore_1
        //   56: iload_1
        //   57: ifeq +17 -> 74
        //   60: iconst_1
        //   61: istore_3
        //   62: aload 5
        //   64: invokevirtual 60	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 60	android/os/Parcel:recycle	()V
        //   72: iload_3
        //   73: ireturn
        //   74: iconst_0
        //   75: istore_3
        //   76: goto -14 -> 62
        //   79: astore 6
        //   81: aload 5
        //   83: invokevirtual 60	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 60	android/os/Parcel:recycle	()V
        //   91: aload 6
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramInt1	int
        //   0	94	2	paramInt2	int
        //   61	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        //   79	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	56	79	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/INetInitiatedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */