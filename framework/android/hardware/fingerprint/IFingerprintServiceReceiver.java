package android.hardware.fingerprint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IFingerprintServiceReceiver
  extends IInterface
{
  public abstract void onAcquired(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onAuthenticationFailed(long paramLong)
    throws RemoteException;
  
  public abstract void onAuthenticationSucceeded(long paramLong, Fingerprint paramFingerprint, int paramInt)
    throws RemoteException;
  
  public abstract void onEnrollResult(long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void onError(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onRemoved(long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintServiceReceiver
  {
    private static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintServiceReceiver";
    static final int TRANSACTION_onAcquired = 2;
    static final int TRANSACTION_onAuthenticationFailed = 4;
    static final int TRANSACTION_onAuthenticationSucceeded = 3;
    static final int TRANSACTION_onEnrollResult = 1;
    static final int TRANSACTION_onError = 5;
    static final int TRANSACTION_onRemoved = 6;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.fingerprint.IFingerprintServiceReceiver");
    }
    
    public static IFingerprintServiceReceiver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintServiceReceiver))) {
        return (IFingerprintServiceReceiver)localIInterface;
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
        paramParcel2.writeString("android.hardware.fingerprint.IFingerprintServiceReceiver");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
        onEnrollResult(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
        onAcquired(paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
        long l = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Fingerprint)Fingerprint.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onAuthenticationSucceeded(l, paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
        onAuthenticationFailed(paramParcel1.readLong());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
        onError(paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintServiceReceiver");
      onRemoved(paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IFingerprintServiceReceiver
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
        return "android.hardware.fingerprint.IFingerprintServiceReceiver";
      }
      
      public void onAcquired(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceReceiver");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onAuthenticationFailed(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceReceiver");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onAuthenticationSucceeded(long paramLong, Fingerprint paramFingerprint, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 5
        //   14: lload_1
        //   15: invokevirtual 44	android/os/Parcel:writeLong	(J)V
        //   18: aload_3
        //   19: ifnull +44 -> 63
        //   22: aload 5
        //   24: iconst_1
        //   25: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   28: aload_3
        //   29: aload 5
        //   31: iconst_0
        //   32: invokevirtual 67	android/hardware/fingerprint/Fingerprint:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload 5
        //   37: iload 4
        //   39: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   42: aload_0
        //   43: getfield 19	android/hardware/fingerprint/IFingerprintServiceReceiver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   46: iconst_3
        //   47: aload 5
        //   49: aconst_null
        //   50: iconst_1
        //   51: invokeinterface 54 5 0
        //   56: pop
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: return
        //   63: aload 5
        //   65: iconst_0
        //   66: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   69: goto -34 -> 35
        //   72: astore_3
        //   73: aload 5
        //   75: invokevirtual 57	android/os/Parcel:recycle	()V
        //   78: aload_3
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   0	80	1	paramLong	long
        //   0	80	3	paramFingerprint	Fingerprint
        //   0	80	4	paramInt	int
        //   3	71	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	18	72	finally
        //   22	35	72	finally
        //   35	57	72	finally
        //   63	69	72	finally
      }
      
      public void onEnrollResult(long paramLong, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceReceiver");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onError(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceReceiver");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onRemoved(long paramLong, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.fingerprint.IFingerprintServiceReceiver");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/IFingerprintServiceReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */