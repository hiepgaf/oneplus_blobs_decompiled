package android.hardware.location;

import android.location.FusedBatchOptions;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IFusedLocationHardware
  extends IInterface
{
  public abstract void flushBatchedLocations()
    throws RemoteException;
  
  public abstract int getSupportedBatchSize()
    throws RemoteException;
  
  public abstract int getVersion()
    throws RemoteException;
  
  public abstract void injectDeviceContext(int paramInt)
    throws RemoteException;
  
  public abstract void injectDiagnosticData(String paramString)
    throws RemoteException;
  
  public abstract void registerSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
    throws RemoteException;
  
  public abstract void requestBatchOfLocations(int paramInt)
    throws RemoteException;
  
  public abstract void startBatching(int paramInt, FusedBatchOptions paramFusedBatchOptions)
    throws RemoteException;
  
  public abstract void stopBatching(int paramInt)
    throws RemoteException;
  
  public abstract boolean supportsDeviceContextInjection()
    throws RemoteException;
  
  public abstract boolean supportsDiagnosticDataInjection()
    throws RemoteException;
  
  public abstract void unregisterSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
    throws RemoteException;
  
  public abstract void updateBatchingOptions(int paramInt, FusedBatchOptions paramFusedBatchOptions)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFusedLocationHardware
  {
    private static final String DESCRIPTOR = "android.hardware.location.IFusedLocationHardware";
    static final int TRANSACTION_flushBatchedLocations = 12;
    static final int TRANSACTION_getSupportedBatchSize = 3;
    static final int TRANSACTION_getVersion = 13;
    static final int TRANSACTION_injectDeviceContext = 11;
    static final int TRANSACTION_injectDiagnosticData = 9;
    static final int TRANSACTION_registerSink = 1;
    static final int TRANSACTION_requestBatchOfLocations = 7;
    static final int TRANSACTION_startBatching = 4;
    static final int TRANSACTION_stopBatching = 5;
    static final int TRANSACTION_supportsDeviceContextInjection = 10;
    static final int TRANSACTION_supportsDiagnosticDataInjection = 8;
    static final int TRANSACTION_unregisterSink = 2;
    static final int TRANSACTION_updateBatchingOptions = 6;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IFusedLocationHardware");
    }
    
    public static IFusedLocationHardware asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IFusedLocationHardware");
      if ((localIInterface != null) && ((localIInterface instanceof IFusedLocationHardware))) {
        return (IFusedLocationHardware)localIInterface;
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
      int j = 0;
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.location.IFusedLocationHardware");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        registerSink(IFusedLocationHardwareSink.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        unregisterSink(IFusedLocationHardwareSink.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        paramInt1 = getSupportedBatchSize();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (FusedBatchOptions)FusedBatchOptions.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startBatching(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        stopBatching(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (FusedBatchOptions)FusedBatchOptions.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateBatchingOptions(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        requestBatchOfLocations(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        bool = supportsDiagnosticDataInjection();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        injectDiagnosticData(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        bool = supportsDeviceContextInjection();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        injectDeviceContext(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
        flushBatchedLocations();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.location.IFusedLocationHardware");
      paramInt1 = getVersion();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IFusedLocationHardware
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
      
      public void flushBatchedLocations()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.location.IFusedLocationHardware";
      }
      
      public int getSupportedBatchSize()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getVersion()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void injectDeviceContext(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void injectDiagnosticData(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          localParcel1.writeString(paramString);
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
      
      public void registerSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          if (paramIFusedLocationHardwareSink != null) {
            localIBinder = paramIFusedLocationHardwareSink.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestBatchOfLocations(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
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
      
      /* Error */
      public void startBatching(int paramInt, FusedBatchOptions paramFusedBatchOptions)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +44 -> 65
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 85	android/location/FusedBatchOptions:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IFusedLocationHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_4
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 43 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 46	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 49	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 49	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   70: goto -35 -> 35
        //   73: astore_2
        //   74: aload 4
        //   76: invokevirtual 49	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 49	android/os/Parcel:recycle	()V
        //   83: aload_2
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramInt	int
        //   0	85	2	paramFusedBatchOptions	FusedBatchOptions
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	73	finally
        //   24	35	73	finally
        //   35	55	73	finally
        //   65	70	73	finally
      }
      
      public void stopBatching(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public boolean supportsDeviceContextInjection()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/hardware/location/IFusedLocationHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 10
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 43 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 46	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 57	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 49	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 49	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 49	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean supportsDiagnosticDataInjection()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/hardware/location/IFusedLocationHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 43 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 46	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 57	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 49	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 49	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 49	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void unregisterSink(IFusedLocationHardwareSink paramIFusedLocationHardwareSink)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IFusedLocationHardware");
          if (paramIFusedLocationHardwareSink != null) {
            localIBinder = paramIFusedLocationHardwareSink.asBinder();
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
      
      /* Error */
      public void updateBatchingOptions(int paramInt, FusedBatchOptions paramFusedBatchOptions)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 85	android/location/FusedBatchOptions:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IFusedLocationHardware$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 6
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 43 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 46	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 49	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 49	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramFusedBatchOptions	FusedBatchOptions
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IFusedLocationHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */