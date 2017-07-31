package com.fingerprints.extension.sensortest;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IFingerprintSensorTest
  extends IInterface
{
  public abstract void cancelCapture()
    throws RemoteException;
  
  public abstract void cancelSensorTest()
    throws RemoteException;
  
  public abstract void capture(ICaptureCallback paramICaptureCallback, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract SensorInfo getSensorInfo()
    throws RemoteException;
  
  public abstract List<SensorTest> getSensorTests()
    throws RemoteException;
  
  public abstract void runSensorTest(ISensorTestCallback paramISensorTestCallback, SensorTest paramSensorTest, SensorTestInput paramSensorTestInput)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintSensorTest
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.sensortest.IFingerprintSensorTest";
    static final int TRANSACTION_cancelCapture = 6;
    static final int TRANSACTION_cancelSensorTest = 4;
    static final int TRANSACTION_capture = 5;
    static final int TRANSACTION_getSensorInfo = 1;
    static final int TRANSACTION_getSensorTests = 2;
    static final int TRANSACTION_runSensorTest = 3;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.sensortest.IFingerprintSensorTest");
    }
    
    public static IFingerprintSensorTest asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintSensorTest))) {
        return (IFingerprintSensorTest)localIInterface;
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
      boolean bool2 = false;
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        paramParcel1 = getSensorInfo();
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
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        paramParcel1 = getSensorTests();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        ISensorTestCallback localISensorTestCallback = ISensorTestCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject = (SensorTest)SensorTest.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label228;
          }
        }
        for (paramParcel1 = (SensorTestInput)SensorTestInput.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          runSensorTest(localISensorTestCallback, (SensorTest)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        cancelSensorTest();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        label228:
        paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
        localObject = ICaptureCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool1 = true;; bool1 = false)
        {
          if (paramParcel1.readInt() != 0) {
            bool2 = true;
          }
          capture((ICaptureCallback)localObject, bool1, bool2);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
      cancelCapture();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IFingerprintSensorTest
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
      
      public void cancelCapture()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cancelSensorTest()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
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
      
      /* Error */
      public void capture(ICaptureCallback paramICaptureCallback, boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: iconst_1
        //   4: istore 5
        //   6: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 7
        //   11: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   14: astore 8
        //   16: aload 7
        //   18: ldc 33
        //   20: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   23: aload_1
        //   24: ifnull +11 -> 35
        //   27: aload_1
        //   28: invokeinterface 57 1 0
        //   33: astore 6
        //   35: aload 7
        //   37: aload 6
        //   39: invokevirtual 60	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   42: iload_2
        //   43: ifeq +60 -> 103
        //   46: iconst_1
        //   47: istore 4
        //   49: aload 7
        //   51: iload 4
        //   53: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   56: iload_3
        //   57: ifeq +52 -> 109
        //   60: iload 5
        //   62: istore 4
        //   64: aload 7
        //   66: iload 4
        //   68: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   71: aload_0
        //   72: getfield 19	com/fingerprints/extension/sensortest/IFingerprintSensorTest$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   75: iconst_5
        //   76: aload 7
        //   78: aload 8
        //   80: iconst_0
        //   81: invokeinterface 43 5 0
        //   86: pop
        //   87: aload 8
        //   89: invokevirtual 46	android/os/Parcel:readException	()V
        //   92: aload 8
        //   94: invokevirtual 49	android/os/Parcel:recycle	()V
        //   97: aload 7
        //   99: invokevirtual 49	android/os/Parcel:recycle	()V
        //   102: return
        //   103: iconst_0
        //   104: istore 4
        //   106: goto -57 -> 49
        //   109: iconst_0
        //   110: istore 4
        //   112: goto -48 -> 64
        //   115: astore_1
        //   116: aload 8
        //   118: invokevirtual 49	android/os/Parcel:recycle	()V
        //   121: aload 7
        //   123: invokevirtual 49	android/os/Parcel:recycle	()V
        //   126: aload_1
        //   127: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	128	0	this	Proxy
        //   0	128	1	paramICaptureCallback	ICaptureCallback
        //   0	128	2	paramBoolean1	boolean
        //   0	128	3	paramBoolean2	boolean
        //   47	64	4	i	int
        //   4	57	5	j	int
        //   1	37	6	localIBinder	IBinder
        //   9	113	7	localParcel1	Parcel
        //   14	103	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   16	23	115	finally
        //   27	35	115	finally
        //   35	42	115	finally
        //   49	56	115	finally
        //   64	92	115	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.fingerprints.extension.sensortest.IFingerprintSensorTest";
      }
      
      /* Error */
      public SensorInfo getSensorInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	com/fingerprints/extension/sensortest/IFingerprintSensorTest$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_1
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 43 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 46	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 72	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 78	com/fingerprints/extension/sensortest/SensorInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 84 2 0
        //   48: checkcast 74	com/fingerprints/extension/sensortest/SensorInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 49	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 49	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localSensorInfo	SensorInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public List<SensorTest> getSensorTests()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(SensorTest.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void runSensorTest(ISensorTestCallback paramISensorTestCallback, SensorTest paramSensorTest, SensorTestInput paramSensorTestInput)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.fingerprints.extension.sensortest.IFingerprintSensorTest");
            if (paramISensorTestCallback != null) {
              localIBinder = paramISensorTestCallback.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramSensorTest != null)
            {
              localParcel1.writeInt(1);
              paramSensorTest.writeToParcel(localParcel1, 0);
              if (paramSensorTestInput != null)
              {
                localParcel1.writeInt(1);
                paramSensorTestInput.writeToParcel(localParcel1, 0);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/IFingerprintSensorTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */