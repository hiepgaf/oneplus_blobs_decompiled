package android.service.trust;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.os.RemoteException;
import java.util.List;

public abstract interface ITrustAgentService
  extends IInterface
{
  public abstract void onConfigure(List<PersistableBundle> paramList, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onDeviceLocked()
    throws RemoteException;
  
  public abstract void onDeviceUnlocked()
    throws RemoteException;
  
  public abstract void onTrustTimeout()
    throws RemoteException;
  
  public abstract void onUnlockAttempt(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCallback(ITrustAgentServiceCallback paramITrustAgentServiceCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITrustAgentService
  {
    private static final String DESCRIPTOR = "android.service.trust.ITrustAgentService";
    static final int TRANSACTION_onConfigure = 5;
    static final int TRANSACTION_onDeviceLocked = 3;
    static final int TRANSACTION_onDeviceUnlocked = 4;
    static final int TRANSACTION_onTrustTimeout = 2;
    static final int TRANSACTION_onUnlockAttempt = 1;
    static final int TRANSACTION_setCallback = 6;
    
    public Stub()
    {
      attachInterface(this, "android.service.trust.ITrustAgentService");
    }
    
    public static ITrustAgentService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.trust.ITrustAgentService");
      if ((localIInterface != null) && ((localIInterface instanceof ITrustAgentService))) {
        return (ITrustAgentService)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.trust.ITrustAgentService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        onUnlockAttempt(bool);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
        onTrustTimeout();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
        onDeviceLocked();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
        onDeviceUnlocked();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
        onConfigure(paramParcel1.createTypedArrayList(PersistableBundle.CREATOR), paramParcel1.readStrongBinder());
        return true;
      }
      paramParcel1.enforceInterface("android.service.trust.ITrustAgentService");
      setCallback(ITrustAgentServiceCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements ITrustAgentService
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
        return "android.service.trust.ITrustAgentService";
      }
      
      public void onConfigure(List<PersistableBundle> paramList, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentService");
          localParcel.writeTypedList(paramList);
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDeviceLocked()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentService");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDeviceUnlocked()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentService");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTrustTimeout()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentService");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onUnlockAttempt(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +27 -> 40
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/service/trust/ITrustAgentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_1
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 53 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 56	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 56	android/os/Parcel:recycle	()V
        //   51: aload 4
        //   53: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	54	0	this	Proxy
        //   0	54	1	paramBoolean	boolean
        //   1	41	2	i	int
        //   5	43	3	localParcel	Parcel
        //   45	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	45	finally
        //   16	35	45	finally
      }
      
      public void setCallback(ITrustAgentServiceCallback paramITrustAgentServiceCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentService");
          if (paramITrustAgentServiceCallback != null) {
            localIBinder = paramITrustAgentServiceCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(6, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/trust/ITrustAgentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */