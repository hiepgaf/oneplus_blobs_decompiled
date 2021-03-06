package com.fingerprints.extension.engineering;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFingerprintEngineering
  extends IInterface
{
  public abstract SensorSize getSensorSize()
    throws RemoteException;
  
  public abstract void startImageInjection(IImageInjectionCallback paramIImageInjectionCallback)
    throws RemoteException;
  
  public abstract void startImageSubscription(IImageSubscriptionCallback paramIImageSubscriptionCallback)
    throws RemoteException;
  
  public abstract void stopImageInjection()
    throws RemoteException;
  
  public abstract void stopImageSubscription()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintEngineering
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.engineering.IFingerprintEngineering";
    static final int TRANSACTION_getSensorSize = 1;
    static final int TRANSACTION_startImageInjection = 4;
    static final int TRANSACTION_startImageSubscription = 2;
    static final int TRANSACTION_stopImageInjection = 5;
    static final int TRANSACTION_stopImageSubscription = 3;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.engineering.IFingerprintEngineering");
    }
    
    public static IFingerprintEngineering asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintEngineering))) {
        return (IFingerprintEngineering)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.engineering.IFingerprintEngineering");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
        paramParcel1 = getSensorSize();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
        startImageSubscription(IImageSubscriptionCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
        stopImageSubscription();
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
        startImageInjection(IImageInjectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.engineering.IFingerprintEngineering");
      stopImageInjection();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFingerprintEngineering
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
        return "com.fingerprints.extension.engineering.IFingerprintEngineering";
      }
      
      /* Error */
      public SensorSize getSensorSize()
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
        //   14: aload_0
        //   15: getfield 19	com/fingerprints/extension/engineering/IFingerprintEngineering$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_1
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 46 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 49	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 53	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 59	com/fingerprints/extension/engineering/SensorSize:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 65 2 0
        //   48: checkcast 55	com/fingerprints/extension/engineering/SensorSize
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 68	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localSensorSize	SensorSize
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public void startImageInjection(IImageInjectionCallback paramIImageInjectionCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IFingerprintEngineering");
          if (paramIImageInjectionCallback != null) {
            localIBinder = paramIImageInjectionCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startImageSubscription(IImageSubscriptionCallback paramIImageSubscriptionCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IFingerprintEngineering");
          if (paramIImageSubscriptionCallback != null) {
            localIBinder = paramIImageSubscriptionCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void stopImageInjection()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IFingerprintEngineering");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopImageSubscription()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.engineering.IFingerprintEngineering");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/engineering/IFingerprintEngineering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */